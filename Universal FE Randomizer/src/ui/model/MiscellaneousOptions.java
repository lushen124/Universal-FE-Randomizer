package ui.model;

public class MiscellaneousOptions {
	
	public enum RewardMode {
		SIMILAR, RANDOM
	}
	
	public enum ExperienceRate {
		NORMAL, PARAGON, RENEGADE
	}
	
	public final Boolean applyEnglishPatch;
	
	public final Boolean tripleEffectiveness;
	
	public final Boolean randomizeRewards;
	public final Integer enemyDropChance;
	public final Boolean singleRNMode;
	
	public final Boolean randomizeFogOfWar;
	public final Integer fogOfWarChance;
	public final MinMaxOption fogOfWarVisionRange;
	
	public final RewardMode rewardMode;
	
	public final ExperienceRate experienceRate;
	public final Boolean casualMode;
	
	public static class FollowupRequirement {
		public final Boolean requiresPursuit;
		public final int thresholdWithPursuit;
		public final int thresholdWithoutPursuit;
		
		public FollowupRequirement(boolean requiresPursuit, int withPursuit, int withoutPursuit) {
			this.requiresPursuit = requiresPursuit;
			thresholdWithPursuit = withPursuit;
			thresholdWithoutPursuit = withoutPursuit;
		}
	}
	
	public final FollowupRequirement followupRequirement;
	
	// FE7, FE8
	public MiscellaneousOptions(Boolean randomRewards, int enemyDropChance, Boolean tripleEffectiveness, Boolean singleRN, Boolean fogOfWar, int fogOfWarChance, MinMaxOption visionRange, Boolean casualMode, ExperienceRate experienceRate) {
		super();
		this.applyEnglishPatch = false;
		this.tripleEffectiveness = tripleEffectiveness;
		rewardMode = RewardMode.RANDOM;
		this.randomizeRewards = randomRewards;
		this.enemyDropChance = enemyDropChance;
		followupRequirement = null;
		singleRNMode = singleRN;
		randomizeFogOfWar = fogOfWar;
		this.fogOfWarChance = fogOfWarChance;
		fogOfWarVisionRange = visionRange;
		this.casualMode = casualMode;
		this.experienceRate = experienceRate;
	}

	// FE4
	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards, FollowupRequirement followupRequirement) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		this.randomizeRewards = randomRewards;
		rewardMode = RewardMode.RANDOM;
		this.followupRequirement = followupRequirement;
		this.tripleEffectiveness = false;
		this.enemyDropChance = 0;
		singleRNMode = false;
		randomizeFogOfWar = false;
		fogOfWarVisionRange = null;
		fogOfWarChance = 0;
		casualMode = false;
		experienceRate = ExperienceRate.NORMAL;
	}
	
	// FE6
	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards, Boolean tripleEffectiveness, Boolean singleRN, Boolean fogOfWar, int fogOfWarChance, MinMaxOption visionRange, Boolean casualMode, ExperienceRate experienceRate) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		this.tripleEffectiveness = tripleEffectiveness;
		rewardMode = RewardMode.RANDOM;
		this.randomizeRewards = randomRewards;
		enemyDropChance = 0;
		followupRequirement = null;
		singleRNMode = singleRN;
		randomizeFogOfWar = fogOfWar;
		fogOfWarVisionRange = visionRange;
		this.fogOfWarChance = fogOfWarChance;
		this.casualMode = casualMode;
		this.experienceRate = experienceRate;
	}
	
	// FE9
	public MiscellaneousOptions(Boolean tripleEffectiveness, Boolean randomRewards, RewardMode rewardMode, Integer enemyDropChance) {
		super();
		this.applyEnglishPatch = false;
		this.tripleEffectiveness = tripleEffectiveness;
		this.rewardMode = rewardMode;
		this.randomizeRewards = randomRewards;
		this.enemyDropChance = enemyDropChance;
		followupRequirement = null;
		singleRNMode = false;
		randomizeFogOfWar = false;
		fogOfWarVisionRange = null;
		fogOfWarChance = 0;
		casualMode = false;
		experienceRate = ExperienceRate.NORMAL;
	}
}