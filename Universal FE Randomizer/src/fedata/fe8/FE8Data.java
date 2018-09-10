package fedata.fe8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.general.PaletteColor;
import fedata.general.PaletteInfo;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;
import util.WhyDoesJavaNotHaveThese;

public class FE8Data {
	public static final String FriendlyName = "Fire Emblem: The Sacred Stones";
	public static final String GameCode = "BE8E";

	public static final long CleanCRC32 = 0xA47246AEL;
	public static final long CleanSize = 16777216;
	
	public static final int NumberOfCharacters = 256;
	public static final int BytesPerCharacter = 52;
	public static final long CharacterTablePointer = 0x17D64; 
	//public static final long DefaultCharacterTableAddress = 0x803D30; 
	
	public static final int NumberOfClasses = 128;
	public static final int BytesPerClass = 84;
	public static final long ClassTablePointer = 0x17AB8;
	//public static final long DefaultClassTableAddress = 0x807110;
	
	public static final int NumberOfItems = 205;
	public static final int BytesPerItem = 36;
	public static final long ItemTablePointer = 0x16410;
	//public static final long DefaultItemTableAddress = 0x809B10;
	
	public static final int NumberOfSpellAnimations = 160;
	public static final int BytesPerSpellAnimation = 16;
	public static final long SpellAnimationTablePointer = 0x58014;
	//public static final long DefaultSpellAnimationTableOffset = 0x8AFBD8;
	
	public static final int HuffmanTreeStart = 0x6E0; // Resolved once
	public static final int HuffmanTreeEnd = 0x6DC; // Resolved twice
	public static final long TextTablePointer = 0xA2A0;
	//public static final long DefaultTextArrayOffset = 0x15D48C;
	public static final int NumberOfTextStrings = 0xD4B;
	
	public static final long ChapterTablePointer = 0x19900;
	//public static final long DefaultChapterArrayOffset = 0x8B363C;
	public static final int BytesPerChapterUnit = 20;
	
	public static final long PromotionItemTablePointer = 0x29218; // These work the same way as FE7.
	
	// Unique to FE8
	public static final long PromotionBranchTablePointer = 0xCC7D0;
	//public static final long DefaultPromotionBranchTableOffset = 0x95DFA4L;
	public static final int BytesPerPromotionBranchEntry = 2;
	
	public static final long PaletteClassTablePointer = 0x575B4;
	//public static final long DefaultPaletteClassTableOffset = 0x95E0A4L;
	public static final int BytesPerPaletteTableEntry = 7;
	
	public static final long PaletteIndexTablePointer = 0x57394;
	//public static final long DefaultPaletteIndexTableOffset = 0x95EEA4;
	public static final int BytesPerPaletteIndexTableEntry = 7;
	
	public static final int BytesPerBossPalette = 80; // Play around with this. Hopefully it doesn't collide with another palette.
	public static final int BytesPerPalette = 40;
	
	public enum Character {
		NONE(0x00),
		
		EIRIKA(0x01), SETH(0x02), GILLIAM(0x03), FRANZ(0x04), MOULDER(0x05), VANESSA(0x06), ROSS(0x07), NEIMI(0x08), COLM(0x09), GARCIA(0x0A), INNES(0x0B), LUTE(0x0C), NATASHA(0x0D), CORMAG(0x0E),
		EPHRAIM(0x0F), FORDE(0x10), KYLE(0x11), AMELIA(0x12), ARTUR(0x13), GERIK(0x14), TETHYS(0x15), MARISA(0x16), SALEH(0x17), EWAN(0x18), LARACHEL(0x19), DOZLA(0x1A), RENNAC(0x1C), DUESSEL(0x1D),
		MYRRH(0x1E), KNOLL(0x1F), JOSHUA(0x20), SYRENE(0x21), TANA(0x22), ORSON_5X(0x42),
		
		LYON(0x23), LYON_CH17(0x40), MORVA(0x41), LYON_FINAL(0x6C), DEMON_KING(0xBE),
		
		ORSON(0x24), SELENA(0x26), SELENA_10B_13B(0x44), VALTER(0x27), VALTER_CH15(0x43), VALTER_PROLOGUE(0x45), RIEV(0x28), CAELLACH(0x29), BREGUET(0x46), BONE(0x47), BAZBA(0x48), MUMMY_CH4(0x49),
		SAAR(0x4A), NOVALA(0x4B), MURRAY(0x4C), TIRADO(0x4D), BINKS(0x4E), PABLO(0x4F), MACDAIRE_12A(0x50), AIAS(0x51), CARLYLE(0x52), CAELLACH_CH15(0x53), PABLO_13A(0x54), GORGON_CH18(0x56),
		RIEV_CH19_CH20(0x57), GHEB(0x5A), BERAN(0x5B), CYCLOPS_CH12B(0x5C), HELLBONE_11A(0x5D), DEATHGOYLE_11B(0x5E), ONEILL(0x68), GLEN_CUTSCENE(0x69), ZONTA(0x6A), VIGARDE(0x6B), ORSON_CH16(0x6D), 
		
		GLEN(0x25), FADO(0x2A), ISMAIRE(0x2B), HAYDEN(0x2C),
		
		FADO_NPC(0xC5), HAYDEN_NPC(0xC7), MANSEL(0xC8), KLIMT(0xC9), DARA(0xCA), ISMAIRE_NPC(0xCB);
		
		public int ID;
		
		private static Map<Integer, Character> map = new HashMap<Integer, Character>();
		
		static {
			for (Character character : Character.values()) {
				map.put(character.ID, character);
			}
		}
		
		private Character(final int id) { ID = id; }
		
		public static Character valueOf(int characterId) {
			return map.get(characterId);
		}
		
		public static int[] characterIDsForCharacters(Character[] charArray) {
			int[] idArray = new int[charArray.length];
			for (int i = 0; i < charArray.length; i++) {
				idArray[i] = charArray[i].ID;
			}
			
			return idArray;
		}
		
		public static int canonicalIDForCharacterID(int characterID) {
			Character character = valueOf(characterID);
			if (character == null) { return 0; }
			
			switch (character) {
			case VALTER_CH15:
			case VALTER_PROLOGUE:
				return VALTER.ID;
			case SELENA_10B_13B: 
				return SELENA.ID;
			case ORSON_5X:
			case ORSON_CH16: 
				return ORSON.ID;
			case RIEV_CH19_CH20: 
				return RIEV.ID;
			case CAELLACH_CH15: 
				return CAELLACH.ID;
			case GLEN_CUTSCENE: 
				return GLEN.ID;
			case PABLO_13A:
				return PABLO.ID;
			case LYON_CH17:
			case LYON_FINAL:
				return LYON.ID;
			default: return characterID;
			}
		}
		
		public static Set<Character> allPlayableCharacters = new HashSet<Character>(Arrays.asList(EIRIKA, SETH, GILLIAM, FRANZ, MOULDER, VANESSA, ROSS, NEIMI, COLM, GARCIA, INNES, LUTE, NATASHA, CORMAG,
				EPHRAIM, FORDE, KYLE, AMELIA, ARTUR, GERIK, TETHYS, MARISA, SALEH, EWAN, LARACHEL, DOZLA, RENNAC, DUESSEL, MYRRH, KNOLL, JOSHUA, SYRENE, TANA));
		
		public static Set<Character> allBossCharacters = new HashSet<Character>(Arrays.asList(ORSON, SELENA, SELENA_10B_13B, VALTER, VALTER_CH15, VALTER_PROLOGUE, RIEV, CAELLACH, BREGUET, BONE, BAZBA, MUMMY_CH4,
				SAAR, NOVALA, MURRAY, TIRADO, BINKS, PABLO, MACDAIRE_12A, AIAS, CARLYLE, CAELLACH_CH15, PABLO_13A, GORGON_CH18,
				RIEV_CH19_CH20, GHEB, BERAN, CYCLOPS_CH12B, HELLBONE_11A, DEATHGOYLE_11B, ONEILL, GLEN_CUTSCENE, ZONTA, VIGARDE, ORSON_CH16));
		public static Set<Character> restrictedClassCharacters = new HashSet<Character>(Arrays.asList(CORMAG, VALTER, GLEN, GLEN_CUTSCENE, VALTER_CH15, VALTER_PROLOGUE));
		
		public static Set<Character> allLords = new HashSet<Character>(Arrays.asList(EIRIKA, EPHRAIM));
		public static Set<Character> allThieves = new HashSet<Character>(Arrays.asList(COLM, RENNAC));
		
		public static Set<Character> charactersThatRequireRange = new HashSet<Character>(Arrays.asList());
		public static Set<Character> charactersThatRequireMelee = new HashSet<Character>(Arrays.asList(SETH)); // The prologue scripted battle.
		
		public Boolean isLord() {
			return allLords.contains(this);
		}
		
		public Boolean isThief() {
			return allThieves.contains(this);
		}
		
		public Boolean isBoss() {
			return allBossCharacters.contains(this);
		}
		
		public Boolean isPlayableCharacter() {
			return allPlayableCharacters.contains(this);
		}
		
		public Boolean requiresRange() {
			return charactersThatRequireRange.contains(this);
		}
		
		public Boolean requiresMelee() {
			return charactersThatRequireMelee.contains(this);
		}
		
		public Boolean hasLimitedClasses() {
			return restrictedClassCharacters.contains(this);
		}
		
		public static Map<Integer, Character> getCharacterCounters() {
			Map<Integer, Character> counterMap = new HashMap<Integer, Character>();
			return counterMap;
		}
		
		public static Character[] allLinkedCharactersFor(Character character) {
			switch (character) {
			case ORSON:
			case ORSON_5X:
			case ORSON_CH16:
				return new Character[] {ORSON, ORSON_5X, ORSON_CH16};
			case LYON:
			case LYON_CH17:
			case LYON_FINAL:
				return new Character[] {LYON, LYON_CH17, LYON_FINAL};
			case SELENA:
			case SELENA_10B_13B:
				return new Character[] {SELENA, SELENA_10B_13B};
			case VALTER:
			case VALTER_CH15:
			case VALTER_PROLOGUE:
				return new Character[] {VALTER, VALTER_CH15, VALTER_PROLOGUE};
			case RIEV:
			case RIEV_CH19_CH20:
				return new Character[] {RIEV, RIEV_CH19_CH20};
			case CAELLACH:
			case CAELLACH_CH15:
				return new Character[] {CAELLACH, CAELLACH_CH15};
			case PABLO:
			case PABLO_13A:
				return new Character[] {PABLO, PABLO_13A};
			case GLEN:
			case GLEN_CUTSCENE:
				return new Character[] {GLEN, GLEN_CUTSCENE};
			case FADO:
			case FADO_NPC:
				return new Character[] {FADO, FADO_NPC};
			case HAYDEN:
			case HAYDEN_NPC:
				return new Character[] {HAYDEN, HAYDEN_NPC};
			case ISMAIRE:
			case ISMAIRE_NPC:
				return new Character[] {ISMAIRE, ISMAIRE_NPC};
			default:
				return new Character[] {character};
			}
		}
	}
	
	public enum CharacterClass {
		NONE(0x00),
		
		TRAINEE(0x3D), PUPIL(0x3E), RECRUIT(0x47),
		
		EPHRAIM_LORD(0x01), CAVALIER(0x05), KNIGHT(0x09), THIEF(0x0D), MERCENARY(0x0F), MYRMIDON(0x13), ARCHER(0x19), WYVERN_RIDER(0x1F), MAGE(0x25), SHAMAN(0x2D), FIGHTER(0x3F), BRIGAND(0x41), PIRATE(0x42),
		MONK(0x44), PRIEST(0x45), SOLDIER(0x4E), TRAINEE_2(0x7E), PUPIL_2(0x7F),
		
		EIRIKA_LORD(0x02), CAVALIER_F(0x06), KNIGHT_F(0x0A), MYRMIDON_F(0x14), ARCHER_F(0x1A), MAGE_F(0x26), PEGASUS_KNIGHT(0x48), CLERIC(0x4A), TROUBADOUR(0x4B), DANCER(0x4D), RECRUIT_2(0x37),
		
		EPHRAIM_MASTER_LORD(0x03), PALADIN(0x07), GENERAL(0x0B), HERO(0x11), SWORDMASTER(0x15), ASSASSIN(0x17), SNIPER(0x1B), RANGER(0x1D), WYVERN_LORD(0x21), WYVERN_KNIGHT(0x23), SAGE(0x27), MAGE_KNIGHT(0x29), BISHOP(0x2B),
		DRUID(0x2F), SUMMONER(0x31), ROGUE(0x33), GREAT_KNIGHT(0x35), SUPER_TRAINEE(0x38), SUPER_PUPIL(0x39), WARRIOR(0x40), BERSERKER(0x43),
		
		EIRIKA_MASTER_LORD(0x04), PALADIN_F(0x08), GENERAL_F(0x0C), SWORDMASTER_F(0x16), ASSASSIN_F(0x18), SNIPER_F(0x1C), RANGER_F(0x1E), WYVERN_KNIGHT_F(0x24), SAGE_F(0x28), MAGE_KNIGHT_F(0x2A), BISHOP_F(0x2C),
		GREAT_KNIGHT_F(0x36), SUPER_RECRUIT(0x3A), MANAKETE_F(0x3C), FALCON_KNIGHT(0x49), VALKYRIE(0x4C),
		
		REVENANT(0x52), BONEWALKER(0x54), BONEWALKER_BOW(0x55), BAEL(0x58), MAUTHE_DOOG(0x5B), TARVOS(0x5D), MOGALL(0x5F), GARGOYLE(0x63),
		
		ENTOMBED(0x53), WIGHT(0x56), WIGHT_BOW(0x57), ELDER_BAEL(0x59), CYCLOPS(0x5A), GWYLLGI(0x5C), MAELDUIN(0x5E), ARCH_MOGALL(0x60), GORGON(0x61), DEATHGOYLE(0x64), CYCLOPS_2(0x7C), ELDER_BAEL_2(0x7D),
		
