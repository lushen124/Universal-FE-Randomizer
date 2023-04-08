package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class MiscellaneousOptions implements RecordableOption {

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
	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards,
			FollowupRequirement followupRequirement) {
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
	public MiscellaneousOptions(Boolean tripleEffectiveness, Boolean randomRewards, RewardMode rewardMode,
			Integer enemyDropChance) {
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

	@Override
	public void record(RecordKeeper rk, GameType type) {
		if (randomizeRewards) {
			rk.addHeaderItem("Randomize Rewards", "YES");
		} else {
			rk.addHeaderItem("Randomize Rewards", "NO");
		}

		if (singleRNMode) {
			rk.addHeaderItem("Enable Single RN", "YES");
		} else {
			rk.addHeaderItem("Enable Single RN", "NO");
		}

		if (randomizeFogOfWar) {
			rk.addHeaderItem("Randomize Fog of War", "YES - " + Integer.toString(fogOfWarChance) + "%");
			rk.addHeaderItem("Fog of War Vision Range", Integer.toString(fogOfWarVisionRange.minValue) + " ~ "
					+ Integer.toString(fogOfWarVisionRange.maxValue));
		} else {
			rk.addHeaderItem("Randomize Fog of War", "NO");
		}
		
		String expRateDesc = "";
		switch(experienceRate) {
		case NORMAL: expRateDesc = "Regular exp gain."; break;
		case PARAGON: expRateDesc = "Paragon Mode. Exp gain doubled."; break;
		case RENEGADE: expRateDesc = "Renegade Mode. Exp gain halved."; break;
		}
		
		rk.addHeaderItem("Experience Rate: ", expRateDesc);
		
		if (casualMode) {
			rk.addHeaderItem("Casual Mode", "YES");
		} else {
			rk.addHeaderItem("Casual Mode", "NO");
		}
	}
}