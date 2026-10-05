package random.gba.randomizer;

import java.util.Map;

import fedata.gba.GBAFECharacterData;
import fedata.gba.fe7.FE7Data;
import fedata.gba.general.PaletteInfo;
import fedata.gba.general.PaletteV2;
import fedata.gba.general.PaletteV2.PaletteType;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.PaletteLoader;
import util.Diff;
import util.DiffCompiler;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;

public class PaletteHelper {
	
	public static void synchronizePalettes(GameType type, boolean includeExtras, CharacterDataLoader charData, ClassDataLoader classData, PaletteLoader paletteData, Map<GBAFECharacterData, GBAFECharacterData> slotToReference, FreeSpaceManager freeSpace) {
		for (GBAFECharacterData playableCharacter : charData.canonicalPlayableCharacters(includeExtras)) {
			int classID = playableCharacter.getClassID();
			
			if (charData.canChangeCharacterID(playableCharacter.getID()) == false) { continue; }
			
			if (type == GameType.FE8) {
				if (slotToReference != null && slotToReference.get(playableCharacter) != null) {
					paletteData.adaptFE8CharacterToClass(playableCharacter, slotToReference.get(playableCharacter), classID, false);
				} else {
					paletteData.adaptFE8CharacterToClass(playableCharacter, classID, false);
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
			int classID = bossCharacter.getClassID();
			
			if (type == GameType.FE8) {
				paletteData.adaptFE8CharacterToClass(bossCharacter, classID, true);
			} else {
				paletteData.enqueueChange(bossCharacter, bossCharacter, charData, classData, classID, false);
			}
		}
		
		paletteData.flushChangeQueue(charData, freeSpace);
	}
	
	public static void applyCharacterPaletteToSprite(GameType type, FileHandler handler, GBAFECharacterData character, int classID, PaletteLoader paletteData, FreeSpaceManager freeSpace, DiffCompiler compiler) {
		assert type == GameType.FE7 : "This method is only useful for FE7 at the moment.";
		if (type != GameType.FE7) { return; }
		
		// TODO: Maybe this needs to be its own object...
		int characterID = character.getID();
		
		PaletteV2 adaptedPalette = paletteData.generatePalette(classID, characterID, PaletteType.PLAYER, FE7Data.Palette.supplementaryHairColorForCharacter(characterID));
		if (character.hasBattlePaletteOverrides()) {
			adaptedPalette = paletteData.generatePalette(classID, 
					character.overrideBattleHairColor, character.overrideBattlePrimaryColor, 
					character.overrideBattleSecondaryColor, character.overrideBattleTertiaryColor);
		}
		byte[] compressed = adaptedPalette.getCompressedData();

		// Need to repoint the old palette address to another one.
		long freeAddress = freeSpace.reserveSpace(compressed.length, "Sprite Palette for Class 0x" + Integer.toHexString(classID), true);
		
		int animationID = FE7Data.CharacterClass.valueOf(classID).animationID();
		assert animationID < FE7Data.AnimationPointerTableCount : "Animation ID out of bounds.";
		
		long entryOffset = FE7Data.AnimationPointerTableOffset + animationID * FE7Data.AnimationPointerTableEntrySize;
		// The palette is stored on byte 28.
		compiler.addDiff(new Diff(entryOffset + 28, 4, WhyDoesJavaNotHaveThese.bytesFromAddress(freeAddress), null));
		
		adaptedPalette.overrideOffset(freeAddress);
		adaptedPalette.forceCommit(compiler);
	}

}
