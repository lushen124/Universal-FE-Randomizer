package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapPortraitData;
import fedata.gba.general.WeaponRank;
import fedata.general.FEBase.GameType;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import random.general.RelativeValueMapper;
import ui.model.ItemAssignmentOptions;
import ui.model.RecruitmentOptions;
import ui.model.RecruitmentOptions.BaseStatAutolevelType;
import ui.model.RecruitmentOptions.GrowthAdjustmentMode;
import ui.model.RecruitmentOptions.StatAdjustmentMode;
import util.DebugPrinter;
import util.FreeSpaceManager;

public class RecruitmentRandomizer {
	
	static final int rngSalt = 911;
	
	public static Map<GBAFECharacterData, GBAFECharacterData> randomizeRecruitment(RecruitmentOptions options, ItemAssignmentOptions inventoryOptions, GameType type, 
			CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, ChapterLoader chapterData, TextLoader textData, FreeSpaceManager freeSpace,
			Random rng) {
		
		// Figure out mapping first.
		List<GBAFECharacterData> characterPool = new ArrayList<GBAFECharacterData>(characterData.canonicalPlayableCharacters());
		characterPool.removeIf(character -> (characterData.charactersExcludedFromRandomRecruitment().contains(character)));
		
		Map<Integer, GBAFECharacterData> referenceData = characterPool.stream().map(character -> {
			GBAFECharacterData copy = character.createCopy(false);
			copy.lock();
			return copy;
		}).collect(Collectors.toMap(charData -> (charData.getID()), charData -> (charData)));
		
		boolean separateByGender = !options.allowCrossGender;
		
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
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigning fliers...");
		List<SlotAssignment> assignedSlots = shuffleCharactersInPool(false, separateByGender, flierSlotsRemaining, flierPool, characterMap, referenceData, characterData, classData, textData, rng);
		for (SlotAssignment assignment : assignedSlots) {
			slotsRemaining.removeIf(character -> (character.getID() == assignment.slot.getID()));
			characterPool.removeIf(character -> (character.getID() == assignment.fill.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Prioritize those with melee/ranged requirements too.
		List<GBAFECharacterData> meleeRequiredSlotsRemaining = slotsRemaining.stream().filter(character -> (characterData.characterIDRequiresMelee(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> meleePool = characterPool.stream().filter(character -> (classData.canSupportMelee(character.getClassID()))).collect(Collectors.toList());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigning Required Melee Units...");
		assignedSlots = shuffleCharactersInPool(false, separateByGender, meleeRequiredSlotsRemaining, meleePool, characterMap, referenceData, characterData, classData, textData, rng);
		for (SlotAssignment assignment : assignedSlots) {
			slotsRemaining.removeIf(character -> (character.getID() == assignment.slot.getID()));
			characterPool.removeIf(character -> (character.getID() == assignment.fill.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		List<GBAFECharacterData> rangeRequiredSlotsRemaining = slotsRemaining.stream().filter(character -> (characterData.characterIDRequiresRange(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> rangePool = characterPool.stream().filter(character -> (classData.canSupportRange(character.getClassID()))).collect(Collectors.toList());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigning Required Ranged Units...");
		assignedSlots = shuffleCharactersInPool(false, separateByGender, rangeRequiredSlotsRemaining, rangePool, characterMap, referenceData, characterData, classData, textData, rng);
		for (SlotAssignment assignment : assignedSlots) {
			slotsRemaining.removeIf(character -> (character.getID() == assignment.slot.getID()));
			characterPool.removeIf(character -> (character.getID() == assignment.fill.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Prioritize anybody that HAS to promote. This usually isn't an issue except for FE6, where the one class that can attack but can't promote is thieves.
		List<GBAFECharacterData> mustPromoteSlots = slotsRemaining.stream().filter(character -> (characterData.mustPromote(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> promotablePool = characterPool.stream().filter(character -> {
			GBAFEClassData charClass = classData.classForID(character.getClassID());
			if (classData.isPromotedClass(charClass.getID()) && classData.canClassDemote(charClass.getID())) {
				// Roll in the requires attack here as well.
				for (GBAFEClassData demotedClass : classData.demotionOptions(charClass.getID())) {
					if (classData.canClassAttack(demotedClass.getID()) == false) { return false; }
				}
				return true;
			} else if (!classData.isPromotedClass(charClass.getID()) && classData.canClassPromote(charClass.getID())) {
				return classData.canClassAttack(charClass.getID()); // Roll in the attack requirement as well.
			}
			return false; // Everything else is not allowed.
		}).collect(Collectors.toList());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigning Required Promotion Slots...");
		assignedSlots = shuffleCharactersInPool(false, separateByGender, mustPromoteSlots, promotablePool, characterMap, referenceData, characterData, classData, textData, rng);
		for (SlotAssignment assignment : assignedSlots) {
			slotsRemaining.removeIf(character -> (character.getID() == assignment.slot.getID()));
			characterPool.removeIf(character -> (character.getID() == assignment.fill.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Prioritize those that require attack next. This generally means lords.
		// Note: these also have to be able to demote.
		List<GBAFECharacterData> attackingSlotsRemaining = slotsRemaining.stream().filter(character -> (characterData.mustAttack(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> attackingPool = characterPool.stream().filter(character -> {
			GBAFEClassData charClass = classData.classForID(character.getClassID());
			// Promoted class that can demote should check all of their demotion options. Any demotion that can't attack disqualifies the class.
			if (classData.isPromotedClass(charClass.getID()) && classData.canClassDemote(charClass.getID())) {
				for (GBAFEClassData demotedClass : classData.demotionOptions(charClass.getID())) {
					if (classData.canClassAttack(demotedClass.getID()) == false) { return false; }
				}
			}
			return classData.canClassAttack(charClass.getID());
		}).collect(Collectors.toList());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigning Required Attackers...");
		assignedSlots = shuffleCharactersInPool(false, separateByGender, attackingSlotsRemaining, attackingPool, characterMap, referenceData, characterData, classData, textData, rng);
		for (SlotAssignment assignment : assignedSlots) {
			slotsRemaining.removeIf(character -> (character.getID() == assignment.slot.getID()));
			characterPool.removeIf(character -> (character.getID() == assignment.fill.getID()));
		}
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		// Prioritize those that can't demote into valid classes so they don't get left behind.
		// Unlike the other criteria, this one is primarily based off the character's class and not the slot.
		// We only need to do this if the pool is not empty.
		List<GBAFECharacterData> promotedSlotsRemaining = slotsRemaining.stream().filter(character -> (classData.isPromotedClass(character.getClassID()))).collect(Collectors.toList());
		List<GBAFECharacterData> mustBePromotedPool = characterPool.stream().filter(character -> {
			GBAFEClassData charClass = classData.classForID(character.getClassID());
			return !classData.canClassDemote(charClass.getID()) && classData.isPromotedClass(charClass.getID());
		}).collect(Collectors.toList());
		if (!mustBePromotedPool.isEmpty()) {
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigning non-demotable classes...");
			assignedSlots = shuffleCharactersInPool(false, separateByGender, promotedSlotsRemaining, mustBePromotedPool, characterMap, referenceData, characterData, classData, textData, rng);
			for (SlotAssignment assignment : assignedSlots) {	
				slotsRemaining.removeIf(character -> (character.getID() == assignment.slot.getID()));
				characterPool.removeIf(character -> (character.getID() == assignment.fill.getID()));
			}
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		}
		
		// Assign everybody else randomly.
		// We do have to make sure characters that can get assigned can promote/demote if necessary.
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigning the remainder of the characters...");
		assignedSlots = shuffleCharactersInPool(true, separateByGender, slotsRemaining, characterPool, characterMap, referenceData, characterData, classData, textData, rng);
		for (SlotAssignment assignment : assignedSlots) {	
			slotsRemaining.removeIf(character -> (character.getID() == assignment.slot.getID()));
			characterPool.removeIf(character -> (character.getID() == assignment.fill.getID()));
		}
		
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots Remaining: " + slotsRemaining.size());
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool Size: " + characterPool.size());
		
		if (!characterPool.isEmpty()) {
			for (GBAFECharacterData unassigned: characterPool) {
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Unassigned: 0x" + Integer.toHexString(unassigned.getID()) + " (" + textData.getStringAtIndex(unassigned.getNameIndex(), true) + ")");
			}
		}
		
		assert characterPool.isEmpty() : "Unable to satisfy all constraints for random recruitment.";
		
		Map<String, String> textReplacements = new HashMap<String, String>();
		
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
				textReplacements.put(textData.getStringAtIndex(slot.getNameIndex(), true).trim(), textData.getStringAtIndex(fill.getNameIndex(), true).trim());
				textReplacements.put(textData.getStringAtIndex(slot.getNameIndex(), true).toUpperCase().trim(), textData.getStringAtIndex(fill.getNameIndex(), true).trim()); // Sometimes people yell too. :(
				// TODO: pronouns?
				
				// Apply the change to the data.
				fillSlot(options, inventoryOptions, slot, fill, characterData, classData, itemData, chapterData, textData, type, rng);
			}
		}
		
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
			
			textData.setStringAtIndex(i, sb.toString());
		}
		
		for (GBAFEWorldMapData worldMapEvent : chapterData.allWorldMapEvents()) {
			for (GBAFEWorldMapPortraitData portrait : worldMapEvent.allPortraits()) {
				for (GBAFECharacterData slot : characterMap.keySet()) {
					GBAFECharacterData slotReference = referenceData.get(slot.getID());
					GBAFECharacterData fill = characterMap.get(slot);
					if (portrait.getFaceID() == slotReference.getFaceID()) {
						portrait.setFaceID(fill.getFaceID());
						break;
					}
				}
			}
		}
		
		return characterMap;
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
	
	private static class SlotAssignment {
		GBAFECharacterData slot;
		GBAFECharacterData fill;
		
		private SlotAssignment(GBAFECharacterData slot, GBAFECharacterData fill) {
			this.slot = slot;
			this.fill = fill;
		}
	}
	
	private static List<SlotAssignment> shuffleCharactersInPool(boolean assignAll, boolean separateByGender, List<GBAFECharacterData> slots, List<GBAFECharacterData> pool, Map<GBAFECharacterData, GBAFECharacterData> characterMap, Map<Integer, GBAFECharacterData> referenceData, 
			CharacterDataLoader charData, ClassDataLoader classData, TextLoader textData, Random rng) {
		List<SlotAssignment> additions = new ArrayList<SlotAssignment>();
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slots: " + String.join(", ", slots.stream().map(character -> (String.format("%s[%s]", textData.getStringAtIndex(character.getNameIndex(), true), classData.debugStringForClass(character.getClassID())))).collect(Collectors.toList())));
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Pool: " + String.join(", ", pool.stream().map(character -> String.format("%s[%s]", textData.getStringAtIndex(character.getNameIndex(), true), classData.debugStringForClass(character.getClassID()))).collect(Collectors.toList())));
		
		if (separateByGender) {
			List<GBAFECharacterData> femaleSlots = slots.stream().filter(character -> (charData.isFemale(character.getID()))).collect(Collectors.toList());
			List<GBAFECharacterData> femalePool = pool.stream().filter(character -> (charData.isFemale(character.getID()))).collect(Collectors.toList());
			
			List<GBAFECharacterData> maleSlots = slots.stream().filter(character -> (charData.isFemale(character.getID()) == false)).collect(Collectors.toList());
			List<GBAFECharacterData> malePool = pool.stream().filter(character -> (charData.isFemale(character.getID()) == false)).collect(Collectors.toList());
			
			additions.addAll(shuffle(femaleSlots, femalePool, referenceData, classData, textData, rng));
			additions.addAll(shuffle(maleSlots, malePool, referenceData, classData, textData, rng));
			
			if (assignAll) {
				List<GBAFECharacterData> remainingSlots = new ArrayList<GBAFECharacterData>(femaleSlots);
				remainingSlots.addAll(maleSlots);
				List<GBAFECharacterData> remainingPool = new ArrayList<GBAFECharacterData>(femalePool);
				remainingPool.addAll(malePool);
				additions.addAll(shuffle(remainingSlots, remainingPool, referenceData, classData, textData, rng));
			}
		} else {
			additions.addAll(shuffle(slots, pool, referenceData, classData, textData, rng));
		}
		
		for (SlotAssignment assignment : additions) {
			characterMap.put(assignment.slot, assignment.fill);
		}
			
		return additions;
	}
	
	private static List<SlotAssignment> shuffle(List<GBAFECharacterData> slots, List<GBAFECharacterData> pool, Map<Integer, GBAFECharacterData> referenceData, 
			ClassDataLoader classData, TextLoader textData, Random rng) {
		List<SlotAssignment> additions = new ArrayList<SlotAssignment>();
		if (slots.isEmpty()) { return additions; }
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Shuffling Slots: " + String.join(", ", slots.stream().map(charData -> (String.format("%s[%s]", textData.getStringAtIndex(charData.getNameIndex(), true), classData.debugStringForClass(charData.getClassID())))).collect(Collectors.toList())));
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Character Pool: " + String.join(", ", pool.stream().map(charData -> String.format("%s[%s]", textData.getStringAtIndex(charData.getNameIndex(), true), classData.debugStringForClass(charData.getClassID()))).collect(Collectors.toList())));
		
		List<GBAFECharacterData> promotedSlots = slots.stream().filter(slot -> (classData.isPromotedClass(slot.getClassID()))).collect(Collectors.toList());
		List<GBAFECharacterData> cantDemotePool = pool.stream().filter(fill -> (classData.isPromotedClass(fill.getClassID()) == true && classData.canClassDemote(fill.getClassID()) == false)).collect(Collectors.toList());
		List<GBAFECharacterData> promotedSlotCandidates = pool.stream().filter(fill -> (classData.isPromotedClass(fill.getClassID()) == true || classData.canClassPromote(fill.getClassID()) == true)).collect(Collectors.toList());
		
		List<GBAFECharacterData> unpromotedSlots = slots.stream().filter(slot -> (classData.isPromotedClass(slot.getClassID()) == false)).collect(Collectors.toList());
		List<GBAFECharacterData> cantPromotePool = pool.stream().filter(fill -> (classData.isPromotedClass(fill.getClassID()) == false && classData.canClassPromote(fill.getClassID()) == false)).collect(Collectors.toList());
		List<GBAFECharacterData> unpromotedSlotCandidates = pool.stream().filter(fill -> (classData.isPromotedClass(fill.getClassID()) == false || classData.canClassDemote(fill.getClassID()) == true)).collect(Collectors.toList());
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "\tPromoted: " + String.join(", ", promotedSlots.stream().map(charData -> (textData.getStringAtIndex(charData.getNameIndex(), true))).collect(Collectors.toList())));
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "\tCan't Demote: " + String.join(", ", cantDemotePool.stream().map(charData -> (textData.getStringAtIndex(charData.getNameIndex(), true))).collect(Collectors.toList())));
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "\tUnpromoted: " + String.join(", ", unpromotedSlots.stream().map(charData -> (textData.getStringAtIndex(charData.getNameIndex(), true))).collect(Collectors.toList())));
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "\tCan't Promote: " + String.join(", ", cantPromotePool.stream().map(charData -> (textData.getStringAtIndex(charData.getNameIndex(), true))).collect(Collectors.toList())));
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "\tCandidates for Promoted Slots: " + String.join(", ", promotedSlotCandidates.stream().map(charData -> (textData.getStringAtIndex(charData.getNameIndex(), true))).collect(Collectors.toList())));
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "\tCandidates for Unpromoted Slots: " + String.join(", ", unpromotedSlotCandidates.stream().map(charData -> (textData.getStringAtIndex(charData.getNameIndex(), true))).collect(Collectors.toList())));
		
		// If there are those that can't demote, prioritize them first.
		List<SlotAssignment> intermediate = shuffle(promotedSlots, cantDemotePool, referenceData, textData, rng);
		for (SlotAssignment assignment : intermediate) {
			slots.removeIf(currentSlot -> (currentSlot.getID() == assignment.slot.getID()));
			pool.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
			promotedSlotCandidates.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
			unpromotedSlotCandidates.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
		}
		additions.addAll(intermediate);
		
		// Same for any character that can't promote.
		intermediate = shuffle(unpromotedSlots, cantPromotePool, referenceData, textData, rng);
		for (SlotAssignment assignment : intermediate) {
			slots.removeIf(currentSlot -> (currentSlot.getID() == assignment.slot.getID()));
			pool.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
			promotedSlotCandidates.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
			unpromotedSlotCandidates.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
		}
		additions.addAll(intermediate);
		
		// Prioritize slots now using either promoted or promotable candidates in promoted slots.
		intermediate = shuffle(promotedSlots, promotedSlotCandidates, referenceData, textData, rng);
		for (SlotAssignment assignment : intermediate) {
			slots.removeIf(currentSlot -> (currentSlot.getID() == assignment.slot.getID()));
			pool.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
			unpromotedSlotCandidates.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
		}
		additions.addAll(intermediate);
		
		// Same for unpromoted slots.
		intermediate = shuffle(unpromotedSlots, unpromotedSlotCandidates, referenceData, textData, rng);
		for (SlotAssignment assignment : intermediate) {
			slots.removeIf(currentSlot -> (currentSlot.getID() == assignment.slot.getID()));
			pool.removeIf(currentFill -> (currentFill.getID() == assignment.fill.getID()));
		}
		additions.addAll(intermediate);
		
		// Anything else left over...
		if (!slots.isEmpty() && !pool.isEmpty()) {
			intermediate = shuffle(slots, pool, referenceData, textData, rng);
			additions.addAll(intermediate);
		}
		
		return additions;
	}
	
	private static List<SlotAssignment> shuffle(List<GBAFECharacterData> slots, List<GBAFECharacterData> candidates, Map<Integer, GBAFECharacterData> referenceData, TextLoader textData, Random rng) {
		List<SlotAssignment> additions = new ArrayList<SlotAssignment>();
		
		while (!slots.isEmpty() && !candidates.isEmpty()) {
			int slotIndex = rng.nextInt(slots.size());
			GBAFECharacterData slot = slots.get(slotIndex);
			int fillIndex = rng.nextInt(candidates.size());
			GBAFECharacterData fill = candidates.get(fillIndex);
			
			GBAFECharacterData reference = referenceData.get(fill.getID());
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigned slot 0x" + Integer.toHexString(slot.getID()) + " (" + textData.getStringAtIndex(slot.getNameIndex(), true) + 
					") to 0x" + Integer.toHexString(reference.getID()) + " (" + textData.getStringAtIndex(reference.getNameIndex(), true) + ")");
			
			additions.add(new SlotAssignment(slot, reference));
			
			slots.remove(slotIndex);
			candidates.remove(fillIndex);
		}
		
		return additions;
	}

	private static void fillSlot(RecruitmentOptions options, ItemAssignmentOptions inventoryOptions, GBAFECharacterData slot, GBAFECharacterData fill, CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, ChapterLoader chapterData, TextLoader textData, GameType type, Random rng) {
		// Create copy for reference, since we're about to overwrite the slot data.
		// slot is the target for the changes. All changes should be on slot.
		// fill is the source of all of the changes. Fill should NOT be modified.
		GBAFECharacterData slotReference = slot.createCopy(false);
		
		boolean shouldBePromoted = classData.isPromotedClass(slotReference.getClassID());
		boolean isPromoted = classData.isPromotedClass(fill.getClassID());
		
		GBAFEClassData slotSourceClass = classData.classForID(slotReference.getClassID());
		
		GBAFEClassData fillSourceClass = classData.classForID(fill.getClassID());
		GBAFEClassData targetClass = null;
		
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Filling Slot [" + textData.getStringAtIndex(slotReference.getNameIndex(), true) + "](" + textData.getStringAtIndex(slotSourceClass.getNameIndex(), true) + ") with [" +
				textData.getStringAtIndex(fill.getNameIndex(), true) + "](" + textData.getStringAtIndex(fillSourceClass.getNameIndex(), true) + ")");
		
		GBAFECharacterData[] linkedSlots = characterData.linkedCharactersForCharacter(slotReference);
		for (GBAFECharacterData linkedSlot : linkedSlots) {
			// First, replace the description, and face
			// The name is unnecessary because there's a text find/replace that we apply later.
			linkedSlot.setDescriptionIndex(fill.getDescriptionIndex());
			linkedSlot.setFaceID(fill.getFaceID());
			
			linkedSlot.setIsLord(characterData.isLordCharacterID(slotReference.getID()));
			
			int targetLevel = linkedSlot.getLevel();
			int sourceLevel = fill.getLevel();
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slot level: " + Integer.toString(targetLevel) + "\tFill Level: " + Integer.toString(sourceLevel));
			
			// Handle Promotion/Demotion leveling as necessary
			if (shouldBePromoted) { targetLevel += 15; }
			if (isPromoted) { sourceLevel += 15; }
			
			int levelsToAdd = targetLevel - sourceLevel;
			
			// To make newly created pre-promotes not completely busted (since they probably had higher growths than real pre-promotes)
			// we'll subtract a few levels from their autoleveling amount.
			if (!isPromoted && shouldBePromoted) {
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Dropping 5 additional levels for new prepromotes.");
				levelsToAdd -= 5;
			}
			
			int promoAdjustHP = 0;
			int promoAdjustSTR = 0;
			int promoAdjustSKL = 0;
			int promoAdjustSPD = 0;
			int promoAdjustDEF = 0;
			int promoAdjustRES = 0;
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Adjusted Slot level: " + Integer.toString(targetLevel) + "\tAdjusted Fill Level: " + Integer.toString(sourceLevel) + "\tLevels To Add: " + Integer.toString(levelsToAdd));
			
			if (shouldBePromoted && !isPromoted) {
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Promoting [" + textData.getStringAtIndex(fill.getNameIndex(), true) + "]");
				// Promote Fill.
				if (targetClass == null) {
					List<GBAFEClassData> promotionOptions = classData.promotionOptions(fill.getClassID());
					DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Promotion Options: [" + String.join(", ", promotionOptions.stream().map(charClass -> (textData.getStringAtIndex(charClass.getNameIndex(), true))).collect(Collectors.toList())) + "]");
					if (!promotionOptions.isEmpty()) {
						targetClass = promotionOptions.get(rng.nextInt(promotionOptions.size()));
					} else {
						targetClass = fillSourceClass;
					}
					DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Selected Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
					
					// For some reason, some promoted class seem to have lower bases than their unpromoted variants (FE8 lords are an example). If they are lower, adjust upwards.
					if (targetClass.getBaseHP() < fillSourceClass.getBaseHP()) { promoAdjustHP = fillSourceClass.getBaseHP() - targetClass.getBaseHP() + targetClass.getPromoHP(); }
					if (targetClass.getBaseSTR() < fillSourceClass.getBaseSTR()) { promoAdjustSTR = fillSourceClass.getBaseSTR() - targetClass.getBaseSTR() + targetClass.getPromoSTR(); }
					if (targetClass.getBaseSKL() < fillSourceClass.getBaseSKL()) { promoAdjustSKL = fillSourceClass.getBaseSKL() - targetClass.getBaseSKL() + targetClass.getPromoSKL(); }
					if (targetClass.getBaseSPD() < fillSourceClass.getBaseSPD()) { promoAdjustSPD = fillSourceClass.getBaseSPD() - targetClass.getBaseSPD() + targetClass.getPromoSPD(); }
					if (targetClass.getBaseDEF() < fillSourceClass.getBaseDEF()) { promoAdjustDEF = fillSourceClass.getBaseDEF() - targetClass.getBaseDEF() + targetClass.getPromoDEF(); }
					if (targetClass.getBaseRES() < fillSourceClass.getBaseRES()) { promoAdjustRES = fillSourceClass.getBaseRES() - targetClass.getBaseRES() + targetClass.getPromoRES(); }
				}
				
				setSlotClass(inventoryOptions, linkedSlot, targetClass, characterData, classData, itemData, textData, chapterData, rng);
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
					DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Selected Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
					
					// For some reason, some promoted class seem to have lower bases than their unpromoted variants (FE8 lords are an example). If our demoted class has higher bases, adjust downwards
					if (targetClass.getBaseHP() > fillSourceClass.getBaseHP()) { promoAdjustHP = targetClass.getBaseHP() - fillSourceClass.getBaseHP() + fillSourceClass.getPromoHP(); promoAdjustHP *= -1; }
					if (targetClass.getBaseSTR() > fillSourceClass.getBaseSTR()) { promoAdjustSTR = targetClass.getBaseSTR() - fillSourceClass.getBaseSTR() + fillSourceClass.getPromoSTR(); promoAdjustSTR *= -1; }
					if (targetClass.getBaseSKL() > fillSourceClass.getBaseSKL()) { promoAdjustSKL = targetClass.getBaseSKL() - fillSourceClass.getBaseSKL() + fillSourceClass.getPromoSKL(); promoAdjustSKL *= -1; }
					if (targetClass.getBaseSPD() > fillSourceClass.getBaseSPD()) { promoAdjustSPD = targetClass.getBaseSPD() - fillSourceClass.getBaseSPD() + fillSourceClass.getPromoSPD(); promoAdjustSPD *= -1; }
					if (targetClass.getBaseDEF() > fillSourceClass.getBaseDEF()) { promoAdjustDEF = targetClass.getBaseDEF() - fillSourceClass.getBaseDEF() + fillSourceClass.getPromoDEF(); promoAdjustDEF *= -1; }
					if (targetClass.getBaseRES() > fillSourceClass.getBaseRES()) { promoAdjustRES = targetClass.getBaseRES() - fillSourceClass.getBaseRES() + fillSourceClass.getPromoRES(); promoAdjustRES *= -1; }
				}
				
				setSlotClass(inventoryOptions, linkedSlot, targetClass, characterData, classData, itemData, textData, chapterData, rng);
			} else {
				// Transfer as is.
				if (targetClass == null) {
					targetClass = fillSourceClass;
				}
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "No Promotion/Demotion Needed. Class: " + (targetClass != null ? textData.getStringAtIndex(targetClass.getNameIndex(), true) : "None"));
				setSlotClass(inventoryOptions, linkedSlot, targetClass, characterData, classData, itemData, textData, chapterData, rng);
			}
			
			int targetHPGrowth = fill.getHPGrowth();
			int targetSTRGrowth = fill.getSTRGrowth();
			int targetSKLGrowth = fill.getSKLGrowth();
			int targetSPDGrowth = fill.getSPDGrowth();
			int targetDEFGrowth = fill.getDEFGrowth();
			int targetRESGrowth = fill.getRESGrowth();
			int targetLCKGrowth = fill.getLCKGrowth();
			
			if (options.growthMode == GrowthAdjustmentMode.USE_FILL) {
				// Do nothing in this case. This is the default.
			} else if (options.growthMode == GrowthAdjustmentMode.USE_SLOT) {
				// Overwrite with slot growths.
				targetHPGrowth = slot.getHPGrowth();
				targetSTRGrowth = slot.getSTRGrowth();
				targetSKLGrowth = slot.getSKLGrowth();
				targetSPDGrowth = slot.getSPDGrowth();
				targetDEFGrowth = slot.getDEFGrowth();
				targetRESGrowth = slot.getRESGrowth();
				targetLCKGrowth = slot.getLCKGrowth();
			} else if (options.growthMode == GrowthAdjustmentMode.RELATIVE_TO_SLOT) {
				List<Integer> mappedStats = RelativeValueMapper.mappedValues(Arrays.asList(slot.getHPGrowth(), slot.getSTRGrowth(), slot.getSKLGrowth(), slot.getSPDGrowth(), slot.getDEFGrowth(), slot.getRESGrowth(), slot.getLCKGrowth()), 
						Arrays.asList(fill.getHPGrowth(), fill.getSTRGrowth(), fill.getSKLGrowth(), fill.getSPDGrowth(), fill.getDEFGrowth(), fill.getRESGrowth(), fill.getLCKGrowth()));
				targetHPGrowth = mappedStats.get(0);
				targetSTRGrowth = mappedStats.get(1);
				targetSKLGrowth = mappedStats.get(2);
				targetSPDGrowth = mappedStats.get(3);
				targetDEFGrowth = mappedStats.get(4);
				targetRESGrowth = mappedStats.get(5);
				targetLCKGrowth = mappedStats.get(6);
			}
			
			int newHP = 0;
			int newSTR = 0;
			int newSKL = 0;
			int newSPD = 0;
			int newLCK = 0;
			int newDEF = 0;
			int newRES = 0;
			
			if (options.baseMode == StatAdjustmentMode.AUTOLEVEL) {
				
				int hpGrowth = fill.getHPGrowth();
				int strGrowth = fill.getSTRGrowth();
				int sklGrowth = fill.getSKLGrowth();
				int spdGrowth = fill.getSPDGrowth();
				int defGrowth = fill.getDEFGrowth();
				int resGrowth = fill.getRESGrowth();
				int lckGrowth = fill.getLCKGrowth();
				
				if (options.autolevelMode == BaseStatAutolevelType.USE_NEW) {
					hpGrowth = targetHPGrowth;
					strGrowth = targetSTRGrowth;
					sklGrowth = targetSKLGrowth;
					spdGrowth = targetSPDGrowth;
					defGrowth = targetDEFGrowth;
					resGrowth = targetRESGrowth;
					lckGrowth = targetLCKGrowth;
				}
				
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "== Stat Adjustment from Class Bases ==");
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "HP: " + promoAdjustHP);
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "STR: " + promoAdjustSTR);
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "SKL: " + promoAdjustSKL);
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "SPD: " + promoAdjustSPD);
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "DEF: " + promoAdjustDEF);
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "RES: " + promoAdjustRES);
				
				// Adjust bases based on level difference and promotion changes.
				int hpDelta = (int)Math.floor((float)(hpGrowth / 100.0) * levelsToAdd) + promoAdjustHP;
				int strDelta = (int)Math.floor((float)(strGrowth / 100.0) * levelsToAdd) + promoAdjustSTR;
				int sklDelta = (int)Math.floor((float)(sklGrowth / 100.0) * levelsToAdd) + promoAdjustSKL;
				int spdDelta = (int)Math.floor((float)(spdGrowth / 100.0) * levelsToAdd) + promoAdjustSPD;
				int lckDelta = (int)Math.floor((float)(lckGrowth / 100.0) * levelsToAdd);
				int defDelta = (int)Math.floor((float)(defGrowth / 100.0) * levelsToAdd) + promoAdjustDEF;
				int resDelta = (int)Math.floor((float)(resGrowth / 100.0) * levelsToAdd) + promoAdjustRES;
				
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "== Base Deltas ==");
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "HP: " + Integer.toString(hpDelta));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "STR: " + Integer.toString(strDelta));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "SKL: " + Integer.toString(sklDelta));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "SPD: " + Integer.toString(spdDelta));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "DEF: " + Integer.toString(defDelta));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "RES: " + Integer.toString(resDelta));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "LCK: " + Integer.toString(lckDelta));
				
				// Clamp the delta to make sure we're not overflowing caps or underflowing to negative.
				// Clamp the minimum so that people aren't force to 0 base stats, but they can go down as far as 50% of their normal bases.
				newHP = Math.min(targetClass.getMaxHP() - targetClass.getBaseHP(), Math.max(fill.getBaseHP() + hpDelta, -2 * targetClass.getBaseHP() / 4));
				newSTR = Math.min(targetClass.getMaxSTR() - targetClass.getBaseSTR(), Math.max(fill.getBaseSTR() + strDelta, -2 * targetClass.getBaseSTR() / 4));
				newSKL = Math.min(targetClass.getMaxSKL() - targetClass.getBaseSKL(), Math.max(fill.getBaseSKL() + sklDelta, -2 * targetClass.getBaseSKL() / 4));
				newSPD = Math.min(targetClass.getMaxSPD() - targetClass.getBaseSPD(), Math.max(fill.getBaseSPD() + spdDelta, -2 * targetClass.getBaseSPD() / 4));
				newLCK = Math.min(targetClass.getMaxLCK() - targetClass.getBaseLCK(), Math.max(fill.getBaseLCK() + lckDelta, -2 * targetClass.getBaseLCK() / 4));
				newDEF = Math.min(targetClass.getMaxDEF() - targetClass.getBaseDEF(), Math.max(fill.getBaseDEF() + defDelta, -2 * targetClass.getBaseDEF() / 4));
				newRES = Math.min(targetClass.getMaxRES() - targetClass.getBaseRES(), Math.max(fill.getBaseRES() + resDelta, -2 * targetClass.getBaseRES() / 4));
				
				// Add their original bases back into the new value.
				
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "== New Bases ==");
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "HP: " + Integer.toString(fillSourceClass.getBaseHP()) + " + " + Integer.toString(fill.getBaseHP()) + " -> " + Integer.toString(targetClass.getBaseHP()) + " + " + Integer.toString(newHP));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "STR: " + Integer.toString(fillSourceClass.getBaseSTR()) + " + " + Integer.toString(fill.getBaseSTR()) + " -> " + Integer.toString(targetClass.getBaseSTR()) + " + " + Integer.toString(newSTR));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "SKL: " + Integer.toString(fillSourceClass.getBaseSKL()) + " + " + Integer.toString(fill.getBaseSKL()) + " -> " + Integer.toString(targetClass.getBaseSKL()) + " + " + Integer.toString(newSKL));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "SPD: " + Integer.toString(fillSourceClass.getBaseSPD()) + " + " + Integer.toString(fill.getBaseSPD()) + " -> " + Integer.toString(targetClass.getBaseSPD()) + " + " + Integer.toString(newSPD));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "DEF: " + Integer.toString(fillSourceClass.getBaseDEF()) + " + " + Integer.toString(fill.getBaseDEF()) + " -> " + Integer.toString(targetClass.getBaseDEF()) + " + " + Integer.toString(newDEF));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "RES: " + Integer.toString(fillSourceClass.getBaseRES()) + " + " + Integer.toString(fill.getBaseRES()) + " -> " + Integer.toString(targetClass.getBaseRES()) + " + " + Integer.toString(newRES));
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "LCK: " + Integer.toString(fillSourceClass.getBaseLCK()) + " + " + Integer.toString(fill.getBaseLCK()) + " -> " + Integer.toString(targetClass.getBaseLCK()) + " + " + Integer.toString(newLCK));
			} else if (options.baseMode == StatAdjustmentMode.MATCH_SLOT) {
				newHP = slotReference.getBaseHP() + slotSourceClass.getBaseHP() - targetClass.getBaseHP();
				newSTR = slotReference.getBaseSTR() + slotSourceClass.getBaseSTR() - targetClass.getBaseSTR();
				newSKL = slotReference.getBaseSKL() + slotSourceClass.getBaseSKL() - targetClass.getBaseSKL();
				newSPD = slotReference.getBaseSPD() + slotSourceClass.getBaseSPD() - targetClass.getBaseSPD();
				newLCK = slotReference.getBaseLCK() + slotSourceClass.getBaseLCK() - targetClass.getBaseLCK();
				newDEF = slotReference.getBaseDEF() + slotSourceClass.getBaseDEF() - targetClass.getBaseDEF();
				newRES = slotReference.getBaseRES() + slotSourceClass.getBaseRES() - targetClass.getBaseRES();
			} else if (options.baseMode == StatAdjustmentMode.RELATIVE_TO_SLOT) {
				newHP = slotReference.getBaseHP() + slotSourceClass.getBaseHP() - targetClass.getBaseHP(); // Keep HP the same logic as above.
				
				int slotSTR = slotReference.getBaseSTR() + slotSourceClass.getBaseSTR();
				int slotSKL = slotReference.getBaseSKL() + slotSourceClass.getBaseSKL();
				int slotSPD = slotReference.getBaseSPD() + slotSourceClass.getBaseSPD();
				int slotLCK = slotReference.getBaseLCK() + slotSourceClass.getBaseLCK();
				int slotDEF = slotReference.getBaseDEF() + slotSourceClass.getBaseDEF();
				int slotRES = slotReference.getBaseRES() + slotSourceClass.getBaseRES();
				
				int fillSTR = fill.getBaseSTR() + fillSourceClass.getBaseSTR();
				int fillSKL = fill.getBaseSKL() + fillSourceClass.getBaseSKL();
				int fillSPD = fill.getBaseSPD() + fillSourceClass.getBaseSPD();
				int fillLCK = fill.getBaseLCK() + fillSourceClass.getBaseLCK();
				int fillDEF = fill.getBaseDEF() + fillSourceClass.getBaseDEF();
				int fillRES = fill.getBaseRES() + fillSourceClass.getBaseRES();
				
				List<Integer> mappedStats = RelativeValueMapper.mappedValues(Arrays.asList(slotSTR, slotSKL, slotSPD, slotDEF, slotRES, slotLCK), 
						Arrays.asList(fillSTR, fillSKL, fillSPD, fillDEF, fillRES, fillLCK));
				
				newSTR = Math.max(mappedStats.get(0) - targetClass.getBaseSTR(), -1 * targetClass.getBaseSTR());
				newSKL = Math.max(mappedStats.get(1) - targetClass.getBaseSKL(), -1 * targetClass.getBaseSKL());
				newSPD = Math.max(mappedStats.get(2) - targetClass.getBaseSPD(), -1 * targetClass.getBaseSPD());
				newLCK = Math.max(mappedStats.get(5) - targetClass.getBaseLCK(), -1 * targetClass.getBaseLCK());
				newDEF = Math.max(mappedStats.get(3) - targetClass.getBaseDEF(), -1 * targetClass.getBaseDEF());
				newRES = Math.max(mappedStats.get(4) - targetClass.getBaseRES(), -1 * targetClass.getBaseRES());
			} else {
				assert false : "Invalid stat adjustment mode for random recruitment.";
			}
			
			linkedSlot.setBaseHP(newHP);
			linkedSlot.setBaseSTR(newSTR);
			linkedSlot.setBaseSKL(newSKL);
			linkedSlot.setBaseSPD(newSPD);
			linkedSlot.setBaseLCK(newLCK);
			linkedSlot.setBaseDEF(newDEF);
			linkedSlot.setBaseRES(newRES);
			
			// Transfer growths.
			linkedSlot.setHPGrowth(targetHPGrowth);
			linkedSlot.setSTRGrowth(targetSTRGrowth);
			linkedSlot.setSKLGrowth(targetSKLGrowth);
			linkedSlot.setSPDGrowth(targetSPDGrowth);
			linkedSlot.setDEFGrowth(targetDEFGrowth);
			linkedSlot.setRESGrowth(targetRESGrowth);
			linkedSlot.setLCKGrowth(targetLCKGrowth);
			
			linkedSlot.setConstitution(fill.getConstitution());
			linkedSlot.setAffinityValue(fill.getAffinityValue());
		}
	}
	
	private static void transferWeaponRanks(GBAFECharacterData target, GBAFEClassData targetClass, ItemDataLoader itemData, Random rng) {
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
		
		int targetWeaponUsage = 0;
		if (targetClass.getSwordRank() > 0) { targetWeaponUsage++; }
		if (targetClass.getLanceRank() > 0) { targetWeaponUsage++; }
		if (targetClass.getAxeRank() > 0) { targetWeaponUsage++; }
		if (targetClass.getBowRank() > 0) { targetWeaponUsage++; }
		if (targetClass.getLightRank() > 0) { targetWeaponUsage++; }
		if (targetClass.getDarkRank() > 0) { targetWeaponUsage++; }
		if (targetClass.getAnimaRank() > 0) { targetWeaponUsage++; }
		if (targetClass.getStaffRank() > 0) { targetWeaponUsage++; }
		
		Collections.sort(rankValues);
		while (rankValues.size() > targetWeaponUsage) {
			rankValues.remove(0); // Remove the lowest rank if we're filling less weapons than we have to work with.
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
			if (itemData.weaponRankFromValue(randomRankValue) == WeaponRank.E) {
				// Dark magic floors on D. There's no E rank dark magic.
				randomRankValue = itemData.weaponRankValueForRank(WeaponRank.D);
			}
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
	
	private static void setSlotClass(ItemAssignmentOptions inventoryOptions, GBAFECharacterData slot, GBAFEClassData targetClass, CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData, ChapterLoader chapterData, Random rng) {
		
		slot.setClassID(targetClass.getID());
		transferWeaponRanks(slot, targetClass, itemData, rng);
		
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			GBAFEChapterItemData reward = chapter.chapterItemGivenToCharacter(slot.getID());
			if (reward != null) {
				GBAFEItemData item = itemData.getRandomWeaponForCharacter(slot, false, false, rng);
				reward.setItemID(item.getID());
			}
			
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.getCharacterNumber() == slot.getID()) {
					unit.setStartingClass(targetClass.getID());
					
					// Set Inventory.
					ClassRandomizer.validateCharacterInventory(inventoryOptions, slot, targetClass, unit, characterData.characterIDRequiresRange(slot.getID()), characterData.characterIDRequiresMelee(slot.getID()), classData, itemData, textData, false, rng);
					if (characterData.isThiefCharacterID(slot.getID())) {
						ClassRandomizer.validateFormerThiefInventory(unit, itemData);
					}
					ClassRandomizer.validateSpecialClassInventory(unit, itemData, rng);
				}
			}
		}
	}
}
