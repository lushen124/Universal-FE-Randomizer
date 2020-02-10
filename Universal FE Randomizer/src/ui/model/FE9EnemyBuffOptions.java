package ui.model;

public class FE9EnemyBuffOptions {
	
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
	public final boolean giveMinionsSkills;
	public final int minionSkillChance;
	
	public final BossStatMode bossMode;
	public final int bossBuff;
	
	public final boolean improveBossWeapons;
	public final int bossImprovementChance;
	public final boolean giveBossSkills;
	public final int bossSkillChance;
	
	public FE9EnemyBuffOptions(MinionGrowthMode minionMode,
			int minionBuff,
			boolean improveMinionWeapons,
			int minionImprovementChance,
			boolean giveMinionSkills,
			int minionSkillChance,
			BossStatMode bossMode,
			int bossBuff,
			boolean improveBossWeapons,
			int bossImprovementChance,
			boolean giveBossSkills,
			int bossSkillChance) {
		
		this.minionMode = minionMode;
		this.minionBuff = minionBuff;
		this.improveMinionWeapons = improveMinionWeapons;
		this.minionImprovementChance = minionImprovementChance;
		this.giveMinionsSkills = giveMinionSkills;
		this.minionSkillChance = minionSkillChance;
		
		this.bossMode = bossMode;
		this.bossBuff = bossBuff;
		this.improveBossWeapons = improveBossWeapons;
		this.bossImprovementChance = bossImprovementChance;
		this.giveBossSkills = giveBossSkills;
		this.bossSkillChance = bossSkillChance;
	}

}
