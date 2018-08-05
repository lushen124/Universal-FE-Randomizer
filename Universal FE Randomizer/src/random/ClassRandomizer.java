package random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fedata.FEChapter;
import fedata.FEChapterUnit;
import fedata.FECharacter;
import fedata.FEClass;
import fedata.FEItem;
import fedata.general.WeaponType;
import util.WhyDoesJavaNotHaveThese;

public class ClassRandomizer {
	
	static final int rngSalt = 874;
	
	public static void randomizeClassMovement(int minMOV, int maxMOV, ClassDataLoader classData, Random rng) {
		FEClass[] allClasses = classData.allClasses();
		for (FEClass currentClass : allClasses) {
			if (currentClass.getMOV() > 0) {
				int randomMOV = rng.nextInt(maxMOV - minMOV) + minMOV;
				currentClass.setMOV(randomMOV);
			}
		}
	}
	
	public static void randomizePlayableCharacterClasses(Boolean includeLords, Boolean includeThieves, CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, PaletteLoader paletteData, Random rng) {
		FECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		Map<Integer, FEClass> determinedClasses = new HashMap<Integer, FEClass>();
		
		for (FECharacter character : allPlayableCharacters) {
			
			Boolean characterRequiresRange = charactersData.characterIDRequiresRange(character.getID());
			
			int originalClassID = character.getClassID();
			FEClass originalClass = classData.classForID(originalClassID);
			
			FEClass targetClass = null;
			
			if (determinedClasses.containsKey(character.getID())) {
				continue;
			} else {
				FEClass[] possibleClasses = classData.potentialClasses(originalClass, !includeLords, !includeThieves, true, charactersData.isLordCharacterID(character.getID()), characterRequiresRange, character.isClassRestricted());
				if (possibleClasses.length == 0) {
					continue;
				}
			
				int randomIndex = rng.nextInt(possibleClasses.length);
				targetClass = possibleClasses[randomIndex];
			}
			
			if (targetClass == null) {
				continue;
			}
			
			for (FECharacter linked : charactersData.linkedCharactersForCharacter(character)) {
				determinedClasses.put(linked.getID(), targetClass);
				updateCharacterToClass(linked, originalClass, targetClass, characterRequiresRange, classData, chapterData, itemData, rng);
			}
				
			Boolean hasOriginalPromotionData = false;
			int originalPromoClassID = 0;
			if (!classData.isPromotedClass(originalClass.getID())) {
				int promotedClassID = originalClass.getTargetPromotionID();
				if (classData.isValidClass(promotedClassID)) {
					hasOriginalPromotionData = true;
					originalPromoClassID = promotedClassID;
				}
			}
			
			Boolean hasTargetPromotionData = false;
			int targetPromoClassID = 0;
			if (!classData.isPromotedClass(targetClass.getID())) {
				int promotedClassID = targetClass.getTargetPromotionID();
				if (classData.isValidClass(promotedClassID)) {
					hasTargetPromotionData = true;
					targetPromoClassID = promotedClassID;
				}
			}
			paletteData.adaptCharacterToClass(charactersData.getCanonicalIDForCharacter(character), originalClass.getID(), hasOriginalPromotionData ? originalPromoClassID : 0, targetClass.getID(), hasTargetPromotionData ? targetPromoClassID : 0);
		}
	}
	
	public static void randomizeBossCharacterClasses(CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, PaletteLoader paletteData, Random rng) {
		FECharacter[] allBossCharacters = charactersData.bossCharacters();
		
		Boolean includeLords = true;
		Boolean includeThieves = true;
		Map<Integer, FEClass> determinedClasses = new HashMap<Integer, FEClass>();
		
		for (FECharacter character : allBossCharacters) {
			
			Boolean characterRequiresRange = charactersData.characterIDRequiresRange(character.getID());
			
			int originalClassID = character.getClassID();
			FEClass originalClass = classData.classForID(originalClassID);
			if (originalClass == null) {
				System.err.println("Invalid Class found: Class ID = " + Integer.toHexString(originalClassID));
				continue;
			}
			
			FEClass targetClass = null;
			
			if (determinedClasses.containsKey(character.getID())) {
				continue;
			} else {			
				FEClass[] possibleClasses = classData.potentialClasses(originalClass, !includeLords, !includeThieves, true, true, characterRequiresRange, false);
				if (possibleClasses.length == 0) {
					continue;
				}
			
				int randomIndex = rng.nextInt(possibleClasses.length);
				targetClass = possibleClasses[randomIndex];
			}
			
			if (targetClass == null) {
				continue;
			}
			
			for (FECharacter linked : charactersData.linkedCharactersForCharacter(character)) {
				determinedClasses.put(linked.getID(), targetClass);
				updateCharacterToClass(linked, originalClass, targetClass, characterRequiresRange, classData, chapterData, itemData, rng);
			}
			
			paletteData.adaptCharacterToClass(charactersData.getCanonicalIDForCharacter(character), originalClass.getID(), 0, targetClass.getID(), 0);
		}
	}
	
