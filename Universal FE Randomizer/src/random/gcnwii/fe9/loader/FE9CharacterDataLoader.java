package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gcnwii.fe9.FE9Base64;
import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponRank;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponType;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.ChangelogBuilder;
import util.recordkeeper.ChangelogHeader;
import util.recordkeeper.ChangelogHeader.HeaderLevel;
import util.recordkeeper.ChangelogImage;
import util.recordkeeper.ChangelogSection;
import util.recordkeeper.ChangelogStyleRule;
import util.recordkeeper.ChangelogTOC;
import util.recordkeeper.ChangelogTable;
import util.recordkeeper.ChangelogText;
import util.recordkeeper.ChangelogText.Style;

public class FE9CharacterDataLoader {
	
	List<FE9Character> allCharacters;
	
	List<FE9Character> playableCharacters;
	List<FE9Character> bossCharacters;
	List<FE9Character> minionCharacters;
	
	Map<String, List<FE9Character>> daeinMinionsByJID;
	
	Map<String, Long> knownAddresses;
	Map<Long, String> knownPointers;
	
	Map<String, FE9Character> idLookup;
	
	GCNDataFileHandler fe8databin;
	
	public FE9CharacterDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		allCharacters = new ArrayList<FE9Character>();
		
		playableCharacters = new ArrayList<FE9Character>();
		bossCharacters = new ArrayList<FE9Character>();
		minionCharacters = new ArrayList<FE9Character>();
		
		knownAddresses = new HashMap<String, Long>();
		knownPointers = new HashMap<Long, String>();
		
		daeinMinionsByJID = new HashMap<String, List<FE9Character>>();
		
