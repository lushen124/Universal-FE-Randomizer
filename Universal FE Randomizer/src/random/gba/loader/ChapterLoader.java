package random.gba.loader;

import fedata.gba.GBAFEChapter;
import fedata.gba.GBAFEChapterItem;
import fedata.gba.GBAFEChapterUnit;
import fedata.gba.GBAFECharacter;
import fedata.gba.GBAFEClass;
import fedata.gba.GBAFEItem;
import fedata.gba.fe6.FE6Chapter;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Chapter;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Chapter;
import fedata.gba.fe8.FE8Data;
import fedata.gba.general.CharacterNudge;
import fedata.general.FEBase;
import io.FileHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.RecordKeeper;

public class ChapterLoader {
	
	private FEBase.GameType gameType;
	
	private GBAFEChapter[] chapters;
	
	public static final String RecordKeeperCategoryKey = "Chapters";

	public ChapterLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE6:
				int numberOfChapters = FE6Data.ChapterPointer.values().length;
				chapters = new GBAFEChapter[numberOfChapters];
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
				chapters = new GBAFEChapter[numberOfChapters];
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
			case FE8:
				numberOfChapters = FE8Data.ChapterPointer.values().length;
				chapters = new GBAFEChapter[numberOfChapters];
				i = 0;
				baseAddress = FileReadHelper.readAddress(handler, FE8Data.ChapterTablePointer);
				for (FE8Data.ChapterPointer chapter : FE8Data.ChapterPointer.values()) {
					int[] classBlacklist = new int[chapter.blacklistedClasses().length];
					for (int index = 0; index < chapter.blacklistedClasses().length; index++) {
						classBlacklist[index] = chapter.blacklistedClasses()[index].ID;
					}
					int[] trackedRewardRecipients = new int[chapter.targetedRewardRecipientsToTrack().length];
					for (int index = 0; index < chapter.targetedRewardRecipientsToTrack().length; index++) {
						trackedRewardRecipients[index] = chapter.targetedRewardRecipientsToTrack()[index].ID;
					}
					
					CharacterNudge[] nudges = chapter.nudgesRequired();
					long chapterOffset = baseAddress + (4 * chapter.chapterID);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading " + chapter.toString());
					FE8Chapter fe8Chapter = new FE8Chapter(handler, chapterOffset, chapter.isClassSafe(), chapter.shouldRemoveFightScenes(), classBlacklist, chapter.toString(), chapter.shouldBeEasy(), trackedRewardRecipients, chapter.additionalUnitOffsets(), nudges); 
					chapters[i++] = fe8Chapter;
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Chapter " + chapter.toString() + " loaded " + fe8Chapter.allUnits().length + " characters and " + fe8Chapter.allRewards().length + " rewards");
				}
				break; 
			default:
				break;
		}
	}
	
	public GBAFEChapter[] allChapters() {
		switch (gameType) {
			case FE6:
			case FE7:
			case FE8:
				return chapters;
			default:
				return new GBAFEChapter[] {};
		}
	}
	
	public void commit() {
		for (GBAFEChapter chapter : chapters) {
			chapter.applyNudges();
			GBAFEChapterUnit[] units = chapter.allUnits();
			for (GBAFEChapterUnit unit : units) {
				unit.commitChanges();
			}
			GBAFEChapterItem[] rewards = chapter.allRewards();
			for (GBAFEChapterItem item : rewards) {
				item.commitChanges();
			}
			GBAFEChapterItem[] targetedRewards = chapter.allTargetedRewards();
			for (GBAFEChapterItem item : targetedRewards) {
				item.commitChanges();
			}
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (GBAFEChapter chapter : chapters) {
			chapter.applyNudges();
			GBAFEChapterUnit[] units = chapter.allUnits();
			for (GBAFEChapterUnit unit : units) {
				unit.commitChanges();
				if (unit.hasCommittedChanges()) {
					byte[] unitData = unit.getData();
					Diff unitDiff = new Diff(unit.getAddressOffset(), unitData.length, unitData, null);
					compiler.addDiff(unitDiff);
				}
			}
			
			GBAFEChapterItem[] rewards = chapter.allRewards();
			for (GBAFEChapterItem item : rewards) {
				item.commitChanges();
				if (item.hasCommittedChanges()) {
					byte[] rewardData = item.getData();
					Diff rewardDiff = new Diff(item.getAddressOffset(), rewardData.length, rewardData, null);
					compiler.addDiff(rewardDiff);
				}
			}
			
			GBAFEChapterItem[] targetedRewards = chapter.allTargetedRewards();
			for (GBAFEChapterItem item : targetedRewards) {
				item.commitChanges();
				if (item.hasCommittedChanges()) {
					byte[] rewardData = item.getData();
					Diff rewardDiff = new Diff(item.getAddressOffset(), rewardData.length, rewardData, null);
					compiler.addDiff(rewardDiff);
				}
			}
			
			if (chapter.getFightAddresses() != null) {
				for (long fightOffset : chapter.getFightAddresses()) {
					Diff fightRemovalDiff = new Diff(fightOffset, chapter.fightCommandLength(), chapter.fightReplacementBytes(), null);
					compiler.addDiff(fightRemovalDiff);
				}
			}
		}
	}
	
	public void recordChapters(RecordKeeper rk, Boolean isInitial, CharacterDataLoader charData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData) {
		for (GBAFEChapter chapter : allChapters()) {
			recordChapter(rk, isInitial, chapter, charData, classData, itemData, textData);
		}
	}
	
	private void recordChapter(RecordKeeper rk, Boolean isInitial, GBAFEChapter chapter, CharacterDataLoader charData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData) {
		String chapterName = WhyDoesJavaNotHaveThese.stringByCapitalizingFirstLetter(chapter.getFriendlyName());
		
		int unitCounter = 1;
		for (GBAFEChapterUnit unit : chapter.allUnits()) {
			if (isInitial) {
				rk.recordOriginalEntry(RecordKeeperCategoryKey, chapterName, "Unit #" + unitCounter, markupForUnit(rk, unit, charData, classData, itemData, textData));
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryKey, chapterName, "Unit #" + unitCounter, markupForUnit(rk, unit, charData, classData, itemData, textData));
			}
			unitCounter++;
		}
		
		int chestCounter = 1;
		int villageCounter = 1;
		for (GBAFEChapterItem reward : chapter.allRewards()) {
			String key;
			if (reward.getRewardType() == GBAFEChapterItem.Type.CHES) { 
				key = "Chest #" + chestCounter;
				chestCounter++;
			} else {
				key = "Village #" + villageCounter;
				villageCounter++;
			}
			
			GBAFEItem item = itemData.itemWithID(reward.getItemID());
			
			if (isInitial) {
				rk.recordOriginalEntry(RecordKeeperCategoryKey, chapterName, key, (item != null ? textData.getStringAtIndex(item.getNameIndex()) : "Unknown (0x" + Integer.toHexString(reward.getItemID()).toUpperCase() + ")"));
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryKey, chapterName, key, (item != null ? textData.getStringAtIndex(item.getNameIndex()) : "Unknown (0x" + Integer.toHexString(reward.getItemID()).toUpperCase() + ")"));
			}
		}
		
		for (GBAFEChapterItem targetedReward : chapter.allTargetedRewards()) {
			String key = "Targeted Item";
			GBAFEItem item = itemData.itemWithID(targetedReward.getItemID());
			if (isInitial) {
				rk.recordOriginalEntry(RecordKeeperCategoryKey, chapterName, key, (item != null ? textData.getStringAtIndex(item.getNameIndex()) : "Unknown (0x" + Integer.toHexString(targetedReward.getItemID()).toUpperCase() + ")"));
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryKey, chapterName, key, (item != null ? textData.getStringAtIndex(item.getNameIndex()) : "Unknown (0x" + Integer.toHexString(targetedReward.getItemID()).toUpperCase() + ")"));
			}
		}
	}
	
	private String markupForUnit(RecordKeeper rk, GBAFEChapterUnit chapterUnit, CharacterDataLoader charData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData) {
		int characterID = chapterUnit.getCharacterNumber();
		GBAFECharacter character = charData.characterWithID(characterID);
		GBAFEClass charClass = classData.classForID(chapterUnit.getStartingClass());
		StringBuilder sb = new StringBuilder();
		sb.append("<table>\n");
		if (character == null) {
			// This is probably a minion.
			sb.append("<tr><td>Character ID</td><td>0x" + Integer.toHexString(chapterUnit.getCharacterNumber()).toUpperCase() + "</td></tr>\n");
		} else {
			// This is a somewhat important character.
			sb.append("<tr><td>Character ID</td><td>" + textData.getStringAtIndex(character.getNameIndex()) + "</td></tr>\n");
		}
		
		sb.append("<tr><td>Class</td><td>" + (charClass != null ? textData.getStringAtIndex(charClass.getNameIndex()) + (classData.isFemale(charClass.getID()) ? " (F)" : "") : "Unknown (0x" + Integer.toHexString(chapterUnit.getStartingClass()) + ")") + "</td></tr>\n");
		sb.append("<tr><td>Loading Coordinates</td><td>(" + chapterUnit.getLoadingX() + ", " + chapterUnit.getLoadingY() + ")</td></tr>\n");
		sb.append("<tr><td>Starting Coordinates</td><td>(" + chapterUnit.getStartingX() + ", " + chapterUnit.getStartingY() + ")</td></tr>\n");
		GBAFEItem item1 = itemData.itemWithID(chapterUnit.getItem1());
		GBAFEItem item2 = itemData.itemWithID(chapterUnit.getItem2());
		GBAFEItem item3 = itemData.itemWithID(chapterUnit.getItem3());
		GBAFEItem item4 = itemData.itemWithID(chapterUnit.getItem4());
		
		sb.append("<tr><td>Item 1</td><td>" + (item1 != null ? textData.getStringAtIndex(item1.getNameIndex()) : (chapterUnit.getItem1() != 0 ? "Unknown (0x" + Integer.toHexString(chapterUnit.getItem1()).toUpperCase() + ")" : "")) + "</td></tr>\n");
		sb.append("<tr><td>Item 2</td><td>" + (item2 != null ? textData.getStringAtIndex(item2.getNameIndex()) : (chapterUnit.getItem2() != 0 ? "Unknown (0x" + Integer.toHexString(chapterUnit.getItem2()).toUpperCase() + ")" : "")) + "</td></tr>\n");
		sb.append("<tr><td>Item 3</td><td>" + (item3 != null ? textData.getStringAtIndex(item3.getNameIndex()) : (chapterUnit.getItem3() != 0 ? "Unknown (0x" + Integer.toHexString(chapterUnit.getItem3()).toUpperCase() + ")" : "")) + "</td></tr>\n");
		sb.append("<tr><td>Item 4</td><td>" + (item4 != null ? textData.getStringAtIndex(item4.getNameIndex()) : (chapterUnit.getItem4() != 0 ? "Unknown (0x" + Integer.toHexString(chapterUnit.getItem4()).toUpperCase() + ")" : "")) + "</td></tr>\n");
		
		sb.append("</table>\n");
		return sb.toString();
	}
}
