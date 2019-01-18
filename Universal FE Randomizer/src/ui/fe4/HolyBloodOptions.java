package ui.fe4;

public class HolyBloodOptions {
	
	public final boolean randomizeGrowthBonuses;
	public final int growthTotal;
	
	public final boolean randomizeWeaponBonuses;
	
	public final boolean giveHolyBlood;
	public final boolean matchClass;
	public final int majorBloodChance;

	public HolyBloodOptions(boolean randomizeGrowth, int growthTotal, boolean randomizeWeapons, boolean giveHolyBlood, boolean matchClass, int majorBloodChance) {
		super();
		
		this.randomizeGrowthBonuses = randomizeGrowth;
		this.growthTotal = growthTotal;
		
		this.randomizeWeaponBonuses = randomizeWeapons;
		
		this.giveHolyBlood = giveHolyBlood;
		this.matchClass = matchClass;
		this.majorBloodChance = majorBloodChance;
	}
}
