package ui.model;

public class RecruitmentOptions {
	public enum StatAdjustmentMode {
		AUTOLEVEL, MATCH_SLOT, RELATIVE_TO_SLOT
	}
	
	public final StatAdjustmentMode statMode;
	
	public RecruitmentOptions(StatAdjustmentMode statMode) {
		super();
		this.statMode = statMode;
	}
}
