package random.gba.randomizer.shuffling;

import java.util.List;

import random.gba.randomizer.shuffling.data.GBAFEPortraitData;

/**
 * Interface containing all methods that a Data Provider needs to offer to use the Character Shuffling functionality
 */
public interface GBAFEShufflingDataProvider {
	/**
	 * The address of the portrait data Table
	 */
	public long portraitDataTableAddress();

	/**
	 * The number of entries in the Portrait Data Table
	 */
	public int numberOfPortraits();
	
	/**
	 * The length of each entry in the Portrait Data Table
	 */
	public int bytesPerPortraitEntry();
	
	/**
	 * Factory for the GBAFEPortraitData. 
	 * The data class can decide if it creates the object as base GBAFEPortraitData or with subclass (currently only FE6 does).
	 */
	public GBAFEPortraitData portraitDataWithData(byte[] data, long offset, int faceId);
	
	/**
	 * FE7 and 8 have some characters with multiple portraits, returns a List containing all the faceIds that belong together
	 */
	public List<Integer> getRelatedPortraits(Integer faceId);
	
	/**
	 * FE6 has some characters who have their names duplicated at multiple name indicies, if changed we should change all of them.
	 */
	public List<Integer> getRelatedNames(Integer nameIndex);
}