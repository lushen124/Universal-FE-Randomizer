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
import util.recordkeeper.RecordKeeper;

public class HolyBloodLoader {
	
	private Map<FE4Data.HolyBlood, FE4HolyBlood> bloodMap;
	
	private boolean isHeadered;
	
	public static final String RecordKeeperCategoryKey = "Holy Blood";
	
	public HolyBloodLoader(FileHandler handler, boolean headered) {
		super();
		
		this.isHeadered = headered;
		
		bloodMap = new HashMap<FE4Data.HolyBlood, FE4HolyBlood>();
		
		long tableOffset = FE4Data.HolyBloodDataOffset;
		if (!this.isHeadered) { tableOffset -= 0x200; }
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
	
	public void recordHolyBlood(RecordKeeper rk, Boolean isInitial) {
		for (FE4HolyBlood blood : bloodMap.values()) {
			recordBlood(rk, isInitial, blood);
		}
	}
	
	private void recordData(RecordKeeper rk, boolean isInitial, String entryKey, String key, String value) {
		if (isInitial) {
			rk.recordOriginalEntry(RecordKeeperCategoryKey, entryKey, key, value);
		} else {
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, entryKey, key, value);
		}
	}
	
	private void recordBlood(RecordKeeper rk, boolean isInitial, FE4HolyBlood blood) {
		FE4Data.Item holyWeapon = FE4Data.Item.valueOf(blood.getHolyWeaponID());
		FE4Data.HolyBlood bloodType = holyWeapon.holyBloodForHolyWeapon();
		String name = bloodType.toString();
		
		recordData(rk, isInitial, name, "HP Growth Bonus", String.format("+%d%%", blood.getHPGrowthBonus()));
		recordData(rk, isInitial, name, "STR Growth Bonus", String.format("+%d%%", blood.getSTRGrowthBonus()));
		recordData(rk, isInitial, name, "MAG Growth Bonus", String.format("+%d%%", blood.getMAGGrowthBonus()));
		recordData(rk, isInitial, name, "SKL Growth Bonus", String.format("+%d%%", blood.getSKLGrowthBonus()));
		recordData(rk, isInitial, name, "SPD Growth Bonus", String.format("+%d%%", blood.getSPDGrowthBonus()));
		recordData(rk, isInitial, name, "LCK Growth Bonus", String.format("+%d%%", blood.getLCKGrowthBonus()));
		recordData(rk, isInitial, name, "DEF Growth Bonus", String.format("+%d%%", blood.getDEFGrowthBonus()));
		recordData(rk, isInitial, name, "RES Growth Bonus", String.format("+%d%%", blood.getRESGrowthBonus()));
		
		recordData(rk, isInitial, name, holyWeapon.toString() + "'s STR Bonus", Integer.toString(blood.getHolyWeaponSTRBonus()));
		recordData(rk, isInitial, name, holyWeapon.toString() + "'s MAG Bonus", Integer.toString(blood.getHolyWeaponMAGBonus()));
		recordData(rk, isInitial, name, holyWeapon.toString() + "'s SKL Bonus", Integer.toString(blood.getHolyWeaponSKLBonus()));
		recordData(rk, isInitial, name, holyWeapon.toString() + "'s SPD Bonus", Integer.toString(blood.getHolyWeaponSPDBonus()));
		recordData(rk, isInitial, name, holyWeapon.toString() + "'s DEF Bonus", Integer.toString(blood.getHolyWeaponDEFBonus()));
		recordData(rk, isInitial, name, holyWeapon.toString() + "'s RES Bonus", Integer.toString(blood.getHolyWeaponRESBonus()));
	}
}
