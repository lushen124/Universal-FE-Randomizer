package ui.model;

import java.util.List;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class RecruitmentOptions implements RecordableOption {
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

	@Override
	public void record(RecordKeeper rk, GameType type) {
		StringBuilder sb = new StringBuilder();

		if (allowCrossGender) {
			sb.append("Allow Cross-Gender<br>");
		}

		switch (classMode) {
		case USE_FILL:
			sb.append("Use Fill Class<br>");
			break;
		case USE_SLOT:
			sb.append("Use Slot Class<br>");
			break;
		}

		switch (growthMode) {
		case USE_FILL:
			sb.append("Use Fill Growths<br>");
			break;
		case USE_SLOT:
			sb.append("Use Slot Growths<br>");
			break;
		case RELATIVE_TO_SLOT:
			sb.append("Use Slot Relative Growths<br>");
			break;
		}

		switch (baseMode) {
		case AUTOLEVEL:
			sb.append("Autolevel Base Stats<br>");
			switch (autolevelMode) {
			case USE_ORIGINAL:
				sb.append("Autolevel w/ Original Growths");
				break;
			case USE_NEW:
				sb.append("Autolevel w/ New Growths");
				break;
			}
			break;
		case RELATIVE_TO_SLOT:
			sb.append("Relative Base Stats");
			break;
		case MATCH_SLOT:
			sb.append("Match Base Stats");
			break;
		}

		if (includeLords) {
			sb.append("<br>Include Lords");
		}
		if (includeThieves) {
			sb.append("<br>Include Thieves");
		}
		if (includeSpecial) {
			sb.append("<br>Include Special Characters");
		}

		rk.addHeaderItem("Randomize Recruitment", sb.toString());
	}
}
