package random.gba.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.fe8.FE8PaletteMapper;
import fedata.gba.fe8.FE8PaletteMapper.SlotType;
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
import util.recordkeeper.RecordKeeper;

public class PaletteLoader {
	private FEBase.GameType gameType;
	
	// V2
	private Map<Integer, PaletteV2> templatesV2 = new HashMap<Integer, PaletteV2>();
	private Map<Integer, PaletteV2> paletteByPaletteIDV2 = new HashMap<Integer, PaletteV2>();
	
	private Map<Integer, PaletteV2> newPalettesV2 = new HashMap<Integer, PaletteV2>();
	private Map<Integer, PaletteV2> appendedPaletteIDsV2 = new HashMap<Integer, PaletteV2>();
	
	// CharacterID -> (ClassID, Palette)
	private Map<Integer, Map<Integer, PaletteV2>> referencePalettesV2 = new HashMap<Integer, Map<Integer, PaletteV2>>();
	
	private Map<Integer, PaletteColor[]> supplementalHairColors = new HashMap<Integer, PaletteColor[]>();
	
	// TODO: Put this somewhere else.
	private PaletteMapper mapper;
	
	private FE8PaletteMapper fe8Mapper;
	private PromotionDataLoader fe8Promotions;
	
	private List<Integer> emptyPaletteIDs = new ArrayList<Integer>();
	
