package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import fedata.gba.GBAFEChapter;
import fedata.gba.GBAFEChapterUnit;
import fedata.gba.GBAFEClass;
import fedata.gba.GBAFEItem;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;

public class EnemyBuffer {
	
	static final int rngSalt = 252521;

	public static void buffEnemyGrowthRates(int buffAmount, ClassDataLoader classData) {
		GBAFEClass[] allClasses = classData.allClasses();
		for (GBAFEClass currentClass : allClasses) {
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
		GBAFEClass[] allClasses = classData.allClasses();
		float multiplier = 1 + scaleAmount / 100;
		for (GBAFEClass currentClass : allClasses) {
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
		for (GBAFEChapter chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnit chapterUnit : chapter.allUnits()) {
				int leaderID = chapterUnit.getLeaderID();
				if (charactersData.isBossCharacterID(leaderID)) {
					GBAFEClass originalClass = classData.classForID(chapterUnit.getStartingClass());
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
		
		GBAFEClass[] allClasses = classData.allClasses();
		for (GBAFEClass currentClass : allClasses) {
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
	
	private static void upgradeWeapons(GBAFEChapterUnit unit, ClassDataLoader classData, ItemDataLoader itemData, Random rng) {
		GBAFEClass unitClass = classData.classForID(unit.getStartingClass());
		int item1ID = unit.getItem1();
		GBAFEItem item1 = itemData.itemWithID(item1ID);
		if (item1 != null && item1.getType() != WeaponType.NOT_A_WEAPON) {
			GBAFEItem[] improvedItems = availableItems(unitClass, 
					WeaponRank.nextRankHigherThanRank(item1.getWeaponRank()), item1.getType(), itemData);
			if (improvedItems.length > 0) {
				GBAFEItem replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem1(replacementItem.getID());
			}
		}
		
		int item2ID = unit.getItem2();
		GBAFEItem item2 = itemData.itemWithID(item2ID);
		if (item2 != null && item2.getType() != WeaponType.NOT_A_WEAPON) {
			GBAFEItem[] improvedItems = availableItems(unitClass, 
					WeaponRank.nextRankHigherThanRank(item2.getWeaponRank()), item2.getType(), itemData);
			if (improvedItems.length > 0) {
				GBAFEItem replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem2(replacementItem.getID());
			}
		}
		
		int item3ID = unit.getItem3();
		GBAFEItem item3 = itemData.itemWithID(item3ID);
		if (item3 != null && item3.getType() != WeaponType.NOT_A_WEAPON) {
			GBAFEItem[] improvedItems = availableItems(unitClass, 
					WeaponRank.nextRankHigherThanRank(item3.getWeaponRank()), item3.getType(), itemData);
			if (improvedItems.length > 0) {
				GBAFEItem replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem3(replacementItem.getID());
			}
		}
		
		int item4ID = unit.getItem4();
		GBAFEItem item4 = itemData.itemWithID(item4ID);
		if (item4 != null && item4.getType() != WeaponType.NOT_A_WEAPON) {
			GBAFEItem[] improvedItems = availableItems(unitClass, 
					WeaponRank.nextRankHigherThanRank(item4.getWeaponRank()), item4.getType(), itemData);
			if (improvedItems.length > 0) {
				GBAFEItem replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem4(replacementItem.getID());
			}
		}
	}
	
	private static GBAFEItem[] availableItems(GBAFEClass characterClass, WeaponRank rank, WeaponType type, ItemDataLoader itemData) {
		if (rank == WeaponRank.NONE) {
			return new GBAFEItem[] {};
		}
		
		ArrayList<GBAFEItem> items = new ArrayList<GBAFEItem>();
		
		GBAFEItem[] improvedItems = itemData.itemsOfTypeAndEqualRank(type, rank, false, false, true);
		items.addAll(Arrays.asList(improvedItems));
		
		GBAFEItem[] prfWeapons = itemData.prfWeaponsForClass(characterClass.getID());
		if (prfWeapons != null) {
			items.addAll(Arrays.asList(prfWeapons));
		}
		GBAFEItem[] classWeapons = itemData.lockedWeaponsToClass(characterClass.getID());
		if (classWeapons != null) {
			items.addAll(Arrays.asList(classWeapons));
		}
		
		return items.toArray(new GBAFEItem[items.size()]);
	}
}
