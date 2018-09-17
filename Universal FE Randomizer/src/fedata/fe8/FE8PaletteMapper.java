package fedata.fe8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedata.fe8.FE8Data.Palette;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;

import io.FileHandler;

// FE8 uses two auxiliary tables to map palettes based on class.
// The first tells us which classes a character can be, and the second
// says which palette to use based on those classes.
// Presumably, if the class does not match, the game will default
// to the default generic palette, so we need to make sure we map these properly.

// For the most part, we don't need to mess with the palette assignment, as we'll
// be updating those colors anyway, but we do need to make sure the classes line up.
// TODO: Integrate this table. Some units are going to cheat a little by altering the underlying
// sprite itself, but characters like Tethys who go from no promotions to some promotions won't have
// enough palettes to support it, not to mention Eirika and Ephraim have no custom palettes available to them othrwise.
public class FE8PaletteMapper {
	
	public enum ClassMapType {
		UNKNOWN, TRAINEE, UNPROMOTED, PROMOTED;
	}
	
	public enum SlotType {
		TRAINEE, PRIMARY_BASE, SECONDARY_BASE, FIRST_PROMOTION, SECOND_PROMOTION, THIRD_PROMOTION, FOURTH_PROMOTION;
	}
	
	private class MapEntry {
		byte[] originalData;
		byte[] data;
		
		long originalOffset;
		
		Boolean wasModified;
		
		private MapEntry(FileHandler handler, long offset) {
			originalOffset = offset;
			originalData = handler.readBytesAtOffset(offset, FE8Data.BytesPerPaletteTableEntry);
			data = originalData.clone();
			
			wasModified = false;
		}
		
		public int getByte0() {
			return data[0] & 0xFF;
		}
		public void setByte0(int value) {
			data[0] = (byte)(value & 0xFF);
			wasModified = true;
		}
		
		public int getByte1() {
			return data[1] & 0xFF;
		}
		public void setByte1(int value) {
			data[1] = (byte)(value & 0xFF);
			wasModified = true;
		}
		
		public int getByte2() {
			return data[2] & 0xFF;
		}
		public void setByte2(int value) {
			data[2] = (byte)(value & 0xFF);
			wasModified = true;
		}
		
		public int getByte3() {
			return data[3] & 0xFF;
		}
		public void setByte3(int value) {
			data[3] = (byte)(value & 0xFF);
			wasModified = true;
		}
		
		public int getByte4() {
			return data[4] & 0xFF;
		}
		public void setByte4(int value) {
			data[4] = (byte)(value & 0xFF);
			wasModified = true;
		}
		
		public int getByte5() {
			return data[5] & 0xFF;
		}
		public void setByte5(int value) {
			data[5] = (byte)(value & 0xFF);
			wasModified = true;
		}
		
		public int getByte6() {
			return data[6] & 0xFF;
		}
		public void setByte6(int value) {
			data[6] = (byte)(value & 0xFF);
			wasModified = true;
		}
		
		public void reset() {
			data = originalData.clone();
			wasModified = false;
		}
		
		public void commit(DiffCompiler compiler) {
			if (wasModified) {
				compiler.addDiff(new Diff(originalOffset, data.length, data, originalData));
			}
		}
	}
	
	private class PaletteMapEntry extends MapEntry {
		private PaletteMapEntry(FileHandler handler, long offset) { super(handler, offset); }
		
		private int getTraineePaletteID() { return getByte0(); }
		private void setTraineePaletteID(int paletteID) { setByte0(paletteID); }
		
		private int getBasePaletteID() { return getByte1(); }
		private void setBasePaletteID(int paletteID) { setByte1(paletteID); }
		
		private int getSecondaryBasePaletteID() { return getByte2(); }
		private void setSecondaryBasePaletteID(int paletteID) { setByte2(paletteID); }
		
		private int getFirstPromotionPaletteID() { return getByte3(); }
		private void setFirstPromotionPaletteID(int paletteID) { setByte3(paletteID); }
		
		private int getSecondaryPromotionPaletteID() { return getByte4(); }
		private void setSecondaryPromotionPaletteID(int paletteID) { setByte4(paletteID); }
		
		private int getThirdPromotionPaletteID() { return getByte5(); }
		private void setThirdPromotionPaletteID(int paletteID) { setByte5(paletteID); }
		
		private int getFourthPromotionPaletteID() { return getByte6(); }
		private void setFourthPromotionPaletteID(int paletteID) { setByte6(paletteID); }
	}
	
