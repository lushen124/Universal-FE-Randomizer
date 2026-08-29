package ui.model;

public class ItemAssignmentOptions {
	
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
	
	public ItemAssignmentOptions(WeaponReplacementPolicy weapons, ShopAdjustment shops, boolean assignPromo, boolean assignPoison) {
		this.weaponPolicy = weapons;
		shopAdjustment = shops;
		assignPromoWeapons = assignPromo;
		assignPoisonWeapons = assignPoison;
	}
}
