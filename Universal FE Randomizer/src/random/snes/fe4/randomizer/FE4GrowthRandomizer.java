package random.snes.fe4.randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4StaticCharacter;
import random.general.WeightedDistributor;
import random.snes.fe4.loader.CharacterDataLoader;

public class FE4GrowthRandomizer {
	
	static final int rngSalt = 888;
	
	static final int GrowthCap = 85;
	
	enum StatArea {
		HP, STR, MAG, SKL, SPD, LCK, DEF, RES;
	}
	
	public static void randomizeGrowthsByRedistribution(int variance, boolean adjustHPGrowths, boolean adjustSTRMAGByClass, CharacterDataLoader charData, Random rng) {
		List<FE4StaticCharacter> allChars = new ArrayList<FE4StaticCharacter>();
		allChars.addAll(charData.getGen1Characters());
		allChars.addAll(charData.getGen2CommonCharacters());
		allChars.addAll(charData.getGen2SubstituteCharacters());
		
		for (FE4StaticCharacter staticChar : allChars) {
			
			if (staticChar.wasModified()) {
				continue;
			}
			
			int growthTotal = staticChar.getHPGrowth() + staticChar.getSTRGrowth() + staticChar.getMAGGrowth() + staticChar.getSKLGrowth() + 
					staticChar.getSPDGrowth() + staticChar.getLCKGrowth() + staticChar.getDEFGrowth() + staticChar.getRESGrowth();
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				growthTotal += rng.nextInt(variance + 1);
			} else {
				growthTotal -= rng.nextInt(variance + 1);
			}
			
			int newHPGrowth = 0;
			int newSTRGrowth = 0;
			int newMAGGrowth = 0;
			int newSKLGrowth = 0;
			int newSPDGrowth = 0;
			int newLCKGrowth = 0;
			int newDEFGrowth = 0;
			int newRESGrowth = 0;
			
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			boolean weightSTR = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsStrength() : false;
			boolean weightMAG = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsMagic() : false;
			
			WeightedDistributor<StatArea> distributor = new WeightedDistributor<StatArea>();
			distributor.addItem(StatArea.HP, adjustHPGrowths ? 10 : 6);
			distributor.addItem(StatArea.SKL, 4);
			distributor.addItem(StatArea.SPD, 4);
			distributor.addItem(StatArea.LCK, 4);
			distributor.addItem(StatArea.DEF, 4);
			distributor.addItem(StatArea.RES, 4);
			
			if (weightSTR && !weightMAG) {
				distributor.addItem(StatArea.STR, 4);
				distributor.addItem(StatArea.MAG, 2);
			} else if (weightMAG && !weightSTR) {
				distributor.addItem(StatArea.STR, 2);
				distributor.addItem(StatArea.MAG, 4);
			} else {
				distributor.addItem(StatArea.STR, 4);
				distributor.addItem(StatArea.MAG, 4);
			}
			
			while (growthTotal > 0) {
				int amount = Math.min(5, growthTotal);
				
				if (newHPGrowth + amount >= GrowthCap && newSTRGrowth + amount >= GrowthCap && newMAGGrowth + amount >= GrowthCap && newSKLGrowth + amount >= GrowthCap &&
						newSPDGrowth + amount >= GrowthCap && newDEFGrowth + amount >= GrowthCap && newRESGrowth + amount >= GrowthCap && newLCKGrowth + amount >= GrowthCap) { break; }
				
				StatArea area = distributor.getRandomItem(rng);
				switch (area) {
				case HP:
					if (newHPGrowth + amount <= GrowthCap) { newHPGrowth += amount; }
					else { continue; }
					break;
				case STR:
					if (newSTRGrowth + amount <= GrowthCap) { newSTRGrowth += amount; }
					else { continue; }
					break;
				case MAG:
					if (newMAGGrowth + amount <= GrowthCap) { newMAGGrowth += amount; }
					else { continue; }
					break;
				case SKL:
					if (newSKLGrowth + amount <= GrowthCap) { newSKLGrowth += amount; }
					else { continue; }
					break;
				case SPD:
					if (newSPDGrowth + amount <= GrowthCap) { newSPDGrowth += amount; }
					else { continue; }
					break;
				case LCK:
					if (newLCKGrowth + amount <= GrowthCap) { newLCKGrowth += amount; }
					else { continue; }
					break;
				case DEF:
					if (newDEFGrowth + amount <= GrowthCap) { newDEFGrowth += amount; }
					else { continue; }
					break;
				case RES:
					if (newRESGrowth + amount <= GrowthCap) { newRESGrowth += amount; }
					else { continue; }
					break;
				}
				
				growthTotal -= amount;
			}
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				FE4StaticCharacter targetChar = charData.getStaticCharacter(linked);
				if (targetChar != null) {
					targetChar.setHPGrowth(newHPGrowth);
					targetChar.setSTRGrowth(newSTRGrowth);
					targetChar.setMAGGrowth(newMAGGrowth);
					targetChar.setSKLGrowth(newSKLGrowth);
					targetChar.setSPDGrowth(newSPDGrowth);
					targetChar.setLCKGrowth(newLCKGrowth);
					targetChar.setDEFGrowth(newDEFGrowth);
					targetChar.setRESGrowth(newRESGrowth);
				}
			}
		}
	}
	
	public static void randomizeGrowthsByRandomDelta(int maxDelta, boolean adjustHPGrowths, boolean adjustSTRMAGByClass, CharacterDataLoader charData, Random rng) {
		List<FE4StaticCharacter> allChars = new ArrayList<FE4StaticCharacter>();
		allChars.addAll(charData.getGen1Characters());
		allChars.addAll(charData.getGen2CommonCharacters());
		allChars.addAll(charData.getGen2SubstituteCharacters());
		
		for (FE4StaticCharacter staticChar : allChars) {
			if (staticChar.wasModified()) {
				continue;
			}
			
			int newHPGrowth = staticChar.getHPGrowth();
			int newSTRGrowth = staticChar.getSTRGrowth();
			int newMAGGrowth = staticChar.getMAGGrowth();
			int newSKLGrowth = staticChar.getSKLGrowth();
			int newSPDGrowth = staticChar.getSPDGrowth();
			int newLCKGrowth = staticChar.getLCKGrowth();
			int newDEFGrowth = staticChar.getDEFGrowth();
			int newRESGrowth = staticChar.getRESGrowth();
			
			FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			boolean weightSTR = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsStrength() : false;
			boolean weightMAG = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsMagic() : false;
			
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newHPGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
			} else {
				newHPGrowth -= rng.nextInt(maxDelta / 5 + 1) * 5;
				if (adjustHPGrowths) {
					newHPGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
				}
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newSTRGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
			} else {
				newSTRGrowth -= rng.nextInt(maxDelta / 5 + 1) * 5;
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newMAGGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
			} else {
				newMAGGrowth -= rng.nextInt(maxDelta / 5 + 1) * 5;
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newSKLGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
			} else {
				newSKLGrowth -= rng.nextInt(maxDelta / 5 + 1) * 5;
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newSPDGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
			} else {
				newSPDGrowth -= rng.nextInt(maxDelta / 5 + 1) * 5;
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newLCKGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
			} else {
				newLCKGrowth -= rng.nextInt(maxDelta / 5 + 1) * 5;
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newDEFGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
			} else {
				newDEFGrowth -= rng.nextInt(maxDelta / 5 + 1) * 5;
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newRESGrowth += rng.nextInt(maxDelta / 5 + 1) * 5;
			} else {
				newRESGrowth -= rng.nextInt(maxDelta / 5 + 1) * 5;
			}
			
			if ((weightSTR && !weightMAG && newSTRGrowth < newMAGGrowth) || (weightMAG && !weightSTR && newMAGGrowth < newSTRGrowth)) {
				int oldSTR = newSTRGrowth;
				newSTRGrowth = newMAGGrowth;
				newMAGGrowth = oldSTR;
			}
			
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				FE4StaticCharacter character = charData.getStaticCharacter(linked);
				character.setHPGrowth(Math.min(GrowthCap, Math.max(0, newHPGrowth)));
				character.setSTRGrowth(Math.min(GrowthCap, Math.max(0, newSTRGrowth)));
				character.setMAGGrowth(Math.min(GrowthCap, Math.max(0, newMAGGrowth)));
				character.setSKLGrowth(Math.min(GrowthCap, Math.max(0, newSKLGrowth)));
				character.setSPDGrowth(Math.min(GrowthCap, Math.max(0, newSPDGrowth)));
				character.setLCKGrowth(Math.min(GrowthCap, Math.max(0, newLCKGrowth)));
				character.setDEFGrowth(Math.min(GrowthCap, Math.max(0, newDEFGrowth)));
				character.setRESGrowth(Math.min(GrowthCap, Math.max(0, newRESGrowth)));
			}
		}
	}

	public static void fullyRandomizeGrowthsWithRange(int minGrowth, int maxGrowth, boolean adjustHPGrowths, boolean adjustSTRMAGByClass, CharacterDataLoader charData, Random rng) {
		List<FE4StaticCharacter> allChars = new ArrayList<FE4StaticCharacter>();
		allChars.addAll(charData.getGen1Characters());
		allChars.addAll(charData.getGen2CommonCharacters());
		allChars.addAll(charData.getGen2SubstituteCharacters());
		
		for (FE4StaticCharacter staticChar : allChars) {
			if (staticChar.wasModified()) {
				continue;
			}
		
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			FE4Data.CharacterClass fe4CharClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			boolean weightSTR = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsStrength() : false;
			boolean weightMAG = adjustSTRMAGByClass ? fe4CharClass.primaryAttackIsMagic() : false;
			
			int range = maxGrowth - minGrowth;
			
			int newHPGrowth = range != 0 ? rng.nextInt(range) : 0 + minGrowth;
			int newSTRGrowth = range != 0 ? rng.nextInt(range) : 0 + minGrowth;
			int newMAGGrowth = range != 0 ? rng.nextInt(range) : 0 + minGrowth;
			int newSKLGrowth = range != 0 ? rng.nextInt(range) : 0 + minGrowth;
			int newSPDGrowth = range != 0 ? rng.nextInt(range) : 0 + minGrowth;
			int newLCKGrowth = range != 0 ? rng.nextInt(range) : 0 + minGrowth;
			int newDEFGrowth = range != 0 ? rng.nextInt(range) : 0 + minGrowth;
			int newRESGrowth = range != 0 ? rng.nextInt(range) : 0 + minGrowth;
			
			if (adjustHPGrowths) {
				int threshold = range / 2 + minGrowth; // This ensures the HP is always in the upper half of the range specified.
				if (newHPGrowth < threshold) {
					if (newHPGrowth + threshold <= maxGrowth) {
						newHPGrowth += range / 2;
					} else {
						newHPGrowth = maxGrowth;
					}
				}
			}
			
			if ((weightSTR && !weightMAG && newSTRGrowth < newMAGGrowth) ||
					(weightMAG && !weightSTR && newMAGGrowth < newSTRGrowth)) {
				int oldSTRGrowth = newSTRGrowth;
				newSTRGrowth = newMAGGrowth;
				newMAGGrowth = oldSTRGrowth;
			}
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				FE4StaticCharacter targetChar = charData.getStaticCharacter(linked);
				if (targetChar != null) {
					targetChar.setHPGrowth(newHPGrowth);
					targetChar.setSTRGrowth(newSTRGrowth);
					targetChar.setMAGGrowth(newMAGGrowth);
					targetChar.setSKLGrowth(newSKLGrowth);
					targetChar.setSPDGrowth(newSPDGrowth);
					targetChar.setLCKGrowth(newLCKGrowth);
					targetChar.setDEFGrowth(newDEFGrowth);
					targetChar.setRESGrowth(newRESGrowth);
				}
			}
		}
	}
}
