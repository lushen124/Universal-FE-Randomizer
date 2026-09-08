package random.gba.randomizer.service;

import java.util.*;
import java.util.stream.Collectors;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEStatDto;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponRanks;
import fedata.gba.general.WeaponType;
import fedata.general.FEBase.GameType;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import ui.model.RecruitmentOptions;
import ui.model.RecruitmentOptions.ClassMode;
import util.DebugPrinter;

public class GBASlotAdjustmentService {

	/**
	 * This Constant is for the level at which a character is assumed to be promoted with regards to Slot Adjustment.
	 * F.e. if Eirika -> Seth she would only get 9 levels then promote. 
	 * Rather than going to level 20 for promotion and getting 19 levels.
	 */
	private static final int ASSUMED_PROMOTION_LEVEL = 10;

	/**
	 * This constant describes the threshold after which the number of Autolevels will be reduced a bit.
	 * 
	 * This help with Making sure htat characters which receive a lot of positive levels aren't too strong, 
	 * and characters that get negative levels don't become too weak. 
	 */
	private static final int AUTOLEVEL_REDUCTION_THRESHOLD = 10;
	
	/**
	 * Used by Recruitment Randomization and Character Shuffling to Calculate the following information:
	 * 
	 * A) Should the character, be demoted, promoted or are they already in the right tier of class.
	 * B) How many auto levels should they receive
	 * C) Which promotion bonuses will they receive
	 */
	public static ClassAdjustmentDto handleClassAdjustment(int targetLevel, int sourceLevel,
			boolean shouldBePromoted, boolean isPromoted, Random rng, ClassDataLoader classData, 
			GBAFEClassData targetClass, GBAFEClassData fillSourceClass, GBAFECharacterData fill, 
			GBAFEClassData slotSourceClass, RecruitmentOptions options, TextLoader textData, DebugPrinter.Key key) {
		ClassAdjustmentDto dto = new ClassAdjustmentDto();
		if (shouldBePromoted) { targetLevel += ASSUMED_PROMOTION_LEVEL; }
		if (isPromoted) { sourceLevel += ASSUMED_PROMOTION_LEVEL; }
		dto.levelAdjustment = targetLevel - sourceLevel;
		
		// To make newly created pre-promotes not completely busted (since they probably had higher growths than real pre-promotes)
		// we'll subtract a few levels from their autoleveling amount, assuming they get a lot (like 10).
		if (!isPromoted && shouldBePromoted && dto.levelAdjustment > AUTOLEVEL_REDUCTION_THRESHOLD) {
			DebugPrinter.log(key, "Dropping 3 additional levels for new prepromotes.");
			dto.levelAdjustment  -= 3;
		} else if(isPromoted && !shouldBePromoted && dto.levelAdjustment < -AUTOLEVEL_REDUCTION_THRESHOLD) {
			// Likewise, don't ruin former prepromotes as much
			DebugPrinter.log(key, "Dropping 3 less levels for newly demoted units.");
			dto.levelAdjustment  += 3;
		}
		
		if (shouldBePromoted && !isPromoted) {
			DebugPrinter.log(key, "Promoting [" + textData.getStringAtIndex(fill.getNameIndex(), true) + "]");
			// Promote Fill.
			if (targetClass == null) {
				List<GBAFEClassData> promotionOptions = classData.promotionOptions(fill.getClassID());
				DebugPrinter.log(key, "Promotion Options: [" + String.join(", ", promotionOptions.stream().map(charClass -> (textData.getStringAtIndex(charClass.getNameIndex(), true))).collect(Collectors.toList())) + "]");
				if (!promotionOptions.isEmpty()) {
					targetClass = promotionOptions.get(rng.nextInt(promotionOptions.size()));
					if (!classData.isPromotedClass(targetClass.getID())) {
						// This is really only for FE8. If a trainee switches into a promoted unit, there's two promotions that need to be done.
						dto.promoBonuses.add(targetClass.getPromoBonuses());
						promotionOptions = classData.promotionOptions(targetClass.getID());
						DebugPrinter.log(key, "Promotion Options: [" + String.join(", ", promotionOptions.stream().map(charClass -> (textData.getStringAtIndex(charClass.getNameIndex(), true))).collect(Collectors.toList())) + "]");
						if (!promotionOptions.isEmpty()) {
							targetClass = promotionOptions.get(rng.nextInt(promotionOptions.size()));
							dto.levelAdjustment += 10;
						}
					}
				} else {
					targetClass = fillSourceClass;
				}
				
				if (options!= null && options.classMode == ClassMode.USE_SLOT) {
					targetClass = slotSourceClass;
				}
				
				DebugPrinter.log(key, "Selected Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
			}
			dto.promoBonuses.add(targetClass.getPromoBonuses());
			// For some reason, some promoted class seem to have lower bases than their unpromoted variants (FE8 lords are an example). If they are lower, adjust upwards.
			dto.promoBonuses.add(GBAFEStatDto.upAdjust(targetClass.getBases(), fillSourceClass.getBases()));

		} else if (!shouldBePromoted && isPromoted) {
			DebugPrinter.log(key, "Demoting [" + textData.getStringAtIndex(fill.getNameIndex(), true) + "]");
			// Demote Fill.
			if (targetClass == null) {
				List<GBAFEClassData> demotionOptions = classData.demotionOptions(fill.getClassID());
				DebugPrinter.log(key, "Demotion Options: [" + String.join(", ", demotionOptions.stream().map(charClass -> (textData.getStringAtIndex(charClass.getNameIndex(), true))).collect(Collectors.toList())) + "]");
				if (!demotionOptions.isEmpty()) {
					targetClass = demotionOptions.get(rng.nextInt(demotionOptions.size()));
				} else {
					targetClass = fillSourceClass;
				}
				
				if (options!= null && options.classMode == ClassMode.USE_SLOT) {
					targetClass = slotSourceClass;
				}
				
				DebugPrinter.log(key, "Selected Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
			}
			
			dto.promoBonuses.add(fillSourceClass.getPromoBonuses()
					.multiply(-1)); // Multiply the promotion bonuses with -1 to make them demotion bonuses 
			
			// For some reason, some promoted class seem to have lower bases than their unpromoted variants (FE8 lords are an example). If our demoted class has higher bases, adjust downwards
			dto.promoBonuses.add(GBAFEStatDto.downAdjust(targetClass.getBases(), fillSourceClass.getBases()));
		} else {
			// Transfer as is.
			if (targetClass == null) {
				if (options!= null && options.classMode == ClassMode.USE_FILL) { targetClass = fillSourceClass; }
				else if (options!= null && options.classMode == ClassMode.USE_SLOT) { targetClass = slotSourceClass; }
				else {
					// This shouldn't happen, but default to fill.
					targetClass = fillSourceClass;
				}
			}
			DebugPrinter.log(key, "No Promotion/Demotion Needed. Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
		}
		
		dto.targetClass = targetClass;
		
		DebugPrinter.log(key, String.format("Finished Adjusting class for character %s, fill sourceClass %s, slot source class %s, new class %s, should receive %d auto levels, and Promotion bonuses: %s",
				fill.displayString(), fillSourceClass.displayString(), slotSourceClass.displayString(), dto.targetClass.displayString(), dto.levelAdjustment, dto.promoBonuses));
		return dto;
	}
	

	/**
	 * Calculates the personal bases for the unit after applying the promotions /
	 * demotions and required autolevels.
	 * 
	 * Ensures that the Personal bases don't lead to over / underflows by clamping.
	 * 
	 * @param bases          the personal bases prior to the current change.
	 * @param growths        the growths that will be used for the auto levels
	 * @param promoBonuses   a list with all the promotion (or demotion) bonuses
	 *                       that will be given to the unit
	 * @param levelsRequired the number of autolevels to apply
	 * @param targetClass    the class that the unit will be in, used for clamping
	 * @param key            the Key for which to log the changes
	 * @return a GBAFEStatDto with the new personal bases.
	 */
	public static GBAFEStatDto autolevel(GBAFEStatDto bases, GBAFEStatDto growths, List<GBAFEStatDto> promoBonuses
			, int levelsRequired, GBAFEClassData targetClass, DebugPrinter.Key key) {
		// initialize a new DAO with the original Bases
		GBAFEStatDto newBases = new GBAFEStatDto(bases);
		GBAFEStatDto classBases = targetClass.getBases();
		newBases.add(classBases);
		DebugPrinter.log(key, String.format("Original Bases: %s%n", newBases.toString()));
		// Add all necessary promotion or demotions
		if (!promoBonuses.isEmpty()) {
			GBAFEStatDto totalPromoChanges = new GBAFEStatDto(promoBonuses);
			DebugPrinter.log(key, String.format("Total Promotion Changes: %s%n", totalPromoChanges.toString()));
			newBases.add(totalPromoChanges); // Demotion bonuses are already negative
			DebugPrinter.log(key, String.format("Stats after Promotion / Demotion: %s%n", newBases.toString()));
		}

		// add the required number of levels
		newBases.add(calculateLevels(growths, levelsRequired, key));
		DebugPrinter.log(key, String.format("Stats after Autolevels: %s%n", newBases.toString()));
		
		// Now we have the calculated auto leveled stats.
		// Here we must ensure that the character doesn't over or underflow, so we add the Stats to the class bases, and clamp it to the max and min stats.
		GBAFEStatDto totalBases = new GBAFEStatDto(Arrays.asList(newBases));
		totalBases = totalBases.clamp(GBAFEStatDto.MINIMUM_STATS, targetClass.getCaps()); // Clamp to prevent over or underflow
		DebugPrinter.log(key, String.format("Theoretical final Stats after clamp: %s%n", totalBases.toString()));

		// Now we remove the Class bases again and are left with proper Personal Bases
		newBases = totalBases.subtract(classBases);
		DebugPrinter.log(key, String.format("Proper personal bases: %s", newBases.toString()));
		return newBases;
	}

	public static GBAFEStatDto calculateLevels(GBAFEStatDto growths, int levelsRequired, DebugPrinter.Key key) {
		GBAFEStatDto levelGains = new GBAFEStatDto();
		levelGains.hp += (int) Math.floor((growths.hp / 100.0) * levelsRequired);
		levelGains.str += (int) Math.floor((growths.str / 100.0) * levelsRequired);
		levelGains.skl += (int) Math.floor((growths.skl / 100.0) * levelsRequired);
		levelGains.spd += (int) Math.floor((growths.spd / 100.0) * levelsRequired);
		levelGains.def += (int) Math.floor((growths.def / 100.0) * levelsRequired);
		levelGains.res += (int) Math.floor((growths.res / 100.0) * levelsRequired);
		levelGains.lck += (int) Math.floor((growths.lck / 100.0) * levelsRequired);
		DebugPrinter.log(key, String.format("Stats from %d Autolevels: %s with growths %s %n", levelsRequired, levelGains.toString(), growths.toString()));
		return levelGains;
	}

	/**
	 * Method to unify weapon rank transfer from one class to another
	 *
	 * @param slot {@link GBAFECharacterData} which contains the original weapon ranks of the current character slot
	 * @param sourceClass the sourceClass that the character originated from
	 * @param targetClass the targetClass that the character is now
	 * @param rng rng to decide what rank will be chosen for which weapon type, assuming there is more than one option
	 */
	public static void transferWeaponRanks(GBAFECharacterData slot, GBAFEClassData sourceClass, GBAFEClassData targetClass, GameType type, Random rng) {
		WeaponRanks weaponRanks = new WeaponRanks(slot, sourceClass);
		WeaponRanks targetClassRanks = targetClass.getWeaponRanks(true, type);

		List<WeaponRank> rankValues = weaponRanks.asList().stream()
				.filter(rank -> !WeaponRank.NONE.equals(rank))
				.sorted(WeaponRank::compare)
				.collect(Collectors.toList());

		if (rankValues.isEmpty()) {
			slot.setWeaponRanks(targetClassRanks);
			return;
		}

		int targetWeaponUsage = targetClass.getWeaponRanks(true, type).getTypes().size();

		while (rankValues.size() > targetWeaponUsage) {
			rankValues.remove(0); // Remove the lowest rank if we're filling less weapons than we have to work with.
		}

		for (WeaponType weaponType : WeaponType.getWeaponTypes()) {
			if (WeaponRank.NONE.equals(targetClassRanks.rankForType(weaponType))) {
				slot.setWeaponRank(weaponType, WeaponRank.NONE);
				continue;
			}
			WeaponRank randomRank = rankValues.get(rng.nextInt(rankValues.size()));
			if (rankValues.size() > 1) {
				rankValues.remove(randomRank);
			}

			// The lowest rank dark tome is D, so make sure to round up
			if (WeaponType.DARK.equals(weaponType) && WeaponRank.D.isHigherThan(randomRank)) {
				randomRank = WeaponRank.D;
			}
			slot.setWeaponRank(weaponType, randomRank);
		}
	}
}
