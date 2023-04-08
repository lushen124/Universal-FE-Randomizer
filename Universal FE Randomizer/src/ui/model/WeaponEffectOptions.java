package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class WeaponEffectOptions implements RecordableOption {

	public final int statBoosts;
	public final int effectiveness;
	public final int unbreakable;
	public final int brave;
	public final int reverseTriangle;
	public final int extendedRange;
	public final int highCritical;
	public final MinMaxOption criticalRange;
	public final int magicDamage;
	public final int poison;
	public final int stealHP;
	public final int critImmune;
	public final int noCrit;
	public final int eclipse;
	public final int devil;
	
	public WeaponEffectOptions(int statBoosts, int effectiveness, int unbreakable, int brave,
			int reverseTriangle, int extendedRange, int highCritical, MinMaxOption critRange,
			int magicDamage, int poison, int stealHP, int critImmune, int noCrit,
			int eclipse, int devil) {
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
	
	public int getWeightTotal() {
		return statBoosts + effectiveness + unbreakable + brave + reverseTriangle + extendedRange + highCritical + magicDamage + poison + stealHP + critImmune + noCrit + eclipse + devil;
	}

	@Override
	public void record(RecordKeeper rk, GameType type) {
		// TODO Auto-generated method stub
		
	}
}
