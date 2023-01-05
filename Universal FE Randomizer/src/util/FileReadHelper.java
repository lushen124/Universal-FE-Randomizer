package util;

import io.FileHandler;

public class FileReadHelper {

	public static long readWord(FileHandler handler, long offset, Boolean isPointer) {
		byte[] word = handler.readBytesAtOffset(offset, 4);
		return wordValue(word, isPointer);
	}
	
	public static long readWord(FileHandler handler, Boolean isPointer) {
		byte[] word = handler.continueReadingBytes(4);
		return wordValue(word, isPointer);
	}
	
	
	
	public static long readBigEndianWord(FileHandler handler, long offset) {
		byte[] word = handler.readBytesAtOffset(offset, 4);
		long result = 0;
		result = word[0];
		result = (result << 8) | word[1];
		result = (result << 8) | word[2];
		result = (result << 8) | word[3];
		
		return result;
	}
	
	public static byte[] readBytesInRange(AddressRange range, FileHandler handler) {
		return handler.readBytesAtOffset(range.start, (int)(range.end - range.start));
	}
	
	public static int readSignedHalfWord(FileHandler handler, long offset) {
		byte[] halfword = handler.readBytesAtOffset(offset, 2);
		int result = 0;
		result |= (halfword[0] & 0xFF);
		result |= (((halfword[1] & 0xFF) << 8));
		if ((halfword[1] & 0x80) != 0) {
			result |= 0xFFFF0000;
		}
		
		return result;
	}
	
	public static long readAddress(FileHandler handler, long offset) {
		long address = readWord(handler, offset, true);
		if (address >= 0x1000 && address <= 0x1FFFFFF) {
			return address;
		} else {
			return -1;
		}
	}
	
	public static long readAddress(FileHandler handler) {
		long address = readWord(handler, true);
		if (address >= 0x1000 && address <= 0x1FFFFFF) {
			return address;
		} else {
			return -1;
		}
	}
	
	public static long wordValue(byte[] word, Boolean isPointer) {
		long result = 0;
		result |= (word[0] & 0xFF);
		result |= ((word[1] << 8) & 0xFF00);
		result |= ((word[2] << 16) & 0xFF0000);
		result |= ((word[3] << 24) & 0xFF000000);
		
		result &= 0xFFFFFFFFL;
		
		if (isPointer) {
			if (result > 0x8000000) {
				result -= 0x8000000;
			} else {
				return -1;
			}
		}
		
		return result;
	}
}
