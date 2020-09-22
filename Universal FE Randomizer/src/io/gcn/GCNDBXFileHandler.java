package io.gcn;

import java.util.Arrays;

import io.FileHandler;
import util.ByteArrayBuilder;
import util.WhyDoesJavaNotHaveThese;

public class GCNDBXFileHandler extends GCNByteArrayHandler {

	private boolean hasChanges = false;
	
	public GCNDBXFileHandler(GCNFSTFileEntry file, FileHandler handler, byte[] byteArray) {
		super(file, handler, byteArray);
	}

	@Override
	public long getFileLength() {
		return byteArray.length;
	}
	
	public byte[] getRawData() {
		byte[] rawData = Arrays.copyOf(byteArray, byteArray.length);
		return diffCompiler.byteArrayWithDiffs(rawData, 0);
	}
	
	public String getStringForKey(String key, int block) {
		if (key == null) { return null; }
		
		byte[] keyBytes = WhyDoesJavaNotHaveThese.asciiBytesFromString(key);
		
		int blockIndex = 0;
		int startIndex = WhyDoesJavaNotHaveThese.firstIndexOfBytesInByteArray(byteArray, WhyDoesJavaNotHaveThese.asciiBytesFromString("{"), 0, byteArray.length);
		if (startIndex == -1) { return null; } // No curly braces found.
		while (blockIndex < block) {
			blockIndex++;
			startIndex = WhyDoesJavaNotHaveThese.firstIndexOfBytesInByteArray(byteArray, WhyDoesJavaNotHaveThese.asciiBytesFromString("{"), startIndex + 1, byteArray.length);
		}
		if (startIndex == -1) { return null; } // Block index out of bounds.
		int endIndex = WhyDoesJavaNotHaveThese.firstIndexOfBytesInByteArray(byteArray, WhyDoesJavaNotHaveThese.asciiBytesFromString("}"), startIndex, byteArray.length);
		
		int keyIndex = WhyDoesJavaNotHaveThese.firstIndexOfBytesInByteArray(byteArray, keyBytes, startIndex, endIndex);
		if (keyIndex == -1) { return null; } // Key not found.
		
		int valueIndex = keyIndex + keyBytes.length;
		byte valueByte;
		while (byteArray[valueIndex] < 0x20) { valueIndex++; }
		
		ByteArrayBuilder builder = new ByteArrayBuilder();
		do {
			valueByte = byteArray[valueIndex++];
			if (valueByte < 0x20) {
				break;
			}
			builder.appendByte(valueByte);
		} while (valueByte > 0x20);
		
		return WhyDoesJavaNotHaveThese.stringFromAsciiBytes(builder.toByteArray());
	}
	
	public void setStringForKey(String key, int block, String value) {
		if (key == null || value == null) { return; }
		
		byte[] keyBytes = WhyDoesJavaNotHaveThese.asciiBytesFromString(key);
		
		int blockIndex = 0;
		int startIndex = WhyDoesJavaNotHaveThese.firstIndexOfBytesInByteArray(byteArray, WhyDoesJavaNotHaveThese.asciiBytesFromString("{"), 0, byteArray.length);
		if (startIndex == -1) { return; } // No curly braces found.
		while (blockIndex < block) {
			blockIndex++;
			startIndex = WhyDoesJavaNotHaveThese.firstIndexOfBytesInByteArray(byteArray, WhyDoesJavaNotHaveThese.asciiBytesFromString("{"), startIndex + 1, byteArray.length);
		}
		if (startIndex == -1) { return; } // Block index out of bounds.
		int endIndex = WhyDoesJavaNotHaveThese.firstIndexOfBytesInByteArray(byteArray, WhyDoesJavaNotHaveThese.asciiBytesFromString("}"), startIndex, byteArray.length);
		
		int keyIndex = WhyDoesJavaNotHaveThese.firstIndexOfBytesInByteArray(byteArray, keyBytes, startIndex, endIndex);
		if (keyIndex == -1) {
			// This is a new key.
			ByteArrayBuilder newData = new ByteArrayBuilder();
			newData.appendBytes(byteArray, endIndex);
			// Insert a tab first. (0x9)
			newData.appendByte((byte)0x9);
			// Insert the key string.
			newData.appendBytes(keyBytes);
			// Then another tab.
			newData.appendByte((byte)0x9);
			// Then the value.
			byte[] valueBytes = WhyDoesJavaNotHaveThese.asciiBytesFromString(value);
			newData.appendBytes(valueBytes);
			// Then a CR (0xD) and LF (0xA)
			newData.appendByte((byte)0xD);
			newData.appendByte((byte)0xA);
			// We can write the rest of the original data.
			newData.appendBytes(WhyDoesJavaNotHaveThese.subArray(byteArray, endIndex, byteArray.length - endIndex));
			byteArray = newData.toByteArray();
		} else {
			// Replace value for existing key.
			int valueStartIndex = keyIndex + keyBytes.length;
			while (byteArray[valueStartIndex] < 0x20) { valueStartIndex++; }
			int valueEndIndex = valueStartIndex;
			while (byteArray[valueEndIndex] > 0x20) { valueEndIndex++; }
			ByteArrayBuilder newData = new ByteArrayBuilder();
			newData.appendBytes(byteArray, valueStartIndex);
			newData.appendBytes(WhyDoesJavaNotHaveThese.asciiBytesFromString(value));
			newData.appendBytes(WhyDoesJavaNotHaveThese.subArray(byteArray, valueEndIndex, byteArray.length - valueEndIndex));
			byteArray = newData.toByteArray();
		}
		
		hasChanges = true;
	}

	@Override
	public boolean hasChanges() {
		return hasChanges;
	}
	
	
}
