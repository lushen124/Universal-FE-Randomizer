package random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.FEBase;
import fedata.FECharacter;
import fedata.FEBase.GameType;
import fedata.FEClass;
import fedata.FEItem;
import fedata.FESpellAnimationCollection;
import fedata.fe6.FE6Data;
import fedata.fe6.FE6Item;
import fedata.fe6.FE6SpellAnimationCollection;
import fedata.fe7.FE7Data;
import fedata.fe7.FE7Item;
import fedata.fe7.FE7SpellAnimationCollection;
import fedata.fe8.FE8Data;
import fedata.fe8.FE8Item;
import fedata.fe8.FE8SpellAnimationCollection;
import fedata.fe7.FE7Data.Item;
import fedata.fe7.FE7Data.Item.WeaponEffect;
import fedata.general.WeaponEffects;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FreeSpaceManager;
import util.HuffmanHelper;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.RecordKeeper;

public class ItemDataLoader {
private FEBase.GameType gameType;

	public enum AdditionalData {
		STR_MAG_BOOST("STRMAG"), SKL_BOOST("SKL"), SPD_BOOST("SPD"), DEF_BOOST("DEF"), RES_BOOST("RES"), LCK_BOOST("LCK"),
		
		HERO_CREST_CLASSES("HEROCREST"), KNIGHT_CREST_CLASSES("KNIGHTCREST"), MASTER_SEAL_CLASSES("MASTERSEAL"), ELYSIAN_WHIP_CLASSES("ELYSIANWHIP"), GUIDING_RING_CLASSES("GUIDINGRING"), ORION_BOLT_CLASSES("ORIONBOLT"),
		
		KNIGHTCAV_EFFECT("EFF_KNIGHT_CAV"), KNIGHT_EFFECT("EFF_KNIGHT"), DRAGON_EFFECT("EFF_DRAGON"), CAVALRY_EFFECT("EFF_CAVALRY"), MYRMIDON_EFFECT("EFF_MYRMIDON"), FLIERS_EFFECT("EFF_FLIER"), MONSTER_EFFECT("EFF_MONSTER");
		
		String key;
		
		private AdditionalData(String key) {
			this.key = key;
		}
	}
	
	private Map<Integer, FEItem> itemMap = new HashMap<Integer, FEItem>();
	
	// TODO: Put this somewhere else.
	public FESpellAnimationCollection spellAnimations;
	
	private FreeSpaceManager freeSpace;
	private Map<AdditionalData, Long> offsetsForAdditionalData;
	private Map<AdditionalData, Long> promotionItemAddressPointers;
	
	public static final String RecordKeeperCategoryWeaponKey = "Weapons";

