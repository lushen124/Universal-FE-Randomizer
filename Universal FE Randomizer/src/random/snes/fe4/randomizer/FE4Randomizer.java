package random.snes.fe4.randomizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Class.ClassSkills;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4Data.CharacterClass;
import fedata.snes.fe4.FE4StaticCharacter;
import io.DiffApplicator;
import io.FileHandler;
import io.UPSPatcher;
import random.general.Randomizer;
import random.general.WeightedDistributor;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.ClassDataLoader;
import random.snes.fe4.loader.ItemDataLoader;
import random.snes.fe4.loader.HolyBloodLoader;
import random.snes.fe4.loader.ItemMapper;
import random.snes.fe4.loader.PromotionMapper;
import ui.fe4.FE4ClassOptions;
import ui.fe4.FE4EnemyBuffOptions;
import ui.fe4.FE4PromotionOptions;
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
	private FE4PromotionOptions promoOptions;
	private FE4EnemyBuffOptions buffOptions;
	private MiscellaneousOptions miscOptions;
	
	HolyBloodLoader bloodData;
	CharacterDataLoader charData;
	ClassDataLoader classData;
	ItemDataLoader itemData;
	ItemMapper itemMapper;
	PromotionMapper promotionMapper;
	
	private String seedString;
	
	private DiffCompiler diffCompiler;
	
	private FileHandler handler;
	
	public FE4Randomizer(String sourcePath, boolean isHeadered, String targetPath, DiffCompiler diffs, GrowthOptions growthOptions, BaseOptions basesOptions, HolyBloodOptions bloodOptions, 
			SkillsOptions skillOptions, FE4ClassOptions classOptions, FE4PromotionOptions promoOptions, FE4EnemyBuffOptions buffOptions, MiscellaneousOptions miscOptions, String seed) {
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
		this.promoOptions = promoOptions;
		this.buffOptions = buffOptions;
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
		updateProgress(0.40);
		randomizeSkillsIfNecessary(seed);
		updateProgress(0.45);
		randomizeGrowthsIfNecessary(seed);
		updateProgress(0.50);
		randomizeBasesIfNecessary(seed);
		updateProgress(0.55);
		randomizeBloodIfNecessary(seed);
		updateProgress(0.65);
		randomizeRingsIfNecessary(seed);
		updateProgress(0.75);
		randomizePromotionsIfNecessary(seed);
		updateProgress(0.85);
		buffEnemiesIfNecessary(seed);
		updateProgress(0.90);
		makeFinalAdjustments(seed);
		updateProgress(0.95);
		
		updateStatusString("Compiling changes...");
		updateProgress(0.95);
		charData.compileDiffs(diffCompiler);
		itemMapper.compileDiff(diffCompiler);
		bloodData.compileDiffs(diffCompiler);
		promotionMapper.compileDiff(diffCompiler);
		itemData.compileDiffs(diffCompiler);
		classData.compileDiffs(diffCompiler);
		
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
		
		charData.registerInventories(itemMapper);
		// Register shop items.
		for (FE4Data.ShopItem shopItem : FE4Data.ShopItems) {
			itemMapper.registerInventoryID(shopItem.itemInventoryID, "Shop (Ch. " + Integer.toString(shopItem.chapter) + ")");
		}
		// Register known event items.
		for (FE4Data.EventGift eventItem : FE4Data.EventGifts) {
			if (eventItem.donor != FE4Data.Character.NONE) {
				itemMapper.registerInventoryID(eventItem.giftInventoryID, "Event (" + eventItem.donor.toString() + " -> " + eventItem.recipient.toString() + ")");
			} else {
				itemMapper.registerInventoryID(eventItem.giftInventoryID, "Event (" + eventItem.recipient.toString() + ")");
			}
		}
		// Register village items.
		for (FE4Data.VillageGift villageItem : FE4Data.VillageGifts) {
			if (villageItem.chapter == 0) {
				itemMapper.registerInventoryID(villageItem.giftInventoryID, "Village (Prologue)");
			} else {
				itemMapper.registerInventoryID(villageItem.giftInventoryID, "Village (Ch. " + villageItem.chapter + ")");
			}
		}
		
		charData.recordCharacters(recordKeeper, false, itemMapper);
		bloodData.recordHolyBlood(recordKeeper, false);
		itemMapper.recordItemMap(recordKeeper, false);
		promotionMapper.recordPromotions(recordKeeper, false);
		classData.recordClasses(recordKeeper, false);
		
		charData.recordAdditionalData(recordKeeper, bloodData, classData);
		
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
			
			// Diffs to allow holy weapons to be sellable.
			diffCompiler.addDiff(new Diff(FE4Data.SellableHolyWeaponsOffset, 1, new byte[] {FE4Data.SellableHolyWeaponEnabledValue}, new byte[] {FE4Data.SellableHolyWeaponsDisabledValue}));
			
			// Diff to fix Female Emperor battle animation.
			diffCompiler.addDiff(new Diff(FE4Data.FemaleEmperorStaffAnimationFixOffset, 1, new byte[] {FE4Data.FemaleEmperorStaffAnimationFixNewValue}, new byte[] {FE4Data.FemaleEmperorStaffAnimationFixOldValue}));
			
			// Diff to remove 0x34 from the Ch. 8 shop, since we're using it for Deirdre's Aura.
			diffCompiler.addDiff(new Diff(FE4Data.Chapter8ShopListOffset, FE4Data.Chapter8ShopOldListByteArray.length, FE4Data.Chapter8ShopNewListByteArray, FE4Data.Chapter8ShopOldListByteArray));
			
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
			
			// Diffs to allow holy weapons to be sellable.
			diffCompiler.addDiff(new Diff(FE4Data.SellableHolyWeaponsOffset - 0x200, 1, new byte[] {FE4Data.SellableHolyWeaponEnabledValue}, new byte[] {FE4Data.SellableHolyWeaponsDisabledValue}));
			
			// Diff to fix Female Emperor battle animation.
			diffCompiler.addDiff(new Diff(FE4Data.FemaleEmperorStaffAnimationFixOffset - 0x200, 1, new byte[] {FE4Data.FemaleEmperorStaffAnimationFixNewValue}, new byte[] {FE4Data.FemaleEmperorStaffAnimationFixOldValue}));
			
			// Diff to remove 0x34 from the Ch. 8 shop, since we're using it for Deirdre's Aura.
			diffCompiler.addDiff(new Diff(FE4Data.Chapter8ShopListOffset - 0x200, FE4Data.Chapter8ShopOldListByteArray.length, FE4Data.Chapter8ShopNewListByteArray, FE4Data.Chapter8ShopOldListByteArray));
		}
	}

	private void generateDataLoaders() {
		updateStatusString("Loading Character Data...");
		updateProgress(0.10);
		charData = new CharacterDataLoader(handler, isHeadered);
		
		updateStatusString("Loading Item Map...");
		updateProgress(0.15);
		itemMapper = new ItemMapper(handler, isHeadered);
		
		updateStatusString("Loading Holy Blood Data...");
		updateProgress(0.20);
		bloodData = new HolyBloodLoader(handler, isHeadered);
		
		updateStatusString("Loading Promotion Map...");
		updateProgress(0.25);
		promotionMapper = new PromotionMapper(handler, charData, isHeadered);
		
		updateStatusString("Loading Item Data...");
		updateProgress(0.30);
		itemData = new ItemDataLoader(handler, isHeadered);
		
		updateStatusString("Loading Class Data...");
		updateProgress(0.40);
		classData = new ClassDataLoader(handler, isHeadered);
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
			if (bloodOptions.giveHolyBlood) {
				updateStatusString("Assigning Holy Blood...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4BloodRandomizer.rngSalt + 3));
				FE4BloodRandomizer.assignHolyBlood(bloodOptions.majorBloodChance, bloodOptions.minorBloodChance, bloodOptions.matchClass, charData, bloodData, itemMapper, rng);
			}
		}
	}
	
	private void randomizeClassesIfNecessary(String seed) {
		if (classOptions != null) {
			if (classOptions.randomizePlayableCharacters) {
				updateStatusString("Randomizing player classes...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4ClassRandomizer.rngSalt + 1));
				FE4ClassRandomizer.randomizePlayableCharacterClasses(classOptions, charData, bloodData, itemMapper, rng);
				charData.commit();
				itemMapper.commitChanges();
			}
			if (classOptions.randomizeMinions) {
				updateStatusString("Randomizing minions...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4ClassRandomizer.rngSalt + 2));
				FE4ClassRandomizer.randomizeMinions(classOptions, charData, itemMapper, rng);
				charData.commit();
			}
			if (classOptions.randomizeBosses) {
				updateStatusString("Randomizing bosses...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4ClassRandomizer.rngSalt + 3));
				FE4ClassRandomizer.randomizeBosses(classOptions, charData, itemMapper, rng);
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
	
	private void randomizePromotionsIfNecessary(String seed) {
		if (promoOptions != null) {
			// Don't touch promotions if they're supposed to be strict and we didn't randomize playable character classes.
			if (promoOptions.promotionMode == FE4PromotionOptions.Mode.STRICT && (classOptions == null || !classOptions.randomizePlayableCharacters)) { return; }
			updateStatusString("Randomizing Promotions...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4PromotionRandomizer.rngSalt + 1));
			FE4PromotionRandomizer.randomizePromotions(promoOptions, charData, promotionMapper, rng);
			// Special case, since Finn is the only character to go between both gens and is unpromoted.
			// It's possible for him to get two different promotions in Gen2, so we want to make sure he's synced across both gens.
			promotionMapper.setPromotionForCharacter(FE4Data.Character.FINN_GEN_2, promotionMapper.getPromotionForCharacter(FE4Data.Character.FINN_GEN_1));
		}
	}
	
	private void buffEnemiesIfNecessary(String seed) {
		if (buffOptions != null) {
			if (buffOptions.increaseEnemyScaling) {
				updateStatusString("Scaling up enemies...");
				FE4EnemyBuffer.buffEnemyStats(buffOptions, charData, classData);
			}
			if (buffOptions.improveMinionWeapons) {
				updateStatusString("Improving Enemy Weapons...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4EnemyBuffer.rngSalt + 1));
				FE4EnemyBuffer.improveEquipment(buffOptions, charData, rng);
			}
			if (buffOptions.majorHolyBloodBosses) {
				updateStatusString("Upgrading Holy Bosses...");
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE4EnemyBuffer.rngSalt + 2));
				FE4EnemyBuffer.forceMajorBloodOnHolyBosses(buffOptions, charData, itemMapper, rng);
			}
		}
	}
	
	// Should be called after all other randomizations.
	private void makeFinalAdjustments(String seed) {
		updateStatusString("Making final adjustments...");
		
		// Give Dark magic a price
		itemData.itemForID(FE4Data.Item.YOTSMUNGAND.ID).setPrice(15000);
		itemData.itemForID(FE4Data.Item.HEL.ID).setPrice(20000);
		
		// Remove Charm from Princess
		classData.classForID(FE4Data.CharacterClass.PRINCESS.ID).setSlot2ClassSkills(new ArrayList<ClassSkills>());
		
		// Gotta fix Oifey's promotion so that he doesn't somehow promote even though he's already promoted.
		promotionMapper.setPromotionForCharacter(FE4Data.Character.OIFEY, FE4Data.CharacterClass.NONE);
		
		// These only need to be performed if playable character classes were randomized. Otherwise, the default values should still work.
		if (classOptions.randomizePlayableCharacters) {
			// Make sure Sigurd does NOT pass his holy weapon to Seliph.
			// Tyrfing normally sits at inventory ID 0x27. Since we didn't change inventory IDs, this should still be safe.
			diffCompiler.addDiff(new Diff(FE4Data.SeliphHolyWeaponInheritenceBanOffset - (isHeadered ? 0 : 0x200), 1, new byte[] {(byte)(itemMapper.getItemAtIndex(0x27).ID & 0xFF)}, new byte[] {(FE4Data.SeliphHolyWeaponInheritenceBanOldID)}));
			diffCompiler.addDiff(new Diff(FE4Data.SeliphHolyWeaponInheritenceBanOffset2 - (isHeadered ? 0 : 0x200), 1, new byte[] {FE4Data.SeliphHolyWeaponInheritenceBanNewValue}, new byte[] {FE4Data.SeliphHolyWeaponInheritenceBanOldValue}));
			
			// Make sure Lex's Hero Axe event still triggers (the reward should have already been updated if the "Adjust Conversation Items" option was enabled).
			// Trigger it off of whatever equipment Lex started with.
			FE4StaticCharacter lex = charData.getStaticCharacter(FE4Data.Character.LEX);
			int equip1 = lex.getEquipment1();
			FE4Data.Item item1 = itemMapper.getItemAtIndex(equip1);
			diffCompiler.addDiff(new Diff(FE4Data.LexHeroAxeEventItemRequirementOffset - (isHeadered ? 0 : 0x200), 1, new byte[] {(byte)item1.ID}, new byte[] {FE4Data.LexHeroAxeEventItemRequirementOldID}));
		}
		
		if (classOptions.randomizeBlood || bloodOptions.giveHolyBlood) {
			// Hard code Seliph's Holy Blood, based on his parents.
			// He only has the first two bytes, so drop the other blood (which they should be limited in already).
			FE4StaticCharacter sigurd = charData.getStaticCharacter(FE4Data.Character.SIGURD);
			FE4StaticCharacter deirdre = charData.getStaticCharacter(FE4Data.Character.DEIRDRE);
			
			List<FE4Data.HolyBloodSlot1> sigurdSlot1 = FE4Data.HolyBloodSlot1.slot1HolyBlood(sigurd.getHolyBlood1Value());
			List<FE4Data.HolyBloodSlot1> deirdreSlot1 = FE4Data.HolyBloodSlot1.slot1HolyBlood(deirdre.getHolyBlood1Value());
			
			List<FE4Data.HolyBloodSlot2> sigurdSlot2 = FE4Data.HolyBloodSlot2.slot2HolyBlood(sigurd.getHolyBlood2Value());
			List<FE4Data.HolyBloodSlot2> deirdreSlot2 = FE4Data.HolyBloodSlot2.slot2HolyBlood(deirdre.getHolyBlood2Value());
			
			// We can discard any major blood Deirdre has, as that will generally go to Julia. Seliph will always inherit Sigurd's Major blood and a minor version of Deirdre's Major Blood.
			FE4Data.HolyBlood deirdreMajorBlood = null;
			FE4Data.HolyBlood sigurdMajorBlood = null;
			sigurdMajorBlood = sigurdSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent() ? sigurdSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType() : null;
			if (sigurdMajorBlood == null) {
				sigurdMajorBlood = sigurdSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent() ? sigurdSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType() : null;
			}
			deirdreMajorBlood = deirdreSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent() ? deirdreSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType() : null;
			if (deirdreMajorBlood == null) {
				deirdreMajorBlood = deirdreSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent() ? deirdreSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType() : null;
			}
			
			Set<FE4Data.HolyBloodSlot1> seliphSlot1Set = new HashSet<FE4Data.HolyBloodSlot1>();
			Set<FE4Data.HolyBloodSlot2> seliphSlot2Set = new HashSet<FE4Data.HolyBloodSlot2>();
			
			if (sigurdMajorBlood != null) {
				FE4Data.HolyBloodSlot1 slot1Major = FE4Data.HolyBloodSlot1.blood(sigurdMajorBlood, true);
				if (slot1Major != null) {
					seliphSlot1Set.add(slot1Major);
				} else {
					FE4Data.HolyBloodSlot2 slot2Major = FE4Data.HolyBloodSlot2.blood(sigurdMajorBlood, true);
					if (slot2Major != null) {
						seliphSlot2Set.add(slot2Major);
					}
				}
			}
			
			if (deirdreMajorBlood != null) {
				FE4Data.HolyBloodSlot1 slot1Minor = FE4Data.HolyBloodSlot1.blood(deirdreMajorBlood, false);
				if (slot1Minor != null) {
					seliphSlot1Set.add(slot1Minor);
				} else {
					FE4Data.HolyBloodSlot2 slot2Minor = FE4Data.HolyBloodSlot2.blood(deirdreMajorBlood, false);
					if (slot2Minor != null) {
						seliphSlot2Set.add(slot2Minor);
					}
				}
			}
			
			// Transfer all minor blood that might be present.
			seliphSlot1Set.addAll(sigurdSlot1.stream().filter(blood -> (blood.isMajor() == false)).collect(Collectors.toList()));
			seliphSlot1Set.addAll(deirdreSlot1.stream().filter(blood -> (blood.isMajor() == false)).collect(Collectors.toList()));
			seliphSlot2Set.addAll(sigurdSlot2.stream().filter(blood -> (blood.isMajor() == false)).collect(Collectors.toList()));
			seliphSlot2Set.addAll(deirdreSlot2.stream().filter(blood -> (blood.isMajor() == false)).collect(Collectors.toList()));
			
			// Search for shared minor, which will become major.
			Set<FE4Data.HolyBloodSlot1> sharedSlot1 = new HashSet<FE4Data.HolyBloodSlot1>(sigurdSlot1);
			sharedSlot1.retainAll(deirdreSlot1);
			for (FE4Data.HolyBloodSlot1 slot1 : sharedSlot1) {
				if (slot1.isMajor()) { continue; }
				// Remove shared minor blood and add the major version.
				seliphSlot1Set.remove(slot1);
				FE4Data.HolyBlood bloodType = slot1.bloodType();
				seliphSlot1Set.add(FE4Data.HolyBloodSlot1.blood(bloodType, true));
			}
			
			Set<FE4Data.HolyBloodSlot2> sharedSlot2 = new HashSet<FE4Data.HolyBloodSlot2>(sigurdSlot2);
			sharedSlot2.retainAll(deirdreSlot2);
			for (FE4Data.HolyBloodSlot2 slot2 : sharedSlot2) {
				if (slot2.isMajor()) { continue; }
				// Remove shared minor blood and add the major version.
				seliphSlot2Set.remove(slot2);
				FE4Data.HolyBlood bloodType = slot2.bloodType();
				seliphSlot2Set.add(FE4Data.HolyBloodSlot2.blood(bloodType, true));
			}
			
			List<FE4Data.HolyBloodSlot1> seliphSlot1 = new ArrayList<FE4Data.HolyBloodSlot1>(seliphSlot1Set);
			List<FE4Data.HolyBloodSlot2> seliphSlot2 = new ArrayList<FE4Data.HolyBloodSlot2>(seliphSlot2Set);
			
			int slot1Value = FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(seliphSlot1);
			int slot2Value = FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(seliphSlot2);
			
			diffCompiler.addDiff(new Diff(FE4Data.SeliphHolyBloodByte1Offset - (isHeadered ? 0 : 0x200), 1, new byte[] {(byte)slot1Value}, null));
			diffCompiler.addDiff(new Diff(FE4Data.SeliphHolyBloodByte2Offset - (isHeadered ? 0 : 0x200), 1, new byte[] {(byte)slot2Value}, null));
			
			if (classOptions != null && promoOptions != null && promoOptions.promotionMode != FE4PromotionOptions.Mode.STRICT) {
				// Make sure Seliph's class can use Sigurd's major blood weapon.
				FE4ChildCharacter seliph = charData.getChildCharacter(FE4Data.Character.SELIPH);
				FE4Data.CharacterClass seliphClass = FE4Data.CharacterClass.valueOf(seliph.getCharacterID());
				Set<FE4Data.HolyBlood> supportedBlood = new HashSet<FE4Data.HolyBlood>(Arrays.asList(seliphClass.supportedHolyBlood()));
				if (supportedBlood.contains(sigurdMajorBlood) == false) {
					FE4Data.CharacterClass[] fullPool = sigurdMajorBlood.classPool();
					Set<FE4Data.CharacterClass> unpromotedPool = new HashSet<FE4Data.CharacterClass>(Arrays.asList(FE4Data.CharacterClass.filteredClasses(fullPool, false, false)));
					unpromotedPool.removeAll(Arrays.asList(FE4Data.Character.SELIPH.blacklistedClasses()));
					List<FE4Data.CharacterClass> unpromotedList = unpromotedPool.stream().sorted(new Comparator<FE4Data.CharacterClass>() {
						@Override
						public int compare(CharacterClass arg0, CharacterClass arg1) {
							return Integer.compare(arg0.ID, arg1.ID);
						}
					 }).collect(Collectors.toList());
					
					if (!unpromotedList.isEmpty()) {
						Random rng = new Random(SeedGenerator.generateSeedValue(seed, 0));
						FE4Data.CharacterClass unpromotedClass = unpromotedList.get(rng.nextInt(unpromotedList.size()));
						FE4ClassRandomizer.setChildCharacterToClass(classOptions, seliph, sigurd, unpromotedClass, itemMapper, rng);
						if (promoOptions.promotionMode == FE4PromotionOptions.Mode.RANDOM) {
							FE4Data.CharacterClass[] promotedPool = FE4Data.CharacterClass.filteredClasses(fullPool, true, false);
							promotionMapper.setPromotionForCharacter(FE4Data.Character.SELIPH, promotedPool[rng.nextInt(promotedPool.length)]);
						} else { // LOOSE
							FE4Data.CharacterClass[] promotedPool = unpromotedClass.getLoosePromotionOptions(false, promoOptions.allowMountChanges, promoOptions.allowEnemyOnlyPromotedClasses, seliphSlot1, seliphSlot2, null);
							promotionMapper.setPromotionForCharacter(FE4Data.Character.SELIPH, promotedPool[rng.nextInt(promotedPool.length)]);
						}
					}
				}
			}
		}
		
		// Correct STR/MAG on bosses if necessary.
		for (FE4StaticCharacter holyBoss : charData.getHolyBossCharacters()) {
			FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(holyBoss.getClassID());
			if (charClass == null) { continue; }
			boolean isSTRBased = charClass.primaryAttackIsStrength();
			boolean isMAGBased = charClass.primaryAttackIsMagic();
			
			List<FE4Data.HolyBlood> majorBlood = FE4Data.HolyBloodSlot1.slot1HolyBlood(holyBoss.getHolyBlood1Value()).stream().filter(blood -> (blood.isMajor() == true)).map(slot1 -> (slot1.bloodType())).collect(Collectors.toList());
			majorBlood.addAll(FE4Data.HolyBloodSlot2.slot2HolyBlood(holyBoss.getHolyBlood2Value()).stream().filter(blood -> (blood.isMajor() == true)).map(slot2 -> (slot2.bloodType())).collect(Collectors.toList()));
			majorBlood.addAll(FE4Data.HolyBloodSlot3.slot3HolyBlood(holyBoss.getHolyBlood3Value()).stream().filter(blood -> (blood.isMajor() == true)).map(slot3 -> (slot3.bloodType())).collect(Collectors.toList()));
			
			if (majorBlood.isEmpty()) {			
				if ((isSTRBased && !isMAGBased && holyBoss.getBaseSTR() < holyBoss.getBaseMAG()) ||
						(isMAGBased && !isSTRBased && holyBoss.getBaseMAG() < holyBoss.getBaseSTR())) {
					int oldSTR = holyBoss.getBaseSTR();
					holyBoss.setBaseSTR(holyBoss.getBaseMAG());
					holyBoss.setBaseMAG(oldSTR);
				}
			} else {
				FE4Data.HolyBlood majorBloodType = majorBlood.get(0);
				if ((majorBloodType.holyWeapon.getType().isPhysical() && holyBoss.getBaseSTR() < holyBoss.getBaseMAG()) ||
						(majorBloodType.holyWeapon.getType().isPhysical() == false & holyBoss.getBaseMAG() < holyBoss.getBaseSTR())) {
					int oldSTR = holyBoss.getBaseSTR();
					holyBoss.setBaseSTR(holyBoss.getBaseMAG());
					holyBoss.setBaseMAG(oldSTR);
				}
			}
		}
	}
	
	public RecordKeeper initializeRecordKeeper() {
		int index = Math.max(targetPath.lastIndexOf('/'), targetPath.lastIndexOf('\\'));
		String title =  targetPath.substring(index + 1);
		String gameTitle = FE4Data.FriendlyName;
		
		RecordKeeper rk = new RecordKeeper(title);
		
		rk.addHeaderItem("Game Title", gameTitle);
		
		if (growthOptions != null) {
			switch (growthOptions.mode) {
			case REDISTRIBUTE:
				rk.addHeaderItem("Randomize Growths", "Redistribution (" + growthOptions.redistributionOption.variance + "% variance)");
				break;
			case DELTA:
				rk.addHeaderItem("Randomize Growths", "Delta (+/- " + growthOptions.deltaOption.variance + "%)");
				break;
			case FULL:
				rk.addHeaderItem("Randomize Growths", "Full (" + growthOptions.fullOption.minValue + "% ~ " + growthOptions.fullOption.maxValue + "%)");
				break;
			}
			
			rk.addHeaderItem("Adjust HP Growths", growthOptions.adjustHP ? "YES" : "NO");
			rk.addHeaderItem("Adjust STR/MAG Growths by Class", growthOptions.adjustSTRMAGSplit ? "YES" : "NO");
		} else {
			rk.addHeaderItem("Randomize Growths", "NO");
		}
		
		if (basesOptions != null) {
			switch (basesOptions.mode) {
			case REDISTRIBUTE:
				rk.addHeaderItem("Randomize Bases", "Redistribution (" + basesOptions.redistributionOption.variance + " variance)");
				break;
			case DELTA:
				rk.addHeaderItem("Randomize Bases", "Delta (+/- " + basesOptions.deltaOption.variance + ")");
				break;
			}
			
			rk.addHeaderItem("Adjust STR/MAG bases by Class", basesOptions.adjustSTRMAGByClass ? "YES" : "NO");
		} else {
			rk.addHeaderItem("Randomize Bases", "NO");
		}
		
		if (bloodOptions != null) {
			rk.addHeaderItem("Randomize Holy Blood Growth Bonuses", bloodOptions.randomizeGrowthBonuses ? "YES (Growth Total: " + bloodOptions.growthTotal + ")" : "NO");
			rk.addHeaderItem("Randomize Holy Weapon Bonuses", bloodOptions.randomizeWeaponBonuses ? "YES" : "NO");
			if (bloodOptions.giveHolyBlood) {
				rk.addHeaderItem("Assign Holy Blood", "YES");
				rk.addHeaderItem("Match Holy Blood to Class", bloodOptions.matchClass ? "YES" : "NO");
				rk.addHeaderItem("Holy Blood Distribution", String.format("%d%% Major, %d%% Minor, %d%% None", bloodOptions.majorBloodChance, bloodOptions.minorBloodChance, 100 - bloodOptions.majorBloodChance - bloodOptions.minorBloodChance));
			} else {
				rk.addHeaderItem("Assign Holy Blood", "NO");
			}
		} else {
			rk.addHeaderItem("Randomize Holy Blood", "NO");
		}
		
		if (skillsOptions != null) {
			rk.addHeaderItem("Retain Number of Skills", skillsOptions.retainNumberOfSkills ? "YES" : "NO");
			switch (skillsOptions.mode) {
			case SHUFFLE:
				rk.addHeaderItem("Randomize Skills", "Shuffle");
				rk.addHeaderItem("Separate Pools by Generation", skillsOptions.separatePoolsByGeneration ? "YES" : "NO");
				break;
			case RANDOMIZE:
				rk.addHeaderItem("Randomize Skills", "Randomize");
				if (!skillsOptions.retainNumberOfSkills) {
					WeightedDistributor<Integer> skillCountDistributor = FE4SkillsRandomizer.skillCountDistributionFromOptions(skillsOptions);
					rk.addHeaderItem("Zero Skills Weight", skillsOptions.skillCounts.zeroSkillsChance.enabled ? skillsOptions.skillCounts.zeroSkillsChance.weight.toString() + String.format(" (%.2f%%)", skillCountDistributor.chanceOfResult(0) * 100) : "Disabled");
					rk.addHeaderItem("One Skill Weight", skillsOptions.skillCounts.oneSkillChance.enabled ? skillsOptions.skillCounts.oneSkillChance.weight.toString() + String.format(" (%.2f%%)", skillCountDistributor.chanceOfResult(1) * 100) : "Disabled");
					rk.addHeaderItem("Two Skills Weight", skillsOptions.skillCounts.twoSkillChance.enabled ? skillsOptions.skillCounts.twoSkillChance.weight.toString() + String.format(" (%.2f%%)", skillCountDistributor.chanceOfResult(2) * 100) : "Disabled");
					rk.addHeaderItem("Three Skills Weight", skillsOptions.skillCounts.threeSkillChance.enabled ? skillsOptions.skillCounts.threeSkillChance.weight.toString() + String.format(" (%.2f%%)", skillCountDistributor.chanceOfResult(3) * 100) : "Disabled");
				}
				
				WeightedDistributor<FE4Data.Skill> skillDistributor = FE4SkillsRandomizer.skillDistributionFromOptions(skillsOptions);
				rk.addHeaderItem("Adept Weight", skillsOptions.skillWeights.adeptWeight.enabled ? skillsOptions.skillWeights.adeptWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.ADEPT) * 100) : "Disabled");
				rk.addHeaderItem("Astra Weight", skillsOptions.skillWeights.astraWeight.enabled ? skillsOptions.skillWeights.astraWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.ASTRA) * 100) : "Disabled");
				rk.addHeaderItem("Bargain Weight", skillsOptions.skillWeights.bargainWeight.enabled ? skillsOptions.skillWeights.bargainWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.BARGAIN) * 100) : "Disabled");
				rk.addHeaderItem("Charge Weight", skillsOptions.skillWeights.chargeWeight.enabled ? skillsOptions.skillWeights.chargeWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.CHARGE) * 100) : "Disabled");
				rk.addHeaderItem("Charm Weight", skillsOptions.skillWeights.charmWeight.enabled ? skillsOptions.skillWeights.charmWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.CHARM) * 100) : "Disabled");
				rk.addHeaderItem("Critical Weight", skillsOptions.skillWeights.criticalWeight.enabled ? skillsOptions.skillWeights.criticalWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.CRITICAL) * 100) : "Disabled");
				rk.addHeaderItem("Luna Weight", skillsOptions.skillWeights.lunaWeight.enabled ? skillsOptions.skillWeights.lunaWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.LUNA) * 100) : "Disabled");
				rk.addHeaderItem("Miracle Weight", skillsOptions.skillWeights.miracleWeight.enabled ? skillsOptions.skillWeights.miracleWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.MIRACLE) * 100) : "Disabled");
				rk.addHeaderItem("Nihil Weight", skillsOptions.skillWeights.nihilWeight.enabled ? skillsOptions.skillWeights.nihilWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.NIHIL) * 100) : "Disabled");
				rk.addHeaderItem("Paragon Weight", skillsOptions.skillWeights.paragonWeight.enabled ? skillsOptions.skillWeights.paragonWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.PARAGON) * 100) : "Disabled");
				rk.addHeaderItem("Renewal Weight", skillsOptions.skillWeights.renewalWeight.enabled ? skillsOptions.skillWeights.renewalWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.RENEWAL) * 100) : "Disabled");
				rk.addHeaderItem("Sol Weight", skillsOptions.skillWeights.solWeight.enabled ? skillsOptions.skillWeights.solWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.SOL) * 100) : "Disabled");
				rk.addHeaderItem("Vantage Weight", skillsOptions.skillWeights.vantageWeight.enabled ? skillsOptions.skillWeights.vantageWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.VANTAGE) * 100) : "Disabled");
				rk.addHeaderItem("Wrath Weight", skillsOptions.skillWeights.wrathWeight.enabled ? skillsOptions.skillWeights.wrathWeight.weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(FE4Data.Skill.WRATH) * 100) : "Disabled");
				
				rk.addHeaderItem("Pursuit Chance", String.format(" %d%%", skillsOptions.skillWeights.pursuitChance));
			}
			
		} else {
			rk.addHeaderItem("Randomize Skills", "NO");
		}
		
		if (classOptions != null) {
			if (classOptions.randomizePlayableCharacters) {
				rk.addHeaderItem("Randomize Playable Classes", "YES");
				rk.addHeaderItem("Include Lords", classOptions.includeLords ? "YES" : "NO");
				rk.addHeaderItem("Include Thieves", classOptions.includeThieves ? "YES" : "NO");
				rk.addHeaderItem("Include Dancers", classOptions.includeDancers ? "YES" : "NO");
				rk.addHeaderItem("Retain Healers", classOptions.retainHealers ? "YES" : "NO");
				
				switch (classOptions.childOption) {
				case MATCH_STRICT:
					rk.addHeaderItem("Child Randomization", "Match Parent (Strict)");
					break;
				case MATCH_LOOSE:
					rk.addHeaderItem("Child Randomization", "Match Parent (Loose)");
					break;
				case RANDOM_CLASS:
					rk.addHeaderItem("Child Randomization", "Randomize");
					break;
				}
				
				rk.addHeaderItem("Randomize Holy Blood", classOptions.randomizeBlood ? "YES" : "NO");
				switch (classOptions.shopOption) {
				case DO_NOT_ADJUST:
					rk.addHeaderItem("Shop Items", "No Change");
					break;
				case ADJUST_TO_MATCH:
					rk.addHeaderItem("Shop Items", "Adjust to Party");
					break;
				case RANDOMIZE:
					rk.addHeaderItem("Shop Items", "Randomize");
					break;
				}
				
				rk.addHeaderItem("Adjust Conversation Gifts", classOptions.adjustConversationWeapons ? "YES" : "NO");
				rk.addHeaderItem("Adjust STR/MAG Growths and Bases", classOptions.adjustSTRMAG ? "YES" : "NO");
			} else {
				rk.addHeaderItem("Randomize Playable Classes", "NO");
			}
			
			rk.addHeaderItem("Randomize Minions", classOptions.randomizeMinions ? "YES" : "NO");
			rk.addHeaderItem("Randomize Arena", classOptions.randomizeArena ? "YES" : "NO");
			
			if (classOptions.randomizeBosses) {
				rk.addHeaderItem("Randomize Bosses", "YES");
				rk.addHeaderItem("Randomize Boss Holy Blood", classOptions.randomizeBossBlood ? "YES" : "NO");
			} else {
				rk.addHeaderItem("Randomize Bosses", "NO");
			}
		} else {
			rk.addHeaderItem("Randomize Classes", "NO");
		}
		
		if (promoOptions != null) {
			switch (promoOptions.promotionMode) {
			case STRICT:
				rk.addHeaderItem("Promotion Assignment", "Default");
				break;
			case LOOSE:
				rk.addHeaderItem("Promotion Assignment", "Similar");
				rk.addHeaderItem("Allow Mount Change", promoOptions.allowMountChanges ? "YES" : "NO");
				rk.addHeaderItem("Allow Enemy-only Classes", promoOptions.allowEnemyOnlyPromotedClasses ? "YES" : "NO");
				break;
			case RANDOM:
				rk.addHeaderItem("Promotion Assignment", "Random");
				rk.addHeaderItem("Require Common Weapons", promoOptions.requireCommonWeapon ? "YES" : "NO");
			}
		}
		
		if (miscOptions != null) {
			rk.addHeaderItem("Apply English Patch", miscOptions.applyEnglishPatch ? "YES" : "NO");
			rk.addHeaderItem("Randomize Rings", miscOptions.randomizeRewards ? "YES" : "NO");
		}
		
		charData.recordCharacters(rk, true, itemMapper);
		bloodData.recordHolyBlood(rk, true);
		itemMapper.recordItemMap(rk, true);
		promotionMapper.recordPromotions(rk, true);
		classData.recordClasses(rk, true);
		
		return rk;
	}
}
