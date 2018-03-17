package fedata.fe7;

import java.util.HashMap;
import java.util.Map;

public class FE7Data {

	public static final String FriendlyName = "Fire Emblem: Blazing Sword";
	public static final String GameCode = "AE7E";

	public static final long CleanCRC32 = 0x2A524221;
	public static final long CleanSize = 16777216;
	
	public static final int NumberOfCharacters = 254;
	public static final int BytesPerCharacter = 52;
	public static final int DefaultCharacterTableAddress = 0xBDCE18;
	
	public static final int NumberOfClasses = 99;
	public static final int BytesPerClass = 84;
	public static final int DefaultClassTableAddress = 0xBE015C;
	
	public static final int HuffmanTreeStart = 0x6BC;
	public static final int HuffmanTreeEnd = 0x6B8;
	public static final int DefaultTextArrayOffset = 0xB808AC;
	public static final int NumberOfTextStrings = 0x133E;
	
	public enum Character {
		NONE(0x00),
		
		ELIWOOD(0x01), HECTOR(0x02), RAVEN(0x04), GEITZ(0x05), GUY(0x06), KAREL(0x07), DORCAS(0x08), BARTRE(0x09), OSWIN(0x0B), REBECCA(0x0E), LOUISE(0x0F), LUCIUS(0x10), SERRA(0x11), 
		RENAULT(0x12), ERK(0x13), NINO(0x14), PENT(0x15), CANAS(0x16), LOWEN(0x19), MARCUS(0x1A), PRISCILLA(0x1B), FIORA(0x1E), FARINA(0x1F), HEATH(0x20), VAIDA(0x21), HAWKEYE(0x22), 
		MATTHEW(0x23), JAFFAR(0x24), NINIAN(0x25), NILS(0x26),  ATHOS(0x27), WALLACE(0x2C), LYN(0x2D), WIL(0x2E), KENT(0x2F), SAIN(0x30), FLORINA(0x31), RATH(0x32), DART(0x33),
		ISADORA(0x34), LEGAULT(0x36), KARLA(0x37), HARKEN(0x38),
		
		FARGUS(0x0C), MERLINUS(0x28), UTHER(0x2A), VAIDA_BOSS(0x2B), ELENORA(0x35), LEILA(0x39), BRAMIMOND(0x3A), ZEPHIEL(0x7A), ELBERT(0x7B), BRENDAN(0x84), NATALIE(0x9E), TACTICIAN(0xCD),
		
		LYN_TUTORIAL(0x03), WIL_TUTORIAL(0x0D), KENT_TUTORIAL(0x17), SAIN_TUTORIAL(0x18), RATH_TUTORIAL(0x1C), FLORINA_TUTORIAL(0x1D),
		
		NILS_FINALCHAPTER(0x29),
		
		GROZNYI(0x3C), WIRE(0x3D), ZAGAN(0x3F), BOIES(0x40), PUZON(0x41), SANTALS(0x43), ERIK(0x45), SEALEN(0x46), BAUKER(0x47), BERNARD(0x48), DAMIAN(0x49),
		ZOLDAM(0x4A), UHAI(0x4B), AION(0x4C), DARIN(0x4D), CAMERON(0x4E), OLEG(0x4F), EUBANS(0x50), URSULA(0x51), PAUL(0x53), JASMINE(0x54), PASCAL(0x57), KENNETH(0x58), JERME(0x59),
		MAXIME(0x5A), SONIA(0x5B), TEODOR(0x5C), GEORG(0x5D), KAIM(0x5E), DENNING(0x60), LIMSTELLA(0x85), BATTA(0x87), ZUGU(0x89), GLASS(0x8D), MIGAL(0x8E), CARJIGA(0x94), BUG(0x99),
		BOOL(0x9F), HEINTZ(0xA6), BEYARD(0xAD), YOGI(0xB6), EAGLER(0xBE), LUNDGREN(0xC5),
		
		LLOYD_FFO(0x63), LINUS_FFO(0x64), LLOYD_COD(0x65), LINUS_COD(0x66),
		
		JERME_MORPH(0x56), LLOYD_MORPH(0xF4), LINUS_MORPH(0xF5), BRENDAN_MORPH(0xF6), UHAI_MORPH(0xF7), URSULA_MORPH(0xF8), KENNETH_MORPH(0xF9), DARIN_MORPH(0xFA),
		
