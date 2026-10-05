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
	
	public static void randomizeGrowthsByRedistribution(int variance, int min, int max, boolean adjustHPGrowths, boolean adjustSTRMAGByClass, CharacterDataLoader charData, Random rng) {
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
			
			int newHPGrowth = min;
			int newSTRGrowth = min;
			int newMAGGrowth = min;
			int newSKLGrowth = min;
			int newSPDGrowth = min;
			int newLCKGrowth = min;
			int newDEFGrowth = min;
			int newRESGrowth = min;
			
			growthTotal -= min * 8;
			
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
			
			int effectiveMax = Math.min(max, GrowthCap);
			
			while (growthTotal > 0) {
				int amount = Math.min(5, growthTotal);
				
				if (newHPGrowth + amount >= effectiveMax && newSTRGrowth + amount >= effectiveMax && newMAGGrowth + amount >= effectiveMax && newSKLGrowth + amount >= effectiveMax &&
						newSPDGrowth + amount >= effectiveMax && newDEFGrowth + amount >= effectiveMax && newRESGrowth + amount >= effectiveMax && newLCKGrowth + amount >= effectiveMax) { break; }
				
				StatArea area = distributor.getRandomItem(rng);
				switch (area) {
				case HP:
					if (newHPGrowth + amount <= effectiveMax) { newHPGrowth += amount; }
					else { continue; }
					break;
				case STR:
					if (newSTRGrowth + amount <= effectiveMax) { newSTRGrowth += amount; }
					else { continue; }
					break;
				case MAG:
					if (newMAGGrowth + amount <= effectiveMax) { newMAGGrowth += amount; }
					else { continue; }
					break;
				case SKL:
					if (newSKLGrowth + amount <= effectiveMax) { newSKLGrowth += amount; }
					else { continue; }
					break;
				case SPD:
					if (newSPDGrowth + amount <= effectiveMax) { newSPDGrowth += amount; }
					else { continue; }
					break;
				case LCK:
					if (newLCKGrowth + amount <= effectiveMax) { newLCKGrowth += amount; }
					else { continue; }
					break;
				case DEF:
					if (newDEFGrowth + amount <= effectiveMax) { newDEFGrowth += amount; }
					else { continue; }
					break;
				case RES:
					if (newRESGrowth + amount <= effectiveMax) { newRESGrowth += amount; }
					else { continue; }
					break;
				}
				
				growthTotal -= amount;
			}
			
			if (growthTotal > 0) {
				newHPGrowth = effectiveMax;
				newSTRGrowth = effectiveMax;
				newMAGGrowth = effectiveMax;
				newSKLGrowth = effectiveMax;
				newSPDGrowth = effectiveMax;
				newLCKGrowth = effectiveMax;
				newDEFGrowth = effectiveMax;
				newRESGrowth = effectiveMax;
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
	
	public static void randomizeGrowthsByRandomDelta(int maxDelta, int min, int max, boolean adjustHPGrowths, boolean adjustSTRMAGByClass, CharacterDataLoader charData, Random rng) {
		List<FE4StaticCharacter> allChars = new ArrayList<FE4StaticCharacter>();
		allChars.addAll(charData.getGen1Characters());
		allChars.addAll(charData.getGen2CommonCharacters());
		allChars.addAll(charData.getGen2SubstituteCharacters());
		
		int effectiveMax = Math.min(max, GrowthCap);
		
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
			if ((randomNum == 0 && newHPGrowth < effectiveMax) || adjustHPGrowths) {
				newHPGrowth += Math.min(effectiveMax - newHPGrowth + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			} else if (newHPGrowth > min) {
				newHPGrowth -= Math.min(newHPGrowth - min + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0 && newSTRGrowth < effectiveMax) {
				newSTRGrowth += Math.min(effectiveMax - newSTRGrowth + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			} else if (newSTRGrowth > min) {
				newSTRGrowth -= Math.min(newSTRGrowth - min + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0 && newMAGGrowth < effectiveMax) {
				newMAGGrowth += Math.min(effectiveMax - newMAGGrowth + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			} else if (newMAGGrowth > min) {
				newMAGGrowth -= Math.min(newMAGGrowth - min + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0 && newSKLGrowth < effectiveMax) {
				newSKLGrowth += Math.min(effectiveMax - newSKLGrowth + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			} else if (newSKLGrowth > min) {
				newSKLGrowth -= Math.min(newSKLGrowth - min + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0 && newSPDGrowth < effectiveMax) {
				newSPDGrowth += Math.min(effectiveMax - newSPDGrowth + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			} else if (newSPDGrowth > min) {
				newSPDGrowth -= Math.min(newSPDGrowth - min + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0 && newLCKGrowth < effectiveMax) {
				newLCKGrowth += Math.min(effectiveMax - newLCKGrowth + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			} else if (newLCKGrowth > min) {
				newLCKGrowth -= Math.min(newLCKGrowth - min + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0 && newDEFGrowth < effectiveMax) {
				newDEFGrowth += Math.min(effectiveMax - newDEFGrowth + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			} else if (newDEFGrowth > min) {
				newDEFGrowth -= Math.min(newDEFGrowth - min + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			}
			
			randomNum = rng.nextInt(2);
			if (randomNum == 0 && newRESGrowth < effectiveMax) {
				newRESGrowth += Math.min(effectiveMax - newRESGrowth + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			} else if (newRESGrowth > min) {
				newRESGrowth -= Math.min(newRESGrowth - min + 1, rng.nextInt(maxDelta / 5 + 1) * 5);
			}
			
			if ((weightSTR && !weightMAG && newSTRGrowth < newMAGGrowth) || (weightMAG && !weightSTR && newMAGGrowth < newSTRGrowth)) {
				int oldSTR = newSTRGrowth;
				newSTRGrowth = newMAGGrowth;
				newMAGGrowth = oldSTR;
			}
			
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				FE4StaticCharacter character = charData.getStaticCharacter(linked);
				character.setHPGrowth(Math.min(effectiveMax, Math.max(min, newHPGrowth)));
				character.setSTRGrowth(Math.min(effectiveMax, Math.max(min, newSTRGrowth)));
				character.setMAGGrowth(Math.min(effectiveMax, Math.max(min, newMAGGrowth)));
				character.setSKLGrowth(Math.min(effectiveMax, Math.max(min, newSKLGrowth)));
				character.setSPDGrowth(Math.min(effectiveMax, Math.max(min, newSPDGrowth)));
				character.setLCKGrowth(Math.min(effectiveMax, Math.max(min, newLCKGrowth)));
				character.setDEFGrowth(Math.min(effectiveMax, Math.max(min, newDEFGrowth)));
				character.setRESGrowth(Math.min(effectiveMax, Math.max(min, newRESGrowth)));
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
			
			int newHPGrowth = (range != 0 ? rng.nextInt(range) : 0) + minGrowth;
			int newSTRGrowth = (range != 0 ? rng.nextInt(range) : 0) + minGrowth;
			int newMAGGrowth = (range != 0 ? rng.nextInt(range) : 0) + minGrowth;
			int newSKLGrowth = (range != 0 ? rng.nextInt(range) : 0) + minGrowth;
			int newSPDGrowth = (range != 0 ? rng.nextInt(range) : 0) + minGrowth;
			int newLCKGrowth = (range != 0 ? rng.nextInt(range) : 0) + minGrowth;
			int newDEFGrowth = (range != 0 ? rng.nextInt(range) : 0) + minGrowth;
			int newRESGrowth = (range != 0 ? rng.nextInt(range) : 0) + minGrowth;
			
			if (adjustHPGrowths) {
				int threshold = range / 2 + minGrowth; // This ensures the HP is always in the upper half of the range specified.
				if (newHPGrowth < threshold) {
					if (newHPGrowth + range / 2 <= maxGrowth) {
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
