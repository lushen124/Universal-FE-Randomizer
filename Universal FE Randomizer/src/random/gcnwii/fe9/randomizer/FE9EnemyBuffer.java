package random.gcnwii.fe9.randomizer;

import java.util.List;
import java.util.Random;

import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Item;
import fedata.gcnwii.fe9.FE9Skill;
import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;

public class FE9EnemyBuffer {
	
	public static final int rngSalt = 9139;
	
	public static void flatBuffMinionGrowths(int buffAmount, FE9ClassDataLoader classData) {
		for (FE9Class charClass : classData.allValidClasses()) {
			charClass.setHPGrowth(Math.min(charClass.getHPGrowth() + buffAmount, 255));
			charClass.setSTRGrowth(Math.min(charClass.getSTRGrowth() + buffAmount, 255));
			charClass.setMAGGrowth(Math.min(charClass.getMAGGrowth() + buffAmount, 255));
			charClass.setSKLGrowth(Math.min(charClass.getSKLGrowth() + buffAmount, 255));
			charClass.setSPDGrowth(Math.min(charClass.getSPDGrowth() + buffAmount, 255));
			charClass.setLCKGrowth(Math.min(charClass.getLCKGrowth() + buffAmount, 255));
			charClass.setDEFGrowth(Math.min(charClass.getDEFGrowth() + buffAmount, 255));
			charClass.setRESGrowth(Math.min(charClass.getRESGrowth() + buffAmount, 255));
		}
		
		classData.commit();
	}
	
	public static void scaleBuffMinionGrowths(int buffAmount, FE9ClassDataLoader classData) {
		double multiplier = 1 + (double)buffAmount / (double)100;
		for (FE9Class charClass : classData.allValidClasses()) {
			charClass.setHPGrowth(Math.min((int)(charClass.getHPGrowth() * multiplier), 255));
			charClass.setSTRGrowth(Math.min((int)(charClass.getSTRGrowth() * multiplier), 255));
			charClass.setMAGGrowth(Math.min((int)(charClass.getMAGGrowth() * multiplier), 255));
			charClass.setSKLGrowth(Math.min((int)(charClass.getSKLGrowth() * multiplier), 255));
			charClass.setSPDGrowth(Math.min((int)(charClass.getSPDGrowth() * multiplier), 255));
			charClass.setLCKGrowth(Math.min((int)(charClass.getLCKGrowth() * multiplier), 255));
			charClass.setDEFGrowth(Math.min((int)(charClass.getDEFGrowth() * multiplier), 255));
			charClass.setRESGrowth(Math.min((int)(charClass.getRESGrowth() * multiplier), 255));
		}
		
		classData.commit();
	}

	public static void buffBossStatsLinearly(int buffAmount, FE9CharacterDataLoader charData, FE9ClassDataLoader classData) {
		double divisor = FE9Data.Chapter.allChapters().size();
		double dividend = 0;
		for (FE9Data.Chapter chapter : FE9Data.Chapter.allChapters()) {
			int delta = (int)((dividend / divisor) * buffAmount);
			FE9Data.Character[] bosses = chapter.bossCharactersForChapter();
			for (FE9Data.Character character : bosses) {
				FE9Character boss = charData.characterWithID(character.getPID());
				FE9Class bossClass = classData.classWithID(charData.getJIDForCharacter(boss));
				boss.setBaseHP(Math.min(bossClass.getMaxHP() - bossClass.getBaseHP(), boss.getBaseHP() + delta));
				boss.setBaseSTR(Math.min(bossClass.getMaxSTR() - bossClass.getBaseSTR(), boss.getBaseSTR() + delta));
				boss.setBaseMAG(Math.min(bossClass.getMaxMAG() - bossClass.getBaseMAG(), boss.getBaseMAG() + delta));
				boss.setBaseSKL(Math.min(bossClass.getMaxSKL() - bossClass.getBaseSKL(), boss.getBaseSKL() + delta));
				boss.setBaseSPD(Math.min(bossClass.getMaxSPD() - bossClass.getBaseSPD(), boss.getBaseSPD() + delta));
				boss.setBaseLCK(Math.min(bossClass.getMaxLCK() - bossClass.getBaseLCK(), boss.getBaseLCK() + delta));
				boss.setBaseDEF(Math.min(bossClass.getMaxDEF() - bossClass.getBaseDEF(), boss.getBaseDEF() + delta));
				boss.setBaseRES(Math.min(bossClass.getMaxRES() - bossClass.getBaseRES(), boss.getBaseRES() + delta));
			}
			dividend += 1;
		}
		
		charData.commit();
	}
	
