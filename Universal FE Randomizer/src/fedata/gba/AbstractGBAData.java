package fedata.gba;

import fedata.general.FEModifiableData;

public abstract class AbstractGBAData implements FEModifiableData {
	protected byte[] originalData;
	protected byte[] data;

	protected long originalOffset;
	protected Long addressOverride;

	protected Boolean wasModified = false;
	protected Boolean hasChanges = false;

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

	public void markModified() {
		this.wasModified = true;
	}
	
	public Boolean wasModified() {
		return wasModified;
	}

	public void setOriginalOffset(long offset) {
		this.originalOffset = offset;
	}
	
	public long getAddressOffset() {
		return addressOverride != null ? addressOverride : originalOffset;
	}

	public void overrideAddress(long newAddress) {
		addressOverride = newAddress;
		wasModified = true;
	}
	
	protected int asInt(byte b) {
		return b & 0xFF;
	}
	protected byte asByte(int i) {
		return (byte) (i & 0xFF);
	}
}
