package fedata.gba.fe8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import random.gba.loader.PromotionDataLoader;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.WhyDoesJavaNotHaveThese;
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
		
		public void synchronize(MapEntry otherMap) {
			setByte0(otherMap.getByte0());
			setByte1(otherMap.getByte1());
			setByte2(otherMap.getByte2());
			setByte3(otherMap.getByte3());
			setByte4(otherMap.getByte4());
			setByte5(otherMap.getByte5());
			setByte6(otherMap.getByte6());
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
		
		@SuppressWarnings("unused")
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
	
	public class ClassMapEntry extends MapEntry {
		
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
		
		public int getTraineeClassID() { return getByte0(); }
		public void setTraineeClassID(int newClassID) { setByte0(newClassID); }
		
		public int getBaseClassID() { return getByte1(); }
		public void setBaseClassID(int newClassID) { setByte1(newClassID); }
		
		public int getSecondaryBaseClassID() { return getByte2(); }
		public void setSecondaryBaseClassID(int newClassID) { setByte2(newClassID); }
		
		public int getFirstPromotionClassID() { return getByte3(); }
		public void setFirstPromotionClassID(int newClassID) { setByte3(newClassID); }
		
		public int getSecondaryPromotionClassID() { return getByte4(); }
		public void setSecondaryPromotionClassID(int newClassID) { setByte4(newClassID); }
		
		public int getThirdPromotionClassID() { return getByte5(); }
		public void setThirdPromotionClassID(int newClassID) { setByte5(newClassID); }
		
		public int getFourthPromotionClassID() { return getByte6(); }
		public void setFourthPromotionClassID(int newClassID) { setByte6(newClassID); }
	}
	
	private Map<FE8Data.Character, ClassMapEntry> paletteClassMap;
	private Map<FE8Data.Character, PaletteMapEntry> paletteIndexMap;
	
	private Map<Integer, Integer> registeredPaletteLengths;
	private Map<Integer, Long> registeredPaletteOffsets;
	private Set<Long> registeredOffsets;
	
	private Map<FE8Data.Character, List<SlotType>> charactersThatNeedPalettes; // Maps to array of slots.
	
	private List<Integer> emptyPaletteIDs; // Can be used as long as the address is written after the pointer table.
	private Map<Integer, List<Integer>> recycledPaletteIDsByLength; // Maps lengths to recycled paletteIDs of that length.
	
	private PromotionDataLoader promotionManager;
	
	public FE8PaletteMapper(FileHandler handler, PromotionDataLoader promotionManager, List<Integer> emptyIDs) {
		paletteClassMap = new HashMap<FE8Data.Character, ClassMapEntry>();
		paletteIndexMap = new HashMap<FE8Data.Character, PaletteMapEntry>();
		
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
		
		this.emptyPaletteIDs = new ArrayList<Integer>(emptyIDs);
		this.registeredPaletteLengths = new HashMap<Integer, Integer>();
		this.registeredPaletteOffsets = new HashMap<Integer, Long>();
		this.recycledPaletteIDsByLength = new HashMap<Integer, List<Integer>>();
		this.registeredOffsets = new HashSet<Long>();
	}
	
	public void registerPaletteID(int paletteID, int length, Long offset) {
		if (paletteID == 0) { return; }
		if (registeredPaletteLengths.get(paletteID) != null) { return; }
		registeredPaletteLengths.put(paletteID, length);
		assert registeredPaletteOffsets.get(paletteID) == null : "Already registered this paletteID";
		if (offset != null) {
			assert registeredOffsets.contains(offset) == false : "This offset has already been registered.";
			registeredOffsets.add(offset);
			registeredPaletteOffsets.put(paletteID, offset);
		}
	}
	
	public Integer getRegisteredPaletteLength(int paletteID) {
		return registeredPaletteLengths.get(paletteID);
	}
	
	public Long getRegisteredPaletteOffset(int paletteID) {
		return registeredPaletteOffsets.get(paletteID);
	}
	
	public ClassMapType getMapTypeForCharacter(int characterID) {
		FE8Data.Character character = FE8Data.Character.valueOf(characterID);
		if (character == null) { return ClassMapType.UNKNOWN; }
		return paletteClassMap.get(character).getMapType();
	}
	
	private void markPaletteIDAsFree(int paletteID) {
		if (paletteID == 0) { return; }
		Integer length = getRegisteredPaletteLength(paletteID);
		assert length != null : "No length found for palette being marked as free. ID = 0x" + Integer.toHexString(paletteID);
		DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Freed up palette with ID 0x" + Integer.toHexString(paletteID) + "(size: " + Integer.toString(length) + ")");
		if (length != null) {
			List<Integer> paletteIDList = recycledPaletteIDsByLength.get(length);
			if (paletteIDList == null) {
				paletteIDList = new ArrayList<Integer>();
				recycledPaletteIDsByLength.put(length, paletteIDList);
			}
			
			paletteIDList.add(paletteID);
		}
	}
	
	private Integer requestRecycledPaletteForSize(int paletteSize) {
		if (recycledPaletteIDsByLength.keySet().stream().filter(size -> (size >= paletteSize)).min(WhyDoesJavaNotHaveThese.ascendingIntegerComparator).isPresent()) {
			int length = recycledPaletteIDsByLength.keySet().stream().filter(size -> (size >= paletteSize)).min(WhyDoesJavaNotHaveThese.ascendingIntegerComparator).get();
			List<Integer> availableIDs = recycledPaletteIDsByLength.get(length);
			if (availableIDs.isEmpty()) {
				recycledPaletteIDsByLength.remove(length);
				return requestRecycledPaletteForSize(paletteSize); // Ask again for the next closest available slot.
			} else {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Recycled palette with ID 0x" + Integer.toHexString(availableIDs.get(0)) + "(size: " + Integer.toString(length) + ", requested size: " + Integer.toString(paletteSize) + ")");
				return availableIDs.remove(0);
			}
		}
		return null;
	}
	
	private Integer requestEmptyPaletteForSize(int paletteSize) {
		Integer emptyID = emptyPaletteIDs.remove(0);
		if (emptyID != null) {
			registerPaletteID(emptyID, paletteSize, null);
		}
		
		return emptyID;
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
	
	public int paletteIDForCharacterInClassType(int characterID, SlotType type) {
		FE8Data.Character character = FE8Data.Character.valueOf(FE8Data.Character.canonicalIDForCharacterID(characterID));
		PaletteMapEntry paletteMap = paletteIndexMap.get(character);
		switch (type) {
		case TRAINEE:
			return paletteMap.getTraineePaletteID();
		case PRIMARY_BASE:
			return paletteMap.getBasePaletteID();
		case SECONDARY_BASE:
			return paletteMap.getSecondaryBasePaletteID();
		case FIRST_PROMOTION:
			return paletteMap.getFirstPromotionPaletteID();
		case SECOND_PROMOTION:
			return paletteMap.getSecondaryPromotionPaletteID();
		case THIRD_PROMOTION:
			return paletteMap.getThirdPromotionPaletteID();
		case FOURTH_PROMOTION:
			return paletteMap.getFourthPromotionPaletteID();
		default:
			return 0;
		}
	}
	
	public Map<SlotType, Integer> requestRecycledPaletteIndicesForCharacter(int characterID) {
		assert false : "Shouldn't be using this anymore.";
		int canonicalID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalID);
		List<SlotType> palettesNeeded = charactersThatNeedPalettes.get(character);
		List<SlotType> slotsRemaining = new ArrayList<SlotType>(palettesNeeded);
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Handing out free palettes to character 0x" + Integer.toHexString(characterID));
		
		if (palettesNeeded.size() < emptyPaletteIDs.size()) {
			Map<SlotType, Integer> recycledIndices = new HashMap<SlotType, Integer>();
			while (!slotsRemaining.isEmpty()) {
				int paletteIndex = emptyPaletteIDs.remove(0);
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

	public void setUnpromotedClass(int unpromotedClassID, int characterID, Boolean setPromotions, int basePaletteSize, int promotedPaletteSize, int secondPromotionPaletteSize) {
		FE8Data.CharacterClass unpromotedClass = FE8Data.CharacterClass.valueOf(unpromotedClassID);
		if (unpromotedClass == null) { return; }
		if (!FE8Data.CharacterClass.allUnpromotedClasses.contains(unpromotedClass)) { return; }
		
		int primaryPromotionID = promotionManager.getFirstPromotionOptionClassID(unpromotedClass.ID);
		int secondaryPromotionID = promotionManager.getSecondPromotionOptionClassID(unpromotedClass.ID);
		int canonicalCharID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalCharID);
		PaletteMapEntry existingPaletteMap = paletteIndexMap.get(character);
		List<SlotType> palettesNeeded = new ArrayList<SlotType>();
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Assigning unpromoted class ID 0x" + Integer.toHexString(unpromotedClassID) + " (" + unpromotedClass.toString() + ") to character 0x" + Integer.toHexString(characterID) + " (" + character.toString() + ")");
		
		ClassMapEntry classMap = paletteClassMap.get(character);
		classMap.setBaseClassID(unpromotedClassID);
		
		Integer paletteLength = registeredPaletteLengths.get(existingPaletteMap.getBasePaletteID());
		
		if (existingPaletteMap.getBasePaletteID() == 0 || (paletteLength != null && basePaletteSize > paletteLength)) {
			if (existingPaletteMap.getBasePaletteID() != 0) {
				// We're about to replace this ID. Go ahead and recycle it.
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Current palette (0x" + Integer.toHexString(existingPaletteMap.getBasePaletteID()) + ") is too small, looking for alternatives...");
				markPaletteIDAsFree(existingPaletteMap.getBasePaletteID());
				existingPaletteMap.setBasePaletteID(0);
			}
			
			// This palette won't fit. We need to use a recycled one or use an empty one.
			Integer recycledPaletteID = requestRecycledPaletteForSize(basePaletteSize);
			if (recycledPaletteID != null) {
				existingPaletteMap.setBasePaletteID(recycledPaletteID);
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Found recycled palette 0x" + Integer.toHexString(recycledPaletteID));
			} else {
				Integer emptyID = requestEmptyPaletteForSize(basePaletteSize);
				if (emptyID != null) {
					existingPaletteMap.setBasePaletteID(emptyID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Using empty palette 0x" + Integer.toHexString(emptyID));
				} else {
					palettesNeeded.add(SlotType.PRIMARY_BASE);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "No Palettes currently available. Adding to waitlist.");
				}
			}
		} else {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Palette (0x" + Integer.toHexString(existingPaletteMap.getBasePaletteID()) + ") OK! (oldSize: " + Integer.toString(paletteLength) + " newSize: " + Integer.toString(basePaletteSize) + ")");
		}
		
		if (setPromotions) {
			if (primaryPromotionID != 0) {
				classMap.setFirstPromotionClassID(primaryPromotionID);
				paletteLength = registeredPaletteLengths.get(existingPaletteMap.getFirstPromotionPaletteID());
				if (existingPaletteMap.getFirstPromotionPaletteID() == 0 || (paletteLength != null && promotedPaletteSize > paletteLength)) {
					if (existingPaletteMap.getFirstPromotionPaletteID() != 0) {
						DebugPrinter.log(DebugPrinter.Key.PALETTE, "Current palette (0x" + Integer.toHexString(existingPaletteMap.getFirstPromotionPaletteID()) + ") is too small or doesn't exist, looking for alternatives...");
						markPaletteIDAsFree(existingPaletteMap.getFirstPromotionPaletteID());
						existingPaletteMap.setFirstPromotionPaletteID(0);
					}
					
					Integer recycledPaletteID = requestRecycledPaletteForSize(promotedPaletteSize);
					if (recycledPaletteID != null) {
						existingPaletteMap.setFirstPromotionPaletteID(recycledPaletteID);
						DebugPrinter.log(DebugPrinter.Key.PALETTE, "Found recycled palette 0x" + Integer.toHexString(recycledPaletteID));
					} else {
						Integer emptyID = requestEmptyPaletteForSize(promotedPaletteSize);
						if (emptyID != null) {
							existingPaletteMap.setFirstPromotionPaletteID(emptyID);
							DebugPrinter.log(DebugPrinter.Key.PALETTE, "Using empty palette 0x" + Integer.toHexString(emptyID));
						} else {
							palettesNeeded.add(SlotType.FIRST_PROMOTION);
							DebugPrinter.log(DebugPrinter.Key.PALETTE, "No Palettes currently available. Adding to waitlist.");
						}
					}
				} else {
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Palette (0x" + Integer.toHexString(existingPaletteMap.getFirstPromotionPaletteID()) + ") OK! (oldSize: " + Integer.toString(paletteLength) + " newSize: " + Integer.toString(promotedPaletteSize) + ")");
				}
			} else {
				classMap.setFirstPromotionClassID(0);
				if (existingPaletteMap.getFirstPromotionPaletteID() != 0) {
					markPaletteIDAsFree(existingPaletteMap.getFirstPromotionPaletteID());
					existingPaletteMap.setFirstPromotionPaletteID(0);
				}
			}
			
			if (secondaryPromotionID != 0) {
				classMap.setSecondaryPromotionClassID(secondaryPromotionID);
				paletteLength = registeredPaletteLengths.get(existingPaletteMap.getSecondaryPromotionPaletteID());
				if (existingPaletteMap.getSecondaryPromotionPaletteID() == 0 || (paletteLength != null && secondPromotionPaletteSize > paletteLength)) {
					if (existingPaletteMap.getSecondaryPromotionPaletteID() != 0) {
						DebugPrinter.log(DebugPrinter.Key.PALETTE, "Current palette (0x" + Integer.toHexString(existingPaletteMap.getSecondaryPromotionPaletteID()) + ") is too small or doesn't exist, looking for alternatives...");
						markPaletteIDAsFree(existingPaletteMap.getSecondaryPromotionPaletteID());
						existingPaletteMap.setSecondaryPromotionPaletteID(0);
					}
					
					Integer recycledPaletteID = requestRecycledPaletteForSize(secondPromotionPaletteSize);
					if (recycledPaletteID != null) {
						existingPaletteMap.setSecondaryPromotionPaletteID(recycledPaletteID);
						DebugPrinter.log(DebugPrinter.Key.PALETTE, "Found recycled palette 0x" + Integer.toHexString(recycledPaletteID));
					} else {
						Integer emptyID = requestEmptyPaletteForSize(secondPromotionPaletteSize);
						if (emptyID != null) {
							existingPaletteMap.setSecondaryPromotionPaletteID(emptyID);
							DebugPrinter.log(DebugPrinter.Key.PALETTE, "Using empty palette 0x" + Integer.toHexString(emptyID));
						} else {
							palettesNeeded.add(SlotType.SECOND_PROMOTION);
							DebugPrinter.log(DebugPrinter.Key.PALETTE, "No Palettes currently available. Adding to waitlist.");
						}
					}
				} else {
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Palette (0x" + Integer.toHexString(existingPaletteMap.getSecondaryPromotionPaletteID()) + ") OK! (oldSize: " + Integer.toString(paletteLength) + " newSize: " + Integer.toString(secondPromotionPaletteSize) + ")");
				}
			} else {
				classMap.setSecondaryPromotionClassID(0);
				if (existingPaletteMap.getSecondaryPromotionPaletteID() != 0) {
					markPaletteIDAsFree(existingPaletteMap.getSecondaryPromotionPaletteID());
					existingPaletteMap.setSecondaryPromotionPaletteID(0);
				}
			}
		}
		if (!palettesNeeded.isEmpty()) {
			charactersThatNeedPalettes.put(character, palettesNeeded);
		}
		
		// We can recycle any existing third/fourth promotion, trainee, and secondary base class palettes.
		classMap.setTraineeClassID(0);
		if (existingPaletteMap.getTraineePaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getTraineePaletteID());
			existingPaletteMap.setTraineePaletteID(0);
		}
		classMap.setSecondaryBaseClassID(0);
		if (existingPaletteMap.getSecondaryBasePaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getSecondaryBasePaletteID());
			existingPaletteMap.setSecondaryBasePaletteID(0);
		}
		classMap.setThirdPromotionClassID(0);
		if (existingPaletteMap.getThirdPromotionPaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getThirdPromotionPaletteID());
			existingPaletteMap.setThirdPromotionPaletteID(0);
		}
		classMap.setFourthPromotionClassID(0);
		if (existingPaletteMap.getFourthPromotionPaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getFourthPromotionPaletteID());
			existingPaletteMap.setFourthPromotionPaletteID(0);
		}
		
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			// Sync all linked characters to the same palette and class map.
			ClassMapEntry linkedClassMap = paletteClassMap.get(linked);
			PaletteMapEntry linkedPaletteMap = paletteIndexMap.get(linked);
			
			linkedClassMap.synchronize(classMap);
			linkedPaletteMap.synchronize(existingPaletteMap);
		}
	}
	
	public void setPromotedClass(int promotedClassID, int characterID, int paletteSize) {
		FE8Data.CharacterClass promotedClass = FE8Data.CharacterClass.valueOf(promotedClassID);
		if (promotedClass == null) { return; }
		if (!FE8Data.CharacterClass.allPromotedClasses.contains(promotedClass)) { return; }
		
		int canonicalCharID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalCharID);
		PaletteMapEntry existingPaletteMap = paletteIndexMap.get(character);
		List<SlotType> palettesNeeded = new ArrayList<SlotType>();
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Assigning promoted class ID 0x" + Integer.toHexString(promotedClassID) + " (" + promotedClass.toString() + ") to character 0x" + Integer.toHexString(characterID) + " (" + character.toString() + ")");
		
		ClassMapEntry classMap = paletteClassMap.get(character);
		
		classMap.setFirstPromotionClassID(promotedClassID);

		Integer paletteLength = registeredPaletteLengths.get(existingPaletteMap.getFirstPromotionPaletteID());
		
		if (existingPaletteMap.getFirstPromotionPaletteID() == 0 || (paletteLength != null && paletteSize > paletteLength)) {
			if (existingPaletteMap.getFirstPromotionPaletteID() != 0) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Current palette (0x" + Integer.toHexString(existingPaletteMap.getFirstPromotionPaletteID()) + ") is too small, looking for alternatives...");
				markPaletteIDAsFree(existingPaletteMap.getFirstPromotionPaletteID());
				existingPaletteMap.setFirstPromotionPaletteID(0);
			}
			
			Integer recycledPaletteID = requestRecycledPaletteForSize(paletteSize);
			if (recycledPaletteID != null) {
				existingPaletteMap.setFirstPromotionPaletteID(recycledPaletteID);
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Found recycled palette 0x" + Integer.toHexString(recycledPaletteID));
			} else {
				Integer emptyID = requestEmptyPaletteForSize(paletteSize);
				if (emptyID != null) {
					existingPaletteMap.setFirstPromotionPaletteID(emptyID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Using empty palette 0x" + Integer.toHexString(emptyID));
				} else {
					palettesNeeded.add(SlotType.FIRST_PROMOTION);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "No Palettes currently available. Adding to waitlist.");
				}
			}
		} else {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Palette (0x" + Integer.toHexString(existingPaletteMap.getFirstPromotionPaletteID()) + ") OK! (oldSize: " + Integer.toString(paletteLength) + " newSize: " + Integer.toString(paletteSize) + ")");
		}
		
		if (!palettesNeeded.isEmpty()) {
			charactersThatNeedPalettes.put(character, palettesNeeded);
		}
		
		// We can free any trainee or base class palettes, as well as any other promoted palettes.
		classMap.setTraineeClassID(0);
		if (existingPaletteMap.getTraineePaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getTraineePaletteID());
			existingPaletteMap.setTraineePaletteID(0);
		}
		classMap.setBaseClassID(0);
		if (existingPaletteMap.getBasePaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getBasePaletteID());
			existingPaletteMap.setBasePaletteID(0);
		}
		classMap.setSecondaryBaseClassID(0);
		if (existingPaletteMap.getSecondaryBasePaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getSecondaryBasePaletteID());
			existingPaletteMap.setSecondaryBasePaletteID(0);
		}
		classMap.setSecondaryPromotionClassID(0);
		if (existingPaletteMap.getSecondaryPromotionPaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getSecondaryPromotionPaletteID());
			existingPaletteMap.setSecondaryPromotionPaletteID(0);
		}
		classMap.setThirdPromotionClassID(0);
		if (existingPaletteMap.getThirdPromotionPaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getThirdPromotionPaletteID());
			existingPaletteMap.setThirdPromotionPaletteID(0);
		}
		classMap.setFourthPromotionClassID(0);
		if (existingPaletteMap.getFourthPromotionPaletteID() != 0) {
			markPaletteIDAsFree(existingPaletteMap.getFourthPromotionPaletteID());
			existingPaletteMap.setFourthPromotionPaletteID(0);
		}
		
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			// Sync all linked characters to the same palette and class map.
			ClassMapEntry linkedClassMap = paletteClassMap.get(linked);
			PaletteMapEntry linkedPaletteMap = paletteIndexMap.get(linked);
			
			linkedClassMap.synchronize(classMap);
			linkedPaletteMap.synchronize(existingPaletteMap);
		}
	}
	
	public void setTraineeClass(int traineeClassID, int characterID, int traineePaletteSize, int base1PaletteSize, int base2PaletteSize, int promo1PaletteSize, int promo2PaletteSize, int promo3PaletteSize, int promo4PaletteSize) {
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
		
		ClassMapEntry classMap = paletteClassMap.get(character);
		classMap.setTraineeClassID(traineeClassID);
		
		Integer paletteLength = registeredPaletteLengths.get(existingPaletteMap.getTraineePaletteID());
		
		if (existingPaletteMap.getTraineePaletteID() == 0 || (paletteLength != null && traineePaletteSize > paletteLength)) {
			if (existingPaletteMap.getTraineePaletteID() != 0) {
				markPaletteIDAsFree(existingPaletteMap.getTraineePaletteID());
				existingPaletteMap.setTraineePaletteID(0);
			}
			
			Integer recycledPaletteID = requestRecycledPaletteForSize(traineePaletteSize);
			if (recycledPaletteID != null) { existingPaletteMap.setTraineePaletteID(recycledPaletteID); }
			else {
				Integer emptyPaletteID = requestEmptyPaletteForSize(traineePaletteSize);
				if (emptyPaletteID != null) { existingPaletteMap.setTraineePaletteID(emptyPaletteID); }
				else { palettesNeeded.add(SlotType.TRAINEE); }
			}
		}
		
		if (primaryBaseClassID != 0) {
			classMap.setBaseClassID(primaryBaseClassID);
			paletteLength = registeredPaletteLengths.get(existingPaletteMap.getBasePaletteID());
			if ((paletteLength != null && base1PaletteSize > paletteLength) || existingPaletteMap.getBasePaletteID() == 0) {
				if (existingPaletteMap.getBasePaletteID() != 0) {
					markPaletteIDAsFree(existingPaletteMap.getBasePaletteID());
					existingPaletteMap.setBasePaletteID(0);
				}
				
				Integer recycledPaletteID = requestRecycledPaletteForSize(base1PaletteSize);
				if (recycledPaletteID != null) { existingPaletteMap.setBasePaletteID(recycledPaletteID); }
				else {
					Integer emptyPaletteID = requestEmptyPaletteForSize(base1PaletteSize);
					if (emptyPaletteID != null) { existingPaletteMap.setBasePaletteID(emptyPaletteID); }
					else { palettesNeeded.add(SlotType.PRIMARY_BASE); }
				}
			}
		} else {
			classMap.setBaseClassID(0);
			if (existingPaletteMap.getBasePaletteID() != 0) {
				markPaletteIDAsFree(existingPaletteMap.getBasePaletteID());
				existingPaletteMap.setBasePaletteID(0);
			}
		}
		
		if (secondaryBaseClassID != 0) {
			classMap.setSecondaryBaseClassID(secondaryBaseClassID);
			paletteLength = registeredPaletteLengths.get(existingPaletteMap.getSecondaryBasePaletteID());
			if (existingPaletteMap.getSecondaryBasePaletteID() == 0 || (paletteLength != null && base2PaletteSize > paletteLength)) {
				if (existingPaletteMap.getSecondaryBasePaletteID() != 0) {
					markPaletteIDAsFree(existingPaletteMap.getSecondaryBasePaletteID());
					existingPaletteMap.setSecondaryBasePaletteID(0);
				}
				
				Integer recycledPaletteID = requestRecycledPaletteForSize(base2PaletteSize);
				if (recycledPaletteID != null) { existingPaletteMap.setSecondaryBasePaletteID(recycledPaletteID); }
				else {
					Integer emptyPaletteID = requestEmptyPaletteForSize(base2PaletteSize);
					if (emptyPaletteID != null) { existingPaletteMap.setSecondaryBasePaletteID(emptyPaletteID); }
					else { palettesNeeded.add(SlotType.SECONDARY_BASE); }
				}
			}
		} else {
			classMap.setSecondaryBaseClassID(0);
			if (existingPaletteMap.getSecondaryBasePaletteID() != 0) {
				markPaletteIDAsFree(existingPaletteMap.getSecondaryBasePaletteID());
				existingPaletteMap.setSecondaryBasePaletteID(0);
			}
		}
		
		if (primaryPromotedClassID != 0) {
			classMap.setFirstPromotionClassID(primaryPromotedClassID);
			paletteLength = registeredPaletteLengths.get(existingPaletteMap.getFirstPromotionPaletteID());
			if (existingPaletteMap.getFirstPromotionPaletteID() == 0 || (paletteLength != null && promo1PaletteSize > paletteLength)) {
				if (existingPaletteMap.getFirstPromotionPaletteID() != 0) {
					markPaletteIDAsFree(existingPaletteMap.getFirstPromotionPaletteID());
					existingPaletteMap.setFirstPromotionPaletteID(0);
				}
				
				Integer recycledPaletteID = requestRecycledPaletteForSize(promo1PaletteSize);
				if (recycledPaletteID != null) { existingPaletteMap.setFirstPromotionPaletteID(recycledPaletteID); }
				else {
					Integer emptyPaletteID = requestEmptyPaletteForSize(promo1PaletteSize);
					if (emptyPaletteID != null) { existingPaletteMap.setFirstPromotionPaletteID(emptyPaletteID); }
					else { palettesNeeded.add(SlotType.FIRST_PROMOTION); }
				}
			}
		} else {
			classMap.setFirstPromotionClassID(0);
			if (existingPaletteMap.getFirstPromotionPaletteID() != 0) {
				markPaletteIDAsFree(existingPaletteMap.getFirstPromotionPaletteID());
				existingPaletteMap.setFirstPromotionPaletteID(0);
			}
		}
		
		if (secondaryPromotedClassID != 0) {
			classMap.setSecondaryPromotionClassID(secondaryPromotedClassID);
			paletteLength = registeredPaletteLengths.get(existingPaletteMap.getSecondaryPromotionPaletteID());
			if (existingPaletteMap.getSecondaryPromotionPaletteID() == 0 || (paletteLength != null && promo2PaletteSize > paletteLength)) {
				if (existingPaletteMap.getSecondaryPromotionPaletteID() != 0) {
					markPaletteIDAsFree(existingPaletteMap.getSecondaryPromotionPaletteID());
					existingPaletteMap.setSecondaryPromotionPaletteID(0);
				}
				
				Integer recycledPaletteID = requestRecycledPaletteForSize(promo2PaletteSize);
				if (recycledPaletteID != null) { existingPaletteMap.setSecondaryPromotionPaletteID(recycledPaletteID); }
				else {
					Integer emptyPaletteID = requestEmptyPaletteForSize(promo2PaletteSize);
					if (emptyPaletteID != null) { existingPaletteMap.setSecondaryPromotionPaletteID(emptyPaletteID); }
					else { palettesNeeded.add(SlotType.SECOND_PROMOTION); }
				}
			}
		} else {
			classMap.setSecondaryPromotionClassID(0);
			if (existingPaletteMap.getSecondaryPromotionPaletteID() != 0) {
				markPaletteIDAsFree(existingPaletteMap.getSecondaryPromotionPaletteID());
				existingPaletteMap.setSecondaryPromotionPaletteID(0);
			}
		}
		
		if (thirdPromotedClassID != 0) {
			classMap.setThirdPromotionClassID(thirdPromotedClassID);
			if (thirdPromotedClassID == primaryPromotedClassID) {
				existingPaletteMap.setThirdPromotionPaletteID(existingPaletteMap.getFirstPromotionPaletteID());
			} else if (thirdPromotedClassID == secondaryPromotedClassID) {
				existingPaletteMap.setThirdPromotionPaletteID(existingPaletteMap.getSecondaryPromotionPaletteID());
			} else {
				paletteLength = registeredPaletteLengths.get(existingPaletteMap.getThirdPromotionPaletteID());
				if (existingPaletteMap.getThirdPromotionPaletteID() == 0 || (paletteLength != null && promo3PaletteSize > paletteLength)) {
					if (existingPaletteMap.getThirdPromotionPaletteID() != 0) {
						markPaletteIDAsFree(existingPaletteMap.getThirdPromotionPaletteID());
						existingPaletteMap.setThirdPromotionPaletteID(0);
					}
					
					Integer recycledPaletteID = requestRecycledPaletteForSize(promo3PaletteSize);
					if (recycledPaletteID != null) { existingPaletteMap.setThirdPromotionPaletteID(recycledPaletteID); }
					else {
						Integer emptyPaletteID = requestEmptyPaletteForSize(promo3PaletteSize);
						if (emptyPaletteID != null) { existingPaletteMap.setThirdPromotionPaletteID(emptyPaletteID); }
						else { palettesNeeded.add(SlotType.THIRD_PROMOTION); }
					}
				}
			}
		} else {
			classMap.setThirdPromotionClassID(0);
			if (existingPaletteMap.getThirdPromotionPaletteID() != 0) {
				markPaletteIDAsFree(existingPaletteMap.getThirdPromotionPaletteID());
				existingPaletteMap.setThirdPromotionPaletteID(0);
			}
		}
		
		if (fourthPromotedClassID != 0) {
			classMap.setFourthPromotionClassID(fourthPromotedClassID);
			if (fourthPromotedClassID == primaryPromotedClassID) {
				existingPaletteMap.setFourthPromotionPaletteID(existingPaletteMap.getFirstPromotionPaletteID());
			} else if (fourthPromotedClassID == secondaryPromotedClassID) {
				existingPaletteMap.setFourthPromotionPaletteID(existingPaletteMap.getSecondaryPromotionPaletteID());
			} else {
				paletteLength = registeredPaletteLengths.get(existingPaletteMap.getFourthPromotionPaletteID());
				if (existingPaletteMap.getFourthPromotionPaletteID() == 0 || (paletteLength != null && promo4PaletteSize > paletteLength)) {
					if (existingPaletteMap.getFourthPromotionPaletteID() != 0) {
						markPaletteIDAsFree(existingPaletteMap.getFourthPromotionPaletteID());
						existingPaletteMap.setFourthPromotionPaletteID(0);
					}
					
					Integer recycledPaletteID = requestRecycledPaletteForSize(promo4PaletteSize);
					if (recycledPaletteID != null) { existingPaletteMap.setFourthPromotionPaletteID(recycledPaletteID); }
					else {
						Integer emptyPaletteID = requestEmptyPaletteForSize(promo4PaletteSize);
						if (emptyPaletteID != null) { existingPaletteMap.setFourthPromotionPaletteID(emptyPaletteID); }
						else { palettesNeeded.add(SlotType.FOURTH_PROMOTION); }
					}
				}
			}
		} else {
			classMap.setFourthPromotionClassID(0);
			if (existingPaletteMap.getFourthPromotionPaletteID() != 0) {
				markPaletteIDAsFree(existingPaletteMap.getFourthPromotionPaletteID());
				existingPaletteMap.setFourthPromotionPaletteID(0);
			}
		}
		
		if (!palettesNeeded.isEmpty()) {
			charactersThatNeedPalettes.put(character, palettesNeeded);
		}
		
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			// Sync all linked characters to the same palette and class map.
			ClassMapEntry linkedClassMap = paletteClassMap.get(linked);
			PaletteMapEntry linkedPaletteMap = paletteIndexMap.get(linked);
			
			linkedClassMap.synchronize(classMap);
			linkedPaletteMap.synchronize(existingPaletteMap);
		}
	}
	
	public ClassMapEntry getEntryForCharacter(FE8Data.Character character) {
		return paletteClassMap.get(character);
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
