package random.snes.fe4.randomizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fedata.snes.fe4.FE4Data;
import io.DiffApplicator;
import io.FileHandler;
import io.UPSPatcher;
import random.general.Randomizer;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.ItemMapper;
import ui.model.MiscellaneousOptions;
import util.DiffCompiler;
import util.recordkeeper.RecordKeeper;

public class FE4Randomizer extends Randomizer {
	
	private String sourcePath;
	private boolean isHeadered;
	private String targetPath;
	
	private MiscellaneousOptions miscOptions;
	
	CharacterDataLoader charData;
	ItemMapper itemMapper;
	
	private String seedString;
	
	private DiffCompiler diffCompiler;
	
	private FileHandler handler;
	
	public FE4Randomizer(String sourcePath, boolean isHeadered, String targetPath, DiffCompiler diffs, MiscellaneousOptions miscOptions, String seed) {
		super();
		
		this.sourcePath = sourcePath;
		this.isHeadered = isHeadered;
		this.targetPath = targetPath;
		
		this.seedString = seed;
		this.diffCompiler = diffs;
		
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
		generateDataLoaders();
		
		RecordKeeper recordKeeper = initializeRecordKeeper();
		recordKeeper.addHeaderItem("Randomizer Seed Phrase", seed);
		
		updateStatusString("Randomizing...");
		
		updateStatusString("Compiling changes...");
		updateProgress(0.95);
		
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
		
		recordKeeper.sortKeysInCategoryAndSubcategories(CharacterDataLoader.RecordKeeperCategoryKey);
		
		updateStatusString("Done!");
		updateProgress(1);
		notifyCompletion(recordKeeper);
	}

	private void generateDataLoaders() {
		charData = new CharacterDataLoader(handler, isHeadered);
		itemMapper = new ItemMapper(handler, isHeadered);
	}
	
	public RecordKeeper initializeRecordKeeper() {
		int index = Math.max(targetPath.lastIndexOf('/'), targetPath.lastIndexOf('\\'));
		String title =  targetPath.substring(index + 1);
		String gameTitle = FE4Data.FriendlyName;
		
		RecordKeeper rk = new RecordKeeper(title);
		
		rk.addHeaderItem("Game Title", gameTitle);
		
		charData.recordCharacters(rk, true, itemMapper);
		
		return rk;
	}
}
