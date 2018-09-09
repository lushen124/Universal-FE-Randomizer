package random;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.FEBase;
import fedata.FEBase.GameType;
import fedata.FECharacter;
import fedata.FEClass;
import fedata.fe6.FE6Data;
import fedata.fe7.FE7Data;
import fedata.fe8.FE8Data;
import fedata.fe8.FE8PaletteMapper;
import fedata.fe8.FE8PaletteMapper.SlotType;
import fedata.fe8.FE8PromotionManager;
import fedata.general.Palette;
import fedata.general.PaletteColor;
import fedata.general.PaletteInfo;
import io.FileHandler;
import random.exc.NotReached;
import util.DebugPrinter;
import util.DiffCompiler;

public class PaletteLoader {
	private FEBase.GameType gameType;
	
	private Map<Integer, Map<Integer, Palette>> characterPalettes = new HashMap<Integer, Map<Integer, Palette>>();
	private Map<Integer, Palette> templates = new HashMap<Integer, Palette>();
	private Map<Integer, Palette> paletteByPaletteID = new HashMap<Integer, Palette>();
	
	// TODO: Put this somewhere else.
	private FE8PaletteMapper fe8Mapper;
	private FE8PromotionManager fe8Promotions;
	
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
		case FE8:
			paletteByPaletteID = new HashMap<Integer, Palette>();
			for (FE8Data.Character character : FE8Data.Character.allPlayableCharacters) {
				int charID = FE8Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, Palette> map = new HashMap<Integer, Palette>();
				characterPalettes.put(charID, map);
				for (PaletteInfo paletteInfo : FE8Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					Palette palette = new Palette(handler, paletteInfo, 40);
					paletteByPaletteID.put(paletteInfo.getPaletteID(), palette);
					map.put(classID, palette);
					PaletteColor[] supplementalHairColor = FE8Data.Palette.supplementaryHairColorForCharacter(charID);
					if (supplementalHairColor != null) {
						palette.assignSupplementalHairColor(supplementalHairColor);
					}
					FE8Data.CharacterClass fe8class = FE8Data.CharacterClass.valueOf(classID);
					FE8Data.Character fe8char = FE8Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Character 0x" + Integer.toHexString(charID) + " (" + fe8char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()) + " (Class: " + Integer.toHexString(classID) + " (" + fe8class.toString() + "))");
				}
			}
			for (FE8Data.Character boss : FE8Data.Character.allBossCharacters) {
				int charID = FE8Data.Character.canonicalIDForCharacterID(boss.ID);
				Map<Integer, Palette> map = new HashMap<Integer, Palette>();
				characterPalettes.put(charID, map);
				for (PaletteInfo paletteInfo : FE8Data.Palette.palettesForCharacter(charID)) {
					Palette palette = new Palette(handler, paletteInfo, 40);
					paletteByPaletteID.put(paletteInfo.getPaletteID(), palette);
					map.put(paletteInfo.getClassID(), palette);
					PaletteColor[] supplementalHairColor = FE8Data.Palette.supplementaryHairColorForCharacter(charID);
					if (supplementalHairColor != null) {
						palette.assignSupplementalHairColor(supplementalHairColor);
					}
					FE8Data.Character fe8char = FE8Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Boss 0x" + Integer.toHexString(charID) + " (" + fe8char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()));
				}
			}
			
			for (FE8Data.CharacterClass characterClass : FE8Data.CharacterClass.allValidClasses) {
				templates.put(characterClass.ID, new Palette(handler, FE8Data.Palette.defaultPaletteForClass(characterClass.ID), 40));
			}
			break;
		default:
			break;
		}
	}
	
	public FE8PaletteMapper setupFE8SpecialManagers(FileHandler handler, FE8PromotionManager promotionManager) {
		assert gameType == GameType.FE8 : "Special setup only needs to be called for FE8.";
		fe8Mapper = new FE8PaletteMapper(handler, promotionManager);
		fe8Promotions = promotionManager;
		return fe8Mapper;
	}
	
	public Palette getPalette(int characterID, int classID) {
		return characterPalettes.get(canonicalCharacterID(characterID)).get(classID);
	}
	
	public Palette[] getAllPalettesForCharacter(int characterID) {
		Collection<Palette> allPalettes = characterPalettes.get(canonicalCharacterID(characterID)).values();
		return allPalettes.toArray(new Palette[allPalettes.size()]);
	}
	
