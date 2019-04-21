package fedata.gba;

import java.util.Random;
import java.util.Set;

import fedata.gba.general.WeaponEffects;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import fedata.general.FEModifiableData;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;

public interface GBAFEItemData extends FEModifiableData {
	
	// Info
	public int getNameIndex();
	public int getDescriptionIndex();
	public int getUseDescriptionIndex();
	
	public int getID();
	
	public WeaponType getType();
	
	public boolean hasAbility1();
	public int getAbility1();
	public String getAbility1Description(String delimiter);
	
	public boolean hasAbility2();
	public int getAbility2();
	public String getAbility2Description(String delimiter);
	
	public boolean hasAbility3();
	public int getAbility3();
	public String getAbility3Description(String delimiter);
	
	public boolean hasAbility4();
	public int getAbility4();
	public String getAbility4Description(String delimiter);
	
	public long getStatBonusPointer();
	public void setStatBonusPointer(long address);
	public long getEffectivenessPointer();
	public void setEffectivenessPointer(long address);
	
	public int getDurability();
	public int getMight();
	public int getHit();
	public int getWeight();
	public int getCritical();
	
	public int getMinRange();
	public int getMaxRange();
	
	public WeaponRank getWeaponRank();
	
	public boolean hasWeaponEffect();
	public int getWeaponEffect();
	public String getWeaponEffectDescription();
	
	public void setDurability(int durability);
	public void setMight(int might);
	public void setHit(int hit);
	public void setWeight(int weight);
	
	public void applyRandomEffect(Set<WeaponEffects> allowedEffects, ItemDataLoader itemData, TextLoader textData, GBAFESpellAnimationCollection spellAnimations, Random rng);

}
