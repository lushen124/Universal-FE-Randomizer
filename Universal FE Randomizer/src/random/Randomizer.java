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
import util.DiffCompiler;



public class Randomizer {
	
	private String sourcePath;
	private String targetPath;
	
	private FEBase.GameType gameType;
	
	private DiffCompiler diffCompiler;
	
	private GrowthOptions growths;
	private BaseOptions bases;
	private ClassOptions classes;
	
	private CharacterDataLoader charData;
	private ClassDataLoader classData;
	private ChapterLoader chapterData;
	private ItemDataLoader itemData;
	
	private FileHandler handler;

	public Randomizer(String sourcePath, String targetPath, FEBase.GameType gameType, DiffCompiler diffs, GrowthOptions growths, BaseOptions bases, ClassOptions classes) {
		super();
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		
		diffCompiler = diffs;
		
		this.growths = growths;
		this.bases = bases;
		this.classes = classes;
		
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
		
		charData.compileDiffs(diffCompiler);
		chapterData.compileDiffs(diffCompiler);
		
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
}
