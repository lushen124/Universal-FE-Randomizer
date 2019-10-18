package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileWriter {

	public static void writeBinaryDataToFile(byte[] data, String destinationFile) throws IOException {
		new File(destinationFile.substring(0, destinationFile.lastIndexOf(File.separator))).mkdirs();
		FileOutputStream outputStream = new FileOutputStream(destinationFile);
		outputStream.write(data);
		outputStream.close();
	}
	
	private FileOutputStream outputStream;
	
	private long bytesWritten = 0;
	
	public FileWriter(String destination) throws FileNotFoundException {
		new File(destination.substring(0, destination.lastIndexOf(File.separator))).mkdirs();
		outputStream = new FileOutputStream(destination);
	}
	
	public void write(byte[] data) throws IOException {
		outputStream.write(data);
		bytesWritten += data.length;
	}
	
	public void write(byte[] data, int length) throws IOException {
		outputStream.write(data, 0, length);
		bytesWritten += length;
	}
	
	public void write(byte value) throws IOException {
		outputStream.write(value);
		bytesWritten++;
	}
	
	public void copyFromStream(InputStream input, int length) throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		
		while ((bytesRead = input.read(buffer, 0, Math.min(1024, length))) != -1) {
			outputStream.write(buffer, 0, bytesRead);
			length -= bytesRead;
			bytesWritten += bytesRead;
			if (length <= 0) { break; }
		}
	}
	
	public long getBytesWritten() {
		return bytesWritten;
	}
	
	public void finish() {
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
