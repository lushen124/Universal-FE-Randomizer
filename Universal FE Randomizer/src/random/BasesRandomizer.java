package random;

import java.util.concurrent.ThreadLocalRandom;

import fedata.FECharacter;
import fedata.FEClass;
import util.WhyDoesJavaNotHaveThese;

public class BasesRandomizer {
	
	public static void randomizeBasesByRedistribution(int variance, CharacterDataLoader charactersData, ClassDataLoader classData) {
		FECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		for (FECharacter character : allPlayableCharacters) {
			int baseTotal = character.getBaseHP() + character.getBaseSTR() + character.getBaseSKL() + character.getBaseSPD() + character.getBaseDEF() +
					character.getBaseRES() + character.getBaseLCK();
			
			int classID = character.getClassID();
			FEClass charClass = classData.classForID(classID);
			
			int randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				baseTotal += ThreadLocalRandom.current().nextInt(variance + 1);
			} else {
				baseTotal -= ThreadLocalRandom.current().nextInt(variance + 1);
			}
			
			int newHPBase = 0;
			int newSTRBase = 0;
			int newSKLBase = 0;
			int newSPDBase = 0;
			int newLCKBase = 0;
			int newDEFBase = 0;
			int newRESBase = 0;
			
			do {
				randomNum = ThreadLocalRandom.current().nextInt(10);
				int amount = ThreadLocalRandom.current().nextInt(1, 3);
				Boolean negate = ThreadLocalRandom.current().nextInt(4) == 0;
				if (baseTotal < -3) {
					negate = true;
				} else if (baseTotal > 5) {
					negate = false;
				}
				if (negate) {
					amount *= -1;
				}
				
				switch (randomNum) {
				case 1:
					if (!WhyDoesJavaNotHaveThese.isValueBetween(newHPBase + amount, -1 * charClass.getBaseHP(), charClass.getMaxHP() - charClass.getBaseHP())) {
						continue;
					}
					newHPBase += amount;
					break;
				case 2:
					if (!WhyDoesJavaNotHaveThese.isValueBetween(newSTRBase + amount, -1 * charClass.getBaseSTR(), charClass.getMaxSTR() - charClass.getBaseSTR())) {
						continue;
					}
					newSTRBase += amount;
					break;
				case 3:
					if (!WhyDoesJavaNotHaveThese.isValueBetween(newSKLBase + amount, -1 * charClass.getBaseSKL(), charClass.getMaxSKL() - charClass.getBaseSKL())) {
						continue;
					}
					newSKLBase += amount;
					break;
				case 4:
					if (!WhyDoesJavaNotHaveThese.isValueBetween(newSPDBase + amount, -1 * charClass.getBaseSPD(), charClass.getMaxSPD() - charClass.getBaseSPD())) {
						continue;
					}
					newSPDBase += amount;
					break;
				case 0:
				case 5:
				case 8:
				case 9:
					if (negate) {
						amount *= -1;
					}
					if (!WhyDoesJavaNotHaveThese.isValueBetween(newLCKBase + amount, -1 * charClass.getBaseLCK(), charClass.getMaxLCK() - charClass.getBaseLCK())) {
						continue;
					}
					newLCKBase += amount;
					break;
				case 6: 
					if (!WhyDoesJavaNotHaveThese.isValueBetween(newDEFBase + amount, -1 * charClass.getBaseDEF(), charClass.getMaxDEF() - charClass.getBaseDEF())) {
						continue;
					}
					newDEFBase += amount;
					break;
				case 7:
					if (!WhyDoesJavaNotHaveThese.isValueBetween(newRESBase + amount, -1 * charClass.getBaseRES(), charClass.getMaxRES() - charClass.getBaseRES())) {
						continue;
					}
					newRESBase += amount;
					break;
				default:
					break;
				}
				
				baseTotal -= amount;
			} while (baseTotal != 0);
			
			character.setBaseHP(newHPBase);
			character.setBaseSTR(newSTRBase);
			character.setBaseSKL(newSKLBase);
			character.setBaseSPD(newSPDBase);
			character.setBaseLCK(newLCKBase);
			character.setBaseDEF(newDEFBase);
			character.setBaseRES(newRESBase);
		}
		
		charactersData.commit();
	}
	
	public static void randomizeBasesByRandomDelta(int maxDelta, CharacterDataLoader charactersData, ClassDataLoader classData) {
		FECharacter[] allPlayableCharacters = charactersData.playableCharacters();
		for (FECharacter character : allPlayableCharacters) {
			
			int classID = character.getClassID();
			FEClass charClass = classData.classForID(classID);
			
			int newHPBase = character.getBaseHP();
			int newSTRBase = character.getBaseSTR();
			int newSKLBase = character.getBaseSKL();
			int newSPDBase = character.getBaseSPD();
			int newLCKBase = character.getBaseLCK();
			int newDEFBase = character.getBaseDEF();
			int newRESBase = character.getBaseRES();
			
			int randomNum = ThreadLocalRandom.current().nextInt(2);
			int multiplier = 1;
			if (randomNum == 0) {
				multiplier = -1;
			}
			newHPBase = WhyDoesJavaNotHaveThese.clamp(ThreadLocalRandom.current().nextInt(maxDelta + 1) * multiplier + newHPBase, 
					-1 * charClass.getBaseHP(), charClass.getMaxHP() - charClass.getBaseHP());
			
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newSTRBase = WhyDoesJavaNotHaveThese.clamp(ThreadLocalRandom.current().nextInt(maxDelta + 1) * multiplier + newSTRBase, 
					-1 * charClass.getBaseSTR(), charClass.getMaxSTR() - charClass.getBaseSTR());
			
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newSKLBase = WhyDoesJavaNotHaveThese.clamp(ThreadLocalRandom.current().nextInt(maxDelta + 1) * multiplier + newSKLBase, 
					-1 * charClass.getBaseSKL(), charClass.getMaxSKL() - charClass.getBaseSKL());
			
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newSPDBase = WhyDoesJavaNotHaveThese.clamp(ThreadLocalRandom.current().nextInt(maxDelta + 1) * multiplier + newSPDBase, 
					-1 * charClass.getBaseSPD(), charClass.getMaxSPD() - charClass.getBaseSPD());
			
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newLCKBase = WhyDoesJavaNotHaveThese.clamp(ThreadLocalRandom.current().nextInt(maxDelta + 1) * multiplier + newLCKBase, 
					-1 * charClass.getBaseLCK(), charClass.getMaxLCK() - charClass.getBaseLCK());
			
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newDEFBase = WhyDoesJavaNotHaveThese.clamp(ThreadLocalRandom.current().nextInt(maxDelta + 1) * multiplier + newDEFBase, 
					-1 * charClass.getBaseDEF(), charClass.getMaxDEF() - charClass.getBaseDEF());
			
			randomNum = ThreadLocalRandom.current().nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newRESBase = WhyDoesJavaNotHaveThese.clamp(ThreadLocalRandom.current().nextInt(maxDelta + 1) * multiplier + newRESBase, 
					-1 * charClass.getBaseRES(), charClass.getMaxRES() - charClass.getBaseRES());
			
			character.setBaseHP(newHPBase);
			character.setBaseSTR(newSTRBase);
			character.setBaseSKL(newSKLBase);
			character.setBaseSPD(newSPDBase);
			character.setBaseLCK(newLCKBase);
			character.setBaseDEF(newDEFBase);
			character.setBaseRES(newRESBase);
		}
		
		charactersData.commit();
	}
}
