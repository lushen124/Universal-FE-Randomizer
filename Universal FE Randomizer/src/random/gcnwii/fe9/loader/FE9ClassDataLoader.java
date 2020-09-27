package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponType;
import util.DebugPrinter;
import util.Diff;
import util.WhyDoesJavaNotHaveThese;

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
	
	Map<String, Long> knownAddresses;
	Map<Long, String> knownPointers;
	
	Map<String, FE9Class> idLookup;
	
	GCNDataFileHandler fe8databin;
	FE9CommonTextLoader textLoader;
	
	public enum StatBias {
		NONE, PHYSICAL_ONLY, MAGICAL_ONLY, LEAN_PHYSICAL, LEAN_MAGICAL
	}
	
	public FE9ClassDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		textLoader = commonTextLoader;
		
		allClasses = new ArrayList<FE9Class>();
		
		knownAddresses = new HashMap<String, Long>();
		knownPointers = new HashMap<Long, String>();
		
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
		assert(handler instanceof GCNDataFileHandler);
		if (handler instanceof GCNDataFileHandler) {
			fe8databin = (GCNDataFileHandler)handler;
		}
		
		long offset = FE9Data.ClassDataStartOffset;
		for (int i = 0; i < FE9Data.ClassCount; i++) {
			long dataOffset = offset + i * FE9Data.ClassDataSize;
			byte[] data = handler.readBytesAtOffset(dataOffset, FE9Data.ClassDataSize);
			FE9Class charClass = new FE9Class(data, dataOffset);
			allClasses.add(charClass);
			
			debugPrintClass(charClass, handler, commonTextLoader);
			
			String jid = stringForPointer(charClass.getClassIDPointer(), handler, null);
			String mjid = stringForPointer(charClass.getClassNamePointer(), handler, null);
			String mhj = stringForPointer(charClass.getClassDescriptionPointer(), handler, null);
			String promotedJID = stringForPointer(charClass.getPromotionIDPointer(), handler, null);
			String defaultIID = stringForPointer(charClass.getDefaultWeaponPointer(), handler, null);
			String weaponLevels = stringForPointer(charClass.getWeaponLevelPointer(), handler, null);
			String sid1 = stringForPointer(charClass.getSkill1Pointer(), handler, null);
			String sid2 = stringForPointer(charClass.getSkill2Pointer(), handler, null);
			String sid3 = stringForPointer(charClass.getSkill3Pointer(), handler, null);
			String race = stringForPointer(charClass.getRacePointer(), handler, null);
			
			knownAddresses.put(jid, charClass.getClassIDPointer());
			knownAddresses.put(mjid, charClass.getClassNamePointer());
			knownAddresses.put(mhj, charClass.getClassDescriptionPointer());
			knownAddresses.put(promotedJID, charClass.getPromotionIDPointer());
			knownAddresses.put(defaultIID, charClass.getDefaultWeaponPointer());
			knownAddresses.put(weaponLevels, charClass.getWeaponLevelPointer());
			knownAddresses.put(sid1, charClass.getSkill1Pointer());
			knownAddresses.put(sid2, charClass.getSkill2Pointer());
			knownAddresses.put(sid3, charClass.getSkill3Pointer());
			knownAddresses.put(race, charClass.getRacePointer());
			
			knownPointers.put(charClass.getClassIDPointer(), jid);
			knownPointers.put(charClass.getClassNamePointer(), mjid);
			knownPointers.put(charClass.getClassDescriptionPointer(), mhj);
			knownPointers.put(charClass.getPromotionIDPointer(), promotedJID);
			knownPointers.put(charClass.getDefaultWeaponPointer(), defaultIID);
			knownPointers.put(charClass.getWeaponLevelPointer(), weaponLevels);
			knownPointers.put(charClass.getSkill1Pointer(), sid1);
			knownPointers.put(charClass.getSkill2Pointer(), sid2);
			knownPointers.put(charClass.getSkill3Pointer(), sid3);
			knownPointers.put(charClass.getRacePointer(), race);
			
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
		
		Long pointer = fe8databin.pointerForString(sid);
		fe8databin.addPointerOffset(charClass.getAddressOffset() + FE9Class.ClassSkill1Offset - 0x20);
		if (pointer == null) {
			fe8databin.addString(sid);
			fe8databin.commitAdditions();
			pointer = fe8databin.pointerForString(sid);
		}
		
		charClass.setSkill1Pointer(pointer);
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
		
		Long pointer = fe8databin.pointerForString(sid);
		fe8databin.addPointerOffset(charClass.getAddressOffset() + FE9Class.ClassSkill2Offset - 0x20);
		if (pointer == null) {
			fe8databin.addString(sid);
			fe8databin.commitAdditions();
			pointer = fe8databin.pointerForString(sid);
		}
		
		charClass.setSkill2Pointer(pointer);
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
		
		Long pointer = fe8databin.pointerForString(sid);
		fe8databin.addPointerOffset(charClass.getAddressOffset() + FE9Class.ClassSkill3Offset - 0x20);
		if (pointer == null) {
			fe8databin.addString(sid);
			fe8databin.commitAdditions();
			pointer = fe8databin.pointerForString(sid);
		}
		
		charClass.setSkill3Pointer(pointer);
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
		fe8databin.commitAdditions();
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
		if (fe9Class == null) { 
			String classID = knownPointers.get(charClass.getClassIDPointer());
			DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Unknown class found. Class ID: " + classID);
			return StatBias.NONE;
		}
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
	
	public String pointerLookup(long pointer) {
		return knownPointers.get(pointer);
	}
	
	public long addressLookup(String value) {
		return knownAddresses.get(value);
	}
	
	public String getDisplayName(FE9Class charClass) {
		long pointer = charClass.getClassNamePointer();
		if (pointer == 0) { return "(null)"; }
		fe8databin.setNextReadOffset(pointer);
		byte[] bytes = fe8databin.continueReadingBytesUpToNextTerminator(pointer + 0xFF);
		String identifier = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(bytes);
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
		handler.setNextReadOffset(pointer);
		byte[] bytes = handler.continueReadingBytesUpToNextTerminator(pointer + 0xFF);
		String identifier = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(bytes);
		if (commonTextLoader == null) { return identifier; }
		
		String resolvedValue = commonTextLoader.textStringForIdentifier(identifier);
		if (resolvedValue != null) {
			return identifier + " (" + resolvedValue + ")";
		} else {
			return identifier;
		}
	}
	
	private FE9Data.CharacterClass fe9ClassForClass(FE9Class charClass) {
		String classID = pointerLookup(charClass.getClassIDPointer());
		if (classID == null) { return null; }
		return FE9Data.CharacterClass.withJID(classID);
	}
	
	public void commit() {
		for (FE9Class fe9Class : allClasses) {
			fe9Class.commitChanges();
		}
	}
	
	public void compileDiffs(GCNISOHandler isoHandler) {
		try {
			GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.ClassDataFilename);
			for (FE9Class charClass : allClasses) {
				charClass.commitChanges();
				if (charClass.hasCommittedChanges()) {
					Diff classDiff = new Diff(charClass.getAddressOffset(), charClass.getData().length, charClass.getData(), null);
					handler.addChange(classDiff);
				}
			}
		} catch (GCNISOException e) {
			e.printStackTrace();
		}
	}
}
