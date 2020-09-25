package ui.model;

public class MiscellaneousOptions {
	
	public enum RewardMode {
		SIMILAR, RANDOM
	}
	
	public final Boolean applyEnglishPatch;
	
	public final Boolean randomizeRewards;
	public final Integer enemyDropChance;
	
	public final RewardMode rewardMode;
	
	public MiscellaneousOptions(Boolean randomRewards) {
		super();
		this.applyEnglishPatch = false;
		rewardMode = RewardMode.RANDOM;
		this.randomizeRewards = randomRewards;
		enemyDropChance = 0;
	}

	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		rewardMode = RewardMode.RANDOM;
		this.randomizeRewards = randomRewards;
		enemyDropChance = 0;
	}
	
	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards, RewardMode rewardMode, Integer enemyDropChance) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		this.rewardMode = rewardMode;
		this.randomizeRewards = randomRewards;
		this.enemyDropChance = enemyDropChance;
	}
}