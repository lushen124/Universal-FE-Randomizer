package ui.model.fe9;

import java.util.ArrayList;
import java.util.List;

public class FE9EnemyBuffOptions {
	
	public enum MinionGrowthMode {
		NONE, FLAT, SCALING
	}
	
	public enum BossStatMode {
		NONE, LINEAR, EASE_IN_OUT
	}
	
	public static class BuffStats {
		public final boolean hp;
		public final boolean str;
		public final boolean mag;
		public final boolean skl;
		public final boolean spd;
		public final boolean lck;
		public final boolean def;
		public final boolean res;
		
		public BuffStats(boolean hp, boolean str, boolean mag, boolean skl, boolean spd, boolean lck, boolean def, boolean res) {
			this.hp = hp;
			this.str = str;
			this.mag = mag;
			this.skl = skl;
			this.spd = spd;
			this.lck = lck;
			this.def = def;
			this.res = res;
		}
		
		public String buffString() {
			List<String> statStrings = new ArrayList<String>();
			if (hp) { statStrings.add("HP"); }
			if (str) { statStrings.add("STR"); }
			if (mag) { statStrings.add("MAG"); }
			if (skl) { statStrings.add("SKL"); }
			if (spd) { statStrings.add("SPD"); }
			if (lck) { statStrings.add("LCK"); }
			if (def) { statStrings.add("DEF"); }
			if (res) { statStrings.add("RES"); }
			return String.join(", ", statStrings);
		}
	}
	
	public final MinionGrowthMode minionMode;
	public final int minionBuff;
	public final BuffStats minionBuffStats;
	
	public final boolean improveMinionWeapons;
	public final int minionImprovementChance;
	public final boolean giveMinionsSkills;
	public final int minionSkillChance;
	
	public final BossStatMode bossMode;
	public final int bossBuff;
	public final BuffStats bossBuffStats;
	
	public final boolean improveBossWeapons;
	public final int bossImprovementChance;
	public final boolean giveBossSkills;
	public final int bossSkillChance;
	
	public FE9EnemyBuffOptions(MinionGrowthMode minionMode,
			int minionBuff,
			BuffStats minionBuffStats,
			boolean improveMinionWeapons,
			int minionImprovementChance,
			boolean giveMinionSkills,
			int minionSkillChance,
			BossStatMode bossMode,
			int bossBuff,
			BuffStats bossBuffStats,
			boolean improveBossWeapons,
			int bossImprovementChance,
			boolean giveBossSkills,
			int bossSkillChance) {
		
		this.minionMode = minionMode;
		this.minionBuff = minionBuff;
		this.minionBuffStats = minionBuffStats;
		this.improveMinionWeapons = improveMinionWeapons;
		this.minionImprovementChance = minionImprovementChance;
		this.giveMinionsSkills = giveMinionSkills;
		this.minionSkillChance = minionSkillChance;
		
		this.bossMode = bossMode;
		this.bossBuff = bossBuff;
		this.bossBuffStats = bossBuffStats;
		this.improveBossWeapons = improveBossWeapons;
		this.bossImprovementChance = bossImprovementChance;
		this.giveBossSkills = giveBossSkills;
		this.bossSkillChance = bossSkillChance;
	}

}
