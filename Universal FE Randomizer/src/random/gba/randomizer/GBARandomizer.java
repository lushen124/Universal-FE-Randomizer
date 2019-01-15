package random.gba.randomizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

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
import ui.model.MiscellaneousOptions;
import ui.model.OtherCharacterOptions;
import ui.model.WeaponOptions;
import util.DiffCompiler;
import util.FreeSpaceManager;
import util.SeedGenerator;
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
	
	private CharacterDataLoader charData;
	private ClassDataLoader classData;
	private ChapterLoader chapterData;
	private ItemDataLoader itemData;
	private PaletteLoader paletteData;
	private TextLoader textData;
	
	// FE8 only
	private FE8PaletteMapper fe8_paletteMapper;
	private FE8PromotionManager fe8_promotionManager;
	private FE8SummonerModule fe8_summonerModule;
	
	private String seedString;
	
	private FreeSpaceManager freeSpace;
	
	private FileHandler handler;

	public GBARandomizer(String sourcePath, String targetPath, FEBase.GameType gameType, DiffCompiler diffs, 
			GrowthOptions growths, BaseOptions bases, ClassOptions classes, WeaponOptions weapons,
			OtherCharacterOptions other, EnemyOptions enemies, MiscellaneousOptions otherOptions, String seed) {
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
				Boolean success = UPSPatcher.applyUPSPatch("FE6-TLRedux-v1.0.ups", sourcePath, tempPath, null);
				if (!success) {
					notifyError("Failed to apply translation patch.");
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
			generateFE6DataLoaders();
			break;
		case FE7:
			updateStatusString("Loading Data...");
			updateProgress(0.01);
			generateFE7DataLoaders();
			break;
		case FE8:
			updateStatusString("Loading Data...");
			updateProgress(0.01);
			generateFE8DataLoaders();
			break;
		default:
			notifyError("This game is not supported.");
			return;
		}
		
		RecordKeeper recordKeeper = initializeRecordKeeper();
		recordKeeper.addHeaderItem("Randomizer Seed Phrase", seed);
		
		updateStatusString("Randomizing...");
		randomizeGrowthsIfNecessary(seed);
		updateProgress(0.55);
		randomizeClassesIfNecessary(seed); // This MUST come before bases.
		updateProgress(0.70);
		randomizeBasesIfNecessary(seed);
		updateProgress(0.75);
		randomizeWeaponsIfNecessary(seed);
		updateProgress(0.80);
		randomizeOtherCharacterTraitsIfNecessary(seed);
		updateProgress(0.85);
		buffEnemiesIfNecessary(seed);
		updateProgress(0.90);
		randomizeOtherThingsIfNecessary(seed); // i.e. Miscellaneous options.
		
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
		
		charData.recordCharacters(recordKeeper, false, classData, textData);
		classData.recordClasses(recordKeeper, false, classData, textData);
		itemData.recordWeapons(recordKeeper, false, classData, textData, targetFileHandler);
		chapterData.recordChapters(recordKeeper, false, charData, classData, itemData, textData);
		
		recordKeeper.sortKeysInCategory(CharacterDataLoader.RecordKeeperCategoryKey);
		recordKeeper.sortKeysInCategory(ClassDataLoader.RecordKeeperCategoryKey);
		recordKeeper.sortKeysInCategory(ItemDataLoader.RecordKeeperCategoryWeaponKey);
		
		updateStatusString("Done!");
		updateProgress(1);
		notifyCompletion(recordKeeper);
	}
	
	private void generateFE7DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE7);
		updateStatusString("Loading Text...");
		updateProgress(0.02);
		textData = new TextLoader(FEBase.GameType.FE7, handler);
		textData.allowTextChanges = true;
		
		updateStatusString("Loading Character Data...");
		updateProgress(0.20);
		charData = new CharacterDataLoader(FE7Data.characterProvider, handler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.25);
		classData = new ClassDataLoader(FE7Data.classProvider, handler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.30);
		chapterData = new ChapterLoader(FEBase.GameType.FE7, handler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.45);
		itemData = new ItemDataLoader(FE7Data.itemProvider, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.50);
		paletteData = new PaletteLoader(FEBase.GameType.FE7, handler);
		
		handler.clearAppliedDiffs();
	}
	
	private void generateFE6DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.12);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE6);
		updateStatusString("Loading Text...");
		updateProgress(0.15);
		textData = new TextLoader(FEBase.GameType.FE6, handler);
		if (miscOptions.applyEnglishPatch) {
			textData.allowTextChanges = true;
		}
		
		updateStatusString("Loading Character Data...");
		updateProgress(0.30);
		charData = new CharacterDataLoader(FE6Data.characterProvider, handler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.33);
		classData = new ClassDataLoader(FE6Data.classProvider, handler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.36);
		chapterData = new ChapterLoader(FEBase.GameType.FE6, handler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.45);
		itemData = new ItemDataLoader(FE6Data.itemProvider, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.50);
		paletteData = new PaletteLoader(FEBase.GameType.FE6, handler);
		
		handler.clearAppliedDiffs();
	}
	
	private void generateFE8DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE8);
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
		updateProgress(0.35);
		itemData = new ItemDataLoader(FE8Data.itemProvider, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.40);
		paletteData = new PaletteLoader(FEBase.GameType.FE8, handler);
		
		updateStatusString("Loading Summoner Module...");
		updateProgress(0.45);
		fe8_summonerModule = new FE8SummonerModule(handler);
		
		updateStatusString("Loading Palette Mapper...");
		updateProgress(0.50);
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
				ClassRandomizer.randomizePlayableCharacterClasses(classes, gameType, charData, classData, chapterData, itemData, paletteData, textData, rng);
			}
			if (classes.randomizeEnemies) {
				updateStatusString("Randomizing minions...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 2));
				ClassRandomizer.randomizeMinionClasses(classes, gameType, charData, classData, chapterData, itemData, rng);
			}
			if (classes.randomizeBosses) {
				updateStatusString("Randomizing boss classes...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 3));
				ClassRandomizer.randomizeBossCharacterClasses(classes, gameType, charData, classData, chapterData, itemData, paletteData, textData, rng);
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
				WeaponsRandomizer.randomizeEffects(weapons.effectsList, itemData, textData, weapons.noEffectIronWeapons, rng);
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
			if (enemies.mode == EnemyOptions.BuffMode.FLAT) {
				updateStatusString("Buffing enemies...");
				EnemyBuffer.buffEnemyGrowthRates(enemies.buffAmount, classData);
			} else if (enemies.mode == EnemyOptions.BuffMode.SCALING) {
				updateStatusString("Buffing enemies...");
				EnemyBuffer.scaleEnemyGrowthRates(enemies.buffAmount, classData);
			}
			
			if (enemies.improveWeapons) {
				updateStatusString("Upgrading enemy weapons...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, EnemyBuffer.rngSalt));
				EnemyBuffer.improveWeapons(enemies.improvementChance, charData, classData, chapterData, itemData, rng);
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
			rk.addHeaderItem("Add Random Effects", "YES");
			StringBuilder sb = new StringBuilder();
			sb.append("<ul>\n");
			if (weapons.effectsList.none) { sb.append("<li>No Effect</li>\n"); }
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
			rk.addHeaderItem("Randomize Playable Character Classes", "YES");
			if (classes.includeLords) {
				rk.addHeaderItem("Include Lords", "YES");
			} else {
				rk.addHeaderItem("Include Lords", "NO");
			}
			if (classes.includeThieves) {
				rk.addHeaderItem("Include Thieves", "YES");
			} else {
				rk.addHeaderItem("Include Thieves", "NO");
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
		if (gameType == GameType.FE8) {
			if (classes.separateMonsters) {
				rk.addHeaderItem("Mix Monster and Human Classes", "NO");
			} else {
				rk.addHeaderItem("Mix Monster and Human Classes", "YES");
			}
		}
		
		switch (enemies.mode) {
		case NONE:
			rk.addHeaderItem("Buff Enemies", "NO");
			break;
		case FLAT:
			rk.addHeaderItem("Buff Enemies", "Flat Buff (Growths +" + enemies.buffAmount + "%)");
			break;
		case SCALING:
			rk.addHeaderItem("Buff Enemies", "Scaling Buff (Growths x" + String.format("%.2f", (enemies.buffAmount / 100.0) + 1) + ")");
			break;
		}
		
		if (enemies.improveWeapons) {
			rk.addHeaderItem("Improve Enemy Weapons", "" + enemies.improvementChance + "% of enemies");
		} else {
			rk.addHeaderItem("Improve Enemy Weapons", "NO");
		}
		
		if (miscOptions.randomizeRewards) {
			rk.addHeaderItem("Randomize Rewards", "YES");
		} else {
			rk.addHeaderItem("Randomize Rewards", "NO");
		}
		
		charData.recordCharacters(rk, true, classData, textData);
		classData.recordClasses(rk, true, classData, textData);
		itemData.recordWeapons(rk, true, classData, textData, handler);
		chapterData.recordChapters(rk, true, charData, classData, itemData, textData);
		
		return rk;
	}
}
