package random.snes.fe4.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4StaticCharacter;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.PromotionMapper;
import ui.model.fe4.FE4ClassOptions;
import ui.model.fe4.FE4ClassOptions.ChildOptions;
import ui.model.fe4.FE4PromotionOptions;

public class FE4PromotionRandomizer {
	
	static final int rngSalt = 192168;
	
	public static void randomizePromotions(FE4PromotionOptions options, FE4ClassOptions classOptions, CharacterDataLoader charData, PromotionMapper promotionMap, Random rng) {
		boolean matchAnalogueForChildren = classOptions != null ? classOptions.childOption == ChildOptions.MATCH_STRICT : false;
		if (options.promotionMode == FE4PromotionOptions.Mode.STRICT) { setPromotions(charData, promotionMap, rng); }
		if (options.promotionMode == FE4PromotionOptions.Mode.LOOSE) { randomizePromotionsLoosely(charData, promotionMap, options.allowMountChanges, options.allowEnemyOnlyPromotedClasses, matchAnalogueForChildren, rng); }
		if (options.promotionMode == FE4PromotionOptions.Mode.RANDOM) { randomizePromotionsRandomly(charData, promotionMap, options.requireCommonWeapon, matchAnalogueForChildren, rng); }
	}
	
	private static void setPromotions(CharacterDataLoader charData, PromotionMapper promotionMap, Random rng) {
		// Gen 1 needs to be processed first because gen 2 might depend on gen 1.
		for (FE4StaticCharacter gen1Char : charData.getGen1Characters()) {
			setMatchingPromotionForStaticCharacter(gen1Char, promotionMap, rng);
		}
		
		// Gen 2 can be done in any order, technically.
		for (FE4StaticCharacter gen2Char : charData.getGen2CommonCharacters()) {
			setMatchingPromotionForStaticCharacter(gen2Char, promotionMap, rng);
		}
		
		for (FE4StaticCharacter sub : charData.getGen2SubstituteCharacters()) {
			setMatchingPromotionForStaticCharacter(sub, promotionMap, rng);
		}
		
		// These are the reason why gen 1 needs to go first.
		for (FE4ChildCharacter child : charData.getAllChildren()) {
			int classID = child.getClassID();
			boolean isFemale = child.isFemale();
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(child.getCharacterID());
			FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(classID);
			
			// If the character is already promoted, don't assign another class.
			if (fe4CharClass.isPromoted()) {
				promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
				continue;
			}
			
			// Otherwise, assign it based on the child's randomized class.
			List<FE4Data.CharacterClass> promotions = Arrays.asList(fe4CharClass.promotionClasses(isFemale)).stream().sorted(FE4Data.CharacterClass.defaultComparator).collect(Collectors.toList());
			
			if (promotions.isEmpty()) {
				promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
			} else {
				promotionMap.setPromotionForCharacter(fe4Char, promotions.get(rng.nextInt(promotions.size())));
			}
		}
	}
	
	private static void setMatchingPromotionForStaticCharacter(FE4StaticCharacter staticChar, PromotionMapper promotionMap, Random rng) {
		FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
		FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
		if (fe4CharClass.isPromoted()) { 
			promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
		} else {
			FE4Data.CharacterClass[] possiblePromotions = fe4CharClass.promotionClasses(staticChar.isFemale());
			FE4Data.CharacterClass promotedClass = FE4Data.CharacterClass.NONE;
			if (possiblePromotions.length > 0) {
				promotedClass = possiblePromotions[rng.nextInt(possiblePromotions.length)];
			}
			while (FE4Data.Character.CharactersThatRequireHorses.contains(fe4Char) && !promotedClass.isHorseback()) {
				promotedClass = possiblePromotions[rng.nextInt(possiblePromotions.length)];
			}
			promotionMap.setPromotionForCharacter(fe4Char, promotedClass);
		}
	}

