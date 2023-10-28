package fedata.gba.fe7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.general.CharacterNudge;
import io.FileHandler;
import util.DebugPrinter;
import util.FileReadHelper;
import util.WhyDoesJavaNotHaveThese;

public class FE7Chapter implements GBAFEChapterData {
	
	private String friendlyName;
	
	private Boolean removeFightScenes;
	
	private Boolean isClassSafe;
	private Boolean shouldBeSimplified;
	
	private long turnBasedEventsOffset = 0;
	private long characterBasedEventsOffset = 0;
	private long locationBasedEventsOffset = 0;
	private long miscEventsOffset = 0;
	
	@SuppressWarnings("unused")
	private long trapsEliwoodOffset = 0;
	@SuppressWarnings("unused")
	private long trapsHectorOffset = 0;
	
	private long enemiesOffsetENM = 0;
	private long enemiesOffsetEHM = 0;
	private long enemiesOffsetHNM = 0;
	private long enemiesOffsetHHM = 0;
	private long alliesOffsetENM = 0;
	private long alliesOffsetEHM = 0;
	private long alliesOffsetHNM = 0;
	private long alliesOffsetHHM = 0;
	private long beginningSceneOffset = 0;
	private long endingSceneOffset = 0;
	
	private List<FE7ChapterUnit> allChapterUnits;
	private List<FE7ChapterItem> allChapterRewards;
	
	private Map<Integer, FE7ChapterTargetedItem> targetedChapterRewards;
	private Set<Integer> targetedRewardRecipients;
	
	private Set<Integer> blacklistedClassIDs;
	private Set<Long> fightEventOffsets;
	
	private CharacterNudge[] nudges;
	
	private Set<Integer> knownAllyIDs;
	private Set<Integer> knownEnemyIDs;
	private int probableLordID = 0;
	private int probableBossID = 0;
	
	private int maxEnemyClassLimit = 0;

