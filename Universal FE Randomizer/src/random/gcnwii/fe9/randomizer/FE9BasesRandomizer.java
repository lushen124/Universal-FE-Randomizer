package random.gcnwii.fe9.randomizer;

import java.util.List;
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
	
	public static void randomizeBasesByRedistribution(int variance, boolean adjustSTRMAG, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, Random rng) {
		FE9Character[] characters = charData.allPlayableCharacters();
		for (FE9Character character : characters) {
			if (character.wasModified()) { continue; }
			
			String classID = charData.getJIDForCharacter(character);
			FE9Class charClass = classData.classWithID(classID);
			
			int charBaseTotal = character.getBaseHP() + character.getBaseSTR() + character.getBaseMAG() + character.getBaseSKL() +
					character.getBaseSPD() + character.getBaseLCK() + character.getBaseDEF() + character.getBaseRES();
			
			int classBaseTotal = charClass.getBaseHP() + charClass.getBaseSTR() + charClass.getBaseMAG() + charClass.getBaseSKL() +
					charClass.getBaseSPD() + charClass.getBaseLCK() + charClass.getBaseDEF() + charClass.getBaseRES();
			
			int baseTotal = charBaseTotal + classBaseTotal;
			
//			int randomNum = rng.nextInt(2);
//			if (randomNum == 0) {
				baseTotal += rng.nextInt(variance + 1);
//			} else {
//				baseTotal -= rng.nextInt(variance + 1);
//			}
			
			int hp = 0;
			int str = 0;
			int mag = 0;
			int skl = 0;
			int spd = 0;
			int lck = 0;
			int def = 0;
			int res = 0;
			
			// Randomize Luck first to remove it from the equation later, since that's hard to balance.
			// Luck ranges from half of their level up to 3 higher than their level.
			// Characters under level 5 are treated as if their level were 5.
			// Promoted characters are treated as 12 + their current level for this.
			boolean isPromoted = classData.isPromotedClass(classData.classWithID(charData.getJIDForCharacter(character)));
			int effectiveLevel = character.getLevel();
			if (isPromoted) { effectiveLevel += 12; }
			else { effectiveLevel = Math.max(5, effectiveLevel); }
			lck = rng.nextInt(effectiveLevel / 2, effectiveLevel + 3 + 1);
			baseTotal -= lck;
			
			// HP is also hard to balance, so do that separately as well.
			// HP can range from 15 up to 24 + unit level for unpromoted
			// and 30 up to 40 + unit level for promoted.
			if (isPromoted) {
				hp = rng.nextInt(30, 40 + character.getLevel() + 1);
			} else {
				hp = rng.nextInt(15, 24 + character.getLevel() + 1);
			}
			baseTotal -= hp;
			
			WeightedDistributor<FE9Data.StatArea> distributor = new WeightedDistributor<FE9Data.StatArea>();
			if (adjustSTRMAG) {
				// Larger blunting factors will reduce the effect of the class growths on the stat distribution.
				// For example: Knights have a 50% STR growth and 30% SPD growth by default.
				// With no blunting factor and only these two growths in the pool, the STR chance would be 50 / 80 (62.5%) while SPD is 30 / 80 (37.5%).
				// With a blunting factor of 50, this becomes 100 / 180 (55.5%) for STR and 80 / 180 (44.4%) for SPD.
				// Negative blunting factors increase the correlation with the class growths.
				int bluntingFactor = -10;
				
				distributor.addItem(FE9Data.StatArea.STR, Math.max(1, charClass.getSTRGrowth() + bluntingFactor));
				distributor.addItem(FE9Data.StatArea.MAG, Math.max(1, charClass.getMAGGrowth() + bluntingFactor));
				distributor.addItem(FE9Data.StatArea.SKL, Math.max(1, charClass.getSKLGrowth() + bluntingFactor));
				distributor.addItem(FE9Data.StatArea.SPD, Math.max(1, charClass.getSPDGrowth() + bluntingFactor));
				distributor.addItem(FE9Data.StatArea.DEF, Math.max(1, charClass.getDEFGrowth() + bluntingFactor));
				distributor.addItem(FE9Data.StatArea.RES, Math.max(1, charClass.getRESGrowth() + bluntingFactor));
			} else {
				distributor.addItem(FE9Data.StatArea.SKL, 10);
				distributor.addItem(FE9Data.StatArea.SPD, 10);
				distributor.addItem(FE9Data.StatArea.STR, 10);
				distributor.addItem(FE9Data.StatArea.MAG, 10);
				distributor.addItem(FE9Data.StatArea.DEF, 10);
				distributor.addItem(FE9Data.StatArea.RES, 10);
			}
			
			while (baseTotal > 0) {
				baseTotal -= 1;
				
				switch(distributor.getRandomItem(rng)) {
				case HP: hp += 1; break;
				case STR: str += 1; break;
				case MAG: mag += 1; break;
				case SKL: skl += 1; break;
				case SPD: spd += 1; break;
				case LCK: lck += 1; break;
				case DEF: def += 1; break;
				case RES: res += 1; break;
				}
			}
			
			int hpBase = hp - charClass.getBaseHP();
			int strBase = str - charClass.getBaseSTR();
			int magBase = mag - charClass.getBaseMAG();
			int sklBase = skl - charClass.getBaseSKL();
			int spdBase = spd - charClass.getBaseSPD();
			int lckBase = lck - charClass.getBaseLCK();
			int defBase = def - charClass.getBaseDEF();
			int resBase = res - charClass.getBaseRES();
			
			character.setBaseHP(WhyDoesJavaNotHaveThese.clamp(hpBase, charClass.getBaseHP() * -1 + 1, charClass.getMaxHP() - charClass.getBaseHP()));
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
			
			StatBias bias = classData.statBiasForClass(charClass);
			
			int characterSTR = character.getBaseSTR() + charClass.getBaseSTR();
			int characterMAG = character.getBaseMAG() + charClass.getBaseMAG();
			boolean swapSTRMAG = false;
			switch (bias) {
			case NONE:
				break;
			case LEAN_MAGICAL:
			case MAGICAL_ONLY:
				swapSTRMAG = characterSTR > characterMAG;
				break;
			case LEAN_PHYSICAL:
			case PHYSICAL_ONLY:
				swapSTRMAG = characterMAG > characterSTR;
				break;
			}
			if (swapSTRMAG) {
				int targetSTR = characterMAG;
				int targetMAG = characterSTR;
				
				character.setBaseSTR(targetSTR - charClass.getBaseSTR());
				character.setBaseMAG(targetMAG - charClass.getBaseMAG());
			}
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
