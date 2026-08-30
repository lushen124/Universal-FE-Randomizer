package util;

public class FindAndReplace {
	public byte[] oldBytes;
	public byte[] newBytes;
	public boolean isByteAligned;
	
	public FindAndReplace(byte[] bytesToReplace, byte[] bytesToReplaceWith, boolean byteAligned) {
		assert bytesToReplace.length == bytesToReplaceWith.length;
		oldBytes = bytesToReplace;
		newBytes = bytesToReplaceWith;
		isByteAligned = byteAligned;
	}

}