	private class ClassMapEntry extends MapEntry {
		
		private ClassMapType getMapType() {
			if (getTraineeClassID() != 0 && getBaseClassID() != 0 && getSecondaryBaseClassID() != 0 && getFirstPromotionClassID() != 0 &&
					getSecondaryPromotionClassID() != 0 && getThirdPromotionClassID() != 0 && getFourthPromotionClassID() != 0) {
				return ClassMapType.TRAINEE;
			} else if (getTraineeClassID() == 0 && getBaseClassID() == 0 && getSecondaryBaseClassID() == 0 &&
					getFirstPromotionClassID() != 0 && getSecondaryPromotionClassID() == 0 && getThirdPromotionClassID() == 0 && getFourthPromotionClassID() == 0) {
				return ClassMapType.PROMOTED;
			} else {
				return ClassMapType.UNPROMOTED;
			}
		}
		
		private ClassMapEntry(FileHandler handler, long offset) { super(handler, offset); }
		
		private int getTraineeClassID() { return getByte0(); }
		private void setTraineeClassID(int newClassID) { setByte0(newClassID); }
		
		private int getBaseClassID() { return getByte1(); }
		private void setBaseClassID(int newClassID) { setByte1(newClassID); }
		
		private int getSecondaryBaseClassID() { return getByte2(); }
		private void setSecondaryBaseClassID(int newClassID) { setByte2(newClassID); }
		
		private int getFirstPromotionClassID() { return getByte3(); }
		private void setFirstPromotionClassID(int newClassID) { setByte3(newClassID); }
		
		private int getSecondaryPromotionClassID() { return getByte4(); }
		private void setSecondaryPromotionClassID(int newClassID) { setByte4(newClassID); }
		
		private int getThirdPromotionClassID() { return getByte5(); }
		private void setThirdPromotionClassID(int newClassID) { setByte5(newClassID); }
		
		private int getFourthPromotionClassID() { return getByte6(); }
		private void setFourthPromotionClassID(int newClassID) { setByte6(newClassID); }
	}
	
	private Map<FE8Data.Character, ClassMapEntry> paletteClassMap;
	private Map<FE8Data.Character, PaletteMapEntry> paletteIndexMap;
	
	private Map<Integer, FE8Data.Palette> freedPaletteIndices; // Maps indices to palettes.
	private Map<FE8Data.Character, List<SlotType>> charactersThatNeedPalettes; // Maps to array of slots.
	
	private FE8PromotionManager promotionManager;
	
	public FE8PaletteMapper(FileHandler handler, FE8PromotionManager promotionManager) {
		paletteClassMap = new HashMap<FE8Data.Character, ClassMapEntry>();
		paletteIndexMap = new HashMap<FE8Data.Character, PaletteMapEntry>();
		
		freedPaletteIndices = new HashMap<Integer, FE8Data.Palette>();
		charactersThatNeedPalettes = new HashMap<FE8Data.Character, List<SlotType>>();
		
		long tableOffset = FileReadHelper.readAddress(handler, FE8Data.PaletteClassTablePointer);
		// As far as the indices go, it looks like the offsets follow the same order as the characters' IDs, albeit - 1, so Eirika starts at 0x0 instead of 0x1, Seth at 0x1 instead of 0x2, and so on.
		
		FE8Data.Character[] characters = FE8Data.Character.values();
		for (FE8Data.Character currentCharacter : characters) {
			int index = currentCharacter.ID - 1;
			long offset = FE8Data.BytesPerPaletteTableEntry * index + tableOffset;
			ClassMapEntry characterMapEntry = new ClassMapEntry(handler, offset);
			paletteClassMap.put(currentCharacter, characterMapEntry);
		}
		
		tableOffset = FileReadHelper.readAddress(handler, FE8Data.PaletteIndexTablePointer);
		// Same as above. The IDs are offset by 1.
		
		for (FE8Data.Character currentCharacter : characters) {
			int index = currentCharacter.ID - 1;
			long offset = FE8Data.BytesPerPaletteIndexTableEntry * index + tableOffset;
			PaletteMapEntry paletteMapEntry = new PaletteMapEntry(handler, offset);
			paletteIndexMap.put(currentCharacter, paletteMapEntry);
		}
		
		this.promotionManager = promotionManager;
	}
	
	public ClassMapType getMapTypeForCharacter(int characterID) {
		FE8Data.Character character = FE8Data.Character.valueOf(characterID);
		if (character == null) { return ClassMapType.UNKNOWN; }
		return paletteClassMap.get(character).getMapType();
	}
	
