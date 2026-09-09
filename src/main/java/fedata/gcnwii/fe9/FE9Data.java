package fedata.gcnwii.fe9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.WhyDoesJavaNotHaveThese;

public class FE9Data {
	
	public static final String FriendlyName = "Fire Emblem: Path of Radiance";
	public static final String GameCode = "GFEE01";

	public static final long CleanCRC32 = 0xF24CB38AL;
	public static final long CleanSize = 1459978240L;
	
	// GCN and above start to use different files in file systems, so these
	// offsets are going to be read from files.
	
	public static final String CharacterDataFilename = "system.cmp/FE8Data.bin";
	public static final String CharacterDataSectionName = "PersonData";
	public static final int CharacterDataSize = 0x54;
	
	public static final String ClassDataFilename = "system.cmp/FE8Data.bin";
	public static final String ClassDataSectionName = "JobData";
	public static final int ClassDataSize = 0x64;
	
	public static final String ItemDataFilename = "system.cmp/FE8Data.bin";
	public static final String ItemDataSectionName = "ItemData";
	public static final int ItemDataSize = 0x60;
	
	public static final String ItemDBXFilePath = "zdbx.cmp/xwp/";
	
	public static final String SkillDataFilename = "system.cmp/FE8Data.bin";
	// Skills are stored kind of weirdly, since they all seem to have their own dedicated "file" inside the data file.
	// Look for all of the files with the "SID_" prefix.
	public static final String SkillDataSectionPrefix = "SID_";
	
	public static final String CommonTextFilename = "system.cmp/mess/common.m";
	public static final int CommonTextEntrySize = 0x8;
	
	public static final int ChapterUnitEntrySize = 0x6C;
	
	public enum Character {
		
		NONE(null),
		
		BOYD_PROLOGUE("PID_BOLE_MAP1"), // Make sure prologue Boyd is worse in case we randomize a crappy Ike.
		
		IKE("PID_IKE"), TITANIA("PID_TIAMAT"), SOREN("PID_SENERIO"), MIST("PID_MIST"), OSCAR("PID_OSCAR"), BOYD("PID_BOLE"), ROLF("PID_LOFA"), 
		SHINON("PID_CHINON"), GATRIE("PID_GATRIE"), RHYS("PID_KILROY"), MIA("PID_WAYU"), ELINCIA("PID_ERINCIA_QUEEN"), GEOFFREY("PID_GEOFFRAY"), 
		BASTIAN("PID_ULYSSES"), KIERAN("PID_KEVIN"), LUCIA("PID_LUCHINO"), DEVDAN("PID_DALAHOWE"), CALILL("PID_CALILL"), LARGO("PID_LARGO"), 
		VOLKE("PID_VOKE"), NEPHENEE("PID_NEPENEE"), BROM("PID_CHAP"), ZIHARK("PID_ZIHARK"), ILYANA("PID_ELAICE"), SOTHE("PID_SOTHE"), HAAR("PID_HAAR"), 
		JILL("PID_JILL"), TAURONEO("PID_TAURONEO"), TANITH("PID_TANIS"), MARCIA("PID_MARCIA"), MAKALOV("PID_MAKAROV"), TORMOD("PID_TOPUCK"), 
		MUARIM("PID_MWARIM"), STEFAN("PID_SOANVALCKE"), ASTRID("PID_STELLA"), ENA("PID_ENA"), NASIR("PID_NASIR"), GIFFCA("PID_GIFFCA"), LETHE("PID_LETHE"), 
		MORDECAI("PID_MORDY"), RANULF("PID_LAY"), TIBARN("PID_TIBARN"), ULKI("PID_VULCI"), JANAFF("PID_JANAFF"), NAESALA("PID_NAESALA"), REYSON("PID_RIEUSION"),
		
		HAVETTI("PID_HIBUCCHI"), NEDATA("PID_NEDATA"), IKANAU("PID_IKANAU"), ZAWANA("PID_ZAWANAR"), MAIJIN("PID_MAIZIN"), KAMURA("PID_KAMURA"),
		DAKOVA("PID_DAKKOWA"), BALMER("PID_BARUMA"), KOTAFF("PID_KOTAHU"), NORRIS("PID_NOSHITHI"), DANOMILL("PID_DANOMIRU"), MACKOYA("PID_MAKKOYAR"),
		SEEKER("PID_CHIGU"), EMIL("PID_EMAKOU"), GASHILAMA("PID_GASIRAMA"), KIMAARSI("PID_KIMARSI"), SHIHARAM("PID_SIHARAMU"), HOMASA("PID_HOMAS"),
		KAYACHEY("PID_KAYACHE"), KASATAI("PID_KASATAI"), SCHAEFFER("PID_SIRKUKO"), RIKARD("PID_RIHITORU"), HAFEDD("PID_HAHUXEDO"), GROMELL("PID_GURORMERU"),
		HEDDWYN("PID_KOYUJO"), BRYCE("PID_BURAISU"), 
		
		OLIVER("PID_OLIVER"), PETRINE("PID_PRAGUE"), BERTRAM("PID_BEUFORRES");
		
		private String pid;
		
		private static Map<String, Character> map = new HashMap<String, Character>();
		
		static {
			for (Character character : Character.values()) {
				map.put(character.pid, character);
			}
		}
		
		private Character(String pid) {
			this.pid = pid;
		}
		
		public static Character withPID(String pid) {
			return map.get(pid);
		}
		
		public static Set<Character> allPlayableCharacters = new HashSet<Character>(Arrays.asList(IKE, TITANIA, SOREN, MIST, OSCAR, BOYD, ROLF,
				SHINON, GATRIE, RHYS, MIA, ELINCIA, GEOFFREY, BASTIAN, KIERAN, LUCIA, DEVDAN, CALILL, LARGO, VOLKE, NEPHENEE, BROM, ZIHARK, ILYANA,
				SOTHE, HAAR, JILL, TAURONEO, TANITH, MARCIA, MAKALOV, TORMOD, MUARIM, STEFAN, ASTRID, ENA, NASIR, GIFFCA, LETHE, MORDECAI, RANULF,
				TIBARN, ULKI, JANAFF, NAESALA, REYSON));
		
		public static Set<Character> allBossCharacters = new HashSet<Character>(Arrays.asList(HAVETTI, NEDATA, IKANAU, ZAWANA, MAIJIN, KAMURA, DAKOVA, BALMER,
				KOTAFF, NORRIS, DANOMILL, MACKOYA, SEEKER, EMIL, GASHILAMA, KIMAARSI, SHIHARAM, HOMASA, KAYACHEY, KASATAI, SCHAEFFER, RIKARD, HAFEDD, GROMELL,
				HEDDWYN, BRYCE, OLIVER, PETRINE, BERTRAM));
		
		public static Set<Character> allLords = new HashSet<Character>(Arrays.asList(IKE));
		public static Set<Character> allThieves = new HashSet<Character>(Arrays.asList(VOLKE, SOTHE));
		public static Set<Character> doNotChange = new HashSet<Character>(Arrays.asList(TIBARN, NAESALA, GIFFCA));
		
		public static Set<Character> buggedCharacters = new HashSet<Character>(Arrays.asList(
		));
		
		public static Set<Character> requiresRange = new HashSet<Character>(Arrays.asList());
		public static Set<Character> requiresMelee = new HashSet<Character>(Arrays.asList());
		
		public static Set<Character> requiredFliers = new HashSet<Character>(Arrays.asList(JILL, HAAR, SEEKER));
		public static Set<Character> requiredAttackers = new HashSet<Character>(Arrays.asList(IKE, BOYD));
		
		public static Set<Character> femaleSet = new HashSet<Character>(Arrays.asList(TITANIA, MIST, MIA, ELINCIA, LUCIA, CALILL, NEPHENEE, ILYANA, 
				JILL, TANITH, MARCIA, ASTRID, ENA, LETHE));
		
		public static Set<Character> requiresPromotion = new HashSet<Character>(Arrays.asList(IKE));
		
		public static Set<Character> allSpecial = new HashSet<Character>(Arrays.asList(REYSON));
		
		public boolean isPlayable() {
			return allPlayableCharacters.contains(this);
		}
		
		public boolean isModifiable() {
			return !doNotChange.contains(this);
		}
		
		public boolean isBugged() {
			return buggedCharacters.contains(this);
		}
		
		public boolean isBoss() {
			return allBossCharacters.contains(this);
		}
		
		public boolean isLord() {
			return allLords.contains(this);
		}
		
		public boolean isThief() {
			return allThieves.contains(this);
		}
		
		public boolean isFemale() {
			return femaleSet.contains(this);
		}
		
		public String getPID() {
			return this.pid;
		}
		
		public boolean isUnsafeForRandomization() {
			switch (this) {
			case NEDATA: return true;
			default: return false;
			}
		}
		
		public Chapter joinChapter() {
			switch (this) {
			case IKE: return Chapter.PROLOGUE;
			case BOYD: case OSCAR: case TITANIA: case ZAWANA: return Chapter.CHAPTER_1;
			case RHYS: case IKANAU: return Chapter.CHAPTER_2;
			case HAVETTI: return Chapter.CHAPTER_3;
			case SOREN: case MAIJIN: return Chapter.CHAPTER_4;
			case DAKOVA: return Chapter.CHAPTER_5;
			case EMIL: return Chapter.CHAPTER_6;
			case MIA: case BALMER: return Chapter.CHAPTER_7;
			case ILYANA: case KAMURA: return Chapter.CHAPTER_8;
			case MIST: case ROLF: case MARCIA: case LETHE: case MORDECAI: case NEDATA: case KOTAFF: return Chapter.CHAPTER_9;
			case VOLKE: case KIERAN: case BROM: case NEPHENEE: case DANOMILL: return Chapter.CHAPTER_10;
			case ZIHARK: case MACKOYA: return Chapter.CHAPTER_11;
			case JILL: case SEEKER: return Chapter.CHAPTER_12;
			case SOTHE: case ASTRID: case GATRIE: case NORRIS: return Chapter.CHAPTER_13;
			case MAKALOV: case GASHILAMA: return Chapter.CHAPTER_14;
			case MUARIM: case STEFAN: return Chapter.CHAPTER_15;
			case TORMOD: case DEVDAN: case KIMAARSI: return Chapter.CHAPTER_16;
			case JANAFF: case ULKI: case OLIVER: return Chapter.CHAPTER_17;
			case REYSON: case TANITH: case SHINON: case KAYACHEY: return Chapter.CHAPTER_18;
			case HOMASA: return Chapter.CHAPTER_19;
			case CALILL: case SHIHARAM: return Chapter.CHAPTER_20;
			case TAURONEO: case KASATAI: case ENA: return Chapter.CHAPTER_21;
			case SCHAEFFER: return Chapter.CHAPTER_22;
			case RANULF: case HAAR: case PETRINE: return Chapter.CHAPTER_23;
			case BASTIAN: case LUCIA: case GEOFFREY: case LARGO: return Chapter.CHAPTER_24;
			case GROMELL: return Chapter.CHAPTER_25;
			case ELINCIA: case BERTRAM: return Chapter.CHAPTER_26;
			case HAFEDD: return Chapter.CHAPTER_27;
			case NASIR: case HEDDWYN: return Chapter.CHAPTER_28;
			case GIFFCA: case NAESALA: case TIBARN: case BRYCE: return Chapter.ENDGAME;
			default: return null;
			}
		}
	}
	
	public enum CharacterClass {
		NONE(null),
		
		RANGER("JID_RANGER"), LORD("JID_HERO"), GREIL_HERO("JID_HERO_G"),
		
		MYRMIDON("JID_SWORDER"), MYRMIDON_F("JID_SWORDER/F"), SWORDMASTER("JID_SWORDMASTER"), SWORDMASTER_F("JID_SWORDMASTER/F"),
		
		SOLDIER("JID_SOLDIER"), SOLDIER_F("JID_SOLDIER/F"), HALBERDIER("JID_HALBERDIER"), HALBERDIER_F("JID_HALBERDIER/F"),
		
		FIGHTER("JID_FIGHTER"), WARRIOR("JID_WARRIOR"),
		
		ARCHER("JID_ARCHER"), SNIPER("JID_SNIPER"),
		
		KNIGHT("JID_ARMOR"), GENERAL("JID_GENERAL"),
		
		SWORD_KNIGHT("JID_SOCIALKNIGHT_S"), LANCE_KNIGHT("JID_SOCIALKNIGHT_L"), AXE_KNIGHT("JID_SOCIALKNIGHT_A"), BOW_KNIGHT("JID_SOCIALKNIGHT_B"),
		SWORD_KNIGHT_F("JID_SOCIALKNIGHT_S/F"), LANCE_KNIGHT_F("JID_SOCIALKNIGHT_L/F"), AXE_KNIGHT_F("JID_SOCIALKNIGHT_A/F"), BOW_KNIGHT_F("JID_SOCIALKNIGHT_B/F"),
		SWORD_PALADIN("JID_PALADIN_S"), LANCE_PALADIN("JID_PALADIN_L"), AXE_PALADIN("JID_PALADIN_A"), BOW_PALADIN("JID_PALADIN_B"),
		SWORD_PALADIN_F("JID_PALADIN_S/F"), LANCE_PALADIN_F("JID_PALADIN_L/F"), AXE_PALADIN_F("JID_PALADIN_A/F"), BOW_PALADIN_F("JID_PALADIN_B/F"),
		