	public static void buffBossStatsByEasing(int buffAmount, FE9CharacterDataLoader charData, FE9ClassDataLoader classData) {
		double[] factors = new double[] {
				1.0 / 31.0, // Prologue (not used) 
				1.4 / 31.0, // Ch 1 (+ 0.4)
				1.9 / 31.0, // Ch 2 (+ 0.5)
				2.5 / 31.0, // Ch 3 (+ 0.6)
				3.2 / 31.0, // Ch 4 (+ 0.7)
				4.0 / 31.0, // Ch 5 (+ 0.8)
				4.9 / 31.0, // Ch 6 (+ 0.9)
				5.9 / 31.0, // Ch 7 (+ 1.0)
				7.0 / 31.0, // Ch 8 (+ 1.1)
				8.2 / 31.0, // Ch 9 (+ 1.2)
				9.4 / 31.0, // Ch 10 (+ 1.3)
				10.8 / 31.0, // Ch 11 (+ 1.4)
				12.3 / 31.0, // Ch 12 (+ 1.5)
				13.9 / 31.0, // Ch 13 (+ 1.6)
				15.5 / 31.0, // Ch 14 (+ 1.6)
				17.1 / 31.0, // Ch 15 (+ 1.6)
				18.7 / 31.0, // Ch 16 (+ 1.6)
				20.2 / 31.0, // Ch 17 (+ 1.5)
				21.6 / 31.0, // Ch 18 (+ 1.4)
				22.9 / 31.0, // Ch 19 (+ 1.3)
				24.1 / 31.0, // Ch 20 (+ 1.2)
				25.2 / 31.0, // Ch 21 (+ 1.1)
				26.2 / 31.0, // Ch 22 (+ 1.0)
				27.1 / 31.0, // Ch 23 (+ 0.9)
				27.9 / 31.0, // Ch 24 (+ 0.8)
				28.6 / 31.0, // Ch 25 (+ 0.7)
				29.2 / 31.0, // Ch 26 (+ 0.6)
				29.7 / 31.0, // Ch 27 (+ 0.5)
				30.1 / 31.0, // Ch 28 (+ 0.4)
				31.0 / 31.0 // Endgame (+ 0.9)
				};
		int count = 0;
		for (FE9Data.Chapter chapter : FE9Data.Chapter.allChapters()) {
			int delta = (int)(factors[count++] * buffAmount);
			FE9Data.Character[] bosses = chapter.bossCharactersForChapter();
			for (FE9Data.Character character : bosses) {
				FE9Character boss = charData.characterWithID(character.getPID());
				FE9Class bossClass = classData.classWithID(charData.getJIDForCharacter(boss));
				boss.setBaseHP(Math.min(bossClass.getMaxHP() - bossClass.getBaseHP(), boss.getBaseHP() + delta));
				boss.setBaseSTR(Math.min(bossClass.getMaxSTR() - bossClass.getBaseSTR(), boss.getBaseSTR() + delta));
				boss.setBaseMAG(Math.min(bossClass.getMaxMAG() - bossClass.getBaseMAG(), boss.getBaseMAG() + delta));
				boss.setBaseSKL(Math.min(bossClass.getMaxSKL() - bossClass.getBaseSKL(), boss.getBaseSKL() + delta));
				boss.setBaseSPD(Math.min(bossClass.getMaxSPD() - bossClass.getBaseSPD(), boss.getBaseSPD() + delta));
				boss.setBaseLCK(Math.min(bossClass.getMaxLCK() - bossClass.getBaseLCK(), boss.getBaseLCK() + delta));
				boss.setBaseDEF(Math.min(bossClass.getMaxDEF() - bossClass.getBaseDEF(), boss.getBaseDEF() + delta));
				boss.setBaseRES(Math.min(bossClass.getMaxRES() - bossClass.getBaseRES(), boss.getBaseRES() + delta));
			}
		}
		
		charData.commit();
	}
	
