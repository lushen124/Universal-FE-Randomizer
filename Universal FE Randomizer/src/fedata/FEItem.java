package fedata;

import fedata.general.WeaponRank;
import fedata.general.WeaponType;

public interface FEItem extends FEModifiableObject {
	
	// Info
	public int getNameIndex();
	public int getDescriptionIndex();
	public int getUseDescriptionIndex();
	
	public int getID();
	
	public WeaponType getType();
	
	public int getAbility1();
	public int getAbility2();
	public int getAbility3();
	public int getAbility4();
	
	public long getStatBonusPointer();
	public long getEffectivenessPointer();
	
	public int getDurability();
	public int getMight();
	public int getHit();
	public int getWeight();
	public int getCritical();
	
	public int getMinRange();
	public int getMaxRange();
	
	public WeaponRank getWeaponRank();
	public int getWeaponEffect();
	
	public void setDurability(int durability);
	public void setMight(int might);
	public void setHit(int hit);
	public void setWeight(int weight);
	public void setCritical(int critical);
	
	public void setMinRange(int range);
	public void setMaxRange(int range);
	
	public void applyRandomEffect();

}
