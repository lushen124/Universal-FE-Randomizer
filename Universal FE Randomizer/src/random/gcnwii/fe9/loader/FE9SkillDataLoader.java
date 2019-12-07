package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.List;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Skill;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9SkillDataLoader {
	
	List<FE9Skill> allSkills;
	
	public FE9SkillDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allSkills = new ArrayList<FE9Skill>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.SkillDataFilename);
		long offset = FE9Data.SkillDataStartOffset;
		for (int i = 0; i < FE9Data.SkillCount; i++) {
			long dataOffset = offset + i * FE9Data.SkillDataSize;
			byte[] data = handler.readBytesAtOffset(dataOffset, FE9Data.SkillDataSize);
			FE9Skill skill = new FE9Skill(data, dataOffset);
			allSkills.add(skill);
			
			debugPrintSkill(skill, handler, commonTextLoader);
		}
	}

	private void debugPrintSkill(FE9Skill skill, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "===== Printing Skill =====");
		
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "SID: " + stringForPointer(skill.getSkillIDPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, 
				"Unknown Pointer: 0x" + Long.toHexString(skill.getUnknownPointer()) + 
				" (" + rawBytesStringForPointer(skill.getUnknownPointer(), handler) + ")");
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "MSID: " + stringForPointer(skill.getSkillNamePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "Mess_Help: " + stringForPointer(skill.getHelpText1Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "Mess_Help2: " + stringForPointer(skill.getHelpText2Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "EID: " + stringForPointer(skill.getEffectIDPointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "Skill Number?: " + skill.getSkillNumber());
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "Unknown Value 1: " + skill.getUnknownValue1());
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "Skill Cost: " + skill.getSkillCost());
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "Unknown Value 2: " + skill.getUnknownValue2());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "Unknown Pointer?: " + Long.toHexString(skill.getUnknownPointer2()));
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "IID: " + stringForPointer(pointerAtPointer(skill.getItemIDPointer(), handler), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "JID: " + stringForPointer(pointerAtPointer(skill.getClassRestrictionPointer(), handler), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "===== End Printing Skill =====");
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
	
	private long pointerAtPointer(long pointer, GCNFileHandler handler) {
		if (pointer == 0) { return 0; }
		handler.setNextReadOffset(pointer);
		long nextPointer = WhyDoesJavaNotHaveThese.longValueFromByteArray(handler.continueReadingBytes(4), false);
		return nextPointer + 0x20;
	}
	
	private String rawBytesStringForPointer(long pointer, GCNFileHandler handler) {
		if (pointer == 0) { return "(null)"; }
		handler.setNextReadOffset(pointer);
		byte[] bytes = handler.continueReadingBytesUpToNextTerminator(pointer + 0xFF);
		return WhyDoesJavaNotHaveThese.displayStringForBytes(bytes) + " (" + WhyDoesJavaNotHaveThese.stringFromAsciiBytes(bytes) + ")";
	}
}
