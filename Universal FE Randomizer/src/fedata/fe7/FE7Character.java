package fedata.fe7;

import fedata.FECharacter;
import util.WhyDoesJavaNotHaveThese;

public class FE7Character implements FECharacter {

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE7Character(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public int getNameIndex() {
		return (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
	}
	
	public int getDescriptionIndex() {
		return (data[2] & 0xFF) | ((data[3] & 0xFF) << 8);
	}
	
	public int getID() {
		return data[4] & 0xFF;
	}
	
	public int getClassID() {
		return data[5] & 0xFF;
	}
	
	public void setClassID(int classID) {
		data[5] = (byte)(classID & 0xFF);
		wasModified = true;
	}
	
	public int getHPGrowth() {
		return data[28] & 0xFF;
	}
	
	public void setHPGrowth(int hpGrowth) {
		hpGrowth = WhyDoesJavaNotHaveThese.clamp(hpGrowth, 0, 255);
		data[28] = (byte)(hpGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSTRGrowth() {
		return data[29] & 0xFF;
	}
	
	public void setSTRGrowth(int strGrowth) {
		strGrowth = WhyDoesJavaNotHaveThese.clamp(strGrowth, 0, 255);
		data[29] = (byte)(strGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSKLGrowth() {
		return data[30] & 0xFF;
	}
	
	public void setSKLGrowth(int sklGrowth) {
		sklGrowth = WhyDoesJavaNotHaveThese.clamp(sklGrowth, 0, 255);
		data[30] = (byte)(sklGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSPDGrowth() {
		return data[31] & 0xFF;
	}
	
	public void setSPDGrowth(int spdGrowth) {
		spdGrowth = WhyDoesJavaNotHaveThese.clamp(spdGrowth, 0, 255);
		data[31] = (byte)(spdGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getDEFGrowth() {
		return data[32] & 0xFF;
	}
	
	public void setDEFGrowth(int defGrowth) {
		defGrowth = WhyDoesJavaNotHaveThese.clamp(defGrowth, 0, 255);
		data[32] = (byte)(defGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getRESGrowth() {
		return data[33] & 0xFF;
	}
	
	public void setRESGrowth(int resGrowth) {
		resGrowth = WhyDoesJavaNotHaveThese.clamp(resGrowth, 0, 255);
		data[33] = (byte)(resGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getLCKGrowth() {
		return data[34] & 0xFF;
	}
	
	public void setLCKGrowth(int lckGrowth) {
		lckGrowth = WhyDoesJavaNotHaveThese.clamp(lckGrowth, 0, 255);
		data[34] = (byte)(lckGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getBaseHP() {
		int baseHP = data[12] & 0xFF;
		if ((baseHP & 0x80) != 0) {
			baseHP |= 0xFFFFFF00;
		}
		
		return baseHP;
	}
	
	public void setBaseHP(int baseHP) {
		data[12] = (byte)(baseHP & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSTR() {
		int baseSTR = data[13] & 0xFF;
		if ((baseSTR & 0x80) != 0) {
			baseSTR |= 0xFFFFFF00;
		}
		
		return baseSTR;
	}
	
	public void setBaseSTR(int baseSTR) {
		data[13] = (byte)(baseSTR & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSKL() {
		int baseSKL = data[14] & 0xFF;
		if ((baseSKL & 0x80) != 0) {
			baseSKL |= 0xFFFFFF00;
		}
		
		return baseSKL;
	}
	
	public void setBaseSKL(int baseSKL) {
		data[14] = (byte)(baseSKL & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSPD() {
		int baseSPD = data[15] & 0xFF;
		if ((baseSPD & 0x80) != 0) {
			baseSPD |= 0xFFFFFF00;
		}
		
		return baseSPD;
	}
	
	public void setBaseSPD(int baseSPD) {
		data[15] = (byte)(baseSPD & 0xFF);
		wasModified = true;
	}
	
	public int getBaseDEF() {
		int baseDEF = data[16] & 0xFF;
		if ((baseDEF & 0x80) != 0) {
			baseDEF |= 0xFFFFFF00;
		}
		
		return baseDEF;
	}
	
	public void setBaseDEF(int baseDEF) {
		data[16] = (byte)(baseDEF & 0xFF);
		wasModified = true;
	}
	
	public int getBaseRES() {
		int baseRES = data[17] & 0xFF;
		if ((baseRES & 0x80) != 0) {
			baseRES |= 0xFFFFFF00;
		}
		
		return baseRES;
	}
	
	public void setBaseRES(int baseRES) {
		data[17] = (byte)(baseRES & 0xFF);
		wasModified = true;
	}
	
	public int getBaseLCK() {
		int baseLCK = data[18] & 0xFF;
		if ((baseLCK & 0x80) != 0) {
			baseLCK |= 0xFFFFFF00;
		}
		
		return baseLCK;
	}
	
	public void setBaseLCK(int baseLCK) {
		data[18] = (byte)(baseLCK & 0xFF);
		wasModified = true;
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
	
	public byte[] getCharacterData() {
		return data;
	}
	
	public Boolean wasModified() {
		return wasModified;
	}
	
	public long getAddressOffset() {
		return originalOffset;
	}
}
