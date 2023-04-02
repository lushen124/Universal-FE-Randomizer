package random.gba.loader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.general.GBAFETextProvider;
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
	
	public Set<Integer> excludedNameIndicies;
	
	public TextLoader(FEBase.GameType gameType, GBAFETextProvider provider, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		long startMillis = System.currentTimeMillis();
		
		huffman = new HuffmanHelper(handler);
		int startIndex = gameType.equals(GameType.FE7) ? 0 : 1;
		int earlyEnd = gameType.equals(GameType.FE7) ? 1 : 0;
		allStrings = new String[provider.getNumberOfTextStrings() + startIndex];
		textArrayOffset = FileReadHelper.readAddress(handler, provider.getTextTablePointer());
		treeAddress = FileReadHelper.readAddress(handler, provider.getHuffmanTreeStart());
		rootAddress = FileReadHelper.readAddress(handler, FileReadHelper.readAddress(handler, provider.getHuffmanTreeEnd()));
		excludedNameIndicies = provider.getExcludedIndiciesFromNameUpdate();
		
		for (int i = startIndex; i <= provider.getNumberOfTextStrings() - earlyEnd; i++) {
			String decoded = huffman.sanitizeByteArrayIntoTextString(huffman.decodeTextAddressWithHuffmanTree(
					FileReadHelper.readAddress(handler, textArrayOffset + 4 * i),
					treeAddress, 
					rootAddress), false, gameType);
			DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, String.format("Decoded %s String for index 0x%s decoded: %s", gameType.name(), Integer.toHexString(i).toUpperCase(), decoded));
			allStrings[i] = decoded;
		}
		long endMillis = System.currentTimeMillis();
		DebugPrinter.log(DebugPrinter.Key.TEXT_LOADING, String.format("Text Import took %s ms", endMillis - startMillis));
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
		for (Entry<Integer, String> e : replacementsWithCodes.entrySet()) {
			int index = e.getKey();
			String replacementWithCodes = e.getValue();
			
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
	
	public boolean isExcludedNameIndex(int index) {
		return excludedNameIndicies.contains(index);
	}
}
