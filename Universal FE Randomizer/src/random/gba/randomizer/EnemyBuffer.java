package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import ui.model.EnemyOptions;

public class EnemyBuffer {
	
	static final int rngSalt = 252521;
	
	// Enemy growths top out at 127. Going above that will underflow back to 0.
	private static int MaximumGrowthRate = 127;

	public static void buffMinionGrowthRates(int buffAmount, ClassDataLoader classData, EnemyOptions.BuffStats buffStats) {
		GBAFEClassData[] allClasses = classData.allClasses();
		for (GBAFEClassData currentClass : allClasses) {
			if (buffStats.hp) { currentClass.setHPGrowth(Math.min(MaximumGrowthRate, currentClass.getHPGrowth() + buffAmount)); }
			if (buffStats.str) { currentClass.setSTRGrowth(Math.min(MaximumGrowthRate, currentClass.getSTRGrowth() + buffAmount)); }
			if (buffStats.skl) { currentClass.setSKLGrowth(Math.min(MaximumGrowthRate, currentClass.getSKLGrowth() + buffAmount)); }
			if (buffStats.spd) { currentClass.setSPDGrowth(Math.min(MaximumGrowthRate, currentClass.getSPDGrowth() + buffAmount)); }
			if (buffStats.def) { currentClass.setDEFGrowth(Math.min(MaximumGrowthRate, currentClass.getDEFGrowth() + buffAmount)); }
			if (buffStats.res) { currentClass.setRESGrowth(Math.min(MaximumGrowthRate, currentClass.getRESGrowth() + buffAmount)); }
			if (buffStats.lck) { currentClass.setLCKGrowth(Math.min(MaximumGrowthRate, currentClass.getLCKGrowth() + buffAmount)); }
		}
	}
	
	public static void buffBossStatsLinearly(int maxBuff, CharacterDataLoader charData, ClassDataLoader classData, EnemyOptions.BuffStats buffStats) {
		for (GBAFECharacterData boss : charData.bossCharacters()) {
			double appearanceFactor = (double)charData.appearanceChapter(boss) / (double)charData.chapterCount();
			int buffAmount = Math.min(maxBuff, (int)Math.ceil(maxBuff * appearanceFactor));
			GBAFEClassData bossClass = classData.classForID(boss.getClassID());
			if (buffStats.hp) { boss.setBaseHP(Math.min(boss.getBaseHP() + buffAmount, (bossClass.getMaxHP() - bossClass.getBaseHP()))); }
			if (buffStats.str) { boss.setBaseSTR(Math.min(boss.getBaseSTR() + buffAmount, (bossClass.getMaxSTR() - bossClass.getBaseSTR()))); }
			if (buffStats.skl) { boss.setBaseSKL(Math.min(boss.getBaseSKL() + buffAmount, (bossClass.getMaxSKL() - bossClass.getBaseSKL()))); }
			if (buffStats.spd) { boss.setBaseSPD(Math.min(boss.getBaseSPD() + buffAmount, (bossClass.getMaxSPD() - bossClass.getBaseSPD()))); }
			if (buffStats.def) { boss.setBaseDEF(Math.min(boss.getBaseDEF() + buffAmount, (bossClass.getMaxDEF() - bossClass.getBaseDEF()))); }
			if (buffStats.res) { boss.setBaseRES(Math.min(boss.getBaseRES() + buffAmount, (bossClass.getMaxRES() - bossClass.getBaseRES()))); }
			if (buffStats.lck) { boss.setBaseLCK(Math.min(boss.getBaseLCK() + buffAmount, (bossClass.getMaxLCK() - bossClass.getBaseLCK()))); }
		}
	}
	
	public static void scaleEnemyGrowthRates(int scaleAmount, ClassDataLoader classData, EnemyOptions.BuffStats buffStats) {
		GBAFEClassData[] allClasses = classData.allClasses();
		double multiplier = 1 + (double)scaleAmount / 100.0;
		for (GBAFEClassData currentClass : allClasses) {
			if (buffStats.hp) { currentClass.setHPGrowth(Math.min(MaximumGrowthRate, (int)(currentClass.getHPGrowth() * multiplier))); }
			if (buffStats.str) { currentClass.setSTRGrowth(Math.min(MaximumGrowthRate, (int)(currentClass.getSTRGrowth() * multiplier))); }
			if (buffStats.skl) { currentClass.setSKLGrowth(Math.min(MaximumGrowthRate, (int)(currentClass.getSKLGrowth() * multiplier))); }
			if (buffStats.spd) { currentClass.setSPDGrowth(Math.min(MaximumGrowthRate, (int)(currentClass.getSPDGrowth() * multiplier))); }
			if (buffStats.def) { currentClass.setDEFGrowth(Math.min(MaximumGrowthRate, (int)(currentClass.getDEFGrowth() * multiplier))); }
			if (buffStats.res) { currentClass.setRESGrowth(Math.min(MaximumGrowthRate, (int)(currentClass.getRESGrowth() * multiplier))); }
			if (buffStats.lck) { currentClass.setLCKGrowth(Math.min(MaximumGrowthRate, (int)(currentClass.getLCKGrowth() * multiplier))); }
		}
	}
	
