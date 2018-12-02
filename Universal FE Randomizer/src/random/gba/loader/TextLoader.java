package random.gba.loader;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FreeSpaceManager;
import util.HuffmanHelper;
import util.WhyDoesJavaNotHaveThese;

public class TextLoader {
	private FEBase.GameType gameType;
	
	private String[] allStrings;
	private HuffmanHelper huffman;
	
	private long textArrayOffset;
	
	private Map<Integer, String> replacements = new HashMap<Integer, String>();
	
	public Boolean allowTextChanges = false;
	
	public TextLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		Date start = new Date();
		switch (gameType) {
			case FE6: {
				huffman = new HuffmanHelper(handler);
				allStrings = new String[FE6Data.NumberOfTextStrings + 1];
				textArrayOffset = FileReadHelper.readAddress(handler, FE6Data.TextTablePointer);
				for (int i = 1; i <= FE6Data.NumberOfTextStrings; i++) {
					String decoded = huffman.sanitizeByteArrayIntoTextString(huffman.decodeTextAddressWithHuffmanTree(
							FileReadHelper.readWord(handler, textArrayOffset + 4 * i, false), // FE6 uses the most significant bit on the text address to signify its english encoding, so this is a little less safe.
							FileReadHelper.readAddress(handler, FE6Data.HuffmanTreeStart), 
							FileReadHelper.readAddress(handler, FileReadHelper.readAddress(handler, FE6Data.HuffmanTreeEnd))), false, gameType);
					DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Decoded FE6 String for index 0x" + Integer.toHexString(i).toUpperCase());
					DebugPrinter.log(DebugPrinter.Key.HUFFMAN, decoded);
					allStrings[i] = decoded;
				}
				break;
			}
			case FE7: {
				huffman = new HuffmanHelper(handler);
				allStrings = new String[FE7Data.NumberOfTextStrings];
				textArrayOffset = FileReadHelper.readAddress(handler, FE7Data.TextTablePointer);
				for (int i = 0; i < FE7Data.NumberOfTextStrings; i++) {
					String decoded = huffman.sanitizeByteArrayIntoTextString(huffman.decodeTextAddressWithHuffmanTree( 
							FileReadHelper.readAddress(handler, textArrayOffset + 4 * i), 
							FileReadHelper.readAddress(handler, FE7Data.HuffmanTreeStart), 
							FileReadHelper.readAddress(handler, FileReadHelper.readAddress(handler, FE7Data.HuffmanTreeEnd))), false, gameType);
					allStrings[i] = decoded;
				}
				break;
			}
			case FE8: {
				huffman = new HuffmanHelper(handler);
				allStrings = new String[FE8Data.NumberOfTextStrings + 1];
				textArrayOffset = FileReadHelper.readAddress(handler, FE8Data.TextTablePointer);
				for (int i = 1; i <= FE8Data.NumberOfTextStrings; i++) {
					String decoded = huffman.sanitizeByteArrayIntoTextString(huffman.decodeTextAddressWithHuffmanTree( 
							FileReadHelper.readAddress(handler, textArrayOffset + 4 * i), 
							FileReadHelper.readAddress(handler, FE8Data.HuffmanTreeStart), 
							FileReadHelper.readAddress(handler, FileReadHelper.readAddress(handler, FE8Data.HuffmanTreeEnd))), false, gameType);
					DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, "Loaded Text for index 0x" + Integer.toHexString(i) + ": " + decoded);
					allStrings[i] = decoded;
				}
				break;
			}
			default:
				break;
		}
		Date end = new Date();
		DebugPrinter.log(DebugPrinter.Key.HUFFMAN, "Text Import took " + Long.toString(end.getTime() - start.getTime()) + "ms");
		huffman.printCache();
	}

	public String getStringAtIndex(int index) {
		if (index > 0xFFFF) { return null; }
		
		String replacement = replacements.get(index);
		if (replacement != null) { return replacement; }
		
		String result = allStrings[index];
		if (result == null) { return ""; }
		return result.replaceAll("\\[[^\\[]*\\]", "");
	}
	
	public void setStringAtIndex(int index, String string) {
		if (allowTextChanges) {
			replacements.put(index, string);
		}
	}
	
	public HuffmanHelper getHuffman() {
		return huffman;
	}
	
	public void commitChanges(FreeSpaceManager freeSpace, DiffCompiler compiler) {
		if (!allowTextChanges) { return; }
		
		for (int index : replacements.keySet()) {
			String replacement = replacements.get(index);
			
			byte[] newByteArray = gameType == GameType.FE6 ? huffman.encodeNonHuffmanString(replacement) : huffman.encodeString(replacement);
			long offset = freeSpace.setValue(newByteArray, "Text At Index 0x" + Integer.toHexString(index));
			if (gameType == GameType.FE6) { offset |= 0x80000000; } // Mark this as uncompressed.
			long pointer = textArrayOffset + 4 * index;
			byte[] addressBytes = WhyDoesJavaNotHaveThese.bytesFromAddress(offset);
			compiler.addDiff(new Diff(pointer, 4, addressBytes, null));
		}
	}
}