		TITANIA_PALADIN("JID_TIAMAT/F"), // Interesting class...
		
		PEGASUS_KNIGHT("JID_PEGASUSKNIGHT/F"), FALCON_KNIGHT("JID_FALCONKNIGHT/F"),
		
		ELINCIA_FALCON_KNIGHT("JID_FALCONKNIGHT_E/F"), // Specifically for use with swords and staves, as opposed to the usual Swords and Lances.
		
		WYVERN_RIDER("JID_DRAGONKNIGHT"), WYVERN_RIDER_F("JID_DRAGONKNIGHT/F"), WYVERN_LORD("JID_DRAGONMASTER"), WYVERN_LORD_F("JID_DRAGONMASTER/F"),
		
		KING_DAEIN("JID_DRAGONMASTER_A"), // Ashnard is his own class, presumably so that he can use a sword.
		
		// These mages/sages only have magic ranks. No staves and no knives.
		FIRE_MAGE("JID_MAGE_F"), WIND_MAGE("JID_MAGE_W"), THUNDER_MAGE("JID_MAGE_T"), MAGE("JID_MAGE"),
		FIRE_MAGE_F("JID_MAGE_F/F"), WIND_MAGE_F("JID_MAGE_W/F"), THUNDER_MAGE_F("JID_MAGE_T/F"), MAGE_F("JID_MAGE/F"),
		FIRE_SAGE("JID_SAGE_F"), WIND_SAGE("JID_SAGE_W"), THUNDER_SAGE("JID_SAGE_T"), SAGE("JID_SAGE"),
		FIRE_SAGE_F("JID_SAGE_F/F"), WIND_SAGE_F("JID_SAGE_W/F"), THUNDER_SAGE_F("JID_SAGE_T/F"), SAGE_F("JID_SAGE/F"),
		
		// These sages have staff ability.
		FIRE_SAGE_STAFF("JID_SAGE_R_F"), WIND_SAGE_STAFF("JID_SAGE_R_W"), THUNDER_SAGE_STAFF("JID_SAGE_R_T"),
		
		// These sages have knife ability.
		FIRE_SAGE_KNIFE("JID_SAGE_S_F"), WIND_SAGE_KNIFE("JID_SAGE_S_W"), THUNDER_SAGE_KNIFE("JID_SAGE_S_T"),
		
		PRIEST("JID_PRIEST"), BISHOP("JID_BISHOP"), BISHOP_F("JID_BISHOP/F"), // No Priest_F?
		
		CLERIC("JID_CLERIC/F"), VALKYRIE("JID_VALKYRIE/F"),
		
		THIEF("JID_THIEF"), ASSASSIN("JID_ASSASSIN"), ASSASSIN_F("JID_ASSASSIN/F"), // No Thief_F?
		
		BANDIT("JID_BANDIT"), BERSERKER("JID_BERSERKER"),
		
		// These are untransformed.
		LION("JID_BEAST_L"), TIGER("JID_BEAST_T"), CAT("JID_BEAST_C"), CAT_F("JID_BEAST_C/F"),
		BLACK_DRAGON("JID_DRAGON_B"), WHITE_DRAGON("JID_DRAGON_W"), RED_DRAGON("JID_DRAGON_R"), RED_DRAGON_F("JID_DRAGON_R/F"),
		HAWK("JID_BIRD_HA"), CROW("JID_BIRD_C"), HERON("JID_BIRD_HE"), W_HERON("JID_BIRD_HE_W"), W_HERON_F("JID_BIRD_HE_W/F"),
		
		// These are transformed. (Feral Ones)
		FERAL_LION("JID_LION"), FERAL_TIGER("JID_TIGER"), FERAL_CAT("JID_CAT"), FERAL_CAT_F("JID_CAT/F"),
		FERAL_BLACK_DRAGON("JID_BLACKDRAGON"), FERAL_WHITE_DRAGON("JID_WHITEDRAGON"), FERAL_RED_DRAGON("JID_REDDRAGON"), FERAL_RED_DRAGON_F("JID_REDDRAGON/F"),
		FERAL_HAWK("JID_HAWK"), FERAL_CROW("JID_CROW"), FERAL_HERON("JID_HERON"), FERAL_W_HERON("JID_HERON_W"), FERAL_W_HERON_F("JID_HERON_W/F"),
		
		// More sage variants?
		SAGE_STAFF("JID_SAGE_R"), SAGE_KNIFE("JID_SAGE_S"),
		SAGE_STAFF_F("JID_SAGE_R/F"), SAGE_KNIFE_F("JID_SAGE_S/F"),
		
		TIBARN_HAWK("JID_BIRD_HA_TIB"), EVENT_TIBARN("JID_HAWK_TIB"),
		NAESALA_CROW("JID_BIRD_C_NES"), EVENT_NAESALA("JID_CROW_NES"),
		
		BLACK_KNIGHT("JID_BKNIGHT")
		
		;
		
		private String jid;
		
		private static Map<String, CharacterClass> map = new HashMap<String, CharacterClass>();
		
		static {
			for (CharacterClass charClass : CharacterClass.values()) {
				map.put(charClass.jid, charClass);
			}
		}
		
		private CharacterClass(String jid) {
			this.jid = jid;
		}
		
		public static CharacterClass withJID(String jid) {
			return map.get(jid);
		}
		
		public String getJID() {
			return jid;
		}
		
