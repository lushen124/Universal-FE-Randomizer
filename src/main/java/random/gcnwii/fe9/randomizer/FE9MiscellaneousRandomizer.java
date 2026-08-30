package random.gcnwii.fe9.randomizer;

import java.util.Random;

import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;

public class FE9MiscellaneousRandomizer {
	
	public static final int rngSalt = 90210;
	
	public static void randomizeCON(int variance, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, Random rng) {
		for (FE9Character character : charData.allPlayableCharacters()) {
			String jid = charData.getJIDForCharacter(character);
			FE9Class charClass = classData.classWithID(jid);
			int effectiveCON = character.getBuild() + charClass.getBaseCON();
			
			boolean isNegative = rng.nextInt(2) == 0;
			int adjustment = rng.nextInt(variance);
			if (isNegative) {
				if (adjustment > effectiveCON) { adjustment = effectiveCON - 1; }
				character.setBuild(character.getBuild() - adjustment);
				character.setWeight(character.getWeight() - adjustment);
			} else {
				character.setBuild(character.getBuild() + adjustment);
				character.setWeight(character.getWeight() + adjustment);
			}
		}
		
		charData.commit();
	}
	
	public static void randomizeAffinity(FE9CharacterDataLoader charData, Random rng) {
		FE9Data.Affinity[] affinities = FE9Data.Affinity.values();
		for (FE9Character character : charData.allPlayableCharacters()) {
			FE9Data.Affinity currentAffinity = charData.getAffinityForCharacter(character);
			if (currentAffinity == null) { continue; }
			FE9Data.Affinity newAffinity = affinities[rng.nextInt(affinities.length)];
			while (newAffinity.getInternalID().equals(currentAffinity.getInternalID())) {
				newAffinity = affinities[rng.nextInt(affinities.length)];
			}
			charData.setAffinityForCharacter(character, newAffinity);
		}
		
		charData.commit();
	}
}
