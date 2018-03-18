package ui.model;

public class ClassOptions {
	
	public final Boolean randomizePCs;
	public final Boolean includeLords;
	public final Boolean includeThieves;
	
	public final Boolean randomizeEnemies;
	public final Boolean randomizeBosses;
	
	public ClassOptions(Boolean pcs, Boolean lords, Boolean thieves, Boolean enemies, Boolean bosses) {
		super();
		randomizePCs = pcs;
		includeLords = lords;
		includeThieves = thieves;
		
		randomizeEnemies = enemies;
		
		randomizeBosses = bosses;
	}

}
