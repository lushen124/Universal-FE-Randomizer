package util;

public class DebugPrinter {
	
	public enum Key {
		PALETTE("Palette"), CHAPTER_LOADER("ChapterLoader"), DIFF("Diff"), HUFFMAN("Huffman"), TEXT_LOADING("Text"), RANDOM("Random"), FREESPACE("Free Space"), WEAPONS("Weapon Effect"), UPS("UPS"), CLASS_RANDOMIZER("Class Random"),
		PALETTE_RECYCLER("Palette Recycling"), FE8_SUMMONER_MODULE("Summoner"), FE4_CHARACTER_LOADER("FE4 Character Loader"), FE4_ITEM_MAPPER("FE4 Item Mapper"), FE4_SKILL_RANDOM("FE4 Skill Randomizer"), 
		GBA_TEXT_CODE_CHANGE("GBAFE Text Change"), GBA_RANDOM_RECRUITMENT("GBA Random Recruitment"), LZ77("LZ77");
		
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
		case GBA_RANDOM_RECRUITMENT:
			return true;
		default:
			return false;
		}
	}
}
