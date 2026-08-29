package random.gcnwii.fe9.randomizer;

import java.util.Random;

import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader.StatBias;
import random.general.WeightedDistributor;
import util.WhyDoesJavaNotHaveThese;

public class FE9BasesRandomizer {
	
	static final int rngSalt = 7736;
	
	private enum StatArea { HP, STR, MAG, SKL, SPD, LCK, DEF, RES; }
	
	public static void randomizeBasesByRedistribution(int variance, boolean adjustSTRMAG, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, Random rng) {
		FE9Character[] characters = charData.allPlayableCharacters();
		for (FE9Character character : characters) {
			if (character.wasModified()) { continue; }
			
			int baseTotal = character.getBaseHP() + character.getBaseSTR() + character.getBaseMAG() + character.getBaseSKL() +
					character.getBaseSPD() + character.getBaseLCK() + character.getBaseDEF() + character.getBaseRES();
			
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				baseTotal += rng.nextInt(variance + 1);
			} else {
				baseTotal -= rng.nextInt(variance + 1);
			}
			
			int hpBase = 0;
			int strBase = 0;
			int magBase = 0;
			int sklBase = 0;
			int spdBase = 0;
			int lckBase = 0;
			int defBase = 0;
			int resBase = 0;
			
			String classID = charData.getJIDForCharacter(character);
			FE9Class charClass = classData.classWithID(classID);
			StatBias bias = classData.statBiasForClass(charClass);
			
			WeightedDistributor<StatArea> distributor = new WeightedDistributor<StatArea>();
			distributor.addItem(StatArea.HP, 20);
			distributor.addItem(StatArea.SKL, 10);
			distributor.addItem(StatArea.SPD, 10);
			distributor.addItem(StatArea.LCK, 20);
			distributor.addItem(StatArea.DEF, 10);
			distributor.addItem(StatArea.RES, 10);
			switch (bias) {
			case NONE:
				distributor.addItem(StatArea.STR, 10);
				distributor.addItem(StatArea.MAG, 10);
				break;
			case LEAN_MAGICAL:
				distributor.addItem(StatArea.STR, adjustSTRMAG ? 7 : 10);
				distributor.addItem(StatArea.MAG, adjustSTRMAG ? 13 : 10);
				break;
			case LEAN_PHYSICAL:
				distributor.addItem(StatArea.STR, adjustSTRMAG ? 13 : 10);
				distributor.addItem(StatArea.MAG, adjustSTRMAG ? 7 : 10);
				break;
			case MAGICAL_ONLY:
				distributor.addItem(StatArea.STR, adjustSTRMAG ? 5 : 10);
				distributor.addItem(StatArea.MAG, adjustSTRMAG ? 15 : 10);
				break;
			case PHYSICAL_ONLY:
				distributor.addItem(StatArea.STR, adjustSTRMAG ? 15 : 10);
				distributor.addItem(StatArea.MAG, adjustSTRMAG ? 5 : 10);
				break;
			}
			
			int swingsAvailable = 3 + variance; // Allow us to go in the opposite direction at least three times for more potentially interesting results.
			
			while (baseTotal != 0) {
				int delta = baseTotal > 0 ? 1 : -1;
				if (swingsAvailable > 0 && rng.nextInt(5) == 0) { delta *= -1; swingsAvailable--; }
				baseTotal -= delta;
				
				switch(distributor.getRandomItem(rng)) {
				case HP: hpBase += delta; break;
				case STR: strBase += delta; break;
				case MAG: magBase += delta; break;
				case SKL: sklBase += delta; break;
				case SPD: spdBase += delta; break;
				case LCK: lckBase += delta; break;
				case DEF: defBase += delta; break;
				case RES: resBase += delta; break;
				}
			}
			
			character.setBaseHP(WhyDoesJavaNotHaveThese.clamp(hpBase, charClass.getBaseHP() * -1, charClass.getMaxHP() - charClass.getBaseHP()));
			character.setBaseSTR(WhyDoesJavaNotHaveThese.clamp(strBase, charClass.getBaseSTR() * -1, charClass.getMaxSTR() - charClass.getBaseSTR()));
			character.setBaseMAG(WhyDoesJavaNotHaveThese.clamp(magBase, charClass.getBaseMAG() * -1, charClass.getMaxMAG() - charClass.getBaseMAG()));
			character.setBaseSKL(WhyDoesJavaNotHaveThese.clamp(sklBase, charClass.getBaseSKL() * -1, charClass.getMaxSKL() - charClass.getBaseSKL()));
			character.setBaseSPD(WhyDoesJavaNotHaveThese.clamp(spdBase, charClass.getBaseSPD() * -1, charClass.getMaxSPD() - charClass.getBaseSPD()));
			character.setBaseLCK(WhyDoesJavaNotHaveThese.clamp(lckBase, charClass.getBaseLCK() * -1, charClass.getMaxLCK() - charClass.getBaseLCK()));
			character.setBaseDEF(WhyDoesJavaNotHaveThese.clamp(defBase, charClass.getBaseDEF() * -1, charClass.getMaxDEF() - charClass.getBaseDEF()));
			character.setBaseRES(WhyDoesJavaNotHaveThese.clamp(resBase, charClass.getBaseRES() * -1, charClass.getMaxRES() - charClass.getBaseRES()));
		}
		
