package fedata;

public interface FEChapterItem extends FEModifiableObject {

	public enum Type {
		CHES, ITGV
	}
	
	public Type getRewardType();
	public int getItemID();
	
	public void setItemID(int newItemID);
}
