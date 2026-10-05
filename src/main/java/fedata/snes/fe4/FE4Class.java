package fedata.snes.fe4;

import java.util.ArrayList;
import java.util.List;

import fedata.general.FEModifiableData;

public class FE4Class implements FEModifiableData {
	
	public enum ClassSkills {
		CANTO(1, 0x1), GREAT_SHIELD(1, 0x2), WRATH(1, 0x4), PURSUIT(1, 0x8), ADEPT(1, 0x10), STEAL(1, 0x20), DANCE(1, 0x80),
		CHARM(2, 0x8), CRITICAL(2, 0x10);
		
		public int slot;
		public int mask;
		
		private ClassSkills(int slot, int mask) { this.slot = slot; this.mask = mask; }
	}

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE4Class(byte[] data, long originalOffset) {
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
	
	public int getHPGrowth() {
		return data[8] & 0xFF;
	}
	
	public void setHPGrowth(int hpGrowth) {
		data[8] = (byte)(hpGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSTRGrowth() {
		return data[9] & 0xFF;
	}
	
	public void setSTRGrowth(int strGrowth) {
		data[9] = (byte)(strGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getMAGGrowth() {
		return data[10] & 0xFF;
	}
	
	public void setMAGGrowth(int magGrowth) {
		data[10] = (byte)(magGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSKLGrowth() {
		return data[11] & 0xFF;
	}
	
	public void setSKLGrowth(int sklGrowth) {
		data[11] = (byte)(sklGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSPDGrowth() {
		return data[12] & 0xFF;
	}
	
	public void setSPDGrowth(int spdGrowth) {
		data[12] = (byte)(spdGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getDEFGrowth() {
		return data[13] & 0xFF;
	}
	
	public void setDEFGrowth(int defGrowth) {
		data[13] = (byte)(defGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getRESGrowth() {
		return data[14] & 0xFF;
	}
	
	public void setRESGrowth(int resGrowth) {
		data[14] = (byte)(resGrowth & 0xFF);
		wasModified = true;
	}
	
	public FE4Data.Item.WeaponRank getSwordRank() { return getWeaponRank(data[15] & 0xFF); }
	public void setSwordRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 15); }
	
	public FE4Data.Item.WeaponRank getLanceRank() { return getWeaponRank(data[16] & 0xFF); }
	public void setLanceRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 16); }
	
	public FE4Data.Item.WeaponRank getAxeRank() { return getWeaponRank(data[17] & 0xFF); }
	public void setAxeRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 17); }
	
	public FE4Data.Item.WeaponRank getBowRank() { return getWeaponRank(data[18] & 0xFF); }
	public void setBowRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 18); }
	
	public FE4Data.Item.WeaponRank getStaffRank() { return getWeaponRank(data[19] & 0xFF); }
	public void setStaffRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 19); }
	
	public FE4Data.Item.WeaponRank getFireRank() { return getWeaponRank(data[20] & 0xFF); }
	public void setFireRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 20); }
	
	public FE4Data.Item.WeaponRank getThunderRank() { return getWeaponRank(data[21] & 0xFF); }
	public void setThunderRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 21); }
	
	public FE4Data.Item.WeaponRank getWindRank() { return getWeaponRank(data[22] & 0xFF); }
	public void setWindRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 22); }
	
	public FE4Data.Item.WeaponRank getLightRank() { return getWeaponRank(data[23] & 0xFF); }
	public void setLightRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 23); }
	
	public FE4Data.Item.WeaponRank getDarkRank() { return getWeaponRank(data[24] & 0xFF); }
	public void setDarkRank(FE4Data.Item.WeaponRank rank) { setWeaponRank(rank, 24); }
	
	private void setWeaponRank(FE4Data.Item.WeaponRank rank, int dataIndex) {
		switch (rank) {
		case NONE:
			data[dataIndex] = 0x3;
			break;
		case C:
			data[dataIndex] = 0x2;
			break;
		case B:
			data[dataIndex] = 0x1;
			break;
		case A:
			data[dataIndex] = 0x0;
			break;
		default:
			return;
		}
		wasModified = true;
	}
	
	private FE4Data.Item.WeaponRank getWeaponRank(int value) {
		if (value == 0x3) {
			return FE4Data.Item.WeaponRank.NONE;
		} else if (value == 0x2) {
			return FE4Data.Item.WeaponRank.C;
		} else if (value == 0x1) {
			return FE4Data.Item.WeaponRank.B;
		} else if (value == 0x0) {
			return FE4Data.Item.WeaponRank.A;
		}
		
		return FE4Data.Item.WeaponRank.NONE;
	}
	
	public int getMovement() {
		return data[25] & 0xFF;
	}
	
	public void setMovement(int movement) {
		data[25] = (byte)(movement & 0xFF);
		wasModified = true;
	}
	
	public List<ClassSkills> getSlot1ClassSkills() {
		return getSkills(1, data[28] & 0xFF);
	}
	
	public void setSlot1ClassSkills(List<ClassSkills> skills) {
		data[28] = (byte)(valueForSkills(1, skills) & 0xFF);
		wasModified = true;
	}
	
	public List<ClassSkills> getSlot2ClassSkills() {
		return getSkills(2, data[29] & 0xFF);
	}
	
	public void setSlot2ClassSkills(List<ClassSkills> skills) {
		data[29] = (byte)(valueForSkills(2, skills) & 0xFF);
		wasModified = true;
	}
	
	private int valueForSkills(int slot, List<ClassSkills> skills) {
		int value = 0;
		for (ClassSkills skill : skills) {
			if (skill.slot == slot) {
				value |= skill.mask;
			}
		}
		
		return value;
	}
	
	private List<ClassSkills> getSkills(int slot, int value) {
		List<ClassSkills> skills = new ArrayList<ClassSkills>();
		for (ClassSkills skill : ClassSkills.values()) {
			if (skill.slot == slot && ((value & skill.mask) != 0)) {
				skills.add(skill);
			}
		}
		
		return skills;
	}
	
	public int getStartingGold() {
		return (data[30] & 0xFF) * 100;
	}
	
	public void setStartingGold(int startingGold) {
		int value = startingGold / 100;
		data[30] = (byte)(value & 0xFF);
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
