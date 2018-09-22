package fedata.gba;

public interface GBAFEChapterItem extends GBAFEModifiableObject {

	public enum Type {
		CHES, ITGV
	}
	
	public Type getRewardType();
	public int getItemID();
	
	public void setItemID(int newItemID);
}