	public FE7Chapter(FileHandler handler, long pointer, Boolean isClassSafe, Boolean removeFightScenes, int[] targetedRecipientsToTrack, int[] blacklistedClassIDs, String friendlyName, Boolean simple, CharacterNudge[] nudges) {
		
		this.friendlyName = friendlyName;
		this.blacklistedClassIDs = new HashSet<Integer>();
		for (int classID : blacklistedClassIDs) {
			this.blacklistedClassIDs.add(classID);
		}
		this.removeFightScenes = removeFightScenes;
		this.shouldBeSimplified = simple;
		
		targetedRewardRecipients = new HashSet<Integer>();
		for (int recipient : targetedRecipientsToTrack) { targetedRewardRecipients.add(recipient); }
				
		// We need one jump.
		long pointerTableOffset = FileReadHelper.readAddress(handler, pointer);
		this.isClassSafe = isClassSafe;
		
		this.nudges = nudges;
		
		long currentOffset = pointerTableOffset;
		turnBasedEventsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		characterBasedEventsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		locationBasedEventsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		miscEventsOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		trapsEliwoodOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		trapsHectorOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		enemiesOffsetENM = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		enemiesOffsetEHM = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		enemiesOffsetHNM = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		enemiesOffsetHHM = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		alliesOffsetENM = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		alliesOffsetEHM = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		alliesOffsetHNM = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		alliesOffsetHHM = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		beginningSceneOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		endingSceneOffset = FileReadHelper.readAddress(handler, currentOffset);
		currentOffset += 4;
		
		allChapterUnits = new ArrayList<FE7ChapterUnit>();
		allChapterRewards = new ArrayList<FE7ChapterItem>();
		
		fightEventOffsets = new HashSet<Long>();
		
		knownAllyIDs = new HashSet<Integer>();
		knownEnemyIDs = new HashSet<Integer>();
		
		targetedChapterRewards = new HashMap<Integer, FE7ChapterTargetedItem>();
		
		loadUnits(handler);
		loadRewards(handler);
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
	public Boolean shouldBeSimplified() {
		return shouldBeSimplified;
	}
	
	public Boolean shouldCharacterBeUnarmed(int characterID) {
		return false;
	}

	@Override
	public GBAFEChapterUnitData[] allUnits() {
		return allChapterUnits.toArray(new GBAFEChapterUnitData[allChapterUnits.size()]);
	}
	
	public GBAFEChapterItemData[] allRewards() {
		return allChapterRewards.toArray(new GBAFEChapterItemData[allChapterRewards.size()]);
	}
	
	public GBAFEChapterItemData[] allTargetedRewards() {
		return targetedChapterRewards.values().toArray(new GBAFEChapterItemData[targetedChapterRewards.size()]);
	}
	
	public int lordLeaderID() {
		return probableLordID;
	}
	
	public int bossLeaderID() {
		return probableBossID;
	}
	
	public long[] getFightAddresses() {
		if (removeFightScenes) {
			long[] offsets = new long[fightEventOffsets.size()];
			int i = 0;
			for (long offset : fightEventOffsets) {
				offsets[i++] = offset;
			}
			
			return offsets;
		} else {
			return new long[] {};
		}
	}
	
	public int fightCommandLength() {
		return 20; // Put this elsewhere?
	}
	
	public byte[] fightReplacementBytes() {
		return new byte[] {0x02, 0x00, 0x08, 0x00, // STAL 8
				0x02, 0x00, 0x08, 0x00, // STAL 8
				0x02, 0x00, 0x08, 0x00, // STAL 8
				0x02, 0x00, 0x08, 0x00, // STAL 8
				0x02, 0x00, 0x08, 0x00 }; // STAL 8
	}
	
	public Boolean isClassSafe() {
		return isClassSafe;
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
				} else if(unit.getPostMoveX() == nudge.getOldX() && unit.getPostMoveY() == nudge.getOldY()) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Nudging character PostMove 0x" + Integer.toHexString(unit.getCharacterNumber()) + " from (" + unit.getPostMoveX() + ", " + unit.getPostMoveY() + ") to (" + nudge.getNewX() + ", " + nudge.getNewY() + ")");
					if (unit.getPostMoveX() == unit.getStartingX() && unit.getPostMoveY() == unit.getStartingY()) { 
						unit.setStartingX(nudge.getNewX());
						unit.setStartingX(nudge.getNewY());
					}
					unit.setPostMoveX(nudge.getNewX());
					unit.setPostMoveY(nudge.getNewY());
				}
				
