package random.gcnwii.fe9.randomizer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterRewards;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import io.FileHandler;
import io.FileWriter;
import io.gcn.GCNCMBFileHandler;
import io.gcn.GCNCMPFileHandler;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNFSTEntry;
import io.gcn.GCNFSTFileEntry;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import io.gcn.GCNISOHandlerRecompilationDelegate;
import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9CommonTextLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;
import random.general.Randomizer;
import ui.fe9.FE9ClassOptions;
import ui.fe9.FE9SkillsOptions;
import ui.model.BaseOptions;
import ui.model.FE9EnemyBuffOptions;
import ui.model.FE9OtherCharacterOptions;
import ui.model.GrowthOptions;
import ui.model.MiscellaneousOptions;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.LZ77;
import util.SeedGenerator;
import util.WhyDoesJavaNotHaveThese;

public class FE9Randomizer extends Randomizer {
	private String sourcePath;
	private String targetPath;
	
	private String seedString;
	
	private GCNISOHandler handler;
	
	private GrowthOptions growthOptions;
	private BaseOptions baseOptions;
	private FE9SkillsOptions skillOptions;
	private MiscellaneousOptions miscOptions;
	private FE9OtherCharacterOptions otherCharOptions;
	private FE9EnemyBuffOptions enemyBuffOptions;
	private FE9ClassOptions classOptions;
	
	FE9CommonTextLoader textData;
	FE9CharacterDataLoader charData;
	FE9ClassDataLoader classData;
	FE9ItemDataLoader itemData;
	FE9SkillDataLoader skillData;
	FE9ChapterDataLoader chapterData;
	
	public FE9Randomizer(String sourcePath, String targetPath, GrowthOptions growthOptions, BaseOptions baseOptions, FE9SkillsOptions skillOptions, FE9OtherCharacterOptions otherCharOptions, FE9EnemyBuffOptions enemyBuffOptions, FE9ClassOptions classOptions, MiscellaneousOptions miscOptions, String seed) {
		super();
		
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		
		this.growthOptions = growthOptions;
		this.baseOptions = baseOptions;
		this.skillOptions = skillOptions;
		this.otherCharOptions = otherCharOptions;
		this.enemyBuffOptions = enemyBuffOptions;
		this.classOptions = classOptions;
		this.miscOptions = miscOptions;
		
		this.seedString = seed;
	}
	
	public void run() {
		randomize(seedString);
	}
	
	private void updateStatus(double progress, String description) {
		updateProgress(progress);
		updateStatusString(description);
	}
	
	private void randomize(String seed) {
		try {
			updateStatus(0.05, "Creating file handler for ISO file...");
			handler = new GCNISOHandler(new FileHandler(sourcePath));
		} catch (IOException e) {
			notifyError("Failed to open source file.");
			return;
		} catch (GCNISOException e) {
			notifyError("Failed to read Gamecube ISO format.");
		}
		
		try {
			updateStatus(0.10, "Loading Text Data...");
			textData = new FE9CommonTextLoader(handler);
			updateStatus(0.15, "Loading Character Data...");
			charData = new FE9CharacterDataLoader(handler, textData);
			updateStatus(0.20, "Loading Class Data...");
			classData = new FE9ClassDataLoader(handler, textData);
			updateStatus(0.25, "Loading Item Data...");
			itemData = new FE9ItemDataLoader(handler, textData);
			updateStatus(0.30, "Loading Skill Data...");
			skillData = new FE9SkillDataLoader(handler, textData);
			updateStatus(0.35, "Loading Chapter Data...");
			chapterData = new FE9ChapterDataLoader(handler, textData);
			
			randomizeClassesIfNecessary(seed);
			randomizeGrowthsIfNecessary(seed);
			randomizeBasesIfNecessary(seed);
			randomizeSkillsIfNecessary(seed);
			randomizeOtherCharacterOptionsIfNecessary(seed);
			randomizeMiscellaneousIfNecessary(seed);
			buffEnemiesIfNecessary(seed);
			
			updateStatus(0.50, "Committing changes...");
			charData.compileDiffs(handler);
			classData.compileDiffs(handler);
			
		} catch (GCNISOException e1) {
			notifyError("Failed to load character data.");
			return;
		}
		
		handler.recompile(targetPath, new GCNISOHandlerRecompilationDelegate() {
			
			@Override
			public void onStatusUpdate(String status) {
				updateStatusString(status);
			}
			
			@Override
			public void onProgressUpdate(double progress) {
				updateProgress(0.5 + (0.5 * progress));
			}
		});
		
		notifyCompletion(null);
	}
	
