package fedata.fe8;

import java.util.ArrayList;

import fedata.FEChapterUnit;

public class FE8ChapterUnit implements FEChapterUnit {
	
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
	
	public int getLeaderID() {
		return 0; // FE8 doesn't use Leader IDs
	}

	public int getLoadingX() {
		return 0; // FE8 doesn't use this.
	}

	public int getLoadingY() {
		return 0; // FE8 doesn't use this.
	}
	
	public int getStartingX() {
		return data[4] & 0xFF;
	}
	
	public int getStartingY() {
		return data[5] & 0xFF;
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
	
	public void giveItems(int[] itemIDs) {
		ArrayList<Integer> workingIDs = new ArrayList<Integer>();
		for (int i = 0; i < itemIDs.length; i++) {
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
		int[] items = new int[4];
		int counter = 0;
		
		if (getItem1() != 0) {
			items[counter] = getItem1();
			counter++;
		}
		if (getItem2() != 0) {
			items[counter] = getItem2();
			counter++;
		}
		if (getItem3() != 0) {
			items[counter] = getItem3();
			counter++;
		}
		if (getItem4() != 0) {
			items[counter] = getItem4();
			counter++;
		}
		
		setItem1(items[0]);
		setItem2(items[1]);
		setItem3(items[2]);
		setItem4(items[3]);
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
