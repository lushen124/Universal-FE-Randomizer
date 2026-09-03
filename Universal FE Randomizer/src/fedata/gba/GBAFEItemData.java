package fedata.gba;

import java.util.Comparator;
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
	
	public static Comparator<GBAFEItemData> idComparator = new Comparator<GBAFEItemData>() {
		@Override
		public int compare(GBAFEItemData o1, GBAFEItemData o2) {
			return Integer.compare(o1.getID(), o2.getID());
		}
	};
	
	// Info
	public int getNameIndex();
	public void setNameIndex(int newNameIndex);
	public int getDescriptionIndex();
	public void setDescriptionIndex(int newDescriptionIndex);
	public int getUseDescriptionIndex();
	public void setUseDescriptionIndex(int newUseIndex);
	
	public int getID();
	
	public WeaponType getType();
	public void setType(WeaponType type);
	
	public boolean hasAbility1();
	public int getAbility1();
	public String getAbility1Description(String delimiter);
	public void setAbility1(int ability);
	
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
	public void setAbility4(int ability);
	
	public boolean hasAbilityOrEffect(String abilityEffectString);
	
	public long getStatBonusPointer();
	public void setStatBonusPointer(long address);
	public long getEffectivenessPointer();
	public void setEffectivenessPointer(long address);
	
	public int getIconIndex();
	public void setIconIndex(int newIcon);
	
	public int getDurability();
	public int getMight();
	public int getHit();
	public int getWeight();
	public int getCritical();
	
	public int getMinRange();
	public int getMaxRange();
	
	public void setMinRange(int minRange);
	public void setMaxRange(int maxRange);
	
	public int getCostPerUse();
	public void setCostPerUse(int cost);
	
	public WeaponRank getWeaponRank();
	public void setWeaponRank(WeaponRank newRank);
	
	public boolean hasWeaponEffect();
	public int getWeaponEffect();
	public String getWeaponEffectDescription();
	
	public int getWeaponExperience();
	
	public void setDurability(int durability);
	public void setMight(int might);
	public void setHit(int hit);
	public void setWeight(int weight);
	public void setCritical(int crit);
	
	public void applyRandomEffect(WeightedDistributor<WeaponEffects> allowedEffects, ItemDataLoader itemData, TextLoader textData, GBAFESpellAnimationCollection spellAnimations, Random rng);

	public void turnIntoLordWeapon(int lordID, int nameIndex, int descriptionIndex, WeaponType weaponType, boolean isUnbreakable, boolean isEffective, int targetWeaponWeight, GBAFEItemData referenceItem, ItemDataLoader itemData, FreeSpaceManager freeSpace);
	
	public GBAFEItemData createLordWeapon(int lordID, int newItemID, int nameIndex, int descriptionIndex, WeaponType weaponType, boolean isUnbreakable, boolean isEffective, int targetWeaponWeight, int iconIndex, ItemDataLoader itemData, FreeSpaceManager freeSpace);
}