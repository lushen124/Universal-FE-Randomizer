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
import ui.fe9.FE9SkillsOptions;
import ui.model.BaseOptions;
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
	
	FE9CommonTextLoader textData;
	FE9CharacterDataLoader charData;
	FE9ClassDataLoader classData;
	FE9ItemDataLoader itemData;
	FE9SkillDataLoader skillData;
	FE9ChapterDataLoader chapterData;
	
	public FE9Randomizer(String sourcePath, String targetPath, GrowthOptions growthOptions, BaseOptions baseOptions, FE9SkillsOptions skillOptions, FE9OtherCharacterOptions otherCharOptions, MiscellaneousOptions miscOptions, String seed) {
		super();
		
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		
		this.growthOptions = growthOptions;
		this.baseOptions = baseOptions;
		this.skillOptions = skillOptions;
		this.otherCharOptions = otherCharOptions;
		this.miscOptions = miscOptions;
		
		this.seedString = seed;
	}
	
	public void run() {
		randomize(seedString);
	}
	
	private void randomize(String seed) {
		try {
			handler = new GCNISOHandler(new FileHandler(sourcePath));
		} catch (IOException e) {
			notifyError("Failed to open source file.");
			return;
		} catch (GCNISOException e) {
			notifyError("Failed to read Gamecube ISO format.");
		}
		
		int indexOfPathSeparator = targetPath.lastIndexOf(File.separator);
		String path = targetPath.substring(0, indexOfPathSeparator);
		
//		try {
//			List<GCNFSTEntry> mapEntries = handler.entriesWithFilename("map.cmp");
//			for (GCNFSTEntry entry : mapEntries) {
//				String fullName = handler.fstNameOfEntry(entry);
//				fullName = fullName.replace("/", "_");
//				String disposPath = path + File.separator + "map" + File.separator + fullName;
//				GCNFileHandler fileHandler = handler.handlerForFSTEntry(entry);
//				
//				byte[] data = fileHandler.readBytesAtOffset(0, (int)fileHandler.getFileLength());
//				try {
//					FileWriter.writeBinaryDataToFile(LZ77.decompress(data), disposPath);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				GCNCMPFileHandler cmpHandler = (GCNCMPFileHandler)fileHandler;
//				for (String packedName : cmpHandler.getNames()) {
//					GCNFileHandler childHandler = cmpHandler.getChildHandler(packedName);
//					String childPath = disposPath + File.separator + packedName;
//					byte[] childData = childHandler.readBytesAtOffset(0, (int)childHandler.getFileLength());
//					try {
//						FileWriter.writeBinaryDataToFile(childData, childPath);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		} catch (GCNISOException e) {
//			e.printStackTrace();
//			notifyError("Failed to extract map.cmp files.");
//			return;
//		}
		
		try {
			textData = new FE9CommonTextLoader(handler);
			charData = new FE9CharacterDataLoader(handler, textData);
			classData = new FE9ClassDataLoader(handler, textData);
			itemData = new FE9ItemDataLoader(handler, textData);
			skillData = new FE9SkillDataLoader(handler, textData);
			chapterData = new FE9ChapterDataLoader(handler, textData);
			
			List<FE9ChapterArmy> armies = chapterData.armiesForChapter(FE9Data.Chapter.PROLOGUE);
			for (FE9ChapterArmy army : armies) {
				FE9ChapterUnit ike = army.getUnitForPID("PID_IKE");
				army.setSkill2ForUnit(ike, FE9Data.Skill.RESOLVE.getSID());
				army.setSkill3ForUnit(ike, FE9Data.Skill.WRATH.getSID());
				army.commitChanges();
			}
			
			armies = chapterData.armiesForChapter(FE9Data.Chapter.CHAPTER_1);
			for (FE9ChapterArmy army : armies) {
				for (String unitID : army.getAllUnitIDs()) {
					FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
					if (army.getSkill1ForUnit(unit) == null) {
						army.setSkill1ForUnit(unit, FE9Data.Skill.SERENITY.getSID());
					} else if (army.getSkill2ForUnit(unit) == null) {
						army.setSkill2ForUnit(unit, FE9Data.Skill.TEMPEST.getSID());
					} else if (army.getSkill3ForUnit(unit) == null) {
						army.setSkill3ForUnit(unit, FE9Data.Skill.MIRACLE.getSID());
					}
				}
				army.commitChanges();
			}
			
			randomizeGrowthsIfNecessary(seed);
			randomizeBasesIfNecessary(seed);
			randomizeSkillsIfNecessary(seed);
			randomizeOtherCharacterOptionsIfNecessary(seed);
			randomizeMiscellaneousIfNecessary(seed);
			
			//FE9Character kieran = charData.characterWithID(FE9Data.Character.KIERAN.getPID());
			//FE9Character oscar = charData.characterWithID(FE9Data.Character.OSCAR.getPID());
			
			//byte[] kieranData = Arrays.copyOf(kieran.getData(), kieran.getData().length);
			//byte[] oscarData = Arrays.copyOf(oscar.getData(), oscar.getData().length);
			
			//long kieranPIDPtr = kieran.getCharacterIDPointer();
			//long oscarPIDPtr = oscar.getCharacterIDPointer();
			
			//kieran.setData(oscarData);
			//oscar.setData(kieranData);
			
			//oscar.setCharacterIDPointer(kieranPIDPtr);
			//kieran.setCharacterIDPointer(oscarPIDPtr);
			
			//kieran.setUnknown6Bytes(oscar.getUnknown6Bytes());
			//kieran.setUnknown8Bytes(oscar.getUnknown8Bytes());
			GCNFileHandler fe8databin = handler.handlerForFileWithName("system.cmp/FE8Data.bin");
			//fe8databin.addChange(new Diff(0x1CE2C, 4, new byte[] {0, 0, (byte)0x02, (byte)0x24}, new byte[] {0, 0, (byte)0x05, (byte)0x6C}));
			assert(fe8databin instanceof GCNDataFileHandler);
			GCNDataFileHandler dataFileHandler = (GCNDataFileHandler)fe8databin;
			
//			oscar.setClassPointer(dataFileHandler.pointerForString(FE9Data.CharacterClass.CAT.getJID()) - 0x20);
//			oscar.setUnpromotedAnimationPointer(0);
//			oscar.setPromotedAnimationPointer(dataFileHandler.pointerForString("AID_BEAST_RE"));
//			oscar.setSkill1Pointer(charData.addressLookup("SID_EQUIPFANG"));
//			oscar.setSkill2Pointer(charData.addressLookup("SID_CONTINUATION"));
//			oscar.setUnknown6Bytes(new byte[] {0x0, 0x0E, 0x00, 0x00, 0x10, 0x1E});
//			oscar.setUnknown8Bytes(new byte[] {0x50, 0x50, 0x00, 0x00, 0x50, 0x32, 0x50, 0x00});
//			
//			dataFileHandler.addPointerOffset(0x224);
//			dataFileHandler.addPointerOffset(0x228);
//			dataFileHandler.addString("---------");
//			dataFileHandler.commitAdditions();
//			
//			oscar.setWeaponLevelsPointer(dataFileHandler.pointerForString("---------"));
//			
//			oscar.commitChanges();
			//kieran.commitChanges();
			
			charData.compileDiffs(handler);
			//chapterData.commitChanges();
			
//			for (FE9ChapterArmy army : ch1) {
//				army.debugWriteDisposHandler(path + File.separator + "ch1_dispos" + File.separator + army.getID().replace("/", File.separator));
//			}
//			
//			try {
//				FileWriter.writeBinaryDataToFile(dataFileHandler.getRawData(), path + File.separator + "FE8Data.bin");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			
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
				updateProgress(progress);
			}
		});
		
		notifyCompletion(null);
	}
	
	private void randomizeGrowthsIfNecessary(String seed) {
		if (growthOptions != null) {
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9GrowthRandomizer.rngSalt));
			switch (growthOptions.mode) {
			case REDISTRIBUTE:
				FE9GrowthRandomizer.randomizeGrowthsByRedistribution(growthOptions.redistributionOption.variance, growthOptions.adjustHP, charData, classData, rng);
				break;
			case DELTA:
				FE9GrowthRandomizer.randomizeGrowthsByDelta(growthOptions.deltaOption.variance, charData, rng);
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
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9SkillRandomizer.rngSalt));
			switch (skillOptions.mode) {
			case RANDOMIZE_EXISTING:
				FE9SkillRandomizer.randomizeExistingSkills(skillOptions.skillWeights, charData, skillData, rng);
				break;
			case FULL_RANDOM:
				FE9SkillRandomizer.fullyRandomizeSkills(skillOptions.skillChance, skillOptions.skillWeights, charData, skillData, rng);
				break;
			}
			charData.commit();
		}
	}
	
	private void randomizeMiscellaneousIfNecessary(String seed) {
		if (miscOptions != null) {
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
	
	private void randomizeOtherCharacterOptionsIfNecessary(String seed) {
		if (otherCharOptions != null) {
			Random rng = new Random(SeedGenerator.generateSeedValue(seed, FE9MiscellaneousRandomizer.rngSalt));
			if (otherCharOptions.randomizeCON) {
				FE9MiscellaneousRandomizer.randomizeCON(otherCharOptions.conVariance, charData, classData, rng);
			}
			if (otherCharOptions.randomizeAffinity) {
				FE9MiscellaneousRandomizer.randomizeAffinity(charData, rng);
			}
		}
	}
}
