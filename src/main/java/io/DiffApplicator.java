package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import fedata.gba.fe6.FE6Data;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FindAndReplace;
import util.WhyDoesJavaNotHaveThese;

public class DiffApplicator {
	
	public static ArrayList<Diff> applyDiffs(DiffCompiler compiler, FileHandler handler, String outputPath) throws FileNotFoundException {
		ArrayList<Diff> failedDiffs = new ArrayList<>();
		
		long currentOffset = 0;
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		
		// Copy the entire file first.
		long lengthDiff = handler.getFileLength() - currentOffset;
		while (lengthDiff > 0) {
			int bytesToRead = (int) Math.min(1024, lengthDiff);
			byte[] batch = handler.readBytesAtOffset(currentOffset, bytesToRead);
			try {
				outputStream.write(batch);
			} catch (IOException e) {
				e.printStackTrace();
			}
			lengthDiff -= batch.length;
			currentOffset += batch.length;
		}
		
		// Now close the stream and re-open a random access writeable file.
		try {
			outputStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			outputStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		RandomAccessFile resultFile = new RandomAccessFile(outputPath, "rw");
		
		// Apply all the diffs.
		for (int i = 0; i < compiler.diffArray.size(); i++) {
			Diff currentDiff = compiler.diffArray.get(i);
			long nextAddress = currentDiff.address;
			int length = currentDiff.length;
			byte[] oldValue = currentDiff.requiredOldValues;
			byte[] newValue = currentDiff.changes;
			
			DebugPrinter.log(DebugPrinter.Key.DIFF, "Address: 0x" + Long.toHexString(nextAddress).toUpperCase() + " - Length: " + length + ", Old Value: " + 
					WhyDoesJavaNotHaveThese.displayStringForBytes(oldValue) + ", New Value: " + WhyDoesJavaNotHaveThese.displayStringForBytes(newValue));
			
			try {
				resultFile.seek(nextAddress);
				if (oldValue != null) {
					byte[] existingValue = new byte[length];
					resultFile.read(existingValue);
					if (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(existingValue, oldValue)) {
						assert false;
						failedDiffs.add(currentDiff);
						continue;
					}
				}
				resultFile.seek(nextAddress);
				resultFile.write(newValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Apply any find and replaces.
		for (int i = 0; i < compiler.replaceArray.size(); i++) {
			FindAndReplace replace = compiler.replaceArray.get(i);
			if (replace.oldBytes.length != replace.newBytes.length) { continue; }
			
			byte[] buffer = new byte[1024];
			
			try {
				resultFile.seek(0);
				long chunkStart = resultFile.getFilePointer();
				int bytesAvailable = resultFile.read(buffer);
				do {
					if (bytesAvailable == 0) { break; }
					
					for (int index = 0; index < bytesAvailable; index++) {
						if (buffer[index] == replace.oldBytes[0]) {
							boolean isMatch = true;
							for (int j = 1; j < replace.oldBytes.length; j++) {
								if (index + j >= bytesAvailable || replace.oldBytes[j] != buffer[index + j]) {
									isMatch = false;
									break;
								}
							}
							
							if (isMatch) {
								resultFile.seek(chunkStart + index);
								resultFile.write(replace.newBytes);
								index += replace.newBytes.length - 1;
							}
						}
					}
					
					resultFile.seek(chunkStart + bytesAvailable - replace.oldBytes.length);
					chunkStart = resultFile.getFilePointer();
					bytesAvailable = resultFile.read(buffer);
				} while (bytesAvailable > replace.oldBytes.length);
			} catch (IOException e1) {
				
			}
		}
		
		try {
			resultFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return failedDiffs;
	}
	
	public static Boolean applyDiff(String diffName, FileHandler handler, String outputPath) {
		try {
			long currentOffset = 0;
			FileOutputStream outputStream = new FileOutputStream(outputPath);
			
			InputStream stream = DiffApplicator.class.getClassLoader().getResourceAsStream(diffName + ".diff");;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			String currentLine = bufferedReader.readLine();
			while(currentLine != null) {
				Scanner scanner = new Scanner(currentLine);
				scanner.useDelimiter("[\\s\\W]+");
				long nextAddress = scanner.nextLong(16);
				int existingValue = scanner.nextInt(16);
				int newValue = scanner.nextInt(16);
				
				
				
				long lengthDiff = nextAddress - currentOffset;
				while (lengthDiff > 0) {
					int bytesToRead = (int) Math.min(1024, lengthDiff);
					byte[] batch = handler.readBytesAtOffset(currentOffset, bytesToRead);
					outputStream.write(batch);
					lengthDiff -= batch.length;
					currentOffset += batch.length;
				}
				
				byte[] byteToChange = handler.readBytesAtOffset(nextAddress, 1);
				if (byteToChange.length == 1) {
					if ((byteToChange[0] & 0xFF) == existingValue) {
						outputStream.write(newValue & 0xFF);
					} else {
						System.err.println("Value Mismatch detected at address 0x" + Long.toHexString(nextAddress).toUpperCase() + ". Expected: " + 
								Integer.toHexString(existingValue).toUpperCase() + ", but found " + Integer.toHexString(byteToChange[0]));	
					}
				} else {
					System.err.println("No Value Found at address 0x" + Long.toHexString(nextAddress).toUpperCase());
				}
				
				currentOffset += 1;
				
				scanner.close();
;				currentLine = bufferedReader.readLine();
			}
			
			long lengthDiff = handler.getFileLength() - currentOffset;
			while (lengthDiff > 0) {
				int bytesToRead = (int) Math.min(1024, lengthDiff);
				byte[] batch = handler.readBytesAtOffset(currentOffset, bytesToRead);
				outputStream.write(batch);
				lengthDiff -= batch.length;
				currentOffset += batch.length;
			}
			
			bufferedReader.close();
			outputStream.flush();
			outputStream.close();
			
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}

}
