package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;

public class EnemyBuffer {
	
	static final int rngSalt = 252521;

	public static void buffEnemyGrowthRates(int buffAmount, ClassDataLoader classData) {
		GBAFEClassData[] allClasses = classData.allClasses();
		for (GBAFEClassData currentClass : allClasses) {
			currentClass.setHPGrowth(currentClass.getHPGrowth() + buffAmount);
			currentClass.setSTRGrowth(currentClass.getSTRGrowth() + buffAmount);
			currentClass.setSKLGrowth(currentClass.getSKLGrowth() + buffAmount);
			currentClass.setSPDGrowth(currentClass.getSPDGrowth() + buffAmount);
			currentClass.setDEFGrowth(currentClass.getDEFGrowth() + buffAmount);
			currentClass.setRESGrowth(currentClass.getRESGrowth() + buffAmount);
			currentClass.setLCKGrowth(currentClass.getLCKGrowth() + buffAmount);
		}
	}
	
	public static void scaleEnemyGrowthRates(int scaleAmount, ClassDataLoader classData) {
		GBAFEClassData[] allClasses = classData.allClasses();
		float multiplier = 1 + scaleAmount / 100;
		for (GBAFEClassData currentClass : allClasses) {
			currentClass.setHPGrowth((int)(currentClass.getHPGrowth() * multiplier));
			currentClass.setSTRGrowth((int)(currentClass.getSTRGrowth() * multiplier));
			currentClass.setSKLGrowth((int)(currentClass.getSKLGrowth() * multiplier));
			currentClass.setSPDGrowth((int)(currentClass.getSPDGrowth() * multiplier));
			currentClass.setDEFGrowth((int)(currentClass.getDEFGrowth() * multiplier));
			currentClass.setRESGrowth((int)(currentClass.getRESGrowth() * multiplier));
			currentClass.setLCKGrowth((int)(currentClass.getLCKGrowth() * multiplier));
		}
	}
	
	public static void improveWeapons(int probability, CharacterDataLoader charactersData, 
			ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, Random rng) {
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				int leaderID = chapterUnit.getLeaderID();
				if (charactersData.isBossCharacterID(leaderID)) {
					GBAFEClassData originalClass = classData.classForID(chapterUnit.getStartingClass());
					if (originalClass == null) {
						continue;
					}
					
					if (classData.isThief(originalClass.getID())) {
						continue;
					}
					
					if (rng.nextInt(100) < probability) {
						upgradeWeapons(chapterUnit, classData, itemData, rng);
					}
				}
			}
		}
		
		GBAFEClassData[] allClasses = classData.allClasses();
		for (GBAFEClassData currentClass : allClasses) {
			if (currentClass.getSwordRank() > 0) { currentClass.setSwordRank(WeaponRank.S); }
			if (currentClass.getLanceRank() > 0) { currentClass.setLanceRank(WeaponRank.S); }
			if (currentClass.getAxeRank() > 0) { currentClass.setAxeRank(WeaponRank.S); }
			if (currentClass.getBowRank() > 0) { currentClass.setBowRank(WeaponRank.S); }
			if (currentClass.getAnimaRank() > 0) { currentClass.setAnimaRank(WeaponRank.S); }
			if (currentClass.getDarkRank() > 0) { currentClass.setDarkRank(WeaponRank.S); }
			if (currentClass.getLightRank() > 0) { currentClass.setLightRank(WeaponRank.S); }
			if (currentClass.getStaffRank() > 0) { currentClass.setStaffRank(WeaponRank.S); }
		}
	}
	
	private static void upgradeWeapons(GBAFEChapterUnitData unit, ClassDataLoader classData, ItemDataLoader itemData, Random rng) {
		GBAFEClassData unitClass = classData.classForID(unit.getStartingClass());
		int item1ID = unit.getItem1();
		GBAFEItemData item1 = itemData.itemWithID(item1ID);
		if (item1 != null && item1.getType() != WeaponType.NOT_A_WEAPON) {
			GBAFEItemData[] improvedItems = availableItems(unitClass, 
					WeaponRank.nextRankHigherThanRank(item1.getWeaponRank()), item1.getType(), itemData);
			if (improvedItems.length > 0) {
				GBAFEItemData replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem1(replacementItem.getID());
			}
		}
		
		int item2ID = unit.getItem2();
		GBAFEItemData item2 = itemData.itemWithID(item2ID);
		if (item2 != null && item2.getType() != WeaponType.NOT_A_WEAPON) {
			GBAFEItemData[] improvedItems = availableItems(unitClass, 
					WeaponRank.nextRankHigherThanRank(item2.getWeaponRank()), item2.getType(), itemData);
			if (improvedItems.length > 0) {
				GBAFEItemData replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem2(replacementItem.getID());
			}
		}
		
		int item3ID = unit.getItem3();
		GBAFEItemData item3 = itemData.itemWithID(item3ID);
		if (item3 != null && item3.getType() != WeaponType.NOT_A_WEAPON) {
			GBAFEItemData[] improvedItems = availableItems(unitClass, 
					WeaponRank.nextRankHigherThanRank(item3.getWeaponRank()), item3.getType(), itemData);
			if (improvedItems.length > 0) {
				GBAFEItemData replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem3(replacementItem.getID());
			}
		}
		
		int item4ID = unit.getItem4();
		GBAFEItemData item4 = itemData.itemWithID(item4ID);
		if (item4 != null && item4.getType() != WeaponType.NOT_A_WEAPON) {
			GBAFEItemData[] improvedItems = availableItems(unitClass, 
					WeaponRank.nextRankHigherThanRank(item4.getWeaponRank()), item4.getType(), itemData);
			if (improvedItems.length > 0) {
				GBAFEItemData replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem4(replacementItem.getID());
			}
		}
	}
	
	private static GBAFEItemData[] availableItems(GBAFEClassData characterClass, WeaponRank rank, WeaponType type, ItemDataLoader itemData) {
		if (rank == WeaponRank.NONE) {
			return new GBAFEItemData[] {};
		}
		
		ArrayList<GBAFEItemData> items = new ArrayList<GBAFEItemData>();
		
		GBAFEItemData[] improvedItems = itemData.itemsOfTypeAndEqualRank(type, rank, false, false, true);
		items.addAll(Arrays.asList(improvedItems));
		
		GBAFEItemData[] prfWeapons = itemData.prfWeaponsForClass(characterClass.getID());
		if (prfWeapons != null) {
			items.addAll(Arrays.asList(prfWeapons));
		}
		GBAFEItemData[] classWeapons = itemData.lockedWeaponsToClass(characterClass.getID());
		if (classWeapons != null) {
			items.addAll(Arrays.asList(classWeapons));
		}
		
		return items.toArray(new GBAFEItemData[items.size()]);
	}
}
