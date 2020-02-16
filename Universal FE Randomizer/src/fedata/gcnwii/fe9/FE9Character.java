package fedata.gcnwii.fe9;

import java.util.Arrays;

import fedata.general.FEModifiableData;
import util.WhyDoesJavaNotHaveThese;

public class FE9Character implements FEModifiableData {
	
	public static int CharacterSkill1Offset = 0x1C;
	public static int CharacterSkill2Offset = 0x20;
	public static int CharacterSkill3Offset = 0x24;
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private Long cachedCharIDPointer;
	private Long cachedCharNamePointer;
	
	private Long cachedPortraitPointer;
	private Long cachedClassPointer;
	
	private Long cachedAffinityPointer;
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
	
	public void setCharacterIDPointer(long newCharacterID) {
		cachedCharIDPointer = newCharacterID;
		writePointerToOffset(newCharacterID, 0x0);
		wasModified = true;
	}
	
	public long getCharacterNamePointer() {
		if (cachedCharNamePointer == null) {
			cachedCharNamePointer = readPointerAtOffset(0x4);
		}
		return cachedCharNamePointer;
	}
	
	// 4 bytes of 0 follow...
	
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
	
	public void setClassPointer(long newClassID) {
		cachedClassPointer = newClassID;
		writePointerToOffset(newClassID, 0x10);
		wasModified = true;
	}
	
	public long getAffinityPointer() {
		if (cachedAffinityPointer == null) {
			cachedAffinityPointer = readPointerAtOffset(0x14);
		}
		return cachedAffinityPointer;
	}
	
	public void setAffinityPointer(long newPointer) {
		cachedAffinityPointer = newPointer;
		writePointerToOffset(newPointer, 0x14);
		wasModified = true;
	}
	
	public long getWeaponLevelsPointer() {
		if (cachedWeaponLevelsPointer == null) {
			cachedWeaponLevelsPointer = readPointerAtOffset(0x18);
		}
		return cachedWeaponLevelsPointer;
	}
	
	public void setWeaponLevelsPointer(long pointer) {
		cachedWeaponLevelsPointer = pointer;
		writePointerToOffset(pointer, 0x18);
		wasModified = true;
	}
	
	public long getSkill1Pointer() {
		if (cachedSkill1Pointer == null) {
			cachedSkill1Pointer = readPointerAtOffset(CharacterSkill1Offset);
		}
		return cachedSkill1Pointer;
	}
	
	public void setSkill1Pointer(long pointer) {
		cachedSkill1Pointer = pointer;
		writePointerToOffset(pointer, CharacterSkill1Offset);
		wasModified = true;
	}
	
	public long getSkill2Pointer() {
		if (cachedSkill2Pointer == null) {
			cachedSkill2Pointer = readPointerAtOffset(CharacterSkill2Offset);
		}
		return cachedSkill2Pointer;
	}
	
	public void setSkill2Pointer(long pointer) {
		cachedSkill2Pointer = pointer;
		writePointerToOffset(pointer, CharacterSkill2Offset);
		wasModified = true;
	}
	
	public long getSkill3Pointer() {
		if (cachedSkill3Pointer == null) {
			cachedSkill3Pointer = readPointerAtOffset(CharacterSkill3Offset);
		}
		return cachedSkill3Pointer;
	}
	
	public void setSkill3Pointer(long pointer) {
		cachedSkill3Pointer = pointer;
		writePointerToOffset(pointer, CharacterSkill3Offset);
		wasModified = true;
	}
	
	public long getUnpromotedAnimationPointer() {
		if (cachedUnpromotedAnimationPointer == null) {
			cachedUnpromotedAnimationPointer = readPointerAtOffset(0x28);
		}
		return cachedUnpromotedAnimationPointer;
	}
	
	public void setUnpromotedAnimationPointer(long animationPointer) {
		cachedUnpromotedAnimationPointer = animationPointer;
		writePointerToOffset(animationPointer, 0x28);
		wasModified = true;
	}
	
	public long getPromotedAnimationPointer() {
		if (cachedPromotedAnimationPointer == null) {
			cachedPromotedAnimationPointer = readPointerAtOffset(0x2C);
		}
		return cachedPromotedAnimationPointer;
	}
	
	public void setPromotedAnimationPointer(long animationPointer) {
		cachedPromotedAnimationPointer = animationPointer;
		writePointerToOffset(animationPointer, 0x2C);
		wasModified = true;
	}
	
	public int getLevel() {
		return data[0x36];
	}
	
	public int getBuild() {
		return data[0x37];
	}
	
	public void setBuild(int newBuild) {
		data[0x37] = (byte)(newBuild & 0xFF);
		wasModified = true;
	}
	
	public int getWeight() {
		return data[0x38];
	}
	
	public void setWeight(int newWeight) {
		data[0x38] = (byte)(newWeight & 0xFF);
		wasModified = true;
	}
	
	public int getBaseHP() {
		return data[0x39];
	}
	
