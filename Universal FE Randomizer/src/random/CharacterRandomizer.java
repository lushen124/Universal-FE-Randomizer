package random;

import java.util.concurrent.ThreadLocalRandom;

import fedata.FECharacter;

public class CharacterRandomizer {
	public static void randomizeAffinity(CharacterDataLoader charactersData) {
		FECharacter[] playableCharacters = charactersData.playableCharacters();
		int[] values = charactersData.validAffinityValues();
		for (FECharacter character : playableCharacters) {
			int affinity = values[ThreadLocalRandom.current().nextInt(values.length)];
			character.setAffinityValue(affinity);
		}
	}
}
