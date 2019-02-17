package fedata.gba.fe6;

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
import util.AddressRange;
import util.WhyDoesJavaNotHaveThese;

public class FE6Data implements GBAFECharacterProvider, GBAFEClassProvider, GBAFEItemProvider {
	public static final String FriendlyName = "ファイアーエムブレム　封印の剣";
	public static final String GameCode = "AFEJ";

	public static final long CleanCRC32 = 0xD38763E1L;
	public static final long CleanSize = 8388608;
	
	public static final long PrepatchedTranslationCRC32 = 0x99B8B6D7;
	public static final long PrepatchedSize = 16777532;
	
	public static final int NumberOfCharacters = 220;
	public static final int BytesPerCharacter = 48;
	public static final long CharacterTablePointer = 0x17680; // True in both prepatch and postpatch
	//public static final long DefaultCharacterTableAddress = 0x6076A0; 
	
	public static final int NumberOfClasses = 76;
	public static final int BytesPerClass = 72;
	public static final long ClassTablePointer = 0x176E0; // True in both prepatch and postpatch
	//public static final long DefaultClassTableAddress = 0x60A0E8;
	
	public static final int NumberOfItems = 128;
	public static final int BytesPerItem = 32;
	public static final long ItemTablePointer = 0x16410; // True in both prepatch and postpatch
	//public static final long DefaultItemTableAddress = 0x60B648;
	
	public static final int NumberOfSpellAnimations = 107;
	public static final int BytesPerSpellAnimation = 16;
	public static final long SpellAnimationTablePointer = 0x49DB4; // True in both prepatch and postpatch
	//public static final long DefaultSpellAnimationTableOffset = 0x662E4C;
	
	public static final int HuffmanTreeStart = 0x6E0; // Resolved once
	public static final int HuffmanTreeEnd = 0x6DC; // Resolved twice
	public static final long TextTablePointer = 0x13B10;
	//public static final long DefaultTextArrayOffset = 0xB808AC;
	public static final int NumberOfTextStrings = 0xD0D;
	
	public static final long ChapterTablePointer = 0x18A7C;
	//public static final long DefaultChapterArrayOffset = 0x664398;
	public static final int BytesPerChapterUnit = 16;
	
	public static final long PromotionItemTablePointer = 0x237AC; // Hero's Crest (0), Knights Crest (1), Orion Bolt (2), Elysian Whip (3), Guiding Ring (8)
	
	public static final long PaletteTableOffset = 0x7FC004L;
	public static final int PaletteEntryCount = 130;
	public static final int PaletteEntrySize = 16;
	
	// These are spaces confirmed free inside the natural ROM size (0xFFFFFF).
	// It's somewhat limited, so let's not use these unless we absolutely have to (like for palettes).
	// These are only valid when patched. The JP ROM does *not* have these.
	public static final List<AddressRange> InternalFreeRange = createFreeRangeList();
	private static final List<AddressRange> createFreeRangeList() {
		List<AddressRange> ranges = new ArrayList<AddressRange>();
		ranges.add(new AddressRange(0xA297B0L, 0xB00000L));
		ranges.add(new AddressRange(0xB013F0L, 0xFFFFFFL));
		return ranges;
	}
	
	private static final FE6Data sharedInstance = new FE6Data();
	
	public static final GBAFECharacterProvider characterProvider = sharedInstance;
	public static final GBAFEClassProvider classProvider = sharedInstance;
	public static final GBAFEItemProvider itemProvider = sharedInstance;
	
	public enum Character implements GBAFECharacter {
		NONE(0x00),
		
		ROY(0x01), CLARINE(0x02), FA(0x03), SHIN(0x04), SUE(0x05), DAYAN(0x06), BARTH(0x08), BORS(0x09), WENDY(0x0A), DOUGLAS(0x0B), 
		WOLT(0x0D), DOROTHY(0x0E), KLEIN(0x0F), SAUL(0x10), ELEN(0x11), YODEL(0x12), CHAD(0x14), KAREL(0x15), FIR(0x16), RUTGER(0x17), DIECK(0x18), 
		OUJAY(0x19), GARET(0x1A), ALAN(0x1B), LANCE(0x1C), PERCIVAL(0x1D), IGRENE(0x1E), MARCUS(0x1F), ASTOL(0x20), WARD(0x21), LOT(0x22), BARTRE(0x23), 
		LUGH(0x25), LILINA(0x26), HUGH(0x27), NIIME(0x28), REI(0x2A), LALAM(0x2B), YUNNO(0x2C), THITO(0x2E), 
		THANY(0x31), ZEISS(0x32), GALE(0x33), ELFIN(0x34), CASS(0x35), SOPHIA(0x36), MILEDY(0x37), GONZALES(0x38),
		NOAH(0x3A), TRECK(0x3B), ZEALOT(0x3C), ECHIDNA(0x3D), CECILIA(0x3F), GEESE(0x40), 
		
		MERLINUS(0x42), ELIWOOD(0x43), GUINEVERE(0x44), HECTOR(0xCF),
		
		KLEIN_UNIT(0xD8), TATE_UNIT(0xD9),
		
		DAYAN_NPC(0x07), DOUGLAS_NPC(0x0C), YODEL_NPC(0x13), BARTRE_NPC(0x24), NIIME_NPC(0x29), YUNNO_NPC(0x2D), THITO_NPC(0x30), GONZALES_NPC(0x39), ECHIDNA_NPC(0x3E), GEESE_NPC(0x40), 
		
		THITO_ENEMY(0x2F), 
		
		DAMAS(0x4A), RUDE(0x4B), SLATER(0x4C), ERIK(0x4D), DORY(0x4E), WAGNER(0x4F), DEVIAS(0x50), LEGYLANCE(0x51), SCOTT(0x52), NORD(0x53), FLAER(0x55), ORO(0x56), ROBARTS(0x57), AINE(0x58),
		NARSHEN(0x59), RANDY(0x5A), ROSE(0x5B), MAGGIE(0x5C), RAETH(0x5D), ARCARD(0x5E), MARTEL(0x5F), SIGUNE(0x60), ROARTZ(0x61), MURDOCK(0x62), BRUNYA(0x63), ZINC(0x68), MONKE(0x69), GEL(0x6A),
		SCOLLAN(0xB6), GRERO(0xB8), OHTZ(0xB9), TECK(0xBA), THORIL(0xBE), BRAKUL(0xBF), KUDOKA(0xC0), MARRAL(0xC1), KABUL(0xC2), CHAN(0xC3), PERETH(0xC4), WINDAM(0xC6), MORGAN(0xC8),
		
