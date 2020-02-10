package random.gcnwii.fe9.randomizer;

import java.util.List;
import java.util.Random;

import fedata.gcnwii.fe9.FE9ChapterRewards;
import fedata.gcnwii.fe9.FE9Item;
import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;

public class FE9RewardsRandomizer {
	
	static final int rngSalt = 42069;
	
	public static void randomizeSimilarRewards(FE9ItemDataLoader itemData, FE9ChapterDataLoader chapterData, Random rng) {
		for (FE9ChapterRewards rewards : chapterData.getAllChapterRewards()) {
			for (String iid : rewards.getChestContents()) {
				rewards.replaceChest(iid, similarIID(iid, itemData, rng));
			}
			for (String iid : rewards.getVillageContents()) {
				rewards.replaceVillage(iid, similarIID(iid, itemData, rng));
			}
			for (String iid : rewards.getDesertContents()) {
				rewards.replaceDesert(iid, similarIID(iid, itemData, rng));
			}
			
			rewards.commitChanges();
		}
	}
	
	public static void randomizeRewards(FE9ItemDataLoader itemData, FE9ChapterDataLoader chapterData, Random rng) {
		for (FE9ChapterRewards rewards : chapterData.getAllChapterRewards()) {
			for (String iid : rewards.getChestContents()) {
				rewards.replaceChest(iid, randomIID(iid, itemData, rng));
			}
			for (String iid : rewards.getVillageContents()) {
				rewards.replaceVillage(iid, randomIID(iid, itemData, rng));
			}
			for (String iid : rewards.getDesertContents()) {
				rewards.replaceDesert(iid, randomIID(iid, itemData, rng));
			}
			
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
