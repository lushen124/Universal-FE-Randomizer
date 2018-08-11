package random;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import fedata.FEBase;
import fedata.fe7.FE7Data;
import io.DiffApplicator;
import io.FileHandler;
import random.exc.FileOpenException;
import random.exc.UnsupportedGameException;
import ui.model.BaseOptions;
import ui.model.ClassOptions;
import ui.model.EnemyOptions;
import ui.model.GrowthOptions;
import ui.model.MiscellaneousOptions;
import ui.model.OtherCharacterOptions;
import ui.model.WeaponEffectOptions;
import ui.model.WeaponOptions;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FreeSpaceManager;
import util.HuffmanHelper;
import util.SeedGenerator;

public class Randomizer {
	
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
	
	private FreeSpaceManager freeSpace;
	
	private FileHandler handler;

	public Randomizer(String sourcePath, String targetPath, FEBase.GameType gameType, DiffCompiler diffs, 
			GrowthOptions growths, BaseOptions bases, ClassOptions classes, WeaponOptions weapons,
			OtherCharacterOptions other, EnemyOptions enemies, MiscellaneousOptions otherOptions) {
		super();
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		
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
	
	public void randomize(String seed) throws Exception {
		try {
			handler = new FileHandler(sourcePath);
		} catch (IOException e) {
			throw new FileOpenException();
		}
		
		switch (gameType) {
		case FE7:
			generateFE7DataLoaders();
			break;
		default:
			throw new UnsupportedGameException();
		}
		
		randomizeGrowthsIfNecessary(seed);
		randomizeClassesIfNecessary(seed); // This MUST come before bases.
		randomizeBasesIfNecessary(seed);
		randomizeWeaponsIfNecessary(seed);
		randomizeOtherCharacterTraitsIfNecessary(seed);
		buffEnemiesIfNecessary(seed);
		randomizeOtherThingsIfNecessary(seed); // i.e. Miscellaneous options.
		
		charData.compileDiffs(diffCompiler);
		chapterData.compileDiffs(diffCompiler);
		classData.compileDiffs(diffCompiler);
		itemData.compileDiffs(diffCompiler);
		paletteData.compileDiffs(diffCompiler);
		textData.commitChanges(freeSpace, diffCompiler);
		
		freeSpace.commitChanges(diffCompiler);
		
		if (targetPath != null) {
			DiffApplicator.applyDiffs(diffCompiler, handler, targetPath);
		}
	}
	
	private void generateFE7DataLoaders() {
		handler.setAppliedDiffs(diffCompiler);
		
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE7);
		textData = new TextLoader(FEBase.GameType.FE7, handler);
		
		charData = new CharacterDataLoader(FEBase.GameType.FE7, handler);
		classData = new ClassDataLoader(FEBase.GameType.FE7, handler);
		chapterData = new ChapterLoader(FEBase.GameType.FE7, handler);
		itemData = new ItemDataLoader(FEBase.GameType.FE7, handler, freeSpace);
		paletteData = new PaletteLoader(FEBase.GameType.FE7, handler);
		
		handler.clearAppliedDiffs();
	}
	