		KISHUNA(0x3B), NERGAL(0x44), DRAGON(0x86);
		
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
		
		public static long dataOffsetForCharacter(Character character) {
			return DefaultCharacterTableAddress + (character.ID * BytesPerCharacter);
		}
		
		public static int[] characterIDsForCharacters(Character[] charArray) {
			int[] idArray = new int[charArray.length];
			for (int i = 0; i < charArray.length; i++) {
				idArray[i] = charArray[i].ID;
			}
			
			return idArray;
		}
		
		public static Character[] allPlayableCharacters = new Character[] {ELIWOOD, HECTOR, RAVEN, GEITZ, GUY, KAREL,  DORCAS, BARTRE, OSWIN, REBECCA, LOUISE, LUCIUS, SERRA, RENAULT, 
				ERK, NINO, PENT, CANAS, LOWEN, MARCUS, PRISCILLA, FIORA, FARINA, HEATH, VAIDA, HAWKEYE, MATTHEW, JAFFAR, NINIAN, NILS, WALLACE, LYN, WIL, KENT, SAIN, FLORINA, 
				RATH, DART, ISADORA, LEGAULT, KARLA, HARKEN, LYN_TUTORIAL, WIL_TUTORIAL, KENT_TUTORIAL, SAIN_TUTORIAL, RATH_TUTORIAL, FLORINA_TUTORIAL, NILS_FINALCHAPTER};
		
		public static Character[] allBossCharacters = new Character[] {GROZNYI, WIRE, ZAGAN, BOIES, PUZON, SANTALS, ERIK, SEALEN, BAUKER, BERNARD, DAMIAN, ZOLDAM, UHAI,
				AION, DARIN, CAMERON, OLEG, EUBANS, URSULA, PAUL, JASMINE, PASCAL, KENNETH, JERME, MAXIME, SONIA, TEODOR, GEORG, KAIM, DENNING, LIMSTELLA, BATTA, ZUGU, GLASS, MIGAL, CARJIGA,
				BUG, BOOL, HEINTZ, BEYARD, YOGI, EAGLER, LUNDGREN, LLOYD_FFO, LINUS_FFO, LLOYD_COD, LINUS_COD, JERME_MORPH, LLOYD_MORPH, LINUS_MORPH, BRENDAN_MORPH, UHAI_MORPH, URSULA_MORPH,
				KENNETH_MORPH, DARIN_MORPH};
		
		public static Character[] allLords = new Character[] {ELIWOOD, HECTOR, LYN, LYN_TUTORIAL};
		public static Character[] allThieves = new Character[] {MATTHEW, LEGAULT, JAFFAR};
		
		public static Character[] allLinkedCharactersFor(Character character) {
			switch (character) {
			case LYN:
			case LYN_TUTORIAL:
				return new Character[] {LYN, LYN_TUTORIAL};
			case WIL:
			case WIL_TUTORIAL:
				return new Character[] {WIL, WIL_TUTORIAL};
			case KENT:
			case KENT_TUTORIAL:
				return new Character[] {KENT, KENT_TUTORIAL};
			case SAIN:
			case SAIN_TUTORIAL:
				return new Character[] {SAIN, SAIN_TUTORIAL};
			case RATH:
			case RATH_TUTORIAL:
				return new Character[] {RATH, RATH_TUTORIAL};
			case FLORINA:
			case FLORINA_TUTORIAL:
				return new Character[] {FLORINA, FLORINA_TUTORIAL};
			case NILS:
			case NILS_FINALCHAPTER:
				return new Character[] {NILS, NILS_FINALCHAPTER};
			case VAIDA:
			case VAIDA_BOSS:
				return new Character[] {VAIDA, VAIDA_BOSS};
			case LLOYD_FFO:
			case LLOYD_COD:
			case LLOYD_MORPH:
				return new Character[] {LLOYD_FFO, LLOYD_COD, LLOYD_MORPH};
			case LINUS_FFO:
			case LINUS_COD:
			case LINUS_MORPH:
				return new Character[] {LINUS_FFO, LINUS_COD, LINUS_MORPH};
			case JERME:
			case JERME_MORPH:
				return new Character[] {JERME, JERME_MORPH};
			case KENNETH:
			case KENNETH_MORPH:
				return new Character[] {KENNETH, KENNETH_MORPH};
			case UHAI:
			case UHAI_MORPH:
				return new Character[] {UHAI, UHAI_MORPH};
			case BRENDAN:
			case BRENDAN_MORPH:
				return new Character[] {BRENDAN, BRENDAN_MORPH};
			case URSULA:
			case URSULA_MORPH:
				return new Character[] {URSULA, URSULA_MORPH};
			case DARIN:
			case DARIN_MORPH:
				return new Character[] {DARIN, DARIN_MORPH};
			default:
				return new Character[] {character};
			}
		}
	}
	
