package ui.fe9;

public class FE9SkillsOptions {
	
	public enum Mode {
		RANDOMIZE_EXISTING, FULL_RANDOM
	}
	
	public final Mode mode;
	
	public final int skillChance;
	public final SkillWeightOptions skillWeights;
	
	public FE9SkillsOptions(Mode mode, int skillChance, SkillWeightOptions skillWeights) {
		this.mode = mode;
		this.skillChance = skillChance;
		this.skillWeights = skillWeights;
	}

}
