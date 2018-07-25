package random;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fedata.FEBase;
import fedata.FECharacter;
import fedata.fe7.FE7Character;
import fedata.fe7.FE7Data;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;

public class CharacterDataLoader {
	
	private FEBase.GameType gameType;
	
	private Map<Integer, FECharacter> characterMap = new HashMap<Integer, FECharacter>();

	public CharacterDataLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE7:
				for (FE7Data.Character character : FE7Data.Character.values()) {
					long offset = FE7Data.Character.dataOffsetForCharacter(character);
					byte[] charData = handler.readBytesAtOffset(offset, FE7Data.BytesPerCharacter);
					characterMap.put(character.ID, new FE7Character(charData, offset));
				}
				break;
			default:
				break;
		}
	}
	
	public FECharacter[] playableCharacters() {
		switch (gameType) {
			case FE7:
				Set<FE7Data.Character> characters = FE7Data.Character.allPlayableCharacters;
				return charactersFromList(characters.toArray(new FE7Data.Character[characters.size()]));
			default:
				return new FECharacter[] {};
		}
	}
	
	public FECharacter[] bossCharacters() {
		switch (gameType) {
		case FE7:
			Set<FE7Data.Character> characters = FE7Data.Character.allBossCharacters;
			return charactersFromList(characters.toArray(new FE7Data.Character[characters.size()]));
		default:
			return new FECharacter[] {};
		}
	}
	
	public Boolean isPlayableCharacterID(int characterID) {
		switch (gameType) {
		case FE7:
			FE7Data.Character character = FE7Data.Character.valueOf(characterID);
			if (character != null) {
				return character.isPlayableCharacter();	
			} else {
				return false;
			}
			
		default:
			return false;
		}
	}
	
	public Boolean isBossCharacterID(int characterID) {
		switch (gameType) {
		case FE7:
			FE7Data.Character character = FE7Data.Character.valueOf(characterID);
			if (character != null) {
				return character.isBoss();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public Boolean isLordCharacterID(int characterID) {
		switch (gameType) {
		case FE7:
			FE7Data.Character character = FE7Data.Character.valueOf(characterID);
			if (character != null) {
				return character.isLord();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public Boolean isThiefCharacterID(int characterID) {
		switch (gameType) {
		case FE7:
			FE7Data.Character character = FE7Data.Character.valueOf(characterID);
			if (character != null) {
				return character.isThief();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public int[] validAffinityValues() {
		switch (gameType) {
		case FE7:
			FE7Character.Affinity[] affinities = FE7Character.Affinity.values();
			int[] validValues = new int[affinities.length];
			for (int i = 0; i < affinities.length; i++) {
				validValues[i] = affinities[i].value;
			}
			
			return validValues;
		default:
			return new int[] {};
		}
	}
	
	public Boolean characterIDRequiresRange(int characterID) {
		switch (gameType) {
		case FE7:
			FE7Data.Character character = FE7Data.Character.valueOf(characterID);
			if (character != null) {
				return character.requiresRange();
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public FECharacter[] linkedCharactersForCharacter(FECharacter character) {
		switch (gameType) {
		case FE7:
			FE7Data.Character characters[] = FE7Data.Character.allLinkedCharactersFor(FE7Data.Character.valueOf(character.getID()));
			return charactersFromList(characters);
		default:
			return new FECharacter[] {character};
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
	
	private FECharacter[] charactersFromList(FE7Data.Character[] characters) {
		int charCount = characters.length;
		FECharacter[] result = new FECharacter[charCount];
		for (int i = 0; i < charCount; i++) {
			FECharacter character = characterMap.get(characters[i].ID);
			result[i] = character;
		}
		
		return result;
	}
}
