package random.gba.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.fe8.FE8Data;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEClassProvider;
import fedata.gba.general.WeaponType;
import io.FileHandler;
import ui.model.ClassOptions;
import ui.model.ClassOptions.GenderRestrictionOption;
import util.AddressRange;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FindAndReplace;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.RecordKeeper;

public class ClassDataLoader {
	
	private GBAFEClassProvider provider;
	
	private Map<Integer, GBAFEClassData> classMap = new HashMap<Integer, GBAFEClassData>();
	private Map<Integer, GBAFEClassData> addedClasses = new HashMap<Integer, GBAFEClassData>();
	
	private List<GBAFEClassData> classList = new ArrayList<GBAFEClassData>();
	
	private int lastClassID = 0;
	
	public static final String RecordKeeperCategoryKey = "Classes";
	
	public static ClassDataLoader createReadOnlyClassDataLoader(GBAFEClassProvider provider, FileHandler handler) {
		return new ClassDataLoader(provider, handler, true);
	}
	
	private ClassDataLoader(GBAFEClassProvider provider, FileHandler handler, boolean parseTable) {
		super();
		this.provider = provider;
		
		long baseAddress = FileReadHelper.readAddress(handler, provider.classDataTablePointer());
		long currentAddress = baseAddress;
		int classID = -1;
		List<Integer> processLaterIDs = new ArrayList<Integer>();
		do {
			byte[] classData = handler.readBytesAtOffset(currentAddress, provider.bytesPerClass());
			GBAFEClassData charClass = provider.classDataWithData(classData, currentAddress, null);
			classID = charClass.getID();
			if (classID == 0x0 || classID == 0xFF) {
				currentAddress += provider.bytesPerClass();
				continue;
			}
			if (charClass.isPromotedClass()) {
				GBAFEClassData demotedClass = null;
				try {
					demotedClass = classList.stream().filter(curr -> curr.getTargetPromotionID() == charClass.getID()).findFirst().get();
				} catch (NoSuchElementException e) {}
				if (demotedClass == null) {
					// We need the demoted class first. process this later.
					processLaterIDs.add(charClass.getID());
					currentAddress += provider.bytesPerClass();
					continue;
				}
				
				GBAFEClassData finalClass = provider.classDataWithData(classData, currentAddress, demotedClass);
				classMap.put(finalClass.getID(), finalClass);
				classList.add(finalClass);
			} else {
				classMap.put(charClass.getID(), charClass);
				classList.add(charClass);
			}
			
			currentAddress += provider.bytesPerClass();
		} while ((classID != 0 && classID != 0xFF) || classList.isEmpty());
		
		for (int deferredID : processLaterIDs) {
			long address = baseAddress + (deferredID * provider.bytesPerClass());
			byte[] classData = handler.readBytesAtOffset(address, provider.bytesPerClass());
			GBAFEClassData demotedClass = null;
			try {
				demotedClass = classList.stream().filter(curr -> curr.getTargetPromotionID() == deferredID).findFirst().get();
			} catch (NoSuchElementException e) {
				System.err.println("Deferred Class ID still cannot be processed! Promotion Info may not be available for class with ID 0x" + Integer.toHexString(deferredID).toUpperCase());
			}
			
			GBAFEClassData finalClass = provider.classDataWithData(classData, address, demotedClass);
			classMap.put(finalClass.getID(), finalClass);
			classList.add(finalClass);
		}
		
		classList.sort(GBAFEClassData.defaultComparator);
	}
	
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
		
