package fedata.gcnwii.fe9;

import java.util.Arrays;

import fedata.general.FEModifiableData;
import util.WhyDoesJavaNotHaveThese;

public class FE9Item implements FEModifiableData {
	
	public static int ItemTrait1Offset = 0x18;
	public static int ItemTrait2Offset = 0x1C;
	public static int ItemTrait3Offset = 0x20;
	public static int ItemTrait4Offset = 0x24;
	public static int ItemTrait5Offset = 0x28;
	public static int ItemTrait6Offset = 0x2C;

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private Long cachedItemIDPointer;
	private Long cachedItemNamePointer;
	private Long cachedItemDescriptionPointer;
	
	private Long cachedItemTypePointer;
	private Long cachedItemSubtypePointer;
	private Long cachedItemRankPointer;
	
	private Long cachedItemTrait1Pointer;
	private Long cachedItemTrait2Pointer;
	private Long cachedItemTrait3Pointer;
	private Long cachedItemTrait4Pointer;
	private Long cachedItemTrait5Pointer;
	private Long cachedItemTrait6Pointer;
	
	private Long cachedItemEffectiveness1Pointer;
	private Long cachedItemEffectiveness2Pointer;
	
	private Long cachedItemEffectAnimation1Pointer;
	private Long cachedItemEffectAnimation2Pointer;
	
	private Long cachedItemUnknownPointer8;
	private Long cachedItemUnknownPointer9;
	
