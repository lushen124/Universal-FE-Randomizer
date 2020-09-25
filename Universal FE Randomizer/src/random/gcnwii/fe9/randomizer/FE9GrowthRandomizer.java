package random.gcnwii.fe9.randomizer;

import java.util.Random;

import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader.StatBias;
import random.general.WeightedDistributor;
import util.WhyDoesJavaNotHaveThese;

public class FE9GrowthRandomizer {
	
	static final int rngSalt = 4892;
	
	private enum StatArea { HP, STR, MAG, SKL, SPD, LCK, DEF, RES; }
	
	public static void randomizeGrowthsByRedistribution(int variance, boolean adjustSTRMAG, boolean adjustHP, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, Random rng) {
		FE9Character[] characters = charData.allPlayableCharacters();
		for (FE9Character character : characters) {
			if (character.wasModified()) { continue; }
			
			int growthTotal = character.getHPGrowth() + character.getSTRGrowth() + character.getMAGGrowth() + character.getSKLGrowth() +
					character.getSPDGrowth() + character.getLCKGrowth() + character.getDEFGrowth() + character.getRESGrowth();
			
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				growthTotal += rng.nextInt(variance + 1);
			} else {
				growthTotal -= rng.nextInt(variance + 1);
			}
			
			int hpGrowth = 0;
			int strGrowth = 0;
			int magGrowth = 0;
			int sklGrowth = 0;
			int spdGrowth = 0;
			int lckGrowth = 0;
			int defGrowth = 0;
			int resGrowth = 0;
			
			String classID = charData.pointerLookup(character.getClassPointer());
			FE9Class charClass = classData.classWithID(classID);
			StatBias bias = classData.statBiasForClass(charClass);
			if (!adjustSTRMAG) {
				bias = StatBias.NONE;
			}
			WeightedDistributor<StatArea> distributor = new WeightedDistributor<StatArea>();
			switch (bias) {
			case NONE:
				distributor.addItem(StatArea.HP, adjustHP ? 3 : 1);
				distributor.addItem(StatArea.STR, adjustHP ? 2 : 1);
				distributor.addItem(StatArea.MAG, adjustHP ? 2 : 1);
				distributor.addItem(StatArea.SKL, adjustHP ? 2 : 1);
				distributor.addItem(StatArea.SPD, adjustHP ? 2 : 1);
				distributor.addItem(StatArea.LCK, adjustHP ? 2 : 1);
				distributor.addItem(StatArea.DEF, adjustHP ? 2 : 1);
				distributor.addItem(StatArea.RES, adjustHP ? 2 : 1);
				break;
			case PHYSICAL_ONLY:
				distributor.addItem(StatArea.HP, adjustHP ? 30 : 10);
				distributor.addItem(StatArea.STR, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.MAG, adjustHP ? 5 : 2);
				distributor.addItem(StatArea.SKL, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.SPD, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.LCK, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.DEF, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.RES, adjustHP ? 20 : 10);
				break;
			case MAGICAL_ONLY:
				distributor.addItem(StatArea.HP, adjustHP ? 30 : 10);
				distributor.addItem(StatArea.STR, adjustHP ? 5 : 2);
				distributor.addItem(StatArea.MAG, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.SKL, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.SPD, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.LCK, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.DEF, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.RES, adjustHP ? 20 : 10);
				break;
			case LEAN_MAGICAL:
				distributor.addItem(StatArea.HP, adjustHP ? 30 : 10);
				distributor.addItem(StatArea.STR, adjustHP ? 10 : 5);
				distributor.addItem(StatArea.MAG, adjustHP ? 15 : 8);
				distributor.addItem(StatArea.SKL, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.SPD, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.LCK, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.DEF, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.RES, adjustHP ? 20 : 10);
				break;
			case LEAN_PHYSICAL:
				distributor.addItem(StatArea.HP, adjustHP ? 30 : 10);
				distributor.addItem(StatArea.STR, adjustHP ? 15 : 8);
				distributor.addItem(StatArea.MAG, adjustHP ? 10 : 5);
				distributor.addItem(StatArea.SKL, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.SPD, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.LCK, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.DEF, adjustHP ? 20 : 10);
				distributor.addItem(StatArea.RES, adjustHP ? 20 : 10);
				break;
			}
			
			while (growthTotal > 0) {
				StatArea area = distributor.getRandomItem(rng);
				int value = Math.min(5, growthTotal);
				growthTotal -= value;
				switch (area) {
				case HP: hpGrowth += value; break;
				case STR: strGrowth += value; break;
				case MAG: magGrowth += value; break;
				case SKL: sklGrowth += value; break;
				case SPD: spdGrowth += value; break;
				case LCK: lckGrowth += value; break;
				case DEF: defGrowth += value; break;
				case RES: resGrowth += value; break;
				}
			}
			
			character.setHPGrowth(hpGrowth);
			character.setSTRGrowth(strGrowth);
			character.setMAGGrowth(magGrowth);
			character.setSKLGrowth(sklGrowth);
			character.setSPDGrowth(spdGrowth);
			character.setLCKGrowth(lckGrowth);
			character.setDEFGrowth(defGrowth);
			character.setRESGrowth(resGrowth);
		}
		
