package random.gcnwii.fe9.randomizer;

import java.util.List;
import java.util.Random;

import fedata.gcnwii.fe9.FE9ChapterRewards;
import fedata.gcnwii.fe9.FE9ChapterRewardsProcessor;
import fedata.gcnwii.fe9.FE9Item;
import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import util.DebugPrinter;

public class FE9RewardsRandomizer {
	
	static final int rngSalt = 42069;
	
	public static void randomizeSimilarRewards(FE9ItemDataLoader itemData, FE9ChapterDataLoader chapterData, Random rng) {
		for (FE9ChapterRewards rewards : chapterData.getAllChapterRewards()) {
			rewards.replaceRewards(new FE9ChapterRewardsProcessor() {
				@Override
				public String replaceItem(String iid) {
					String newIID = similarIID(iid, itemData, rng);
					DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "Replacing item " + iid + " with " + newIID);
					return newIID;
				}
			});
			
			rewards.commitChanges();
		}
	}
	
	public static void randomizeRewards(FE9ItemDataLoader itemData, FE9ChapterDataLoader chapterData, Random rng) {
		for (FE9ChapterRewards rewards : chapterData.getAllChapterRewards()) {
			rewards.replaceRewards(new FE9ChapterRewardsProcessor() {
				@Override
				public String replaceItem(String iid) {
					String newIID = randomIID(iid, itemData, rng);
					DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "Replacing item " + iid + " with " + newIID);
					return newIID;
				}
			});
			
			rewards.commitChanges();
		}
	}

	private static String similarIID(String originalIID, FE9ItemDataLoader itemData, Random rng) {
		List<FE9Item> potentialItems = itemData.getSimilarItemsTo(itemData.itemWithIID(originalIID));
		if (potentialItems.isEmpty()) { return null; }
		FE9Item replacement = potentialItems.get(rng.nextInt(potentialItems.size()));
		return itemData.iidOfItem(replacement);
	}
	
	private static String randomIID(String originalIID, FE9ItemDataLoader itemData, Random rng) {
		List<FE9Item> potentialItems = itemData.getPossibleRewards();
		if (potentialItems.isEmpty()) { return null; }
		FE9Item replacement = potentialItems.get(rng.nextInt(potentialItems.size()));
		return itemData.iidOfItem(replacement);
	}
}
