package random.gcnwii.fe9.randomizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterRewards;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Item;
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
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponRank;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;
import random.general.Randomizer;
import random.general.WeightedDistributor;
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
import util.recordkeeper.ChangelogAsset;
import util.recordkeeper.ChangelogBuilder;
import util.recordkeeper.ChangelogDivider;
import util.recordkeeper.ChangelogHeader;
import util.recordkeeper.ChangelogSection;
import util.recordkeeper.ChangelogStyleRule;
import util.recordkeeper.ChangelogTOC;
import util.recordkeeper.ChangelogTable;
import util.recordkeeper.ChangelogHeader.HeaderLevel;

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
		
		ChangelogBuilder changelogBuilder = new ChangelogBuilder();
		changelogBuilder.addElement(new ChangelogDivider());
		
		addRandomizationOptionsToChangelog(changelogBuilder, seed);
		
		changelogBuilder.addElement(new ChangelogDivider());
		
		ChangelogTOC mainTOC = new ChangelogTOC("main-toc");
		changelogBuilder.addElement(mainTOC);
		
		changelogBuilder.addElement(new ChangelogDivider());
		
		ChangelogSection characterSection = new ChangelogSection("character-data");
		mainTOC.addAnchorWithTitle("character-data", "Character Data");
		changelogBuilder.addElement(characterSection);
		
		ChangelogSection itemSection = new ChangelogSection("item-data");
		mainTOC.addAnchorWithTitle("item-data", "Item Data");
		changelogBuilder.addElement(itemSection);
		
		ChangelogSection chapterSection = new ChangelogSection("chapter-data");
		mainTOC.addAnchorWithTitle("chapter-data", "Chapter Data");
		changelogBuilder.addElement(chapterSection);
		
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
			
			charData.recordOriginalCharacterData(changelogBuilder, characterSection, textData, classData, skillData, itemData);
			itemData.recordOriginalItemData(changelogBuilder, itemSection, textData);
			chapterData.recordOriginalChapterData(changelogBuilder, chapterSection, textData, charData, classData, skillData, itemData);
			
			makePreRandomizationAdjustments();
			
			randomizeClassesIfNecessary(seed);
			randomizeGrowthsIfNecessary(seed);
			randomizeBasesIfNecessary(seed);
			randomizeSkillsIfNecessary(seed);
			randomizeOtherCharacterOptionsIfNecessary(seed);
			randomizeMiscellaneousIfNecessary(seed);
			buffEnemiesIfNecessary(seed);
			
			makePostRandomizationAdjustments();
			
			updateStatus(0.50, "Committing changes...");
			charData.compileDiffs(handler);
			itemData.compileDiffs(handler);
			classData.compileDiffs(handler);
			
			charData.recordUpdatedCharacterData(characterSection, textData, classData, skillData, itemData);
			itemData.recordUpdatedItemData(itemSection, textData);
			chapterData.recordUpdatedChapterData(chapterSection, textData, charData, classData, skillData, itemData);
			
			ChangelogAsset.registerAssets(changelogBuilder);
			
		} catch (GCNISOException e1) {
			notifyError("Failed to load data from the ISO.\n\n" + e1.getMessage());
			return;
		} catch (Exception e) {
			notifyError(e.getClass().getSimpleName() + "\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); 
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
		
		notifyCompletion(null, changelogBuilder);
	}
	
	private void makePreRandomizationAdjustments() {
		// Remove KEY0 and KEY50 from Sothe and Volke, respectively, if randomize classes is enabled and thieves are also enabled.
		if (classOptions.randomizePCs && classOptions.includeThieves) {
			FE9Character sothe = charData.characterWithID(FE9Data.Character.SOTHE.getPID());
			//FE9Character volke = charData.characterWithID(FE9Data.Character.VOLKE.getPID());
		
			sothe.setSkill2Pointer(0);
			//volke.setSkill2Pointer(0); // Maybe it would be interesting to allow Volke to always open chests for 50G a pop.
			
			// The thief class actually already has too many skills to fit another in its class data. We'll have to assign these manually
			// in the chapter unit data.
		}
	}
	
	private void makePostRandomizationAdjustments() {
		// Remove damage immunity from Ch. 27 BK and Ashnard.
		// Unfortunately, this isn't a skill that is on the characters.
		// The only thing we know is that weapons with the trait 'weakA' seem to be capable of bypassing this.
		// So outside of removing what is giving them damage immunity, we allow some weapons to bypass it.
		List<FE9Item> bypassBlessedArmorWeaponList = new ArrayList<FE9Item>();
		bypassBlessedArmorWeaponList.addAll(itemData.allWeaponsOfRank(WeaponRank.S));
		bypassBlessedArmorWeaponList.addAll(itemData.allWeaponsOfRank(WeaponRank.A));
		for (FE9Item weapon : bypassBlessedArmorWeaponList) {
			List<String> traits = new ArrayList<String>(Arrays.asList(itemData.getItemTraits(weapon)));
			if (traits.contains(FE9Data.Item.WeaponTraits.BYPASS_BLESSED_ARMOR.getTraitString())) {
				continue;
			}
			for (int i = 0; i < traits.size(); i++) {
				String trait = traits.get(i);
				if (trait == null || trait.length() == 0) {
					traits.remove(i);
					traits.add(i, FE9Data.Item.WeaponTraits.BYPASS_BLESSED_ARMOR.getTraitString());
					break;
				}
			}
			String[] traitsArray = traits.toArray(new String[traits.size()]);
			itemData.setItemTraits(weapon, traitsArray);
		}
		
		// Update Regal Sword and Ragnell's weapon lock.
		// The lock normally is tied directly to the protagonist by character, which means if Ike doesn't use swords
		// those two weapons are useless. Change it to the class instead.
		// Thankfully, we can cheat by recycling Rolf's lock, which is locked to a skill.
		// FE9 does the same check as GBA where the type of weapon still matters even if the lock is for you.
		FE9Item regalSword = itemData.itemWithIID(FE9Data.Item.REGAL_SWORD.getIID());
		String[] weaponTraits = itemData.getItemTraits(regalSword);
		// The lock is in the first slot.
		weaponTraits[0] = FE9Data.Item.WeaponTraits.ROLF_LOCK.getTraitString();
		itemData.setItemTraits(regalSword, weaponTraits);
		
		FE9Item ragnell = itemData.itemWithIID(FE9Data.Item.RAGNELL.getIID());
		weaponTraits = itemData.getItemTraits(ragnell);
		// Also in the first slot.
		weaponTraits[0] = FE9Data.Item.WeaponTraits.ROLF_LOCK.getTraitString();
		itemData.setItemTraits(ragnell, weaponTraits);
		
		FE9Class ranger = classData.classWithID(FE9Data.CharacterClass.RANGER.getJID());
		FE9Class lord = classData.classWithID(FE9Data.CharacterClass.LORD.getJID());
		
		// Give Ranger and Lord the Rolf Lock skill.
		// Slot 1 is actually the same slot as the skill that forces Ike's promotion in Ch. 17, so this kills two birds with one stone.
		classData.setSID1ForClass(ranger, FE9Data.Skill.EQUIP_A.getSID());
		// Slot 1 for lord is SID_HIGHER, so we'll put this in slot 2.
		classData.setSID2ForClass(lord, FE9Data.Skill.EQUIP_A.getSID());
		
		// Update the promotion lock on the Ranger class to only be for Ike and not the class.
		// Ranger has a special skill that prevents it from promoting naturally.
		// Remove it from Ranger (so anybody else that becomes a Ranger can promote)
		// and add it specifically to Ike (so Ike will always promote in the same chapter no matter his class).
		// We already removed it from Ranger in the above fix for Regal Sword and Ragnell, so we just need to give it to Ike.
		FE9Character ike = charData.characterWithID(FE9Data.Character.IKE.getPID());
		// His first slot is always SID_HERO, which gives him seizing capabilities among other things.
		if (charData.getSID2ForCharacter(ike) == null) {
			charData.setSID2ForCharacter(ike, FE9Data.Skill.EVENT_CC.getSID());
		} else if (charData.getSID3ForCharacter(ike) == null) {
			charData.setSID3ForCharacter(ike, FE9Data.Skill.EVENT_CC.getSID());
		} else {
			// Try giving it to him in the prologue script.
			List<FE9ChapterArmy> prologueArmies = chapterData.armiesForChapter(FE9Data.Chapter.PROLOGUE);
			for (FE9ChapterArmy army : prologueArmies) {
				for (String unitID : army.getAllUnitIDs()) {
					FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
					if (army.getPIDForUnit(unit).equals(FE9Data.Character.IKE.getPID())) {
						if (army.getSkill1ForUnit(unit) == null) {
							army.setSkill1ForUnit(unit, FE9Data.Skill.EVENT_CC.getSID());
						} else if (army.getSkill2ForUnit(unit) == null) {
							army.setSkill2ForUnit(unit, FE9Data.Skill.EVENT_CC.getSID());
						} else {
							// We have nowhere else to put this. Even if slot 3 is used, force it here.
							army.setSkill3ForUnit(unit, FE9Data.Skill.EVENT_CC.getSID());
						}
					}
				}
				army.commitChanges();
			}
		}
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
				FE9GrowthRandomizer.randomizeGrowthsFully(growthOptions.fullOption.minValue, growthOptions.fullOption.maxValue, growthOptions.adjustHP, growthOptions.adjustSTRMAGSplit, charData, classData, rng);
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
	
	private void addRandomizationOptionsToChangelog(ChangelogBuilder changelogBuilder, String seed) {
		// Randomization options.
		ChangelogSection optionsSection = new ChangelogSection("options-section");
		optionsSection.addElement(new ChangelogHeader(HeaderLevel.HEADING_2, "Randomization Options", "options-section-header"));
		
		ChangelogTable table = new ChangelogTable(2, null, "options-table");
		table.addRow(new String[] {"Game Title", FE9Data.FriendlyName});
		if (growthOptions != null) {
			switch (growthOptions.mode) {
			case REDISTRIBUTE:
				table.addRow(new String[] {"Randomize Growths", "Redistribute (Variance: " + growthOptions.redistributionOption.variance + "%)"});
				break;
			case FULL:
				table.addRow(new String[] {"Randomize Growths", "Full (" + growthOptions.fullOption.minValue + "% ~ " + growthOptions.fullOption.maxValue + "%)"});
				break;
			case DELTA:
				table.addRow(new String[] {"Randomize Growths", "Delta (+/- " + growthOptions.deltaOption.variance + "%)"});
				break;
			}
			table.addRow(new String[] {"Adjust HP Growths", growthOptions.adjustHP ? "YES" : "NO"});
			table.addRow(new String[] {"Adjust STR/MAG Split", growthOptions.adjustSTRMAGSplit ? "YES" : "NO"});
		} else {
			table.addRow(new String[] {"Randomize Growths", "NO"});
		}
		if (baseOptions != null) {
			switch (baseOptions.mode) {
			case REDISTRIBUTE:
				table.addRow(new String[] {"Randomize Bases", "Redistribute (Variance: " + baseOptions.redistributionOption.variance + ")"});
				break;
			case DELTA:
				table.addRow(new String[] {"Randomize Bases", "Delta (Variance: " + baseOptions.deltaOption.variance + ")"});
				break;
			}
			table.addRow(new String[] {"Adjust STR/MAG Split", baseOptions.adjustSTRMAGByClass ? "YES" : "NO"});
		} else {
			table.addRow(new String[] {"Randomize Bases", "NO"});
		}
		if (otherCharOptions != null) {
			table.addRow(new String[] {"Randomize CON", otherCharOptions.randomizeCON ? "YES (Variance: " + otherCharOptions.conVariance + ")" : "NO"});
			table.addRow(new String[] {"Randomize Affinity", otherCharOptions.randomizeAffinity ? "YES" : "NO"});
		}
		if (skillOptions != null) {
			switch (skillOptions.mode) {
			case RANDOMIZE_EXISTING:
				table.addRow(new String[] {"Randomize Skills", "Randomize Existing Skills"});
				break;
			case FULL_RANDOM:
				table.addRow(new String[] {"Randomize Skills", "Fully Randomize Skills (Skill Chance: " + skillOptions.skillChance + "%)"});
				break;
			}
			WeightedDistributor<String> skillDistributor = FE9SkillRandomizer.weightedDistributorForOptions(skillOptions.skillWeights);
			List<String> skillNames = skillOptions.skillWeights.getSkillNames().stream().sorted(new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					return arg0.compareTo(arg1);
				}
			}).collect(Collectors.toList());
			for (String skillName : skillNames) {
				if (skillOptions.skillWeights.getWeightedOptionsByName(skillName).enabled) {
					table.addRow(new String[] {skillName + " chance", skillOptions.skillWeights.getWeightedOptionsByName(skillName).weight.toString() + String.format(" (%.2f%%)", skillDistributor.chanceOfResult(skillName) * 100)});
				} else {
					table.addRow(new String[] {skillName + " chance", "Disabled"});
				}
			}
		} else {
			table.addRow(new String[] {"Randomize Skills", "NO"});
		}
		if (classOptions != null) {
			if (classOptions.randomizePCs) {
				table.addRow(new String[] {"Randomize Playable Characters", "YES"});
				table.addRow(new String[] {"Include Lords", classOptions.includeLords ? "YES" : "NO"});
				table.addRow(new String[] {"Include Thieves", classOptions.includeThieves ? "YES" : "NO"});
				table.addRow(new String[] {"Include Special", classOptions.includeSpecial ? "YES" : "NO"});
				table.addRow(new String[] {"Mix Races for Playable Characters", classOptions.mixPCRaces ? "YES" : "NO"});
				table.addRow(new String[] {"Allow Cross-gender Assignments", classOptions.allowCrossgender ? "YES" : "NO"});
			} else {
				table.addRow(new String[] {"Randomize Playable Characters", "NO"});
			}
			if (classOptions.randomizeBosses) {
				table.addRow(new String[] {"Randomize Bosses", "YES"});
				table.addRow(new String[] {"Mix Races for Bosses", classOptions.mixBossRaces ? "YES" : "NO"});
			} else {
				table.addRow(new String[] {"Randomize Bosses", "NO"});
			}
			if (classOptions.randomizeMinions) {
				table.addRow(new String[] {"Randomize Minions", "YES (" + classOptions.minionRandomChance + "%)"});
				table.addRow(new String[] {"Mix Races for Minions", classOptions.mixMinionRaces ? "YES" : "NO"});
			} else {
				table.addRow(new String[] {"Randomize Minions", "NO"});
			}
			if (classOptions.randomizePCs || classOptions.randomizeBosses || classOptions.randomizeMinions) {
				table.addRow(new String[] {"Force Class Change", classOptions.forceDifferent ? "YES" : "NO"});
			}
		}
		if (enemyBuffOptions != null) {
			switch (enemyBuffOptions.minionMode) {
			case NONE:
				table.addRow(new String[] {"Buff Minions", "NO"});
				break;
			case FLAT:
				table.addRow(new String[] {"Buff Minions", "Flat Buff (+" + enemyBuffOptions.minionBuff + "%)"});
				break;
			case SCALING:
				table.addRow(new String[] {"Buff Minions", "Scaling Buff (+" + enemyBuffOptions.minionBuff + "%)"});
				break;
			}
			table.addRow(new String[] {"Improve Minion Weapons", enemyBuffOptions.improveMinionWeapons ? "YES (" + enemyBuffOptions.minionImprovementChance + "%)" : "NO"});
			table.addRow(new String[] {"Give Minions Skills", enemyBuffOptions.giveMinionsSkills ? "YES (" + enemyBuffOptions.minionSkillChance + "%)" : "NO"});
			
			switch (enemyBuffOptions.bossMode) {
			case NONE:
				table.addRow(new String[] {"Buff Bosses", "NO"});
				break;
			case LINEAR:
				table.addRow(new String[] {"Buff Bosses", "YES (Max Boost: " + enemyBuffOptions.bossBuff + " - Linear)"});
				break;
			case EASE_IN_OUT:
				table.addRow(new String[] {"Buff Bosses", "YES (Max Boost: " + enemyBuffOptions.bossBuff + " - Ease In/Ease Out);"});
				break;
			}
			table.addRow(new String[] {"Improve Boss Weapons", enemyBuffOptions.improveBossWeapons ? "YES (" + enemyBuffOptions.bossImprovementChance + "%)" : "NO"});
			table.addRow(new String[] {"Give Bosses Skills", enemyBuffOptions.giveBossSkills ? "YES (" + enemyBuffOptions.bossSkillChance + "%)" : "NO"});
		}
		if (miscOptions != null) {
			if (miscOptions.randomizeRewards) {
				switch(miscOptions.rewardMode) {
				case SIMILAR:
					table.addRow(new String[] {"Randomize Rewards", "Randomize with Similar Items"});
					break;
				case RANDOM:
					table.addRow(new String[] {"Randomize Rewards", "Randomize with Random Items"});
					break;
				}
			} else {
				table.addRow(new String[] {"Randomize Rewards", "NO"});
			}
		}
		
		table.addRow(new String[] {"Randomizer Seed Phrase", seed});
		
		optionsSection.addElement(table);
		changelogBuilder.addElement(optionsSection);
		
		ChangelogStyleRule rule = new ChangelogStyleRule();
		rule.setElementIdentifier("options-section-header");
		rule.addRule("text-align", "center");
		changelogBuilder.addStyle(rule);
		
		rule = new ChangelogStyleRule();
		rule.setOverrideSelectorString("#options-table tr:nth-child(even)");
		rule.addRule("background-color", "#DDD");
		changelogBuilder.addStyle(rule);
		
		rule = new ChangelogStyleRule();
		rule.setElementIdentifier("options-table");
		rule.addRule("width", "75%");
		rule.addRule("margin-left", "auto");
		rule.addRule("margin-right", "auto");
		changelogBuilder.addStyle(rule);
	}
}
