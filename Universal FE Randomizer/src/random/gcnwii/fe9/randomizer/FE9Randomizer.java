package random.gcnwii.fe9.randomizer;

import java.io.File;
import java.io.IOException;

import io.FileHandler;
import io.FileWriter;
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
import util.DebugPrinter;
import util.LZ77;
import util.WhyDoesJavaNotHaveThese;

public class FE9Randomizer extends Randomizer {
	private String sourcePath;
	private String targetPath;
	
	private String seedString;
	
	private GCNISOHandler handler;
	
	public FE9Randomizer(String sourcePath, String targetPath, String seed) {
		super();
		
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		
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
		
		GCNFileHandler systemCMP;
		try {
			systemCMP = handler.handlerForFileWithName("system.cmp");
		} catch (GCNISOException e) {
			notifyError("Failed to read filesystem file: system.cmp");
			return;
		}
		
//		DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "System.CMP: " + WhyDoesJavaNotHaveThese.displayStringForBytes(systemCMP.readBytesAtOffset(0, 0x10)));
//		byte[] systemData = systemCMP.readBytesAtOffset(0, (int)systemCMP.getFileLength());
//		byte[] decompressedSystemData = LZ77.decompress(systemData);
//		int indexOfPathSeparator = targetPath.lastIndexOf(File.separator);
//		String path = targetPath.substring(0, indexOfPathSeparator);
//		
//		try {
//			FileWriter.writeBinaryDataToFile(decompressedSystemData, path + File.separator + "decomp_system.cmp");
//			FileWriter.writeBinaryDataToFile(LZ77.compress(decompressedSystemData, 0xFFF), path + File.separator + "recomp_system.cmp");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			FE9CommonTextLoader textData = new FE9CommonTextLoader(handler);
			FE9CharacterDataLoader charData = new FE9CharacterDataLoader(handler, textData);
			FE9ClassDataLoader classData = new FE9ClassDataLoader(handler, textData);
			FE9ItemDataLoader itemData = new FE9ItemDataLoader(handler, textData);
			FE9SkillDataLoader skillData = new FE9SkillDataLoader(handler, textData);
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
}
