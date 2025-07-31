package random.gba.randomizer;

import java.util.*;
import java.util.stream.Collectors;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.general.WeaponRanks;
import fedata.gba.GBAFEStatDto;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import fedata.general.FEBase.GameType;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import random.gba.randomizer.service.GBASlotAdjustmentService;
import random.general.PoolDistributor;
import random.general.RelativeValueMapper;
import ui.model.ClassOptions;
import ui.model.ItemAssignmentOptions;
import ui.model.ClassOptions.GenderRestrictionOption;
import ui.model.ItemAssignmentOptions.WeaponReplacementPolicy;
import util.DebugPrinter;

public class ClassRandomizer {
	
	static final int rngSalt = 874;
	
	public static void randomizeClassMovement(int minMOV, int maxMOV, ClassDataLoader classData, Random rng) {
		GBAFEClassData[] allClasses = classData.allClasses();
		List<GBAFEClassData> unpromotedClasses = Arrays.asList(allClasses).stream()
				.filter(currentClass -> classData.isPromotedClass(currentClass.getID()) == false)
				.sorted(GBAFEClassData.defaultComparator)
				.collect(Collectors.toList());
		for (GBAFEClassData currentClass : unpromotedClasses) {
			if (currentClass.getMOV() > 0) {
				// #259: Allow for maximum provided in UI
				// Fringe benefit of allowing (min == max), i.e. every class has the same MOV
				int randomMOV = rng.nextInt(maxMOV - minMOV + 1) + minMOV;
				currentClass.setMOV(randomMOV);
			}
		}
		
		// Make sure all promoted classes have at least their base class's MOV so you can never lose MOV from promotion.
		List<GBAFEClassData> promotedClasses = Arrays.asList(allClasses).stream()
				.filter(currentClass -> classData.isPromotedClass(currentClass.getID()))
				.sorted(GBAFEClassData.defaultComparator)
				.collect(Collectors.toList());
		for (GBAFEClassData currentClass : promotedClasses) {
			List<GBAFEClassData> unpromoted = classData.demotionOptions(currentClass.getID());
			int highestUnpromotedMOV = 0;
			for (GBAFEClassData charClass : unpromoted) { highestUnpromotedMOV = Math.max(highestUnpromotedMOV, charClass.getMOV()); }
			if (highestUnpromotedMOV > 0) {
				int randomMOV = rng.nextInt(maxMOV - highestUnpromotedMOV + 1) + highestUnpromotedMOV;
				currentClass.setMOV(randomMOV);
			}
		}
	}
	
	public static void randomizePlayableCharacterClasses(ClassOptions options, ItemAssignmentOptions inventoryOptions, GameType type, CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, TextLoader textData, Random rng) {
		GBAFECharacterData[] allPlayableCharacters = charactersData.playableCharacters();
		Map<Integer, GBAFEClassData> determinedClasses = new HashMap<Integer, GBAFEClassData>();
		
		Boolean includeLords = options.includeLords;
		Boolean includeThieves = options.includeThieves;
		Boolean includeSpecial = options.includeSpecial;
		Boolean hasMonsters = false;
		Boolean separateMonsters = false;
		
		Boolean forceChange = options.forceChange;
		
		if (type == GameType.FE8) {
			hasMonsters = true;
			separateMonsters = options.separateMonsters;
		}
		
		PoolDistributor<GBAFEClassData> classDistributor = new PoolDistributor<GBAFEClassData>();
		Arrays.asList(classData.allClasses()).stream().forEach(charClass -> {
			classDistributor.addItem(charClass);
		});
		
		for (GBAFECharacterData character : allPlayableCharacters) {
			
			Boolean isLordCharacter = charactersData.isLordCharacterID(character.getID());
			Boolean isThiefCharacter = charactersData.isThiefCharacterID(character.getID());
			Boolean isSpecialCharacter = charactersData.isSpecialCharacterID(character.getID());
			Boolean canChange = charactersData.canChangeCharacterID(character.getID());
			
			if (isLordCharacter && !includeLords) { continue; }
			if (isThiefCharacter && !includeThieves) { continue; }
			if (isSpecialCharacter && !includeSpecial) { continue; }
			if (!canChange) { continue; }
			
			Boolean characterRequiresRange = charactersData.characterIDRequiresRange(character.getID());
			Boolean characterRequiresMelee = charactersData.characterIDRequiresMelee(character.getID());
			
			int originalClassID = character.getClassID();
			GBAFEClassData originalClass = classData.classForID(originalClassID);
			
			GBAFEClassData targetClass = null;
			
			boolean isFemale = charactersData.isFemale(character.getID());
			
			if (determinedClasses.containsKey(character.getID())) {
				continue;
			} else {
				GBAFEClassData[] possibleClasses = hasMonsters ? classData.potentialClasses(originalClass, charactersData.isEnemyAtAnyPoint(character.getID()), !includeLords, !includeThieves, !includeSpecial, separateMonsters, forceChange, isLordCharacter, characterRequiresRange, characterRequiresMelee, character.isClassRestricted(), options.genderOption, null) :
					classData.potentialClasses(originalClass, charactersData.isEnemyAtAnyPoint(character.getID()), !includeLords, !includeThieves, !includeSpecial, forceChange, isLordCharacter, characterRequiresRange, characterRequiresMelee, character.isClassRestricted(), options.genderOption, null);
				if (possibleClasses.length == 0) {
					continue;
				}
				
				if (options.assignEvenly) {
					Set<GBAFEClassData> classSet = new HashSet<GBAFEClassData>(Arrays.asList(possibleClasses));
					if (Collections.disjoint(classDistributor.possibleResults(), classSet)) {
						Arrays.asList(classData.allClasses()).stream().forEach(charClass -> {
							classDistributor.addItem(charClass);
						});
					}
					classSet.retainAll(classDistributor.possibleResults());
					List<GBAFEClassData> classList = classSet.stream().sorted(GBAFEClassData.defaultComparator).collect(Collectors.toList());
					PoolDistributor<GBAFEClassData> pool = new PoolDistributor<GBAFEClassData>();
					for (GBAFEClassData charClass : classList) {
						pool.addItem(charClass, classDistributor.itemCount(charClass));
					}
					targetClass = pool.getRandomItem(rng, true);
					classDistributor.removeItem(targetClass, false);
				} else {
					int randomIndex = rng.nextInt(possibleClasses.length);
					targetClass = possibleClasses[randomIndex];
				}
				
				if (options.genderOption == GenderRestrictionOption.LOOSE) {
					if (isFemale) {
						targetClass = classData.correspondingFemaleClass(targetClass);
					} else {
						targetClass = classData.correspondingMaleClass(targetClass);
					}
				}
			}
			
			if (targetClass == null) {
				continue;
			}
			
			DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Assigning character 0x" + Integer.toHexString(character.getID()).toUpperCase() + " (" + textData.getStringAtIndex(character.getNameIndex(), true) + ") to class 0x" + Integer.toHexString(targetClass.getID()) + " (" + textData.getStringAtIndex(targetClass.getNameIndex(), true) + ")");
			
			for (GBAFECharacterData linked : charactersData.linkedCharactersForCharacter(character)) {
				determinedClasses.put(linked.getID(), targetClass);
				updateCharacterToClass(options, inventoryOptions, linked, originalClass, targetClass, characterRequiresRange, characterRequiresMelee, charactersData, classData, chapterData, itemData, textData, false, false, false, type, rng);
				linked.setIsLord(isLordCharacter);
			}
		}
	}
	
