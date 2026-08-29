package random.snes.fe4.randomizer;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4Data.Skill;
import fedata.snes.fe4.FE4StaticCharacter;
import random.general.PoolDistributor;
import random.general.WeightedDistributor;
import random.snes.fe4.loader.CharacterDataLoader;
import ui.model.fe4.SkillsOptions;
import ui.model.WeightedOptions;
import util.DebugPrinter;

import java.util.*;

public class FE4SkillsRandomizer {
	
	static final int rngSalt = 2468;
	
	public static void shufflePlayableCharacterSkills(SkillsOptions options, CharacterDataLoader charData, Random rng) {
		if (options.separatePoolsByGeneration) {
			// Collect all of the skills from all characters. Go ahead and wipe them at the same time so we don't have to iterate it again.
			PoolDistributor<FE4Data.Skill> universalPool = new PoolDistributor<FE4Data.Skill>();
			PoolDistributor<FE4Data.Character> characterPool = new PoolDistributor<FE4Data.Character>();
			Map<FE4Data.Character, List<FE4Data.Skill>> assignedSkills = new HashMap<FE4Data.Character, List<FE4Data.Skill>>();
			Map<FE4Data.Character, FE4StaticCharacter> characterMap = new HashMap<FE4Data.Character, FE4StaticCharacter>();
			
			// Gen 1
			List<FE4StaticCharacter> gen1Characters = charData.getGen1Characters();
			for (FE4StaticCharacter staticChar : gen1Characters) {
				FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
				if (fe4Char.linkedCharacters()[0] != fe4Char) {
					continue;
				}
				
				List<FE4Data.SkillSlot1> slot1Skills = FE4Data.SkillSlot1.slot1Skills(staticChar.getSkillSlot1Value());
				List<FE4Data.SkillSlot2> slot2Skills = FE4Data.SkillSlot2.slot2Skills(staticChar.getSkillSlot2Value());
				List<FE4Data.SkillSlot3> slot3Skills = FE4Data.SkillSlot3.slot3Skills(staticChar.getSkillSlot3Value());
				
				for (FE4Data.SkillSlot1 slot1 : slot1Skills) { universalPool.addItem(slot1.generalSkill()); characterPool.addItem(fe4Char); }
				for (FE4Data.SkillSlot2 slot2 : slot2Skills) { universalPool.addItem(slot2.generalSkill()); characterPool.addItem(fe4Char); }
				for (FE4Data.SkillSlot3 slot3 : slot3Skills) { universalPool.addItem(slot3.generalSkill()); characterPool.addItem(fe4Char); }

				characterMap.put(fe4Char, staticChar);
			}
			
			while (!universalPool.possibleResults().isEmpty()) {
				FE4Data.Skill skill = universalPool.getRandomItem(rng, true);
				FE4Data.Character recipient = characterPool.getRandomItem(rng, false);
				List<FE4Data.Skill> skillList = assignedSkills.get(recipient);
				if (skillList == null) {
					skillList = new ArrayList<FE4Data.Skill>();
					assignedSkills.put(recipient, skillList);
				}
				
				while (skillList.contains(skill)) {
					recipient = characterPool.getRandomItem(rng, false);
					skillList = assignedSkills.get(recipient);
					if (recipient.mustLoseToCharacters().length > 0 && skill == FE4Data.Skill.NIHIL) {
						continue; // Nobody that has to lose can have Nihil as a skill.
					}
					if (skillList == null) {
						skillList = new ArrayList<FE4Data.Skill>();
						assignedSkills.put(recipient, skillList);
					}
				}
				
				skillList.add(skill);
				if (options.retainNumberOfSkills) {
					characterPool.removeItem(recipient, false);
				}
				
				// Cap skills at 4 per character.
				else if (skillList.size() >= 4) {
					characterPool.removeItem(recipient, true);
				}
			}
			
			// Apply the assigned skills.
			for (FE4Data.Character fe4Char : assignedSkills.keySet()) {
				List<FE4Data.Skill> skills = assignedSkills.get(fe4Char);
				
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					FE4StaticCharacter staticChar = characterMap.get(linked);
					if (staticChar == null) { staticChar = charData.getStaticCharacter(linked); }
					if (staticChar == null) { continue; }
					
					List<FE4Data.SkillSlot1> slot1Skills = new ArrayList<FE4Data.SkillSlot1>();
					List<FE4Data.SkillSlot2> slot2Skills = new ArrayList<FE4Data.SkillSlot2>();
					List<FE4Data.SkillSlot3> slot3Skills = new ArrayList<FE4Data.SkillSlot3>();
					
					for (FE4Data.Skill skill : skills) {
						if (skill.slot() == 1) { slot1Skills.add(FE4Data.SkillSlot1.skill(skill)); }
						else if (skill.slot() == 2) { slot2Skills.add(FE4Data.SkillSlot2.skill(skill)); }
						else if (skill.slot() == 3) { slot3Skills.add(FE4Data.SkillSlot3.skill(skill)); }
					}
					
					staticChar.setSkillSlot1Value(FE4Data.SkillSlot1.valueForSlot1Skills(slot1Skills));
					staticChar.setSkillSlot2Value(FE4Data.SkillSlot2.valueForSlot2Skills(slot2Skills));
					staticChar.setSkillSlot3Value(FE4Data.SkillSlot3.valueForSlot3Skills(slot3Skills));
				}
			}
			
			// Repeat with Gen 2
			// Collect all of the skills from all characters. Go ahead and wipe them at the same time so we don't have to iterate it again.
			universalPool = new PoolDistributor<FE4Data.Skill>();
			characterPool = new PoolDistributor<FE4Data.Character>();
			characterMap = new HashMap<FE4Data.Character, FE4StaticCharacter>();

			// Gen 2 (Common and Substitutes only)
			List<FE4StaticCharacter> gen2Characters = charData.getGen2CommonCharacters();
			gen2Characters.addAll(charData.getGen2SubstituteCharacters());
			for (FE4StaticCharacter staticChar : gen2Characters) {
				FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
				if (fe4Char.linkedCharacters()[0] != fe4Char) {
					continue;
				}
				
				List<FE4Data.SkillSlot1> slot1Skills = FE4Data.SkillSlot1.slot1Skills(staticChar.getSkillSlot1Value());
				List<FE4Data.SkillSlot2> slot2Skills = FE4Data.SkillSlot2.slot2Skills(staticChar.getSkillSlot2Value());
				List<FE4Data.SkillSlot3> slot3Skills = FE4Data.SkillSlot3.slot3Skills(staticChar.getSkillSlot3Value());
				
				for (FE4Data.SkillSlot1 slot1 : slot1Skills) { universalPool.addItem(slot1.generalSkill()); characterPool.addItem(fe4Char); }
				for (FE4Data.SkillSlot2 slot2 : slot2Skills) { universalPool.addItem(slot2.generalSkill()); characterPool.addItem(fe4Char); }
				for (FE4Data.SkillSlot3 slot3 : slot3Skills) { universalPool.addItem(slot3.generalSkill()); characterPool.addItem(fe4Char); }
				
				characterMap.put(fe4Char, staticChar);
			}
			
			while (!universalPool.possibleResults().isEmpty()) {
				FE4Data.Skill skill = universalPool.getRandomItem(rng, true);
				FE4Data.Character recipient = characterPool.getRandomItem(rng, false);
				List<FE4Data.Skill> skillList = assignedSkills.get(recipient);
				if (skillList == null) {
					skillList = new ArrayList<FE4Data.Skill>();
					assignedSkills.put(recipient, skillList);
				}
				
				while (skillList.contains(skill)) {
					recipient = characterPool.getRandomItem(rng, false);
					skillList = assignedSkills.get(recipient);
					if (recipient.mustLoseToCharacters().length > 0 && skill == FE4Data.Skill.NIHIL) {
						continue; // Nobody that has to lose can have Nihil as a skill.
					}
					if (skillList == null) {
						skillList = new ArrayList<FE4Data.Skill>();
						assignedSkills.put(recipient, skillList);
					}
				}
				
				skillList.add(skill);
				if (options.retainNumberOfSkills) {
					characterPool.removeItem(recipient, false);
				}
				
				// Cap skills at 4 per character.
				else if (skillList.size() >= 4) {
					characterPool.removeItem(recipient, true);
				}
			}
			
			// Apply the assigned skills.
			for (FE4Data.Character fe4Char : assignedSkills.keySet()) {
				List<FE4Data.Skill> skills = assignedSkills.get(fe4Char);
				
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					FE4StaticCharacter staticChar = characterMap.get(linked);
					if (staticChar == null) { staticChar = charData.getStaticCharacter(linked); }
					if (staticChar == null) { continue; }
					
					List<FE4Data.SkillSlot1> slot1Skills = new ArrayList<FE4Data.SkillSlot1>();
					List<FE4Data.SkillSlot2> slot2Skills = new ArrayList<FE4Data.SkillSlot2>();
					List<FE4Data.SkillSlot3> slot3Skills = new ArrayList<FE4Data.SkillSlot3>();
					
					for (FE4Data.Skill skill : skills) {
						if (skill.slot() == 1) { slot1Skills.add(FE4Data.SkillSlot1.skill(skill)); }
						else if (skill.slot() == 2) { slot2Skills.add(FE4Data.SkillSlot2.skill(skill)); }
						else if (skill.slot() == 3) { slot3Skills.add(FE4Data.SkillSlot3.skill(skill)); }
					}
					
					staticChar.setSkillSlot1Value(FE4Data.SkillSlot1.valueForSlot1Skills(slot1Skills));
					staticChar.setSkillSlot2Value(FE4Data.SkillSlot2.valueForSlot2Skills(slot2Skills));
					staticChar.setSkillSlot3Value(FE4Data.SkillSlot3.valueForSlot3Skills(slot3Skills));
				}
			}
		} else {
			// Collect all of the skills from all characters.
			PoolDistributor<FE4Data.Skill> universalPool = new PoolDistributor<FE4Data.Skill>();
			PoolDistributor<FE4Data.Character> characterPool = new PoolDistributor<FE4Data.Character>();
			Map<FE4Data.Character, List<FE4Data.Skill>> assignedSkills = new HashMap<FE4Data.Character, List<FE4Data.Skill>>();
			Map<FE4Data.Character, FE4StaticCharacter> characterMap = new HashMap<FE4Data.Character, FE4StaticCharacter>();
			
			// Gen 1
			List<FE4StaticCharacter> gen1Characters = charData.getGen1Characters();
			for (FE4StaticCharacter staticChar : gen1Characters) {
				FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
				if (fe4Char.linkedCharacters()[0] != fe4Char) {
					continue;
				}
				
				List<FE4Data.SkillSlot1> slot1Skills = FE4Data.SkillSlot1.slot1Skills(staticChar.getSkillSlot1Value());
				List<FE4Data.SkillSlot2> slot2Skills = FE4Data.SkillSlot2.slot2Skills(staticChar.getSkillSlot2Value());
				List<FE4Data.SkillSlot3> slot3Skills = FE4Data.SkillSlot3.slot3Skills(staticChar.getSkillSlot3Value());
				
				for (FE4Data.SkillSlot1 slot1 : slot1Skills) { universalPool.addItem(slot1.generalSkill()); characterPool.addItem(fe4Char); }
				for (FE4Data.SkillSlot2 slot2 : slot2Skills) { universalPool.addItem(slot2.generalSkill()); characterPool.addItem(fe4Char); }
				for (FE4Data.SkillSlot3 slot3 : slot3Skills) { universalPool.addItem(slot3.generalSkill()); characterPool.addItem(fe4Char); }
				
				characterMap.put(fe4Char, staticChar);
			}
			
			// Gen 2 (Common and Substitutes only)
			List<FE4StaticCharacter> gen2Characters = charData.getGen2CommonCharacters();
			gen2Characters.addAll(charData.getGen2SubstituteCharacters());
			for (FE4StaticCharacter staticChar : gen2Characters) {
				FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
				if (fe4Char.linkedCharacters()[0] != fe4Char) {
					continue;
				}
				
				List<FE4Data.SkillSlot1> slot1Skills = FE4Data.SkillSlot1.slot1Skills(staticChar.getSkillSlot1Value());
				List<FE4Data.SkillSlot2> slot2Skills = FE4Data.SkillSlot2.slot2Skills(staticChar.getSkillSlot2Value());
				List<FE4Data.SkillSlot3> slot3Skills = FE4Data.SkillSlot3.slot3Skills(staticChar.getSkillSlot3Value());
				
				for (FE4Data.SkillSlot1 slot1 : slot1Skills) { universalPool.addItem(slot1.generalSkill()); characterPool.addItem(fe4Char); }
				for (FE4Data.SkillSlot2 slot2 : slot2Skills) { universalPool.addItem(slot2.generalSkill()); characterPool.addItem(fe4Char); }
				for (FE4Data.SkillSlot3 slot3 : slot3Skills) { universalPool.addItem(slot3.generalSkill()); characterPool.addItem(fe4Char); }
				
				characterMap.put(fe4Char, staticChar);
			}
			
			// Start distributing skills.
			while (!universalPool.possibleResults().isEmpty()) {
				FE4Data.Skill skill = universalPool.getRandomItem(rng, true);
				FE4Data.Character recipient = characterPool.getRandomItem(rng, false);
				List<FE4Data.Skill> skillList = assignedSkills.get(recipient);
				if (skillList == null) {
					skillList = new ArrayList<FE4Data.Skill>();
					assignedSkills.put(recipient, skillList);
				}
				
				while (skillList.contains(skill)) {
					recipient = characterPool.getRandomItem(rng, false);
					skillList = assignedSkills.get(recipient);
					if (recipient.mustLoseToCharacters().length > 0 && skill == FE4Data.Skill.NIHIL) {
						continue; // Nobody that has to lose can have Nihil as a skill.
					}
					if (skillList == null) {
						skillList = new ArrayList<FE4Data.Skill>();
						assignedSkills.put(recipient, skillList);
					}	
				}
				
				skillList.add(skill);
				if (options.retainNumberOfSkills) {
					characterPool.removeItem(recipient, false);
				}
				
				// Cap skills at 4 per character.
				else if (skillList.size() >= 4) {
					characterPool.removeItem(recipient, true);
				}
			}
			
			// Apply the assigned skills.
			for (FE4Data.Character fe4Char : assignedSkills.keySet()) {
				List<FE4Data.Skill> skills = assignedSkills.get(fe4Char);
				
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					FE4StaticCharacter staticChar = characterMap.get(linked);
					if (staticChar == null) { staticChar = charData.getStaticCharacter(linked); }
					if (staticChar == null) { continue; }
					
					List<FE4Data.SkillSlot1> slot1Skills = new ArrayList<FE4Data.SkillSlot1>();
					List<FE4Data.SkillSlot2> slot2Skills = new ArrayList<FE4Data.SkillSlot2>();
					List<FE4Data.SkillSlot3> slot3Skills = new ArrayList<FE4Data.SkillSlot3>();
					
					for (FE4Data.Skill skill : skills) {
						if (skill.slot() == 1) { slot1Skills.add(FE4Data.SkillSlot1.skill(skill)); }
						else if (skill.slot() == 2) { slot2Skills.add(FE4Data.SkillSlot2.skill(skill)); }
						else if (skill.slot() == 3) { slot3Skills.add(FE4Data.SkillSlot3.skill(skill)); }
					}
					
					staticChar.setSkillSlot1Value(FE4Data.SkillSlot1.valueForSlot1Skills(slot1Skills));
					staticChar.setSkillSlot2Value(FE4Data.SkillSlot2.valueForSlot2Skills(slot2Skills));
					staticChar.setSkillSlot3Value(FE4Data.SkillSlot3.valueForSlot3Skills(slot3Skills));
				}
			}
		}
		 
	}
	
	public static void randomizePlayableCharacterSkills(SkillsOptions options, CharacterDataLoader charData, Random rng) {
		
		WeightedDistributor<Integer> skillCountDistributor = skillCountDistributionFromOptions(options);
		
		DebugPrinter.log(DebugPrinter.Key.FE4_SKILL_RANDOM, "Skill Counts:");
		DebugPrinter.log(DebugPrinter.Key.FE4_SKILL_RANDOM, String.format("Zero Skills: %.2f%%", skillCountDistributor.chanceOfResult(0) * 100));
		DebugPrinter.log(DebugPrinter.Key.FE4_SKILL_RANDOM, String.format("One Skill: %.2f%%", skillCountDistributor.chanceOfResult(1) * 100));
		DebugPrinter.log(DebugPrinter.Key.FE4_SKILL_RANDOM, String.format("Two Skills: %.2f%%", skillCountDistributor.chanceOfResult(2) * 100));
		DebugPrinter.log(DebugPrinter.Key.FE4_SKILL_RANDOM, String.format("Three Skills: %.2f%%", skillCountDistributor.chanceOfResult(3) * 100));
		
		WeightedDistributor<FE4Data.Skill> skillDistributor = skillDistributionFromOptions(options);
		
		DebugPrinter.log(DebugPrinter.Key.FE4_SKILL_RANDOM, "Skill Weights:");
		for (FE4Data.Skill skill : FE4Data.Skill.values()) {
			DebugPrinter.log(DebugPrinter.Key.FE4_SKILL_RANDOM, String.format("%s: %.2f%%", skill.toString(), skillDistributor.chanceOfResult(skill) * 100));	
		}
		
		Map<FE4Data.Character, List<FE4Data.Skill>> predeterminedSkills = new HashMap<FE4Data.Character, List<FE4Data.Skill>>();
		
		// Gen 1
		List<FE4StaticCharacter> gen1Characters = charData.getGen1Characters();
		for (FE4StaticCharacter staticChar : gen1Characters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			int numberOfSkills = 0;
			if (options.retainNumberOfSkills) {
				numberOfSkills += FE4Data.SkillSlot1.slot1Skills(staticChar.getSkillSlot1Value()).size();
				numberOfSkills += FE4Data.SkillSlot2.slot2Skills(staticChar.getSkillSlot2Value()).size();
				numberOfSkills += FE4Data.SkillSlot3.slot3Skills(staticChar.getSkillSlot3Value()).size();
			} else {
				numberOfSkills = skillCountDistributor.getRandomItem(rng);
			}
			
			List<FE4Data.Skill> skillsAssigned = predeterminedSkills.get(fe4Char); 
			if (skillsAssigned != null) {
				assignSkillsToStaticCharacter(staticChar, skillsAssigned);
			} else {
				skillsAssigned = assignSkillsToStaticCharacter(options, staticChar, fe4Char, numberOfSkills, skillDistributor, rng);
			
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					predeterminedSkills.put(linked, skillsAssigned);
				}
			}
		}
		
		// Gen 2 (Common and Substitutes only)
		List<FE4StaticCharacter> gen2Characters = charData.getGen2CommonCharacters();
		gen2Characters.addAll(charData.getGen2SubstituteCharacters());
		for (FE4StaticCharacter staticChar : gen2Characters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			int numberOfSkills = 0;
			if (options.retainNumberOfSkills) {
				numberOfSkills += FE4Data.SkillSlot1.slot1Skills(staticChar.getSkillSlot1Value()).size();
				numberOfSkills += FE4Data.SkillSlot2.slot2Skills(staticChar.getSkillSlot2Value()).size();
				numberOfSkills += FE4Data.SkillSlot3.slot3Skills(staticChar.getSkillSlot3Value()).size();
			} else {
				numberOfSkills = skillCountDistributor.getRandomItem(rng);
			}
			
			List<FE4Data.Skill> skillsAssigned = predeterminedSkills.get(fe4Char); 
			if (skillsAssigned != null) {
				assignSkillsToStaticCharacter(staticChar, skillsAssigned);
			} else {
				skillsAssigned = assignSkillsToStaticCharacter(options, staticChar, fe4Char, numberOfSkills, skillDistributor, rng);
			
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					predeterminedSkills.put(linked, skillsAssigned);
				}
			}
		}
	}
	
	public static WeightedDistributor<Integer> skillCountDistributionFromOptions(SkillsOptions options) {
		WeightedDistributor<Integer> skillCountDistributor = new WeightedDistributor<Integer>();
		if (options.skillCounts.zeroSkillsChance.enabled) {
			skillCountDistributor.addItem(0, options.skillCounts.zeroSkillsChance.weight.integerWeightUsingLinearWeight(1, 0));
		}
		if (options.skillCounts.oneSkillChance.enabled) {
			skillCountDistributor.addItem(1, options.skillCounts.oneSkillChance.weight.integerWeightUsingLinearWeight(1, 0));
		}
		if (options.skillCounts.twoSkillChance.enabled) {
			skillCountDistributor.addItem(2, options.skillCounts.twoSkillChance.weight.integerWeightUsingLinearWeight(1, 0));
		}
		if (options.skillCounts.threeSkillChance.enabled) {
			skillCountDistributor.addItem(3, options.skillCounts.threeSkillChance.weight.integerWeightUsingLinearWeight(1, 0));
		}
		
		return skillCountDistributor;
	}
	
	public static WeightedDistributor<FE4Data.Skill> skillDistributionFromOptions(SkillsOptions options) {
		WeightedDistributor<FE4Data.Skill> skillDistributor = new WeightedDistributor<FE4Data.Skill>();
		int linearSlope = 1;
		int linearOffset = 0;
		
		for (FE4Data.Skill skill : FE4Data.Skill.values()) {
			String skillName = skill.capitalizedName();
			WeightedOptions weightedOptions = options.skillWeights.getWeightedOptionsByName(skillName);
			skillDistributor.addItem(skill, weightedOptions.weight.integerWeightUsingLinearWeight(linearSlope, linearOffset));
		}

		return skillDistributor;
	}
	
	private static List<FE4Data.Skill> assignSkillsToStaticCharacter(SkillsOptions options, FE4StaticCharacter staticChar, FE4Data.Character fe4Char, int numberOfSkills, WeightedDistributor<FE4Data.Skill> skillDistributor, Random rng) {
		staticChar.setSkillSlot1Value(0);
		staticChar.setSkillSlot2Value(0);
		staticChar.setSkillSlot3Value(0);
		
		if (numberOfSkills == 0) { return new ArrayList<FE4Data.Skill>(); }
		
		boolean assignPursuit = false;
		if (options.skillWeights.getPursuitChance() > 0) {
			assignPursuit = rng.nextInt(100) < options.skillWeights.getPursuitChance();
		}
	
		WeightedDistributor<FE4Data.Skill> workingSkillDistributor = new WeightedDistributor<>(skillDistributor);
		List<FE4Data.Skill> skillsGiven = new ArrayList<FE4Data.Skill>();
		
		if (assignPursuit) {
			numberOfSkills--;
			skillsGiven.add(Skill.PURSUIT);
		}
		
		for (int i = 0; i < numberOfSkills; i++) {
			FE4Data.Skill randomSkill = workingSkillDistributor.getRandomItem(rng);
			if (fe4Char.mustLoseToCharacters().length > 0) {
				while (randomSkill == FE4Data.Skill.NIHIL) {
					randomSkill = workingSkillDistributor.getRandomItem(rng);
				}
			}
			if (randomSkill == null) { break; }
			skillsGiven.add(randomSkill);
			workingSkillDistributor.removeItem(randomSkill);
			if (workingSkillDistributor.possibleResults().isEmpty()) { break; }
		}
		
		assignSkillsToStaticCharacter(staticChar, skillsGiven);
		
		return skillsGiven;
	}
	
	private static void assignSkillsToStaticCharacter(FE4StaticCharacter staticChar, List<FE4Data.Skill> skillsGiven) {
		List<FE4Data.SkillSlot1> slot1Skills = new ArrayList<FE4Data.SkillSlot1>();
		List<FE4Data.SkillSlot2> slot2Skills = new ArrayList<FE4Data.SkillSlot2>();
		List<FE4Data.SkillSlot3> slot3Skills = new ArrayList<FE4Data.SkillSlot3>();
		
		for (FE4Data.Skill skill : skillsGiven) {
			if (skill.slot() == 1) {
				slot1Skills.add(FE4Data.SkillSlot1.skill(skill));
			} else if (skill.slot() == 2) {
				slot2Skills.add(FE4Data.SkillSlot2.skill(skill));
			} else if (skill.slot() == 3) {
				slot3Skills.add(FE4Data.SkillSlot3.skill(skill));
			}
		}
		
		int skill1Value = FE4Data.SkillSlot1.valueForSlot1Skills(slot1Skills);
		int skill2Value = FE4Data.SkillSlot2.valueForSlot2Skills(slot2Skills);
		int skill3Value = FE4Data.SkillSlot3.valueForSlot3Skills(slot3Skills);
		
		staticChar.setSkillSlot1Value(skill1Value);
		staticChar.setSkillSlot2Value(skill2Value);
		staticChar.setSkillSlot3Value(skill3Value);
	}

}
