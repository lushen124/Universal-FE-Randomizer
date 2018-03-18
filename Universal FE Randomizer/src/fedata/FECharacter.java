package fedata;

public interface FECharacter extends FEModifiableObject {
	
	// Info
	
	public int getNameIndex();
	public int getDescriptionIndex();
	
	public int getID();
	
	public int getClassID();
	public void setClassID(int classID);
	
	// Growths
	
	public int getHPGrowth();
	public void setHPGrowth(int hpGrowth);
	
	public int getSTRGrowth();
	public void setSTRGrowth(int strGrowth);
	
	public int getSKLGrowth();
	public void setSKLGrowth(int sklGrowth);
	
	public int getSPDGrowth();
	public void setSPDGrowth(int spdGrowth);
	
	public int getDEFGrowth();
	public void setDEFGrowth(int defGrowth);
	
	public int getRESGrowth();
	public void setRESGrowth(int resGrowth);
	
	public int getLCKGrowth();
	public void setLCKGrowth(int lckGrowth);
	
	// Bases
	
	public int getBaseHP();
	public void setBaseHP(int baseHP);
	
	public int getBaseSTR();
	public void setBaseSTR(int baseSTR);
	
	public int getBaseSKL();
	public void setBaseSKL(int baseSKL);
	
	public int getBaseSPD();
	public void setBaseSPD(int baseSPD);
	
	public int getBaseDEF();
	public void setBaseDEF(int baseDEF);
	
	public int getBaseRES();
	public void setBaseRES(int baseRES);
	
	public int getBaseLCK();
	public void setBaseLCK(int baseLCK);
}
