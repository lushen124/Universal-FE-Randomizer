package random.gba.randomizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe6.FE6SpellAnimationCollection;
import fedata.gba.fe7.FE7Data;
import fedata.gba.general.WeaponType;
import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import io.UPSPatcher;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.ItemDataLoader.AdditionalData;
import random.gba.loader.PaletteLoader;
import random.gba.loader.PortraitDataLoader;
import random.gba.loader.TextLoader;
import ui.model.BaseOptions;
import ui.model.ClassOptions;
import ui.model.EnemyOptions;
import ui.model.GrowthOptions;
import ui.model.ItemAssignmentOptions;
import ui.model.MiscellaneousOptions;
import ui.model.OtherCharacterOptions;
import ui.model.RecruitmentOptions;
import ui.model.WeaponOptions;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FindAndReplace;
import util.FreeSpaceManager;
import util.GBAImageCodec;
import util.SeedGenerator;
import util.WhyDoesJavaNotHaveThese;
import util.OptionRecorder.GBAOptionBundle;

/**
 * Class with FE6 Specific implementations needed for GBA Randomization
 */
public class FE6Randomizer extends AbstractGBARandomizer {

	public FE6Randomizer(String sourcePath, String targetPath, GameType gameType, DiffCompiler diffs,
			GBAOptionBundle options, String seed) {
		super(sourcePath, targetPath, gameType, diffs, options, seed, FE6Data.FriendlyName);
	}

	@Override
	public void runDataloaders() {
		sourceFileHandler.setAppliedDiffs(diffCompiler);

		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE6, FE6Data.InternalFreeRange, sourceFileHandler);
		updateStatusString("Loading Text...");
		updateProgress(0.05);
		textData = new TextLoader(FEBase.GameType.FE6, FE6Data.textProvider, sourceFileHandler);
		if (miscOptions.applyEnglishPatch) {
			textData.allowTextChanges = true;
		}
		updateStatusString("Loading Portrait Data...");
		updateProgress(0.07);
		portraitData = new PortraitDataLoader(FE6Data.shufflingDataProvider, sourceFileHandler);
		updateStatusString("Loading Character Data...");
		updateProgress(0.10);
		charData = new CharacterDataLoader(FE6Data.characterProvider, sourceFileHandler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.15);
		classData = new ClassDataLoader(FE6Data.classProvider, sourceFileHandler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.20);
		chapterData = new ChapterLoader(FEBase.GameType.FE6, sourceFileHandler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.25);
		itemData = new ItemDataLoader(FE6Data.itemProvider, sourceFileHandler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE6, sourceFileHandler, charData, classData);
		updateStatusString("Loading Statboost Data...");

