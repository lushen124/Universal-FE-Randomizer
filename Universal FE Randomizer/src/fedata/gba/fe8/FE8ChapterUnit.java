package fedata.gba.fe8;

import java.util.ArrayList;
import java.util.List;

import fedata.gba.GBAFEChapterUnitData;

public class FE8ChapterUnit implements GBAFEChapterUnitData {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE8ChapterUnit(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public int getCharacterNumber() {
		return data[0] & 0xFF;
	}

	public int getStartingClass() {
		return data[1] & 0xFF;
	}

	public void setStartingClass(int classID) {
		data[1] = (byte)(classID & 0xFF);
		wasModified = true;
	}
	
	// Level and alliance are stored in byte 3, but we need to interpret it properly.
	// LLLL LENA
	// L = Level (5 bits, 0 - 31)
	// E = Set if enemy
	// N = Set if NPC
	// A = Set if autolevel
	// Note: playable characters are defined as not enemies and not NPCs.
	public int getStartingLevel() {	
		int value = data[3] & 0xFF;
		return value >> 3;
	}
	
	public boolean isEnemy() {
		int value = data[3] & 0xFF;
		return (value & 0x4) != 0;
	}
	
	public boolean isNPC() {
		int value = data[3] & 0xFF;
		return (value & 0x2) != 0;
	}
	
	public boolean isAutolevel() {
		int value = data[3] & 0xFF;
		return (value & 0x1) != 0;
	}
	
	public int getLeaderID() {
		return 0; // FE8 doesn't use Leader IDs
	}

	public int getLoadingX() {
		return 0; // FE8 doesn't use this.
	}

	public int getLoadingY() {
		return 0; // FE8 doesn't use this.
	}
	
	public void setLoadingX(int newX) {
		// FE8 doesn't use this.
	}
	
	public void setLoadingY(int newY) {
		// FE8 doesn't use this.
	}
	
	// FE8 does things a bit differently. Each position is only 6 bits instead of 8 bits. We need to read bytes 4, 5 to get the data.
	public int getStartingX() {
		// Little endian means the least significant bits are in the latter byte.
		// In terms of bit layout: 5555 5555 4444 4444
		// The only bits that matter are the latter 12 bits: 5555 4444 4444
		// Now we just split it down the middle, 6 bits each: 555544 444444
		// X is the second piece, Y is the first piece. yyyyyy xxxxxx
		int positionData = (((data[5] & 0xFF) << 8) | (data[4] & 0xFF)) & 0xFFF;
		return positionData & 0x3F;
	}
	
	public int getStartingY() {
		int positionData = (((data[5] & 0xFF) << 8) | (data[4] & 0xFF)) & 0xFFF;
		return (positionData >> 6) & 0x3F;
	}
	
	public void setStartingX(int newX) {
		int startingY = getStartingY();
		int positionData = (startingY << 6) | (newX & 0x3F);
		data[5] = (byte)((positionData & 0xF00) >> 8);
		data[4] = (byte)(positionData & 0xFF);
		wasModified = true;
	}
	
	public void setStartingY(int newY) {
		int startingX = getStartingX();
		int positionData = (newY << 6) | (startingX & 0x3F);
		data[5] = (byte)((positionData & 0xF00) >> 8);
		data[4] = (byte)(positionData & 0xFF);
		wasModified = true;
	}
	
	// Presumably the Reinforcement pointer defines any movement necessary, not that we care that much.

	public int getItem1() {
		return data[12] & 0xFF;
	}

	public void setItem1(int itemID) {
		data[12] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem2() {
		return data[13] & 0xFF;
	}

	public void setItem2(int itemID) {
		data[13] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem3() {
		return data[14] & 0xFF;
	}

	public void setItem3(int itemID) {
		data[14] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem4() {
		return data[15] & 0xFF;
	}

	public void setItem4(int itemID) {
		data[15] = (byte)(itemID & 0xFF);
		wasModified = true;
	}
	
	public void giveItem(int itemID) {
		if (getItem1() == 0) {
			setItem1(itemID);
		} else if (getItem2() == 0) {
			setItem2(itemID);
		} else if (getItem3() == 0) {
			setItem3(itemID);
		} else {
			setItem4(itemID);
		}
	}
	
	public void giveItems(int[] itemIDs) {
		ArrayList<Integer> workingIDs = new ArrayList<Integer>();
		for (int i = 0; i < itemIDs.length; i++) {
			if (getItem1() == itemIDs[i] || getItem2() == itemIDs[i] || getItem3() == itemIDs[i] || getItem4() == itemIDs[i]) { continue; }
			workingIDs.add(itemIDs[i]);
		}
		
		if (!workingIDs.isEmpty()) {
			setItem4(workingIDs.remove(0));
			if (!workingIDs.isEmpty()) {
				setItem3(workingIDs.remove(0));
				if (!workingIDs.isEmpty()) {
					setItem2(workingIDs.remove(0));
					if (!workingIDs.isEmpty()) {
						setItem1(workingIDs.remove(0));
					}
				}
			}
		}
		
		collapseItems();
	}
	
	public void removeItem(int itemID) {
		if (getItem1() == itemID) {
			setItem1(0);
		}
		if (getItem2() == itemID) {
			setItem2(0);
		}
		if (getItem3() == itemID) {
			setItem3(0);
		}
		if (getItem4() == itemID) {
			setItem4(0);
		}
		
		collapseItems();
	}
	
	private void collapseItems() {
		List<Integer> items = new ArrayList<Integer>();
		
		if (getItem1() != 0) {
			if (FE8Data.Item.valueOf(getItem1()).isWeapon()) {
				items.add(0, getItem1());
			} else {
				items.add(getItem1());
			}
		}
		if (getItem2() != 0) {
			if (FE8Data.Item.valueOf(getItem2()).isWeapon()) {
				items.add(0, getItem2());
			} else {
				items.add(getItem2());
			}
		}
		if (getItem3() != 0) {
			if (FE8Data.Item.valueOf(getItem3()).isWeapon()) {
				items.add(0, getItem3());
			} else {
				items.add(getItem3());
			}
		}
		if (getItem4() != 0) {
			if (FE8Data.Item.valueOf(getItem4()).isWeapon()) {
				items.add(0, getItem4());
			} else {
				items.add(getItem4());
			}
		}
		
		
		
		setItem1(items.size() > 0 ? items.get(0) : 0);
		setItem2(items.size() > 1 ? items.get(1) : 0);
		setItem3(items.size() > 2 ? items.get(2) : 0);
		setItem4(items.size() > 3 ? items.get(3) : 0);
	}
	
	public void resetData() {
		data = originalData;
		wasModified = false;
	}
	
	public void commitChanges() {
		if (wasModified) {
			hasChanges = true;
		}
		wasModified = false;
	}
	
	public Boolean hasCommittedChanges() {
		return hasChanges;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public Boolean wasModified() {
		return wasModified;
	}
	
	public long getAddressOffset() {
		return originalOffset;
	}
	
	// TODO: Figure out FE8 AI flags.
	public void setAIToHeal(Boolean allowAttack) {
//		data[12] = (byte) (allowAttack ? 0x0F : 0x0E);
//		wasModified = true;
	}

	public void setAIToOnlyAttack(Boolean allowMove) {
//		data[12] = (byte) (allowMove ? 0x00 : 0x03);
//		wasModified = true;
	}

}