	public static void improveMinionWeapons(int chance, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, FE9ItemDataLoader itemData, FE9ChapterDataLoader chapterData, Random rng) {
		for (FE9Data.Chapter chapter : FE9Data.Chapter.allChapters()) {
			List<FE9ChapterArmy> armies = chapterData.armiesForChapter(chapter);
			for (FE9ChapterArmy army : armies) {
				for (String unitID : army.getAllUnitIDs()) {
					FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
					FE9Character character = charData.characterWithID(army.getPIDForUnit(unit));
					if (charData.isMinionCharacter(character) && army.getWeapon1ForUnit(unit) != null) {
						if (rng.nextInt(100) < chance) {
							String iid = army.getWeapon1ForUnit(unit);
							List<FE9Item> possibleReplacements = itemData.possibleUpgradesToWeapon(itemData.itemWithIID(iid));
							if (possibleReplacements != null && !possibleReplacements.isEmpty()) {
								army.setWeapon1ForUnit(unit, itemData.iidOfItem(possibleReplacements.get(rng.nextInt(possibleReplacements.size()))));
							}
						}
						if (army.getWeapon2ForUnit(unit) != null && rng.nextInt(100) < chance) {
							String iid = army.getWeapon2ForUnit(unit);
							List<FE9Item> possibleReplacements = itemData.possibleUpgradesToWeapon(itemData.itemWithIID(iid));
							if (possibleReplacements != null && !possibleReplacements.isEmpty()) {
								army.setWeapon2ForUnit(unit, itemData.iidOfItem(possibleReplacements.get(rng.nextInt(possibleReplacements.size()))));
							}
						}
					}
				}
			}
		}
		
		chapterData.commitChanges();
		for (FE9Class charClass : classData.allValidClasses()) {
			String weaponLevelString = classData.getWeaponLevelsForClass(charClass);
			classData.setWeaponLevelsForClass(charClass, sRankWeaponLevel(weaponLevelString));
		}
		classData.commit();
	}
	
	public static void improveBossWeapons(int chance, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, FE9ItemDataLoader itemData, 
			FE9ChapterDataLoader chapterData, Random rng) {
		for (FE9Data.Chapter chapter : FE9Data.Chapter.allChapters()) {
			List<FE9ChapterArmy> armies = chapterData.armiesForChapter(chapter);
			for (FE9ChapterArmy army : armies) {
				for (String unitID : army.getAllUnitIDs()) {
					FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
					FE9Character character = charData.characterWithID(army.getPIDForUnit(unit));
					if (charData.isBossCharacter(character)) {
						String weaponLevelString = charData.getWeaponLevelStringForCharacter(character);
						charData.setWeaponLevelStringForCharacter(character, sRankWeaponLevel(weaponLevelString));
						if (army.getWeapon1ForUnit(unit) != null && rng.nextInt(100) < chance) {
							String iid = army.getWeapon1ForUnit(unit);
							List<FE9Item> possibleReplacements = itemData.possibleUpgradesToWeapon(itemData.itemWithIID(iid));
							if (possibleReplacements != null && !possibleReplacements.isEmpty()) {
								army.setWeapon1ForUnit(unit, itemData.iidOfItem(possibleReplacements.get(rng.nextInt(possibleReplacements.size()))));
							}
						}
						if (army.getWeapon2ForUnit(unit) != null && rng.nextInt(100) < chance) {
							String iid = army.getWeapon2ForUnit(unit);
							List<FE9Item> possibleReplacements = itemData.possibleUpgradesToWeapon(itemData.itemWithIID(iid));
							if (possibleReplacements != null && !possibleReplacements.isEmpty()) {
								army.setWeapon2ForUnit(unit, itemData.iidOfItem(possibleReplacements.get(rng.nextInt(possibleReplacements.size()))));
							}
						}
					}
				}
			}
		}
		
		chapterData.commitChanges();
		charData.commit();
	}
	
