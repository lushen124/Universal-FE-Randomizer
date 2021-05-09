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
	
	private long treeAddress;
	private long rootAddress;

	private Map<Integer, String> replacementsWithCodes = new HashMap<Integer, String>();
	
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
				treeAddress = FileReadHelper.readAddress(handler, FE6Data.HuffmanTreeStart);
				rootAddress = FileReadHelper.readAddress(handler, FileReadHelper.readAddress(handler, FE6Data.HuffmanTreeEnd));
				for (int i = 1; i <= FE6Data.NumberOfTextStrings; i++) {
					String decoded = huffman.sanitizeByteArrayIntoTextString(huffman.decodeTextAddressWithHuffmanTree(
							FileReadHelper.readAddress(handler, textArrayOffset + 4 * i),
							treeAddress, 
							rootAddress), false, gameType);
					DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, "Decoded FE6 String for index 0x" + Integer.toHexString(i).toUpperCase());
					DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, decoded);
					allStrings[i] = decoded;
				}
				break;
			}
			case FE7: {
				huffman = new HuffmanHelper(handler);
				allStrings = new String[FE7Data.NumberOfTextStrings];
				textArrayOffset = FileReadHelper.readAddress(handler, FE7Data.TextTablePointer);
				treeAddress = FileReadHelper.readAddress(handler, FE7Data.HuffmanTreeStart);
				rootAddress = FileReadHelper.readAddress(handler, FileReadHelper.readAddress(handler, FE7Data.HuffmanTreeEnd));
				for (int i = 0; i < FE7Data.NumberOfTextStrings; i++) {
					String decoded = huffman.sanitizeByteArrayIntoTextString(huffman.decodeTextAddressWithHuffmanTree( 
							FileReadHelper.readAddress(handler, textArrayOffset + 4 * i), 
							treeAddress, 
							rootAddress), false, gameType);
					DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, "Decoded FE7 String for index 0x" + Integer.toHexString(i).toUpperCase());
					DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, decoded);
					allStrings[i] = decoded;
				}
				break;
			}
			case FE8: {
				huffman = new HuffmanHelper(handler);
				allStrings = new String[FE8Data.NumberOfTextStrings + 1];
				textArrayOffset = FileReadHelper.readAddress(handler, FE8Data.TextTablePointer);
				treeAddress = FileReadHelper.readAddress(handler, FE8Data.HuffmanTreeStart);
				rootAddress = FileReadHelper.readAddress(handler, FileReadHelper.readAddress(handler, FE8Data.HuffmanTreeEnd));
				for (int i = 1; i <= FE8Data.NumberOfTextStrings; i++) {
					String decoded = huffman.sanitizeByteArrayIntoTextString(huffman.decodeTextAddressWithHuffmanTree( 
							FileReadHelper.readAddress(handler, textArrayOffset + 4 * i), 
							treeAddress, 
							rootAddress), false, gameType);
					DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, "Loaded Text for index 0x" + Integer.toHexString(i) + ": " + decoded);
					allStrings[i] = decoded;
				}
				break;
			}
			default:
				break;
		}
		Date end = new Date();
		DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, "Text Import took " + Long.toString(end.getTime() - start.getTime()) + "ms");
		huffman.printCache();
	}
	
	public int getStringCount() {
		return allStrings.length;
	}

	// Note that we save strings with codes and without codes in separate tables.
	// When fetching, specifying whether to strip codes determines which to pull from.
	// If a string with codes is set, retrieving the string without codes will NOT give the modified string
	// and vice versa. Instead, it will return the unchanged string.
	public String getStringAtIndex(int index, boolean stripCodes) {
		if (index > 0xFFFF) { return null; }
		
		String replacement = replacementsWithCodes.get(index);
		
		String result = replacement != null ? replacement : allStrings[index];
		if (result == null) { return ""; }
		if (!stripCodes) { return result; }
		return result.replaceAll("\\[[^\\[]*\\]", "");
	}
	
	public void setStringAtIndex(int index, String string) {
		if (allowTextChanges) {
			replacementsWithCodes.put(index, string);
		}
	}
	
	public HuffmanHelper getHuffman() {
		return huffman;
	}
	
	public void commitChanges(FreeSpaceManager freeSpace, DiffCompiler compiler) {
		if (!allowTextChanges) { return; }
		
		// Let replacements with codes override any replacements without codes, if both exist.
		for (int index : replacementsWithCodes.keySet()) {
			String replacementWithCodes = replacementsWithCodes.get(index);
			
			// FE6 has some junk entries (or at least they look like junk.)
			if (index >= 0x1D2 && index <= 0x1E7) { continue; }
			
			// TODO: If we ever need to encode FE6 names, the new translation patch has a few characters that need special attention, since some characters are special thinner versions.
			boolean useThin = false;
			// These are character names. Only "Gwendolyn" needs this treatment.
			if (index >= 0x7EC && index <= 0x8B5 && replacementWithCodes.contains("Gwendolyn")) { useThin = true; }
			// The only other strings that have this are the Binding Blade (whose name we don't change), and some epilogue titles, which we also don't change.
			
			byte[] newByteArray = gameType == GameType.FE6 ? huffman.encodeNonHuffmanString(replacementWithCodes, true, useThin) : huffman.encodeString(replacementWithCodes, true);
			long offset = freeSpace.setValue(newByteArray, "Text At Index 0x" + Integer.toHexString(index));
			//if (gameType == GameType.FE6) { offset |= 0x80000000; } // Mark this as uncompressed.
			long pointer = textArrayOffset + 4 * index;
			byte[] addressBytes = WhyDoesJavaNotHaveThese.bytesFromAddress(offset);
			compiler.addDiff(new Diff(pointer, 4, addressBytes, null));
			
			allStrings[index] = replacementWithCodes; // We can replace these now, since they both have codes on them.
		}
	}
}
