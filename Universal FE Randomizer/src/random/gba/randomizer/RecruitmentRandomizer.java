package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

public class RecruitmentRandomizer {
	
	static final int rngSalt = 911;
	
	public static void randomizeRecruitment(GameType type, 
			CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, ChapterLoader chapterData, PaletteLoader paletteData, TextLoader textData, 
			Random rng) {
		
		// Figure out mapping first.
		List<GBAFECharacterData> characterPool = new ArrayList<GBAFECharacterData>(Arrays.asList(characterData.playableCharacters()));
		
		Map<GBAFECharacterData, GBAFECharacterData> characterMap = new HashMap<GBAFECharacterData, GBAFECharacterData>();
		List<GBAFECharacterData> slotsRemaining = new ArrayList<GBAFECharacterData>(characterPool);
		
		// Assign fliers first, since they are restricted in where they can end up.
		// The slots are determined by the character, since we know which characters must be flying normally.
		// The pool is determined by the character's new class (if it was randomized). This pool should always be larger than the number of slots
		// since all fliers are required to randomize into flier classes. There might be other characters that randomized into fliers though.
		// All fliers can promote and demote, so we should be ok here for promotions.
		List<GBAFECharacterData> flierSlotsRemaining = characterPool.stream().filter(character -> (characterData.isFlyingCharacter(character.getID()))).collect(Collectors.toList());
		List<GBAFECharacterData> flierPool = characterPool.stream().filter(character -> (classData.isFlying(character.getClassID()))).collect(Collectors.toList());
		while (!flierSlotsRemaining.isEmpty() && !flierPool.isEmpty()) {
			int slotIndex = rng.nextInt(flierSlotsRemaining.size());
			GBAFECharacterData flierSlot = flierSlotsRemaining.get(slotIndex);
			int fillIndex = rng.nextInt(flierPool.size());
			GBAFECharacterData flier = flierPool.get(fillIndex);
			
			characterMap.put(flierSlot, flier);
			
			flierPool.remove(fillIndex);
			flierSlotsRemaining.remove(slotIndex);
			
			slotsRemaining.removeIf(character -> (character.getID() == flierSlot.getID()));
			characterPool.removeIf(character -> (character.getID() == flier.getID()));
		}
		
		// Prioritize those with melee/ranged requirements too.
		List<GBAFECharacterData> meleeRequiredSlotsRemaining = characterPool.stream().filter(character -> (characterData.characterIDRequiresMelee(character.getID()))).collect(Collectors.toList());
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
			
			characterMap.put(slot, fill);
			
			meleePool.remove(fillIndex);
			meleeRequiredSlotsRemaining.remove(slotIndex);
			
			slotsRemaining.removeIf(character -> (character.getID() == slot.getID()));
			characterPool.removeIf(character -> (character.getID() == fill.getID()));
		}
		
		List<GBAFECharacterData> rangeRequiredSlotsRemaining = characterPool.stream().filter(character -> (characterData.characterIDRequiresRange(character.getID()))).collect(Collectors.toList());
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
			
			characterMap.put(slot, fill);
			
			rangePool.remove(fillIndex);
			rangeRequiredSlotsRemaining.remove(slotIndex);
			
