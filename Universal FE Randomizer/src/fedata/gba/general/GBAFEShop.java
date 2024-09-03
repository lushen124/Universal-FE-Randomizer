package fedata.gba.general;

import java.util.Set;

public interface GBAFEShop {
	
	public long getPointerOffset();
	public long getOriginalShopAddress();
	public Set<GBAFEShop> groupedShops();

}
