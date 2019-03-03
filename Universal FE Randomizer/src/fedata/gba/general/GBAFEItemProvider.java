package fedata.gba.general;

import java.util.List;
import java.util.Random;
import java.util.Set;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFESpellAnimationCollection;

public interface GBAFEItemProvider {
	
	public static class WeaponRanks {
		public final WeaponRank swordRank;
		public final WeaponRank lanceRank;
		public final WeaponRank axeRank;
		public final WeaponRank bowRank;
		public final WeaponRank animaRank;
		public final WeaponRank lightRank;
		public final WeaponRank darkRank;
		public final WeaponRank staffRank;
		
		public WeaponRanks(GBAFECharacterData character, GBAFEItemProvider provider) {
			swordRank = provider.rankWithValue(character.getSwordRank());
			lanceRank = provider.rankWithValue(character.getLanceRank());
			axeRank = provider.rankWithValue(character.getAxeRank());
			bowRank = provider.rankWithValue(character.getBowRank());
			animaRank = provider.rankWithValue(character.getAnimaRank());
			lightRank = provider.rankWithValue(character.getLightRank());
			darkRank = provider.rankWithValue(character.getDarkRank());
			staffRank = provider.rankWithValue(character.getStaffRank());
		}
		
		public WeaponRanks(GBAFEClassData characterClass, GBAFEItemProvider provider) {
			swordRank = provider.rankWithValue(characterClass.getSwordRank());
			lanceRank = provider.rankWithValue(characterClass.getLanceRank());
			axeRank = provider.rankWithValue(characterClass.getAxeRank());
			bowRank = provider.rankWithValue(characterClass.getBowRank());
			animaRank = provider.rankWithValue(characterClass.getAnimaRank());
			lightRank = provider.rankWithValue(characterClass.getLightRank());
			darkRank = provider.rankWithValue(characterClass.getDarkRank());
			staffRank = provider.rankWithValue(characterClass.getStaffRank());
		}
	}
	
	public long itemTablePointer();
	public int numberOfItems();
	public int bytesPerItem();
	
	public GBAFEItem[] allItems();
	
	public GBAFEItem itemWithID(int itemID);
	public GBAFEItem basicWeaponOfType(WeaponType type);
	
	public WeaponRank rankWithValue(int value);
	public int rankValueForRank(WeaponRank rank);
	
	public int getHighestWeaponRankValue();
	
	public Set<GBAFEItem> allWeapons();
	public Set<GBAFEItem> weaponsWithStatBoosts();
	public Set<GBAFEItem> weaponsWithEffectiveness();
	public Set<GBAFEItem> weaponsOfTypeUpToRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee);
	public Set<GBAFEItem> weaponsOfTypeAndEqualRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee, Boolean allowLower);
	public Set<GBAFEItem> prfWeaponsForClassID(int classID);
	public Set<GBAFEItem> allPotentialChestRewards();
	public Set<GBAFEItem> relatedItemsToItem(GBAFEItemData item);
	public Set<GBAFEItem> weaponsLockedToClass(int classID);
	public Set<GBAFEItem> weaponsForClass(int classID);
	public Set<GBAFEItem> basicWeaponsForClass(int classID);
	public Set<GBAFEItem> comparableWeaponsForClass(int classID, WeaponRanks ranks, GBAFEItemData originalItem, boolean strict);
	public Set<GBAFEItem> formerThiefInventory();
	public Set<GBAFEItem> thiefItemsToRemove();
	public Set<GBAFEItem> itemKitForSpecialClass(int classID, Random rng);
	
	public String statBoostStringForWeapon(GBAFEItem weapon);
	public String effectivenessStringForWeapon(GBAFEItem weapon, Boolean shortString);
	
	public GBAFEItemData itemDataWithData(byte[] data, long offset, int itemID); // itemID is required for FE8
	
	public List<GBAFEClass> knightCavEffectivenessClasses();
	public List<GBAFEClass> knightEffectivenessClasses();
	public List<GBAFEClass> cavalryEffectivenessClasses();
	public List<GBAFEClass> dragonEffectivenessClasses();
	public List<GBAFEClass> flierEffectivenessClasses();
	public List<GBAFEClass> myrmidonEffectivenessClasses(); // FE7 and FE8
	public List<GBAFEClass> monsterEffectivenessClasses(); // FE8 only
	
	public GBAFEPromotionItem[] allPromotionItems();
	
	public List<GBAFEClass> additionalClassesForPromotionItem(GBAFEPromotionItem promotionItem, List<Byte> existingClassIDs);
	
	public long spellAnimationTablePointer();
	public int numberOfAnimations();
	public int bytesPerAnimation();
	public GBAFESpellAnimationCollection spellAnimationCollectionAtAddress(byte[] data, long offset);

}