	public enum CharacterClass {
		NONE(0x00),
		
		LORD_ELIWOOD(0x01), LORD_LYN(0x02), LORD_HECTOR(0x03),
		
		LORD_KNIGHT(0x07), BLADE_LORD(0x08), GREAT_LORD(0x09),
		
		MERCENARY(0x0A), MYRMIDON(0x0E), FIGHTER(0x12), KNIGHT(0x14), ARCHER(0x18), MONK(0x1C), MAGE(0x20), SHAMAN(0x24), CAVALIER(0x28), NOMAD(0x2E), WYVERNKNIGHT(0x34), SOLDIER(0x38),
		BRIGAND(0x39), PIRATE(0x3A), THIEF(0x3C), BARD(0x41), CORSAIR(0x50),
		
		ARCHER_F(0x19), CLERIC(0x1D), MAGE_F(0x21), TROUBADOUR(0x2C), PEGASUSKNIGHT(0x32), DANCER(0x40),
		
		MERCENARY_F(0x0B), // Doesn't exist naturally. May not work.
		MYRMIDON_F(0x0F), // Doesn't exist naturally. May not work.
		KNIGHT_F(0x15), // Doesn't exist naturally. May not work.
		SHAMAN_F(0x25), // Doesn't exist naturally. May not work.
		CAVALIER_F(0x29), // Doesn't exist naturally. May not work.
		NOMAD_F(0x2F), // Doesn't exist naturally. May not work.
		WYVERNKNIGHT_F(0x35), // Doesn't exist naturally. May not work.
		THIEF_F(0x3D), // Leila?
		
		HERO(0x0C), SWORDMASTER(0x10), WARRIOR(0x13), GENERAL(0x16), SNIPER(0x1A), BISHOP(0x1E), SAGE(0x22), DRUID(0x26), PALADIN(0x2A), NOMADTROOPER(0x30), WYVERNLORD(0x36), 
		BERSERKER(0x3B), ASSASSIN(0x3E),
		
		SWORDMASTER_F(0x11), SNIPER_F(0x1B), BISHOP_F(0x1F), SAGE_F(0x23), PALADIN_F(0x2B), VALKYRIE(0x2D), FALCONKNIGHT(0x33), WYVERNLORD_F(0x37), 
		
		HERO_F(0x0D), // Doesn't exist naturally. May not work.
		GENERAL_F(0x17), // Doesn't exist naturally. May not work.
		DRUID_F(0x27), // Doesn't exist naturally. May not work.
		NOMADTROOPER_F(0x31); // Doesn't exist naturally. May not work.
		
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
		
		public static long dataOffsetForClass(CharacterClass charClass) {
			return DefaultClassTableAddress + (charClass.ID * BytesPerClass);
		}
		
		public static int[] classIDsForClassArray(CharacterClass[] classArray) {
			int[] idArray = new int[classArray.length];
			for (int i = 0; i < classArray.length; i++) {
				idArray[i] = classArray[i].ID;
			}
			
			return idArray;
		}
		