	public static void randomizeMinionClasses(CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, Random rng) {
		for (FEChapter chapter : chapterData.allChapters()) {
			for (FEChapterUnit chapterUnit : chapter.allUnits()) {
				if (!chapterUnit.isModifiable()) {
					continue;
				}
				
				int leaderID = chapterUnit.getLeaderID();
				int characterID = chapterUnit.getCharacterNumber();
				if (charactersData.isBossCharacterID(leaderID) && !charactersData.isPlayableCharacterID(characterID)) {
					FEClass originalClass = classData.classForID(chapterUnit.getStartingClass());
					if (originalClass == null) {
						continue;
					}
					
					if (classData.isThief(originalClass.getID())) {
						continue;
					}
					
					Boolean shouldRestrictToSafeClasses = !chapter.isClassSafe();
					FEClass[] possibleClasses = classData.potentialClasses(originalClass, false, false, false, true, false, shouldRestrictToSafeClasses);
					if (possibleClasses.length == 0) {
						continue;
					}
					
					int randomIndex = rng.nextInt(possibleClasses.length);
					FEClass targetClass = possibleClasses[randomIndex];
					
					updateMinionToClass(chapterUnit, targetClass, classData, itemData, rng);
				}
			}
		}
	}

	private static void updateCharacterToClass(FECharacter character, FEClass sourceClass, FEClass targetClass, Boolean ranged, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, Random rng) {
		
		character.prepareForClassRandomization();
		character.setClassID(targetClass.getID());
		transferWeaponLevels(character, sourceClass, targetClass, rng);
		applyBaseCorrectionForCharacter(character, sourceClass, targetClass);
		
		for (FEChapter chapter : chapterData.allChapters()) {
			for (FEChapterUnit chapterUnit : chapter.allUnits()) {
				if (chapterUnit.getCharacterNumber() == character.getID()) {
					if (chapterUnit.getStartingClass() != sourceClass.getID()) {
						System.err.println("Class mismatch for character with ID " + character.getID() + ". Expected Class " + sourceClass.getID() + " but found " + chapterUnit.getStartingClass() + " in chapter with offset " + Long.toHexString(chapter.getAddressOffset()));
					}
					chapterUnit.setStartingClass(targetClass.getID());
					validateCharacterInventory(character, chapterUnit, ranged, itemData, rng);
					if (classData.isThief(sourceClass.getID())) {
						validateFormerThiefInventory(chapterUnit, itemData);
					}
					validateSpecialClassInventory(character, chapterUnit, itemData, rng);
				}
			}
		}
	}
	
	private static void applyBaseCorrectionForCharacter(FECharacter character, FEClass sourceClass, FEClass targetClass) {
		int hpDelta = sourceClass.getBaseHP() - targetClass.getBaseHP();
		character.setBaseHP(character.getBaseHP() + hpDelta);
		int strDelta = sourceClass.getBaseSTR() - targetClass.getBaseSTR();
		character.setBaseSTR(character.getBaseSTR() + strDelta);
		int sklDelta = sourceClass.getBaseSKL() - targetClass.getBaseSKL();
		character.setBaseSKL(character.getBaseSKL() + sklDelta);
		int spdDelta = sourceClass.getBaseSPD() - targetClass.getBaseSPD();
		character.setBaseSPD(character.getBaseSPD() + spdDelta);
		int defDelta = sourceClass.getBaseDEF() - targetClass.getBaseDEF();
		character.setBaseDEF(character.getBaseDEF() + defDelta);
		int resDelta = sourceClass.getBaseRES() - targetClass.getBaseRES();
		character.setBaseRES(character.getBaseRES() + resDelta);
	}
	
	private static void updateMinionToClass(FEChapterUnit chapterUnit, FEClass targetClass, ClassDataLoader classData, ItemDataLoader itemData, Random rng) {
		chapterUnit.setStartingClass(targetClass.getID());
		validateMinionInventory(chapterUnit, classData, itemData, rng);
	}
	
