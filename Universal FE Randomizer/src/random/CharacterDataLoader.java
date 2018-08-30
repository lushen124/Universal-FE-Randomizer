package random;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fedata.FEBase;
import fedata.FECharacter;
import fedata.fe6.FE6Character;
import fedata.fe6.FE6Data;
import fedata.fe7.FE7Character;
import fedata.fe7.FE7Data;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;

public class CharacterDataLoader {
	
	private FEBase.GameType gameType;
	
	private Map<Integer, FECharacter> characterMap = new HashMap<Integer, FECharacter>();
	private Map<Integer, FECharacter> counterMap = new HashMap<Integer, FECharacter>();

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
			FE6Character.Affinity[] fe6Affinities = FE6Character.Affinity.values();
			int[] validValues = new int[fe6Affinities.length];
			for (int i = 0; i < fe6Affinities.length; i++) {
				validValues[i] = fe6Affinities[i].value;
			}
			
			return validValues;
		case FE7:
			FE7Character.Affinity[] fe7Affinities = FE7Character.Affinity.values();
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
}