	public void setBaseHP(int newBaseHP) {
		data[0x39] = (byte)(newBaseHP & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSTR() {
		return data[0x3A];
	}
	
	public void setBaseSTR(int newBaseSTR) {
		data[0x3A] = (byte)(newBaseSTR & 0xFF);
		wasModified = true;
	}
	
	public int getBaseMAG() {
		return data[0x3B];
	}
	
	public void setBaseMAG(int newBaseMAG) {
		data[0x3B] = (byte)(newBaseMAG & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSKL() {
		return data[0x3C];
	}
	
	public void setBaseSKL(int newBaseSKL) {
		data[0x3C] = (byte)(newBaseSKL & 0xFF);
		wasModified = true;
	}
	
	public int getBaseSPD() {
		return data[0x3D];
	}
	
	public void setBaseSPD(int newBaseSPD) {
		data[0x3D] = (byte)(newBaseSPD & 0xFF);
		wasModified = true;
	}
	
	public int getBaseLCK() {
		return data[0x3E];
	}
	
	public void setBaseLCK(int newBaseLCK) {
		data[0x3E] = (byte)(newBaseLCK & 0xFF);
		wasModified = true;
	}
	
	public int getBaseDEF() {
		return data[0x3F];
	}
	
	public void setBaseDEF(int newBaseDEF) {
		data[0x3F] = (byte)(newBaseDEF & 0xFF);
		wasModified = true;
	}
	
	public int getBaseRES() {
		return data[0x40];
	}
	
	public void setBaseRES(int newBaseRES) {
		data[0x40] = (byte)(newBaseRES & 0xFF);
		wasModified = true;
	}
	
	public int getHPGrowth() {
		return data[0x41];
	}
	
	public void setHPGrowth(int newHPGrowth) {
		data[0x41] = (byte)(newHPGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSTRGrowth() {
		return data[0x42];
	}
	
	public void setSTRGrowth(int newSTRGrowth) {
		data[0x42] = (byte)(newSTRGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getMAGGrowth() {
		return data[0x43];
	}
	
	public void setMAGGrowth(int newMAGGrowth) {
		data[0x43] = (byte)(newMAGGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSKLGrowth() {
		return data[0x44];
	}
	
	public void setSKLGrowth(int newSKLGrowth) {
		data[0x44] = (byte)(newSKLGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getSPDGrowth() {
		return data[0x45];
	}
	
	public void setSPDGrowth(int newSPDGrowth) {
		data[0x45] = (byte)(newSPDGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getLCKGrowth() {
		return data[0x46];
	}
	
	public void setLCKGrowth(int newLCKGrowth) {
		data[0x46] = (byte)(newLCKGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getDEFGrowth() {
		return data[0x47];
	}
	
	public void setDEFGrowth(int newDEFGrowth) {
		data[0x47] = (byte)(newDEFGrowth & 0xFF);
		wasModified = true;
	}
	
	public int getRESGrowth() {
		return data[0x48];
	}
	
	public void setRESGrowth(int newRESGrowth) {
		data[0x48] = (byte)(newRESGrowth & 0xFF);
		wasModified = true;
	}
	
	public byte[] getUnknown6Bytes() {
		return Arrays.copyOfRange(data, 0x30, 0x36); // For some reason, 0x35 as the end drops a byte. :/
	}
	
	public void setUnknown6Bytes(byte[] sixBytes) {
		WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(sixBytes, data, 0x30, 6);
		wasModified = true;
	}
	
	public int getLaguzTransformationStartingValue() {
		return data[0x34];
	}
	
	public void setLaguzTransformationStartingValue(int startingValue) {
		startingValue = WhyDoesJavaNotHaveThese.clamp(startingValue, 0, 20);
		data[0x34] = (byte)(startingValue & 0xFF);
		wasModified = true;
	}
	
	public byte[] getUnknown8Bytes() {
		return Arrays.copyOfRange(data, 0x49, 0x51);
	}
	
	public void setUnknown8Bytes(byte[] eightBytes) {
		WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(eightBytes, data, 0x49, 8);
		wasModified = true;
	}
	
	private long readPointerAtOffset(int offset) {
		byte[] ptr = Arrays.copyOfRange(data, offset, offset + 4);
		if (WhyDoesJavaNotHaveThese.byteArraysAreEqual(ptr, new byte[] {0, 0, 0, 0})) { return 0; }
		
		return WhyDoesJavaNotHaveThese.longValueFromByteArray(ptr, false) + 0x20;
	}
	
	private void writePointerToOffset(long pointer, int offset) {
		byte[] ptr = pointer == 0 ? new byte[] {0, 0, 0, 0} : WhyDoesJavaNotHaveThese.bytesFromPointer(pointer - 0x20);
		WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(ptr, data, offset, 4);
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
	
	public void setData(byte[] newData) {
		data = Arrays.copyOf(newData, newData.length);
		wasModified = true;
	}
	
	public Boolean wasModified() {
		return wasModified;
	}
	
	public long getAddressOffset() {
		return originalOffset;
	}
}
