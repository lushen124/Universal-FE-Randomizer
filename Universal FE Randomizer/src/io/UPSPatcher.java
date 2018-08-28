package io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import util.WhyDoesJavaNotHaveThese;

public class UPSPatcher {
	
	public static Boolean applyUPSPatch(File patchFile, String sourceFile, String targetFile) {
		try {
			FileHandler patchHandler = new FileHandler(patchFile);
			byte[] header = patchHandler.readBytesAtOffset(0, 4);
			if (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(header, new byte[] {0x55, 0x50, 0x53, 0x31})) {
				return false;
			}
			
			long inputLength = readVariableWidthOffset(patchHandler, 4);
			long outputLength = readVariableWidthOffset(patchHandler, patchHandler.getNextReadOffset());
			
			FileHandler sourceHandler = new FileHandler(sourceFile);
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			
			long relative = 0;
			long lastWrittenOffset = 0;
			
			while (patchHandler.getNextReadOffset() < patchHandler.getFileLength() - 12) {
				relative = relative + readVariableWidthOffset(patchHandler, patchHandler.getNextReadOffset());
				if (relative > outputLength) { continue; }
				
				outputStream.write(sourceHandler.readBytesAtOffset(lastWrittenOffset, (int)relative));
				lastWrittenOffset += relative;
				for (long i = relative; i < outputLength - 1; i++) { // This is to make sure we don't write beyond what the output length should be. The index isn't that useful.
					byte delta = patchHandler.readBytesAtOffset(patchHandler.getNextReadOffset(), 1)[0];
					long currentRelative = relative;
					relative++;
					if (delta == 0) { break; }
					if (i < outputLength) {
						byte sourceByte = 0;
						if (i < inputLength && currentRelative < sourceHandler.getFileLength()) {
							sourceByte = sourceHandler.readBytesAtOffset(currentRelative, 1)[0];
						}
						
						outputStream.write(delta ^ sourceByte);
					}
				}
			}
			
			outputStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	private static long readVariableWidthOffset(FileHandler handler, long filePosition) {
		long offset = 0;
		long shift = 1;
		
		long currentPosition = filePosition;
		for(;;) {
			byte currentByte = handler.readBytesAtOffset(currentPosition++, 1)[0];
			offset += (currentByte & 0x7F) * shift;
			if ((currentByte & 0x80) != 0) { break; }
			shift <<= 7;
			offset += shift;
		}
		
		return offset;
	}
}
