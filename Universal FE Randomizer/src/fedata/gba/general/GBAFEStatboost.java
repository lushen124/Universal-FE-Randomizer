package fedata.gba.general;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fedata.gba.AbstractGBAData;
import fedata.gba.GBAFEStatDto;
import random.general.PoolDistributor;

/**
 * Dataobject that contains the data of a GBAFE Statboost entry.
 * 
 * Each entry has 12 bytes: HP, POW, SKL, SPD, DEF, RES, LUK, MOVE, CON, ??, ??, ??. (?? = unused)
 * 
 */
public class GBAFEStatboost extends AbstractGBAData {
	
	public GBAFEStatboostDao dao;
	
	public enum BoostedStat {
		HP(0), STR(1), SKL(2), SPD(3), DEF(4), RES(5), LCK(6), MOV(7), CON(8);
		
		public int statIndex;
		
		BoostedStat(int index) {
			this.statIndex = index;
		}
		
		public static PoolDistributor<BoostedStat> getPool(boolean excludeMovCon){
			PoolDistributor<BoostedStat> pool = new PoolDistributor<>();
			pool.addAll(values());
			if (excludeMovCon) {
				pool.removeItem(MOV, true);
				pool.removeItem(CON, true);
			}

			return pool;
		}
		
		public static BoostedStat valueOf(int index) {
			switch(index) {
			case 0: return HP; 
			case 1: return STR;
			case 2: return SKL;
			case 3: return SPD; 
			case 4: return DEF;
			case 5: return RES;
			case 6: return LCK;
			case 7: return MOV;
			case 8: return CON;
			default: throw new UnsupportedOperationException(String.format("No Stat matching the given index %d found", index));
			}
		}
	}
	
	public class GBAFEStatboostDao extends GBAFEStatDto {
		public GBAFEStatboost parent;
		public int mov;
		public int con;
		
		public GBAFEStatboostDao(GBAFEStatboost parent, byte[] bytes) {
			assert(bytes.length == 12); // HP, POW, SKL, SPD, DEF, RES, LCK, MOV, CON in that order followed by 3 useless bytes.
			this.parent = parent;
			
			hp = asInt(bytes[0]);
			str = asInt(bytes[1]);
			skl = asInt(bytes[2]);
			spd = asInt(bytes[3]);
			def = asInt(bytes[4]);
			res = asInt(bytes[5]);
			lck = asInt(bytes[6]);
			mov = asInt(bytes[7]);
			con = asInt(bytes[8]);
		}
		
		/**
		 * Verifies that there is only one non-zero stat and throws an exception if there is more than one.
		 * After verifying, returns the index of that stat.
		 */
		public int getIndexOfOnlyStat() {
			List<Integer> stats = asList().stream().filter(i -> i != 0).collect(Collectors.toList());
			if (stats.size() != 1) {
				throw new UnsupportedOperationException("Tried finding the index of the only stat, but there is more than one.");
			}
			return asList().indexOf(stats.get(0));
		}
		
		public void setStatAtIndex(BoostedStat boostedStat, int stat) {
			switch(boostedStat) {
				case HP: hp  = stat; break;
				case STR: str = stat; break;
				case SKL: skl = stat; break;
				case SPD: spd = stat; break;
				case DEF: def = stat; break;
				case RES: res = stat; break;
				case LCK: lck = stat; break;
				case MOV: mov = stat; break;
				case CON: con = stat; break;
			}
		}
		
		/**
		 * Subtracts the stats from the given DAO from the current instance
		 */
		public GBAFEStatboostDao subtract(GBAFEStatboostDao other) {
			super.subtract(other);
			this.con -= other.con;
			this.mov -= other.mov;
			return this;
		} 
		
		public String toString() {
			return String.format("%s, move %d, con %d", super.toString(), mov, con); 
		}
		
		@Override
		public List<Integer> asList(){
			ArrayList<Integer> list = new ArrayList<>(super.asList());
			list.add(mov);
			list.add(con);
			return list;
		}
		
		public long numberOfBoosts() {
			return this.asList().stream().filter(i -> i > 0).count();
		}
		