	public static void giveMinionSkills(int chance, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, FE9ChapterDataLoader chapterData, Random rng) {
		List<FE9Skill> skillPool = skillData.skillList(false);
		for (FE9Data.Chapter chapter : FE9Data.Chapter.allChapters()) {
			List<FE9ChapterArmy> armies = chapterData.armiesForChapter(chapter);
			for (FE9ChapterArmy army : armies) {
				for (String unitID : army.getAllUnitIDs()) {
					FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
					FE9Character character = charData.characterWithID(army.getPIDForUnit(unit));
					FE9Class charClass = classData.classWithID(army.getJIDForUnit(unit));
					if (charData.isMinionCharacter(character)) {
						if (rng.nextInt(100) < chance) {
							if (army.getSkill1ForUnit(unit) == null) {
								int rn = rng.nextInt(skillPool.size() + (classData.isPromotedClass(charClass) ? 1 : 0));
								if (rn == skillPool.size()) {
									FE9Skill occult = skillData.occultSkillForJID(classData.getJIDForClass(charClass));
									if (occult != null) {
										army.setSkill1ForUnit(unit, skillData.getSID(occult));
										continue;
									} else {
										rn = rng.nextInt(skillPool.size());
									}
								}
								army.setSkill1ForUnit(unit, skillData.getSID(skillPool.get(rn)));
							} else if (army.getSkill2ForUnit(unit) == null) {
								int rn = rng.nextInt(skillPool.size());
								army.setSkill2ForUnit(unit, skillData.getSID(skillPool.get(rn)));
							}
						}
					}
				}
			}
		}
		
		chapterData.commitChanges();
	}
	
	public static void giveBossesSkills(int chance, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, Random rng) {
		List<FE9Skill> skillPool = skillData.skillList(false);
		for (FE9Character boss : charData.allBossCharacters()) {
			FE9Class charClass = classData.classWithID(charData.getJIDForCharacter(boss));
			if (rng.nextInt(100) < chance) {
				// SID1 is almost always SID_BOSS.
				if (charData.getSID2ForCharacter(boss) == null) {
					int rn = rng.nextInt(skillPool.size() + (classData.isPromotedClass(charClass) ? 1 : 0));
					if (rn == skillPool.size()) {
						FE9Skill occult = skillData.occultSkillForJID(classData.getJIDForClass(charClass));
						if (occult != null) {
							charData.setSID2ForCharacter(boss, skillData.getSID(occult));
							continue;
						} else {
							rn = rng.nextInt(skillPool.size());
						}
					}
					charData.setSID2ForCharacter(boss, skillData.getSID(skillPool.get(rn)));
				} else if (charData.getSID3ForCharacter(boss) == null) {
					int rn = rng.nextInt(skillPool.size());
					charData.setSID3ForCharacter(boss, skillData.getSID(skillPool.get(rn)));
				}
			}
		}
		
		charData.commit();
	}
	
	private static String sRankWeaponLevel(String weaponLevelString) {
		weaponLevelString = weaponLevelString.replace('E', 'S');
		weaponLevelString = weaponLevelString.replace('D', 'S');
		weaponLevelString = weaponLevelString.replace('C', 'S');
		weaponLevelString = weaponLevelString.replace('B', 'S');
		weaponLevelString = weaponLevelString.replace('A', 'S');
		return weaponLevelString;
	}
}
