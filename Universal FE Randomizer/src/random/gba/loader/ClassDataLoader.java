package random.gba.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gba.GBAFEClassData;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEClassProvider;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.recordkeeper.RecordKeeper;

public class ClassDataLoader {
	
	private GBAFEClassProvider provider;
	
	private Map<Integer, GBAFEClassData> classMap = new HashMap<Integer, GBAFEClassData>();
	
	public static final String RecordKeeperCategoryKey = "Classes";
	
	public ClassDataLoader(GBAFEClassProvider provider, FileHandler handler) {
		super();
		this.provider = provider;
		
		long baseAddress = FileReadHelper.readAddress(handler, provider.classDataTablePointer());
		List<GBAFEClass> classList = new ArrayList<GBAFEClass>(Arrays.asList(provider.allClasses()));
		List<GBAFEClass> unpromotedList = classList.stream().filter(charClass -> {
			return charClass.isPromoted() == false;
		}).collect(Collectors.toList());
		List<GBAFEClass> remainderList = classList.stream().filter(charClass -> {
			return unpromotedList.contains(charClass) == false;
		}).collect(Collectors.toList());
		
		// Key: the promoted class, Value: The class object for the base class.
		Map<GBAFEClass, GBAFEClassData> promotionMap = new HashMap<GBAFEClass, GBAFEClassData>();
		
		// This is done in two passes to satisfy FE6's lack of distinct promotion bonus (which is simply the delta between the two class bases.)
		for (GBAFEClass charClass : unpromotedList) {
			long offset = baseAddress + (charClass.getID() * provider.bytesPerClass());
			byte[] classData = handler.readBytesAtOffset(offset, provider.bytesPerClass());
			GBAFEClassData classObject = provider.classDataWithData(classData, offset, null);
			classMap.put(charClass.getID(), classObject);
			promotionMap.put(provider.classWithID(classObject.getTargetPromotionID()), classObject);
		}
		
		for (GBAFEClass charClass : remainderList) {
			long offset = baseAddress + (charClass.getID() * provider.bytesPerClass());
			byte[] classData = handler.readBytesAtOffset(offset, provider.bytesPerClass());
			GBAFEClassData demoted = promotionMap.get(charClass);
			GBAFEClassData classObject = provider.classDataWithData(classData, offset, demoted);
			classMap.put(charClass.getID(), classObject); 
		}
		
		provider.prepareForClassRandomization(classMap);
	}
	
	public GBAFEClassData[] allClasses() {
		return feClassesFromSet(provider.allValidClasses());
	}
	
	public GBAFEClassData classForID(int classID) {
		return classMap.get(classID);
	}
	
	public String debugStringForClass(int classID) {
		return provider.classWithID(classID).toString();
	}
	
