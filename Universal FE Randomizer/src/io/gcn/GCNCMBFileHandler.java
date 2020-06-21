package io.gcn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.FileHandler;
import util.ByteArrayBuilder;
import util.WhyDoesJavaNotHaveThese;

public class GCNCMBFileHandler extends GCNFileHandler {
	
	private Map<Long, String> stringsByAddress; // These are the exact locations in the file. When accessing, remember to offset by start of strings table.
	private Map<String, Long> addressesByString;

	private String name;
	
	private long stringTableOffset;
	private long scriptTableOffset;
	
	private byte[] fullData;
	
	private ByteArrayBuilder newStringData;
	
	public GCNCMBFileHandler(GCNFSTFileEntry entry, FileHandler handler, GCNISOHandler isoHandler) throws GCNISOException {
		super(entry, handler);
		
		stringsByAddress = new HashMap<Long, String>();
		addressesByString = new HashMap<String, Long>();
		
		// Verify Header. This is the first 0x2C bytes.
		byte[] header = readBytesAtOffset(0, 0x4);
		if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(header, new byte[] {(byte)'c', (byte)'m', (byte)'b', 0})) {
			byte[] nameBytes = continueReadingBytesUpToNextTerminator(0x2C);
			name = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(nameBytes);
			
			setNextReadOffset(0x24);
			byte[] stringTableOffsetBytes = continueReadingBytes(4);
			stringTableOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(stringTableOffsetBytes, true);
			byte[] scriptTableOffsetBytes = continueReadingBytes(4);
			scriptTableOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(scriptTableOffsetBytes, true);
			
			ByteArrayBuilder fullData = new ByteArrayBuilder();
			setNextReadOffset(0);
			while (getNextReadOffset() < getFileLength()) {
				fullData.appendBytes(continueReadingBytes(1024));
			}
			this.fullData = fullData.toByteArray();
			
			newStringData = new ByteArrayBuilder();
			
			setNextReadOffset(stringTableOffset);
			while (getNextReadOffset() < scriptTableOffset) {
				long currentOffset = getNextReadOffset();
				byte[] stringBytes = continueReadingBytesUpToNextTerminator(scriptTableOffset);
				String stringValue = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(stringBytes);
				stringsByAddress.put(currentOffset, stringValue);
				addressesByString.put(stringValue, currentOffset);
			}
		} else {
			throw new GCNISOException("Invalid CMB file header.");
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void addString(String string) {
		if (string == null) { return; }
		if (addressesByString.keySet().contains(string)) {
			return;
		}
		
		long targetAddress = scriptTableOffset + newStringData.getBytesWritten();
		
		byte[] stringData = WhyDoesJavaNotHaveThese.asciiBytesFromString(string);
		newStringData.appendBytes(stringData);
		newStringData.appendByte((byte)0);
		
		addressesByString.put(string, targetAddress);
		stringsByAddress.put(targetAddress, string);
		
		return;
	}
	
	public byte[] build() {
		if (newStringData.getBytesWritten() == 0) { return fullData; }
		
		// Split into components.
		byte[] headerData = WhyDoesJavaNotHaveThese.subArray(fullData, 0, 0x2C);
		byte[] existingStringData = WhyDoesJavaNotHaveThese.subArray(fullData, (int)stringTableOffset, (int)(scriptTableOffset - stringTableOffset));
		byte[] existingScriptData = WhyDoesJavaNotHaveThese.subArray(fullData, (int)scriptTableOffset, (int)(fullData.length - scriptTableOffset));
		
		// Pad the new string data to byte align.
		while (newStringData.getBytesWritten() % 4 != 0) { newStringData.appendByte((byte)0); }
		
		// Update script table's offset in the header.
		long newScriptTableOffset = scriptTableOffset + newStringData.getBytesWritten();
		WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(newScriptTableOffset, true, 4), headerData, 0x28, 4);
		
		// All of the pointers for the scripts need to be adjusted accordingly as well.
		ByteArrayBuilder scriptPointerBuilder = new ByteArrayBuilder();
		byte[] currentPtr = new byte[4];
		for (int i = 0; i < existingScriptData.length; i += 4) {
			currentPtr[0] = existingScriptData[i];
			currentPtr[1] = existingScriptData[i + 1];
			currentPtr[2] = existingScriptData[i + 2];
			currentPtr[3] = existingScriptData[i + 3];
			long pointer = WhyDoesJavaNotHaveThese.longValueFromByteArray(currentPtr, true);
			if (pointer == 0) { break; }
			pointer += newStringData.getBytesWritten();
			byte[] newPointerBytes = WhyDoesJavaNotHaveThese.byteArrayFromLongValue(pointer, true, 4);
			scriptPointerBuilder.appendBytes(newPointerBytes);
		}
		
		byte[] updatedPointerBytes = scriptPointerBuilder.toByteArray();
		WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(updatedPointerBytes, existingScriptData, 0, updatedPointerBytes.length);
		
		// We might need to change all of the pointers in the scripts themselves too...
		// This is dangerous. We'll see if we can identify the pointers properly. They should be valid pointers pointing to addresses
		// larger than the scriptTableOffset but smaller than the file size.
		for (int i = updatedPointerBytes.length; i < existingScriptData.length; i += 4) {
			currentPtr[0] = existingScriptData[i];
			currentPtr[1] = existingScriptData[i + 1];
			currentPtr[2] = existingScriptData[i + 2];
			currentPtr[3] = existingScriptData[i + 3];
			long pointer = WhyDoesJavaNotHaveThese.longValueFromByteArray(currentPtr, true);
			if (pointer < scriptTableOffset || pointer > fullData.length) { continue; }
			pointer += newStringData.getBytesWritten();
			byte[] newPointerBytes = WhyDoesJavaNotHaveThese.byteArrayFromLongValue(pointer, true, 4);
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(newPointerBytes, existingScriptData, i, 4);
		}
		
		// Build it.
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendBytes(headerData);
		builder.appendBytes(existingStringData);
		assert(builder.getBytesWritten() == scriptTableOffset);
		builder.appendBytes(newStringData.toByteArray());
		assert(builder.getBytesWritten() == newScriptTableOffset);
		builder.appendBytes(existingScriptData);
		
		newStringData.clear();
		fullData = builder.toByteArray();
		scriptTableOffset = newScriptTableOffset;
		
		return builder.toByteArray();
	}
	
