package random.snes.fe4.randomizer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4Data.CharacterClass;
import fedata.snes.fe4.FE4StaticCharacter;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.PromotionMapper;
import ui.fe4.FE4PromotionOptions;

public class FE4PromotionRandomizer {
	
	static final int rngSalt = 192168;
	
	public static void randomizePromotions(FE4PromotionOptions options, CharacterDataLoader charData, PromotionMapper promotionMap, Random rng) {
		if (options.promotionMode == FE4PromotionOptions.Mode.STRICT) { setPromotions(charData, promotionMap, rng); }
		if (options.promotionMode == FE4PromotionOptions.Mode.LOOSE) { randomizePromotionsLoosely(charData, promotionMap, options.allowMountChanges, options.allowEnemyOnlyPromotedClasses, rng); }
		if (options.promotionMode == FE4PromotionOptions.Mode.RANDOM) { randomizePromotionsRandomly(charData, promotionMap, options.requireCommonWeapon, rng); }
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
			
			FE4Data.Character analogue = fe4Char.getGen1Analogue();
			FE4Data.CharacterClass referenceClass = FE4Data.CharacterClass.valueOf(charData.getStaticCharacter(analogue).getClassID());
			
			if (referenceClass.isPromoted()) {
				// Set it as is.
				promotionMap.setPromotionForCharacter(fe4Char, referenceClass);
			} else {
				// Get the promotion for the analogue.
				Set<FE4Data.CharacterClass> classPool = new HashSet<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(isFemale)));
				classPool.removeAll(Arrays.asList(fe4Char.blacklistedClasses()));
				FE4Data.CharacterClass[] whitelistedClasses = fe4Char.whitelistedClasses(false);
				if (whitelistedClasses.length > 0) {
					classPool.retainAll(Arrays.asList(whitelistedClasses));
				}
				if (classPool.isEmpty()) {
					// Forget the reference class, use our own class.
					FE4Data.CharacterClass childClass = FE4Data.CharacterClass.valueOf(classID);
					classPool = new HashSet<FE4Data.CharacterClass>(Arrays.asList(childClass.promotionClasses(isFemale)));
				}
				if (classPool.isEmpty()) {
					promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
				} else {
					List<FE4Data.CharacterClass> classList = classPool.stream().sorted(new Comparator<FE4Data.CharacterClass>() {
						@Override
						public int compare(CharacterClass arg0, CharacterClass arg1) {
							return Integer.compare(arg0.ID, arg1.ID);
						}
					}).collect(Collectors.toList());
					
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

	private static void randomizePromotionsLoosely(CharacterDataLoader charData, PromotionMapper promotionMap, boolean allowMountChange, boolean allowEnemyClasses, Random rng) {
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
				List<FE4Data.CharacterClass> classList = classPool.stream().sorted(new Comparator<FE4Data.CharacterClass>() {
					@Override
					public int compare(CharacterClass arg0, CharacterClass arg1) {
						return Integer.compare(arg0.ID, arg1.ID);
					}
				}).collect(Collectors.toList());
				
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
			
			FE4Data.CharacterClass[] promoOptions = baseClass.getLoosePromotionOptions(staticChar.isFemale(), allowMountChange, allowEnemyClasses, slot1Blood, slot2Blood, slot3Blood);
			if (promoOptions.length > 0) {
				FE4Data.CharacterClass promotion = promoOptions[rng.nextInt(promoOptions.length)];
				if (FE4Data.CharacterClass.reducedChanceClasses.contains(promotion)) {
					// Reroll once for anybody ending up in these classes.
					promotion = promoOptions[rng.nextInt(promoOptions.length)];
				}
				while (FE4Data.Character.CharactersThatRequireHorses.contains(fe4Char) && !promotion.isHorseback()) {
					promotion = promoOptions[rng.nextInt(promoOptions.length)];
				}
				promotionMap.setPromotionForCharacter(fe4Char, promotion);
			}
		}
	}
	
	private static void randomizePromotionsRandomly(CharacterDataLoader charData, PromotionMapper promotionMap, boolean commonWeapons, Random rng) {
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
			
			FE4Data.CharacterClass[] promoOptions = FE4Data.CharacterClass.promotedClasses.toArray(new FE4Data.CharacterClass[FE4Data.CharacterClass.promotedClasses.size()]);
			if (commonWeapons) {
				promoOptions = baseClass.sharedWeaponPromotions(gen2Child.isFemale());
			}
			
			if (promoOptions.length > 0) {
				FE4Data.CharacterClass promotion = promoOptions[rng.nextInt(promoOptions.length)];
				if (FE4Data.CharacterClass.reducedChanceClasses.contains(promotion)) {
					// Reroll once for anybody ending up in these classes.
					promotion = promoOptions[rng.nextInt(promoOptions.length)];
				}
				
				while (FE4Data.Character.CharactersThatRequireHorses.contains(fe4Char) && !promotion.isHorseback()) {
					promotion = promoOptions[rng.nextInt(promoOptions.length)];
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
			
			FE4Data.CharacterClass[] promoOptions = FE4Data.CharacterClass.promotedClasses.toArray(new FE4Data.CharacterClass[FE4Data.CharacterClass.promotedClasses.size()]);
			if (commonWeapons) {
				promoOptions = baseClass.sharedWeaponPromotions(staticChar.isFemale());
			}
			
			if (promoOptions.length > 0) {
				FE4Data.CharacterClass promotion = promoOptions[rng.nextInt(promoOptions.length)];
				if (FE4Data.CharacterClass.reducedChanceClasses.contains(promotion)) {
					// Reroll once for anybody ending up in these classes.
					promotion = promoOptions[rng.nextInt(promoOptions.length)];
				}
				
				while (FE4Data.Character.CharactersThatRequireHorses.contains(fe4Char) && !promotion.isHorseback()) {
					promotion = promoOptions[rng.nextInt(promoOptions.length)];
				}
				
				promotionMap.setPromotionForCharacter(fe4Char, promotion);
			} else {
				System.err.println("No promotions available for " + fe4Char.toString() + " (" + baseClass.toString() + ")");
			}
		}
	}
}