	public void commit() {
		for (GBAFEClassData charClass : classMap.values()) {
			charClass.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (GBAFEClassData charClass : classMap.values()) {
			charClass.commitChanges();
			if (charClass.hasCommittedChanges()) {
				Diff charDiff = new Diff(charClass.getAddressOffset(), charClass.getData().length, charClass.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
	
	public Boolean isLordClass(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? charClass.isLord() : false;
	}
	
	public Boolean isThief(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? charClass.isThief() : false;
	}
	
	public Boolean isFemale(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? charClass.isFemale() : false;
	}
	
	public GBAFEClassData[] potentialClasses(GBAFEClassData sourceClass, Boolean excludeLords, Boolean excludeThieves, Boolean excludeSource, Boolean requireAttack, Boolean requireRange, Boolean requireMelee, Boolean applyRestrictions, GBAFEClassData mustLoseToClass) {
		return potentialClasses(sourceClass, excludeLords, excludeThieves, false, excludeSource, requireAttack, requireRange, requireMelee, applyRestrictions, mustLoseToClass);
	}
	
	public GBAFEClassData[] potentialClasses(GBAFEClassData sourceClass, Boolean excludeLords, Boolean excludeThieves, Boolean separateMonsters, Boolean excludeSource, Boolean requireAttack, Boolean requireRange, Boolean requireMelee, Boolean applyRestrictions, GBAFEClassData mustLoseToClass) {
		GBAFEClass sourceCharClass = provider.classWithID(sourceClass.getID());
		Set<GBAFEClass> targetClasses = null;
		
		Map<String, Boolean> options = new HashMap<String, Boolean>();
		options.put(GBAFEClassProvider.optionKeyExcludeLords, excludeLords);
		options.put(GBAFEClassProvider.optionKeyExcludeThieves, excludeThieves);
		options.put(GBAFEClassProvider.optionKeySeparateMonsters, separateMonsters);
		options.put(GBAFEClassProvider.optionKeyExcludeSource, excludeSource);
		options.put(GBAFEClassProvider.optionKeyRequireAttack, requireAttack);
		options.put(GBAFEClassProvider.optionKeyRequireRange, requireRange);
		options.put(GBAFEClassProvider.optionKeyRequireMelee, requireMelee);
		options.put(GBAFEClassProvider.optionKeyApplyRestrictions, applyRestrictions);
		
		if (mustLoseToClass != null) {
			targetClasses = provider.classesThatLoseToClass(provider.classWithID(sourceClass.getID()), provider.classWithID(mustLoseToClass.getID()), options);
		}
		
		if (targetClasses == null || targetClasses.size() == 0) {
			targetClasses = provider.targetClassesForRandomization(sourceCharClass, options);
		}
		
		return feClassesFromSet(targetClasses);
	}
	
	public Boolean isPromotedClass(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? charClass.isPromoted() : false;
	}
	
	public Boolean canClassDemote(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? provider.canClassDemote(charClass) : false;
	}
	
	public Boolean canClassPromote(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? provider.canClassPromote(charClass) : false;
	}
	
	public List<GBAFEClassData> demotionOptions(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		if (charClass == null) { return new ArrayList<GBAFEClassData>(); }
		GBAFEClass[] options = provider.demotedClass(charClass);
		List<GBAFEClassData> result = new ArrayList<GBAFEClassData>();
		for (GBAFEClass option : options) {
			result.add(classMap.get(option.getID()));
		}
		return result;
	}
	
	public List<GBAFEClassData> promotionOptions(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		if (charClass == null) { return new ArrayList<GBAFEClassData>(); }
		GBAFEClass[] options = provider.promotedClass(charClass);
		List<GBAFEClassData> result = new ArrayList<GBAFEClassData>();
		for (GBAFEClass option : options) {
			result.add(classMap.get(option.getID()));
		}
		result.sort(new Comparator<GBAFEClassData>() {
			@Override
			public int compare(GBAFEClassData arg0, GBAFEClassData arg1) {
				return Integer.compare(arg0.getID(), arg1.getID());
			}
		});
		return result;
	}
	
	public Boolean isFlying(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? provider.isFlier(charClass) : false;
	}
	
	public Boolean canSupportMelee(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return provider.meleeSupportedClasses().contains(charClass);
	}
	
	public Boolean canSupportRange(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return provider.rangeSupportedClasses().contains(charClass);
	}
	
	public Boolean isValidClass(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return provider.allValidClasses().contains(charClass);
	}
	
	public Boolean canClassAttack(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? charClass.canAttack() : false;
	}
	
	private GBAFEClassData[] feClassesFromSet(Set<GBAFEClass> classes) {
		List<GBAFEClass> charClasses = new ArrayList<GBAFEClass>(classes);
		Collections.sort(charClasses, new Comparator<GBAFEClass>() {
			public int compare(GBAFEClass arg0, GBAFEClass arg1) { return Integer.compare(arg0.getID(), arg1.getID()); }
		});
		
		GBAFEClassData[] classList = new GBAFEClassData[charClasses.size()];
		for (int i = 0; i < charClasses.size(); i++) {
			classList[i] = classForID(charClasses.get(i).getID());
		}
		
		return classList;
	}
	
	public void recordClasses(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, TextLoader textData) {
		for (GBAFEClassData charClass : allClasses()) {
			if (!isValidClass(charClass.getID())) { continue; }
			recordClass(rk, charClass, isInitial, textData);
		}
	}
	
	private void recordClass(RecordKeeper rk, GBAFEClassData charClass, Boolean isInitial, TextLoader textData) {
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
