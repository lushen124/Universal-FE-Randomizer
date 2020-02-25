package util;

public class DebugPrinter {
	
	public enum Key {
		MAIN("Main"),
		
		PALETTE("Palette"), CHAPTER_LOADER("ChapterLoader"), DIFF("Diff"), HUFFMAN("Huffman"), TEXT_LOADING("Text"), RANDOM("Random"), FREESPACE("Free Space"), WEAPONS("Weapon Effect"), UPS("UPS"), CLASS_RANDOMIZER("Class Random"),
		PALETTE_RECYCLER("Palette Recycling"), FE8_SUMMONER_MODULE("Summoner"), FE4_CHARACTER_LOADER("FE4 Character Loader"), FE4_ITEM_MAPPER("FE4 Item Mapper"), FE4_SKILL_RANDOM("FE4 Skill Randomizer"), 
		GBA_TEXT_CODE_CHANGE("GBAFE Text Change"), GBA_RANDOM_RECRUITMENT("GBA Random Recruitment"), LZ77("LZ77"), 
		
		GCN_HANDLER("GCN Handler"), FE9_CHARACTER_LOADER("FE9 Character Loader"), FE9_TEXT_LOADER("FE9 Text Loader"),
		FE9_CLASS_LOADER("FE9 Class Loader"), FE9_ITEM_LOADER("FE9 Item Loader"), FE9_SKILL_LOADER("FE9 Skill Loader"),
		FE9_CHAPTER_LOADER("FE9 Chapter Loader"), FE9_ARMY_LOADER("FE9 Army Loader"), FE9_RANDOM_CLASSES("FE9 Class Randomization"),
		
		MISC("MISC");
		
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
//		case MAIN:
//		case FE9_ARMY_LOADER:
//			return true;
		default:
			return false;
		}
	}
}
