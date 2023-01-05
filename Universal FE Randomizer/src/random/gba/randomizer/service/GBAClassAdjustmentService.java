package random.gba.randomizer.service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEStatDAO;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.TextLoader;
import ui.model.RecruitmentOptions;
import ui.model.RecruitmentOptions.ClassMode;
import util.DebugPrinter;

public class GBAClassAdjustmentService {

	
	/**
	 * Used by Recruitment Randomization and Character Shuffling to Calculate the following information:
	 * 
	 * A) Should the character, be demoted, promoted or are they already in the right tier of class.
	 * B) How many auto levels should they receive
	 * C)  
	 */
	public static ClassAdjustmentDAO handleClassAdjustment(int targetLevel, int sourceLevel,
			boolean shouldBePromoted, boolean isPromoted, Random rng, ClassDataLoader classData, 
			GBAFEClassData targetClass, GBAFEClassData fillSourceClass, GBAFECharacterData fill, 
			GBAFEClassData slotSourceClass, RecruitmentOptions options, TextLoader textData) {
		ClassAdjustmentDAO dao = new ClassAdjustmentDAO();
		if (shouldBePromoted) { targetLevel += 10; }
		if (isPromoted) { sourceLevel += 10; }
		dao.levelAdjustment = targetLevel - sourceLevel;
		
		// To make newly created pre-promotes not completely busted (since they probably had higher growths than real pre-promotes)
		// we'll subtract a few levels from their autoleveling amount.
		if (!isPromoted && shouldBePromoted) {
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Dropping 3 additional levels for new prepromotes.");
			dao.levelAdjustment  -= 3;
		}
		
		if (shouldBePromoted && !isPromoted) {
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Promoting [" + textData.getStringAtIndex(fill.getNameIndex(), true) + "]");
			// Promote Fill.
			if (targetClass == null) {
				List<GBAFEClassData> promotionOptions = classData.promotionOptions(fill.getClassID());
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Promotion Options: [" + String.join(", ", promotionOptions.stream().map(charClass -> (textData.getStringAtIndex(charClass.getNameIndex(), true))).collect(Collectors.toList())) + "]");
				if (!promotionOptions.isEmpty()) {
					targetClass = promotionOptions.get(rng.nextInt(promotionOptions.size()));
					if (!classData.isPromotedClass(targetClass.getID())) {
						// This is really only for FE8. If a trainee switches into a promoted unit, there's two promotions that need to be done.
						dao.promoBonuses.add(targetClass.getPromoBonuses());
						promotionOptions = classData.promotionOptions(targetClass.getID());
						DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Promotion Options: [" + String.join(", ", promotionOptions.stream().map(charClass -> (textData.getStringAtIndex(charClass.getNameIndex(), true))).collect(Collectors.toList())) + "]");
						if (!promotionOptions.isEmpty()) {
							targetClass = promotionOptions.get(rng.nextInt(promotionOptions.size()));
							dao.levelAdjustment += 10;
						}
					}
				} else {
					targetClass = fillSourceClass;
				}
				
				if (options!= null && options.classMode == ClassMode.USE_SLOT) {
					targetClass = slotSourceClass;
				}
				
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Selected Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
			}
			dao.promoBonuses.add(targetClass.getPromoBonuses());
			// For some reason, some promoted class seem to have lower bases than their unpromoted variants (FE8 lords are an example). If they are lower, adjust upwards.
			dao.promoBonuses.add(GBAFEStatDAO.upAdjust(targetClass.getBases(), fillSourceClass.getBases()));

		} else if (!shouldBePromoted && isPromoted) {
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Demoting [" + textData.getStringAtIndex(fill.getNameIndex(), true) + "]");
			// Demote Fill.
			if (targetClass == null) {
				List<GBAFEClassData> demotionOptions = classData.demotionOptions(fill.getClassID());
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Demotion Options: [" + String.join(", ", demotionOptions.stream().map(charClass -> (textData.getStringAtIndex(charClass.getNameIndex(), true))).collect(Collectors.toList())) + "]");
				if (!demotionOptions.isEmpty()) {
					targetClass = demotionOptions.get(rng.nextInt(demotionOptions.size()));
				} else {
					targetClass = fillSourceClass;
				}
				
				if (options!= null && options.classMode == ClassMode.USE_SLOT) {
					targetClass = slotSourceClass;
				}
				
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Selected Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
			}
			
			dao.promoBonuses.add(fillSourceClass.getPromoBonuses().multiply(-1));
			
			// For some reason, some promoted class seem to have lower bases than their unpromoted variants (FE8 lords are an example). If our demoted class has higher bases, adjust downwards
			dao.promoBonuses.add(GBAFEStatDAO.downAdjust(targetClass.getBases(), fillSourceClass.getBases()));
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
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "No Promotion/Demotion Needed. Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
		}
		
		dao.targetClass = targetClass;
		return dao;
	}

	public static GBAFEStatDAO autolevel(GBAFEStatDAO bases, GBAFEStatDAO growths, List<GBAFEStatDAO> promoBonuses,
			boolean promotionRequired, boolean demotionRequired, int levelsRequired) {
		// initialize a new DAO with the original Bases
		GBAFEStatDAO newBases = new GBAFEStatDAO(bases);

		// Add all necessary promotion or demotions
		if (promotionRequired || demotionRequired) {
			newBases.add(new GBAFEStatDAO(promoBonuses).multiply(promotionRequired ? 1 : -1));
		}

		// add the required number of levels
		newBases.add(calculateLevels(growths, levelsRequired));
		return newBases;
	}

	public static GBAFEStatDAO calculateLevels(GBAFEStatDAO growths, int levelsRequired) {
		GBAFEStatDAO levelGains = new GBAFEStatDAO();
		levelGains.hp += (int) Math.floor((growths.hp / 100.0) * levelsRequired);
		levelGains.str += (int) Math.floor((growths.str / 100.0) * levelsRequired);
		levelGains.skl += (int) Math.floor((growths.skl / 100.0) * levelsRequired);
		levelGains.spd += (int) Math.floor((growths.spd / 100.0) * levelsRequired);
		levelGains.def += (int) Math.floor((growths.def / 100.0) * levelsRequired);
		levelGains.res += (int) Math.floor((growths.res / 100.0) * levelsRequired);
		levelGains.lck += (int) Math.floor((growths.lck / 100.0) * levelsRequired);
		return levelGains;
	}

}
