package fedata.gcnwii.fe9;

import java.util.Arrays;

import fedata.general.FEModifiableData;
import util.WhyDoesJavaNotHaveThese;

public class FE9Skill implements FEModifiableData {

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE9Skill(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	private Long cachedSkillIDPointer;
	private Long cachedUnknownPointer;
	private Long cachedSkillNamePointer;
	private Long cachedHelpTextPointer1;
	private Long cachedHelpTextPointer2;
	
	private Long cachedEffectIDPointer;
	
	private Long cachedUnknownPointer2;
	
	// The following two pointers are double-dereferenced. (i.e. they point to another pointer.)
	private Long cachedItemIDPointer; // Skill scroll?
	private Long cachedClassRestrictionPointer;
	
	public long getSkillIDPointer() {
		if (cachedSkillIDPointer == null) { cachedSkillIDPointer = readPointerAtOffset(0x0); }
		return cachedSkillIDPointer;
	}
	
	public long getUnknownPointer() {
		if (cachedUnknownPointer == null) { cachedUnknownPointer = readPointerAtOffset(0x4); }
		return cachedUnknownPointer;
	}
	
	public long getSkillNamePointer() {
		if (cachedSkillNamePointer == null) { cachedSkillNamePointer = readPointerAtOffset(0x8); }
		return cachedSkillNamePointer;
	}
	
	public long getHelpText1Pointer() {
		if (cachedHelpTextPointer1 == null) { cachedHelpTextPointer1 = readPointerAtOffset(0xC); }
		return cachedHelpTextPointer1;
	}
	
	public long getHelpText2Pointer() {
		if (cachedHelpTextPointer2 == null) { cachedHelpTextPointer2 = readPointerAtOffset(0x10); }
		return cachedHelpTextPointer2;
	}
	
	public long getEffectIDPointer() {
		if (cachedEffectIDPointer == null) { cachedEffectIDPointer = readPointerAtOffset(0x14); }
		return cachedEffectIDPointer;
	}
	
	public int getSkillNumber() {
		return data[0x18] & 0xFF;
	}
	
	public int getUnknownValue1() {
		return data[0x19] & 0xFF;
	}
	
	public int getSkillCost() {
		return data[0x1A] & 0xFF;
	}
	
	public int getUnknownValue2() {
		return data[0x1B] & 0xFF;
	}
	
	public long getUnknownPointer2() {
		if (cachedUnknownPointer2 == null) { 
			byte[] ptr = Arrays.copyOfRange(data, 0x1C, 0x20);
			cachedUnknownPointer2 = WhyDoesJavaNotHaveThese.longValueFromByteArray(ptr, false);
		}
		return cachedUnknownPointer2;
	}
	
	public long getItemIDPointer() {
		if (cachedItemIDPointer == null) { cachedItemIDPointer = readPointerAtOffset(0x20); }
		return cachedItemIDPointer;
	}
	
	public long getClassRestrictionPointer() {
		if (cachedClassRestrictionPointer == null) { cachedClassRestrictionPointer = readPointerAtOffset(0x24); }
		return cachedClassRestrictionPointer;
	}
	
	private long readPointerAtOffset(int offset) {
		byte[] ptr = Arrays.copyOfRange(data, offset, offset + 4);
		long address = WhyDoesJavaNotHaveThese.longValueFromByteArray(ptr, false);
		if (address != 0) { address += 0x20; }
		return address;
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
