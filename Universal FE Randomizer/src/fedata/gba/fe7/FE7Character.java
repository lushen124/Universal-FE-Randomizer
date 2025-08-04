package fedata.gba.fe7;

import java.util.Arrays;

import fedata.gba.GBAFECharacterData;
import fedata.general.FEBase;

public class FE7Character extends GBAFECharacterData {
	public FE7Character(byte[] data, long originalOffset, Boolean isClassRestricted) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
		this.isClassRestricted = isClassRestricted;
		this.gameType = FEBase.GameType.FE7;
	}
	
	public GBAFECharacterData createCopy(boolean useOriginalData) {
		if (useOriginalData) {
			return new FE7Character(Arrays.copyOf(this.originalData, this.originalData.length), this.originalOffset, this.isClassRestricted);
		}
		return new FE7Character(Arrays.copyOf(this.data, this.data.length), this.originalOffset, this.isClassRestricted);
	}
	
	public int getOriginalID() {
		return originalData[4] & 0xFF;
	}
	
	@Override
	public void prepareForClassRandomization() {
		// null out custom battle animations.
		data[37] = 0;
		data[38] = 0;
	}
	
	@Override 
	public int getCustomUnpromotedBattleSprite() {
		return data[37] & 0xFF;
	}
	
	@Override
	public int getCustomPromotedBattleSprite() {
		return data[38] & 0xFF;
	}
	
	@Override
	public void setCustomUnpromotedBattleSprite(int spriteID) {
		data[37] = (byte)(spriteID & 0xFF);
		wasModified = true;
	}
	
	@Override
	public void setCustomPromotedBattleSprite(int spriteID) {
		data[38] = (byte)(spriteID & 0xFF);
		wasModified = true;
	}
	
	public boolean hasAbility(String abilityString) {
		FE7Data.CharacterAndClassAbility1Mask ability1 = FE7Data.CharacterAndClassAbility1Mask.maskForDisplayString(abilityString);
		if (ability1 != null) {
			return ((byte)getAbility1() & (byte)ability1.ID) != 0;
		}
		FE7Data.CharacterAndClassAbility2Mask ability2 = FE7Data.CharacterAndClassAbility2Mask.maskForDisplayString(abilityString);
		if (ability2 != null) {
			return ((byte)getAbility2() & (byte)ability2.ID) != 0;
		}
		FE7Data.CharacterAndClassAbility3Mask ability3 = FE7Data.CharacterAndClassAbility3Mask.maskForDisplayString(abilityString);
		if (ability3 != null) {
			return ((byte)getAbility3() & (byte)ability3.ID) != 0;
		}
		FE7Data.CharacterAndClassAbility4Mask ability4 = FE7Data.CharacterAndClassAbility4Mask.maskForDisplayString(abilityString);
		if (ability4 != null) {
			return ((byte)getAbility4() & (byte)ability4.ID) != 0;
		}
		
		return false;
	}
}
