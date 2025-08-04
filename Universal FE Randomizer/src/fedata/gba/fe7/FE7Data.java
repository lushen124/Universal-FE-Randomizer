package fedata.gba.fe7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFESpellAnimationCollection;
import fedata.gba.GBAFECharacterData.Affinity;
import fedata.gba.general.*;
import fedata.gba.general.GBAFEChapterMetadataChapter;
import fedata.gba.general.GBAFECharacter;
import fedata.gba.general.GBAFECharacterProvider;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEClassProvider;
import fedata.gba.general.GBAFEItem;
import fedata.gba.general.GBAFEItemProvider;
import fedata.gba.general.GBAFEPromotionItem;
import fedata.gba.general.GBAFEShop;
import fedata.gba.general.GBAFEShopProvider;
import fedata.gba.general.PaletteColor;
import fedata.gba.general.PaletteInfo;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import random.gba.loader.ItemDataLoader.AdditionalData;
import random.gba.randomizer.shuffling.GBAFEShufflingDataProvider;
import random.gba.randomizer.shuffling.data.GBAFEPortraitData;
import util.AddressRange;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE7Data implements GBAFECharacterProvider, GBAFEClassProvider, GBAFEItemProvider, GBAFEShufflingDataProvider, GBAFETextProvider, GBAFEStatboostProvider, GBAFEShopProvider {

	public static final String FriendlyName = "Fire Emblem: Blazing Sword";
	public static final String GameCode = "AE7E";

	public static final long CleanCRC32 = 0x2A524221;
	public static final long CleanSize = 16777216;
	
	public static final int NumberOfCharacters = 254;
	public static final int BytesPerCharacter = 52;
	public static final long CharacterTablePointer = 0x17890;
	//public static final long DefaultCharacterTableAddress = 0xBDCE18; 
	
	public static final int NumberOfClasses = 100;
	public static final int BytesPerClass = 84;
	public static final long ClassTablePointer = 0x178F0;
	//public static final long DefaultClassTableAddress = 0xBE015C;
	
	// This also needs to be moved in order to make sure map sprites
	// show up properly. This is implicitly tied to the class table.
	public static final long ClassMapSpriteTablePointer = 0x6D574;
	public static final int BytesPerMapSpriteTableEntry = 8;
	public static final int NumberOfMapSpriteEntries = 99;
	
	public static final int NumberOfItems = 159;
	public static final int BytesPerItem = 36;
	public static final long ItemTablePointer = 0x16060;
	//public static final long DefaultItemTableAddress = 0xBE222C;
	
	public static final int NumberOfSpellAnimations = 127;
	public static final int BytesPerSpellAnimation = 16;
	public static final long SpellAnimationTablePointer = 0x52B24;
	//public static final long DefaultSpellAnimationTableOffset = 0xC999C0;
	
	public static final long ChapterTablePointer = 0x191C8;
	//public static final long DefaultChapterArrayOffset = 0xC9C9C8;
	public static final int BytesPerChapterUnit = 16;
	
	public static final long ChapterMetadataTablePointer = 0x31580;
	//public static final long DefaultChapterMetadataArrayOffset = 0xC9A200;
	public static final int BytesPerChapterMetadata = 152;
	
	// This is more than just promotion items, but they're all clustered together around here.
	// I actually suspect this is the table for the class restrictions for item usage. 
	// Most have the same value which is probably the no-restriction case.
	public static final long PromotionItemTablePointer = 0x27428;											
	
	public static final long PaletteTableOffset = 0xFD8004L;
	public static final int PaletteEntryCount = 256;
	public static final int PaletteEntrySize = 16;
	
	public static final long WorldMapEventTableOffset = 0xC9CDACL;
	public static final int WorldMapEventItemSize = 4;
	public static final int WorldMapEventCount = 43;
	
	public static final long AnimationPointerTableOffset = 0xE00008L;
	public static final int AnimationPointerTableCount = 256;
	public static final int AnimationPointerTableEntrySize = 32;
	
	public static final long ModeSelectHackOffset = 0x9E9FCL;
	public static final byte[] ModeSelectHackOriginalData = new byte[] { (byte)0x30, (byte)0xB5, (byte)0x99, (byte)0xB0 };
	public static final byte[] ModeSelectHackNewData = new byte[] { (byte)0x1F, (byte)0x20, (byte)0x70, (byte)0x47 };
	
	// The mode select hard codes the portraits and classes that show up.
	// We should figure out who our lords are (if we randomize recruitment) and replace those faces accordingly.
	// The faces are stored in 12 bytes, 4 bytes each, starting with Lyn, then Eliwood, then Hector
	// The default data was 16 00 00 00 02 00 00 00 0C 00 00 00 with 16 being Lyn's FaceID, 02 being Eliwood's FaceID, and 0C being Hector's FaceID.
	public static final long ModeSelectPortraitOffset = 0x418DB4L;
	// Sprite played is actually right before it, formatted int he same way, just with animation IDs.
	// The default data was 0E 00 00 00 00 00 00 00 06 00 00 00 with 0E being Lyn Lord's battle animation, 00 being Eliwood Lord's Battle Animation, and 06 being Hector Lord's battle animation.
	public static final long ModeSelectClassAnimationOffset = 0x418DA8L;
	// The weapon type is stored elsewhere. Stored in triplets of text indices. Each entry is 12 bytes long and contains their name, their weapon type, and a short description (not sure where that's used)
	// The default data is:
	// DE 04 00 00 - 'Lyn' 
	// B3 12 00 00 - 'Swordfighter of Sacae'
	// B8 12 00 00 - 'Swd'
	// DC 04 00 00 - 'Eliwood'
	// B4 12 00 00 - 'Nobleman of Pherae'
	// B8 12 00 00 - 'Swd'
	// DD 04 00 00 - 'Hector'
	// B5 12 00 00 - 'Marquess Ostia's brother'
	// B9 12 00 00 - 'Axe'
	// Obviously, there's some sharing for Lyn and Eliwood's weapon, so we'll jack an unused string's text entry. This is the only one we'll need to change.
	public static final long ModeSelectEliwoodWeaponOffset = 0xCE48D4L;
	public static final int ModeSelectTextLynWeaponTypeIndex = 0x12B8;
	public static final int ModeSelectTextHectorWeaponTypeIndex = 0x12B9;
	// 0x123D - 'This unit has no SRAM information[.][X]
	// I'm not sure if this is used, but we're going to steal it for Eliwood's slot's weapon.
	public static final int ModeSelectTextEliwoodWeaponTypeIndex = 0x123D;
	
	// Lucius's equipment is given to him in secret in Chapter 16/17 as part of ASM.
	// The item ID bytes can be replaced at 0x7D0C2 (which is normally 0x3E for Lightning)
	// and 0x7D0D8 (which is normally 0x6B for Vulnerary). After these are actually
	// the equipment given to the green units if those are useful.
	public static final int LuciusEquipment1IDOffset = 0x7D0C2;
	public static final int LuciusEquipment2IDOffset = 0x7D0D8;
	
	// Delphi Shield logic is a hard coded check against the effectiveness pointer of a weapon.
	// This address it checks against is 0x8C97ED2, which can be found at the following offset
	// and should be replaced if we create our own effectiveness pointers.
	public static final long FlierEffectivenessPointer = 0x8C97ED2L;
	public static final int DelphiShieldEffectivenessCheckPointer = 0x168A0;
	
	// These are spaces confirmed free inside the natural ROM size (0xFFFFFF).
	// It's somewhat limited, so let's not use these unless we absolutely have to (like for palettes).
	public static final List<AddressRange> InternalFreeRange = createFreeRangeList();
	private static final List<AddressRange> createFreeRangeList() {
		List<AddressRange> ranges = new ArrayList<AddressRange>();
		// FE7 doesn't seem to have anything that's obviously free...
		// There's stuff here, but hacking docs seem to think these are free to use so...
		ranges.add(new AddressRange(0xD00000L, 0xD90000L));
		return ranges;
	}
	
	private static final FE7Data sharedInstance = new FE7Data();
	
	public static final GBAFECharacterProvider characterProvider = sharedInstance;
	public static final GBAFEClassProvider classProvider = sharedInstance;
	public static final GBAFEItemProvider itemProvider = sharedInstance;
	public static final GBAFEStatboostProvider statboostProvider = sharedInstance;
	public static final GBAFEShufflingDataProvider shufflingDataProvider = sharedInstance;
	public static final GBAFETextProvider textProvider = sharedInstance;
	public static final GBAFEShopProvider shopProvider = sharedInstance;
	
	public enum CharacterAndClassAbility1Mask {
		USE_MOUNTED_AID(0x1), CANTO(0x2), STEAL(0x4), USE_LOCKPICKS(0x8),
		DANCE(0x10), PLAY(0x20), CRITICAL_15(0x40), BALLISTA(0x80);
		
		public int ID;
		
		private static Map<Integer, CharacterAndClassAbility1Mask> map = new HashMap<Integer, CharacterAndClassAbility1Mask>();
		private static Map<CharacterAndClassAbility1Mask, String> displayStrings = new HashMap<CharacterAndClassAbility1Mask, String>();
		
		static {
			for (CharacterAndClassAbility1Mask ability1 : CharacterAndClassAbility1Mask.values()) {
				map.put(ability1.ID, ability1);
				switch(ability1) {
				case USE_MOUNTED_AID:
					displayStrings.put(ability1, "Uses Mounted Aid (0x01)");
					break;
				case CANTO:
					displayStrings.put(ability1, "Canto (0x02)");
					break;
				case STEAL:
					displayStrings.put(ability1, "Has Steal Command (0x04)");
					break;
				case USE_LOCKPICKS:
					displayStrings.put(ability1, "Can Use Lockpicks (0x08)");
					break;
				case DANCE:
					displayStrings.put(ability1, "Has Dance Command (0x10)");
					break;
				case PLAY:
					displayStrings.put(ability1, "Has Play Command (0x20)");
					break;
				case CRITICAL_15:
					displayStrings.put(ability1, "Critical +15 (0x40)");
					break;
				case BALLISTA:
					displayStrings.put(ability1, "Can Use Ballistas (0x80)");
					break;
				default:
					break;
				}
			}
		}
		
		private CharacterAndClassAbility1Mask(final int id) { ID = id; }
		
		public static CharacterAndClassAbility1Mask valueOf(int maskID) {
			return map.get(maskID);
		}
		
		public String displayString() {
			return CharacterAndClassAbility1Mask.displayStrings.get(this);
		}
		
		public static CharacterAndClassAbility1Mask maskForDisplayString(String displayString) {
			CharacterAndClassAbility1Mask mask = null;
			try {
				mask = displayStrings.keySet().stream().filter(ability -> displayString.equals(displayStrings.get(ability))).findFirst().get();
			} catch (NoSuchElementException e) {}
			
			return mask;
		}
	}
	
	public enum CharacterAndClassAbility2Mask {
		PROMOTED(0x1), SUPPLY_DEPOT(0x2), HORSE_ICON(0x4), WYVERN_ICON(0x8),
		PEGASUS_ICON(0x10), LORD(0x20), FEMALE(0x40), BOSS(0x80);
		
		public int ID;
		
		private static Map<Integer, CharacterAndClassAbility2Mask> map = new HashMap<Integer, CharacterAndClassAbility2Mask>();
		private static Map<CharacterAndClassAbility2Mask, String> displayStrings = new HashMap<CharacterAndClassAbility2Mask, String>();
		
		static {
			for (CharacterAndClassAbility2Mask ability2 : CharacterAndClassAbility2Mask.values()) {
				map.put(ability2.ID, ability2);
				switch (ability2) {
				case PROMOTED:
					displayStrings.put(ability2, "Promoted Class (0x01)");
					break;
				case SUPPLY_DEPOT:
					displayStrings.put(ability2, "Is Convoy (0x02)");
					break;
				case HORSE_ICON:
					displayStrings.put(ability2, "Shows Horse Icon (0x04)");
					break;
				case WYVERN_ICON:
					displayStrings.put(ability2, "Shows Wyvern Icon (0x08)");
					break;
				case PEGASUS_ICON:
					displayStrings.put(ability2, "Shows Pegasus Icon (0x10)");
					break;
				case LORD:
					displayStrings.put(ability2, "Is Lord (0x20)");
					break;
				case FEMALE:
					displayStrings.put(ability2, "Is Female (0x40)");
					break;
				case BOSS:
					displayStrings.put(ability2, "Is Boss (0x80)");
					break;
				default:
					break;
				}
			}
		}
		
		private CharacterAndClassAbility2Mask(int id) { ID = id; }
		
		public static CharacterAndClassAbility2Mask valueOf(int maskID) {
			return map.get(maskID);
		}
		
		public String displayString() {
			return CharacterAndClassAbility2Mask.displayStrings.get(this);
		}
		
		public static CharacterAndClassAbility2Mask maskForDisplayString(String displayString) {
			CharacterAndClassAbility2Mask mask = null;
			try {
				mask = displayStrings.keySet().stream().filter(ability -> displayString.equals(displayStrings.get(ability))).findFirst().get();
			} catch (NoSuchElementException e) {}
			
			return mask;
		}
	}
	
	public enum CharacterAndClassAbility3Mask {
		UNUSED_WEAPON_LOCK(0x1), WO_DAO_LOCK(0x2), DRAGONSTONE_LOCK(0x4), MORPHS_VAIDA(0x8),
		UNCONTROLLABLE(0x10), TRIANGLE_ATTACK(0x20), UNUSED_TRIANGLE_ATTACK(0x40), DO_NOT_USE(0x80);
		
		public int ID;
		
		private static Map<Integer, CharacterAndClassAbility3Mask> map = new HashMap<Integer, CharacterAndClassAbility3Mask>();
		private static Map<CharacterAndClassAbility3Mask, String> displayStrings = new HashMap<CharacterAndClassAbility3Mask, String>();
		
		static {
			for (CharacterAndClassAbility3Mask ability3 : CharacterAndClassAbility3Mask.values()) {
				map.put(ability3.ID, ability3);
				switch (ability3) {
				case UNUSED_WEAPON_LOCK:
					displayStrings.put(ability3, "Lock - Unused (0x01)");
					break;
				case WO_DAO_LOCK:
					displayStrings.put(ability3, "Lock - Myrmidon (0x02)");
					break;
				case DRAGONSTONE_LOCK:
					displayStrings.put(ability3, "Lock - Dragons (0x04)");
					break;
				case MORPHS_VAIDA:
					displayStrings.put(ability3, "Morphs (0x08)");
					break;
				case UNCONTROLLABLE:
					displayStrings.put(ability3, "Cannot Be Selected (0x10)");
					break;
				case TRIANGLE_ATTACK:
					displayStrings.put(ability3, "Pegasus Triangle Attack (0x20)");
					break;
				case UNUSED_TRIANGLE_ATTACK:
					displayStrings.put(ability3, "Second Triangle Attack (0x40)");
					break;
				case DO_NOT_USE:
					displayStrings.put(ability3, "Do Not Use! (0x80)");
					break;
				default:
					break;
				}
			}
		}
		
		private CharacterAndClassAbility3Mask(final int id) { ID = id; }
		
		public static CharacterAndClassAbility3Mask valueOf(int maskID) {
			return map.get(maskID);
		}
		
		public String displayString() {
			return CharacterAndClassAbility3Mask.displayStrings.get(this);
		}
		
		public static CharacterAndClassAbility3Mask maskForDisplayString(String displayString) {
			CharacterAndClassAbility3Mask mask = null;
			try {
				mask = displayStrings.keySet().stream().filter(ability -> displayString.equals(displayStrings.get(ability))).findFirst().get();
			} catch (NoSuchElementException e) {}
			
			return mask;
		}
	}
	
	public enum CharacterAndClassAbility4Mask {
		NO_EXPERIENCE_GIVEN(0x1), LETHALITY(0x2), MAGIC_SEAL(0x4), DROP_LAST_ITEM(0x8),
		ELIWOOD_LOCK(0x10), HECTOR_LOCK(0x20), LYN_LOCK(0x40), ATHOS_LOCK(0x80);
		
		public int ID;
		
		private static Map<Integer, CharacterAndClassAbility4Mask> map = new HashMap<Integer, CharacterAndClassAbility4Mask>();
		private static Map<CharacterAndClassAbility4Mask, String> displayStrings = new HashMap<CharacterAndClassAbility4Mask, String>();
		
		static {
			for (CharacterAndClassAbility4Mask ability4 : CharacterAndClassAbility4Mask.values()) {
				map.put(ability4.ID, ability4);
				switch (ability4) {
				case NO_EXPERIENCE_GIVEN:
					displayStrings.put(ability4, "Gives No Experience (0x01)");
					break;
				case LETHALITY:
					displayStrings.put(ability4, "Can Trigger Lethality (0x02)");
					break;
				case MAGIC_SEAL:
					displayStrings.put(ability4, "Seals Magic (0x04)");
					break;
				case DROP_LAST_ITEM:
					displayStrings.put(ability4, "Drops Last Item (0x08)");
					break;
				case ELIWOOD_LOCK:
					displayStrings.put(ability4, "Lock - Eliwood (0x10)");
					break;
				case HECTOR_LOCK:
					displayStrings.put(ability4, "Lock - Hector (0x20)");
					break;
				case LYN_LOCK:
					displayStrings.put(ability4, "Lock - Lyn (0x40)");
					break;
				case ATHOS_LOCK:
					displayStrings.put(ability4, "Lock - Athos (0x80)");
					break;
				default:
					break;
				}
			}
		}
		
		private CharacterAndClassAbility4Mask(final int id) { ID = id; }
		
		public static CharacterAndClassAbility4Mask valueOf(int maskID) {
			return map.get(maskID);
		}
		
		public String displayString() {
			return CharacterAndClassAbility4Mask.displayStrings.get(this);
		}
		
		public static CharacterAndClassAbility4Mask maskForDisplayString(String displayString) {
			CharacterAndClassAbility4Mask mask = null;
			try {
				mask = displayStrings.keySet().stream().filter(ability -> displayString.equals(displayStrings.get(ability))).findFirst().get();
			} catch (NoSuchElementException e) {};
			
			return mask;
		}
	}
	
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
		
		public static Set<Character> requiredFliers = new HashSet<Character>(Arrays.asList(FIORA, FARINA, VAIDA));
		public static Set<Character> requiredAttackers = new HashSet<Character>(Arrays.asList(LYN, LYN_TUTORIAL, ELIWOOD, HECTOR, JAFFAR, RATH, ERK, RATH_TUTORIAL));
		public static Set<Character> femaleSet = new HashSet<Character>(Arrays.asList(REBECCA, LOUISE, SERRA, NINO, PRISCILLA, FIORA, FARINA, VAIDA, FLORINA, ISADORA, KARLA, VAIDA_BOSS, ELENORA, LEILA, 
				NATALIE, LYN_TUTORIAL, FLORINA_TUTORIAL, URSULA, SONIA, LIMSTELLA, URSULA_MORPH, LYN, NINIAN));
		public static Set<Character> requiresPromotion = new HashSet<Character>(Arrays.asList(ELIWOOD, HECTOR));
		
		public static Set<Character> doNotBuff = new HashSet<Character>(Arrays.asList());
		
		public static Set<Character> allSpecial = new HashSet<Character>(Arrays.asList(NILS, NINIAN, NILS_FINALCHAPTER));
		
		// Playable characters only.
		public static Map<Character, Set<Integer>> charactersWithMultiplePortraits = createMultiPortraitMap();
		private static Map<Character, Set<Integer>> createMultiPortraitMap() {
			Map<Character, Set<Integer>> map = new HashMap<Character, Set<Integer>>();
			map.put(ELIWOOD, new HashSet<Integer>(Arrays.asList(0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B)));
			map.put(HECTOR, new HashSet<Integer>(Arrays.asList(0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15)));
			map.put(LYN, new HashSet<Integer>(Arrays.asList(0x16, 0x17, 0x18, 0x19, 0x1A)));
			map.put(LYN_TUTORIAL, new HashSet<Integer>(Arrays.asList(0x16, 0x17, 0x18, 0x19, 0x1A)));
			map.put(NINIAN, new HashSet<Integer>(Arrays.asList(0x1C, 0x1D, 0x1E)));
			map.put(JAFFAR, new HashSet<Integer>(Arrays.asList(0x21, 0x22)));
			map.put(FLORINA, new HashSet<Integer>(Arrays.asList(0x33, 0x34)));
			map.put(NILS, new HashSet<Integer>(Arrays.asList(0x41, 0x42, 0x43, 0x44)));
			map.put(NILS_FINALCHAPTER, new HashSet<Integer>(Arrays.asList(0x41, 0x42, 0x43, 0x44)));
			return map;
		}
		
		public Boolean isLord() {
			return allLords.contains(this);
		}
		
		public Boolean isThief() {
			return allThieves.contains(this);
		}
		
		public Boolean isSpecial() {
			return allSpecial.contains(this);
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
		
		public Boolean requiresAttack() {
			return requiredAttackers.contains(this);
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
		
		public Boolean canBuff() {
			return !doNotBuff.contains(this);
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
		
		MERCENARY(0x0A), MYRMIDON(0x0E), FIGHTER(0x12), KNIGHT(0x14), ARCHER(0x18), MONK(0x1C), MAGE(0x20), SHAMAN(0x24), CAVALIER(0x28), NOMAD(0x2E), WYVERN_RIDER(0x34), SOLDIER(0x38),
		BRIGAND(0x39), PIRATE(0x3A), THIEF(0x3C), BARD(0x41), CORSAIR(0x50),
		
		ARCHER_F(0x19), CLERIC(0x1D), MAGE_F(0x21), TROUBADOUR(0x2C), PEGASUS_KNIGHT(0x32), DANCER(0x40),
		
		MERCENARY_F(0x0B), // Doesn't exist naturally. Dysfunctional map sprites. Uses male battle animations.
		MYRMIDON_F(0x0F), // Doesn't exist naturally. Uses male battle animations.
		KNIGHT_F(0x15), // Doesn't exist naturally.
		SHAMAN_F(0x25), // Doesn't exist naturally. Uses male battle animations.
		CAVALIER_F(0x29), // Doesn't exist naturally. uses male map sprites. Uses a mix of male Cavalier and Assassin(!) animations.
		NOMAD_F(0x2F), // Uses male battle animations. 
		WYVERN_RIDER_F(0x35), // Doesn't exist naturally.
		THIEF_F(0x3D), // For Leila
		
		HERO(0x0C), SWORDMASTER(0x10), WARRIOR(0x13), GENERAL(0x16), SNIPER(0x1A), BISHOP(0x1E), SAGE(0x22), DRUID(0x26), PALADIN(0x2A), NOMAD_TROOPER(0x30), WYVERN_LORD(0x36), 
		BERSERKER(0x3B), ASSASSIN(0x3E),
		
		SWORDMASTER_F(0x11), SNIPER_F(0x1B), BISHOP_F(0x1F), SAGE_F(0x23), PALADIN_F(0x2B), VALKYRIE(0x2D), FALCON_KNIGHT(0x33), WYVERN_LORD_F(0x37), 
		
		HERO_F(0x0D), // Doesn't exist naturally. Uses male Hero battle animations.
		GENERAL_F(0x17), // Uses male General battle animations.
		DRUID_F(0x27), // Uses male Druid battle animations.
		NOMAD_TROOPER_F(0x31), // Doesn't exist naturally. Uses male Nomad Trooper battle animations.
		
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
				WYVERN_RIDER, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, CORSAIR, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID, PALADIN, NOMAD_TROOPER, WYVERN_LORD,
				BERSERKER, ASSASSIN, LORD_KNIGHT, GREAT_LORD));
		public static Set<CharacterClass> allFemaleClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_LYN, BLADE_LORD, ARCHER_F, CLERIC, MAGE_F, TROUBADOUR, PEGASUS_KNIGHT, DANCER, SWORDMASTER_F, SNIPER_F,
				BISHOP_F, SAGE_F, PALADIN_F, VALKYRIE, FALCON_KNIGHT, WYVERN_LORD_F, UBER_SAGE));
		public static Set<CharacterClass> allLordClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_ELIWOOD, LORD_LYN, LORD_HECTOR, LORD_KNIGHT, BLADE_LORD, GREAT_LORD));
		public static Set<CharacterClass> allThiefClasses = new HashSet<CharacterClass>(Arrays.asList(THIEF, ASSASSIN));
		public static Set<CharacterClass> allVillageDestroyingClasses = new HashSet<CharacterClass>(Arrays.asList(THIEF, BRIGAND, PIRATE));
		public static Set<CharacterClass> allSpecialClasses = new HashSet<CharacterClass>(Arrays.asList(DANCER, BARD));
		public static Set<CharacterClass> allUnpromotedClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_ELIWOOD, LORD_LYN, LORD_HECTOR, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, MONK, MAGE, SHAMAN,
				CAVALIER, NOMAD, WYVERN_RIDER, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, CORSAIR, ARCHER_F, CLERIC, MAGE_F, TROUBADOUR, PEGASUS_KNIGHT, DANCER));
		public static Set<CharacterClass> allPromotedClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_KNIGHT, BLADE_LORD, GREAT_LORD, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID,
				PALADIN, NOMAD_TROOPER, WYVERN_LORD, BERSERKER, ASSASSIN, SWORDMASTER_F, SNIPER_F, BISHOP_F, SAGE_F, PALADIN_F, VALKYRIE, FALCON_KNIGHT, WYVERN_LORD_F, UBER_SAGE));
		public static Set<CharacterClass> allPacifistClasses = new HashSet<CharacterClass>(Arrays.asList(DANCER, BARD, CLERIC, TROUBADOUR));
		public static Set<CharacterClass> allMeleeLockedClasses = new HashSet<CharacterClass>(Arrays.asList(MYRMIDON, MERCENARY, LORD_LYN, LORD_ELIWOOD, THIEF));
		
		public static Set<CharacterClass> allValidClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_ELIWOOD, LORD_HECTOR, MERCENARY, MYRMIDON, FIGHTER, KNIGHT, ARCHER, MONK, MAGE, SHAMAN, CAVALIER, NOMAD,
				WYVERN_RIDER, SOLDIER, BRIGAND, PIRATE, THIEF, BARD, CORSAIR, HERO, SWORDMASTER, WARRIOR, GENERAL, SNIPER, BISHOP, SAGE, DRUID, PALADIN, NOMAD_TROOPER, WYVERN_LORD,
				BERSERKER, ASSASSIN, LORD_LYN, BLADE_LORD, ARCHER_F, CLERIC, MAGE_F, TROUBADOUR, PEGASUS_KNIGHT, DANCER, SWORDMASTER_F, SNIPER_F,
				BISHOP_F, SAGE_F, PALADIN_F, VALKYRIE, FALCON_KNIGHT, WYVERN_LORD_F, LORD_KNIGHT, GREAT_LORD, UBER_SAGE));
		
		public static Set<CharacterClass> allPlayerOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(DANCER, BARD));
		
		public static Set<CharacterClass> flyingClasses = new HashSet<CharacterClass>(Arrays.asList(WYVERN_RIDER, WYVERN_LORD, PEGASUS_KNIGHT, FALCON_KNIGHT));
		
		public static Set<CharacterClass> meleeOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_ELIWOOD, MERCENARY, MYRMIDON, THIEF, SWORDMASTER, ASSASSIN, LORD_LYN, SWORDMASTER_F));
		public static Set<CharacterClass> rangedOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(ARCHER, NOMAD, SNIPER, ARCHER_F, SNIPER_F));
		
		public static Map<CharacterClass, CharacterClass> promotionMap = createPromotionMap();
		private static Map<CharacterClass, CharacterClass> createPromotionMap() {
			Map<CharacterClass, CharacterClass> map = new HashMap<CharacterClass, CharacterClass>();
			map.put(LORD_ELIWOOD, LORD_KNIGHT);
			map.put(LORD_LYN, BLADE_LORD);
			map.put(LORD_HECTOR, GREAT_LORD); 
			map.put(MERCENARY, HERO);
			map.put(MYRMIDON, SWORDMASTER);
			map.put(FIGHTER, WARRIOR);
			map.put(KNIGHT, GENERAL);
			map.put(ARCHER, SNIPER);
			map.put(MONK, BISHOP);
			map.put(MAGE, SAGE);
			map.put(SHAMAN, DRUID);
			map.put(CAVALIER, PALADIN);
			map.put(NOMAD, NOMAD_TROOPER);
			map.put(WYVERN_RIDER, WYVERN_LORD);
			map.put(SOLDIER, GENERAL);
			map.put(BRIGAND, BERSERKER);
			map.put(PIRATE, BERSERKER);
			map.put(THIEF, ASSASSIN);
			map.put(CORSAIR, BERSERKER);
			map.put(ARCHER_F, SNIPER_F);
			map.put(CLERIC, BISHOP_F); 
			map.put(MAGE_F, SAGE_F);
			map.put(TROUBADOUR, VALKYRIE);
			map.put(PEGASUS_KNIGHT, FALCON_KNIGHT);
			return map;
		}
		
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
			case PEGASUS_KNIGHT: {
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
				classList.add(PEGASUS_KNIGHT);
				classList.add(SOLDIER);
				break;
			}
			default:
				break;
			}
			
			return classList;
		}
		
		public static Set<CharacterClass> targetClassesForRandomization(CharacterClass sourceClass, boolean isForEnemy, Boolean excludeSource, Boolean excludeLords, Boolean excludeThieves, Boolean excludeSpecial, Boolean requireAttack, Boolean requiresRange, Boolean applyRestrictions, Boolean restrictGender) {
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
			
			if (excludeSpecial) {
				classList.removeAll(allSpecialClasses);
			}
			
			if (requireAttack) {
				classList.removeAll(allPacifistClasses);
			}
			if (requiresRange) {
				classList.removeAll(allPacifistClasses);
				classList.removeAll(allMeleeLockedClasses);
			}
			
			if (restrictGender) {
				if (sourceClass.isFemale()) {
					classList.retainAll(allFemaleClasses);
				} else {
					classList.retainAll(allMaleClasses);
				}
			}
			
			classList.retainAll(allValidClasses);
			
			if (isForEnemy) {
				classList.removeAll(allPlayerOnlyClasses);
			}
			
			return classList;
		}
		
		private static Set<CharacterClass> limitedClassesForRandomization(CharacterClass sourceClass) {
			switch(sourceClass) {
			case WYVERN_RIDER:
			case PEGASUS_KNIGHT:
				return new HashSet<CharacterClass>(Arrays.asList(WYVERN_RIDER, PEGASUS_KNIGHT));
			case WYVERN_LORD:
			case WYVERN_LORD_F:
			case FALCON_KNIGHT:
				return new HashSet<CharacterClass>(Arrays.asList(WYVERN_LORD, WYVERN_LORD_F, FALCON_KNIGHT));
			case PIRATE:
			case CORSAIR:
				return new HashSet<CharacterClass>(Arrays.asList(PIRATE, CORSAIR, WYVERN_RIDER, PEGASUS_KNIGHT));
			case BRIGAND:
				return new HashSet<CharacterClass>(Arrays.asList(BRIGAND, WYVERN_RIDER, PEGASUS_KNIGHT));
			case BERSERKER:
				return new HashSet<CharacterClass>(Arrays.asList(BERSERKER, WYVERN_LORD, WYVERN_LORD_F, FALCON_KNIGHT));
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
		
		public Boolean canDestroyVillages() {
			return CharacterClass.allVillageDestroyingClasses.contains(this);
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
		
		public int animationID() {
			switch (this) {
			case LORD_ELIWOOD: return 0x0;
			case LORD_KNIGHT: return 0x2;
			case LORD_HECTOR: return 0x6;
			case GREAT_LORD: return 0x9;
			case LORD_LYN: return 0x0E;
			case BLADE_LORD: return 0x10;
			case BRIGAND: return 0x14;
			case CORSAIR:
			case PIRATE: return 0x17;
			case FIGHTER: return 0x1D;
			case WARRIOR: return 0x20;
			case ARCHER: return 0x24;
			case ARCHER_F: return 0x26;
			case SNIPER: return 0x28;
			case SNIPER_F: return 0x2A;
			case MERCENARY: return 0x2C;
			case HERO: return 0x2E;
			case MYRMIDON: return 0x32;
			case SWORDMASTER: return 0x34;
			case SWORDMASTER_F: return 0x38;
			case CAVALIER: return 0x3A;
			case PALADIN: return 0x43;
			case PALADIN_F: return 0x4D;
			case SOLDIER: return 0x4F;
			case KNIGHT: return 0x51;
			case GENERAL: return 0x53;
			case MAGE: return 0x57;
			case MAGE_F: return 0x58;
			case SAGE: return 0x59;
			case SAGE_F: return 0x5B;
			case CLERIC: return 0x61;
			case MONK: return 0x63;
			case BISHOP: return 0x64;
			case BISHOP_F: return 0x66;
			case SHAMAN: return 0x68;
			case DRUID: return 0x69;
			case TROUBADOUR: return 0x6D;
			case VALKYRIE: return 0x6F;
			case NOMAD: return 0x73;
			case NOMAD_TROOPER: return 0x76;
			case THIEF: return 0x78;
			case ASSASSIN: return 0x7E;
			case PEGASUS_KNIGHT: return 0x80;
			case FALCON_KNIGHT: return 0x83;
			case WYVERN_RIDER: return 0x85;
			case WYVERN_LORD_F:
			case WYVERN_LORD: return 0x87;
			case DANCER: return 0x8C;
			case BARD: return 0x8D;
			case BERSERKER: return 0x9C;
			default: 
				assert false: "Unhandled class animation ID.";
				return 0x1;
			}
		}
		
		public String primaryWeaponType() {
			switch (this) {
			case LORD_ELIWOOD:
			case LORD_KNIGHT:
			case LORD_LYN:
			case BLADE_LORD:
			case MERCENARY:
			case HERO:
			case MYRMIDON:
			case SWORDMASTER:
			case SWORDMASTER_F:
			case THIEF:
			case ASSASSIN:
				return "Sword";
			case CAVALIER:
			case PALADIN:
			case PALADIN_F:
			case SOLDIER:
			case KNIGHT:
			case GENERAL:
			case PEGASUS_KNIGHT:
			case FALCON_KNIGHT:
			case WYVERN_RIDER:
			case WYVERN_LORD:
			case WYVERN_LORD_F:
				return "Lance";
			case LORD_HECTOR:
			case GREAT_LORD:
			case PIRATE:
			case CORSAIR:
			case FIGHTER:
			case WARRIOR:
			case BERSERKER:
				return "Axe";
			case ARCHER:
			case ARCHER_F:
			case SNIPER:
			case SNIPER_F:
			case NOMAD:
			case NOMAD_TROOPER:
				return "Bow";
			case MAGE:
			case MAGE_F:
			case SAGE:
			case SAGE_F:
				return "Anima";
			case MONK:
			case BISHOP:
			case BISHOP_F:
			case VALKYRIE:
				return "Light";
			case SHAMAN:
			case DRUID:
				return "Dark";
			case CLERIC:
			case TROUBADOUR:
				return "Staff";
			default:
				return "None";
			}
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
			private static Map<Ability1Mask, String> displayStrings = new HashMap<Ability1Mask, String>();
			
			static {
				for (Ability1Mask ability : Ability1Mask.values()) {
					map.put(ability.ID, ability);
					switch (ability) {
					case WEAPON:
						displayStrings.put(ability, "Is Weapon (0x01)");
						break;
					case MAGIC:
						displayStrings.put(ability, "Is Magic (0x02)");
						break;
					case STAFF:
						displayStrings.put(ability, "Is Staff (0x04)");
						break;
					case UNBREAKABLE:
						displayStrings.put(ability, "Is Unbreakable (0x08)");
						break;
					case UNSELLABLE:
						displayStrings.put(ability, "Is Unsellable (0x10)");
						break;
					case BRAVE:
						displayStrings.put(ability, "Brave (0x20)");
						break;
					case MAGIC_DAMAGE:
						displayStrings.put(ability, "Magic Damage (0x40)");
						break;
					case UNCOUNTERABLE:
						displayStrings.put(ability, "Uncounterable (0x80)");
						break;
					default:
						break;
					}
				}
			}
			
			private Ability1Mask(final int id) { ID = id; }
			
			public static Ability1Mask valueOf(int val) {
				return map.get(val);
			}
			
			public static Ability1Mask maskForDisplayString(String string) {
				Ability1Mask mask = null;
				try {
					mask = displayStrings.keySet().stream().filter(ability -> string.equals(displayStrings.get(ability))).findFirst().get();
				} catch (NoSuchElementException e) {}
				
				return mask;
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
			NONE(0x00), REVERSE_WEAPON_TRIANGLE(0x01), UNKNOWN(0x02), DRAGONSTONE_LOCK(0x04), UNUSED_WEAPON_LOCK(0x08),
			MYRMIDON_LOCK(0x10), UNKNOWN_2(0x20), IOTE_SHIELD_EFFECT(0x40), IRON_RUNE_EFFECT(0x80);
			public int ID;
			
			private static Map<Integer, Ability2Mask> map = new HashMap<Integer, Ability2Mask>();
			private static Map<Ability2Mask, String> displayStrings = new HashMap<Ability2Mask, String>();
			
			static {
				for (Ability2Mask ability : Ability2Mask.values()) {
					map.put(ability.ID, ability);
					switch (ability) {
					case REVERSE_WEAPON_TRIANGLE:
						displayStrings.put(ability, "Reverse Triangle (0x01)");
						break;
					case UNKNOWN:
						displayStrings.put(ability, "Unknown (0x02)");
						break;
					case DRAGONSTONE_LOCK:
						displayStrings.put(ability, "Lock - Dragonstone (0x04)");
						break;
					case UNUSED_WEAPON_LOCK:
						displayStrings.put(ability, "Lock - Unused (0x08)");
						break;
					case MYRMIDON_LOCK:
						displayStrings.put(ability, "Lock - Myrmidon (0x10)");
						break;
					case UNKNOWN_2:
						displayStrings.put(ability, "Unknown (0x20)");
						break;
					case IOTE_SHIELD_EFFECT:
						displayStrings.put(ability, "Iote Shield (0x40)");
						break;
					case IRON_RUNE_EFFECT:
						displayStrings.put(ability, "Iron Rune (0x80)");
					default:
						break;
					}
				}
			}
			
			private Ability2Mask(final int id) { ID = id; }
			
			public static Ability2Mask valueOf(int val) {
				return map.get(val);
			}
			
			public static Ability2Mask maskForDisplayString(String string) {
				Ability2Mask mask = null;
				try {
					mask = displayStrings.keySet().stream().filter(ability -> string.equals(displayStrings.get(ability))).findFirst().get();
				} catch (NoSuchElementException e) {}
				
				return mask;
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
			NONE(0x00), UNKNOWN(0x01), NEGATE_DEFENSE(0x02), ELIWOOD_LOCK(0x04), HECTOR_LOCK(0x08),
			LYN_LOCK(0x10), ATHOS_LOCK(0x20), UNKNOWN_2(0x40), UNKNOWN_3(0x80);
			
			public int ID;
			
			private static Map<Integer, Ability3Mask> map = new HashMap<Integer, Ability3Mask>();
			private static Map<Ability3Mask, String> displayStrings = new HashMap<Ability3Mask, String>();
			
			static {
				for (Ability3Mask ability : Ability3Mask.values()) {
					map.put(ability.ID, ability);
					switch (ability) {
					case UNKNOWN:
						displayStrings.put(ability, "Unknown (0x01)");
						break;
					case NEGATE_DEFENSE:
						displayStrings.put(ability, "Negate Defense (0x02)");
						break;
					case ELIWOOD_LOCK:
						displayStrings.put(ability, "Lock - Eliwood (0x04)");
						break;
					case HECTOR_LOCK:
						displayStrings.put(ability, "Lock - Hector (0x08)");
						break;
					case LYN_LOCK:
						displayStrings.put(ability, "Lock - Lyn (0x10)");
						break;
					case ATHOS_LOCK:
						displayStrings.put(ability, "Lock - Athos (0x20)");
						break;
					case UNKNOWN_2:
						displayStrings.put(ability, "Unknown (0x40)");
						break;
					case UNKNOWN_3:
						displayStrings.put(ability, "Unknown (0x80)");
						break;
					default:
						break;
					}
				}
			}
			
			private Ability3Mask(final int id) { ID = id; }
			
			public static Ability3Mask valueOf(int val) {
				return map.get(val);
			}
			
			public static Ability3Mask maskForDisplayString(String string) {
				Ability3Mask mask = null;
				try {
					mask = displayStrings.keySet().stream().filter(ability -> string.equals(displayStrings.get(ability))).findFirst().get();
				} catch (NoSuchElementException e) {}
				
				return mask;
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
			private static Map<WeaponEffect, String> displayStrings = new HashMap<WeaponEffect, String>();
			
			static {
				for (WeaponEffect effect : WeaponEffect.values()) {
					map.put(effect.ID, effect);
					switch (effect) {
					case POISON:
						displayStrings.put(effect, "Poison (0x01)");
						break;
					case STEALS_HP:
						displayStrings.put(effect, "Steals HP (0x02)");
						break;
					case HALVES_HP:
						displayStrings.put(effect, "Halves HP (0x03)");
						break;
					case DEVIL:
						displayStrings.put(effect, "Devil (0x04)");
						break;
					default:
						break;
					}
				}
			}
			
			private WeaponEffect(final int id) { ID = id; }
			
			public static WeaponEffect valueOf(int val) {
				return map.get(val);
			}
			
			public static WeaponEffect effectForDisplayString(String string) {
				WeaponEffect effect = null;
				try {
					effect = displayStrings.keySet().stream().filter(eff -> string.equals(displayStrings.get(eff))).findFirst().get();
				} catch (NoSuchElementException e) {}
				
				return effect;
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
		
		public static Set<Item> allSiegeTomes = new HashSet<Item>(Arrays.asList(BOLTING, PURGE, ECLIPSE));
		
		public static Set<Item> allPotentialRewards = new HashSet<Item>(Arrays.asList(/*IRON_SWORD, SLIM_SWORD, EMBLEM_SWORD, IRON_LANCE, SLIM_LANCE,*/ JAVELIN, /*EMBLEM_LANCE, POISON_LANCE,*/ HAND_AXE, /*IRON_AXE, EMBLEM_AXE,*/ STEEL_AXE,
				DEVIL_AXE, /*IRON_BOW, EMBLEM_BOW, FIRE, LIGHTNING, HEAL, POISON_SWORD,*/ STEEL_SWORD, IRON_BLADE, ARMORSLAYER, LONGSWORD, WO_DAO, STEEL_LANCE, HEAVY_SPEAR, HORSESLAYER, /*POISON_AXE,*/ HALBERD, HAMMER, /*POISON_BOW,*/
				SHORT_BOW, LONGBOW, STEEL_BOW, THUNDER, SHINE, FLUX, MEND, TORCH_STAFF, UNLOCK, STEEL_BLADE, KILLING_EDGE, WYRMSLAYER, LIGHT_BRAND, LANCEREAVER, SHORT_SPEAR, KILLER_LANCE, AXEREAVER, DRAGON_AXE, KILLER_AXE, SWORDREAVER, 
				SWORDSLAYER, KILLER_BOW, ELFIRE, DIVINE, NOSFERATU, RECOVER, RESTORE, HAMMERNE, BARRIER, BRAVE_SWORD, WIND_SWORD, BRAVE_LANCE, SPEAR, BRAVE_AXE, BRAVE_BOW, BOLTING, PURGE, ECLIPSE, PHYSIC, SILENCE, SLEEP, BERSERK, 
				RESCUE, SILVER_SWORD, SILVER_BLADE, RUNE_SWORD, SILVER_LANCE, TOMAHAWK, SILVER_AXE, SILVER_BOW, FIMBULVETR, AURA, FENRIR, FORTIFY, WARP,
				ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWINGS, GODDESS_ICON, DRAGONSHIELD, TALISMAN, BOOTS, BODY_RING, AFA_DROPS,
				HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP, GUIDING_RING, EARTH_SEAL, HEAVEN_SEAL, EMBLEM_SEAL, FELL_CONTRACT, OCEAN_SEAL,
				FILLA_MIGHT, NINI_GRACE, THOR_IRE, SET_LITANY,
				DELPHI_SHIELD, MEMBER_CARD, IRON_RUNE, SILVER_CARD,
				WHITE_GEM, BLUE_GEM, RED_GEM));
		
		public static Set<Item> commonDrops = new HashSet<Item>(Arrays.asList(VULNERARY, ANTITOXIN, TORCH, MINE, LIGHT_RUNE,
				DOOR_KEY, CHEST_KEY, CHEST_KEY_5, PURE_WATER, ELIXIR, RED_GEM));
		public static Set<Item> uncommonDrops = new HashSet<Item>(Arrays.asList(HERO_CREST, KNIGHT_CREST, ORION_BOLT, ELYSIAN_WHIP,
				GUIDING_RING, EARTH_SEAL, FELL_CONTRACT, OCEAN_SEAL, AFA_DROPS, BLUE_GEM));
		public static Set<Item> rareDrops = new HashSet<Item>(Arrays.asList(ANGELIC_ROBE, ENERGY_RING, SECRET_BOOK, SPEEDWINGS,
				GODDESS_ICON, DRAGONSHIELD, TALISMAN, BOOTS, BODY_RING, WHITE_GEM));
		
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
		
		public static Set<Item> normalSet = new HashSet<Item>(Arrays.asList(IRON_SWORD, SLIM_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, EMBLEM_SWORD, IRON_LANCE, 
				SLIM_LANCE, STEEL_LANCE, SILVER_LANCE, EMBLEM_LANCE, IRON_AXE, STEEL_AXE, SILVER_AXE, EMBLEM_AXE, IRON_BOW, STEEL_BOW, SILVER_BOW, SHORT_BOW, EMBLEM_BOW, FIRE, THUNDER, ELFIRE, 
				FIMBULVETR, LIGHTNING, SHINE, DIVINE, AURA, FLUX, FENRIR, HEAL, MEND, RECOVER));
		public static Set<Item> interestingSet = new HashSet<Item>(Arrays.asList(POISON_SWORD, RAPIER, MANI_KATTI, BRAVE_SWORD,
				WO_DAO, KILLING_EDGE, ARMORSLAYER, WYRMSLAYER, LIGHT_BRAND, RUNE_SWORD, LANCEREAVER, LONGSWORD, WIND_SWORD, POISON_LANCE, BRAVE_LANCE, KILLER_LANCE, HORSESLAYER, JAVELIN, SPEAR, AXEREAVER, HEAVY_SPEAR, SHORT_SPEAR, 
				POISON_AXE, BRAVE_AXE, KILLER_AXE, HALBERD, HAMMER, DEVIL_AXE, HAND_AXE, TOMAHAWK, SWORDREAVER, SWORDSLAYER, DRAGON_AXE, 
				WOLF_BEIL, POISON_BOW, KILLER_BOW, BRAVE_BOW, LONGBOW, THUNDER, BOLTING, SHINE, PURGE, LUNA, NOSFERATU, ECLIPSE, PHYSIC, FORTIFY, RESTORE, WARP, RESCUE, TORCH_STAFF, HAMMERNE, UNLOCK, BARRIER, SILENCE, SLEEP, BERSERK));
		public static Set<Item> promoSet = new HashSet<Item>(Arrays.asList(EMBLEM_AXE, EMBLEM_BOW, EMBLEM_LANCE, EMBLEM_SWORD, EMBLEM_SEAL));
		
		// This list is anything that can replace a siege tome. It can be a higher rank, since we'll filter that out later if needed.
		public static Set<Item> siegeReplacementSet = new HashSet<Item>(Arrays.asList(DIVINE, NOSFERATU, ELFIRE, AURA, FENRIR, FIMBULVETR));
		
		public static Set<Item> tomeRangeReplacementSet = new HashSet<Item>(Arrays.asList(WIND_SWORD, LIGHT_BRAND, RUNE_SWORD, JAVELIN, SPEAR, SHORT_SPEAR, HAND_AXE, TOMAHAWK, LONGBOW));
		
		public static Set<Item> killerSet = new HashSet<Item>(Arrays.asList(KILLING_EDGE, WO_DAO, MANI_KATTI, KILLER_LANCE, KILLER_AXE, KILLER_BOW, LUNA));
		public static Set<Item> effectiveSet = new HashSet<Item>(Arrays.asList(RAPIER, MANI_KATTI, ARMORSLAYER, WYRMSLAYER, LONGSWORD, HORSESLAYER, HEAVY_SPEAR, HALBERD, HAMMER, SWORDSLAYER, DRAGON_AXE, WOLF_BEIL));
		public static Set<Item> poisonSet = new HashSet<Item>(Arrays.asList(POISON_SWORD, POISON_AXE, POISON_LANCE, POISON_BOW));
		public static Set<Item> rangedSet = new HashSet<Item>(Arrays.asList(WIND_SWORD, LIGHT_BRAND, RUNE_SWORD, JAVELIN, SPEAR, SHORT_SPEAR, HAND_AXE, TOMAHAWK, LONGBOW, BOLTING, PURGE, ECLIPSE, PHYSIC));
		public static Set<Item> reaverSet = new HashSet<Item>(Arrays.asList(LANCEREAVER, AXEREAVER, SWORDREAVER, SWORDSLAYER));
		public static Set<Item> braveSet = new HashSet<Item>(Arrays.asList(BRAVE_SWORD, BRAVE_LANCE, BRAVE_AXE, BRAVE_BOW));
		
		public static Set<Item> allRestrictedWeapons = new HashSet<Item>(Arrays.asList(WO_DAO));
		
		public static Set<Item> allBasicWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, IRON_LANCE, IRON_AXE, IRON_BOW, FIRE, LIGHTNING, FLUX));
		public static Set<Item> allSteelWeapons = new HashSet<Item>(Arrays.asList(STEEL_SWORD, STEEL_LANCE, STEEL_AXE, STEEL_BOW, THUNDER));
		public static Set<Item> allBasicThrownWeapons = new HashSet<Item>(Arrays.asList(JAVELIN, HAND_AXE));
		
		public static Set<Item> vendorItems = new HashSet<Item>(Arrays.asList(CHEST_KEY, CHEST_KEY_5, DOOR_KEY, LOCKPICK, VULNERARY, PURE_WATER, ANTITOXIN, TORCH));
		
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
			
			if (min == WeaponRank.NONE && max == WeaponRank.NONE) {
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
		
		public Boolean isSteelWeapon() {
			return allSteelWeapons.contains(this);
		}
		
		public Boolean isBasicThrownWeapon() {
			return allBasicThrownWeapons.contains(this);
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
		
		public Boolean isPromoWeapon() {
			return promoSet.contains(this);
		}
		
		public Boolean isPoisonWeapon() {
			return poisonSet.contains(this);
		}
	}
	
	public enum ChapterMetadata implements GBAFEChapterMetadataChapter {
		
		PROLOGUE_A_GIRL_FROM_THE_PLAINS(0x0),
		CHAPTER_1_FOOTSTEPS_OF_FATE(0x1),
		CHAPTER_2_SWORD_OF_SPIRITS(0x2),
		CHAPTER_3_BAND_OF_MERCENARIES(0x3),
		CHAPTER_4_IN_OCCUPATIONS_SHADOW(0x4),
		CHAPTER_5_BEYOND_THE_BORDERS(0x5),
		CHAPTER_6_BLOOD_OF_PRIDE(0x6),
		CHAPTER_7_SIBLINGS_ABROAD(0x7),
		CHAPTER_7X_THE_BLACK_SHADOW(0x8),
		CHAPTER_8_VORTEX_OF_STRATEGY(0x9),
		CHAPTER_9_A_GRIM_REUNION(0xA),
		CHAPTER_10_THE_DISTANT_PLAINS(0xB),
		CHAPTER_11E_TAKING_LEAVE(0xC),
		CHAPTER_11H_ANOTHER_JOURNEY(0xD),
		CHAPTER_12_BIRDS_OF_A_FEATHER(0xE),
		CHAPTER_13_IN_SEARCH_OF_TRUTH(0xF),
		CHAPTER_13X_THE_PEDDLER_MERLINUS(0x10),
		CHAPTER_14_FALSE_FRIENDS(0x11),
		CHAPTER_15_TALONS_ALIGHT(0x12),
		CHAPTER_16_NOBLE_LADY_OF_CAELIN(0x13),
		CHAPTER_17_WHEREABOUTS_UNKNOWN(0x14),
		CHAPTER_17X_THE_PORT_OF_BADON(0x15), 
		CHAPTER_18_PIRATE_SHIP(0x16),
		CHAPTER_19_THE_DREAD_ISLE(0x17),
		CHAPTER_19X_IMPRISONER_OF_MAGIC(0x18),
		CHAPTER_19XX_A_GLIMPSE_IN_TIME(0x19),
		CHAPTER_20_DRAGONS_GATE(0x1A),
		CHAPTER_21_NEW_RESOLVE(0x1B),
		CHAPTER_22_KINSHIPS_BOND(0x1C),
		CHAPTER_23_LIVING_LEGEND(0x1D),
		CHAPTER_23X_GENESIS(0x1E),
		CHAPTER_24_FOUR_FANGED_OFFENSE_LLOYD(0x1F),
		CHAPTER_24_FOUR_FANGED_OFFENSE_LINUS(0x20),
		CHAPTER_25_CRAZED_BEAST(0x21),
		CHAPTER_26_UNFULFILLED_HEART(0x22),
		CHAPTER_27_PALE_FLOWER_OF_DARKNESS_KENNETH(0x23),
		CHAPTER_27_PALE_FLOWER_OF_DARKNESS_JERME(0x24),
		CHAPTER_28_BATTLE_BEFORE_DAWN(0x25),
		CHAPTER_28X_NIGHT_OF_FAREWELLS(0x26),
		CHAPTER_29_COG_OF_DESTINY(0x27),
		CHAPTER_28E_VALOROUS_ROLAND(0x28),
		CHAPTER_30H_THE_BERSERKER(0x29),
		CHAPTER_31_SANDS_OF_TIME(0x2A),
		CHAPTER_31X_BATTLE_PREPARATIONS(0x2B),
		CHAPTER_32_VICTORY_OR_DEATH(0x2C),
		CHAPTER_32X_THE_VALUE_OF_LIFE(0x2D),
		FINAL_CHAPTER_LIGHT_1(0x2E),
		FINAL_CHAPTER_LIGHT_2(0x2F);
		
		public int index;
		
		private ChapterMetadata(int index) {
			this.index = index;
		}
		
		public boolean fogOfWarAllowed() {
			switch (this) {
			case CHAPTER_19X_IMPRISONER_OF_MAGIC:
			case CHAPTER_23X_GENESIS:
			case CHAPTER_32X_THE_VALUE_OF_LIFE:
				return false;
			default:
				return true;
			}
		}
		
		public static ChapterMetadata[] orderedChapters() {
			return new ChapterMetadata[] {
					PROLOGUE_A_GIRL_FROM_THE_PLAINS,
					CHAPTER_1_FOOTSTEPS_OF_FATE,
					CHAPTER_2_SWORD_OF_SPIRITS,
					CHAPTER_3_BAND_OF_MERCENARIES,
					CHAPTER_4_IN_OCCUPATIONS_SHADOW,
					CHAPTER_5_BEYOND_THE_BORDERS,
					CHAPTER_6_BLOOD_OF_PRIDE,
					CHAPTER_7_SIBLINGS_ABROAD,
					CHAPTER_7X_THE_BLACK_SHADOW,
					CHAPTER_8_VORTEX_OF_STRATEGY,
					CHAPTER_9_A_GRIM_REUNION,
					CHAPTER_10_THE_DISTANT_PLAINS,
					CHAPTER_11E_TAKING_LEAVE,
					CHAPTER_11H_ANOTHER_JOURNEY,
					CHAPTER_12_BIRDS_OF_A_FEATHER,
					CHAPTER_13_IN_SEARCH_OF_TRUTH,
					CHAPTER_13X_THE_PEDDLER_MERLINUS,
					CHAPTER_14_FALSE_FRIENDS,
					CHAPTER_15_TALONS_ALIGHT,
					CHAPTER_16_NOBLE_LADY_OF_CAELIN,
					CHAPTER_17_WHEREABOUTS_UNKNOWN,
					CHAPTER_17X_THE_PORT_OF_BADON, 
					CHAPTER_18_PIRATE_SHIP,
					CHAPTER_19_THE_DREAD_ISLE,
					CHAPTER_19X_IMPRISONER_OF_MAGIC,
					CHAPTER_19XX_A_GLIMPSE_IN_TIME,
					CHAPTER_20_DRAGONS_GATE,
					CHAPTER_21_NEW_RESOLVE,
					CHAPTER_22_KINSHIPS_BOND,
					CHAPTER_23_LIVING_LEGEND,
					CHAPTER_23X_GENESIS,
					CHAPTER_24_FOUR_FANGED_OFFENSE_LLOYD,
					CHAPTER_24_FOUR_FANGED_OFFENSE_LINUS,
					CHAPTER_25_CRAZED_BEAST,
					CHAPTER_26_UNFULFILLED_HEART,
					CHAPTER_27_PALE_FLOWER_OF_DARKNESS_KENNETH,
					CHAPTER_27_PALE_FLOWER_OF_DARKNESS_JERME,
					CHAPTER_28_BATTLE_BEFORE_DAWN,
					CHAPTER_28X_NIGHT_OF_FAREWELLS,
					CHAPTER_29_COG_OF_DESTINY,
					CHAPTER_28E_VALOROUS_ROLAND,
					CHAPTER_30H_THE_BERSERKER,
					CHAPTER_31_SANDS_OF_TIME,
					CHAPTER_31X_BATTLE_PREPARATIONS,
					CHAPTER_32_VICTORY_OR_DEATH,
					CHAPTER_32X_THE_VALUE_OF_LIFE,
					FINAL_CHAPTER_LIGHT_1,
					FINAL_CHAPTER_LIGHT_2
			};
		}
		
		public String getFriendlyName() {
			switch (this) {
			case PROLOGUE_A_GIRL_FROM_THE_PLAINS: return "Prologue: A Girl From the Plains";
			case CHAPTER_1_FOOTSTEPS_OF_FATE: return "Chapter 1: Footsteps of Fate";
			case CHAPTER_2_SWORD_OF_SPIRITS: return "Chapter 2: Sword of Spirits";
			case CHAPTER_3_BAND_OF_MERCENARIES: return "Chapter 3: Band of Mercenaries";
			case CHAPTER_4_IN_OCCUPATIONS_SHADOW: return "Chapter 4: In Occupation's Shadow";
			case CHAPTER_5_BEYOND_THE_BORDERS: return "Chapter 5: Beyond the Borders";
			case CHAPTER_6_BLOOD_OF_PRIDE: return "Chapter 6: Blood of Pride";
			case CHAPTER_7_SIBLINGS_ABROAD: return "Chapter 7: Siblings Abroad";
			case CHAPTER_7X_THE_BLACK_SHADOW: return "Chapter 7x: The Black Shadow";
			case CHAPTER_8_VORTEX_OF_STRATEGY: return "Chapter 8: Vortex of Strategy";
			case CHAPTER_9_A_GRIM_REUNION: return "Chapter 9: A Grim Reunion";
			case CHAPTER_10_THE_DISTANT_PLAINS: return "Chapter 10: The Distant Plains";
			case CHAPTER_11E_TAKING_LEAVE: return "Chapter 11 (Eliwood): Taking Leave";
			case CHAPTER_11H_ANOTHER_JOURNEY: return "Chapter 11 (Hector): Another Journey";
			case CHAPTER_12_BIRDS_OF_A_FEATHER: return "Chapter 12: Birds of a Feather";
			case CHAPTER_13_IN_SEARCH_OF_TRUTH: return "Chapter 13: In Search of Truth";
			case CHAPTER_13X_THE_PEDDLER_MERLINUS: return "Chapter 13x: The Peddler Merlinus";
			case CHAPTER_14_FALSE_FRIENDS: return "Chapter 14: False Friends";
			case CHAPTER_15_TALONS_ALIGHT: return "Chapter 15: Talons Alight";
			case CHAPTER_16_NOBLE_LADY_OF_CAELIN: return "Chapter 16: Noble Lady of Caelin";
			case CHAPTER_17_WHEREABOUTS_UNKNOWN: return "Chapter 17: Whereabouts Unknown";
			case CHAPTER_17X_THE_PORT_OF_BADON: return "Chapter 17x: The Port of Badon";
			case CHAPTER_18_PIRATE_SHIP: return "Chapter 18: Pirate Ship";
			case CHAPTER_19_THE_DREAD_ISLE: return "Chapter 19: The Dread Isle";
			case CHAPTER_19X_IMPRISONER_OF_MAGIC: return "Chapter 19x: Imprisoner of Magic";
			case CHAPTER_19XX_A_GLIMPSE_IN_TIME: return "Chapter 19xx: A Glimpse in Time";
			case CHAPTER_20_DRAGONS_GATE: return "Chapter 20: Dragon's Gate";
			case CHAPTER_21_NEW_RESOLVE: return "Chapter 21: New Resolve";
			case CHAPTER_22_KINSHIPS_BOND: return "Chapter 22: Kinship's Bond";
			case CHAPTER_23_LIVING_LEGEND: return "Chapter 23: Living Legend";
			case CHAPTER_23X_GENESIS: return "Chapter 23x: Genesis";
			case CHAPTER_24_FOUR_FANGED_OFFENSE_LLOYD: return "Chapter 24: Four Fanged Offense (Lloyd)";
			case CHAPTER_24_FOUR_FANGED_OFFENSE_LINUS: return "Chapter 24: Four Fanged Offense (Linus)";
			case CHAPTER_25_CRAZED_BEAST: return "Chapter 25: Crazed Beast";
			case CHAPTER_26_UNFULFILLED_HEART: return "Chapter 26: Unfulfilled Heart";
			case CHAPTER_27_PALE_FLOWER_OF_DARKNESS_KENNETH: return "Chapter 27: Pale Flower of Darkness (Kenneth)";
			case CHAPTER_27_PALE_FLOWER_OF_DARKNESS_JERME: return "Chapter 27: Pale Flower of Darkness (Jerme)";
			case CHAPTER_28_BATTLE_BEFORE_DAWN: return "Chapter 28: Battle Before Dawn";
			case CHAPTER_28X_NIGHT_OF_FAREWELLS: return "Chapter 28x: Night of Farewells";
			case CHAPTER_29_COG_OF_DESTINY: return "Chapter 29: Cog of Destiny";
			case CHAPTER_28E_VALOROUS_ROLAND: return "Chapter 28 (Eliwood): Valorous Roland";
			case CHAPTER_30H_THE_BERSERKER: return "Chapter 30 (Hector): The Berserker";
			case CHAPTER_31_SANDS_OF_TIME: return "Chapter 31: Sands of Time";
			case CHAPTER_31X_BATTLE_PREPARATIONS: return "Chapter 31x: Battle Preparations";
			case CHAPTER_32_VICTORY_OR_DEATH: return "Chapter 32: Victory or Death";
			case CHAPTER_32X_THE_VALUE_OF_LIFE: return "Chapter 32x: The Value of Life";
			case FINAL_CHAPTER_LIGHT_1: return "Final: Light (Part 1)";
			case FINAL_CHAPTER_LIGHT_2: return "Final: Light (Part 2)";
			default: return "?";
			}
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
		
		public static List<ChapterPointer> orderedChapters() {
			return new ArrayList<ChapterPointer>(Arrays.asList(PROLOGUE, CHAPTER_1, CHAPTER_2, CHAPTER_3, CHAPTER_4,
					CHAPTER_5, CHAPTER_6, CHAPTER_7, CHAPTER_7X, CHAPTER_8, CHAPTER_9, CHAPTER_10, CHAPTER_11_E,
					CHAPTER_11_H, CHAPTER_12, CHAPTER_13, CHAPTER_13X, CHAPTER_14, CHAPTER_15, CHAPTER_16,
					CHAPTER_17, CHAPTER_17X, CHAPTER_18, CHAPTER_19, CHAPTER_19X, CHAPTER_19XX, CHAPTER_20,
					CHAPTER_21, CHAPTER_22, CHAPTER_23, CHAPTER_23X, CHAPTER_24_LLOYD, CHAPTER_24_LINUS,
					CHAPTER_25, CHAPTER_26, CHAPTER_27_KENNETH, CHAPTER_27_JERME, CHAPTER_28, CHAPTER_28X,
					CHAPTER_28_E, CHAPTER_29, CHAPTER_30_H, CHAPTER_31, CHAPTER_31X, CHAPTER_32, CHAPTER_32X,
					CHAPTER_FINAL, CHAPTER_FINAL_2));
		}
		
		public ChapterMetadata getMetadata() {
			switch (this) {
			case PROLOGUE: return ChapterMetadata.PROLOGUE_A_GIRL_FROM_THE_PLAINS;
			case CHAPTER_1: return ChapterMetadata.CHAPTER_1_FOOTSTEPS_OF_FATE;
			case CHAPTER_2: return ChapterMetadata.CHAPTER_2_SWORD_OF_SPIRITS;
			case CHAPTER_3: return ChapterMetadata.CHAPTER_3_BAND_OF_MERCENARIES;
			case CHAPTER_4: return ChapterMetadata.CHAPTER_4_IN_OCCUPATIONS_SHADOW;
			case CHAPTER_5: return ChapterMetadata.CHAPTER_5_BEYOND_THE_BORDERS;
			case CHAPTER_6: return ChapterMetadata.CHAPTER_6_BLOOD_OF_PRIDE;
			case CHAPTER_7: return ChapterMetadata.CHAPTER_7_SIBLINGS_ABROAD;
			case CHAPTER_7X: return ChapterMetadata.CHAPTER_7X_THE_BLACK_SHADOW;
			case CHAPTER_8: return ChapterMetadata.CHAPTER_8_VORTEX_OF_STRATEGY;
			case CHAPTER_9: return ChapterMetadata.CHAPTER_9_A_GRIM_REUNION;
			case CHAPTER_10: return ChapterMetadata.CHAPTER_10_THE_DISTANT_PLAINS;
			case CHAPTER_11_E: return ChapterMetadata.CHAPTER_11E_TAKING_LEAVE;
			case CHAPTER_11_H: return ChapterMetadata.CHAPTER_11H_ANOTHER_JOURNEY;
			case CHAPTER_12: return ChapterMetadata.CHAPTER_12_BIRDS_OF_A_FEATHER;
			case CHAPTER_13: return ChapterMetadata.CHAPTER_13_IN_SEARCH_OF_TRUTH;
			case CHAPTER_13X: return ChapterMetadata.CHAPTER_13X_THE_PEDDLER_MERLINUS;
			case CHAPTER_14: return ChapterMetadata.CHAPTER_14_FALSE_FRIENDS;
			case CHAPTER_15: return ChapterMetadata.CHAPTER_15_TALONS_ALIGHT;
			case CHAPTER_16: return ChapterMetadata.CHAPTER_16_NOBLE_LADY_OF_CAELIN;
			case CHAPTER_17: return ChapterMetadata.CHAPTER_17_WHEREABOUTS_UNKNOWN;
			case CHAPTER_17X: return ChapterMetadata.CHAPTER_17X_THE_PORT_OF_BADON;
			case CHAPTER_18: return ChapterMetadata.CHAPTER_18_PIRATE_SHIP;
			case CHAPTER_19: return ChapterMetadata.CHAPTER_19_THE_DREAD_ISLE;
			case CHAPTER_19X: return ChapterMetadata.CHAPTER_19X_IMPRISONER_OF_MAGIC;
			case CHAPTER_19XX: return ChapterMetadata.CHAPTER_19XX_A_GLIMPSE_IN_TIME;
			case CHAPTER_20: return ChapterMetadata.CHAPTER_20_DRAGONS_GATE;
			case CHAPTER_21: return ChapterMetadata.CHAPTER_21_NEW_RESOLVE;
			case CHAPTER_22: return ChapterMetadata.CHAPTER_22_KINSHIPS_BOND;
			case CHAPTER_23: return ChapterMetadata.CHAPTER_23_LIVING_LEGEND;
			case CHAPTER_23X: return ChapterMetadata.CHAPTER_23X_GENESIS;
			case CHAPTER_24_LLOYD: return ChapterMetadata.CHAPTER_24_FOUR_FANGED_OFFENSE_LLOYD;
			case CHAPTER_24_LINUS: return ChapterMetadata.CHAPTER_24_FOUR_FANGED_OFFENSE_LINUS;
			case CHAPTER_25: return ChapterMetadata.CHAPTER_25_CRAZED_BEAST;
			case CHAPTER_26: return ChapterMetadata.CHAPTER_26_UNFULFILLED_HEART;
			case CHAPTER_27_KENNETH: return ChapterMetadata.CHAPTER_27_PALE_FLOWER_OF_DARKNESS_KENNETH;
			case CHAPTER_27_JERME: return ChapterMetadata.CHAPTER_27_PALE_FLOWER_OF_DARKNESS_JERME;
			case CHAPTER_28: return ChapterMetadata.CHAPTER_28_BATTLE_BEFORE_DAWN;
			case CHAPTER_28X: return ChapterMetadata.CHAPTER_28X_NIGHT_OF_FAREWELLS;
			case CHAPTER_28_E: return ChapterMetadata.CHAPTER_28E_VALOROUS_ROLAND;
			case CHAPTER_29: return ChapterMetadata.CHAPTER_29_COG_OF_DESTINY;
			case CHAPTER_30_H: return ChapterMetadata.CHAPTER_30H_THE_BERSERKER;
			case CHAPTER_31: return ChapterMetadata.CHAPTER_31_SANDS_OF_TIME;
			case CHAPTER_31X: return ChapterMetadata.CHAPTER_31X_BATTLE_PREPARATIONS;
			case CHAPTER_32: return ChapterMetadata.CHAPTER_32_VICTORY_OR_DEATH;
			case CHAPTER_32X: return ChapterMetadata.CHAPTER_32X_THE_VALUE_OF_LIFE;
			case CHAPTER_FINAL: return ChapterMetadata.FINAL_CHAPTER_LIGHT_1;
			case CHAPTER_FINAL_2: return ChapterMetadata.FINAL_CHAPTER_LIGHT_2;
			default: return null;
			}
		}
		
		public FE7Data.CharacterClass[] blacklistedClasses() {
			switch(this) {
			case CHAPTER_5:
				return new FE7Data.CharacterClass[] {CharacterClass.ARCHER};
			default:
				return new FE7Data.CharacterClass[] {};
			}
		}
		
		public CharacterNudge[] nudgesRequired() {
			switch(this) {
			case CHAPTER_25:
				return new CharacterNudge[] {new CharacterNudge(Character.FARINA.ID, 20, 19, 18, 19) }; // Farina flies onscreen and stays on a mountain.
			
			default:
				return new CharacterNudge[] {};
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
		
		// TODO: Figure out how Lucius actually works, because it's not as easy as an ITGC.
		public Character[] targetedRewardRecipientsToTrack() {
			switch (this) {
			case CHAPTER_2: {
				return new Character[] {Character.LYN_TUTORIAL};
			}
			case CHAPTER_17: {
				return new Character[] {Character.LUCIUS};
			}
			default:
				break;
			}
			
			return new Character[] {};
		}
		
		public Character[] unarmedUnits() {
			switch (this) {
			case CHAPTER_17: {
				return new Character[] {Character.LUCIUS}; // Random soldier gives Lucius a weapon.
			}
			default:
				break;
			}
			
			return new Character[] {};
		}
		
		// These should always be true for every chapter.
		public static Map<Integer, Integer> universalWorldMapSpriteClassIDToCharacterIDMapping() {
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			map.put(CharacterClass.LORD_LYN.ID, Character.LYN.ID);
			map.put(CharacterClass.BLADE_LORD.ID, Character.LYN.ID);
			map.put(CharacterClass.LORD_ELIWOOD.ID, Character.ELIWOOD.ID);
			map.put(CharacterClass.LORD_KNIGHT.ID, Character.ELIWOOD.ID);
			map.put(CharacterClass.LORD_HECTOR.ID, Character.HECTOR.ID);
			map.put(CharacterClass.GREAT_LORD.ID, Character.HECTOR.ID);
			map.put(CharacterClass.BARD.ID, Character.NILS.ID);
			map.put(CharacterClass.DANCER.ID, Character.NINIAN.ID);
			
			map.put(CharacterClass.SOLDIER.ID, Character.NONE.ID); // Don't touch these.
			return map;
		}
		
		public Map<Integer, List<Integer>> worldMapSpriteClassIDToCharacterIDMapping() {
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			switch (this) {
			case CHAPTER_1:
				map.put(CharacterClass.CAVALIER.ID, new ArrayList<Integer>(Arrays.asList(Character.SAIN.ID, Character.KENT.ID)));
				break;
			case CHAPTER_3:
				map.put(CharacterClass.BRIGAND.ID, new ArrayList<Integer>(Arrays.asList(Character.MIGAL.ID)));
				map.put(CharacterClass.PEGASUS_KNIGHT.ID, new ArrayList<Integer>(Arrays.asList(Character.FLORINA.ID)));
				break;
			case CHAPTER_4:
				map.put(CharacterClass.BRIGAND.ID, new ArrayList<Integer>(Arrays.asList(Character.CARJIGA.ID)));
				break;
			case CHAPTER_5:
				map.put(CharacterClass.BRIGAND.ID, new ArrayList<Integer>(Arrays.asList(Character.BUG.ID)));
				break;
			case CHAPTER_6:
				map.put(CharacterClass.GENERAL.ID, new ArrayList<Integer>(Arrays.asList(Character.LUNDGREN.ID)));
				map.put(CharacterClass.KNIGHT.ID, new ArrayList<Integer>(Arrays.asList(Character.BOOL.ID)));
				break;
			case CHAPTER_7X:
				map.put(CharacterClass.VALKYRIE.ID, new ArrayList<Integer>(Arrays.asList(Character.URSULA.ID)));
				break;
			case CHAPTER_8:
				map.put(CharacterClass.KNIGHT.ID, new ArrayList<Integer>(Arrays.asList(Character.YOGI.ID)));
				break;
			case CHAPTER_10:
				map.put(CharacterClass.GENERAL.ID, new ArrayList<Integer>(Arrays.asList(Character.LUNDGREN.ID)));
				break;
			case CHAPTER_11_E:
				map.put(CharacterClass.PALADIN.ID, new ArrayList<Integer>(Arrays.asList(Character.NONE.ID)));
				break;
			case CHAPTER_15:
				map.put(CharacterClass.NOMAD.ID, new ArrayList<Integer>(Arrays.asList(Character.SEALEN.ID)));
				break;
			case CHAPTER_17:
				map.put(CharacterClass.GENERAL.ID, new ArrayList<Integer>(Arrays.asList(Character.BERNARD.ID)));
				break;
			case CHAPTER_19X:
				map.put(CharacterClass.SAGE.ID, new ArrayList<Integer>(Arrays.asList(Character.AION.ID)));
				break;
			case CHAPTER_28:
				map.put(CharacterClass.MAGE_F.ID, new ArrayList<Integer>(Arrays.asList(Character.NINO.ID)));
				map.put(CharacterClass.ASSASSIN.ID, new ArrayList<Integer>(Arrays.asList(Character.JAFFAR.ID)));
				break;
			case CHAPTER_29:
				map.put(CharacterClass.WYVERN_RIDER.ID, new ArrayList<Integer>(Arrays.asList(Character.NONE.ID)));
				break;
			default:
				break;
			}
			return map;
		}
		
		// TODO: Figure out a better way of doing this...
		public static ChapterPointer chapterForWorldMapEventOffset(long worldMapOffset) {
			if (worldMapOffset == 0xCE7920L) { return CHAPTER_1; }
			else if (worldMapOffset == 0xCE7AC0L) { return CHAPTER_2; }
			else if (worldMapOffset == 0xCE7BB4L) { return CHAPTER_3; }
			else if (worldMapOffset == 0xCE7E1CL) { return CHAPTER_4; }
			else if (worldMapOffset == 0xCE7F30L) { return CHAPTER_5; }
			else if (worldMapOffset == 0xCE8078L) { return CHAPTER_6; }
			else if (worldMapOffset == 0xCE821CL) { return CHAPTER_7; }
			else if (worldMapOffset == 0xCE833CL) { return CHAPTER_7X; }
			else if (worldMapOffset == 0xCE84C8L) { return CHAPTER_8; }
			else if (worldMapOffset == 0xCE8618L) { return CHAPTER_9; }
			else if (worldMapOffset == 0xCE8894L) { return CHAPTER_10; }
			else if (worldMapOffset == 0xCE89ECL) { return CHAPTER_11_E; }
			else if (worldMapOffset == 0xCECDD8L) { return CHAPTER_11_H; }
			else if (worldMapOffset == 0xCE8D50L) { return CHAPTER_12; }
			else if (worldMapOffset == 0xCE8FACL) { return CHAPTER_13; }
			else if (worldMapOffset == 0xCE9200L) { return CHAPTER_13X; }
			else if (worldMapOffset == 0xCE9408L) { return CHAPTER_14; }
			else if (worldMapOffset == 0xCECF0CL) { return CHAPTER_15; }
			else if (worldMapOffset == 0xCE9BF8L) { return CHAPTER_16; }
			else if (worldMapOffset == 0xCE9DD4L) { return CHAPTER_17; }
			else if (worldMapOffset == 0xCE9F88L) { return CHAPTER_17X; }
			else if (worldMapOffset == 0xCEA10CL) { return CHAPTER_18; }
			else if (worldMapOffset == 0xCEA754L) { return CHAPTER_19; }
			else if (worldMapOffset == 0xCEA8C8L) { return CHAPTER_19X; }
			else if (worldMapOffset == 0xCED038L) { return CHAPTER_19XX; }
			else if (worldMapOffset == 0xCEAA5CL) { return CHAPTER_20; }
			else if (worldMapOffset == 0xCEAC48L) { return CHAPTER_21; }
			else if (worldMapOffset == 0xCEAEA0L) { return CHAPTER_22; }
			else if (worldMapOffset == 0xCEB0E8L) { return CHAPTER_23; }
			else if (worldMapOffset == 0xCEB3ACL) { return CHAPTER_24_LINUS; }
			else if (worldMapOffset == 0xCEB67CL) { return CHAPTER_24_LLOYD; }
			else if (worldMapOffset == 0xCED188L) { return CHAPTER_25; }
			else if (worldMapOffset == 0xCEB94CL) { return CHAPTER_26; }
			else if (worldMapOffset == 0xCEBF5CL) { return CHAPTER_27_JERME; }
			else if (worldMapOffset == 0xCEBD20L) { return CHAPTER_27_KENNETH; }
			else if (worldMapOffset == 0xCEC198L) { return CHAPTER_28; }
			else if (worldMapOffset == 0xCEC96CL) { return CHAPTER_28_E; }
			else if (worldMapOffset == 0xCEC3F8L) { return CHAPTER_28X; }
			else if (worldMapOffset == 0xCEC5B4L) { return CHAPTER_29; }
			else if (worldMapOffset == 0xCED478L) { return CHAPTER_30_H; }
			else if (worldMapOffset == 0xCECA24L) { return CHAPTER_31; }
			else if (worldMapOffset == 0xCECBB0L) { return CHAPTER_32; }
			else if (worldMapOffset == 0xCED554L) { return CHAPTER_32X; }
			else {
				assert false: "Shouldn't be requesting an unknown world map event offset.";
				return null;
			}
		}
	}
	
	public enum PromotionItem implements GBAFEPromotionItem {
		// The indexes here are based off of a table that starts at 0x27428 (this may not be the actual start of whatever this table is).
		// Remember that the actual address of the class IDs starts at byte 4 after the jump.
		// The class IDs are 00 terminated.
		HERO_CREST(0x01), KNIGHT_CREST(0x02), ORION_BOLT(0x03), ELYSIAN_WHIP(0x04), GUIDING_RING(0x05), MASTER_SEAL(0x25), FALLEN_CONTRACT(0x29), 
		
		// These are special. I'm not sure what points to them, so they're direct.
		HECTOR_LYN_HEAVEN_SEAL(0x46), ELIWOOD_LYN_HEAVEN_SEAL(0x47),
		OCEAN_SEAL(0x53);
		
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
			return this != OCEAN_SEAL && this != HECTOR_LYN_HEAVEN_SEAL && this != ELIWOOD_LYN_HEAVEN_SEAL;
		}
		
		public String itemName() {
			return this.toString();
		}
		
		public int getItemID() {
			switch (this) {
			case HERO_CREST:
				return Item.HERO_CREST.ID;
			case KNIGHT_CREST:
				return Item.KNIGHT_CREST.ID;
			case ORION_BOLT:
				return Item.ORION_BOLT.ID;
			case ELYSIAN_WHIP:
				return Item.ELYSIAN_WHIP.ID;
			case GUIDING_RING:
				return Item.GUIDING_RING.ID;
			case MASTER_SEAL:
				return Item.EARTH_SEAL.ID;
			case FALLEN_CONTRACT:
				return Item.FELL_CONTRACT.ID;
			case HECTOR_LYN_HEAVEN_SEAL:
			case ELIWOOD_LYN_HEAVEN_SEAL:
				return Item.HEAVEN_SEAL.ID;
			case OCEAN_SEAL:
				return Item.OCEAN_SEAL.ID;
			default:
				return 0;
			}
		}
	}
	
	public enum Shops implements GBAFEShop {
		
		CHAPTER_3_ARMORY(0xCA0B98L, 0xCA6F3CL),
		CHAPTER_5_ARMORY(0xCA0F38L, 0xCA6F42L),
		CHAPTER_7_VENDOR(0xCA130CL, 0xCA6F4AL),
		CHAPTER_8_ARMORY(0xCA1504L, 0xCA6F52L),
		CHAPTER_10_ARMORY(0xCA187CL, 0xCA6F5CL),
		CHAPTER_10_VENDOR(0xCA1888L, 0xCA6F66L),
		CHAPTER_11_VENDOR(0xCA1958L, 0xCA6F72L),
		CHAPTER_12_VENDOR(0xCA1B4CL, 0xCA6F76L),
		CHAPTER_12_ARMORY(0xCA1B58L, 0xCA6F7AL),
		CHAPTER_13_ARMORY(0xCA1D24L, 0xCA6F84L),
		CHAPTER_13_VENDOR(0xCA1D30L, 0xCA6F8EL),
		CHAPTER_14_ARMORY(0xCA218CL, 0xCA6F94L),
		CHAPTER_14_VENDOR(0xCA2198L, 0xCA6F9EL),
		CHAPTER_16_ARMORY_1(0xCA25C0L, 0xCA6FA6L),
		CHAPTER_16_ARMORY_2(0xCA25CCL, 0xCA6FB4L),
		CHAPTER_16_VENDOR(0xCA25D8L, 0xCA6FC2L),
		CHAPTER_17X_VENDOR(0xCA2A24L, 0xCA6FCCL),
		CHAPTER_18_ARMORY(0xCA2C64L, 0xCA6FD8L),
		CHAPTER_18_VENDOR(0xCA2C70L, 0xCA6FEAL),
		CHAPTER_20_SECRET_1(0xCA344CL, 0xCA6FFCL),
		CHAPTER_20_SECRET_2(0xCA3458L, 0xCA700CL),
		CHAPTER_21_ARMORY_1(0xCA374CL, 0xCA7022L),
		CHAPTER_21_VENDOR_1(0xCA3764L, 0xCA703AL),
		CHAPTER_21_ARMORY_2(0xCA3740L, 0xCA7018L),
		CHAPTER_21_VENDOR_2(0xCA3758L, 0xCA7030L),
		CHAPTER_22_SECRET(0xCA3ABCL, 0xCA7048L),
		CHAPTER_24A_ARMORY_1(0xCA4214L, 0xCA7068L),
		CHAPTER_24A_VENDOR_1(0xCA4220L, 0xCA7076L),
		CHAPTER_24A_ARMORY_2(0xCA4208L, 0xCA7058L),
		CHAPTER_24A_VENDOR_2(0xCA422CL, 0xCA7082L),
		CHAPTER_24A_SECRET(0xCA4238L, 0xCA708CL),
		CHAPTER_24B_ARMORY_1(0xCA4518L, 0xCA709AL),
		CHAPTER_24B_VENDOR_1(0xCA4530L, 0xCA70B8L),
		CHAPTER_24B_ARMORY_2(0xCA4524L, 0xCA70AAL),
		CHAPTER_24B_VENDOR_2(0xCA453CL, 0xCA70C2L),
		CHAPTER_24B_SECRET(0xCA4548L, 0xCA70CEL),
		CHAPTER_25_ARMORY(0xCA471CL, 0xCA70DEL),
		CHAPTER_25_VENDOR(0xCA4728L, 0xCA70E8L),
		CHAPTER_26_ARMORY(0xCA4928L, 0xCA70F2L),
		CHAPTER_26_VENDOR(0xCA4934L, 0xCA7100L),
		CHAPTER_29_ARMORY(0xCA5A28L, 0xCA710CL),
		CHAPTER_29_VENDOR(0xCA5A34L, 0xCA711EL),
		CHAPTER_31_SECRET(0xCA5EA0L, 0xCA7130L),
		CHAPTER_31X_ARMORY_1(0xCA601CL, 0xCA7168L),
		CHAPTER_31X_VENDOR_1(0xCA6028L, 0xCA7176L),
		CHAPTER_31X_VENDOR_2(0xCA6034L, 0xCA7186L),
		CHAPTER_31X_ARMORY_2(0xCA5FF8L, 0xCA713CL),
		CHAPTER_31X_ARMORY_3(0xCA6004L, 0xCA714AL),
		CHAPTER_31X_ARMORY_4(0xCA6010L, 0xCA715AL),
		CHAPTER_32_SECRET(0xCA63ACL, 0xCA7198L)
		;
		
		long pointerOffset;
		long originalShopAddress;
		
		private Shops(final long pointerOffset, final long originalShopList) {
			this.pointerOffset = pointerOffset;
			this.originalShopAddress = originalShopList;
		}
		
		public static Set<Shops> allVendors() {
			return new HashSet<Shops>(Arrays.asList(CHAPTER_7_VENDOR, CHAPTER_10_VENDOR, CHAPTER_11_VENDOR, CHAPTER_12_VENDOR, CHAPTER_13_VENDOR, CHAPTER_14_VENDOR, CHAPTER_16_VENDOR, CHAPTER_17X_VENDOR, CHAPTER_18_VENDOR, CHAPTER_21_VENDOR_1,
					CHAPTER_21_VENDOR_2, CHAPTER_24A_VENDOR_1, CHAPTER_24A_VENDOR_2, CHAPTER_24B_VENDOR_1, CHAPTER_24B_VENDOR_2, CHAPTER_25_VENDOR, CHAPTER_26_VENDOR, CHAPTER_29_VENDOR, CHAPTER_31X_VENDOR_1, CHAPTER_31X_VENDOR_2));
		}
		
		public static Set<Shops> allArmories() {
			return new HashSet<Shops>(Arrays.asList(CHAPTER_3_ARMORY, CHAPTER_5_ARMORY, CHAPTER_8_ARMORY, CHAPTER_10_ARMORY, CHAPTER_12_ARMORY, CHAPTER_13_ARMORY, CHAPTER_14_ARMORY, CHAPTER_16_ARMORY_1, CHAPTER_16_ARMORY_2, CHAPTER_18_ARMORY,
					CHAPTER_21_ARMORY_1, CHAPTER_21_ARMORY_2, CHAPTER_24A_ARMORY_1, CHAPTER_24A_ARMORY_2, CHAPTER_24B_ARMORY_1, CHAPTER_24B_ARMORY_2, CHAPTER_25_ARMORY, CHAPTER_26_ARMORY, CHAPTER_29_ARMORY, CHAPTER_31X_ARMORY_1, CHAPTER_31X_ARMORY_2,
					CHAPTER_31X_ARMORY_3, CHAPTER_31X_ARMORY_4
					));
		}
		
		public static Set<Shops> allSecretShops() {
			return new HashSet<Shops>(Arrays.asList(CHAPTER_20_SECRET_1, CHAPTER_20_SECRET_2, CHAPTER_22_SECRET, CHAPTER_24A_SECRET, CHAPTER_24B_SECRET, CHAPTER_31_SECRET, CHAPTER_32_SECRET));
		}
		
		public Set<GBAFEShop> groupedShops() {
			switch (this) {
			case CHAPTER_16_ARMORY_1:
			case CHAPTER_16_ARMORY_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_16_ARMORY_1, CHAPTER_16_ARMORY_2));
			case CHAPTER_20_SECRET_1:
			case CHAPTER_20_SECRET_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_20_SECRET_1, CHAPTER_20_SECRET_2));
			case CHAPTER_21_ARMORY_1:
			case CHAPTER_21_ARMORY_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_21_ARMORY_1, CHAPTER_21_ARMORY_2));
			case CHAPTER_21_VENDOR_1:
			case CHAPTER_21_VENDOR_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_21_VENDOR_1, CHAPTER_21_VENDOR_2));
			case CHAPTER_24A_ARMORY_1:
			case CHAPTER_24A_ARMORY_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_24A_ARMORY_1, CHAPTER_24A_ARMORY_2));
			case CHAPTER_24A_VENDOR_1:
			case CHAPTER_24A_VENDOR_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_24A_VENDOR_1, CHAPTER_24A_VENDOR_2));
			case CHAPTER_24B_ARMORY_1:
			case CHAPTER_24B_ARMORY_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_24B_ARMORY_1, CHAPTER_24B_ARMORY_2));
			case CHAPTER_24B_VENDOR_1:
			case CHAPTER_24B_VENDOR_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_24B_VENDOR_1, CHAPTER_24B_VENDOR_2));
			case CHAPTER_31X_ARMORY_1:
			case CHAPTER_31X_ARMORY_2:
			case CHAPTER_31X_ARMORY_3:
			case CHAPTER_31X_ARMORY_4:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_31X_ARMORY_1, CHAPTER_31X_ARMORY_2, CHAPTER_31X_ARMORY_3, CHAPTER_31X_ARMORY_4));
			case CHAPTER_31X_VENDOR_1:
			case CHAPTER_31X_VENDOR_2:
				return new HashSet<GBAFEShop>(Arrays.asList(CHAPTER_31X_VENDOR_1, CHAPTER_31X_VENDOR_2));
			default:
				return new HashSet<GBAFEShop>(Arrays.asList(this));
			}
		}
		
		public GameStage getGameStage() {
			switch (this) {
			case CHAPTER_3_ARMORY:
			case CHAPTER_5_ARMORY:
				return GameStage.EARLY;
			case CHAPTER_7_VENDOR:
			case CHAPTER_8_ARMORY:
			case CHAPTER_10_ARMORY:
			case CHAPTER_10_VENDOR:
				return GameStage.MID;
			case CHAPTER_11_VENDOR:
			case CHAPTER_12_VENDOR:
			case CHAPTER_12_ARMORY:
			case CHAPTER_13_ARMORY:
			case CHAPTER_13_VENDOR:
			case CHAPTER_14_ARMORY:
			case CHAPTER_14_VENDOR:
				return GameStage.EARLY;
			case CHAPTER_16_ARMORY_1:
			case CHAPTER_16_ARMORY_2:
			case CHAPTER_16_VENDOR:
			case CHAPTER_17X_VENDOR:
			case CHAPTER_18_ARMORY:
			case CHAPTER_18_VENDOR:
			case CHAPTER_20_SECRET_1:
			case CHAPTER_20_SECRET_2:
			case CHAPTER_21_ARMORY_1:
			case CHAPTER_21_VENDOR_1:
			case CHAPTER_21_ARMORY_2:
			case CHAPTER_21_VENDOR_2:
			case CHAPTER_22_SECRET:
				return GameStage.MID;
			case CHAPTER_24A_ARMORY_1:
			case CHAPTER_24A_VENDOR_1:
			case CHAPTER_24A_ARMORY_2:
			case CHAPTER_24A_VENDOR_2:
			case CHAPTER_24A_SECRET:
			case CHAPTER_24B_ARMORY_1:
			case CHAPTER_24B_VENDOR_1:
			case CHAPTER_24B_ARMORY_2:
			case CHAPTER_24B_VENDOR_2:
			case CHAPTER_24B_SECRET:
			case CHAPTER_25_ARMORY:
			case CHAPTER_25_VENDOR:
			case CHAPTER_26_ARMORY:
			case CHAPTER_26_VENDOR:
			case CHAPTER_29_ARMORY:
			case CHAPTER_29_VENDOR:
			case CHAPTER_31_SECRET:
			case CHAPTER_31X_ARMORY_1:
			case CHAPTER_31X_VENDOR_1:
			case CHAPTER_31X_VENDOR_2:
			case CHAPTER_31X_ARMORY_2:
			case CHAPTER_31X_ARMORY_3:
			case CHAPTER_31X_ARMORY_4:
			case CHAPTER_32_SECRET:
				return GameStage.LATE;
			}
			
			return GameStage.LATE;
		}
		
		public long getPointerOffset() {
			return pointerOffset;
		}
		
		public long getOriginalShopAddress() {
			return originalShopAddress;
		}
	}
	
	public enum Palette {
		
		ARCHER_WIL(0x03, Character.WIL.ID, CharacterClass.ARCHER.ID, 0xFD90B4),
		ARCHER_REBECCA(0x02, Character.REBECCA.ID, CharacterClass.ARCHER_F.ID, 0xFD9050),
		
		ASSASSIN_MATTHEW(0x0F, Character.MATTHEW.ID, CharacterClass.ASSASSIN.ID, 0xFD95B8),
		ASSASSIN_JAFFAR(0x0C, Character.JAFFAR.ID, CharacterClass.ASSASSIN.ID, 0xFD94B4),
		ASSASSIN_LEGAULT(0x0E, Character.LEGAULT.ID, CharacterClass.ASSASSIN.ID, 0xFD955C),
		ASSASSIN_JERME(0x0D, Character.JERME.ID, CharacterClass.ASSASSIN.ID, 0xFD9508), // Needs hair override
		
		BRIGAND_BATTA(0x10, Character.BATTA.ID, CharacterClass.BRIGAND.ID, 0xFD9610),
		BRIGAND_BUG(0x11, Character.BUG.ID, CharacterClass.BRIGAND.ID, 0xFD9664),
		BRIGAND_CARJIGA(0x12, Character.CARJIGA.ID, CharacterClass.BRIGAND.ID, 0xFD96B4),
		BRIGAND_MIGAL(0x13, Character.MIGAL.ID, CharacterClass.BRIGAND.ID, 0xFD9704),
		BRIGAND_ZUGU(0x15, Character.ZUGU.ID, CharacterClass.BRIGAND.ID, 0xFD97A8),
		BRIGAND_GROZNYI(0x78, Character.GROZNYI.ID, CharacterClass.BRIGAND.ID, 0xFDBF64),
		
		BARD_NILS(0x20, Character.NILS.ID, CharacterClass.BARD.ID, 0xFD9B7C),
		
		BERSERKER_DART(0x21, Character.DART.ID, CharacterClass.BERSERKER.ID, 0xFD9BDC), // Needs hair override
		BERSERKER_GEORG(0x23, Character.GEORG.ID, CharacterClass.BERSERKER.ID, 0xFD9C84),
		BERSERKER_HAWKEYE(0x16, Character.HAWKEYE.ID, CharacterClass.BERSERKER.ID, 0xFD97F8),
		
		BISHOP_RENAULT(0x19, Character.RENAULT.ID, CharacterClass.BISHOP.ID, 0xFD9908),
		BISHOP_KENNETH(0x18, Character.KENNETH.ID, CharacterClass.BISHOP.ID, 0xFD98A8),
		BISHOP_SERRA(0x17, Character.SERRA.ID, CharacterClass.BISHOP_F.ID, 0xFD9848),
		BISHOP_LUCIUS(0x1A, Character.LUCIUS.ID, CharacterClass.BISHOP.ID, 0xFD9970),
		
		BLADE_LORD_LYN(0x1B, Character.LYN.ID, CharacterClass.BLADE_LORD.ID, 0xFD99DC),
		
		CAVALIER_KENT(0x62, Character.KENT.ID, CharacterClass.CAVALIER.ID, 0xFDB720),
		CAVALIER_LOWEN(0x63, Character.LOWEN.ID, CharacterClass.CAVALIER.ID, 0xFDB798),
		CAVALIER_SAIN(0x64, Character.SAIN.ID, CharacterClass.CAVALIER.ID, 0xFDB810),
		CAVALIER_ERIK(0x61, Character.ERIK.ID, CharacterClass.CAVALIER.ID, 0xFDB6A4),
		
		CLERIC_SERRA(0x53, Character.SERRA.ID, CharacterClass.CLERIC.ID, 0xFDB134),
		
		DANCER_NINIAN(0x24, Character.NINIAN.ID, CharacterClass.DANCER.ID, 0xFD9CD4),
		
		DRUID_CANAS(0x29, Character.CANAS.ID, CharacterClass.DRUID.ID, 0xFD9F08),
		DRUID_TEODOR(0x2A, Character.TEODOR.ID, CharacterClass.DRUID.ID, 0xFD9F5C),
		
		FALCON_KNIGHT_FARINA(0x2C, Character.FARINA.ID, CharacterClass.FALCON_KNIGHT.ID, 0xFDA00C),
		FALCON_KNIGHT_FIORA(0x2D, Character.FIORA.ID, CharacterClass.FALCON_KNIGHT.ID, 0xFDA07C),
		FALCON_KNIGHT_FLORINA(0x2E, Character.FLORINA.ID, CharacterClass.FALCON_KNIGHT.ID, 0xFDA0EC),
		
		FIGHTER_BARTRE(0x2F, Character.BARTRE.ID, CharacterClass.FIGHTER.ID, 0xFDA15C),
		FIGHTER_DORCAS(0x30, Character.DORCAS.ID, CharacterClass.FIGHTER.ID, 0xFDA1B4),
		FIGHTER_ZAGAN(0x14, Character.ZAGAN.ID, CharacterClass.FIGHTER.ID, 0xFD9754),
		
		GENERAL_OSWIN(0x33, Character.OSWIN.ID, CharacterClass.GENERAL.ID, 0xFDA2EC), // Needs hair override
		GENERAL_WALLACE(0x34, Character.WALLACE.ID, CharacterClass.GENERAL.ID, 0xFDA374), // RIP
		GENERAL_LUNDGREN(0x35, Character.LUNDGREN.ID, CharacterClass.GENERAL.ID, 0xFDA3FC), // Hair override
		GENERAL_DARIN(0x32, Character.DARIN.ID, CharacterClass.GENERAL.ID, 0xFDA27C), // Hair override
		GENERAL_BERNARD(0x31, Character.BERNARD.ID, CharacterClass.GENERAL.ID, 0xFDA20C), // Hair override
		
		GREAT_LORD_HECTOR(0x36, Character.HECTOR.ID, CharacterClass.GREAT_LORD.ID, 0xFDA46C),
		
		HERO_HARKEN(0x1C, Character.HARKEN.ID, CharacterClass.HERO.ID, 0xFD9A2C),
		HERO_KAIM(0x1D, Character.KAIM.ID, CharacterClass.HERO.ID, 0xFD9A84),
		HERO_RAVEN(0x1E, Character.RAVEN.ID, CharacterClass.HERO.ID, 0xFD9AD4),
		HERO_LINUS(0x1F, Character.LINUS_FFO.ID, CharacterClass.HERO.ID, 0xFD9B2C),
		
		// All knights need hair overrides.
		KNIGHT_OSWIN(0x08, Character.OSWIN.ID, CharacterClass.KNIGHT.ID, 0xFD92D8),
		KNIGHT_WALLACE(0x09, Character.WALLACE.ID, CharacterClass.KNIGHT.ID, 0xFD935C),
		KNIGHT_BOIES(0x04, Character.BOIES.ID, CharacterClass.KNIGHT.ID, 0xFD9114),
		KNIGHT_BOOL(0x05, Character.BOOL.ID, CharacterClass.KNIGHT.ID, 0xFD9180),
		KNIGHT_BAUKER(0x06, Character.BAUKER.ID, CharacterClass.KNIGHT.ID, 0xFD91EC),
		KNIGHT_WIRE(0x0A, Character.WIRE.ID, CharacterClass.KNIGHT.ID, 0xFD93DC),
		KNIGHT_YOGI(0x0B, Character.YOGI.ID, CharacterClass.KNIGHT.ID, 0xFD9448),
		
		LORD_LYN(0x01, Character.LYN.ID, CharacterClass.LORD_LYN.ID, 0xFD9000),
		LORD_ELIWOOD(0x2B, Character.ELIWOOD.ID, CharacterClass.LORD_ELIWOOD.ID, 0xFD9FAC),
		LORD_HECTOR(0x37, Character.HECTOR.ID, CharacterClass.LORD_HECTOR.ID, 0xFDA4C8),
		LORD_KNIGHT_ELIWOOD(0x38, Character.ELIWOOD.ID, CharacterClass.LORD_KNIGHT.ID, 0xFDA524),
		
		MAGE_ERK(0x3A, Character.ERK.ID, CharacterClass.MAGE.ID, 0xFDA5E8),
		MAGE_NINO(0x39, Character.NINO.ID, CharacterClass.MAGE_F.ID, 0xFDA57C),
		
		MERCENARY_RAVEN(0x3D, Character.RAVEN.ID, CharacterClass.MERCENARY.ID, 0xFDA714),
		MERCENARY_BEYARD(0x3B, Character.BEYARD.ID, CharacterClass.MERCENARY.ID, 0xFDA65C),
		MERCENARY_GLASS(0x3C, Character.GLASS.ID, CharacterClass.MERCENARY.ID, 0xFDA6B8),
		MERCENARY_PUZON(0x3E, Character.PUZON.ID, CharacterClass.MERCENARY.ID, 0xFDA780),
		
		MONK_LUCIUS(0x3F, Character.LUCIUS.ID, CharacterClass.MONK.ID, 0xFDA7DC),
		
		MYRMIDON_GUY(0x40, Character.GUY.ID, CharacterClass.MYRMIDON.ID, 0xFDA83C),
		
		NOMAD_RATH(0x41, Character.RATH.ID, CharacterClass.NOMAD.ID, 0xFDA89C),
		NOMAD_SEALEN(0x42, Character.SEALEN.ID, CharacterClass.NOMAD.ID, 0xFDA910),
		
		NOMAD_TROOPER_RATH(0x43, Character.RATH.ID, CharacterClass.NOMAD_TROOPER.ID, 0xFDA984),
		NOMAD_TROOPER_UHAI(0x44, Character.UHAI.ID, CharacterClass.NOMAD_TROOPER.ID, 0xFDAA14),
		
		PALADIN_KENT(0x49, Character.KENT.ID, CharacterClass.PALADIN.ID, 0xFDAC6C),
		PALADIN_LOWEN(0x4A, Character.LOWEN.ID, CharacterClass.PALADIN.ID, 0xFDACF8),
		PALADIN_MARCUS(0x4B, Character.MARCUS.ID, CharacterClass.PALADIN.ID, 0xFDAD84), // Hair override
		PALADIN_SAIN(0x4E, Character.SAIN.ID, CharacterClass.PALADIN.ID, 0xFDAF00),
		PALADIN_EAGLER(0x07, Character.EAGLER.ID, CharacterClass.PALADIN.ID, 0xFD9258), // Hair override
		PALADIN_CAMERON(0x46, Character.CAMERON.ID, CharacterClass.PALADIN.ID, 0xFDAB04),
		PALADIN_DAMIAN(0x47, Character.DAMIAN.ID, CharacterClass.PALADIN.ID, 0xFDAB7C),
		PALADIN_EUBANS(0x48, Character.EUBANS.ID, CharacterClass.PALADIN.ID, 0xFDABF4), // Hair override
		PALADIN_MAXIME(0x4C, Character.MAXIME.ID, CharacterClass.PALADIN.ID, 0xFDAE10),
		PALADIN_PASCAL(0x4D, Character.PASCAL.ID, CharacterClass.PALADIN.ID, 0xFDAE88), // Hair override
		
		PALADIN_ISADORA(0x45, Character.ISADORA.ID, CharacterClass.PALADIN_F.ID, 0xFDAA8C),
		
		PEGASUS_KNIGHT_FARINA(0x4F, Character.FARINA.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0xFDAF8C),
		PEGASUS_KNIGHT_FIORA(0x50, Character.FIORA.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0xFDAFF8),
		PEGASUS_KNIGHT_FLORINA(0x51, Character.FLORINA.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0xFDB064),
		
		PIRATE_DART(0x52, Character.DART.ID, CharacterClass.PIRATE.ID, 0xFDB0D4), // Needs hair override
		
		SAGE_AION(0x57, Character.AION.ID, CharacterClass.SAGE.ID, 0xFDB2D4),
		SAGE_PENT(0x59, Character.PENT.ID, CharacterClass.SAGE.ID, 0xFDB3B8),
		SAGE_ERK(0x58, Character.ERK.ID, CharacterClass.SAGE.ID, 0xFDB340),
		SAGE_NINO(0x55, Character.NINO.ID, CharacterClass.SAGE_F.ID, 0xFDB1FC),
		SAGE_SONIA(0x56, Character.SONIA.ID, CharacterClass.SAGE_F.ID, 0xFDB26C),
		UBER_SAGE_SONIA(0x56, Character.SONIA.ID, CharacterClass.UBER_SAGE.ID, 0xFDB26C), // Not sure when this shows up, actually, but it's there.
		UBER_SAGE_LIMSTELLA(0x54, Character.LIMSTELLA.ID, CharacterClass.UBER_SAGE.ID, 0xFDB194),
		
		SHAMAN_CANAS(0x5A, Character.CANAS.ID, CharacterClass.SHAMAN.ID, 0xFDB430),
		SHAMAN_HEINTZ(0x5B, Character.HEINTZ.ID, CharacterClass.SHAMAN.ID, 0xFDB484),
		SHAMAN_ZOLDAM(0x5C, Character.ZOLDAM.ID, CharacterClass.SHAMAN.ID, 0xFDB4D4),
		
		SNIPER_WIL(0x60, Character.WIL.ID, CharacterClass.SNIPER.ID, 0xFDB644),
		SNIPER_DENNING(0x5F, Character.DENNING.ID, CharacterClass.SNIPER.ID, 0xFDB5EC),
		SNIPER_LOUISE(0x5D, Character.LOUISE.ID, CharacterClass.SNIPER_F.ID, 0xFDB524),
		SNIPER_REBECCA(0x5E, Character.REBECCA.ID, CharacterClass.SNIPER_F.ID, 0xFDB588),
		
		SWORDMASTER_KARLA(0x66, Character.KARLA.ID, CharacterClass.SWORDMASTER_F.ID, 0xFDB8E4),
		SWORDMASTER_KAREL(0x68, Character.KAREL.ID, CharacterClass.SWORDMASTER.ID, 0xFDB9A8),
		SWORDMASTER_GUY(0x67, Character.GUY.ID, CharacterClass.SWORDMASTER.ID, 0xFDB944),
		SWORDMASTER_LLOYD(0x69, Character.LLOYD_FFO.ID, CharacterClass.SWORDMASTER.ID, 0xFDBA08),
		
		THIEF_MATTHEW(0x6C, Character.MATTHEW.ID, CharacterClass.THIEF.ID, 0xFDBB0C),
		THIEF_LEGAULT(0x6B, Character.LEGAULT.ID, CharacterClass.THIEF.ID, 0xFDBAA8),
		
		TROUBADOUR_PRISCILLA(0x6D, Character.PRISCILLA.ID, CharacterClass.TROUBADOUR.ID, 0xFDBB74),
		
		VALKYRIE_PRISCILLA(0x6E, Character.PRISCILLA.ID, CharacterClass.VALKYRIE.ID, 0xFDBBD0),
		VALKYRIE_URSULA(0x6F, Character.URSULA.ID, CharacterClass.VALKYRIE.ID, 0xFDBC3C),
		
		WARRIOR_BARTRE(0x70, Character.BARTRE.ID, CharacterClass.WARRIOR.ID, 0xFDBCA0),
		WARRIOR_DORCAS(0x72, Character.DORCAS.ID, CharacterClass.WARRIOR.ID, 0xFDBD58),
		WARRIOR_GEITZ(0x73, Character.GEITZ.ID, CharacterClass.WARRIOR.ID, 0xFDBDB4), // Hair Override
		WARRIOR_BRENDAN(0x71, Character.BRENDAN.ID, CharacterClass.WARRIOR.ID, 0xFDBCFC), // Hair override
		WARRIOR_JASMINE(0x74, Character.JASMINE.ID, CharacterClass.WARRIOR.ID, 0xFDBE14),
		WARRIOR_OLEG(0x75, Character.OLEG.ID, CharacterClass.WARRIOR.ID, 0xFDBE70), // Hair override
		WARRIOR_PAUL(0x76, Character.PAUL.ID, CharacterClass.WARRIOR.ID, 0xFDBECC),
		
		WYVERN_RIDER_HEATH(0x25, Character.HEATH.ID, CharacterClass.WYVERN_RIDER.ID, 0xFD9D44), // May need hair override.
		
		WYVERN_LORD_HEATH(0x26, Character.HEATH.ID, CharacterClass.WYVERN_LORD.ID, 0xFD9DB4),
		WYVERN_LORD_VAIDA(0x27, Character.VAIDA.ID, CharacterClass.WYVERN_LORD_F.ID, 0xFD9E34), // Hair override
		
		GENERIC_ELIWOOD_LORD(0x0, Character.NONE.ID, CharacterClass.LORD_ELIWOOD.ID, 0xE07958),
		GENERIC_LORD_KNIGHT(0x0, Character.NONE.ID, CharacterClass.LORD_KNIGHT.ID, 0xE10654),
		GENERIC_HECTOR_LORD(0x0, Character.NONE.ID, CharacterClass.LORD_HECTOR.ID, 0xE26D60),
		GENERIC_GREAT_LORD(0x0, Character.NONE.ID, CharacterClass.GREAT_LORD.ID, 0xE2E570),
		GENERIC_LYN_LORD(0x0, Character.NONE.ID, CharacterClass.LORD_LYN.ID, 0xE3F400),
		GENERIC_BLADE_LORD(0x0, Character.NONE.ID, CharacterClass.BLADE_LORD.ID, 0xE45118),
		GENERIC_BRIGAND(0x0, Character.NONE.ID, CharacterClass.BRIGAND.ID, 0xE520B4),
		GENERIC_PIRATE(0x0, Character.NONE.ID, CharacterClass.PIRATE.ID, 0xE55B68),
		GENERIC_FIGHTER(0x0, Character.NONE.ID, CharacterClass.FIGHTER.ID, 0xE61810),
		GENERIC_WARRIOR(0x0, Character.NONE.ID, CharacterClass.WARRIOR.ID, 0xE6B45C),
		GENERIC_ARCHER(0x0, Character.NONE.ID, CharacterClass.ARCHER.ID, 0xE74CFC),
		GENERIC_ARCHER_F(0x0, Character.NONE.ID, CharacterClass.ARCHER_F.ID, 0xE778EC),
		GENERIC_SNIPER(0x0, Character.NONE.ID, CharacterClass.SNIPER.ID, 0xE7A5D0),
		GENERIC_SNIPER_F(0x0, Character.NONE.ID, CharacterClass.SNIPER_F.ID, 0xE7D0FC),
		GENERIC_MERCENARY(0x0, Character.NONE.ID, CharacterClass.MERCENARY.ID, 0xE819FC),
		GENERIC_HERO(0x0, Character.NONE.ID, CharacterClass.HERO.ID, 0xE880CC),
		GENERIC_MYRMIDON(0x0, Character.NONE.ID, CharacterClass.MYRMIDON.ID, 0xE93AAC),
		GENERIC_SWORDMASTER(0x0, Character.NONE.ID, CharacterClass.SWORDMASTER.ID, 0xE99428),
		GENERIC_SWORDMASTER_F(0x0, Character.NONE.ID, CharacterClass.SWORDMASTER_F.ID, 0xEA44D8),
		GENERIC_CAVALIER(0x0, Character.NONE.ID, CharacterClass.CAVALIER.ID, 0xEAA4A8),
		GENERIC_PALADIN(0x0, Character.NONE.ID, CharacterClass.PALADIN.ID, 0xEC7E30),
		GENERIC_PALADIN_F(0x0, Character.NONE.ID, CharacterClass.PALADIN_F.ID, 0xEE13A0),
		GENERIC_SOLDIER(0x0, Character.NONE.ID, CharacterClass.SOLDIER.ID, 0xEE67A8),
		GENERIC_KNIGHT(0x0, Character.NONE.ID, CharacterClass.KNIGHT.ID, 0xEEAD0C),
		GENERIC_GENERAL(0x0, Character.NONE.ID, CharacterClass.GENERAL.ID, 0xEF3B00),
		GENERIC_MAGE(0x0, Character.NONE.ID, CharacterClass.MAGE.ID, 0xEFA0F4),
		GENERIC_MAGE_F(0x0, Character.NONE.ID, CharacterClass.MAGE_F.ID, 0xEFCD98),
		GENERIC_SAGE(0x0, Character.NONE.ID, CharacterClass.SAGE.ID, 0xF00AFC),
		GENERIC_SAGE_F(0x0, Character.NONE.ID, CharacterClass.SAGE_F.ID, 0xF05DD0),
		GENERIC_CLERIC(0x0, Character.NONE.ID, CharacterClass.CLERIC.ID, 0xF0A3D0),
		GENERIC_MONK(0x0, Character.NONE.ID, CharacterClass.MONK.ID, 0xF0D108),
		GENERIC_BISHOP(0x0, Character.NONE.ID, CharacterClass.BISHOP.ID, 0xF10980),
		GENERIC_BISHOP_F(0x0, Character.NONE.ID, CharacterClass.BISHOP_F.ID, 0xF137C4),
		GENERIC_SHAMAN(0x0, Character.NONE.ID, CharacterClass.SHAMAN.ID, 0xF1773C),
		GENERIC_DRUID(0x0, Character.NONE.ID, CharacterClass.DRUID.ID, 0xF1BE98),
		GENERIC_TROUBADOUR(0x0, Character.NONE.ID, CharacterClass.TROUBADOUR.ID, 0xF24490),
		GENERIC_VALKYRIE(0x0, Character.NONE.ID, CharacterClass.VALKYRIE.ID, 0xF2817C),
		GENERIC_NOMAD(0x0, Character.NONE.ID, CharacterClass.NOMAD.ID, 0xF2FF14),
		GENERIC_NOMAD_TROOPER(0x0, Character.NONE.ID, CharacterClass.NOMAD_TROOPER.ID, 0xF39590),
		GENERIC_THIEF(0x0, Character.NONE.ID, CharacterClass.THIEF.ID, 0xF3E024),
		GENERIC_ASSASSIN(0x0, Character.NONE.ID, CharacterClass.ASSASSIN.ID, 0xF43390),
		GENERIC_PEGASUS_KNIGHT(0x0, Character.NONE.ID, CharacterClass.PEGASUS_KNIGHT.ID, 0xF4BC70),
		GENERIC_FALCON_KNIGHT(0x0, Character.NONE.ID, CharacterClass.FALCON_KNIGHT.ID, 0xF5A7E0),
		GENERIC_WYVERN_RIDER(0x0, Character.NONE.ID, CharacterClass.WYVERN_RIDER.ID, 0xF64434),
		GENERIC_WYVERN_LORD(0x0, Character.NONE.ID, CharacterClass.WYVERN_LORD.ID, 0xF718C8),
		GENERIC_DANCER(0x0, Character.NONE.ID, CharacterClass.DANCER.ID, 0xF816D4),
		GENERIC_BARD(0x0, Character.NONE.ID, CharacterClass.BARD.ID, 0xF83F80),
		GENERIC_BERSERKER(0x0, Character.NONE.ID, CharacterClass.BERSERKER.ID, 0xFA106C)
		
		;
		
		int characterID;
		int classID;
		
		int paletteID;
		
		PaletteInfo info;
		
		static Map<Integer, Map<Integer, PaletteInfo>> classByCharacter = new HashMap<Integer, Map<Integer, PaletteInfo>>();
		static Map<Integer, Map<Integer, PaletteInfo>> charactersByClass = new HashMap<Integer, Map<Integer, PaletteInfo>>();
		static Map<Integer, PaletteInfo> defaultPaletteForClass = new HashMap<Integer, PaletteInfo>();
		static Map<Integer, Palette> palettesByID = new HashMap<Integer, Palette>();
		
		static Map<Integer, PaletteInfo> spritePalettesByClass = new HashMap<Integer, PaletteInfo>();
		
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
			
			spritePalettesByClass.put(CharacterClass.LORD_ELIWOOD.ID, GENERIC_ELIWOOD_LORD.info);
			spritePalettesByClass.put(CharacterClass.LORD_KNIGHT.ID, GENERIC_LORD_KNIGHT.info);
			spritePalettesByClass.put(CharacterClass.LORD_HECTOR.ID, GENERIC_HECTOR_LORD.info);
			spritePalettesByClass.put(CharacterClass.GREAT_LORD.ID, GENERIC_GREAT_LORD.info);
			spritePalettesByClass.put(CharacterClass.LORD_LYN.ID, GENERIC_LYN_LORD.info);
			spritePalettesByClass.put(CharacterClass.BLADE_LORD.ID, GENERIC_BLADE_LORD.info);
			spritePalettesByClass.put(CharacterClass.BRIGAND.ID, GENERIC_BRIGAND.info);
			spritePalettesByClass.put(CharacterClass.CORSAIR.ID, GENERIC_PIRATE.info); // Corsair doesn't have one, so we're just going to use pirate.
			spritePalettesByClass.put(CharacterClass.PIRATE.ID, GENERIC_PIRATE.info);
			spritePalettesByClass.put(CharacterClass.FIGHTER.ID, GENERIC_FIGHTER.info);
			spritePalettesByClass.put(CharacterClass.WARRIOR.ID, GENERIC_WARRIOR.info);
			spritePalettesByClass.put(CharacterClass.ARCHER.ID, GENERIC_ARCHER.info);
			spritePalettesByClass.put(CharacterClass.ARCHER_F.ID, GENERIC_ARCHER_F.info);
			spritePalettesByClass.put(CharacterClass.SNIPER.ID, GENERIC_SNIPER.info);
			spritePalettesByClass.put(CharacterClass.SNIPER_F.ID, GENERIC_SNIPER_F.info);
			spritePalettesByClass.put(CharacterClass.MERCENARY.ID, GENERIC_MERCENARY.info);
			spritePalettesByClass.put(CharacterClass.HERO.ID, GENERIC_HERO.info);
			spritePalettesByClass.put(CharacterClass.MYRMIDON.ID, GENERIC_MYRMIDON.info);
			spritePalettesByClass.put(CharacterClass.SWORDMASTER.ID, GENERIC_SWORDMASTER.info);
			spritePalettesByClass.put(CharacterClass.CAVALIER.ID, GENERIC_CAVALIER.info);
			spritePalettesByClass.put(CharacterClass.PALADIN.ID, GENERIC_PALADIN.info);
			spritePalettesByClass.put(CharacterClass.PALADIN_F.ID, GENERIC_PALADIN_F.info);
			spritePalettesByClass.put(CharacterClass.SOLDIER.ID, GENERIC_SOLDIER.info);
			spritePalettesByClass.put(CharacterClass.KNIGHT.ID, GENERIC_KNIGHT.info);
			spritePalettesByClass.put(CharacterClass.GENERAL.ID, GENERIC_GENERAL.info);
			spritePalettesByClass.put(CharacterClass.MAGE.ID, GENERIC_MAGE.info);
			spritePalettesByClass.put(CharacterClass.MAGE_F.ID, GENERIC_MAGE_F.info);
			spritePalettesByClass.put(CharacterClass.SAGE.ID, GENERIC_SAGE.info);
			spritePalettesByClass.put(CharacterClass.SAGE_F.ID, GENERIC_SAGE_F.info);
			spritePalettesByClass.put(CharacterClass.CLERIC.ID, GENERIC_CLERIC.info);
			spritePalettesByClass.put(CharacterClass.MONK.ID, GENERIC_MONK.info);
			spritePalettesByClass.put(CharacterClass.BISHOP.ID, GENERIC_BISHOP.info);
			spritePalettesByClass.put(CharacterClass.BISHOP_F.ID, GENERIC_BISHOP_F.info);
			spritePalettesByClass.put(CharacterClass.SHAMAN.ID, GENERIC_SHAMAN.info);
			spritePalettesByClass.put(CharacterClass.DRUID.ID, GENERIC_DRUID.info);
			spritePalettesByClass.put(CharacterClass.TROUBADOUR.ID, GENERIC_TROUBADOUR.info);
			spritePalettesByClass.put(CharacterClass.VALKYRIE.ID, GENERIC_VALKYRIE.info);
			spritePalettesByClass.put(CharacterClass.NOMAD.ID, GENERIC_NOMAD.info);
			spritePalettesByClass.put(CharacterClass.NOMAD_TROOPER.ID, GENERIC_NOMAD_TROOPER.info);
			spritePalettesByClass.put(CharacterClass.THIEF.ID, GENERIC_THIEF.info);
			spritePalettesByClass.put(CharacterClass.ASSASSIN.ID, GENERIC_ASSASSIN.info);
			spritePalettesByClass.put(CharacterClass.PEGASUS_KNIGHT.ID, GENERIC_PEGASUS_KNIGHT.info);
			spritePalettesByClass.put(CharacterClass.FALCON_KNIGHT.ID, GENERIC_FALCON_KNIGHT.info);
			spritePalettesByClass.put(CharacterClass.WYVERN_RIDER.ID, GENERIC_WYVERN_RIDER.info);
			spritePalettesByClass.put(CharacterClass.WYVERN_LORD.ID, GENERIC_WYVERN_LORD.info);
			spritePalettesByClass.put(CharacterClass.DANCER.ID, GENERIC_DANCER.info);
			spritePalettesByClass.put(CharacterClass.BARD.ID, GENERIC_BARD.info);
			spritePalettesByClass.put(CharacterClass.BERSERKER.ID, GENERIC_BERSERKER.info);
			
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
			defaultPaletteForClass.put(CharacterClass.BISHOP_F.ID, BISHOP_SERRA.info);
			defaultPaletteForClass.put(CharacterClass.CAVALIER.ID, CAVALIER_SAIN.info);
			defaultPaletteForClass.put(CharacterClass.CLERIC.ID, CLERIC_SERRA.info);
			defaultPaletteForClass.put(CharacterClass.MONK.ID, MONK_LUCIUS.info);
			defaultPaletteForClass.put(CharacterClass.MYRMIDON.ID, MYRMIDON_GUY.info);
			defaultPaletteForClass.put(CharacterClass.DRUID.ID, DRUID_CANAS.info);
			defaultPaletteForClass.put(CharacterClass.FALCON_KNIGHT.ID, FALCON_KNIGHT_FLORINA.info);
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
			defaultPaletteForClass.put(CharacterClass.NOMAD_TROOPER.ID, NOMAD_TROOPER_RATH.info);
			defaultPaletteForClass.put(CharacterClass.PALADIN.ID, PALADIN_MARCUS.info);
			defaultPaletteForClass.put(CharacterClass.PALADIN_F.ID, PALADIN_ISADORA.info);
			defaultPaletteForClass.put(CharacterClass.PEGASUS_KNIGHT.ID, PEGASUS_KNIGHT_FLORINA.info);
			defaultPaletteForClass.put(CharacterClass.PIRATE.ID, PIRATE_DART.info);
			defaultPaletteForClass.put(CharacterClass.CORSAIR.ID, PIRATE_DART.info);
			defaultPaletteForClass.put(CharacterClass.SHAMAN.ID, SHAMAN_CANAS.info);
			defaultPaletteForClass.put(CharacterClass.THIEF.ID, THIEF_MATTHEW.info);
			defaultPaletteForClass.put(CharacterClass.TROUBADOUR.ID, TROUBADOUR_PRISCILLA.info);
			defaultPaletteForClass.put(CharacterClass.VALKYRIE.ID, VALKYRIE_PRISCILLA.info);
			defaultPaletteForClass.put(CharacterClass.WARRIOR.ID, WARRIOR_DORCAS.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_RIDER.ID, WYVERN_RIDER_HEATH.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_LORD.ID, WYVERN_LORD_HEATH.info);
			defaultPaletteForClass.put(CharacterClass.WYVERN_LORD_F.ID, WYVERN_LORD_VAIDA.info);
		}
		
		private Palette(int paletteID, int charID, int classID, long offset) {
			this.characterID = charID;
			this.classID = classID;
			this.paletteID = paletteID;
			CharacterClass charClass = CharacterClass.valueOf(classID);
			if (charClass != null) {
				switch (charClass) {
				case SOLDIER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {11, 12, 13, 14}, new int[] {}, new int[] {8, 9, 10});
					break;
				case BLADE_LORD:
				case LORD_LYN:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {12, 13, 14}, new int[] {});
					break;
				case DANCER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {12, 13, 14}, new int[] {9, 10});
					break;
				case ARCHER:
				case ARCHER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {11, 12, 13, 14}, new int[] {});
					break;
				case SNIPER:
				case SNIPER_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {11, 12, 13, 14}, new int[] {}, new int[] {8, 9, 10});
					break;
				case ASSASSIN:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {12, 13, 14}, new int[] {5, 10});
					break;
				case BRIGAND:
				case FIGHTER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {12, 13, 14}, new int[] {}, new int[] {8, 9, 10});
					break;
				case BARD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {11, 12, 13, 14}, new int[] {8, 9, 10, 4});
					break;
				case BERSERKER:
					if (charID == Character.HAWKEYE.ID) {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {4, 5, 6}, new int[] {12, 13, 14}, new int[] {});
					} else {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {8, 9, 10}, new int[] {12, 13, 14}, new int[] {});
					}
					break;
				case BISHOP:
					if (charID == Character.LUCIUS.ID) {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {7, 5, 6}, new int[] {8, 13, 14}, new int[] {11, 12});
					} else {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6}, new int[] {8, 13, 14}, new int[] {11, 12});
					}
					break;
				case BISHOP_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6}, new int[] {11, 12, 13, 14}, new int[] {});
					break;
				case CAVALIER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {8, 9, 10, 11}, new int[] {3, 5});
					break;
				case CLERIC:
				case MONK: // May need to split out Lucius as a special case. This is assuming a Lucius sprite, which is unique from other monks.
				case MYRMIDON: 
				case SWORDMASTER:
				case SWORDMASTER_F:
					if (charID == Character.LLOYD_FFO.ID) {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {12, 13, 14}, new int[] {});
					} else {
						this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {11, 12, 13, 14}, new int[] {});
					}
					break;
				case FALCON_KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {7, 6}, new int[] {11, 12, 9, 13, 10, 14}, new int[] {});
					break;
				case GENERAL:
					// this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {12, 13, 14}, new int[] {});
				case GENERAL_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {11, 12, 13, 14}, new int[] {4, 5}, new int[] {8, 9, 10});
					break;
				case HERO:
				case LORD_HECTOR:
				case GREAT_LORD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {11, 12, 13}, new int[] {}, new int[] {8, 9, 10});
					break;
				case KNIGHT:
//					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {4, 2, 5, 3, 1}, new int[] {});
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {6, 4, 2, 1}, new int[] {}, new int[] {9, 5, 3});
					break;
				case LORD_ELIWOOD:
				case LORD_KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 5, 7}, new int[] {11, 12, 13, 14}, new int[] {});
					break;
				case MAGE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {12, 13, 14}, new int[] {8, 9, 10});
					break;
				case MAGE_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {12, 13, 14}, new int[] {8, 9, 10});
					break;
				case SAGE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {11, 12, 13, 14}, new int[] {8, 9, 10});
					break;
				case SAGE_F:
				case UBER_SAGE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {12, 13, 14}, new int[] {11, 8, 9, 10});
					break;
				case MERCENARY:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7, 12}, new int[] {9, 3, 14}, new int[] {});
					break;
				case NOMAD:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7, 14}, new int[] {3, 4, 5}, new int[] {});
					break;
				case NOMAD_TROOPER:
//					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {9, 10, 11}, new int[] {});
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {8, 2, 9, 5, 10, 11}, new int[] {});
					break;
				case PALADIN:
					//this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {8, 9, 10, 11}, new int[] {}, new int[] {6, 7});
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {8, 9, 10, 11}, new int[] {6, 7}, new int[] {12, 13, 14}); // Secondary is shield (which is blended with white). Tertiary is horse body.
					break;
				case PALADIN_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {8, 9, 10, 11}, new int[] {}); // Hair matches shield in the female.
					break;
				case PEGASUS_KNIGHT:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {7, 6}, new int[] {9, 10}, new int[] {}, new int[] {});
					break;
				case CORSAIR:
				case PIRATE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {11, 12, 13, 14}, new int[] {}); // Outfit/Bandana is the only color.
					break;
				case SHAMAN:
				case DRUID:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {8, 9, 10}, new int[] {11, 12, 13, 14}, new int[] {}); // Not really hair, but it matches up in the only one that matters (Canas)
					break;
				case THIEF:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {5, 6, 7}, new int[] {12, 11, 4}, new int[] {13, 14});
					break;
				case TROUBADOUR:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {8, 5, 11}, new int[] {9, 10});
					break;
				case VALKYRIE:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {6, 7}, new int[] {8, 9, 10, 11}, new int[] {12, 13, 14});
					break;
				case WARRIOR:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {12, 13, 14}, new int[] {7, 11}); // No Hair. Primary is pants/helmet color. Secondary is breastplate.
					break;
				case WYVERN_RIDER:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {3, 2}, new int[] {10, 11, 12, 13}, new int[] {9});
					break;
				case WYVERN_LORD:
				case WYVERN_LORD_F:
					this.info = new PaletteInfo(classID, charID, offset, new int[] {}, new int[] {10, 11, 12, 13}, new int[] {7, 9, 8}, new int[] {}); // Primary is Wyvern body, secondary is armor/belly/wing
					break;
				default:
					assert false: "Unable to create palette info.";
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
			List<PaletteInfo> list = new ArrayList<PaletteInfo>(classByCharacter.get(canonicalID).values());
			return list.toArray(new PaletteInfo[list.size()]);
		}
		
		public static int maxUsedPaletteIndex() {
			return 0x78; // Groznyi
		}
		
		public static int maxPaletteIndex() {
			return 0xF0; // There's space for this many, but they're all 0s. Seems like they could be used. It actually goes to FF, but we'll leave some space.
		}
		
		public static PaletteColor[] supplementaryHairColorForCharacter(int characterID) {
			Character character = Character.valueOf(Character.canonicalIDForCharacterID(characterID));
			switch (character) {
			case MARCUS:
				return new PaletteColor[] {new PaletteColor(208, 144, 216), new PaletteColor(168, 112, 168), new PaletteColor(112, 80, 120), new PaletteColor(88, 64, 96)};
			case OSWIN:
				return new PaletteColor[] {new PaletteColor(152, 144, 8), new PaletteColor(120, 112, 16), new PaletteColor(104, 80, 16)};
			case HEATH:
				return new PaletteColor[] {new PaletteColor(56, 184, 72), new PaletteColor(32, 144, 48)};
			case VAIDA:
				return new PaletteColor[] {new PaletteColor(248, 248, 112), new PaletteColor(216, 208, 64), new PaletteColor(176, 152, 64)};
			case BOOL:
				return new PaletteColor[] {new PaletteColor(181, 132, 57), new PaletteColor(140, 99, 49), new PaletteColor(99, 82, 57)};
			case YOGI:
				return new PaletteColor[] {new PaletteColor(181, 115, 115), new PaletteColor(140, 99, 99), new PaletteColor(115, 90, 99)};
			case EAGLER:
				return new PaletteColor[] {new PaletteColor(123, 189, 123), new PaletteColor(115, 148, 107), new PaletteColor(90, 107, 82)};
			case LUNDGREN:
				return new PaletteColor[] {new PaletteColor(222, 239, 239), new PaletteColor(181, 206, 222), new PaletteColor(156, 181, 222)};
			case BOIES:
				return new PaletteColor[] {new PaletteColor(49, 181, 74), new PaletteColor(16, 140, 41), new PaletteColor(16, 99, 24)};
			case BAUKER:
				return new PaletteColor[] {new PaletteColor(198, 107, 90), new PaletteColor(156, 82, 74), new PaletteColor(115, 74, 66)};
			case BERNARD:
				return new PaletteColor[] {new PaletteColor(214, 66, 57), new PaletteColor(181, 57, 49), new PaletteColor(132, 74, 82)};
			case DARIN:
				return new PaletteColor[] {new PaletteColor(189, 74, 214), new PaletteColor(156, 41, 181), new PaletteColor(123, 49, 140)};
			case EUBANS:
				return new PaletteColor[] {new PaletteColor(156, 189, 206), new PaletteColor(123, 148, 165), new PaletteColor(99, 107, 115)};
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
		
		public static PaletteInfo spritePaletteForClass(int classID) {
			return spritePalettesByClass.get(classID);
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
	
	public int appearanceChapter(int characterID) {
		Character character = Character.valueOf(characterID);
		if (character == null) { return 0; }
		switch (character) {
		case LYN: case BATTA: return 1; // Prologue
		case KENT: case SAIN: case ZUGU: return 2;
		case GLASS: return 3;
		case FLORINA: case WIL: case MIGAL: return 4;
		case DORCAS: case CARJIGA: return 5;
		case SERRA: case ERK: case BUG: return 6;
		case RATH: case MATTHEW: case BOOL: return 7;
		case NILS: case LUCIUS: case HEINTZ: return 8;
		case BEYARD: return 8;
		case YOGI: return 9;
		case WALLACE: case EAGLER: return 10;
		case LUNDGREN: return 11;
		case HECTOR: case WIRE: return 12;
		case OSWIN: case ELIWOOD: case MARCUS: case REBECCA: case BARTRE: case LOWEN: case ZAGAN: return 13;
		case GUY: case BOIES: return 14;
		case MERLINUS: case PUZON: return 14;
		case PRISCILLA: case ERIK: return 15;
		case SEALEN: return 16;
		case BAUKER: return 17;
		case RAVEN: case BERNARD: return 18;
		case CANAS: case FARGUS: case DAMIAN: return 19;
		case ZOLDAM: return 19;
		case DART: case FIORA: case UHAI: return 20;
		case AION: case KISHUNA: return 20;
		case TEODOR: return 20;
		case LEGAULT: case DARIN: case CAMERON: return 21;
		case NINIAN: case OLEG: return 22;
		case ISADORA: case HEATH: case EUBANS: return 23;
		case HAWKEYE: case PAUL: case JASMINE: return 24;
		case GEITZ: case LINUS_FFO: case LLOYD_FFO: return 25;
		case FARINA: case PASCAL: return 26;
		case LOUISE: case PENT: case VAIDA: return 27;
		case HARKEN: case KAREL: case JERME: case KENNETH: return 28;
		case NINO: case MAXIME: case URSULA: return 29;
		case JAFFAR: case SONIA: return 29;
		case LLOYD_COD: case LINUS_COD: return 30;
		case GEORG: case KAIM: return 31;
		case DENNING: return 32;
		case KARLA: return 32;
		case RENAULT: case LIMSTELLA: return 33;
		case ATHOS: case NERGAL: case DRAGON: case BRENDAN_MORPH: case DARIN_MORPH: case JERME_MORPH: case KENNETH_MORPH: case LINUS_MORPH: case LLOYD_MORPH: case UHAI_MORPH: case URSULA_MORPH: return 34;
		default: return 0;
		}
	}
	
	public int chapterCount() {
		return 34; // Including lyn chapters and endgame. Gaiden chapters count as the same chapter.
	}
	
	public Set<GBAFECharacter> extraCharacters() {
		return new HashSet<GBAFECharacter>();
	}
	
	public Set<GBAFECharacter> charactersExcludedFromRandomRecruitment() {
		return new HashSet<GBAFECharacter>(Arrays.asList(Character.ATHOS));
	}
	
	public Set<Integer> linkedPortraitIDs(int characterID) {
		Character character = Character.valueOf(characterID);
		if (Character.charactersWithMultiplePortraits.containsKey(character)) {
			return Character.charactersWithMultiplePortraits.get(character);
		}
		
		return new HashSet<Integer>();
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
	
	public Set<GBAFECharacter> mustPromote() {
		return new HashSet<GBAFECharacter>(Character.requiresPromotion);
	}
	
	public GBAFECharacter characterWithID(int characterID) {
		GBAFECharacter character = Character.valueOf(characterID);
		if (character == null) {
			character = Character.NONE;
		}
		
		return character;
	}
	
	public boolean isValidCharacter(GBAFECharacter character) {
		return character != Character.NONE;
	}
	
	public GBAFECharacter nullCharacter() {
		return Character.NONE;
	}
	
	public boolean isEnemyAtAnyPoint(int characterID) {
		Character character = Character.valueOf(characterID);
		switch (character) {
		case DORCAS:
		case GUY:
		case RAVEN:
		case DART:
		case LEGAULT:
		case HEATH:
		case GEITZ:
		case HARKEN:
		case VAIDA:
			return true;
		default:
			return !allPlayableCharacters().contains(character);
		}
	}

	public int[] affinityValues() {
		int[] values = new int[FE7Character.Affinity.validAffinities().length];
		int i = 0;
		for (FE7Character.Affinity affinity : FE7Character.Affinity.validAffinities()) {
			values[i++] = affinity.value;
		}
		
		return values;
	}
	
	public int affinityValueForAffinity(GBAFECharacterData.Affinity affinity) {
		switch (affinity) {
		case FIRE: return FE7Character.Affinity.FIRE.value;
		case THUNDER: return FE7Character.Affinity.THUNDER.value;
		case WIND: return FE7Character.Affinity.WIND.value;
		case WATER: return FE7Character.Affinity.WATER.value;
		case DARK: return FE7Character.Affinity.DARK.value;
		case LIGHT: return FE7Character.Affinity.LIGHT.value;
		case ANIMA: return FE7Character.Affinity.ANIMA.value;
		default: return FE7Character.Affinity.NONE.value;
		}
	}

	public int canonicalID(int characterID) {
		return Character.canonicalIDForCharacterID(characterID);
	}
	
	public Integer canonicalLevelForCharacter(GBAFECharacter character) {
		Character fe7Char = Character.valueOf(character.getID());
		switch (fe7Char) {
		case RAVEN: return 5;
		case GUY: return 3;
		case LOUISE: return 4;
		case KAREL: return 8;
		case WIL_TUTORIAL: return 2;
		default: return null;
		}
	}

	public GBAFECharacterData characterDataWithData(byte[] data, long offset, Boolean hasLimitedClasses) {
		FE7Character charData = new FE7Character(data, offset, hasLimitedClasses);
		Character fe7Char = Character.valueOf(charData.getID());
		if (fe7Char != null) {
			charData.initializeDisplayString(fe7Char.toString());
		} else {
			charData.initializeDisplayString("Unregistered [0x" + Integer.toHexString(charData.getID()) + "]");
		}
		return charData;
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
	
	public Set<GBAFEClass> playerOnlyClasses() {
		Set<GBAFEClass> classes = new HashSet<GBAFEClass>(CharacterClass.allPlayerOnlyClasses);
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
		
		classList.sort(GBAFEClass.idComparator);
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
		
		classList.sort(GBAFEClass.idComparator);
		return classList.toArray(new GBAFEClass[classList.size()]);
	}
	
	public boolean isFlier(GBAFEClass charClass) {
		return CharacterClass.flyingClasses.contains(charClass);
	}

	public Set<GBAFEClass> classesThatLoseToClass(GBAFEClass sourceClass, GBAFEClass winningClass, Map<String, Boolean> options) {
		Boolean excludeLords = options.get(GBAFEClassProvider.optionKeyExcludeLords);
		if (excludeLords == null) { excludeLords = false; }
		Boolean excludeThieves = options.get(GBAFEClassProvider.optionKeyExcludeThieves);
		if (excludeThieves == null) { excludeThieves = false; }
		return new HashSet<GBAFEClass>(CharacterClass.classesThatLoseToClass(CharacterClass.valueOf(sourceClass.getID()), 
				CharacterClass.valueOf(winningClass.getID()), excludeLords, excludeThieves));
	}

	public Set<GBAFEClass> targetClassesForRandomization(GBAFEClass sourceClass, boolean isForEnemy, Map<String, Boolean> options) {
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
		Boolean excludeSpecial = options.get(GBAFEClassProvider.optionKeyExcludeSpecial);
		if (excludeSpecial == null) { excludeSpecial = false; }
		Boolean restrictGender = options.get(GBAFEClassProvider.optionKeyRestrictGender);
		if (restrictGender == null) { restrictGender = false; }
		
		return new HashSet<GBAFEClass>(CharacterClass.targetClassesForRandomization(CharacterClass.valueOf(sourceClass.getID()), isForEnemy,
				excludeSource, excludeLords, excludeThieves, excludeSpecial, requireAttack, requiresRange, applyRestrictions, restrictGender));
	}
	
	public GBAFEClass correspondingMaleClass(GBAFEClass charClass) {
		switch ((FE7Data.CharacterClass)charClass) {
		case LORD_LYN: return FE7Data.CharacterClass.LORD_ELIWOOD;
		case BLADE_LORD: return FE7Data.CharacterClass.GREAT_LORD;
		case ARCHER_F: return FE7Data.CharacterClass.ARCHER;
		case MAGE_F: return FE7Data.CharacterClass.MAGE;
		case DANCER: return FE7Data.CharacterClass.BARD;
		case SWORDMASTER_F: return FE7Data.CharacterClass.SWORDMASTER;
		case SNIPER_F: return FE7Data.CharacterClass.SNIPER;
		case BISHOP_F: return FE7Data.CharacterClass.BISHOP;
		case SAGE_F: return FE7Data.CharacterClass.SAGE;
		case PALADIN_F: return FE7Data.CharacterClass.PALADIN;
		case WYVERN_LORD_F: return FE7Data.CharacterClass.WYVERN_LORD;
		case UBER_SAGE: return FE7Data.CharacterClass.SAGE;
		default: return charClass;
		}
	}
	public GBAFEClass correspondingFemaleClass(GBAFEClass charClass) {
		switch ((FE7Data.CharacterClass)charClass) {
		case LORD_ELIWOOD:
		case LORD_HECTOR: return FE7Data.CharacterClass.LORD_LYN;
		case ARCHER: return FE7Data.CharacterClass.ARCHER_F;
		case MAGE: return FE7Data.CharacterClass.MAGE_F;
		case BARD: return FE7Data.CharacterClass.DANCER;
		case GREAT_LORD:
		case LORD_KNIGHT: return FE7Data.CharacterClass.BLADE_LORD;
		case SWORDMASTER: return FE7Data.CharacterClass.SWORDMASTER_F;
		case SNIPER: return FE7Data.CharacterClass.SNIPER_F;
		case BISHOP: return FE7Data.CharacterClass.BISHOP_F;
		case SAGE: return FE7Data.CharacterClass.SAGE_F;
		case PALADIN: return FE7Data.CharacterClass.PALADIN_F;
		case WYVERN_LORD: return FE7Data.CharacterClass.WYVERN_LORD_F;
		default: return charClass;
		}
	}

	public void prepareForClassRandomization(Map<Integer, GBAFEClassData> classMap) {
		GBAFEClassData soldierData = classMap.get(CharacterClass.SOLDIER.ID);
		if (soldierData != null) {
			soldierData.setTargetPromotionID(CharacterClass.GENERAL.ID);
		}
	}

	public GBAFEClassData classDataWithData(byte[] data, long offset, GBAFEClassData demotedClass) {
		FE7Class charClass = new FE7Class(data, offset, demotedClass);
		CharacterClass fe7Class = CharacterClass.valueOf(charClass.getID());
		if (fe7Class != null) {
			charClass.initializeDisplayString(fe7Class.toString());
		} else {
			charClass.initializeDisplayString("Unregistered [0x" + Integer.toHexString(charClass.getID()) + "]");
		}
		return charClass;
	}
	
	public List<String> charClassAbility1Flags() {
		return Arrays.asList(CharacterAndClassAbility1Mask.values()).stream().map(ability -> ability.displayString()).filter(string -> string != null).collect(Collectors.toList());
	}
	
	public List<String> charClassAbility2Flags() {
		return Arrays.asList(CharacterAndClassAbility2Mask.values()).stream().map(ability -> ability.displayString()).filter(string -> string != null).collect(Collectors.toList());
	}
	
	public List<String> charClassAbility3Flags() {
		return Arrays.asList(CharacterAndClassAbility3Mask.values()).stream().map(ability -> ability.displayString()).filter(string -> string != null).collect(Collectors.toList());
	}
	
	public List<String> charClassAbility4Flags() {
		return Arrays.asList(CharacterAndClassAbility4Mask.values()).stream().map(ability -> ability.displayString()).filter(string -> string != null).collect(Collectors.toList());
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
	
	public Set<GBAFEItem> weaponsOfRank(WeaponRank rank) {
		return new HashSet<GBAFEItem>(Item.weaponsOfRank(rank));
	}
	
	public Set<GBAFEItem> healingStaves(WeaponRank maxRank) {
		Set<Item> staves = new HashSet<Item>(Item.allHealingStaves);
		if (maxRank.isLowerThan(WeaponRank.S)) {
			staves.removeAll(Item.allSRank);
		}
		if (maxRank.isLowerThan(WeaponRank.A)) {
			staves.removeAll(Item.allARank);
		}
		if (maxRank.isLowerThan(WeaponRank.B)) {
			staves.removeAll(Item.allBRank);
		}
		if (maxRank.isLowerThan(WeaponRank.C)) {
			staves.removeAll(Item.allCRank);
		}
		if (maxRank.isLowerThan(WeaponRank.D)) {
			staves.removeAll(Item.allDRank);
		}
		return new HashSet<GBAFEItem>(staves);
	}
	
	public Set<GBAFEItem> prfWeaponsForClassID(int classID) {
		Set<Item> weapons = Item.prfWeaponsForClassID(classID);
		 if (weapons == null) { return new HashSet<GBAFEItem>(); }
		return new HashSet<GBAFEItem>(weapons);
	}
	
	public Set<GBAFEItem> allPotentialChestRewards() {
		return new HashSet<GBAFEItem>(Item.allPotentialRewards);
	}
	
	public Set<GBAFEItem> relatedItemsToItem(GBAFEItemData itemData, boolean excludeBasic) {
		if (itemData == null) { return new HashSet<GBAFEItem>(); }
		Item item = Item.valueOf(itemData.getID());
		if (item == null) { return new HashSet<GBAFEItem>(); }
		
		Set<GBAFEItem> relatedItems;
		
		if (item.isWeapon()) {
			Set<GBAFEItem> relatedWeapons = new HashSet<GBAFEItem>();
			relatedWeapons.addAll(Item.weaponsOfRank(item.getRank()));
			relatedWeapons.addAll(Item.weaponsOfType(item.getType()));
			relatedWeapons.removeAll(Item.allSRank);
			if (excludeBasic) {
				relatedWeapons.removeAll(Item.allBasicWeapons);
			}
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
		case LORD_ELIWOOD:
		case LORD_LYN:
		case MYRMIDON:
		case MYRMIDON_F:
		case SWORDMASTER:
		case SWORDMASTER_F:
		case MERCENARY:
		case MERCENARY_F:
		case THIEF:
		case THIEF_F:
		case ASSASSIN:
			usableItems.addAll(Item.allSwords);
			break;
		case LORD_HECTOR:
		case FIGHTER:
		case BERSERKER:
		case PIRATE:
		case BRIGAND:
		case CORSAIR:
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
			usableItems.addAll(Item.allAnima);
			break;
		case SAGE:
		case SAGE_F:
		case VALKYRIE:
		case UBER_SAGE:
			usableItems.addAll(Item.allStaves);
			usableItems.addAll(Item.allAnima);
			break;
		case SHAMAN:
		case SHAMAN_F:
			usableItems.addAll(Item.allDark);
			break;
		case DRUID:
		case DRUID_F:
			usableItems.addAll(Item.allDark);
			usableItems.addAll(Item.allStaves);
			break;
		case MONK:
			usableItems.addAll(Item.allLight);
			break;
		case BISHOP:
		case BISHOP_F:
			usableItems.addAll(Item.allStaves);
			usableItems.addAll(Item.allLight);
			break;
		case CLERIC:
		case TROUBADOUR:
			usableItems.addAll(Item.allStaves);
			break;
		case LORD_KNIGHT:
		case CAVALIER:
		case CAVALIER_F:
		case FALCON_KNIGHT:
		case WYVERN_LORD:
		case WYVERN_LORD_F:
			usableItems.addAll(Item.allSwords);
			usableItems.addAll(Item.allLances);
			break;
		case GREAT_LORD:
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
		case BLADE_LORD:
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
	
	public Set<GBAFEItem> comparableWeaponsForClass(int classID, WeaponRanks ranks, GBAFEItemData originalItem, boolean strict) {
		if (originalItem == null) { return new HashSet<GBAFEItem>(); }
		Item item = Item.valueOf(originalItem.getID());
		if (item == null) { return new HashSet<GBAFEItem>(); }
		
		Set<GBAFEItem> itemsUsableByClass = new HashSet<GBAFEItem>(weaponsForClass(classID));
		
		itemsUsableByClass.removeIf(weapon -> (weapon.getType() == WeaponType.SWORD && ranks.swordRank.isLowerThan(weapon.getRank())));
		itemsUsableByClass.removeIf(weapon -> (weapon.getType() == WeaponType.LANCE && ranks.lanceRank.isLowerThan(weapon.getRank())));
		itemsUsableByClass.removeIf(weapon -> (weapon.getType() == WeaponType.AXE && ranks.axeRank.isLowerThan(weapon.getRank())));
		itemsUsableByClass.removeIf(weapon -> (weapon.getType() == WeaponType.BOW && ranks.bowRank.isLowerThan(weapon.getRank())));
		itemsUsableByClass.removeIf(weapon -> (weapon.getType() == WeaponType.ANIMA && ranks.animaRank.isLowerThan(weapon.getRank())));
		itemsUsableByClass.removeIf(weapon -> (weapon.getType() == WeaponType.LIGHT && ranks.lightRank.isLowerThan(weapon.getRank())));
		itemsUsableByClass.removeIf(weapon -> (weapon.getType() == WeaponType.DARK && ranks.darkRank.isLowerThan(weapon.getRank())));
		itemsUsableByClass.removeIf(weapon -> (weapon.getType() == WeaponType.STAFF && ranks.staffRank.isLowerThan(weapon.getRank())));
		
		Set<GBAFEItem> usableSet = new HashSet<GBAFEItem>(itemsUsableByClass);
		
		// Only allow weapons of surrounding ranks. That is to say, if we're replacing a D rank weapon, let the pool be made up of any weapon between E and C.
		Set<WeaponRank> allowableRanks = WeaponRank.surroundingRanks(item.getRank());
		itemsUsableByClass.removeIf(weapon -> allowableRanks.contains(weapon.getRank()) == false);
		
		if (strict) {
			Set<GBAFEItem> usableByRank = new HashSet<GBAFEItem>(itemsUsableByClass);
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
			}  else if (Item.allRangedWeapons.contains(item) && !Collections.disjoint(Item.tomeRangeReplacementSet, itemsUsableByClass)) {
				// Is a ranged weapon, but not in the ranged set. This is probably a tome. If we need range, try ranged swords/lances/axes/bows.
				DebugPrinter.log(DebugPrinter.Key.STRICT_WEAPON_ASSIGNMENT, "Has range, but no special range (probably a tome).");
				itemsUsableByClass.retainAll(Item.tomeRangeReplacementSet);
			} else if (Item.poisonSet.contains(item) && !Collections.disjoint(Item.poisonSet, itemsUsableByClass)) {
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
			
		// Try to match the rank.
		Set<GBAFEItem> matchRank = new HashSet<GBAFEItem>(itemsUsableByClass);
		matchRank.removeIf(weapon -> (weapon.getRank() != item.getRank()));
		if (!matchRank.isEmpty() && matchRank.stream().allMatch(rankMatchingItem -> Item.allStaves.contains(rankMatchingItem)) == false) {
			return matchRank;
		}
		
		return itemsUsableByClass;
	}
	
	public Set<GBAFEItem> formerThiefInventory() {
		return new HashSet<GBAFEItem>(Item.formerThiefKit());
	}

	public Set<GBAFEItem> thiefItemsToRemove() {
		return new HashSet<GBAFEItem>(Item.itemsToRemoveFromFormerThief());
	}
	
	public Set<GBAFEItem> specialItemsToRetain() {
		return new HashSet<GBAFEItem>(Arrays.asList(Item.MEMBER_CARD));
	}

	public Set<GBAFEItem> itemKitForSpecialClass(int classID, Random rng) {
		Set<Item> kit = Item.specialClassKit(classID, rng);
		if (kit == null) { return new HashSet<GBAFEItem>(); }
		return new HashSet<GBAFEItem>(kit);
	}
	
	public Set<GBAFEItem> playerOnlyWeapons() {
		return new HashSet<GBAFEItem>();
	}
	
	public Set<GBAFEItem> promoWeapons() {
		return new HashSet<GBAFEItem>(Item.promoSet);
	}
	
	public Set<GBAFEItem> poisonWeapons() {
		return new HashSet<GBAFEItem>(Item.poisonSet);
	}
	
	public Set<GBAFEItem> commonDrops() {
		return new HashSet<GBAFEItem>(Item.commonDrops);
	}
	
	public Set<GBAFEItem> uncommonDrops() {
		return new HashSet<GBAFEItem>(Item.uncommonDrops);
	}
	
	public Set<GBAFEItem> rareDrops() {
		return new HashSet<GBAFEItem>(Item.rareDrops);
	}
	
	public Set<GBAFEItem> vendorItems(boolean rare) {
		Set<GBAFEItem> items = new HashSet<GBAFEItem>(Item.vendorItems);
		if (rare) {
			items.add(Item.ELIXIR);
			items.add(Item.MINE);
			items.add(Item.LIGHT_RUNE);
		}
		
		return items;
	}
	
	public Set<GBAFEItem> secretItems() {
		Set<GBAFEItem> items = new HashSet<GBAFEItem>();
		items.addAll(Item.allPromotionItems);
		items.addAll(Item.reaverSet);
		items.addAll(Item.killerSet);
		items.addAll(Item.braveSet);
		items.addAll(Item.allSiegeTomes);
		items.add(Item.ELIXIR);
		items.add(Item.RESCUE);
		items.add(Item.FORTIFY);
		items.add(Item.PHYSIC);
		items.add(Item.MANI_KATTI);
		items.add(Item.RAPIER);
		items.add(Item.WOLF_BEIL);
		items.add(Item.WO_DAO);
		items.add(Item.LIGHT_BRAND);
		items.add(Item.WIND_SWORD);
		return items;
	}
	public Set<GBAFEItem> rareSecretItems() {
		Set<GBAFEItem> items = new HashSet<GBAFEItem>();
		items.addAll(Item.allStatBoosters);
		items.addAll(Item.allSRank);
		items.addAll(Item.allDancingRings);
		items.add(Item.HAMMERNE);
		items.add(Item.WARP);
		items.add(Item.RUNE_SWORD);
		return items;
	}
	
	public Set<GBAFEItem> disallowedWeaponsInShops() {
		return new HashSet<GBAFEItem>();
	}
	
	public GBAFEItem legendaryWeaponOfType(WeaponType type, boolean isLord) {
		switch (type) {
		case SWORD:
			return isLord ? Item.DURANDAL : Item.REGAL_BLADE; // RIP Sol Katti
		case LANCE:
			return Item.REX_HASTA;
		case AXE:
			return isLord ? Item.ARMADS : Item.BASILIKOS;
		case BOW:
			return Item.RIENFLECHE;
		case ANIMA:
			return isLord ? Item.FORBLAZE : Item.EXCALIBUR;
		case LIGHT:
			return Item.AUREOLA;
		case DARK:
			return Item.GESPENST;
		case STAFF:
			return Item.FORTIFY;
		default:
			return null;
		}
	}
	
	public List<String> itemAbility1Flags() {
		return Arrays.asList(Item.Ability1Mask.values()).stream().map(mask -> Item.Ability1Mask.displayStrings.get(mask)).filter(string -> string != null).collect(Collectors.toList());
	}
	
	public List<String> itemAbility2Flags() {
		return Arrays.asList(Item.Ability2Mask.values()).stream().map(mask -> Item.Ability2Mask.displayStrings.get(mask)).filter(string -> string != null).collect(Collectors.toList());
	}
	
	public List<String> itemAbility3Flags() {
		return Arrays.asList(Item.Ability3Mask.values()).stream().map(mask -> Item.Ability3Mask.displayStrings.get(mask)).filter(string -> string != null).collect(Collectors.toList());
	}
	
	public List<String> weaponEffectFlags() {
		return Arrays.asList(Item.WeaponEffect.values()).stream().map(effect -> Item.WeaponEffect.displayStrings.get(effect)).filter(string -> string != null).collect(Collectors.toList());
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

	public GBAFEItemData itemDataWithData(byte[] data, long offset) {
		FE7Item item = new FE7Item(data, offset);
		Item fe7Item = Item.valueOf(item.getID());
		if (fe7Item != null) {
			item.initializeDisplayString(fe7Item.toString());
		} else {
			item.initializeDisplayString("Unregistered [0x" + Integer.toHexString(item.getID()) + "]");
		}
		return item;
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
				CharacterClass.NOMAD_TROOPER,
				CharacterClass.NOMAD_TROOPER_F,
				CharacterClass.TROUBADOUR,
				CharacterClass.VALKYRIE,
				CharacterClass.LORD_KNIGHT,
				CharacterClass.GREAT_LORD,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE // Since there are three potential Lords that can fall under the same effectiveness, we leave space for 3 class IDs and a terminator.
				));
	}

	public List<GBAFEClass> knightEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.KNIGHT,
				CharacterClass.KNIGHT_F,
				CharacterClass.GENERAL,
				CharacterClass.GENERAL_F,
				CharacterClass.GREAT_LORD,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE
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
				CharacterClass.NOMAD_TROOPER,
				CharacterClass.NOMAD_TROOPER_F,
				CharacterClass.TROUBADOUR,
				CharacterClass.VALKYRIE,
				CharacterClass.LORD_KNIGHT,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE
				));
	}

	public List<GBAFEClass> dragonEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.FIRE_DRAGON,
				CharacterClass.WYVERN_RIDER,
				CharacterClass.WYVERN_RIDER_F,
				CharacterClass.WYVERN_LORD,
				CharacterClass.WYVERN_LORD_F,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE
				));
	}

	public List<GBAFEClass> flierEffectivenessClasses() {
		return new ArrayList<GBAFEClass>(Arrays.asList(
				CharacterClass.PEGASUS_KNIGHT,
				CharacterClass.FALCON_KNIGHT,
				CharacterClass.WYVERN_RIDER,
				CharacterClass.WYVERN_RIDER_F,
				CharacterClass.WYVERN_LORD,
				CharacterClass.WYVERN_LORD_F,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE
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
				CharacterClass.BLADE_LORD,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE,
				CharacterClass.NONE
				));
	}

	public List<GBAFEClass> monsterEffectivenessClasses() {
		// Doesn't exist in FE7.
		return new ArrayList<GBAFEClass>();
	}
	
	public AdditionalData effectivenessPointerType(long effectivenessPtr) {
		if (effectivenessPtr == 0xC97E9CL) { return AdditionalData.KNIGHTCAV_EFFECT; }
		if (effectivenessPtr == 0xC97E96L) { return AdditionalData.KNIGHT_EFFECT; }
		if (effectivenessPtr == 0xC97EC5L) { return AdditionalData.DRAGON_EFFECT; }
		if (effectivenessPtr == 0xC97EB7L) { return AdditionalData.CAVALRY_EFFECT; }
		if (effectivenessPtr == 0xC97EADL) { return AdditionalData.MYRMIDON_EFFECT; }
		if (effectivenessPtr == 0xC97ED2L) { return AdditionalData.FLIERS_EFFECT; }
//		if (effectivenessPtr == 0xC97ECBL) { return AdditionalData.DRAGON_EFFECT; } // Used for Aureola. It's Dragon + Dark Druid.
		return null;
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
					CharacterClass.LORD_LYN,
					CharacterClass.BRIGAND
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
		if (promotionItem == PromotionItem.OCEAN_SEAL) {
			return new ArrayList<GBAFEClass>(Arrays.asList(
					CharacterClass.CORSAIR,
					CharacterClass.BRIGAND
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

	@Override
	public long portraitDataTableAddress() {
		return 0xC96584;
	}

	@Override
	public int numberOfPortraits() {
		return 229;
	}

	@Override
	public int bytesPerPortraitEntry() {
		return 28;
	}

	@Override
	public GBAFEPortraitData portraitDataWithData(byte[] data, long offset, int faceId) {
		return new GBAFEPortraitData(data, offset, faceId, true);
	}
	
	public static Map<Integer, List<Integer>> faceIdRelationMap = createFaceIdRelationMap();
	private static Map<Integer, List<Integer>> createFaceIdRelationMap(){
		Map<Integer, List<Integer>> relationMap = new HashMap<>();
		relationMap.put(0x2, Arrays.asList(0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB)); // Eliwood
		relationMap.put(0xC, Arrays.asList(0xD, 0xE, 0xF, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15)); // Hector
		relationMap.put(0x16, Arrays.asList(0x17, 0x18, 0x19, 0x1A)); // Lyn
		relationMap.put(0x1C, Arrays.asList(0x1D, 0x1E)); // Ninian
		relationMap.put(0x21, Arrays.asList(0x22)); // Jaffar
		relationMap.put(0x33, Arrays.asList(0x34)); // Florina
		relationMap.put(0x41, Arrays.asList(0x42, 0x43, 0x44)); // Nils
		return relationMap;
	}

	@Override
	public List<Integer> getRelatedPortraits(Integer faceId) {
		List<Integer> result = faceIdRelationMap.get(faceId);
		return result == null ? new ArrayList<>() : new ArrayList<>(result);	
	}
	
	@Override
	public List<Integer> getRelatedNames(Integer nameIndex) {
		return new ArrayList<>();
	}
	
	@Override
	public int getHuffmanTreeStart() {
		return 0x6BC;
	}

	@Override
	public int getHuffmanTreeEnd() {
		return  0x6B8;
	}

	@Override
	public int getTextTablePointer() {
		return  0x12CB8;
	}

	@Override
	public int getNumberOfTextStrings() {
		return 0x133E;
	}

	private final Set<Integer> excludedIndicies = generateExcludedIndiciesSet();
	
	public Set<Integer> getExcludedIndiciesFromNameUpdate() {
		return excludedIndicies;
	}

	private Set<Integer> generateExcludedIndiciesSet() {
		Set<Integer> indicies = new HashSet<>();
		indicies.add(0x405); // Lancereaver
		indicies.add(0x408); // Iron Lance
		indicies.add(0x409); // Slim Lance
		indicies.add(0x40A); // Steel Lance
		indicies.add(0x40B); // Silver Lance
		indicies.add(0x40C); // Toxin Lance
		indicies.add(0x40D); // Brave Lance
		indicies.add(0x40E); // Killer Lance
		
		return indicies;
	}
	
	@Override
	public long getBaseAddress() {
		return 0xC98F98;
	}

	private List<Integer> statboosterIndicies = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

	@Override
	public boolean isStatboosterIndex(int i) {
		return statboosterIndicies.contains(i);
	}

	@Override
	public int getNumberEntries() {
		return 16;
	}
	
	public Set<GBAFEShop> allShops() {
		return new HashSet<GBAFEShop>(Arrays.asList(Shops.values()));
	}
	
	public Set<GBAFEShop> allVendors() {
		return new HashSet<GBAFEShop>(Shops.allVendors());
	}
	
	public Set<GBAFEShop> allArmories() {
		return new HashSet<GBAFEShop>(Shops.allArmories());
	}
	
	public Set<GBAFEShop> allSecretShops() {
		return new HashSet<GBAFEShop>(Shops.allSecretShops());
	}
	
	public List<GBAFEShop> orderedShops() {
		return new ArrayList<GBAFEShop>(Arrays.asList(Shops.values()));
	}
	
	public Boolean isMapShop(GBAFEShop shop) { return false; }
}