	public static void randomizeBossCharacterClasses(ClassOptions options, ItemAssignmentOptions inventoryOptions, GameType type, CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, TextLoader textData, Random rng) {
		GBAFECharacterData[] allBossCharacters = charactersData.bossCharacters();
		
		Boolean includeLords = false;
		Boolean includeThieves = false;
		Boolean includeSpecial = false;
		Boolean hasMonsters = false;
		Boolean separateMonsters = false;
		Boolean forceChange = options.forceChange;
		if (type == GameType.FE8) {
			hasMonsters = true;
			separateMonsters = options.separateMonsters;
		}
		
		Map<Integer, GBAFEClassData> determinedClasses = new HashMap<Integer, GBAFEClassData>();
		
		for (GBAFECharacterData character : allBossCharacters) {
			
			Boolean canChange = charactersData.canChangeCharacterID(character.getID());
			if (!canChange) { continue; }
			
			Boolean characterRequiresRange = charactersData.characterIDRequiresRange(character.getID());
			Boolean characterRequiresMelee = charactersData.characterIDRequiresMelee(character.getID());
			
			int originalClassID = character.getClassID();
			GBAFEClassData originalClass = classData.classForID(originalClassID);
			if (originalClass == null) {
				System.err.println("Invalid Class found: Class ID = " + Integer.toHexString(originalClassID));
				continue;
			}
			
			if (classData.isValidClass(originalClassID) == false) {
				DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Skipping character " + character.displayString() + " because class is not a valid candidate for randomization (" + originalClass.displayString() + ").");
				continue;
			}
			
			GBAFEClassData targetClass = null;
			
			Boolean forceBasicWeaponry = false;
			Boolean shouldNerf = false;
			
			boolean isFemale = charactersData.isFemale(character.getID());
			
			if (determinedClasses.containsKey(character.getID())) {
				continue;
			} else {			
				GBAFECharacterData mustLoseToCharacter = charactersData.characterRequiresCounterToCharacter(character);
				GBAFEClassData mustLoseToClass = null;
				if (mustLoseToCharacter != null) {
					mustLoseToClass = classData.classForID(mustLoseToCharacter.getClassID());
					forceBasicWeaponry = true;
					shouldNerf = true;
				}
				
				GBAFEClassData[] possibleClasses = hasMonsters ? 
						classData.potentialClasses(originalClass, true, !includeLords, !includeThieves, !includeSpecial, separateMonsters, forceChange, true, characterRequiresRange, characterRequiresMelee, character.isClassRestricted(), options.genderOption, mustLoseToClass) :
					classData.potentialClasses(originalClass, true, !includeLords, !includeThieves, !includeSpecial, forceChange, true, characterRequiresRange, characterRequiresMelee, character.isClassRestricted(), options.genderOption, mustLoseToClass);
				if (possibleClasses.length == 0) {
					continue;
				}
			
				int randomIndex = rng.nextInt(possibleClasses.length);
				targetClass = possibleClasses[randomIndex];
			}
			
			if (options.genderOption == GenderRestrictionOption.LOOSE) {
				if (isFemale) {
					targetClass = classData.correspondingFemaleClass(targetClass);
				} else {
					targetClass = classData.correspondingMaleClass(targetClass);
				}
			}
			
			if (targetClass == null) {
				continue;
			}
			
			for (GBAFECharacterData linked : charactersData.linkedCharactersForCharacter(character)) {
				determinedClasses.put(linked.getID(), targetClass);
				updateCharacterToClass(options, inventoryOptions, linked, originalClass, targetClass, characterRequiresRange, characterRequiresMelee, charactersData, classData, chapterData, itemData, textData, forceBasicWeaponry && linked.getID() == character.getID(), true, true, type, rng);
				if (shouldNerf) { // Halve skill, speed, defense, and resistance if we need to make sure he loses to us.
					linked.setBaseSKL(linked.getBaseSKL() >> 1);
					linked.setBaseSPD(linked.getBaseSPD() >> 1);
					linked.setBaseDEF(linked.getBaseDEF() >> 1);
					linked.setBaseRES(linked.getBaseRES() >> 1);
				}
			}
		}
	}
	
