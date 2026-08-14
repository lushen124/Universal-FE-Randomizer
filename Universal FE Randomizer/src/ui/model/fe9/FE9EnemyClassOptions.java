package ui.model.fe9;

public class FE9EnemyClassOptions {

	public boolean randomizeBosses;
	public boolean forceChange;
	public boolean allowCrossRaceBosses;
	
	public boolean randomizeMinions;
	public int minionRandomizeChance;
	public boolean allowCrossRaceMinions;
	
	public FE9EnemyClassOptions(boolean randomizeBosses, boolean forceBossChange, boolean allowBossCrossRace, boolean randomizeMinions, int minionChance, boolean allowMinionCrossRace) {
		this.randomizeBosses = randomizeBosses;
		this.forceChange = forceBossChange;
		this.allowCrossRaceBosses = allowBossCrossRace;
		
		this.randomizeMinions = randomizeMinions;
		this.minionRandomizeChance = minionChance;
		this.allowCrossRaceMinions = allowMinionCrossRace;
	}
}
