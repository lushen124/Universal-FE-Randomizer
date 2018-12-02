package fedata.gba.general;

import java.util.Comparator;

public interface GBAFEItem {
	
	public int getID();
	
	static Comparator<GBAFEItem> defaultComparator() {
		return new Comparator<GBAFEItem>() {
			public int compare(GBAFEItem arg0, GBAFEItem arg1) {
				return Integer.compare(arg0.getID(), arg1.getID());
			}
		};
	}
	
	public Boolean isWeapon();
	public Boolean isBasicWeapon();
	public Boolean isStatBooster();
	public Boolean isPromotionItem();
	public Boolean isHealingStaff();

	public WeaponType getType();
	public WeaponRank getRank();
}
