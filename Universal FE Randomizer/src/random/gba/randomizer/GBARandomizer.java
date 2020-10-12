package random.gba.randomizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapSpriteData;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.fe8.FE8PaletteMapper;
import fedata.gba.fe8.FE8PromotionManager;
import fedata.gba.fe8.FE8SummonerModule;
import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import io.DiffApplicator;
import io.FileHandler;
import io.UPSPatcher;
import io.UPSPatcherStatusListener;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.PaletteLoader;
import random.gba.loader.TextLoader;
import random.general.Randomizer;
import ui.model.BaseOptions;
import ui.model.ClassOptions;
import ui.model.EnemyOptions;
import ui.model.GrowthOptions;
import ui.model.ItemAssignmentOptions;
import ui.model.MiscellaneousOptions;
import ui.model.OtherCharacterOptions;
import ui.model.RecruitmentOptions;
import ui.model.WeaponOptions;
import ui.model.EnemyOptions.BossStatMode;
import ui.model.ItemAssignmentOptions.ShopAdjustment;
import ui.model.ItemAssignmentOptions.WeaponReplacementPolicy;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FreeSpaceManager;
import util.SeedGenerator;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.RecordKeeper;

public class GBARandomizer extends Randomizer {
	
	private String sourcePath;
	private String targetPath;
	
	private FEBase.GameType gameType;
	
	private DiffCompiler diffCompiler;
	
	private GrowthOptions growths;
	private BaseOptions bases;
	private ClassOptions classes;
	private WeaponOptions weapons;
	private OtherCharacterOptions otherCharacterOptions;
	private EnemyOptions enemies;
	private MiscellaneousOptions miscOptions;
	private RecruitmentOptions recruitOptions;
	private ItemAssignmentOptions itemAssignmentOptions;
	
	private CharacterDataLoader charData;
	private ClassDataLoader classData;
	private ChapterLoader chapterData;
	private ItemDataLoader itemData;
	private PaletteLoader paletteData;
	private TextLoader textData;
	
	private boolean needsPaletteFix;
	private Map<GBAFECharacterData, GBAFECharacterData> characterMap; // valid with random recruitment. Maps slots to reference character.
	
	// FE8 only
	private FE8PaletteMapper fe8_paletteMapper;
	private FE8PromotionManager fe8_promotionManager;
	private FE8SummonerModule fe8_summonerModule;
	
	private String seedString;
	
	private FreeSpaceManager freeSpace;
	
	private FileHandler handler;

	public GBARandomizer(String sourcePath, String targetPath, FEBase.GameType gameType, DiffCompiler diffs, 
			GrowthOptions growths, BaseOptions bases, ClassOptions classes, WeaponOptions weapons,
			OtherCharacterOptions other, EnemyOptions enemies, MiscellaneousOptions otherOptions,
			RecruitmentOptions recruit, ItemAssignmentOptions itemAssign, String seed) {
		super();
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		this.seedString = seed;
		
		diffCompiler = diffs;
		
		this.growths = growths;
		this.bases = bases;
		this.classes = classes;
		this.weapons = weapons;
		otherCharacterOptions = other;
		this.enemies = enemies;
		miscOptions = otherOptions;
		recruitOptions = recruit;
		itemAssignmentOptions = itemAssign;
		if (itemAssignmentOptions == null) { itemAssignmentOptions = new ItemAssignmentOptions(WeaponReplacementPolicy.ANY_USABLE, ShopAdjustment.NO_CHANGE); }
		
		this.gameType = gameType;
	}
	
	public void run() {
		randomize(seedString);
	}
	
