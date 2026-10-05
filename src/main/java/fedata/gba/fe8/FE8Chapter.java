package fedata.gba.fe8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFEChapterItemData.Type;
import fedata.gba.fe8.FE8ChapterUnitMoveData;
import fedata.gba.general.CharacterNudge;
import io.FileHandler;
import util.DebugPrinter;
import util.FileReadHelper;
import util.WhyDoesJavaNotHaveThese;

public class FE8Chapter implements GBAFEChapterData {
	
	private String friendlyName;
	
	@SuppressWarnings("unused")
	private Boolean removeFightScenes;
	
	private Boolean isClassSafe;
	private Boolean shouldBeSimplified;
	
	private long turnBasedEventsOffset;
	private long characterBasedEventsOffset;
	private long locationBasedEventsOffset;
	private long miscEventsOffset;
	
	// 3 unknown offsets and a tutorial offset here.
	// 2 traps offsets
	
	private long unitOffset; // This one is repeated twice usually, but just in case.
	private long secondUnitOffset; // Usually a duplicate.
	
	// 2 sets of 3 0x00000000 offsets
	
	private long beginningSceneOffset;
	private long endingSceneOffset;
	
	private List<FE8ChapterUnit> allChapterUnits;
	private List<FE8ChapterItem> allChapterRewards;
	
	private Map<Integer, FE8ChapterItem> targetedChapterRewards;
	private Set<Integer> targetRewardRecipients;
	
	private Set<Integer> unarmedCharacterIDs;
	
	private Set<Integer> blacklistedClassIDs;
	@SuppressWarnings("unused")
	private Set<Long> fightEventOffsets;
	
	private CharacterNudge[] nudges;
	
	private int maxEnemyClassLimit = 0;
	
	public FE8Chapter(FileHandler handler, long pointer, Boolean isClassSafe, Boolean removeFightScenes, int[] blacklistedClassIDs, String friendlyName, Boolean simple, int[] targetedRewardRecipientsToTrack, int[] unarmedCharacters, long[] additionalUnitOffsets, CharacterNudge[] nudgesRequired) {
		this.friendlyName = friendlyName;
		this.blacklistedClassIDs = new HashSet<Integer>();
		for (int classID : blacklistedClassIDs) {
			this.blacklistedClassIDs.add(classID);
		}
		this.removeFightScenes = removeFightScenes;
		this.shouldBeSimplified = simple;
		
		targetRewardRecipients = new HashSet<Integer>();
		for (int recipient : targetedRewardRecipientsToTrack) { targetRewardRecipients.add(recipient); }
		
		unarmedCharacterIDs = new HashSet<Integer>();
		for (int unarmedChar : unarmedCharacters) { unarmedCharacterIDs.add(unarmedChar); }
				
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
		currentOffset += 4 * 6;
		unitOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		secondUnitOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		currentOffset += 4 * 6;
		beginningSceneOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		endingSceneOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		
		allChapterUnits = new ArrayList<FE8ChapterUnit>();
		allChapterRewards = new ArrayList<FE8ChapterItem>();
		
		targetedChapterRewards = new HashMap<Integer, FE8ChapterItem>();
		
		fightEventOffsets = new HashSet<Long>();
		
		nudges = nudgesRequired;
		
		loadUnits(handler);
		loadRewards(handler);
		
		for (long unitOffset : additionalUnitOffsets) {
			DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loading auxiliary units from offset 0x" + Long.toHexString(unitOffset));
			loadUnitsFromAddress(handler, unitOffset);
		}
	}

	@Override
	public int lordLeaderID() {
		return 0;
	}

	@Override
	public int bossLeaderID() {
		return 0;
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
		Collection<FE8ChapterItem> collection = targetedChapterRewards.values();
		return collection.toArray(new FE8ChapterItem[collection.size()]);
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
		return unarmedCharacterIDs.contains(characterID);
	}
	
	public boolean chapterHasChests() {
		return allChapterRewards.stream().anyMatch(reward -> reward.getRewardType() == Type.CHES);
	}
	
	public boolean chapterHasVillages() {
		return allChapterRewards.stream().anyMatch(reward -> reward.getRewardType() == Type.ITGV);
	}
	
