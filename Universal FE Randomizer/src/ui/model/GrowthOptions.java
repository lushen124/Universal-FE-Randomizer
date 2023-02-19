package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class GrowthOptions implements RecordableOption {

	public enum Mode {
		REDISTRIBUTE, DELTA, FULL
	}

	public final Mode mode;

	public final MinMaxVarOption redistributionOption;
	public final MinMaxVarOption deltaOption;
	public final MinMaxOption fullOption;
	public final boolean adjustSTRMAGSplit;
	public final boolean adjustHP;

	public GrowthOptions(Mode mode, MinMaxVarOption redistributionOption, MinMaxVarOption deltaOption,
			MinMaxOption fullOption, boolean adjustHP, boolean adjustSTRMAGSplit) {
		super();
		this.mode = mode;
		this.redistributionOption = redistributionOption;
		this.deltaOption = deltaOption;
		this.fullOption = fullOption;
		this.adjustSTRMAGSplit = adjustSTRMAGSplit;
		this.adjustHP = adjustHP;
	}

	public void record(RecordKeeper rk, GameType type) {
		switch (mode) {
		case REDISTRIBUTE:
			rk.addHeaderItem("Randomize Growths", "Redistribution (" + redistributionOption.variance + "% variance)");
			break;
		case DELTA:
			rk.addHeaderItem("Randomize Growths", "Delta (+/- " + deltaOption.variance + "%)");
			break;
		case FULL:
			rk.addHeaderItem("Randomize Growths", "Full (" + fullOption.minValue + "% ~ " + fullOption.maxValue + "%)");
			break;
		}

		rk.addHeaderItem("Adjust HP Growths", adjustHP ? "YES" : "NO");
	}
}
