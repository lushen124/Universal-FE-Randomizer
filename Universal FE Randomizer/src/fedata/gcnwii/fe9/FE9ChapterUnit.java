package fedata.gcnwii.fe9;

import java.util.Arrays;

import fedata.general.FEModifiableData;
import util.WhyDoesJavaNotHaveThese;

public class FE9ChapterUnit implements FEModifiableData {
	
	public static final int CharacterIDOffset = 0x0;
	public static final int ClassIDOffset = 0x4;
	public static final int Weapon1Offset = 0xC;
	public static final int Weapon2Offset = 0x10;
	public static final int Weapon3Offset = 0x14;
	public static final int Weapon4Offset = 0x18;
	
	public static final int Item1Offset = 0x1C;
	public static final int Item2Offset = 0x20;
	public static final int Item3Offset = 0x24;
	public static final int Item4Offset = 0x28;
	
	public static final int Skill1Offset = 0x2C;
	public static final int Skill2Offset = 0x30;
	public static final int Skill3Offset = 0x34;
	
	public static final int HPAdjustmentOffset = 0x43;
	public static final int STRAdjustmentOffset = 0x44;
	public static final int MAGAdjustmentOffset = 0x45;
	public static final int SKLAdjustmentOffset = 0x46;
	public static final int SPDAdjustmentOffset = 0x47;
	public static final int LCKAdjustmentOffset = 0x48;
	public static final int DEFAdjustmentOffset = 0x49;
	public static final int RESAdjustmentOffset = 0x4A;
	
	private static final int SEQ1Offset = 0x4C;
	private static final int SEQ2Offset = 0x50;
	private static final int SEQ3Offset = 0x54;
	private static final int MTYPEOffset = 0x58;
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private Long cachedCharacterIDPointer;
	private Long cachedClassIDPointer;
	
	private Long cachedWeapon1Pointer;
	private Long cachedWeapon2Pointer;
	private Long cachedWeapon3Pointer;
	private Long cachedWeapon4Pointer;
	
	private Long cachedItem1Pointer;
	private Long cachedItem2Pointer;
	private Long cachedItem3Pointer;
	private Long cachedItem4Pointer;
	
	private Long cachedSkill1Pointer;
	private Long cachedSkill2Pointer;
	private Long cachedSkill3Pointer;
	private Long cachedSkill4Pointer;
	
	private Long cachedSEQ1Pointer;
	private Long cachedSEQ2Pointer;
	private Long cachedSEQ3Pointer;
	private Long cachedMTYPEPointer;
	
