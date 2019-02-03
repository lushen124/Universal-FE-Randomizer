package random.gba.randomizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.general.FEBase.GameType;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.PaletteLoader;
import random.gba.loader.TextLoader;
import util.DebugPrinter;
import util.FreeSpaceManager;

public class RecruitmentRandomizer {
	
	static final int rngSalt = 911;
	
	public static void randomizeRecruitment(GameType type, 
			CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, ChapterLoader chapterData, PaletteLoader paletteData, TextLoader textData, FreeSpaceManager freeSpace,
			Random rng) {
		
		// Figure out mapping first.
		List<GBAFECharacterData> characterPool = new ArrayList<GBAFECharacterData>(characterData.canonicalPlayableCharacters());
		
		List<GBAFECharacterData> referenceData = characterPool.stream().map(character -> {
			return character.createCopy(false);
		}).collect(Collectors.toList());
		
		Map<GBAFECharacterData, GBAFECharacterData> characterMap = new HashMap<GBAFECharacterData, GBAFECharacterData>();
		List<GBAFECharacterData> slotsRemaining = new ArrayList<GBAFECharacterData>(characterPool);
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Assign fliers first, since they are restricted in where they can end up.
		// The slots are determined by the character, since we know which characters must be flying normally.
		// The pool is determined by the character's new class (if it was randomized). This pool should always be larger than the number of slots
		// since all fliers are required to randomize into flier classes. There might be other characters that randomized into fliers though.
		// All fliers can promote and demote, so we should be ok here for promotions.
		List<GBAFECharacterData> flierSlotsRemaining = slotsRemaining.stream().filter(character -> (characterData.isFlyingCharacter(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> flierPool = characterPool.stream().filter(character -> (classData.isFlying(character.getClassID()))).collect(Collectors.toList());
		while (!flierSlotsRemaining.isEmpty() && !flierPool.isEmpty()) {
			int slotIndex = rng.nextInt(flierSlotsRemaining.size());
			GBAFECharacterData flierSlot = flierSlotsRemaining.get(slotIndex);
			int fillIndex = rng.nextInt(flierPool.size());
			GBAFECharacterData flier = flierPool.get(fillIndex);
			
			// Shouldn't need to guard this one...
			GBAFECharacterData reference = referenceData.stream().filter(character -> {
				return character.getID() == flier.getID();
			}).findFirst().get();
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigned flier slot 0x" + Integer.toHexString(flierSlot.getID()) + " (" + textData.getStringAtIndex(flierSlot.getNameIndex()) + 
					") to flier 0x" + Integer.toHexString(reference.getID()) + " (" + textData.getStringAtIndex(reference.getNameIndex()) + ")");
			
			characterMap.put(flierSlot, reference);
			
			flierPool.remove(fillIndex);
			flierSlotsRemaining.remove(slotIndex);
			
			slotsRemaining.removeIf(character -> (character.getID() == flierSlot.getID()));
			characterPool.removeIf(character -> (character.getID() == flier.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Prioritize those that require attack next. This generally means lords.
		// Note: these also have to be able to demote.
		List<GBAFECharacterData> attackingSlotsRemaining = slotsRemaining.stream().filter(character -> (characterData.isLordCharacterID(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> attackingPool = characterPool.stream().filter(character -> {
			GBAFEClassData charClass = classData.classForID(character.getClassID());
			if (classData.canClassDemote(charClass.getID())) {
				for (GBAFEClassData demotion : classData.demotionOptions(charClass.getID())) {
					if (!classData.canClassAttack(demotion.getID())) { return false; }
				}
				
				return classData.canClassDemote(charClass.getID());
			} else {
				return classData.canClassAttack(charClass.getID()) && classData.canClassDemote(charClass.getID());
			}
		}).collect(Collectors.toList());
		while (!attackingSlotsRemaining.isEmpty() && !attackingPool.isEmpty()) {
			int slotIndex = rng.nextInt(attackingSlotsRemaining.size());
			GBAFECharacterData attackerSlot = attackingSlotsRemaining.get(slotIndex);
			int fillIndex = rng.nextInt(attackingPool.size());
			GBAFECharacterData attacker = attackingPool.get(fillIndex);
			
			// Shouldn't need to guard this one...
			GBAFECharacterData reference = referenceData.stream().filter(character -> {
				return character.getID() == attacker.getID();
			}).findFirst().get();
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigned attacking slot 0x" + Integer.toHexString(attackerSlot.getID()) + " (" + textData.getStringAtIndex(attackerSlot.getNameIndex()) + 
					") to attacker 0x" + Integer.toHexString(reference.getID()) + " (" + textData.getStringAtIndex(reference.getNameIndex()) + ")");
			
			characterMap.put(attackerSlot, reference);
			
			attackingPool.remove(fillIndex);
			attackingSlotsRemaining.remove(slotIndex);
			
			slotsRemaining.removeIf(character -> (character.getID() == attackerSlot.getID()));
			characterPool.removeIf(character -> (character.getID() == attacker.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Prioritize those with melee/ranged requirements too.
		List<GBAFECharacterData> meleeRequiredSlotsRemaining = slotsRemaining.stream().filter(character -> (characterData.characterIDRequiresMelee(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> meleePool = characterPool.stream().filter(character -> (classData.canSupportMelee(character.getClassID()))).collect(Collectors.toList());
		while (!meleeRequiredSlotsRemaining.isEmpty() && !meleePool.isEmpty()) {
			int slotIndex = rng.nextInt(meleeRequiredSlotsRemaining.size());
			GBAFECharacterData slot = meleeRequiredSlotsRemaining.get(slotIndex);
			
			boolean slotIsPromoted = classData.isPromotedClass(slot.getClassID());
			
			List<GBAFECharacterData> fillCandidates = meleePool.stream().filter(fill -> {
				if (slotIsPromoted) {
					return classData.isPromotedClass(fill.getClassID()) || classData.canClassPromote(fill.getClassID());
				} else {
					return !classData.isPromotedClass(fill.getClassID()) || classData.canClassDemote(fill.getClassID()); 
				}
			}).collect(Collectors.toList());
			if (fillCandidates.isEmpty()) {
				// This shouldn't happen...
				System.err.println("No fill candidate found for melee slot for character ID 0x" + Integer.toHexString(slot.getID()));
				slotsRemaining.remove(slotIndex);
				continue;
			}
			
			int fillIndex = rng.nextInt(fillCandidates.size());
			GBAFECharacterData fill = fillCandidates.get(fillIndex);
			
			// Shouldn't need to guard this one...
			GBAFECharacterData reference = referenceData.stream().filter(character -> {
				return character.getID() == fill.getID();
			}).findFirst().get();
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigned melee slot 0x" + Integer.toHexString(slot.getID()) + " (" + textData.getStringAtIndex(slot.getNameIndex()) + 
					") to melee 0x" + Integer.toHexString(reference.getID()) + " (" + textData.getStringAtIndex(reference.getNameIndex()) + ")");
			
			characterMap.put(slot, reference);
			
			meleePool.remove(fillIndex);
			meleeRequiredSlotsRemaining.remove(slotIndex);
			
			slotsRemaining.removeIf(character -> (character.getID() == slot.getID()));
			characterPool.removeIf(character -> (character.getID() == fill.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		List<GBAFECharacterData> rangeRequiredSlotsRemaining = slotsRemaining.stream().filter(character -> (characterData.characterIDRequiresRange(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> rangePool = characterPool.stream().filter(character -> (classData.canSupportRange(character.getClassID()))).collect(Collectors.toList());
		while (!rangeRequiredSlotsRemaining.isEmpty() && !rangePool.isEmpty()) {
			int slotIndex = rng.nextInt(rangeRequiredSlotsRemaining.size());
			GBAFECharacterData slot = rangeRequiredSlotsRemaining.get(slotIndex);
			
			boolean slotIsPromoted = classData.isPromotedClass(slot.getClassID());
			
			List<GBAFECharacterData> fillCandidates = rangePool.stream().filter(fill -> {
				if (slotIsPromoted) {
					return classData.isPromotedClass(fill.getClassID()) || classData.canClassPromote(fill.getClassID());
				} else {
					return !classData.isPromotedClass(fill.getClassID()) || classData.canClassDemote(fill.getClassID()); 
				}
			}).collect(Collectors.toList());
			if (fillCandidates.isEmpty()) {
				// This shouldn't happen...
				System.err.println("No fill candidate found for range slot for character ID 0x" + Integer.toHexString(slot.getID()));
				slotsRemaining.remove(slotIndex);
				continue;
			}
			
			int fillIndex = rng.nextInt(fillCandidates.size());
			GBAFECharacterData fill = fillCandidates.get(fillIndex);
			
			// Shouldn't need to guard this one...
			GBAFECharacterData reference = referenceData.stream().filter(character -> {
				return character.getID() == fill.getID();
			}).findFirst().get();
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigned ranged slot 0x" + Integer.toHexString(slot.getID()) + " (" + textData.getStringAtIndex(slot.getNameIndex()) + 
					") to ranged 0x" + Integer.toHexString(reference.getID()) + " (" + textData.getStringAtIndex(reference.getNameIndex()) + ")");
						
			characterMap.put(slot, reference);
			
			rangePool.remove(fillIndex);
			rangeRequiredSlotsRemaining.remove(slotIndex);
			
			slotsRemaining.removeIf(character -> (character.getID() == slot.getID()));
			characterPool.removeIf(character -> (character.getID() == fill.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Prioritize those that can't demote into valid classes so they don't get left behind.
		List<GBAFECharacterData> promotedSlotsRemaining = slotsRemaining.stream().filter(character -> (classData.isPromotedClass(character.getClassID()))).collect(Collectors.toList());
		List<GBAFECharacterData> mustBePromotedPool = characterPool.stream().filter(character -> {
			GBAFEClassData charClass = classData.classForID(character.getClassID());
			return !classData.canClassDemote(charClass.getID()) && classData.isPromotedClass(charClass.getID());
		}).collect(Collectors.toList());
		while (!promotedSlotsRemaining.isEmpty() && !mustBePromotedPool.isEmpty()) {
			int slotIndex = rng.nextInt(promotedSlotsRemaining.size());
			GBAFECharacterData promotedSlot = promotedSlotsRemaining.get(slotIndex);
			int fillIndex = rng.nextInt(mustBePromotedPool.size());
			GBAFECharacterData promotedFill = mustBePromotedPool.get(fillIndex);
			
			// Shouldn't need to guard this one...
			GBAFECharacterData reference = referenceData.stream().filter(character -> {
				return character.getID() == promotedFill.getID();
			}).findFirst().get();
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigned promoted slot 0x" + Integer.toHexString(promotedSlot.getID()) + " (" + textData.getStringAtIndex(promotedSlot.getNameIndex()) + 
					") to promoted unit 0x" + Integer.toHexString(reference.getID()) + " (" + textData.getStringAtIndex(reference.getNameIndex()) + ")");
			
			characterMap.put(promotedSlot, reference);
			
			mustBePromotedPool.remove(fillIndex);
			promotedSlotsRemaining.remove(slotIndex);
			
			slotsRemaining.removeIf(character -> (character.getID() == promotedSlot.getID()));
			characterPool.removeIf(character -> (character.getID() == promotedFill.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Assign everybody else randomly.
		// We do have to make sure characters that can get assigned can promote/demote if necessary.
		while (!slotsRemaining.isEmpty() && !characterPool.isEmpty()) {
			int slotIndex = rng.nextInt(slotsRemaining.size());
			GBAFECharacterData slot = slotsRemaining.get(slotIndex);
			
			boolean slotIsPromoted = classData.isPromotedClass(slot.getClassID());
			
			List<GBAFECharacterData> fillCandidates = characterPool.stream().filter(fill -> {
				if (slotIsPromoted) {
					return classData.isPromotedClass(fill.getClassID()) || classData.canClassPromote(fill.getClassID());
				} else {
					return !classData.isPromotedClass(fill.getClassID()) || classData.canClassDemote(fill.getClassID()); 
				}
			}).collect(Collectors.toList());
			if (fillCandidates.isEmpty()) {
				// This shouldn't happen...
				System.err.println("No fill candidate found for slot for character ID 0x" + Integer.toHexString(slot.getID()));
				slotsRemaining.remove(slotIndex);
				continue;
			}
			
			int fillIndex = rng.nextInt(fillCandidates.size());
			GBAFECharacterData fill = fillCandidates.get(fillIndex);
			
			// Shouldn't need to guard this one...
			GBAFECharacterData reference = referenceData.stream().filter(character -> {
				return character.getID() == fill.getID();
			}).findFirst().get();
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigned slot 0x" + Integer.toHexString(slot.getID()) + " (" + textData.getStringAtIndex(slot.getNameIndex()) + 
					") to 0x" + Integer.toHexString(reference.getID()) + " (" + textData.getStringAtIndex(reference.getNameIndex()) + ")");
						
			characterMap.put(slot, reference);
			
			slotsRemaining.remove(slotIndex);
			characterPool.removeIf(character -> (character.getID() == fill.getID()));
		}
		
		Map<String, String> textReplacements = new HashMap<String, String>();
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		if (!characterPool.isEmpty()) {
			for (GBAFECharacterData unassigned: characterPool) {
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Unassigned: 0x" + Integer.toHexString(unassigned.getID()) + " (" + textData.getStringAtIndex(unassigned.getNameIndex()) + ")");
			}
		}
		
		// Process every mapped character.
		// The fill should always be reference data, so it will not have changed from earlier substitutions.
		for (GBAFECharacterData slot : characterMap.keySet()) {
			GBAFECharacterData fill = characterMap.get(slot);
			if (fill != null) {
				// Track the text changes before we change anything.
				// Face IDs
				// Some games have multiple portraits per character. Replace all of them (think Eliwood's many faces in FE7).
				if (characterData.multiPortraitsForCharacter(slot.getID()).isEmpty()) {
					textReplacements.put("[LoadFace][0x" + Integer.toHexString(slot.getFaceID()) + "]", "[LoadFace][0x" + Integer.toHexString(fill.getFaceID()) + "]");
				} else {
					for (int faceID : characterData.multiPortraitsForCharacter(slot.getID())) {
						textReplacements.put("[LoadFace][0x" + Integer.toHexString(faceID) + "]", "[LoadFace][0x" + Integer.toHexString(fill.getFaceID()) + "]");
					}
				}
				textReplacements.put(textData.getStringAtIndex(slot.getNameIndex()), textData.getStringAtIndex(fill.getNameIndex()));
				textReplacements.put(textData.getStringAtIndex(slot.getNameIndex()).toUpperCase(), textData.getStringAtIndex(fill.getNameIndex())); // Sometimes people yell too. :(
				// TODO: pronouns?
				
				// Apply the change to the data.
				fillSlot(slot, fill, characterData, classData, itemData, chapterData, paletteData, textData, type, rng);
			}
		}
		
		// Commit all of the palettes now.
		paletteData.flushChangeQueue(freeSpace);
		
		// Run through the text and modify portraits and names in text.
		
		// Build tokens for pattern
		String patternString = "(" + patternStringFromReplacements(textReplacements) + ")";
		Pattern pattern = Pattern.compile(patternString);
					
		for (int i = 0; i < textData.getStringCount(); i++) {
			String originalStringWithCodes = textData.getStringAtIndex(i, false);
			
			String workingString = new String(originalStringWithCodes);
			Matcher matcher = pattern.matcher(workingString);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String capture = matcher.group(1);
				String replacementKey = textReplacements.get(capture);
				if (replacementKey == null) {
					// Strip out any stuttering.
					String truncated = capture.substring(capture.lastIndexOf('-') + 1);
					replacementKey = textReplacements.get(truncated);
				}
				matcher.appendReplacement(sb, replacementKey);
			}
			
			matcher.appendTail(sb);
			
			textData.setStringAtIndex(i, sb.toString(), true);
		}
	}
	
	private static String patternStringFromReplacements(Map<String, String> replacements) {
		StringBuilder sb = new StringBuilder();
		for (String stringToReplace : replacements.keySet()) {
			boolean isControlCode = stringToReplace.charAt(0) == '[';
			
			if (!isControlCode) { sb.append("\\b[" + stringToReplace.charAt(0) + "-]*"); } // Removes any stuttering (ala "E-E-Eliwood!")
			sb.append(Pattern.compile(stringToReplace.replace("[",  "\\[").replace("]", "\\]"), Pattern.LITERAL));
			if (!isControlCode) { sb.append("\\b"); }
			sb.append('|');
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private static void fillSlot(GBAFECharacterData slot, GBAFECharacterData fill, CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, ChapterLoader chapterData, PaletteLoader paletteData, TextLoader textData, GameType type, Random rng) {
		// Create copy for reference, since we're about to overwrite the slot data.
		// slot is the target for the changes. All changes should be on slot.
		// fill is the source of all of the changes. Fill should NOT be modified.
		GBAFECharacterData slotReference = slot.createCopy(false);
		
		boolean shouldBePromoted = classData.isPromotedClass(slotReference.getClassID());
		boolean isPromoted = classData.isPromotedClass(fill.getClassID());
		
		GBAFEClassData slotSourceClass = classData.classForID(slotReference.getClassID());
		GBAFEClassData slotSourcePromoted = classData.canClassPromote(slotSourceClass.getID()) ? classData.classForID(slotSourceClass.getTargetPromotionID()) : null;
		
		GBAFEClassData sourceClass = classData.classForID(fill.getClassID());
		GBAFEClassData targetClass = null;
		
		GBAFECharacterData[] linkedSlots = characterData.linkedCharactersForCharacter(slotReference);
		for (GBAFECharacterData linkedSlot : linkedSlots) {
			// First, replace the description, and face
			// The name is unnecessary because there's a text find/replace that we apply later.
			linkedSlot.setDescriptionIndex(fill.getDescriptionIndex());
			linkedSlot.setFaceID(fill.getFaceID());
			
			int targetLevel = chapterData.getStartingLevelForCharacter(linkedSlot.getID());
			int sourceLevel = chapterData.getStartingLevelForCharacter(fill.getID());
			
			// Handle Promotion/Demotion leveling as necessary
			if (shouldBePromoted) { targetLevel += 10; }
			if (isPromoted) { sourceLevel += 10; }
			
			int levelsToAdd = targetLevel - sourceLevel;
			
			if (shouldBePromoted && !isPromoted) {
				// Promote Fill.
				if (targetClass == null) {
					List<GBAFEClassData> promotionOptions = classData.promotionOptions(fill.getClassID());
					if (!promotionOptions.isEmpty()) {
						targetClass = promotionOptions.get(rng.nextInt(promotionOptions.size()));
					} else {
						targetClass = sourceClass;
					}
				}
				
				setSlotClass(linkedSlot, targetClass, characterData, classData, itemData, textData, chapterData, rng);
			} else if (!shouldBePromoted && isPromoted) {
				// Demote Fill.
				if (targetClass == null) {
					List<GBAFEClassData> demotionOptions = classData.demotionOptions(fill.getClassID());
					if (!demotionOptions.isEmpty()) {
						targetClass = demotionOptions.get(rng.nextInt(demotionOptions.size()));
					} else {
						targetClass = sourceClass;
					}
				}
				
				setSlotClass(linkedSlot, targetClass, characterData, classData, itemData, textData, chapterData, rng);
			} else {
				// Transfer as is.
				if (targetClass == null) {
					targetClass = sourceClass;
				}
				setSlotClass(linkedSlot, targetClass, characterData, classData, itemData, textData, chapterData, rng);
			}
			
			// Adjust bases if necessary.
			int hpDelta = (int)Math.floor((float)(fill.getHPGrowth() / 100.0) * levelsToAdd);
			int strDelta = (int)Math.floor((float)(fill.getSTRGrowth() / 100.0) * levelsToAdd);
			int sklDelta = (int)Math.floor((float)(fill.getSKLGrowth() / 100.0) * levelsToAdd);
			int spdDelta = (int)Math.floor((float)(fill.getSPDGrowth() / 100.0) * levelsToAdd);
			int lckDelta = (int)Math.floor((float)(fill.getLCKGrowth() / 100.0) * levelsToAdd);
			int defDelta = (int)Math.floor((float)(fill.getDEFGrowth() / 100.0) * levelsToAdd);
			int resDelta = (int)Math.floor((float)(fill.getRESGrowth() / 100.0) * levelsToAdd);
			
			// Clamp the delta to make sure we're not overflowing caps or underflowing to negative.
			int newHP = Math.min(targetClass.getMaxHP() - targetClass.getBaseHP(), Math.max(fill.getBaseHP() + hpDelta, -1 * targetClass.getBaseHP() + 1)); // Gotta have at least 1 HP.
			int newSTR = Math.min(targetClass.getMaxSTR() - targetClass.getBaseSTR(), Math.max(fill.getBaseSTR() + strDelta, -1 * targetClass.getBaseSTR()));
			int newSKL = Math.min(targetClass.getMaxSKL() - targetClass.getBaseSKL(), Math.max(fill.getBaseSKL() + sklDelta, -1 * targetClass.getBaseSKL()));
			int newSPD = Math.min(targetClass.getMaxSPD() - targetClass.getBaseSPD(), Math.max(fill.getBaseSPD() + spdDelta, -1 * targetClass.getBaseSPD()));
			int newLCK = Math.min(targetClass.getMaxLCK() - targetClass.getBaseLCK(), Math.max(fill.getBaseLCK() + lckDelta, -1 * targetClass.getBaseLCK()));
			int newDEF = Math.min(targetClass.getMaxDEF() - targetClass.getBaseDEF(), Math.max(fill.getBaseDEF() + defDelta, -1 * targetClass.getBaseDEF()));
			int newRES = Math.min(targetClass.getMaxRES() - targetClass.getBaseRES(), Math.max(fill.getBaseRES() + resDelta, -1 * targetClass.getBaseRES()));
			
			linkedSlot.setBaseHP(newHP);
			linkedSlot.setBaseSTR(newSTR);
			linkedSlot.setBaseSKL(newSKL);
			linkedSlot.setBaseSPD(newSPD);
			linkedSlot.setBaseLCK(newLCK);
			linkedSlot.setBaseDEF(newDEF);
			linkedSlot.setBaseRES(newRES);
			
			// Transfer growths.
			linkedSlot.setHPGrowth(fill.getHPGrowth());
			linkedSlot.setSTRGrowth(fill.getSTRGrowth());
			linkedSlot.setSKLGrowth(fill.getSKLGrowth());
			linkedSlot.setSPDGrowth(fill.getSPDGrowth());
			linkedSlot.setLCKGrowth(fill.getLCKGrowth());
			linkedSlot.setDEFGrowth(fill.getDEFGrowth());
			linkedSlot.setRESGrowth(fill.getRESGrowth());
			
			linkedSlot.setConstitution(fill.getConstitution());
			linkedSlot.setAffinityValue(fill.getAffinityValue());
		}
		
		// Update palettes to match class.
		if (type == GameType.FE8) {
			paletteData.adaptFE8CharacterToClass(slot.getID(), fill.getID(), slotReference.getClassID(), targetClass.getID(), false);
		} else {
			// Enqueue the change.
			Integer sourceUnpromoted = null;
			if (!shouldBePromoted) { sourceUnpromoted = slotSourceClass.getID(); }
			Integer sourcePromoted = null;
			if (classData.canClassPromote(slotSourceClass.getID())) { sourcePromoted = slotSourcePromoted.getID(); }
			else if (shouldBePromoted) { sourcePromoted = slotSourceClass.getID(); }
			
			Integer targetUnpromoted = null;
			if (!shouldBePromoted) { targetUnpromoted = targetClass.getID(); }
			Integer targetPromoted = null;
			if (classData.canClassPromote(targetClass.getID())) { targetPromoted = targetClass.getTargetPromotionID(); }
			else if (shouldBePromoted) { targetPromoted = targetClass.getID(); }
			
			paletteData.enqueueChange(slot, fill, sourceUnpromoted, targetUnpromoted, sourcePromoted, targetPromoted);
		}
	}
	
	private static void transferWeaponRanks(GBAFECharacterData target, GBAFEClassData targetClass, Random rng) {
		List<Integer> rankValues = new ArrayList<Integer>();
		rankValues.add(target.getSwordRank());
		rankValues.add(target.getLanceRank());
		rankValues.add(target.getAxeRank());
		rankValues.add(target.getBowRank());
		rankValues.add(target.getAnimaRank());
		rankValues.add(target.getLightRank());
		rankValues.add(target.getDarkRank());
		rankValues.add(target.getStaffRank());
		rankValues.removeIf(rank -> (rank == 0));
		
		if (rankValues.isEmpty()) {
			target.setSwordRank(targetClass.getSwordRank());
			target.setLanceRank(targetClass.getLanceRank());
			target.setAxeRank(targetClass.getAxeRank());
			target.setBowRank(targetClass.getBowRank());
			target.setAnimaRank(targetClass.getAnimaRank());
			target.setLightRank(targetClass.getLightRank());
			target.setDarkRank(targetClass.getDarkRank());
			target.setStaffRank(targetClass.getStaffRank());
			
			return;
		}
		
		if (targetClass.getSwordRank() > 0) {
			int randomRankValue = rankValues.get(rng.nextInt(rankValues.size()));
			target.setSwordRank(randomRankValue);
			if (rankValues.size() > 1) {
				rankValues.remove((Integer)randomRankValue);
			}
		} else { target.setSwordRank(0); }
		if (targetClass.getLanceRank() > 0) {
			int randomRankValue = rankValues.get(rng.nextInt(rankValues.size()));
			target.setLanceRank(randomRankValue);
			if (rankValues.size() > 1) {
				rankValues.remove((Integer)randomRankValue);
			}
		} else { target.setLanceRank(0); }
		if (targetClass.getAxeRank() > 0) {
			int randomRankValue = rankValues.get(rng.nextInt(rankValues.size()));
			target.setAxeRank(randomRankValue);
			if (rankValues.size() > 1) {
				rankValues.remove((Integer)randomRankValue);
			}
		} else { target.setAxeRank(0); }
		if (targetClass.getBowRank() > 0) {
			int randomRankValue = rankValues.get(rng.nextInt(rankValues.size()));
			target.setBowRank(randomRankValue);
			if (rankValues.size() > 1) {
				rankValues.remove((Integer)randomRankValue);
			}
		} else { target.setBowRank(0); }
		if (targetClass.getAnimaRank() > 0) {
			int randomRankValue = rankValues.get(rng.nextInt(rankValues.size()));
			target.setAnimaRank(randomRankValue);
			if (rankValues.size() > 1) {
				rankValues.remove((Integer)randomRankValue);
			}
		} else { target.setAnimaRank(0); }
		if (targetClass.getLightRank() > 0) {
			int randomRankValue = rankValues.get(rng.nextInt(rankValues.size()));
			target.setLightRank(randomRankValue);
			if (rankValues.size() > 1) {
				rankValues.remove((Integer)randomRankValue);
			}
		} else { target.setLightRank(0); }
		if (targetClass.getDarkRank() > 0) {
			int randomRankValue = rankValues.get(rng.nextInt(rankValues.size()));
			target.setDarkRank(randomRankValue);
			if (rankValues.size() > 1) {
				rankValues.remove((Integer)randomRankValue);
			}
		} else { target.setDarkRank(0); }
		if (targetClass.getStaffRank() > 0) {
			int randomRankValue = rankValues.get(rng.nextInt(rankValues.size()));
			target.setStaffRank(randomRankValue);
			if (rankValues.size() > 1) {
				rankValues.remove((Integer)randomRankValue);
			}
		} else { target.setStaffRank(0); }
	}
	
	private static void setSlotClass(GBAFECharacterData slot, GBAFEClassData targetClass, CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData, ChapterLoader chapterData, Random rng) {
		slot.setClassID(targetClass.getID());
		transferWeaponRanks(slot, targetClass, rng);
		
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.getCharacterNumber() == slot.getID()) {
					unit.setStartingClass(targetClass.getID());
					
					// Set Inventory.
					ClassRandomizer.validateCharacterInventory(slot, targetClass, unit, characterData.characterIDRequiresRange(slot.getID()), characterData.characterIDRequiresMelee(slot.getID()), classData, itemData, textData, false, rng);
				}
			}
		}
	}
}
