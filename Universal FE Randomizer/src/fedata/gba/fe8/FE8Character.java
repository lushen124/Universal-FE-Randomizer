package fedata.gba.fe8;

import java.util.Arrays;

import fedata.gba.GBAFECharacterData;
import fedata.general.FEBase;

public class FE8Character extends GBAFECharacterData {
	public FE8Character(byte[] data, long originalOffset, Boolean isClassRestricted) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
		this.isClassRestricted = isClassRestricted;
		this.gameType = FEBase.GameType.FE8;
	}

	public GBAFECharacterData createCopy(boolean useOriginalData) {
		if (useOriginalData) {
			return new FE8Character(Arrays.copyOf(this.originalData, this.originalData.length), this.originalOffset,
					this.isClassRestricted);
		}

		return new FE8Character(Arrays.copyOf(this.data, this.data.length), this.originalOffset,
				this.isClassRestricted);
	}

	public int getOriginalDescriptionIndex() {
		return (originalData[2] & 0xFF) | ((originalData[3] & 0xFF) << 8);
	}

	public int getOriginalID() {
		return originalData[4] & 0xFF;
	}

	// FE8 uses a separate table away from the character data.
	@Override
	public int getUnpromotedPaletteIndex() {
		return 0;
	}

	@Override
	public void setUnpromotedPaletteIndex(int newIndex) {
	}

	@Override
	public int getPromotedPaletteIndex() {
		return 0;
	}

	@Override
	public void setPromotedPaletteIndex(int newIndex) {
	}

	public boolean hasAbility(String abilityString) {
		FE8Data.CharacterAndClassAbility1Mask ability1 = FE8Data.CharacterAndClassAbility1Mask.maskForDisplayString(abilityString);
		if (ability1 != null) {
			return ((byte)getAbility1() & (byte)ability1.ID) != 0;
		}
		FE8Data.CharacterAndClassAbility2Mask ability2 = FE8Data.CharacterAndClassAbility2Mask.maskForDisplayString(abilityString);
		if (ability2 != null) {
			return ((byte)getAbility2() & (byte)ability2.ID) != 0; 
		}
		FE8Data.CharacterAndClassAbility3Mask ability3 = FE8Data.CharacterAndClassAbility3Mask.maskForDisplayString(abilityString);
		if (ability3 != null) {
			return ((byte)getAbility3() & (byte)ability3.ID) != 0;
		}
		FE8Data.CharacterAndClassAbility4Mask ability4 = FE8Data.CharacterAndClassAbility4Mask.maskForDisplayString(abilityString);
		if (ability4 != null) {
			return ((byte)getAbility4() & (byte)ability4.ID) != 0;
		}
		
		return false;
	}
}
