package random.gba.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gba.general.GBAFEStatboost;
import fedata.gba.general.GBAFEStatboostProvider;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;

/**
 * Class for loading the Statboost Data from the Rom  
 */
public class StatboostLoader {
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