package util;

import java.util.Arrays;

public class ByteArrayBuilder {
	
	private byte[] byteArray;
	private int bytesWritten;
	
	public ByteArrayBuilder() {
		byteArray = new byte[255];
		bytesWritten = 0;
	}
	
	public void appendByte(byte value) {
		if (bytesWritten == byteArray.length) { byteArray = Arrays.copyOf(byteArray, bytesWritten * 2); }
		byteArray[bytesWritten++] = value;
	}
	
	public void appendBytes(byte[] otherArray) {
		for (int i = 0; i < otherArray.length; i++) {
			appendByte(otherArray[i]);
		}
	}
	
	public void appendBytes(byte[] otherArray, int length) {
		for (int i = 0; i < length; i++) {
			appendByte(otherArray[i]);
		}
	}

	public byte[] toByteArray() {
		return Arrays.copyOfRange(byteArray, 0, bytesWritten);
	}
	
	public int getBytesWritten() {
		return bytesWritten;
	}
}
