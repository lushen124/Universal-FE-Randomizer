package ui.model;

public class MiscellaneousOptions {
	
	public enum RewardMode {
		SIMILAR, RANDOM
	}
	
	public final Boolean applyEnglishPatch;
	
	public final Boolean randomizeRewards;
	
	public final RewardMode rewardMode;
	
	public MiscellaneousOptions(Boolean randomRewards) {
		super();
		this.applyEnglishPatch = false;
		rewardMode = RewardMode.RANDOM;
		this.randomizeRewards = randomRewards;
	}

	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		rewardMode = RewardMode.RANDOM;
		this.randomizeRewards = randomRewards;
	}
	
	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards, RewardMode rewardMode) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		this.rewardMode = rewardMode;
		this.randomizeRewards = randomRewards;
	}
}