		public static CharacterClass[] allMaleClasses = new CharacterClass[] {LORD_ELIWOOD, LORD_HECTOR, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, MONK, MAGE, SHAMAN, CAVALIER, NOMAD,
				WYVERNKNIGHT, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, CORSAIR, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID, PALADIN, NOMADTROOPER, WYVERNLORD,
				BERSERKER, ASSASSIN};
		public static CharacterClass[] allFemaleClasses = new CharacterClass[] {LORD_LYN, BLADE_LORD, ARCHER_F, CLERIC, MAGE_F, TROUBADOUR, PEGASUSKNIGHT, DANCER, SWORDMASTER_F, SNIPER_F,
				BISHOP_F, SAGE_F, PALADIN_F, VALKYRIE, FALCONKNIGHT, WYVERNLORD_F};
		public static CharacterClass[] allLordClasses = new CharacterClass[] {LORD_ELIWOOD, LORD_LYN, LORD_HECTOR, LORD_KNIGHT, BLADE_LORD, GREAT_LORD};
		public static CharacterClass[] allThiefClasses = new CharacterClass[] {THIEF, ASSASSIN};
		public static CharacterClass[] allUnpromotedClasses = new CharacterClass[] {LORD_ELIWOOD, LORD_LYN, LORD_HECTOR, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, MONK, MAGE, SHAMAN,
				CAVALIER, NOMAD, WYVERNKNIGHT, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, CORSAIR, ARCHER_F, CLERIC, MAGE_F, TROUBADOUR, PEGASUSKNIGHT, DANCER};
		public static CharacterClass[] allPromotedClasses = new CharacterClass[] {LORD_KNIGHT, BLADE_LORD, GREAT_LORD, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID,
				PALADIN, NOMADTROOPER, WYVERNLORD, BERSERKER, ASSASSIN, SWORDMASTER_F, SNIPER_F, BISHOP_F, SAGE_F, PALADIN_F, VALKYRIE, FALCONKNIGHT, WYVERNLORD_F};
	}
	
	public enum Item {
		NONE(0x00),
		
		IRON_SWORD(0x01), SLIM_SWORD(0x02), STEEL_SWORD(0x03), SILVER_SWORD(0x04), IRON_BLADE(0x05), STEEL_BLADE(0x06), SILVER_BLADE(0x07), POISON_SWORD(0x08), RAPIER(0x09), MANI_KATTI(0x0A),
		BRAVE_SWORD(0x0B), WO_DAO(0x0C), KILLING_EDGE(0x0D), ARMORSLAYER(0x0E), WYRMSLAYER(0x0F), LIGHT_BRAND(0x10),  RUNE_SWORD(0x11),  LANCEREAVER(0x12), LONGSWORD(0x13), EMBLEM_SWORD(0x80),
		DURANDAL(0x84), SOL_KATTI(0x8C), REGAL_BLADE(0x90), WIND_SWORD(0x99),    
		 
		IRON_LANCE(0x14), SLIM_LANCE(0x15), STEEL_LANCE(0x16), SILVER_LANCE(0x17), POISON_LANCE(0x18), BRAVE_LANCE(0x19), KILLER_LANCE(0x1A), HORSESLAYER(0x1B), JAVELIN(0x1C), SPEAR(0x1D),
		AXEREAVER(0x1E), EMBLEM_LANCE(0x81), REX_HASTA(0x91), HEAVY_SPEAR(0x94), SHORT_SPEAR(0x95),   
		
		IRON_AXE(0x1F), STEEL_AXE(0x20), SILVER_AXE(0x21), POISON_AXE(0x22), BRAVE_AXE(0x23), KILLER_AXE(0x24), HALBERD(0x25), HAMMER(0x26), DEVIL_AXE(0x27), HAND_AXE(0x28), TOMAHAWK(0x29),
		SWORDREAVER(0x2A), SWORDSLAYER(0x2B), DRAGON_AXE(0x59), EMBLEM_AXE(0x82), ARMADS(0x85), WOLF_BEIL(0x8D), BASILIKOS(0x92),     
		
		IRON_BOW(0x2C), STEEL_BOW(0x2D), SILVER_BOW(0x2E), POISON_BOW(0x2F), KILLER_BOW(0x30), BRAVE_BOW(0x31), SHORT_BOW(0x32), LONGBOW(0x33), EMBLEM_BOW(0x83), RIENFLECHE(0x93), 
		
		FIRE(0x37), THUNDER(0x38), ELFIRE(0x39), BOLTING(0x3A), FIMBULVETR(0x3B), FORBLAZE(0x3C), EXCALIBUR(0x3D),
		
		LIGHTNING(0x3E), SHINE(0x3F), DIVINE(0x40), PURGE(0x41), AURA(0x42), LUCE(0x43), AUREOLA(0x86),      
		
		FLUX(0x44), LUNA(0x45), NOSFERATU(0x46), ECLIPSE(0x47), FENRIR(0x48), GESPENST(0x49),       
		
		HEAL(0x4A), MEND(0x4B), RECOVER(0x4C), PHYSIC(0x4D), FORTIFY(0x4E), RESTORE(0x4F), SILENCE(0x50), SLEEP(0x51), BERSERK(0x52), WARP(0x53), RESCUE(0x54), TORCH_STAFF(0x55),
		HAMMERNE(0x56), UNLOCK(0x57), BARRIER(0x58),  
		 
