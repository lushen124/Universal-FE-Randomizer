package fedata.snes.fe4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FE4Data {
	
	public static final String FriendlyName = "ファイアーエムブレム　聖戦の系譜";
	public static final String InternalName = "FIREEMBLEM4";
	public static final long InternalNameUnheaderedOffset = 0xFFC0L;
	public static final long InternalNameHeaderedOffset = 0x101C0L;
	
	public static final long CleanUnheaderedCRC32 = 0xDC0D8CF9L;
	public static final long CleanUnheaderedSize = 4194304;
	
	public static final long CleanHeaderedCRC32 = 0x53D982CFL;
	public static final long CleanHeaderedSize = 4194816;
	
	public static final long PrepatchedUnheaderedCRC32 = 0x0939AF26L;
	public static final long PrepatchedUnheaderedSize = 8388608;
	
	public static final long PrepatchedHeaderedCRC32 = 0x4E3ACE45L;
	public static final long PrepatchedHeaderedSize = 8389120;
	
	// All offsets are assuming a Headered ROM. Subtract 0x200 for the unheadered version.
	// This is only for Playable Characters in Gen 1.
	public static final long Gen1CharacterTableOffset = 0x3B467;
	public static final int Gen1CharacterCount = 24;
	public static final int StaticCharacterSize = 38;
	
	// Static characters are those that don't do inheritance.
	public static final long Gen2StaticCharacterTable1Offset = 0x3B803;
	public static final int Gen2StaticCharacterTable1Count = 3; // Includes Shanan, Dalvin, and Asaello
	
	public static final long Gen2StaticCharacterTable3Offset = 0x3BAA1;
	public static final int Gen2StaticCharacterTable3Count = 5; // Includes Hermina, Linda, Laylea, Jeanne, and Iucharba.
	
	public static final long Gen2StaticCharacterTable2Offset = 0x3B881;
	public static final int Gen2StaticCharacterTable2Count = 14; // Everybody else. In order: Iuchar, Charlot, Hawk, Tristan, Finn, Deimne, Hannibal, Ares, Amid, Oifey, Daisy, Creidne, Muirne, and Julia
	
	// These characters all use inheritance of some kind.
	public static final long Gen2ChildrenCharacterTable1Offset = 0x3B7F7;
	public static final int Gen2ChildrenCharacterTable1Count = 2; // Includes Seliph and Leif
	public static final int Gen2ChildrenCharacterTable1ItemSize = 126; // For whatever reason, Nightmare says the object is "126 bytes" long, even though we only care about 12 of them.
	
	public static final long Gen2ChildrenCharacterTable2Offset = 0x3BA95;
	public static final int Gen2ChildrenCharacterTable2Count = 1; // Only Altena is here.
	public static final int Gen2ChildrenCharacterSize = 12; // Like normal.
	
	public static final long Gen2ChildrenCharacterTable3Offset = 0x3BB5F;
	public static final int Gen2ChildrenCharacterTable3Count = 14; // Everybody else. In order: Ulster, Febail, Coirpre, Ced, Diarmuid, Lester, Arthur, Patty, Larcei, Lana, Fee, Tine, Lynn, and Nanna.
	
	// Promotions are separated out and are defined individually. Two characters of the same class could go to different promotions.
	public static final long PromotionTableOffset = 0x3853C;
	public static final int PromotionTableCount = 63;
	public static final int PromotionTableEntrySize = 1; // Each item is 1 byte (the class ID). The order is baked in, following the character IDs - 1 (because Sigurd is 1).
	
	// All player weapons are tracked by kills and usage, so they need special treatment here.
	// While weapon stats are located elsewhere, these define which item is in which "slot" so to sepak.
	public static final long PlayerItemMappingTableOffset = 0x3F688;
	public static final int PlayerItemMappingTableCount = 145; // We only have 144 items to work with, technically. Don't use item 0.
	public static final int PlayerItemMappingTableItemSize = 1; // Each item is a byte, representing the item ID in that slot. The index is how the items are referred to afterwards.
	
	public static final int EnemyDataSize = 13;
	
	public enum Character {
		NONE(0x0000),
		
		SIGURD(0x0001), NAOISE(0x0002), ALEC(0x0003), ARDEN(0x0004), FINN_GEN_1(0x0005), QUAN(0x0006), MIDIR(0x0007),
		LEWYN(0x0008), CHULAINN(0x0009), AZELLE(0x000A), JAMKE(0x000B), CLAUD(0x000C), BEOWOLF(0x000D), LEX(0x000E),
		DEW(0x000F), DEIRDRE(0x0010), ETHLYN(0x0011), LACHESIS(0x0012), AYRA(0x0013), ERINYS(0x0014), TAILTIU(0x0015),
		SILVIA(0x0016), EDAIN(0x0017), BRIGID(0x0018),
		
		SELIPH(0x0019), SHANNAN(0x001A), LEIF(0x001D), IUCHAR(0x001E), FINN_GEN_2(0x0022), HANNIBAL(0x0024),
		ARES(0x0025), OIFEY(0x0027), JULIA(0x002B), ALTENA(0x002C), IUCHARBA(0x0031),
		
		ULSTER(0x0032), FEBAIL(0x0033), COIRPRE(0x0034), CED(0x0035), DIARMUID(0x0036), LESTER(0x0037), ARTHUR(0x0038),
		PATTY(0x0039), LARCEI(0x003A), LANA(0x003B), FEE(0x003C), TINE(0x003D), LENE(0x003E), NANNA(0x003F),
		
		DALVIN(0x001B), ASAELLO(0x001C), CHARLOT(0x001F), HAWK(0x0020), TRISTAN(0x0021), DEIMNE(0x0023), AMID(0x0026),
		DAISY(0x0028), CREIDNE(0x0029), MUIRNE(0x002A), HERMINA(0x002D), LINDA(0x002E), LAYLEA(0x002F), JEANNE(0x0030),
		
		// PROLOGUE
		GERRARD(0x0040), DIMAGGIO(0x0041), // 0x42 - 0x47 = VERDANE ARMY
		
		PROLOGUE_VERDANE_ARMY_1(0x42),
		PROLOGUE_VERDANE_ARMY_2(0x43),
		PROLOGUE_VERDANE_ARMY_3(0x44),
		PROLOGUE_VERDANE_ARMY_4(0x45),
		PROLOGUE_VERDANE_ARMY_5(0x46),
		PROLOGUE_VERDANE_ARMY_6(0x47),
		
		// CH 1
		CIMBAETH(0x0048), // 0x49 - 0x4C = VERDANE ARMY
		MUNNIR(0x004D), // 0x4E = COMMANDER, 0x4F - 0x51 = VERDANE ARMY
		SANDIMA(0x0052), // 0x53 - 0x54 = VERDANE ARMY, 0x55 = COMMANDER, 0x56 = VERDANE ARMY, 0x57 = THIEF
		ELLIOT_CH1_SCENE(0x0058), // 0x59 = HEIRHEIN ARMY
		ELDIGAN_CH1_SCENE(0x005A), // 0x5B = CROSS KNIGHTS, 0x5C = COMMANDER
		
		CH1_VERDANE_ARMY_1(0x49),
		CH1_VERDANE_ARMY_2(0x4A),
		CH1_VERDANE_ARMY_3(0x4B),
		CH1_VERDANE_ARMY_4(0x4C),
		CH1_VERDANE_COMMANDER_1(0x4E),
		CH1_VERDANE_ARMY_5(0x4F),
		CH1_VERDANE_ARMY_6(0x50),
		CH1_VERDANE_ARMY_7(0x51),
		CH1_VERDANE_ARMY_8(0x53),
		CH1_VERDANE_ARMY_9(0x54),
		CH1_VERDANE_COMMANDER_2(0x55),
		CH1_VERDANE_ARMY_10(0x56),
		CH1_VERDANE_THIEF_1(0x57),
		CH1_HEIRHEIN_ARMY(0x59),
		CH1_CROSS_KNIGHTS(0x5B),
		CH1_COMMANDER(0x5C),
		
		// CH 2
		BOLDOR(0x005D), // 0x5E = HEIRHEIN ARMY
		PHILLIP(0x005F), // 0x60 - 0x65 = HEIRHEIN ARMY
		ELLIOT_CH2(0x0066), //0x67 - 0x69 = HEIRHEIN ARMY, 0x6A = COMMANDER, 0x6B = ANPHONY ARMY, 0x6C = THEIF, 0x6D = MACKILY ARMY
		EVE(0x006E),
		MACBETH(0x006F), // 0x70 - 0x72 = ANPHONY ARMY
		VOLTZ(0x0073), // 0x74 = MERCENARY
		CLEMENT(0x0075), // 0x76 - 0x78 = MACKILY ARMY, 0x79 = COMMANDER, 0x7A = MACKILY ARMY
		CHAGALL_CH2(0x007B), // 0x7C - 0x7D = AGUSTY ARMY, 0x7E = COMMANDER, 0x7F = AGUSTY ARMY, 0x80 = COMMANDER, 0x81 = AGUSTY ARMY, 0x82 = FURY SQUAD
		ZYNE(0x0083), // 0x84 - 0x87 = AGUSTY ARMY,
		EVA(0x0088),
		ALVA(0x0089),
		
		CH2_HEIRHEIN_ARMY_1(0x5E),
		CH2_HEIRHEIN_ARMY_2(0x60),
		CH2_HEIRHEIN_ARMY_3(0x61),
		CH2_HEIRHEIN_ARMY_4(0x62),
		CH2_HEIRHEIN_ARMY_5(0x63),
		CH2_HEIRHEIN_ARMY_6(0x64),
		CH2_HEIRHEIN_ARMY_7(0x65),
		CH2_HEIRHEIN_ARMY_8(0x67),
		CH2_HEIRHEIN_ARMY_9(0x68),
		CH2_HEIRHEIN_ARMY_10(0x69),
		CH2_HEIRHEIN_COMMANDER_1(0x6A),
		CH2_ANPHONY_ARMY_1(0x6B),
		CH2_ANPHONY_THIEF_1(0x6C),
		CH2_MACKILY_ARMY_1(0x6D),
		CH2_ANPHONY_ARMY_2(0x70),
		CH2_ANPHONY_ARMY_3(0x71),
		CH2_ANPHONY_ARMY_4(0x72),
		CH2_VOLTZ_SQUAD(0x74),
		CH2_MACKILY_ARMY_2(0x76),
		CH2_MACKILY_ARMY_3(0x77),
		CH2_MACKILY_ARMY_4(0x78),
		CH2_MACKILY_COMMANDER_1(0x79),
		CH2_MACKILY_ARMY_5(0x7A),
		CH2_AGUSTY_ARMY_1(0x7C),
		CH2_AGUSTY_ARMY_2(0x7D),
		CH2_AGUSTY_COMMANDER_1(0x7E),
		CH2_AGUSTY_ARMY_3(0x7F),
		CH2_AGUSTY_COMMANDER_2(0x80),
		CH2_AGUSTY_ARMY_4(0x81),
		CH2_ERINYS_SQUAD(0x82),
		CH2_AGUSTY_ARMY_5(0x84),
		CH2_AGUSTY_ARMY_6(0x85),
		CH2_AGUSTY_ARMY_7(0x86),
		CH2_AGUSTY_ARMY_8(0x87),
		
		// CH 3
		JACOBAN(0x008A), // 0x8B - 0x8F = MADINO ARMY, 0x90 = COMMANDER, 0x91 = MADINO ARMY, 0x92 = COMMANDER, 0x93 = MADINO ARMY, 0x94 = COMMANDER, 0x95 = MADINO ARMY, 0x96 = COMMANDER, 0x97 = MADINO ARMY, 
						 //0x98 = COMMANDER, 0x99 = MADINO ARMY, 0x9A = COMMANDER, 0x9B = MADINO ARMY, 0x9C = PIRATE, 0x9D = COMMANDER, 0x9E = SILVAIL ARMY
		ELDIGAN_CH3(0x009F), // 0xA0 = CROSS KNIGHTS
		CHAGALL_CH3(0x00A1), // 0xA2 - 0xA3 = SILVAIL ARMY
		PAPILION(0x00A4), // 0xA5 = THRACIA ARMY, 0xA6 - 0xA9 = PIRATE
		DOBARL(0x00AA), // 0xAB = PIRATE
		PIZARL(0x00AC), // 0xAD - 0xB0 = PIRATE
		
		CH3_MADINO_ARMY_1(0x8B),
		CH3_MADINO_ARMY_2(0x8C),
		CH3_MADINO_ARMY_3(0x8D),
		CH3_MADINO_ARMY_4(0x8E),
		CH3_MADINO_ARMY_5(0x8F),
		CH3_MADINO_COMMANDER_1(0x90),
		CH3_MADINO_ARMY_6(0x91),
		CH3_MADINO_COMMANDER_2(0x92),
		CH3_MADINO_ARMY_7(0x93),
		CH3_MADINO_COMMANDER_3(0x94),
		CH3_MADINO_ARMY_8(0x95),
		CH3_MADINO_COMMANDER_4(0x96),
		CH3_MADINO_ARMY_9(0x97),
		CH3_MADINO_COMMANDER_5(0x98), 
		CH3_MADINO_ARMY_10(0x99),
		CH3_MADINO_COMMANDER_6(0x9A),
		CH3_MADINO_ARMY_11(0x9B),
		CH3_MADINO_PIRATE_1(0x9C),
		CH3_MADINO_COMMANDER_7(0x9D),
		CH3_SILVAIL_ARMY_1(0x9E),
		CH3_CROSS_KNIGHTS(0xA0),
		CH3_SILVAIL_ARMY_2(0xA2),
		CH3_SILVAIL_ARMY_3(0xA3),
		CH3_PAPILION_SQUAD(0xA5),
		CH3_PIRATE_1(0xA6),
		CH3_PIRATE_2(0xA7),
		CH3_PIRATE_3(0xA8),
		CH3_PIRATE_4(0xA9),
		CH3_PIRATE_5(0xAB),
		CH3_PIRATE_6(0xAD),
		CH3_PIRATE_7(0xAE),
		CH3_PIRATE_8(0xAF),
		CH3_PIRATE_9(0xB0),
		
		// CH 3 Scene
		ARVIS_CH3_SCENE(0x00B1), 
		LOMBARD_CH3_SCENE(0x00B2), 
		CH3_SCENE_GRAO_RITTER(0x00B3), 
		REPTOR_CH3_SCENE(0x00B4), 
		CH3_SCENE_GELB_RITTER(0x00B5), 
		MAHNYA_CH3_SCENE(0x00B6), 
		CH3_SCENE_SILESIA_ARMY(0x00B7),
		
		// CH 4
		MAIOS(0x00B8), // 0xB9 - 0xBA = THOVE ARMY, 0xBB = COMMANDER, 0xBC = THOVE ARMY
		CUVULI(0x00BD), // 0xBE = THOVE ARMY, 0xBF = COMMANDER, 0xC0 - 0xC1 = THOVE ARMY
		DEETVAR(0x00C2), // 0xC3 = DEETVAR SQUAD, 0xC4 = PIRATE, 0xC5 = SILESIA ARMY
		DACCAR(0x00C6),
		MAHNYA(0x00C7), // 0xC8 = MAHNYA SQUAD
		PAMELA(0x00C9), // 0xCA = PAMELA SQUAD
		ANDOREY_CH4(0x00CB), // 0xCC = BEIG RITTER, 0xCD - 0xCE = CIVILIAN
		DONOVAN(0x00CF), // 0xD0 - 0xD4 = ZAXON ARMY
		LAMIA(0x00D5), // 0xD6 - 0xDA = MERCENARY
		
		CH4_THOVE_ARMY_1(0xB9),
		CH4_THOVE_ARMY_2(0xBA),
		CH4_THOVE_COMMANDER_1(0xBB),
		CH4_THOVE_ARMY_3(0xBC),
		CH4_THOVE_ARMY_4(0xBE),
		CH4_THOVE_COMMANDER_2(0xBF),
		CH4_THOVE_ARMY_5(0xC0),
		CH4_THOVE_ARMY_6(0xC1),
		CH4_DEETVAR_SQUAD(0xC3),
		CH4_PIRATE_1(0xC4),
		CH4_SILESIA_ARMY(0xC5),
		CH4_MAHNYA_SQUAD(0xC8),
		CH4_PAMELA_SQUAD(0xCA),
		CH4_BEIGE_RITTER(0xCC),
		CH4_ZAXON_ARMY_1(0xD0),
		CH4_ZAXON_ARMY_2(0xD1),
		CH4_ZAXON_ARMY_3(0xD2),
		CH4_ZAXON_ARMY_4(0xD3),
		CH4_ZAXON_ARMY_5(0xD4),
		CH4_LAMIA_MERC_1(0xD6),
		CH4_LAMIA_MERC_2(0xD7),
		CH4_LAMIA_MERC_3(0xD8),
		CH4_LAMIA_MERC_4(0xD9),
		CH4_LAMIA_MERC_5(0xDA),
		
		// CH 5
		LOMBARD(0x00DB), // 0xDC = CHILD, 0xDD = LUBECK ARMY
		SLAYDER(0x00DE), // 0xDF = LUBECK ARMY, 0xE0 = COMMANDER, 0xE1 - 0xE2 = LUBECK ARMY, 0xE3 = THIEF, 0xE4 = COMMANDER, 0xE5 - 0xE7 = LUBECK ARMY
		BYRON(0x00E8),
		ANDOREY_CH5(0x00E9), // 0xEA = BEIG RITTER
		VAHA(0x00EB), // 0xEC = VELTHOMER ARMY, 0xED = PRIEST, 0xEE = VELTHOMER ARMY
		TRAVANT_CH5(0x00EF),
		MAGORN(0x00F0), // 0xF1 = THRACIA ARMY, 0xF2 = LENSTER ARMY
		AIDA(0x00F3), // 0xF4 = VELTHOMER ARMY
		REPTOR(0x00F5), // 0xF6 - 0xFD = FREEGE ARMY, 0xFE = COMMANDER, 0xFF = ROT RITTER, 0x100 = COMMANDER, 0x101 = ROT RITTER, 0x102 = COMMANDER, 0x103 = ROT RITTER
		ARVIS_CH5(0x0104), // 0x105 = COMMANDER, 0x106 = ROT RITTER
		
		CH5_LUBECK_ARMY_1(0xDD),
		CH5_LUBECK_ARMY_2(0xDF),
		CH5_LUBECK_COMMANDER_1(0xE0),
		CH5_LUBECK_ARMY_3(0xE1),
		CH5_LUBECK_ARMY_4(0xE2),
		CH5_LUBECK_THIEF_1(0xE3),
		CH5_LUBECK_COMMANDER_2(0xE4),
		CH5_LUBECK_ARMY_5(0xE5),
		CH5_LUBECK_ARMY_6(0xE6),
		CH5_LUBECK_ARMY_7(0xE7),
		CH5_BEIGE_RITTER(0xEA),
		CH5_VELTHOMER_ARMY_1(0xEC),
		CH5_VELTHOMER_PRIEST_1(0xED),
		CH5_VELTHOMER_ARMY_2(0xEE),
		CH5_THRACIA_ARMY(0xF1),
		CH5_LEONSTER_ARMY(0xF2),
		CH5_VELTHOMER_ARMY_3(0xF4),
		CH5_FREEGE_ARMY_1(0xF6),
		CH5_FREEGE_ARMY_2(0xF7),
		CH5_FREEGE_ARMY_3(0xF8),
		CH5_FREEGE_ARMY_4(0xF9),
		CH5_FREEGE_ARMY_5(0xFA),
		CH5_FREEGE_ARMY_6(0xFB),
		CH5_FREEGE_ARMY_7(0xFC),
		CH5_FREEGE_ARMY_8(0xFD),
		
		// Probably CH5 Scene?
		CH5_COMMANDER_1(0xFE),
		CH5_ROT_RITTER_1(0xFF), 
		CH5_COMMANDER_2(0x100),
		CH5_ROT_RITTER_2(0x101),
		CH5_COMMANDER_3(0x102),
		CH5_ROT_RITTER_3(0x103),
		CH5_COMMANDER_4(0x105),
		CH5_ROT_RITTER_4(0x106),
		
		// CH 6
		HAROLD(0x0107), // 0x108 - 0x109 = GANESHA ARMY, 0x10A = THIEF, 0x10B = SOPHARA ARMY, 0x10C - 0x10D = ISAAC ARMY, 0x10E = SOPHARA ARMY
		SCHMIDT(0x010F), // 0x110 = RIVOUGH ARMY
		DANANN(0x0111), // 0x112 - 0x113 = GANESHA ARMY
		
		CH6_GANESHA_ARMY_1(0x108),
		CH6_GANESHA_ARMY_2(0x109),
		CH6_GANESHA_THIEF_1(0x10A),
		CH6_SOPHARA_ARMY_1(0x10B),
		CH6_ISAAC_ARMY_1(0x10C),
		CH6_ISAAC_ARMY_2(0x10D),
		CH6_SOPHARA_ARMY_2(0x10E),
		CH6_RIVOUGH_ARMY_1(0x110),
		CH6_GANESHA_ARMY_3(0x112),
		CH6_GANESHA_ARMY_4(0x113),
		
		// CH 7
		KUTUZOV(0x0114), // 0x115 - 0x116 = YIED MAGE, 0x117 = MERCENARY, 0x118 = YIED MAGE, 0x119 = COMMANDER, 0x11A - 0x11B = ALSTER ARMY, 0x11C = THIEF
		BLOOM_CH7(0x011D), // 0x11E = ALSTER ARMY, 0x11F = DARNA ARMY
		ISHTORE(0x0120), // 0x121 = MELGAN ARMY
		LIZA(0x0122), // 0x123 - 0x128 = MELGAN ARMY
		JAVARRO(0x0129), // 0x12A - 0x12B = MERCENARY
		BRAMSEL(0x012C), // 0x12D - DARNA ARMY, 0x12E = COMMANDER, 0x12F - 0x130 = ALSTER ARMY
		VAMPA_CH7(0x0131),
		FETRA_CH7(0x0132),
		ELIU_CH7(0x0133), // 0x134 - 0x136 = HIGH MAGE
		
		CH7_YIED_MAGE_1(0x115),
		CH7_YIED_MAGE_2(0x116),
		CH7_MERCENARY_1(0x117),
		CH7_YIED_MAGE_3(0x118),
		CH7_YIED_COMMANDER_1(0x119),
		CH7_ALSTER_ARMY_1(0x11A),
		CH7_ALSTER_ARMY_2(0x11B),
		CH7_ALSTER_THIEF(0x11C),
		CH7_ALSTER_ARMY_3(0x11E),
		CH7_DARNA_ARMY_1(0x11F),
		CH7_MELGAN_ARMY_1(0x121),
		CH7_MELGAN_ARMY_2(0x123),
		CH7_MELGAN_ARMY_3(0x124),
		CH7_MELGAN_ARMY_4(0x125),
		CH7_MELGAN_ARMY_5(0x126),
		CH7_MELGAN_ARMY_6(0x127),
		CH7_MELGAN_ARMY_7(0x128),
		CH7_JAVARRO_SQUAD_1(0x12A),
		CH7_JAVARRO_SQUAD_2(0x12B),
		CH7_MERCENARY_2(0x12A),
		CH7_MERCENARY_3(0x12B),
		CH7_DARNA_ARMY_2(0x12D),
		CH7_DARNA_COMMANDER_1(0x12E),
		CH7_ALSTER_ARMY_4(0x12F),
		CH7_ALSTER_ARMY_5(0x130),
		CH7_HIGH_MAGE_1(0x134),
		CH7_HIGH_MAGE_2(0x135),
		CH7_HIGH_MAGE_3(0x136),
		
		// CH 8
		MUHAMMAD(0x0137), // 0x138 - 0x13A = CONOTE ARMY, 0x13B = PRIEST
		OVO(0x013C), // 0x13D - 0x13E = CONOTE ARMY, 0x13F = THIEF, 0x140 = COMMANDER, 0x141 = CONOTE ARMY
		ISHTAR_CH8(0x0142), // 0x143 = COMMANDER, 0x144 = CONOTE ARMY;
		BLOOM_CH8(0x0145), // 0x146 = CONOTE ARMY, 0x147 - 0x148 = CIVILIAN, 0x149 = COMMANDER, 0x14A = THRACIA ARMY
		CORUTA(0x014B), // 0x14C = THRACIA ARMY, 0x14D = COMMANDER, 0x14E = THRACIA ARMY, 0x14F = COMMANDER, 0x150 = THRACIA ARMY
		MAIKOV(0x0151),
		VAMPA_CH8(0x0152),
		FETRA_CH8(0x0153),
		ELIU_CH8(0x0154),
		
		CH8_CONOTE_ARMY_1(0x138),
		CH8_CONOTE_ARMY_2(0x139),
		CH8_CONOTE_ARMY_3(0x13A),
		CH8_CONOTE_PRIEST_1(0x13B),
		CH8_CONOTE_ARMY_4(0x13D),
		CH8_CONOTE_ARMY_5(0x13E),
		CH8_CONOTE_THIEF_1(0x13F),
		CH8_CONOTE_COMMANDER_1(0x140),
		CH8_CONOTE_ARMY_6(0x141),
		CH8_CONOTE_COMMANDER_2(0x143),
		CH8_CONOTE_ARMY_7(0x144),
		CH8_CONOTE_ARMY_8(0x146),
		CH8_CONOTE_COMMANDER_3(0x149),
		CH8_THRACIA_ARMY_1(0x14A),
		CH8_THRACIA_ARMY_2(0x14C),
		CH8_THRACIA_COMMANDER_1(0x14D),
		CH8_THRACIA_ARMY_3(0x14E),
		CH8_THRACIA_COMMANDER_2(0x14F),
		CH8_THRACIA_ARMY_4(0x150),
		
		// CH 9
		// 0x155 - 0x158 = KAPATHOGIA ARMY, 0x159 - 0x15A = THRACIA ARMY, 0x15B = THIEF
		KANATZ(0x015C),
		DISLER(0x015D), // 0x15E = GRUTIA ARMY, 0x15F - 0x160 = MERCENARY
		TRAVANT_CH9(0x0161), // 0x162 = THRACIA ARMY
		JUDAH(0x0163), // 0x164 = COMMANDER, 0x165 = GRUTIA ARMY,
		ARION_CH9(0x0166), // 0x167 - 0x168 = DRAGON KNIGHT, 0x169 = COMMANDER, 0x16A = THRACIA ARMY, 0x16B = COMMANDER, 0x16C = THRACIA ARMY, 0x16D = COMMANDER, 0x16E = THRACIA ARMY
		MUSAR(0x016F),
		
		CH9_KAPATHOGIA_ARMY_1(0x155),
		CH9_KAPATHOGIA_ARMY_2(0x156),
		CH9_KAPATHOGIA_ARMY_3(0x157),
		CH9_KAPATHOGIA_ARMY_4(0x158),
		CH9_THRACIA_ARMY_1(0x159),
		CH9_THRACIA_ARMY_2(0x15A),
		CH9_THRACIA_THIEF_1(0x15B),
		CH9_GRUTIA_ARMY_1(0x15E),
		CH9_MERCENARY_1(0x15F),
		CH9_MERCENARY_2(0x160),
		CH9_THRACIA_ARMY_3(0x162),
		CH9_GRUTIA_COMMANDER_1(0x164),
		CH9_GRUTIA_ARMY_2(0x165),
		CH9_ARION_SQUAD_1(0x167),
		CH9_ARION_SQUAD_2(0x168),
		CH9_THRACIA_COMMANDER_1(0x169),
		CH9_THRACIA_ARMY_4(0x16A),
		CH9_THRACIA_COMMANDER_2(0x16B),
		CH9_THRACIA_ARMY_5(0x16C),
		CH9_THRACIA_COMMANDER_3(0x16D),
		CH9_THRACIA_ARMY_6(0x16E),
		
		// CH 10
		// 0x170 - 0x173 = IMPERIAL ARMY
		RIDALE(0x0174), // 0x175 - 0x179 = RIDALE'S SQUAD
		MORRIGAN(0x017A), // 0x17B = DARK MAGE, 0x17C = PIRATE
		HILDA_CH10(0x017D), // 0x17E = DARK PRIEST, 0x17F - 0x180 = DARK MAGE, 0x181 - 0x182 = MERCENARY, 0x183 - 0x184 = CHILD,
		ZAGAM(0x0185), // 0x186 - 0x18A = DARK MAGE, 0x18B - 0x18D = MERCENARY
		JULIUS_CH10(0x018E),
		ISHTAR_CH10(0x018F),
		ARVIS_CH10(0x0190), // 0x191 - 0x193 = ROT RITTER, 0x194 = DARK MAGE
		PALMARK(0x0195), // 0x196 - 0x197 = CHILD, 0x198 = COMMANDER, 0x199 - 0x19A = ROT RITTER, 0x19B = COMMANDER, 0x19C - 0x19D = ROT RITTER, 0x19E = COMMANDER, 0x19F - 0x1A1 = ROT RITTER, 0x1A2 - 0x1A3 = DARK MAGE
		
		CH10_IMPERIAL_ARMY_1(0x170),
		CH10_IMPERIAL_ARMY_2(0x171),
		CH10_IMPERIAL_ARMY_3(0x172),
		CH10_IMPERIAL_ARMY_4(0x173),
		CH10_RIDALE_SQUAD_1(0x175),
		CH10_RIDALE_SQUAD_2(0x176),
		CH10_RIDALE_SQUAD_3(0x177),
		CH10_RIDALE_SQUAD_4(0x178),
		CH10_RIDALE_SQUAD_5(0x179),
		CH10_DARK_MAGE_1(0x17B),
		CH10_PIRATE_1(0x17C),
		CH10_DARK_PRIEST_1(0x17E),
		CH10_DARK_MAGE_2(0x17F),
		CH10_DARK_MAGE_3(0x180),
		CH10_MERCENARY_1(0x181),
		CH10_MERCENARY_2(0x182),
		CH10_DARK_MAGE_4(0x186),
		CH10_DARK_MAGE_5(0x187),
		CH10_DARK_MAGE_6(0x188),
		CH10_DARK_MAGE_7(0x189),
		CH10_DARK_MAGE_8(0x18A),
		CH10_MERCENARY_3(0x18B),
		CH10_MERCENARY_4(0x18C),
		CH10_MERCENARY_5(0x18D),
		CH10_ROT_RITTER_1(0x191),
		CH10_ROT_RITTER_2(0x192),
		CH10_ROT_RITTER_3(0x193),
		CH10_DARK_MAGE_9(0x194),
		CH10_CHALPHY_COMMANDER_1(0x198),
		CH10_ROT_RITTER_4(0x199),
		CH10_ROT_RITTER_5(0x19A),
		CH10_CHALPHY_COMMANDER_2(0x19B),
		CH10_ROT_RITTER_6(0x19C),
		CH10_ROT_RITTER_7(0x19D),
		CH10_CHALPHY_COMMANDER_3(0x19E),
		CH10_ROT_RITTER_8(0x19F),
		CH10_ROT_RITTER_9(0x1A0),
		CH10_ROT_RITTER_10(0x1A1),
		CH10_DARK_MAGE_10(0x1A2),
		CH10_DARK_MAGE_11(0x1A3),
		
		// ENDGAME
		ROBERT(0x01A4), // 0x1A5 - 0x1A9 = MERCENARY
		BOYCE(0x01AA), // 0x1AB - 0x1AC = MERCENARY
		RODAN(0x01AD), // 0x1AE - 0x1B0 = EDDA ARMY
		YUPHEEL(0x01B2), // 0x1B3 - 0x1B4 = DARK MAGE
		FISHER(0x01B5), // 0x1B6 - 0x1B7 = DOZEL ARMY
		BRIAN(0x01B8), // 0x1B9 - 0x1BA = GRAO RITTER
		DAGGON(0x01BB),
		SCIPIO(0x01BC), // 0x1BD - 0x1BE = BEIG RITTER
		HILDA_FINAL(0x01BF), // 0x1C0 - 0x1C1 = GELB RITTER, 0x1C2 = DARK PRIEST, 0x1C3 = DARK MAGE
		MANFROY(0x01C4), // 0x1C5 = DARK MAGE
		JULIUS_FINAL(0x01C6),
		MUS(0x01C7), // a.k.a Eins
		BOVIS(0x01C8), // Zwei
		TIGRIS(0x01C9), // Drei
		LEPUS(0x01CA), // Vier
		DRACO(0x01CB), // Funf
		ANGUILLA(0x01CC), // Sechs
		EQUUS(0x01CD), // Sieben
		OVIS(0x01CE), // Acht
		SIMIA(0x01CF), // Neun
		GALLUS(0x01D0), // Zehn
		CANIS(0x01D1), // Elf
		PORCUS(0x01D2), // Zwolf
		ARION_FINAL(0x01D3), // 0x1D4 = DRAGON KNIGHT
		ISHTAR_FINAL(0x01D5),
		MENG(0x01D6),
		MAYBELL(0x01D7),
		BLEG(0x01D8), // 0x1D9 - 0x1DC = BELHALLA ARMY
		BARAN(0x01DD),
		CUTUZOV_FINAL(0x1DE), // Not sure why he's here again. 0x1DF = THIEF
		
		ENDGAME_MERCENARY_1(0x1A5),
		ENDGAME_MERCENARY_2(0x1A6),
		ENDGAME_MERCENARY_3(0x1A7),
		ENDGAME_MERCENARY_4(0x1A8),
		ENDGAME_MERCENARY_5(0x1A9),
		ENDGAME_MERCENARY_6(0x1AB),
		ENDGAME_MERCENARY_7(0x1AC),
		ENDGAME_EDDA_ARMY_1(0x1AE),
		ENDGAME_EDDA_ARMY_2(0x1AF),
		ENDGAME_EDDA_ARMY_3(0x1B0),
		ENDGAME_DARK_MAGE_1(0x1B3),
		ENDGAME_DARK_MAGE_2(0x1B4),
		ENDGAME_DOZEL_ARMY_1(0x1B6),
		ENDGAME_DOZEL_ARMY_2(0x1B7),
		ENDGAME_GRAU_RITTER_1(0x1B9),
		ENDGAME_GRAU_RITTER_2(0x1BA),
		ENDGAME_BEIGE_RITTER_1(0x1BD),
		ENDGAME_BEIGE_RITTER_2(0x1BE),
		ENDGAME_GELB_RITTER_1(0x1C0),
		ENDGAME_GELB_RITTER_2(0x1C1),
		ENDGAME_DARK_PRIEST_1(0x1C2),
		ENDGAME_DARK_MAGE_3(0x1C3),
		ENDGAME_DARK_MAGE_4(0x1C5),
		ENDGAME_ARION_SQUAD(0x1D4),
		ENDGAME_BELHALLA_ARMY_1(0x1D9),
		ENDGAME_BELHALLA_ARMY_2(0x1DA),
		ENDGAME_BELHALLA_ARMY_3(0x1DB),
		ENDGAME_BELHALLA_ARMY_4(0x1DC),
		ENDGAME_THIEF_1(0x1DF),
		
		// ARENA
		// CH1 (Shark is Melee only, Hood is Ranged only, Crotor and Emil must support both ranges.)
		ARENA_GAZACK(0x01E0), ARENA_CROTOR(0x01E1), ARENA_WISEMAN(0x01E2), ARENA_SHARK(0x01E3), ARENA_HOOD(0x01E4), ARENA_BACHUS(0x01E5), ARENA_EMIL(0x01E6), ARENA_DICE_CH1(0x01E7),
		// CH2 (Barknin and Keimos are Melee only, Millet and Marilyn are ranged only, Mahatma and Chacof must support both ranges.)
		ARENA_ZERO(0x01E8), ARENA_MAHATMA(0x01E9), ARENA_ROWIN(0x01EA), ARENA_BARKNIN(0x01EB), ARENA_MILLET(0x01EC), ARENA_HELTSOK(0x01ED), ARENA_CHACOF(0x01EE), ARENA_KEIMOS_CH2(0x01EF), ARENA_MARILYN_CH2(0x01F0),
		// CH3 (Tyler, Trevick, and Torton are Melee only, Rip, Geller, and Mario are ranged only, Duma must support both ranges.)
		ARENA_TYLER(0x01F1), ARENA_RIP_CH3(0x01F2), ARENA_PELIO(0x01F3), ARENA_TREVICK(0x01F4), ARENA_GELLER(0x01F5), ARENA_BAZAN(0x01F6), ARENA_DUMA(0x01F7), ARENA_KEHELA(0x01F8), ARENA_TORTON_CH3(0x01F9), ARENA_MARIO_CH3(0x01FA),
		// CH4 (Kaledin and Nikita are Melee only, Keith and Nene are Ranged only, Kemal, Graph, and Atlas must support both ranges.)
		ARENA_KEMAL(0x01FB), ARENA_KALEDIN(0x01FC), ARENA_KEITH(0x01FD), ARENA_SENGHOR(0x01FE), ARENA_NIKITA(0x01FF), ARENA_NENE(0x0200), ARENA_GRAPH(0x0201), ARENA_NIKIAS(0x0202), ARENA_ATLAS_CH4(0x0203),
		// CH5 (Shackal and Shisiel are Melee only, Rip and Hawks are Ranged only, Lee, Nazarl and Thief must support both ranges.)
		ARENA_SHACKAL(0x0204), ARENA_RIP_CH5(0x0205), ARENA_GREIAS(0x0206), ARENA_GEESE(0x0207), ARENA_LEE(0x0208), ARENA_SHISIEL(0x0209), ARENA_HAWKS(0x020A), ARENA_NAZARL(0x020B), ARENA_THIEF(0x020C),
		// CH7  (Manstein is melee only, Kashim is ranged only, Boshimas must support both ranges.)
		ARENA_TOLSTOY(0x020D), ARENA_BOSHIMAS(0x020E), ARENA_KRUGE(0x020F), ARENA_MANSTEIN(0x0210), ARENA_KASHIM(0x0211), ARENA_HEPNNER(0x0212), ARENA_RANDOCK(0x0213), ARENA_WOLF(0x0214),
		// CH8 (Louis and Xenon are Melee only, Heste and Jackson are Ranged only, Fate and Marda must support both ranges.)
		ARENA_DEWY(0x0215), ARENA_FATE(0x0216), ARENA_MALSHARK(0x0217), ARENA_LOUIS(0x0218), ARENA_HESTE(0x0219), ARENA_MARDA(0x021A), ARENA_JISMENT(0x021B), ARENA_XENON_CH8(0x021C), ARENA_JACKSON_CH8(0x021D),
		// CH9 (Indra and Miria are Melee only, Nietzche and Shiron are Ranged only, Slayton and Canaan must support both ranges.)
		ARENA_INDRA_CH9(0x021E), ARENA_NIETZCHE(0x021F), ARENA_APOSTOL(0x0220), ARENA_MIRIA(0x0221), ARENA_SHIRON(0x0222), ARENA_KLEIN(0x0223), ARENA_SLAYTON(0x0224), ARENA_TRIESTA(0x0225), ARENA_CANAAN_CH9(0x0226),
		// CH10 (Glanz and Riva are Melee only, Krosroy and Gloria are Ranged only, Hasmarn, Massigli, Note, and Indra must support both ranges.)
		ARENA_HASMARN(0x0227), ARENA_GLANZ(0x0228), ARENA_KROSROY(0x0229), ARENA_ROLAND(0x022A), ARENA_MASSIGLI(0x022B), ARENA_RIVA(0x022C), ARENA_GLORIA(0x022D), ARENA_NOTE(0x022E), ARENA_INDRA_CH10(0x022F),
		// FINAL (Keimos, Xenon, and Torton are Melee only, Marilyn, Jackson, and Mario are Ranged only, Canaan, Atlas, and Zeus must support both ranges.)
		ARENA_DICE_FINAL(0x0230), ARENA_KEIMOS_FINAL(0x0231), ARENA_MARILYN_FINAL(0x0232), ARENA_XENON_FINAL(0x0233), ARENA_JACKSON_FINAL(0x0234), ARENA_TORTON_FINAL(0x0235), ARENA_MARIO_FINAL(0x0236), ARENA_CANAAN_FINAL(0x0237), ARENA_ATLAS_FINAL(0x0238), ARENA_ZEUS(0x0239)
		
		// There's a bunch of duplicates beyond this point of major characters/bosses. I'm not listing them here unless we need them later.
		;
		
		
		private static final Set<Character> Gen1PlayableCharacters = new HashSet<Character>(Arrays.asList(SIGURD, NAOISE, ALEC, ARDEN, FINN_GEN_1, QUAN, MIDIR, LEWYN, CHULAINN, AZELLE,
				JAMKE, CLAUD, BEOWOLF, LEX, DEW, DEIRDRE, ETHLYN, LACHESIS, AYRA, ERINYS, TAILTIU, SILVIA, EDAIN, BRIGID));
		private static final Set<Character> Gen2StaticCharacters = new HashSet<Character>(Arrays.asList(SHANNAN, IUCHAR, FINN_GEN_2, HANNIBAL, ARES, OIFEY, IUCHARBA));
		private static final Set<Character> Gen2ChildCharacters = new HashSet<Character>(Arrays.asList(SELIPH, LEIF, JULIA, ALTENA, ULSTER, FEBAIL, COIRPRE, CED, DIARMUID, LESTER, ARTHUR, 
				PATTY, LARCEI, LANA, FEE, TINE, LENE, NANNA));
		private static final Set<Character> Gen2SubstituteCharacters = new HashSet<Character>(Arrays.asList(DALVIN, ASAELLO, CHARLOT, HAWK, TRISTAN, DEIMNE, AMID, DAISY, CREIDNE, MUIRNE,
				HERMINA, LINDA, LAYLEA, JEANNE));
		
		private static final Set<Character> Gen1Bosses = new HashSet<Character>(Arrays.asList(
				DIMAGGIO, GERRARD,
				CIMBAETH, MUNNIR, SANDIMA,
				ELLIOT_CH2, PHILLIP, BOLDOR, MACBETH, VOLTZ, CLEMENT, ZYNE, CHAGALL_CH2,
				JACOBAN, ELDIGAN_CH3, CHAGALL_CH3, PAPILION, PIZARL, DOBARL,
				CUVULI, DEETVAR, MAIOS, PAMELA, DONOVAN, LAMIA, DACCAR,
				SLAYDER, ANDOREY_CH5, LOMBARD, MAGORN, VAHA, REPTOR));
		
		private static final Set<Character> Gen2Bosses = new HashSet<Character>(Arrays.asList(
				HAROLD, SCHMIDT, DANANN,
				KUTUZOV, LIZA, ISHTORE, JAVARRO, BRAMSEL, VAMPA_CH7, FETRA_CH7, ELIU_CH7, BLOOM_CH7,
				MUHAMMAD, OVO, VAMPA_CH8, FETRA_CH8, ELIU_CH8, ISHTAR_CH8, BLOOM_CH8, CORUTA, MAIKOV,
				KANATZ, DISLER, TRAVANT_CH9, MUSAR, JUDAH, ARION_CH9,
				RIDALE, HILDA_CH10, MORRIGAN, ISHTAR_CH10, JULIUS_CH10, ZAGAM, ARVIS_CH10,
				ROBERT, BOYCE, RODAN, YUPHEEL, FISHER, BRIAN, DAGGON, SCIPIO, HILDA_FINAL, BARAN, MENG, BLEG, MAYBELL, ISHTAR_FINAL, ARION_FINAL, MANFROY, MUS, BOVIS, TIGRIS, LEPUS, DRACO, ANGUILLA, EQUUS, OVIS, SIMIA, GALLUS, CANIS, PORCUS, JULIUS_FINAL));

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
		
		public boolean isPlayable() {
			return Gen1PlayableCharacters.contains(this) || Gen2StaticCharacters.contains(this) || Gen2ChildCharacters.contains(this) || Gen2SubstituteCharacters.contains(this);
		}
		
		public boolean isGen1() {
			String name = this.toString();
			boolean isGen1Minion = name.startsWith("PROLOGUE") || name.startsWith("CH1") || name.startsWith("CH2") || name.startsWith("CH3") || name.startsWith("CH4") || name.startsWith("CH5");
			return Gen1PlayableCharacters.contains(this) || Gen1Bosses.contains(this) || isGen1Minion;
		}
		
		public boolean isGen2() {
			String name = this.toString();
			boolean isGen2Minion = name.startsWith("CH6") || name.startsWith("CH7") || name.startsWith("CH8") || name.startsWith("CH9") || name.startsWith("CH10") || name.startsWith("ENDGAME");
			return Gen2StaticCharacters.contains(this) || Gen2ChildCharacters.contains(this) || Gen2SubstituteCharacters.contains(this) || Gen2Bosses.contains(this) || isGen2Minion;
		}
		
		public boolean isStatic() {
			return Gen1PlayableCharacters.contains(this) || Gen2StaticCharacters.contains(this) || Gen2SubstituteCharacters.contains(this);
		}
		
		public boolean isChild() {
			return Gen2ChildCharacters.contains(this);
		}
		
		public boolean isSubstitute() {
			return Gen2SubstituteCharacters.contains(this);
		}
		
		public boolean isMinion() {
			String name = this.toString();
			return name.startsWith("PROLOGUE") || name.startsWith("CH1") || name.startsWith("CH2") || name.startsWith("CH3") || name.startsWith("CH4") || name.startsWith("CH5") ||
					name.startsWith("CH6") || name.startsWith("CH7") || name.startsWith("CH8") || name.startsWith("CH9") || name.startsWith("CH10") || name.startsWith("ENDGAME");
		}
		
		public boolean isArena() {
			String name = this.toString();
			return name.startsWith("ARENA");
		}
		
		public boolean isBoss() {
			return Gen1Bosses.contains(this) || Gen2Bosses.contains(this);
		}
	}

	public enum CharacterClass {
		NONE(0xFF),
		
		SOCIAL_KNIGHT(0x00), // Cavalier  
		LANCE_KNIGHT(0x01), // Lance Cav
		ARCH_KNIGHT(0x02), // Bow Cav 
		AXE_KNIGHT(0x03), // Axe Cav 
		FREE_KNIGHT(0x04), // Sword Cav 
		TROUBADOUR(0x05), // Staff Cav
		PEGASUS_KNIGHT(0x10),
		DRAGON_RIDER(0x12),
		DRAGON_KNIGHT(0x13), // Altena uses this class.
		BOW_FIGHTER(0x15), // Archer
		SWORD_FIGHTER(0x16), // Myrmidon
		AXE_ARMOR(0x23),
		BOW_ARMOR(0x24),
		SWORD_ARMOR(0x25),
		AXE_FIGHTER(0x27),
		JUNIOR_LORD(0x2C),
		PRINCE(0x2E),
		PRINCESS(0x2F),
		PRIEST(0x33),
		MAGE(0x34),
		FIRE_MAGE(0x35),
		THUNDER_MAGE(0x36),
		WIND_MAGE(0x37),
		BARD(0x3A),
		THIEF(0x3F),
		
		// Enemy Only
		BARBARIAN(0x26), 
		MOUNTAIN_THIEF(0x28),
		PIRATE(0x2B),
		HUNTER(0x2A),
		DARK_MAGE(0x3D),
		
		// Unused
		//PEGASUS_RIDER(0x0F),
		//SOLDIER(0x1D),
		//SPEAR_SOLDIER(0x1E),
		//AXE_SOLDIER(0x1F),
		//ARCHER(0x20),
		//SWORD_SOLDIER(0x21),
		//ARMOR(0x22),
		//SHAMAN(0x3C),
		
		// Promoted
		LORD_KNIGHT(0x06), 
		DUKE_KNIGHT(0x07), // Lance Pal. 
		MASTER_KNIGHT(0x08), // Almost-everything Pal.
		PALADIN(0x09), 
		PALADIN_F(0x0A), // Valkyrie
		BOW_KNIGHT(0x0B), // Bow Pal.
		FORREST_KNIGHT(0x0C), // Ranger
		MAGE_KNIGHT(0x0D),
		GREAT_KNIGHT(0x0E), // Axe Pal.
		FALCON_KNIGHT(0x11),
		DRAGON_MASTER(0x14),
		SWORD_MASTER(0x17), // Swordmaster_F
		SNIPER(0x18),
		FORREST(0x19), // Swordmaster (aka Hero)
		GENERAL(0x1A), // Sword, Lance, Axes, Bows
		WARRIOR(0x29),
		MAGE_FIGHTER(0x2D),
		MAGE_FIGHTER_F(0x30),
		HIGH_PRIEST(0x38),
		SAGE(0x3A),
		THIEF_FIGHTER(0x40),
		
		// Enemy Only
		EMPEROR(0x1B), // All types except light and dark magic.
		BARON(0x1C), // Enemy-only, same weaponry as emperor.
		QUEEN(0x31),
		BISHOP(0x39),
		DARK_BISHOP(0x3E),
		
		// Special
		DANCER(0x32),
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
	}
	
	public enum Item {
		NONE(0xFF),
		
		IRON_SWORD(0x00), STEEL_SWORD(0x01), SILVER_SWORD(0x02), IRON_BLADE(0x03), STEEL_BLADE(0x04), SILVER_BLADE(0x05), MIRACLE_SWORD(0x06),
		THIEF_SWORD(0x07), BARRIER_BLADE(0x08), BERSERK_SWORD(0x09), BRAVE_SWORD(0x0A), SILENCE_SWORD(0x0B), SLEEP_SWORD(0x0C), SLIM_SWORD(0x0D),
		SAFEGUARD(0x0E), FLAME_SWORD(0x0F), EARTH_SWORD(0x10), LEVIN_SWORD(0x11), WIND_SWORD(0x12), LIGHT_BRAND(0x13), MYSTLETAINN(0x14), TYRFING(0x15),
		BALMUNG(0x16), ARMORSLAYER(0x17), WING_CLIPPER(0x18), BROKEN_SWORD_A(0x19), BROKEN_SWORD_B(0x1A), BROKEN_SWORD_C(0x1B), 
		
		IRON_LANCE(0x1C), STEEL_LANCE(0x1D), SILVER_LANCE(0x1E), JAVELIN(0x1F), HORSESLAYER(0x20), BRAVE_LANCE(0x21), SLIM_LANCE(0x22),
		GUNGNIR(0x23), GAE_BOLG(0x24), BROKEN_LANCE_A(0x25), BROKEN_LANCE_B(0x26), BROKEN_LANCE_C(0x27),
		
		IRON_AXE(0x28), STEEL_AXE(0x29), SILVER_AXE(0x2A), BRAVE_AXE(0x2B), HELSWATH(0x2C), HAND_AXE(0x2D), BROKEN_AXE_A(0x2E), BROKEN_AXE_B(0x2F), BROKEN_AXE_C(0x30),
		
		IRON_BOW(0x31), STEEL_BOW(0x32), SILVER_BOW(0x33), BRAVE_BOW(0x34), KILLER_BOW(0x35), YEWFELLE(0x36), BROKEN_BOW_A(0x37), BROKEN_BOW_B(0x38), BROEKN_BOW_C(0x39),
		
		// Ballistas here, which we don't care about.
		
		FIRE(0x3E), ELFIRE(0x3F), BOLGANONE(0x40), VALFLAME(0x41), METEOR(0x42),
		
		THUNDER(0x43), ELTHUNDER(0x44), THORON(0x45), MJOLNIR(0x46), BOLTING(0x47),
		
		WIND(0x48), ELWIND(0x49), TORNADO(0x4A), FORSETI(0x4B), BLIZZARD(0x4C),
		
		LIGHT(0x4D), NOSFERATU(0x4E), AURA(0x4F), NAGA(0x50),
		
		YOTSMUNGAND(0x51), FENRIR(0x52), HEL(0x53), LOPTYR(0x54),
		
		EMPTY_BOOK_A_1(0x55), EMPTY_BOOK_B_1(0x56), EMPTY_BOOK_C_1(0x57), // No idea which kind of tome this is...
		
		HEAL(0x58), MEND(0x59), RECOVER(0x5A), PHYSIC(0x5B), FORTIFY(0x5C), RETURN(0x5D), WARP(0x5E), RESCUE(0x5F), RESTORE(0x61), VALKYRIE(0x62), SILENCE(0x63), SLEEP(0x64), BERSERK(0x65),
		BROKEN_STAFF_A(0x67), BROKEN_STAFF_B(0x68), BROKEN_STAFF_C(0x69),
		
		LIFE_RING(0x6A), ELITE_RING(0x6B), THIEF_RING(0x6C), PRAYER_RING(0x6D), PURSUIT_RING(0x6E), RECOVER_RING(0x6F),
		BARGAIN_RING(0x70), KNIGHT_RING(0x71), RETURN_RING(0x72), SPEED_RING(0x73), MAGIC_RING(0x74), POWER_RING(0x75),
		SHIELD_RING(0x76), BARRIER_RING(0x77), LEG_RING(0x78), SKILL_RING(0x79),
		
		BROKEN_HOLY_SWORD(0x7A), BROKEN_HOLY_SPEAR(0x7B), BROKEN_HOLY_BOW(0x7C),
		
		EMPTY_BOOK_A_2(0x7D), EMPTY_BOOK_B_2(0x7E), EMPTY_BOOK_C_2(0x7F),
		EMPTY_BOOK_A_3(0x80), EMPTY_BOOK_B_3(0x81), EMPTY_BOOK_C_3(0x82),
		EMPTY_BOOK_A_4(0x83), EMPTY_BOOK_B_4(0x84), EMPTY_BOOK_C_4(0x85),
		EMPTY_BOOK_A_5(0x86), EMPTY_BOOK_B_5(0x87),
		
		BROKEN_STAFF(0x88),
		
		CIRCLET(0x89)
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
	}
	
	public enum SkillSlot1 {
		WRATH(0x1), PURSUIT(0x2), ADEPT(0x4),
		CHARM(0x10), NIHIL(0x40), MIRACLE(0x80);
		
		public int mask;
		
		private SkillSlot1(final int mask) { this.mask = mask; }
		
		public static List<SkillSlot1> slot1Skills(int skillSlot1Value) {
			List<SkillSlot1> skills = new ArrayList<SkillSlot1>();
			for (SkillSlot1 skill : SkillSlot1.values()) {
				if ((skillSlot1Value & skill.mask) != 0) {
					skills.add(skill);
				}
			}
			
			return skills;
		}
		
		public static byte valueForSlot1Skills(List<SkillSlot1> skills) {
			byte result = 0;
			if (skills == null) { return 0; }
			for (SkillSlot1 skill : skills) {
				result |= (skill.mask & 0xFF);
			}
			
			return result;
		}
	}
	
	public enum SkillSlot2 {
		CRITICAL(0x1), VANTAGE(0x2), CHARGE(0x4), ASTRA(0x8),
		LUNA(0x10), SOL(0x20);
		
		public int mask;
		
		private SkillSlot2(final int mask) { this.mask = mask; }
		
		public static List<SkillSlot2> slot2Skills(int skillSlot2Value) {
			List<SkillSlot2> skills = new ArrayList<SkillSlot2>();
			for(SkillSlot2 skill : SkillSlot2.values()) {
				if ((skillSlot2Value & skill.mask) != 0) {
					skills.add(skill);
				}
			}
			
			return skills;
		}
		
		public static byte valueForSlot2Skills(List<SkillSlot2> skills) {
			byte result = 0;
			if (skills == null) { return 0; }
			for (SkillSlot2 skill : skills) {
				result |= (skill.mask & 0xFF);
			}
			
			return result;
		}
	}
	
	public enum SkillSlot3 {
		RENEWAL(0x1), PARAGON(0x2),
		BARGAIN(0x10);
		
		public int mask;
		
		private SkillSlot3(final int mask) { this.mask = mask; }
		
		public static List<SkillSlot3> slot3Skills(int skillSlot3Value) {
			List<SkillSlot3> skills = new ArrayList<SkillSlot3>();
			for(SkillSlot3 skill : SkillSlot3.values()) {
				if ((skillSlot3Value & skill.mask) != 0) {
					skills.add(skill);
				}
			}
			
			return skills;
		}
		
		public static byte valueForSlot3Skills(List<SkillSlot3> skills) {
			byte result = 0;
			if (skills == null) { return 0; }
			for (SkillSlot3 skill : skills) {
				result |= (skill.mask & 0xFF);
			}
			
			return result;
		}
	}
	
	public enum HolyBloodSlot1 {
		MINOR_BALDR(0x1), MAJOR_BALDR(0x2), 
		MINOR_NAGA(0x4), MAJOR_NAGA(0x8),
		MINOR_DAIN(0x10), MAJOR_DAIN(0x20),
		MINOR_NJORUN(0x40), MAJOR_NJORUN(0x80);
		
		public int mask;
		
		private HolyBloodSlot1(final int mask) { this.mask = mask; }
		
		public static List<HolyBloodSlot1> slot1HolyBlood(int holyBloodSlot1Value) {
			List<HolyBloodSlot1> blood = new ArrayList<HolyBloodSlot1>();
			for(HolyBloodSlot1 holyBlood : HolyBloodSlot1.values()) {
				if ((holyBloodSlot1Value & holyBlood.mask) != 0) {
					blood.add(holyBlood);
				}
			}
			
			return blood;
		}
		
		public static byte valueForSlot1HolyBlood(List<HolyBloodSlot1> blood) {
			byte result = 0;
			if (blood == null) { return 0; }
			for (HolyBloodSlot1 holyBlood : blood) {
				result |= (holyBlood.mask & 0xFF);
			}
			
			return result;
		}
	}
	
	public enum HolyBloodSlot2 {
		MINOR_OD(0x1), MAJOR_OD(0x2), 
		MINOR_ULIR(0x4), MAJOR_ULIR(0x8),
		MINOR_NEIR(0x10), MAJOR_NEIR(0x20),
		MINOR_FJALAR(0x40), MAJOR_FJALAR(0x80);
		
		public int mask;
		
		private HolyBloodSlot2(final int mask) { this.mask = mask; }
		
		public static List<HolyBloodSlot2> slot2HolyBlood(int holyBloodSlot2Value) {
			List<HolyBloodSlot2> blood = new ArrayList<HolyBloodSlot2>();
			for(HolyBloodSlot2 holyBlood : HolyBloodSlot2.values()) {
				if ((holyBloodSlot2Value & holyBlood.mask) != 0) {
					blood.add(holyBlood);
				}
			}
			
			return blood;
		}
		
		public static byte valueForSlot2HolyBlood(List<HolyBloodSlot2> blood) {
			byte result = 0;
			if (blood == null) { return 0; }
			for (HolyBloodSlot2 holyBlood : blood) {
				result |= (holyBlood.mask & 0xFF);
			}
			
			return result;
		}
	}
	
	public enum HolyBloodSlot3 {
		MINOR_THRUD(0x1), MAJOR_THRUD(0x2), 
		MINOR_FORSETI(0x4), MAJOR_FORSETI(0x8),
		MINOR_BRAGI(0x10), MAJOR_BRAGI(0x20),
		MINOR_HEZUL(0x40), MAJOR_HEZUL(0x80);
		
		public int mask;
		
		private HolyBloodSlot3(final int mask) { this.mask = mask; }
		
		public static List<HolyBloodSlot3> slot3HolyBlood(int holyBloodSlot3Value) {
			List<HolyBloodSlot3> blood = new ArrayList<HolyBloodSlot3>();
			for(HolyBloodSlot3 holyBlood : HolyBloodSlot3.values()) {
				if ((holyBloodSlot3Value & holyBlood.mask) != 0) {
					blood.add(holyBlood);
				}
			}
			
			return blood;
		}
		
		public static byte valueForSlot3HolyBlood(List<HolyBloodSlot3> blood) {
			byte result = 0;
			if (blood == null) { return 0; }
			for (HolyBloodSlot3 holyBlood : blood) {
				result |= (holyBlood.mask & 0xFF);
			}
			
			return result;
		}
	}
	
	public enum HolyBloodSlot4 {
		MINOR_LOPTOUS(0x1), MAJOR_LOPTOUS(0x2);
		
		public int mask;
		
		private HolyBloodSlot4(final int mask) { this.mask = mask; }
		
		public static List<HolyBloodSlot4> slot4HolyBlood(int holyBloodSlot4Value) {
			List<HolyBloodSlot4> blood = new ArrayList<HolyBloodSlot4>();
			for(HolyBloodSlot4 holyBlood : HolyBloodSlot4.values()) {
				if ((holyBloodSlot4Value & holyBlood.mask) != 0) {
					blood.add(holyBlood);
				}
			}
			
			return blood;
		}
		
		public static byte valueForSlot4HolyBlood(List<HolyBloodSlot4> blood) {
			byte result = 0;
			if (blood == null) { return 0; }
			for (HolyBloodSlot4 holyBlood : blood) {
				result |= (holyBlood.mask & 0xFF);
			}
			
			return result;
		}
	}
	
	// Like the other offsets, these assume a headered file. Subtract 0x200 for the unheadered version.
	public enum EnemyTable {
		TABLE1(0x3BC07, 26),
		TABLE2(0x3BD7F, 68),
		TABLE3(0x3C119, 43),
		TABLE4(0x3C36E, 15),
		TABLE5(0x3C457, 12),
		TABLE6(0x3C53F, 5),
		TABLE7(0x3C5A6, 5),
		TABLE8(0x3C60D, 14),
		TABLE9(0x3C6E9, 12),
		TABLE10(0x3C7AB, 11),
		TABLE11(0x3C860, 2),
		TABLE12(0x3C8A0, 16),
		TABLE13(0x3C9E2, 14),
		TABLE14(0x3CABE, 2),
		TABLE15(0x3CAFE, 12),
		TABLE16(0x3CC0C, 12),
		TABLE17(0x3CCCE, 4),
		TABLE18(0x3CD28, 8),
		TABLE19(0x3CDB6, 4),
		TABLE20(0x3CE10, 8),
		TABLE21(0x3CE9E, 16),
		TABLE22(0x3CFE0, 19),
		TABLE23(0x3D0FD, 5),
		TABLE24(0x3D164, 13),
		TABLE25(0x3D259, 2),
		TABLE26(0x3D299, 2),
		TABLE27(0x3D2D9, 4),
		TABLE28_1(0x3D333, 1),
		TABLE28_2(0x3D554, 1),
		TABLE29(0x3D5F9, 97)
		;
		
		public long offset;
		public int count;
		
		private EnemyTable(final long offset, final int count) { this.offset = offset; this.count = count; }
	}
	
	public enum HolyEnemyTable { // Names based off of Nightmare modules.
		ISHTAR3_3_PEG_KNIGHTS(0x3D561, 4),
		THREE_SISTERS_1(0x3C970, 3),
		THREE_SISTERS_2(0x3CB9A, 3),
		TABLE11_1(0x3CD90, 1),
		TABLE11_2(0x3CDEA, 1),
		TABLE14_1(0x3D0D7, 1),
		TABLE14_2(0x3D13E, 1),
		ARVIS_TRAVANT_1(0x3C580, 1),
		ARVIS_TRAVANT_2(0x3C6C3, 1),
		BRIAN(0x3D20D, 2),
		DANANN_BLOOM_1(0x3C785, 1),
		DANANN_BLOOM_2(0x3C83A, 1),
		DEADLORDS_ARION2(0x3D366, 13),
		ELDIGAN_1(0x3BD59, 1),
		ELDIGAN_2(0x3C0F3, 1),
		HILDA(0x3CE78, 1),
		HILDA2_SCOPIO_1(0x3D273, 1),
		HILDA2_SCOPIO_2(0x3D2B3, 1),
		ISHTAR1_BLOOM2_1(0x3CA98, 1),
		ISHTAR1_BLOOM2_2(0x3CAD8, 1),
		ISHTORE(0x3C87A, 1),
		JULIUS1_ISHTAR2_ARVIS2(0x3CF6E, 3),
		LOMBARD_ANDOREY2_1(0x3C348, 1),
		LOMBARD_ANDOREY2_2(0x3C431, 1),
		MANFROY_JULIUS2_1(0x3D30D, 1),
		MANFROY_JULIUS2_2(0x3D340, 1),
		REPTOR(0x3C5E7, 1),
		TRAVANT2_ARION1_1(0x3CCA8, 1),
		TRAVANT2_ARION1_2(0x3CD02, 1),
		BYRON_ANDOREY(0x3C4F3, 2)
		;
		public long offset;
		public int count;
		
		private HolyEnemyTable(final long offset, final int count) { this.offset = offset; this.count = count; }
	}
}
