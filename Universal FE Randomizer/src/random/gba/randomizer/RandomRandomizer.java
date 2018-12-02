package random.gba.randomizer;

import java.util.Random;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEItemData;
import random.gba.loader.ChapterLoader;
import random.gba.loader.ItemDataLoader;

public class RandomRandomizer {
	static final int rngSalt = 27682;
	
	public static void randomizeRewards(ItemDataLoader itemData, ChapterLoader chapterData, Random rng) {
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			GBAFEChapterItemData[] allRewards = chapter.allRewards();
			for (GBAFEChapterItemData chapterItem : allRewards) {
				int itemID = chapterItem.getItemID();
				GBAFEItemData[] relatedItems = itemData.relatedItems(itemID);
				GBAFEItemData[] allPossibleItems = itemData.getChestRewards();
				
				if (relatedItems.length == 0 && allPossibleItems.length == 0) {
					continue;
				}
				
				int chanceOfRelatedItem = 50;
				if (relatedItems.length == 0) {
					chanceOfRelatedItem = 0;
				}
				
				int random = rng.nextInt(100);
				if (random < chanceOfRelatedItem) {
					int randomIndex = rng.nextInt(relatedItems.length);
					chapterItem.setItemID(relatedItems[randomIndex].getID());
				} else {
					int randomIndex = rng.nextInt(allPossibleItems.length);
					chapterItem.setItemID(allPossibleItems[randomIndex].getID());
				}
			}
		}
	}

}
