package random;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import org.eclipse.swt.widgets.Display;

import application.Main;
import fedata.FEBase;
import fedata.fe7.FE7Data;
import io.DiffApplicator;
import io.FileHandler;
import io.UPSPatcher;
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

public class Randomizer extends Thread {
	
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
	
	private String seedString;
	
	private FreeSpaceManager freeSpace;
	
	private FileHandler handler;
	
	private RandomizerListener listener = null;

	public Randomizer(String sourcePath, String targetPath, FEBase.GameType gameType, DiffCompiler diffs, 
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
	
	public void setListener(RandomizerListener listener) {
		this.listener = listener;
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
				URI patchURI = null;
				try {
					patchURI = Randomizer.class.getClassLoader().getResource("FE6-TLRedux-v1.0.ups").toURI();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File patchFile = new File(patchURI);
				Boolean success = UPSPatcher.applyUPSPatch(patchFile, sourcePath, tempPath);
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
		default:
			notifyError("This game is not supported.");
			return;
		}
		
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
		
		updateStatusString("Done!");
		updateProgress(1);
		notifyCompletion();
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
		charData = new CharacterDataLoader(FEBase.GameType.FE7, handler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.25);
		classData = new ClassDataLoader(FEBase.GameType.FE7, handler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.30);
		chapterData = new ChapterLoader(FEBase.GameType.FE7, handler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.45);
		itemData = new ItemDataLoader(FEBase.GameType.FE7, handler, freeSpace);
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
		charData = new CharacterDataLoader(FEBase.GameType.FE6, handler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.33);
		classData = new ClassDataLoader(FEBase.GameType.FE6, handler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.36);
		chapterData = new ChapterLoader(FEBase.GameType.FE6, handler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.45);
		itemData = new ItemDataLoader(FEBase.GameType.FE6, handler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.50);
		paletteData = new PaletteLoader(FEBase.GameType.FE6, handler);
		
		handler.clearAppliedDiffs();
	}
	
	private void randomizeGrowthsIfNecessary(String seed) {
		if (growths != null) {
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, GrowthsRandomizer.rngSalt));
			switch (growths.mode) {
			case REDISTRIBUTE:
				updateStatusString("Redistributing growths...");
				GrowthsRandomizer.randomizeGrowthsByRedistribution(growths.redistributionOption.variance, charData, rng);
				break;
			case DELTA:
				updateStatusString("Applying random deltas to growths...");
				GrowthsRandomizer.randomizeGrowthsByRandomDelta(growths.deltaOption.variance, charData, rng);
				break;
			case FULL:
				updateStatusString("Randomizing growths...");
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
				ClassRandomizer.randomizePlayableCharacterClasses(classes.includeLords, classes.includeThieves, charData, classData, chapterData, itemData, paletteData, textData, rng);
			}
			if (classes.randomizeEnemies) {
				updateStatusString("Randomizing minions...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 2));
				ClassRandomizer.randomizeMinionClasses(charData, classData, chapterData, itemData, rng);
			}
			if (classes.randomizeBosses) {
				updateStatusString("Randomizing boss classes...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, ClassRandomizer.rngSalt + 3));
				ClassRandomizer.randomizeBossCharacterClasses(charData, classData, chapterData, itemData, paletteData, rng);
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
	
	private void updateStatusString(String string) {
		if (listener != null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					listener.onStatusUpdate(string);	
				}
			});
		}
	}
	
	private void updateProgress(double progress) {
		if (listener != null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					listener.onProgressUpdate(progress);
				}
			});	
		}
	}
	
	private void notifyError(String errorString) {
		if (listener != null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					listener.onError(errorString);	
				}
			});
		}
	}
	
	private void notifyCompletion() {
		if (listener != null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					listener.onComplete();	
				}
			});
		}
	}
}
