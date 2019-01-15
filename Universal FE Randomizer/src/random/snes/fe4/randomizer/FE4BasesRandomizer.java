package random.snes.fe4.randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4StaticCharacter;
import random.general.WeightedDistributor;
import random.snes.fe4.loader.CharacterDataLoader;

public class FE4BasesRandomizer {
	
	static final int rngSalt = 9999;
	
	enum StatArea {
		STR, MAG, SKL, SPD, LCK, DEF, RES
	}
	
	public static void randomizeBasesByRedistribution(int variance, boolean adjustSTRMAGByClass, CharacterDataLoader charData, Random rng) {
		List<FE4StaticCharacter> allChars = new ArrayList<FE4StaticCharacter>();
		allChars.addAll(charData.getGen1Characters());
		allChars.addAll(charData.getGen2CommonCharacters());
		allChars.addAll(charData.getGen2SubstituteCharacters());
		
		for (FE4StaticCharacter staticChar : allChars) {
			int baseTotal = staticChar.getBaseSTR() + staticChar.getBaseMAG() + staticChar.getBaseSKL() + staticChar.getBaseSPD() + 
					staticChar.getBaseLCK() + staticChar.getBaseDEF() + staticChar.getBaseRES();
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				baseTotal += rng.nextInt(variance + 1);
			} else {
				baseTotal -= rng.nextInt(variance + 1);
			}
			
			int newBaseSTR = 0;
			int newBaseMAG = 0;
			int newBaseSKL = 0;
			int newBaseSPD = 0;
			int newBaseLCK = 0;
			int newBaseDEF = 0;
			int newBaseRES = 0;
			
			FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			boolean weightSTR = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsStrength() : false;
			boolean weightMAG = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsMagic() : false;
			
			WeightedDistributor<StatArea> distributor = new WeightedDistributor<StatArea>();
			distributor.addItem(StatArea.SKL, 3);
			distributor.addItem(StatArea.SPD, 3);
			distributor.addItem(StatArea.LCK, 5); // Since there's no base here, usually.
			distributor.addItem(StatArea.DEF, 3);
			distributor.addItem(StatArea.RES, 3);
			
			if (weightSTR && !weightMAG) {
				distributor.addItem(StatArea.STR, 3);
				distributor.addItem(StatArea.MAG, 1);
			} else if (weightMAG && !weightSTR) {
				distributor.addItem(StatArea.STR, 1);
				distributor.addItem(StatArea.MAG, 3);
			} else {
				distributor.addItem(StatArea.STR, 3);
				distributor.addItem(StatArea.MAG, 3);
			}
			
			while (baseTotal > 0) {
				baseTotal -= 1;
				StatArea area = distributor.getRandomItem(rng);
				switch (area) {
				case STR:
					newBaseSTR++;
					break;
				case MAG:
					newBaseMAG++;
					break;
				case SKL:
					newBaseSKL++;
					break;
				case SPD:
					newBaseSPD++;
					break;
				case LCK:
					newBaseLCK++;
					break;
				case DEF:
					newBaseDEF++;
					break;
				case RES:
					newBaseRES++;
					break;
				}
			}
			
			staticChar.setBaseSTR(newBaseSTR);
			staticChar.setBaseMAG(newBaseMAG);
			staticChar.setBaseSKL(newBaseSKL);
			staticChar.setBaseSPD(newBaseSPD);
			staticChar.setBaseLCK(newBaseLCK);
			staticChar.setBaseDEF(newBaseDEF);
			staticChar.setBaseRES(newBaseRES);
		}
	}
	
	public static void randomizeBasesByDelta(int maxDelta, boolean adjustSTRMAGByClass, CharacterDataLoader charData, Random rng) {
		List<FE4StaticCharacter> allChars = new ArrayList<FE4StaticCharacter>();
		allChars.addAll(charData.getGen1Characters());
		allChars.addAll(charData.getGen2CommonCharacters());
		allChars.addAll(charData.getGen2SubstituteCharacters());
		
		for (FE4StaticCharacter staticChar : allChars) {
			
			int newBaseHP = staticChar.getBaseHP();
			int newBaseSTR = staticChar.getBaseSTR();
			int newBaseMAG = staticChar.getBaseMAG();
			int newBaseSKL = staticChar.getBaseSKL();
			int newBaseSPD = staticChar.getBaseSPD();
			int newBaseLCK = staticChar.getBaseLCK();
			int newBaseDEF = staticChar.getBaseDEF();
			int newBaseRES = staticChar.getBaseRES();
			
			FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			boolean weightSTR = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsStrength() : false;
			boolean weightMAG = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsMagic() : false;
			
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newBaseHP += rng.nextInt(maxDelta + 1);
			} else {
				newBaseHP -= rng.nextInt(maxDelta + 1);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newBaseSTR += rng.nextInt(maxDelta + 1);
			} else {
				newBaseSTR -= rng.nextInt(maxDelta + 1);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newBaseMAG += rng.nextInt(maxDelta + 1);
			} else {
				newBaseMAG -= rng.nextInt(maxDelta + 1);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newBaseSKL += rng.nextInt(maxDelta + 1);
			} else {
				newBaseSKL -= rng.nextInt(maxDelta + 1);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newBaseSPD += rng.nextInt(maxDelta + 1);
			} else {
				newBaseSPD -= rng.nextInt(maxDelta + 1);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newBaseLCK += rng.nextInt(maxDelta + 1);
			} else {
				newBaseLCK -= rng.nextInt(maxDelta + 1);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newBaseDEF += rng.nextInt(maxDelta + 1);
			} else {
				newBaseDEF -= rng.nextInt(maxDelta + 1);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newBaseRES += rng.nextInt(maxDelta + 1);
			} else {
				newBaseRES -= rng.nextInt(maxDelta + 1);
			}
			
			if ((weightSTR && !weightMAG && newBaseSTR < newBaseMAG) || (weightMAG && !weightSTR && newBaseMAG < newBaseSTR)) {
				int oldSTR = newBaseSTR;
				newBaseSTR = newBaseMAG;
				newBaseMAG = oldSTR;
			}
			
			staticChar.setBaseHP(Math.max(0, newBaseHP));
			staticChar.setBaseSTR(Math.max(0, newBaseSTR));
			staticChar.setBaseMAG(Math.max(0, newBaseMAG));
			staticChar.setBaseSKL(Math.max(0, newBaseSKL));
			staticChar.setBaseSPD(Math.max(0, newBaseSPD));
			staticChar.setBaseLCK(Math.max(0, newBaseLCK));
			staticChar.setBaseDEF(Math.max(0, newBaseDEF));
			staticChar.setBaseRES(Math.max(0, newBaseRES));
		}
	}

}
