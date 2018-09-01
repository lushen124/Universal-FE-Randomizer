package io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import util.DebugPrinter;
import util.FileReadHelper;
import util.WhyDoesJavaNotHaveThese;

public class UPSPatcher {
	
	public static Boolean applyUPSPatch(String patchFile, String sourceFile, String targetFile, UPSPatcherStatusListener listener) {
		try {
			InputStream stream = UPSPatcher.class.getClassLoader().getResourceAsStream(patchFile);
			Path temp = Files.createTempFile("file", "temp");
			Files.copy(stream, temp, StandardCopyOption.REPLACE_EXISTING);
			stream.close();
			
			if (listener != null) { listener.onMessageUpdate("Opening patch file..."); }
			FileHandler patchHandler = new FileHandler(temp.toString());
			if (listener != null) { listener.onMessageUpdate("Reading Magic number..."); }
			byte[] header = patchHandler.readBytesAtOffset(0, 4);
			if (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(header, new byte[] {0x55, 0x50, 0x53, 0x31})) {
				return false;
			}
			
			if (listener != null) { listener.onMessageUpdate("Reading input length..."); }
			long inputLength = readVariableWidthOffset(patchHandler);
			if (listener != null) { listener.onMessageUpdate("Reading output length..."); }
			long outputLength = readVariableWidthOffset(patchHandler);
			
			if (listener != null) { listener.onMessageUpdate("Loading CRC32s..."); }
			long oldReadOffset = patchHandler.getNextReadOffset();
			long sourceCRC = FileReadHelper.readWord(patchHandler, patchHandler.getFileLength() - 12, false);
			patchHandler.setNextReadOffset(oldReadOffset);
			
			if (listener != null) { listener.onMessageUpdate("Opening source file..."); }
			FileHandler sourceHandler = new FileHandler(sourceFile);
			if (listener != null) { listener.onMessageUpdate("Opening output stream..."); }
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			
			if (inputLength != sourceHandler.getFileLength()) {
				System.err.println("UPS patch failed. Input file length is incorrect.");
				outputStream.close();
				return false;
			}
			if (sourceCRC != sourceHandler.getCRC32()) {
				System.err.println("UPS patch failed. Input checksum is incorrect.");
				outputStream.close();
				return false;
			}
			
			DebugPrinter.log(DebugPrinter.Key.UPS, "Patching UPS file: " + patchFile);
			DebugPrinter.log(DebugPrinter.Key.UPS, "Input Length:  " + inputLength);
			DebugPrinter.log(DebugPrinter.Key.UPS, "Expected Result Length: " + outputLength);
			
			long bytesToSkip = 0;
			long lastWrittenOffset = 0;
			
			if (listener != null) { listener.onMessageUpdate("Patching..."); }
			
			while (patchHandler.getNextReadOffset() < patchHandler.getFileLength() - 12) {
				bytesToSkip = readVariableWidthOffset(patchHandler);
				DebugPrinter.log(DebugPrinter.Key.UPS, "Skipping " + bytesToSkip + " bytes");
				if (lastWrittenOffset + bytesToSkip > outputLength) { continue; }
				
				int sourceBytesLength = 0;
				if (lastWrittenOffset + 1 < inputLength) {
					byte[] sourceBytes = sourceHandler.readBytesAtOffset(lastWrittenOffset, (int)bytesToSkip);
					outputStream.write(sourceBytes);
					sourceBytesLength = sourceBytes.length;
					DebugPrinter.log(DebugPrinter.Key.UPS, "Read/copied " + sourceBytesLength + " bytes from the source.");
				}
				if (sourceBytesLength < bytesToSkip) {
					int difference = (int)bytesToSkip - sourceBytesLength;
					byte[] zeros = new byte[difference];
					for (int i = 0; i < difference; i++) {
						zeros[i] = 0;
					}
					outputStream.write(zeros);
					DebugPrinter.log(DebugPrinter.Key.UPS, "Filled in " + zeros.length + " bytes worth of 0.");
				}
				lastWrittenOffset += bytesToSkip;
				DebugPrinter.log(DebugPrinter.Key.UPS, "Starting diffs from offset 0x" + Long.toHexString(lastWrittenOffset).toUpperCase());
				
				byte[] delta = patchHandler.continueReadingBytesUpToNextTerminator((int)patchHandler.getFileLength() - 12);
				int deltaLength = delta.length;
				byte[] sourceBytes = lastWrittenOffset + 1 < inputLength ? sourceHandler.readBytesAtOffset(lastWrittenOffset, deltaLength) : new byte[] {};
				byte[] resultBytes = new byte[deltaLength];
				for (int i = 0; i < deltaLength; i++) {
					byte result = (byte)((delta[i] & 0xFF) ^ (i < sourceBytes.length ? (sourceBytes[i] & 0xFF) : 0));
					outputStream.write(result);
					resultBytes[i] = result;
					lastWrittenOffset++;
				}
				DebugPrinter.log(DebugPrinter.Key.UPS, "Wrote " + deltaLength + " bytes");
				DebugPrinter.log(DebugPrinter.Key.UPS, "Bytes written: " + WhyDoesJavaNotHaveThese.displayStringForBytes(resultBytes));
			}
			
			outputStream.close();
			
			long targetCRC = FileReadHelper.readWord(patchHandler, patchHandler.getFileLength() - 8, false);
			
			FileHandler resultHandler = new FileHandler(targetFile);
			long resultCRC = resultHandler.getCRC32();
			
			if (targetCRC != resultCRC || resultHandler.getFileLength() != outputLength) {
				System.err.println("Resulting checksum/file length is incorrect.");
				return false;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	private static long readVariableWidthOffset(FileHandler handler) {
//		long offset = 0;
//		long shift = 0;
//		
//		for(;;) {
//			byte currentByte = handler.continueReadingNextByte();
//			if ((currentByte & 0x80) != 0) {
//				offset += ((currentByte & 0x7F) << shift) & 0xFFFFFFFFL;
//				break;
//			}
//			offset += ((currentByte | 0x80) << shift) & 0xFFFFFFFFL;
//			shift += 7;
//		}
//		
//		return offset;
		
		long offset = 0;
		long shift = 1;
		
		for (;;) {
			byte currentByte = handler.continueReadingNextByte();
			offset += ((currentByte & 0x7F) * shift) & 0xFFFFFFFFL;
			if ((currentByte & 0x80) != 0) { break; }
			shift <<= 7;
			offset += shift;
		}
		
		return offset;
	}
}
