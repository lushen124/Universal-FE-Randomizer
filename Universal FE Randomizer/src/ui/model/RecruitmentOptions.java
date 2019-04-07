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
	
	public final boolean allowCrossGender;
	
	public final boolean includeExtras;
	
	public RecruitmentOptions(GrowthAdjustmentMode growthMode, StatAdjustmentMode baseMode, BaseStatAutolevelType autolevel, boolean crossGender, boolean includeExtras) {
		super();
		this.growthMode = growthMode;
		this.baseMode = baseMode;
		this.autolevelMode = autolevel;
		
		this.allowCrossGender = crossGender;
		this.includeExtras = includeExtras;
	}
}
