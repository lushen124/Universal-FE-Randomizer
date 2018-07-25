package ui.model;

public class EnemyOptions {

	public enum BuffMode {
		NONE, FLAT, SCALING
	}
	
	public final BuffMode mode;
	public final int buffAmount;
	
	public final Boolean improveWeapons;
	public final Boolean nudgeUnits;
	
	public EnemyOptions(BuffMode mode, int buffAmount, Boolean improveWeapons, Boolean nudgeUnits) {
		super();
		this.mode = mode;
		this.buffAmount = buffAmount;
		this.improveWeapons = improveWeapons;
		this.nudgeUnits = nudgeUnits;
	}
}