	public static void randomizeMinionClasses(ClassOptions options, ItemAssignmentOptions inventoryOptions, GameType type, CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, Random rng) {
		Boolean includeLords = false;
		Boolean includeThieves = false;
		Boolean includeSpecial = false;
		Boolean hasMonsters = false;
		Boolean separateMonsters = false;
		Boolean forceChange = options.forceChange;
		if (type == GameType.FE8) {
			hasMonsters = true;
			separateMonsters = options.separateMonsters;
		}
		
		// Before we start, make all classes naturally have A rank so that weapons can transfer more easily.
		// Somehow, some enemies, despite all signs of them only being able to use up to C rank weapons,
		// are able to use A rank somehow in some cases. Since I don't know why this is,
		// we're going to modify all classes to have A rank in all areas. Characters with lower ranks will override it
		// which includes all playable characters.
//		for (GBAFEClassData charClass : classData.allClasses()) {
//			if (charClass.getSwordRank() > 0) { charClass.setSwordRank(WeaponRank.A); }
//			if (charClass.getLanceRank() > 0) { charClass.setLanceRank(WeaponRank.A); }
//			if (charClass.getAxeRank() > 0) { charClass.setAxeRank(WeaponRank.A); }
//			if (charClass.getBowRank() > 0) { charClass.setBowRank(WeaponRank.A); }
//			if (charClass.getAnimaRank() > 0) { charClass.setAnimaRank(WeaponRank.A); }
//			if (charClass.getLightRank() > 0) { charClass.setLightRank(WeaponRank.A); }
//			if (charClass.getDarkRank() > 0) { charClass.setDarkRank(WeaponRank.A); }
//			if (charClass.getStaffRank() > 0) { charClass.setStaffRank(WeaponRank.A); }
//		}
		
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			int maxEnemyClassLimit = chapter.getMaxEnemyClassLimit();
			// There's really four slots we need to reserve.
			// Unpromoted land unit
			// Promoted land unit
			// Unpromoted flying unit
			// Promoted flying unit
			// If we have all of these, we can guarantee a replacement if we run into the limit.
			
			List<GBAFEClassData> unpromotedLandUnit = new ArrayList<GBAFEClassData>();
			List<GBAFEClassData> promotedLandUnit = new ArrayList<GBAFEClassData>();
			List<GBAFEClassData> unpromotedFlyingUnit = new ArrayList<GBAFEClassData>();
			List<GBAFEClassData> promotedFlyingUnit = new ArrayList<GBAFEClassData>();
			
			Map<GBAFEClassData, List<GBAFEChapterUnitData>> selectedClasses = new HashMap<GBAFEClassData, List<GBAFEChapterUnitData>>();
			GBAFECharacterData lordCharacter = charactersData.characterWithID(chapter.lordLeaderID());
			GBAFEClassData lordClass = classData.classForID(lordCharacter.getClassID());
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				// int leaderID = chapterUnit.getLeaderID();
				int characterID = chapterUnit.getCharacterNumber();
				int classID = chapterUnit.getStartingClass();
				// It's safe to check for boss leader ID in the case of FE7, but FE6 tends to put other IDs there (kind of like squad captains).
				// We're going to remove this safety check in the meantime, but we should be wary of any accidental changes.
				// Also check to make sure it's not any character we definitely don't want to change.
				// Finally, also make sure the starting class is valid. Classes we don't recognize, we shouldn't touch.
				if (!charactersData.isBossCharacterID(characterID) && /*charactersData.isBossCharacterID(leaderID) &&*/ !charactersData.isPlayableCharacterID(characterID) && 
						charactersData.canChangeCharacterID(characterID) && classData.isValidClass(classID)) {
					
					GBAFEClassData originalClass = classData.classForID(classID);
					if (originalClass == null) {
						continue;
					}
					
					// If their AI flag is set to target villages/chests, do not randomize them.
					if (chapterUnit.isAITargetingVillages()) {
						continue;
					}
					
					GBAFECharacterData minionCharacterData = charactersData.minionCharacterWithID(characterID);
					if (minionCharacterData == null) {
						continue;
					}
					
					GBAFEClassData targetClass = null;
					boolean characterHasWeaponRanks = !itemData.ranksForCharacter(minionCharacterData, null).getTypes().isEmpty();
					if (characterHasWeaponRanks) {
						minionCharacterData.clearAllWeaponRanks();
						minionCharacterData.commitChanges();
						characterHasWeaponRanks = false;
					}
					
					chapterUnit.setAutolevel(true);
					
					if (targetClass != null) {
						updateMinionToClass(inventoryOptions, chapterUnit, minionCharacterData, targetClass, classData, itemData, type, rng);
					} else {
						Boolean shouldRestrictToSafeClasses = !chapter.isClassSafe();
						Boolean shouldMakeEasy = chapter.shouldBeSimplified();
						GBAFEClassData loseToClass = shouldMakeEasy ? lordClass : null;
						GBAFEClassData[] possibleClasses = hasMonsters ? 
								classData.potentialClasses(originalClass, true, !includeLords, !includeThieves, !includeSpecial, separateMonsters, forceChange, true, false, false, shouldRestrictToSafeClasses, options.genderOption, loseToClass) :
							classData.potentialClasses(originalClass, true, false, false, false, forceChange, true, false, false, shouldRestrictToSafeClasses, options.genderOption, loseToClass);
						if (possibleClasses.length == 0) {
							continue;
						}
						
						if (maxEnemyClassLimit > 0) {
							int numberOfSlotsNeededToFill = 4;
							if (!promotedFlyingUnit.isEmpty()) { numberOfSlotsNeededToFill--; }
							if (!unpromotedFlyingUnit.isEmpty()) { numberOfSlotsNeededToFill--; }
							if (!promotedLandUnit.isEmpty()) { numberOfSlotsNeededToFill--; }
							if (!unpromotedLandUnit.isEmpty()) { numberOfSlotsNeededToFill--; }
							
							if (selectedClasses.size() >= maxEnemyClassLimit - numberOfSlotsNeededToFill) {
								// We've reached the maximum limit. Reuse one of the classes we've already assigned.
								boolean isPromoted = classData.isPromotedClass(originalClass.getID());
								boolean isFlying = classData.isFlying(originalClass.getID());
								
								if (isPromoted && isFlying && !promotedFlyingUnit.isEmpty()) {
									targetClass = promotedFlyingUnit.get(rng.nextInt(promotedFlyingUnit.size()));
								} else if (isPromoted && !isFlying && !promotedLandUnit.isEmpty()) {
									// A land unit can be subbed with a flying unit.
									targetClass = promotedLandUnit.get(rng.nextInt(promotedLandUnit.size()));
								} else if (!isPromoted && isFlying && !unpromotedFlyingUnit.isEmpty()) {
									targetClass = unpromotedFlyingUnit.get(rng.nextInt(unpromotedFlyingUnit.size()));
								} else if (!isPromoted && !isFlying && !unpromotedLandUnit.isEmpty()) {
									// A land unit can be subbed with a flying unit too.
									targetClass = unpromotedLandUnit.get(rng.nextInt(unpromotedLandUnit.size()));
								}
							}
						}
						
						if (targetClass == null) {
							int randomIndex = rng.nextInt(possibleClasses.length);
							targetClass = possibleClasses[randomIndex];
						
						
							if (classData.isFlying(originalClass.getID()) == false && classData.isFlying(targetClass.getID())) {
								// If this is a new flier, roll one more time. 
								// Reduce the number of non-flying minions that become fliers.
								randomIndex = rng.nextInt(possibleClasses.length);
								targetClass = possibleClasses[randomIndex];
							}
							
							// If we have a class limit, don't allow any non-flying unit to be flying.
							if (maxEnemyClassLimit > 0) {
								while (classData.isFlying(targetClass.getID()) && !classData.isFlying(originalClass.getID())) {
									randomIndex = rng.nextInt(possibleClasses.length);
									targetClass = possibleClasses[randomIndex];
								}
							}
						}
						
						if (characterHasWeaponRanks) {
							updateMinionCharacterToClass(inventoryOptions, chapterUnit, minionCharacterData, originalClass, targetClass, classData, itemData, type, rng);
						} else {
							updateMinionToClass(inventoryOptions, chapterUnit, minionCharacterData, targetClass, classData, itemData, type, rng);
						}
						
						// Issue#399 Certain Minions might have Negative Bases in the earlier game (f.e. Lyn Mode) 
						// to make the game a bit easier, ensure that their stats don't underflow if we change their class.
						GBAFEStatDto finalMinionStats = minionCharacterData.getBases().add(targetClass.getBases());
						finalMinionStats = finalMinionStats.clamp(GBAFEStatDto.MINIMUM_STATS, targetClass.getCaps());
						finalMinionStats.subtract(targetClass.getBases());
						minionCharacterData.setBases(finalMinionStats);
						
						if (classData.isPromotedClass(targetClass.getID())) {
							if (classData.isFlying(targetClass.getID())) {
								promotedFlyingUnit.add(targetClass);
							} else {
								promotedLandUnit.add(targetClass);
							}
						} else {
							if (classData.isFlying(targetClass.getID())) {
								unpromotedFlyingUnit.add(targetClass);
							} else {
								unpromotedLandUnit.add(targetClass);
							}
						}
						
						List<GBAFEChapterUnitData> unitsInClass = selectedClasses.get(targetClass);
						if (unitsInClass == null) {
							unitsInClass = new ArrayList<GBAFEChapterUnitData>();
							selectedClasses.put(targetClass, unitsInClass);
						}
						
						unitsInClass.add(chapterUnit);
					}
				}
			}
		}
	}

	private static void updateCharacterToClass(ClassOptions classOptions, ItemAssignmentOptions inventoryOptions, GBAFECharacterData character, GBAFEClassData sourceClass, GBAFEClassData targetClass, Boolean ranged, Boolean melee, CharacterDataLoader charData, ClassDataLoader classData, ChapterLoader chapterData, ItemDataLoader itemData, TextLoader textData, Boolean forceBasicWeapons, Boolean excludeBasicWeapons, boolean highestRankMustBeWeapon, GameType type, Random rng) {
		character.prepareForClassRandomization();
		character.setClassID(targetClass.getID());
		if (charData.isBossCharacterID(character.getID())) {
			transferBossWeaponLevels(character, sourceClass, targetClass, type);
		} else {
			GBASlotAdjustmentService.transferWeaponRanks(character, sourceClass, targetClass, type, rng);
		}
		if (charData.isBossCharacterID(character.getID()) == false) {
			switch (classOptions.basesTransfer) {
			case ADJUST_TO_MATCH:
				applyBaseCorrectionForCharacter(character, sourceClass, targetClass);
				break;
			case NO_CHANGE:
				break;
			case ADJUST_TO_CLASS:
				adjustBasesToMatchClass(character, sourceClass, targetClass);
				break;
			}
		}
		
		// We need to make sure nobody underflows, so keep an eye out for negative personal bases.
		if (character.getBaseHP() + targetClass.getBaseHP() < 0) { character.setBaseHP(-1 * targetClass.getBaseHP() + 1); } // Should always have at least 1 HP.
		if (character.getBaseSTR() + targetClass.getBaseSTR() < 0) { character.setBaseSTR(-1 * targetClass.getBaseSTR()); }
		if (character.getBaseSKL() + targetClass.getBaseSKL() < 0) { character.setBaseSKL(-1 * targetClass.getBaseSKL()); }
		if (character.getBaseSPD() + targetClass.getBaseSPD() < 0) { character.setBaseSPD(-1 * targetClass.getBaseSPD()); }
		if (character.getBaseDEF() + targetClass.getBaseDEF() < 0) { character.setBaseDEF(-1 * targetClass.getBaseDEF()); }
		if (character.getBaseRES() + targetClass.getBaseRES() < 0) { character.setBaseRES(-1 * targetClass.getBaseRES()); }
		if (character.getBaseLCK() + targetClass.getBaseLCK() < 0) { character.setBaseLCK(-1 * targetClass.getBaseLCK()); }
		
		switch (classOptions.growthOptions) {
		case TRANSFER_PERSONAL_GROWTHS:
			int hpOffset = character.getHPGrowth() - sourceClass.getHPGrowth();
			int strOffset = character.getSTRGrowth() - sourceClass.getSTRGrowth();
			int sklOffset = character.getSKLGrowth() - sourceClass.getSKLGrowth();
			int spdOffset = character.getSPDGrowth() - sourceClass.getSPDGrowth();
			int lckOffset = character.getLCKGrowth() - sourceClass.getLCKGrowth();
			int defOffset = character.getDEFGrowth() - sourceClass.getDEFGrowth();
			int resOffset = character.getRESGrowth() - sourceClass.getRESGrowth();
			
			character.setHPGrowth(Math.max(0, targetClass.getHPGrowth() + hpOffset));
			character.setSTRGrowth(Math.max(0, targetClass.getSTRGrowth() + strOffset));
			character.setSKLGrowth(Math.max(0, targetClass.getSKLGrowth() + sklOffset));
			character.setSPDGrowth(Math.max(0, targetClass.getSPDGrowth() + spdOffset));
			character.setLCKGrowth(Math.max(0, targetClass.getLCKGrowth() + lckOffset));
			character.setDEFGrowth(Math.max(0, targetClass.getDEFGrowth() + defOffset));
			character.setRESGrowth(Math.max(0, targetClass.getRESGrowth() + resOffset));
			break;
		case CLASS_RELATIVE_GROWTHS:
			adjustGrowthsToMatchClass(character, sourceClass, targetClass);
			break;
		default:
			break;
		}
		
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			GBAFEChapterItemData reward = chapter.chapterItemGivenToCharacter(character.getID());
			if (reward != null) {
				GBAFEItemData item = itemData.getRandomWeaponForCharacter(character, ranged, melee, false, inventoryOptions.assignPromoWeapons, inventoryOptions.assignPoisonWeapons, excludeBasicWeapons, false, rng); 
				
				// If this character has a prf weapon, use that instead.
				GBAFEItemData[] prfWeapons = itemData.prfWeaponsForClass(targetClass.getID());
				if (prfWeapons.length > 0) {
					item = prfWeapons[rng.nextInt(prfWeapons.length)];
				}
				reward.setItemID(item.getID());
			}
			
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				if (chapterUnit.getCharacterNumber() == character.getID()) {
					if (chapterUnit.getStartingClass() != sourceClass.getID()) {
						System.err.println("Class mismatch for character with ID " + character.getID() + ". Expected Class " + sourceClass.getID() + " but found " + chapterUnit.getStartingClass() + " in Chapter " + chapter.getFriendlyName());
						if (!classData.isValidClass(chapterUnit.getStartingClass()) && chapterUnit.getStartingClass() != 0) {
							System.err.println("Invalid class detected. Skipping class change for " + charData.debugStringForCharacter(character.getID()) + ". Invalid class: " + classData.debugStringForClass(chapterUnit.getStartingClass()));
							continue;
						}
					}
					chapterUnit.setStartingClass(targetClass.getID());
					validateCharacterInventory(inventoryOptions, character, targetClass, chapterUnit, ranged, melee, charData, classData, itemData, textData, forceBasicWeapons, excludeBasicWeapons, highestRankMustBeWeapon, rng);
					if (classData.isThief(sourceClass.getID())) {
						validateFormerThiefInventory(chapterUnit, itemData);
					}
					validateSpecialClassInventory(chapterUnit, itemData, rng);
				}
			}
		}
	}
	
	private static void applyBaseCorrectionForCharacter(GBAFECharacterData character, GBAFEClassData sourceClass, GBAFEClassData targetClass) {
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
		int lckDelta = sourceClass.getBaseLCK() - targetClass.getBaseLCK();
		character.setBaseLCK(character.getBaseLCK() + lckDelta);
		
		// Only correct CON if it ends up being an invalid (i.e. negative) CON.
		// This is only really possible if the character had a negative CON adjustment to begin with.
		if (character.getConstitution() < 0 && Math.abs(character.getConstitution()) > targetClass.getCON()) {
			character.setConstitution(-1 * targetClass.getCON());
		}
	}
	
	private static void adjustBasesToMatchClass(GBAFECharacterData character, GBAFEClassData sourceClass, GBAFEClassData targetClass) {
		// HP transfers directly, as does LCK.
		int hpDelta = sourceClass.getBaseHP() - targetClass.getBaseHP();
		character.setBaseHP(character.getBaseHP() + hpDelta);
		int lckDelta = sourceClass.getBaseLCK() - targetClass.getBaseLCK();
		character.setBaseLCK(character.getBaseLCK() + lckDelta);
		
		// STR, SKL, SPD, DEF, and RES are transfered based on which one is highest on the target class.
		int effectiveSTR = character.getBaseSTR() + sourceClass.getBaseSTR();
		int effectiveSKL = character.getBaseSKL() + sourceClass.getBaseSKL();
		int effectiveSPD = character.getBaseSPD() + sourceClass.getBaseSPD();
		int effectiveDEF = character.getBaseDEF() + sourceClass.getBaseDEF();
		int effectiveRES = character.getBaseRES() + sourceClass.getBaseRES();
		
		List<Integer> mappedStats = RelativeValueMapper.mappedValues(Arrays.asList(effectiveSTR, effectiveSKL, effectiveSPD, effectiveDEF, effectiveRES),
				Arrays.asList(targetClass.getBaseSTR(), targetClass.getBaseSKL(), targetClass.getBaseSPD(), targetClass.getBaseDEF(), targetClass.getBaseRES()));
		
		character.setBaseSTR(mappedStats.get(0) - targetClass.getBaseSTR());
		character.setBaseSKL(mappedStats.get(1) - targetClass.getBaseSKL());
		character.setBaseSPD(mappedStats.get(2) - targetClass.getBaseSPD());
		character.setBaseDEF(mappedStats.get(3) - targetClass.getBaseDEF());
		character.setBaseRES(mappedStats.get(4) - targetClass.getBaseRES());
	}
	
	private static void adjustGrowthsToMatchClass(GBAFECharacterData character, GBAFEClassData sourceClass, GBAFEClassData targetClass) {
		List<Integer> mappedGrowths = RelativeValueMapper.mappedValues(Arrays.asList(character.getHPGrowth(), character.getSTRGrowth(), character.getSKLGrowth(), character.getSPDGrowth(), character.getDEFGrowth(), character.getRESGrowth(), character.getLCKGrowth()),
				Arrays.asList(targetClass.getHPGrowth(), targetClass.getSTRGrowth(), targetClass.getSKLGrowth(), targetClass.getSPDGrowth(), targetClass.getDEFGrowth(), targetClass.getRESGrowth(), targetClass.getLCKGrowth()));
		
		character.setHPGrowth(mappedGrowths.get(0));
		character.setSTRGrowth(mappedGrowths.get(1));
		character.setSKLGrowth(mappedGrowths.get(2));
		character.setSPDGrowth(mappedGrowths.get(3));
		character.setDEFGrowth(mappedGrowths.get(4));
		character.setRESGrowth(mappedGrowths.get(5));
		character.setLCKGrowth(mappedGrowths.get(6));
	}
	
	// TODO: Offer an option for sidegrade strictness?
	private static void updateMinionToClass(ItemAssignmentOptions inventoryOptions, GBAFEChapterUnitData chapterUnit, GBAFECharacterData minionCharacter, GBAFEClassData targetClass, ClassDataLoader classData, ItemDataLoader itemData, GameType type, Random rng) {
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Updating minion from class 0x" + Integer.toHexString(chapterUnit.getStartingClass()) + " to class 0x" + Integer.toHexString(targetClass.getID()));
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Starting Inventory: [0x" + Integer.toHexString(chapterUnit.getItem1()) + ", 0x" + Integer.toHexString(chapterUnit.getItem2()) + ", 0x" + Integer.toHexString(chapterUnit.getItem3()) + ", 0x" + Integer.toHexString(chapterUnit.getItem4()) + "]");
		chapterUnit.setStartingClass(targetClass.getID());
		validateMinionInventory(inventoryOptions, chapterUnit, targetClass, classData, itemData, type, rng);
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Minion update complete. Inventory: [0x" + Integer.toHexString(chapterUnit.getItem1()) + ", 0x" + Integer.toHexString(chapterUnit.getItem2()) + ", 0x" + Integer.toHexString(chapterUnit.getItem3()) + ", 0x" + Integer.toHexString(chapterUnit.getItem4()) + "]");
	}
	
	private static void updateMinionCharacterToClass(ItemAssignmentOptions inventoryOptions, GBAFEChapterUnitData chapterUnit, GBAFECharacterData minionCharacter, GBAFEClassData sourceClass, GBAFEClassData targetClass, ClassDataLoader classData, ItemDataLoader itemData, GameType type, Random rng) {
		// Write this into the character data.
		minionCharacter.setClassID(targetClass.getID());
		GBASlotAdjustmentService.transferWeaponRanks(minionCharacter, sourceClass, targetClass, type, rng);
		chapterUnit.setStartingClass(targetClass.getID());
		validateMinionInventory(inventoryOptions, chapterUnit, minionCharacter, classData, itemData, rng);
	}
	
	public static void validateFormerThiefInventory(GBAFEChapterUnitData chapterUnit, ItemDataLoader itemData) {		
		GBAFEItemData[] requiredItems = itemData.formerThiefInventory();
		if (requiredItems != null) {
			giveItemsToChapterUnit(chapterUnit, itemData, requiredItems);
		}
		
		GBAFEItemData[] thiefItemsToRemove = itemData.thiefItemsToRemove();
		for (GBAFEItemData item : thiefItemsToRemove) {
			chapterUnit.removeItem(item.getID());
		}
	}
	
