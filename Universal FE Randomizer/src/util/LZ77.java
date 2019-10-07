package util;

import java.util.Arrays;

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
					int jumpDistance = (((compressedByte & 0xF) << 8) | (compressedByte2 & 0xFF)) + 1;
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
					int jumpDistance = (((compressedByte & 0xF) << 8) | (compressedByte2 & 0xFF)) + 1;
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
					int jumpDistance = (((compressedByte & 0xF) << 8) | (compressedByte2 & 0xFF)) + 1;
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
		return compress(decompressed, 32);
	}
	
	public static byte[] compress(byte[] decompressed, int windowSize) {
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
				int index = 0; // This will keep track of the location of the first byte that matched.
				// Window size seems to be variable, depending on the platform.
				// GBA seems to be ok with 32, but I've seen GCN go higher. The maximum is 0xFFF, since we have
				// 12 total bits that could represent the offset.
				int longestMatchingIndex = -1;
				int longestMatchLength = 0;
				int longestSequence = 0;
				
				for (index = Math.max(0, inputOffset - windowSize); index < inputOffset; index++) {
					if (decompressed[index] == currentInputByte) {
						int matchingLength = 0;
						byte[] potentialSequence = Arrays.copyOfRange(decompressed, index, inputOffset);
						while (inputOffset + matchingLength < decompressed.length &&
								potentialSequence[matchingLength % potentialSequence.length] == decompressed[inputOffset + matchingLength]) {
							matchingLength++;
						}
						byte[] actualSequence = Arrays.copyOfRange(potentialSequence, 0, Math.min(matchingLength, potentialSequence.length));
						if (matchingLength > longestMatchLength || (matchingLength == longestMatchLength && actualSequence.length >= longestSequence)) {
							longestMatchingIndex = index;
							longestMatchLength = matchingLength;
							longestSequence = actualSequence.length;
						}
					}
				}
				
				// We want to avoid having offsets of 0.
				int offset = inputOffset - longestMatchingIndex - 1;
				
				// We can only compress if we have a match, and that match is longer than 3 bytes.
				if (longestMatchingIndex != -1 && longestMatchLength >= 3 && offset > 0) {
					index = longestMatchingIndex;
					byte[] sequence = Arrays.copyOfRange(decompressed, index, inputOffset);
					// We need three matching bytes at a minimum to use compression.
					byte[] matchedBytes = new byte[18]; // GBA can only pull 18 matching bytes at most.
					int matchedArrayIndex = 0;
					matchedBytes[matchedArrayIndex++] = currentInputByte;
					for (int j = 1; j < 18; j++) {
						if (inputOffset + j >= decompressed.length) { break; }
						if (decompressed[inputOffset + j] == sequence[j % sequence.length]) {
							matchedBytes[matchedArrayIndex++] = sequence[j % sequence.length];
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
			
			if (compressedData.length <= outputOffset) {
				byte[] expandedData = Arrays.copyOf(compressedData, compressedData.length * 2);
				compressedData = expandedData;
			}
			compressedData[outputOffset++] = flag;
			
			while (compressedData.length < outputOffset + blockIndex) {
				byte[] expandedData = Arrays.copyOf(compressedData, compressedData.length * 2);
				compressedData = expandedData;
			}
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
