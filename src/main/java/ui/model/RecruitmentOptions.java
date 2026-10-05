package ui.model;

import java.util.List;

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
	
	public enum ClassMode {
		USE_FILL, USE_SLOT
	}
	
	public final GrowthAdjustmentMode growthMode;
	public final StatAdjustmentMode baseMode;
	public final BaseStatAutolevelType autolevelMode;
	public final ClassMode classMode;
	
	public final boolean allowCrossGender;
	public final boolean includeLords;
	public final boolean includeThieves;
	public final boolean includeSpecial;
	
	public final boolean includeExtras;

	public RecruitmentOptions(GrowthAdjustmentMode growthMode, StatAdjustmentMode baseMode, BaseStatAutolevelType autolevel, ClassMode classMode, boolean lords, boolean thieves, boolean special, boolean crossGender, boolean includeExtras) {
		this.growthMode = growthMode;
		this.baseMode = baseMode;
		this.autolevelMode = autolevel;
		this.classMode = classMode;
		
		this.includeLords = lords;
		this.includeThieves = thieves;
		this.includeSpecial = special;
		
		this.allowCrossGender = crossGender;
		this.includeExtras = includeExtras;
	}
}
