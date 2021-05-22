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
	
	public static int ItemEffectiveness1Offset = 0x30;
	public static int ItemEffectiveness2Offset = 0x34;
	
	public static int ItemAnimation1Offset = 0x38;
	public static int ItemAnimation2Offset = 0x3C;

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
	
	public void setItemSubtypePointer(long ptr) {
		cachedItemSubtypePointer = ptr;
		writePointerToOffset(ptr, 0x10);
		wasModified = true;
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
		if (cachedItemEffectiveness1Pointer == null) { cachedItemEffectiveness1Pointer = readPointerAtOffset(ItemEffectiveness1Offset); }
		return cachedItemEffectiveness1Pointer;
	}
	
	public void setItemEffectiveness1Pointer(long ptr) {
		cachedItemEffectiveness1Pointer = ptr;
		writePointerToOffset(ptr, ItemEffectiveness1Offset);
		wasModified = true;
	}
	
	public long getItemEffectiveness2Pointer() {
		if (cachedItemEffectiveness2Pointer == null) { cachedItemEffectiveness2Pointer = readPointerAtOffset(ItemEffectiveness2Offset); }
		return cachedItemEffectiveness2Pointer;
	}
	
	public void setItemEffectiveness2Pointer(long ptr) {
		cachedItemEffectiveness2Pointer = ptr;
		writePointerToOffset(ptr, ItemEffectiveness2Offset);
		wasModified = true;
	}
	
	public long getItemEffectAnimation1Pointer() {
		if (cachedItemEffectAnimation1Pointer == null) { cachedItemEffectAnimation1Pointer = readPointerAtOffset(ItemAnimation1Offset); }
		return cachedItemEffectAnimation1Pointer;
	}
	
	public void setItemEffectAnimation1Pointer(long ptr) {
		cachedItemEffectAnimation1Pointer = ptr;
		writePointerToOffset(ptr, ItemAnimation1Offset);
		wasModified = true;
	}
	
	public long getItemEffectAnimation2Pointer() {
		if (cachedItemEffectAnimation2Pointer == null) { cachedItemEffectAnimation2Pointer = readPointerAtOffset(ItemAnimation2Offset); }
		return cachedItemEffectAnimation2Pointer;
	}
	
	public void setItemEffectAnimation2Pointer(long ptr) {
		cachedItemEffectAnimation2Pointer = ptr;
		writePointerToOffset(ptr, ItemAnimation2Offset);
		wasModified = true;
	}
	
	public int getItemCost() {
		return ((data[0x40] & 0xFF) << 8) | (data[0x41] & 0xFF);
	}
	
	public void setItemCost(int newCost) {
		data[0x40] = (byte)((newCost & 0xFF00) >> 8);
		data[0x41] = (byte)(newCost & 0xFF);
		wasModified = true;
	}
	
	public int getItemDurability() {
		return (data[0x42] & 0xFF);
	}
	
	public void setItemDurability(int durability) {
		data[0x42] = (byte)(durability & 0xFF);
		wasModified = true;
	}
	
	public int getItemMight() {
		return (data[0x43] & 0xFF);
	}
	
	public void setItemMight(int might) {
		data[0x43] = (byte)(might & 0xFF);
		wasModified = true;
	}
	
	public int getItemAccuracy() {
		return (data[0x44] & 0xFF);
	}
	
	public void setItemAccuracy(int accuracy) {
		data[0x44] = (byte)(accuracy & 0xFF);
		wasModified = true;
	}
	
	public int getItemWeight() {
		return (data[0x45] & 0xFF);
	}
	
	public void setItemWeight(int weight) {
		data[0x45] = (byte)(weight & 0xFF);
		wasModified = true;
	}
	
	public int getItemCritical() {
		return (data[0x46] & 0xFF);
	}
	
	public void setItemCritical(int crit) {
		data[0x46] = (byte)(crit & 0xFF);
		wasModified = true;
	}
	
	public int getMinimumRange() {
		return (data[0x47] & 0xFF);
	}
	
	public int getMaximumRange() {
		return (data[0x48] & 0xFF);
	}
	
	public void setRange(int min, int max) {
		if (min <= max) {
			data[0x47] = (byte)(min & 0xFF);
			data[0x48] = (byte)(max & 0xFF);
			wasModified = true;
		}
	}
	
	public int getIconNumber() {
		return (data[0x49] & 0xFF);
	}
	
	public void setIconNumber(int iconNumber) {
		data[0x49] = (byte)(iconNumber & 0xFF);
		wasModified = true;
	}
	
	public int getWeaponExperience() {
		return (data[0x4A] & 0xFF);
	}
	
	public void setWeaponExperience(int exp) {
		data[0x4A] = (byte)(exp & 0xFF);
		wasModified = true;
	}
	
	public int getHPBonus() {
		return data[0x4B];
	}
	
	public void setHPBonus(int newValue) {
		data[0x4B] = (byte)(newValue & 0xFF);
		wasModified = true;
	}
	
	public int getSTRBonus() {
		return (data[0x4C] & 0xFF);
	}
	
	public void setSTRBonus(int bonus) {
		data[0x4C] = (byte)(bonus & 0xFF);
		wasModified = true;
	}
	
	public int getMAGBonus() {
		return (data[0x4D] & 0xFF);
	}
	
	public void setMAGBonus(int bonus) {
		data[0x4D] = (byte)(bonus & 0xFF);
		wasModified = true;
	}
	
	public int getSKLBonus() {
		return (data[0x4E] & 0xFF);
	}
	
	public void setSKLBonus(int bonus) {
		data[0x4E] = (byte)(bonus & 0xFF);
		wasModified = true;
	}
	
	public int getSPDBonus() {
		return (data[0x4F] & 0xFF);
	}
	
	public void setSPDBonus(int bonus) {
		data[0x4F] = (byte)(bonus & 0xFF);
		wasModified = true;
	}
	
	public int getLCKBonus() {
		return (data[0x50] & 0xFF);
	}
	
	public void setLCKBonus(int bonus) {
		data[0x50] = (byte)(bonus & 0xFF);
		wasModified = true;
	}
	
	public int getDEFBonus() {
		return (data[0x51] & 0xFF);
	}
	
	public void setDEFBonus(int bonus) {
		data[0x51] = (byte)(bonus & 0xFF);
		wasModified = true;
	}
	
	public int getRESBonus() {
		return (data[0x52] & 0xFF);
	}
	
	public void setRESBonus(int bonus) {
		data[0x52] = (byte)(bonus & 0xFF);
		wasModified = true;
	}
		
	public byte[] getRemainingBytes() {
		return Arrays.copyOfRange(data, 0x53, 0x60);
	}
	
	public void setByteInRemainingBytes(byte value, int offset) {
		if (offset >= 0x54 && offset < 0x60) {
			data[offset] = value;
			wasModified = true;
		}
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
	
	public Boolean wasModified() {
		return wasModified;
	}
	
	public long getAddressOffset() {
		return originalOffset;
	}
}
