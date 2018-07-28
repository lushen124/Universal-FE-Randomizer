package fedata;

public interface FEChapterUnit extends FEModifiableObject {
	
	public Boolean isModifiable();
	
	public int getCharacterNumber();
	
	public int getStartingClass();
	public void setStartingClass(int classID);
	
	public int getLeaderID();
	
	public int getLoadingX();
	public int getLoadingY();
	
	public int getStartingX();
	public int getStartingY();
	
	public int getItem1();
	public void setItem1(int itemID);	
	public int getItem2();
	public void setItem2(int itemID);
	public int getItem3();
	public void setItem3(int itemID);
	public int getItem4();
	public void setItem4(int itemID);
	
	public void giveItems(int[] itemIDs);
	
	public void setAIToHeal(Boolean allowAttack);
	public void setAIToOnlyAttack(Boolean allowMove);
}
