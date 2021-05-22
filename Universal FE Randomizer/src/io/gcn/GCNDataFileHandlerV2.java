package io.gcn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.FileHandler;
import util.ByteArrayBuilder;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

/* This is V2 of the data file handler for binary data for GCN.
 * 
 * This is more aware of the full structure of the file. The data files can store any binary data
 * while sharing the same pool of strings that they can point to.
 * 
 * Anatomy of a data file
 * 
 * 0x0 ~ 0x20: Header of the data file
 * -- 0x0 ~ 0x3: Length of the entire file
 * -- 0x4 ~ 0x7: The offset to the table of string pointers.
 * -- 0x8 ~ 0xB: The number of pointers that exist in the table.
 * -- 0xC ~ 0xF: The number of data sections that exist.
 * -- 0x10 ~ 0x1F: 00s as far as I can tell.
 * 
 * Immediately following the header is the actual binary data for all sections. The start offsets will be found later.
 * 
 * Following the binary data is the list of strings that can be referenced by the data. The strings aren't part of
 * any data section and is of arbitrary length, without any necessary byte alignment, just separated by 00 bytes.
 * String data should go all the way up to the offset found in the file header (0x4 ~ 0x7).
 * 
 * Following the strings is an array of pointers that match with the header in terms of start position
 * and count. These are offsets in the binary data that are supposed to point to the strings.
 * Every valid pointer to a string in the data should have an entry here. The offsets here are relative to the start of the
 * binary data and not the beginning of the file. Therefore, an offset here of 0x0 is actually 0x20 in the file.
 * 
 * Immediately following this table of pointers is a collection of 8-byte long entries that define the sections.
 * Each entry is as follows:
 * 
 * 0x0 ~ 0x3: Offset from the start of binary data where this section's data begins. Data goes up to the beginning of the next section.
 * 				Remember, relative to the start of binary data means that 0x0 offset is at 0x20 file offset.
 * 0x4 ~ 0x7: Offset from the start of the section name strings. This section actually comes after the 8-byte entries we're in now.
 * 				They point to the name of the data section.
 * 
 * Note: We don't yet support expanding the data area yet. To do so, we'd need to be able to mass update all of the
 *			string pointers in the data, since they would push back the strings.
 * 
 */

public class GCNDataFileHandlerV2 extends GCNByteArrayHandler {
	
	public static class GCNDataFileDataSection {
		public final String identifier;
		private byte[] rawData;
		
		public final long originalOffset;
		private List<Long> validPointers; // Relative to start of your own data, not the rest of the file.
		
		private GCNDataFileDataSection(String identifier, byte[] rawData, long originalOffset, List<Long> pointers) {
			this.identifier = identifier;
			this.rawData = rawData;
			this.originalOffset = originalOffset;
			
			validPointers = new ArrayList<Long>();
			for (Long pointer : pointers) {
				long effectivePointer = pointer - (originalOffset - 0x20);
				if (effectivePointer < 0) { continue; }
				if (effectivePointer >= rawData.length) { continue; }
				validPointers.add(effectivePointer);
			}
		}
		
		public long getLength() {
			return rawData.length;
		}
		
		public byte[] getRawData(long offset, int length) {
			return WhyDoesJavaNotHaveThese.subArray(rawData, (int)offset, length);
		}
		
		private boolean addPointerOffset(long offset) {
			if (validPointers.contains(offset)) { return false; }
			if (offset < 0 || offset + 4 > rawData.length) { return false; }
			validPointers.add(offset);
			return true;
		}
		
		private boolean writePointerToOffset(long offset, long pointer) {
			if (validPointers.contains(offset)) {
				WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(pointer, false, 4), rawData, (int)offset, 4);
				return true;
			} else {
				return false;
			}
		}
		
		private boolean overwriteData(long offset, byte[] data) {
			if (data == null) { return false; }
			if (offset + data.length > rawData.length) { return false; }
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(data, rawData, (int)offset, data.length);
			return true;
		}
		