	public static void buffBossStatsWithEaseInOutCurve(int maxBuff, CharacterDataLoader charData, ClassDataLoader classData, EnemyOptions.BuffStats buffStats) {
		for (GBAFECharacterData boss : charData.bossCharacters()) {
			double appearanceFactor = (double)charData.appearanceChapter(boss) / (double)charData.chapterCount();
			appearanceFactor = Math.pow(appearanceFactor, 2) / (Math.pow(appearanceFactor, 2) + Math.pow(1 - appearanceFactor, 2));
			int buffAmount = Math.min(maxBuff, (int)Math.ceil(maxBuff * appearanceFactor));
			GBAFEClassData bossClass = classData.classForID(boss.getClassID());
			if (buffStats.hp) { boss.setBaseHP(Math.min(boss.getBaseHP() + buffAmount, (bossClass.getMaxHP() - bossClass.getBaseHP()))); }
			if (buffStats.str) { boss.setBaseSTR(Math.min(boss.getBaseSTR() + buffAmount, (bossClass.getMaxSTR() - bossClass.getBaseSTR()))); }
			if (buffStats.skl) { boss.setBaseSKL(Math.min(boss.getBaseSKL() + buffAmount, (bossClass.getMaxSKL() - bossClass.getBaseSKL()))); }
			if (buffStats.spd) { boss.setBaseSPD(Math.min(boss.getBaseSPD() + buffAmount, (bossClass.getMaxSPD() - bossClass.getBaseSPD()))); }
			if (buffStats.def) { boss.setBaseDEF(Math.min(boss.getBaseDEF() + buffAmount, (bossClass.getMaxDEF() - bossClass.getBaseDEF()))); }
			if (buffStats.res) { boss.setBaseRES(Math.min(boss.getBaseRES() + buffAmount, (bossClass.getMaxRES() - bossClass.getBaseRES()))); }
			if (buffStats.lck) { boss.setBaseLCK(Math.min(boss.getBaseLCK() + buffAmount, (bossClass.getMaxLCK() - bossClass.getBaseLCK()))); }
		}
	}
	
	public static void improveMinionWeapons(int probability, CharacterDataLoader charactersData, 
			ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, Random rng) {
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				if (charactersData.isBossCharacterID(chapterUnit.getCharacterNumber())) {
					// Bosses get their own routine.
					continue;
				}
				int leaderID = chapterUnit.getLeaderID();
				if (charactersData.isBossCharacterID(leaderID) || (chapterUnit.isEnemy() && chapterUnit.isAutolevel())) {
					GBAFEClassData originalClass = classData.classForID(chapterUnit.getStartingClass());
					if (originalClass == null) {
						continue;
					}
					
					GBAFECharacterData minionCharacterData = charactersData.characterWithID(chapterUnit.getCharacterNumber());
					boolean characterHasWeaponRanks = !itemData.ranksForCharacter(minionCharacterData, null).getTypes().isEmpty();
					// Recruitable enemies can get a weapon upgrade, but they should not clear their weapon ranks!
					if (characterHasWeaponRanks && charactersData.isPlayableCharacterID(chapterUnit.getCharacterNumber()) == false) {
						minionCharacterData.clearAllWeaponRanks();
						minionCharacterData.commitChanges();
					}
					
					chapterUnit.setAutolevel(true);
					
					if (rng.nextInt(100) < probability) {
						upgradeWeapons(chapterUnit, classData, itemData, rng);
					}
				}
			}
		}
		
