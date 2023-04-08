package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class BaseOptions implements RecordableOption {

	public enum Mode {
		REDISTRIBUTE, DELTA
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

	public void record(RecordKeeper rk, GameType type) {
		switch (mode) {
		case REDISTRIBUTE:
			rk.addHeaderItem("Randomize Bases", "Redistribution (" + redistributionOption.variance + " variance)");
			break;
		case DELTA:
			rk.addHeaderItem("Randomize Bases", "Delta (+/- " + deltaOption.variance + ")");
			break;
		}
	}
}
