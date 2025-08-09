package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Item;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNDataFileHandlerV2;
import io.gcn.GCNDataFileHandlerV2.GCNDataFileDataSection;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponType;
import util.DebugPrinter;
import util.Diff;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.fe9.ChangelogBuilder;
import util.recordkeeper.fe9.ChangelogHeader;
import util.recordkeeper.fe9.ChangelogSection;
import util.recordkeeper.fe9.ChangelogStyleRule;
import util.recordkeeper.fe9.ChangelogTOC;
import util.recordkeeper.fe9.ChangelogTable;
import util.recordkeeper.fe9.ChangelogHeader.HeaderLevel;

public class FE9ClassDataLoader {
	
	List<FE9Class> allClasses;
	
	List<FE9Class> allUnpromotedClasses;
	List<FE9Class> allPromotedClasses;
	List<FE9Class> allLaguzClasses;
	
	List<FE9Class> allFliers;
	List<FE9Class> allMaleClasses;
	List<FE9Class> allFemaleClasses;
	
	List<FE9Class> playerEligibleClasses;
	List<FE9Class> enemyEligibleClasses;
	
	List<FE9Class> allPacifistClasses;
	
	Map<String, FE9Class> idLookup;
	
	GCNDataFileHandlerV2 fe8databin;
	GCNDataFileDataSection classDataSection;
	FE9CommonTextLoader textLoader;
	
	public enum StatBias {
		NONE, PHYSICAL_ONLY, MAGICAL_ONLY, LEAN_PHYSICAL, LEAN_MAGICAL
	}
	
	public FE9ClassDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		textLoader = commonTextLoader;
		
		allClasses = new ArrayList<FE9Class>();
		
		allLaguzClasses = new ArrayList<FE9Class>();
		allUnpromotedClasses = new ArrayList<FE9Class>();
		allPromotedClasses = new ArrayList<FE9Class>();
		
		allFliers = new ArrayList<FE9Class>();
		allMaleClasses = new ArrayList<FE9Class>();
		allFemaleClasses = new ArrayList<FE9Class>();
		
		allPacifistClasses = new ArrayList<FE9Class>();
		
		playerEligibleClasses = new ArrayList<FE9Class>();
		enemyEligibleClasses = new ArrayList<FE9Class>();
		
