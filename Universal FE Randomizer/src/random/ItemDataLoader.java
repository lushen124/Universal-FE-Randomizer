package random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.FEBase;
import fedata.FEClass;
import fedata.FEItem;
import fedata.FESpellAnimationCollection;
import fedata.fe6.FE6Data;
import fedata.fe6.FE6Item;
import fedata.fe6.FE6SpellAnimationCollection;
import fedata.fe7.FE7Data;
import fedata.fe7.FE7Item;
import fedata.fe7.FE7SpellAnimationCollection;
import fedata.fe7.FE7Data.Item;
import fedata.fe7.FE7Data.Item.WeaponEffect;
import fedata.general.WeaponEffects;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FreeSpaceManager;
import util.HuffmanHelper;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.RecordKeeper;

public class ItemDataLoader {
private FEBase.GameType gameType;

	public enum AdditionalData {
		STR_MAG_BOOST("STRMAG"), SKL_BOOST("SKL"), SPD_BOOST("SPD"), DEF_BOOST("DEF"), RES_BOOST("RES"), LCK_BOOST("LCK"),
		
		HERO_CREST_CLASSES("HEROCREST"), KNIGHT_CREST_CLASSES("KNIGHTCREST"), MASTER_SEAL_CLASSES("MASTERSEAL"),
		
		KNIGHTCAV_EFFECT("EFF_KNIGHT_CAV"), KNIGHT_EFFECT("EFF_KNIGHT"), DRAGON_EFFECT("EFF_DRAGON"), CAVALRY_EFFECT("EFF_CAVALRY"), MYRMIDON_EFFECT("EFF_MYRMIDON"), FLIERS_EFFECT("EFF_FLIER");
		
		String key;
		
		private AdditionalData(String key) {
			this.key = key;
		}
	}
	
	private Map<Integer, FEItem> itemMap = new HashMap<Integer, FEItem>();
	
	// TODO: Put this somewhere else.
	public FESpellAnimationCollection spellAnimations;
	
	private FreeSpaceManager freeSpace;
	private Map<AdditionalData, Long> offsetsForAdditionalData;
	private Map<AdditionalData, Long> promotionItemAddressPointers;
	
	public static final String RecordKeeperCategoryWeaponKey = "Weapons";

