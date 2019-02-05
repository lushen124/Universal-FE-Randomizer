package random.snes.fe4.randomizer;

import java.util.List;
import java.util.Random;

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Data;
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
		for (FE4Data.Character fe4Char : promotionMap.allPromotableCharacters()) {
			int classID = FE4Data.CharacterClass.NONE.ID;
			boolean isFemale = false;
			if (fe4Char.isChild()) {
				FE4ChildCharacter child = charData.getChildCharacter(fe4Char);
				if (child != null) {
					classID = child.getClassID();
					isFemale = child.isFemale();
				}
			} else {
				FE4StaticCharacter staticChar = charData.getStaticCharacter(fe4Char);
				if (staticChar != null) {
					classID = staticChar.getClassID();
					isFemale = staticChar.isFemale();
				}
			}
			
			if (classID == FE4Data.CharacterClass.NONE.ID) { continue; }
			
			FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(classID);
			if (fe4CharClass.isPromoted()) { 
				promotionMap.setPromotionForCharacter(fe4Char, FE4Data.CharacterClass.NONE);
			} else {
				FE4Data.CharacterClass[] possiblePromotions = fe4CharClass.promotionClasses(isFemale);
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
			FE4Data.CharacterClass targetPromotion = promotionMap.getPromotionForCharacter(fe4Char);
			if (targetPromotion == FE4Data.CharacterClass.NONE) { continue; }
			FE4Data.CharacterClass baseClass = FE4Data.CharacterClass.valueOf(gen2Child.getClassID());
			// We have no blood information for children, so we have to be a bit safer with the loose randomization.
			FE4Data.CharacterClass[] promoOptions = baseClass.getLoosePromotionOptions(gen2Child.isFemale(), allowMountChange, allowEnemyClasses, null, null, null);
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
	
	private static void randomizeStaticCharacterPromotionsLoosely(List<FE4StaticCharacter> characters, PromotionMapper promotionMap, boolean allowMountChange, boolean allowEnemyClasses, Random rng) {
		for (FE4StaticCharacter staticChar : characters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			FE4Data.CharacterClass targetPromotion = promotionMap.getPromotionForCharacter(fe4Char);
			if (targetPromotion == FE4Data.CharacterClass.NONE) { continue; } // This character is probably already promoted, or at least does not promote naturally. Leave it alone.
			FE4Data.CharacterClass baseClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
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
			FE4Data.CharacterClass targetPromotion = promotionMap.getPromotionForCharacter(fe4Char);
			if (targetPromotion == FE4Data.CharacterClass.NONE) { continue; }
			FE4Data.CharacterClass baseClass = FE4Data.CharacterClass.valueOf(gen2Child.getClassID());
			
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
