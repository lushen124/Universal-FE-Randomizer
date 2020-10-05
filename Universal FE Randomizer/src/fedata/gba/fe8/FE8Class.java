package fedata.gba.fe8;

import java.util.Arrays;

import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import util.WhyDoesJavaNotHaveThese;

public class FE8Class implements GBAFEClassData {

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	private Long addressOverride;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private String debugString = "Uninitialized";
	
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
	
	public void initializeDisplayString(String debugString) {
		this.debugString = debugString;
	}
	
	public String displayString() {
		return debugString;
	}

	public int getNameIndex() {
		return (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
	}
	
	public int getDescriptionIndex() {
		return (data[2] & 0xFF) | ((data[3] & 0xFF) << 8);
	}

	@Override
	public int getID() {
		return data[4] & 0xFF;
	}
	
	public void setID(int newID) {
		data[4] = (byte)(newID & 0xFF);
		wasModified = true;
	}
	
	public int getSpriteIndex() {
		return data[6] & 0xFF;
	}
	
	public int getTargetPromotionID() {
		return data[5] & 0xFF;
	}
	public void setTargetPromotionID(int promotionTargetClassID) {
		data[5] = (byte)(promotionTargetClassID & 0xFF);
		wasModified = true;
	}

	public int getHPGrowth() {
		return data[27] & 0xFF;
	}
	
	public void setHPGrowth(int hpGrowth) {
		hpGrowth = WhyDoesJavaNotHaveThese.clamp(hpGrowth, 0, 255);
		data[27] = (byte)(hpGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSTRGrowth() {
		return data[28] & 0xFF;
	}
	
	public void setSTRGrowth(int strGrowth) {
		strGrowth = WhyDoesJavaNotHaveThese.clamp(strGrowth, 0, 255);
		data[28] = (byte)(strGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSKLGrowth() {
		return data[29] & 0xFF;
	}
	
	public void setSKLGrowth(int sklGrowth) {
		sklGrowth = WhyDoesJavaNotHaveThese.clamp(sklGrowth, 0, 255);
		data[29] = (byte)(sklGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSPDGrowth() {
		return data[30] & 0xFF;
	}
	
	public void setSPDGrowth(int spdGrowth) {
		spdGrowth = WhyDoesJavaNotHaveThese.clamp(spdGrowth, 0, 255);
		data[30] = (byte)(spdGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getDEFGrowth() {
		return data[31] & 0xFF;
	}
	
	public void setDEFGrowth(int defGrowth) {
		defGrowth = WhyDoesJavaNotHaveThese.clamp(defGrowth, 0, 255);
		data[31] = (byte)(defGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getRESGrowth() {
		return data[32] & 0xFF;
	}
	
	public void setRESGrowth(int resGrowth) {
		resGrowth = WhyDoesJavaNotHaveThese.clamp(resGrowth, 0, 255);
		data[32] = (byte)(resGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getLCKGrowth() {
		return data[33] & 0xFF;
	}
	
	public void setLCKGrowth(int lckGrowth) {
		lckGrowth = WhyDoesJavaNotHaveThese.clamp(lckGrowth, 0, 255);
		data[33] = (byte)(lckGrowth & 0xFF);
		wasModified = true;
	}

	public int getBaseHP() {
		int baseHP = data[11] & 0xFF;
		if ((baseHP & 0x80) != 0) {
			baseHP |= 0xFFFFFF00;
		}
		
		return baseHP;
	}
	
	public int getBaseSTR() {
		int baseSTR = data[12] & 0xFF;
		if ((baseSTR & 0x80) != 0) {
			baseSTR |= 0xFFFFFF00;
		}
		
		return baseSTR;
	}
	
	public int getBaseSKL() {
		int baseSKL = data[13] & 0xFF;
		if ((baseSKL & 0x80) != 0) {
			baseSKL |= 0xFFFFFF00;
		}
		
		return baseSKL;
	}
	
	public int getBaseSPD() {
		int baseSPD = data[14] & 0xFF;
		if ((baseSPD & 0x80) != 0) {
			baseSPD |= 0xFFFFFF00;
		}
		
		return baseSPD;
	}
	
	public int getBaseDEF() {
		int baseDEF = data[15] & 0xFF;
		if ((baseDEF & 0x80) != 0) {
			baseDEF |= 0xFFFFFF00;
		}
		
		return baseDEF;
	}
	
	public int getBaseRES() {
		int baseRES = data[16] & 0xFF;
		if ((baseRES & 0x80) != 0) {
			baseRES |= 0xFFFFFF00;
		}
		
		return baseRES;
	}
	
	public int getBaseLCK() {
		return 0;
	}

	public int getMaxHP() {
		int maxHP = data[19] & 0xFF;
		if ((maxHP & 0x80) != 0) {
			maxHP |= 0xFFFFFF00;
		}
		
		return maxHP;
	}

	public int getMaxSTR() {
		int maxSTR = data[20] & 0xFF;
		if ((maxSTR & 0x80) != 0) {
			maxSTR |= 0xFFFFFF00;
		}
		
		return maxSTR;
	}

	public int getMaxSKL() {
		int maxSKL = data[21] & 0xFF;
		if ((maxSKL & 0x80) != 0) {
			maxSKL |= 0xFFFFFF00;
		}
		
		return maxSKL;
	}

	public int getMaxSPD() {
		int maxSPD = data[22] & 0xFF;
		if ((maxSPD & 0x80) != 0) {
			maxSPD |= 0xFFFFFF00;
		}
		
		return maxSPD;
	}

	public int getMaxDEF() {
		int maxDEF = data[23] & 0xFF;
		if ((maxDEF & 0x80) != 0) {
			maxDEF |= 0xFFFFFF00;
		}
		
		return maxDEF;
	}

	public int getMaxRES() {
		int maxRES = data[24] & 0xFF;
		if ((maxRES & 0x80) != 0) {
			maxRES |= 0xFFFFFF00;
		}
		
		return maxRES;
	}
	
	public int getMaxLCK() {
		return 30;
	}
	
	public int getPromoHP() {
		return data[34] & 0xFF;
	}
	
	public int getPromoSTR() {
		return data[35] & 0xFF;
	}
	
	public int getPromoSKL() {
		return data[36] & 0xFF;
	}
	
	public int getPromoSPD() {
		return data[37] & 0xFF;
	}
	
	public int getPromoDEF() {
		return data[38] & 0xFF;
	}
	
	public int getPromoRES() {
		return data[39] & 0xFF;
	}
	
	public int getSwordRank() {
		return data[44] & 0xFF;
	}
	
	public void setSwordRank(WeaponRank rank) {
		FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
		int value = fe8Rank.value;
		data[44] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getLanceRank() {
		return data[45] & 0xFF;
	}
	
	public void setLanceRank(WeaponRank rank) {
		FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
		int value = fe8Rank.value;
		data[45] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getAxeRank() {
		return data[46] & 0xFF;
	}
	
	public void setAxeRank(WeaponRank rank) {
		FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
		int value = fe8Rank.value;
		data[46] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getBowRank() {
		return data[47] & 0xFF;
	}
	
	public void setBowRank(WeaponRank rank) {
		FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
		int value = fe8Rank.value;
		data[47] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getAnimaRank() {
		return data[49] & 0xFF;
	}
	
	public void setAnimaRank(WeaponRank rank) {
		FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
		int value = fe8Rank.value;
		data[49] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getLightRank() {
		return data[50] & 0xFF;
	}
	
	public void setLightRank(WeaponRank rank) {
		FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
		int value = fe8Rank.value;
		data[50] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getDarkRank() {
		return data[51] & 0xFF;	
	}
	
	public void setDarkRank(WeaponRank rank) {
		FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
		int value = fe8Rank.value;
		data[51] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getStaffRank() {
		return data[48] & 0xFF;
	}
	
	public void setStaffRank(WeaponRank rank) {
		FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
		int value = fe8Rank.value;
		data[48] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getBaseRankValue() {
		int rankValue = 255;
		
		if (getSwordRank() > 0 && getSwordRank() < rankValue) {
			rankValue = getSwordRank();
		}
		if (getLanceRank() > 0 && getLanceRank() < rankValue) {
			rankValue = getLanceRank();
		}
		if (getAxeRank() > 0 && getAxeRank() < rankValue) {
			rankValue = getAxeRank();
		}
		if (getBowRank() > 0 && getBowRank() < rankValue) {
			rankValue = getBowRank();
		}
		if (getAnimaRank() > 0 && getAnimaRank() < rankValue) {
			rankValue = getAnimaRank();
		}
		if (getDarkRank() > 0 && getDarkRank() < rankValue) {
			rankValue = getDarkRank();
		}
		if (getLightRank() > 0 && getLightRank() < rankValue) {
			rankValue = getLightRank();
		}
		if (getStaffRank() > 0 && getStaffRank() < rankValue) {
			rankValue = getStaffRank();
		}
		
		return rankValue;
	}
	
	public int getMOV() {
		return data[18] & 0xFF;
	}
	
	public void setMOV(int newMOV) {
		newMOV = WhyDoesJavaNotHaveThese.clamp(newMOV, 0, 15);
		data[18] = (byte)(newMOV & 0xFF);
		wasModified = true;
	}
	
	public int getCON() {
		return data[17] & 0xFF;
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
		return addressOverride != null ? addressOverride : originalOffset;
	}
	
	public void overrideAddress(long newAddress) {
		addressOverride = newAddress;
		wasModified = true;
	}
	
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
	
	private WeaponRank getRankForType(WeaponType type) {
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
}