		MANAKETE(0x0E), MERCENARY_F(0x10), HERO_F(0x12), WYVERN_RIDER_F(0x20), WYVERN_LORD_F(0x22), SHAMAN_F(0x2E), DRUID_F(0x30), SUMMONER_F(0x32), MANAKETE_2(0x3B), BARD(0x46), 
		
		GORGON_EGG(0x34), NECROMANCER(0x4F), FLEET(0x50), GHOST_FIGHTER(0x51), DRACOZOMBIE(0x65), DEMON_KING(0x66)
		;
		
		public int ID;
		
		private static Map<Integer, CharacterClass> map = new HashMap<Integer, CharacterClass>();
		
		static {
			for (CharacterClass charClass : CharacterClass.values()) {
				map.put(charClass.ID, charClass);
			}
		}
		
		private CharacterClass(final int id) { ID = id; }
		
		public static CharacterClass valueOf(int classId) {
			return map.get(classId);
		}
		
		public static int[] classIDsForClassArray(CharacterClass[] classArray) {
			int[] idArray = new int[classArray.length];
			for (int i = 0; i < classArray.length; i++) {
				idArray[i] = classArray[i].ID;
			}
			
			return idArray;
		}
		
		public static Set<CharacterClass> allMaleClasses = new HashSet<CharacterClass>(Arrays.asList(TRAINEE, PUPIL, EPHRAIM_LORD, CAVALIER, KNIGHT, THIEF, MERCENARY, MYRMIDON, ARCHER, WYVERN_RIDER, MAGE, SHAMAN, 
				FIGHTER, BRIGAND, PIRATE, MONK, PRIEST, SOLDIER, TRAINEE_2, PUPIL_2, EPHRAIM_MASTER_LORD, PALADIN, GENERAL, HERO, SWORDMASTER, ASSASSIN, SNIPER, RANGER, WYVERN_LORD, WYVERN_KNIGHT, SAGE, 
				MAGE_KNIGHT, BISHOP, DRUID, SUMMONER, ROGUE, GREAT_KNIGHT, SUPER_TRAINEE, SUPER_PUPIL, WARRIOR, BERSERKER));
		public static Set<CharacterClass> allFemaleClasses = new HashSet<CharacterClass>(Arrays.asList(RECRUIT, EIRIKA_LORD, CAVALIER_F, KNIGHT_F, MYRMIDON_F, ARCHER_F, MAGE_F, PEGASUS_KNIGHT, CLERIC, TROUBADOUR, DANCER,
				RECRUIT_2, EIRIKA_MASTER_LORD, PALADIN_F, GENERAL_F, SWORDMASTER_F, ASSASSIN_F, SNIPER_F, RANGER_F, WYVERN_KNIGHT_F, SAGE_F, MAGE_KNIGHT_F, BISHOP_F,
				GREAT_KNIGHT_F, SUPER_RECRUIT, MANAKETE_F, FALCON_KNIGHT, VALKYRIE));
		public static Set<CharacterClass> allMonsterClasses = new HashSet<CharacterClass>(Arrays.asList(REVENANT, BONEWALKER, BONEWALKER_BOW, BAEL, MAUTHE_DOOG, TARVOS, MOGALL, GARGOYLE,
		ENTOMBED, WIGHT, WIGHT_BOW, ELDER_BAEL, CYCLOPS, GWYLLGI, MAELDUIN, ARCH_MOGALL, GORGON, DEATHGOYLE, CYCLOPS_2, ELDER_BAEL_2));
		
		public static Set<CharacterClass> allLordClasses = new HashSet<CharacterClass>(Arrays.asList(EIRIKA_LORD, EPHRAIM_LORD, EIRIKA_MASTER_LORD, EPHRAIM_MASTER_LORD));
		public static Set<CharacterClass> allThiefClasses = new HashSet<CharacterClass>(Arrays.asList(THIEF, ASSASSIN, ROGUE));
		
		public static Set<CharacterClass> allTraineeClasses = new HashSet<CharacterClass>(Arrays.asList(TRAINEE, PUPIL, RECRUIT));
		public static Set<CharacterClass> allUnpromotedClasses = new HashSet<CharacterClass>(Arrays.asList(EPHRAIM_LORD, CAVALIER, KNIGHT, THIEF, MERCENARY, MYRMIDON, ARCHER, WYVERN_RIDER, MAGE, SHAMAN, RECRUIT_2, 
				FIGHTER, BRIGAND, PIRATE, MONK, PRIEST, SOLDIER, TRAINEE_2, PUPIL_2, EIRIKA_LORD, CAVALIER_F, KNIGHT_F, MYRMIDON_F, ARCHER_F, MAGE_F, PEGASUS_KNIGHT, CLERIC, TROUBADOUR, DANCER, REVENANT, BONEWALKER, 
				BONEWALKER_BOW, BAEL, MAUTHE_DOOG, TARVOS, MOGALL, GARGOYLE));
		public static Set<CharacterClass> allPromotedClasses = new HashSet<CharacterClass>(Arrays.asList(EPHRAIM_MASTER_LORD, PALADIN, GENERAL, HERO, SWORDMASTER, ASSASSIN, SNIPER, RANGER, WYVERN_LORD, WYVERN_KNIGHT, 
				SAGE, MAGE_KNIGHT, BISHOP, DRUID, SUMMONER, ROGUE, GREAT_KNIGHT, SUPER_TRAINEE, SUPER_PUPIL, WARRIOR, BERSERKER, EIRIKA_MASTER_LORD, PALADIN_F, GENERAL_F, SWORDMASTER_F, ASSASSIN_F, SNIPER_F, RANGER_F, 
				WYVERN_KNIGHT_F, SAGE_F, MAGE_KNIGHT_F, BISHOP_F, GREAT_KNIGHT_F, SUPER_RECRUIT, MANAKETE_F, FALCON_KNIGHT, VALKYRIE, ENTOMBED, WIGHT, WIGHT_BOW, ELDER_BAEL, CYCLOPS, GWYLLGI, MAELDUIN, ARCH_MOGALL, 
				GORGON, DEATHGOYLE, CYCLOPS_2, ELDER_BAEL_2));
		
		public static Set<CharacterClass> allPacifistClasses = new HashSet<CharacterClass>(Arrays.asList(DANCER, CLERIC, TROUBADOUR, PRIEST));
		public static Set<CharacterClass> allMeleeLockedClasses = new HashSet<CharacterClass>(Arrays.asList(MYRMIDON, MERCENARY, EIRIKA_LORD, THIEF, MYRMIDON_F, BAEL, ELDER_BAEL, ELDER_BAEL_2, MAUTHE_DOOG,
				GWYLLGI, REVENANT, ENTOMBED));
		public static Set<CharacterClass> allRangeLockedClasses = new HashSet<CharacterClass>(Arrays.asList(ARCHER, ARCHER_F, SNIPER, SNIPER_F, BONEWALKER_BOW, WIGHT_BOW));
		
		public static Set<CharacterClass> allValidClasses = new HashSet<CharacterClass>(Arrays.asList(EPHRAIM_LORD, CAVALIER, KNIGHT, THIEF, MERCENARY, MYRMIDON, ARCHER, WYVERN_RIDER, MAGE, SHAMAN, 
				FIGHTER, BRIGAND, PIRATE, MONK, PRIEST, SOLDIER, TRAINEE_2, PUPIL_2, EPHRAIM_MASTER_LORD, PALADIN, GENERAL, HERO, SWORDMASTER, ASSASSIN, SNIPER, RANGER, WYVERN_LORD, WYVERN_KNIGHT, SAGE, 
				MAGE_KNIGHT, BISHOP, DRUID, SUMMONER, ROGUE, GREAT_KNIGHT, SUPER_TRAINEE, SUPER_PUPIL, WARRIOR, BERSERKER, EIRIKA_LORD, CAVALIER_F, KNIGHT_F, MYRMIDON_F, ARCHER_F, MAGE_F, PEGASUS_KNIGHT, CLERIC, 
				TROUBADOUR, DANCER, RECRUIT_2, EIRIKA_MASTER_LORD, PALADIN_F, GENERAL_F, SWORDMASTER_F, ASSASSIN_F, SNIPER_F, RANGER_F, WYVERN_KNIGHT_F, SAGE_F, MAGE_KNIGHT_F, BISHOP_F,
				GREAT_KNIGHT_F, SUPER_RECRUIT, MANAKETE_F, FALCON_KNIGHT, VALKYRIE, REVENANT, BONEWALKER, BONEWALKER_BOW, BAEL, MAUTHE_DOOG, TARVOS, MOGALL, GARGOYLE,
				ENTOMBED, WIGHT, WIGHT_BOW, ELDER_BAEL, CYCLOPS, GWYLLGI, MAELDUIN, ARCH_MOGALL, GORGON, DEATHGOYLE, CYCLOPS_2, ELDER_BAEL_2));
		
		private static Boolean isClassPromoted(CharacterClass sourceClass) {
			return allPromotedClasses.contains(sourceClass);
		}
		
		public static CharacterClass[] classesThatLoseToClass(CharacterClass originalClass, CharacterClass winningClass, Boolean excludeLords, Boolean excludeThieves) {
			Set<CharacterClass> classList = new HashSet<CharacterClass>();
			
			switch (winningClass) {
			case EIRIKA_LORD:
			case MYRMIDON_F:
			case MERCENARY:
			case MYRMIDON: 
			case BONEWALKER:
			case THIEF: {
				classList.add(FIGHTER);
				classList.add(BRIGAND);
				classList.add(PIRATE);
				break;
			}
			case FIGHTER:
			case BRIGAND:
			case PIRATE:
			case TARVOS:
			case TRAINEE_2: {
				classList.add(PEGASUS_KNIGHT);
				classList.add(SOLDIER);
				break;
			}
			case KNIGHT:
			case GARGOYLE:
			case SOLDIER:
			case PEGASUS_KNIGHT: {
				classList.add(MYRMIDON);
				classList.add(MYRMIDON_F);
				classList.add(MERCENARY);
				classList.add(SOLDIER);
				if (!excludeThieves) {
					classList.add(THIEF);
				}
				break;
			}
			case MONK: {
				classList.add(SHAMAN);
				classList.add(KNIGHT);
				classList.add(SOLDIER);
				break;
			}
			case MAGE:
			case MAGE_F: {
				classList.add(MONK);
				classList.add(KNIGHT);
				classList.add(SOLDIER);
				break;
			}
			case SHAMAN: {
				classList.add(MAGE);
				classList.add(MAGE_F);
				classList.add(KNIGHT);
				classList.add(SOLDIER);
				break;
			}
			case ARCHER:
			case ARCHER_F: {
				classList.add(PEGASUS_KNIGHT);
				classList.add(SOLDIER);
				break;
			}
			default:
				break;
			}
			
			return classList.toArray(new CharacterClass[classList.size()]);
		}
		
		public static CharacterClass[] targetClassesForRandomization(CharacterClass sourceClass, Boolean excludeSource, Boolean excludeLords, Boolean excludeThieves, Boolean separateMonsterClasses, Boolean requireAttack, Boolean requiresRange, Boolean requiresMelee, Boolean applyRestrictions) {
			CharacterClass[] limited = limitedClassesForRandomization(sourceClass, separateMonsterClasses, requiresRange, requiresMelee);
			if (limited != null && applyRestrictions) {
				return limited;
			}
			
			Set<CharacterClass> classList;
			
			if (isClassPromoted(sourceClass)) {
				Set<CharacterClass> promoted = new HashSet<CharacterClass>(allPromotedClasses);
				if (excludeSource) {
					promoted.remove(sourceClass);
				}
				classList = promoted;
				
			} else {
				Set<CharacterClass> unpromoted = new HashSet<CharacterClass>(allUnpromotedClasses);
				if (excludeSource) {
					unpromoted.remove(sourceClass);
				}
				classList = unpromoted;
			}
			
			if (excludeLords) {
				classList.removeAll(allLordClasses);
			}
			
			if (excludeThieves) {
				classList.removeAll(allThiefClasses);
			}
			
			if (separateMonsterClasses) {
				if (allMonsterClasses.contains(sourceClass)) {
					classList.retainAll(allMonsterClasses);
				} else {
					classList.removeAll(allMonsterClasses);
				}
			}
			
			// Until we figure out how trainees work universally, disable tier 0 trainees.
			classList.removeAll(allTraineeClasses);
			
			if (requireAttack) {
				classList.removeAll(allPacifistClasses);
			}
			if (requiresRange) {
				classList.removeAll(allPacifistClasses);
				classList.removeAll(allMeleeLockedClasses);
			}
			if (requiresMelee) {
				classList.removeAll(allPacifistClasses);
				classList.removeAll(allRangeLockedClasses);
			}
			
			return classList.toArray(new CharacterClass[classList.size()]);
		}
		
