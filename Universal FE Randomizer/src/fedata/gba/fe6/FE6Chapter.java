package fedata.gba.fe6;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.general.CharacterNudge;
import io.FileHandler;
import util.DebugPrinter;
import util.FileReadHelper;
import util.WhyDoesJavaNotHaveThese;

public class FE6Chapter implements GBAFEChapterData {
	
	private String friendlyName;
	
	@SuppressWarnings("unused")
	private Boolean removeFightScenes;
	
	private Boolean isClassSafe;
	private Boolean shouldBeSimplified;
	
	private long turnBasedEventsOffset;
	private long characterBasedEventsOffset;
	private long locationBasedEventsOffset;
	private long miscEventsOffset;
	private long enemyUnitsOffset;
	private long allyUnitsOffset;
	private long endingSceneOffset;
	
	private List<FE6ChapterUnit> allChapterUnits;
	private List<FE6ChapterItem> allChapterRewards;
	
	private Set<Integer> blacklistedClassIDs;
	private Set<Long> fightEventOffsets;
	
	private Set<Integer> knownAllyIDs;
	private Set<Integer> knownEnemyIDs;
	private int probableLordID = 0;
	private int probableBossID = 0;
	
	private CharacterNudge[] nudges;
	
	private int maxEnemyClassLimit = 0;
	
	public FE6Chapter(FileHandler handler, long pointer, Boolean isClassSafe, Boolean removeFightScenes, int[] blacklistedClassIDs, String friendlyName, Boolean simple, CharacterNudge[] nudgesRequired) {
		this.friendlyName = friendlyName;
		this.blacklistedClassIDs = new HashSet<Integer>();
		for (int classID : blacklistedClassIDs) {
			this.blacklistedClassIDs.add(classID);
		}
		this.removeFightScenes = removeFightScenes;
		this.shouldBeSimplified = simple;
				
		// We need one jump.
		long pointerTableOffset = FileReadHelper.readAddress(handler, pointer);
		this.isClassSafe = isClassSafe;
		
		long currentOffset = pointerTableOffset;
		turnBasedEventsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		characterBasedEventsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		locationBasedEventsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		miscEventsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		enemyUnitsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		allyUnitsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		endingSceneOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		
		allChapterUnits = new ArrayList<FE6ChapterUnit>();
		allChapterRewards = new ArrayList<FE6ChapterItem>();
		
		fightEventOffsets = new HashSet<Long>();
		
		knownAllyIDs = new HashSet<Integer>();
		knownEnemyIDs = new HashSet<Integer>();
		
		nudges = nudgesRequired;
		
		loadUnits(handler);
		loadRewards(handler);
	}

	@Override
	public int lordLeaderID() {
		return probableLordID;
	}

	@Override
	public int bossLeaderID() {
		return probableBossID;
	}

	@Override
	public GBAFEChapterUnitData[] allUnits() {
		return allChapterUnits.toArray(new GBAFEChapterUnitData[allChapterUnits.size()]);
	}

	@Override
	public Boolean isClassSafe() {
		return isClassSafe;
	}

	@Override
	public GBAFEChapterItemData[] allRewards() {
		return allChapterRewards.toArray(new GBAFEChapterItemData[allChapterRewards.size()]);
	}
	
	public GBAFEChapterItemData[] allTargetedRewards() {
		return new GBAFEChapterItemData[] {};
	}

