package ui.fe4;

public class FE4ClassOptions {
	
	public enum HolyBloodOptions {
		NONE,
		RESTRICT_CLASS,
		RESTRICT_BLOOD
	}
	
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
	public final HolyBloodOptions bloodOption;
	public final boolean adjustShopItems;
	
	public final boolean randomizeMinions;
	
	public final boolean randomizeArena;
	
	public final boolean randomizeBosses;
	public final HolyBloodOptions bossBloodOption;
	
	public FE4ClassOptions(boolean pcs, boolean lords, boolean thieves, boolean dancers, ChildOptions children, boolean blood, HolyBloodOptions bloodOptions, boolean adjustShops, boolean minions, boolean arena, boolean bosses, HolyBloodOptions bossBlood) {
		super();
		this.randomizePlayableCharacters = pcs;
		this.includeLords = lords;
		this.includeThieves = thieves;
		this.includeDancers = dancers;
		this.childOption = children;
		this.randomizeBlood = blood;
		this.bloodOption = bloodOptions;
		this.adjustShopItems = adjustShops;
		
		this.randomizeMinions = minions;
		
		this.randomizeArena = arena;
		
		this.randomizeBosses = bosses;
		this.bossBloodOption = bossBlood;
	}
}
