package fedata.fe7;

import java.util.ArrayList;

import fedata.FEChapterUnit;

public class FE7ChapterUnit implements FEChapterUnit {

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private Boolean modifiable = true;
	
	public FE7ChapterUnit(byte[] data, long originalOffset, Boolean modifiable) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
		this.modifiable = modifiable;
	}

	public int getCharacterNumber() {
		return data[0] & 0xFF;
	}

	public int getStartingClass() {
		return data[1] & 0xFF;
	}

	public void setStartingClass(int classID) {
		if (!modifiable) {
			return;
		}
		
		data[1] = (byte)(classID & 0xFF);
		wasModified = true;
	}
	
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
		if (!modifiable) {
			return;
		}
		
		data[8] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem2() {
		return data[9] & 0xFF;
	}

	public void setItem2(int itemID) {
		if (!modifiable) {
			return;
		}
		
		data[9] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem3() {
		return data[10] & 0xFF;
	}

	public void setItem3(int itemID) {
		if (!modifiable) {
			return;
		}
		
		data[10] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem4() {
		return data[11] & 0xFF;
	}

	public void setItem4(int itemID) {
		if (!modifiable) {
			return;
		}
		
		data[11] = (byte)(itemID & 0xFF);
		wasModified = true;
	}
	
	public void giveItems(int[] itemIDs) {
		if (!modifiable) {
			return;
		}
		
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
		
		if (getItem3() == 0) {
			setItem3(getItem4());
			setItem4(0);
		}
		if (getItem2() == 0) {
			setItem2(getItem3());
			setItem3(0);
		}
		if (getItem1() == 0) {
			setItem1(getItem2());
			setItem2(0);
		}
	}
	
	public void resetData() {
		data = originalData;
		wasModified = false;
	}
	
	public void commitChanges() {
		if (!modifiable) {
			return;
		}
		
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
	
	public Boolean isModifiable() {
		return modifiable;
	}

	public void setAIToHeal(Boolean allowAttack) {
		data[12] = (byte) (allowAttack ? 0x0F : 0x0E);
		wasModified = true;
	}

	public void setAIToOnlyAttack(Boolean allowMove) {
		data[12] = (byte) (allowMove ? 0x00 : 0x03);
		wasModified = true;
	}
}