		ANGELIC_ROBE(0x5A), ENERGY_RING(0x5B), SECRET_BOOK(0x5C), SPEEDWINGS(0x5D), GODDESS_ICON(0x5E), DRAGONSHIELD(0x5F), TALISMAN(0x60), BOOTS(0x61), BODY_RING(0x62),
		
		HERO_CREST(0x63), KNIGHT_CREST(0x64), ORION_BOLT(0x65), ELYSIAN_WHIP(0x66), GUIDING_RING(0x67), EARTH_SEAL(0x87), HEAVEN_SEAL(0x89), EMBLEM_SEAL(0x8A), FELL_CONTRACT(0x8B), 
		OCEAN_SEAL(0x96), AFA_DROPS(0x88),
		
		CHEST_KEY(0x68), CHEST_KEY_5(0x78), DOOR_KEY(0x69), LOCKPICK(0x6A), VULNERARY(0x6B), ELIXIR(0x6C), PURE_WATER(0x6D), ANTITOXIN(0x6E), TORCH(0x6F), MINE(0x79), LIGHT_RUNE(0x7A), 

		DELPHI_SHIELD(0x70), MEMBER_CARD(0x71), IRON_RUNE(0x7B), SILVER_CARD(0x72),  
		
		WHITE_GEM(0x73), BLUE_GEM(0x74), RED_GEM(0x75),  
		
		UBER_SPEAR(0x77), ERESHKIGAL(0x8E), FLAMETONGUE(0x8F),
		
		FILLA_MIGHT(0x7C), NINI_GRACE(0x7D), THOR_IRE(0x7E), SET_LITANY(0x7F), 
		
		GOLD_3000(0x97), GOLD_5000(0x98);
		
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
		
		public static Item[] allSwords = new Item[] {IRON_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, POISON_SWORD, RAPIER, MANI_KATTI, BRAVE_SWORD,
				WO_DAO, KILLING_EDGE, ARMORSLAYER, WYRMSLAYER, LIGHT_BRAND, RUNE_SWORD, LANCEREAVER, LONGSWORD, EMBLEM_SWORD, DURANDAL, SOL_KATTI, REGAL_BLADE, WIND_SWORD};
		public static Item[] allLances = new Item[] {IRON_LANCE, SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, POISON_LANCE, BRAVE_LANCE, KILLER_LANCE, HORSESLAYER, JAVELIN, SPEAR, AXEREAVER,
				EMBLEM_LANCE, REX_HASTA, HEAVY_SPEAR, SHORT_SPEAR, UBER_SPEAR};
		public static Item[] allAxes = new Item[] {IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, KILLER_AXE, HALBERD, HAMMER, DEVIL_AXE, HAND_AXE, TOMAHAWK, SWORDREAVER,
				SWORDSLAYER, DRAGON_AXE, EMBLEM_AXE, ARMADS, WOLF_BEIL, BASILIKOS};
		public static Item[] allBows = new Item[] {IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, EMBLEM_BOW, RIENFLECHE};
		public static Item[] allAnima = new Item[] {FIRE, THUNDER, ELFIRE, BOLTING, FIMBULVETR, FORBLAZE, EXCALIBUR};
		public static Item[] allLight = new Item[] {LIGHTNING, SHINE, DIVINE, PURGE, AURA, LUCE, AUREOLA};
		public static Item[] allDark = new Item[] {FLUX, LUNA, NOSFERATU, ECLIPSE, FENRIR, GESPENST};
		public static Item[] allHealingStaves = new Item[] {HEAL, MEND, RECOVER, PHYSIC, FORTIFY};
		public static Item[] allSupportStaves = new Item[] {RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER};
		public static Item[] allStatusStaves = new Item[] {SILENCE, SLEEP, BERSERK};
		public static Item[] allStatBoosters = new Item[] {ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWINGS, GODDESS_ICON, DRAGONSHIELD, TALISMAN, BOOTS, BODY_RING, AFA_DROPS};
		public static Item[] allPromotionItems = new Item[] {HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP, GUIDING_RING, EARTH_SEAL, HEAVEN_SEAL, EMBLEM_SEAL, FELL_CONTRACT, OCEAN_SEAL};
		public static Item[] allDancingRings = new Item[] {FILLA_MIGHT, NINI_GRACE, THOR_IRE, SET_LITANY};
		public static Item[] allSpecialItems = new Item[] {DELPHI_SHIELD, MEMBER_CARD, IRON_RUNE, SILVER_CARD};
		public static Item[] allMoneyItems = new Item[] {WHITE_GEM, BLUE_GEM, RED_GEM, GOLD_3000, GOLD_5000};
		public static Item[] chestOnlyItems = new Item[] {GOLD_3000, GOLD_5000};
		public static Item[] usableItems = new Item[] {CHEST_KEY, CHEST_KEY_5, DOOR_KEY, LOCKPICK, VULNERARY, ELIXIR, PURE_WATER, ANTITOXIN, TORCH, MINE, LIGHT_RUNE};
		
