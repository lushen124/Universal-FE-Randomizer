package random.gba.randomizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapSpriteData;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe7.FE7SpellAnimationCollection;
import fedata.gba.general.WeaponType;
import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
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
 * Class with FE7 Specific implementations needed for GBA Randomization
 */
public class FE7Randomizer extends AbstractGBARandomizer {

	public FE7Randomizer(String sourcePath, String targetPath, GameType gameType, DiffCompiler diffs,
			GBAOptionBundle options, String seed) {
		super(sourcePath, targetPath, gameType, diffs, options, seed, FE7Data.FriendlyName);
	}

	@Override
	public void runDataloaders() {
		sourceFileHandler.setAppliedDiffs(diffCompiler);

		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE7, FE7Data.InternalFreeRange, sourceFileHandler);
		updateStatusString("Loading Text...");
		updateProgress(0.05);
		textData = new TextLoader(FEBase.GameType.FE7, FE7Data.textProvider, sourceFileHandler);
		textData.allowTextChanges = true;
		updateStatusString("Loading Portrait Data...");
		updateProgress(0.07);
		portraitData = new PortraitDataLoader(FE7Data.shufflingDataProvider, sourceFileHandler);
		updateStatusString("Loading Character Data...");
		updateProgress(0.10);
		charData = new CharacterDataLoader(FE7Data.characterProvider, sourceFileHandler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.15);
		classData = new ClassDataLoader(FE7Data.classProvider, sourceFileHandler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.20);
		chapterData = new ChapterLoader(FEBase.GameType.FE7, sourceFileHandler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.25);
		itemData = new ItemDataLoader(FE7Data.itemProvider, sourceFileHandler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE7, sourceFileHandler, charData, classData);
		updateStatusString("Loading Statboost Data...");

