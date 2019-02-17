package random.gba.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fedata.gba.GBAFECharacterData;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.fe8.FE8PaletteMapper;
import fedata.gba.fe8.FE8PromotionManager;
import fedata.gba.fe8.FE8PaletteMapper.SlotType;
import fedata.gba.general.Palette;
import fedata.gba.general.PaletteColor;
import fedata.gba.general.PaletteInfo;
import fedata.gba.general.PaletteV2;
import fedata.gba.general.PaletteV2.PaletteType;
import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;

public class PaletteLoader {
	private FEBase.GameType gameType;
	
	private Map<Integer, Map<Integer, Palette>> characterPalettes = new HashMap<Integer, Map<Integer, Palette>>();
	private Map<Integer, Palette> templates = new HashMap<Integer, Palette>();
	private Map<Integer, Palette> paletteByPaletteID = new HashMap<Integer, Palette>();
	
	// CharacterID -> (ClassID, Palette)
	private Map<Integer, Map<Integer, Palette>> referencePalettes = new HashMap<Integer, Map<Integer, Palette>>();
	
	// V2
	private Map<Integer, Map<Integer, PaletteV2>> characterPalettesV2 = new HashMap<Integer, Map<Integer, PaletteV2>>();
	private Map<Integer, PaletteV2> templatesV2 = new HashMap<Integer, PaletteV2>();
	private Map<Integer, PaletteV2> paletteByPaletteIDV2 = new HashMap<Integer, PaletteV2>();
	
	private Map<Integer, PaletteV2> newPalettesV2 = new HashMap<Integer, PaletteV2>();
	private Map<Integer, PaletteV2> appendedPaletteIDsV2 = new HashMap<Integer, PaletteV2>();
	
	// CharacterID -> (ClassID, Palette)
	private Map<Integer, Map<Integer, PaletteV2>> referencePalettesV2 = new HashMap<Integer, Map<Integer, PaletteV2>>();
	
	// TODO: Put this somewhere else.
	private PaletteMapper mapper;
	
	private FE8PaletteMapper fe8Mapper;
	private FE8PromotionManager fe8Promotions;
	
	private List<Integer> emptyPaletteIDs = new ArrayList<Integer>();
	private Map<Integer, Palette> appendedPaletteIDs = new HashMap<Integer, Palette>();
	
