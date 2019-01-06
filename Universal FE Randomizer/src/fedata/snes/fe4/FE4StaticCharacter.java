package fedata.snes.fe4;

import fedata.general.FEModifiableData;

// Only for use for playable characters that do not inherit anything (i.e. Gen 1, non-child Gen 2, and Gen 2 replacements).
// Also used for Boss characters with holy blood (major bosses with skills).
public class FE4StaticCharacter implements FEModifiableData {

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE4StaticCharacter(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public int getBaseHP() {
		return data[1] & 0xFF;
	}
	
	public void setBaseHP(int baseHP) {
		data[1] = (byte)(baseHP & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSTR() {
		return data[2] & 0xFF;
	}
	
	public void setBaseSTR(int baseSTR) {
		data[2] = (byte)(baseSTR & 0xFF);
		wasModified = true;
	}
	
	public int getBaseMAG() {
		return data[3] & 0xFF;
	}
	
	public void setBaseMAG(int baseMAG) {
		data[3] = (byte)(baseMAG & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSKL() {
		return data[4] & 0xFF;
	}
	
	public void setBaseSKL(int baseSKL) {
		data[4] = (byte)(baseSKL & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSPD() {
		return data[5] & 0xFF;
	}
	
	public void setBaseSPD(int baseSPD) {
		data[5] = (byte)(baseSPD & 0xFF);
		wasModified = true;
	}
	
	public int getBaseDEF() {
		return data[6] & 0xFF;
	}
	
	public void setBaseDEF(int baseDEF) {
		data[6] = (byte)(baseDEF & 0xFF);
		wasModified = true;
	}
	
	public int getBaseRES() {
		return data[7] & 0xFF;
	}
	
	public void setBaseRES(int baseRES) {
		data[7] = (byte)(baseRES & 0xFF);
		wasModified = true;
	}
	
	public int getBaseLCK() {
		return data[8] & 0xFF;
	}
	
	public void setBaseLCK(int baseLCK) {
		data[8] = (byte)(baseLCK & 0xFF);
		wasModified = true;
	}
	
	public int getClassID() {
		return data[9] & 0xFF;
	}
	
	public void setClassID(int classID) {
		data[9] = (byte)(classID & 0xFF);
		wasModified = true;
	}
	
	public int getStartingMoney() {
		return (data[12] & 0xFF) * 1000;
	}
	
	// Note: Truncated to nearest 1000. Max: 50k.
	public void setStartingMoney(int startingMoney) {
		int value = Math.min(50, Math.max(0, startingMoney) / 1000);
		data[12] = (byte)(value & 0xFF);
		wasModified = true;
	}
	
	public int getCharacterID() {
		return (data[13] & 0xFF) | ((data[14] & 0xFF) << 8);
	}
	
	public boolean isFemale() {
		return data[18] == 0x01;
	}
	
	public int getHPGrowth() {
		return data[20] & 0xFF;
	}
	
	public void setHPGrowth(int hpGrowth) {
		data[20] = (byte)(Math.max(0, hpGrowth) & 0xFF);
		wasModified = true;
	}
	
	public int getSTRGrowth() {
		return data[21] & 0xFF;
	}
	
	public void setSTRGrowth(int strGrowth) {
		data[21] = (byte)(Math.max(0, strGrowth) & 0xFF);
		wasModified = true;
	}
	
	public int getMAGGrowth() {
		return data[22] & 0xFF;
	}
	
	public void setMAGGrowth(int magGrowth) {
		data[22] = (byte)(Math.max(0, magGrowth) & 0xFF);
		wasModified = true;
	}
	
	public int getSKLGrowth() {
		return data[23] & 0xFF;
	}
	
	public void setSKLGrowth(int sklGrowth) {
		data[23] = (byte)(Math.max(0, sklGrowth) & 0xFF);
		wasModified = true;
	}
	
	public int getSPDGrowth() {
		return data[24] & 0xFF;
	}
	
	public void setSPDGrowth(int spdGrowth) {
		data[24] = (byte)(Math.max(0, spdGrowth) & 0xFF);
		wasModified = true;
	}
	
	public int getDEFGrowth() {
		return data[25] & 0xFF;
	}
	
	public void setDEFGrowth(int defGrowth) {
		data[25] = (byte)(Math.max(0, defGrowth) & 0xFF);
		wasModified = true;
	}
	
	public int getRESGrowth() {
		return data[26] & 0xFF;
	}
	
	public void setRESGrowth(int resGrowth) {
		data[26] = (byte)(Math.max(0, resGrowth) & 0xFF);
		wasModified = true;
	}
	
	public int getLCKGrowth() {
		return data[27] & 0xFF;
	}
	
	public void setLCKGrowth(int lckGrowth) {
		data[27] = (byte)(Math.max(0, lckGrowth) & 0xFF);
		wasModified = true;
	}
	
	public int getSkillSlot1Value() {
		return data[28] & 0xFF;
	}
	
	public void setSkillSlot1Value(int skillSlot1) {
		data[28] = (byte)(skillSlot1 & 0xFF);
		wasModified = true;
	}
	
	public int getSkillSlot2Value() {
		return data[29] & 0xFF;
	}
	
	public void setSkillSlot2Value(int skillSlot2) {
		data[29] = (byte)(skillSlot2 & 0xFF);
		wasModified = true;
	}
	
	public int getSkillSlot3Value() {
		return data[30] & 0xFF;
	}
	
	public void setSkillSlot3Value(int skillSlot3) {
		data[30] = (byte)(skillSlot3 & 0xFF);
		wasModified = true;
	}
	
	public int getHolyBlood1Value() {
		return data[31] & 0xFF;
	}
	
	public void setHolyBlood1Value(int holyBloodSlot1) {
		data[31] = (byte)(holyBloodSlot1 & 0xFF);
		wasModified = true;
	}
	
	public int getHolyBlood2Value() {
		return data[32] & 0xFF;
	}
	
	public void setHolyBlood2Value(int holyBloodSlot2) {
		data[32] = (byte)(holyBloodSlot2 & 0xFF);
		wasModified = true;
	}
	
	public int getHolyBlood3Value() {
		return data[33] & 0xFF;
	}
	
	public void setHolyBlood3Value(int holyBloodSlot3) {
		data[33] = (byte)(holyBloodSlot3 & 0xFF);
		wasModified = true;
	}
	
	public int getHolyBlood4Value() {
		return data[34] & 0xFF;
	}
	
	public void setHolyBlood4Value(int holyBloodSlot4) {
		data[34] = (byte)(holyBloodSlot4 & 0xFF);
		wasModified = true;
	}
	
	public int getEquipment1() {
		return data[35] & 0xFF;
	}
	
	public void setEquipment1(int equipment1) {
		data[35] = (byte)(equipment1 & 0xFF);
		wasModified = true;
	}
	
	public int getEquipment2() {
		return data[36] & 0xFF;
	}
	
	public void setEquipment2(int equipment2) {
		data[36] = (byte)(equipment2 & 0xFF);
		wasModified = true;
	}
	
	public int getEquipment3() {
		return data[37] & 0xFF;
	}
	
	public void setEquipment3(int equipment3) {
		data[37] = (byte)(equipment3 & 0xFF);
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
}
