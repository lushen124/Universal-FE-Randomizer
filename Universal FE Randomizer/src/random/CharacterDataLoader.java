package random;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fedata.FEBase;
import fedata.FECharacter;
import fedata.FEClass;
import fedata.fe6.FE6Character;
import fedata.fe6.FE6Data;
import fedata.fe7.FE7Character;
import fedata.fe7.FE7Data;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.recordkeeper.RecordKeeper;

public class CharacterDataLoader {
	
	private FEBase.GameType gameType;
	
	private Map<Integer, FECharacter> characterMap = new HashMap<Integer, FECharacter>();
	private Map<Integer, FECharacter> counterMap = new HashMap<Integer, FECharacter>();
	
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
			default:
				break;
		}
	}
	
	public FECharacter characterWithID(int characterID) {
		return characterMap.get(characterID);
	}
	
	public FECharacter[] playableCharacters() {
		switch (gameType) {
		case FE6:
				Set<FE6Data.Character> fe6Characters = FE6Data.Character.allPlayableCharacters;
				return fe6CharactersFromList(fe6Characters.toArray(new FE6Data.Character[fe6Characters.size()]));
			case FE7:
				Set<FE7Data.Character> fe7Characters = FE7Data.Character.allPlayableCharacters;
				return fe7CharactersFromList(fe7Characters.toArray(new FE7Data.Character[fe7Characters.size()]));
			default:
				return new FECharacter[] {};
		}
	}
	
	public FECharacter[] bossCharacters() {
		switch (gameType) {
		case FE6:
			Set<FE6Data.Character> fe6Characters = FE6Data.Character.allBossCharacters;
			return fe6CharactersFromList(fe6Characters.toArray(new FE6Data.Character[fe6Characters.size()]));
		case FE7:
			Set<FE7Data.Character> fe7Characters = FE7Data.Character.allBossCharacters;
			return fe7CharactersFromList(fe7Characters.toArray(new FE7Data.Character[fe7Characters.size()]));
		default:
			return new FECharacter[] {};
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
		default:
			return false;
		}
	}
	
	public FECharacter characterRequiresCounterToCharacter(FECharacter character) {
		return counterMap.get(character.getID());
	}
	
	public FECharacter[] linkedCharactersForCharacter(FECharacter character) {
		switch (gameType) {
		case FE6:
			FE6Data.Character fe6Characters[] = FE6Data.Character.allLinkedCharactersFor(FE6Data.Character.valueOf(character.getID()));
			return fe6CharactersFromList(fe6Characters);
		case FE7:
			FE7Data.Character fe7Characters[] = FE7Data.Character.allLinkedCharactersFor(FE7Data.Character.valueOf(character.getID()));
			return fe7CharactersFromList(fe7Characters);
		default:
			return new FECharacter[] {character};
		}
	}
	
	public int getCanonicalIDForCharacter(FECharacter character) {
		switch (gameType) {
		case FE6:
			return FE6Data.Character.canonicalIDForCharacterID(character.getID());
		case FE7:
			return FE7Data.Character.canonicalIDForCharacterID(character.getID());
		default:
			return character.getID();
		}
	}
	
	public void commit() {
		for (FECharacter character : characterMap.values()) {
			character.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FECharacter character : characterMap.values()) {
			character.commitChanges();
			if (character.hasCommittedChanges()) {
				Diff charDiff = new Diff(character.getAddressOffset(), character.getData().length, character.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
	
	private FECharacter[] fe7CharactersFromList(FE7Data.Character[] characters) {
		int charCount = characters.length;
		FECharacter[] result = new FECharacter[charCount];
		for (int i = 0; i < charCount; i++) {
			FECharacter character = characterMap.get(characters[i].ID);
			result[i] = character;
		}
		
		return result;
	}
	
	private FECharacter[] fe6CharactersFromList(FE6Data.Character[] characters) {
		int charCount = characters.length;
		FECharacter[] result = new FECharacter[charCount];
		for (int i = 0; i < charCount; i++) {
			FECharacter character = characterMap.get(characters[i].ID);
			result[i] = character;
		}
		
		return result;
	}
	
	public void recordCharacters(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, TextLoader textData) {
		for (FECharacter character : playableCharacters()) {
			recordCharacter(rk, character, isInitial, classData, textData);
		}
		for (FECharacter boss : bossCharacters()) {
			recordCharacter(rk, boss, isInitial, classData, textData);
		}
	}
	
	private void recordCharacter(RecordKeeper rk, FECharacter character, Boolean isInitial, ClassDataLoader classData, TextLoader textData) {
		int nameIndex = character.getNameIndex();
		int classID = character.getClassID();
		FEClass charClass = classData.classForID(classID);
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
