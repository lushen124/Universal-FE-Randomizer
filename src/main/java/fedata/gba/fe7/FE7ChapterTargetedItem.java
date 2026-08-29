package fedata.gba.fe7;

import fedata.gba.GBAFEChapterTargetedItemData;

public class FE7ChapterTargetedItem implements GBAFEChapterTargetedItemData {

	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE7ChapterTargetedItem(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}

	@Override
	public void resetData() {
		wasModified = false;
		data = originalData;
	}

	@Override
	public void commitChanges() {
		if (wasModified) {
			hasChanges = true;
		}
		
		wasModified = false;
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public Boolean hasCommittedChanges() {
		return hasChanges;
	}

	@Override
	public Boolean wasModified() {
		return wasModified;
	}

	@Override
	public long getAddressOffset() {
		return originalOffset;
	}

	@Override
	public Type getRewardType() {
		if (data[0] == 0x5C) { return Type.ITGC; }
		
		return data[0] == 0x5B ? Type.ITGV : Type.CHES;
	}
	
	public int getTargetID() {
		return data[4] & 0xFF;
	}

	@Override
	public int getItemID() {
		return data[8] & 0xFF;
	}

	@Override
	public void setItemID(int newItemID) {
		data[8] = (byte)(newItemID & 0xFF);
		wasModified = true;
	}
}
