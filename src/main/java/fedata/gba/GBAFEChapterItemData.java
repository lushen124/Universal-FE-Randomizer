package fedata.gba;

import fedata.general.FEModifiableData;

public interface GBAFEChapterItemData extends FEModifiableData {

	public enum Type {
		CHES, ITGV, ITGC
	}
	
	public Type getRewardType();
	public int getItemID();
	
	public void setItemID(int newItemID);
}