	@Override
	public long[] getFightAddresses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int fightCommandLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] fightReplacementBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFriendlyName() {
		return friendlyName;
	}

	@Override
	public Boolean shouldBeSimplified() {
		return shouldBeSimplified;
	}
	
	@Override
	public Boolean shouldCharacterBeUnarmed(int characterID) {
		return false;
	}
	
	public void applyNudges() {
		if (nudges == null) { return; }
		for (CharacterNudge nudge : nudges) {
			characterLoop : for (GBAFEChapterUnitData unit : allUnits()) {
				if (unit.getCharacterNumber() != nudge.getCharacterID() ) {
					continue;
				}
				if (unit.getStartingX() == nudge.getOldX() && unit.getStartingY() == nudge.getOldY()) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Nudging character PreMove 0x" + Integer.toHexString(unit.getCharacterNumber()) + " from (" + unit.getPostMoveX() + ", " + unit.getPostMoveY() + ") to (" + nudge.getNewX() + ", " + nudge.getNewY() + ")");
					if (unit.getPostMoveX() == unit.getStartingX() && unit.getPostMoveY() == unit.getStartingY()) { 
						unit.setPostMoveX(nudge.getNewX());
						unit.setPostMoveY(nudge.getNewY());
					}
					unit.setStartingX(nudge.getNewX());
					unit.setStartingY(nudge.getNewY());
					break characterLoop;
				} else if(unit.getPostMoveX() == nudge.getOldX() && unit.getPostMoveY() == nudge.getOldY()) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Nudging character PostMove 0x" + Integer.toHexString(unit.getCharacterNumber()) + " from (" + unit.getPostMoveX() + ", " + unit.getPostMoveY() + ") to (" + nudge.getNewX() + ", " + nudge.getNewY() + ")");
					if (unit.getPostMoveX() == unit.getStartingX() && unit.getPostMoveY() == unit.getStartingY()) { 
						unit.setStartingX(nudge.getNewX());
						unit.setStartingX(nudge.getNewY());
					}
					unit.setPostMoveX(nudge.getNewX());
					unit.setPostMoveY(nudge.getNewY());
					break characterLoop;
				}
				
			}
		}
	}
	
	private void loadUnits(FileHandler handler) {
		Set<Long> addressesSearched = new HashSet<Long>();
		// Look in the obvious places first.
		loadUnitsFromAddress(handler, allyUnitsOffset);
		addressesSearched.add(allyUnitsOffset);
		loadUnitsFromAddress(handler, enemyUnitsOffset);
		addressesSearched.add(enemyUnitsOffset);
		
		Set<Long> eventAddresses = eventAddressesFromTurnEvents(handler);
		eventAddresses.addAll(eventAddressesFromCharacterEvents(handler));
		eventAddresses.addAll(eventAddressesFromLocationEvents(handler));
		eventAddresses.addAll(eventAddressesFromMiscEvents(handler));
		eventAddresses.add(endingSceneOffset);
		for (long eventAddress : eventAddresses) {
			if (eventAddress == 0x1) { continue; } // Some events use 1 for some reason. LOCA and DOOR do this sometimes.
			Set<Long> unitAddresses = unitAddressesFromEventBlob(handler, eventAddress);
			for (long unitAddress : unitAddresses) {
				if (!addressesSearched.contains(unitAddress)) {
					addressesSearched.add(unitAddress);
					loadUnitsFromAddress(handler, unitAddress);
				}
			}
			
			recordFightAddressesFromEventBlob(handler, eventAddress);
		}
	}
	
	private Set<Long> eventAddressesFromTurnEvents(FileHandler handler) {
		Set<Long> eventAddresses = new HashSet<Long>();
		byte[] turnCommand;
		long currentAddress = turnBasedEventsOffset;
		turnCommand = handler.readBytesAtOffset(currentAddress, 12);
		while (turnCommand[0] == 0x02 || turnCommand[0] == 0x0D || turnCommand[0] == 0x03 || turnCommand[0] == 0x01) {
			// TURN (0x02) - 12 bytes - offset starts at byte 4 and is 4 bytes long.
			// ASME (0x0D) - 12 bytes - same
			// TURN_HM (0x03) - 12 bytes - same
			// AFEV (0x01) - 12 bytes - same
			long address = FileReadHelper.readAddress(handler, currentAddress + 4);
			if (address != -1) {
				eventAddresses.add(address);
			}
			currentAddress += 12;
			turnCommand = handler.readBytesAtOffset(currentAddress, 12);
		}
		if (turnCommand[0] != 0x00) {
			System.err.println("Unhandled turn event type detected.");
		}
		
		return eventAddresses;
	}
	
	private Set<Long> eventAddressesFromCharacterEvents(FileHandler handler) {
		Set<Long> eventAddresses = new HashSet<Long>();
		byte[] charCommand;
		long currentAddress = characterBasedEventsOffset;
		charCommand = handler.readBytesAtOffset(currentAddress, 12);
		while (charCommand[0] == 0x0D || charCommand[0] == 0x04) {
			// CHAR (0x04), ASME (0x0D) - 12 bytes - offset starts at byte 4 and is 4 bytes long.
			long address = FileReadHelper.readAddress(handler, currentAddress + 4);
			if (address != -1) {
				eventAddresses.add(address);
			}
			
			currentAddress += 12;
			charCommand = handler.readBytesAtOffset(currentAddress, 12);
		}
		if (charCommand[0] != 0x00) {
			System.err.println("Unhandled character event type detected.");
		}
		
		return eventAddresses;
	}
	
	private Set<Long> eventAddressesFromLocationEvents(FileHandler handler) {
		Set<Long> eventAddresses = new HashSet<Long>();
		byte[] locationCommand;
		long currentAddress = locationBasedEventsOffset;
		locationCommand = handler.readBytesAtOffset(currentAddress, 12);
		// LOCA (0x05) - 12 bytes, offset 4
		// VILL (0x06) - 12 bytes, offset 4
		// DOOR (0x08) - 12 bytes, offset 4 (this is less likely, but possible).
		
		// The ones that don't matter for events:
		// SHOP (0x0A)
		// CHES (0x07)
		while (locationCommand[0] == 0x05 || locationCommand[0] == 0x06 || locationCommand[0] == 0x0A || locationCommand[0] == 0x07 || locationCommand[0] == 0x08) {
			if (locationCommand[0] == 0x05 || locationCommand[0] == 0x06 || locationCommand[0] == 0x08) {
				long address = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (address != -1) {
					eventAddresses.add(address);
				}
			}
			currentAddress += 12;
			locationCommand = handler.readBytesAtOffset(currentAddress, 12);
		}
		if (locationCommand[0] != 0x00) {
			System.err.println("Unhandled character event type detected.");
		}
		
		return eventAddresses;
	}
	
	private Set<Long> eventAddressesFromMiscEvents(FileHandler handler) {
		Set<Long> eventAddresses = new HashSet<Long>();
		byte[] miscCommand;
		long currentAddress = miscEventsOffset;
		miscCommand = handler.readBytesAtOffset(currentAddress, 12);
		while (miscCommand[0] != 0x00) {
			// AREA (0x0B) - 12 bytes, offset 4
			// ASME (0x0D) - 12 bytes, offset 4
			// AFEV (0x01) - 12 bytes, offset 4
			if (miscCommand[0] == 0x0B || miscCommand[0] == 0x01 || miscCommand[0] == 0x0D) {
				long address = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (address != -1) {
					eventAddresses.add(address);
				}
			} else {
				System.err.println("Unhandled misc event type detected.");
			}
			
			currentAddress += 12;
			miscCommand = handler.readBytesAtOffset(currentAddress, 12);
		}
		
		return eventAddresses;
	}
	
	private void recordFightAddressesFromEventBlob(FileHandler handler, long eventAddress) {
		if (eventAddress >= 0x1000000) { return; }
		
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Searching for fights beginning at 0x" + Long.toHexString(eventAddress));
		
		byte[] commandWord;
		long currentAddress = eventAddress;
		commandWord = handler.readBytesAtOffset(currentAddress, 4);
		while (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x06, 0x00, 0x00, 0x00})) {
			if (WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x49, 0x00, 0x00, 0x00})) {
				// FIGH - Always has command byte 0x49, and is always 20 length. The second word always contains attacker and defender IDs.
				// They vary after that, but those aren't important.
				long address = FileReadHelper.readAddress(handler, currentAddress + 12);
				if (address != -1) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found FIGH at 0x" + Long.toHexString(currentAddress));
					fightEventOffsets.add(currentAddress);
				}
				currentAddress += 16;
			}
			
			if (commandWord[1] == 0 && commandWord[2] == 0 && commandWord[3] == 0) {
				// BACG (0x03), LABEL (0x1B), MUSC (0x36), MUSS (0x37), DISA (0x30), ENUT (0x34), ENUF (0x35), STAL (0x02), CAM1(0xB, 0xC), CURF(0x2C, 0x2D), TEX1(0x7), MORETEXT(0x8), MNCH(0x3D), GOTO(0x1C) - 8 bytes
				if (commandWord[0] == 0x03 || commandWord[0] == 0x1B || commandWord[0] == 0x36 || commandWord[0] == 0x37 || commandWord[0] == 0x30 ||
						commandWord[0] == 0x34 || commandWord[0] == 0x35 || commandWord[0] == 0x02 || commandWord[0] == 0x0B || commandWord[0] == 0x0C ||
						commandWord[0] == 0x2C || commandWord[0] == 0x2D || commandWord[0] == 0x07 || commandWord[0] == 0x08 || commandWord[0] == 0x3D ||
						commandWord[0] == 0x1C) { 
					currentAddress += 4;
				} else if (commandWord[0] == 0x20 || commandWord[0] == 0x23 || commandWord[0] == 0x22) { // IFAF (0x20), IFEF (0x23), GOTO_IFET (0x22) - 12 bytes
					currentAddress += 8;
				} else if (commandWord[0] == 0x3F) { // LOMA (0x3F) - 16 bytes
					currentAddress += 12;
				}
			}
			
			currentAddress += 4;
			commandWord = handler.readBytesAtOffset(currentAddress, 4);
		}
		
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Finished searching for fights at 0x" + Long.toHexString(currentAddress));
	}
	
	private Set<Long> unitAddressesFromEventBlob(FileHandler handler, long eventAddress) {
		Set<Long> addressesLoaded = new HashSet<Long>();
		if (eventAddress >= 0x1000000) { return addressesLoaded; }
		
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Searching for unit addresses beginning at 0x" + Long.toHexString(eventAddress));
		
		byte[] commandWord;
		long currentAddress = eventAddress;
		commandWord = handler.readBytesAtOffset(currentAddress, 4);
		while (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x06, 0x00, 0x00, 0x00})) {
			if (commandWord[1] == 0 && commandWord[2] == 0 && commandWord[3] == 0) {
				if (commandWord[0] == 0x12 || commandWord[0] == 0x13) {
					// LOU1 - 0x12 key. Pointer at byte 4, length 4 - total 8 bytes.
					// LOU2 - 0x13 key. Same as LOU1.
					long address = FileReadHelper.readAddress(handler, currentAddress + 4);
					if (address != -1) {
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOU1 or LOU2 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: " + Long.toHexString(address));
						addressesLoaded.add(address);
					}
					currentAddress += 4;
				}
				
				DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Processed command 0x" + Integer.toHexString(commandWord[0] & 0xFF));
			
				// BACG (0x03), LABEL (0x1B), MUSC (0x36), MUSS (0x37), DISA (0x30), ENUT (0x34), ENUF (0x35), STAL (0x02), CAM1(0xB, 0xC), CURF(0x2C, 0x2D), TEX1(0x7), MORETEXT(0x8), MNCH(0x3D), GOTO(0x1C) - 8 bytes
				if (commandWord[0] == 0x03 || commandWord[0] == 0x1B || commandWord[0] == 0x36 || commandWord[0] == 0x37 || commandWord[0] == 0x30 ||
						commandWord[0] == 0x34 || commandWord[0] == 0x35 || commandWord[0] == 0x02 || commandWord[0] == 0x0B || commandWord[0] == 0x0C ||
						commandWord[0] == 0x2C || commandWord[0] == 0x2D || commandWord[0] == 0x07 || commandWord[0] == 0x08 || commandWord[0] == 0x3D ||
						commandWord[0] == 0x1C) { 
					currentAddress += 4;
				} else if (commandWord[0] == 0x20 || commandWord[0] == 0x23 || commandWord[0] == 0x22) { // IFAF (0x20), IFEF (0x23), GOTO_IFET (0x22) - 12 bytes
					currentAddress += 8;
				} else if (commandWord[0] == 0x3F) { // LOMA (0x3F) - 16 bytes
					currentAddress += 12;
				}
			}
			
			currentAddress += 4;
			commandWord = handler.readBytesAtOffset(currentAddress, 4);
		}
		
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Finished searching for loading units at address 0x" + Long.toHexString(currentAddress));
		
		return addressesLoaded;
	}
	
	private void loadUnitsFromAddress(FileHandler handler, long unitAddress) {
		if (unitAddress >= 0x1000000) { return; }
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading units from 0x" + Long.toHexString(unitAddress));
		long currentOffset = unitAddress;
		byte[] unitData = handler.readBytesAtOffset(currentOffset, FE6Data.BytesPerChapterUnit);
		while (unitData[0] != 0x00) {
			DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loaded unit with data " + WhyDoesJavaNotHaveThese.displayStringForBytes(unitData));
			FE6ChapterUnit unit = new FE6ChapterUnit(unitData, currentOffset); 
			if (!blacklistedClassIDs.contains(unit.getStartingClass())) { // Remove any characters starting as a blacklisted class from consideration.
				allChapterUnits.add(unit);
				
				if (unitAddress == allyUnitsOffset) {
					knownAllyIDs.add(unit.getCharacterNumber());
					if (unit.getLeaderID() == 0) { probableLordID = unit.getCharacterNumber(); }
				}
				if (unitAddress == enemyUnitsOffset) {
					knownEnemyIDs.add(unit.getCharacterNumber());
					if (unit.getLeaderID() == 0) { probableBossID = unit.getCharacterNumber(); }
				}
			}
			
			currentOffset += FE6Data.BytesPerChapterUnit;
			unitData = handler.readBytesAtOffset(currentOffset, FE6Data.BytesPerChapterUnit);
		}
	}
	
	private void loadRewards(FileHandler handler) {
		byte[] locationCommand;
		long currentAddress = locationBasedEventsOffset;
		locationCommand = handler.readBytesAtOffset(currentAddress, 12); // These events are only 12 bytes long.
		while (locationCommand[0] == 0x05 || locationCommand[0] == 0x06 || locationCommand[0] == 0x0A || locationCommand[0] == 0x07 || locationCommand[0] == 0x08) {
			if (locationCommand[0] == 0x07) {
				// We only care about CHES events.
				// CHES includes money too, which we don't want, so only filter to those
				// without money. The ID should only be in byte 4. Bytes 5, 6, and 7 should be 00s.
				if (locationCommand[4] != 0x00 && locationCommand[5] == 0 && locationCommand[6] == 0 && locationCommand[7] == 0) {
					allChapterRewards.add(new FE6ChapterItem(locationCommand, currentAddress));
				}
			}
			
			currentAddress += 12;
			locationCommand = handler.readBytesAtOffset(currentAddress, 12);
		}
		if (locationCommand[0] != 0x00) {
			System.err.println("Unhandled character event type detected.");
		}
		
		Set<Long> eventAddresses = eventAddressesFromTurnEvents(handler);
		eventAddresses.addAll(eventAddressesFromCharacterEvents(handler));
		eventAddresses.addAll(eventAddressesFromLocationEvents(handler));
		eventAddresses.addAll(eventAddressesFromMiscEvents(handler));
		eventAddresses.add(endingSceneOffset);
		for (long eventOffset : eventAddresses) {
			loadRewardsFromEventBlob(handler, eventOffset);
		}
	}
	
	private void loadRewardsFromEventBlob(FileHandler handler, long eventOffset) {
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Searching for rewards beginning at 0x" + Long.toHexString(eventOffset));
		byte[] commandWord;
		long currentAddress = eventOffset;
		commandWord = handler.readBytesAtOffset(currentAddress, 4);
		while (commandWord[0] != 0x06) {
			// We just need ITGV.
			if (commandWord[0] == 0x26) {
				FE6ChapterItem chapterItem = new FE6ChapterItem(handler.readBytesAtOffset(currentAddress, 8), currentAddress);
				allChapterRewards.add(chapterItem);
				DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found reward at offset 0x" + Long.toHexString(currentAddress) + " Item ID: 0x" + Integer.toHexString(chapterItem.getItemID()));
				currentAddress += 4;
			}
			
			if (commandWord[1] == 0 && commandWord[2] == 0 && commandWord[3] == 0) {
				// BACG (0x03), LABEL (0x1B), MUSC (0x36), MUSS (0x37), DISA (0x30), ENUT (0x34), ENUF (0x35), STAL (0x02), CAM1(0xB, 0xC), CURF(0x2C, 0x2D), TEX1(0x7), MORETEXT(0x8), MNCH(0x3D), GOTO(0x1C) - 8 bytes
				if (commandWord[0] == 0x03 || commandWord[0] == 0x1B || commandWord[0] == 0x36 || commandWord[0] == 0x37 || commandWord[0] == 0x30 ||
						commandWord[0] == 0x34 || commandWord[0] == 0x35 || commandWord[0] == 0x02 || commandWord[0] == 0x0B || commandWord[0] == 0x0C ||
						commandWord[0] == 0x2C || commandWord[0] == 0x2D || commandWord[0] == 0x07 || commandWord[0] == 0x08 || commandWord[0] == 0x3D ||
						commandWord[0] == 0x1C) { 
					currentAddress += 4;
				} else if (commandWord[0] == 0x20 || commandWord[0] == 0x23 || commandWord[0] == 0x22) { // IFAF (0x20), IFEF (0x23), GOTO_IFET (0x22) - 12 bytes
					currentAddress += 8;
				} else if (commandWord[0] == 0x3F) { // LOMA (0x3F) - 16 bytes
					currentAddress += 12;
				}
			}
			
			currentAddress += 4;
			commandWord = handler.readBytesAtOffset(currentAddress, 4);
		}
		
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Finished searching for rewards at 0x" + Long.toHexString(currentAddress));
	}

	public GBAFEChapterItemData chapterItemGivenToCharacter(int characterID) { return null; }
	
	public void setMaxEnemyClassLimit(int limit) {
		maxEnemyClassLimit = limit;
	}
	
	public int getMaxEnemyClassLimit() {
		return maxEnemyClassLimit;
	}
}
