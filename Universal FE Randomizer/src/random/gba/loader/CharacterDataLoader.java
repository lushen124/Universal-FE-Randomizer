package random.gba.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedata.gba.GBAFECharacter;
import fedata.gba.GBAFEClass;
import fedata.gba.fe6.FE6Character;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe6.FE6Data.Character;
import fedata.gba.fe7.FE7Character;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Character;
import fedata.gba.fe8.FE8Data;
import fedata.general.FEBase;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.recordkeeper.RecordKeeper;

public class CharacterDataLoader {
	
	private FEBase.GameType gameType;
	
	private Map<Integer, GBAFECharacter> characterMap = new HashMap<Integer, GBAFECharacter>();
	private Map<Integer, GBAFECharacter> counterMap = new HashMap<Integer, GBAFECharacter>();
	
	public static final String RecordKeeperCategoryKey = "Characters";

	public CharacterDataLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE6:
				long baseAddress = FileReadHelper.readAddress(handler, FE6Data.CharacterTablePointer);
				for (FE6Data.Character character : FE6Data.Character.values()) {
					long offset = baseAddress + (FE6Data.BytesPerCharacter * character.ID);
					byte[] charData = handler.readBytesAtOffset(offset, FE6Data.BytesPerCharacter);
					characterMap.put(character.ID, new FE6Character(charData, offset, character.hasLimitedClasses()));
				}
				Map<Integer, FE6Data.Character> fe6Counters = FE6Data.Character.getCharacterCounters(); 
				for (int characterID : fe6Counters.keySet()) {
					counterMap.put(characterID, characterMap.get(fe6Counters.get(characterID).ID));
				}
				break;
			case FE7:
				baseAddress = FileReadHelper.readAddress(handler, FE7Data.CharacterTablePointer);
				for (FE7Data.Character character : FE7Data.Character.values()) {
					long offset = baseAddress + (FE7Data.BytesPerCharacter * character.ID);
					byte[] charData = handler.readBytesAtOffset(offset, FE7Data.BytesPerCharacter);
					characterMap.put(character.ID, new FE7Character(charData, offset, character.hasLimitedClasses()));
				}
				Map<Integer, FE7Data.Character> fe7Counters = FE7Data.Character.getCharacterCounters(); 
				for (int characterID : fe7Counters.keySet()) {
					counterMap.put(characterID, characterMap.get(fe7Counters.get(characterID).ID));
				}
				break;
			case FE8:
				baseAddress = FileReadHelper.readAddress(handler, FE8Data.CharacterTablePointer);
				for (FE8Data.Character character : FE8Data.Character.values()) {
					long offset = baseAddress + (FE8Data.BytesPerCharacter * character.ID);
					byte[] charData = handler.readBytesAtOffset(offset, FE8Data.BytesPerCharacter);
					characterMap.put(character.ID, new FE8Character(charData, offset, character.hasLimitedClasses()));
				}
				Map<Integer, FE8Data.Character> fe8Counters = FE8Data.Character.getCharacterCounters();
				for (int characterID : fe8Counters.keySet()) {
					counterMap.put(characterID, characterMap.get(fe8Counters.get(characterID).ID));
				}
				break;
			default:
				break;
		}
	}
	
	public GBAFECharacter characterWithID(int characterID) {
		return characterMap.get(characterID);
	}
	
	public GBAFECharacter[] playableCharacters() {
		switch (gameType) {
		case FE6:
				return fe6CharactersFromList(FE6Data.Character.allPlayableCharacters);
			case FE7:
				return fe7CharactersFromList(FE7Data.Character.allPlayableCharacters);
			case FE8:
				return fe8CharactersFromList(FE8Data.Character.allPlayableCharacters);
			default:
				return new GBAFECharacter[] {};
		}
	}
	
	public GBAFECharacter[] bossCharacters() {
		switch (gameType) {
		case FE6:
			return fe6CharactersFromList(FE6Data.Character.allBossCharacters);
		case FE7:
			return fe7CharactersFromList(FE7Data.Character.allBossCharacters);
		case FE8:
			return fe8CharactersFromList(FE8Data.Character.allBossCharacters);
		default:
			return new GBAFECharacter[] {};
		}
	}
	
	public Boolean isPlayableCharacterID(int characterID) {
		switch (gameType) {
		case FE6:
			FE6Data.Character fe6Character = FE6Data.Character.valueOf(characterID);
			if (fe6Character != null) {
				return fe6Character.isPlayableCharacter();	
			} else {
				return false;
			}
		case FE7:
			FE7Data.Character fe7Character = FE7Data.Character.valueOf(characterID);
			if (fe7Character != null) {
				return fe7Character.isPlayableCharacter();	
			} else {
				return false;
			}
		case FE8:
			FE8Data.Character fe8Character = FE8Data.Character.valueOf(characterID);
			if (fe8Character != null) {
				return fe8Character.isPlayableCharacter();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public Boolean isBossCharacterID(int characterID) {
		switch (gameType) {
		case FE6:
			FE6Data.Character fe6Character = FE6Data.Character.valueOf(characterID);
			if (fe6Character != null) {
				return fe6Character.isBoss();
			} else {
				return false;
			}
		case FE7:
			FE7Data.Character fe7Character = FE7Data.Character.valueOf(characterID);
			if (fe7Character != null) {
				return fe7Character.isBoss();
			} else {
				return false;
			}
		case FE8:
			FE8Data.Character fe8Character = FE8Data.Character.valueOf(characterID);
			if (fe8Character != null) {
				return fe8Character.isBoss();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	// Generally used for minions, whose character IDs we don't track, so nulls are probably pointing to minion characters.
	// Those are generally safe to change.
	public Boolean canChangeCharacterID(int characterID) {
		switch (gameType) {
		case FE6:
			FE6Data.Character fe6Character = FE6Data.Character.valueOf(characterID);
			return fe6Character == null ? true : fe6Character.canChange();
		case FE7:
			FE7Data.Character fe7Character = FE7Data.Character.valueOf(characterID);
			return fe7Character == null ? true : fe7Character.canChange();
		case FE8:
			FE8Data.Character fe8Character = FE8Data.Character.valueOf(characterID);
			return fe8Character == null ? true : fe8Character.canChange();
		default:
			return false;
		}
	}
	
	public Boolean isLordCharacterID(int characterID) {
		switch (gameType) {
		case FE6:
			FE6Data.Character fe6Character = FE6Data.Character.valueOf(characterID);
			if (fe6Character != null) {
				return fe6Character.isLord();
			} else {
				return false;
			}
		case FE7:
			FE7Data.Character fe7Character = FE7Data.Character.valueOf(characterID);
			if (fe7Character != null) {
				return fe7Character.isLord();
			} else {
				return false;
			}
		case FE8:
			FE8Data.Character fe8Character = FE8Data.Character.valueOf(characterID);
			if (fe8Character != null) {
				return fe8Character.isLord();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public Boolean isThiefCharacterID(int characterID) {
		switch (gameType) {
		case FE6:
			FE6Data.Character fe6Character = FE6Data.Character.valueOf(characterID);
			if (fe6Character != null) {
				return fe6Character.isThief();
			} else {
				return false;
			}
		case FE7:
			FE7Data.Character fe7Character = FE7Data.Character.valueOf(characterID);
			if (fe7Character != null) {
				return fe7Character.isThief();
			} else {
				return false;
			}
		case FE8:
			FE8Data.Character fe8Character = FE8Data.Character.valueOf(characterID);
			if (fe8Character != null) {
				return fe8Character.isThief();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public int[] validAffinityValues() {
		switch (gameType) {
		case FE6:
			FE6Character.Affinity[] fe6Affinities = FE6Character.Affinity.validAffinities();
			int[] validValues = new int[fe6Affinities.length];
			for (int i = 0; i < fe6Affinities.length; i++) {
				validValues[i] = fe6Affinities[i].value;
			}
			
			return validValues;
		case FE7:
			FE7Character.Affinity[] fe7Affinities = FE7Character.Affinity.validAffinities();
			validValues = new int[fe7Affinities.length];
			for (int i = 0; i < fe7Affinities.length; i++) {
				validValues[i] = fe7Affinities[i].value;
			}
			
			return validValues;
		case FE8:
			FE8Character.Affinity[] fe8Affinities = FE8Character.Affinity.validAffinities();
			validValues = new int[fe8Affinities.length];
			for (int i = 0; i < fe8Affinities.length; i++) {
				validValues[i] = fe8Affinities[i].value;
			}
			
			return validValues;
		default:
			return new int[] {};
		}
	}
	
	public Boolean characterIDRequiresRange(int characterID) {
		switch (gameType) {
		case FE6:
			FE6Data.Character fe6Character = FE6Data.Character.valueOf(characterID);
			if (fe6Character != null) {
				return fe6Character.requiresRange();
			} else {
				return false;
			}
		case FE7:
			FE7Data.Character fe7Character = FE7Data.Character.valueOf(characterID);
			if (fe7Character != null) {
				return fe7Character.requiresRange();
			} else {
				return false;
			}
		case FE8:
			FE8Data.Character fe8Character = FE8Data.Character.valueOf(characterID);
			if (fe8Character != null) {
				return fe8Character.requiresRange();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public Boolean characterIDRequiresMelee(int characterID) {
		switch (gameType) {
		case FE6:
			FE6Data.Character fe6Character = FE6Data.Character.valueOf(characterID);
			if (fe6Character != null) {
				return fe6Character.requiresMelee();
			} else {
				return false;
			}
		case FE7:
			FE7Data.Character fe7Character = FE7Data.Character.valueOf(characterID);
			if (fe7Character != null) {
				return fe7Character.requiresMelee();
			} else {
				return false;
			}
		case FE8:
			FE8Data.Character fe8Character = FE8Data.Character.valueOf(characterID);
			if (fe8Character != null) {
				return fe8Character.requiresMelee();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public GBAFECharacter characterRequiresCounterToCharacter(GBAFECharacter character) {
		return counterMap.get(character.getID());
	}
	
	public GBAFECharacter[] linkedCharactersForCharacter(GBAFECharacter character) {
		switch (gameType) {
		case FE6:
			return fe6CharactersFromList(FE6Data.Character.allLinkedCharactersFor(FE6Data.Character.valueOf(character.getID())));
		case FE7:
			return fe7CharactersFromList(FE7Data.Character.allLinkedCharactersFor(FE7Data.Character.valueOf(character.getID())));
		case FE8:
			return fe8CharactersFromList(FE8Data.Character.allLinkedCharactersFor(FE8Data.Character.valueOf(character.getID())));
		default:
			return new GBAFECharacter[] {character};
		}
	}
	
	public int getCanonicalIDForCharacter(GBAFECharacter character) {
		switch (gameType) {
		case FE6:
			return FE6Data.Character.canonicalIDForCharacterID(character.getID());
		case FE7:
			return FE7Data.Character.canonicalIDForCharacterID(character.getID());
		case FE8:
			return FE8Data.Character.canonicalIDForCharacterID(character.getID());
		default:
			return character.getID();
		}
	}
	
	public void commit() {
		for (GBAFECharacter character : characterMap.values()) {
			character.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (GBAFECharacter character : characterMap.values()) {
			character.commitChanges();
			if (character.hasCommittedChanges()) {
				Diff charDiff = new Diff(character.getAddressOffset(), character.getData().length, character.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
	
	private GBAFECharacter[] fe7CharactersFromList(Set<FE7Data.Character> characters) {
		List<FE7Data.Character> orderedCharacters = new ArrayList<FE7Data.Character>(characters);
		Collections.sort(orderedCharacters, new Comparator<FE7Data.Character>() {
			public int compare(fedata.gba.fe7.FE7Data.Character arg0, fedata.gba.fe7.FE7Data.Character arg1) { return Integer.compare(arg0.ID, arg1.ID); }
		});
		
		GBAFECharacter[] characterList = new GBAFECharacter[orderedCharacters.size()];
		for (int i = 0; i < orderedCharacters.size(); i++) {
			characterList[i] = characterWithID(orderedCharacters.get(i).ID);
		}
		
		return characterList;
	}
	
	private GBAFECharacter[] fe6CharactersFromList(Set<FE6Data.Character> characters) {
		List<FE6Data.Character> orderedCharacters = new ArrayList<FE6Data.Character>(characters);
		Collections.sort(orderedCharacters, new Comparator<FE6Data.Character>() {
			public int compare(fedata.gba.fe6.FE6Data.Character arg0, fedata.gba.fe6.FE6Data.Character arg1) { return Integer.compare(arg0.ID, arg1.ID); }
		});
		
		GBAFECharacter[] characterList = new GBAFECharacter[orderedCharacters.size()];
		for (int i = 0; i < orderedCharacters.size(); i++) {
			characterList[i] = characterWithID(orderedCharacters.get(i).ID);
		}
		
		return characterList;
	}
	
	private GBAFECharacter[] fe8CharactersFromList(Set<FE8Data.Character> characters) {
		List<FE8Data.Character> orderedCharacters = new ArrayList<FE8Data.Character>(characters);
		Collections.sort(orderedCharacters, new Comparator<FE8Data.Character>() {
			public int compare(fedata.gba.fe8.FE8Data.Character arg0, fedata.gba.fe8.FE8Data.Character arg1) { return Integer.compare(arg0.ID, arg1.ID); }
		});
		
		GBAFECharacter[] characterList = new GBAFECharacter[orderedCharacters.size()];
		for (int i = 0; i < orderedCharacters.size(); i++) {
			characterList[i] = characterWithID(orderedCharacters.get(i).ID);
		}
		
		return characterList;
	}
	
	public void recordCharacters(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, TextLoader textData) {
		for (GBAFECharacter character : playableCharacters()) {
			recordCharacter(rk, character, isInitial, classData, textData);
		}
		for (GBAFECharacter boss : bossCharacters()) {
			recordCharacter(rk, boss, isInitial, classData, textData);
		}
	}
	
	private void recordCharacter(RecordKeeper rk, GBAFECharacter character, Boolean isInitial, ClassDataLoader classData, TextLoader textData) {
		int nameIndex = character.getNameIndex();
		int classID = character.getClassID();
		GBAFEClass charClass = classData.classForID(classID);
		int classNameIndex = charClass.getNameIndex();
		String name = textData.getStringAtIndex(nameIndex).trim();
		String className = textData.getStringAtIndex(classNameIndex).trim();
		
		String classValue = className + " (0x" + Integer.toHexString(classID).toUpperCase() + ")";
		if (isInitial) {
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Class", classValue);
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "HP Growth", String.format("%d%%", character.getHPGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "STR/MAG Growth", String.format("%d%%", character.getSTRGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "SKL Growth", String.format("%d%%", character.getSKLGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "SPD Growth", String.format("%d%%", character.getSPDGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "LCK Growth", String.format("%d%%", character.getLCKGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "DEF Growth", String.format("%d%%", character.getDEFGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "RES Growth", String.format("%d%%", character.getRESGrowth()));
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Base HP", Integer.toString(character.getBaseHP() + charClass.getBaseHP()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Base STR/MAG", Integer.toString(character.getBaseSTR() + charClass.getBaseSTR()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Base SKL", Integer.toString(character.getBaseSKL() + charClass.getBaseSKL()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Base SPD", Integer.toString(character.getBaseSPD() + charClass.getBaseSPD()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Base LCK", Integer.toString(character.getBaseLCK() + charClass.getBaseLCK()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Base DEF", Integer.toString(character.getBaseDEF() + charClass.getBaseDEF()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Base RES", Integer.toString(character.getBaseRES() + charClass.getBaseRES()));
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Base CON", Integer.toString(character.getConstitution() + charClass.getCON()));
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, name, "Affinity", character.getAffinityName() + " (0x" + Integer.toHexString(character.getAffinityValue()).toUpperCase() + ")");
		} else {
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Class", classValue);
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "HP Growth", String.format("%d%%", character.getHPGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "STR/MAG Growth", String.format("%d%%", character.getSTRGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "SKL Growth", String.format("%d%%", character.getSKLGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "SPD Growth", String.format("%d%%", character.getSPDGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "LCK Growth", String.format("%d%%", character.getLCKGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "DEF Growth", String.format("%d%%", character.getDEFGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "RES Growth", String.format("%d%%", character.getRESGrowth()));
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Base HP", Integer.toString(character.getBaseHP() + charClass.getBaseHP()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Base STR/MAG", Integer.toString(character.getBaseSTR() + charClass.getBaseSTR()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Base SKL", Integer.toString(character.getBaseSKL() + charClass.getBaseSKL()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Base SPD", Integer.toString(character.getBaseSPD() + charClass.getBaseSPD()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Base LCK", Integer.toString(character.getBaseLCK() + charClass.getBaseLCK()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Base DEF", Integer.toString(character.getBaseDEF() + charClass.getBaseDEF()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Base RES", Integer.toString(character.getBaseRES() + charClass.getBaseRES()));
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Base CON", Integer.toString(character.getConstitution() + charClass.getCON()));
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, name, "Affinity", character.getAffinityName() + " (0x" + Integer.toHexString(character.getAffinityValue()).toUpperCase() + ")");
		}
	}
}
