package ui.model.fe4;

import ui.model.SkillWeightOptions;

public class SkillsOptions {
	
	public enum Mode {
		SHUFFLE, RANDOMIZE
	}
	
	public final Mode mode;
	
	public final boolean retainNumberOfSkills;
	
	// For Shuffle mode only.
	public final boolean separatePoolsByGeneration;
	
	// For Randomize mode only.
	public final SkillCountDistributionOptions skillCounts;
	
	// Skill weights
	public final SkillWeightOptions skillWeights;
	
	public SkillsOptions(Mode mode, boolean retainNumberOfSkills, boolean separatePoolsByGeneration, SkillCountDistributionOptions skillCounts, SkillWeightOptions skillWeights) {
		super();
		this.mode = mode;
		this.retainNumberOfSkills = retainNumberOfSkills;
		this.separatePoolsByGeneration = separatePoolsByGeneration;
		this.skillCounts = skillCounts;
		this.skillWeights = skillWeights;
	}
}
