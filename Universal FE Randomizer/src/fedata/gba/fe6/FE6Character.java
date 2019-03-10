package fedata.gba.fe6;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fedata.gba.GBAFECharacterData;
import util.WhyDoesJavaNotHaveThese;

public class FE6Character implements GBAFECharacterData {

	public enum Affinity {
		NONE(0x00), FIRE(0x01), THUNDER(0x02), WIND(0x03), WATER(0x04), DARK(0x05), LIGHT(0x06), ANIMA(0x07);
		
		public int value;
		
		private static Map<Integer, Affinity> map = new HashMap<Integer, Affinity>();
		
		static {
			for (Affinity affinity : Affinity.values()) {
				map.put(affinity.value, affinity);
			}
		}
		
		private Affinity(final int value) { this.value = value; }
		
		public static Affinity affinityWithID(int value) {
			return map.get(value);
		}
		
		public static Affinity[] validAffinities() { 
			return new Affinity[] {FIRE, THUNDER, WIND, WATER, DARK, LIGHT, ANIMA};
		}
	}
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private Boolean isClassRestricted = false;
	
	private Boolean isReadOnly = false;
	
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
	
	public void lock() {
		isReadOnly = true;
	}
	
	public void unlock() {
		isReadOnly = false;
	}
	
	public Boolean isClassRestricted() {
		return isClassRestricted;
	}
	
	public int getNameIndex() {
		return (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
	}
	
	public void setNameIndex(int newIndex) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[0] = (byte)(newIndex & 0xFF);
		data[1] = (byte)((newIndex >> 8) & 0xFF);
		wasModified = true;
	}
	
	public int getDescriptionIndex() {
		return (data[2] & 0xFF) | ((data[3] & 0xFF) << 8);
	}
	
	public void setDescriptionIndex(int newIndex) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[2] = (byte)(newIndex & 0xFF);
		data[3] = (byte)((newIndex >> 8) & 0xFF);
		wasModified = true;
	}
	
	public int getID() {
		return data[4] & 0xFF;
	}
	
	public int getClassID() {
		return data[5] & 0xFF;
	}
	
	public void setClassID(int classID) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[5] = (byte)(classID & 0xFF);
		wasModified = true;
	}
	
	public int getFaceID() {
		return data[6] & 0xFF;
	}
	
	public void setFaceID(int faceID) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[6] = (byte)(faceID & 0xFF);
		wasModified = true;
	}
	
	public int getLevel() {
		return data[11] & 0xFF;
	}
	
	public int getHPGrowth() {
		return data[28] & 0xFF;
	}
	
	public void setHPGrowth(int hpGrowth) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		hpGrowth = WhyDoesJavaNotHaveThese.clamp(hpGrowth, 0, 255);
		data[28] = (byte)(hpGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSTRGrowth() {
		return data[29] & 0xFF;
	}
	
	public void setSTRGrowth(int strGrowth) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		strGrowth = WhyDoesJavaNotHaveThese.clamp(strGrowth, 0, 255);
		data[29] = (byte)(strGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSKLGrowth() {
		return data[30] & 0xFF;
	}
	
	public void setSKLGrowth(int sklGrowth) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		sklGrowth = WhyDoesJavaNotHaveThese.clamp(sklGrowth, 0, 255);
		data[30] = (byte)(sklGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSPDGrowth() {
		return data[31] & 0xFF;
	}
	
	public void setSPDGrowth(int spdGrowth) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		spdGrowth = WhyDoesJavaNotHaveThese.clamp(spdGrowth, 0, 255);
		data[31] = (byte)(spdGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getDEFGrowth() {
		return data[32] & 0xFF;
	}
	
	public void setDEFGrowth(int defGrowth) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		defGrowth = WhyDoesJavaNotHaveThese.clamp(defGrowth, 0, 255);
		data[32] = (byte)(defGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getRESGrowth() {
		return data[33] & 0xFF;
	}
	
	public void setRESGrowth(int resGrowth) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		resGrowth = WhyDoesJavaNotHaveThese.clamp(resGrowth, 0, 255);
		data[33] = (byte)(resGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getLCKGrowth() {
		return data[34] & 0xFF;
	}
	
	public void setLCKGrowth(int lckGrowth) {
		assert !isReadOnly : "Attempted to modify a locked character.";
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
		assert !isReadOnly : "Attempted to modify a locked character.";
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
		assert !isReadOnly : "Attempted to modify a locked character.";
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
		assert !isReadOnly : "Attempted to modify a locked character.";
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
		assert !isReadOnly : "Attempted to modify a locked character.";
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
		assert !isReadOnly : "Attempted to modify a locked character.";
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
		assert !isReadOnly : "Attempted to modify a locked character.";
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
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[18] = (byte)(baseLCK & 0xFF);
		wasModified = true;
	}
	
	public int getSwordRank() {
		return data[20] & 0xFF;
	}
	
	public void setSwordRank(int rank) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[20] = (byte)(rank & 0xFF);
		wasModified = true;
	}
	
	public int getLanceRank() {
		return data[21] & 0xFF;
	}
	
	public void setLanceRank(int rank) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[21] = (byte)(rank & 0xFF);
		wasModified = true;
	}
	
	public int getAxeRank() {
		return data[22] & 0xFF;
	}
	
	public void setAxeRank(int rank) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[22] = (byte)(rank & 0xFF);
		wasModified = true;
	}
	
	public int getBowRank() {
		return data[23] & 0xFF;
	}
	
	public void setBowRank(int rank) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[23] = (byte)(rank & 0xFF);
		wasModified = true;
	}
	
	public int getAnimaRank() {
		return data[25] & 0xFF;
	}
	
	public void setAnimaRank(int rank) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[25] = (byte)(rank & 0xFF);
		wasModified = true;
	}
	
	public int getDarkRank() {
		return data[27] & 0xFF;
	}
	
	public void setDarkRank(int rank) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[27] = (byte)(rank & 0xFF);
		wasModified = true;
	}
	
	public int getLightRank() {
		return data[26] & 0xFF;
	}
	
	public void setLightRank(int rank) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[26] = (byte)(rank & 0xFF);
		wasModified = true;
	}
	
	public int getStaffRank() {
		return data[24] & 0xFF;
	}
	
	public void setStaffRank(int rank) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[24] = (byte)(rank & 0xFF);
		wasModified = true;
	}
	
	public int getConstitution() {
		int constitution = data[19] & 0xFF;
		if ((constitution & 0x80) != 0) {
			constitution |= 0xFFFFFF00;
		}
		
		return constitution;
	}
	
	public void setConstitution(int newCON) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[19] = (byte)(newCON & 0xFF);
		wasModified = true;
	}
	
	public int getAffinityValue() {
		return data[9] & 0xFF;
	}
	
	public void setAffinityValue(int newAffinity) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[9] = (byte)(newAffinity & 0xFF);
		wasModified = true;
	}
	
	public String getAffinityName() {
		return Affinity.affinityWithID(getAffinityValue()).toString();
	}
	
	public int getUnpromotedPaletteIndex() {
		return data[35] & 0xFF;
	}
	
	public void setUnpromotedPaletteIndex(int newIndex) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[35] = (byte)(newIndex & 0xFF);
		wasModified = true;
	}
	
	public int getPromotedPaletteIndex() {
		return data[36] & 0xFF;
	}
	
	public void setPromotedPaletteIndex(int newIndex) {
		assert !isReadOnly : "Attempted to modify a locked character.";
		data[36] = (byte)(newIndex & 0xFF);
		wasModified = true;
	}
	
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
	
	public void prepareForClassRandomization() {
		// nothing to do here
	}
}
