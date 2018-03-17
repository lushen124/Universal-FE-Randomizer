package random;

import java.util.concurrent.ThreadLocalRandom;

import fedata.FECharacter;

public class GrowthsRandomizer {
	
	public static void randomizeGrowthsByRedistribution(int variance, CharacterDataLoader charactersData) {
		FECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		for (FECharacter character : allPlayableCharacters) {
			
			if (character.wasModified()) {
				continue;
			}
			
			int growthTotal = character.getHPGrowth() + character.getSTRGrowth() + character.getSKLGrowth() + character.getSPDGrowth() + 
					character.getLCKGrowth() + character.getDEFGrowth() + character.getRESGrowth();
			
			int randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				growthTotal += ThreadLocalRandom.current().nextInt(variance + 1);
			} else {
				growthTotal -= ThreadLocalRandom.current().nextInt(variance + 1);
			}
			
			int newHPGrowth = 0;
			int newSTRGrowth = 0;
			int newSKLGrowth = 0;
			int newSPDGrowth = 0;
			int newLCKGrowth = 0;
			int newDEFGrowth = 0;
			int newRESGrowth = 0;
			
			while (growthTotal > 0) {
				randomNum = ThreadLocalRandom.current().nextInt(8);
				int amount = Math.min(5,  growthTotal);
				growthTotal -= amount;
				switch (randomNum) {
				case 0:
				case 1:
					newHPGrowth += amount;
					break;
				case 2:
					newSTRGrowth += amount;
					break;
				case 3:
					newSKLGrowth += amount;
					break;
				case 4:
					newSPDGrowth += amount;
					break;
				case 5:
					newLCKGrowth += amount;
					break;
				case 6: 
					newDEFGrowth += amount;
					break;
				case 7:
					newRESGrowth += amount;
					break;
				default:
					break;
				}
			}
			
			for (FECharacter thisCharacter : charactersData.linkedCharactersForCharacter(character)) {
				thisCharacter.setHPGrowth(newHPGrowth);
				thisCharacter.setSTRGrowth(newSTRGrowth);
				thisCharacter.setSKLGrowth(newSKLGrowth);
				thisCharacter.setSPDGrowth(newSPDGrowth);
				thisCharacter.setLCKGrowth(newLCKGrowth);
				thisCharacter.setDEFGrowth(newDEFGrowth);
				thisCharacter.setRESGrowth(newRESGrowth);
			}
		}
		
		charactersData.commit();
	}
	
	public static void randomizeGrowthsByRandomDelta(int maxDelta, CharacterDataLoader charactersData) {
		FECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		for (FECharacter character : allPlayableCharacters) {
			
			if (character.wasModified()) {
				continue;
			}
			
			int newHPGrowth = character.getHPGrowth();
			int newSTRGrowth = character.getSTRGrowth();
			int newSKLGrowth = character.getSKLGrowth();
			int newSPDGrowth = character.getSPDGrowth();
			int newLCKGrowth = character.getLCKGrowth();
			int newDEFGrowth = character.getDEFGrowth();
			int newRESGrowth = character.getRESGrowth();
			
			int randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				newHPGrowth += ThreadLocalRandom.current().nextInt(maxDelta + 1);
			} else {
				newHPGrowth -= ThreadLocalRandom.current().nextInt(maxDelta + 1);
			}
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				newSTRGrowth += ThreadLocalRandom.current().nextInt(maxDelta + 1);
			} else {
				newSTRGrowth -= ThreadLocalRandom.current().nextInt(maxDelta + 1);
			}
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				newSKLGrowth += ThreadLocalRandom.current().nextInt(maxDelta + 1);
			} else {
				newSKLGrowth -= ThreadLocalRandom.current().nextInt(maxDelta + 1);
			}
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				newSPDGrowth += ThreadLocalRandom.current().nextInt(maxDelta + 1);
			} else {
				newSPDGrowth -= ThreadLocalRandom.current().nextInt(maxDelta + 1);
			}
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				newLCKGrowth += ThreadLocalRandom.current().nextInt(maxDelta + 1);
			} else {
				newLCKGrowth -= ThreadLocalRandom.current().nextInt(maxDelta + 1);
			}
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				newDEFGrowth += ThreadLocalRandom.current().nextInt(maxDelta + 1);
			} else {
				newDEFGrowth -= ThreadLocalRandom.current().nextInt(maxDelta + 1);
			}
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				newRESGrowth += ThreadLocalRandom.current().nextInt(maxDelta + 1);
			} else {
				newRESGrowth -= ThreadLocalRandom.current().nextInt(maxDelta + 1);
			}
			
			for (FECharacter thisCharacter : charactersData.linkedCharactersForCharacter(character)) {
				thisCharacter.setHPGrowth(newHPGrowth);
				thisCharacter.setSTRGrowth(newSTRGrowth);
				thisCharacter.setSKLGrowth(newSKLGrowth);
				thisCharacter.setSPDGrowth(newSPDGrowth);
				thisCharacter.setLCKGrowth(newLCKGrowth);
				thisCharacter.setDEFGrowth(newDEFGrowth);
				thisCharacter.setRESGrowth(newRESGrowth);
			}
		}
		
		charactersData.commit();
	}
	
	public static void fullyRandomizeGrowthsWithRange(int minGrowth, int maxGrowth, CharacterDataLoader charactersData) {
		FECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		for (FECharacter character : allPlayableCharacters) {
			
			if (character.wasModified()) {
				continue;
			}
			
			int newHPGrowth = ThreadLocalRandom.current().nextInt(minGrowth, maxGrowth + 1);
			int newSTRGrowth = ThreadLocalRandom.current().nextInt(minGrowth, maxGrowth + 1);
			int newSKLGrowth = ThreadLocalRandom.current().nextInt(minGrowth, maxGrowth + 1);
			int newSPDGrowth = ThreadLocalRandom.current().nextInt(minGrowth, maxGrowth + 1);
			int newLCKGrowth = ThreadLocalRandom.current().nextInt(minGrowth, maxGrowth + 1);
			int newDEFGrowth = ThreadLocalRandom.current().nextInt(minGrowth, maxGrowth + 1);
			int newRESGrowth = ThreadLocalRandom.current().nextInt(minGrowth, maxGrowth + 1);
			
			for (FECharacter thisCharacter : charactersData.linkedCharactersForCharacter(character)) {
				thisCharacter.setHPGrowth(newHPGrowth);
				thisCharacter.setSTRGrowth(newSTRGrowth);
				thisCharacter.setSKLGrowth(newSKLGrowth);
				thisCharacter.setSPDGrowth(newSPDGrowth);
				thisCharacter.setLCKGrowth(newLCKGrowth);
				thisCharacter.setDEFGrowth(newDEFGrowth);
				thisCharacter.setRESGrowth(newRESGrowth);
			}
		}
		
		charactersData.commit();
	}

}
