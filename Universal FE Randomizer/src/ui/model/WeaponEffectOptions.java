package ui.model;

public class WeaponEffectOptions {

	public final Boolean statBoosts;
	public final Boolean effectiveness;
	public final Boolean unbreakable;
	public final Boolean brave;
	public final Boolean reverseTriangle;
	public final Boolean extendedRange;
	public final Boolean highCritical;
	public final MinMaxOption criticalRange;
	public final Boolean magicDamage;
	public final Boolean poison;
	public final Boolean stealHP;
	public final Boolean critImmune;
	public final Boolean noCrit;
	public final Boolean eclipse;
	public final Boolean devil;
	
	public WeaponEffectOptions(Boolean statBoosts, Boolean effectiveness, Boolean unbreakable, Boolean brave,
			Boolean reverseTriangle, Boolean extendedRange, Boolean highCritical, MinMaxOption critRange,
			Boolean magicDamage, Boolean poison, Boolean stealHP, Boolean critImmune, Boolean noCrit,
			Boolean eclipse, Boolean devil) {
		super();
		this.statBoosts = statBoosts;
		this.effectiveness = effectiveness;
		this.unbreakable = unbreakable;
		this.brave = brave;
		this.reverseTriangle = reverseTriangle;
		this.extendedRange = extendedRange;
		this.highCritical = highCritical;
		this.criticalRange = critRange;
		this.magicDamage = magicDamage;
		this.poison = poison;
		this.stealHP = stealHP;
		this.critImmune = critImmune;
		this.noCrit = noCrit;
		this.eclipse = eclipse;
		this.devil = devil;
	}
}
