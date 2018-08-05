package util;

import io.FileHandler;

public class FileReadHelper {

	public static int readWord(FileHandler handler, long offset, Boolean isPointer) {
		byte[] word = handler.readBytesAtOffset(offset, 4);
		int result = 0;
		result |= (word[0] & 0xFF);
		result |= (((word[1] & 0xFF) << 8));
		result |= (((word[2] & 0xFF) << 16));
		result |= (((word[3] & 0xFF) << 24));
		
		if (isPointer && result > 0x8000000) {
			result -= 0x8000000;
		}
		
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
}
