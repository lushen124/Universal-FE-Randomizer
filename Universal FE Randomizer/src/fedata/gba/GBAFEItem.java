package fedata.gba;

import java.util.Random;
import java.util.Set;

import fedata.gba.general.WeaponEffects;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;

public interface GBAFEItem extends GBAFEModifiableObject {
	
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
	
	public void applyRandomEffect(Set<WeaponEffects> allowedEffects, ItemDataLoader itemData, TextLoader textData, GBAFESpellAnimationCollection spellAnimations, Random rng);

}
