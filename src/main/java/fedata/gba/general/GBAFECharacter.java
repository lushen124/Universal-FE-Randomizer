package fedata.gba.general;

import java.util.Comparator;

public interface GBAFECharacter {

	public int getID();
	
	public Boolean isPlayable();
	public Boolean isBoss();
	public Boolean canChange();
	public Boolean isLord();
	public Boolean isThief();
	public Boolean isSpecial();
	public Boolean requiresRange();
	public Boolean requiresMelee();
	public Boolean isClassLimited();
	public Boolean requiresAttack();
	public Boolean canBuff();
	
	public String toString();
	
	public static Comparator<GBAFECharacter> getIDComparator() {
		return new Comparator<GBAFECharacter>() {
			@Override
			public int compare(GBAFECharacter o1, GBAFECharacter o2) {
				return Integer.compare(o1.getID(), o2.getID());
			}
		};
	}
}
