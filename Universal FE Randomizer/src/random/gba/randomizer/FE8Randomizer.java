package random.gba.randomizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapSpriteData;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.fe8.FE8PaletteMapper;
import fedata.gba.fe8.FE8PromotionManager;
import fedata.gba.fe8.FE8SpellAnimationCollection;
import fedata.gba.fe8.FE8SummonerModule;
import fedata.gba.general.WeaponType;
import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import io.UPSPatcher;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.ItemDataLoader.AdditionalData;
import random.gba.loader.PaletteLoader;
import random.gba.loader.PortraitDataLoader;
import random.gba.loader.TextLoader;
import ui.model.BaseOptions;
import ui.model.ClassOptions;
import ui.model.EnemyOptions;
import ui.model.GrowthOptions;
import ui.model.ItemAssignmentOptions;
import ui.model.MiscellaneousOptions;
import ui.model.OtherCharacterOptions;
import ui.model.RecruitmentOptions;
import ui.model.WeaponOptions;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FreeSpaceManager;
import util.GBAImageCodec;
import util.SeedGenerator;
import util.WhyDoesJavaNotHaveThese;
import util.OptionRecorder.GBAOptionBundle;

public class FE8Randomizer extends AbstractGBARandomizer {

	public FE8Randomizer(String sourcePath, String targetPath, GameType gameType, DiffCompiler diffs,
			GBAOptionBundle options, String seed) {
		super(sourcePath, targetPath, gameType, diffs, options, seed, FE8Data.FriendlyName);
		
	}
	// FE8 only
	private FE8PaletteMapper fe8_paletteMapper;
	private FE8PromotionManager fe8_promotionManager;
	private FE8SummonerModule fe8_summonerModule;
	private boolean fe8_walkingSoundFixApplied = false;
	
	
	@Override
	public void runDataloaders() {
		sourceFileHandler.setAppliedDiffs(diffCompiler);
		
		updateStatusString("Detecting Free Space...");
		updateProgress(0.02);
		freeSpace = new FreeSpaceManager(FEBase.GameType.FE8, FE8Data.InternalFreeRange, sourceFileHandler);
		updateStatusString("Loading Text...");
		updateProgress(0.04);
		textData = new TextLoader(FEBase.GameType.FE8, FE8Data.textProvider, sourceFileHandler);
		textData.allowTextChanges = true;
		
		updateStatusString("Loading Promotion Data...");
		updateProgress(0.06);
		fe8_promotionManager = new FE8PromotionManager(sourceFileHandler);
		updateStatusString("Loading Portrait Data...");
		updateProgress(0.07);
		portraitData = new PortraitDataLoader(FE8Data.shufflingDataProvider, sourceFileHandler);
		updateStatusString("Loading Character Data...");
		updateProgress(0.10);
		charData = new CharacterDataLoader(FE8Data.characterProvider, sourceFileHandler);
		updateStatusString("Loading Class Data...");
		updateProgress(0.15);
		classData = new ClassDataLoader(FE8Data.classProvider, sourceFileHandler);
		updateStatusString("Loading Chapter Data...");
		updateProgress(0.20);
		chapterData = new ChapterLoader(FEBase.GameType.FE8, sourceFileHandler);
		updateStatusString("Loading Item Data...");
		updateProgress(0.25);
		itemData = new ItemDataLoader(FE8Data.itemProvider, sourceFileHandler, freeSpace);
		updateStatusString("Loading Palette Data...");
		updateProgress(0.30);
		paletteData = new PaletteLoader(FEBase.GameType.FE8, sourceFileHandler, charData, classData);
		updateStatusString("Loading Statboost Data...");
		
		updateStatusString("Loading Summoner Module...");
		updateProgress(0.35);
		fe8_summonerModule = new FE8SummonerModule(sourceFileHandler);
		
		updateStatusString("Loading Palette Mapper...");
		updateProgress(0.40);
		fe8_paletteMapper = paletteData.setupFE8SpecialManagers(sourceFileHandler, fe8_promotionManager);
		
		
		sourceFileHandler.clearAppliedDiffs();		
	}
	
