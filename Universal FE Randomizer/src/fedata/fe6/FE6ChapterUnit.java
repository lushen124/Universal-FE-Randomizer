package fedata.fe6;

import java.util.ArrayList;

import fedata.FEChapterUnit;

public class FE6ChapterUnit implements FEChapterUnit {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE6ChapterUnit(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}

	@Override
	public int getCharacterNumber() {
		return data[0] & 0xFF;
	}

	@Override
	public int getStartingClass() {
		return data[1] & 0xFF;
	}

	@Override
	public void setStartingClass(int classID) {
		data[1] = (byte)(classID & 0xFF);
		wasModified = true;
	}

	@Override
	public int getLeaderID() {
		return data[2] & 0xFF;
	}

	public int getLoadingX() {
		return data[4] & 0xFF;
	}

	public int getLoadingY() {
		return data[5] & 0xFF;
	}
	
	public int getStartingX() {
		return data[6] & 0xFF;
	}
	
	public int getStartingY() {
		return data[7] & 0xFF;
	}

	public int getItem1() {
		return data[8] & 0xFF;
	}

	public void setItem1(int itemID) {
		data[8] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem2() {
		return data[9] & 0xFF;
	}

	public void setItem2(int itemID) {
		data[9] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem3() {
		return data[10] & 0xFF;
	}

	public void setItem3(int itemID) {
		data[10] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem4() {
		return data[11] & 0xFF;
	}

	public void setItem4(int itemID) {
		data[11] = (byte)(itemID & 0xFF);
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

	@Override
	public void setAIToHeal(Boolean allowAttack) {
		if (allowAttack) { // Based off of late-game sages
			data[12] = 0x0E;
			data[13] = 0x03;
			data[14] = 0x29;
			data[15] = 0x00;
		} else { // Based off of early-game priests
			data[12] = 0x0E;
			data[13] = 0x03;
			data[14] = 0x10;
			data[15] = 0x00;
		}
		wasModified = true;
	}

	@Override
	public void setAIToOnlyAttack(Boolean allowMove) {
		if (allowMove) { // Based off of chapter 1 AI (aggressive minions)
			data[12] = 0x00;
			data[13] = 0x00;
			data[14] = 0x09;
			data[15] = 0x00;
		} else { // Based off of chapter 1 AI (waiting minions)
			data[12] = 0x00;
			data[13] = 0x03;
			data[14] = 0x09; // Some archers use 0x29 here instead. This value changes throughout the chapters, so no idea how it'll perform beyond chapter 1.
			data[15] = 0x00;
		}
		wasModified = true;
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
}
