package random;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

public class ItemDataLoader {
private FEBase.GameType gameType;
	
	private Map<Integer, FEItem> itemMap = new HashMap<Integer, FEItem>();
	
	// TODO: Put this somewhere else.
	public FESpellAnimationCollection spellAnimations;

	public ItemDataLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE7:
				for (FE7Data.Item item : FE7Data.Item.values()) {
					if (item == FE7Data.Item.NONE) {
						continue;
					}
					
					long offset = FE7Data.Item.dataOffsetForItem(item);
					byte[] itemData = handler.readBytesAtOffset(offset, FE7Data.BytesPerItem);
					itemMap.put(item.ID, new FE7Item(itemData, offset));
				}
				
				spellAnimations = new FE7SpellAnimationCollection(handler.readBytesAtOffset(FE7Data.DefaultSpellAnimationTableOffset, 
						FE7Data.NumberOfSpellAnimations * FE7Data.BytesPerSpellAnimation), FE7Data.DefaultSpellAnimationTableOffset);
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
				System.out.println("Invalid Item " + Integer.toHexString(itemID));
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
	
	public FEItem[] specialInventoryForClass(int classID) {
		switch (gameType) {
		case FE7:
			FE7Data.Item[] items = FE7Data.Item.specialClassKit(classID);
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
		
		switch (gameType) {
		case FE7:
			Map<Long, byte[]> aux = FE7Data.auxiliaryData();
			for (long offset : aux.keySet()) {
				Diff auxDiff = new Diff(offset, aux.get(offset).length, aux.get(offset), null);
				compiler.addDiff(auxDiff);
			}
			break;
		default:
			break;
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
