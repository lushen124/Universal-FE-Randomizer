package util;

public class DebugPrinter {
	
	public enum Key {
		PALETTE("Palette"), CHAPTER_LOADER("ChapterLoader"), DIFF("Diff"), HUFFMAN("Huffman"), RANDOM("Random"), FREESPACE("Free Space"), WEAPONS("Weapon Effect");
		
		String label;
		
		private Key(String key) {
			this.label = key;
		}
	}
	
	public static void log(Key label, String output) {
		if (shouldPrintLabel(label)) {
			System.out.println("[" + label.label + "] " + output);
		}
	}
	
	private static Boolean shouldPrintLabel(Key label) {
		switch (label) {
		case CHAPTER_LOADER:
			return true;
		default:
			return false;
		}
	}
}
