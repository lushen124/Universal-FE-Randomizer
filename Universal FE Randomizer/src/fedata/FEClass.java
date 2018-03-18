package fedata;

public interface FEClass extends FEModifiableObject {
	
	// Info
	
	public int getNameIndex();
	public int getDescriptionIndex();
	
	public int getID();
	
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
	public int getBaseSTR();
	public int getBaseSKL();
	public int getBaseSPD();
	public int getBaseDEF();
	public int getBaseRES();
	public int getBaseLCK();
	
	// Caps
	
	public int getMaxHP();
	public int getMaxSTR();
	public int getMaxSKL();
	public int getMaxSPD();
	public int getMaxDEF();
	public int getMaxRES();
	public int getMaxLCK();
}
