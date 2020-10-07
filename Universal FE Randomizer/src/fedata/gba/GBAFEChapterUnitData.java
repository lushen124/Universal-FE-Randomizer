package fedata.gba;

import fedata.general.FEModifiableData;

public interface GBAFEChapterUnitData extends FEModifiableData {
	public int getCharacterNumber();
	
	public int getStartingClass();
	public void setStartingClass(int classID);
	
	public int getStartingLevel();
	public void setStartingLevel(int level);
	public boolean isEnemy();
	public boolean isNPC();
	public boolean isAutolevel();
	
	public int getLeaderID();
	
	public int getLoadingX();
	public int getLoadingY();
	
	public void setLoadingX(int newX);
	public void setLoadingY(int newY);
	
	public int getStartingX();
	public int getStartingY();
	
	public void setStartingX(int newX);
	public void setStartingY(int newY);
	
	public int getItem1();
	public void setItem1(int itemID);	
	public int getItem2();
	public void setItem2(int itemID);
	public int getItem3();
	public void setItem3(int itemID);
	public int getItem4();
	public void setItem4(int itemID);
	
	public void giveItem(int itemID);
	public void giveItems(int[] itemIDs);
	public void removeItem(int itemID);
	public boolean hasItem(int itemID);
	
	public void setAIToHeal(Boolean allowAttack);
	public void setAIToOnlyAttack(Boolean allowMove);
}
