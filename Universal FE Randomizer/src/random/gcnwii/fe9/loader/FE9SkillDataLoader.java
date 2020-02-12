package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Data.Skill;
import fedata.gcnwii.fe9.FE9Skill;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9SkillDataLoader {
	
	List<FE9Skill> allSkills;
	
	Map<String, FE9Skill> skillBySID;
	
	GCNDataFileHandler fe8databin;
	
	public FE9SkillDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allSkills = new ArrayList<FE9Skill>();
		skillBySID = new HashMap<String, FE9Skill>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.SkillDataFilename);
		assert (handler instanceof GCNDataFileHandler);
		if (handler instanceof GCNDataFileHandler) {
			fe8databin = (GCNDataFileHandler)handler;
		}
		
		long offset = FE9Data.SkillDataStartOffset;
		for (int i = 0; i < FE9Data.SkillCount; i++) {
			long dataOffset = offset + i * FE9Data.SkillDataSize;
			byte[] data = handler.readBytesAtOffset(dataOffset, FE9Data.SkillDataSize);
			FE9Skill skill = new FE9Skill(data, dataOffset);
			allSkills.add(skill);
			
			debugPrintSkill(skill, handler, commonTextLoader);
			
			String sid = stringForPointer(skill.getSkillIDPointer(), handler, commonTextLoader);
			skillBySID.put(sid, skill);
		}
	}

	private void debugPrintSkill(FE9Skill skill, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "===== Printing Skill =====");
		
		DebugPrinter.log(DebugPrinter.Key.FE9_SKILL_LOADER, "SID: " + getSID(skill));
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
	
	public FE9Skill getSkillWithSID(String sid) {
		return skillBySID.get(sid);
	}
	
	public String getSID(FE9Skill skill) {
		assert(fe8databin != null);
		return fe8databin.stringForPointer(skill.getSkillIDPointer());
	}
	
	public FE9Skill skillWithDisplayName(String displayName) {
		for (FE9Data.Skill skill : FE9Data.Skill.values()) {
			if (skill.getDisplayString().equals(displayName)) {
				return getSkillWithSID(skill.getSID());
			}
		}
		
		return null;
	}
	
	public List<FE9Skill> skillList(boolean isForPlayableCharacter) {
		Set<FE9Data.Skill> skills = new HashSet<FE9Data.Skill>();
		skills.addAll(FE9Data.Skill.allValidSkills);
		if (!isForPlayableCharacter) {
			skills.removeAll(FE9Data.Skill.playerOnlySkills);
		}
		
		return skills.stream().sorted(new Comparator<FE9Data.Skill>() {
			@Override
			public int compare(Skill arg0, Skill arg1) {
				return arg0.getSID().compareTo(arg1.getSID());
			}
		}).map(fe9dataskill -> {
			return getSkillWithSID(fe9dataskill.getSID());
		}).collect(Collectors.toList());
	}
	
	public Long pointerForSkill(FE9Skill skill) {
		if (skill == null) { return null; }
		return fe8databin.pointerForString(getSID(skill));
	}
	
	public boolean isModifiableSkill(FE9Skill skill) {
		return FE9Data.Skill.withSID(getSID(skill)).isModifiable();
	}
	
	public boolean isOccultSkill(FE9Skill skill) {
		return FE9Data.Skill.withSID(getSID(skill)).isOccult();
	}
	
	public FE9Skill occultSkillForJID(String jid) {
		return getSkillWithSID(FE9Data.Skill.occultSkillForClass(FE9Data.CharacterClass.withJID(jid)).getSID());
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
