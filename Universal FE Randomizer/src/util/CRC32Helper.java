package util;

import java.util.Arrays;
import java.util.zip.CRC32;

public class CRC32Helper {
	public static long getCRC32(byte[] data) {
		CRC32 checksum = new CRC32();
		long currentOffset = 0;
		int numBytes = Math.min(1024, (int)(data.length - currentOffset));
		while (numBytes > 0) {
			byte[] batch = Arrays.copyOfRange(data, (int)currentOffset, (int)currentOffset + numBytes);
			checksum.update(batch);
			currentOffset += batch.length;
			numBytes = Math.min(1024, (int)(data.length - currentOffset));
		}
		
		return checksum.getValue();
	}

}