	private static void validateFormerThiefInventory(FEChapterUnit chapterUnit, ItemDataLoader itemData) {
		FEItem[] requiredItems = itemData.formerThiefInventory();
		if (requiredItems != null) {
			giveItemsToChapterUnit(chapterUnit, requiredItems);
		}
		
		FEItem[] thiefItemsToRemove = itemData.thiefItemsToRemove();
		for (FEItem item : thiefItemsToRemove) {
			chapterUnit.removeItem(item.getID());
		}
	}
	
	private static void validateSpecialClassInventory(FECharacter character, FEChapterUnit chapterUnit, ItemDataLoader itemData, Random rng) {
		FEItem[] requiredItems = itemData.specialInventoryForClass(chapterUnit.getStartingClass(), rng);
		if (requiredItems != null && requiredItems.length > 0) {
			giveItemsToChapterUnit(chapterUnit, requiredItems);
		}
	}
	
	private static void giveItemsToChapterUnit(FEChapterUnit chapterUnit, FEItem[] items) {
		int[] requiredItemIDs = new int[items.length];
		for (int i = 0; i < items.length; i++) {
			requiredItemIDs[i] = items[i].getID();
		}
		chapterUnit.giveItems(requiredItemIDs);
	}
	
	private static void validateMinionInventory(FEChapterUnit chapterUnit, ClassDataLoader classData, ItemDataLoader itemData, Random rng) {
		int classID = chapterUnit.getStartingClass();
		FEClass unitClass = classData.classForID(classID);
		if (unitClass != null) {
			int item1ID = chapterUnit.getItem1();
			FEItem item1 = itemData.itemWithID(item1ID);
			if (item1 != null && item1.getType() != WeaponType.NOT_A_WEAPON) {
				if (!canClassUseItem(unitClass, item1)) {
					FEItem replacementItem = getSidegradeWeapon(unitClass, item1, itemData, rng);
					if (replacementItem != null) {
						chapterUnit.setItem1(replacementItem.getID());
					} else {
						chapterUnit.setItem1(0);
					}
				}
			}
			
			int item2ID = chapterUnit.getItem2();
			FEItem item2 = itemData.itemWithID(item2ID);
			if (item2 != null && item2.getType() != WeaponType.NOT_A_WEAPON) {
				if (!canClassUseItem(unitClass, item2)) {
					FEItem replacementItem = getSidegradeWeapon(unitClass, item2, itemData, rng);
					if (replacementItem != null) {
						chapterUnit.setItem2(replacementItem.getID());
					} else {
						chapterUnit.setItem2(0);
					}
				}
			}
			
			int item3ID = chapterUnit.getItem3();
			FEItem item3 = itemData.itemWithID(item3ID);
			if (item3 != null && item3.getType() != WeaponType.NOT_A_WEAPON) {
				if (!canClassUseItem(unitClass, item3)) {
					FEItem replacementItem = getSidegradeWeapon(unitClass, item3, itemData, rng);
					if (replacementItem != null) {
						chapterUnit.setItem3(replacementItem.getID());
					} else {
						chapterUnit.setItem3(0);
					}
				}
			}
			
			int item4ID = chapterUnit.getItem4();
			FEItem item4 = itemData.itemWithID(item4ID);
			if (item4 != null && item4.getType() != WeaponType.NOT_A_WEAPON) {
				if (!canClassUseItem(unitClass, item4)) {
					FEItem replacementItem = getSidegradeWeapon(unitClass, item4, itemData, rng);
					if (replacementItem != null) {
						chapterUnit.setItem4(replacementItem.getID());
					} else {
						chapterUnit.setItem4(0);
					}
				}
			}
		}
	}
	
