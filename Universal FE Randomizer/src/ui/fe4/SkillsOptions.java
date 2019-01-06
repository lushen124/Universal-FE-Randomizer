package ui.fe4;

public class SkillsOptions {
	
	public enum Mode {
		SHUFFLE, RANDOMIZE
	}
	
	public final Mode mode;
	
	public final boolean retainNumberOfSkills;
	
	// For Randomize mode only.
	public final SkillCountDistributionOptions skillCounts;
	
	// Skill weights
	public final SkillWeightOptions skillWeights;
	
	public SkillsOptions(Mode mode, boolean retainNumberOfSkills, SkillCountDistributionOptions skillCounts, SkillWeightOptions skillWeights) {
		super();
		this.mode = mode;
		this.retainNumberOfSkills = retainNumberOfSkills;
		this.skillCounts = skillCounts;
		this.skillWeights = skillWeights;
	}
}
