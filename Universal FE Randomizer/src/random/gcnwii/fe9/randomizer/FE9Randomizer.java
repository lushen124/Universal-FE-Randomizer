package random.gcnwii.fe9.randomizer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Data;
import io.FileHandler;
import io.FileWriter;
import io.gcn.GCNCMPFileHandler;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNFSTEntry;
import io.gcn.GCNFSTFileEntry;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import io.gcn.GCNISOHandlerRecompilationDelegate;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9CommonTextLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;
import random.general.Randomizer;
import ui.model.BaseOptions;
import ui.model.GrowthOptions;
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
	
	FE9CommonTextLoader textData;
	FE9CharacterDataLoader charData;
	FE9ClassDataLoader classData;
	FE9ItemDataLoader itemData;
	FE9SkillDataLoader skillData;
	
	public FE9Randomizer(String sourcePath, String targetPath, GrowthOptions growthOptions, BaseOptions baseOptions, String seed) {
		super();
		
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		
		this.growthOptions = growthOptions;
		this.baseOptions = baseOptions;
		
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
//			List<GCNFSTEntry> disposEntries = handler.entriesWithFilename("dispos.cmp");
//			for (GCNFSTEntry entry : disposEntries) {
//				String fullName = handler.fstNameOfEntry(entry);
//				fullName = fullName.replace("/", "_");
//				String disposPath = path + File.separator + "dispos" + File.separator + fullName;
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
//			notifyError("Failed to extract dispos.cmp files.");
//			return;
//		}
		
		try {
			textData = new FE9CommonTextLoader(handler);
			charData = new FE9CharacterDataLoader(handler, textData);
			classData = new FE9ClassDataLoader(handler, textData);
			itemData = new FE9ItemDataLoader(handler, textData);
			skillData = new FE9SkillDataLoader(handler, textData);
			
			randomizeGrowthsIfNecessary(seed);
			randomizeBasesIfNecessary(seed);
			
			//FE9Character kieran = charData.characterWithID(FE9Data.Character.KIERAN.getPID());
			FE9Character oscar = charData.characterWithID(FE9Data.Character.OSCAR.getPID());
			
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
			oscar.setSkill1Pointer(charData.addressLookup("SID_CONTINUATION"));
			GCNFileHandler fe8databin = handler.handlerForFileWithName("system.cmp/FE8Data.bin");
			//fe8databin.addChange(new Diff(0x1CE2C, 4, new byte[] {0, 0, (byte)0x02, (byte)0x24}, new byte[] {0, 0, (byte)0x05, (byte)0x6C}));
			assert(fe8databin instanceof GCNDataFileHandler);
			GCNDataFileHandler dataFileHandler = (GCNDataFileHandler)fe8databin;
			dataFileHandler.addPointerOffset(0x224);
			dataFileHandler.commitAdditions();
			
			try {
				FileWriter.writeBinaryDataToFile(dataFileHandler.getRawData(), path + File.separator + "FE8Data" + File.separator + "FE8Data.bin");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			oscar.commitChanges();
			//kieran.commitChanges();
			
			charData.compileDiffs(handler);
			
			
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
			charData.commit();
		}
	}
}
