package ui.fe4;

public class FE4ClassOptions {
	
	public enum ChildOptions {
		ADJUST_TO_MATCH,
		RANDOM_CLASS
	}
	
	public final boolean randomizePlayableCharacters;
	public final boolean includeLords;
	public final boolean includeThieves;
	public final boolean includeDancers;
	public final ChildOptions childOption;
	public final boolean randomizeBlood;
	
	public final boolean randomizeMinions;
	
	public final boolean randomizeArena;
	
	public final boolean randomizeBosses;
	public final boolean randomizeBossBlood;
	
	public FE4ClassOptions(boolean pcs, boolean lords, boolean thieves, boolean dancers, ChildOptions children, boolean blood, boolean minions, boolean arena, boolean bosses, boolean bossBlood) {
		super();
		this.randomizePlayableCharacters = pcs;
		this.includeLords = lords;
		this.includeThieves = thieves;
		this.includeDancers = dancers;
		this.childOption = children;
		this.randomizeBlood = blood;
		
		this.randomizeMinions = minions;
		
		this.randomizeArena = arena;
		
		this.randomizeBosses = bosses;
		this.randomizeBossBlood = bossBlood;
	}
}
