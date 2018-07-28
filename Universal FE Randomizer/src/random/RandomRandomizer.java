package random;

import java.util.concurrent.ThreadLocalRandom;

import fedata.FEChapterItem;
import fedata.FEItem;

public class RandomRandomizer {
	public static void randomizeRewards(ItemDataLoader itemData, ChapterLoader chapterData) {
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
			
			int random = ThreadLocalRandom.current().nextInt(100);
			if (random < chanceOfRelatedItem) {
				int randomIndex = ThreadLocalRandom.current().nextInt(relatedItems.length);
				chapterItem.setItemID(relatedItems[randomIndex].getID());
			} else {
				int randomIndex = ThreadLocalRandom.current().nextInt(allPossibleItems.length);
				chapterItem.setItemID(allPossibleItems[randomIndex].getID());
			}
		}
	}

}
