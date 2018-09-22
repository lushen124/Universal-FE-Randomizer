package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.general.FEBase;
import io.FileHandler;

public class HuffmanHelper {
	
	private class Bitstream {
		private List<Integer> stream;
		
		public Bitstream() {
			stream = new ArrayList<Integer>();
		}
		
		public Bitstream(Bitstream previousStream) {
			stream = new ArrayList<Integer>(previousStream.stream);
		}
		
		public void pushZero() {
			stream.add(0);
		}
		
		public void pushOne() {
			stream.add(1);
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < stream.size(); i++) {
				sb.append(stream.get(i) == 0 ? '0' : '1');
			}
			
			return sb.toString();
		}
		
		public void appendStream(Bitstream otherStream) {
			stream.addAll(otherStream.stream);
		}
		
		public byte[] toByteArray() {
			byte[] byteArray = new byte[stream.size() / 8 + 1];
			byte currentByte = 0;
			int bit = 0;
			int arrayCounter = 0;
			for (int i = 0; i < stream.size(); i++) {
				if (bit == 8) {
					bit = 0;
					byteArray[arrayCounter++] = currentByte;
					currentByte = 0;
				}
				
				currentByte |= (stream.get(i) << bit);
				bit++;
			}
			byteArray[arrayCounter] = currentByte;
			
			return byteArray;
		}
		