		ZEPHIEL(0x64), IDOUN(0x66), YAHN(0x67);
		
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
			case DAYAN_NPC: return DAYAN.ID;
			case DOUGLAS_NPC: return DOUGLAS.ID;
			case YODEL_NPC: return YODEL.ID;
			case BARTRE_NPC: return BARTRE.ID;
			case NIIME_NPC: return NIIME.ID;
			case YUNNO_NPC: return YUNNO.ID;
			case THITO_NPC:
			case THITO_ENEMY: return THITO.ID;
			case GONZALES_NPC: return GONZALES.ID;
			case ECHIDNA_NPC: return ECHIDNA.ID;
			case GEESE_NPC: return GEESE.ID;
			default: return characterID;
			}
		}
		
		public static Set<Character> allPlayableCharacters = new HashSet<Character>(Arrays.asList(ROY, CLARINE, FA, SHIN, SUE, DAYAN, BARTH, BORS, WENDY, DOUGLAS, WOLT, DOROTHY, KLEIN, SAUL, ELEN, YODEL,
				CHAD, KAREL, FIR, RUTGER, DIECK, OUJAY, GARET, ALAN, LANCE, PERCIVAL, IGRENE, MARCUS, ASTOL, WARD, LOT, BARTRE, LUGH, LILINA, HUGH, NIIME, REI, LALAM, YUNNO, THITO, THANY, ZEISS,
				ELFIN, CASS, SOPHIA, MILEDY, GONZALES, NOAH, TRECK, ZEALOT, ECHIDNA, CECILIA, GEESE));
		
		public static Set<Character> allBossCharacters = new HashSet<Character>(Arrays.asList(DAMAS, RUDE, SLATER, ERIK, DORY, WAGNER, DEVIAS, LEGYLANCE, SCOTT, NORD, FLAER, ORO, ROBARTS, AINE,
				NARSHEN, RANDY, ROSE, MAGGIE, RAETH, ARCARD, MARTEL, SIGUNE, ROARTZ, MURDOCK, BRUNYA, ZINC, MONKE, GEL, SCOLLAN, GRERO, OHTZ, TECK, THORIL, BRAKUL, KUDOKA, MARRAL, KABUL, CHAN, PERETH,
				WINDAM, MORGAN));
		
		public static Set<Character> restrictedClassCharacters = new HashSet<Character>(Arrays.asList(THITO, MILEDY, GALE, NARSHEN, ROY));
		
		public static Set<Character> allLords = new HashSet<Character>(Arrays.asList(ROY));
		public static Set<Character> allThieves = new HashSet<Character>(Arrays.asList(CHAD, ASTOL, CASS));
		public static Set<Character> doNotChange = new HashSet<Character>(Arrays.asList(ZEPHIEL, YAHN, IDOUN, MERLINUS));
		
		public static Set<Character> charactersThatRequireRange = new HashSet<Character>(Arrays.asList());
		public static Set<Character> charactersThatRequireMelee = new HashSet<Character>(Arrays.asList());
		
		public static Set<Character> requiredFliers = new HashSet<Character>(Arrays.asList(THITO, MILEDY, GALE, NARSHEN));
		public static Set<Character> requiredAttackers = new HashSet<Character>(Arrays.asList(ROY));
		
		public static Set<Character> femaleSet = new HashSet<Character>(Arrays.asList(CLARINE, FA, SUE, WENDY, DOROTHY, ELEN, FIR, IGRENE, LILINA, NIIME, LALAM, YUNNO, THITO, THANY, 
				CASS, SOPHIA, MILEDY, ECHIDNA, CECILIA, GUINEVERE, TATE_UNIT, NIIME_NPC, YUNNO_NPC, THITO_NPC, ECHIDNA_NPC, THITO_ENEMY, SIGUNE, BRUNYA, IDOUN));
		
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
			case DAYAN:
			case DAYAN_NPC:
				return new HashSet<Character>(Arrays.asList(DAYAN, DAYAN_NPC));
			case DOUGLAS:
			case DOUGLAS_NPC:
				return new HashSet<Character>(Arrays.asList(DOUGLAS, DOUGLAS_NPC));
			case YODEL:
			case YODEL_NPC:
				return new HashSet<Character>(Arrays.asList(YODEL, YODEL_NPC));
			case BARTRE:
			case BARTRE_NPC:
				return new HashSet<Character>(Arrays.asList(BARTRE, BARTRE_NPC));
			case NIIME:
			case NIIME_NPC:
				return new HashSet<Character>(Arrays.asList(NIIME, NIIME_NPC));
			case YUNNO:
			case YUNNO_NPC:
				return new HashSet<Character>(Arrays.asList(YUNNO, YUNNO_NPC));
			case THITO:
			case THITO_NPC:
			case THITO_ENEMY:
				return new HashSet<Character>(Arrays.asList(THITO, THITO_NPC, THITO_ENEMY));
			case GONZALES:
			case GONZALES_NPC:
				return new HashSet<Character>(Arrays.asList(GONZALES, GONZALES_NPC));
			case ECHIDNA:
			case ECHIDNA_NPC:
				return new HashSet<Character>(Arrays.asList(ECHIDNA, ECHIDNA_NPC));
			case GEESE:
			case GEESE_NPC:
				return new HashSet<Character>(Arrays.asList(GEESE, GEESE_NPC));
			default:
				return new HashSet<Character>(Arrays.asList(character));
			}
		}
	}
	
	public enum CharacterClass implements GBAFEClass {
		NONE(0x00),
		
		LORD(0x01), MERCENARY(0x02), MYRMIDON(0x06), FIGHTER(0x0A), KNIGHT(0x0C), ARCHER(0x10), PRIEST(0x14), MAGE(0x18), SHAMAN(0x1C),  
		CAVALIER(0x20), NOMAD(0x26), WYVERN_RIDER(0x2C), SOLDIER(0x30), BRIGAND(0x31), PIRATE(0x32),  THIEF(0x34), BARD(0x36),
		
		HERO(0x04), SWORDMASTER(0x08), WARRIOR(0x0B), GENERAL(0x0E), SNIPER(0x12), BISHOP(0x16), SAGE(0x1A), DRUID(0x1E), PALADIN(0x22),
		NOMAD_TROOPER(0x28), WYVERN_KNIGHT(0x2E), BERSERKER(0x33), MANAKETE(0x38), MASTER_LORD(0x43),
		
		MYRMIDON_F(0x07), KNIGHT_F(0x0D), ARCHER_F(0x11),  CLERIC(0x15), MAGE_F(0x19), SHAMAN_F(0x1D),  TROUBADOUR(0x24), NOMAD_F(0x27),
		PEGASUS_KNIGHT(0x2A), WYVERN_RIDER_F(0x2D), THIEF_F(0x35), DANCER(0x37),
		
		HERO_F(0x05), SWORDMASTER_F(0x09), GENERAL_F(0x0F), SNIPER_F(0x13), BISHOP_F(0x17), SAGE_F(0x1B), DRUID_F(0x1F), VALKYRIE(0x25),
		NOMAD_TROOPER_F(0x29), FALCON_KNIGHT(0x2B), WYVERN_KNIGHT_F(0x2F), MANAKETE_F(0x39),
		
		FIRE_DRAGON(0x3A), DIVINE_DRAGON(0x3B), MAGIC_DRAGON(0x3C),
		
		KING(0x3D),
		
		MERCENARY_F(0x03), CAVALIER_F(0x21), PALADIN_F(0x23);
		
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
		
		public static Comparator<CharacterClass> classIDComparator() {
			return new Comparator<CharacterClass>() { public int compare(CharacterClass o1, CharacterClass o2) { return Integer.compare(o1.ID, o2.ID); } };
		}
		
		public static int[] classIDsForClassArray(CharacterClass[] classArray) {
			int[] idArray = new int[classArray.length];
			for (int i = 0; i < classArray.length; i++) {
				idArray[i] = classArray[i].ID;
			}
			
			return idArray;
		}
		
		public int getID() {
			return ID;
		}
		
		public static Set<CharacterClass> allMaleClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, PRIEST, MAGE, SHAMAN, 
				CAVALIER, NOMAD, WYVERN_RIDER, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID, PALADIN, NOMAD_TROOPER, WYVERN_KNIGHT,
				BERSERKER, /*MANAKETE,*/ MASTER_LORD));
		public static Set<CharacterClass> allFemaleClasses = new HashSet<CharacterClass>(Arrays.asList(MYRMIDON_F, KNIGHT_F, ARCHER_F, CLERIC, MAGE_F, SHAMAN_F, TROUBADOUR, NOMAD_F, PEGASUS_KNIGHT, 
				WYVERN_RIDER_F, THIEF_F, DANCER, HERO_F, SWORDMASTER_F, GENERAL_F, SNIPER_F, BISHOP_F, SAGE_F, DRUID_F, VALKYRIE, NOMAD_TROOPER_F, FALCON_KNIGHT, WYVERN_KNIGHT_F, MANAKETE_F));
		public static Set<CharacterClass> allLordClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, MASTER_LORD));
		public static Set<CharacterClass> allThiefClasses = new HashSet<CharacterClass>(Arrays.asList(THIEF));
		public static Set<CharacterClass> allUnpromotedClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, PRIEST, MAGE, SHAMAN,
				CAVALIER, NOMAD, WYVERN_RIDER, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, MYRMIDON_F, KNIGHT_F, ARCHER_F, CLERIC, MAGE_F, SHAMAN_F, TROUBADOUR, NOMAD_F, PEGASUS_KNIGHT, WYVERN_RIDER_F, THIEF_F, DANCER));
		public static Set<CharacterClass> allPromotedClasses = new HashSet<CharacterClass>(Arrays.asList(MASTER_LORD, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID,
				PALADIN, NOMAD_TROOPER, WYVERN_KNIGHT, BERSERKER, /*MANAKETE,*/ MASTER_LORD, HERO_F, SWORDMASTER_F, GENERAL_F, SNIPER_F, BISHOP_F, SAGE_F, DRUID_F, VALKYRIE, NOMAD_TROOPER_F, FALCON_KNIGHT, WYVERN_KNIGHT_F,
				MANAKETE_F));
		public static Set<CharacterClass> allPacifistClasses = new HashSet<CharacterClass>(Arrays.asList(DANCER, BARD, PRIEST, CLERIC, TROUBADOUR));
		public static Set<CharacterClass> allMeleeLockedClasses = new HashSet<CharacterClass>(Arrays.asList(MYRMIDON, MERCENARY, LORD, THIEF));
		
		public static Set<CharacterClass> allValidClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, PRIEST, MAGE, SHAMAN, CAVALIER, NOMAD,
				WYVERN_RIDER, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID, PALADIN, NOMAD_TROOPER, WYVERN_KNIGHT,
				BERSERKER, /*MANAKETE,*/ MASTER_LORD, MYRMIDON_F, KNIGHT_F, ARCHER_F, CLERIC, MAGE_F, SHAMAN_F, TROUBADOUR, NOMAD_F, PEGASUS_KNIGHT, WYVERN_RIDER_F, THIEF_F, DANCER, HERO_F, SWORDMASTER_F, GENERAL_F, SNIPER_F,
				BISHOP_F, SAGE_F, DRUID_F, VALKYRIE, NOMAD_TROOPER_F, FALCON_KNIGHT, WYVERN_KNIGHT_F, MANAKETE_F));
		
		public static Set<CharacterClass> flyingClasses = new HashSet<CharacterClass>(Arrays.asList(WYVERN_KNIGHT, WYVERN_KNIGHT_F, WYVERN_RIDER, WYVERN_RIDER_F, PEGASUS_KNIGHT));
		
		// Includes most sword locks. Yes, they gain range with magic swords, but we're not going to assume they can use magic swords.
		public static Set<CharacterClass> meleeOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(LORD, MERCENARY, MYRMIDON, SWORDMASTER, MASTER_LORD, MYRMIDON_F, THIEF, THIEF_F, SWORDMASTER_F, MANAKETE_F));
		public static Set<CharacterClass> rangedOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(NOMAD, ARCHER, SNIPER, SNIPER_F, NOMAD_F));
		
		public static Map<CharacterClass, CharacterClass> promotionMap = createPromotionMap();
		private static Map<CharacterClass, CharacterClass> createPromotionMap() {
			Map<CharacterClass, CharacterClass> map = new HashMap<CharacterClass, CharacterClass>();
			map.put(LORD, MASTER_LORD);
			map.put(MERCENARY, HERO);
			map.put(MYRMIDON, SWORDMASTER);
			map.put(FIGHTER, WARRIOR);
			map.put(KNIGHT, GENERAL);
			map.put(ARCHER, SNIPER);
			map.put(PRIEST, BISHOP); 
			map.put(MAGE, SAGE);
			map.put(SHAMAN, DRUID);
			map.put(CAVALIER, PALADIN);
			map.put(NOMAD, NOMAD_TROOPER);
			map.put(WYVERN_RIDER, WYVERN_KNIGHT); 
			map.put(SOLDIER, GENERAL); 
			map.put(BRIGAND, BERSERKER);
			map.put(PIRATE, BERSERKER);
			map.put(MYRMIDON_F, SWORDMASTER_F); 
			map.put(KNIGHT_F, GENERAL_F);
			map.put(ARCHER_F, SNIPER_F);
			map.put(CLERIC, BISHOP_F);
			map.put(MAGE_F, SAGE_F);
			map.put(SHAMAN_F, DRUID_F);
			map.put(TROUBADOUR, VALKYRIE);
			map.put(NOMAD_F, NOMAD_TROOPER_F);
			map.put(PEGASUS_KNIGHT, FALCON_KNIGHT);
			map.put(WYVERN_RIDER_F, WYVERN_KNIGHT_F);
			
			return map;
		}
		
		private static Boolean isClassPromoted(CharacterClass sourceClass) {
			return allPromotedClasses.contains(sourceClass);
		}
		
		public static Set<CharacterClass> classesThatLoseToClass(CharacterClass originalClass, CharacterClass winningClass, Boolean excludeLords, Boolean excludeThieves) {
			Set<CharacterClass> classList = new HashSet<CharacterClass>();
			
			switch (winningClass) {
			case LORD:
			case MERCENARY:
			case MYRMIDON: 
			case MYRMIDON_F:
			case THIEF:
			case THIEF_F: {
				classList.add(FIGHTER);
				classList.add(BRIGAND);
				classList.add(PIRATE);
				break;
			}
			case FIGHTER:
			case BRIGAND:
			case PIRATE: {
				classList.add(KNIGHT);
				classList.add(KNIGHT_F);
				classList.add(SOLDIER);
				break;
			}
			case KNIGHT:
			case KNIGHT_F:
			case SOLDIER:
			case PEGASUS_KNIGHT: {
				classList.add(MYRMIDON);
				classList.add(MYRMIDON_F);
				classList.add(MERCENARY);
				classList.add(SOLDIER);
				if (!excludeLords) {
					classList.add(LORD);
				}
				if (!excludeThieves) {
					classList.add(THIEF);
					classList.add(THIEF_F);
				}
				break;
			}
			case MAGE:
			case MAGE_F: {
				classList.add(KNIGHT);
				classList.add(KNIGHT_F);
				classList.add(SOLDIER);
				break;
			}
			case SHAMAN:
			case SHAMAN_F: {
				classList.add(MAGE);
				classList.add(MAGE_F);
				classList.add(KNIGHT);
				classList.add(KNIGHT_F);
				classList.add(SOLDIER);
				break;
			}
			case ARCHER:
			case ARCHER_F:
			case NOMAD:
			case NOMAD_F: {
				classList.add(PEGASUS_KNIGHT);
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
			case LORD: // Special case for Roy to be able to always use swords (and have promotions)
				return new HashSet<CharacterClass>(Arrays.asList(LORD, MYRMIDON, MERCENARY, MYRMIDON_F, CAVALIER, PEGASUS_KNIGHT, NOMAD));
			case THIEF_F: // Temporary fix to make sure the game is playable. Thief AI tends to lock up the game if they're randomized to something else. The only relevent thief for us is Cath.
				return new HashSet<CharacterClass>(Arrays.asList(THIEF_F));
			case WYVERN_RIDER:
			case WYVERN_RIDER_F:
			case PEGASUS_KNIGHT:
				return new HashSet<CharacterClass>(Arrays.asList(WYVERN_RIDER, WYVERN_RIDER_F, PEGASUS_KNIGHT));
			case WYVERN_KNIGHT:
			case WYVERN_KNIGHT_F:
			case FALCON_KNIGHT:
				return new HashSet<CharacterClass>(Arrays.asList(WYVERN_KNIGHT, WYVERN_KNIGHT_F, FALCON_KNIGHT));
			case PIRATE:
				return new HashSet<CharacterClass>(Arrays.asList(PIRATE, WYVERN_RIDER, WYVERN_RIDER_F, PEGASUS_KNIGHT));
			case BRIGAND:
				return new HashSet<CharacterClass>(Arrays.asList(BRIGAND, WYVERN_RIDER, WYVERN_RIDER_F, PEGASUS_KNIGHT));
			case BERSERKER:
				return new HashSet<CharacterClass>(Arrays.asList(BERSERKER, WYVERN_KNIGHT, WYVERN_KNIGHT_F, FALCON_KNIGHT));
			default:
				return null;
			}
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
		
		IRON_SWORD(0x01), IRON_BLADE(0x02), STEEL_SWORD(0x03), SILVER_SWORD(0x04), SLIM_SWORD(0x05), POISON_SWORD(0x06), BRAVE_SWORD(0x07), LIGHT_BRAND(0x08),
		ARMORSLAYER(0x0A), RAPIER(0x0B), KILLING_EDGE(0x0C), LANCEREAVER(0x0D), WO_DAO(0x0E), STEEL_BLADE(0x72), SILVER_BLADE(0x73), AL_SWORD(0x74), WYRMSLAYER(0x78),
		RUNE_SWORD(0x7D),
		
		IRON_LANCE(0x10), STEEL_LANCE(0x11), SILVER_LANCE(0x12), SLIM_LANCE(0x13), POISON_LANCE(0x14), BRAVE_LANCE(0x15), JAVELIN(0x16), HORSESLAYER(0x18),
		KILLER_LANCE(0x19), AXEREAVER(0x1A), GANT_LANCE(0x75), SPEAR(0x7E),
		
		IRON_AXE(0x1B), STEEL_AXE(0x1C), SILVER_AXE(0x1D), POISON_AXE(0x1E), BRAVE_AXE(0x1F), HAND_AXE(0x20), HAMMER(0x22), KILLER_AXE(0x23), SWORDREAVER(0x24),
		DEVIL_AXE(0x25), HALBERD(0x26), TOMAHAWK(0x7F),
		
		IRON_BOW(0x27), STEEL_BOW(0x28), SILVER_BOW(0x29), POISON_BOW(0x2A), KILLER_BOW(0x2B), BRAVE_BOW(0x2C), SHORT_BOW(0x2D), LONGBOW(0x2E),
		
		FIRE(0x33), THUNDER(0x34), FIMBULVETR(0x35), ELFIRE(0x36), AIRCALIBUR(0x37), BOLTING(0x39),
		
		LIGHTNING(0x3B), DIVINE(0x3C), PURGE(0x3D),
		
		FLUX(0x3F), NOSFERATU(0x40), ECLIPSE(0x41), FENRIR(0x38),
		
		DURANDAL(0x09), MALTET(0x17), ARMADS(0x21), MURGLEIS(0x2F), FORBLAZE(0x3A), AUREOLA(0x3E), APOCALYPSE(0x42),
		
		HEAL(0x43), MEND(0x44), RECOVER(0x45), PHYSIC(0x46), FORTIFY(0x47), WARP(0x48), RESCUE(0x49), RESTORE(0x4A), SILENCE(0x4B), SLEEP(0x4C),
		TORCH_STAFF(0x4D), HAMMERNE(0x4E), BERSERK(0x50), UNLOCK(0x51), BARRIER(0x52), TINA_STAFF(0x76), HOLY_MAIDEN(0x77),
		
		FIRE_DRAGON_STONE(0x53), DIVINE_DRAGON_STONE(0x54), MAGIC_DRAGON_STONE(0x55),
		
		SECRET_BOOK(0x56), GODDESS_ICON(0x57), ANGELIC_ROBE(0x58), DRAGON_SHIELD(0x59), ENERGY_RING(0x5A), SPEEDWING(0x5B), TALISMAN(0x5C), BOOTS(0x5D), BODY_RING(0x5E),
		
		HERO_CREST(0x5F), KNIGHT_CREST(0x60), ORION_BOLT(0x61), ELYSIAN_WHIP(0x62), GUIDING_RING(0x63),
		
		CHEST_KEY_5(0x64), DOOR_KEY(0x65), LOCKPICK(0x67),
		
		VULNERARY(0x68), ELIXIR(0x69), PURE_WATER(0x6A), TORCH(0x6B), ANTITOXIN(0x6C),
		
		MEMBER_CARD(0x6D), SILVER_CARD(0x6E),
		
		WHITE_GEM(0x79), BLUE_GEM(0x7A), RED_GEM(0x7B),
		
		DELPHI_SHIELD(0x7C),
		
		BALLISTA(0x30), IRON_BALLISTA(0x31), KILLER_BALLISTA(0x32),
		
		BINDING_BLADE(0x0F), DARK_BREATH(0x70), ECKESACHS(0x71);
		
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
		
		public enum FE6WeaponRank {
			E(0x01), D(0x33), C(0x65), B(0x097), A(0x0C9), S(0x0FB);
			
			public int value;
			
			private static Map<Integer, FE6WeaponRank> map = new HashMap<Integer, FE6WeaponRank>();
			
			static {
				for (FE6WeaponRank rank : FE6WeaponRank.values()) {
					map.put(rank.value, rank);
				}
			}
			
			private FE6WeaponRank(final int value) { this.value = value; }
			
			public static FE6WeaponRank valueOf(int rankVal) {
				return map.get(rankVal);
			}
			
			public static FE6WeaponRank rankFromGeneralRank(WeaponRank generalRank) {
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
			
			public Boolean isHigherThanRank(FE6WeaponRank otherRank) {
				return (value & 0xFF) > (otherRank.value & 0xFF);
			}
			
			public Boolean isLowerThanRank(FE6WeaponRank otherRank) {
				return (value & 0xFF) < (otherRank.value & 0xFF);
			}
			
			public Boolean isSameRank(FE6WeaponRank otherRank) {
				return value == otherRank.value;
			}
		}
		
		public enum FE6WeaponType {
			SWORD(0x00), LANCE(0x01), AXE(0x02), BOW(0x03), STAFF(0x04), ANIMA(0x05), LIGHT(0x06), DARK(0x07), 
			
			ITEM(0x09), 
			
			DRAGONSTONE(0x0B), 
			
			DANCINGRING(0x0C);
			
			public int ID;
			
			private static Map<Integer, FE6WeaponType> map = new HashMap<Integer, FE6WeaponType>();
			
			static {
				for (FE6WeaponType type : FE6WeaponType.values()) {
					map.put(type.ID, type);
				}
			}
			
			private FE6WeaponType(final int id) { ID = id; }
			
			public static FE6WeaponType valueOf(int typeVal) {
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
			UNSELLABLE(0x10), BRAVE(0x20), MAGICDAMAGE(0x40), UNCOUNTERABLE(0x80);
			
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
			NONE(0x00), REVERSE_WEAPON_TRIANGLE(0x01), DRAGONSTONE_LOCK(0x04), LORD_LOCK(0x08),
			MYRMIDON_LOCK(0x10), KING_LOCK(0x20), IOTE_SHIELD_EFFECT(0x40);
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
		
		public enum WeaponEffect {
			NONE(0x00), POISON(0x01), STEALS_HP(0x02), REDUCE_TO_1HP(0x03), DEVIL(0x04);
			
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
		
		public static Set<Item> allSwords = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, RAPIER, AL_SWORD, POISON_SWORD, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, WO_DAO, STEEL_BLADE, KILLING_EDGE,
				WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, BRAVE_SWORD, SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, DURANDAL, ECKESACHS, BINDING_BLADE));
		public static Set<Item> allLances = new HashSet<Item>(Arrays.asList(IRON_LANCE, STEEL_LANCE, SILVER_LANCE, SLIM_LANCE, POISON_LANCE, BRAVE_LANCE, JAVELIN, MALTET, HORSESLAYER,
				KILLER_LANCE, AXEREAVER, GANT_LANCE, SPEAR));
		public static Set<Item> allAxes = new HashSet<Item>(Arrays.asList(IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, HAND_AXE, ARMADS, HAMMER, KILLER_AXE, SWORDREAVER, DEVIL_AXE,
				HALBERD, TOMAHAWK));
		public static Set<Item> allBows = new HashSet<Item>(Arrays.asList(IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, MURGLEIS));
		public static Set<Item> allAnima = new HashSet<Item>(Arrays.asList(FIRE, THUNDER, FIMBULVETR, ELFIRE, AIRCALIBUR, BOLTING, FORBLAZE));
		public static Set<Item> allLight = new HashSet<Item>(Arrays.asList(LIGHTNING, DIVINE, PURGE, AUREOLA));
		public static Set<Item> allDark = new HashSet<Item>(Arrays.asList(FLUX, NOSFERATU, ECLIPSE, FENRIR, APOCALYPSE));
		public static Set<Item> allHealingStaves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, PHYSIC, FORTIFY, TINA_STAFF, HOLY_MAIDEN));
		public static Set<Item> allSupportStaves = new HashSet<Item>(Arrays.asList(RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER));
		public static Set<Item> allStatusStaves = new HashSet<Item>(Arrays.asList(SILENCE, SLEEP, BERSERK));
		public static Set<Item> allStatBoosters = new HashSet<Item>(Arrays.asList(ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWING, GODDESS_ICON, DRAGON_SHIELD, TALISMAN, BOOTS, BODY_RING));
		public static Set<Item> allPromotionItems = new HashSet<Item>(Arrays.asList(HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP, GUIDING_RING));
		public static Set<Item> allSpecialItems = new HashSet<Item>(Arrays.asList(DELPHI_SHIELD, MEMBER_CARD, SILVER_CARD));
		public static Set<Item> allMoneyItems = new HashSet<Item>(Arrays.asList(WHITE_GEM, BLUE_GEM, RED_GEM));
		public static Set<Item> usableItems = new HashSet<Item>(Arrays.asList(CHEST_KEY_5, DOOR_KEY, LOCKPICK, VULNERARY, ELIXIR, PURE_WATER, ANTITOXIN, TORCH));
		
		public static Set<Item> allPotentialRewards = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, IRON_LANCE, SLIM_LANCE, JAVELIN, POISON_LANCE, HAND_AXE, IRON_AXE, STEEL_AXE,
				DEVIL_AXE, IRON_BOW, FIRE, LIGHTNING, HEAL, POISON_SWORD, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, WO_DAO, STEEL_LANCE, HORSESLAYER, POISON_AXE, HALBERD, HAMMER, POISON_BOW,
				SHORT_BOW, LONGBOW, STEEL_BOW, THUNDER, FLUX, MEND, TORCH_STAFF, UNLOCK, STEEL_BLADE, KILLING_EDGE, WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, KILLER_LANCE, AXEREAVER, KILLER_AXE, SWORDREAVER, 
				KILLER_BOW, ELFIRE, AIRCALIBUR, DIVINE, NOSFERATU, RECOVER, RESTORE, HAMMERNE, BARRIER, BRAVE_SWORD, BRAVE_LANCE, SPEAR, BRAVE_AXE, BRAVE_BOW, BOLTING, PURGE, ECLIPSE, PHYSIC, SILENCE, SLEEP, BERSERK, 
				RESCUE, SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, SILVER_LANCE, TOMAHAWK, SILVER_AXE, SILVER_BOW, FIMBULVETR, FENRIR, FORTIFY, WARP,
				AL_SWORD, GANT_LANCE, TINA_STAFF,
				ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWING, GODDESS_ICON, DRAGON_SHIELD, TALISMAN, BOOTS, BODY_RING,
				HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP, GUIDING_RING,
				DELPHI_SHIELD, MEMBER_CARD, SILVER_CARD,
				WHITE_GEM, BLUE_GEM, RED_GEM));
		
		public static Set<Item> allWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, RAPIER, AL_SWORD, POISON_SWORD, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, WO_DAO, STEEL_BLADE, KILLING_EDGE,
				WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, BRAVE_SWORD, SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, DURANDAL, ECKESACHS, BINDING_BLADE, IRON_LANCE, STEEL_LANCE, SILVER_LANCE, SLIM_LANCE, POISON_LANCE, 
				BRAVE_LANCE, JAVELIN, MALTET, HORSESLAYER, KILLER_LANCE, AXEREAVER, GANT_LANCE, SPEAR, IRON_AXE, STEEL_AXE, SILVER_AXE, POISON_AXE, BRAVE_AXE, HAND_AXE, ARMADS, HAMMER, KILLER_AXE, SWORDREAVER, DEVIL_AXE,
				HALBERD, TOMAHAWK, IRON_BOW, STEEL_BOW, SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, MURGLEIS, FIRE, THUNDER, FIMBULVETR, ELFIRE, AIRCALIBUR, BOLTING, FORBLAZE,
				LIGHTNING, DIVINE, PURGE, AUREOLA, FLUX, NOSFERATU, ECLIPSE, FENRIR, APOCALYPSE));
		public static Set<Item> allRangedWeapons = new HashSet<Item>(Arrays.asList(LIGHT_BRAND, RUNE_SWORD, ECKESACHS, BINDING_BLADE, JAVELIN, SPEAR, HAND_AXE, TOMAHAWK, IRON_BOW, STEEL_BOW,
				SILVER_BOW, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, MURGLEIS, FIRE, THUNDER, ELFIRE, FIMBULVETR, FORBLAZE, AIRCALIBUR, LIGHTNING, DIVINE, AUREOLA, FLUX, NOSFERATU, FENRIR, APOCALYPSE));
		public static Set<Item> allStaves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, PHYSIC, FORTIFY, RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER, SILENCE, SLEEP, BERSERK, TINA_STAFF, HOLY_MAIDEN));
		
		public static Set<Item> allSiegeTomes = new HashSet<Item>(Arrays.asList(BOLTING, PURGE, ECLIPSE));
		
		public static Set<Item> allERank = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, IRON_LANCE, SLIM_LANCE, JAVELIN, POISON_LANCE, HAND_AXE, IRON_AXE, STEEL_AXE, DEVIL_AXE, IRON_BOW, FIRE, LIGHTNING, HEAL, TINA_STAFF));
		public static Set<Item> allDRank = new HashSet<Item>(Arrays.asList(AL_SWORD, POISON_SWORD, STEEL_SWORD, IRON_BLADE, ARMORSLAYER, WO_DAO, STEEL_LANCE, GANT_LANCE, HORSESLAYER, POISON_AXE, HALBERD, HAMMER, POISON_BOW,
				SHORT_BOW, LONGBOW, STEEL_BOW, THUNDER, FLUX, MEND, TORCH_STAFF, UNLOCK));
		public static Set<Item> allCRank = new HashSet<Item>(Arrays.asList(STEEL_BLADE, KILLING_EDGE, WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, KILLER_LANCE, AXEREAVER, KILLER_AXE, SWORDREAVER, 
				KILLER_BOW, ELFIRE, DIVINE, NOSFERATU, RECOVER, RESTORE, HAMMERNE, BARRIER));
		public static Set<Item> allBRank = new HashSet<Item>(Arrays.asList(BRAVE_SWORD, BRAVE_LANCE, SPEAR, BRAVE_AXE, BRAVE_BOW, AIRCALIBUR, ECLIPSE, PHYSIC, SILENCE, SLEEP, BERSERK, RESCUE));
		public static Set<Item> allARank = new HashSet<Item>(Arrays.asList(SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, SILVER_LANCE, TOMAHAWK, SILVER_AXE, SILVER_BOW, FIMBULVETR, BOLTING, PURGE, FENRIR, FORTIFY, WARP));
		public static Set<Item> allSRank = new HashSet<Item>(Arrays.asList(DURANDAL, MALTET, ARMADS, MURGLEIS, FORBLAZE, APOCALYPSE, HOLY_MAIDEN, AUREOLA));
		public static Set<Item> allPrfRank = new HashSet<Item>(Arrays.asList(RAPIER, BINDING_BLADE, ECKESACHS));
		
		public static Set<Item> normalSet = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, AL_SWORD, STEEL_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_SWORD, SILVER_BLADE, IRON_LANCE, STEEL_LANCE, SILVER_LANCE, SLIM_LANCE, 
				GANT_LANCE, IRON_AXE, STEEL_AXE, SILVER_AXE, IRON_BOW, STEEL_BOW, SILVER_BOW, FIRE, THUNDER, FIMBULVETR, ELFIRE, LIGHTNING, DIVINE, FLUX, FENRIR, HEAL, MEND, RECOVER, TINA_STAFF));
		public static Set<Item> interestingSet = new HashSet<Item>(Arrays.asList(RAPIER, POISON_SWORD, ARMORSLAYER, WO_DAO, KILLING_EDGE, WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, BRAVE_SWORD, RUNE_SWORD, POISON_LANCE, 
				BRAVE_LANCE, JAVELIN, HORSESLAYER, KILLER_LANCE, AXEREAVER, SPEAR, POISON_AXE, BRAVE_AXE, HAND_AXE, HAMMER, KILLER_AXE, SWORDREAVER, DEVIL_AXE,
				HALBERD, TOMAHAWK, POISON_BOW, KILLER_BOW, BRAVE_BOW, SHORT_BOW, LONGBOW, AIRCALIBUR, BOLTING, PURGE, NOSFERATU, ECLIPSE, PHYSIC, FORTIFY, RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER,
				SILENCE, SLEEP, BERSERK, HOLY_MAIDEN));
		
		// These must be of lower rank than the siege tomes set, and each weapon type needs to have an equivalent analogue.
		public static Set<Item> siegeReplacementSet = new HashSet<Item>(Arrays.asList(NOSFERATU, DIVINE, ELFIRE));
		
		public static Set<Item> killerSet = new HashSet<Item>(Arrays.asList(KILLING_EDGE, WO_DAO, KILLER_LANCE, KILLER_AXE, KILLER_BOW));
		public static Set<Item> effectiveSet = new HashSet<Item>(Arrays.asList(ARMORSLAYER, WYRMSLAYER, RAPIER, HORSESLAYER, HAMMER, HALBERD, AIRCALIBUR));
		public static Set<Item> poisonSet = new HashSet<Item>(Arrays.asList(POISON_SWORD, POISON_LANCE, POISON_AXE, POISON_BOW));
		public static Set<Item> rangedSet = new HashSet<Item>(Arrays.asList(LIGHT_BRAND, RUNE_SWORD, JAVELIN, SPEAR, HAND_AXE, TOMAHAWK, LONGBOW, BOLTING, PURGE, ECLIPSE, PHYSIC));
		public static Set<Item> reaverSet = new HashSet<Item>(Arrays.asList(LANCEREAVER, AXEREAVER, SWORDREAVER));
		public static Set<Item> braveSet = new HashSet<Item>(Arrays.asList(BRAVE_SWORD, BRAVE_LANCE, BRAVE_AXE, BRAVE_BOW));
		
		public static Set<Item> allRestrictedWeapons = new HashSet<Item>(Arrays.asList(WO_DAO));
		
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
			if (classID == FE6Data.CharacterClass.MANAKETE.ID) {
				return new HashSet<Item>(Arrays.asList(FIRE_DRAGON_STONE));
			} else if (classID == FE6Data.CharacterClass.MANAKETE_F.ID) {
				return new HashSet<Item>(Arrays.asList(DIVINE_DRAGON_STONE));
			} else if (classID == FE6Data.CharacterClass.THIEF.ID || classID == FE6Data.CharacterClass.THIEF_F.ID) {
				return new HashSet<Item>(Arrays.asList(LOCKPICK));
			} else if (classID == FE6Data.CharacterClass.DANCER.ID || classID == FE6Data.CharacterClass.BARD.ID) {
				return new HashSet<Item>(Arrays.asList(ELIXIR));
			}
			
			return null;
		}
		
		public static Set<Item> prfWeaponsForClassID(int classID) {
			if (classID == FE6Data.CharacterClass.LORD.ID || classID == FE6Data.CharacterClass.MASTER_LORD.ID) {
				return new HashSet<Item>(Arrays.asList(RAPIER));
			}
			
			return null;
		}
		
		public static Set<Item> lockedWeaponsToClassID(int classID) {
			if (classID == FE6Data.CharacterClass.MYRMIDON.ID || classID == FE6Data.CharacterClass.MYRMIDON_F.ID ||
					classID == FE6Data.CharacterClass.SWORDMASTER.ID || classID == FE6Data.CharacterClass.SWORDMASTER_F.ID) {
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
			
			if (min == WeaponRank.NONE && max == WeaponRank.NONE) {
				return null;
			}
			
			if (min == null || max == null) {
				return null;
			}
			
			FE6WeaponRank minRank = FE6WeaponRank.E;
			if (min != WeaponRank.NONE) {
				minRank = FE6WeaponRank.rankFromGeneralRank(min);
			}
			
			FE6WeaponRank maxRank = FE6WeaponRank.S;
			if (max != WeaponRank.NONE) {
				maxRank = FE6WeaponRank.rankFromGeneralRank(max);
			}
			
			if (minRank.isHigherThanRank(maxRank)) {
				return null;
			}
			
			Set<Item> list = new HashSet<Item>();
			
			list.addAll(weaponsOfType(type));
			
			if (FE6WeaponRank.E.isLowerThanRank(minRank)) {
				list.removeAll(allERank);
			}
			if (FE6WeaponRank.D.isLowerThanRank(minRank)) {
				list.removeAll(allDRank);
			}
			if (FE6WeaponRank.C.isLowerThanRank(minRank)) {
				list.removeAll(allCRank);
			}
			if (FE6WeaponRank.B.isLowerThanRank(minRank)) {
				list.removeAll(allBRank);
			}
			if (FE6WeaponRank.A.isLowerThanRank(minRank)) {
				list.removeAll(allARank);
			}
			
			list.removeAll(allPrfRank);
			list.remove(WO_DAO); // This one is special. It must be added in only if we're certain the class asking for the item can use it.
			
			if (FE6WeaponRank.S.isHigherThanRank(maxRank)) {
				list.removeAll(allSRank);
			}
			if (FE6WeaponRank.A.isHigherThanRank(maxRank)) {
				list.removeAll(allARank);
			}
			if (FE6WeaponRank.B.isHigherThanRank(maxRank)) {
				list.removeAll(allBRank);
			}
			if (FE6WeaponRank.C.isHigherThanRank(maxRank)) {
				list.removeAll(allCRank);
			}
			if (FE6WeaponRank.D.isHigherThanRank(maxRank)) {
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
		CHAPTER_1(0x0C), CHAPTER_2(0x10), CHAPTER_3(0x17), CHAPTER_4(0x1B), CHAPTER_5(0x1F), CHAPTER_6(0x26), CHAPTER_7(0x2A),
		CHAPTER_8(0x31), CHAPTER_9(0x38), 
		
		CHAPTER_10A(0x3C), CHAPTER_11A(0x43),
		CHAPTER_10B(0x9A), CHAPTER_11B(0x9E),
		
		CHAPTER_12(0x4A), CHAPTER_13(0x51), CHAPTER_14(0x57), CHAPTER_15(0x5B), CHAPTER_16(0x60),
		
		CHAPTER_17A(0x64), CHAPTER_18A(0x6D), CHAPTER_19A(0x71), CHAPTER_20A(0x78),
		CHAPTER_17B(0xA3), CHAPTER_18B(0xA9), CHAPTER_19B(0xAF), CHAPTER_20B(0xB6),
		
		CHAPTER_21(0x7F), CHAPTER_22(0x88), CHAPTER_23(0x8C), CHAPTER_24(0x93),
		
		CHAPTER_8X(0xBB), CHAPTER_12X(0xC1), CHAPTER_14X(0xC7), CHAPTER_16X(0xCB), CHAPTER_20AX(0xD0), CHAPTER_20BX(0xD3), CHAPTER_21X(0xD8),
		
		CHAPTER_FINAL(0x97);
		
		public int chapterID;
		
		private ChapterPointer(int chapterID) {
			this.chapterID = chapterID;
		}
		
		public FE6Data.CharacterClass[] blacklistedClasses() {
			switch(this) {
			default:
				return new FE6Data.CharacterClass[] {};
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
			default:
				return false;
			}
		}
	}
	
	public enum PromotionItem implements GBAFEPromotionItem {
		// Hero's Crest (0), Knights Crest (1), Orion Bolt (2), Elysian Whip (3), Guiding Ring (8)
		// These point directly to the class list.
		// The class IDs are 00 terminated.
		HERO_CREST(0x00), KNIGHT_CREST(0x01), ORION_BOLT(0x02), ELYSIAN_WHIP(0x03), GUIDING_RING(0x08);
		
		int offset;
		
		private PromotionItem(final int offset) {
			this.offset = offset;
		}
		
		public long getListAddress() {
			return (offset * 8) + PromotionItemTablePointer + 4;
		}
		
		public Boolean isIndirected() {
			return false;
		}
		
		public String itemName() {
			return this.toString();
		}
	}
	
	public enum Palette {
		
		LORD_ROY(0x01, Character.ROY.ID, CharacterClass.LORD.ID, 0x7FC800),
		
		ARCHER_WOLT(0x02, Character.WOLT.ID, CharacterClass.ARCHER.ID, 0x7FC858),
		ARCHER_DOROTHY(0x03, Character.DOROTHY.ID, CharacterClass.ARCHER_F.ID, 0x7FC8B8),
		
		BERSERKER_GEESE(0x04, Character.GEESE.ID, CharacterClass.BERSERKER.ID, 0x7FC91C),
		BERSERKER_GONZALES(0x05, Character.GONZALES.ID, CharacterClass.BERSERKER.ID, 0x7FC974),
		BERSERKER_GARET(0x0F, Character.GARET.ID, CharacterClass.BERSERKER.ID, 0x7FCD74),
		// Scott has no palettes :(
		// Rose has no palettes :(
		// Maggie has no palettes :(
		
		KNIGHT_BORS(0x06, Character.BORS.ID, CharacterClass.KNIGHT.ID, 0x7FC9CC),
		KNIGHT_BARTH(0x07, Character.BARTH.ID, CharacterClass.KNIGHT.ID, 0x7FCA4C),
		KNIGHT_WENDY(0x08, Character.WENDY.ID, CharacterClass.KNIGHT_F.ID, 0x7FCAC0),
		KNIGHT_DEVIAS(0x56, Character.DEVIAS.ID, CharacterClass.KNIGHT.ID, 0x7FEA24),
		KNIGHT_RUDE(0x57, Character.RUDE.ID, CharacterClass.KNIGHT.ID, 0x7FEA7C),
		KNIGHT_SLATER(0x58, Character.SLATER.ID, CharacterClass.KNIGHT.ID, 0x7FEAD8),
		
		BRIGAND_GONZALES(0x09, Character.GONZALES.ID, CharacterClass.BRIGAND.ID, 0x7FCB38),
		BRIGAND_DORY(0x5A, Character.DORY.ID, CharacterClass.BRIGAND.ID, 0x7FEB80),
		BRIGAND_DAMAS(0x59, Character.DAMAS.ID, CharacterClass.BRIGAND.ID, 0x7FEB30), // FE6 is so busted. The classes in the class table are straight up wrong for some characters.
		
		BISHOP_ELEN(0x0A, Character.ELEN.ID, CharacterClass.BISHOP_F.ID, 0x7FCB90),
		BISHOP_YODEL(0x0B, Character.YODEL.ID, CharacterClass.BISHOP.ID, 0x7FCBF8),
		BISHOP_SAUL(0x0C, Character.SAUL.ID, CharacterClass.BISHOP.ID, 0x7FCC5C),
		// Oro has no palettes :(
		// Martel has no palettes :(
		
		HERO_ECHIDNA(0x0D, Character.ECHIDNA.ID, CharacterClass.HERO_F.ID, 0x7FCCC0),
		HERO_DIECK(0x0E, Character.DIECK.ID, CharacterClass.HERO.ID, 0x7FCD18),
		HERO_OUJAY(0x10, Character.OUJAY.ID, CharacterClass.HERO.ID, 0x7FCDCC),
		// Randy has no palettes :(
		
		BARD_ELFIN(0x11, Character.ELFIN.ID, CharacterClass.BARD.ID, 0x7FCE28),
		DANCER_LALAM(0x12, Character.LALAM.ID, CharacterClass.DANCER.ID, 0x7FCE88),
		
		WYVERN_RIDER_MILEDY(0x13, Character.MILEDY.ID, CharacterClass.WYVERN_RIDER_F.ID, 0x7FCEE0),
		WYVERN_RIDER_ZEISS(0x0, Character.ZEISS.ID, CharacterClass.WYVERN_RIDER.ID, 0x77144C), // This one is using the sprite colors.
		
		DRUID_REI(0x14, Character.REI.ID, CharacterClass.DRUID.ID, 0x7FCF60),
		DRUID_NIIME(0x15, Character.NIIME.ID, CharacterClass.DRUID_F.ID, 0x7FCFBC),
		DRUID_SOPHIA(0x5F, Character.SOPHIA.ID, CharacterClass.DRUID_F.ID, 0x7FEDB0),
		// Nord has no palettes. :(
		
		FALCON_KNIGHT_YUNNO(0x16, Character.YUNNO.ID, CharacterClass.FALCON_KNIGHT.ID, 0x7FD01C),
		FALCON_KNIGHT_THANY(0x17, Character.THANY.ID, CharacterClass.FALCON_KNIGHT.ID, 0x7FD07C),
		FALCON_KNIGHT_THITO(0x18, Character.THITO.ID, CharacterClass.FALCON_KNIGHT.ID, 0x7FD0DC),
		// Sigune has no palettes :(
		
		FIGHTER_BARTRE(0x19, Character.BARTRE.ID, CharacterClass.FIGHTER.ID, 0x7FD13C), // Not used.
		FIGHTER_LOT(0x1A, Character.LOT.ID, CharacterClass.FIGHTER.ID, 0x7FD194),
		FIGHTER_WARD(0x1B, Character.WARD.ID, CharacterClass.FIGHTER.ID, 0x7FD1EC),
		FIGHTER_DAMAS(0x59, Character.DAMAS.ID, CharacterClass.FIGHTER.ID, 0x7FEB30),
		
		MAGE_LILINA(0x1C, Character.LILINA.ID, CharacterClass.MAGE_F.ID, 0x7FD244),
		MAGE_HUGH(0x1D, Character.HUGH.ID, CharacterClass.MAGE.ID, 0x7FD2A0),
		MAGE_LUGH(0x1E, Character.LUGH.ID, CharacterClass.MAGE.ID, 0x7FD304),
		
		MERCENARY_DIECK(0x1F, Character.DIECK.ID, CharacterClass.MERCENARY.ID, 0x7FD36C),
		MERCENARY_OUJAY(0x20, Character.OUJAY.ID, CharacterClass.MERCENARY.ID, 0x7FD3C0),
		
		MYRMIDON_FIR(0x21, Character.FIR.ID, CharacterClass.MYRMIDON_F.ID, 0x7FD41C),
		MYRMIDON_RUTGER(0x22, Character.RUTGER.ID, CharacterClass.MYRMIDON.ID, 0x7FD47C),
		
		NOMAD_SUE(0x23, Character.SUE.ID, CharacterClass.NOMAD_F.ID, 0x7FD4DC),
		NOMAD_SHIN(0x24, Character.SHIN.ID, CharacterClass.NOMAD.ID, 0x7FD540),
		
		NOMAD_TROOPER_SUE(0x25, Character.SUE.ID, CharacterClass.NOMAD_TROOPER_F.ID, 0x7FD5B0),
		NOMAD_TROOPER_SHIN(0x26, Character.SHIN.ID, CharacterClass.NOMAD_TROOPER.ID, 0x7FD634),
		NOMAD_TROOPER_DAYAN(0x27, Character.DAYAN.ID, CharacterClass.NOMAD_TROOPER.ID, 0x7FD6B8),
		
		PALADIN_ALAN(0x28, Character.ALAN.ID, CharacterClass.PALADIN.ID, 0x7FD740),
		PALADIN_LANCE(0x2A, Character.LANCE.ID, CharacterClass.PALADIN.ID, 0x7FD828),
		PALADIN_MARCUS(0x2B, Character.MARCUS.ID, CharacterClass.PALADIN.ID, 0x7FD8B4),
		PALADIN_NOAH(0x2C, Character.NOAH.ID, CharacterClass.PALADIN.ID, 0x7FD940),
		PALADIN_PERCIVAL(0x2D, Character.PERCIVAL.ID, CharacterClass.PALADIN.ID, 0x7FD9D4),
		PALADIN_TRECK(0x2E, Character.TRECK.ID, CharacterClass.PALADIN.ID, 0x7FDA50),
		PALADIN_ZEALOT(0x2F, Character.ZEALOT.ID, CharacterClass.PALADIN.ID, 0x7FDAC8),
		// Robarts has no palettes :(
		// Arcard has no palettes :(
		
		PEGASUS_KNIGHT_YUNNO(0x30, Character.YUNNO.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0x7FDB48), // Not used.
		PEGASUS_KNIGHT_THANY(0x31, Character.THANY.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0x7FDBA8),
		PEGASUS_KNIGHT_THITO(0x32, Character.THITO.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0x7FDC08),
		
		PIRATE_GEESE(0x33, Character.GEESE.ID, CharacterClass.PIRATE.ID, 0x7FDC68),
		
		CLERIC_ELEN(0x34, Character.ELEN.ID, CharacterClass.CLERIC.ID, 0x7FDCD8),
		PRIEST_SAUL(0x35, Character.SAUL.ID, CharacterClass.PRIEST.ID, 0x7FDD34),
		
		CAVALIER_ALAN(0x36, Character.ALAN.ID, CharacterClass.CAVALIER.ID, 0x7FDD88),
		CAVALIER_LANCE(0x37, Character.LANCE.ID, CharacterClass.CAVALIER.ID, 0x7FDE00),
		CAVALIER_NOAH(0x38, Character.NOAH.ID, CharacterClass.CAVALIER.ID, 0x7FDE78),
		CAVALIER_TRECK(0x3A, Character.TRECK.ID, CharacterClass.CAVALIER.ID, 0x7FDF40),
		CAVALIER_ERIK(0x39, Character.ERIK.ID, CharacterClass.CAVALIER.ID, 0x7FDEF0),
		
		SAGE_LILINA(0x3B, Character.LILINA.ID, CharacterClass.SAGE_F.ID, 0x7FDFB8),
		SAGE_HUGH(0x3C, Character.HUGH.ID, CharacterClass.SAGE.ID, 0x7FE014),
		SAGE_LUGH(0x65, Character.LUGH.ID, CharacterClass.SAGE.ID, 0x7FEFF0),
		SAGE_BRUNYA(0x4B, Character.BRUNYA.ID, CharacterClass.SAGE_F.ID, 0x7FE5D0),
		
		SNIPER_DOROTHY(0x3D, Character.DOROTHY.ID, CharacterClass.SNIPER_F.ID, 0x7FE074),
		SNIPER_IGRENE(0x3E, Character.IGRENE.ID, CharacterClass.SNIPER_F.ID, 0x7FE0DC),
		SNIPER_KLEIN(0x3F, Character.KLEIN.ID, CharacterClass.SNIPER.ID, 0x7FE140),
		SNIPER_WOLT(0x40, Character.WOLT.ID, CharacterClass.SNIPER.ID, 0x7FE1A0),
		
		SHAMAN_SOPHIA(0x41, Character.SOPHIA.ID, CharacterClass.SHAMAN_F.ID, 0x7FE200),
		SHAMAN_REI(0x42, Character.REI.ID, CharacterClass.SHAMAN.ID, 0x7FE254),
		// Wagner has no palettes. :(
		
		SWORDMASTER_FIR(0x43, Character.FIR.ID, CharacterClass.SWORDMASTER_F.ID, 0x7FE2B0),
		SWORDMASTER_KAREL(0x44, Character.KAREL.ID, CharacterClass.SWORDMASTER.ID, 0x7FE310),
		SWORDMASTER_RUTGER(0x45, Character.RUTGER.ID, CharacterClass.SWORDMASTER.ID, 0x7FE370),
		
		THIEF_ASTOL(0x29, Character.ASTOL.ID, CharacterClass.THIEF.ID, 0x7FD7CC),
		THIEF_CASS(0x46, Character.CASS.ID, CharacterClass.THIEF_F.ID, 0x7FE3D4),
		THIEF_CHAD(0x47, Character.CHAD.ID, CharacterClass.THIEF.ID, 0x7FE42C),
		
		TROUBADOUR_CLARINE(0x48, Character.CLARINE.ID, CharacterClass.TROUBADOUR.ID, 0x7FE484),
		
		VALKYRIE_CLARINE(0x49, Character.CLARINE.ID, CharacterClass.VALKYRIE.ID, 0x7FE4E4),
		VALKYRIE_CECILIA(0x4A, Character.CECILIA.ID, CharacterClass.VALKYRIE.ID, 0x7FE558),
		
		GENERAL_WENDY(0x4F, Character.WENDY.ID, CharacterClass.GENERAL_F.ID, 0x7FE71C),
		GENERAL_BARTH(0x50, Character.BARTH.ID, CharacterClass.GENERAL.ID, 0x7FE7A0),
		GENERAL_BORS(0x51, Character.BORS.ID, CharacterClass.GENERAL.ID, 0x7FE82C),
		GENERAL_DOUGLAS(0x52, Character.DOUGLAS.ID, CharacterClass.GENERAL.ID, 0x7FE8B0),
		GENERAL_MURDOCK(0x4D, Character.MURDOCK.ID, CharacterClass.GENERAL.ID, 0x7FE67C),
		GENERAL_LEGYLANCE(0x4E, Character.LEGYLANCE.ID, CharacterClass.GENERAL.ID, 0x7FE6C8),
		GENERAL_ROARTZ(0x54, Character.ROARTZ.ID, CharacterClass.GENERAL.ID, 0x7FE984),
		GENERAL_ZINC(0x55, Character.ZINC.ID, CharacterClass.GENERAL.ID, 0x7FE9D4),
		
		WYVERN_KNIGHT_MILEDY(0x5C, Character.MILEDY.ID, CharacterClass.WYVERN_KNIGHT_F.ID, 0x7FEC50),
		WYVERN_KNIGHT_ZEISS(0x5E, Character.ZEISS.ID, CharacterClass.WYVERN_KNIGHT.ID, 0x7FED38),
		WYVERN_KNIGHT_GALE(0x5B, Character.GALE.ID, CharacterClass.WYVERN_KNIGHT.ID, 0x7FEBD0),
		WYVERN_KNIGHT_NARSHEN(0x5D, Character.NARSHEN.ID, CharacterClass.WYVERN_KNIGHT.ID, 0x7FECC8),
		// Flaer has no palettes :(
		// Raeth has no palettes :(
		
		WARRIOR_LOT(0x60, Character.LOT.ID, CharacterClass.WARRIOR.ID, 0x7FEE08),
		WARRIOR_WARD(0x61, Character.WARD.ID, CharacterClass.WARRIOR.ID, 0x7FEE60),
		WARRIOR_BARTRE(0x64, Character.BARTRE.ID, CharacterClass.WARRIOR.ID, 0x7FEF94),
		
		MANAKETE_GENERIC(0x0, Character.NONE.ID, CharacterClass.MANAKETE.ID, 0x716DCB), // Based off of sprite's base palette.
		MANAKETE_FA(0x0, Character.FA.ID, CharacterClass.MANAKETE_F.ID, 0x7FF050) // TODO: Verify Fa's pointer. There's one last entry in the table that's not listed anywhere else, but Fa herself has no palette index.
		;
		
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
			
			defaultPaletteForClass.put(CharacterClass.SOLDIER.ID, ARCHER_WOLT.info); // No idea.
			defaultPaletteForClass.put(CharacterClass.ARCHER.ID, ARCHER_WOLT.info);
			defaultPaletteForClass.put(CharacterClass.ARCHER_F.ID, ARCHER_DOROTHY.info);
			defaultPaletteForClass.put(CharacterClass.BARD.ID, BARD_ELFIN.info);
			defaultPaletteForClass.put(CharacterClass.DANCER.ID, DANCER_LALAM.info);
			defaultPaletteForClass.put(CharacterClass.BRIGAND.ID, BRIGAND_GONZALES.info);
			defaultPaletteForClass.put(CharacterClass.FIGHTER.ID, FIGHTER_LOT.info);
			defaultPaletteForClass.put(CharacterClass.SNIPER.ID, SNIPER_KLEIN.info);
			defaultPaletteForClass.put(CharacterClass.SNIPER_F.ID, SNIPER_IGRENE.info);
			defaultPaletteForClass.put(CharacterClass.BERSERKER.ID, BERSERKER_GARET.info);
			defaultPaletteForClass.put(CharacterClass.BISHOP.ID, BISHOP_YODEL.info);
			defaultPaletteForClass.put(CharacterClass.BISHOP_F.ID, BISHOP_ELEN.info);
			defaultPaletteForClass.put(CharacterClass.CAVALIER.ID, CAVALIER_ALAN.info);
			defaultPaletteForClass.put(CharacterClass.PRIEST.ID, PRIEST_SAUL.info);
			defaultPaletteForClass.put(CharacterClass.CLERIC.ID, CLERIC_ELEN.info);
			defaultPaletteForClass.put(CharacterClass.MYRMIDON.ID, MYRMIDON_RUTGER.info);
			defaultPaletteForClass.put(CharacterClass.MYRMIDON_F.ID, MYRMIDON_FIR.info);
			defaultPaletteForClass.put(CharacterClass.DRUID.ID, DRUID_REI.info);
			defaultPaletteForClass.put(CharacterClass.DRUID_F.ID, DRUID_NIIME.info);
			defaultPaletteForClass.put(CharacterClass.FALCON_KNIGHT.ID, FALCON_KNIGHT_YUNNO.info);
			defaultPaletteForClass.put(CharacterClass.GENERAL.ID, GENERAL_DOUGLAS.info);
			defaultPaletteForClass.put(CharacterClass.GENERAL_F.ID, GENERAL_WENDY.info);
			defaultPaletteForClass.put(CharacterClass.HERO.ID, HERO_DIECK.info);
			defaultPaletteForClass.put(CharacterClass.HERO_F.ID, HERO_ECHIDNA.info);
			defaultPaletteForClass.put(CharacterClass.SWORDMASTER.ID, SWORDMASTER_KAREL.info);
			defaultPaletteForClass.put(CharacterClass.SWORDMASTER_F.ID, SWORDMASTER_FIR.info);
			defaultPaletteForClass.put(CharacterClass.KNIGHT.ID, KNIGHT_BORS.info);
			defaultPaletteForClass.put(CharacterClass.KNIGHT_F.ID, KNIGHT_WENDY.info);
			defaultPaletteForClass.put(CharacterClass.MAGE.ID, MAGE_LUGH.info);
			defaultPaletteForClass.put(CharacterClass.MAGE_F.ID, MAGE_LILINA.info);
			defaultPaletteForClass.put(CharacterClass.SAGE.ID, SAGE_LUGH.info);
			defaultPaletteForClass.put(CharacterClass.SAGE_F.ID, SAGE_LILINA.info);
			defaultPaletteForClass.put(CharacterClass.MERCENARY.ID, MERCENARY_DIECK.info);
			defaultPaletteForClass.put(CharacterClass.NOMAD.ID, NOMAD_SHIN.info);
			defaultPaletteForClass.put(CharacterClass.NOMAD_F.ID, NOMAD_SUE.info);
			defaultPaletteForClass.put(CharacterClass.NOMAD_TROOPER.ID, NOMAD_TROOPER_DAYAN.info);
			defaultPaletteForClass.put(CharacterClass.NOMAD_TROOPER_F.ID, NOMAD_TROOPER_SUE.info);
			defaultPaletteForClass.put(CharacterClass.PALADIN.ID, PALADIN_MARCUS.info);
			defaultPaletteForClass.put(CharacterClass.PEGASUS_KNIGHT.ID, PEGASUS_KNIGHT_THANY.info);
			defaultPaletteForClass.put(CharacterClass.PIRATE.ID, PIRATE_GEESE.info);
			defaultPaletteForClass.put(CharacterClass.SHAMAN.ID, SHAMAN_REI.info);
			defaultPaletteForClass.put(CharacterClass.SHAMAN_F.ID, SHAMAN_SOPHIA.info);
			defaultPaletteForClass.put(CharacterClass.THIEF.ID, THIEF_CHAD.info);
			defaultPaletteForClass.put(CharacterClass.THIEF_F.ID, THIEF_CASS.info);
			defaultPaletteForClass.put(CharacterClass.TROUBADOUR.ID, TROUBADOUR_CLARINE.info);
			defaultPaletteForClass.put(CharacterClass.VALKYRIE.ID, VALKYRIE_CECILIA.info);
			defaultPaletteForClass.put(CharacterClass.WARRIOR.ID, WARRIOR_BARTRE.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_KNIGHT.ID, WYVERN_KNIGHT_GALE.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_KNIGHT_F.ID, WYVERN_KNIGHT_MILEDY.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_RIDER.ID, WYVERN_RIDER_MILEDY.info); // Assuming male == female for palettes here.
			defaultPaletteForClass.put(CharacterClass.WYVERN_RIDER_F.ID, WYVERN_RIDER_MILEDY.info);
			defaultPaletteForClass.put(CharacterClass.MANAKETE_F.ID, MANAKETE_FA.info);
			defaultPaletteForClass.put(CharacterClass.LORD.ID, LORD_ROY.info);
			defaultPaletteForClass.put(CharacterClass.MASTER_LORD.ID, LORD_ROY.info);
			defaultPaletteForClass.put(CharacterClass.MANAKETE.ID, MANAKETE_GENERIC.info);
		}
		
		private Palette(int paletteID, int charID, int classID, long offset) {
			this.characterID = charID;
			this.classID = classID;
			this.paletteID = paletteID;
			CharacterClass charClass = CharacterClass.valueOf(classID);
			if (charClass != null) {
				switch (charClass) {
				case ARCHER:
				case ARCHER_F:
					this.info = new PaletteInfo(classID, charID, offset, 16, 3, 32, 3);
					break;
				case DANCER:
					this.info = new PaletteInfo(classID, charID, offset, 16, 3, 32, 3, 23, 3);
					break;
				case BRIGAND:
				case FIGHTER:
					this.info = new PaletteInfo(classID, charID, offset, 18, 2, 32, 3);
					break;
				case BARD:
				case MANAKETE:
				case SNIPER:
				case SNIPER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34, 36}, new int[] {23, 25, 27});
					break;
				case BERSERKER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {32, 34, 36}, new int[] {});
					break;
				case BISHOP:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18}, new int[] {23, 34, 36}, new int[] {29, 32});
					break;
				case BISHOP_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 16, 18}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case CAVALIER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 25, 27}, new int[] {11, 16});
					break;
				case CLERIC:
				case PRIEST:
				case MANAKETE_F:
				case MYRMIDON:
				case MYRMIDON_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case SOLDIER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {29, 32, 34, 36}, new int[] {23, 25, 27});
					break;
				case DRUID:
				case DRUID_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 25, 27}, new int[] {29, 32, 34, 36}, new int[] {16, 18, 20});
					break;
				case FALCON_KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {20, 18}, new int[] {25, 27, 36}, new int[] {16, 11, 9});
					break;
				case GENERAL:
				case GENERAL_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {32, 34, 36}, new int[] {});
					break;
				case HERO:
				case HERO_F:
				case SWORDMASTER:
				case SWORDMASTER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {29, 32, 34}, new int[] {});
					break;
				case KNIGHT:
				case KNIGHT_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {14, 16, 9, 11, 7}, new int[] {});
					break;
				case LORD:
				case MASTER_LORD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 16, 20}, new int[] {29, 32, 34, 36}, new int[] {});
					break;
				case MAGE:
					this.info = new PaletteInfo(classID, charID, offset, 18, 2, 32, 3, 23, 3);
					break;
				case MAGE_F:
				case SAGE:
				case SAGE_F:
					this.info = new PaletteInfo(classID, charID, offset, 16, 3, 32, 3, 23, 3);
					break;
				case MERCENARY:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20, 36}, new int[] {25, 11, 9}, new int[] {});
					break;
				case NOMAD:
				case NOMAD_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20, 36}, new int[] {14, 16}, new int[] {23, 25, 27, 29}); // Secondary is Mount/Bow
					break;
				case NOMAD_TROOPER:
				case NOMAD_TROOPER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {9, 16}, new int[] {23, 25, 27, 29});
					break;
				case PALADIN:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {23, 25, 27}, new int[] {16, 11, 14}, new int[] {18, 20}); // No hair. Armor primary, mane/insignia secondary, shield tertiary.
					break;
				case PEGASUS_KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {20, 18}, new int[] {25, 27, 36}, new int[] {29, 32, 34}, new int[] {16, 11}); // Armor Primary, Wing Secondary, Mane tertiary
					break;
				case PIRATE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {29, 32, 34, 36}, new int[] {}); // Outfit/Bandana is the only color.
					break;
				case SHAMAN:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {23, 25, 27}, new int[] {29, 32, 34}, new int[] {9, 16, 18}); // Not really hair, but it matches up in the only one that matters (Canas)
					break;
				case SHAMAN_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {18, 20}, new int[] {23, 25, 27}, new int[] {29, 32, 34});
					break;
				case THIEF:
				case THIEF_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {16, 18, 20}, new int[] {11, 29, 14}, new int[] {32, 34, 36}); // Primary is cape, secondary is inner cloak (+ hairband for female)
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
				case WYVERN_RIDER:
				case WYVERN_RIDER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {11, 9}, new int[] {27, 29, 32, 34}, new int[] {25}); // Primary is Armor, Secondary is Wyvern Body, Tertiary is Wyvern's Wingspan
					break;
				case WYVERN_KNIGHT:
				case WYVERN_KNIGHT_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {27, 29, 32, 34}, new int[] {7, 25}, new int[] {25});
					break;
				default:
					System.err.println("Unknown class detected while creating palette info.");
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
			if (map != null) {
				List<PaletteInfo> list = new ArrayList<PaletteInfo>(map.values());
				return list.toArray(new PaletteInfo[list.size()]);
			} else {
				return new PaletteInfo[] {};
			}
		}
		
		public static int maxUsedPaletteIndex() {
			return 0x66; // Fa, I think.
		}
		
		public static int maxPaletteIndex() {
			return 0x78; // There's space for this many, but they're all 0s. Seems like they could be used.
		}
		
		public static PaletteColor[] supplementaryHairColorForCharacter(int characterID) {
			Character character = Character.valueOf(characterID);
			switch (character) {
			case BARTH:
				return new PaletteColor[] {new PaletteColor(198, 189, 82), new PaletteColor(165, 148, 74), new PaletteColor(115, 107, 90)};
			case BORS:
				return new PaletteColor[] {new PaletteColor(90, 214, 74), new PaletteColor(82, 173, 74), new PaletteColor(66, 123, 74)};
			case DOUGLAS:
				return new PaletteColor[] {new PaletteColor(165, 123, 74), new PaletteColor(115, 90, 74), new PaletteColor(99, 74, 57)};
			case PERCIVAL:
				return new PaletteColor[] {new PaletteColor(255, 247, 99), new PaletteColor(231, 206, 57), new PaletteColor(189, 156, 57)};
			case MARCUS:
				return new PaletteColor[] {new PaletteColor(255, 231, 255), new PaletteColor(222, 198, 222), new PaletteColor(140, 123, 156)};
			case REI:
				return new PaletteColor[] {new PaletteColor(123, 239, 57), new PaletteColor(90, 198, 57), new PaletteColor(90, 132, 82)};
			case ZEISS:
				return new PaletteColor[] {new PaletteColor(239, 66, 49), new PaletteColor(206, 57, 49), new PaletteColor(140, 66, 57)};
			case ZEALOT:
				return new PaletteColor[] {new PaletteColor(148, 140, 156), new PaletteColor(115, 115, 132), new PaletteColor(99, 90, 99)};
			case WENDY:
				return new PaletteColor[] {new PaletteColor(255, 123, 222), new PaletteColor(231, 99, 189), new PaletteColor(173, 66, 148)};
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
	
	public Set<GBAFECharacter> charactersExcludedFromRandomRecruitment() {
		return new HashSet<GBAFECharacter>(Arrays.asList(Character.FA));
	}
	
	public Set<Integer> linkedPortraitIDs(int characterID) {
		return new HashSet<Integer>(); // We don't have that feature yet in FE6.
	}
	
	public Set<GBAFECharacter> allFliers() {
		return new HashSet<GBAFECharacter>(Character.requiredFliers);
	}
	
	public Set<GBAFECharacter> mustAttack() {
		return new HashSet<GBAFECharacter>(Character.requiredAttackers);
	}
	
	public Set<GBAFECharacter> femaleSet() {
		return new HashSet<GBAFECharacter>(Character.femaleSet);
	}
	
	public GBAFECharacter characterWithID(int characterID) {
		GBAFECharacter character = Character.valueOf(characterID);
		if (character == null) {
			character = Character.NONE;
		}
		
		return character;
	}

	public int[] affinityValues() {
		int[] values = new int[FE6Character.Affinity.values().length];
		int i = 0;
		for (FE6Character.Affinity affinity : FE6Character.Affinity.values()) {
			values[i++] = affinity.value;
		}
		
		return values;
	}

	public int canonicalID(int characterID) {
		return Character.canonicalIDForCharacterID(characterID);
	}

	public GBAFECharacterData characterDataWithData(byte[] data, long offset, Boolean hasLimitedClasses) {
		return new FE6Character(data, offset, hasLimitedClasses);
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
	
	public Set<GBAFEClass> meleeSupportedClasses() {
		Set<GBAFEClass> classes = new HashSet<GBAFEClass>(CharacterClass.allValidClasses);
		classes.removeAll(CharacterClass.rangedOnlyClasses);
		classes.removeAll(CharacterClass.allPacifistClasses);
		return classes;
	}
	
	public Set<GBAFEClass> rangeSupportedClasses() {
		Set<GBAFEClass> classes = new HashSet<GBAFEClass>(CharacterClass.allValidClasses);
		classes.removeAll(CharacterClass.meleeOnlyClasses);
		classes.removeAll(CharacterClass.allPacifistClasses);
		return classes;
	}

	public GBAFEClass classWithID(int classID) {
		return CharacterClass.valueOf(classID);
	}
	
	public boolean canClassDemote(GBAFEClass charClass) {
		for (GBAFEClass baseClass : CharacterClass.promotionMap.keySet()) {
			if (CharacterClass.promotionMap.get(baseClass).ID == charClass.getID()) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean canClassPromote(GBAFEClass charClass) {
		return CharacterClass.promotionMap.keySet().contains(charClass);
	}
	
	public GBAFEClass[] promotedClass(GBAFEClass baseClass) {
		List<GBAFEClass> classList = new ArrayList<GBAFEClass>();
		for (CharacterClass charClass : CharacterClass.promotionMap.keySet()) {
			if (charClass.ID == baseClass.getID()) {
				classList.add(CharacterClass.promotionMap.get(charClass));
			}
		}
		
		return classList.toArray(new GBAFEClass[classList.size()]);
	}
	
	public GBAFEClass[] demotedClass(GBAFEClass promotedClass) {
		List<GBAFEClass> classList = new ArrayList<GBAFEClass>();
		for (CharacterClass baseClass : CharacterClass.promotionMap.keySet()) {
			CharacterClass charClass = CharacterClass.promotionMap.get(baseClass);
			if (charClass.ID == promotedClass.getID()) {
				classList.add(baseClass);
			}
		}
		
		return classList.toArray(new GBAFEClass[classList.size()]);
	}
	
	public boolean isFlier(GBAFEClass charClass) {
		return CharacterClass.flyingClasses.contains(charClass);
	}

	public Set<GBAFEClass> classesThatLoseToClass(GBAFEClass sourceClass, GBAFEClass winningClass,
			Map<String, Boolean> options) {
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

	public GBAFEClassData classDataWithData(byte[] data, long offset, GBAFEClassData demotedClass) {
		return new FE6Class(data, offset, demotedClass);
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
		Set<Item> basicWeapons = Item.basicItemsOfType(type);
		return Collections.min(basicWeapons, Item.itemIDComparator());
	}
	
	public WeaponRank rankWithValue(int value) {
		Item.FE6WeaponRank weaponRank = Item.FE6WeaponRank.valueOf(value);
		if (weaponRank != null) { return weaponRank.toGeneralRank(); }
		
		return WeaponRank.NONE;
	}
	
	public int rankValueForRank(WeaponRank rank) {
		Item.FE6WeaponRank fe6Rank = Item.FE6WeaponRank.rankFromGeneralRank(rank);
		if (fe6Rank != null) { return fe6Rank.value; }
		return 0;
	}
	
	public int getHighestWeaponRankValue() {
		return Item.FE6WeaponRank.S.value;
	}
	
	public Set<GBAFEItem> allWeapons() {
		return new HashSet<GBAFEItem>(Item.allWeapons);
	}
	
	public Set<GBAFEItem> weaponsWithStatBoosts() {
		return new HashSet<GBAFEItem>(Arrays.asList(
				Item.DURANDAL,
				Item.BINDING_BLADE,
				Item.MALTET,
				Item.ARMADS,
				Item.MURGLEIS,
				Item.FORBLAZE,
				Item.AUREOLA,
				Item.APOCALYPSE
				));
	}
	
	public Set<GBAFEItem> weaponsWithEffectiveness() {
		return new HashSet<GBAFEItem>(Arrays.asList(
				Item.RAPIER,
				Item.IRON_BOW,
				Item.HORSESLAYER,
				Item.HAMMER,
				Item.WYRMSLAYER
				));
	}
	
	public Set<GBAFEItem> weaponsOfTypeUpToRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee) {
		if (type == Item.FE6WeaponType.DARK.toGeneralType() && rank == Item.FE6WeaponRank.E.toGeneralRank()) {
			rank = WeaponRank.D;
		}
		return new HashSet<GBAFEItem>(Item.weaponsOfTypeAndRank(type, WeaponRank.E, rank, rangedOnly));
	}
	
	public Set<GBAFEItem> weaponsOfTypeAndEqualRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee, Boolean allowLower) {
		if (type == Item.FE6WeaponType.DARK.toGeneralType() && rank == Item.FE6WeaponRank.E.toGeneralRank()) {
			rank = WeaponRank.D;
		}
		
		Set<Item> equalRankWeapons = Item.weaponsOfTypeAndRank(type, rank, rank, rangedOnly);
		if (equalRankWeapons.isEmpty() && allowLower) {
			return weaponsOfTypeUpToRank(type, rank, rangedOnly, requiresMelee);
		}
		
		return new HashSet<GBAFEItem>(equalRankWeapons);
	}
	
	public Set<GBAFEItem> prfWeaponsForClassID(int classID) {
		Set<Item> weapons = Item.prfWeaponsForClassID(classID);
		 if (weapons == null) { return new HashSet<GBAFEItem>(); }
		return new HashSet<GBAFEItem>(weapons);
	}
	
	public Set<GBAFEItem> allPotentialChestRewards() {
		return new HashSet<GBAFEItem>(Item.allPotentialRewards);
	}
	
	public Set<GBAFEItem> relatedItemsToItem(GBAFEItemData itemData) {
		if (itemData == null) { return new HashSet<GBAFEItem>(); }
		
		Item item = Item.valueOf(itemData.getID());
		if (item == null) { return new HashSet<GBAFEItem>(); }
		
		Set<GBAFEItem> relatedItems;
		
		if (item.isWeapon()) {
			Set<GBAFEItem> relatedWeapons = new HashSet<GBAFEItem>();
			relatedWeapons.addAll(Item.weaponsOfType(item.getType()));
			relatedWeapons.addAll(Item.weaponsOfRank(item.getRank()));
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
		Set<Item> lockedWeapons = Item.lockedWeaponsToClassID(classID);
		if (lockedWeapons == null) { return new HashSet<GBAFEItem>(); }
		return new HashSet<GBAFEItem>(lockedWeapons);
	}

	public Set<GBAFEItem> weaponsForClass(int classID) {
		CharacterClass charClass = CharacterClass.valueOf(classID);
		if (charClass == null) { return new HashSet<GBAFEItem>(); }
		
		Set<GBAFEItem> usableItems = new HashSet<GBAFEItem>();
		
		switch (charClass) {
		case MYRMIDON:
		case MYRMIDON_F:
		case SWORDMASTER:
		case SWORDMASTER_F:
		case MERCENARY:
		case MERCENARY_F:
		case LORD:
		case MASTER_LORD:
		case THIEF:
		case THIEF_F:
			usableItems.addAll(Item.allSwords);
			break;
		case FIGHTER:
		case BERSERKER:
		case PIRATE:
		case BRIGAND:
			usableItems.addAll(Item.allAxes);
			break;
		case KNIGHT:
		case KNIGHT_F:
		case WYVERN_RIDER:
		case WYVERN_RIDER_F:
		case SOLDIER:
		case PEGASUS_KNIGHT:
			usableItems.addAll(Item.allLances);
			break;
		case ARCHER:
		case ARCHER_F:
		case SNIPER:
		case SNIPER_F:
		case NOMAD:
		case NOMAD_F:
			usableItems.addAll(Item.allBows);
			break;
		case MAGE:
		case MAGE_F:
		case VALKYRIE:
		case SAGE:
		case SAGE_F:
			usableItems.addAll(Item.allAnima);
			break;
		case SHAMAN:
		case SHAMAN_F:
		case DRUID:
		case DRUID_F:
			usableItems.addAll(Item.allDark);
			break;
		case BISHOP:
		case BISHOP_F:
			usableItems.addAll(Item.allLight);
			break;
		case CAVALIER:
		case CAVALIER_F:
		case FALCON_KNIGHT:
		case WYVERN_KNIGHT:
		case WYVERN_KNIGHT_F:
			usableItems.addAll(Item.allSwords);
			usableItems.addAll(Item.allLances);
			break;
		case HERO:
		case HERO_F:
			usableItems.addAll(Item.allSwords);
			usableItems.addAll(Item.allAxes);
			break;
		case WARRIOR:
			usableItems.addAll(Item.allAxes);
			usableItems.addAll(Item.allBows);
			break;
		case GENERAL:
		case GENERAL_F:
			usableItems.addAll(Item.allLances);
			usableItems.addAll(Item.allAxes);
			break;
		case NOMAD_TROOPER:
		case NOMAD_TROOPER_F:
			usableItems.addAll(Item.allSwords);
			usableItems.addAll(Item.allBows);
			break;
		case PALADIN:
		case PALADIN_F:
			usableItems.addAll(Item.allSwords);
			usableItems.addAll(Item.allLances);
			usableItems.addAll(Item.allAxes);
			break;
		default:
			break;
		}
		
		usableItems.removeAll(Item.allRestrictedWeapons);
		usableItems.removeAll(Item.allPrfRank);
		usableItems.addAll(weaponsLockedToClass(classID));
		usableItems.addAll(prfWeaponsForClassID(classID));
		
		return usableItems;
	}
	
	public Set<GBAFEItem> basicWeaponsForClass(int classID) {
		Set<GBAFEItem> itemsUsableByClass = new HashSet<GBAFEItem>(weaponsForClass(classID));
		itemsUsableByClass.retainAll(Item.allBasicWeapons);
		return itemsUsableByClass;
	}

	public Set<GBAFEItem> comparableWeaponsForClass(int classID, GBAFEItemData originalItem, boolean strict) {
		if (originalItem == null) { return new HashSet<GBAFEItem>(); }
		Item item = Item.valueOf(originalItem.getID());
		if (item == null) { return new HashSet<GBAFEItem>(); }
		
		Set<GBAFEItem> itemsUsableByClass = new HashSet<GBAFEItem>(weaponsForClass(classID));
		Set<GBAFEItem> usableSet = new HashSet<GBAFEItem>(itemsUsableByClass);
		
		switch (item.getRank()) {
		case E:
			itemsUsableByClass.retainAll(Item.allERank);
			break;
		case D:
			itemsUsableByClass.retainAll(Item.allDRank);
			break;
		case C:
			itemsUsableByClass.retainAll(Item.allCRank);
			break;
		case B:
			itemsUsableByClass.retainAll(Item.allBRank);
			break;
		case A:
			itemsUsableByClass.retainAll(Item.allARank);
			break;
		case S:
			itemsUsableByClass.retainAll(Item.allSRank);
			break;
		default:
			itemsUsableByClass.clear();
			break;
		}
		
		if (strict) {
			Set<GBAFEItem> usableByRank = new HashSet<GBAFEItem>(itemsUsableByClass);
			// filter out based on matches to pre-defined sets.
			if (Item.braveSet.contains(item) && !Collections.disjoint(Item.braveSet, itemsUsableByClass)) {
				itemsUsableByClass.retainAll(Item.braveSet);
			} else if (Item.reaverSet.contains(item) && !Collections.disjoint(Item.reaverSet, itemsUsableByClass)) {
				itemsUsableByClass.retainAll(Item.reaverSet);
			} else if (Item.rangedSet.contains(item) && !Collections.disjoint(Item.rangedSet, itemsUsableByClass)) {
				itemsUsableByClass.retainAll(Item.rangedSet);
				if (!Item.allSiegeTomes.contains(item)) {
					itemsUsableByClass.removeAll(Item.allSiegeTomes);
					Set<GBAFEItem> usableSiegeReplacements = new HashSet<GBAFEItem>(usableSet);
					usableSiegeReplacements.retainAll(Item.siegeReplacementSet);
					itemsUsableByClass.addAll(usableSiegeReplacements);
				}
			} else if (Item.rangedSet.contains(item) && !Collections.disjoint(Item.poisonSet, itemsUsableByClass)) {
				itemsUsableByClass.retainAll(Item.poisonSet);
			} else if (Item.effectiveSet.contains(item) && !Collections.disjoint(Item.effectiveSet, itemsUsableByClass)) {
				itemsUsableByClass.retainAll(Item.effectiveSet);
			} else if (Item.killerSet.contains(item) && !Collections.disjoint(Item.killerSet, itemsUsableByClass)) {
				itemsUsableByClass.retainAll(Item.killerSet);
			} else if (Item.interestingSet.contains(item) && !Collections.disjoint(Item.interestingSet, itemsUsableByClass)) {
				itemsUsableByClass.retainAll(Item.interestingSet);
				if (!Item.allSiegeTomes.contains(item)) {
					itemsUsableByClass.removeAll(Item.allSiegeTomes);
					Set<GBAFEItem> usableSiegeReplacements = new HashSet<GBAFEItem>(usableSet);
					usableSiegeReplacements.retainAll(Item.siegeReplacementSet);
					itemsUsableByClass.addAll(usableSiegeReplacements);
				}
			} else {
				itemsUsableByClass.retainAll(Item.normalSet);
			}
			
			if (itemsUsableByClass.isEmpty() && !usableByRank.isEmpty()) {
				itemsUsableByClass = usableByRank;
			}
		}
		
		return itemsUsableByClass;
	}

	public Set<GBAFEItem> formerThiefInventory() {
		return new HashSet<GBAFEItem>(Item.formerThiefKit());
	}

	public Set<GBAFEItem> thiefItemsToRemove() {
		return new HashSet<GBAFEItem>(Item.itemsToRemoveFromFormerThief());
	}

	public Set<GBAFEItem> itemKitForSpecialClass(int classID, Random rng) {
		Set<Item> kit = Item.specialClassKit(classID, rng);
		if (kit == null) { return new HashSet<GBAFEItem>(); }
		return new HashSet<GBAFEItem>(kit);
	}
	
	public String statBoostStringForWeapon(GBAFEItem weapon) {
		if (weapon == Item.DURANDAL) { return "+5 Strength"; }
		if (weapon == Item.BINDING_BLADE) { return "+5 Defense, Resistance"; }
		if (weapon == Item.MALTET) { return "+5 Skill"; }
		if (weapon == Item.ARMADS) { return "+5 Defense"; }
		if (weapon == Item.MURGLEIS) { return "+5 Speed"; }
		if (weapon == Item.FORBLAZE) { return "+5 Luck"; }
		if (weapon == Item.AUREOLA) { return "+5 Resistance"; }
		if (weapon == Item.APOCALYPSE) { return "+5 Magic"; }
		
		return null;
	}
	
	public String effectivenessStringForWeapon(GBAFEItem weapon, Boolean shortString) {
		if (weapon == Item.RAPIER) { return shortString ? "Eff. Infantry" : "Effective against infantry"; }
		if (weapon == Item.IRON_BOW) { return shortString ? "Eff. Fliers" : "Effective against fliers"; }
		if (weapon == Item.HORSESLAYER) { return shortString ? "Eff. Cavalry" : "Effective against cavalry"; }
		if (weapon == Item.HAMMER) { return shortString ? "Eff. Knights" : "Effective against knights"; }
		if (weapon == Item.WYRMSLAYER) { return shortString ? "Eff. Dragons" : "Effective against dragons"; }
		
		return null;
	}
	
	public GBAFEItemData itemDataWithData(byte[] data, long offset, int itemID) {
		return new FE6Item(data, offset);
	}

	public List<GBAFEClass> knightCavEffectivenessClasses() {
		// Nomads and Troubadours don't count in FE6.
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.CAVALIER,
				CharacterClass.CAVALIER_F,
				CharacterClass.PALADIN,
				CharacterClass.PALADIN_F,
				CharacterClass.KNIGHT,
				CharacterClass.KNIGHT_F,
				CharacterClass.GENERAL,
				CharacterClass.GENERAL_F));
	}

	public List<GBAFEClass> knightEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.KNIGHT,
				CharacterClass.KNIGHT_F,
				CharacterClass.GENERAL,
				CharacterClass.GENERAL_F));
	}

	public List<GBAFEClass> cavalryEffectivenessClasses() {
		// Nomads and Troubadours don't count in FE6.
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.CAVALIER,
				CharacterClass.CAVALIER_F,
				CharacterClass.PALADIN,
				CharacterClass.PALADIN_F));
	}

	public List<GBAFEClass> dragonEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.FIRE_DRAGON,
				CharacterClass.DIVINE_DRAGON,
				CharacterClass.MAGIC_DRAGON,
				CharacterClass.WYVERN_RIDER,
				CharacterClass.WYVERN_RIDER_F,
				CharacterClass.WYVERN_KNIGHT,
				CharacterClass.WYVERN_KNIGHT_F
				));
	}

	public List<GBAFEClass> flierEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.PEGASUS_KNIGHT,
				CharacterClass.FALCON_KNIGHT,
				CharacterClass.WYVERN_KNIGHT,
				CharacterClass.WYVERN_KNIGHT_F,
				CharacterClass.WYVERN_RIDER,
				CharacterClass.WYVERN_RIDER_F
				));
	}

	public List<GBAFEClass> myrmidonEffectivenessClasses() {
		// Doesn't exist in FE6.
		return new ArrayList<GBAFEClass>();
	}

	public List<GBAFEClass> monsterEffectivenessClasses() {
		// Doesn't exist in FE6.
		return new ArrayList<GBAFEClass>();
	}

	public GBAFEPromotionItem[] allPromotionItems() {
		return PromotionItem.values();
	}

	public List<GBAFEClass> additionalClassesForPromotionItem(GBAFEPromotionItem promotionItem,
			List<Byte> existingClassIDs) {
		if (promotionItem == PromotionItem.KNIGHT_CREST) {
			return new ArrayList<GBAFEClass>(Arrays.asList(
					CharacterClass.LORD,
					CharacterClass.SOLDIER
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
		return new FE6SpellAnimationCollection(data, offset);
	}
}
