package ui.model;

public class EnemyOptions {

	public enum BuffMode {
		NONE, FLAT, SCALING
	}
	
	public final BuffMode mode;
	public final int buffAmount;
	
	public final Boolean improveWeapons;
	public final int improvementChance;
	
	public EnemyOptions(BuffMode mode, int buffAmount, Boolean improveWeapons, int improvementChance) {
		super();
		this.mode = mode;
		this.buffAmount = buffAmount;
		this.improveWeapons = improveWeapons;
		this.improvementChance = improvementChance;
	}
}