	public FE9ChapterUnit(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public long getCharacterIDPointer() {
		if (cachedCharacterIDPointer == null) { cachedCharacterIDPointer = readPointerAtOffset(CharacterIDOffset); }
		return cachedCharacterIDPointer;
	}
	
	public void setCharacterIDPointer(long pidPtr) {
		cachedCharacterIDPointer = pidPtr;
		writePointerToOffset(pidPtr, CharacterIDOffset);
		wasModified = true;
	}
	
	public long getClassIDPointer() {
		if (cachedClassIDPointer == null) { cachedClassIDPointer = readPointerAtOffset(ClassIDOffset); }
		return cachedClassIDPointer;
	}
	
	public void setClassIDPointer(long jidPtr) {
		cachedClassIDPointer = jidPtr;
		writePointerToOffset(jidPtr, 0x4);
		wasModified = true;
	}
	
	public long getWeapon1Pointer() {
		if (cachedWeapon1Pointer == null) { cachedWeapon1Pointer = readPointerAtOffset(Weapon1Offset); }
		return cachedWeapon1Pointer;
	}
	
	public void setWeapon1Pointer(long iidPtr) {
		cachedWeapon1Pointer = iidPtr;
		writePointerToOffset(iidPtr, Weapon1Offset);
		wasModified = true;
	}
	
	public long getWeapon2Pointer() {
		if (cachedWeapon2Pointer == null) { cachedWeapon2Pointer = readPointerAtOffset(Weapon2Offset); }
		return cachedWeapon2Pointer;
	}
	
	public void setWeapon2Pointer(long iidPtr) {
		cachedWeapon2Pointer = iidPtr;
		writePointerToOffset(iidPtr, Weapon2Offset);
		wasModified = true;
	}
	
	public long getWeapon3Pointer() {
		if (cachedWeapon3Pointer == null) { cachedWeapon3Pointer = readPointerAtOffset(Weapon3Offset); }
		return cachedWeapon3Pointer;
	}
	
	public void setWeapon3Pointer(long iidPtr) {
		cachedWeapon3Pointer = iidPtr;
		writePointerToOffset(iidPtr, Weapon3Offset);
		wasModified = true;
	}
	
	public long getWeapon4Pointer() {
		if (cachedWeapon4Pointer == null) { cachedWeapon4Pointer = readPointerAtOffset(Weapon4Offset); }
		return cachedWeapon4Pointer;
	}
	
	public void setWeapon4Pointer(long iidPtr) {
		cachedWeapon4Pointer = iidPtr;
		writePointerToOffset(iidPtr, Weapon4Offset);
		wasModified = true;
	}
	
	public long getItem1Pointer() {
		if (cachedItem1Pointer == null) { cachedItem1Pointer = readPointerAtOffset(Item1Offset); }
		return cachedItem1Pointer;
	}
	
	public void setItem1Pointer(long iidPtr) {
		cachedItem1Pointer = iidPtr;
		writePointerToOffset(iidPtr, Item1Offset);
		wasModified = true;
	}
	
	public long getItem2Pointer() {
		if (cachedItem2Pointer == null) { cachedItem2Pointer = readPointerAtOffset(Item2Offset); }
		return cachedItem2Pointer;
	}
	
	public void setItem2Pointer(long iidPtr) {
		cachedItem2Pointer = iidPtr;
		writePointerToOffset(iidPtr, Item2Offset);
		wasModified = true;
	}
	
	public long getItem3Pointer() {
		if (cachedItem3Pointer == null) { cachedItem3Pointer = readPointerAtOffset(Item3Offset); }
		return cachedItem3Pointer;
	}
	
	public void setItem3Pointer(long iidPtr) {
		cachedItem3Pointer = iidPtr;
		writePointerToOffset(iidPtr, Item3Offset);
		wasModified = true;
	}
	
	public long getItem4Pointer() {
		if (cachedItem4Pointer == null) { cachedItem4Pointer = readPointerAtOffset(Item4Offset); }
		return cachedItem4Pointer;
	}
	
	public void setItem4Pointer(long iidPtr) {
		cachedItem4Pointer = iidPtr;
		writePointerToOffset(iidPtr, Item4Offset);
		wasModified = true;
	}
	
	public long getSkill1Pointer() {
		if (cachedSkill1Pointer == null) { cachedSkill1Pointer = readPointerAtOffset(Skill1Offset); }
		return cachedSkill1Pointer;
	}
	
	public void setSkill1Pointer(long sidPtr) {
		cachedSkill1Pointer = sidPtr;
		writePointerToOffset(sidPtr, Skill1Offset);
		wasModified = true;
	}
	
	public long getSkill2Pointer() {
		if (cachedSkill2Pointer == null) { cachedSkill2Pointer = readPointerAtOffset(Skill2Offset); }
		return cachedSkill2Pointer;
	}
	
	public void setSkill2Pointer(long sidPtr) {
		cachedSkill2Pointer = sidPtr;
		writePointerToOffset(sidPtr, Skill2Offset);
		wasModified = true;
	}
	
	public long getSkill3Pointer() {
		if (cachedSkill3Pointer == null) { cachedSkill3Pointer = readPointerAtOffset(Skill3Offset); }
		return cachedSkill3Pointer;
	}
	
	public void setSkill3Pointer(long sidPtr) {
		cachedSkill3Pointer = sidPtr;
		writePointerToOffset(sidPtr, Skill3Offset);
		wasModified = true;
	}
	
	public byte[] getPostSkillData() {
		// 0x38 ~ 0x42
		return Arrays.copyOfRange(data, 0x38, 0x43);
	}
	
	public int getHPAdjustment() {
		return data[HPAdjustmentOffset];
	}
	
	public void setHPAdjustment(int adjustment) {
		data[HPAdjustmentOffset] = (byte)(adjustment & 0xFF);
		wasModified = true;
	}
	
	public int getSTRAdjustment() {
		return data[STRAdjustmentOffset];
	}
	
	public void setSTRAdjustment(int adjustment) {
		data[STRAdjustmentOffset] = (byte)(adjustment & 0xFF);
		wasModified = true;
	}
	
	public int getMAGAdjustment() {
		return data[MAGAdjustmentOffset];
	}
	
	public void setMAGAdjustment(int adjustment) {
		data[MAGAdjustmentOffset] = (byte)(adjustment & 0xFF);
		wasModified = true;
	}
	
	public int getSKLAdjustment() {
		return data[SKLAdjustmentOffset];
	}
	
	public void setSKLAdjustment(int adjustment) {
		data[SKLAdjustmentOffset] = (byte)(adjustment & 0xFF);
		wasModified = true;
	}
	
	public int getSPDAdjustment() {
		return data[SPDAdjustmentOffset];
	}
	
	public void setSPDAdjustment(int adjustment) {
		data[SPDAdjustmentOffset] = (byte)(adjustment & 0xFF);
		wasModified = true;
	}
	
	public int getLCKAdjustment() {
		return data[LCKAdjustmentOffset];
	}
	
	public void setLCKAdjustment(int adjustment) {
		data[LCKAdjustmentOffset] = (byte)(adjustment & 0xFF);
		wasModified = true;
	}
	
	public int getDEFAdjustment() {
		return data[DEFAdjustmentOffset];
	}
	
	public void setDEFAdjustment(int adjustment) {
		data[DEFAdjustmentOffset] = (byte)(adjustment & 0xFF);
		wasModified = true;
	}
	
	public int getRESAdjustment() {
		return data[RESAdjustmentOffset];
	}
	
	public void setRESAdjustment(int adjustment) {
		data[RESAdjustmentOffset] = (byte)(adjustment & 0xFF);
		wasModified = true;
	}
	
	public byte[] getPostAdjustmentData() {
		// 0x4B
		return Arrays.copyOfRange(data, 0x4B, 0x4C);
	}
	
	public long getSEQ1Pointer() {
		if (cachedSEQ1Pointer == null) { cachedSEQ1Pointer = readPointerAtOffset(SEQ1Offset); }
		return cachedSEQ1Pointer;
	}
	
	public void setSEQ1Pointer(long seqPtr) {
		cachedSEQ1Pointer = seqPtr;
		writePointerToOffset(seqPtr, SEQ1Offset);
		wasModified = true;
	}
	
	public long getSEQ2Pointer() {
		if (cachedSEQ2Pointer == null) { cachedSEQ2Pointer = readPointerAtOffset(SEQ2Offset); }
		return cachedSEQ2Pointer;
	}
	
	public void setSEQ2Pointer(long seqPtr) {
		cachedSEQ2Pointer = seqPtr;
		writePointerToOffset(seqPtr, SEQ2Offset);
		wasModified = true;
	}
	
	public long getSEQ3Pointer() {
		if (cachedSEQ3Pointer == null) { cachedSEQ3Pointer = readPointerAtOffset(SEQ3Offset); }
		return cachedSEQ3Pointer;
	}
	
	public void setSEQ3Pointer(long seqPtr) {
		cachedSEQ3Pointer = seqPtr;
		writePointerToOffset(seqPtr, SEQ3Offset);
		wasModified = true;
	}
	
	public long getMTYPEPointer() {
		if (cachedMTYPEPointer == null) { cachedMTYPEPointer = readPointerAtOffset(MTYPEOffset); }
		return cachedMTYPEPointer;
	}
	
	public void setMTYPEPointer(long mtypePtr) {
		cachedMTYPEPointer = mtypePtr;
		writePointerToOffset(mtypePtr, MTYPEOffset);
		wasModified = true;
	}
	
	public int getStartingX() {
		return data[0x5C];
	}
	
	public int getStartingY() {
		return data[0x5D];
	}
	
	public void setStartingX(int newX) {
		data[0x5C] = (byte)(newX & 0xFF);
		wasModified = true;
	}
	
	public void setStartingY(int newY) {
		data[0x5D] = (byte)(newY & 0xFF);
		wasModified = true;
	}
	
	public int getEndingX() {
		return data[0x5E];
	}
	
	public int getEndingY() {
		return data[0x5F];
	}
	
	public void setEndingX(int newX) {
		data[0x5E] = (byte)(newX & 0xFF);
		wasModified = true;
	}
	
	public void setEndingY(int newY) {
		data[0x5F] = (byte)(newY & 0xFF);
		wasModified = true;
	}
	
	public int getStartingLevel() {
		return data[0x60];
	}
	
	public byte[] getPreDropData() {
		// 0x61 ~ 0x63
		return Arrays.copyOfRange(data, 0x61, 0x64);
	}
	
	public boolean willDropWeapon1() {
		return data[0x64] != 0x0;
	}
	
	public void setWillDropWeapon1(boolean drop) {
		if (drop != willDropWeapon1()) { 
			data[0x64] = (byte)(drop ? 0x1 : 0x0);
			wasModified = true;
		}
	}
	
	public boolean willDropWeapon2() {
		return data[0x65] != 0x0;
	}
	
	public void setWillDropWeapon2(boolean drop) {
		if (drop != willDropWeapon2()) {
			data[0x65] = (byte)(drop ? 0x1 : 0x0);
			wasModified = true;
		}
	}
	
	public boolean willDropWeapon3() {
		return data[0x66] != 0x0;
	}
	
	public void setWillDropWeapon3(boolean drop) {
		if (drop != willDropWeapon3()) {
			data[0x66] = (byte)(drop ? 0x1 : 0x0);
			wasModified = true;
		}
	}
	
	public boolean willDropWeapon4() {
		return data[0x67] != 0x0;
	}
	
	public void setWillDropWeapon4(boolean drop) {
		if (drop != willDropWeapon4()) {
			data[0x67] = (byte)(drop ? 0x1 : 0x0);
			wasModified = true;
		}
	}
	
	public boolean willDropItem1() {
		return data[0x68] != 0x0;
	}
	
	public void setWillDropItem1(boolean drop) {
		if (drop != willDropItem1()) {
			data[0x68] = (byte)(drop ? 0x1 : 0x0);
			wasModified = true;
		}
	}
	
	public boolean willDropItem2() {
		return data[0x69] != 0x0;
	}
	
	public void setWillDropItem2(boolean drop) {
		if (drop != willDropItem2()) {
			data[0x69] = (byte)(drop ? 0x1 : 0x0);
			wasModified = true;
		}
	}
	
	public boolean willDropItem3() {
		return data[0x6A] != 0x0;
	}
	
	public void setWillDropItem3(boolean drop) {
		if (drop != willDropItem3()) {
			data[0x6A] = (byte)(drop ? 0x1 : 0x0);
			wasModified = true;
		}
	}
	
	public boolean willDropItem4() {
		return data[0x6B] != 0x0;
	}
	
	public void setWillDropItem4(boolean drop) {
		if (drop != willDropItem4()) {
			data[0x6B] = (byte)(drop ? 0x1 : 0x0);
			wasModified = true;
		}
	}
	
	public void setStartingLevel(int level) {
		data[0x60] = (byte)(level & 0xFF);
		wasModified = true;
	}
	
	private long readPointerAtOffset(int offset) {
		byte[] ptr = Arrays.copyOfRange(data, offset, offset + 4);
		if (WhyDoesJavaNotHaveThese.byteArraysAreEqual(ptr, new byte[] {0, 0, 0, 0})) { return 0; }
		
		return WhyDoesJavaNotHaveThese.longValueFromByteArray(ptr, false);
	}
	
	private void writePointerToOffset(long pointer, int offset) {
		byte[] ptr = pointer == 0 ? new byte[] {0, 0, 0, 0} : WhyDoesJavaNotHaveThese.bytesFromPointer(pointer);
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
