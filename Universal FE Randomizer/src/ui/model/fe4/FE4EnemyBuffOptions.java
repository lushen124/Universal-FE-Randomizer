package ui.model.fe4;

public class FE4EnemyBuffOptions {
	
	public enum EnemyScalingOptions {
		FLAT, SCALING
	}
	
	public final boolean increaseEnemyScaling;
	public final EnemyScalingOptions scalingOption;
	public final int scalingAmount;
	
	public final boolean improveMinionWeapons;
	public final int improvementChance;
	
	public final boolean majorHolyBloodBosses;
	
	public FE4EnemyBuffOptions(boolean increaseEnemyScaling, EnemyScalingOptions scalingOption, int scalingAmount, boolean improveMinionWeapons, int improveChance, boolean majorBloodHolyBosses) {
		super();
		this.increaseEnemyScaling = increaseEnemyScaling;
		this.scalingOption = scalingOption;
		this.scalingAmount = scalingAmount;
		this.improveMinionWeapons = improveMinionWeapons;
		this.improvementChance = improveChance;
		this.majorHolyBloodBosses = majorBloodHolyBosses;
	}
}
