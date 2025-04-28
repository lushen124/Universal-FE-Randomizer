package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFEItemData;
import fedata.gcnwii.fe9.FE9Data.Chapter;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ItemDataLoader;
import random.general.PoolDistributor;
import random.general.WeightedDistributor;

public class RandomRandomizer {
	static final int rngSalt = 27682;
	
	public static void randomizeRewards(ItemDataLoader itemData, ChapterLoader chapterData, boolean includePromoWeapons, Random rng) {
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			GBAFEChapterItemData[] allRewards = chapter.allRewards();
			for (GBAFEChapterItemData chapterItem : allRewards) {
				int itemID = chapterItem.getItemID();
				GBAFEItemData[] relatedItems = itemData.relatedItems(itemID, true);
				GBAFEItemData[] allPossibleItems = itemData.getChestRewards(includePromoWeapons);
				
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

	public static void addRandomEnemyDrops(int chance, CharacterDataLoader charData, ItemDataLoader itemData, ChapterLoader chapterData, Random rng) {
		GBAFEChapterData[] chapters = chapterData.allChapters();
		WeightedDistributor<GBAFEItemData> firstQuarter = new WeightedDistributor<GBAFEItemData>();
		for (GBAFEItemData item : itemData.commonDrops()) { firstQuarter.addItem(item, 90); }
		for (GBAFEItemData item : itemData.uncommonDrops()) { firstQuarter.addItem(item, 9); }
		for (GBAFEItemData item : itemData.rareDrops()) { firstQuarter.addItem(item, 1); }
		for (int i = 0; i < chapters.length / 4; i++) {
			GBAFEChapterData chapter = chapters[i];
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.isEnemy() && !charData.isBossCharacterID(unit.getCharacterNumber())) {
					if (rng.nextInt(100) < chance) {
						unit.setUnitToDropLastItem(true);
						if (rng.nextInt(4) != 0) {
							unit.giveItems(new int[] {firstQuarter.getRandomItem(rng).getID()}, itemData);
						}
					}
				}
			}
		}
		WeightedDistributor<GBAFEItemData> secondQuarter = new WeightedDistributor<GBAFEItemData>();
		for (GBAFEItemData item : itemData.commonDrops()) { secondQuarter.addItem(item, 70); }
		for (GBAFEItemData item : itemData.uncommonDrops()) { secondQuarter.addItem(item, 20); }
		for (GBAFEItemData item : itemData.rareDrops()) { secondQuarter.addItem(item, 10); }
		for (int i = chapters.length / 4; i < chapters.length / 2; i++) {
			GBAFEChapterData chapter = chapters[i];
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.isEnemy() && !charData.isBossCharacterID(unit.getCharacterNumber())) {
					if (rng.nextInt(100) < chance) {
						unit.setUnitToDropLastItem(true);
						if (rng.nextInt(4) != 0) {
							unit.giveItems(new int[] {secondQuarter.getRandomItem(rng).getID()}, itemData);
						}
					}
				}
			}
		}
		
		WeightedDistributor<GBAFEItemData> thirdQuarter = new WeightedDistributor<GBAFEItemData>();
		for (GBAFEItemData item : itemData.commonDrops()) { thirdQuarter.addItem(item, 60); }
		for (GBAFEItemData item : itemData.uncommonDrops()) { thirdQuarter.addItem(item, 30); }
		for (GBAFEItemData item : itemData.rareDrops()) { thirdQuarter.addItem(item, 10); }
		for (int i = chapters.length / 2; i < chapters.length * 3 / 4; i++) {
			GBAFEChapterData chapter = chapters[i];
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.isEnemy() && !charData.isBossCharacterID(unit.getCharacterNumber())) {
					if (rng.nextInt(100) < chance) {
						unit.setUnitToDropLastItem(true);
						if (rng.nextInt(4) != 0) {
							unit.giveItems(new int[] {thirdQuarter.getRandomItem(rng).getID()}, itemData);
						}
					}
				}
			}
		}
		
		WeightedDistributor<GBAFEItemData> fourthQuarter = new WeightedDistributor<GBAFEItemData>();
		for (GBAFEItemData item : itemData.commonDrops()) { fourthQuarter.addItem(item, 50); }
		for (GBAFEItemData item : itemData.uncommonDrops()) { fourthQuarter.addItem(item, 30); }
		for (GBAFEItemData item : itemData.rareDrops()) { fourthQuarter.addItem(item, 20); }
		for (int i = chapters.length * 3 / 4; i < chapters.length; i++) {
			GBAFEChapterData chapter = chapters[i];
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.isEnemy() && !charData.isBossCharacterID(unit.getCharacterNumber())) {
					if (rng.nextInt(100) < chance) {
						unit.setUnitToDropLastItem(true);
						if (rng.nextInt(4) != 0) {
							unit.giveItems(new int[] {fourthQuarter.getRandomItem(rng).getID()}, itemData);
						}
					}
				}
			}
		}
	}
}
