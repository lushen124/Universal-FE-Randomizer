package random;

import java.util.Random;

import fedata.FECharacter;
import fedata.FEClass;

public class CharacterRandomizer {
	
	public static int rngSalt = 9002;
	
	public static void randomizeAffinity(CharacterDataLoader charactersData, Random rng) {
		FECharacter[] playableCharacters = charactersData.playableCharacters();
		int[] values = charactersData.validAffinityValues();
		for (FECharacter character : playableCharacters) {
			int affinity = values[rng.nextInt(values.length)];
			character.setAffinityValue(affinity);
		}
	}
	
	public static void randomizeConstitution(int minCON, int variance, CharacterDataLoader characterData, ClassDataLoader classData, Random rng) {
		FECharacter[] allPlayableCharacters = characterData.playableCharacters();
		for (FECharacter character : allPlayableCharacters) {
			FEClass currentClass = classData.classForID(character.getClassID());
			int classCON = currentClass.getCON();
			int personalCON = character.getConstitution();
			int totalCON = classCON + personalCON;
			
			int newCON = totalCON;
			
			int direction = rng.nextInt(2);
			if (direction == 0) {
				newCON += rng.nextInt(variance);
			} else {
				newCON -= rng.nextInt(variance);
			}
			
			int newPersonalCON = newCON - classCON;
			
			character.setConstitution(newPersonalCON);
		}
	}
}