		lastClassID = provider.numberOfClasses();
	}
	
	public GBAFEClassData createLordClassBasedOnClass(GBAFEClassData referenceClass) {
		if (referenceClass == null) { return null; }
		GBAFEClassData clonedClass = referenceClass.createClone();
		clonedClass.setID(lastClassID++);
		addedClasses.put(clonedClass.getID(), clonedClass);
		return clonedClass;
	}
	
	public GBAFEClassData createLordClassBasedOnClass(GBAFEClassData referenceClass, int idToReplace) {
		if (referenceClass == null) { return null; }
		if (idToReplace >= provider.numberOfClasses()) { return null; }
		GBAFEClassData clonedClass = referenceClass.createClone();
		clonedClass.setID(idToReplace);
		
		GBAFEClassData classToReplace = classMap.get(idToReplace);
		if (classToReplace == null) {
			// Find one before it and calculate the correct address.
			int offset = 0;
			GBAFEClassData priorClass = null;
			do {
				offset++;
				priorClass = classMap.get(idToReplace - offset);
			} while (priorClass == null && idToReplace - offset > 0);
			long targetAddress = priorClass.getAddressOffset() + (provider.bytesPerClass() * offset);
			clonedClass.overrideAddress(targetAddress);
			classMap.put(idToReplace, clonedClass);
		} else {
			clonedClass.overrideAddress(classToReplace.getAddressOffset());
			classMap.put(idToReplace, clonedClass);
		}
		
		return clonedClass;
	}
	
	public GBAFEClassData[] allClasses() {
		return feClassesFromSet(provider.allValidClasses());
	}
	
	public GBAFEClassData classForID(int classID) {
		if (classMap.containsKey(classID)) {
			return classMap.get(classID);
		}
		else {
			return addedClasses.get(classID);
		}
	}
	
	public List<GBAFEClassData> getParsedClassList() {
		return new ArrayList<GBAFEClassData>(classList);
	}
	
	public String debugStringForClass(int classID) {
		if (provider.classWithID(classID) != null) {
			return provider.classWithID(classID).toString();
		} else {
			return "UNKNOWN (0x" + Integer.toHexString(classID) + ")";
		}
	}
	
	public void commit() {
		for (GBAFEClassData charClass : classMap.values()) {
			charClass.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler, FileHandler handler, FreeSpaceManager freeSpace) {
		if (addedClasses.isEmpty()) {
			for (GBAFEClassData charClass : classMap.values()) {
				charClass.commitChanges();
				if (charClass.hasCommittedChanges()) {
					Diff charDiff = new Diff(charClass.getAddressOffset(), charClass.getData().length, charClass.getData(), null);
					compiler.addDiff(charDiff);
				}
			}
		} else {
			// This needs a repoint.
			
			// Commit everything first.
			for (GBAFEClassData charClass : classMap.values()) {
				charClass.commitChanges();
			}
			for (GBAFEClassData charClass : addedClasses.values()) {
				charClass.commitChanges();
			}
			
			// Write the classes in order, including ones we didn't modify. Those will have to be copied from the handler, since we didn't have objects made for them.
			long startingOffset = FileReadHelper.readAddress(handler, provider.classDataTablePointer());
			long newStartingOffset = 0;
			for (int i = 0; i < provider.numberOfClasses(); i++) {
				// Thankfully, the index doubles as the ID.
				GBAFEClassData objectData = classMap.get(i);
				if (objectData != null) {
					// This is a class we could have modified. Read it from the object data.
					long writtenOffset = freeSpace.setValue(objectData.getData(), "Class Data for Class 0x" + Integer.toHexString(i), i == 0);
					if (i == 0) { newStartingOffset = writtenOffset; }
				} else {
					// This is a class we don't touch. Read it from the original file.
					long existingStart = startingOffset + i * provider.bytesPerClass();
					long existingEnd = existingStart + provider.bytesPerClass();
					long writtenOffset = freeSpace.setValue(FileReadHelper.readBytesInRange(new AddressRange(existingStart, existingEnd), handler), "Copied class data for Class 0x" + Integer.toHexString(i), i == 0);
					if (i == 0) { newStartingOffset = writtenOffset; }
				}
			}
			
			// Append any classes we added.
			List<GBAFEClassData> addedClassList = new ArrayList<GBAFEClassData>(addedClasses.values());
			addedClassList.sort(new Comparator<GBAFEClassData>() {
				@Override
				public int compare(GBAFEClassData o1, GBAFEClassData o2) {
					return Integer.compare(o1.getID(), o2.getID());
				}
			});
			for (GBAFEClassData charClass : addedClassList) {
				freeSpace.setValue(charClass.getData(), "Added Class Data for Class 0x" + Integer.toHexString(charClass.getID()));
			}
			
			// Update the pointers to this table.
			compiler.findAndReplace(new FindAndReplace(WhyDoesJavaNotHaveThese.gbaAddressFromOffset(startingOffset), WhyDoesJavaNotHaveThese.gbaAddressFromOffset(newStartingOffset), true));
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
	
	public Boolean canDestroyVillages(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? charClass.canDestroyVillages() : false;
	}
	
	public Boolean isFemale(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? charClass.isFemale() : false;
	}
	
	public GBAFEClassData correspondingMaleClass(GBAFEClassData originalClass) {
		GBAFEClass correspondingClass = provider.correspondingMaleClass(provider.classWithID(originalClass.getID()));
		return classForID(correspondingClass.getID());
	}
	
	public GBAFEClassData correspondingFemaleClass(GBAFEClassData originalClass) {
		GBAFEClass correspondingClass = provider.correspondingFemaleClass(provider.classWithID(originalClass.getID()));
		return classForID(correspondingClass.getID());
	}
	
	public GBAFEClassData[] potentialClasses(GBAFEClassData sourceClass, Boolean isForEnemy, Boolean excludeLords, Boolean excludeThieves, Boolean excludeSpecial, Boolean excludeSource, Boolean requireAttack, Boolean requireRange, Boolean requireMelee, Boolean applyRestrictions, ClassOptions.GenderRestrictionOption restrictGender, GBAFEClassData mustLoseToClass) {
		return potentialClasses(sourceClass, isForEnemy, excludeLords, excludeThieves, excludeSpecial, false, excludeSource, requireAttack, requireRange, requireMelee, applyRestrictions, restrictGender, mustLoseToClass);
	}
	
	public GBAFEClassData[] potentialClasses(GBAFEClassData sourceClass, Boolean isForEnemy, Boolean excludeLords, Boolean excludeThieves, Boolean excludeSpecial, Boolean separateMonsters, Boolean excludeSource, Boolean requireAttack, Boolean requireRange, Boolean requireMelee, Boolean applyRestrictions, ClassOptions.GenderRestrictionOption restrictGender, GBAFEClassData mustLoseToClass) {
		GBAFEClass sourceCharClass = provider.classWithID(sourceClass.getID());
		Set<GBAFEClass> targetClasses = null;
		
		Map<String, Boolean> options = new HashMap<String, Boolean>();
		options.put(GBAFEClassProvider.optionKeyExcludeLords, excludeLords);
		options.put(GBAFEClassProvider.optionKeyExcludeThieves, excludeThieves);
		options.put(GBAFEClassProvider.optionKeyExcludeSpecial, excludeSpecial);
		options.put(GBAFEClassProvider.optionKeySeparateMonsters, separateMonsters);
		options.put(GBAFEClassProvider.optionKeyExcludeSource, excludeSource);
		options.put(GBAFEClassProvider.optionKeyRequireAttack, requireAttack);
		options.put(GBAFEClassProvider.optionKeyRequireRange, requireRange);
		options.put(GBAFEClassProvider.optionKeyRequireMelee, requireMelee);
		options.put(GBAFEClassProvider.optionKeyApplyRestrictions, applyRestrictions);
		options.put(GBAFEClassProvider.optionKeyRestrictGender, restrictGender == GenderRestrictionOption.STRICT);
		
		if (mustLoseToClass != null) {
			targetClasses = provider.classesThatLoseToClass(provider.classWithID(sourceClass.getID()), provider.classWithID(mustLoseToClass.getID()), options);
		}
		
		if (targetClasses == null || targetClasses.size() == 0) {
			targetClasses = provider.targetClassesForRandomization(sourceCharClass, isForEnemy, options);
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
	
	public boolean isPlayerOnly(int classID) {
		GBAFEClass charClass = provider.classWithID(classID);
		return charClass != null ? provider.playerOnlyClasses().contains(charClass) : false;
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
	
	public List<String> ability1Flags() {
		return provider.charClassAbility1Flags();
	}
	
	public List<String> ability2Flags() {
		return provider.charClassAbility2Flags();
	}
	
	public List<String> ability3Flags() {
		return provider.charClassAbility3Flags();
	}
	
	public List<String> ability4Flags() {
		return provider.charClassAbility4Flags();
	}
	
	public boolean classHasFlagByDisplayString(String displayString, GBAFEClassData charClass) {
		return charClass.hasAbility(displayString);
	}
	
	public List<WeaponType> usableTypesForClass(GBAFEClassData charClass) {
		List<WeaponType> types = new ArrayList<WeaponType>();
		
		if (charClass.getSwordRank() > 0) { types.add(WeaponType.SWORD); }
		if (charClass.getLanceRank() > 0) { types.add(WeaponType.LANCE); }
		if (charClass.getAxeRank() > 0) { types.add(WeaponType.AXE); }
		if (charClass.getBowRank() > 0) { types.add(WeaponType.BOW); }
		if (charClass.getAnimaRank() > 0) { types.add(WeaponType.ANIMA); }
		if (charClass.getDarkRank() > 0) { types.add(WeaponType.DARK); }
		if (charClass.getLightRank() > 0) { types.add(WeaponType.LIGHT); }
		if (charClass.getStaffRank() > 0) { types.add(WeaponType.STAFF); }
		
		return types;
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
		String name = textData.getStringAtIndex(nameIndex, true).trim();
		
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
