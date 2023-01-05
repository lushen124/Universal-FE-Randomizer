package random.gba.randomizer.shuffling;

import java.util.List;

import random.gba.randomizer.shuffling.data.GBAFEPortraitData;

public interface GBAFEPortraitProvider {
	public long portraitDataTablePointer();

	public int numberOfPortraits();

	public int bytesPerPortraitEntry();
	
	public GBAFEPortraitData portraitDataWithData(byte[] data, long offset, int faceId);
	public List<Integer> getRelatedPortraits(Integer faceId);
}