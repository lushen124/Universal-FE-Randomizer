package util;

import fedata.FEBase;
import io.FileHandler;

public class HuffmanHelper {

	public static byte[] decodeTextAddressWithHuffmanTree(FileHandler handler, long textAddress, long treeAddress, long rootAddress) {
		byte[] result = new byte[0x1000];
		int i = 0;
		
		Boolean isMarked = (textAddress & 0x80000000) != 0;
		long maskedAddress = textAddress & 0x7FFFFFFF;
		
		byte currentByte = 0;
		
		if (isMarked) {
			do {
				currentByte = handler.readBytesAtOffset(maskedAddress, 1)[0];
				result[i] = currentByte;
				i++;
				maskedAddress += 1;
			} while (currentByte != 0);
			
			return result;
		} else {
			int bit = 0;
			long node = rootAddress;
			currentByte = handler.readBytesAtOffset(maskedAddress, 1)[0];
			long textDataPosition = maskedAddress + 1;
			while (i < 0x1000) {
				long currentTreeAddress = node;
				int left = FileReadHelper.readSignedHalfWord(handler, currentTreeAddress);
				currentTreeAddress += 2;
				int right = FileReadHelper.readSignedHalfWord(handler, currentTreeAddress);
				
				Boolean rightReachedLeafMask = right < 0;
				if (rightReachedLeafMask) {
					node = rootAddress;
					result[i] = (byte) (left & 0xFF);
					i += 1;
					if ((left & 0xFF00) != 0) {
						if (i != 0x1000) {
							result[i] = (byte) ((left & 0xFF00) >> 8);
							i += 1;
						}
					} else if (left == 0) {
						break;
					}
				} else {
					if (bit == 8) {
						bit = 0;
						currentByte = handler.readBytesAtOffset(textDataPosition, 1)[0];
						textDataPosition += 1;
					}
					
					int offset;
					if ((currentByte & 0x1) == 0) {
						offset = left;
					}
					else {
						offset = right;
					}
					
					node = treeAddress + (4 * offset);
					currentByte >>= 1;
						bit += 1;
				}
			}
		}
		
		return result;
	}
	
	public static String sanitizeByteArrayIntoTextString(byte[] byteArray, Boolean squelchCodes, FEBase.GameType gameType) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < byteArray.length; i++) {
			byte currentByte = byteArray[i];
			
			if (currentByte == 0) {
				if (!squelchCodes) {
					sb.append("[X]");
				}
				break;
			} else if (currentByte == 1) {
				if (!squelchCodes) {
					sb.append(System.lineSeparator());
				}
			} else if (currentByte == 2) {
				if (!squelchCodes) {
					sb.append("[0x02]");
				}
			} else if (currentByte == 3) {
				if (!squelchCodes) {
					sb.append("[A]");
				}
			} else if (currentByte == 0x10) {
				if (i + 2 < byteArray.length && byteArray[i + 2] == 0xFF) {
					if (!squelchCodes) {
						sb.append("[LoadFace][0x" + Integer.toHexString(byteArray[i + 1] & 0xFF) + "][0xFF]");
					}
					i += 2;
				} else {
					if (!squelchCodes) {
						sb.append("[LoadFace][0x" + Integer.toHexString(byteArray[i + 1] & 0xFF) + "][0xFF]");
					}
					i += 2;
				}
			} else if (currentByte == '[') {
				if (!squelchCodes) {
					sb.append("[");
				}
			} else if (currentByte == 0x80) {
				if (!squelchCodes) {
					sb.append("[0x80" + Integer.toHexString(byteArray[i + 1] & 0xFF) + "]");
				}
				i += 1;
			} else if (currentByte == 0x82) {
				// FE6 stuff.
			} else {
				if (currentByte < 0x20) {
					if (!squelchCodes) {
						sb.append("[0x" + Integer.toHexString(currentByte & 0xFF) + "]");
					}
				} else {
					sb.append(Character.toString((char)currentByte));
				}
			}
		}
		
		return sb.toString();
	}
}
