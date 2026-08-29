package random.snes.fe4.loader;

import java.util.HashMap;
import java.util.Map;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4Weapon;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.recordkeeper.RecordKeeper;

public class ItemDataLoader {
	
	private Map<FE4Data.Item, FE4Weapon> weaponMap = new HashMap<FE4Data.Item, FE4Weapon>();
	
	public static final String RecordKeeperCategoryKey = "Items";
	
	public ItemDataLoader(FileHandler handler, boolean headered) {
		super();
		
		long baseOffset = FE4Data.ItemTableOffset;
		
		for (int i = 0; i < FE4Data.ItemTableCount; i++) {
			long address = baseOffset + (i * FE4Data.ItemSize);
			if (!headered) {
				address -= 0x200;
			}
			
			byte[] itemData = handler.readBytesAtOffset(address, FE4Data.ItemSize);
			FE4Weapon itemObject = new FE4Weapon(itemData, address);
			FE4Data.Item fe4Item = FE4Data.Item.valueOf(itemObject.getID());
			weaponMap.put(fe4Item, itemObject);
		}
	}
	
	public FE4Weapon itemForID(int itemID) {
		FE4Data.Item item = FE4Data.Item.valueOf(itemID);
		if (item == null) { return null; }
		return weaponMap.get(item);
	}
	
	public void commit() {
		for (FE4Weapon item : weaponMap.values()) {
			item.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FE4Weapon item : weaponMap.values()) {
			item.commitChanges();
			if (item.hasCommittedChanges()) {
				Diff itemDiff = new Diff(item.getAddressOffset(), item.getData().length, item.getData(), null);
				compiler.addDiff(itemDiff);
			}
		}
	}
	
	public void recordItems(RecordKeeper rk, Boolean isInitial) {
		for (FE4Data.Item fe4Item : weaponMap.keySet()) {
			FE4Weapon item = weaponMap.get(fe4Item);
			recordItem(rk, isInitial, fe4Item, item);
		}
	}
	
	private void recordData(RecordKeeper rk, boolean isInitial, String category, String entryKey, String key, String value) {
		if (isInitial) {
			rk.recordOriginalEntry(category, entryKey, key, value);
		} else {
			rk.recordUpdatedEntry(category, entryKey, key, value);
		}
	}
	
	private void recordItem(RecordKeeper rk, boolean isInitial, FE4Data.Item fe4Item, FE4Weapon itemObject) {
		String name = fe4Item.toString();
		
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Cost", Integer.toString(itemObject.getPrice()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Durability", Integer.toString(itemObject.getDurability()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Power", Integer.toString(itemObject.getPower()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Accuracy", Integer.toString(itemObject.getAccuracy()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Weight", Integer.toString(itemObject.getWeight()));
	}

}
