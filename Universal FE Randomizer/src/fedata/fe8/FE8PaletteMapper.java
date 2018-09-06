package fedata.fe8;

import java.util.HashMap;
import java.util.Map;

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
public class FE8PaletteMapper {
	
	private class PromotionBranch {
		byte[] data;
		
		private PromotionBranch(FileHandler handler, long offset) {
			data = handler.readBytesAtOffset(offset, FE8PromotionBranchTableEntryLength);
		}
		
		private int getFirstPromotion() {
			return data[0] & 0xFF;
		}
		
		private int getSecondPromotion() {
			return data[1] & 0xFF;
		}
	}
	
	private class MapEntry {
		byte[] originalData;
		byte[] data;
		
		long originalOffset;
		
		Boolean wasModified;
		
		private MapEntry(FileHandler handler, long offset) {
			originalOffset = offset;
			originalData = handler.readBytesAtOffset(offset, FE8PaletteClassTableEntryLength);
			data = originalData.clone();
			
			wasModified = false;
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
	
	private static final long FE8PaletteClassTablePointer = 0x575B4;
	//private static final long FE8DefaultPaletteClassTableOffset = 0x95E0A4L;
	private static final int FE8PaletteClassTableEntryLength = 7;
	
	// The order of the promotion classes kind of matters, so we need to look up the correct ordering from another table.
	
	private static final long FE8PromotionBranchTablePointer = 0xCC7D0;
	//private static final long FE8PromotionBranchTableOffset = 0x95DFA4L;
	private static final int FE8PromotionBranchTableEntryLength = 2;
	
	private Map<FE8Data.Character, MapEntry> paletteClassMap;
	private Map<FE8Data.CharacterClass, PromotionBranch> promotionBranches;
	
	public FE8PaletteMapper(FileHandler handler) {
		paletteClassMap = new HashMap<FE8Data.Character, MapEntry>();
		
		long tableOffset = FileReadHelper.readAddress(handler, FE8PaletteClassTablePointer);
		// As far as the indices go, it looks like the offsets follow the same order as the characters' IDs, albeit - 1, so Eirika starts at 0x0 instead of 0x1, Seth at 0x1 instead of 0x2, and so on.
		
		FE8Data.Character[] characters = FE8Data.Character.values();
		for (FE8Data.Character currentCharacter : characters) {
			int index = currentCharacter.ID - 1;
			long offset = FE8PaletteClassTableEntryLength * index + tableOffset;
			MapEntry characterMapEntry = new MapEntry(handler, offset);
			paletteClassMap.put(currentCharacter, characterMapEntry);
		}
		
		long promotionTableOffset = FileReadHelper.readAddress(handler, FE8PromotionBranchTablePointer);
		// Unlike above, these actually point to a 0 index, so the class ID can be used as is.
		
		promotionBranches = new HashMap<FE8Data.CharacterClass, PromotionBranch>();
		FE8Data.CharacterClass[] charClasses = FE8Data.CharacterClass.allUnpromotedClasses.toArray(new FE8Data.CharacterClass[FE8Data.CharacterClass.allUnpromotedClasses.size()]);
		for (FE8Data.CharacterClass currentClass : charClasses) {
			int index = currentClass.ID;
			long offset = FE8PromotionBranchTableEntryLength * index + promotionTableOffset;
			PromotionBranch branch = new PromotionBranch(handler, offset);
			promotionBranches.put(currentClass, branch);
		}
		FE8Data.CharacterClass[] traineeClasses = FE8Data.CharacterClass.allTraineeClasses.toArray(new FE8Data.CharacterClass[FE8Data.CharacterClass.allTraineeClasses.size()]);
		for (FE8Data.CharacterClass traineeClass : traineeClasses) {
			int index = traineeClass.ID;
			long offset = FE8PromotionBranchTableEntryLength * index + promotionTableOffset;
			PromotionBranch branch = new PromotionBranch(handler, offset);
			promotionBranches.put(traineeClass, branch);
		}
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
			
			PromotionBranch branch = promotionBranches.get(unpromotedClass);
			int primaryPromotionID = branch.getFirstPromotion();
			int secondaryPromotionID = branch.getSecondPromotion();
			
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
			
			PromotionBranch traineeBranch = promotionBranches.get(traineeClass);
			int primaryBaseClassID = traineeBranch.getFirstPromotion();
			int secondaryBaseClassID = traineeBranch.getSecondPromotion();
			
			if (primaryBaseClassID != 0) {
				FE8Data.CharacterClass primaryBaseClass = FE8Data.CharacterClass.valueOf(primaryBaseClassID);
				if (primaryBaseClass != null) {
					map.setBaseClassID(primaryBaseClassID);
					PromotionBranch primaryBranch = promotionBranches.get(primaryBaseClass);
					int primaryPromotedClassID = primaryBranch.getFirstPromotion();
					int secondaryPromotedClassID = primaryBranch.getSecondPromotion();
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
					PromotionBranch secondaryBranch = promotionBranches.get(secondaryBaseClass);
					int thirdPromotedClassID = secondaryBranch.getFirstPromotion();
					int fourthPromotedClassID = secondaryBranch.getSecondPromotion();
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
