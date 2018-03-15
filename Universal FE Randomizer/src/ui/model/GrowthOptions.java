package ui.model;

public class GrowthOptions {
	
	public enum Mode {
		REDISTRIBUTE, DELTA, FULL
	}
	
	public final Mode mode;
	
	public final VarOption redistributionOption;
	public final VarOption deltaOption;
	public final MinMaxOption fullOption;
	
	public GrowthOptions(Mode mode, VarOption redistributionOption, VarOption deltaOption, MinMaxOption fullOption) {
		super();
		this.mode = mode;
		this.redistributionOption = redistributionOption;
		this.deltaOption = deltaOption;
		this.fullOption = fullOption;
	}
}
