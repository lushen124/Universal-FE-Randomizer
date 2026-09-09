package ui.model;

public class WeaponOptions {

	public final MinMaxVarOption mightOptions;
	public final MinMaxVarOption hitOptions;
//	public final MinMaxVarOption critOptions;
	public final MinMaxVarOption weightOptions;
	public final MinMaxVarOption durabilityOptions;
	
	public final Boolean shouldAddEffects;
	public final Boolean noEffectIronWeapons;
	public final Boolean noEffectSteelWeapons;
	public final Boolean noEffectThrownWeapons;
	public final Boolean includeLaguzWeapons;
	public final int effectChance;
	public final WeaponEffectOptions effectsList;
	
	public WeaponOptions(MinMaxVarOption mightOptions, 
			MinMaxVarOption hitOptions, 
//			MinMaxVarOption critOptions,
			MinMaxVarOption weightOptions, 
			MinMaxVarOption durabilityOptions, 
			Boolean shouldAddEffects,
			int effectChance,
			WeaponEffectOptions effects,
			Boolean noIrons,
			Boolean noSteels,
			Boolean noThrown,
			Boolean includeLaguzWeapons) {
		super();
		
		this.mightOptions = mightOptions;
		this.hitOptions = hitOptions;
//		this.critOptions = critOptions;
		this.weightOptions = weightOptions;
		this.durabilityOptions = durabilityOptions;
		this.shouldAddEffects = shouldAddEffects;
		effectsList = effects;
		noEffectIronWeapons = noIrons;
		noEffectSteelWeapons = noSteels;
		noEffectThrownWeapons = noThrown;
		this.effectChance = effectChance;
		this.includeLaguzWeapons = includeLaguzWeapons;
	}
	
}
