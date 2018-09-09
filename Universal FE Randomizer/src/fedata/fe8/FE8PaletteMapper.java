package fedata.fe8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;

import io.FileHandler;
import random.exc.NotReached;

// FE8 uses two auxiliary tables to map palettes based on class.
// The first tells us which classes a character can be, and the second
// says which palette to use based on those classes.
// Presumably, if the class does not match, the game will default
// to the default generic palette, so we need to make sure we map these properly.

// For the most part, we don't need to mess with the palette assignment, as we'll
// be updating those colors anyway, but we do need to make sure the classes line up.
// TODO: Integrate this table. Some units are going to cheat a little by altering the underlying
// sprite itself, but characters like Tethys who go from no promotions to some promotions won't have
// enough palettes to support it.
public class FE8PaletteMapper {
	
	public enum PaletteMapType {
		UNKNOWN, TRAINEE, UNPROMOTED, PROMOTED;
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
		
		private PaletteMapType getMapType() {
			if (getTraineeClassID() != 0 && getBaseClassID() != 0 && getSecondaryBaseClassID() != 0 && getFirstPromotionClassID() != 0 &&
					getSecondaryPromotionClassID() != 0 && getThirdPromotionClassID() != 0 && getFourthPromotionClassID() != 0) {
				return PaletteMapType.TRAINEE;
			} else if (getTraineeClassID() == 0 && getBaseClassID() != 0 && getSecondaryBaseClassID() == 0 &&
					getFirstPromotionClassID() != 0 && getThirdPromotionClassID() == 0 && getFourthPromotionClassID() == 0) { // The second promotion is optional, since some classes only have one option.
				return PaletteMapType.UNPROMOTED;
			} else if (getTraineeClassID() == 0 && getBaseClassID() == 0 && getSecondaryBaseClassID() == 0 &&
					getFirstPromotionClassID() != 0 && getSecondaryPromotionClassID() == 0 && getThirdPromotionClassID() == 0 && getFourthPromotionClassID() == 0) {
				return PaletteMapType.PROMOTED;
			}
			
			NotReached.trigger("Detected unknown palette map type.");
			
			return PaletteMapType.UNKNOWN;
		}
		
		private int getTraineeClassID() {
			return data[0] & 0xFF;
		}
		private void setTraineeClassID(int newClassID) {
			data[0] = (byte)(newClassID & 0xFF);
			wasModified = true;
		}
		
		private int getBaseClassID() {
			return data[1] & 0xFF;
		}
		private void setBaseClassID(int newClassID) {
			data[1] = (byte)(newClassID & 0xFF);
			wasModified = true;
		}
		
		private int getSecondaryBaseClassID() {
			return data[2] & 0xFF;
		}
		private void setSecondaryBaseClassID(int newClassID) {
			data[2] = (byte)(newClassID & 0xFF);
			wasModified = true;
		}
		
		private int getFirstPromotionClassID() {
			return data[3] & 0xFF;
		}
		private void setFirstPromotionClassID(int newClassID) {
			data[3] = (byte)(newClassID & 0xFF);
			wasModified = true;
		}
		
		private int getSecondaryPromotionClassID() {
			return data[4] & 0xFF;
		}
		private void setSecondaryPromotionClassID(int newClassID) {
			data[4] = (byte)(newClassID & 0xFF);
			wasModified = true;
		}
		
		private int getThirdPromotionClassID() {
			return data[5] & 0xFF;
		}
		private void setThirdPromotionClassID(int newClassID) {
			data[5] = (byte)(newClassID & 0xFF);
			wasModified = true;
		}
		
		private int getFourthPromotionClassID() {
			return data[6] & 0xFF;
		}
		private void setFourthPromotionClassID(int newClassID) {
			data[6] = (byte)(newClassID & 0xFF);
			wasModified = true;
		}
		
		private void reset() {
			data = originalData.clone();
			wasModified = false;
		}
		
		private void commit(DiffCompiler compiler) {
			if (wasModified) {
				compiler.addDiff(new Diff(originalOffset, data.length, data, originalData));
			}
		}
	}
	
	private Map<FE8Data.Character, MapEntry> paletteClassMap;
	private FE8PromotionManager promotionManager;
	
