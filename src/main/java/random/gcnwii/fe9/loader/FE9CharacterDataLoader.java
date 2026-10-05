package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gcnwii.fe9.FE9Base64;
import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Skill;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNDataFileHandlerV2;
import io.gcn.GCNDataFileHandlerV2.GCNDataFileDataSection;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponRank;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponType;
import util.DebugPrinter;
import util.Diff;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.fe9.Base64Asset;
import util.recordkeeper.fe9.ChangelogAsset;
import util.recordkeeper.fe9.ChangelogBuilder;
import util.recordkeeper.fe9.ChangelogElement;
import util.recordkeeper.fe9.ChangelogHeader;
import util.recordkeeper.fe9.ChangelogSection;
import util.recordkeeper.fe9.ChangelogStyleRule;
import util.recordkeeper.fe9.ChangelogTOC;
import util.recordkeeper.fe9.ChangelogTable;
import util.recordkeeper.fe9.ChangelogText;
import util.recordkeeper.fe9.ChangelogHeader.HeaderLevel;
import util.recordkeeper.fe9.ChangelogText.Style;

public class FE9CharacterDataLoader {
	
	List<FE9Character> allCharacters;
	
	List<FE9Character> playableCharacters;
	List<FE9Character> bossCharacters;
	List<FE9Character> minionCharacters;
	
	Map<String, List<FE9Character>> daeinMinionsByJID;
	Map<String, List<FE9Character>> pid14MinionsByJID;
	
	Map<String, FE9Character> idLookup;
	
	GCNDataFileHandlerV2 fe8databin;
	GCNDataFileDataSection characterDataSection;
	FE9CommonTextLoader textData;
	
	public FE9CharacterDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allCharacters = new ArrayList<FE9Character>();
		
		playableCharacters = new ArrayList<FE9Character>();
		bossCharacters = new ArrayList<FE9Character>();
		minionCharacters = new ArrayList<FE9Character>();
		
		daeinMinionsByJID = new HashMap<String, List<FE9Character>>();
		pid14MinionsByJID = new HashMap<String, List<FE9Character>>();
		