	@Override
	protected void makePreliminaryAdjustments() {
		// FE8 Walking sound effect fix.
		// From Tequila's patch.
		try {
			InputStream stream = UPSPatcher.class.getClassLoader().getResourceAsStream("fe8_walking_sound_fix.bin");
			byte[] fixData = new byte[0x14C];
			stream.read(fixData);
			stream.close();
			
			diffCompiler.addDiff(new Diff(0x78d78, fixData.length, fixData, null));
			
			fe8_walkingSoundFixApplied = true;
		} catch (Exception e) {
			
		}
		
		super.makePreliminaryAdjustments();
	}
	
	@Override
	protected void makeFinalAdjustments() {
		Random rng = new Random(SeedGenerator.generateSeedValue(seedString, 1));
		applyPaletteFixes();
		applyPromotionFix();
		fixWorldMapSprites();
		createTraineeSeal();
		ensureHealersHaveStaves(rng);
		createSpecialLordClasses();
		createPrfs(rng);
	}
	
	@Override
	protected void applyPromotionFix() {
		// FE8 stores this in a separate table.
		for (GBAFEClassData charClass : classData.allClasses()) {
			if (classData.isPromotedClass(charClass.getID())) {
				int demotedID1 = fe8_promotionManager.getFirstPromotionOptionClassID(charClass.getID());
				int demotedID2 = fe8_promotionManager.getSecondPromotionOptionClassID(charClass.getID());
				if (demotedID1 == 0 && demotedID2 == 0) {
					// If we have no promotions and we are a promoted class, then apply our fix.
					// Promote into yourself if this happens.
					fe8_promotionManager.setFirstPromotionOptionForClass(charClass.getID(), charClass.getID());
				}
			}
		}
	}

	@Override
	public void recordNotes() {
		recordKeeper.addNote("Characters that randomize into the Soldier class can promote into a Paladin or General using a Knight's Crest or Master Seal.");
		recordKeeper.addNote("Characters that randomize into the Eirika Lord class can promote using a Knight's Crest or Master Seal.");
		recordKeeper.addNote("Characters that randomize into the Ephraim Lord class can promote using a Knight's Crest or Master Seal.");
		recordKeeper.addNote("Characters that randomize into Revenant, Sword/Lance Bonewalkers, and Mauthe Doogs promote using a Hero's Crest or Master Seal.");
		recordKeeper.addNote("Characters that randomize into Tarvos and Bael promote using a Knight's Crest or Master Seal.");
		recordKeeper.addNote("Characters that randomize into a Mogall promote using a Guiding Ring or Master Seal.");
		recordKeeper.addNote("Characters that randomize into a Bow Bonewalker promote using an Orion's Bolt or Master Seal.");
		recordKeeper.addNote("Characters that randomize into a Gargoyle promote using an Elysian Whip or Master Seal.");
	}

	@Override
	public void recordPostRandomizationState() {
		charData.recordCharacters(recordKeeper, false, classData, itemData, textData);
		classData.recordClasses(recordKeeper, false, classData, textData);
		itemData.recordWeapons(recordKeeper, false, classData, textData, targetFileHandler);
		chapterData.recordChapters(recordKeeper, false, charData, classData, itemData, textData);
		//FE8 Specific
		paletteData.recordUpdatedFE8Palettes(recordKeeper, charData, classData, textData);
		//FE8 Specific
	}

