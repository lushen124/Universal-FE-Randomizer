package ui.model;

public class GrowthOptions {
	
	public enum Mode {
		REDISTRIBUTE, DELTA, FULL
	}
	
	public final Mode mode;
	
	public final VarOption redistributionOption;
	public final VarOption deltaOption;
	public final MinMaxOption fullOption;
	public final boolean adjustSTRMAGSplit;
	public final boolean adjustHP;
	
	public GrowthOptions(Mode mode, VarOption redistributionOption, VarOption deltaOption, MinMaxOption fullOption, boolean adjustHP, boolean adjustSTRMAGSplit) {
		super();
		this.mode = mode;
		this.redistributionOption = redistributionOption;
		this.deltaOption = deltaOption;
		this.fullOption = fullOption;
		this.adjustSTRMAGSplit = adjustSTRMAGSplit;
		this.adjustHP = adjustHP;
	}
}