	public int canonicalCharacterID(int characterID) {
		switch (gameType) {
		case FE6:
			return FE6Data.Character.canonicalIDForCharacterID(characterID);
		case FE7:
			return FE7Data.Character.canonicalIDForCharacterID(characterID);
		case FE8:
			return FE8Data.Character.canonicalIDForCharacterID(characterID);
		default:
			return 0;
		}
	}
	
	public void backfillFE8Palettes() {
		assert gameType == GameType.FE8 : "This method is only for FE8.";
		assert fe8Mapper != null : "FE8 requires additional setup before it can adapt palettes.";
		
		int[] characterIDsThatNeedBackfilling = fe8Mapper.getCharactersNeedingAdditionalPalettes();
		for (int characterID : characterIDsThatNeedBackfilling) {
			Palette[] allReferencePalettes = getAllPalettesForCharacter(characterID);
			Map<Integer, Palette> paletteMap = characterPalettes.get(characterID);
			
			Map<SlotType, Integer> recycledIndices = fe8Mapper.requestRecycledPaletteIndicesForCharacter(characterID);
			if (recycledIndices.size() == 0) {
				// We don't have anymore indices to use.
				// Everyone left is just going to have to deal with default palettes. :/
				break;
			}
			
			for (SlotType type : recycledIndices.keySet()) {
				int paletteIndex = recycledIndices.get(type);
				Palette recipientPalette = paletteByPaletteID.get(paletteIndex);
				
				int targetClassID = fe8Mapper.classIDMappedToCharacterForType(characterID, type);
				Palette template = getTemplatePalette(targetClassID);
				
				Palette adapted = new Palette(template, recipientPalette, allReferencePalettes);
				paletteMap.put(targetClassID, adapted);
				
				fe8Mapper.assignRecycledPaletteIndexToCharacterAndClass(paletteIndex, characterID, targetClassID);
			}
		}
	}
	
