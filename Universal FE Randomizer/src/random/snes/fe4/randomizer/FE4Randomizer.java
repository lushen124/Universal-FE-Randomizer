package random.snes.fe4.randomizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4StaticCharacter;
import io.DiffApplicator;
import io.FileHandler;
import io.UPSPatcher;
import random.general.Randomizer;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.HolyBloodLoader;
import random.snes.fe4.loader.ItemMapper;
import ui.fe4.FE4ClassOptions;
import ui.fe4.HolyBloodOptions;
import ui.fe4.SkillsOptions;
import ui.fe4.SkillsOptions.Mode;
import ui.model.BaseOptions;
import ui.model.GrowthOptions;
import ui.model.MiscellaneousOptions;
import util.Diff;
import util.DiffCompiler;
import util.SeedGenerator;
import util.recordkeeper.RecordKeeper;

public class FE4Randomizer extends Randomizer {
	
	private String sourcePath;
	private boolean isHeadered;
	private String targetPath;
	
	private GrowthOptions growthOptions;
	private BaseOptions basesOptions;
	private HolyBloodOptions bloodOptions;
	private SkillsOptions skillsOptions;
	private FE4ClassOptions classOptions;
	private MiscellaneousOptions miscOptions;
	
	HolyBloodLoader bloodData;
	CharacterDataLoader charData;
	ItemMapper itemMapper;
	
	private String seedString;
	
	private DiffCompiler diffCompiler;
	
	private FileHandler handler;
	
