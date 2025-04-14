package random.gba.randomizer;

import java.util.List;
import java.util.Random;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEStatDto;
import fedata.gba.GBAFEStatDto.Stat;
import fedata.gba.general.GBAFEChapterMetadataData;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.general.NormalDistributor;
import util.WhyDoesJavaNotHaveThese;

public class BasesRandomizer {
	
	public static int rngSalt = 9001;
	
	public static void smartRandomizeBases(CharacterDataLoader charactersData, ClassDataLoader classData, ChapterLoader chapterData, Random rng) {
		GBAFECharacterData[] allPlayableCharacters = charactersData.playableCharacters();
		// Character data contains a level, but it's also sometimes wrong compared to the chapter data's level, so we will use that instead.
		for (GBAFECharacterData character : allPlayableCharacters) {
			int startingLevel = chapterData.getStartingLevelForCharacter(character.getID());
			if (startingLevel == 0) {
				// In the rare case we can't find a level for the character, use the level on the character data.
				startingLevel = character.getLevel();
			}
			
			GBAFEClassData characterClass = classData.classForID(character.getClassID());
			Boolean isPromoted = classData.isPromotedClass(character.getClassID());
			if (isPromoted) {
				// Promoted class bases are kind of bad, so add a few levels here.
				startingLevel += 10;
			}
			GBAFEStatDto classBaseline = new GBAFEStatDto(characterClass.getBases());
			// Use a mix of the class's bases and the character's growths (character growths are randomized before this step, so they may not be in line
			// with the class).
			GBAFEStatDto characterBaseline = GBAFEStatDto.expectedValueLevel(classBaseline, character.getGrowths(), startingLevel - 1, rng);
			
			// Now we add a modifier based on how a character rolls.
			// Class Base HP is pretty bad all around, so we'll skew this upward with a range between 0 and characters level or +5, whichever is greater.
			// So a level 1 character will get a bonus of 0 ~ 5 and a level 10 character will get a bonus of 0 ~ 10
			NormalDistributor hpDistributor = new NormalDistributor(0, Math.max(startingLevel, 5), 1);
			// Most normal stats can range from -2 to +4
			NormalDistributor statDistributor = new NormalDistributor(-2, 4, 1);
			if (isPromoted) {
				// Promoted class bases are so bad, we are going to shift the stat distributor for most stats upward to compensate.
				statDistributor = new NormalDistributor(2, 8, 1);
			}
			// LCK is an absolute random.
			// If the unit is unpromoted, this ranges from 0 to the higher of their level or 10.
			// i.e. A level 1 unit will have LCK between 0 and 10.
			//      A level 10 unit will have LCK between 0 and 10.
			//      A level 15 unit will have LCK between 0 and 15.
			// If the unit is promoted, this ranges from 2 to the higher of their level + 15 or 20.
			// i.e. A level 1 promoted unit will have LCK between 2 and 20.
			//      A level 10 promoted unit will have LCK between 2 and 25.
			//      A level 15 promoted unit will have LCK between 2 and 30.
			NormalDistributor lckDistributor = new NormalDistributor(isPromoted ? 2 : 0, Math.max(startingLevel + (isPromoted ? 5 : 0), (isPromoted ? 20 : 10)), 1);
			
			List<GBAFEStatDto.Stat> statOrder = characterClass.getGrowthStatOrder(true);
			statOrder.remove(Stat.HP); // HP excluded
			statOrder.remove(Stat.LCK); // LCK also excluded
			
			int hpOffset = hpDistributor.getRandomValue(rng, NormalDistributor.allBuckets);
			// For most stats, a character can roll from all buckets except for the class's "best" stat, which cannot roll abysmal.
			int strOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.POW) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
			int sklOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.SKL) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
			int spdOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.SPD) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
			int defOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.DEF) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
			int resOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.RES) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
			int totalLck = lckDistributor.getRandomValue(rng, NormalDistributor.allBuckets);
			
			// If our total offsets are too bad (mostly bad and abysmal) then reroll.
			while (strOffset + sklOffset + spdOffset + defOffset + resOffset < -5) {
				strOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.POW) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
				sklOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.SKL) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
				spdOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.SPD) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
				defOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.DEF) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
				resOffset = statDistributor.getRandomValue(rng, statOrder.indexOf(Stat.RES) == 0 ? NormalDistributor.notAbysmal : NormalDistributor.allBuckets);
			}
			
			characterBaseline.add(new GBAFEStatDto(hpOffset, strOffset, sklOffset, spdOffset, defOffset, resOffset, 0));
			characterBaseline.lck = totalLck;
			characterBaseline.clamp(GBAFEStatDto.MINIMUM_STATS, characterClass.getCaps());
			characterBaseline.subtract(characterClass.getBases());
			character.setBases(characterBaseline);
		}
		
		charactersData.commit();
	}
	
	public static void randomizeBasesByRedistribution(int variance, CharacterDataLoader charactersData, ClassDataLoader classData, Random rng) {
		GBAFECharacterData[] allPlayableCharacters = charactersData.playableCharacters();
		for (GBAFECharacterData character : allPlayableCharacters) {
			int baseTotal = character.getBaseHP() + character.getBaseSTR() + character.getBaseSKL() + character.getBaseSPD() + character.getBaseDEF() +
					character.getBaseRES() + character.getBaseLCK();
			
			int classID = character.getClassID();
			GBAFEClassData charClass = classData.classForID(classID);
			
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				baseTotal += rng.nextInt(variance + 1);
			} else {
				baseTotal -= rng.nextInt(variance + 1);
			}
			
			int newHPBase = 0;
			int newSTRBase = 0;
			int newSKLBase = 0;
			int newSPDBase = 0;
			int newLCKBase = 0;
			int newDEFBase = 0;
			int newRESBase = 0;
			
			int initialLuck = rng.nextInt(4) + rng.nextInt(4);
			newLCKBase += initialLuck;
			baseTotal -= initialLuck;
			if (baseTotal < 0) {
				baseTotal = 0;
			}
			
			if (baseTotal > 0) {	
				do {
					randomNum = rng.nextInt(9);
					int amount = rng.nextInt(3) + 1;
					
					switch (randomNum) {
					case 0:
					case 5:
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
					case 8:
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
				} while (baseTotal > 0);
			}
			
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
	
	public static void randomizeBasesByRandomDelta(int maxDelta, CharacterDataLoader charactersData, ClassDataLoader classData, Random rng) {
		GBAFECharacterData[] allPlayableCharacters = charactersData.playableCharacters();
		for (GBAFECharacterData character : allPlayableCharacters) {
			
			int classID = character.getClassID();
			GBAFEClassData charClass = classData.classForID(classID);
			
			int newHPBase = character.getBaseHP();
			int newSTRBase = character.getBaseSTR();
			int newSKLBase = character.getBaseSKL();
			int newSPDBase = character.getBaseSPD();
			int newLCKBase = character.getBaseLCK();
			int newDEFBase = character.getBaseDEF();
			int newRESBase = character.getBaseRES();
			
			int randomNum = rng.nextInt(2);
			int multiplier = 1;
			if (randomNum == 0) {
				multiplier = -1;
			}
			newHPBase = WhyDoesJavaNotHaveThese.clamp(rng.nextInt(maxDelta + 1) * multiplier + newHPBase, 
					-1 * charClass.getBaseHP(), charClass.getMaxHP() - charClass.getBaseHP());
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newSTRBase = WhyDoesJavaNotHaveThese.clamp(rng.nextInt(maxDelta + 1) * multiplier + newSTRBase, 
					-1 * charClass.getBaseSTR(), charClass.getMaxSTR() - charClass.getBaseSTR());
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newSKLBase = WhyDoesJavaNotHaveThese.clamp(rng.nextInt(maxDelta + 1) * multiplier + newSKLBase, 
					-1 * charClass.getBaseSKL(), charClass.getMaxSKL() - charClass.getBaseSKL());
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newSPDBase = WhyDoesJavaNotHaveThese.clamp(rng.nextInt(maxDelta + 1) * multiplier + newSPDBase, 
					-1 * charClass.getBaseSPD(), charClass.getMaxSPD() - charClass.getBaseSPD());
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newLCKBase = WhyDoesJavaNotHaveThese.clamp(rng.nextInt(maxDelta + 1) * multiplier + newLCKBase, 
					-1 * charClass.getBaseLCK(), charClass.getMaxLCK() - charClass.getBaseLCK());
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newDEFBase = WhyDoesJavaNotHaveThese.clamp(rng.nextInt(maxDelta + 1) * multiplier + newDEFBase, 
					-1 * charClass.getBaseDEF(), charClass.getMaxDEF() - charClass.getBaseDEF());
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			newRESBase = WhyDoesJavaNotHaveThese.clamp(rng.nextInt(maxDelta + 1) * multiplier + newRESBase, 
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
