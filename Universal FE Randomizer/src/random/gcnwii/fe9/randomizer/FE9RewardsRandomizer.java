package random.gcnwii.fe9.randomizer;

import java.util.List;
import java.util.Random;

import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterRewards;
import fedata.gcnwii.fe9.FE9ChapterRewardsProcessor;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Item;
import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
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
	
	public static void addEnemyDrops(FE9CharacterDataLoader charData, FE9ItemDataLoader itemData, FE9ChapterDataLoader chapterData, int dropChance, Random rng) {
		List<FE9Item> potentialItemRewards = itemData.getPossibleRewards();
		potentialItemRewards.removeIf(item -> { return itemData.isWeapon(item); });
		
		for (FE9Data.Chapter chapter : FE9Data.Chapter.allChapters()) {
			List<FE9ChapterArmy> armies = chapterData.armiesForChapter(chapter);
			for (FE9ChapterArmy army : armies) {
				for (String unitID : army.getAllUnitIDs()) {
					FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
					String pid = army.getPIDForUnit(unit);
					if (charData.isMinionCharacter(charData.characterWithID(pid))) {
						if (rng.nextInt(100) < dropChance) {
							// We want to drop. 50% of these should be weapons, and 50% should be items.
							if (rng.nextInt(2) == 0 && army.getWeapon1ForUnit(unit) != null) {
								// Drop the weapon.
								unit.setWillDropWeapon1(true);
							} else {
								if (potentialItemRewards.isEmpty()) { continue; }
								FE9Item droppedItem = potentialItemRewards.get(rng.nextInt(potentialItemRewards.size()));
								// Drop an item.
								if (unit.willDropItem1()) {
									if (army.getItem1ForUnit(unit).equals(FE9Data.Item.CHEST_KEY.getIID())) { continue; } // Do not replace chest keys.
									army.setItem1ForUnit(unit, itemData.iidOfItem(droppedItem));
								} else if (unit.willDropItem2()) {
									if (army.getItem2ForUnit(unit).equals(FE9Data.Item.CHEST_KEY.getIID())) { continue; }
									army.setItem2ForUnit(unit, itemData.iidOfItem(droppedItem));
								} else if (unit.willDropItem3()) {
									if (army.getItem3ForUnit(unit).equals(FE9Data.Item.CHEST_KEY.getIID())) { continue; }
									army.setItem3ForUnit(unit, itemData.iidOfItem(droppedItem));
								} else if (unit.willDropItem4()) {
									if (army.getItem4ForUnit(unit).equals(FE9Data.Item.CHEST_KEY.getIID())) { continue; }
									army.setItem4ForUnit(unit, itemData.iidOfItem(droppedItem));
								} else {
									if (army.getItem1ForUnit(unit) == null) {
										army.setItem1ForUnit(unit, itemData.iidOfItem(droppedItem));
										unit.setWillDropItem1(true);
									} else if (army.getItem2ForUnit(unit) == null) {
										army.setItem2ForUnit(unit, itemData.iidOfItem(droppedItem));
										unit.setWillDropItem2(true);
									} else if (army.getItem3ForUnit(unit) == null) {
										army.setItem3ForUnit(unit, itemData.iidOfItem(droppedItem));
										unit.setWillDropItem3(true);
									} else if (army.getItem4ForUnit(unit) == null) {
										army.setItem4ForUnit(unit, itemData.iidOfItem(droppedItem));
										unit.setWillDropItem4(true);
									}
								}
							}
						}
					}
				}
				army.commitChanges();
			}
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
