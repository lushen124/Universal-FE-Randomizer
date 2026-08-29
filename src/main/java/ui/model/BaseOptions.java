package ui.model;

public class BaseOptions {
	
	public enum Mode {
		REDISTRIBUTE, DELTA, SMART
	}
	
	public final Mode mode;
	
	public final VarOption redistributionOption;
	public final VarOption deltaOption;
	
	public final boolean adjustSTRMAGByClass;
	
	public BaseOptions(Mode mode, VarOption redistributionOption, VarOption deltaOption, boolean adjustSTRMAGByClass) {
		super();
		this.mode = mode;
		this.redistributionOption = redistributionOption;
		this.deltaOption = deltaOption;
		this.adjustSTRMAGByClass = adjustSTRMAGByClass;
	}
}