	private void markPaletteIDAsFree(int paletteID) {
		DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Freed up palette with ID 0x" + Integer.toHexString(paletteID));
		freedPaletteIndices.put(paletteID, FE8Data.Palette.paletteForID(paletteID));
	}
	
	public int[] getCharactersNeedingAdditionalPalettes() {
		int[] charIDs = new int[charactersThatNeedPalettes.size()];
		int index = 0;
		for (FE8Data.Character character : charactersThatNeedPalettes.keySet()) {
			charIDs[index++] = character.ID;
		}
		
		return charIDs;
	}
	
	public int classIDMappedToCharacterForType(int characterID, SlotType type) {
		int charID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		FE8Data.Character character = FE8Data.Character.valueOf(charID);
		ClassMapEntry map = paletteClassMap.get(character);
		switch (type) {
		case TRAINEE:
			return map.getTraineeClassID();
		case PRIMARY_BASE:
			return map.getBaseClassID();
		case SECONDARY_BASE:
			return map.getSecondaryBaseClassID();
		case FIRST_PROMOTION:
			return map.getFirstPromotionClassID();
		case SECOND_PROMOTION:
			return map.getSecondaryPromotionClassID();
		case THIRD_PROMOTION:
			return map.getThirdPromotionClassID();
		case FOURTH_PROMOTION:
			return map.getFourthPromotionClassID();
		default:
			return 0;
		}
	}
	
	public Map<SlotType, Integer> requestRecycledPaletteIndicesForCharacter(int characterID) {
		int canonicalID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalID);
		List<SlotType> palettesNeeded = charactersThatNeedPalettes.get(character);
		List<SlotType> slotsRemaining = new ArrayList<SlotType>(palettesNeeded);
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Handing out free palettes to character 0x" + Integer.toHexString(characterID));
		
