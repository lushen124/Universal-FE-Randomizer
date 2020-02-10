package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Data.Item;
import fedata.gcnwii.fe9.FE9Item;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9ItemDataLoader {
	
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