	public void adaptFE8CharacterToClass(int characterID, int originalClassID, int newClassID, Boolean isBoss) { // This currently assumes lateral movement. i.e. no Unpromoted -> Promoted.
		assert gameType == GameType.FE8 : "This method is only for FE8.";
		assert fe8Mapper != null : "FE8 requires additional setup before it can adapt palettes.";
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapting Character " + FE8Data.Character.valueOf(characterID).toString() + " from class " + FE8Data.CharacterClass.valueOf(originalClassID).toString() + " to " + FE8Data.CharacterClass.valueOf(newClassID).toString());
		
		Boolean originalClassHadPromotions = fe8Promotions.hasPromotions(originalClassID);
		int originalPromotion1 = fe8Promotions.getFirstPromotionOptionClassID(originalClassID);
		int originalPromotion2 = fe8Promotions.getSecondPromotionOptionClassID(originalClassID);
		
		Boolean originalIsTrainee = fe8Promotions.hasPromotions(originalPromotion1) || fe8Promotions.hasPromotions(originalPromotion2);
		
		Boolean newClassHasPromotions = fe8Promotions.hasPromotions(newClassID);
		int newPromotion1 = fe8Promotions.getFirstPromotionOptionClassID(newClassID);
		int newPromotion2 = fe8Promotions.getSecondPromotionOptionClassID(newClassID);
		
		Boolean willBecomeTrainee = fe8Promotions.hasPromotions(newPromotion1) || fe8Promotions.hasPromotions(newPromotion2);
		
		int charID = canonicalCharacterID(characterID);
		Palette[] referencePalettes = getAllPalettesForCharacter(characterID);
		Map<Integer, Palette> paletteMap = characterPalettes.get(charID);
		
		switch (fe8Mapper.getMapTypeForCharacter(charID)) {
		case UNKNOWN:
			NotReached.trigger("Unknown palette type detected.");
			break;
		case TRAINEE:
			assert originalIsTrainee : "Inconsistency detected. Palettes are for a trainee class, but the original class was not a trainee class.";
			
			int oldPromoted1 = fe8Promotions.getFirstPromotionOptionClassID(originalPromotion1);
			int oldPromoted2 = fe8Promotions.getSecondPromotionOptionClassID(originalPromotion1);
			int oldPromoted3 = fe8Promotions.getFirstPromotionOptionClassID(originalPromotion2);
			int oldPromoted4 = fe8Promotions.getSecondPromotionOptionClassID(originalPromotion2);
		
			if (willBecomeTrainee) {
				fe8Mapper.setTraineeClass(newClassID, charID);
				
				int base1 = newPromotion1;
				int base2 = newPromotion2;
				int promoted1 = fe8Promotions.getFirstPromotionOptionClassID(newPromotion1);
				int promoted2 = fe8Promotions.getSecondPromotionOptionClassID(newPromotion1);
				int promoted3 = fe8Promotions.getFirstPromotionOptionClassID(newPromotion2);
				int promoted4 = fe8Promotions.getSecondPromotionOptionClassID(newPromotion2);
				
				// Adapt every palette over as is.
				Palette adaptedTrainee = paletteForAdaptingCharacterToClass(charID, originalClassID, newClassID, referencePalettes);
				Palette adaptedBase1 = paletteForAdaptingCharacterToClass(charID, originalPromotion1, base1, referencePalettes);
				Palette adaptedBase2 = paletteForAdaptingCharacterToClass(charID, originalPromotion2, base2, referencePalettes);
				Palette adaptedPromoted1 = paletteForAdaptingCharacterToClass(charID, oldPromoted1, promoted1, referencePalettes);
				Palette adaptedPromoted2 = paletteForAdaptingCharacterToClass(charID, oldPromoted2, promoted2, referencePalettes);
				Palette adaptedPromoted3 = paletteForAdaptingCharacterToClass(charID, oldPromoted3, promoted3, referencePalettes);
				Palette adaptedPromoted4 = paletteForAdaptingCharacterToClass(charID, oldPromoted4, promoted4, referencePalettes);
				
				paletteMap.clear();
				if (adaptedTrainee != null) { paletteMap.put(newClassID, adaptedTrainee); }
				if (adaptedBase1 != null) { paletteMap.put(base1, adaptedBase1); }
				if (adaptedBase2 != null) { paletteMap.put(base2, adaptedBase2); }
				if (adaptedPromoted1 != null) { paletteMap.put(promoted1, adaptedPromoted1); }
				if (adaptedPromoted2 != null) { paletteMap.put(promoted2, adaptedPromoted2); }
				if (adaptedPromoted3 != null) { paletteMap.put(promoted3, adaptedPromoted3); }
				if (adaptedPromoted4 != null) { paletteMap.put(promoted4, adaptedPromoted4); }
			} else {
				fe8Mapper.setUnpromotedClass(newClassID, charID, !isBoss);
				if (newClassHasPromotions && !isBoss) {
					int promoted1 = newPromotion1;
					int promoted2 = newPromotion2;
					Palette adaptedBase = paletteForAdaptingCharacterToClass(charID, originalPromotion1, newClassID, referencePalettes);
					Palette adaptedPromotion1 = paletteForAdaptingCharacterToClass(charID, oldPromoted1, promoted1, referencePalettes);
					
					paletteMap.clear();
					
					if (promoted2 != 0) {
						Palette adaptedPromotion2 = paletteForAdaptingCharacterToClass(charID, oldPromoted2, promoted2, referencePalettes);
						if (adaptedPromotion2 != null) { paletteMap.put(promoted2, adaptedPromotion2); }
					}
					
					if (adaptedBase != null) { paletteMap.put(newClassID, adaptedBase); }
					if (adaptedPromotion1 != null) { paletteMap.put(promoted1, adaptedPromotion1); }
					
				} else {
					Palette adaptedBase = paletteForAdaptingCharacterToClass(charID, originalPromotion1, newClassID, referencePalettes);
					paletteMap.clear();
					if (adaptedBase != null) {
						paletteMap.put(newClassID, adaptedBase);
					} else {
						System.err.println("Failed to adapt palette.");
					}
				}
			}
			break;
		case UNPROMOTED:
			fe8Mapper.setUnpromotedClass(newClassID, charID, !isBoss);
			if (originalClassHadPromotions) {
				Palette adaptedBase = paletteForAdaptingCharacterToClass(charID, originalClassID, newClassID, referencePalettes);
				
				paletteMap.clear();
				if (newClassHasPromotions && !isBoss) {
					Palette adaptedPromotion1 = paletteForAdaptingCharacterToClass(charID, originalPromotion1, newPromotion1, referencePalettes);
					if (newPromotion2 != 0) {
						Palette adaptedPromotion2 = paletteForAdaptingCharacterToClass(charID, originalPromotion2, newPromotion2, referencePalettes);
						if (adaptedPromotion2 != null) { paletteMap.put(newPromotion2, adaptedPromotion2); }
					}
					
					if (adaptedPromotion1 != null) { paletteMap.put(newPromotion1, adaptedPromotion1); }
				}
				
				if (adaptedBase != null) {
					paletteMap.put(newClassID, adaptedBase);
				} else {
					System.err.println("Failed to adapt palette.");
				}
			} else {
				// This is Tethys's case. she won't have enough palettes to work with because she only starts with 1, and if she needs 3 because of branching paths, then we're screwed.
				// TODO: Handle this case. We can probably steal some palettes from anybody that became a dancer, but that involves the palette assignment table, which we're not using yet.
				// Not to mention, if we allow more than lateral movement, then we're really screwed here.
				Palette adaptedBase = paletteForAdaptingCharacterToClass(charID, originalClassID, newClassID, referencePalettes);
				if (adaptedBase != null) {
					paletteMap.put(newClassID, adaptedBase);
					paletteMap.remove(originalClassID);
				} else {
					System.err.println("Failed to adapt palette.");
				}
			}
			break;
		case PROMOTED:
			fe8Mapper.setPromotedClass(newClassID, charID);
			Palette adaptedPromotion = paletteForAdaptingCharacterToClass(charID, originalClassID, newClassID, referencePalettes);
			if (adaptedPromotion != null) {
				paletteMap.put(newClassID, adaptedPromotion);
				paletteMap.remove(originalClassID);
			} else {
				System.err.println("Failed to adapt palette.");
			}
			break;
		}
	}
	
