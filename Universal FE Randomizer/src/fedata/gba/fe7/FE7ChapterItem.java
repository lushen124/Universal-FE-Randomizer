package fedata.gba.fe7;

import fedata.gba.GBAFEChapterItem;

public class FE7ChapterItem implements GBAFEChapterItem {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE7ChapterItem(byte[] data, long originalOffset) {
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
		return data[0] == 0x5B ? Type.ITGV : Type.CHES;
	}

	@Override
	public int getItemID() {
		return data[4] & 0xFF;
	}

	@Override
	public void setItemID(int newItemID) {
		data[4] = (byte)(newItemID & 0xFF);
		wasModified = true;
	}

}