		sourceFileHandler.clearAppliedDiffs();
	}

	@Override
	protected void makeFinalAdjustments() {
		Random rng = new Random(SeedGenerator.generateSeedValue(seedString, 1));
		applyPaletteFixes();
		applyPromotionFix();
		removeLockpicksFromEnemies();
		ensureHealersHaveStaves(rng);
		createSpecialLordClasses();
		createPrfs(rng);
	}

	@Override
	public void recordNotes() {
		recordKeeper.addNote("Characters that randomize into the Soldier class can promote using a Knight's Crest.");
		recordKeeper.addNote("Characters that randomize into the Roy Lord class can promote using a Knight's Crest.");
	}

	protected void removeLockpicksFromEnemies() {
		// Make sure no non-playable non-thief units have lock picks, as they will
		// softlock the game when the AI gets a hold of them.
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				FE6Data.CharacterClass charClass = FE6Data.CharacterClass.valueOf(chapterUnit.getStartingClass());
				if (!FE6Data.CharacterClass.allThiefClasses.contains(charClass)
						&& (chapterUnit.isNPC() || chapterUnit.isEnemy())) {
					chapterUnit.removeItem(FE6Data.Item.LOCKPICK.ID);
				}
			}
		}
	}

	protected void applyEnglishPatch() {
		String tempPath = null;
		if (miscOptions.applyEnglishPatch) {
			updateStatusString("Applying English Patch...");
			updateProgress(0.05);

			tempPath = new String(targetPath).concat(".tmp");

			try {
				Boolean success = UPSPatcher.applyUPSPatch("FE6Localization_v1.1.ups", sourcePath, tempPath, null);
				if (!success) {
					notifyError("Failed to apply translation patch.");
					return;
				}
			} catch (Exception e) {
				notifyError("Encountered error while applying patch." + buildErrorMessage(e));
				return;
			}

			try {
				sourceFileHandler = new FileHandler(tempPath);
			} catch (IOException e1) {
				System.err.println("Unable to open post-patched file.");
				e1.printStackTrace();
				notifyError("Failed to apply translation patch.");
				return;
			}
		}
	}

	@Override
	protected void createSpecialLordClasses() {
		GBAFECharacterData roy = charData.characterWithID(FE6Data.Character.ROY.ID);

		int oldRoyClassID = roy.getClassID();

		GBAFEClassData newRoyClass = classData.createLordClassBasedOnClass(classData.classForID(oldRoyClassID));

		roy.setClassID(newRoyClass.getID());

		// Add his new class to any effectiveness tables.
		List<AdditionalData> effectivenesses = itemData.effectivenessArraysForClassID(oldRoyClassID);
		for (AdditionalData effectiveness : effectivenesses) {
			itemData.addClassIDToEffectiveness(effectiveness, newRoyClass.getID());
		}

		// Incidentally, Roy doesn't need a promotion item, because his promotion is
		// entirely scripted without any items.

		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.getCharacterNumber() == FE6Data.Character.ROY.ID) {
					if (unit.getStartingClass() == oldRoyClassID) {
						unit.setStartingClass(newRoyClass.getID());
					}
				}
			}
		}

		long mapSpriteTableOffset = FileReadHelper.readAddress(sourceFileHandler, FE6Data.ClassMapSpriteTablePointer);
		byte[] spriteTable = sourceFileHandler.readBytesAtOffset(mapSpriteTableOffset,
				FE6Data.BytesPerMapSpriteTableEntry * FE6Data.NumberOfMapSpriteEntries);
		long newSpriteTableOffset = freeSpace.setValue(spriteTable, "Repointed Sprite Table", true);
		freeSpace.setValue(WhyDoesJavaNotHaveThese.subArray(spriteTable, (oldRoyClassID - 1) * 8, 8),
				"Roy Map Sprite Entry");
		diffCompiler.findAndReplace(new FindAndReplace(WhyDoesJavaNotHaveThese.bytesFromAddress(mapSpriteTableOffset),
				WhyDoesJavaNotHaveThese.bytesFromAddress(newSpriteTableOffset), true));
	}

	@Override
	protected void createPrfs(Random rng) {
		if ((classes == null || !classes.createPrfs) || (recruitOptions == null || !recruitOptions.createPrfs)) {
			return;
		}

		boolean unbreakablePrfs = ((classes != null && classes.unbreakablePrfs)
				|| (recruitOptions != null && recruitOptions.createPrfs));

		GBAFECharacterData roy = charData.characterWithID(FE6Data.Character.ROY.ID);
		GBAFEClassData royClass = classData.classForID(roy.getClassID());
		List<WeaponType> royWeaponTypes = classData.usableTypesForClass(royClass);
		royWeaponTypes.remove(WeaponType.STAFF);
		if (!royWeaponTypes.isEmpty()) {
			WeaponType selectedType = royWeaponTypes.get(rng.nextInt(royWeaponTypes.size()));
			String iconName = null;
			String weaponName = null;
			switch (selectedType) {
			case SWORD:
				weaponName = "Sun Sword";
				iconName = "weaponIcons/SunSword.png";
				break;
			case LANCE:
				weaponName = "Sea Spear";
				iconName = "weaponIcons/SeaSpear.png";
				break;
			case AXE:
				weaponName = "Gaea Splitter";
				iconName = "weaponIcons/EarthSplitter.png";
				break;
			case BOW:
				weaponName = "Gust Shot";
				iconName = "weaponIcons/GustShot.png";
				break;
			case ANIMA:
				weaponName = "Fierce Flame";
				iconName = "weaponIcons/FierceFlame.png";
				break;
			case DARK:
				weaponName = "Dark Miasma";
				iconName = "weaponIcons/DarkMiasma.png";
				break;
			case LIGHT:
				weaponName = "Holy Light";
				iconName = "weaponIcons/HolyLight.png";
				break;
			default:
				break;
			}

			if (weaponName != null && iconName != null) {
				// Replace the old icon.
				byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(iconName,
						GBAImageCodec.gbaWeaponColorPalette);
				if (iconData == null) {
					notifyError("Invalid image data for icon " + iconName);
				}
				diffCompiler.addDiff(new Diff(0xFC400, iconData.length, iconData, null));

				// We're going to reuse some indices already used by the watch staff. While the
				// name's index isn't available, both its
				// description and use item description are available.
				textData.setStringAtIndex(0x5FE, weaponName + "[X]");
				// TODO: Maybe give it a description string?

				GBAFEItemData itemToReplace = itemData.itemWithID(FE6Data.Item.UNUSED_WATCH_STAFF.ID);
				itemToReplace.turnIntoLordWeapon(roy.getID(), 0x5FE, 0x0, selectedType, unbreakablePrfs,
						royClass.getCON() + roy.getConstitution(), itemData.itemWithID(FE6Data.Item.RAPIER.ID),
						itemData, freeSpace);

				switch (selectedType) {
				case SWORD:
				case LANCE:
				case AXE:
					itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2,
							FE6SpellAnimationCollection.Animation.NONE2.value,
							FE6SpellAnimationCollection.Flash.WHITE.value);
					break;
				case BOW:
					itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2,
							FE6SpellAnimationCollection.Animation.ARROW.value,
							FE6SpellAnimationCollection.Flash.WHITE.value);
					break;
				case ANIMA:
					itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2,
							FE6SpellAnimationCollection.Animation.ELFIRE.value,
							FE6SpellAnimationCollection.Flash.RED.value);
					break;
				case DARK:
					itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2,
							FE6SpellAnimationCollection.Animation.FLUX.value,
							FE6SpellAnimationCollection.Flash.DARK.value);
					break;
				case LIGHT:
					itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2,
							FE6SpellAnimationCollection.Animation.DIVINE.value,
							FE6SpellAnimationCollection.Flash.YELLOW.value);
					break;
				default:
					// No animation needed here.
					break;
				}

				// Make sure the old lord class, if anybody randomizes into it, can't use this
				// weapon.
				GBAFEClassData oldLordClass = classData.classForID(FE6Data.CharacterClass.LORD.ID);
				oldLordClass.removeLordLocks();
				GBAFEClassData oldPromotedLordClass = classData.classForID(FE6Data.CharacterClass.MASTER_LORD.ID);
				oldPromotedLordClass.removeLordLocks();

				// Make sure Roy himself can.
				roy.enableWeaponLock(FE6Data.CharacterAndClassAbility3Mask.RAPIER_LOCK.getValue());

				for (GBAFEChapterData chapter : chapterData.allChapters()) {
					for (GBAFEChapterUnitData unit : chapter.allUnits()) {
						// Give Roy the weapon when he shows up.
						if (unit.getCharacterNumber() == roy.getID()) {
							unit.giveItem(itemToReplace.getID());
						}

						// Replace any Rapiers with iron swords, since we need to reuse the same lock.
						if (unit.hasItem(FE6Data.Item.RAPIER.ID)) {
							unit.removeItem(FE6Data.Item.RAPIER.ID);
							unit.giveItem(FE6Data.Item.IRON_SWORD.ID);
						}
					}
				}
			}
		}
	}

	@Override
	protected void applySingleRN() {
		diffCompiler.addDiff(new Diff(0xE6A, 4, new byte[] { (byte) 0xC0, (byte) 0x46, (byte) 0xC0, (byte) 0x46 },
				new byte[] { (byte) 0xFF, (byte) 0xF7, (byte) 0xBB, (byte) 0xFF }));
	}

	@Override
	protected void gameSpecificDiffCompilations() {
		//N/A
	}
	
	@Override
	protected void applyUpsPatches() {
		applyEnglishPatch();
	}

	@Override
	protected void applyCasualMode() {
		diffCompiler.addDiff(new Diff(0x17BEA, 1, new byte[] {(byte)0x09}, new byte[] {(byte)0x05}));
	}

	@Override
	protected void applyParagonMode() {
		// Combat EXP
		diffCompiler.addDiff(new Diff(0x258D0, 24, 
				new byte[] {(byte)0x24, (byte)0x18, (byte)0x64, (byte)0x00, (byte)0x64, (byte)0x2C, (byte)0x00, (byte)0xDD,
						    (byte)0x64, (byte)0x24, (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0xDA, (byte)0x00, (byte)0x24,
						    (byte)0x20, (byte)0x1C, (byte)0x70, (byte)0xBC, (byte)0x02, (byte)0xBC, (byte)0x08, (byte)0x47}, 
				new byte[] {(byte)0x24, (byte)0x18, (byte)0x64, (byte)0x2C, (byte)0x00, (byte)0xDD, (byte)0x64, (byte)0x24, 
						    (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0xDA, (byte)0x00, (byte)0x24, (byte)0x20, (byte)0x1C,
						    (byte)0x70, (byte)0xBC, (byte)0x02, (byte)0xBC, (byte)0x08, (byte)0x47, (byte)0x00, (byte)0x00}));
		diffCompiler.addDiff(new Diff(0x258BC, 2,
				new byte[] {(byte)0x11, (byte)0xE0},
				new byte[] {(byte)0x10, (byte)0xE0}));
		diffCompiler.addDiff(new Diff(0x258AA, 2,
				new byte[] {(byte)0x1A, (byte)0xE0},
				new byte[] {(byte)0x19, (byte)0xE0}));
		diffCompiler.addDiff(new Diff(0x258BA, 2,
				new byte[] {(byte)0x02, (byte)0x20},
				new byte[] {(byte)0x01, (byte)0x20}));
		
		// Staff EXP
		diffCompiler.addDiff(new Diff(0x25988, 12,
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x01, (byte)0xD0, (byte)0x52, (byte)0x10, (byte)0x00, (byte)0x00,
						    (byte)0x52, (byte)0x00, (byte)0x64, (byte)0x2A},
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x02, (byte)0xD0, (byte)0xD0, (byte)0x0F, (byte)0x10, (byte)0x18,
						    (byte)0x42, (byte)0x10, (byte)0x64, (byte)0x2A}));
		
		// Steal/Dance EXP
		diffCompiler.addDiff(new Diff(0x259C6, 8,
				new byte[] {(byte)0x14, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x18, (byte)0x1C, (byte)0x14, (byte)0x30},
				new byte[] {(byte)0x0A, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x18, (byte)0x1C, (byte)0x0A, (byte)0x30}));		
	}

	@Override
	protected void applyRenegadeMode() {
		// Combat EXP
		diffCompiler.addDiff(new Diff(0x258D0, 24, 
				new byte[] {(byte)0x24, (byte)0x18, (byte)0x64, (byte)0x10, (byte)0x64, (byte)0x2C, (byte)0x00, (byte)0xDD,
						    (byte)0x64, (byte)0x24, (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0xDA, (byte)0x00, (byte)0x24,
						    (byte)0x20, (byte)0x1C, (byte)0x70, (byte)0xBC, (byte)0x02, (byte)0xBC, (byte)0x08, (byte)0x47}, 
				new byte[] {(byte)0x24, (byte)0x18, (byte)0x64, (byte)0x2C, (byte)0x00, (byte)0xDD, (byte)0x64, (byte)0x24, 
						    (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0xDA, (byte)0x00, (byte)0x24, (byte)0x20, (byte)0x1C,
						    (byte)0x70, (byte)0xBC, (byte)0x02, (byte)0xBC, (byte)0x08, (byte)0x47, (byte)0x00, (byte)0x00}));
		diffCompiler.addDiff(new Diff(0x258BC, 2,
				new byte[] {(byte)0x11, (byte)0xE0},
				new byte[] {(byte)0x10, (byte)0xE0}));
		diffCompiler.addDiff(new Diff(0x258AA, 2,
				new byte[] {(byte)0x1A, (byte)0xE0},
				new byte[] {(byte)0x19, (byte)0xE0}));
		
		// Staff EXP
		diffCompiler.addDiff(new Diff(0x25988, 12,
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x01, (byte)0xD0, (byte)0x52, (byte)0x10, (byte)0x00, (byte)0x00,
						    (byte)0x52, (byte)0x10, (byte)0x64, (byte)0x2A},
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x02, (byte)0xD0, (byte)0xD0, (byte)0x0F, (byte)0x10, (byte)0x18,
						    (byte)0x42, (byte)0x10, (byte)0x64, (byte)0x2A}));
		
		// Steal/Dance EXP
		diffCompiler.addDiff(new Diff(0x259C6, 8,
				new byte[] {(byte)0x05, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x18, (byte)0x1C, (byte)0x05, (byte)0x30},
				new byte[] {(byte)0x0A, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x18, (byte)0x1C, (byte)0x0A, (byte)0x30}));		
	}
}
