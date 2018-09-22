package fedata.gba;

public interface GBAFEChapterItemData extends GBAFEModifiableData {

	public enum Type {
		CHES, ITGV
	}
	
	public Type getRewardType();
	public int getItemID();
	
	public void setItemID(int newItemID);
}
