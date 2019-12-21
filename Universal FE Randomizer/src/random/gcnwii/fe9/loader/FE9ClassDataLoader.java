package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9ClassDataLoader {
	
	List<FE9Class> allClasses;
	
	Map<String, Long> knownAddresses;
	Map<Long, String> knownPointers;
	
	Map<String, FE9Class> idLookup;
	
	public enum StatBias {
		NONE, PHYSICAL_ONLY, MAGICAL_ONLY, LEAN_PHYSICAL, LEAN_MAGICAL
	}
	
	public FE9ClassDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allClasses = new ArrayList<FE9Class>();
		
		knownAddresses = new HashMap<String, Long>();
		knownPointers = new HashMap<Long, String>();
		
		idLookup = new HashMap<String, FE9Class>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.ClassDataFilename);
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
		}
	}
	
	public FE9Class classWithID(String jid) {
		return idLookup.get(jid);
	}
	
	public StatBias statBiasForClass(FE9Class charClass) {
		FE9Data.CharacterClass fe9Class = fe9ClassForClass(charClass);
		if (fe9Class == null) { 
			String classID = knownPointers.get(charClass.getClassIDPointer());
			DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Unknown class found. Class ID: " + classID);
			return StatBias.NONE;
		}
		if (fe9Class.isPhysicalClass()) { return fe9Class.isHybridPhyiscalClass() ? StatBias.LEAN_PHYSICAL : StatBias.PHYSICAL_ONLY; }
		else if (fe9Class.isMagicalClass()) { return fe9Class.isHybridMagicalClass() ? StatBias.LEAN_MAGICAL : StatBias.MAGICAL_ONLY; }
		return StatBias.NONE;
	}
	
	public String pointerLookup(long pointer) {
		return knownPointers.get(pointer);
	}
	
	public long addressLookup(String value) {
		return knownAddresses.get(value);
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
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CLASS_LOADER, "Unknown 8-1: " + WhyDoesJavaNotHaveThese.displayStringForBytes(charClass.getUnknown8Bytes()));
		
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
}
