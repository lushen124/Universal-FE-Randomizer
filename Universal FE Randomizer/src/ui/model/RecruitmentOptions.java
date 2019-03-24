package ui.model;

public class RecruitmentOptions {
	public enum GrowthAdjustmentMode {
		USE_FILL, USE_SLOT, RELATIVE_TO_SLOT
	}
	public enum StatAdjustmentMode {
		AUTOLEVEL, MATCH_SLOT, RELATIVE_TO_SLOT
	}
	public enum BaseStatAutolevelType {
		USE_ORIGINAL, USE_NEW
	}
	
	public final GrowthAdjustmentMode growthMode;
	public final StatAdjustmentMode baseMode;
	public final BaseStatAutolevelType autolevelMode;
	
	public RecruitmentOptions(GrowthAdjustmentMode growthMode, StatAdjustmentMode baseMode, BaseStatAutolevelType autolevel) {
		super();
		this.growthMode = growthMode;
		this.baseMode = baseMode;
		this.autolevelMode = autolevel;
	}
}
