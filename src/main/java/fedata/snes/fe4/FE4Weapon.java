package fedata.snes.fe4;

import java.util.Arrays;

import fedata.general.FEModifiableData;

public class FE4Weapon implements FEModifiableData {

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public enum ItemType {
		WEAPON, STAFF, RING, UNKNOWN;
	}
	
	public enum WeaponRank {
		C, B, A, S, UNKNOWN;
	}
	
	public enum WeaponType {
		SWORD, LANCE, AXE, BOW, STAFF, FIRE, THUNDER, WIND, LIGHT, DARK, UNKNOWN;
	}
	
	public FE4Weapon(byte[] data, long originalOffset) {
		super();
		this.originalData = Arrays.copyOf(data, data.length);
		this.data = Arrays.copyOf(data, data.length);
		this.originalOffset = originalOffset;
	}
	
	public int getID() {
		return data[0] & 0xFF;
	}
	
	public ItemType getItemType() {
		int typeValue = data[1] & 0xFF;
		if (typeValue == 0) { return ItemType.WEAPON; } 
		else if (typeValue == 1) { return ItemType.STAFF; } 
		else if (typeValue == 2) { return ItemType.RING; } 
		else { return ItemType.UNKNOWN; }
	}
	
	public int getDurability() {
		return data[2] & 0xFF;
	}
	
	public void setDurability(int durability) {
		data[2] = (byte)(durability & 0xFF);
		wasModified = true;
	}
	
	public int getPrice() {
		return (data[3] & 0xFF) | ((data[4] & 0xFF) << 8);
	}
	
	public void setPrice(int newPrice) {
		int priceCap = Math.min(newPrice, 65535); // 65535 becomes unpurchasable since the maximum gold cap per character is 50000.
		data[3] = (byte)(priceCap & 0xFF);
		data[4] = (byte)((priceCap >> 8) & 0xFF);
		wasModified = true;
	}
	
	public WeaponRank getRank() {
		int rankValue = data[7] & 0xFF;
		if (rankValue == 0) { return WeaponRank.A; } 
		else if (rankValue == 1) { return WeaponRank.B; } 
		else if (rankValue == 2) { return WeaponRank.C; } 
		else if (rankValue == 255) { return WeaponRank.S; } 
		else { return WeaponRank.UNKNOWN; }
	}
	
	public WeaponType getWeaponType() {
		int weaponValue = data[10] & 0xFF;
		if (weaponValue == 0) { return WeaponType.SWORD; }
		else if (weaponValue == 1) { return WeaponType.LANCE; }
		else if (weaponValue == 2) { return WeaponType.AXE; }
		else if (weaponValue == 3) { return WeaponType.BOW; }
		else if (weaponValue == 4) { return WeaponType.STAFF; }
		else if (weaponValue == 5) { return WeaponType.FIRE; }
		else if (weaponValue == 6) { return WeaponType.THUNDER; }
		else if (weaponValue == 7) { return WeaponType.WIND; }
		else if (weaponValue == 8) { return WeaponType.LIGHT; }
		else if (weaponValue == 9) { return WeaponType.DARK; }
		else { return WeaponType.UNKNOWN; }
	}
	
	public int getPower() {
		return data[11] & 0xFF;
	}
	
	public void setPower(int newPower) {
		data[11] = (byte)(newPower & 0xFF);
		wasModified = true;
	}
	
	public int getAccuracy() {
		return data[12] & 0xFF;
	}
	
	public void setAccuracy(int newAccuracy) {
		data[12] = (byte)(newAccuracy & 0xFF); 
		wasModified = true;
	}
	
	public int getWeight() {
		return data[13] & 0xFF;
	}
	
	public void setWeight(int newWeight) {
		data[13] = (byte)(newWeight & 0xFF);
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
