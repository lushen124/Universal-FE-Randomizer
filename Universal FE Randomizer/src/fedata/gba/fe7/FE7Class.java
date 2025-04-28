package fedata.gba.fe7;

import java.util.Arrays;

import fedata.gba.GBAFEClassData;

public class FE7Class extends GBAFEClassData {
	
	int promoCON;
	
	
	public FE7Class(GBAFEClassData reference) {
		super();
		this.originalData = Arrays.copyOf(reference.getData(), reference.getData().length);
		this.data = Arrays.copyOf(reference.getData(), reference.getData().length);
	}

	public FE7Class(byte[] data, long originalOffset, GBAFEClassData demotedClass) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
		
		if (demotedClass != null) {
			promoCON = getCON() - demotedClass.getCON();
		}
	}
	


	@Override
	public GBAFEClassData createClone() {
		GBAFEClassData clone = new FE7Class(this);
		clone.setOriginalOffset(-1);
		return clone;
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
	
	public boolean isPromotedClass() {
		return ((byte)getAbility2() & (byte)FE7Data.CharacterAndClassAbility2Mask.PROMOTED.ID) != 0;
	}
	
	@Override
	public int getPromoCON() {
		return promoCON;
	}
}
