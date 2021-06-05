package fedata.gba.general;

import fedata.general.FEModifiableData;

public class GBAFEChapterMetadataData implements FEModifiableData {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public GBAFEChapterMetadataData(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	// There's a bunch of stuff in here, but we're only interested in the fog of war vision for the time being.
	// Conveniently, this is at index 12 in all three games. If we do other stuff, we may need to
	// split this into individual game types, but for now, we're consolidating it all into the same object.
	public int getVisionRange() {
		return data[12];
	}
	
	public void setVisionRange(int visionRange) {
		data[12] = (byte)(visionRange & 0xFF);
		wasModified = true;
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
