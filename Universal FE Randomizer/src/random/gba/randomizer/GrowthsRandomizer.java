package random.gba.randomizer;

import java.util.Random;

import fedata.gba.GBAFECharacter;
import random.gba.loader.CharacterDataLoader;

public class GrowthsRandomizer {
	
	static final int rngSalt = 124;
	
	public static void randomizeGrowthsByRedistribution(int variance, CharacterDataLoader charactersData, Random rng) {
		GBAFECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		for (GBAFECharacter character : allPlayableCharacters) {
			
			if (character.wasModified()) {
				continue;
			}
			
			int growthTotal = character.getHPGrowth() + character.getSTRGrowth() + character.getSKLGrowth() + character.getSPDGrowth() + 
					character.getLCKGrowth() + character.getDEFGrowth() + character.getRESGrowth();
			
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				growthTotal += rng.nextInt(variance + 1);
			} else {
				growthTotal -= rng.nextInt(variance + 1);
			}
			
			int newHPGrowth = 0;
			int newSTRGrowth = 0;
			int newSKLGrowth = 0;
			int newSPDGrowth = 0;
			int newLCKGrowth = 0;
			int newDEFGrowth = 0;
			int newRESGrowth = 0;
			
			while (growthTotal > 0) {
				randomNum = rng.nextInt(8);
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
			
			for (GBAFECharacter thisCharacter : charactersData.linkedCharactersForCharacter(character)) {
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
	
	public static void randomizeGrowthsByRandomDelta(int maxDelta, CharacterDataLoader charactersData, Random rng) {
		GBAFECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		for (GBAFECharacter character : allPlayableCharacters) {
			
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
			
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newHPGrowth += rng.nextInt(maxDelta + 1);
			} else {
				newHPGrowth -= rng.nextInt(maxDelta + 1);
			}
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newSTRGrowth += rng.nextInt(maxDelta + 1);
			} else {
				newSTRGrowth -= rng.nextInt(maxDelta + 1);
			}
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newSKLGrowth += rng.nextInt(maxDelta + 1);
			} else {
				newSKLGrowth -= rng.nextInt(maxDelta + 1);
			}
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newSPDGrowth += rng.nextInt(maxDelta + 1);
			} else {
				newSPDGrowth -= rng.nextInt(maxDelta + 1);
			}
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newLCKGrowth += rng.nextInt(maxDelta + 1);
			} else {
				newLCKGrowth -= rng.nextInt(maxDelta + 1);
			}
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newDEFGrowth += rng.nextInt(maxDelta + 1);
			} else {
				newDEFGrowth -= rng.nextInt(maxDelta + 1);
			}
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newRESGrowth += rng.nextInt(maxDelta + 1);
			} else {
				newRESGrowth -= rng.nextInt(maxDelta + 1);
			}
			
			for (GBAFECharacter thisCharacter : charactersData.linkedCharactersForCharacter(character)) {
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
	
	public static void fullyRandomizeGrowthsWithRange(int minGrowth, int maxGrowth, CharacterDataLoader charactersData, Random rng) {
		GBAFECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		for (GBAFECharacter character : allPlayableCharacters) {
			
			if (character.wasModified()) {
				continue;
			}
			
			int range = maxGrowth - minGrowth + 1;
			
			int newHPGrowth = rng.nextInt(range) + minGrowth;
			int newSTRGrowth = rng.nextInt(range) + minGrowth;
			int newSKLGrowth = rng.nextInt(range) + minGrowth;
			int newSPDGrowth = rng.nextInt(range) + minGrowth;
			int newLCKGrowth = rng.nextInt(range) + minGrowth;
			int newDEFGrowth = rng.nextInt(range) + minGrowth;
			int newRESGrowth = rng.nextInt(range) + minGrowth;
			
			for (GBAFECharacter thisCharacter : charactersData.linkedCharactersForCharacter(character)) {
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