	public FE8PaletteMapper(FileHandler handler, FE8PromotionManager promotionManager) {
		paletteClassMap = new HashMap<FE8Data.Character, MapEntry>();
		
		long tableOffset = FileReadHelper.readAddress(handler, FE8Data.PaletteClassTablePointer);
		// As far as the indices go, it looks like the offsets follow the same order as the characters' IDs, albeit - 1, so Eirika starts at 0x0 instead of 0x1, Seth at 0x1 instead of 0x2, and so on.
		
		FE8Data.Character[] characters = FE8Data.Character.values();
		for (FE8Data.Character currentCharacter : characters) {
			int index = currentCharacter.ID - 1;
			long offset = FE8Data.BytesPerPaletteTableEntry * index + tableOffset;
			MapEntry characterMapEntry = new MapEntry(handler, offset);
			paletteClassMap.put(currentCharacter, characterMapEntry);
		}
		
		this.promotionManager = promotionManager;
	}
	
	public PaletteMapType getMapTypeForCharacter(int characterID) {
		FE8Data.Character character = FE8Data.Character.valueOf(characterID);
		if (character == null) { return PaletteMapType.UNKNOWN; }
		return paletteClassMap.get(character).getMapType();
	}

	public void setUnpromotedClass(int unpromotedClassID, int characterID) {
		FE8Data.CharacterClass unpromotedClass = FE8Data.CharacterClass.valueOf(unpromotedClassID);
		if (unpromotedClass == null) { return; }
		if (!FE8Data.CharacterClass.allUnpromotedClasses.contains(unpromotedClass)) { return; }
		
		int canonicalCharID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalCharID);
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			MapEntry map = paletteClassMap.get(linked);
			
			map.setTraineeClassID(0);
			map.setBaseClassID(unpromotedClassID);
			map.setSecondaryBaseClassID(0);
			
			int primaryPromotionID = promotionManager.getFirstPromotionOptionClassID(unpromotedClass.ID);
			int secondaryPromotionID = promotionManager.getSecondPromotionOptionClassID(unpromotedClass.ID);
			
			if (primaryPromotionID != 0) {
				FE8Data.CharacterClass primaryPromotionClass = FE8Data.CharacterClass.valueOf(primaryPromotionID);
				if (primaryPromotionClass != null) {
					map.setFirstPromotionClassID(primaryPromotionID);
				} else {
					System.err.println("Invalid class detected in promotion branch (Base Class: " + unpromotedClass.toString() + ").");
				}
			} else {
				map.setFirstPromotionClassID(0);
			}
			
			if (secondaryPromotionID != 0) {
				FE8Data.CharacterClass secondaryPromotionClass = FE8Data.CharacterClass.valueOf(secondaryPromotionID);
				if (secondaryPromotionClass != null) {
					map.setSecondaryPromotionClassID(secondaryPromotionID);
				} else {
					System.err.println("Invalid class detected in promotion branch (Base Class: " + unpromotedClass.toString() + ").");
				}
			} else {
				map.setSecondaryPromotionClassID(0);
			}
			
