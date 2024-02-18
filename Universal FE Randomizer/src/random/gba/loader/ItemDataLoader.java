package random.gba.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFESpellAnimationCollection;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEItem;
import fedata.gba.general.GBAFEItemProvider;
import fedata.gba.general.WeaponRanks;
import fedata.gba.general.GBAFEPromotionItem;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import io.FileHandler;
import util.AddressRange;
import util.ByteArrayBuilder;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FindAndReplace;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.RecordKeeper;

public class ItemDataLoader {
	private GBAFEItemProvider provider;

	public enum AdditionalData {
		STR_MAG_BOOST("STRMAG"), SKL_BOOST("SKL"), SPD_BOOST("SPD"), DEF_BOOST("DEF"), RES_BOOST("RES"), LCK_BOOST("LCK"),
		
		KNIGHTCAV_EFFECT("EFF_KNIGHT_CAV"), KNIGHT_EFFECT("EFF_KNIGHT"), DRAGON_EFFECT("EFF_DRAGON"), CAVALRY_EFFECT("EFF_CAVALRY"), MYRMIDON_EFFECT("EFF_MYRMIDON"), FLIERS_EFFECT("EFF_FLIER"), MONSTER_EFFECT("EFF_MONSTER");
		
		String key;
		
		private AdditionalData(String key) {
			this.key = key;
		}
	}
	
	private Map<Integer, GBAFEItemData> itemMap = new HashMap<Integer, GBAFEItemData>();
	private Map<Integer, GBAFEItemData> addedItems = new HashMap<Integer, GBAFEItemData>();
	
	private long originalTableOffset;
	
	// TODO: Put this somewhere else.
	public GBAFESpellAnimationCollection spellAnimations;
	
	private FreeSpaceManager freeSpace;
	private Map<AdditionalData, Long> offsetsForAdditionalData;
	private Map<AdditionalData, List<Byte>> additionalDataMap;
	private Map<String, Long> promotionItemAddressPointers;
	private Map<String, List<Byte>> promotionClassLists;
	private Map<Integer, List<GBAFEPromotionItem>> promotionItemsForClassIDs;
	
	public static final String RecordKeeperCategoryWeaponKey = "Weapons";
	
