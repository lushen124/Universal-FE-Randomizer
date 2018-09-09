package random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.FEBase.GameType;
import fedata.FEChapter;
import fedata.FEChapterItem;
import fedata.FEChapterUnit;
import fedata.FECharacter;
import fedata.FEClass;
import fedata.FEItem;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;
import ui.model.ClassOptions;
import util.DebugPrinter;

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
	
	public static void randomizePlayableCharacterClasses(ClassOptions options, GameType type, CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, PaletteLoader paletteData, TextLoader textData, Random rng) {
		FECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		Map<Integer, FEClass> determinedClasses = new HashMap<Integer, FEClass>();
		
		Boolean includeLords = options.includeLords;
		Boolean includeThieves = options.includeThieves;
		Boolean hasMonsters = false;
		Boolean separateMonsters = false;
		if (type == GameType.FE8) {
			hasMonsters = true;
			separateMonsters = options.separateMonsters;
		}
		
		for (FECharacter character : allPlayableCharacters) {
			
			Boolean isLordCharacter = charactersData.isLordCharacterID(character.getID());
			Boolean characterRequiresRange = charactersData.characterIDRequiresRange(character.getID());
			Boolean characterRequiresMelee = charactersData.characterIDRequiresMelee(character.getID());
			
			int originalClassID = character.getClassID();
			FEClass originalClass = classData.classForID(originalClassID);
			
			FEClass targetClass = null;
			
			if (determinedClasses.containsKey(character.getID())) {
				continue;
			} else {
				FEClass[] possibleClasses = hasMonsters ? classData.potentialClasses(originalClass, !includeLords, !includeThieves, separateMonsters, true, isLordCharacter, characterRequiresRange, characterRequiresMelee, character.isClassRestricted(), null) :
					classData.potentialClasses(originalClass, !includeLords, !includeThieves, true, isLordCharacter, characterRequiresRange, characterRequiresMelee, character.isClassRestricted(), null);
				if (possibleClasses.length == 0) {
					continue;
				}
			
				int randomIndex = rng.nextInt(possibleClasses.length);
				targetClass = possibleClasses[randomIndex];
			}
			
			if (targetClass == null) {
				continue;
			}
			
			DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Assigning character 0x" + Integer.toHexString(character.getID()).toUpperCase() + " (" + textData.getStringAtIndex(character.getNameIndex()) + ") to class 0x" + Integer.toHexString(targetClass.getID()) + " (" + textData.getStringAtIndex(targetClass.getNameIndex()) + ")");
			
			for (FECharacter linked : charactersData.linkedCharactersForCharacter(character)) {
				determinedClasses.put(linked.getID(), targetClass);
				updateCharacterToClass(linked, originalClass, targetClass, characterRequiresRange, characterRequiresMelee, classData, chapterData, itemData, textData, false, rng);
				if (isLordCharacter) {
					linked.setIsLord();
				}
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
			
			if (type == GameType.FE8) {
				paletteData.adaptFE8CharacterToClass(character.getID(), originalClass.getID(), targetClass.getID(), false);
			} else {
				paletteData.adaptCharacterToClass(charactersData.getCanonicalIDForCharacter(character), originalClass.getID(), hasOriginalPromotionData ? originalPromoClassID : 0, targetClass.getID(), hasTargetPromotionData ? targetPromoClassID : 0);
			}
		}
		
		if (type == GameType.FE8) {
			paletteData.backfillFE8Palettes();
		}
	}
	
	public static void randomizeBossCharacterClasses(ClassOptions options, GameType type, CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, PaletteLoader paletteData, TextLoader textData, Random rng) {
		FECharacter[] allBossCharacters = charactersData.bossCharacters();
		
		Boolean includeLords = false;
		Boolean includeThieves = false;
		Boolean hasMonsters = false;
		Boolean separateMonsters = false;
		if (type == GameType.FE8) {
			hasMonsters = true;
			separateMonsters = options.separateMonsters;
		}
		
		Map<Integer, FEClass> determinedClasses = new HashMap<Integer, FEClass>();
		
		for (FECharacter character : allBossCharacters) {
			
			Boolean characterRequiresRange = charactersData.characterIDRequiresRange(character.getID());
			Boolean characterRequiresMelee = charactersData.characterIDRequiresMelee(character.getID());
			
			int originalClassID = character.getClassID();
			FEClass originalClass = classData.classForID(originalClassID);
			if (originalClass == null) {
				System.err.println("Invalid Class found: Class ID = " + Integer.toHexString(originalClassID));
				continue;
			}
			
			FEClass targetClass = null;
			
			Boolean forceBasicWeaponry = false;
			
			if (determinedClasses.containsKey(character.getID())) {
				continue;
			} else {			
				FECharacter mustLoseToCharacter = charactersData.characterRequiresCounterToCharacter(character);
				FEClass mustLoseToClass = null;
				if (mustLoseToCharacter != null) {
					mustLoseToClass = classData.classForID(mustLoseToCharacter.getClassID());
					forceBasicWeaponry = true;
				}
				
				FEClass[] possibleClasses = hasMonsters ? 
						classData.potentialClasses(originalClass, !includeLords, !includeThieves, separateMonsters, true, true, characterRequiresRange, characterRequiresMelee, character.isClassRestricted(), mustLoseToClass) :
					classData.potentialClasses(originalClass, !includeLords, !includeThieves, true, true, characterRequiresRange, characterRequiresMelee, character.isClassRestricted(), mustLoseToClass);
				if (possibleClasses.length == 0) {
					continue;
				}
			
				int randomIndex = rng.nextInt(possibleClasses.length);
				targetClass = possibleClasses[randomIndex];
			}
			
			if (targetClass == null) {
				continue;
			}
			
			if (character.getID() == 0x46) {
				System.out.println("Debugging Breguet");
			}
			
			for (FECharacter linked : charactersData.linkedCharactersForCharacter(character)) {
				determinedClasses.put(linked.getID(), targetClass);
				updateCharacterToClass(linked, originalClass, targetClass, characterRequiresRange, characterRequiresMelee, classData, chapterData, itemData, textData, forceBasicWeaponry && linked.getID() == character.getID(), rng);
			}
			
			if (type == GameType.FE8) {
				paletteData.adaptFE8CharacterToClass(charactersData.getCanonicalIDForCharacter(character), originalClass.getID(), targetClass.getID(), true);
			} else {
				paletteData.adaptCharacterToClass(charactersData.getCanonicalIDForCharacter(character), originalClass.getID(), 0, targetClass.getID(), 0);
			}
		}
	}
	
	public static void randomizeMinionClasses(ClassOptions options, GameType type, CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, Random rng) {
		Boolean includeLords = false;
		Boolean includeThieves = false;
		Boolean hasMonsters = false;
		Boolean separateMonsters = false;
		if (type == GameType.FE8) {
			hasMonsters = true;
			separateMonsters = options.separateMonsters;
		}
		
		for (FEChapter chapter : chapterData.allChapters()) {
			FECharacter lordCharacter = charactersData.characterWithID(chapter.lordLeaderID());
			FEClass lordClass = classData.classForID(lordCharacter.getClassID());
			for (FEChapterUnit chapterUnit : chapter.allUnits()) {
				int leaderID = chapterUnit.getLeaderID();
				int characterID = chapterUnit.getCharacterNumber();
				// It's safe to check for boss leader ID in the case of FE7, but FE6 tends to put other IDs there (kind of like squad captains).
				// We're going to remove this safety check in the meantime, but we should be wary of any accidental changes.
				if (!charactersData.isBossCharacterID(characterID) && /*charactersData.isBossCharacterID(leaderID) &&*/ !charactersData.isPlayableCharacterID(characterID)) {
					FEClass originalClass = classData.classForID(chapterUnit.getStartingClass());
					if (originalClass == null) {
						continue;
					}
					
					if (classData.isThief(originalClass.getID())) {
						continue;
					}
					
					Boolean shouldRestrictToSafeClasses = !chapter.isClassSafe();
					Boolean shouldMakeEasy = chapter.shouldBeSimplified();
					FEClass loseToClass = shouldMakeEasy ? lordClass : null;
					FEClass[] possibleClasses = hasMonsters ? 
							classData.potentialClasses(originalClass, !includeLords, !includeThieves, separateMonsters, false, true, false, false, shouldRestrictToSafeClasses, loseToClass) :
						classData.potentialClasses(originalClass, false, false, false, true, false, false, shouldRestrictToSafeClasses, loseToClass);
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

	private static void updateCharacterToClass(FECharacter character, FEClass sourceClass, FEClass targetClass, Boolean ranged, Boolean melee, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, TextLoader textData, Boolean forceBasicWeapons, Random rng) {
		
		character.prepareForClassRandomization();
		character.setClassID(targetClass.getID());
		transferWeaponLevels(character, sourceClass, targetClass, rng);
		applyBaseCorrectionForCharacter(character, sourceClass, targetClass);
		
		for (FEChapter chapter : chapterData.allChapters()) {
			FEChapterItem reward = chapter.chapterItemGivenToCharacter(character.getID());
			if (reward != null) {
				reward.setItemID(getRandomWeaponForCharacter(character, ranged, melee, itemData, rng).getID());
			}
			
			for (FEChapterUnit chapterUnit : chapter.allUnits()) {
				if (chapterUnit.getCharacterNumber() == character.getID()) {
					if (chapterUnit.getStartingClass() != sourceClass.getID()) {
						System.err.println("Class mismatch for character with ID " + character.getID() + ". Expected Class " + sourceClass.getID() + " but found " + chapterUnit.getStartingClass());
					}
					chapterUnit.setStartingClass(targetClass.getID());
					validateCharacterInventory(character, targetClass, chapterUnit, ranged, melee, itemData, textData, forceBasicWeapons, rng);
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
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Updating minion from class 0x" + Integer.toHexString(chapterUnit.getStartingClass()) + " to class 0x" + Integer.toHexString(targetClass.getID()));
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Starting Inventory: [0x" + Integer.toHexString(chapterUnit.getItem1()) + ", 0x" + Integer.toHexString(chapterUnit.getItem2()) + ", 0x" + Integer.toHexString(chapterUnit.getItem3()) + ", 0x" + Integer.toHexString(chapterUnit.getItem4()) + "]");
		chapterUnit.setStartingClass(targetClass.getID());
		validateMinionInventory(chapterUnit, classData, itemData, rng);
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Minion update complete. Inventory: [0x" + Integer.toHexString(chapterUnit.getItem1()) + ", 0x" + Integer.toHexString(chapterUnit.getItem2()) + ", 0x" + Integer.toHexString(chapterUnit.getItem3()) + ", 0x" + Integer.toHexString(chapterUnit.getItem4()) + "]");
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
			if (item1 != null && item1.getType() != WeaponType.NOT_A_WEAPON && item1.getWeaponRank() != WeaponRank.NONE) {
				if (!canClassUseItem(unitClass, item1, itemData)) {
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
			if (item2 != null && item2.getType() != WeaponType.NOT_A_WEAPON && item2.getWeaponRank() != WeaponRank.NONE) {
				if (!canClassUseItem(unitClass, item2, itemData)) {
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
			if (item3 != null && item3.getType() != WeaponType.NOT_A_WEAPON && item3.getWeaponRank() != WeaponRank.NONE) {
				if (!canClassUseItem(unitClass, item3, itemData)) {
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
			if (item4 != null && item4.getType() != WeaponType.NOT_A_WEAPON && item4.getWeaponRank() != WeaponRank.NONE) {
				if (!canClassUseItem(unitClass, item4, itemData)) {
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
	
	private static void validateCharacterInventory(FECharacter character, FEClass charClass, FEChapterUnit chapterUnit, Boolean ranged, Boolean melee, ItemDataLoader itemData, TextLoader textData, Boolean forceBasic, Random rng) {
		int item1ID = chapterUnit.getItem1();
		FEItem item1 = itemData.itemWithID(item1ID);
		int item2ID = chapterUnit.getItem2();
		FEItem item2 = itemData.itemWithID(item2ID);
		int item3ID = chapterUnit.getItem3();
		FEItem item3 = itemData.itemWithID(item3ID);
		int item4ID = chapterUnit.getItem4();
		FEItem item4 = itemData.itemWithID(item4ID);
		
		FEItem[] prfWeapons = itemData.prfWeaponsForClass(character.getClassID());
		Set<Integer> prfIDs = new HashSet<Integer>();
		for (FEItem prfWeapon : prfWeapons) {
			prfIDs.add(prfWeapon.getID());
		}
		
		Boolean isHealerClass = charClass.getStaffRank() > 0;
		Boolean hasAtLeastOneHealingStaff = false;
		
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Validating inventory for character 0x" + Integer.toHexString(character.getID()) + " (" + textData.getStringAtIndex(character.getNameIndex()) +") in class 0x" + Integer.toHexString(charClass.getID()) + " (" + textData.getStringAtIndex(charClass.getNameIndex()) + ")");
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Original Inventory: [0x" + Integer.toHexString(item1ID) + (item1 == null ? "" : " (" + textData.getStringAtIndex(item1.getNameIndex()) + ")") + ", 0x" + Integer.toHexString(item2ID) + (item2 == null ? "" : " (" + textData.getStringAtIndex(item2.getNameIndex()) + ")") + ", 0x" + Integer.toHexString(item3ID) + (item3 == null ? "" : " (" + textData.getStringAtIndex(item3.getNameIndex()) + ")") + ", 0x" + Integer.toHexString(item4ID) + (item4 == null ? "" : " (" + textData.getStringAtIndex(item4.getNameIndex()) + ")") + "]");
		
		if (item1 != null && item1.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item1, itemData) || (item1.getWeaponRank() == WeaponRank.PRF && !prfIDs.contains(item1ID))) {
				FEItem replacementItem = forceBasic ? getBasicWeaponForCharacter(character, ranged, itemData, rng) : getRandomWeaponForCharacter(character, ranged, melee, itemData, rng);
				if (item1.getWeaponRank() == WeaponRank.S) {
					FEItem[] topWeapons = topRankWeaponsForClass(charClass, itemData);
					if (topWeapons.length > 0) {
						replacementItem = topWeapons[rng.nextInt(topWeapons.length)];
					}
				}
				if (replacementItem != null) {
					if (replacementItem.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(replacementItem.getID()); }
					chapterUnit.setItem1(replacementItem.getID());
				} else {
					chapterUnit.setItem1(0);
				}
			}
		}
		
		if (item2 != null && item2.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item2, itemData) || (item2.getWeaponRank() == WeaponRank.PRF && !prfIDs.contains(item2ID))) {
				FEItem replacementItem = forceBasic ? getBasicWeaponForCharacter(character, ranged, itemData, rng) : getRandomWeaponForCharacter(character, ranged, melee, itemData, rng);
				if (item2.getWeaponRank() == WeaponRank.S) {
					FEItem[] topWeapons = topRankWeaponsForClass(charClass, itemData);
					if (topWeapons.length > 0) {
						replacementItem = topWeapons[rng.nextInt(topWeapons.length)];
					}
				}
				if (replacementItem != null) {
					if (replacementItem.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(replacementItem.getID()); }
					chapterUnit.setItem2(replacementItem.getID());
				} else {
					chapterUnit.setItem2(0);
				}
			}
		}
		
		if (item3 != null && item3.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item3, itemData) || (item3.getWeaponRank() == WeaponRank.PRF && !prfIDs.contains(item3ID))) {
				FEItem replacementItem = forceBasic ? getBasicWeaponForCharacter(character, ranged, itemData, rng) : getRandomWeaponForCharacter(character, ranged, melee, itemData, rng);
				if (item3.getWeaponRank() == WeaponRank.S) {
					FEItem[] topWeapons = topRankWeaponsForClass(charClass, itemData);
					if (topWeapons.length > 0) {
						replacementItem = topWeapons[rng.nextInt(topWeapons.length)];
					}
				}
				if (replacementItem != null) {
					if (replacementItem.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(replacementItem.getID()); }
					chapterUnit.setItem3(replacementItem.getID());
				} else {
					chapterUnit.setItem3(0);
				}
			}
		}
		
		if (item4 != null && item4.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item4, itemData) || (item4.getWeaponRank() == WeaponRank.PRF && !prfIDs.contains(item4ID))) {
				FEItem replacementItem = forceBasic ? getBasicWeaponForCharacter(character, ranged, itemData, rng) : getRandomWeaponForCharacter(character, ranged, melee, itemData, rng);
				if (item4.getWeaponRank() == WeaponRank.S) {
					FEItem[] topWeapons = topRankWeaponsForClass(charClass, itemData);
					if (topWeapons.length > 0) {
						replacementItem = topWeapons[rng.nextInt(topWeapons.length)];
					}
				}
				if (replacementItem != null) {
					if (replacementItem.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(replacementItem.getID()); }
					chapterUnit.setItem4(replacementItem.getID());
				} else {
					chapterUnit.setItem4(0);
				}
			}
		}
		
		if (isHealerClass && !hasAtLeastOneHealingStaff) {
			chapterUnit.giveItems(new int[] {itemData.getRandomHealingStaff(itemData.weaponRankFromValue(character.getStaffRank()), rng).getID()});
		}
		
		item1ID = chapterUnit.getItem1();
		item1 = itemData.itemWithID(item1ID);
		item2ID = chapterUnit.getItem2();
		item2 = itemData.itemWithID(item2ID);
		item3ID = chapterUnit.getItem3();
		item3 = itemData.itemWithID(item3ID);
		item4ID = chapterUnit.getItem4();
		item4 = itemData.itemWithID(item4ID);
		
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Final Inventory: [0x" + Integer.toHexString(item1ID) + (item1 == null ? "" : " (" + textData.getStringAtIndex(item1.getNameIndex()) + ")") + ", 0x" + Integer.toHexString(item2ID) + (item2 == null ? "" : " (" + textData.getStringAtIndex(item2.getNameIndex()) + ")") + ", 0x" + Integer.toHexString(item3ID) + (item3 == null ? "" : " (" + textData.getStringAtIndex(item3.getNameIndex()) + ")") + ", 0x" + Integer.toHexString(item4ID) + (item4 == null ? "" : " (" + textData.getStringAtIndex(item4.getNameIndex()) + ")") + "]");
	}
	
	private static FEItem getRandomWeaponForCharacter(FECharacter character, Boolean ranged, Boolean melee, ItemDataLoader itemData, Random rng) {
		FEItem[] potentialItems = usableWeaponsForCharacter(character, ranged, melee, itemData);
		if (potentialItems == null || potentialItems.length < 1) {
			return null;
		}
		
		int index = rng.nextInt(potentialItems.length);
		return potentialItems[index];
	}
	
	private static FEItem getBasicWeaponForCharacter(FECharacter character, Boolean ranged, ItemDataLoader itemData, Random rng) {
		if (character.getSwordRank() > 0) { return itemData.basicItemOfType(WeaponType.SWORD); }
		if (character.getLanceRank() > 0) { return itemData.basicItemOfType(WeaponType.LANCE); }
		if (character.getAxeRank() > 0) { return itemData.basicItemOfType(WeaponType.AXE); }
		if (character.getBowRank() > 0) { return itemData.basicItemOfType(WeaponType.BOW); }
		if (character.getAnimaRank() > 0) { return itemData.basicItemOfType(WeaponType.ANIMA); }
		if (character.getLightRank() > 0) { return itemData.basicItemOfType(WeaponType.LIGHT); }
		if (character.getDarkRank() > 0) { return itemData.basicItemOfType(WeaponType.DARK); }
		if (character.getStaffRank() > 0) { return itemData.basicItemOfType(WeaponType.STAFF); }
		
		return null;
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
	
	private static FEItem[] usableWeaponsForCharacter(FECharacter character, Boolean ranged, Boolean melee, ItemDataLoader itemData) {
		ArrayList<FEItem> items = new ArrayList<FEItem>();
		
		if (character.getSwordRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.SWORD, character.getSwordRank(), ranged, melee))); }
		if (character.getLanceRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.LANCE, character.getLanceRank(), ranged, melee))); }
		if (character.getAxeRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.AXE, character.getAxeRank(), ranged, melee))); }
		if (character.getBowRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.BOW, character.getBowRank(), ranged, melee))); }
		if (character.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.ANIMA, character.getAnimaRank(), ranged, melee))); }
		if (character.getLightRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.LIGHT, character.getLightRank(), ranged, melee))); }
		if (character.getDarkRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.DARK, character.getDarkRank(), ranged, melee))); }
		if (character.getStaffRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.STAFF, character.getStaffRank(), ranged, melee))); }
		
		return items.toArray(new FEItem[items.size()]);
	}
	
	private static FEItem[] topRankWeaponsForClass(FEClass characterClass, ItemDataLoader itemData) {
		ArrayList<FEItem> items = new ArrayList<FEItem>();
		if (characterClass.getSwordRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.SWORD, WeaponRank.S, false, false, true))); }
		if (characterClass.getLanceRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.LANCE, WeaponRank.S, false, false, true))); }
		if (characterClass.getAxeRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.AXE, WeaponRank.S, false, false, true))); }
		if (characterClass.getBowRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.BOW, WeaponRank.S, false, false, true))); }
		if (characterClass.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.ANIMA, WeaponRank.S, false, false, true))); }
		if (characterClass.getLightRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.LIGHT, WeaponRank.S, false, false, true))); }
		if (characterClass.getDarkRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.DARK, WeaponRank.S, false, false, true))); }
		if (characterClass.getStaffRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.STAFF, WeaponRank.S, false, false, true))); }
		
		return items.toArray(new FEItem[items.size()]);
	}
	
	private static FEItem[] comparableWeaponsForClass(FEClass characterClass, FEItem referenceItem, ItemDataLoader itemData) {
		ArrayList<FEItem> items = new ArrayList<FEItem>();
		
		if (characterClass.getSwordRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.SWORD, referenceItem.getWeaponRank(), false, true, true))); }
		if (characterClass.getLanceRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.LANCE, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getAxeRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.AXE, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getBowRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.BOW, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, false, true))); }
		if (characterClass.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.ANIMA, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getLightRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.LIGHT, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getDarkRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.DARK, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getStaffRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.STAFF, referenceItem.getWeaponRank(), false, false, true))); }
		
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
	
	private static Boolean canCharacterUseItem(FECharacter character, FEItem weapon, ItemDataLoader itemData) {
		int weaponRankValue = itemData.weaponRankValueForRank(weapon.getWeaponRank());
		if ((weapon.getType() == WeaponType.SWORD && character.getSwordRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.LANCE && character.getLanceRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.AXE && character.getAxeRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.BOW && character.getBowRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.ANIMA && character.getAnimaRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.LIGHT && character.getLightRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.DARK && character.getDarkRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.STAFF && character.getStaffRank() >= weaponRankValue)) {
			return true;
		}
		
		return false;
	}
	
	private static Boolean canClassUseItem(FEClass characterClass, FEItem weapon, ItemDataLoader itemData) {
		int weaponRankValue = itemData.weaponRankValueForRank(weapon.getWeaponRank());
		if ((weapon.getType() == WeaponType.SWORD && characterClass.getSwordRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.LANCE && characterClass.getLanceRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.AXE && characterClass.getAxeRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.BOW && characterClass.getBowRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.ANIMA && characterClass.getAnimaRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.LIGHT && characterClass.getLightRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.DARK && characterClass.getDarkRank() >= weaponRankValue) ||
				(weapon.getType() == WeaponType.STAFF && characterClass.getStaffRank() >= weaponRankValue)) {
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
		
		int[] targetRanks = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		
		if (applySwordRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = rng.nextInt(ranks.size());
				rankToApply = Math.max(ranks.get(rankIndex), rankToApply);
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
				rankToApply = Math.max(ranks.get(rankIndex), rankToApply);
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
				rankToApply = Math.max(ranks.get(rankIndex), rankToApply);
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
				rankToApply = Math.max(ranks.get(rankIndex), rankToApply);
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
				rankToApply = Math.max(ranks.get(rankIndex), rankToApply);
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
				rankToApply = Math.max(ranks.get(rankIndex), rankToApply);
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
				rankToApply = Math.max(ranks.get(rankIndex), rankToApply);
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
				rankToApply = Math.max(ranks.get(rankIndex), rankToApply);
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
