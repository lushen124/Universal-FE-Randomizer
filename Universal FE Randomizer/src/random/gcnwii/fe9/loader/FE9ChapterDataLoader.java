package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterRewards;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Data;
import io.gcn.GCNCMBFileHandler;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;

public class FE9ChapterDataLoader {
	
	List<FE9ChapterArmy> allChapterArmies;
	Map<FE9Data.Chapter, List<FE9ChapterArmy>> armiesByChapter;
	
	List<FE9ChapterRewards> allChapterRewards;
	Map<FE9Data.Chapter, FE9ChapterRewards> rewardsByChapter;
	
	public FE9ChapterDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allChapterArmies = new ArrayList<FE9ChapterArmy>();
		armiesByChapter = new HashMap<FE9Data.Chapter, List<FE9ChapterArmy>>();
		
		allChapterRewards = new ArrayList<FE9ChapterRewards>();
		rewardsByChapter = new HashMap<FE9Data.Chapter, FE9ChapterRewards>();
		
		for (FE9Data.Chapter chapter : FE9Data.Chapter.values()) {
			List<FE9ChapterArmy> armyList = new ArrayList<FE9ChapterArmy>();
			for (String difficultyPath : chapter.getAllDifficulties()) {
				GCNFileHandler handler = isoHandler.handlerForFileWithName(difficultyPath);
				assert(handler instanceof GCNDataFileHandler);
				if (!(handler instanceof GCNDataFileHandler)) { continue; }
				GCNDataFileHandler dataFileHandler = (GCNDataFileHandler)handler;
				FE9ChapterArmy army = new FE9ChapterArmy(dataFileHandler, chapter, difficultyPath);
				allChapterArmies.add(army);
				armyList.add(army);
			}
			armiesByChapter.put(chapter, armyList);
			
			if (chapter.getScriptPath() != null) {
				GCNFileHandler handler = isoHandler.handlerForFileWithName(chapter.getScriptPath());
				assert(handler instanceof GCNCMBFileHandler);
				if (!(handler instanceof GCNCMBFileHandler)) { continue; }
				GCNCMBFileHandler cmbFileHandler = (GCNCMBFileHandler)handler;
				FE9ChapterRewards rewards = new FE9ChapterRewards(cmbFileHandler);
				allChapterRewards.add(rewards);
				rewardsByChapter.put(chapter, rewards);
			}
		}
	}
	
	public FE9ChapterRewards rewardsForChapter(FE9Data.Chapter chapter) {
		return rewardsByChapter.get(chapter);
	}
	
	public List<FE9ChapterRewards> getAllChapterRewards() {
		return allChapterRewards;
	}

	public void debugPrintAllChapterArmies() {
		for (FE9ChapterArmy army : allChapterArmies) {
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "===== Printing Chapter Army: " + army.getID() + " ======");
			for (String pid : army.getAllPIDs()) {
				FE9ChapterUnit unit = army.getUnitForPID(pid);
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "--- Starting Character " + pid + " ---");
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "PID: " + pid);
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "JID: " + army.getJIDForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Weapon 1: " + army.getWeapon1ForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Weapon 2: " + army.getWeapon2ForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Weapon 3: " + army.getWeapon3ForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Weapon 4: " + army.getWeapon4ForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Item 1: " + army.getItem1ForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Item 2: " + army.getItem2ForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Item 3: " + army.getItem3ForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Item 4: " + army.getItem4ForUnit(unit));
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Starting Coordinates: (" + army.getStartingXForUnit(unit) + ", " + army.getStartingYForUnit(unit) + ")");
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Ending Coordinates: (" + army.getEndingXForUnit(unit) + ", " + army.getEndingYForUnit(unit) + ")");
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "--- Ending Character " + pid + " ---");
			}
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "===== End Chapter Army: " + army.getID() + " =====");
		}
	}
	
	public List<FE9ChapterArmy> armiesForChapter(FE9Data.Chapter chapter) {
		return armiesByChapter.get(chapter);
	}
	
	public void commitChanges() {
		for (FE9ChapterArmy army : allChapterArmies) {
			army.commitChanges();
		}
	}
}
