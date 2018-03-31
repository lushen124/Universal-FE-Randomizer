package random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import fedata.FEChapter;
import fedata.FEChapterUnit;
import fedata.FECharacter;
import fedata.FEClass;
import fedata.FEItem;
import fedata.general.WeaponType;

public class ClassRandomizer {
	
	public static void randomizePlayableCharacterClasses(Boolean includeLords, Boolean includeThieves, CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData) {
		FECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		Map<Integer, FEClass> determinedClasses = new HashMap<Integer, FEClass>();
		
		for (FECharacter character : allPlayableCharacters) {
			
			int originalClassID = character.getClassID();
			FEClass originalClass = classData.classForID(originalClassID);
			
			FEClass targetClass = null;
			
			if (determinedClasses.containsKey(character.getID())) {
				targetClass = determinedClasses.get(character.getID());
			}
			
			FEClass[] possibleClasses = classData.potentialClasses(originalClass, !includeLords, !includeThieves, true);
			if (possibleClasses.length == 0) {
				continue;
			}
			
			if (targetClass == null) {
				int randomIndex = ThreadLocalRandom.current().nextInt(possibleClasses.length);
				targetClass = possibleClasses[randomIndex];
			}
			
			if (targetClass == null) {
				continue;
			}
			
			updateCharacterToClass(character, originalClass, targetClass, classData, chapterData, itemData);
		}
	}

	private static void updateCharacterToClass(FECharacter character, FEClass sourceClass, FEClass targetClass, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData) {
		
		transferWeaponLevels(character, sourceClass, targetClass);
		
		for (FEChapter chapter : chapterData.allChapters()) {
			for (FEChapterUnit chapterUnit : chapter.allUnits()) {
				if (chapterUnit.getCharacterNumber() == character.getID()) {
//					if (chapterUnit.getStartingClass() != sourceClass.getID()) {
//						System.err.println("Class mismatch for character with ID " + character.getID() + ". Expected Class " + sourceClass.getID() + " but found " + chapterUnit.getStartingClass() + " in chapter with offset " + Long.toHexString(chapter.getAddressOffset()));
//						return;
//					}
					chapterUnit.setStartingClass(targetClass.getID());
					validateInventory(character, chapterUnit, itemData);
					if (classData.isThief(sourceClass.getID())) {
						validateFormerThiefInventory(chapterUnit, itemData);
					}
					validateSpecialClassInventory(character, chapterUnit, itemData);
				}
			}
		}
	}
	
	private static void validateFormerThiefInventory(FEChapterUnit chapterUnit, ItemDataLoader itemData) {
		FEItem[] requiredItems = itemData.formerThiefInventory();
		if (requiredItems != null) {
			giveItemsToChapterUnit(chapterUnit, requiredItems);
		}
	}
	
	private static void validateSpecialClassInventory(FECharacter character, FEChapterUnit chapterUnit, ItemDataLoader itemData) {
		FEItem[] requiredItems = itemData.specialInventoryForClass(chapterUnit.getStartingClass());
		if (requiredItems != null) {
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
	
	private static void validateInventory(FECharacter character, FEChapterUnit chapterUnit, ItemDataLoader itemData) {
		int item1ID = chapterUnit.getItem1();
		FEItem item1 = itemData.itemWithID(item1ID);
		if (item1 != null && item1.getType() != WeaponType.NOT_A_WEAPON) {
			if (!canCharacterUseItem(character, item1)) {
				FEItem replacementItem = getRandomWeaponForCharacter(character, itemData);
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
				FEItem replacementItem = getRandomWeaponForCharacter(character, itemData);
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
				FEItem replacementItem = getRandomWeaponForCharacter(character, itemData);
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
				FEItem replacementItem = getRandomWeaponForCharacter(character, itemData);
				if (replacementItem != null) {
					chapterUnit.setItem4(replacementItem.getID());
				} else {
					chapterUnit.setItem4(0);
				}
			}
		}
	}
	
	private static FEItem getRandomWeaponForCharacter(FECharacter character, ItemDataLoader itemData) {
		FEItem[] potentialItems = usableWeaponsForCharacter(character, itemData);
		if (potentialItems == null || potentialItems.length < 1) {
			return null;
		}
		
		int index = ThreadLocalRandom.current().nextInt(potentialItems.length);
		return potentialItems[index];
	}
	
	private static FEItem[] usableWeaponsForCharacter(FECharacter character, ItemDataLoader itemData) {
		ArrayList<FEItem> items = new ArrayList<FEItem>();
		
		
		if (character.getSwordRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.SWORD, character.getSwordRank()))); }
		if (character.getLanceRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.LANCE, character.getLanceRank()))); }
		if (character.getAxeRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.AXE, character.getAxeRank()))); }
		if (character.getBowRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.BOW, character.getBowRank()))); }
		if (character.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.ANIMA, character.getAnimaRank()))); }
		if (character.getLightRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.LIGHT, character.getLightRank()))); }
		if (character.getDarkRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.DARK, character.getDarkRank()))); }
		if (character.getStaffRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndBelowRankValue(WeaponType.STAFF, character.getStaffRank()))); }
		
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
	
	private static void transferWeaponLevels(FECharacter character, FEClass sourceClass, FEClass targetClass) {
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
				int rankIndex = ThreadLocalRandom.current().nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (ThreadLocalRandom.current().nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[0] = rankToApply;
		}
		if (applyLanceRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = ThreadLocalRandom.current().nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (ThreadLocalRandom.current().nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[1] = rankToApply;
		}
		if (applyAxeRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = ThreadLocalRandom.current().nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (ThreadLocalRandom.current().nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[2] = rankToApply;
		}
		if (applyBowRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = ThreadLocalRandom.current().nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (ThreadLocalRandom.current().nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[3] = rankToApply;
		}
		if (applyAnimaRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = ThreadLocalRandom.current().nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (ThreadLocalRandom.current().nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[4] = rankToApply;
		}
		if (applyLightRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = ThreadLocalRandom.current().nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (ThreadLocalRandom.current().nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[5] = rankToApply;
		}
		if (applyDarkRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = ThreadLocalRandom.current().nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (ThreadLocalRandom.current().nextInt(2) == 0) {
					ranks.remove(rankIndex);
				}
			}
			targetRanks[6] = rankToApply;
		}
		if (applyStaffRank) {
			int rankToApply = targetClass.getBaseRankValue();
			if (ranks.size() > 0) {
				int rankIndex = ThreadLocalRandom.current().nextInt(ranks.size());
				rankToApply = ranks.get(rankIndex);
				if (ThreadLocalRandom.current().nextInt(2) == 0) {
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