		/**
		 * Return a string which should be used for the ingame description of this statbooster.
		 * 
		 * We have access to 3 lines of 32 characters for the description.
		 * 
		 * Stats will be shown as following:
		 * 
		 * HP +XX POW +XX SKL +XX
		 * SPD +XX DEF +XX RES +XX 
		 * LCK +XX MOV +XX CON +XX 
		 */
		public String buildDescription() {
			StringBuilder sb = new StringBuilder();
			if (numberOfBoosts() == 1) {
				sb.append("Grants ");
			}
			sb.append(buildDescriptionsImp("%s +%d"));
			return sb.toString();
		}

		/**
		 * Return a string which should be used for the ingame description of this statbooster.
		 * 
		 * This description is displayed in the menu when you try to use the item.
		 * 
		 * We have access to 3 lines of 13 characters for the description.
		 * 
		 * This will just be the short names for the stats, sadly if we add the "Boosts" at the beginning 
		 * it wouldn't fit if too many stats are boosted, so only add that if it's single one.
		 */
		public String buildUseDescription() {
			StringBuilder sb = new StringBuilder();
			if (numberOfBoosts() == 1) {
				sb.append("Boosts ");
			}
			sb.append(buildDescriptionsImp("%s"));
			return sb.toString();
		}
		
		public String buildDescriptionsImp(String format) {
			StringBuilder sb = new StringBuilder();
			List<Integer> stats = asList();
			int nrAppendedStats = 0;
			for (int i = 0; i < 9; i++) {
				if (stats.get(i) == 0) {
					continue;
				}
				
				nrAppendedStats++;
				
				switch(i) {
					case 0: sb.append(String.format(format, "HP", this.hp)); break;
					case 1: sb.append(String.format(format, "Pow", this.str)); break;
					case 2: sb.append(String.format(format, "Skl", this.skl)); break;
					case 3: sb.append(String.format(format, "Spd", this.spd)); break;
					case 4: sb.append(String.format(format, "Def", this.def)); break;
					case 5: sb.append(String.format(format, "Res", this.res)); break;
					case 6: sb.append(String.format(format, "Lck", this.lck)); break;
					case 7: sb.append(String.format(format, "Mov", this.mov)); break;
					case 8: sb.append(String.format(format, "Con", this.con)); break;
				}
				
				if (Arrays.asList(3, 6).contains(nrAppendedStats)) {
					sb.append("[0x1]");
				} else {
					sb.append(" ");
				}
			}
			
			return sb.toString();
		}
	}
	
	/**
	 * Constructor to be used when new Statboosts are being allocated in the rom.
	 */
	public GBAFEStatboost(long offset) {
		byte[] emptyData = new byte[12];
		this.originalData = emptyData;
		this.data = emptyData;
		this.originalOffset = offset;
		this.dao = new GBAFEStatboostDao(this, data);
	}
	
	/**
	 * Constructor to be used when default Statboosts are loaded from the Rom. 
	 */
	public GBAFEStatboost(byte[] data, long originalOffset) {
		this.data = data;
		// Make a copy of the array to ensure that the original data will not be overriden by changes, 
		// so that we can still identify which statboosters were originally boots.
		this.originalData = Arrays.copyOf(this.data, this.data.length);
		this.originalOffset = originalOffset;
		this.dao = new GBAFEStatboostDao(this, data);
	}
	
	/**
	 * Writes the data from the Dao into the data array. 
	 */
	public void write() {
		this.data[0] = asByte(dao.hp);
		this.data[1] = asByte(dao.str);
		this.data[2] = asByte(dao.skl);
		this.data[3] = asByte(dao.spd);
		this.data[4] = asByte(dao.def);
		this.data[5] = asByte(dao.res);
		this.data[6] = asByte(dao.lck);
		this.data[7] = asByte(dao.mov);
		this.data[8] = asByte(dao.con);
		this.wasModified = true;
	}
	
	/**
	 * Returns true if this stat booster was originally a pair of boots. 
	 */
	public boolean isBoots() {
		return asInt(this.originalData[7]) != 0;
	}
	
	/**
	 * Returns true if this stat booster was originally a Body Ring. 
	 */
	public boolean isBodyRing() {
		return asInt(this.originalData[8]) != 0;
	}
}
