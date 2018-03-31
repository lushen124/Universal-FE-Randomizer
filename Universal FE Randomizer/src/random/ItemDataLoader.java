package random;

import java.util.HashMap;
import java.util.Map;

import fedata.FEBase;
import fedata.FEItem;
import fedata.fe7.FE7Data;
import fedata.fe7.FE7Item;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;

public class ItemDataLoader {
private FEBase.GameType gameType;
	
	private Map<Integer, FEItem> itemMap = new HashMap<Integer, FEItem>();

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
				break;
			default:
				break;
		}
	}
	
	public FEItem itemWithID(int itemID) {
		return itemMap.get(itemID);
	}
	
	public FEItem[] itemsOfTypeAndBelowRankValue(WeaponType type, int rankValue) {
		switch (gameType) {
		case FE7:
			return itemsOfTypeAndBelowRank(type, FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank());
		default:
			break;
		}
		
		return null;
	}
	
	public FEItem[] itemsOfTypeAndBelowRank(WeaponType type, WeaponRank rank) {
		switch (gameType) {
		case FE7:
			FE7Data.Item[] weapons = FE7Data.Item.weaponsOfTypeAndRank(type, null, rank);
			return itemsFromFE7Items(weapons);
		default:
			break;
		}
		
		return null;
	}
	
	public FEItem[] formerThiefInventory() {
		switch (gameType) {
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.formerThiefKit());
		default:
			return null;
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
		
		return null;
	}
	
	public void commit() {
		for (FEItem item : itemMap.values()) {
			item.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FEItem item : itemMap.values()) {
			item.commitChanges();
			if (item.hasCommittedChanges()) {
				Diff charDiff = new Diff(item.getAddressOffset(), item.getData().length, item.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
	
	private FEItem[] itemsFromFE7Items(FE7Data.Item[] fe7Items) {
		FEItem[] result = new FEItem[fe7Items.length];
		for (int i = 0; i < fe7Items.length; i++) {
			result[i] = itemMap.get(fe7Items[i].ID);
		}
		
		return result;
	}
}
