package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.List;

import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Data;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9CharacterDataLoader {
	
	List<FE9Character> allCharacters;
	
	public FE9CharacterDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allCharacters = new ArrayList<FE9Character>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.CharacterDataFilename);
		long offset = FE9Data.CharacterDataStartOffset;
		for (int i = 0; i < FE9Data.CharacterCount; i++) {
			long dataOffset = offset + i * FE9Data.CharacterDataSize;
			byte[] data = handler.readBytesAtOffset(dataOffset, FE9Data.CharacterDataSize);
			FE9Character character = new FE9Character(data, dataOffset);
			allCharacters.add(character);
			
			debugPrintCharacter(character, handler, commonTextLoader);
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
		String resolvedValue = commonTextLoader.textStringForIdentifier(identifier);
		if (resolvedValue != null) {
			return identifier + " (" + resolvedValue + ")";
		} else {
			return identifier;
		}
	}

}
