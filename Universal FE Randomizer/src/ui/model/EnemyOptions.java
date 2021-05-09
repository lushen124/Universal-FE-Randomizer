package ui.model;

import java.util.ArrayList;
import java.util.List;

public class EnemyOptions {

	public enum MinionGrowthMode {
		NONE, FLAT, SCALING
	}
	
	public enum BossStatMode {
		NONE, LINEAR, EASE_IN_OUT
	}
	
	public static class BuffStats {
		public final boolean hp;
		public final boolean str;
		public final boolean skl;
		public final boolean spd;
		public final boolean lck;
		public final boolean def;
		public final boolean res;
		
		public BuffStats(boolean hp, boolean str, boolean skl, boolean spd, boolean lck, boolean def, boolean res) {
			this.hp = hp;
			this.str = str;
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
	
	public final BossStatMode bossMode;
	public final int bossBuff;
	public final BuffStats bossBuffStats;
	
	public final boolean improveBossWeapons;
	public final int bossImprovementChance;
	
	public EnemyOptions(MinionGrowthMode mode, int buffAmount, boolean minionWeapons, int minionWeaponChance, BuffStats minionBuffStats, BossStatMode bossMode, int bossBuff, boolean bossWeapons, int bossWeaponChance, BuffStats bossBuffStats) {
		super();
		this.minionMode = mode;
		this.minionBuff = buffAmount;
		this.improveMinionWeapons = minionWeapons;
		this.minionImprovementChance = minionWeaponChance;
		this.minionBuffStats = minionBuffStats;
		
		this.bossMode = bossMode;
		this.bossBuff = bossBuff;
		this.improveBossWeapons = bossWeapons;
		this.bossImprovementChance = bossWeaponChance;
		this.bossBuffStats = bossBuffStats;
	}
}
