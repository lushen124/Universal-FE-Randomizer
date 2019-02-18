package util;

import io.FileHandler;

public class LZ77 {
	
	public static byte[] decompress(FileHandler handler, long offset) {
		// Read the first 4 bytes. Byte 0 should be 1 for LZ77 compression.
		// Bytes 1-3 should be the size of the decompressed data.
		byte[] header = handler.readBytesAtOffset(offset, 4);
		if (header[0] != 0x10) { return null; }
		int size = (header[1] & 0xFF) | ((header[2] & 0xFF) << 8) | ((header[3] & 0xFF) << 16);
		
		byte[] output = new byte[size];
		
		int outputIndex = 0;
		
		while (outputIndex < size) {
			byte flag = handler.continueReadingNextByte();
			for (int i = 0; i < 8; i++) {
				if (outputIndex >= size) { break; }
				if ((flag & (1 << 7 - i)) == 0) {
					// The next byte is uncompressed. Read it into the output as is.
					byte uncompressedByte = handler.continueReadingNextByte();
					output[outputIndex++] = uncompressedByte;
				} else {
					// The next byte is compressed.
					// Bits 0 - 3 are the MSBs of the offset
					// Bits 4 - 7 are the number of bytes to copy (which we need to add 3 to for the actual number)
					// Bits 8 - 15 are the LSBs of the offset
					// The actual operation reads from the bytes written so far, less the offset in the compressed byte, and then minus another one.
					// For example: F0 1F -> 1FF0 (remember, little endian)
					// 0 - MSBs of the offset
					// F - Read 15 (+3) bytes
					// 1F - LSBs of the offset (31 + 1)
					// Means to go back 32 (31 + 1) bytes and read 18 bytes from what's been written so far.
					byte compressedByte = handler.continueReadingNextByte();
					byte compressedByte2 = handler.continueReadingNextByte();
					int jumpDistance = (((compressedByte & 0xF) << 16) | (compressedByte2 & 0xFF)) + 1;
					int bytesToCopy = ((compressedByte & 0xF0) >> 4) + 3;
					for (int j = 0; j < bytesToCopy; j++) {
						int sourceIndex = outputIndex - jumpDistance;
						output[outputIndex++] = output[sourceIndex];
					}
				}
			}
		}
		
		return output;
	}
	
	public static int compressedLength(FileHandler handler, long offset) {
		byte[] header = handler.readBytesAtOffset(offset, 4);
		if (header[0] != 0x10) { return 0; }
		int size = (header[1] & 0xFF) | ((header[2] & 0xFF) << 8) | ((header[3] & 0xFF) << 16);
		
		byte[] output = new byte[size];
		
		int outputIndex = 0;
		
		int bytesRead = 4;
		
		while (outputIndex < size) {
			byte flag = handler.continueReadingNextByte();
			bytesRead++;
			for (int i = 0; i < 8; i++) {
				if (outputIndex >= size) { break; }
				if ((flag & (1 << 7 - i)) == 0) {
					// The next byte is uncompressed. Read it into the output as is.
					byte uncompressedByte = handler.continueReadingNextByte();
					bytesRead++;
					output[outputIndex++] = uncompressedByte;
				} else {
					// The next byte is compressed.
					// Bits 0 - 3 are the MSBs of the offset
					// Bits 4 - 7 are the number of bytes to copy (which we need to add 3 to for the actual number)
					// Bits 8 - 15 are the LSBs of the offset
					// The actual operation reads from the bytes written so far, less the offset in the compressed byte, and then minus another one.
					// For example: F0 1F -> 1FF0 (remember, little endian)
					// 0 - MSBs of the offset
					// F - Read 15 (+3) bytes
					// 1F - LSBs of the offset (31 + 1)
					// Means to go back 32 (31 + 1) bytes and read 18 bytes from what's been written so far.
					byte compressedByte = handler.continueReadingNextByte();
					bytesRead++;
					byte compressedByte2 = handler.continueReadingNextByte();
					bytesRead++;
					int jumpDistance = (((compressedByte & 0xF) << 16) | (compressedByte2 & 0xFF)) + 1;
					int bytesToCopy = ((compressedByte & 0xF0) >> 4) + 3;
					for (int j = 0; j < bytesToCopy; j++) {
						int sourceIndex = outputIndex - jumpDistance;
						output[outputIndex++] = output[sourceIndex];
					}
				}
			}
		}
		// These have to be byte aligned, so they should be divisible by 4. Round up if it's not.
		return bytesRead + (bytesRead % 4 == 0 ? 0 : 4 - (bytesRead % 4));
	}
	
