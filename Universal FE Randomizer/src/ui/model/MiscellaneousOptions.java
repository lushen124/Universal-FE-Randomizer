package ui.model;

public class MiscellaneousOptions {
	
	public final Boolean applyEnglishPatch;
	
	public final Boolean randomizeRewards;
	
	public MiscellaneousOptions(Boolean randomRewards) {
		super();
		this.applyEnglishPatch = false;
		this.randomizeRewards = randomRewards;
	}

	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		this.randomizeRewards = randomRewards;
	}
}