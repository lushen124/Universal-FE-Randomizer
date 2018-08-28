package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;

import util.DiffCompiler;

public class FileHandler {
	public String pathToFile;
	
	private RandomAccessFile inputFile;
	private long crc32;
	private long fileLength;
	
	private DiffCompiler appliedDiffs;
	
	private long nextReadOffset = 0;
	
	public FileHandler(File file) throws IOException {
		super();
		this.pathToFile = file.getAbsolutePath();
		
		inputFile = new RandomAccessFile(file, "r");
		fileLength = inputFile.length();
		
		FileInputStream inputStream = new FileInputStream(pathToFile);
		
		CRC32 checksum = new CRC32();
		byte[] batch = new byte[256];
		while (inputStream.read(batch) != -1) {
			checksum.update(batch);
		}
		
		crc32 = checksum.getValue();
		
		inputStream.close();
	}

	public FileHandler(String pathToFile) throws IOException {
		super();
		this.pathToFile = pathToFile;
		
		inputFile = new RandomAccessFile(pathToFile, "r");
		fileLength = inputFile.length();
		
		FileInputStream inputStream = new FileInputStream(pathToFile);
		
		CRC32 checksum = new CRC32();
		byte[] batch = new byte[256];
		while (inputStream.read(batch) != -1) {
			checksum.update(batch);
		}
		
		crc32 = checksum.getValue();
		
		inputStream.close();
	}
	
	public void setAppliedDiffs(DiffCompiler diffs) {
		appliedDiffs = diffs;
	}
	
	public void clearAppliedDiffs() {
		appliedDiffs = null;
	}
	
	public long getNextReadOffset() {
		return nextReadOffset;
	}
	
	public byte[] readBytesAtOffset(long offset, int numBytes) {
		long remainingBytes = fileLength - offset;
		if (numBytes > remainingBytes) {
			numBytes = (int)remainingBytes;
		}
		byte[] outputBytes = new byte[numBytes];
		
		try {
			inputFile.seek(offset);
			inputFile.readFully(outputBytes);
			nextReadOffset = inputFile.getFilePointer();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Failed to read " + numBytes + " bytes starting from offset " + Long.toHexString(offset) + ".");
			return null;
		}
		
		if (appliedDiffs != null) {
			return appliedDiffs.byteArrayWithDiffs(outputBytes, offset);
		}
		
		return outputBytes;
	}
	
	public long getCRC32() {
		return crc32;
	}
	
	public long getFileLength() {
		return fileLength;
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			inputFile.close();
		} finally {
			super.finalize();
		}
	}
}