		private static CharacterClass[] limitedClassesForRandomization(CharacterClass sourceClass, Boolean separateMonsters, Boolean requireRange, Boolean requireMelee) {
			if (separateMonsters) {
				switch(sourceClass) {
				case WYVERN_RIDER:
				case PEGASUS_KNIGHT:
					return new CharacterClass[] {WYVERN_RIDER, PEGASUS_KNIGHT};
				case WYVERN_KNIGHT:
				case WYVERN_KNIGHT_F:
				case WYVERN_LORD:
				case FALCON_KNIGHT:
					return new CharacterClass[] {WYVERN_LORD, WYVERN_KNIGHT, WYVERN_KNIGHT_F, FALCON_KNIGHT};
				case GARGOYLE:
				case MOGALL:
					return new CharacterClass[] {GARGOYLE, MOGALL};
				case DEATHGOYLE:
				case ARCH_MOGALL:
					return new CharacterClass[] {DEATHGOYLE, ARCH_MOGALL};
				case PIRATE:
					return new CharacterClass[] {PIRATE, WYVERN_RIDER, PEGASUS_KNIGHT};
				case BRIGAND:
					return new CharacterClass[] {BRIGAND, WYVERN_RIDER, PEGASUS_KNIGHT};
				case BAEL:
					return new CharacterClass[] {BAEL, MOGALL, GARGOYLE};
				case ELDER_BAEL:
				case ELDER_BAEL_2:
					return new CharacterClass[] {ELDER_BAEL, ELDER_BAEL_2, ARCH_MOGALL, DEATHGOYLE};
				case BERSERKER:
					return new CharacterClass[] {BERSERKER, WYVERN_LORD, WYVERN_KNIGHT, WYVERN_KNIGHT_F, FALCON_KNIGHT};
				default:
					return null;
				}
			} else {
				switch(sourceClass) {
				case WYVERN_RIDER:
				case PEGASUS_KNIGHT:
				case GARGOYLE:
				case MOGALL:
					return new CharacterClass[] {WYVERN_RIDER, PEGASUS_KNIGHT, GARGOYLE, MOGALL};
				case WYVERN_KNIGHT:
				case WYVERN_KNIGHT_F:
				case WYVERN_LORD:
				case FALCON_KNIGHT:
				case DEATHGOYLE:
				case ARCH_MOGALL:
					return new CharacterClass[] {WYVERN_LORD, WYVERN_KNIGHT, WYVERN_KNIGHT_F, FALCON_KNIGHT, DEATHGOYLE, ARCH_MOGALL};
				case PIRATE:
					return new CharacterClass[] {PIRATE, WYVERN_RIDER, PEGASUS_KNIGHT, GARGOYLE, MOGALL};
				case BRIGAND:
				case BAEL:
					if (requireRange) {
						return new CharacterClass[] {BRIGAND, WYVERN_RIDER, PEGASUS_KNIGHT, MOGALL, GARGOYLE};
					} else {
						return new CharacterClass[] {BRIGAND, WYVERN_RIDER, PEGASUS_KNIGHT, BAEL, MOGALL, GARGOYLE};
					}
				case ELDER_BAEL:
				case ELDER_BAEL_2:
				case BERSERKER:
					if (requireRange) {
						return new CharacterClass[] {BERSERKER, WYVERN_LORD, WYVERN_KNIGHT, WYVERN_KNIGHT_F, FALCON_KNIGHT, ARCH_MOGALL, DEATHGOYLE};
					} else {
						return new CharacterClass[] {BERSERKER, WYVERN_LORD, WYVERN_KNIGHT, WYVERN_KNIGHT_F, FALCON_KNIGHT, ELDER_BAEL, ELDER_BAEL_2, ARCH_MOGALL, DEATHGOYLE};
					}
				default:
					return null;
				}	
			}
		}
	}
	
	public enum Item {
		NONE(0x00),
		
		IRON_SWORD(0x01), SLIM_SWORD(0x02), STEEL_SWORD(0x03), SILVER_SWORD(0x04), IRON_BLADE(0x05), STEEL_BLADE(0x06), SILVER_BLADE(0x07), POISON_SWORD(0x08), RAPIER(0x09),
		BRAVE_SWORD(0x0B), SHAMSHIR(0x0C), KILLING_EDGE(0x0D), ARMORSLAYER(0x0E), WYRMSLAYER(0x0F), LIGHT_BRAND(0x10), RUNE_SWORD(0x11), LANCEREAVER(0x12), ZANBATO(0x13),
		SHADOWKILLER(0x81), SIEGLINDE(0x85), AUDHULMA(0x91), WIND_SWORD(0xA1),
		
		IRON_LANCE(0x14), SLIM_LANCE(0x15), STEEL_LANCE(0x16), SILVER_LANCE(0x17), TOXIN_LANCE(0x18), BRAVE_LANCE(0x19), KILLER_LANCE(0x1A), HORSESLAYER(0x1B), JAVELIN(0x1C),
		SPEAR(0x1D), AXEREAVER(0x1E), REGINLEIF(0x78), BRIGHT_LANCE(0x82), DRAGONSPEAR(0x8D), VIDOFNIR(0x8E), SIEGMUND(0x92), HEAVY_SPEAR(0x95), SHORT_SPEAR(0x96),
		
		IRON_AXE(0x1F), STEEL_AXE(0x20), SILVER_AXE(0x21), POISON_AXE(0x22), BRAVE_AXE(0x23), KILLER_AXE(0x24), HALBERD(0x25), HAMMER(0x26), DEVIL_AXE(0x27), HAND_AXE(0x28),
		TOMAHAWK(0x29), SWORDREAVER(0x2A), SWORDSLAYER(0x2B), HATCHET(0x2C), DRAGON_AXE(0x5A), FIENDCLEAVER(0x83), BATTLE_AXE(0x86), GARM(0x93),
		
		IRON_BOW(0x2D), STEEL_BOW(0x2E), SILVER_BOW(0x2F), POISON_BOW(0x30), KILLER_BOW(0x31), BRAVE_BOW(0x32), SHORT_BOW(0x33), LONG_BOW(0x34), BEACON_BOW(0x84), NIDHOGG(0x94),
		
		FIRE(0x38), THUNDER(0x39), ELFIRE(0x3A), BOLTING(0x3B), FIMBULVETR(0x3C), EXCALIBUR(0x3E),
		
		LIGHTNING(0x3F), SHINE(0x40), DIVINE(0x41), PURGE(0x42), AURA(0x43), IVALDI(0x87),
		
		FLUX(0x45), LUNA(0x46), NOSFERATU(0x47), ECLIPSE(0x48), FENRIR(0x49), GLEIPNIR(0x4A), NAGLFAR(0x8F),
		
		HEAL(0x4B), MEND(0x4C), RECOVER(0x4D), PHYSIC(0x4E), FORTIFY(0x4F), LATONA(0x8C),
		
		RESTORE(0x50), WARP(0x54), RESCUE(0x55), TORCH_STAFF(0x56), HAMMERNE(0x57), UNLOCK(0x58), BARRIER(0x59),
		
		SILENCE(0x51), SLEEP(0x52), BERSERK(0x53),
		
		SHARP_CLAW(0x8B), WRETCHED_AIR(0x90), DRAGONSTONE(0xAA), DEMON_SURGE(0xAB), SHADOWSHOT(0xAC), ROTTEN_CLAW(0xAD), FETID_CLAW(0xAE), POISON_CLAW(0xAF), LETHAL_TALON(0xB0),
		FIERY_FANG(0xB1), HELLFANG(0xB2), EVIL_EYE(0xB3), CRIMSON_EYE(0xB4), STONE(0xB5), 
		
		ANGELIC_ROBE(0x5B), ENERGY_RING(0x5C), SECRET_BOOK(0x5D), SPEEDWINGS(0x5E), GODDESS_ICON(0x5F), DRAGONSHIELD(0x60), TALISMAN(0x61), BOOTS(0x62), BODY_RING(0x63),
		
		HERO_CREST(0x64), KNIGHT_CREST(0x65), ORION_BOLT(0x66), ELYSIAN_WHIP(0x67), GUIDING_RING(0x68), MASTER_SEAL(0x88), CONQUORER_PROOF(0x97), MOON_BRACELET(0x98), SUN_BRACELET(0x99),
		
		CHEST_KEY(0x69), DOOR_KEY(0x6A), LOCKPICK(0x6B), CHEST_KEY_5(0x79),
		
		VULNERARY(0x6C), ELIXIR(0x6D), PURE_WATER(0x6E), ANTITOXIN(0x6F), TORCH(0x70), 
		
		FILI_SHIELD(0x71), MEMBER_CARD(0x72), SILVER_CARD(0x73), HOPLON_GUARD(0x7C), METIS_TOME(0x89),
		
		WHITE_GEM(0x74), BLUE_GEM(0x75), RED_GEM(0x76), BLACK_GEM(0xBA), GOLD_GEM(0xBB),
		
		GOLD_1(0x77), GOLD_1_AGAIN(0x9A), GOLD_5(0x9B), GOLD_10(0x9C), GOLD_50(0x9D), GOLD_100(0x9E), GOLD_3000(0x9F), GOLD_5000(0xA0),
		
		;
		public int ID;
		
		private static Map<Integer, Item> map = new HashMap<Integer, Item>();
		
		static {
			for (Item item : Item.values()) {
				map.put(item.ID, item);
			}
		}
		
		private Item(final int id) { ID = id; }
		
		public static Item valueOf(int itemId) {
			return map.get(itemId);
		}
		
		public static int[] itemIDsForItemArray(Item[] itemArray) {
			int[] idArray = new int[itemArray.length];
			for (int i = 0; i < itemArray.length; i++) {
				idArray[i] = itemArray[i].ID;
			}
			
			return idArray;
		}
		
		public enum FE8WeaponRank {
			E(0x01), D(0x1F), C(0x47), B(0x79), A(0x0B5), S(0x0FB);
			
			public int value;
			
			private static Map<Integer, FE8WeaponRank> map = new HashMap<Integer, FE8WeaponRank>();
			
			static {
				for (FE8WeaponRank rank : FE8WeaponRank.values()) {
					map.put(rank.value, rank);
				}
			}
			
			private FE8WeaponRank(final int value) { this.value = value; }
			
			public static FE8WeaponRank valueOf(int rankVal) {
				return map.get(rankVal);
			}
			
			public static FE8WeaponRank rankFromGeneralRank(WeaponRank generalRank) {
				switch (generalRank) {
				case A:
					return A;
				case B:
					return B;
				case C:
					return C;
				case D:
					return D;
				case E:
					return E;
				case NONE:
					return null;
				case S:
					return S;
				default:
					return null;
				}
			}
			
			public WeaponRank toGeneralRank() {
				switch (this) {
				case A:
					return WeaponRank.A;
				case B:
					return WeaponRank.B;
				case C:
					return WeaponRank.C;
				case D:
					return WeaponRank.D;
				case E:
					return WeaponRank.E;
				case S:
					return WeaponRank.S;
				default:
					return WeaponRank.NONE;
				}
			}
			
			public Boolean isHigherThanRank(FE8WeaponRank otherRank) {
				return (value & 0xFF) > (otherRank.value & 0xFF);
			}
			
			public Boolean isLowerThanRank(FE8WeaponRank otherRank) {
				return (value & 0xFF) < (otherRank.value & 0xFF);
			}
			
			public Boolean isSameRank(FE8WeaponRank otherRank) {
				return value == otherRank.value;
			}
		}
		
		public enum FE8WeaponType {
			SWORD(0x00), LANCE(0x01), AXE(0x02), BOW(0x03), STAFF(0x04), ANIMA(0x05), LIGHT(0x06), DARK(0x07), 
			
			ITEM(0x09), 
			
			MONSTER_WEAPON(0x0B), 
			
			RING(0x0C),
			
			FIRE_DRAGON_STONE(0x11),
			
			DANCING_RING(0x12);
			
			public int ID;
			
			private static Map<Integer, FE8WeaponType> map = new HashMap<Integer, FE8WeaponType>();
			
			static {
				for (FE8WeaponType type : FE8WeaponType.values()) {
					map.put(type.ID, type);
				}
			}
			
			private FE8WeaponType(final int id) { ID = id; }
			
			public static FE8WeaponType valueOf(int typeVal) {
				return map.get(typeVal);
			}
			
			public WeaponType toGeneralType() {
				switch (this) {
				case SWORD:
					return WeaponType.SWORD;
				case LANCE:
					return WeaponType.LANCE;
				case AXE:
					return WeaponType.AXE;
				case BOW:
					return WeaponType.BOW;
				case ANIMA:
					return WeaponType.ANIMA;
				case LIGHT:
					return WeaponType.LIGHT;
				case DARK:
					return WeaponType.DARK;
				case STAFF:
					return WeaponType.STAFF;
				default:
					return WeaponType.NOT_A_WEAPON;
				}
			}
		}
		
		public enum Ability1Mask {
			NONE(0x00), WEAPON(0x01), MAGIC(0x02), STAFF(0x04), UNBREAKABLE(0x08), 
			UNSELLABLE(0x10), BRAVE(0x20), MAGIC_DAMAGE(0x40), UNCOUNTERABLE(0x80);
			
			public int ID;
			
			private static Map<Integer, Ability1Mask> map = new HashMap<Integer, Ability1Mask>();
			
			static {
				for (Ability1Mask ability : Ability1Mask.values()) {
					map.put(ability.ID, ability);
				}
			}
			
			private Ability1Mask(final int id) { ID = id; }
			
			public static Ability1Mask valueOf(int val) {
				return map.get(val);
			}
			
			public static String stringOfActiveAbilities(int abilityValue, String delimiter) {
				List<String> strings = new ArrayList<String>();
				for (Ability1Mask mask : Ability1Mask.values()) {
					if ((abilityValue & mask.ID) != 0) { strings.add(WhyDoesJavaNotHaveThese.stringByCapitalizingFirstLetter(mask.toString())); }
				}
				return String.join(delimiter, strings);
			}
		}
		
		public enum Ability2Mask {
			NONE(0x00), REVERSE_WEAPON_TRIANGLE(0x01), MONSTER_LOCK(0x04), 
			MYRMIDON_LOCK(0x10), FILI_SHIELD_EFFECT(0x40), HOPLON_GUARD_EFFECT(0x80);
			public int ID;
			
			private static Map<Integer, Ability2Mask> map = new HashMap<Integer, Ability2Mask>();
			
			static {
				for (Ability2Mask ability : Ability2Mask.values()) {
					map.put(ability.ID, ability);
				}
			}
			
			private Ability2Mask(final int id) { ID = id; }
			
			public static Ability2Mask valueOf(int val) {
				return map.get(val);
			}
			
			public static String stringOfActiveAbilities(int abilityValue, String delimiter) {
				List<String> strings = new ArrayList<String>();
				for (Ability2Mask mask : Ability2Mask.values()) {
					if ((abilityValue & mask.ID) != 0) { strings.add(WhyDoesJavaNotHaveThese.stringByCapitalizingFirstLetter(mask.toString())); }
				}
				return String.join(delimiter, strings);
			}
		}
		
		public enum Ability3Mask {
			NONE(0x00), UNUSABLE(0x01), NEGATE_DEFENSE(0x02), EIRIKA_LOCK(0x04), EPHRAIM_LOCK(0x08);
			
			public int ID;
			
			private static Map<Integer, Ability3Mask> map = new HashMap<Integer, Ability3Mask>();
			