	public static byte[] decompress(byte[] inputBytes) {
		byte[] header = new byte[4];
		WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(inputBytes, header, 0, 4);
		if (header[0] != 0x10) { return null; }
		int size = (header[1] & 0xFF) | ((header[2] & 0xFF) << 8) | ((header[3] & 0xFF) << 16);
		
		byte[] output = new byte[size];
		
		int outputIndex = 0;
		int inputIndex = 4;
		
		while (outputIndex < size) {
			byte flag = inputBytes[inputIndex++];
			for (int i = 0; i < 8; i++) {
				if (outputIndex >= size) { break; }
				if ((flag & (1 << 7 - i)) == 0) {
					byte uncompressedByte = inputBytes[inputIndex++];
					output[outputIndex++] = uncompressedByte;
				} else {
					byte compressedByte = inputBytes[inputIndex++];
					byte compressedByte2 = inputBytes[inputIndex++];
					int jumpDistance = (((compressedByte & 0xF) << 16) | (compressedByte2 & 0xFF)) + 1;
					int bytesToCopy = ((compressedByte & 0xF0) >> 4) + 3;
					for (int j = 0; j < bytesToCopy; j++) {
						int sourceIndex = outputIndex - jumpDistance;
						output[outputIndex++] = output[sourceIndex];
					}
				}	
			}
		}
		
		return output;
	}
	
	public static byte[] compress(byte[] decompressed) {
		if (decompressed == null) { return null; }
		
		byte[] header = new byte[4];
		header[0] = (byte)0x10; // Header to mark an LZ77 compressed block of data.
		int size = decompressed.length;
		header[1] = (byte)(size & 0xFF);
		header[2] = (byte)((size >> 8) & 0xFF);
		header[3] = (byte)((size >> 16) & 0xFF);
		
		int inputOffset = 0;
		
		byte[] compressedData = new byte[255];
		int outputOffset = 0;
		
		WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(header, compressedData, outputOffset, header.length);
		outputOffset += header.length;
		
		while (inputOffset < size) {
			byte[] currentBlock = new byte[16]; // At most we can have 16 bytes (uncompressed blocks are 1 byte, compressed blocks are 2 bytes)
			int blockIndex = 0;
			byte flag = 0;
			
			for (int i = 0; i < 8; i++) {
				if (inputOffset >= decompressed.length) { break; }
				byte currentInputByte = decompressed[inputOffset];
				boolean patternMatched = false;
				int index = 0; // This will keep track of the location of the first byte that matched.
				// I think the window can only look back 32 bytes at most, so we should lock it there.
				for (index = Math.max(0, inputOffset - 32); index < inputOffset; index++) {
					if (decompressed[index] == currentInputByte) {
						patternMatched = true;
						break; 
					}
				}
				
				if (patternMatched) {
					// We need three matching bytes at a minimum to use compression.
					byte[] matchedBytes = new byte[18]; // GBA can only pull 18 matching bytes at most.
					int matchedArrayIndex = 0;
					matchedBytes[matchedArrayIndex++] = currentInputByte;
					for (int j = 1; j < 18; j++) {
						if (inputOffset + j >= decompressed.length) { break; }
						if (decompressed[inputOffset + j] == decompressed[index + j]) {
							matchedBytes[matchedArrayIndex++] = decompressed[inputOffset + j];
						} else {
							break;
						}
					}
					
					// We can't write more than we have space for in the current set of blocks.
					int writtenLength = matchedArrayIndex;
					
					if (writtenLength < 3) {
						// This match is too small to compress. Write the bytes as uncompressed. Only write as many as can fit.
						writtenLength = Math.min(matchedArrayIndex, 8 - i);
						for (int j = 0; j < writtenLength; j++) {
							currentBlock[blockIndex++] = matchedBytes[j];
						}
						i += (writtenLength - 1);
					} else {
						// We can compress this.
						int offset = inputOffset - index - 1;
						int numBytes = writtenLength;
						byte compressed = (byte)((offset >> 16) & 0xFF);
						compressed |= (((numBytes - 3) & 0xF) << 4);
						byte lsb = (byte)(offset & 0xFF);
						currentBlock[blockIndex++] = compressed;
						currentBlock[blockIndex++] = lsb;
						int mask = 1 << (7 - i);
						flag |= mask; // Mark block as compressed.
					}
					
					inputOffset += writtenLength;
				} else {
					// This byte is new and needs to be stored uncompressed.
					currentBlock[blockIndex++] = currentInputByte;
					inputOffset++;
				}
			}
			
			compressedData[outputOffset++] = flag;
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(currentBlock, compressedData, outputOffset, blockIndex);
			outputOffset += blockIndex;
		}
		
		int outputSize = outputOffset;
		int paddingNecessary = 4 - (outputOffset % 4);
		if (paddingNecessary % 4 != 0) {
			outputSize += paddingNecessary;
		}
		
		byte[] truncated = new byte[outputSize];
		WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(compressedData, truncated, 0, outputOffset);
		
		return truncated;
	}

}
