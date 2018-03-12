package util;

import java.util.Hashtable;

import fedata.FE7Data;
import fedata.FEBase;
import io.FileHandler;

public class TextHelper {

	long[] addresses;
	Hashtable<Long, String> strings = new Hashtable<>();
	
	long textArrayOffset;
	int textArrayCount;
	
	long huffmanTreeStart;
	long huffmanTreeRoot;
	
	public TextHelper(FEBase.GameType gameType, FileHandler handler) {
		super();
		
		long startPointer = 0;
		long endPointer = 0;
		
		switch (gameType) {
			case FE7:
				startPointer = FE7Data.HuffmanTreeStart;
				endPointer = FE7Data.HuffmanTreeEnd;
				
				textArrayCount = FE7Data.NumberOfTextStrings;
				textArrayOffset = FE7Data.DefaultTextArrayOffset;
				break;
			default:
				break;
		}
		
		huffmanTreeStart = FileReadHelper.readWord(handler, startPointer, true);
		huffmanTreeRoot = FileReadHelper.readWord(handler, FileReadHelper.readWord(handler, endPointer, true), true);
		
		addresses = new long[textArrayCount];
		
		long currentOffset = textArrayOffset;
		for (int i = 0; i < textArrayCount; i++) {
			long address = FileReadHelper.readWord(handler, currentOffset, true);
			if ((address & 0x7E000000) != 0) {
				break;
			}
			addresses[i] = address;
			currentOffset += 4;
		}
		
		for (long address : addresses) {
			String resolvedString = HuffmanHelper.sanitizeByteArrayIntoTextString(HuffmanHelper.decodeTextAddressWithHuffmanTree(handler, address, huffmanTreeStart, huffmanTreeRoot), true, gameType);
			strings.put(address, resolvedString);
		}
	}
	
	public String getTextAtIndex(int textIndex) {
		return strings.get(addresses[textIndex]);
	}
}