			static {
				for (Ability3Mask ability : Ability3Mask.values()) {
					map.put(ability.ID, ability);
				}
			}
			
			private Ability3Mask(final int id) { ID = id; }
			
			public static Ability3Mask valueOf(int val) {
				return map.get(val);
			}
			
			public static String stringOfActiveAbilities(int abilityValue, String delimiter) {
				List<String> strings = new ArrayList<String>();
				for (Ability3Mask mask : Ability3Mask.values()) {
					if ((abilityValue & mask.ID) != 0) { strings.add(WhyDoesJavaNotHaveThese.stringByCapitalizingFirstLetter(mask.toString())); }
				}
				return String.join(delimiter, strings);
			}
		}
		
		public enum WeaponEffect {
			NONE(0x00), POISON(0x01), STEALS_HP(0x02), HALVES_HP(0x03), DEVIL(0x04), PETRIFY(0x05);
			
			public int ID;
			
			private static Map<Integer, WeaponEffect> map = new HashMap<Integer, WeaponEffect>();
			
			static {
				for (WeaponEffect effect : WeaponEffect.values()) {
					map.put(effect.ID, effect);
				}
			}
			
			private WeaponEffect(final int id) { ID = id; }
			
			public static WeaponEffect valueOf(int val) {
				return map.get(val);
			}
			
			public static String stringOfActiveEffect(int effectValue) {
				for (WeaponEffect effect : WeaponEffect.values()) {
					if (effectValue == effect.ID) { return WhyDoesJavaNotHaveThese.stringByCapitalizingFirstLetter(effect.toString()); }
				}
				
				return "Unknown";
			}
		}
		