	@Override
	protected void createSpecialLordClasses() {
		GBAFECharacterData eirika = charData.characterWithID(FE8Data.Character.EIRIKA.ID);
		GBAFECharacterData ephraim = charData.characterWithID(FE8Data.Character.EPHRAIM.ID);
		
		int oldEirikaClass = eirika.getClassID();
		int oldEphraimClass = ephraim.getClassID();
		
		// GBAFE only stores 5 bits for the class (in save data), so using any ID greater than 0x7F will have issues. We have to replace an existing class.
		GBAFEClassData newEirikaClass = classData.createLordClassBasedOnClass(classData.classForID(oldEirikaClass), FE8Data.CharacterClass.UNUSED_TENT.ID); // This was a (unused?) tent.
		GBAFEClassData newEphraimClass = classData.createLordClassBasedOnClass(classData.classForID(oldEphraimClass), FE8Data.CharacterClass.UNUSED_MANAKETE.ID); // This is an unused manakete class.
		
		eirika.setClassID(newEirikaClass.getID());
		ephraim.setClassID(newEphraimClass.getID());
		
		// Add new classes to any effectiveness tables.
		List<AdditionalData> effectivenesses = itemData.effectivenessArraysForClassID(oldEirikaClass);
		for (AdditionalData effectiveness : effectivenesses) {
			itemData.addClassIDToEffectiveness(effectiveness, newEirikaClass.getID());
		}
		effectivenesses = itemData.effectivenessArraysForClassID(oldEphraimClass);
		for (AdditionalData effectiveness : effectivenesses) {
			itemData.addClassIDToEffectiveness(effectiveness, newEphraimClass.getID());
		}
		
		itemData.replaceClassesForPromotionItem(FE8Data.PromotionItem.LUNAR_BRACE, new ArrayList<Integer>(Arrays.asList(newEirikaClass.getID())));
		itemData.replaceClassesForPromotionItem(FE8Data.PromotionItem.SOLAR_BRACE, new ArrayList<Integer>(Arrays.asList(newEphraimClass.getID())));
		
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.getCharacterNumber() == FE8Data.Character.EIRIKA.ID) {
					if (unit.getStartingClass() == oldEirikaClass) { unit.setStartingClass(newEirikaClass.getID()); }
				} else if (unit.getCharacterNumber() == FE8Data.Character.EPHRAIM.ID) {
					if (unit.getStartingClass() == oldEphraimClass) { unit.setStartingClass(newEphraimClass.getID()); /* unit.setStartingLevel(10); */}
				}
				/*
				if (unit.getCharacterNumber() == FE8Data.Character.ORSON_5X.ID) {
					unit.giveItem(itemData.itemsToPromoteClass(oldEphraimClass).get(0).getID());
					unit.giveItem(itemData.itemsToPromoteClass(newEphraimClass.getID()).get(0).getID());
				}*/
			}
		}
		
		// Update the promotions table, since they're technically "different" classes.
		fe8_promotionManager.setFirstPromotionOptionForClass(newEirikaClass.getID(), fe8_promotionManager.getFirstPromotionOptionClassID(oldEirikaClass));
		fe8_promotionManager.setSecondPromotionOptionForClass(newEirikaClass.getID(), fe8_promotionManager.getSecondPromotionOptionClassID(oldEirikaClass));
		fe8_promotionManager.setFirstPromotionOptionForClass(newEphraimClass.getID(), fe8_promotionManager.getFirstPromotionOptionClassID(oldEphraimClass));
		fe8_promotionManager.setSecondPromotionOptionForClass(newEphraimClass.getID(), fe8_promotionManager.getSecondPromotionOptionClassID(oldEphraimClass));
		
		// Palettes are also tied to class.
		FE8PaletteMapper.ClassMapEntry eirikaPalette = fe8_paletteMapper.getEntryForCharacter(FE8Data.Character.EIRIKA);
		FE8PaletteMapper.ClassMapEntry ephraimPalette = fe8_paletteMapper.getEntryForCharacter(FE8Data.Character.EPHRAIM);
		
		// Only base classes need to be updated. Promoted classes are not special.
		eirikaPalette.setBaseClassID(newEirikaClass.getID());
		ephraimPalette.setBaseClassID(newEphraimClass.getID());
		
		// On the bright side, we don't need to repoint the FE8 map sprite table. We just need to replace some entries in the existing one.
		long mapSpriteTableOffset = FileReadHelper.readAddress(sourceFileHandler, FE8Data.ClassMapSpriteTablePointer);
		long eirikaTargetOffset = (newEirikaClass.getID() - 1) * 8 + mapSpriteTableOffset;
		long ephraimTargetOffset = (newEphraimClass.getID() - 1) * 8 + mapSpriteTableOffset;
		byte[] eirikaSpriteData = sourceFileHandler.readBytesAtOffset((oldEirikaClass - 1) * 8 + mapSpriteTableOffset, 8);
		byte[] ephraimSpriteData = sourceFileHandler.readBytesAtOffset((oldEphraimClass - 1) * 8 + mapSpriteTableOffset, 8);
		diffCompiler.addDiff(new Diff(eirikaTargetOffset, 8, eirikaSpriteData, null));
		diffCompiler.addDiff(new Diff(ephraimTargetOffset, 8, ephraimSpriteData, null));
		
		if (fe8_walkingSoundFixApplied) {
			long eirikaWalkingSoundOffset = 0x78D90 + newEirikaClass.getID();
			long ephraimWalkingSoundOffset = 0x78D90 + newEphraimClass.getID();
			
			InputStream stream = UPSPatcher.class.getClassLoader().getResourceAsStream("fe8_walking_sound_fix.bin");
			byte[] fixData = new byte[0x14C];
			try {
				stream.read(fixData);
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			byte eirikaWalkingSoundID = fixData[0x18 + oldEirikaClass];
			byte ephraimWalkingSoundID = fixData[0x18 + oldEphraimClass];
			
			diffCompiler.addDiff(new Diff(eirikaWalkingSoundOffset, 1, new byte[] {eirikaWalkingSoundID}, null));
			diffCompiler.addDiff(new Diff(ephraimWalkingSoundOffset, 1, new byte[] {ephraimWalkingSoundID}, null));
		}		
	}

	@Override
	protected void createPrfs(Random rng) {
		if ((classes == null || !classes.createPrfs) || (recruitOptions == null || !recruitOptions.createPrfs)) {
			return;
		}
		
		boolean unbreakablePrfs = ((classes != null && classes.unbreakablePrfs) || (recruitOptions != null && recruitOptions.createPrfs));
		GBAFECharacterData eirika = charData.characterWithID(FE8Data.Character.EIRIKA.ID);
		GBAFECharacterData ephraim = charData.characterWithID(FE8Data.Character.EPHRAIM.ID);
		
		GBAFEClassData eirikaClass = classData.classForID(eirika.getClassID());
		GBAFEClassData ephraimClass = classData.classForID(ephraim.getClassID());
		
		List<WeaponType> eirikaWeaponTypes = classData.usableTypesForClass(eirikaClass);
		List<WeaponType> ephraimWeaponTypes = classData.usableTypesForClass(ephraimClass);
		
		eirikaWeaponTypes.remove(WeaponType.STAFF);
		ephraimWeaponTypes.remove(WeaponType.STAFF);
		
		String eirikaIconName = null;
		String eirikaWeaponName = null;
		WeaponType eirikaSelectedType = null;
		String ephraimIconName = null;
		String ephraimWeaponName = null;
		WeaponType ephraimSelectedType = null;
		
		if (!eirikaWeaponTypes.isEmpty()) {
			eirikaSelectedType = eirikaWeaponTypes.get(rng.nextInt(eirikaWeaponTypes.size()));
			switch (eirikaSelectedType) {
			case SWORD:
				eirikaWeaponName = "Moon Blade";
				eirikaIconName = "weaponIcons/MoonBlade.png";
				break;
			case LANCE:
				eirikaWeaponName = "Moon Spear";
				eirikaIconName = "weaponIcons/MoonSpear.png";
				break;
			case AXE:
				eirikaWeaponName = "Moon Hammer";
				eirikaIconName = "weaponIcons/MoonHammer.png";
				break;
			case BOW:
				eirikaWeaponName = "Moon Shot";
				eirikaIconName = "weaponIcons/MoonShot.png";
				break;
			case ANIMA:
				eirikaWeaponName = "Lunar Bolt";
				eirikaIconName = "weaponIcons/LunarBolt.png";
				break;
			case DARK:
				eirikaWeaponName = "Lunar Eclipse";
				eirikaIconName = "weaponIcons/LunarEclipse.png";
				break;
			case LIGHT:
				eirikaWeaponName = "Lunar Beam";
				eirikaIconName = "weaponIcons/LunarBeam.png";
				break;
			default: 
				break;
			}
		}
		
		if (!ephraimWeaponTypes.isEmpty()) {
			ephraimSelectedType = ephraimWeaponTypes.get(rng.nextInt(ephraimWeaponTypes.size()));
			switch (ephraimSelectedType) {
			case SWORD:
				ephraimWeaponName = "Sun Blade";
				ephraimIconName = "weaponIcons/SunBlade.png";
				break;
			case LANCE:
				ephraimWeaponName = "Sun Spear";
				ephraimIconName = "weaponIcons/SunSpear.png";
				break;
			case AXE:
				ephraimWeaponName = "Sun Mallet";
				ephraimIconName = "weaponIcons/SunMallet.png";
				break;
			case BOW:
				ephraimWeaponName = "Sun Shot";
				ephraimIconName = "weaponIcons/SunShot.png";
				break;
			case ANIMA:
				ephraimWeaponName = "Solar Flare";
				ephraimIconName = "weaponIcons/SolarFlare.png";
				break;
			case DARK:
				ephraimWeaponName = "Solar Eclipse";
				ephraimIconName = "weaponIcons/SolarEclipse.png";
				break;
			case LIGHT:
				ephraimWeaponName = "Solar Beam";
				ephraimIconName = "weaponIcons/SolarBeam.png";
				break;
			default: 
				break;
			}
		}
			
		if (eirikaWeaponName != null && eirikaIconName != null) {
			// Replace the old icon.
			byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(eirikaIconName, GBAImageCodec.gbaWeaponColorPalette);
			if (iconData == null) {
				notifyError("Invalid image data for icon " + eirikaIconName);
			}
			diffCompiler.addDiff(new Diff(0x592B74, iconData.length, iconData, null));
			
			// Reusing the dummy Mani Katti
			textData.setStringAtIndex(0x3A, eirikaWeaponName + "[X]");
			// We need a description string so that the rest of the weapon stats will show, even if it's a blank string.
			textData.setStringAtIndex(0x3B, " [.][X]");
			
			GBAFEItemData itemToReplace = itemData.itemWithID(FE8Data.Item.UNUSED_MANI_KATTI.ID);
			itemToReplace.turnIntoLordWeapon(eirika.getID(), 0x3A, 0x3B, eirikaSelectedType, unbreakablePrfs, eirikaClass.getCON() + eirika.getConstitution(), 
					itemData.itemWithID(FE8Data.Item.RAPIER.ID), itemData, freeSpace);
			
			switch (eirikaSelectedType) {
			case SWORD:
			case LANCE:
			case AXE:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.NONE2.value, FE8SpellAnimationCollection.Flash.WHITE.value);
				break;
			case BOW:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.ARROW.value, FE8SpellAnimationCollection.Flash.WHITE.value);
				break;
			case ANIMA:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.THUNDER.value, FE8SpellAnimationCollection.Flash.YELLOW.value);
				break;
			case DARK:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.FLUX.value, FE8SpellAnimationCollection.Flash.DARK.value);
				break;
			case LIGHT:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.DIVINE.value, FE8SpellAnimationCollection.Flash.BLUE.value);
				break;
			default:
				// No animation needed here.
				break;
			}
			
			// Make sure Eirika herself can. She'll use the unused Lyn Lock.
			eirika.enableWeaponLock(FE8Data.CharacterAndClassAbility4Mask.EIRIKA_WEAPON_LOCK.getValue());
			itemToReplace.setAbility3(FE8Data.Item.Ability3Mask.EIRIKA_LOCK.ID);
			
			// Eirika will get her weapon from Seth.
			GBAFEChapterData prologue = chapterData.chapterWithID(FE8Data.ChapterPointer.PROLOGUE.chapterID);
			GBAFEChapterItemData item = prologue.chapterItemGivenToCharacter(FE8Data.Character.EIRIKA.ID);
			item.setItemID(itemToReplace.getID());
		}
		
		if (ephraimWeaponName != null && ephraimIconName != null) {
			// Replace the old icon.
			byte[] iconData = GBAImageCodec.getGBAGraphicsDataForImage(ephraimIconName, GBAImageCodec.gbaWeaponColorPalette);
			if (iconData == null) {
				notifyError("Invalid image data for icon " + ephraimIconName);
			}
			diffCompiler.addDiff(new Diff(0x594474, iconData.length, iconData, null));
			
			// Reusing the dummy Forblaze
			textData.setStringAtIndex(0x3C, ephraimWeaponName + "[X]");
			// We need a description string for the rest of the weapon stats to show up.
			textData.setStringAtIndex(0x3D, " [.][X]");
			
			GBAFEItemData itemToReplace = itemData.itemWithID(FE8Data.Item.UNUSED_FORBLAZE.ID);
			itemToReplace.turnIntoLordWeapon(eirika.getID(), 0x3C, 0x3D, ephraimSelectedType, unbreakablePrfs, ephraimClass.getCON() + ephraim.getConstitution(), 
					itemData.itemWithID(FE8Data.Item.REGINLEIF.ID), itemData, freeSpace);
			
			switch (ephraimSelectedType) {
			case SWORD:
			case LANCE:
			case AXE:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.NONE2.value, FE8SpellAnimationCollection.Flash.WHITE.value);
				break;
			case BOW:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.ARROW.value, FE8SpellAnimationCollection.Flash.WHITE.value);
				break;
			case ANIMA:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.ELFIRE.value, FE8SpellAnimationCollection.Flash.RED.value);
				break;
			case DARK:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.FLUX.value, FE8SpellAnimationCollection.Flash.DARK.value);
				break;
			case LIGHT:
				itemData.spellAnimations.addAnimation(itemToReplace.getID(), 2, 
						FE8SpellAnimationCollection.Animation.DIVINE.value, FE8SpellAnimationCollection.Flash.YELLOW.value);
				break;
			default:
				// No animation needed here.
				break;
			}
			
			// Make sure Ephraim himself can. He'll use the unused Athos Lock.
			ephraim.enableWeaponLock(FE8Data.CharacterAndClassAbility4Mask.UNUSED_ATHOS_LOCK.getValue());
			itemToReplace.setAbility3(FE8Data.Item.Ability3Mask.UNUSED_WEAPON_LOCK.ID);
			
			// Ephraim starts with his weapon.
			GBAFEChapterData ch5x = chapterData.chapterWithID(FE8Data.ChapterPointer.CHAPTER_5X.chapterID);
			for (GBAFEChapterUnitData unit : ch5x.allUnits()) {
				if (unit.getCharacterNumber() == ephraim.getID()) {
					unit.removeItem(FE8Data.Item.REGINLEIF.ID);
					unit.giveItem(itemToReplace.getID());
				}
			}
		}
	}
	
	/**
	 * Loop through the chapters and fix all the World Map Sprites that need to change.
	 */
	protected void fixWorldMapSprites() {
		for (FE8Data.ChapterPointer chapter : FE8Data.ChapterPointer.values()) {
			Map<Integer, List<Integer>> perChapterMap = chapter.worldMapSpriteClassIDToCharacterIDMapping();
			GBAFEWorldMapData worldMapData = chapterData.worldMapEventsForChapterID(chapter.chapterID);
			for (GBAFEWorldMapSpriteData sprite : worldMapData.allSprites()) {
				// If it's a class we don't touch, ignore it.
				if (classData.classForID(sprite.getClassID()) == null) { continue; }
				// Check Universal list first.
				Integer characterID = FE8Data.ChapterPointer.universalWorldMapSpriteClassIDToCharacterIDMapping().get(sprite.getClassID());
				if (characterID != null) {
					if (characterID == FE8Data.Character.NONE.ID) { continue; }
					syncWorldMapSpriteToCharacter(sprite, characterID);
				} else {
					// Check per chapter
					List<Integer> charactersForClassID = perChapterMap.get(sprite.getClassID());
					if (charactersForClassID != null && !charactersForClassID.isEmpty()) {
						int charID = charactersForClassID.remove(0);
						if (charID == FE8Data.Character.NONE.ID) {
							charactersForClassID.add(FE8Data.Character.NONE.ID);
							continue;
						}
						syncWorldMapSpriteToCharacter(sprite, charID);
					} else {
						assert false : "Unaccounted for world map sprite in " + chapter.toString();
					}
				}
			}
		}
	}
	
	protected void createTraineeSeal() {
		// Create the Trainee Seal using the old heaven seal.
		textData.setStringAtIndex(0x4AB, "Promotes Tier 0 Trainees at Lv 10.[X]");
		textData.setStringAtIndex(0x403, "Trainee Seal[X]");
		long offset = freeSpace.setValue(new byte[] {(byte)FE8Data.CharacterClass.TRAINEE.ID, (byte)FE8Data.CharacterClass.PUPIL.ID, (byte)FE8Data.CharacterClass.RECRUIT.ID}, "TraineeSeal");
		diffCompiler.addDiff(new Diff(FE8Data.HeavenSealPromotionPointer, 4, WhyDoesJavaNotHaveThese.bytesFromAddress(offset), WhyDoesJavaNotHaveThese.bytesFromAddress(FE8Data.HeavenSealOldAddress)));
		
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			for (GBAFEChapterUnitData chapterUnit : chapter.allUnits()) {
				FE8Data.CharacterClass charClass = FE8Data.CharacterClass.valueOf(chapterUnit.getStartingClass());
				if (FE8Data.CharacterClass.allTraineeClasses.contains(charClass)) {
					chapterUnit.giveItems(new int[] {FE8Data.Item.HEAVEN_SEAL.ID});
				}
			}
		}
	}

	@Override
	protected void applySingleRN() {
		diffCompiler.addDiff(new Diff(0xCC2, 4, new byte[] { (byte) 0xC0, (byte) 0x46, (byte) 0xC0, (byte) 0x46 },
				new byte[] { (byte) 0xFF, (byte) 0xF7, (byte) 0xCF, (byte) 0xFF }));
	}
	
	@Override
	protected void gameSpecificDiffCompilations() {
		fe8_paletteMapper.commitChanges(diffCompiler);
		fe8_summonerModule.validateSummoners(charData, new Random(SeedGenerator.generateSeedValue(seedString, 0)));
		fe8_summonerModule.commitChanges(diffCompiler, freeSpace);
	}
	
	@Override
	protected void applyUpsPatches() {
		// N/A
	}

	@Override
	protected void applyCasualMode() {
		diffCompiler.addDiff(new Diff(0x1841A, 1, new byte[] {(byte)0x09}, new byte[] {(byte)0x05}));
	}

	@Override
	protected void applyParagonMode() {
		// Combat EXP
		diffCompiler.addDiff(new Diff(0x2C58A, 24,
				new byte[] {(byte)0x00, (byte)0x99, (byte)0x09, (byte)0x18, (byte)0x48, (byte)0x00, (byte)0x64, (byte)0x28,
						    (byte)0x00, (byte)0xDD, (byte)0x64, (byte)0x20, (byte)0x00, (byte)0x28, (byte)0x00, (byte)0xDC,
						    (byte)0x01, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x90},
				new byte[] {(byte)0x00, (byte)0x99, (byte)0x09, (byte)0x18, (byte)0x00, (byte)0x91, (byte)0x64, (byte)0x29,
						    (byte)0x01, (byte)0xDD, (byte)0x64, (byte)0x20, (byte)0x00, (byte)0x90, (byte)0x00, (byte)0x98,
						    (byte)0x00, (byte)0x28, (byte)0x01, (byte)0xDC, (byte)0x01, (byte)0x20, (byte)0x00, (byte)0x90}));
		
		// Staff EXP
		diffCompiler.addDiff(new Diff(0x2C688, 12,
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x01, (byte)0xD0, (byte)0x52, (byte)0x10, (byte)0x00, (byte)0x00,
						    (byte)0x52, (byte)0x00, (byte)0x64, (byte)0x2A},
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x02, (byte)0xD0, (byte)0xD0, (byte)0x0F, (byte)0x10, (byte)0x18,
						    (byte)0x42, (byte)0x10, (byte)0x64, (byte)0x2A}));
		
		// Steal/Dance EXP
		diffCompiler.addDiff(new Diff(0x2C6CC, 8,
				new byte[] {(byte)0x14, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x14, (byte)0x30},
				new byte[] {(byte)0x0A, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x0A, (byte)0x30}));		
	}

	@Override
	protected void applyRenegadeMode() {
		// Combat EXP
		diffCompiler.addDiff(new Diff(0x2C58A, 24,
				new byte[] {(byte)0x00, (byte)0x99, (byte)0x09, (byte)0x18, (byte)0x48, (byte)0x10, (byte)0x64, (byte)0x28,
						    (byte)0x00, (byte)0xDD, (byte)0x64, (byte)0x20, (byte)0x00, (byte)0x28, (byte)0x00, (byte)0xDC,
						    (byte)0x01, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x90},
				new byte[] {(byte)0x00, (byte)0x99, (byte)0x09, (byte)0x18, (byte)0x00, (byte)0x91, (byte)0x64, (byte)0x29,
						    (byte)0x01, (byte)0xDD, (byte)0x64, (byte)0x20, (byte)0x00, (byte)0x90, (byte)0x00, (byte)0x98,
						    (byte)0x00, (byte)0x28, (byte)0x01, (byte)0xDC, (byte)0x01, (byte)0x20, (byte)0x00, (byte)0x90}));
		
		// Staff EXP
		diffCompiler.addDiff(new Diff(0x2C688, 12,
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x01, (byte)0xD0, (byte)0x52, (byte)0x10, (byte)0x00, (byte)0x00,
				            (byte)0x52, (byte)0x10, (byte)0x64, (byte)0x2A},
				new byte[] {(byte)0x00, (byte)0x28, (byte)0x02, (byte)0xD0, (byte)0xD0, (byte)0x0F, (byte)0x10, (byte)0x18,
						    (byte)0x42, (byte)0x10, (byte)0x64, (byte)0x2A}));
		
		// Steal/Dance EXP
		diffCompiler.addDiff(new Diff(0x2C6CC, 8,
				new byte[] {(byte)0x05, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x05, (byte)0x30},
				new byte[] {(byte)0x0A, (byte)0x20, (byte)0x08, (byte)0x70, (byte)0x60, (byte)0x7A, (byte)0x0A, (byte)0x30}));		
	}
}
