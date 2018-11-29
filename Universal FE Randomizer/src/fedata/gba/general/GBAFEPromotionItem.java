package fedata.gba.general;

public interface GBAFEPromotionItem {
	
	public long getListAddress();
	public Boolean isIndirected(); // True for FE7 and 8, but not FE6.
	
	public String itemName();

}
