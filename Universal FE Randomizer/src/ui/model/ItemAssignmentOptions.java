package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class ItemAssignmentOptions implements RecordableOption {
	
	public enum WeaponReplacementPolicy {
		STRICT, EQUAL_RANK, ANY_USABLE
	}

	public enum ShopAdjustment {
		NO_CHANGE, ADJUST_TO_PARTY, RANDOM
	}
	
	public final WeaponReplacementPolicy weaponPolicy;
	public final ShopAdjustment shopAdjustment;
	
	public final boolean assignPromoWeapons;
	public final boolean assignPoisonWeapons;
	
	public ItemAssignmentOptions() {
		this.weaponPolicy= WeaponReplacementPolicy.ANY_USABLE;
		this.shopAdjustment = ShopAdjustment.NO_CHANGE;
		this.assignPromoWeapons = false;
		this.assignPoisonWeapons = false;
	}
	
	public ItemAssignmentOptions(WeaponReplacementPolicy weapons, ShopAdjustment shops, boolean assignPromo, boolean assignPoison) {
		this.weaponPolicy = weapons;
		shopAdjustment = shops;
		assignPromoWeapons = assignPromo;
		assignPoisonWeapons = assignPoison;
	}

	@Override
	public void record(RecordKeeper rk, GameType type) {
		switch (weaponPolicy) {
		case STRICT:
			rk.addHeaderItem("Weapon Assignment Policy", "Strict Matching");
			break;
		case EQUAL_RANK:
			rk.addHeaderItem("Weapon Assignment Policy", "Match Rank");
			break;
		case ANY_USABLE:
			rk.addHeaderItem("Weapon Assignment Policy", "Random");
			break;
		}

		if (type != GameType.FE6) {
			rk.addHeaderItem("Assign Promotional Weapons",
					assignPromoWeapons ? "YES" : "NO");
		}
		rk.addHeaderItem("Assign Poison Weapons",
				assignPoisonWeapons ? "YES" : "NO");		
	}
}