	private void randomizeGrowthsIfNecessary(String seed) {
		if (growthOptions != null) {
			updateStatus(0.42, "Randomizing growths...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9GrowthRandomizer.rngSalt));
			switch (growthOptions.mode) {
			case REDISTRIBUTE:
				FE9GrowthRandomizer.randomizeGrowthsByRedistribution(growthOptions.redistributionOption.variance, growthOptions.adjustHP, growthOptions.adjustSTRMAGSplit, charData, classData, rng);
				break;
			case DELTA:
				FE9GrowthRandomizer.randomizeGrowthsByDelta(growthOptions.deltaOption.variance, growthOptions.adjustSTRMAGSplit, charData, classData, rng);
				break;
			case FULL:
				FE9GrowthRandomizer.randomizeGrowthsFully(growthOptions.fullOption.minValue, growthOptions.fullOption.minValue, growthOptions.adjustHP, growthOptions.adjustSTRMAGSplit, charData, classData, rng);
				break;
			}
			charData.commit();
		}
	}
	
	private void randomizeBasesIfNecessary(String seed) {
		if (baseOptions != null) {
			updateStatus(0.44, "Randomizing bases...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9BasesRandomizer.rngSalt));
			switch (baseOptions.mode) {
			case REDISTRIBUTE:
				FE9BasesRandomizer.randomizeBasesByRedistribution(baseOptions.redistributionOption.variance, baseOptions.adjustSTRMAGByClass, charData, classData, rng);
				break;
			case DELTA:
				FE9BasesRandomizer.randomizeBasesByDelta(baseOptions.deltaOption.variance, baseOptions.adjustSTRMAGByClass, charData, classData, rng);
				break;
			}
			
			// If we do this, nerf Prologue Boyd to make sure the prologue is completeable, in case we randomize a crappy Ike.
			FE9BasesRandomizer.nerfPrologueBoyd(charData);
			
			charData.commit();
		}
	}
	
	private void randomizeSkillsIfNecessary(String seed) {
		if (skillOptions != null) {
			updateStatus(0.46, "Randomizing skills...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9SkillRandomizer.rngSalt));
			switch (skillOptions.mode) {
			case RANDOMIZE_EXISTING:
				FE9SkillRandomizer.randomizeExistingSkills(skillOptions.skillWeights, charData, skillData, rng);
				break;
			case FULL_RANDOM:
				FE9SkillRandomizer.fullyRandomizeSkills(skillOptions.skillChance, skillOptions.skillWeights, charData, skillData, chapterData, rng);
				break;
			}
			charData.commit();
		}
	}
	
	private void randomizeMiscellaneousIfNecessary(String seed) {
		if (miscOptions != null) {
			updateStatus(0.48, "Randomizing rewards...");
			if (miscOptions.randomizeRewards) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9RewardsRandomizer.rngSalt));
				switch (miscOptions.rewardMode) {
				case SIMILAR:
					FE9RewardsRandomizer.randomizeSimilarRewards(itemData, chapterData, rng);
					break;
				case RANDOM:
					FE9RewardsRandomizer.randomizeRewards(itemData, chapterData, rng);
					break;
				}
			}
		}
	}
	
	private void randomizeOtherCharacterOptionsIfNecessary(String seed) {
		if (otherCharOptions != null) {
			updateStatus(0.47, "Randomizing other character options...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9MiscellaneousRandomizer.rngSalt));
			if (otherCharOptions.randomizeCON) {
				FE9MiscellaneousRandomizer.randomizeCON(otherCharOptions.conVariance, charData, classData, rng);
			}
			if (otherCharOptions.randomizeAffinity) {
				FE9MiscellaneousRandomizer.randomizeAffinity(charData, rng);
			}
		}
	}
	
	private void buffEnemiesIfNecessary(String seed) {
		if (enemyBuffOptions != null) {
			updateStatus(0.49, "Buffing enemies...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9EnemyBuffer.rngSalt));
			
			switch (enemyBuffOptions.minionMode) {
			case FLAT:
				FE9EnemyBuffer.flatBuffMinionGrowths(enemyBuffOptions.minionBuff, classData);
				break;
			case SCALING:
				FE9EnemyBuffer.scaleBuffMinionGrowths(enemyBuffOptions.minionBuff, classData);
				break;
			default:
				break;
			}
			if (enemyBuffOptions.improveMinionWeapons) {
				FE9EnemyBuffer.improveMinionWeapons(enemyBuffOptions.minionImprovementChance, charData, classData, itemData, chapterData, rng);
			}
			if (enemyBuffOptions.giveMinionsSkills) {
				FE9EnemyBuffer.giveMinionSkills(enemyBuffOptions.minionSkillChance, charData, classData, skillData, chapterData, rng);
			}
			
			switch (enemyBuffOptions.bossMode) {
			case LINEAR:
				FE9EnemyBuffer.buffBossStatsLinearly(enemyBuffOptions.bossBuff, charData, classData);
				break;
			case EASE_IN_OUT:
				FE9EnemyBuffer.buffBossStatsByEasing(enemyBuffOptions.bossBuff, charData, classData);
				break;
			default:
				break;
			}
			if (enemyBuffOptions.improveBossWeapons) {
				FE9EnemyBuffer.improveBossWeapons(enemyBuffOptions.bossImprovementChance, charData, classData, itemData, chapterData, rng);
			}
			if (enemyBuffOptions.giveBossSkills) {
				FE9EnemyBuffer.giveBossesSkills(enemyBuffOptions.bossSkillChance, charData, classData, skillData, rng);
			}
		}
	}
	
	private void randomizeClassesIfNecessary(String seed) {
		if (classOptions != null) {
			updateStatus(0.4, "Randomizing classes...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9ClassRandomizer.rngSalt));
			if (classOptions.randomizePCs) {
				FE9ClassRandomizer.randomizePlayableCharacters(classOptions.includeLords, 
						classOptions.includeThieves, 
						classOptions.includeSpecial, 
						classOptions.forceDifferent, 
						classOptions.mixPCRaces, 
						classOptions.allowCrossgender, charData, classData, chapterData, skillData, itemData, rng);
				try {
					FE9ClassRandomizer.updateIkeInventoryChapter1Script(charData, itemData, handler);
				} catch (GCNISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (classOptions.randomizeBosses) {
				FE9ClassRandomizer.randomizeBossCharacters(classOptions.forceDifferent, 
						classOptions.mixBossRaces, false, charData, classData, chapterData, skillData, itemData, rng);
			}
			if (classOptions.randomizeMinions) {
				FE9ClassRandomizer.randomizeMinionCharacters(classOptions.minionRandomChance,
						classOptions.forceDifferent, 
						classOptions.mixMinionRaces, false, charData, classData, chapterData, skillData, itemData, rng);
			}
		}
	}
}
