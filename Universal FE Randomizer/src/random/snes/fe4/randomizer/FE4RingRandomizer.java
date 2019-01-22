package random.snes.fe4.randomizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fedata.snes.fe4.FE4Data;
import random.snes.fe4.loader.ItemMapper;

public class FE4RingRandomizer {
	
	static final int rngSalt = 8008;
	
	public static void randomizeRings(ItemMapper itemMap, Random rng) {
		Set<FE4Data.Item> ringSet = new HashSet<FE4Data.Item>(FE4Data.Item.rings);
		ringSet.removeAll(FE4Data.Item.blacklistedRings);
		List<FE4Data.Item> ringList = new ArrayList<FE4Data.Item>(ringSet); 
		for (int index : itemMap.allIndices()) {
			FE4Data.Item item = itemMap.getItemAtIndex(index);
			if (item.isRing()) {
				FE4Data.Item newRing = ringList.get(rng.nextInt(ringList.size()));
				itemMap.setItemAtIndex(index, newRing);
			}
		}
	}

}