		public static Set<CharacterClass> allMaleClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER, GREIL_HERO, MYRMIDON, SWORDMASTER, 
				SOLDIER, HALBERDIER, FIGHTER, WARRIOR, ARCHER, SNIPER, KNIGHT, GENERAL, SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_RIDER, WYVERN_LORD, FIRE_MAGE, WIND_MAGE, THUNDER_MAGE, MAGE,
				FIRE_SAGE, WIND_SAGE, THUNDER_SAGE, SAGE, FIRE_SAGE_STAFF, WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, FIRE_SAGE_KNIFE, WIND_SAGE_KNIFE,
				THUNDER_SAGE_KNIFE, PRIEST, BISHOP, THIEF, ASSASSIN, BANDIT, BERSERKER, LION, TIGER, CAT, BLACK_DRAGON, WHITE_DRAGON, RED_DRAGON,
				HAWK, CROW, HERON, W_HERON, FERAL_LION, FERAL_TIGER, FERAL_CAT, FERAL_BLACK_DRAGON, FERAL_WHITE_DRAGON,
				FERAL_RED_DRAGON, FERAL_HAWK, FERAL_CROW, FERAL_HERON, FERAL_W_HERON, SAGE_STAFF, SAGE_KNIFE,
				TIBARN_HAWK, EVENT_TIBARN, NAESALA_CROW, EVENT_NAESALA, KING_DAEIN, BLACK_KNIGHT));
		
		public static Set<CharacterClass> allFemaleClasses = new HashSet<CharacterClass>(Arrays.asList(MYRMIDON_F, SWORDMASTER_F, SOLDIER_F, HALBERDIER_F, 
				SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F, BOW_KNIGHT_F, SWORD_PALADIN_F, LANCE_PALADIN_F, AXE_PALADIN_F, BOW_PALADIN_F, TITANIA_PALADIN,
				PEGASUS_KNIGHT, FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT, WYVERN_RIDER_F, WYVERN_LORD_F, FIRE_MAGE_F, WIND_MAGE_F, THUNDER_MAGE_F, MAGE_F,
				FIRE_SAGE_F, WIND_SAGE_F, THUNDER_SAGE_F, SAGE_F, BISHOP_F, CLERIC, VALKYRIE, ASSASSIN_F, CAT_F, RED_DRAGON_F, W_HERON_F, FERAL_CAT_F,
				FERAL_RED_DRAGON_F, FERAL_W_HERON_F, SAGE_STAFF_F, SAGE_KNIFE_F));
		
		public static Set<CharacterClass> allLordClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER));
		public static Set<CharacterClass> allThiefClasses = new HashSet<CharacterClass>(Arrays.asList(THIEF));
		public static Set<CharacterClass> allSpecialClasses = new HashSet<CharacterClass>(Arrays.asList(HERON, W_HERON, W_HERON_F));
		
		public static Set<CharacterClass> allUnpromotedClasses = new HashSet<CharacterClass>(Arrays.asList(RANGER, MYRMIDON, SOLDIER, FIGHTER, ARCHER, KNIGHT, 
				SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT, WYVERN_RIDER, FIRE_MAGE, WIND_MAGE, THUNDER_MAGE, MAGE, PRIEST, THIEF, BANDIT, MYRMIDON_F,
				SOLDIER_F, SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F, BOW_KNIGHT_F, PEGASUS_KNIGHT, WYVERN_RIDER_F, FIRE_MAGE_F, WIND_MAGE_F, THUNDER_MAGE_F,
				MAGE_F, CLERIC));
		public static Set<CharacterClass> allPromotedClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, GREIL_HERO, SWORDMASTER, HALBERDIER, WARRIOR,
				SNIPER, GENERAL, SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_LORD, FIRE_SAGE, WIND_SAGE, THUNDER_SAGE, SAGE, FIRE_SAGE_STAFF,
				WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, FIRE_SAGE_KNIFE, THUNDER_SAGE_KNIFE, WIND_SAGE_KNIFE, BISHOP, ASSASSIN, BERSERKER, TIGER, CAT, WHITE_DRAGON,
				RED_DRAGON, HAWK, CROW, HERON, W_HERON, SAGE_STAFF, SAGE_KNIFE, SWORDMASTER_F, HALBERDIER_F, SWORD_PALADIN_F, LANCE_PALADIN_F, AXE_PALADIN_F, BOW_PALADIN_F,
				TITANIA_PALADIN, FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT, WYVERN_LORD_F, FIRE_SAGE_F, WIND_SAGE_F, THUNDER_SAGE_F, SAGE_F, BISHOP_F, VALKYRIE, ASSASSIN_F,
				CAT_F, RED_DRAGON_F, W_HERON_F, SAGE_STAFF_F, SAGE_KNIFE_F));
		
		public static Set<CharacterClass> allBeorcClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER, GREIL_HERO, MYRMIDON, SWORDMASTER, 
				SOLDIER, HALBERDIER, FIGHTER, WARRIOR, ARCHER, SNIPER, KNIGHT, GENERAL, SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_RIDER, WYVERN_LORD, FIRE_MAGE, WIND_MAGE, THUNDER_MAGE, MAGE,
				FIRE_SAGE, WIND_SAGE, THUNDER_SAGE, SAGE, FIRE_SAGE_STAFF, WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, FIRE_SAGE_KNIFE, WIND_SAGE_KNIFE,
				THUNDER_SAGE_KNIFE, PRIEST, BISHOP, THIEF, ASSASSIN, BANDIT, BERSERKER, KING_DAEIN, BLACK_KNIGHT, MYRMIDON_F, SWORDMASTER_F, SOLDIER_F, 
				HALBERDIER_F, SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F, BOW_KNIGHT_F, SWORD_PALADIN_F, LANCE_PALADIN_F, AXE_PALADIN_F, BOW_PALADIN_F, 
				TITANIA_PALADIN, PEGASUS_KNIGHT, FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT, WYVERN_RIDER_F, WYVERN_LORD_F, FIRE_MAGE_F, WIND_MAGE_F, THUNDER_MAGE_F, 
				MAGE_F, FIRE_SAGE_F, WIND_SAGE_F, THUNDER_SAGE_F, SAGE_F, BISHOP_F, CLERIC, VALKYRIE, ASSASSIN_F, SAGE_STAFF, SAGE_KNIFE, SAGE_STAFF_F, 
				SAGE_KNIFE_F));
		public static Set<CharacterClass> allHorseClasses = new HashSet<CharacterClass>(Arrays.asList(SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F, BOW_KNIGHT_F, SWORD_PALADIN_F, LANCE_PALADIN_F, AXE_PALADIN_F, BOW_PALADIN_F, 
				TITANIA_PALADIN));
		
		public static Set<CharacterClass> allLaguzClasses = new HashSet<CharacterClass>(Arrays.asList(LION, TIGER, CAT, BLACK_DRAGON, WHITE_DRAGON, RED_DRAGON,
				HAWK, CROW, HERON, W_HERON, FERAL_LION, FERAL_TIGER, FERAL_CAT, FERAL_BLACK_DRAGON, FERAL_WHITE_DRAGON,
				FERAL_RED_DRAGON, FERAL_HAWK, FERAL_CROW, FERAL_HERON, FERAL_W_HERON, TIBARN_HAWK, EVENT_TIBARN, 
				NAESALA_CROW, EVENT_NAESALA, CAT_F, RED_DRAGON_F, W_HERON_F, FERAL_CAT_F, FERAL_RED_DRAGON_F, FERAL_W_HERON_F));
		
		public static Set<CharacterClass> allPacifistClasses = new HashSet<CharacterClass>(Arrays.asList(HERON, W_HERON, W_HERON_F, PRIEST, CLERIC, 
				FERAL_HERON, FERAL_W_HERON, FERAL_W_HERON_F));
		
		public static Set<CharacterClass> allFlyingClasses = new HashSet<CharacterClass>(Arrays.asList(PEGASUS_KNIGHT, FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT,
				WYVERN_RIDER, WYVERN_LORD, WYVERN_RIDER_F, WYVERN_LORD_F, KING_DAEIN, HERON, W_HERON, W_HERON_F, FERAL_HERON, FERAL_W_HERON, 
				FERAL_W_HERON_F, HAWK, CROW, FERAL_CROW, FERAL_HAWK, TIBARN_HAWK, EVENT_TIBARN, NAESALA_CROW, EVENT_NAESALA));
		
		public static Set<CharacterClass> allValidClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER, MYRMIDON, SWORDMASTER, 
				SOLDIER, HALBERDIER, FIGHTER, WARRIOR, ARCHER, SNIPER, KNIGHT, GENERAL, SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_RIDER, WYVERN_LORD, /*FIRE_MAGE, WIND_MAGE, THUNDER_MAGE,*/ MAGE,
				/*FIRE_SAGE, WIND_SAGE, THUNDER_SAGE,*/ SAGE, /*FIRE_SAGE_STAFF, WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, FIRE_SAGE_KNIFE, WIND_SAGE_KNIFE,
				THUNDER_SAGE_KNIFE,*/ PRIEST, BISHOP, THIEF, ASSASSIN, BANDIT, BERSERKER, TIGER, CAT, WHITE_DRAGON, RED_DRAGON,
				HAWK, CROW, /*HERON,*/ W_HERON, FERAL_TIGER, FERAL_CAT, /*FERAL_WHITE_DRAGON*,*/
				FERAL_RED_DRAGON, FERAL_HAWK, FERAL_CROW, SAGE_STAFF, SAGE_KNIFE,
				MYRMIDON_F, SWORDMASTER_F, SOLDIER_F, HALBERDIER_F, 
				/*SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F, */BOW_KNIGHT_F, SWORD_PALADIN_F, LANCE_PALADIN_F, AXE_PALADIN_F, BOW_PALADIN_F, TITANIA_PALADIN,
				PEGASUS_KNIGHT, FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT, WYVERN_RIDER_F, WYVERN_LORD_F, /*FIRE_MAGE_F, WIND_MAGE_F, THUNDER_MAGE_F,*/ MAGE_F,
				/*FIRE_SAGE_F, WIND_SAGE_F, THUNDER_SAGE_F, SAGE_F, BISHOP_F,*/ CLERIC, VALKYRIE, /*ASSASSIN_F,*/ CAT_F, RED_DRAGON_F, FERAL_CAT_F,
				FERAL_RED_DRAGON_F, SAGE_STAFF_F, SAGE_KNIFE_F));
		
		public static Set<CharacterClass> enemyOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(/*FIRE_MAGE, WIND_MAGE, THUNDER_MAGE, 
				FIRE_SAGE, WIND_SAGE, THUNDER_SAGE, FIRE_SAGE_STAFF, WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, FIRE_SAGE_KNIFE, WIND_SAGE_KNIFE,
				THUNDER_SAGE_KNIFE, FIRE_MAGE_F, WIND_MAGE_F, THUNDER_MAGE_F, FIRE_SAGE_F, WIND_SAGE_F, THUNDER_SAGE_F,*/SAGE, 
				FERAL_LION, FERAL_TIGER, FERAL_CAT, FERAL_WHITE_DRAGON, FERAL_RED_DRAGON, FERAL_HAWK, FERAL_CROW, FERAL_CAT_F, FERAL_RED_DRAGON_F));
		public static Set<CharacterClass> playerEligibleClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER, MYRMIDON, SWORDMASTER, 
				SOLDIER, HALBERDIER, FIGHTER, WARRIOR, ARCHER, SNIPER, KNIGHT, GENERAL, SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_RIDER, WYVERN_LORD, /*FIRE_MAGE, WIND_MAGE, THUNDER_MAGE,*/ MAGE,
				PRIEST, BISHOP, THIEF, ASSASSIN, BANDIT, BERSERKER, TIGER, CAT, WHITE_DRAGON, RED_DRAGON,
				HAWK, CROW, /*HERON,*/ W_HERON, 
				MYRMIDON_F, SWORDMASTER_F, SOLDIER_F, HALBERDIER_F, 
				/*SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F,*/ BOW_KNIGHT_F, SWORD_PALADIN_F, LANCE_PALADIN_F, AXE_PALADIN_F, BOW_PALADIN_F, TITANIA_PALADIN,
				PEGASUS_KNIGHT, FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT, WYVERN_RIDER_F, WYVERN_LORD_F, MAGE_F,
				/*BISHOP_F,*/ CLERIC, VALKYRIE, CAT_F, RED_DRAGON_F,
				SAGE_STAFF, SAGE_KNIFE,
				SAGE_STAFF_F, SAGE_KNIFE_F));
		public static Set<CharacterClass> playerOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(RANGER, LORD, TIGER, CAT, WHITE_DRAGON, RED_DRAGON,
				HAWK, CROW, /*HERON,*/ W_HERON));
		
		public static Set<CharacterClass> physicalClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER, GREIL_HERO, MYRMIDON, SWORDMASTER, 
				SOLDIER, HALBERDIER, FIGHTER, WARRIOR, ARCHER, SNIPER, KNIGHT, GENERAL, SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_RIDER, WYVERN_LORD, THIEF, ASSASSIN, BANDIT, BERSERKER, KING_DAEIN, BLACK_KNIGHT, 
				MYRMIDON_F, SWORDMASTER_F, SOLDIER_F, HALBERDIER_F, SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F, BOW_KNIGHT_F, SWORD_PALADIN_F, LANCE_PALADIN_F, 
				AXE_PALADIN_F, BOW_PALADIN_F, TITANIA_PALADIN, PEGASUS_KNIGHT, FALCON_KNIGHT, WYVERN_RIDER_F, WYVERN_LORD_F, ASSASSIN_F,
				LION, TIGER, CAT, CAT_F, WHITE_DRAGON, RED_DRAGON, RED_DRAGON_F, HAWK, CROW, FERAL_LION, FERAL_TIGER, FERAL_CAT, FERAL_WHITE_DRAGON, FERAL_RED_DRAGON, 
				FERAL_HAWK, FERAL_CROW, FERAL_CAT_F, FERAL_RED_DRAGON_F));
		public static Set<CharacterClass> magicalClasses = new HashSet<CharacterClass>(Arrays.asList(FIRE_MAGE, WIND_MAGE, THUNDER_MAGE, MAGE,
				FIRE_SAGE, WIND_SAGE, THUNDER_SAGE, SAGE, FIRE_SAGE_STAFF, WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, PRIEST, BISHOP, FIRE_MAGE_F, WIND_MAGE_F, 
				THUNDER_MAGE_F, MAGE_F, FIRE_SAGE_F, WIND_SAGE_F, THUNDER_SAGE_F, SAGE_F, BISHOP_F, SAGE_STAFF, SAGE_STAFF_F, HERON, W_HERON, W_HERON_F));
		public static Set<CharacterClass> hybridMagicalClasses = new HashSet<CharacterClass>(Arrays.asList(FIRE_SAGE_KNIFE, WIND_SAGE_KNIFE,
				THUNDER_SAGE_KNIFE, SAGE_KNIFE, SAGE_KNIFE_F));
		public static Set<CharacterClass> hybridPhysicalClasses = new HashSet<CharacterClass>(Arrays.asList(ELINCIA_FALCON_KNIGHT, CLERIC, VALKYRIE));
		
		public static Set<CharacterClass> advancedClasses = new HashSet<CharacterClass>(Arrays.asList(WYVERN_RIDER, WYVERN_RIDER_F,
				PEGASUS_KNIGHT));
		
		public boolean isLordClass() { return allLordClasses.contains(this); }
		public boolean isThiefClass() { return allThiefClasses.contains(this); }
		public boolean isSpecialClass() { return allSpecialClasses.contains(this); }
		
		public boolean isPhysicalClass() { return physicalClasses.contains(this); }
		public boolean isMagicalClass() { return magicalClasses.contains(this); }
		public boolean isHybridMagicalClass() { return hybridMagicalClasses.contains(this); }
		public boolean isHybridPhyiscalClass() { return hybridPhysicalClasses.contains(this); }
		
		public boolean isValidClass() { return allValidClasses.contains(this); }
		public boolean isValidPlayerClass() { return playerEligibleClasses.contains(this); }
		public boolean isEnemyOnly() { return enemyOnlyClasses.contains(this); }
		public boolean isPlayerOnly() { return playerOnlyClasses.contains(this); }
		
		public boolean isLaguz() { return allLaguzClasses.contains(this); }
		public boolean isBeorc() { return allBeorcClasses.contains(this); }
		
		public boolean isPromotedClass() { return allPromotedClasses.contains(this); }
		
		public boolean isFemale() { return allFemaleClasses.contains(this); }
		public boolean isMounted() {return allHorseClasses.contains(this); }//Valkyrie excepted; his is more a "isCavalier" bool
		public boolean isFlier() { return allFlyingClasses.contains(this); }
		
		public boolean isPacifist() { return allPacifistClasses.contains(this); }
		public boolean isAdvanced() { return advancedClasses.contains(this); }
		
		public Set<CharacterClass> similarClasses() {
			switch (this) {
			case SWORD_KNIGHT:
			case LANCE_KNIGHT:
			case AXE_KNIGHT:
			case BOW_KNIGHT:
			case SWORD_KNIGHT_F:
			case LANCE_KNIGHT_F:
			case AXE_KNIGHT_F:
			case BOW_KNIGHT_F:
				return new HashSet<CharacterClass>(Arrays.asList(SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT, SWORD_KNIGHT_F, AXE_KNIGHT_F, LANCE_KNIGHT_F, BOW_KNIGHT_F));
			case SWORD_PALADIN:
			case LANCE_PALADIN:
			case AXE_PALADIN:
			case BOW_PALADIN:
			case SWORD_PALADIN_F:
			case LANCE_PALADIN_F:
			case AXE_PALADIN_F:
			case BOW_PALADIN_F:
			case TITANIA_PALADIN:
				return new HashSet<CharacterClass>(Arrays.asList(SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, SWORD_PALADIN_F, LANCE_PALADIN_F, AXE_PALADIN_F, BOW_PALADIN_F, TITANIA_PALADIN));
			case MAGE:
			case MAGE_F:
				return new HashSet<CharacterClass>(Arrays.asList(MAGE, MAGE_F));
			case SAGE:
			case SAGE_F:
			case SAGE_KNIFE:
			case SAGE_KNIFE_F:
			case SAGE_STAFF:
			case SAGE_STAFF_F:
				return new HashSet<CharacterClass>(Arrays.asList(SAGE, SAGE_F, SAGE_KNIFE, SAGE_KNIFE_F, SAGE_STAFF, SAGE_STAFF_F));
			case SOLDIER:
			case SOLDIER_F:
				return new HashSet<CharacterClass>(Arrays.asList(SOLDIER, SOLDIER_F));
			case HALBERDIER:
			case HALBERDIER_F:
				return new HashSet<CharacterClass>(Arrays.asList(HALBERDIER, HALBERDIER_F));
			case MYRMIDON:
			case MYRMIDON_F:
				return new HashSet<CharacterClass>(Arrays.asList(MYRMIDON, MYRMIDON_F));
			case SWORDMASTER:
			case SWORDMASTER_F:
				return new HashSet<CharacterClass>(Arrays.asList(SWORDMASTER, SWORDMASTER_F));
			case FALCON_KNIGHT:
			case ELINCIA_FALCON_KNIGHT:
				return new HashSet<CharacterClass>(Arrays.asList(FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT));
			case CAT:
			case CAT_F:
				return new HashSet<CharacterClass>(Arrays.asList(CAT, CAT_F));
			default:
				return new HashSet<CharacterClass>();
			}
		}
		
		public CharacterClass getTransformedClass() {
			switch (this) {
			case CAT: return FERAL_CAT;
			case CAT_F: return FERAL_CAT_F;
			case TIGER: return FERAL_TIGER;
			case CROW: return FERAL_CROW;
			case HAWK: return FERAL_HAWK;
			case RED_DRAGON: return FERAL_RED_DRAGON;
			case RED_DRAGON_F: return FERAL_RED_DRAGON_F;
			case WHITE_DRAGON: return FERAL_WHITE_DRAGON;
			case W_HERON: return FERAL_W_HERON;
			default: return null;
			}
		}
		
		public CharacterClass getPromoted() {
			switch(this) {
			case RANGER: return LORD;
			case MYRMIDON: return SWORDMASTER; 
			case SOLDIER: return HALBERDIER;
			case FIGHTER: return WARRIOR; 
			case ARCHER: return SNIPER;
			case KNIGHT: return GENERAL; 
			case SWORD_KNIGHT: return SWORD_PALADIN; 
			case LANCE_KNIGHT: return LANCE_PALADIN; 
			case AXE_KNIGHT: return AXE_PALADIN;
			case BOW_KNIGHT: return BOW_PALADIN;
			case WYVERN_RIDER: return WYVERN_LORD;
			case MAGE: return SAGE_STAFF;
			case PRIEST: return BISHOP;
			case THIEF: return ASSASSIN;
			case BANDIT: return BERSERKER;
			
			case MYRMIDON_F: return SWORDMASTER_F;
			case SOLDIER_F: return HALBERDIER_F; 
			case SWORD_KNIGHT_F: return SWORD_PALADIN_F;
			case LANCE_KNIGHT_F: return LANCE_PALADIN_F;
			case AXE_KNIGHT_F: return AXE_PALADIN_F;
			case BOW_KNIGHT_F: return BOW_PALADIN_F;
			case PEGASUS_KNIGHT: return FALCON_KNIGHT;
			case WYVERN_RIDER_F: return WYVERN_LORD_F;
			case MAGE_F: return SAGE_STAFF_F;
			case CLERIC: return VALKYRIE;
			default: return null;
			}
		}
		
		public String getAidString() {
			switch(this) {
				case ARCHER: return "AID_ARCHER";
				case KNIGHT: return "AID_ARMOR";
				case BANDIT: return "AID_BARBARIAN";
				case FIGHTER: return "AID_FIGHTER";
				case SWORD_KNIGHT_F: case AXE_KNIGHT_F: case LANCE_KNIGHT_F: case SWORD_KNIGHT: case AXE_KNIGHT: case LANCE_KNIGHT: 
					case BOW_KNIGHT: return "AID_KNIGHT";
				case BOW_KNIGHT_F: return "AID_KNIGHT_ST"; // Only female bow knight works with Astrid's animation, apparently.
				case RANGER: return "AID_LORD";
				case MAGE: case FIRE_MAGE: return "AID_MAGE";
				case MAGE_F: return "AID_MAGEF";
				case PEGASUS_KNIGHT: return "AID_PEGASU";
				case SOLDIER: return "AID_SOLDIER";
				case SOLDIER_F: return "AID_SOLDIERF";
				case MYRMIDON: return "AID_SWORDER";
				case MYRMIDON_F: return "AID_SWORDERF";
				case THIEF: return "AID_THIEF";
				case WYVERN_RIDER: case WYVERN_RIDER_F: return "AID_DRAGON";
				case PRIEST: return "AID_BISHOP";
				case CLERIC: return "AID_CLERIC";
				
				case BISHOP: return "AID_BISHOP2";
				case BISHOP_F: return "AID_BISHOP2F";
				case GENERAL: return "AID_GENERAL";
				case SWORD_PALADIN: case AXE_PALADIN: case LANCE_PALADIN: case BOW_PALADIN: return "AID_KNIGHT2";
				case SWORD_PALADIN_F: case AXE_PALADIN_F: case LANCE_PALADIN_F: case BOW_PALADIN_F: return "AID_KNIGHT2_ST";
				case TITANIA_PALADIN: return "AID_KNIGHT2_TIA";
				case LORD: return "AID_LORD2";
				case SAGE_STAFF: case SAGE_KNIFE: return "AID_MAGE2";
				case SAGE_STAFF_F: return "AID_MAGEF3_EL"; // Use Ilyana's animations 
				case SAGE_KNIFE_F: return "AID_MAGEF3_CA"; // Use Calill's animations
				case FALCON_KNIGHT: return "AID_PEGASU2"; 
				case ELINCIA_FALCON_KNIGHT: return "AID_PEGASU2_ELI";
				case HALBERDIER: return "AID_SOLDIER2";
				case HALBERDIER_F: return "AID_SOLDIER2F";
				case SWORDMASTER: return "AID_SWORDER2";
				case SWORDMASTER_F: return "AID_SWORDER2F_LU"; // Lucia is the default swordmaster battle animation.
				case ASSASSIN: return "AID_THIEF2";
				case WYVERN_LORD: case WYVERN_LORD_F: return "AID_DRAGON2";
				case VALKYRIE: return "AID_CLERIC2";
				case SNIPER: return "AID_ARCHER2";
				case BERSERKER: return "AID_BARBARIAN2";
				case WARRIOR: return "AID_FIGHTER2";
				
				// I don't think we actually need any of these.
				case CAT: return "AID_BEASTCA_LA"; // Use Ranulf's human form.
				// Lethe doesn't have one here...?
				//case CAT_F: return null;
				case TIGER: return "AID_BEASTTI_MO"; // Use Mordecai
				case HAWK: return "AID_BIRDFA_VU"; // Use Ulki
				case CROW: return "AID_BIRDCR_CH"; // I think this is Seeker
				case W_HERON: return "AID_BIRDEG";
				//case W_HERON_F: return "AID_BIRDEGF";
				case WHITE_DRAGON: return "AID_NASIR";
				case RED_DRAGON: case RED_DRAGON_F: return "AID_DRAGONRE";
				
				default: return null;
			}
		}
		
		public String getLaguzTransformedAidString() {
			switch (this) {
				case CAT:
				case FERAL_CAT:
					return "AID_BEAST";
				case CAT_F:
				case FERAL_CAT_F:
					return "AID_BEAST_RE"; // This is Lethe's transformed.
				case TIGER:
				case FERAL_TIGER:
					return "AID_TIGER";
				case HAWK:
				case FERAL_HAWK:
					return "AID_FALCON";
				case CROW:
				case FERAL_CROW:
					return "AID_CROW";
				case W_HERON: return "AID_EGRET";
				case W_HERON_F: return "AID_EGRET";
				case WHITE_DRAGON: return "AID_WDRAGON"; // This is the only white dragon human form we get...
				
				case RED_DRAGON: 
				case FERAL_RED_DRAGON:
					return "AID_RDRAGON";
				case RED_DRAGON_F:
				case FERAL_RED_DRAGON_F:
					return "AID_RDRAGON_EN"; // There seems to be a problem using regular red dragon for a female dragon. We'll use Ena's animations instead.
				default: return null;
			}
		}
		
		public int getTransformSTRBonus() {
			switch (this) {
			case CAT: case CAT_F: case HAWK: return 6;
			case TIGER: return 7;
			case LION: return 8;
			case CROW: return 5;
			case RED_DRAGON: case WHITE_DRAGON: return 10;
			case RED_DRAGON_F: return 5;
			default: return 0;
			}
		}
		
		public int getTransformMAGBonus() {
			switch (this) {
			case CROW: return 1;
			case HERON: return 5;
			default: return 0;
			}
		}
		
		public int getTransformSKLBonus() {
			switch (this) {
			case HERON: return 3;
			case CAT: case CAT_F: case TIGER: case LION: case CROW: case RED_DRAGON_F: return 4;
			case HAWK: case WHITE_DRAGON: return 5;
			case RED_DRAGON: return 6;
			default: return 0;
			}
		}
		
		public int getTransformSPDBonus() {
			switch (this) {
			case CAT: case CAT_F: case TIGER: case LION: case HAWK: case RED_DRAGON: case WHITE_DRAGON: return 3;
			case CROW: case HERON: case RED_DRAGON_F: return 4;
			default: return 0;
			}
		}
		
		public int getTransformDEFBonus() {
			switch (this) {
			case CAT: case CAT_F: case LION: case WHITE_DRAGON: return 5;
			case HAWK: case RED_DRAGON: case RED_DRAGON_F: return 4;
			case TIGER: case CROW: return 3;
			case HERON: return 1;
			default: return 0;
			}
		}
		
		public int getTransformRESBonus() {
			switch (this) {
			case CAT: case CAT_F: case TIGER: case LION: return 3;
			case HAWK: return 2;
			case CROW: return 4;
			case HERON: case RED_DRAGON: case RED_DRAGON_F: case WHITE_DRAGON: return 5;
			default: return 0;
			}
		}
	}
	
	public enum Skill {
		NONE(null),
		
		// These skills are hidden. Some of these, I have no idea what they do.
		LORD("SID_HERO"), FEMALE("SID_FEMALE"), PROMOTED("SID_HIGHER"), MOVE_AGAIN("SID_TWICE"), FLIER("SID_FLY"), RAIDER("SID_VILLAGEDESTROY"),
		BOSS("SID_BOSS"), FINAL_BOSS("SID_FINAL"), LAGUZ("SID_ANIMALIZE"), LYCANTHROPE("SID_LYCANTHROPE"), FERAL("SID_FIXEDBEAST"),
		FORCE_S_RANK("SID_WLUPTOS"), IMPREGNABLE("SID_IMGREGNABLE"), FLY_THROUGH("SID_FLYTHRU"), WALK_THROUGH("SID_WALKTHRU"), 
		SHOVE("SID_TACKLE"),
		
		// Not sure if these work...
		BARGAIN("SID_BARGAIN"), CHANT_HP("SID_CHANTHP"), CHANT_STR("SID_CHANTSTR"), CHANT_MAG("SID_CHANTMPOW"), CHANT_SKL("SID_CHANTTECH"),
		CHANT_SPD("SID_CHANTQUICK"), CHANT_LCK("SID_CHANTLUCK"), CHANT_DEF("SID_CHANTDEF"), CHANT_RES("SID_CHANTMDEF"), CHARM("SID_CHARISMA"),
		
		// Not sure what these are...
		CHAOS("SID_CHAOS"), CONFRONT("SID_CONFRONT"), ABSMOVE("SID_ABSMOVE"), EVIL_EYE("SID_EVILEYE"), EVENT_CC("SID_EVENTCC"),
		SUMMONED("SID_SUMMONED"), SHOOT("SID_SHOOT"), TEMP_ON_DIE("SID_TEMP_ON_DIE"), EQREV_A("SID_EQREV_A"), WEAK_A("SID_WEAK_A"),
		AHIMSA("SID_AHIMSA"),
		
		// Special
		DISCIPLINE("SID_HANDI"), EQUIP_FANG("SID_EQUIPFANG"), LUMINA("SID_EQUIPLIGHT"), CANTO("SID_CHANT"), STEAL("SID_STEAL"), VORTEX("SID_FLUTTER"),
		REINFORCE("SID_REINFORCEMENTS"), KEY_50("SID_KEY50"), INSIGHT("SID_TELEGNOSIS"), VIGILANCE("SID_BIGEAR"), MANTLE("SID_GODDESSBLESS"), 
		TRIANGLE_ATTACK_A("SID_TRI_A"), TRIANGLE_ATTACK_B("SID_TRI_B"), KEY_0("SID_KEY0"), IMMORTAL("SID_IMMORTAL"), CRITICAL_UP("SID_CRITRISE"),
		
		// Weapon locks?
		EQUIP_A("SID_EQ_A"), // Rolf's Bow lock 
		EQUIP_B("SID_EQ_B"), // Amiti lock
		EQUIP_C("SID_EQ_C"), // Laguz Royals lock (Laguz Band?)
		EQUIP_D("SID_EQ_D"), // Knight's Ward lock
		
		// Paladin added weapon disciplines
		EQUIP_SWORD("SID_EQSW"), EQUIP_LANCE("SID_EQLA"), EQUIP_AXE("SID_EQAX"), EQUIP_BOW("SID_EQBW"),
		
		// Sage added weapon disciplines
		EQUIP_KNIFE("SID_EQUIPKNIFE"), EQUIP_STAFF("SID_EQRD"),
		
		// Normal Skills
		PARAGON("SID_ELITE"), RENEWAL("SID_TURNREGENE"), CELERITY("SID_SWIFT"), RESOLVE("SID_BLAVE"), TEMPEST("SID_TEMPER"), SERENITY("SID_CALM"),
		SAVIOR("SID_RESCUEP"), VANTAGE("SID_AMBUSH"), NIHIL("SID_GRASP"), WRATH("SID_ANGER"), GUARD("SID_DEFENCE"), MIRACLE("SID_PRAY"), 
		ADEPT("SID_CONTINUATION"), CORROSION("SID_WEAPONDESTROY"), COUNTER("SID_COUNTER"), DAUNT("SID_HORROR"),
		PROVOKE("SID_PROVOKE"), SHADE("SID_SHADE"), GAMBLE("SID_GAMBLE"), PARITY("SID_FAIRNESS"), SMITE("SID_TACKLE2"), BLOSSOM("SID_FRAC90"),
		
		// Occult skills
		SOL("SID_SUNTRICK"), LUNA("SID_MOONTRICK"), ASTRA("SID_STARTRICK"), LETHALITY("SID_ASSASSINATE"), DEADEYE("SID_SNIPE"), COLOSSUS("SID_RUMBLE"),
		STUN("SID_IMPACT"), ROAR("SID_SNARL"), BOON("SID_EARTHBLESSING"), BLESSING("SID_SKYBLESSING"), AETHER("SID_SUNMOON"), FLARE("SID_BRIGHTNESS"),
		CANCEL("SID_WINGSHIELD")
		;
		
		private String sid;
		
		private static Map<String, Skill> map = new HashMap<String, Skill>();
		
		static {
			for (Skill skill : Skill.values()) {
				map.put(skill.sid, skill);
			}
		}
		
		private Skill(String sid) {
			this.sid = sid;
		}
		
		public static Skill withSID(String sid) {
			return map.get(sid);
		}
		
		public String getSID() {
			return sid;
		}
		
		public String getDisplayString() {
			return WhyDoesJavaNotHaveThese.inCamelCase(toString()).replace("_", " ");
		}
		
		public static Set<Skill> allValidSkills = new HashSet<Skill>(Arrays.asList(PARAGON, RENEWAL, CELERITY, RESOLVE, TEMPEST, SERENITY, SAVIOR, VANTAGE,
				NIHIL, WRATH, GUARD, MIRACLE, ADEPT, CORROSION, COUNTER, DAUNT, PROVOKE, SHADE, GAMBLE, PARITY, SMITE, BLOSSOM, INSIGHT, VIGILANCE));
		public static Set<Skill> playerOnlySkills = new HashSet<Skill>(Arrays.asList(PARAGON, SAVIOR, PROVOKE, SHADE, GAMBLE, SMITE, BLOSSOM));
		public static Set<Skill> replaceableInvalidSkills = new HashSet<Skill>(Arrays.asList(REINFORCE));
		
		public static Set<Skill> occultSkills = new HashSet<Skill>(Arrays.asList(SOL, LUNA, ASTRA, LETHALITY, DEADEYE, COLOSSUS, STUN, ROAR, BOON, BLESSING, AETHER, FLARE, CANCEL));
		public static Map<CharacterClass, Skill> occultSkillsByClass = new HashMap<CharacterClass, Skill>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
			put(CharacterClass.LORD, AETHER);
			
			put(CharacterClass.TITANIA_PALADIN, SOL);
			put(CharacterClass.AXE_PALADIN, SOL);
			put(CharacterClass.AXE_PALADIN_F, SOL);
			put(CharacterClass.LANCE_PALADIN, SOL);
			put(CharacterClass.LANCE_PALADIN_F, SOL);
			put(CharacterClass.SWORD_PALADIN, SOL);
			put(CharacterClass.SWORD_PALADIN_F, SOL);
			put(CharacterClass.BOW_PALADIN, SOL);
			put(CharacterClass.BOW_PALADIN_F, SOL);
			put(CharacterClass.VALKYRIE, SOL);
			
			put(CharacterClass.WARRIOR, COLOSSUS);
			put(CharacterClass.BERSERKER, COLOSSUS);
			
			put(CharacterClass.SWORDMASTER, ASTRA);
			put(CharacterClass.SWORDMASTER_F, ASTRA);
			
			put(CharacterClass.WYVERN_LORD, STUN);
			put(CharacterClass.WYVERN_LORD_F, STUN);
			put(CharacterClass.FALCON_KNIGHT, STUN);
			put(CharacterClass.ELINCIA_FALCON_KNIGHT, STUN);
			
			put(CharacterClass.HALBERDIER, LUNA);
			put(CharacterClass.HALBERDIER_F, LUNA);
			put(CharacterClass.GENERAL, LUNA);
			
			put(CharacterClass.SNIPER, DEADEYE);
			
			put(CharacterClass.ASSASSIN, LETHALITY);
			put(CharacterClass.ASSASSIN_F, LETHALITY);
			
			put(CharacterClass.CAT, ROAR);
			put(CharacterClass.FERAL_CAT, ROAR);
			put(CharacterClass.TIGER, ROAR);
			put(CharacterClass.FERAL_TIGER, ROAR);
			put(CharacterClass.LION, ROAR);
			put(CharacterClass.FERAL_LION, ROAR);
			
			put(CharacterClass.HAWK, CANCEL);
			put(CharacterClass.FERAL_HAWK, CANCEL);
			put(CharacterClass.TIBARN_HAWK, CANCEL);
			
			put(CharacterClass.CROW, VORTEX);
			put(CharacterClass.FERAL_CROW, VORTEX);
			put(CharacterClass.NAESALA_CROW, VORTEX);
			
			put(CharacterClass.BLACK_DRAGON, BOON);
			put(CharacterClass.FERAL_BLACK_DRAGON, BOON);
			put(CharacterClass.FERAL_RED_DRAGON, BOON);
			put(CharacterClass.FERAL_RED_DRAGON_F, BOON);
			put(CharacterClass.FERAL_WHITE_DRAGON, BOON);
			put(CharacterClass.RED_DRAGON, BOON);
			put(CharacterClass.RED_DRAGON_F, BOON);
			put(CharacterClass.WHITE_DRAGON, BOON);
			
			put(CharacterClass.HERON, BLESSING);
			put(CharacterClass.FERAL_HERON, BLESSING);
			put(CharacterClass.FERAL_W_HERON, BLESSING);
			put(CharacterClass.FERAL_W_HERON_F, BLESSING);
			put(CharacterClass.W_HERON, BLESSING);
			put(CharacterClass.W_HERON_F, BLESSING);
			
			put(CharacterClass.SAGE, FLARE);
			put(CharacterClass.SAGE_F, FLARE);
			put(CharacterClass.SAGE_KNIFE, FLARE);
			put(CharacterClass.SAGE_KNIFE_F, FLARE);
			put(CharacterClass.SAGE_STAFF, FLARE);
			put(CharacterClass.SAGE_STAFF_F, FLARE);
			put(CharacterClass.FIRE_SAGE, FLARE);
			put(CharacterClass.FIRE_SAGE_F, FLARE);
			put(CharacterClass.FIRE_SAGE_KNIFE, FLARE);
			put(CharacterClass.FIRE_SAGE_STAFF, FLARE);
			put(CharacterClass.THUNDER_SAGE, FLARE);
			put(CharacterClass.THUNDER_SAGE_F, FLARE);
			put(CharacterClass.THUNDER_SAGE_KNIFE, FLARE);
			put(CharacterClass.THUNDER_SAGE_STAFF, FLARE);
			put(CharacterClass.WIND_SAGE, FLARE);
			put(CharacterClass.WIND_SAGE_F, FLARE);
			put(CharacterClass.WIND_SAGE_KNIFE, FLARE);
			put(CharacterClass.WIND_SAGE_STAFF, FLARE);
			put(CharacterClass.BISHOP, FLARE);
			put(CharacterClass.BISHOP_F, FLARE);
		}};
		
		public boolean isModifiable() {
			return allValidSkills.contains(this) || replaceableInvalidSkills.contains(this);
		}
		
		public boolean isOccult() {
			return occultSkills.contains(this);
		}
		
		public static Skill occultSkillForClass(CharacterClass charClass) {
			return occultSkillsByClass.get(charClass);
		}
	}
	
	public enum Item {
		NONE(null),
		
		IRON_SWORD("IID_IRONSWORD"), PRACTICE_SWORD("IID_PRACTICESWORD"), SLIM_SWORD("IID_SLIMSWORD"), STEEL_SWORD("IID_STEELSWORD"), 
		SILVER_SWORD("IID_SILVERSWORD"), IRON_BLADE("IID_IRONBLADE"), SILVER_BLADE("IID_SILVERBLADE"), VENIN_EDGE("IID_POISONSWORD"),
		REGAL_SWORD("IID_LONGSWORD"), BRAVE_SWORD("IID_BRAVESWORD"), VAGUE_KATTI("IID_WATOU"), KILLING_EDGE("IID_KILLSWORD"), ARMORSLAYER("IID_ARMORKILLER"),
		LAGUZSLAYER("IID_RANCEBASTER"), LONGSWORD("IID_HORSEKILLER"), RUNESWORD("IID_RUNESWORD"), ALONDITE("IID_ALONDITE"), RAGNELL("IID_RAGNELL"),
		GURGURANT("IID_GURGURANT"), AMITI("IID_CRIMEASWORD"), STEEL_BLADE("IID_STEELBLADE"), SONIC_SWORD("IID_SONICBLADE"),
		
		IRON_LANCE("IID_IRONLANCE"), SLIM_LANCE("IID_SLIMLANCE"), STEEL_LANCE("IID_STEELLANCE"), SILVER_LANCE("IID_SILVERLANCE"),
		VENIN_LANCE("IID_POISONLANCE"), BRAVE_LANCE("IID_BRAVELANCE"), KILLER_LANCE("IID_KILLERLANCE"), KNIGHT_KILLER("IID_KNIGHTKILLER"),
		JAVELIN("IID_HANDSPEAR"), SPEAR("IID_SLENDERSPEAR"), FLAME_LANCE("IID_FRAMELANCE"), LAGUZ_LANCE("IID_DRAGONRANCE"), SHORT_SPEAR("IID_AXEBASTER"),
		WISHBLADE("IID_ZANEZPHTE"), HEAVY_SPEAR("IID_ARMORBREAK"),
		
		IRON_AXE("IID_IRONAXE"), PRACTICE_AXE("IID_PRACTICEAXE"), STEEL_AXE("IID_STEELAXE"), SILVER_AXE("IID_SILVERAXE"), VENIN_AXE("IID_POISONAXE"),
		BRAVE_AXE("IID_BRAVEAXE"), KILLER_AXE("IID_KILLERAXE"), POLEAX("IID_POLEAXE"), HAMMER("IID_HAMMER"), DEVIL_AXE("IID_DEVILAXE"), HAND_AXE("IID_HANDAXE"),
		TOMAHAWK("IID_TOMAHAWK"), BOLT_AXE("IID_SONICAXE"), SHORT_AXE("IID_SWORDBASTER"), LAGUZ_AXE("IID_BEASTAXE"), URVAN("IID_URVAN"),
		
		DOUBLE_BOW("IID_LONGLONGBOW"), IRON_BOW("IID_IRONBOW"), STEEL_BOW("IID_STEELBOW"), SILVER_BOW("IID_SILVERBOW"), VENIN_BOW("IID_POISONBOW"),
		KILLER_BOW("IID_KILLERBOW"), BRAVE_BOW("IID_BRAVEBOW"), LAGUZ_BOW("IID_SHORTBOW"), LONGBOW("IID_LONGBOW"), ROLF_BOW("IID_LOFABOW"),
		BRIGHT_BOW("IID_LIGHTNINGBOW"),
		
		BALLISTA("IID_LONGARCH"), IRON_BALLISTA("IID_IRONARCH"), KILLER_BALLISTA("IID_KILLERARCH"), ONAGER("IID_ONAGER"),
		
		FIRE("IID_FIRE"), ELFIRE("IID_ELFIRE"), BOLGANONE("IID_BOLGANONE"), REXFLAME("IID_FORBLAZE"), METEOR("IID_METEOR"),
		WIND("IID_WIND"), ELWIND("IID_ELWIND"), TORNADO("IID_TORNADO"), REXCALIBUR("IID_FIMBULVETR"), BLIZZARD("IID_BLIZZARD"),
		THUNDER("IID_THUNDER"), ELTHUNDER("IID_ELTHUNDER"), THORON("IID_THORON"), REXBOLT("IID_MJOLNIR"), BOLTING("IID_THUNDERSTORM"),
		LIGHT("IID_LIGHTNING"), SHINE("IID_SHINE"), NOSFERATU("IID_RESIRE"), REXAURA("IID_AURA"), PURGE("IID_PURGE"),
		
		ASHERA_STAFF("IID_GODDESSROD"), HEAL("IID_LIVE"), MEND("IID_RELIVE"), RECOVER("IID_RECOVER"), PHYSIC("IID_REBLOW"), FORTIFY("IID_RESERVE"),
		RESTORE("IID_REST"), SILENCE("IID_SILENCE"), SLEEP("IID_SLEEP"), WARP("IID_WARP"), RESCUE("IID_RESCUE"), TORCH_STAFF("IID_TORCH"), 
		HAMMERNE("IID_HAMMERNE"), WARD("IID_MSHIELD"),
		
		VORTEX("IID_FLUTTER"), BERSERK("IID_BERSERK"), UNLOCK("IID_UNLOCK"),
		
		KNIFE("IID_KNIFE"), DAGGER("IID_DAGGER"), STILETTO("IID_STILETTO"),
		
		LION_CLAW("IID_LIONNAIL"), TIGER_CLAW("IID_TIGERNAIL"), CAT_CLAW("IID_CATNAIL"),
		
		HAWK_BEAK("IID_HAWKBEAK"), CROW_BEAK("IID_CROWBEAK"), HAWK_KING_BEAK("IID_HAWKBEAK_KING"), CROW_KING_BEAK("IID_CROWBEAK_KING"),
		
		WHITE_BREATH("IID_WHITEBREATH"), RED_BREATH("IID_REDBREATH"),
		
		LAGUZ_STONE("IID_CHANGESTONE"), LAGUZ_STONE_1("IID_CHANGESTONE1"), MASTER_SEAL("IID_MASTERPROOF"), CHEST_KEY("IID_TREASUREKEY"), DOOR_KEY("IID_DOORKEY"), 
		VULNERARY("IID_VULNERARY"), ELIXIR("IID_ELIXIR"), PURE_WATER("IID_HOLYWATER"), ANTITOXIN("IID_DETOXDRUG"), TORCH("IID_JACKLIGHT"), COIN("IID_COIN"),
		
		KNIGHT_RING("IID_KNIGHTRING"), LAGUZGUARD("IID_VSBEAST"), BEORCGUARD("IID_VSHUMAN"), FULL_GUARD("IID_DELPHISHIELD"), KNIGHT_WARD("IID_KNIGHTSHIELD"),
		DEMI_BAND("IID_CHANGERING"), LAGUZ_BAND("IID_CHANGERING2"),
		
		SWORD_BAND("IID_SWORDERSRING"), SOLDIER_BAND("IID_SOLDIERRING"), FIGHTERS_BAND("IID_FIGHTERSRING"), ARCHER_BAND("IID_ARCHERSRING"), 
		KNIGHT_BAND("IID_ARMORSRING"), PALADIN_BAND("IID_SOCIALKNIGHTSRING"), PEGASUS_BAND("IID_PEGASUSKNIGHTSRING"), WYVERN_BAND("IID_DRAGONKNIGHTSRING"),
		MAGE_BAND("IID_MAGESRING"), PRIEST_BAND("IID_PRIESTSRING"), THIEF_BAND("IID_THIEFSRING"),
		
		SERAPH_ROBE("IID_HPDROP"), ENERGY_DROP("IID_POWERDROP"), SPIRIT_DUST("IID_MAGICDROP"), SECRET_BOOK("IID_TECHDROP"), SPEEDWING("IID_SPEEDDROP"),
		ASHERA_ICON("IID_LUCKDROP"), DRACOSHIELD("IID_DEFDROP"), TALISMAN("IID_MDDROP"), BOOTS("IID_BOOTS"), STATUE_FRAG("IID_BODYRING"), 
		ARMS_SCROLL("IID_WEAPONDROP"),
		
		SILVER_CARD("IID_SILVERCARD"),
		
		WHITE_GEM("IID_WHITEGEM"), BLUE_GEM("IID_BLUEGEM"), RED_GEM("IID_REDGEM"),
		
		PARAGON_SCROLL("IID_ELITE"), OCCULT_SCROLL("IID_ESOTERIC"), RESOLVE_SCROLL("IID_BLAVE"), SAVIOR_SCROLL("IID_RESCUEP"), VANTAGE_SCROLL("IID_AMBUSH"),
		NIHIL_SCROLL("IID_GRASP"), WRATH_SCROLL("IID_ANGER"), CORROSION_SCROLL("IID_WEAPONDESTROY"), MIRACLE_SCROLL("IID_PRAY"), COUNTER_SCROLL("IID_COUNTER"),
		DAUNT_SCROLL("IID_HORROR"), PROVOKE_SCROLL("IID_PROVOKE"), SHADE_SCROLL("IID_SHADE"), GAMBLE_SCROLL("IID_GAMBLE"), PARITY_SCROLL("IID_FAIRNESS"),
		SMITE_SCROLL("IID_TACKLE2"), GUARD_SCROLL("IID_DEFENCE"), ADEPT_SCROLL("IID_CONTINUATION"), RENEWAL_SCROLL("IID_TURNREGENE"),
		
		// Not sure what these are. They look like scrolls that are unused.
		CELERITY_SCROLL("IID_SWIFT"), IID_IMPREGNABLE("IID_IMPREGNABLE"), TEMPEST_SCROLL("IID_TEMPER"), SERENITY_SCROLL("IID_CALM"), CANTO_SCROLL("IID_CHANT"),
		BLOSSOM_SCROLL("IID_FRAC90"), REINFORCE_SCROLL("IID_REINFORCEMENTS"), INSIGHT_SCROLL("IID_TELEGNOSIS"), VIGILANCE_SCROLL("IID_BIGEAR"),
		;
		
		public enum ItemType {
			
			NONE(null), SWORD("sword"), LANCE("lance"), AXE("axe"), BOW("bow"), FIRE("flame"), THUNDER("thunder"), WIND("wind"), STAFF_LIGHT("rod"), KNIFE("knife"), LAGUZ("fang"),
			
			ACCESSORY("acc"), ITEM("item");
			
			private static Map<String, ItemType> map = new HashMap<String, ItemType>();
			
			private String typeString;
			
			static {
				for (ItemType type : ItemType.values()) {
					map.put(type.typeString, type);
				}
			}
			
			private ItemType(String string) {
				typeString = string;
			}
			
			public String getTypeString() {
				return typeString;
			}
			
			public static ItemType typeWithString(String string) {
				return map.get(string);
			}
			
			public boolean isMagicType() {
				switch (this) {
				case FIRE:
				case THUNDER:
				case WIND:
				case STAFF_LIGHT:
					return true;
				default: 
					return false;
				}
			}
			
			public boolean isWeaponType() {
				switch (this) {
				case SWORD:
				case LANCE:
				case AXE:
				case BOW:
				case FIRE:
				case WIND:
				case THUNDER:
				case STAFF_LIGHT:
				case KNIFE:
				case LAGUZ:
					return true;
				default:
					return false;
				}
			}
		}
		
		public enum WeaponTraits {
			CANNOT_CRIT("crit0"), UNBREAKABLE("infinity"), POISON("poison"), BRAVE("twice"), UNSELLABLE("valuable"), MAGIC_SWORD("magsw"),
			STEAL_HP("resire"), CANNOT_BE_CRIT("sealcrit"), RANGED_PHYSICAL_SWORD("stormsw"), BYPASS_BLESSED_ARMOR("weakA"),
			
			GUARANTEED_HIT("absolutehit"), // I think? Used for the stone thrower. It could also mean it doesn't use the user's STR stat.
			
			LAGUZ_WEAPON("fang"), SIEGE_WEAPON("longfar"), BALLISTA("sh"), AOE_ATTACK("areaattack"),
			
			FLUTTER("flutter"), // Seems to only be used for Vortex?
			
			// These are for the demi band. Not sure what each one does.
			FORCE_LAGUZ_TRANSFORM("beastsamul"), STAT_REDUCTION("JH"), NON_ROYAL_LAGUZ_LOCK("eqrevA"),
			
			MOVE_AGAIN("movtw"), HALVE_LAGUZ_DAMAGE("lycdamhalf"), IGNORE_LAGUZ_EFFECTIVENESS("lycsfxseal"), IGNORE_EFFECTIVE_DAMAGE("sfxseal"),
			
			HERO_LOCK("heroonly"), ARCHER_LOCK("shootonly"), BK_LOCK("blackonly"), ASHNARD_LOCK("finalonly"), ROLF_LOCK("eqA"), ELINCIA_LOCK("eqB"),
			BEORC_LOCK("humanonly"), LAGUZ_LOCK("beastonly"), SOLDIER_KNIGHT_CAV_LOCK("eqD"), LAGUZ_ROYAL_LOCK("eqC"),
			
			// This is on boots.
			BOOTS_UNKNOWN("noneart"),
			
			// Both dragon breaths have this.
			DRAGON_BREATH("breath");
			
			private static Map<String, WeaponTraits> map = new HashMap<String, WeaponTraits>();
			
			private String traitString;
			
			static {
				for (WeaponTraits trait : WeaponTraits.values()) {
					map.put(trait.traitString, trait);
				}
			}
			
			private WeaponTraits(String string) {
				traitString = string;
			}
			
			public String getTraitString() {
				return traitString;
			}
			
			public static WeaponTraits traitWithString(String string) {
				return map.get(string);
			}
		}
		
		public enum Effectiveness {
			FLIERS("fly"), CAVALRY("knight"), ARMOR("armor"), LAGUZ("alize"), BEAST_TRIBE("beast"), DOORS("doorbreak"), DRAGON_TRIBE("dragon");
			
			private static Map<String, Effectiveness> map = new HashMap<String, Effectiveness>();
			
			private String effectString;
			
			static {
				for (Effectiveness effect : Effectiveness.values()) {
					map.put(effect.effectString, effect);
				}
			}
			
			private Effectiveness(String string) {
				effectString = string;
			}
			
			public String getEffectString() {
				return effectString;
			}
			
			public String getEffectDescription() {
				switch (this) {
				case FLIERS:
					return "Effective against fliers.";
				case CAVALRY:
					return "Effective against horseback units.";
				case ARMOR:
					return "Effective against armored units.";
				case LAGUZ:
					return "Effective against laguz units.";
				case BEAST_TRIBE:
					return "Effective against beasts.";
				case DOORS:
					return "";
				case DRAGON_TRIBE:
					return "Effective against dragons.";
				default:
					return "";
				}
			}
			
			public String getShortDescription() {
				switch (this) {
				case FLIERS:
					return "flying";
				case CAVALRY:
					return "horseback";
				case ARMOR:
					return "armored";
				case LAGUZ:
					return "laguz";
				case BEAST_TRIBE:
					return "beast";
				case DRAGON_TRIBE:
					return "dragon";
				default:
					return "";
				}
			}
			
			public static Effectiveness effectWithString(String string) {
				return map.get(string);
			}
			
			public static Effectiveness[] eligibleEffects() {
				return new Effectiveness[] {FLIERS, CAVALRY, ARMOR, LAGUZ, BEAST_TRIBE, DRAGON_TRIBE};
			}
		}
		
		public enum EffectAnimation1 {
			LIGHT("EID_LIGHT2"), WIND("EID_WIND2"), FIRE("EID_FIRE2"), THUNDER("EID_THUNDER2"), RAGNELL_SHOCKWAVE("EID_REPPU1");
			
			private String animationString;
			
			private EffectAnimation1(String string) {
				animationString = string;
			}
			
			public String getAnimationString() {
				return animationString;
			}
		}
		
		public enum EffectAnimation2 {
			LIGHT("EID_LIGHT2_WP"), WIND("EID_WIND2_WP"), FIRE("EID_FIRE2_WP"), THUNDER("EID_THUNDER2");
			
			private String animationString;
			
			private EffectAnimation2(String string) {
				animationString = string;
			}
			
			public String getAnimationString() {
				return animationString;
			}
		}
		
		private String iid;
		
		private static Map<String, Item> map = new HashMap<String, Item>();
		
		static {
			for (Item item : Item.values()) {
				map.put(item.iid, item);
			}
		}
		
		private Item(String iid) {
			this.iid = iid;
		}
		
		public static Item withIID(String iid) {
			return map.get(iid);
		}
		
		public String getIID() {
			return iid;
		}
		
		public String getDBX() {
			String dbx = iid.substring(4) + ".dbx";
			return dbx;
		}
		
		public static Set<Item> allSwords = new HashSet<Item>(Arrays.asList(IRON_SWORD, PRACTICE_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, 
				SILVER_BLADE, VENIN_EDGE, REGAL_SWORD, BRAVE_SWORD, VAGUE_KATTI, KILLING_EDGE, ARMORSLAYER, LAGUZSLAYER, LONGSWORD, RUNESWORD, ALONDITE, 
				RAGNELL, GURGURANT, AMITI, STEEL_BLADE, SONIC_SWORD));
		public static Set<Item> allLances = new HashSet<Item>(Arrays.asList(IRON_LANCE, SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, VENIN_LANCE, BRAVE_LANCE, 
				KILLER_LANCE, KNIGHT_KILLER, JAVELIN, SPEAR, FLAME_LANCE, LAGUZ_LANCE, SHORT_SPEAR, WISHBLADE, HEAVY_SPEAR));
		public static Set<Item> allAxes = new HashSet<Item>(Arrays.asList(IRON_AXE, PRACTICE_AXE, STEEL_AXE, SILVER_AXE, VENIN_AXE, BRAVE_AXE, KILLER_AXE, 
				POLEAX, HAMMER, DEVIL_AXE, HAND_AXE, TOMAHAWK, BOLT_AXE, SHORT_AXE, LAGUZ_AXE, URVAN));
		public static Set<Item> allBows = new HashSet<Item>(Arrays.asList(DOUBLE_BOW, IRON_BOW, STEEL_BOW, SILVER_BOW, VENIN_BOW, KILLER_BOW, BRAVE_BOW, 
				LAGUZ_BOW, LONGBOW, ROLF_BOW, BRIGHT_BOW));
		
		public static Set<Item> allFireMagic = new HashSet<Item>(Arrays.asList(FIRE, ELFIRE, BOLGANONE, REXFLAME, METEOR));
		public static Set<Item> allWindMagic = new HashSet<Item>(Arrays.asList(WIND, ELWIND, TORNADO, REXCALIBUR, BLIZZARD));
		public static Set<Item> allThunderMagic = new HashSet<Item>(Arrays.asList(THUNDER, ELTHUNDER, THORON, REXBOLT, BOLTING));
		public static Set<Item> allLightMagic = new HashSet<Item>(Arrays.asList(LIGHT, SHINE, NOSFERATU, REXAURA, PURGE));
		
		public static Set<Item> allBasicWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, IRON_LANCE, IRON_AXE, IRON_BOW, KNIFE,
				FIRE, WIND, THUNDER, LIGHT, HEAL));
		
		public static Set<Item> meleeOnlyWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, PRACTICE_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, 
				SILVER_BLADE, VENIN_EDGE, REGAL_SWORD, BRAVE_SWORD, VAGUE_KATTI, KILLING_EDGE, ARMORSLAYER, LAGUZSLAYER, LONGSWORD, AMITI, STEEL_BLADE,
				IRON_LANCE, SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, VENIN_LANCE, BRAVE_LANCE, 
				KILLER_LANCE, KNIGHT_KILLER, LAGUZ_LANCE, HEAVY_SPEAR, IRON_AXE, PRACTICE_AXE, STEEL_AXE, SILVER_AXE, VENIN_AXE, BRAVE_AXE, KILLER_AXE, 
				POLEAX, HAMMER, DEVIL_AXE, LAGUZ_AXE, URVAN, KNIFE, DAGGER, STILETTO));
		public static Set<Item> rangedOnlyWeapons = new HashSet<Item>(Arrays.asList(DOUBLE_BOW, IRON_BOW, STEEL_BOW, SILVER_BOW, VENIN_BOW, KILLER_BOW, BRAVE_BOW, 
				LAGUZ_BOW, LONGBOW, ROLF_BOW, BRIGHT_BOW, METEOR, BLIZZARD, BOLTING, PURGE));
		
		public static Set<Item> allKnives = new HashSet<Item>(Arrays.asList(KNIFE, DAGGER, STILETTO));
		public static Set<Item> allStaves = new HashSet<Item>(Arrays.asList(ASHERA_STAFF, HEAL, MEND, RECOVER, PHYSIC, FORTIFY, RESTORE, SILENCE, SLEEP, 
				WARP, RESCUE, TORCH_STAFF, HAMMERNE, WARD));
		
		public static Set<Item> allLaguzWeapons = new HashSet<Item>(Arrays.asList(LION_CLAW, TIGER_CLAW, CAT_CLAW, HAWK_BEAK, CROW_BEAK, 
				HAWK_KING_BEAK, CROW_KING_BEAK, WHITE_BREATH, RED_BREATH));
		
		public static Set<Item> allNonWeaponEquips = new HashSet<Item>(Arrays.asList(KNIGHT_RING, LAGUZGUARD, BEORCGUARD, FULL_GUARD, KNIGHT_WARD,
				DEMI_BAND, LAGUZ_BAND, SWORD_BAND, SOLDIER_BAND, FIGHTERS_BAND, ARCHER_BAND, KNIGHT_BAND, PALADIN_BAND, PEGASUS_BAND, WYVERN_BAND, MAGE_BAND, 
				PRIEST_BAND, THIEF_BAND));
		
		public static Set<Item> allStatBoosters = new HashSet<Item>(Arrays.asList(SERAPH_ROBE, ENERGY_DROP, SPIRIT_DUST, SECRET_BOOK, SPEEDWING,
				ASHERA_ICON, DRACOSHIELD, TALISMAN, BOOTS, STATUE_FRAG, ARMS_SCROLL));
		public static Set<Item> allSkillScrolls = new HashSet<Item>(Arrays.asList(PARAGON_SCROLL, OCCULT_SCROLL, RESOLVE_SCROLL, SAVIOR_SCROLL, VANTAGE_SCROLL,
				NIHIL_SCROLL, WRATH_SCROLL, CORROSION_SCROLL, MIRACLE_SCROLL, COUNTER_SCROLL, DAUNT_SCROLL, PROVOKE_SCROLL, SHADE_SCROLL, GAMBLE_SCROLL, 
				PARITY_SCROLL, SMITE_SCROLL, GUARD_SCROLL, ADEPT_SCROLL, RENEWAL_SCROLL, CELERITY_SCROLL, BLOSSOM_SCROLL));
		public static Set<Item> allPromotionItems = new HashSet<Item>(Arrays.asList(MASTER_SEAL));
		public static Set<Item> allConsumables = new HashSet<Item>(Arrays.asList(LAGUZ_STONE, LAGUZ_STONE_1, CHEST_KEY, DOOR_KEY, 
				VULNERARY, ELIXIR, PURE_WATER, ANTITOXIN, TORCH, COIN));
		public static Set<Item> allGems = new HashSet<Item>(Arrays.asList(WHITE_GEM, BLUE_GEM, RED_GEM));
		
		public static Set<Item> allBands = new HashSet<Item>(Arrays.asList(SOLDIER_BAND, ARCHER_BAND, MAGE_BAND, FIGHTERS_BAND, KNIGHT_BAND, PALADIN_BAND,
				PEGASUS_BAND, WYVERN_BAND, THIEF_BAND, PRIEST_BAND, SWORD_BAND));
		
		// These shouldn't be changed or given out.
		public static Set<Item> allRestrictedItems = new HashSet<Item>(Arrays.asList(PRACTICE_SWORD, REGAL_SWORD, ALONDITE, RAGNELL, GURGURANT, AMITI, ROLF_BOW,
				LION_CLAW, HAWK_KING_BEAK, CROW_KING_BEAK, LONGBOW));
		
		public static Set<Item> allERankWeapons = new HashSet<Item>(Arrays.asList(SLIM_SWORD, IRON_SWORD, PRACTICE_SWORD, IRON_LANCE, SLIM_LANCE,
				JAVELIN, IRON_AXE, PRACTICE_AXE, HAND_AXE, STEEL_AXE, DEVIL_AXE, IRON_BOW, FIRE, THUNDER, WIND, HEAL, KNIFE));
		public static Set<Item> allDRankWeapons = new HashSet<Item>(Arrays.asList(VENIN_EDGE, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, LONGSWORD, VENIN_LANCE, STEEL_LANCE,
				KNIGHT_KILLER, HEAVY_SPEAR, VENIN_AXE, HAMMER, POLEAX, VENIN_BOW, LONGBOW, STEEL_BOW, ELFIRE, ELWIND, ELTHUNDER, LIGHT, MEND, TORCH_STAFF));
		public static Set<Item> allCRankWeapons = new HashSet<Item>(Arrays.asList(KILLING_EDGE, LAGUZSLAYER, STEEL_BLADE, SHORT_SPEAR, KILLER_LANCE,
				LAGUZ_LANCE, SHORT_AXE, KILLER_AXE, LAGUZ_AXE, LAGUZ_BOW, KILLER_BOW, METEOR, BOLTING, BLIZZARD, SHINE, WARD, RESTORE, PHYSIC, HAMMERNE,
				DAGGER));
		public static Set<Item> allBRankWeapons = new HashSet<Item>(Arrays.asList(BRAVE_SWORD, SONIC_SWORD, FLAME_LANCE, BRAVE_LANCE, SPEAR, BOLT_AXE,
				BRAVE_AXE, TOMAHAWK, BRAVE_BOW, BRIGHT_BOW, PURGE, SILENCE, SLEEP, RESCUE, RECOVER));
		public static Set<Item> allARankWeapons = new HashSet<Item>(Arrays.asList(SILVER_SWORD, SILVER_BLADE, RUNESWORD, SILVER_LANCE, SILVER_AXE,
				SILVER_BOW, BOLGANONE, THORON, TORNADO, NOSFERATU, WARP, FORTIFY, STILETTO));
		public static Set<Item> allSRankWeapons = new HashSet<Item>(Arrays.asList(VAGUE_KATTI, WISHBLADE, URVAN, DOUBLE_BOW, REXFLAME, REXBOLT, REXCALIBUR,
				REXAURA, ASHERA_STAFF));
		
		public static Set<Item> blacklistedWeapons = new HashSet<Item>(Arrays.asList(PRACTICE_AXE, PRACTICE_SWORD, DEVIL_AXE));
		
		public static Set<Item> siegeTomes = new HashSet<Item>(Arrays.asList(PURGE, METEOR, BLIZZARD, BOLTING));
		
		public static Set<Item> allDroppableWeapons() {
			Set<Item> result = new HashSet<Item>();
			result.addAll(allSwords);
			result.addAll(allLances);
			result.addAll(allAxes);
			result.addAll(allBows);
			result.addAll(allFireMagic);
			result.addAll(allThunderMagic);
			result.addAll(allWindMagic);
			result.addAll(allLightMagic);
			result.addAll(allStaves);
			result.removeAll(allRestrictedItems);
			result.removeAll(blacklistedWeapons);
			return result;
		}
		
		public boolean isSword() { return allSwords.contains(this); }
		public boolean isLance() { return allLances.contains(this); }
		public boolean isAxe() { return allAxes.contains(this); }
		public boolean isBow() { return allBows.contains(this); }
		public boolean isFireMagic() { return allFireMagic.contains(this); }
		public boolean isThunderMagic() { return allThunderMagic.contains(this); }
		public boolean isWindMagic() { return allWindMagic.contains(this); }
		public boolean isLightMagic() { return allLightMagic.contains(this); }
		public boolean isMagic() { return allFireMagic.contains(this) || allThunderMagic.contains(this) || allWindMagic.contains(this) || allLightMagic.contains(this); }
		public boolean isStaff() { return allStaves.contains(this); }
		public boolean isKnife() { return allKnives.contains(this); }
		
		public boolean isERank() { return allERankWeapons.contains(this); }
		public boolean isDRank() { return allDRankWeapons.contains(this); }
		public boolean isCRank() { return allCRankWeapons.contains(this); }
		public boolean isBRank() { return allBRankWeapons.contains(this); }
		public boolean isARank() { return allARankWeapons.contains(this); }
		public boolean isSRank() { return allSRankWeapons.contains(this); }
		
		public boolean isWeapon() { return isSword() || isLance() || isAxe() || isBow() || isMagic() || isStaff(); }
		
		public boolean isBasicWeapon() { return allBasicWeapons.contains(this); }
		public boolean isLaguzWeapon() { return allLaguzWeapons.contains(this); }
		
		public boolean doesMagicDamage() {
			if (isMagic()) { return true; }
			if (this == SONIC_SWORD || this == FLAME_LANCE || this == BOLT_AXE || this == RUNESWORD || this == BRIGHT_BOW) { return true; }
			return false;
		}
		
		public boolean isConsumable() { return allConsumables.contains(this); }
		public boolean isPromotionItem() { return allPromotionItems.contains(this); }
		public boolean isStatBooster() { return allStatBoosters.contains(this); }
		public boolean isTreasure() { return allGems.contains(this); }
		public boolean isSkillScroll() { return allSkillScrolls.contains(this); }
		
		public boolean isBlacklisted() { return blacklistedWeapons.contains(this); }
		public boolean isSiegeTome() { return siegeTomes.contains(this); }
		
		public boolean isMeleeLocked() { return meleeOnlyWeapons.contains(this); }
		public boolean isRangeLocked() { return rangedOnlyWeapons.contains(this); }
		
		public static Set<Item> specialWeaponsForClass(FE9Data.CharacterClass charClass) {
			switch (charClass) {
			case ELINCIA_FALCON_KNIGHT: return new HashSet<Item>(Arrays.asList(AMITI));
			case RANGER: return new HashSet<Item>(Arrays.asList(REGAL_SWORD));
			case ARCHER: case SNIPER: return new HashSet<Item>(Arrays.asList(ROLF_BOW, LONGBOW));
			default: return null;
			}
		}
	}
	
	public enum Chapter {
		
		ALL_CHAPTERS("zmap/always/dispos.cmp", null, null), // Supposedly between-chapter units are loaded here? Only has a cross-difficulty definition.
		
		PROLOGUE("zmap/bmap01/dispos.cmp", "Scripts/C01.cmb", "Mess/c01.m"),
		CHAPTER_1("zmap/bmap02/dispos.cmp", "Scripts/C02.cmb", "Mess/c02.m"),
		CHAPTER_2("zmap/bmap03/dispos.cmp", "Scripts/C03.cmb", "Mess/c03.m"),
		CHAPTER_3("zmap/bmap04/dispos.cmp", "Scripts/C04.cmb", "Mess/c04.m"),
		CHAPTER_4("zmap/bmap05/dispos.cmp", "Scripts/C05.cmb", "Mess/c05.m"),
		CHAPTER_5("zmap/bmap06/dispos.cmp", "Scripts/C06.cmb", "Mess/c06.m"),
		CHAPTER_5_CUTSCENE("zmap/bmap06_2/dispos.cmp", null, null),
		CHAPTER_6("zmap/bmap07/dispos.cmp", "Scripts/C07.cmb", "Mess/c07.m"),
		CHAPTER_7("zmap/bmap08/dispos.cmp", "Scripts/C08.cmb", "Mess/c08.m"),
		CHAPTER_7_CUTSCENE("zmap/bmap08_2/dispos.cmp", null, null),
		CHAPTER_8("zmap/bmap09/dispos.cmp", "Scripts/C09.cmb", "Mess/c09.m"),
		CHAPTER_8_CUTSCENE("zmap/bmap09_2/dispos.cmp", null, null),
		CHAPTER_8_CUTSCENE_2("zmap/bmap09_3/dispos.cmp", null, null),
		CHAPTER_8_CUTSCENE_3("zmap/bmap09_4/dispos.cmp", null, null),
		CHAPTER_8_CUTSCENE_4("zmap/bmap09_5/dispos.cmp", null, null),
		CHAPTER_9("zmap/bmap10/dispos.cmp", "Scripts/C10.cmb", "Mess/c10.m"),
		CHAPTER_10("zmap/bmap11/dispos.cmp", "Scripts/C11.cmb", "Mess/c11.m"),
		CHAPTER_11("zmap/bmap12/dispos.cmp", "Scripts/C12.cmb", "Mess/c12.m"),
		CHAPTER_11_CUTSCENE("zmap/bmap12_2/dispos.cmp", null, null),
		CHAPTER_12("zmap/bmap13/dispos.cmp", "Scripts/C13.cmb", "Mess/c13.m"),
		CHAPTER_13("zmap/bmap14/dispos.cmp", "Scripts/C14.cmb", "Mess/c14.m"),
		CHAPTER_13_CUTSCENE("zmap/bmap14_2/dispos.cmp", null, null),
		CHAPTER_14("zmap/bmap15/dispos.cmp", "Scripts/C15.cmb", "Mess/c15.m"),
		CHAPTER_15("zmap/bmap16/dispos.cmp", "Scripts/C16.cmb", "Mess/c16.m"),
		CHAPTER_16("zmap/bmap17/dispos.cmp", "Scripts/C17.cmb", "Mess/c17.m"),
		CHAPTER_17("zmap/bmap18/dispos.cmp", "Scripts/C18.cmb", "Mess/c18.m"), // All of it.
		CHAPTER_17_CUTSCENE("zmap/bmap18_3/dispos.cmp", null, null), // There's a bmap18_2, but that one only has a map.cmp.
		CHAPTER_18("zmap/bmap19/dispos.cmp", "Scripts/C19.cmb", "Mess/c19.m"),
		CHAPTER_19("zmap/bmap20/dispos.cmp", "Scripts/C20.cmb", "Mess/c20.m"),
		CHAPTER_20("zmap/bmap21/dispos.cmp", "Scripts/C21.cmb", "Mess/c21.m"),
		CHAPTER_21("zmap/bmap22/dispos.cmp", "Scripts/C22.cmb", "Mess/c22.m"),
		CHAPTER_22("zmap/bmap23/dispos.cmp", "Scripts/C23.cmb", "Mess/c23.m"),
		CHAPTER_23("zmap/bmap24/dispos.cmp", "Scripts/C24.cmb", "Mess/c24.m"),
		CHAPTER_24("zmap/bmap25/dispos.cmp", "Scripts/C25.cmb", "Mess/c25.m"),
		CHAPTER_24_CUTSCENE("zmap/bmap25_2/dispos.cmp", null, null),
		CHAPTER_25("zmap/bmap26/dispos.cmp", "Scripts/C26.cmb", "Mess/c26.m"),
		CHAPTER_25_CUTSCENE("zmap/bmap26_2/dispos.cmp", null, null),
		CHAPTER_26("zmap/bmap27/dispos.cmp", "Scripts/C27.cmb", "Mess/c27.m"),
		CHAPTER_27("zmap/bmap28/dispos.cmp", "Scripts/C28.cmb", "Mess/c28.m"),
		CHAPTER_27_BK_FIGHT("zmap/bmap29/dispos.cmp", "Scripts/C29.cmb", "Mess/c29.m"), // This is its own, for some reason.
		CHAPTER_28("zmap/bmap30/dispos.cmp", "Scripts/C30.cmb", "Mess/c30.m"),
		ENDGAME("zmap/bmap31/dispos.cmp", "Scripts/C31.cmb", "Mess/c31.m"),
		ENDGAME_CUTSCENE("zmap/bmap31_2/dispos.cmp", null, null),
		ENDGAME_CUTSCENE_2("zmap/bmap31_3/dispos.cmp", null, null),
		
		MAP_6("zmap/Map6/dispos.cmp", null, null), // Not sure what this is... debug map? 
		T1("zmap/T1/dispos.cmp", null, null), // Only has cross-difficulty. Cutscene?
		T2("zmap/T2/dispos.cmp", null, null), // See above.
		T3("zmap/T3/dispos.cmp", null, null), // Actually, I think these are tutorial chapters.
		
		// Trial maps. These also only have one difficulty (cross-difficulty)
		TRIAL_MAP_1("zmap/trial_01/dispos.cmp", null, null),
		TRIAL_MAP_2("zmap/trial_02/dispos.cmp", null, null),
		TRIAL_MAP_3("zmap/trial_03/dispos.cmp", null, null),
		TRIAL_MAP_4("zmap/trial_04/dispos.cmp", null, null),
		TRIAL_MAP_5("zmap/trial_05/dispos.cmp", null, null),
		TRIAL_MAP_6("zmap/trial_06/dispos.cmp", null, null)
		;
		
		private String path;
		private String scriptPath;
		private String stringsPath;
		
		private Chapter(String path, String scriptPath, String stringsPath) {
			this.path = path;
			this.scriptPath = scriptPath;
			this.stringsPath = stringsPath;
		}
		
		public String getPath() {
			return path;
		}
		
		public String getScriptPath() {
			return scriptPath;
		}
		
		public String getStringsPath() {
			return stringsPath;
		}
		
		public String getDisplayString() {
			return WhyDoesJavaNotHaveThese.inCamelCase(toString()).replace("_", " ");
		}
		
		public String getDisplayNameID() {
			switch (this) {
			case PROLOGUE: return "MCT01";
			case CHAPTER_1: return "MCT02";
			case CHAPTER_2: return "MCT03";
			case CHAPTER_3: return "MCT04";
			case CHAPTER_4: return "MCT05";
			case CHAPTER_5: return "MCT06";
			case CHAPTER_6: return "MCT07";
			case CHAPTER_7: return "MCT08";
			case CHAPTER_8: return "MCT09";
			case CHAPTER_9: return "MCT10";
			case CHAPTER_10: return "MCT11";
			case CHAPTER_11: return "MCT12";
			case CHAPTER_12: return "MCT13";
			case CHAPTER_13: return "MCT14";
			case CHAPTER_14: return "MCT15";
			case CHAPTER_15: return "MCT16";
			case CHAPTER_16: return "MCT17";
			case CHAPTER_17: return "MCT18";
			case CHAPTER_18: return "MCT19";
			case CHAPTER_19: return "MCT20";
			case CHAPTER_20: return "MCT21";
			case CHAPTER_21: return "MCT22";
			case CHAPTER_22: return "MCT23";
			case CHAPTER_23: return "MCT24";
			case CHAPTER_24: return "MCT25";
			case CHAPTER_25: return "MCT26";
			case CHAPTER_26: return "MCT27";
			case CHAPTER_27: return "MCT28";
			case CHAPTER_27_BK_FIGHT: return "MCT29";
			case CHAPTER_28: return "MCT30";
			case ENDGAME: return "MCT31";
			default: return null;
			}
		}
		
		public static Set<Chapter> chaptersWithAllDifficulties = new HashSet<Chapter>(Arrays.asList(PROLOGUE, CHAPTER_1, CHAPTER_2, CHAPTER_3, CHAPTER_4,
				CHAPTER_5, CHAPTER_6, CHAPTER_7, CHAPTER_8, CHAPTER_9, CHAPTER_10, CHAPTER_11, CHAPTER_11, CHAPTER_12, CHAPTER_13, CHAPTER_14, CHAPTER_15,
				CHAPTER_16, CHAPTER_17, CHAPTER_18, CHAPTER_19, CHAPTER_20, CHAPTER_21, CHAPTER_22, CHAPTER_23, CHAPTER_24, CHAPTER_25, CHAPTER_26,
				CHAPTER_27, CHAPTER_27_BK_FIGHT, CHAPTER_28, ENDGAME));
		public static Set<Chapter> chaptersWithOnlyOneDifficulty = new HashSet<Chapter>(Arrays.asList(ALL_CHAPTERS, CHAPTER_5_CUTSCENE, CHAPTER_7_CUTSCENE,
				CHAPTER_8_CUTSCENE, CHAPTER_8_CUTSCENE_2, CHAPTER_8_CUTSCENE_3, CHAPTER_8_CUTSCENE_4, CHAPTER_11_CUTSCENE, CHAPTER_13_CUTSCENE,
				CHAPTER_17_CUTSCENE, CHAPTER_24_CUTSCENE, CHAPTER_25_CUTSCENE, ENDGAME_CUTSCENE, ENDGAME_CUTSCENE_2, T1, T2, T3, TRIAL_MAP_1, TRIAL_MAP_2,
				TRIAL_MAP_3, TRIAL_MAP_4, TRIAL_MAP_5, TRIAL_MAP_6));
		
		public String[] getAllDifficulties() {
			if (chaptersWithOnlyOneDifficulty.contains(this)) {
				return new String[] {path + "/dispos_c.bin"};
			} else {
				return new String[] {path + "/dispos_c.bin", path + "/dispos_h.bin", path + "/dispos_m.bin", path + "/dispos_n.bin"};
			}
		}
		
		public long getStartingOffset() {
			// As far as I can tell, 0x20 is always a date, and conveniently points to the end of the data too. 
			// The section header starts right after at 0x24.
			return 0x20;
		}
		
		public static List<Chapter> allChapters() {
			return new ArrayList<Chapter>(Arrays.asList(PROLOGUE, CHAPTER_1, CHAPTER_2, CHAPTER_3, CHAPTER_4, CHAPTER_5, CHAPTER_6, CHAPTER_7,
					CHAPTER_8, CHAPTER_9, CHAPTER_10, CHAPTER_11, CHAPTER_12, CHAPTER_13, CHAPTER_14, CHAPTER_15, CHAPTER_16, CHAPTER_17, CHAPTER_18,
					CHAPTER_19, CHAPTER_20, CHAPTER_21, CHAPTER_22, CHAPTER_23, CHAPTER_24, CHAPTER_25, CHAPTER_26, CHAPTER_27, CHAPTER_27_BK_FIGHT,
					CHAPTER_28, ENDGAME, ALL_CHAPTERS));
		}
		
		public boolean hasWaterSpawningBandits() {
			switch (this) {
			case CHAPTER_9: // Bandits spawn over water here.
			case CHAPTER_14: // A bandit spawns over water here.
			case CHAPTER_24: // Bandits and berserkers over water here.
				return true; 
			default: return false;
			}
		}
		
		public boolean useRestrictedClassSetForMinions() {
			switch (this) {
			case CHAPTER_1:
			case CHAPTER_2:
			case CHAPTER_3:
			case CHAPTER_4:
			case CHAPTER_5:
			case CHAPTER_6:
			case CHAPTER_7:
				return true;
			default:
				return false;
			}
		}
		
		public Character[] bossCharactersForChapter() {
			switch(this) {
			case CHAPTER_1: return new Character[] { Character.ZAWANA };
			case CHAPTER_2: return new Character[] { Character.IKANAU };
			case CHAPTER_3: return new Character[] { Character.HAVETTI };
			case CHAPTER_4: return new Character[] { Character.MAIJIN };
			case CHAPTER_5: return new Character[] { Character.DAKOVA };
			case CHAPTER_6: return new Character[] { Character.EMIL };
			case CHAPTER_7: return new Character[] { Character.BALMER };
			case CHAPTER_8: return new Character[] { Character.KAMURA };
			case CHAPTER_9: return new Character[] { Character.KOTAFF, Character.NEDATA };
			case CHAPTER_10: return new Character[] { Character.DANOMILL };
			case CHAPTER_11: return new Character[] { Character.MACKOYA };
			case CHAPTER_12: return new Character[] { Character.SEEKER };
			case CHAPTER_13: return new Character[] { Character.NORRIS };
			case CHAPTER_14: return new Character[] { Character.GASHILAMA };
			case CHAPTER_15: return new Character[] { Character.MUARIM }; // Muarim?
			case CHAPTER_16: return new Character[] { Character.KIMAARSI };
			case CHAPTER_17: return new Character[] { Character.OLIVER };
			case CHAPTER_18: return new Character[] { Character.KAYACHEY };
			case CHAPTER_19: return new Character[] { Character.NAESALA, Character.HOMASA };
			case CHAPTER_20: return new Character[] { Character.SHIHARAM };
			case CHAPTER_21: return new Character[] { Character.KASATAI, Character.ENA }; // Ena?
			case CHAPTER_22: return new Character[] { Character.SCHAEFFER };
			case CHAPTER_23: return new Character[] { Character.PETRINE };
			case CHAPTER_24: return new Character[] { Character.RIKARD };
			case CHAPTER_25: return new Character[] { Character.GROMELL };
			case CHAPTER_26: return new Character[] { Character.BERTRAM };
			case CHAPTER_27: return new Character[] { Character.HAFEDD };
			case CHAPTER_28: return new Character[] { Character.HEDDWYN };
			case ENDGAME: return new Character[] { Character.BRYCE };
			default: return new Character[] {};
			}
		}
	}
	
	public enum Affinity {
		HEAVEN("heaven"), WATER("water"), WIND("wind"), THUNDER("thunder"), DARK("dark"), LIGHT("light"), FIRE("fire"), EARTH("telius");
		
		private String internalID;
		
		private static Map<String, Affinity> map = new HashMap<String, Affinity>();
		
		static {
			for (Affinity affinity : Affinity.values()) {
				map.put(affinity.internalID, affinity);
			}
		}
		
		private Affinity(String id) {
			internalID = id;
		}
		
		public static Affinity withID(String affinityID) {
			return map.get(affinityID);
		}
		
		public String getInternalID() {
			return internalID;
		}
	}
}
