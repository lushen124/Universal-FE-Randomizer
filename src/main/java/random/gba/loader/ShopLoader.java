package random.gba.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedata.gba.GBAFEItemData;
import fedata.gba.general.GBAFEItem;
import fedata.gba.general.GBAFEItemProvider;
import fedata.gba.general.GBAFEShop;
import fedata.gba.general.GBAFEShopProvider;
import io.FileHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.RecordKeeper;

public class ShopLoader {

	private GBAFEShopProvider provider;
	private FreeSpaceManager freeSpace;
	
	private Map<GBAFEShop, List<GBAFEItemData>> itemMap;
	private Map<GBAFEShop, List<GBAFEItemData>> updatedItemMap;
	private Map<GBAFEShop, Long> listAddressMap;
	
	public static final String RecordKeeperCategoryShopKey = "Shops";
	
	public ShopLoader(GBAFEShopProvider provider, GBAFEItemProvider itemProvider, ItemDataLoader itemData, FileHandler handler, FreeSpaceManager freeSpace) {
		super();
		
		this.freeSpace = freeSpace;
		this.provider = provider;
		
		itemMap = new HashMap<GBAFEShop, List<GBAFEItemData>>();
		listAddressMap = new HashMap<GBAFEShop, Long>();
		
		for (GBAFEShop shop : provider.orderedShops()) {
			long pointerOffset = shop.getPointerOffset();
			long originalShopList = shop.getOriginalShopAddress();
			
			long readShopAddress = WhyDoesJavaNotHaveThese.longValueFromByteArray(handler.readBytesAtOffset(pointerOffset, 4), true) - 0x8000000L;
			if (readShopAddress != originalShopList) {
				DebugPrinter.error(DebugPrinter.Key.GBA_SHOP_LOADER, "Address mismatch for shop: " + shop.toString());
				continue;
			}
			DebugPrinter.log(DebugPrinter.Key.GBA_SHOP_LOADER, "Loading shop: " + shop.toString());
			List<GBAFEItemData> itemsSold = new ArrayList<GBAFEItemData>();
			handler.setNextReadOffset(readShopAddress);
			byte[] readBuffer;
			do {
				readBuffer = handler.continueReadingBytes(2);
				if (readBuffer[0] != 0) {
					GBAFEItemData item = itemData.itemWithID(readBuffer[0] & 0xFF);
					DebugPrinter.log(DebugPrinter.Key.GBA_SHOP_LOADER, "Found Item: " + itemData.itemWithID(item.getID()).displayString());
					itemsSold.add(item);
				}
			} while (readBuffer[0] != 0);
			DebugPrinter.log(DebugPrinter.Key.GBA_SHOP_LOADER, "Finished shop: " + shop.toString());
			itemMap.put(shop, itemsSold);
			listAddressMap.put(shop, readShopAddress);
		}
		
		updatedItemMap = new HashMap<GBAFEShop, List<GBAFEItemData>>();
	}
	
	public List<GBAFEShop> getAllShops() {
		return provider.orderedShops();
	}
	
	public List<GBAFEItemData> getItemsInShop(GBAFEShop shop) {
		if (shopWasUpdated(shop)) {
			return updatedItemMap.get(shop);	
		} else {
			return itemMap.get(shop);
		}
	}
	
	public boolean shopWasUpdated(GBAFEShop shop) {
		return updatedItemMap.containsKey(shop);
	}
	
	public void setItemsInShop(GBAFEShop shop, List<GBAFEItemData> items) {	
		updatedItemMap.put(shop, items);
	}
	
	public boolean isArmory(GBAFEShop shop) {
		return provider.allArmories().contains(shop);
	}
	
	public boolean isVendor(GBAFEShop shop) {
		return provider.allVendors().contains(shop);
	}
	
	public boolean isMapShop(GBAFEShop shop) {
		return provider.isMapShop(shop);
	}
	
	public boolean isSecret(GBAFEShop shop) {
		return provider.allSecretShops().contains(shop);
	}
	
	public Set<GBAFEShop> linkedShops(GBAFEShop shop) {
		return shop.groupedShops();
	}
	
	public void compileDiffs(DiffCompiler diffCompiler) {
		for (GBAFEShop shop : provider.allShops()) {
			if (shopWasUpdated(shop) == false) { continue; }
			
			long originalOffset = listAddressMap.get(shop);
			
			List<GBAFEItemData> newItems = updatedItemMap.get(shop);
			int bytesNeeded = (newItems.size() + 1) * 2;
			byte[] itemData = new byte[bytesNeeded];
			int index = 0;
			for (GBAFEItemData item : newItems) {
				byte itemID = (byte)(0xFF & item.getID());
				itemData[index++] = itemID;
				itemData[index++] = 0;
			}
			itemData[index++] = 0;
			itemData[index++] = 0;
			
			long newOffset = freeSpace.reserveSpace(bytesNeeded, shop.toString(), true);
			
			diffCompiler.addDiff(new Diff(newOffset, bytesNeeded, itemData, null));
			diffCompiler.addDiff(new Diff(shop.getPointerOffset(), 4, WhyDoesJavaNotHaveThese.gbaAddressFromOffset(newOffset), WhyDoesJavaNotHaveThese.gbaAddressFromOffset(originalOffset)));
			
			listAddressMap.put(shop, newOffset);
		}
	}
	
	public void recordShopData(RecordKeeper rk, Boolean isInitial, ItemDataLoader itemData, TextLoader textData) {
		for (GBAFEShop shop : provider.orderedShops()) {
			recordShop(rk, shop, isInitial, itemData, textData, RecordKeeperCategoryShopKey);
		}
	}

	private void recordShop(RecordKeeper rk, GBAFEShop shop, Boolean isInitial, ItemDataLoader itemData, TextLoader textData, String category) {
		if (isInitial) {
			rk.recordOriginalEntry(category, shop.toString(), "ID", shop.toString());
			rk.recordOriginalEntry(category, shop.toString(), "Pointer Offset", String.format("0x%08X", shop.getPointerOffset()));
			rk.recordOriginalEntry(category, shop.toString(), "Shop List Address", String.format("0x%08X", shop.getOriginalShopAddress()));
			
			List<GBAFEItemData> items = itemMap.get(shop);
			for (int i = 0; i < items.size(); i++) {
				GBAFEItemData item = items.get(i);
				rk.recordOriginalEntry(category, shop.toString(), "Item #" + (i + 1), textData.getStringAtIndex(itemData.itemWithID(item.getID()).getNameIndex(), true));
			}
		} else {
			rk.recordUpdatedEntry(category, shop.toString(), "ID", shop.toString());
			rk.recordUpdatedEntry(category, shop.toString(), "Pointer Offset", String.format("0x%08X", shop.getPointerOffset()));
			rk.recordUpdatedEntry(category, shop.toString(), "Shop List Address", String.format("0x%08X", listAddressMap.get(shop)));
			
			List<GBAFEItemData> items = updatedItemMap.get(shop);
			if (items == null) {
				items = itemMap.get(shop);
			}
			for (int i = 0; i < items.size(); i++) {
				GBAFEItemData item = items.get(i);
				rk.recordUpdatedEntry(category, shop.toString(), "Item #" + (i + 1), textData.getStringAtIndex(itemData.itemWithID(item.getID()).getNameIndex(), true));
			}
		}
		
	}
}
