package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Data;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.WhyDoesJavaNotHaveThese;

public class FE9CharacterDataLoader {
	
	List<FE9Character> allCharacters;
	
	List<FE9Character> playableCharacters;
	
	Map<String, Long> knownAddresses;
	Map<Long, String> knownPointers;
	
	Map<String, FE9Character> idLookup;
	
	public FE9CharacterDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allCharacters = new ArrayList<FE9Character>();
		
		playableCharacters = new ArrayList<FE9Character>();
		
		knownAddresses = new HashMap<String, Long>();
		knownPointers = new HashMap<Long, String>();
		
		idLookup = new HashMap<String, FE9Character>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.CharacterDataFilename);
		long offset = FE9Data.CharacterDataStartOffset;
		for (int i = 0; i < FE9Data.CharacterCount; i++) {
			long dataOffset = offset + i * FE9Data.CharacterDataSize;
			byte[] data = handler.readBytesAtOffset(dataOffset, FE9Data.CharacterDataSize);
			FE9Character character = new FE9Character(data, dataOffset);
			allCharacters.add(character);
			
			debugPrintCharacter(character, handler, commonTextLoader);
			
			String pid = stringForPointer(character.getCharacterIDPointer(), handler, null);
			String mpid = stringForPointer(character.getCharacterNamePointer(), handler, null);
			String fid = stringForPointer(character.getPortraitPointer(), handler, null);
			String jid = stringForPointer(character.getClassPointer(), handler, null);
			String affiliation = stringForPointer(character.getAffiliationPointer(), handler, null);
			String weaponLevels = stringForPointer(character.getWeaponLevelsPointer(), handler, null);
			String sid1 = stringForPointer(character.getSkill1Pointer(), handler, null);
			String sid2 = stringForPointer(character.getSkill2Pointer(), handler, null);
			String sid3 = stringForPointer(character.getSkill3Pointer(), handler, null);
			String aid1 = stringForPointer(character.getUnpromotedAnimationPointer(), handler, null);
			String aid2 = stringForPointer(character.getPromotedAnimationPointer(), handler, null);
			
			knownAddresses.put(pid, character.getCharacterIDPointer());
			knownAddresses.put(mpid, character.getCharacterNamePointer());
			knownAddresses.put(fid, character.getPortraitPointer());
			knownAddresses.put(jid, character.getClassPointer());
			knownAddresses.put(affiliation, character.getAffiliationPointer());
			knownAddresses.put(weaponLevels, character.getWeaponLevelsPointer());
			knownAddresses.put(sid1, character.getSkill1Pointer());
			knownAddresses.put(sid2, character.getSkill2Pointer());
			knownAddresses.put(sid3, character.getSkill3Pointer());
			knownAddresses.put(aid1, character.getUnpromotedAnimationPointer());
			knownAddresses.put(aid2, character.getPromotedAnimationPointer());
			
			knownPointers.put(character.getCharacterIDPointer(), pid);
			knownPointers.put(character.getCharacterNamePointer(), mpid);
			knownPointers.put(character.getPortraitPointer(), fid);
			knownPointers.put(character.getClassPointer(), jid);
			knownPointers.put(character.getAffiliationPointer(), affiliation);
			knownPointers.put(character.getWeaponLevelsPointer(), weaponLevels);
			knownPointers.put(character.getSkill1Pointer(), sid1);
			knownPointers.put(character.getSkill2Pointer(), sid2);
			knownPointers.put(character.getSkill3Pointer(), sid3);
			knownPointers.put(character.getUnpromotedAnimationPointer(), aid1);
			knownPointers.put(character.getPromotedAnimationPointer(), aid2);
			
			FE9Data.Character fe9Char = FE9Data.Character.withPID(pid);
			if (fe9Char != null && fe9Char.isPlayable()) { playableCharacters.add(character); }
			
			idLookup.put(pid, character);
		}
	}
	
	public FE9Character[] allPlayableCharacters() {
		return playableCharacters.toArray(new FE9Character[playableCharacters.size()]);
	}
	
	public FE9Character characterWithID(String pid) {
		return idLookup.get(pid);
	}
	
	public String pointerLookup(long pointer) {
		return knownPointers.get(pointer);
	}
	
	public long addressLookup(String value) {
		return knownAddresses.get(value);
	}
	
	public void commit() {
		for (FE9Character character : allCharacters) {
			character.commitChanges();
		}
	}
	
	public void compileDiffs(GCNISOHandler isoHandler) {
		try {
			GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.CharacterDataFilename);
			for (FE9Character character : allCharacters) {
				character.commitChanges();
				if (character.hasCommittedChanges()) {
					Diff charDiff = new Diff(character.getAddressOffset(), character.getData().length, character.getData(), null);
					handler.addChange(charDiff);
				}
			}
		} catch (GCNISOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void debugPrintCharacter(FE9Character character, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "===== Printing Character =====");
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"PID: " + stringForPointer(character.getCharacterIDPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"MPID: " + stringForPointer(character.getCharacterNamePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"FID: " + stringForPointer(character.getPortraitPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"JID: " + stringForPointer(character.getClassPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"Affiliation: " + stringForPointer(character.getAffiliationPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"Weapon Levels: " + stringForPointer(character.getWeaponLevelsPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"SID: " + stringForPointer(character.getSkill1Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"SID 2: " + stringForPointer(character.getSkill2Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"SID 3: " + stringForPointer(character.getSkill3Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"Unpromoted AID: " + stringForPointer(character.getUnpromotedAnimationPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, 
				"Promoted AID: " + stringForPointer(character.getPromotedAnimationPointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Level: " + character.getLevel());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Build: " + character.getBuild());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Weight: " + character.getWeight());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Base HP: " + character.getBaseHP());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Base STR: " + character.getBaseSTR());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Base MAG: " + character.getBaseMAG());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Base SKL: " + character.getBaseSKL());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Base SPD: " + character.getBaseSPD());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Base LCK: " + character.getBaseLCK());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Base DEF: " + character.getBaseDEF());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Base RES: " + character.getBaseRES());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "HP Growth: " + character.getHPGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "STR Growth: " + character.getSTRGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "MAG Growth: " + character.getMAGGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "SKL Growth: " + character.getSKLGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "SPD Growth: " + character.getSPDGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "LCK Growth: " + character.getLCKGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "DEF Growth: " + character.getDEFGrowth());
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "RES Growth: " + character.getRESGrowth());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Unknown 6: " + WhyDoesJavaNotHaveThese.displayStringForBytes(character.getUnknown6Bytes()));
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Unknown 8: " + WhyDoesJavaNotHaveThese.displayStringForBytes(character.getUnknown8Bytes()));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "===== End Printing Character =====");
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
	
	private FE9Data.Character fe9CharacterForCharacter(FE9Character character) {
		String characterID = pointerLookup(character.getCharacterIDPointer());
		if (characterID == null) { return null; }
		return FE9Data.Character.withPID(characterID);
	}

}
