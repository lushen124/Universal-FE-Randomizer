package fedata.fe7;

import fedata.FEItem;
import fedata.fe7.FE7Data.Item.FE7WeaponRank;
import fedata.fe7.FE7Data.Item.FE7WeaponType;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;

public class FE7Item implements FEItem {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;

	public FE7Item(byte[] data, long originalOffset) {
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

	public int getUseDescriptionIndex() {
		return (data[4] & 0xFF) | ((data[5] & 0xFF) << 8);
	}

	public int getID() {
		return data[6] & 0xFF;
	}

	public WeaponType getType() {
		FE7WeaponType type = FE7WeaponType.valueOf(data[7] & 0xFF);
		return type.toGeneralType();
	}

	public int getAbility1() {
		return data[8] & 0xFF;
	}

	public int getAbility2() {
		return data[9] & 0xFF;
	}

	public int getAbility3() {
		return data[10] & 0xFF;
	}

	public int getAbility4() {
		return data[11] & 0xFF;
	}

	public long getStatBonusPointer() {
		return (data[12] & 0xFF) | ((data[13] & 0xFF) << 8) | ((data[14] & 0xFF) << 16) | ((data[15] & 0xFF) << 24) ;
	}

	public long getEffectivenessPointer() {
		return (data[16] & 0xFF) | ((data[17] & 0xFF) << 8) | ((data[18] & 0xFF) << 16) | ((data[19] & 0xFF) << 24) ;
	}

	public int getDurability() {
		return data[20] & 0xFF;
	}

	public int getMight() {
		return data[21] & 0xFF;
	}

	public int getHit() {
		return data[22] & 0xFF;
	}

	public int getWeight() {
		return data[23] & 0xFF;
	}

	public int getCritical() {
		return data[24] & 0xFF;
	}

	public int getMinRange() {
		return data[25] & 0xF0 >> 4;
	}

	public int getMaxRange() {
		return data[25] & 0x0F;
	}

	public WeaponRank getWeaponRank() {
		int rank = data[28] & 0xFF;
		FE7WeaponRank weaponRank = FE7Data.Item.FE7WeaponRank.valueOf(rank);
		return weaponRank.toGeneralRank();
	}

	public int getWeaponEffect() {
		return data[31];
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
