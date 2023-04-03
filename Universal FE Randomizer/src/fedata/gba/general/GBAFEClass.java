package fedata.gba.general;

public interface GBAFEClass {
	public int getID();
	
	public Boolean isLord();
	public Boolean isThief();
	public Boolean isFemale();
	public Boolean isPromoted();
	public Boolean canAttack();
	public String name();
}
