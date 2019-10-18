package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
		long currentOffset = 0;
		int numBytes = Math.min(1024, (int)(fileLength - currentOffset));
		while (numBytes > 0) {
			byte[] batch = new byte[numBytes];
			if (inputStream.read(batch) == -1) { break; }
			checksum.update(batch);
			currentOffset += batch.length;
			numBytes = Math.min(1024, (int)(fileLength - currentOffset));
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
		long currentOffset = 0;
		int numBytes = Math.min(1024, (int)(fileLength - currentOffset));
		while (numBytes > 0) {
			byte[] batch = new byte[numBytes];
			if (inputStream.read(batch) == -1) { break; }
			checksum.update(batch);
			currentOffset += batch.length;
			numBytes = Math.min(1024, (int)(fileLength - currentOffset));
		}
		
		crc32 = checksum.getValue();
		
		inputStream.close();
	}
	
	public void close() {
		try {
			if (inputFile != null) { inputFile.close(); }
		} catch (IOException e) {
			
		}
		inputFile = null;
	}
	
	public InputStream getInputStream(long offset) throws IOException {
		InputStream stream = new FileInputStream(pathToFile);
		stream.skip(offset);
		return stream;
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
	
	public void setNextReadOffset(long newOffset) {
		if (inputFile != null) {
			try {
				inputFile.seek(newOffset);
				nextReadOffset = newOffset;
			} catch (IOException e) {
				System.err.println("Failed to seek to offset.");
			}
		}
	}
	
	public byte continueReadingNextByte() {
		if (inputFile == null) { return 0; }
		byte[] outputBytes = new byte[1];
		try {
			inputFile.readFully(outputBytes);
			nextReadOffset++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Failed to read next byte starting from offset " + Long.toHexString(nextReadOffset) + ".");
			return 0;
		}
		
		return outputBytes[0];
	}
	
	public int continueReadingBytes(byte[] buffer) {
		if (inputFile == null) { return 0; }
		int bytesRead = 0;
		long bytesRemaining;
		try {
			bytesRemaining = fileLength - inputFile.getFilePointer();
		} catch (IOException e1) {
			return 0;
		}
		try {
			inputFile.readFully(buffer);
			bytesRead = (int)Math.min(buffer.length, bytesRemaining);
			nextReadOffset += bytesRead;
		} catch (IOException e) {
		}
		
		return bytesRead;
	}
	
	public byte[] continueReadingBytes(int numBytes) {
		if (inputFile == null) { return new byte[] {}; }
		try {
			long remainingBytes = fileLength - inputFile.getFilePointer();
			if (numBytes > remainingBytes) {
				numBytes = (int)remainingBytes;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to get file pointer.");
			return null;
		}
		
		byte[] outputBytes = new byte[numBytes];
		
		try {
			inputFile.readFully(outputBytes);
			nextReadOffset += numBytes;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Failed to read " + numBytes + " bytes starting from offset " + Long.toHexString(nextReadOffset) + ".");
			return null;
		}
		
		if (appliedDiffs != null) {
			return appliedDiffs.byteArrayWithDiffs(outputBytes, nextReadOffset - numBytes);
		}
		
		return outputBytes;
	}
	
	public byte[] continueReadingBytesUpToNextTerminator(long maxOffset) {
		if (inputFile == null) { return new byte[] {}; }
		
		byte[] result = null;
		int zeroIndex = -1;
		
		do {
			long initialReadOffset = nextReadOffset;
			int numBytes = Math.min(1024, (int)(maxOffset - initialReadOffset - 1));
			if (numBytes <= 0) {
				break;
			}
			
			byte[] batch = continueReadingBytes(numBytes);
			int oldSize = result != null ? result.length : 0;
			int deltaSize = 0;
			for (int i = 0; i < batch.length; i++) {
				deltaSize++;
				if (batch[i] == 0) {
					zeroIndex = i;
					break;
				}
			}
			int newSize = oldSize + deltaSize;
			byte[] newResult = new byte[newSize];
			for (int i = 0; i < oldSize; i++) {
				newResult[i] = result[i];
			}
			for (int i = 0; i < deltaSize; i++) {
				newResult[i + oldSize] = batch[i];
			}
			if (zeroIndex != -1) {
				nextReadOffset = initialReadOffset + deltaSize;
				try {
					inputFile.seek(nextReadOffset);
				} catch (IOException e) {
					System.err.println("Unable to seek to specific offset.");
					e.printStackTrace();
				}
			}
			
			result = newResult;
		} while (zeroIndex == -1);
		
		return result;
	}
	
	public byte[] readBytesAtOffset(long offset, int numBytes) {
		if (inputFile == null) { return new byte[] {}; }
		
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
			if (inputFile != null) { inputFile.close(); }
		} finally {
			super.finalize();
		}
	}
}
