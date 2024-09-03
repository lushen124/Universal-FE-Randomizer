package fedata.gba.general;

import java.util.Set;

public interface GBAFEShopProvider {

	public Set<GBAFEShop> allShops();
	public Set<GBAFEShop> allVendors();
	public Set<GBAFEShop> allArmories();
	public Set<GBAFEShop> allSecretShops();
}
