package ui.model.fe4;

public class HolyBloodOptions {
	
	public enum STRMAGOptions {
		NO_LIMIT, ADJUST_STR_MAG, LIMIT_STR_MAG
	}
	
	public final boolean randomizeGrowthBonuses;
	public final int growthTotal;
	public final int chunkSize;
	public final int hpBaseline;
	public final STRMAGOptions strMagOptions;
	public final boolean generateUniqueBonuses;
	
	public final boolean randomizeWeaponBonuses;
	
	public final boolean giveHolyBlood;
	public final boolean matchClass;
	public final int majorBloodChance;
	public final int minorBloodChance;

	public HolyBloodOptions(boolean randomizeGrowth, int growthTotal, int chunkSize, int hpBaseline, STRMAGOptions strMagOptions, boolean uniqueBonuses, boolean randomizeWeapons, boolean giveHolyBlood, boolean matchClass, int majorBloodChance, int minorBloodChance) {
		super();
		
		this.randomizeGrowthBonuses = randomizeGrowth;
		this.growthTotal = growthTotal;
		this.chunkSize = chunkSize;
		this.hpBaseline = hpBaseline;
		this.strMagOptions = strMagOptions;
		this.generateUniqueBonuses = uniqueBonuses;
		
		this.randomizeWeaponBonuses = randomizeWeapons;
		
		this.giveHolyBlood = giveHolyBlood;
		this.matchClass = matchClass;
		this.majorBloodChance = majorBloodChance;
		this.minorBloodChance = minorBloodChance;
	}
}
