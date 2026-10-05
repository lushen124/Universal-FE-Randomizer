package fedata.gcnwii.fe9;

import java.util.Arrays;

import fedata.general.FEModifiableData;
import util.WhyDoesJavaNotHaveThese;

public class FE9TextEntry implements FEModifiableData {
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private Long cachedIDOffset;
	private Long cachedStringOffset;
	
	public FE9TextEntry(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public long getIDOffset() {
		if (cachedIDOffset == null) {
			cachedIDOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(Arrays.copyOfRange(data, 4, 8), false); 
		}
		return cachedIDOffset;
	}
	
	public long getStringOffset() {
		if (cachedStringOffset == null) {
			cachedStringOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(Arrays.copyOfRange(data, 0, 4),  false) + 0x20;
		}
		return cachedStringOffset;
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
