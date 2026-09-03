package ui.model;

public class ShopOptions {

	public final MinMaxOption shopSize;
	public final boolean useProgressionWeights;
	public final boolean mixArmoryVendor;
	public final boolean mixSecretVendor;
	
	public final boolean randomizeMapShops; // FE8 Only
	
	public ShopOptions(MinMaxOption shopSize, boolean useProgressionWeights, boolean mixArmoryVendor, boolean mixSecretVendor, boolean randomizeMapShops) {
		super();
		this.shopSize = shopSize;
		this.useProgressionWeights = useProgressionWeights;
		this.mixArmoryVendor = mixArmoryVendor;
		this.mixSecretVendor = mixSecretVendor;
		this.randomizeMapShops = randomizeMapShops;
	}
}