//	private static void itemsToGiveBack(GBAFEChapterUnitData chapterUnit, Set<GBAFEItemData> itemsToRetain, ItemDataLoader itemData) {
//		int item1ID = chapterUnit.getItem1();
//		GBAFEItemData item1 = itemData.itemWithID(item1ID);
//		int item2ID = chapterUnit.getItem2();
//		GBAFEItemData item2 = itemData.itemWithID(item2ID);
//		int item3ID = chapterUnit.getItem3();
//		GBAFEItemData item3 = itemData.itemWithID(item3ID);
//		int item4ID = chapterUnit.getItem4();
//		GBAFEItemData item4 = itemData.itemWithID(item4ID);
//		
//		if (!itemsToRetain.isEmpty()) {
//			if (item1 != null) { itemsToRetain.remove(item1); }
//			if (item2 != null) { itemsToRetain.remove(item2); }
//			if (item3 != null) { itemsToRetain.remove(item3); }
//			if (item4 != null) { itemsToRetain.remove(item4); }
//		}
//	}
	
	public static void validateSpecialClassInventory(GBAFEChapterUnitData chapterUnit, ItemDataLoader itemData, Random rng) {
		GBAFEItemData[] requiredItems = itemData.specialInventoryForClass(chapterUnit.getStartingClass(), rng);
		if (requiredItems != null && requiredItems.length > 0) {
			giveItemsToChapterUnit(chapterUnit, itemData, requiredItems);
		}
	}
	
	private static void giveItemsToChapterUnit(GBAFEChapterUnitData chapterUnit, ItemDataLoader itemData, GBAFEItemData[] items) {
		int[] requiredItemIDs = new int[items.length];
		for (int i = 0; i < items.length; i++) {
			requiredItemIDs[i] = items[i].getID();
		}
		chapterUnit.giveItems(requiredItemIDs, itemData);
	}
	
	private static void validateMinionInventory(ItemAssignmentOptions inventoryOptions, GBAFEChapterUnitData chapterUnit, GBAFEClassData targetClass, ClassDataLoader classData, ItemDataLoader itemData, GameType type, Random rng) {
		int classID = chapterUnit.getStartingClass();
		GBAFEClassData unitClass = classData.classForID(classID);
		
		boolean canAttack = classData.canClassAttack(classID);
		boolean isHealer = unitClass.getStaffRank() > 0;
		
		boolean limitStaves = isHealer && canAttack;
		boolean hasStaff = false;
		boolean hasWeapon = false;
		boolean hasItems = false;
		
		GBAFEItemData replacementItem = null;
		
		if (unitClass != null) {
			int item1ID = chapterUnit.getItem1();
			GBAFEItemData item1 = itemData.itemWithID(item1ID);
			if (!hasItems) { hasItems = item1 != null; }
			if (item1 != null && (itemData.isWeapon(item1) || item1.getType() == WeaponType.STAFF)) {
				if (!unitClass.canUseWeapon(item1)) {
					replacementItem = itemData.getSidegradeWeapon(unitClass, item1, inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, true, type, rng);
					if (replacementItem != null && (isHealer && limitStaves && hasStaff) && replacementItem.getType() == WeaponType.STAFF) {
						replacementItem = null; // We'll handle this later.
					}
					if (replacementItem != null) {
						chapterUnit.setItem1(replacementItem.getID());
					} else {
						chapterUnit.setItem1(0);
					}
					item1 = replacementItem;
				}
			}
			
			if (item1 != null) {
				if (!hasStaff) { hasStaff = item1.getType() == WeaponType.STAFF; }
				if (!hasWeapon) { hasWeapon = itemData.isWeapon(item1); }
			}
			
			int item2ID = chapterUnit.getItem2();
			GBAFEItemData item2 = itemData.itemWithID(item2ID);
			if (!hasItems) { hasItems = item2 != null; }
			if (item2 != null && (itemData.isWeapon(item2) || item2.getType() == WeaponType.STAFF)) {
				if (!unitClass.canUseWeapon(item2)) {
					replacementItem = itemData.getSidegradeWeapon(unitClass, item2, inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, true, type, rng);
					if ((isHealer && limitStaves && hasStaff) && replacementItem.getType() == WeaponType.STAFF) {
						replacementItem = null; // We'll handle this later.
					}
					if (replacementItem != null) {
						chapterUnit.setItem2(replacementItem.getID());
					} else {
						chapterUnit.setItem2(0);
					}
					item2 = replacementItem;
				}
			}
			
			if (item2 != null) {
				if (!hasStaff) { hasStaff = item2.getType() == WeaponType.STAFF; }
				if (!hasWeapon) { hasWeapon = itemData.isWeapon(item2); }
			}
			
			int item3ID = chapterUnit.getItem3();
			GBAFEItemData item3 = itemData.itemWithID(item3ID);
			if (!hasItems) { hasItems = item3 != null; }
			if (item3 != null && (itemData.isWeapon(item3) || item3.getType() == WeaponType.STAFF)) {
				if (!unitClass.canUseWeapon(item3)) {
					replacementItem = itemData.getSidegradeWeapon(unitClass, item3, inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, true, type, rng);
					if ((isHealer && limitStaves && hasStaff) && replacementItem.getType() == WeaponType.STAFF) {
						replacementItem = null; // We'll handle this later.
					}
					if (replacementItem != null) {
						chapterUnit.setItem3(replacementItem.getID());
					} else {
						chapterUnit.setItem3(0);
					}
					item3 = replacementItem;
				}
			}
			
			if (item3 != null) {
				if (!hasStaff) { hasStaff = item3.getType() == WeaponType.STAFF; }
				if (!hasWeapon) { hasWeapon = itemData.isWeapon(item3); }
			}
			
			int item4ID = chapterUnit.getItem4();
			GBAFEItemData item4 = itemData.itemWithID(item4ID);
			if (!hasItems) { hasItems = item4 != null; }
			if (item4 != null && (itemData.isWeapon(item4) || item4.getType() == WeaponType.STAFF)) {
				if (!unitClass.canUseWeapon(item4)) {
					replacementItem = itemData.getSidegradeWeapon(unitClass, item4, inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, true, type, rng);
					if ((isHealer && limitStaves && hasStaff) && replacementItem.getType() == WeaponType.STAFF) {
						replacementItem = null; // We'll handle this later.
					}
					if (replacementItem != null) {
						chapterUnit.setItem4(replacementItem.getID());
					} else {
						chapterUnit.setItem4(0);
					}
					item4 = replacementItem;
				}
			}
			
			if (item4 != null) {
				if (!hasStaff) { hasStaff = item4.getType() == WeaponType.STAFF; }
				if (!hasWeapon) { hasWeapon = itemData.isWeapon(item4); }
			}
			
			// Sanity check.
			if (hasItems) {
				if (canAttack) {
					if (!hasWeapon) {
						// Make sure enemies that can attack have weapons.
						WeaponRanks ranks = itemData.ranksForClass(unitClass, type);
						List<WeaponType> types = ranks.getTypes();
						types.remove(WeaponType.STAFF);
						if (!types.isEmpty()) {
							for(;;) {
								WeaponType randomType = types.get(rng.nextInt(types.size()));
								GBAFEItemData[] candidates = itemData.itemsOfTypeAndBelowRank(randomType, ranks.rankForType(randomType), false, false);
								if (candidates.length > 0) {
									GBAFEItemData randomWeapon = candidates[rng.nextInt(candidates.length)];
									chapterUnit.giveItems(new int[] {randomWeapon.getID()}, itemData);
									break;
								}
							}
						}
					}
				}
				if (isHealer && !canAttack) {
					assert hasStaff : "No staff for healer.";
				}
			}
		}
	}
	
	private static void validateMinionInventory(ItemAssignmentOptions inventoryOptions, GBAFEChapterUnitData chapterUnit, GBAFECharacterData minionCharacter, ClassDataLoader classData, ItemDataLoader itemData, Random rng) {
		int classID = chapterUnit.getStartingClass();
		GBAFEClassData unitClass = classData.classForID(classID);
		
		boolean canAttack = classData.canClassAttack(classID);
		boolean isHealer = unitClass.getStaffRank() > 0;
		
		boolean limitStaves = isHealer && canAttack;
		boolean hasStaff = false;
		boolean hasWeapon = false;
		boolean hasItems = false;
		
		GBAFEItemData replacementItem = null;
		
		int item1ID = chapterUnit.getItem1();
		GBAFEItemData item1 = itemData.itemWithID(item1ID);
		if (!hasItems) { hasItems = item1 != null; }
		if (item1 != null && (itemData.isWeapon(item1) || item1.getType() == WeaponType.STAFF)) {
			if (!canCharacterUseItem(minionCharacter, item1, itemData)) {
				replacementItem = itemData.getSidegradeWeapon(minionCharacter, unitClass, item1, true, inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, true, false, rng);
				if ((isHealer && limitStaves && hasStaff) && replacementItem.getType() == WeaponType.STAFF) {
					replacementItem = null; // We'll handle this later.
				}
				if (replacementItem != null) {
					chapterUnit.setItem1(replacementItem.getID());
				} else {
					chapterUnit.setItem1(0);
				}
				item1 = replacementItem;
			}
		}
		
		if (item1 != null) {
			if (!hasStaff) { hasStaff = item1.getType() == WeaponType.STAFF; }
			if (!hasWeapon) { hasWeapon = itemData.isWeapon(item1); }
		}
		
		int item2ID = chapterUnit.getItem2();
		GBAFEItemData item2 = itemData.itemWithID(item2ID);
		if (!hasItems) { hasItems = item2 != null; }
		if (item2 != null && (itemData.isWeapon(item2) || item2.getType() == WeaponType.STAFF)) {
			if (!canCharacterUseItem(minionCharacter, item2, itemData)) {
				replacementItem = itemData.getSidegradeWeapon(minionCharacter, unitClass, item2, true, inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, true, false, rng);
				if ((isHealer && limitStaves && hasStaff) && replacementItem.getType() == WeaponType.STAFF) {
					replacementItem = null; // We'll handle this later.
				}
				if (replacementItem != null) {
					chapterUnit.setItem2(replacementItem.getID());
				} else {
					chapterUnit.setItem2(0);
				}
				item2 = replacementItem;
			}
		}
		
		if (item2 != null) {
			if (!hasStaff) { hasStaff = item2.getType() == WeaponType.STAFF; }
			if (!hasWeapon) { hasWeapon = itemData.isWeapon(item2); }
		}
		
		int item3ID = chapterUnit.getItem3();
		GBAFEItemData item3 = itemData.itemWithID(item3ID);
		if (!hasItems) { hasItems = item3 != null; }
		if (item3 != null && (itemData.isWeapon(item3) || item3.getType() == WeaponType.STAFF)) {
			if (!canCharacterUseItem(minionCharacter, item3, itemData)) {
				replacementItem = itemData.getSidegradeWeapon(minionCharacter, unitClass, item3, true, inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, true, false, rng);
				if ((isHealer && limitStaves && hasStaff) && replacementItem.getType() == WeaponType.STAFF) {
					replacementItem = null; // We'll handle this later.
				}
				if (replacementItem != null) {
					chapterUnit.setItem3(replacementItem.getID());
				} else {
					chapterUnit.setItem3(0);
				}
				item3 = replacementItem;
			}
		}
		
		if (item3 != null) {
			if (!hasStaff) { hasStaff = item3.getType() == WeaponType.STAFF; }
			if (!hasWeapon) { hasWeapon = itemData.isWeapon(item3); }
		}
		
		int item4ID = chapterUnit.getItem4();
		GBAFEItemData item4 = itemData.itemWithID(item4ID);
		if (!hasItems) { hasItems = item4 != null; }
		if (item4 != null && (itemData.isWeapon(item4) || item4.getType() == WeaponType.STAFF)) {
			if (!canCharacterUseItem(minionCharacter, item4, itemData)) {
				replacementItem = itemData.getSidegradeWeapon(minionCharacter, unitClass, item4, true, inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, true, false, rng);
				if ((isHealer && limitStaves && hasStaff) && replacementItem.getType() == WeaponType.STAFF) {
					replacementItem = null; // We'll handle this later.
				}
				if (replacementItem != null) {
					chapterUnit.setItem4(replacementItem.getID());
				} else {
					chapterUnit.setItem4(0);
				}
				item4 = replacementItem;
			}
		}
		
		if (item4 != null) {
			if (!hasStaff) { hasStaff = item4.getType() == WeaponType.STAFF; }
			if (!hasWeapon) { hasWeapon = itemData.isWeapon(item4); }
		}
		
		// Sanity check.
		if (hasItems) {
			if (canAttack) {
				if (!hasWeapon) {
					// Make sure enemies that can attack have weapons.
					WeaponRanks ranks = itemData.ranksForCharacter(minionCharacter, unitClass);
					List<WeaponType> types = ranks.getTypes();
					types.remove(WeaponType.STAFF);
					if (!types.isEmpty()) {
						for(;;) {
							WeaponType randomType = types.get(rng.nextInt(types.size()));
							GBAFEItemData[] candidates = itemData.itemsOfTypeAndBelowRank(randomType, ranks.rankForType(randomType), false, false);
							if (candidates.length > 0) {
								GBAFEItemData randomWeapon = candidates[rng.nextInt(candidates.length)];
								chapterUnit.giveItems(new int[] {randomWeapon.getID()}, itemData);
								break;
							}
						}
					}
				}
			}
			if (isHealer && !canAttack) {
				assert hasStaff : "No staff for healer.";
			}
		}
	}
	
	private static GBAFEItemData findReplacementItem(ItemDataLoader itemData, CharacterDataLoader charData, GBAFECharacterData character, GBAFEClassData charClass, GBAFEItemData originalItem, ItemAssignmentOptions inventoryOptions, boolean ranged, boolean melee, boolean forceBasic, boolean excludeBasic, boolean mustBeWeapon, Random rng) {
		GBAFEItemData replacementItem = itemData.getBasicWeaponForCharacter(character, ranged, false, rng);
		if (!forceBasic) {
			if (inventoryOptions.weaponPolicy == WeaponReplacementPolicy.ANY_USABLE || ranged || melee) {
				replacementItem = itemData.getRandomWeaponForCharacter(character, ranged, melee, charData.isEnemyAtAnyPoint(character.getID()), inventoryOptions.assignPromoWeapons, inventoryOptions.assignPoisonWeapons, excludeBasic, mustBeWeapon == false, rng);
			} else {
				replacementItem = itemData.getSidegradeWeapon(character, charClass, originalItem, charData.isEnemyAtAnyPoint(character.getID()), inventoryOptions.weaponPolicy == WeaponReplacementPolicy.STRICT, inventoryOptions.assignPromoWeapons, inventoryOptions.assignPoisonWeapons, mustBeWeapon, rng);
			}
		}
		
		if (replacementItem == null) {
			System.err.println("No suitable replacements for " + character.displayString() + " in class " + charClass.displayString() + " for original item " + originalItem.displayString());
			return null;
		}
		
		if (excludeBasic && itemData.isBasicWeapon(replacementItem.getID())) {
			System.err.println("Tried to exclude basic weapons, but ended up with a basic weapon anyway for " + character.displayString() + " in class " + charClass.displayString() + ": " + replacementItem.displayString());
		}
		
		return replacementItem;
	}
	
	public static void validateCharacterInventory(ItemAssignmentOptions inventoryOptions, GBAFECharacterData character, GBAFEClassData charClass, GBAFEChapterUnitData chapterUnit, Boolean ranged, Boolean melee, CharacterDataLoader charData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData, Boolean forceBasic, boolean excludeBasic, boolean highestRankMustBeWeapon, Random rng) {
		int item1ID = chapterUnit.getItem1();
		GBAFEItemData item1 = itemData.itemWithID(item1ID);
		int item2ID = chapterUnit.getItem2();
		GBAFEItemData item2 = itemData.itemWithID(item2ID);
		int item3ID = chapterUnit.getItem3();
		GBAFEItemData item3 = itemData.itemWithID(item3ID);
		int item4ID = chapterUnit.getItem4();
		GBAFEItemData item4 = itemData.itemWithID(item4ID);
		
		GBAFEItemData[] prfWeapons = itemData.prfWeaponsForClass(charClass.getID());
		Set<Integer> prfIDs = new HashSet<Integer>();
		for (GBAFEItemData prfWeapon : prfWeapons) {
			prfIDs.add(prfWeapon.getID());
		}
		
		Boolean isHealerClass = charClass.getStaffRank() > 0;
		Boolean hasAtLeastOneHealingStaff = false;
		
		Boolean classCanAttack = classData.canClassAttack(charClass.getID());
		Boolean hasAtLeastOneWeapon = false;
		
		Set<GBAFEItemData> itemsToRetain = new HashSet<GBAFEItemData>(Arrays.asList(itemData.specialItemsToRetain()));
		
		Integer highestRankID = chapterUnit.getHighestRankItemID(itemData);
		
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Validating inventory for character 0x" + Integer.toHexString(character.getID()) + " (" + textData.getStringAtIndex(character.getNameIndex(), true) +") in class 0x" + Integer.toHexString(charClass.getID()) + " (" + textData.getStringAtIndex(charClass.getNameIndex(), true) + ")");
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Original Inventory: [0x" + Integer.toHexString(item1ID) + (item1 == null ? "" : " (" + textData.getStringAtIndex(item1.getNameIndex(), true) + ")") + ", 0x" + Integer.toHexString(item2ID) + (item2 == null ? "" : " (" + textData.getStringAtIndex(item2.getNameIndex(), true) + ")") + ", 0x" + Integer.toHexString(item3ID) + (item3 == null ? "" : " (" + textData.getStringAtIndex(item3.getNameIndex(), true) + ")") + ", 0x" + Integer.toHexString(item4ID) + (item4 == null ? "" : " (" + textData.getStringAtIndex(item4.getNameIndex(), true) + ")") + "]");
		
		if (itemsToRetain.stream().anyMatch((item) -> item.getID() == item1ID) == false && (itemData.isWeapon(item1) || (item1 != null && item1.getType() == WeaponType.STAFF))) {
			if (!canCharacterUseItem(character, item1, itemData) || (item1.getWeaponRank() == WeaponRank.PRF && !prfIDs.contains(item1ID))) {
				GBAFEItemData replacementItem = findReplacementItem(itemData, charData, character, charClass, item1, inventoryOptions, ranged, melee, forceBasic, excludeBasic, highestRankMustBeWeapon && highestRankID == item1ID, rng);
				
				if (item1.getWeaponRank() == WeaponRank.S) {
					GBAFEItemData[] topWeapons = topRankWeaponsForClass(charClass, itemData);
					if (topWeapons.length > 0) {
						replacementItem = topWeapons[rng.nextInt(topWeapons.length)];
					}
				}
				
				if (replacementItem != null) {
					if (replacementItem.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(replacementItem.getID()); }
					else { hasAtLeastOneWeapon = hasAtLeastOneWeapon || itemData.isWeapon(replacementItem); }
					chapterUnit.setItem1(replacementItem.getID());
				} else {
					chapterUnit.setItem1(0);
				}
			} else {
				if (item1.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(item1.getID()); }
				else { hasAtLeastOneWeapon = hasAtLeastOneWeapon || itemData.isWeapon(item1); }
			}
		}
		
		if (itemsToRetain.stream().anyMatch((item) -> item.getID() == item2ID) == false && (itemData.isWeapon(item2) || (item2 != null && item2.getType() == WeaponType.STAFF))) {
			if (!canCharacterUseItem(character, item2, itemData) || (item2.getWeaponRank() == WeaponRank.PRF && !prfIDs.contains(item2ID))) {
				GBAFEItemData replacementItem = findReplacementItem(itemData, charData, character, charClass, item2, inventoryOptions, ranged, melee, forceBasic, excludeBasic, highestRankMustBeWeapon && highestRankID == item2ID, rng);
				
				if (item2.getWeaponRank() == WeaponRank.S) {
					GBAFEItemData[] topWeapons = topRankWeaponsForClass(charClass, itemData);
					if (topWeapons.length > 0) {
						replacementItem = topWeapons[rng.nextInt(topWeapons.length)];
					}
				}
				if (replacementItem != null) {
					if (replacementItem.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(replacementItem.getID()); }
					else { hasAtLeastOneWeapon = hasAtLeastOneWeapon || itemData.isWeapon(replacementItem); }
					chapterUnit.setItem2(replacementItem.getID());
				} else {
					chapterUnit.setItem2(0);
				}
			} else {
				if (item2.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(item2.getID()); }
				else { hasAtLeastOneWeapon = hasAtLeastOneWeapon || itemData.isWeapon(item2); }
			}
		}
		
		if (itemsToRetain.stream().anyMatch((item) -> item.getID() == item3ID) == false && (itemData.isWeapon(item3) || (item3 != null && item3.getType() == WeaponType.STAFF))) {
			if (!canCharacterUseItem(character, item3, itemData) || (item3.getWeaponRank() == WeaponRank.PRF && !prfIDs.contains(item3ID))) {
				GBAFEItemData replacementItem = findReplacementItem(itemData, charData, character, charClass, item3, inventoryOptions, ranged, melee, forceBasic, excludeBasic, highestRankMustBeWeapon && highestRankID == item3ID, rng);
				
				if (item3.getWeaponRank() == WeaponRank.S) {
					GBAFEItemData[] topWeapons = topRankWeaponsForClass(charClass, itemData);
					if (topWeapons.length > 0) {
						replacementItem = topWeapons[rng.nextInt(topWeapons.length)];
					}
				}
				if (replacementItem != null) {
					if (replacementItem.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(replacementItem.getID()); }
					else { hasAtLeastOneWeapon = hasAtLeastOneWeapon || itemData.isWeapon(replacementItem); }
					chapterUnit.setItem3(replacementItem.getID());
				} else {
					chapterUnit.setItem3(0);
				}
			} else {
				if (item3.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(item3.getID()); }
				else { hasAtLeastOneWeapon = hasAtLeastOneWeapon || itemData.isWeapon(item3); }
			}
		}
		
		if (itemsToRetain.stream().anyMatch((item) -> item.getID() == item4ID) == false && (itemData.isWeapon(item4) || (item4 != null && item4.getType() == WeaponType.STAFF))) {
			if (!canCharacterUseItem(character, item4, itemData) || (item4.getWeaponRank() == WeaponRank.PRF && !prfIDs.contains(item4ID))) {
				GBAFEItemData replacementItem = findReplacementItem(itemData, charData, character, charClass, item4, inventoryOptions, ranged, melee, forceBasic, excludeBasic, highestRankMustBeWeapon && highestRankID == item4ID, rng);
				
				if (item4.getWeaponRank() == WeaponRank.S) {
					GBAFEItemData[] topWeapons = topRankWeaponsForClass(charClass, itemData);
					if (topWeapons.length > 0) {
						replacementItem = topWeapons[rng.nextInt(topWeapons.length)];
					}
				}
				if (replacementItem != null) {
					if (replacementItem.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(replacementItem.getID()); }
					else { hasAtLeastOneWeapon = hasAtLeastOneWeapon || itemData.isWeapon(replacementItem); }
					chapterUnit.setItem4(replacementItem.getID());
				} else {
					chapterUnit.setItem4(0);
				}
			} else {
				if (item4.getType() == WeaponType.STAFF) { hasAtLeastOneHealingStaff = hasAtLeastOneHealingStaff || itemData.isHealingStaff(item4.getID()); }
				else { hasAtLeastOneWeapon = hasAtLeastOneWeapon || itemData.isWeapon(item4); }
			}
		}
		
		if (isHealerClass && !hasAtLeastOneHealingStaff) {
			chapterUnit.giveItems(new int[] {itemData.getRandomHealingStaff(itemData.weaponRankFromValue(character.getStaffRank()), rng).getID()}, itemData);
		}
		if (classCanAttack && !hasAtLeastOneWeapon) {
			GBAFEItemData basicWeapon = itemData.getBasicWeaponForCharacter(character, ranged, true, rng);
			if (basicWeapon != null) {
				chapterUnit.giveItems(new int[] {basicWeapon.getID()}, itemData);
			}
		}
		
		GBAFEItemData prf = itemData.getPrfWeaponForClass(charClass.getID());
		if (prf != null) {
			chapterUnit.giveItems(new int[] {prf.getID()}, itemData);
		}
		
		if (charData.characterIDRequiresAttack(character.getID())) {
			if (!itemData.isWeapon(itemData.itemWithID(chapterUnit.getItem1()))) {
				int swap = chapterUnit.getItem1();
				if (swap != 0) {
					if (itemData.isWeapon(itemData.itemWithID(chapterUnit.getItem2()))) {
						chapterUnit.setItem1(chapterUnit.getItem2());
						chapterUnit.setItem2(swap);
					} else if (itemData.isWeapon(itemData.itemWithID(chapterUnit.getItem3()))) {
						chapterUnit.setItem1(chapterUnit.getItem3());
						chapterUnit.setItem3(swap);
					} else if (itemData.isWeapon(itemData.itemWithID(chapterUnit.getItem4()))) {
						chapterUnit.setItem1(chapterUnit.getItem4());
						chapterUnit.setItem4(swap);
					}
				}
			}
		}
		
		int newItem1ID = chapterUnit.getItem1();
		int newItem2ID = chapterUnit.getItem2();
		int newItem3ID = chapterUnit.getItem3();
		int newItem4ID = chapterUnit.getItem4();
		
		item1 = itemData.itemWithID(chapterUnit.getItem1());
		item2 = itemData.itemWithID(chapterUnit.getItem2());
		item3 = itemData.itemWithID(chapterUnit.getItem3());
		item4 = itemData.itemWithID(chapterUnit.getItem4());
		
		DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Final Inventory: [0x" + Integer.toHexString(newItem1ID) + (item1 == null ? "" : " (" + textData.getStringAtIndex(item1.getNameIndex(), true) + ")") + ", 0x" + Integer.toHexString(newItem2ID) + (item2 == null ? "" : " (" + textData.getStringAtIndex(item2.getNameIndex(), true) + ")") + ", 0x" + Integer.toHexString(newItem3ID) + (item3 == null ? "" : " (" + textData.getStringAtIndex(item3.getNameIndex(), true) + ")") + ", 0x" + Integer.toHexString(newItem4ID) + (item4 == null ? "" : " (" + textData.getStringAtIndex(item4.getNameIndex(), true) + ")") + "]");
	}
	
	private static GBAFEItemData[] topRankWeaponsForClass(GBAFEClassData characterClass, ItemDataLoader itemData) {
		ArrayList<GBAFEItemData> items = new ArrayList<GBAFEItemData>();
		if (characterClass.getSwordRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.SWORD, WeaponRank.S, false, false, true))); }
		if (characterClass.getLanceRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.LANCE, WeaponRank.S, false, false, true))); }
		if (characterClass.getAxeRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.AXE, WeaponRank.S, false, false, true))); }
		if (characterClass.getBowRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.BOW, WeaponRank.S, false, false, true))); }
		if (characterClass.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.ANIMA, WeaponRank.S, false, false, true))); }
		if (characterClass.getLightRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.LIGHT, WeaponRank.S, false, false, true))); }
		if (characterClass.getDarkRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.DARK, WeaponRank.S, false, false, true))); }
		if (characterClass.getStaffRank() > 0) { items.addAll(Arrays.asList(itemData.itemsOfTypeAndEqualRank(WeaponType.STAFF, WeaponRank.S, false, false, true))); }
		
		return items.toArray(new GBAFEItemData[items.size()]);
	}
	
	private static Boolean canCharacterUseItem(GBAFECharacterData character, GBAFEItemData weapon, ItemDataLoader itemData) {
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
	
	private static void transferBossWeaponLevels(GBAFECharacterData character, GBAFEClassData sourceClass, GBAFEClassData targetClass, GameType type) {
		WeaponRanks ranks = new WeaponRanks(character, sourceClass);
		Optional<WeaponRank> highestRank = ranks.asList().stream().max(WeaponRank::compare);

		WeaponRanks targetRanks = targetClass.getWeaponRanks(true, type);
		for (WeaponType weaponType : WeaponType.getWeaponTypes()) {
			WeaponRank newRank = WeaponRank.NONE;
			if (targetRanks.rankForType(weaponType).isHigherThan(WeaponRank.NONE)) {
				newRank = highestRank.get();
			}
			character.setWeaponRank(weaponType, newRank);
		}
	}
	
}