	private static void validateCharacterInventory(FECharacter character, FEChapterUnit chapterUnit, Boolean ranged, ItemDataLoader itemData, Random rng) {
		int item1ID = chapterUnit.getItem1();
		FEItem item1 = itemData.itemWithID(item1ID);
		if (item1 != null && item1.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item1)) {
				FEItem replacementItem = getRandomWeaponForCharacter(character, ranged, itemData, rng);
				if (replacementItem != null) {
					chapterUnit.setItem1(replacementItem.getID());
				} else {
					chapterUnit.setItem1(0);
				}
			}
		}
		
		int item2ID = chapterUnit.getItem2();
		FEItem item2 = itemData.itemWithID(item2ID);
		if (item2 != null && item2.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item2)) {
				FEItem replacementItem = getRandomWeaponForCharacter(character, ranged, itemData, rng);
				if (replacementItem != null) {
					chapterUnit.setItem2(replacementItem.getID());
				} else {
					chapterUnit.setItem2(0);
				}
			}
		}
		
		int item3ID = chapterUnit.getItem3();
		FEItem item3 = itemData.itemWithID(item3ID);
		if (item3 != null && item3.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item3)) {
				FEItem replacementItem = getRandomWeaponForCharacter(character, ranged, itemData, rng);
				if (replacementItem != null) {
					chapterUnit.setItem3(replacementItem.getID());
				} else {
					chapterUnit.setItem3(0);
				}
			}
		}
		
		int item4ID = chapterUnit.getItem4();
		FEItem item4 = itemData.itemWithID(item4ID);
		if (item4 != null && item4.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item4)) {
				FEItem replacementItem = getRandomWeaponForCharacter(character, ranged, itemData, rng);
				if (replacementItem != null) {
					chapterUnit.setItem4(replacementItem.getID());
				} else {
					chapterUnit.setItem4(0);
				}
			}
		}
	}
	
	private static FEItem getRandomWeaponForCharacter(FECharacter character, Boolean ranged, ItemDataLoader itemData, Random rng) {
		FEItem[] potentialItems = usableWeaponsForCharacter(character, ranged, itemData);
		if (potentialItems == null || potentialItems.length < 1) {
			return null;
		}
		
		int index = rng.nextInt(potentialItems.length);
		return potentialItems[index];
	}
	
	private static FEItem getSidegradeWeapon(FEClass targetClass, FEItem originalWeapon, ItemDataLoader itemData, Random rng) {
		if (originalWeapon.getType() == WeaponType.NOT_A_WEAPON) {
			return null;
		}
		
		FEItem[] potentialItems = comparableWeaponsForClass(targetClass, originalWeapon, itemData);
		if (potentialItems == null || potentialItems.length < 1) {
			return null;
		}
		
		int index = rng.nextInt(potentialItems.length);
		return potentialItems[index];
	}
	
	private static FEItem[] usableWeaponsForCharacter(FECharacter character, Boolean ranged, ItemDataLoader itemData) {
		ArrayList<FEItem> items = new ArrayList<FEItem>();
		
		if (character.getSwordRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.SWORD, character.getSwordRank(), ranged))); }
		if (character.getLanceRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.LANCE, character.getLanceRank(), ranged))); }
		if (character.getAxeRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.AXE, character.getAxeRank(), ranged))); }
		if (character.getBowRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.BOW, character.getBowRank(), ranged))); }
		if (character.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.ANIMA, character.getAnimaRank(), ranged))); }
		if (character.getLightRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.LIGHT, character.getLightRank(), ranged))); }
		if (character.getDarkRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.DARK, character.getDarkRank(), ranged))); }
		if (character.getStaffRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.STAFF, character.getStaffRank(), ranged))); }
		
		return items.toArray(new FEItem[items.size()]);
	}
	
	private static FEItem[] comparableWeaponsForClass(FEClass characterClass, FEItem referenceItem, ItemDataLoader itemData) {
		ArrayList<FEItem> items = new ArrayList<FEItem>();
		
		if (characterClass.getSwordRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.SWORD, referenceItem.getWeaponRank(), false, true))); }
		if (characterClass.getLanceRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.LANCE, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true))); }
		if (characterClass.getAxeRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.AXE, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true))); }
		if (characterClass.getBowRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.BOW, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true))); }
		if (characterClass.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.ANIMA, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true))); }
		if (characterClass.getLightRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.LIGHT, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true))); }
		if (characterClass.getDarkRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.DARK, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true))); }
		if (characterClass.getStaffRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.STAFF, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true))); }
		
		FEItem[] prfWeapons = itemData.prfWeaponsForClass(characterClass.getID());
		if (prfWeapons != null) {
			items.addAll(Arrays.asList(prfWeapons));
		}
		
		FEItem[] classWeapons = itemData.lockedWeaponsToClass(characterClass.getID());
		if (classWeapons != null) {
			items.addAll(Arrays.asList(classWeapons));
		}
		
		return items.toArray(new FEItem[items.size()]);
	}
	
	private static Boolean canCharacterUseItem(FECharacter character, FEItem weapon) {
		if ((weapon.getType() == WeaponType.SWORD && character.getSwordRank() > 0) ||
				(weapon.getType() == WeaponType.LANCE && character.getLanceRank() > 0) ||
				(weapon.getType() == WeaponType.AXE && character.getAxeRank() > 0) ||
				(weapon.getType() == WeaponType.BOW && character.getBowRank() > 0) ||
				(weapon.getType() == WeaponType.ANIMA && character.getAnimaRank() > 0) ||
				(weapon.getType() == WeaponType.LIGHT && character.getLightRank() > 0) ||
				(weapon.getType() == WeaponType.DARK && character.getDarkRank() > 0) ||
				(weapon.getType() == WeaponType.STAFF && character.getStaffRank() > 0)) {
			return true;
		}
		
		return false;
	}
	
	private static Boolean canClassUseItem(FEClass characterClass, FEItem weapon) {
		if ((weapon.getType() == WeaponType.SWORD && characterClass.getSwordRank() > 0) ||
				(weapon.getType() == WeaponType.LANCE && characterClass.getLanceRank() > 0) ||
				(weapon.getType() == WeaponType.AXE && characterClass.getAxeRank() > 0) ||
				(weapon.getType() == WeaponType.BOW && characterClass.getBowRank() > 0) ||
				(weapon.getType() == WeaponType.ANIMA && characterClass.getAnimaRank() > 0) ||
				(weapon.getType() == WeaponType.LIGHT && characterClass.getLightRank() > 0) ||
				(weapon.getType() == WeaponType.DARK && characterClass.getDarkRank() > 0) ||
				(weapon.getType() == WeaponType.STAFF && characterClass.getStaffRank() > 0)) {
			return true;
		}
		
		return false;
	}
	
	private static void transferWeaponLevels(FECharacter character, FEClass sourceClass, FEClass targetClass, Random rng) {
		ArrayList<Integer> ranks = new ArrayList<Integer>();
		
		if (character.getSwordRank() > 0) { ranks.add(character.getSwordRank()); }
		if (character.getLanceRank() > 0) { ranks.add(character.getLanceRank()); }
		if (character.getAxeRank() > 0) { ranks.add(character.getAxeRank()); }
		if (character.getBowRank() > 0) { ranks.add(character.getBowRank()); }
		if (character.getAnimaRank() > 0) { ranks.add(character.getAnimaRank()); }
		if (character.getLightRank() > 0) { ranks.add(character.getLightRank()); }
		if (character.getDarkRank() > 0) { ranks.add(character.getDarkRank()); }
		if (character.getStaffRank() > 0) { ranks.add(character.getStaffRank()); }
		
		Collections.sort(ranks);
		
		Boolean applySwordRank = targetClass.getSwordRank() > 0;
		Boolean applyLanceRank = targetClass.getLanceRank() > 0;
		Boolean applyAxeRank = targetClass.getAxeRank() > 0;
		Boolean applyBowRank = targetClass.getBowRank() > 0;
		Boolean applyAnimaRank = targetClass.getAnimaRank() > 0;
		Boolean applyLightRank = targetClass.getLightRank() > 0;
		Boolean applyDarkRank = targetClass.getDarkRank() > 0;
		Boolean applyStaffRank = targetClass.getStaffRank() > 0;
		
		int[] targetRanks = new int[] { 0, 0, 0, 0, 0, 0, 0, 0};
		
		if (applySwordRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (rng.nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[0] = rankToApply;
		}
		if (applyLanceRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (rng.nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[1] = rankToApply;
		}
		if (applyAxeRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (rng.nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[2] = rankToApply;
		}
		if (applyBowRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (rng.nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[3] = rankToApply;
		}
		if (applyAnimaRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (rng.nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[4] = rankToApply;
		}
		if (applyLightRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (rng.nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[5] = rankToApply;
		}
		if (applyDarkRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (rng.nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[6] = rankToApply;
		}
		if (applyStaffRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (rng.nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[7] = rankToApply;
		}
		
		character.setSwordRank(targetRanks[0]);
		character.setLanceRank(targetRanks[1]);
		character.setAxeRank(targetRanks[2]);
		character.setBowRank(targetRanks[3]);
		character.setAnimaRank(targetRanks[4]);
		character.setLightRank(targetRanks[5]);
		character.setDarkRank(targetRanks[6]);
		character.setStaffRank(targetRanks[7]);
	}
}
