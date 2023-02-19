package fedata.gba.general;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fedata.gba.AbstractGBAData;
import fedata.gba.GBAFEStatDto;

/**
 * Dataobject that contains the data of a GBAFE Stat boost entry.
 * 
 * Each entry has 12 bytes: HP, POW, SKL, SPD, DEF, RES, LUK, MOVE, CON, ??, ??, ??. (?? = unused)
 * 
 */
public class GBAFEStatboost extends AbstractGBAData {
	
	public GBAFEStatboostDao dao;
	
	public class GBAFEStatboostDao extends GBAFEStatDto {
		public GBAFEStatboost parent;
		public int mov;
		public int con;
		
		public boolean statIndexIsHp(int index) {
			return index == 0;
		}
		
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
		 * After verifiying, returns the index of that stat.
		 */
		public int getIndexOfOnlyStat() {
			List<Integer> stats = asList().stream().filter(i -> i != 0).collect(Collectors.toList());
			if (stats.size() != 1) {
				throw new UnsupportedOperationException("Tried finding the index of the only stat, but there is more than one.");
			}
			return asList().indexOf(stats.get(0));
		}
		
		public void setStatAtIndex(int index, int stat) {
			switch(index) {
			case 0: 
				hp = stat;
				break;
			case 1: 
				str = stat;
				break;
			case 2: 
				skl = stat;
				break;
			case 3: 
				spd = stat;
				break;
			case 4: 
				def = stat;
				break;
			case 5: 
				res = stat;
				break;
			case 6: 
				lck = stat;
				break;
			case 7: 
				mov = stat;
				break;
			case 8: 
				con = stat;
				break;
			}
		}
		
		/**
		 * Substracts the stats from the given DAO from the current instance
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
		this.originalData = data;
		this.data = data;
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