				break characterLoop;
			}
		}
	}
	
	private void loadUnits(FileHandler handler) {
		Set<Long> addressesSearched = new HashSet<Long>();
		// Look in the obvious places first.
		loadUnitsFromAddress(handler, alliesOffsetENM);
		addressesSearched.add(alliesOffsetENM);
		if (!addressesSearched.contains(alliesOffsetEHM)) {
			addressesSearched.add(alliesOffsetEHM);
			loadUnitsFromAddress(handler, alliesOffsetEHM);
		}
		if (!addressesSearched.contains(alliesOffsetHNM)) {
			addressesSearched.add(alliesOffsetHNM);
			loadUnitsFromAddress(handler, alliesOffsetHNM);
		}
		if (!addressesSearched.contains(alliesOffsetHHM)) {
			addressesSearched.add(alliesOffsetHHM);
			loadUnitsFromAddress(handler, alliesOffsetHHM);
		}
		
		if (!addressesSearched.contains(enemiesOffsetENM)) {
			addressesSearched.add(enemiesOffsetENM);
			loadUnitsFromAddress(handler, enemiesOffsetENM);
		}
		if (!addressesSearched.contains(enemiesOffsetEHM)) {
			addressesSearched.add(enemiesOffsetEHM);
			loadUnitsFromAddress(handler, enemiesOffsetEHM);
		}
		if (!addressesSearched.contains(enemiesOffsetHNM)) {
			addressesSearched.add(enemiesOffsetHNM);
			loadUnitsFromAddress(handler, enemiesOffsetHNM);
		}
		if (!addressesSearched.contains(enemiesOffsetHHM)) {
			addressesSearched.add(enemiesOffsetHHM);
			loadUnitsFromAddress(handler, enemiesOffsetHHM);
		}
		
		Set<Long> eventAddresses = eventAddressesFromTurnEvents(handler);
		eventAddresses.addAll(eventAddressesFromCharacterEvents(handler));
		eventAddresses.addAll(eventAddressesFromLocationEvents(handler));
		eventAddresses.addAll(eventAddressesFromMiscEvents(handler));
		eventAddresses.add(beginningSceneOffset);
		eventAddresses.add(endingSceneOffset);
		for (long eventAddress : eventAddresses) {
			if (eventAddress == 0x1) { continue; } // Some events use 1 for some reason. LOCA does this sometimes.
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
		turnCommand = handler.readBytesAtOffset(currentAddress, 16);
		while (turnCommand[0] == 0x02 || turnCommand[0] == 0x01) {
			// TURN - 16 bytes
			if (turnCommand[0] == 0x02) {
				// Format is 02 00 00 00 XX XX XX XX YY YY ZZ ZZ 00 00 00 00
				// Where XX XX XX XX is the address of the events.
				// YY YY is the turn count it happens on (start turn, end turn) (end turn is 0 if it doesn't repeat)
				// ZZ ZZ are some miscellaneous flags. The first is the turn time (i.e. player phase or enemy phase) the second limits to difficulty mode.
				long address = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (address != -1) {
					eventAddresses.add(address);
				}
				currentAddress += 16;
			}
			// AFEV - 12 bytes
			if (turnCommand[0] == 0x01) {
				long address = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (address != -1) {
					eventAddresses.add(address);
				}
				currentAddress += 12;
			}
			
			turnCommand = handler.readBytesAtOffset(currentAddress, 16);
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
		while (charCommand[0] == 0x03 || charCommand[0] == 0x04) {
			// CHAR and CHARASM are supported, with 0x03 and 0x04 commands. In both cases, the address is at byte 4, 4 length.
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
		locationCommand = handler.readBytesAtOffset(currentAddress, 12); // These events are only 12 bytes long.
		while (locationCommand[0] == 0x05 || locationCommand[0] == 0x06 || locationCommand[0] == 0x0A || locationCommand[0] == 0x07 || locationCommand[0] == 0x08) {
			if (locationCommand[0] == 0x05 || locationCommand[0] == 0x06) {
				// LOCA and VILL are the only ones that matter here. The others don't have event pointers.
				// In both cases, the address is at byte 4, 4 length.
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
		miscCommand = handler.readBytesAtOffset(currentAddress, 12); // These events are only 12 bytes long.
		while (miscCommand[0] != 0x00) {
			if (miscCommand[0] == 0x0B || miscCommand[0] == 0x01 || miscCommand[0] == 0x0E) {
				// ASME, AREA and AFEV are the only ones we care about. Both 12 bytes, and the event is at byte 4, length 4.
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
		while (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x0A, 0x00, 0x00, 0x00}) &&
				!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x0B, 0x00, 0x00, 0x00})) {
			if (WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {(byte)0x97, 0x00, 0x00, 0x00})) {
				// FIGH - Always has command byte 0x97, and is always 20 length. The second word always contains attacker and defender IDs.
				// They vary after that, but those aren't important.
				long address = FileReadHelper.readAddress(handler, currentAddress + 12);
				if (address != -1) {
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found FIGH at 0x" + Long.toHexString(currentAddress));
					fightEventOffsets.add(currentAddress);
				}
				currentAddress += 16;
			}
			
			// 0xCB is an interesting command, because it allows us to jump to (and return from) another label, where there might be FIGH events hiding.
			if (commandWord[0] == (byte)0xCB) {
				long eventBlob = FileReadHelper.readAddress(handler, currentAddress + 4);
				if (eventBlob != -1) {
					// Pray that these blobs don't reference each other.
					recordFightAddressesFromEventBlob(handler, eventBlob);
				}
				currentAddress += 4;
			}

			// Since we don't know how long each command is, we accidentally include what should be an argument for
			// another event as a command code. Below is a whitelist of codes that cause issues and how many bytes we need to skip.
			if (commandWord[0] == 0x44) { // 0x44 is apparently LABEL, which I have no idea what it does. It takes 1 word as an argument.
				currentAddress += 4;
			} else if (commandWord[0] == 0x27) { // MOVE has several variants, but only the ones that use character ID are dangerous for us. This one has 16 bytes.
				currentAddress += 12;
			} else if (commandWord[0] == 0x26 || commandWord[0] == 0x28) { // MOVE with 12 bytes.
				currentAddress += 8;
			} else if (commandWord[0] == 0x49) { // GOTO_IFAF has 12 bytes., The second word is occasionally 0xA or 0xB, leading us to exit prematurely.
				currentAddress += 8;
			}  else if (commandWord[0] == 0x4F || commandWord[0] == 0x50 || commandWord[0] == 0x52 || commandWord[0] == 0x53 || // GOTO_IFNHM, GOTO_IFNEM, GOTO_IFNO, GOTO_IFYES have 8 bytes. The second word can be 0xB.
					commandWord[0] == 0x54 || commandWord[0] == 0x56) { // GOTO_IFNTUTORIAL, GOTO_IFTU
				currentAddress += 4;
			} else if (commandWord[0] == (byte)0x84) { // LOMA, in the case that it loads a map that has the same hex as the commands above. 16 bytes total.
				currentAddress += 12;
			} else if (commandWord[0] == 0x6F || commandWord[0] == 0x70) { // REPA, 2 flavors, both 8 bytes. Causes characters to re-appear.
				currentAddress += 4;
			} else if (commandWord[0] == 0x45) { // GOTO (the counterpart of LABEL). The word as the argument is the destination.
				currentAddress += 4;
			} else if (commandWord[0] == 0x4B) { // No idea what this is. It's not well defined, but it's a conditional of some kind. 8 bytes long. The condition can be 0xA or 0xB.
				currentAddress += 4;
			} else if (commandWord[0] == 0x1A) { // TEXTIFEVENTID - 16 bytes, for conditional text. 
				currentAddress += 12;
			} else if (commandWord[0] == 0x4C || commandWord[0] == 0x4D) { // GOTO_IFET and GOTO_IFEF - 12 bytes
				currentAddress += 8;
			} else if (commandWord[0] == 0x0D) { // TEX1 - 8 bytes
				currentAddress += 4;
			} else if (commandWord[0] == 0x2E || commandWord[0] == 0x2F) { // REPOS - 12 bytes
				currentAddress += 8;
			} else if (commandWord[0] == 0x29) { // MOVENEXTTO - 12 bytes
				currentAddress += 8;
			} else if (commandWord[0] == 0x66 || commandWord[0] == 0x67) { // CURF - 8 bytes
				currentAddress += 4;
			} else if (commandWord[0] == 0x6A || commandWord[0] == 0x6B) { // DISA - 8 bytes
				currentAddress += 4;
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
		while (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x0A, 0x00, 0x00, 0x00}) &&
				!WhyDoesJavaNotHaveThese.byteArraysAreEqual(commandWord, new byte[] {0x0B, 0x00, 0x00, 0x00})) {
			if (commandWord[1] == 0 && commandWord[2] == 0 && commandWord[3] == 0) {
				if (commandWord[0] == 0x32 || commandWord[0] == 0x33 || commandWord[0] == 0x36) {
					// LOU1 - 0x32 key. Pointer at byte 4, length 4 - total 8 bytes.
					// LOU2 - 0x36 key. Same as LOU1.
					// 0x33 also seems to be one, but it's undefined in EA. (Found in Ch. 15, 17x)
					long address = FileReadHelper.readAddress(handler, currentAddress + 4);
					if (address != -1) {
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOU1 or LOU2 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: " + Long.toHexString(address));
						addressesLoaded.add(address);
					}
					currentAddress += 4;
				} else if (commandWord[0] == 0x35 || commandWord[0] == 0x38) {
					// LOUMODE1 - 0x35 key. Pointers at byte 4, 8, 12, 16, all length 4. Total 20 bytes.
					// LOUMODE2 - 0x38 key. Same as LOUMODE1.
					long address = FileReadHelper.readAddress(handler, currentAddress + 4);
					if (address != -1) {
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOUMODE1 or LOUMODE2 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: " + Long.toHexString(address) + " (1 / 4)");
						addressesLoaded.add(address);
					}
					address = FileReadHelper.readAddress(handler, currentAddress + 8);
					if (address != -1) {
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOUMODE1 or LOUMODE2 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: " + Long.toHexString(address) + " (2 / 4)");
						addressesLoaded.add(address);
					}
					address = FileReadHelper.readAddress(handler, currentAddress + 12);
					if (address != -1) {
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOUMODE1 or LOUMODE2 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: " + Long.toHexString(address) + " (3 / 4)");
						addressesLoaded.add(address);
					}
					address = FileReadHelper.readAddress(handler, currentAddress + 16);
					if (address != -1) {
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOUMODE1 or LOUMODE2 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: " + Long.toHexString(address) + " (4 / 4)");
						addressesLoaded.add(address);
					}
					
					currentAddress += 16;
				} else if (commandWord[0] == 0x34 || commandWord[0] == 0x37) {
					// LOUFILTERED - 0x34 key. Pointer at byte 8, length 4 - total 12 bytes.
					// LOUFILTERED2 - 0x37 key. Same as LOUFILTERED
					long address = FileReadHelper.readAddress(handler, currentAddress + 8);
					if (address != -1) {
						DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found LOUFILTERED or LOUFILTERED2 at 0x" + Long.toHexString(currentAddress) + ". Unit Address: " + Long.toHexString(address));
						addressesLoaded.add(address);
					}
					currentAddress += 8;
				}
				
				// 0xCB is an interesting command, because it allows us to jump to (and return from) another label, where there might be UNIT definitions hiding.
				if (commandWord[0] == (byte)0xCB) {
					long eventBlob = FileReadHelper.readAddress(handler, currentAddress + 4);
					if (eventBlob != -1) {
						// Pray that these blobs don't reference each other.
						addressesLoaded.addAll(unitAddressesFromEventBlob(handler, eventBlob));
					}
					currentAddress += 4;
				}
				
				DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Processed command 0x" + Integer.toHexString(commandWord[0] & 0xFF));

				// Since we don't know how long each command is, we accidentally include what should be an argument for
				// another event as a command code. Below is a whitelist of codes that cause issues and how many bytes we need to skip.
				if (commandWord[0] == 0x44) { // 0x44 is apparently LABEL, which I have no idea what it does. It takes 1 word as an argument.
					currentAddress += 4;
				} else if (commandWord[0] == 0x27) { // MOVE has several variants, but only the ones that use character ID are dangerous for us. This one has 16 bytes.
					currentAddress += 12;
				} else if (commandWord[0] == 0x26 || commandWord[0] == 0x28) { // MOVE with 12 bytes.
					currentAddress += 8;
				} else if (commandWord[0] == 0x49) { // GOTO_IFAF has 12 bytes. The second word is occasionally 0xA or 0xB, leading us to exit prematurely.
					currentAddress += 8;
				} else if (commandWord[0] == 0x4F || commandWord[0] == 0x50 || commandWord[0] == 0x52 || commandWord[0] == 0x53 || // GOTO_IFNHM, GOTO_IFNEM, GOTO_IFNO, GOTO_IFYES have 8 bytes. The second word can be 0xB.
						commandWord[0] == 0x54 || commandWord[0] == 0x56) { // GOTO_IFNTUTORIAL, GOTO_IFTU
					currentAddress += 4;
				} else if (commandWord[0] == (byte)0x84) { // LOMA, in the case that it loads a map that has the same hex as the commands above. 16 bytes total.
					currentAddress += 12;
				} else if (commandWord[0] == 0x6F || commandWord[0] == 0x70) { // REPA, 2 flavors, both 8 bytes. Causes characters to re-appear.
					currentAddress += 4;
				} else if (commandWord[0] == 0x45) { // GOTO (the counterpart of LABEL). The word as the argument is the destination.
					currentAddress += 4;
				} else if (commandWord[0] == 0x4B) { // No idea what this is. It's not well defined, but it's a conditional of some kind. 8 bytes long. The condition can be 0xA or 0xB.
					currentAddress += 4;
				} else if (commandWord[0] == 0x1A) { // TEXTIFEVENTID - 16 bytes, for conditional text. 
					currentAddress += 12;
				} else if (commandWord[0] == 0x4C || commandWord[0] == 0x4D) { // GOTO_IFET and GOTO_IFEF - 12 bytes
					currentAddress += 8;
				} else if (commandWord[0] == 0x0D) { // TEX1 - 8 bytes
					currentAddress += 4;
				} else if (commandWord[0] == 0x2E || commandWord[0] == 0x2F) { // REPOS - 12 bytes
					currentAddress += 8;
				} else if (commandWord[0] == 0x29) { // MOVENEXTTO - 12 bytes
					currentAddress += 8;
				} else if (commandWord[0] == 0x66 || commandWord[0] == 0x67) { // CURF - 8 bytes
					currentAddress += 4;
				} else if (commandWord[0] == 0x6A || commandWord[0] == 0x6B) { // DISA - 8 bytes
					currentAddress += 4;
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
		if (unitAddress <= 0xC00000) {
			System.err.println("Suspicious address found for unit: " + Long.toHexString(unitAddress));
			return;
		}
		long currentOffset = unitAddress;
		byte[] unitData = handler.readBytesAtOffset(currentOffset, FE7Data.BytesPerChapterUnit);
		while (unitData[0] != 0x00) {
			DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Loaded unit with data " + WhyDoesJavaNotHaveThese.displayStringForBytes(unitData));
			FE7ChapterUnit unit = new FE7ChapterUnit(unitData, currentOffset); 
			if (!blacklistedClassIDs.contains(unit.getStartingClass())) { // Remove any characters starting as a blacklisted class from consideration.
				allChapterUnits.add(unit);
				
				if (unitAddress == alliesOffsetEHM || unitAddress == alliesOffsetENM || unitAddress == alliesOffsetHHM || unitAddress == alliesOffsetHNM) {
					knownAllyIDs.add(unit.getCharacterNumber());
					if (unit.getLeaderID() == 0) { probableLordID = unit.getCharacterNumber(); }
				}
				if (unitAddress == enemiesOffsetEHM || unitAddress == enemiesOffsetENM || unitAddress == enemiesOffsetHHM || unitAddress == enemiesOffsetHNM) {
					knownEnemyIDs.add(unit.getCharacterNumber());
					if (unit.getLeaderID() == 0) { probableBossID = unit.getCharacterNumber(); }
				}
			}
			
			currentOffset += FE7Data.BytesPerChapterUnit;
			unitData = handler.readBytesAtOffset(currentOffset, FE7Data.BytesPerChapterUnit);
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
					allChapterRewards.add(new FE7ChapterItem(locationCommand, currentAddress));
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
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Searching for rewards beginning at 0x" + Long.toHexString(eventOffset));
		byte[] commandWord;
		long currentAddress = eventOffset;
		commandWord = handler.readBytesAtOffset(currentAddress, 4);
		while (commandWord[0] != 0x0A && commandWord[0] != 0x0B) {
			// We just need ITGV.
			if (commandWord[0] == 0x5B) {
				FE7ChapterItem chapterItem = new FE7ChapterItem(handler.readBytesAtOffset(currentAddress, 8), currentAddress);
				allChapterRewards.add(chapterItem);
				DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found reward at offset 0x" + Long.toHexString(currentAddress) + " Item ID: 0x" + Integer.toHexString(chapterItem.getItemID()));
				currentAddress += 4;
			}
			// ITGC is also used for targeted items.
			if (commandWord[0] == 0x5C) {
				FE7ChapterTargetedItem chapterItem = new FE7ChapterTargetedItem(handler.readBytesAtOffset(currentAddress, 12), currentAddress);
				if (targetedRewardRecipients.contains(chapterItem.getTargetID())) {
					targetedChapterRewards.put(chapterItem.getTargetID(), chapterItem);
					DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Found targeted reward at offset 0x" + Long.toHexString(currentAddress) + " Target ID: 0x" + Integer.toHexString(chapterItem.getTargetID()) + " Item ID: 0x" + Integer.toHexString(chapterItem.getItemID()));
				}
				currentAddress += 8;
			}
			
			// Since we don't know how long each command is, we accidentally include what should be an argument for
			// another event as a command code. Below is a whitelist of codes that cause issues and how many bytes we need to skip.
			if (commandWord[0] == 0x44) { // 0x44 is apparently LABEL, which I have no idea what it does. It takes 1 word as an argument.
				currentAddress += 4;
			} else if (commandWord[0] == 0x27) { // MOVE has several variants, but only the ones that use character ID are dangerous for us. This one has 16 bytes.
				currentAddress += 12;
			} else if (commandWord[0] == 0x26 || commandWord[0] == 0x28) { // MOVE with 12 bytes.
				currentAddress += 8;
			} else if (commandWord[0] == 0x49) { // GOTO_IFAF has 12 bytes., The second word is occasionally 0xA or 0xB, leading us to exit prematurely.
				currentAddress += 8;
			}  else if (commandWord[0] == 0x4F || commandWord[0] == 0x50 || commandWord[0] == 0x52 || commandWord[0] == 0x53 || // GOTO_IFNHM, GOTO_IFNEM, GOTO_IFNO, GOTO_IFYES have 8 bytes. The second word can be 0xB.
					commandWord[0] == 0x54 || commandWord[0] == 0x56) { // GOTO_IFNTUTORIAL, GOTO_IFTU
				currentAddress += 4;
			} else if (commandWord[0] == (byte)0x84) { // LOMA, in the case that it loads a map that has the same hex as the commands above. 16 bytes total.
				currentAddress += 12;
			} else if (commandWord[0] == 0x6F || commandWord[0] == 0x70) { // REPA, 2 flavors, both 8 bytes. Causes characters to re-appear.
				currentAddress += 4;
			} else if (commandWord[0] == 0x45) { // GOTO (the counterpart of LABEL). The word as the argument is the destination.
				currentAddress += 4;
			} else if (commandWord[0] == 0x4B) { // No idea what this is. It's not well defined, but it's a conditional of some kind. 8 bytes long. The condition can be 0xA or 0xB.
				currentAddress += 4;
			} else if (commandWord[0] == 0x1A) { // TEXTIFEVENTID - 16 bytes, for conditional text. 
				currentAddress += 12;
			} else if (commandWord[0] == 0x4C || commandWord[0] == 0x4D) { // GOTO_IFET and GOTO_IFEF - 12 bytes
				currentAddress += 8;
			} else if (commandWord[0] == 0x0D) { // TEX1 - 8 bytes
				currentAddress += 4;
			} else if (commandWord[0] == 0x2E || commandWord[0] == 0x2F) { // REPOS - 12 bytes
				currentAddress += 8;
			} else if (commandWord[0] == 0x29) { // MOVENEXTTO - 12 bytes
				currentAddress += 8;
			} else if (commandWord[0] == 0x66 || commandWord[0] == 0x67) { // CURF - 8 bytes
				currentAddress += 4;
			} else if (commandWord[0] == 0x6A || commandWord[0] == 0x6B) { // DISA - 8 bytes
				currentAddress += 4;
			}
						
			currentAddress += 4;
			commandWord = handler.readBytesAtOffset(currentAddress, 4);
		}
		
		DebugPrinter.log(DebugPrinter.Key.CHAPTER_LOADER, "Finished searching for rewards at 0x" + Long.toHexString(currentAddress));
	}
	
	public GBAFEChapterItemData chapterItemGivenToCharacter(int characterID) {
		if (targetedRewardRecipients.contains(characterID)) {
			return targetedChapterRewards.get(characterID);
		}
		
		return null;
	}
	
	public void setMaxEnemyClassLimit(int limit) {
		maxEnemyClassLimit = limit;
	}
	
	public int getMaxEnemyClassLimit() {
		return maxEnemyClassLimit;
	}
}
