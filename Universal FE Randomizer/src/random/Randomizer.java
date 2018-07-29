package random;

import java.io.IOException;

import fedata.FEBase;
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
import util.DiffCompiler;

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
	
	public void randomize() throws Exception {
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
		
		randomizeGrowthsIfNecessary();
		randomizeClassesIfNecessary(); // This MUST come before bases.
		randomizeBasesIfNecessary();
		randomizeWeaponsIfNecessary();
		randomizeOtherCharacterTraitsIfNecessary();
		buffEnemiesIfNecessary();
		randomizeOtherThingsIfNecessary(); // i.e. Miscellaneous options.
		
		charData.compileDiffs(diffCompiler);
		chapterData.compileDiffs(diffCompiler);
		itemData.compileDiffs(diffCompiler);
		
		if (targetPath != null) {
			DiffApplicator.applyDiffs(diffCompiler, handler, targetPath);
		}
	}
	
	private void generateFE7DataLoaders() {
		charData = new CharacterDataLoader(FEBase.GameType.FE7, handler);
		classData = new ClassDataLoader(FEBase.GameType.FE7, handler);
		chapterData = new ChapterLoader(FEBase.GameType.FE7, handler);
		itemData = new ItemDataLoader(FEBase.GameType.FE7, handler);
	}
	
	private void randomizeGrowthsIfNecessary() {
		if (growths != null) {
			switch (growths.mode) {
			case REDISTRIBUTE:
				GrowthsRandomizer.randomizeGrowthsByRedistribution(growths.redistributionOption.variance, charData);
				break;
			case DELTA:
				GrowthsRandomizer.randomizeGrowthsByRandomDelta(growths.deltaOption.variance, charData);
				break;
			case FULL:
				GrowthsRandomizer.fullyRandomizeGrowthsWithRange(growths.fullOption.minValue, growths.fullOption.maxValue, charData);
				break;
			}
		}
	}
	
	private void randomizeBasesIfNecessary() {
		if (bases != null) {
			switch (bases.mode) {
			case REDISTRIBUTE:
				BasesRandomizer.randomizeBasesByRedistribution(bases.redistributionOption.variance, charData, classData);
				break;
			case DELTA:
				BasesRandomizer.randomizeBasesByRandomDelta(bases.deltaOption.variance, charData, classData);
			}
		}
	}
	
	private void randomizeClassesIfNecessary() {
		if (classes != null) {
			if (classes.randomizePCs) {
				ClassRandomizer.randomizePlayableCharacterClasses(classes.includeLords, classes.includeThieves, charData, classData, chapterData, itemData);
			}
			if (classes.randomizeEnemies) {
				ClassRandomizer.randomizeMinionClasses(charData, classData, chapterData, itemData);
			}
			if (classes.randomizeBosses) {
				ClassRandomizer.randomizeBossCharacterClasses(charData, classData, chapterData, itemData);
			}
		}
	}
	
	private void randomizeWeaponsIfNecessary() {
		if (weapons != null) {
			if (weapons.mightOptions != null) {
				WeaponsRandomizer.randomizeMights(weapons.mightOptions.minValue, weapons.mightOptions.maxValue, weapons.mightOptions.variance, itemData);
			}
			if (weapons.hitOptions != null) {
				WeaponsRandomizer.randomizeHit(weapons.hitOptions.minValue, weapons.hitOptions.maxValue, weapons.hitOptions.variance, itemData);
			}
			if (weapons.weightOptions != null) {
				WeaponsRandomizer.randomizeWeight(weapons.weightOptions.minValue, weapons.weightOptions.maxValue, weapons.weightOptions.variance, itemData);
			}
			if (weapons.durabilityOptions != null) {
				WeaponsRandomizer.randomizeDurability(weapons.durabilityOptions.minValue, weapons.durabilityOptions.maxValue, weapons.durabilityOptions.variance, itemData);
			}
			
			if (weapons.shouldAddEffects && weapons.effectsList != null) {
				WeaponsRandomizer.randomizeEffects(weapons.effectsList, itemData);
			}
		}
	}
	
	private void randomizeOtherCharacterTraitsIfNecessary() {
		if (otherCharacterOptions != null) {
			if (otherCharacterOptions.movementOptions != null) {
				ClassRandomizer.randomizeClassMovement(otherCharacterOptions.movementOptions.minValue, otherCharacterOptions.movementOptions.maxValue, classData);
			}
			if (otherCharacterOptions.constitutionOptions != null) {
				CharacterRandomizer.randomizeConstitution(otherCharacterOptions.constitutionOptions.minValue, otherCharacterOptions.constitutionOptions.variance, charData, classData);
			}
			if (otherCharacterOptions.randomizeAffinity) {
				CharacterRandomizer.randomizeAffinity(charData);
			}
		}
	}
	
	private void buffEnemiesIfNecessary() {
		if (enemies != null) {
			if (enemies.mode == EnemyOptions.BuffMode.FLAT) {
				EnemyBuffer.buffEnemyGrowthRates(enemies.buffAmount, classData);
			} else if (enemies.mode == EnemyOptions.BuffMode.SCALING) {
				EnemyBuffer.scaleEnemyGrowthRates(enemies.buffAmount, classData);
			}
			
			if (enemies.improveWeapons) {
				EnemyBuffer.improveWeapons(25, charData, classData, chapterData, itemData);
			}
		}
	}
	
	private void randomizeOtherThingsIfNecessary() {
		if (miscOptions != null) {
			if (miscOptions.randomizeRewards) {
				RandomRandomizer.randomizeRewards(itemData, chapterData);
			}
		}
	}
}