		if (palettesNeeded.size() < freedPaletteIndices.size()) {
			Map<SlotType, Integer> recycledIndices = new HashMap<SlotType, Integer>();
			for (int paletteIndex : freedPaletteIndices.keySet()) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Handed out palette index 0x" + Integer.toHexString(paletteIndex));
				recycledIndices.put(slotsRemaining.remove(0), paletteIndex);
				if (slotsRemaining.isEmpty()) {
					break;
				}
			}
			return recycledIndices;
		} else {
			return new HashMap<SlotType, Integer>();
		}
	}
	
	public Palette recycledPaletteForIndex(int paletteIndex) {
		return freedPaletteIndices.get(paletteIndex);
	}
	
	public void assignRecycledPaletteIndexToCharacterAndClass(int paletteIndex, int characterID, int classID) {
		FE8Data.Character character = FE8Data.Character.valueOf(FE8Data.Character.canonicalIDForCharacterID(characterID));
		SlotType slotType = SlotType.TRAINEE;
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			ClassMapEntry map = paletteClassMap.get(linked);
			PaletteMapEntry paletteMap = paletteIndexMap.get(linked);
			Boolean assignedSuccessfully = false;
			
			if (map.getTraineeClassID() == classID) {
				paletteMap.setTraineePaletteID(paletteIndex);
				assignedSuccessfully = true;
			} else if (map.getBaseClassID() == classID) {
				paletteMap.setBasePaletteID(paletteIndex);
				slotType = SlotType.PRIMARY_BASE;
				assignedSuccessfully = true;
			} else if (map.getSecondaryBaseClassID() == classID) {
				paletteMap.setSecondaryBasePaletteID(paletteIndex);
				slotType = SlotType.SECONDARY_BASE;
				assignedSuccessfully = true;
			} else if (map.getFirstPromotionClassID() == classID) {
				paletteMap.setFirstPromotionPaletteID(paletteIndex);
				slotType = SlotType.FIRST_PROMOTION;
				assignedSuccessfully = true;
			} else if (map.getSecondaryPromotionClassID() == classID) {
				paletteMap.setSecondaryPromotionPaletteID(paletteIndex);
				slotType = SlotType.SECOND_PROMOTION;
				assignedSuccessfully = true;
			} else if (map.getThirdPromotionClassID() == classID) {
				paletteMap.setThirdPromotionPaletteID(paletteIndex);
				slotType = SlotType.THIRD_PROMOTION;
				assignedSuccessfully = true;
			} else if (map.getFourthPromotionClassID() == classID) {
				paletteMap.setFourthPromotionPaletteID(paletteIndex);
				slotType = SlotType.FOURTH_PROMOTION;
				assignedSuccessfully = true;
			}
			
			if (!assignedSuccessfully) {
				System.err.println("Invalid character class combination detected.");
				break;
			}
		}
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Reused palette with ID 0x" + Integer.toHexString(paletteIndex) + " for character 0x" + Integer.toHexString(characterID) + " in class 0x" + Integer.toHexString(classID));
		
		freedPaletteIndices.remove(paletteIndex);
		List<SlotType> slotsRemaining = charactersThatNeedPalettes.get(character);
		if (slotsRemaining.contains(slotType)) {
			slotsRemaining.remove(slotType);
			if (slotsRemaining.isEmpty()) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Finished backfilling character 0x" + Integer.toHexString(characterID));
				charactersThatNeedPalettes.remove(character);
			}
		} else {
			System.err.println("Assigned a palette to an invalid slot.");
		}
	}

	public void setUnpromotedClass(int unpromotedClassID, int characterID, Boolean setPromotions) {
		FE8Data.CharacterClass unpromotedClass = FE8Data.CharacterClass.valueOf(unpromotedClassID);
		if (unpromotedClass == null) { return; }
		if (!FE8Data.CharacterClass.allUnpromotedClasses.contains(unpromotedClass)) { return; }
		
		int primaryPromotionID = promotionManager.getFirstPromotionOptionClassID(unpromotedClass.ID);
		int secondaryPromotionID = promotionManager.getSecondPromotionOptionClassID(unpromotedClass.ID);
		int canonicalCharID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalCharID);
		PaletteMapEntry existingPaletteMap = paletteIndexMap.get(character);
		List<SlotType> palettesNeeded = new ArrayList<SlotType>();
		if (existingPaletteMap.getBasePaletteID() == 0) { palettesNeeded.add(SlotType.PRIMARY_BASE); }
		if (setPromotions) {
			if (primaryPromotionID != 0 && existingPaletteMap.getFirstPromotionPaletteID() == 0) { palettesNeeded.add(SlotType.FIRST_PROMOTION); }
			if (secondaryPromotionID != 0 && existingPaletteMap.getSecondaryPromotionPaletteID() == 0) { palettesNeeded.add(SlotType.SECOND_PROMOTION); }
		}
		if (!palettesNeeded.isEmpty()) {
			charactersThatNeedPalettes.put(character, palettesNeeded);
		}
		
		Set<Integer> paletteIDsInUse = new HashSet<Integer>();
		Set<Integer> unusedPaletteIDs = new HashSet<Integer>();
		
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			ClassMapEntry map = paletteClassMap.get(linked);
			PaletteMapEntry paletteMap = paletteIndexMap.get(linked);
			
			map.setTraineeClassID(0);
			if (paletteMap.getTraineePaletteID() != 0) {
				unusedPaletteIDs.add(paletteMap.getTraineePaletteID());
				paletteMap.setTraineePaletteID(0);
			}
			map.setBaseClassID(unpromotedClassID);
			paletteIDsInUse.add(paletteMap.getBasePaletteID());
			map.setSecondaryBaseClassID(0);
			if (paletteMap.getSecondaryBasePaletteID() != 0) {
				unusedPaletteIDs.add(paletteMap.getSecondaryBasePaletteID());
				paletteMap.setSecondaryBasePaletteID(0);
			}
			
			if (setPromotions) {
				if (primaryPromotionID != 0) {
					FE8Data.CharacterClass primaryPromotionClass = FE8Data.CharacterClass.valueOf(primaryPromotionID);
					if (primaryPromotionClass != null) {
						map.setFirstPromotionClassID(primaryPromotionID);
						paletteIDsInUse.add(paletteMap.getFirstPromotionPaletteID());
					} else {
						System.err.println("Invalid class detected in promotion branch (Base Class: " + unpromotedClass.toString() + ").");
					}
				} else {
					map.setFirstPromotionClassID(0);
					if (paletteMap.getFirstPromotionPaletteID() != 0) {
						unusedPaletteIDs.add(paletteMap.getFirstPromotionPaletteID());
						paletteMap.setFirstPromotionPaletteID(0);
					}
				}
				
				if (secondaryPromotionID != 0) {
					FE8Data.CharacterClass secondaryPromotionClass = FE8Data.CharacterClass.valueOf(secondaryPromotionID);
					if (secondaryPromotionClass != null) {
						map.setSecondaryPromotionClassID(secondaryPromotionID);
						paletteIDsInUse.add(paletteMap.getSecondaryPromotionPaletteID());
					} else {
						System.err.println("Invalid class detected in promotion branch (Base Class: " + unpromotedClass.toString() + ").");
					}
				} else {
					map.setSecondaryPromotionClassID(0);
					if (paletteMap.getSecondaryPromotionPaletteID() != 0) {
						unusedPaletteIDs.add(paletteMap.getSecondaryPromotionPaletteID());
						paletteMap.setSecondaryPromotionPaletteID(0);
					}
				}
			
				map.setThirdPromotionClassID(0);
				map.setFourthPromotionClassID(0);
				
				if (paletteMap.getThirdPromotionPaletteID() != 0) {
					unusedPaletteIDs.add(paletteMap.getThirdPromotionPaletteID());
					paletteMap.setThirdPromotionPaletteID(0);
				}
				if (paletteMap.getFourthPromotionPaletteID() != 0) {
					unusedPaletteIDs.add(paletteMap.getFourthPromotionPaletteID());
					paletteMap.setFourthPromotionPaletteID(0);
				}
			}
			
			// Some characters use the same palette for more than one promotion path (i.e. Amelia has Great Knight as an option in both Cavalier and Knight.)
			unusedPaletteIDs.removeAll(paletteIDsInUse);
			for (int paletteID : unusedPaletteIDs) {
				markPaletteIDAsFree(paletteID);
			}
		}
	}
	
	public void setPromotedClass(int promotedClassID, int characterID) {
		FE8Data.CharacterClass promotedClass = FE8Data.CharacterClass.valueOf(promotedClassID);
		if (promotedClass == null) { return; }
		if (!FE8Data.CharacterClass.allPromotedClasses.contains(promotedClass)) { return; }
		
		int canonicalCharID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalCharID);
		PaletteMapEntry existingPaletteMap = paletteIndexMap.get(character);
		List<SlotType> palettesNeeded = new ArrayList<SlotType>();
		if (existingPaletteMap.getFirstPromotionPaletteID() == 0) { palettesNeeded.add(SlotType.FIRST_PROMOTION); }
		if (!palettesNeeded.isEmpty()) {
			charactersThatNeedPalettes.put(character, palettesNeeded);
		}
		
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			ClassMapEntry map = paletteClassMap.get(linked);
			PaletteMapEntry paletteMap = paletteIndexMap.get(linked);
			
			map.setTraineeClassID(0);
			if (paletteMap.getTraineePaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getTraineePaletteID());
				paletteMap.setTraineePaletteID(0);
			}
			map.setBaseClassID(0);
			if (paletteMap.getBasePaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getBasePaletteID());
				paletteMap.setBasePaletteID(0);
			}
			map.setSecondaryBaseClassID(0);
			if (paletteMap.getSecondaryBasePaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getSecondaryBasePaletteID());
				paletteMap.setSecondaryBasePaletteID(0);
			}
			map.setFirstPromotionClassID(promotedClassID);
			map.setSecondaryPromotionClassID(0);
			if (paletteMap.getSecondaryPromotionPaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getSecondaryPromotionPaletteID());
				paletteMap.setSecondaryPromotionPaletteID(0);
			}
			map.setThirdPromotionClassID(0);
			if (paletteMap.getThirdPromotionPaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getThirdPromotionPaletteID());
				paletteMap.setThirdPromotionPaletteID(0);
			}
			map.setFourthPromotionClassID(0);
			if (paletteMap.getFourthPromotionPaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getFourthPromotionPaletteID());
				paletteMap.setFourthPromotionPaletteID(0);
			}
		}
	}
	
	public void setTraineeClass(int traineeClassID, int characterID) {
		// This should only be done after we figure out how to universally promote trainees.
		
		FE8Data.CharacterClass traineeClass = FE8Data.CharacterClass.valueOf(traineeClassID);
		if (traineeClass == null) { return; }
		if (!FE8Data.CharacterClass.allTraineeClasses.contains(traineeClass)) { return; }
		
		int canonicalCharID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		
		int primaryBaseClassID = promotionManager.getFirstPromotionOptionClassID(traineeClass.ID);
		int secondaryBaseClassID = promotionManager.getSecondPromotionOptionClassID(traineeClass.ID);
		
		int primaryPromotedClassID = 0;
		int secondaryPromotedClassID = 0;
		int thirdPromotedClassID = 0;
		int fourthPromotedClassID = 0;
		
		FE8Data.CharacterClass primaryBase = FE8Data.CharacterClass.valueOf(primaryBaseClassID);
		if (FE8Data.CharacterClass.allValidClasses.contains(primaryBase)) {
			primaryPromotedClassID = promotionManager.getFirstPromotionOptionClassID(primaryBaseClassID);
			secondaryPromotedClassID = promotionManager.getSecondPromotionOptionClassID(primaryBaseClassID);
		}
		
		FE8Data.CharacterClass secondaryBase = FE8Data.CharacterClass.valueOf(secondaryBaseClassID);
		if (FE8Data.CharacterClass.allValidClasses.contains(secondaryBase)) {
			thirdPromotedClassID = promotionManager.getFirstPromotionOptionClassID(secondaryBaseClassID);
			fourthPromotedClassID = promotionManager.getSecondPromotionOptionClassID(secondaryBaseClassID);
		}
		
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalCharID);
		PaletteMapEntry existingPaletteMap = paletteIndexMap.get(character);
		List<SlotType> palettesNeeded = new ArrayList<SlotType>();
		if (existingPaletteMap.getTraineePaletteID() == 0) { palettesNeeded.add(SlotType.TRAINEE); }
		if (existingPaletteMap.getBasePaletteID() == 0 && primaryBaseClassID != 0) { palettesNeeded.add(SlotType.PRIMARY_BASE); }
		if (existingPaletteMap.getSecondaryBasePaletteID() == 0 && secondaryBaseClassID != 0) { palettesNeeded.add(SlotType.SECONDARY_BASE); }
		if (existingPaletteMap.getFirstPromotionPaletteID() == 0 && primaryPromotedClassID != 0) { palettesNeeded.add(SlotType.FIRST_PROMOTION); }
		if (existingPaletteMap.getSecondaryPromotionPaletteID() == 0 && secondaryPromotedClassID != 0) { palettesNeeded.add(SlotType.SECOND_PROMOTION); }
		if (existingPaletteMap.getThirdPromotionPaletteID() == 0 && thirdPromotedClassID != 0) { palettesNeeded.add(SlotType.THIRD_PROMOTION); }
		if (existingPaletteMap.getFourthPromotionPaletteID() == 0 && fourthPromotedClassID != 0) { palettesNeeded.add(SlotType.FOURTH_PROMOTION); }
		if (!palettesNeeded.isEmpty()) {
			charactersThatNeedPalettes.put(character, palettesNeeded);
		}
		
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			ClassMapEntry map = paletteClassMap.get(linked);
			PaletteMapEntry paletteMap = paletteIndexMap.get(linked);
			
			map.setTraineeClassID(traineeClassID);
			map.setBaseClassID(primaryBaseClassID);
			map.setSecondaryBaseClassID(secondaryBaseClassID);
			map.setFirstPromotionClassID(primaryPromotedClassID);
			map.setSecondaryPromotionClassID(secondaryPromotedClassID);
			map.setThirdPromotionClassID(thirdPromotedClassID);
			map.setFourthPromotionClassID(fourthPromotedClassID);
			
			if (primaryBaseClassID == 0 && paletteMap.getBasePaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getBasePaletteID());
				paletteMap.setBasePaletteID(0);
			}
			if (secondaryBaseClassID == 0 && paletteMap.getSecondaryBasePaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getSecondaryBasePaletteID());
				paletteMap.setSecondaryBasePaletteID(0);
			}
			if (primaryPromotedClassID == 0 && paletteMap.getFirstPromotionPaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getFirstPromotionPaletteID());
				paletteMap.setFirstPromotionPaletteID(0);
			}
			if (secondaryPromotedClassID == 0 && paletteMap.getSecondaryPromotionPaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getSecondaryPromotionPaletteID());
				paletteMap.setSecondaryPromotionPaletteID(0);
			}
			if (thirdPromotedClassID == 0 && paletteMap.getThirdPromotionPaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getThirdPromotionPaletteID());
				paletteMap.setThirdPromotionPaletteID(0);
			}
			if (fourthPromotedClassID == 0 && paletteMap.getFourthPromotionPaletteID() != 0) {
				markPaletteIDAsFree(paletteMap.getFourthPromotionPaletteID());
				paletteMap.setFourthPromotionPaletteID(0);
			}
		}
	}
	
	public void commitChanges(DiffCompiler compiler) {
		for (MapEntry entry : paletteClassMap.values()) {
			entry.commit(compiler);
		}
		for (MapEntry entry : paletteIndexMap.values()) {
			entry.commit(compiler);
		}
	}
}
