package random.gba.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFESpellAnimationCollection;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEItem;
import fedata.gba.general.GBAFEItemProvider;
import fedata.gba.general.GBAFEItemProvider.WeaponRanks;
import fedata.gba.general.GBAFEPromotionItem;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
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
	
	// TODO: Put this somewhere else.
	public GBAFESpellAnimationCollection spellAnimations;
	
	private FreeSpaceManager freeSpace;
	private Map<AdditionalData, Long> offsetsForAdditionalData;
	private Map<String, Long> promotionItemAddressPointers;
	
	public static final String RecordKeeperCategoryWeaponKey = "Weapons";
	
	public ItemDataLoader(GBAFEItemProvider provider, FileHandler handler, FreeSpaceManager freeSpace) {
		super();
		
		this.freeSpace = freeSpace;
		this.provider = provider;
		
		long baseAddress = FileReadHelper.readAddress(handler, provider.itemTablePointer());
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
				}
			} while (currentByte != 0);
			
			List<GBAFEClass> additionalClasses = provider.additionalClassesForPromotionItem(promotionItem, idList);
			if (additionalClasses != null && !additionalClasses.isEmpty()) {
				for (int i = 0; i < additionalClasses.size(); i++) {
					GBAFEClass additionalClass = additionalClasses.get(i);
					if (!idList.contains((byte)additionalClass.getID())) { idList.add((byte)additionalClass.getID()); }
				}
				idList.add((byte)0x0);
				byte[] byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				
				offset = freeSpace.setValue(byteArray, promotionItem.itemName());
				promotionItemAddressPointers.put(promotionItem.itemName(), promotionItemOffset);
			}
		}
	}
	
	private void registerAdditionalData(AdditionalData dataName, byte[] byteArray) {
		if (byteArray.length > 0) {
			long offset = freeSpace.setValue(byteArray, dataName.key);
			offsetsForAdditionalData.put(dataName, offset);
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
	
	public GBAFEItemData itemWithID(int itemID) {
		return itemMap.get(itemID);
	}
	
	public int getHighestWeaponRank() {
		return provider.getHighestWeaponRankValue();
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
	
	public GBAFEItemData[] getChestRewards() {
		return feItemsFromItemSet(provider.allPotentialChestRewards());
	}
	
	public GBAFEItemData[] relatedItems(int itemID) {
		return feItemsFromItemSet(provider.relatedItemsToItem(itemMap.get(itemID)));
	}
	
	public GBAFEItemData[] lockedWeaponsToClass(int classID) {
		return feItemsFromItemSet(provider.weaponsLockedToClass(classID));
	}
	
	public Boolean isHealingStaff(int itemID) {
		return provider.itemWithID(itemID).isHealingStaff();
	}
	
	public WeaponRank weaponRankFromValue(int rankValue) {
		return provider.rankWithValue(rankValue);
	}
	
	public GBAFEItemData getRandomHealingStaff(WeaponRank maxRank, Random rng) {
		Set<GBAFEItem> healingStaves = provider.weaponsOfTypeUpToRank(WeaponType.STAFF, maxRank, false, false);
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
	
	public GBAFEItemData getSidegradeWeapon(GBAFEClassData targetClass, GBAFEItemData originalWeapon, boolean strict, Random rng) {
		if (!isWeapon(originalWeapon)) {
			return null;
		}
		
		Set<GBAFEItem> potentialItems = provider.comparableWeaponsForClass(targetClass.getID(), new WeaponRanks(targetClass, provider), originalWeapon, strict);
		if (potentialItems.isEmpty()) { 
			potentialItems = provider.basicWeaponsForClass(targetClass.getID());
			
			if (potentialItems.isEmpty()) {
				return null;
			}
		}
		
		GBAFEItem[] itemsArray = potentialItems.toArray(new GBAFEItem[potentialItems.size()]);
		int index = rng.nextInt(potentialItems.size());
		return itemMap.get(itemsArray[index].getID());
	}
	
	public GBAFEItemData getSidegradeWeapon(GBAFECharacterData character, GBAFEItemData originalWeapon, boolean strict, Random rng) {
		if (!isWeapon(originalWeapon)) {
			return null;
		}
		
		Set<GBAFEItem> potentialItems = provider.comparableWeaponsForClass(character.getClassID(), new WeaponRanks(character, provider), originalWeapon, strict);
		if (potentialItems.isEmpty()) { 
			potentialItems = provider.basicWeaponsForClass(character.getClassID());
			
			if (potentialItems.isEmpty()) {
				return null;
			}
		}
		
		GBAFEItem[] itemsArray = potentialItems.toArray(new GBAFEItem[potentialItems.size()]);
		int index = rng.nextInt(potentialItems.size());
		return itemMap.get(itemsArray[index].getID());
	}
	
	public GBAFEItemData getRandomWeaponForCharacter(GBAFECharacterData character, Boolean ranged, Boolean melee, Random rng) {
		GBAFEItemData[] potentialItems = usableWeaponsForCharacter(character, ranged, melee);
		if (potentialItems == null || potentialItems.length < 1) {
			// Check class specific weapons (e.g. FE8 monsters)
			potentialItems = feItemsFromItemSet(provider.weaponsForClass(character.getClassID()));
			if (potentialItems == null || potentialItems.length < 1) {
				return null;
			}
		}
		
		int index = rng.nextInt(potentialItems.length);
		return potentialItems[index];
	}
	
	private GBAFEItemData[] usableWeaponsForCharacter(GBAFECharacterData character, Boolean ranged, Boolean melee) {
		ArrayList<GBAFEItemData> items = new ArrayList<GBAFEItemData>();
		
		if (character.getSwordRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.SWORD, character.getSwordRank(), ranged, melee))); }
		if (character.getLanceRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.LANCE, character.getLanceRank(), ranged, melee))); }
		if (character.getAxeRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.AXE, character.getAxeRank(), ranged, melee))); }
		if (character.getBowRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.BOW, character.getBowRank(), ranged, melee))); }
		if (character.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.ANIMA, character.getAnimaRank(), ranged, melee))); }
		if (character.getLightRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.LIGHT, character.getLightRank(), ranged, melee))); }
		if (character.getDarkRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.DARK, character.getDarkRank(), ranged, melee))); }
		if (character.getStaffRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.STAFF, character.getStaffRank(), ranged, melee))); }
		
		return items.toArray(new GBAFEItemData[items.size()]);
	}
	
	public GBAFEItemData[] formerThiefInventory() {
		return feItemsFromItemSet(provider.formerThiefInventory());
	}
	
	public GBAFEItemData[] thiefItemsToRemove() {
		return feItemsFromItemSet(provider.thiefItemsToRemove());
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
	
	public void compileDiffs(DiffCompiler compiler) {
		for (GBAFEItemData item : itemMap.values()) {
			item.commitChanges();
			if (item.hasCommittedChanges()) {
				Diff charDiff = new Diff(item.getAddressOffset(), item.getData().length, item.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
		
		spellAnimations.compileDiffs(compiler);
		
		for (String promotionItemName : promotionItemAddressPointers.keySet()) {
			if (freeSpace.hasOffsetForKey(promotionItemName)) {
				long offset = freeSpace.getOffsetForKey(promotionItemName);
				byte[] addressByteArray = WhyDoesJavaNotHaveThese.bytesFromAddress(offset);
				long targetOffset = promotionItemAddressPointers.get(promotionItemName);
				compiler.addDiff(new Diff(targetOffset, addressByteArray.length, addressByteArray, null));
			}
		}
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
		String name = textData.getStringAtIndex(nameIndex).trim();
		int descriptionIndex = item.getDescriptionIndex();
		String description = textData.getStringAtIndex(descriptionIndex).trim();
		
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
								classList.add(textData.getStringAtIndex(classObject.getNameIndex()).trim() + " (F)");
							} else {
								classList.add(textData.getStringAtIndex(classObject.getNameIndex()).trim());
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
								classList.add(textData.getStringAtIndex(classObject.getNameIndex()).trim() + " (F)");
							} else {
								classList.add(textData.getStringAtIndex(classObject.getNameIndex()).trim());
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