		idLookup = new HashMap<String, FE9Character>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.CharacterDataFilename);
		assert (handler instanceof GCNDataFileHandler);
		if (handler instanceof GCNDataFileHandler) {
			fe8databin = (GCNDataFileHandler)handler;
		}
		
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
			String affiliation = stringForPointer(character.getAffinityPointer(), handler, null);
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
			knownAddresses.put(affiliation, character.getAffinityPointer());
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
			knownPointers.put(character.getAffinityPointer(), affiliation);
			knownPointers.put(character.getWeaponLevelsPointer(), weaponLevels);
			knownPointers.put(character.getSkill1Pointer(), sid1);
			knownPointers.put(character.getSkill2Pointer(), sid2);
			knownPointers.put(character.getSkill3Pointer(), sid3);
			knownPointers.put(character.getUnpromotedAnimationPointer(), aid1);
			knownPointers.put(character.getPromotedAnimationPointer(), aid2);
			
			FE9Data.Character fe9Char = FE9Data.Character.withPID(pid);
			if (fe9Char != null && fe9Char.isPlayable()) { playableCharacters.add(character); }
			
			idLookup.put(pid, character);
			
			if (sid1.equals(FE9Data.Skill.BOSS.getSID()) || sid2.equals(FE9Data.Skill.BOSS.getSID()) || sid3.equals(FE9Data.Skill.BOSS.getSID())) {
				bossCharacters.add(character);
			}
			
			if ((pid.contains("_DAYNE") || pid.contains("_ZAKO") || pid.contains("_BANDIT")) && !pid.contains("_EV")) {
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
		return ((pid.contains("_DAYNE") || pid.contains("_ZAKO") || pid.contains("_BANDIT")) && !pid.contains("_EV"));
	}
	
	public boolean isRestrictedMinionCharacterPID(String pid) {
		// These characters are specially referenced by chapter scripts, so we need to be careful about changing them.
		if (isMinionCharacter(characterWithID(pid))) {
			if (pid.contains("_DAYNE")) {
				if (pid.matches("PID_DAYNE_[A-Z]{3}_[0-9]+")) { return true; }
				return false;
			} else {
				return false;
			}
		}
		
		return false;
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
	
	public String pointerLookup(long pointer) {
		if (fe8databin != null) {
			return fe8databin.stringForPointer(pointer);
		}
		return knownPointers.get(pointer);
	}
	
	public Long addressLookup(String value) {
		if (fe8databin != null) {
			return fe8databin.pointerForString(value);
		}
		return knownAddresses.get(value);
	}
	
	public boolean isDaeinCharacter(FE9Character character) {
		String pid = getPIDForCharacter(character);
		if (pid == null) { return false; }
		return pid.contains("_DAYNE");
	}
	
	public List<FE9Character> getDaeinCharactersForJID(String jid) {
		return daeinMinionsByJID.get(jid);
	}
	
	public String getPIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getCharacterIDPointer() == 0) { return null; }
		
		if (fe8databin != null) {
			return fe8databin.stringForPointer(character.getCharacterIDPointer());
		}
		return pointerLookup(character.getCharacterIDPointer());
	}
	
	public String getMPIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getCharacterNamePointer() == 0) { return null; }
		
		return fe8databin.stringForPointer(character.getCharacterNamePointer());
	}
	
	public String getJIDForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getClassPointer() == 0) { return null; }
		
		if (fe8databin != null) {
			return fe8databin.stringForPointer(character.getClassPointer());
		}
		return pointerLookup(character.getClassPointer());
	}
	
	public void setJIDForCharacter(FE9Character character, String jid) {
		if (fe8databin != null) {
			Long jidAddress = fe8databin.pointerForString(jid);
			if (jidAddress == null) {
				fe8databin.addString(jid);
				fe8databin.commitAdditions();
				jidAddress = fe8databin.pointerForString(jid);
			}
			character.setClassPointer(jidAddress);
		} else {
			Long jidAddress = addressLookup(jid);
			assert(jidAddress != null);
			if (jidAddress != null) {
				character.setClassPointer(jidAddress);
			}
		}
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
		Long aidAddress = fe8databin.pointerForString(aid);
		if (aidAddress == null) {
			fe8databin.addString(aid);
			fe8databin.commitAdditions();
			aidAddress = fe8databin.pointerForString(aid);
		}
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
		Long aidAddress = fe8databin.pointerForString(aid);
		if (aidAddress == null) {
			fe8databin.addString(aid);
			fe8databin.commitAdditions();
			aidAddress = fe8databin.pointerForString(aid);
		}
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
		fe8databin.commitAdditions();
		character.setWeaponLevelsPointer(fe8databin.pointerForString(weaponLevelString));
	}
	
	public String getSID1ForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getSkill1Pointer() == 0) { return null; }
		
		if (fe8databin != null) {
			return fe8databin.stringForPointer(character.getSkill1Pointer());
		}
		return pointerLookup(character.getSkill1Pointer());
	}
	
	public void setSID1ForCharacter(FE9Character character, String sid) {
		if (character == null) { return; }
		if (sid == null) {
			character.setSkill1Pointer(0);
			return;
		}
		
		if (fe8databin != null) {
			Long sidAddress = fe8databin.pointerForString(sid);
			fe8databin.addPointerOffset(character.getAddressOffset() + FE9Character.CharacterSkill1Offset - 0x20);
			fe8databin.commitAdditions();
			if (sidAddress == null) {
				fe8databin.addString(sid);
				fe8databin.commitAdditions();
				sidAddress = fe8databin.pointerForString(sid);
			}
			character.setSkill1Pointer(sidAddress);
		} else {
			Long sidAddress = addressLookup(sid);
			assert(sidAddress != null);
			if (sidAddress != null) {
				character.setSkill1Pointer(sidAddress);
			}
		}
	}
	
	public String getSID2ForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getSkill2Pointer() == 0) { return null; }
		
		if (fe8databin != null) {
			return fe8databin.stringForPointer(character.getSkill2Pointer());
		}
		return pointerLookup(character.getSkill2Pointer());
	}
	
	public void setSID2ForCharacter(FE9Character character, String sid) {
		if (character == null) { return; }
		if (sid == null) {
			character.setSkill2Pointer(0);
			return;
		}
		
		if (fe8databin != null) {
			Long sidAddress = fe8databin.pointerForString(sid);
			fe8databin.addPointerOffset(character.getAddressOffset() + FE9Character.CharacterSkill2Offset - 0x20);
			fe8databin.commitAdditions();
			if (sidAddress == null) {
				fe8databin.addString(sid);
				fe8databin.commitAdditions();
				sidAddress = fe8databin.pointerForString(sid);
			}
			character.setSkill2Pointer(sidAddress);
		} else {
			Long sidAddress = addressLookup(sid);
			assert(sidAddress != null);
			if (sidAddress != null) {
				character.setSkill2Pointer(sidAddress);
			}
		}
	}
	
	public String getSID3ForCharacter(FE9Character character) {
		if (character == null) { return null; }
		if (character.getSkill3Pointer() == 0) { return null; }
		
		if (fe8databin != null) {
			return fe8databin.stringForPointer(character.getSkill3Pointer());
		}
		return pointerLookup(character.getSkill3Pointer());
	}
	
	public void setSID3ForCharacter(FE9Character character, String sid) {
		if (character == null) { return; }
		if (sid == null) {
			character.setSkill3Pointer(0);
			return;
		}
		
		if (fe8databin != null) {
			Long sidAddress = fe8databin.pointerForString(sid);
			fe8databin.addPointerOffset(character.getAddressOffset() + FE9Character.CharacterSkill3Offset - 0x20);
			fe8databin.commitAdditions();
			if (sidAddress == null) {
				fe8databin.addString(sid);
				fe8databin.commitAdditions();
				sidAddress = fe8databin.pointerForString(sid);
			}
			character.setSkill3Pointer(sidAddress);
		} else {
			Long sidAddress = addressLookup(sid);
			assert(sidAddress != null);
			if (sidAddress != null) {
				character.setSkill3Pointer(sidAddress);
			}
		}
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

	public void recordOriginalCharacterData(ChangelogBuilder builder, ChangelogSection characterDataSection, 
			FE9CommonTextLoader textData, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, FE9ItemDataLoader itemData) {
		ChangelogTOC playableTOC = new ChangelogTOC("playable-character-data");
		playableTOC.addClass("character-section-toc");
		characterDataSection.addElement(new ChangelogHeader(HeaderLevel.HEADING_2, "Character Data", "character-data-header"));
		characterDataSection.addElement(playableTOC);
		
		for (FE9Character character : playableCharacters) {
			String characterName = textData.textStringForIdentifier(getMPIDForCharacter(character));
			String anchor = "pc-data-" + getPIDForCharacter(character);
			playableTOC.addAnchorWithTitle(anchor, characterName);
			
			ChangelogHeader titleHeader = new ChangelogHeader(HeaderLevel.HEADING_3, characterName, anchor);
			titleHeader.addClass("pc-data-title");
			characterDataSection.addElement(titleHeader);
			
			ChangelogTable characterDataTable = new ChangelogTable(3, new String[] {"", "Old Value", "New Value"}, anchor + "-data-table");
			characterDataTable.addClass("character-data-table");
			characterDataTable.addRow(new String[] {"PID", getPIDForCharacter(character), ""});
			characterDataTable.addRow(new String[] {"Name", characterName, ""});
			
			String jid = getJIDForCharacter(character);
			FE9Class charClass = classData.classWithID(jid);
			String className = textData.textStringForIdentifier(classData.getMJIDForClass(charClass));
			characterDataTable.addRow(new String[] {"Class", className + " (" + jid + ")", ""});
			
			FE9Data.Affinity affinity = getAffinityForCharacter(character);
			characterDataTable.addRow(new String[] {"Affinity", "", ""});
			ChangelogSection affinityCell = new ChangelogSection("pc-data-original-affinity-" + getPIDForCharacter(character));
			affinityCell.addElement(new ChangelogImage(anchor + "-affinity-image", 
					FE9Base64.affinityBase64Prefix + FE9Base64.base64StringForAffinity(affinity), 
					24, 24));
			ChangelogText affinityText = new ChangelogText(anchor + "-affinity-text", Style.NONE, affinity.toString() + " (" + affinity.getInternalID() + ")");
			affinityText.addClass("pc-data-affinity-text");
			affinityCell.addElement(affinityText);
			affinityCell.addClass("pc-data-affinity-cell");
			characterDataTable.setElement(3, 1, affinityCell);
			
			String weaponLevelString = getWeaponLevelStringForCharacter(character);
			Map<WeaponType, WeaponRank> ranks = itemData.weaponLevelsForWeaponString(weaponLevelString);
			ChangelogSection weaponLevelCell = new ChangelogSection("pc-data-original-weapon-levels-" + getPIDForCharacter(character));
			weaponLevelCell.addClass("pc-data-weapon-level-cell");
			List<WeaponType> orderedTypes = new ArrayList<WeaponType>(Arrays.asList(WeaponType.SWORD, WeaponType.LANCE, WeaponType.AXE,
					WeaponType.BOW, WeaponType.FIRE, WeaponType.THUNDER, WeaponType.WIND, WeaponType.STAFF));
			for (WeaponType type : orderedTypes) {
				if (ranks.get(type) != null && ranks.get(type) != WeaponRank.NONE && ranks.get(type) != WeaponRank.UNKNOWN) {
					ChangelogSection weaponLevel = new ChangelogSection(anchor + "-original-" + type.toString());
					weaponLevel.addClass("pc-data-weapon-level-entry");
					weaponLevel.addElement(new ChangelogImage(anchor + "-original-" + type.toString() + "-image", 
							FE9Base64.weaponTypeBase64Prefix + FE9Base64.base64StringForWeaponType(type), 
							23, 23));
					weaponLevel.addElement(new ChangelogText(anchor + "-original-" + type.toString() + "-text", Style.NONE, ranks.get(type).toString()));
					weaponLevelCell.addElement(weaponLevel);
				}
			}
			characterDataTable.addRow(new String[] {"Weapon Levels", "", ""});
			characterDataTable.setElement(4, 1, weaponLevelCell);
			
			characterDataSection.addElement(characterDataTable);
		}
		
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
		
		ChangelogStyleRule affinityStyle = new ChangelogStyleRule();
		affinityStyle.setElementClass("pc-data-affinity-cell");
		affinityStyle.addRule("display", "flex");
		affinityStyle.addRule("flex-direction", "row");
		affinityStyle.addRule("align-items", "center");
		affinityStyle.addRule("margin-left", "5px");
		builder.addStyle(affinityStyle);
		
		ChangelogStyleRule weaponLevelCellStyle = new ChangelogStyleRule();
		weaponLevelCellStyle.setElementClass("pc-data-weapon-level-cell");
		weaponLevelCellStyle.addRule("display", "flex");
		weaponLevelCellStyle.addRule("flex-direction", "row");
		weaponLevelCellStyle.addRule("align-items", "center");
		weaponLevelCellStyle.addRule("margin-left", "5px");
		builder.addStyle(weaponLevelCellStyle);
		
		ChangelogStyleRule weaponLevelEntryStyle = new ChangelogStyleRule();
		weaponLevelEntryStyle.setElementClass("pc-data-weapon-level-entry");
		weaponLevelEntryStyle.addRule("margin-right", "15px");
		weaponLevelEntryStyle.addRule("display", "flex");
		weaponLevelEntryStyle.addRule("flex-direction", "row");
		weaponLevelEntryStyle.addRule("align-items", "center");
		builder.addStyle(weaponLevelEntryStyle);
		
		ChangelogStyleRule weaponLevelEntryTextStyle = new ChangelogStyleRule();
		weaponLevelEntryTextStyle.setElementClass("pc-data-weapon-level-entry");
		weaponLevelEntryTextStyle.setChildTags(new ArrayList<String>(Arrays.asList("p")));
		weaponLevelEntryTextStyle.addRule("margin-left", "5px");
		weaponLevelEntryTextStyle.addRule("margin-top", "0px");
		weaponLevelEntryTextStyle.addRule("margin-bottom", "0px");
		builder.addStyle(weaponLevelEntryTextStyle);
		
		ChangelogStyleRule affinityTextStyle = new ChangelogStyleRule();
		affinityTextStyle.setElementClass("pc-data-affinity-text");
		affinityTextStyle.addRule("margin-left", "10px");
		affinityTextStyle.addRule("margin-top", "0px");
		affinityTextStyle.addRule("margin-bottom", "0px");
		builder.addStyle(affinityTextStyle);
		
		ChangelogStyleRule tableStyle = new ChangelogStyleRule();
		tableStyle.setElementClass("character-data-table");
		tableStyle.addRule("width", "75%");
		tableStyle.addRule("margin-left", "auto");
		tableStyle.addRule("margin-right", "auto");
		tableStyle.addRule("border", "1px solid black");
		builder.addStyle(tableStyle);
		
		ChangelogStyleRule titleStyle = new ChangelogStyleRule();
		titleStyle.setElementClass("pc-data-title");
		titleStyle.addRule("text-align", "center");
		builder.addStyle(titleStyle);
		
		ChangelogStyleRule columnStyle = new ChangelogStyleRule();
		columnStyle.setElementClass("character-data-table");
		columnStyle.setChildTags(new ArrayList<String>(Arrays.asList("td", "th")));
		columnStyle.addRule("border", "1px solid black");
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
	}
	
	public void recordUpdatedCharacterData(ChangelogSection characterDataSection,
			FE9CommonTextLoader textData, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, FE9ItemDataLoader itemData) {
		for (FE9Character character : playableCharacters) {
			String characterName = textData.textStringForIdentifier(getMPIDForCharacter(character));
			String anchor = "pc-data-" + getPIDForCharacter(character);
			
			ChangelogTable characterDataTable = (ChangelogTable)characterDataSection.getChildWithIdentifier(anchor + "-data-table");
			
			characterDataTable.setContents(0, 2, getPIDForCharacter(character));
			characterDataTable.setContents(1, 2, characterName);
			
			String jid = getJIDForCharacter(character);
			FE9Class charClass = classData.classWithID(jid);
			String className = textData.textStringForIdentifier(classData.getMJIDForClass(charClass));
			
			characterDataTable.setContents(2, 2, className + " (" + jid + ")");
			
			FE9Data.Affinity affinity = getAffinityForCharacter(character);
			ChangelogSection affinityCell = new ChangelogSection("pc-data-new-affinity-" + getPIDForCharacter(character));
			affinityCell.addElement(new ChangelogImage(anchor + "-affinity-image", 
					FE9Base64.affinityBase64Prefix + FE9Base64.base64StringForAffinity(affinity), 
					24, 24));
			ChangelogText affinityText = new ChangelogText(anchor + "-affinity-text", Style.NONE, affinity.toString() + " (" + affinity.getInternalID() + ")");
			affinityText.addClass("pc-data-affinity-text");
			affinityCell.addElement(affinityText);
			affinityCell.addClass("pc-data-affinity-cell");
			characterDataTable.setElement(3, 2, affinityCell);
			
			String weaponLevelString = getWeaponLevelStringForCharacter(character);
			Map<WeaponType, WeaponRank> ranks = itemData.weaponLevelsForWeaponString(weaponLevelString);
			ChangelogSection weaponLevelCell = new ChangelogSection("pc-data-new-weapon-levels-" + getPIDForCharacter(character));
			weaponLevelCell.addClass("pc-data-weapon-level-cell");
			List<WeaponType> orderedTypes = new ArrayList<WeaponType>(Arrays.asList(WeaponType.SWORD, WeaponType.LANCE, WeaponType.AXE,
					WeaponType.BOW, WeaponType.FIRE, WeaponType.THUNDER, WeaponType.WIND, WeaponType.STAFF));
			for (WeaponType type : orderedTypes) {
				if (ranks.get(type) != null && ranks.get(type) != WeaponRank.NONE && ranks.get(type) != WeaponRank.UNKNOWN) {
					ChangelogSection weaponLevel = new ChangelogSection(anchor + "-new-" + type.toString());
					weaponLevel.addClass("pc-data-weapon-level-entry");
					weaponLevel.addElement(new ChangelogImage(anchor + "-new-" + type.toString() + "-image", 
							FE9Base64.weaponTypeBase64Prefix + FE9Base64.base64StringForWeaponType(type), 
							23, 23));
					weaponLevel.addElement(new ChangelogText(anchor + "-new-" + type.toString() + "-text", Style.NONE, ranks.get(type).toString()));
					weaponLevelCell.addElement(weaponLevel);
				}
			}
			characterDataTable.setElement(4, 2, weaponLevelCell);
		}
	}
}