	private Palette paletteForAdaptingCharacterToClass(int charID, int originalClassID, int newClassID, Palette[] referencePalettes) {
		Palette originalPalette = getPalette(charID, originalClassID);
		Palette template = getTemplatePalette(newClassID);
		
		if (originalPalette == null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "No palette found for character 0x" + Integer.toHexString(charID));
			return null;
		}
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Original palette offset: " + Long.toHexString(originalPalette.getInfo().getOffset()));
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Template palette offset: " + Long.toHexString(template.getInfo().getOffset()));
		
		Palette adaptedPalette = new Palette(template, originalPalette, referencePalettes);
		return adaptedPalette;
	}
	
	public void adaptCharacterToClass(int characterID, int originalClassID, int originalPromotedClassID, int newClassID, int newPromotedClassID) {
		assert gameType != GameType.FE8 : "FE8 should use a special method to adapt character palettes.";
		
		int charID = canonicalCharacterID(characterID);
		
		switch (gameType) {
		case FE6: {
			FE6Data.Character character = FE6Data.Character.valueOf(charID);
			FE6Data.CharacterClass oldClass = FE6Data.CharacterClass.valueOf(originalClassID);
			FE6Data.CharacterClass newClass = FE6Data.CharacterClass.valueOf(newClassID);
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapting character 0x" + Integer.toHexString(charID) + " (" + character.toString() + ") " + 
			" from class 0x" + Integer.toHexString(originalClassID) + "(" + oldClass.toString() + ")" + " to 0x" + Integer.toHexString(newClassID) + " (" + newClass.toString() + ")");
			break;
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
			
			break;
		}
		default:
			break;
		}
		
		Palette adaptedPalette = paletteForAdaptingCharacterToClass(charID, originalClassID, newClassID, getAllPalettesForCharacter(charID));
		Map<Integer, Palette> paletteMap = characterPalettes.get(charID);
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapted palette offset: " + Long.toHexString(adaptedPalette.getInfo().getOffset()));
		
		Palette adaptedPromotedPalette = paletteForAdaptingCharacterToClass(charID, originalPromotedClassID, newPromotedClassID, getAllPalettesForCharacter(charID));
		if (adaptedPromotedPalette != null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapted palette offset: " + Long.toHexString(adaptedPalette.getInfo().getOffset()));
			paletteMap.put(newPromotedClassID, adaptedPromotedPalette);
			paletteMap.remove(originalPromotedClassID);
		}
		if (adaptedPalette != null) {
			paletteMap.put(newClassID, adaptedPalette);
			paletteMap.remove(originalClassID);
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
		case FE8:
			return templates.get(classID);
		default:
			return null;
		}
	}
}
