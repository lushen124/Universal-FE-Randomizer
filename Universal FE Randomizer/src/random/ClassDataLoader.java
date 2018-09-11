package random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedata.FEBase;
import fedata.FECharacter;
import fedata.FEClass;
import fedata.fe6.FE6Class;
import fedata.fe6.FE6Data;
import fedata.fe6.FE6Data.Item.FE6WeaponRank;
import fedata.fe7.FE7Class;
import fedata.fe7.FE7Data;
import fedata.fe8.FE8Class;
import fedata.fe8.FE8Data;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;
import io.FileHandler;
import random.exc.NotReached;
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
			case FE8:
				baseAddress = FileReadHelper.readAddress(handler, FE8Data.ClassTablePointer);
				for (FE8Data.CharacterClass charClass : FE8Data.CharacterClass.values()) {
					long offset = baseAddress + (charClass.ID * FE8Data.BytesPerClass);
					byte[] classData = handler.readBytesAtOffset(offset, FE8Data.BytesPerClass);
					FE8Class classObject = new FE8Class(classData, offset);
					classMap.put(charClass.ID, classObject);
					
					if (FE8Data.CharacterClass.allLordClasses.contains(charClass)) {
						lordMap.put(charClass.ID, classObject);
					}
					if (FE8Data.CharacterClass.allThiefClasses.contains(charClass)) {
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
		case FE8:
			classes = new ArrayList<FEClass>();
			for (FE8Data.CharacterClass charClass : FE8Data.CharacterClass.allValidClasses) {
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
			case FE8:
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
		case FE8:
			return FE8Data.CharacterClass.allFemaleClasses.contains(FE8Data.CharacterClass.valueOf(classID));
		default:
			return false;
		}
	}
	
	public FEClass[] potentialClasses(FEClass sourceClass, Boolean excludeLords, Boolean excludeThieves, Boolean excludeSource, Boolean requireAttack, Boolean requireRange, Boolean requiresMelee, Boolean applyRestrictions, FEClass mustLoseToClass) {
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
		case FE8: {
			NotReached.trigger("FE8 should use the variant of this method that includes the separateMonsters parameter.");
		}
		default:
			return new FEClass[] {};
		}
	}
	
	public FEClass[] potentialClasses(FEClass sourceClass, Boolean excludeLords, Boolean excludeThieves, Boolean separateMonsters, Boolean excludeSource, Boolean requireAttack, Boolean requireRange, Boolean requireMelee, Boolean applyRestrictions, FEClass mustLoseToClass) {
	switch (gameType) {
		case FE8:
			FE8Data.CharacterClass sourceCharClass = FE8Data.CharacterClass.valueOf(sourceClass.getID());
			FE8Data.CharacterClass[] targetClasses = null;
			if (mustLoseToClass != null) {
				targetClasses = FE8Data.CharacterClass.classesThatLoseToClass(sourceCharClass, FE8Data.CharacterClass.valueOf(mustLoseToClass.getID()), excludeLords, excludeThieves);
			} 
			
			if (targetClasses == null || targetClasses.length == 0) {
				targetClasses = FE8Data.CharacterClass.targetClassesForRandomization(sourceCharClass, excludeSource, excludeLords, excludeThieves, separateMonsters, requireAttack, requireRange, requireMelee, applyRestrictions);
			}
			FEClass[] result = new FEClass[targetClasses.length];
			for (int i = 0; i < targetClasses.length; i++) {
				result[i] = classMap.get(targetClasses[i].ID);
			}
			
			return result;
		default:
			NotReached.trigger("This method is only intended for FE8.");
			return new FEClass[] {};
		}
	}
	
	public Boolean isPromotedClass(int classID) {
		switch (gameType) {
		case FE6:
			return FE6Data.CharacterClass.allPromotedClasses.contains(FE6Data.CharacterClass.valueOf(classID));
		case FE7:
			return FE7Data.CharacterClass.allPromotedClasses.contains(FE7Data.CharacterClass.valueOf(classID));
		case FE8:
			return FE8Data.CharacterClass.allPromotedClasses.contains(FE8Data.CharacterClass.valueOf(classID));
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
		case FE8:
			return FE8Data.CharacterClass.allValidClasses.contains(FE8Data.CharacterClass.valueOf(classID));
		default:
			return false;
		}
	}
	
	public Boolean canClassAttack(int classID) {
		switch (gameType) {
		case FE6:
			return !FE6Data.CharacterClass.allPacifistClasses.contains(FE6Data.CharacterClass.valueOf(classID));
		case FE7:
			return !FE7Data.CharacterClass.allPacifistClasses.contains(FE7Data.CharacterClass.valueOf(classID));
		case FE8:
			return !FE8Data.CharacterClass.allPacifistClasses.contains(FE8Data.CharacterClass.valueOf(classID));
		default:
			return false;
		}
	}
	
	public Boolean canClassUseItem(int itemID, FEClass characterClass) {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> fe6Items = new HashSet<FE6Data.Item>();
			if (characterClass.getSwordRank() > 0) { fe6Items.addAll(Arrays.asList(FE6Data.Item.weaponsOfTypeAndRank(WeaponType.SWORD, WeaponRank.E, FE6Data.Item.FE6WeaponRank.valueOf(characterClass.getSwordRank()).toGeneralRank(), false))); }
			if (characterClass.getLanceRank() > 0) { fe6Items.addAll(Arrays.asList(FE6Data.Item.weaponsOfTypeAndRank(WeaponType.LANCE, WeaponRank.E, FE6Data.Item.FE6WeaponRank.valueOf(characterClass.getLanceRank()).toGeneralRank(), false))); }
			if (characterClass.getAxeRank() > 0) { fe6Items.addAll(Arrays.asList(FE6Data.Item.weaponsOfTypeAndRank(WeaponType.AXE, WeaponRank.E, FE6Data.Item.FE6WeaponRank.valueOf(characterClass.getAxeRank()).toGeneralRank(), false))); }
			if (characterClass.getBowRank() > 0) { fe6Items.addAll(Arrays.asList(FE6Data.Item.weaponsOfTypeAndRank(WeaponType.BOW, WeaponRank.E, FE6Data.Item.FE6WeaponRank.valueOf(characterClass.getBowRank()).toGeneralRank(), false))); }
			if (characterClass.getAnimaRank() > 0) { fe6Items.addAll(Arrays.asList(FE6Data.Item.weaponsOfTypeAndRank(WeaponType.ANIMA, WeaponRank.E, FE6Data.Item.FE6WeaponRank.valueOf(characterClass.getAnimaRank()).toGeneralRank(), false))); }
			if (characterClass.getLightRank() > 0) { fe6Items.addAll(Arrays.asList(FE6Data.Item.weaponsOfTypeAndRank(WeaponType.LIGHT, WeaponRank.E, FE6Data.Item.FE6WeaponRank.valueOf(characterClass.getLightRank()).toGeneralRank(), false))); }
			if (characterClass.getDarkRank() > 0) { fe6Items.addAll(Arrays.asList(FE6Data.Item.weaponsOfTypeAndRank(WeaponType.DARK, WeaponRank.E, FE6Data.Item.FE6WeaponRank.valueOf(characterClass.getDarkRank()).toGeneralRank(), false))); }
			if (characterClass.getStaffRank() > 0) { fe6Items.addAll(Arrays.asList(FE6Data.Item.weaponsOfTypeAndRank(WeaponType.STAFF, WeaponRank.E, FE6Data.Item.FE6WeaponRank.valueOf(characterClass.getStaffRank()).toGeneralRank(), false))); }
			return fe6Items.contains(FE6Data.Item.valueOf(itemID));
		}
		case FE7: {
			Set<FE7Data.Item> fe7Items = new HashSet<FE7Data.Item>();
			if (characterClass.getSwordRank() > 0) { fe7Items.addAll(Arrays.asList(FE7Data.Item.weaponsOfTypeAndRank(WeaponType.SWORD, WeaponRank.E, FE7Data.Item.FE7WeaponRank.valueOf(characterClass.getSwordRank()).toGeneralRank(), false))); }
			if (characterClass.getLanceRank() > 0) { fe7Items.addAll(Arrays.asList(FE7Data.Item.weaponsOfTypeAndRank(WeaponType.LANCE, WeaponRank.E, FE7Data.Item.FE7WeaponRank.valueOf(characterClass.getLanceRank()).toGeneralRank(), false))); }
			if (characterClass.getAxeRank() > 0) { fe7Items.addAll(Arrays.asList(FE7Data.Item.weaponsOfTypeAndRank(WeaponType.AXE, WeaponRank.E, FE7Data.Item.FE7WeaponRank.valueOf(characterClass.getAxeRank()).toGeneralRank(), false))); }
			if (characterClass.getBowRank() > 0) { fe7Items.addAll(Arrays.asList(FE7Data.Item.weaponsOfTypeAndRank(WeaponType.BOW, WeaponRank.E, FE7Data.Item.FE7WeaponRank.valueOf(characterClass.getBowRank()).toGeneralRank(), false))); }
			if (characterClass.getAnimaRank() > 0) { fe7Items.addAll(Arrays.asList(FE7Data.Item.weaponsOfTypeAndRank(WeaponType.ANIMA, WeaponRank.E, FE7Data.Item.FE7WeaponRank.valueOf(characterClass.getAnimaRank()).toGeneralRank(), false))); }
			if (characterClass.getLightRank() > 0) { fe7Items.addAll(Arrays.asList(FE7Data.Item.weaponsOfTypeAndRank(WeaponType.LIGHT, WeaponRank.E, FE7Data.Item.FE7WeaponRank.valueOf(characterClass.getLightRank()).toGeneralRank(), false))); }
			if (characterClass.getDarkRank() > 0) { fe7Items.addAll(Arrays.asList(FE7Data.Item.weaponsOfTypeAndRank(WeaponType.DARK, WeaponRank.E, FE7Data.Item.FE7WeaponRank.valueOf(characterClass.getDarkRank()).toGeneralRank(), false))); }
			if (characterClass.getStaffRank() > 0) { fe7Items.addAll(Arrays.asList(FE7Data.Item.weaponsOfTypeAndRank(WeaponType.STAFF, WeaponRank.E, FE7Data.Item.FE7WeaponRank.valueOf(characterClass.getStaffRank()).toGeneralRank(), false))); }
			return fe7Items.contains(FE7Data.Item.valueOf(itemID));
		}
		case FE8: {
			Set<FE8Data.Item> fe8Items = new HashSet<FE8Data.Item>();
			if (characterClass.getSwordRank() > 0) { fe8Items.addAll(Arrays.asList(FE8Data.Item.weaponsOfTypeAndRank(WeaponType.SWORD, WeaponRank.E, FE8Data.Item.FE8WeaponRank.valueOf(characterClass.getSwordRank()).toGeneralRank(), false, false))); }
			if (characterClass.getLanceRank() > 0) { fe8Items.addAll(Arrays.asList(FE8Data.Item.weaponsOfTypeAndRank(WeaponType.LANCE, WeaponRank.E, FE8Data.Item.FE8WeaponRank.valueOf(characterClass.getLanceRank()).toGeneralRank(), false, false))); }
			if (characterClass.getAxeRank() > 0) { fe8Items.addAll(Arrays.asList(FE8Data.Item.weaponsOfTypeAndRank(WeaponType.AXE, WeaponRank.E, FE8Data.Item.FE8WeaponRank.valueOf(characterClass.getAxeRank()).toGeneralRank(), false, false))); }
			if (characterClass.getBowRank() > 0) { fe8Items.addAll(Arrays.asList(FE8Data.Item.weaponsOfTypeAndRank(WeaponType.BOW, WeaponRank.E, FE8Data.Item.FE8WeaponRank.valueOf(characterClass.getBowRank()).toGeneralRank(), false, false))); }
			if (characterClass.getAnimaRank() > 0) { fe8Items.addAll(Arrays.asList(FE8Data.Item.weaponsOfTypeAndRank(WeaponType.ANIMA, WeaponRank.E, FE8Data.Item.FE8WeaponRank.valueOf(characterClass.getAnimaRank()).toGeneralRank(), false, false))); }
			if (characterClass.getLightRank() > 0) { fe8Items.addAll(Arrays.asList(FE8Data.Item.weaponsOfTypeAndRank(WeaponType.LIGHT, WeaponRank.E, FE8Data.Item.FE8WeaponRank.valueOf(characterClass.getLightRank()).toGeneralRank(), false, false))); }
			if (characterClass.getDarkRank() > 0) { fe8Items.addAll(Arrays.asList(FE8Data.Item.weaponsOfTypeAndRank(WeaponType.DARK, WeaponRank.E, FE8Data.Item.FE8WeaponRank.valueOf(characterClass.getDarkRank()).toGeneralRank(), false, false))); }
			if (characterClass.getStaffRank() > 0) { fe8Items.addAll(Arrays.asList(FE8Data.Item.weaponsOfTypeAndRank(WeaponType.STAFF, WeaponRank.E, FE8Data.Item.FE8WeaponRank.valueOf(characterClass.getStaffRank()).toGeneralRank(), false, false))); }
			
			if (fe8Items.contains(FE8Data.Item.valueOf(itemID))) {
				return true;
			} else {
				// It's likely a monster weapon.
				return FE8Data.Item.canMonsterClassUseItem(itemID, characterClass.getID());
			}
		}
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
