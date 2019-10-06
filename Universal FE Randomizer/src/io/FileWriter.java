package io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWriter {

	public static void writeBinaryDataToFile(byte[] data, String destinationFile) throws IOException {
		new File(destinationFile.substring(0, destinationFile.lastIndexOf(File.separator))).mkdirs();
		FileOutputStream outputStream = new FileOutputStream(destinationFile);
		outputStream.write(data);
		outputStream.close();
	}
}
