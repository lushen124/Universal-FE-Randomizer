package random;

import java.util.Random;

import fedata.FEChapterItem;
import fedata.FEItem;

public class RandomRandomizer {
	static final int rngSalt = 27682;
	
	public static void randomizeRewards(ItemDataLoader itemData, ChapterLoader chapterData, Random rng) {
		FEChapterItem[] allRewards = chapterData.allRewards();
		for (FEChapterItem chapterItem : allRewards) {
			int itemID = chapterItem.getItemID();
			FEItem[] relatedItems = itemData.relatedItems(itemID);
			FEItem[] allPossibleItems = itemData.getChestRewards();
			
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