		idLookup = new HashMap<String, FE9Character>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.CharacterDataFilename);
		assert (handler instanceof GCNDataFileHandlerV2);
		if (handler instanceof GCNDataFileHandlerV2) {
			fe8databin = (GCNDataFileHandlerV2)handler;
		}
		
		textData = commonTextLoader;
		
		characterDataSection = fe8databin.getSectionWithName(FE9Data.CharacterDataSectionName);
		int count = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(characterDataSection.getRawData(0, 4), false);
		
		long offset = 4;
		for (int i = 0; i < count; i++) {
			long dataOffset = offset + i * FE9Data.CharacterDataSize;
			byte[] data = characterDataSection.getRawData(dataOffset, FE9Data.CharacterDataSize);
			FE9Character character = new FE9Character(data, dataOffset);
			allCharacters.add(character);
			
			debugPrintCharacter(character, handler, commonTextLoader);
			
			String pid = fe8databin.stringForPointer(character.getCharacterIDPointer());
			
			FE9Data.Character fe9Char = FE9Data.Character.withPID(pid);
			if (fe9Char != null && fe9Char.isPlayable()) { playableCharacters.add(character); }
			
			idLookup.put(pid, character);
			
			String sid1 = fe8databin.stringForPointer(character.getSkill1Pointer());
			String sid2 = fe8databin.stringForPointer(character.getSkill2Pointer());
			String sid3 = fe8databin.stringForPointer(character.getSkill3Pointer());
			
			if (FE9Data.Skill.BOSS.getSID().equals(sid1) || FE9Data.Skill.BOSS.getSID().equals(sid2) || FE9Data.Skill.BOSS.getSID().equals(sid3)) {
				bossCharacters.add(character);
			}
			
			String jid = fe8databin.stringForPointer(character.getClassPointer());
			
			if (isMinionCharacter(character)) {
				minionCharacters.add(character);
				if (pid.contains("_DAYNE")) {
					// Daein soldiers have classes built into them, so we need to explicitly change PIDs when randomizing minions later.
					// That said, some of these have special scripts built in, so some characters cannot be changed.
					// The ones this applies to seems to be those with a PID that ends in a number.
					// e.g. PID_DAYNE_SOL_1
					List<FE9Character> daeinCharacters = daeinMinionsByJID.get(jid);
					if (daeinCharacters == null) {
						daeinCharacters = new ArrayList<FE9Character>();
						daeinMinionsByJID.put(jid, daeinCharacters);
					}
					daeinCharacters.add(character);
				} else if (pid.startsWith("PID_14_")) {
					// Chapter 13 units also do the same thing as the Daein soldiers, where their classes are baked in to the PID.
					List<FE9Character> pid14Characters = pid14MinionsByJID.get(jid);
					if (pid14Characters == null) {
						pid14Characters = new ArrayList<FE9Character>();
						pid14MinionsByJID.put(jid, pid14Characters);
					}
					pid14Characters.add(character);
				}
			}
		}
	}
	
	public boolean isPlayableCharacter(FE9Character character) {
		if (character == null) { return false; }
		FE9Data.Character fe9Char = FE9Data.Character.withPID(fe8databin.stringForPointer(character.getCharacterIDPointer()));
		if (fe9Char == null) { return false; }
		return fe9Char.isPlayable();
	}
	
	public boolean isBossCharacter(FE9Character character) {
		if (character == null) { return false; }
		FE9Data.Character fe9Char = FE9Data.Character.withPID(fe8databin.stringForPointer(character.getCharacterIDPointer()));
		if (fe9Char == null) { return false; }
		return fe9Char.isBoss();
	}
	
	public boolean isMinionCharacter(FE9Character character) {
		if (character == null) { return false; }
		String pid = fe8databin.stringForPointer(character.getCharacterIDPointer());
		return (pid.contains("_DAYNE") || pid.contains("_ZAKO") || pid.contains("_BANDIT") ||
				pid.startsWith("PID_14_") || // Chapter 13, also has classes baked in.
				pid.startsWith("PID_15_PEDDLING") || // Chapter 14 minions all use the same PID, with an optional suffix for the two Feral Ones.
				pid.equals("PID_16_LIBERATION") || // Chapter 15 laguz units all use the same PID.
				pid.equals("PID_178_TANAS") // Chapter 16 and 17 are all the same.
				) && !pid.contains("_EV");
	}
	
	public boolean isRestrictedMinionCharacterPID(String pid) {
		// These characters are specially referenced by chapter scripts, so we need to be careful about changing them.
		if (isMinionCharacter(characterWithID(pid))) {
			if (pid.contains("_DAYNE")) {
				// Chapter 4 has some scripted soldiers that appear.
				if (pid.matches("PID_DAYNE_[A-Z]{3}_[0-9]+")) { return true; }
				return false;
			} else {
				// Chapter 10's watches are a little special.
				if (pid.startsWith("PID_D_ZAKO_11_")) { return true; }
				return false;
			}
		}
		
		return false;
	}
	
	public boolean isModifiableCharacter(FE9Character character) {
		if (character == null) { return false; }
		FE9Data.Character fe9Char = FE9Data.Character.withPID(getPIDForCharacter(character));
		if (fe9Char == null) { return false; }
		return fe9Char.isModifiable() && !fe9Char.isBugged();
	}
	
	public List<FE9Character> allCharacters() {
		return allCharacters;
	}
	
	public FE9Character[] allPlayableCharacters() {
		return playableCharacters.toArray(new FE9Character[playableCharacters.size()]);
	}
	
	public FE9Character[] allBossCharacters() {
		return bossCharacters.toArray(new FE9Character[bossCharacters.size()]);
	}
	
	public FE9Character[] allMinionCharacters() {
		return minionCharacters.toArray(new FE9Character[minionCharacters.size()]);
	}
	
	public FE9Character characterWithID(String pid) {
		return idLookup.get(pid);
	}
	
	public boolean isDaeinCharacter(FE9Character character) {
		String pid = getPIDForCharacter(character);
		if (pid == null) { return false; }
		return pid.contains("_DAYNE");
	}
	
	public List<String> availableJIDsForDaeinMinions() {
		List<String> jids = new ArrayList<String>(daeinMinionsByJID.keySet());
		jids.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return jids;
	}
	
	public List<String> availableJIDsForPID14Minions() {
		List<String> jids = new ArrayList<String>(pid14MinionsByJID.keySet());
		jids.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return jids;
	}
	
	public boolean isPID14Character(FE9Character character) {
		String pid = getPIDForCharacter(character);
		if (pid == null) { return false; }
		return pid.startsWith("PID_14");
	}
	
	public List<FE9Character> getDaeinCharactersForJID(String jid) {
		return daeinMinionsByJID.get(jid);
	}
	
	public List<FE9Character> getPID14CharactersForJID(String jid) {
		return pid14MinionsByJID.get(jid);
	}
	
	public String getDisplayName(FE9Character character) {
		return textData.textStringForIdentifier(getMPIDForCharacter(character));
	}
	
	public String getPIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getCharacterIDPointer() == 0) { return null; }
		
		return fe8databin.stringForPointer(character.getCharacterIDPointer());
	}
	
	public String getMPIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getCharacterNamePointer() == 0) { return null; }
		
		return fe8databin.stringForPointer(character.getCharacterNamePointer());
	}
	
	public String getFIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getPortraitPointer() == 0) { return null; }
		
		return fe8databin.stringForPointer(character.getPortraitPointer());
	}
	
	public String getJIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getClassPointer() == 0) { return null; }
		
		return fe8databin.stringForPointer(character.getClassPointer());
	}
	
	public void setJIDForCharacter(FE9Character character, String jid) {
		fe8databin.addString(jid);
		Long jidAddress = fe8databin.pointerForString(jid);
		character.setClassPointer(jidAddress);
	}
	
	public String getUnpromotedAIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getUnpromotedAnimationPointer() == 0) { return null; }
		return fe8databin.stringForPointer(character.getUnpromotedAnimationPointer());
	}
	
	public void setUnpromotedAIDForCharacter(FE9Character character, String aid) {
		if (character == null) { return; }
		if (aid == null) {
			character.setUnpromotedAnimationPointer(0);
			return;
		}
		
		// For some reason, giving Mist any AID for unpromoted causes her to render on the map
		// as a shadow Ike. There may be other characters that share this trait.
		// Nephenee also shares this trait.
		if (getPIDForCharacter(character).equals(FE9Data.Character.MIST.getPID()) ||
				getPIDForCharacter(character).equals(FE9Data.Character.NEPHENEE.getPID())) { return; }
		
		fe8databin.addString(aid);
		Long aidAddress = fe8databin.pointerForString(aid);
		character.setUnpromotedAnimationPointer(aidAddress);
	}
	
	public String getPromotedAIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getPromotedAnimationPointer() == 0) { return null; }
		return fe8databin.stringForPointer(character.getPromotedAnimationPointer());
	}
	
	public void setPromotedAIDForCharacter(FE9Character character, String aid) {
		if (character == null) { return; }
		if (aid == null) {
			character.setPromotedAnimationPointer(0);
			return;
		}
		
		// This was necessary for Mist's unpromoted model. It's probably true here too.
		if (getPIDForCharacter(character).equals(FE9Data.Character.MIST.getPID()) ||
				getPIDForCharacter(character).equals(FE9Data.Character.NEPHENEE.getPID())) { return; }
		
		fe8databin.addString(aid);
		Long aidAddress = fe8databin.pointerForString(aid);
		character.setPromotedAnimationPointer(aidAddress);
	}
	
	public int getLaguzStartingGaugeForCharacter(FE9Character character) {
		return character.getLaguzTransformationStartingValue();
	}
	
	public void setLaguzStartingGaugeForCharacter(FE9Character character, int value) {
		character.setLaguzTransformationStartingValue(WhyDoesJavaNotHaveThese.clamp(value, 0, 20));
	}
	
	public String getWeaponLevelStringForCharacter(FE9Character character) {
		if (character == null) { return null; }
		return fe8databin.stringForPointer(character.getWeaponLevelsPointer());
	}
	
	public void setWeaponLevelStringForCharacter(FE9Character character, String weaponLevelString) {
		if (character == null || weaponLevelString == null || weaponLevelString.length() != 9) { return; }
		// Validate string. We only allow -, *, S, A, B, C, D, E characters.
		for (int i = 0; i < weaponLevelString.length(); i++) {
			char c = weaponLevelString.charAt(i);
			if (c != '-' && c != '*' && c != 'S' && c != 'A' && c != 'B' && c != 'C' && c != 'D' && c != 'E') {
				return;
			}
		}
		
		fe8databin.addString(weaponLevelString);
		character.setWeaponLevelsPointer(fe8databin.pointerForString(weaponLevelString));
	}
	
	public String getSID1ForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getSkill1Pointer() == 0) { return null; }
		
		return fe8databin.stringForPointer(character.getSkill1Pointer());
	}
	
	public void setSID1ForCharacter(FE9Character character, String sid) {
		if (character == null) { return; }
		if (sid == null) {
			character.setSkill1Pointer(0);
			return;
		}
		
		fe8databin.addString(sid);
		fe8databin.addPointerOffset(characterDataSection, character.getAddressOffset() + FE9Character.CharacterSkill1Offset);
		character.setSkill1Pointer(fe8databin.pointerForString(sid));
	}
	
	public String getSID2ForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getSkill2Pointer() == 0) { return null; }
		
		return fe8databin.stringForPointer(character.getSkill2Pointer());
	}
	
	public void setSID2ForCharacter(FE9Character character, String sid) {
		if (character == null) { return; }
		if (sid == null) {
			character.setSkill2Pointer(0);
			return;
		}
		
		fe8databin.addString(sid);
		fe8databin.addPointerOffset(characterDataSection, character.getAddressOffset() + FE9Character.CharacterSkill2Offset);
		character.setSkill2Pointer(fe8databin.pointerForString(sid));
	}
	
	public String getSID3ForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getSkill3Pointer() == 0) { return null; }
		
		return fe8databin.stringForPointer(character.getSkill3Pointer());
	}
	
	public void setSID3ForCharacter(FE9Character character, String sid) {
		if (character == null) { return; }
		if (sid == null) {
			character.setSkill3Pointer(0);
			return;
		}
		
		fe8databin.addString(sid);
		fe8databin.addPointerOffset(characterDataSection, character.getAddressOffset() + FE9Character.CharacterSkill3Offset);
		character.setSkill3Pointer(fe8databin.pointerForString(sid));
	}
	
	public int getLevelForCharacter(FE9Character character) {
		return character.getLevel();
	}
	
	public int getBuildForCharacter(FE9Character character) {
		return character.getBuild();
	}
	
	public int getWeightForCharacter(FE9Character character) {
		return character.getWeight();
	}
	
	public FE9Data.Affinity getAffinityForCharacter(FE9Character character) {
		String affinityID = fe8databin.stringForPointer(character.getAffinityPointer());
		return FE9Data.Affinity.withID(affinityID);
	}
	
	public void setAffinityForCharacter(FE9Character character, FE9Data.Affinity affinity) {
		if (character == null) { return; }
		long affinityPtr = fe8databin.pointerForString(affinity.getInternalID());
		character.setAffinityPointer(affinityPtr);
	}
	
	public void commit() {
		for (FE9Character character : allCharacters) {
			character.commitChanges();
		}
	}
	
	public void compileDiffs(GCNISOHandler isoHandler) {
		for (FE9Character character : allCharacters) {
			DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Writing character: " + getDisplayName(character));
			character.commitChanges();
			if (character.hasCommittedChanges()) {
				fe8databin.writeDataToSection(characterDataSection, character.getAddressOffset(), character.getData());
			}
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
				"Affinity: " + stringForPointer(character.getAffinityPointer(), handler, commonTextLoader));
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
		DebugPrinter.log(DebugPrinter.Key.FE9_CHARACTER_LOADER, "Unknown 8: " + WhyDoesJavaNotHaveThese.displayStringForBytes(character.getUnknown13Bytes()));
		
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
		String characterID = fe8databin.stringForPointer(character.getCharacterIDPointer());
		if (characterID == null) { return null; }
		return FE9Data.Character.withPID(characterID);
	}

	public void recordOriginalCharacterData(ChangelogBuilder builder, ChangelogSection characterDataSection, 
			FE9CommonTextLoader textData, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, FE9ItemDataLoader itemData,
			FE9ChapterDataLoader chapterData) {
		ChangelogTOC playableTOC = new ChangelogTOC("playable-character-data");
		playableTOC.addClass("character-section-toc");
		characterDataSection.addElement(new ChangelogHeader(HeaderLevel.HEADING_2, "Character Data", "character-data-header"));
		characterDataSection.addElement(playableTOC);
		
		ChangelogSection pcDataSection = new ChangelogSection("pc-data-section");
		characterDataSection.addElement(pcDataSection);
		
		for (FE9Character character : playableCharacters) {
			createCharacterSection(character, classData, skillData, textData, itemData, playableTOC, pcDataSection, true);
		}
		
		ChangelogTOC bossTOC = new ChangelogTOC("boss-character-data");
		bossTOC.addClass("character-section-toc");
		characterDataSection.addElement(new ChangelogHeader(HeaderLevel.HEADING_2, "Boss Data", "boss-data-header"));
		characterDataSection.addElement(bossTOC);
		
		ChangelogSection bossDataSection = new ChangelogSection("boss-data-section");
		characterDataSection.addElement(bossDataSection);
		
		for (FE9Character character : bossCharacters) {
			createBossSection(character, classData, skillData, textData, itemData, chapterData, bossTOC, bossDataSection, true);
		}
		
		setupRules(builder);
	}
	
	public void recordUpdatedCharacterData(ChangelogSection characterDataSection,
			FE9CommonTextLoader textData, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, FE9ItemDataLoader itemData,
			FE9ChapterDataLoader chapterData) {
		ChangelogTOC playableTOC = (ChangelogTOC)characterDataSection.getChildWithIdentifier("playable-character-data");
		ChangelogSection pcDataSection = (ChangelogSection)characterDataSection.getChildWithIdentifier("pc-data-section");
		for (FE9Character character : playableCharacters) {
			createCharacterSection(character, classData, skillData, textData, itemData, playableTOC, pcDataSection, false);
		}
		ChangelogSection bossDataSection = (ChangelogSection)characterDataSection.getChildWithIdentifier("boss-data-section");
		ChangelogTOC bossTOC = (ChangelogTOC)characterDataSection.getChildWithIdentifier("boss-character-data");
		for (FE9Character character : bossCharacters) {
			createBossSection(character, classData, skillData, textData, itemData, chapterData, bossTOC, bossDataSection, false);
		}
	}
	
	private void createBossSection(FE9Character boss, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, 
			FE9CommonTextLoader textData, FE9ItemDataLoader itemData, FE9ChapterDataLoader chapterData, ChangelogTOC toc, 
			ChangelogSection parentSection, boolean isOriginal) {
		String characterName = textData.textStringForIdentifier(getMPIDForCharacter(boss));
		String anchor = "char-data-" + getPIDForCharacter(boss);
		ChangelogTable characterDataTable;
		ChangelogSection section;
		if (isOriginal) {
			section = new ChangelogSection(anchor + "-section");
			section.addClass("character-data-section");
			toc.addAnchorWithTitle(anchor, characterName);
		
			ChangelogHeader titleHeader = new ChangelogHeader(HeaderLevel.HEADING_3, characterName, anchor);
			titleHeader.addClass("char-data-title");
			section.addElement(titleHeader);
		
			characterDataTable = new ChangelogTable(3, new String[] {"", "Old Value", "New Value"}, anchor + "-data-table");
			characterDataTable.addClass("character-data-table");
			characterDataTable.addRow(new String[] {"PID", getPIDForCharacter(boss), ""});
			characterDataTable.addRow(new String[] {"Name", characterName, ""});
		} else {
			section = (ChangelogSection)parentSection.getChildWithIdentifier(anchor + "-section");
			characterDataTable = (ChangelogTable)section.getChildWithIdentifier(anchor + "-data-table");
			characterDataTable.setContents(0, 2, getPIDForCharacter(boss));
			characterDataTable.setContents(1, 2, characterName);
		}
		
		String jid = getJIDForCharacter(boss);
		FE9Class charClass = classData.classWithID(jid);
		String className = textData.textStringForIdentifier(classData.getMJIDForClass(charClass));
		if (isOriginal) {
			characterDataTable.addRow(new String[] {"Class", className + " (" + jid + ")", ""});
		} else {
			characterDataTable.setContents(2, 2, className + " (" + jid + ")");
		}
		
		int row = 3;
		
		FE9Data.Affinity affinity = getAffinityForCharacter(boss);
		if (affinity != null) {
			if (isOriginal) { characterDataTable.addRow(new String[] {"Affinity", "", ""}); }
			ChangelogSection affinityCell = new ChangelogSection("char-data-" + (isOriginal ? "original" : "new") + "-affinity-" + getPIDForCharacter(boss));
			Base64Asset affinityAsset = new Base64Asset("affinity-" + affinity.toString(), 
					FE9Base64.affinityBase64Prefix + FE9Base64.base64StringForAffinity(affinity), 
					24, 24);
			affinityCell.addElement(new ChangelogAsset(anchor + "-affinity-image", affinityAsset));
			ChangelogText affinityText = new ChangelogText(anchor + "-affinity-text", Style.NONE, affinity.toString() + " (" + affinity.getInternalID() + ")");
			affinityText.addClass("char-data-affinity-text");
			affinityCell.addElement(affinityText);
			affinityCell.addClass("char-data-affinity-cell");
			characterDataTable.setElement(row, isOriginal ? 1 : 2, affinityCell);
			row++;
		}
		
		String weaponLevelString = getWeaponLevelStringForCharacter(boss);
		Map<WeaponType, WeaponRank> ranks = itemData.weaponLevelsForWeaponString(weaponLevelString);
		ChangelogSection weaponLevelCell = new ChangelogSection("char-data-" + (isOriginal ? "original" : "new") + "-weapon-levels-" + getPIDForCharacter(boss));
		weaponLevelCell.addClass("char-data-weapon-level-cell");
		List<WeaponType> orderedTypes = new ArrayList<WeaponType>(Arrays.asList(WeaponType.SWORD, WeaponType.LANCE, WeaponType.AXE,
				WeaponType.BOW, WeaponType.FIRE, WeaponType.THUNDER, WeaponType.WIND, WeaponType.STAFF));
		for (WeaponType type : orderedTypes) {
			if (ranks.get(type) != null && ranks.get(type) != WeaponRank.NONE && ranks.get(type) != WeaponRank.UNKNOWN) {
				ChangelogSection weaponLevel = new ChangelogSection(anchor + "-" + (isOriginal ? "original" : "new") + "-" + type.toString());
				weaponLevel.addClass("char-data-weapon-level-entry");
				Base64Asset weaponAsset = new Base64Asset("weapon-icon-" + type.toString(), FE9Base64.weaponTypeBase64Prefix + FE9Base64.base64StringForWeaponType(type), 
						23, 23);
				weaponLevel.addElement(new ChangelogAsset(anchor + "-" + (isOriginal ? "original" : "new") + "-" + type.toString() + "-image", weaponAsset));
				weaponLevel.addElement(new ChangelogText(anchor + "-" + (isOriginal ? "original" : "new") + "-" + type.toString() + "-text", Style.NONE, ranks.get(type).toString()));
				weaponLevelCell.addElement(weaponLevel);
			}
		}
		if (isOriginal) { characterDataTable.addRow(new String[] {"Weapon Levels", "", ""}); }
		characterDataTable.setElement(row, (isOriginal ? 1 : 2), weaponLevelCell);
		row++;
		
		String sid1 = getSID1ForCharacter(boss);
		String sid2 = getSID2ForCharacter(boss);
		String sid3 = getSID3ForCharacter(boss);
		if (isOriginal) {
			characterDataTable.addRow(new String[] {"Skill 1", "", ""});
			characterDataTable.addRow(new String[] {"Skill 2", "", ""});
			characterDataTable.addRow(new String[] {"Skill 3", "", ""});
		}
		int column = isOriginal ? 1 : 2;
		if (sid1 != null) { characterDataTable.setElement(row, column, createSkillSectionWithSID(sid1, skillData, textData, isOriginal, anchor, 1)); } 
		else { characterDataTable.setContents(row, column, "None"); }
		row++;
		if (sid2 != null) { characterDataTable.setElement(row, column, createSkillSectionWithSID(sid2, skillData, textData, isOriginal, anchor, 2)); } 
		else { characterDataTable.setContents(row, column, "None"); }
		row++;
		if (sid3 != null) { characterDataTable.setElement(row, column, createSkillSectionWithSID(sid3, skillData, textData, isOriginal, anchor, 3)); } 
		else { characterDataTable.setContents(row, column, "None"); }
		row++;
		
		String pid = getPIDForCharacter(boss);
		List<FE9ChapterUnit> bossUnits = new ArrayList<FE9ChapterUnit>();
		for (FE9Data.Chapter chapter : FE9Data.Chapter.values()) {
			for (FE9ChapterArmy army : chapterData.armiesForChapter(chapter)) {
				FE9ChapterUnit bossUnit = army.getUnitForPID(pid);
				if (bossUnit != null) {
					bossUnits.add(bossUnit);
				}
			}
		}
		
		// Find the lowest offsets out of all of the boss units for each stat area.
		// We're going to use that as the baseline. All additional adjustments
		// will be higher than this value.
		int normalizedHPAdjustment = bossUnits.isEmpty() ? 0 : 255;
		int normalizedSTRAdjustment = bossUnits.isEmpty() ? 0 : 255;
		int normalizedMAGAdjustment = bossUnits.isEmpty() ? 0 : 255;
		int normalizedSKLAdjustment = bossUnits.isEmpty() ? 0 : 255;
		int normalizedSPDAdjustment = bossUnits.isEmpty() ? 0 : 255;
		int normalizedLCKAdjustment = bossUnits.isEmpty() ? 0 : 255;
		int normalizedDEFAdjustment = bossUnits.isEmpty() ? 0 : 255;
		int normalizedRESAdjustment = bossUnits.isEmpty() ? 0 : 255;
		
		for (FE9ChapterUnit bossUnit : bossUnits) {
			normalizedHPAdjustment = Math.min(normalizedHPAdjustment, bossUnit.getHPAdjustment());
			normalizedSTRAdjustment = Math.min(normalizedSTRAdjustment, bossUnit.getSTRAdjustment());
			normalizedMAGAdjustment = Math.min(normalizedMAGAdjustment, bossUnit.getMAGAdjustment());
			normalizedSKLAdjustment = Math.min(normalizedSKLAdjustment, bossUnit.getSKLAdjustment());
			normalizedSPDAdjustment = Math.min(normalizedSPDAdjustment, bossUnit.getSPDAdjustment());
			normalizedLCKAdjustment = Math.min(normalizedLCKAdjustment, bossUnit.getLCKAdjustment());
			normalizedDEFAdjustment = Math.min(normalizedDEFAdjustment, bossUnit.getDEFAdjustment());
			normalizedRESAdjustment = Math.min(normalizedRESAdjustment, bossUnit.getRESAdjustment());
		}
		
		if (isOriginal) {
			characterDataTable.addRow(new String[] {"HP Growth", boss.getHPGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"STR Growth", boss.getSTRGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"MAG Growth", boss.getMAGGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"SKL Growth", boss.getSKLGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"SPD Growth", boss.getSPDGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"LCK Growth", boss.getLCKGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"DEF Growth", boss.getDEFGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"RES Growth", boss.getRESGrowth() + "%", ""});
			
			characterDataTable.addRow(new String[] {"Base HP", charClass.getBaseHP() + " + " + boss.getBaseHP() + " + " + normalizedHPAdjustment + " = " + (charClass.getBaseHP() + boss.getBaseHP() + normalizedHPAdjustment), ""});
			characterDataTable.addRow(new String[] {"Base STR", charClass.getBaseSTR() + " + " + boss.getBaseSTR() + " + " + normalizedSTRAdjustment + " = " + (charClass.getBaseSTR() + boss.getBaseSTR() + normalizedSTRAdjustment), ""});
			characterDataTable.addRow(new String[] {"Base MAG", charClass.getBaseMAG() + " + " + boss.getBaseMAG() + " + " + normalizedMAGAdjustment + " = " + (charClass.getBaseMAG() + boss.getBaseMAG() + normalizedMAGAdjustment), ""});
			characterDataTable.addRow(new String[] {"Base SKL", charClass.getBaseSKL() + " + " + boss.getBaseSKL() + " + " + normalizedSKLAdjustment + " = " + (charClass.getBaseSKL() + boss.getBaseSKL() + normalizedSKLAdjustment), ""});
			characterDataTable.addRow(new String[] {"Base SPD", charClass.getBaseSPD() + " + " + boss.getBaseSPD() + " + " + normalizedSPDAdjustment + " = " + (charClass.getBaseSPD() + boss.getBaseSPD() + normalizedSPDAdjustment), ""});
			characterDataTable.addRow(new String[] {"Base LCK", charClass.getBaseLCK() + " + " + boss.getBaseLCK() + " + " + normalizedLCKAdjustment + " = " + (charClass.getBaseLCK() + boss.getBaseLCK() + normalizedLCKAdjustment), ""});
			characterDataTable.addRow(new String[] {"Base DEF", charClass.getBaseDEF() + " + " + boss.getBaseDEF() + " + " + normalizedDEFAdjustment + " = " + (charClass.getBaseDEF() + boss.getBaseDEF() + normalizedDEFAdjustment), ""});
			characterDataTable.addRow(new String[] {"Base RES", charClass.getBaseRES() + " + " + boss.getBaseRES() + " + " + normalizedRESAdjustment + " = " + (charClass.getBaseRES() + boss.getBaseRES() + normalizedRESAdjustment), ""});
			
			characterDataTable.addRow(new String[] {"Unpromoted AID", getUnpromotedAIDForCharacter(boss), ""});
			characterDataTable.addRow(new String[] {"Promoted AID", getPromotedAIDForCharacter(boss), ""});
		} else {
			characterDataTable.setContents(row++, 2, boss.getHPGrowth() + "%");
			characterDataTable.setContents(row++, 2, boss.getSTRGrowth() + "%");
			characterDataTable.setContents(row++, 2, boss.getMAGGrowth() + "%");
			characterDataTable.setContents(row++, 2, boss.getSKLGrowth() + "%");
			characterDataTable.setContents(row++, 2, boss.getSPDGrowth() + "%");
			characterDataTable.setContents(row++, 2, boss.getLCKGrowth() + "%");
			characterDataTable.setContents(row++, 2, boss.getDEFGrowth() + "%");
			characterDataTable.setContents(row++, 2, boss.getRESGrowth() + "%");
			
			characterDataTable.setContents(row++, 2, charClass.getBaseHP() + " + " + boss.getBaseHP() + " + " + normalizedHPAdjustment + " = " + (charClass.getBaseHP() + boss.getBaseHP() + normalizedHPAdjustment));
			characterDataTable.setContents(row++, 2, charClass.getBaseSTR() + " + " + boss.getBaseSTR() + " + " + normalizedSTRAdjustment + " = " + (charClass.getBaseSTR() + boss.getBaseSTR() + normalizedSTRAdjustment));
			characterDataTable.setContents(row++, 2, charClass.getBaseMAG() + " + " + boss.getBaseMAG() + " + " + normalizedMAGAdjustment + " = " + (charClass.getBaseMAG() + boss.getBaseMAG() + normalizedMAGAdjustment));
			characterDataTable.setContents(row++, 2, charClass.getBaseSKL() + " + " + boss.getBaseSKL() + " + " + normalizedSKLAdjustment + " = " + (charClass.getBaseSKL() + boss.getBaseSKL() + normalizedSKLAdjustment));
			characterDataTable.setContents(row++, 2, charClass.getBaseSPD() + " + " + boss.getBaseSPD() + " + " + normalizedSPDAdjustment + " = " + (charClass.getBaseSPD() + boss.getBaseSPD() + normalizedSPDAdjustment));
			characterDataTable.setContents(row++, 2, charClass.getBaseLCK() + " + " + boss.getBaseLCK() + " + " + normalizedLCKAdjustment + " = " + (charClass.getBaseLCK() + boss.getBaseLCK() + normalizedLCKAdjustment));
			characterDataTable.setContents(row++, 2, charClass.getBaseDEF() + " + " + boss.getBaseDEF() + " + " + normalizedDEFAdjustment + " = " + (charClass.getBaseDEF() + boss.getBaseDEF() + normalizedDEFAdjustment));
			characterDataTable.setContents(row++, 2, charClass.getBaseRES() + " + " + boss.getBaseRES() + " + " + normalizedRESAdjustment + " = " + (charClass.getBaseRES() + boss.getBaseRES() + normalizedRESAdjustment));
			
			characterDataTable.setContents(row++, 2, getUnpromotedAIDForCharacter(boss));
			characterDataTable.setContents(row++, 2, getPromotedAIDForCharacter(boss));
		}
		
		if (isOriginal) {
			section.addElement(characterDataTable);
			parentSection.addElement(section);
		}
	}
	
	private void createCharacterSection(FE9Character character, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, 
			FE9CommonTextLoader textData, FE9ItemDataLoader itemData, ChangelogTOC toc, ChangelogSection parentSection,
			boolean isOriginal) {
		String characterName = textData.textStringForIdentifier(getMPIDForCharacter(character));
		String anchor = "char-data-" + getPIDForCharacter(character);
		ChangelogTable characterDataTable;
		ChangelogSection section;
		if (isOriginal) {
			section = new ChangelogSection(anchor + "-section");
			section.addClass("character-data-section");
			toc.addAnchorWithTitle(anchor, characterName);
		
			ChangelogHeader titleHeader = new ChangelogHeader(HeaderLevel.HEADING_3, characterName, anchor);
			titleHeader.addClass("char-data-title");
			section.addElement(titleHeader);
		
			characterDataTable = new ChangelogTable(3, new String[] {"", "Old Value", "New Value"}, anchor + "-data-table");
			characterDataTable.addClass("character-data-table");
			characterDataTable.addRow(new String[] {"PID", getPIDForCharacter(character), ""});
			characterDataTable.addRow(new String[] {"Name", characterName, ""});
		} else {
			section = (ChangelogSection)parentSection.getChildWithIdentifier(anchor + "-section");
			characterDataTable = (ChangelogTable)section.getChildWithIdentifier(anchor + "-data-table");
			characterDataTable.setContents(0, 2, getPIDForCharacter(character));
			characterDataTable.setContents(1, 2, characterName);
		}
		
		String jid = getJIDForCharacter(character);
		FE9Class charClass = classData.classWithID(jid);
		String className = textData.textStringForIdentifier(classData.getMJIDForClass(charClass));
		if (isOriginal) {
			characterDataTable.addRow(new String[] {"Class", className + " (" + jid + ")", ""});
		} else {
			characterDataTable.setContents(2, 2, className + " (" + jid + ")");
		}
		
		int row = 3;
		
		FE9Data.Affinity affinity = getAffinityForCharacter(character);
		if (affinity != null) {
			if (isOriginal) { characterDataTable.addRow(new String[] {"Affinity", "", ""}); }
			ChangelogSection affinityCell = new ChangelogSection("char-data-" + (isOriginal ? "original" : "new") + "-affinity-" + getPIDForCharacter(character));
			Base64Asset affinityAsset = new Base64Asset("affinity-" + affinity.toString(), 
					FE9Base64.affinityBase64Prefix + FE9Base64.base64StringForAffinity(affinity), 
					24, 24);
			affinityCell.addElement(new ChangelogAsset(anchor + "-affinity-image", affinityAsset));
			ChangelogText affinityText = new ChangelogText(anchor + "-affinity-text", Style.NONE, affinity.toString() + " (" + affinity.getInternalID() + ")");
			affinityText.addClass("char-data-affinity-text");
			affinityCell.addElement(affinityText);
			affinityCell.addClass("char-data-affinity-cell");
			characterDataTable.setElement(row, isOriginal ? 1 : 2, affinityCell);
			row++;
		}
		
		String weaponLevelString = getWeaponLevelStringForCharacter(character);
		Map<WeaponType, WeaponRank> ranks = itemData.weaponLevelsForWeaponString(weaponLevelString);
		ChangelogSection weaponLevelCell = new ChangelogSection("char-data-" + (isOriginal ? "original" : "new") + "-weapon-levels-" + getPIDForCharacter(character));
		weaponLevelCell.addClass("char-data-weapon-level-cell");
		List<WeaponType> orderedTypes = new ArrayList<WeaponType>(Arrays.asList(WeaponType.SWORD, WeaponType.LANCE, WeaponType.AXE,
				WeaponType.BOW, WeaponType.FIRE, WeaponType.THUNDER, WeaponType.WIND, WeaponType.STAFF));
		for (WeaponType type : orderedTypes) {
			if (ranks.get(type) != null && ranks.get(type) != WeaponRank.NONE && ranks.get(type) != WeaponRank.UNKNOWN) {
				ChangelogSection weaponLevel = new ChangelogSection(anchor + "-" + (isOriginal ? "original" : "new") + "-" + type.toString());
				weaponLevel.addClass("char-data-weapon-level-entry");
				Base64Asset weaponAsset = new Base64Asset("weapon-icon-" + type.toString(), FE9Base64.weaponTypeBase64Prefix + FE9Base64.base64StringForWeaponType(type), 
						23, 23);
				weaponLevel.addElement(new ChangelogAsset(anchor + "-" + (isOriginal ? "original" : "new") + "-" + type.toString() + "-image", weaponAsset));
				weaponLevel.addElement(new ChangelogText(anchor + "-" + (isOriginal ? "original" : "new") + "-" + type.toString() + "-text", Style.NONE, ranks.get(type).toString()));
				weaponLevelCell.addElement(weaponLevel);
			}
		}
		if (isOriginal) { characterDataTable.addRow(new String[] {"Weapon Levels", "", ""}); }
		characterDataTable.setElement(row, (isOriginal ? 1 : 2), weaponLevelCell);
		row++;
		
		String sid1 = getSID1ForCharacter(character);
		String sid2 = getSID2ForCharacter(character);
		String sid3 = getSID3ForCharacter(character);
		if (isOriginal) {
			characterDataTable.addRow(new String[] {"Skill 1", "", ""});
			characterDataTable.addRow(new String[] {"Skill 2", "", ""});
			characterDataTable.addRow(new String[] {"Skill 3", "", ""});
		}
		int column = isOriginal ? 1 : 2;
		if (sid1 != null) { characterDataTable.setElement(row, column, createSkillSectionWithSID(sid1, skillData, textData, isOriginal, anchor, 1)); } 
		else { characterDataTable.setContents(row, column, "None"); }
		row++;
		if (sid2 != null) { characterDataTable.setElement(row, column, createSkillSectionWithSID(sid2, skillData, textData, isOriginal, anchor, 2)); } 
		else { characterDataTable.setContents(row, column, "None"); }
		row++;
		if (sid3 != null) { characterDataTable.setElement(row, column, createSkillSectionWithSID(sid3, skillData, textData, isOriginal, anchor, 3)); } 
		else { characterDataTable.setContents(row, column, "None"); }
		row++;
		
		if (isOriginal) {
			characterDataTable.addRow(new String[] {"HP Growth", character.getHPGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"STR Growth", character.getSTRGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"MAG Growth", character.getMAGGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"SKL Growth", character.getSKLGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"SPD Growth", character.getSPDGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"LCK Growth", character.getLCKGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"DEF Growth", character.getDEFGrowth() + "%", ""});
			characterDataTable.addRow(new String[] {"RES Growth", character.getRESGrowth() + "%", ""});
			
			characterDataTable.addRow(new String[] {"Base HP", charClass.getBaseHP() + " + " + character.getBaseHP() + " = " + (charClass.getBaseHP() + character.getBaseHP()), ""});
			characterDataTable.addRow(new String[] {"Base STR", charClass.getBaseSTR() + " + " + character.getBaseSTR() + " = " + (charClass.getBaseSTR() + character.getBaseSTR()), ""});
			characterDataTable.addRow(new String[] {"Base MAG", charClass.getBaseMAG() + " + " + character.getBaseMAG() + " = " + (charClass.getBaseMAG() + character.getBaseMAG()), ""});
			characterDataTable.addRow(new String[] {"Base SKL", charClass.getBaseSKL() + " + " + character.getBaseSKL() + " = " + (charClass.getBaseSKL() + character.getBaseSKL()), ""});
			characterDataTable.addRow(new String[] {"Base SPD", charClass.getBaseSPD() + " + " + character.getBaseSPD() + " = " + (charClass.getBaseSPD() + character.getBaseSPD()), ""});
			characterDataTable.addRow(new String[] {"Base LCK", charClass.getBaseLCK() + " + " + character.getBaseLCK() + " = " + (charClass.getBaseLCK() + character.getBaseLCK()), ""});
			characterDataTable.addRow(new String[] {"Base DEF", charClass.getBaseDEF() + " + " + character.getBaseDEF() + " = " + (charClass.getBaseDEF() + character.getBaseDEF()), ""});
			characterDataTable.addRow(new String[] {"Base RES", charClass.getBaseRES() + " + " + character.getBaseRES() + " = " + (charClass.getBaseRES() + character.getBaseRES()), ""});
			
			characterDataTable.addRow(new String[] {"Unpromoted AID", getUnpromotedAIDForCharacter(character), ""});
			characterDataTable.addRow(new String[] {"Promoted AID", getPromotedAIDForCharacter(character), ""});
		} else {
			characterDataTable.setContents(row++, 2, character.getHPGrowth() + "%");
			characterDataTable.setContents(row++, 2, character.getSTRGrowth() + "%");
			characterDataTable.setContents(row++, 2, character.getMAGGrowth() + "%");
			characterDataTable.setContents(row++, 2, character.getSKLGrowth() + "%");
			characterDataTable.setContents(row++, 2, character.getSPDGrowth() + "%");
			characterDataTable.setContents(row++, 2, character.getLCKGrowth() + "%");
			characterDataTable.setContents(row++, 2, character.getDEFGrowth() + "%");
			characterDataTable.setContents(row++, 2, character.getRESGrowth() + "%");
			
			characterDataTable.setContents(row++, 2, charClass.getBaseHP() + " + " + character.getBaseHP() + " = " + (charClass.getBaseHP() + character.getBaseHP()));
			characterDataTable.setContents(row++, 2, charClass.getBaseSTR() + " + " + character.getBaseSTR() + " = " + (charClass.getBaseSTR() + character.getBaseSTR()));
			characterDataTable.setContents(row++, 2, charClass.getBaseMAG() + " + " + character.getBaseMAG() + " = " + (charClass.getBaseMAG() + character.getBaseMAG()));
			characterDataTable.setContents(row++, 2, charClass.getBaseSKL() + " + " + character.getBaseSKL() + " = " + (charClass.getBaseSKL() + character.getBaseSKL()));
			characterDataTable.setContents(row++, 2, charClass.getBaseSPD() + " + " + character.getBaseSPD() + " = " + (charClass.getBaseSPD() + character.getBaseSPD()));
			characterDataTable.setContents(row++, 2, charClass.getBaseLCK() + " + " + character.getBaseLCK() + " = " + (charClass.getBaseLCK() + character.getBaseLCK()));
			characterDataTable.setContents(row++, 2, charClass.getBaseDEF() + " + " + character.getBaseDEF() + " = " + (charClass.getBaseDEF() + character.getBaseDEF()));
			characterDataTable.setContents(row++, 2, charClass.getBaseRES() + " + " + character.getBaseRES() + " = " + (charClass.getBaseRES() + character.getBaseRES()));
			
			characterDataTable.setContents(row++, 2, getUnpromotedAIDForCharacter(character));
			characterDataTable.setContents(row++, 2, getPromotedAIDForCharacter(character));
		}
		
		if (isOriginal) {
			section.addElement(characterDataTable);
			parentSection.addElement(section);
		}
	}
	
	private void setupRules(ChangelogBuilder builder) {
		ChangelogStyleRule tocStyle = new ChangelogStyleRule();
		tocStyle.setElementClass("character-section-toc");
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
		tocItemAfter.setOverrideSelectorString(".character-section-toc div:not(:last-child)::after");
		tocItemAfter.addRule("content", "\"|\"");
		tocItemAfter.addRule("margin", "0px 5px");
		builder.addStyle(tocItemAfter);
		
		ChangelogStyleRule pcContainer = new ChangelogStyleRule();
		pcContainer.setElementIdentifier("pc-data-section");
		pcContainer.addRule("display", "flex");
		pcContainer.addRule("flex-direction", "row");
		pcContainer.addRule("flex-wrap", "wrap");
		pcContainer.addRule("justify-content", "center");
		pcContainer.addRule("margin-left", "10px");
		pcContainer.addRule("margin-right", "10px");
		builder.addStyle(pcContainer);
		
		ChangelogStyleRule bossContainer = new ChangelogStyleRule();
		bossContainer.setElementIdentifier("boss-data-section");
		bossContainer.addRule("display", "flex");
		bossContainer.addRule("flex-direction", "row");
		bossContainer.addRule("flex-wrap", "wrap");
		bossContainer.addRule("justify-content", "center");
		bossContainer.addRule("margin-left", "10px");
		bossContainer.addRule("margin-right", "10px");
		builder.addStyle(bossContainer);
		
		ChangelogStyleRule characterSection = new ChangelogStyleRule();
		characterSection.setElementClass("character-data-section");
		characterSection.addRule("margin", "20px");
		characterSection.addRule("flex", "0 0 400px");
		builder.addStyle(characterSection);
		
		ChangelogStyleRule tableStyle = new ChangelogStyleRule();
		tableStyle.setElementClass("character-data-table");
		tableStyle.addRule("width", "100%");
		tableStyle.addRule("border", "1px solid black");
		builder.addStyle(tableStyle);
		
		ChangelogStyleRule titleStyle = new ChangelogStyleRule();
		titleStyle.setElementClass("char-data-title");
		titleStyle.addRule("text-align", "center");
		builder.addStyle(titleStyle);
		
		ChangelogStyleRule columnStyle = new ChangelogStyleRule();
		columnStyle.setElementClass("character-data-table");
		columnStyle.setChildTags(new ArrayList<String>(Arrays.asList("td", "th")));
		columnStyle.addRule("border", "1px solid black");
		columnStyle.addRule("padding", "5px");
		builder.addStyle(columnStyle);
		
		ChangelogStyleRule firstColumnStyle = new ChangelogStyleRule();
		firstColumnStyle.setOverrideSelectorString(".character-data-table td:first-child");
		firstColumnStyle.addRule("width", "20%");
		firstColumnStyle.addRule("text-align", "right");
		builder.addStyle(firstColumnStyle);
		
		ChangelogStyleRule otherColumnStyle = new ChangelogStyleRule();
		otherColumnStyle.setOverrideSelectorString(".character-data-table th:not(:first-child)");
		otherColumnStyle.addRule("width", "40%");
		builder.addStyle(otherColumnStyle);
		
		ChangelogStyleRule affinityStyle = new ChangelogStyleRule();
		affinityStyle.setElementClass("char-data-affinity-cell");
		affinityStyle.addRule("display", "flex");
		affinityStyle.addRule("flex-direction", "row");
		affinityStyle.addRule("align-items", "center");
		builder.addStyle(affinityStyle);
		
		ChangelogStyleRule weaponLevelCellStyle = new ChangelogStyleRule();
		weaponLevelCellStyle.setElementClass("char-data-weapon-level-cell");
		weaponLevelCellStyle.addRule("display", "flex");
		weaponLevelCellStyle.addRule("flex-direction", "row");
		weaponLevelCellStyle.addRule("align-items", "center");
		builder.addStyle(weaponLevelCellStyle);
		
		ChangelogStyleRule weaponLevelEntryStyle = new ChangelogStyleRule();
		weaponLevelEntryStyle.setElementClass("char-data-weapon-level-entry");
		weaponLevelEntryStyle.addRule("margin-right", "15px");
		weaponLevelEntryStyle.addRule("display", "flex");
		weaponLevelEntryStyle.addRule("flex-direction", "row");
		weaponLevelEntryStyle.addRule("align-items", "center");
		builder.addStyle(weaponLevelEntryStyle);
		
		ChangelogStyleRule weaponLevelEntryTextStyle = new ChangelogStyleRule();
		weaponLevelEntryTextStyle.setElementClass("char-data-weapon-level-entry");
		weaponLevelEntryTextStyle.setChildTags(new ArrayList<String>(Arrays.asList("p")));
		weaponLevelEntryTextStyle.addRule("margin-left", "5px");
		weaponLevelEntryTextStyle.addRule("margin-top", "0px");
		weaponLevelEntryTextStyle.addRule("margin-bottom", "0px");
		builder.addStyle(weaponLevelEntryTextStyle);
		
		ChangelogStyleRule affinityTextStyle = new ChangelogStyleRule();
		affinityTextStyle.setElementClass("char-data-affinity-text");
		affinityTextStyle.addRule("margin-left", "10px");
		affinityTextStyle.addRule("margin-top", "0px");
		affinityTextStyle.addRule("margin-bottom", "0px");
		builder.addStyle(affinityTextStyle);
		
		ChangelogStyleRule skillCellStyle = new ChangelogStyleRule();
		skillCellStyle.setElementClass("char-data-skill-cell");
		skillCellStyle.addRule("display", "flex");
		skillCellStyle.addRule("flex-direction", "row");
		skillCellStyle.addRule("align-items", "center");
		builder.addStyle(skillCellStyle);
		
		ChangelogStyleRule skillTextStyle = new ChangelogStyleRule();
		skillTextStyle.setElementClass("char-data-skill-text");
		skillTextStyle.addRule("margin-left", "5px");
		builder.addStyle(skillTextStyle);
	}
	
	private ChangelogSection createSkillSectionWithSID(String sid, FE9SkillDataLoader skillData, FE9CommonTextLoader textData,
			boolean original, String anchor, int index) {
		FE9Skill skill = skillData.getSkillWithSID(sid);
		String skillName = textData.textStringForIdentifier(skillData.getMSID(skill));
		FE9Data.Skill fe9Skill = FE9Data.Skill.withSID(sid);
		
		ChangelogSection skill1Section = new ChangelogSection(anchor + "-" + (original ? "original" : "new") + "-skill-" + index + "-cell");
		skill1Section.addClass("char-data-skill-cell");
		
		String base64 = FE9Base64.base64StringForSkill(fe9Skill);
		if (base64 != null) {
			Base64Asset asset = new Base64Asset("skill-" + sid, FE9Base64.skillBase64Prefix + base64, 32, 32);
			skill1Section.addElement(new ChangelogAsset(anchor + "-" + (original ? "original" : "new") + "-skill-" + index + "-asset", asset));
		}
		
		ChangelogText detail = new ChangelogText(anchor + "-" + (original ? "original" : "new") + "-skill-" + index + "-text", Style.NONE, skillName + " (" + sid + ")");
		detail.addClass("char-data-skill-text");
		skill1Section.addElement(detail);
		
		return skill1Section;
	}
}
