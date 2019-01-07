package fedata.snes.fe4;

import fedata.general.FEModifiableData;

// Only for use with enemy characters definitions (not army definitions).
public class FE4EnemyCharacter implements FEModifiableData {
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE4EnemyCharacter(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public int getCharacterID() {
		return (data[1] & 0xFF) | ((data[2] & 0xFF) << 8);
	}
	
	public int getClassID() {
		return data[5] & 0xFF;
	}
	
	public void setClassID(int classID) {
		data[5] = (byte)(classID & 0xFF);
		wasModified = true;
	}
	
	public boolean isFemale() {
		return data[6] == 0x01;
	}
	
	public int getLevel() {
		return data[7] & 0xFF;
	}
	
	public void setLevel(int newLevel) {
		data[7] = (byte)(Math.max(1, Math.min(30, newLevel)) & 0xFF);
		wasModified = true;
	}
	
	public int getLeadership() {
		return data[8] & 0xFF;
	}
	
	public int getEquipment1() {
		return data[10] & 0xFF;
	}
	
	public void setEquipment1(int equipment1) {
		data[10] = (byte)(equipment1 & 0xFF);
		wasModified = true;
	}
	
	public int getEquipment2() {
		return data[11] & 0xFF;
	}
	
	public void setEquipment2(int equipment2) {
		data[11] = (byte)(equipment2 & 0xFF);
		wasModified = true;
	}
	
	public int getDropableEquipment() {
		return data[12] & 0xFF;
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
