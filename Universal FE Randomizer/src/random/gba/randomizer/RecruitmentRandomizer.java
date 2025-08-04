package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import fedata.gba.GBAFEStatDto;
import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapPortraitData;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import fedata.general.FEBase.GameType;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import random.gba.randomizer.service.ClassAdjustmentDto;
import random.gba.randomizer.service.GBASlotAdjustmentService;
import random.gba.randomizer.service.GBATextReplacementService;
import random.gba.randomizer.service.ItemAssignmentService;
import random.general.RelativeValueMapper;
import ui.model.ItemAssignmentOptions;
import ui.model.RecruitmentOptions;
import ui.model.RecruitmentOptions.BaseStatAutolevelType;
import ui.model.RecruitmentOptions.ClassMode;
import ui.model.RecruitmentOptions.GrowthAdjustmentMode;
import ui.model.RecruitmentOptions.StatAdjustmentMode;
import util.DebugPrinter;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;

public class RecruitmentRandomizer {
	
	static final int rngSalt = 911;
	
	public static Map<GBAFECharacterData, GBAFECharacterData> randomizeRecruitment(RecruitmentOptions options, ItemAssignmentOptions inventoryOptions, GameType type, 
			CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, ChapterLoader chapterData, TextLoader textData, FreeSpaceManager freeSpace,
			Random rng) {
		
		// Figure out mapping first.
		List<GBAFECharacterData> characterPool = new ArrayList<GBAFECharacterData>(characterData.canonicalPlayableCharacters(options.includeExtras));
		characterPool.removeIf(character -> (characterData.charactersExcludedFromRandomRecruitment().contains(character)));
		
		if (!options.includeLords) {
			characterPool.removeIf(character -> (characterData.isLordCharacterID(character.getID())));
		}
		if (!options.includeThieves) {
			characterPool.removeIf(character -> (characterData.isThiefCharacterID(character.getID())));
		}
		if (!options.includeSpecial) {
			characterPool.removeIf(character -> (characterData.isSpecialCharacterID(character.getID())));
		}
		
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
		
		// The restrictions here only need to be implemented if we use the fill class.
		// If we're using the slot class, then the class restrictions are no longer needed.
		if (options.classMode == ClassMode.USE_FILL) {
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
		}
		
		// Assign everybody else randomly.
		// We do have to make sure characters that can get assigned can promote/demote if necessary.
		DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Assigning the remainder of the characters...");
		List<SlotAssignment> assignedSlots = shuffleCharactersInPool(true, separateByGender, slotsRemaining, characterPool, characterMap, referenceData, characterData, classData, textData, rng);
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
		List<GBAFECharacterData> orderedKeys = new ArrayList<GBAFECharacterData>(characterMap.keySet());
		orderedKeys.sort(new Comparator<GBAFECharacterData>() {
			@Override
			public int compare(GBAFECharacterData o1, GBAFECharacterData o2) {
				return o1.getID() < o2.getID() ? -1 : (o1.getID() > o2.getID() ? 1 : 0);
			}
		});
		for (GBAFECharacterData slot : orderedKeys) {
			GBAFECharacterData fill = characterMap.get(slot);
			if (fill != null) {
				// Track the text changes before we change anything.
				GBATextReplacementService.enqueueUpdate(textData, characterData, slot, fill);
				// Apply the change to the data.
				fillSlot(options, inventoryOptions, slot, fill, characterData, classData, itemData, chapterData, textData, type, rng);
			}
		}
				
		// Run through the text and modify portraits and names in text.
		GBATextReplacementService.applyChanges(textData);
		
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
			// Do not modify if they happen to have a different class.
			if (linkedSlot.getClassID() != slotReference.getClassID()) { continue; }
			
			// First, replace the description, and face
			// The name is unnecessary because there's a text find/replace that we apply later.
			linkedSlot.setDescriptionIndex(fill.getDescriptionIndex());
			linkedSlot.setFaceID(fill.getFaceID());
			
			linkedSlot.setIsLord(characterData.isLordCharacterID(slotReference.getID()));
			
			int targetLevel = linkedSlot.getLevel();
			int sourceLevel = fill.getLevel();
			
			DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, "Slot level: " + Integer.toString(targetLevel) + "\tFill Level: " + Integer.toString(sourceLevel));
			
			List<GBAFEStatDto> promoBonuses = new ArrayList<>();

			
			
			ClassAdjustmentDto adjustmentDAO = GBASlotAdjustmentService.handleClassAdjustment(targetLevel, sourceLevel, shouldBePromoted, 
					isPromoted, rng, classData, targetClass, fillSourceClass, fill, slotSourceClass, 
					options, textData, DebugPrinter.Key.GBA_RANDOM_RECRUITMENT);
			targetClass = adjustmentDAO.targetClass;
			int levelsToAdd = adjustmentDAO.levelAdjustment;
			promoBonuses =  adjustmentDAO.promoBonuses;
			
			setSlotClass(inventoryOptions, linkedSlot, targetClass, characterData, classData, itemData, textData, chapterData, type, rng);
			
