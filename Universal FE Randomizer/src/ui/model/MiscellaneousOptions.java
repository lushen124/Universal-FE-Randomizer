package ui.model;

public class MiscellaneousOptions {
	
	public final Boolean applyEnglishPatch;
	
	public final Boolean randomizeRewards;
	public final Boolean randomizeRecruitment;
	
	public MiscellaneousOptions(Boolean randomRewards, Boolean randomRecruitment) {
		super();
		this.applyEnglishPatch = false;
		this.randomizeRewards = randomRewards;
		this.randomizeRecruitment = randomRecruitment;
	}

	public MiscellaneousOptions(Boolean applyEnglishPatch, Boolean randomRewards, Boolean randomRecruitment) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		this.randomizeRewards = randomRewards;
		this.randomizeRecruitment = randomRecruitment;
	}
}