package ui.fe4;

public class HolyBloodOptions {
	
	public final boolean randomizeGrowthBonuses;
	public final int growthTotal;
	
	public final boolean randomizeWeaponBonuses;

	public HolyBloodOptions(boolean randomizeGrowth, int growthTotal, boolean randomizeWeapons) {
		super();
		
		this.randomizeGrowthBonuses = randomizeGrowth;
		this.growthTotal = growthTotal;
		
		this.randomizeWeaponBonuses = randomizeWeapons;
	}
}