	private void randomizeGrowthsIfNecessary(String seed) {
		if (growths != null) {
			
			
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, GrowthsRandomizer.rngSalt));
			switch (growths.mode) {
			case REDISTRIBUTE:
				GrowthsRandomizer.randomizeGrowthsByRedistribution(growths.redistributionOption.variance, charData, rng);
				break;
			case DELTA:
				GrowthsRandomizer.randomizeGrowthsByRandomDelta(growths.deltaOption.variance, charData, rng);
				break;
			case FULL:
				GrowthsRandomizer.fullyRandomizeGrowthsWithRange(growths.fullOption.minValue, growths.fullOption.maxValue, charData, rng);
				break;
			}
		}
	}
	
	private void randomizeBasesIfNecessary(String seed) {
		if (bases != null) {
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, BasesRandomizer.rngSalt));
			switch (bases.mode) {
			case REDISTRIBUTE:
				BasesRandomizer.randomizeBasesByRedistribution(bases.redistributionOption.variance, charData, classData, rng);
				break;
			case DELTA:
				BasesRandomizer.randomizeBasesByRandomDelta(bases.deltaOption.variance, charData, classData, rng);
			}
		}
	}
	
	private void randomizeClassesIfNecessary(String seed) {
		if (classes != null) {
			if (classes.randomizePCs) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 1));
				ClassRandomizer.randomizePlayableCharacterClasses(classes.includeLords, classes.includeThieves, charData, classData, chapterData, itemData, paletteData, rng);
			}
			if (classes.randomizeEnemies) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 2));
				ClassRandomizer.randomizeMinionClasses(charData, classData, chapterData, itemData, rng);
			}
			if (classes.randomizeBosses) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 3));
				ClassRandomizer.randomizeBossCharacterClasses(charData, classData, chapterData, itemData, paletteData, rng);
			}
		}
	}
	
	private void randomizeWeaponsIfNecessary(String seed) {
		if (weapons != null) {
			if (weapons.mightOptions != null) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt));
				WeaponsRandomizer.randomizeMights(weapons.mightOptions.minValue, weapons.mightOptions.maxValue, weapons.mightOptions.variance, itemData, rng);
			}
			if (weapons.hitOptions != null) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt + 1));
				WeaponsRandomizer.randomizeHit(weapons.hitOptions.minValue, weapons.hitOptions.maxValue, weapons.hitOptions.variance, itemData, rng);
			}
			if (weapons.weightOptions != null) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt + 2));
				WeaponsRandomizer.randomizeWeight(weapons.weightOptions.minValue, weapons.weightOptions.maxValue, weapons.weightOptions.variance, itemData, rng);
			}
			if (weapons.durabilityOptions != null) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt + 3));
				WeaponsRandomizer.randomizeDurability(weapons.durabilityOptions.minValue, weapons.durabilityOptions.maxValue, weapons.durabilityOptions.variance, itemData, rng);
			}
			
			if (weapons.shouldAddEffects && weapons.effectsList != null) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, WeaponsRandomizer.rngSalt + 4));
				WeaponsRandomizer.randomizeEffects(weapons.effectsList, itemData, textData, rng);
			}
		}
	}
	
	private void randomizeOtherCharacterTraitsIfNecessary(String seed) {
		if (otherCharacterOptions != null) {
			if (otherCharacterOptions.movementOptions != null) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 4));
				ClassRandomizer.randomizeClassMovement(otherCharacterOptions.movementOptions.minValue, otherCharacterOptions.movementOptions.maxValue, classData, rng);
			}
			if (otherCharacterOptions.constitutionOptions != null) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, CharacterRandomizer.rngSalt));
				CharacterRandomizer.randomizeConstitution(otherCharacterOptions.constitutionOptions.minValue, otherCharacterOptions.constitutionOptions.variance, charData, classData, rng);
			}
			if (otherCharacterOptions.randomizeAffinity) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, CharacterRandomizer.rngSalt + 1));
				CharacterRandomizer.randomizeAffinity(charData, rng);
			}
		}
	}
	
	private void buffEnemiesIfNecessary(String seed) {
		if (enemies != null) {
			if (enemies.mode == EnemyOptions.BuffMode.FLAT) {
				EnemyBuffer.buffEnemyGrowthRates(enemies.buffAmount, classData);
			} else if (enemies.mode == EnemyOptions.BuffMode.SCALING) {
				EnemyBuffer.scaleEnemyGrowthRates(enemies.buffAmount, classData);
			}
			
			if (enemies.improveWeapons) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, EnemyBuffer.rngSalt));
				EnemyBuffer.improveWeapons(enemies.improvementChance, charData, classData, chapterData, itemData, rng);
			}
		}
	}
	
	private void randomizeOtherThingsIfNecessary(String seed) {
		if (miscOptions != null) {
			if (miscOptions.randomizeRewards) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, RandomRandomizer.rngSalt));
				RandomRandomizer.randomizeRewards(itemData, chapterData, rng);
			}
		}
	}
}
