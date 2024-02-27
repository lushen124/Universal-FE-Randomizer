package fedata.snes.fe4;

import fedata.snes.fe4.FE4Data.Item.ItemType;
import fedata.snes.fe4.FE4Data.Item.WeaponRank;

import java.util.*;
import java.util.stream.Collectors;

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
	public static final int Gen2ChildrenCharacterTable1ItemSize = 12; // This should remain as 12. There's some important data after it that we don't want to overwrite.
	public static final int Gen2ChildrenCharacterTable1Spacing = 126; // For whatever reason, Nightmare says the object is "126 bytes" long, even though we only care about 12 of them.
	
	public static final long Gen2ChildrenCharacterTable2Offset = 0x3BA95;
	public static final int Gen2ChildrenCharacterTable2Count = 1; // Only Altena is here.
	public static final int Gen2ChildrenCharacterSize = 12; // Like normal.
	
	public static final long Gen2ChildrenCharacterTable3Offset = 0x3BB5F;
	public static final int Gen2ChildrenCharacterTable3Count = 14; // Everybody else. In order: Ulster, Febail, Coirpre, Ced, Diarmuid, Lester, Arthur, Patty, Larcei, Lana, Fee, Tine, Lynn, and Nanna.
	
	public static final long ClassTableOffset = 0x391C5L;
	public static final int ClassTableCount = 72;
	public static final int ClassTableItemSize = 31;
	
	// Promotions are separated out and are defined individually. Two characters of the same class could go to different promotions.
	public static final long PromotionTableOffset = 0x3853C;
	public static final int PromotionTableCount = 63;
	public static final int PromotionTableEntrySize = 1; // Each item is 1 byte (the class ID). The order is baked in, following the character IDs - 1 (because Sigurd is 1).
	
	// All player weapons are tracked by kills and usage, so they need special treatment here.
	// While weapon stats are located elsewhere, these define which item is in which "slot" so to speak.
	public static final long PlayerItemMappingTableOffset = 0x3F688;
	public static final int PlayerItemMappingTableCount = 145; // We only have 144 items to work with, technically. Don't use item 0.
	public static final int PlayerItemMappingTableItemSize = 1; // Each item is a byte, representing the item ID in that slot. The index is how the items are referred to afterwards.
	
	public static final int EnemyDataSize = 13;
	
	// Lex's hero axe event checks for an item to be in his inventory. We should try to match this to his starting equipment. Note this uses Item ID, not inventory ID.
	public static final long LexHeroAxeEventItemRequirementOffset = 0x108ED7L;
	public static final byte LexHeroAxeEventItemRequirementOldID = 0x28;
	
	// Since we enable all weapons to be inherited, we cannot let Seliph start with his, whatever it might be.
	public static final long SeliphHolyWeaponInheritenceBanOffset = 0x7AD56L;
	public static final byte SeliphHolyWeaponInheritenceBanOldID = 0x4B; // Was Forseti, which is normally allowed. Since we flipped the logic to allow all EXCEPT these, this becomes an exclusion list (uses item IDs).
	public static final long SeliphHolyWeaponInheritenceBanOffset2 = 0x7AD59L; // This byte also needs to be set appropriately.
	public static final byte SeliphHolyWeaponInheritenceBanOldValue = 0x18;
	public static final byte SeliphHolyWeaponInheritenceBanNewValue = 0x1E;
	// Same for Quan's but for a different reason, since Altena's weapon is hard coded on her, we don't want multiple copies flying around.
	public static final long QuanHolyWeaponInheritenceBanOffset = 0x7AD5BL;
	public static final byte QuanHolyWeaponInheritenceBanOldID = 0x62; // Was the Valkyrie Staff.
	public static final long QuanHolyWeaponInheritenceBanOffset2 = 0x7AD5EL;
	public static final byte QuanHolyWeaponInheritenceBanOldValue = 0x13;
	public static final byte QuanHolyWeaponInheritenceBanNewValue = 0x19;
	
	// Seliph also has hard-coded blood. Since we limited the parent's blood. We can calculate what his blood should be.
	public static final long SeliphHolyBloodByte1Offset = 0x4856DL;
	public static final long SeliphHolyBloodByte2Offset = 0x4856EL;
	public static final long SeliphHolyBloodByte3Offset = 0x48573L;
	// Byte 4 is right after Byte 3, but since we don't plan on assigning Loptous, we don't need it.
	
	// This changes the rank of the Circlet from 0xFF to 0xFE
	// Used in conjunction with the next change.
	public static final long ChangeCircletRankToFEOffset = 0x3F686L;
	public static final byte ChangeCircletRankToFENewValue = (byte)0xFE;
	public static final byte ChangeCircletRankToFEOldValue = (byte)0xFF;
	
	// This makes it so that items of rank 0xFE cannot be sold.
	// Holy weapons have rank 0xFF, and can continue to be sold.
	public static final long ChangeUnsellableRankToFEOffset = 0x4B4E4L;
	public static final byte ChangeUnsellableRankToFENewValue = (byte)0xFE;
	public static final byte ChangeUnsellableRankToFEOldValue = (byte)0xFF;
	
	// Old change to make holy weapons sellable. Has the undesirable side effect of making the Circlet also sellable.
	// Leaving here deprecated for reference.
	public static final long _deprecated_SellableHolyWeaponsOffset = 0x4B4E7L;
	public static final byte _deprecated_SellableHolyWeaponEnabledValue = 0x00;
	public static final byte _deprecated_SellableHolyWeaponsDisabledValue = 0x16;
	
	public static final long ItemTableOffset = 0x3ECE4L;
	public static final int ItemTableCount = 107;
	public static final int ItemSize = 19;
	
	// Emperor has a 1 byte mistake in its battle animation data that breaks for female emperors using staves. We can fix this and re-enable Emperor as a class.
	public static final long FemaleEmperorStaffAnimationFixOffset = 0x178E7CL;
	public static final byte FemaleEmperorStaffAnimationFixOldValue = 0x00;
	public static final byte FemaleEmperorStaffAnimationFixNewValue = 0x01;
	
	// Lord Knight's map sprite is special for Sigurd, but it doesn't need to be.
	// We can use enable Lord Knight's map sprite to be used for more than just Sigurd.
	public static final long KnightLordMapSpriteFixOffset = 0x3830CL;
	public static final byte KnightLordMapSpriteFixOldValue = 0x01;
	public static final byte KnightLordMapSpriteFixNewValue = 0x00;
	
	// Aura shenanigans.
	// 0x34 is a Steel Lance freely available in Ch. 8 that we can use
	// This allows us to swap it for Deirdre's Aura, which can side-step the duplicate item issue in gen 2.
	// This means Deirdre gets 0x34 randomized to her usage, which frees up 0x60 (what used to be Aura) to be unique to Julia.
	public static final int Chapter8ShopSteelLanceInventoryID = 0x34;
	public static final int DeirdreAuraInventoryID = 0x60;
	
	// Ethlyn's appearance in Ch. 5 gives her two items that aren't well documented: 0x2 (Iron Sword) and 0x67 (Mend).
	// 0x2 is isolated from the rest of the game, but is programmatically inherited by Leif.
	// 0x67 is the starting equipment for Nanna/Jeanne, and will be randomized by her, which might cause problems. Additionally, 0x67 is also found in the shop in Ch. 8. It's also possible for Leif to inherit this if he could.
	// The solution then is to give Nanna/Jeanne a new inventory item to split it up. 0x67 will still fall into the Ch. 8 shop, but Jeanne and Nanna won't be able to give Ethlyn a problematic weapon now.
	// This is deprecated, since we can simply unassign 0x67 from Ethlyn in the Yied Desert event.
	public static final int _deprecated_JeanneNannaOldStartingInventoryID = 0x67;
	public static final int _deprecated_JeanneNannaNewStartingInventoryID = 0x40;
	
	// Unassign Item 0x67 (Mend) from Ethlyn in Chapter 5. It's not that important, and it allows us to reclaim item 0x40 as unused.
	public static final long UnassignMendFromCh5EthlynOffset = 0x1DC4E2L;
	public static final byte[] UnassignMendFromCh5EthlynNewValues = new byte[] {0x0, 0x0, 0x0};
	public static final byte[] UnassignMendFromCh5EthlynOldValues = new byte[] {0x11, 0x0, 0x67};
	
	// We should also assign Leif 0x2 so that he can randomize that weapon. Ideally it's something both of them can use, but otherwise, defer to Leif.
	public static final int LeifEthlynSharedInventoryID = 0x02;
	
	// Remove 0x34 from the Ch. 8 shop.
	public static final long Chapter8ShopListOffset = 0x6F54CL;
	public static final byte[] Chapter8ShopOldListByteArray = new byte[] {0x08, 0x1C, 0x23, 0x29, 0x32, 0x33, 0x34, 0x44, 0x47, 0x4A, 0x50, 0x55, 0x58, 0x65, 0x72, 0x06, 0x30, 0x48, 0x5D, 0x64, 0x67};
	public static final byte[] Chapter8ShopNewListByteArray = new byte[] {0x08, 0x1C, 0x23, 0x29, 0x32, 0x33, 0x44, 0x47, 0x4A, 0x50, 0x55, 0x58, 0x65, 0x72, 0x06, 0x30, 0x48, 0x5D, 0x64, 0x67, (byte)0xFF};
	
	// Follow-up logic change to update the requirements necessary.
	// This stubs out the old logic with 0s and jumps to a new subroutine with the new logic.
	// The assumption here is that it jumps to ROM address 0x50110.
	public static final long OriginalFollowupLogicOffset = 0x4E567L;
	public static final byte[] OriginalFollowupLogicNewValues = new byte[] {0x20, 0x10, (byte)0xFF, 0x60, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	public static final byte[] OriginalFollowupLogicOldValues = new byte[] {(byte)0x90, 0x12, (byte)0xBD, 0x24, 0x00, (byte)0x89, 0x00, 0x40, (byte)0xF0, 0x0A, (byte)0xB9, 0x30, 0x00, (byte)0xDD, 0x30, 0x00, 0x10, 0x02, 0x38, 0x60, 0x18, 0x60};
	
	// This is the new follow-up logic, which allows doubling with and without Pursuit at two different AS thresholds.
	// Should be used in conjunction with the above change.
	public static final long NewFollowupLogicOffset = 0x50110L;
	public static final byte[] NewFollowupLogicValues = new byte[] {(byte)0x90, 0x2C, (byte)0xB9, 0x30, 0x00, (byte)0xDD, 0x30, 0x00, 0x10, 0x24, (byte)0xBD, 0x24, 0x00, (byte)0x89, 0x00, 0x40, (byte)0xF0, 0x0E, (byte)0xBD, 0x30, 0x00, 0x38, (byte)0xF9, 0x30, 0x00, (byte)0xC9, 0x03, 0x00, (byte)0x90, 0x10, (byte)0x80, 0x0C, (byte)0xBD, 0x30, 0x00, 0x38, (byte)0xF9, 0x30, 0x00, (byte)0xC9, 0x06, 0x00, (byte)0x90, 0x02, 0x38, 0x60, 0x18, 0x60};
	public static final byte[] NewFollowupLogicEmptySpace = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	
	// The above chunk assumes AS thresholds of 3 and 6 for with and without Pursuit, respectively.
	// These values can be changed independently.
	public static final long FollowupWithPursuitOffset = 0x5012AL;
	public static final long FollowupWithoutPursuitOffset = 0x50138L;
	
	// These don't seem to be used by anybody in the game. Free holy weapons? Or free staves!
	// The last two may or may not work. They're empty right now, but they may be serving as terminator bytes.
	public static final List<Integer> UnusedInventoryIDs = new ArrayList<Integer>(Arrays.asList(0x12, 0x40, 0x8D, 0x8E));
	
	public static class EventGift {
		public Character recipient;
		public Character donor;
		public int giftInventoryID;
		
		public EventGift(Character donor, Character recipient, int giftInventoryID) {
			this.recipient = recipient;
			this.donor = donor;
			this.giftInventoryID = giftInventoryID;
		}
	}
	
	public static final List<EventGift> EventGifts = createEventGiftList();
	private static List<EventGift> createEventGiftList() {
		List<EventGift> gifts = new ArrayList<EventGift>();
		gifts.add(new EventGift(Character.ARVIS_CH5, Character.SIGURD, 0x0A)); // Silver Sword
		gifts.add(new EventGift(Character.EDAIN, Character.ETHLYN, 0x6D)); // Return Staff
		gifts.add(new EventGift(Character.DEW, Character.EDAIN, 0x6E)); // Warp Staff
		gifts.add(new EventGift(Character.DEIRDRE, Character.ETHLYN, 0x25)); // Light Brand
		gifts.add(new EventGift(Character.DEW, Character.LACHESIS, 0x16)); // Thief Sword
		gifts.add(new EventGift(Character.QUAN, Character.FINN_GEN_1, 0x3B)); // Brave Lance
		gifts.add(new EventGift(Character.LEX, Character.AYRA, 0x19)); // Brave Sword
		gifts.add(new EventGift(Character.CHULAINN, Character.AYRA, 0x19)); // Brave Sword (Same as Lex)
		gifts.add(new EventGift(Character.ELDIGAN_CH3, Character.LACHESIS, 0x22)); // Earth Sword
		gifts.add(new EventGift(Character.ETHLYN, Character.QUAN, 0x3E)); // Gae Bolg
		gifts.add(new EventGift(Character.EDAIN, Character.BRIGID, 0x4F)); // Yewfelle
		gifts.add(new EventGift(Character.EDAIN, Character.MIDIR, 0x4D)); // Brave Bow
		gifts.add(new EventGift(Character.EDAIN, Character.JAMKE, 0x4D)); // Brave Bow (Same as Midir)
		gifts.add(new EventGift(Character.AZELLE, Character.EDAIN, 0x6F)); // Rescue Staff
		gifts.add(new EventGift(Character.BYRON, Character.SIGURD, 0x27)); // Tyrfing
		gifts.add(new EventGift(Character.LANA, Character.JULIA, 0x66)); // Mend Staff
		gifts.add(new EventGift(Character.MUIRNE, Character.JULIA, 0x66)); // Mend Staff (Same as Lana)
		gifts.add(new EventGift(Character.SELIPH, Character.JULIA, 0x60)); // Aura
		gifts.add(new EventGift(Character.SELIPH, Character.JULIA, 0x5F)); // Nosferatu
		gifts.add(new EventGift(Character.PATTY, Character.SHANNAN, 0x28)); // Balmung
		gifts.add(new EventGift(Character.DAISY, Character.SHANNAN, 0x28)); // Balmung (Same as Patty)
		gifts.add(new EventGift(Character.PATTY, Character.SELIPH, 0x1A)); // Hero Sword
		gifts.add(new EventGift(Character.DAISY, Character.SELIPH, 0x1A)); // Hero Sword (Same as Patty)
		gifts.add(new EventGift(Character.HANNIBAL, Character.CHARLOT, 0x74)); // Berserk Staff
		gifts.add(new EventGift(Character.PALMARK, Character.SELIPH, 0x27)); // Tyrfing (Same as for Sigurd)
		
		gifts.add(new EventGift(Character.NONE, Character.LEX, 0x45)); // Brave Axe
		gifts.add(new EventGift(Character.NONE, Character.ARDEN, 0x89)); // Pursuit Ring
		gifts.add(new EventGift(Character.NONE, Character.DEW, 0x24)); // Wind Sword
		gifts.add(new EventGift(Character.NONE, Character.SILVIA, 0x20)); // Defender
		gifts.add(new EventGift(Character.NONE, Character.LEWYN, 0x5C)); // Foresti
		gifts.add(new EventGift(Character.NONE, Character.LAYLEA, 0x17)); // Barrier Sword
		gifts.add(new EventGift(Character.NONE, Character.SELIPH, 0x84)); // Life Ring
		gifts.add(new EventGift(Character.NONE, Character.JULIA, 0x61)); // Naga
		
		gifts.add(new EventGift(Character.ALVA, Character.LACHESIS, 0x88)); // Knight Ring
		return gifts;
	}
	
	public static class ShopItem {
		public int itemInventoryID;
		public int chapter;
		
		public ShopItem(int itemInventoryID, int chapter) {
			this.itemInventoryID = itemInventoryID;
			this.chapter = chapter;
		}
	}
	
	public static final List<ShopItem> ShopItems = createShopItemList();
	private static List<ShopItem> createShopItemList() {
		List<ShopItem> shopItems = new ArrayList<ShopItem>();
		// Ch. 1
		shopItems.add(new ShopItem(0x1D, 1));
		shopItems.add(new ShopItem(0x2F, 1));
		shopItems.add(new ShopItem(0x38, 1));
		shopItems.add(new ShopItem(0x41, 1));
		// Ch. 2
		shopItems.add(new ShopItem(0x33, 2));
		shopItems.add(new ShopItem(0x55, 2));
		shopItems.add(new ShopItem(0x10, 2));
		shopItems.add(new ShopItem(0x4A, 2));
		// Ch. 3
		shopItems.add(new ShopItem(0x0B, 3));
		shopItems.add(new ShopItem(0x36, 3));
		shopItems.add(new ShopItem(0x44, 3));
		shopItems.add(new ShopItem(0x4B, 3));
		shopItems.add(new ShopItem(0x52, 3));
		// Ch. 4
		shopItems.add(new ShopItem(0x58, 4));
		shopItems.add(new ShopItem(0x68, 4));
		shopItems.add(new ShopItem(0x83, 4));
		// Ch. 6
		shopItems.add(new ShopItem(0x01, 6));
		shopItems.add(new ShopItem(0x03, 6));
		shopItems.add(new ShopItem(0x04, 6));
		shopItems.add(new ShopItem(0x0A, 6));
		shopItems.add(new ShopItem(0x0C, 6));
		shopItems.add(new ShopItem(0x25, 6));
		// Ch. 7
		shopItems.add(new ShopItem(0x07, 7));
		shopItems.add(new ShopItem(0x10, 7));
		shopItems.add(new ShopItem(0x1D, 7));
		shopItems.add(new ShopItem(0x21, 7));
		shopItems.add(new ShopItem(0x2D, 7));
		shopItems.add(new ShopItem(0x2E, 7));
		shopItems.add(new ShopItem(0x2F, 7));
		shopItems.add(new ShopItem(0x3C, 7));
		shopItems.add(new ShopItem(0x3F, 7));
		shopItems.add(new ShopItem(0x41, 7));
		shopItems.add(new ShopItem(0x46, 7));
		shopItems.add(new ShopItem(0x49, 7));
		shopItems.add(new ShopItem(0x51, 7));
		shopItems.add(new ShopItem(0x54, 7));
		shopItems.add(new ShopItem(0x62, 7));
		shopItems.add(new ShopItem(0x63, 7));
		shopItems.add(new ShopItem(0x69, 7));
		shopItems.add(new ShopItem(0x6D, 7));
		shopItems.add(new ShopItem(0x05, 7));
		shopItems.add(new ShopItem(0x0E, 7));
		shopItems.add(new ShopItem(0x0F, 7));
		shopItems.add(new ShopItem(0x1F, 7));
		shopItems.add(new ShopItem(0x3D, 7));
		// Ch. 8
		shopItems.add(new ShopItem(0x08, 8));
		shopItems.add(new ShopItem(0x1C, 8));
		shopItems.add(new ShopItem(0x23, 8));
		shopItems.add(new ShopItem(0x29, 8));
		shopItems.add(new ShopItem(0x32, 8));
		shopItems.add(new ShopItem(0x33, 8));
		shopItems.add(new ShopItem(0x34, 8));
		shopItems.add(new ShopItem(0x44, 8));
		shopItems.add(new ShopItem(0x47, 8));
		shopItems.add(new ShopItem(0x4A, 8));
		shopItems.add(new ShopItem(0x50, 8));
		shopItems.add(new ShopItem(0x55, 8));
		shopItems.add(new ShopItem(0x58, 8));
		shopItems.add(new ShopItem(0x65, 8));
		shopItems.add(new ShopItem(0x72, 8));
		shopItems.add(new ShopItem(0x06, 8));
		shopItems.add(new ShopItem(0x30, 8));
		shopItems.add(new ShopItem(0x48, 8));
		shopItems.add(new ShopItem(0x5D, 8));
		shopItems.add(new ShopItem(0x64, 8));
		shopItems.add(new ShopItem(0x67, 8));
		// Ch. 9
		shopItems.add(new ShopItem(0x09, 9));
		shopItems.add(new ShopItem(0x0B, 9));
		shopItems.add(new ShopItem(0x1E, 9));
		shopItems.add(new ShopItem(0x22, 9));
		shopItems.add(new ShopItem(0x2B, 9));
		shopItems.add(new ShopItem(0x35, 9));
		shopItems.add(new ShopItem(0x36, 9));
		shopItems.add(new ShopItem(0x37, 9));
		shopItems.add(new ShopItem(0x38, 9));
		shopItems.add(new ShopItem(0x4B, 9));
		shopItems.add(new ShopItem(0x52, 9));
		shopItems.add(new ShopItem(0x5A, 9));
		shopItems.add(new ShopItem(0x6E, 9));
		shopItems.add(new ShopItem(0x70, 9));
		shopItems.add(new ShopItem(0x59, 9));
		// CH. 10
		shopItems.add(new ShopItem(0x0D, 10));
		shopItems.add(new ShopItem(0x24, 10));
		shopItems.add(new ShopItem(0x68, 10));
		shopItems.add(new ShopItem(0x6A, 10));
		shopItems.add(new ShopItem(0x6F, 10));
		shopItems.add(new ShopItem(0x73, 10));
		shopItems.add(new ShopItem(0x6B, 10));
		
		return shopItems;
	}
	
	public static class VillageGift {
		public int giftInventoryID;
		public int chapter;
		
		public VillageGift(int giftInventoryID, int chapter) {
			this.giftInventoryID = giftInventoryID;
			this.chapter = chapter;
		}
	}
	
	public static final List<VillageGift> VillageGifts = createVillageGiftList();
	private static List<VillageGift> createVillageGiftList() {
		List<VillageGift> gifts = new ArrayList<VillageGift>();
		gifts.add(new VillageGift(0x29, 2)); // Armorslayer
		gifts.add(new VillageGift(0x2B, 3)); // Wing Clipper
		gifts.add(new VillageGift(0x70, 3)); // Restore Staff
		gifts.add(new VillageGift(0x7B, 0)); // Speed Ring
		gifts.add(new VillageGift(0x76, 8)); // Power Ring
		gifts.add(new VillageGift(0x78, 10)); // Magic Ring
		gifts.add(new VillageGift(0x7A, 6)); // Skill Ring
		gifts.add(new VillageGift(0x7C, 7)); // Speed Ring
		gifts.add(new VillageGift(0x7E, 7)); // Shield Ring
		gifts.add(new VillageGift(0x80, 9)); // Barrier Ring
		gifts.add(new VillageGift(0x8B, 8)); // Thief Ring
		return gifts;
	}
	
	public static final Map<Character, List<Integer>> EventItemInventoryIDsByRecipient = createEventItemMap();
	private static Map<Character, List<Integer>> createEventItemMap() {
		Map<Character, List<Integer>> map = new HashMap<Character, List<Integer>>();
		map.put(Character.LACHESIS, new ArrayList<Integer>(Arrays.asList(0x16, 0x22))); // Thief Sword, Earth Sword
		map.put(Character.ETHLYN, new ArrayList<Integer>(Arrays.asList(0x25, 0x6D))); // Light Brand, Return Staff
		map.put(Character.AYRA, new ArrayList<Integer>(Arrays.asList(0x19))); // Brave Sword (Technically needs to work for Coruta (boss) as well...)
		map.put(Character.SILVIA, new ArrayList<Integer>(Arrays.asList(0x20))); // Defender
		map.put(Character.DEW, new ArrayList<Integer>(Arrays.asList(0x24))); // Wind Sword
		map.put(Character.SIGURD, new ArrayList<Integer>(Arrays.asList(0x0A))); // Silver Sword
		/*map.put(Character.SIGURD, 0x27); // Tyrfing*/
		map.put(Character.FINN_GEN_1, new ArrayList<Integer>(Arrays.asList(0x3B))); // Brave Lance
		/*map.put(Character.QUAN, 0x3E); // Gae Bolg*/
		map.put(Character.LEX, new ArrayList<Integer>(Arrays.asList(0x45))); // Brave Axe (Needs to work with Schmidt (boss) too...)
		map.put(Character.MIDIR, new ArrayList<Integer>(Arrays.asList(0x4D))); // Brave Bow (Technically needs to work for Jamke too...)
		/*map.put(Character.BRIGID, 0x4F); // Yewfelle*/
		/*map.put(Character.LEWYN, 0x5C); // Forseti*/
		map.put(Character.EDAIN, new ArrayList<Integer>(Arrays.asList(0x6E, 0x6F))); // Warp Staff, Rescue Staff
		map.put(Character.LAYLEA, new ArrayList<Integer>(Arrays.asList(0x17))); // Barrier Sword
		map.put(Character.SELIPH, new ArrayList<Integer>(Arrays.asList(0x1A))); // Hero Sword
		/*map.put(Character.SHANNAN, 0x28); // Balmung*/
		map.put(Character.JULIA, new ArrayList<Integer>(Arrays.asList(0x5E, 0x5F, 0x60, 0x66))); // Lightning, Nosferatu, Mend Staff
		// With the change to give Deirdre 0x34 instead (a Chapter 8 Shop Steel Lance), this frees up 0x60 for Julia.
		/*map.put(Character.JULIA, 0x61); // Naga*/
		map.put(Character.CHARLOT, new ArrayList<Integer>(Arrays.asList(0x74))); // Berserk Staff
		return map;
	}
	
	// These characters need to at least share one weapon type if their class is randomized.
	public static final Map<Character, Character> WeaklyLinkedCharacters = createWeakLinkMap();
	private static Map<Character, Character> createWeakLinkMap() {
		Map<Character, Character> map = new HashMap<Character, Character>();
		// Reflective relationships makes this easier.
		
		// Brave Sword
		map.put(Character.AYRA, Character.CORUTA);
		map.put(Character.CORUTA, Character.AYRA);
		
		// Brave Bow
		map.put(Character.MIDIR, Character.JAMKE);
		map.put(Character.JAMKE, Character.MIDIR);
		// This is a triplet. Jamke and Midir need to use the same weapons, but if they don't pass down their weapon, it shows up on a minion.
		map.put(Character.CH8_CONOTE_COMMANDER_2, Character.MIDIR);
		// Killer Bow
		map.put(Character.MAIKOV, Character.JAMKE);
		
		// Brave Axe
		map.put(Character.LEX, Character.SCHMIDT);
		map.put(Character.SCHMIDT, Character.LEX);
		
		// Miracle Sword
		map.put(Character.LACHESIS, Character.CH8_THRACIA_COMMANDER_1);
		map.put(Character.CH8_THRACIA_COMMANDER_1, Character.LACHESIS);
		// Thief Sword
		map.put(Character.DANANN, Character.LACHESIS);
		
		// Fortify staff
		map.put(Character.CLAUD, Character.CH9_KAPATHOGIA_ARMY_1);
		map.put(Character.CH9_KAPATHOGIA_ARMY_1, Character.CLAUD);
		
		// Silver Blade
		map.put(Character.CHAGALL_CH3, Character.BLOOM_CH7);
		map.put(Character.BLOOM_CH7, Character.CHAGALL_CH3);
		
		// Horseslayer
		map.put(Character.ZYNE, Character.BRAMSEL);
		map.put(Character.BRAMSEL, Character.ZYNE);
		
		// Brave Lance
		map.put(Character.MUHAMMAD, Character.FINN_GEN_1);
		map.put(Character.FINN_GEN_1, Character.MUHAMMAD);
		
		// Thoron
		map.put(Character.ISHTORE, Character.TAILTIU);
		map.put(Character.TAILTIU, Character.ISHTORE);
		
		return map;
	}
	
	public static final Map<Item, Integer> HolyWeaponInventoryIDs = createHolyWeaponMap();
	private static Map<Item, Integer> createHolyWeaponMap() {
		Map<Item, Integer> map = new HashMap<Item, Integer>();
		map.put(Item.TYRFING, 0x27); // Tyrfing
		map.put(Item.GAE_BOLG, 0x3E); // Gae Bolg
		map.put(Item.YEWFELLE, 0x4F); // Yewfelle
		map.put(Item.FORSETI, 0x5C); // Forseti
		
		map.put(Item.BALMUNG, 0x28); // Balmung
		map.put(Item.NAGA, 0x61); // Naga
		return map;
	}
	
	public static final long HolyBloodDataOffset = 0x38BE3L;
	public static final int HolyBloodDataCount = 13;
	public static final int HolyBloodDataSize = 16;
	
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
		CH1_GENOA_COMMANDER(0x5C),
		
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
		KUTUZOV_TURN_12(0x1DE), // This boss reappears after turn 12 with a new inventory, which the game treats as a completely new character.
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
		ARENA_TOLSTOY(0x020D), ARENA_BOSHIMAS(0x020E), ARENA_KRUGE(0x020F), ARENA_MANSTEIN(0x0210), ARENA_KASHIM(0x0211), ARENA_HEPPNER(0x0212), ARENA_RANDOCK(0x0213), ARENA_WOLF(0x0214),
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
		
		public int arenaChapter() {
			switch (this) {
			case ARENA_GAZACK: case ARENA_CROTOR: case ARENA_WISEMAN: case ARENA_SHARK:
			case ARENA_HOOD: case ARENA_BACHUS: case ARENA_EMIL: case ARENA_DICE_CH1:
				return 1;
			case ARENA_ZERO: case ARENA_MAHATMA: case ARENA_ROWIN: case ARENA_BARKNIN: case ARENA_MILLET:
			case ARENA_HELTSOK: case ARENA_CHACOF: case ARENA_KEIMOS_CH2: case ARENA_MARILYN_CH2:
				return 2;
			case ARENA_TYLER: case ARENA_RIP_CH3: case ARENA_PELIO: case ARENA_TREVICK: case ARENA_GELLER:
			case ARENA_BAZAN: case ARENA_DUMA: case ARENA_KEHELA: case ARENA_TORTON_CH3: case ARENA_MARIO_CH3:
				return 3;
			case ARENA_KEMAL: case ARENA_KALEDIN: case ARENA_KEITH: case ARENA_SENGHOR: case ARENA_NIKITA:
			case ARENA_NENE: case ARENA_GRAPH: case ARENA_NIKIAS: case ARENA_ATLAS_CH4:
				return 4;
			case ARENA_SHACKAL: case ARENA_RIP_CH5: case ARENA_GREIAS: case ARENA_GEESE: case ARENA_LEE:
			case ARENA_SHISIEL: case ARENA_HAWKS: case ARENA_NAZARL: case ARENA_THIEF:
				return 5;
			case ARENA_TOLSTOY: case ARENA_BOSHIMAS: case ARENA_KRUGE: case ARENA_MANSTEIN: 
			case ARENA_KASHIM: case ARENA_HEPPNER: case ARENA_RANDOCK: case ARENA_WOLF:
				return 7;
			case ARENA_DEWY: case ARENA_FATE: case ARENA_MALSHARK: case ARENA_LOUIS: case ARENA_HESTE:
			case ARENA_MARDA: case ARENA_JISMENT: case ARENA_XENON_CH8: case ARENA_JACKSON_CH8:
				return 8;
			case ARENA_INDRA_CH9: case ARENA_NIETZCHE: case ARENA_APOSTOL: case ARENA_MIRIA: case ARENA_SHIRON:
			case ARENA_KLEIN: case ARENA_SLAYTON: case ARENA_TRIESTA: case ARENA_CANAAN_CH9:
				return 9;
			case ARENA_HASMARN: case ARENA_GLANZ: case ARENA_KROSROY: case ARENA_ROLAND: case ARENA_MASSIGLI:
			case ARENA_RIVA: case ARENA_GLORIA: case ARENA_NOTE: case ARENA_INDRA_CH10:
				return 10;
			case ARENA_DICE_FINAL: case ARENA_KEIMOS_FINAL: case ARENA_MARILYN_FINAL: case ARENA_XENON_FINAL:
			case ARENA_JACKSON_FINAL: case ARENA_TORTON_FINAL: case ARENA_MARIO_FINAL:case ARENA_CANAAN_FINAL:
			case ARENA_ATLAS_FINAL: case ARENA_ZEUS:
				return 11;
			default: return 0;
			}
		}
		
		public int arenaLevel() {
			switch (this) {
			case ARENA_GAZACK: case ARENA_ZERO: case ARENA_TYLER: case ARENA_RIP_CH3: case ARENA_KEMAL: case ARENA_SHACKAL: case ARENA_RIP_CH5:
			case ARENA_TOLSTOY: case ARENA_DEWY: case ARENA_INDRA_CH9: case ARENA_NIETZCHE: case ARENA_HASMARN: case ARENA_DICE_FINAL:
				return 1;
			case ARENA_CROTOR: case ARENA_MAHATMA: case ARENA_PELIO: case ARENA_KALEDIN: case ARENA_KEITH: case ARENA_GREIAS:
			case ARENA_BOSHIMAS: case ARENA_FATE: case ARENA_APOSTOL: case ARENA_GLANZ: case ARENA_KROSROY: case ARENA_KEIMOS_FINAL: case ARENA_MARILYN_FINAL:
				return 2;
			case ARENA_WISEMAN: case ARENA_ROWIN: case ARENA_TREVICK: case ARENA_GELLER: case ARENA_SENGHOR: case ARENA_GEESE:
			case ARENA_KRUGE: case ARENA_MALSHARK: case ARENA_MIRIA: case ARENA_SHIRON: case ARENA_ROLAND: case ARENA_XENON_FINAL: case ARENA_JACKSON_FINAL:
				return 3;
			case ARENA_SHARK: case ARENA_HOOD: case ARENA_BARKNIN: case ARENA_MILLET: case ARENA_BAZAN: case ARENA_NIKITA: case ARENA_NENE: case ARENA_LEE:
			case ARENA_MANSTEIN: case ARENA_KASHIM: case ARENA_LOUIS: case ARENA_HESTE: case ARENA_KLEIN: case ARENA_MASSIGLI: case ARENA_TORTON_FINAL: case ARENA_MARIO_FINAL:
				return 4;
			case ARENA_BACHUS: case ARENA_HELTSOK: case ARENA_DUMA: case ARENA_GRAPH: case ARENA_SHISIEL: case ARENA_HAWKS:
			case ARENA_HEPPNER: case ARENA_MARDA: case ARENA_SLAYTON: case ARENA_RIVA: case ARENA_GLORIA: case ARENA_CANAAN_FINAL:
				return 5;
			case ARENA_EMIL: case ARENA_CHACOF: case ARENA_KEHELA: case ARENA_NIKIAS: case ARENA_NAZARL:
			case ARENA_RANDOCK: case ARENA_JISMENT: case ARENA_TRIESTA: case ARENA_NOTE: case ARENA_ATLAS_FINAL:
				return 6;
			case ARENA_DICE_CH1: case ARENA_KEIMOS_CH2: case ARENA_MARILYN_CH2: case ARENA_TORTON_CH3: case ARENA_MARIO_CH3: case ARENA_ATLAS_CH4: case ARENA_THIEF:
			case ARENA_WOLF: case ARENA_XENON_CH8: case ARENA_JACKSON_CH8: case ARENA_CANAAN_CH9: case ARENA_INDRA_CH10: case ARENA_ZEUS:
				return 7;
			default: return 0;
			}
		}
		
		public static final Set<Character> RangedOnlyArenaCharacters = new HashSet<Character>(Arrays.asList(
				ARENA_HOOD, 
				ARENA_MILLET, ARENA_MARILYN_CH2, 
				ARENA_RIP_CH3, ARENA_GELLER, ARENA_MARIO_CH3,
				ARENA_KEITH, ARENA_NENE,
				ARENA_RIP_CH5, ARENA_HAWKS,
				ARENA_KASHIM,
				ARENA_HESTE, ARENA_JACKSON_CH8,
				ARENA_NIETZCHE, ARENA_SHIRON,
				ARENA_KROSROY, ARENA_GLORIA,
				ARENA_MARILYN_FINAL, ARENA_JACKSON_FINAL, ARENA_MARIO_FINAL
				));
		
		public static final Set<Character> MeleeOnlyArenaCharacters = new HashSet<Character>(Arrays.asList(
				ARENA_SHARK, 
				ARENA_BARKNIN, ARENA_KEIMOS_CH2,
				ARENA_TYLER, ARENA_TREVICK, ARENA_TORTON_CH3,
				ARENA_KALEDIN, ARENA_NIKITA,
				ARENA_SHACKAL, ARENA_SHISIEL,
				ARENA_MANSTEIN,
				ARENA_LOUIS, ARENA_XENON_CH8,
				ARENA_INDRA_CH9, ARENA_MIRIA,
				ARENA_GLANZ, ARENA_RIVA,
				ARENA_KEIMOS_FINAL, ARENA_XENON_FINAL, ARENA_TORTON_FINAL
				));
		
		public static final Set<Character> LordCharacters = new HashSet<Character>(Arrays.asList(SIGURD, SELIPH));
		public static final Set<Character> ThiefCharacters = new HashSet<Character>(Arrays.asList(DEW, PATTY, DAISY));
		public static final Set<Character> DancerCharacters = new HashSet<Character>(Arrays.asList(SILVIA, LENE, LAYLEA));
		public static final Set<Character> HealerCharacters = new HashSet<Character>(Arrays.asList(EDAIN, CLAUD, LANA, MUIRNE, COIRPRE, CHARLOT));
		
		// Elliot and his squad must lose to Eldigan and his Cross Knights. We'll make sure the minions win or lose, but Eldigan can probably solo all of them.
		public static final Set<Character> MustWin1 = new HashSet<Character>(Arrays.asList(CH1_CROSS_KNIGHTS));
		public static final Set<Character> MustLose1 = new HashSet<Character>(Arrays.asList(CH1_HEIRHEIN_ARMY));
		// Quan and Ethlyn (and their squad) must lose to Travant (and Magorn) and his squad.
		public static final Set<Character> MustWin2 = new HashSet<Character>(Arrays.asList(TRAVANT_CH5, CH5_THRACIA_ARMY, MAGORN));
		public static final Set<Character> MustLose2 = new HashSet<Character>(Arrays.asList(QUAN, ETHLYN, CH5_LEONSTER_ARMY));
		// Mahnya and her Squad must lose to Andorey and the Beige Ritter. (Andorey himself probably isn't necessary, since his squad gets all Brave Bows.)
		public static final Set<Character> MustWin3 = new HashSet<Character>(Arrays.asList(CH4_BEIGE_RITTER));
		public static final Set<Character> MustLose3 = new HashSet<Character>(Arrays.asList(MAHNYA, CH4_MAHNYA_SQUAD));
		
		public static final Set<Character> Gen1PlayableCharacters = new HashSet<Character>(Arrays.asList(SIGURD, NAOISE, ALEC, ARDEN, FINN_GEN_1, QUAN, MIDIR, LEWYN, CHULAINN, AZELLE,
				JAMKE, CLAUD, BEOWOLF, LEX, DEW, DEIRDRE, ETHLYN, LACHESIS, AYRA, ERINYS, TAILTIU, SILVIA, EDAIN, BRIGID));
		public static final Set<Character> Gen2StaticCharacters = new HashSet<Character>(Arrays.asList(SHANNAN, IUCHAR, FINN_GEN_2, HANNIBAL, ARES, OIFEY, IUCHARBA, JULIA));
		public static final Set<Character> Gen2ChildCharacters = new HashSet<Character>(Arrays.asList(SELIPH, LEIF, ALTENA, ULSTER, FEBAIL, COIRPRE, CED, DIARMUID, LESTER, ARTHUR, 
				PATTY, LARCEI, LANA, FEE, TINE, LENE, NANNA));
		public static final Set<Character> Gen2SubstituteCharacters = new HashSet<Character>(Arrays.asList(DALVIN, ASAELLO, CHARLOT, HAWK, TRISTAN, DEIMNE, AMID, DAISY, CREIDNE, MUIRNE,
				HERMINA, LINDA, LAYLEA, JEANNE));
		
		// These characters need to make sure no other characters get their blood (more specifically their holy weapon, as they will not be inherited).
		public static final Set<Character> CharactersRequiringUniqueBlood = new HashSet<Character>(Arrays.asList(SIGURD, QUAN));

		public static final Set<Character> RecruitableEnemyCharacters = new HashSet<Character>(Arrays.asList(AYRA, JAMKE, CHULAINN, BEOWOLF, ERINYS, ASAELLO, FEBAIL, ALTENA, TINE, IUCHAR, IUCHARBA, HANNIBAL, LINDA));
		
		public static final Set<Character> Gen1Bosses = new HashSet<Character>(Arrays.asList(
				DIMAGGIO, GERRARD,
				CIMBAETH, MUNNIR, SANDIMA,
				ELLIOT_CH2, PHILLIP, BOLDOR, MACBETH, VOLTZ, CLEMENT, ZYNE, CHAGALL_CH2,
				JACOBAN, ELDIGAN_CH3, CHAGALL_CH3, PAPILION, PIZARL, DOBARL,
				CUVULI, DEETVAR, MAIOS, PAMELA, DONOVAN, LAMIA, DACCAR,
				SLAYDER, ANDOREY_CH5, LOMBARD, MAGORN, VAHA, REPTOR));
		
		public static final Set<Character> Gen2Bosses = new HashSet<Character>(Arrays.asList(
				HAROLD, SCHMIDT, DANANN,
				KUTUZOV, KUTUZOV_TURN_12, LIZA, ISHTORE, JAVARRO, BRAMSEL, VAMPA_CH7, FETRA_CH7, ELIU_CH7, BLOOM_CH7,
				MUHAMMAD, OVO, VAMPA_CH8, FETRA_CH8, ELIU_CH8, ISHTAR_CH8, BLOOM_CH8, CORUTA, MAIKOV,
				KANATZ, DISLER, TRAVANT_CH9, MUSAR, JUDAH, ARION_CH9,
				RIDALE, HILDA_CH10, MORRIGAN, ISHTAR_CH10, JULIUS_CH10, ZAGAM, ARVIS_CH10,
				ROBERT, BOYCE, RODAN, YUPHEEL, FISHER, BRIAN, DAGGON, SCIPIO, HILDA_FINAL, BARAN, MENG, BLEG, MAYBELL, ISHTAR_FINAL, ARION_FINAL, MANFROY, MUS, BOVIS, TIGRIS, LEPUS, DRACO, ANGUILLA, EQUUS, OVIS, SIMIA, GALLUS, CANIS, PORCUS, JULIUS_FINAL));
		
		public static final Set<Character> CastleGuards = new HashSet<Character>(Arrays.asList(
				DIMAGGIO, GERRARD,
				CH1_GENOA_COMMANDER, MUNNIR, SANDIMA,
				BOLDOR, MACBETH, CLEMENT, CHAGALL_CH2,
				JACOBAN, CHAGALL_CH3, DOBARL,
				MAIOS, DACCAR, DONOVAN,
				LOMBARD, VAHA,
				HAROLD, DANANN,
				KUTUZOV, KUTUZOV_TURN_12, ISHTORE, BRAMSEL, BLOOM_CH7,
				BLOOM_CH8, MAIKOV,
				DISLER, JUDAH,
				MORRIGAN, HILDA_CH10, ZAGAM, ARVIS_CH10,
				YUPHEEL, DAGGON, BARAN, MANFROY, JULIUS_FINAL
				));
		
		// These enemies are probably part of the Ch. 5 scene, which will not work properly if they're not mages because it tries to play a fire mage animation from the map.
		public static final Set<Character> DoNotTouchEnemies = new HashSet<Character>(Arrays.asList(
				CH1_VERDANE_COMMANDER_1, CH1_VERDANE_ARMY_5, // Dew and Edain need to outrun these guys in Chapter 1.
				CH5_COMMANDER_1, CH5_ROT_RITTER_1, CH5_COMMANDER_2, CH5_ROT_RITTER_2, CH5_COMMANDER_3, CH5_ROT_RITTER_3, CH5_COMMANDER_4, CH5_ROT_RITTER_4 // Chapter 5 epilogue scene
				));
		
		// Midir will make the game confused if he can't attack in his opening scene.
		// Seliph *technically* doesn't need to attack.
		public static final Set<Character> CharactersThatMustBeAbleToAttack = new HashSet<Character>(Arrays.asList(SIGURD, /*SELIPH,*/ MIDIR));
		public static final Set<Character> CharactersThatMustAttackAtMeleeRange = new HashSet<Character>(Arrays.asList(CHULAINN, BEOWOLF)); // For whatever reason, Beowolf's AI likes to attack at melee range, even if he's bow locked.
		public static final Set<Character> CharactersThatRequireHorses = new HashSet<Character>(Arrays.asList(QUAN, ETHLYN)); // :(
		
		// These bosses can drop their holy weapon if they were randomized to get them.
		public static final Set<Character> HolyBossesWithFreeDrops = new HashSet<Character>(Arrays.asList(HILDA_CH10, MUSAR, ARION_CH9));
		// These bosses can be assigned holy blood, even if they didn't have any before.
		public static final Set<Character> HolyBossesThatReceiveNewHolyBlood = new HashSet<Character>(Arrays.asList(VAMPA_CH7, VAMPA_CH8, RIDALE, ROBERT, BOYCE, SCIPIO, MUS, BOVIS, TIGRIS, LEPUS, DRACO, SIMIA, GALLUS, CANIS, PORCUS, MANFROY));
		// These are the only bosses that can drop their holy weapon.
		public static final Set<Character> HolyBossesThatCanDropHolyWeapons = new HashSet<Character>(Arrays.asList(ANDOREY_CH5, LOMBARD, VAMPA_CH7, VAMPA_CH8, ISHTAR_CH8, BLOOM_CH8, MUSAR, ARION_CH9, HILDA_CH10, RIDALE, ROBERT, BOYCE, BRIAN, SCIPIO));

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
		
		public Character[] getChildren() {
			switch (this) {
			case ETHLYN: return new Character[] {LEIF, ALTENA};
			case EDAIN: return new Character[] {LANA, LESTER};
			case AYRA: return new Character[] {LARCEI, ULSTER};
			case LACHESIS: return new Character[] {DIARMUID, NANNA};
			case ERINYS: return new Character[] {FEE, CED};
			case SILVIA: return new Character[] {LENE, COIRPRE};
			case TAILTIU: return new Character[] {ARTHUR, TINE};
			case BRIGID: return new Character[] {PATTY, FEBAIL};
			case DEIRDRE: return new Character[] {SELIPH, JULIA};
			default: return new Character[] {};
			}
		}
		
		public Character primaryParent() {
			switch (this) {
			case LEIF: return ETHLYN;
			case ALTENA: return QUAN;
			case SELIPH: return SIGURD;
			case JULIA: return DEIRDRE;
			case LANA: 
			case LESTER: 
				return EDAIN;
			case DIARMUID:
			case NANNA:
				return LACHESIS;
			case LARCEI:
			case ULSTER:
				return AYRA;
			case FEE:
			case CED:
				return ERINYS;
			case LENE:
			case COIRPRE:
				return SILVIA;
			case ARTHUR:
			case TINE:
				return TAILTIU;
			case PATTY:
			case FEBAIL:
				return BRIGID;
			default: return null;
			}
		}
		
		public Character getGen1Analogue() {
			switch (this) {
			case LANA: return EDAIN;
			case LESTER: return MIDIR;
			case LARCEI: return AYRA;
			case ULSTER: return CHULAINN;
			case DIARMUID: return BEOWOLF;
			case NANNA: return LACHESIS;
			case FEE: return ERINYS;
			case CED: return LEWYN;
			case LENE: return SILVIA;
			case COIRPRE: return CLAUD;
			case ARTHUR: return AZELLE;
			case TINE: return TAILTIU;
			case PATTY: return DEW;
			case FEBAIL: return BRIGID;
			
			case SELIPH: return SIGURD;
			case LEIF: return ETHLYN;
			//case ALTENA: return QUAN; // We don't need to specify this one, since we handle it elsewhere and they don't really match to begin with.
			default: return null;
			}
		}
		
		public Item requiresWeapon() {
			switch (this) {
			case MUNNIR: return Item.HAND_AXE;
			default: return null;
			}
		}
		
		public CharacterClass[] blacklistedClasses() {
			switch (this) {
			case SIGURD: // Needs to sieze ch. 4 castle. Also may break the game if flying.
				Set<CharacterClass> blacklist = new HashSet<CharacterClass>(CharacterClass.armoredClasses);
				blacklist.addAll(CharacterClass.fliers);
				return blacklist.toArray(new CharacterClass[blacklist.size()]);
			case AYRA: // Technically she's ok, but Larcei depends on her.
			case LARCEI:
			case CREIDNE: // Be careful about sequence breaking Ch. 6. They can recruit Iuchar and Iucharba earlier than they're supposed to if they can fly.
			case SELIPH: // Seliph can also seize castles out of order if he turns out to be able to fly.
				return CharacterClass.fliers.toArray(new CharacterClass[CharacterClass.fliers.size()]);
			case LEWYN:  // Needs holy weapon from ch. 4 castle.
				return CharacterClass.armoredClasses.toArray(new CharacterClass[CharacterClass.armoredClasses.size()]);
			case DEW: // Patty as a possible child means Dew needs to be limited in case of strictly matched children.
				blacklist = new HashSet<CharacterClass>(CharacterClass.maleOnlyClasses);
				blacklist.addAll(CharacterClass.femaleOnlyClasses);
				return blacklist.toArray(new CharacterClass[blacklist.size()]);
			case ELDIGAN_CH1_SCENE:
			case ELDIGAN_CH3: // Eldigan shouldn't get bow-locked. He might not win against Elliot if he is.
				return CharacterClass.rangedOnlyClasses.toArray(new CharacterClass[CharacterClass.rangedOnlyClasses.size()]);
				
			default: return new CharacterClass[] {};
			}
		}
		
		// If this returns a non-empty array, then the result should be restricted to classes in the set.
		public CharacterClass[] whitelistedClasses(boolean isRandomizingMinions) {
			Set<CharacterClass> whitelistedClasses = new HashSet<CharacterClass>();
			// Otherwise, there are some characters that have to be careful with what they pass down to gen 2 enemies if their weapons are not inerhited.
			switch (this) {
			case CLAUD: // Some random minion has one of Claud's items, so we need to make sure that remains working.
				if (!isRandomizingMinions) {
					whitelistedClasses.addAll(CharacterClass.fireUsers);
					whitelistedClasses.removeAll(CharacterClass.B_fireUsers);
					whitelistedClasses.addAll(CharacterClass.thunderUsers);
					whitelistedClasses.removeAll(CharacterClass.B_thunderUsers);
					whitelistedClasses.addAll(CharacterClass.windUsers);
					whitelistedClasses.removeAll(CharacterClass.B_windUsers);
					whitelistedClasses.addAll(CharacterClass.staffUsers);
				}
				break;
			case ERINYS:
			case FEE:
			case HERMINA:
			case ALTENA:
				whitelistedClasses.addAll(CharacterClass.fliers);
			default:
				break;
			}
			
			return whitelistedClasses.toArray(new CharacterClass[whitelistedClasses.size()]);
		}
		
		public HolyBlood[] limitedHolyBloodSelection() {
			switch (this) {
			case QUAN: return new HolyBlood[] {HolyBlood.BALDR, HolyBlood.OD, HolyBlood.HEZUL, HolyBlood.DAIN, HolyBlood.NJORUN}; // Mostly due to Altena needing to fly (and therefore locked to lances and swords).
			/* Sigurd and Deirdre no longer need to be limited because Seliph's blood can have all four bytes set (they're just not contiguous, for some reason).
			 * We still want to avoid Loptous for obvious reasons (and possibly Bragi), but everything else is fair game.
			 
			case SIGURD:
			case DEIRDRE: // Seliph's blood inheritence only supports the first two bytes, so neither parent can go beyond that.
				return new HolyBlood[] {HolyBlood.BALDR, HolyBlood.NAGA, HolyBlood.DAIN, HolyBlood.NJORUN, HolyBlood.OD, HolyBlood.ULIR, HolyBlood.NEIR, HolyBlood.FJALAR};
				
			*/
			case ELDIGAN_CH1_SCENE: // This is just to make sure he doesn't get stuck with Yewfelle in chapter 1.
				return new HolyBlood[] { HolyBlood.BALDR, HolyBlood.OD, HolyBlood.HEZUL, HolyBlood.DAIN, HolyBlood.NJORUN, 
						HolyBlood.NEIR, HolyBlood.FJALAR, HolyBlood.THRUD, HolyBlood.FORSETI, HolyBlood.NAGA};
			case ARVIS_CH5: // Make sure we don't get Ulir, since it'll crash the Ch. 5 scene. He also shouldn't get Naga because it forces animations on.
			case ARVIS_CH3_SCENE:
			case ARVIS_CH10:
				return new HolyBlood[] { HolyBlood.BALDR, HolyBlood.OD, HolyBlood.HEZUL, HolyBlood.DAIN, HolyBlood.NJORUN, 
						HolyBlood.NEIR, HolyBlood.FJALAR, HolyBlood.THRUD, HolyBlood.FORSETI};
			case SHANNAN: // Just to make sure he can escape with Patty, make sure he doesn't get stuck with a staff.
				return new HolyBlood[] {
						HolyBlood.BALDR, HolyBlood.OD, HolyBlood.HEZUL, HolyBlood.DAIN, HolyBlood.NJORUN, 
						HolyBlood.NEIR, HolyBlood.ULIR, HolyBlood.FJALAR, HolyBlood.THRUD, HolyBlood.FORSETI, HolyBlood.NAGA};
			default: return new HolyBlood[] {HolyBlood.BALDR, HolyBlood.OD, HolyBlood.HEZUL, HolyBlood.DAIN, HolyBlood.NJORUN, 
					HolyBlood.NEIR, HolyBlood.ULIR, HolyBlood.FJALAR, HolyBlood.THRUD, HolyBlood.FORSETI, HolyBlood.NAGA, HolyBlood.BRAGI};
			}
		}
		
		public boolean requiresAttack() {
			return CharactersThatMustBeAbleToAttack.contains(this);
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
		
		public boolean isLord() {
			return LordCharacters.contains(this);
		}
		
		public boolean isThief() {
			return ThiefCharacters.contains(this);
		}
		
		public boolean isDancer() {
			return DancerCharacters.contains(this);
		}
		
		public boolean isHealer() {
			return HealerCharacters.contains(this);
		}
		
		public boolean isMinion() {
			String name = this.toString();
			return name.startsWith("PROLOGUE") || name.startsWith("CH1") || name.startsWith("CH2") || name.startsWith("CH3") || name.startsWith("CH4") || name.startsWith("CH5") ||
					name.startsWith("CH6") || name.startsWith("CH7") || name.startsWith("CH8") || name.startsWith("CH9") || name.startsWith("CH10") || name.startsWith("ENDGAME");
		}
		
		public int joinChapter() {
			switch (this) {
			case SIGURD: case NAOISE: case ALEC: case ARDEN: case QUAN: case ETHLYN: case FINN_GEN_1: case LEX: case AZELLE: case MIDIR:
				return 0;
			case EDAIN: case AYRA: case DEW: case JAMKE: case DEIRDRE:
				return 1;
			case CHULAINN: case LACHESIS: case LEWYN: case SILVIA: case BEOWOLF: case ERINYS:
				return 2;
			case BRIGID: case TAILTIU: case CLAUD:
				return 3;
			case SELIPH: case LANA: case MUIRNE: case LARCEI: case ULSTER: case CREIDNE: case DALVIN:case LESTER: case DEIMNE: case OIFEY: case DIARMUID:
			case TRISTAN: case JULIA: case FEE: case HERMINA: case ARTHUR: case AMID: case IUCHAR: case IUCHARBA:
				return 6;
			case SHANNAN: case PATTY: case DAISY: case LEIF: case NANNA: case JEANNE: case FINN_GEN_2: case ARES: case LENE: case LAYLEA: case TINE: case LINDA:
				return 7;
			case FEBAIL: case ASAELLO: case CED: case HAWK:
				return 8;
			case HANNIBAL: case COIRPRE: case CHARLOT: case ALTENA:
				return 9;
			default:
				return 0;
			}
		}
		
		public int minionChapter() {
			String name = this.toString();
			if (name.startsWith("PROLOGUE")) { return 0; }
			else if (name.startsWith("CH1")) { return 1; }
			else if (name.startsWith("CH2")) { return 2; } 
			else if (name.startsWith("CH3")) { return 3; }
			else if (name.startsWith("CH4")) { return 4; }
			else if (name.startsWith("CH5")) { return 5; }
			else if (name.startsWith("CH6")) { return 6; }
			else if (name.startsWith("CH7")) { return 7; }
			else if (name.startsWith("CH8")) { return 8; }
			else if (name.startsWith("CH9")) { return 9; }
			else if (name.startsWith("CH10")) { return 10; }
			else if (name.startsWith("ENDGAME")) { return 11; }
			
			return 0;
		}
		
		public boolean isArena() {
			String name = this.toString();
			return name.startsWith("ARENA");
		}
		
		public boolean requiresMelee() {
			return (isArena() && !RangedOnlyArenaCharacters.contains(this)) || CharactersThatMustAttackAtMeleeRange.contains(this);
		}
		
		public boolean requiresRange() {
			return !MeleeOnlyArenaCharacters.contains(this);
		}
		
		public boolean isBoss() {
			return Gen1Bosses.contains(this) || Gen2Bosses.contains(this);
		}
		
		public Character[] mustLoseToCharacters() {
			if (MustLose1.contains(this)) {
				return MustWin1.toArray(new Character[MustWin1.size()]);
			} else if (MustLose2.contains(this)) {
				return MustWin2.toArray(new Character[MustWin2.size()]);
			} else if (MustLose3.contains(this)) {
				return MustWin3.toArray(new Character[MustWin3.size()]);
			}
			
			return new Character[] {};
		}
		
		public Character[] mustBeatCharacter() {
			if (MustWin1.contains(this)) {
				return MustLose1.toArray(new Character[MustLose1.size()]);
			} else if (MustWin2.contains(this)) {
				return MustLose2.toArray(new Character[MustLose2.size()]);
			} else if (MustWin3.contains(this)) {
				return MustLose3.toArray(new Character[MustLose3.size()]);
			}
			
			return new Character[] {};
		}
		
		public Character[] sharedWeaknesses() {
			Set<Character> charSet = new HashSet<Character>();
			if (MustLose1.contains(this)) {
				charSet.addAll(MustLose1);
			} else if (MustLose2.contains(this)) {
				charSet.addAll(MustLose2);
			} else if (MustLose3.contains(this)) {
				charSet.addAll(MustLose3);
			}
			
			charSet.remove(this);
			
			return charSet.toArray(new Character[charSet.size()]);
		}
		
		public Character[] linkedCharacters() {
			switch (this) {
			case FINN_GEN_1:
			case FINN_GEN_2:
				return new Character[] {FINN_GEN_1, FINN_GEN_2};
			case ELLIOT_CH1_SCENE:
			case ELLIOT_CH2:
				return new Character[] {ELLIOT_CH1_SCENE, ELLIOT_CH2};
			case ELDIGAN_CH1_SCENE:
			case ELDIGAN_CH3:
				return new Character[] {ELDIGAN_CH1_SCENE, ELDIGAN_CH3};
			case CHAGALL_CH2:
			case CHAGALL_CH3:
				return new Character[] {CHAGALL_CH2, CHAGALL_CH3};
			case ANDOREY_CH4:
			case ANDOREY_CH5:
				return new Character[] {ANDOREY_CH4, ANDOREY_CH5};
			case LOMBARD_CH3_SCENE:
			case LOMBARD:
				return new Character[] {LOMBARD, LOMBARD_CH3_SCENE};
			case REPTOR_CH3_SCENE:
			case REPTOR:
				return new Character[] {REPTOR, REPTOR_CH3_SCENE};
			case KUTUZOV:
			case KUTUZOV_TURN_12:
				return new Character[] {KUTUZOV, KUTUZOV_TURN_12};
			case VAMPA_CH7:
			case VAMPA_CH8:
				return new Character[] {VAMPA_CH7, VAMPA_CH8};
			case FETRA_CH7:
			case FETRA_CH8:
				return new Character[] {FETRA_CH7, FETRA_CH8};
			case ELIU_CH7:
			case ELIU_CH8:
				return new Character[] {ELIU_CH7, ELIU_CH8};
			case BLOOM_CH7:
			case BLOOM_CH8:
				return new Character[] {BLOOM_CH7, BLOOM_CH8};
			case ISHTAR_CH8:
			case ISHTAR_CH10:
			case ISHTAR_FINAL:
				return new Character[] {ISHTAR_CH8, ISHTAR_CH10, ISHTAR_FINAL};
			case TRAVANT_CH5:
			case TRAVANT_CH9:
				return new Character [] {TRAVANT_CH5, TRAVANT_CH9};
			case ARION_CH9:
			case ARION_FINAL:
				return new Character[] {ARION_CH9, ARION_FINAL};
			case HILDA_CH10:
			case HILDA_FINAL:
				return new Character[] {HILDA_CH10, HILDA_FINAL};
			case ARVIS_CH3_SCENE:
			case ARVIS_CH5:
			case ARVIS_CH10:
				return new Character[] {ARVIS_CH3_SCENE, ARVIS_CH5, ARVIS_CH10};
			case JULIUS_CH10:
			case JULIUS_FINAL:
				return new Character[] {JULIUS_CH10, JULIUS_FINAL};
			case MAHNYA:
			case MAHNYA_CH3_SCENE:
				return new Character[] {MAHNYA, MAHNYA_CH3_SCENE};
			default:
				return new Character[] {this};
			}
		}
		
		public Character substituteForChild() {
			switch (this) {
			case LANA: return MUIRNE;
			case LESTER: return DEIMNE;
			case LARCEI: return CREIDNE;
			case ULSTER: return DALVIN;
			case NANNA: return JEANNE;
			case DIARMUID: return TRISTAN;
			case LENE: return LAYLEA;
			case COIRPRE: return CHARLOT;
			case FEE: return HERMINA;
			case CED: return HAWK;
			case TINE: return LINDA;
			case ARTHUR: return AMID;
			case PATTY: return DAISY;
			case FEBAIL: return ASAELLO;
			default: return null;
			}
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
		ARMOR(0x22), // LANCE_ARMOR
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
		BARD(0x3B),
		LIGHT_PRIESTESS(0x3C), // Deirdre and Julia's class.
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
		// DARK_PRINCE(0x47),
		
		// Special
		DANCER(0x32),
		;
		
		public static final Comparator<CharacterClass> defaultComparator = new Comparator<CharacterClass>() {
			@Override
			public int compare(CharacterClass o1, CharacterClass o2) {
				return Integer.compare(o1.ID, o2.ID);
			}
		};
		
		public enum GenderType {
			ANY, MALE_ONLY, FEMALE_ONLY;
		}
		
		public static final Set<CharacterClass> unpromotedClasses = new HashSet<CharacterClass>(Arrays.asList(SOCIAL_KNIGHT, LANCE_KNIGHT, ARCH_KNIGHT, AXE_KNIGHT, FREE_KNIGHT, TROUBADOUR, 
				PEGASUS_KNIGHT, DRAGON_RIDER, DRAGON_KNIGHT, BOW_FIGHTER, SWORD_FIGHTER, ARMOR, AXE_ARMOR, BOW_ARMOR, SWORD_ARMOR, AXE_FIGHTER, JUNIOR_LORD, PRINCE, PRINCESS, PRIEST, MAGE,
				FIRE_MAGE, THUNDER_MAGE, WIND_MAGE, BARD, LIGHT_PRIESTESS, THIEF, BARBARIAN, MOUNTAIN_THIEF, PIRATE, HUNTER, DARK_MAGE, DANCER));
		public static final Set<CharacterClass> promotedClasses = new HashSet<CharacterClass>(Arrays.asList(LORD_KNIGHT, DUKE_KNIGHT, MASTER_KNIGHT, PALADIN, PALADIN_F, BOW_KNIGHT, FORREST_KNIGHT,
				MAGE_KNIGHT, GREAT_KNIGHT, FALCON_KNIGHT, DRAGON_MASTER, SWORD_MASTER, SNIPER, FORREST, GENERAL, WARRIOR, MAGE_FIGHTER, MAGE_FIGHTER_F, HIGH_PRIEST, SAGE, THIEF_FIGHTER, EMPEROR,
				BARON, QUEEN, BISHOP, DARK_BISHOP));
		
		// We can blacklist as necessary, but this list includes all of the classes we don't give out to playable characters freely.
		// Emperor and Queen are blocked as we don't give out advanced classes freely (but we allow playable characters to promote into them under the right settings).
		public static final Set<CharacterClass> enemyOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(/*BARBARIAN, MOUNTAIN_THIEF, PIRATE, HUNTER, DARK_MAGE, EMPEROR,*/ BARON/*, QUEEN*/, BISHOP, DARK_BISHOP));
		// Princess should be ok, so long as we adhere to gender flags.
		public static final Set<CharacterClass> playerOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(/*PRINCESS*/));
		
		public static final Set<CharacterClass> swordUsers = new HashSet<CharacterClass>(Arrays.asList(JUNIOR_LORD, LORD_KNIGHT, PRINCE, PRINCESS, MASTER_KNIGHT, SWORD_FIGHTER, SWORD_MASTER, FORREST, THIEF,
				THIEF_FIGHTER, DANCER, SOCIAL_KNIGHT, PALADIN, TROUBADOUR, PALADIN_F, FREE_KNIGHT, FORREST_KNIGHT, SWORD_ARMOR, GENERAL, BARON, EMPEROR, PEGASUS_KNIGHT, FALCON_KNIGHT, DRAGON_RIDER,
				DRAGON_KNIGHT, DRAGON_MASTER, MAGE_FIGHTER, MAGE_FIGHTER_F, MAGE_KNIGHT));
		public static final Set<CharacterClass> lanceUsers = new HashSet<CharacterClass>(Arrays.asList(LORD_KNIGHT, MASTER_KNIGHT, SOCIAL_KNIGHT, PALADIN, PALADIN_F, LANCE_KNIGHT, DUKE_KNIGHT, ARMOR, GENERAL, BARON,
				EMPEROR, PEGASUS_KNIGHT, FALCON_KNIGHT, DRAGON_RIDER, DRAGON_KNIGHT, DRAGON_MASTER));
		public static final Set<CharacterClass> axeUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, AXE_FIGHTER, WARRIOR, MOUNTAIN_THIEF, BARBARIAN, PIRATE, AXE_KNIGHT, GREAT_KNIGHT, AXE_ARMOR, GENERAL,
				BARON, EMPEROR));
		public static final Set<CharacterClass> bowUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, WARRIOR, BOW_FIGHTER, SNIPER, HUNTER, ARCH_KNIGHT, BOW_KNIGHT, BOW_ARMOR, GENERAL, BARON, EMPEROR));
		public static final Set<CharacterClass> fireUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, FIRE_MAGE, MAGE_FIGHTER, MAGE_FIGHTER_F, MAGE, MAGE_KNIGHT, BARD, SAGE, HIGH_PRIEST,
				BISHOP, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> thunderUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, THUNDER_MAGE, MAGE_FIGHTER, MAGE_FIGHTER_F, MAGE, MAGE_KNIGHT, BARD, SAGE,
				HIGH_PRIEST, BISHOP, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> windUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, WIND_MAGE, MAGE_FIGHTER, MAGE_FIGHTER_F, MAGE, MAGE_KNIGHT, BARD, SAGE, HIGH_PRIEST,
				BISHOP, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> lightUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARD, LIGHT_PRIESTESS, SAGE));
		public static final Set<CharacterClass> darkUsers = new HashSet<CharacterClass>(Arrays.asList(DARK_MAGE, DARK_BISHOP));
		public static final Set<CharacterClass> staffUsers = new HashSet<CharacterClass>(Arrays.asList(PRINCESS, MASTER_KNIGHT, TROUBADOUR, PALADIN_F, BARON, EMPEROR, FALCON_KNIGHT, MAGE_FIGHTER_F, LIGHT_PRIESTESS, SAGE,
				PRIEST, HIGH_PRIEST, BISHOP, QUEEN, DARK_MAGE, DARK_BISHOP));
		
		public static final Set<CharacterClass> B_swordUsers = new HashSet<CharacterClass>(Arrays.asList(JUNIOR_LORD, LORD_KNIGHT, PRINCE, PRINCESS, MASTER_KNIGHT, SWORD_FIGHTER, SWORD_MASTER, FORREST,
				THIEF_FIGHTER, SOCIAL_KNIGHT, PALADIN, TROUBADOUR, PALADIN_F, FREE_KNIGHT, FORREST_KNIGHT, SWORD_ARMOR, GENERAL, BARON, EMPEROR, PEGASUS_KNIGHT, FALCON_KNIGHT,
				DRAGON_KNIGHT, DRAGON_MASTER, MAGE_KNIGHT));
		public static final Set<CharacterClass> B_lanceUsers = new HashSet<CharacterClass>(Arrays.asList(LORD_KNIGHT, MASTER_KNIGHT, PALADIN, LANCE_KNIGHT, DUKE_KNIGHT, ARMOR, GENERAL, BARON,
				EMPEROR, PEGASUS_KNIGHT, FALCON_KNIGHT, DRAGON_KNIGHT, DRAGON_MASTER));
		public static final Set<CharacterClass> B_axeUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, AXE_FIGHTER, WARRIOR, MOUNTAIN_THIEF, BARBARIAN, PIRATE, AXE_KNIGHT, GREAT_KNIGHT, AXE_ARMOR, GENERAL,
				BARON, EMPEROR));
		public static final Set<CharacterClass> B_bowUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, WARRIOR, BOW_FIGHTER, SNIPER, HUNTER, ARCH_KNIGHT, BOW_KNIGHT, BOW_ARMOR, GENERAL, BARON, EMPEROR));
		public static final Set<CharacterClass> B_fireUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, FIRE_MAGE, MAGE_FIGHTER, MAGE_FIGHTER_F, MAGE_KNIGHT, SAGE, BISHOP, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> B_thunderUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, THUNDER_MAGE, MAGE_FIGHTER, MAGE_FIGHTER_F, MAGE_KNIGHT, SAGE, BISHOP, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> B_windUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, WIND_MAGE, MAGE_FIGHTER, MAGE_FIGHTER_F, MAGE_KNIGHT, SAGE,
				BISHOP, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> B_lightUsers = new HashSet<CharacterClass>(Arrays.asList(LIGHT_PRIESTESS, SAGE));
		public static final Set<CharacterClass> B_darkUsers = new HashSet<CharacterClass>(Arrays.asList(DARK_MAGE, DARK_BISHOP));
		public static final Set<CharacterClass> B_staffUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, MAGE_FIGHTER_F, LIGHT_PRIESTESS, SAGE, PRIEST, HIGH_PRIEST, BISHOP, QUEEN, DARK_MAGE, 
				DARK_BISHOP));
		
		public static final Set<CharacterClass> A_swordUsers = new HashSet<CharacterClass>(Arrays.asList(LORD_KNIGHT, PRINCE, MASTER_KNIGHT, SWORD_FIGHTER, SWORD_MASTER, FORREST, THIEF_FIGHTER, FORREST_KNIGHT, GENERAL, 
				BARON, EMPEROR, FALCON_KNIGHT, DRAGON_MASTER));
		public static final Set<CharacterClass> A_lanceUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, DUKE_KNIGHT, GENERAL, BARON, EMPEROR, FALCON_KNIGHT, DRAGON_MASTER));
		public static final Set<CharacterClass> A_axeUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, AXE_FIGHTER, WARRIOR, GREAT_KNIGHT, BARON, EMPEROR));
		public static final Set<CharacterClass> A_bowUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BOW_FIGHTER, SNIPER, BOW_KNIGHT, BARON, EMPEROR));
		public static final Set<CharacterClass> A_fireUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> A_thunderUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> A_windUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, QUEEN, DARK_BISHOP));
		public static final Set<CharacterClass> A_lightUsers = new HashSet<CharacterClass>(Arrays.asList(LIGHT_PRIESTESS));
		public static final Set<CharacterClass> A_darkUsers = new HashSet<CharacterClass>(Arrays.asList(DARK_BISHOP));
		public static final Set<CharacterClass> A_staffUsers = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, BARON, EMPEROR, HIGH_PRIEST, BISHOP, QUEEN, DARK_BISHOP));
		
		public static final Set<CharacterClass> maleOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(JUNIOR_LORD, LORD_KNIGHT, PRINCE, AXE_FIGHTER, WARRIOR, BARBARIAN, PIRATE, HUNTER, SWORD_ARMOR, ARMOR, 
				AXE_ARMOR, BOW_ARMOR, DRAGON_RIDER, MAGE_FIGHTER, FREE_KNIGHT, FORREST_KNIGHT, PALADIN, MOUNTAIN_THIEF, BISHOP, DARK_MAGE));
		public static final Set<CharacterClass> femaleOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(PRINCESS, DANCER, TROUBADOUR, PALADIN_F, FALCON_KNIGHT, PEGASUS_KNIGHT, MAGE_FIGHTER_F, LIGHT_PRIESTESS, QUEEN));
		
		public static final Set<CharacterClass> noWeaknessClasses = new HashSet<CharacterClass>(Arrays.asList(BOW_FIGHTER, SWORD_FIGHTER, AXE_FIGHTER, JUNIOR_LORD, PRINCE, PRINCESS, PRIEST, MAGE,
				FIRE_MAGE, THUNDER_MAGE, WIND_MAGE, BARD, LIGHT_PRIESTESS, THIEF, BARBARIAN, MOUNTAIN_THIEF, PIRATE, HUNTER, DARK_MAGE, DANCER, SWORD_MASTER, SNIPER, FORREST, WARRIOR, MAGE_FIGHTER, MAGE_FIGHTER_F, HIGH_PRIEST, 
				SAGE, THIEF_FIGHTER, QUEEN, BISHOP, DARK_BISHOP));
		
		public static final Set<CharacterClass> lordClasses = new HashSet<CharacterClass>(Arrays.asList(JUNIOR_LORD, LORD_KNIGHT));
		public static final Set<CharacterClass> thiefClasses = new HashSet<CharacterClass>(Arrays.asList(THIEF, THIEF_FIGHTER));
		
		public static final Set<CharacterClass> pacifistClasses = new HashSet<CharacterClass>(Arrays.asList(PRIEST, DANCER));
		public static final Set<CharacterClass> healingClasses = new HashSet<CharacterClass>(Arrays.asList(PRIEST, TROUBADOUR, PRINCESS));
		
		public static final Set<CharacterClass> horsebackClasses = new HashSet<CharacterClass>(Arrays.asList(SOCIAL_KNIGHT, LANCE_KNIGHT, ARCH_KNIGHT, AXE_KNIGHT, FREE_KNIGHT, TROUBADOUR, 
				LORD_KNIGHT, DUKE_KNIGHT, MASTER_KNIGHT, PALADIN, PALADIN_F, BOW_KNIGHT, FORREST_KNIGHT, MAGE_KNIGHT, GREAT_KNIGHT));
		public static final Set<CharacterClass> fliers = new HashSet<CharacterClass>(Arrays.asList(PEGASUS_KNIGHT, DRAGON_RIDER, DRAGON_KNIGHT, FALCON_KNIGHT, DRAGON_MASTER));
		public static final Set<CharacterClass> armoredClasses = new HashSet<CharacterClass>(Arrays.asList(ARMOR, AXE_ARMOR, BOW_ARMOR, SWORD_ARMOR, GENERAL, EMPEROR, BARON));
		
		public static final Set<CharacterClass> rangedOnlyClasses = new HashSet<CharacterClass>(Arrays.asList(ARCH_KNIGHT, BOW_KNIGHT, HUNTER, BOW_ARMOR, BOW_FIGHTER, SNIPER));
		
		public static final Set<CharacterClass> advancedClasses = new HashSet<CharacterClass>(Arrays.asList(EMPEROR, QUEEN));
		
		public static final Set<CharacterClass> reducedChanceClasses = new HashSet<CharacterClass>(Arrays.asList(MASTER_KNIGHT, EMPEROR, QUEEN, BARON));
		
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
		
		public boolean isHorseback() { return horsebackClasses.contains(this); }
		public boolean isFlier() { return fliers.contains(this); }
		public boolean isPacifist() { return pacifistClasses.contains(this); }
		public boolean isArmored() { return armoredClasses.contains(this); }
		public boolean isHealer() { return healingClasses.contains(this); }
		
		public HolyBlood[] supportedHolyBlood() {
			Set<HolyBlood> bloodSet = new HashSet<HolyBlood>();
			if (swordUsers.contains(this)) { bloodSet.add(HolyBlood.BALDR); bloodSet.add(HolyBlood.OD); bloodSet.add(HolyBlood.HEZUL); }
			if (lanceUsers.contains(this)) { bloodSet.add(HolyBlood.DAIN); bloodSet.add(HolyBlood.NJORUN); }
			if (axeUsers.contains(this)) { bloodSet.add(HolyBlood.NEIR); }
			if (bowUsers.contains(this)) { bloodSet.add(HolyBlood.ULIR); }
			if (fireUsers.contains(this)) { bloodSet.add(HolyBlood.FJALAR); }
			if (thunderUsers.contains(this)) { bloodSet.add(HolyBlood.THRUD); }
			if (windUsers.contains(this)) { bloodSet.add(HolyBlood.FORSETI); }
			if (lightUsers.contains(this)) { bloodSet.add(HolyBlood.NAGA); }
			if (darkUsers.contains(this)) { bloodSet.add(HolyBlood.LOPTOUS); }
			if (staffUsers.contains(this)) { bloodSet.add(HolyBlood.BRAGI); }
			return bloodSet.toArray(new HolyBlood[bloodSet.size()]);
		}
		
		public Item[] usableItems(List<HolyBloodSlot1> slot1Blood, List<HolyBloodSlot2> slot2Blood, List<HolyBloodSlot3> slot3Blood) {
			return usableItems(slot1Blood, slot2Blood, slot3Blood, false);
		}
		
		public Item[] usableItems(List<HolyBloodSlot1> slot1Blood, List<HolyBloodSlot2> slot2Blood, List<HolyBloodSlot3> slot3Blood, boolean allowSiegeTomes) {
			if (slot1Blood == null) { slot1Blood = new ArrayList<HolyBloodSlot1>(); }
			if (slot2Blood == null) { slot2Blood = new ArrayList<HolyBloodSlot2>(); }
			if (slot3Blood == null) { slot3Blood = new ArrayList<HolyBloodSlot3>(); }
			
			Set<Item> items = new HashSet<Item>();
			
			if (swordUsers.contains(this)) {
				boolean hasMajorBlood = slot1Blood.contains(HolyBloodSlot1.MAJOR_BALDR) || slot2Blood.contains(HolyBloodSlot2.MAJOR_OD) || slot3Blood.contains(HolyBloodSlot3.MAJOR_HEZUL);
				boolean hasMinorBlood = slot1Blood.contains(HolyBloodSlot1.MINOR_BALDR) || slot2Blood.contains(HolyBloodSlot2.MINOR_OD) || slot3Blood.contains(HolyBloodSlot3.MINOR_HEZUL);
				addItemsOfTypeAndRank(items, ItemType.SWORD, hasMajorBlood, hasMinorBlood);
			}
			if (lanceUsers.contains(this)) {
				boolean hasMajorBlood = slot1Blood.contains(HolyBloodSlot1.MAJOR_DAIN) || slot1Blood.contains(HolyBloodSlot1.MAJOR_NJORUN);
				boolean hasMinorBlood = slot1Blood.contains(HolyBloodSlot1.MINOR_DAIN) || slot1Blood.contains(HolyBloodSlot1.MINOR_NJORUN);
				addItemsOfTypeAndRank(items, ItemType.LANCE, hasMajorBlood, hasMinorBlood);
			}
			if (axeUsers.contains(this)) {
				boolean hasMajorBlood = slot2Blood.contains(HolyBloodSlot2.MAJOR_NEIR);
				boolean hasMinorBlood = slot2Blood.contains(HolyBloodSlot2.MINOR_NEIR);
				addItemsOfTypeAndRank(items, ItemType.AXE, hasMajorBlood, hasMinorBlood);
			}
			if (bowUsers.contains(this)) {
				boolean hasMajorBlood = slot2Blood.contains(HolyBloodSlot2.MAJOR_ULIR);
				boolean hasMinorBlood = slot2Blood.contains(HolyBloodSlot2.MINOR_ULIR);
				addItemsOfTypeAndRank(items, ItemType.BOW, hasMajorBlood, hasMinorBlood);
			}
			if (fireUsers.contains(this)) {
				boolean hasMajorBlood = slot2Blood.contains(HolyBloodSlot2.MAJOR_FJALAR);
				boolean hasMinorBlood = slot2Blood.contains(HolyBloodSlot2.MINOR_FJALAR);
				addItemsOfTypeAndRank(items, ItemType.FIRE_MAGIC, hasMajorBlood, hasMinorBlood);
			}
			if (thunderUsers.contains(this)) {
				boolean hasMajorBlood = slot3Blood.contains(HolyBloodSlot3.MAJOR_THRUD);
				boolean hasMinorBlood = slot3Blood.contains(HolyBloodSlot3.MINOR_THRUD);
				addItemsOfTypeAndRank(items, ItemType.THUNDER_MAGIC, hasMajorBlood, hasMinorBlood);
			}
			if (windUsers.contains(this)) {
				boolean hasMajorBlood = slot3Blood.contains(HolyBloodSlot3.MAJOR_FORSETI);
				boolean hasMinorBlood = slot3Blood.contains(HolyBloodSlot3.MINOR_FORSETI);
				addItemsOfTypeAndRank(items, ItemType.WIND_MAGIC, hasMajorBlood, hasMinorBlood);
			}
			if (lightUsers.contains(this)) {
				boolean hasMajorBlood = slot1Blood.contains(HolyBloodSlot1.MAJOR_NAGA);
				boolean hasMinorBlood = slot1Blood.contains(HolyBloodSlot1.MINOR_NAGA);
				addItemsOfTypeAndRank(items, ItemType.LIGHT_MAGIC, hasMajorBlood, hasMinorBlood);
			}
			if (darkUsers.contains(this)) {
				addItemsOfTypeAndRank(items, ItemType.DARK_MAGIC, false, false);
			}
			if (staffUsers.contains(this)) {
				boolean hasMajorBlood = slot3Blood.contains(HolyBloodSlot3.MAJOR_BRAGI);
				boolean hasMinorBlood = slot3Blood.contains(HolyBloodSlot3.MINOR_BRAGI);
				addItemsOfTypeAndRank(items, ItemType.STAFF, hasMajorBlood, hasMinorBlood);
			}
			
			if (!allowSiegeTomes) {
				items.removeAll(Item.siegeTomes);
			}
			
			return items.toArray(new Item[items.size()]);
		}
		
		private void addItemsOfTypeAndRank(Set<Item> items, ItemType type, boolean hasMajorBlood, boolean hasMinorBlood) {
			if (hasMajorBlood) {
				Collections.addAll(items, Item.weaponsOfTypeAndRank(type, WeaponRank.PRF, true));
			} else {
				if (classSetForTypeAndRank(type, WeaponRank.A).contains(this) || (classSetForTypeAndRank(type, WeaponRank.B).contains(this) && hasMinorBlood)) {
					Collections.addAll(items, Item.weaponsOfTypeAndRank(type, WeaponRank.A, true));
				} else if (classSetForTypeAndRank(type, WeaponRank.B).contains(this) || hasMinorBlood) {
					Collections.addAll(items, Item.weaponsOfTypeAndRank(type, WeaponRank.B, true));
				} else {
					Collections.addAll(items, Item.weaponsOfTypeAndRank(type, WeaponRank.C, true));
				}
			}
		}
		
		private Set<CharacterClass> classSetForTypeAndRank(ItemType type, WeaponRank rank) {
			switch (type) {
			case SWORD:
				switch (rank) {
				case A: return A_swordUsers;
				case B: return B_swordUsers;
				default: return swordUsers;
				}
			case LANCE:
				switch (rank) {
				case A: return A_lanceUsers;
				case B: return B_lanceUsers;
				default: return lanceUsers;
				}
			case AXE:
				switch (rank) {
				case A: return A_axeUsers;
				case B: return B_axeUsers;
				default: return axeUsers;
				}
			case BOW:
				switch (rank) {
				case A: return A_bowUsers;
				case B: return B_bowUsers;
				default: return bowUsers;
				}
			case FIRE_MAGIC:
				switch (rank) {
				case A: return A_fireUsers;
				case B: return B_fireUsers;
				default: return fireUsers;
				}
			case THUNDER_MAGIC:
				switch (rank) {
				case A: return A_thunderUsers;
				case B: return B_thunderUsers;
				default: return thunderUsers;
				}
			case WIND_MAGIC:
				switch (rank) {
				case A: return A_windUsers;
				case B: return B_windUsers;
				default: return windUsers;
				}
			case LIGHT_MAGIC:
				switch (rank) {
				case A: return A_lightUsers;
				case B: return B_lightUsers;
				default: return lightUsers;
				}
			case DARK_MAGIC:
				switch (rank) {
				case A: return A_darkUsers;
				case B: return B_darkUsers;
				default: return darkUsers;
				}
			case STAFF:
				switch (rank) {
				case A: return A_staffUsers;
				case B: return B_staffUsers;
				default: return staffUsers;
				}
			default: return null;
			}
		}
		
		public boolean primaryAttackIsMagic() {
			return fireUsers.contains(this) || thunderUsers.contains(this) || windUsers.contains(this) || lightUsers.contains(this) || darkUsers.contains(this) || (staffUsers.contains(this) && !primaryAttackIsStrength());
		}
		public boolean primaryAttackIsStrength() {
			return swordUsers.contains(this) || lanceUsers.contains(this) || axeUsers.contains(this) || bowUsers.contains(this);
		}
		
		public boolean canUseWeapon(Item weapon, List<HolyBloodSlot1> blood1, List<HolyBloodSlot2> blood2, List<HolyBloodSlot3> blood3) {
			if (blood1 == null) { blood1 = new ArrayList<HolyBloodSlot1>(); }
			if (blood2 == null) { blood2 = new ArrayList<HolyBloodSlot2>(); }
			if (blood3 == null) { blood3 = new ArrayList<HolyBloodSlot3>(); }
			
			List<HolyBlood> majorBlood = blood1.stream().filter(blood -> (blood.isMajor())).map(slot1 -> (slot1.bloodType())).collect(Collectors.toList());
			majorBlood.addAll(blood2.stream().filter(blood -> (blood.isMajor())).map(slot2 -> (slot2.bloodType())).collect(Collectors.toList()));
			majorBlood.addAll(blood3.stream().filter(blood -> (blood.isMajor())).map(slot3 -> (slot3.bloodType())).collect(Collectors.toList()));
			
			List<HolyBlood> minorBlood = blood1.stream().filter(blood -> (blood.isMajor() == false)).map(slot1 -> (slot1.bloodType())).collect(Collectors.toList());
			minorBlood.addAll(blood2.stream().filter(blood -> (blood.isMajor() == false)).map(slot2 -> (slot2.bloodType())).collect(Collectors.toList()));
			minorBlood.addAll(blood3.stream().filter(blood -> (blood.isMajor() == false)).map(slot3 -> (slot3.bloodType())).collect(Collectors.toList()));
			
			boolean majorSwordBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.BALDR, HolyBlood.HEZUL, HolyBlood.OD));
			boolean minorSwordBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.BALDR, HolyBlood.HEZUL, HolyBlood.OD));
			boolean majorLanceBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.NJORUN, HolyBlood.DAIN));
			boolean minorLanceBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.NJORUN, HolyBlood.DAIN));
			boolean majorAxeBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.NEIR));
			boolean minorAxeBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.NEIR));
			boolean majorBowBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.ULIR));
			boolean minorBowBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.ULIR));
			boolean majorFireBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.FJALAR));
			boolean minorFireBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.FJALAR));
			boolean majorThunderBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.THRUD));
			boolean minorThunderBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.THRUD));
			boolean majorWindBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.FORSETI));
			boolean minorWindBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.FORSETI));
			boolean majorLightBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.NAGA));
			boolean minorLightBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.NAGA));
			boolean majorStaffBlood = !Collections.disjoint(majorBlood, Arrays.asList(HolyBlood.BRAGI));
			boolean minorStaffBlood = !Collections.disjoint(minorBlood, Arrays.asList(HolyBlood.BRAGI));
			
			HolyBlood majorHolyBlood = majorBlood.isEmpty() ? null : majorBlood.get(0);
			
			if (weapon.getType() == ItemType.RING) {
				// Everybody can use rings.
				return true;
			}
			
			if (weapon.getType() == ItemType.SWORD) {
				if (majorSwordBlood && swordUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return swordUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_swordUsers.contains(this) || (swordUsers.contains(this) && minorSwordBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_swordUsers.contains(this) || (B_swordUsers.contains(this) && minorSwordBlood); }
			}
			else if (weapon.getType() == ItemType.LANCE) {
				if (majorLanceBlood && lanceUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return lanceUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_lanceUsers.contains(this) || (lanceUsers.contains(this) && minorLanceBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_lanceUsers.contains(this) || (B_lanceUsers.contains(this) && minorLanceBlood); }
			}
			else if (weapon.getType() == ItemType.AXE) {
				if (majorAxeBlood && axeUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return axeUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_axeUsers.contains(this) || (axeUsers.contains(this) && minorAxeBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_axeUsers.contains(this) || (B_axeUsers.contains(this) && minorAxeBlood); }
			}
			else if (weapon.getType() == ItemType.BOW) {
				if (majorBowBlood && bowUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return bowUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_bowUsers.contains(this) || (bowUsers.contains(this) && minorBowBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_bowUsers.contains(this) || (B_bowUsers.contains(this) && minorBowBlood); }
			}
			else if (weapon.getType() == ItemType.FIRE_MAGIC) {
				if (majorFireBlood && fireUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return fireUsers.contains(this) || (fireUsers.contains(this) && minorFireBlood); }
				if (weapon.getRank() == WeaponRank.B) { return B_fireUsers.contains(this) || (B_fireUsers.contains(this) && minorFireBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_fireUsers.contains(this); }
			}
			else if (weapon.getType() == ItemType.THUNDER_MAGIC) {
				if (majorThunderBlood && thunderUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return thunderUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_thunderUsers.contains(this) || (thunderUsers.contains(this) && minorThunderBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_thunderUsers.contains(this) || (B_thunderUsers.contains(this) && minorThunderBlood); }
			}
			else if (weapon.getType() == ItemType.WIND_MAGIC) {
				if (majorWindBlood && windUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return windUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_windUsers.contains(this) || (windUsers.contains(this) && minorWindBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_windUsers.contains(this) || (B_windUsers.contains(this) && minorWindBlood); }
			}
			else if (weapon.getType() == ItemType.LIGHT_MAGIC) {
				if (majorLightBlood && lightUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return lightUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_lightUsers.contains(this) || (lightUsers.contains(this) && minorLightBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_lightUsers.contains(this) || (B_lightUsers.contains(this) && minorLightBlood); }
			}
			else if (weapon.getType() == ItemType.DARK_MAGIC) {
				if (weapon.getRank() == WeaponRank.C) { return darkUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_darkUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.A) { return A_darkUsers.contains(this); }
			}
			else if (weapon.getType() == ItemType.STAFF) {
				if (majorStaffBlood && staffUsers.contains(this)) { return majorHolyBlood != null ? majorHolyBlood.holyWeapon.ID == weapon.ID : false; }
				if (weapon.getRank() == WeaponRank.C) { return staffUsers.contains(this); }
				if (weapon.getRank() == WeaponRank.B) { return B_staffUsers.contains(this) || (staffUsers.contains(this) && minorStaffBlood); }
				if (weapon.getRank() == WeaponRank.A) { return A_staffUsers.contains(this) || (B_staffUsers.contains(this) && minorStaffBlood); }
			}
			
			return false;
		}
		
		// Note that sameWeapon will result in a class that has at least one weapon shared with the current class.
		// e.g. Calling this on SOCIAL_KNIGHT is going to result in all classes that can use Swords OR Lances.
		public CharacterClass[] getClassPool(boolean sameWeapon, boolean isEnemy, boolean allowSame, boolean isFemale, boolean requireWeakness, boolean requireAttack, boolean requireHorse, boolean requiresMelee, Item mustUseWeapon, Item mustBeWeakAgainstWeapon) {
			return getClassPool(sameWeapon, isEnemy, allowSame, isFemale, requireWeakness, requireAttack, requireHorse, requiresMelee, fliers.contains(this), mustUseWeapon, mustBeWeakAgainstWeapon);
		}
		
		public CharacterClass[] getClassPool(boolean sameWeapon, boolean isEnemy, boolean allowSame, boolean isFemale, boolean requireWeakness, boolean requireAttack, boolean requireHorse, boolean requiresMelee, boolean requiresFlying, Item mustUseWeapon, Item mustBeWeakAgainstWeapon) {
			// Don't touch these. These are generally for bandits raiding villages.
			// Adding pirates here too because Ch. 3 pirates are over water, which makes them stuck.
			if (isEnemy && (this == MOUNTAIN_THIEF || this == PIRATE)) {
				return new CharacterClass[] {};
			}
			
			Set<CharacterClass> workingSet = new HashSet<CharacterClass>();
			if (isPromoted()) { workingSet.addAll(promotedClasses); }
			else { workingSet.addAll(unpromotedClasses); }
			
			if (!isEnemy) { 
				workingSet.removeAll(enemyOnlyClasses);
				// Nobody is going to immediately randomize into advanced classes though.
				workingSet.removeAll(advancedClasses);
			}
			else { workingSet.removeAll(playerOnlyClasses); }
			
			if (!allowSame) { workingSet.remove(this); }
			
			if (sameWeapon) {
				Set<CharacterClass> crossSet = new HashSet<CharacterClass>();
				if (swordUsers.contains(this)) { crossSet.addAll(swordUsers); }
				if (lanceUsers.contains(this)) { crossSet.addAll(lanceUsers); }
				if (axeUsers.contains(this)) { crossSet.addAll(axeUsers); }
				if (bowUsers.contains(this)) { crossSet.addAll(bowUsers); }
				if (fireUsers.contains(this)) { crossSet.addAll(fireUsers); }
				if (thunderUsers.contains(this)) { crossSet.addAll(thunderUsers); }
				if (windUsers.contains(this)) { crossSet.addAll(windUsers); }
				if (lightUsers.contains(this)) { crossSet.addAll(lightUsers); }
				if (darkUsers.contains(this)) { crossSet.addAll(darkUsers); }
				if (staffUsers.contains(this)) { crossSet.addAll(staffUsers); }
				
				workingSet.retainAll(crossSet);
			}
			
			if (mustBeWeakAgainstWeapon != null) {
				if (Item.bows.contains(mustBeWeakAgainstWeapon) || mustBeWeakAgainstWeapon == Item.WING_CLIPPER) {
					workingSet.retainAll(fliers);
				} else if (mustBeWeakAgainstWeapon == Item.HORSESLAYER) {
					workingSet.retainAll(horsebackClasses);
				} else if (mustBeWeakAgainstWeapon == Item.ARMORSLAYER) {
					workingSet.retainAll(armoredClasses);
				} else if (Item.swords.contains(mustBeWeakAgainstWeapon)) {
					workingSet.retainAll(axeUsers);
				} else if (Item.lances.contains(mustBeWeakAgainstWeapon)) {
					workingSet.retainAll(swordUsers);
				} else if (Item.axes.contains(mustBeWeakAgainstWeapon)) {
					workingSet.retainAll(lanceUsers);
				} else if (Item.fireMagic.contains(mustBeWeakAgainstWeapon)) {
					workingSet.retainAll(windUsers);
				} else if (Item.thunderMagic.contains(mustBeWeakAgainstWeapon)) {
					workingSet.retainAll(fireUsers);
				} else if (Item.windMagic.contains(mustBeWeakAgainstWeapon)) {
					workingSet.retainAll(thunderUsers);
				} else if (Item.lightMagic.contains(mustBeWeakAgainstWeapon)) {
					workingSet.retainAll(darkUsers);
				}
			}
			
			if (isFemale) {
				workingSet.removeAll(maleOnlyClasses);
			} else {
				workingSet.removeAll(femaleOnlyClasses);
			}
			
			if (requireWeakness) {
				workingSet.removeAll(noWeaknessClasses);
			}
			
			if (requireAttack) {
				workingSet.removeAll(pacifistClasses);
			}
			
			if (requiresMelee) {
				workingSet.removeAll(rangedOnlyClasses);
			}
		
			if (requiresFlying) { workingSet.retainAll(fliers); }
			
			if (requireHorse) { workingSet.retainAll(horsebackClasses); }
			
			if (mustUseWeapon != null && (mustUseWeapon.isWeapon() || mustUseWeapon.getType() == Item.ItemType.STAFF)) {
				Set<CharacterClass> weaponFilter = new HashSet<CharacterClass>();
				for (CharacterClass charClass : workingSet) {
					// TODO: Pass holy blood from the character in, if we have it.
					// Might be a bit dangerous if we go and later change the holy blood to something else though...
					if (charClass.canUseWeapon(mustUseWeapon, null, null, null)) { weaponFilter.add(charClass); }
				}
				
				workingSet = weaponFilter;
			}
			
			if (isEnemy) {
				// Never return dancer from this.
				workingSet.remove(DANCER);
			}
			
			return workingSet.toArray(new CharacterClass[workingSet.size()]);
		}
		
		public boolean isPromoted() {
			return promotedClasses.contains(this);
		}
		
		public boolean canBeMale() {
			return !femaleOnlyClasses.contains(this);
		}
		
		public boolean canBeFemale() {
			return !maleOnlyClasses.contains(this);
		}
		
		public CharacterClass toMale() {
			if (!femaleOnlyClasses.contains(this)) { return this; }
			switch (this) {
			case PRINCESS: return PRINCE;
			case TROUBADOUR: return SOCIAL_KNIGHT;
			case PALADIN_F: return PALADIN;
			case FALCON_KNIGHT: return DRAGON_MASTER;
			case PEGASUS_KNIGHT: return DRAGON_RIDER;
			case MAGE_FIGHTER_F: return MAGE_FIGHTER;
			case LIGHT_PRIESTESS: return BARD;
			case QUEEN: return EMPEROR;
			default:
				// There's no good option for Dancer, so just return Junior Lord or something.
				if (isPromoted()) { return LORD_KNIGHT; }
				return JUNIOR_LORD;
			}
		}
		
		public CharacterClass toFemale() {
			if (!maleOnlyClasses.contains(this)) { return this; }
			switch (this) {
			case JUNIOR_LORD:
			case PRINCE:
				return PRINCESS; // Close enough for Junior Lord.
			case LORD_KNIGHT:
				return MASTER_KNIGHT;
			case AXE_FIGHTER:
			case BARBARIAN:
			case PIRATE:
			case MOUNTAIN_THIEF:
			case AXE_ARMOR:
				return AXE_KNIGHT;
			case ARMOR:
				return LANCE_KNIGHT;
			case SWORD_ARMOR:
				return SWORD_FIGHTER;
			case WARRIOR: return GREAT_KNIGHT;
			case BOW_ARMOR: return BOW_FIGHTER;
			case DRAGON_RIDER: return PEGASUS_KNIGHT;
			case MAGE_FIGHTER: return MAGE_FIGHTER_F;
			case FREE_KNIGHT: return TROUBADOUR;
			case FORREST_KNIGHT: 
			case PALADIN:
				return PALADIN_F;
			case BISHOP:
			case DARK_MAGE:
				return SAGE;
			default:
				// There shouldn't be anything here, but just return Princess or something if we get here.
				if (isPromoted()) { return MASTER_KNIGHT; }
				return PRINCESS;
			}
		}
		
		public static CharacterClass[] filteredClasses(CharacterClass[] classArray, boolean promoted, boolean isFemale) {
			Set<CharacterClass> classSet = new HashSet<CharacterClass>(Arrays.asList(classArray));
			if (promoted) {
				classSet.removeAll(unpromotedClasses);
			} else {
				classSet.removeAll(promotedClasses);
			}
			
			if (isFemale) {
				classSet.removeAll(maleOnlyClasses);
			} else {
				classSet.removeAll(femaleOnlyClasses);
			}
			
			return classSet.toArray(new CharacterClass[classSet.size()]);
		}
		
		public Item[] criticalWeaknessWeapons() {
			Set<Item> workingSet = new HashSet<Item>();
			if (fliers.contains(this)) { workingSet.addAll(Item.bows); workingSet.add(Item.WING_CLIPPER); }
			if (horsebackClasses.contains(this)) { workingSet.add(Item.HORSESLAYER); }
			if (armoredClasses.contains(this)) { workingSet.add(Item.ARMORSLAYER); }
			return workingSet.toArray(new Item[workingSet.size()]);
		}
		
		public GenderType getGenderType() {
			if (maleOnlyClasses.contains(this)) { return GenderType.MALE_ONLY; }
			if (femaleOnlyClasses.contains(this)) { return GenderType.FEMALE_ONLY; }
			return GenderType.ANY;
		}
		
		public CharacterClass[] demotedClasses(boolean isFemale) {
			if (unpromotedClasses.contains(this)) { return new CharacterClass[] {}; }
			Set<CharacterClass> classSet = new HashSet<CharacterClass>();
			
			switch (this) {
			case LORD_KNIGHT: classSet.add(JUNIOR_LORD); break;
			case DUKE_KNIGHT: classSet.add(LANCE_KNIGHT); break;
			case MASTER_KNIGHT: classSet.add(isFemale ? PRINCESS : PRINCE); break;
			case PALADIN: classSet.add(SOCIAL_KNIGHT); break;
			case PALADIN_F: classSet.add(TROUBADOUR); break;
			case BOW_KNIGHT: classSet.add(ARCH_KNIGHT); break;
			case FORREST_KNIGHT: classSet.add(FREE_KNIGHT); break;
			case MAGE_KNIGHT: classSet.add(MAGE); break;
			case GREAT_KNIGHT: classSet.add(AXE_KNIGHT); break;
			case FALCON_KNIGHT: classSet.add(PEGASUS_KNIGHT); break;
			case DRAGON_MASTER:
				classSet.add(DRAGON_KNIGHT);
				if (!isFemale) { classSet.add(DRAGON_RIDER); }
				break;
			case SWORD_MASTER: 
			case FORREST:
				classSet.add(SWORD_FIGHTER); 
				break;
			case SNIPER: classSet.add(BOW_FIGHTER); break;
			case GENERAL: classSet.addAll(Arrays.asList(ARMOR, SWORD_ARMOR, AXE_ARMOR, BOW_ARMOR)); break;
			case WARRIOR: classSet.addAll(Arrays.asList(HUNTER, BARBARIAN, MOUNTAIN_THIEF, PIRATE, AXE_FIGHTER)); break;
			case MAGE_FIGHTER:
			case MAGE_FIGHTER_F:
				classSet.addAll(Arrays.asList(MAGE, FIRE_MAGE, THUNDER_MAGE, WIND_MAGE)); 
				break;
			case HIGH_PRIEST:
			case BISHOP: classSet.add(PRIEST); break;
			case SAGE: classSet.addAll(Arrays.asList(BARD, LIGHT_PRIESTESS)); break;
			case THIEF_FIGHTER: classSet.add(THIEF); break;
			case DARK_BISHOP: classSet.add(DARK_MAGE); break;
			default: break;
			}
			
			List<CharacterClass> classList = classSet.stream().map(charClass -> (isFemale ? charClass.toFemale() : charClass.toMale())).distinct().sorted(new Comparator<CharacterClass>() {
				@Override
				public int compare(CharacterClass arg0, CharacterClass arg1) {
					return Integer.compare(arg0.ID, arg1.ID);
				}
			}).collect(Collectors.toList());
			
			return classList.toArray(new CharacterClass[classList.size()]);
		}
		
		public CharacterClass[] promotionClasses(boolean isFemale) {
			if (promotedClasses.contains(this)) { return new CharacterClass[] {}; }
			Set<CharacterClass> classSet = new HashSet<CharacterClass>();
			switch (this) {
			case SOCIAL_KNIGHT: classSet.add(isFemale ? PALADIN_F : PALADIN); break; 
			case LANCE_KNIGHT: classSet.add(DUKE_KNIGHT); break; 
			case ARCH_KNIGHT: classSet.add(BOW_KNIGHT); break;
			case AXE_KNIGHT: classSet.add(GREAT_KNIGHT); break;
			case FREE_KNIGHT: classSet.add(FORREST_KNIGHT); break;
			case TROUBADOUR: classSet.add(PALADIN_F); break; 
			case PEGASUS_KNIGHT: classSet.add(FALCON_KNIGHT); break;
			case DRAGON_RIDER:
			case DRAGON_KNIGHT:
				classSet.add(DRAGON_MASTER); 
				break;
			case BOW_FIGHTER: classSet.add(SNIPER); break;
			case SWORD_FIGHTER: classSet.addAll(Arrays.asList(SWORD_MASTER, FORREST)); break;
			case ARMOR:
			case AXE_ARMOR:
			case BOW_ARMOR:
			case SWORD_ARMOR:
				classSet.add(GENERAL);
				break;
			case BARBARIAN:
			case MOUNTAIN_THIEF:
			case PIRATE:
			case HUNTER:
			case AXE_FIGHTER: 
				classSet.add(WARRIOR);
				break;
			case JUNIOR_LORD: classSet.add(LORD_KNIGHT); break;
			case PRINCE:
			case PRINCESS:
				classSet.add(MASTER_KNIGHT);
				break;
			case PRIEST: classSet.add(HIGH_PRIEST); break;
			case MAGE:
			case FIRE_MAGE:
			case THUNDER_MAGE:
			case WIND_MAGE: 
				classSet.addAll(Arrays.asList(MAGE_KNIGHT, isFemale ? MAGE_FIGHTER_F : MAGE_FIGHTER)); 
				break;
			case BARD:
			case LIGHT_PRIESTESS: 
				classSet.add(SAGE);
				break;
			case THIEF: classSet.add(THIEF_FIGHTER); break;
			case DARK_MAGE: classSet.add(DARK_BISHOP); break;
			default: break;
			}
			
			List<CharacterClass> classList = classSet.stream().map(charClass -> (isFemale ? charClass.toFemale() : charClass.toMale())).distinct().sorted(new Comparator<CharacterClass>() {
				@Override
				public int compare(CharacterClass arg0, CharacterClass arg1) {
					return Integer.compare(arg0.ID, arg1.ID);
				}
			}).collect(Collectors.toList());
			
			return classList.toArray(new CharacterClass[classList.size()]);
		}
		
		public CharacterClass[] getLoosePromotionOptions(boolean isFemale, boolean includeAlternateMounts, boolean includeEnemyClasses, List<HolyBloodSlot1> slot1Blood, List<HolyBloodSlot2> slot2Blood, List<HolyBloodSlot3> slot3Blood) {
			List<CharacterClass> resultList = new ArrayList<CharacterClass>();
			
			if (slot1Blood == null) { slot1Blood = new ArrayList<HolyBloodSlot1>(); }
			if (slot2Blood == null) { slot2Blood = new ArrayList<HolyBloodSlot2>(); }
			if (slot3Blood == null) { slot3Blood = new ArrayList<HolyBloodSlot3>(); }
			
			switch (this) {
			case SOCIAL_KNIGHT:
				resultList.add(isFemale ? PALADIN_F : PALADIN);
				if (!isFemale) { resultList.add(LORD_KNIGHT); }
				resultList.add(MASTER_KNIGHT);
				if (includeAlternateMounts) {
					resultList.add(DRAGON_MASTER);
					if (isFemale) { resultList.add(FALCON_KNIGHT); }
				}
				break;
			case LANCE_KNIGHT:
				if (!isFemale) { resultList.addAll(Arrays.asList(PALADIN, LORD_KNIGHT)); }
				resultList.addAll(Arrays.asList(DUKE_KNIGHT, MASTER_KNIGHT));
				if (isFemale && (slot1Blood.contains(HolyBloodSlot1.MAJOR_DAIN) || slot1Blood.contains(HolyBloodSlot1.MAJOR_NJORUN))) { resultList.add(PALADIN_F); }
				if (includeAlternateMounts) {
					resultList.add(DRAGON_MASTER);
					if (isFemale) { resultList.add(FALCON_KNIGHT); }
				}
				break;
			case ARCH_KNIGHT:
				resultList.addAll(Arrays.asList(BOW_KNIGHT, MASTER_KNIGHT));
				break;
			case AXE_KNIGHT:
				resultList.addAll(Arrays.asList(GREAT_KNIGHT, MASTER_KNIGHT));
				break;
			case FREE_KNIGHT:
				assert !isFemale : "Free Knights can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(FORREST_KNIGHT, PALADIN, LORD_KNIGHT, MASTER_KNIGHT));
				if (includeAlternateMounts) { resultList.add(DRAGON_MASTER); }
				break;
			case TROUBADOUR:
				assert isFemale : "Troubadours can only be loosely promoted by females.";
				resultList.addAll(Arrays.asList(PALADIN_F, MASTER_KNIGHT));
				if (includeAlternateMounts) { resultList.add(FALCON_KNIGHT); }
				break;
			case PEGASUS_KNIGHT:
				assert isFemale : "Pegasus Knights can only be loosely promoted by females.";
				resultList.add(FALCON_KNIGHT);
				if (includeAlternateMounts) { resultList.add(MASTER_KNIGHT); }
				break;
			case DRAGON_RIDER:
				assert !isFemale : "Dragon Rider can only be loosely promoted by males.";
				resultList.add(DRAGON_MASTER);
				if (includeAlternateMounts) { resultList.addAll(Arrays.asList(PALADIN, MASTER_KNIGHT)); }
				break;
			case DRAGON_KNIGHT:
				resultList.add(DRAGON_MASTER);
				if (includeAlternateMounts) { 
					resultList.add(MASTER_KNIGHT);
				}
				break;
			case BOW_FIGHTER:
				resultList.addAll(Arrays.asList(SNIPER, MASTER_KNIGHT));
				break;
			case SWORD_FIGHTER:
				resultList.addAll(Arrays.asList(SWORD_MASTER, FORREST, MASTER_KNIGHT));
				break;
			case ARMOR:
				assert !isFemale : "Armors can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(GENERAL, DRAGON_MASTER, MASTER_KNIGHT));
				if (includeEnemyClasses) { resultList.addAll(Arrays.asList(BARON, EMPEROR)); }
				break;
			case AXE_ARMOR:
				assert !isFemale : "Axe Armors can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(GENERAL, GREAT_KNIGHT, MASTER_KNIGHT));
				if (includeEnemyClasses) { resultList.addAll(Arrays.asList(BARON, EMPEROR)); }
				break;
			case BOW_ARMOR:
				assert !isFemale : "Bow Armors can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(GENERAL, MASTER_KNIGHT));
				if (includeEnemyClasses) { resultList.addAll(Arrays.asList(BARON, EMPEROR)); }
				break;
			case SWORD_ARMOR:
				assert !isFemale : "Sword Armors can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(GENERAL, DRAGON_MASTER, MASTER_KNIGHT));
				if (includeEnemyClasses) { resultList.addAll(Arrays.asList(BARON, EMPEROR)); }
				break;
			case AXE_FIGHTER:
				assert !isFemale : "Axe Fighters can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(WARRIOR, MASTER_KNIGHT));
				break;
			case JUNIOR_LORD:
				assert !isFemale : "Junior Lords can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(PALADIN, LORD_KNIGHT, FORREST_KNIGHT, DRAGON_MASTER, FORREST, SWORD_MASTER, THIEF_FIGHTER, MAGE_KNIGHT, MASTER_KNIGHT));
				if (slot1Blood.contains(HolyBloodSlot1.MAJOR_BALDR) || slot2Blood.contains(HolyBloodSlot2.MAJOR_OD) || slot3Blood.contains(HolyBloodSlot3.MAJOR_HEZUL)) {
					resultList.add(MAGE_FIGHTER);
				}
				break;
			case PRINCE:
				assert !isFemale : "Princes can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(LORD_KNIGHT, FORREST_KNIGHT, DRAGON_MASTER, FORREST, SWORD_MASTER, MASTER_KNIGHT));
				if (slot1Blood.contains(HolyBloodSlot1.MINOR_BALDR) || slot1Blood.contains(HolyBloodSlot1.MAJOR_BALDR) ||
						slot2Blood.contains(HolyBloodSlot2.MINOR_OD) || slot2Blood.contains(HolyBloodSlot2.MAJOR_OD) ||
						slot3Blood.contains(HolyBloodSlot3.MINOR_HEZUL) || slot3Blood.contains(HolyBloodSlot3.MAJOR_HEZUL)) {
					resultList.add(PALADIN);
				}
				break;
			case PRINCESS:
				assert isFemale : "Princesses can only be loosely promoted by females.";
				resultList.addAll(Arrays.asList(PALADIN_F, FALCON_KNIGHT, MASTER_KNIGHT));
				break;
			case PRIEST:
				resultList.addAll(Arrays.asList(HIGH_PRIEST, SAGE, MASTER_KNIGHT));
				if (isFemale && slot3Blood.contains(HolyBloodSlot3.MAJOR_BRAGI)) { resultList.add(FALCON_KNIGHT); }
				if (includeEnemyClasses) { resultList.addAll(Arrays.asList(BARON, DARK_BISHOP, EMPEROR)); }
				if (isFemale && includeEnemyClasses) { resultList.add(QUEEN); }
				break;
			case MAGE:
				resultList.addAll(Arrays.asList(SAGE, MAGE_KNIGHT, MASTER_KNIGHT, (isFemale ? MAGE_FIGHTER_F : MAGE_FIGHTER)));
				if (includeEnemyClasses) { resultList.addAll(Arrays.asList(BARON, DARK_BISHOP, EMPEROR)); }
				if (isFemale && includeEnemyClasses) { resultList.add(QUEEN); }
				break;
			case FIRE_MAGE:
				resultList.addAll(Arrays.asList(SAGE, MAGE_KNIGHT, MASTER_KNIGHT, (isFemale ? MAGE_FIGHTER_F : MAGE_FIGHTER)));
				if (includeEnemyClasses) { resultList.addAll(Arrays.asList(DARK_BISHOP, EMPEROR)); }
				if (isFemale && includeEnemyClasses) { resultList.add(QUEEN); }
				break;
			case THUNDER_MAGE:
			case WIND_MAGE:
				resultList.addAll(Arrays.asList(SAGE, MASTER_KNIGHT, (isFemale ? MAGE_FIGHTER_F : MAGE_FIGHTER)));
				if (includeEnemyClasses) { resultList.addAll(Arrays.asList(DARK_BISHOP, EMPEROR)); }
				if (isFemale && includeEnemyClasses) { resultList.add(QUEEN); }
				break;
			case BARD:
				resultList.addAll(Arrays.asList(SAGE, MASTER_KNIGHT));
				break;
			case LIGHT_PRIESTESS:
				assert isFemale : "Light Priestess can only be loosely promoted by females.";
				resultList.add(SAGE);
				break;
			case THIEF:
				resultList.add(THIEF_FIGHTER);
				break;
			case DARK_MAGE:
				assert !isFemale : "Dark Mages can only be loosely promoted by males.";
				resultList.add(DARK_BISHOP);
				break;
			case HUNTER:
				assert !isFemale : "Hunters can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(SNIPER, BOW_KNIGHT, MASTER_KNIGHT));
				if (slot2Blood.contains(HolyBloodSlot2.MINOR_ULIR) || slot2Blood.contains(HolyBloodSlot2.MAJOR_ULIR)) { resultList.add(WARRIOR); }
				break;
			case MOUNTAIN_THIEF:
			case PIRATE:
			case BARBARIAN:
				assert !isFemale : "Mountain Thieves, Barbarians, and Pirates can only be loosely promoted by males.";
				resultList.addAll(Arrays.asList(WARRIOR, GREAT_KNIGHT, MASTER_KNIGHT));
				break;
			default:
				break;
			}
			
			if (resultList.isEmpty()) {
				return promotionClasses(isFemale);
			}
			
			return resultList.toArray(new CharacterClass[resultList.size()]);
		}
		
		public CharacterClass[] sharedWeaponPromotions(boolean isFemale) {
			Set<CharacterClass> promotions = new HashSet<CharacterClass>();
			if (swordUsers.contains(this)) { promotions.addAll(swordUsers); }
			if (lanceUsers.contains(this)) { promotions.addAll(lanceUsers); }
			if (axeUsers.contains(this)) { promotions.addAll(axeUsers); }
			if (bowUsers.contains(this)) { promotions.addAll(bowUsers); }
			if (fireUsers.contains(this)) { promotions.addAll(fireUsers); }
			if (thunderUsers.contains(this)) { promotions.addAll(thunderUsers); }
			if (windUsers.contains(this)) { promotions.addAll(windUsers); }
			if (lightUsers.contains(this)) { promotions.addAll(lightUsers); }
			if (darkUsers.contains(this)) { promotions.addAll(darkUsers); }
			if (staffUsers.contains(this)) { promotions.addAll(staffUsers); }
			
			promotions.removeAll(unpromotedClasses);
			if (isFemale) {
				promotions.removeAll(maleOnlyClasses);
			} else {
				promotions.removeAll(femaleOnlyClasses);
			}
			
			return promotions.toArray(new CharacterClass[promotions.size()]);
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
		
		IRON_BOW(0x31), STEEL_BOW(0x32), SILVER_BOW(0x33), BRAVE_BOW(0x34), KILLER_BOW(0x35), YEWFELLE(0x36), BROKEN_BOW_A(0x37), BROKEN_BOW_B(0x38), BROKEN_BOW_C(0x39),
		
		// Ballistas here, which we don't care about.
		
		FIRE(0x3E), ELFIRE(0x3F), BOLGANONE(0x40), VALFLAME(0x41), METEOR(0x42),
		
		THUNDER(0x43), ELTHUNDER(0x44), THORON(0x45), MJOLNIR(0x46), BOLTING(0x47),
		
		WIND(0x48), ELWIND(0x49), TORNADO(0x4A), FORSETI(0x4B), BLIZZARD(0x4C),
		
		LIGHT(0x4D), NOSFERATU(0x4E), AURA(0x4F), NAGA(0x50),
		
		YOTSMUNGAND(0x51), FENRIR(0x52), HEL(0x53), LOPTYR(0x54),
		
		EMPTY_BOOK_A_FIRE(0x55), EMPTY_BOOK_B_FIRE(0x56), EMPTY_BOOK_C_FIRE(0x57),
		
		HEAL(0x58), MEND(0x59), RECOVER(0x5A), PHYSIC(0x5B), FORTIFY(0x5C), RETURN(0x5D), WARP(0x5E), RESCUE(0x5F), RESTORE(0x61), VALKYRIE(0x62), SILENCE(0x63), SLEEP(0x64), BERSERK(0x65),
		BROKEN_STAFF_A(0x67), BROKEN_STAFF_B(0x68), BROKEN_STAFF_C(0x69),
		
		LIFE_RING(0x6A), ELITE_RING(0x6B), THIEF_RING(0x6C), PRAYER_RING(0x6D), PURSUIT_RING(0x6E), RECOVER_RING(0x6F),
		BARGAIN_RING(0x70), KNIGHT_RING(0x71), RETURN_RING(0x72), SPEED_RING(0x73), MAGIC_RING(0x74), POWER_RING(0x75),
		SHIELD_RING(0x76), BARRIER_RING(0x77), LEG_RING(0x78), SKILL_RING(0x79),
		
		BROKEN_HOLY_SWORD(0x7A), BROKEN_HOLY_SPEAR(0x7B), BROKEN_HOLY_BOW(0x7C),
		
		EMPTY_BOOK_A_THUNDER(0x7D), EMPTY_BOOK_B_THUNDER(0x7E), EMPTY_BOOK_C_THUNDER(0x7F),
		EMPTY_BOOK_S_WIND(0x80), EMPTY_BOOK_A_WIND(0x81), EMPTY_BOOK_B_WIND(0x82), EMPTY_BOOK_C_WIND(0x83), 
		EMPTY_BOOK_S_LIGHT(0x84), EMPTY_BOOK_A_LIGHT(0x85), EMPTY_BOOK_B_LIGHT(0x86), EMPTY_BOOK_C_LIGHT(0x87),
		
		BROKEN_HOLY_STAFF(0x88),
		
		CIRCLET(0x89)
		;
		
		public static Comparator<Item> defaultComparator = new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				return Integer.compare(o1.ID, o2.ID);
			}
		};
		
		// Silence sword doesn't actually work properly, so we'll remove it from the list for now (unless we can hack it into working properly...)
		public static final Set<Item> swords = new HashSet<Item>(Arrays.asList(IRON_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, MIRACLE_SWORD,
				THIEF_SWORD, BARRIER_BLADE, BERSERK_SWORD, BRAVE_SWORD, /*SILENCE_SWORD,*/ SLEEP_SWORD, SLIM_SWORD,
				SAFEGUARD, FLAME_SWORD, EARTH_SWORD, LEVIN_SWORD, WIND_SWORD, LIGHT_BRAND, MYSTLETAINN, TYRFING,
				BALMUNG, ARMORSLAYER, WING_CLIPPER, BROKEN_SWORD_C, BROKEN_SWORD_B, BROKEN_SWORD_A, BROKEN_HOLY_SWORD));
		public static final Set<Item> lances = new HashSet<Item>(Arrays.asList(IRON_LANCE, STEEL_LANCE, SILVER_LANCE, JAVELIN, HORSESLAYER, BRAVE_LANCE, SLIM_LANCE,
				GUNGNIR, GAE_BOLG, BROKEN_LANCE_C, BROKEN_LANCE_B, BROKEN_LANCE_A, BROKEN_HOLY_SPEAR));
		public static final Set<Item> axes = new HashSet<Item>(Arrays.asList(IRON_AXE, STEEL_AXE, SILVER_AXE, BRAVE_AXE, HELSWATH, HAND_AXE, BROKEN_AXE_C, BROKEN_AXE_B, BROKEN_AXE_A));
		public static final Set<Item> bows = new HashSet<Item>(Arrays.asList(IRON_BOW, STEEL_BOW, SILVER_BOW, BRAVE_BOW, KILLER_BOW, YEWFELLE, BROKEN_BOW_C, BROKEN_BOW_B, BROKEN_BOW_A, BROKEN_HOLY_BOW));
		public static final Set<Item> fireMagic = new HashSet<Item>(Arrays.asList(FIRE, ELFIRE, BOLGANONE, VALFLAME, METEOR, EMPTY_BOOK_C_FIRE, EMPTY_BOOK_B_FIRE, EMPTY_BOOK_A_FIRE));
		public static final Set<Item> thunderMagic = new HashSet<Item>(Arrays.asList(THUNDER, ELTHUNDER, THORON, MJOLNIR, BOLTING, EMPTY_BOOK_C_THUNDER, EMPTY_BOOK_B_THUNDER, EMPTY_BOOK_A_THUNDER));
		public static final Set<Item> windMagic = new HashSet<Item>(Arrays.asList(WIND, ELWIND, TORNADO, FORSETI, BLIZZARD, EMPTY_BOOK_C_WIND, EMPTY_BOOK_B_WIND, EMPTY_BOOK_A_WIND, EMPTY_BOOK_S_WIND));
		public static final Set<Item> lightMagic = new HashSet<Item>(Arrays.asList(LIGHT, NOSFERATU, AURA, NAGA, EMPTY_BOOK_C_LIGHT, EMPTY_BOOK_B_LIGHT, EMPTY_BOOK_A_LIGHT, EMPTY_BOOK_S_LIGHT));
		public static final Set<Item> darkMagic = new HashSet<Item>(Arrays.asList(YOTSMUNGAND, FENRIR, HEL, LOPTYR));
		public static final Set<Item> staves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, PHYSIC, FORTIFY, RETURN, WARP, RESCUE, RESTORE, VALKYRIE, SILENCE, SLEEP, BERSERK, BROKEN_STAFF_C, BROKEN_STAFF_B, BROKEN_STAFF_A, BROKEN_HOLY_STAFF));
		
		public static final Set<Item> healingStaves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, PHYSIC, FORTIFY));
		public static final Set<Item> siegeTomes = new HashSet<Item>(Arrays.asList(METEOR, BOLTING, BLIZZARD, FENRIR));
		
		public static final Set<Item> meleeStaves = new HashSet<Item>(Arrays.asList(HEAL, MEND, RECOVER, RETURN, WARP, RESTORE));
		
		public static final Set<Item> brokenWeapons = new HashSet<Item>(Arrays.asList(BROKEN_SWORD_A, BROKEN_SWORD_B, BROKEN_SWORD_C, BROKEN_LANCE_A, BROKEN_LANCE_B, BROKEN_LANCE_C, BROKEN_AXE_A, BROKEN_AXE_B, BROKEN_AXE_C,
				BROKEN_BOW_A, BROKEN_BOW_B, BROKEN_BOW_C, BROKEN_STAFF_A, BROKEN_STAFF_B, BROKEN_STAFF_C, EMPTY_BOOK_A_FIRE, EMPTY_BOOK_A_THUNDER, EMPTY_BOOK_A_WIND, EMPTY_BOOK_A_LIGHT, EMPTY_BOOK_B_FIRE, EMPTY_BOOK_B_THUNDER, 
				EMPTY_BOOK_B_WIND, EMPTY_BOOK_B_LIGHT, EMPTY_BOOK_C_FIRE, EMPTY_BOOK_C_THUNDER, EMPTY_BOOK_C_WIND, EMPTY_BOOK_C_LIGHT, 
				EMPTY_BOOK_S_WIND, EMPTY_BOOK_S_LIGHT, BROKEN_HOLY_SWORD, BROKEN_HOLY_SPEAR, BROKEN_HOLY_BOW, BROKEN_HOLY_STAFF));
		
		public static final Set<Item> meleeWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, MIRACLE_SWORD, THIEF_SWORD, BARRIER_BLADE,
				BERSERK_SWORD, BRAVE_SWORD, /*SILENCE_SWORD,*/ SLEEP_SWORD, SLIM_SWORD, SAFEGUARD, FLAME_SWORD, EARTH_SWORD, LEVIN_SWORD, WIND_SWORD, LIGHT_BRAND, MYSTLETAINN, TYRFING, BALMUNG, ARMORSLAYER, WING_CLIPPER,
				IRON_LANCE, STEEL_LANCE, SILVER_LANCE, JAVELIN, HORSESLAYER, BRAVE_LANCE, SLIM_LANCE, GUNGNIR, GAE_BOLG,
				IRON_AXE, STEEL_AXE, SILVER_AXE, BRAVE_AXE, HELSWATH, HAND_AXE,
				FIRE, ELFIRE, BOLGANONE, VALFLAME,
				THUNDER, ELTHUNDER, THORON, MJOLNIR,
				WIND, ELWIND, TORNADO, FORSETI,
				LIGHT, NOSFERATU, AURA, NAGA,
				YOTSMUNGAND
				));
		public static final Set<Item> rangedWeapons = new HashSet<Item>(Arrays.asList(FLAME_SWORD, EARTH_SWORD, LEVIN_SWORD, WIND_SWORD, LIGHT_BRAND, JAVELIN, HAND_AXE, HELSWATH, IRON_BOW, STEEL_BOW, SILVER_BOW,
				BRAVE_BOW, KILLER_BOW, YEWFELLE,
				FIRE, ELFIRE, BOLGANONE, VALFLAME,
				THUNDER, ELTHUNDER, THORON, MJOLNIR,
				WIND, ELWIND, TORNADO, FORSETI,
				LIGHT, NOSFERATU, AURA, NAGA,
				YOTSMUNGAND));
		
		public static final Set<Item> normalWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, STEEL_SWORD, SILVER_SWORD, IRON_BLADE, SILVER_BLADE, SLIM_SWORD, IRON_LANCE, STEEL_LANCE, SILVER_LANCE, JAVELIN,
				SLIM_LANCE, IRON_AXE, STEEL_AXE, SILVER_AXE, HAND_AXE, IRON_BOW, STEEL_BOW, SILVER_BOW, FIRE, ELFIRE, WIND, ELWIND, THUNDER, ELTHUNDER, LIGHT, YOTSMUNGAND));
		public static final Set<Item> interestingWeapons = new HashSet<Item>(Arrays.asList(MIRACLE_SWORD, THIEF_SWORD,
				BARRIER_BLADE, BERSERK_SWORD, BRAVE_SWORD, /*SILENCE_SWORD,*/ SLEEP_SWORD, SLIM_SWORD, SAFEGUARD,
				FLAME_SWORD, EARTH_SWORD, LEVIN_SWORD, WIND_SWORD, LIGHT_BRAND, ARMORSLAYER, WING_CLIPPER, BRAVE_AXE, 
				BRAVE_LANCE, HORSESLAYER, BRAVE_BOW, KILLER_BOW, NOSFERATU, HEL));
		
		public static final Set<Item> powerfulWeapons = new HashSet<Item>(Arrays.asList(SILVER_SWORD, STEEL_BLADE, SILVER_BLADE, SILVER_LANCE,
				SILVER_AXE, SILVER_BOW, BOLGANONE, THORON, TORNADO, AURA, FENRIR));
		
		public static final Set<Item> ironSet = new HashSet<Item>(Arrays.asList(IRON_SWORD, IRON_BLADE, SLIM_SWORD, IRON_LANCE, SLIM_LANCE, IRON_AXE, IRON_BOW, FIRE, WIND, THUNDER, LIGHT, YOTSMUNGAND, HEAL));
		public static final Set<Item> steelSet = new HashSet<Item>(Arrays.asList(STEEL_SWORD, STEEL_BLADE, STEEL_LANCE, STEEL_AXE, STEEL_BOW, ELFIRE, ELTHUNDER, ELWIND, LIGHT, YOTSMUNGAND, MEND));
		public static final Set<Item> silverSet = new HashSet<Item>(Arrays.asList(SILVER_SWORD, SILVER_BLADE, SILVER_LANCE, SILVER_AXE, SILVER_BOW, BOLGANONE, THORON, TORNADO, AURA, YOTSMUNGAND, RECOVER));
		public static final Set<Item> rangedSet = new HashSet<Item>(Arrays.asList(FLAME_SWORD, LEVIN_SWORD, WIND_SWORD, LIGHT_BRAND, JAVELIN, HAND_AXE, STEEL_BOW, ELFIRE, ELTHUNDER, ELWIND, LIGHT, YOTSMUNGAND, PHYSIC));
		public static final Set<Item> effectiveSet = new HashSet<Item>(Arrays.asList(WING_CLIPPER, ARMORSLAYER, HORSESLAYER, HAND_AXE, KILLER_BOW, ELFIRE, ELWIND, ELTHUNDER, LIGHT, YOTSMUNGAND, RECOVER));
		public static final Set<Item> braveSet = new HashSet<Item>(Arrays.asList(BRAVE_SWORD, BRAVE_LANCE, BRAVE_AXE, BRAVE_BOW, ELFIRE, ELWIND, ELTHUNDER, LIGHT, YOTSMUNGAND, RECOVER));
		
		public static final Set<Item> statusSet = new HashSet<Item>(Arrays.asList(THIEF_SWORD, BERSERK_SWORD, /*SILENCE_SWORD,*/ SLEEP_SWORD));
		
		public static final Set<Item> playerOnlySet = new HashSet<Item>(Arrays.asList(BERSERK, BERSERK_SWORD, RETURN, WARP));
		
		public static final Set<Item> rings = new HashSet<Item>(Arrays.asList(LIFE_RING, ELITE_RING, THIEF_RING, PRAYER_RING, PURSUIT_RING, RECOVER_RING, BARGAIN_RING, 
				KNIGHT_RING, RETURN_RING, SPEED_RING, MAGIC_RING, POWER_RING, SHIELD_RING, BARRIER_RING, LEG_RING, SKILL_RING));
		
		public static final Set<Item> statRings = new HashSet<Item>(Arrays.asList(SPEED_RING, MAGIC_RING, POWER_RING, SHIELD_RING, BARRIER_RING, SKILL_RING));
		
		public static final Set<Item> blacklistedRings = new HashSet<Item>(Arrays.asList(RECOVER_RING));
		
		public static final Set<Item> cWeapons = new HashSet<Item>(Arrays.asList(IRON_SWORD, MIRACLE_SWORD, THIEF_SWORD, BARRIER_BLADE, BERSERK_SWORD, /*SILENCE_SWORD,*/ SLEEP_SWORD, SLIM_SWORD, SAFEGUARD, FLAME_SWORD,
				EARTH_SWORD, LEVIN_SWORD, WIND_SWORD, LIGHT_BRAND, IRON_LANCE, JAVELIN, HORSESLAYER, SLIM_LANCE, IRON_AXE, HAND_AXE, IRON_BOW, KILLER_BOW, FIRE, METEOR, THUNDER, BOLTING, WIND, BLIZZARD, LIGHT, YOTSMUNGAND,
				HEAL, MEND, RETURN, BROKEN_SWORD_C, BROKEN_LANCE_C, BROKEN_AXE_C, BROKEN_BOW_C, EMPTY_BOOK_C_FIRE, EMPTY_BOOK_C_THUNDER, EMPTY_BOOK_C_WIND, EMPTY_BOOK_C_LIGHT, BROKEN_STAFF_C));
		public static final Set<Item> bWeapons = new HashSet<Item>(Arrays.asList(STEEL_SWORD, BRAVE_SWORD, ARMORSLAYER, WING_CLIPPER, STEEL_LANCE, BRAVE_LANCE, STEEL_AXE, BRAVE_AXE, STEEL_BOW, BRAVE_BOW, ELFIRE, 
				ELTHUNDER, ELWIND, FENRIR, HEL, RECOVER, PHYSIC, WARP, RESTORE, SILENCE, SLEEP, BERSERK, BROKEN_SWORD_B, BROKEN_LANCE_B, BROKEN_AXE_B, BROKEN_BOW_B, EMPTY_BOOK_B_FIRE, EMPTY_BOOK_B_THUNDER, 
				EMPTY_BOOK_B_WIND, EMPTY_BOOK_B_LIGHT, BROKEN_STAFF_B));
		public static final Set<Item> aWeapons = new HashSet<Item>(Arrays.asList(SILVER_SWORD, IRON_BLADE, STEEL_BLADE, SILVER_BLADE, SILVER_LANCE, SILVER_AXE, SILVER_BOW, BOLGANONE, THORON, TORNADO, NOSFERATU,
				AURA, FORTIFY, RESCUE, BROKEN_SWORD_A, BROKEN_LANCE_A, BROKEN_AXE_A, BROKEN_BOW_A, EMPTY_BOOK_A_FIRE, EMPTY_BOOK_A_THUNDER, EMPTY_BOOK_A_WIND, EMPTY_BOOK_A_LIGHT, BROKEN_STAFF_A));
		public static final Set<Item> holyWeapons = new HashSet<Item>(Arrays.asList(MYSTLETAINN, TYRFING, BALMUNG, GUNGNIR, GAE_BOLG, HELSWATH, YEWFELLE, VALFLAME, MJOLNIR, FORSETI, NAGA, LOPTYR, VALKYRIE,
				BROKEN_HOLY_SWORD, BROKEN_HOLY_SPEAR, BROKEN_HOLY_BOW, EMPTY_BOOK_S_WIND, EMPTY_BOOK_S_LIGHT, BROKEN_HOLY_STAFF));
		
		public static final Set<Item> femaleOnlyWeapons = new HashSet<Item>(Arrays.asList(MIRACLE_SWORD));
		
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
		
		public enum WeaponRank {
			NONE, C, B, A, PRF;
			
			public boolean isHigher(WeaponRank other) {
				switch (this) {
				case NONE:
				case C: return false;
				case B: return other == C;
				case A: return other == C || other == B;
				case PRF: return other != PRF;
				default: return false;
				}
			}
			
			public String displayString() {
				switch (this) {
				case NONE: return "-";
				case C: return "C";
				case B: return "B";
				case A: return "A";
				case PRF: return "S";
				}
				
				return "?";
			}
		}
		
		public enum ItemType {
			NONE, RING, SWORD, LANCE, AXE, BOW, FIRE_MAGIC, THUNDER_MAGIC, WIND_MAGIC, LIGHT_MAGIC, DARK_MAGIC, STAFF;
			
			public Item getBasic() {
				switch (this) {
				case SWORD: return IRON_SWORD;
				case LANCE: return IRON_LANCE;
				case AXE: return IRON_AXE;
				case BOW: return IRON_BOW;
				case FIRE_MAGIC: return FIRE;
				case THUNDER_MAGIC: return THUNDER;
				case WIND_MAGIC: return WIND;
				case LIGHT_MAGIC: return LIGHT;
				case DARK_MAGIC: return YOTSMUNGAND;
				case STAFF: return HEAL;
				default: return null;
				}
			}
			
			public boolean isPhysical() {
				switch (this) {
				case SWORD:
				case LANCE:
				case AXE:
				case BOW:
					return true;
				default:
					return false;
				}
			}
		}
		
		public static Item[] weaponsOfTypeAndRank(ItemType type, WeaponRank rank, boolean includeLowerRank) {
			if (type == null || rank == null || rank == WeaponRank.NONE) { return new Item[] {}; }
			Set<Item> items = new HashSet<Item>();
			switch (type) {
			case SWORD:
				items.addAll(swords);
				break;
			case LANCE:
				items.addAll(lances);
				break;
			case AXE:
				items.addAll(axes);
				break;
			case BOW:
				items.addAll(bows);
				break;
			case FIRE_MAGIC:
				items.addAll(fireMagic);
				break;
			case THUNDER_MAGIC:
				items.addAll(thunderMagic);
				break;
			case WIND_MAGIC:
				items.addAll(windMagic);
				break;
			case LIGHT_MAGIC:
				items.addAll(lightMagic);
				break;
			case DARK_MAGIC:
				items.addAll(darkMagic);
				break;
			case STAFF:
				items.addAll(staves);
				break;
			default: 
				break;
			}
			
			switch (rank) {
			case C:
				items.removeAll(bWeapons);
				items.removeAll(aWeapons);
				items.removeAll(holyWeapons);
				break;
			case B:
				if (!includeLowerRank) { items.removeAll(cWeapons); }
				items.removeAll(aWeapons);
				items.removeAll(holyWeapons);
				break;
			case A:
				if (!includeLowerRank) {
					items.removeAll(cWeapons);
					items.removeAll(bWeapons);
				}
				items.removeAll(holyWeapons);
				break;
			case PRF:
				if (!includeLowerRank) {
					items.removeAll(cWeapons);
					items.removeAll(bWeapons);
					items.removeAll(aWeapons);
				}
				break;
			default:
				break;
			}
			
			items.removeAll(siegeTomes); // Don't give out these.
			items.removeAll(brokenWeapons); // Nor these (unless explicitly asked for)
			
			return items.toArray(new Item[items.size()]);
		}
		
		public static Item getBrokenWeapon(ItemType type, WeaponRank rank) {
			switch (type) {
			case AXE:
				switch (rank) {
				case PRF:
				case A:
					return BROKEN_AXE_A;
				case B:
					return BROKEN_AXE_B;
				default:
					return BROKEN_AXE_C;
				}
			case LANCE:
				switch (rank) {
				case PRF:
					return BROKEN_HOLY_SPEAR;
				case A:
					return BROKEN_LANCE_A;
				case B:
					return BROKEN_LANCE_B;
				default:
					return BROKEN_LANCE_C;
				}
			case SWORD:
				switch (rank) {
				case PRF:
					return BROKEN_HOLY_SWORD;
				case A:
					return BROKEN_SWORD_A;
				case B:
					return BROKEN_SWORD_B;
				default:
					return BROKEN_SWORD_C;
				}
			case BOW:
				switch (rank) {
				case PRF:
					return BROKEN_HOLY_BOW;
				case A:
					return BROKEN_BOW_A;
				case B:
					return BROKEN_BOW_B;
				default:
					return BROKEN_BOW_C;
				}
			case FIRE_MAGIC:
				switch (rank) {
				case PRF:
				case A:
					return EMPTY_BOOK_A_FIRE;
				case B:
					return EMPTY_BOOK_B_FIRE;
				default:
					return EMPTY_BOOK_C_FIRE;
				}
			case THUNDER_MAGIC:
				switch (rank) {
				case PRF:
				case A:
					return EMPTY_BOOK_A_THUNDER;
				case B:
					return EMPTY_BOOK_B_THUNDER;
				default:
					return EMPTY_BOOK_C_THUNDER;
				}
			case WIND_MAGIC:
				switch (rank) {
				case PRF:
					return EMPTY_BOOK_S_WIND;
				case A:
					return EMPTY_BOOK_A_WIND;
				case B:
					return EMPTY_BOOK_B_WIND;
				default:
					return EMPTY_BOOK_C_WIND;
				}
			case LIGHT_MAGIC:
				switch (rank) {
				case PRF:
					return EMPTY_BOOK_S_LIGHT;
				case A:
					return EMPTY_BOOK_A_LIGHT;
				case B:
					return EMPTY_BOOK_B_LIGHT;
				default:
					return EMPTY_BOOK_C_LIGHT;
				}
			case STAFF:
				switch (rank) {
				case PRF:
					return BROKEN_HOLY_STAFF;
				case A:
					return BROKEN_STAFF_A;
				case B:
					return BROKEN_STAFF_B;
				default:
					return BROKEN_STAFF_C;
				}
			default:
				return null;
			}
		}
		
		public ItemType getType() {
			if (swords.contains(this)) { return ItemType.SWORD; }
			if (lances.contains(this)) { return ItemType.LANCE; }
			if (axes.contains(this)) { return ItemType.AXE; }
			if (bows.contains(this)) { return ItemType.BOW; }
			if (fireMagic.contains(this)) { return ItemType.FIRE_MAGIC; }
			if (thunderMagic.contains(this)) { return ItemType.THUNDER_MAGIC; }
			if (windMagic.contains(this)) { return ItemType.WIND_MAGIC; }
			if (lightMagic.contains(this)) { return ItemType.LIGHT_MAGIC; }
			if (darkMagic.contains(this)) { return ItemType.DARK_MAGIC; }
			if (staves.contains(this)) { return ItemType.STAFF; }
			if (rings.contains(this)) { return ItemType.RING; }
			return ItemType.NONE;
		}
		
		public boolean isBroken() {
			return brokenWeapons.contains(this);
		}
		
		public boolean isWeapon() {
			return swords.contains(this) || lances.contains(this) || axes.contains(this) || bows.contains(this) ||
					fireMagic.contains(this) || thunderMagic.contains(this) || windMagic.contains(this) ||
					lightMagic.contains(this) || darkMagic.contains(this);
		}
		
		public boolean isRing() {
			return rings.contains(this);
		}
		
		public boolean isSiegeTome() {
			return siegeTomes.contains(this);
		}
		
		public WeaponRank getRank() {
			if (cWeapons.contains(this)) { return WeaponRank.C; }
			if (bWeapons.contains(this)) { return WeaponRank.B; }
			if (aWeapons.contains(this)) { return WeaponRank.A; }
			if (holyWeapons.contains(this)) { return WeaponRank.PRF; }
			return WeaponRank.NONE;
		}
		
		public HolyBlood holyBloodForHolyWeapon() {
			switch(this) {
			case TYRFING: return HolyBlood.BALDR;
			case MYSTLETAINN: return HolyBlood.HEZUL;
			case BALMUNG: return HolyBlood.OD;
			case GAE_BOLG: return HolyBlood.NJORUN;
			case GUNGNIR: return HolyBlood.DAIN;
			case HELSWATH: return HolyBlood.NEIR;
			case YEWFELLE: return HolyBlood.ULIR;
			case VALFLAME: return HolyBlood.FJALAR;
			case MJOLNIR: return HolyBlood.THRUD;
			case FORSETI: return HolyBlood.FORSETI;
			case NAGA: return HolyBlood.NAGA;
			case LOPTYR: return HolyBlood.LOPTOUS;
			case VALKYRIE: return HolyBlood.BRAGI;
			default: return HolyBlood.NONE;
			}
		}
	}
	
	public enum Skill {
		WRATH, PURSUIT, ADEPT, CHARM, NIHIL, MIRACLE,
		CRITICAL, VANTAGE, CHARGE, ASTRA, LUNA, SOL,
		RENEWAL, PARAGON, BARGAIN;
		
		public int slot() {
			switch (this) {
			case WRATH:
			case PURSUIT:
			case ADEPT:
			case CHARM:
			case NIHIL:
			case MIRACLE:
				return 1;
			case CRITICAL:
			case VANTAGE:
			case CHARGE:
			case ASTRA:
			case LUNA:
			case SOL:
				return 2;
			case RENEWAL:
			case PARAGON:
			case BARGAIN:
				return 3;
			}
			
			return 0;
		}

		/**
		 * Returns this Skills name with only the first letter capitalized
		 */
		public String capitalizedName() {
			return name().substring(0,1) + name().substring(1).toLowerCase();
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
		
		public Skill generalSkill() {
			switch (this) {
			case WRATH: return Skill.WRATH;
			case PURSUIT: return Skill.PURSUIT;
			case ADEPT: return Skill.ADEPT;
			case CHARM: return Skill.CHARM;
			case NIHIL: return Skill.NIHIL;
			case MIRACLE: return Skill.MIRACLE;
			default: return null;
			}
		}
		
		public static SkillSlot1 skill(Skill genericSkill) {
			switch (genericSkill) {
			case WRATH: return WRATH;
			case PURSUIT: return PURSUIT;
			case ADEPT: return ADEPT;
			case CHARM: return CHARM;
			case NIHIL: return NIHIL;
			case MIRACLE: return MIRACLE;
			default: return null;
			}
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
		
		public static SkillSlot2 skill(Skill genericSkill) {
			switch (genericSkill) {
			case CRITICAL: return CRITICAL;
			case VANTAGE: return VANTAGE;
			case CHARGE: return CHARGE;
			case ASTRA: return ASTRA;
			case LUNA: return LUNA;
			case SOL: return SOL;
			default: return null;
			}
		}
		
		public Skill generalSkill() {
			switch (this) {
			case CRITICAL: return Skill.CRITICAL;
			case VANTAGE: return Skill.VANTAGE;
			case CHARGE: return Skill.CHARGE;
			case ASTRA: return Skill.ASTRA;
			case LUNA: return Skill.LUNA;
			case SOL: return Skill.SOL;
			default: return null;
			}
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
		
		public static SkillSlot3 skill(Skill genericSkill) {
			switch (genericSkill) {
			case RENEWAL: return RENEWAL;
			case PARAGON: return PARAGON;
			case BARGAIN: return BARGAIN;
			default: return null;
			}
		}
		
		public Skill generalSkill() {
			switch (this) {
			case RENEWAL: return Skill.RENEWAL;
			case PARAGON: return Skill.PARAGON;
			case BARGAIN: return Skill.BARGAIN;
			default: return null;
			}
		}
	}
	
	public enum HolyBlood {
		NONE(Item.NONE, Item.ItemType.NONE, Character.NONE),
		BALDR(Item.TYRFING, Item.ItemType.SWORD, Character.SIGURD), 
		OD(Item.BALMUNG, Item.ItemType.SWORD, Character.SHANNAN), 
		HEZUL(Item.MYSTLETAINN, Item.ItemType.SWORD, Character.ELDIGAN_CH1_SCENE), 
		NJORUN(Item.GAE_BOLG, Item.ItemType.LANCE, Character.QUAN), 
		DAIN(Item.GUNGNIR, Item.ItemType.LANCE, Character.ARION_CH9), 
		NEIR(Item.HELSWATH, Item.ItemType.AXE, Character.LOMBARD), 
		ULIR(Item.YEWFELLE, Item.ItemType.BOW, Character.BRIGID), 
		FJALAR(Item.VALFLAME, Item.ItemType.FIRE_MAGIC, Character.ARVIS_CH5), 
		THRUD(Item.MJOLNIR, Item.ItemType.THUNDER_MAGIC, Character.REPTOR), 
		FORSETI(Item.FORSETI, Item.ItemType.WIND_MAGIC, Character.LEWYN), 
		NAGA(Item.NAGA, Item.ItemType.LIGHT_MAGIC, Character.JULIA), 
		LOPTOUS(Item.LOPTYR, Item.ItemType.DARK_MAGIC, Character.JULIUS_FINAL), 
		BRAGI(Item.VALKYRIE, Item.ItemType.STAFF, Character.CLAUD);
		
		public Item holyWeapon;
		public Item.ItemType weaponType;
		public Character representative;
		
		private HolyBlood(Item weapon, Item.ItemType type, Character representative) { 
			this.holyWeapon = weapon; 
			this.weaponType = type;
			this.representative = representative;
		}
		
		// They're not IDs because they're not referenced this way, but they are stored in data in this order.
		public static List<HolyBlood> orderedByDataTable() {
			return new ArrayList<HolyBlood>(Arrays.asList(BALDR, NAGA, DAIN, NJORUN, OD, ULIR, NEIR, FJALAR, THRUD, FORSETI, BRAGI, HEZUL, LOPTOUS));
		}
		
		public static Comparator<HolyBlood> defaultComparator = new Comparator<HolyBlood>() {
			@Override
			public int compare(HolyBlood o1, HolyBlood o2) {
				return Integer.compare(orderedByDataTable().indexOf(o1), orderedByDataTable().indexOf(o2));
			}
		};
		
		public CharacterClass[] classPool() {
			switch (this) {
			case BALDR:
			case OD:
			case HEZUL:
				return CharacterClass.swordUsers.toArray(new CharacterClass[CharacterClass.swordUsers.size()]);
			case NJORUN:
			case DAIN:
				return CharacterClass.lanceUsers.toArray(new CharacterClass[CharacterClass.lanceUsers.size()]);
			case NEIR: CharacterClass.axeUsers.toArray(new CharacterClass[CharacterClass.axeUsers.size()]);
			case FJALAR: CharacterClass.fireUsers.toArray(new CharacterClass[CharacterClass.fireUsers.size()]);
			case THRUD: CharacterClass.thunderUsers.toArray(new CharacterClass[CharacterClass.thunderUsers.size()]);
			case FORSETI: CharacterClass.windUsers.toArray(new CharacterClass[CharacterClass.windUsers.size()]);
			case NAGA: CharacterClass.lightUsers.toArray(new CharacterClass[CharacterClass.lightUsers.size()]);
			case LOPTOUS: CharacterClass.darkUsers.toArray(new CharacterClass[CharacterClass.darkUsers.size()]);
			case BRAGI: CharacterClass.staffUsers.toArray(new CharacterClass[CharacterClass.staffUsers.size()]);
			default: return new CharacterClass[] {};
			}
		}
		
		public boolean isEnemyBlood() {
			return !representative.isPlayable();
		}
		
		// These should not be doled out arbitrarily.
		public boolean isRestricted() {
			switch (this) {
			case NAGA:
			case LOPTOUS:
			case BRAGI: // There's too many cases where this can't be assigned to anybody because of the weapon, so we won't shuffle this blood.
				return true;
			default:
					return false;
			}
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
		
		public static HolyBloodSlot1 blood(HolyBlood type, boolean isMajor) {
			switch (type) {
			case BALDR: return isMajor ? MAJOR_BALDR : MINOR_BALDR;
			case NAGA: return isMajor ? MAJOR_NAGA : MINOR_NAGA;
			case DAIN: return isMajor ? MAJOR_DAIN : MINOR_DAIN;
			case NJORUN: return isMajor ? MAJOR_NJORUN : MINOR_NJORUN;
			default: return null;
			}
		}
		
		public HolyBlood bloodType() {
			switch (this) {
			case MINOR_BALDR:
			case MAJOR_BALDR:
				return HolyBlood.BALDR;
			case MINOR_NAGA:
			case MAJOR_NAGA:
				return HolyBlood.NAGA;
			case MINOR_DAIN:
			case MAJOR_DAIN:
				return HolyBlood.DAIN;
			case MINOR_NJORUN:
			case MAJOR_NJORUN:
				return HolyBlood.NJORUN;
			default:
				return null;
			}
		}
		
		public boolean isMajor() {
			switch (this) {
			case MAJOR_BALDR:
			case MAJOR_NAGA:
			case MAJOR_DAIN:
			case MAJOR_NJORUN:
				return true;
			default:
				return false;
			}
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
		
		public static HolyBloodSlot2 blood(HolyBlood type, boolean isMajor) {
			switch (type) {
			case OD: return isMajor ? MAJOR_OD : MINOR_OD;
			case ULIR: return isMajor ? MAJOR_ULIR : MINOR_ULIR;
			case NEIR: return isMajor ? MAJOR_NEIR : MINOR_NEIR;
			case FJALAR: return isMajor ? MAJOR_FJALAR : MINOR_FJALAR;
			default: return null;
			}
		}
		
		public HolyBlood bloodType() {
			switch (this) {
			case MINOR_OD:
			case MAJOR_OD:
				return HolyBlood.OD;
			case MINOR_ULIR:
			case MAJOR_ULIR:
				return HolyBlood.ULIR;
			case MINOR_NEIR:
			case MAJOR_NEIR:
				return HolyBlood.NEIR;
			case MINOR_FJALAR:
			case MAJOR_FJALAR:
				return HolyBlood.FJALAR;
			default:
				return null;
			}
		}
		
		public boolean isMajor() {
			switch (this) {
			case MAJOR_OD:
			case MAJOR_ULIR:
			case MAJOR_NEIR:
			case MAJOR_FJALAR:
				return true;
			default:
				return false;
			}
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
		
		public static HolyBloodSlot3 blood(HolyBlood type, boolean isMajor) {
			switch (type) {
			case THRUD: return isMajor ? MAJOR_THRUD : MINOR_THRUD;
			case FORSETI: return isMajor ? MAJOR_FORSETI : MINOR_FORSETI;
			case BRAGI: return isMajor ? MAJOR_BRAGI : MINOR_BRAGI;
			case HEZUL: return isMajor ? MAJOR_HEZUL : MINOR_HEZUL;
			default: return null;
			}
		}
		
		public HolyBlood bloodType() {
			switch (this) {
			case MINOR_THRUD:
			case MAJOR_THRUD:
				return HolyBlood.THRUD;
			case MINOR_FORSETI:
			case MAJOR_FORSETI:
				return HolyBlood.FORSETI;
			case MINOR_BRAGI:
			case MAJOR_BRAGI:
				return HolyBlood.BRAGI;
			case MINOR_HEZUL:
			case MAJOR_HEZUL:
				return HolyBlood.HEZUL;
			default:
				return null;
			}
		}
		
		public boolean isMajor() {
			switch (this) {
			case MAJOR_THRUD:
			case MAJOR_FORSETI:
			case MAJOR_BRAGI:
			case MAJOR_HEZUL:
				return true;
			default:
				return false;
			}
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
		
		public static HolyBloodSlot4 blood(HolyBlood type, boolean isMajor) {
			switch (type) {
			case LOPTOUS: return isMajor ? MAJOR_LOPTOUS : MINOR_LOPTOUS;
			default: return null;
			}
		}
		
		public HolyBlood bloodType() {
			return HolyBlood.LOPTOUS;
		}
		
		public boolean isMajor() {
			switch (this) {
			case MAJOR_LOPTOUS:
				return true;
			default:
				return false;
			}
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
	
	public enum Shops {
		CHAPTER_1(new HashSet<Integer>(Arrays.asList(0x1D, 0x2F, 0x38, 0x41))), 
		CHAPTER_2(new HashSet<Integer>(Arrays.asList(0x33, 0x55, 0x10, 0x4A))), 
		CHAPTER_3(new HashSet<Integer>(Arrays.asList(0x0B, 0x36, 0x44, 0x4B, 0x52))), 
		CHAPTER_4(new HashSet<Integer>(Arrays.asList(0x58, 0x68, 0x83))), 
		CHAPTER_5(new HashSet<Integer>(Arrays.asList())), 
		CHAPTER_6(new HashSet<Integer>(Arrays.asList())), 
		CHAPTER_7(new HashSet<Integer>(Arrays.asList(0x21, 0x51, 0x69))), 
		CHAPTER_8(new HashSet<Integer>(Arrays.asList())), 
		CHAPTER_9(new HashSet<Integer>(Arrays.asList())), 
		CHAPTER_10(new HashSet<Integer>(Arrays.asList()));
		
		public Set<Integer> inventoryIDs;
		
		private Shops(Set<Integer> inventoryIDs) { this.inventoryIDs = inventoryIDs; }
	}
}
