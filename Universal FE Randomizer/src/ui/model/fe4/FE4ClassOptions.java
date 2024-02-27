package ui.model.fe4;

public class FE4ClassOptions {
	
	public enum ChildOptions {
		MATCH_STRICT,
		MATCH_LOOSE,
		RANDOM_CLASS
	}
	
	public enum ShopOptions {
		DO_NOT_ADJUST,
		ADJUST_TO_MATCH,
		RANDOMIZE
	}
	
	public enum ItemAssignmentOptions {
		SIDEGRADE_STRICT,
		SIDEGRADE_LOOSE,
		RANDOMIZE
	}
	
	public enum BloodOptions {
		NO_CHANGE,
		SHUFFLE,
		RANDOMIZE
	}
	
	public final boolean randomizePlayableCharacters;
	public final boolean includeLords;
	public final boolean retainHealers;
	public final boolean retainHorses;
	public final boolean includeThieves;
	public final boolean includeDancers;
	public final boolean includeJulia;
	public final boolean assignEvenly;
	public final ChildOptions childOption;
	public final BloodOptions playerBloodOption;
	public final ShopOptions shopOption;
	public final boolean adjustConversationWeapons;
	public final boolean adjustSTRMAG;
	public final ItemAssignmentOptions itemOptions;
	
	public final boolean randomizeMinions;
	
	public final boolean randomizeArena;
	
	public final boolean randomizeBosses;
	public final BloodOptions bossBloodOption;
	
	public FE4ClassOptions(boolean pcs, boolean lords, boolean healers, boolean horses, boolean thieves, boolean dancers, boolean julia, boolean assignEvenly, ChildOptions children, BloodOptions playerBlood, ShopOptions shops, boolean adjustConvoWeapons, boolean adjustSTRMAG, ItemAssignmentOptions itemOptions, boolean minions, boolean arena, boolean bosses, BloodOptions bossBlood) {
		super();
		this.randomizePlayableCharacters = pcs;
		this.includeLords = lords;
		this.retainHealers = healers;
		this.retainHorses = horses;
		this.includeThieves = thieves;
		this.includeDancers = dancers;
		this.includeJulia = julia;
		this.assignEvenly = assignEvenly;
		this.childOption = children;
		this.playerBloodOption = playerBlood;
		this.shopOption = shops;
		this.adjustConversationWeapons = adjustConvoWeapons;
		this.adjustSTRMAG = adjustSTRMAG;
		this.itemOptions = itemOptions;
		
		this.randomizeMinions = minions;
		
		this.randomizeArena = arena;
		
		this.randomizeBosses = bosses;
		this.bossBloodOption = bossBlood;
	}
}
