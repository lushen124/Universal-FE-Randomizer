package fedata.gba.general;

public interface GBAFECharacter {

	public int getID();
	
	public Boolean isPlayable();
	public Boolean isBoss();
	public Boolean canChange();
	public Boolean isLord();
	public Boolean isThief();
	public Boolean requiresRange();
	public Boolean requiresMelee();
	public Boolean isClassLimited();
}