	public ItemDataLoader(FEBase.GameType gameType, FileHandler handler, FreeSpaceManager freeSpace) {
		super();
		this.gameType = gameType;
		this.freeSpace = freeSpace;
		
		switch (gameType) {
			case FE6: {
				long baseAddress = FileReadHelper.readAddress(handler, FE6Data.ItemTablePointer);
				for (FE6Data.Item item : FE6Data.Item.values()) {
					if (item == FE6Data.Item.NONE) {
						continue;
					}	
					long offset = baseAddress + (FE6Data.BytesPerItem * item.ID);
					byte[] itemData = handler.readBytesAtOffset(offset, FE6Data.BytesPerItem);
					itemMap.put(item.ID, new FE6Item(itemData, offset));
				}
				
				long spellAnimationBaseAddress = FileReadHelper.readAddress(handler, FE6Data.SpellAnimationTablePointer);
				spellAnimations = new FE6SpellAnimationCollection(handler.readBytesAtOffset(spellAnimationBaseAddress, 
						FE6Data.NumberOfSpellAnimations * FE6Data.BytesPerSpellAnimation), spellAnimationBaseAddress);
				
				offsetsForAdditionalData = new HashMap<AdditionalData, Long>();
				
				// Set up effectiveness.
				long offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.CAVALIER.ID,
						(byte)FE6Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE6Data.CharacterClass.PALADIN.ID,
						(byte)FE6Data.CharacterClass.PALADIN_F.ID,
						(byte)FE6Data.CharacterClass.KNIGHT.ID,
						(byte)FE6Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE6Data.CharacterClass.GENERAL.ID,
						(byte)FE6Data.CharacterClass.GENERAL_F.ID,
						(byte)FE6Data.CharacterClass.NOMAD.ID,
						(byte)FE6Data.CharacterClass.NOMAD_F.ID,
						(byte)FE6Data.CharacterClass.NOMAD_TROOPER.ID,
						(byte)FE6Data.CharacterClass.NOMAD_TROOPER_F.ID,
						(byte)FE6Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE6Data.CharacterClass.VALKYRIE.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHTCAV_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHTCAV_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.KNIGHT.ID,
						(byte)FE6Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE6Data.CharacterClass.GENERAL.ID,
						(byte)FE6Data.CharacterClass.GENERAL_F.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHT_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.CAVALIER.ID,
						(byte)FE6Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE6Data.CharacterClass.PALADIN.ID,
						(byte)FE6Data.CharacterClass.PALADIN_F.ID,
						(byte)FE6Data.CharacterClass.NOMAD.ID,
						(byte)FE6Data.CharacterClass.NOMAD_F.ID,
						(byte)FE6Data.CharacterClass.NOMAD_TROOPER.ID,
						(byte)FE6Data.CharacterClass.NOMAD_TROOPER_F.ID,
						(byte)FE6Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE6Data.CharacterClass.VALKYRIE.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.CAVALRY_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.CAVALRY_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.FIRE_DRAGON.ID,
						(byte)FE6Data.CharacterClass.DIVINE_DRAGON.ID,
						(byte)FE6Data.CharacterClass.MAGIC_DRAGON.ID,
						(byte)FE6Data.CharacterClass.WYVERN_RIDER.ID,
						(byte)FE6Data.CharacterClass.WYVERN_RIDER_F.ID,
						(byte)FE6Data.CharacterClass.WYVERN_KNIGHT.ID,
						(byte)FE6Data.CharacterClass.WYVERN_KNIGHT_F.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.DRAGON_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.DRAGON_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.PEGASUS_KNIGHT.ID,
						(byte)FE6Data.CharacterClass.FALCON_KNIGHT.ID,
						(byte)FE6Data.CharacterClass.WYVERN_KNIGHT.ID,
						(byte)FE6Data.CharacterClass.WYVERN_KNIGHT_F.ID,
						(byte)FE6Data.CharacterClass.WYVERN_RIDER.ID,
						(byte)FE6Data.CharacterClass.WYVERN_RIDER_F.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.FLIERS_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.FLIERS_EFFECT, offset);
				
				// Set up stat boosts.
				offset = freeSpace.setValue(new byte[] {0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.STR_MAG_BOOST.key);
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
				long knightCrestOffset = FE6Data.PromotionItem.KNIGHT_CREST.getListAddress();
				List<Byte> idList = new ArrayList<Byte>();
				byte currentByte = 0x0;
				long currentOffset = FileReadHelper.readAddress(handler, knightCrestOffset); // One more jump here.
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE6Data.CharacterClass.LORD.ID)) { idList.add((byte)FE6Data.CharacterClass.LORD.ID); }
				if (!idList.contains((byte)FE6Data.CharacterClass.SOLDIER.ID)) { idList.add((byte)FE6Data.CharacterClass.SOLDIER.ID); }
				idList.add((byte)FE6Data.CharacterClass.NONE.ID);
				byte[] byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.KNIGHT_CREST_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_CREST_CLASSES, offset);
					
				promotionItemAddressPointers = new HashMap<AdditionalData, Long>();
				promotionItemAddressPointers.put(AdditionalData.KNIGHT_CREST_CLASSES, knightCrestOffset);
				
