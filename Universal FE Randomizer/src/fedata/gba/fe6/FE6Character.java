package fedata.gba.fe6;

import java.util.Arrays;

import fedata.gba.GBAFECharacterData;
import fedata.general.FEBase.GameType;

public class FE6Character extends GBAFECharacterData {
	
	public FE6Character(byte[] data, long originalOffset, Boolean isClassRestricted) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
		this.isClassRestricted = isClassRestricted;
		this.gameType = GameType.FE6;

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
	
	public boolean hasAbility(String abilityString) {
		FE6Data.CharacterAndClassAbility1Mask ability1 = FE6Data.CharacterAndClassAbility1Mask.maskForDisplayString(abilityString);
		if (ability1 != null) {
			return ((byte)getAbility1() & (byte)ability1.ID) != 0; 
		}
		FE6Data.CharacterAndClassAbility2Mask ability2 = FE6Data.CharacterAndClassAbility2Mask.maskForDisplayString(abilityString);
		if (ability2 != null) {
			return ((byte)getAbility2() & (byte)ability2.ID) != 0;
		}
		FE6Data.CharacterAndClassAbility3Mask ability3 = FE6Data.CharacterAndClassAbility3Mask.maskForDisplayString(abilityString);
		if (ability3 != null) {
			return ((byte)getAbility3() & (byte)ability3.ID) != 0; 
		}
		FE6Data.CharacterAndClassAbility4Mask ability4 = FE6Data.CharacterAndClassAbility4Mask.maskForDisplayString(abilityString);
		if (ability4 != null) {
			return ((byte)getAbility4() & (byte)ability4.ID) != 0;
		}
		
		return false;
	}
}
