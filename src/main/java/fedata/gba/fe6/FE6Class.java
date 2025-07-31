package fedata.gba.fe6;

import java.util.Arrays;

import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import util.WhyDoesJavaNotHaveThese;

public class FE6Class extends GBAFEClassData {

	int promoHP;
	int promoSTR;
	int promoSKL;
	int promoSPD;
	int promoDEF;
	int promoRES;
	int promoCON;

	public FE6Class(GBAFEClassData reference) {
		super();
		this.originalData = Arrays.copyOf(reference.getData(), reference.getData().length);
		this.data = Arrays.copyOf(reference.getData(), reference.getData().length);

		this.promoHP = reference.getPromoHP();
		this.promoSTR = reference.getPromoSTR();
		this.promoSKL = reference.getPromoSKL();
		this.promoSPD = reference.getPromoSPD();
		this.promoDEF = reference.getPromoDEF();
		this.promoRES = reference.getPromoRES();
		this.promoCON = reference.getPromoCON();
	}

	public FE6Class(byte[] data, long originalOffset, GBAFEClassData demotedClass) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;

		if (demotedClass != null) {
			promoHP = getBaseHP() - demotedClass.getBaseHP();
			promoSTR = getBaseSTR() - demotedClass.getBaseSTR();
			promoSKL = getBaseSKL() - demotedClass.getBaseSKL();
			promoSPD = getBaseSPD() - demotedClass.getBaseSPD();
			promoDEF = getBaseDEF() - demotedClass.getBaseDEF();
			promoRES = getBaseRES() - demotedClass.getBaseRES();
			promoCON = getCON() - demotedClass.getCON();
		}
	}

	public int getLCKGrowth() {
		return 0;
	}

	public void setLCKGrowth(int lckGrowth) {
		// FE6 has no luck growth
	}

	public int getPromoHP() {
		return promoHP;
	}

	public int getPromoSTR() {
		return promoSTR;
	}

	public int getPromoSKL() {
		return promoSKL;
	}

	public int getPromoSPD() {
		return promoSPD;
	}

	public int getPromoDEF() {
		return promoDEF;
	}

	public int getPromoRES() {
		return promoRES;
	}
	
	public int getPromoCON() {
		return promoCON;
	}

	@Override
	public int getSwordRank() {
		return data[40] & 0xFF;
	}

	@Override
	public void setSwordRank(WeaponRank rank) {
		FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
		int value = fe6Rank.value;
		data[40] = (byte) (value & 0xFF);
		wasModified = true;
	}

	@Override
	public int getLanceRank() {
		return data[41] & 0xFF;
	}

	@Override
	public void setLanceRank(WeaponRank rank) {
		FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
		int value = fe6Rank.value;
		data[41] = (byte) (value & 0xFF);
		wasModified = true;
	}

	@Override
	public int getAxeRank() {
		return data[42] & 0xFF;
	}

	@Override
	public void setAxeRank(WeaponRank rank) {
		FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
		int value = fe6Rank.value;
		data[42] = (byte) (value & 0xFF);
		wasModified = true;
	}

	@Override
	public int getBowRank() {
		return data[43] & 0xFF;
	}

	@Override
	public void setBowRank(WeaponRank rank) {
		FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
		int value = fe6Rank.value;
		data[43] = (byte) (value & 0xFF);
		wasModified = true;
	}

	@Override
	public int getAnimaRank() {
		return data[45] & 0xFF;
	}

	@Override
	public void setAnimaRank(WeaponRank rank) {
		FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
		int value = fe6Rank.value;
		data[45] = (byte) (value & 0xFF);
		wasModified = true;
	}

	@Override
	public int getLightRank() {
		return data[46] & 0xFF;
	}

	@Override
	public void setLightRank(WeaponRank rank) {
		FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
		int value = fe6Rank.value;
		data[46] = (byte) (value & 0xFF);
		wasModified = true;
	}

	@Override
	public int getDarkRank() {
		return data[47] & 0xFF;
	}

	@Override
	public void setDarkRank(WeaponRank rank) {
		FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
		int value = fe6Rank.value;
		data[47] = (byte) (value & 0xFF);
		wasModified = true;
	}

	@Override
	public int getStaffRank() {
		return data[44] & 0xFF;
	}

	@Override
	public void setStaffRank(WeaponRank rank) {
		FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
		int value = fe6Rank.value;
		data[44] = (byte) (value & 0xFF);
		wasModified = true;
	}

	@Override
	public Boolean canUseWeapon(GBAFEItemData weapon) {
		if (weapon == null) {
			return false;
		}

		WeaponType type = weapon.getType();
		return getRankForType(type) != WeaponRank.NONE;
	}

	protected WeaponRank getRankForType(WeaponType type) {
		int rankValue = 0;
		switch (type) {
		case SWORD:
			rankValue = getSwordRank();
			break;
		case LANCE:
			rankValue = getLanceRank();
			break;
		case AXE:
			rankValue = getAxeRank();
			break;
		case BOW:
			rankValue = getBowRank();
			break;
		case ANIMA:
			rankValue = getAnimaRank();
			break;
		case LIGHT:
			rankValue = getLightRank();
			break;
		case DARK:
			rankValue = getDarkRank();
			break;
		case STAFF:
			rankValue = getStaffRank();
			break;
		default:
			rankValue = 0;
		}

		if (rankValue == 0) {
			return WeaponRank.NONE;
		}

		return FE6Data.Item.FE6WeaponRank.valueOf(rankValue).toGeneralRank();
	}
	
	@Override
	public int getAbility1() {
		return data[36] & 0xFF;
	}
	
	@Override
	public int getAbility2() {
		return data[37] & 0xFF;
	}
	
	@Override
	public int getAbility3() {
		return data[38] & 0xFF;
	}
	
	@Override
	public int getAbility4() {
		return data[39] & 0xFF;
	}
	
	@Override
	public long getBattleAnimationPointer() {
		byte[] pointer = WhyDoesJavaNotHaveThese.subArray(data, 48, 4);
		long offset = WhyDoesJavaNotHaveThese.longValueFromByteArray(pointer, true);
		return offset;
	}
	
	@Override
	public long getMovementTypePointer() {
		byte[] pointer = WhyDoesJavaNotHaveThese.subArray(data, 52, 4);
		long offset = WhyDoesJavaNotHaveThese.longValueFromByteArray(pointer, true);
		return offset;
	}
	
	@Override
	public Long getRainMovementPointer() {
		return null;
	}
	
	@Override
	public Long getSnowMovementPointer() {
		return null;
	}
	
	@Override
	public long getTerrainAvoidBonusPointer() {
		byte[] pointer = WhyDoesJavaNotHaveThese.subArray(data, 56, 4);
		long offset = WhyDoesJavaNotHaveThese.longValueFromByteArray(pointer, true);
		return offset;
	}
	
	@Override
	public long getTerrainDefenseBonusPointer() {
		byte[] pointer = WhyDoesJavaNotHaveThese.subArray(data, 60, 4);
		long offset = WhyDoesJavaNotHaveThese.longValueFromByteArray(pointer, true);
		return offset;
	}
	
	@Override
	public long getTerrainResistanceBonusPointer() {
		byte[] pointer = WhyDoesJavaNotHaveThese.subArray(data, 64, 4);
		long offset = WhyDoesJavaNotHaveThese.longValueFromByteArray(pointer, true);
		return offset;
	}
	
	@Override
	public long getTerrainUnknownBonusPointer() {
		byte[] pointer = WhyDoesJavaNotHaveThese.subArray(data, 68, 4);
		long offset = WhyDoesJavaNotHaveThese.longValueFromByteArray(pointer, true);
		return offset;
	}

	public void removeLordLocks() {
		data[38] &= 0xFE;
		wasModified = true;
	}

	public GBAFEClassData createClone() {
		FE6Class clone = new FE6Class(this);
		clone.originalOffset = -1;
		return clone;
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
	
	public boolean isPromotedClass() {
		return ((byte)getAbility2() & (byte)FE6Data.CharacterAndClassAbility2Mask.PROMOTED.ID) != 0;
	}
}