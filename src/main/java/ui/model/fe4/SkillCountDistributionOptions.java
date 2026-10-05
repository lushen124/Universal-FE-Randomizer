package ui.model.fe4;

import ui.model.WeightedOptions;

public class SkillCountDistributionOptions {
	public final WeightedOptions zeroSkillsChance;
	public final WeightedOptions oneSkillChance;
	public final WeightedOptions twoSkillChance;
	public final WeightedOptions threeSkillChance;
	
	public SkillCountDistributionOptions(WeightedOptions zero, WeightedOptions one, WeightedOptions two, WeightedOptions three) {
		super();
		this.zeroSkillsChance = zero;
		this.oneSkillChance = one;
		this.twoSkillChance = two;
		this.threeSkillChance = three;
	}
}