		idLookup = new HashMap<String, FE9Class>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.ClassDataFilename);
		assert(handler instanceof GCNDataFileHandlerV2);
		if (handler instanceof GCNDataFileHandlerV2) {
			fe8databin = (GCNDataFileHandlerV2)handler;
		}
		
		classDataSection = fe8databin.getSectionWithName(FE9Data.ClassDataSectionName);
		int count = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(classDataSection.getRawData(0, 4), false);
		
		long offset = 4;
		for (int i = 0; i < count; i++) {
			long dataOffset = offset + i * FE9Data.ClassDataSize;
			FE9Class charClass = new FE9Class(classDataSection.getRawData(dataOffset, FE9Data.ClassDataSize), dataOffset);
			allClasses.add(charClass);
			
			debugPrintClass(charClass, handler, commonTextLoader);
			
			String jid = fe8databin.stringForPointer(charClass.getClassIDPointer());
			idLookup.put(jid, charClass);
			
			FE9Data.CharacterClass fe9CharClass = FE9Data.CharacterClass.withJID(jid);
			if (fe9CharClass == null) { continue; }
			
			if (fe9CharClass.isLaguz()) {
				allLaguzClasses.add(charClass);
			} else if (fe9CharClass.isPromotedClass()) {
				allPromotedClasses.add(charClass);
			} else {
				allUnpromotedClasses.add(charClass);
			}
			
			if (fe9CharClass.isFemale()) {
				allFemaleClasses.add(charClass);
			} else {
				allMaleClasses.add(charClass);
			}
			
			if (fe9CharClass.isFlier()) {
				allFliers.add(charClass);
			}
			
			if (fe9CharClass.isValidPlayerClass() && !fe9CharClass.isEnemyOnly()) {
				playerEligibleClasses.add(charClass);
			}
			if (!fe9CharClass.isPlayerOnly()) {
				enemyEligibleClasses.add(charClass);
			}
			
			if (fe9CharClass.isPacifist()) {
				allPacifistClasses.add(charClass);
			}
		}
	}
	
	public List<FE9Class> allClasses() {
		return allClasses;
	}
	
	public List<FE9Class> allValidClasses() {
		return allClasses.stream().filter(fe9Class -> {
			String jid = fe8databin.stringForPointer(fe9Class.getClassIDPointer());
			FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
			return (charClass != null && charClass.isValidClass());
		}).collect(Collectors.toList());
	}
	
	public boolean areClassesSimilar(FE9Class class1, FE9Class class2) {
		if (class1 == null || class2 == null) { return false; }
		FE9Data.CharacterClass charClass1 = fe9ClassForClass(class1);
		FE9Data.CharacterClass charClass2 = fe9ClassForClass(class2);
		return charClass1.similarClasses().contains(charClass2);
	}
	
	public List<FE9Class> allLaguzClasses() {
		return allLaguzClasses;
	}
	
	public List<FE9Class> allUnpromotedClasses() {
		return allUnpromotedClasses;
	}
	
	public List<FE9Class> allPromotedClasses() {
		return allPromotedClasses;
	}
	
	public List<FE9Class> allFemale() {
		return allFemaleClasses;
	}
	
	public List<FE9Class> allMale() {
		return allMaleClasses;
	}
	
	public List<FE9Class> allPacifistClasses() {
		return allPacifistClasses;
	}
	
	public List<FE9Class> allPlayerEligible(boolean includeLords, boolean includeThieves, boolean includeSpecial) {
		return playerEligibleClasses.stream().filter(fe9Class -> {
			String jid = getJIDForClass(fe9Class);
			if (!includeLords && FE9Data.CharacterClass.withJID(jid).isLordClass()) { return false; }
			if (!includeThieves && FE9Data.CharacterClass.withJID(jid).isThiefClass()) { return false; }
			if (!includeSpecial && FE9Data.CharacterClass.withJID(jid).isSpecialClass()) { return false; }
			return true;
		}).collect(Collectors.toList());
	}
	
	public List<FE9Class> allEnemyEligible() {
		return enemyEligibleClasses;
	}
	
	public List<FE9Class> allFliers() {
		return allFliers;
	}
	
	public FE9Class classWithID(String jid) {
		return idLookup.get(jid);
	}
	
	public String getJIDForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getClassIDPointer());
	}
	
	public String getMJIDForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getClassNamePointer());
	}
	
	public String getMHJForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getClassDescriptionPointer());
	}
	
	public FE9Class getPromotedClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return classWithID(fe8databin.stringForPointer(charClass.getPromotionIDPointer()));
	}
	
	public String getDefaultIIDForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getDefaultWeaponPointer());
	}
	
	public String getRaceForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getRacePointer());
	}
	
	public String getTraitForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getMiscPointer());
	}
	
	public int getLaguzSTROffset(FE9Class laguzClass) {
		if (laguzClass == null) { return 0; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(laguzClass)).getTransformSTRBonus();
	}
	
	public int getLaguzMAGOffset(FE9Class laguzClass) {
		if (laguzClass == null) { return 0; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(laguzClass)).getTransformMAGBonus();
	}
	
	public int getLaguzSKLOffset(FE9Class laguzClass) {
		if (laguzClass == null) { return 0; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(laguzClass)).getTransformSKLBonus();
	}
	
	public int getLaguzSPDOffset(FE9Class laguzClass) {
		if (laguzClass == null) { return 0; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(laguzClass)).getTransformSPDBonus();
	}
	
	public int getLaguzDEFOffset(FE9Class laguzClass) {
		if (laguzClass == null) { return 0; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(laguzClass)).getTransformDEFBonus();
	}
	
	public int getLaguzRESOffset(FE9Class laguzClass) {
		if (laguzClass == null) { return 0; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(laguzClass)).getTransformRESBonus();
	}
	
	public String getUnpromotedAIDForClass(FE9Class charClass) {
		if (isPromotedClass(charClass) && !isLaguzClass(charClass)) { return null; }
		FE9Data.CharacterClass fe9CharClass = FE9Data.CharacterClass.withJID(getJIDForClass(charClass));
		return fe9CharClass.getAidString();
	}
	
	public String getPromotedAIDForClass(FE9Class charClass) {
		FE9Data.CharacterClass fe9CharClass = FE9Data.CharacterClass.withJID(getJIDForClass(charClass));
		if (isLaguzClass(charClass)) {
			return fe9CharClass.getLaguzTransformedAidString();
		} else if (isPromotedClass(charClass)) {
			return fe9CharClass.getAidString();
		} else {
			FE9Data.CharacterClass promotedCharClass = fe9CharClass.getPromoted();
			if (promotedCharClass != null) {
				return promotedCharClass.getAidString();
			}
		}
		
		return null;
	}
	
	public String getSID1ForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getSkill1Pointer());
	}
	
	public void setSID1ForClass(FE9Class charClass, String sid) {
		if (charClass == null) { return; }
		if (sid == null) {
			charClass.setSkill1Pointer(0);
			return;
		}
		
		fe8databin.addString(sid);
		fe8databin.addPointerOffset(classDataSection, charClass.getAddressOffset() + FE9Class.ClassSkill1Offset);
		charClass.setSkill1Pointer(fe8databin.pointerForString(sid));
	}
	
	public String getSID2ForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getSkill2Pointer());
	}
	
	public void setSID2ForClass(FE9Class charClass, String sid) {
		if (charClass == null) { return; }
		if (sid == null) {
			charClass.setSkill2Pointer(0);
			return;
		}
		
		fe8databin.addString(sid);
		fe8databin.addPointerOffset(classDataSection, charClass.getAddressOffset() + FE9Class.ClassSkill2Offset);
		charClass.setSkill2Pointer(fe8databin.pointerForString(sid));
	}
	
	public String getSID3ForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getSkill3Pointer());
	}
	
	public void setSID3ForClass(FE9Class charClass, String sid) {
		if (charClass == null) { return; }
		if (sid == null) {
			charClass.setSkill3Pointer(0);
			return;
		}
		
		fe8databin.addString(sid);
		fe8databin.addPointerOffset(classDataSection, charClass.getAddressOffset() + FE9Class.ClassSkill3Offset);
		charClass.setSkill3Pointer(fe8databin.pointerForString(sid));
	}
	
	public String getSID4ForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getSkill4Pointer());
	}
	
	public void setSID4ForClass(FE9Class charClass, String sid) {
		if (charClass == null) { return; }
		if (sid == null) {
			charClass.setSkill4Pointer(0);
			return;
		}
		
		fe8databin.addString(sid);
		fe8databin.addPointerOffset(classDataSection, charClass.getAddressOffset() + FE9Class.ClassSkill4Offset);
		charClass.setSkill4Pointer(fe8databin.pointerForString(sid));
	}
	
	public String getSID5ForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getSkill5Pointer());
	}
	
	public void setSID5ForClass(FE9Class charClass, String sid) {
		if (charClass == null) { return; }
		if (sid == null) {
			charClass.setSkill5Pointer(0);
			return;
		}
		
		fe8databin.addString(sid);
		fe8databin.addPointerOffset(classDataSection, charClass.getAddressOffset() + FE9Class.ClassSkill5Offset);
		charClass.setSkill5Pointer(fe8databin.pointerForString(sid));
	}
	
	public String getWeaponLevelsForClass(FE9Class charClass) {
		if (charClass == null) { return null; }
		return fe8databin.stringForPointer(charClass.getWeaponLevelPointer());
	}
	
	public void setWeaponLevelsForClass(FE9Class charClass, String weaponLevelString) {
		if (charClass == null || weaponLevelString == null || weaponLevelString.length() != 9) { return; }
		// Validate string. We only allow -, *, S, A, B, C, D, E characters.
		for (int i = 0; i < weaponLevelString.length(); i++) {
			char c = weaponLevelString.charAt(i);
			if (c != '-' && c != '*' && c != 'S' && c != 'A' && c != 'B' && c != 'C' && c != 'D' && c != 'E') {
				return;
			}
		}
		
		fe8databin.addString(weaponLevelString);
		charClass.setWeaponLevelPointer(fe8databin.pointerForString(weaponLevelString));
	}
	
	public List<WeaponType> getUsableWeaponTypesForClass(FE9Class charClass) {
		List<WeaponType> types = new ArrayList<WeaponType>();
		String weaponLevels = getWeaponLevelsForClass(charClass);
		if (weaponLevels.charAt(0) != '-') { types.add(WeaponType.SWORD); }
		if (weaponLevels.charAt(1) != '-') { types.add(WeaponType.LANCE); }
		if (weaponLevels.charAt(2) != '-') { types.add(WeaponType.AXE); }
		if (weaponLevels.charAt(3) != '-') { types.add(WeaponType.BOW); }
		if (weaponLevels.charAt(4) != '-') { types.add(WeaponType.FIRE); }
		if (weaponLevels.charAt(5) != '-') { types.add(WeaponType.THUNDER); }
		if (weaponLevels.charAt(6) != '-') { types.add(WeaponType.WIND); }
		if (weaponLevels.charAt(7) != '-') {
			types.add(WeaponType.STAFF);
			if (canClassUseLightMagic(charClass)) {
				types.add(WeaponType.LIGHT);
			}
		}
		
		if (canClassUseKnives(charClass)) {
			types.add(WeaponType.KNIFE);
		}
		
		return types;
	}
	
	public boolean canClassUseKnives(FE9Class charClass) {
		if (charClass == null) { return false; }
		
		String sid1 = getSID1ForClass(charClass);
		String sid2 = getSID2ForClass(charClass);
		String sid3 = getSID3ForClass(charClass);
		
		return (sid1 != null && sid1.equals(FE9Data.Skill.EQUIP_KNIFE.getSID())) ||
				(sid2 != null && sid2.equals(FE9Data.Skill.EQUIP_KNIFE.getSID())) ||
				(sid3 != null && sid3.equals(FE9Data.Skill.EQUIP_KNIFE.getSID()));
	}
	
	public boolean canClassUseLightMagic(FE9Class charClass) {
		if (charClass == null) { return false; }
		
		String sid1 = getSID1ForClass(charClass);
		String sid2 = getSID2ForClass(charClass);
		String sid3 = getSID3ForClass(charClass);
		
		return (sid1 != null && sid1.equals(FE9Data.Skill.LUMINA.getSID())) ||
				(sid2 != null && sid2.equals(FE9Data.Skill.LUMINA.getSID())) ||
				(sid3 != null && sid3.equals(FE9Data.Skill.LUMINA.getSID()));
	}
	
	public StatBias statBiasForClass(FE9Class charClass) {
		FE9Data.CharacterClass fe9Class = fe9ClassForClass(charClass);
		if (fe9Class.isHybridMagicalClass()) { return StatBias.LEAN_MAGICAL; }
		else if (fe9Class.isHybridPhyiscalClass()) { return StatBias.LEAN_PHYSICAL; }
		else if (fe9Class.isPhysicalClass()) { return StatBias.PHYSICAL_ONLY; }
		else if (fe9Class.isMagicalClass()) { return StatBias.MAGICAL_ONLY; }
		return StatBias.NONE;
	}
	
	public boolean isLordClass(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isLordClass();
	}
	
	public boolean isThiefClass(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isThiefClass();
	}
	
	public boolean isSpecialClass(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isSpecialClass();
	}
	
	public boolean isPacifistClass(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isPacifist();
	}
	
	public boolean isPromotedClass(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isPromotedClass();
	}
	
	public boolean isFemale(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isFemale();
	}
	
	public boolean isLaguzClass(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isLaguz();
	}
	
	public boolean isFlierClass(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isFlier();
	}
	
	public boolean isAdvancedClass(FE9Class charClass) {
		if (charClass == null) { return false; }
		return FE9Data.CharacterClass.withJID(getJIDForClass(charClass)).isAdvanced();
	}
	
	public String getDisplayName(FE9Class charClass) {
		long pointer = charClass.getClassNamePointer();
		if (pointer == 0) { return "(null)"; }
		String identifier = fe8databin.stringForPointer(charClass.getClassNamePointer());
		if (textLoader == null) { return identifier; }
		
		String resolvedValue = textLoader.textStringForIdentifier(identifier);
		if (resolvedValue != null) {
			return resolvedValue;
		} else {
			return identifier;
		}
	}

	private void debugPrintClass(FE9Class charClass, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "===== Printing Class =====");
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"JID: " + stringForPointer(charClass.getClassIDPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"MJID: " + stringForPointer(charClass.getClassNamePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"MH_J: " + stringForPointer(charClass.getClassDescriptionPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"Promoted JID: " + stringForPointer(charClass.getPromotionIDPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"Default IID: " + stringForPointer(charClass.getDefaultWeaponPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"Weapon Levels: " + stringForPointer(charClass.getWeaponLevelPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"SID: " + stringForPointer(charClass.getSkill1Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"SID 2: " + stringForPointer(charClass.getSkill2Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"SID 3: " + stringForPointer(charClass.getSkill3Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"Race: " + stringForPointer(charClass.getRacePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, 
				"Unknown: " + stringForPointer(charClass.getMiscPointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Unknown 3: " + WhyDoesJavaNotHaveThese.displayStringForBytes(charClass.getUnknown3Bytes()));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Base HP: " + charClass.getBaseHP());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Base STR: " + charClass.getBaseSTR());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Base MAG: " + charClass.getBaseMAG());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Base SKL: " + charClass.getBaseSKL());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Base SPD: " + charClass.getBaseSPD());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Base LCK: " + charClass.getBaseLCK());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Base DEF: " + charClass.getBaseDEF());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Base RES: " + charClass.getBaseRES());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Max HP: " + charClass.getMaxHP());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Max STR: " + charClass.getMaxSTR());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Max MAG: " + charClass.getMaxMAG());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Max SKL: " + charClass.getMaxSKL());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Max SPD: " + charClass.getMaxSPD());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Max LCK: " + charClass.getMaxLCK());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Max DEF: " + charClass.getMaxDEF());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Max RES: " + charClass.getMaxRES());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "HP Growth: " + charClass.getHPGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "STR Growth: " + charClass.getSTRGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "MAG Growth: " + charClass.getMAGGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "SKL Growth: " + charClass.getSKLGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "SPD Growth: " + charClass.getSPDGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "LCK Growth: " + charClass.getLCKGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "DEF Growth: " + charClass.getDEFGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "RES Growth: " + charClass.getRESGrowth());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Unknown 8-2: " + WhyDoesJavaNotHaveThese.displayStringForBytes(charClass.getLaguzData()));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "===== End Printing Class =====");
	}
	
	private String stringForPointer(long pointer, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		if (pointer == 0) { return "(null)"; }
		String identifier = fe8databin.stringForPointer(pointer);
		if (commonTextLoader == null) { return identifier; }
		
		String resolvedValue = commonTextLoader.textStringForIdentifier(identifier);
		if (resolvedValue != null) {
			return identifier + " (" + resolvedValue + ")";
		} else {
			return identifier;
		}
	}
	
	private FE9Data.CharacterClass fe9ClassForClass(FE9Class charClass) {
		String classID = fe8databin.stringForPointer(charClass.getClassIDPointer());
		if (classID == null) { return null; }
		return FE9Data.CharacterClass.withJID(classID);
	}
	
	public void commit() {
		for (FE9Class fe9Class : allClasses) {
			fe9Class.commitChanges();
		}
	}
	
	public void compileDiffs(GCNISOHandler isoHandler) {
		for (FE9Class charClass : allClasses) {
			charClass.commitChanges();
			if (charClass.hasCommittedChanges()) {
				fe8databin.writeDataToSection(classDataSection, charClass.getAddressOffset(), charClass.getData());
			}
		}
	}
	
	public void recordOriginalClassData(ChangelogBuilder builder, ChangelogSection classDataSection,
			FE9CommonTextLoader textData) {
		ChangelogTOC classTOC = new ChangelogTOC("class-data");
		classTOC.addClass("class-section-toc");
		classDataSection.addElement(new ChangelogHeader(HeaderLevel.HEADING_2, "Class Data", "class-data-header"));
		classDataSection.addElement(classTOC);
		
		ChangelogSection classContainer = new ChangelogSection("class-data-container");
		classDataSection.addElement(classContainer);
		
		for (FE9Class charClass : allClasses) {
			createClassSection(charClass, textData, classTOC, classContainer, true);
		}
		
		setupRules(builder);
	}
	
	public void recordUpdatedClassData(ChangelogSection classDataSection, FE9CommonTextLoader textData) {
		ChangelogTOC classTOC = (ChangelogTOC)classDataSection.getChildWithIdentifier("class-data");
		ChangelogSection classContainer = (ChangelogSection)classDataSection.getChildWithIdentifier("class-data-container");
		
		for (FE9Class charClass : allClasses) {
			createClassSection(charClass, textData, classTOC, classContainer, false);
		}
	}
	
	private void createClassSection(FE9Class charClass, FE9CommonTextLoader textData, ChangelogTOC toc, ChangelogSection parentSection, boolean isOriginal) {
		String className = textData.textStringForIdentifier(getMJIDForClass(charClass));
		String anchor = "class-data-" + getJIDForClass(charClass);
		ChangelogTable classDataTable;
		ChangelogSection section;
		if (isOriginal) {
			section = new ChangelogSection(anchor + "-section");
			section.addClass("class-data-section");
			toc.addAnchorWithTitle(anchor, className);
			
			ChangelogHeader titleHeader = new ChangelogHeader(HeaderLevel.HEADING_3, className, anchor);
			titleHeader.addClass("class-data-title");
			section.addElement(titleHeader);
			
			classDataTable = new ChangelogTable(3, new String[] {"", "Old Value", "New Value"}, anchor + "-data-table");
			classDataTable.addClass("class-data-table");
			classDataTable.addRow(new String[] {"JID", getJIDForClass(charClass), ""});
			classDataTable.addRow(new String[] {"Name", className, ""});
			classDataTable.addRow(new String[] {"Description", textData.textStringForIdentifier(fe8databin.stringForPointer(charClass.getClassDescriptionPointer())), ""});
			classDataTable.addRow(new String[] {"HP Growth", "" + charClass.getHPGrowth() + "%", ""});
			classDataTable.addRow(new String[] {"STR Growth", "" + charClass.getSTRGrowth() + "%", ""});
			classDataTable.addRow(new String[] {"MAG Growth", "" + charClass.getMAGGrowth() + "%", ""});
			classDataTable.addRow(new String[] {"SKL Growth", "" + charClass.getSKLGrowth() + "%", ""});
			classDataTable.addRow(new String[] {"SPD Growth", "" + charClass.getSPDGrowth() + "%", ""});
			classDataTable.addRow(new String[] {"LCK Growth", "" + charClass.getLCKGrowth() + "%", ""});
			classDataTable.addRow(new String[] {"DEF Growth", "" + charClass.getDEFGrowth() + "%", ""});
			classDataTable.addRow(new String[] {"RES Growth", "" + charClass.getRESGrowth() + "%", ""});
		} else {
			section = (ChangelogSection)parentSection.getChildWithIdentifier(anchor + "-section");
			classDataTable = (ChangelogTable)section.getChildWithIdentifier(anchor + "-data-table");
			classDataTable.setContents(0, 2, getJIDForClass(charClass));
			classDataTable.setContents(1, 2, className);
			classDataTable.setContents(2, 2, textData.textStringForIdentifier(fe8databin.stringForPointer(charClass.getClassDescriptionPointer())));
			classDataTable.setContents(3, 2, "" + charClass.getHPGrowth() + "%");
			classDataTable.setContents(4, 2, "" + charClass.getSTRGrowth() + "%");
			classDataTable.setContents(5, 2, "" + charClass.getMAGGrowth() + "%");
			classDataTable.setContents(6, 2, "" + charClass.getSKLGrowth() + "%");
			classDataTable.setContents(7, 2, "" + charClass.getSPDGrowth() + "%");
			classDataTable.setContents(8, 2, "" + charClass.getLCKGrowth() + "%");
			classDataTable.setContents(9, 2, "" + charClass.getDEFGrowth() + "%");
			classDataTable.setContents(10, 2, "" + charClass.getRESGrowth() + "%");
		}
		
		if (isOriginal) {
			section.addElement(classDataTable);
			parentSection.addElement(section);
		}
	}
	
	private void setupRules(ChangelogBuilder builder) {
		ChangelogStyleRule tocStyle = new ChangelogStyleRule();
		tocStyle.setElementClass("class-section-toc");
		tocStyle.addRule("display", "flex");
		tocStyle.addRule("flex-direction", "row");
		tocStyle.addRule("width", "75%");
		tocStyle.addRule("align-items", "center");
		tocStyle.addRule("justify-content", "center");
		tocStyle.addRule("flex-wrap", "wrap");
		tocStyle.addRule("margin-left", "auto");
		tocStyle.addRule("margin-right", "auto");
		builder.addStyle(tocStyle);
		
		ChangelogStyleRule tocItemAfter = new ChangelogStyleRule();
		tocItemAfter.setOverrideSelectorString(".class-section-toc div:not(:last-child)::after");
		tocItemAfter.addRule("content", "\"|\"");
		tocItemAfter.addRule("margin", "0px 5px");
		builder.addStyle(tocItemAfter);
		
		ChangelogStyleRule classContainer = new ChangelogStyleRule();
		classContainer.setElementIdentifier("class-data-container");
		classContainer.addRule("display", "flex");
		classContainer.addRule("flex-direction", "row");
		classContainer.addRule("flex-wrap", "wrap");
		classContainer.addRule("justify-content", "center");
		classContainer.addRule("margin-left", "10px");
		classContainer.addRule("margin-right", "10px");
		builder.addStyle(classContainer);
		
		ChangelogStyleRule itemSection = new ChangelogStyleRule();
		itemSection.setElementClass("item-data-section");
		itemSection.addRule("margin", "20px");
		itemSection.addRule("flex", "0 0 400px");
		builder.addStyle(itemSection);
		
		ChangelogStyleRule tableStyle = new ChangelogStyleRule();
		tableStyle.setElementClass("class-data-table");
		tableStyle.addRule("width", "100%");
		tableStyle.addRule("border", "1px solid black");
		builder.addStyle(tableStyle);
		
		ChangelogStyleRule titleStyle = new ChangelogStyleRule();
		titleStyle.setElementClass("class-data-title");
		titleStyle.addRule("text-align", "center");
		builder.addStyle(titleStyle);
		
		ChangelogStyleRule columnStyle = new ChangelogStyleRule();
		columnStyle.setElementClass("class-data-table");
		columnStyle.setChildTags(new ArrayList<String>(Arrays.asList("td", "th")));
		columnStyle.addRule("border", "1px solid black");
		columnStyle.addRule("padding", "5px");
		builder.addStyle(columnStyle);
		
		ChangelogStyleRule firstColumnStyle = new ChangelogStyleRule();
		firstColumnStyle.setOverrideSelectorString(".class-data-table td:first-child");
		firstColumnStyle.addRule("width", "20%");
		firstColumnStyle.addRule("text-align", "right");
		builder.addStyle(firstColumnStyle);
		
		ChangelogStyleRule otherColumnStyle = new ChangelogStyleRule();
		otherColumnStyle.setOverrideSelectorString(".class-data-table th:not(:first-child)");
		otherColumnStyle.addRule("width", "40%");
		builder.addStyle(otherColumnStyle);
	}
}
