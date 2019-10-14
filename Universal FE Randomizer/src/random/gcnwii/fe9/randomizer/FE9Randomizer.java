package random.gcnwii.fe9.randomizer;

import java.io.File;
import java.io.IOException;

import io.FileHandler;
import io.FileWriter;
import io.GCNISOException;
import io.GCNISOHandler;
import io.GCNISOHandler.GCNFileHandler;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9CommonTextLoader;
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
//			FileWriter.writeBinaryDataToFile(decompressedSystemData, path + File.separator + "system.cmp");
//			FileWriter.writeBinaryDataToFile(LZ77.compress(decompressedSystemData, 0xFFF), path + File.separator + "recomp_system.cmp");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			FE9CommonTextLoader textData = new FE9CommonTextLoader(handler);
			FE9CharacterDataLoader charData = new FE9CharacterDataLoader(handler, textData);
			FE9ClassDataLoader classData = new FE9ClassDataLoader(handler, textData);
		} catch (GCNISOException e1) {
			notifyError("Failed to load character data.");
			return;
		}
	}
}
