package random;

import java.util.HashMap;
import java.util.Map;

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
				return charactersFromList(FE7Data.Character.allPlayableCharacters);
			default:
				return new FECharacter[] {};
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
