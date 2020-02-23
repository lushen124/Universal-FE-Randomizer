package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Data.CharacterClass;
import fedata.gcnwii.fe9.FE9Data.Item;
import fedata.gcnwii.fe9.FE9Item;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9ItemDataLoader {
	
	public enum WeaponRank {
		NONE, E, D, C, B, A, S, UNKNOWN;
		
		public static WeaponRank rankForCharacter(char c) {
			switch (c) {
			case 'E': return E;
			case 'D': return D;
			case 'C': return C;
			case 'B': return B;
			case 'A': return A;
			case 'S': return S;
			case '*': return UNKNOWN;
			case '-': return NONE;
			default: return null;
			}
		}
		
		public boolean isHigherThan(WeaponRank rank) {
			switch (this) {
			case E: return rank != E;
			case D: return rank != E && rank != D;
			case C: return rank != E && rank != D && rank != C;
			case B: return rank == A || rank == S;
			case A: return rank == S;
			case S: return false;
			default: return false;
			}
		}
		
		public boolean isLowerThan(WeaponRank rank) {
			return this != rank && !isHigherThan(rank);
		}
		
		public WeaponRank higherRank() {
			switch (this) {
			case E: return D;
			case D: return C;
			case C: return B;
			case B: return A;
			case A: return S;
			default: return NONE;
			}
		}
		
		public WeaponRank lowerRank() {
			switch (this) {
			case S: return A;
			case A: return B;
			case B: return C;
			case C: return D;
			case D: return E;
			default: return NONE;
			}
		}
	}
	
	public enum WeaponType {
		NONE, SWORD, LANCE, AXE, BOW, FIRE, THUNDER, WIND, LIGHT, STAFF, KNIFE;
		
		public static Comparator<WeaponType> getComparator() {
			return new Comparator<WeaponType>() {
				@Override
				public int compare(WeaponType arg0, WeaponType arg1) {
					return arg0.toString().compareTo(arg1.toString());
				}
			};
		}
	}
	
	List<FE9Item> allItems;
	
	Map<String, FE9Item> idLookup;
	
	GCNDataFileHandler fe8databin;
	
	public FE9ItemDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allItems = new ArrayList<FE9Item>();
		
		idLookup = new HashMap<String, FE9Item>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.ItemDataFilename);
		assert (handler instanceof GCNDataFileHandler);
		if (handler instanceof GCNDataFileHandler) {
			fe8databin = (GCNDataFileHandler)handler;
		}
		
		long offset = FE9Data.ItemDataStartOffset;
		for (int i = 0; i < FE9Data.ItemCount; i++) {
			long dataOffset = offset + i * FE9Data.ItemDataSize;
			byte[] data = handler.readBytesAtOffset(dataOffset, FE9Data.ItemDataSize);
			FE9Item item = new FE9Item(data, dataOffset);
			allItems.add(item);
			
			//debugPrintItem(item, handler, commonTextLoader);
			long iidPtr = item.getItemIDPointer();
			String iid = fe8databin.stringForPointer(iidPtr);
			idLookup.put(iid, item);
		}
	}
	
	public FE9Item itemWithIID(String iid) {
		return idLookup.get(iid);
	}
	
	public String iidOfItem(FE9Item item) {
		assert (fe8databin != null);
		return fe8databin.stringForPointer(item.getItemIDPointer());
	}
	
	public boolean isWeapon(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isWeapon();
	}
	
	public boolean isConsumable(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isConsumable();
	}
	
	public boolean isStatBooster(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isStatBooster();
	}
	
	public boolean isTreasure(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isTreasure();
	}
	
	public boolean isSkillScroll(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isSkillScroll();
	}
	
	public WeaponRank swordRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(0)); }
	public WeaponRank lanceRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(1)); }
	public WeaponRank axeRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(2)); }
	public WeaponRank bowRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(3)); }
	public WeaponRank fireRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(4)); }
	public WeaponRank thunderRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(5)); }
	public WeaponRank windRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(6)); }
	public WeaponRank staffRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(7)); }
	public WeaponRank knifeRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(8)); }
	
	public Map<WeaponType, WeaponRank> weaponLevelsForWeaponString(String weaponLevels) {
		Map<WeaponType, WeaponRank> levelMap = new HashMap<WeaponType, WeaponRank>();
		if (swordRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.SWORD, swordRankForWeaponLevelString(weaponLevels)); }
		if (lanceRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.LANCE, lanceRankForWeaponLevelString(weaponLevels)); }
		if (axeRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.AXE, axeRankForWeaponLevelString(weaponLevels)); }
		if (bowRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.BOW, bowRankForWeaponLevelString(weaponLevels)); }
		if (fireRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.FIRE, fireRankForWeaponLevelString(weaponLevels)); }
		if (thunderRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.THUNDER, thunderRankForWeaponLevelString(weaponLevels)); }
		if (windRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.WIND, windRankForWeaponLevelString(weaponLevels)); }
		if (staffRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { 
			levelMap.put(WeaponType.STAFF, staffRankForWeaponLevelString(weaponLevels));
			levelMap.put(WeaponType.LIGHT, staffRankForWeaponLevelString(weaponLevels));
		}
		return levelMap;
	}
	
	public WeaponRank highestRankInString(String weaponLevels) {
		if (weaponLevels.contains("S")) { return WeaponRank.S; }
		if (weaponLevels.contains("A")) { return WeaponRank.A; }
		if (weaponLevels.contains("B")) { return WeaponRank.B; }
		if (weaponLevels.contains("C")) { return WeaponRank.C; }
		if (weaponLevels.contains("D")) { return WeaponRank.D; }
		if (weaponLevels.contains("E")) { return WeaponRank.E; }
		return WeaponRank.NONE;
	}
	
	public WeaponType weaponTypeForItem(FE9Item item) {
		if (FE9Data.Item.withIID(iidOfItem(item)).isSword()) { return WeaponType.SWORD; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isLance()) { return WeaponType.LANCE; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isAxe()) { return WeaponType.AXE; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isBow()) { return WeaponType.BOW; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isFireMagic()) { return WeaponType.FIRE; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isThunderMagic()) { return WeaponType.THUNDER; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isWindMagic()) { return WeaponType.WIND; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isLightMagic()) { return WeaponType.LIGHT; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isStaff()) { return WeaponType.STAFF; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isKnife()) { return WeaponType.KNIFE; }
		return WeaponType.NONE;
	}
	
	public WeaponRank weaponRankForItem(FE9Item item) {
		if (FE9Data.Item.withIID(iidOfItem(item)).isERank()) { return WeaponRank.E; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isDRank()) { return WeaponRank.D; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isCRank()) { return WeaponRank.C; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isBRank()) { return WeaponRank.B; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isARank()) { return WeaponRank.A; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isSRank()) { return WeaponRank.S; }
		if (isWeapon(item)) { return WeaponRank.UNKNOWN; }
		return WeaponRank.NONE;
	}
	
	public List<FE9Item> weaponsOfRankAndType(WeaponRank rank, WeaponType type) {
		Set<FE9Data.Item> items = new HashSet<FE9Data.Item>();
		switch (rank) {
		case E: items.addAll(FE9Data.Item.allERankWeapons); break;
		case D: items.addAll(FE9Data.Item.allDRankWeapons); break;
		case C: items.addAll(FE9Data.Item.allCRankWeapons); break;
		case B: items.addAll(FE9Data.Item.allBRankWeapons); break;
		case A: items.addAll(FE9Data.Item.allARankWeapons); break;
		case S: items.addAll(FE9Data.Item.allSRankWeapons); break;
		default: break;
		}
		switch (type) {
		case SWORD: items.retainAll(FE9Data.Item.allSwords); break;
		case LANCE: items.retainAll(FE9Data.Item.allLances); break;
		case AXE: items.retainAll(FE9Data.Item.allAxes); break;
		case BOW: items.retainAll(FE9Data.Item.allBows); break;
		case FIRE: items.retainAll(FE9Data.Item.allFireMagic); break;
		case WIND: items.retainAll(FE9Data.Item.allWindMagic); break;
		case THUNDER: items.retainAll(FE9Data.Item.allThunderMagic); break;
		case LIGHT: items.retainAll(FE9Data.Item.allLightMagic); break;
		case STAFF: items.retainAll(FE9Data.Item.allStaves); break;
		case KNIFE: items.retainAll(FE9Data.Item.allKnives); break;
		default: break;
		}
		items.removeAll(FE9Data.Item.blacklistedWeapons);
		return fe9ItemListFromSet(items);
	}
	
	public List<FE9Item> specialWeaponsForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		return fe9ItemListFromSet(FE9Data.Item.specialWeaponsForClass(charClass));
	}
	
	public List<FE9Item> weaponsSetForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		List<FE9Item> weapons = new ArrayList<FE9Item>();
		if (charClass.isLaguz()) {
			switch (charClass) {
			case LION: case FERAL_LION: weapons.add(itemWithIID(FE9Data.Item.LION_CLAW.getIID())); break;
			case TIGER: case FERAL_TIGER: weapons.add(itemWithIID(FE9Data.Item.TIGER_CLAW.getIID())); break;
			case CAT: case FERAL_CAT: case CAT_F: case FERAL_CAT_F: weapons.add(itemWithIID(FE9Data.Item.CAT_CLAW.getIID())); break;
			case WHITE_DRAGON: case FERAL_WHITE_DRAGON: weapons.add(itemWithIID(FE9Data.Item.WHITE_BREATH.getIID())); break;
			case RED_DRAGON: case FERAL_RED_DRAGON: case RED_DRAGON_F: case FERAL_RED_DRAGON_F: weapons.add(itemWithIID(FE9Data.Item.RED_BREATH.getIID())); break;
			case HAWK: case FERAL_HAWK: weapons.add(itemWithIID(FE9Data.Item.HAWK_BEAK.getIID())); break;
			case CROW: case FERAL_CROW: weapons.add(itemWithIID(FE9Data.Item.CROW_BEAK.getIID())); break;
			case TIBARN_HAWK: weapons.add(itemWithIID(FE9Data.Item.HAWK_KING_BEAK.getIID())); break;
			case NAESALA_CROW: weapons.add(itemWithIID(FE9Data.Item.CROW_KING_BEAK.getIID())); break;
			default: break;
			}
		}
		return weapons;
	}
	
	public List<FE9Item> formerThiefKit() {
		return new ArrayList<FE9Item>(Arrays.asList(
				itemWithIID(FE9Data.Item.DOOR_KEY.getIID()),
				itemWithIID(FE9Data.Item.CHEST_KEY.getIID())
				));
	}
	
	public List<FE9Item> equipmentListForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		List<FE9Item> equipment = new ArrayList<FE9Item>();
		if (charClass.isLaguz()) {
			equipment.add(itemWithIID(FE9Data.Item.LAGUZ_STONE.getIID()));
		}
		return equipment;
	}
	
	public List<FE9Item> potentialEquipmentListForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		List<FE9Item> equipment = new ArrayList<FE9Item>();
		equipment.add(itemWithIID(FE9Data.Item.VULNERARY.getIID()));
		equipment.add(itemWithIID(FE9Data.Item.ELIXIR.getIID()));
		
		return equipment; 
	}
	
	public List<FE9Item> veryRareEquipment() {
		List<FE9Item> equipment = new ArrayList<FE9Item>();
		for (int i = 0; i < 10; i++) { equipment.add(itemWithIID(FE9Data.Item.RED_GEM.getIID())); }
		for (int i = 0; i < 5; i++) { equipment.add(itemWithIID(FE9Data.Item.BLUE_GEM.getIID())); }
		equipment.add(itemWithIID(FE9Data.Item.WHITE_GEM.getIID()));
		
		equipment.addAll(FE9Data.Item.allStatBoosters.stream().map(fe9item -> {
			return itemWithIID(fe9item.getIID());
		}).collect(Collectors.toList()));
		equipment.addAll(FE9Data.Item.allSkillScrolls.stream().map(fe9item -> {
			return itemWithIID(fe9item.getIID());
		}).collect(Collectors.toList()));
		
		equipment.sort(new Comparator<FE9Item>() {
			@Override
			public int compare(FE9Item o1, FE9Item o2) {
				return iidOfItem(o1).compareTo(iidOfItem(o2));
			}
		});
		
		return equipment;
	}
	
	public List<FE9Item> rareEquipmentForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		List<FE9Item> equipment = new ArrayList<FE9Item>();
		
		if (charClass.isLaguz()) { 
			equipment.add(itemWithIID(FE9Data.Item.BEORCGUARD.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.DEMI_BAND.getIID()));
		}
		if (charClass.isBeorc()) { equipment.add(itemWithIID(FE9Data.Item.LAGUZGUARD.getIID())); }
		if (charClass.isFlier()) { equipment.add(itemWithIID(FE9Data.Item.FULL_GUARD.getIID())); }
		switch (charClass) {
		case KNIGHT: case GENERAL:
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_WARD.getIID()));
			break;
		case MYRMIDON: case SWORDMASTER: case MYRMIDON_F: case SWORDMASTER_F:
			equipment.add(itemWithIID(FE9Data.Item.SWORD_BAND.getIID()));
			break;
		case SOLDIER: case HALBERDIER: case SOLDIER_F: case HALBERDIER_F:
			equipment.add(itemWithIID(FE9Data.Item.SOLDIER_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_WARD.getIID()));
			break;
		case FIGHTER: case WARRIOR:
			equipment.add(itemWithIID(FE9Data.Item.FIGHTERS_BAND.getIID()));
			break;
		case ARCHER: case SNIPER:
			equipment.add(itemWithIID(FE9Data.Item.ARCHER_BAND.getIID()));
			break;
		case SWORD_KNIGHT: case SWORD_KNIGHT_F: case LANCE_KNIGHT: case LANCE_KNIGHT_F: case AXE_KNIGHT: case AXE_KNIGHT_F: case BOW_KNIGHT: case BOW_KNIGHT_F:
		case AXE_PALADIN: case AXE_PALADIN_F: case BOW_PALADIN: case BOW_PALADIN_F: case LANCE_PALADIN: case LANCE_PALADIN_F: case SWORD_PALADIN: case SWORD_PALADIN_F:
		case TITANIA_PALADIN:
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_WARD.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.PALADIN_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_RING.getIID()));
			break;
		case PEGASUS_KNIGHT: case FALCON_KNIGHT: case ELINCIA_FALCON_KNIGHT:
			equipment.add(itemWithIID(FE9Data.Item.PEGASUS_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.FULL_GUARD.getIID()));
			break;
		case WYVERN_RIDER: case WYVERN_LORD: case WYVERN_LORD_F: case WYVERN_RIDER_F:
			equipment.add(itemWithIID(FE9Data.Item.WYVERN_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.FULL_GUARD.getIID()));
			break;
		case MAGE: case MAGE_F: case SAGE_KNIFE: case SAGE_KNIFE_F: case SAGE_STAFF: case SAGE_STAFF_F:
			equipment.add(itemWithIID(FE9Data.Item.MAGE_BAND.getIID()));
			break;
		case PRIEST: case BISHOP: case CLERIC:
			equipment.add(itemWithIID(FE9Data.Item.PRIEST_BAND.getIID()));
			break;
		case THIEF:
			equipment.add(itemWithIID(FE9Data.Item.THIEF_BAND.getIID()));
			break;
		}
		
		return equipment;
	}
	
	public List<FE9Item> possibleUpgradesToWeapon(FE9Item item, boolean isWielderPromoted) {
		if (item == null) { return null; }
		FE9Data.Item original = FE9Data.Item.withIID(iidOfItem(item));
		if (original == null || !original.isWeapon() || original.isStaff()) { return null; }
		Set<FE9Data.Item> upgrades = new HashSet<FE9Data.Item>();
		if (original.isERank()) { upgrades.addAll(FE9Data.Item.allDRankWeapons); }
		else if (original.isDRank()) { upgrades.addAll(FE9Data.Item.allCRankWeapons); }
		else if (original.isCRank()) { upgrades.addAll(FE9Data.Item.allCRankWeapons); upgrades.addAll(FE9Data.Item.allBRankWeapons); }
		else if (original.isBRank()) { // Unpromoted classes apparently can't go above B rank.
			upgrades.addAll(FE9Data.Item.allBRankWeapons); 
			if (isWielderPromoted) { 
				upgrades.addAll(FE9Data.Item.allARankWeapons); 
			}
		}
		else if (original.isARank()) { upgrades.addAll(FE9Data.Item.allSRankWeapons); }
		
		if (original.isSword()) { upgrades.retainAll(FE9Data.Item.allSwords); }
		else if (original.isLance()) { upgrades.retainAll(FE9Data.Item.allLances); }
		else if (original.isAxe()) { upgrades.retainAll(FE9Data.Item.allAxes); }
		else if (original.isBow()) { upgrades.retainAll(FE9Data.Item.allBows); }
		else if (original.isFireMagic()) { upgrades.retainAll(FE9Data.Item.allFireMagic); }
		else if (original.isThunderMagic()) { upgrades.retainAll(FE9Data.Item.allThunderMagic); }
		else if (original.isWindMagic()) { upgrades.retainAll(FE9Data.Item.allWindMagic); }
		else if (original.isLightMagic()) { upgrades.retainAll(FE9Data.Item.allLightMagic); }
		else { return null; }
		
		return upgrades.stream().sorted(new Comparator<FE9Data.Item>() {
			@Override
			public int compare(Item arg0, Item arg1) {
				return arg0.getIID().compareTo(arg1.getIID());
			}
		}).map(fe9DataItem -> {
			return itemWithIID(fe9DataItem.getIID());
		}).collect(Collectors.toList());
	}
	
	public List<FE9Item> getSimilarItemsTo(FE9Item originalItem) {
		if (originalItem == null) { return null; }
		Set<FE9Data.Item> fe9DataItems = new HashSet<FE9Data.Item>(); 
		if (isWeapon(originalItem)) {
			FE9Data.Item original = FE9Data.Item.withIID(iidOfItem(originalItem));
			if (original.isSword()) { fe9DataItems.addAll(FE9Data.Item.allSwords); }
			if (original.isLance()) { fe9DataItems.addAll(FE9Data.Item.allLances); }
			if (original.isAxe()) { fe9DataItems.addAll(FE9Data.Item.allAxes); }
			if (original.isBow()) { fe9DataItems.addAll(FE9Data.Item.allBows); }
			if (original.isMagic()) { 
				fe9DataItems.addAll(FE9Data.Item.allFireMagic);
				fe9DataItems.addAll(FE9Data.Item.allWindMagic);
				fe9DataItems.addAll(FE9Data.Item.allThunderMagic);
				fe9DataItems.addAll(FE9Data.Item.allLightMagic);
			}
			if (original.isStaff()) { fe9DataItems.addAll(FE9Data.Item.allStaves); }
			
			if (original.isERank()) { fe9DataItems.addAll(FE9Data.Item.allERankWeapons); }
			if (original.isDRank()) { fe9DataItems.addAll(FE9Data.Item.allDRankWeapons); }
			if (original.isCRank()) { fe9DataItems.addAll(FE9Data.Item.allCRankWeapons); }
			if (original.isBRank()) { fe9DataItems.addAll(FE9Data.Item.allBRankWeapons); }
			if (original.isARank()) { fe9DataItems.addAll(FE9Data.Item.allARankWeapons); }
			if (original.isSRank()) { fe9DataItems.addAll(FE9Data.Item.allSRankWeapons); }
		} else if (isConsumable(originalItem)) {
			fe9DataItems.addAll(FE9Data.Item.allConsumables);
		} else if (isStatBooster(originalItem)) {
			fe9DataItems.addAll(FE9Data.Item.allStatBoosters);
		} else if (isTreasure(originalItem)) {
			fe9DataItems.addAll(FE9Data.Item.allGems);
		} else if (isSkillScroll(originalItem)) {
			fe9DataItems.addAll(FE9Data.Item.allSkillScrolls);
		}
		
		fe9DataItems.removeAll(FE9Data.Item.allRestrictedItems);
		fe9DataItems.removeAll(FE9Data.Item.blacklistedWeapons);
		
		return fe9ItemListFromSet(fe9DataItems);
	}
	
	public List<FE9Item> getPossibleRewards() {
		Set<FE9Data.Item> fe9DataItems = new HashSet<FE9Data.Item>();
		fe9DataItems.addAll(FE9Data.Item.allDroppableWeapons());
		fe9DataItems.addAll(FE9Data.Item.allGems);
		fe9DataItems.addAll(FE9Data.Item.allStatBoosters);
		fe9DataItems.addAll(FE9Data.Item.allSkillScrolls);
		fe9DataItems.addAll(FE9Data.Item.allConsumables);
		
		return fe9ItemListFromSet(fe9DataItems);
	}
	
	private List<FE9Item> fe9ItemListFromSet(Set<FE9Data.Item> fe9dataItemSet) {
		return fe9dataItemSet.stream().sorted(new Comparator<FE9Data.Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				return o1.getIID().compareTo(o2.getIID());
			}
		}).map(fe9DataItem -> {
			return idLookup.get(fe9DataItem.getIID());
		}).collect(Collectors.toList());
	}
	
	private void debugPrintItem(FE9Item item, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "===== Printing Item =====");
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, 
				"IID: " + stringForPointer(item.getItemIDPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, 
				"MIID: " + stringForPointer(item.getItemNamePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, 
				"MH_I: " + stringForPointer(item.getItemDescriptionPointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Type: " + stringForPointer(item.getItemTypePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Subtype?: " + stringForPointer(item.getItemSubtypePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Rank: " + stringForPointer(item.getItemRankPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Restrictions: " + stringForPointer(item.getItemTrait1Pointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 1: " + stringForPointer(item.getItemTrait2Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 2: " + stringForPointer(item.getItemTrait3Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 3: " + stringForPointer(item.getItemTrait4Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 4: " + stringForPointer(item.getItemTrait5Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 5: " + stringForPointer(item.getItemTrait6Pointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Effectiveness: " + stringForPointer(item.getItemEffectiveness1Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 6: " + stringForPointer(item.getItemEffectiveness2Pointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Effect Animation: " + stringForPointer(item.getItemEffectAnimation1Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 7: " + stringForPointer(item.getItemEffectAnimation2Pointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Cost per Use: " + item.getItemCost());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Durability: " + item.getItemDurability());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Might: " + item.getItemMight());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Accuracy: " + item.getItemAccuracy());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Weight: " + item.getItemWeight());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Critical: " + item.getItemCritical());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Range: " + item.getMinimumRange() + " ~ " + item.getMaximumRange());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Unknown Value 1: " + item.getItemNumber());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Weapon EXP: " + item.getWeaponExperience());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Unknown Value 2: " + item.getUnknownValue2());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 8: " + stringForPointer(item.getItemUnknownPointer8(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 9: " + stringForPointer(item.getItemUnknownPointer9(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Remaining Bytes: " + WhyDoesJavaNotHaveThese.displayStringForBytes(item.getRemainingBytes()));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "===== End Printing Item =====");
	}

	private String stringForPointer(long pointer, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		if (pointer == 0) { return "(null)"; }
		handler.setNextReadOffset(pointer);
		byte[] bytes = handler.continueReadingBytesUpToNextTerminator(pointer + 0xFF);
		String identifier = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(bytes);
		String resolvedValue = commonTextLoader.textStringForIdentifier(identifier);
		if (resolvedValue != null) {
			return identifier + " (" + resolvedValue + ")";
		} else {
			return identifier;
		}
	}
}
