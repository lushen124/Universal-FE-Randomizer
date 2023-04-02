package fedata.gba.general;

import java.util.Set;

public interface GBAFETextProvider {
	public int getHuffmanTreeStart();
	public int getHuffmanTreeEnd();
	public int getTextTablePointer();
	public int getNumberOfTextStrings();
	
	/**
	 * During Random Recruitment the names are replaced in the text, these Indicies should be excluded from that, since they might cause issues.
	 * (F.e. FE6 Lance -> Iron Lance, Silver Lance etc.) 
	 */
	public Set<Integer> getExcludedIndiciesFromNameUpdate();
}
