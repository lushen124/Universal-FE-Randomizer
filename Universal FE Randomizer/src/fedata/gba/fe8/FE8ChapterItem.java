package fedata.gba.fe8;

import fedata.gba.GBAFEChapterItem;

public class FE8ChapterItem implements GBAFEChapterItem {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE8ChapterItem(byte[] data, long originalOffset) {
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
		return data[0] == 0x07 ? Type.CHES : Type.ITGV; // I say ITGV, but FE8 actually uses a combination of SETVAL 0x3 ITEM_ID and GIVEITEMTO 0xFFFF to represent this.
	}

	@Override
	public int getItemID() {
		return data[4] & 0xFF; // Thankfully, FE8 still stores the item ID in byte 4 of this arrangement, so this still works.
	}

	@Override
	public void setItemID(int newItemID) {
		data[4] = (byte)(newItemID & 0xFF);
		wasModified = true;
	}
}
