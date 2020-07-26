package io.gcn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9TextEntry;
import io.FileHandler;
import util.ByteArrayBuilder;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class GCNMessageFileHandler extends GCNFileHandler {
	
	private String fileIdentifier;
	
	private long entryStartingOffset;
	
	private List<String> orderedIDs;
	
	private Map<String, String> idToDisplayString;
	
	private Map<String, String> stringsToAdd;

	private byte[] builtData;
	
	private boolean needsRebuild = false;
	
	public GCNMessageFileHandler(GCNFSTFileEntry entry, FileHandler handler, byte[] rawData) {
		super(entry, handler);
		
		orderedIDs = new ArrayList<String>();
		idToDisplayString = new HashMap<String, String>();
		
		builtData = rawData;
		
		entryStartingOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(mess_readBytesAtOffset(0x4, 4), false) + 0x20;
		int count = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(mess_readBytesAtOffset(0xC, 4), false);
		
		long idStartOffset = entryStartingOffset + (count * FE9Data.CommonTextEntrySize);
		
		for (int i = 0; i < count; i++) {
			long dataOffset = entryStartingOffset + i * FE9Data.CommonTextEntrySize;
			byte[] data = mess_readBytesAtOffset(dataOffset, FE9Data.CommonTextEntrySize);
			FE9TextEntry textEntry = new FE9TextEntry(data, dataOffset);
			
			long idOffset = textEntry.getIDOffset() + idStartOffset;
			byte[] idBytes = mess_readUntilTerminator(idOffset);
			String identifier = WhyDoesJavaNotHaveThese.stringFromShiftJIS(idBytes);
			orderedIDs.add(identifier);
			
			long valueOffset = textEntry.getStringOffset();
			byte[] stringData = mess_readUntilTerminator(valueOffset);
			String result = WhyDoesJavaNotHaveThese.stringFromShiftJIS(stringData);
			idToDisplayString.put(identifier, result);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_TEXT_LOADER, "Loaded text entry: " + identifier + " (" + result + ")");
		}
	}
	
	public GCNMessageFileHandler(GCNFSTFileEntry entry, FileHandler handler, GCNISOHandler isoHandler) throws GCNISOException {
		super(entry, handler);
		
		fileIdentifier = isoHandler.fstNameOfEntry(entry);

		orderedIDs = new ArrayList<String>();
		idToDisplayString = new HashMap<String, String>();
		
		entryStartingOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(0x4, 4), false) + 0x20;
		int count = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(readBytesAtOffset(0xC, 4), false);
		
		long idStartOffset = entryStartingOffset + (count * FE9Data.CommonTextEntrySize);
		
		for (int i = 0; i < count; i++) {
			long dataOffset = entryStartingOffset + i * FE9Data.CommonTextEntrySize;
			byte[] data = readBytesAtOffset(dataOffset, FE9Data.CommonTextEntrySize);
			FE9TextEntry textEntry = new FE9TextEntry(data, dataOffset);
			
			long idOffset = textEntry.getIDOffset() + idStartOffset;
			setNextReadOffset(idOffset);
			byte[] idBytes = continueReadingBytesUpToNextTerminator(idOffset + 0xFF);
			String identifier = WhyDoesJavaNotHaveThese.stringFromShiftJIS(idBytes);
			orderedIDs.add(identifier);
			
			long valueOffset = textEntry.getStringOffset();
			setNextReadOffset(valueOffset);
			byte[] stringData = continueReadingBytesUpToNextTerminator(valueOffset + 0xFFFF);
			String result = WhyDoesJavaNotHaveThese.stringFromShiftJIS(stringData);
			idToDisplayString.put(identifier, result);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_TEXT_LOADER, "Loaded text entry: " + identifier + " (" + result + ")");
		}
		
		ByteArrayBuilder rawData = new ByteArrayBuilder();
		setNextReadOffset(0);
		while (getNextReadOffset() < super.getFileLength()) {
			rawData.appendBytes(continueReadingBytes(1024));
		}
		builtData = rawData.toByteArray();
	}
	
	public List<String> allIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.addAll(orderedIDs);
		if (stringsToAdd != null) {
			identifiers.addAll(stringsToAdd.keySet());
		}
		return identifiers;
	}

	public String getStringWithIdentifier(String identifier) {
		if (stringsToAdd != null && stringsToAdd.containsKey(identifier)) { return stringsToAdd.get(identifier); }
		return idToDisplayString.get(identifier);
	}
	
	public void setStringForIdentifier(String identifier, String newString) {
		idToDisplayString.put(identifier, newString);
		needsRebuild = true;
	}
	
	public void addStringWithIdentifier(String newIdentifier, String newString) {
		if (stringsToAdd == null) { stringsToAdd = new HashMap<String, String>(); }
		if (idToDisplayString.containsKey(newIdentifier)) {
			setStringForIdentifier(newIdentifier, newString);
			return;
		}
		
		stringsToAdd.put(newIdentifier, newString);
		needsRebuild = true;
	}
	
	public byte[] build() {
		if (!needsRebuild) { return builtData; }
		
		ByteArrayBuilder builder = new ByteArrayBuilder();
		// This file starts with a 0x20 header.
		// The first four bytes determine the file size itself.
		builder.appendBytes(new byte[] {0, 0, 0, 0}); // To be filled in later.
		
		// The next four bytes determine the starting offset of the entries.
		builder.appendBytes(new byte[] {0, 0, 0, 0}); // To be filled in later.
		
		// The next four bytes are 0.
		builder.appendBytes(new byte[] {0, 0, 0, 0});
		
		// The next four bytes are the number of entries.
		long numberOfEntries = orderedIDs.size() + stringsToAdd.size();
		builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(numberOfEntries, false, 4));
		
		// The remaining bytes are 0 until the start of the (byte-aligned) raw strings.
		builder.appendBytes(new byte[] {0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0});
		
		// Write the raw string data.
		Map<String, Long> stringOffsets = new HashMap<String, Long>();
		
		// Keep track of the IDs while we're at it.
		ByteArrayBuilder idBuilder = new ByteArrayBuilder();
		Map<String, Long> idOffsets = new HashMap<String, Long>();
		
		for (String id : orderedIDs) {
			stringOffsets.put(id, (long)builder.getBytesWritten() - 0x20); // Subtracting 0x20 to account for the header space.
			String string = idToDisplayString.get(id);
			builder.appendBytes(WhyDoesJavaNotHaveThese.asciiBytesFromString(string));
			builder.appendByte((byte)0);
			while (builder.getBytesWritten() % 4 != 0) { builder.appendByte((byte)0); }
			
			idOffsets.put(id, (long)idBuilder.getBytesWritten());
			idBuilder.appendBytes(WhyDoesJavaNotHaveThese.asciiBytesFromString(id));
			idBuilder.appendByte((byte)0);
		}
		
		List<String> orderedNewIDs = null;
		if (stringsToAdd != null) {
			orderedNewIDs = stringsToAdd.keySet().stream().sorted(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			}).collect(Collectors.toList());
		
			for (String addedID : orderedNewIDs) {
				stringOffsets.put(addedID, (long)builder.getBytesWritten() - 0x20);
				String string = stringsToAdd.get(addedID);
				builder.appendBytes(WhyDoesJavaNotHaveThese.asciiBytesFromString(string));
				builder.appendByte((byte)0);
				while (builder.getBytesWritten() % 4 != 0) { builder.appendByte((byte)0); }
				
				idOffsets.put(addedID, (long)idBuilder.getBytesWritten());
				idBuilder.appendBytes(WhyDoesJavaNotHaveThese.asciiBytesFromString(addedID));
				idBuilder.appendByte((byte)0);
			}
		}
		
		// After the raw strings is the entry table. We can write the offset in the header now, based on our current offset.
		builder.replaceBytes(4, WhyDoesJavaNotHaveThese.byteArrayFromLongValue(builder.getBytesWritten() - 0x20, false, 4));
		
		// Build the entries table.
		for (String id : orderedIDs) {
			// The order is the string offset first, followed by the ID offset.
			builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(stringOffsets.get(id), false, 4));
			builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(idOffsets.get(id), false, 4));
		}
		
		if (orderedNewIDs != null) {
			for (String addedID : orderedNewIDs) {
				builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(stringOffsets.get(addedID), false, 4));
				builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(idOffsets.get(addedID), false, 4));
			}
		}
		
		// Append the idBuilder's data from earlier.
		builder.appendBytes(idBuilder.toByteArray());
		
		// Now we can finish with writing the file size in the header.
		builder.replaceBytes(0, WhyDoesJavaNotHaveThese.byteArrayFromLongValue(builder.getBytesWritten(), false, 4));
		
		builtData = builder.toByteArray();
		
		needsRebuild = false;
		
		return builder.toByteArray();
	}
	
	@Override
	public long getFileLength() {
		return builtData.length;
	}
	
	private byte[] mess_readBytesAtOffset(long offset, int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = builtData[(int)(i + offset)];
		}
		
		return result;
	}
	
	private byte[] mess_readUntilTerminator(long startOffset) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		int currentOffset = (int)startOffset;
		while (builtData[currentOffset] != 0) {
			builder.appendByte(builtData[currentOffset++]);
		}
		return builder.toByteArray();
	}
}
