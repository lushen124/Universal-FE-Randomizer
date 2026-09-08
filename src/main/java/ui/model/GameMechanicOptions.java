package ui.model;

public class GameMechanicOptions {
	

	public enum ExperienceRate {
		NORMAL, PARAGON, RENEGADE
	}
	
	public final Boolean applyEnglishPatch;
	
	public final Boolean tripleEffectiveness;
	
	public final Boolean singleRNMode;
	
	public final Boolean randomizeFogOfWar;
	public final Integer fogOfWarChance;
	public final MinMaxOption fogOfWarVisionRange;
	

	public final ExperienceRate experienceRate;
	public final Boolean casualMode;
	
	public static class FollowupRequirement {
		public final Boolean requiresPursuit;
		public final int thresholdWithPursuit;
		public final int thresholdWithoutPursuit;
		
		public FollowupRequirement(boolean requiresPursuit, int withPursuit, int withoutPursuit) {
			this.requiresPursuit = requiresPursuit;
			thresholdWithPursuit = withPursuit;
			thresholdWithoutPursuit = withoutPursuit;
		}
	}
	
	public final FollowupRequirement followupRequirement;
	
	// FE7, FE8
	public GameMechanicOptions(Boolean tripleEffectiveness, Boolean singleRN, Boolean fogOfWar, int fogOfWarChance, MinMaxOption visionRange, Boolean casualMode, ExperienceRate experienceRate) {
		super();
		this.applyEnglishPatch = false;
		this.tripleEffectiveness = tripleEffectiveness;
		followupRequirement = null;
		singleRNMode = singleRN;
		randomizeFogOfWar = fogOfWar;
		this.fogOfWarChance = fogOfWarChance;
		fogOfWarVisionRange = visionRange;
		this.casualMode = casualMode;
		this.experienceRate = experienceRate;
	}

	// FE4
	public GameMechanicOptions(Boolean applyEnglishPatch, FollowupRequirement followupRequirement) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		this.followupRequirement = followupRequirement;
		this.tripleEffectiveness = false;
		singleRNMode = false;
		randomizeFogOfWar = false;
		fogOfWarVisionRange = null;
		fogOfWarChance = 0;
		casualMode = false;
		experienceRate = ExperienceRate.NORMAL;
	}
	
	// FE6
	public GameMechanicOptions(Boolean applyEnglishPatch, Boolean tripleEffectiveness, Boolean singleRN, Boolean fogOfWar, int fogOfWarChance, MinMaxOption visionRange, Boolean casualMode, ExperienceRate experienceRate) {
		super();
		this.applyEnglishPatch = applyEnglishPatch;
		this.tripleEffectiveness = tripleEffectiveness;
		followupRequirement = null;
		singleRNMode = singleRN;
		randomizeFogOfWar = fogOfWar;
		fogOfWarVisionRange = visionRange;
		this.fogOfWarChance = fogOfWarChance;
		this.casualMode = casualMode;
		this.experienceRate = experienceRate;
	}
	
}