package random.gcnwii.fe9.randomizer;

import java.util.List;
import java.util.Random;

import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Skill;
import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;
import random.general.WeightedDistributor;
import ui.model.SkillWeightOptions;
import ui.model.WeightedOptions;

public class FE9SkillRandomizer {
	
	static final int rngSalt = 21457;
	
	public static void randomizeExistingSkills(SkillWeightOptions weights, FE9CharacterDataLoader charData, FE9SkillDataLoader skillData, Random rng) {
		WeightedDistributor<FE9Skill> distributor = weightedDistributorForOptions(weights, skillData);
		
		for (FE9Character character : charData.allPlayableCharacters()) {
			String sid1 = charData.getSID1ForCharacter(character);
			if (sid1 == null) { continue; }
			FE9Skill skill1 = skillData.getSkillWithSID(sid1);
			if (skillData.isModifiableSkill(skill1)) {
				FE9Skill randomSkill = distributor.getRandomItem(rng);
				charData.setSID1ForCharacter(character, skillData.getSID(randomSkill));
			} else if (skillData.isOccultSkill(skill1)) {
				// Occult skills should already be handled by the class randomization if they need to be changed.
				// We'll add options for this later to randomize occult skills.
			} else {
				String sid2 = charData.getSID2ForCharacter(character);
				if (sid2 == null) { continue; }
				FE9Skill skill2 = skillData.getSkillWithSID(sid2);
				if (skillData.isModifiableSkill(skill2)) {
					FE9Skill randomSkill = distributor.getRandomItem(rng);
					charData.setSID2ForCharacter(character, skillData.getSID(randomSkill));	
				} else if (skillData.isOccultSkill(skill2)) {
					// TODO: Randomize with Occult Skills
				} else {
					String sid3 = charData.getSID3ForCharacter(character);
					if (sid3 == null) { continue; }
					FE9Skill skill3 = skillData.getSkillWithSID(sid3);
					if (skillData.isModifiableSkill(skill3)) {
						FE9Skill randomSkill = distributor.getRandomItem(rng);
						charData.setSID3ForCharacter(character, skillData.getSID(randomSkill));
					} else if (skillData.isOccultSkill(skill3)) {
						// TODO: Randomize with Occult Skills
					}
				}
			}
		}
		
		charData.commit();
	}
	
	public static void fullyRandomizeSkills(int skillChance, SkillWeightOptions weights, FE9CharacterDataLoader charData, FE9SkillDataLoader skillData, FE9ChapterDataLoader chapterData, Random rng) {
		WeightedDistributor<FE9Skill> distributor = weightedDistributorForOptions(weights, skillData);
		
		for (FE9Character character : charData.allPlayableCharacters()) {
			String sid1 = charData.getSID1ForCharacter(character);
			FE9Skill skill1 = skillData.getSkillWithSID(sid1);
			if (skill1 == null || skillData.isModifiableSkill(skill1)) {
				if (rng.nextInt(100) < skillChance) {
					FE9Skill randomSkill = distributor.getRandomItem(rng);
					charData.setSID1ForCharacter(character, skillData.getSID(randomSkill));	
				} else {
					charData.setSID1ForCharacter(character, null);
				}
			} else if (skillData.isOccultSkill(skill1)) {
				// TODO: Randomize with Occult Skills
			} else {
				String sid2 = charData.getSID2ForCharacter(character);
				FE9Skill skill2 = skillData.getSkillWithSID(sid2);
				if (skill2 == null || skillData.isModifiableSkill(skill2)) {
					if (rng.nextInt(100) < skillChance) {
						FE9Skill randomSkill = distributor.getRandomItem(rng);
						charData.setSID2ForCharacter(character, skillData.getSID(randomSkill));
						// Ike is special for Normal mode. For whatever reason, his skill won't show up without
						// coding it into the chapter army data. It shows up fine in Hard mode though.
						if (charData.getPIDForCharacter(character).equals(FE9Data.Character.IKE.getPID())) {
							giveIkeSkill(chapterData, skillData.getSID(randomSkill));
						}
					} else {
						charData.setSID2ForCharacter(character, null);
					}
				} else if (skillData.isOccultSkill(skill2)) {
					// TODO: Randomize with Occult Skills	
				} else {
					String sid3 = charData.getSID3ForCharacter(character);
					FE9Skill skill3 = skillData.getSkillWithSID(sid3);
					if (skill3 == null || skillData.isModifiableSkill(skill3)) {
						if (rng.nextInt(100) < skillChance) {
							FE9Skill randomSkill = distributor.getRandomItem(rng);
							charData.setSID3ForCharacter(character, skillData.getSID(randomSkill));
						} else {
							charData.setSID3ForCharacter(character, null);
						}
					} else if (skillData.isOccultSkill(skill3)) {
						// TODO: Randomize with Occult Skills
					}
				}
			}
		}
		
		charData.commit();
	}
	
	private static void giveIkeSkill(FE9ChapterDataLoader chapterData, String sid) {
		List<FE9ChapterArmy> prologueArmies =  chapterData.armiesForChapter(FE9Data.Chapter.PROLOGUE);
		for (FE9ChapterArmy army : prologueArmies) {
			FE9ChapterUnit ike = army.getUnitForPID(FE9Data.Character.IKE.getPID());
			if (ike != null) {
				army.setSkill2ForUnit(ike, sid);
				army.commitChanges();
			}
		}
	}

	private static WeightedDistributor<FE9Skill> weightedDistributorForOptions(SkillWeightOptions weights, FE9SkillDataLoader skillData) {
		WeightedDistributor<FE9Skill> distributor = new WeightedDistributor<FE9Skill>();
		int linearSlope = 1;
		int linearOffset = 0;
		for (String skillName : weights.getSkillNames()) {
			WeightedOptions option = weights.getWeightedOptionsByName(skillName);
			if (option.enabled) {
				FE9Skill skill = skillData.skillWithDisplayName(skillName);
				assert(skill != null);
				distributor.addItem(skill, option.weight.integerWeightUsingLinearWeight(linearSlope, linearOffset));
			}
		}
		
		return distributor;
	}
	
	public static WeightedDistributor<String> weightedDistributorForOptions(SkillWeightOptions weights) {
		WeightedDistributor<String> distributor = new WeightedDistributor<String>();
		int linearSlope = 1;
		int linearOffset = 0;
		for (String skillName : weights.getSkillNames()) {
			WeightedOptions option = weights.getWeightedOptionsByName(skillName);
			if (option.enabled) {
				distributor.addItem(skillName, option.weight.integerWeightUsingLinearWeight(linearSlope, linearOffset));
			}
		}
		
		return distributor;
	}
}
