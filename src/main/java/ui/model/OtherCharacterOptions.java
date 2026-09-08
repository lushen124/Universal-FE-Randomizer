package ui.model;

public class OtherCharacterOptions {
	
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
}
