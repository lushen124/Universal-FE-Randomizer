package random;

import fedata.FEBase;
import fedata.FEChapter;
import fedata.FEChapterItem;
import fedata.FEChapterUnit;
import fedata.fe6.FE6Chapter;
import fedata.fe6.FE6Data;
import fedata.fe7.FE7Chapter;
import fedata.fe7.FE7Data;
import io.FileHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;

public class ChapterLoader {
	
	private FEBase.GameType gameType;
	
	private FEChapter[] chapters;

	public ChapterLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE6:
				int numberOfChapters = FE6Data.ChapterPointer.values().length;
				chapters = new FEChapter[numberOfChapters];
				int i = 0;
				long baseAddress = FileReadHelper.readAddress(handler, FE6Data.ChapterTablePointer);
				for (FE6Data.ChapterPointer chapter : FE6Data.ChapterPointer.values()) {
					int[] classBlacklist = new int[chapter.blacklistedClasses().length];
					for (int index = 0; index < chapter.blacklistedClasses().length; index++) {
						classBlacklist[index] = chapter.blacklistedClasses()[index].ID;
					}
					long chapterOffset = baseAddress + (4 * chapter.chapterID);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading " + chapter.toString());
					FE6Chapter fe6Chapter = new FE6Chapter(handler, chapterOffset, chapter.isClassSafe(), chapter.shouldRemoveFightScenes(), classBlacklist, chapter.toString(), chapter.shouldBeEasy()); 
					chapters[i++] = fe6Chapter;
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Chapter " + chapter.toString() + " loaded " + fe6Chapter.allUnits().length + " characters and " + fe6Chapter.allRewards().length + " rewards");
				}
				break;
			case FE7:
				numberOfChapters = FE7Data.ChapterPointer.values().length;
				chapters = new FEChapter[numberOfChapters];
				i = 0;
				baseAddress = FileReadHelper.readAddress(handler, FE7Data.ChapterTablePointer);
				for (FE7Data.ChapterPointer chapter : FE7Data.ChapterPointer.values()) {
					int[] classBlacklist = new int[chapter.blacklistedClasses().length];
					for (int index = 0; index < chapter.blacklistedClasses().length; index++) {
						classBlacklist[index] = chapter.blacklistedClasses()[index].ID;
					}
					long chapterOffset = baseAddress + (4 * chapter.chapterID);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading " + chapter.toString());
					FE7Chapter fe7Chapter = new FE7Chapter(handler, chapterOffset, chapter.isClassSafe(), chapter.shouldRemoveFightScenes(), classBlacklist, chapter.toString(), chapter.shouldBeEasy()); 
					chapters[i++] = fe7Chapter;
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Chapter " + chapter.toString() + " loaded " + fe7Chapter.allUnits().length + " characters and " + fe7Chapter.allRewards().length + " rewards");
				}
				break;
			default:
				break;
		}
	}
	
	public FEChapter[] allChapters() {
		switch (gameType) {
			case FE6:
			case FE7:
				return chapters;
			default:
				return new FEChapter[] {};
		}
	}
	
	public void commit() {
		for (FEChapter chapter : chapters) {
			FEChapterUnit[] units = chapter.allUnits();
			for (FEChapterUnit unit : units) {
				unit.commitChanges();
			}
			FEChapterItem[] rewards = chapter.allRewards();
			for (FEChapterItem item : rewards) {
				item.commitChanges();
			}
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FEChapter chapter : chapters) {
			FEChapterUnit[] units = chapter.allUnits();
			for (FEChapterUnit unit : units) {
				unit.commitChanges();
				if (unit.hasCommittedChanges()) {
					byte[] unitData = unit.getData();
					Diff unitDiff = new Diff(unit.getAddressOffset(), unitData.length, unitData, null);
					compiler.addDiff(unitDiff);
				}
			}
			
			FEChapterItem[] rewards = chapter.allRewards();
			for (FEChapterItem item : rewards) {
				item.commitChanges();
				if (item.hasCommittedChanges()) {
					byte[] rewardData = item.getData();
					Diff rewardDiff = new Diff(item.getAddressOffset(), rewardData.length, rewardData, null);
					compiler.addDiff(rewardDiff);
				}
			}
			
			for (long fightOffset : chapter.getFightAddresses()) {
				Diff fightRemovalDiff = new Diff(fightOffset, chapter.fightCommandLength(), chapter.fightReplacementBytes(), null);
				compiler.addDiff(fightRemovalDiff);
			}
		}
	}
}
