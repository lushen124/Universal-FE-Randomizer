package fedata.gba.fe8;

import java.util.Arrays;

import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;

public class FE8Class extends GBAFEClassData {
	
	public FE8Class(GBAFEClassData reference) {
		super();
		this.originalData = Arrays.copyOf(reference.getData(), reference.getData().length);
		this.data = Arrays.copyOf(reference.getData(), reference.getData().length);
	}
	
	public FE8Class(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}


	@Override
	public Boolean canUseWeapon(GBAFEItemData weapon) {
		if (weapon == null) { return false; }
		
		WeaponType type = weapon.getType();
		if (type == WeaponType.NOT_A_WEAPON) {
			// Do we use monster weapons?
			if (usesMonsterWeapons()) {
				// Check if it's a monster weapon.
				return FE8Data.Item.allMonsterWeapons.contains(FE8Data.Item.valueOf(getID()));
			}
			
			return false;
		}
		return getRankForType(type) != WeaponRank.NONE;
	}
	
	private Boolean usesMonsterWeapons() {
		return (getSwordRank() == 0 && getLanceRank() == 0 && getAxeRank() == 0 && getBowRank() == 0 &&
				getAnimaRank() == 0 && getLightRank() == 0 && getDarkRank() == 0 &&
				FE8Data.CharacterClass.allMonsterClasses.contains(FE8Data.CharacterClass.valueOf(getID())));
	}
	
	protected WeaponRank getRankForType(WeaponType type) {
		int rankValue = 0;
		switch (type) {
		case SWORD: rankValue = getSwordRank(); break;
		case LANCE: rankValue = getLanceRank(); break;
		case AXE: rankValue = getAxeRank(); break;
		case BOW: rankValue = getBowRank(); break;
		case ANIMA: rankValue = getAnimaRank(); break;
		case LIGHT: rankValue = getLightRank(); break;
		case DARK: rankValue = getDarkRank(); break;
		case STAFF: rankValue = getStaffRank(); break;
		default: rankValue = 0;
		}
		
		if (rankValue == 0) { return WeaponRank.NONE; }
		
		return FE8Data.Item.FE8WeaponRank.valueOf(rankValue).toGeneralRank();
	}
	
	public GBAFEClassData createClone() {
		FE8Class clone = new FE8Class(this);
		clone.originalOffset = -1;
		return clone;
	}

	@Override
	public void removeLordLocks() {
		data[42] &= ~FE8Data.CharacterAndClassAbility3Mask.UNUSED_WEAPON_LOCK.ID;
		data[43] &= 0x0F;
		wasModified = true;
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
	
	public boolean isPromotedClass() {
		return ((byte)getAbility2() & (byte)FE8Data.CharacterAndClassAbility2Mask.PROMOTED.ID) != 0;
	}
}
