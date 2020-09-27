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
	
	private static class StringEntry {
		String identifier;
		int valueOffset;
		int identifierOffset;
		int entryIndex;
		
		public StringEntry(String identifier, int valueOffset, int identifierOffset, int entryIndex) {
			this.identifier = identifier;
			this.valueOffset = valueOffset;
			this.identifierOffset = identifierOffset;
			this.entryIndex = entryIndex;
		}
		
		public static Comparator<StringEntry> getValueComparator() {
			return new Comparator<StringEntry>() {
				@Override
				public int compare(StringEntry arg0, StringEntry arg1) {
					return Integer.compare(arg0.valueOffset, arg1.valueOffset);
				}
			};
		}
		
		public static Comparator<StringEntry> getIdentifierOffsetComparator() {
			return new Comparator<StringEntry>() {
				@Override
				public int compare(StringEntry arg0, StringEntry arg1) {
					return Integer.compare(arg0.identifierOffset, arg1.identifierOffset);
				}
			};
		}
		
		public static Comparator<StringEntry> getEntryIndexComparator() {
			return new Comparator<StringEntry>() {
				@Override
				public int compare(StringEntry arg0, StringEntry arg1) {
					return Integer.compare(arg0.entryIndex, arg1.entryIndex);
				}
			};
		}
	}
	
	private String fileIdentifier;
	
	private long entryStartingOffset;
	
	private List<StringEntry> orderedIDs;
	
	private Map<String, String> idToDisplayString;
	
	private Map<String, String> stringsToAdd;

	private byte[] builtData;
	
	private boolean needsRebuild = false;
	
	public GCNMessageFileHandler(GCNFSTFileEntry entry, FileHandler handler, byte[] rawData) {
		super(entry, handler);
		
		orderedIDs = new ArrayList<StringEntry>();
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
			long valueOffset = textEntry.getStringOffset();
			orderedIDs.add(new StringEntry(identifier, (int)valueOffset - 0x20, (int)idOffset, i));
			
			byte[] stringData = mess_readUntilTerminator(valueOffset);
			String result = WhyDoesJavaNotHaveThese.stringFromShiftJIS(stringData);
			idToDisplayString.put(identifier, result);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_TEXT_LOADER, "Loaded text entry: " + identifier + " (" + result + ")");
		}
	}
	
	public GCNMessageFileHandler(GCNFSTFileEntry entry, FileHandler handler, GCNISOHandler isoHandler) throws GCNISOException {
		super(entry, handler);
		
		fileIdentifier = isoHandler.fstNameOfEntry(entry);

		orderedIDs = new ArrayList<StringEntry>();
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
			long valueOffset = textEntry.getStringOffset();
			orderedIDs.add(new StringEntry(identifier, (int)valueOffset, (int)idOffset, i));
			
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
		identifiers.addAll(orderedIDs.stream().map(entry -> { return entry.identifier; }).collect(Collectors.toList()));
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
	
	public byte[] orderedBuild() {
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
		
		// Build the raw string data. These are word-aligned.
		orderedIDs.sort(StringEntry.getValueComparator());
		ByteArrayBuilder valueBuilder = new ByteArrayBuilder();
		Map<String, Integer> valueOffsetsByID = new HashMap<String, Integer>();
		
		for (StringEntry entry : orderedIDs) {
			String id = entry.identifier;
			while (valueBuilder.getBytesWritten() < entry.valueOffset) {
				valueBuilder.appendByte((byte)0);
			}
			while (valueBuilder.getBytesWritten() % 4 != 0) { valueBuilder.appendByte((byte)0); }
			
			valueOffsetsByID.put(id, valueBuilder.getBytesWritten());
			valueBuilder.appendBytes(WhyDoesJavaNotHaveThese.shiftJISBytesFromString(idToDisplayString.get(id)));
			valueBuilder.appendByte((byte)0);
		}
		
		// Add new strings here.
		for (String addedStringID : stringsToAdd.keySet()) {
			String addedString = stringsToAdd.get(addedStringID);
			while (valueBuilder.getBytesWritten() % 4 != 0) { valueBuilder.appendByte((byte)0); }
			valueOffsetsByID.put(addedStringID, valueBuilder.getBytesWritten());
			valueBuilder.appendBytes(WhyDoesJavaNotHaveThese.shiftJISBytesFromString(addedString));
			valueBuilder.appendByte((byte)0);
		}
		
		// Build the ID table. These are null terminated, but not word-aligned.
		orderedIDs.sort(StringEntry.getIdentifierOffsetComparator());
		ByteArrayBuilder idBuilder = new ByteArrayBuilder();
		Map<String, Integer> idOffsetsByID = new HashMap<String, Integer>();
		
		for (StringEntry entry : orderedIDs) {
			String id = entry.identifier;
			idOffsetsByID.put(id, idBuilder.getBytesWritten());
			idBuilder.appendBytes(WhyDoesJavaNotHaveThese.shiftJISBytesFromString(id));
			idBuilder.appendByte((byte)0);
		}
		
		// Add new string IDs here.
		for (String addedStringID : stringsToAdd.keySet()) {
			idOffsetsByID.put(addedStringID, idBuilder.getBytesWritten());
			idBuilder.appendBytes(WhyDoesJavaNotHaveThese.shiftJISBytesFromString(addedStringID));
			idBuilder.appendByte((byte)0);
		}
		
		// Write the table of pointers.
		// It should be a pointer to the value, followed by a pointer to the ID.
		orderedIDs.sort(StringEntry.getEntryIndexComparator());
		ByteArrayBuilder pointerBuilder = new ByteArrayBuilder();
		for (StringEntry entry : orderedIDs) {
			String identifier = entry.identifier;
			pointerBuilder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(valueOffsetsByID.get(identifier), false, 4));
			pointerBuilder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(idOffsetsByID.get(identifier), false, 4));
		}
		
		// Add pointers for added strings.
		for (String addedID : stringsToAdd.keySet()) {
			pointerBuilder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(valueOffsetsByID.get(addedID), false, 4));
			pointerBuilder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(idOffsetsByID.get(addedID), false, 4));
		}
		
		// Compile them all together.
		builder.appendBytes(valueBuilder.toByteArray());
		while (builder.getBytesWritten() % 4 != 0) { builder.appendByte((byte)0); }
		int pointerTableOffset = builder.getBytesWritten();
		builder.appendBytes(pointerBuilder.toByteArray());
		builder.appendBytes(idBuilder.toByteArray());
		
		// Set the file length.
		builder.replaceBytes(0, WhyDoesJavaNotHaveThese.byteArrayFromLongValue(builder.getBytesWritten(), false, 4));
		// Set the pointer table offset.
		builder.replaceBytes(4, WhyDoesJavaNotHaveThese.byteArrayFromLongValue(pointerTableOffset - 0x20, false, 4));
		
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
