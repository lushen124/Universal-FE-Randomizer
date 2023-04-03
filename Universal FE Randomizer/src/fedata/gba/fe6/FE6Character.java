package fedata.gba.fe6;

import java.util.Arrays;

import fedata.gba.GBAFECharacterData;

public class FE6Character extends GBAFECharacterData {
	
	public FE6Character(byte[] data, long originalOffset, Boolean isClassRestricted) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
		this.isClassRestricted = isClassRestricted;
	}
	
	public GBAFECharacterData createCopy(boolean useOriginalData) {
		if (useOriginalData) {
			return new FE6Character(Arrays.copyOf(this.originalData, this.originalData.length), this.originalOffset, this.isClassRestricted);
		}
		return new FE6Character(Arrays.copyOf(this.data, this.data.length), this.originalOffset, this.isClassRestricted);
	}
	
	@Override
	public void setIsLord(boolean isLord) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		// Mark as Lord (Ability 2)
		byte oldValue = (byte)(data[41] & 0xFF);
		byte newValue = isLord ? (byte)(oldValue | 0x20) : (byte)(oldValue & 0xDF);
		data[41] = newValue;
		
		// Give Sword of Seals lock (Ability 3)
		oldValue = (byte)(data[42] & 0xFF);
		newValue = isLord ? (byte)(oldValue | 0x01) : (byte)(oldValue & 0xFE);
		data[42] = newValue; 
		
		wasModified = true;
	}
	
	@Override
	public void enableWeaponLock(int lockMask) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[42] |= lockMask;
		wasModified = true;
	}
}
