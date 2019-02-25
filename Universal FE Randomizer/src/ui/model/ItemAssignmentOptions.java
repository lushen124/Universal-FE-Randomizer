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
	
	public ItemAssignmentOptions(WeaponReplacementPolicy weapons, ShopAdjustment shops) {
		this.weaponPolicy = weapons;
		shopAdjustment = shops;
	}
}