	private void randomize(String seed) {
		try {
			handler = new FileHandler(sourcePath);
		} catch (IOException e) {
			notifyError("Failed to open source file.");
			return;
		}
		
		String tempPath = null;
		
		switch (gameType) {
		case FE6:
			// Apply patch first, if necessary.
			if (miscOptions.applyEnglishPatch) {
				updateStatusString("Applying English Patch...");
				updateProgress(0.05);
				
				tempPath = new String(targetPath).concat(".tmp");
				
				try {
					Boolean success = UPSPatcher.applyUPSPatch("FE6-TLRedux-v1.0.ups", sourcePath, tempPath, null);
					if (!success) {
						notifyError("Failed to apply translation patch.");
						return;
					}
				} catch (Exception e) {
					notifyError("Encountered error while applying patch.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList())));
					return;
				}
				
				try {
					handler = new FileHandler(tempPath);
				} catch (IOException e1) {
					System.err.println("Unable to open post-patched file.");
					e1.printStackTrace();
					notifyError("Failed to apply translation patch.");
					return;
				}
			}
			updateStatusString("Loading Data...");
			updateProgress(0.1);
			try { generateFE6DataLoaders(); } catch (Exception e) { notifyError("Encountered error while loading data.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
			break;
		case FE7:
			updateStatusString("Loading Data...");
			updateProgress(0.01);
			try { generateFE7DataLoaders(); } catch (Exception e) { notifyError("Encountered error while loading data.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
			break;
		case FE8:
			updateStatusString("Loading Data...");
			updateProgress(0.01);
			try { generateFE8DataLoaders(); } catch (Exception e) { notifyError("Encountered error while loading data.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
			break;
		default:
			notifyError("This game is not supported.");
			return;
		}
		
		RecordKeeper recordKeeper = initializeRecordKeeper();
		recordKeeper.addHeaderItem("Randomizer Seed Phrase", seed);
		
		charData.recordCharacters(recordKeeper, true, classData, itemData, textData);
		classData.recordClasses(recordKeeper, true, classData, textData);
		itemData.recordWeapons(recordKeeper, true, classData, textData, handler);
		chapterData.recordChapters(recordKeeper, true, charData, classData, itemData, textData);
		
		paletteData.recordReferencePalettes(recordKeeper, charData, classData, textData);
		
		updateStatusString("Randomizing...");
		try { randomizeGrowthsIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing growths.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.45);
		try { randomizeClassesIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing classes.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; } 
		updateProgress(0.50);
		try { randomizeBasesIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing bases.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.55);
		try { randomizeWeaponsIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing weapons.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.60);
		try { randomizeOtherCharacterTraitsIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing other character traits.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.65);
		try { buffEnemiesIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while buffing enemies.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.70);
		try { randomizeOtherThingsIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing miscellaneous settings.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; } // i.e. Miscellaneous options.
		updateProgress(0.75);
		try { randomizeRecruitmentIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing recruitment.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.90);
		try { makeFinalAdjustments(seed); } catch (Exception e) { notifyError("Encountered error while making final adjustments.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		
		updateStatusString("Compiling changes...");
		updateProgress(0.95);
		charData.compileDiffs(diffCompiler);
		chapterData.compileDiffs(diffCompiler);
		classData.compileDiffs(diffCompiler);
		itemData.compileDiffs(diffCompiler);
		paletteData.compileDiffs(diffCompiler);
		textData.commitChanges(freeSpace, diffCompiler);
		
		if (gameType == GameType.FE8) {
			fe8_paletteMapper.commitChanges(diffCompiler);
			fe8_promotionManager.compileDiffs(diffCompiler);
			
			fe8_summonerModule.validateSummoners(charData, new Random(SeedGenerator.generateSeedValue(seed, 0)));
			fe8_summonerModule.commitChanges(diffCompiler, freeSpace);
		}
		
		freeSpace.commitChanges(diffCompiler);
		
		updateStatusString("Applying changes...");
		updateProgress(0.99);
		if (targetPath != null) {
			try {
				DiffApplicator.applyDiffs(diffCompiler, handler, targetPath);
			} catch (FileNotFoundException e) {
				notifyError("Could not write to destination file.");
				return;
			}
		}
		
		handler.close();
		handler = null;
		
		if (tempPath != null) {
			updateStatusString("Cleaning up...");
			File tempFile = new File(tempPath);
			if (tempFile != null) { 
				Boolean success = tempFile.delete();
				if (!success) {
					System.err.println("Failed to delete temp file.");
				}
			}
		}
		
		FileHandler targetFileHandler = null;
		try {
			targetFileHandler = new FileHandler(targetPath);
		} catch (IOException e) {
			notifyError("Failed to open source file.");
			return;
		}
		
		charData.recordCharacters(recordKeeper, false, classData, itemData, textData);
		classData.recordClasses(recordKeeper, false, classData, textData);
		itemData.recordWeapons(recordKeeper, false, classData, textData, targetFileHandler);
		chapterData.recordChapters(recordKeeper, false, charData, classData, itemData, textData);
		
		if (gameType == FEBase.GameType.FE8) {
			paletteData.recordUpdatedFE8Palettes(recordKeeper, charData, classData, textData);
		} else {
			paletteData.recordUpdatedPalettes(recordKeeper, charData, classData, textData);
		}
		
		recordKeeper.sortKeysInCategory(CharacterDataLoader.RecordKeeperCategoryKey);
		recordKeeper.sortKeysInCategory(ClassDataLoader.RecordKeeperCategoryKey);
		recordKeeper.sortKeysInCategory(ItemDataLoader.RecordKeeperCategoryWeaponKey);
		
		switch (gameType) {
		case FE6:
			recordKeeper.addNote("Characters that randomize into the Soldier class can promote using a Knight's Crest.");
			recordKeeper.addNote("Characters that randomize into the Roy Lord class can promote using a Knight's Crest.");
			break;
		case FE7:
			recordKeeper.addNote("Characters that randomize into the Soldier class can promote using a Knight's Crest or Earth Seal.");
			recordKeeper.addNote("Characters that randomize into the Lyn Lord class can promote using a Hero's Crest or Earth Seal.");
			recordKeeper.addNote("Characters that randomize into the Eliwood Lord class can promote using a Knight's Crest or Earth Seal.");
			recordKeeper.addNote("Characters that randomzie into the Hector Lord class can promote using a Knight's Crest or Earth Seal.");
			recordKeeper.addNote("Characters that randomize into the Corsair class can promote using an Ocean's Seal or Earth Seal.");
			recordKeeper.addNote("Characters that randomize into the Brigand class can promote using a Hero's Crest, Ocean's Seal, or Earth Seal.");
			recordKeeper.addNote("Emblem Bow is now effective against fliers by default.");
			break;
		case FE8:
			recordKeeper.addNote("Characters that randomize into the Soldier class can promote into a Paladin or General using a Knight's Crest or Master Seal.");
			recordKeeper.addNote("Characters that randomize into the Eirika Lord class can promote using a Knight's Crest or Master Seal.");
			recordKeeper.addNote("Characters that randomize into the Ephraim Lord class can promote using a Knight's Crest or Master Seal.");
			recordKeeper.addNote("Characters that randomize into Revenant, Sword/Lance Bonewalkers, and Mauthe Doogs promote using a Hero's Crest or Master Seal.");
			recordKeeper.addNote("Characters that randomize into Tarvos and Bael promote using a Knight's Crest or Master Seal.");
			recordKeeper.addNote("Characters that randomize into a Mogall promote using a Guiding Ring or Master Seal.");
			recordKeeper.addNote("Characters that randomize into a Bow Bonewalker promote using an Orion's Bolt or Master Seal.");
			recordKeeper.addNote("Characters that randomize into a Gargoyle promote using an Elysian Whip or Master Seal.");
			break;
		default:
			break;
		}
		
		updateStatusString("Done!");
		updateProgress(1);
		notifyCompletion(recordKeeper, null);
	}
	
	private void generateFE7DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE7, FE7Data.InternalFreeRange, handler);
		updateStatusString("Loading Text...");
		updateProgress(0.05);
		textData = new TextLoader(FEBase.GameType.FE7, handler);
		textData.allowTextChanges = true;
		
		updateStatusString("Loading Character Data...");
		updateProgress(0.10);
		charData = new CharacterDataLoader(FE7Data.characterProvider, handler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.15);
		classData = new ClassDataLoader(FE7Data.classProvider, handler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.20);
		chapterData = new ChapterLoader(FEBase.GameType.FE7, handler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.25);
		itemData = new ItemDataLoader(FE7Data.itemProvider, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE7, handler, charData, classData);
		
		handler.clearAppliedDiffs();
	}
	
	private void generateFE6DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE6, FE6Data.InternalFreeRange, handler);
		updateStatusString("Loading Text...");
		updateProgress(0.05);
		textData = new TextLoader(FEBase.GameType.FE6, handler);
		if (miscOptions.applyEnglishPatch) {
			textData.allowTextChanges = true;
		}
		
		updateStatusString("Loading Character Data...");
		updateProgress(0.10);
		charData = new CharacterDataLoader(FE6Data.characterProvider, handler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.15);
		classData = new ClassDataLoader(FE6Data.classProvider, handler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.20);
		chapterData = new ChapterLoader(FEBase.GameType.FE6, handler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.25);
		itemData = new ItemDataLoader(FE6Data.itemProvider, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE6, handler, charData, classData);
		
		handler.clearAppliedDiffs();
	}
	
	private void generateFE8DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE8, FE8Data.InternalFreeRange, handler);
		updateStatusString("Loading Text...");
		updateProgress(0.04);
		textData = new TextLoader(FEBase.GameType.FE8, handler);
		textData.allowTextChanges = true;
		
		updateStatusString("Loading Promotion Data...");
		updateProgress(0.06);
		fe8_promotionManager = new FE8PromotionManager(handler);
		
		updateStatusString("Loading Character Data...");
		updateProgress(0.10);
		charData = new CharacterDataLoader(FE8Data.characterProvider, handler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.15);
		classData = new ClassDataLoader(FE8Data.classProvider, handler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.20);
		chapterData = new ChapterLoader(FEBase.GameType.FE8, handler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.25);
		itemData = new ItemDataLoader(FE8Data.itemProvider, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE8, handler, charData, classData);
		
		updateStatusString("Loading Summoner Module...");
		updateProgress(0.35);
		fe8_summonerModule = new FE8SummonerModule(handler);
		
		updateStatusString("Loading Palette Mapper...");
		updateProgress(0.40);
		fe8_paletteMapper = paletteData.setupFE8SpecialManagers(handler, fe8_promotionManager);
		
		
		handler.clearAppliedDiffs();
	}
	
	private void randomizeGrowthsIfNecessary(String seed) {
		if (growths != null) {
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, GrowthsRandomizer.rngSalt));
			switch (growths.mode) {
			case REDISTRIBUTE:
				updateStatusString("Redistributing growths...");
				GrowthsRandomizer.randomizeGrowthsByRedistribution(growths.redistributionOption.variance, growths.adjustHP, charData, rng);
				break;
			case DELTA:
				updateStatusString("Applying random deltas to growths...");
				GrowthsRandomizer.randomizeGrowthsByRandomDelta(growths.deltaOption.variance, growths.adjustHP, charData, rng);
				break;
			case FULL:
				updateStatusString("Randomizing growths...");
				GrowthsRandomizer.fullyRandomizeGrowthsWithRange(growths.fullOption.minValue, growths.fullOption.maxValue, growths.adjustHP, charData, rng);
				break;
			}
		}
	}
	
	private void randomizeBasesIfNecessary(String seed) {
		if (bases != null) {
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, BasesRandomizer.rngSalt));
			switch (bases.mode) {
			case REDISTRIBUTE:
				updateStatusString("Redistributing bases...");
				BasesRandomizer.randomizeBasesByRedistribution(bases.redistributionOption.variance, charData, classData, rng);
				break;
			case DELTA:
				updateStatusString("Applying random deltas to growths...");
				BasesRandomizer.randomizeBasesByRandomDelta(bases.deltaOption.variance, charData, classData, rng);
				break;
			}
		}
	}
	
	private void randomizeClassesIfNecessary(String seed) {
		if (classes != null) {
			if (classes.randomizePCs) {
				updateStatusString("Randomizing player classes...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 1));
				ClassRandomizer.randomizePlayableCharacterClasses(classes, itemAssignmentOptions, gameType, charData, classData, chapterData, itemData, textData, rng);
				needsPaletteFix = true;
			}
			if (classes.randomizeEnemies) {
				updateStatusString("Randomizing minions...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 2));
				ClassRandomizer.randomizeMinionClasses(classes, itemAssignmentOptions, gameType, charData, classData, chapterData, itemData, rng);
			}
			if (classes.randomizeBosses) {
				updateStatusString("Randomizing boss classes...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 3));
				ClassRandomizer.randomizeBossCharacterClasses(classes, itemAssignmentOptions, gameType, charData, classData, chapterData, itemData, textData, rng);
				needsPaletteFix = true;
			}
		}
	}
	
	private void randomizeWeaponsIfNecessary(String seed) {
		if (weapons != null) {
			if (weapons.mightOptions != null) {
				updateStatusString("Randomizing weapon power...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt));
				WeaponsRandomizer.randomizeMights(weapons.mightOptions.minValue, weapons.mightOptions.maxValue, weapons.mightOptions.variance, itemData, rng);
			}
			if (weapons.hitOptions != null) {
				updateStatusString("Randomizing weapon accuracy...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt + 1));
				WeaponsRandomizer.randomizeHit(weapons.hitOptions.minValue, weapons.hitOptions.maxValue, weapons.hitOptions.variance, itemData, rng);
			}
			if (weapons.weightOptions != null) {
				updateStatusString("Randomizing weapon weights...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt + 2));
				WeaponsRandomizer.randomizeWeight(weapons.weightOptions.minValue, weapons.weightOptions.maxValue, weapons.weightOptions.variance, itemData, rng);
			}
			if (weapons.durabilityOptions != null) {
				updateStatusString("Randomizing weapon durability...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt + 3));
				WeaponsRandomizer.randomizeDurability(weapons.durabilityOptions.minValue, weapons.durabilityOptions.maxValue, weapons.durabilityOptions.variance, itemData, rng);
			}
			
			if (weapons.shouldAddEffects && weapons.effectsList != null) {
				updateStatusString("Adding random effects to weapons...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt + 4));
				WeaponsRandomizer.randomizeEffects(weapons.effectsList, itemData, textData, weapons.noEffectIronWeapons, weapons.noEffectSteelWeapons, weapons.noEffectThrownWeapons, weapons.effectChance, rng);
			}
		}
	}
	
	private void randomizeOtherCharacterTraitsIfNecessary(String seed) {
		if (otherCharacterOptions != null) {
			if (otherCharacterOptions.movementOptions != null) {
				updateStatusString("Randomizing class movement ranges...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 4));
				ClassRandomizer.randomizeClassMovement(otherCharacterOptions.movementOptions.minValue, otherCharacterOptions.movementOptions.maxValue, classData, rng);
			}
			if (otherCharacterOptions.constitutionOptions != null) {
				updateStatusString("Randomizing character constitution...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, CharacterRandomizer.rngSalt));
				CharacterRandomizer.randomizeConstitution(otherCharacterOptions.constitutionOptions.minValue, otherCharacterOptions.constitutionOptions.variance, charData, classData, rng);
			}
			if (otherCharacterOptions.randomizeAffinity) {
				updateStatusString("Randomizing character affinity...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, CharacterRandomizer.rngSalt + 1));
				CharacterRandomizer.randomizeAffinity(charData, rng);
			}
		}
	}
	
	private void buffEnemiesIfNecessary(String seed) {
		if (enemies != null) {
			if (enemies.minionMode == EnemyOptions.MinionGrowthMode.FLAT) {
				updateStatusString("Buffing enemies...");
				EnemyBuffer.buffMinionGrowthRates(enemies.minionBuff, classData);
			} else if (enemies.minionMode == EnemyOptions.MinionGrowthMode.SCALING) {
				updateStatusString("Buffing enemies...");
				EnemyBuffer.scaleEnemyGrowthRates(enemies.minionBuff, classData);
			}
			
			if (enemies.improveMinionWeapons) {
				updateStatusString("Upgrading enemy weapons...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, EnemyBuffer.rngSalt));
				EnemyBuffer.improveMinionWeapons(enemies.minionImprovementChance, charData, classData, chapterData, itemData, rng);
			}
			
			if (enemies.bossMode == BossStatMode.LINEAR) {
				updateStatusString("Buffing Bosses...");
				EnemyBuffer.buffBossStatsLinearly(enemies.bossBuff, charData, classData);
			} else if (enemies.bossMode == BossStatMode.EASE_IN_OUT) {
				updateStatusString("Buffing Bosses...");
				EnemyBuffer.buffBossStatsWithEaseInOutCurve(enemies.bossBuff, charData, classData);
			}
			
			if (enemies.improveBossWeapons) {
				updateStatusString("Upgrading boss weapons...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, EnemyBuffer.rngSalt + 1));
				EnemyBuffer.improveBossWeapons(enemies.bossImprovementChance, charData, classData, chapterData, itemData, rng);
			}
		}
	}
	
	private void randomizeOtherThingsIfNecessary(String seed) {
		if (miscOptions != null) {
			if (miscOptions.randomizeRewards) {
				updateStatusString("Randomizing rewards...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, RandomRandomizer.rngSalt));
				RandomRandomizer.randomizeRewards(itemData, chapterData, rng);
			}
			
			if (miscOptions.enemyDropChance > 0) {
				if (gameType == GameType.FE7) {
					// Change the code at 0x17826 from
					// 20 68 61 68 80 6A 89 6A 08 43 80 21 09 05 08 40
					// to
					// 20 1C 41 30 00 78 40 21 08 40 00 00 00 00 00 00
					// This will allow us to set the 4th AI bit for units to drop the last item if
					// the 0x40 bit is set.
					diffCompiler.addDiff(new Diff(0x17826, 16,
							new byte[] {(byte)0x20, (byte)0x1C, (byte)0x41, (byte)0x30,
									(byte)0x00, (byte)0x78, (byte)0x40, (byte)0x21,
									(byte)0x08, (byte)0x40, (byte)0x00, (byte)0x00,
									(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
									
							},
							new byte[] {(byte)0x20, (byte)0x68, (byte)0x61, (byte)0x68,
									(byte)0x80, (byte)0x6A, (byte)0x89, (byte)0x6A,
									(byte)0x08, (byte)0x43, (byte)0x80, (byte)0x21,
									(byte)0x09, (byte)0x05, (byte)0x08, (byte)0x40
							}));
				}
				updateStatusString("Adding drops...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, RandomRandomizer.rngSalt + 1));
				RandomRandomizer.addRandomEnemyDrops(miscOptions.enemyDropChance, charData, itemData, chapterData, rng);
			}
		}
	}
	
	private void randomizeRecruitmentIfNecessary(String seed) {
		if (recruitOptions != null) {
			updateStatusString("Randomizing recruitment...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, RecruitmentRandomizer.rngSalt));
			characterMap = RecruitmentRandomizer.randomizeRecruitment(recruitOptions, itemAssignmentOptions, gameType, charData, classData, itemData, chapterData, textData, freeSpace, rng);
			needsPaletteFix = true;
		}
	}
	
	private void makeFinalAdjustments(String seed) {
		// Fix the palettes based on final classes.
		if (needsPaletteFix) {
			PaletteHelper.synchronizePalettes(gameType, recruitOptions != null ? recruitOptions.includeExtras : false, charData, classData, paletteData, characterMap, freeSpace);
		}
		
		// Fix promotions so that forcing a promoted unit to promote again doesn't demote them.
		if (gameType == GameType.FE6 || gameType == GameType.FE7) {
			// FE6 and FE7 store this on the class directly. Just switch the target promotion for promoted classes to themselves.
			// Only do this if the class's demoted class promotes into it (just to make sure we don't accidentally change anything we don't need to).
			for (GBAFEClassData charClass : classData.allClasses()) {
				if (classData.isPromotedClass(charClass.getID())) {
					int demotedID = charClass.getTargetPromotionID();
					GBAFEClassData demotedClass = classData.classForID(demotedID);
					if (demotedClass.getTargetPromotionID() == charClass.getID()) {
						charClass.setTargetPromotionID(charClass.getID());
					}
				}
			}
		} else if (gameType == GameType.FE8) {
			// FE8 stores this in a separate table.
			for (GBAFEClassData charClass : classData.allClasses()) {
				if (classData.isPromotedClass(charClass.getID())) {
					int demotedID1 = fe8_promotionManager.getFirstPromotionOptionClassID(charClass.getID());
					int demotedID2 = fe8_promotionManager.getSecondPromotionOptionClassID(charClass.getID());
					if (demotedID1 == 0 && demotedID2 == 0) {
						// If we have no promotions and we are a promoted class, then apply our fix.
						// Promote into yourself if this happens.
						fe8_promotionManager.setFirstPromotionOptionForClass(charClass.getID(), charClass.getID());
					}
				}
			}
		}

		// For some reason, FE7's Emblem Bow has no effectiveness added to it.
		if (gameType == GameType.FE7) {
			GBAFEItemData emblemBow = itemData.itemWithID(FE7Data.Item.EMBLEM_BOW.ID);
			emblemBow.setEffectivenessPointer(itemData.flierEffectPointer());
		}
		
		// Hack in mode select without needing clear data for FE7.
		if (gameType == GameType.FE7) {
			try {
				InputStream stream = UPSPatcher.class.getClassLoader().getResourceAsStream("FE7ClearSRAM.bin");
				byte[] bytes = new byte[0x6F];
				stream.read(bytes);
				stream.close();
				
				long offset = freeSpace.setValue(bytes, "FE7 Hardcoded SRAM", true);
				long pointer = freeSpace.setValue(WhyDoesJavaNotHaveThese.bytesFromAddress(offset), "FE7 Hardcoded SRAM Pointer", true);
				diffCompiler.addDiff(new Diff(FE7Data.HardcodedSRAMHeaderOffset, 4, WhyDoesJavaNotHaveThese.bytesFromAddress(pointer), WhyDoesJavaNotHaveThese.bytesFromAddress(FE7Data.DefaultSRAMHeaderPointer)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Fix up the portraits in mode select, since they're hardcoded.
			// Only necessary if we randomized recruitment.
			// All of the data should have been commited at this point, so asking for Lyn will get you the Lyn replacement.
			if (recruitOptions != null && recruitOptions.includeLords) {
				GBAFECharacterData lyn = charData.characterWithID(FE7Data.Character.LYN.ID);
				GBAFECharacterData eliwood = charData.characterWithID(FE7Data.Character.ELIWOOD.ID);
				GBAFECharacterData hector = charData.characterWithID(FE7Data.Character.HECTOR.ID);
				
				byte lynReplacementFaceID = (byte)lyn.getFaceID();
				byte eliwoodReplacementFaceID = (byte)eliwood.getFaceID();
				byte hectorReplacementFaceID = (byte)hector.getFaceID();
				
				diffCompiler.addDiff(new Diff(FE7Data.ModeSelectPortraitOffset, 12,
						new byte[] {lynReplacementFaceID, 0, 0, 0, eliwoodReplacementFaceID, 0, 0, 0, hectorReplacementFaceID, 0, 0, 0}, null));
				
				// Conveniently, the class animations are here too, in the same format.
				FE7Data.CharacterClass lynClass = FE7Data.CharacterClass.valueOf(lyn.getClassID());
				FE7Data.CharacterClass eliwoodClass = FE7Data.CharacterClass.valueOf(eliwood.getClassID());
				FE7Data.CharacterClass hectorClass = FE7Data.CharacterClass.valueOf(hector.getClassID());
				
				byte lynReplacementAnimationID = (byte)lynClass.animationID();
				byte eliwoodReplacementAnimationID = (byte)eliwoodClass.animationID();
				byte hectorReplacementAnimationID = (byte)hectorClass.animationID();
				
				diffCompiler.addDiff(new Diff(FE7Data.ModeSelectClassAnimationOffset, 12,
						new byte[] {lynReplacementAnimationID, 0, 0, 0, eliwoodReplacementAnimationID, 0, 0, 0, hectorReplacementAnimationID, 0, 0, 0}, null));
				
				// See if we can apply their palettes to the class default.
				PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, handler, characterMap.get(lyn), lyn.getClassID(), paletteData, freeSpace, diffCompiler);
				PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, handler, characterMap.get(eliwood), eliwood.getClassID(), paletteData, freeSpace, diffCompiler);
				PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, handler, characterMap.get(hector), hector.getClassID(), paletteData, freeSpace, diffCompiler);
				
				// Finally, fix the weapon text.
				textData.setStringAtIndex(FE7Data.ModeSelectTextLynWeaponTypeIndex, lynClass.primaryWeaponType() + "[X]");
				textData.setStringAtIndex(FE7Data.ModeSelectTextEliwoodWeaponTypeIndex, eliwoodClass.primaryWeaponType() + "[X]");
				textData.setStringAtIndex(FE7Data.ModeSelectTextHectorWeaponTypeIndex, hectorClass.primaryWeaponType() + "[X]");
				
				// Eliwood is the one we're going to override, since he normally shares the weapon string with Lyn.
				diffCompiler.addDiff(new Diff(FE7Data.ModeSelectEliwoodWeaponOffset, 2, 
						new byte[] {(byte)(FE7Data.ModeSelectTextEliwoodWeaponTypeIndex & 0xFF), (byte)((FE7Data.ModeSelectTextEliwoodWeaponTypeIndex >> 8) & 0xFF)}, null));
			}
		}
		
		if (gameType == GameType.FE7 || gameType == GameType.FE8) {
			// Fix world map sprites.
			if (gameType == GameType.FE7) {
				for (FE7Data.ChapterPointer chapter : FE7Data.ChapterPointer.values()) {
					Map<Integer, List<Integer>> perChapterMap = chapter.worldMapSpriteClassIDToCharacterIDMapping();
					GBAFEWorldMapData worldMapData = chapterData.worldMapEventsForChapterID(chapter.chapterID);
					if (worldMapData == null) { continue; }
					for (GBAFEWorldMapSpriteData sprite : worldMapData.allSprites()) {
						// If it's a class we don't touch, ignore it.
						if (classData.classForID(sprite.getClassID()) == null) { continue; }
						// Check Universal list first.
						Integer characterID = FE7Data.ChapterPointer.universalWorldMapSpriteClassIDToCharacterIDMapping().get(sprite.getClassID());
						if (characterID != null) {
							if (characterID == FE7Data.Character.NONE.ID) { continue; }
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
			else {
				for (FE8Data.ChapterPointer chapter : FE8Data.ChapterPointer.values()) {
					Map<Integer, List<Integer>> perChapterMap = chapter.worldMapSpriteClassIDToCharacterIDMapping();
					GBAFEWorldMapData worldMapData = chapterData.worldMapEventsForChapterID(chapter.chapterID);
					for (GBAFEWorldMapSpriteData sprite : worldMapData.allSprites()) {
						// If it's a class we don't touch, ignore it.
						if (classData.classForID(sprite.getClassID()) == null) { continue; }
						// Check Universal list first.
						Integer characterID = FE8Data.ChapterPointer.universalWorldMapSpriteClassIDToCharacterIDMapping().get(sprite.getClassID());
						if (characterID != null) {
							if (characterID == FE8Data.Character.NONE.ID) { continue; }
							syncWorldMapSpriteToCharacter(sprite, characterID);
						} else {
							// Check per chapter
							List<Integer> charactersForClassID = perChapterMap.get(sprite.getClassID());
							if (charactersForClassID != null && !charactersForClassID.isEmpty()) {
								int charID = charactersForClassID.remove(0);
								if (charID == FE8Data.Character.NONE.ID) {
									charactersForClassID.add(FE8Data.Character.NONE.ID);
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
		}
		
		if (gameType == GameType.FE8) {
			// Create the Trainee Seal using the old heaven seal.
			textData.setStringAtIndex(0x4AB, "Promotes Tier 0 Trainees at Lv 10.[X]");
			textData.setStringAtIndex(0x403, "Trainee Seal[X]");
			long offset = freeSpace.setValue(new byte[] {(byte)FE8Data.CharacterClass.TRAINEE.ID, (byte)FE8Data.CharacterClass.PUPIL.ID, (byte)FE8Data.CharacterClass.RECRUIT.ID}, "TraineeSeal");
			diffCompiler.addDiff(new Diff(FE8Data.HeavenSealPromotionPointer, 4, WhyDoesJavaNotHaveThese.bytesFromAddress(offset), WhyDoesJavaNotHaveThese.bytesFromAddress(FE8Data.HeavenSealOldAddress)));
			
			for (GBAFEChapterData chapter : chapterData.allChapters()) {
				for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
					FE8Data.CharacterClass charClass = FE8Data.CharacterClass.valueOf(chapterUnit.getStartingClass());
					if (FE8Data.CharacterClass.allTraineeClasses.contains(charClass)) {
						chapterUnit.giveItems(new int[] {FE8Data.Item.HEAVEN_SEAL.ID});
					}
				}
			}
		}
		
		// Make sure no non-playable non-thief units have lock picks, as they will softlock the game when the AI gets a hold of them.
		if (gameType == GameType.FE6) {
			for (GBAFEChapterData chapter : chapterData.allChapters()) {
				for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
					FE6Data.CharacterClass charClass = FE6Data.CharacterClass.valueOf(chapterUnit.getStartingClass());
					if (!FE6Data.CharacterClass.allThiefClasses.contains(charClass) && (chapterUnit.isNPC() || chapterUnit.isEnemy())) {
						chapterUnit.removeItem(FE6Data.Item.LOCKPICK.ID);
					}
				}
			}
		}
		
		// If the option is enabled, set the effectiveness for FE7 to triple.
		// TODO: FE9 could use this this if we could figure it out.
		if (gameType == GameType.FE7 && miscOptions.tripleEffectiveness) {
			// Replace bytes at 0x28B3E from 
			// 01 28 07 D1 30 88 EE F7 36 FB 29 1C 5A 31 0A 88 50 00 08 80 29 1C 5A 31
			// to
			// 29 1C 5A 31 01 28 07 D1 30 78 C0 46 C0 46 0A 88 XX 20 50 43 08 80 C0 46
			// where XX is the multiplier (03 in our case)
			diffCompiler.addDiff(new Diff(0x28B3E, 24, 
					new byte[] {(byte)0x29, (byte)0x1C, (byte)0x5A, (byte)0x31,
							(byte)0x01, (byte)0x28, (byte)0x07, (byte)0xD1,
							(byte)0x30, (byte)0x78, (byte)0xC0, (byte)0x46,
							(byte)0xC0, (byte)0x46, (byte)0x0A, (byte)0x88,
							(byte)0x03, (byte)0x20, (byte)0x50, (byte)0x43,
							(byte)0x08, (byte)0x80, (byte)0xC0, (byte)0x46}, 
					new byte[] {(byte)0x01, (byte)0x28, (byte)0x07, (byte)0xD1,
							(byte)0x30, (byte)0x88, (byte)0xEE, (byte)0xF7,
							(byte)0x36, (byte)0xFB, (byte)0x29, (byte)0x1C,
							(byte)0x5A, (byte)0x31, (byte)0x0A, (byte)0x88,
							(byte)0x50, (byte)0x00, (byte)0x08, (byte)0x80,
							(byte)0x29, (byte)0x1C, (byte)0x5A, (byte)0x31
					}));
		}
	}
	
	private void syncWorldMapSpriteToCharacter(GBAFEWorldMapSpriteData sprite, int characterID) {
		GBAFECharacterData character = charData.characterWithID(characterID);
		boolean spriteIsPromoted = classData.isPromotedClass(sprite.getClassID());
		int classID = character.getClassID();
		boolean characterClassIsPromoted = classData.isPromotedClass(classID);
		if (spriteIsPromoted == characterClassIsPromoted) {
			sprite.setClassID(classID);
		} else {
			if (spriteIsPromoted) {
				sprite.setClassID(classData.classForID(classID).getTargetPromotionID());
			} else {
				assert false : "This shouldn't ever be the case...";
			}
		}
	}
	
	public RecordKeeper initializeRecordKeeper() {
		int index = Math.max(targetPath.lastIndexOf('/'), targetPath.lastIndexOf('\\'));
		String title =  targetPath.substring(index + 1);
		String gameTitle = "(null)";
		switch (gameType) {
		case FE6:
			gameTitle = FE6Data.FriendlyName;
			break;
		case FE7:
			gameTitle = FE7Data.FriendlyName;
			break;
		case FE8:
			gameTitle = FE8Data.FriendlyName;
			break;
		default:
			gameTitle = "Unknown Game";
			break;
		}
		
		RecordKeeper rk = new RecordKeeper(title);
		
		rk.addHeaderItem("Game Title", gameTitle);
		
		if (growths != null) {
			switch (growths.mode) {
			case REDISTRIBUTE:
				rk.addHeaderItem("Randomize Growths", "Redistribution (" + growths.redistributionOption.variance + "% variance)");
				break;
			case DELTA:
				rk.addHeaderItem("Randomize Growths", "Delta (+/- " + growths.deltaOption.variance + "%)");
				break;
			case FULL:
				rk.addHeaderItem("Randomize Growths", "Full (" + growths.fullOption.minValue + "% ~ " + growths.fullOption.maxValue + "%)");	
				break;
			}
			
			rk.addHeaderItem("Adjust HP Growths", growths.adjustHP ? "YES" : "NO");
		} else {
			rk.addHeaderItem("Randomize Growths", "NO");
		}
		
		if (bases != null) {
			switch (bases.mode) {
			case REDISTRIBUTE:
				rk.addHeaderItem("Randomize Bases", "Redistribution (" + bases.redistributionOption.variance + " variance)");
				break;
			case DELTA:
				rk.addHeaderItem("Randomize Bases", "Delta (+/- " + bases.deltaOption.variance + ")");
				break;
			}
		} else {
			rk.addHeaderItem("Randomize Bases", "NO");
		}
		
		if (otherCharacterOptions.constitutionOptions != null) {
			rk.addHeaderItem("Randomize Constitution", "+/- " + otherCharacterOptions.constitutionOptions.variance + ", Min: " + otherCharacterOptions.constitutionOptions.minValue);
		} else {
			rk.addHeaderItem("Randomize Constitution", "NO");
		}
		
		if (otherCharacterOptions.movementOptions != null) {
			rk.addHeaderItem("Randomize Movement Ranges", "" + otherCharacterOptions.movementOptions.minValue + " ~ " + otherCharacterOptions.movementOptions.maxValue);
		} else {
			rk.addHeaderItem("Randomize Movement Ranges", "NO");
		}
		
		if (otherCharacterOptions.randomizeAffinity) {
			rk.addHeaderItem("Randomize Affinity", "YES");
		} else {
			rk.addHeaderItem("Randomize Affinity", "NO");
		}
		
		if (weapons.mightOptions != null) {
			rk.addHeaderItem("Randomize Weapon Power", "+/- " + weapons.mightOptions.variance + ", (" + weapons.mightOptions.minValue + " ~ " + weapons.mightOptions.maxValue + ")");
		} else {
			rk.addHeaderItem("Randomize Weapon Power", "NO");
		}
		if (weapons.hitOptions != null) {
			rk.addHeaderItem("Randomize Weapon Accuracy", "+/- " + weapons.hitOptions.variance + ", (" + weapons.hitOptions.minValue + " ~ " + weapons.hitOptions.maxValue + ")");
		} else {
			rk.addHeaderItem("Randomize Weapon Accuracy", "NO");
		}
		if (weapons.weightOptions != null) {
			rk.addHeaderItem("Randomize Weapon Weight", "+/- " + weapons.weightOptions.variance + ", (" + weapons.weightOptions.minValue + " ~ " + weapons.weightOptions.maxValue + ")");
		} else {
			rk.addHeaderItem("Randomize Weapon Weight", "NO");
		}
		if (weapons.durabilityOptions != null) {
			rk.addHeaderItem("Randomize Weapon Durability", "+/- " + weapons.durabilityOptions.variance + ", (" + weapons.durabilityOptions.minValue + " ~ " + weapons.durabilityOptions.maxValue + ")");
		} else {
			rk.addHeaderItem("Randomize Weapon Durability", "NO");
		}
		if (weapons.shouldAddEffects) {
			rk.addHeaderItem("Add Random Effects", "YES (" + weapons.effectChance + "%)");
			StringBuilder sb = new StringBuilder();
			sb.append("<ul>\n");
			if (weapons.effectsList.statBoosts) { sb.append("<li>Stat Boosts</li>\n"); }
			if (weapons.effectsList.effectiveness) { sb.append("<li>Effectiveness</li>\n"); }
			if (weapons.effectsList.unbreakable) { sb.append("<li>Unbreakable</li>\n"); }
			if (weapons.effectsList.brave) { sb.append("<li>Brave</li>\n"); }
			if (weapons.effectsList.reverseTriangle) { sb.append("<li>Reverse Triangle</li>\n"); }
			if (weapons.effectsList.extendedRange) { sb.append("<li>Extended Range</li>\n"); }
			if (weapons.effectsList.highCritical) { sb.append("<li>Critical</li>\n"); }
			if (weapons.effectsList.magicDamage) { sb.append("<li>Magic Damage</li>\n"); }
			if (weapons.effectsList.poison) { sb.append("<li>Poison</li>\n"); }
			if (weapons.effectsList.eclipse) { sb.append("<li>Eclipse</li>\n"); }
			if (weapons.effectsList.devil) { sb.append("<li>Devil</li>\n"); }
			sb.append("</ul>\n");
			rk.addHeaderItem("Random Effects Allowed", sb.toString());
			if (weapons.noEffectIronWeapons) {
				rk.addHeaderItem("Safe Basic Weapons", "YES");
			} else {
				rk.addHeaderItem("Safe Basic Weapons", "NO");
			}
		} else {
			rk.addHeaderItem("Add Random Effects", "NO");
		}
		
		if (classes.randomizePCs) {
			StringBuilder sb = new StringBuilder();
			
			if (classes.includeLords) {
				sb.append("Include Lords<br>");
			}
			if (classes.includeThieves) {
				sb.append("Include Thieves<br>");
			}
			if (classes.includeSpecial) {
				sb.append("Include Special Classes<br>");
			}
			if (classes.assignEvenly) {
				sb.append("Assign Evenly<br>");
			}
			if (sb.length() > 4) {
				sb.delete(sb.length() - 4, sb.length());
			}
			if (sb.length() == 0) { sb.append("YES"); }
			rk.addHeaderItem("Randomize Playable Character Classes", sb.toString());
		} else {
			rk.addHeaderItem("Randomize Playable Character Classes", "NO");
		}
		if (classes.randomizeBosses) {
			rk.addHeaderItem("Randomize Boss Classes", "YES");
		} else {
			rk.addHeaderItem("Randomize Boss Classes", "NO");
		}
		if (classes.randomizeEnemies) {
			rk.addHeaderItem("Randomize Minions", "YES");
		} else {
			rk.addHeaderItem("Randomize Minions", "NO");
		}
		if (classes.randomizePCs || classes.randomizeBosses) {
			switch (classes.basesTransfer) {
			case NO_CHANGE:
				rk.addHeaderItem("Base Stats Transfer Mode", "Retain Personal Bases");
				break;
			case ADJUST_TO_MATCH:
				rk.addHeaderItem("Base Stats Transfer Mode", "Retain Final Bases");
				break;
			case ADJUST_TO_CLASS:
				rk.addHeaderItem("Base Stats Transfer Mode", "Adjust to Class");
				break;
			}
		}
		if (classes.forceChange) {
			rk.addHeaderItem("Force Class Change", "YES");
		} else {
			rk.addHeaderItem("Force Class Change", "NO");
		}
		if (gameType == GameType.FE8) {
			if (classes.separateMonsters) {
				rk.addHeaderItem("Mix Monster and Human Classes", "NO");
			} else {
				rk.addHeaderItem("Mix Monster and Human Classes", "YES");
			}
		}
		
		switch (enemies.minionMode) {
		case NONE:
			rk.addHeaderItem("Buff Minions", "NO");
			break;
		case FLAT:
			rk.addHeaderItem("Buff Minions", "Flat Buff (Growths +" + enemies.minionBuff + "%)");
			break;
		case SCALING:
			rk.addHeaderItem("Buff Minions", "Scaling Buff (Growths x" + String.format("%.2f", (enemies.minionBuff / 100.0) + 1) + ")");
			break;
		}
		
		if (enemies.improveMinionWeapons) {
			rk.addHeaderItem("Improve Minion Weapons", "" + enemies.minionImprovementChance + "% of enemies");
		} else {
			rk.addHeaderItem("Improve Minion Weapons", "NO");
		}
		
		switch (enemies.bossMode) {
		case NONE:
			rk.addHeaderItem("Buff Bosses", "NO");
			break;
		case LINEAR:
			rk.addHeaderItem("Buff Bosses", "Linear - Max Gain: +" + enemies.bossBuff);
			break;
		case EASE_IN_OUT:
			rk.addHeaderItem("Buff Bosses", "Ease In/Ease Out - Max Gain: +" + enemies.bossBuff);
			break;
		}
		
		if (enemies.improveBossWeapons) {
			rk.addHeaderItem("Improve Boss Weapons", "" + enemies.bossImprovementChance + "% of bosses");
		} else {
			rk.addHeaderItem("Improve Boss Weapons", "NO");
		}
		
		if (miscOptions.randomizeRewards) {
			rk.addHeaderItem("Randomize Rewards", "YES");
		} else {
			rk.addHeaderItem("Randomize Rewards", "NO");
		}
		
		if (recruitOptions != null) {
			StringBuilder sb = new StringBuilder();
			
			if (recruitOptions.allowCrossGender) {
				sb.append("Allow Cross-Gender<br>");
			}
			
			switch (recruitOptions.classMode) {
			case USE_FILL:
				sb.append("Use Fill Class<br>");
				break;
			case USE_SLOT:
				sb.append("Use Slot Class<br>");
				break;
			}
			
			switch (recruitOptions.growthMode) {
			case USE_FILL:
				sb.append("Use Fill Growths<br>");
				break;
			case USE_SLOT:
				sb.append("Use Slot Growths<br>");
				break;
			case RELATIVE_TO_SLOT:
				sb.append("Use Slot Relative Growths<br>");
				break;
			}
			
			switch (recruitOptions.baseMode) {
			case AUTOLEVEL:
				sb.append("Autolevel Base Stats<br>");
				switch (recruitOptions.autolevelMode) {
				case USE_ORIGINAL:
					sb.append("Autolevel w/ Original Growths");
					break;
				case USE_NEW:
					sb.append("Autolevel w/ New Growths");
					break;
				}
				break;
			case RELATIVE_TO_SLOT:
				sb.append("Relative Base Stats");
				break;
			case MATCH_SLOT:
				sb.append("Match Base Stats");
				break;
			}
			
			if (recruitOptions.includeLords) {
				sb.append("<br>Include Lords");
			}
			if (recruitOptions.includeThieves) {
				sb.append("<br>Include Thieves");
			}
			if (recruitOptions.includeSpecial) {
				sb.append("<br>Include Special Characters");
			}
			
			rk.addHeaderItem("Randomize Recruitment", sb.toString());
		} else {
			rk.addHeaderItem("Randomize Recruitment", "NO");
		}
		
		if (itemAssignmentOptions != null) {
			switch (itemAssignmentOptions.weaponPolicy) {
			case STRICT:
				rk.addHeaderItem("Weapon Assignment Policy", "Strict Matching");
				break;
			case EQUAL_RANK:
				rk.addHeaderItem("Weapon Assignment Policy", "Match Rank");
				break;
			case ANY_USABLE:
				rk.addHeaderItem("Weapon Assignment Policy", "Random");
				break;
			}
		}
		
		return rk;
	}
}