		sourceFileHandler.clearAppliedDiffs();
	}

	@Override
	protected void makeFinalAdjustments() {
		Random rng = new Random(SeedGenerator.generateSeedValue(seedString, 1));
		applyPaletteFixes();
		applyPromotionFix();
		applyEmblemBowEffectiveness();
		updateModeSelect();
		fixWorldMapSprites();
		ensureHealersHaveStaves(rng);
		ensureHectorBeatsWire();
		createSpecialLordClasses();
		createPrfs(rng);
	}

	@Override
	public void recordNotes() {
		recordKeeper.addNote(
				"Characters that randomize into the Soldier class can promote using a Knight's Crest or Earth Seal.");
		recordKeeper.addNote(
				"Characters that randomize into the Lyn Lord class can promote using a Hero's Crest or Earth Seal.");
		recordKeeper.addNote(
				"Characters that randomize into the Eliwood Lord class can promote using a Knight's Crest or Earth Seal.");
		recordKeeper.addNote(
				"Characters that randomzie into the Hector Lord class can promote using a Knight's Crest or Earth Seal.");
		recordKeeper.addNote(
				"Characters that randomize into the Corsair class can promote using an Ocean's Seal or Earth Seal.");
		recordKeeper.addNote(
				"Characters that randomize into the Brigand class can promote using a Hero's Crest, Ocean's Seal, or Earth Seal.");
		recordKeeper.addNote("Emblem Bow is now effective against fliers by default.");
	}

	public void ensureHectorBeatsWire() {
		GBAFECharacterData wire = charData.characterWithID(FE7Data.Character.WIRE.ID);
		GBAFECharacterData hector = charData.characterWithID(FE7Data.Character.HECTOR.ID);

		GBAFEClassData wireClass = classData.classForID(wire.getClassID());
		GBAFEClassData hectorClass = classData.classForID(hector.getClassID());

		GBAFEChapterData ch11 = chapterData.chapterWithID(FE7Data.ChapterPointer.CHAPTER_11_H.chapterID);
		GBAFEItemData wireWeapon = null;
		GBAFEItemData hectorWeapon = null;
		for (GBAFEChapterUnitData unit : ch11.allUnits()) {
			if (unit.getCharacterNumber() == wire.getID()) {
				wireWeapon = chapterData.getWeaponForUnit(unit, itemData);
			}
			if (unit.getCharacterNumber() == hector.getID()) {
				hectorWeapon = chapterData.getWeaponForUnit(unit, itemData);
			}
		}

		// Simulate numbers for Hector v. Wire.
		int hectorHP = hector.getBaseHP() + hectorClass.getBaseHP();
		int hectorSPD = hector.getBaseSPD() + hectorClass.getBaseSPD();
		int hectorCON = hector.getConstitution() + hectorClass.getCON();
		if (wireWeapon.getType().isPhysical()) { // Wire attacks Hector.
			int hectorDEF = hector.getBaseDEF() + hectorClass.getBaseDEF();
			int wireSTR = wire.getBaseSTR() + wireClass.getBaseSTR();
			int wireSPD = wire.getBaseSPD() + wireClass.getBaseSPD();
			int wireCON = wire.getConstitution() + wireClass.getCON();

			int hectorAS = hectorSPD + Math.min(0, hectorCON - hectorWeapon.getWeight());
			int wireATK = wireSTR + wireWeapon.getMight()
					+ (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
			int wireAS = wireSPD + Math.min(0, wireCON - wireWeapon.getWeight());

			boolean wireDoublesHector = hectorAS < wireAS - 3;
			int damageDealtToHector = wireATK - hectorDEF;
			int totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);

			// Hector should not be two-rounded (unless buffing boss weapons is on).
			while (totalDamageDealt * (enemies.improveBossWeapons ? 2 : 3) > hectorHP) {
				// If he doubles, get rid of that first.
				if (wireDoublesHector && (wireCON > 1 || wireSPD > 0)) {
					if (wireCON > 1) {
						wire.setConstitution(wire.getConstitution() - 1);
						wireCON = wire.getConstitution() + wireClass.getCON();
					} else if (wireSPD > 0) {
						wire.setBaseSPD(wire.getBaseSPD() - 1);
						wireSPD = wire.getBaseSPD() + wireClass.getBaseSPD();
					}
					wireAS = wireSPD + Math.min(0, wireCON - wireWeapon.getWeight());
					wireDoublesHector = hectorAS < wireAS - 3;
					totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);
				} else if (wireSTR > 0) { // Nerf Wire's damage output next.
					wire.setBaseSTR(wire.getBaseSTR() - 1);
					wireSTR = wire.getBaseSTR() + wireClass.getBaseSTR();
					wireATK = wireSTR + wireWeapon.getMight()
							+ (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
					damageDealtToHector = wireATK - hectorDEF;
					totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);
				} else { // This is a pretty bad Hector if he can't take out a 0 AS, 0 STR Wire. Buff his
							// DEF.
					hector.setBaseDEF(hector.getBaseDEF() + 1);
					hectorDEF = hector.getBaseDEF() + hectorClass.getBaseDEF();
					damageDealtToHector = wireATK - hectorDEF;
					totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);
				}
			}
		} else {
			int hectorRES = hector.getBaseRES() + hectorClass.getBaseRES();
			int wireMAG = wire.getBaseSTR() + wireClass.getBaseSTR();
			int wireSPD = wire.getBaseSPD() + wireClass.getBaseSPD();
			int wireCON = wire.getConstitution() + wireClass.getCON();

			int hectorAS = Math.max(0, hectorSPD + Math.min(0, hectorCON - hectorWeapon.getWeight()));
			int wireATK = wireMAG + wireWeapon.getMight()
					+ (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
			int wireAS = Math.max(0, wireSPD + Math.min(0, wireCON - wireWeapon.getWeight()));

			boolean wireDoublesHector = hectorAS < wireAS - 3;
			int damageDealtToHector = wireATK - hectorRES;
			int totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);

			// Hector should not be two-rounded.
			while (totalDamageDealt * (enemies.improveBossWeapons ? 2 : 3) > hectorHP) {
				// If he doubles, get rid of that first.
				if (wireDoublesHector && (wireCON > 1 || wireSPD > 0)) {
					if (wireCON > 1) {
						wire.setConstitution(wire.getConstitution() - 1);
						wireCON = wire.getConstitution() + wireClass.getCON();
					} else if (wireSPD > 0) {
						wire.setBaseSPD(wire.getBaseSPD() - 1);
						wireSPD = wire.getBaseSPD() + wireClass.getBaseSPD();
					}
					wireAS = Math.max(0, wireSPD + Math.min(0, wireCON - wireWeapon.getWeight()));
					wireDoublesHector = hectorAS < wireAS - 3;
					totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);
				} else if (wireMAG > 0) { // Nerf Wire's damage output next.
					wire.setBaseSTR(wire.getBaseSTR() - 1);
					wireMAG = wire.getBaseSTR() + wireClass.getBaseSTR();
					wireATK = wireMAG + wireWeapon.getMight()
							+ (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
					damageDealtToHector = wireATK - hectorRES;
					totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);
				} else { // This is a pretty bad Hector if he can't take out a 0 AS, 0 STR Wire. Buff his
							// RES.
					hector.setBaseRES(hector.getBaseRES() + 1);
					hectorRES = hector.getBaseRES() + hectorClass.getBaseRES();
					damageDealtToHector = wireATK - hectorRES;
					totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);
				}
			}
		}

		// Hector attacks Wire
		int wireHP = wire.getBaseHP() + wireClass.getBaseHP();
		int wireSPD = wire.getBaseSPD() + wireClass.getBaseSPD();
		int wireCON = wire.getConstitution() + wireClass.getCON();
		if (hectorWeapon.getType().isPhysical()) {
			int wireDEF = wire.getBaseDEF() + wireClass.getBaseDEF();
			int hectorSTR = hector.getBaseSTR() + hectorClass.getBaseSTR();

			int hectorAS = Math.max(0, hectorSPD + Math.min(0, hectorCON - hectorWeapon.getWeight()));
			int hectorATK = hectorSTR + hectorWeapon.getMight()
					- (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
			int wireAS = Math.max(0, wireSPD + Math.min(0, wireCON - wireWeapon.getWeight()));

			boolean hectorDoublesWire = wireAS < hectorAS - 3;
			int damageDealtToWire = hectorATK - wireDEF;
			int totalDamageDealt = damageDealtToWire + (hectorDoublesWire ? damageDealtToWire : 0);

			// This fight shouldn't take more than 3 rounds.
			int i = 0;
			while (wireHP > totalDamageDealt * 3) {
				// Lower his defense first.
				if (wireDEF > 0) {
					wire.setBaseDEF(wire.getBaseDEF() - 1);
					wireDEF = wire.getBaseDEF() + wireClass.getBaseDEF();
					damageDealtToWire = hectorATK - wireDEF;
					totalDamageDealt = damageDealtToWire + (hectorDoublesWire ? damageDealtToWire : 0);
				} else { // Alternate between increasing Hector's SPD and ATK.
					if (i++ % 2 == 0) {
						if (hectorWeapon.getWeight() > hectorCON) { // Try raising CON before we start raising SPD.
							hector.setConstitution(hector.getConstitution() + 1);
							hectorCON = hector.getConstitution() + hectorClass.getCON();
						} else {
							hector.setBaseSPD(hector.getBaseSPD() + 1);
							hectorSPD = hector.getBaseSPD() + hectorClass.getBaseSPD();
						}
					} else {
						hector.setBaseSTR(hector.getBaseSTR() + 1);
						hectorSTR = hector.getBaseSTR() + hectorClass.getBaseSTR();
					}

					hectorAS = Math.max(0, hectorSPD + Math.min(0, hectorCON - hectorWeapon.getWeight()));
					hectorATK = hectorSTR + hectorWeapon.getMight()
							- (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
					hectorDoublesWire = wireAS < hectorAS - 3;
					damageDealtToWire = hectorATK - wireDEF;
					totalDamageDealt = damageDealtToWire + (hectorDoublesWire ? damageDealtToWire : 0);
				}
			}
		} else {
			int wireRES = wire.getBaseRES() + wireClass.getBaseRES();
			int hectorSTR = hector.getBaseSTR() + hectorClass.getBaseSTR();

			int hectorAS = Math.max(0, hectorSPD + Math.min(0, hectorCON - hectorWeapon.getWeight()));
			int hectorATK = hectorSTR + hectorWeapon.getMight()
					- (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
			int wireAS = Math.max(0, wireSPD + Math.min(0, wireCON - wireWeapon.getWeight()));

			boolean hectorDoublesWire = wireAS < hectorAS - 3;
			int damageDealtToWire = hectorATK - wireRES;
			int totalDamageDealt = damageDealtToWire + (hectorDoublesWire ? damageDealtToWire : 0);

			// This fight shouldn't take more than 3 rounds.
			int i = 0;
			while (wireHP > totalDamageDealt * 3) {
				// Lower his defense first.
				if (wireRES > 0) {
					wire.setBaseRES(wire.getBaseRES() - 1);
					wireRES = wire.getBaseRES() + wireClass.getBaseRES();
					damageDealtToWire = hectorATK - wireRES;
					totalDamageDealt = damageDealtToWire + (hectorDoublesWire ? damageDealtToWire : 0);
				} else { // Alternate between increasing Hector's SPD and ATK.
					if (i++ % 2 == 0) {
						if (hectorWeapon.getWeight() > hectorCON) { // Try raising CON before we start raising SPD.
							hector.setConstitution(hector.getConstitution() + 1);
							hectorCON = hector.getConstitution() + hectorClass.getCON();
						} else {
							hector.setBaseSPD(hector.getBaseSPD() + 1);
							hectorSPD = hector.getBaseSPD() + hectorClass.getBaseSPD();
						}
					} else {
						hector.setBaseSTR(hector.getBaseSTR() + 1);
						hectorSTR = hector.getBaseSTR() + hectorClass.getBaseSTR();
					}

					hectorAS = Math.max(0, hectorSPD + Math.min(0, hectorCON - hectorWeapon.getWeight()));
					hectorATK = hectorSTR + hectorWeapon.getMight()
							- (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
					hectorDoublesWire = wireAS < hectorAS - 3;
					damageDealtToWire = hectorATK - wireRES;
					totalDamageDealt = damageDealtToWire + (hectorDoublesWire ? damageDealtToWire : 0);
				}
			}
		}

		// Make sure Hector has at least 5 (assuming boss weapons are buffed, as this
		// gives them S rank) + Wire's SKL/2 Luck to prevent crits.
		hector.setBaseLCK(Math.max(hector.getBaseLCK(),
				(enemies.improveBossWeapons ? 5 : 0) + (wire.getBaseSKL() + wireClass.getBaseSKL()) / 2));
	}

	@Override
	protected void createSpecialLordClasses() {
		GBAFECharacterData lyn = charData.characterWithID(FE7Data.Character.LYN.ID);
		GBAFECharacterData tutorialLyn = charData.characterWithID(FE7Data.Character.LYN_TUTORIAL.ID);
		GBAFECharacterData eliwood = charData.characterWithID(FE7Data.Character.ELIWOOD.ID);
		GBAFECharacterData hector = charData.characterWithID(FE7Data.Character.HECTOR.ID);

		int oldLynClassID = lyn.getClassID();
		int oldEliwoodClassID = eliwood.getClassID();
		int oldHectorClassID = hector.getClassID();

		GBAFEClassData newLynClass = classData.createLordClassBasedOnClass(classData.classForID(lyn.getClassID()));
		GBAFEClassData newEliwoodClass = classData
				.createLordClassBasedOnClass(classData.classForID(eliwood.getClassID()));
		GBAFEClassData newHectorClass = classData
				.createLordClassBasedOnClass(classData.classForID(hector.getClassID()));

		lyn.setClassID(newLynClass.getID());
		tutorialLyn.setClassID(newLynClass.getID());
		eliwood.setClassID(newEliwoodClass.getID());
		hector.setClassID(newHectorClass.getID());

		// Add new classes to any effectiveness tables.
		List<AdditionalData> effectivenesses = itemData.effectivenessArraysForClassID(oldLynClassID);
		for (AdditionalData effectiveness : effectivenesses) {
			itemData.addClassIDToEffectiveness(effectiveness, newLynClass.getID());
		}
		effectivenesses = itemData.effectivenessArraysForClassID(oldEliwoodClassID);
		for (AdditionalData effectiveness : effectivenesses) {
			itemData.addClassIDToEffectiveness(effectiveness, newEliwoodClass.getID());
		}
		effectivenesses = itemData.effectivenessArraysForClassID(oldHectorClassID);
		for (AdditionalData effectiveness : effectivenesses) {
			itemData.addClassIDToEffectiveness(effectiveness, newHectorClass.getID());
		}

		itemData.replaceClassesForPromotionItem(FE7Data.PromotionItem.ELIWOOD_LYN_HEAVEN_SEAL,
				new ArrayList<Integer>(Arrays.asList(newLynClass.getID(), newEliwoodClass.getID())));
		itemData.replaceClassesForPromotionItem(FE7Data.PromotionItem.HECTOR_LYN_HEAVEN_SEAL,
				new ArrayList<Integer>(Arrays.asList(newHectorClass.getID(), newLynClass.getID())));

		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.getCharacterNumber() == FE7Data.Character.LYN.ID
						|| unit.getCharacterNumber() == FE7Data.Character.LYN_TUTORIAL.ID) {
					if (unit.getStartingClass() == oldLynClassID) {
						unit.setStartingClass(newLynClass.getID());
					}
				} else if (unit.getCharacterNumber() == FE7Data.Character.ELIWOOD.ID) {
					if (unit.getStartingClass() == oldEliwoodClassID) {
						unit.setStartingClass(newEliwoodClass.getID());
					}
				} else if (unit.getCharacterNumber() == FE7Data.Character.HECTOR.ID) {
					if (unit.getStartingClass() == oldHectorClassID) {
						unit.setStartingClass(newHectorClass.getID());
					}
				}
			}
		}

		long mapSpriteTableOffset = FileReadHelper.readAddress(sourceFileHandler, FE7Data.ClassMapSpriteTablePointer);
		byte[] spriteTable = sourceFileHandler.readBytesAtOffset(mapSpriteTableOffset,
				FE7Data.BytesPerMapSpriteTableEntry * FE7Data.NumberOfMapSpriteEntries);
		long newSpriteTableOffset = freeSpace.setValue(spriteTable, "Repointed Sprite Table", true);
		freeSpace.setValue(WhyDoesJavaNotHaveThese.subArray(spriteTable, (oldLynClassID - 1) * 8, 8),
				"Lyn Map Sprite Entry");
		freeSpace.setValue(WhyDoesJavaNotHaveThese.subArray(spriteTable, (oldEliwoodClassID - 1) * 8, 8),
				"Eliwood Map Sprite Entry");
		freeSpace.setValue(WhyDoesJavaNotHaveThese.subArray(spriteTable, (oldHectorClassID - 1) * 8, 8),
				"Hector Map Sprite Entry");
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

		GBAFECharacterData lyn = charData.characterWithID(FE7Data.Character.LYN.ID);
		GBAFECharacterData eliwood = charData.characterWithID(FE7Data.Character.ELIWOOD.ID);
		GBAFECharacterData hector = charData.characterWithID(FE7Data.Character.HECTOR.ID);

		GBAFEClassData lynClass = classData.classForID(lyn.getClassID());
		GBAFEClassData eliwoodClass = classData.classForID(eliwood.getClassID());
		GBAFEClassData hectorClass = classData.classForID(hector.getClassID());

		List<WeaponType> lynWeaponTypes = classData.usableTypesForClass(lynClass);
		List<WeaponType> eliwoodWeaponTypes = classData.usableTypesForClass(eliwoodClass);
		List<WeaponType> hectorWeaponTypes = classData.usableTypesForClass(hectorClass);

		boolean lynLockUsed = false;
		boolean eliwoodLockUsed = false;
		boolean hectorLockUsed = false;
		boolean athosLockUsed = false;
		boolean unusedLockUsed = false;

		lynWeaponTypes.remove(WeaponType.STAFF);
		eliwoodWeaponTypes.remove(WeaponType.STAFF);
		hectorWeaponTypes.remove(WeaponType.STAFF);

		String lynIconName = null;
		String lynWeaponName = null;
		WeaponType lynSelectedType = null;
		String eliwoodIconName = null;
		String eliwoodWeaponName = null;
		WeaponType eliwoodSelectedType = null;
		String hectorIconName = null;
		String hectorWeaponName = null;
		WeaponType hectorSelectedType = null;

		if (!lynWeaponTypes.isEmpty()) {
			// Deprioritize Swords, since we only have 2 locks we can use for it.
			if (lynWeaponTypes.size() > 1) {
				lynWeaponTypes.remove(WeaponType.SWORD);
			}
			lynSelectedType = lynWeaponTypes.get(rng.nextInt(lynWeaponTypes.size()));
			switch (lynSelectedType) {
			case SWORD:
				lynWeaponName = "Summeredge";
				lynIconName = "weaponIcons/Summeredge.png";
				break;
			case LANCE:
				lynWeaponName = "Flare Lance";
				lynIconName = "weaponIcons/FlareLance.png";
				break;
			case AXE:
				lynWeaponName = "Storm Axe";
				lynIconName = "weaponIcons/StormAxe.png";
				break;
			case BOW:
				lynWeaponName = "Summer Shot";
				lynIconName = "weaponIcons/SummerShot.png";
				break;
			case ANIMA:
				lynWeaponName = "Thunderstorm";
				lynIconName = "weaponIcons/Thunderstorm.png";
				break;
			case DARK:
				lynWeaponName = "Summer Void";
				lynIconName = "weaponIcons/SummerVoid.png";
				break;
			case LIGHT:
				lynWeaponName = "Sunlight";
				lynIconName = "weaponIcons/Sunlight.png";
				break;
			default:
				break;
			}
		}

		if (!eliwoodWeaponTypes.isEmpty()) {
			// Deprioritize Swords, since we only have 2 locks we can use for it.
			if (eliwoodWeaponTypes.size() > 1) {
				eliwoodWeaponTypes.remove(WeaponType.SWORD);
			}
			eliwoodSelectedType = eliwoodWeaponTypes.get(rng.nextInt(eliwoodWeaponTypes.size()));
			switch (eliwoodSelectedType) {
			case SWORD:
				eliwoodWeaponName = "Autumn Blade";
				eliwoodIconName = "weaponIcons/AutumnBlade.png";
				break;
			case LANCE:
				eliwoodWeaponName = "Autumn's End";
				eliwoodIconName = "weaponIcons/AutumnsEnd.png";
				break;
			case AXE:
				eliwoodWeaponName = "Harvester";
				eliwoodIconName = "weaponIcons/Harvester.png";
				break;
			case BOW:
				eliwoodWeaponName = "Autumn Shot";
				eliwoodIconName = "weaponIcons/AutumnShot.png";
				break;
			case ANIMA:
				eliwoodWeaponName = "Will o' Wisp";
				eliwoodIconName = "weaponIcons/WillOWisp.png";
				break;
			case DARK:
				eliwoodWeaponName = "Fall Vortex";
				eliwoodIconName = "weaponIcons/FallVortex.png";
				break;
			case LIGHT:
				eliwoodWeaponName = "Starlight";
				eliwoodIconName = "weaponIcons/Starlight.png";
				break;
			default:
				break;
			}
		}

		if (!hectorWeaponTypes.isEmpty()) {
			// Deprioritize Swords, since we only have 2 locks we can use for it.
			if (hectorWeaponTypes.size() > 1) {
				hectorWeaponTypes.remove(WeaponType.SWORD);
			}
			hectorSelectedType = hectorWeaponTypes.get(rng.nextInt(hectorWeaponTypes.size()));
			switch (hectorSelectedType) {
			case SWORD:
				hectorWeaponName = "Winter Sword";
				hectorIconName = "weaponIcons/WinterSword.png";
				break;
			case LANCE:
				hectorWeaponName = "Icicle Lance";
				hectorIconName = "weaponIcons/IcicleLance.png";
				break;
			case AXE:
				hectorWeaponName = "Icy Mallet";
				hectorIconName = "weaponIcons/IcyMallet.png";
				break;
			case BOW:
				hectorWeaponName = "Winter Shot";
				hectorIconName = "weaponIcons/WinterShot.png";
				break;
			case ANIMA:
				hectorWeaponName = "Winter's Howl";
				hectorIconName = "weaponIcons/WintersHowl.png";
				break;
			case DARK:
				hectorWeaponName = "Winter Abyss";
				hectorIconName = "weaponIcons/WinterAbyss.png";
				break;
			case LIGHT:
				hectorWeaponName = "Moonlight";
				hectorIconName = "weaponIcons/Moonlight.png";
				break;
			default:
				break;
			}
		}

		if (lynSelectedType != null && lynWeaponName != null && lynIconName != null) {
			byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(lynIconName,
					GBAImageCodec.gbaWeaponColorPalette);
			if (iconData == null) {
				notifyError("Invalid image data for icon " + lynIconName);
			}
			diffCompiler.addDiff(new Diff(0xCB524, iconData.length, iconData, null));

			textData.setStringAtIndex(0x1225, lynWeaponName + "[X]");
			GBAFEItemData referenceWeapon = itemData.itemWithID(FE7Data.Item.MANI_KATTI.ID);
			GBAFEItemData newWeapon = referenceWeapon.createLordWeapon(FE7Data.Character.LYN.ID, 0x9F, 0x1225, 0x0,
					lynSelectedType, unbreakablePrfs, lynClass.getCON() + lyn.getConstitution(), 0xAD, itemData,
					freeSpace);

			// Lyn's the first, so all weapon locks are unused.
			// Try to use her own lock, assuming it's not a sword or a bow.
			// Remember, Lyn has a tutorial version too.
			GBAFECharacterData lynTutorial = charData.characterWithID(FE7Data.Character.LYN_TUTORIAL.ID);
			if (lynSelectedType == WeaponType.SWORD) {
				athosLockUsed = true;
				newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
				lyn.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.getValue());
				lynTutorial.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.getValue());
			} else if (lynSelectedType == WeaponType.BOW) {
				eliwoodLockUsed = true;
				newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ELIWOOD_LOCK.ID);
				lyn.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ELIWOOD_LOCK.getValue());
				lynTutorial.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ELIWOOD_LOCK.getValue());
			} else {
				lynLockUsed = true;
				newWeapon.setAbility3(FE7Data.Item.Ability3Mask.LYN_LOCK.ID);
				lyn.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.getValue());
				lynTutorial.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.getValue());
			}

			itemData.addNewItem(newWeapon);

			switch (lynSelectedType) {
			case SWORD:
			case LANCE:
			case AXE:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.NONE2.value,
						FE7SpellAnimationCollection.Flash.WHITE.value);
				break;
			case BOW:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.ARROW.value,
						FE7SpellAnimationCollection.Flash.WHITE.value);
				break;
			case ANIMA:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.THUNDER.value,
						FE7SpellAnimationCollection.Flash.YELLOW.value);
				break;
			case DARK:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.FLUX.value, FE7SpellAnimationCollection.Flash.DARK.value);
				break;
			case LIGHT:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.SHINE.value,
						FE7SpellAnimationCollection.Flash.YELLOW.value);
				break;
			default:
				break;
			}

			// Give her the weapon in place of the Mani Katti in Lyn mode.
			// In every other mode, give it to her by default.
			// Thankfully Lyn Mode uses a different Lyn, so we're good.
			for (GBAFEChapterData chapter : chapterData.allChapters()) {
				if (chapter == chapterData.chapterWithID(FE7Data.ChapterPointer.CHAPTER_2.chapterID)) {
					GBAFEChapterItemData item = chapter.chapterItemGivenToCharacter(FE7Data.Character.LYN_TUTORIAL.ID);
					if (item != null) {
						item.setItemID(newWeapon.getID());
					}
				}
				for (GBAFEChapterUnitData unit : chapter.allUnits()) {
					if (unit.getCharacterNumber() == lyn.getID()) {
						unit.removeItem(referenceWeapon.getID());
						unit.giveItem(newWeapon.getID());
					}
				}
			}
		}

		if (eliwoodSelectedType != null && eliwoodWeaponName != null && eliwoodIconName != null) {
			byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(eliwoodIconName,
					GBAImageCodec.gbaWeaponColorPalette);
			if (iconData == null) {
				notifyError("Invalid image data for icon " + eliwoodIconName);
			}
			diffCompiler.addDiff(new Diff(0xCB5A4, iconData.length, iconData, null));

			textData.setStringAtIndex(0x1227, eliwoodWeaponName + "[X]");
			GBAFEItemData referenceWeapon = itemData.itemWithID(FE7Data.Item.RAPIER.ID);
			GBAFEItemData newWeapon = referenceWeapon.createLordWeapon(FE7Data.Character.ELIWOOD.ID, 0xA0, 0x1227, 0x0,
					eliwoodSelectedType, unbreakablePrfs, eliwoodClass.getCON() + eliwood.getConstitution(), 0xAE,
					itemData, freeSpace);

			// Eliwood only has to take into account the locks that could have already be
			// used (Athos, Eliwood, or Lyn).
			// Try to use his own lock, assuming it's not a sword or a lance.
			if (eliwoodSelectedType == WeaponType.SWORD) {
				if (!athosLockUsed) {
					athosLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.getValue());
				} else {
					// We only have the unused lock left.
					unusedLockUsed = true;
					newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.getValue());
				}
			} else if (eliwoodSelectedType == WeaponType.LANCE) {
				if (!lynLockUsed) {
					lynLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.LYN_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.getValue());
				} else if (!athosLockUsed) {
					athosLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.getValue());
				} else {
					unusedLockUsed = true;
					newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.getValue());
				}
			} else {
				if (!eliwoodLockUsed) {
					eliwoodLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ELIWOOD_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ELIWOOD_LOCK.getValue());
				} else if (!lynLockUsed) {
					lynLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.LYN_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.getValue());
				} else if (!athosLockUsed && // Athos lock cannot be used with any tome.
						eliwoodSelectedType != WeaponType.ANIMA && eliwoodSelectedType != WeaponType.DARK
						&& eliwoodSelectedType != WeaponType.LIGHT) {
					athosLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.getValue());
				} else {
					unusedLockUsed = true;
					newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
					eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.getValue());
				}
			}

			itemData.addNewItem(newWeapon);

			switch (eliwoodSelectedType) {
			case SWORD:
			case LANCE:
			case AXE:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.NONE2.value,
						FE7SpellAnimationCollection.Flash.WHITE.value);
				break;
			case BOW:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.ARROW.value,
						FE7SpellAnimationCollection.Flash.WHITE.value);
				break;
			case ANIMA:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.ELFIRE.value,
						FE7SpellAnimationCollection.Flash.BLUE.value);
				break;
			case DARK:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.FLUX.value, FE7SpellAnimationCollection.Flash.DARK.value);
				break;
			case LIGHT:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.SHINE.value,
						FE7SpellAnimationCollection.Flash.GREEN.value);
				break;
			default:
				break;
			}

			// Replace Eliwood's starting Rapier, if he has one.
			for (GBAFEChapterData chapter : chapterData.allChapters()) {
				for (GBAFEChapterUnitData unit : chapter.allUnits()) {
					if (unit.getCharacterNumber() == eliwood.getID()) {
						unit.removeItem(referenceWeapon.getID());
						unit.giveItem(newWeapon.getID());
					}
				}
			}
		}

		if (hectorSelectedType != null && hectorWeaponName != null && hectorIconName != null) {
			byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(hectorIconName,
					GBAImageCodec.gbaWeaponColorPalette);
			if (iconData == null) {
				notifyError("Invalid image data for icon " + hectorIconName);
			}
			diffCompiler.addDiff(new Diff(0xCB624, iconData.length, iconData, null));

			textData.setStringAtIndex(0x1229, hectorWeaponName + "[X]");
			GBAFEItemData referenceWeapon = itemData.itemWithID(FE7Data.Item.WOLF_BEIL.ID);
			GBAFEItemData newWeapon = referenceWeapon.createLordWeapon(FE7Data.Character.HECTOR.ID, 0xA1, 0x1229, 0x0,
					hectorSelectedType, unbreakablePrfs, hectorClass.getCON() + hector.getConstitution(), 0xAF,
					itemData, freeSpace);

			// We've avoided using Hector lock the entire time, so we just need to account
			// for swords and axes.
			if (hectorSelectedType == WeaponType.SWORD) {
				// Athos and Unused are the only ones possible here. If they're both used, GG.
				if (!athosLockUsed) {
					athosLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
					hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.getValue());
				} else if (!unusedLockUsed) {
					unusedLockUsed = true;
					newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
					hector.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.getValue());
				} else {
					// GG. Just use Hector lock.
					newWeapon.setAbility2(FE7Data.Item.Ability3Mask.HECTOR_LOCK.ID);
					hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.HECTOR_LOCK.getValue());
				}
			} else if (hectorSelectedType == WeaponType.AXE) {
				if (!lynLockUsed) {
					lynLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.LYN_LOCK.ID);
					hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.getValue());
				} else if (!athosLockUsed) {
					athosLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
					hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.getValue());
				} else if (!eliwoodLockUsed) {
					eliwoodLockUsed = true;
					newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ELIWOOD_LOCK.ID);
					hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ELIWOOD_LOCK.getValue());
				} else { // There's no way we used 4 locks with two characters.
					unusedLockUsed = true;
					newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
					hector.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.getValue());
				}
			} else {
				hectorLockUsed = true;
				newWeapon.setAbility3(FE7Data.Item.Ability3Mask.HECTOR_LOCK.ID);
				hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.HECTOR_LOCK.getValue());
			}

			itemData.addNewItem(newWeapon);

			switch (hectorSelectedType) {
			case SWORD:
			case LANCE:
			case AXE:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.NONE2.value,
						FE7SpellAnimationCollection.Flash.WHITE.value);
				break;
			case BOW:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.ARROW.value,
						FE7SpellAnimationCollection.Flash.WHITE.value);
				break;
			case ANIMA:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.FIMBULVETR.value,
						FE7SpellAnimationCollection.Flash.BLUE.value);
				break;
			case DARK:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.FLUX.value, FE7SpellAnimationCollection.Flash.DARK.value);
				break;
			case LIGHT:
				itemData.spellAnimations.addAnimation(newWeapon.getID(), 2,
						FE7SpellAnimationCollection.Animation.SHINE.value,
						FE7SpellAnimationCollection.Flash.BLUE.value);
				break;
			default:
				break;
			}

			// Replace Hector's starting Wolf Beil, if he has one.
			for (GBAFEChapterData chapter : chapterData.allChapters()) {
				for (GBAFEChapterUnitData unit : chapter.allUnits()) {
					if (unit.getCharacterNumber() == hector.getID()) {
						unit.removeItem(referenceWeapon.getID());
						unit.giveItem(newWeapon.getID());
					}
				}
			}
		}
	}

	/**
	 * Loop through the chapters and fix all the World Map Sprites that need to
	 * change.
	 */
	protected void fixWorldMapSprites() {
		for (FE7Data.ChapterPointer chapter : FE7Data.ChapterPointer.values()) {
			Map<Integer, List<Integer>> perChapterMap = chapter.worldMapSpriteClassIDToCharacterIDMapping();
			GBAFEWorldMapData worldMapData = chapterData.worldMapEventsForChapterID(chapter.chapterID);
			if (worldMapData == null) {
				continue;
			}
			for (GBAFEWorldMapSpriteData sprite : worldMapData.allSprites()) {
				// If it's a class we don't touch, ignore it.
				if (classData.classForID(sprite.getClassID()) == null) {
					continue;
				}
				// Check Universal list first.
				Integer characterID = FE7Data.ChapterPointer.universalWorldMapSpriteClassIDToCharacterIDMapping()
						.get(sprite.getClassID());
				if (characterID != null) {
					if (characterID == FE7Data.Character.NONE.ID) {
						continue;
					}
					syncWorldMapSpriteToCharacter(sprite, characterID);
				} else {
					// Check per chapter
					List<Integer> charactersForClassID = perChapterMap.get(sprite.getClassID());
					if (charactersForClassID != null && !charactersForClassID.isEmpty()) {
						int charID = charactersForClassID.remove(0);
						if (charID == FE7Data.Character.NONE.ID) {
							charactersForClassID.add(FE7Data.Character.NONE.ID);
							continue;
						}
						syncWorldMapSpriteToCharacter(sprite, charID);
					} else {
						assert false : "Unaccounted for world map sprite in " + chapter.toString();
					}
				}
			}
		}
	}

	@Override
	protected void randomizeMiscellaneousThingsIfNecessary() {
		super.randomizeMiscellaneousThingsIfNecessary();

		// FE7 Specific
		// If the option is enabled, set the effectiveness for FE7 to triple.
		// TODO: FE9 could use this this if we could figure it out.
		if (miscOptions.tripleEffectiveness) {
			// Replace bytes at 0x28B3E from
			// 01 28 07 D1 30 88 EE F7 36 FB 29 1C 5A 31 0A 88 50 00 08 80 29 1C 5A 31
			// to
			// 29 1C 5A 31 01 28 07 D1 30 78 C0 46 C0 46 0A 88 XX 20 50 43 08 80 C0 46
			// where XX is the multiplier (03 in our case)
			diffCompiler.addDiff(new Diff(0x28B3E, 24,
					new byte[] { (byte) 0x29, (byte) 0x1C, (byte) 0x5A, (byte) 0x31, (byte) 0x01, (byte) 0x28,
							(byte) 0x07, (byte) 0xD1, (byte) 0x30, (byte) 0x78, (byte) 0xC0, (byte) 0x46, (byte) 0xC0,
							(byte) 0x46, (byte) 0x0A, (byte) 0x88, (byte) 0x03, (byte) 0x20, (byte) 0x50, (byte) 0x43,
							(byte) 0x08, (byte) 0x80, (byte) 0xC0, (byte) 0x46 },
					new byte[] { (byte) 0x01, (byte) 0x28, (byte) 0x07, (byte) 0xD1, (byte) 0x30, (byte) 0x88,
							(byte) 0xEE, (byte) 0xF7, (byte) 0x36, (byte) 0xFB, (byte) 0x29, (byte) 0x1C, (byte) 0x5A,
							(byte) 0x31, (byte) 0x0A, (byte) 0x88, (byte) 0x50, (byte) 0x00, (byte) 0x08, (byte) 0x80,
							(byte) 0x29, (byte) 0x1C, (byte) 0x5A, (byte) 0x31 }));
		}
	}

	/**
	 * By Default the player is forced to play Lyn Normal Mode the first time they
	 * start the game, apply the necessary changes to ensure that the players are
	 * not forced to this, as they probably don't need a tutorial. Which is all that
	 * this is.
	 */
	protected void updateModeSelect() {
		try {
			InputStream stream = UPSPatcher.class.getClassLoader().getResourceAsStream("FE7ClearSRAM.bin");
			byte[] bytes = new byte[0x6F];
			stream.read(bytes);
			stream.close();

			long offset = freeSpace.setValue(bytes, "FE7 Hardcoded SRAM", true);
			long pointer = freeSpace.setValue(WhyDoesJavaNotHaveThese.bytesFromAddress(offset),
					"FE7 Hardcoded SRAM Pointer", true);
			diffCompiler.addDiff(
					new Diff(FE7Data.HardcodedSRAMHeaderOffset, 4, WhyDoesJavaNotHaveThese.bytesFromAddress(pointer),
							WhyDoesJavaNotHaveThese.bytesFromAddress(FE7Data.DefaultSRAMHeaderPointer)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Fix up the portraits in mode select, since they're hardcoded.
		// Only necessary if we randomized recruitment.
		// All of the data should have been commited at this point, so asking for Lyn
		// will get you the Lyn replacement.
		if ((recruitOptions != null && recruitOptions.includeLords) || (classes != null && classes.includeLords)) {
			GBAFECharacterData lyn = charData.characterWithID(FE7Data.Character.LYN.ID);
			GBAFECharacterData eliwood = charData.characterWithID(FE7Data.Character.ELIWOOD.ID);
			GBAFECharacterData hector = charData.characterWithID(FE7Data.Character.HECTOR.ID);

			byte lynReplacementFaceID = (byte) lyn.getFaceID();
			byte eliwoodReplacementFaceID = (byte) eliwood.getFaceID();
			byte hectorReplacementFaceID = (byte) hector.getFaceID();

			diffCompiler
					.addDiff(
							new Diff(
									FE7Data.ModeSelectPortraitOffset, 12, new byte[] { lynReplacementFaceID, 0, 0, 0,
											eliwoodReplacementFaceID, 0, 0, 0, hectorReplacementFaceID, 0, 0, 0 },
									null));

			// Conveniently, the class animations are here too, in the same format.
			FE7Data.CharacterClass lynClass = FE7Data.CharacterClass.valueOf(lyn.getClassID());
			FE7Data.CharacterClass eliwoodClass = FE7Data.CharacterClass.valueOf(eliwood.getClassID());
			FE7Data.CharacterClass hectorClass = FE7Data.CharacterClass.valueOf(hector.getClassID());

			byte lynReplacementAnimationID = (byte) lynClass.animationID();
			byte eliwoodReplacementAnimationID = (byte) eliwoodClass.animationID();
			byte hectorReplacementAnimationID = (byte) hectorClass.animationID();

			diffCompiler
					.addDiff(new Diff(
							FE7Data.ModeSelectClassAnimationOffset, 12, new byte[] { lynReplacementAnimationID, 0, 0, 0,
									eliwoodReplacementAnimationID, 0, 0, 0, hectorReplacementAnimationID, 0, 0, 0 },
							null));

			// See if we can apply their palettes to the class default.
			PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, sourceFileHandler,
					characterMap != null ? characterMap.get(lyn) : lyn, lyn.getClassID(), paletteData, freeSpace,
					diffCompiler);
			PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, sourceFileHandler,
					characterMap != null ? characterMap.get(eliwood) : eliwood, eliwood.getClassID(), paletteData,
					freeSpace, diffCompiler);
			PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, sourceFileHandler,
					characterMap != null ? characterMap.get(hector) : hector, hector.getClassID(), paletteData,
					freeSpace, diffCompiler);

			// Finally, fix the weapon text.
			textData.setStringAtIndex(FE7Data.ModeSelectTextLynWeaponTypeIndex, lynClass.primaryWeaponType() + "[X]");
			textData.setStringAtIndex(FE7Data.ModeSelectTextEliwoodWeaponTypeIndex,
					eliwoodClass.primaryWeaponType() + "[X]");
			textData.setStringAtIndex(FE7Data.ModeSelectTextHectorWeaponTypeIndex,
					hectorClass.primaryWeaponType() + "[X]");

			// Eliwood is the one we're going to override, since he normally shares the
			// weapon string with Lyn.
			diffCompiler
					.addDiff(
							new Diff(FE7Data.ModeSelectEliwoodWeaponOffset, 2,
									new byte[] { (byte) (FE7Data.ModeSelectTextEliwoodWeaponTypeIndex & 0xFF),
											(byte) ((FE7Data.ModeSelectTextEliwoodWeaponTypeIndex >> 8) & 0xFF) },
									null));
		}
	}

	@Override
	protected void addRandomDrops() {
		// Change the code at 0x17826 from
		// 20 68 61 68 80 6A 89 6A 08 43 80 21 09 05 08 40
		// to
		// 20 1C 41 30 00 78 40 21 08 40 00 00 00 00 00 00
		// This will allow us to set the 4th AI bit for units to drop the last item if
		// the 0x40 bit is set.
		diffCompiler.addDiff(new Diff(0x17826, 16,
				new byte[] { (byte) 0x20, (byte) 0x1C, (byte) 0x41, (byte) 0x30, (byte) 0x00, (byte) 0x78, (byte) 0x40,
						(byte) 0x21, (byte) 0x08, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00

				},
				new byte[] { (byte) 0x20, (byte) 0x68, (byte) 0x61, (byte) 0x68, (byte) 0x80, (byte) 0x6A, (byte) 0x89,
						(byte) 0x6A, (byte) 0x08, (byte) 0x43, (byte) 0x80, (byte) 0x21, (byte) 0x09, (byte) 0x05,
						(byte) 0x08, (byte) 0x40 }));

		super.addRandomDrops();
	}

	protected void applyEmblemBowEffectiveness() {
		GBAFEItemData emblemBow = itemData.itemWithID(FE7Data.Item.EMBLEM_BOW.ID);
		emblemBow.setEffectivenessPointer(itemData.flierEffectPointer());
	}

	@Override
	protected void applySingleRN() {
		diffCompiler.addDiff(new Diff(0xE92, 4, new byte[] { (byte) 0xC0, (byte) 0x46, (byte) 0xC0, (byte) 0x46 },
				new byte[] { (byte) 0xFF, (byte) 0xF7, (byte) 0xB7, (byte) 0xFF }));
	}
	
	@Override
	protected void gameSpecificDiffCompilations() {
		//N/A
	}
	
	@Override
	protected void applyUpsPatches() {
		// N/A
	}

	@Override
	protected void applyCasualMode() {
		diffCompiler.addDiff(new Diff(0x17EA0, 1, new byte[] {(byte)0x09}, new byte[] {(byte)0x05}));
	}

	@Override
	protected void applyParagonMode() {
		// Combat EXP
		diffCompiler.addDiff(new Diff(0x29F64, 24, 
				new byte[] {(byte)0x24, (byte)0x18, (byte)0x64, (byte)0x00, (byte)0x64, (byte)0x2C, (byte)0x00, (byte)0xDD,
						    (byte)0x64, (byte)0x24, (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0xDA, (byte)0x00, (byte)0x24,
						    (byte)0x20, (byte)0x1C, (byte)0x70, (byte)0xBC, (byte)0x02, (byte)0xBC, (byte)0x08, (byte)0x47}, 
				new byte[] {(byte)0x24, (byte)0x18, (byte)0x64, (byte)0x2C, (byte)0x00, (byte)0xDD, (byte)0x64, (byte)0x24, 
						    (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0xDA, (byte)0x00, (byte)0x24, (byte)0x20, (byte)0x1C,
						    (byte)0x70, (byte)0xBC, (byte)0x02, (byte)0xBC, (byte)0x08, (byte)0x47, (byte)0x00, (byte)0x00}));
		diffCompiler.addDiff(new Diff(0x29F50, 2,
				new byte[] {(byte)0x11, (byte)0xE0},
				new byte[] {(byte)0x10, (byte)0xE0}));
		diffCompiler.addDiff(new Diff(0x29F3E, 2,
				new byte[] {(byte)0x1A, (byte)0xE0},
				new byte[] {(byte)0x19, (byte)0xE0}));
		diffCompiler.addDiff(new Diff(0x29F4E, 2,
				new byte[] {(byte)0x02, (byte)0x20},
				new byte[] {(byte)0x01, (byte)0x20}));
		
		// Staff EXP
		diffCompiler.addDiff(new Diff(0x2A044, 12,
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x01, (byte)0xD0, (byte)0x52, (byte)0x10, (byte)0x00, (byte)0x00,
						    (byte)0x52, (byte)0x00, (byte)0x64, (byte)0x2A},
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x02, (byte)0xD0, (byte)0xD0, (byte)0x0F, (byte)0x10, (byte)0x18,
						    (byte)0x42, (byte)0x10, (byte)0x64, (byte)0x2A}));
		
		// Steal/Dance EXP
		diffCompiler.addDiff(new Diff(0x2A086, 8,
				new byte[] {(byte)0x14, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x14, (byte)0x30},
				new byte[] {(byte)0x0A, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x0A, (byte)0x30}));		
	}

	@Override
	protected void applyRenegadeMode() {
		// Combat EXP
		diffCompiler.addDiff(new Diff(0x29F64, 24, 
				new byte[] {(byte)0x24, (byte)0x18, (byte)0x64, (byte)0x10, (byte)0x64, (byte)0x2C, (byte)0x00, (byte)0xDD,
					        (byte)0x64, (byte)0x24, (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0xDA, (byte)0x00, (byte)0x24,
					        (byte)0x20, (byte)0x1C, (byte)0x70, (byte)0xBC, (byte)0x02, (byte)0xBC, (byte)0x08, (byte)0x47}, 
				new byte[] {(byte)0x24, (byte)0x18, (byte)0x64, (byte)0x2C, (byte)0x00, (byte)0xDD, (byte)0x64, (byte)0x24, 
						    (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0xDA, (byte)0x00, (byte)0x24, (byte)0x20, (byte)0x1C,
						    (byte)0x70, (byte)0xBC, (byte)0x02, (byte)0xBC, (byte)0x08, (byte)0x47, (byte)0x00, (byte)0x00}));
		diffCompiler.addDiff(new Diff(0x29F50, 2,
				new byte[] {(byte)0x11, (byte)0xE0},
				new byte[] {(byte)0x10, (byte)0xE0}));
		diffCompiler.addDiff(new Diff(0x29F3E, 2,
				new byte[] {(byte)0x1A, (byte)0xE0},
				new byte[] {(byte)0x19, (byte)0xE0}));
		
		// Staff EXP
		diffCompiler.addDiff(new Diff(0x2A044, 12,
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x01, (byte)0xD0, (byte)0x52, (byte)0x10, (byte)0x00, (byte)0x00,
					        (byte)0x52, (byte)0x10, (byte)0x64, (byte)0x2A},
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x02, (byte)0xD0, (byte)0xD0, (byte)0x0F, (byte)0x10, (byte)0x18,
						    (byte)0x42, (byte)0x10, (byte)0x64, (byte)0x2A}));
		
		// Steal/Dance EXP
		diffCompiler.addDiff(new Diff(0x2A086, 8,
				new byte[] {(byte)0x05, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x05, (byte)0x30},
				new byte[] {(byte)0x0A, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x0A, (byte)0x30}));		
	}

}
