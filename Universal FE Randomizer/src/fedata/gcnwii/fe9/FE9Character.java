package fedata.gcnwii.fe9;

import java.util.Arrays;

import fedata.general.FEModifiableData;
import util.WhyDoesJavaNotHaveThese;

public class FE9Character implements FEModifiableData {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private Long cachedCharIDPointer;
	private Long cachedCharNamePointer;
	
	private Long cachedPortraitPointer;
	private Long cachedClassPointer;
	
	private Long cachedAffiliationPointer;
	private Long cachedWeaponLevelsPointer;
	
	private Long cachedSkill1Pointer;
	private Long cachedSkill2Pointer;
	private Long cachedSkill3Pointer;
	
	private Long cachedUnpromotedAnimationPointer;
	private Long cachedPromotedAnimationPointer;
	
	public FE9Character(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public long getCharacterIDPointer() {
		if (cachedCharIDPointer == null) {
			cachedCharIDPointer = readPointerAtOffset(0x0);
		}
		return cachedCharIDPointer;
	}
	
	public long getCharacterNamePointer() {
		if (cachedCharNamePointer == null) {
			cachedCharNamePointer = readPointerAtOffset(0x4);
		}
		return cachedCharNamePointer;
	}
	
	// 8 bytes of 0 follow...
	
	public long getPortraitPointer() {
		if (cachedPortraitPointer == null) {
			cachedPortraitPointer = readPointerAtOffset(0xC);
		}
		return cachedPortraitPointer;
	}
	
	public long getClassPointer() {
		if (cachedClassPointer == null) {
			cachedClassPointer = readPointerAtOffset(0x10);
		}
		return cachedClassPointer;
	}
	
	public long getAffiliationPointer() {
		if (cachedAffiliationPointer == null) {
			cachedAffiliationPointer = readPointerAtOffset(0x14);
		}
		return cachedAffiliationPointer;
	}
	
	public long getWeaponLevelsPointer() {
		if (cachedWeaponLevelsPointer == null) {
			cachedWeaponLevelsPointer = readPointerAtOffset(0x18);
		}
		return cachedWeaponLevelsPointer;
	}
	
	public long getSkill1Pointer() {
		if (cachedSkill1Pointer == null) {
			cachedSkill1Pointer = readPointerAtOffset(0x1C);
		}
		return cachedSkill1Pointer;
	}
	
	public long getSkill2Pointer() {
		if (cachedSkill2Pointer == null) {
			cachedSkill2Pointer = readPointerAtOffset(0x20);
		}
		return cachedSkill2Pointer;
	}
	
	public long getSkill3Pointer() {
		if (cachedSkill3Pointer == null) {
			cachedSkill3Pointer = readPointerAtOffset(0x24);
		}
		return cachedSkill3Pointer;
	}
	
	public long getUnpromotedAnimationPointer() {
		if (cachedUnpromotedAnimationPointer == null) {
			cachedUnpromotedAnimationPointer = readPointerAtOffset(0x28);
		}
		return cachedUnpromotedAnimationPointer;
	}
	
	public long getPromotedAnimationPointer() {
		if (cachedPromotedAnimationPointer == null) {
			cachedPromotedAnimationPointer = readPointerAtOffset(0x2C);
		}
		return cachedPromotedAnimationPointer;
	}
	
	public int getLevel() {
		return data[0x36];
	}
	
	public int getBuild() {
		return data[0x37];
	}
	
	public int getWeight() {
		return data[0x38];
	}
	
	public int getBaseHP() {
		return data[0x39];
	}
	
	public int getBaseSTR() {
		return data[0x3A];
	}
	
	public int getBaseMAG() {
		return data[0x3B];
	}
	
	public int getBaseSKL() {
		return data[0x3C];
	}
	
	public int getBaseSPD() {
		return data[0x3D];
	}
	
	public int getBaseLCK() {
		return data[0x3E];
	}
	
	public int getBaseDEF() {
		return data[0x3F];
	}
	
	public int getBaseRES() {
		return data[0x40];
	}
	
	public int getHPGrowth() {
		return data[0x41];
	}
	
	public int getSTRGrowth() {
		return data[0x42];
	}
	
	public int getMAGGrowth() {
		return data[0x43];
	}
	
	public int getSKLGrowth() {
		return data[0x44];
	}
	
	public int getSPDGrowth() {
		return data[0x45];
	}
	
	public int getLCKGrowth() {
		return data[0x46];
	}
	
	public int getDEFGrowth() {
		return data[0x47];
	}
	
	public int getRESGrowth() {
		return data[0x48];
	}
	
	public byte[] getUnknown6Bytes() {
		return Arrays.copyOfRange(data, 0x30, 0x36); // For some reason, 0x35 as the end drops a byte. :/
	}
	
	public byte[] getUnknown8Bytes() {
		return Arrays.copyOfRange(data, 0x49, 0x51);
	}
	
	private long readPointerAtOffset(int offset) {
		byte[] ptr = Arrays.copyOfRange(data, offset, offset + 4);
		return WhyDoesJavaNotHaveThese.longValueFromByteArray(ptr, false) + 0x20;
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
