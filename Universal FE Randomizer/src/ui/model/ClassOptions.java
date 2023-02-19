package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class ClassOptions implements RecordableOption {
	
	public enum BaseTransferOption {
		NO_CHANGE, ADJUST_TO_MATCH, ADJUST_TO_CLASS
	}
	
	public enum GenderRestrictionOption {
		STRICT, LOOSE, NONE
	}
	
	public enum GrowthAdjustmentOption {
		NO_CHANGE, TRANSFER_PERSONAL_GROWTHS, CLASS_RELATIVE_GROWTHS
	}
	
	public final Boolean randomizePCs;
	public final Boolean createPrfs;
	public final Boolean unbreakablePrfs;
	public final Boolean includeLords;
	public final Boolean includeThieves;
	public final Boolean includeSpecial;
	public final boolean assignEvenly;
	
	public final boolean forceChange;
	
	public final GenderRestrictionOption genderOption;
	
	public final Boolean separateMonsters; // FE8 only.
	
	public final Boolean randomizeEnemies;
	public final Boolean randomizeBosses;
	
	public final BaseTransferOption basesTransfer;
	public final GrowthAdjustmentOption growthOptions;
	
	public ClassOptions(Boolean pcs, Boolean lords, Boolean newPrfs, Boolean unbreakablePrfs, Boolean thieves, Boolean special, boolean forceChange, GenderRestrictionOption genderOption, boolean assignEvenly, Boolean enemies, Boolean bosses, BaseTransferOption basesTransfer, GrowthAdjustmentOption growthOptions) {
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
		this.growthOptions = growthOptions;
		
		this.forceChange = forceChange;
		this.genderOption = genderOption;
	}
	
	public ClassOptions(Boolean pcs, Boolean lords, Boolean newPrfs, Boolean unbreakablePrfs, Boolean thieves, Boolean special, Boolean separateMonsters, boolean forceChange, GenderRestrictionOption genderOption, boolean assignEvenly, Boolean enemies, Boolean bosses, BaseTransferOption basesTransfer, GrowthAdjustmentOption growthOptions) {
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
		this.growthOptions = growthOptions;
		
		this.forceChange = forceChange;
		this.genderOption = genderOption;
	}

	@Override
	public void record(RecordKeeper rk, GameType type) {
		if (randomizePCs) {
			StringBuilder sb = new StringBuilder();

			if (includeLords) {
				sb.append("Include Lords<br>");
			}
			if (includeThieves) {
				sb.append("Include Thieves<br>");
			}
			if (includeSpecial) {
				sb.append("Include Special Classes<br>");
			}
			if (assignEvenly) {
				sb.append("Assign Evenly<br>");
			}
			if (sb.length() > 4) {
				sb.delete(sb.length() - 4, sb.length());
			}
			if (sb.length() == 0) {
				sb.append("YES");
			}
			rk.addHeaderItem("Randomize Playable Character Classes", sb.toString());

			switch (growthOptions) {
			case NO_CHANGE:
				rk.addHeaderItem("Growth Transfer Option", "No Change");
				break;
			case TRANSFER_PERSONAL_GROWTHS:
				rk.addHeaderItem("Growth Transfer Option", "Transfer Personal Growths");
				break;
			case CLASS_RELATIVE_GROWTHS:
				rk.addHeaderItem("Growth Transfer Option", "Class Relative Growths");
				break;
			}
		} else {
			rk.addHeaderItem("Randomize Playable Character Classes", "NO");
		}
		if (randomizeBosses) {
			rk.addHeaderItem("Randomize Boss Classes", "YES");
		} else {
			rk.addHeaderItem("Randomize Boss Classes", "NO");
		}
		if (randomizeEnemies) {
			rk.addHeaderItem("Randomize Minions", "YES");
		} else {
			rk.addHeaderItem("Randomize Minions", "NO");
		}
		if (randomizePCs || randomizeBosses) {
			switch (basesTransfer) {
			case NO_CHANGE:
				rk.addHeaderItem("Base Stats Transfer Mode", "Retain Personal Bases");
				break;
			case ADJUST_TO_MATCH:
				rk.addHeaderItem("Base Stats Transfer Mode", "Retain Final Bases");
				break;
			case ADJUST_TO_CLASS:
				rk.addHeaderItem("Base Stats Transfer Mode", "Adjust to Class");
				break;
			}
		}
		if (forceChange) {
			rk.addHeaderItem("Force Class Change", "YES");
		} else {
			rk.addHeaderItem("Force Class Change", "NO");
		}
		switch (genderOption) {
		case NONE:
			rk.addHeaderItem("Gender Restriction", "None");
			break;
		case LOOSE:
			rk.addHeaderItem("Gender Restriction", "Loose");
			break;
		case STRICT:
			rk.addHeaderItem("Gender Restriction", "Strict");
			break;
		}
		if (type == GameType.FE8) {
			if (separateMonsters) {
				rk.addHeaderItem("Mix Monster and Human Classes", "NO");
			} else {
				rk.addHeaderItem("Mix Monster and Human Classes", "YES");
			}
		}
		
	}

}
