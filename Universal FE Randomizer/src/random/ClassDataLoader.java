package random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.FEBase;
import fedata.FECharacter;
import fedata.FEClass;
import fedata.fe6.FE6Class;
import fedata.fe6.FE6Data;
import fedata.fe7.FE7Class;
import fedata.fe7.FE7Data;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.recordkeeper.RecordKeeper;

public class ClassDataLoader {

private FEBase.GameType gameType;
	
	private Map<Integer, FEClass> classMap = new HashMap<Integer, FEClass>();
	
	private Map<Integer, FEClass> lordMap = new HashMap<Integer, FEClass>();
	private Map<Integer, FEClass> thiefMap = new HashMap<Integer, FEClass>();
	
	public static final String RecordKeeperCategoryKey = "Classes";

	public ClassDataLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE6:
				long baseAddress = FileReadHelper.readAddress(handler, FE6Data.ClassTablePointer);
				for (FE6Data.CharacterClass charClass : FE6Data.CharacterClass.values()) {
					long offset = baseAddress + (charClass.ID * FE6Data.BytesPerClass);
					byte[] classData = handler.readBytesAtOffset(offset, FE6Data.BytesPerClass);
					FE6Class classObject = new FE6Class(classData, offset);
					classMap.put(charClass.ID, classObject);
					
					if (charClass.ID == FE6Data.CharacterClass.SOLDIER.ID) {
						classObject.setTargetPromotionID(FE6Data.CharacterClass.GENERAL.ID);
					}
					
					if (FE6Data.CharacterClass.allLordClasses.contains(charClass)) {
						lordMap.put(charClass.ID, classObject);
					}
					if (FE6Data.CharacterClass.allThiefClasses.contains(charClass)) {
						thiefMap.put(charClass.ID, classObject);
					}
				}
				break;
			case FE7:
				baseAddress = FileReadHelper.readAddress(handler, FE7Data.ClassTablePointer);
				for (FE7Data.CharacterClass charClass : FE7Data.CharacterClass.values()) {
					long offset = baseAddress + (charClass.ID * FE7Data.BytesPerClass);
					byte[] classData = handler.readBytesAtOffset(offset, FE7Data.BytesPerClass);
					FE7Class classObject = new FE7Class(classData, offset);
					classMap.put(charClass.ID, classObject);
					
					if (charClass.ID == FE7Data.CharacterClass.SOLDIER.ID) {
						classObject.setTargetPromotionID(FE7Data.CharacterClass.GENERAL.ID);
					}
					
					if (FE7Data.CharacterClass.allLordClasses.contains(charClass)) {
						lordMap.put(charClass.ID, classObject);
					}
					if (FE7Data.CharacterClass.allThiefClasses.contains(charClass)) {
						thiefMap.put(charClass.ID, classObject);
					}
				}
				break;
			default:
				break;
		}
	}
	
	public FEClass[] allClasses() {
		switch (gameType) {
		case FE6:
			List<FEClass> classes = new ArrayList<FEClass>();
			for (FE6Data.CharacterClass charClass : FE6Data.CharacterClass.allValidClasses) {
				classes.add(classMap.get(charClass.ID));
			}
			return classes.toArray(new FEClass[classes.size()]);
		case FE7:
			classes = new ArrayList<FEClass>();
			for (FE7Data.CharacterClass charClass : FE7Data.CharacterClass.allValidClasses) {
				classes.add(classMap.get(charClass.ID));
			}
			return classes.toArray(new FEClass[classes.size()]);
		default:
			return new FEClass[] {};
		}
	}
	
	public FEClass classForID(int classID) {
		switch (gameType) {
			case FE6:
			case FE7:
				return classMap.get(classID);
			default:
				return null;
		}
	}
	
	public void commit() {
		for (FEClass charClass : classMap.values()) {
			charClass.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FEClass charClass : classMap.values()) {
			charClass.commitChanges();
			if (charClass.hasCommittedChanges()) {
				Diff charDiff = new Diff(charClass.getAddressOffset(), charClass.getData().length, charClass.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
	
	public Boolean isLordClass(int classID) {
		return lordMap.containsKey(classID);
	}
	
	public Boolean isThief(int classID) {
		return thiefMap.containsKey(classID);
	}
	
	public Boolean isFemale(int classID) {
		switch (gameType) {
		case FE6:
			return FE6Data.CharacterClass.allFemaleClasses.contains(FE6Data.CharacterClass.valueOf(classID));
		case FE7:
			return FE7Data.CharacterClass.allFemaleClasses.contains(FE7Data.CharacterClass.valueOf(classID));
		default:
			return false;
		}
	}
	
	public FEClass[] potentialClasses(FEClass sourceClass, Boolean excludeLords, Boolean excludeThieves, Boolean excludeSource, Boolean requireAttack, Boolean requireRange, Boolean applyRestrictions, FEClass mustLoseToClass) {
		switch (gameType) {
		case FE6: {
			FE6Data.CharacterClass sourceCharClass = FE6Data.CharacterClass.valueOf(sourceClass.getID());
			FE6Data.CharacterClass[] targetClasses = null;
			if (mustLoseToClass != null) {
				targetClasses = FE6Data.CharacterClass.classesThatLoseToClass(sourceCharClass, FE6Data.CharacterClass.valueOf(mustLoseToClass.getID()), excludeLords, excludeThieves);
			} 
			
			if (targetClasses == null || targetClasses.length == 0) {
				targetClasses = FE6Data.CharacterClass.targetClassesForRandomization(sourceCharClass, excludeSource, excludeLords, excludeThieves, requireAttack, requireRange, applyRestrictions);
			}
			FEClass[] result = new FEClass[targetClasses.length];
			for (int i = 0; i < targetClasses.length; i++) {
				result[i] = classMap.get(targetClasses[i].ID);
			}
			
			return result;
		}
		case FE7: {
			FE7Data.CharacterClass sourceCharClass = FE7Data.CharacterClass.valueOf(sourceClass.getID());
			FE7Data.CharacterClass[] targetClasses = null;
			if (mustLoseToClass != null) {
				targetClasses = FE7Data.CharacterClass.classesThatLoseToClass(sourceCharClass, FE7Data.CharacterClass.valueOf(mustLoseToClass.getID()), excludeLords, excludeThieves);
			} 
			
			if (targetClasses == null || targetClasses.length == 0) {
				targetClasses = FE7Data.CharacterClass.targetClassesForRandomization(sourceCharClass, excludeSource, excludeLords, excludeThieves, requireAttack, requireRange, applyRestrictions);
			}
			FEClass[] result = new FEClass[targetClasses.length];
			for (int i = 0; i < targetClasses.length; i++) {
				result[i] = classMap.get(targetClasses[i].ID);
			}
			
			return result;
		}
		default:
			return new FEClass[] {};
		}
	}
	
	public Boolean isPromotedClass(int classID) {
		switch (gameType) {
		case FE6:
			return FE6Data.CharacterClass.allPromotedClasses.contains(FE6Data.CharacterClass.valueOf(classID));
		case FE7:
			return FE7Data.CharacterClass.allPromotedClasses.contains(FE7Data.CharacterClass.valueOf(classID));
		default:
			return false;
		}
	}
	
	public Boolean isValidClass(int classID) {
		switch (gameType) {
		case FE6:
			return FE6Data.CharacterClass.allValidClasses.contains(FE6Data.CharacterClass.valueOf(classID));
		case FE7:
			return FE7Data.CharacterClass.allValidClasses.contains(FE7Data.CharacterClass.valueOf(classID));
		default:
			return false;
		}
	}
	
	public void recordClasses(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, TextLoader textData) {
		for (FEClass charClass : allClasses()) {
			if (!isValidClass(charClass.getID())) { continue; }
			recordClass(rk, charClass, isInitial, textData);
		}
	}
	
	private void recordClass(RecordKeeper rk, FEClass charClass, Boolean isInitial, TextLoader textData) {
		int nameIndex = charClass.getNameIndex();
		String name = textData.getStringAtIndex(nameIndex).trim();
		
		Boolean isFemale = isFemale(charClass.getID());
		if (isFemale) { name = name + " (F)"; }
		
		if (isInitial) {
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "HP Growth", String.format("%d%%", charClass.getHPGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "STR/MAG Growth", String.format("%d%%", charClass.getSTRGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "SKL Growth", String.format("%d%%", charClass.getSKLGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "SPD Growth", String.format("%d%%", charClass.getSPDGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "LCK Growth", String.format("%d%%", charClass.getLCKGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "DEF Growth", String.format("%d%%", charClass.getDEFGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "RES Growth", String.format("%d%%", charClass.getRESGrowth()));
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Movement Range", Integer.toString(charClass.getMOV()));
		} else {
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "HP Growth", String.format("%d%%", charClass.getHPGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "STR/MAG Growth", String.format("%d%%", charClass.getSTRGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "SKL Growth", String.format("%d%%", charClass.getSKLGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "SPD Growth", String.format("%d%%", charClass.getSPDGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "LCK Growth", String.format("%d%%", charClass.getLCKGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "DEF Growth", String.format("%d%%", charClass.getDEFGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "RES Growth", String.format("%d%%", charClass.getRESGrowth()));
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Movement Range", Integer.toString(charClass.getMOV()));
		}
	}
}
