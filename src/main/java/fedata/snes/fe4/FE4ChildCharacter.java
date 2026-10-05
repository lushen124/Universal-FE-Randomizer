package fedata.snes.fe4;

import fedata.general.FEModifiableData;

// Only for use with characters that inherit from parents (Gen 2 Children)
public class FE4ChildCharacter implements FEModifiableData {
	
	public enum Influence {
		MOTHER, FATHER;
		
		public int rawValue() {
			switch (this) {
			case MOTHER: return 0;
			case FATHER: return 1;
			}
			
			return 0;
		}
		
		public static Influence valueOf(int influenceValue) {
			if (influenceValue == 0) { return MOTHER; }
			return FATHER;
		}
	}
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE4ChildCharacter(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public int getClassID() {
		return data[1] & 0xFF;
	}
	
	public void setClassID(int classID) {
		data[1] = (byte)(classID & 0xFF);
		wasModified = true;
	}
	
	public int getCharacterID() {
		return (data[3] & 0xFF) | ((data[4] & 0xFF) << 8);
	}
	
	public boolean isFemale() {
		return data[8] == 0x01;
	}
	
	public int getEquipment1() {
		return data[9] & 0xFF;
	}
	
	public void setEquipment1(int equipment1) {
		data[9] = (byte)(equipment1 & 0xFF);
		wasModified = true;
	}
	
	public int getEquipment2() {
		return data[10] & 0xFF;
	}
	
	public void setEquipment2(int equipment2) {
		data[10] = (byte)(equipment2 & 0xFF);
		wasModified = true;
	}
	
	public Influence getMajorInfluence() {
		return Influence.valueOf(data[11] & 0xFF);
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
