package io;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileWriter {

	public static void writeBinaryDataToFile(byte[] data, String destinationFile) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(destinationFile);
		outputStream.write(data);
		outputStream.close();
	}
}