	public void applyNudges() {
		if (nudges == null || nudges.length == 0) { return; }
		for (CharacterNudge nudge : nudges) {
			characterLoop : for (GBAFEChapterUnitData unit : allUnits()) {
				// If the nudge isn't for the current character, just continue
				if (unit.getCharacterNumber() != nudge.getCharacterID()) {
					continue;
				}
				
				// Check if the nudge is for the current instance of the character
				if (unit.getStartingX() != nudge.getOldX() || unit.getStartingY() != nudge.getOldY()) {
					continue;
				}
				
				DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Nudging character 0x" + Integer.toHexString(unit.getCharacterNumber()) + " from (" + unit.getPostMoveX() + ", " + unit.getPostMoveY() + ") to (" + nudge.getNewX() + ", " + nudge.getNewY() + ")");
				// Movement Id -1 implies it's the starting position
				if (nudge.getMovementId() == -1) {
					unit.setStartingX(nudge.getNewX());
					unit.setStartingY(nudge.getNewY());
				} else {
					// Movement Ids other than -1 should be in the movements list
					((FE8ChapterUnit) unit).getMovements().get(nudge.getMovementId()).setPostMoveX(nudge.getNewX());
					((FE8ChapterUnit) unit).getMovements().get(nudge.getMovementId()).setPostMoveY(nudge.getNewY());
				} 
				
				// The nudge was applied successfully, break out of the character loop.
				break characterLoop;
			}
		}
	}
	
	private static class MemoryValues {
		private long values[] = new long[0xE];
		
		public void setValue(long value, int index) {
			assert index < 0xE : "Attempted to set memory value out of bounds.";
			if (index == 0) { return; } // Writing to 0 has no effect.
			values[index] = value; 
		}
		
		public long getValue(int index) {
			assert index < 0xE : "Attempted to read memory value out of bounds.";
			return values[index];
		}
	}
	