	public PaletteLoader(FEBase.GameType gameType, FileHandler handler, CharacterDataLoader charData, ClassDataLoader classData) {
		this.gameType = gameType;
		
		switch (gameType) {
		case FE6:
			for (FE6Data.Character character : FE6Data.Character.allPlayableCharacters) {
				int charID = FE6Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, Palette> map = new HashMap<Integer, Palette>();
				characterPalettes.put(charID, map);
				for (PaletteInfo paletteInfo : FE6Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					Palette palette = new Palette(handler, paletteInfo, 40, null);
					paletteByPaletteID.put(paletteInfo.getPaletteID(), palette);
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
					Palette palette = new Palette(handler, paletteInfo, 40, null);
					paletteByPaletteID.put(paletteInfo.getPaletteID(), palette);
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
				templates.put(characterClass.ID, new Palette(handler, FE6Data.Palette.defaultPaletteForClass(characterClass.ID), 40, null));
			}
			
			for (int i = FE6Data.Palette.maxUsedPaletteIndex() + 1; i < FE6Data.Palette.maxPaletteIndex(); i++) {
				emptyPaletteIDs.add(i);
			}
			
			mapper = new PaletteMapper(charData, classData);
			break;
		case FE7:
			for (FE7Data.Character character : FE7Data.Character.allPlayableCharacters) {
				int charID = FE7Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, Palette> map = new HashMap<Integer, Palette>();
				characterPalettes.put(charID, map);
				for (PaletteInfo paletteInfo : FE7Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					Palette palette = new Palette(handler, paletteInfo, 40, null);
					paletteByPaletteID.put(paletteInfo.getPaletteID(), palette);
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
					Palette palette = new Palette(handler, paletteInfo, 40, null);
					paletteByPaletteID.put(paletteInfo.getPaletteID(), palette);
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
				templates.put(characterClass.ID, new Palette(handler, FE7Data.Palette.defaultPaletteForClass(characterClass.ID), 40, null));
			}
			
			for (int i = FE7Data.Palette.maxUsedPaletteIndex() + 1; i < FE7Data.Palette.maxPaletteIndex(); i++) {
				emptyPaletteIDs.add(i);
			}
			
			mapper = new PaletteMapper(charData, classData);
			break;
		case FE8:
			paletteByPaletteID = new HashMap<Integer, Palette>();
			for (FE8Data.Character character : FE8Data.Character.allPlayableCharacters) {
				int charID = FE8Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, PaletteV2> map = new HashMap<Integer, PaletteV2>();
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				characterPalettesV2.put(charID, map);
				for (PaletteInfo paletteInfo : FE8Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
					map.put(classID, palette);
					referenceMap.put(classID, new PaletteV2(handler, paletteInfo));
					FE8Data.CharacterClass fe8class = FE8Data.CharacterClass.valueOf(classID);
					FE8Data.Character fe8char = FE8Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Character 0x" + Integer.toHexString(charID) + " (" + fe8char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()) + " (Class: " + Integer.toHexString(classID) + " (" + fe8class.toString() + "))");
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Palette size: " + Integer.toString(palette.getOriginalCompressedLength()) + " bytes");
				}
			}
			for (FE8Data.Character boss : FE8Data.Character.allBossCharacters) {
				int charID = FE8Data.Character.canonicalIDForCharacterID(boss.ID);
				Map<Integer, PaletteV2> map = new HashMap<Integer, PaletteV2>();
				characterPalettesV2.put(charID, map);
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				for (PaletteInfo paletteInfo : FE8Data.Palette.palettesForCharacter(charID)) {
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
					map.put(paletteInfo.getClassID(), palette);
					referenceMap.put(paletteInfo.getClassID(), new PaletteV2(handler, paletteInfo));
					FE8Data.Character fe8char = FE8Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Boss 0x" + Integer.toHexString(charID) + " (" + fe8char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()));
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Palette size: " + Integer.toString(palette.getOriginalCompressedLength()) + " bytes");
				}
			}
			
			for (FE8Data.CharacterClass characterClass : FE8Data.CharacterClass.allValidClasses) {
				PaletteV2 classPalette = new PaletteV2(handler, FE8Data.Palette.defaultPaletteForClass(characterClass.ID));
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Registering palette for class " + characterClass.toString() + " (" + classPalette.getOriginalCompressedLength() + " bytes)");
				DebugPrinter.log(DebugPrinter.Key.PALETTE, WhyDoesJavaNotHaveThese.displayStringForBytes(classPalette.getCompressedData()));
				templatesV2.put(characterClass.ID, classPalette);
			}
			// Trainee classes aren't technically valid classes in other contexts, but here they are.
			for (FE8Data.CharacterClass characterClass : FE8Data.CharacterClass.allTraineeClasses) {
				PaletteV2 classPalette = new PaletteV2(handler, FE8Data.Palette.defaultPaletteForClass(characterClass.ID));
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Registering palette for class " + characterClass.toString() + " (" + classPalette.getOriginalCompressedLength() + " bytes)");
				DebugPrinter.log(DebugPrinter.Key.PALETTE, WhyDoesJavaNotHaveThese.displayStringForBytes(classPalette.getCompressedData()));
				templatesV2.put(characterClass.ID, classPalette);
			}
			
			for (int i = FE8Data.Palette.maxUsedPaletteIndex() + 1; i < FE8Data.Palette.maxPaletteIndex(); i++) {
				emptyPaletteIDs.add(i);
			}
			
			return;
		default:
			break;
		}
		
		for (Integer characterID : characterPalettes.keySet()) {
			Map<Integer, Palette> paletteMap = characterPalettes.get(characterID);
			Map<Integer, Palette> copy = new HashMap<Integer, Palette>();
			for (Integer classID : paletteMap.keySet()) {
				copy.put(classID, new Palette(paletteMap.get(classID)));
			}
			referencePalettes.put(characterID, copy);
		}
	}
	
	public FE8PaletteMapper setupFE8SpecialManagers(FileHandler handler, FE8PromotionManager promotionManager) {
		assert gameType == GameType.FE8 : "Special setup only needs to be called for FE8.";
		// FE8PaletteMapper will handle the empty palettes for us. All we need to be prepared for is the chance of a palette ID not existing yet.
		fe8Mapper = new FE8PaletteMapper(handler, promotionManager, emptyPaletteIDs);
		fe8Promotions = promotionManager;
		// We need to tell it about lengths too, in case we try to use a palette that won't fit the old slot.
		for (int paletteID : paletteByPaletteIDV2.keySet()) {
			fe8Mapper.registerPaletteID(paletteID, paletteByPaletteIDV2.get(paletteID).getOriginalCompressedLength(), paletteByPaletteIDV2.get(paletteID).getDestinationOffset());
		}
		return fe8Mapper;
	}
	
	public Palette getPalette(int characterID, int classID) {
		return characterPalettes.get(canonicalCharacterID(characterID)).get(classID);
	}
	
	public Palette[] getAllPalettesForCharacter(int characterID) {
		Collection<Palette> allPalettes = characterPalettes.get(canonicalCharacterID(characterID)).values();
		return allPalettes.toArray(new Palette[allPalettes.size()]);
	}
	
	public Palette[] getReferencePalettesForCharacter(int characterID) {
		Collection<Palette> allPalettes = referencePalettes.get(canonicalCharacterID(characterID)).values();
		return allPalettes.toArray(new Palette[allPalettes.size()]);
	}
	
	public PaletteV2[] getV2ReferencePalettesForCharacter(int characterID) {
		Collection<PaletteV2> allPalettes = referencePalettesV2.get(canonicalCharacterID(characterID)).values();
		return allPalettes.toArray(new PaletteV2[allPalettes.size()]);
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
			Map<Integer, PaletteV2> paletteMap = characterPalettesV2.get(characterID);
			
			Map<SlotType, Integer> recycledIndices = fe8Mapper.requestRecycledPaletteIndicesForCharacter(characterID);
			if (recycledIndices.size() == 0) {
				// We don't have anymore indices to use.
				// Everyone left is just going to have to deal with default palettes. :/
				break;
			}
			
			for (SlotType type : recycledIndices.keySet()) {
				int paletteIndex = recycledIndices.get(type);
				PaletteV2 adaptedPalette = paletteMap.get(fe8Mapper.classIDMappedToCharacterForType(characterID, type)); 
				PaletteV2 recipientPalette = paletteByPaletteIDV2.get(paletteIndex);
				
				if (recipientPalette != null) {
					adaptedPalette.overrideOffset(recipientPalette.getDestinationOffset());
				} else if (paletteIndex != 0) {
					newPalettesV2.put(paletteIndex, adaptedPalette);
				}
				
				fe8Mapper.registerPaletteID(paletteIndex, adaptedPalette.getCompressedData().length, recipientPalette.getDestinationOffset());
			}
		}
	}
	
	public void adaptFE8CharacterToClass(int characterID, int newClassID, Boolean isBoss) {
		adaptFE8CharacterToClass(characterID, characterID, newClassID, isBoss);
	}
	
	public void adaptFE8CharacterToClass(int characterID, int referenceID, int newClassID, Boolean isBoss) {
		assert gameType == GameType.FE8 : "This method is only for FE8.";
		assert fe8Mapper != null : "FE8 requires additional setup before it can adapt palettes.";
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapting Character " + FE8Data.Character.valueOf(characterID).toString() + " to class " + FE8Data.CharacterClass.valueOf(newClassID).toString() + " using Reference " + FE8Data.Character.valueOf(referenceID).toString());
		
		Boolean newClassHasPromotions = fe8Promotions.hasPromotions(newClassID);
		int newPromotion1 = fe8Promotions.getFirstPromotionOptionClassID(newClassID);
		int newPromotion2 = fe8Promotions.getSecondPromotionOptionClassID(newClassID);
		
		Boolean willBecomeTrainee = fe8Promotions.hasPromotions(newPromotion1) || fe8Promotions.hasPromotions(newPromotion2);
		Boolean newClassIsPromoted = FE8Data.CharacterClass.valueOf(newClassID).isPromoted();
		
		int charID = canonicalCharacterID(characterID);
		PaletteV2[] referencePalettes = getV2ReferencePalettesForCharacter(referenceID);
		Map<Integer, PaletteV2> paletteMap = characterPalettesV2.get(charID);
		
		PaletteColor[] supplementalHair = FE8Data.Palette.supplementaryHairColorForCharacter(referenceID);
		
		if (willBecomeTrainee) {
			int base1 = newPromotion1;
			int base2 = newPromotion2;
			int promoted1 = fe8Promotions.getFirstPromotionOptionClassID(newPromotion1);
			int promoted2 = fe8Promotions.getSecondPromotionOptionClassID(newPromotion1);
			int promoted3 = fe8Promotions.getFirstPromotionOptionClassID(newPromotion2);
			int promoted4 = fe8Promotions.getSecondPromotionOptionClassID(newPromotion2);
			
			// Adapt every palette over as is.
			PaletteV2 adaptedTrainee = v2PaletteForClass(newClassID, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
			PaletteV2 adaptedBase1 = v2PaletteForClass(base1, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
			PaletteV2 adaptedBase2 = v2PaletteForClass(base2, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
			PaletteV2 adaptedPromoted1 = v2PaletteForClass(promoted1, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
			PaletteV2 adaptedPromoted2 = v2PaletteForClass(promoted2, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
			PaletteV2 adaptedPromoted3 = v2PaletteForClass(promoted3, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
			PaletteV2 adaptedPromoted4 = v2PaletteForClass(promoted4, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
			
			fe8Mapper.setTraineeClass(newClassID, charID,
					adaptedTrainee.getCompressedData().length,
					adaptedBase1.getCompressedData().length,
					adaptedBase2.getCompressedData().length,
					adaptedPromoted1.getCompressedData().length,
					adaptedPromoted2.getCompressedData().length,
					adaptedPromoted3.getCompressedData().length,
					adaptedPromoted4.getCompressedData().length);
			
			integrateFE8PaletteIfPossible(charID, adaptedTrainee, SlotType.TRAINEE);
			integrateFE8PaletteIfPossible(charID, adaptedBase1, SlotType.PRIMARY_BASE);
			integrateFE8PaletteIfPossible(charID, adaptedBase2, SlotType.SECONDARY_BASE);
			integrateFE8PaletteIfPossible(charID, adaptedPromoted1, SlotType.FIRST_PROMOTION);
			integrateFE8PaletteIfPossible(charID, adaptedPromoted2, SlotType.SECOND_PROMOTION);
			integrateFE8PaletteIfPossible(charID, adaptedPromoted3, SlotType.THIRD_PROMOTION);
			integrateFE8PaletteIfPossible(charID, adaptedPromoted4, SlotType.FOURTH_PROMOTION);
			
			paletteMap.clear();
			if (adaptedTrainee != null) { paletteMap.put(newClassID, adaptedTrainee); }
			if (adaptedBase1 != null) { paletteMap.put(base1, adaptedBase1); }
			if (adaptedBase2 != null) { paletteMap.put(base2, adaptedBase2); }
			if (adaptedPromoted1 != null) { paletteMap.put(promoted1, adaptedPromoted1); }
			if (adaptedPromoted2 != null) { paletteMap.put(promoted2, adaptedPromoted2); }
			if (adaptedPromoted3 != null) { paletteMap.put(promoted3, adaptedPromoted3); }
			if (adaptedPromoted4 != null) { paletteMap.put(promoted4, adaptedPromoted4); }
		} else if (!newClassIsPromoted) {
			if (newClassHasPromotions && !isBoss) {
				int promoted1 = newPromotion1;
				int promoted2 = newPromotion2;
				PaletteV2 adaptedBase = v2PaletteForClass(newClassID, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
				PaletteV2 adaptedPromotion1 = v2PaletteForClass(promoted1, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
				PaletteV2 adaptedPromotion2 = promoted2 != 0 ? v2PaletteForClass(promoted2, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair) : null;
				
				fe8Mapper.setUnpromotedClass(newClassID, charID, !isBoss,
						adaptedBase.getCompressedData().length,
						adaptedPromotion1.getCompressedData().length,
						adaptedPromotion2 != null ? adaptedPromotion2.getCompressedData().length : 0);
				
				integrateFE8PaletteIfPossible(charID, adaptedBase, SlotType.PRIMARY_BASE);
				integrateFE8PaletteIfPossible(charID, adaptedPromotion1, SlotType.FIRST_PROMOTION);
				if (adaptedPromotion2 != null) { integrateFE8PaletteIfPossible(charID, adaptedPromotion2, SlotType.SECOND_PROMOTION); }
				
				paletteMap.clear();
				
				if (adaptedBase != null) { paletteMap.put(newClassID, adaptedBase); }
				if (adaptedPromotion1 != null) { paletteMap.put(promoted1, adaptedPromotion1); }
				if (adaptedPromotion2 != null) { paletteMap.put(promoted2, adaptedPromotion2); }
				
			} else {
				PaletteV2 adaptedBase = v2PaletteForClass(newClassID, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
				fe8Mapper.setUnpromotedClass(newClassID, charID, !isBoss, adaptedBase.getCompressedData().length, 0, 0);
				integrateFE8PaletteIfPossible(charID, adaptedBase, SlotType.PRIMARY_BASE);
				paletteMap.clear();
				if (adaptedBase != null) {
					paletteMap.put(newClassID, adaptedBase);
				}
			}
		} else { // New class is promoted
			PaletteV2 adaptedPromotion = v2PaletteForClass(newClassID, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
			fe8Mapper.setPromotedClass(newClassID, charID, adaptedPromotion.getCompressedData().length);
			integrateFE8PaletteIfPossible(charID, adaptedPromotion, SlotType.FIRST_PROMOTION);
			paletteMap.clear();
			if (adaptedPromotion != null) {
				paletteMap.put(newClassID, adaptedPromotion);
			}
		}
	}
	
	private void integrateFE8PaletteIfPossible(int charID, PaletteV2 adaptedPalette, SlotType type) {
		if (adaptedPalette == null) { return; }
		byte[] newData = adaptedPalette.getCompressedData();
		
		int paletteID = fe8Mapper.paletteIDForCharacterInClassType(charID, type);
		adaptedPalette.setIdentifier(paletteID);
		if (paletteID != 0) {
			Long offset = fe8Mapper.getRegisteredPaletteOffset(paletteID);
			Integer existingLength = fe8Mapper.getRegisteredPaletteLength(paletteID);
			if (offset != null) {
				assert existingLength == null || existingLength >= newData.length : "Palette space is too small to accomodate adapted palette.";
				adaptedPalette.overrideOffset(offset);
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Assigned palette ID 0x" + Integer.toHexString(paletteID).toUpperCase() + " offset 0x" + Long.toHexString(offset));
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Existing Palette Size: " + existingLength + "\tNew Palette Size: " + newData.length);
			} else {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Assigned New Palette ID 0x" + Integer.toHexString(paletteID).toUpperCase() + " (length: " + Integer.toString(newData.length) + ")");
				newPalettesV2.put(paletteID, adaptedPalette);
			}
			
			paletteByPaletteIDV2.put(paletteID, adaptedPalette);
		}
	}
	
	private Palette paletteForAdaptingCharacterToClass(int charID, int originalClassID, int newClassID, Palette[] referencePalettes, Map<Integer, Integer> customMap) {
		Palette originalPalette = getPalette(charID, originalClassID);
		Palette template = getTemplatePalette(newClassID);
		
		if (originalPalette == null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "No palette found for character 0x" + Integer.toHexString(charID) + " class 0x" + Integer.toHexString(originalClassID));
			return null;
		}
		
		if (template == null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "No template found for class 0x" + Integer.toHexString(newClassID));
			return null;
		}
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Original palette offset: " + Long.toHexString(originalPalette.getInfo().getOffset()));
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Template palette offset: " + Long.toHexString(template.getInfo().getOffset()));
		
		Palette adaptedPalette = new Palette(template, originalPalette, referencePalettes, customMap);
		return adaptedPalette;
	}
	
	private Palette paletteForClass(int newClassID, Palette[] referencePalettes, Map<Integer, Integer> customMap) {
		Palette template = getTemplatePalette(newClassID);
		return new Palette(template, null, referencePalettes, customMap);
	}
	
	private PaletteV2 v2PaletteForClass(int newClassID, PaletteV2[] referencePalettes, PaletteV2.PaletteType type, PaletteColor[] supplementalHairColors) {
		PaletteV2 template = getV2TemplatePalette(newClassID);
		PaletteV2 adapted = new PaletteV2(template);
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapting Palette using " + referencePalettes.length + " reference palettes.");
		// We should just apply it to every allegiance. It'll cut down on compression space if they all match.
		adapted.adaptPalette(referencePalettes, PaletteType.PLAYER, supplementalHairColors);
		adapted.adaptPalette(referencePalettes, PaletteType.ENEMY, supplementalHairColors);
		adapted.adaptPalette(referencePalettes, PaletteType.NPC, supplementalHairColors);
		adapted.adaptPalette(referencePalettes, PaletteType.OTHER, supplementalHairColors);
		adapted.adaptPalette(referencePalettes, PaletteType.LINK, supplementalHairColors);
		return adapted;
	}
	
	private static class Change {
		private GBAFECharacterData character;
		private GBAFECharacterData reference;
		private Integer sourceUnpromotedClassID;
		private Integer targetUnpromotedClassID;
		private Integer sourcePromotedClassID;
		private Integer targetPromotedClassID;
	}
	
	private List<Change> queuedChanges = new ArrayList<Change>();
	
	public void enqueueChange(GBAFECharacterData character, GBAFECharacterData reference, Integer sourceUnpromotedClassID, Integer targetUnpromotedClassID, Integer sourcePromotedClassID, Integer targetPromotedClassID) {
		if (targetUnpromotedClassID != null) {
			mapper.prepareCharacterForClass(character.getID(), targetUnpromotedClassID);
		} else {
			mapper.prepareCharacterForClass(character.getID(), targetPromotedClassID);
		}
		
		Change change = new Change();
		change.character = character;
		change.reference = reference;
		change.sourceUnpromotedClassID = sourceUnpromotedClassID;
		change.targetUnpromotedClassID = targetUnpromotedClassID;
		change.sourcePromotedClassID = sourcePromotedClassID;
		change.targetPromotedClassID = targetPromotedClassID;
		
		queuedChanges.add(change);
	}
	
	public void flushChangeQueue(FreeSpaceManager freeSpace) {
		finalizePreparations();
		if (gameType == GameType.FE8) {
			List<Integer> orderedIDsToWrite = newPalettesV2.keySet().stream().sorted().collect(Collectors.toList());
			for (int paletteID : orderedIDsToWrite) {
				PaletteV2 palette = newPalettesV2.get(paletteID);
				long newOffset = freeSpace.reserveInternalSpace(palette.getCompressedData().length, "Palette 0x" + Integer.toHexString(paletteID), true);
				assert newOffset != 0 : "Insufficient internal space for palette.";
				palette.overrideOffset(newOffset);
				appendedPaletteIDsV2.put(paletteID, palette);
			}
		} else {
			for (Change change : queuedChanges) {
				if (change.targetUnpromotedClassID != null) {
					adaptCharacterToClass(change.character, change.reference, change.sourceUnpromotedClassID, change.targetUnpromotedClassID, false, freeSpace);
				}
				if (change.targetPromotedClassID != null) {
					adaptCharacterToClass(change.character, change.reference, change.sourcePromotedClassID, change.targetPromotedClassID, true, freeSpace);
				}
			}
		}
	}
	
	// Fills characters without palette IDs a palette ID appended to the end.
	private void finalizePreparations() {
		if (gameType == GameType.FE8) {
			backfillFE8Palettes();
		} else {
			mapper.flushWaitListWithEmptyPaletteIDs(emptyPaletteIDs);
		}
	}
	
	private void adaptCharacterToClass(GBAFECharacterData character, GBAFECharacterData reference, Integer sourceClassID, Integer targetClassID, boolean isPromoted, FreeSpaceManager freeSpace) {
		int paletteID = isPromoted ? character.getPromotedPaletteIndex() : character.getUnpromotedPaletteIndex();
		Palette existingPalette = paletteByPaletteID.get(paletteID);
		Palette[] references = getReferencePalettesForCharacter(reference.getID());
		Palette adaptedPalette = paletteForClass(targetClassID, references, null);
		if (existingPalette != null) {
			adaptedPalette.overrideOffset(existingPalette.getInfo().getOffset());
		} else {
			long newOffset = freeSpace.reserveSpace(adaptedPalette.getDataLength(), "Palette 0x" + Integer.toHexString(paletteID), true);
			adaptedPalette.overrideOffset(newOffset);
			// We also need to write this address.
			appendedPaletteIDs.put(paletteID, adaptedPalette);
		}
		
		Map<Integer, Palette> paletteMap = characterPalettes.get(character.getID());
		if (sourceClassID != null) {
			paletteMap.remove(sourceClassID);
		}
		paletteMap.put(targetClassID, adaptedPalette);
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
		
		Palette adaptedPalette = paletteForAdaptingCharacterToClass(charID, originalClassID, newClassID, getAllPalettesForCharacter(charID), null);
		Map<Integer, Palette> paletteMap = characterPalettes.get(charID);
		Palette adaptedPromotedPalette = paletteForAdaptingCharacterToClass(charID, originalPromotedClassID, newPromotedClassID, getAllPalettesForCharacter(charID), null);
		if (adaptedPromotedPalette != null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapted palette offset: " + Long.toHexString(adaptedPalette.getInfo().getOffset()));
			paletteMap.put(newPromotedClassID, adaptedPromotedPalette);
			paletteMap.remove(originalPromotedClassID);
		}
		if (adaptedPalette != null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Adapted palette offset: " + Long.toHexString(adaptedPalette.getInfo().getOffset()));
			paletteMap.put(newClassID, adaptedPalette);
			paletteMap.remove(originalClassID);
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		if (gameType == GameType.FE8) {
			
			for (Map<Integer, PaletteV2> map : characterPalettesV2.values()) {
				for (PaletteV2 palette : map.values()) {
					palette.commitPalette(compiler);
				}
			}
			
			long baseOffset = FE8Data.PaletteTableOffset;
			int entrySize = FE8Data.PaletteEntrySize;
			
			for (Integer appendedPaletteID : appendedPaletteIDsV2.keySet()) {
				PaletteV2 appendedPalette = appendedPaletteIDsV2.get(appendedPaletteID);
				long offsetToWriteTo = baseOffset + (appendedPaletteID * entrySize);
				byte[] bytesToWrite = WhyDoesJavaNotHaveThese.bytesFromAddress(appendedPalette.getDestinationOffset());
				compiler.addDiff(new Diff(offsetToWriteTo, bytesToWrite.length, bytesToWrite, new byte[] {0, 0, 0, 0}));
			}
			
		} else {
			for (Map<Integer, Palette> map : characterPalettes.values()) {
				for (Palette palette : map.values()) {
					palette.commitPalette(compiler);
				}
			}
			
			// Write the pointers to any palettes we added.
			long baseOffset = 0;
			int entrySize = 0;
			if (gameType == GameType.FE6) { baseOffset = FE6Data.PaletteTableOffset; entrySize = FE6Data.PaletteEntrySize; }
			else if (gameType == GameType.FE7) { baseOffset = FE7Data.PaletteTableOffset; entrySize = FE7Data.PaletteEntrySize; }
			else { return; }
			for (Integer appendedPaletteID : appendedPaletteIDs.keySet()) {
				Palette appendedPalette = appendedPaletteIDs.get(appendedPaletteID);
				long offsetToWriteTo = baseOffset + (appendedPaletteID * entrySize);
				byte[] bytesToWrite = WhyDoesJavaNotHaveThese.bytesFromAddress(appendedPalette.getInfo().getOffset());
				compiler.addDiff(new Diff(offsetToWriteTo, bytesToWrite.length, bytesToWrite, new byte[] {0, 0, 0, 0}));
			}
		}
	}
	
	public Palette getTemplatePalette(int classID) {
		switch (gameType) {
		case FE6:
		case FE7:
			return templates.get(classID);
		case FE8:
			
		default:
			return null;
		}
	}
	
	public PaletteV2 getV2TemplatePalette(int classID) {
		return templatesV2.get(classID);
	}
}
