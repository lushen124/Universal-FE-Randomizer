package random;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.FEBase;
import fedata.fe7.FE7Data;
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
	
	public TextLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		Date start = new Date();
		switch (gameType) {
			case FE7:
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
		return result.replaceAll("\\[[^\\[]*\\]", "");
	}
	
	public void setStringAtIndex(int index, String string) {
		replacements.put(index, string);
	}
	
	public HuffmanHelper getHuffman() {
		return huffman;
	}
	
	public void commitChanges(FreeSpaceManager freeSpace, DiffCompiler compiler) {
		for (int index : replacements.keySet()) {
			String replacement = replacements.get(index);
			
			byte[] newByteArray = huffman.encodeString(replacement);
			long offset = freeSpace.setValue(newByteArray, "Text At Index 0x" + Integer.toHexString(index));
			long pointer = textArrayOffset + 4 * index;
			byte[] addressBytes = WhyDoesJavaNotHaveThese.bytesFromAddress(offset);
			compiler.addDiff(new Diff(pointer, 4, addressBytes, null));
		}
	}
}
