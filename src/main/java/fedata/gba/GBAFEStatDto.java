package fedata.gba;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import util.WhyDoesJavaNotHaveThese;

/**
 * Stat DAO for convenient setting / getting of the 7 Main Stats
 */
public class GBAFEStatDto {
	
	/**
	 * Static GBAFEStatDao which contains the minimum stats that a character may have
	 */
	public static final GBAFEStatDto MINIMUM_STATS = new GBAFEStatDto(10, 0, 0, 0, 0, 0 ,0);
	
	public int hp;
	public int str;
	public int skl;
	public int spd;
	public int def;
	public int res;
	public int lck;
	
	public enum Stat {
		HP, POW, SKL, SPD, DEF, RES, LCK
	}
	
	/**
	 * Empty default constructor 
	 */
	public GBAFEStatDto() {
	}
	
	/**
	 * Copy constructor
	 */
	public GBAFEStatDto(GBAFEStatDto other) {
		this.hp  = other.hp;
		this.str = other.str;
		this.skl = other.skl;
		this.spd = other.spd;
		this.def = other.def;
		this.res = other.res;
		this.lck = other.lck;
	}
	
	/**
	 * accumulation constructor
	 */
	public GBAFEStatDto(List<GBAFEStatDto> bonuses) {
		for (GBAFEStatDto bonus : bonuses) {
			this.add(bonus);
		}
	}
	
	/**
	 * Constructor with 7 int values, in order hp, str, skl, spd, def, res, lck
	 */
	public GBAFEStatDto(int... args) {
		assert args.length == 7;
		
		hp = args[0];
		str = args[1];
		skl = args[2];
		spd = args[3];
		def = args[4];
		res = args[5];
		lck = args[6];
	}
	
	/**
	 * Returns a stat list that is ordered from the highest value to the lowest value.
	 */
	public List<Stat> orderedStats() {
		List<Stat> list = new ArrayList<Stat>();
		GBAFEStatDto workingDTO = new GBAFEStatDto(hp, str, skl, spd, def, res, lck);
		for (int i = 0; i < 7; i++) {
			Stat highest = workingDTO.highestStat();
			if (highest == null) { break; }
			list.add(highest);
			workingDTO.invalidateStat(highest);
		}
		
		return list;
	}
	
	private void invalidateStat(Stat stat) {
		switch (stat) {
		case HP: hp = -1; break;
		case POW: str = -1; break;
		case SKL: skl = -1; break;
		case SPD: spd = -1; break;
		case LCK: lck = -1; break;
		case DEF: def = -1; break;
		case RES: res = -1; break;
		}
	}
	
	private Stat highestStat() {
		List<Integer> list = asList();
		int highest = Collections.max(list);
		if (highest == hp) { return Stat.HP; }
		else if (highest == str) { return Stat.POW; }
		else if (highest == skl) { return Stat.SKL; }
		else if (highest == spd) { return Stat.SPD; }
		else if (highest == lck) { return Stat.LCK; }
		else if (highest == def) { return Stat.DEF; }
		else if (highest == res) { return Stat.RES; }
		else { return null; }
	}
	
	/**
	 * Returns the stats as a list with stats in order hp, str, skl, spd, def, res, lck
	 */
	public List<Integer> asList(){
		return Arrays.asList(hp, str, skl, spd, def, res, lck);
	}
	
	/**
	 * Multiplies all the stas with the given multiplier
	 */
	public GBAFEStatDto multiply(int multiplier) {
		this.hp *= multiplier;
		this.str *= multiplier;
		this.skl *= multiplier;
		this.spd *= multiplier;
		this.def *= multiplier;
		this.res *= multiplier;
		this.lck *= multiplier;
		
		return this;
	}
	
	/**
	 * Returns a new GBAFEStatDto with its stats multiplied. Does not modify the original.
	 */
	public GBAFEStatDto multipliedBy(int multiplier) {
		return new GBAFEStatDto(
				hp * multiplier,
				str * multiplier,
				skl * multiplier,
				spd * multiplier,
				def * multiplier,
				res * multiplier,
				lck * multiplier
				);
	}
	