		charData.commit();
	}
	
	public static void randomizeBasesByDelta(int variance, boolean adjustSTRMAG, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, Random rng) {
		FE9Character[] characters = charData.allPlayableCharacters();
		for (FE9Character character : characters) {
			if (character.wasModified()) { continue; }
			
			String classID = charData.getJIDForCharacter(character);
			FE9Class charClass = classData.classWithID(classID);
			
			int minHP = -1 * charClass.getBaseHP();
			int maxHP = charClass.getMaxHP() - charClass.getBaseHP();
			int randHP = character.getBaseHP() + (rng.nextInt(2) == 0 ? 1 : -1) * rng.nextInt(variance);
			randHP = WhyDoesJavaNotHaveThese.clamp(randHP, minHP, maxHP);
			character.setBaseHP(randHP);
			
			int minSTR = -1 * charClass.getBaseSTR();
			int maxSTR = charClass.getMaxSTR() - charClass.getBaseSTR();
			int randSTR = character.getBaseSTR() + (rng.nextInt(2) == 0 ? 1 : -1) * rng.nextInt(variance);
			randSTR = WhyDoesJavaNotHaveThese.clamp(randSTR, minSTR, maxSTR);
			character.setBaseSTR(randSTR);
			
			int minMAG = -1 * charClass.getBaseMAG();
			int maxMAG = charClass.getMaxMAG() - charClass.getBaseMAG();
			int randMAG = character.getBaseMAG() + (rng.nextInt(2) == 0 ? 1 : -1) * rng.nextInt(variance);
			randMAG = WhyDoesJavaNotHaveThese.clamp(randMAG, minMAG, maxMAG);
			character.setBaseMAG(randMAG);
			
			int minSKL = -1 * charClass.getBaseSKL();
			int maxSKL = charClass.getMaxSKL() - charClass.getBaseSKL();
			int randSKL = character.getBaseSKL() + (rng.nextInt(2) == 0 ? 1 : -1) * rng.nextInt(variance);
			randSKL = WhyDoesJavaNotHaveThese.clamp(randSKL, minSKL, maxSKL);
			character.setBaseSKL(randSKL);
			
			int minSPD = -1 * charClass.getBaseSPD();
			int maxSPD = charClass.getMaxSPD() - charClass.getBaseSPD();
			int randSPD = character.getBaseSPD() + (rng.nextInt(2) == 0 ? 1 : -1) * rng.nextInt(variance);
			randSPD = WhyDoesJavaNotHaveThese.clamp(randSPD, minSPD, maxSPD);
			character.setBaseSPD(randSPD);
			
			int minLCK = -1 * charClass.getBaseLCK();
			int maxLCK = charClass.getMaxLCK() - charClass.getBaseLCK();
			int randLCK = character.getBaseLCK() + (rng.nextInt(2) == 0 ? 1 : -1) * rng.nextInt(variance);
			randLCK = WhyDoesJavaNotHaveThese.clamp(randLCK, minLCK, maxLCK);
			character.setBaseLCK(randLCK);
			
			int minDEF = -1 * charClass.getBaseDEF();
			int maxDEF = charClass.getMaxDEF() - charClass.getBaseDEF();
			int randDEF = character.getBaseDEF() + (rng.nextInt(2) == 0 ? 1 : -1) * rng.nextInt(variance);
			randDEF = WhyDoesJavaNotHaveThese.clamp(randDEF, minDEF, maxDEF);
			character.setBaseDEF(randDEF);
			
			int minRES = -1 * charClass.getBaseRES();
			int maxRES = charClass.getMaxRES() - charClass.getBaseRES();
			int randRES = character.getBaseRES() + (rng.nextInt(2) == 0 ? 1 : -1) * rng.nextInt(variance);
			randRES = WhyDoesJavaNotHaveThese.clamp(randRES, minRES, maxRES);
			character.setBaseRES(randRES);
		}
		
		charData.commit();
	}
	
	public static void nerfPrologueBoyd(FE9CharacterDataLoader charData) {
		FE9Character prologueBoyd = charData.characterWithID(FE9Data.Character.BOYD_PROLOGUE.getPID());
		prologueBoyd.setBaseSTR(prologueBoyd.getBaseSTR() - 2);
		prologueBoyd.setBaseSPD(prologueBoyd.getBaseSPD() - 2);
		prologueBoyd.setBaseDEF(prologueBoyd.getBaseDEF() - 2);
		prologueBoyd.commitChanges();
	}
}
