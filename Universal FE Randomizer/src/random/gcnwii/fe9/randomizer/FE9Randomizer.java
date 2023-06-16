package random.gcnwii.fe9.randomizer;

import fedata.gcnwii.fe9.*;
import fedata.gcnwii.fe9.scripting.*;
import io.FileHandler;
import io.gcn.GCNCMBFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import io.gcn.GCNISOHandlerRecompilationDelegate;
import random.gcnwii.fe9.loader.*;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponRank;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponType;
import random.general.Randomizer;
import random.general.WeightedDistributor;
import ui.model.fe9.FE9ClassOptions;
import ui.model.fe9.FE9SkillsOptions;
import ui.model.*;
import ui.model.fe9.FE9EnemyBuffOptions;
import ui.model.fe9.FE9OtherCharacterOptions;
import util.SeedGenerator;
import util.recordkeeper.*;
import util.recordkeeper.ChangelogHeader.HeaderLevel;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FE9Randomizer extends Randomizer {
	private String sourcePath;
	private String targetPath;
	
	private String seedString;
	
	private GCNISOHandler handler;
	
	private GrowthOptions growthOptions;
	private BaseOptions baseOptions;
	private FE9SkillsOptions skillOptions;
	private GameMechanicOptions miscOptions;
	private RewardOptions rewardOptions;
	private FE9OtherCharacterOptions otherCharOptions;
	private FE9EnemyBuffOptions enemyBuffOptions;
	private FE9ClassOptions classOptions;
	private WeaponOptions weaponOptions;
	
	FE9CommonTextLoader textData;
	FE9CharacterDataLoader charData;
	FE9ClassDataLoader classData;
	FE9ItemDataLoader itemData;
	FE9SkillDataLoader skillData;
	FE9ChapterDataLoader chapterData;
	
	public FE9Randomizer(String sourcePath, String targetPath, GrowthOptions growthOptions, BaseOptions baseOptions, FE9SkillsOptions skillOptions, FE9OtherCharacterOptions otherCharOptions, FE9EnemyBuffOptions enemyBuffOptions, FE9ClassOptions classOptions, WeaponOptions weaponOptions, GameMechanicOptions miscOptions, RewardOptions rewardOptions, String seed) {
		super();
		
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		
		this.growthOptions = growthOptions;
		this.baseOptions = baseOptions;
		this.skillOptions = skillOptions;
		this.otherCharOptions = otherCharOptions;
		this.enemyBuffOptions = enemyBuffOptions;
		this.classOptions = classOptions;
		this.weaponOptions = weaponOptions;
		this.miscOptions = miscOptions;
		this.rewardOptions = rewardOptions;

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
			return;
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
		
		ChangelogSection classSection = new ChangelogSection("class-data");
		mainTOC.addAnchorWithTitle("class-data", "Class Data");
		changelogBuilder.addElement(classSection);
		
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
			
			charData.recordOriginalCharacterData(changelogBuilder, characterSection, textData, classData, skillData, itemData, chapterData);
			itemData.recordOriginalItemData(changelogBuilder, itemSection, textData);
			classData.recordOriginalClassData(changelogBuilder, classSection, textData);
			chapterData.recordOriginalChapterData(changelogBuilder, chapterSection, textData, charData, classData, skillData, itemData);
			
			makePreRandomizationAdjustments();
			
			randomizeClassesIfNecessary(seed);
			randomizeGrowthsIfNecessary(seed);
			randomizeBasesIfNecessary(seed);
			randomizeSkillsIfNecessary(seed);
			randomizeOtherCharacterOptionsIfNecessary(seed);
			randomizeMiscellaneousIfNecessary(seed);
			buffEnemiesIfNecessary(seed);
			randomizeWeaponsIfNecessary(seed);
			
			makePostRandomizationAdjustments(seed);
			
			updateStatus(0.50, "Committing changes...");
			charData.compileDiffs(handler);
			itemData.compileDiffs(handler);
			classData.compileDiffs(handler);
			skillData.compileDiffs(handler);
			
			charData.recordUpdatedCharacterData(characterSection, textData, classData, skillData, itemData, chapterData);
			itemData.recordUpdatedItemData(itemSection, textData);
			classData.recordUpdatedClassData(classSection, textData);
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

			@Override
			public void onError(String errorMessage) {
				notifyError(errorMessage);
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
		
		// FE9 routinely uses chapter unit data to modify boss units stats, which isn't accounted for in the
		// normal logic flow. To alleviate this, we want to re-allocate those points appropriately
		// to make sure the rest of the logic works. Effectively, we want to move those adjustments
		// to the character data and only use the dispos adjustments for, say, hard mode adjustments.
		for (FE9Character boss : charData.allBossCharacters()) {
			String pid = charData.getPIDForCharacter(boss);
			List<FE9ChapterUnit> bossUnits = new ArrayList<FE9ChapterUnit>();
			for (FE9Data.Chapter chapter : FE9Data.Chapter.values()) {
				for (FE9ChapterArmy army : chapterData.armiesForChapter(chapter)) {
					FE9ChapterUnit bossUnit = army.getUnitForPID(pid);
					if (bossUnit != null) {
						bossUnits.add(bossUnit);
					}
					army.commitChanges();
				}
			}
			chapterData.commitChanges();
			
			if (!bossUnits.isEmpty()) {
				// Find the lowest offsets out of all of the boss units for each stat area.
				// We're going to use that as the baseline. All additional adjustments
				// will be higher than this value.
				int normalizedHPAdjustment = 255;
				int normalizedSTRAdjustment = 255;
				int normalizedMAGAdjustment = 255;
				int normalizedSKLAdjustment = 255;
				int normalizedSPDAdjustment = 255;
				int normalizedLCKAdjustment = 255;
				int normalizedDEFAdjustment = 255;
				int normalizedRESAdjustment = 255;
				
				for (FE9ChapterUnit bossUnit : bossUnits) {
					normalizedHPAdjustment = Math.min(normalizedHPAdjustment, bossUnit.getHPAdjustment());
					normalizedSTRAdjustment = Math.min(normalizedSTRAdjustment, bossUnit.getSTRAdjustment());
					normalizedMAGAdjustment = Math.min(normalizedMAGAdjustment, bossUnit.getMAGAdjustment());
					normalizedSKLAdjustment = Math.min(normalizedSKLAdjustment, bossUnit.getSKLAdjustment());
					normalizedSPDAdjustment = Math.min(normalizedSPDAdjustment, bossUnit.getSPDAdjustment());
					normalizedLCKAdjustment = Math.min(normalizedLCKAdjustment, bossUnit.getLCKAdjustment());
					normalizedDEFAdjustment = Math.min(normalizedDEFAdjustment, bossUnit.getDEFAdjustment());
					normalizedRESAdjustment = Math.min(normalizedRESAdjustment, bossUnit.getRESAdjustment());
				}
			
				for (FE9ChapterUnit bossUnit : bossUnits) {
					bossUnit.setHPAdjustment(bossUnit.getHPAdjustment() - normalizedHPAdjustment);
					bossUnit.setSTRAdjustment(bossUnit.getSTRAdjustment() - normalizedSTRAdjustment);
					bossUnit.setMAGAdjustment(bossUnit.getMAGAdjustment() - normalizedMAGAdjustment);
					bossUnit.setSKLAdjustment(bossUnit.getSKLAdjustment() - normalizedSKLAdjustment);
					bossUnit.setSPDAdjustment(bossUnit.getSPDAdjustment() - normalizedSPDAdjustment);
					bossUnit.setLCKAdjustment(bossUnit.getLCKAdjustment() - normalizedLCKAdjustment);
					bossUnit.setDEFAdjustment(bossUnit.getDEFAdjustment() - normalizedDEFAdjustment);
					bossUnit.setRESAdjustment(bossUnit.getRESAdjustment() - normalizedRESAdjustment);
				}
			
				// Apply the adjustment we took out of the dispos data and inject it back into the character data.
				boss.setBaseHP(boss.getBaseHP() + normalizedHPAdjustment);
				boss.setBaseSTR(boss.getBaseSTR() + normalizedSTRAdjustment);
				boss.setBaseMAG(boss.getBaseMAG() + normalizedMAGAdjustment);
				boss.setBaseSKL(boss.getBaseSKL() + normalizedSKLAdjustment);
				boss.setBaseSPD(boss.getBaseSPD() + normalizedSPDAdjustment);
				boss.setBaseLCK(boss.getBaseLCK() + normalizedLCKAdjustment);
				boss.setBaseDEF(boss.getBaseDEF() + normalizedDEFAdjustment);
				boss.setBaseRES(boss.getBaseRES() + normalizedRESAdjustment);
			}
		}
		
		// Give Warp a real name and description.
		textData.setStringForIdentifier("MIID_WARP", "Warp");
		textData.setStringForIdentifier("MH_I_WARP", "You could just play through the map properly...");
		
		// Give Paragon scroll a real name and allow it to be used with all characters.
		textData.setStringForIdentifier("MIID_ELITE", "Paragon");
		StringBuilder sb = new StringBuilder();
		sb.append("Doubles the experience");
		sb.append((char)0xA); // line break
		sb.append("points this unit gains.");
		sb.append((char)0xA); // line break
		sb.append((char)0x20); // spacing
		sb.append((char)0x20); // spacing
		sb.append("#C04All units");
		textData.setStringForIdentifier("Mess_Help2_skill_Elite", sb.toString());
		FE9Skill paragon = skillData.getSkillWithSID(FE9Data.Skill.PARAGON.getSID());
		paragon.setRestrictionCount(0);
		paragon.setRestrictionPointer(0);
		paragon.commitChanges();
		
		// Same for Blossom and Celerity.
		textData.setStringForIdentifier("MIID_FRAC90", "Blossom");
		sb = new StringBuilder();
		sb.append("An item that allows you to acquire the skill");
		sb.append((char)0xA);
		sb.append("Blossom when you're at a base.");
		textData.setStringForIdentifier("MH_I_FRAC90", sb.toString());
		FE9Skill blossom = skillData.getSkillWithSID(FE9Data.Skill.BLOSSOM.getSID());
		blossom.setRestrictionCount(0);
		blossom.setRestrictionPointer(0);
		textData.setStringForIdentifier("MIID_SWIFT", "Celerity");
		sb = new StringBuilder();
		sb.append("An item that allows you to acquire the skill");
		sb.append((char)0xA);
		sb.append("Celerity when you're at a base.");
		textData.setStringForIdentifier("MH_I_SWIFT", sb.toString());
		
		// Daunt scroll also doesn't have the correct name.
		textData.setStringForIdentifier("MIID_HORROR", "Daunt");
		
		// Balmer is incorrectly set as a Mage instead of a Sage in the character data. Adjust for this.
		FE9Character balmer = charData.characterWithID(FE9Data.Character.BALMER.getPID());
		charData.setJIDForCharacter(balmer, FE9Data.CharacterClass.SAGE_STAFF.getJID());
	}
	
	private void makePostRandomizationAdjustments(String seed) {
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
		
		// Only two classes in the game can "walk slowly" or at least have the animation for doing so: Ranger and Priest.
		// Telling these characters to walk slowly can mess up animations and may be the reason why
		// this doesn't work on real hardware. Disable this slow walk whenever it shows up.
		for (FE9Data.Chapter chapter : FE9Data.Chapter.values()) {
			GCNCMBFileHandler chapterScript = chapterData.getHandlerForScripts(chapter);
			if (chapterScript == null) { continue; }
			for (FE9ScriptScene scene : chapterScript.getScenes()) {
				List<ScriptInstruction> instructions = scene.getInstructions();
				boolean didChange = false;
				for (int i = 0; i < instructions.size(); i++) {
					ScriptInstruction instruction = instructions.get(i);
					if (instruction instanceof CallSceneByNameInstruction && ((CallSceneByNameInstruction)instruction).getSceneName().equals("UnitWalkSlow")) {
						// Get the previous instruction, which should be a PUSH_NUM_LITERAL_8 that pushes either 0 (to disable) or 1 (to enable).
						// Make sure it's always 0.
						ScriptInstruction previousInstruction = instructions.get(i - 1);
						if (previousInstruction instanceof PushLiteralNum8Instruction) {
							// Replace these instructions with NO-OPs.
							instructions.remove(i - 1); // Push Literal
							instructions.remove(i - 1); // Call UnitWalkSlow
							instructions.remove(i - 1); // Discard Top
							
							instructions.add(i - 1, new NOPInstruction());
							instructions.add(i - 1, new NOPInstruction());
							instructions.add(i - 1, new NOPInstruction());
							
							didChange = true;
						}
					}
				}
				if (didChange) {
					scene.setInstructions(instructions);
					//scene.commit();
				}
			}
		}
		
		// There are a few cases where characters are assumed to be Laguz and are asked to transform.
		// Doing so as a Beorc will result in them demoting from their promoted classes, so check the classes of the characters when this happens
		// to remove the transform as necessary.
		// Transform scripts usually load the character into one of the addresses, then recall the address, and then push either 0 to untransform
		// or 1 to transform. For example:
		//
		//		PUSH_VAR_REF_8 (0x2)
		//		PUSH_LITERAL_STRING_16 (PID_LAY)
		//		CALL_SCENE_NAME ("UnitGetByPID", 1)
		//		STORE_VAL_IN_REF
		//		PUSH_VAR_8 (0x2)
		//		PUSH_NUM_LITERAL_8 (0x1)
		//		CALL_SCENE_NAME ("UnitTransform", 2)
		//		DISCARD_TOP
		//
		// We really only need to wipe out the bottom half of that, starting when it tries to push the value stored in address 0x2.
		
		// Ranulf shows up in Ch. 7 post-battle to bail out the Greil Mercs (Script 35).
		// Lethe (2nd) and Mordecai (1st) show up at the end of Ch. 8 after defeating Kamura (Script 31). 
		// 		There's also a second scene where Mordecai transforms and untransforms (Script 32).
		// Ranulf shows up in the opening of Ch. 11 to lure away some guards (Script 24) and once more in the closing against BK (Script 53).
		// Seeker gets an auto-transform in Ch. 12 (InstantUnitTransform). This should also be removed if he's not a Laguz (Script 6).
		// 		There's another normal transform for him (Script 11).
		// Muarim has an auto-transform in Ch. 15 (Script 18), and a normal transform (Script 21).
		// Tibarn transforms in Ch. 17 (Script 62) and also tries to give him and make him equip a Laguz Band, which we should also probably get rid of if he's not a Laguz.
		// In the ending of Ch. 17 (Script 94), there's a few auto-transforms. They are, in order, Reyson, Tibarn, Ulki, and Janaff. (Interestingly, they all use address 5.)
		// Ch. 19 has Naesala (Script 78). Like Tibarn, he also tries to equip a Laguz band, though in this case, it doesn't give him one, only searches his inventory. If it's not there, it does nothing.
		// There's a few transforms in between here, but they're with the _EV versions, which we don't change, so we don't really care yet.
		// Ch. 28 features Tibarn again who is auto-transformed (Script 14). Like in Ch. 17, he is given a Laguz band and forced to equip it. 
		// 		He also transforms normally in Script 19 (but is still given the Laguz Band).
		
		FE9Character ranulf = charData.characterWithID(FE9Data.Character.RANULF.getPID());
		String ranulfJID = charData.getJIDForCharacter(ranulf);
		FE9Class ranulfClass = classData.classWithID(ranulfJID);
		
		FE9Character lethe = charData.characterWithID(FE9Data.Character.LETHE.getPID());
		String letheJID = charData.getJIDForCharacter(lethe);
		FE9Class letheClass = classData.classWithID(letheJID);
		
		FE9Character mordecai = charData.characterWithID(FE9Data.Character.MORDECAI.getPID());
		String mordecaiJID = charData.getJIDForCharacter(mordecai);
		FE9Class mordecaiClass = classData.classWithID(mordecaiJID);
		
		FE9Character seeker = charData.characterWithID(FE9Data.Character.SEEKER.getPID());
		String seekerJID = charData.getJIDForCharacter(seeker);
		FE9Class seekerClass = classData.classWithID(seekerJID);
		
		FE9Character muarim = charData.characterWithID(FE9Data.Character.MUARIM.getPID());
		String muarimJID = charData.getJIDForCharacter(muarim);
		FE9Class muarimClass = classData.classWithID(muarimJID);
		
		FE9Character janaff = charData.characterWithID(FE9Data.Character.JANAFF.getPID());
		String janaffJID = charData.getJIDForCharacter(janaff);
		FE9Class janaffClass = classData.classWithID(janaffJID);
		
		FE9Character ulki = charData.characterWithID(FE9Data.Character.ULKI.getPID());
		String ulkiJID = charData.getJIDForCharacter(ulki);
		FE9Class ulkiClass = classData.classWithID(ulkiJID);
		
		FE9Character reyson = charData.characterWithID(FE9Data.Character.REYSON.getPID());
		String reysonJID = charData.getJIDForCharacter(reyson);
		FE9Class reysonClass = classData.classWithID(reysonJID);
		
		if (classOptions.randomizePCs && classOptions.mixPCRaces) {
			if (!classData.isLaguzClass(ranulfClass)) { // Ch. 7 and Ch. 11
				GCNCMBFileHandler ch7Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_7);
				FE9ScriptScene scene = ch7Script.getSceneWithIndex(35);
				removeTransformFromScript(scene, 1, false);
				
				GCNCMBFileHandler ch11Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_11);
				scene = ch11Script.getSceneWithIndex(24);
				removeTransformFromScript(scene, 1, false);
				
				scene = ch11Script.getSceneWithIndex(53);
				removeTransformFromScript(scene, 1, false);
			} else {
				// That said, if they are Laguz characters, we've replaced their class with the untransformed version.
				// In some cases, this is fine, but some cutscenes start them off transformed. Ch. 7 does this with Ranulf.
				// Make sure his class is set as the transformed version so that he can untransform properly.
				for (FE9ChapterArmy army : chapterData.armiesForChapter(FE9Data.Chapter.CHAPTER_7)) {
					for (String unitID : army.getAllUnitIDs()) {
						FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
						if (army.getPIDForUnit(unit).equals(FE9Data.Character.RANULF.getPID())) {
							army.setJIDForUnit(unit, FE9Data.CharacterClass.withJID(army.getJIDForUnit(unit)).getTransformedClass().getJID());
							army.commitChanges();
						}
					}
				}
			}

			if (!classData.isLaguzClass(letheClass)) { // Ch. 8 (Must come before Mordecai.)
				GCNCMBFileHandler ch8Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_8);
				FE9ScriptScene scene = ch8Script.getSceneWithIndex(31);
				removeTransformFromScript(scene, 2, false);
			} else {
				for (FE9ChapterArmy army : chapterData.armiesForChapter(FE9Data.Chapter.CHAPTER_8)) {
					for (String unitID : army.getAllUnitIDs()) {
						FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
						if (army.getPIDForUnit(unit).equals(FE9Data.Character.LETHE.getPID())) {
							army.setJIDForUnit(unit, FE9Data.CharacterClass.withJID(army.getJIDForUnit(unit)).getTransformedClass().getJID());
							army.commitChanges();
						}
					}
				}
			}
			
			if (!classData.isLaguzClass(mordecaiClass)) { // Ch. 8 (Must come after Lethe.)
				GCNCMBFileHandler ch8Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_8);
				FE9ScriptScene scene = ch8Script.getSceneWithIndex(31);
				removeTransformFromScript(scene, 1, false);
				
				scene = ch8Script.getSceneWithIndex(32);
				removeTransformFromScript(scene, 2, false);
				removeTransformFromScript(scene, 1, false);
			} else {
				for (FE9ChapterArmy army : chapterData.armiesForChapter(FE9Data.Chapter.CHAPTER_8)) {
					for (String unitID : army.getAllUnitIDs()) {
						FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
						if (army.getPIDForUnit(unit).equals(FE9Data.Character.MORDECAI.getPID())) {
							army.setJIDForUnit(unit, FE9Data.CharacterClass.withJID(army.getJIDForUnit(unit)).getTransformedClass().getJID());
							army.commitChanges();
						}
					}
				}
			}
			
			if (!classData.isLaguzClass(muarimClass)) {
				GCNCMBFileHandler ch15Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_15);
				FE9ScriptScene scene = ch15Script.getSceneWithIndex(18);
				removeTransformFromScript(scene, 1, true);
				
				scene = ch15Script.getSceneWithIndex(21);
				removeTransformFromScript(scene, 1, false);
			}
			
			// TODO: If we decide to randomize Tibarn or Naesala, we have to take care of their transformations. Tibarn's is a bit more complex as it involves getting rid of the Laguz band given to him by the script.
			
			if (!classData.isLaguzClass(janaffClass)) {
				GCNCMBFileHandler ch17Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_17);
				FE9ScriptScene scene = ch17Script.getSceneWithIndex(94);
				removeTransformFromScript(scene, 4, true);
			}
			
			if (!classData.isLaguzClass(ulkiClass)) {
				GCNCMBFileHandler ch17Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_17);
				FE9ScriptScene scene = ch17Script.getSceneWithIndex(94);
				removeTransformFromScript(scene, 3, true);
			}
			
			if (!classData.isLaguzClass(reysonClass)) {
				GCNCMBFileHandler ch17Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_17);
				FE9ScriptScene scene = ch17Script.getSceneWithIndex(94);
				removeTransformFromScript(scene, 1, true);
			}
		}
		
		if (classOptions.randomizeBosses && classOptions.mixBossRaces) {
			if (!classData.isLaguzClass(seekerClass)) {
				GCNCMBFileHandler ch12Script = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_12);
				FE9ScriptScene scene = ch12Script.getSceneWithIndex(6);
				removeTransformFromScript(scene, 1, true);
				
				scene = ch12Script.getSceneWithIndex(11);
				removeTransformFromScript(scene, 1, false);
			}
		}
		
		
		
		// Update Ike's starting inventory based on class (if necessary).
		FE9Class ikeClass = classData.classWithID(charData.getJIDForCharacter(ike));
		List<WeaponType> ikeWeaponTypes = classData.getUsableWeaponTypesForClass(ikeClass);
		FE9Item basicWeapon = null;
		if (!ikeWeaponTypes.isEmpty()) {
			WeaponType selectedType = ikeWeaponTypes.get(0);
			basicWeapon =  itemData.basicItemForType(selectedType);
		}
		
		if (basicWeapon != null && !itemData.iidOfItem(basicWeapon).equals(FE9Data.Item.IRON_SWORD.getIID())) {
			// In the script for Prologue, the game gives him one iron sword.
			// In the script for Chapter 1, the game gives him three more iron swords.
			// TODO: See what's calling the script in Chapter 1. Maybe we can get it to not do that.
			GCNCMBFileHandler prologue = chapterData.getHandlerForScripts(FE9Data.Chapter.PROLOGUE);
			FE9ScriptScene scene = prologue.getSceneWithIndex(0x13);
			List<ScriptInstruction> instructions = scene.getInstructions();
			for (int i = 0; i < instructions.size(); i++) {
				ScriptInstruction instruction = instructions.get(i);
				if (instruction instanceof PushLiteralString16Instruction && 
						((PushLiteralString16Instruction) instruction).getString().equals(FE9Data.Item.IRON_SWORD.getIID())) {
					instructions.remove(i);
					instructions.add(i, new PushLiteralString16Instruction(itemData.iidOfItem(basicWeapon), prologue));
					break;
				}
			}
			scene.setInstructions(instructions);
			//scene.commit();
			
			GCNCMBFileHandler chapter1 = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_1);
			scene = chapter1.getSceneWithIndex(0x1C);
			instructions = scene.getInstructions();
			for (int i = 0; i < instructions.size(); i++) {
				ScriptInstruction instruction = instructions.get(i);
				if (instruction instanceof PushLiteralString16Instruction &&
					((PushLiteralString16Instruction) instruction).getString().equals(FE9Data.Item.IRON_SWORD.getIID())) {
					instructions.remove(i);
					instructions.add(i, new PushLiteralString16Instruction(itemData.iidOfItem(basicWeapon), chapter1));
				}	
			}
			scene.setInstructions(instructions);
			//scene.commit();
		}
		
		// Just to be doubly sure, give Ike a Vulnerary in Prologue.
		List<FE9ChapterArmy> armies = chapterData.armiesForChapter(FE9Data.Chapter.PROLOGUE);
		for (FE9ChapterArmy army : armies) {
			for (String unitID : army.getAllUnitIDs()) {
				FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
				if (army.getPIDForUnit(unit).equals(FE9Data.Character.IKE.getPID())) {
					if (army.unitHasItem(unit, FE9Data.Item.VULNERARY.getIID()) || army.unitHasItem(unit, FE9Data.Item.ELIXIR.getIID())) { continue; }
					army.setItem1ForUnit(unit, FE9Data.Item.VULNERARY.getIID());
				}
			}
			army.commitChanges();
		}
		
		// Update Gatrie's rejoin weapon in Chapter 13.
		FE9Item replacement = null;
		FE9Character gatrie = charData.characterWithID(FE9Data.Character.GATRIE.getPID());
		FE9Class gatrieClass = classData.classWithID(charData.getJIDForCharacter(gatrie));
		if (classData.isLaguzClass(gatrieClass)) {
			replacement = itemData.laguzWeaponForJID(classData.getJIDForClass(gatrieClass));
		} else {
			List<WeaponType> weaponTypes = classData.getUsableWeaponTypesForClass(gatrieClass);
			if (!weaponTypes.contains(WeaponType.LANCE)) {
				for (WeaponType type : weaponTypes) {
					List<FE9Item> items = itemData.weaponsOfRankAndType(WeaponRank.D, type);
					if (items.size() > 0) {
						Random rng = new Random(SeedGenerator.generateSeedValue(seed, 0));
						replacement = items.get(rng.nextInt(items.size()));
						break;
					}
				}
			}
		}
		
		if (replacement != null) {
			GCNCMBFileHandler chapter13 = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_13);
			FE9ScriptScene scene = chapter13.getSceneWithIndex(0x12);
			List<ScriptInstruction> instructions = scene.getInstructions();
			for (int i = 0; i < instructions.size(); i++) {
				ScriptInstruction instruction = instructions.get(i);
				if (instruction instanceof PushLiteralString16Instruction && 
						((PushLiteralString16Instruction) instruction).getString().equals(FE9Data.Item.STEEL_LANCE.getIID())) {
					instructions.remove(i);
					instructions.add(i, new PushLiteralString16Instruction(itemData.iidOfItem(replacement), chapter13));
				}
			}
			scene.setInstructions(instructions);
			//scene.commit();
		}
		
		// Update Shinon's rejoin weapon.
		replacement = null;
		FE9Character shinon = charData.characterWithID(FE9Data.Character.SHINON.getPID());
		FE9Class shinonClass = classData.classWithID(charData.getJIDForCharacter(shinon));
		boolean shouldDrop = true;
		if (classData.isLaguzClass(shinonClass)) {
			replacement = itemData.laguzWeaponForJID(classData.getJIDForClass(shinonClass));
			shouldDrop = false;
		} else {
			List<WeaponType> weaponTypes = classData.getUsableWeaponTypesForClass(shinonClass);
			if (!weaponTypes.contains(WeaponType.BOW)) {
				weaponTypes.remove(WeaponType.STAFF);
				for (WeaponType type : weaponTypes) {
					List<FE9Item> items = itemData.weaponsOfRankAndType(WeaponRank.B, type);
					if (items.size() > 0) {
						Random rng = new Random(SeedGenerator.generateSeedValue(seed, 0));
						replacement = items.get(rng.nextInt(items.size()));
						break;
					}
				}
			}
		}
		
		if (replacement != null) {
			GCNCMBFileHandler chapter18 = chapterData.getHandlerForScripts(FE9Data.Chapter.CHAPTER_18);
			FE9ScriptScene scene = chapter18.getSceneWithIndex(0xE);
			List<ScriptInstruction> instructions = scene.getInstructions();
			for (int i = 0; i < instructions.size(); i++) {
				ScriptInstruction instruction = instructions.get(i);
				if (instruction instanceof PushLiteralString16Instruction && 
						((PushLiteralString16Instruction) instruction).getString().equals(FE9Data.Item.BRAVE_BOW.getIID())) {
					instructions.remove(i);
					instructions.add(i, new PushLiteralString16Instruction(itemData.iidOfItem(replacement), chapter18));
				}
			}
			
			if (!shouldDrop) {
				// Don't drop laguz weapons in case he's a laguz unit.
				for (int i = 0; i < instructions.size(); i++) {
					ScriptInstruction instruction = instructions.get(i);
					if (instruction instanceof CallSceneByNameInstruction && 
							((CallSceneByNameInstruction) instruction).getSceneName().equals("UnitItemSetDrop")) {
						// There's four instructions to dummy out.
						// PUSH_VAR_8 (0x0)
						// PUSH_VAR_8 (0x1)
						// CALL_SCENE_NAME ("UnitItemSetDrop", 2)
						// DISCARD_TOP
						instructions.remove(i + 1);
						instructions.remove(i);
						instructions.remove(i - 1);
						instructions.remove(i - 2);
						
						instructions.add(i - 2, new NOPInstruction());
						instructions.add(i - 1, new NOPInstruction());
						instructions.add(i, new NOPInstruction());
						instructions.add(i + 1, new NOPInstruction());
					}
				}
			}
			scene.setInstructions(instructions);
			//scene.commit();
		}
		
		// PID_B_ZAKO is for Bengion generic soldiers. They, for some reason, have a hard-coded AID of a soldier, which can mess with their map models.
		// Remove this AID and let the game handle it.
		FE9Character bZako = charData.characterWithID("PID_B_ZAKO");
		charData.setUnpromotedAIDForCharacter(bZako, null);
		
	}
	
	// If called more than once on the same script index, should be done in reverse order to make sure the index of occurence remains consistent.
	private void removeTransformFromScript(FE9ScriptScene scene, int occurrence, boolean isInstant) {
		List<ScriptInstruction> instructions = scene.getInstructions();
		boolean didChange = false;
		for (int i = 0; i < instructions.size(); i++) {
			ScriptInstruction instruction = instructions.get(i);
			if (instruction instanceof CallSceneByNameInstruction && ((CallSceneByNameInstruction)instruction).getSceneName().equals(isInstant ? "InstantUnitTransform" : "UnitTransform")) {
				if (--occurrence == 0) {
					for (int j = 0; j < 4; j++) { instructions.remove(i - 2); }
					for (int j = 0; j < 4; j++) { instructions.add(i - 2, new NOPInstruction()); }
					didChange = true;
					break;
				}
			}
		}
		
		assert didChange;
		if (didChange) {
			scene.setInstructions(instructions);
			//scene.commit();
		}
	}
	
	private void randomizeGrowthsIfNecessary(String seed) {
		if (growthOptions != null) {
			updateStatus(0.42, "Randomizing growths...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9GrowthRandomizer.rngSalt));
			switch (growthOptions.mode) {
			case REDISTRIBUTE:
				FE9GrowthRandomizer.randomizeGrowthsByRedistribution(growthOptions.redistributionOption.variance, growthOptions.redistributionOption.minValue, growthOptions.redistributionOption.maxValue, growthOptions.adjustHP, growthOptions.adjustSTRMAGSplit, charData, classData, rng);
				break;
			case DELTA:
				FE9GrowthRandomizer.randomizeGrowthsByDelta(growthOptions.deltaOption.variance, growthOptions.deltaOption.minValue, growthOptions.deltaOption.maxValue, growthOptions.adjustSTRMAGSplit, charData, classData, rng);
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
			updateStatus(0.43, "Randomizing bases...");
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
			updateStatus(0.44, "Randomizing skills...");
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
		if (rewardOptions != null) {
			updateStatus(0.45, "Randomizing rewards...");
			if (rewardOptions.randomizeRewards) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9RewardsRandomizer.rngSalt));
				switch (rewardOptions.rewardMode) {
				case SIMILAR:
					FE9RewardsRandomizer.randomizeSimilarRewards(itemData, chapterData, rng);
					break;
				case RANDOM:
					FE9RewardsRandomizer.randomizeRewards(itemData, chapterData, rng);
					break;
				}
			}
			if (rewardOptions.enemyDropChance > 0) {
				Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9RewardsRandomizer.rngSalt));
				FE9RewardsRandomizer.addEnemyDrops(charData, itemData, chapterData, rewardOptions.enemyDropChance, rng);
			}
		}
	}
	
	private void randomizeOtherCharacterOptionsIfNecessary(String seed) {
		if (otherCharOptions != null) {
			updateStatus(0.46, "Randomizing other character options...");
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
			updateStatus(0.47, "Buffing enemies...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9EnemyBuffer.rngSalt));
			
			switch (enemyBuffOptions.minionMode) {
			case FLAT:
				FE9EnemyBuffer.flatBuffMinionGrowths(enemyBuffOptions.minionBuff, classData, enemyBuffOptions.minionBuffStats);
				break;
			case SCALING:
				FE9EnemyBuffer.scaleBuffMinionGrowths(enemyBuffOptions.minionBuff, classData, enemyBuffOptions.minionBuffStats);
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
				FE9EnemyBuffer.buffBossStatsLinearly(enemyBuffOptions.bossBuff, charData, classData, enemyBuffOptions.bossBuffStats);
				break;
			case EASE_IN_OUT:
				FE9EnemyBuffer.buffBossStatsByEasing(enemyBuffOptions.bossBuff, charData, classData, enemyBuffOptions.bossBuffStats);
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
						classOptions.allowCrossgender, 
						classOptions.assignClassesEvenly,
						charData, classData, chapterData, skillData, itemData, rng);
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
	
	private void randomizeWeaponsIfNecessary(String seed) {
		if (weaponOptions != null) {
			updateStatus(0.48, "Randomizing Weapons...");
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9WeaponRandomizer.rngSalt));
			if (weaponOptions.mightOptions != null) {
				FE9WeaponRandomizer.randomizeWeaponMight(weaponOptions.mightOptions.variance, weaponOptions.mightOptions.minValue, weaponOptions.mightOptions.maxValue, itemData, rng);
			}
			if (weaponOptions.hitOptions != null) {
				FE9WeaponRandomizer.randomizeWeaponAccuracy(weaponOptions.hitOptions.variance, weaponOptions.hitOptions.minValue, weaponOptions.hitOptions.maxValue, itemData, rng);
			}
			if (weaponOptions.weightOptions != null) {
				FE9WeaponRandomizer.randomizeWeaponWeight(weaponOptions.weightOptions.variance, weaponOptions.weightOptions.minValue, weaponOptions.weightOptions.maxValue, itemData, rng);		
			}
			if (weaponOptions.durabilityOptions != null) {
				FE9WeaponRandomizer.randomizeWeaponDurability(weaponOptions.durabilityOptions.variance, weaponOptions.durabilityOptions.minValue, weaponOptions.durabilityOptions.maxValue, itemData, rng);
			}
			if (weaponOptions.shouldAddEffects) {
				FE9WeaponRandomizer.addRandomEffects(weaponOptions.effectChance, weaponOptions.noEffectIronWeapons, weaponOptions.includeLaguzWeapons, weaponOptions.effectsList, handler, itemData, textData, rng);
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
		if (weaponOptions != null) {
			if (weaponOptions.mightOptions != null) {
				table.addRow(new String[] {"Randomize Weapon MT", "+/-" + weaponOptions.mightOptions.variance + ", " + weaponOptions.mightOptions.minValue + " ~ " + weaponOptions.mightOptions.maxValue});
			}
			if (weaponOptions.hitOptions != null) {
				table.addRow(new String[] {"Randomize Weapon Hit", "+/-" + weaponOptions.hitOptions.variance + ", " + weaponOptions.hitOptions.minValue + " ~ " + weaponOptions.hitOptions.maxValue});
			}
			if (weaponOptions.weightOptions != null) {
				table.addRow(new String[] {"Randomize Weapon WT", "+/-" + weaponOptions.weightOptions.variance + ", " + weaponOptions.weightOptions.minValue + " ~ " + weaponOptions.weightOptions.maxValue});
			}
			if (weaponOptions.durabilityOptions != null) {
				table.addRow(new String[] {"Randomize Weapon Durability", "+/-" + weaponOptions.durabilityOptions.variance + ", " + weaponOptions.durabilityOptions.minValue + " ~ " + weaponOptions.durabilityOptions.maxValue});
			}
			if (weaponOptions.shouldAddEffects) {
				table.addRow(new String[] {"Add Random Effects", "YES (" + weaponOptions.effectChance + "%)"});
				table.addRow(new String[] {"Possible Effects", 
						"<ul>" +
						(weaponOptions.effectsList.statBoosts > 0 ? "<li>Stat Boosts (" + String.format("%.2f%%", (double)weaponOptions.effectsList.statBoosts / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.effectiveness > 0 ? "<li>Effectiveness (" + String.format("%.2f%%", (double)weaponOptions.effectsList.effectiveness / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.unbreakable > 0 ? "<li>Unbreakable (" + String.format("%.2f%%", (double)weaponOptions.effectsList.unbreakable / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.brave > 0 ? "<li>Brave (" + String.format("%.2f%%", (double)weaponOptions.effectsList.brave / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.reverseTriangle > 0 ? "<li>Shifted Triangle (" + String.format("%.2f%%", (double)weaponOptions.effectsList.reverseTriangle / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.extendedRange > 0 ? "<li>Extended Range (" + String.format("%.2f%%", (double)weaponOptions.effectsList.extendedRange / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.highCritical > 0 ? "<li>Critical (" + weaponOptions.effectsList.criticalRange.minValue + " ~ " + weaponOptions.effectsList.criticalRange.maxValue + ") (" + String.format("%.2f%%", (double)weaponOptions.effectsList.highCritical / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.magicDamage > 0 ? "<li>Magic Damage (" + String.format("%.2f%%", (double)weaponOptions.effectsList.magicDamage / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.poison > 0 ? "<li>Poison (" + String.format("%.2f%%", (double)weaponOptions.effectsList.poison / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.stealHP > 0 ? "<li>Steal HP (" + String.format("%.2f%%", (double)weaponOptions.effectsList.stealHP / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.critImmune > 0 ? "<li>Critical Immunity (" + String.format("%.2f%%", (double)weaponOptions.effectsList.critImmune / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						(weaponOptions.effectsList.noCrit > 0 ? "<li>Cannot Crit. (" + String.format("%.2f%%", (double)weaponOptions.effectsList.noCrit / (double)weaponOptions.effectsList.getWeightTotal() * 100) + ")</li>" : "") +
						"</ul>"
						});
				table.addRow(new String[] {"Safe Basic Weapons", weaponOptions.noEffectIronWeapons ? "YES" : "NO"});
				table.addRow(new String[] {"Include Laguz Weapons", weaponOptions.includeLaguzWeapons ? "YES" : "NO"});
			}
		}
		if (classOptions != null) {
			if (classOptions.randomizePCs) {
				table.addRow(new String[] {"Randomize Playable Characters", "YES"});
				table.addRow(new String[] {"Include Lords", classOptions.includeLords ? "YES" : "NO"});
				table.addRow(new String[] {"Include Thieves", classOptions.includeThieves ? "YES" : "NO"});
				table.addRow(new String[] {"Include Special", classOptions.includeSpecial ? "YES" : "NO"});
				table.addRow(new String[] {"Mix Races for Playable Characters", classOptions.mixPCRaces ? "YES" : "NO"});
				table.addRow(new String[] {"Allow Cross-gender Assignments", classOptions.allowCrossgender ? "YES" : "NO"});
				table.addRow(new String[] {"Assign Classes Evenly", classOptions.assignClassesEvenly ? "YES" : "NO"});
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
				table.addRow(new String[] {"Buffed Minion Stats", enemyBuffOptions.minionBuffStats.buffString()});
				break;
			case SCALING:
				table.addRow(new String[] {"Buff Minions", "Scaling Buff (+" + enemyBuffOptions.minionBuff + "%)"});
				table.addRow(new String[] {"Buffed Minion Stats", enemyBuffOptions.minionBuffStats.buffString()});
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
				table.addRow(new String[] {"Buffed Boss Stats", enemyBuffOptions.bossBuffStats.buffString()});
				break;
			case EASE_IN_OUT:
				table.addRow(new String[] {"Buff Bosses", "YES (Max Boost: " + enemyBuffOptions.bossBuff + " - Ease In/Ease Out);"});
				table.addRow(new String[] {"Buffed Boss Stats", enemyBuffOptions.bossBuffStats.buffString()});
				break;
			}
			table.addRow(new String[] {"Improve Boss Weapons", enemyBuffOptions.improveBossWeapons ? "YES (" + enemyBuffOptions.bossImprovementChance + "%)" : "NO"});
			table.addRow(new String[] {"Give Bosses Skills", enemyBuffOptions.giveBossSkills ? "YES (" + enemyBuffOptions.bossSkillChance + "%)" : "NO"});
		}
		if (rewardOptions != null && rewardOptions.randomizeRewards) {
				switch(rewardOptions.rewardMode) {
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
