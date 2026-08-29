package ui.model;

public class GrowthOptions {
	
	public enum Mode {
		REDISTRIBUTE, DELTA, FULL, SMART
	}
	
	public final Mode mode;
	
	public final MinMaxVarOption redistributionOption;
	public final MinMaxVarOption deltaOption;
	public final MinMaxOption fullOption;
	public final boolean adjustSTRMAGSplit;
	public final boolean adjustHP;
	
	public GrowthOptions(Mode mode, MinMaxVarOption redistributionOption, MinMaxVarOption deltaOption, MinMaxOption fullOption, boolean adjustHP, boolean adjustSTRMAGSplit) {
		super();
		this.mode = mode;
		this.redistributionOption = redistributionOption;
		this.deltaOption = deltaOption;
		this.fullOption = fullOption;
		this.adjustSTRMAGSplit = adjustSTRMAGSplit;
		this.adjustHP = adjustHP;
	}
}
