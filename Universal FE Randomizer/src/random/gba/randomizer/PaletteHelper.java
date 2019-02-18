package random.gba.randomizer;

import java.util.Map;

import fedata.gba.GBAFECharacterData;
import fedata.general.FEBase.GameType;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.PaletteLoader;
import util.FreeSpaceManager;

public class PaletteHelper {
	
	public static void synchronizePalettes(GameType type, CharacterDataLoader charData, ClassDataLoader classData, PaletteLoader paletteData, Map<GBAFECharacterData, GBAFECharacterData> slotToReference, FreeSpaceManager freeSpace) {
		for (GBAFECharacterData playableCharacter : charData.canonicalPlayableCharacters()) {
			int characterID = playableCharacter.getID();
			int classID = playableCharacter.getClassID();
			
			if (charData.canChangeCharacterID(characterID) == false) { continue; }
			
			if (type == GameType.FE8) {
				if (slotToReference != null && slotToReference.get(playableCharacter) != null) {
					paletteData.adaptFE8CharacterToClass(characterID, slotToReference.get(playableCharacter).getID(), classID, false);
				} else {
					paletteData.adaptFE8CharacterToClass(characterID, classID, false);
				}
			} else {
				if (slotToReference != null && slotToReference.get(playableCharacter) != null) {
					paletteData.enqueueChange(playableCharacter, slotToReference.get(playableCharacter), charData, classData, classID, true);
				} else {
					paletteData.enqueueChange(playableCharacter, playableCharacter, charData, classData, classID, true);
				}
			}
		}
		
		for (GBAFECharacterData bossCharacter : charData.bossCharacters()) {
			int characterID = bossCharacter.getID();
			int classID = bossCharacter.getClassID();
			
			if (type == GameType.FE8) {
				paletteData.adaptFE8CharacterToClass(characterID, classID, true);
			} else {
				paletteData.enqueueChange(bossCharacter, bossCharacter, charData, classData, classID, false);
			}
		}
		
		paletteData.flushChangeQueue(charData, freeSpace);
	}

}