	public byte[] bytePrefixForString(String string) {
		if (string == null) {
			return null;
		}
		
		Long addressForString = addressesByString.get(string);
		if (addressForString != null) {
			return WhyDoesJavaNotHaveThese.byteArrayFromLongValue(addressForString - stringTableOffset, false, 2);
		}
		
		return null;
	}
	
	public String stringForOffset(byte[] offsetBytes) {
		long offsetValue = WhyDoesJavaNotHaveThese.longValueFromByteArray(offsetBytes, false) + stringTableOffset;
		return stringsByAddress.get(offsetValue);
	}
	
	public List<Long> offsetsForString(String string) {
		if (string == null) {
			return null;
		}
		
		Long addressForString = addressesByString.get(string);
		if (addressForString != null) {
			return offsetsForBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(addressForString - stringTableOffset, false, 2));
		}
		
		return null;
	}
	
	public byte[] cmb_readBytesAtOffset(long offset, int length) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		for (int i = 0; i < length; i++) {
			builder.appendByte(fullData[(int)(offset + i)]);
		}
		return builder.toByteArray();
	}
	
	public byte[] cmb_readBytesUpToNextTerminator(int offset) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		int index = offset;
		byte currentByte = fullData[index++];
		while (currentByte != 0) {
			builder.appendByte(currentByte);
			currentByte = fullData[index++];
		}
		
		builder.appendByte((byte)0);
		return builder.toByteArray();
	}
	
	public void cmb_writeBytesToOffset(long offset, byte[] bytesToWrite) {
		for (int i = 0; i < bytesToWrite.length; i++) {
			fullData[(int)(offset + i)] = bytesToWrite[i];
		}
	}
	
	public List<Long> offsetsForBytes(byte[] bytesToSearch) {
		List<Long> offsets = new ArrayList<Long>();
		long currentOffset = scriptTableOffset;
		while (currentOffset < fullData.length) {
			Long nextOffset = advanceToNextInstance(bytesToSearch, currentOffset);
			if (nextOffset != null) {
				offsets.add(nextOffset);
			} else {
				break;
			}
			currentOffset = nextOffset + 1;
		}
		return offsets;
	}
	
	public long getScriptTableOffset() {
		return scriptTableOffset;
	}
	
	private Long advanceToNextInstance(byte[] bytesToSearch, long startingOffset) {
		byte currentByte = 0;
		long currentOffset = startingOffset;
		while (currentOffset < fullData.length) {
			currentByte = fullData[(int)currentOffset];
			int index = 0;
			while (index < bytesToSearch.length && bytesToSearch[index] == currentByte) {
				if (++index >= bytesToSearch.length) {
					return currentOffset;
				}
				if (currentOffset + index >= fullData.length) { break; }
				currentByte = fullData[(int)(currentOffset + index)];
			}
			currentOffset++;
		}
		
		return null;
	}
	
	@Override
	public long getFileLength() {
		if (fullData == null) { return super.getFileLength(); }
		return fullData.length;
	}
}
