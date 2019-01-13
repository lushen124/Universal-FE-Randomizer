package ui.fe4;

public class FE4ClassOptions {
	
	public enum ChildOptions {
		MATCH_STRICT,
		MATCH_LOOSE,
		RANDOM_CLASS
	}
	
	public enum ShopOptions {
		ADJUST_TO_MATCH,
		RANDOMIZE
	}
	
	public final boolean randomizePlayableCharacters;
	public final boolean includeLords;
	public final boolean retainHealers;
	public final boolean includeThieves;
	public final boolean includeDancers;
	public final ChildOptions childOption;
	public final boolean randomizeBlood;
	public final ShopOptions shopOption;
	public final boolean adjustConversationWeapons;
	
	public final boolean randomizeMinions;
	
	public final boolean randomizeArena;
	
	public final boolean randomizeBosses;
	public final boolean randomizeBossBlood;
	
	public FE4ClassOptions(boolean pcs, boolean lords, boolean healers, boolean thieves, boolean dancers, ChildOptions children, boolean blood, ShopOptions shops, boolean adjustConvoWeapons, boolean minions, boolean arena, boolean bosses, boolean bossBlood) {
		super();
		this.randomizePlayableCharacters = pcs;
		this.includeLords = lords;
		this.retainHealers = healers;
		this.includeThieves = thieves;
		this.includeDancers = dancers;
		this.childOption = children;
		this.randomizeBlood = blood;
		this.shopOption = shops;
		this.adjustConversationWeapons = adjustConvoWeapons;
		
		this.randomizeMinions = minions;
		
		this.randomizeArena = arena;
		
		this.randomizeBosses = bosses;
		this.randomizeBossBlood = bossBlood;
	}
}
