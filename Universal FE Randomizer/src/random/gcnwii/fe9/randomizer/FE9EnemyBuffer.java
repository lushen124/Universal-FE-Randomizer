package random.gcnwii.fe9.randomizer;

import java.util.Random;

import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;

public class FE9EnemyBuffer {
	
	public static final int rngSalt = 9139;
	
	public static void flatBuffMinionGrowths(int buffAmount, FE9ClassDataLoader classData) {
		
	}
	
	public static void scaleBuffMinionGrowths(int buffAmount, FE9ClassDataLoader classData) {
		
	}

	public static void buffBossStatsLinearly(int buffAmount, FE9CharacterDataLoader charData, FE9ClassDataLoader classData) {
		
	}
	
	public static void buffBossStatsByEasing(int buffAmount, FE9CharacterDataLoader charData, FE9ClassDataLoader classData) {
		
	}
	
	public static void improveMinionWeapons(int chance, FE9ClassDataLoader classData, FE9ItemDataLoader itemData, FE9ChapterDataLoader chapterData, Random rng) {
		
	}
	
	public static void improveBossWeapons(int chance, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, FE9ItemDataLoader itemData, 
			FE9ChapterDataLoader chapterData, Random rng) {
		
	}
	
	public static void giveMinionSkills(int chance, FE9SkillDataLoader skillData, FE9ChapterDataLoader chapterData, Random rng) {
		
	}
	
	public static void giveBossesSkills(int chance, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, FE9SkillDataLoader skillData, Random rng) {
		
	}
}