	private void loadUnits(FileHandler handler) {
		Set<Long> addressesSearched = new HashSet<Long>();
		// Look in the obvious places first.
		loadUnitsFromAddress(handler, unitOffset);
		addressesSearched.add(unitOffset);
		
		if (secondUnitOffset != unitOffset) {
			loadUnitsFromAddress(handler, secondUnitOffset);
			addressesSearched.add(secondUnitOffset);
		}
		
		Set<Long> eventAddresses = eventAddressesFromTurnEvents(handler);
		eventAddresses.addAll(eventAddressesFromCharacterEvents(handler));
		eventAddresses.addAll(eventAddressesFromLocationEvents(handler));
		eventAddresses.addAll(eventAddressesFromMiscEvents(handler));
		eventAddresses.add(beginningSceneOffset);
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
		while (turnCommand[0] == 0x02) { // Thankfully, this is the only one we have to look for.
			// TURN (0x02) - 12 bytes - offset starts at byte 4 and is 4 bytes long.
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
		charCommand = handler.readBytesAtOffset(currentAddress, 16);
		while (charCommand[0] == 0x03) { // I guess everything else is done by SETVAL now.
			// CHAR (0x03) - 16 bytes - offset starts at byte 4 and is 4 bytes long.
			long address = FileReadHelper.readAddress(handler, currentAddress + 4);
			if (address != -1) {
				eventAddresses.add(address);
			}
			
			currentAddress += 16;
			charCommand = handler.readBytesAtOffset(currentAddress, 16);
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
			// AFEV (0x01) - 12 bytes, offset 4
			if (miscCommand[0] == 0x0B || miscCommand[0] == 0x01) {
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
		// TODO
	}
	
	private Set<Long> unitAddressesFromEventBlob(FileHandler handler, long eventAddress) {
		return unitAddressesFromEventBlob(handler, eventAddress, new MemoryValues());
	}
	
	private Set<Long> unitAddressesFromEventBlob(FileHandler handler, long eventAddress, MemoryValues memSlots) {
		Set<Long> addressesLoaded = new HashSet<Long>();
		if (eventAddress >= 0x1000000) { return addressesLoaded; }
		
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Searching for unit addresses beginning at 0x" + Long.toHexString(eventAddress));
		
		byte[] commandWord;
		long currentAddress = eventAddress;
		// Unlike FE6 and FE7, FE8's commands are 2 bytes long. We'll still read 4, but we'll need to compare more than just the first byte of it to determine the command.
		commandWord = handler.readBytesAtOffset(currentAddress, 4);
		// The terminals in FE8 are ENDA (0x120) and ENDB(0x121). In hex, they show up in little endian, so 20 01 or 21 01.
		while (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x20, 0x01, 0x00, 0x00}) &&
				!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x21, 0x01, 0x00, 0x00})) {
			// FE8 has a few ways of loading, so we have to cover all of them.
			if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(commandWord, new byte[] {0x40, 0x2C})) {
				// LOAD1 and LOAD_SLOT1
				// The address starts at byte 4 in the normal case, but LOAD_SLOT1 will have an address of 0xFFFFFFFF.
				long bytes = FileReadHelper.readWord(handler, currentAddress + 4, false);
				if (bytes == 0xFFFFFFFFL) {
					// LOAD_SLOT1
					// It says LOAD_SLOT1, but it always reads from slot 2 actually.
					if (memSlots.getValue(0x2) >= 0x8000000) {
						long address = memSlots.getValue(0x2) - 0x8000000;
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOAD_SLOT1 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: 0x" + Long.toHexString(address));
						addressesLoaded.add(address);
						currentAddress += 4;
					} else {
						System.err.println("Invalid looking unit address found for LOAD_SLOT1 at 0x" + Long.toHexString(currentAddress) + ". Address found in slot 1: 0x" + Long.toHexString(memSlots.getValue(0x2)));
					}
				} else {
					// LOAD1
					long address = bytes - 0x8000000;
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOAD1 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: 0x" + Long.toHexString(address));
					addressesLoaded.add(address);
					currentAddress += 4;
				}
			} else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(commandWord, new byte[] {0x41, 0x2C})) {
				// LOAD2
				long address = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (address != -1) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOAD2 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: 0x" + Long.toHexString(address));
					addressesLoaded.add(address);
					currentAddress += 4;
				} else {
					System.err.println("Invalid unit address found for LOAD2 at 0x" + Long.toHexString(currentAddress));
				}
			} else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(commandWord, new byte[] {0x42, 0x2C})) {
				// LOAD3
				long address = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (address != -1) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOAD3 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: 0x" + Long.toHexString(address));
					addressesLoaded.add(address);
					currentAddress += 4;
				} else {
					System.err.println("Invalid unit address found for LOAD3 at 0x" + Long.toHexString(currentAddress));
				}
			} else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(commandWord, new byte[] {0x40, 0x05})) {
				// SETVAL - Since LOAD_SLOT1 uses this, we need to keep track of this if we ever see it set a value to memory slot 1.
				// I say slot 1, but the events always write the address to slot 2 instead, as seen in the command word. So *shrugs*
				// The third byte is the slot that the value is written to...
				long value = FileReadHelper.readWord(handler, currentAddress + 4, false);
				int slot = commandWord[2];
				memSlots.setValue(value, slot);
				DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Storing value 0x" + Long.toHexString(value) + " into slot " + Integer.toHexString(slot) + " for potential loading. Current Address: 0x" + Long.toHexString(currentAddress));
				currentAddress += 4;
			} else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(commandWord, new byte[] {0x40, 0x0A})) {
				// CALL - Look for unit addresses in the jump and add them to our set.
				long address = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (address != -1) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "CALLing to address 0x" + Long.toHexString(address) + " to look for more unit addresses. Current Address: 0x" + Long.toHexString(currentAddress));
					Set<Long> addressesFound = unitAddressesFromEventBlob(handler, address, memSlots);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Returned from address 0x" + Long.toHexString(address));
					for (long addressFound : addressesFound) {
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found unit address in CALL: 0x" + Long.toHexString(addressFound));
					}
					addressesLoaded.addAll(addressesFound);
					currentAddress += 4;
				}
			} else if ((commandWord[0] & 0xF0) == 0x20 && commandWord[1] == 0x6) { // Matches 0x062X, which all deal with memory management.
				// The argument is in the following 2 bytes, in the format of 0x0XYZ (which in little endian is stored as YZ 0X).
				// X - Source slot for Operand 1
				// Y - Source slot for Operand 2
				// Z - Destination
				int sourceSlotX = commandWord[3] & 0xF;
				int sourceSlotY = (commandWord[2] >> 4) & 0xF;
				int destinationZ = commandWord[2] & 0xF;
				if (commandWord[0] == 0x20) { // SADD - We need to handle this because this mechanism is used in a few chapters to load some units.
					// Often times, X is set to 0, which turns the operation into a "move Y to Z"
					memSlots.setValue(memSlots.getValue(sourceSlotX) + memSlots.getValue(sourceSlotY), destinationZ);
				} else if (commandWord[0] == 0x21) { // SSUB (technically RSUB, as X is subtracted from Y)
					memSlots.setValue(memSlots.getValue(sourceSlotY) - memSlots.getValue(sourceSlotX), destinationZ);
				} else if (commandWord[0] == 0x22) { // SMUL
					memSlots.setValue(memSlots.getValue(sourceSlotX) * memSlots.getValue(sourceSlotY), destinationZ);
				} else if (commandWord[0] == 0x23) { // SDIV
					memSlots.setValue((int)memSlots.getValue(sourceSlotY) / (int)memSlots.getValue(sourceSlotX), destinationZ);
				} else if (commandWord[0] == 0x24) { // SMOD (modulo)
					memSlots.setValue((int)memSlots.getValue(sourceSlotY) % (int)memSlots.getValue(sourceSlotX), destinationZ);
				} else if (commandWord[0] == 0x25) { // SAND (bitwise AND)
					memSlots.setValue(memSlots.getValue(sourceSlotX) & memSlots.getValue(sourceSlotY), destinationZ);
				} else if (commandWord[0] == 0x26) { // SORR (bitwise OR)
					memSlots.setValue(memSlots.getValue(sourceSlotX) | memSlots.getValue(sourceSlotY), destinationZ);
				} else if (commandWord[0] == 0x27) { // SXOR (bitwise XOR)
					memSlots.setValue(memSlots.getValue(sourceSlotX) ^ memSlots.getValue(sourceSlotY), destinationZ);
				} else if (commandWord[0] == 0x28) { // SLSL (left shift)
					memSlots.setValue(memSlots.getValue(sourceSlotY) << memSlots.getValue(sourceSlotX), destinationZ);
				} else if (commandWord[0] == 0x29) { // SLSR (right shift)
					memSlots.setValue(memSlots.getValue(sourceSlotY) >> memSlots.getValue(sourceSlotX), destinationZ);
				} else {
					assert false : "Unhandled slot operation.";
				}
			}
			
			// LOAD4 is not used, it seems. SPAWN_* looks like it's only for cutscenes. There's only Character ID, so presumably it reads the class from the character data, so we don't care.
			
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
		byte[] unitData = handler.readBytesAtOffset(currentOffset, FE8Data.BytesPerChapterUnit);
		while (unitData[0] != 0x00) {
			DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loaded unit with data " + WhyDoesJavaNotHaveThese.displayStringForBytes(unitData));
			FE8ChapterUnit unit = new FE8ChapterUnit(unitData, currentOffset);
			
			
			// If the unit has movements, load them.
			if (unit.getNumberMovements() != 0) {
				long movementsPointer = FileReadHelper.readAddress(handler, currentOffset + 8);
				for (int i = 0; i < unit.getNumberMovements(); i++) {
					long originalOffset = movementsPointer + i * 8;
					byte[] bytes = handler.readBytesAtOffset(originalOffset, 8);
					unit.addMovement(new FE8ChapterUnitMoveData(originalOffset, bytes));
				}
			}
			
			if (!blacklistedClassIDs.contains(unit.getStartingClass())) { // Remove any characters starting as a blacklisted class from consideration.
				allChapterUnits.add(unit);
			}
			
			currentOffset += FE8Data.BytesPerChapterUnit;
			unitData = handler.readBytesAtOffset(currentOffset, FE8Data.BytesPerChapterUnit);
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
					allChapterRewards.add(new FE8ChapterItem(locationCommand, currentAddress));
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
		eventAddresses.add(beginningSceneOffset);
		eventAddresses.add(endingSceneOffset);
		for (long eventOffset : eventAddresses) {
			loadRewardsFromEventBlob(handler, eventOffset);
		}
	}
	
	private void loadRewardsFromEventBlob(FileHandler handler, long eventOffset) {
		loadRewardsFromEventBlob(handler, eventOffset, 0);
	}
	
	private void loadRewardsFromEventBlob(FileHandler handler, long eventOffset, long lastSlot1Value) {
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Searching for rewards beginning at 0x" + Long.toHexString(eventOffset));
		byte[] commandWord;
		long currentAddress = eventOffset;
		commandWord = handler.readBytesAtOffset(currentAddress, 4);
		long lastStoredValueInSlot1 = lastSlot1Value;
		// FE8 does this a bit differently. It uses a combination of SETVAL and GIVEITEMTO
		// SETVAL is always setting the item ID into slot 3, so we should get the first word as
		// 0x40, 0x05, 0x03, 0x00 (the first two indicate SETVAL, and the third byte is the memory slot).
		// This is followed with GIVEITEMTO, represented by 0x3720 (i.e. 0x20, 0x37) and
		// and then -1 (0xFF, 0xFF), indicating to give to the current character.
		// We'll just search for the whole 12 byte block, but advance only by 4s. So that we check every
		// combination of 12 bytes to make sure we don't miss anything.
		
		List<Byte> format = new ArrayList<Byte>(Arrays.asList(
				new Byte((byte)0x40), new Byte((byte)0x05), new Byte((byte)0x03), new Byte((byte)0x00), // SETVAL 0x03
				null, new Byte((byte)0x00), new Byte((byte)0x00), new Byte((byte)0x00), // Item ID, followed by 0s.
				new Byte((byte)0x20), new Byte((byte)0x37), new Byte((byte)0xFF), new Byte((byte)0xFF))); // GIVEITEMTO 0xFFFF
		
		List<Byte> targetedFormat = new ArrayList<Byte>(Arrays.asList(
				new Byte((byte)0x40), new Byte((byte)0x05), new Byte((byte)0x03), new Byte((byte)0x00),
				null, new Byte((byte)0x00), new Byte((byte)0x00), new Byte((byte)0x00),
				new Byte((byte)0x20), new Byte((byte)0x37), null, null));
						
		
		while (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x20, 0x01, 0x00, 0x00}) &&
				!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x21, 0x01, 0x00, 0x00})) {
			// Read 12 bytes.
			byte[] full = handler.readBytesAtOffset(currentAddress, 12);
			if (WhyDoesJavaNotHaveThese.byteArrayMatchesFormat(full, format)) {
				FE8ChapterItem chapterItem = new FE8ChapterItem(full, currentAddress);
				allChapterRewards.add(chapterItem);
				DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found reward at offset 0x" + Long.toHexString(currentAddress) + " Item ID: 0x" + Integer.toHexString(chapterItem.getItemID()));
				currentAddress += 8;
			} else if (WhyDoesJavaNotHaveThese.byteArrayMatchesFormat(full, targetedFormat)) {
				int characterID = full[10];
				if (targetRewardRecipients.contains(characterID)) {
					targetedChapterRewards.put(characterID, new FE8ChapterItem(full, currentAddress));
				}
			} else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(commandWord, new byte[] {0x40, 0x0A})) { // I don't think this ever happens, but in case items are hiding behind CALLs, let's check anyway.
				long address = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (address != -1) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "CALLing to address 0x" + Long.toHexString(address) + " to look for more rewards. Current Address: 0x" + Long.toHexString(currentAddress));
					loadRewardsFromEventBlob(handler, address, lastStoredValueInSlot1);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Returned from address 0x" + Long.toHexString(address));

					currentAddress += 4;
				}
			}
			
			currentAddress += 4;
			commandWord = handler.readBytesAtOffset(currentAddress, 4);
		}
		
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Finished searching for rewards at 0x" + Long.toHexString(currentAddress));
	}
	
	public GBAFEChapterItemData chapterItemGivenToCharacter(int characterID) {
		return targetedChapterRewards.get(characterID);
	}
	
	public void setMaxEnemyClassLimit(int limit) {
		maxEnemyClassLimit = limit;
	}
	
	public int getMaxEnemyClassLimit() {
		return maxEnemyClassLimit;
	}
}
