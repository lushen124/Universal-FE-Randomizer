package random;

import java.util.HashMap;
import java.util.Map;

import fedata.FEBase;
import fedata.fe6.FE6Data;
import fedata.fe7.FE7Data;
import fedata.general.Palette;
import fedata.general.PaletteColor;
import fedata.general.PaletteInfo;
import io.FileHandler;
import util.DebugPrinter;
import util.DiffCompiler;

public class PaletteLoader {
	private FEBase.GameType gameType;
	
	private Map<Integer, Map<Integer, Palette>> characterPalettes = new HashMap<Integer, Map<Integer, Palette>>();
	private Map<Integer, Palette> templates = new HashMap<Integer, Palette>();
	
	public PaletteLoader(FEBase.GameType gameType, FileHandler handler) {
		this.gameType = gameType;
		
		switch (gameType) {
		case FE6:
			for (FE6Data.Character character : FE6Data.Character.allPlayableCharacters) {
				int charID = FE6Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, Palette> map = new HashMap<Integer, Palette>();
				characterPalettes.put(charID, map);
				for (PaletteInfo paletteInfo : FE6Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					Palette palette = new Palette(handler, paletteInfo, 40);
					map.put(classID, palette);
					PaletteColor[] supplementalHairColor = FE6Data.Palette.supplementaryHairColorForCharacter(charID);
					if (supplementalHairColor != null) {
						palette.assignSupplementalHairColor(supplementalHairColor);
					}
					FE6Data.CharacterClass fe6class = FE6Data.CharacterClass.valueOf(classID);
					FE6Data.Character fe6char = FE6Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Character 0x" + Integer.toHexString(charID) + " (" + fe6char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()) + " (Class: " + Integer.toHexString(classID) + " (" + fe6class.toString() + "))");
				}
			}
			for (FE6Data.Character boss : FE6Data.Character.allBossCharacters) {
				int charID = FE6Data.Character.canonicalIDForCharacterID(boss.ID);
				Map<Integer, Palette> map = new HashMap<Integer, Palette>();
				characterPalettes.put(charID, map);
				for (PaletteInfo paletteInfo : FE6Data.Palette.palettesForCharacter(charID)) {
					Palette palette = new Palette(handler, paletteInfo, 40);
					map.put(paletteInfo.getClassID(), palette);
					PaletteColor[] supplementalHairColor = FE6Data.Palette.supplementaryHairColorForCharacter(charID);
					if (supplementalHairColor != null) {
						palette.assignSupplementalHairColor(supplementalHairColor);
					}
					FE6Data.Character fe6char = FE6Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Boss 0x" + Integer.toHexString(charID) + " (" + fe6char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()));
				}
			}
			
			for (FE6Data.CharacterClass characterClass : FE6Data.CharacterClass.allValidClasses) {
				templates.put(characterClass.ID, new Palette(handler, FE6Data.Palette.defaultPaletteForClass(characterClass.ID), 40));
			}
			break;
		case FE7:
			for (FE7Data.Character character : FE7Data.Character.allPlayableCharacters) {
				int charID = FE7Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, Palette> map = new HashMap<Integer, Palette>();
				characterPalettes.put(charID, map);
				for (PaletteInfo paletteInfo : FE7Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					Palette palette = new Palette(handler, paletteInfo, 40);
					map.put(classID, palette);
					PaletteColor[] supplementalHairColor = FE7Data.Palette.supplementaryHairColorForCharacter(charID);
					if (supplementalHairColor != null) {
						palette.assignSupplementalHairColor(supplementalHairColor);
					}
					FE7Data.CharacterClass fe7class = FE7Data.CharacterClass.valueOf(classID);
					FE7Data.Character fe7char = FE7Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Character 0x" + Integer.toHexString(charID) + " (" + fe7char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()) + " (Class: " + Integer.toHexString(classID) + " (" + fe7class.toString() + "))");
				}
			}
			for (FE7Data.Character boss : FE7Data.Character.allBossCharacters) {
				int charID = FE7Data.Character.canonicalIDForCharacterID(boss.ID);
				Map<Integer, Palette> map = new HashMap<Integer, Palette>();
				characterPalettes.put(charID, map);
				for (PaletteInfo paletteInfo : FE7Data.Palette.palettesForCharacter(charID)) {
					Palette palette = new Palette(handler, paletteInfo, 40);
					map.put(paletteInfo.getClassID(), palette);
					PaletteColor[] supplementalHairColor = FE7Data.Palette.supplementaryHairColorForCharacter(charID);
					if (supplementalHairColor != null) {
						palette.assignSupplementalHairColor(supplementalHairColor);
					}
					FE7Data.Character fe7char = FE7Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Boss 0x" + Integer.toHexString(charID) + " (" + fe7char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()));
				}
			}
			
			for (FE7Data.CharacterClass characterClass : FE7Data.CharacterClass.allValidClasses) {
				templates.put(characterClass.ID, new Palette(handler, FE7Data.Palette.defaultPaletteForClass(characterClass.ID), 40));
			}
			break;
		default:
			break;
		}
	}
	
	public Palette getPalette(int characterID, int classID) {
		switch (gameType) {
		case FE6:
			return characterPalettes.get(FE6Data.Character.canonicalIDForCharacterID(characterID)).get(classID);
		case FE7:
			return characterPalettes.get(FE7Data.Character.canonicalIDForCharacterID(characterID)).get(classID);
		default:
			return null;
		}
	}
	
	public int canonicalCharacterID(int characterID) {
		switch (gameType) {
		case FE6:
			return FE6Data.Character.canonicalIDForCharacterID(characterID);
		case FE7:
			return FE7Data.Character.canonicalIDForCharacterID(characterID);
		default:
			return 0;
		}
	}
	
	public void adaptCharacterToClass(int characterID, int originalClassID, int originalPromotedClassID, int newClassID, int newPromotedClassID) {
		int charID = canonicalCharacterID(characterID);
		switch (gameType) {
		case FE6: {
			FE6Data.Character character = FE6Data.Character.valueOf(charID);
			FE6Data.CharacterClass oldClass = FE6Data.CharacterClass.valueOf(originalClassID);
			FE6Data.CharacterClass newClass = FE6Data.CharacterClass.valueOf(newClassID);
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapting character 0x" + Integer.toHexString(charID) + " (" + character.toString() + ") " + 
			" from class 0x" + Integer.toHexString(originalClassID) + "(" + oldClass.toString() + ")" + " to 0x" + Integer.toHexString(newClassID) + " (" + newClass.toString() + ")");
		}
		case FE7: {
			FE7Data.Character character = FE7Data.Character.valueOf(charID);
			FE7Data.CharacterClass oldClass = FE7Data.CharacterClass.valueOf(originalClassID);
			FE7Data.CharacterClass newClass = FE7Data.CharacterClass.valueOf(newClassID);
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapting character 0x" + Integer.toHexString(charID) + " (" + character.toString() + ") " + 
			" from class 0x" + Integer.toHexString(originalClassID) + "(" + oldClass.toString() + ")" + " to 0x" + Integer.toHexString(newClassID) + " (" + newClass.toString() + ")");
			
			if (newClass.ID == FE7Data.CharacterClass.BARD.ID || newClass.ID == FE7Data.CharacterClass.DANCER.ID) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Debugging dancer/bard palettes");
			}
		}
		default:
			break;
		}
		
		Palette originalPalette = getPalette(charID, originalClassID);
		Palette originalPromotedPalette = getPalette(charID, originalPromotedClassID);
		Palette template = getTemplatePalette(newClassID);
		Palette promotedTemplate = getTemplatePalette(newPromotedClassID);
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Original palette offset: " + Long.toHexString(originalPalette.getInfo().getOffset()));
		if (originalPromotedPalette != null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Original promoted palette offset: " + Long.toHexString(originalPromotedPalette.getInfo().getOffset()));
		} else {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "No Promoted Palette.");
		}
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Template palette offset: " + Long.toHexString(template.getInfo().getOffset()));
		if (promotedTemplate != null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Promoted Template palette offset: " + Long.toHexString(promotedTemplate.getInfo().getOffset()));
		} else {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "No promoted template.");
		}
		
		
		Palette adaptedPalette = new Palette(template, originalPalette, originalPromotedPalette);
		Map<Integer, Palette> paletteMap = characterPalettes.get(charID);
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapted palette offset: " + Long.toHexString(adaptedPalette.getInfo().getOffset()));
		
		paletteMap.put(newClassID, adaptedPalette);
		paletteMap.remove(originalClassID);
		
		if (originalPromotedPalette != null && promotedTemplate != null) {
			Palette adaptedPromotedPalette = new Palette(promotedTemplate, originalPromotedPalette, originalPalette);
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapted palette offset: " + Long.toHexString(adaptedPalette.getInfo().getOffset()));
			paletteMap.put(newPromotedClassID, adaptedPromotedPalette);
			paletteMap.remove(originalPromotedClassID);
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (Map<Integer, Palette> map : characterPalettes.values()) {
			for (Palette palette : map.values()) {
				palette.commitPalette(compiler);
			}
		}
	}
	
	public Palette getTemplatePalette(int classID) {
		switch (gameType) {
		case FE6:
		case FE7:
			return templates.get(classID);
		default:
			return null;
		}
	}
}