		public static Set<Item> allSwords = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, POISON_SWORD, RAPIER,
				BRAVE_SWORD, SHAMSHIR, KILLING_EDGE, ARMORSLAYER, WYRMSLAYER, LIGHT_BRAND, RUNE_SWORD, LANCEREAVER, ZANBATO, SHADOWKILLER, SIEGLINDE, AUDHULMA, WIND_SWORD));
		public static Set<Item> allLances = new HashSet<Item>(Arrays.asList(IRON_LANCE, SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, TOXIN_LANCE, BRAVE_LANCE, KILLER_LANCE, HORSESLAYER, JAVELIN,
				SPEAR, AXEREAVER, REGINLEIF, BRIGHT_LANCE, DRAGONSPEAR, VIDOFNIR, SIEGMUND, HEAVY_SPEAR, SHORT_SPEAR));
		public static Set<Item> allAxes = new HashSet<Item>(Arrays.asList(IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, KILLER_AXE, HALBERD, HAMMER, DEVIL_AXE, HAND_AXE,
				TOMAHAWK, SWORDREAVER, SWORDSLAYER, HATCHET, DRAGON_AXE, FIENDCLEAVER, BATTLE_AXE, GARM));
		public static Set<Item> allBows = new HashSet<Item>(Arrays.asList(IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONG_BOW, BEACON_BOW, NIDHOGG));
		public static Set<Item> allAnima = new HashSet<Item>(Arrays.asList(FIRE, THUNDER, ELFIRE, BOLTING, FIMBULVETR, EXCALIBUR));
		public static Set<Item> allLight = new HashSet<Item>(Arrays.asList(LIGHTNING, SHINE, DIVINE, PURGE, AURA, IVALDI));
		public static Set<Item> allDark = new HashSet<Item>(Arrays.asList(FLUX, LUNA, NOSFERATU, ECLIPSE, FENRIR, GLEIPNIR, NAGLFAR));
		public static Set<Item> allMonsterWeapons = new HashSet<Item>(Arrays.asList(SHARP_CLAW, WRETCHED_AIR, DRAGONSTONE, DEMON_SURGE, SHADOWSHOT, ROTTEN_CLAW, FETID_CLAW, POISON_CLAW, LETHAL_TALON,
		FIERY_FANG, HELLFANG, EVIL_EYE, CRIMSON_EYE, STONE));
		public static Set<Item> allHealingStaves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, PHYSIC, FORTIFY, LATONA));
		public static Set<Item> allSupportStaves = new HashSet<Item>(Arrays.asList(RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER));
		public static Set<Item> allStatusStaves = new HashSet<Item>(Arrays.asList(SILENCE, SLEEP, BERSERK));
		public static Set<Item> allStatBoosters = new HashSet<Item>(Arrays.asList(ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWINGS, GODDESS_ICON, DRAGONSHIELD, TALISMAN, BOOTS, BODY_RING));
		public static Set<Item> allPromotionItems = new HashSet<Item>(Arrays.asList(HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP, GUIDING_RING));
		public static Set<Item> allSpecialItems = new HashSet<Item>(Arrays.asList(FILI_SHIELD, MEMBER_CARD, HOPLON_GUARD, SILVER_CARD, METIS_TOME));
		public static Set<Item> allMoneyItems = new HashSet<Item>(Arrays.asList(WHITE_GEM, BLUE_GEM, RED_GEM, BLACK_GEM, GOLD_GEM));
		public static Set<Item> usableItems = new HashSet<Item>(Arrays.asList(CHEST_KEY, CHEST_KEY_5, DOOR_KEY, LOCKPICK, VULNERARY, ELIXIR, PURE_WATER, ANTITOXIN, TORCH));
		
		public static Set<Item> allPotentialRewards = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, POISON_SWORD, RAPIER,
				BRAVE_SWORD, SHAMSHIR, KILLING_EDGE, ARMORSLAYER, WYRMSLAYER, LIGHT_BRAND, RUNE_SWORD, LANCEREAVER, ZANBATO, SHADOWKILLER, WIND_SWORD,
				IRON_LANCE, SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, TOXIN_LANCE, BRAVE_LANCE, KILLER_LANCE, HORSESLAYER, JAVELIN,
				SPEAR, AXEREAVER, REGINLEIF, BRIGHT_LANCE, DRAGONSPEAR, HEAVY_SPEAR, SHORT_SPEAR,
				IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, KILLER_AXE, HALBERD, HAMMER, DEVIL_AXE, HAND_AXE,
				TOMAHAWK, SWORDREAVER, SWORDSLAYER, HATCHET, DRAGON_AXE, FIENDCLEAVER, BATTLE_AXE,
				IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONG_BOW, BEACON_BOW,
				FIRE, THUNDER, ELFIRE, BOLTING, FIMBULVETR,
				LIGHTNING, SHINE, DIVINE, PURGE, AURA,
				FLUX, LUNA, NOSFERATU, ECLIPSE, FENRIR,
				HEAL, MEND, RECOVER, PHYSIC, FORTIFY,
				RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER,
				SILENCE, SLEEP, BERSERK,
				ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWINGS, GODDESS_ICON, DRAGONSHIELD, TALISMAN, BOOTS, BODY_RING,
				HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP, GUIDING_RING,
				FILI_SHIELD, MEMBER_CARD, HOPLON_GUARD, SILVER_CARD, METIS_TOME,
				WHITE_GEM, BLUE_GEM, RED_GEM, BLACK_GEM, GOLD_GEM,
				CHEST_KEY, CHEST_KEY_5, DOOR_KEY, LOCKPICK, VULNERARY, ELIXIR, PURE_WATER, ANTITOXIN, TORCH));
		
		public static Set<Item> allWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, POISON_SWORD, RAPIER,
				BRAVE_SWORD, SHAMSHIR, KILLING_EDGE, ARMORSLAYER, WYRMSLAYER, LIGHT_BRAND, RUNE_SWORD, LANCEREAVER, ZANBATO, SHADOWKILLER, SIEGLINDE, AUDHULMA, WIND_SWORD,
				IRON_LANCE, SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, TOXIN_LANCE, BRAVE_LANCE, KILLER_LANCE, HORSESLAYER, JAVELIN,
				SPEAR, AXEREAVER, REGINLEIF, BRIGHT_LANCE, DRAGONSPEAR, VIDOFNIR, SIEGMUND, HEAVY_SPEAR, SHORT_SPEAR,
				IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, KILLER_AXE, HALBERD, HAMMER, DEVIL_AXE, HAND_AXE,
				TOMAHAWK, SWORDREAVER, SWORDSLAYER, HATCHET, DRAGON_AXE, FIENDCLEAVER, BATTLE_AXE, GARM,
				IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONG_BOW, BEACON_BOW, NIDHOGG,
				FIRE, THUNDER, ELFIRE, BOLTING, FIMBULVETR, EXCALIBUR,
				LIGHTNING, SHINE, DIVINE, PURGE, AURA, IVALDI,
				FLUX, LUNA, NOSFERATU, ECLIPSE, FENRIR, GLEIPNIR, NAGLFAR,
				SHARP_CLAW, WRETCHED_AIR, DRAGONSTONE, DEMON_SURGE, SHADOWSHOT, ROTTEN_CLAW, FETID_CLAW, POISON_CLAW, LETHAL_TALON,
				FIERY_FANG, HELLFANG, EVIL_EYE, CRIMSON_EYE, STONE));
		public static Set<Item> allRangedWeapons = new HashSet<Item>(Arrays.asList(LIGHT_BRAND, RUNE_SWORD, WIND_SWORD, JAVELIN, SPEAR, SHORT_SPEAR, HAND_AXE, TOMAHAWK, IRON_BOW, STEEL_BOW,
				SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONG_BOW, BEACON_BOW, NIDHOGG, FIRE, THUNDER, ELFIRE, FIMBULVETR, EXCALIBUR, LIGHTNING, SHINE, DIVINE, 
				AURA, IVALDI, FLUX, LUNA, NOSFERATU, FENRIR, GLEIPNIR, NAGLFAR, WRETCHED_AIR, DEMON_SURGE, EVIL_EYE, CRIMSON_EYE));
		public static Set<Item> allRangedOnlyWeapons = new HashSet<Item>(Arrays.asList(IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONG_BOW, BEACON_BOW, NIDHOGG));
		public static Set<Item> allStaves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, PHYSIC, FORTIFY, RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER, SILENCE, SLEEP, BERSERK));
		
		public static Set<Item> allERank = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, SHADOWKILLER, IRON_LANCE, SLIM_LANCE, TOXIN_LANCE, JAVELIN, BRIGHT_LANCE, IRON_AXE, STEEL_AXE, DEVIL_AXE,
				HAND_AXE, HATCHET, FIENDCLEAVER, IRON_BOW, BEACON_BOW, HEAL, FIRE, LIGHTNING));
		public static Set<Item> allDRank = new HashSet<Item>(Arrays.asList(STEEL_SWORD, IRON_BLADE, POISON_SWORD, SHAMSHIR, ARMORSLAYER, ZANBATO, STEEL_LANCE, HORSESLAYER, HEAVY_SPEAR,
				POISON_AXE, HALBERD, HAMMER, STEEL_BOW, POISON_BOW, SHORT_BOW, LONG_BOW, MEND, TORCH_STAFF, UNLOCK, THUNDER, SHINE, FLUX));
		public static Set<Item> allCRank = new HashSet<Item>(Arrays.asList(STEEL_BLADE, KILLING_EDGE, WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, KILLER_LANCE, AXEREAVER, DRAGONSPEAR, SHORT_SPEAR, KILLER_AXE,
				SWORDREAVER, SWORDSLAYER, DRAGON_AXE, KILLER_BOW, RECOVER, RESTORE, SLEEP, HAMMERNE, BARRIER, ELFIRE, DIVINE, LUNA, NOSFERATU));
		public static Set<Item> allBRank = new HashSet<Item>(Arrays.asList(BRAVE_SWORD, WIND_SWORD, BRAVE_LANCE, SPEAR, BRAVE_AXE, BATTLE_AXE, BRAVE_BOW, PHYSIC, SILENCE, BERSERK, RESCUE, BOLTING, PURGE, ECLIPSE));
		public static Set<Item> allARank = new HashSet<Item>(Arrays.asList(SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, SILVER_LANCE, SILVER_AXE, TOMAHAWK, SILVER_BOW, FORTIFY, WARP, FIMBULVETR, AURA, FENRIR));
		public static Set<Item> allSRank = new HashSet<Item>(Arrays.asList(AUDHULMA, VIDOFNIR, GARM, NIDHOGG, LATONA, EXCALIBUR, IVALDI, GLEIPNIR, NAGLFAR));
		public static Set<Item> allPrfRank = new HashSet<Item>(Arrays.asList(REGINLEIF, RAPIER, SIEGMUND, SIEGLINDE));
		
		public static Set<Item> allBasicWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, IRON_LANCE, IRON_AXE, IRON_BOW, FIRE, LIGHTNING, FLUX, ROTTEN_CLAW, FIERY_FANG));
		
		public static Item[] basicItemsOfType(WeaponType type) {
			Set<Item> set = new HashSet<Item>();
			set.addAll(Arrays.asList(weaponsOfType(type)));
			set.retainAll(allBasicWeapons);
			return set.toArray(new Item[set.size()]);
		}
		
		public static Item[] formerThiefKit() {
			return new Item[] {CHEST_KEY_5, DOOR_KEY, DOOR_KEY};
		}
		
		public static Item[] specialClassKit(int classID, Random rng) {
			if (classID == FE8Data.CharacterClass.DANCER.ID) {
				return new Item[] {ELIXIR};
			} else if (classID == FE8Data.CharacterClass.REVENANT.ID || classID == FE8Data.CharacterClass.BAEL.ID) {
				int random = rng.nextInt(3);
				if (random == 0) { return new Item[] {ROTTEN_CLAW}; }
				else if (random == 1) { return new Item[] {LETHAL_TALON}; }
				else { return new Item[] {POISON_CLAW}; }
			} else if (classID == FE8Data.CharacterClass.ENTOMBED.ID || classID == FE8Data.CharacterClass.ELDER_BAEL.ID || classID == FE8Data.CharacterClass.ELDER_BAEL_2.ID) {
				int random = rng.nextInt(2);
				if (random == 0) { return new Item[] {FETID_CLAW}; }
				else { return new Item[] {SHARP_CLAW}; }
			} else if (classID == FE8Data.CharacterClass.MAUTHE_DOOG.ID) {
				return new Item[] {FIERY_FANG};
			} else if(classID == FE8Data.CharacterClass.GWYLLGI.ID) {
				return new Item[] {HELLFANG};
			} else if (classID == FE8Data.CharacterClass.MOGALL.ID) {
				return new Item[] {EVIL_EYE};
			} else if (classID == FE8Data.CharacterClass.ARCH_MOGALL.ID) {
				int random = rng.nextInt(3);
				if (random == 0) { return new Item[] {CRIMSON_EYE, SHADOWSHOT}; }
				else { return new Item[] {CRIMSON_EYE}; }
			} else if (classID == FE8Data.CharacterClass.GORGON.ID) {
				int random = rng.nextInt(6);
				if (random == 0) { return new Item[] {DEMON_SURGE, SHADOWSHOT, STONE}; }
				else if (random == 1) { return new Item[] {DEMON_SURGE, STONE}; }
				else if (random == 2) { return new Item[] {DEMON_SURGE, SHADOWSHOT}; }
				else { return new Item[] {DEMON_SURGE}; }
			} else if (classID == FE8Data.CharacterClass.THIEF.ID) {
				return new Item[] {LOCKPICK};
			} else if (classID == FE8Data.CharacterClass.MANAKETE_F.ID) {
				return new Item[] {DRAGONSTONE};
			}
			
			return null;
		}
		
		public static Item[] prfWeaponsForClassID(int classID) {
			if (classID == FE8Data.CharacterClass.EIRIKA_LORD.ID || classID == FE8Data.CharacterClass.EIRIKA_MASTER_LORD.ID) {
				return new Item[] {RAPIER};
			} else if (classID == FE8Data.CharacterClass.EPHRAIM_LORD.ID || classID == FE8Data.CharacterClass.EPHRAIM_MASTER_LORD.ID) {
				return new Item[] {REGINLEIF};
			}
			
			return null;
		}
		
		public static Item[] lockedWeaponsToClassID(int classID) {
			if (classID == FE8Data.CharacterClass.MYRMIDON.ID || classID == FE8Data.CharacterClass.MYRMIDON_F.ID ||
					classID == FE8Data.CharacterClass.SWORDMASTER.ID || classID == FE8Data.CharacterClass.SWORDMASTER_F.ID ||
					classID == FE8Data.CharacterClass.ASSASSIN.ID || classID == FE8Data.CharacterClass.ASSASSIN_F.ID ||
					classID == FE8Data.CharacterClass.EIRIKA_LORD.ID || classID == FE8Data.CharacterClass.EIRIKA_MASTER_LORD.ID) {
				return new Item[] {SHAMSHIR};
			}
			
			return null;
		}
		
		public static Item[] weaponsOfType(WeaponType type) {
			Set<Item> list = new HashSet<Item>();
			
			switch (type) {
			case SWORD:
				list.addAll(allSwords);
				break;
			case LANCE:
				list.addAll(allLances);
				break;
			case AXE:
				list.addAll(allAxes);
				break;
			case BOW:
				list.addAll(allBows);
				break;
			case ANIMA:
				list.addAll(allAnima);
				break;
			case LIGHT:
				list.addAll(allLight);
				break;
			case DARK:
				list.addAll(allDark);
				break;
			case STAFF:
				list.addAll(allStaves);
				break;
			default:
				break;
			}
			
			return list.toArray(new Item[list.size()]);
		}
		
		public static Item[] weaponsOfRank(WeaponRank rank) {
			Set<Item> list = new HashSet<Item>();
			
			switch (rank) {
			case E:
				list.addAll(allERank);
				break;
			case D:
				list.addAll(allDRank);
				break;
			case C:
				list.addAll(allCRank);
				break;
			case B:
				list.addAll(allBRank);
				break;
			case A:
				list.addAll(allARank);
				break;
			case S:
				list.addAll(allSRank);
				break;
			default:
				break;
			}
			
			return list.toArray(new Item[list.size()]);
		}
		
		public static Boolean isStatBooster(int itemID) {
			if (valueOf(itemID) == null) { return false; }
			return allStatBoosters.contains(valueOf(itemID));
		}
		
		public static Boolean isPromotionItem(int itemID) {
			if (valueOf(itemID) == null) { return false; }
			return allPromotionItems.contains(valueOf(itemID));
		}
		
		public static Boolean isBasicWeapon(int itemID) {
			if (valueOf(itemID) == null) { return false; }
			return allBasicWeapons.contains(valueOf(itemID));
		}
		
		public static Item upgradeMonsterWeapon(int itemID) {
			Item item = valueOf(itemID);
			if (item != null) {
				switch (item) {
				case POISON_CLAW:
					return ROTTEN_CLAW;
				case ROTTEN_CLAW:
					return LETHAL_TALON;
				case LETHAL_TALON:
					return FETID_CLAW;
				case FETID_CLAW:
					return SHARP_CLAW;
				case FIERY_FANG:
					return HELLFANG;
				case EVIL_EYE:
					return CRIMSON_EYE;
				default:
					return item;
				}
			} else {
				return null;
			}
		}
		
		public static Item equivalentMonsterWeapon(int baseItemID, int userClassID) {
			FE8Data.CharacterClass userClass = FE8Data.CharacterClass.valueOf(userClassID);
			Item baseItem = valueOf(baseItemID);
			if (userClass == null) { return baseItem; }
			if (baseItem == null) {
				switch (userClass) {
				case REVENANT: return POISON_CLAW;
				case ENTOMBED: return FETID_CLAW;
				case BAEL: return ROTTEN_CLAW;
				case ELDER_BAEL: return FETID_CLAW;
				case ELDER_BAEL_2: return FETID_CLAW;
				case MAUTHE_DOOG: return FIERY_FANG;
				case GWYLLGI: return HELLFANG;
				case MOGALL: return EVIL_EYE;
				case ARCH_MOGALL: return CRIMSON_EYE;
				case GORGON: return DEMON_SURGE;
				default: return null;
				}
			}
			
			if (!FE8Data.CharacterClass.allMonsterClasses.contains(userClass)) { return baseItem; }
			
			switch (userClass) {
			case REVENANT:
			case ENTOMBED:
			case BAEL:
			case ELDER_BAEL:
			case ELDER_BAEL_2:
				if (allERank.contains(baseItem)) { return POISON_CLAW; }
				else if (allDRank.contains(baseItem)) { return ROTTEN_CLAW; }
				else if (allCRank.contains(baseItem)) { return LETHAL_TALON; }
				else if (allBRank.contains(baseItem)) { return FETID_CLAW; }
				else { return SHARP_CLAW; }
			case MOGALL:
			case ARCH_MOGALL:
				if (allERank.contains(baseItem) || allDRank.contains(baseItem) || allCRank.contains(baseItem)) { return EVIL_EYE; }
				else { return CRIMSON_EYE; }
			case MAUTHE_DOOG:
			case GWYLLGI:
				if (allERank.contains(baseItem) || allDRank.contains(baseItem) || allCRank.contains(baseItem)) { return FIERY_FANG; }
				else { return HELLFANG; }
			case GORGON:
				return DEMON_SURGE;
			default:
				return baseItem;
			}
		}
		
		public static Item[] weaponsOfTypeAndRank(WeaponType type, WeaponRank min, WeaponRank max, Boolean requiresRange, Boolean requiresMelee) {
			if (min == WeaponRank.PRF || max == WeaponRank.PRF) {
				return null;
			}
			
			FE8WeaponRank minRank = FE8WeaponRank.E;
			if (min != null) {
				minRank = FE8WeaponRank.rankFromGeneralRank(min);
			}
			
			FE8WeaponRank maxRank = FE8WeaponRank.S;
			if (max != null) {
				maxRank = FE8WeaponRank.rankFromGeneralRank(max);
			}
			
			if (minRank.isHigherThanRank(maxRank)) {
				return null;
			}
			Set<Item> list = new HashSet<Item>();
			
			switch (type) {
			case SWORD:
				list.addAll(allSwords);
				break;
			case LANCE:
				list.addAll(allLances);
				break;
			case AXE:
				list.addAll(allAxes);
				break;
			case BOW:
				list.addAll(allBows);
				break;
			case ANIMA:
				list.addAll(allAnima);
				break;
			case LIGHT:
				list.addAll(allLight);
				break;
			case DARK:
				list.addAll(allDark);
				break;
			case STAFF:
				list.addAll(allStaves);
				break;
			default:
				break;
			}
			
			if (FE8WeaponRank.E.isLowerThanRank(minRank)) {
				list.removeAll(allERank);
			}
			if (FE8WeaponRank.D.isLowerThanRank(minRank)) {
				list.removeAll(allDRank);
			}
			if (FE8WeaponRank.C.isLowerThanRank(minRank)) {
				list.removeAll(allCRank);
			}
			if (FE8WeaponRank.B.isLowerThanRank(minRank)) {
				list.removeAll(allBRank);
			}
			if (FE8WeaponRank.A.isLowerThanRank(minRank)) {
				list.removeAll(allARank);
			}
			
			list.removeAll(allPrfRank);
			list.remove(SHAMSHIR); // This one is special. It must be added in only if we're certain the class asking for the item can use it.
			
			if (FE8WeaponRank.S.isHigherThanRank(maxRank)) {
				list.removeAll(allSRank);
			}
			if (FE8WeaponRank.A.isHigherThanRank(maxRank)) {
				list.removeAll(allARank);
			}
			if (FE8WeaponRank.B.isHigherThanRank(maxRank)) {
				list.removeAll(allBRank);
			}
			if (FE8WeaponRank.C.isHigherThanRank(maxRank)) {
				list.removeAll(allCRank);
			}
			if (FE8WeaponRank.D.isHigherThanRank(maxRank)) {
				list.removeAll(allDRank);
			}
			
			if (requiresRange) {
				list.retainAll(allRangedWeapons);
			}
			if (requiresMelee) {
				list.removeAll(allRangedOnlyWeapons);
			}
			
			return list.toArray(new Item[list.size()]);
		}
	}

	public enum ChapterPointer {
		PROLOGUE(0x07), CHAPTER_1(0x0A), CHAPTER_2(0x0D), CHAPTER_3(0x14), CHAPTER_4(0x17), CHAPTER_5(0x21), CHAPTER_5X(0x1E), CHAPTER_6(0x24),
		CHAPTER_7(0x27), CHAPTER_8(0x2A),
		
		CHAPTER_9_EIRIKA(0x2D), CHAPTER_10_EIRIKA(0x33), CHAPTER_11_EIRIKA(0xD1), CHAPTER_12_EIRIKA(0x37), CHAPTER_13_EIRIKA(0x3B), CHAPTER_14_EIRIKA(0x41),
		CHAPTER_15_EIRIKA(0x47), CHAPTER_16_EIRIKA(0x4B), CHAPTER_17_EIRIKA(0x4F), CHAPTER_18_EIRIKA(0x56), CHAPTER_19_EIRIKA(0x5A), CHAPTER_20_EIRIKA(0x5E),
		FINAL_1_EIRIKA(0x64), FINAL_2_EIRIKA(0x67),
		
		CHAPTER_9_EPHRAIM(0x6B), CHAPTER_10_EPHRAIM(0x71), CHAPTER_11_EPHRAIM(0xD5), CHAPTER_12_EPHRAIM(0x75), CHAPTER_13_EPHRAIM(0x78), CHAPTER_14_EPHRAIM(0x7F),
		CHAPTER_15_EPHRAIM(0x80), CHAPTER_16_EPHRAIM(0x81), CHAPTER_17_EPHRAIM(0x82), CHAPTER_18_EPHRAIM(0x83), CHAPTER_19_EPHRAIM(0x84), CHAPTER_20_EPHRAIM(0x85),
		FINAL_1_EPHRAIM(0x86), FINAL_2_EPHRAIM(0x87)
		
		// Include Tower/Ruins?
		;
		public int chapterID;
		
		private ChapterPointer(int chapterID) {
			this.chapterID = chapterID;
		}
		
		public CharacterClass[] blacklistedClasses() {
			switch(this) {
			case CHAPTER_2:
				return new CharacterClass[] {CharacterClass.BRIGAND};
			default:
				return new CharacterClass[] {};
			}
		}
		
		public Boolean shouldBeEasy() {
			switch(this) {
			default:
				return false;
			}
		}
		
		public Boolean shouldRemoveFightScenes() {
			switch (this) {
			default:
				return false;
			}
		}
		
		public Boolean isClassSafe() {
			switch (this) {
			case CHAPTER_3:
				return true;
			default:
				return false;
			}
		}
		
		public Character[] targetedRewardRecipientsToTrack() {
			switch (this) {
			case PROLOGUE: {
				return new Character[] {Character.EIRIKA};
			}
			default:
				break;
			}
			
			return new Character[] {};
		}
		
		// Because I'm too lazy to figure out who actually loads these units.
		public long[] additionalUnitOffsets() {
			switch (this) {
			case CHAPTER_1: {
				return new long[] {0x8B43D0L};
			}
			default:
				return new long[] {};
			}
		}
	}
	
	public enum PromotionItem {
		// Ocean Seal is missing, but I can't find it in this table.
		// It's pointer can be found at 0x29408. (Direct Read)
		// Remember that the actual address of the class IDs starts at byte 4 after the jump.
		// The class IDs are 00 terminated.
		HERO_CREST(0x01), KNIGHT_CREST(0x02), ORION_BOLT(0x03), ELYSIAN_WHIP(0x04), GUIDING_RING(0x05), MASTER_SEAL(0x25); // "Conquorer's Proof", Lunar Brace and Solar Brace are later, but we probably don't need to modify them.
		
		int offset;
		
		private PromotionItem(final int offset) {
			this.offset = offset;
		}
		
		public long getPointerAddress() {
			return (offset * 4) + PromotionItemTablePointer;
		}
	}
	
	public enum Palette {
		ARCHER_NEIMI(0x01, Character.NEIMI.ID, CharacterClass.ARCHER_F.ID, 0xEF9000),
		
		ASSASSIN_COLM(0x07, Character.COLM.ID, CharacterClass.ASSASSIN.ID, 0xEF9268),
		ASSASSIN_MARISA(0x06, Character.MARISA.ID, CharacterClass.ASSASSIN_F.ID, 0xEF9200),
		ASSASSIN_JOSHUA(0x08, Character.JOSHUA.ID, CharacterClass.ASSASSIN.ID, 0xEF92B8),
		
		BERSERKER_ROSS(0x14, Character.ROSS.ID, CharacterClass.BERSERKER.ID, 0xEF9708),
		BERSERKER_DOZLA(0x13, Character.DOZLA.ID, CharacterClass.BERSERKER.ID, 0xEF96B4),
		
		BISHOP_MOULDER(0x0D, Character.MOULDER.ID, CharacterClass.BISHOP.ID, 0xEF949C),
		BISHOP_NATASHA(0x0C, Character.NATASHA.ID, CharacterClass.BISHOP_F.ID, 0xEF9440),
		BISHOP_ARTUR(0x0B, Character.ARTUR.ID, CharacterClass.BISHOP.ID, 0xEF93E4),
		BISHOP_RIEV(0x0E, Character.RIEV.ID, CharacterClass.BISHOP.ID, 0xEF94F8),
		
		BRIGAND_BONE(0x09, Character.BONE.ID, CharacterClass.BRIGAND.ID, 0xEF931C),
		BRIGAND_BAZBA(0x0A, Character.BAZBA.ID, CharacterClass.BRIGAND.ID, 0xEF9380),
		
		DANCER_TETHYS(0x15, Character.TETHYS.ID, CharacterClass.DANCER.ID, 0xEF975C),
		
		DRUID_EWAN(0x1A, Character.EWAN.ID, CharacterClass.DRUID.ID, 0xEF991C),
		DRUID_KNOLL(0x19, Character.KNOLL.ID, CharacterClass.DRUID.ID, 0xEF98C8),
		
		FALCON_KNIGHT_VANESSA(0x1C, Character.VANESSA.ID, CharacterClass.FALCON_KNIGHT.ID, 0xEF99C4),
		FALCON_KNIGHT_SYRENE(0x1D, Character.SYRENE.ID, CharacterClass.FALCON_KNIGHT.ID, 0xEF9A18),
		FALCON_KNIGHT_TANA(0x1B, Character.TANA.ID, CharacterClass.FALCON_KNIGHT.ID, 0xEF9970),
		
		FIGHTER_ROSS(0x1F, Character.ROSS.ID, CharacterClass.FIGHTER.ID, 0xEF9AB8),
		FIGHTER_GARCIA(0x1E, Character.GARCIA.ID, CharacterClass.FIGHTER.ID, 0xEF9A68),
		FIGHTER_ONEILL(0x20, Character.ONEILL.ID, CharacterClass.FIGHTER.ID, 0xEF9B08),
		
		GENERAL_GILLIAM(0x25, Character.GILLIAM.ID, CharacterClass.GENERAL.ID, 0xEF9C9C),
		GENERAL_AMELIA(0x24, Character.AMELIA.ID, CharacterClass.GENERAL_F.ID, 0xEF9C48),
		GENERAL_FADO(0x5B, Character.FADO.ID, CharacterClass.GENERAL.ID, 0xEFAD80),
		GENERAL_TIRADO(0x27, Character.TIRADO.ID, CharacterClass.GENERAL.ID, 0xEF9D2C),
		GENERAL_VIGARDE(0x26, Character.VIGARDE.ID, CharacterClass.GENERAL.ID, 0xEF9CF0),
		
		GREAT_KNIGHT_GILLIAM(0x2A, Character.GILLIAM.ID, CharacterClass.GREAT_KNIGHT.ID, 0xEF9E28),
		GREAT_KNIGHT_FRANZ(0x29, Character.FRANZ.ID, CharacterClass.GREAT_KNIGHT.ID, 0xEF9DD4),
		GREAT_KNIGHT_FORDE(0x28, Character.FORDE.ID, CharacterClass.GREAT_KNIGHT.ID, 0xEF9D68),
		GREAT_KNIGHT_KYLE(0x2B, Character.KYLE.ID, CharacterClass.GREAT_KNIGHT.ID, 0xEF9E7C),
		GREAT_KNIGHT_AMELIA(0x2C, Character.AMELIA.ID, CharacterClass.GREAT_KNIGHT_F.ID, 0xEF9ED0),
		GREAT_KNIGHT_DUESSEL(0x2D, Character.DUESSEL.ID, CharacterClass.GREAT_KNIGHT.ID, 0xEF9F20),
		GREAT_KNIGHT_AIAS(0x2E, Character.AIAS.ID, CharacterClass.GREAT_KNIGHT.ID, 0xEF9F84),
		
		HERO_ROSS(0x42, Character.ROSS.ID, CharacterClass.HERO.ID, 0xEFA5AC),
		HERO_GARCIA(0x10, Character.GARCIA.ID, CharacterClass.HERO.ID, 0xEF95B0),
		HERO_GERIK(0x12, Character.GERIK.ID, CharacterClass.HERO.ID, 0xEF9664),
		HERO_CAELLACH(0x6C, Character.CAELLACH.ID, CharacterClass.HERO.ID, 0xEFB2A4),
		
		KNIGHT_GILLIAM(0x03, Character.GILLIAM.ID, CharacterClass.KNIGHT.ID, 0xEF90BC),
		KNIGHT_AMELIA(0x02, Character.AMELIA.ID, CharacterClass.KNIGHT_F.ID, 0xEF9054),
		KNIGHT_BREGUET(0x04, Character.BREGUET.ID, CharacterClass.KNIGHT.ID, 0xEF9128),
		KNIGHT_SAAR(0x05, Character.SAAR.ID, CharacterClass.KNIGHT.ID, 0xEF9194),
		
		MAGE_LUTE(0x2F, Character.LUTE.ID, CharacterClass.MAGE_F.ID, 0xEF9FC0),
		MAGE_EWAN(0x30, Character.EWAN.ID, CharacterClass.MAGE.ID, 0xEFA014),
		
		MAGE_KNIGHT_LUTE(0x35, Character.LUTE.ID, CharacterClass.MAGE_KNIGHT_F.ID, 0xEFA1A0),
		MAGE_KNIGHT_EWAN(0x33, Character.EWAN.ID, CharacterClass.MAGE_KNIGHT.ID, 0xEFA100),
		MAGE_KNIGHT_LARACHEL(0x34, Character.LARACHEL.ID, CharacterClass.MAGE_KNIGHT_F.ID, 0xEFA150),
		MAGE_KNIGHT_SELENA(0x36, Character.SELENA.ID, CharacterClass.MAGE_KNIGHT_F.ID, 0xEFA1F0),
		
		MERCENARY_GERIK(0x31, Character.GERIK.ID, CharacterClass.MERCENARY.ID, 0xEFA06C),
		MERCENARY_ZONTA(0x32, Character.ZONTA.ID, CharacterClass.MERCENARY.ID, 0xEFA0C4),
		
		MONK_ARTUR(0x37, Character.ARTUR.ID, CharacterClass.MONK.ID, 0xEFA22C),
		
		MYRMIDON_MARISA(0x39, Character.MARISA.ID, CharacterClass.MYRMIDON_F.ID, 0xEFA2DC),
		MYRMIDON_JOSHUA(0x38, Character.JOSHUA.ID, CharacterClass.MYRMIDON.ID, 0xEFA280),
		
		PALADIN_SETH(0x3C, Character.SETH.ID, CharacterClass.PALADIN.ID, 0xEFA3E8),
		PALADIN_FRANZ(0x3D, Character.FRANZ.ID, CharacterClass.PALADIN.ID, 0xEFA430),
		PALADIN_FORDE(0x3B, Character.FORDE.ID, CharacterClass.PALADIN.ID, 0xEFA398),
		PALADIN_KYLE(0x3E, Character.KYLE.ID, CharacterClass.PALADIN.ID, 0xEFA480),
		PALADIN_AMELIA(0x3A, Character.AMELIA.ID, CharacterClass.PALADIN_F.ID, 0xEFA338),
		PALADIN_ORSON(0x3F, Character.ORSON.ID, CharacterClass.PALADIN.ID, 0xEFA4D0),
		
		PEGASUS_KNIGHT_VANESSA(0x40, Character.VANESSA.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0xEFA50C),
		PEGASUS_KNIGHT_TANA(0x41, Character.TANA.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0xEFA55C),
		
		PIRATE_ROSS(0x11, Character.ROSS.ID, CharacterClass.PIRATE.ID, 0xEF9600),
		
		PRIEST_MOULDER(0x45, Character.MOULDER.ID, CharacterClass.PRIEST.ID, 0xEFA6B4),
		
		RANGER_NEIMI(0x22, Character.NEIMI.ID, CharacterClass.RANGER_F.ID, 0xEF9BB0),
		RANGER_GERIK(0x21, Character.GERIK.ID, CharacterClass.RANGER.ID, 0xEF9B58),
		RANGER_HAYDEN(0x5A, Character.HAYDEN.ID, CharacterClass.RANGER.ID, 0xEFAD44),
		RANGER_BERAN(0x23, Character.BERAN.ID, CharacterClass.RANGER.ID, 0xEF9C08),
		
		ROGUE_COLM(0x44, Character.COLM.ID, CharacterClass.ROGUE.ID, 0xEFA660),
		ROGUE_RENNAC(0x46, Character.RENNAC.ID, CharacterClass.ROGUE.ID, 0xEFA710),
		
		SAGE_MOULDER(0x4B, Character.MOULDER.ID, CharacterClass.SAGE.ID, 0xEFA8BC),
		SAGE_LUTE(0x47, Character.LUTE.ID, CharacterClass.SAGE_F.ID, 0xEFA770),
		SAGE_ARTUR(0x48, Character.ARTUR.ID, CharacterClass.SAGE.ID, 0xEFA7C0),
		SAGE_SALEH(0x4C, Character.SALEH.ID, CharacterClass.SAGE.ID, 0xEFA910),
		SAGE_EWAN(0x49, Character.EWAN.ID, CharacterClass.SAGE.ID, 0xEFA814),
		SAGE_PABLO(0x6B, Character.PABLO.ID, CharacterClass.SAGE.ID, 0xEFB268),
		
		SHAMAN_EWAN(0x4D, Character.EWAN.ID, CharacterClass.SHAMAN.ID, 0xEFA964),
		SHAMAN_KNOLL(0x4E, Character.KNOLL.ID, CharacterClass.SHAMAN.ID, 0xEFA9B8),
		SHAMAN_NOVALA(0x4F, Character.NOVALA.ID, CharacterClass.SHAMAN.ID, 0xEFAA0C),
		
		CLERIC_NATASHA(0x43, Character.NATASHA.ID, CharacterClass.CLERIC.ID, 0xEFA5FC),
		
		SNIPER_NEIMI(0x53, Character.NEIMI.ID, CharacterClass.SNIPER_F.ID, 0xEFAB34),
		SNIPER_INNES(0x52, Character.INNES.ID, CharacterClass.SNIPER.ID, 0xEFAAE0),
		
		CAVALIER_FRANZ(0x55, Character.FRANZ.ID, CharacterClass.CAVALIER.ID, 0xEFABDC),
		CAVALIER_FORDE(0x57, Character.FORDE.ID, CharacterClass.CAVALIER.ID, 0xEFAC7C),
		CAVALIER_KYLE(0x56, Character.KYLE.ID, CharacterClass.CAVALIER.ID, 0xEFAC2C),
		CAVALIER_AMELIA(0x54, Character.AMELIA.ID, CharacterClass.CAVALIER_F.ID, 0xEFAB8C),
		CAVALIER_MURRAY(0x58, Character.MURRAY.ID, CharacterClass.CAVALIER.ID, 0xEFACCC),
		
		SUMMONER_EWAN(0x50, Character.EWAN.ID, CharacterClass.SUMMONER.ID, 0xEFAA48),
		SUMMONER_KNOLL(0x51, Character.KNOLL.ID, CharacterClass.SUMMONER.ID, 0xEFAA98),
		
		SWORDMASTER_MARISA(0x5D, Character.MARISA.ID, CharacterClass.SWORDMASTER_F.ID, 0xEFAE10),
		SWORDMASTER_JOSHUA(0x5C, Character.JOSHUA.ID, CharacterClass.SWORDMASTER.ID, 0xEFADBC),
		SWORDMASTER_ISMAIRE(0x59, Character.ISMAIRE.ID, CharacterClass.SWORDMASTER_F.ID, 0xEFAD08),
		SWORDMASTER_CARLYLE(0x5F, Character.CARLYLE.ID, CharacterClass.SWORDMASTER.ID, 0xEFAEAC),
		
		THIEF_COLM(0x5E, Character.COLM.ID, CharacterClass.THIEF.ID, 0xEFAE64),
		
		TROUBADOUR_LARACHEL(0x62, Character.LARACHEL.ID, CharacterClass.TROUBADOUR.ID, 0xEFAFB0),
		
		VALKYRIE_NATASHA(0x61, Character.NATASHA.ID, CharacterClass.VALKYRIE.ID, 0xEFAF4C),
		VALKYRIE_LARACHEL(0x60, Character.LARACHEL.ID, CharacterClass.VALKYRIE.ID, 0xEFAEE8),
		
		WARRIOR_ROSS(0x64, Character.ROSS.ID, CharacterClass.WARRIOR.ID, 0xEFB054),
		WARRIOR_GARCIA(0x63, Character.GARCIA.ID, CharacterClass.WARRIOR.ID, 0xEFB004),
		WARRIOR_BINKS(0x65, Character.BINKS.ID, CharacterClass.WARRIOR.ID, 0xEFB0A4),
		WARRIOR_GHEB(0x6A, Character.GHEB.ID, CharacterClass.WARRIOR.ID, 0xEFB22C),
		
		WYVERN_KNIGHT_VANESSA(0x67, Character.VANESSA.ID, CharacterClass.WYVERN_KNIGHT_F.ID, 0xEFB13C),
		WYVERN_KNIGHT_CORMAG(0x68, Character.CORMAG.ID, CharacterClass.WYVERN_KNIGHT.ID, 0xEFB194),
		WYVERN_KNIGHT_TANA(0x66, Character.TANA.ID, CharacterClass.WYVERN_KNIGHT_F.ID, 0xEFB0E0),
		WYVERN_KNIGHT_VALTER(0x69, Character.VALTER.ID, CharacterClass.WYVERN_KNIGHT.ID, 0xEFB1F0),
		
		WYVERN_LORD_CORMAG(0x17, Character.CORMAG.ID, CharacterClass.WYVERN_LORD.ID, 0xEF9808),
		WYVERN_LORD_GLEN(0x18, Character.GLEN.ID, CharacterClass.WYVERN_LORD.ID, 0xEF9864),
		
		WYVERN_RIDER_CORMAG(0x16, Character.CORMAG.ID, CharacterClass.WYVERN_RIDER.ID, 0xEF97AC),
		
		// Unique palettes
		LORD_EIRIKA(0x00, Character.EIRIKA.ID, CharacterClass.EIRIKA_LORD.ID, 0xC0EB30),
		LORD_EPHRAIM(0x00, Character.EPHRAIM.ID, CharacterClass.EPHRAIM_LORD.ID, 0xC09B10),
		MASTER_LORD_EIRIKA(0x00, Character.EIRIKA.ID, CharacterClass.EIRIKA_MASTER_LORD.ID, 0xC2165C),
		MASTER_LORD_EPHRAIM(0x00, Character.EPHRAIM.ID, CharacterClass.EPHRAIM_MASTER_LORD.ID, 0xC18EF0),
		
		TRAINEE_ROSS(0x00, Character.ROSS.ID, CharacterClass.TRAINEE.ID, 0xDA9694),
		RECRUIT_AMELIA(0x00, Character.AMELIA.ID, CharacterClass.RECRUIT.ID, 0xDB2FB4),
		PUPIL_EWAN(0x00, Character.EWAN.ID, CharacterClass.PUPIL.ID, 0xDAC700),
		
		MANAKETE_MYRRH(0x00, Character.MYRRH.ID, CharacterClass.MANAKETE_F.ID, 0xE45ED4),
		
		SOLDIER_GENERIC(0x00, Character.NONE.ID, CharacterClass.SOLDIER.ID, 0xDB7968),
		
		// Monster palettes (should be read only)
		REVENANT(0x00, Character.NONE.ID, CharacterClass.REVENANT.ID, 0xDCB880),
		ENTOMBED(0x00, Character.NONE.ID, CharacterClass.ENTOMBED.ID, 0xDCC604),
		BONEWALKER(0x00, Character.NONE.ID, CharacterClass.BONEWALKER.ID, 0xDD1290), // Bonewalkers with Bows use the same palette
		WIGHT(0x00, Character.NONE.ID, CharacterClass.WIGHT.ID, 0xDDA1E0), // Wights with Bows use the same palette
		BAEL(0x00, Character.NONE.ID, CharacterClass.BAEL.ID, 0xDE42D8),
		ELDER_BAEL(0x00, Character.NONE.ID, CharacterClass.ELDER_BAEL.ID, 0xDE4F08), // ELDER_BAEL_2 is probably the same.
		CYCLOPS(0x00, Character.NONE.ID, CharacterClass.CYCLOPS.ID, 0xDF03E4),
		MAUTH_DOOG(0x00, Character.NONE.ID, CharacterClass.MAUTHE_DOOG.ID, 0xDF5048),
		GWYLLGI(0x00, Character.NONE.ID, CharacterClass.GWYLLGI.ID, 0xDFBAA8),
		TARVOS(0x00, Character.NONE.ID, CharacterClass.TARVOS.ID, 0xE01BD4),
		MAELDUIN(0x00, Character.NONE.ID, CharacterClass.MAELDUIN.ID, 0xE06440),
		MOGALL(0x00, Character.NONE.ID, CharacterClass.MOGALL.ID, 0xE110A4),
		ARCH_MOGALL(0x00, Character.NONE.ID, CharacterClass.ARCH_MOGALL.ID, 0xE11AF8),
		GORGON(0x00, Character.NONE.ID, CharacterClass.GORGON.ID, 0xE17CB0),
		GARGOYLE(0x00, Character.NONE.ID, CharacterClass.GARGOYLE.ID, 0xE249FC),
		DEATHGOYLE(0x00, Character.NONE.ID, CharacterClass.DEATHGOYLE.ID, 0xE24F50);
		
		int characterID;
		int classID;
		
		int paletteID;
		
		PaletteInfo info;
		
		static Map<Integer, Map<Integer, PaletteInfo>> classByCharacter = new HashMap<Integer, Map<Integer, PaletteInfo>>();
		static Map<Integer, Map<Integer, PaletteInfo>> charactersByClass = new HashMap<Integer, Map<Integer, PaletteInfo>>();
		static Map<Integer, PaletteInfo> defaultPaletteForClass = new HashMap<Integer, PaletteInfo>();
		static Map<Integer, Palette> palettesByID = new HashMap<Integer, Palette>();
		
		static {
			for (Palette palette : Palette.values()) {
				Map<Integer, PaletteInfo> map = classByCharacter.get(palette.characterID);
				if (map == null) {
					map = new HashMap<Integer, PaletteInfo>();
					classByCharacter.put(palette.characterID, map);
				}
				map.put(palette.classID, palette.info);
				
				map = charactersByClass.get(palette.classID);
				if (map == null) {
					map = new HashMap<Integer, PaletteInfo>();
					charactersByClass.put(palette.classID, map);
				}
				map.put(palette.characterID, palette.info);
				
				palettesByID.put(palette.paletteID, palette);
			}
			
			defaultPaletteForClass.put(CharacterClass.EIRIKA_LORD.ID, LORD_EIRIKA.info);
			defaultPaletteForClass.put(CharacterClass.EIRIKA_MASTER_LORD.ID, MASTER_LORD_EIRIKA.info);
			defaultPaletteForClass.put(CharacterClass.EPHRAIM_LORD.ID, LORD_EPHRAIM.info);
			defaultPaletteForClass.put(CharacterClass.EPHRAIM_MASTER_LORD.ID, MASTER_LORD_EPHRAIM.info);
			
			defaultPaletteForClass.put(CharacterClass.TRAINEE.ID, TRAINEE_ROSS.info);
			defaultPaletteForClass.put(CharacterClass.TRAINEE_2.ID, TRAINEE_ROSS.info);
			defaultPaletteForClass.put(CharacterClass.SUPER_TRAINEE.ID, TRAINEE_ROSS.info); // Trainees all have the same sprite, so they probably use the same palette.
			defaultPaletteForClass.put(CharacterClass.RECRUIT.ID, RECRUIT_AMELIA.info);
			defaultPaletteForClass.put(CharacterClass.RECRUIT_2.ID, RECRUIT_AMELIA.info);
			defaultPaletteForClass.put(CharacterClass.SUPER_RECRUIT.ID, RECRUIT_AMELIA.info);
			defaultPaletteForClass.put(CharacterClass.PUPIL.ID, PUPIL_EWAN.info);
			defaultPaletteForClass.put(CharacterClass.PUPIL_2.ID, PUPIL_EWAN.info);
			defaultPaletteForClass.put(CharacterClass.SUPER_PUPIL.ID, PUPIL_EWAN.info);
			
			defaultPaletteForClass.put(CharacterClass.MANAKETE_F.ID, MANAKETE_MYRRH.info);
			
			defaultPaletteForClass.put(CharacterClass.REVENANT.ID, REVENANT.info);
			defaultPaletteForClass.put(CharacterClass.ENTOMBED.ID, ENTOMBED.info);
			defaultPaletteForClass.put(CharacterClass.BONEWALKER.ID, BONEWALKER.info);
			defaultPaletteForClass.put(CharacterClass.BONEWALKER_BOW.ID, BONEWALKER.info);
			defaultPaletteForClass.put(CharacterClass.WIGHT.ID, WIGHT.info);
			defaultPaletteForClass.put(CharacterClass.WIGHT_BOW.ID, WIGHT.info);
			defaultPaletteForClass.put(CharacterClass.BAEL.ID, BAEL.info);
			defaultPaletteForClass.put(CharacterClass.ELDER_BAEL.ID, ELDER_BAEL.info);
			defaultPaletteForClass.put(CharacterClass.ELDER_BAEL_2.ID, ELDER_BAEL.info);
			defaultPaletteForClass.put(CharacterClass.CYCLOPS.ID, CYCLOPS.info);
			defaultPaletteForClass.put(CharacterClass.CYCLOPS_2.ID, CYCLOPS.info);
			defaultPaletteForClass.put(CharacterClass.MAUTHE_DOOG.ID, MAUTH_DOOG.info);
			defaultPaletteForClass.put(CharacterClass.GWYLLGI.ID, GWYLLGI.info);
			defaultPaletteForClass.put(CharacterClass.TARVOS.ID, TARVOS.info);
			defaultPaletteForClass.put(CharacterClass.MAELDUIN.ID, MAELDUIN.info);
			defaultPaletteForClass.put(CharacterClass.MOGALL.ID, MOGALL.info);
			defaultPaletteForClass.put(CharacterClass.ARCH_MOGALL.ID, ARCH_MOGALL.info);
			defaultPaletteForClass.put(CharacterClass.GORGON.ID, GORGON.info);
			defaultPaletteForClass.put(CharacterClass.GARGOYLE.ID, GARGOYLE.info);
			defaultPaletteForClass.put(CharacterClass.DEATHGOYLE.ID, DEATHGOYLE.info);
			
			defaultPaletteForClass.put(CharacterClass.MERCENARY.ID, MERCENARY_GERIK.info);
			defaultPaletteForClass.put(CharacterClass.HERO.ID, HERO_GERIK.info);
			
			defaultPaletteForClass.put(CharacterClass.MYRMIDON.ID, MYRMIDON_JOSHUA.info);
			defaultPaletteForClass.put(CharacterClass.MYRMIDON_F.ID, MYRMIDON_MARISA.info);
			defaultPaletteForClass.put(CharacterClass.SWORDMASTER.ID, SWORDMASTER_JOSHUA.info);
			defaultPaletteForClass.put(CharacterClass.SWORDMASTER_F.ID, SWORDMASTER_MARISA.info);
			defaultPaletteForClass.put(CharacterClass.ARCHER.ID, ARCHER_NEIMI.info); // Archer is the same palette format as Archer_F
			defaultPaletteForClass.put(CharacterClass.ARCHER_F.ID, ARCHER_NEIMI.info);
			defaultPaletteForClass.put(CharacterClass.SNIPER.ID, SNIPER_INNES.info);
			defaultPaletteForClass.put(CharacterClass.SNIPER_F.ID, SNIPER_NEIMI.info);
			defaultPaletteForClass.put(CharacterClass.MONK.ID, MONK_ARTUR.info);
			defaultPaletteForClass.put(CharacterClass.PRIEST.ID, PRIEST_MOULDER.info);
			defaultPaletteForClass.put(CharacterClass.CLERIC.ID, CLERIC_NATASHA.info);
			defaultPaletteForClass.put(CharacterClass.FIGHTER.ID, FIGHTER_GARCIA.info);
			defaultPaletteForClass.put(CharacterClass.BRIGAND.ID, BRIGAND_BAZBA.info);
			defaultPaletteForClass.put(CharacterClass.WARRIOR.ID, WARRIOR_GARCIA.info);
			defaultPaletteForClass.put(CharacterClass.BERSERKER.ID, BERSERKER_DOZLA.info);
			defaultPaletteForClass.put(CharacterClass.RANGER.ID, RANGER_GERIK.info);
			defaultPaletteForClass.put(CharacterClass.RANGER_F.ID, RANGER_NEIMI.info);
			defaultPaletteForClass.put(CharacterClass.CAVALIER.ID, CAVALIER_FRANZ.info);
			defaultPaletteForClass.put(CharacterClass.CAVALIER_F.ID, CAVALIER_AMELIA.info);
			defaultPaletteForClass.put(CharacterClass.PALADIN.ID, PALADIN_SETH.info);
			defaultPaletteForClass.put(CharacterClass.PALADIN_F.ID, PALADIN_AMELIA.info);
			defaultPaletteForClass.put(CharacterClass.KNIGHT.ID, KNIGHT_GILLIAM.info);
			defaultPaletteForClass.put(CharacterClass.KNIGHT_F.ID, KNIGHT_AMELIA.info);
			defaultPaletteForClass.put(CharacterClass.GENERAL.ID, GENERAL_GILLIAM.info);
			defaultPaletteForClass.put(CharacterClass.GENERAL_F.ID, GENERAL_AMELIA.info);
			defaultPaletteForClass.put(CharacterClass.GREAT_KNIGHT.ID, GREAT_KNIGHT_DUESSEL.info);
			defaultPaletteForClass.put(CharacterClass.GREAT_KNIGHT_F.ID, GREAT_KNIGHT_AMELIA.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_RIDER.ID, WYVERN_RIDER_CORMAG.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_LORD.ID, WYVERN_LORD_CORMAG.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_KNIGHT.ID, WYVERN_KNIGHT_CORMAG.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_KNIGHT_F.ID, WYVERN_KNIGHT_TANA.info);
			defaultPaletteForClass.put(CharacterClass.PEGASUS_KNIGHT.ID, PEGASUS_KNIGHT_VANESSA.info);
			defaultPaletteForClass.put(CharacterClass.FALCON_KNIGHT.ID, FALCON_KNIGHT_SYRENE.info);
			defaultPaletteForClass.put(CharacterClass.MAGE.ID, MAGE_EWAN.info);
			defaultPaletteForClass.put(CharacterClass.MAGE_F.ID, MAGE_LUTE.info);
			defaultPaletteForClass.put(CharacterClass.SAGE.ID, SAGE_SALEH.info);
			defaultPaletteForClass.put(CharacterClass.SAGE_F.ID, SAGE_LUTE.info);
			defaultPaletteForClass.put(CharacterClass.MAGE_KNIGHT.ID, MAGE_KNIGHT_EWAN.info);
			defaultPaletteForClass.put(CharacterClass.MAGE_KNIGHT_F.ID, MAGE_KNIGHT_LARACHEL.info);
			defaultPaletteForClass.put(CharacterClass.SHAMAN.ID, SHAMAN_KNOLL.info);
			defaultPaletteForClass.put(CharacterClass.DRUID.ID, DRUID_KNOLL.info);
			defaultPaletteForClass.put(CharacterClass.SUMMONER.ID, SUMMONER_KNOLL.info);
			defaultPaletteForClass.put(CharacterClass.BISHOP.ID, BISHOP_MOULDER.info);
			defaultPaletteForClass.put(CharacterClass.BISHOP_F.ID, BISHOP_NATASHA.info);
			defaultPaletteForClass.put(CharacterClass.TROUBADOUR.ID, TROUBADOUR_LARACHEL.info);
			defaultPaletteForClass.put(CharacterClass.VALKYRIE.ID, VALKYRIE_LARACHEL.info);
			defaultPaletteForClass.put(CharacterClass.THIEF.ID, THIEF_COLM.info);
			defaultPaletteForClass.put(CharacterClass.ASSASSIN.ID, ASSASSIN_COLM.info);
			defaultPaletteForClass.put(CharacterClass.ASSASSIN_F.ID, ASSASSIN_MARISA.info);
			defaultPaletteForClass.put(CharacterClass.ROGUE.ID, ROGUE_RENNAC.info);
			defaultPaletteForClass.put(CharacterClass.DANCER.ID, DANCER_TETHYS.info);
			defaultPaletteForClass.put(CharacterClass.PIRATE.ID, PIRATE_ROSS.info);
			
			defaultPaletteForClass.put(CharacterClass.SOLDIER.ID, SOLDIER_GENERIC.info);
			
		}
		
		private Palette(int paletteID, int charID, int classID, long offset) {
			this.paletteID = paletteID;
			this.characterID = charID;
			this.classID = classID;
			CharacterClass charClass = CharacterClass.valueOf(classID);
			if (charClass != null) {
				switch (charClass) {
				case EIRIKA_LORD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20, 23}, new int[] {25, 27, 29}, new int[] {32, 34, 36});
					break;
				case EIRIKA_MASTER_LORD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20, 23}, new int[] {25, 27, 29}, new int[] {32, 34, 36});
					break;
				case EPHRAIM_LORD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20, 23}, new int[] {25, 27}, new int[] {32, 34, 36});
					break;
				case EPHRAIM_MASTER_LORD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {23, 25}, new int[] {34, 36});
					break;
				case TRAINEE:
				case TRAINEE_2:
				case SUPER_TRAINEE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20, 23}, new int[] {25, 27, 29}, new int[] {});
					break;
				case RECRUIT:
				case RECRUIT_2:
				case SUPER_RECRUIT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {25, 27, 29}, new int[] {});
					break;
				case PUPIL:
				case PUPIL_2:
				case SUPER_PUPIL:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {32, 34, 36}, new int[] {25, 27, 29});
					break;
				case MANAKETE_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 25, 27}, new int[] {29, 32}, new int[] {34, 36});
					break;
				case REVENANT:
				case ENTOMBED:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {18, 20}, new int[] {32, 34, 36}, new int[] {}); // These guys actually have hair. The second color is the remnants of their pants.
					break;
				case BONEWALKER:
				case BONEWALKER_BOW:
				case WIGHT:
				case WIGHT_BOW:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {}, new int[] {23, 25, 27}, new int[] {9, 11, 14}); // No hair. Primary is their loincloth. Secondary is their breastplate.
					break;
				case BAEL:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {16, 14, 18, 11, 20}, new int[] {}, new int[] {}); // They have two hair colors, but in the case of the Bael, they're the same color, so we're keeping it that way.
					break;
				case ELDER_BAEL:
				case ELDER_BAEL_2:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {16, 18, 20}, new int[] {14, 11}, new int[] {}); // Unlike Baels, Elder Baels gain a different color streak through their "hair". That's reflected here.
					break;
				case CYCLOPS:
				case CYCLOPS_2:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {}, new int[] {18, 20, 9, 11, 14, 16}, new int[] {34, 36}); // No hair again, but lots of armor colors. Secondary is the color of their loincloth.
					break;
				case MAUTHE_DOOG:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {25, 27, 29}, new int[] {32, 34, 36}, new int[] {});
					break;
				case GWYLLGI:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {25, 27, 29}, new int[] {32, 34, 36}, new int[] {18, 20, 23}); // Gwyllgis get a tail color in addition to what they had as Mauth Doogs.
					break;
				case TARVOS:
				case MAELDUIN:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {18, 20}, new int[] {9, 11, 14, 16}, new int[] {}); // They have a body color, but since they share the same as Maelduins, we want to have some difference between the two.
					break;
				case MOGALL:
				case ARCH_MOGALL:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {32, 34, 36}, new int[] {14, 16, 18}, new int[] {}); // "Hair" is the tentacles. "Armor" is the membrane.
					break;
				case GORGON:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {16, 18, 20}, new int[] {32, 34, 36}, new int[] {9, 11, 14});
					break;
				case GARGOYLE:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {9, 11, 14}, new int[] {}, new int[] {}); // "Hair" is their horns and claws. Let's not touch anything else for now. They'll get their colors when promoted
					break;
				case DEATHGOYLE:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {9, 11, 14}, new int[] {29, 32, 34, 36}, new int[] {25, 27}); // Now they get a non-gray body.
					break;
				case MERCENARY:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20, 32}, new int[] {25, 11, 36}, new int[] {});
					break;
				case HERO:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34}, new int[] {});
					break;
				case MYRMIDON:
				case MYRMIDON_F:
				case SWORDMASTER:
				case SWORDMASTER_F:
				case ARCHER:
				case ARCHER_F:
				case SNIPER:
				case SNIPER_F:
				case MONK:
				case PRIEST:
				case CLERIC:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case FIGHTER:
				case BRIGAND:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {32, 34, 36}, new int[] {});
					break;
				case WARRIOR:
				case BERSERKER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {32, 34, 36}, new int[] {});
					break;
				case RANGER:
				case RANGER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {34, 16}, new int[] {23, 25, 27, 29});
					break;
				case CAVALIER:
				case CAVALIER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 11, 25, 16, 27, 29}, new int[] {});
					break;
				case PALADIN:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {23, 25, 27, 29}, new int[] {20}, new int[] {16, 11, 14}); // Secondary is shield (which is blended with white). Tertiary is mane + shield crest.
					break;
				case PALADIN_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 25, 27}, new int[] {16, 11, 14}); // Hair also affects shield. Secondary is mane + shield crest.
					break;
				case KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {18, 14, 16, 11, 9, 7}, new int[] {});
					break;
				case KNIGHT_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {18, 14, 9, 7}, new int[] {});
					break;
				case GENERAL:
				case GENERAL_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {29, 32, 34, 36}, new int[] {14, 16, 20});
					break;
				case GREAT_KNIGHT:
				case GREAT_KNIGHT_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {32, 34, 36}, new int[] {});
					break;
				case WYVERN_RIDER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {11, 9}, new int[] {27, 29, 32, 34}, new int[] {25});
					break;
				case WYVERN_LORD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {20, 23}, new int[] {27, 29, 32, 34}, new int[] {25});
					break;
				case WYVERN_KNIGHT:
				case WYVERN_KNIGHT_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {25, 23, 20}, new int[] {27, 29, 32, 34}, new int[] {});
					break;
				case PEGASUS_KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {20, 18}, new int[] {25, 27}, new int[] {});
					break;
				case FALCON_KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {20, 18}, new int[] {25, 27, 36}, new int[] {});
					break;
				case MAGE:
				case MAGE_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {32, 34, 36}, new int[] {23, 25, 27});
					break;
				case SAGE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34, 36}, new int[] {23, 25, 27});
					break;
				case SAGE_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {32, 34, 36}, new int[] {29, 23, 25, 27});
					break;
				case MAGE_KNIGHT:
				case MAGE_KNIGHT_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {27, 29}, new int[] {23, 25}, new int[] {32, 34, 36}); // Primary is cape, secondary is shirt, tertiary is horse armor.
					break;
				case SHAMAN:
				case DRUID:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 25, 27}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case SUMMONER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {20, 23}, new int[] {32, 34, 36}, new int[] {25, 27, 29});
					break;
				case BISHOP:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18}, new int[] {23, 34, 36}, new int[] {29, 32});
					break;
				case BISHOP_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case TROUBADOUR:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 25, 27, 29}, new int[] {});
					break;
				case VALKYRIE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 25, 27, 29, 36}, new int[] {});
					break;
				case THIEF:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {32, 29, 14}, new int[] {34, 36});
					break;
				case ASSASSIN:
				case ASSASSIN_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {16, 27}, new int[] {32, 34, 36});
					break;
				case ROGUE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 25}, new int[] {32, 34, 36}, new int[] {16, 18, 20});
					break;
				case DANCER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {32, 34, 36}, new int[] {23, 25, 27});
					break;
				case PIRATE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case SOLDIER:
					this.info = new PaletteInfo(classID, Character.NONE.ID, offset, new int[] {}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				default:
					break;
				}
			}
			
			this.info.setPaletteID(paletteID);
		}
		
		public static PaletteInfo paletteForCharacterInClass(int characterID, int classID) {
			int canonicalID = Character.canonicalIDForCharacterID(characterID);
			return classByCharacter.get(canonicalID).get(classID);
		}
		
		public static PaletteInfo[] palettesForCharacter(int characterID) {
			int canonicalID = Character.canonicalIDForCharacterID(characterID);
			Map<Integer, PaletteInfo> map = classByCharacter.get(canonicalID);
			if (map == null) { return new PaletteInfo[] {}; }
			List<PaletteInfo> list = new ArrayList<PaletteInfo>(map.values());
			return list.toArray(new PaletteInfo[list.size()]);
		}
		
		public static int paletteSizeForCharacter(int characterID) {
			FE8Data.Character character = FE8Data.Character.valueOf(characterID);
			switch (character) {
			case ONEILL:
			case BREGUET:
				// Only return this if we're sure.
				return BytesPerBossPalette;
			default:
				return BytesPerPalette;
			}
		}
		
		public static Map<Integer, Integer> customMappingForCharacter(int characterID) {
			FE8Data.Character character = FE8Data.Character.valueOf(characterID);
			if (FE8Data.Character.allPlayableCharacters.contains(character)) { return null; }
			switch (character) {
			case ONEILL: {
				Map<Integer, Integer> map = new HashMap<Integer, Integer>();
				map.put(32, 45);
				map.put(33, 46);
				map.put(34, 47);
				map.put(35, 48);
				map.put(36, 49);
				map.put(37, 50);
				return map;
			}
			case BREGUET: {
				Map<Integer, Integer> map = new HashMap<Integer, Integer>();
				map.put(5, 41);
				map.put(6, 42);
				map.put(7, 43);
				map.put(8, 44);
				map.put(9, 45);
				map.put(10, 46);
				map.put(11, 47);
				map.put(12, 48);
				
				map.put(14, 50);
				map.put(15, 51);
				map.put(16, 52);
				map.put(17, 53);
				map.put(18, 54);
				map.put(19, 55);
				map.put(20, 56);
				map.put(21, 57);
				
				map.put(23, 59);
				map.put(24, 60);
				map.put(25, 61);
				map.put(26, 62);
				
				map.put(32, 65);
				map.put(33, 66);
				
				return map;
			}
			default:
				return null;
			}
		}
		
		public static PaletteColor[] supplementaryHairColorForCharacter(int characterID) {
			Character character = Character.valueOf(characterID);
			switch (character) {
			case SETH:
				return new PaletteColor[] {new PaletteColor(189, 16, 8), new PaletteColor(132, 0, 0), new PaletteColor(99, 16, 24)};
			case GILLIAM:
				return new PaletteColor[] {new PaletteColor(132, 132, 107), new PaletteColor(99, 99, 74), new PaletteColor(74, 66, 66)};
			case CORMAG:
			case GLEN:
				return new PaletteColor[] {new PaletteColor(255, 255, 189), new PaletteColor(222, 222, 99), new PaletteColor(173, 173, 49), new PaletteColor(115, 115, 49)};
			case DOZLA:
				return new PaletteColor[] {new PaletteColor(90, 148, 74), new PaletteColor(49, 99, 41), new PaletteColor(57, 66, 41)};
			case DUESSEL:
				return new PaletteColor[] {new PaletteColor(222, 214, 214), new PaletteColor(198, 173, 181), new PaletteColor(156, 123, 156), new PaletteColor(107, 82, 107), new PaletteColor(74, 66, 74)};
			case BREGUET:
				return new PaletteColor[] {new PaletteColor(123, 132, 189), new PaletteColor(82, 90, 132), new PaletteColor(66, 57, 82)};
			case SAAR:
				return new PaletteColor[] {new PaletteColor(198, 181, 148), new PaletteColor(148, 132, 123), new PaletteColor(90, 66, 74)};
			case AIAS:
				return new PaletteColor[] {new PaletteColor(214, 206, 107), new PaletteColor(173, 165, 49), new PaletteColor(107, 99, 16)};
			case GHEB:
				return new PaletteColor[] {new PaletteColor(181, 115, 24), new PaletteColor(140, 82, 8), new PaletteColor(99, 57, 24)};
			case VALTER:
				return new PaletteColor[] {new PaletteColor(123, 173, 189), new PaletteColor(82, 115, 115), new PaletteColor(57, 74, 74)};
			case ORSON:
				return new PaletteColor[] {new PaletteColor(189, 123, 74), new PaletteColor(148, 99, 57), new PaletteColor(107, 66, 57)};
			default:
				return null;
			}
		}
		
		public static Palette paletteForID(int paletteID) {
			return palettesByID.get(paletteID);
		}
		
		public static PaletteInfo defaultPaletteForClass(int classID) {
			return defaultPaletteForClass.get(classID);
		}
	}
}
