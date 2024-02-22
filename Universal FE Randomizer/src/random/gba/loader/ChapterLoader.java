package random.gba.loader;

import java.util.HashMap;
import java.util.Map;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapPortraitData;
import fedata.gba.GBAFEWorldMapSpriteData;
import fedata.gba.fe6.FE6Chapter;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe6.FE6WorldMapEvent;
import fedata.gba.fe7.FE7Chapter;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe7.FE7WorldMapEvent;
import fedata.gba.fe8.FE8Chapter;
import fedata.gba.fe8.FE8ChapterUnit;
import fedata.gba.fe8.FE8Data;
import fedata.gba.fe8.FE8WorldMapEvent;
import fedata.gba.general.CharacterNudge;
import fedata.gba.general.GBAFEChapterMetadataChapter;
import fedata.gba.general.GBAFEChapterMetadataData;
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
	
	private GBAFEChapterData[] chapters;
	private Map<Integer, GBAFEWorldMapData> worldMapEventsByChapterID = new HashMap<Integer, GBAFEWorldMapData>();
	private Map<Integer, GBAFEChapterData> mappedChapters = new HashMap<Integer, GBAFEChapterData>();
	
	private Map<GBAFEChapterMetadataChapter, GBAFEChapterMetadataData> metadataMap = new HashMap<GBAFEChapterMetadataChapter, GBAFEChapterMetadataData>();
	private Map<GBAFEChapterMetadataChapter, GBAFEChapterMetadataData> sideChapterMetadataMap = new HashMap<GBAFEChapterMetadataChapter, GBAFEChapterMetadataData>();
	
	public static final String RecordKeeperCategoryKey = "Chapters";

	public ChapterLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE6:
				int numberOfChapters = FE6Data.ChapterPointer.values().length;
				chapters = new GBAFEChapterData[numberOfChapters];
				int i = 0;
				long baseAddress = FileReadHelper.readAddress(handler, FE6Data.ChapterTablePointer);
				for (FE6Data.ChapterPointer chapter : FE6Data.ChapterPointer.orderedChapters()) {
					int chapterID = chapter.chapterID;
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
					FE6Chapter fe6Chapter = new FE6Chapter(handler, chapterOffset, chapter.isClassSafe(), chapter.shouldRemoveFightScenes(), trackedRewardRecipients, classBlacklist, chapter.getMetadata().getFriendlyName(), chapter.shouldBeEasy(), nudges); 
					chapters[i++] = fe6Chapter;
					mappedChapters.put(chapterID, fe6Chapter);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Chapter " + chapter.toString() + " loaded " + fe6Chapter.allUnits().length + " characters and " + fe6Chapter.allRewards().length + " rewards");
					
					if (chapter.hasWorldMapEvents()) {
						long worldMapOffset = baseAddress + (4 * chapter.worldMapEvents);
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading World Map Events for " + chapter.toString());
						FE6WorldMapEvent fe6WorldMapEvent = new FE6WorldMapEvent(handler, worldMapOffset);
						worldMapEventsByChapterID.put(chapterID, fe6WorldMapEvent);
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Chapter " + chapter.toString() + " loaded " + fe6WorldMapEvent.allPortraits().length + " world map portraits.");
					}
				}
				
				baseAddress = FileReadHelper.readAddress(handler, FE6Data.ChapterMetadataTablePointer);
				for (FE6Data.ChapterMetadata chapterMetadata : FE6Data.ChapterMetadata.orderedChapters()) {
					long offset = baseAddress + (chapterMetadata.index * FE6Data.BytesPerChapterMetadata);
					byte[] rawData = handler.readBytesAtOffset(offset, FE6Data.BytesPerChapterMetadata);
					GBAFEChapterMetadataData data = new GBAFEChapterMetadataData(rawData, offset);
					metadataMap.put(chapterMetadata, data);
				}
				break;
			case FE7:
				numberOfChapters = FE7Data.ChapterPointer.values().length;
				chapters = new GBAFEChapterData[numberOfChapters];
				i = 0;
				baseAddress = FileReadHelper.readAddress(handler, FE7Data.ChapterTablePointer);
				for (FE7Data.ChapterPointer chapter : FE7Data.ChapterPointer.orderedChapters()) {
					int chapterID = chapter.chapterID;
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
					FE7Chapter fe7Chapter = new FE7Chapter(handler, chapterOffset, chapter.isClassSafe(), chapter.shouldRemoveFightScenes(), trackedRewardRecipients, classBlacklist, chapter.getMetadata().getFriendlyName(), chapter.shouldBeEasy(), nudges); 
					chapters[i++] = fe7Chapter;
					mappedChapters.put(chapterID, fe7Chapter);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Chapter " + chapter.toString() + " loaded " + fe7Chapter.allUnits().length + " characters and " + fe7Chapter.allRewards().length + " rewards");
				}
				
				for (int j = 0; j < FE7Data.WorldMapEventCount; j++) {
					long offset = FE7Data.WorldMapEventTableOffset + (j * FE7Data.WorldMapEventItemSize);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading World Map Events from offset 0x" + Long.toHexString(offset));
					FE7WorldMapEvent fe7WorldMapEvent = new FE7WorldMapEvent(handler, offset);
					long dereferencedAddress = FileReadHelper.readAddress(handler, offset);
					FE7Data.ChapterPointer chapter = FE7Data.ChapterPointer.chapterForWorldMapEventOffset(dereferencedAddress);
					worldMapEventsByChapterID.put(chapter.chapterID, fe7WorldMapEvent);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loaded " + fe7WorldMapEvent.allPortraits().length + " world map portraits.");
				}
				
				baseAddress = FileReadHelper.readAddress(handler, FE7Data.ChapterMetadataTablePointer);
				for (FE7Data.ChapterMetadata chapterMetadata : FE7Data.ChapterMetadata.orderedChapters()) {
					long offset = baseAddress + (chapterMetadata.index * FE7Data.BytesPerChapterMetadata);
					byte[] rawData = handler.readBytesAtOffset(offset, FE7Data.BytesPerChapterMetadata);
					GBAFEChapterMetadataData data = new GBAFEChapterMetadataData(rawData, offset);
					metadataMap.put(chapterMetadata, data);
				}
				break;
			case FE8:
				numberOfChapters = FE8Data.ChapterPointer.values().length;
				chapters = new GBAFEChapterData[numberOfChapters];
				i = 0;
				baseAddress = FileReadHelper.readAddress(handler, FE8Data.ChapterTablePointer);
				for (FE8Data.ChapterPointer chapter : FE8Data.ChapterPointer.orderedChapters()) {
					int chapterID = chapter.chapterID;
					int[] classBlacklist = new int[chapter.blacklistedClasses().length];
					for (int index = 0; index < chapter.blacklistedClasses().length; index++) {
						classBlacklist[index] = chapter.blacklistedClasses()[index].ID;
					}
					int[] trackedRewardRecipients = new int[chapter.targetedRewardRecipientsToTrack().length];
					for (int index = 0; index < chapter.targetedRewardRecipientsToTrack().length; index++) {
						trackedRewardRecipients[index] = chapter.targetedRewardRecipientsToTrack()[index].ID;
					}
					
					int[] unarmedCharacterIDs = new int[chapter.unarmedUnits().length];
					for (int index = 0; index < chapter.unarmedUnits().length; index++) {
						unarmedCharacterIDs[index] = chapter.unarmedUnits()[index].ID;
					}
					
					CharacterNudge[] nudges = chapter.nudgesRequired();
					long chapterOffset = baseAddress + (4 * chapter.chapterID);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading " + chapter.toString());
					FE8Chapter fe8Chapter = new FE8Chapter(handler, chapterOffset, chapter.isClassSafe(), chapter.shouldRemoveFightScenes(), classBlacklist, chapter.getMetadata().getFriendlyName(), chapter.shouldBeEasy(), trackedRewardRecipients, unarmedCharacterIDs, chapter.additionalUnitOffsets(), nudges);
					fe8Chapter.setMaxEnemyClassLimit(chapter.enemyClassLimit());
					chapters[i++] = fe8Chapter;
					mappedChapters.put(chapterID, fe8Chapter);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Chapter " + chapter.toString() + " loaded " + fe8Chapter.allUnits().length + " characters and " + fe8Chapter.allRewards().length + " rewards");
				}
				
				for (int j = 0; j < FE8Data.WorldMapEventCount; j++) {
					long offset = FE8Data.WorldMapEventTableOffset + (j * FE8Data.WorldMapEventItemSize);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading World Map Events from offset 0x" + Long.toHexString(offset));
					FE8WorldMapEvent fe8WorldMapEvent = new FE8WorldMapEvent(handler, offset);
					long dereferencedAddress = FileReadHelper.readAddress(handler, offset);
					FE8Data.ChapterPointer chapter = FE8Data.ChapterPointer.chapterForWorldMapEventOffset(dereferencedAddress);
					if (chapter == null) { continue; }
					worldMapEventsByChapterID.put(chapter.chapterID, fe8WorldMapEvent);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loaded " + fe8WorldMapEvent.allPortraits().length + " world map portraits.");
				}
				baseAddress = FileReadHelper.readAddress(handler, FE8Data.ChapterMetadataTablePointer);
				for (FE8Data.ChapterMetadata chapterMetadata : FE8Data.ChapterMetadata.orderedChapters()) {
					long offset = baseAddress + (chapterMetadata.index * FE8Data.BytesPerChapterMetadata);
					byte[] rawData = handler.readBytesAtOffset(offset, FE8Data.BytesPerChapterMetadata);
					GBAFEChapterMetadataData data = new GBAFEChapterMetadataData(rawData, offset);
					metadataMap.put(chapterMetadata, data);
				}
				for (FE8Data.ChapterMetadata chapterMetadata : FE8Data.ChapterMetadata.additionalChapters()) {
					long offset = baseAddress + (chapterMetadata.index * FE8Data.BytesPerChapterMetadata);
					byte[] rawData = handler.readBytesAtOffset(offset, FE8Data.BytesPerChapterMetadata);
					GBAFEChapterMetadataData data = new GBAFEChapterMetadataData(rawData, offset);
					sideChapterMetadataMap.put(chapterMetadata, data);
				}
				break; 
			default:
				break;
		}
	}
	
	public GBAFEChapterData[] allChapters() {
		switch (gameType) {
			case FE6:
			case FE7:
			case FE8:
				return chapters;
			default:
				return new GBAFEChapterData[] {};
		}
	}
	
	public GBAFEWorldMapData[] allWorldMapEvents() {
		switch (gameType) {
		case FE6:
		case FE7:
		case FE8:
			return worldMapEventsByChapterID.values().toArray(new GBAFEWorldMapData[worldMapEventsByChapterID.values().size()]);
		default:
			return new GBAFEWorldMapData[] {};
		}
	}
	
	public GBAFEChapterMetadataChapter[] getMetadataChapters() {
		switch (gameType) {
		case FE6:
			return FE6Data.ChapterMetadata.orderedChapters();
		case FE7:
			return FE7Data.ChapterMetadata.orderedChapters();
		case FE8:
			return FE8Data.ChapterMetadata.orderedChapters();
		default:
			return null;
		}
	}
	
	public GBAFEChapterMetadataChapter[] getAdditionalMetadataChapters() {
		switch (gameType) {
		case FE8:
			return FE8Data.ChapterMetadata.additionalChapters();
		default:
			return null;
		}
	}
	
	public GBAFEChapterMetadataData getMetadataForChapter(GBAFEChapterMetadataChapter chapter) {
		if (metadataMap.get(chapter) != null) {
			return metadataMap.get(chapter);
		} else {
			return sideChapterMetadataMap.get(chapter);
		}
	}
	
	public GBAFEWorldMapData worldMapEventsForChapterID(int chapterID) {
		return worldMapEventsByChapterID.get(chapterID);
	}
	
	public GBAFEChapterData chapterWithID(int chapterID) {
		return mappedChapters.get(chapterID);
	}
	
	public int getStartingLevelForCharacter(int characterID) {
		for (GBAFEChapterData chapter : allChapters()) {
			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.getCharacterNumber() == characterID) {
					return unit.getStartingLevel();
				}
			}
		}
		
		return 0;
	}
	
	public GBAFEItemData getWeaponForUnit(GBAFEChapterUnitData unit, ItemDataLoader itemData) {
		GBAFEItemData item1 = itemData.itemWithID(unit.getItem1());
		GBAFEItemData item2 = itemData.itemWithID(unit.getItem2());
		GBAFEItemData item3 = itemData.itemWithID(unit.getItem3());
		GBAFEItemData item4 = itemData.itemWithID(unit.getItem4());
		if (itemData.isWeapon(item1)) { return item1; }
		if (itemData.isWeapon(item2)) { return item2; }
		if (itemData.isWeapon(item3)) { return item3; }
		if (itemData.isWeapon(item4)) { return item4; }
		return null;
	}
	
	public void commit() {
		for (GBAFEChapterData chapter : chapters) {
			chapter.applyNudges();
			GBAFEChapterUnitData[] units = chapter.allUnits();
			for (GBAFEChapterUnitData unit : units) {
				unit.commitChanges();
			}
			GBAFEChapterItemData[] rewards = chapter.allRewards();
			for (GBAFEChapterItemData item : rewards) {
				item.commitChanges();
			}
			GBAFEChapterItemData[] targetedRewards = chapter.allTargetedRewards();
			for (GBAFEChapterItemData item : targetedRewards) {
				item.commitChanges();
			}
		}
		
		for (GBAFEWorldMapData worldMapEvent : worldMapEventsByChapterID.values()) {
			for (GBAFEWorldMapPortraitData portrait : worldMapEvent.allPortraits()) {
				portrait.commitChanges();
			}
			for (GBAFEWorldMapSpriteData sprite : worldMapEvent.allSprites()) {
				sprite.commitChanges();
			}
		}
		
		for (GBAFEChapterMetadataData chapterMetadataData : metadataMap.values()) {
			chapterMetadataData.commitChanges();
		}
		
		for (GBAFEChapterMetadataData chapterMetadataData : sideChapterMetadataMap.values()) {
			chapterMetadataData.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (GBAFEChapterData chapter : chapters) {
			chapter.applyNudges();
			GBAFEChapterUnitData[] units = chapter.allUnits();
			for (GBAFEChapterUnitData unit : units) {
				unit.commitChanges();
				if (unit.hasCommittedChanges()) {
					byte[] unitData = unit.getData();
					Diff unitDiff = new Diff(unit.getAddressOffset(), unitData.length, unitData, null);
					compiler.addDiff(unitDiff);
				}
				// FE8 handles post move placements differently
				if (unit instanceof FE8ChapterUnit) {
					((FE8ChapterUnit)unit).getMovements().forEach(mvm -> {
						mvm.commitChanges();
						if(!mvm.hasCommittedChanges()) {
							return;
						}
						byte[] movementData = mvm.getData();
						Diff unitDiff = new Diff(mvm.getAddressOffset(), movementData.length, movementData, null);
						compiler.addDiff(unitDiff);
					});
				}
			}
			
			GBAFEChapterItemData[] rewards = chapter.allRewards();
			for (GBAFEChapterItemData item : rewards) {
				item.commitChanges();
				if (item.hasCommittedChanges()) {
					byte[] rewardData = item.getData();
					Diff rewardDiff = new Diff(item.getAddressOffset(), rewardData.length, rewardData, null);
					compiler.addDiff(rewardDiff);
				}
			}
			
			GBAFEChapterItemData[] targetedRewards = chapter.allTargetedRewards();
			for (GBAFEChapterItemData item : targetedRewards) {
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
		
		for (GBAFEWorldMapData worldMapEvent : worldMapEventsByChapterID.values()) {
			for (GBAFEWorldMapPortraitData portrait : worldMapEvent.allPortraits()) {
				portrait.commitChanges();
				if (portrait.hasCommittedChanges()) {
					byte[] portraitData = portrait.getData();
					Diff portraitDiff = new Diff(portrait.getAddressOffset(), portraitData.length, portraitData, null);
					compiler.addDiff(portraitDiff);
				}
			}
			for (GBAFEWorldMapSpriteData sprite : worldMapEvent.allSprites()) {
				sprite.commitChanges();
				if (sprite.hasCommittedChanges()) {
					byte[] spriteData = sprite.getData();
					Diff spriteDiff = new Diff(sprite.getAddressOffset(), spriteData.length, spriteData, null);
					compiler.addDiff(spriteDiff);
				}
			}
		}
		
		for (GBAFEChapterMetadataData metadata : metadataMap.values()) {
			metadata.commitChanges();
			if (metadata.hasCommittedChanges()) {
				byte[] updatedData = metadata.getData();
				Diff chapterMetadataDiff = new Diff(metadata.getAddressOffset(), updatedData.length, updatedData, null);
				compiler.addDiff(chapterMetadataDiff);
			}
		}
		
		for (GBAFEChapterMetadataData metadata : sideChapterMetadataMap.values()) {
			metadata.commitChanges();
			if (metadata.hasCommittedChanges()) {
				byte[] updatedData = metadata.getData();
				Diff chapterMetadataDiff = new Diff(metadata.getAddressOffset(), updatedData.length, updatedData, null);
				compiler.addDiff(chapterMetadataDiff);
			}
		}
	}
	
	public void recordChapters(RecordKeeper rk, Boolean isInitial, CharacterDataLoader charData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData) {
		for (GBAFEChapterData chapter : allChapters()) {
			recordChapter(rk, isInitial, chapter, charData, classData, itemData, textData);
		}
		
		for (GBAFEChapterMetadataChapter metadata : metadataMap.keySet()) {
			recordMetadata(rk, isInitial, metadata);
		}
	}
	
	private void recordMetadata(RecordKeeper rk, Boolean isInitial, GBAFEChapterMetadataChapter chapterMetadata) {
		String chapterName = chapterMetadata.getFriendlyName();
		GBAFEChapterMetadataData metadataData = metadataMap.get(chapterMetadata);
		
		if (isInitial) {
			rk.recordOriginalEntry(RecordKeeperCategoryKey, chapterName, "Fog Vision Range", metadataData.getVisionRange() != 0 ? Integer.toString(metadataData.getVisionRange()) : "None");
		} else {
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, chapterName, "Fog Vision Range", metadataData.getVisionRange() != 0 ? Integer.toString(metadataData.getVisionRange()) : "None");
		}
	}
	
	private void recordChapter(RecordKeeper rk, Boolean isInitial, GBAFEChapterData chapter, CharacterDataLoader charData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData) {
		String chapterName = chapter.getFriendlyName();
		
		int unitCounter = 1;
		for (GBAFEChapterUnitData unit : chapter.allUnits()) {
			if (isInitial) {
				rk.recordOriginalEntry(RecordKeeperCategoryKey, chapterName, "Unit #" + unitCounter, markupForUnit(rk, unit, charData, classData, itemData, textData));
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryKey, chapterName, "Unit #" + unitCounter, markupForUnit(rk, unit, charData, classData, itemData, textData));
			}
			unitCounter++;
		}
		
		int chestCounter = 1;
		int villageCounter = 1;
		for (GBAFEChapterItemData reward : chapter.allRewards()) {
			String key;
			if (reward.getRewardType() == GBAFEChapterItemData.Type.CHES) { 
				key = "Chest #" + chestCounter;
				chestCounter++;
			} else {
				key = "Village #" + villageCounter;
				villageCounter++;
			}
			
			GBAFEItemData item = itemData.itemWithID(reward.getItemID());
			
			if (isInitial) {
				rk.recordOriginalEntry(RecordKeeperCategoryKey, chapterName, key, (item != null ? textData.getStringAtIndex(item.getNameIndex(), true) : "Unknown (0x" + Integer.toHexString(reward.getItemID()).toUpperCase() + ")"));
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryKey, chapterName, key, (item != null ? textData.getStringAtIndex(item.getNameIndex(), true) : "Unknown (0x" + Integer.toHexString(reward.getItemID()).toUpperCase() + ")"));
			}
		}
		
		for (GBAFEChapterItemData targetedReward : chapter.allTargetedRewards()) {
			String key = "Targeted Item";
			GBAFEItemData item = itemData.itemWithID(targetedReward.getItemID());
			if (isInitial) {
				rk.recordOriginalEntry(RecordKeeperCategoryKey, chapterName, key, (item != null ? textData.getStringAtIndex(item.getNameIndex(), true) : "Unknown (0x" + Integer.toHexString(targetedReward.getItemID()).toUpperCase() + ")"));
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryKey, chapterName, key, (item != null ? textData.getStringAtIndex(item.getNameIndex(), true) : "Unknown (0x" + Integer.toHexString(targetedReward.getItemID()).toUpperCase() + ")"));
			}
		}
	}
	
	private String markupForUnit(RecordKeeper rk, GBAFEChapterUnitData chapterUnit, CharacterDataLoader charData, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData) {
		int characterID = chapterUnit.getCharacterNumber();
		GBAFECharacterData character = charData.characterWithID(characterID);
		GBAFEClassData charClass = classData.classForID(chapterUnit.getStartingClass());
		StringBuilder sb = new StringBuilder();
		sb.append("<table>\n");
		if (character == null) {
			// This is probably a minion.
			sb.append("<tr><td>Character ID</td><td>0x" + Integer.toHexString(chapterUnit.getCharacterNumber()).toUpperCase() + "</td></tr>\n");
		} else {
			// This is a somewhat important character.
			sb.append("<tr><td>Character ID</td><td>" + textData.getStringAtIndex(character.getNameIndex(), true) + " [0x" + Integer.toHexString(chapterUnit.getCharacterNumber()).toUpperCase() + "]</td></tr>\n");
		}
		
		sb.append("<tr><td>Class</td><td>" + (charClass != null ? textData.getStringAtIndex(charClass.getNameIndex(), true) + (classData.isFemale(charClass.getID()) ? " (F)" : "") + " [0x" + Integer.toHexString(charClass.getID()).toUpperCase() + "]" : "Unknown (0x" + Integer.toHexString(chapterUnit.getStartingClass()) + ")") + "</td></tr>\n");
		sb.append("<tr><td>Loading Coordinates</td><td>(" + chapterUnit.getStartingX() + ", " + chapterUnit.getStartingY() + ")</td></tr>\n");
		sb.append("<tr><td>Starting Coordinates</td><td>(" + chapterUnit.getPostMoveX() + ", " + chapterUnit.getPostMoveY() + ")</td></tr>\n");
		GBAFEItemData item1 = itemData.itemWithID(chapterUnit.getItem1());
		GBAFEItemData item2 = itemData.itemWithID(chapterUnit.getItem2());
		GBAFEItemData item3 = itemData.itemWithID(chapterUnit.getItem3());
		GBAFEItemData item4 = itemData.itemWithID(chapterUnit.getItem4());
		
		sb.append("<tr><td>Item 1</td><td>" + (item1 != null ? textData.getStringAtIndex(item1.getNameIndex(), true) : (chapterUnit.getItem1() != 0 ? "Unknown (0x" + Integer.toHexString(chapterUnit.getItem1()).toUpperCase() + ")" : "")) + "</td></tr>\n");
		sb.append("<tr><td>Item 2</td><td>" + (item2 != null ? textData.getStringAtIndex(item2.getNameIndex(), true) : (chapterUnit.getItem2() != 0 ? "Unknown (0x" + Integer.toHexString(chapterUnit.getItem2()).toUpperCase() + ")" : "")) + "</td></tr>\n");
		sb.append("<tr><td>Item 3</td><td>" + (item3 != null ? textData.getStringAtIndex(item3.getNameIndex(), true) : (chapterUnit.getItem3() != 0 ? "Unknown (0x" + Integer.toHexString(chapterUnit.getItem3()).toUpperCase() + ")" : "")) + "</td></tr>\n");
		sb.append("<tr><td>Item 4</td><td>" + (item4 != null ? textData.getStringAtIndex(item4.getNameIndex(), true) : (chapterUnit.getItem4() != 0 ? "Unknown (0x" + Integer.toHexString(chapterUnit.getItem4()).toUpperCase() + ")" : "")) + "</td></tr>\n");
		
		sb.append("</table>\n");
		return sb.toString();
	}
}
