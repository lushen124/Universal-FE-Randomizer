package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class OtherCharacterOptions implements RecordableOption {

	public final MinMaxOption movementOptions;
	public final MinVarOption constitutionOptions;

	public final Boolean randomizeAffinity;

	public OtherCharacterOptions(MinMaxOption movementOptions, MinVarOption constitutionOptions,
			Boolean randomizeAffinity) {
		super();
		this.movementOptions = movementOptions;
		this.constitutionOptions = constitutionOptions;
		this.randomizeAffinity = randomizeAffinity;
	}

	@Override
	public void record(RecordKeeper rk, GameType type) {
		if (constitutionOptions != null) {
			rk.addHeaderItem("Randomize Constitution",
					"+/- " + constitutionOptions.variance + ", Min: " + constitutionOptions.minValue);
		} else {
			rk.addHeaderItem("Randomize Constitution", "NO");
		}

		if (movementOptions != null) {
			rk.addHeaderItem("Randomize Movement Ranges",
					"" + movementOptions.minValue + " ~ " + movementOptions.maxValue);
		} else {
			rk.addHeaderItem("Randomize Movement Ranges", "NO");
		}

		if (randomizeAffinity) {
			rk.addHeaderItem("Randomize Affinity", "YES");
		} else {
			rk.addHeaderItem("Randomize Affinity", "NO");
		}
	}
}