//		GBAFEClassData[] allClasses = classData.allClasses();
//		for (GBAFEClassData currentClass : allClasses) {
//			if (currentClass.getSwordRank() > 0) { currentClass.setSwordRank(WeaponRank.A); }
//			if (currentClass.getLanceRank() > 0) { currentClass.setLanceRank(WeaponRank.A); }
//			if (currentClass.getAxeRank() > 0) { currentClass.setAxeRank(WeaponRank.A); }
//			if (currentClass.getBowRank() > 0) { currentClass.setBowRank(WeaponRank.A); }
//			if (currentClass.getAnimaRank() > 0) { currentClass.setAnimaRank(WeaponRank.A); }
//			if (currentClass.getDarkRank() > 0) { currentClass.setDarkRank(WeaponRank.A); }
//			if (currentClass.getLightRank() > 0) { currentClass.setLightRank(WeaponRank.A); }
//			if (currentClass.getStaffRank() > 0) { currentClass.setStaffRank(WeaponRank.A); }
//		}
	}
	
	public static void improveBossWeapons(int probability, CharacterDataLoader charactersData, 
			ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, Random rng) {
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				if (!charactersData.isBossCharacterID(chapterUnit.getCharacterNumber())) { continue; }
				if (rng.nextInt(100) < probability) {
					upgradeWeapons(chapterUnit, classData, itemData, rng);
				}
				GBAFECharacterData character = charactersData.characterWithID(chapterUnit.getCharacterNumber());
				if (character.getSwordRank() > 0) { character.setSwordRank(itemData.getHighestWeaponRank()); }
				if (character.getLanceRank() > 0) { character.setLanceRank(itemData.getHighestWeaponRank()); }
				if (character.getAxeRank() > 0) { character.setAxeRank(itemData.getHighestWeaponRank()); }
				if (character.getBowRank() > 0) { character.setBowRank(itemData.getHighestWeaponRank()); }
				if (character.getAnimaRank() > 0) { character.setAnimaRank(itemData.getHighestWeaponRank()); }
				if (character.getLightRank() > 0) { character.setLightRank(itemData.getHighestWeaponRank()); }
				if (character.getDarkRank() > 0) { character.setDarkRank(itemData.getHighestWeaponRank()); }
				if (character.getStaffRank() > 0) { character.setStaffRank(itemData.getHighestWeaponRank()); }
			}
		}
	}
	
	private static void upgradeWeapons(GBAFEChapterUnitData unit, ClassDataLoader classData, ItemDataLoader itemData, Random rng) {
		GBAFEClassData unitClass = classData.classForID(unit.getStartingClass());
		int item1ID = unit.getItem1();
		GBAFEItemData item1 = itemData.itemWithID(item1ID);
		if (item1 != null && item1.getType() != WeaponType.NOT_A_WEAPON && item1.getWeaponRank() != WeaponRank.A) {
			GBAFEItemData[] improvedItems = availableItems(unitClass, item1, itemData);
			if (improvedItems.length > 0) {
				GBAFEItemData replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem1(replacementItem.getID());
			}
		}
		
		int item2ID = unit.getItem2();
		GBAFEItemData item2 = itemData.itemWithID(item2ID);
		if (item2 != null && item2.getType() != WeaponType.NOT_A_WEAPON && item2.getWeaponRank() != WeaponRank.A) {
			GBAFEItemData[] improvedItems = availableItems(unitClass, item2, itemData);
			if (improvedItems.length > 0) {
				GBAFEItemData replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem2(replacementItem.getID());
			}
		}
		
		int item3ID = unit.getItem3();
		GBAFEItemData item3 = itemData.itemWithID(item3ID);
		if (item3 != null && item3.getType() != WeaponType.NOT_A_WEAPON && item3.getWeaponRank() != WeaponRank.A) {
			GBAFEItemData[] improvedItems = availableItems(unitClass, item3, itemData);
			if (improvedItems.length > 0) {
				GBAFEItemData replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem3(replacementItem.getID());
			}
		}
		
		int item4ID = unit.getItem4();
		GBAFEItemData item4 = itemData.itemWithID(item4ID);
		if (item4 != null && item4.getType() != WeaponType.NOT_A_WEAPON && item4.getWeaponRank() != WeaponRank.A) {
			GBAFEItemData[] improvedItems = availableItems(unitClass, item4, itemData);
			if (improvedItems.length > 0) {
				GBAFEItemData replacementItem = improvedItems[rng.nextInt(improvedItems.length)];
				unit.setItem4(replacementItem.getID());
			}
		}
	}
	
	private static GBAFEItemData[] availableItems(GBAFEClassData characterClass, GBAFEItemData original, ItemDataLoader itemData) {
		WeaponRank rank = WeaponRank.nextRankHigherThanRank(original.getWeaponRank());
		WeaponType type = original.getType();
		
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
		
		GBAFEItemData[] sameRank = itemData.itemsOfTypeAndEqualRank(type, original.getWeaponRank(), false, false, false);
		for (GBAFEItemData weapon : sameRank) {
			if (weapon.getMight() > original.getMight()) {
				items.add(weapon);
			}
		}
		
		List<GBAFEItemData> filteredList = items.stream().filter(item -> (!itemData.isPlayerOnly(item.getID()))).collect(Collectors.toList());
		
		return filteredList.toArray(new GBAFEItemData[filteredList.size()]);
	}
}