	public ItemDataLoader(GBAFEItemProvider provider, FileHandler handler, FreeSpaceManager freeSpace) {
		super();
		
		this.freeSpace = freeSpace;
		this.provider = provider;
		
		long baseAddress = FileReadHelper.readAddress(handler, provider.itemTablePointer());
		originalTableOffset = baseAddress;
		for (GBAFEItem item : provider.allItems()) {
			if (item.getID() == 0) { continue; }
			
			long offset = baseAddress + (provider.bytesPerItem() * item.getID());
			byte[] itemData = handler.readBytesAtOffset(offset, provider.bytesPerItem());
			itemMap.put(item.getID(), provider.itemDataWithData(itemData, offset, item.getID()));
		}
		
		long spellAnimationBaseAddress = FileReadHelper.readAddress(handler, provider.spellAnimationTablePointer());
		byte[] spellAnimationData = handler.readBytesAtOffset(spellAnimationBaseAddress, provider.numberOfAnimations() * provider.bytesPerAnimation());
		spellAnimations = provider.spellAnimationCollectionAtAddress(spellAnimationData, spellAnimationBaseAddress);
		
		offsetsForAdditionalData = new HashMap<AdditionalData, Long>();
		additionalDataMap = new HashMap<AdditionalData, List<Byte>>();
		
		// Set up effectiveness.
		registerAdditionalData(AdditionalData.KNIGHTCAV_EFFECT, 
				classByteArrayFromClassList(provider.knightCavEffectivenessClasses()));
		registerAdditionalData(AdditionalData.KNIGHT_EFFECT,
				classByteArrayFromClassList(provider.knightEffectivenessClasses()));
		registerAdditionalData(AdditionalData.CAVALRY_EFFECT, 
				classByteArrayFromClassList(provider.cavalryEffectivenessClasses()));
		registerAdditionalData(AdditionalData.DRAGON_EFFECT,
				classByteArrayFromClassList(provider.dragonEffectivenessClasses()));
		registerAdditionalData(AdditionalData.MYRMIDON_EFFECT,
				classByteArrayFromClassList(provider.myrmidonEffectivenessClasses()));
		registerAdditionalData(AdditionalData.FLIERS_EFFECT,
				classByteArrayFromClassList(provider.flierEffectivenessClasses()));
		registerAdditionalData(AdditionalData.MONSTER_EFFECT,
				classByteArrayFromClassList(provider.monsterEffectivenessClasses()));
		
		// Set up stat boosts.
		long offset = freeSpace.setValue(new byte[] {0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.STR_MAG_BOOST.key);
		offsetsForAdditionalData.put(AdditionalData.STR_MAG_BOOST, offset);
		offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.SKL_BOOST.key);
		offsetsForAdditionalData.put(AdditionalData.SKL_BOOST, offset);
		offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00}, AdditionalData.SPD_BOOST.key);
		offsetsForAdditionalData.put(AdditionalData.SPD_BOOST, offset);
		offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00}, AdditionalData.DEF_BOOST.key);
		offsetsForAdditionalData.put(AdditionalData.DEF_BOOST, offset);
		offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00}, AdditionalData.RES_BOOST.key);
		offsetsForAdditionalData.put(AdditionalData.RES_BOOST, offset);
		offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00}, AdditionalData.LCK_BOOST.key);
		offsetsForAdditionalData.put(AdditionalData.LCK_BOOST, offset);
		
		// Set up promotion items.
		promotionItemAddressPointers = new HashMap<String, Long>();
		promotionClassLists = new HashMap<String, List<Byte>>();
		promotionItemsForClassIDs = new HashMap<Integer, List<GBAFEPromotionItem>>();
		for (GBAFEPromotionItem promotionItem : provider.allPromotionItems()) {
			long promotionItemOffset = promotionItem.getListAddress();
			if (promotionItem.isIndirected()) {
				promotionItemOffset = FileReadHelper.readAddress(handler, promotionItemOffset) + 4;
			}
			
			List<Byte> idList = new ArrayList<Byte>();
			byte currentByte = 0x0;
			long currentOffset = FileReadHelper.readAddress(handler, promotionItemOffset); // One more jump here.
			do {
				currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
				if (currentByte != 0) {
					idList.add(currentByte);
					
					List<GBAFEPromotionItem> promotionItems = promotionItemsForClassIDs.get((int)currentByte & 0xFF);
					if (promotionItems == null) { 
						promotionItems = new ArrayList<GBAFEPromotionItem>();
						promotionItemsForClassIDs.put((int)currentByte & 0xFF, promotionItems);
					}
					promotionItems.add(promotionItem);
				}
			} while (currentByte != 0);
			
			List<GBAFEClass> additionalClasses = provider.additionalClassesForPromotionItem(promotionItem, idList);
			if (additionalClasses != null && !additionalClasses.isEmpty()) {
				for (int i = 0; i < additionalClasses.size(); i++) {
					GBAFEClass additionalClass = additionalClasses.get(i);
					if (!idList.contains((byte)additionalClass.getID())) { 
						idList.add((byte)additionalClass.getID());
						List<GBAFEPromotionItem> promotionItems = promotionItemsForClassIDs.get((int)currentByte & 0xFF);
						if (promotionItems == null) { 
							promotionItems = new ArrayList<GBAFEPromotionItem>();
							promotionItemsForClassIDs.put((int)currentByte & 0xFF, promotionItems);
						}
						promotionItems.add(promotionItem);
					}
				}
			}
			
			promotionClassLists.put(promotionItem.itemName(), idList);
			promotionItemAddressPointers.put(promotionItem.itemName(), promotionItemOffset);
		}
	}
	
	public void prepareForRandomization() {
		// Translate existing effectiveness pointers to our new ones.
		for (GBAFEItemData itemData : itemMap.values()) {
			long ptr = itemData.getEffectivenessPointer();
			if (ptr == 0) { continue; }
			ptr -= 0x8000000L;
			AdditionalData effectivenessType = provider.effectivenessPointerType(ptr);
			if (effectivenessType != null) {
				long newPtr = offsetsForAdditionalData.get(effectivenessType);
				newPtr += 0x8000000L;
				itemData.setEffectivenessPointer(newPtr);
			}
		}
	}
	
	public void addClassToPromotionItem(GBAFEPromotionItem promotionItem, int classID) {
		if (promotionItem == null) { return; }
		List<Byte> idList = promotionClassLists.get(promotionItem.itemName());
		if (idList == null) { return; }
		idList.add((byte)(classID & 0xFF));
		
		List<GBAFEPromotionItem> promotionItems = promotionItemsForClassIDs.get(classID & 0xFF);
		if (promotionItems == null) { 
			promotionItems = new ArrayList<GBAFEPromotionItem>();
			promotionItemsForClassIDs.put(classID & 0xFF, promotionItems);
		}
		promotionItems.add(promotionItem);
	}
	
	public void replaceClassesForPromotionItem(GBAFEPromotionItem promotionItem, List<Integer> classIDs) {
		if (classIDs == null || promotionItem == null) { return; }
		List<Byte> oldList = promotionClassLists.get(promotionItem.itemName());
		if (oldList != null && oldList.size() > 0) {
			for (Byte id : oldList) {
				List<GBAFEPromotionItem> promotionItems = promotionItemsForClassIDs.get((int)id & 0xFF);
				if (promotionItems != null) { 
					promotionItems.remove(promotionItem);
				}
			}
		}
		
		List<Byte> idList = new ArrayList<Byte>();
		for (int id : classIDs) {
			idList.add((byte)(id & 0xFF));
			List<GBAFEPromotionItem> items = promotionItemsForClassIDs.get(id);
			if (items == null) {
				items = new ArrayList<GBAFEPromotionItem>();
				promotionItemsForClassIDs.put(id, items);
			}
			items.add(promotionItem);
		}
		promotionClassLists.put(promotionItem.itemName(), idList);
	}
	
	public List<GBAFEItemData> itemsToPromoteClass(int classID) {
		List<GBAFEPromotionItem> promotionItems = promotionItemsForClassIDs.get(classID);
		List<GBAFEItemData> items = new ArrayList<GBAFEItemData>();
		if (promotionItems == null) { return items; }
		for (GBAFEPromotionItem item : promotionItems) {
			GBAFEItemData itemData = itemWithID(item.getItemID());
			if (itemData != null) {
				items.add(itemData);
			}
		}
		
		return items;
	}
	
	public List<AdditionalData> effectivenessArraysForClassID(int classID) {
		List<AdditionalData> datanames = new ArrayList<AdditionalData>();
		
		List<Byte> registeredByteArray = additionalDataMap.get(AdditionalData.CAVALRY_EFFECT);
		if (registeredByteArray != null && registeredByteArray.contains((byte)classID)) { datanames.add(AdditionalData.CAVALRY_EFFECT); }
		registeredByteArray = additionalDataMap.get(AdditionalData.DRAGON_EFFECT);
		if (registeredByteArray != null && registeredByteArray.contains((byte)classID)) { datanames.add(AdditionalData.DRAGON_EFFECT); }
		registeredByteArray = additionalDataMap.get(AdditionalData.FLIERS_EFFECT);
		if (registeredByteArray != null && registeredByteArray.contains((byte)classID)) { datanames.add(AdditionalData.FLIERS_EFFECT); }
		registeredByteArray = additionalDataMap.get(AdditionalData.KNIGHT_EFFECT);
		if (registeredByteArray != null && registeredByteArray.contains((byte)classID)) { datanames.add(AdditionalData.KNIGHT_EFFECT); }
		registeredByteArray = additionalDataMap.get(AdditionalData.KNIGHTCAV_EFFECT);
		if (registeredByteArray != null && registeredByteArray.contains((byte)classID)) { datanames.add(AdditionalData.KNIGHTCAV_EFFECT); }
		registeredByteArray = additionalDataMap.get(AdditionalData.MONSTER_EFFECT);
		if (registeredByteArray != null && registeredByteArray.contains((byte)classID)) { datanames.add(AdditionalData.MONSTER_EFFECT); }
		registeredByteArray = additionalDataMap.get(AdditionalData.MYRMIDON_EFFECT);
		if (registeredByteArray != null && registeredByteArray.contains((byte)classID)) { datanames.add(AdditionalData.MYRMIDON_EFFECT); }
		
		return datanames;
	}
	
	public void addClassIDToEffectiveness(AdditionalData dataName, int classID) {
		List<Byte> registeredByteArray = null;
		switch (dataName) {
		case CAVALRY_EFFECT:
		case DRAGON_EFFECT:
		case FLIERS_EFFECT:
		case KNIGHT_EFFECT:
		case KNIGHTCAV_EFFECT:
		case MONSTER_EFFECT:
		case MYRMIDON_EFFECT:
			registeredByteArray = additionalDataMap.get(dataName);
			break;
		default:
			break;
		}
		
		if (registeredByteArray != null && registeredByteArray.size() > 0) {
			int firstEmptySpot = registeredByteArray.indexOf((byte)0x0);
			if (firstEmptySpot == -1 || firstEmptySpot >= registeredByteArray.size() - 1) {
				// We're out of space (somehow?).
				return;
			}
			
			registeredByteArray.remove(firstEmptySpot);
			registeredByteArray.add(firstEmptySpot, (byte)(classID & 0xFF));
			byte[] updated = WhyDoesJavaNotHaveThese.byteArrayFromByteList(registeredByteArray);
			
			freeSpace.setValue(updated, dataName.key);
		}
	}
	
	public byte[] bytesForAdditionalData(AdditionalData dataName) {
		if (additionalDataMap.containsKey(dataName)) {
			List<Byte> byteList = additionalDataMap.get(dataName);
			ByteArrayBuilder builder = new ByteArrayBuilder();
			for (Byte current : byteList) {
				assert current != null;
				if (current == null) { return null; }
				builder.appendByte(current);
			}
			return builder.toByteArray();
		}
		
		return null;
	}
	
	private void registerAdditionalData(AdditionalData dataName, byte[] byteArray) {
		if (byteArray.length > 0) {
			long offset = freeSpace.setValue(byteArray, dataName.key);
			offsetsForAdditionalData.put(dataName, offset);
			additionalDataMap.put(dataName, WhyDoesJavaNotHaveThese.byteArrayToByteList(byteArray));
		}
	}
	
	private byte[] classByteArrayFromClassList(List<GBAFEClass> classList) {
		if (classList == null || classList.size() == 0) { return new byte[] {}; }
		
		Boolean addTerminal = false;
		if (classList.get(classList.size() - 1).getID() != 0) {
			addTerminal = true;
		}
		
		byte[] byteArray = new byte[classList.size() + (addTerminal ? 1 : 0)];
		for (int i = 0; i < classList.size(); i++) {
			byteArray[i] = (byte)(classList.get(i).getID() & 0xFF);
		}
		
		if (addTerminal) {
			byteArray[byteArray.length - 1] = 0x0;
		}
		
		return byteArray;
	}
	
	public void addNewItem(GBAFEItemData newItem) {
		if (newItem != null) {
			addedItems.put(newItem.getID(), newItem);
		}
	}
	
	public GBAFEItemData itemWithID(int itemID) {
		return itemMap.get(itemID);
	}
	
	public int getHighestWeaponRank() {
		return provider.getHighestWeaponRankValue();
	}
	
	public WeaponRank rankForValue(int value) {
		return provider.rankWithValue(value);
	}
	
	public WeaponRanks ranksForCharacter(GBAFECharacterData character, GBAFEClassData charClass) {
		return new WeaponRanks(character, charClass);
	}
	
	public WeaponRanks ranksForClass(GBAFEClassData charClass) {
		return new WeaponRanks(charClass);
	}
	
	public GBAFEItemData[] getAllWeapons() {
		return feItemsFromItemSet(provider.allWeapons());
	}
	
	public long[] possibleStatBoostAddresses() {
		return new long[] { offsetsForAdditionalData.get(AdditionalData.STR_MAG_BOOST),
				offsetsForAdditionalData.get(AdditionalData.SKL_BOOST),
				offsetsForAdditionalData.get(AdditionalData.SPD_BOOST),
				offsetsForAdditionalData.get(AdditionalData.DEF_BOOST),
				offsetsForAdditionalData.get(AdditionalData.RES_BOOST),
				offsetsForAdditionalData.get(AdditionalData.LCK_BOOST)};
	}
	
	public String descriptionStringForAddress(long address, Boolean isMagic, Boolean shortForm) {
		if (offsetsForAdditionalData.get(AdditionalData.STR_MAG_BOOST) == address) { return isMagic ? "+5 Magic" : "+5 Strength"; }
		if (offsetsForAdditionalData.get(AdditionalData.SKL_BOOST) == address) { return "+5 Skill"; }
		if (offsetsForAdditionalData.get(AdditionalData.SPD_BOOST) == address) { return "+5 Speed"; }
		if (offsetsForAdditionalData.get(AdditionalData.LCK_BOOST) == address) { return "+5 Luck"; }
		if (offsetsForAdditionalData.get(AdditionalData.DEF_BOOST) == address) { return "+5 Defense"; }
		if (offsetsForAdditionalData.get(AdditionalData.RES_BOOST) == address) { return "+5 Resistance"; }
		
		if (offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT) == address) { return shortForm ? "Eff. Knights" : "Effective against knights"; }
		if (offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT) == address) { return shortForm ? "Eff. Infantry" : "Effective against infantry"; }
		if (offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT) == address) { return shortForm ? "Eff. Cavalry" : "Effective against cavalry"; }
		if (offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT) == address) { return shortForm ? "Eff. Fliers" : "Effective against fliers"; }
		if (offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT) != null && address == offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT)) { return shortForm ? "Eff. Swordfighters" : "Effective against swordfighters"; }
		if (offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT) == address) { return shortForm ? "Eff. Dragons" : "Effective against dragons"; }
		
		for (GBAFEItem weapon : provider.weaponsWithStatBoosts()) {
			if (address == itemMap.get(weapon.getID()).getStatBonusPointer()) {
				return provider.statBoostStringForWeapon(weapon);
			}
		}
		
		for (GBAFEItem weapon : provider.weaponsWithEffectiveness()) {
			if (address == itemMap.get(weapon.getID()).getEffectivenessPointer()) {
				return provider.effectivenessStringForWeapon(weapon, shortForm);
			}
		}
		
		return null;
	}
	
	public long flierEffectPointer() {
		if (offsetsForAdditionalData.containsKey(AdditionalData.FLIERS_EFFECT)) {
			return offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT);
		}
		
		return 0;
	}
	
	public long offsetForAdditionalData(AdditionalData name) {
		return offsetsForAdditionalData.get(name);
	}
	
	public long[] possibleEffectivenessAddresses() {
		List<Long> registeredEffectivenessPointers = new ArrayList<Long>();
		if (offsetsForAdditionalData.containsKey(AdditionalData.KNIGHTCAV_EFFECT)) {
			registeredEffectivenessPointers.add(offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT));
		}
		if (offsetsForAdditionalData.containsKey(AdditionalData.KNIGHT_EFFECT)) {
			registeredEffectivenessPointers.add(offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT));
		}
		if (offsetsForAdditionalData.containsKey(AdditionalData.CAVALRY_EFFECT)) {
			registeredEffectivenessPointers.add(offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT));
		}
		if (offsetsForAdditionalData.containsKey(AdditionalData.DRAGON_EFFECT)) {
			registeredEffectivenessPointers.add(offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT));
		}
		if (offsetsForAdditionalData.containsKey(AdditionalData.FLIERS_EFFECT)) {
			registeredEffectivenessPointers.add(offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT));
		}
		if (offsetsForAdditionalData.containsKey(AdditionalData.MYRMIDON_EFFECT)) {
			registeredEffectivenessPointers.add(offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT));
		}
		if (offsetsForAdditionalData.containsKey(AdditionalData.MONSTER_EFFECT)) {
			registeredEffectivenessPointers.add(offsetsForAdditionalData.get(AdditionalData.MONSTER_EFFECT));
		}
		
		long[] result = new long[registeredEffectivenessPointers.size()];
		for (int i = 0; i < registeredEffectivenessPointers.size(); i++) {
			result[i] = registeredEffectivenessPointers.get(i);
		}
		
		return result;
	}
	
	public Boolean isWeapon(GBAFEItemData item) {
		if (item == null) { return false; }
		return provider.itemWithID(item.getID()).isWeapon();
	}
	
	public Boolean isBasicWeapon(int itemID) {
		return provider.itemWithID(itemID).isBasicWeapon();
	}
	
	public Boolean isSteelWeapon(int itemID) {
		return provider.itemWithID(itemID).isSteelWeapon();
	}
	
	public Boolean isBasicThrowingWeapon(int itemID) {
		return provider.itemWithID(itemID).isBasicThrownWeapon();
	}
	
	public GBAFEItemData basicItemOfType(WeaponType type) {
		GBAFEItem basicItem = provider.basicWeaponOfType(type);
		if (basicItem != null) { return itemMap.get(basicItem.getID()); }
		return null;
	}
	
	public GBAFEItemData[] itemsOfTypeAndBelowRankValue(WeaponType type, int rankValue, Boolean rangedOnly, Boolean requiresMelee) {
		return itemsOfTypeAndBelowRank(type, provider.rankWithValue(rankValue), rangedOnly, requiresMelee);
	}
	
	public GBAFEItemData[] itemsOfTypeAndBelowRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee) {
		return feItemsFromItemSet(provider.weaponsOfTypeUpToRank(type, rank, rangedOnly, requiresMelee));
	}
	
	public GBAFEItemData[] itemsOfTypeAndEqualRankValue(WeaponType type, int rankValue, Boolean rangedOnly, Boolean requiresMelee, Boolean allowLower) {
		return itemsOfTypeAndEqualRank(type, provider.rankWithValue(rankValue), rangedOnly, requiresMelee, allowLower);
	}
	
	public int weaponRankValueForRank(WeaponRank rank) {
		return provider.rankValueForRank(rank);
	}
	
	public GBAFEItemData[] itemsOfTypeAndEqualRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee, Boolean allowLower) {
		return feItemsFromItemSet(provider.weaponsOfTypeAndEqualRank(type, rank, rangedOnly, requiresMelee, allowLower));
	}
	
	public GBAFEItemData[] prfWeaponsForClass(int classID) {
		return feItemsFromItemSet(provider.prfWeaponsForClassID(classID));
	}
	
	public GBAFEItemData[] getChestRewards(boolean includePromoWeapons) {
		Set<GBAFEItem> rewards = provider.allPotentialChestRewards();
		if (!includePromoWeapons) {
			rewards.removeAll(provider.promoWeapons());
		}
		return feItemsFromItemSet(rewards);
	}
	
	public GBAFEItemData[] commonDrops() {
		return feItemsFromItemSet(provider.commonDrops());
	}
	
	public GBAFEItemData[] uncommonDrops() {
		return feItemsFromItemSet(provider.uncommonDrops());
	}
	
	public GBAFEItemData[] rareDrops() {
		return feItemsFromItemSet(provider.rareDrops());
	}
	
	public GBAFEItemData[] relatedItems(int itemID) {
		return feItemsFromItemSet(provider.relatedItemsToItem(itemMap.get(itemID)));
	}
	
	public GBAFEItemData[] lockedWeaponsToClass(int classID) {
		return feItemsFromItemSet(provider.weaponsLockedToClass(classID));
	}
	
	public boolean isPlayerOnly(int itemID) {
		return provider.playerOnlyWeapons().stream().anyMatch(item -> item.getID() == itemID);
	}
	
	public Boolean isHealingStaff(int itemID) {
		return provider.itemWithID(itemID) != null ? provider.itemWithID(itemID).isHealingStaff() : false;
	}
	
	public Boolean isPromoWeapon(int itemID) {
		return provider.itemWithID(itemID) != null ? provider.itemWithID(itemID).isPromoWeapon() : false;
	}
	
	public Boolean isPoisonWeapon(int itemID) {
		return provider.itemWithID(itemID) != null ? provider.itemWithID(itemID).isPoisonWeapon() : false;
	}
	
	public WeaponRank weaponRankFromValue(int rankValue) {
		return provider.rankWithValue(rankValue);
	}
	
	public GBAFEItemData getRandomHealingStaff(WeaponRank maxRank, Random rng) {
		Set<GBAFEItem> healingStaves = provider.healingStaves(maxRank);
		GBAFEItem[] staves = healingStaves.toArray(new GBAFEItem[healingStaves.size()]);
		return itemMap.get(staves[rng.nextInt(staves.length)].getID());
	}
	
	public GBAFEItemData getBasicWeaponForCharacter(GBAFECharacterData character, Boolean ranged, Boolean mustAttack, Random rng) {
		int classID = character.getClassID();
		Set<GBAFEItem> weapons = provider.basicWeaponsForClass(classID);
		GBAFEItem[] weaponArray = weapons.toArray(new GBAFEItem[weapons.size()]);
		if (weapons.size() == 1) { return itemMap.get(weaponArray[0].getID()); }
		else if (weapons.isEmpty()) { return null; }
		return itemMap.get(weaponArray[rng.nextInt(weapons.size())].getID());
	}
	
	public GBAFEItemData getSidegradeWeapon(GBAFEClassData targetClass, GBAFEItemData originalWeapon, boolean strict, boolean includePromo, boolean includePoison, Random rng) {
		if (!isWeapon(originalWeapon) && originalWeapon.getType() != WeaponType.STAFF) {
			return null;
		}
		
		Set<GBAFEItem> potentialItems = provider.comparableWeaponsForClass(targetClass.getID(), new WeaponRanks(targetClass), originalWeapon, strict);
		if (!includePromo) {
			potentialItems.removeAll(provider.promoWeapons());
		}
		
		if (!includePoison) {
			potentialItems.removeAll(provider.poisonWeapons());
		}
		if (potentialItems.isEmpty()) { 
			potentialItems = provider.basicWeaponsForClass(targetClass.getID());
			
			if (potentialItems.isEmpty()) {
				return null;
			}
		}
		
		// No minion should be getting any player only weapons.
		potentialItems.removeAll(provider.playerOnlyWeapons());
		
		List<GBAFEItem> itemList = potentialItems.stream().sorted(GBAFEItem.defaultComparator()).collect(Collectors.toList());
		if (itemList.isEmpty()) {
			// Fall back to basic weapons.
			itemList = provider.basicWeaponsForClass(targetClass.getID()).stream().sorted(GBAFEItem.defaultComparator()).collect(Collectors.toList());
			if (itemList.isEmpty()) { return null; }
		}
		return itemMap.get(itemList.get(rng.nextInt(itemList.size())).getID());
	}
	
	public GBAFEItemData getSidegradeWeapon(GBAFECharacterData character, GBAFEClassData charClass, GBAFEItemData originalWeapon, boolean isEnemy, boolean strict, boolean includePromo, boolean includePoison, Random rng) {
		if (!isWeapon(originalWeapon) && originalWeapon.getType() != WeaponType.STAFF) {
			return null;
		}
		
		Set<GBAFEItem> potentialItems = provider.comparableWeaponsForClass(character.getClassID(), new WeaponRanks(character, charClass), originalWeapon, strict);
		if (!includePromo) {
			potentialItems.removeAll(provider.promoWeapons());
		}
		if (!includePoison) {
			potentialItems.removeAll(provider.poisonWeapons());
		}
		if (potentialItems.isEmpty()) { 
			potentialItems = provider.basicWeaponsForClass(character.getClassID());
			
			if (potentialItems.isEmpty()) {
				return null;
			}
		}
		
		if (isEnemy) {
			potentialItems.removeAll(provider.playerOnlyWeapons());
		}
	
		if (potentialItems.isEmpty()) {
			return null;
		}
		
		List<GBAFEItem> itemList = potentialItems.stream().sorted(GBAFEItem.defaultComparator()).collect(Collectors.toList());
		return itemMap.get(itemList.get(rng.nextInt(itemList.size())).getID());
	}
	
	public GBAFEItemData getPrfWeaponForClass(int classID) {
		Set<GBAFEItem> prfs = provider.prfWeaponsForClassID(classID);
		GBAFEItem item = prfs.stream().min(new Comparator<GBAFEItem>() {
			@Override
			public int compare(GBAFEItem arg0, GBAFEItem arg1) {
				return Integer.compare(arg0.getID(), arg1.getID());
			}
		}).orElse(null);
		return item != null ? itemWithID(item.getID()) : null;
	}
	
	public GBAFEItemData getRandomWeaponForCharacter(GBAFECharacterData character, Boolean ranged, Boolean melee, boolean isEnemy, boolean includePromo, boolean includePoison, Random rng) {
		GBAFEItemData[] potentialItems = usableWeaponsForCharacter(character, ranged, melee, isEnemy, includePromo, includePoison);
		if (potentialItems == null || potentialItems.length < 1) {
			// Check class specific weapons (e.g. FE8 monsters)
			Set<GBAFEItem> classWeaponSet = provider.weaponsForClass(character.getClassID());
			if (!includePromo) {
				classWeaponSet.removeAll(provider.promoWeapons());
				classWeaponSet.removeAll(provider.poisonWeapons());
			}
			potentialItems = feItemsFromItemSet(classWeaponSet);
			if (potentialItems == null || potentialItems.length < 1) {
				return null;
			}
		}
		
		int index = rng.nextInt(potentialItems.length);
		return potentialItems[index];
	}
	
	private GBAFEItemData[] usableWeaponsForCharacter(GBAFECharacterData character, Boolean ranged, Boolean melee, boolean isEnemy, boolean includePromo, boolean includePoison) {
		ArrayList<GBAFEItemData> items = new ArrayList<GBAFEItemData>();
		
		if (character.getSwordRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.SWORD, character.getSwordRank(), ranged, melee))); }
		if (character.getLanceRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.LANCE, character.getLanceRank(), ranged, melee))); }
		if (character.getAxeRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.AXE, character.getAxeRank(), ranged, melee))); }
		if (character.getBowRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.BOW, character.getBowRank(), ranged, melee))); }
		if (character.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.ANIMA, character.getAnimaRank(), ranged, melee))); }
		if (character.getLightRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.LIGHT, character.getLightRank(), ranged, melee))); }
		if (character.getDarkRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.DARK, character.getDarkRank(), ranged, melee))); }
		if (character.getStaffRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.STAFF, character.getStaffRank(), ranged, melee))); }
		
		Set<GBAFEItem> prfs = provider.prfWeaponsForClassID(character.getClassID());
		items.addAll(Arrays.asList(feItemsFromItemSet(prfs)));
		
		if (isEnemy) {
			items.removeIf(item -> provider.playerOnlyWeapons().contains(provider.itemWithID(item.getID())));
		}
		
		if (!includePromo) {
			items.removeIf(item -> provider.promoWeapons().contains(provider.itemWithID(item.getID())));
		}
		
		if (!includePoison) {
			items.removeIf(item -> provider.poisonWeapons().contains(provider.itemWithID(item.getID())));
		}
		
		return items.toArray(new GBAFEItemData[items.size()]);
	}
	
	public GBAFEItemData[] formerThiefInventory() {
		return feItemsFromItemSet(provider.formerThiefInventory());
	}
	
	public GBAFEItemData[] thiefItemsToRemove() {
		return feItemsFromItemSet(provider.thiefItemsToRemove());
	}
	
	public GBAFEItemData[] specialItemsToRetain() {
		return feItemsFromItemSet(provider.specialItemsToRetain());
	}
	
	public GBAFEItemData[] specialInventoryForClass(int classID, Random rng) {
		return feItemsFromItemSet(provider.itemKitForSpecialClass(classID, rng));
	}
	
	public void commit() {
		for (GBAFEItemData item : itemMap.values()) {
			item.commitChanges();
		}
		
		spellAnimations.commit();
	}
	
	public void compileDiffs(DiffCompiler compiler, FileHandler handler) {
		if (addedItems.isEmpty()) {
			for (GBAFEItemData item : itemMap.values()) {
				item.commitChanges();
				if (item.hasCommittedChanges()) {
					Diff charDiff = new Diff(item.getAddressOffset(), item.getData().length, item.getData(), null);
					compiler.addDiff(charDiff);
				}
			}
		} else {
			// Need a repoint.
			
			// Commit everything first.
			for (GBAFEItemData item : itemMap.values()) {
				item.commitChanges();
			}
			
			for (GBAFEItemData item : addedItems.values()) {
				item.commitChanges();
			}
			
			long startingOffset = FileReadHelper.readAddress(handler, provider.itemTablePointer());
			long newTableOffset = 0;
			for (int i = 0; i < provider.numberOfItems(); i++) {
				GBAFEItemData item = itemMap.get(i);
				if (item != null) {
					long writtenOffset = freeSpace.setValue(item.getData(), "Item data for 0x" + Integer.toHexString(item.getID()), i == 0);
					if (i == 0) { newTableOffset = writtenOffset; }
				} else {
					long existingStart = startingOffset + i * provider.bytesPerItem();
					long existingEnd = existingStart + provider.bytesPerItem();
					long writtenOffset = freeSpace.setValue(FileReadHelper.readBytesInRange(new AddressRange(existingStart, existingEnd), handler), "Copied class data for Item 0x" + Integer.toHexString(i), i == 0);
					if (i == 0) { newTableOffset = writtenOffset; }
				}
			}
			
			List<GBAFEItemData> addedItemList = new ArrayList<GBAFEItemData>(addedItems.values());
			addedItemList.sort(new Comparator<GBAFEItemData>() {
				@Override
				public int compare(GBAFEItemData o1, GBAFEItemData o2) {
					return Integer.compare(o1.getID(), o2.getID());
				}
			});
			for (GBAFEItemData item : addedItemList) {
				freeSpace.setValue(item.getData(), "Added Item Data for Class 0x" + Integer.toHexString(item.getID()));
			}
			
			compiler.findAndReplace(new FindAndReplace(WhyDoesJavaNotHaveThese.gbaAddressFromOffset(originalTableOffset), WhyDoesJavaNotHaveThese.gbaAddressFromOffset(newTableOffset), true));
		}
		
		spellAnimations.compileDiffs(compiler, freeSpace);
		
		for (String promotionItemName : promotionItemAddressPointers.keySet()) {
			List<Byte> idBytes = promotionClassLists.get(promotionItemName);
			byte[] bytesToWrite = new byte[idBytes.size() + 1];
			for (int i = 0; i < idBytes.size(); i++) {
				bytesToWrite[i] = idBytes.get(i);
			}
			bytesToWrite[bytesToWrite.length - 1] = 0;
			
			long offset = freeSpace.setValue(bytesToWrite, promotionItemName);
			byte[] addressByteArray = WhyDoesJavaNotHaveThese.bytesFromAddress(offset);
			long targetOffset = promotionItemAddressPointers.get(promotionItemName);
			compiler.addDiff(new Diff(targetOffset, addressByteArray.length, addressByteArray, null));
		}
	}
	
	public List<GBAFEItemData> itemsByStatboostAddress(long address){
		List<GBAFEItemData> ret = new ArrayList<>();
		
		for (GBAFEItemData gbafeItemData : itemMap.values()) {
			if (gbafeItemData.getStatBonusPointer() == address + 0x08000000) {
				ret.add(gbafeItemData);
			}
		}
		return ret;
	}
	
	private GBAFEItemData[] feItemsFromItemSet(Set<GBAFEItem> itemSet) {
		if (itemSet == null) { return new GBAFEItemData[] {}; }
		
		List<GBAFEItem> itemList = new ArrayList<GBAFEItem>(itemSet);
		Collections.sort(itemList, GBAFEItem.defaultComparator());
		GBAFEItemData[] items = new GBAFEItemData[itemList.size()];
		for (int i = 0; i < itemList.size(); i++) {
			items[i] = itemWithID(itemList.get(i).getID());
		}
		
		return items;
	}
	
	public void recordWeapons(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, TextLoader textData, FileHandler handler) {
		for (GBAFEItemData item : getAllWeapons()) {
			recordWeapon(rk, item, isInitial, classData, textData, handler);
		}
	}
	
	private void recordWeapon(RecordKeeper rk, GBAFEItemData item, Boolean isInitial, ClassDataLoader classData, TextLoader textData, FileHandler handler) {
		int nameIndex = item.getNameIndex();
		String name = textData.getStringAtIndex(nameIndex, true).trim();
		int descriptionIndex = item.getDescriptionIndex();
		String description = textData.getStringAtIndex(descriptionIndex, true).trim();
		
		if (isInitial) {
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Description", description);
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Power (MT)", String.format("%d", item.getMight()));
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Accuracy (Hit)", String.format("%d", item.getHit()));
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Weight (WT)", String.format("%d", item.getWeight()));
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Durability", String.format("%d", item.getDurability()));
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Critical", String.format("%d",  item.getCritical()));
			
			long statPointerAddress = item.getStatBonusPointer();
			if (statPointerAddress != 0) {
				statPointerAddress -= 0x8000000;
				if (handler != null) {
					byte[] bonuses = handler.readBytesAtOffset(statPointerAddress, 7);
					List<String> bonusStrings = new ArrayList<String>();
					if (bonuses[0] > 0) { bonusStrings.add("+" + bonuses[0] + " HP"); }
					if (bonuses[1] > 0) { bonusStrings.add("+" + bonuses[1] + ((item.getType() == WeaponType.ANIMA || item.getType() == WeaponType.LIGHT || item.getType() == WeaponType.DARK) ? " Magic" : " Strength")); }
					if (bonuses[2] > 0) { bonusStrings.add("+" + bonuses[2] + " Skill"); }
					if (bonuses[3] > 0) { bonusStrings.add("+" + bonuses[3] + " Speed"); }
					if (bonuses[4] > 0) { bonusStrings.add("+" + bonuses[4] + " Defense"); }
					if (bonuses[5] > 0) { bonusStrings.add("+" + bonuses[5] + " Resistance"); }
					if (bonuses[6] > 0) { bonusStrings.add("+" + bonuses[6] + " Luck"); }
					
					rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", String.join("<br>", bonusStrings));
				} else {
					rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", "No input handler.");
				}
			} else {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", "None");
			}
			
			long effectiveClasses = item.getEffectivenessPointer();
			if (effectiveClasses != 0) {
				effectiveClasses -= 0x8000000;
				if (handler != null) {
					handler.setNextReadOffset(effectiveClasses);
					byte[] classes = handler.continueReadingBytesUpToNextTerminator(effectiveClasses + 100);
					List<String> classList = new ArrayList<String>();
					for (byte classID : classes) {
						if (classID == 0) { break; }
						GBAFEClassData classObject = classData.classForID(classID);
						if (classObject == null) {
							classList.add("Unknown (0x" + Integer.toHexString(classID).toUpperCase() + ")");
						} else {
							if (classData.isFemale(classID)) {
								classList.add(textData.getStringAtIndex(classObject.getNameIndex(), true).trim() + " (F)");
							} else {
								classList.add(textData.getStringAtIndex(classObject.getNameIndex(), true).trim());
							}
						}
					}
					rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", String.join("<br>", classList));
				} else {
					rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "No input handler.");
				}
			} else {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "None");
			}
			
			if (item.hasAbility1()) {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", item.getAbility1Description("<br>"));
			}
			if (item.hasAbility2()) {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", item.getAbility2Description("<br>"));
			}
			if (item.hasAbility3()) {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 3", item.getAbility3Description("<br>"));
			}
			if (item.hasAbility4()) {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 4", item.getAbility4Description("<br>"));
			}
			if (item.hasWeaponEffect()) {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effect", item.getWeaponEffectDescription());
			}
			
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Range", String.format("%d ~ %d", item.getMinRange(), item.getMaxRange()));
			
		} else {
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Description", description);
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Power (MT)", String.format("%d", item.getMight()));
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Accuracy (Hit)", String.format("%d", item.getHit()));
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Weight (WT)", String.format("%d", item.getWeight()));
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Durability", String.format("%d", item.getDurability()));
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Critical", String.format("%d",  item.getCritical()));
			
			long statPointerAddress = item.getStatBonusPointer();
			if (statPointerAddress != 0) {
				statPointerAddress -= 0x8000000;
				if (handler != null) {
					byte[] bonuses = handler.readBytesAtOffset(statPointerAddress, 7);
					List<String> bonusStrings = new ArrayList<String>();
					if (bonuses[0] > 0) { bonusStrings.add("+" + bonuses[0] + " HP"); }
					if (bonuses[1] > 0) { bonusStrings.add("+" + bonuses[1] + ((item.getType() == WeaponType.ANIMA || item.getType() == WeaponType.LIGHT || item.getType() == WeaponType.DARK) ? " Magic" : " Strength")); }
					if (bonuses[2] > 0) { bonusStrings.add("+" + bonuses[2] + " Skill"); }
					if (bonuses[3] > 0) { bonusStrings.add("+" + bonuses[3] + " Speed"); }
					if (bonuses[4] > 0) { bonusStrings.add("+" + bonuses[4] + " Defense"); }
					if (bonuses[5] > 0) { bonusStrings.add("+" + bonuses[5] + " Resistance"); }
					if (bonuses[6] > 0) { bonusStrings.add("+" + bonuses[6] + " Luck"); }
					
					rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", String.join("<br>", bonusStrings));
				} else {
					rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", "No output handler.");
				}
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", "None");
			}
			
			long effectiveClasses = item.getEffectivenessPointer();
			if (effectiveClasses != 0) {
				effectiveClasses -= 0x8000000;
				if (handler != null) {
					handler.setNextReadOffset(effectiveClasses);
					byte[] classes = handler.continueReadingBytesUpToNextTerminator(effectiveClasses + 100);
					List<String> classList = new ArrayList<String>();
					for (byte classID : classes) {
						if (classID == 0) { break; }
						GBAFEClassData classObject = classData.classForID(classID);
						if (classObject == null) {
							classList.add("Unknown (0x" + Integer.toHexString(classID).toUpperCase() + ")");
						} else {
							if (classData.isFemale(classID)) {
								classList.add(textData.getStringAtIndex(classObject.getNameIndex(), true).trim() + " (F)");
							} else {
								classList.add(textData.getStringAtIndex(classObject.getNameIndex(), true).trim());
							}
						}
					}
					rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", String.join("<br>", classList));
				}  else {
					rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "No output handler.");
				}
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "None");
			}
			
			if (item.hasAbility1()) {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", item.getAbility1Description("<br>"));
			}
			if (item.hasAbility2()) {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", item.getAbility2Description("<br>"));
			}
			if (item.hasAbility3()) {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 3", item.getAbility3Description("<br>"));
			}
			if (item.hasAbility4()) {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 4", item.getAbility4Description("<br>"));
			}
			if (item.hasWeaponEffect()) {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effect", item.getWeaponEffectDescription());
			}
			
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Range", String.format("%d ~ %d", item.getMinRange(), item.getMaxRange()));
		}
	}
}
