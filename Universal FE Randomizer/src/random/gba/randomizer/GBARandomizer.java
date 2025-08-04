package random.gba.randomizer;

import fedata.gba.*;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe6.FE6SpellAnimationCollection;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe7.FE7SpellAnimationCollection;
import fedata.gba.fe8.*;
import fedata.gba.general.GBAFEChapterMetadataChapter;
import fedata.gba.general.GBAFEChapterMetadataData;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import io.DiffApplicator;
import io.FileHandler;
import io.UPSPatcher;
import random.gba.loader.*;
import io.UPSPatcherStatusListener;
import random.gba.loader.ItemDataLoader.AdditionalData;
import random.gba.randomizer.shuffling.CharacterShuffler;
import random.general.Randomizer;
import ui.model.*;
import ui.model.CharacterShufflingOptions.ShuffleLevelingMode;
import ui.model.EnemyOptions.BossStatMode;
import ui.model.ItemAssignmentOptions.ShopAdjustment;
import ui.model.ItemAssignmentOptions.WeaponReplacementPolicy;
import util.*;
import util.recordkeeper.RecordKeeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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
	private StatboosterOptions statboosterOptions;
	private EnemyOptions enemies;
	private GameMechanicOptions miscOptions;
	private RewardOptions rewardOptions;
	private RecruitmentOptions recruitOptions;
	private ItemAssignmentOptions itemAssignmentOptions;
	private CharacterShufflingOptions shufflingOptions;
	private PrfOptions prfs;
	private ShopOptions shopOptions;
	
	private CharacterDataLoader charData;
	private ClassDataLoader classData;
	private StatboostLoader statboostData;
	private ChapterLoader chapterData;
	private ItemDataLoader itemData;
	private PaletteLoader paletteData;
	private TextLoader textData;
	private PortraitDataLoader portraitData;
	private ShopLoader shopData;
	
	private boolean needsPaletteFix;
	private Map<GBAFECharacterData, GBAFECharacterData> characterMap; // valid with random recruitment. Maps slots to reference character.
	
	// FE8 only
	private FE8PromotionManager fe8_promotionManager;
	private FE8PaletteMapper fe8_paletteMapper;
	private FE8SummonerModule fe8_summonerModule;
	
	private String seedString;
	
	private FreeSpaceManager freeSpace;
	
	private FileHandler handler;
	
	private boolean fe8_walkingSoundFixApplied = false;

	public GBARandomizer(String sourcePath, String targetPath, FEBase.GameType gameType, DiffCompiler diffs, 
			GrowthOptions growths, BaseOptions bases, ClassOptions classes, WeaponOptions weapons,
			OtherCharacterOptions other, EnemyOptions enemies, GameMechanicOptions otherOptions,
			RecruitmentOptions recruit, ItemAssignmentOptions itemAssign, CharacterShufflingOptions shufflingOptions, StatboosterOptions statboosterOptions, 
			RewardOptions rewards, PrfOptions prfs, ShopOptions shopOptions, String seed) {
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
		this.statboosterOptions = statboosterOptions;
		this.shufflingOptions = shufflingOptions;
		this.rewardOptions = rewards;
		this.prfs = prfs;
		this.shopOptions = shopOptions;
		if (itemAssignmentOptions == null) { itemAssignmentOptions = new ItemAssignmentOptions(WeaponReplacementPolicy.ANY_USABLE, ShopAdjustment.NO_CHANGE, false, false); }
		
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
					Boolean success = UPSPatcher.applyUPSPatch("FE6Localization_v1.1.ups", sourcePath, tempPath, null);
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
		itemData.recordItems(recordKeeper, true, textData, handler);
		chapterData.recordChapters(recordKeeper, true, charData, classData, itemData, textData);
		shopData.recordShopData(recordKeeper, true, itemData, textData);
		
		paletteData.recordReferencePalettes(recordKeeper, charData, classData, textData);
		statboostData.recordInitial(recordKeeper, itemData, statboosterOptions);
		
		makePreliminaryAdjustments();
		
		updateStatusString("Randomizing...");
		try { randomizeRecruitmentIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing recruitment.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.40);
		try { shuffleCharactersIfNecessary(seed);} catch(Exception e) {notifyError("Encountered error while shuffling in Characters.\n\n" + e.getClass().getSimpleName() + "\n\nStackTrace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.45);
		try { randomizeClassesIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing classes.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; } 
		updateProgress(0.50);
		try { randomizeBasesIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing bases.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.55);
		try { randomizeWeaponsIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing weapons.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.58);
		try { randomizeShopsIfNessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing shops.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n",  Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.60);
		try { randomizeOtherCharacterTraitsIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing other character traits.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.65);
		try { buffEnemiesIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while buffing enemies.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.70);
		try { randomizeOtherThingsIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing miscellaneous settings.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; } // i.e. Miscellaneous options.
		updateProgress(0.75);
		try { randomizeStatboostersIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing miscellaneous settings.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; } // i.e. Miscellaneous options.
		updateProgress(0.70);
		try { randomizeGrowthsIfNecessary(seed); } catch (Exception e) { notifyError("Encountered error while randomizing growths.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateProgress(0.90);
		try { makeFinalAdjustments(seed); } catch (Exception e) { notifyError("Encountered error while making final adjustments.\n\n" + e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); return; }
		updateStatusString("Compiling changes...");
		updateProgress(0.95);
		charData.compileDiffs(diffCompiler);
		chapterData.compileDiffs(diffCompiler);
		classData.compileDiffs(diffCompiler, handler, freeSpace);
		itemData.compileDiffs(diffCompiler, handler);
		shopData.compileDiffs(diffCompiler);
		paletteData.compileDiffs(diffCompiler);
		textData.commitChanges(freeSpace, diffCompiler);
		portraitData.compileDiffs(diffCompiler);
		statboostData.compileDiffs(diffCompiler);
		
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
		itemData.recordItems(recordKeeper, false, textData, targetFileHandler);
		chapterData.recordChapters(recordKeeper, false, charData, classData, itemData, textData);
		statboostData.recordUpdated(recordKeeper, itemData, statboosterOptions);
		shopData.recordShopData(recordKeeper, false, itemData, textData);
		
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
		textData = new TextLoader(FEBase.GameType.FE7, FE7Data.textProvider, handler);
		textData.allowTextChanges = true;

		updateStatusString("Loading Portrait Data...");
		updateProgress(0.07);
		portraitData = new PortraitDataLoader(FE7Data.shufflingDataProvider, handler);
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
		updateStatusString("Loading Shop Data...");
		updateProgress(0.27);
		shopData = new ShopLoader(FE7Data.shopProvider, FE7Data.itemProvider, itemData, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE7, handler, charData, classData);
		updateStatusString("Loading Statboost Data...");
		updateProgress(0.31);
		statboostData = new StatboostLoader(FE7Data.statboostProvider, handler);
		
		handler.clearAppliedDiffs();
	}
	
	private void generateFE6DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE6, FE6Data.InternalFreeRange, handler);
		updateStatusString("Loading Text...");
		updateProgress(0.05);
		textData = new TextLoader(FEBase.GameType.FE6, FE6Data.textProvider, handler);
		if (miscOptions.applyEnglishPatch) {
			textData.allowTextChanges = true;
		}

		updateStatusString("Loading Portrait Data...");
		updateProgress(0.07);
		portraitData = new PortraitDataLoader(FE6Data.shufflingDataProvider, handler);
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
		updateStatusString("Loading Shop Data...");
		updateProgress(0.27);
		shopData = new ShopLoader(FE6Data.shopProvider, FE6Data.itemProvider, itemData, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE6, handler, charData, classData);
		updateStatusString("Loading Statboost Data...");
		updateProgress(0.31);
		statboostData = new StatboostLoader(FE6Data.statboostProvider, handler);
		
		
		handler.clearAppliedDiffs();
	}
	
	private void generateFE8DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE8, FE8Data.InternalFreeRange, handler);
		updateStatusString("Loading Text...");
		updateProgress(0.04);
		textData = new TextLoader(FEBase.GameType.FE8, FE8Data.textProvider, handler);
		textData.allowTextChanges = true;
		
		updateStatusString("Loading Promotion Data...");
		updateProgress(0.06);
		fe8_promotionManager = new FE8PromotionManager(handler);

		updateStatusString("Loading Portrait Data...");
		updateProgress(0.07);
		portraitData = new PortraitDataLoader(FE8Data.shufflingDataProvider, handler);
		
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
		updateStatusString("Loading Shop Data...");
		updateProgress(0.27);
		shopData = new ShopLoader(FE8Data.shopProvider, FE8Data.itemProvider, itemData, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE8, handler, charData, classData);
		updateStatusString("Loading Statboost Data...");
		updateProgress(0.31);
		statboostData = new StatboostLoader(FE8Data.statboostProvider, handler);
		
		updateStatusString("Loading Summoner Module...");
		updateProgress(0.35);
		fe8_summonerModule = new FE8SummonerModule(handler);
		
		updateStatusString("Loading Palette Mapper...");
		updateProgress(0.40);
		fe8_paletteMapper = paletteData.setupFE8SpecialManagers(handler, fe8_promotionManager);
		
		
		handler.clearAppliedDiffs();
	}
	
	public void shuffleCharactersIfNecessary(String seed) {
		if(shufflingOptions != null && shufflingOptions.isShuffleEnabled()) {
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, GrowthsRandomizer.rngSalt));
			CharacterShuffler shuffler = new CharacterShuffler(gameType, charData, textData, rng, handler, portraitData, freeSpace, chapterData, classData, shufflingOptions, itemAssignmentOptions, itemData);
			shuffler.shuffleCharacters();
			needsPaletteFix = true;
		}
	}
	
	private void randomizeGrowthsIfNecessary(String seed) {
		if (growths != null) {
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, GrowthsRandomizer.rngSalt));
			switch (growths.mode) {
			case REDISTRIBUTE:
				updateStatusString("Redistributing growths...");
				GrowthsRandomizer.randomizeGrowthsByRedistribution(growths.redistributionOption.variance, growths.redistributionOption.minValue, growths.redistributionOption.maxValue, growths.adjustHP, charData, rng);
				break;
			case DELTA:
				updateStatusString("Applying random deltas to growths...");
				GrowthsRandomizer.randomizeGrowthsByRandomDelta(growths.deltaOption.variance, growths.deltaOption.minValue, growths.deltaOption.maxValue, growths.adjustHP, charData, rng);
				break;
			case FULL:
				updateStatusString("Randomizing growths...");
				GrowthsRandomizer.fullyRandomizeGrowthsWithRange(growths.fullOption.minValue, growths.fullOption.maxValue, growths.adjustHP, charData, rng);
				break;
			case SMART:
				updateStatusString("Smart Randomizing growths...");
				GrowthsRandomizer.smartRandomizeGrowths(charData, classData, rng);
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
			case SMART:
				updateStatusString("Smart Randomizing bases...");
				BasesRandomizer.smartRandomizeBases(charData, classData, chapterData, rng);
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
	
	private void randomizeShopsIfNessary(String seed) {
		if (shopOptions != null) {
			updateStatusString("Randomizing Shops...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, ShopRandomizer.rngSalt));
			ShopRandomizer.randomizeShops(shopData, itemData, false, false, shopOptions.shopSize.minValue, shopOptions.shopSize.maxValue, rng);
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
				EnemyBuffer.buffMinionGrowthRates(enemies.minionBuff, classData, enemies.minionBuffStats);
			} else if (enemies.minionMode == EnemyOptions.MinionGrowthMode.SCALING) {
				updateStatusString("Buffing enemies...");
				EnemyBuffer.scaleEnemyGrowthRates(enemies.minionBuff, classData, enemies.minionBuffStats);
			}
			
			if (enemies.improveMinionWeapons) {
				updateStatusString("Upgrading enemy weapons...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, EnemyBuffer.rngSalt));
				EnemyBuffer.improveMinionWeapons(enemies.minionImprovementChance, charData, classData, chapterData, itemData, rng);
			}
			
			if (enemies.bossMode == BossStatMode.LINEAR) {
				updateStatusString("Buffing Bosses...");
				EnemyBuffer.buffBossStatsLinearly(enemies.bossBuff, charData, classData, enemies.bossBuffStats);
			} else if (enemies.bossMode == BossStatMode.EASE_IN_OUT) {
				updateStatusString("Buffing Bosses...");
				EnemyBuffer.buffBossStatsWithEaseInOutCurve(enemies.bossBuff, charData, classData, enemies.bossBuffStats);
			}
			
			if (enemies.improveBossWeapons) {
				updateStatusString("Upgrading boss weapons...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, EnemyBuffer.rngSalt + 1));
				EnemyBuffer.improveBossWeapons(enemies.bossImprovementChance, charData, classData, chapterData, itemData, rng);
			}
		}
	}
	
	private void randomizeStatboostersIfNecessary(String seed) {
		Random rng = new Random(SeedGenerator.generateSeedValue(seed, StatboosterRandomizer.SALT));
		StatboosterRandomizer.randomize(statboosterOptions, statboostData, itemData, textData, rng);
	}
	
	private void randomizeOtherThingsIfNecessary(String seed) {
		if (rewardOptions != null) {
			if (rewardOptions.randomizeRewards) {
				updateStatusString("Randomizing rewards...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, RandomRandomizer.rngSalt));
				RandomRandomizer.randomizeRewards(itemData, chapterData, itemAssignmentOptions.assignPromoWeapons, rng);
			}

			if (rewardOptions.enemyDropChance > 0) {
				if (gameType == GameType.FE7) {
					// Change the code at 0x17826 from
					// 20 68 61 68 80 6A 89 6A 08 43 80 21 09 05 08 40
					// to
					// 20 1C 41 30 00 78 40 21 08 40 00 00 00 00 00 00
					// This will allow us to set the 4th AI bit for units to drop the last item if
					// the 0x40 bit is set.
					diffCompiler.addDiff(new Diff(0x17826, 16,
							new byte[]{(byte) 0x20, (byte) 0x1C, (byte) 0x41, (byte) 0x30,
									(byte) 0x00, (byte) 0x78, (byte) 0x40, (byte) 0x21,
									(byte) 0x08, (byte) 0x40, (byte) 0x00, (byte) 0x00,
									(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00

							},
							new byte[]{(byte) 0x20, (byte) 0x68, (byte) 0x61, (byte) 0x68,
									(byte) 0x80, (byte) 0x6A, (byte) 0x89, (byte) 0x6A,
									(byte) 0x08, (byte) 0x43, (byte) 0x80, (byte) 0x21,
									(byte) 0x09, (byte) 0x05, (byte) 0x08, (byte) 0x40
							}));
				}
				updateStatusString("Adding drops...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, RandomRandomizer.rngSalt + 1));
				RandomRandomizer.addRandomEnemyDrops(rewardOptions.enemyDropChance, charData, itemData, chapterData, rng);
			}
		}
		if (miscOptions != null) {
			if (miscOptions.randomizeFogOfWar) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, RandomRandomizer.rngSalt + 2));
				for (GBAFEChapterMetadataChapter chapter : chapterData.getMetadataChapters()) {
					GBAFEChapterMetadataData chapterMetadata = chapterData.getMetadataForChapter(chapter);
					if (chapterMetadata.getVisionRange() > 0) { continue; }
					if (rng.nextInt(100) < miscOptions.fogOfWarChance) {
						int visionRange = rng.nextInt(miscOptions.fogOfWarVisionRange.maxValue - miscOptions.fogOfWarVisionRange.minValue + 1) + miscOptions.fogOfWarVisionRange.minValue;
						chapterMetadata.setVisionRange(visionRange);
						chapterMetadata.commitChanges();
					}
				}
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
	
	private void makePreliminaryAdjustments() {
		// FE8 Walking sound effect fix.
		// From Tequila's patch.
		if (gameType == GameType.FE8) {
			try {
				InputStream stream = UPSPatcher.class.getClassLoader().getResourceAsStream("fe8_walking_sound_fix.bin");
				byte[] fixData = new byte[0x14C];
				stream.read(fixData);
				stream.close();
				
				diffCompiler.addDiff(new Diff(0x78d78, fixData.length, fixData, null));
				
				fe8_walkingSoundFixApplied = true;
			} catch (Exception e) {
				
			}
		}
		
		// Some characters have discrepancies between character data and chapter data. We'll try to address that before we get to any modifications.
		charData.applyLevelCorrectionsIfNecessary();
		
		itemData.prepareForRandomization(gameType, diffCompiler);
	}
	
	private void makeFinalAdjustments(String seed) {
		
		// If we need RNG, set one up here.
		Random rng = new Random(SeedGenerator.generateSeedValue(seed, 1));
		
		// Fix the palettes based on final classes.
		if (needsPaletteFix) {
			PaletteHelper.synchronizePalettes(gameType, recruitOptions != null ? recruitOptions.includeExtras : false, charData, classData, paletteData, characterMap, freeSpace);
			charData.commit();
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
		
		// Chapter 16 (Whereabouts Unknown) has a tutorial check that keeps appearing even if we're not in tutorial.
		// I suspect the flag for tutorial is always set for some reason.
		// The proper way is to interpret the EA properly, but we can just change specifically what we want to 
		// get the desired results (i.e. always skip the tutorial text).
		if (gameType == GameType.FE7) {
			// All we need to do is replace the check for the tutorial with a GOTO 0x32, which is where it goes if the check fails (i.e. the tutorial is not active).
			// This should be at 0xCB163C.
			diffCompiler.addDiff(new Diff(0xCB163CL, 8, new byte[] {0x45, 0, 0, 0, 0x32, 0, 0, 0}, new byte[] {0x54, 0, 0, 0, 0x32, 0, 0, 0}));
			
			// Lucius's inventory in this chapter is set with an ASMC call, so we need to modify that item directly.
			// Get a basic weapon for him.
			GBAFECharacterData lucius = charData.characterWithID(FE7Data.Character.LUCIUS.ID);
			GBAFEItemData luciusItem = itemData.getBasicWeaponForCharacter(lucius, false, false, rng);
			// This byte lives at 0x7d0c2, which in ASM is a MOV constant into r0. Normally it's 0x3E (which is Lightning).
			diffCompiler.addDiff(new Diff(0x7D0C2L, 1, new byte[] {(byte)luciusItem.getID()}, new byte[] {0x3E}));
			// In case it's useful, he also gets a Vulnerary that's set at 0x7D0D8, which is normally 0x6B for a Vulnerary.
		}

		// For some reason, FE7's Emblem Bow has no effectiveness added to it.
		if (gameType == GameType.FE7) {
			GBAFEItemData emblemBow = itemData.itemWithID(FE7Data.Item.EMBLEM_BOW.ID);
			emblemBow.setEffectivenessPointer(itemData.flierEffectPointer());
			
			// FE7's Aureola is also an S-rank tome with no cost associated with it. Since it can be randomized into shops now, give it the same cost as other S rank weapons (i.e. 600 per use).
			GBAFEItemData aureola = itemData.itemWithID(FE7Data.Item.AUREOLA.ID);
			aureola.setCostPerUse(600);
			
			// None of the Dancer Rings have costs either. Give them an appropriate cost. They have 15 uses each. For now, let's try a total cost of 6000 (400 per use).
			for (FE7Data.Item ring : FE7Data.Item.allDancingRings) {
				GBAFEItemData ringItem = itemData.itemWithID(ring.ID);
				ringItem.setCostPerUse(400);
			}
			
			// Emblem weapons and Poison weapons also have no cost. Give them the same cost per use as their iron counterparts.
			GBAFEItemData emblemSword = itemData.itemWithID(FE7Data.Item.EMBLEM_SWORD.ID);
			GBAFEItemData poisonSword = itemData.itemWithID(FE7Data.Item.POISON_SWORD.ID);
			GBAFEItemData ironSword = itemData.itemWithID(FE7Data.Item.IRON_SWORD.ID);
			emblemSword.setCostPerUse(ironSword.getCostPerUse());
			poisonSword.setCostPerUse(ironSword.getCostPerUse());
			
			GBAFEItemData emblemLance = itemData.itemWithID(FE7Data.Item.EMBLEM_LANCE.ID);
			GBAFEItemData poisonLance = itemData.itemWithID(FE7Data.Item.POISON_LANCE.ID);
			GBAFEItemData ironLance = itemData.itemWithID(FE7Data.Item.IRON_LANCE.ID);
			emblemLance.setCostPerUse(ironLance.getCostPerUse());
			poisonLance.setCostPerUse(ironLance.getCostPerUse());
			
			
			GBAFEItemData emblemAxe = itemData.itemWithID(FE7Data.Item.EMBLEM_AXE.ID);
			GBAFEItemData poisonAxe = itemData.itemWithID(FE7Data.Item.POISON_AXE.ID);
			GBAFEItemData ironAxe = itemData.itemWithID(FE7Data.Item.IRON_AXE.ID);
			poisonAxe.setCostPerUse(ironAxe.getCostPerUse());
			emblemAxe.setCostPerUse(ironAxe.getCostPerUse());
			
			GBAFEItemData ironBow = itemData.itemWithID(FE7Data.Item.IRON_BOW.ID);
			GBAFEItemData poisonBow = itemData.itemWithID(FE7Data.Item.POISON_BOW.ID);
			poisonBow.setCostPerUse(ironBow.getCostPerUse());
			emblemBow.setCostPerUse(ironBow.getCostPerUse());
			
			// Afa Drops are also free. Give it an expensive cost (10,000?)
			GBAFEItemData afaDrops = itemData.itemWithID(FE7Data.Item.AFA_DROPS.ID);
			afaDrops.setCostPerUse(10000);
			
			// Emblem Seal is also free. It's not nearly as useful, IMO.
			GBAFEItemData emblemSeal = itemData.itemWithID(FE7Data.Item.EMBLEM_SEAL.ID);
			emblemSeal.setCostPerUse(1000); 
			
			// Female Paladins (i.e. Isadora) actually have a custom promoted sprite (0x9). Any character that has this class should use that custom sprite.
			List<GBAFECharacterData> allCharacters = charData.characterList();
			List<GBAFECharacterData> femalePaladins = allCharacters.stream().filter(character -> {
				return character.getClassID() == FE7Data.CharacterClass.PALADIN_F.ID;
			}).collect(Collectors.toList());
			femalePaladins.stream().forEach(character -> {
				DebugPrinter.log(DebugPrinter.Key.CLASS_RANDOMIZER, "Giving female paladin " + character.displayString() + " custom promoted battle sprite 0x9.");
				character.setCustomPromotedBattleSprite(0x9); // TODO: Document the constants used here.
				character.commitChanges();
			});
		}
		
		// Allow FE6 to be able to use hard mode without needing clear data.
		if (gameType == GameType.FE6) {
			diffCompiler.addDiff(new Diff(FE6Data.HardModeHackOffset, FE6Data.HardModeHackNewData.length, FE6Data.HardModeHackNewData, FE6Data.HardModeHackOriginalData));
			
			// Now that shops are randomizable, give costs to items that may not have had them. For FE6, it's just poison weapons.
			GBAFEItemData poisonSword = itemData.itemWithID(FE6Data.Item.POISON_SWORD.ID);
			GBAFEItemData ironSword = itemData.itemWithID(FE6Data.Item.IRON_SWORD.ID);
			poisonSword.setCostPerUse(ironSword.getCostPerUse());
			
			GBAFEItemData poisonLance = itemData.itemWithID(FE6Data.Item.POISON_LANCE.ID);
			GBAFEItemData ironLance = itemData.itemWithID(FE6Data.Item.IRON_LANCE.ID);
			poisonLance.setCostPerUse(ironLance.getCostPerUse());
			
			GBAFEItemData poisonAxe = itemData.itemWithID(FE6Data.Item.POISON_AXE.ID);
			GBAFEItemData ironAxe = itemData.itemWithID(FE6Data.Item.IRON_AXE.ID);
			poisonAxe.setCostPerUse(ironAxe.getCostPerUse());
			
			GBAFEItemData poisonBow = itemData.itemWithID(FE6Data.Item.POISON_BOW.ID);
			GBAFEItemData ironBow = itemData.itemWithID(FE6Data.Item.IRON_BOW.ID);
			poisonBow.setCostPerUse(ironBow.getCostPerUse());
		}
		
		// FE8 Item costs for shop randomization.
		if (gameType == GameType.FE8) {
			// Poison weapons, as usual.
			GBAFEItemData poisonSword = itemData.itemWithID(FE8Data.Item.POISON_SWORD.ID);
			GBAFEItemData ironSword = itemData.itemWithID(FE8Data.Item.IRON_SWORD.ID);
			poisonSword.setCostPerUse(ironSword.getCostPerUse());
			
			GBAFEItemData poisonLance = itemData.itemWithID(FE8Data.Item.TOXIN_LANCE.ID);
			GBAFEItemData ironLance = itemData.itemWithID(FE8Data.Item.IRON_LANCE.ID);
			poisonLance.setCostPerUse(ironLance.getCostPerUse());
			
			GBAFEItemData poisonAxe = itemData.itemWithID(FE8Data.Item.POISON_AXE.ID);
			GBAFEItemData ironAxe = itemData.itemWithID(FE8Data.Item.IRON_AXE.ID);
			poisonAxe.setCostPerUse(ironAxe.getCostPerUse());
			
			GBAFEItemData poisonBow = itemData.itemWithID(FE8Data.Item.POISON_BOW.ID);
			GBAFEItemData ironBow = itemData.itemWithID(FE8Data.Item.IRON_BOW.ID);
			poisonBow.setCostPerUse(ironBow.getCostPerUse());
			
			// Hatchet doesn't have one either.
			// For reference, Iron Swords have 10 gold vs Slim Sword's 16 gold.
			// Hand Axes cost 15 gold, so it should be slightly more for the benefits of higher accuracy and lighter weight.
			// At 20 gold per use x 50 uses, that's 1000 gold per Hatchet to Hand Axe's 300 and Tomahawk's 3000.
			GBAFEItemData hatchet = itemData.itemWithID(FE8Data.Item.HATCHET.ID);
			hatchet.setCostPerUse(20);
			
			// The promo monster-slaying weapons also have no cost. Since they are
			// effective against monsters, they should cost a bit more than Iron counterparts.
			// They also have 60 uses, so they will naturally be more expensive.
			// Using Iron level weapons as a reference, Axes are the cheapest, followed by Lances, then Swords, then Bows.
			// Effective weapons are generally a decent bit more expensive, so we should be aiming towards the 3000 - 5000 range, since they have 60 uses.
			// This gives them an effective cost per use between 50 and 83.
			GBAFEItemData fiendCleaver = itemData.itemWithID(FE8Data.Item.FIENDCLEAVER.ID);
			GBAFEItemData brightLance = itemData.itemWithID(FE8Data.Item.BRIGHT_LANCE.ID);
			GBAFEItemData shadowKiller = itemData.itemWithID(FE8Data.Item.SHADOWKILLER.ID);
			GBAFEItemData beaconBow = itemData.itemWithID(FE8Data.Item.BEACON_BOW.ID);
			fiendCleaver.setCostPerUse(50); // 3000
			brightLance.setCostPerUse(61); // 3660
			shadowKiller.setCostPerUse(72); // 4320
			beaconBow.setCostPerUse(83); // 4980
			
			// Metis's Tome also needs a cost.
			GBAFEItemData metisTome = itemData.itemWithID(FE8Data.Item.METIS_TOME.ID);
			metisTome.setCostPerUse(10000);
			
			// Juna Fruit also doesn't have anything set.
			GBAFEItemData junaFruit = itemData.itemWithID(FE8Data.Item.JUNA_FRUIT.ID);
			junaFruit.setCostPerUse(5000);
		}
		
		// Hack in mode select without needing clear data for FE7.
		if (gameType == GameType.FE7) {
			diffCompiler.addDiff(new Diff(FE7Data.ModeSelectHackOffset, FE7Data.ModeSelectHackNewData.length, FE7Data.ModeSelectHackNewData, FE7Data.ModeSelectHackOriginalData));
			
			// Fix up the portraits in mode select, since they're hardcoded.
			// Only necessary if we randomized recruitment.
			// All of the data should have been commited at this point, so asking for Lyn will get you the Lyn replacement.
			if ((recruitOptions != null && recruitOptions.includeLords) || (classes != null && classes.includeLords)) {
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
				PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, handler, characterMap != null && characterMap.containsKey(lyn) ? characterMap.get(lyn) : lyn, lyn.getClassID(), paletteData, freeSpace, diffCompiler);
				PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, handler, characterMap != null && characterMap.containsKey(eliwood) ? characterMap.get(eliwood) : eliwood, eliwood.getClassID(), paletteData, freeSpace, diffCompiler);
				PaletteHelper.applyCharacterPaletteToSprite(GameType.FE7, handler, characterMap != null && characterMap.containsKey(hector) ? characterMap.get(hector) : hector, hector.getClassID(), paletteData, freeSpace, diffCompiler);

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
						chapterUnit.giveItems(new int[] {FE8Data.Item.HEAVEN_SEAL.ID}, itemData);
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

		// Make sure healing classes have at least one healing staff in their starting inventory.
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				GBAFEClassData unitClass = classData.classForID(chapterUnit.getStartingClass());
				if (unitClass == null) { continue; }
				if (unitClass.getStaffRank() != 0) {
					if (itemData.isHealingStaff(chapterUnit.getItem1()) || itemData.isHealingStaff(chapterUnit.getItem2()) ||
							itemData.isHealingStaff(chapterUnit.getItem3()) || itemData.isHealingStaff(chapterUnit.getItem4())) {
						continue;
					} else {
						if (charData.isPlayableCharacterID(chapterUnit.getCharacterNumber())) {
							GBAFECharacterData character = charData.characterWithID(chapterUnit.getCharacterNumber());
							GBAFEItemData healingStaff = itemData.getRandomHealingStaff(itemData.rankForValue(character.getStaffRank()), rng);
							if (healingStaff != null) {
								chapterUnit.giveItems(new int[] {healingStaff.getID()}, itemData);
							}
						}
					}
				}
			}
		}
		
		// Adjust Wire and/or Hector in FE7, since he has a high chance of softlocking Hector and Matthew in Ch. 11.
		// TODO: Maybe make this logic more generic to ensure winnable matchups.
		if (gameType == GameType.FE7) {
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
				int wireATK = wireSTR + wireWeapon.getMight() + (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
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
						wireATK = wireSTR + wireWeapon.getMight() + (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
						damageDealtToHector = wireATK - hectorDEF;
						totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);
					} else { // This is a pretty bad Hector if he can't take out a 0 AS, 0 STR Wire. Buff his DEF.
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
				int wireATK = wireMAG + wireWeapon.getMight() + (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
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
						wireATK = wireMAG + wireWeapon.getMight() + (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
						damageDealtToHector = wireATK - hectorRES;
						totalDamageDealt = damageDealtToHector + (wireDoublesHector ? damageDealtToHector : 0);
					} else { // This is a pretty bad Hector if he can't take out a 0 AS, 0 STR Wire. Buff his RES.
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
				int hectorATK = hectorSTR + hectorWeapon.getMight() - (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
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
						hectorATK = hectorSTR + hectorWeapon.getMight() - (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
						hectorDoublesWire = wireAS < hectorAS - 3;
						damageDealtToWire = hectorATK - wireDEF;
						totalDamageDealt = damageDealtToWire + (hectorDoublesWire ? damageDealtToWire : 0);
					}
				}
			} else {
				int wireRES = wire.getBaseRES() + wireClass.getBaseRES();
				int hectorSTR = hector.getBaseSTR() + hectorClass.getBaseSTR();
				
				int hectorAS = Math.max(0, hectorSPD + Math.min(0, hectorCON - hectorWeapon.getWeight()));
				int hectorATK = hectorSTR + hectorWeapon.getMight() - (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
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
						hectorATK = hectorSTR + hectorWeapon.getMight() - (wireWeapon.getType().typeAdvantage() == hectorWeapon.getType() ? 1 : 0);
						hectorDoublesWire = wireAS < hectorAS - 3;
						damageDealtToWire = hectorATK - wireRES;
						totalDamageDealt = damageDealtToWire + (hectorDoublesWire ? damageDealtToWire : 0);
					}
				}
			}
			
			// Make sure Hector has at least 5 (assuming boss weapons are buffed, as this gives them S rank) + Wire's SKL/2 Luck to prevent crits.
			hector.setBaseLCK(Math.max(hector.getBaseLCK(), (enemies.improveBossWeapons ? 5 : 0) + (wire.getBaseSKL() + wireClass.getBaseSKL()) / 2));
		}
		
		// Create special lord classes to prevent them from promoting prematurely.
		// Do this last, so that we don't mess up anything else that needs to read class IDs.
		if (gameType == GameType.FE6) {
			GBAFECharacterData roy = charData.characterWithID(FE6Data.Character.ROY.ID);
			
			int oldRoyClassID = roy.getClassID();
			
			GBAFEClassData newRoyClass = classData.createLordClassBasedOnClass(classData.classForID(oldRoyClassID));
			
			roy.setClassID(newRoyClass.getID());
			
			// We need to update the class lookup routine to take into account this new ID, since it's beyond
			// the normal bounds of the IDs the game searches through during his promotion scene.
			if (newRoyClass.getID() > (byte)0x3F) {
				diffCompiler.addDiff(new Diff(FE6Data.ClassLookupMaxIndexOffset, 1, new byte[] { (byte)((newRoyClass.getID() + 1) & 0xFF) }, new byte[] { (byte)0x3F }));	
			}
			
			// Add his new class to any effectiveness tables.
			List<AdditionalData> effectivenesses = itemData.effectivenessArraysForClassID(oldRoyClassID);
			for (AdditionalData effectiveness : effectivenesses) {
				itemData.addClassIDToEffectiveness(effectiveness, newRoyClass.getID());
			}
			
			// Incidentally, Roy doesn't need a promotion item, because his promotion is entirely scripted without any items.
			// We do need to make sure the weapon that's forced on him during promotion is one he can use though. Otherwise, the game will crash.
			GBAFEItemData bindingBlade = itemData.itemWithID(FE6Data.Item.BINDING_BLADE.ID);
			if (newRoyClass.canUseWeapon(bindingBlade) == false) {
				GBAFEItemData legendaryWeapon = itemData.legendaryWeaponForClass(newRoyClass, true, gameType);
				if (legendaryWeapon == null) {
					legendaryWeapon = itemData.basicItemOfType(newRoyClass.getWeaponRanks(true, gameType).getHighestRank());
				}
				diffCompiler.addDiff(new Diff(FE6Data.RoyPromotionForcedEquippedItemOffset, 1, new byte[] { (byte)(legendaryWeapon.getID()) }, new byte[] { (byte)0x0F} ));
			}
			
			for (GBAFEChapterData chapter : chapterData.allChapters()) {
				for (GBAFEChapterUnitData unit : chapter.allUnits()) {
					if (unit.getCharacterNumber() == FE6Data.Character.ROY.ID) {
						if (unit.getStartingClass() == oldRoyClassID) { unit.setStartingClass(newRoyClass.getID()); }
					}
				}
			}
			
			long mapSpriteTableOffset = FileReadHelper.readAddress(handler, FE6Data.ClassMapSpriteTablePointer);
			byte[] spriteTable = handler.readBytesAtOffset(mapSpriteTableOffset, FE6Data.BytesPerMapSpriteTableEntry * FE6Data.NumberOfMapSpriteEntries);
			long newSpriteTableOffset = freeSpace.setValue(spriteTable, "Repointed Sprite Table", true);
			freeSpace.setValue(WhyDoesJavaNotHaveThese.subArray(spriteTable, (oldRoyClassID - 1) * 8, 8), "Roy Map Sprite Entry");
			diffCompiler.findAndReplace(new FindAndReplace(WhyDoesJavaNotHaveThese.bytesFromAddress(mapSpriteTableOffset), WhyDoesJavaNotHaveThese.bytesFromAddress(newSpriteTableOffset), true));
		} else if (gameType == GameType.FE7) {
			GBAFECharacterData lyn = charData.characterWithID(FE7Data.Character.LYN.ID);
			GBAFECharacterData tutorialLyn = charData.characterWithID(FE7Data.Character.LYN_TUTORIAL.ID);
			GBAFECharacterData eliwood = charData.characterWithID(FE7Data.Character.ELIWOOD.ID);
			GBAFECharacterData hector = charData.characterWithID(FE7Data.Character.HECTOR.ID);
			
			int oldLynClassID = lyn.getClassID();
			int oldEliwoodClassID = eliwood.getClassID();
			int oldHectorClassID = hector.getClassID();
			
			GBAFEClassData newLynClass = classData.createLordClassBasedOnClass(classData.classForID(lyn.getClassID()));
			GBAFEClassData newEliwoodClass = classData.createLordClassBasedOnClass(classData.classForID(eliwood.getClassID()));
			GBAFEClassData newHectorClass = classData.createLordClassBasedOnClass(classData.classForID(hector.getClassID()));
			
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
			
			itemData.replaceClassesForPromotionItem(FE7Data.PromotionItem.ELIWOOD_LYN_HEAVEN_SEAL, new ArrayList<Integer>(Arrays.asList(newLynClass.getID(), newEliwoodClass.getID())));
			itemData.replaceClassesForPromotionItem(FE7Data.PromotionItem.HECTOR_LYN_HEAVEN_SEAL, new ArrayList<Integer>(Arrays.asList(newHectorClass.getID(), newLynClass.getID())));
			
			for (GBAFEChapterData chapter : chapterData.allChapters()) {
				for(GBAFEChapterUnitData unit : chapter.allUnits()) {
					if (unit.getCharacterNumber() == FE7Data.Character.LYN.ID || unit.getCharacterNumber() == FE7Data.Character.LYN_TUTORIAL.ID) {
						if (unit.getStartingClass() == oldLynClassID) { unit.setStartingClass(newLynClass.getID()); }
					} else if (unit.getCharacterNumber() == FE7Data.Character.ELIWOOD.ID) {
						if (unit.getStartingClass() == oldEliwoodClassID) { unit.setStartingClass(newEliwoodClass.getID()); }
					} else if (unit.getCharacterNumber() == FE7Data.Character.HECTOR.ID) {
						if (unit.getStartingClass() == oldHectorClassID) { unit.setStartingClass(newHectorClass.getID()); }
					}
				}
			}
			
			long mapSpriteTableOffset = FileReadHelper.readAddress(handler, FE7Data.ClassMapSpriteTablePointer);
			byte[] spriteTable = handler.readBytesAtOffset(mapSpriteTableOffset, FE7Data.BytesPerMapSpriteTableEntry * FE7Data.NumberOfMapSpriteEntries);
			long newSpriteTableOffset = freeSpace.setValue(spriteTable, "Repointed Sprite Table", true);
			freeSpace.setValue(WhyDoesJavaNotHaveThese.subArray(spriteTable, (oldLynClassID - 1) * 8, 8), "Lyn Map Sprite Entry");
			freeSpace.setValue(WhyDoesJavaNotHaveThese.subArray(spriteTable, (oldEliwoodClassID - 1) * 8, 8), "Eliwood Map Sprite Entry");
			freeSpace.setValue(WhyDoesJavaNotHaveThese.subArray(spriteTable, (oldHectorClassID - 1) * 8, 8), "Hector Map Sprite Entry");
			diffCompiler.findAndReplace(new FindAndReplace(WhyDoesJavaNotHaveThese.bytesFromAddress(mapSpriteTableOffset), WhyDoesJavaNotHaveThese.bytesFromAddress(newSpriteTableOffset), true));
			
		} else if (gameType == GameType.FE8) {
			GBAFECharacterData eirika = charData.characterWithID(FE8Data.Character.EIRIKA.ID);
			GBAFECharacterData ephraim = charData.characterWithID(FE8Data.Character.EPHRAIM.ID);
			
			int oldEirikaClass = eirika.getClassID();
			int oldEphraimClass = ephraim.getClassID();
			
			// GBAFE only stores 5 bits for the class (in save data), so using any ID greater than 0x7F will have issues. We have to replace an existing class.
			GBAFEClassData newEirikaClass = classData.createLordClassBasedOnClass(classData.classForID(oldEirikaClass), FE8Data.CharacterClass.UNUSED_TENT.ID); // This was a (unused?) tent.
			GBAFEClassData newEphraimClass = classData.createLordClassBasedOnClass(classData.classForID(oldEphraimClass), FE8Data.CharacterClass.UNUSED_MANAKETE.ID); // This is an unused manakete class.
			
			eirika.setClassID(newEirikaClass.getID());
			ephraim.setClassID(newEphraimClass.getID());
			
			// Add new classes to any effectiveness tables.
			List<AdditionalData> effectivenesses = itemData.effectivenessArraysForClassID(oldEirikaClass);
			for (AdditionalData effectiveness : effectivenesses) {
				itemData.addClassIDToEffectiveness(effectiveness, newEirikaClass.getID());
			}
			effectivenesses = itemData.effectivenessArraysForClassID(oldEphraimClass);
			for (AdditionalData effectiveness : effectivenesses) {
				itemData.addClassIDToEffectiveness(effectiveness, newEphraimClass.getID());
			}
			
			itemData.replaceClassesForPromotionItem(FE8Data.PromotionItem.LUNAR_BRACE, new ArrayList<Integer>(Arrays.asList(newEirikaClass.getID())));
			itemData.replaceClassesForPromotionItem(FE8Data.PromotionItem.SOLAR_BRACE, new ArrayList<Integer>(Arrays.asList(newEphraimClass.getID())));
			
			for (GBAFEChapterData chapter : chapterData.allChapters()) {
				for (GBAFEChapterUnitData unit : chapter.allUnits()) {
					if (unit.getCharacterNumber() == FE8Data.Character.EIRIKA.ID) {
						if (unit.getStartingClass() == oldEirikaClass) { unit.setStartingClass(newEirikaClass.getID()); }
					} else if (unit.getCharacterNumber() == FE8Data.Character.EPHRAIM.ID) {
						if (unit.getStartingClass() == oldEphraimClass) { unit.setStartingClass(newEphraimClass.getID()); /* unit.setStartingLevel(10); */}
					}
					/*
					if (unit.getCharacterNumber() == FE8Data.Character.ORSON_5X.ID) {
						unit.giveItem(itemData.itemsToPromoteClass(oldEphraimClass).get(0).getID());
						unit.giveItem(itemData.itemsToPromoteClass(newEphraimClass.getID()).get(0).getID());
					}*/
				}
			}
			
			// Update the promotions table, since they're technically "different" classes.
			fe8_promotionManager.setFirstPromotionOptionForClass(newEirikaClass.getID(), fe8_promotionManager.getFirstPromotionOptionClassID(oldEirikaClass));
			fe8_promotionManager.setSecondPromotionOptionForClass(newEirikaClass.getID(), fe8_promotionManager.getSecondPromotionOptionClassID(oldEirikaClass));
			fe8_promotionManager.setFirstPromotionOptionForClass(newEphraimClass.getID(), fe8_promotionManager.getFirstPromotionOptionClassID(oldEphraimClass));
			fe8_promotionManager.setSecondPromotionOptionForClass(newEphraimClass.getID(), fe8_promotionManager.getSecondPromotionOptionClassID(oldEphraimClass));
			
			// Palettes are also tied to class.
			FE8PaletteMapper.ClassMapEntry eirikaPalette = fe8_paletteMapper.getEntryForCharacter(FE8Data.Character.EIRIKA);
			FE8PaletteMapper.ClassMapEntry ephraimPalette = fe8_paletteMapper.getEntryForCharacter(FE8Data.Character.EPHRAIM);
			
			// Only base classes need to be updated. Promoted classes are not special.
			eirikaPalette.setBaseClassID(newEirikaClass.getID());
			ephraimPalette.setBaseClassID(newEphraimClass.getID());
			
			// On the bright side, we don't need to repoint the FE8 map sprite table. We just need to replace some entries in the existing one.
			long mapSpriteTableOffset = FileReadHelper.readAddress(handler, FE8Data.ClassMapSpriteTablePointer);
			long eirikaTargetOffset = (newEirikaClass.getID() - 1) * 8 + mapSpriteTableOffset;
			long ephraimTargetOffset = (newEphraimClass.getID() - 1) * 8 + mapSpriteTableOffset;
			byte[] eirikaSpriteData = handler.readBytesAtOffset((oldEirikaClass - 1) * 8 + mapSpriteTableOffset, 8);
			byte[] ephraimSpriteData = handler.readBytesAtOffset((oldEphraimClass - 1) * 8 + mapSpriteTableOffset, 8);
			diffCompiler.addDiff(new Diff(eirikaTargetOffset, 8, eirikaSpriteData, null));
			diffCompiler.addDiff(new Diff(ephraimTargetOffset, 8, ephraimSpriteData, null));
			
			if (fe8_walkingSoundFixApplied) {
				long eirikaWalkingSoundOffset = 0x78D90 + newEirikaClass.getID();
				long ephraimWalkingSoundOffset = 0x78D90 + newEphraimClass.getID();
				
				InputStream stream = UPSPatcher.class.getClassLoader().getResourceAsStream("fe8_walking_sound_fix.bin");
				byte[] fixData = new byte[0x14C];
				try {
					stream.read(fixData);
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				byte eirikaWalkingSoundID = fixData[0x18 + oldEirikaClass];
				byte ephraimWalkingSoundID = fixData[0x18 + oldEphraimClass];
				
				diffCompiler.addDiff(new Diff(eirikaWalkingSoundOffset, 1, new byte[] {eirikaWalkingSoundID}, null));
				diffCompiler.addDiff(new Diff(ephraimWalkingSoundOffset, 1, new byte[] {ephraimWalkingSoundID}, null));
			}
		}
		
		if (prfs != null && prfs.createPrfs) {
			boolean unbreakablePrfs = prfs.unbreakablePrfs;

			// Create new PRF weapons.
			if (gameType == GameType.FE6) {
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
						byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(iconName, GBAImageCodec.gbaWeaponColorPalette);
						if (iconData == null) {
							notifyError("Invalid image data for icon " + iconName);
						}
						diffCompiler.addDiff(new Diff(0xFC400, iconData.length, iconData, null));
						
						// We're going to reuse some indices already used by the watch staff. While the name's index isn't available, both its
						// description and use item description are available.
						textData.setStringAtIndex(0x5FE, weaponName + "[X]");
						// TODO: Maybe give it a description string?
						
						GBAFEItemData itemToReplace = itemData.itemWithID(FE6Data.Item.UNUSED_WATCH_STAFF.ID);
						itemToReplace.turnIntoLordWeapon(roy.getID(), 0x5FE, 0x0, selectedType, unbreakablePrfs, royClass.getCON() + roy.getConstitution(), 
								itemData.itemWithID(FE6Data.Item.RAPIER.ID), itemData, freeSpace);
						
						switch (selectedType) {
						case SWORD:
						case LANCE:
						case AXE:
							itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
									FE6SpellAnimationCollection.Animation.NONE2.value, FE6SpellAnimationCollection.Flash.WHITE.value);
							break;
						case BOW:
							itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
									FE6SpellAnimationCollection.Animation.ARROW.value, FE6SpellAnimationCollection.Flash.WHITE.value);
							break;
						case ANIMA:
							itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
									FE6SpellAnimationCollection.Animation.ELFIRE.value, FE6SpellAnimationCollection.Flash.RED.value);
							break;
						case DARK:
							itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
									FE6SpellAnimationCollection.Animation.FLUX.value, FE6SpellAnimationCollection.Flash.DARK.value);
							break;
						case LIGHT:
							itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
									FE6SpellAnimationCollection.Animation.DIVINE.value, FE6SpellAnimationCollection.Flash.YELLOW.value);
							break;
						default:
							// No animation needed here.
							break;
						}
						
						if (selectedType == WeaponType.SWORD) {
							// Make sure the old lord class, if anybody randomizes into it, can't use this weapon.
							GBAFEClassData oldLordClass = classData.classForID(FE6Data.CharacterClass.LORD.ID);
							oldLordClass.removeLordLocks();
							GBAFEClassData oldPromotedLordClass = classData.classForID(FE6Data.CharacterClass.MASTER_LORD.ID);
							oldPromotedLordClass.removeLordLocks();
							
							// Make sure Roy himself can.
							// For FE6, use the Eckesachs lock. The rapier lock is still valid for the normal Roy Lord class.
							// This is safe so long as we're not giving out Eckesachs to anybody randomly.
							roy.enableWeaponLock(FE6Data.CharacterAndClassAbility3Mask.ECKESACHS_LOCK.ID);	
						} else {
							// There shouldn't be any need to remove the old lock since Lord is Sword-locked.
							// So if this isn't a sword, then we can safely just use the same lock again.
							// Do make sure Roy can use it though.
							roy.enableWeaponLock(FE6Data.CharacterAndClassAbility3Mask.RAPIER_LOCK.ID);
							// If we go down this route, Rapier must have a rank though.
							GBAFEItemData bindingBlade = itemData.itemWithID(FE6Data.Item.BINDING_BLADE.ID); 
							bindingBlade.setWeaponRank(WeaponRank.E);
							bindingBlade.commitChanges();
						}
						
						
						for (GBAFEChapterData chapter : chapterData.allChapters()) {
							for (GBAFEChapterUnitData unit : chapter.allUnits()) {
								// Give Roy the weapon when he shows up.
								if (unit.getCharacterNumber() == roy.getID()) {
									unit.giveItems(new int[] {itemToReplace.getID()}, itemData);
								}
								
								// Replace any Rapiers with iron swords, since we need to reuse the same lock.
								if (unit.hasItem(FE6Data.Item.RAPIER.ID)) {
									unit.removeItem(FE6Data.Item.RAPIER.ID);
									unit.giveItems(new int[] {FE6Data.Item.IRON_SWORD.ID}, itemData);								}
							}
						}
					}
				}
			} else if (gameType == GameType.FE7) {
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
					if (lynWeaponTypes.size() > 1) { lynWeaponTypes.remove(WeaponType.SWORD); } 
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
					default: break;
					}
				}
					
				if (!eliwoodWeaponTypes.isEmpty()) {
					// Deprioritize Swords, since we only have 2 locks we can use for it.
					if (eliwoodWeaponTypes.size() > 1) { eliwoodWeaponTypes.remove(WeaponType.SWORD); }
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
					if (hectorWeaponTypes.size() > 1) { hectorWeaponTypes.remove(WeaponType.SWORD); }
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
					byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(lynIconName, GBAImageCodec.gbaWeaponColorPalette);
					if (iconData == null) {
						notifyError("Invalid image data for icon " + lynIconName);
					}
					diffCompiler.addDiff(new Diff(0xCB524, iconData.length, iconData, null));
					
					textData.setStringAtIndex(0x1225, lynWeaponName + "[X]");
					GBAFEItemData referenceWeapon = itemData.itemWithID(FE7Data.Item.MANI_KATTI.ID);
					GBAFEItemData newWeapon = referenceWeapon.createLordWeapon(FE7Data.Character.LYN.ID, 0x9F, 0x1225, 0x0, 
							lynSelectedType, unbreakablePrfs, lynClass.getCON() + lyn.getConstitution(), 
							0xAD, itemData, freeSpace);
					
					// Lyn's the first, so all weapon locks are unused.
					// Try to use her own lock, assuming it's not a sword or a bow.
					// Remember, Lyn has a tutorial version too.
					GBAFECharacterData lynTutorial = charData.characterWithID(FE7Data.Character.LYN_TUTORIAL.ID);
					if (lynSelectedType == WeaponType.SWORD) {
						athosLockUsed = true;
						newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
						lyn.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.ID);
						lynTutorial.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.ID);
					} else if (lynSelectedType == WeaponType.BOW) {
						eliwoodLockUsed = true;
						newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ELIWOOD_LOCK.ID);
						lyn.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ELIWOOD_LOCK.ID);
						lynTutorial.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ELIWOOD_LOCK.ID);
					} else {
						lynLockUsed = true;
						newWeapon.setAbility3(FE7Data.Item.Ability3Mask.LYN_LOCK.ID);
						lyn.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.ID);
						lynTutorial.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.ID);
					}
					
					itemData.addNewItem(newWeapon);
					
					switch (lynSelectedType) {
					case SWORD:
					case LANCE:
					case AXE:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.NONE2.value, FE7SpellAnimationCollection.Flash.WHITE.value);
						break;
					case BOW:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.ARROW.value	, FE7SpellAnimationCollection.Flash.WHITE.value);
						break;
					case ANIMA:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.THUNDER.value, FE7SpellAnimationCollection.Flash.YELLOW.value);
						break;
					case DARK:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.FLUX.value, FE7SpellAnimationCollection.Flash.DARK.value);
						break;
					case LIGHT:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.SHINE.value, FE7SpellAnimationCollection.Flash.YELLOW.value);
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
								unit.giveItems(new int[] {newWeapon.getID()}, itemData);
							}
						}
					}
				}
				
				if (eliwoodSelectedType != null && eliwoodWeaponName != null && eliwoodIconName != null) {
					byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(eliwoodIconName, GBAImageCodec.gbaWeaponColorPalette);
					if (iconData == null) {
						notifyError("Invalid image data for icon " + eliwoodIconName);
					}
					diffCompiler.addDiff(new Diff(0xCB5A4, iconData.length, iconData, null));
					
					textData.setStringAtIndex(0x1227, eliwoodWeaponName + "[X]");
					GBAFEItemData referenceWeapon = itemData.itemWithID(FE7Data.Item.RAPIER.ID);
					GBAFEItemData newWeapon = referenceWeapon.createLordWeapon(FE7Data.Character.ELIWOOD.ID, 0xA0, 0x1227, 0x0, 
							eliwoodSelectedType, unbreakablePrfs, eliwoodClass.getCON() + eliwood.getConstitution(), 
							0xAE, itemData, freeSpace);
					
					// Eliwood only has to take into account the locks that could have already be used (Athos, Eliwood, or Lyn).
					// Try to use his own lock, assuming it's not a sword or a lance.
					if (eliwoodSelectedType == WeaponType.SWORD) {
						if (!athosLockUsed) {
							athosLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.ID);
						} else {
							// We only have the unused lock left.
							unusedLockUsed = true;
							newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.ID);
						}
					} else if (eliwoodSelectedType == WeaponType.LANCE) {
						if (!lynLockUsed) {
							lynLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.LYN_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.ID);
						} else if (!athosLockUsed) {
							athosLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.ID);
						} else {
							unusedLockUsed = true;
							newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.ID);
						}
					} else {
						if (!eliwoodLockUsed) {
							eliwoodLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ELIWOOD_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ELIWOOD_LOCK.ID);
						} else if (!lynLockUsed) {
							lynLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.LYN_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.ID);
						} else if (!athosLockUsed && // Athos lock cannot be used with any tome.
								eliwoodSelectedType != WeaponType.ANIMA && 
								eliwoodSelectedType != WeaponType.DARK && 
								eliwoodSelectedType != WeaponType.LIGHT) {
							athosLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.ID);
						} else {
							unusedLockUsed = true;
							newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
							eliwood.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.ID);
						}
					}
					
					itemData.addNewItem(newWeapon);
					
					switch (eliwoodSelectedType) {
					case SWORD:
					case LANCE:
					case AXE:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.NONE2.value, FE7SpellAnimationCollection.Flash.WHITE.value);
						break;
					case BOW:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.ARROW.value	, FE7SpellAnimationCollection.Flash.WHITE.value);
						break;
					case ANIMA:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.ELFIRE.value, FE7SpellAnimationCollection.Flash.BLUE.value);
						break;
					case DARK:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.FLUX.value, FE7SpellAnimationCollection.Flash.DARK.value);
						break;
					case LIGHT:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.SHINE.value, FE7SpellAnimationCollection.Flash.GREEN.value);
						break;
					default:
						break;
					}
					
					// Replace Eliwood's starting Rapier, if he has one.
					for (GBAFEChapterData chapter : chapterData.allChapters()) {
						for (GBAFEChapterUnitData unit : chapter.allUnits()) {
							if (unit.getCharacterNumber() == eliwood.getID()) {
								unit.removeItem(referenceWeapon.getID());
								unit.giveItems(new int[] {newWeapon.getID()}, itemData);
							}
						}
					}
				}
				
				if (hectorSelectedType != null && hectorWeaponName != null && hectorIconName != null) {
					byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(hectorIconName, GBAImageCodec.gbaWeaponColorPalette);
					if (iconData == null) {
						notifyError("Invalid image data for icon " + hectorIconName);
					}
					diffCompiler.addDiff(new Diff(0xCB624, iconData.length, iconData, null));
					
					textData.setStringAtIndex(0x1229, hectorWeaponName + "[X]");
					GBAFEItemData referenceWeapon = itemData.itemWithID(FE7Data.Item.WOLF_BEIL.ID);
					GBAFEItemData newWeapon = referenceWeapon.createLordWeapon(FE7Data.Character.HECTOR.ID, 0xA1, 0x1229, 0x0, 
							hectorSelectedType, unbreakablePrfs, hectorClass.getCON() + hector.getConstitution(), 
							0xAF, itemData, freeSpace);
					
					// We've avoided using Hector lock the entire time, so we just need to account for swords and axes.
					if (hectorSelectedType == WeaponType.SWORD) {
						// Athos and Unused are the only ones possible here. If they're both used, GG.
						if (!athosLockUsed) {
							athosLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
							hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.ID);
						} else if (!unusedLockUsed) {
							unusedLockUsed = true;
							newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
							hector.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.ID);
						} else {
							// GG. Just use Hector lock.
							newWeapon.setAbility2(FE7Data.Item.Ability3Mask.HECTOR_LOCK.ID);
							hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.HECTOR_LOCK.ID);
						}
					} else if (hectorSelectedType == WeaponType.AXE) {
						if (!lynLockUsed) {
							lynLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.LYN_LOCK.ID);
							hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.LYN_LOCK.ID);
						} else if (!athosLockUsed) {
							athosLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ATHOS_LOCK.ID);
							hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ATHOS_LOCK.ID);
						} else if (!eliwoodLockUsed) {
							eliwoodLockUsed = true;
							newWeapon.setAbility3(FE7Data.Item.Ability3Mask.ELIWOOD_LOCK.ID);
							hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.ELIWOOD_LOCK.ID);
						} else { // There's no way we used 4 locks with two characters.
							unusedLockUsed = true;
							newWeapon.setAbility2(FE7Data.Item.Ability2Mask.UNUSED_WEAPON_LOCK.ID);
							hector.enableWeaponLock(FE7Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.ID);
						}
					} else {
						hectorLockUsed = true;
						newWeapon.setAbility3(FE7Data.Item.Ability3Mask.HECTOR_LOCK.ID);
						hector.enableWeaponLock(FE7Data.CharacterAndClassAbility4Mask.HECTOR_LOCK.ID);
					}
					
					itemData.addNewItem(newWeapon);
					
					switch (hectorSelectedType) {
					case SWORD:
					case LANCE:
					case AXE:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.NONE2.value, FE7SpellAnimationCollection.Flash.WHITE.value);
						break;
					case BOW:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.ARROW.value	, FE7SpellAnimationCollection.Flash.WHITE.value);
						break;
					case ANIMA:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.FIMBULVETR.value, FE7SpellAnimationCollection.Flash.BLUE.value);
						break;
					case DARK:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.FLUX.value, FE7SpellAnimationCollection.Flash.DARK.value);
						break;
					case LIGHT:
						itemData.spellAnimations.addAnimation(newWeapon.getID(), 2, 
								FE7SpellAnimationCollection.Animation.SHINE.value, FE7SpellAnimationCollection.Flash.BLUE.value);
						break;
					default:
						break;
					}
					
					// Replace Hector's starting Wolf Beil, if he has one.
					for (GBAFEChapterData chapter : chapterData.allChapters()) {
						for (GBAFEChapterUnitData unit : chapter.allUnits()) {
							if (unit.getCharacterNumber() == hector.getID()) {
								unit.removeItem(referenceWeapon.getID());
								unit.giveItems(new int[] {newWeapon.getID()}, itemData);
							}
						}
					}
				}
				
			} else if (gameType == GameType.FE8) {
				GBAFECharacterData eirika = charData.characterWithID(FE8Data.Character.EIRIKA.ID);
				GBAFECharacterData ephraim = charData.characterWithID(FE8Data.Character.EPHRAIM.ID);
				
				GBAFEClassData eirikaClass = classData.classForID(eirika.getClassID());
				GBAFEClassData ephraimClass = classData.classForID(ephraim.getClassID());
				
				List<WeaponType> eirikaWeaponTypes = classData.usableTypesForClass(eirikaClass);
				List<WeaponType> ephraimWeaponTypes = classData.usableTypesForClass(ephraimClass);
				
				eirikaWeaponTypes.remove(WeaponType.STAFF);
				ephraimWeaponTypes.remove(WeaponType.STAFF);
				
				String eirikaIconName = null;
				String eirikaWeaponName = null;
				WeaponType eirikaSelectedType = null;
				String ephraimIconName = null;
				String ephraimWeaponName = null;
				WeaponType ephraimSelectedType = null;
				
				if (!eirikaWeaponTypes.isEmpty()) {
					eirikaSelectedType = eirikaWeaponTypes.get(rng.nextInt(eirikaWeaponTypes.size()));
					switch (eirikaSelectedType) {
					case SWORD:
						eirikaWeaponName = "Moon Blade";
						eirikaIconName = "weaponIcons/MoonBlade.png";
						break;
					case LANCE:
						eirikaWeaponName = "Moon Spear";
						eirikaIconName = "weaponIcons/MoonSpear.png";
						break;
					case AXE:
						eirikaWeaponName = "Moon Hammer";
						eirikaIconName = "weaponIcons/MoonHammer.png";
						break;
					case BOW:
						eirikaWeaponName = "Moon Shot";
						eirikaIconName = "weaponIcons/MoonShot.png";
						break;
					case ANIMA:
						eirikaWeaponName = "Lunar Bolt";
						eirikaIconName = "weaponIcons/LunarBolt.png";
						break;
					case DARK:
						eirikaWeaponName = "Lunar Eclipse";
						eirikaIconName = "weaponIcons/LunarEclipse.png";
						break;
					case LIGHT:
						eirikaWeaponName = "Lunar Beam";
						eirikaIconName = "weaponIcons/LunarBeam.png";
						break;
					default: 
						break;
					}
				}
				
				if (!ephraimWeaponTypes.isEmpty()) {
					ephraimSelectedType = ephraimWeaponTypes.get(rng.nextInt(ephraimWeaponTypes.size()));
					switch (ephraimSelectedType) {
					case SWORD:
						ephraimWeaponName = "Sun Blade";
						ephraimIconName = "weaponIcons/SunBlade.png";
						break;
					case LANCE:
						ephraimWeaponName = "Sun Spear";
						ephraimIconName = "weaponIcons/SunSpear.png";
						break;
					case AXE:
						ephraimWeaponName = "Sun Mallet";
						ephraimIconName = "weaponIcons/SunMallet.png";
						break;
					case BOW:
						ephraimWeaponName = "Sun Shot";
						ephraimIconName = "weaponIcons/SunShot.png";
						break;
					case ANIMA:
						ephraimWeaponName = "Solar Flare";
						ephraimIconName = "weaponIcons/SolarFlare.png";
						break;
					case DARK:
						ephraimWeaponName = "Solar Eclipse";
						ephraimIconName = "weaponIcons/SolarEclipse.png";
						break;
					case LIGHT:
						ephraimWeaponName = "Solar Beam";
						ephraimIconName = "weaponIcons/SolarBeam.png";
						break;
					default: 
						break;
					}
				}
					
				if (eirikaWeaponName != null && eirikaIconName != null) {
					// Replace the old icon.
					byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(eirikaIconName, GBAImageCodec.gbaWeaponColorPalette);
					if (iconData == null) {
						notifyError("Invalid image data for icon " + eirikaIconName);
					}
					diffCompiler.addDiff(new Diff(0x592B74, iconData.length, iconData, null));
					
					// Reusing the dummy Mani Katti
					textData.setStringAtIndex(0x3A, eirikaWeaponName + "[X]");
					// We need a description string so that the rest of the weapon stats will show, even if it's a blank string.
					textData.setStringAtIndex(0x3B, " [.][X]");
					
					GBAFEItemData itemToReplace = itemData.itemWithID(FE8Data.Item.UNUSED_MANI_KATTI.ID);
					itemToReplace.turnIntoLordWeapon(eirika.getID(), 0x3A, 0x3B, eirikaSelectedType, unbreakablePrfs, eirikaClass.getCON() + eirika.getConstitution(), 
							itemData.itemWithID(FE8Data.Item.RAPIER.ID), itemData, freeSpace);
					
					switch (eirikaSelectedType) {
					case SWORD:
					case LANCE:
					case AXE:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.NONE2.value, FE8SpellAnimationCollection.Flash.WHITE.value);
						break;
					case BOW:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.ARROW.value, FE8SpellAnimationCollection.Flash.WHITE.value);
						break;
					case ANIMA:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.THUNDER.value, FE8SpellAnimationCollection.Flash.YELLOW.value);
						break;
					case DARK:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.FLUX.value, FE8SpellAnimationCollection.Flash.DARK.value);
						break;
					case LIGHT:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.DIVINE.value, FE8SpellAnimationCollection.Flash.BLUE.value);
						break;
					default:
						// No animation needed here.
						break;
					}
					
					// Make sure Eirika herself can. She'll use the unused Lyn Lock.
					eirika.enableWeaponLock(FE8Data.CharacterAndClassAbility4Mask.UNUSED_LYN_LOCK.ID);
					itemToReplace.setAbility3(FE8Data.Item.Ability3Mask.UNUSED_LYN_LOCK.ID);
					
					// Eirika will get her weapon from Seth.
					GBAFEChapterData prologue = chapterData.chapterWithID(FE8Data.ChapterPointer.PROLOGUE.chapterID);
					GBAFEChapterItemData item = prologue.chapterItemGivenToCharacter(FE8Data.Character.EIRIKA.ID);
					item.setItemID(itemToReplace.getID());
				}
				
				if (ephraimWeaponName != null && ephraimIconName != null) {
					// Replace the old icon.
					byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(ephraimIconName, GBAImageCodec.gbaWeaponColorPalette);
					if (iconData == null) {
						notifyError("Invalid image data for icon " + ephraimIconName);
					}
					diffCompiler.addDiff(new Diff(0x594474, iconData.length, iconData, null));
					
					// Reusing the dummy Forblaze
					textData.setStringAtIndex(0x3C, ephraimWeaponName + "[X]");
					// We need a description string for the rest of the weapon stats to show up.
					textData.setStringAtIndex(0x3D, " [.][X]");
					
					GBAFEItemData itemToReplace = itemData.itemWithID(FE8Data.Item.UNUSED_FORBLAZE.ID);
					itemToReplace.turnIntoLordWeapon(eirika.getID(), 0x3C, 0x3D, ephraimSelectedType, unbreakablePrfs, ephraimClass.getCON() + ephraim.getConstitution(), 
							itemData.itemWithID(FE8Data.Item.REGINLEIF.ID), itemData, freeSpace);
					
					switch (ephraimSelectedType) {
					case SWORD:
					case LANCE:
					case AXE:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.NONE2.value, FE8SpellAnimationCollection.Flash.WHITE.value);
						break;
					case BOW:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.ARROW.value, FE8SpellAnimationCollection.Flash.WHITE.value);
						break;
					case ANIMA:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.ELFIRE.value, FE8SpellAnimationCollection.Flash.RED.value);
						break;
					case DARK:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.FLUX.value, FE8SpellAnimationCollection.Flash.DARK.value);
						break;
					case LIGHT:
						itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
								FE8SpellAnimationCollection.Animation.DIVINE.value, FE8SpellAnimationCollection.Flash.YELLOW.value);
						break;
					default:
						// No animation needed here.
						break;
					}
					
					// Make sure Ephraim himself can. He'll use the unused Athos Lock.
					ephraim.enableWeaponLock(FE8Data.CharacterAndClassAbility4Mask.UNUSED_ATHOS_LOCK.ID);
					itemToReplace.setAbility3(FE8Data.Item.Ability3Mask.UNUSED_WEAPON_LOCK.ID);
					
					// Ephraim starts with his weapon.
					GBAFEChapterData ch5x = chapterData.chapterWithID(FE8Data.ChapterPointer.CHAPTER_5X.chapterID);
					for (GBAFEChapterUnitData unit : ch5x.allUnits()) {
						if (unit.getCharacterNumber() == ephraim.getID()) {
							unit.removeItem(FE8Data.Item.REGINLEIF.ID);
							unit.giveItems(new int[] {itemToReplace.getID()}, itemData);
						}
					}
				}
			}
		}
		
		if (miscOptions.singleRNMode) {
			switch (gameType) {
			case FE6:
				diffCompiler.addDiff(new Diff(0xE6A, 4, new byte[] {(byte)0xC0, (byte)0x46, (byte)0xC0, (byte)0x46}, new byte[] {(byte)0xFF, (byte)0xF7, (byte)0xBB, (byte)0xFF}));
				break;
			case FE7:
				diffCompiler.addDiff(new Diff(0xE92, 4, new byte[] {(byte)0xC0, (byte)0x46, (byte)0xC0, (byte)0x46}, new byte[] {(byte)0xFF, (byte)0xF7, (byte)0xB7, (byte)0xFF}));
				break;
			case FE8:
				diffCompiler.addDiff(new Diff(0xCC2, 4, new byte[] {(byte)0xC0, (byte)0x46, (byte)0xC0, (byte)0x46}, new byte[] {(byte)0xFF, (byte)0xF7, (byte)0xCF, (byte)0xFF}));
				break;
			default:
				break;
			}
		}
		
		if (miscOptions.casualMode) {
			switch (gameType) {
			case FE6:
				diffCompiler.addDiff(new Diff(0x17BEA, 1, new byte[] {(byte)0x09}, new byte[] {(byte)0x05}));
				break;
			case FE7:
				diffCompiler.addDiff(new Diff(0x17EA0, 1, new byte[] {(byte)0x09}, new byte[] {(byte)0x05}));
				break;
			case FE8:
				diffCompiler.addDiff(new Diff(0x1841A, 1, new byte[] {(byte)0x09}, new byte[] {(byte)0x05}));
				break;
			default:
				break;
			}
		}
		
		switch (miscOptions.experienceRate) {
		case NORMAL:
			break;
		case PARAGON:
			switch (gameType) {
			case FE6:
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
				break;
			case FE7:
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
				break;
			case FE8:
				// Combat EXP
				diffCompiler.addDiff(new Diff(0x2C58A, 24,
						new byte[] {(byte)0x00, (byte)0x99, (byte)0x09, (byte)0x18, (byte)0x48, (byte)0x00, (byte)0x64, (byte)0x28,
								    (byte)0x00, (byte)0xDD, (byte)0x64, (byte)0x20, (byte)0x00, (byte)0x28, (byte)0x00, (byte)0xDC,
								    (byte)0x01, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x90},
						new byte[] {(byte)0x00, (byte)0x99, (byte)0x09, (byte)0x18, (byte)0x00, (byte)0x91, (byte)0x64, (byte)0x29,
								    (byte)0x01, (byte)0xDD, (byte)0x64, (byte)0x20, (byte)0x00, (byte)0x90, (byte)0x00, (byte)0x98,
								    (byte)0x00, (byte)0x28, (byte)0x01, (byte)0xDC, (byte)0x01, (byte)0x20, (byte)0x00, (byte)0x90}));
				
				// Staff EXP
				diffCompiler.addDiff(new Diff(0x2C688, 12,
						new byte[] {(byte)0x00, (byte)0x28, (byte)0x01, (byte)0xD0, (byte)0x52, (byte)0x10, (byte)0x00, (byte)0x00,
								    (byte)0x52, (byte)0x00, (byte)0x64, (byte)0x2A},
						new byte[] {(byte)0x00, (byte)0x28, (byte)0x02, (byte)0xD0, (byte)0xD0, (byte)0x0F, (byte)0x10, (byte)0x18,
								    (byte)0x42, (byte)0x10, (byte)0x64, (byte)0x2A}));
				
				// Steal/Dance EXP
				diffCompiler.addDiff(new Diff(0x2C6CC, 8,
						new byte[] {(byte)0x14, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x14, (byte)0x30},
						new byte[] {(byte)0x0A, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x0A, (byte)0x30}));
				break;
			default:
				break;
			}
			break;
		case RENEGADE:
			switch (gameType) {
			case FE6:
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
				break;
			case FE7:
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
				break;
			case FE8:
				// Combat EXP
				diffCompiler.addDiff(new Diff(0x2C58A, 24,
						new byte[] {(byte)0x00, (byte)0x99, (byte)0x09, (byte)0x18, (byte)0x48, (byte)0x10, (byte)0x64, (byte)0x28,
								    (byte)0x00, (byte)0xDD, (byte)0x64, (byte)0x20, (byte)0x00, (byte)0x28, (byte)0x00, (byte)0xDC,
								    (byte)0x01, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x90},
						new byte[] {(byte)0x00, (byte)0x99, (byte)0x09, (byte)0x18, (byte)0x00, (byte)0x91, (byte)0x64, (byte)0x29,
								    (byte)0x01, (byte)0xDD, (byte)0x64, (byte)0x20, (byte)0x00, (byte)0x90, (byte)0x00, (byte)0x98,
								    (byte)0x00, (byte)0x28, (byte)0x01, (byte)0xDC, (byte)0x01, (byte)0x20, (byte)0x00, (byte)0x90}));
				
				// Staff EXP
				diffCompiler.addDiff(new Diff(0x2C688, 12,
						new byte[] {(byte)0x00, (byte)0x28, (byte)0x01, (byte)0xD0, (byte)0x52, (byte)0x10, (byte)0x00, (byte)0x00,
						            (byte)0x52, (byte)0x10, (byte)0x64, (byte)0x2A},
						new byte[] {(byte)0x00, (byte)0x28, (byte)0x02, (byte)0xD0, (byte)0xD0, (byte)0x0F, (byte)0x10, (byte)0x18,
								    (byte)0x42, (byte)0x10, (byte)0x64, (byte)0x2A}));
				
				// Steal/Dance EXP
				diffCompiler.addDiff(new Diff(0x2C6CC, 8,
						new byte[] {(byte)0x05, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x05, (byte)0x30},
						new byte[] {(byte)0x0A, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x0A, (byte)0x30}));
				break;
			default:
				break;
			}
			break;
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
			case SMART:
				rk.addHeaderItem("Randomize Growths", "Smart");
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
			case SMART:
				rk.addHeaderItem("Randomize Bases", "Smart");
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
		if (weapons != null) {
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
				if (weapons.effectsList.statBoosts > 0) { sb.append("<li>Stat Boosts" + String.format(" (%.2f%%)", (double)weapons.effectsList.statBoosts / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.effectiveness > 0) { sb.append("<li>Effectiveness" + String.format(" (%.2f%%)", (double)weapons.effectsList.effectiveness / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.unbreakable > 0) { sb.append("<li>Unbreakable" + String.format(" (%.2f%%)", (double)weapons.effectsList.unbreakable / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.brave > 0) { sb.append("<li>Brave" + String.format(" (%.2f%%)", (double)weapons.effectsList.brave / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.reverseTriangle > 0) { sb.append("<li>Reverse Triangle" + String.format(" (%.2f%%)", (double)weapons.effectsList.reverseTriangle / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.extendedRange > 0) { sb.append("<li>Extended Range" + String.format(" (%.2f%%)", (double)weapons.effectsList.extendedRange / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.highCritical > 0) { 
					sb.append("<li>Critical");
					sb.append(" (" + weapons.effectsList.criticalRange.minValue + "% ~ " + weapons.effectsList.criticalRange.maxValue + "%)");
					sb.append(String.format(" (%.2f%%)", (double)weapons.effectsList.highCritical / (double)weapons.effectsList.getWeightTotal() * 100));
					sb.append("</li>\n");
				}
				if (weapons.effectsList.magicDamage > 0) { sb.append("<li>Magic Damage" + String.format(" (%.2f%%)", (double)weapons.effectsList.magicDamage / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.poison > 0) { sb.append("<li>Poison" + String.format(" (%.2f%%)", (double)weapons.effectsList.poison / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.eclipse > 0) { sb.append("<li>Eclipse" + String.format(" (%.2f%%)", (double)weapons.effectsList.eclipse / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				if (weapons.effectsList.devil > 0) { sb.append("<li>Devil" + String.format(" (%.2f%%)", (double)weapons.effectsList.devil / (double)weapons.effectsList.getWeightTotal() * 100) + "</li>\n"); }
				sb.append("</ul>\n");
				rk.addHeaderItem("Random Effects Allowed", sb.toString());
				if (weapons.noEffectIronWeapons) {
					rk.addHeaderItem("Safe Basic Weapons", "YES");
				} else {
					rk.addHeaderItem("Safe Basic Weapons", "NO");
				}
				if (weapons.noEffectSteelWeapons) {
					rk.addHeaderItem("Safe Steel Weapons", "YES");
				} else {
					rk.addHeaderItem("Safe Steel Weapons", "NO");
				}
				if (weapons.noEffectThrownWeapons) {
					rk.addHeaderItem("Safe Basic Thrown Weapons", "YES");
				} else {
					rk.addHeaderItem("Safe Basic Thrown Weapons", "NO");
				}
			} else {
				rk.addHeaderItem("Add Random Effects", "NO");
			}
		}
		if(classes != null) {
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
				
				switch (classes.growthOptions) {
				case NO_CHANGE:
					rk.addHeaderItem("Growth Transfer Option", "No Change");
					break;
				case TRANSFER_PERSONAL_GROWTHS:
					rk.addHeaderItem("Growth Transfer Option", "Transfer Personal Growths");
					break;
				case CLASS_RELATIVE_GROWTHS:
					rk.addHeaderItem("Growth Transfer Option", "Class Relative Growths");
					break;
				}
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
			switch (classes.genderOption) {
			case NONE:
				rk.addHeaderItem("Gender Restriction", "None");
				break;
			case LOOSE:
				rk.addHeaderItem("Gender Restriction", "Loose");
				break;
			case STRICT:
				rk.addHeaderItem("Gender Restriction", "Strict");
				break;
			}
			if (gameType == GameType.FE8) {
				if (classes.separateMonsters) {
					rk.addHeaderItem("Mix Monster and Human Classes", "NO");
				} else {
					rk.addHeaderItem("Mix Monster and Human Classes", "YES");
				}
			}
			
		}
		if(enemies != null) {
			switch (enemies.minionMode) {
			case NONE:
				rk.addHeaderItem("Buff Minions", "NO");
				break;
			case FLAT:
				rk.addHeaderItem("Buff Minions", "Flat Buff (Growths +" + enemies.minionBuff + "%)");
				rk.addHeaderItem("Buffed Minion Stats", enemies.minionBuffStats.buffString());
				break;
			case SCALING:
				rk.addHeaderItem("Buff Minions", "Scaling Buff (Growths x" + String.format("%.2f", (enemies.minionBuff / 100.0) + 1) + ")");
				rk.addHeaderItem("Buffed Minion Stats", enemies.minionBuffStats.buffString());
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
				rk.addHeaderItem("Buffed Boss Stats", enemies.bossBuffStats.buffString());
				break;
			case EASE_IN_OUT:
				rk.addHeaderItem("Buff Bosses", "Ease In/Ease Out - Max Gain: +" + enemies.bossBuff);
				rk.addHeaderItem("Buffed Boss Stats", enemies.bossBuffStats.buffString());
				break;
			}
			
			if (enemies.improveBossWeapons) {
				rk.addHeaderItem("Improve Boss Weapons", "" + enemies.bossImprovementChance + "% of bosses");
			} else {
				rk.addHeaderItem("Improve Boss Weapons", "NO");
			}
		}
		if (rewardOptions != null) {
			if (rewardOptions.randomizeRewards) {
				rk.addHeaderItem("Randomize Rewards", "YES");
			} else {
				rk.addHeaderItem("Randomize Rewards", "NO");
			}
		}
		if(miscOptions != null) {

			if (miscOptions.singleRNMode) {
				rk.addHeaderItem("Enable Single RN", "YES");
			} else {
				rk.addHeaderItem("Enable Single RN", "NO");
			}
			
			if (miscOptions.randomizeFogOfWar) {
				rk.addHeaderItem("Randomize Fog of War", "YES - " + Integer.toString(miscOptions.fogOfWarChance) + "%");
				rk.addHeaderItem("Fog of War Vision Range", Integer.toString(miscOptions.fogOfWarVisionRange.minValue) + " ~ " + Integer.toString(miscOptions.fogOfWarVisionRange.maxValue));
			} else {
				rk.addHeaderItem("Randomize Fog of War", "NO");
			}
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
			
			if (gameType != GameType.FE6) {
				rk.addHeaderItem("Assign Promotional Weapons", itemAssignmentOptions.assignPromoWeapons ? "YES" : "NO");
			}
			rk.addHeaderItem("Assign Poison Weapons", itemAssignmentOptions.assignPoisonWeapons ? "YES" : "NO");
		}
		

		if(shufflingOptions != null) {
			if (shufflingOptions.isShuffleEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("Leveling Mode: %s", shufflingOptions.getLevelingMode() == ShuffleLevelingMode.AUTOLEVEL ? "autolevel characters": "leave characters unchanged")).append("<br>");
				sb.append(String.format("Shuffle chance: %d%%", shufflingOptions.getChance())).append("<br>");
				sb.append(shufflingOptions.shouldChangeDescription() ? "Description will be changed" : "Description will be left unchanged").append("<br>");
				sb.append("Included configurations:<br>");
				for (String s : shufflingOptions.getIncludedShuffles()) {
					sb.append(s).append("<br>");
				}
				rk.addHeaderItem("Character Shuffling", sb.toString());
			} else {
				rk.addHeaderItem("Character Shuffling", "NO");
			}
		}
		
		if (statboosterOptions != null) {
			statboosterOptions.record(rk);
		}
		
		if (shopOptions != null) {
			rk.addHeaderItem("Randomize Shops", "YES (" + shopOptions.shopSize.minValue + " ~ " + shopOptions.shopSize.maxValue + " items)");
		} else {
			rk.addHeaderItem("Randomize Shops", "NO");
		}
		
		return rk;
	}
}
