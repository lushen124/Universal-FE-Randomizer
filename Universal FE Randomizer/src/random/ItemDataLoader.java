package random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.FEBase;
import fedata.FEItem;
import fedata.FESpellAnimationCollection;
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
import util.WhyDoesJavaNotHaveThese;

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
	
	public int ironSwordDescriptionIndex;
	
	private FreeSpaceManager freeSpace;
	private Map<AdditionalData, Long> offsetsForAdditionalData;
	private Map<AdditionalData, Long> promotionItemAddressPointers;

	public ItemDataLoader(FEBase.GameType gameType, FileHandler handler, FreeSpaceManager freeSpace) {
		super();
		this.gameType = gameType;
		this.freeSpace = freeSpace;
		
		switch (gameType) {
			case FE7:
				long baseAddress = FileReadHelper.readAddress(handler, FE7Data.ItemTablePointer);
				for (FE7Data.Item item : FE7Data.Item.values()) {
					if (item == FE7Data.Item.NONE) {
						continue;
					}	
					long offset = baseAddress + (FE7Data.BytesPerItem * item.ID);
					byte[] itemData = handler.readBytesAtOffset(offset, FE7Data.BytesPerItem);
					itemMap.put(item.ID, new FE7Item(itemData, offset));
					
					FEItem ironSword = itemMap.get(FE7Data.Item.IRON_SWORD.ID);
					ironSwordDescriptionIndex = ironSword.getDescriptionIndex();
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
				long currentOffset = heroCrestPointerAddress;
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
				currentOffset = knightCrestPointerAddress;
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
				currentOffset = masterSealPointerAddress;
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
			default:
				break;
		}
	}
	
	public FEItem itemWithID(int itemID) {
		return itemMap.get(itemID);
	}
	
	public int getHighestWeaponRank() {
		switch (gameType) {
		case FE7:
			return FE7Data.Item.FE7WeaponRank.S.value;
		default:
			break;
		}
		
		return 0;
	}
	
	public FEItem[] getAllWeapons() {
		switch (gameType) {
		case FE7:
			int arraySize = FE7Data.Item.allWeapons.size();
			return itemsFromFE7Items(FE7Data.Item.allWeapons.toArray(new FE7Data.Item[arraySize]));
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
	
	public long[] possibleEffectivenessAddresses() {
		return new long[] { offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT)};
	}
	
	public FEItem[] itemsOfTypeAndBelowRankValue(WeaponType type, int rankValue, Boolean rangedOnly) {
		switch (gameType) {
		case FE7:
			return itemsOfTypeAndBelowRank(type, FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly);
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] itemsOfTypeAndBelowRank(WeaponType type, WeaponRank rank, Boolean rangedOnly) {
		switch (gameType) {
		case FE7:
			FE7Data.Item[] weapons = FE7Data.Item.weaponsOfTypeAndRank(type, null, rank, rangedOnly);
			return itemsFromFE7Items(weapons);
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] itemsOfTypeAndEqualRankValue(WeaponType type, int rankValue, Boolean rangedOnly, Boolean allowLower) {
		switch (gameType) {
		case FE7:
			return itemsOfTypeAndEqualRank(type, FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, allowLower);
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] itemsOfTypeAndEqualRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean allowLower) {
		switch (gameType) {
		case FE7:
			if (type == WeaponType.DARK && rank == WeaponRank.E) { rank = WeaponRank.D; } // There is no E rank dark tome, so we need to set a floor of D.
			FE7Data.Item[] weapons = FE7Data.Item.weaponsOfTypeAndRank(type, rank, rank, rangedOnly);
			if ((weapons == null || weapons.length == 0) && allowLower) {
				weapons = FE7Data.Item.weaponsOfTypeAndRank(type, null, rank, rangedOnly);
			}
			return itemsFromFE7Items(weapons);
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] prfWeaponsForClass(int classID) {
		switch (gameType) {
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.prfWeaponsForClassID(classID));
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] getChestRewards() {
		switch (gameType) {
		case FE7:
			Set<FE7Data.Item> items = new HashSet<FE7Data.Item>();
			items.addAll(FE7Data.Item.allPotentialRewards);
			return itemsFromFE7Items(items.toArray(new FE7Data.Item[items.size()]));
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] relatedItems(int itemID) {
		switch (gameType) {
		case FE7:
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
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] lockedWeaponsToClass(int classID) {
		switch (gameType) {
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.lockedWeaponsToClassID(classID));
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] formerThiefInventory() {
		switch (gameType) {
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.formerThiefKit());
		default:
			return new FEItem[] {};
		}
	}
	
	public FEItem[] thiefItemsToRemove() {
		switch (gameType) {
		case FE7:
			return itemsFromFE7Items(new FE7Data.Item[] {FE7Data.Item.LOCKPICK});
		default:
			return new FEItem[] {};
		}
	}
	
	public FEItem[] specialInventoryForClass(int classID, Random rng) {
		switch (gameType) {
		case FE7:
			FE7Data.Item[] items = FE7Data.Item.specialClassKit(classID, rng);
			if (items != null) {
				return itemsFromFE7Items(items);
			}
			break;
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
}