	public FE9Item(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public long getItemIDPointer() {
		if (cachedItemIDPointer == null) { cachedItemIDPointer = readPointerAtOffset(0x0); }
		return cachedItemIDPointer;
	}
	
	public long getItemNamePointer() {
		if (cachedItemNamePointer == null) { cachedItemNamePointer = readPointerAtOffset(0x4); }
		return cachedItemNamePointer;
	}
	
	public long getItemDescriptionPointer() {
		if (cachedItemDescriptionPointer == null) { cachedItemDescriptionPointer = readPointerAtOffset(0x8); }
		return cachedItemDescriptionPointer;
	}
	
	public long getItemTypePointer() {
		if (cachedItemTypePointer == null) { cachedItemTypePointer = readPointerAtOffset(0xC); }
		return cachedItemTypePointer;
	}
	
	public long getItemSubtypePointer() {
		if (cachedItemSubtypePointer == null) { cachedItemSubtypePointer = readPointerAtOffset(0x10); }
		return cachedItemSubtypePointer;
	}
	
	public long getItemRankPointer() {
		if (cachedItemRankPointer == null) { cachedItemRankPointer = readPointerAtOffset(0x14); }
		return cachedItemRankPointer;
	}
	
	public long getItemTrait1Pointer() {
		if (cachedItemTrait1Pointer == null) { cachedItemTrait1Pointer = readPointerAtOffset(ItemTrait1Offset); }
		return cachedItemTrait1Pointer;
	}
	
	public void setItemTrait1Pointer(long ptr) {
		cachedItemTrait1Pointer = ptr;
		writePointerToOffset(ptr, ItemTrait1Offset);
		wasModified = true;
	}
	
	public long getItemTrait2Pointer() {
		if (cachedItemTrait2Pointer == null) { cachedItemTrait2Pointer = readPointerAtOffset(ItemTrait2Offset); }
		return cachedItemTrait2Pointer;
	}
	
	public void setItemTrait2Pointer(long ptr) {
		cachedItemTrait2Pointer = ptr;
		writePointerToOffset(ptr, ItemTrait2Offset);
		wasModified = true;
	}
	
	public long getItemTrait3Pointer() {
		if (cachedItemTrait3Pointer == null) { cachedItemTrait3Pointer = readPointerAtOffset(ItemTrait3Offset); }
		return cachedItemTrait3Pointer;
	}
	
	public void setItemTrait3Pointer(long ptr) {
		cachedItemTrait3Pointer = ptr;
		writePointerToOffset(ptr, ItemTrait3Offset);
		wasModified = true;
	}
	
	public long getItemTrait4Pointer() {
		if (cachedItemTrait4Pointer == null) { cachedItemTrait4Pointer = readPointerAtOffset(ItemTrait4Offset); }
		return cachedItemTrait4Pointer;
	}
	
	public void setItemTrait4Pointer(long ptr) {
		cachedItemTrait4Pointer = ptr;
		writePointerToOffset(ptr, ItemTrait4Offset);
		wasModified = true;
	}
	
	public long getItemTrait5Pointer() {
		if (cachedItemTrait5Pointer == null) { cachedItemTrait5Pointer = readPointerAtOffset(ItemTrait5Offset); }
		return cachedItemTrait5Pointer;
	}
	
	public void setItemTrait5Pointer(long ptr) {
		cachedItemTrait5Pointer = ptr;
		writePointerToOffset(ptr, ItemTrait5Offset);
		wasModified = true;
	}
	
	public long getItemTrait6Pointer() {
		if (cachedItemTrait6Pointer == null) { cachedItemTrait6Pointer = readPointerAtOffset(ItemTrait6Offset); }
		return cachedItemTrait6Pointer;
	}
	
	public void setItemTrait6Pointer(long ptr) {
		cachedItemTrait6Pointer = ptr;
		writePointerToOffset(ptr, ItemTrait6Offset);
		wasModified = true;
	}
	
	public long getItemEffectiveness1Pointer() {
		if (cachedItemEffectiveness1Pointer == null) { cachedItemEffectiveness1Pointer = readPointerAtOffset(0x30); }
		return cachedItemEffectiveness1Pointer;
	}
	
	public long getItemEffectiveness2Pointer() {
		if (cachedItemEffectiveness2Pointer == null) { cachedItemEffectiveness2Pointer = readPointerAtOffset(0x34); }
		return cachedItemEffectiveness2Pointer;
	}
	
	public long getItemEffectAnimation1Pointer() {
		if (cachedItemEffectAnimation1Pointer == null) { cachedItemEffectAnimation1Pointer = readPointerAtOffset(0x38); }
		return cachedItemEffectAnimation1Pointer;
	}
	
	public long getItemEffectAnimation2Pointer() {
		if (cachedItemEffectAnimation2Pointer == null) { cachedItemEffectAnimation2Pointer = readPointerAtOffset(0x3C); }
		return cachedItemEffectAnimation2Pointer;
	}
	
	public int getItemCost() {
		return ((data[0x40] & 0xFF) << 8) | (data[0x41]);
	}
	
	public int getItemDurability() {
		return (data[0x42] & 0xFF);
	}
	
	public int getItemMight() {
		return (data[0x43] & 0xFF);
	}
	
	public int getItemAccuracy() {
		return (data[0x44] & 0xFF);
	}
	
	public int getItemWeight() {
		return (data[0x45] & 0xFF);
	}
	
	public int getItemCritical() {
		return (data[0x46] & 0xFF);
	}
	
	public int getMinimumRange() {
		return (data[0x47] & 0xFF);
	}
	
	public int getMaximumRange() {
		return (data[0x48] & 0xFF);
	}
	
	public int getItemNumber() {
		return (data[0x49] & 0xFF);
	}
	
	public int getWeaponExperience() {
		return (data[0x4A] & 0xFF);
	}
	
	public int getUnknownValue2() {
		return data[0x4B];
	}
	
	public long getItemUnknownPointer8() {
		if (cachedItemUnknownPointer8 == null) { cachedItemUnknownPointer8 = readPointerAtOffset(0x4C); }
		return cachedItemUnknownPointer8;
	}
	
	public long getItemUnknownPointer9() {
		if (cachedItemUnknownPointer9 == null) { cachedItemUnknownPointer9 = readPointerAtOffset(0x50); }
		return cachedItemUnknownPointer9;
	}
	
	public byte[] getRemainingBytes() {
		return Arrays.copyOfRange(data, 0x54, 0x60);
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
	
	public Boolean wasModified() {
		return wasModified;
	}
	
	public long getAddressOffset() {
		return originalOffset;
	}
}
