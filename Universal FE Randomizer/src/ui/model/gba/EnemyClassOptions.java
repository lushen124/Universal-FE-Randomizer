package ui.model.gba;

public class EnemyClassOptions {
	public boolean randomizeBosses;
	public boolean randomizeMinions;
	public int minionChance;
	
	public EnemyClassOptions(boolean bosses, boolean minions, int minionChance) {
		randomizeBosses = bosses;
		randomizeMinions = minions;
		this.minionChance = minionChance;
	}
}
