package random.snes.fe4.loader;

import java.util.List;
import java.util.Map;

import fedata.snes.fe4.FE4Class;
import fedata.snes.fe4.FE4Data;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.recordkeeper.RecordKeeper;

public class ClassDataLoader {
	
	private Map<FE4Data.CharacterClass, FE4Class> classMap;
	
	public static final String RecordKeeperCategoryKey = "Classes";
	
	public ClassDataLoader(FileHandler handler, boolean headered) {
		super();
		
		long baseOffset = FE4Data.ClassTableOffset;
		
		for (int i = 0; i < FE4Data.ClassTableCount; i++) {
			long address = baseOffset + (i * FE4Data.ClassTableItemSize);
			if (!headered) {
				address -= 0x200;
			}
			byte[] classData = handler.readBytesAtOffset(address, FE4Data.ClassTableItemSize);
			FE4Class classObject = new FE4Class(classData, address);
			FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(i);
			classMap.put(fe4CharClass, classObject);
		}
	}
	
	public void commit() {
		for (FE4Class classObject : classMap.values()) {
			classObject.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FE4Class classObject : classMap.values()) {
			classObject.commitChanges();
			if (classObject.hasCommittedChanges()) {
				Diff classDiff = new Diff(classObject.getAddressOffset(), classObject.getData().length, classObject.getData(), null);
				compiler.addDiff(classDiff);
			}
		}
	}
	
	public void recordClasses(RecordKeeper rk, Boolean isInitial) {
		for (FE4Data.CharacterClass fe4CharClass : classMap.keySet()) {
			FE4Class classObject = classMap.get(fe4CharClass);
			recordClass(rk, isInitial, fe4CharClass, classObject);
		}
	}
	
	private void recordData(RecordKeeper rk, boolean isInitial, String category, String entryKey, String key, String value) {
		if (isInitial) {
			rk.recordOriginalEntry(category, entryKey, key, value);
		} else {
			rk.recordUpdatedEntry(category, entryKey, key, value);
		}
	}
	
	private void recordClass(RecordKeeper rk, boolean isInitial, FE4Data.CharacterClass charClass, FE4Class classObject) {
		String name = charClass.toString();
		
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Enemy HP Growth", String.format("%d%%", classObject.getHPGrowth()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Enemy STR Growth", String.format("%d%%", classObject.getSTRGrowth()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Enemy MAG Growth", String.format("%d%%", classObject.getMAGGrowth()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Enemy SKL Growth", String.format("%d%%", classObject.getSKLGrowth()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Enemy SPD Growth", String.format("%d%%", classObject.getSPDGrowth()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Enemy DEF Growth", String.format("%d%%", classObject.getDEFGrowth()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Enemy RES Growth", String.format("%d%%", classObject.getRESGrowth()));
		
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Base HP", Integer.toString(classObject.getBaseHP()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Base STR", Integer.toString(classObject.getBaseSTR()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Base MAG", Integer.toString(classObject.getBaseMAG()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Base SKL", Integer.toString(classObject.getBaseSKL()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Base SPD", Integer.toString(classObject.getBaseSPD()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Base DEF", Integer.toString(classObject.getBaseDEF()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Base RES", Integer.toString(classObject.getBaseRES()));
		
		List<FE4Class.ClassSkills> slot1 = classObject.getSlot1ClassSkills();
		if (slot1.isEmpty()) {
			recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Class Skills 1", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Class.ClassSkills skill : slot1) {
				sb.append(skill.toString() + "<br>");
			}
			recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Class Skills 1", sb.toString());
		}
		
		List<FE4Class.ClassSkills> slot2 = classObject.getSlot2ClassSkills();
		if (slot2.isEmpty()) {
			recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Class Skills 2", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Class.ClassSkills skill : slot2) {
				sb.append(skill.toString() + "<br>");
			}
			recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Class Skills 2", sb.toString());
		}
		
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Movement Range", Integer.toString(classObject.getMovement()));
		recordData(rk, isInitial, RecordKeeperCategoryKey, name, "Gold", Integer.toString(classObject.getStartingGold()));
	}
}