	private static void randomizePromotionsLoosely(CharacterDataLoader charData, PromotionMapper promotionMap, boolean allowMountChange, boolean allowEnemyClasses, boolean matchAnalogue, Random rng) {
		// Gen 1
		randomizeStaticCharacterPromotionsLoosely(charData.getGen1Characters(), promotionMap, allowMountChange, allowEnemyClasses, rng);
		// Gen 2 Common
		randomizeStaticCharacterPromotionsLoosely(charData.getGen2CommonCharacters(), promotionMap, allowMountChange, allowEnemyClasses, rng);
		// Gen 2 Subs
		randomizeStaticCharacterPromotionsLoosely(charData.getGen2SubstituteCharacters(), promotionMap, allowMountChange, allowEnemyClasses, rng);
		// Gen 2 Children
		for (FE4ChildCharacter gen2Child : charData.getAllChildren()) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(gen2Child.getCharacterID());
			FE4Data.CharacterClass baseClass = FE4Data.CharacterClass.valueOf(gen2Child.getClassID());
			
			// If the class is promoted or has no promotions avaialble, don't set one.
			if (baseClass.isPromoted() || baseClass.promotionClasses(gen2Child.isFemale()).length == 0) {
				promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
				continue;
			}
			
			FE4Data.Character parent = fe4Char.primaryParent();
			FE4Data.Character analogue = fe4Char.getGen1Analogue();
			
			FE4StaticCharacter analogueCharacter = charData.getStaticCharacter(analogue);
			if (matchAnalogue && analogueCharacter != null) {
				FE4Data.CharacterClass analogueClass = FE4Data.CharacterClass.valueOf(analogueCharacter.getClassID());
				// We can only do this if the classes are available, otherwise, we'll need to see if a close analogue exists.
				if (analogueClass.isPromoted()) {
					if (((gen2Child.isFemale() && analogueClass.canBeFemale()) || (!gen2Child.isFemale() && analogueClass.canBeMale()))) {
						promotionMap.setPromotionForCharacter(fe4Char, analogueClass);
					} else {
						// Try to get the one that's closest.
						if (gen2Child.isFemale()) { promotionMap.setPromotionForCharacter(fe4Char, analogueClass.toFemale()); }
						else { promotionMap.setPromotionForCharacter(fe4Char, analogueClass.toMale()); }
					}
					continue;
				}
			}
			
			FE4StaticCharacter parentCharacter = charData.getStaticCharacter(parent);
			List<FE4Data.HolyBloodSlot1> slot1Blood = null;
			List<FE4Data.HolyBloodSlot2> slot2Blood = null;
			List<FE4Data.HolyBloodSlot3> slot3Blood = null;
			if (parentCharacter != null) {
				// We at least know their holy blood so we can use them. In most cases, this is the mother. In Seliph and Altena's case, this is the Father.
				slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(parentCharacter.getHolyBlood1Value());
				slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(parentCharacter.getHolyBlood2Value());
				slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(parentCharacter.getHolyBlood3Value());
			}
			
			Set<FE4Data.CharacterClass> classPool = new HashSet<FE4Data.CharacterClass>(Arrays.asList(baseClass.getLoosePromotionOptions(gen2Child.isFemale(), allowMountChange, allowEnemyClasses, slot1Blood, slot2Blood, slot3Blood)));
			classPool.removeAll(Arrays.asList(fe4Char.blacklistedClasses()));
			FE4Data.CharacterClass[] whitelistedClasses = fe4Char.whitelistedClasses(false);
			if (whitelistedClasses.length > 0) {
				classPool.retainAll(Arrays.asList(whitelistedClasses));
			}
			if (classPool.isEmpty()) {
				promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
			} else {
				List<FE4Data.CharacterClass> classList = classPool.stream().sorted(FE4Data.CharacterClass.defaultComparator).collect(Collectors.toList());
				
				FE4Data.CharacterClass promotion = classList.get(rng.nextInt(classList.size()));
				if (FE4Data.CharacterClass.reducedChanceClasses.contains(promotion)) {
					promotion = classList.get(rng.nextInt(classList.size()));
				}
				
				while (FE4Data.Character.CharactersThatRequireHorses.contains(fe4Char) && !promotion.isHorseback()) {
					promotion = classList.get(rng.nextInt(classList.size()));
				}
				
				promotionMap.setPromotionForCharacter(fe4Char, promotion);
			}
		}
	}
	
	private static void randomizeStaticCharacterPromotionsLoosely(List<FE4StaticCharacter> characters, PromotionMapper promotionMap, boolean allowMountChange, boolean allowEnemyClasses, Random rng) {
		for (FE4StaticCharacter staticChar : characters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			FE4Data.CharacterClass baseClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			
			// If the class is promoted or has no promotions avaialble, don't set one.
			if (baseClass.isPromoted() || baseClass.promotionClasses(staticChar.isFemale()).length == 0) {
				promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
				continue;
			}
			
			List<FE4Data.HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(staticChar.getHolyBlood1Value());
			List<FE4Data.HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(staticChar.getHolyBlood2Value());
			List<FE4Data.HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(staticChar.getHolyBlood3Value());
			
			List<FE4Data.CharacterClass> blacklistedClasses = Arrays.asList(fe4Char.blacklistedClasses());
			List<FE4Data.CharacterClass> promoOptions = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(baseClass.getLoosePromotionOptions(staticChar.isFemale(), allowMountChange, allowEnemyClasses, slot1Blood, slot2Blood, slot3Blood)));
			promoOptions = promoOptions.stream().filter(charClass -> (!blacklistedClasses.contains(charClass))).sorted(FE4Data.CharacterClass.defaultComparator).collect(Collectors.toList());
			
			if (promoOptions.size() > 0) {
				FE4Data.CharacterClass promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				if (FE4Data.CharacterClass.reducedChanceClasses.contains(promotion)) {
					// Reroll once for anybody ending up in these classes.
					promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				}
				while (FE4Data.Character.CharactersThatRequireHorses.contains(fe4Char) && !promotion.isHorseback()) {
					promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				}
				promotionMap.setPromotionForCharacter(fe4Char, promotion);
			}
		}
	}
	
	private static void randomizePromotionsRandomly(CharacterDataLoader charData, PromotionMapper promotionMap, boolean commonWeapons, boolean matchAnalogue, Random rng) {
		// Gen 1
		randomizeStaticCharacterPromotionsRandomly(charData.getGen1Characters(), promotionMap, commonWeapons, rng);
		// Gen 2 Common
		randomizeStaticCharacterPromotionsRandomly(charData.getGen2CommonCharacters(), promotionMap, commonWeapons, rng);
		// Gen 2 Subs
		randomizeStaticCharacterPromotionsRandomly(charData.getGen2SubstituteCharacters(), promotionMap, commonWeapons, rng);
		// Gen 2 Children
		for (FE4ChildCharacter gen2Child : charData.getAllChildren()) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(gen2Child.getCharacterID());
			FE4Data.CharacterClass baseClass = FE4Data.CharacterClass.valueOf(gen2Child.getClassID());
			
			// If the class is promoted or has no promotions avaialble, don't set one.
			if (baseClass.isPromoted() || baseClass.promotionClasses(gen2Child.isFemale()).length == 0) {
				promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
				continue;
			}
			
			FE4Data.Character analogue = fe4Char.getGen1Analogue();
			
			FE4StaticCharacter analogueCharacter = charData.getStaticCharacter(analogue);
			if (matchAnalogue && analogueCharacter != null) {
				FE4Data.CharacterClass analogueClass = FE4Data.CharacterClass.valueOf(analogueCharacter.getClassID());
				// We can only do this if the classes are available, otherwise, we'll need to see if a close analogue exists.
				if (analogueClass.isPromoted()) {
					if (((gen2Child.isFemale() && analogueClass.canBeFemale()) || (!gen2Child.isFemale() && analogueClass.canBeMale()))) {
						promotionMap.setPromotionForCharacter(fe4Char, analogueClass);
					} else {
						// Try to get the one that's closest.
						if (gen2Child.isFemale()) { promotionMap.setPromotionForCharacter(fe4Char, analogueClass.toFemale()); }
						else { promotionMap.setPromotionForCharacter(fe4Char, analogueClass.toMale()); }
					}
					continue;
				}
			}
			
			FE4Data.CharacterClass[] options = FE4Data.CharacterClass.promotedClasses.toArray(new FE4Data.CharacterClass[FE4Data.CharacterClass.promotedClasses.size()]);
			if (commonWeapons) {
				options = baseClass.sharedWeaponPromotions(gen2Child.isFemale());
			}
			
			List<FE4Data.CharacterClass> blacklistedClasses = Arrays.asList(fe4Char.blacklistedClasses());
			List<FE4Data.CharacterClass> promoOptions = Arrays.asList(options).stream().filter(charClass -> (!blacklistedClasses.contains(charClass))).sorted(FE4Data.CharacterClass.defaultComparator).collect(Collectors.toList());
			
			if (promoOptions.size() > 0) {
				FE4Data.CharacterClass promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				if (FE4Data.CharacterClass.reducedChanceClasses.contains(promotion)) {
					// Reroll once for anybody ending up in these classes.
					promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				}
				
				while (FE4Data.Character.CharactersThatRequireHorses.contains(fe4Char) && !promotion.isHorseback()) {
					promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				}
				
				promotionMap.setPromotionForCharacter(fe4Char, promotion);
			}
		}
	}
	
	private static void randomizeStaticCharacterPromotionsRandomly(List<FE4StaticCharacter> characters, PromotionMapper promotionMap, boolean commonWeapons, Random rng) {
		for (FE4StaticCharacter staticChar : characters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			FE4Data.CharacterClass targetPromotion = promotionMap.getPromotionForCharacter(fe4Char);
			if (targetPromotion == FE4Data.CharacterClass.NONE) { continue; } // Probably already promoted.
			FE4Data.CharacterClass baseClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			
			FE4Data.CharacterClass[] options = FE4Data.CharacterClass.promotedClasses.toArray(new FE4Data.CharacterClass[FE4Data.CharacterClass.promotedClasses.size()]);
			if (commonWeapons) {
				options = baseClass.sharedWeaponPromotions(staticChar.isFemale());
			}
			
			List<FE4Data.CharacterClass> blacklistedClasses = Arrays.asList(fe4Char.blacklistedClasses());
			List<FE4Data.CharacterClass> promoOptions = Arrays.asList(options).stream().filter(charClass -> (!blacklistedClasses.contains(charClass))).sorted(FE4Data.CharacterClass.defaultComparator).collect(Collectors.toList());
			
			if (promoOptions.size() > 0) {
				FE4Data.CharacterClass promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				if (FE4Data.CharacterClass.reducedChanceClasses.contains(promotion)) {
					// Reroll once for anybody ending up in these classes.
					promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				}
				
				while (FE4Data.Character.CharactersThatRequireHorses.contains(fe4Char) && !promotion.isHorseback()) {
					promotion = promoOptions.get(rng.nextInt(promoOptions.size()));
				}
				
				promotionMap.setPromotionForCharacter(fe4Char, promotion);
			} else {
				System.err.println("No promotions available for " + fe4Char.toString() + " (" + baseClass.toString() + ")");
			}
		}
	}
}