		public static Item[] allWeapons = {IRON_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, POISON_SWORD, RAPIER, MANI_KATTI, BRAVE_SWORD,
				WO_DAO, KILLING_EDGE, ARMORSLAYER, WYRMSLAYER, LIGHT_BRAND, RUNE_SWORD, LANCEREAVER, LONGSWORD, EMBLEM_SWORD, DURANDAL, SOL_KATTI, REGAL_BLADE, WIND_SWORD, IRON_LANCE, 
				SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, POISON_LANCE, BRAVE_LANCE, KILLER_LANCE, HORSESLAYER, JAVELIN, SPEAR, AXEREAVER, EMBLEM_LANCE, REX_HASTA, HEAVY_SPEAR, SHORT_SPEAR, 
				IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, KILLER_AXE, HALBERD, HAMMER, DEVIL_AXE, HAND_AXE, TOMAHAWK, SWORDREAVER, SWORDSLAYER, DRAGON_AXE, EMBLEM_AXE, ARMADS, 
				WOLF_BEIL, BASILIKOS, IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, EMBLEM_BOW, RIENFLECHE, FIRE, THUNDER, ELFIRE, BOLTING, 
				FIMBULVETR, FORBLAZE, EXCALIBUR, LIGHTNING, SHINE, DIVINE, PURGE, AURA, LUCE, AUREOLA, FLUX, LUNA, NOSFERATU, ECLIPSE, FENRIR, GESPENST};
		public static Item[] allStaves = {HEAL, MEND, RECOVER, PHYSIC, FORTIFY, RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER, SILENCE, SLEEP, BERSERK};
		public static Item[] allERank = {IRON_SWORD, SLIM_SWORD, EMBLEM_SWORD, IRON_LANCE, SLIM_LANCE, JAVELIN, EMBLEM_LANCE, POISON_LANCE, HAND_AXE, IRON_AXE, EMBLEM_AXE, STEEL_AXE,
				DEVIL_AXE, IRON_BOW, EMBLEM_BOW, FIRE, LIGHTNING, HEAL};
		public static Item[] allDRank = {POISON_SWORD, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, LONGSWORD, WO_DAO, STEEL_LANCE, HEAVY_SPEAR, HORSESLAYER, POISON_AXE, HALBERD, HAMMER, POISON_BOW,
				SHORT_BOW, LONGBOW, STEEL_BOW, THUNDER, SHINE, FLUX, MEND, TORCH_STAFF, UNLOCK};
		public static Item[] allCRank = {STEEL_BLADE, KILLING_EDGE, WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, SHORT_SPEAR, KILLER_LANCE, AXEREAVER, DRAGON_AXE, KILLER_AXE, SWORDREAVER, 
				SWORDSLAYER, KILLER_BOW, ELFIRE, DIVINE, NOSFERATU, RECOVER, RESTORE, HAMMERNE, BARRIER};
		public static Item[] allBRank = {BRAVE_SWORD, WIND_SWORD, BRAVE_LANCE, SPEAR, UBER_SPEAR, BRAVE_AXE, BRAVE_BOW, BOLTING, PURGE, ECLIPSE, PHYSIC, SILENCE, SLEEP, BERSERK, RESCUE};
		public static Item[] allARank = {SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, SILVER_LANCE, TOMAHAWK, SILVER_AXE, SILVER_BOW, FIMBULVETR, AURA, FENRIR, FORTIFY, WARP};
		public static Item[] allSRank = {REGAL_BLADE, REX_HASTA, BASILIKOS, RIENFLECHE, EXCALIBUR, LUCE, GESPENST};
		public static Item[] allPrfRank = {MANI_KATTI, RAPIER, DURANDAL, SOL_KATTI, WOLF_BEIL, ARMADS, FORBLAZE};
	}
}
