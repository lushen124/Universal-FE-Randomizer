package ui.model;

public class EnemyOptions {

	public enum MinionGrowthMode {
		NONE, FLAT, SCALING
	}
	
	public enum BossStatMode {
		NONE, LINEAR, EASE_IN_OUT
	}
	
	public final MinionGrowthMode minionMode;
	public final int minionBuff;
	
	public final boolean improveMinionWeapons;
	public final int minionImprovementChance;
	
	public final BossStatMode bossMode;
	public final int bossBuff;
	
	public final boolean improveBossWeapons;
	public final int bossImprovementChance;
	
	public EnemyOptions(MinionGrowthMode mode, int buffAmount, boolean minionWeapons, int minionWeaponChance, BossStatMode bossMode, int bossBuff, boolean bossWeapons, int bossWeaponChance) {
		super();
		this.minionMode = mode;
		this.minionBuff = buffAmount;
		this.improveMinionWeapons = minionWeapons;
		this.minionImprovementChance = minionWeaponChance;
		
		this.bossMode = bossMode;
		this.bossBuff = bossBuff;
		this.improveBossWeapons = bossWeapons;
		this.bossImprovementChance = bossWeaponChance;
	}
}
