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
	public final boolean createPrfs;
	public final boolean unbreakablePrfs;
	public final boolean includeThieves;
	public final boolean includeSpecial;
	
	public final boolean includeExtras;
	public List<String> gamesToShuffle;
	
	public RecruitmentOptions(GrowthAdjustmentMode growthMode, StatAdjustmentMode baseMode, BaseStatAutolevelType autolevel, ClassMode classMode, boolean lords, boolean prfs, boolean unbreakablePrfs, boolean thieves, boolean special, boolean crossGender, boolean includeExtras) {
		super();
		this.growthMode = growthMode;
		this.baseMode = baseMode;
		this.autolevelMode = autolevel;
		this.classMode = classMode;
		
		this.includeLords = lords;
		createPrfs = prfs;
		this.unbreakablePrfs = unbreakablePrfs;
		this.includeThieves = thieves;
		this.includeSpecial = special;
		
		this.allowCrossGender = crossGender;
		this.includeExtras = includeExtras;
	}
}
