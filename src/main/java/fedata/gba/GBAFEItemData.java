package fedata.gba;

import java.util.Random;

import fedata.gba.general.WeaponEffects;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import fedata.general.FEModifiableData;
import fedata.general.FEPrintableData;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import random.general.WeightedDistributor;
import util.FreeSpaceManager;

public interface GBAFEItemData extends FEModifiableData, FEPrintableData {
	
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
	public void setAbility2(int ability); // Used for an unused weapon lock in FE7.
	
	public boolean hasAbility3();
	public int getAbility3();
	public String getAbility3Description(String delimiter);
	public void setAbility3(int ability); // Used for FE7's weapon locks.
	
	public boolean hasAbility4();
	public int getAbility4();
	public String getAbility4Description(String delimiter);
	
	public boolean hasAbilityOrEffect(String abilityEffectString);
	
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
	
	public int getCostPerUse();
	public void setCostPerUse(int cost);
	
	public WeaponRank getWeaponRank();
	public void setWeaponRank(WeaponRank newRank);
	
	public boolean hasWeaponEffect();
	public int getWeaponEffect();
	public String getWeaponEffectDescription();
	
	public void setDurability(int durability);
	public void setMight(int might);
	public void setHit(int hit);
	public void setWeight(int weight);
	
	public void applyRandomEffect(WeightedDistributor<WeaponEffects> allowedEffects, ItemDataLoader itemData, TextLoader textData, GBAFESpellAnimationCollection spellAnimations, Random rng);

	public void turnIntoLordWeapon(int lordID, int nameIndex, int descriptionIndex, WeaponType weaponType, boolean isUnbreakable, int targetWeaponWeight, GBAFEItemData referenceItem, ItemDataLoader itemData, FreeSpaceManager freeSpace);
	
	public GBAFEItemData createLordWeapon(int lordID, int newItemID, int nameIndex, int descriptionIndex, WeaponType weaponType, boolean isUnbreakable, int targetWeaponWeight, int iconIndex, ItemDataLoader itemData, FreeSpaceManager freeSpace);
}