package ui.model;

public class ClassOptions {
	
	public final Boolean randomizePCs;
	public final Boolean includeLords;
	public final Boolean includeThieves;
	public final boolean assignEvenly;
	
	public final Boolean separateMonsters; // FE8 only.
	
	public final Boolean randomizeEnemies;
	public final Boolean randomizeBosses;
	
	public ClassOptions(Boolean pcs, Boolean lords, Boolean thieves, boolean assignEvenly, Boolean enemies, Boolean bosses) {
		super();
		randomizePCs = pcs;
		includeLords = lords;
		includeThieves = thieves;
		this.assignEvenly = assignEvenly;
		separateMonsters = false;
		
		randomizeEnemies = enemies;
		
		randomizeBosses = bosses;
	}
	
	public ClassOptions(Boolean pcs, Boolean lords, Boolean thieves, Boolean separateMonsters, boolean assignEvenly, Boolean enemies, Boolean bosses ) {
		super();
		randomizePCs = pcs;
		includeLords = lords;
		includeThieves = thieves;
		this.assignEvenly = assignEvenly;
		this.separateMonsters = separateMonsters;
		
		randomizeEnemies = enemies;
		
		randomizeBosses = bosses;
	}

}