		public Long getPointerAtOffset(long offset) {
			if (validPointers.contains(offset)) {
				return WhyDoesJavaNotHaveThese.longValueFromByteArray(WhyDoesJavaNotHaveThese.subArray(rawData, (int)offset, 4), false);
			}
			
			return null;
		}
	}
	
	private List<GCNDataFileDataSection> dataSections;
	private List<String> strings;
	
	private List<Long> pointers; // Pointers here are with respect to the beginning of the data, not the file. Add 0x20 for the file address.
	
	private Map<Long, String> stringsByPointers;
	
	private Map<String, Long> pointersByStrings;
	
	private ByteArrayBuilder addedStringData;
	
	private long nextStringInsertionFileOffset;
	
	private List<String> orderedSectionNames;
	
	private boolean needsRebuild = false;
	private boolean hasChanges = false;
	
	public GCNDataFileHandlerV2(GCNFSTFileEntry file, FileHandler handler, byte[] byteArray) {
		super(file, handler, byteArray);
		
		dataSections = new ArrayList<GCNDataFileDataSection>();
		strings = new ArrayList<String>();
		pointers = new ArrayList<Long>();
		
		stringsByPointers = new HashMap<Long, String>();
		pointersByStrings = new HashMap<String, Long>();
		
		orderedSectionNames = new ArrayList<String>();
		
		addedStringData = new ByteArrayBuilder();
		
		long pointerTableOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(0x4, 4), false);
		long numberOfPointers = WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(0x8, 4), false);
		
		long sectionTableOffset = pointerTableOffset + (4 * numberOfPointers) + 0x20;
		long numberOfSections = WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(0xC, 4), false);
		long sectionNameTableOffset = sectionTableOffset + numberOfSections * 8;
		
		Map<Long, String> namesByDataPointers = new HashMap<Long, String>();
		List<Long> orderedDataPointers = new ArrayList<Long>();
		
		for (long i = 0; i < numberOfSections; i++) {
			long pointerToData = WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(sectionTableOffset + i * 8, 4), false) + 0x20;
			long pointerToName = WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(sectionTableOffset + i * 8 + 4, 4), false);
			
			setNextReadOffset(pointerToName + sectionNameTableOffset);
			String name = WhyDoesJavaNotHaveThese.stringFromShiftJIS(continueReadingBytesUpToNextTerminator(getNextReadOffset() + 0xFFFFL));
			
			namesByDataPointers.put(pointerToData, name);
			orderedDataPointers.add(pointerToData);
			
			orderedSectionNames.add(name);
		}
		
		orderedDataPointers.sort(new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				return Long.compare(o1, o2);
			}
		});
		
		long firstStringOffset = -1;
		long lastStringOffset = -1;
		
		List<Long> validPointers = new ArrayList<Long>();
		
		for (long i = 0; i < numberOfPointers; i++) {
			long pointerToPointer = WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(pointerTableOffset + i * 4 + 0x20, 4), false);
			validPointers.add(pointerToPointer);
			long pointer = WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(pointerToPointer + 0x20, 4), false);
			setNextReadOffset(pointer + 0x20);
			String dereferenced = WhyDoesJavaNotHaveThese.stringFromShiftJIS(continueReadingBytesUpToNextTerminator(getNextReadOffset() + 0xFFFFL));
			strings.add(dereferenced);
			if (!orderedDataPointers.isEmpty() && pointer + 0x20 < orderedDataPointers.get(orderedDataPointers.size() - 1)) { continue; } // This isn't a string.
			if (firstStringOffset == -1 || (firstStringOffset > pointer + 0x20)) {
				firstStringOffset = pointer + 0x20;
			}
			if (lastStringOffset == -1 || lastStringOffset < pointer + 0x20) {
				lastStringOffset = pointer + 0x20;
			}
			
			if (!pointers.contains(pointer)) {
				pointers.add(pointer);
			}
			
			stringsByPointers.put(pointer, dereferenced);
			pointersByStrings.put(dereferenced, pointer);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Registered string \"" + dereferenced + "\" for pointer 0x" + Long.toHexString(pointer));
		}
		
		setNextReadOffset(lastStringOffset);
		continueReadingBytesUpToNextTerminator(getNextReadOffset() + 0xFFFFL);
		nextStringInsertionFileOffset = getNextReadOffset() + 1 - 0x20;
		
		for (int i = 0; i < orderedDataPointers.size(); i++) {
			long pointerToData = orderedDataPointers.get(i);
			String name = namesByDataPointers.get(pointerToData);
			long endOffset = firstStringOffset;
			if (i + 1 < numberOfSections) {
				endOffset = orderedDataPointers.get(i + 1);
			}
			
			GCNDataFileDataSection section = new GCNDataFileDataSection(name, WhyDoesJavaNotHaveThese.subArray(byteArray, (int)pointerToData, (int)endOffset - (int)pointerToData), pointerToData, validPointers);
			dataSections.add(section);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Processed section \"" + name + "\" starting at file offset 0x" + Long.toHexString(pointerToData) + " up to file offset 0x" + Long.toHexString(endOffset));
		}
	}
	
	public GCNDataFileDataSection getSectionWithName(String name) {
		return dataSections.stream().filter(section -> section.identifier.equals(name)).findFirst().orElse(null);
	}
	
	public List<String> getSectionNames() {
		return dataSections.stream().map(section -> section.identifier).collect(Collectors.toList());
	}
	
	public List<GCNDataFileDataSection> getSections() {
		return dataSections;
	}
	
	public void addPointerOffset(GCNDataFileDataSection section, long offset) {
		if (section == null) { return; }
		needsRebuild = section.addPointerOffset(offset) || needsRebuild;
	}
	
	public boolean writeStringToOffset(GCNDataFileDataSection section, long offset, String string, boolean addIfNeeded) {
		if (section == null) { return false; }
		
		Long pointerOfString = pointerForString(string);
		if (pointerOfString == null) { 
			if (!addIfNeeded) { return false; }
			addString(string);
			pointerOfString = pointerForString(string);
		}
		
		if (section.addPointerOffset(offset) == false) { return false; }
		if (pointerOfString == null) { return false; }
		
		boolean success = section.writePointerToOffset(offset, pointerOfString);
		if (success) { needsRebuild = true; }
		return success;
	}
	
	public boolean writeDataToSection(GCNDataFileDataSection section, long offset, byte[] dataToWrite) {
		if (dataToWrite == null || section == null) { return false; }
		if (offset + dataToWrite.length > section.getLength()) { return false; }
		boolean dataWritten = section.overwriteData(offset, dataToWrite);
		if (dataWritten) { needsRebuild = true; }
		return dataWritten;
	}
	
	public void addString(String string) {
		if (string == null || strings.contains(string)) { return; }
		needsRebuild = true;
		strings.add(string);
		
		byte[] newStringData = WhyDoesJavaNotHaveThese.shiftJISBytesFromString(string);
		addedStringData.appendBytes(newStringData);
		addedStringData.appendByte((byte)0);
		
		pointers.add(nextStringInsertionFileOffset);
		
		stringsByPointers.put(nextStringInsertionFileOffset, string);
		pointersByStrings.put(string, nextStringInsertionFileOffset);
		
		nextStringInsertionFileOffset += newStringData.length + 1;
	}
	
	public List<String> allStrings() {
		return strings.stream().sorted(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		}).collect(Collectors.toList());
	}
	
	public Long pointerForString(String string) {
		return pointersByStrings.get(string);
	}
	
	public String stringForPointer(long pointer) {
		return stringsByPointers.get(pointer);
	}
	
	public void commitAdditions() {
		if (needsRebuild) {
			hasChanges = true;
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Building data file...");
			
			ByteArrayBuilder builder = new ByteArrayBuilder();
			// Build the header first.
			// We need to leave 00s first before we figure out the final size of the data.
			builder.appendBytes(new byte[] {0, 0, 0, 0}); // File Size
			builder.appendBytes(new byte[] {0, 0, 0, 0}); // Pointer table offset
			
			// Calculate the number of pointers.
			List<Long> validPointers = new ArrayList<Long>();
			for (GCNDataFileDataSection section : dataSections) {
				validPointers.addAll(section.validPointers.stream().map(pointer -> pointer + section.originalOffset).collect(Collectors.toList()));
			}
			
			builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(validPointers.size(), false, 4)); // Number of pointers
			builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(dataSections.size(), false, 4)); // Number of sections
			
			// 16 bytes of zeros follow.
			builder.appendBytes(new byte[] {0, 0, 0, 0, 
					0, 0, 0, 0, 
					0, 0, 0, 0, 
					0, 0, 0, 0});
			
			// Sections should already be in the order their data shows up, so we can start writing data.
			Map<GCNDataFileDataSection, Long> writtenOffsets = new HashMap<GCNDataFileDataSection, Long>();
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Writing section data...");
			for (GCNDataFileDataSection section : dataSections) {
				while (builder.getBytesWritten() % 4 != 0) {
					builder.appendByte((byte)0);
				}
				assert (long)builder.getBytesWritten() == section.originalOffset;
				DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Writing section " + section.identifier + " at file offset 0x" + Long.toHexString(builder.getBytesWritten()));
				writtenOffsets.put(section, (long)builder.getBytesWritten());
				builder.appendBytes(section.getRawData(0, (int)section.getLength()));
				DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Finished writing section " + section.identifier + " at file offset 0x" + Long.toHexString(builder.getBytesWritten()));
			}
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Finished writing section data.");
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Writing text strings...");
			
			pointers.sort(new Comparator<Long>() {
				@Override
				public int compare(Long o1, Long o2) {
					return Long.compare(o1, o2);
				}
			});
			for (int i = 0; i < pointers.size(); i++) {
				while (builder.getBytesWritten() < pointers.get(i) + 0x20) {
					builder.appendByte((byte)0);
				}
				
				DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Writing string " + stringForPointer(pointers.get(i)) + " at offset 0x" + Long.toHexString(builder.getBytesWritten()));
				builder.appendBytes(WhyDoesJavaNotHaveThese.shiftJISBytesFromString(stringForPointer(pointers.get(i))));
				if (builder.getLastByteWritten() != 0) {
					builder.appendByte((byte)0);
				}
			}
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Finished writing strings.");
			
			builder.appendBytes(new byte[] {0, 0, 0, 0}); // Seems to be a 4 byte divider for this.
			
			while (builder.getBytesWritten() % 4 != 0) {
				builder.appendByte((byte)0);
			}
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Writing pointer table...");
			int pointerTableOffset = builder.getBytesWritten();
			validPointers.sort(new Comparator<Long>() {
				@Override
				public int compare(Long o1, Long o2) {
					return Long.compare(o1, o2);
				}
			});
			
			byte[] writtenBytes = builder.toByteArray();
			
			int pointersWritten = 0;
			for (long pointer : validPointers) {
//				if (WhyDoesJavaNotHaveThese.longValueFromByteArray(WhyDoesJavaNotHaveThese.subArray(writtenBytes, (int)pointer, 4), false) == 0) {
//					DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Skipping 0 pointer at offset 0x" + Long.toHexString(pointer));
//					continue;
//				}
				DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Writing pointer 0x" + Long.toHexString(pointer - 0x20) + " at offset 0x" + Long.toHexString(builder.getBytesWritten()));
				builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(pointer - 0x20, false, 4));
				pointersWritten++;
			}
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Finished writing pointer table.");
			
			DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Calculating Section Name offsets...");
			ByteArrayBuilder sectionNames = new ByteArrayBuilder();
			for (String name : orderedSectionNames) {
				DebugPrinter.log(DebugPrinter.Key.FE9_DATA_FILE_HANDLER_V2, "Section name " + name + " set to offset 0x" + Long.toHexString(sectionNames.getBytesWritten()));
				builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(writtenOffsets.get(getSectionWithName(name)) - 0x20, false, 4));
				builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(sectionNames.getBytesWritten(), false, 4));
				sectionNames.appendBytes(WhyDoesJavaNotHaveThese.shiftJISBytesFromString(name));
				if (sectionNames.getLastByteWritten() != 0) {
					sectionNames.appendByte((byte)0);
				}
			}
			
			builder.appendBytes(sectionNames.toByteArray());
			
			while (builder.getBytesWritten() % 4 != 0) {
				builder.appendByte((byte)0);
			}
			
			byteArray = builder.toByteArray();
			
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(byteArray.length, false, 4), byteArray, 0, 4); // Write the length of the file.
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(pointerTableOffset - 0x20, false, 4), byteArray, 4, 4); // Write the offset of the pointer table.
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(pointersWritten, false, 4), byteArray, 8, 4); // Write the number of pointers.
		}
	}
	
	@Override
	public byte[] getRawData() {
		return byteArray;
	}
	
	public boolean hasChanges() {
		return hasChanges || needsRebuild;
	}
}
