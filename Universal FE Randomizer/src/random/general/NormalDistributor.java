package random.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import random.general.NormalDistributor.Bucket;

// Extreme quotation marks around the "Normal" part, since we're
// not going to be exact, but this is just a way to generate a random
// number that we can qualify as good or bad, extreme or average.
public class NormalDistributor {
	
	public static List<Bucket> topBuckets = Arrays.asList(Bucket.EXCELLENT, Bucket.GOOD, Bucket.AVERAGE);
	public static List<Bucket> bottomBuckets = Arrays.asList(Bucket.ABYSMAL, Bucket.BAD, Bucket.AVERAGE, Bucket.GOOD);
	public static List<Bucket> allBuckets = Arrays.asList(Bucket.EXCELLENT, Bucket.GOOD, Bucket.AVERAGE, Bucket.BAD, Bucket.ABYSMAL);
	public static List<Bucket> notAbysmal = Arrays.asList(Bucket.EXCELLENT, Bucket.GOOD, Bucket.AVERAGE, Bucket.BAD);
	
	public enum Bucket {
		ABYSMAL, // Bottom 5% (1 / 20)
		BAD, // 5% - 35% (6 / 20)
		AVERAGE, // 35% - 65% (6 / 20) (+ whatever remainder didn't sort evenly into the other buckets)
		GOOD, // 65 - 95% (6 / 20)
		EXCELLENT // Top 5% (1 / 20)
	}
	
	List<Integer> abysmalValues = new ArrayList<Integer>();
	List<Integer> badValues = new ArrayList<Integer>();
	List<Integer> averageValues = new ArrayList<Integer>();
	List<Integer> goodValues = new ArrayList<Integer>();
	List<Integer> excellentValues = new ArrayList<Integer>();

	// Bounds are inclusive.
	public NormalDistributor(int lowerBound, int upperBound, int steps) {
		List<Integer> allValues = new ArrayList<Integer>();
		for (int i = lowerBound; i <= upperBound; i += steps) {
			allValues.add(i);
		}
		
		int size = allValues.size();
		if (size == 0) {
			assert false; // This is not allowed.
		}
		if (size < 5) {
			// we'll have to stretch the definition on one end.
			// start assigning from excellent and extend downwards.
			excellentValues.add(lastItem(allValues));
			dropLastButDontEmpty(allValues);
			goodValues.add(lastItem(allValues));
			dropLastButDontEmpty(allValues);
			averageValues.add(lastItem(allValues));
			dropLastButDontEmpty(allValues);
			badValues.add(lastItem(allValues));
			dropLastButDontEmpty(allValues);
			abysmalValues.add(lastItem(allValues));
		}
		else {
			float itemsPer5 = (float)size / 20f;
			int itemsPerBucket = (int)Math.floor(itemsPer5 * 6);
			int usedFrontIndex = 0;
			int usedBackIndex = allValues.size();
			int extremeCount = itemsPer5 < 1 ? 1 : (int)Math.floor(itemsPer5);
			abysmalValues.addAll(allValues.subList(usedFrontIndex, extremeCount));
			excellentValues.addAll(allValues.subList(usedBackIndex - extremeCount, usedBackIndex));
			usedFrontIndex += extremeCount;
			usedBackIndex -= extremeCount;
			badValues.addAll(allValues.subList(usedFrontIndex, usedFrontIndex + itemsPerBucket));
			goodValues.addAll(allValues.subList(usedBackIndex - itemsPerBucket, usedBackIndex));
			usedFrontIndex += itemsPerBucket;
			usedBackIndex -= itemsPerBucket;
			// Everything else goes into the average bucket.
			averageValues.addAll(allValues.subList(usedFrontIndex, usedBackIndex));
		}
	}
	
	public int getRandomValue(Random rng, List<Bucket> allowedBuckets) {
		if (allowedBuckets.isEmpty()) { return 0; }
		
		while (true) {
			int randomNum = rng.nextInt(20); // 0 ~ 19
			if (randomNum == 0 && allowedBuckets.contains(Bucket.ABYSMAL)) {
				return getRandomValue(Bucket.ABYSMAL, rng);
			} else if (randomNum <= 6 && allowedBuckets.contains(Bucket.BAD)) {
				return getRandomValue(Bucket.BAD, rng);
			} else if (randomNum <= 12 && allowedBuckets.contains(Bucket.AVERAGE)) {
				return getRandomValue(Bucket.AVERAGE, rng);
			} else if (randomNum <= 18 && allowedBuckets.contains(Bucket.GOOD)) {
				return getRandomValue(Bucket.GOOD, rng);
			} else if (allowedBuckets.contains(Bucket.EXCELLENT)) {
				return getRandomValue(Bucket.EXCELLENT, rng);
			}
		}
	}
	
	public int getRandomValue(Bucket bucket, Random rng) {
		switch (bucket) {
		case ABYSMAL: return abysmalValues.get(rng.nextInt(abysmalValues.size()));
		case BAD: return badValues.get(rng.nextInt(badValues.size()));
		case AVERAGE: return averageValues.get(rng.nextInt(averageValues.size()));
		case GOOD: return goodValues.get(rng.nextInt(goodValues.size()));
		default: return excellentValues.get(rng.nextInt(excellentValues.size()));
		}
	}
	
	public Bucket getBucketForValue(int value) {
		if (abysmalValues.contains(value)) { return Bucket.ABYSMAL; }
		else if (badValues.contains(value)) { return Bucket.BAD; }
		else if (averageValues.contains(value)) { return Bucket.AVERAGE; }
		else if (goodValues.contains(value)) { return Bucket.GOOD; }
		else if (excellentValues.contains(value)) { return Bucket.EXCELLENT; }
		else { return null; }
	}
	
	private Integer lastItem(List<Integer> list) {
		return list.get(list.size() - 1);
	}
	
	private void dropLastButDontEmpty(List<Integer> list) {
		if (list.size() > 1) {
			list.remove(list.size() - 1);
		}
	}
}
