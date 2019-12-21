package fedata.gcnwii.fe9;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	public enum Character {
		
		NONE(null),
		
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
}
