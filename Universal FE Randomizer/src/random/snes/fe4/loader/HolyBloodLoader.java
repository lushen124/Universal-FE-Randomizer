package random.snes.fe4.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4HolyBlood;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;

public class HolyBloodLoader {
	
	private Map<FE4Data.HolyBlood, FE4HolyBlood> bloodMap;
	
	private boolean isHeadered;
	
	public static final String RecordKeeperCategoryKey = "Holy Blood";
	
	public HolyBloodLoader(FileHandler handler, boolean headered) {
		super();
		
		this.isHeadered = headered;
		
		bloodMap = new HashMap<FE4Data.HolyBlood, FE4HolyBlood>();
		
		long tableOffset = FE4Data.HolyBloodDataOffset;
		if (this.isHeadered) { tableOffset -= 0x200; }
		FE4Data.HolyBlood[] bloodOrder = FE4Data.HolyBlood.orderedByDataTable();
		assert bloodOrder.length == FE4Data.HolyBloodDataCount;
		for (int i = 0; i < FE4Data.HolyBloodDataCount; i++) {
			FE4Data.HolyBlood currentBlood = bloodOrder[i];
			long address = tableOffset + (i * FE4Data.HolyBloodDataSize);
			byte[] data = handler.readBytesAtOffset(address, FE4Data.HolyBloodDataSize);
			FE4HolyBlood holyBlood = new FE4HolyBlood(data, address);
			bloodMap.put(currentBlood, holyBlood);
		}
	}

	public List<FE4HolyBlood> allHolyBlood() {
		return new ArrayList<FE4HolyBlood>(bloodMap.values());
	}
	
	public FE4HolyBlood holyBloodByType(FE4Data.HolyBlood blood) {
		return bloodMap.get(blood);
	}
	
	public void commit() {
		for (FE4HolyBlood blood : bloodMap.values()) {
			blood.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FE4HolyBlood blood : bloodMap.values()) {
			blood.commitChanges();
			if (blood.hasCommittedChanges()) {
				Diff bloodChanges = new Diff(blood.getAddressOffset(), blood.getData().length, blood.getData(), null);
				compiler.addDiff(bloodChanges);
			}
		}
	}
}
