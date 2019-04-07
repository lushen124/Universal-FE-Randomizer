package ui.model;

public class WeaponOptions {

	public final MinMaxVarOption mightOptions;
	public final MinMaxVarOption hitOptions;
//	public final MinMaxVarOption critOptions;
	public final MinMaxVarOption weightOptions;
	public final MinMaxVarOption durabilityOptions;
	
	public final Boolean shouldAddEffects;
	public final Boolean noEffectIronWeapons;
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
			Boolean noIrons) {
		super();
		
		this.mightOptions = mightOptions;
		this.hitOptions = hitOptions;
//		this.critOptions = critOptions;
		this.weightOptions = weightOptions;
		this.durabilityOptions = durabilityOptions;
		this.shouldAddEffects = shouldAddEffects;
		effectsList = effects;
		noEffectIronWeapons = noIrons;
		this.effectChance = effectChance;
	}
	
}
