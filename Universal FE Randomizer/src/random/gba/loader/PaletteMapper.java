package random.gba.loader;

import java.util.ArrayList;
import java.util.List;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;

// For use with FE6 and FE7 only.
public class PaletteMapper {
	
	public enum ClassType {
		UNPROMOTED, PROMOTED, UNPROMOTED_ONLY;
	}
	
	private static class WaitListItem {
		GBAFECharacterData character;
		ClassType requiredType;
		
		private WaitListItem(GBAFECharacterData character, ClassType type) {
			this.character = character;
			this.requiredType = type;
		}
	}
	
	private List<Integer> recycledPaletteIDs = new ArrayList<Integer>();
	private List<WaitListItem> waitList = new ArrayList<WaitListItem>();
	
	private CharacterDataLoader charData;
	private ClassDataLoader classData;
	
	public PaletteMapper(CharacterDataLoader charData, ClassDataLoader classData) {
		this.charData = charData;
		this.classData = classData;
	}
	
	public void flushWaitListWithEmptyPaletteIDs(List<Integer> emptyPaletteIDs) {
		int idIndex = 0;
		
		while (idIndex < emptyPaletteIDs.size() && !waitList.isEmpty()) {
			WaitListItem queuedItem = waitList.get(0);
			waitList.remove(0);
			
			int paletteID = emptyPaletteIDs.get(idIndex++);
			
			GBAFECharacterData[] linked = charData.linkedCharactersForCharacter(queuedItem.character);
			for (GBAFECharacterData linkedChar : linked) {
				if (queuedItem.requiredType == ClassType.PROMOTED) {
					linkedChar.setPromotedPaletteIndex(paletteID);
				} else if (queuedItem.requiredType == ClassType.UNPROMOTED) {
					linkedChar.setUnpromotedPaletteIndex(paletteID);
				} else {
					assert false : "Unpromoted-only is not a valid type to wait on.";
				}
			}
		}
	}
	
	public void prepareCharacterForClass(int characterID, int classID) {
		GBAFECharacterData character = charData.characterWithID(characterID);
		GBAFECharacterData[] linked = charData.linkedCharactersForCharacter(character);
		
		GBAFEClassData sourceClass = classData.classForID(character.getClassID());
		GBAFEClassData targetClass = classData.classForID(classID);
		
		boolean sourceIsPromoted = classData.canClassDemote(sourceClass.getID());
		boolean targetIsPromoted = classData.canClassDemote(targetClass.getID());
		
		boolean sourceCanPromote = classData.canClassPromote(sourceClass.getID());
		boolean targetCanPromote = classData.canClassPromote(targetClass.getID());
		
		ClassType sourceType;
		ClassType targetType;
		
		if (sourceIsPromoted) { sourceType = ClassType.PROMOTED; }
		else if (sourceCanPromote) { sourceType = ClassType.UNPROMOTED; }
		else { sourceType = ClassType.UNPROMOTED_ONLY; }
		
		if (targetIsPromoted) { targetType = ClassType.PROMOTED; }
		else if (targetCanPromote) { targetType = ClassType.UNPROMOTED; }
		else { targetType = ClassType.UNPROMOTED_ONLY; }
		
		if (sourceType == targetType) {
			// Nothing to do here.
			return;
		}
		
		if (sourceType == ClassType.PROMOTED) {
			assert character.getUnpromotedPaletteIndex() == 0 || character.getUnpromotedPaletteIndex() == character.getPromotedPaletteIndex() : "Character is promoted, yet already has a unique unpromoted palette.";
			character.setUnpromotedPaletteIndex(0);
			if (targetType == ClassType.UNPROMOTED) {
				// Need to add an unpromoted palette.
				allocatePaletteToCharacter(character, ClassType.UNPROMOTED);
				if (character.getPromotedPaletteIndex() == 0) {
					// Some characters are relying on the sprite's base palette. If we can, we should give them a real palette.
					allocatePaletteToCharacter(character, ClassType.PROMOTED);
				}
			} else { // Unpromoted-only
				// We can just move the promoted palette to the unpromoted palette.
				if (character.getPromotedPaletteIndex() == 0) {
					// In the case we didn't have a promoted palette to begin with, just allocate an unpromoted one.
					allocatePaletteToCharacter(character, ClassType.UNPROMOTED);
				} else {
					for (GBAFECharacterData linkedChar : linked) {
						linkedChar.setUnpromotedPaletteIndex(linkedChar.getPromotedPaletteIndex());
						linkedChar.setPromotedPaletteIndex(0);
					}
				}
			}
		} else if (sourceType == ClassType.UNPROMOTED) {
			if (targetType == ClassType.PROMOTED) {
				// We can free up the unpromoted palette for use elsewhere.
				freePaletteFromCharacter(character, ClassType.UNPROMOTED);
				if (character.getPromotedPaletteIndex() == 0) {
					allocatePaletteToCharacter(character, ClassType.PROMOTED);
				}
			} else { // Unpromoted-only
				// We can free up the promoted palette for use elsewhere.
				freePaletteFromCharacter(character, ClassType.PROMOTED);
				if (character.getUnpromotedPaletteIndex() == 0) {
					allocatePaletteToCharacter(character, ClassType.UNPROMOTED);
				}
			}
		} else { // Unpromoted-only
			assert character.getPromotedPaletteIndex() == 0 || character.getPromotedPaletteIndex() == character.getUnpromotedPaletteIndex() : "Character has no promotions, yet has a unique promoted palette.";
			character.setPromotedPaletteIndex(0);
			if (targetType == ClassType.PROMOTED) {
				// We can just move the unpromoted palette to the promoted palette.
				if (character.getUnpromotedPaletteIndex() == 0) {
					// If we didn't have an unpromoted palette, just assign one to the promoted.
					allocatePaletteToCharacter(character, ClassType.PROMOTED);
				} else {
					for (GBAFECharacterData linkedChar : linked) {
						linkedChar.setPromotedPaletteIndex(linkedChar.getUnpromotedPaletteIndex());
						linkedChar.setUnpromotedPaletteIndex(0);
					}
				}
			} else { // Unpromoted
				// Need to add a promoted palette.
				allocatePaletteToCharacter(character, ClassType.PROMOTED);
				if (character.getUnpromotedPaletteIndex() == 0) {
					// Get an unpromoted palette if we didn't have one already.
					allocatePaletteToCharacter(character, ClassType.UNPROMOTED);
				}
			}
		}
	}
	
