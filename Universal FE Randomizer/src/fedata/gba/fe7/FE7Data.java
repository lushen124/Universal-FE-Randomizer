package fedata.gba.fe7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFESpellAnimationCollection;
import fedata.gba.general.GBAFECharacter;
import fedata.gba.general.GBAFECharacterProvider;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEClassProvider;
import fedata.gba.general.GBAFEItem;
import fedata.gba.general.GBAFEItemProvider;
import fedata.gba.general.GBAFEPromotionItem;
import fedata.gba.general.PaletteColor;
import fedata.gba.general.PaletteInfo;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import util.WhyDoesJavaNotHaveThese;

public class FE7Data implements GBAFECharacterProvider, GBAFEClassProvider, GBAFEItemProvider {

	public static final String FriendlyName = "Fire Emblem: Blazing Sword";
	public static final String GameCode = "AE7E";

	public static final long CleanCRC32 = 0x2A524221;
	public static final long CleanSize = 16777216;
	
	public static final int NumberOfCharacters = 254;
	public static final int BytesPerCharacter = 52;
	public static final long CharacterTablePointer = 0x17890;
	//public static final long DefaultCharacterTableAddress = 0xBDCE18; 
	
	public static final int NumberOfClasses = 99;
	public static final int BytesPerClass = 84;
	public static final long ClassTablePointer = 0x178F0;
	//public static final long DefaultClassTableAddress = 0xBE015C;
	
	public static final int NumberOfItems = 159;
	public static final int BytesPerItem = 36;
	public static final long ItemTablePointer = 0x16060;
	//public static final long DefaultItemTableAddress = 0xBE222C;
	
	public static final int NumberOfSpellAnimations = 127;
	public static final int BytesPerSpellAnimation = 16;
	public static final long SpellAnimationTablePointer = 0x52B24;
	//public static final long DefaultSpellAnimationTableOffset = 0xC999C0;
	
	public static final int HuffmanTreeStart = 0x6BC; // Resolved once
	public static final int HuffmanTreeEnd = 0x6B8; // Resolved twice
	public static final long TextTablePointer = 0x12CB8;
	//public static final long DefaultTextArrayOffset = 0xB808AC;
	public static final int NumberOfTextStrings = 0x133E;
	
	public static final long ChapterTablePointer = 0x191C8;
	//public static final long DefaultChapterArrayOffset = 0xC9C9C8;
	public static final int BytesPerChapterUnit = 16;
	
	public static final long PromotionItemTablePointer = 0x27428; // Accounts for most of them, except for ocean seal.
	
	public static final int OceanSealAddressPointer = 0x27574; // ???
	public static final int OceanSealDefaultAddress = 0xC97F24;
	
	private static final FE7Data sharedInstance = new FE7Data();
	
	public static final GBAFECharacterProvider characterProvider = sharedInstance;
	public static final GBAFEClassProvider classProvider = sharedInstance;
	
	public enum Character implements GBAFECharacter {
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
		
		public static int[] characterIDsForCharacters(Character[] charArray) {
			int[] idArray = new int[charArray.length];
			for (int i = 0; i < charArray.length; i++) {
				idArray[i] = charArray[i].ID;
			}
			
			return idArray;
		}
		
		@Override
		public int getID() {
			return ID;
		}
		
		public static Comparator<Character> characterIDComparator() {
			return new Comparator<Character>() { public int compare(Character o1, Character o2) { return Integer.compare(o1.ID, o2.ID); } };
		}
		
		public static int canonicalIDForCharacterID(int characterID) {
			Character character = valueOf(characterID);
			if (character == null) { return 0; }
			
			switch (character) {
			case LYN_TUTORIAL: return LYN.ID;
			case SAIN_TUTORIAL: return SAIN.ID;
			case KENT_TUTORIAL: return KENT.ID;
			case WIL_TUTORIAL: return WIL.ID;
			case FLORINA_TUTORIAL: return FLORINA.ID;
			case RATH_TUTORIAL: return RATH.ID;
			case LINUS_COD:
			case LINUS_MORPH: return LINUS_FFO.ID;
			case LLOYD_COD:
			case LLOYD_MORPH: return LLOYD_FFO.ID;
			case BRENDAN_MORPH: return BRENDAN.ID;
			case UHAI_MORPH: return UHAI.ID;
			case DARIN_MORPH: return DARIN.ID;
			case URSULA_MORPH: return URSULA.ID;
			case NILS_FINALCHAPTER: return NILS.ID;
			case VAIDA_BOSS: return VAIDA.ID;
			case KENNETH_MORPH: return KENNETH.ID;
			case JERME_MORPH: return JERME.ID;
			default: return characterID;
			}
		}
		
		public static Set<Character> allPlayableCharacters = new HashSet<Character>(Arrays.asList(ELIWOOD, HECTOR, RAVEN, GEITZ, GUY, KAREL,  DORCAS, BARTRE, OSWIN, REBECCA, LOUISE, LUCIUS, SERRA, RENAULT, 
				ERK, NINO, PENT, CANAS, LOWEN, MARCUS, PRISCILLA, FIORA, FARINA, HEATH, VAIDA, HAWKEYE, MATTHEW, JAFFAR, NINIAN, NILS, WALLACE, LYN, WIL, KENT, SAIN, FLORINA, 
				RATH, DART, ISADORA, LEGAULT, KARLA, HARKEN, LYN_TUTORIAL, WIL_TUTORIAL, KENT_TUTORIAL, SAIN_TUTORIAL, RATH_TUTORIAL, FLORINA_TUTORIAL, NILS_FINALCHAPTER));
		
		public static Set<Character> allBossCharacters = new HashSet<Character>(Arrays.asList(GROZNYI, WIRE, ZAGAN, BOIES, PUZON, ERIK, SEALEN, BAUKER, BERNARD, DAMIAN, ZOLDAM, UHAI,
				AION, DARIN, CAMERON, OLEG, EUBANS, URSULA, PAUL, JASMINE, PASCAL, KENNETH, JERME, MAXIME, BRENDAN, SONIA, TEODOR, GEORG, KAIM, DENNING, LIMSTELLA, BATTA, ZUGU, GLASS, MIGAL, CARJIGA,
				BUG, BOOL, HEINTZ, BEYARD, YOGI, EAGLER, LUNDGREN, LLOYD_FFO, LINUS_FFO, LLOYD_COD, LINUS_COD, JERME_MORPH, LLOYD_MORPH, LINUS_MORPH, BRENDAN_MORPH, UHAI_MORPH, URSULA_MORPH,
				KENNETH_MORPH, DARIN_MORPH));
		public static Set<Character> restrictedClassCharacters = new HashSet<Character>(Arrays.asList(FIORA, FARINA, VAIDA));
		
		public static Set<Character> allLords = new HashSet<Character>(Arrays.asList(ELIWOOD, HECTOR, LYN, LYN_TUTORIAL));
		public static Set<Character> allThieves = new HashSet<Character>(Arrays.asList(MATTHEW, LEGAULT, JAFFAR));
		public static Set<Character> doNotChange = new HashSet<Character>(Arrays.asList(NERGAL, DRAGON, KISHUNA, FARGUS, MERLINUS, UTHER, ELENORA, LEILA, BRAMIMOND, ZEPHIEL, ELBERT, NATALIE, TACTICIAN));
		
		public static Set<Character> charactersThatRequireRange = new HashSet<Character>(Arrays.asList(ERK, RATH, RATH_TUTORIAL));
		public static Set<Character> charactersThatRequireMelee = new HashSet<Character>(Arrays.asList());
		
		public Boolean isLord() {
			return allLords.contains(this);
		}
		
		public Boolean isThief() {
			return allThieves.contains(this);
		}
		
		public Boolean isBoss() {
			return allBossCharacters.contains(this);
		}
		
		public Boolean isPlayable() {
			return allPlayableCharacters.contains(this);
		}
		
		public Boolean canChange() {
			return !doNotChange.contains(this);
		}
		
		public Boolean requiresRange() {
			return charactersThatRequireRange.contains(this);
		}
		
		public Boolean requiresMelee() {
			return charactersThatRequireMelee.contains(this);
		}
		
		public Boolean isClassLimited() {
			return restrictedClassCharacters.contains(this);
		}
		
		public static Set<Character> allLinkedCharactersFor(Character character) {
			switch (character) {
			case LYN:
			case LYN_TUTORIAL:
				return new HashSet<Character>(Arrays.asList(LYN, LYN_TUTORIAL));
			case WIL:
			case WIL_TUTORIAL:
				return new HashSet<Character>(Arrays.asList(WIL, WIL_TUTORIAL));
			case KENT:
			case KENT_TUTORIAL:
				return new HashSet<Character>(Arrays.asList(KENT, KENT_TUTORIAL));
			case SAIN:
			case SAIN_TUTORIAL:
				return new HashSet<Character>(Arrays.asList(SAIN, SAIN_TUTORIAL));
			case RATH:
			case RATH_TUTORIAL:
				return new HashSet<Character>(Arrays.asList(RATH, RATH_TUTORIAL));
			case FLORINA:
			case FLORINA_TUTORIAL:
				return new HashSet<Character>(Arrays.asList(FLORINA, FLORINA_TUTORIAL));
			case NILS:
			case NILS_FINALCHAPTER:
				return new HashSet<Character>(Arrays.asList(NILS, NILS_FINALCHAPTER));
			case VAIDA:
			case VAIDA_BOSS:
				return new HashSet<Character>(Arrays.asList(VAIDA, VAIDA_BOSS));
			case LLOYD_FFO:
			case LLOYD_COD:
			case LLOYD_MORPH:
				return new HashSet<Character>(Arrays.asList(LLOYD_FFO, LLOYD_COD, LLOYD_MORPH));
			case LINUS_FFO:
			case LINUS_COD:
			case LINUS_MORPH:
				return new HashSet<Character>(Arrays.asList(LINUS_FFO, LINUS_COD, LINUS_MORPH));
			case JERME:
			case JERME_MORPH:
				return new HashSet<Character>(Arrays.asList(JERME, JERME_MORPH));
			case KENNETH:
			case KENNETH_MORPH:
				return new HashSet<Character>(Arrays.asList(KENNETH, KENNETH_MORPH));
			case UHAI:
			case UHAI_MORPH:
				return new HashSet<Character>(Arrays.asList(UHAI, UHAI_MORPH));
			case BRENDAN:
			case BRENDAN_MORPH:
				return new HashSet<Character>(Arrays.asList(BRENDAN, BRENDAN_MORPH));
			case URSULA:
			case URSULA_MORPH:
				return new HashSet<Character>(Arrays.asList(URSULA, URSULA_MORPH));
			case DARIN:
			case DARIN_MORPH:
				return new HashSet<Character>(Arrays.asList(DARIN, DARIN_MORPH));
			default:
				return new HashSet<Character>(Arrays.asList(character));
			}
		}
	}
	
