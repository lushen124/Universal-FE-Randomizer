package fedata.gcnwii.fe9;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
	
	public static final long CharacterDataStartOffset = 0x30;
	public static final String CharacterDataFilename = "system.cmp/FE8Data.bin";
	public static final int CharacterCount = 0x154; // Maybe?
	public static final int CharacterDataSize = 0x54;
	
	public static final long ClassDataStartOffset = 0x6FC4;
	public static final String ClassDataFilename = "system.cmp/FE8Data.bin";
	public static final int ClassCount = 0x73;
	public static final int ClassDataSize = 0x64;
	
	public static final long ItemDataStartOffset = 0x9CB4;
	public static final String ItemDataFilename = "system.cmp/FE8Data.bin";
	public static final int ItemCount = 0xBD;
	public static final int ItemDataSize = 0x60;
	
	public static final long SkillDataStartOffset = 0xE398;
	public static final String SkillDataFilename = "system.cmp/FE8Data.bin";
	public static final int SkillCount = 0x62;
	public static final int SkillDataSize = 0x28;
	
	public static final long CommonTextDataStartOffset = 0x155CC;
	public static final long CommonTextIDStartOffset = 0x1A4DC;
	public static final String CommonTextFilename = "mess/common.m";
	public static final int CommonTextCount = 0x9E2;
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
		public static Set<Character> doNotChange = new HashSet<Character>(Arrays.asList());
		
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
				CAT_F, RED_DRAGON_F, W_HERON_F));
		
		public static Set<CharacterClass> allBeorcClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER, GREIL_HERO, MYRMIDON, SWORDMASTER, 
				SOLDIER, HALBERDIER, FIGHTER, WARRIOR, ARCHER, SNIPER, KNIGHT, GENERAL, SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_RIDER, WYVERN_LORD, FIRE_MAGE, WIND_MAGE, THUNDER_MAGE, MAGE,
				FIRE_SAGE, WIND_SAGE, THUNDER_SAGE, SAGE, FIRE_SAGE_STAFF, WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, FIRE_SAGE_KNIFE, WIND_SAGE_KNIFE,
				THUNDER_SAGE_KNIFE, PRIEST, BISHOP, THIEF, ASSASSIN, BANDIT, BERSERKER, KING_DAEIN, BLACK_KNIGHT, MYRMIDON_F, SWORDMASTER_F, SOLDIER_F, 
				HALBERDIER_F, SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F, BOW_KNIGHT_F, SWORD_PALADIN_F, LANCE_PALADIN_F, AXE_PALADIN_F, BOW_PALADIN_F, 
				TITANIA_PALADIN, PEGASUS_KNIGHT, FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT, WYVERN_RIDER_F, WYVERN_LORD_F, FIRE_MAGE_F, WIND_MAGE_F, THUNDER_MAGE_F, 
				MAGE_F, FIRE_SAGE_F, WIND_SAGE_F, THUNDER_SAGE_F, SAGE_F, BISHOP_F, CLERIC, VALKYRIE, ASSASSIN_F, SAGE_STAFF, SAGE_KNIFE, SAGE_STAFF_F, 
				SAGE_KNIFE_F));
		public static Set<CharacterClass> allLaguzClasses = new HashSet<CharacterClass>(Arrays.asList(LION, TIGER, CAT, BLACK_DRAGON, WHITE_DRAGON, RED_DRAGON,
				HAWK, CROW, HERON, W_HERON, FERAL_LION, FERAL_TIGER, FERAL_CAT, FERAL_BLACK_DRAGON, FERAL_WHITE_DRAGON,
				FERAL_RED_DRAGON, FERAL_HAWK, FERAL_CROW, FERAL_HERON, FERAL_W_HERON, TIBARN_HAWK, EVENT_TIBARN, 
				NAESALA_CROW, EVENT_NAESALA, CAT_F, RED_DRAGON_F, W_HERON_F, FERAL_CAT_F, FERAL_RED_DRAGON_F, FERAL_W_HERON_F));
		
		public static Set<CharacterClass> allPacifistClasses = new HashSet<CharacterClass>(Arrays.asList(HERON, W_HERON, W_HERON_F, PRIEST, CLERIC, 
				FERAL_HERON, FERAL_W_HERON, FERAL_W_HERON_F));
		
		public static Set<CharacterClass> allFlyingClasses = new HashSet<CharacterClass>(Arrays.asList(PEGASUS_KNIGHT, FALCON_KNIGHT, ELINCIA_FALCON_KNIGHT,
				WYVERN_RIDER, WYVERN_LORD, WYVERN_RIDER_F, WYVERN_LORD_F, KING_DAEIN, HERON, W_HERON, W_HERON_F, FERAL_HERON, FERAL_W_HERON, 
				FERAL_W_HERON_F, HAWK, CROW, TIBARN_HAWK, EVENT_TIBARN, NAESALA_CROW, EVENT_NAESALA));
		
		public static Set<CharacterClass> allValidClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER, MYRMIDON, SWORDMASTER, 
				SOLDIER, HALBERDIER, FIGHTER, WARRIOR, ARCHER, SNIPER, KNIGHT, GENERAL, SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_RIDER, WYVERN_LORD, FIRE_MAGE, WIND_MAGE, THUNDER_MAGE, MAGE,
				FIRE_SAGE, WIND_SAGE, THUNDER_SAGE, SAGE, FIRE_SAGE_STAFF, WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, FIRE_SAGE_KNIFE, WIND_SAGE_KNIFE,
				THUNDER_SAGE_KNIFE, PRIEST, BISHOP, THIEF, ASSASSIN, BANDIT, BERSERKER, TIGER, CAT, WHITE_DRAGON, RED_DRAGON,
				HAWK, CROW, HERON, W_HERON, FERAL_TIGER, FERAL_CAT, FERAL_WHITE_DRAGON,
				FERAL_RED_DRAGON, FERAL_HAWK, FERAL_CROW, SAGE_STAFF, SAGE_KNIFE));
		
		public static Set<CharacterClass> enemyOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(FERAL_LION, FERAL_TIGER, FERAL_CAT, FERAL_BLACK_DRAGON, 
				FERAL_WHITE_DRAGON, FERAL_RED_DRAGON, FERAL_HAWK, FERAL_CROW, FERAL_CAT_F, FERAL_RED_DRAGON_F, FERAL_W_HERON_F));
		
		public static Set<CharacterClass> physicalClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, RANGER, GREIL_HERO, MYRMIDON, SWORDMASTER, 
				SOLDIER, HALBERDIER, FIGHTER, WARRIOR, ARCHER, SNIPER, KNIGHT, GENERAL, SWORD_KNIGHT, LANCE_KNIGHT, AXE_KNIGHT, BOW_KNIGHT,
				SWORD_PALADIN, LANCE_PALADIN, AXE_PALADIN, BOW_PALADIN, WYVERN_RIDER, WYVERN_LORD, THIEF, ASSASSIN, BANDIT, BERSERKER, KING_DAEIN, BLACK_KNIGHT, 
				MYRMIDON_F, SWORDMASTER_F, SOLDIER_F, HALBERDIER_F, SWORD_KNIGHT_F, LANCE_KNIGHT_F, AXE_KNIGHT_F, BOW_KNIGHT_F, SWORD_PALADIN_F, LANCE_PALADIN_F, 
				AXE_PALADIN_F, BOW_PALADIN_F, TITANIA_PALADIN, PEGASUS_KNIGHT, FALCON_KNIGHT, WYVERN_RIDER_F, WYVERN_LORD_F, ASSASSIN_F));
		public static Set<CharacterClass> magicalClasses = new HashSet<CharacterClass>(Arrays.asList(FIRE_MAGE, WIND_MAGE, THUNDER_MAGE, MAGE,
				FIRE_SAGE, WIND_SAGE, THUNDER_SAGE, SAGE, FIRE_SAGE_STAFF, WIND_SAGE_STAFF, THUNDER_SAGE_STAFF, PRIEST, BISHOP, FIRE_MAGE_F, WIND_MAGE_F, 
				THUNDER_MAGE_F, MAGE_F, FIRE_SAGE_F, WIND_SAGE_F, THUNDER_SAGE_F, SAGE_F, BISHOP_F, SAGE_STAFF, SAGE_STAFF_F, HERON, W_HERON, W_HERON_F));
		public static Set<CharacterClass> hybridMagicalClasses = new HashSet<CharacterClass>(Arrays.asList(FIRE_SAGE_KNIFE, WIND_SAGE_KNIFE,
				THUNDER_SAGE_KNIFE, SAGE_KNIFE, SAGE_KNIFE_F));
		public static Set<CharacterClass> hybridPhysicalClasses = new HashSet<CharacterClass>(Arrays.asList(ELINCIA_FALCON_KNIGHT, CLERIC, VALKYRIE));
		
		public boolean isPhysicalClass() { return !magicalClasses.contains(this); }
		public boolean isMagicalClass() { return !physicalClasses.contains(this); }
		public boolean isHybridMagicalClass() { return hybridMagicalClasses.contains(this); }
		public boolean isHybridPhyiscalClass() { return hybridPhysicalClasses.contains(this); }
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
		EQUIP_A("SID_EQ_A"), EQUIP_B("SID_EQ_B"), EQUIP_C("SID_EQ_C"), EQUIP_D("SID_EQ_D"),
		
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
				NIHIL, WRATH, GUARD, MIRACLE, ADEPT, CORROSION, COUNTER, DAUNT, PROVOKE, SHADE, GAMBLE, PARITY, SMITE, BLOSSOM));
		public static Set<Skill> playerOnlySkills = new HashSet<Skill>(Arrays.asList(PARAGON, SAVIOR, PROVOKE, SHADE, GAMBLE, SMITE, BLOSSOM));
		public static Set<Skill> replaceableInvalidSkills = new HashSet<Skill>(Arrays.asList(REINFORCE, INSIGHT, VIGILANCE));
		
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
			return allValidSkills.contains(this);
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
		IID_SWIFT("IID_SWIFT"), IID_IMPREGNABLE("IID_IMPREGNABLE"), TEMPEST_SCROLL("IID_TEMPER"), SERENITY_SCROLL("IID_CALM"), CANTO_SCROLL("IID_CHANT"),
		BLOSSOM_SCROLL("IID_FRAC90"), REINFORCE_SCROLL("IID_REINFORCEMENTS"), INSIGHT_SCROLL("IID_TELEGNOSIS"), VIGILANCE_SCROLL("IID_BIGEAR"),
		;
		
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
				PARITY_SCROLL, SMITE_SCROLL, GUARD_SCROLL, ADEPT_SCROLL, RENEWAL_SCROLL));
		public static Set<Item> allConsumables = new HashSet<Item>(Arrays.asList(LAGUZ_STONE, LAGUZ_STONE_1, MASTER_SEAL, CHEST_KEY, DOOR_KEY, 
				VULNERARY, ELIXIR, PURE_WATER, ANTITOXIN, TORCH, COIN));
		public static Set<Item> allGems = new HashSet<Item>(Arrays.asList(WHITE_GEM, BLUE_GEM, RED_GEM));
		
		// These shouldn't be changed or given out.
		public static Set<Item> allRestrictedItems = new HashSet<Item>(Arrays.asList(PRACTICE_SWORD, REGAL_SWORD, ALONDITE, RAGNELL, GURGURANT, AMITI, ROLF_BOW,
				LION_CLAW, HAWK_KING_BEAK, CROW_KING_BEAK, SWORD_BAND, SOLDIER_BAND, FIGHTERS_BAND, ARCHER_BAND, KNIGHT_BAND, PALADIN_BAND, PEGASUS_BAND,
				WYVERN_BAND, MAGE_BAND, PRIEST_BAND, THIEF_BAND));
		
		public static Set<Item> allERankWeapons = new HashSet<Item>(Arrays.asList(SLIM_SWORD, IRON_SWORD, PRACTICE_SWORD, IRON_LANCE, SLIM_LANCE,
				JAVELIN, VENIN_LANCE, IRON_AXE, PRACTICE_AXE, HAND_AXE, STEEL_AXE, DEVIL_AXE, IRON_BOW, FIRE, THUNDER, WIND, HEAL));
		public static Set<Item> allDRankWeapons = new HashSet<Item>(Arrays.asList(VENIN_EDGE, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, LONGSWORD, STEEL_LANCE,
				KNIGHT_KILLER, HEAVY_SPEAR, VENIN_AXE, HAMMER, POLEAX, VENIN_BOW, LONGBOW, STEEL_BOW, ELFIRE, ELWIND, ELTHUNDER, LIGHT, MEND, TORCH_STAFF));
		public static Set<Item> allCRankWeapons = new HashSet<Item>(Arrays.asList(KILLING_EDGE, LAGUZSLAYER, STEEL_BLADE, SHORT_SPEAR, KILLER_LANCE,
				LAGUZ_LANCE, SHORT_AXE, KILLER_AXE, LAGUZ_AXE, LAGUZ_BOW, KILLER_BOW, METEOR, BOLTING, BLIZZARD, SHINE, WARD, RESTORE, PHYSIC, HAMMERNE));
		public static Set<Item> allBRankWeapons = new HashSet<Item>(Arrays.asList(BRAVE_SWORD, SONIC_SWORD, FLAME_LANCE, BRAVE_LANCE, SPEAR, BOLT_AXE,
				BRAVE_AXE, TOMAHAWK, BRAVE_BOW, BRIGHT_BOW, PURGE, SILENCE, SLEEP, RESCUE, RECOVER));
		public static Set<Item> allARankWeapons = new HashSet<Item>(Arrays.asList(SILVER_SWORD, SILVER_BLADE, RUNESWORD, SILVER_LANCE, SILVER_AXE,
				SILVER_BOW, BOLGANONE, THORON, TORNADO, NOSFERATU, WARP, FORTIFY));
		public static Set<Item> allSRankWeapons = new HashSet<Item>(Arrays.asList(VAGUE_KATTI, WISHBLADE, URVAN, DOUBLE_BOW, REXFLAME, REXBOLT, REXCALIBUR,
				REXAURA, ASHERA_STAFF));
		
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
			return result;
		}
		
		public boolean isSword() { return allSwords.contains(this); }
		public boolean isLance() { return allLances.contains(this); }
		public boolean isAxe() { return allAxes.contains(this); }
		public boolean isBow() { return allBows.contains(this); }
		public boolean isMagic() { return allFireMagic.contains(this) || allThunderMagic.contains(this) || allWindMagic.contains(this) || allLightMagic.contains(this); }
		public boolean isStaff() { return allStaves.contains(this); }
		
		public boolean isERank() { return allERankWeapons.contains(this); }
		public boolean isDRank() { return allDRankWeapons.contains(this); }
		public boolean isCRank() { return allCRankWeapons.contains(this); }
		public boolean isBRank() { return allBRankWeapons.contains(this); }
		public boolean isARank() { return allARankWeapons.contains(this); }
		public boolean isSRank() { return allSRankWeapons.contains(this); }
		
		public boolean isWeapon() { return isSword() || isLance() || isAxe() || isBow() || isMagic() || isStaff(); }
		
		public boolean isConsumable() { return allConsumables.contains(this); }
		public boolean isStatBooster() { return allStatBoosters.contains(this); }
		public boolean isTreasure() { return allGems.contains(this); }
		public boolean isSkillScroll() { return allSkillScrolls.contains(this); }
	}
	
	public enum Chapter {
		
		ALL_CHAPTERS("zmap/always/dispos.cmp", null), // Supposedly between-chapter units are loaded here? Only has a cross-difficulty definition.
		
		PROLOGUE("zmap/bmap01/dispos.cmp", "Scripts/C01.cmb"),
		CHAPTER_1("zmap/bmap02/dispos.cmp", "Scripts/C02.cmb"),
		CHAPTER_2("zmap/bmap03/dispos.cmp", "Scripts/C03.cmb"),
		CHAPTER_3("zmap/bmap04/dispos.cmp", "Scripts/C04.cmb"),
		CHAPTER_4("zmap/bmap05/dispos.cmp", "Scripts/C05.cmb"),
		CHAPTER_5("zmap/bmap06/dispos.cmp", "Scripts/C06.cmb"),
		CHAPTER_5_CUTSCENE("zmap/bmap06_2/dispos.cmp", null),
		CHAPTER_6("zmap/bmap07/dispos.cmp", "Scripts/C07.cmb"),
		CHAPTER_7("zmap/bmap08/dispos.cmp", "Scripts/C08.cmb"),
		CHAPTER_7_CUTSCENE("zmap/bmap08_2/dispos.cmp", null),
		CHAPTER_8("zmap/bmap09/dispos.cmp", "Scripts/C09.cmb"),
		CHAPTER_8_CUTSCENE("zmap/bmap09_2/dispos.cmp", null),
		CHAPTER_8_CUTSCENE_2("zmap/bmap09_3/dispos.cmp", null),
		CHAPTER_8_CUTSCENE_3("zmap/bmap09_4/dispos.cmp", null),
		CHAPTER_8_CUTSCENE_4("zmap/bmap09_5/dispos.cmp", null),
		CHAPTER_9("zmap/bmap10/dispos.cmp", "Scripts/C10.cmb"),
		CHAPTER_10("zmap/bmap11/dispos.cmp", "Scripts/C11.cmb"),
		CHAPTER_11("zmap/bmap12/dispos.cmp", "Scripts/C12.cmb"),
		CHAPTER_11_CUTSCENE("zmap/bmap12_2/dispos.cmp", null),
		CHAPTER_12("zmap/bmap13/dispos.cmp", "Scripts/C13.cmb"),
		CHAPTER_13("zmap/bmap14/dispos.cmp", "Scripts/C14.cmb"),
		CHAPTER_13_CUTSCENE("zmap/bmap14_2/dispos.cmp", null),
		CHAPTER_14("zmap/bmap15/dispos.cmp", "Scripts/C15.cmb"),
		CHAPTER_15("zmap/bmap16/dispos.cmp", "Scripts/C16.cmb"),
		CHAPTER_16("zmap/bmap17/dispos.cmp", "Scripts/C17.cmb"),
		CHAPTER_17("zmap/bmap18/dispos.cmp", "Scripts/C18.cmb"), // All of it.
		CHAPTER_17_CUTSCENE("zmap/bmap18_3/dispos.cmp", null), // There's a bmap18_2, but that one only has a map.cmp.
		CHAPTER_18("zmap/bmap19/dispos.cmp", "Scripts/C19.cmb"),
		CHAPTER_19("zmap/bmap20/dispos.cmp", "Scripts/C20.cmb"),
		CHAPTER_20("zmap/bmap21/dispos.cmp", "Scripts/C21.cmb"),
		CHAPTER_21("zmap/bmap22/dispos.cmp", "Scripts/C22.cmb"),
		CHAPTER_22("zmap/bmap23/dispos.cmp", "Scripts/C23.cmb"),
		CHAPTER_23("zmap/bmap24/dispos.cmp", "Scripts/C24.cmb"),
		CHAPTER_24("zmap/bmap25/dispos.cmp", "Scripts/C25.cmb"),
		CHAPTER_24_CUTSCENE("zmap/bmap25_2/dispos.cmp", null),
		CHAPTER_25("zmap/bmap26/dispos.cmp", "Scripts/C26.cmb"),
		CHAPTER_25_CUTSCENE("zmap/bmap26_2/dispos.cmp", null),
		CHAPTER_26("zmap/bmap27/dispos.cmp", "Scripts/C27.cmb"),
		CHAPTER_27("zmap/bmap28/dispos.cmp", "Scripts/C28.cmb"),
		CHAPTER_27_BK_FIGHT("zmap/bmap29/dispos.cmp", "Scripts/C29.cmb"), // This is its own, for some reason.
		CHAPTER_28("zmap/bmap30/dispos.cmp", "Scripts/C30.cmb"),
		ENDGAME("zmap/bmap31/dispos.cmp", "Scripts/C31.cmb"),
		ENDGAME_CUTSCENE("zmap/bmap31_2/dispos.cmp", null),
		ENDGAME_CUTSCENE_2("zmap/bmap31_3/dispos.cmp", null),
		
		MAP_6("zmap/Map6/dispos.cmp", null), // Not sure what this is... debug map? 
		T1("zmap/T1/dispos.cmp", null), // Only has cross-difficulty. Cutscene?
		T2("zmap/T2/dispos.cmp", null), // See above.
		T3("zmap/T3/dispos.cmp", null), // Actually, I think these are tutorial chapters.
		
		// Trial maps. These also only have one difficulty (cross-difficulty)
		TRIAL_MAP_1("zmap/trial_01/dispos.cmp", null),
		TRIAL_MAP_2("zmap/trial_02/dispos.cmp", null),
		TRIAL_MAP_3("zmap/trial_03/dispos.cmp", null),
		TRIAL_MAP_4("zmap/trial_04/dispos.cmp", null),
		TRIAL_MAP_5("zmap/trial_05/dispos.cmp", null),
		TRIAL_MAP_6("zmap/trial_06/dispos.cmp", null)
		;
		
		private String path;
		private String scriptPath;
		
		private Chapter(String path, String scriptPath) {
			this.path = path;
			this.scriptPath = scriptPath;
		}
		
		public String getPath() {
			return path;
		}
		
		public String getScriptPath() {
			return scriptPath;
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
	}
}