			map.setThirdPromotionClassID(0);
			map.setFourthPromotionClassID(0);
		}
	}
	
	public void setPromotedClass(int promotedClassID, int characterID) {
		FE8Data.CharacterClass promotedClass = FE8Data.CharacterClass.valueOf(promotedClassID);
		if (promotedClass == null) { return; }
		if (!FE8Data.CharacterClass.allPromotedClasses.contains(promotedClass)) { return; }
		
		int canonicalCharID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalCharID);
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			MapEntry map = paletteClassMap.get(linked);
			
			map.setTraineeClassID(0);
			map.setBaseClassID(0);
			map.setSecondaryBaseClassID(0);
			map.setFirstPromotionClassID(promotedClassID);
			map.setSecondaryPromotionClassID(0);
			map.setThirdPromotionClassID(0);
			map.setFourthPromotionClassID(0);
		}
	}
	
	public void setTraineeClass(int traineeClassID, int characterID) {
		// This should only be done after we figure out how to universally promote trainees.
		
		FE8Data.CharacterClass traineeClass = FE8Data.CharacterClass.valueOf(traineeClassID);
		if (traineeClass == null) { return; }
		if (!FE8Data.CharacterClass.allTraineeClasses.contains(traineeClass)) { return; }
		
		int canonicalCharID = FE8Data.Character.canonicalIDForCharacterID(characterID);
		
		FE8Data.Character character = FE8Data.Character.valueOf(canonicalCharID);
		for (FE8Data.Character linked : FE8Data.Character.allLinkedCharactersFor(character)) {
			MapEntry map = paletteClassMap.get(linked);
			
			map.setTraineeClassID(traineeClassID);
			
			int primaryBaseClassID = promotionManager.getFirstPromotionOptionClassID(traineeClass.ID);
			int secondaryBaseClassID = promotionManager.getSecondPromotionOptionClassID(traineeClass.ID);
			
			if (primaryBaseClassID != 0) {
				FE8Data.CharacterClass primaryBaseClass = FE8Data.CharacterClass.valueOf(primaryBaseClassID);
				if (primaryBaseClass != null) {
					map.setBaseClassID(primaryBaseClassID);
					
					int primaryPromotedClassID = promotionManager.getFirstPromotionOptionClassID(primaryBaseClass.ID);
					int secondaryPromotedClassID = promotionManager.getSecondPromotionOptionClassID(primaryBaseClass.ID);
					
					if (primaryPromotedClassID != 0) {
						FE8Data.CharacterClass primaryPromotedClass = FE8Data.CharacterClass.valueOf(primaryPromotedClassID);
						if (primaryPromotedClass != null) {
							map.setFirstPromotionClassID(primaryPromotedClassID);
						} else {
							System.err.println("Invalid class detected in promotion branch (Base Class: " + primaryBaseClass.toString() + ").");
						}
					}
					if (secondaryPromotedClassID != 0) {
						FE8Data.CharacterClass secondaryPromotedClass = FE8Data.CharacterClass.valueOf(secondaryPromotedClassID);
						if (secondaryPromotedClass != null) {
							map.setSecondaryPromotionClassID(secondaryPromotedClassID);
						} else {
							System.err.println("Invalid class detected in promotion branch (Base Class: " + primaryBaseClass.toString() + ").");
						}
					}
				} else {
					System.err.println("Invalid class detected in promotion branch (Base Class: " + traineeClass.toString() + ").");
				}
			} else {
				map.setFirstPromotionClassID(0);
				map.setSecondaryPromotionClassID(0);
			}
			
			if (secondaryBaseClassID != 0) {
				FE8Data.CharacterClass secondaryBaseClass = FE8Data.CharacterClass.valueOf(secondaryBaseClassID);
				if (secondaryBaseClass != null) {
					map.setSecondaryBaseClassID(secondaryBaseClassID);
					
					int thirdPromotedClassID = promotionManager.getFirstPromotionOptionClassID(secondaryBaseClass.ID);
					int fourthPromotedClassID = promotionManager.getSecondPromotionOptionClassID(secondaryBaseClass.ID);
					
					if (thirdPromotedClassID != 0) {
						FE8Data.CharacterClass thirdPromotedClass = FE8Data.CharacterClass.valueOf(thirdPromotedClassID);
						if (thirdPromotedClass != null) {
							map.setThirdPromotionClassID(thirdPromotedClassID);
						} else {
							System.err.println("Invalid class detected in promotion branch (Base Class: " + secondaryBaseClass.toString() + ").");
						}
					}
					if (fourthPromotedClassID != 0) {
						FE8Data.CharacterClass fourthPromotedClass = FE8Data.CharacterClass.valueOf(fourthPromotedClassID);
						if (fourthPromotedClass != null) {
							map.setSecondaryPromotionClassID(fourthPromotedClassID);
						} else {
							System.err.println("Invalid class detected in promotion branch (Base Class: " + secondaryBaseClass.toString() + ").");
						}
					}
				} else {
					System.err.println("Invalid class detected in promotion branch (Base Class: " + traineeClass.toString() + ").");
				}
			} else {
				map.setThirdPromotionClassID(0);
				map.setFourthPromotionClassID(0);
			}
		}
	}
	
	public void commitChanges(DiffCompiler compiler) {
		for (MapEntry entry : paletteClassMap.values()) {
			entry.commit(compiler);
		}
	}
}
