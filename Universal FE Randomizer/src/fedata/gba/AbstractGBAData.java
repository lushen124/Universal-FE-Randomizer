package fedata.gba;

import fedata.general.FEModifiableData;

import java.util.Arrays;

public abstract class AbstractGBAData implements FEModifiableData {
	protected byte[] originalData;
	protected byte[] data;

	protected long originalOffset;
	protected Long addressOverride;

	protected Boolean wasModified = false;
	protected Boolean hasChanges = false;

	public AbstractGBAData() {
	}

	public AbstractGBAData(byte[] data, long originalOffset) {
		this.originalData = Arrays.copyOf(data, data.length);
		this.data = data;
		this.originalOffset = originalOffset;
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

	public int dataAtIndex(int index) {
		return asInt(data[index]);
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

	protected long readPointerFromData(int startingIndex) {
		return data[startingIndex] << 24 | data[startingIndex+1] << 16 | data[startingIndex+2] << 8 | data[startingIndex+3];
	}

	public static int asInt(byte b) {
		return b & 0xFF;
	}
	public static byte asByte(int i) {
		return (byte) (i & 0xFF);
	}

	public byte[] copyOriginalData() {
		return Arrays.copyOf(originalData, originalData.length);
	}
}
