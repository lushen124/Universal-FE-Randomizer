package random;

import java.io.IOException;

import fedata.FEBase;
import io.DiffApplicator;
import io.FileHandler;
import random.exc.FileOpenException;
import random.exc.UnsupportedGameException;
import ui.model.BaseOptions;
import ui.model.ClassOptions;
import ui.model.GrowthOptions;
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
	
	private CharacterDataLoader charData;
	private ClassDataLoader classData;
	private ChapterLoader chapterData;
	private ItemDataLoader itemData;
	
	private FileHandler handler;

	public Randomizer(String sourcePath, String targetPath, FEBase.GameType gameType, DiffCompiler diffs, 
			GrowthOptions growths, BaseOptions bases, ClassOptions classes, WeaponOptions weapons) {
		super();
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		
		diffCompiler = diffs;
		
		this.growths = growths;
		this.bases = bases;
		this.classes = classes;
		this.weapons = weapons;
		
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
		randomizeClassesIfNecessary();
		randomizeBasesIfNecessary();
		randomizeWeaponsIfNecessary();
		
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
}