	/**
	 * Divides all the stats with a divisor. By default the result is floored, but can be overridden to ceiling instead.
	 */
	public GBAFEStatDto dividedBy(int divisor, boolean ceil) {
		if (ceil) {
			return new GBAFEStatDto(
					(int)Math.ceil((double)hp / (double)divisor),
					(int)Math.ceil((double)str / (double)divisor),
					(int)Math.ceil((double)skl / (double)divisor),
					(int)Math.ceil((double)spd / (double)divisor),
					(int)Math.ceil((double)def / (double)divisor),
					(int)Math.ceil((double)res / (double)divisor),
					(int)Math.ceil((double)lck / (double)divisor)
					);
		} else {
			return new GBAFEStatDto(
					hp / divisor,
					str / divisor,
					skl / divisor,
					spd / divisor,
					def / divisor,
					res / divisor,
					lck / divisor
					);
		}
	}
	
	/**
	 * Returns the remainder of each stat when divided by the divisor.
	 */
	public GBAFEStatDto remainderFor(int divisor) {
		return new GBAFEStatDto(
				hp % divisor,
				str % divisor,
				skl % divisor,
				spd % divisor,
				def % divisor,
				res % divisor,
				lck % divisor
				);
	}
	
	/**
	 * Adds the stats of the given DAO to the current instance
	 */
	public GBAFEStatDto add(GBAFEStatDto other) {
		this.hp += other.hp;
		this.str += other.str;
		this.skl += other.skl;
		this.spd += other.spd;
		this.def += other.def;
		this.res += other.res;
		this.lck += other.lck;
		return this;
	} 
	
	/**
	 * Adds stats from a second set and returns the rsults. Does not modify the original.
	 */
	public GBAFEStatDto addedWith(GBAFEStatDto other) {
		return new GBAFEStatDto(
				this.hp + other.hp,
				this.str + other.str,
				this.skl + other.skl,
				this.spd + other.spd,
				this.def + other.def,
				this.res + other.res,
				this.lck + other.lck
				);
	}
	
	/**
	 * Substracts the stats from the given DAO from the current instance
	 */
	public GBAFEStatDto subtract(GBAFEStatDto other) {
		this.hp  -= other.hp;
		this.str -= other.str;
		this.skl -= other.skl;
		this.spd -= other.spd;
		this.def -= other.def;
		this.res -= other.res;
		this.lck -= other.lck;
		return this;
	} 
	
	
	/**
	 * Clamps the stats of the current instance against the given upper and lower values
	 */
	public GBAFEStatDto clamp(GBAFEStatDto lower, GBAFEStatDto upper) {
		this.hp =  WhyDoesJavaNotHaveThese.clamp(this.hp,   lower.hp , upper.hp );
		this.str = WhyDoesJavaNotHaveThese.clamp(this.str,  lower.str, upper.str);
		this.skl = WhyDoesJavaNotHaveThese.clamp(this.skl,  lower.skl, upper.skl);
		this.spd = WhyDoesJavaNotHaveThese.clamp(this.spd,  lower.spd, upper.spd);
		this.def = WhyDoesJavaNotHaveThese.clamp(this.def,  lower.def, upper.def);
		this.res = WhyDoesJavaNotHaveThese.clamp(this.res,  lower.res, upper.res);
		this.lck = WhyDoesJavaNotHaveThese.clamp(this.lck,  lower.lck, upper.lck);
		
		return this;
	}
	