	public PaletteLoader(FEBase.GameType gameType, FileHandler handler, CharacterDataLoader charData, ClassDataLoader classData) {
		this.gameType = gameType;
		
		switch (gameType) {
		case FE6:
			for (FE6Data.Character character : FE6Data.Character.allPlayableCharacters) {
				int charID = FE6Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				PaletteColor[] supplementalHair = FE6Data.Palette.supplementaryHairColorForCharacter(charID);
				if (supplementalHair != null) {
					supplementalHairColors.put(charID, supplementalHair);
				}
				for (PaletteInfo paletteInfo : FE6Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
					referenceMap.put(classID, new PaletteV2(handler, paletteInfo));
					FE6Data.CharacterClass fe6class = FE6Data.CharacterClass.valueOf(classID);
					FE6Data.Character fe6char = FE6Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Character 0x" + Integer.toHexString(charID) + " (" + fe6char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()) + " (Class: " + Integer.toHexString(classID) + " (" + fe6class.toString() + "))");
				}
			}
			for (FE6Data.Character boss : FE6Data.Character.allBossCharacters) {
				int charID = FE6Data.Character.canonicalIDForCharacterID(boss.ID);
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				PaletteColor[] supplementalHair = FE6Data.Palette.supplementaryHairColorForCharacter(charID);
				if (supplementalHair != null) {
					supplementalHairColors.put(charID, supplementalHair);
				}
				for (PaletteInfo paletteInfo : FE6Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
					referenceMap.put(classID, new PaletteV2(handler, paletteInfo));
					FE6Data.Character fe6char = FE6Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Boss 0x" + Integer.toHexString(charID) + " (" + fe6char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()));
				}
			}
			
			for (FE6Data.CharacterClass characterClass : FE6Data.CharacterClass.allValidClasses) {
				templatesV2.put(characterClass.ID, new PaletteV2(handler, FE6Data.Palette.defaultPaletteForClass(characterClass.ID)));
			}
			
			for (FE6Data.CharacterClass characterClass : FE6Data.CharacterClass.additionalClassesToPalletLoad) {
				templatesV2.put(characterClass.ID, new PaletteV2(handler, FE6Data.Palette.defaultPaletteForClass(characterClass.ID)));
			}
			
			for (int i = FE6Data.Palette.maxUsedPaletteIndex() + 1; i < FE6Data.Palette.maxPaletteIndex(); i++) {
				emptyPaletteIDs.add(i);
			}
			
			mapper = new PaletteMapper(charData, emptyPaletteIDs);
			
			for (int paletteID : paletteByPaletteIDV2.keySet()) {
				mapper.registerPalette(paletteID, paletteByPaletteIDV2.get(paletteID).getOriginalCompressedLength(), paletteByPaletteIDV2.get(paletteID).getDestinationOffset());
			}
			
			break;
		case FE7:
			for (FE7Data.Character character : FE7Data.Character.allPlayableCharacters) {
				int charID = FE7Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				PaletteColor[] supplementalHair = FE7Data.Palette.supplementaryHairColorForCharacter(charID);
				if (supplementalHair != null) {
					supplementalHairColors.put(charID, supplementalHair);
				}
				for (PaletteInfo paletteInfo : FE7Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
					referenceMap.put(classID, new PaletteV2(handler, paletteInfo));
					FE7Data.CharacterClass fe7class = FE7Data.CharacterClass.valueOf(classID);
					FE7Data.Character fe7char = FE7Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Character 0x" + Integer.toHexString(charID) + " (" + fe7char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()) + " (Class: " + Integer.toHexString(classID) + " (" + fe7class.toString() + "))");
				}
			}
			for (FE7Data.Character boss : FE7Data.Character.allBossCharacters) {
				int charID = FE7Data.Character.canonicalIDForCharacterID(boss.ID);
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				PaletteColor[] supplementalHair = FE7Data.Palette.supplementaryHairColorForCharacter(charID);
				if (supplementalHair != null) {
					supplementalHairColors.put(charID, supplementalHair);
				}
				for (PaletteInfo paletteInfo : FE7Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
					referenceMap.put(classID, new PaletteV2(handler, paletteInfo));
					FE7Data.Character fe7char = FE7Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Boss 0x" + Integer.toHexString(charID) + " (" + fe7char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()));
				}
			}
			
			for (FE7Data.CharacterClass characterClass : FE7Data.CharacterClass.allValidClasses) {
				templatesV2.put(characterClass.ID, new PaletteV2(handler, FE7Data.Palette.defaultPaletteForClass(characterClass.ID)));
			}
			
			for (int i = FE7Data.Palette.maxUsedPaletteIndex() + 1; i < FE7Data.Palette.maxPaletteIndex(); i++) {
				emptyPaletteIDs.add(i);
			}
			
			mapper = new PaletteMapper(charData, emptyPaletteIDs);
			
			for (int paletteID : paletteByPaletteIDV2.keySet()) {
				mapper.registerPalette(paletteID, paletteByPaletteIDV2.get(paletteID).getOriginalCompressedLength(), paletteByPaletteIDV2.get(paletteID).getDestinationOffset());
			}
			
			break;
		case FE8:
			for (FE8Data.Character character : FE8Data.Character.allPlayableCharacters) {
				int charID = FE8Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				for (PaletteInfo paletteInfo : FE8Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
					referenceMap.put(classID, new PaletteV2(handler, paletteInfo));
					FE8Data.CharacterClass fe8class = FE8Data.CharacterClass.valueOf(classID);
					FE8Data.Character fe8char = FE8Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Character 0x" + Integer.toHexString(charID) + " (" + fe8char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()) + " (Class: " + Integer.toHexString(classID) + " (" + fe8class.toString() + "))");
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Palette size: " + Integer.toString(palette.getOriginalCompressedLength()) + " bytes");
				}
			}
			for (FE8Data.Character character : FE8Data.Character.safeCreatureCampaignCharacters) {
				int charID = FE8Data.Character.canonicalIDForCharacterID(character.ID);
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				for (PaletteInfo paletteInfo : FE8Data.Palette.palettesForCharacter(charID)) {
					int classID = paletteInfo.getClassID();
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
					referenceMap.put(classID, new PaletteV2(handler, paletteInfo));
					FE8Data.CharacterClass fe8class = FE8Data.CharacterClass.valueOf(classID);
					FE8Data.Character fe8char = FE8Data.Character.valueOf(charID);
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Initializing Character 0x" + Integer.toHexString(charID) + " (" + fe8char.toString() + ")" + " with palette at offset 0x" + Long.toHexString(paletteInfo.getOffset()) + " (Class: " + Integer.toHexString(classID) + " (" + fe8class.toString() + "))");
					DebugPrinter.log(DebugPrinter.Key.PALETTE, "Palette size: " + Integer.toString(palette.getOriginalCompressedLength()) + " bytes");
				}
			}
			for (FE8Data.Character boss : FE8Data.Character.allBossCharacters) {
				int charID = FE8Data.Character.canonicalIDForCharacterID(boss.ID);
				Map<Integer, PaletteV2> referenceMap = new HashMap<Integer, PaletteV2>();
				referencePalettesV2.put(charID, referenceMap);
				for (PaletteInfo paletteInfo : FE8Data.Palette.palettesForCharacter(charID)) {
					PaletteV2 palette = new PaletteV2(handler, paletteInfo);
					paletteByPaletteIDV2.put(paletteInfo.getPaletteID(), palette);
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
			for (FE8Data.CharacterClass characterClass : FE8Data.CharacterClass.additionalClassesToPalletLoad) {
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
			
			break;
		default:
			break;
		}
	}
	
	public FE8PaletteMapper setupFE8SpecialManagers(FileHandler handler, PromotionDataLoader promotionManager) {
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
	
	public PaletteV2[] getV2ReferencePalettesForCharacter(int characterID) {
		Collection<PaletteV2> allPalettes = referencePalettesV2.get(canonicalCharacterID(characterID)).values();
		return allPalettes.toArray(new PaletteV2[allPalettes.size()]);
	}
	
	public PaletteV2 getV2ReferencePalettesForCharacter(int characterID, int classID) {
		PaletteV2 specificPalette = referencePalettesV2.get(canonicalCharacterID(characterID)).get(classID);
		return specificPalette;
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
		
		if (fe8Mapper.getCharactersNeedingAdditionalPalettes().length == 0) { return; }
		
		// TODO: Implement this if we actually need it. I don't think we do...
		assert false : "Not implemented.";
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
		
		PaletteColor[] supplementalHair = FE8Data.Palette.supplementaryHairColorForCharacter(referenceID);
		
		if (willBecomeTrainee) {
			if (fe8Mapper.classIDMappedToCharacterForType(charID, SlotType.TRAINEE) == newClassID) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Same trainee class found. Skipping palette replacement.");
			} else {
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
			}
		} else if (!newClassIsPromoted) {
			if (fe8Mapper.classIDMappedToCharacterForType(charID, SlotType.PRIMARY_BASE) == newClassID) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Same base class found. Skipping palette replacement.");
			} else {
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
				} else {
					PaletteV2 adaptedBase = v2PaletteForClass(newClassID, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
					fe8Mapper.setUnpromotedClass(newClassID, charID, !isBoss, adaptedBase.getCompressedData().length, 0, 0);
					integrateFE8PaletteIfPossible(charID, adaptedBase, SlotType.PRIMARY_BASE);
				}
			}
		} else { // New class is promoted
			if (fe8Mapper.classIDMappedToCharacterForType(charID, SlotType.FIRST_PROMOTION) == newClassID) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Same Promoted class found. Skipping palette replacement.");
			} else {
				PaletteV2 adaptedPromotion = v2PaletteForClass(newClassID, referencePalettes, isBoss ? PaletteType.ENEMY : PaletteType.PLAYER, supplementalHair);
				fe8Mapper.setPromotedClass(newClassID, charID, adaptedPromotion.getCompressedData().length);
				integrateFE8PaletteIfPossible(charID, adaptedPromotion, SlotType.FIRST_PROMOTION);
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
	
	public PaletteV2 generatePalette(int classID, int characterID, PaletteV2.PaletteType type, PaletteColor[] supplementalHairColors) {
		PaletteV2 template = getV2TemplatePalette(classID);
		PaletteV2 adapted = new PaletteV2(template);
		PaletteV2[] referencePalettes = getV2ReferencePalettesForCharacter(characterID);
		adapted.adaptPalette(referencePalettes, type, supplementalHairColors);
		return adapted;
	}
	
	private PaletteV2 v2PaletteForClass(int newClassID, PaletteV2[] referencePalettes, PaletteV2.PaletteType type, PaletteColor[] supplementalHairColors) {
		PaletteV2 template = getV2TemplatePalette(newClassID);
		if (template == null) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, String.format("No Template Palette found for Class %d.", newClassID));
			
		}
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
		
		private PaletteV2 basePalette;
		private PaletteV2 promotedPalette;
	}
	
	private List<Change> queuedChanges = new ArrayList<Change>();
	
	public void enqueueChange(GBAFECharacterData character, GBAFECharacterData reference, CharacterDataLoader charData, ClassDataLoader classData, Integer targetClassID, boolean needsPromotion) {
		boolean isPromoted = classData.isPromotedClass(targetClassID);
		boolean canPromote = classData.canClassPromote(targetClassID) && needsPromotion;
		
		int characterID = character.getID();
		int referenceID = reference.getID();
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Enqueuing change for character " + charData.debugStringForCharacter(characterID) + " to class " + classData.debugStringForClass(targetClassID) + " using reference " + charData.debugStringForCharacter(referenceID));
		
		Change change = new Change();
		change.character = character;
		
		PaletteV2[] referencePalettes = getV2ReferencePalettesForCharacter(referenceID);
		if (referencePalettes == null || referencePalettes.length == 0) {
			return; // If we have no references, this character probably has no palettes to begin with.
		}
		
		if (isPromoted) {
			int originalPaletteIndex = character.getPromotedPaletteIndex();
			PaletteV2 originalPalette = paletteByPaletteIDV2.get(originalPaletteIndex);
			if (originalPalette != null && originalPalette.getClassID() == targetClassID) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Same promoted class found. Skipping adapting palette.");
			} else {
				PaletteV2 adaptedPromotion = v2PaletteForClass(targetClassID, referencePalettes, PaletteType.PLAYER, supplementalHairColors.get(referenceID));
				mapper.setCharacterToPromotedClass(characterID, targetClassID, adaptedPromotion.getCompressedData().length);
				adaptedPromotion.setIdentifier(character.getPromotedPaletteIndex());
				change.promotedPalette = adaptedPromotion;
			}
		} else if (!canPromote) {
			int originalPaletteIndex = character.getUnpromotedPaletteIndex();
			PaletteV2 originalPalette = paletteByPaletteIDV2.get(originalPaletteIndex);
			if (originalPalette != null && originalPalette.getClassID() == targetClassID) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Same unpromoted class found. Skipping adapting palette.");
			} else {
				PaletteV2 adaptedBase = v2PaletteForClass(targetClassID, referencePalettes, PaletteType.PLAYER, supplementalHairColors.get(referenceID));
				mapper.setCharacterToUnpromotedOnlyClass(characterID, targetClassID, adaptedBase.getCompressedData().length);
				adaptedBase.setIdentifier(character.getUnpromotedPaletteIndex());
				change.basePalette = adaptedBase;
			}
		} else {
			int unpromotedPaletteIndex = character.getUnpromotedPaletteIndex();
			int promotedPaletteIndex = character.getPromotedPaletteIndex();
			PaletteV2 unpromotedPalette = paletteByPaletteIDV2.get(unpromotedPaletteIndex);
			PaletteV2 promotedPalette = paletteByPaletteIDV2.get(promotedPaletteIndex);
			
			int promotedClassID = classData.classForID(targetClassID).getTargetPromotionID();
			
			if ((unpromotedPalette != null && unpromotedPalette.getClassID() == targetClassID) ||
					(promotedPalette != null && promotedPalette.getClassID() == promotedClassID)) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "Same unpromoted class found. Skipping adapting palette.");
			} else {
				PaletteV2 adaptedBase = v2PaletteForClass(targetClassID, referencePalettes, PaletteType.PLAYER, supplementalHairColors.get(referenceID));
				PaletteV2 adaptedPromotion = v2PaletteForClass(promotedClassID, referencePalettes, PaletteType.PLAYER, supplementalHairColors.get(referenceID));
				mapper.setCharacterToUnpromotedClass(characterID, targetClassID, adaptedBase.getCompressedData().length, adaptedPromotion.getCompressedData().length);
				adaptedBase.setIdentifier(character.getUnpromotedPaletteIndex());
				adaptedPromotion.setIdentifier(character.getPromotedPaletteIndex());
				change.basePalette = adaptedBase;
				change.promotedPalette = adaptedPromotion;
			}
		}
		
		if (change.basePalette != null || change.promotedPalette != null) {
			queuedChanges.add(change);
		}
	}
	
	public void flushChangeQueue(CharacterDataLoader charData, FreeSpaceManager freeSpace) {
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
				int unpromotedPaletteID = change.character.getUnpromotedPaletteIndex();
				if (unpromotedPaletteID != 0) {
					assert change.basePalette != null : "Unpromoted palette ID detected, but no palette was found.";
					int availableLength = mapper.getPaletteLength(unpromotedPaletteID);
					byte[] compressedBase = change.basePalette.getCompressedData();
					assert compressedBase.length <= availableLength : "Insufficient Space to write palette.";
					Long targetOffset = mapper.getPaletteOffset(unpromotedPaletteID);
					if (targetOffset == null) { 
						targetOffset = freeSpace.reserveInternalSpace(compressedBase.length, "Palette 0x" + Integer.toHexString(unpromotedPaletteID), true);
						appendedPaletteIDsV2.put(unpromotedPaletteID, change.basePalette);
					}
					
					change.basePalette.overrideOffset(targetOffset);
					change.basePalette.setIdentifier(unpromotedPaletteID);
				}
				
				int promotedPaletteID = change.character.getPromotedPaletteIndex();
				if (promotedPaletteID != 0) {
					assert change.promotedPalette != null : "Promoted palette ID detected, but no palette was found.";
					int availableLength = mapper.getPaletteLength(promotedPaletteID);
					byte[] compressedPromotion = change.promotedPalette.getCompressedData();
					assert compressedPromotion.length <= availableLength : "Insufficient Space to write palette.";
					Long targetOffset = mapper.getPaletteOffset(promotedPaletteID);
					if (targetOffset == null) {
						targetOffset = freeSpace.reserveInternalSpace(compressedPromotion.length, "Palette 0x" + Integer.toHexString(promotedPaletteID), true);
						appendedPaletteIDsV2.put(promotedPaletteID, change.promotedPalette);
					}
					
					change.promotedPalette.overrideOffset(targetOffset);
					change.promotedPalette.setIdentifier(promotedPaletteID);
				}
			}
		}
	}
	
	// Fills characters without palette IDs a palette ID appended to the end.
	private void finalizePreparations() {
		if (gameType == GameType.FE8) {
			backfillFE8Palettes();
		} else {
			mapper.flushWaitListWithEmptyPaletteIDs();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		if (gameType == GameType.FE8) {
			for (PaletteV2 palette : paletteByPaletteIDV2.values()) {
				palette.commitPalette(compiler);
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
			for (Change change : queuedChanges) {
				if (change.basePalette != null) { change.basePalette.commitPalette(compiler); }
				if (change.promotedPalette != null) { change.promotedPalette.commitPalette(compiler); }
			}
			
			// Write the pointers to any palettes we added.
			long baseOffset = 0;
			int entrySize = 0;
			if (gameType == GameType.FE6) { baseOffset = FE6Data.PaletteTableOffset; entrySize = FE6Data.PaletteEntrySize; }
			else if (gameType == GameType.FE7) { baseOffset = FE7Data.PaletteTableOffset; entrySize = FE7Data.PaletteEntrySize; }
			else { return; }
			for (Integer appendedPaletteID : appendedPaletteIDsV2.keySet()) {
				PaletteV2 appendedPalette = appendedPaletteIDsV2.get(appendedPaletteID);
				long offsetToWriteTo = baseOffset + (appendedPaletteID * entrySize);
				byte[] bytesToWrite = WhyDoesJavaNotHaveThese.bytesFromAddress(appendedPalette.getDestinationOffset());
				compiler.addDiff(new Diff(offsetToWriteTo, bytesToWrite.length, bytesToWrite, new byte[] {0, 0, 0, 0}));
			}
		}
	}
	
	private String changelogStringForPalette(PaletteV2 palette) {
		if (palette == null) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append("<div style=\"");
		sb.append("display:flex;");
		sb.append("flex-wrap:wrap;");
		sb.append("flex-direction:row;");
		sb.append("\">");
		
		for (int i = 0; i < palette.getNumColors(); i++) {
			PaletteColor color = palette.colorAtIndex(i, PaletteType.PLAYER);
			sb.append("<div style=\"");
			sb.append("flex:0 0 auto;");
			sb.append("margin:5px;");
			
			sb.append("background-color:rgb(" + color.getRedValue() + "," + color.getGreenValue() + "," + color.getBlueValue() + ");");
			
			sb.append("\">");
			
			sb.append("<p style=\"");
			sb.append("color:");
			if (color.getBrightness() > 0.6) {
				sb.append("black;");
			} else {
				sb.append("white;");
			}
			sb.append("margin:0px;");
			sb.append("padding:0px;");
			sb.append("\">");
			
			sb.append(color.getRedValue() + ", " + color.getGreenValue() + ", " + color.getBlueValue());
			
			sb.append("</p>");
			
			sb.append("</div>");
		}
		
		sb.append("</div>");
		
		return sb.toString();
	}
	
	public void recordReferencePalettes(RecordKeeper rk, CharacterDataLoader charData, ClassDataLoader classData, TextLoader textData) {
		String category = "Debug - Palettes";
		rk.registerCategory(category);
		for (GBAFECharacterData character : charData.canonicalPlayableCharacters(true)) {
			String characterName = textData.getStringAtIndex(character.getNameIndex(), true);
			Map<Integer, PaletteV2> palettesByClassID = referencePalettesV2.get(character.getID());
			if (palettesByClassID == null) { continue; }
			List<Integer> classIDs = palettesByClassID.keySet().stream().sorted().collect(Collectors.toList());
			
			for (int classID : classIDs) {
				GBAFEClassData charClass = classData.classForID(classID);
				String className = textData.getStringAtIndex(charClass.getNameIndex(), true);
				boolean isFemale = classData.isFemale(classID);
				if (isFemale) { className = className + " (F)"; }
				rk.recordOriginalEntry(category, characterName, className, changelogStringForPalette(palettesByClassID.get(classID)));
			}
		}
	}
	
	private void recordPalette(RecordKeeper rk, String category, String characterName, int classID, PaletteV2 palette, ClassDataLoader classData, TextLoader textData) {
		GBAFEClassData charClass = classData.classForID(classID);
		String className = textData.getStringAtIndex(charClass.getNameIndex(), true);
		boolean isFemale = classData.isFemale(classID);
		if (isFemale) { className = className + " (F)"; }
		rk.recordUpdatedEntry(category, characterName, className, changelogStringForPalette(palette));
	}
	
	public void recordUpdatedPalettes(RecordKeeper rk, CharacterDataLoader charData, ClassDataLoader classData, TextLoader textData) {
		String category = "Debug - Palettes";
		for (Change change : queuedChanges) {
			GBAFECharacterData character = change.character;
			String characterName = textData.getStringAtIndex(character.getNameIndex(), true);
			
			if (change.basePalette != null) {
				recordPalette(rk, category, characterName, change.basePalette.getClassID(), change.basePalette, classData, textData);
			}
			if (change.promotedPalette != null) {
				recordPalette(rk, category, characterName, change.promotedPalette.getClassID(), change.promotedPalette, classData, textData);
			}
		}
	}
	
	private PaletteV2 paletteForCharacter(GBAFECharacterData character, SlotType type) {
		int paletteID = fe8Mapper.paletteIDForCharacterInClassType(character.getID(), type);
		if (paletteID == 0) { return null; }
		PaletteV2 palette = paletteByPaletteIDV2.get(paletteID);
		if (palette == null) {
			palette = newPalettesV2.get(paletteID);
		}
		return palette;
	}
	
	public void recordUpdatedFE8Palettes(RecordKeeper rk, CharacterDataLoader charData, ClassDataLoader classData, TextLoader textData) {
		String category = "Debug - Palettes";
		for (GBAFECharacterData character : charData.canonicalPlayableCharacters(true)) {
			String characterName = textData.getStringAtIndex(character.getNameIndex(), true);
			int traineeClassID = fe8Mapper.classIDMappedToCharacterForType(character.getID(), SlotType.TRAINEE);
			if (traineeClassID != 0) {
				recordPalette(rk, category, characterName, traineeClassID, paletteForCharacter(character, SlotType.TRAINEE), classData, textData);
			}
			int firstBaseID = fe8Mapper.classIDMappedToCharacterForType(character.getID(), SlotType.PRIMARY_BASE);
			if (firstBaseID != 0) {
				recordPalette(rk, category, characterName, firstBaseID, paletteForCharacter(character, SlotType.PRIMARY_BASE), classData, textData);
			}
			int secondBaseID = fe8Mapper.classIDMappedToCharacterForType(character.getID(), SlotType.SECONDARY_BASE);
			if (secondBaseID != 0) {
				recordPalette(rk, category, characterName, secondBaseID, paletteForCharacter(character, SlotType.SECONDARY_BASE), classData, textData);
			}
			int firstPromoID = fe8Mapper.classIDMappedToCharacterForType(character.getID(), SlotType.FIRST_PROMOTION);
			if (firstPromoID != 0) {
				recordPalette(rk, category, characterName, firstPromoID, paletteForCharacter(character, SlotType.FIRST_PROMOTION), classData, textData);
			}
			int secondPromoID = fe8Mapper.classIDMappedToCharacterForType(character.getID(), SlotType.SECOND_PROMOTION);
			if (secondPromoID != 0) {
				recordPalette(rk, category, characterName, secondPromoID, paletteForCharacter(character, SlotType.SECOND_PROMOTION), classData, textData);
			}
			int thirdPromoID = fe8Mapper.classIDMappedToCharacterForType(character.getID(), SlotType.THIRD_PROMOTION);
			if (thirdPromoID != 0) {
				recordPalette(rk, category, characterName, thirdPromoID, paletteForCharacter(character, SlotType.THIRD_PROMOTION), classData, textData);
			}
			int fourthPromoID = fe8Mapper.classIDMappedToCharacterForType(character.getID(), SlotType.FOURTH_PROMOTION);
			if (fourthPromoID != 0) {
				recordPalette(rk, category, characterName, fourthPromoID, paletteForCharacter(character, SlotType.FOURTH_PROMOTION), classData, textData);
			}
		}
	}
	
	public PaletteV2 getV2TemplatePalette(int classID) {
		return templatesV2.get(classID);
	}
}