	public FE4Randomizer(String sourcePath, boolean isHeadered, String targetPath, DiffCompiler diffs, GrowthOptions growthOptions, BaseOptions basesOptions, HolyBloodOptions bloodOptions, SkillsOptions skillOptions, FE4ClassOptions classOptions, MiscellaneousOptions miscOptions, String seed) {
		super();
		
		this.sourcePath = sourcePath;
		this.isHeadered = isHeadered;
		this.targetPath = targetPath;
		
		this.seedString = seed;
		this.diffCompiler = diffs;
		
		this.growthOptions = growthOptions;
		this.basesOptions = basesOptions;
		this.bloodOptions = bloodOptions;
		this.skillsOptions = skillOptions;
		this.classOptions = classOptions;
		this.miscOptions = miscOptions;
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
		
		// Apply patch first, if necessary.
		if (miscOptions.applyEnglishPatch) {
			updateStatusString("Applying English Patch...");
			updateProgress(0.05);
			
			tempPath = new String(targetPath).concat(".tmp");
			
			Boolean success = false;
			if (isHeadered) {
				success = UPSPatcher.applyUPSPatch("FE4-Naga-Headered.ups", sourcePath, tempPath, null);
			} else {
				success = UPSPatcher.applyUPSPatch("FE4-Naga-Unheadered.ups", sourcePath, tempPath, null);
			}
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
		addUniversalDiffs(isHeadered);
		generateDataLoaders();
		
		RecordKeeper recordKeeper = initializeRecordKeeper();
		recordKeeper.addHeaderItem("Randomizer Seed Phrase", seed);
		
		updateStatusString("Randomizing...");
		randomizeClassesIfNecessary(seed);
		updateProgress(0.60);
		randomizeSkillsIfNecessary(seed);
		updateProgress(0.65);
		randomizeGrowthsIfNecessary(seed);
		updateProgress(0.70);
		randomizeBasesIfNecessary(seed);
		updateProgress(0.75);
		randomizeBloodIfNecessary(seed);
		updateProgress(0.80);
		randomizeRingsIfNecessary(seed);
		updateProgress(0.85);
		makeFinalAdjustments();
		updateProgress(0.90);
		
		updateStatusString("Compiling changes...");
		updateProgress(0.95);
		charData.compileDiffs(diffCompiler);
		itemMapper.compileDiff(diffCompiler);
		
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
		
		charData.recordCharacters(recordKeeper, false, itemMapper);
		bloodData.recordHolyBlood(recordKeeper, false);
		itemMapper.recordRingMap(recordKeeper, false);
		
		recordKeeper.sortKeysInCategoryAndSubcategories(CharacterDataLoader.RecordKeeperCategoryKey);
		
		updateStatusString("Done!");
		updateProgress(1);
		notifyCompletion(recordKeeper);
	}
	
	private void addUniversalDiffs(boolean isHeadered) {
		if (isHeadered) {
			// Diffs for allowing Sigurd/Seliph to sieze, regardless of their class.
			diffCompiler.addDiff(new Diff(0x5E63CL, 4, new byte[] {(byte)0x22, (byte)0x33, (byte)0xA3, (byte)0x84}, new byte[] {(byte)0x22, (byte)0x2D, (byte)0xA0, (byte)0x84}));
			diffCompiler.addDiff(new Diff(0x5E641L, 1, new byte[] {(byte)0x01}, new byte[] {(byte)0x06}));
			diffCompiler.addDiff(new Diff(0x5E646L, 1, new byte[] {(byte)0x19}, new byte[] {(byte)0x2C}));
			
			// Diffs to make all holy weapon inheritable by default.
			diffCompiler.addDiff(new Diff(0x7AD6C, 1, new byte[] {(byte)0x05}, new byte[] {(byte)0x0B}));
			
			// Diffs to allow sword skills to be usable by any weapon type.
			try {
				diffCompiler.addDiffsFromFile("fe4_swordSkills");
			} catch (IOException e) {
				System.err.println("Failed to apply patch for Universal FE4 Sword Skill usage.");
			}
			
			// Diffs to allow sword skills to be inheritable.
			// Table starts at 0x7AB75L and goes to 0x7ABE9 (each entry is 3 bytes).
			diffCompiler.addDiff(new Diff(0x7AB76L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7AB82L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABAFL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABC4L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABC7L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABCAL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABCDL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABD0L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABD3L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABDCL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABDFL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABE2L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7ABE8L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			
		} else {
			// Diffs for allowing Sigurd/Seliph to sieze, regardless of their class.
			diffCompiler.addDiff(new Diff(0x5E43CL, 4, new byte[] {(byte)0x22, (byte)0x33, (byte)0xA3, (byte)0x84}, new byte[] {(byte)0x22, (byte)0x2D, (byte)0xA0, (byte)0x84}));
			diffCompiler.addDiff(new Diff(0x5E441L, 1, new byte[] {(byte)0x01}, new byte[] {(byte)0x06}));
			diffCompiler.addDiff(new Diff(0x5E446L, 1, new byte[] {(byte)0x19}, new byte[] {(byte)0x2C}));
			
			// Diffs to make all holy weapon inheritable by default.
			diffCompiler.addDiff(new Diff(0x7AB6C, 1, new byte[] {(byte)0x05}, new byte[] {(byte)0x0B}));
			
			// Diffs to allow sword skills to be usable by any weapon type.
			try {
				diffCompiler.addDiffsFromFile("fe4_swordSkills", -1 * 0x200);
			} catch (IOException e) {
				System.err.println("Failed to apply patch for Universal FE4 Sword Skill usage.");
			}
			
			// Diffs to allow sword skills to be inheritable.
			// Table starts at 0x7AB75L and goes to 0x7ABE9 (each entry is 3 bytes).
			diffCompiler.addDiff(new Diff(0x7A976L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A982L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9AFL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9C4L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9C7L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9CAL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9CDL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9D0L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9D3L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9DCL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9DFL, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9E2L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));
			diffCompiler.addDiff(new Diff(0x7A9E8L, 1, new byte[] {(byte)0xFF}, new byte[] {(byte)0x07}));			
		}
	}

	private void generateDataLoaders() {
		updateStatusString("Loading Character Data...");
		updateProgress(0.10);
		charData = new CharacterDataLoader(handler, isHeadered);
		
		updateStatusString("Loading Item Map...");
		updateProgress(0.20);
		itemMapper = new ItemMapper(handler, isHeadered);
		
		updateStatusString("Loading Holy Blood Data...");
		updateProgress(0.30);
		bloodData = new HolyBloodLoader(handler, isHeadered);
	}
	
	private void randomizeGrowthsIfNecessary(String seed) {
		if (growthOptions != null) {
			updateStatusString("Randomizing growths...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4GrowthRandomizer.rngSalt));
			switch (growthOptions.mode) {
			case REDISTRIBUTE:
				FE4GrowthRandomizer.randomizeGrowthsByRedistribution(growthOptions.redistributionOption.variance, growthOptions.adjustHP, growthOptions.adjustSTRMAGSplit, charData, rng);
				charData.commit();
				break;
			case DELTA:
				FE4GrowthRandomizer.randomizeGrowthsByRandomDelta(growthOptions.deltaOption.variance, growthOptions.adjustHP, growthOptions.adjustSTRMAGSplit, charData, rng);
				charData.commit();
				break;
			case FULL:
				FE4GrowthRandomizer.fullyRandomizeGrowthsWithRange(growthOptions.fullOption.minValue, growthOptions.fullOption.maxValue, growthOptions.adjustHP, growthOptions.adjustSTRMAGSplit, charData, rng);
				charData.commit();
				break;
			}
		}
	}
	
	private void randomizeBasesIfNecessary(String seed) {
		if (basesOptions != null) {
			updateStatusString("Randomizing base stats...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4BasesRandomizer.rngSalt));
			switch (basesOptions.mode) {
			case REDISTRIBUTE:
				FE4BasesRandomizer.randomizeBasesByRedistribution(basesOptions.redistributionOption.variance, basesOptions.adjustSTRMAGByClass, charData, rng);
				charData.commit();
				break;
			case DELTA:
				FE4BasesRandomizer.randomizeBasesByDelta(basesOptions.deltaOption.variance, basesOptions.adjustSTRMAGByClass, charData, rng);
				charData.commit();
				break;
			}
		}
	}
	
	private void randomizeBloodIfNecessary(String seed) {
		if (bloodOptions != null) {
			if (bloodOptions.randomizeGrowthBonuses) {
				updateStatusString("Randomizing Holy Blood Growth Bonuses...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4BloodRandomizer.rngSalt + 1));
				FE4BloodRandomizer.randomizeHolyBloodGrowthBonuses(bloodOptions.growthTotal, bloodData, rng);
				bloodData.commit();
			}
			if (bloodOptions.randomizeWeaponBonuses) {
				updateStatusString("Randomizing Holy Weapon Bonuses...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4BloodRandomizer.rngSalt + 2));
				FE4BloodRandomizer.randomizeHolyWeaponBonuses(bloodData, rng);
			}
		}
	}
	
	private void randomizeClassesIfNecessary(String seed) {
		if (classOptions != null) {
			if (classOptions.randomizePlayableCharacters) {
				updateStatusString("Randomizing player classes...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4ClassRandomizer.rngSalt + 1));
				FE4ClassRandomizer.randomizePlayableCharacterClasses(classOptions, charData, itemMapper, rng);
				charData.commit();
				itemMapper.commitChanges();
			}
			if (classOptions.randomizeMinions) {
				updateStatusString("Randomizing minions...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4ClassRandomizer.rngSalt + 2));
				FE4ClassRandomizer.randomizeMinions(classOptions, charData, rng);
				charData.commit();
			}
			if (classOptions.randomizeBosses) {
				updateStatusString("Randomizing bosses...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4ClassRandomizer.rngSalt + 3));
				FE4ClassRandomizer.randomizeBosses(classOptions, charData, rng);
				charData.commit();
			}
			if (classOptions.randomizeArena) {
				updateStatusString("Randomizing arena combatants...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4ClassRandomizer.rngSalt + 4));
				FE4ClassRandomizer.randomizeArena(classOptions, charData, rng);
				charData.commit();
			}
		}
	}
	
	private void randomizeSkillsIfNecessary(String seed) {
		if (skillsOptions != null) {
			if (skillsOptions.mode == Mode.RANDOMIZE) {
				updateStatusString("Randomizing Skills...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4SkillsRandomizer.rngSalt + 1));
				FE4SkillsRandomizer.randomizePlayableCharacterSkills(skillsOptions, charData, rng);
				charData.commit();
			} else if (skillsOptions.mode == Mode.SHUFFLE) {
				updateStatusString("Shuffling Skills...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4SkillsRandomizer.rngSalt + 2));
				FE4SkillsRandomizer.shufflePlayableCharacterSkills(skillsOptions, charData, rng);
				charData.commit();
			}
		}
	}
	
	private void randomizeRingsIfNecessary(String seed) {
		if (miscOptions.randomizeRewards) {
			updateStatusString("Randomizing Rings...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4RingRandomizer.rngSalt + 1));
			FE4RingRandomizer.randomizeRings(itemMapper, rng);
			itemMapper.commitChanges();
		}
	}
	
	// Should be called after all other randomizations.
	private void makeFinalAdjustments() {
		updateStatusString("Making final adjustments...");
		
		// These only need to be performed if playable character classes were randomized. Otherwise, the default values should still work.
		if (classOptions.randomizePlayableCharacters) {
			// Make sure Sigurd does NOT pass his holy weapon to Seliph.
			// Tyrfing normally sits at inventory ID 0x27. Since we didn't change inventory IDs, this should still be safe.
			diffCompiler.addDiff(new Diff(FE4Data.SeliphHolyWeaponInheritenceBanOffset, 1, new byte[] {(byte)(itemMapper.getItemAtIndex(0x27).ID & 0xFF)}, new byte[] {(FE4Data.SeliphHolyWeaponInheritenceBanOldID)}));
			diffCompiler.addDiff(new Diff(FE4Data.SeliphHolyWeaponInheritenceBanOffset2, 1, new byte[] {FE4Data.SeliphHolyWeaponInheritenceBanNewValue}, new byte[] {FE4Data.SeliphHolyWeaponInheritenceBanOldValue}));
			
			// Make sure Lex's Hero Axe event still triggers (the reward should have already been updated if the "Adjust Conversation Items" option was enabled).
			// Trigger it off of whatever equipment Lex started with.
			FE4StaticCharacter lex = charData.getStaticCharacter(FE4Data.Character.LEX);
			int equip1 = lex.getEquipment1();
			FE4Data.Item item1 = FE4Data.Item.valueOf(equip1);
			diffCompiler.addDiff(new Diff(FE4Data.LexHeroAxeEventItemRequirementOffset, 1, new byte[] {(byte)item1.ID}, new byte[] {FE4Data.LexHeroAxeEventItemRequirementOldID}));
		}
	}
	
	public RecordKeeper initializeRecordKeeper() {
		int index = Math.max(targetPath.lastIndexOf('/'), targetPath.lastIndexOf('\\'));
		String title =  targetPath.substring(index + 1);
		String gameTitle = FE4Data.FriendlyName;
		
		RecordKeeper rk = new RecordKeeper(title);
		
		rk.addHeaderItem("Game Title", gameTitle);
		
		charData.recordCharacters(rk, true, itemMapper);
		bloodData.recordHolyBlood(rk, true);
		itemMapper.recordRingMap(rk, true);
		
		return rk;
	}
}