		public Boolean hasSuffix(Bitstream suffix) {
			int streamBase = stream.size() - suffix.stream.size();
			if (streamBase < 0) { return false; }
			for (int i = 0; i < suffix.stream.size(); i++) {
				int index = streamBase + i;
				if (suffix.stream.get(i) != stream.get(index)) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	private class CacheEntry {
		public Bitstream stream;
		public Boolean hasValue1;
		public Boolean hasValue2;
		public byte value1;
		public byte value2;
		public Boolean isTerminal;
		public long nodeValue;
		
		@SuppressWarnings("unused")
		public CacheEntry parent;
		public CacheEntry left;
		public CacheEntry right;
		public CacheEntry value;
		
		public CacheEntry(CacheEntry previousEntry) {
			stream = new Bitstream(previousEntry.stream);
			hasValue1 = false;
			hasValue2 = false;
			value1 = 0;
			value2 = 0;
			nodeValue = 0;
			isTerminal = false;
			
			parent = previousEntry;
			left = null;
			right = null;
			value = null;
		}
		
		public CacheEntry() {
			stream = new Bitstream();
			hasValue1 = false;
			hasValue2 = false;
			value1 = 0;
			value2 = 0;
			nodeValue = 0;
			isTerminal = false;
			
			parent = null;
			left = null;
			right = null;
			value = null;
		}
		
		public String getValueString() {
			if (!hasValue1 && !hasValue2) { return null; }
			StringBuilder sb = new StringBuilder();
			if (hasValue1) {
				if (value1 >= 0x20 && value1 <= 0x7E) { sb.append((char)value1); }
				else { sb.append("0x" + Integer.toHexString(value1 & 0xFF)); }
			}
			if (hasValue2) {
				sb.append(" ");
				if (value2 >= 0x20 && value2 <= 0x7E) { sb.append((char)value2); }
				else { sb.append("0x" + Integer.toHexString(value2 & 0xFF)); }
			}
			return sb.toString();
		}
	}
	
	private class EncoderEntry {
		public Bitstream stream;
		
		public Map<Character, EncoderEntry> followups;
		
		public EncoderEntry(Bitstream stream) {
			if (stream != null) {
				this.stream = new Bitstream(stream);
			}
			
			followups = new HashMap<Character, EncoderEntry>();
		}
	}
	
	private FileHandler handler;
	private CacheEntry cacheRoot;
	
	private EncoderEntry[] encoder;
	private Boolean staleEncoder = true;
	private Bitstream terminatorBitstream;
	
	public HuffmanHelper(FileHandler handler) {
		this.handler = handler;
		cacheRoot = new CacheEntry();
	}
	
	public void buildEncoder() {
		if (!staleEncoder) { return; }
		encoder = new EncoderEntry[0x7F - 0x20];
		buildEncoderHelper(cacheRoot);
	}
	
	private void buildEncoderHelper(CacheEntry root) {
		if (root == null) { return; }
		buildEncoderHelper(root.left);
		if (root.value != null) {
			if (root.value.value1 >= 0x20 && root.value.value1 <= 0x7E) {
				int index = root.value.value1 - 0x20;
				EncoderEntry entry = encoder[index];
				if (entry == null) {
					if (root.value.hasValue2 && root.value.value2 >= 0x20 && root.value.value2 <= 0x7E) {
						entry = new EncoderEntry(null);
						encoder[index] = entry;
						entry.followups.put((char)root.value.value2, new EncoderEntry(root.value.stream));
					} else {
						entry = new EncoderEntry(root.value.stream);
						encoder[index] = entry;
					}
				} else {
					if (root.value.hasValue2 && root.value.value2 >= 0x20 && root.value.value2 <= 0x7E) {
						entry.followups.put((char)root.value.value2, new EncoderEntry(root.value.stream));
					} else {
						if (entry.stream == null) {
							entry.stream = new Bitstream(root.value.stream);
						}
					}
				}
			} else if (root.value.value1 == 0x0) {
				terminatorBitstream = root.value.stream;
			}
		}
		buildEncoderHelper(root.right);
	}
	
	public void printCache() {
		DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Printing Bitstream Cache:");
		printCacheHelper(cacheRoot);
	}
	
	private void printCacheHelper(CacheEntry root) {
		if (root == null) { return; }
		printCacheHelper(root.left);
		if (root.value != null && root.value.getValueString() != null) {
			DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Bitstream: " + root.stream.toString() + "\t\tValue: " + root.value.getValueString());
		}
		printCacheHelper(root.right);
	}

	public byte[] decodeTextAddressWithHuffmanTree(long textAddress, long treeAddress, long rootAddress) {
		byte[] result = new byte[0x1000];
		int i = 0;
		
		Boolean isMarked = (textAddress & 0x80000000) != 0;
		long maskedAddress = textAddress & 0x7FFFFFFF;
		if (isMarked) { maskedAddress -= 0x8000000; }
		
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
			staleEncoder = true;
			int bit = 0;
			long node = rootAddress;
			currentByte = handler.readBytesAtOffset(maskedAddress, 1)[0];
			long textDataPosition = maskedAddress + 1;
			CacheEntry currentEntry = cacheRoot;
			currentEntry.nodeValue = node;
			while (i < 0x1000) {
				CacheEntry leftEntry = currentEntry.left;
				CacheEntry rightEntry = currentEntry.right;
				
				if (bit == 8) {
					bit = 0;
					currentByte = handler.readBytesAtOffset(textDataPosition, 1)[0];
					textDataPosition += 1;
				}
				Boolean nextBitIsZero = (currentByte & 0x1) == 0;
				if (currentEntry.value != null) {
					result[i++] = currentEntry.value.value1;
					DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Wrote Byte 0x" + Integer.toHexString(currentEntry.value.value1));
					if (currentEntry.value.hasValue2) {
						result[i++] = currentEntry.value.value2;
						DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Wrote Byte 0x" + Integer.toHexString(currentEntry.value.value2));
					} else if (currentEntry.value.isTerminal) {
						DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Terminal");
						break;
					}
					node = rootAddress;
					currentEntry = cacheRoot;
					continue;
				}
				
				if (nextBitIsZero && leftEntry != null) {
					DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] 0 - left");
					currentEntry = leftEntry;
					node = leftEntry.nodeValue;
					currentByte >>= 1;
					bit += 1;
					DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] node = 0x" + Long.toHexString(node));
				} else if (!nextBitIsZero && rightEntry != null) {
					DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] 1 - right");
					currentEntry = rightEntry;
					node = rightEntry.nodeValue;
					currentByte >>= 1;
					bit += 1;
					DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] node = 0x" + Long.toHexString(node));
				} else {
					long currentTreeAddress = node;
					int left = FileReadHelper.readSignedHalfWord(handler, currentTreeAddress);
					currentTreeAddress += 2;
					int right = FileReadHelper.readSignedHalfWord(handler, currentTreeAddress);
					
					Boolean rightReachedLeafMask = right < 0;
					if (!rightReachedLeafMask) {
						int offset;
						if (nextBitIsZero) {
							offset = left;
							CacheEntry previousEntry = currentEntry;
							currentEntry = new CacheEntry(previousEntry);
							previousEntry.left = currentEntry;
							currentEntry.stream.pushZero();
							DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "0 - left");
						}
						else {
							offset = right;
							CacheEntry previousEntry = currentEntry;
							currentEntry = new CacheEntry(previousEntry);
							previousEntry.right = currentEntry;
							currentEntry.stream.pushOne();
							DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "1 - right");
						}
						