	public ItemDataLoader(FEBase.GameType gameType, FileHandler handler, FreeSpaceManager freeSpace) {
		super();
		this.gameType = gameType;
		this.freeSpace = freeSpace;
		
		switch (gameType) {
			case FE6: {
				long baseAddress = FileReadHelper.readAddress(handler, FE6Data.ItemTablePointer);
				for (FE6Data.Item item : FE6Data.Item.values()) {
					if (item == FE6Data.Item.NONE) {
						continue;
					}	
					long offset = baseAddress + (FE6Data.BytesPerItem * item.ID);
					byte[] itemData = handler.readBytesAtOffset(offset, FE6Data.BytesPerItem);
					itemMap.put(item.ID, new FE6Item(itemData, offset));
				}
				
				long spellAnimationBaseAddress = FileReadHelper.readAddress(handler, FE6Data.SpellAnimationTablePointer);
				spellAnimations = new FE6SpellAnimationCollection(handler.readBytesAtOffset(spellAnimationBaseAddress, 
						FE6Data.NumberOfSpellAnimations * FE6Data.BytesPerSpellAnimation), spellAnimationBaseAddress);
				
				offsetsForAdditionalData = new HashMap<AdditionalData, Long>();
				
				// Set up effectiveness.
				long offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.CAVALIER.ID,
						(byte)FE6Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE6Data.CharacterClass.PALADIN.ID,
						(byte)FE6Data.CharacterClass.PALADIN_F.ID,
						(byte)FE6Data.CharacterClass.KNIGHT.ID,
						(byte)FE6Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE6Data.CharacterClass.GENERAL.ID,
						(byte)FE6Data.CharacterClass.GENERAL_F.ID,
						(byte)FE6Data.CharacterClass.NOMAD.ID,
						(byte)FE6Data.CharacterClass.NOMAD_F.ID,
						(byte)FE6Data.CharacterClass.NOMAD_TROOPER.ID,
						(byte)FE6Data.CharacterClass.NOMAD_TROOPER_F.ID,
						(byte)FE6Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE6Data.CharacterClass.VALKYRIE.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHTCAV_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHTCAV_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.KNIGHT.ID,
						(byte)FE6Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE6Data.CharacterClass.GENERAL.ID,
						(byte)FE6Data.CharacterClass.GENERAL_F.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHT_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.CAVALIER.ID,
						(byte)FE6Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE6Data.CharacterClass.PALADIN.ID,
						(byte)FE6Data.CharacterClass.PALADIN_F.ID,
						(byte)FE6Data.CharacterClass.NOMAD.ID,
						(byte)FE6Data.CharacterClass.NOMAD_F.ID,
						(byte)FE6Data.CharacterClass.NOMAD_TROOPER.ID,
						(byte)FE6Data.CharacterClass.NOMAD_TROOPER_F.ID,
						(byte)FE6Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE6Data.CharacterClass.VALKYRIE.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.CAVALRY_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.CAVALRY_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.FIRE_DRAGON.ID,
						(byte)FE6Data.CharacterClass.DIVINE_DRAGON.ID,
						(byte)FE6Data.CharacterClass.MAGIC_DRAGON.ID,
						(byte)FE6Data.CharacterClass.WYVERN_RIDER.ID,
						(byte)FE6Data.CharacterClass.WYVERN_RIDER_F.ID,
						(byte)FE6Data.CharacterClass.WYVERN_KNIGHT.ID,
						(byte)FE6Data.CharacterClass.WYVERN_KNIGHT_F.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.DRAGON_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.DRAGON_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE6Data.CharacterClass.PEGASUS_KNIGHT.ID,
						(byte)FE6Data.CharacterClass.FALCON_KNIGHT.ID,
						(byte)FE6Data.CharacterClass.WYVERN_KNIGHT.ID,
						(byte)FE6Data.CharacterClass.WYVERN_KNIGHT_F.ID,
						(byte)FE6Data.CharacterClass.WYVERN_RIDER.ID,
						(byte)FE6Data.CharacterClass.WYVERN_RIDER_F.ID,
						(byte)FE6Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.FLIERS_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.FLIERS_EFFECT, offset);
				
				// Set up stat boosts.
				offset = freeSpace.setValue(new byte[] {0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.STR_MAG_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.STR_MAG_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.SKL_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.SKL_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00}, AdditionalData.SPD_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.SPD_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00}, AdditionalData.DEF_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.DEF_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00}, AdditionalData.RES_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.RES_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00}, AdditionalData.LCK_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.LCK_BOOST, offset);
				
				// Set up promotion items.
				long knightCrestOffset = FE6Data.PromotionItem.KNIGHT_CREST.getListAddress();
				List<Byte> idList = new ArrayList<Byte>();
				byte currentByte = 0x0;
				long currentOffset = FileReadHelper.readAddress(handler, knightCrestOffset); // One more jump here.
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE6Data.CharacterClass.LORD.ID)) { idList.add((byte)FE6Data.CharacterClass.LORD.ID); }
				if (!idList.contains((byte)FE6Data.CharacterClass.SOLDIER.ID)) { idList.add((byte)FE6Data.CharacterClass.SOLDIER.ID); }
				idList.add((byte)FE6Data.CharacterClass.NONE.ID);
				byte[] byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.KNIGHT_CREST_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_CREST_CLASSES, offset);
					
				promotionItemAddressPointers = new HashMap<AdditionalData, Long>();
				promotionItemAddressPointers.put(AdditionalData.KNIGHT_CREST_CLASSES, knightCrestOffset);
				
				break;
			}
			case FE7: {
				long baseAddress = FileReadHelper.readAddress(handler, FE7Data.ItemTablePointer);
				for (FE7Data.Item item : FE7Data.Item.values()) {
					if (item == FE7Data.Item.NONE) {
						continue;
					}	
					long offset = baseAddress + (FE7Data.BytesPerItem * item.ID);
					byte[] itemData = handler.readBytesAtOffset(offset, FE7Data.BytesPerItem);
					itemMap.put(item.ID, new FE7Item(itemData, offset));
				}
				
				long spellAnimationBaseAddress = FileReadHelper.readAddress(handler, FE7Data.SpellAnimationTablePointer);
				spellAnimations = new FE7SpellAnimationCollection(handler.readBytesAtOffset(spellAnimationBaseAddress, 
						FE7Data.NumberOfSpellAnimations * FE7Data.BytesPerSpellAnimation), spellAnimationBaseAddress);
				
				offsetsForAdditionalData = new HashMap<AdditionalData, Long>();
				
				// Set up effectiveness.
				long offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.CAVALIER.ID,
						(byte)FE7Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE7Data.CharacterClass.PALADIN.ID,
						(byte)FE7Data.CharacterClass.PALADIN_F.ID,
						(byte)FE7Data.CharacterClass.KNIGHT.ID,
						(byte)FE7Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE7Data.CharacterClass.GENERAL.ID,
						(byte)FE7Data.CharacterClass.GENERAL_F.ID,
						(byte)FE7Data.CharacterClass.NOMAD.ID,
						(byte)FE7Data.CharacterClass.NOMAD_F.ID,
						(byte)FE7Data.CharacterClass.NOMADTROOPER.ID,
						(byte)FE7Data.CharacterClass.NOMADTROOPER_F.ID,
						(byte)FE7Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE7Data.CharacterClass.VALKYRIE.ID,
						(byte)FE7Data.CharacterClass.LORD_KNIGHT.ID,
						(byte)FE7Data.CharacterClass.GREAT_LORD.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHTCAV_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHTCAV_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.KNIGHT.ID,
						(byte)FE7Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE7Data.CharacterClass.GENERAL.ID,
						(byte)FE7Data.CharacterClass.GENERAL_F.ID,
						(byte)FE7Data.CharacterClass.GREAT_LORD.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHT_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.CAVALIER.ID,
						(byte)FE7Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE7Data.CharacterClass.PALADIN.ID,
						(byte)FE7Data.CharacterClass.PALADIN_F.ID,
						(byte)FE7Data.CharacterClass.NOMAD.ID,
						(byte)FE7Data.CharacterClass.NOMAD_F.ID,
						(byte)FE7Data.CharacterClass.NOMADTROOPER.ID,
						(byte)FE7Data.CharacterClass.NOMADTROOPER_F.ID,
						(byte)FE7Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE7Data.CharacterClass.VALKYRIE.ID,
						(byte)FE7Data.CharacterClass.LORD_KNIGHT.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.CAVALRY_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.CAVALRY_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.FIRE_DRAGON.ID,
						(byte)FE7Data.CharacterClass.WYVERNKNIGHT.ID,
						(byte)FE7Data.CharacterClass.WYVERNKNIGHT_F.ID,
						(byte)FE7Data.CharacterClass.WYVERNLORD.ID,
						(byte)FE7Data.CharacterClass.WYVERNLORD_F.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.DRAGON_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.DRAGON_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.MYRMIDON.ID,
						(byte)FE7Data.CharacterClass.MYRMIDON_F.ID,
						(byte)FE7Data.CharacterClass.SWORDMASTER.ID,
						(byte)FE7Data.CharacterClass.SWORDMASTER_F.ID,
						(byte)FE7Data.CharacterClass.MERCENARY.ID,
						(byte)FE7Data.CharacterClass.MERCENARY_F.ID,
						(byte)FE7Data.CharacterClass.HERO.ID,
						(byte)FE7Data.CharacterClass.HERO_F.ID,
						(byte)FE7Data.CharacterClass.BLADE_LORD.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.MYRMIDON_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.MYRMIDON_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE7Data.CharacterClass.PEGASUSKNIGHT.ID,
						(byte)FE7Data.CharacterClass.FALCONKNIGHT.ID,
						(byte)FE7Data.CharacterClass.WYVERNKNIGHT.ID,
						(byte)FE7Data.CharacterClass.WYVERNKNIGHT_F.ID,
						(byte)FE7Data.CharacterClass.WYVERNLORD.ID,
						(byte)FE7Data.CharacterClass.WYVERNLORD_F.ID,
						(byte)FE7Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.FLIERS_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.FLIERS_EFFECT, offset);
				
				// Set up stat boosts.
				offset = freeSpace.setValue(new byte[] {0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.STR_MAG_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.STR_MAG_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.SKL_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.SKL_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00}, AdditionalData.SPD_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.SPD_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00}, AdditionalData.DEF_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.DEF_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00}, AdditionalData.RES_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.RES_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00}, AdditionalData.LCK_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.LCK_BOOST, offset);
				
				// Set up promotion items.
				long heroCrestOffset = FE7Data.PromotionItem.HERO_CREST.getPointerAddress();
				long heroCrestPointerAddress = FileReadHelper.readAddress(handler, heroCrestOffset) + 4;
				List<Byte> idList = new ArrayList<Byte>();
				byte currentByte = 0x0;
				long currentOffset = FileReadHelper.readAddress(handler, heroCrestPointerAddress); // One more jump here.
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_LYN.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_LYN.ID); }
				// Always end with terminal
				idList.add((byte)FE7Data.CharacterClass.NONE.ID);
				byte[] byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.HERO_CREST_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.HERO_CREST_CLASSES, offset);
				
				long knightCrestOffset = FE7Data.PromotionItem.KNIGHT_CREST.getPointerAddress();
				long knightCrestPointerAddress = FileReadHelper.readAddress(handler, knightCrestOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, knightCrestPointerAddress); // One more jump here.
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_HECTOR.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_HECTOR.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_ELIWOOD.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_ELIWOOD.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.SOLDIER.ID)) { idList.add((byte)FE7Data.CharacterClass.SOLDIER.ID); }
				idList.add((byte)FE7Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.KNIGHT_CREST_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_CREST_CLASSES, offset);
				
				long masterSealOffset = FE7Data.PromotionItem.MASTER_SEAL.getPointerAddress();
				long masterSealPointerAddress = FileReadHelper.readAddress(handler, masterSealOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, masterSealPointerAddress); // One more jump here.
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_LYN.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_LYN.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_HECTOR.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_HECTOR.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.LORD_ELIWOOD.ID)) { idList.add((byte)FE7Data.CharacterClass.LORD_ELIWOOD.ID); }
				if (!idList.contains((byte)FE7Data.CharacterClass.SOLDIER.ID)) { idList.add((byte)FE7Data.CharacterClass.SOLDIER.ID); }
				idList.add((byte)FE7Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.MASTER_SEAL_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.MASTER_SEAL_CLASSES, offset);
				
				promotionItemAddressPointers = new HashMap<AdditionalData, Long>();
				promotionItemAddressPointers.put(AdditionalData.HERO_CREST_CLASSES, heroCrestPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.KNIGHT_CREST_CLASSES, knightCrestPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.MASTER_SEAL_CLASSES, masterSealPointerAddress);
				
				break;
		}
			case FE8: {
				long baseAddress = FileReadHelper.readAddress(handler, FE8Data.ItemTablePointer);
				for (FE8Data.Item item : FE8Data.Item.values()) {
					if (item == FE8Data.Item.NONE) {
						continue;
					}	
					long offset = baseAddress + (FE8Data.BytesPerItem * item.ID);
					byte[] itemData = handler.readBytesAtOffset(offset, FE8Data.BytesPerItem);
					itemMap.put(item.ID, new FE8Item(itemData, offset, item.ID));
				}
				
				long spellAnimationBaseAddress = FileReadHelper.readAddress(handler, FE8Data.SpellAnimationTablePointer);
				spellAnimations = new FE8SpellAnimationCollection(handler.readBytesAtOffset(spellAnimationBaseAddress, 
						FE8Data.NumberOfSpellAnimations * FE8Data.BytesPerSpellAnimation), spellAnimationBaseAddress);
				
				offsetsForAdditionalData = new HashMap<AdditionalData, Long>();
				
				// Set up effectiveness.
				long offset = freeSpace.setValue(new byte[] {
						(byte)FE8Data.CharacterClass.CAVALIER.ID,
						(byte)FE8Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE8Data.CharacterClass.PALADIN.ID,
						(byte)FE8Data.CharacterClass.PALADIN_F.ID,
						(byte)FE8Data.CharacterClass.GREAT_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.GREAT_KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.KNIGHT.ID,
						(byte)FE8Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.GENERAL.ID,
						(byte)FE8Data.CharacterClass.GENERAL_F.ID,
						(byte)FE8Data.CharacterClass.RANGER.ID,
						(byte)FE8Data.CharacterClass.RANGER_F.ID,
						(byte)FE8Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE8Data.CharacterClass.VALKYRIE.ID,
						(byte)FE8Data.CharacterClass.MAGE_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.MAGE_KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.EIRIKA_MASTER_LORD.ID,
						(byte)FE8Data.CharacterClass.EPHRAIM_MASTER_LORD.ID,
						(byte)FE8Data.CharacterClass.TARVOS.ID,
						(byte)FE8Data.CharacterClass.MAELDUIN.ID,
						(byte)FE8Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHTCAV_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHTCAV_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE8Data.CharacterClass.KNIGHT.ID,
						(byte)FE8Data.CharacterClass.KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.GENERAL.ID,
						(byte)FE8Data.CharacterClass.GENERAL_F.ID,
						(byte)FE8Data.CharacterClass.GREAT_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.GREAT_KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.KNIGHT_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE8Data.CharacterClass.CAVALIER.ID,
						(byte)FE8Data.CharacterClass.CAVALIER_F.ID,
						(byte)FE8Data.CharacterClass.PALADIN.ID,
						(byte)FE8Data.CharacterClass.PALADIN_F.ID,
						(byte)FE8Data.CharacterClass.GREAT_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.GREAT_KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.RANGER.ID,
						(byte)FE8Data.CharacterClass.RANGER_F.ID,
						(byte)FE8Data.CharacterClass.MAGE_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.MAGE_KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.TROUBADOUR.ID,
						(byte)FE8Data.CharacterClass.VALKYRIE.ID,
						(byte)FE8Data.CharacterClass.EIRIKA_MASTER_LORD.ID,
						(byte)FE8Data.CharacterClass.EPHRAIM_MASTER_LORD.ID,
						(byte)FE8Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.CAVALRY_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.CAVALRY_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE8Data.CharacterClass.REVENANT.ID,
						(byte)FE8Data.CharacterClass.ENTOMBED.ID,
						(byte)FE8Data.CharacterClass.BONEWALKER.ID,
						(byte)FE8Data.CharacterClass.BONEWALKER_BOW.ID,
						(byte)FE8Data.CharacterClass.WIGHT.ID,
						(byte)FE8Data.CharacterClass.WIGHT_BOW.ID,
						(byte)FE8Data.CharacterClass.BAEL.ID,
						(byte)FE8Data.CharacterClass.ELDER_BAEL.ID,
						(byte)FE8Data.CharacterClass.CYCLOPS.ID,
						(byte)FE8Data.CharacterClass.MAUTHE_DOOG.ID,
						(byte)FE8Data.CharacterClass.GWYLLGI.ID,
						(byte)FE8Data.CharacterClass.TARVOS.ID,
						(byte)FE8Data.CharacterClass.MAELDUIN.ID,
						(byte)FE8Data.CharacterClass.MOGALL.ID,
						(byte)FE8Data.CharacterClass.ARCH_MOGALL.ID,
						(byte)FE8Data.CharacterClass.GORGON.ID,
						(byte)FE8Data.CharacterClass.GORGON_EGG.ID,
						(byte)FE8Data.CharacterClass.GARGOYLE.ID,
						(byte)FE8Data.CharacterClass.DEATHGOYLE.ID,
						(byte)FE8Data.CharacterClass.DRACOZOMBIE.ID,
						(byte)FE8Data.CharacterClass.DEMON_KING.ID,
						(byte)FE8Data.CharacterClass.MANAKETE.ID,
						(byte)FE8Data.CharacterClass.CYCLOPS_2.ID,
						(byte)FE8Data.CharacterClass.ELDER_BAEL_2.ID,
						(byte)FE8Data.CharacterClass.GHOST_FIGHTER.ID,
						(byte)FE8Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.MONSTER_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.MONSTER_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE8Data.CharacterClass.DRACOZOMBIE.ID,
						(byte)FE8Data.CharacterClass.WYVERN_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.WYVERN_KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.WYVERN_LORD.ID,
						(byte)FE8Data.CharacterClass.WYVERN_LORD_F.ID,
						(byte)FE8Data.CharacterClass.MANAKETE.ID,
						(byte)FE8Data.CharacterClass.MANAKETE_F.ID,
						(byte)FE8Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.DRAGON_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.DRAGON_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE8Data.CharacterClass.MYRMIDON.ID,
						(byte)FE8Data.CharacterClass.MYRMIDON_F.ID,
						(byte)FE8Data.CharacterClass.SWORDMASTER.ID,
						(byte)FE8Data.CharacterClass.SWORDMASTER_F.ID,
						(byte)FE8Data.CharacterClass.MERCENARY.ID,
						(byte)FE8Data.CharacterClass.MERCENARY_F.ID,
						(byte)FE8Data.CharacterClass.HERO.ID,
						(byte)FE8Data.CharacterClass.HERO_F.ID,
						(byte)FE8Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.MYRMIDON_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.MYRMIDON_EFFECT, offset);
				offset = freeSpace.setValue(new byte[] {
						(byte)FE8Data.CharacterClass.PEGASUS_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.FALCON_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.WYVERN_RIDER.ID,
						(byte)FE8Data.CharacterClass.WYVERN_RIDER_F.ID,
						(byte)FE8Data.CharacterClass.WYVERN_KNIGHT.ID,
						(byte)FE8Data.CharacterClass.WYVERN_KNIGHT_F.ID,
						(byte)FE8Data.CharacterClass.WYVERN_LORD.ID,
						(byte)FE8Data.CharacterClass.WYVERN_LORD_F.ID,
						(byte)FE8Data.CharacterClass.GARGOYLE.ID,
						(byte)FE8Data.CharacterClass.DEATHGOYLE.ID,
						(byte)FE8Data.CharacterClass.MANAKETE.ID,
						(byte)FE8Data.CharacterClass.MANAKETE_F.ID,
						(byte)FE8Data.CharacterClass.DRACOZOMBIE.ID,
						(byte)FE8Data.CharacterClass.NONE.ID // Terminal.
				}, AdditionalData.FLIERS_EFFECT.key); 
				offsetsForAdditionalData.put(AdditionalData.FLIERS_EFFECT, offset);
				
				// Set up stat boosts.
				offset = freeSpace.setValue(new byte[] {0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.STR_MAG_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.STR_MAG_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00}, AdditionalData.SKL_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.SKL_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00}, AdditionalData.SPD_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.SPD_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00}, AdditionalData.DEF_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.DEF_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00}, AdditionalData.RES_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.RES_BOOST, offset);
				offset = freeSpace.setValue(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00}, AdditionalData.LCK_BOOST.key);
				offsetsForAdditionalData.put(AdditionalData.LCK_BOOST, offset);
				
				// Set up promotion items.
				long heroCrestOffset = FE8Data.PromotionItem.HERO_CREST.getPointerAddress();
				long heroCrestPointerAddress = FileReadHelper.readAddress(handler, heroCrestOffset) + 4;
				List<Byte> idList = new ArrayList<Byte>();
				byte currentByte = 0x0;
				long currentOffset = FileReadHelper.readAddress(handler, heroCrestPointerAddress);
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE8Data.CharacterClass.REVENANT.ID)) { idList.add((byte)FE8Data.CharacterClass.REVENANT.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.BONEWALKER.ID)) { idList.add((byte)FE8Data.CharacterClass.BONEWALKER.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.MAUTHE_DOOG.ID)) { idList.add((byte)FE8Data.CharacterClass.MAUTHE_DOOG.ID); }
				// Always end with terminal
				idList.add((byte)FE7Data.CharacterClass.NONE.ID);
				byte[] byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.HERO_CREST_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.HERO_CREST_CLASSES, offset);
				
				long knightCrestOffset = FE8Data.PromotionItem.KNIGHT_CREST.getPointerAddress();
				long knightCrestPointerAddress = FileReadHelper.readAddress(handler, knightCrestOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, knightCrestPointerAddress);
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE8Data.CharacterClass.EIRIKA_LORD.ID)) { idList.add((byte)FE8Data.CharacterClass.EIRIKA_LORD.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.EPHRAIM_LORD.ID)) { idList.add((byte)FE8Data.CharacterClass.EPHRAIM_LORD.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.SOLDIER.ID)) { idList.add((byte)FE8Data.CharacterClass.SOLDIER.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.TARVOS.ID)) { idList.add((byte)FE8Data.CharacterClass.TARVOS.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.BAEL.ID)) { idList.add((byte)FE8Data.CharacterClass.BAEL.ID); }
				idList.add((byte)FE7Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.KNIGHT_CREST_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.KNIGHT_CREST_CLASSES, offset);
				
				long elysianWhipOffset = FE8Data.PromotionItem.ELYSIAN_WHIP.getPointerAddress();
				long elysianWhipPointerAddress = FileReadHelper.readAddress(handler, elysianWhipOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, elysianWhipPointerAddress);
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE8Data.CharacterClass.GARGOYLE.ID)) { idList.add((byte)FE8Data.CharacterClass.GARGOYLE.ID); }
				idList.add((byte)FE8Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.ELYSIAN_WHIP_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.ELYSIAN_WHIP_CLASSES, offset);
				
				long orionBoltOffset = FE8Data.PromotionItem.ORION_BOLT.getPointerAddress();
				long orionBoltPointerAddress = FileReadHelper.readAddress(handler, orionBoltOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, orionBoltPointerAddress);
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE8Data.CharacterClass.BONEWALKER_BOW.ID)) { idList.add((byte)FE8Data.CharacterClass.BONEWALKER_BOW.ID); }
				idList.add((byte)FE8Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.ORION_BOLT_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.ORION_BOLT_CLASSES, offset);
				
				long guidingRingOffset = FE8Data.PromotionItem.GUIDING_RING.getPointerAddress();
				long guidingRingPointerAddress = FileReadHelper.readAddress(handler, guidingRingOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, guidingRingPointerAddress);
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE8Data.CharacterClass.MOGALL.ID)) { idList.add((byte)FE8Data.CharacterClass.MOGALL.ID); }
				idList.add((byte)FE8Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.GUIDING_RING_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.GUIDING_RING_CLASSES, offset);
				
				long masterSealOffset = FE8Data.PromotionItem.MASTER_SEAL.getPointerAddress();
				long masterSealPointerAddress = FileReadHelper.readAddress(handler, masterSealOffset) + 4;
				idList.clear();
				currentByte = 0x0;
				currentOffset = FileReadHelper.readAddress(handler, masterSealPointerAddress);
				do {
					currentByte = handler.readBytesAtOffset(currentOffset++, 1)[0];
					if (currentByte != 0) {
						idList.add(currentByte);
					}
				} while (currentByte != 0);
				if (!idList.contains((byte)FE8Data.CharacterClass.EIRIKA_LORD.ID)) { idList.add((byte)FE8Data.CharacterClass.EIRIKA_LORD.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.EPHRAIM_LORD.ID)) { idList.add((byte)FE8Data.CharacterClass.EPHRAIM_LORD.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.SOLDIER.ID)) { idList.add((byte)FE8Data.CharacterClass.SOLDIER.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.REVENANT.ID)) { idList.add((byte)FE8Data.CharacterClass.REVENANT.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.BONEWALKER.ID)) { idList.add((byte)FE8Data.CharacterClass.BONEWALKER.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.BONEWALKER_BOW.ID)) { idList.add((byte)FE8Data.CharacterClass.BONEWALKER_BOW.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.BAEL.ID)) { idList.add((byte)FE8Data.CharacterClass.BAEL.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.MAUTHE_DOOG.ID)) { idList.add((byte)FE8Data.CharacterClass.MAUTHE_DOOG.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.TARVOS.ID)) { idList.add((byte)FE8Data.CharacterClass.TARVOS.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.MOGALL.ID)) { idList.add((byte)FE8Data.CharacterClass.MOGALL.ID); }
				if (!idList.contains((byte)FE8Data.CharacterClass.GARGOYLE.ID)) { idList.add((byte)FE8Data.CharacterClass.GARGOYLE.ID); }
				
				idList.add((byte)FE8Data.CharacterClass.NONE.ID);
				byteArray = new byte[idList.size()];
				for (int i = 0; i < idList.size(); i++) {
					byteArray[i] = idList.get(i);
				}
				offset = freeSpace.setValue(byteArray, AdditionalData.MASTER_SEAL_CLASSES.key);
				offsetsForAdditionalData.put(AdditionalData.MASTER_SEAL_CLASSES, offset);
				
				promotionItemAddressPointers = new HashMap<AdditionalData, Long>();
				promotionItemAddressPointers.put(AdditionalData.HERO_CREST_CLASSES, heroCrestPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.KNIGHT_CREST_CLASSES, knightCrestPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.MASTER_SEAL_CLASSES, masterSealPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.ORION_BOLT_CLASSES, orionBoltPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.GUIDING_RING_CLASSES, guidingRingPointerAddress);
				promotionItemAddressPointers.put(AdditionalData.ELYSIAN_WHIP_CLASSES, elysianWhipPointerAddress);
				
				break;
		}
			default:
				break;
		}
	}
	
	public FEItem itemWithID(int itemID) {
		return itemMap.get(itemID);
	}
	
	public int getHighestWeaponRank() {
		switch (gameType) {
		case FE6:
			return FE6Data.Item.FE6WeaponRank.S.value;
		case FE7:
			return FE7Data.Item.FE7WeaponRank.S.value;
		case FE8:
			return FE8Data.Item.FE8WeaponRank.S.value;
		default:
			break;
		}
		
		return 0;
	}
	
	public FEItem[] getAllWeapons() {
		switch (gameType) {
		case FE6: {
			return itemsFromFE6Items(FE6Data.Item.allWeapons);
		}
		case FE7: {
			return itemsFromFE7Items(FE7Data.Item.allWeapons);
		}
		case FE8: {
			return itemsFromFE8Items(FE8Data.Item.allWeapons);
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public long[] possibleStatBoostAddresses() {
		return new long[] { offsetsForAdditionalData.get(AdditionalData.STR_MAG_BOOST),
				offsetsForAdditionalData.get(AdditionalData.SKL_BOOST),
				offsetsForAdditionalData.get(AdditionalData.SPD_BOOST),
				offsetsForAdditionalData.get(AdditionalData.DEF_BOOST),
				offsetsForAdditionalData.get(AdditionalData.RES_BOOST),
				offsetsForAdditionalData.get(AdditionalData.LCK_BOOST)};
	}
	
	public String descriptionStringForAddress(long address, Boolean isMagic, Boolean shortForm) {
		if (offsetsForAdditionalData.get(AdditionalData.STR_MAG_BOOST) == address) { return isMagic ? "+5 Magic" : "+5 Strength"; }
		if (offsetsForAdditionalData.get(AdditionalData.SKL_BOOST) == address) { return "+5 Skill"; }
		if (offsetsForAdditionalData.get(AdditionalData.SPD_BOOST) == address) { return "+5 Speed"; }
		if (offsetsForAdditionalData.get(AdditionalData.LCK_BOOST) == address) { return "+5 Luck"; }
		if (offsetsForAdditionalData.get(AdditionalData.DEF_BOOST) == address) { return "+5 Defense"; }
		if (offsetsForAdditionalData.get(AdditionalData.RES_BOOST) == address) { return "+5 Resistance"; }
		
		if (offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT) == address) { return shortForm ? "Eff. Knights" : "Effective against knights"; }
		if (offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT) == address) { return shortForm ? "Eff. Infantry" : "Effective against infantry"; }
		if (offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT) == address) { return shortForm ? "Eff. Cavalry" : "Effective against cavalry"; }
		if (offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT) == address) { return shortForm ? "Eff. Fliers" : "Effective against fliers"; }
		if (offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT) != null && address == offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT)) { return shortForm ? "Eff. Swordfighters" : "Effective against swordfighters"; }
		if (offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT) == address) { return shortForm ? "Eff. Dragons" : "Effective against dragons"; }
		
		switch (gameType) {
		case FE6: {
			long durandalStatBonusAddress = itemMap.get(FE6Data.Item.DURANDAL.ID).getStatBonusPointer(); // STR
			long bindingBladeStatBonusAddress = itemMap.get(FE6Data.Item.BINDING_BLADE.ID).getStatBonusPointer(); // DEF, RES
			long maltetStatBonusAddress = itemMap.get(FE6Data.Item.MALTET.ID).getStatBonusPointer(); // SKL
			long armadsStatBonusAddress = itemMap.get(FE6Data.Item.ARMADS.ID).getStatBonusPointer(); // DEF
			long murgleisStatBonusAddress = itemMap.get(FE6Data.Item.MURGLEIS.ID).getStatBonusPointer(); // SPD
			long forblazeStatBonusAddress = itemMap.get(FE6Data.Item.FORBLAZE.ID).getStatBonusPointer(); // LCK
			long aureolaStatBonusAddress = itemMap.get(FE6Data.Item.AUREOLA.ID).getStatBonusPointer(); // RES
			long apocalypseStatBonusAddress = itemMap.get(FE6Data.Item.APOCALYPSE.ID).getStatBonusPointer(); // MAG
			
			if (address == durandalStatBonusAddress) { return "+5 Strength"; }
			if (address == bindingBladeStatBonusAddress) { return "+5 Defense, Resistance"; }
			if (address == maltetStatBonusAddress) { return "+5 Skill"; }
			if (address == armadsStatBonusAddress) { return "+5 Defense"; }
			if (address == murgleisStatBonusAddress) { return "+5 Speed"; }
			if (address == forblazeStatBonusAddress) { return "+5 Luck"; }
			if (address == aureolaStatBonusAddress) { return "+5 Resistance"; }
			if (address == apocalypseStatBonusAddress) { return "+5 Magic"; }
			
			long rapierEffectivenessAddress = itemMap.get(FE6Data.Item.RAPIER.ID).getEffectivenessPointer();
			long bowEffectivenessAddress = itemMap.get(FE6Data.Item.IRON_BOW.ID).getEffectivenessPointer();
			long horseslayerEffectivenessAddress = itemMap.get(FE6Data.Item.HORSESLAYER.ID).getEffectivenessPointer();
			long hammerEffectivenessAddress = itemMap.get(FE6Data.Item.HAMMER.ID).getEffectivenessPointer();
			long dragonEffectivenessAddress = itemMap.get(FE6Data.Item.WYRMSLAYER.ID).getEffectivenessPointer();
			
			if (address == rapierEffectivenessAddress) { return shortForm ? "Eff. Infantry" : "Effective against infantry"; }
			if (address == bowEffectivenessAddress) { return shortForm ? "Eff. Fliers" : "Effective against fliers"; }
			if (address == horseslayerEffectivenessAddress) { return shortForm ? "Eff. Cavalry" : "Effective against cavalry"; }
			if (address == hammerEffectivenessAddress) { return shortForm ? "Eff. Knights" : "Effective against knights"; }
			if (address == dragonEffectivenessAddress) { return shortForm ? "Eff. Dragons" : "Effective against dragons"; }
			break;
		}
		case FE7: {
			long durandalStatBonusAddress = itemMap.get(FE7Data.Item.DURANDAL.ID).getStatBonusPointer(); // STR
			long solKattiStatBonusAddress = itemMap.get(FE7Data.Item.SOL_KATTI.ID).getStatBonusPointer(); // RES
			long armadsStatBonusAddress = itemMap.get(FE7Data.Item.ARMADS.ID).getStatBonusPointer(); // DEF
			long forblazeStatBonusAddress = itemMap.get(FE7Data.Item.FORBLAZE.ID).getStatBonusPointer(); // LCK
			
			if (address == durandalStatBonusAddress) { return isMagic ? "+5 Magic" : "+5 Strength"; }
			if (address == solKattiStatBonusAddress) { return "+5 Resistance"; }
			if (address == armadsStatBonusAddress) { return "+5 Defense"; }
			if (address == forblazeStatBonusAddress) { return "+5 Luck"; }
			
			long rapierEffectivenessAddress = itemMap.get(FE7Data.Item.RAPIER.ID).getEffectivenessPointer();
			long bowEffectivenessAddress = itemMap.get(FE7Data.Item.IRON_BOW.ID).getEffectivenessPointer();
			long horseslayerEffectivenessAddress = itemMap.get(FE7Data.Item.HORSESLAYER.ID).getEffectivenessPointer();
			long hammerEffectivenessAddress = itemMap.get(FE7Data.Item.HAMMER.ID).getEffectivenessPointer();
			long swordslayerEffectivenessAddress = itemMap.get(FE7Data.Item.SWORDSLAYER.ID).getEffectivenessPointer();
			long dragonEffectivenessAddress = itemMap.get(FE7Data.Item.DRAGON_AXE.ID).getEffectivenessPointer();
			
			if (address == rapierEffectivenessAddress) { return shortForm ? "Eff. Infantry" : "Effective against infantry"; }
			if (address == bowEffectivenessAddress) { return shortForm ? "Eff. Fliers" : "Effective against fliers"; }
			if (address == horseslayerEffectivenessAddress) { return shortForm ? "Eff. Cavalry" : "Effective against cavalry"; }
			if (address == hammerEffectivenessAddress) { return shortForm ? "Eff. Knights" : "Effective against knights"; }
			if (address == swordslayerEffectivenessAddress) { return shortForm ? "Eff. Swordfighters" : "Effective against swordfighters"; }
			if (address == dragonEffectivenessAddress) { return shortForm ? "Eff. Dragons" : "Effective against dragons"; }
			break;
		}
		case FE8: {
			long excaliburStatBonus = itemMap.get(FE8Data.Item.EXCALIBUR.ID).getStatBonusPointer(); // SPD
			long gleipnirStatBonus = itemMap.get(FE8Data.Item.GLEIPNIR.ID).getStatBonusPointer(); // SKL
			long sieglindeStatBonusAddress = itemMap.get(FE8Data.Item.SIEGLINDE.ID).getStatBonusPointer(); // STR
			long ivaldiStatBonusAddress = itemMap.get(FE8Data.Item.IVALDI.ID).getStatBonusPointer(); // DEF
			long vidofnirStatBonusAddress = itemMap.get(FE8Data.Item.VIDOFNIR.ID).getStatBonusPointer(); // DEF
			long audhulmaStatBonusAddress = itemMap.get(FE8Data.Item.AUDHULMA.ID).getStatBonusPointer(); // RES
			long siegmundStatBonusAddress = itemMap.get(FE8Data.Item.SIEGMUND.ID).getStatBonusPointer(); // STR
			long garmStatBonusAddress = itemMap.get(FE8Data.Item.GARM.ID).getStatBonusPointer(); // SPD
			long nidhoggStatBonusAddress = itemMap.get(FE8Data.Item.NIDHOGG.ID).getStatBonusPointer(); // LCK
			
			if (address == excaliburStatBonus || address == garmStatBonusAddress) { return "+5 Speed"; }
			if (address == gleipnirStatBonus) { return "+5 Skill"; }
			if (address == sieglindeStatBonusAddress || address == siegmundStatBonusAddress) { return "+5 Strength"; }
			if (address == ivaldiStatBonusAddress || address == vidofnirStatBonusAddress) { return "+5 Defense"; }
			if (address == audhulmaStatBonusAddress) { return "+5 Resistance"; }
			if (address == nidhoggStatBonusAddress) { return "+5 Luck"; }
			
			long rapierEffectivenessAddress = itemMap.get(FE8Data.Item.RAPIER.ID).getEffectivenessPointer(); // 0x8ADEC2
			long armorslayerEffectivenessAddress = itemMap.get(FE8Data.Item.ARMORSLAYER.ID).getEffectivenessPointer(); // 0x8ADEBB
			long wyrmslayerEffectivenessAddress = itemMap.get(FE8Data.Item.WYRMSLAYER.ID).getEffectivenessPointer(); // 0x8ADF13
			long zanbatoEffectivenessAddress = itemMap.get(FE8Data.Item.ZANBATO.ID).getEffectivenessPointer(); // 0x8ADEE0
			long swordslayerEffectivenessAddress = itemMap.get(FE8Data.Item.SWORDSLAYER.ID).getEffectivenessPointer(); // 0x8ADED7
			long bowEffectivenessAddress = itemMap.get(FE8Data.Item.IRON_BOW.ID).getEffectivenessPointer(); // 0x8ADF2A (includes Wind Sword)
			
			long shadowKillerEffectivenessAddress = itemMap.get(FE8Data.Item.SHADOWKILLER.ID).getEffectivenessPointer(); // 0x8ADF39 (all other legendaries fall under this, as well as Myrrh's Dragonstone)
			long beaconBowEffectivenessAddress = itemMap.get(FE8Data.Item.BEACON_BOW.ID).getEffectivenessPointer(); // 0x8ADEF1 (Monsters + Fliers) (includes Nidhogg)
			
			
			if (address == rapierEffectivenessAddress) { return shortForm ? "Eff. Infantry" : "Effective against infantry"; }
			if (address == bowEffectivenessAddress) { return shortForm ? "Eff. Fliers" : "Effective against fliers"; }
			if (address == zanbatoEffectivenessAddress) { return shortForm ? "Eff. Cavalry" : "Effective against cavalry"; }
			if (address == armorslayerEffectivenessAddress) { return shortForm ? "Eff. Knights" : "Effective against knights"; }
			if (address == swordslayerEffectivenessAddress) { return shortForm ? "Eff. Swordfighters" : "Effective against swordfighters"; }
			if (address == wyrmslayerEffectivenessAddress) { return shortForm ? "Eff. Dragons" : "Effective against dragons"; }
			// Just throw beacon bow under here. it should be obvious that it's still effective against fliers.
			if (address == shadowKillerEffectivenessAddress || address == beaconBowEffectivenessAddress) { return shortForm? "Eff. Monsters" : "Effective against monsters"; }
			break;
		}
		default:
			break;
		}
		
		return null;
	}
	
	public long[] possibleEffectivenessAddresses() {
		switch (gameType) {
		case FE6: return new long[] { offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT)};
		case FE7: return new long[] { offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT)};
		case FE8: return new long[] { offsetsForAdditionalData.get(AdditionalData.KNIGHT_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.KNIGHTCAV_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.CAVALRY_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.MONSTER_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.DRAGON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.MYRMIDON_EFFECT),
				offsetsForAdditionalData.get(AdditionalData.FLIERS_EFFECT)};
		default: return new long[] {};
		}
	}
	
	public Boolean isWeapon(FEItem item) {
		switch (gameType) {
		case FE6:
		case FE7:
			return item != null && item.getType() != WeaponType.NOT_A_WEAPON;
		case FE8:
			// The same check works for normal weapons, but monster weapons need to be included too so that they can be replaced.
			if (item != null) {
				if (item.getType() != WeaponType.NOT_A_WEAPON) {
					return true;
				} else {
					// Cross reference FE8's monster weapon list.
					return FE8Data.Item.allMonsterWeapons.contains(FE8Data.Item.valueOf(item.getID()));
				}
			} else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public Boolean isBasicWeapon(int itemID) {
		switch (gameType) {
		case FE6:
			return FE6Data.Item.isBasicWeapon(itemID);
		case FE7:
			return FE7Data.Item.isBasicWeapon(itemID);
		case FE8:
			return FE8Data.Item.isBasicWeapon(itemID);
		default:
			return false;
		}
	}
	
	public FEItem basicItemOfType(WeaponType type) {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> items = FE6Data.Item.basicItemsOfType(type);
			if (items.size() > 0) { return itemMap.get(itemsFromFE6Items(items)[0].getID()); }
		}
		case FE7: {
			Set<FE7Data.Item> items = FE7Data.Item.basicItemsOfType(type);
			if (items.size() > 0) { return itemMap.get(itemsFromFE7Items(items)[0].getID()); }
		}
		case FE8: {
			Set<FE8Data.Item> items = FE8Data.Item.basicItemsOfType(type);
			if (items.size() > 0) { return itemMap.get(itemsFromFE8Items(items)[0].getID()); }
		}
		default:
			break;
		}
		
		return null;
	}
	
	public FEItem[] itemsOfTypeAndBelowRankValue(WeaponType type, int rankValue, Boolean rangedOnly, Boolean requiresMelee) {
		switch (gameType) {
		case FE6:
			return itemsOfTypeAndBelowRank(type, FE6Data.Item.FE6WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, requiresMelee);
		case FE7:
			return itemsOfTypeAndBelowRank(type, FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, requiresMelee);
		case FE8:
			return itemsOfTypeAndBelowRank(type, FE8Data.Item.FE8WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, requiresMelee);
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] itemsOfTypeAndBelowRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee) {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> weapons = FE6Data.Item.weaponsOfTypeAndRank(type, WeaponRank.NONE, rank, rangedOnly);
			return itemsFromFE6Items(weapons);
		}
		case FE7: {
			Set<FE7Data.Item> weapons = FE7Data.Item.weaponsOfTypeAndRank(type, WeaponRank.NONE, rank, rangedOnly);
			return itemsFromFE7Items(weapons);
		}
		case FE8: {
			Set<FE8Data.Item> weapons = FE8Data.Item.weaponsOfTypeAndRank(type, WeaponRank.NONE, rank, rangedOnly, requiresMelee);
			return itemsFromFE8Items(weapons);
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] itemsOfTypeAndEqualRankValue(WeaponType type, int rankValue, Boolean rangedOnly, Boolean requiresMelee, Boolean allowLower) {
		switch (gameType) {
		case FE6:
			return itemsOfTypeAndEqualRank(type, FE6Data.Item.FE6WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, requiresMelee, allowLower);
		case FE7:
			return itemsOfTypeAndEqualRank(type, FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, requiresMelee, allowLower);
		case FE8:
			return itemsOfTypeAndEqualRank(type, FE8Data.Item.FE8WeaponRank.valueOf(rankValue).toGeneralRank(), rangedOnly, requiresMelee, allowLower);
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public int weaponRankValueForRank(WeaponRank rank) {
		switch (gameType) {
		case FE6: {
			FE6Data.Item.FE6WeaponRank fe6Rank = FE6Data.Item.FE6WeaponRank.rankFromGeneralRank(rank);
			if (fe6Rank != null) { return fe6Rank.value; }
			return 0;
		}
		case FE7: {
			FE7Data.Item.FE7WeaponRank fe7Rank = FE7Data.Item.FE7WeaponRank.rankFromGeneralRank(rank);
			if (fe7Rank != null) { return fe7Rank.value; }
			return 0;
		}
		case FE8: {
			FE8Data.Item.FE8WeaponRank fe8Rank = FE8Data.Item.FE8WeaponRank.rankFromGeneralRank(rank);
			if (fe8Rank != null) { return fe8Rank.value; }
			return 0;
		}
		default:
			break;
		}
		
		return 0;
	}
	
	public FEItem[] itemsOfTypeAndEqualRank(WeaponType type, WeaponRank rank, Boolean rangedOnly, Boolean requiresMelee, Boolean allowLower) {
		switch (gameType) {
		case FE6: {
			if (type == WeaponType.DARK && rank == WeaponRank.E) { rank = WeaponRank.D; } // There is no E rank dark tome, so we need to set a floor of D.
			Set<FE6Data.Item> weapons = FE6Data.Item.weaponsOfTypeAndRank(type, rank, rank, rangedOnly);
			if ((weapons == null || weapons.size() == 0) && allowLower) {
				weapons = FE6Data.Item.weaponsOfTypeAndRank(type, WeaponRank.NONE, rank, rangedOnly);
			}
			return itemsFromFE6Items(weapons);
		}
		case FE7: {
			if (type == WeaponType.DARK && rank == WeaponRank.E) { rank = WeaponRank.D; } // There is no E rank dark tome, so we need to set a floor of D.
			Set<FE7Data.Item> weapons = FE7Data.Item.weaponsOfTypeAndRank(type, rank, rank, rangedOnly);
			if ((weapons == null || weapons.size() == 0) && allowLower) {
				weapons = FE7Data.Item.weaponsOfTypeAndRank(type, WeaponRank.NONE, rank, rangedOnly);
			}
			return itemsFromFE7Items(weapons);
		}
		case FE8: {
			if (type == WeaponType.DARK && rank == WeaponRank.E) { rank = WeaponRank.D; } // There is no E rank dark tome, so we need to set a floor of D.
			Set<FE8Data.Item> weapons = FE8Data.Item.weaponsOfTypeAndRank(type, rank, rank, rangedOnly, requiresMelee);
			if ((weapons == null || weapons.size() == 0) && allowLower) {
				weapons = FE8Data.Item.weaponsOfTypeAndRank(type, WeaponRank.NONE, rank, rangedOnly, requiresMelee);
			}
			return itemsFromFE8Items(weapons);
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] prfWeaponsForClass(int classID) {
		switch (gameType) {
		case FE6:
			return itemsFromFE6Items(FE6Data.Item.prfWeaponsForClassID(classID));
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.prfWeaponsForClassID(classID));
		case FE8:
			return itemsFromFE8Items(FE8Data.Item.prfWeaponsForClassID(classID));
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] getChestRewards() {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> items = new HashSet<FE6Data.Item>();
			items.addAll(FE6Data.Item.allPotentialRewards);
			return itemsFromFE6Items(items);
		}
		case FE7: {
			Set<FE7Data.Item> items = new HashSet<FE7Data.Item>();
			items.addAll(FE7Data.Item.allPotentialRewards);
			return itemsFromFE7Items(items);
		}
		case FE8: {
			Set<FE8Data.Item> items = new HashSet<FE8Data.Item>();
			items.addAll(FE8Data.Item.allPotentialRewards);
			return itemsFromFE8Items(items);
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] relatedItems(int itemID) {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> items = new HashSet<FE6Data.Item>();
			FEItem item = itemWithID(itemID);
			if (item == null) {
				System.err.println("Invalid Item " + Integer.toHexString(itemID));
				break;
			}
			if (item.getType() == WeaponType.NOT_A_WEAPON) {
				if (FE6Data.Item.isStatBooster(item.getID())) {
					items.addAll(FE6Data.Item.allStatBoosters);
				}
				if (FE6Data.Item.isPromotionItem(item.getID())) {
					items.addAll(FE6Data.Item.allPromotionItems);
				}
			} else {
				items.addAll(FE6Data.Item.weaponsOfRank(item.getWeaponRank()));
				items.addAll(FE6Data.Item.weaponsOfType(item.getType()));
				items.removeAll(FE6Data.Item.allSRank);
			}
			
			items.removeIf(i-> i.ID == itemID);
			
			return itemsFromFE6Items(items);
		}
		case FE7: {
			Set<FE7Data.Item> items = new HashSet<FE7Data.Item>();
			FEItem item = itemWithID(itemID);
			if (item == null) {
				System.err.println("Invalid Item " + Integer.toHexString(itemID));
				break;
			}
			if (item.getType() == WeaponType.NOT_A_WEAPON) {
				if (FE7Data.Item.isStatBooster(item.getID())) {
					items.addAll(FE7Data.Item.allStatBoosters);
				}
				if (FE7Data.Item.isPromotionItem(item.getID())) {
					items.addAll(FE7Data.Item.allPromotionItems);
				}
			} else {
				items.addAll(FE7Data.Item.weaponsOfRank(item.getWeaponRank()));
				items.addAll(FE7Data.Item.weaponsOfType(item.getType()));
				items.removeAll(FE7Data.Item.allSRank);
			}
			
			items.removeIf(i-> i.ID == itemID);
			
			return itemsFromFE7Items(items);
		}
		case FE8: {
			Set<FE8Data.Item> items = new HashSet<FE8Data.Item>();
			FEItem item = itemWithID(itemID);
			if (item == null) {
				System.err.println("Invalid Item " + Integer.toHexString(itemID));
				break;
			}
			if (item.getType() == WeaponType.NOT_A_WEAPON) {
				if (FE8Data.Item.isStatBooster(item.getID())) {
					items.addAll(FE8Data.Item.allStatBoosters);
				}
				if (FE8Data.Item.isPromotionItem(item.getID())) {
					items.addAll(FE8Data.Item.allPromotionItems);
				}
			} else {
				items.addAll(FE8Data.Item.weaponsOfRank(item.getWeaponRank()));
				items.addAll(FE8Data.Item.weaponsOfType(item.getType()));
				items.removeAll(FE8Data.Item.allSRank);
			}
			
			items.removeIf(i-> i.ID == itemID);
			
			return itemsFromFE8Items(items);
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public FEItem[] lockedWeaponsToClass(int classID) {
		switch (gameType) {
		case FE6:
			return itemsFromFE6Items(FE6Data.Item.lockedWeaponsToClassID(classID));
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.lockedWeaponsToClassID(classID));
		case FE8:
			return itemsFromFE8Items(FE8Data.Item.lockedWeaponsToClassID(classID));
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public Boolean isHealingStaff(int itemID) {
		switch (gameType) {
		case FE6:
			return FE6Data.Item.allHealingStaves.contains(FE6Data.Item.valueOf(itemID));
		case FE7:
			return FE7Data.Item.allHealingStaves.contains(FE7Data.Item.valueOf(itemID));
		case FE8:
			return FE8Data.Item.allHealingStaves.contains(FE8Data.Item.valueOf(itemID));
		default:
			return false;
		}
	}
	
	public WeaponRank weaponRankFromValue(int rankValue) {
		switch (gameType) {
		case FE6:
			return FE6Data.Item.FE6WeaponRank.valueOf(rankValue).toGeneralRank();
		case FE7:
			return FE7Data.Item.FE7WeaponRank.valueOf(rankValue).toGeneralRank();
		case FE8:
			return FE8Data.Item.FE8WeaponRank.valueOf(rankValue).toGeneralRank();
		default:
			return null;
		}
	}
	
	public FEItem getRandomHealingStaff(WeaponRank maxRank, Random rng) {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> healingStaves = new HashSet<FE6Data.Item>(FE6Data.Item.allHealingStaves);
			if (maxRank.isLowerThan(WeaponRank.S)) { healingStaves.removeAll(FE6Data.Item.allSRank); }
			if (maxRank.isLowerThan(WeaponRank.A)) { healingStaves.removeAll(FE6Data.Item.allARank); }
			if (maxRank.isLowerThan(WeaponRank.B)) { healingStaves.removeAll(FE6Data.Item.allBRank); }
			if (maxRank.isLowerThan(WeaponRank.C)) { healingStaves.removeAll(FE6Data.Item.allCRank); }
			if (maxRank.isLowerThan(WeaponRank.D)) { healingStaves.removeAll(FE6Data.Item.allDRank); }
			FE6Data.Item[] remainingItems = healingStaves.toArray(new FE6Data.Item[healingStaves.size()]);
			return itemMap.get(remainingItems[rng.nextInt(remainingItems.length)].ID);
		}
		case FE7: {
			Set<FE7Data.Item> healingStaves = new HashSet<FE7Data.Item>(FE7Data.Item.allHealingStaves);
			if (maxRank.isLowerThan(WeaponRank.S)) { healingStaves.removeAll(FE7Data.Item.allSRank); }
			if (maxRank.isLowerThan(WeaponRank.A)) { healingStaves.removeAll(FE7Data.Item.allARank); }
			if (maxRank.isLowerThan(WeaponRank.B)) { healingStaves.removeAll(FE7Data.Item.allBRank); }
			if (maxRank.isLowerThan(WeaponRank.C)) { healingStaves.removeAll(FE7Data.Item.allCRank); }
			if (maxRank.isLowerThan(WeaponRank.D)) { healingStaves.removeAll(FE7Data.Item.allDRank); }
			FE7Data.Item[] remainingItems = healingStaves.toArray(new FE7Data.Item[healingStaves.size()]);
			return itemMap.get(remainingItems[rng.nextInt(remainingItems.length)].ID);
		}
		case FE8: {
			Set<FE8Data.Item> healingStaves = new HashSet<FE8Data.Item>(FE8Data.Item.allHealingStaves);
			if (maxRank.isLowerThan(WeaponRank.S)) { healingStaves.removeAll(FE8Data.Item.allSRank); }
			if (maxRank.isLowerThan(WeaponRank.A)) { healingStaves.removeAll(FE8Data.Item.allARank); }
			if (maxRank.isLowerThan(WeaponRank.B)) { healingStaves.removeAll(FE8Data.Item.allBRank); }
			if (maxRank.isLowerThan(WeaponRank.C)) { healingStaves.removeAll(FE8Data.Item.allCRank); }
			if (maxRank.isLowerThan(WeaponRank.D)) { healingStaves.removeAll(FE8Data.Item.allDRank); }
			FE8Data.Item[] remainingItems = healingStaves.toArray(new FE8Data.Item[healingStaves.size()]);
			return itemMap.get(remainingItems[rng.nextInt(remainingItems.length)].ID);
		}
		default:
			return null;
		}
	}
	
	public FEItem getBasicWeaponForCharacter(FECharacter character, Boolean ranged, Boolean mustAttack, Random rng) {
		if (character.getSwordRank() > 0) { return basicItemOfType(WeaponType.SWORD); }
		if (character.getLanceRank() > 0) { return basicItemOfType(WeaponType.LANCE); }
		if (character.getAxeRank() > 0) { return basicItemOfType(WeaponType.AXE); }
		if (character.getBowRank() > 0) { return basicItemOfType(WeaponType.BOW); }
		if (character.getAnimaRank() > 0) { return basicItemOfType(WeaponType.ANIMA); }
		if (character.getLightRank() > 0) { return basicItemOfType(WeaponType.LIGHT); }
		if (character.getDarkRank() > 0) { return basicItemOfType(WeaponType.DARK); }
		if (character.getStaffRank() > 0 && !mustAttack) { return basicItemOfType(WeaponType.STAFF); }
		
		if (gameType == GameType.FE8) {
			if (FE8Data.CharacterClass.allMonsterClasses.contains(FE8Data.CharacterClass.valueOf(character.getClassID()))) {
				FE8Data.Item basicWeapon = FE8Data.Item.basicMonsterWeapon(character.getClassID());
				if (basicWeapon != null) { return itemWithID(basicWeapon.ID); }
			}
		}
		
		return null;
	}
	
	public FEItem getSidegradeWeapon(FEClass targetClass, FEItem originalWeapon, Random rng) {
		if (!isWeapon(originalWeapon)) {
			return null;
		}
		
		FEItem[] potentialItems = comparableWeaponsForClass(targetClass, originalWeapon);
		if (potentialItems == null || potentialItems.length < 1) {
			if (gameType == GameType.FE8) {
				// FE8 has to deal with monster items.
				if (FE8Data.CharacterClass.allMonsterClasses.contains(FE8Data.CharacterClass.valueOf(targetClass.getID()))) {
					FE8Data.Item equivalentWeapon = FE8Data.Item.equivalentMonsterWeapon(originalWeapon.getID(), targetClass.getID());
					if (equivalentWeapon != null) { return itemWithID(equivalentWeapon.ID); }
				}
			}
			return null;
		}
		
		int index = rng.nextInt(potentialItems.length);
		return potentialItems[index];
	}
	
	public FEItem[] comparableWeaponsForClass(FEClass characterClass, FEItem referenceItem) {
		ArrayList<FEItem> items = new ArrayList<FEItem>();
		
		if (characterClass.getSwordRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndEqualRank(WeaponType.SWORD, referenceItem.getWeaponRank(), false, true, true))); }
		if (characterClass.getLanceRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndEqualRank(WeaponType.LANCE, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getAxeRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndEqualRank(WeaponType.AXE, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getBowRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndEqualRank(WeaponType.BOW, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, false, true))); }
		if (characterClass.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndEqualRank(WeaponType.ANIMA, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getLightRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndEqualRank(WeaponType.LIGHT, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getDarkRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndEqualRank(WeaponType.DARK, referenceItem.getWeaponRank(), referenceItem.getMaxRange() > 1, true, true))); }
		if (characterClass.getStaffRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndEqualRank(WeaponType.STAFF, referenceItem.getWeaponRank(), false, false, true))); }
		
		FEItem[] prfWeapons = prfWeaponsForClass(characterClass.getID());
		if (prfWeapons != null) {
			items.addAll(Arrays.asList(prfWeapons));
		}
		
		FEItem[] classWeapons = lockedWeaponsToClass(characterClass.getID());
		if (classWeapons != null) {
			items.addAll(Arrays.asList(classWeapons));
		}
		
		return items.toArray(new FEItem[items.size()]);
	}
	
	public FEItem getRandomWeaponForCharacter(FECharacter character, Boolean ranged, Boolean melee, Random rng) {
		FEItem[] potentialItems = usableWeaponsForCharacter(character, ranged, melee);
		if (potentialItems == null || potentialItems.length < 1) {
			if (gameType == GameType.FE8) {
				// Monster weapons...
				if (FE8Data.CharacterClass.allMonsterClasses.contains(FE8Data.CharacterClass.valueOf(character.getClassID()))) {
					FE8Data.Item basicMonsterWeapon = FE8Data.Item.equivalentMonsterWeapon(0, character.getClassID());
					if (basicMonsterWeapon != null) { return itemWithID(basicMonsterWeapon.ID); }
				}
			}
			return null;
		}
		
		int index = rng.nextInt(potentialItems.length);
		return potentialItems[index];
	}
	
	
	
	private FEItem[] usableWeaponsForCharacter(FECharacter character, Boolean ranged, Boolean melee) {
		ArrayList<FEItem> items = new ArrayList<FEItem>();
		
		if (character.getSwordRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.SWORD, character.getSwordRank(), ranged, melee))); }
		if (character.getLanceRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.LANCE, character.getLanceRank(), ranged, melee))); }
		if (character.getAxeRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.AXE, character.getAxeRank(), ranged, melee))); }
		if (character.getBowRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.BOW, character.getBowRank(), ranged, melee))); }
		if (character.getAnimaRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.ANIMA, character.getAnimaRank(), ranged, melee))); }
		if (character.getLightRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.LIGHT, character.getLightRank(), ranged, melee))); }
		if (character.getDarkRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.DARK, character.getDarkRank(), ranged, melee))); }
		if (character.getStaffRank() > 0) { items.addAll(Arrays.asList(itemsOfTypeAndBelowRankValue(WeaponType.STAFF, character.getStaffRank(), ranged, melee))); }
		
		return items.toArray(new FEItem[items.size()]);
	}
	
	public FEItem[] formerThiefInventory() {
		switch (gameType) {
		case FE6:
			return itemsFromFE6Items(FE6Data.Item.formerThiefKit());
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.formerThiefKit());
		case FE8:
			return itemsFromFE8Items(FE8Data.Item.formerThiefKit());
		default:
			return new FEItem[] {};
		}
	}
	
	public FEItem[] thiefItemsToRemove() {
		switch (gameType) {
		case FE6:
			return itemsFromFE6Items(FE6Data.Item.itemsToRemoveFromFormerThief());
		case FE7:
			return itemsFromFE7Items(FE7Data.Item.itemsToRemoveFromFormerThief());
		case FE8:
			return itemsFromFE8Items(FE8Data.Item.itemsToRemoveFromFormerThief());
		default:
			return new FEItem[] {};
		}
	}
	
	public FEItem[] specialInventoryForClass(int classID, Random rng) {
		switch (gameType) {
		case FE6: {
			Set<FE6Data.Item> items = FE6Data.Item.specialClassKit(classID, rng);
			if (items != null) {
				return itemsFromFE6Items(items);
			}
			break;
		}
		case FE7: {
			Set<FE7Data.Item> items = FE7Data.Item.specialClassKit(classID, rng);
			if (items != null) {
				return itemsFromFE7Items(items);
			}
			break;
		}
		case FE8: {
			Set<FE8Data.Item> items = FE8Data.Item.specialClassKit(classID, rng);
			if (items != null) {
				return itemsFromFE8Items(items);
			}
			break;
		}
		default:
			break;
		}
		
		return new FEItem[] {};
	}
	
	public void commit() {
		for (FEItem item : itemMap.values()) {
			item.commitChanges();
		}
		
		spellAnimations.commit();
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FEItem item : itemMap.values()) {
			item.commitChanges();
			if (item.hasCommittedChanges()) {
				Diff charDiff = new Diff(item.getAddressOffset(), item.getData().length, item.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
		
		spellAnimations.compileDiffs(compiler);
		
		for (AdditionalData promotionItem : promotionItemAddressPointers.keySet()) {
			if (freeSpace.hasOffsetForKey(promotionItem.key)) {
				long offset = freeSpace.getOffsetForKey(promotionItem.key);
				byte[] addressByteArray = WhyDoesJavaNotHaveThese.bytesFromAddress(offset);
				long targetOffset = promotionItemAddressPointers.get(promotionItem);
				compiler.addDiff(new Diff(targetOffset, addressByteArray.length, addressByteArray, null));
			}
		}
	}
	
	private FEItem[] itemsFromFE7Items(Set<FE7Data.Item> fe7Items) {
		if (fe7Items == null) {
			return new FEItem[] {};
		}
		
		List<FE7Data.Item> itemList = new ArrayList<FE7Data.Item>(fe7Items);
		Collections.sort(itemList, FE7Data.Item.itemIDComparator());
		 FEItem[] items = new FEItem[itemList.size()];
		 for (int i = 0; i < itemList.size(); i++) {
			 items[i] = itemWithID(itemList.get(i).ID);
		 }
		 return items;
	}
	
	private FEItem[] itemsFromFE6Items(Set<FE6Data.Item> fe6Items) {
		if (fe6Items == null) {
			return new FEItem[] {};
		}
		
		List<FE6Data.Item> itemList = new ArrayList<FE6Data.Item>(fe6Items);
		Collections.sort(itemList, FE6Data.Item.itemIDComparator());
		FEItem[] items = new FEItem[itemList.size()];
		 for (int i = 0; i < itemList.size(); i++) {
			 items[i] = itemWithID(itemList.get(i).ID);
		 }		
		 return items;
	}
	
	private FEItem[] itemsFromFE8Items(Set<FE8Data.Item> fe8Items) {
		if (fe8Items == null) {
			return new FEItem[] {};
		}
		
		List<FE8Data.Item> itemList = new ArrayList<FE8Data.Item>(fe8Items);
		Collections.sort(itemList, FE8Data.Item.itemIDComparator());
		FEItem[] items = new FEItem[itemList.size()];
		 for (int i = 0; i < itemList.size(); i++) {
			 items[i] = itemWithID(itemList.get(i).ID);
		 }		
		 return items;
	}
	
	public void recordWeapons(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, TextLoader textData, FileHandler handler) {
		for (FEItem item : getAllWeapons()) {
			recordWeapon(rk, item, isInitial, classData, textData, handler);
		}
	}
	
	private void recordWeapon(RecordKeeper rk, FEItem item, Boolean isInitial, ClassDataLoader classData, TextLoader textData, FileHandler handler) {
		int nameIndex = item.getNameIndex();
		String name = textData.getStringAtIndex(nameIndex).trim();
		int descriptionIndex = item.getDescriptionIndex();
		String description = textData.getStringAtIndex(descriptionIndex).trim();
		
		if (isInitial) {
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Description", description);
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Power (MT)", String.format("%d", item.getMight()));
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Accuracy (Hit)", String.format("%d", item.getHit()));
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Weight (WT)", String.format("%d", item.getWeight()));
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Durability", String.format("%d", item.getDurability()));
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Critical", String.format("%d",  item.getCritical()));
			
			long statPointerAddress = item.getStatBonusPointer();
			if (statPointerAddress != 0) {
				statPointerAddress -= 0x8000000;
				if (handler != null) {
					byte[] bonuses = handler.readBytesAtOffset(statPointerAddress, 7);
					List<String> bonusStrings = new ArrayList<String>();
					if (bonuses[0] > 0) { bonusStrings.add("+" + bonuses[0] + " HP"); }
					if (bonuses[1] > 0) { bonusStrings.add("+" + bonuses[1] + ((item.getType() == WeaponType.ANIMA || item.getType() == WeaponType.LIGHT || item.getType() == WeaponType.DARK) ? " Magic" : " Strength")); }
					if (bonuses[2] > 0) { bonusStrings.add("+" + bonuses[2] + " Skill"); }
					if (bonuses[3] > 0) { bonusStrings.add("+" + bonuses[3] + " Speed"); }
					if (bonuses[4] > 0) { bonusStrings.add("+" + bonuses[4] + " Defense"); }
					if (bonuses[5] > 0) { bonusStrings.add("+" + bonuses[5] + " Resistance"); }
					if (bonuses[6] > 0) { bonusStrings.add("+" + bonuses[6] + " Luck"); }
					
					rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", String.join("<br>", bonusStrings));
				} else {
					rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", "No input handler.");
				}
			} else {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", "None");
			}
			
			long effectiveClasses = item.getEffectivenessPointer();
			if (effectiveClasses != 0) {
				effectiveClasses -= 0x8000000;
				if (handler != null) {
					try {
						handler.setNextReadOffset(effectiveClasses);
						byte[] classes = handler.continueReadingBytesUpToNextTerminator(effectiveClasses + 100);
						List<String> classList = new ArrayList<String>();
						for (byte classID : classes) {
							if (classID == 0) { break; }
							FEClass classObject = classData.classForID(classID);
							if (classObject == null) {
								classList.add("Unknown (0x" + Integer.toHexString(classID).toUpperCase() + ")");
							} else {
								if (classData.isFemale(classID)) {
									classList.add(textData.getStringAtIndex(classObject.getNameIndex()).trim() + " (F)");
								} else {
									classList.add(textData.getStringAtIndex(classObject.getNameIndex()).trim());
								}
							}
						}
						rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", String.join("<br>", classList));
					} catch (IOException e) {
						e.printStackTrace();
						rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "Error");
					}
				} else {
					rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "No input handler.");
				}
			} else {
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "None");
			}
			
			int ability1Value = item.getAbility1();
			int ability2Value = item.getAbility2();
			int ability3Value = item.getAbility3();
			int effectValue = item.getWeaponEffect();
			switch(gameType) {
			case FE6:
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", FE6Data.Item.Ability1Mask.stringOfActiveAbilities(ability1Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", FE6Data.Item.Ability2Mask.stringOfActiveAbilities(ability2Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effect", FE6Data.Item.WeaponEffect.stringOfActiveEffect(effectValue));
				break;
			case FE7:
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", FE7Data.Item.Ability1Mask.stringOfActiveAbilities(ability1Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", FE7Data.Item.Ability2Mask.stringOfActiveAbilities(ability2Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Ability 3", FE7Data.Item.Ability3Mask.stringOfActiveAbilities(ability3Value, "<br>"));
				rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Effect", FE7Data.Item.WeaponEffect.stringOfActiveEffect(effectValue));
				break;
			default:
				break;
			}
			
			rk.recordOriginalEntry(RecordKeeperCategoryWeaponKey, name, "Range", String.format("%d ~ %d", item.getMinRange(), item.getMaxRange()));
			
		} else {
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Description", description);
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Power (MT)", String.format("%d", item.getMight()));
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Accuracy (Hit)", String.format("%d", item.getHit()));
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Weight (WT)", String.format("%d", item.getWeight()));
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Durability", String.format("%d", item.getDurability()));
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Critical", String.format("%d",  item.getCritical()));
			
			long statPointerAddress = item.getStatBonusPointer();
			if (statPointerAddress != 0) {
				statPointerAddress -= 0x8000000;
				if (handler != null) {
					byte[] bonuses = handler.readBytesAtOffset(statPointerAddress, 7);
					List<String> bonusStrings = new ArrayList<String>();
					if (bonuses[0] > 0) { bonusStrings.add("+" + bonuses[0] + " HP"); }
					if (bonuses[1] > 0) { bonusStrings.add("+" + bonuses[1] + ((item.getType() == WeaponType.ANIMA || item.getType() == WeaponType.LIGHT || item.getType() == WeaponType.DARK) ? " Magic" : " Strength")); }
					if (bonuses[2] > 0) { bonusStrings.add("+" + bonuses[2] + " Skill"); }
					if (bonuses[3] > 0) { bonusStrings.add("+" + bonuses[3] + " Speed"); }
					if (bonuses[4] > 0) { bonusStrings.add("+" + bonuses[4] + " Defense"); }
					if (bonuses[5] > 0) { bonusStrings.add("+" + bonuses[5] + " Resistance"); }
					if (bonuses[6] > 0) { bonusStrings.add("+" + bonuses[6] + " Luck"); }
					
					rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", String.join("<br>", bonusStrings));
				} else {
					rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", "No output handler.");
				}
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Stat Bonus", "None");
			}
			
			long effectiveClasses = item.getEffectivenessPointer();
			if (effectiveClasses != 0) {
				effectiveClasses -= 0x8000000;
				if (handler != null) {
					try {
						handler.setNextReadOffset(effectiveClasses);
						byte[] classes = handler.continueReadingBytesUpToNextTerminator(effectiveClasses + 100);
						List<String> classList = new ArrayList<String>();
						for (byte classID : classes) {
							if (classID == 0) { break; }
							FEClass classObject = classData.classForID(classID);
							if (classObject == null) {
								classList.add("Unknown (0x" + Integer.toHexString(classID).toUpperCase() + ")");
							} else {
								if (classData.isFemale(classID)) {
									classList.add(textData.getStringAtIndex(classObject.getNameIndex()).trim() + " (F)");
								} else {
									classList.add(textData.getStringAtIndex(classObject.getNameIndex()).trim());
								}
							}
						}
						rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", String.join("<br>", classList));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "Error");
					}
				} else {
					rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "No output handler.");
				}
			} else {
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effectiveness", "None");
			}
			
			int ability1Value = item.getAbility1();
			int ability2Value = item.getAbility2();
			int ability3Value = item.getAbility3();
			int effectValue = item.getWeaponEffect();
			switch(gameType) {
			case FE6:
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", FE6Data.Item.Ability1Mask.stringOfActiveAbilities(ability1Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", FE6Data.Item.Ability2Mask.stringOfActiveAbilities(ability2Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effect", FE6Data.Item.WeaponEffect.stringOfActiveEffect(effectValue));
				break;
			case FE7:
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 1", FE7Data.Item.Ability1Mask.stringOfActiveAbilities(ability1Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 2", FE7Data.Item.Ability2Mask.stringOfActiveAbilities(ability2Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Ability 3", FE7Data.Item.Ability3Mask.stringOfActiveAbilities(ability3Value, "<br>"));
				rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Effect", FE7Data.Item.WeaponEffect.stringOfActiveEffect(effectValue));
				break;
			default:
				break;
			}
			
			rk.recordUpdatedEntry(RecordKeeperCategoryWeaponKey, name, "Range", String.format("%d ~ %d", item.getMinRange(), item.getMaxRange()));
		}
	}
}
