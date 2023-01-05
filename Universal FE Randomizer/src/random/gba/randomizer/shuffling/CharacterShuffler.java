package random.gba.randomizer.shuffling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEStatDAO;
import fedata.gba.fe6.FE6Data;
import fedata.gba.general.PaletteColor;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.PortraitDataLoader;
import random.gba.loader.TextLoader;
import random.gba.randomizer.service.ClassAdjustmentDAO;
import random.gba.randomizer.service.GBAClassAdjustmentService;
import random.gba.randomizer.service.ItemAssignmentService;
import random.gba.randomizer.shuffling.data.GBAFEPortraitData;
import random.gba.randomizer.shuffling.data.PortraitFormat;
import random.general.PoolDistributor;
import ui.model.CharacterShufflingOptions;
import ui.model.ItemAssignmentOptions;
import util.DebugPrinter;
import util.FileReadHelper;
import util.FreeSpaceManager;
import util.GBAImageCodec;
import util.LZ77;
import util.PaletteUtil;
import util.WhyDoesJavaNotHaveThese;

/**
 * Randomizer that shuffles in characters from different games into the rom
 * being randomized.
 * <p>
 * The characters can dynamically be configured in json files.
 */
public class CharacterShuffler {

	
	static final int rngSalt = 911;

	@SuppressWarnings("unused")
	public static void shuffleCharacters(GameType type, CharacterDataLoader characterData, TextLoader textData, 
			Random rng, FileHandler fileHandler, PortraitDataLoader portraitData, FreeSpaceManager freeSpace, ChapterLoader chapterData,
			ClassDataLoader classData, CharacterShufflingOptions options, ItemAssignmentOptions inventoryOptions, ItemDataLoader itemData) {
		// Don't include playable characters into the ones that could be replaced, 
		// as most files probably won't be played enough to unlock those anyway.
		List<GBAFECharacterData> characterPool = new ArrayList<GBAFECharacterData>(
				characterData.canonicalPlayableCharacters(false));

		// Shuffle in character from the other games
		if (options.getIncludedShuffles().size() != 0) {
			PoolDistributor<GBACrossGameData> distributor = new PoolDistributor<GBACrossGameData>();
			for (String includedCharacters : options.getIncludedShuffles()) {
				distributor.addAll(CharacterImporter.importCharacterDataFromFiles(includedCharacters));
			}
			for (GBAFECharacterData character : characterPool) {
				// Determine if the current character should be replaced
				if (options.getChance() < rng.nextInt(1, 100)) {
					continue;
				}

				// The character should be replaced, get a random character to shuffle in
				GBACrossGameData crossGameData = distributor.getRandomItem(rng, true);
				if (crossGameData == null) {
					// If no more character to find, then stop
					return;
				}

				// (a) Get a valid class in the target game for what was configured for the character
				int targetClassId =  GBACrossGameData.getEquivalentClass(type, crossGameData).getID();

				if (targetClassId == 0) {
					DebugPrinter.error(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, String.format("Couldn't find an applicable class for character %s class %s%n", crossGameData.name,
							crossGameData.characterClass));
					continue;
				}
				
				// (b) initialize some variables needed later
				GBAFEClassData targetClass = classData.classForID(targetClassId);
				GBAFEClassData sourceClass = classData.classForID(character.getClassID());
				int slotLevel = character.getLevel();

				// (c) Insert the portrait for the current character into the rom and repoint the Portrait Data
				try {
					changePortrait(character, portraitData, type, crossGameData, freeSpace, fileHandler);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
				// (d) Set simple fields
				textData.setStringAtIndex(character.getNameIndex(), crossGameData.name+"[X]");
				textData.setStringAtIndex(character.getDescriptionIndex(), crossGameData.description1+"\r\n"+crossGameData.description2+"[X]");
				character.setGrowths(crossGameData.growths);
				updateWeaponRanks(character, crossGameData);
				
				// (e) Update the bases, and potentially auto level the Character to the level of the slot.
				// Due to Promotion / Demotion, the output of the targetClass might be different from what was passed into this method
				targetClass = updateBases(textData, rng, classData, options, character, crossGameData, targetClassId,
						targetClass, sourceClass, slotLevel);
				targetClassId = targetClass.getID();

				for (GBAFECharacterData linkedChar : characterData.linkedCharactersForCharacter(character)) {
					updateUnitInChapter(chapterData, character, crossGameData, targetClassId, options, itemData, inventoryOptions);
				}
				
				// (g) give the Unit new items to use
				ItemAssignmentService.assignNewItems(characterData, character, targetClass, chapterData, inventoryOptions, rng, textData, classData, itemData);
			}

		}
	}

	/**
	 * Sets the configured Weapon ranks (clamped to ensure it's between 0 and 255) for the given character
	 */
	private static void updateWeaponRanks(GBAFECharacterData character, GBACrossGameData crossGameData) {
		character.setSwordRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[0], 0, 255));
		character.setLanceRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[1], 0, 255));
		character.setAxeRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[2], 0, 255));
		character.setBowRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[3], 0, 255));
		character.setStaffRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[4], 0, 255));
		character.setAnimaRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[5], 0, 255));
		character.setLightRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[6], 0, 255));
		character.setDarkRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[7], 0, 255));
	}

	/**
	 * Sets the updated bases from the 
	 */
	private static GBAFEClassData updateBases(TextLoader textData, Random rng, ClassDataLoader classData,
			CharacterShufflingOptions options, GBAFECharacterData character, GBACrossGameData chara, int targetClassId,
			GBAFEClassData targetClass, GBAFEClassData sourceClass, int slotLevel) {
		if (CharacterShufflingOptions.ShuffleLevelingMode.UNCHANGED.equals(options.getLevelingMode())) {
			character.setBases(chara.bases);
		} else if (CharacterShufflingOptions.ShuffleLevelingMode.AUTOLEVEL.equals(options.getLevelingMode())) {

			boolean shouldBePromoted = classData.isPromotedClass(character.getClassID());

			character.setClassID(targetClassId);

			boolean isPromoted = classData.isPromotedClass(targetClassId);

			ClassAdjustmentDAO adjustmentDAO = GBAClassAdjustmentService.handleClassAdjustment(slotLevel,
					chara.level, shouldBePromoted, isPromoted, rng, classData, null, targetClass, character,
					sourceClass, null, textData);
			targetClass = adjustmentDAO.targetClass;
			boolean promotionRequired = shouldBePromoted && !isPromoted;
			boolean demotionRequired = !shouldBePromoted && isPromoted;
			character.setClassID(targetClassId);

			GBAFEStatDAO newPersonalBases = GBAClassAdjustmentService.autolevel(chara.bases, chara.growths,
					adjustmentDAO.promoBonuses, promotionRequired, demotionRequired,
					adjustmentDAO.levelAdjustment);
			character.setBases(newPersonalBases);
		}
		return targetClass;
	}

	/**
	 * Updates the class of the character being replaced to the one of the new
	 * character
	 * 
	 * @param chapterData - the data for all the chapters
	 * @param character   - the current Character being changed
	 * @param targetClass - the target Class that the character should now have.
	 */
	private static void updateUnitInChapter(ChapterLoader chapterData, GBAFECharacterData character,
			GBACrossGameData replacement, int targetClass, CharacterShufflingOptions options, 
			ItemDataLoader itemData, ItemAssignmentOptions inventoryOptions) {
		character.setClassID(targetClass);

		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				if (chapterUnit.getCharacterNumber() == character.getID()) {
					chapterUnit.setStartingClass(targetClass);
					
					// If the user selects that the Units should be inserted as they are (which would be dumb) then update the level.
					if (CharacterShufflingOptions.ShuffleLevelingMode.UNCHANGED.equals(options.getLevelingMode())) {
						chapterUnit.setStartingLevel(replacement.level);
					}
				}
			}
		}
	}

	/**
	 * Sets the characters new portrait data
	 *
	 * @param character    - the character in the Rom that will have their portrait
	 *                     replaced
	 * @param portraitData - Portrait Dataloader that gives the offsets
	 * @param type         - The GBA FE Game that is the target of the Randomization
	 * @param chara        - the Character that will get randomized into the rom
	 * @param freeSpace    - the freeSpace manager to insert the new portrait into
	 *                     the free space
	 */
	private static void changePortrait(GBAFECharacterData character, PortraitDataLoader portraitData, GameType type,
			GBACrossGameData chara, FreeSpaceManager freeSpace, FileHandler fileHandler) throws IOException {
		// Grab the Portrait Data (Pointers)
		GBAFEPortraitData characterPortraitData = portraitData.getPortraitDataByFaceId(character.getFaceID());

		// Get the Portrait Format depending on the game
		PortraitFormat targetFormat = PortraitFormat.getPortraitFormatForGame(type);

		// Get the Palette from the Json
		PaletteColor[] palette = GBAImageCodec.getArrayFromPaletteString(chara.paletteString);

		// Insert and repoint Main Portrait
		byte[] mainPortrait = GBAImageCodec.getGBAPortraitGraphicsDataForImage(chara.portraitPath, palette,
				targetFormat.getMainPortraitChunks(), targetFormat.getMainPortraitSize(),
				targetFormat.getMainPortraitPrefix());
		if (targetFormat.isMainPortraitCompressed()) {
			mainPortrait = LZ77.compress(mainPortrait);
		}

		// For some reason the Portrait must be byte aligned or it really messes with the rom..
		long mainPortraitAddress = freeSpace.setValue(mainPortrait, character.getFaceID() + "_MainPortrait", true);
		characterPortraitData.setMainPortraitPointer(WhyDoesJavaNotHaveThese.bytesFromAddress(mainPortraitAddress));

		// Insert and repoint Mini Portrait
		byte[] miniPortrait = GBAImageCodec.getGBAPortraitGraphicsDataForImage(chara.portraitPath, palette,
				targetFormat.getMiniPortraitChunks(), targetFormat.getMiniPortraitSize());
		if (targetFormat.isMiniCompressed()) {
			miniPortrait = LZ77.compress(miniPortrait);
		}

		long miniPortraitAddress = freeSpace.setValue(miniPortrait, character.getFaceID() + "_MiniPortrait", true);
		characterPortraitData.setMiniPortraitPointer(WhyDoesJavaNotHaveThese.bytesFromAddress(miniPortraitAddress));

		// Insert and repoint Mouth Chunks
		if (targetFormat.getMouthChunksSize() != null) {
			byte[] mouthFrames = GBAImageCodec.getGBAPortraitGraphicsDataForImage(chara.portraitPath, palette,
					targetFormat.getMouthChunks(), targetFormat.getMouthChunksSize());
			long mouthFramesAddress = freeSpace.setValue(mouthFrames, character.getFaceID() + "_MouthFramesPortrait", true);
			characterPortraitData.setMouthFramesPointer(WhyDoesJavaNotHaveThese.bytesFromAddress(mouthFramesAddress));
		}

		// Update the Coordinates of the Eyes / Mouth
		boolean isFE6 = GameType.FE6.equals(type);
		byte[] facialFeaturesCoordinates = new byte[isFE6 ? 2 : 4];
		facialFeaturesCoordinates[0] = (byte) chara.mouthX;
		facialFeaturesCoordinates[1] = (byte) chara.mouthY;
		if (!isFE6) {
			facialFeaturesCoordinates[2] = (byte) chara.eyeX;
			facialFeaturesCoordinates[3] = (byte) chara.eyeY;
		}
		characterPortraitData.setFacialFeatureCoordinates(facialFeaturesCoordinates);

		// Write the Palette of the image
		characterPortraitData.setNewPalette(PaletteUtil.getByteArrayFromString(chara.paletteString));

	}

}