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

	private Long cachedItemIDPointer; // Points to the item that can grant this skill.
	private Long cachedRestrictionsPointer; // Points to the start of a list of restrictions (can be JID or PID)
	
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
	
	public int getRestrictionCount() {
		return data[0x1C] & 0xFF;
	}
	
	public void setRestrictionCount(int newCount) {
		data[0x1C] = (byte)(newCount & 0xFF);
		wasModified = true;
	}
	
	public long getItemIDPointer() {
		if (cachedItemIDPointer == null) { cachedItemIDPointer = readPointerAtOffset(0x20); }
		return cachedItemIDPointer;
	}
	
	public long getRestrictionPointer() {
		if (cachedRestrictionsPointer == null) { cachedRestrictionsPointer = readPointerAtOffset(0x24); }
		return cachedRestrictionsPointer;
	}
	
	public void setRestrictionPointer(long newPointer) {
		cachedRestrictionsPointer = newPointer;
		writePointerToOffset(newPointer, 0x1C);
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
	
	public Boolean wasModified() {
		return wasModified;
	}
	
	public long getAddressOffset() {
		return originalOffset;
	}
}
