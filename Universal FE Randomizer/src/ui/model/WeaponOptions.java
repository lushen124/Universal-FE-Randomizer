package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class WeaponOptions implements RecordableOption {

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

	public WeaponOptions(MinMaxVarOption mightOptions, MinMaxVarOption hitOptions,
//			MinMaxVarOption critOptions,
			MinMaxVarOption weightOptions, MinMaxVarOption durabilityOptions, Boolean shouldAddEffects,
			int effectChance, WeaponEffectOptions effects, Boolean noIrons, Boolean noSteels, Boolean noThrown,
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

	@Override
	public void record(RecordKeeper rk, GameType type) {
		if (mightOptions != null) {
			rk.addHeaderItem("Randomize Weapon Power", "+/- " + mightOptions.variance + ", (" + mightOptions.minValue
					+ " ~ " + mightOptions.maxValue + ")");
		} else {
			rk.addHeaderItem("Randomize Weapon Power", "NO");
		}
		if (hitOptions != null) {
			rk.addHeaderItem("Randomize Weapon Accuracy",
					"+/- " + hitOptions.variance + ", (" + hitOptions.minValue + " ~ " + hitOptions.maxValue + ")");
		} else {
			rk.addHeaderItem("Randomize Weapon Accuracy", "NO");
		}
		if (weightOptions != null) {
			rk.addHeaderItem("Randomize Weapon Weight", "+/- " + weightOptions.variance + ", (" + weightOptions.minValue
					+ " ~ " + weightOptions.maxValue + ")");
		} else {
			rk.addHeaderItem("Randomize Weapon Weight", "NO");
		}
		if (durabilityOptions != null) {
			rk.addHeaderItem("Randomize Weapon Durability", "+/- " + durabilityOptions.variance + ", ("
					+ durabilityOptions.minValue + " ~ " + durabilityOptions.maxValue + ")");
		} else {
			rk.addHeaderItem("Randomize Weapon Durability", "NO");
		}
		if (shouldAddEffects) {
			rk.addHeaderItem("Add Random Effects", "YES (" + effectChance + "%)");
			StringBuilder sb = new StringBuilder();
			sb.append("<ul>\n");
			if (effectsList.statBoosts > 0) {
				sb.append("<li>Stat Boosts"
						+ String.format(" (%.2f%%)",
								(double) effectsList.statBoosts / (double) effectsList.getWeightTotal() * 100)
						+ "</li>\n");
			}
			if (effectsList.effectiveness > 0) {
				sb.append("<li>Effectiveness"
						+ String.format(" (%.2f%%)",
								(double) effectsList.effectiveness / (double) effectsList.getWeightTotal() * 100)
						+ "</li>\n");
			}
			if (effectsList.unbreakable > 0) {
				sb.append("<li>Unbreakable"
						+ String.format(" (%.2f%%)",
								(double) effectsList.unbreakable / (double) effectsList.getWeightTotal() * 100)
						+ "</li>\n");
			}
			if (effectsList.brave > 0) {
				sb.append(
						"<li>Brave"
								+ String.format(" (%.2f%%)",
										(double) effectsList.brave / (double) effectsList.getWeightTotal() * 100)
								+ "</li>\n");
			}
			if (effectsList.reverseTriangle > 0) {
				sb.append("<li>Reverse Triangle"
						+ String.format(" (%.2f%%)",
								(double) effectsList.reverseTriangle / (double) effectsList.getWeightTotal() * 100)
						+ "</li>\n");
			}
			if (effectsList.extendedRange > 0) {
				sb.append("<li>Extended Range"
						+ String.format(" (%.2f%%)",
								(double) effectsList.extendedRange / (double) effectsList.getWeightTotal() * 100)
						+ "</li>\n");
			}
			if (effectsList.highCritical > 0) {
				sb.append("<li>Critical");
				sb.append(
						" (" + effectsList.criticalRange.minValue + "% ~ " + effectsList.criticalRange.maxValue + "%)");
				sb.append(String.format(" (%.2f%%)",
						(double) effectsList.highCritical / (double) effectsList.getWeightTotal() * 100));
				sb.append("</li>\n");
			}
			if (effectsList.magicDamage > 0) {
				sb.append("<li>Magic Damage"
						+ String.format(" (%.2f%%)",
								(double) effectsList.magicDamage / (double) effectsList.getWeightTotal() * 100)
						+ "</li>\n");
			}
			if (effectsList.poison > 0) {
				sb.append(
						"<li>Poison"
								+ String.format(" (%.2f%%)",
										(double) effectsList.poison / (double) effectsList.getWeightTotal() * 100)
								+ "</li>\n");
			}
			if (effectsList.eclipse > 0) {
				sb.append("<li>Eclipse"
						+ String.format(" (%.2f%%)",
								(double) effectsList.eclipse / (double) effectsList.getWeightTotal() * 100)
						+ "</li>\n");
			}
			if (effectsList.devil > 0) {
				sb.append(
						"<li>Devil"
								+ String.format(" (%.2f%%)",
										(double) effectsList.devil / (double) effectsList.getWeightTotal() * 100)
								+ "</li>\n");
			}
			sb.append("</ul>\n");
			rk.addHeaderItem("Random Effects Allowed", sb.toString());
			if (noEffectIronWeapons) {
				rk.addHeaderItem("Safe Basic Weapons", "YES");
			} else {
				rk.addHeaderItem("Safe Basic Weapons", "NO");
			}
			if (noEffectSteelWeapons) {
				rk.addHeaderItem("Safe Steel Weapons", "YES");
			} else {
				rk.addHeaderItem("Safe Steel Weapons", "NO");
			}
			if (noEffectThrownWeapons) {
				rk.addHeaderItem("Safe Basic Thrown Weapons", "YES");
			} else {
				rk.addHeaderItem("Safe Basic Thrown Weapons", "NO");
			}
		} else {
			rk.addHeaderItem("Add Random Effects", "NO");
		}
	}

}
