package fedata.gba;

import java.util.Comparator;

import fedata.gba.general.WeaponRank;
import fedata.general.FEModifiableData;
import fedata.general.FEPrintableData;

public interface GBAFEClassData extends FEModifiableData, FEPrintableData {
	
	static Comparator<GBAFEClassData> defaultComparator = new Comparator<GBAFEClassData>() {
		@Override
		public int compare(GBAFEClassData arg0, GBAFEClassData arg1) {
			return Integer.compare(arg0.getID(), arg1.getID());
		}
	};
	
	// Info
	
	public int getNameIndex();
	public int getDescriptionIndex();
	
	public int getID();
	
	public int getTargetPromotionID();
	public void setTargetPromotionID(int promotionTargetClassID);
	
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
	
	// Promo Bonuses
	
	public int getPromoHP();
	public int getPromoSTR();
	public int getPromoSKL();
	public int getPromoSPD();
	public int getPromoDEF();
	public int getPromoRES();
	
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
	
	// Helpers
	public Boolean canUseWeapon(GBAFEItemData weapon);
}