				break;
			}
			case FE7: {
				long baseAddress = FileReadHelper.readAddress(handler, FE7Data.ItemTablePointer);
				for (FE7Data.Item item : FE7Data.Item.values()) {
					if (item == FE7Data.Item.NONE) {
						continue;
					}	
					long offset = baseAddress + (FE7Data.BytesPerItem * item.ID);
					byte[] itemData = handler.readBytesAtOffset(offset, FE7Data.BytesPerItem);
					itemMap.put(item.ID, new FE7Item(itemData, offset));
				}
				
				long spellAnimationBaseAddress = FileReadHelper.readAddress(handler, FE7Data.SpellAnimationTablePointer);
				spellAnimations = new FE7SpellAnimationCollection(handler.readBytesAtOffset(spellAnimationBaseAddress, 
						FE7Data.NumberOfSpellAnimations * FE7Data.BytesPerSpellAnimation), spellAnimationBaseAddress);
				
				offsetsForAdditionalData = new HashMap<AdditionalData, Long>();
				
				// Set up effectiveness.
				long offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.CAVALIER.ID,
						(byte)FE7Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE7Data.CharacterClass.PALADIN.ID,
						(byte)FE7Data.CharacterClass.PALADIN_F.ID,
						(byte)FE7Data.CharacterClass.KNIGHT.ID,
						(byte)FE7Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE7Data.CharacterClass.GENERAL.ID,
						(byte)FE7Data.CharacterClass.GENERAL_F.ID,
						(byte)FE7Data.CharacterClass.NOMAD.ID,
						(byte)FE7Data.CharacterClass.NOMAD_F.ID,
						(byte)FE7Data.CharacterClass.NOMADTROOPER.ID,
						(byte)FE7Data.CharacterClass.NOMADTROOPER_F.ID,
						(byte)FE7Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE7Data.CharacterClass.VALKYRIE.ID,
						(byte)FE7Data.CharacterClass.LORD_KNIGHT.ID,
						(byte)FE7Data.CharacterClass.GREAT_LORD.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHTCAV_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHTCAV_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.KNIGHT.ID,
						(byte)FE7Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE7Data.CharacterClass.GENERAL.ID,
						(byte)FE7Data.CharacterClass.GENERAL_F.ID,
						(byte)FE7Data.CharacterClass.GREAT_LORD.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHT_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.CAVALIER.ID,
						(byte)FE7Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE7Data.CharacterClass.PALADIN.ID,
						(byte)FE7Data.CharacterClass.PALADIN_F.ID,
						(byte)FE7Data.CharacterClass.NOMAD.ID,
						(byte)FE7Data.CharacterClass.NOMAD_F.ID,
						(byte)FE7Data.CharacterClass.NOMADTROOPER.ID,
						(byte)FE7Data.CharacterClass.NOMADTROOPER_F.ID,
						(byte)FE7Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE7Data.CharacterClass.VALKYRIE.ID,
						(byte)FE7Data.CharacterClass.LORD_KNIGHT.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.CAVALRY_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.CAVALRY_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.FIRE_DRAGON.ID,
						(byte)FE7Data.CharacterClass.WYVERNKNIGHT.ID,
						(byte)FE7Data.CharacterClass.WYVERNKNIGHT_F.ID,
						(byte)FE7Data.CharacterClass.WYVERNLORD.ID,
						(byte)FE7Data.CharacterClass.WYVERNLORD_F.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.DRAGON_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.DRAGON_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.MYRMIDON.ID,
						(byte)FE7Data.CharacterClass.MYRMIDON_F.ID,
						(byte)FE7Data.CharacterClass.SWORDMASTER.ID,
						(byte)FE7Data.CharacterClass.SWORDMASTER_F.ID,
						(byte)FE7Data.CharacterClass.MERCENARY.ID,
						(byte)FE7Data.CharacterClass.MERCENARY_F.ID,
						(byte)FE7Data.CharacterClass.HERO.ID,
						(byte)FE7Data.CharacterClass.HERO_F.ID,
						(byte)FE7Data.CharacterClass.BLADE_LORD.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.MYRMIDON_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.MYRMIDON_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.PEGASUSKNIGHT.ID,
						(byte)FE7Data.CharacterClass.FALCONKNIGHT.ID,
						(byte)FE7Data.CharacterClass.WYVERNKNIGHT.ID,
						(byte)FE7Data.CharacterClass.WYVERNKNIGHT_F.ID,
						(byte)FE7Data.CharacterClass.WYVERNLORD.ID,
						(byte)FE7Data.CharacterClass.WYVERNLORD_F.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.FLIERS_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.FLIERS_EFFECT, offset);
				
				// Set up stat boosts.
				offset = freeSpace.setValue(new byte[] {0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.STR_MAG_BOOST.key);
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
				long heroCrestOffset = FE7Data.PromotionItem.HERO_CREST.getPointerAddress();
				long heroCrestPointerAddress = FileReadHelper.readAddress(handler, heroCrestOffset) + 4;
				List<Byte> idList = new ArrayList<Byte>();
				byte currentByte = 0x0;
				long currentOffset = FileReadHelper.readAddress(handler, heroCrestPointerAddress); // One more jump here.
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_LYN.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_LYN.ID); }
				// Always end with terminal
				idList.add((byte)FE7Data.CharacterClass.NONE.ID);
				byte[] byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.HERO_CREST_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.HERO_CREST_CLASSES, offset);
				
				long knightCrestOffset = FE7Data.PromotionItem.KNIGHT_CREST.getPointerAddress();
				long knightCrestPointerAddress = FileReadHelper.readAddress(handler, knightCrestOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, knightCrestPointerAddress); // One more jump here.
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_HECTOR.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_HECTOR.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_ELIWOOD.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_ELIWOOD.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.SOLDIER.ID)) { idList.add((byte)FE7Data.CharacterClass.SOLDIER.ID); }
				idList.add((byte)FE7Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.KNIGHT_CREST_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_CREST_CLASSES, offset);
				
				long masterSealOffset = FE7Data.PromotionItem.MASTER_SEAL.getPointerAddress();
				long masterSealPointerAddress = FileReadHelper.readAddress(handler, masterSealOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, masterSealPointerAddress); // One more jump here.
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_LYN.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_LYN.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_HECTOR.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_HECTOR.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_ELIWOOD.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_ELIWOOD.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.SOLDIER.ID)) { idList.add((byte)FE7Data.CharacterClass.SOLDIER.ID); }
				idList.add((byte)FE7Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.MASTER_SEAL_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.MASTER_SEAL_CLASSES, offset);
				
				promotionItemAddressPointers = new HashMap<AdditionalData, Long>();
				promotionItemAddressPointers.put(AdditionalData.HERO_CREST_CLASSES, heroCrestPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.KNIGHT_CREST_CLASSES, knightCrestPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.MASTER_SEAL_CLASSES, masterSealPointerAddress);
				
				break;
		}
			default:
				break;
		}
	}
	
	public FEItem itemWithID(int itemID) {
		return itemMap.get(itemID);
	}
	
	public int getHighestWeaponRank() {
		switch (gameType) {
		case FE6:
			return FE6Data.Item.FE6WeaponRank.S.value;
		case FE7:
			return FE7Data.Item.FE7WeaponRank.S.value;
		default:
			break;
		}
		
		return 0;
	}
	
	public FEItem[] getAllWeapons() {
		switch (gameType) {
		case FE6: {
			int arraySize = FE6Data.Item.allWeapons.size();
			return itemsFromFE6Items(FE6Data.Item.allWeapons.toArray(new FE6Data.Item[arraySize]));
		}
		case FE7: {
			int arraySize = FE7Data.Item.allWeapons.size();
			return itemsFromFE7Items(FE7Data.Item.allWeapons.toArray(new FE7Data.Item[arraySize]));
		}
		default:
			break;
		}
		
		return new FEItem[] {};
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
		
		switch (gameType) {
		case FE6: {
			long durandalStatBonusAddress = itemMap.get(FE6Data.Item.DURANDAL.ID).getStatBonusPointer(); // STR
			long bindingBladeStatBonusAddress = itemMap.get(FE6Data.Item.BINDING_BLADE.ID).getStatBonusPointer(); // DEF, RES
			long maltetStatBonusAddress = itemMap.get(FE6Data.Item.MALTET.ID).getStatBonusPointer(); // SKL
			long armadsStatBonusAddress = itemMap.get(FE6Data.Item.ARMADS.ID).getStatBonusPointer(); // DEF
			long murgleisStatBonusAddress = itemMap.get(FE6Data.Item.MURGLEIS.ID).getStatBonusPointer(); // SPD
			long forblazeStatBonusAddress = itemMap.get(FE6Data.Item.FORBLAZE.ID).getStatBonusPointer(); // LCK
			long aureolaStatBonusAddress = itemMap.get(FE6Data.Item.AUREOLA.ID).getStatBonusPointer(); // RES
			long apocalypseStatBonusAddress = itemMap.get(FE6Data.Item.APOCALYPSE.ID).getStatBonusPointer(); // MAG
			
			if (address == durandalStatBonusAddress) { return "+5 Strength"; }
			if (address == bindingBladeStatBonusAddress) { return "+5 DEF/RES"; }
			if (address == maltetStatBonusAddress) { return "+5 Skill"; }
			if (address == armadsStatBonusAddress) { return "+5 Defense"; }
			if (address == murgleisStatBonusAddress) { return "+5 Speed"; }
			if (address == forblazeStatBonusAddress) { return "+5 Luck"; }
			if (address == aureolaStatBonusAddress) { return "+5 Resistance"; }
			if (address == apocalypseStatBonusAddress) { return "+5 Magic"; }
			
			long rapierEffectivenessAddress = itemMap.get(FE6Data.Item.RAPIER.ID).getEffectivenessPointer();
			long bowEffectivenessAddress = itemMap.get(FE6Data.Item.IRON_BOW.ID).getEffectivenessPointer();
			long horseslayerEffectivenessAddress = itemMap.get(FE6Data.Item.HORSESLAYER.ID).getEffectivenessPointer();
			long hammerEffectivenessAddress = itemMap.get(FE6Data.Item.HAMMER.ID).getEffectivenessPointer();
			long dragonEffectivenessAddress = itemMap.get(FE6Data.Item.WYRMSLAYER.ID).getEffectivenessPointer();
			
			if (address == rapierEffectivenessAddress) { return shortForm ? "Eff. Infantry" : "Effective against infantry"; }
			if (address == bowEffectivenessAddress) { return shortForm ? "Eff. Fliers" : "Effective against fliers"; }
			if (address == horseslayerEffectivenessAddress) { return shortForm ? "Eff. Cavalry" : "Effective against cavalry"; }
			if (address == hammerEffectivenessAddress) { return shortForm ? "Eff. Knights" : "Effective against knights"; }
			if (address == dragonEffectivenessAddress) { return shortForm ? "Eff. Dragons" : "Effective against dragons"; }
			break;
		}
		case FE7: {
			long durandalStatBonusAddress = itemMap.get(FE7Data.Item.DURANDAL.ID).getStatBonusPointer(); // STR
			long solKattiStatBonusAddress = itemMap.get(FE7Data.Item.SOL_KATTI.ID).getStatBonusPointer(); // RES
			long armadsStatBonusAddress = itemMap.get(FE7Data.Item.ARMADS.ID).getStatBonusPointer(); // DEF
			long forblazeStatBonusAddress = itemMap.get(FE7Data.Item.FORBLAZE.ID).getStatBonusPointer(); // LCK
			
			if (address == durandalStatBonusAddress) { return isMagic ? "+5 Magic" : "+5 Strength"; }
			if (address == solKattiStatBonusAddress) { return "+5 Resistance"; }
			if (address == armadsStatBonusAddress) { return "+5 Defense"; }
			if (address == forblazeStatBonusAddress) { return "+5 Luck"; }
			
			long rapierEffectivenessAddress = itemMap.get(FE7Data.Item.RAPIER.ID).getEffectivenessPointer();
			long bowEffectivenessAddress = itemMap.get(FE7Data.Item.IRON_BOW.ID).getEffectivenessPointer();
			long horseslayerEffectivenessAddress = itemMap.get(FE7Data.Item.HORSESLAYER.ID).getEffectivenessPointer();
			long hammerEffectivenessAddress = itemMap.get(FE7Data.Item.HAMMER.ID).getEffectivenessPointer();
			long swordslayerEffectivenessAddress = itemMap.get(FE7Data.Item.SWORDSLAYER.ID).getEffectivenessPointer();
			long dragonEffectivenessAddress = itemMap.get(FE7Data.Item.DRAGON_AXE.ID).getEffectivenessPointer();
			
			if (address == rapierEffectivenessAddress) { return shortForm ? "Eff. Infantry" : "Effective against infantry"; }
			if (address == bowEffectivenessAddress) { return shortForm ? "Eff. Fliers" : "Effective against fliers"; }
			if (address == horseslayerEffectivenessAddress) { return shortForm ? "Eff. Cavalry" : "Effective against cavalry"; }
			if (address == hammerEffectivenessAddress) { return shortForm ? "Eff. Knights" : "Effective against knights"; }
			if (address == swordslayerEffectivenessAddress) { return shortForm ? "Eff. Swordfighters" : "Effective against swordfighters"; }
			if (address == dragonEffectivenessAddress) { return shortForm ? "Eff. Dragons" : "Effective against dragons"; }
			break;
		}
		default:
			break;
		}
		
		return null;
	}
	
	public long[] possibleEffectivenessAddresses() {
		switch (gameType) {
		case FE6: return new long[] { offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT)};
		case FE7: return new long[] { offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT)};
		default: return new long[] {};
		}
	}
	
	public Boolean isBasicWeapon(int itemID) {
		switch (gameType) {
		case FE6:
			return FE6Data.Item.isBasicWeapon(itemID);
		case FE7:
			return FE7Data.Item.isBasicWeapon(itemID);
		default:
			return false;
		}
	}
	
	public FEItem basicItemOfType(WeaponType type) {
		switch (gameType) {
		case FE6: {
			FE6Data.Item[] items = FE6Data.Item.basicItemsOfType(type);
			if (items.length > 0) { return itemMap.get(items[0].ID); }
		}
		case FE7: {
			FE7Data.Item[] items = FE7Data.Item.basicItemsOfType(type);
			if (items.length > 0) { return itemMap.get(items[0].ID); }
		}
		default:
			break;
		}
		
		return null;
	}
	
	public FEItem[] itemsOfTypeAndBelowRankValue(WeaponType type, int rankValue, Boolean rangedOnly) {
		switch (gameType) {
		case FE6:
			return itemsOfTypeAndBelowRank(type, FE6Data.Item.FE6WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly);
		case FE7:
			return itemsOfTypeAndBelowRank(type, FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly);
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] itemsOfTypeAndBelowRank(WeaponType type, WeaponRank rank, Boolean rangedOnly) {
		switch (gameType) {
		case FE6: {
			FE6Data.Item[] weapons = FE6Data.Item.weaponsOfTypeAndRank(type, null, rank, rangedOnly);
			return itemsFromFE6Items(weapons);
		}
		case FE7: {
			FE7Data.Item[] weapons = FE7Data.Item.weaponsOfTypeAndRank(type, null, rank, rangedOnly);
			return itemsFromFE7Items(weapons);
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] itemsOfTypeAndEqualRankValue(WeaponType type, int rankValue, Boolean rangedOnly, Boolean allowLower) {
		switch (gameType) {
		case FE6:
			return itemsOfTypeAndEqualRank(type, FE6Data.Item.FE6WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, allowLower);
		case FE7:
			return itemsOfTypeAndEqualRank(type, FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, allowLower);
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] itemsOfTypeAndEqualRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean allowLower) {
		switch (gameType) {
		case FE6: {
			if (type == WeaponType.DARK && rank == WeaponRank.E) { rank = WeaponRank.D; } // There is no E rank dark tome, so we need to set a floor of D.
			FE6Data.Item[] weapons = FE6Data.Item.weaponsOfTypeAndRank(type, rank, rank, rangedOnly);
			if ((weapons == null || weapons.length == 0) && allowLower) {
				weapons = FE6Data.Item.weaponsOfTypeAndRank(type, null, rank, rangedOnly);
			}
			return itemsFromFE6Items(weapons);
		}
		case FE7: {
			if (type == WeaponType.DARK && rank == WeaponRank.E) { rank = WeaponRank.D; } // There is no E rank dark tome, so we need to set a floor of D.
			FE7Data.Item[] weapons = FE7Data.Item.weaponsOfTypeAndRank(type, rank, rank, rangedOnly);
			if ((weapons == null || weapons.length == 0) && allowLower) {
				weapons = FE7Data.Item.weaponsOfTypeAndRank(type, null, rank, rangedOnly);
			}
			return itemsFromFE7Items(weapons);
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] prfWeaponsForClass(int classID) {
		switch (gameType) {
		case FE6:
			return itemsFromFE6Items(FE6Data.Item.prfWeaponsForClassID(classID));
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.prfWeaponsForClassID(classID));
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] getChestRewards() {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> items = new HashSet<FE6Data.Item>();
			items.addAll(FE6Data.Item.allPotentialRewards);
			return itemsFromFE6Items(items.toArray(new FE6Data.Item[items.size()]));
		}
		case FE7: {
			Set<FE7Data.Item> items = new HashSet<FE7Data.Item>();
			items.addAll(FE7Data.Item.allPotentialRewards);
			return itemsFromFE7Items(items.toArray(new FE7Data.Item[items.size()]));
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] relatedItems(int itemID) {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> items = new HashSet<FE6Data.Item>();
			FEItem item = itemWithID(itemID);
			if (item == null) {
				System.err.println("Invalid Item " + Integer.toHexString(itemID));
				break;
			}
			if (item.getType() == WeaponType.NOT_A_WEAPON) {
				if (FE6Data.Item.isStatBooster(item.getID())) {
					items.addAll(FE6Data.Item.allStatBoosters);
				}
				if (FE6Data.Item.isPromotionItem(item.getID())) {
					items.addAll(FE6Data.Item.allPromotionItems);
				}
			} else {
				items.addAll(Arrays.asList(FE6Data.Item.weaponsOfRank(item.getWeaponRank())));
				items.addAll(Arrays.asList(FE6Data.Item.weaponsOfType(item.getType())));
			}
			
			items.removeIf(i-> i.ID == itemID);
			
			return itemsFromFE6Items(items.toArray(new FE6Data.Item[items.size()]));
		}
		case FE7: {
			Set<FE7Data.Item> items = new HashSet<FE7Data.Item>();
			FEItem item = itemWithID(itemID);
			if (item == null) {
				System.err.println("Invalid Item " + Integer.toHexString(itemID));
				break;
			}
			if (item.getType() == WeaponType.NOT_A_WEAPON) {
				if (FE7Data.Item.isStatBooster(item.getID())) {
					items.addAll(FE7Data.Item.allStatBoosters);
				}
				if (FE7Data.Item.isPromotionItem(item.getID())) {
					items.addAll(FE7Data.Item.allPromotionItems);
				}
			} else {
				items.addAll(Arrays.asList(FE7Data.Item.weaponsOfRank(item.getWeaponRank())));
				items.addAll(Arrays.asList(FE7Data.Item.weaponsOfType(item.getType())));
			}
			
			items.removeIf(i-> i.ID == itemID);
			
			return itemsFromFE7Items(items.toArray(new FE7Data.Item[items.size()]));
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] lockedWeaponsToClass(int classID) {
		switch (gameType) {
		case FE6:
			return itemsFromFE6Items(FE6Data.Item.lockedWeaponsToClassID(classID));
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.lockedWeaponsToClassID(classID));
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public Boolean isHealingStaff(int itemID) {
		switch (gameType) {
		case FE6:
			return FE6Data.Item.allHealingStaves.contains(FE6Data.Item.valueOf(itemID));
		case FE7:
			return FE7Data.Item.allHealingStaves.contains(FE7Data.Item.valueOf(itemID));
		default:
			return false;
		}
	}
	
	public WeaponRank weaponRankFromValue(int rankValue) {
		switch (gameType) {
		case FE6:
			return FE6Data.Item.FE6WeaponRank.valueOf(rankValue).toGeneralRank();
		case FE7:
			return FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank();
		default:
			return null;
		}
	}
	
	public FEItem getRandomHealingStaff(WeaponRank maxRank, Random rng) {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> healingStaves = new HashSet<FE6Data.Item>(FE6Data.Item.allHealingStaves);
			if (maxRank.isLowerThan(WeaponRank.S)) { healingStaves.removeAll(FE6Data.Item.allSRank); }
			if (maxRank.isLowerThan(WeaponRank.A)) { healingStaves.removeAll(FE6Data.Item.allARank); }
			if (maxRank.isLowerThan(WeaponRank.B)) { healingStaves.removeAll(FE6Data.Item.allBRank); }
			if (maxRank.isLowerThan(WeaponRank.C)) { healingStaves.removeAll(FE6Data.Item.allCRank); }
			if (maxRank.isLowerThan(WeaponRank.D)) { healingStaves.removeAll(FE6Data.Item.allDRank); }
			FE6Data.Item[] remainingItems = healingStaves.toArray(new FE6Data.Item[healingStaves.size()]);
			return itemMap.get(remainingItems[rng.nextInt(remainingItems.length)].ID);
		}
		case FE7: {
			Set<FE7Data.Item> healingStaves = new HashSet<FE7Data.Item>(FE7Data.Item.allHealingStaves);
			if (maxRank.isLowerThan(WeaponRank.S)) { healingStaves.removeAll(FE7Data.Item.allSRank); }
			if (maxRank.isLowerThan(WeaponRank.A)) { healingStaves.removeAll(FE7Data.Item.allARank); }
			if (maxRank.isLowerThan(WeaponRank.B)) { healingStaves.removeAll(FE7Data.Item.allBRank); }
			if (maxRank.isLowerThan(WeaponRank.C)) { healingStaves.removeAll(FE7Data.Item.allCRank); }
			if (maxRank.isLowerThan(WeaponRank.D)) { healingStaves.removeAll(FE7Data.Item.allDRank); }
			FE7Data.Item[] remainingItems = healingStaves.toArray(new FE7Data.Item[healingStaves.size()]);
			return itemMap.get(remainingItems[rng.nextInt(remainingItems.length)].ID);
		}
		default:
			return null;
		}
	}
	
	public FEItem[] formerThiefInventory() {
		switch (gameType) {
		case FE6:
			return itemsFromFE6Items(FE6Data.Item.formerThiefKit());
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.formerThiefKit());
		default:
			return new FEItem[] {};
		}
	}
	
	public FEItem[] thiefItemsToRemove() {
		switch (gameType) {
		case FE6:
			return itemsFromFE6Items(new FE6Data.Item[] {FE6Data.Item.LOCKPICK});
		case FE7:
			return itemsFromFE7Items(new FE7Data.Item[] {FE7Data.Item.LOCKPICK});
		default:
			return new FEItem[] {};
		}
	}
	
	public FEItem[] specialInventoryForClass(int classID, Random rng) {
		switch (gameType) {
		case FE6: {
			FE6Data.Item[] items = FE6Data.Item.specialClassKit(classID, rng);
			if (items != null) {
				return itemsFromFE6Items(items);
			}
			break;
		}
		case FE7: {
			FE7Data.Item[] items = FE7Data.Item.specialClassKit(classID, rng);
			if (items != null) {
				return itemsFromFE7Items(items);
			}
			break;
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public void commit() {
		for (FEItem item : itemMap.values()) {
			item.commitChanges();
		}
		
		spellAnimations.commit();
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FEItem item : itemMap.values()) {
			item.commitChanges();
			if (item.hasCommittedChanges()) {
				Diff charDiff = new Diff(item.getAddressOffset(), item.getData().length, item.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
		
		spellAnimations.compileDiffs(compiler);
		
		for (AdditionalData promotionItem : promotionItemAddressPointers.keySet()) {
			if (freeSpace.hasOffsetForKey(promotionItem.key)) {
				long offset = freeSpace.getOffsetForKey(promotionItem.key);
				byte[] addressByteArray = WhyDoesJavaNotHaveThese.bytesFromAddress(offset);
				long targetOffset = promotionItemAddressPointers.get(promotionItem);
				compiler.addDiff(new Diff(targetOffset, addressByteArray.length, addressByteArray, null));
			}
		}
	}
	
	private FEItem[] itemsFromFE7Items(FE7Data.Item[] fe7Items) {
		if (fe7Items == null) {
			return new FEItem[] {};
		}
		
		FEItem[] result = new FEItem[fe7Items.length];
		for (int i = 0; i < fe7Items.length; i++) {
			result[i] = itemMap.get(fe7Items[i].ID);
		}
		
		return result;
	}
	
	private FEItem[] itemsFromFE6Items(FE6Data.Item[] fe6Items) {
		if (fe6Items == null) {
			return new FEItem[] {};
		}
		
		FEItem[] result = new FEItem[fe6Items.length];
		for (int i = 0; i < fe6Items.length; i++) {
			result[i] = itemMap.get(fe6Items[i].ID);
		}
		
		return result;
	}
	
	public void recordWeapons(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, TextLoader textData, FileHandler handler) {
		for (FEItem item : getAllWeapons()) {
			recordWeapon(rk, item, isInitial, classData, textData, handler);
		}
	}
	
	private void recordWeapon(RecordKeeper rk, FEItem item, Boolean isInitial, ClassDataLoader classData, TextLoader textData, FileHandler handler) {
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
					try {
						handler.setNextReadOffset(effectiveClasses);
						byte[] classes = handler.continueReadingBytesUpToNextTerminator(effectiveClasses + 100);
						List<String> classList = new ArrayList<String>();
						for (byte classID : classes) {
							if (classID == 0) { break; }
							FEClass classObject = classData.classForID(classID);
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
					} catch (IOException e) {
						e.printStackTrace();
						rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "Error");
					}
				} else {
					rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "No input handler.");
				}
			} else {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "None");
			}
			
			int ability1Value = item.getAbility1();
			int ability2Value = item.getAbility2();
			int ability3Value = item.getAbility3();
			int effectValue = item.getWeaponEffect();
			switch(gameType) {
			case FE6:
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", FE6Data.Item.Ability1Mask.stringOfActiveAbilities(ability1Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", FE6Data.Item.Ability2Mask.stringOfActiveAbilities(ability2Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effect", FE6Data.Item.WeaponEffect.stringOfActiveEffect(effectValue));
				break;
			case FE7:
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", FE7Data.Item.Ability1Mask.stringOfActiveAbilities(ability1Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", FE7Data.Item.Ability2Mask.stringOfActiveAbilities(ability2Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 3", FE7Data.Item.Ability3Mask.stringOfActiveAbilities(ability3Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effect", FE7Data.Item.WeaponEffect.stringOfActiveEffect(effectValue));
				break;
			default:
				break;
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
					try {
						handler.setNextReadOffset(effectiveClasses);
						byte[] classes = handler.continueReadingBytesUpToNextTerminator(effectiveClasses + 100);
						List<String> classList = new ArrayList<String>();
						for (byte classID : classes) {
							if (classID == 0) { break; }
							FEClass classObject = classData.classForID(classID);
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
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "Error");
					}
				} else {
					rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "No output handler.");
				}
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "None");
			}
			
			int ability1Value = item.getAbility1();
			int ability2Value = item.getAbility2();
			int ability3Value = item.getAbility3();
			int effectValue = item.getWeaponEffect();
			switch(gameType) {
			case FE6:
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", FE6Data.Item.Ability1Mask.stringOfActiveAbilities(ability1Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", FE6Data.Item.Ability2Mask.stringOfActiveAbilities(ability2Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effect", FE6Data.Item.WeaponEffect.stringOfActiveEffect(effectValue));
				break;
			case FE7:
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", FE7Data.Item.Ability1Mask.stringOfActiveAbilities(ability1Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", FE7Data.Item.Ability2Mask.stringOfActiveAbilities(ability2Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 3", FE7Data.Item.Ability3Mask.stringOfActiveAbilities(ability3Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effect", FE7Data.Item.WeaponEffect.stringOfActiveEffect(effectValue));
				break;
			default:
				break;
			}
			
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Range", String.format("%d ~ %d", item.getMinRange(), item.getMaxRange()));
		}
	}
}
