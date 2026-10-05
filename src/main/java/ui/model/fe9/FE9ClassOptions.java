package ui.model.fe9;

public class FE9ClassOptions {
	
	public final boolean randomizePCs;
	public final boolean includeLords;
	public final boolean includeThieves;
	public final boolean includeSpecial;
	public final boolean allowCrossgender;
	public final boolean mixPCRaces;
	public final boolean assignClassesEvenly;
	
	public final boolean randomizeBosses;
	public final boolean mixBossRaces;
	
	public final boolean randomizeMinions;
	public final boolean mixMinionRaces;
	public final int minionRandomChance;
	
	public boolean forceDifferent;

	public FE9ClassOptions(boolean playableCharacters, boolean lords, boolean thieves, boolean special, boolean crossgender, boolean mixPCRaces, boolean assignEvenly,
			boolean bosses, boolean mixBossRaces,
			boolean minions, boolean mixMinionRaces, int minionRandomChance, 
			boolean forceDifferent) {
		randomizePCs = playableCharacters;
		includeLords = lords;
		includeThieves = thieves;
		includeSpecial = special;
		allowCrossgender = crossgender;
		this.mixPCRaces = mixPCRaces;
		assignClassesEvenly = assignEvenly;
		
		randomizeBosses = bosses;
		this.mixBossRaces = mixBossRaces;
		
		randomizeMinions = minions;
		this.mixMinionRaces = mixMinionRaces;
		this.minionRandomChance = minionRandomChance;
		
		this.forceDifferent = forceDifferent;
	}
}