			GBAFEStatDto targetGrowths;
			switch(options.growthMode) {
				case USE_SLOT:
					targetGrowths = fill.getGrowths();
					break;
				case RELATIVE_TO_SLOT:
					List<Integer> mappedStats = RelativeValueMapper.mappedValues(Arrays.asList(slot.getHPGrowth(), slot.getSTRGrowth(), slot.getSKLGrowth(), slot.getSPDGrowth(), slot.getDEFGrowth(), slot.getRESGrowth(), slot.getLCKGrowth()), 
							Arrays.asList(fill.getHPGrowth(), fill.getSTRGrowth(), fill.getSKLGrowth(), fill.getSPDGrowth(), fill.getDEFGrowth(), fill.getRESGrowth(), fill.getLCKGrowth()));
					targetGrowths = new GBAFEStatDto(mappedStats.get(0), mappedStats.get(1), mappedStats.get(2), mappedStats.get(3), mappedStats.get(4), mappedStats.get(5), mappedStats.get(6));
					break;
				case USE_FILL:
				default:
					targetGrowths = fill.getGrowths();
			
			}
			
			GBAFEStatDto newStats = new GBAFEStatDto();
			
			if (options.baseMode == StatAdjustmentMode.AUTOLEVEL) {
				GBAFEStatDto growthsToUse = options.autolevelMode == BaseStatAutolevelType.USE_NEW ? targetGrowths : fill.getGrowths();
				
				// Calculate the auto leveled personal bases
				newStats = GBASlotAdjustmentService.autolevel(fill.getBases(), growthsToUse, 
						promoBonuses, levelsToAdd, targetClass, DebugPrinter.Key.GBA_RANDOM_RECRUITMENT); 
				
				DebugPrinter.log(DebugPrinter.Key.GBA_RANDOM_RECRUITMENT, String.format("== New Bases ==%n%s", newStats.toString()));
			} else if (options.baseMode == StatAdjustmentMode.MATCH_SLOT) {
				newStats.add(linkedSlot.getBases()) // Add the original Bases of the slot
					    .add(targetClass.getBases()) // Add the stats from the new class
					    .subtract(slotSourceClass.getBases()); // remove the stats from the original class
			} else if (options.baseMode == StatAdjustmentMode.RELATIVE_TO_SLOT) {
				newStats = new GBAFEStatDto();
				newStats.hp = linkedSlot.getBaseHP() + slotSourceClass.getBaseHP() - targetClass.getBaseHP(); // Keep HP the same logic as above.
				GBAFEStatDto slotStats = linkedSlot.getBases().add(slotSourceClass.getBases());
				GBAFEStatDto fillStats = fill.getBases().add(fillSourceClass.getBases());

				// Set HP to an absurdly high value so that the HP values will be mapped to one another and we can ignore them easily
				slotStats.hp = Integer.MAX_VALUE; 
				fillStats.hp = Integer.MAX_VALUE; 
				
				
				List<Integer> mappedStats = RelativeValueMapper.mappedValues(slotStats.asList(), fillStats.asList()); 
				
				// ignore the index 0 in the list, as that is HP, and will be handled separately
				newStats.str = Math.max(mappedStats.get(1) - targetClass.getBaseSTR(), -1 * targetClass.getBaseSTR());
				newStats.skl = Math.max(mappedStats.get(2) - targetClass.getBaseSKL(), -1 * targetClass.getBaseSKL());
				newStats.spd = Math.max(mappedStats.get(3) - targetClass.getBaseSPD(), -1 * targetClass.getBaseSPD());
				newStats.def = Math.max(mappedStats.get(4) - targetClass.getBaseDEF(), -1 * targetClass.getBaseDEF());
				newStats.res = Math.max(mappedStats.get(5) - targetClass.getBaseRES(), -1 * targetClass.getBaseRES());
				newStats.lck = Math.max(mappedStats.get(6) - targetClass.getBaseLCK(), -1 * targetClass.getBaseLCK());
			} else {
				assert false : "Invalid stat adjustment mode for random recruitment.";
			}
			linkedSlot.setBases(newStats);
			
			// Transfer growths.
			linkedSlot.setGrowths(targetGrowths);
			
			linkedSlot.setConstitution(fill.getConstitution());
			linkedSlot.setAffinityValue(fill.getAffinityValue());
		}
	}
	
	private static void setSlotClass(ItemAssignmentOptions inventoryOptions, GBAFECharacterData slot, GBAFEClassData targetClass, CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData, ChapterLoader chapterData, GameType type, Random rng) {
		int oldClassID = slot.getClassID();
		GBAFEClassData originalClass = classData.classForID(oldClassID);
		slot.setClassID(targetClass.getID());
		GBASlotAdjustmentService.transferWeaponRanks(slot, originalClass, targetClass, type, rng);
		ItemAssignmentService.assignNewItems(characterData, slot, targetClass, chapterData, inventoryOptions, type, rng, textData, classData, itemData);
	}
}
