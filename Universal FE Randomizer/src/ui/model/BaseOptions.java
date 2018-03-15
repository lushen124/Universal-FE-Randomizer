package ui.model;

public class BaseOptions {
	
	public enum Mode {
		REDISTRIBUTE, DELTA
	}
	
	public final Mode mode;
	
	public final VarOption redistributionOption;
	public final VarOption deltaOption;
	
	public BaseOptions(Mode mode, VarOption redistributionOption, VarOption deltaOption) {
		super();
		this.mode = mode;
		this.redistributionOption = redistributionOption;
		this.deltaOption = deltaOption;
	}
}
