package random.gba.randomizer.shuffling;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import javax.print.attribute.HashAttributeSet;

import com.sun.jndi.ldap.pool.Pool;
import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEStatDto;
import fedata.gba.GBAFECharacterData.Affinity;
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
import random.gba.randomizer.RecruitmentRandomizer;
import random.gba.randomizer.service.ClassAdjustmentDto;
import random.gba.randomizer.service.GBASlotAdjustmentService;
import random.gba.randomizer.service.GBATextReplacementService;
import random.gba.randomizer.service.ItemAssignmentService;
import random.gba.randomizer.shuffling.data.GBAFEPortraitData;
import random.gba.randomizer.shuffling.data.PortraitFormat;
import random.general.PoolDistributor;
import ui.model.CharacterShufflingOptions;
import ui.model.CharacterShufflingOptions.ShuffleLevelingMode;
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

    private GameType type;
    private CharacterDataLoader characterData;
    private TextLoader textData;
    private Random rng;
    private FileHandler fileHandler;
    private PortraitDataLoader portraitData;
    private FreeSpaceManager freeSpace;
    private ChapterLoader chapterData;
    private ClassDataLoader classData;
    private CharacterShufflingOptions options;
    private ItemAssignmentOptions inventoryOptions;
    private ItemDataLoader itemData;
    
    static final int rngSalt = 18489;

    public CharacterShuffler(GameType type, CharacterDataLoader characterData, TextLoader textData, Random rng,
                             FileHandler fileHandler, PortraitDataLoader portraitData, FreeSpaceManager freeSpace,
                             ChapterLoader chapterData, ClassDataLoader classData, CharacterShufflingOptions options,
                             ItemAssignmentOptions inventoryOptions, ItemDataLoader itemData) {
        this.type = type;
        this.characterData = characterData;
        this.textData = textData;
        this.rng = rng;
        this.fileHandler = fileHandler;
        this.portraitData = portraitData;
        this.freeSpace = freeSpace;
        this.chapterData = chapterData;
        this.classData = classData;
        this.options = options;
        this.inventoryOptions = inventoryOptions;
        this.itemData = itemData;
    }

    @SuppressWarnings("unused")
    public void shuffleCharacters() {
        // Shuffle in character from the other games
        if (options.getIncludedShuffles().size() != 0) {
            List<GBACrossGameData> availableChars = new ArrayList<GBACrossGameData>();
            for (String includedCharacters : options.getIncludedShuffles()) {
                availableChars.addAll(CharacterImporter.importCharacterDataFromFiles(includedCharacters));
            }

            Map<Boolean, List<GBACrossGameData>> partitions = availableChars.stream().collect(Collectors.partitioningBy(data -> data.forcedSlot != null));
            Map<Integer, GBACrossGameData> forcedSlotMapping = partitions.get(true).stream().collect(Collectors.toMap(chara -> chara.forcedSlot, chara -> chara, (initial, replacement) -> initial));
            shuffleByForcedSlot(forcedSlotMapping);
            shuffleRandomly(partitions.get(false), forcedSlotMapping.keySet());

            GBATextReplacementService.applyChanges(textData);
        }
    }

    private void shuffleByForcedSlot(Map<Integer, GBACrossGameData> forcedSlots) {
        for (Entry<Integer, GBACrossGameData> e : forcedSlots.entrySet()) {
            GBAFECharacterData slot = characterData.characterWithID(e.getKey());
            shuffleImpl(slot, e.getValue());
        }
    }

    private void shuffleRandomly(List<GBACrossGameData> availableChars, Set<Integer> forcedSlots) {
        PoolDistributor<GBACrossGameData> distributor = new PoolDistributor<>();
        distributor.addAll(availableChars);

        // Don't include playable post game characters into the ones that could be replaced,
        // as most files probably won't be played enough to unlock those anyway.
        List<GBAFECharacterData> characterPool = new ArrayList<GBAFECharacterData>(
                characterData.canonicalPlayableCharacters(false));

        for (GBAFECharacterData slot : characterPool) {

            if (forcedSlots.contains(slot.getID()))
                continue;

            // Determine if the current character should be replaced
            if (options.getChance() < rng.nextInt(100)) {
                continue;
            }

            // The character should be replaced, get a random character to shuffle in
            GBACrossGameData crossGameData = distributor.getRandomItem(rng, true);
            if (crossGameData == null) {
                // If no more character to find, then stop
                break;
            }

            shuffleImpl(slot, crossGameData);
        }
    }

    private void shuffleImpl(GBAFECharacterData slot, GBACrossGameData crossGameData) {
        DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, String.format("Shuffling Character %s into Slot %d, which was originally %s", crossGameData.name, slot.getID(), slot.displayString()));

        // (a) Get a valid class in the target game for what was configured for the character
        int targetClassId = GBACrossGameData.getEquivalentClass(type, crossGameData, classData).getID();

        if (targetClassId == 0) {
            DebugPrinter.error(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, String.format("Couldn't find an applicable class for character %s class %s%n", crossGameData.name,
                    crossGameData.characterClass));
            return;
        }

        // (b) initialize some variables needed later
        GBAFEClassData targetClass = classData.classForID(targetClassId);
        GBAFEClassData sourceClass = classData.classForID(slot.getClassID());
        int slotLevel = slot.getLevel();

        // (c) Insert the portrait for the current character into the rom and repoint the Portrait Data
        try {
            changePortrait(slot, crossGameData);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // (d) Update name and Description
        updateName(slot, crossGameData);

        // Give an option to not change the description to help with keeping track of which character is which
        if (options.shouldChangeDescription()) {
            // [0x1] = new line
            // [X] = end of text segment?
            textData.setStringAtIndex(slot.getDescriptionIndex(), String.format("%s[0x1]%s[X]", crossGameData.description1, crossGameData.description2));
        }

        for (GBAFECharacterData linkedSlot : characterData.linkedCharactersForCharacter(slot)) {
            linkedSlot.setGrowths(crossGameData.growths);
            linkedSlot.setConstitution(crossGameData.constitution);

            // (e) Update the bases, and potentially auto level the Character to the level of the slot.
            // Due to Promotion / Demotion, the output of the targetClass might be different from what was passed into this method
            targetClass = updateBases(linkedSlot, crossGameData, targetClassId,
                    targetClass, sourceClass, slotLevel);
            targetClassId = targetClass.getID();

            updateWeaponRanks(linkedSlot, crossGameData, sourceClass, targetClass, rng);

            // (f) Update the class for all the slots of the character
            updateUnitInChapter(linkedSlot, crossGameData, targetClassId);

            // (g) give the Unit new items to use
            ItemAssignmentService.assignNewItems(characterData, linkedSlot, targetClass, chapterData, inventoryOptions, type, rng, textData, classData, itemData);
        }
    }


    private void updateName(GBAFECharacterData slot, GBACrossGameData crossGameData) {
        // Save the old name for text replacement
        String oldName = textData.getStringAtIndex(slot.getNameIndex(), true).trim();
        GBATextReplacementService.enqueueNameUpdate(textData, oldName, crossGameData.name);
    }

    /**
     * Sets the configured Weapon ranks (clamped to ensure it's between 0 and 255) for the given character
     */
    private void updateWeaponRanks(GBAFECharacterData character, GBACrossGameData crossGameData, GBAFEClassData sourceClass, GBAFEClassData targetClass, Random rng) {
        if (ShuffleLevelingMode.AUTOLEVEL.equals(options.getLevelingMode())) {
            // Adjust the weapon ranks to the new class
            GBASlotAdjustmentService.transferWeaponRanks(character, sourceClass, targetClass, type, rng);
        } else {
            // If we don't auto level transfer 1 to 1 while making sure it doesn't over or underflow
            character.setSwordRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[0], 0, 255));
            character.setLanceRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[1], 0, 255));
            character.setAxeRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[2], 0, 255));
            character.setBowRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[3], 0, 255));
            character.setStaffRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[4], 0, 255));
            character.setAnimaRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[5], 0, 255));
            character.setLightRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[6], 0, 255));
            character.setDarkRank(WhyDoesJavaNotHaveThese.clamp(crossGameData.weaponRanks[7], 0, 255));
        }


    }

    /**
     * Update the class and stats for the slot based on the configured personal bases and potentially autolevels and promotion bonuses
     */
    private GBAFEClassData updateBases(GBAFECharacterData slot, GBACrossGameData chara, int targetClassId,
                                              GBAFEClassData targetClass, GBAFEClassData sourceClass, int slotLevel) {
        if (ui.model.CharacterShufflingOptions.ShuffleLevelingMode.UNCHANGED.equals(options.getLevelingMode())) {
            slot.setBases(chara.bases);
        } else if (ui.model.CharacterShufflingOptions.ShuffleLevelingMode.AUTOLEVEL.equals(options.getLevelingMode())) {

            boolean shouldBePromoted = classData.isPromotedClass(slot.getClassID());

            slot.setClassID(targetClassId);

            boolean isPromoted = classData.isPromotedClass(targetClassId);

            // Decide Target Class / Promotions or Demotions / Number of Autolevels
            ClassAdjustmentDto adjustmentDAO = GBASlotAdjustmentService.handleClassAdjustment(slotLevel,
                    chara.level, shouldBePromoted, isPromoted, rng, classData, null, targetClass, slot,
                    sourceClass, null, textData, DebugPrinter.Key.GBA_CHARACTER_SHUFFLING);
            targetClass = adjustmentDAO.targetClass;
            slot.setClassID(targetClassId);

            // Calculate the auto leveled personal bases
            GBAFEStatDto newPersonalBases = GBASlotAdjustmentService.autolevel(chara.bases, chara.growths,
                    adjustmentDAO.promoBonuses, adjustmentDAO.levelAdjustment, targetClass, DebugPrinter.Key.GBA_CHARACTER_SHUFFLING);

            slot.setBases(newPersonalBases);
        }
        return targetClass;
    }

    /**
     * Updates the class of the character being replaced to the one of the new
     * character
     *
     * @param character   - the current Character being changed
     * @param replacement - the character being shuffled in
     * @param targetClass - the target Class that the character should now have.
     */
    private void updateUnitInChapter(GBAFECharacterData character, GBACrossGameData replacement, int targetClass) {
        character.setClassID(targetClass);

        for (GBAFEChapterData chapter : chapterData.allChapters()) {
            for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
                if (chapterUnit.getCharacterNumber() == character.getID()) {
                    chapterUnit.setStartingClass(targetClass);

                    // If the user selects that the Units should be inserted as they are (which would be dumb) then update the level.
                    if (ui.model.CharacterShufflingOptions.ShuffleLevelingMode.UNCHANGED.equals(options.getLevelingMode())) {
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
     * @param chara        - the Character that will get randomized into the rom
     */
    private void changePortrait(GBAFECharacterData character, GBACrossGameData chara) throws IOException {
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