package fedata;

import fedata.general.WeaponRank;

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
	
	// Weapon Ranks
	
	public int getSwordRank();
	public void setSwordRank(WeaponRank rank);
	public int getLanceRank();
	public void setLanceRank(WeaponRank rank);
	public int getAxeRank();
	public void setAxeRank(WeaponRank rank);
	public int getBowRank();
	public void setBowRank(WeaponRank rank);
	public int getAnimaRank();
	public void setAnimaRank(WeaponRank rank);
	public int getLightRank();
	public void setLightRank(WeaponRank rank);
	public int getDarkRank();
	public void setDarkRank(WeaponRank rank);
	public int getStaffRank();
	public void setStaffRank(WeaponRank rank);
	
	public int getBaseRankValue();
	
	// Miscellaneous
	
	public int getMOV();
	public void setMOV(int newMOV);
	public int getCON();
	public void setCON(int newCON);
}