		charData.commit();
	}
	
	public static void randomizeGrowthsByDelta(int variance, boolean adjustSTRMAG, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, Random rng) {
		FE9Character[] characters = charData.allPlayableCharacters();
		for (FE9Character character : characters) {
			if (character.wasModified()) { continue; }
			
			int delta = rng.nextInt(variance + 1);
			int sign = rng.nextInt(2) == 0 ? 1 : -1;
			character.setHPGrowth(WhyDoesJavaNotHaveThese.clamp(character.getHPGrowth() + sign * delta, 0, 255));
			
			delta = rng.nextInt(variance + 1);
			sign = rng.nextInt(2) == 0 ? 1 : -1;
			character.setSTRGrowth(WhyDoesJavaNotHaveThese.clamp(character.getSTRGrowth() + sign * delta, 0, 255));
			
			delta = rng.nextInt(variance + 1);
			sign = rng.nextInt(2) == 0 ? 1 : -1;
			character.setMAGGrowth(WhyDoesJavaNotHaveThese.clamp(character.getMAGGrowth() + sign * delta, 0, 255));
			
			delta = rng.nextInt(variance + 1);
			sign = rng.nextInt(2) == 0 ? 1 : -1;
			character.setSKLGrowth(WhyDoesJavaNotHaveThese.clamp(character.getSKLGrowth() + sign * delta, 0, 255));
			
			delta = rng.nextInt(variance + 1);
			sign = rng.nextInt(2) == 0 ? 1 : -1;
			character.setSPDGrowth(WhyDoesJavaNotHaveThese.clamp(character.getSPDGrowth() + sign * delta, 0, 255));
			
			delta = rng.nextInt(variance + 1);
			sign = rng.nextInt(2) == 0 ? 1 : -1;
			character.setLCKGrowth(WhyDoesJavaNotHaveThese.clamp(character.getLCKGrowth() + sign * delta, 0, 255));
			
			delta = rng.nextInt(variance + 1);
			sign = rng.nextInt(2) == 0 ? 1 : -1;
			character.setDEFGrowth(WhyDoesJavaNotHaveThese.clamp(character.getDEFGrowth() + sign * delta, 0, 255));
			
			delta = rng.nextInt(variance + 1);
			sign = rng.nextInt(2) == 0 ? 1 : -1;
			character.setRESGrowth(WhyDoesJavaNotHaveThese.clamp(character.getRESGrowth() + sign * delta, 0, 255));
			
			if (adjustSTRMAG) {
				FE9Class charClass = classData.classWithID(charData.getJIDForCharacter(character));
				StatBias bias = classData.statBiasForClass(charClass);
				
				int strGrowth = character.getSTRGrowth();
				int magGrowth = character.getMAGGrowth();
				
				switch (bias) {
				case NONE: break;
				case LEAN_PHYSICAL:
				case PHYSICAL_ONLY:
					character.setSTRGrowth(Math.max(strGrowth, magGrowth));
					character.setMAGGrowth(Math.min(strGrowth, magGrowth));
					break;
				case LEAN_MAGICAL:
				case MAGICAL_ONLY:
					character.setSTRGrowth(Math.min(strGrowth, magGrowth));
					character.setMAGGrowth(Math.max(strGrowth, magGrowth));
					break;
				}
			}
		}
		
		charData.commit();
	}

	public static void randomizeGrowthsFully(int minimum, int maximum, boolean adjustHP, boolean adjustSTRMAGSplit, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, Random rng) {
		FE9Character[] characters = charData.allPlayableCharacters();
		
		int range = maximum - minimum + 1;
		
		for (FE9Character character : characters) {
			if (character.wasModified()) { continue; }
			
			if (adjustHP) { // Keep the HP growth in the upper half of the range.
				int offset = (range > 2 ? rng.nextInt(range / 2) : 0);
				character.setHPGrowth(minimum + (range / 2) + offset);
			} else {
				character.setHPGrowth(minimum + rng.nextInt(range));
			}
			
			int powGrowth1 = minimum + rng.nextInt(range);
			int powGrowth2 = minimum + rng.nextInt(range);
			
			if (adjustSTRMAGSplit) {
				String classID = charData.pointerLookup(character.getClassPointer());
				FE9Class charClass = classData.classWithID(classID);
				StatBias bias = classData.statBiasForClass(charClass);
				switch (bias) {
				case NONE: 
					character.setSTRGrowth(powGrowth1); 
					character.setMAGGrowth(powGrowth2); 
					break;
				case PHYSICAL_ONLY:
				case LEAN_PHYSICAL:
					character.setSTRGrowth(Math.max(powGrowth1, powGrowth2));
					character.setMAGGrowth(Math.min(powGrowth1, powGrowth2));
					break;
				case MAGICAL_ONLY:
				case LEAN_MAGICAL:
					character.setSTRGrowth(Math.min(powGrowth1, powGrowth2));
					character.setMAGGrowth(Math.max(powGrowth1, powGrowth2));
					break;
				}
			} else {
				character.setSTRGrowth(powGrowth1);
				character.setMAGGrowth(powGrowth2);
			}
			
			character.setSKLGrowth(minimum + rng.nextInt(range));
			character.setSPDGrowth(minimum + rng.nextInt(range));
			character.setLCKGrowth(minimum + rng.nextInt(range));
			character.setDEFGrowth(minimum + rng.nextInt(range));
			character.setRESGrowth(minimum + rng.nextInt(range));
		}
		
		charData.commit();
	}
}