	private void freePaletteFromCharacter(GBAFECharacterData character, ClassType type) {
		// Can't free it if it's shared between both promoted/unpromoted though.
		if (character.getUnpromotedPaletteIndex() == character.getPromotedPaletteIndex()) { return; }
		
		GBAFECharacterData[] linked = charData.linkedCharactersForCharacter(character);
		
		switch (type) {
		case UNPROMOTED:
			recycledPaletteIDs.add(character.getUnpromotedPaletteIndex());
			for (GBAFECharacterData linkedChar : linked) {
				linkedChar.setUnpromotedPaletteIndex(0);
			}
			break;
		case PROMOTED:
			recycledPaletteIDs.add(character.getPromotedPaletteIndex());
			for (GBAFECharacterData linkedChar : linked) {
				linkedChar.setPromotedPaletteIndex(0);
			}
			break;
		case UNPROMOTED_ONLY:
			assert false : "Palettes must be freed from either Unpromoted or Promoted type.";
			return;
		}
		
		if (!waitList.isEmpty()) {
			WaitListItem queuedItem = waitList.get(0);
			waitList.remove(0);
			allocatePaletteToCharacter(queuedItem.character, queuedItem.requiredType);
		}
	}
	
	private void allocatePaletteToCharacter(GBAFECharacterData character, ClassType type) {
		if (character == null || type == null) { return; }
		
		if (recycledPaletteIDs.isEmpty()) {
			waitList.add(new WaitListItem(character, type));
			return;
		}
		
		int paletteID = recycledPaletteIDs.get(0);
		recycledPaletteIDs.remove(0);
		GBAFECharacterData[] linked = charData.linkedCharactersForCharacter(character);
		
		for (GBAFECharacterData linkedChar : linked) {
			switch (type) {
			case PROMOTED:
				linkedChar.setPromotedPaletteIndex(paletteID);
				break;
			case UNPROMOTED:
				linkedChar.setUnpromotedPaletteIndex(paletteID);
				break;
			case UNPROMOTED_ONLY:
				assert false : "Palettes can only be allocated to either Unpromoted or Promoted type.";
				break;
			}
		}
	}

}
