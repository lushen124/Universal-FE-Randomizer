package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.List;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Item;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9ItemDataLoader {
	
	List<FE9Item> allItems;
	
	public FE9ItemDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allItems = new ArrayList<FE9Item>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.ItemDataFilename);
		long offset = FE9Data.ItemDataStartOffset;
		for (int i = 0; i < FE9Data.ItemCount; i++) {
			long dataOffset = offset + i * FE9Data.ItemDataSize;
			byte[] data = handler.readBytesAtOffset(dataOffset, FE9Data.ItemDataSize);
			FE9Item item = new FE9Item(data, dataOffset);
			allItems.add(item);
			
			debugPrintItem(item, handler, commonTextLoader);
		}
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
