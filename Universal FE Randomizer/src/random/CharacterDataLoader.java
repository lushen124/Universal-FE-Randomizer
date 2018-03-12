package random;

import java.util.HashMap;
import java.util.Map;

import fedata.FEBase;
import fedata.FECharacter;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import fedata.FE7Character;
import fedata.FE7Data;

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
				int charCount = FE7Data.Character.allPlayableCharacters.length;
				FECharacter[] charArray = new FECharacter[charCount];
				for (int i = 0; i < charCount; i++) {
					FECharacter character = characterMap.get(FE7Data.Character.allPlayableCharacters[i].ID);
					charArray[i] = character;
				}
				
				return charArray;
			default:
				return new FECharacter[] {};
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FECharacter character : characterMap.values()) {
			if (character.wasModified()) {
				Diff charDiff = new Diff(character.getAddressOffset(), character.getCharacterData().length, character.getCharacterData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
}