			slotsRemaining.removeIf(character -> (character.getID() == slot.getID()));
			characterPool.removeIf(character -> (character.getID() == fill.getID()));
		}
		
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
			
			characterMap.put(slot, fill);
			
			slotsRemaining.remove(slotIndex);
			characterPool.removeIf(character -> (character.getID() == fill.getID()));
		}
		
		// Process every mapped character.
		for (GBAFECharacterData slot : characterMap.keySet()) {
			GBAFECharacterData fill = characterMap.get(slot);
			if (fill != null) {
				fillSlot(slot, fill, characterData, classData, itemData, chapterData, paletteData, textData, rng);
			}
		}
	}

	private static void fillSlot(GBAFECharacterData slot, GBAFECharacterData fill, CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, ChapterLoader chapterData, PaletteLoader paletteData, TextLoader textData, Random rng) {
		// Override the fill's address with the slot's original address.
		fill.setAddressOverride(slot.getOriginalAddress());
		
		// Overwrite the ID as well. For all intents and purposes, this makes the fill character the slot character now, but retains all of its other characteristics.
		fill.setID(slot.getOriginalID());
		
		// Write the description text index to re-attach descriptions.
		fill.setDescriptionIndex(slot.getOriginalDescriptionIndex());
		
		// Handle Promotion/Demotion as necessary
		boolean slotIsPromoted = classData.isPromotedClass(slot.getClassID());
		boolean fillIsPromoted = classData.isPromotedClass(fill.getClassID());
		
		int slotLevel = chapterData.getStartingLevelForCharacter(slot.getOriginalID());
		int fillLevel = chapterData.getStartingLevelForCharacter(fill.getOriginalID());
		
		if (slotIsPromoted) { slotLevel += 10; }
		if (fillIsPromoted) { slotLevel += 10; }
		
		int levelsToAdd = slotLevel - fillLevel;
		
		if (slotIsPromoted && !fillIsPromoted) {
			// Promote Fill.
			List<GBAFEClassData> promotionOptions = classData.promotionOptions(fill.getClassID());
			if (!promotionOptions.isEmpty()) {
				GBAFEClassData targetClass = promotionOptions.get(rng.nextInt(promotionOptions.size()));
				setFillClass(fill, targetClass, characterData, classData, itemData, textData, chapterData, rng);
			}
			levelsToAdd += 10;
		} else if (!slotIsPromoted && fillIsPromoted) {
			// Demote Fill.
			List<GBAFEClassData> demotionOptions = classData.demotionOptions(fill.getClassID());
			if (!demotionOptions.isEmpty()) {
				GBAFEClassData targetClass = demotionOptions.get(rng.nextInt(demotionOptions.size()));
				setFillClass(fill, targetClass, characterData, classData, itemData, textData, chapterData, rng);
			}
			levelsToAdd -= 10;
		}
		
		GBAFEClassData charClass = classData.classForID(fill.getClassID());
		
		// Adjust bases if necessary.
		int hpDelta = (int)Math.floor((float)(fill.getHPGrowth() / 100) * levelsToAdd);
		int strDelta = (int)Math.floor((float)(fill.getSTRGrowth() / 100) * levelsToAdd);
		int sklDelta = (int)Math.floor((float)(fill.getSKLGrowth() / 100) * levelsToAdd);
		int spdDelta = (int)Math.floor((float)(fill.getSPDGrowth() / 100) * levelsToAdd);
		int lckDelta = (int)Math.floor((float)(fill.getLCKGrowth() / 100) * levelsToAdd);
		int defDelta = (int)Math.floor((float)(fill.getDEFGrowth() / 100) * levelsToAdd);
		int resDelta = (int)Math.floor((float)(fill.getRESGrowth() / 100) * levelsToAdd);
		
		// Clamp the delta to make sure we're not overflowing caps or underflowing to negative.
		int newHP = Math.min(charClass.getMaxHP() - charClass.getBaseHP(), Math.max(fill.getBaseHP() + hpDelta, -1 * charClass.getBaseHP()));
		int newSTR = Math.min(charClass.getMaxSTR() - charClass.getBaseSTR(), Math.max(fill.getBaseSTR() + strDelta, -1 * charClass.getBaseSTR()));
		int newSKL = Math.min(charClass.getMaxSKL() - charClass.getBaseSKL(), Math.max(fill.getBaseSKL() + sklDelta, -1 * charClass.getBaseSKL()));
		int newSPD = Math.min(charClass.getMaxSPD() - charClass.getBaseSPD(), Math.max(fill.getBaseSPD() + spdDelta, -1 * charClass.getBaseSPD()));
		int newLCK = Math.min(charClass.getMaxLCK() - charClass.getBaseLCK(), Math.max(fill.getBaseLCK() + lckDelta, -1 * charClass.getBaseLCK()));
		int newDEF = Math.min(charClass.getMaxDEF() - charClass.getBaseDEF(), Math.max(fill.getBaseDEF() + defDelta, -1 * charClass.getBaseDEF()));
		int newRES = Math.min(charClass.getMaxRES() - charClass.getBaseRES(), Math.max(fill.getBaseRES() + resDelta, -1 * charClass.getBaseRES()));
		
		fill.setBaseHP(newHP);
		fill.setBaseSTR(newSTR);
		fill.setBaseSKL(newSKL);
		fill.setBaseSPD(newSPD);
		fill.setBaseLCK(newLCK);
		fill.setBaseDEF(newDEF);
		fill.setBaseRES(newRES);
	}
	
	private static void setFillClass(GBAFECharacterData fill, GBAFEClassData targetClass, CharacterDataLoader characterData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData, ChapterLoader chapterData, Random rng) {
		fill.setClassID(targetClass.getID());
		
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.getCharacterNumber() == fill.getID()) {
					unit.setStartingClass(targetClass.getID());
					
					// Set Inventory.
					ClassRandomizer.validateCharacterInventory(fill, targetClass, unit, characterData.characterIDRequiresRange(fill.getID()), characterData.characterIDRequiresMelee(fill.getID()), classData, itemData, textData, false, rng);
				}
			}
		}
	}
}
