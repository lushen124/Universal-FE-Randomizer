package random.gba.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gba.GBAFEItemData;
import fedata.gba.general.GBAFEStatboost;
import fedata.gba.general.GBAFEStatboostProvider;
import io.FileHandler;
import ui.model.StatboosterOptions;
import util.Diff;
import util.DiffCompiler;
import util.recordkeeper.RecordKeeper;

/**
 * Class for loading the Statboost Data from the Rom  
 */
public class StatboostLoader {
	public static final String RecordKeeperCategoryStatboostersKey = "Statboosters";

	
	private List<GBAFEStatboost> statboostsData = new ArrayList<>();
	private GBAFEStatboostProvider provider;

	public StatboostLoader(GBAFEStatboostProvider provider, FileHandler handler) {
		this.provider = provider;
		long baseAddress = provider.getBaseAddress();
		for (int i = 0; i < provider.getNumberEntries(); i++) {
			long offset = baseAddress + (provider.getEntrySize() * i);
			byte[] originalData = handler.readBytesAtOffset(offset, provider.getEntrySize());
			statboostsData.add(new GBAFEStatboost(originalData, offset));
		}
	}

	/**
	 * Returns a list of all GBAFEStatboosts which are used for Statboosing Consumable items. 
	 * Boots and Body Rings are only included if the user opts in.
	 */
	public List<GBAFEStatboost> getStatboosters(boolean includeBoots, boolean includeBodyRing) {
		List<GBAFEStatboost> statboosters = new ArrayList<>();
		for (int i = 0; i < this.statboostsData.size(); i++) {
			if (provider.isStatboosterIndex(i)) {
				GBAFEStatboost statboost = this.statboostsData.get(i);
				if (!statboost.isBodyRing() && !statboost.isBoots() || // Happy case, normal stat booster
						statboost.isBodyRing() && includeBodyRing || // Only include body rings if user opts in.
						statboost.isBoots() && includeBoots) { // Only include boots if user opts in.
					System.out.println("Index "+i+" isBoots: "+statboost.isBoots()+" isBodyRing: "+statboost.isBodyRing());
					statboosters.add(statboost);
				}
			}
		}

		return statboosters;
	}
	
	public void recordInitial(RecordKeeper rk, ItemDataLoader itemData, StatboosterOptions options) {
		if (options == null) { return; }
		for (GBAFEStatboost boost : getStatboosters(options.includeMov, options.includeCon)) {
			for (GBAFEItemData item : itemData.itemsByStatboostAddress(boost.getAddressOffset())) {
				String name = item.displayString();
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "", "Original");
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "HP", String.valueOf(boost.dao.hp));
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "POW", String.valueOf(boost.dao.str));
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "SKL", String.valueOf(boost.dao.skl));
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "SPD", String.valueOf(boost.dao.spd));
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "LCK", String.valueOf(boost.dao.lck));
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "DEF", String.valueOf(boost.dao.def));
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "RES", String.valueOf(boost.dao.res));
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "MOV", String.valueOf(boost.dao.mov));
				rk.recordOriginalEntry(RecordKeeperCategoryStatboostersKey, name, "CON", String.valueOf(boost.dao.con));
			}
		}
	}
	
	public void recordUpdated(RecordKeeper rk, ItemDataLoader itemData, StatboosterOptions options) {
		if (options == null) { return; }
		for (GBAFEStatboost boost : getStatboosters(options.includeMov, options.includeCon)) {
			for (GBAFEItemData item : itemData.itemsByStatboostAddress(boost.getAddressOffset())) {
				String name = item.displayString();
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "", "Modified");
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "HP", String.valueOf(boost.dao.hp));
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "POW", String.valueOf(boost.dao.str));
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "SKL", String.valueOf(boost.dao.skl));
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "SPD", String.valueOf(boost.dao.spd));
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "LCK", String.valueOf(boost.dao.lck));
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "DEF", String.valueOf(boost.dao.def));
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "RES", String.valueOf(boost.dao.res));
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "MOV", String.valueOf(boost.dao.mov));
				rk.recordUpdatedEntry(RecordKeeperCategoryStatboostersKey, name, "CON", String.valueOf(boost.dao.con));
			}
		}
	}

	public void commit() {
		for (GBAFEStatboost statbooster : statboostsData) {
			statbooster.commitChanges();
		}
	}

	public void compileDiffs(DiffCompiler compiler) {
		for (GBAFEStatboost statbooster : statboostsData) {
			statbooster.commitChanges();

			if (!statbooster.hasCommittedChanges()) {
				continue;
			}

			Diff diff = new Diff(statbooster.getAddressOffset(), statbooster.getData().length, statbooster.getData(),
					null);
			compiler.addDiff(diff);
		}
	}
}