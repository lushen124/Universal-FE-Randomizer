package fedata.snes.fe4;

import fedata.general.FEModifiableData;

public class FE4HolyBlood implements FEModifiableData {
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE4HolyBlood(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public int getHPGrowthBonus() {
		return data[2] & 0xFF;
	}
	
	public void setHPGrowthBonus(int bonus) {
		data[2] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getSTRGrowthBonus() {
		return data[3] & 0xFF;
	}
	
	public void setSTRGrowthBonus(int bonus) {
		data[3] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getMAGGrowthBonus() {
		return data[4] & 0xFF;
	}
	
	public void setMAGGrowthBonus(int bonus) {
		data[4] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getSKLGrowthBonus() {
		return data[5] & 0xFF;
	}
	
	public void setSKLGrowthBonus(int bonus) {
		data[5] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getSPDGrowthBonus() {
		return data[6] & 0xFF;
	}
	
	public void setSPDGrowthBonus(int bonus) {
		data[6] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getDEFGrowthBonus() {
		return data[7] & 0xFF;
	}
	
	public void setDEFGrowthBonus(int bonus) {
		data[7] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getRESGrowthBonus() {
		return data[8] & 0xFF;
	}
	
	public void setRESGrowthBonus(int bonus) {
		data[8] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getLCKGrowthBonus() {
		return data[9] & 0xFF;
	}
	
	public void setLCKGrowthBonus(int bonus) {
		data[9] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getHolyWeaponSTRBonus() {
		return data[10] & 0xFF;
	}
	
	public void setHolyWeaponSTRBonus(int bonus) {
		data[10] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getHolyWeaponMAGBonus() {
		return data[11] & 0xFF;
	}
	
	public void setHolyWeaponMAGBonus(int bonus) {
		data[11] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getHolyWeaponSKLBonus() {
		return data[12] & 0xFF;
	}
	
	public void setHolyWeaponSKLBonus(int bonus) {
		data[12] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getHolyWeaponSPDBonus() {
		return data[13] & 0xFF;
	}
	
	public void setHolyWeaponSPDBonus(int bonus) {
		data[13] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getHolyWeaponDEFBonus() {
		return data[14] & 0xFF;
	}
	
	public void setHolyWeaponDEFBonus(int bonus) {
		data[14] = (byte)(Math.max(0, bonus) & 0xFF);
		wasModified = true;
	}
	
	public int getHolyWeaponRESBonus() {
		return data[15] & 0xFF;
	}
	
	public void setHolyWeaponRESBonus(int bonus) {
		data[15] = (byte)(Math.max(0, bonus) & 0xFF);
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