	public enum CharacterClass implements GBAFEClass {
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
		NOMADTROOPER_F(0x31), // Doesn't exist naturally. May not work.
		
		FIRE_DRAGON(0x46), // For dragon effectiveness.
		UBER_SAGE(0x5A); // Limstella has higher DEF and RES caps.
		
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
		
		public static Comparator<CharacterClass> classIDComparator() {
			return new Comparator<CharacterClass>() { public int compare(CharacterClass o1, CharacterClass o2) { return Integer.compare(o1.ID, o2.ID); } };
		}
		
		public static Set<CharacterClass> allMaleClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_ELIWOOD, LORD_HECTOR, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, MONK, MAGE, SHAMAN, CAVALIER, NOMAD,
				WYVERNKNIGHT, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, CORSAIR, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID, PALADIN, NOMADTROOPER, WYVERNLORD,
				BERSERKER, ASSASSIN));
		public static Set<CharacterClass> allFemaleClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_LYN, BLADE_LORD, ARCHER_F, CLERIC, MAGE_F, TROUBADOUR, PEGASUSKNIGHT, DANCER, SWORDMASTER_F, SNIPER_F,
				BISHOP_F, SAGE_F, PALADIN_F, VALKYRIE, FALCONKNIGHT, WYVERNLORD_F, UBER_SAGE));
		public static Set<CharacterClass> allLordClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_ELIWOOD, LORD_LYN, LORD_HECTOR, LORD_KNIGHT, BLADE_LORD, GREAT_LORD));
		public static Set<CharacterClass> allThiefClasses = new HashSet<CharacterClass>(Arrays.asList(THIEF, ASSASSIN));
		public static Set<CharacterClass> allUnpromotedClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_ELIWOOD, LORD_LYN, LORD_HECTOR, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, MONK, MAGE, SHAMAN,
				CAVALIER, NOMAD, WYVERNKNIGHT, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, CORSAIR, ARCHER_F, CLERIC, MAGE_F, TROUBADOUR, PEGASUSKNIGHT, DANCER));
		public static Set<CharacterClass> allPromotedClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_KNIGHT, BLADE_LORD, GREAT_LORD, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID,
				PALADIN, NOMADTROOPER, WYVERNLORD, BERSERKER, ASSASSIN, SWORDMASTER_F, SNIPER_F, BISHOP_F, SAGE_F, PALADIN_F, VALKYRIE, FALCONKNIGHT, WYVERNLORD_F, UBER_SAGE));
		public static Set<CharacterClass> allPacifistClasses = new HashSet<CharacterClass>(Arrays.asList(DANCER, BARD, CLERIC, TROUBADOUR));
		public static Set<CharacterClass> allMeleeLockedClasses = new HashSet<CharacterClass>(Arrays.asList(MYRMIDON, MERCENARY, LORD_LYN, LORD_ELIWOOD, THIEF));
		
		public static Set<CharacterClass> allValidClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_ELIWOOD, LORD_HECTOR, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, MONK, MAGE, SHAMAN, CAVALIER, NOMAD,
				WYVERNKNIGHT, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, CORSAIR, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID, PALADIN, NOMADTROOPER, WYVERNLORD,
				BERSERKER, ASSASSIN, LORD_LYN, BLADE_LORD, ARCHER_F, CLERIC, MAGE_F, TROUBADOUR, PEGASUSKNIGHT, DANCER, SWORDMASTER_F, SNIPER_F,
				BISHOP_F, SAGE_F, PALADIN_F, VALKYRIE, FALCONKNIGHT, WYVERNLORD_F, LORD_KNIGHT, GREAT_LORD, UBER_SAGE));
		
		private static Boolean isClassPromoted(CharacterClass sourceClass) {
			return allPromotedClasses.contains(sourceClass);
		}
		
		public static Set<CharacterClass> classesThatLoseToClass(CharacterClass originalClass, CharacterClass winningClass, Boolean excludeLords, Boolean excludeThieves) {
			Set<CharacterClass> classList = new HashSet<CharacterClass>();
			
			switch (winningClass) {
			case LORD_ELIWOOD:
			case LORD_LYN:
			case MERCENARY:
			case MYRMIDON: 
			case THIEF: {
				classList.add(FIGHTER);
				classList.add(BRIGAND);
				classList.add(PIRATE);
				classList.add(CORSAIR);
				break;
			}
			case LORD_HECTOR:
			case FIGHTER:
			case BRIGAND:
			case PIRATE:
			case CORSAIR: {
				classList.add(KNIGHT);
				classList.add(SOLDIER);
				break;
			}
			case KNIGHT:
			case SOLDIER:
			case PEGASUSKNIGHT: {
				classList.add(MYRMIDON);
				classList.add(MERCENARY);
				classList.add(SOLDIER);
				if (!excludeLords) {
					classList.add(LORD_ELIWOOD);
					classList.add(LORD_LYN);
				}
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
				classList.add(PEGASUSKNIGHT);
				classList.add(SOLDIER);
				break;
			}
			default:
				break;
			}
			
			return classList;
		}
		
		public static Set<CharacterClass> targetClassesForRandomization(CharacterClass sourceClass, Boolean excludeSource, Boolean excludeLords, Boolean excludeThieves, Boolean requireAttack, Boolean requiresRange, Boolean applyRestrictions) {
			Set<CharacterClass> limited = limitedClassesForRandomization(sourceClass);
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
			
			if (requireAttack) {
				classList.removeAll(allPacifistClasses);
			}
			if (requiresRange) {
				classList.removeAll(allPacifistClasses);
				classList.removeAll(allMeleeLockedClasses);
			}
			
			return classList;
		}
		
		private static Set<CharacterClass> limitedClassesForRandomization(CharacterClass sourceClass) {
			switch(sourceClass) {
			case WYVERNKNIGHT:
			case PEGASUSKNIGHT:
				return new HashSet<CharacterClass>(Arrays.asList(WYVERNKNIGHT, PEGASUSKNIGHT));
			case WYVERNLORD:
			case WYVERNLORD_F:
			case FALCONKNIGHT:
				return new HashSet<CharacterClass>(Arrays.asList(WYVERNLORD, WYVERNLORD_F, FALCONKNIGHT));
			case PIRATE:
			case CORSAIR:
				return new HashSet<CharacterClass>(Arrays.asList(PIRATE, CORSAIR, WYVERNKNIGHT, PEGASUSKNIGHT));
			case BRIGAND:
				return new HashSet<CharacterClass>(Arrays.asList(BRIGAND, WYVERNKNIGHT, PEGASUSKNIGHT));
			case BERSERKER:
				return new HashSet<CharacterClass>(Arrays.asList(BERSERKER, WYVERNLORD, WYVERNLORD_F, FALCONKNIGHT));
			default:
				return null;
			}
		}

		public int getID() {
			return ID;
		}

		public Boolean isLord() {
			return CharacterClass.allLordClasses.contains(this);
		}

		public Boolean isThief() {
			return CharacterClass.allThiefClasses.contains(this);
		}

		public Boolean isFemale() {
			return CharacterClass.allFemaleClasses.contains(this);
		}

		public Boolean isPromoted() {
			return CharacterClass.allPromotedClasses.contains(this);
		}

		public Boolean canAttack() {
			return !CharacterClass.allPacifistClasses.contains(this);
		}
	}
	
	public enum Item implements GBAFEItem {
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
		
		public static Comparator<Item> itemIDComparator() {
			return new Comparator<Item>() { public int compare(Item o1, Item o2) { return Integer.compare(o1.ID, o2.ID); } };
		}
		
		public enum FE7WeaponRank {
			E(0x01), D(0x1F), C(0x47), B(0x79), A(0x0B5), S(0x0FB);
			
			public int value;
			
			private static Map<Integer, FE7WeaponRank> map = new HashMap<Integer, FE7WeaponRank>();
			
			static {
				for (FE7WeaponRank rank : FE7WeaponRank.values()) {
					map.put(rank.value, rank);
				}
			}
			
			private FE7WeaponRank(final int value) { this.value = value; }
			
			public static FE7WeaponRank valueOf(int rankVal) {
				return map.get(rankVal);
			}
			
			public static FE7WeaponRank rankFromGeneralRank(WeaponRank generalRank) {
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
			
			public Boolean isHigherThanRank(FE7WeaponRank otherRank) {
				return (value & 0xFF) > (otherRank.value & 0xFF);
			}
			
			public Boolean isLowerThanRank(FE7WeaponRank otherRank) {
				return (value & 0xFF) < (otherRank.value & 0xFF);
			}
			
			public Boolean isSameRank(FE7WeaponRank otherRank) {
				return value == otherRank.value;
			}
		}
		
		public enum FE7WeaponType {
			SWORD(0x00), LANCE(0x01), AXE(0x02), BOW(0x03), STAFF(0x04), ANIMA(0x05), LIGHT(0x06), DARK(0x07), 
			
			ITEM(0x09), 
			
			DRAGONSTONE(0x0B), 
			
			DANCINGRING(0x0C);
			
			public int ID;
			
			private static Map<Integer, FE7WeaponType> map = new HashMap<Integer, FE7WeaponType>();
			
			static {
				for (FE7WeaponType type : FE7WeaponType.values()) {
					map.put(type.ID, type);
				}
			}
			
			private FE7WeaponType(final int id) { ID = id; }
			
			public static FE7WeaponType valueOf(int typeVal) {
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
			NONE(0x00), REVERSE_WEAPON_TRIANGLE(0x01), DRAGONSTONE_LOCK(0x04), 
			MYRMIDON_LOCK(0x10), IOTE_SHIELD_EFFECT(0x40), IRON_RUNE_EFFECT(0x80);
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
			NONE(0x00), NEGATE_DEFENSE(0x02), ELIWOOD_LOCK(0x04), HECTOR_LOCK(0x08),
			LYN_LOCK(0x10), ATHOS_LOCK(0x20);
			
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
			NONE(0x00), POISON(0x01), STEALS_HP(0x02), HALVES_HP(0x03), DEVIL(0x04);
			
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
		
		public static Set<Item> allSwords = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, POISON_SWORD, RAPIER, MANI_KATTI, BRAVE_SWORD,
				WO_DAO, KILLING_EDGE, ARMORSLAYER, WYRMSLAYER, LIGHT_BRAND, RUNE_SWORD, LANCEREAVER, LONGSWORD, EMBLEM_SWORD, DURANDAL, SOL_KATTI, REGAL_BLADE, WIND_SWORD));
		public static Set<Item> allLances = new HashSet<Item>(Arrays.asList(IRON_LANCE, SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, POISON_LANCE, BRAVE_LANCE, KILLER_LANCE, HORSESLAYER, JAVELIN, SPEAR, AXEREAVER,
				EMBLEM_LANCE, REX_HASTA, HEAVY_SPEAR, SHORT_SPEAR, UBER_SPEAR));
		public static Set<Item> allAxes = new HashSet<Item>(Arrays.asList(IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, KILLER_AXE, HALBERD, HAMMER, DEVIL_AXE, HAND_AXE, TOMAHAWK, SWORDREAVER,
				SWORDSLAYER, DRAGON_AXE, EMBLEM_AXE, ARMADS, WOLF_BEIL, BASILIKOS));
		public static Set<Item> allBows = new HashSet<Item>(Arrays.asList(IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, EMBLEM_BOW, RIENFLECHE));
		public static Set<Item> allAnima = new HashSet<Item>(Arrays.asList(FIRE, THUNDER, ELFIRE, BOLTING, FIMBULVETR, FORBLAZE, EXCALIBUR));
		public static Set<Item> allLight = new HashSet<Item>(Arrays.asList(LIGHTNING, SHINE, DIVINE, PURGE, AURA, LUCE, AUREOLA));
		public static Set<Item> allDark = new HashSet<Item>(Arrays.asList(FLUX, LUNA, NOSFERATU, ECLIPSE, FENRIR, GESPENST));
		public static Set<Item> allHealingStaves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, PHYSIC, FORTIFY));
		public static Set<Item> allSupportStaves = new HashSet<Item>(Arrays.asList(RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER));
		public static Set<Item> allStatusStaves = new HashSet<Item>(Arrays.asList(SILENCE, SLEEP, BERSERK));
		public static Set<Item> allStatBoosters = new HashSet<Item>(Arrays.asList(ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWINGS, GODDESS_ICON, DRAGONSHIELD, TALISMAN, BOOTS, BODY_RING, AFA_DROPS));
		public static Set<Item> allPromotionItems = new HashSet<Item>(Arrays.asList(HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP, GUIDING_RING, EARTH_SEAL, HEAVEN_SEAL, EMBLEM_SEAL, FELL_CONTRACT, OCEAN_SEAL));
		public static Set<Item> allDancingRings = new HashSet<Item>(Arrays.asList(FILLA_MIGHT, NINI_GRACE, THOR_IRE, SET_LITANY));
		public static Set<Item> allSpecialItems = new HashSet<Item>(Arrays.asList(DELPHI_SHIELD, MEMBER_CARD, IRON_RUNE, SILVER_CARD));
		public static Set<Item> allMoneyItems = new HashSet<Item>(Arrays.asList(WHITE_GEM, BLUE_GEM, RED_GEM, GOLD_3000, GOLD_5000));
		public static Set<Item> chestOnlyItems = new HashSet<Item>(Arrays.asList(GOLD_3000, GOLD_5000));
		public static Set<Item> usableItems = new HashSet<Item>(Arrays.asList(CHEST_KEY, CHEST_KEY_5, DOOR_KEY, LOCKPICK, VULNERARY, ELIXIR, PURE_WATER, ANTITOXIN, TORCH, MINE, LIGHT_RUNE));
		
		public static Set<Item> allPotentialRewards = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, EMBLEM_SWORD, IRON_LANCE, SLIM_LANCE, JAVELIN, EMBLEM_LANCE, POISON_LANCE, HAND_AXE, IRON_AXE, EMBLEM_AXE, STEEL_AXE,
				DEVIL_AXE, IRON_BOW, EMBLEM_BOW, FIRE, LIGHTNING, HEAL, POISON_SWORD, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, LONGSWORD, WO_DAO, STEEL_LANCE, HEAVY_SPEAR, HORSESLAYER, POISON_AXE, HALBERD, HAMMER, POISON_BOW,
				SHORT_BOW, LONGBOW, STEEL_BOW, THUNDER, SHINE, FLUX, MEND, TORCH_STAFF, UNLOCK, STEEL_BLADE, KILLING_EDGE, WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, SHORT_SPEAR, KILLER_LANCE, AXEREAVER, DRAGON_AXE, KILLER_AXE, SWORDREAVER, 
				SWORDSLAYER, KILLER_BOW, ELFIRE, DIVINE, NOSFERATU, RECOVER, RESTORE, HAMMERNE, BARRIER, BRAVE_SWORD, WIND_SWORD, BRAVE_LANCE, SPEAR, BRAVE_AXE, BRAVE_BOW, BOLTING, PURGE, ECLIPSE, PHYSIC, SILENCE, SLEEP, BERSERK, 
				RESCUE, SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, SILVER_LANCE, TOMAHAWK, SILVER_AXE, SILVER_BOW, FIMBULVETR, AURA, FENRIR, FORTIFY, WARP,
				ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWINGS, GODDESS_ICON, DRAGONSHIELD, TALISMAN, BOOTS, BODY_RING, AFA_DROPS,
				HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP, GUIDING_RING, EARTH_SEAL, HEAVEN_SEAL, EMBLEM_SEAL, FELL_CONTRACT, OCEAN_SEAL,
				FILLA_MIGHT, NINI_GRACE, THOR_IRE, SET_LITANY,
				DELPHI_SHIELD, MEMBER_CARD, IRON_RUNE, SILVER_CARD,
				WHITE_GEM, BLUE_GEM, RED_GEM));
		
		public static Set<Item> allWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, POISON_SWORD, RAPIER, MANI_KATTI, BRAVE_SWORD,
				WO_DAO, KILLING_EDGE, ARMORSLAYER, WYRMSLAYER, LIGHT_BRAND, RUNE_SWORD, LANCEREAVER, LONGSWORD, EMBLEM_SWORD, DURANDAL, SOL_KATTI, REGAL_BLADE, WIND_SWORD, IRON_LANCE, 
				SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, POISON_LANCE, BRAVE_LANCE, KILLER_LANCE, HORSESLAYER, JAVELIN, SPEAR, AXEREAVER, EMBLEM_LANCE, REX_HASTA, HEAVY_SPEAR, SHORT_SPEAR, 
				IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, KILLER_AXE, HALBERD, HAMMER, DEVIL_AXE, HAND_AXE, TOMAHAWK, SWORDREAVER, SWORDSLAYER, DRAGON_AXE, EMBLEM_AXE, ARMADS, 
				WOLF_BEIL, BASILIKOS, IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, EMBLEM_BOW, RIENFLECHE, FIRE, THUNDER, ELFIRE, BOLTING, 
				FIMBULVETR, FORBLAZE, EXCALIBUR, LIGHTNING, SHINE, DIVINE, PURGE, AURA, LUCE, AUREOLA, FLUX, LUNA, NOSFERATU, ECLIPSE, FENRIR, GESPENST));
		public static Set<Item> allRangedWeapons = new HashSet<Item>(Arrays.asList(LIGHT_BRAND, RUNE_SWORD, WIND_SWORD, JAVELIN, SPEAR, SHORT_SPEAR, HAND_AXE, TOMAHAWK, IRON_BOW, STEEL_BOW,
				SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, EMBLEM_BOW, RIENFLECHE, FIRE, THUNDER, ELFIRE, FIMBULVETR, FORBLAZE, EXCALIBUR, LIGHTNING, SHINE, DIVINE, 
				AURA, LUCE, AUREOLA, FLUX, LUNA, NOSFERATU, FENRIR, GESPENST));
		public static Set<Item> allStaves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, PHYSIC, FORTIFY, RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER, SILENCE, SLEEP, BERSERK));
		public static Set<Item> allERank = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, EMBLEM_SWORD, IRON_LANCE, SLIM_LANCE, JAVELIN, EMBLEM_LANCE, POISON_LANCE, HAND_AXE, IRON_AXE, EMBLEM_AXE, STEEL_AXE,
				DEVIL_AXE, IRON_BOW, EMBLEM_BOW, FIRE, LIGHTNING, HEAL));
		public static Set<Item> allDRank = new HashSet<Item>(Arrays.asList(POISON_SWORD, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, LONGSWORD, WO_DAO, STEEL_LANCE, HEAVY_SPEAR, HORSESLAYER, POISON_AXE, HALBERD, HAMMER, POISON_BOW,
				SHORT_BOW, LONGBOW, STEEL_BOW, THUNDER, SHINE, FLUX, MEND, TORCH_STAFF, UNLOCK));
		public static Set<Item> allCRank = new HashSet<Item>(Arrays.asList(STEEL_BLADE, KILLING_EDGE, WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, SHORT_SPEAR, KILLER_LANCE, AXEREAVER, DRAGON_AXE, KILLER_AXE, SWORDREAVER, 
				SWORDSLAYER, KILLER_BOW, ELFIRE, DIVINE, LUNA, NOSFERATU, RECOVER, RESTORE, HAMMERNE, BARRIER));
		public static Set<Item> allBRank = new HashSet<Item>(Arrays.asList(BRAVE_SWORD, WIND_SWORD, BRAVE_LANCE, SPEAR, UBER_SPEAR, BRAVE_AXE, BRAVE_BOW, BOLTING, PURGE, ECLIPSE, PHYSIC, SILENCE, SLEEP, BERSERK, RESCUE));
		public static Set<Item> allARank = new HashSet<Item>(Arrays.asList(SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, SILVER_LANCE, TOMAHAWK, SILVER_AXE, SILVER_BOW, FIMBULVETR, AURA, FENRIR, FORTIFY, WARP));
		public static Set<Item> allSRank = new HashSet<Item>(Arrays.asList(REGAL_BLADE, REX_HASTA, BASILIKOS, RIENFLECHE, EXCALIBUR, LUCE, GESPENST, AUREOLA));
		public static Set<Item> allPrfRank = new HashSet<Item>(Arrays.asList(MANI_KATTI, RAPIER, DURANDAL, SOL_KATTI, WOLF_BEIL, ARMADS, FORBLAZE));
		
		public static Set<Item> allBasicWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, IRON_LANCE, IRON_AXE, IRON_BOW, FIRE, LIGHTNING, FLUX));
		
		public static Set<Item> basicItemsOfType(WeaponType type) {
			Set<Item> set = new HashSet<Item>();
			set.addAll(weaponsOfType(type));
			set.retainAll(allBasicWeapons);
			return set;
		}
		
		public static List<Item> formerThiefKit() {
			return new ArrayList<Item>(Arrays.asList(CHEST_KEY_5, DOOR_KEY, DOOR_KEY));
		}
		
		public static Set<Item> itemsToRemoveFromFormerThief() {
			return new HashSet<Item>(Arrays.asList(LOCKPICK));
		}
		
		public static Set<Item> specialClassKit(int classID, Random rng) {
			if (classID == FE7Data.CharacterClass.DANCER.ID) {
				int randomIndex = rng.nextInt(allDancingRings.size());
				List<Item> rings = new ArrayList<Item>(allDancingRings);
				Collections.sort(rings, Item.itemIDComparator());
				return new HashSet<Item>(Arrays.asList(rings.get(randomIndex)));
			} else if (classID == FE7Data.CharacterClass.THIEF.ID) {
				return new HashSet<Item>(Arrays.asList(LOCKPICK));
			}
			
			return null;
		}
		
		public static Set<Item> prfWeaponsForClassID(int classID) {
			if (classID == FE7Data.CharacterClass.LORD_LYN.ID || classID == FE7Data.CharacterClass.BLADE_LORD.ID) {
				return new HashSet<Item>(Arrays.asList(MANI_KATTI));
			} else if (classID == FE7Data.CharacterClass.LORD_ELIWOOD.ID || classID == FE7Data.CharacterClass.LORD_KNIGHT.ID) {
				return new HashSet<Item>(Arrays.asList(RAPIER));
			} else if (classID == FE7Data.CharacterClass.LORD_HECTOR.ID || classID == FE7Data.CharacterClass.GREAT_LORD.ID) {
				return new HashSet<Item>(Arrays.asList(WOLF_BEIL));
			}
			
			return null;
		}
		
		public static Set<Item> lockedWeaponsToClassID(int classID) {
			if (classID == FE7Data.CharacterClass.MYRMIDON.ID || classID == FE7Data.CharacterClass.MYRMIDON_F.ID ||
					classID == FE7Data.CharacterClass.SWORDMASTER.ID || classID == FE7Data.CharacterClass.SWORDMASTER_F.ID ||
					classID == FE7Data.CharacterClass.LORD_LYN.ID || classID == FE7Data.CharacterClass.BLADE_LORD.ID) {
				return new HashSet<Item>(Arrays.asList(WO_DAO));
			}
			
			return null;
		}
		
		public static Set<Item> weaponsOfType(WeaponType type) {
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
			
			return list;
		}
		
		public static Set<Item> weaponsOfRank(WeaponRank rank) {
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
			
			return list;
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
		
		public static Set<Item> weaponsOfTypeAndRank(WeaponType type, WeaponRank min, WeaponRank max, Boolean requiresRange) {
			if (min == WeaponRank.PRF || max == WeaponRank.PRF) {
				return null;
			}
			
			if (min == null || max == null) {
				return null;
			}
			
			FE7WeaponRank minRank = FE7WeaponRank.E;
			if (min != WeaponRank.NONE) {
				minRank = FE7WeaponRank.rankFromGeneralRank(min);
			}
			
			FE7WeaponRank maxRank = FE7WeaponRank.S;
			if (max != WeaponRank.NONE) {
				maxRank = FE7WeaponRank.rankFromGeneralRank(max);
			}
			
			if (minRank.isHigherThanRank(maxRank)) {
				return null;
			}
			
			Set<Item> list = new HashSet<Item>();
			list.addAll(weaponsOfType(type));
			
			if (FE7WeaponRank.E.isLowerThanRank(minRank)) {
				list.removeAll(allERank);
			}
			if (FE7WeaponRank.D.isLowerThanRank(minRank)) {
				list.removeAll(allDRank);
			}
			if (FE7WeaponRank.C.isLowerThanRank(minRank)) {
				list.removeAll(allCRank);
			}
			if (FE7WeaponRank.B.isLowerThanRank(minRank)) {
				list.removeAll(allBRank);
			}
			if (FE7WeaponRank.A.isLowerThanRank(minRank)) {
				list.removeAll(allARank);
			}
			
			list.removeAll(allPrfRank);
			list.remove(WO_DAO); // This one is special. It must be added in only if we're certain the class asking for the item can use it.
			list.remove(UBER_SPEAR); // We probably shouldn't have this randomly show up.
			
			if (FE7WeaponRank.S.isHigherThanRank(maxRank)) {
				list.removeAll(allSRank);
			}
			if (FE7WeaponRank.A.isHigherThanRank(maxRank)) {
				list.removeAll(allARank);
			}
			if (FE7WeaponRank.B.isHigherThanRank(maxRank)) {
				list.removeAll(allBRank);
			}
			if (FE7WeaponRank.C.isHigherThanRank(maxRank)) {
				list.removeAll(allCRank);
			}
			if (FE7WeaponRank.D.isHigherThanRank(maxRank)) {
				list.removeAll(allDRank);
			}
			
			if (requiresRange) {
				list.retainAll(allRangedWeapons);
			}
			
			return list;
		}
		
		public int getID() {
			return ID;
		}

		public Boolean isWeapon() {
			return allWeapons.contains(this);
		}

		public Boolean isBasicWeapon() {
			return allBasicWeapons.contains(this);
		}

		public Boolean isStatBooster() {
			return allStatBoosters.contains(this);
		}

		public Boolean isPromotionItem() {
			return allPromotionItems.contains(this);
		}

		public WeaponType getType() {
			if (allSwords.contains(this)) { return WeaponType.SWORD; }
			if (allLances.contains(this)) { return WeaponType.LANCE; }
			if (allAxes.contains(this)) { return WeaponType.AXE; }
			if (allBows.contains(this)) { return WeaponType.BOW; }
			if (allAnima.contains(this)) { return WeaponType.ANIMA; }
			if (allLight.contains(this)) { return WeaponType.LIGHT; }
			if (allDark.contains(this)) { return WeaponType.DARK; }
			if (allStaves.contains(this)) { return WeaponType.STAFF; }
			
			return WeaponType.NOT_A_WEAPON;
		}

		public WeaponRank getRank() {
			if (allSRank.contains(this)) { return WeaponRank.S; }
			if (allARank.contains(this)) { return WeaponRank.A; }
			if (allBRank.contains(this)) { return WeaponRank.B; }
			if (allCRank.contains(this)) { return WeaponRank.C; }
			if (allDRank.contains(this)) { return WeaponRank.D; }
			if (allERank.contains(this)) { return WeaponRank.E; }
			
			return WeaponRank.NONE;
		}

		public Boolean isHealingStaff() {
			return allHealingStaves.contains(this);
		}
	}
	
	public enum ChapterPointer {
		PROLOGUE(0x06), CHAPTER_1(0x09), CHAPTER_2(0x0F), CHAPTER_3(0x15), CHAPTER_4(0x1B), CHAPTER_5(0x21), CHAPTER_6(0x25), CHAPTER_7(0x28),
		CHAPTER_7X(0x2C), CHAPTER_8(0x2F), CHAPTER_9(0x32), CHAPTER_10(0x36),
		
		CHAPTER_11_E(0x39), CHAPTER_11_H(0x3D), CHAPTER_12(0x40), CHAPTER_13(0x43), CHAPTER_13X(0x47), CHAPTER_14(0x4B), CHAPTER_15(0x4E),
		CHAPTER_16(0x51), CHAPTER_17(0x55), CHAPTER_17X(0x5A), CHAPTER_18(0x61), CHAPTER_19(0x65), CHAPTER_19X(0x69), CHAPTER_19XX(0x6F),
		CHAPTER_20(0x72), CHAPTER_21(0x76), CHAPTER_22(0x7A), CHAPTER_23(0x7F), CHAPTER_23X(0x83), CHAPTER_24_LLOYD(0x86), CHAPTER_24_LINUS(0x89),
		CHAPTER_25(0x8D), CHAPTER_26(0x90), CHAPTER_27_KENNETH(0x96), CHAPTER_27_JERME(0x9A), CHAPTER_28(0x9E), CHAPTER_28X(0xA3), CHAPTER_29(0xA6),
		CHAPTER_28_E(0xAD), CHAPTER_30_H(0xB3), CHAPTER_31(0xB6), CHAPTER_31X(0xB9), CHAPTER_32(0xBC), CHAPTER_32X(0xC0), CHAPTER_FINAL(0xC7),
		CHAPTER_FINAL_2(0xCA);
		
		public int chapterID;
		
		private ChapterPointer(int chapterID) {
			this.chapterID = chapterID;
		}
		
		public FE7Data.CharacterClass[] blacklistedClasses() {
			switch(this) {
			case CHAPTER_5:
				return new FE7Data.CharacterClass[] {CharacterClass.ARCHER};
			default:
				return new FE7Data.CharacterClass[] {};
			}
		}
		
		public Boolean shouldBeEasy() {
			switch(this) {
			case PROLOGUE:
			case CHAPTER_11_H:
				return true;
			default:
				return false;
			}
		}
		
		public Boolean shouldRemoveFightScenes() {
			switch (this) {
			case CHAPTER_21:
				return true;
			default:
				return false;
			}
		}
		
		public Boolean isClassSafe() {
			switch (this) {
			case PROLOGUE:
			case CHAPTER_1:
			case CHAPTER_2:
			case CHAPTER_3:
			case CHAPTER_4:
			case CHAPTER_5:
			case CHAPTER_6:
			case CHAPTER_7:
			case CHAPTER_7X:
			case CHAPTER_8:
			case CHAPTER_17X:
			case CHAPTER_29:
				return true;
			default:
				return false;
			}
		}
	}
	
	public enum PromotionItem implements GBAFEPromotionItem {
		// Ocean Seal is missing, but I can't find it in this table.
		// It's pointer can be found at 0x27574.
		// Remember that the actual address of the class IDs starts at byte 4 after the jump.
		// The class IDs are 00 terminated.
		HERO_CREST(0x01), KNIGHT_CREST(0x02), ORION_BOLT(0x03), ELYSIAN_WHIP(0x04), GUIDING_RING(0x05), MASTER_SEAL(0x25), FALLEN_CONTRACT(0x29);
		
		int offset;
		
		private PromotionItem(final int offset) {
			this.offset = offset;
		}
		
		public long getPointerAddress() {
			return (offset * 4) + PromotionItemTablePointer;
		}
		
		public long getListAddress() {
			return getPointerAddress();
		}
		
		public Boolean isIndirected() {
			return true;
		}
		
		public String itemName() {
			return this.toString();
		}
	}
	
	public enum Palette {
		
		ARCHER_WIL(Character.WIL.ID, CharacterClass.ARCHER.ID, 0xFD90B4),
		ARCHER_REBECCA(Character.REBECCA.ID, CharacterClass.ARCHER_F.ID, 0xFD9050),
		
		ASSASSIN_MATTHEW(Character.MATTHEW.ID, CharacterClass.ASSASSIN.ID, 0xFD95B8),
		ASSASSIN_JAFFAR(Character.JAFFAR.ID, CharacterClass.ASSASSIN.ID, 0xFD94B4),
		ASSASSIN_LEGAULT(Character.LEGAULT.ID, CharacterClass.ASSASSIN.ID, 0xFD955C),
		ASSASSIN_JERME(Character.JERME.ID, CharacterClass.ASSASSIN.ID, 0xFD9508), // Needs hair override
		
		BRIGAND_BATTA(Character.BATTA.ID, CharacterClass.BRIGAND.ID, 0xFD9610),
		BRIGAND_BUG(Character.BUG.ID, CharacterClass.BRIGAND.ID, 0xFD9664),
		BRIGAND_CARJIGA(Character.CARJIGA.ID, CharacterClass.BRIGAND.ID, 0xFD96B4),
		BRIGAND_MIGAL(Character.MIGAL.ID, CharacterClass.BRIGAND.ID, 0xFD9704),
		BRIGAND_ZUGU(Character.ZUGU.ID, CharacterClass.BRIGAND.ID, 0xFD97A8),
		BRIGAND_GROZNYI(Character.GROZNYI.ID, CharacterClass.BRIGAND.ID, 0xFDBF64),
		
		BARD_NILS(Character.NILS.ID, CharacterClass.BARD.ID, 0xFD9B7C),
		
		BERSERKER_DART(Character.DART.ID, CharacterClass.BERSERKER.ID, 0xFD9BDC), // Needs hair override
		BERSERKER_GEORG(Character.GEORG.ID, CharacterClass.BERSERKER.ID, 0xFD9C84),
		BERSERKER_HAWKEYE(Character.HAWKEYE.ID, CharacterClass.BERSERKER.ID, 0xFD97F8),
		
		BISHOP_RENAULT(Character.RENAULT.ID, CharacterClass.BISHOP.ID, 0xFD9908),
		BISHOP_KENNETH(Character.KENNETH.ID, CharacterClass.BISHOP.ID, 0xFD98A8),
		BISHOP_SERRA(Character.SERRA.ID, CharacterClass.BISHOP_F.ID, 0xFD9848),
		BISHOP_LUCIUS(Character.LUCIUS.ID, CharacterClass.BISHOP.ID, 0xFD9970),
		
		BLADE_LORD_LYN(Character.LYN.ID, CharacterClass.BLADE_LORD.ID, 0xFD99DC),
		
		CAVALIER_KENT(Character.KENT.ID, CharacterClass.CAVALIER.ID, 0xFDB720),
		CAVALIER_LOWEN(Character.LOWEN.ID, CharacterClass.CAVALIER.ID, 0xFDB798),
		CAVALIER_SAIN(Character.SAIN.ID, CharacterClass.CAVALIER.ID, 0xFDB810),
		CAVALIER_ERIK(Character.ERIK.ID, CharacterClass.CAVALIER.ID, 0xFDB6A4),
		
		CLERIC_SERRA(Character.SERRA.ID, CharacterClass.CLERIC.ID, 0xFDB134),
		
		DANCER_NINIAN(Character.NINIAN.ID, CharacterClass.DANCER.ID, 0xFD9CD4),
		
		DRUID_CANAS(Character.CANAS.ID, CharacterClass.DRUID.ID, 0xFD9F08),
		DRUID_TEODOR(Character.TEODOR.ID, CharacterClass.DRUID.ID, 0xFD9F5C),
		
		FALCONKNIGHT_FARINA(Character.FARINA.ID, CharacterClass.FALCONKNIGHT.ID, 0xFDA00C),
		FALCONKNIGHT_FIORA(Character.FIORA.ID, CharacterClass.FALCONKNIGHT.ID, 0xFDA07C),
		FALCONKNIGHT_FLORINA(Character.FLORINA.ID, CharacterClass.FALCONKNIGHT.ID, 0xFDA0EC),
		
		FIGHTER_BARTRE(Character.BARTRE.ID, CharacterClass.FIGHTER.ID, 0xFDA15C),
		FIGHTER_DORCAS(Character.DORCAS.ID, CharacterClass.FIGHTER.ID, 0xFDA1B4),
		FIGHTER_ZAGAN(Character.ZAGAN.ID, CharacterClass.FIGHTER.ID, 0xFD9754),
		
		GENERAL_OSWIN(Character.OSWIN.ID, CharacterClass.GENERAL.ID, 0xFDA2EC), // Needs hair override
		GENERAL_WALLACE(Character.WALLACE.ID, CharacterClass.GENERAL.ID, 0xFDA374), // RIP
		GENERAL_LUNDGREN(Character.LUNDGREN.ID, CharacterClass.GENERAL.ID, 0xFDA3FC), // Hair override
		GENERAL_DARIN(Character.DARIN.ID, CharacterClass.GENERAL.ID, 0xFDA27C), // Hair override
		GENERAL_BERNARD(Character.BERNARD.ID, CharacterClass.GENERAL.ID, 0xFDA20C), // Hair override
		
		GREAT_LORD_HECTOR(Character.HECTOR.ID, CharacterClass.GREAT_LORD.ID, 0xFDA46C),
		
		HERO_HARKEN(Character.HARKEN.ID, CharacterClass.HERO.ID, 0xFD9A2C),
		HERO_KAIM(Character.KAIM.ID, CharacterClass.HERO.ID, 0xFD9A84),
		HERO_RAVEN(Character.RAVEN.ID, CharacterClass.HERO.ID, 0xFD9AD4),
		HERO_LINUS(Character.LINUS_FFO.ID, CharacterClass.HERO.ID, 0xFD9B2C),
		
		// All knights need hair overrides.
		KNIGHT_OSWIN(Character.OSWIN.ID, CharacterClass.KNIGHT.ID, 0xFD92D8),
		KNIGHT_WALLACE(Character.WALLACE.ID, CharacterClass.KNIGHT.ID, 0xFD935C),
		KNIGHT_BOIES(Character.BOIES.ID, CharacterClass.KNIGHT.ID, 0xFD9114),
		KNIGHT_BOOL(Character.BOOL.ID, CharacterClass.KNIGHT.ID, 0xFD9180),
		KNIGHT_BAUKER(Character.BAUKER.ID, CharacterClass.KNIGHT.ID, 0xFD91EC),
		KNIGHT_WIRE(Character.WIRE.ID, CharacterClass.KNIGHT.ID, 0xFD93DC),
		KNIGHT_YOGI(Character.YOGI.ID, CharacterClass.KNIGHT.ID, 0xFD9448),
		
		LORD_LYN(Character.LYN.ID, CharacterClass.LORD_LYN.ID, 0xFD9000),
		LORD_ELIWOOD(Character.ELIWOOD.ID, CharacterClass.LORD_ELIWOOD.ID, 0xFD9FAC),
		LORD_HECTOR(Character.HECTOR.ID, CharacterClass.LORD_HECTOR.ID, 0xFDA4C8),
		LORD_KNIGHT_ELIWOOD(Character.ELIWOOD.ID, CharacterClass.LORD_KNIGHT.ID, 0xFDA524),
		
		MAGE_ERK(Character.ERK.ID, CharacterClass.MAGE.ID, 0xFDA5E8),
		MAGE_NINO(Character.NINO.ID, CharacterClass.MAGE_F.ID, 0xFDA57C),
		
		MERCENARY_RAVEN(Character.RAVEN.ID, CharacterClass.MERCENARY.ID, 0xFDA714),
		MERCENARY_BEYARD(Character.BEYARD.ID, CharacterClass.MERCENARY.ID, 0xFDA65C),
		MERCENARY_GLASS(Character.GLASS.ID, CharacterClass.MERCENARY.ID, 0xFDA6B8),
		MERCENARY_PUZON(Character.PUZON.ID, CharacterClass.MERCENARY.ID, 0xFDA780),
		
		MONK_LUCIUS(Character.LUCIUS.ID, CharacterClass.MONK.ID, 0xFDA7DC),
		
		MYRMIDON_GUY(Character.GUY.ID, CharacterClass.MYRMIDON.ID, 0xFDA83C),
		
		NOMAD_RATH(Character.RATH.ID, CharacterClass.NOMAD.ID, 0xFDA89C),
		NOMAD_SEALEN(Character.SEALEN.ID, CharacterClass.NOMAD.ID, 0xFDA910),
		
		NOMADTROOPER_RATH(Character.RATH.ID, CharacterClass.NOMADTROOPER.ID, 0xFDA984),
		NOMADTROOPER_UHAI(Character.UHAI.ID, CharacterClass.NOMADTROOPER.ID, 0xFDAA14),
		
		PALADIN_KENT(Character.KENT.ID, CharacterClass.PALADIN.ID, 0xFDAC6C),
		PALADIN_LOWEN(Character.LOWEN.ID, CharacterClass.PALADIN.ID, 0xFDACF8),
		PALADIN_MARCUS(Character.MARCUS.ID, CharacterClass.PALADIN.ID, 0xFDAD84), // Hair override
		PALADIN_SAIN(Character.SAIN.ID, CharacterClass.PALADIN.ID, 0xFDAF00),
		PALADIN_EAGLER(Character.EAGLER.ID, CharacterClass.PALADIN.ID, 0xFD9258), // Hair override
		PALADIN_CAMERON(Character.CAMERON.ID, CharacterClass.PALADIN.ID, 0xFDAB04),
		PALADIN_DAMIAN(Character.DAMIAN.ID, CharacterClass.PALADIN.ID, 0xFDAB7C),
		PALADIN_EUBANS(Character.EUBANS.ID, CharacterClass.PALADIN.ID, 0xFDABF4), // Hair override
		PALADIN_MAXIME(Character.MAXIME.ID, CharacterClass.PALADIN.ID, 0xFDAE10),
		PALADIN_PASCAL(Character.PASCAL.ID, CharacterClass.PALADIN.ID, 0xFDAE88), // Hair override
		
		PALADIN_ISADORA(Character.ISADORA.ID, CharacterClass.PALADIN_F.ID, 0xFDAA8C),
		
		PEGASUSKNIGHT_FARINA(Character.FARINA.ID, CharacterClass.PEGASUSKNIGHT.ID, 0xFDAF8C),
		PEGASUSKNIGHT_FIORA(Character.FIORA.ID, CharacterClass.PEGASUSKNIGHT.ID, 0xFDAFF8),
		PEGASUSKNIGHT_FLORINA(Character.FLORINA.ID, CharacterClass.PEGASUSKNIGHT.ID, 0xFDB064),
		
		PIRATE_DART(Character.DART.ID, CharacterClass.PIRATE.ID, 0xFDB0D4), // Needs hair override
		
		SAGE_AION(Character.AION.ID, CharacterClass.SAGE.ID, 0xFDB2D4),
		SAGE_PENT(Character.PENT.ID, CharacterClass.SAGE.ID, 0xFDB3B8),
		SAGE_ERK(Character.ERK.ID, CharacterClass.SAGE.ID, 0xFDB340),
		SAGE_NINO(Character.NINO.ID, CharacterClass.SAGE_F.ID, 0xFDB1FC),
		SAGE_SONIA(Character.SONIA.ID, CharacterClass.SAGE_F.ID, 0xFDB26C),
		UBER_SAGE_SONIA(Character.SONIA.ID, CharacterClass.UBER_SAGE.ID, 0xFDB26C), // Not sure when this shows up, actually, but it's there.
		UBER_SAGE_LIMSTELLA(Character.LIMSTELLA.ID, CharacterClass.UBER_SAGE.ID, 0xFDB194),
		
		SHAMAN_CANAS(Character.CANAS.ID, CharacterClass.SHAMAN.ID, 0xFDB430),
		SHAMAN_HEINTZ(Character.HEINTZ.ID, CharacterClass.SHAMAN.ID, 0xFDB484),
		SHAMAN_ZOLDAM(Character.ZOLDAM.ID, CharacterClass.SHAMAN.ID, 0xFDB4D4),
		
		SNIPER_WIL(Character.WIL.ID, CharacterClass.SNIPER.ID, 0xFDB644),
		SNIPER_DENNING(Character.DENNING.ID, CharacterClass.SNIPER.ID, 0xFDB5EC),
		SNIPER_LOUISE(Character.LOUISE.ID, CharacterClass.SNIPER_F.ID, 0xFDB524),
		SNIPER_REBECCA(Character.REBECCA.ID, CharacterClass.SNIPER_F.ID, 0xFDB588),
		
		SWORDMASTER_KARLA(Character.KARLA.ID, CharacterClass.SWORDMASTER_F.ID, 0xFDB8E4),
		SWORDMASTER_KAREL(Character.KAREL.ID, CharacterClass.SWORDMASTER.ID, 0xFDB9A8),
		SWORDMASTER_GUY(Character.GUY.ID, CharacterClass.SWORDMASTER.ID, 0xFDB944),
		SWORDMASTER_LLOYD(Character.LLOYD_FFO.ID, CharacterClass.SWORDMASTER.ID, 0xFDBA08),
		
		THIEF_MATTHEW(Character.MATTHEW.ID, CharacterClass.THIEF.ID, 0xFDBB0C),
		THIEF_LEGAULT(Character.LEGAULT.ID, CharacterClass.THIEF.ID, 0xFDBAA8),
		
		TROUBADOUR_PRISCILLA(Character.PRISCILLA.ID, CharacterClass.TROUBADOUR.ID, 0xFDBB74),
		
		VALKYRIE_PRISCILLA(Character.PRISCILLA.ID, CharacterClass.VALKYRIE.ID, 0xFDBBD0),
		VALKYRIE_URSULA(Character.URSULA.ID, CharacterClass.VALKYRIE.ID, 0xFDBC3C),
		
		WARRIOR_BARTRE(Character.BARTRE.ID, CharacterClass.WARRIOR.ID, 0xFDBCA0),
		WARRIOR_DORCAS(Character.DORCAS.ID, CharacterClass.WARRIOR.ID, 0xFDBD58),
		WARRIOR_GEITZ(Character.GEITZ.ID, CharacterClass.WARRIOR.ID, 0xFDBDB4), // Hair Override
		WARRIOR_BRENDAN(Character.BRENDAN.ID, CharacterClass.WARRIOR.ID, 0xFDBCFC), // Hair override
		WARRIOR_JASMINE(Character.JASMINE.ID, CharacterClass.WARRIOR.ID, 0xFDBE14),
		WARRIOR_OLEG(Character.OLEG.ID, CharacterClass.WARRIOR.ID, 0xFDBE70), // Hair override
		WARRIOR_PAUL(Character.PAUL.ID, CharacterClass.WARRIOR.ID, 0xFDBECC),
		
		WYVERNKNIGHT_HEATH(Character.HEATH.ID, CharacterClass.WYVERNKNIGHT.ID, 0xFD9D44), // May need hair override.
		
		WYVERNLORD_HEATH(Character.HEATH.ID, CharacterClass.WYVERNLORD.ID, 0xFD9DB4),
		WYVERNLORD_VAIDA(Character.VAIDA.ID, CharacterClass.WYVERNLORD_F.ID, 0xFD9E34); // Hair override
		
		int characterID;
		int classID;
		
		PaletteInfo info;
		
		static Map<Integer, Map<Integer, PaletteInfo>> classByCharacter = new HashMap<Integer, Map<Integer, PaletteInfo>>();
		static Map<Integer, Map<Integer, PaletteInfo>> charactersByClass = new HashMap<Integer, Map<Integer, PaletteInfo>>();
		static Map<Integer, PaletteInfo> defaultPaletteForClass = new HashMap<Integer, PaletteInfo>();
		
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
			}
			
			defaultPaletteForClass.put(CharacterClass.SOLDIER.ID, ARCHER_WIL.info); // No idea.
			defaultPaletteForClass.put(CharacterClass.ARCHER.ID, ARCHER_WIL.info);
			defaultPaletteForClass.put(CharacterClass.ARCHER_F.ID, ARCHER_REBECCA.info);
			defaultPaletteForClass.put(CharacterClass.BLADE_LORD.ID, BLADE_LORD_LYN.info);
			defaultPaletteForClass.put(CharacterClass.DANCER.ID, DANCER_NINIAN.info);
			defaultPaletteForClass.put(CharacterClass.LORD_LYN.ID, LORD_LYN.info);
			defaultPaletteForClass.put(CharacterClass.ASSASSIN.ID, ASSASSIN_JAFFAR.info);
			defaultPaletteForClass.put(CharacterClass.BRIGAND.ID, BRIGAND_BATTA.info);
			defaultPaletteForClass.put(CharacterClass.FIGHTER.ID, FIGHTER_DORCAS.info);
			defaultPaletteForClass.put(CharacterClass.BARD.ID, BARD_NILS.info);
			defaultPaletteForClass.put(CharacterClass.SNIPER.ID, SNIPER_WIL.info);
			defaultPaletteForClass.put(CharacterClass.SNIPER_F.ID, SNIPER_LOUISE.info);
			defaultPaletteForClass.put(CharacterClass.BERSERKER.ID, BERSERKER_DART.info);
			defaultPaletteForClass.put(CharacterClass.BISHOP.ID, BISHOP_RENAULT.info);
			defaultPaletteForClass.put(CharacterClass.BISHOP_F.ID, BISHOP_LUCIUS.info);
			defaultPaletteForClass.put(CharacterClass.CAVALIER.ID, CAVALIER_SAIN.info);
			defaultPaletteForClass.put(CharacterClass.CLERIC.ID, CLERIC_SERRA.info);
			defaultPaletteForClass.put(CharacterClass.MONK.ID, MONK_LUCIUS.info);
			defaultPaletteForClass.put(CharacterClass.MYRMIDON.ID, MYRMIDON_GUY.info);
			defaultPaletteForClass.put(CharacterClass.DRUID.ID, DRUID_CANAS.info);
			defaultPaletteForClass.put(CharacterClass.FALCONKNIGHT.ID, FALCONKNIGHT_FLORINA.info);
			defaultPaletteForClass.put(CharacterClass.GENERAL.ID, GENERAL_OSWIN.info);
			defaultPaletteForClass.put(CharacterClass.HERO.ID, HERO_HARKEN.info);
			defaultPaletteForClass.put(CharacterClass.GREAT_LORD.ID, GREAT_LORD_HECTOR.info);
			defaultPaletteForClass.put(CharacterClass.SWORDMASTER.ID, SWORDMASTER_KAREL.info);
			defaultPaletteForClass.put(CharacterClass.SWORDMASTER_F.ID, SWORDMASTER_KARLA.info);
			defaultPaletteForClass.put(CharacterClass.KNIGHT.ID, KNIGHT_OSWIN.info);
			defaultPaletteForClass.put(CharacterClass.LORD_ELIWOOD.ID, LORD_ELIWOOD.info);
			defaultPaletteForClass.put(CharacterClass.LORD_KNIGHT.ID, LORD_KNIGHT_ELIWOOD.info);
			defaultPaletteForClass.put(CharacterClass.LORD_HECTOR.ID, LORD_HECTOR.info);
			defaultPaletteForClass.put(CharacterClass.MAGE.ID, MAGE_ERK.info);
			defaultPaletteForClass.put(CharacterClass.MAGE_F.ID, MAGE_NINO.info);
			defaultPaletteForClass.put(CharacterClass.SAGE.ID, SAGE_ERK.info);
			defaultPaletteForClass.put(CharacterClass.SAGE_F.ID, SAGE_NINO.info);
			defaultPaletteForClass.put(CharacterClass.UBER_SAGE.ID, UBER_SAGE_LIMSTELLA.info);
			defaultPaletteForClass.put(CharacterClass.MERCENARY.ID, MERCENARY_RAVEN.info);
			defaultPaletteForClass.put(CharacterClass.NOMAD.ID, NOMAD_RATH.info);
			defaultPaletteForClass.put(CharacterClass.NOMADTROOPER.ID, NOMADTROOPER_RATH.info);
			defaultPaletteForClass.put(CharacterClass.PALADIN.ID, PALADIN_MARCUS.info);
			defaultPaletteForClass.put(CharacterClass.PALADIN_F.ID, PALADIN_ISADORA.info);
			defaultPaletteForClass.put(CharacterClass.PEGASUSKNIGHT.ID, PEGASUSKNIGHT_FLORINA.info);
			defaultPaletteForClass.put(CharacterClass.PIRATE.ID, PIRATE_DART.info);
			defaultPaletteForClass.put(CharacterClass.CORSAIR.ID, PIRATE_DART.info);
			defaultPaletteForClass.put(CharacterClass.SHAMAN.ID, SHAMAN_CANAS.info);
			defaultPaletteForClass.put(CharacterClass.THIEF.ID, THIEF_MATTHEW.info);
			defaultPaletteForClass.put(CharacterClass.TROUBADOUR.ID, TROUBADOUR_PRISCILLA.info);
			defaultPaletteForClass.put(CharacterClass.VALKYRIE.ID, VALKYRIE_PRISCILLA.info);
			defaultPaletteForClass.put(CharacterClass.WARRIOR.ID, WARRIOR_DORCAS.info);
			defaultPaletteForClass.put(CharacterClass.WYVERNKNIGHT.ID, WYVERNKNIGHT_HEATH.info);
			defaultPaletteForClass.put(CharacterClass.WYVERNLORD.ID, WYVERNLORD_HEATH.info);
			defaultPaletteForClass.put(CharacterClass.WYVERNLORD_F.ID, WYVERNLORD_VAIDA.info);
		}
		
		private Palette(int charID, int classID, long offset) {
			this.characterID = charID;
			this.classID = classID;
			CharacterClass charClass = CharacterClass.valueOf(classID);
			if (charClass != null) {
				switch (charClass) {
				case ARCHER:
				case ARCHER_F:
				case BLADE_LORD:
				case DANCER:
				case LORD_LYN:
					this.info = new PaletteInfo(classID, charID, offset, 16, 3, 32, 3);
					break;
				case ASSASSIN:
					if (charID == Character.LEGAULT.ID) { // Legault's unique animation ties hair and cape together with 6 colors.
						this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20, 16, 27, 29, 14}, new int[] {32, 34, 36}, new int[] {}); 
					} else if (charID == Character.JERME.ID) {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {16, 27, 14}, new int[] {32, 34, 36});
					} else {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {16, 27, 14}, new int[] {32, 34});
					}
					break;
				case BRIGAND:
				case FIGHTER:
					this.info = new PaletteInfo(classID, charID, offset, 18, 2, 32, 3);
					break;
				case BARD:
				case SNIPER:
				case SNIPER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34, 36}, new int[] {23, 25, 27});
					break;
				case BERSERKER:
					if (charID == Character.HAWKEYE.ID) {
						this.info = new PaletteInfo(classID, charID, offset, 14, 3, 32, 3);
					} else {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {32, 34, 36}, new int[] {});
					}
					break;
				case BISHOP:
					if (charID == Character.LUCIUS.ID) {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {20, 16, 18}, new int[] {23, 34, 36}, new int[] {29, 32});
					} else {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18}, new int[] {23, 34, 36}, new int[] {29, 32});
					}
					break;
				case BISHOP_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 16, 18}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case CAVALIER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 25, 27}, new int[] {11, 16});
					break;
				case CLERIC:
				case MONK: // May need to split out Lucius as a special case. This is assuming a Lucius sprite, which is unique from other monks.
				case MYRMIDON: // May need to split out Guy as a special case. This is assuming a Guy sprite, which is unique from other myrmidons.
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case DRUID:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 25, 27}, new int[] {29, 32, 34}, new int[] {16, 18, 20});
					break;
				case FALCONKNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {20, 18}, new int[] {25, 27, 36}, new int[] {16, 11, 9});
					break;
				case GENERAL:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {32, 34, 36}, new int[] {});
					break;
				case HERO:
				case GREAT_LORD:
				case SWORDMASTER:
				case SWORDMASTER_F:
					if (charID == Character.GUY.ID) {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34, 36}, new int[] {});
					} else if (charID == Character.LLOYD_FFO.ID) {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {32, 34, 36}, new int[] {});
					} else {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34}, new int[] {});
					}
					break;
				case KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {14, 16, 9, 11, 7}, new int[] {});
					break;
				case LORD_ELIWOOD:
				case LORD_KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 16, 20}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case LORD_HECTOR:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34}, new int[] {36, 14});
					break;
				case MAGE:
					this.info = new PaletteInfo(classID, charID, offset, 18, 2, 32, 3, 23, 3);
					break;
				case MAGE_F:
				case SAGE:
				case SAGE_F:
				case UBER_SAGE:
					this.info = new PaletteInfo(classID, charID, offset, 16, 3, 32, 3, 23, 3);
					break;
				case MERCENARY:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {25, 11, 36}, new int[] {});
					break;
				case NOMAD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20, 36}, new int[] {14, 16}, new int[] {23, 25, 27, 29}); // Secondary is Mount/Bow
					break;
				case NOMADTROOPER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {9, 16}, new int[] {23, 25, 27, 29});
					break;
				case PALADIN:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {23, 25, 27}, new int[] {16, 11, 14}, new int[] {18, 20}); // No hair. Armor primary, mane/insignia secondary, shield tertiary.
					break;
				case PALADIN_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {25, 27, 29}, new int[] {16, 11, 14}); // Hair matches shield in the female.
					break;
				case PEGASUSKNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {20, 18}, new int[] {25, 27, 36}, new int[] {29, 32, 34}, new int[] {16, 11}); // Armor Primary, Wing Secondary, Mane tertiary
					break;
				case CORSAIR:
				case PIRATE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {29, 32, 34, 36}, new int[] {}); // Outfit/Bandana is the only color.
					break;
				case SHAMAN:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 25, 27}, new int[] {29, 32, 34}, new int[] {9, 16, 18}); // Not really hair, but it matches up in the only one that matters (Canas)
					break;
				case THIEF:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {32, 29, 14}, new int[] {34, 36});
					break;
				case TROUBADOUR:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 16}, new int[] {25, 27, 29});
					break;
				case VALKYRIE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 25, 27, 36, 29}, new int[] {});
					break;
				case WARRIOR:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {32, 34, 36}, new int[] {20, 29}); // No Hair. Primary is pants/helmet color. Secondary is breastplate.
					break;
				case WYVERNKNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {27, 29, 32, 34}, new int[] {16, 14, 18}, new int[] {25}); // Primary is Wyvern Body, Secondary is Mount, Tertiary is Wyvern's Wingspan
					break;
				case WYVERNLORD:
				case WYVERNLORD_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {27, 29, 32, 34}, new int[] {7, 25}, new int[] {25});
					break;
				default:
					break;
				}
			}
		}
		
		public static PaletteInfo paletteForCharacterInClass(int characterID, int classID) {
			int canonicalID = Character.canonicalIDForCharacterID(characterID);
			return classByCharacter.get(canonicalID).get(classID);
		}
		
		public static PaletteInfo[] palettesForCharacter(int characterID) {
			int canonicalID = Character.canonicalIDForCharacterID(characterID);
			List<PaletteInfo> list = new ArrayList<PaletteInfo>(classByCharacter.get(canonicalID).values());
			return list.toArray(new PaletteInfo[list.size()]);
		}
		
		public static PaletteColor[] supplementaryHairColorForCharacter(int characterID) {
			Character character = Character.valueOf(characterID);
			switch (character) {
			case MARCUS:
				return new PaletteColor[] {new PaletteColor(208, 144, 216), new PaletteColor(168, 112, 168), new PaletteColor(112, 80, 120), new PaletteColor(88, 64, 96)};
			case OSWIN:
				return new PaletteColor[] {new PaletteColor(152, 144, 8), new PaletteColor(120, 112, 16), new PaletteColor(104, 80, 16)};
			case HEATH:
				return new PaletteColor[] {new PaletteColor(56, 184, 72), new PaletteColor(32, 144, 48)};
			case VAIDA:
				return new PaletteColor[] {new PaletteColor(248, 248, 112), new PaletteColor(216, 208, 64), new PaletteColor(176, 152, 64)};
			default:
				return null;
			}
		}
		
		public static PaletteInfo defaultPaletteForClass(int classID) {
			return defaultPaletteForClass.get(classID);
		}
	}
	
	// Character Provider Methods

	public long characterDataTablePointer() {
		return CharacterTablePointer;
	}

	public int numberOfCharacters() {
		return NumberOfCharacters;
	}

	public int bytesPerCharacter() {
		return BytesPerCharacter;
	}

	public GBAFECharacter[] allCharacters() {
		return Character.values();
	}

	public Map<Integer, GBAFECharacter> counters() {
		Map<Integer, GBAFECharacter> counterMap = new HashMap<Integer, GBAFECharacter>();
		counterMap.put(Character.BATTA.ID, Character.LYN_TUTORIAL);
		counterMap.put(Character.WIRE.ID, Character.HECTOR);
		return counterMap;
	}

	public Set<GBAFECharacter> allPlayableCharacters() {
		return new HashSet<GBAFECharacter>(Character.allPlayableCharacters);
	}

	public Set<GBAFECharacter> allBossCharacters() {
		return new HashSet<GBAFECharacter>(Character.allBossCharacters);
	}

	public Set<GBAFECharacter> linkedCharacters(int characterID) {
		return new HashSet<GBAFECharacter>(Character.allLinkedCharactersFor(Character.valueOf(characterID)));
	}
	
	public GBAFECharacter characterWithID(int characterID) {
		GBAFECharacter character = Character.valueOf(characterID);
		if (character == null) {
			character = Character.NONE;
		}
		
		return character;
	}

	public int[] affinityValues() {
		int[] values = new int[FE7Character.Affinity.values().length];
		int i = 0;
		for (FE7Character.Affinity affinity : FE7Character.Affinity.values()) {
			values[i++] = affinity.value;
		}
		
		return values;
	}

	public int canonicalID(int characterID) {
		return Character.canonicalIDForCharacterID(characterID);
	}

	public GBAFECharacterData characterDataWithData(byte[] data, long offset, Boolean hasLimitedClasses) {
		return new FE7Character(data, offset, hasLimitedClasses);
	}
	
	// Class Provider Methods

	public long classDataTablePointer() {
		return ClassTablePointer;
	}

	public int numberOfClasses() {
		return NumberOfClasses;
	}

	public int bytesPerClass() {
		return BytesPerClass;
	}

	public GBAFEClass[] allClasses() {
		return CharacterClass.values();
	}

	public Set<GBAFEClass> allValidClasses() {
		return new HashSet<GBAFEClass>(CharacterClass.allValidClasses);
	}

	public GBAFEClass classWithID(int classID) {
		return CharacterClass.valueOf(classID);
	}

	public Set<GBAFEClass> classesThatLoseToClass(GBAFEClass sourceClass, GBAFEClass winningClass, Map<String, Boolean> options) {
		Boolean excludeLords = options.get(GBAFEClassProvider.optionKeyExcludeLords);
		if (excludeLords == null) { excludeLords = false; }
		Boolean excludeThieves = options.get(GBAFEClassProvider.optionKeyExcludeThieves);
		if (excludeThieves == null) { excludeThieves = false; }
		return new HashSet<GBAFEClass>(CharacterClass.classesThatLoseToClass(CharacterClass.valueOf(sourceClass.getID()), 
				CharacterClass.valueOf(winningClass.getID()), excludeLords, excludeThieves));
	}

	public Set<GBAFEClass> targetClassesForRandomization(GBAFEClass sourceClass, Map<String, Boolean> options) {
		Boolean excludeLords = options.get(GBAFEClassProvider.optionKeyExcludeLords);
		if (excludeLords == null) { excludeLords = false; }
		Boolean excludeThieves = options.get(GBAFEClassProvider.optionKeyExcludeThieves);
		if (excludeThieves == null) { excludeThieves = false; }
		Boolean excludeSource = options.get(GBAFEClassProvider.optionKeyExcludeSource);
		if (excludeSource == null) { excludeSource = false; }
		Boolean requireAttack = options.get(GBAFEClassProvider.optionKeyRequireAttack);
		if (requireAttack == null) { requireAttack = false; }
		Boolean requiresRange = options.get(GBAFEClassProvider.optionKeyRequireRange);
		if (requiresRange == null) { requiresRange = false; }
		Boolean applyRestrictions = options.get(GBAFEClassProvider.optionKeyApplyRestrictions);
		if (applyRestrictions == null) { applyRestrictions = false; }
		
		return new HashSet<GBAFEClass>(CharacterClass.targetClassesForRandomization(CharacterClass.valueOf(sourceClass.getID()), 
				excludeSource, excludeLords, excludeThieves, requireAttack, requiresRange, applyRestrictions));
	}

	public void prepareForClassRandomization(Map<Integer, GBAFEClassData> classMap) {
		GBAFEClassData soldierData = classMap.get(CharacterClass.SOLDIER.ID);
		if (soldierData != null) {
			soldierData.setTargetPromotionID(CharacterClass.GENERAL.ID);
		}
	}

	public GBAFEClassData classDataWithData(byte[] data, long offset) {
		return new FE7Class(data, offset);
	}
	
	// Item Provider Methods

	public long itemTablePointer() {
		return ItemTablePointer;
	}

	public int numberOfItems() {
		return NumberOfItems;
	}

	public int bytesPerItem() {
		return BytesPerItem;
	}

	public GBAFEItem[] allItems() {
		return Item.values();
	}
	
	public GBAFEItem itemWithID(int itemID) {
		return Item.valueOf(itemID);
	}
	
	public GBAFEItem basicWeaponOfType(WeaponType type) {
		Set<Item> basicItems = Item.basicItemsOfType(type);
		return Collections.min(basicItems, Item.itemIDComparator());
	}
	
	public WeaponRank rankWithValue(int value) {
		Item.FE7WeaponRank fe7Rank = Item.FE7WeaponRank.valueOf(value);
		if (fe7Rank != null) { return fe7Rank.toGeneralRank(); }
		return WeaponRank.NONE;
	}
	
	public int rankValueForRank(WeaponRank rank) {
		Item.FE7WeaponRank fe7Rank = Item.FE7WeaponRank.rankFromGeneralRank(rank);
		if (fe7Rank != null) { return fe7Rank.value; }
		return 0;
	}
	
	public int getHighestWeaponRankValue() {
		return Item.FE7WeaponRank.S.value;
	}
	
	public Set<GBAFEItem> allWeapons() {
		return new HashSet<GBAFEItem>(Item.allWeapons);
	}
	
	public Set<GBAFEItem> weaponsWithStatBoosts() {
		return new HashSet<GBAFEItem>(Arrays.asList(
				Item.DURANDAL,
				Item.SOL_KATTI,
				Item.ARMADS,
				Item.FORBLAZE
				));
	}
	
	public Set<GBAFEItem> weaponsWithEffectiveness() {
		return new HashSet<GBAFEItem>(Arrays.asList(
				Item.RAPIER,
				Item.IRON_BOW,
				Item.HORSESLAYER,
				Item.HAMMER,
				Item.SWORDSLAYER,
				Item.DRAGON_AXE
				));
	}
	
	public Set<GBAFEItem> weaponsOfTypeUpToRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee) {
		if (type == Item.FE7WeaponType.DARK.toGeneralType() && rank == Item.FE7WeaponRank.E.toGeneralRank()) {
			rank = WeaponRank.D;
		}
		return new HashSet<GBAFEItem>(Item.weaponsOfTypeAndRank(type, WeaponRank.E, rank, rangedOnly));
	}
	
	public Set<GBAFEItem> weaponsOfTypeAndEqualRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee, Boolean allowLower) {
		if (type == Item.FE7WeaponType.DARK.toGeneralType() && rank == Item.FE7WeaponRank.E.toGeneralRank()) {
			rank = WeaponRank.D;
		}
		
		Set<Item> equalRankWeapons = Item.weaponsOfTypeAndRank(type, rank, rank, rangedOnly);
		if (equalRankWeapons.isEmpty() && allowLower) {
			return weaponsOfTypeUpToRank(type, rank, rangedOnly, requiresMelee);
		}
		
		return new HashSet<GBAFEItem>(equalRankWeapons);
	}
	
	public Set<GBAFEItem> prfWeaponsForClassID(int classID) {
		return new HashSet<GBAFEItem>(Item.prfWeaponsForClassID(classID));
	}
	
	public Set<GBAFEItem> allPotentialChestRewards() {
		return new HashSet<GBAFEItem>(Item.allPotentialRewards);
	}
	
	public Set<GBAFEItem> relatedItemsToItem(GBAFEItem item) {
		if (item == null) { return new HashSet<GBAFEItem>(); }
		
		Set<GBAFEItem> relatedItems;
		
		if (item.isWeapon()) {
			Set<GBAFEItem> relatedWeapons = new HashSet<GBAFEItem>();
			relatedWeapons.addAll(Item.weaponsOfRank(item.getRank()));
			relatedWeapons.addAll(Item.weaponsOfType(item.getType()));
			relatedWeapons.removeAll(Item.allSRank);
			relatedItems = relatedWeapons;
		} else if (item.isStatBooster()) {
			relatedItems = new HashSet<GBAFEItem>(Item.allStatBoosters);
		} else if (item.isPromotionItem()) {
			relatedItems = new HashSet<GBAFEItem>(Item.allPromotionItems);
		} else {
			relatedItems = new HashSet<GBAFEItem>();
		}
		
		relatedItems.remove(item);
		
		return relatedItems;
	}
	
	public Set<GBAFEItem> weaponsLockedToClass(int classID) {
		return new HashSet<GBAFEItem>(Item.lockedWeaponsToClassID(classID));
	}
	
	public String statBoostStringForWeapon(GBAFEItem weapon) {
		if (weapon == Item.DURANDAL) { return "+5 Strength"; }
		if (weapon == Item.SOL_KATTI) { return "+5 Resistance"; }
		if (weapon == Item.ARMADS) { return "+5 Defense"; }
		if (weapon == Item.FORBLAZE) { return "+5 Luck"; }
		
		return null;
	}
	
	public String effectivenessStringForWeapon(GBAFEItem weapon, Boolean shortString) {
		if (weapon == Item.RAPIER) { return shortString ? "Eff. Infantry" : "Effective against infantry"; }
		if (weapon == Item.IRON_BOW) { return shortString ? "Eff. Fliers" : "Effective against fliers"; }
		if (weapon == Item.HORSESLAYER) { return shortString? "Eff. Cavalry" : "Effective against cavalry"; }
		if (weapon == Item.HAMMER) { return shortString ? "Eff. Knights" : "Effective against knights"; }
		if (weapon == Item.SWORDSLAYER) { return shortString ? "Eff. Swordfighters" : "Effective against swordfighters"; }
		if (weapon == Item.DRAGON_AXE) { return shortString ? "Eff. Dragons" : "Effective against dragons"; }
		
		return null;
	}

	public GBAFEItemData itemDataWithData(byte[] data, long offset, int itemID) {
		return new FE7Item(data, offset);
	}

	public List<GBAFEClass> knightCavEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.CAVALIER,
				CharacterClass.CAVALIER_F,
				CharacterClass.PALADIN,
				CharacterClass.PALADIN_F,
				CharacterClass.KNIGHT,
				CharacterClass.KNIGHT_F,
				CharacterClass.GENERAL,
				CharacterClass.GENERAL_F,
				CharacterClass.NOMAD,
				CharacterClass.NOMAD_F,
				CharacterClass.NOMADTROOPER,
				CharacterClass.NOMADTROOPER_F,
				CharacterClass.TROUBADOUR,
				CharacterClass.VALKYRIE,
				CharacterClass.LORD_KNIGHT,
				CharacterClass.GREAT_LORD
				));
	}

	public List<GBAFEClass> knightEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.KNIGHT,
				CharacterClass.KNIGHT_F,
				CharacterClass.GENERAL,
				CharacterClass.GENERAL_F,
				CharacterClass.GREAT_LORD
				));
	}

	public List<GBAFEClass> cavalryEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.CAVALIER,
				CharacterClass.CAVALIER_F,
				CharacterClass.PALADIN,
				CharacterClass.PALADIN_F,
				CharacterClass.NOMAD,
				CharacterClass.NOMAD_F,
				CharacterClass.NOMADTROOPER,
				CharacterClass.NOMADTROOPER_F,
				CharacterClass.TROUBADOUR,
				CharacterClass.VALKYRIE,
				CharacterClass.LORD_KNIGHT
				));
	}

	public List<GBAFEClass> dragonEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.FIRE_DRAGON,
				CharacterClass.WYVERNKNIGHT,
				CharacterClass.WYVERNKNIGHT_F,
				CharacterClass.WYVERNLORD,
				CharacterClass.WYVERNLORD_F
				));
	}

	public List<GBAFEClass> flierEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.PEGASUSKNIGHT,
				CharacterClass.FALCONKNIGHT,
				CharacterClass.WYVERNKNIGHT,
				CharacterClass.WYVERNKNIGHT_F,
				CharacterClass.WYVERNLORD,
				CharacterClass.WYVERNLORD_F
				));
	}

	public List<GBAFEClass> myrmidonEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.MYRMIDON,
				CharacterClass.MYRMIDON_F,
				CharacterClass.SWORDMASTER,
				CharacterClass.SWORDMASTER_F,
				CharacterClass.MERCENARY,
				CharacterClass.MERCENARY_F,
				CharacterClass.HERO,
				CharacterClass.HERO_F,
				CharacterClass.BLADE_LORD
				));
	}

	public List<GBAFEClass> monsterEffectivenessClasses() {
		// Doesn't exist in FE7.
		return new ArrayList<GBAFEClass>();
	}

	public GBAFEPromotionItem[] allPromotionItems() {
		return PromotionItem.values();
	}

	public List<GBAFEClass> additionalClassesForPromotionItem(GBAFEPromotionItem promotionItem,
			List<Byte> existingClassIDs) {
		if (promotionItem == PromotionItem.KNIGHT_CREST) {
			return new ArrayList<GBAFEClass>(Arrays.asList(
					CharacterClass.SOLDIER,
					CharacterClass.LORD_ELIWOOD,
					CharacterClass.LORD_HECTOR
					));
		}
		if (promotionItem == PromotionItem.HERO_CREST) {
			return new ArrayList<GBAFEClass>(Arrays.asList(
					CharacterClass.LORD_LYN
					));
		}
		if (promotionItem == PromotionItem.MASTER_SEAL) {
			return new ArrayList<GBAFEClass>(Arrays.asList(
					CharacterClass.SOLDIER,
					CharacterClass.LORD_ELIWOOD,
					CharacterClass.LORD_HECTOR,
					CharacterClass.LORD_LYN
					));
		}
		
		return new ArrayList<GBAFEClass>();
	}

	public long spellAnimationTablePointer() {
		return SpellAnimationTablePointer;
	}

	public int numberOfAnimations() {
		return NumberOfSpellAnimations;
	}

	public int bytesPerAnimation() {
		return BytesPerSpellAnimation;
	}

	public GBAFESpellAnimationCollection spellAnimationCollectionAtAddress(byte[] data, long offset) {
		return new FE7SpellAnimationCollection(data, offset);
	}
	
}
