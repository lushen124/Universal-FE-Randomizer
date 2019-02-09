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
		encoder = new EncoderEntry[0x100];
		buildEncoderHelper(cacheRoot);
	}
	
	private void buildEncoderHelper(CacheEntry root) {
		if (root == null) { return; }
		buildEncoderHelper(root.left);
		if (root.value != null) {
			if (root.value.hasValue1) {
				int index = (root.value.value1 & 0xFF);
				EncoderEntry entry = encoder[index];
				if (entry == null) {
					if (root.value.hasValue2) {
						entry = new EncoderEntry(null);
						encoder[index] = entry;
						entry.followups.put((char)(root.value.value2 & 0xFF), new EncoderEntry(root.value.stream));
					} else {
						entry = new EncoderEntry(root.value.stream);
						encoder[index] = entry;
						DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Writing stream " + root.value.stream.toString() + " to encoder index " + index);
					}
				} else {
					if (root.value.hasValue2) {
						entry.followups.put((char)(root.value.value2 & 0xFF), new EncoderEntry(root.value.stream));
					} else {
						if (entry.stream == null) {
							entry.stream = new Bitstream(root.value.stream);
							DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Writing stream " + root.value.stream.toString() + " to encoder index " + index);
						}
					}
				}
			}
			
			if (root.value.value1 == 0x0) {
				terminatorBitstream = root.value.stream;
			}
		} else {
			if (root.hasValue1 || root.hasValue2) {
				DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Null Root has Value!");
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
	
	public byte[] decodeDataArray(byte[] data, long treeAddress, long rootAddress, boolean isMarked) {
		byte[] result = new byte[0x1000];
		int i = 0;
		
		int currentDataIndex = 0;
		byte currentByte = 0;
		
		if (isMarked) {
			do {
				currentByte = data[i];
				result[i] = currentByte;
				i++;
			} while (currentByte != 0);
			
			return result;
		} else {
			int bit = 0;
			long node = rootAddress;
			currentByte = data[currentDataIndex++];
			CacheEntry currentEntry = cacheRoot;
			currentEntry.nodeValue = node;
			while (i < 0x1000) {
				CacheEntry leftEntry = currentEntry.left;
				CacheEntry rightEntry = currentEntry.right;
				
				if (bit == 8) {
					bit = 0;
					currentByte = data[currentDataIndex++];
				}
				Boolean nextBitIsZero = (currentByte & 0x1) == 0;
				if (currentEntry.value != null) {
					result[i++] = currentEntry.value.value1;
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Wrote Byte 0x" + Integer.toHexString(currentEntry.value.value1));
					if (currentEntry.value.hasValue2) {
						result[i++] = currentEntry.value.value2;
						//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Wrote Byte 0x" + Integer.toHexString(currentEntry.value.value2));
					} else if (currentEntry.value.isTerminal) {
						//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Terminal");
						break;
					}
					node = rootAddress;
					currentEntry = cacheRoot;
					continue;
				}
				
				if (nextBitIsZero && leftEntry != null) {
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] 0 - left");
					currentEntry = leftEntry;
					node = leftEntry.nodeValue;
					currentByte >>= 1;
					bit += 1;
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] node = 0x" + Long.toHexString(node));
				} else if (!nextBitIsZero && rightEntry != null) {
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] 1 - right");
					currentEntry = rightEntry;
					node = rightEntry.nodeValue;
					currentByte >>= 1;
					bit += 1;
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] node = 0x" + Long.toHexString(node));
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
							//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "0 - left");
						}
						else {
							offset = right;
							CacheEntry previousEntry = currentEntry;
							currentEntry = new CacheEntry(previousEntry);
							previousEntry.right = currentEntry;
							currentEntry.stream.pushOne();
							//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "1 - right");
						}
						
						node = treeAddress + (4 * offset);
						//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "node = 0x" + Long.toHexString(node));
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
						//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Wrote Byte 0x" + Integer.toHexString(valueEntry.value1));
						i += 1;
						if ((left & 0xFF00) != 0) {
							if (i != 0x1000) {
								result[i] = (byte) ((left >> 8) & 0xFF);
								valueEntry.hasValue2 = true;
								valueEntry.value2 = (byte) ((left >> 8) & 0xFF);
								//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Wrote Byte 0x" + Integer.toHexString(valueEntry.value2));
								i += 1;
							}
						} else if (left == 0) {
							valueEntry.isTerminal = true;
							//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Terminal");
							break;
						}
						currentEntry = cacheRoot;
					}
				}
			}
			
			return result;
		}
	}

	public byte[] decodeTextAddressWithHuffmanTree(long textAddress, long treeAddress, long rootAddress) {
		byte[] result = new byte[0x1000];
		int i = 0;
		
		Boolean isMarked = (textAddress & 0x80000000) != 0;
		long maskedAddress = textAddress & 0x7FFFFFFF;
		if (maskedAddress > 0x08000000) { maskedAddress -= 0x8000000; }
		
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
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Wrote Byte 0x" + Integer.toHexString(currentEntry.value.value1));
					if (currentEntry.value.hasValue2) {
						result[i++] = currentEntry.value.value2;
						//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Wrote Byte 0x" + Integer.toHexString(currentEntry.value.value2));
					} else if (currentEntry.value.isTerminal) {
						//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] Terminal");
						break;
					}
					node = rootAddress;
					currentEntry = cacheRoot;
					continue;
				}
				
				if (nextBitIsZero && leftEntry != null) {
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] 0 - left");
					currentEntry = leftEntry;
					node = leftEntry.nodeValue;
					currentByte >>= 1;
					bit += 1;
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] node = 0x" + Long.toHexString(node));
				} else if (!nextBitIsZero && rightEntry != null) {
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] 1 - right");
					currentEntry = rightEntry;
					node = rightEntry.nodeValue;
					currentByte >>= 1;
					bit += 1;
					//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "[cache] node = 0x" + Long.toHexString(node));
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
							//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "0 - left");
						}
						else {
							offset = right;
							CacheEntry previousEntry = currentEntry;
							currentEntry = new CacheEntry(previousEntry);
							previousEntry.right = currentEntry;
							currentEntry.stream.pushOne();
							//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "1 - right");
						}
						
						node = treeAddress + (4 * offset);
						//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "node = 0x" + Long.toHexString(node));
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
						//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Wrote Byte 0x" + Integer.toHexString(valueEntry.value1));
						i += 1;
						if ((left & 0xFF00) != 0) {
							if (i != 0x1000) {
								result[i] = (byte) ((left >> 8) & 0xFF);
								valueEntry.hasValue2 = true;
								valueEntry.value2 = (byte) ((left >> 8) & 0xFF);
								//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Wrote Byte 0x" + Integer.toHexString(valueEntry.value2));
								i += 1;
							}
						} else if (left == 0) {
							valueEntry.isTerminal = true;
							//DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Terminal");
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
			}/* else if (currentByte == 1) {
				if (!squelchCodes) {
					sb.append(System.lineSeparator());
				}
			} else if (currentByte == 2) {
				if (!squelchCodes) {
					sb.append(System.lineSeparator());
					sb.append(System.lineSeparator());
				}
			}*/ else if (currentByte == 3) {
				if (!squelchCodes) {
					sb.append("[A]");
				}
			} else if (currentByte >= 4 && currentByte <= 7) {
				if (!squelchCodes) {
					sb.append('[');
					for (int j = 0; j < currentByte; j++) {
						sb.append('.');
					}
					sb.append(']');
				}
			} else if (currentByte == 8) {
				if (!squelchCodes) {
					sb.append("[OpenFarLeft]");
				}
			} else if (currentByte == 9) {
				if (!squelchCodes) {
					sb.append("[OpenMidLeft]");
				}
			} else if (currentByte == 0xA) {
				if (!squelchCodes) {
					sb.append("[OpenLeft]");
				}
			} else if (currentByte == 0xB) {
				if (!squelchCodes) {
					sb.append("[OpenRight]");
				}
			} else if (currentByte == 0xC) {
				if (!squelchCodes) {
					sb.append("[OpenMidRight]");
				}
			} else if (currentByte == 0xD) {
				if (!squelchCodes) {
					sb.append("[OpenFarRight]");
				}
			} else if (currentByte == 0xE) {
				if (!squelchCodes) {
					sb.append("[OpenFarFarLeft]");
				}
			} else if (currentByte == 0xF) {
				if (!squelchCodes) {
					sb.append("[OpenFarFarRight]");
				}
			} else if (currentByte == 0x10) {
				if (i + 2 < byteArray.length && byteArray[i + 2] == 0xFF) {
					if (!squelchCodes) {
						sb.append("[LoadFace][0x" + Integer.toHexString(byteArray[i + 1] & 0xFF) + "][0xFF]");
					}
					i += 2;
				} else if (i + 2 < byteArray.length) {
					if (!squelchCodes) {
						sb.append("[LoadFace][0x" + Integer.toHexString(byteArray[i + 1] & 0xFF) + "][0x" + Integer.toHexString(byteArray[i + 2] & 0xFF) + "]");
					}
					i += 2;
				}
			} else if (currentByte == 0x11) {
				if (!squelchCodes) {
					sb.append("[ClearFace]");
				}
			} else if (currentByte == '[') {
				if (!squelchCodes) {
					sb.append("[");
				}
			} else if ((currentByte & 0xFF) == 0x80) {
				if (!squelchCodes) {
					sb.append("[0x80][0x" + Integer.toHexString(byteArray[i + 1] & 0xFF) + "]");
				}
				i += 1;
			} else if ((currentByte & 0xFF) == 0x82) {
				// FE6 stuff.
				byte dataByte = byteArray[i + 1];
				sb.append(fe6CharacterFrom82Byte(dataByte));
				i++;
			} else if ((currentByte & 0xFF) == 0x83) {
				// FE6 Stuff.
				byte dataByte = byteArray[i + 1];
				sb.append(fe6CharacterFrom83Byte(dataByte));
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
	
	private static class StringByteProvider {
		int currentIndex;
		List<Byte> byteArray = new ArrayList<Byte>();
		
		private static Integer encoderIndexFromCodeString(String controlCode) {
			if (controlCode.equals("X")) { return 0; } 
			else if (controlCode.equals("A")) { return 3; } 
			else if (controlCode.equals("....")) { return 4; } 
			else if (controlCode.equals(".....")) { return 5; } 
			else if (controlCode.equals("......")) { return 6; } 
			else if (controlCode.equals(".......")) { return 7; } 
			else if (controlCode.equals("OpenFarLeft")) { return 8; }
			else if (controlCode.equals("OpenMidLeft")) { return 9; }
			else if (controlCode.equals("OpenLeft")) { return 0xA; }
			else if (controlCode.equals("OpenRight")) { return 0xB; }
			else if (controlCode.equals("OpenMidRight")) { return 0xC; }
			else if (controlCode.equals("OpenFarRight")) { return 0xD; }
			else if (controlCode.equals("OpenFarFarLeft")) { return 0xE; }
			else if (controlCode.equals("OpenFarFarRight")) { return 0xF; }
			else if (controlCode.equals("LoadFace")) { return 0x10; }
			else if (controlCode.equals("ClearFace")) { return 0x11; }
			else if (controlCode.startsWith("0x")) {
				String hexString = controlCode.substring(2);
				return Integer.parseUnsignedInt(hexString, 16);
			}
			
			return null;
		}
		
		private StringByteProvider(String string, boolean includesCodes) {
			currentIndex = 0;
			for (int i = 0; i < string.length(); i++) {
				char character = string.charAt(i);
				Integer encoderIndex = (int)character;
				
				/*if (character == '\n') {
					encoderIndex = 1;
					if (i + 1 < string.length() && string.charAt(i + 1) == '\n') {
						encoderIndex = 2;
						i += 1;
					}
				} else if (character == '\r') {
					encoderIndex = 1;
					if (i + 1 < string.length() && string.charAt(i + 1) == '\n') {
						if (i + 3 < string.length() && string.charAt(i + 2) == '\r' && string.charAt(i + 3) == '\n') {
							encoderIndex = 2;
							i += 3;
						} else {
							encoderIndex = 1;
							i += 1;
						}
					} else if (i + 1 < string.length() && string.charAt(i + 1) == '\r') {
						encoderIndex = 2;
						i += 1;
					}
				}*/
				
				if (includesCodes && character == '[') {
					StringBuilder sb = new StringBuilder();
					int j = i + 1;
					for ( ; j < string.length(); j++) {
						char nextChar = string.charAt(j);
						if (nextChar == ']') { break; }
						else {
							sb.append(nextChar);
						}
					}
					i = j;
					String controlCode = sb.toString();
					encoderIndex = encoderIndexFromCodeString(controlCode);
					
					if (encoderIndex == null) {
						System.err.println("Unsupported control code: " + controlCode);
						continue;
					}
				}
				
				byteArray.add((byte)(encoderIndex & 0xFF));
			}
		}
		
		private Byte getCurrent() {
			if (currentIndex >= byteArray.size()) { return null; }
			return byteArray.get(currentIndex);
		}
		
		private Byte peekNext() {
			if (currentIndex + 1 >= byteArray.size()) { return null; }
			return byteArray.get(currentIndex + 1);
		}
		
		private void advance() {
			currentIndex += 1;
		}
		
		private boolean hasData() {
			return currentIndex < byteArray.size();
		}
	}
	
	public byte[] encodeString(String string) {
		return encodeString(string, false);
	}
	
	public byte[] encodeString(String string, boolean includesCodes) {
		buildEncoder();
		Bitstream result = new Bitstream();
		
		StringByteProvider provider = new StringByteProvider(string, includesCodes);
		
		while (provider.hasData()) {
			Character encoderIndex = (char)((int)provider.getCurrent() & 0xFF);
			
			if (encoderIndex < encoder.length) {
				EncoderEntry entry = encoder[encoderIndex];
				Character nextChar = provider.peekNext() != null ? (char)((int)provider.peekNext() & 0xFF) : null;
				
				if (nextChar == null) {
					result.appendStream(entry.stream);
					break;
				}
				
				EncoderEntry followup = entry.followups.get(nextChar);
				if (followup != null) {
					result.appendStream(followup.stream);
					provider.advance();
				} else {
					// ?[A] is apparently not a valid combo in the huffman table... (for FE7)
					// Maybe we can use a space instead in this case.
					// Applies to ,[.....] too. These all have an extra space as an option.
					// Geitz has issues with posessive form (i.e. Geitz's) since z' isn't a valid combination.
					// Letters with colons after them might also cause issues. (i.e. Nino:)
					if (entry.stream == null) {
						// Try the short pause.
						if (entry.followups.get((char)(0x1f)) != null) {
							result.appendStream(entry.followups.get((char)(0x1f)).stream);
						}
						// Try a space.
						else if (entry.followups.get(' ') != null) {
							result.appendStream(entry.followups.get(' ').stream);
						}
					} else {
						result.appendStream(entry.stream);
					}
				}
			}
			
			provider.advance();
		}
		
		if (!result.hasSuffix(terminatorBitstream)) {
			result.appendStream(terminatorBitstream);
		}
		
		return result.toByteArray();
	}
	
	public byte[] encodeNonHuffmanString(String string) {
		return encodeNonHuffmanString(string, false);
	}
	
	public byte[] encodeNonHuffmanString(String string, boolean includesCodes) {
		List<Byte> byteList = new ArrayList<Byte>();
		
		for (int i = 0; i < string.length(); i++) {
			char character = string.charAt(i);
			
			Integer encoderIndex = null;
			
			/*if (character == '\n') {
				encoderIndex = 1;
				if (i + 1 < string.length() && string.charAt(i + 1) == '\n') {
					encoderIndex = 2;
					i += 1;
				}
			} else if (character == '\r') {
				encoderIndex = 1;
				if (i + 1 < string.length() && string.charAt(i + 1) == '\n') {
					if (i + 3 < string.length() && string.charAt(i + 2) == '\r' && string.charAt(i + 3) == '\n') {
						encoderIndex = 2;
						i += 3;
					} else {
						encoderIndex = 1;
						i += 1;
					}
				} else if (i + 1 < string.length() && string.charAt(i + 1) == '\r') {
					encoderIndex = 2;
					i += 1;
				}
			}
			
			if (encoderIndex != null) {
				byteList.add((byte)(encoderIndex & 0xFF));
				continue;
			}*/
			
			if (includesCodes && character == '[') {
				
				StringBuilder sb = new StringBuilder();
				int j = i + 1;
				for ( ; j < string.length(); j++) {
					char nextChar = string.charAt(j);
					if (nextChar == ']') { break; }
					else {
						sb.append(nextChar);
					}
				}
				i = j;
				String controlCode = sb.toString();
				if (controlCode.equals("X")) { encoderIndex = 0; } 
				else if (controlCode.equals("A")) { encoderIndex = 3; } 
				else if (controlCode.equals("....")) { encoderIndex = 4; } 
				else if (controlCode.equals(".....")) { encoderIndex = 5; } 
				else if (controlCode.equals("......")) { encoderIndex = 6; } 
				else if (controlCode.equals(".......")) { encoderIndex = 7; } 
				else if (controlCode.equals("OpenFarLeft")) { encoderIndex = 8; }
				else if (controlCode.equals("OpenMidLeft")) { encoderIndex = 9; }
				else if (controlCode.equals("OpenLeft")) { encoderIndex = 0xA; }
				else if (controlCode.equals("OpenRight")) { encoderIndex = 0xB; }
				else if (controlCode.equals("OpenMidRight")) { encoderIndex = 0xC; }
				else if (controlCode.equals("OpenFarRight")) { encoderIndex = 0xD; }
				else if (controlCode.equals("OpenFarFarLeft")) { encoderIndex = 0xE; }
				else if (controlCode.equals("OpenFarFarRight")) { encoderIndex = 0xF; }
				else if (controlCode.equals("LoadFace")) { encoderIndex = 0x10; }
				else if (controlCode.equals("ClearFace")) { encoderIndex = 0x11; }
				else if (controlCode.startsWith("0x")) {
					String hexString = controlCode.substring(2);
					encoderIndex = Integer.parseUnsignedInt(hexString, 16);
				} else {
					System.err.println("Unsupported control code: " + controlCode);
					continue;
				}
				
				if (encoderIndex != null) {
					byteList.add((byte)(encoderIndex & 0xFF));
					continue;
				}
			}
			
			byte prefix = fe6PrefixByteForCharacter(character);
			byteList.add(prefix);
			if (prefix == (byte)0x82) {
				byteList.add(fe6ByteFrom82Character(character));
			} else {
				byteList.add(fe6ByteFrom83Character(character));
			}
		}
		
		// Terminate the string.
		byteList.add((byte)0);
		
		return WhyDoesJavaNotHaveThese.byteArrayFromByteList(byteList);
	}
	
	private char fe6CharacterFrom83Byte(byte charByte) {
		switch (charByte) {
		case (byte)0x41: return ';';
		case (byte)0x43: return '<';
		case (byte)0x45: return '=';
		case (byte)0x47: return '>';
		case (byte)0x49: return '?';
		case (byte)0x4A: return '@';
		//case (byte)0x4C: return '[';
		//case (byte)0x50: return ']';
		case (byte)0x52: return '^';
		case (byte)0x54: return '_';
		case (byte)0x56: return '`';
		case (byte)0x58: return '{';
		case (byte)0x5A: return '|';
		case (byte)0x5C: return '}';
		case (byte)0x5E: return '~';
		default: return '_';
		}
	}
	
	private char fe6CharacterFrom82Byte(byte charByte) {
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
	
	private byte fe6PrefixByteForCharacter(char character) {
		switch (character) {
		case ';':
		case '<':
		case '=':
		case '>':
		case '?':
		case '@':
		//case '[':
		//case ']':
		case '^':
		case '_':
		case '`':
		case '{':
		case '|':
		case '}':
		case '~':
			return (byte)0x83;
		default:
			return (byte)0x82;
		}
	}
	
	private byte fe6ByteFrom83Character(char character) {
		switch (character) {
		case ';': return (byte)0x41;
		case '<': return (byte)0x43;
		case '=': return (byte)0x45;
		case '>': return (byte)0x47;
		case '?': return (byte)0x49;
		case '@': return (byte)0x4A;
		case '[': return (byte)0x4C;
		case ']': return (byte)0x50;
		case '^': return (byte)0x52;
		case '_': return (byte)0x54;
		case '`': return (byte)0x56;
		case '{': return (byte)0x58;
		case '|': return (byte)0x5A;
		case '}': return (byte)0x5C;
		case '~': return (byte)0x5E;
		default:
			System.err.println("Unencodable character detected.");
			return (byte)0x54; // '_'
		}
	}
	
	private byte fe6ByteFrom82Character(char character) {
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
			return (byte)0xC0; // '$'
		}
	}
}
