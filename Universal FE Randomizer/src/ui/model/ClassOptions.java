package ui.model;

public class ClassOptions {
	
	public enum BaseTransferOption {
		NO_CHANGE, ADJUST_TO_MATCH, ADJUST_TO_CLASS
	}
	
	public final Boolean randomizePCs;
	public final Boolean createPrfs;
	public final Boolean unbreakablePrfs;
	public final Boolean includeLords;
	public final Boolean includeThieves;
	public final Boolean includeSpecial;
	public final boolean assignEvenly;
	
	public final boolean forceChange;
	
	public final Boolean separateMonsters; // FE8 only.
	
	public final Boolean randomizeEnemies;
	public final Boolean randomizeBosses;
	
	public final BaseTransferOption basesTransfer;
	
	public ClassOptions(Boolean pcs, Boolean lords, Boolean newPrfs, Boolean unbreakablePrfs, Boolean thieves, Boolean special, boolean forceChange, boolean assignEvenly, Boolean enemies, Boolean bosses, BaseTransferOption basesTransfer) {
		super();
		randomizePCs = pcs;
		createPrfs = newPrfs;
		this.unbreakablePrfs = unbreakablePrfs;
		includeLords = lords;
		includeThieves = thieves;
		includeSpecial = special;
		this.assignEvenly = assignEvenly;
		separateMonsters = false;
		
		randomizeEnemies = enemies;
		
		randomizeBosses = bosses;
		
		this.basesTransfer = basesTransfer;
		
		this.forceChange = forceChange;
	}
	
	public ClassOptions(Boolean pcs, Boolean lords, Boolean newPrfs, Boolean unbreakablePrfs, Boolean thieves, Boolean special, Boolean separateMonsters, boolean forceChange, boolean assignEvenly, Boolean enemies, Boolean bosses, BaseTransferOption basesTransfer) {
		super();
		randomizePCs = pcs;
		createPrfs = newPrfs;
		this.unbreakablePrfs = unbreakablePrfs;
		includeLords = lords;
		includeThieves = thieves;
		includeSpecial = special;
		this.assignEvenly = assignEvenly;
		this.separateMonsters = separateMonsters;
		
		randomizeEnemies = enemies;
		
		randomizeBosses = bosses;
		
		this.basesTransfer = basesTransfer;
		
		this.forceChange = forceChange;
	}

}