	/**
	 * Given a GBAFEStatDAO o1 that logically should have lower stats than another GBAFEStatDAO o2, but doesn't, 
	 * this will return a new DAO with the necessary decreases to get from the higher stat in o1 to the lower one in o2.
	 */
	public static GBAFEStatDto downAdjust(GBAFEStatDto o1, GBAFEStatDto o2) {
		GBAFEStatDto dao = new GBAFEStatDto();
		if (o1.hp  > o2.hp ) { dao.hp  -= o1.hp  - o2.hp ; }
		if (o1.str > o2.str) { dao.str -= o1.str - o2.str; }
		if (o1.skl > o2.skl) { dao.skl -= o1.skl - o2.skl; }
		if (o1.spd > o2.spd) { dao.spd -= o1.spd - o2.spd; }
		if (o1.def > o2.def) { dao.def -= o1.def - o2.def; }
		if (o1.res > o2.res) { dao.res -= o1.res - o2.res; }
		if (o1.lck > o2.lck) { dao.lck -= o1.lck - o2.lck; }
		return dao;
	}
	
	/**
	 * Given a GBAFEStatDAO o1 that logically should have higher stats than another GBAFEStatDAO o2, but doesn't, 
	 * this will return a new DAO with the necessary increases to get from the lower stat in o1 to the higher one in o2.
	 */
	public static GBAFEStatDto upAdjust(GBAFEStatDto o1, GBAFEStatDto o2) {
		GBAFEStatDto dao = new GBAFEStatDto();
		if (o1.hp  < o2.hp ) { dao.hp  += o1.hp  - o2.hp ; }
		if (o1.str < o2.str) { dao.str += o1.str - o2.str; }
		if (o1.skl < o2.skl) { dao.skl += o1.skl - o2.skl; }
		if (o1.spd < o2.spd) { dao.spd += o1.spd - o2.spd; }
		if (o1.def < o2.def) { dao.def += o1.def - o2.def; }
		if (o1.res < o2.res) { dao.res += o1.res - o2.res; }
		if (o1.lck < o2.lck) { dao.lck += o1.lck - o2.lck; }
		return dao;
	}
	
	/**
	 * Determines the expected value of a set of base stats when leveled up a certain amount of levels
	 * at a given set of growths, where the final value is the cumulative growth over the levels
	 * with the remainder past 100 represented as a chance for an additional +1 after that.
	 * 
	 * 
	 * For example, if the growth passed in is 25%:
	 * If leveling up by 1, then the cumulative growth is 25%, so the result is +0 with a 25% chance of +1.
	 * If leveling up by 3, then the cumulative growth is 75%, so the result is +0 with a 75% chance of +1.
	 * If leveling up by 5, then the cumulative growth is 125%, so the result is +1 with a 25% chance of +2.
	 * If leveling up by 10, then the cumulative growth is 250%, so the result is +2 with a 50% chance of +3.
	 */
	public static GBAFEStatDto expectedValueLevel(GBAFEStatDto bases, GBAFEStatDto growths, int numberOfLevels, Random rng) {
		GBAFEStatDto cumulativeGrowths = growths.multipliedBy(numberOfLevels);
		GBAFEStatDto workingStats = bases.addedWith(cumulativeGrowths.dividedBy(100, false));
		GBAFEStatDto remainderGrowths = growths.remainderFor(100);
		if (rng.nextInt(100) < remainderGrowths.hp) { workingStats.hp += 1; }
		if (rng.nextInt(100) < remainderGrowths.str) { workingStats.str += 1; }
		if (rng.nextInt(100) < remainderGrowths.skl) { workingStats.skl += 1; }
		if (rng.nextInt(100) < remainderGrowths.spd) { workingStats.spd += 1; }
		if (rng.nextInt(100) < remainderGrowths.def) { workingStats.def += 1; }
		if (rng.nextInt(100) < remainderGrowths.res) { workingStats.res += 1; }
		if (rng.nextInt(100) < remainderGrowths.lck) { workingStats.lck += 1; }
		return workingStats;
	}
	
	@Override
	public String toString() {
		return String.format("GBAFEStatDAO: hp %d, str %d, skl %d, spd %d, def %d, res %d, lck %d", hp, str, skl, spd, def, res, lck);
	}
}
