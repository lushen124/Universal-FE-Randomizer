package fedata.gba.general;

/**
 * Interface for Providing that a dataclass needs to extend to be able to use the Statbooster Randomization feature. 
 */
public interface GBAFEStatboostProvider {
	
	/**
	 * Returns the Address of the base Statboost table
	 */
	public long getBaseAddress();
	
	/**
	 * Returns the size of an entry in the Statboost table, this is 12 in all of FE6,7 and 8 
	 */
	public default int getEntrySize() {
		return 12;
	}
	
	/**
	 * Returns the default number of entries that are in the Statboost table for the game
	 */
	public int getNumberEntries();
	
	/**
	 * Returns true if the given index is by default a consumable statbooster. (0 Indexed)
	 */
	public boolean isStatboosterIndex(int i);
}
