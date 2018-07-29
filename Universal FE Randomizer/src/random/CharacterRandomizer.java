package random;

import java.util.concurrent.ThreadLocalRandom;

import fedata.FECharacter;
import fedata.FEClass;

public class CharacterRandomizer {
	public static void randomizeAffinity(CharacterDataLoader charactersData) {
		FECharacter[] playableCharacters = charactersData.playableCharacters();
		int[] values = charactersData.validAffinityValues();
		for (FECharacter character : playableCharacters) {
			int affinity = values[ThreadLocalRandom.current().nextInt(values.length)];
			character.setAffinityValue(affinity);
		}
	}
	
	public static void randomizeConstitution(int minCON, int variance, CharacterDataLoader characterData, ClassDataLoader classData) {
		FECharacter[] allPlayableCharacters = characterData.playableCharacters();
		for (FECharacter character : allPlayableCharacters) {
			FEClass currentClass = classData.classForID(character.getClassID());
			int classCON = currentClass.getCON();
			int personalCON = character.getConstitution();
			int totalCON = classCON + personalCON;
			
			int newCON = totalCON;
			
			int direction = ThreadLocalRandom.current().nextInt(2);
			if (direction == 0) {
				newCON += ThreadLocalRandom.current().nextInt(variance);
			} else {
				newCON -= ThreadLocalRandom.current().nextInt(variance);
			}
			
			int newPersonalCON = newCON - classCON;
			
			character.setConstitution(newPersonalCON);
		}
	}
}