						node = treeAddress + (4 * offset);
						DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "node = 0x" + Long.toHexString(node));
						currentEntry.nodeValue = node;
						currentByte >>= 1;
						bit += 1;
					} else {
						node = rootAddress;
						result[i] = (byte) (left & 0xFF);
						
						CacheEntry valueEntry = new CacheEntry(currentEntry);
						currentEntry.value = valueEntry;
						
						valueEntry.hasValue1 = true;
						valueEntry.value1 = (byte) (left & 0xFF);
						DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Wrote Byte 0x" + Integer.toHexString(valueEntry.value1));
						i += 1;
						if ((left & 0xFF00) != 0) {
							if (i != 0x1000) {
								result[i] = (byte) ((left >> 8) & 0xFF);
								valueEntry.hasValue2 = true;
								valueEntry.value2 = (byte) ((left >> 8) & 0xFF);
								DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Wrote Byte 0x" + Integer.toHexString(valueEntry.value2));
								i += 1;
							}
						} else if (left == 0) {
							valueEntry.isTerminal = true;
							DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Terminal");
							break;
						}
						currentEntry = cacheRoot;
					}
				}
			}
		}
		
		return result;
	}
	
	public String sanitizeByteArrayIntoTextString(byte[] byteArray, Boolean squelchCodes, FEBase.GameType gameType) {
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
			} else if ((currentByte & 0xFF) == 0x80) {
				if (!squelchCodes) {
					sb.append("[0x80" + Integer.toHexString(byteArray[i + 1] & 0xFF) + "]");
				}
				i += 1;
			} else if ((currentByte & 0xFF) == 0x82) {
				// FE6 stuff.
				byte dataByte = byteArray[i + 1];
				sb.append(fe6CharacterFromByte(dataByte));
				i++;
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
	
	public byte[] encodeString(String string) {
		buildEncoder();
		Bitstream result = new Bitstream();
		
		for (int i = 0; i < string.length(); i++) {
			char character = string.charAt(i);
			int encoderIndex = character - 0x20;
			if (encoderIndex < encoder.length) {
				EncoderEntry entry = encoder[encoderIndex];
				if (i + 1 < string.length()) {
					char nextChar = string.charAt(i + 1);
					EncoderEntry followup = entry.followups.get(nextChar);
					if (followup != null) {
						result.appendStream(followup.stream);
						i += 1;
					} else {
						result.appendStream(entry.stream);
					}
				} else {
					result.appendStream(entry.stream);
				}
			}
		}
		
		if (!result.hasSuffix(terminatorBitstream)) {
			result.appendStream(terminatorBitstream);
		}
		
		return result.toByteArray();
	}
	
	public byte[] encodeNonHuffmanString(String string) {
		List<Byte> byteList = new ArrayList<Byte>();
		
		for (int i = 0; i < string.length(); i++) {
			char character = string.charAt(i);
			byteList.add((byte)0x82);
			byteList.add(fe6ByteFromCharacter(character));
		}
		
		// Terminate the string.
		byteList.add((byte)0);
		
		return WhyDoesJavaNotHaveThese.byteArrayFromByteList(byteList);
	}
	
	private char fe6CharacterFromByte(byte charByte) {
		switch (charByte) {
		case (byte)0x9F: return '2';
		case (byte)0xA0: return 'A';
		case (byte)0xA1: return '3';
		case (byte)0xA2: return 'B';
		case (byte)0xA3: return '4';
		case (byte)0xA4: return 'C';
		case (byte)0xA5: return '5';
		case (byte)0xA6: return 'D';
		case (byte)0xA7: return '6';
		case (byte)0xA8: return 'E';
		case (byte)0xA9: return 'F';
		case (byte)0xAA: return 'u';
		case (byte)0xAB: return 'G';
		case (byte)0xAC: return 'v';
		case (byte)0xAD: return 'H';
		case (byte)0xAE: return 'w';
		case (byte)0xAF: return 'I';
		case (byte)0xB0: return 'x';
		case (byte)0xB1: return 'J';
		case (byte)0xB2: return 'y';
		case (byte)0xB3: return 'K';
		case (byte)0xB4: return 'z';
		case (byte)0xB5: return 'L';
		// 0xB6
		case (byte)0xB7: return 'M';
		case (byte)0xB8: return ' ';
		case (byte)0xB9: return 'N';
		case (byte)0xBA: return '!';
		case (byte)0xBB: return 'O';
		case (byte)0xBC: return '"';
		case (byte)0xBD: return 'P';
		case (byte)0xBE: return '#';
		case (byte)0xBF: return 'Q';
		case (byte)0xC0: return '$';
		case (byte)0xC1: return '7';
		case (byte)0xC2: return 'R';
		case (byte)0xC3: return '%';
		case (byte)0xC4: return 'S';
		case (byte)0xC5: return '&';
		case (byte)0xC6: return 'T';
		case (byte)0xC7: return '\'';
		case (byte)0xC8: return 'U';
		case (byte)0xC9: return 'V';
		case (byte)0xCA: return 'W';
		case (byte)0xCB: return 'X';
		case (byte)0xCC: return 'Y';
		case (byte)0xCD: return 'Z';
		case (byte)0xCE: return '(';
		case (byte)0xCF: return '-';
		case (byte)0xD0: return 'a';
		case (byte)0xD1: return ')';
		case (byte)0xD2: return '.';
		case (byte)0xD3: return 'b';
		case (byte)0xD4: return '*';
		case (byte)0xD5: return '/';
		case (byte)0xD6: return 'c';
		case (byte)0xD7: return '+';
		case (byte)0xD8: return '0';
		case (byte)0xD9: return 'd';
		case (byte)0xDA: return ',';
		case (byte)0xDB: return '1';
		case (byte)0xDC: return 'e';
		case (byte)0xDD: return 'f';
		case (byte)0xDE: return 'g';
		case (byte)0xDF: return 'h';
		case (byte)0xE0: return 'i';
		case (byte)0xE1: return '8';
		case (byte)0xE2: return 'j';
		case (byte)0xE3: return '9';
		case (byte)0xE4: return 'k';
		case (byte)0xE5: return ':';
		case (byte)0xE6: return 'l';
		case (byte)0xE7: return 'm';
		case (byte)0xE8: return 'n';
		case (byte)0xE9: return 'o';
		case (byte)0xEA: return 'p';
		case (byte)0xEB: return 'q';
		// 0xEC
		case (byte)0xED: return 'r';
		// 0xEE
		// 0xEF
		case (byte)0xF0: return 's';
		case (byte)0xF1: return 't';
		default: return '?';
		}
	}
	
	private byte fe6ByteFromCharacter(char character) {
		switch (character) {
		case 'A': return (byte)0xA0;
		case 'B': return (byte)0xA2;
		case 'C': return (byte)0xA4;
		case 'D': return (byte)0xA6;
		case 'E': return (byte)0xA8;
		case 'F': return (byte)0xA9;
		case 'G': return (byte)0xAB;
		case 'H': return (byte)0xAD;
		case 'I': return (byte)0xAF;
		case 'J': return (byte)0xB1;
		case 'K': return (byte)0xB3;
		case 'L': return (byte)0xB5;
		case 'M': return (byte)0xB7;
		case 'N': return (byte)0xB9;
		case 'O': return (byte)0xBB;
		case 'P': return (byte)0xBD;
		case 'Q': return (byte)0xBF;
		case 'R': return (byte)0xC2;
		case 'S': return (byte)0xC4;
		case 'T': return (byte)0xC6;
		case 'U': return (byte)0xC8;
		case 'V': return (byte)0xC9;
		case 'W': return (byte)0xCA;
		case 'X': return (byte)0xCB;
		case 'Y': return (byte)0xCC;
		case 'Z': return (byte)0xCD;
		
		case 'a': return (byte)0xD0;
		case 'b': return (byte)0xD3;
		case 'c': return (byte)0xD6;
		case 'd': return (byte)0xD9;
		case 'e': return (byte)0xDC;
		case 'f': return (byte)0xDD;
		case 'g': return (byte)0xDE;
		case 'h': return (byte)0xDF;
		case 'i': return (byte)0xE0;
		case 'j': return (byte)0xE2;
		case 'k': return (byte)0xE4;
		case 'l': return (byte)0xE6;
		case 'm': return (byte)0xE7;
		case 'n': return (byte)0xE8;
		case 'o': return (byte)0xE9;
		case 'p': return (byte)0xEA;
		case 'q': return (byte)0xEB;
		case 'r': return (byte)0xED;
		case 's': return (byte)0xF0;
		case 't': return (byte)0xF1;
		case 'u': return (byte)0xAA;
		case 'v': return (byte)0xAC;
		case 'w': return (byte)0xAE;
		case 'x': return (byte)0xB0;
		case 'y': return (byte)0xB2;
		case 'z': return (byte)0xB4;
		
		case '0': return (byte)0xD8;
		case '1': return (byte)0xDB;
		case '2': return (byte)0x9F;
		case '3': return (byte)0xA1;
		case '4': return (byte)0xA3;
		case '5': return (byte)0xA5;
		case '6': return (byte)0xA7;
		case '7': return (byte)0xC1;
		case '8': return (byte)0xE1;
		case '9': return (byte)0xE3;
		
		case '"': return (byte)0xBC;
		case '\'': return (byte)0xC7;
		case '.': return (byte)0xD2;
		case ' ': return (byte)0xB8;
		case '!': return (byte)0xBA;
		case '#': return (byte)0xBE;
		case '$': return (byte)0xC0;
		case '%': return (byte)0xC3;
		case '&': return (byte)0xC5;
		case '(': return (byte)0xCE;
		case '-': return (byte)0xCF;
		case ')': return (byte)0xD1;
		case '*': return (byte)0xD4;
		case '/': return (byte)0xD5;
		case '+': return (byte)0xD7;
		case ',': return (byte)0xDA;
		case ':': return (byte)0xE5;
		
		default:
			System.err.println("Unencodable character detected.");
			return (byte)0xC0;
		}
	}
}
