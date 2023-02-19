package ui.model;

import java.util.ArrayList;
import java.util.List;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class EnemyOptions implements RecordableOption {

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

	@Override
	public void record(RecordKeeper rk, GameType type) {
		switch (minionMode) {
		case NONE:
			rk.addHeaderItem("Buff Minions", "NO");
			break;
		case FLAT:
			rk.addHeaderItem("Buff Minions", "Flat Buff (Growths +" + minionBuff + "%)");
			rk.addHeaderItem("Buffed Minion Stats", minionBuffStats.buffString());
			break;
		case SCALING:
			rk.addHeaderItem("Buff Minions",
					"Scaling Buff (Growths x" + String.format("%.2f", (minionBuff / 100.0) + 1) + ")");
			rk.addHeaderItem("Buffed Minion Stats", minionBuffStats.buffString());
			break;
		}

		if (improveMinionWeapons) {
			rk.addHeaderItem("Improve Minion Weapons", "" + minionImprovementChance + "% of enemies");
		} else {
			rk.addHeaderItem("Improve Minion Weapons", "NO");
		}

		switch (bossMode) {
		case NONE:
			rk.addHeaderItem("Buff Bosses", "NO");
			break;
		case LINEAR:
			rk.addHeaderItem("Buff Bosses", "Linear - Max Gain: +" + bossBuff);
			rk.addHeaderItem("Buffed Boss Stats", bossBuffStats.buffString());
			break;
		case EASE_IN_OUT:
			rk.addHeaderItem("Buff Bosses", "Ease In/Ease Out - Max Gain: +" + bossBuff);
			rk.addHeaderItem("Buffed Boss Stats", bossBuffStats.buffString());
			break;
		}

		if (improveBossWeapons) {
			rk.addHeaderItem("Improve Boss Weapons", "" + bossImprovementChance + "% of bosses");
		} else {
			rk.addHeaderItem("Improve Boss Weapons", "NO");
		}
	}
}
