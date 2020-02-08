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
	
	public long getClassIDPointer() {
		if (cachedClassIDPointer == null) { cachedClassIDPointer = readPointerAtOffset(ClassIDOffset); }
		return cachedClassIDPointer;
	}
	
	public void setClassIDPointer(long jidPtr) {
		cachedClassIDPointer = jidPtr;
		writePointerToOffset(jidPtr, 0x4);
		hasChanges = true;
	}
	
	public long getWeapon1Pointer() {
		if (cachedWeapon1Pointer == null) { cachedWeapon1Pointer = readPointerAtOffset(Weapon1Offset); }
		return cachedWeapon1Pointer;
	}
	
	public void setWeapon1Pointer(long iidPtr) {
		cachedWeapon1Pointer = iidPtr;
		writePointerToOffset(iidPtr, Weapon1Offset);
		hasChanges = true;
	}
	
	public long getWeapon2Pointer() {
		if (cachedWeapon2Pointer == null) { cachedWeapon2Pointer = readPointerAtOffset(Weapon2Offset); }
		return cachedWeapon2Pointer;
	}
	
	public void setWeapon2Pointer(long iidPtr) {
		cachedWeapon2Pointer = iidPtr;
		writePointerToOffset(iidPtr, Weapon2Offset);
		hasChanges = true;
	}
	
	public long getWeapon3Pointer() {
		if (cachedWeapon3Pointer == null) { cachedWeapon3Pointer = readPointerAtOffset(Weapon3Offset); }
		return cachedWeapon3Pointer;
	}
	
	public void setWeapon3Pointer(long iidPtr) {
		cachedWeapon3Pointer = iidPtr;
		writePointerToOffset(iidPtr, Weapon3Offset);
		hasChanges = true;
	}
	
	public long getWeapon4Pointer() {
		if (cachedWeapon4Pointer == null) { cachedWeapon4Pointer = readPointerAtOffset(Weapon4Offset); }
		return cachedWeapon4Pointer;
	}
	
	public void setWeapon4Pointer(long iidPtr) {
		cachedWeapon4Pointer = iidPtr;
		writePointerToOffset(iidPtr, Weapon4Offset);
		hasChanges = true;
	}
	
	public long getItem1Pointer() {
		if (cachedItem1Pointer == null) { cachedItem1Pointer = readPointerAtOffset(0x1C); }
		return cachedItem1Pointer;
	}
	
	public long getItem2Pointer() {
		if (cachedItem2Pointer == null) { cachedItem2Pointer = readPointerAtOffset(0x20); }
		return cachedItem2Pointer;
	}
	
	public long getItem3Pointer() {
		if (cachedItem3Pointer == null) { cachedItem3Pointer = readPointerAtOffset(0x24); }
		return cachedItem3Pointer;
	}
	
	public long getItem4Pointer() {
		if (cachedItem4Pointer == null) { cachedItem4Pointer = readPointerAtOffset(0x28); }
		return cachedItem4Pointer;
	}
	
	public int getStartingX() {
		return data[0x5C];
	}
	
	public int getStartingY() {
		return data[0x5D];
	}
	
	public int getEndingX() {
		return data[0x5E];
	}
	
	public int getEndingY() {
		return data[0x5F];
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
