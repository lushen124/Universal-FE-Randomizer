package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Data.Item;
import fedata.gcnwii.fe9.FE9Data.Item.Effectiveness;
import fedata.gcnwii.fe9.FE9Data.Item.ItemType;
import fedata.gcnwii.fe9.FE9Data.Item.WeaponTraits;
import fedata.gcnwii.fe9.FE9Item;
import io.gcn.GCNDBXFileHandler;
import io.gcn.GCNDataFileHandler;
import io.gcn.GCNDataFileHandlerV2;
import io.gcn.GCNDataFileHandlerV2.GCNDataFileDataSection;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import ui.model.MinMaxOption;
import util.DebugPrinter;
import util.Diff;
import util.WhyDoesJavaNotHaveThese;
import util.recordkeeper.fe9.ChangelogBuilder;
import util.recordkeeper.fe9.ChangelogHeader;
import util.recordkeeper.fe9.ChangelogSection;
import util.recordkeeper.fe9.ChangelogStyleRule;
import util.recordkeeper.fe9.ChangelogTOC;
import util.recordkeeper.fe9.ChangelogTable;
import util.recordkeeper.fe9.ChangelogHeader.HeaderLevel;

public class FE9ItemDataLoader {
	
	public enum WeaponRank {
		NONE, E, D, C, B, A, S, UNKNOWN;
		
		public static WeaponRank rankForCharacter(char c) {
			switch (c) {
			case 'E': return E;
			case 'D': return D;
			case 'C': return C;
			case 'B': return B;
			case 'A': return A;
			case 'S': return S;
			case '*': return UNKNOWN;
			case '-': return NONE;
			default: return null;
			}
		}
		
		public boolean isHigherThan(WeaponRank rank) {
			switch (this) {
			case E: return rank == UNKNOWN;
			case D: return rank == UNKNOWN || rank == E;
			case C: return rank == UNKNOWN || rank == E || rank == D;
			case B: return rank == UNKNOWN || rank == E || rank == D || rank == C;
			case A: return rank == UNKNOWN || rank == E || rank == D || rank == C || rank == B;
			case S: return rank == UNKNOWN || rank == E || rank == D || rank == C || rank == B || rank == A;
			default: return false;
			}
		}
		
		public boolean isLowerThan(WeaponRank rank) {
			return this != rank && !isHigherThan(rank);
		}
		
		public WeaponRank higherRank() {
			switch (this) {
			case E: return D;
			case D: return C;
			case C: return B;
			case B: return A;
			case A: return S;
			default: return NONE;
			}
		}
		
		public WeaponRank lowerRank() {
			switch (this) {
			case S: return A;
			case A: return B;
			case B: return C;
			case C: return D;
			case D: return E;
			default: return NONE;
			}
		}
	}
	
	public enum WeaponType {
		NONE, SWORD, LANCE, AXE, BOW, FIRE, THUNDER, WIND, LIGHT, STAFF, KNIFE;
		
		public static Comparator<WeaponType> getComparator() {
			return new Comparator<WeaponType>() {
				@Override
				public int compare(WeaponType arg0, WeaponType arg1) {
					return arg0.toString().compareTo(arg1.toString());
				}
			};
		}
	}
	
	public enum WeaponEffect {
		NONE, STAT_BOOST, EFFECTIVENESS, UNBREAKABLE, BRAVE, REVERSE_TRIANGLE, EXTEND_RANGE, CRITICAL, MAGIC_DAMAGE, POISON, STEAL_HP, CRIT_IMMUNE, NO_CRIT;
		
		public enum InfoKey {
			CRITICAL_RANGE;
		}
		
		public Map<InfoKey, Object> additionalInfo;
		
		private WeaponEffect() {
			additionalInfo = new HashMap<InfoKey, Object>();
		}
	}
	
	List<FE9Item> allItems;
	
	Map<String, FE9Item> idLookup;
	
	GCNDataFileHandlerV2 fe8databin;
	GCNDataFileDataSection itemDataSection;
	FE9CommonTextLoader textLoader;
	
	public FE9ItemDataLoader(GCNISOHandler isoHandler, FE9CommonTextLoader commonTextLoader) throws GCNISOException {
		textLoader = commonTextLoader;
		
		allItems = new ArrayList<FE9Item>();
		
		idLookup = new HashMap<String, FE9Item>();
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.ItemDataFilename);
		assert (handler instanceof GCNDataFileHandlerV2);
		if (handler instanceof GCNDataFileHandlerV2) {
			fe8databin = (GCNDataFileHandlerV2)handler;
		}
		
		itemDataSection = fe8databin.getSectionWithName(FE9Data.ItemDataSectionName);
		int count = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(itemDataSection.getRawData(0, 4), false);
		
		long offset = 4;
		for (int i = 0; i < count; i++) {
			long dataOffset = offset + i * FE9Data.ItemDataSize;
			FE9Item item = new FE9Item(itemDataSection.getRawData(dataOffset, FE9Data.ItemDataSize), dataOffset);
			allItems.add(item);
			
			//debugPrintItem(item, handler, commonTextLoader);
			long iidPtr = item.getItemIDPointer();
			String iid = fe8databin.stringForPointer(iidPtr);
			idLookup.put(iid, item);
		}
	}
	
	public List<FE9Item> allItems() {
		return allItems;
	}
	
	public FE9Item itemWithIID(String iid) {
		return idLookup.get(iid);
	}
	
	public String iidOfItem(FE9Item item) {
		assert (fe8databin != null);
		return fe8databin.stringForPointer(item.getItemIDPointer());
	}
	
	public String getMIIDOfItem(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemNamePointer());
	}
	
	public String getMHIForItem(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemDescriptionPointer());
	}
	
	public String getEquipType(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemTypePointer());
	}
	
	public String getRealType(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemSubtypePointer());
	}
	
	public void setRealType(FE9Item item, String typeString) {
		if (item == null) { return; }
		if (typeString == null) { return; }
		
		fe8databin.addString(typeString);
		Long ptr = fe8databin.pointerForString(typeString);
		item.setItemSubtypePointer(ptr);
	}
	
	public String getRank(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemRankPointer());
	}
	
	public String[] getItemTraits(FE9Item item) {
		if (item == null) { return null; }
		String[] traits = new String[6];
		traits[0] = fe8databin.stringForPointer(item.getItemTrait1Pointer());
		traits[1] = fe8databin.stringForPointer(item.getItemTrait2Pointer());
		traits[2] = fe8databin.stringForPointer(item.getItemTrait3Pointer());
		traits[3] = fe8databin.stringForPointer(item.getItemTrait4Pointer());
		traits[4] = fe8databin.stringForPointer(item.getItemTrait5Pointer());
		traits[5] = fe8databin.stringForPointer(item.getItemTrait6Pointer());
		return traits;
	}
	
	public void setItemTraits(FE9Item item, String[] traitsArray) {
		if (item == null || traitsArray == null) { return; }
		assert(traitsArray.length == 6);
		long[] pointers = new long[6];
		for (int i = 0; i < Math.min(traitsArray.length, 6); i++) {
			if (traitsArray[i] == null || traitsArray[i].length() == 0) {
				pointers[i] = 0;
			} else {
				fe8databin.addString(traitsArray[i]);
				pointers[i] = fe8databin.pointerForString(traitsArray[i]);
			}
		}
		
		if (pointers[0] != 0) { fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait1Offset); }
		if (pointers[1] != 0) { fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait2Offset); }
		if (pointers[2] != 0) { fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait3Offset); }
		if (pointers[3] != 0) { fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait4Offset); }
		if (pointers[4] != 0) { fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait5Offset); }
		if (pointers[5] != 0) { fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait6Offset); }
		
		item.setItemTrait1Pointer(pointers[0]);
		item.setItemTrait2Pointer(pointers[1]);
		item.setItemTrait3Pointer(pointers[2]);
		item.setItemTrait4Pointer(pointers[3]);
		item.setItemTrait5Pointer(pointers[4]);
		item.setItemTrait6Pointer(pointers[5]);
	}
	
	public boolean hasTrait(FE9Item item, String traitString) {
		Set<String> existingTraits = new HashSet<String>();
		existingTraits.addAll(Arrays.asList(getItemTraits(item)));
		return existingTraits.contains(traitString);
	}
	
	public boolean addTraitToItem(FE9Item item, String traitString, boolean force) {
		if (item == null || traitString == null) { return false; }
		
		if (hasTrait(item, traitString)) { return false; }
		
		fe8databin.addString(traitString);
		Long pointer = fe8databin.pointerForString(traitString);
		
		if (fe8databin.stringForPointer(item.getItemTrait1Pointer()) == null) { 
			item.setItemTrait1Pointer(pointer);
			fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait1Offset);
		} else if (fe8databin.stringForPointer(item.getItemTrait2Pointer()) == null) {
			item.setItemTrait2Pointer(pointer);
			fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait2Offset);
		} else if (fe8databin.stringForPointer(item.getItemTrait3Pointer()) == null) { 
			item.setItemTrait3Pointer(pointer);
			fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait3Offset);
		} else if (fe8databin.stringForPointer(item.getItemTrait4Pointer()) == null) {
			item.setItemTrait4Pointer(pointer);
			fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait4Offset);
		} else if (fe8databin.stringForPointer(item.getItemTrait5Pointer()) == null) { 
			item.setItemTrait5Pointer(pointer);
			fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait5Offset);
		} else if (fe8databin.stringForPointer(item.getItemTrait6Pointer()) == null || force) { 
			item.setItemTrait6Pointer(pointer);
			fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemTrait6Offset);
		} else {
			return false;
		}
		
		return true;
	}
	
	public String getEffectiveness1ForItem(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemEffectiveness1Pointer());
	}
	
	public void setEffectiveness1ForItem(FE9Item item, String effective) {
		if (item == null) { return; }
		if (effective == null) { 
			item.setItemEffectiveness1Pointer(0);
			return;
		}
		
		fe8databin.addString(effective);
		Long ptr = fe8databin.pointerForString(effective);
		item.setItemEffectiveness1Pointer(ptr);
		
		fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemEffectiveness1Offset);
	}
	
	public String getEffectiveness2ForItem(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemEffectiveness2Pointer());
	}
	
	public void setEffectiveness2ForItem(FE9Item item, String effective) {
		if (item == null) { return; }
		if (effective == null) { 
			item.setItemEffectiveness2Pointer(0);
			return;
		}
		
		fe8databin.addString(effective);
		Long ptr = fe8databin.pointerForString(effective);
		item.setItemEffectiveness2Pointer(ptr);
		
		fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemEffectiveness2Offset);
	}
	
	public String getAnimation1ForItem(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemEffectAnimation1Pointer());
	}
	
	public void setAnimation1ForItem(FE9Item item, String animation) {
		if (item == null) { return; }
		if (animation == null) { 
			item.setItemEffectAnimation1Pointer(0);
			return;
		}
		
		fe8databin.addString(animation);
		Long ptr = fe8databin.pointerForString(animation);
		item.setItemEffectAnimation1Pointer(ptr);
		
		fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemAnimation1Offset);
	}
	
	public String getAnimation2ForItem(FE9Item item) {
		return fe8databin.stringForPointer(item.getItemEffectAnimation2Pointer());
	}
	
	public void setAnimation2ForItem(FE9Item item, String animation) {
		if (item == null) { return; }
		if (animation == null) { 
			item.setItemEffectAnimation2Pointer(0);
			return;
		}
		
		fe8databin.addString(animation);
		Long ptr = fe8databin.pointerForString(animation);
		item.setItemEffectAnimation2Pointer(ptr);
		
		fe8databin.addPointerOffset(itemDataSection, item.getAddressOffset() + FE9Item.ItemAnimation2Offset);
	}
	
	public boolean isWeapon(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isWeapon();
	}
	
	public boolean isStaff(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isStaff();
	}
	
	public boolean isBlacklisted(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isBlacklisted();
	}
	
	public boolean isBasicWeapon(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isBasicWeapon();
	}
	
	public boolean isLaguzWeapon(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isLaguzWeapon();
	}
	
	public boolean isConsumable(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isConsumable();
	}
	
	public boolean isPromotionItem(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isPromotionItem();
	}
	
	public boolean isStatBooster(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isStatBooster();
	}
	
	public boolean isTreasure(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isTreasure();
	}
	
	public boolean isSkillScroll(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).isSkillScroll();
	}
	
	public boolean doesMagicDamage(FE9Item item) {
		return FE9Data.Item.withIID(iidOfItem(item)).doesMagicDamage();
	}
	
	public boolean applyEffectToWeapon(WeaponEffect effect, FE9Item item, GCNISOHandler handler, FE9CommonTextLoader textData, Random rng) {
		if (item == null || effect == null) { return false; }
		if (!isWeapon(item) || isStaff(item)) { return false; }
		boolean applied;
		switch (effect) {
		case NONE:
			return false;
		case STAT_BOOST:
			boolean assigned = false;
			while (!assigned) {
				switch (rng.nextInt(6)) {
				case 0:
					if (doesMagicDamage(item)) {
						if (item.getMAGBonus() == 0) {
							item.setMAGBonus(5);
							assigned = true;
						}
					} 
					else {
						if (item.getSTRBonus() == 0) {
							item.setSTRBonus(5);
							assigned = true;
						}
					}
					break;
				case 1:
					if (item.getSKLBonus() == 0) {
						item.setSKLBonus(5);
						assigned = true;
					}
					break;
				case 2:
					if (item.getSPDBonus() == 0) {
						item.setSPDBonus(5);
						assigned = true;
					}
					break;
				case 3:
					if (item.getLCKBonus() == 0) {
						item.setLCKBonus(5);
						assigned = true;
					}
					break;
				case 4:
					if (item.getDEFBonus() == 0) {
						item.setDEFBonus(5);
						assigned = true;
					}
					break;
				case 5:
					if (item.getRESBonus() == 0) {
						item.setRESBonus(5);
						assigned = true;
					}
					break;
				}
			}
			break;
		case EFFECTIVENESS:
			if (getEffectiveness1ForItem(item) == null) {
				Effectiveness[] effects = Effectiveness.eligibleEffects();
				Effectiveness selectedEffectiveness = effects[rng.nextInt(effects.length)];
				setEffectiveness1ForItem(item, selectedEffectiveness.getEffectString());
			} else if (getEffectiveness2ForItem(item) == null) {
				Effectiveness[] effects = Effectiveness.eligibleEffects();
				Effectiveness selectedEffectiveness = effects[rng.nextInt(effects.length)];
				while (selectedEffectiveness.getEffectString().equals(getEffectiveness1ForItem(item))) {
					selectedEffectiveness = effects[rng.nextInt(effects.length)];
				}
				setEffectiveness2ForItem(item, selectedEffectiveness.getEffectString());
			} else {
				return false;
			}
			break;
		case UNBREAKABLE:
			if (isLaguzWeapon(item)) { return false; }
			applied = addTraitToItem(item, FE9Data.Item.WeaponTraits.UNBREAKABLE.getTraitString(), false);
			if (applied == false) { return false; }
			item.setItemCost(item.getItemCost() * 100);
			item.setItemDurability(1);
			break;
		case BRAVE:
			applied = addTraitToItem(item, FE9Data.Item.WeaponTraits.BRAVE.getTraitString(), false);
			if (applied == false) { return false; }
			break;
		case REVERSE_TRIANGLE: {
			if (isLaguzWeapon(item)) { return false; }
			FE9Data.Item.ItemType type = FE9Data.Item.ItemType.typeWithString(getRealType(item));
			int triangleAffinity = rng.nextInt(3);
			switch (type) {
			case SWORD:
				setRealType(item, triangleAffinity == 0 ? ItemType.AXE.getTypeString() : (triangleAffinity == 1 ? ItemType.LANCE.getTypeString() : ItemType.BOW.getTypeString()));
				break;
			case LANCE:
				setRealType(item, triangleAffinity == 0 ? ItemType.SWORD.getTypeString() : (triangleAffinity == 1 ? ItemType.AXE.getTypeString() : ItemType.BOW.getTypeString()));
				break;
			case AXE:
				setRealType(item, triangleAffinity == 0 ? ItemType.LANCE.getTypeString() : (triangleAffinity == 1 ? ItemType.SWORD.getTypeString() : ItemType.BOW.getTypeString()));
				break;
			case FIRE:
				setRealType(item, triangleAffinity == 0 ? ItemType.WIND.getTypeString() : (triangleAffinity == 1 ? ItemType.THUNDER.getTypeString() : ItemType.STAFF_LIGHT.getTypeString()));
				break;
			case THUNDER:
				setRealType(item, triangleAffinity == 0 ? ItemType.FIRE.getTypeString() : (triangleAffinity == 1 ? ItemType.WIND.getTypeString() : ItemType.STAFF_LIGHT.getTypeString()));
				break;
			case WIND:
				setRealType(item, triangleAffinity == 0 ? ItemType.THUNDER.getTypeString() : (triangleAffinity == 1 ? ItemType.FIRE.getTypeString() : ItemType.STAFF_LIGHT.getTypeString()));
				break;
			case BOW:
				setRealType(item, triangleAffinity == 0 ? ItemType.SWORD.getTypeString() : (triangleAffinity == 1 ? ItemType.LANCE.getTypeString() : ItemType.AXE.getTypeString()));
				break;
			case STAFF_LIGHT:
				setRealType(item, triangleAffinity == 0 ? ItemType.FIRE.getTypeString() : (triangleAffinity == 1 ? ItemType.WIND.getTypeString() : ItemType.THUNDER.getTypeString()));
			default:
				return false;
			}
			break;
		}
		case EXTEND_RANGE: {
			if (isLaguzWeapon(item)) { return false; }
			FE9Data.Item.ItemType type = FE9Data.Item.ItemType.typeWithString(getRealType(item));
			switch (type) {
			case SWORD:
				if (item.getMaximumRange() == 1) {
					// We need to add shockwave animations when taking a sword from melee to ranged.
					applied = addTraitToItem(item, FE9Data.Item.WeaponTraits.RANGED_PHYSICAL_SWORD.getTraitString(), false);
					if (applied == false) { return false; }
					setAnimation1ForItem(item, FE9Data.Item.EffectAnimation1.RAGNELL_SHOCKWAVE.getAnimationString());
					
					try {
						GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
						dbx.setStringForKey("effectId", 1, FE9Data.Item.EffectAnimation1.RAGNELL_SHOCKWAVE.getAnimationString());
					} catch (GCNISOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				}
				item.setRange(item.getMinimumRange(), item.getMaximumRange() + 1);
				break;
			case LANCE:
				if (item.getMaximumRange() == 1) {
					try {
						GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
						dbx.setStringForKey("kind", 1, "6");
						dbx.setStringForKey("missile", 1, "1");
					} catch (GCNISOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				}
				item.setRange(item.getMinimumRange(), item.getMaximumRange() + 1);
				break;
			case AXE:
				if (item.getMaximumRange() == 1) {
					try {
						GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
						dbx.setStringForKey("kind", 1, "7");
						dbx.setStringForKey("missile", 1, "1");
					} catch (GCNISOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				}
				item.setRange(item.getMinimumRange(), item.getMaximumRange() + 1);
				break;
			case BOW:
				if (rng.nextInt(2) == 0) {
					item.setRange(item.getMinimumRange() - 1, item.getMaximumRange());
				} else {
					item.setRange(item.getMinimumRange(), item.getMaximumRange() + 1);
				}
				break;
			case FIRE:
			case THUNDER:
			case WIND:
			case STAFF_LIGHT:
				if (isSiegeTome(item)) { return false; }
				item.setRange(item.getMinimumRange(), item.getMaximumRange() + 1);
				break;
			default:
				return false;
			}
		}
		break;
		case CRITICAL:
			if (item.getItemCritical() >= 25) { return false; }
			MinMaxOption range = (MinMaxOption)effect.additionalInfo.get(WeaponEffect.InfoKey.CRITICAL_RANGE);
			int bonusCrit = 5 * ((range.minValue / 5) + rng.nextInt(((range.maxValue - range.minValue) / 5) + 1));
			item.setItemCritical(item.getItemCritical() + bonusCrit);
			break;
		case MAGIC_DAMAGE:
			if (doesMagicDamage(item)) { return false; }
			FE9Data.Item.ItemType type = FE9Data.Item.ItemType.typeWithString(getRealType(item));
			switch (type) {
			case SWORD:
				try {
					applied = addTraitToItem(item, WeaponTraits.MAGIC_SWORD.getTraitString(), false);
					if (!applied) { return false; }
					GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
					dbx.setStringForKey("kind", 1, "8");
					dbx.setStringForKey("missile", 1, "2");
					dbx.setStringForKey("effectId", 1, "EID_K_LIGHT2_WP");
					dbx.setStringForKey("damageEffId", 1, "EID_K_LIGHT2_WP");
					
					setAnimation1ForItem(item, FE9Data.Item.EffectAnimation1.LIGHT.getAnimationString());
					setAnimation2ForItem(item, FE9Data.Item.EffectAnimation2.LIGHT.getAnimationString());
				} catch (GCNISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				break;
			case LANCE:
				try {
					GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
					dbx.setStringForKey("kind", 1, "6");
					dbx.setStringForKey("missile", 1, "1");
					dbx.setStringForKey("effectId", 1, "EID_K_LIGHT2_WP");
					dbx.setStringForKey("damageEffId", 1, "EID_K_LIGHT2_WP");
					
					setAnimation1ForItem(item, FE9Data.Item.EffectAnimation1.LIGHT.getAnimationString());
					setAnimation2ForItem(item, FE9Data.Item.EffectAnimation2.LIGHT.getAnimationString());
				} catch (GCNISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				break;
			case AXE:
				try {
					GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
					dbx.setStringForKey("kind", 1, "7");
					dbx.setStringForKey("missile", 1, "1");
					dbx.setStringForKey("effectId", 1, "EID_K_LIGHT2_WP");
					dbx.setStringForKey("damageEffId", 1, "EID_K_LIGHT2_WP");
					
					setAnimation1ForItem(item, FE9Data.Item.EffectAnimation1.LIGHT.getAnimationString());
					setAnimation2ForItem(item, FE9Data.Item.EffectAnimation2.LIGHT.getAnimationString());
				} catch (GCNISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				break;
			case BOW:
				try {
					GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
					dbx.setStringForKey("kind", 1, "3");
					dbx.setStringForKey("missile", 1, "1");
					dbx.setStringForKey("effectId", 1, "EID_K_LIGHT2_WP");
					dbx.setStringForKey("damageEffId", 1, "EID_K_LIGHT2_WP");
					
					setAnimation1ForItem(item, FE9Data.Item.EffectAnimation1.LIGHT.getAnimationString());
					setAnimation2ForItem(item, FE9Data.Item.EffectAnimation2.LIGHT.getAnimationString());
				} catch (GCNISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				break;
			case KNIFE:
				try {
					GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
					dbx.setStringForKey("effectId", 1, "EID_K_LIGHT2_WP");
					dbx.setStringForKey("damageEffId", 1, "EID_K_LIGHT2_WP");
					
					setAnimation1ForItem(item, FE9Data.Item.EffectAnimation1.LIGHT.getAnimationString());
					setAnimation2ForItem(item, FE9Data.Item.EffectAnimation2.LIGHT.getAnimationString());
				} catch (GCNISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				break;
			case LAGUZ:
				try {
					GCNDBXFileHandler dbx = (GCNDBXFileHandler)handler.handlerForFileWithName(FE9Data.ItemDBXFilePath + FE9Data.Item.withIID(iidOfItem(item)).getDBX());
					dbx.setStringForKey("effectId", 1, "EID_K_LIGHT2_WP");
					dbx.setStringForKey("damageEffId", 1, "EID_K_LIGHT2_WP");
					
					setAnimation1ForItem(item, FE9Data.Item.EffectAnimation1.LIGHT.getAnimationString());
					setAnimation2ForItem(item, FE9Data.Item.EffectAnimation2.LIGHT.getAnimationString());
				} catch (GCNISOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				break;
			default:
				return false;
			}
			
			ItemType newType = ItemType.STAFF_LIGHT;
			setRealType(item, newType.getTypeString());
			break;
		case POISON:
			assigned = addTraitToItem(item, WeaponTraits.POISON.getTraitString(), false);
			if (!assigned) { return false; }
			break;
		case STEAL_HP:
			assigned = addTraitToItem(item, WeaponTraits.STEAL_HP.getTraitString(), false);
			if (!assigned) { return false; }
			break;
		case CRIT_IMMUNE:
			assigned = addTraitToItem(item, WeaponTraits.CANNOT_BE_CRIT.getTraitString(), false);
			if (!assigned) { return false; }
			break;
		case NO_CRIT:
			assigned = addTraitToItem(item, WeaponTraits.CANNOT_CRIT.getTraitString(), false);
			if (!assigned) { return false; }
			item.setItemAccuracy(item.getItemAccuracy() + 50);
			item.setWeaponExperience(item.getWeaponExperience() * 3);
			break;
		}
		
		List<WeaponEffect> activeEffects = getWeaponEffects(item);
		List<String> existingDescriptions = activeEffects.stream().map(currentEffect -> {
			switch (currentEffect) {
			case STAT_BOOST:
				List<String> boosts = new ArrayList<String>();
				if (item.getSTRBonus() > 0) { boosts.add("+" + item.getSTRBonus() + " Strength"); }
				if (item.getMAGBonus() > 0) { boosts.add("+" + item.getMAGBonus() + " Magic"); }
				if (item.getSKLBonus() > 0) { boosts.add("+" + item.getSKLBonus() + " Skill"); }
				if (item.getSPDBonus() > 0) { boosts.add("+" + item.getSPDBonus() + " Speed"); }
				if (item.getLCKBonus() > 0) { boosts.add("+" + item.getLCKBonus() + " Luck"); }
				if (item.getDEFBonus() > 0) { boosts.add("+" + item.getDEFBonus() + " Defense"); }
				if (item.getRESBonus() > 0) { boosts.add("+" + item.getRESBonus() + " Resistance"); }
				return "Grants " + String.join(", ", boosts) + " while equipped.";
			case EFFECTIVENESS:
				FE9Data.Item.Effectiveness effect1 = FE9Data.Item.Effectiveness.effectWithString(getEffectiveness1ForItem(item));
				FE9Data.Item.Effectiveness effect2 = FE9Data.Item.Effectiveness.effectWithString(getEffectiveness2ForItem(item));
				if (effect1 == Effectiveness.DOORS) { effect1 = null; }
				if (effect2 == Effectiveness.DOORS) { effect2 = null; }
				List<String> effects = new ArrayList<String>();
				if (effect1 != null) { effects.add(effect1.getShortDescription()); }
				if (effect2 != null) { effects.add(effect2.getShortDescription()); }
				if (effects.isEmpty()) { return ""; }
				return "Effective against " + String.join(" and ", effects) + " units.";
			case UNBREAKABLE:
				return "Unbreakable.";
			case BRAVE:
				return "Strikes consecutively.";
			case REVERSE_TRIANGLE:
				switch (FE9Data.Item.ItemType.typeWithString(getRealType(item))) {
				case SWORD:
					return "Good against axes, weak against lances.";
				case LANCE:
					return "Good against swords, weak against axes.";
				case AXE:
					return "Good against lances, weak against swords.";
				case FIRE:
					return "Good against wind magic, weak against thunder magic.";
				case WIND:
					return "Good against thunder magic, weak against fire magic.";
				case THUNDER:
					return "Good against fire magic, weak against wind magic.";
				case BOW:
				case STAFF_LIGHT:
					return "Not affected by the weapon triangle.";
				default:
					return "";
				}
			case EXTEND_RANGE:
				switch (FE9Data.Item.ItemType.typeWithString(getEquipType(item))) {
				case BOW:
					if (item.getMinimumRange() < 2) { return "Can be used in close quarters."; }
					if (item.getMaximumRange() > 2) { return "Has extended range."; }
					break;
				case SWORD:
				case LANCE:
				case AXE:
					return "Can attack at range.";
				default:
					return "";
				}
				break;
			case CRITICAL:
				return "High critical rate.";
			case MAGIC_DAMAGE:
				return "Deals magical damage.";
			case POISON:
				return "Poisons on hit.";
			case STEAL_HP:
				return "Drains life from target.";
			case CRIT_IMMUNE:
				return "Prevents critical hits while equipped.";
			case NO_CRIT:
				return "Accurate, but unable to cause critical hits.";
			default:
				return "";
			}
			
			return "";
		}).filter(string -> { return string.length() > 0; }).collect(Collectors.toList());
		
		int lengths = existingDescriptions.stream().map(string -> { return string.length(); }).reduce(0, (subtotal, element) -> subtotal + element);
		while (lengths > 80) {
			if (effect != WeaponEffect.UNBREAKABLE && existingDescriptions.contains("Unbreakable.")) { existingDescriptions.remove("Unbreakable."); }
			else if (effect != WeaponEffect.CRITICAL && existingDescriptions.contains("High critical rate.")) { existingDescriptions.remove("High critical rate."); }
			else break;
		}
		
		int charactersPerLine = 50;
		
		StringBuilder sb = new StringBuilder();
		int currentLineCharacters = 0;
		for (String string : existingDescriptions) {
			if (currentLineCharacters + string.length() > charactersPerLine) {
				String[] components = string.split(" ");
				for (String word : components) {
					if (currentLineCharacters + word.length() <= charactersPerLine) {
						sb.append(word);
						sb.append(" ");
						currentLineCharacters += word.length() + 1;
					} else {
						sb.append((char)0xA);
						sb.append(word);
						sb.append(" ");
						currentLineCharacters = word.length() + 1;
					}
				}
			} else {
				sb.append(string);
				sb.append(" ");
				currentLineCharacters += string.length() + 1;
			}
			
		}
		
		textData.setStringForIdentifier(getMHIForItem(item), sb.toString());
		
		return true;
	}
	
	private List<WeaponEffect> getWeaponEffects(FE9Item item) {
		if (item == null || !isWeapon(item)) { return null; }
		List<WeaponEffect> effects = new ArrayList<WeaponEffect>();
		if (hasTrait(item, FE9Data.Item.WeaponTraits.CANNOT_CRIT.getTraitString())) { effects.add(WeaponEffect.NO_CRIT); }
		if (hasTrait(item, FE9Data.Item.WeaponTraits.UNBREAKABLE.getTraitString())) { effects.add(WeaponEffect.UNBREAKABLE); }
		if (hasTrait(item, FE9Data.Item.WeaponTraits.POISON.getTraitString())) { effects.add(WeaponEffect.POISON); }
		if (getEffectiveness1ForItem(item) != null || getEffectiveness2ForItem(item) != null) { effects.add(WeaponEffect.EFFECTIVENESS); }
		if (hasTrait(item, FE9Data.Item.WeaponTraits.BRAVE.getTraitString())) { effects.add(WeaponEffect.BRAVE); }
		if (item.getItemCritical() >= 25) { effects.add(WeaponEffect.CRITICAL); }
		if (item.getSTRBonus() > 0 || item.getMAGBonus() > 0 || item.getSKLBonus() > 0 || item.getSPDBonus() > 0 || 
				item.getLCKBonus() > 0 || item.getDEFBonus() > 0 || item.getRESBonus() > 0) { effects.add(WeaponEffect.STAT_BOOST); }
		if (hasTrait(item, FE9Data.Item.WeaponTraits.STEAL_HP.getTraitString())) { effects.add(WeaponEffect.STEAL_HP); }
		if (!getEquipType(item).equals(getRealType(item)) && FE9Data.Item.ItemType.typeWithString(getEquipType(item)).isWeaponType()) {
			boolean equipIsMagic = FE9Data.Item.ItemType.typeWithString(getEquipType(item)).isMagicType();
			boolean realIsMagic = FE9Data.Item.ItemType.typeWithString(getRealType(item)).isMagicType(); 
			if (!equipIsMagic && realIsMagic) {
				effects.add(WeaponEffect.MAGIC_DAMAGE);
			} else if ((equipIsMagic && realIsMagic) || (!equipIsMagic && !realIsMagic)) {
				effects.add(WeaponEffect.REVERSE_TRIANGLE);
			}
		}
		if (item.getMinimumRange() != item.getMaximumRange()) {
			effects.add(WeaponEffect.EXTEND_RANGE);
		}
		if (hasTrait(item, FE9Data.Item.WeaponTraits.CANNOT_BE_CRIT.getTraitString())) { effects.add(WeaponEffect.CRIT_IMMUNE); }
		return effects;
	}
	
	public WeaponRank swordRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(0)); }
	public WeaponRank lanceRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(1)); }
	public WeaponRank axeRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(2)); }
	public WeaponRank bowRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(3)); }
	public WeaponRank fireRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(4)); }
	public WeaponRank thunderRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(5)); }
	public WeaponRank windRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(6)); }
	public WeaponRank staffRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(7)); }
	public WeaponRank knifeRankForWeaponLevelString(String weaponLevels) { return WeaponRank.rankForCharacter(weaponLevels.charAt(8)); }
	
	public Map<WeaponType, WeaponRank> weaponLevelsForWeaponString(String weaponLevels) {
		Map<WeaponType, WeaponRank> levelMap = new HashMap<WeaponType, WeaponRank>();
		if (swordRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.SWORD, swordRankForWeaponLevelString(weaponLevels)); }
		if (lanceRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.LANCE, lanceRankForWeaponLevelString(weaponLevels)); }
		if (axeRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.AXE, axeRankForWeaponLevelString(weaponLevels)); }
		if (bowRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.BOW, bowRankForWeaponLevelString(weaponLevels)); }
		if (fireRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.FIRE, fireRankForWeaponLevelString(weaponLevels)); }
		if (thunderRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.THUNDER, thunderRankForWeaponLevelString(weaponLevels)); }
		if (windRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { levelMap.put(WeaponType.WIND, windRankForWeaponLevelString(weaponLevels)); }
		if (staffRankForWeaponLevelString(weaponLevels) != WeaponRank.NONE) { 
			levelMap.put(WeaponType.STAFF, staffRankForWeaponLevelString(weaponLevels));
			levelMap.put(WeaponType.LIGHT, staffRankForWeaponLevelString(weaponLevels));
		}
		return levelMap;
	}
	
	public WeaponRank highestRankInString(String weaponLevels) {
		if (weaponLevels.contains("S")) { return WeaponRank.S; }
		if (weaponLevels.contains("A")) { return WeaponRank.A; }
		if (weaponLevels.contains("B")) { return WeaponRank.B; }
		if (weaponLevels.contains("C")) { return WeaponRank.C; }
		if (weaponLevels.contains("D")) { return WeaponRank.D; }
		if (weaponLevels.contains("E")) { return WeaponRank.E; }
		return WeaponRank.NONE;
	}
	
	public WeaponType weaponTypeForItem(FE9Item item) {
		if (FE9Data.Item.withIID(iidOfItem(item)).isSword()) { return WeaponType.SWORD; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isLance()) { return WeaponType.LANCE; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isAxe()) { return WeaponType.AXE; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isBow()) { return WeaponType.BOW; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isFireMagic()) { return WeaponType.FIRE; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isThunderMagic()) { return WeaponType.THUNDER; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isWindMagic()) { return WeaponType.WIND; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isLightMagic()) { return WeaponType.LIGHT; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isStaff()) { return WeaponType.STAFF; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isKnife()) { return WeaponType.KNIFE; }
		return WeaponType.NONE;
	}
	
	public WeaponRank weaponRankForItem(FE9Item item) {
		if (FE9Data.Item.withIID(iidOfItem(item)).isERank()) { return WeaponRank.E; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isDRank()) { return WeaponRank.D; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isCRank()) { return WeaponRank.C; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isBRank()) { return WeaponRank.B; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isARank()) { return WeaponRank.A; }
		if (FE9Data.Item.withIID(iidOfItem(item)).isSRank()) { return WeaponRank.S; }
		if (isWeapon(item)) { return WeaponRank.UNKNOWN; }
		return WeaponRank.NONE;
	}
	
	public List<FE9Item> allWeaponsOfRank(WeaponRank rank) {
		Set<FE9Data.Item> items = new HashSet<FE9Data.Item>();
		switch (rank) {
		case E: items.addAll(FE9Data.Item.allERankWeapons); break;
		case D: items.addAll(FE9Data.Item.allDRankWeapons); break;
		case C: items.addAll(FE9Data.Item.allCRankWeapons); break;
		case B: items.addAll(FE9Data.Item.allBRankWeapons); break;
		case A: items.addAll(FE9Data.Item.allARankWeapons); break;
		case S: items.addAll(FE9Data.Item.allSRankWeapons); break;
		default: break;
		}
		
		return fe9ItemListFromSet(items);
	}
	
	public List<FE9Item> weaponsOfRankAndType(WeaponRank rank, WeaponType type) {
		Set<FE9Data.Item> items = new HashSet<FE9Data.Item>();
		switch (rank) {
		case E: items.addAll(FE9Data.Item.allERankWeapons); break;
		case D: items.addAll(FE9Data.Item.allDRankWeapons); break;
		case C: items.addAll(FE9Data.Item.allCRankWeapons); break;
		case B: items.addAll(FE9Data.Item.allBRankWeapons); break;
		case A: items.addAll(FE9Data.Item.allARankWeapons); break;
		case S: items.addAll(FE9Data.Item.allSRankWeapons); break;
		default: break;
		}
		switch (type) {
		case SWORD: items.retainAll(FE9Data.Item.allSwords); break;
		case LANCE: items.retainAll(FE9Data.Item.allLances); break;
		case AXE: items.retainAll(FE9Data.Item.allAxes); break;
		case BOW: items.retainAll(FE9Data.Item.allBows); break;
		case FIRE: items.retainAll(FE9Data.Item.allFireMagic); break;
		case WIND: items.retainAll(FE9Data.Item.allWindMagic); break;
		case THUNDER: items.retainAll(FE9Data.Item.allThunderMagic); break;
		case LIGHT: items.retainAll(FE9Data.Item.allLightMagic); break;
		case STAFF: items.retainAll(FE9Data.Item.allStaves); break;
		case KNIFE: items.retainAll(FE9Data.Item.allKnives); break;
		default: break;
		}
		items.removeAll(FE9Data.Item.blacklistedWeapons);
		items.removeAll(FE9Data.Item.allRestrictedItems);
		return fe9ItemListFromSet(items);
	}
	
	public FE9Item basicItemForType(WeaponType type) {
		switch (type) {
		case SWORD: return itemWithIID(FE9Data.Item.IRON_SWORD.getIID());
		case LANCE: return itemWithIID(FE9Data.Item.IRON_LANCE.getIID());
		case AXE: return itemWithIID(FE9Data.Item.IRON_AXE.getIID());
		case BOW: return itemWithIID(FE9Data.Item.IRON_BOW.getIID());
		case FIRE: return itemWithIID(FE9Data.Item.FIRE.getIID());
		case THUNDER: return itemWithIID(FE9Data.Item.THUNDER.getIID());
		case WIND: return itemWithIID(FE9Data.Item.WIND.getIID());
		case STAFF: return itemWithIID(FE9Data.Item.HEAL.getIID());
		case LIGHT: return itemWithIID(FE9Data.Item.LIGHT.getIID());
		case KNIFE: return itemWithIID(FE9Data.Item.KNIFE.getIID());
		default: return null;
		}
	}
	
	public boolean isSiegeTome(FE9Item item) {
		FE9Data.Item fe9Item = FE9Data.Item.withIID(iidOfItem(item));
		if (fe9Item != null) { return fe9Item.isSiegeTome(); }
		return false;
	}
	
	public boolean isRanged(FE9Item item) {
		FE9Data.Item fe9Item = FE9Data.Item.withIID(iidOfItem(item));
		if (fe9Item != null) { return !fe9Item.isMeleeLocked(); }	
		return false;
	}
	
	public boolean isMelee(FE9Item item) {
		FE9Data.Item fe9Item = FE9Data.Item.withIID(iidOfItem(item));
		if (fe9Item != null) { return !fe9Item.isRangeLocked(); }
		return false;
	}
	
	public List<FE9Item> specialWeaponsForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		return fe9ItemListFromSet(FE9Data.Item.specialWeaponsForClass(charClass));
	}
	
	public FE9Item laguzWeaponForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		if (charClass.isLaguz()) {
			switch (charClass) {
			case LION: case FERAL_LION: return itemWithIID(FE9Data.Item.LION_CLAW.getIID());
			case TIGER: case FERAL_TIGER: return itemWithIID(FE9Data.Item.TIGER_CLAW.getIID());
			case CAT: case FERAL_CAT: case CAT_F: case FERAL_CAT_F: return itemWithIID(FE9Data.Item.CAT_CLAW.getIID());
			case WHITE_DRAGON: case FERAL_WHITE_DRAGON: return itemWithIID(FE9Data.Item.WHITE_BREATH.getIID());
			case RED_DRAGON: case FERAL_RED_DRAGON: case RED_DRAGON_F: case FERAL_RED_DRAGON_F: return itemWithIID(FE9Data.Item.RED_BREATH.getIID());
			case HAWK: case FERAL_HAWK: return itemWithIID(FE9Data.Item.HAWK_BEAK.getIID());
			case CROW: case FERAL_CROW: return itemWithIID(FE9Data.Item.CROW_BEAK.getIID());
			case TIBARN_HAWK: return itemWithIID(FE9Data.Item.HAWK_KING_BEAK.getIID());
			case NAESALA_CROW: return itemWithIID(FE9Data.Item.CROW_KING_BEAK.getIID());
			default: return null;
			}
		}
		
		return null;
	}
	
	public List<FE9Item> formerThiefKit() {
		return new ArrayList<FE9Item>(Arrays.asList(
				itemWithIID(FE9Data.Item.DOOR_KEY.getIID()),
				itemWithIID(FE9Data.Item.CHEST_KEY.getIID())
				));
	}
	
	public List<FE9Item> equipmentListForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		List<FE9Item> equipment = new ArrayList<FE9Item>();
		if (charClass.isLaguz()) {
			equipment.add(itemWithIID(FE9Data.Item.LAGUZ_STONE.getIID()));
		}
		return equipment;
	}
	
	public List<FE9Item> potentialEquipmentListForJID(String jid) {
		if (jid == null) { return null; }
		List<FE9Item> equipment = new ArrayList<FE9Item>();
		equipment.add(itemWithIID(FE9Data.Item.VULNERARY.getIID()));
		equipment.add(itemWithIID(FE9Data.Item.ELIXIR.getIID()));
		
		return equipment; 
	}
	
	public List<FE9Item> veryRareEquipment() {
		List<FE9Item> equipment = new ArrayList<FE9Item>();
		for (int i = 0; i < 10; i++) { equipment.add(itemWithIID(FE9Data.Item.RED_GEM.getIID())); }
		for (int i = 0; i < 5; i++) { equipment.add(itemWithIID(FE9Data.Item.BLUE_GEM.getIID())); }
		equipment.add(itemWithIID(FE9Data.Item.WHITE_GEM.getIID()));
		
		equipment.addAll(FE9Data.Item.allStatBoosters.stream().map(fe9item -> {
			return itemWithIID(fe9item.getIID());
		}).collect(Collectors.toList()));
		equipment.addAll(FE9Data.Item.allSkillScrolls.stream().map(fe9item -> {
			return itemWithIID(fe9item.getIID());
		}).collect(Collectors.toList()));
		
		equipment.sort(new Comparator<FE9Item>() {
			@Override
			public int compare(FE9Item o1, FE9Item o2) {
				return iidOfItem(o1).compareTo(iidOfItem(o2));
			}
		});
		
		return equipment;
	}
	
	public List<FE9Item> rareEquipmentForJID(String jid) {
		if (jid == null) { return null; }
		FE9Data.CharacterClass charClass = FE9Data.CharacterClass.withJID(jid);
		List<FE9Item> equipment = new ArrayList<FE9Item>();
		
		if (charClass.isLaguz()) { 
			equipment.add(itemWithIID(FE9Data.Item.BEORCGUARD.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.DEMI_BAND.getIID()));
		}
		if (charClass.isBeorc()) { equipment.add(itemWithIID(FE9Data.Item.LAGUZGUARD.getIID())); }
		if (charClass.isFlier()) { equipment.add(itemWithIID(FE9Data.Item.FULL_GUARD.getIID())); }
		switch (charClass) {
		case KNIGHT: case GENERAL:
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_WARD.getIID()));
			break;
		case MYRMIDON: case SWORDMASTER: case MYRMIDON_F: case SWORDMASTER_F:
			equipment.add(itemWithIID(FE9Data.Item.SWORD_BAND.getIID()));
			break;
		case SOLDIER: case HALBERDIER: case SOLDIER_F: case HALBERDIER_F:
			equipment.add(itemWithIID(FE9Data.Item.SOLDIER_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_WARD.getIID()));
			break;
		case FIGHTER: case WARRIOR:
			equipment.add(itemWithIID(FE9Data.Item.FIGHTERS_BAND.getIID()));
			break;
		case ARCHER: case SNIPER:
			equipment.add(itemWithIID(FE9Data.Item.ARCHER_BAND.getIID()));
			break;
		case SWORD_KNIGHT: case SWORD_KNIGHT_F: case LANCE_KNIGHT: case LANCE_KNIGHT_F: case AXE_KNIGHT: case AXE_KNIGHT_F: case BOW_KNIGHT: case BOW_KNIGHT_F:
		case AXE_PALADIN: case AXE_PALADIN_F: case BOW_PALADIN: case BOW_PALADIN_F: case LANCE_PALADIN: case LANCE_PALADIN_F: case SWORD_PALADIN: case SWORD_PALADIN_F:
		case TITANIA_PALADIN:
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_WARD.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.PALADIN_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.KNIGHT_RING.getIID()));
			break;
		case PEGASUS_KNIGHT: case FALCON_KNIGHT: case ELINCIA_FALCON_KNIGHT:
			equipment.add(itemWithIID(FE9Data.Item.PEGASUS_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.FULL_GUARD.getIID()));
			break;
		case WYVERN_RIDER: case WYVERN_LORD: case WYVERN_LORD_F: case WYVERN_RIDER_F:
			equipment.add(itemWithIID(FE9Data.Item.WYVERN_BAND.getIID()));
			equipment.add(itemWithIID(FE9Data.Item.FULL_GUARD.getIID()));
			break;
		case MAGE: case MAGE_F: case SAGE_KNIFE: case SAGE_KNIFE_F: case SAGE_STAFF: case SAGE_STAFF_F:
			equipment.add(itemWithIID(FE9Data.Item.MAGE_BAND.getIID()));
			break;
		case PRIEST: case BISHOP: case CLERIC:
			equipment.add(itemWithIID(FE9Data.Item.PRIEST_BAND.getIID()));
			break;
		case THIEF:
			equipment.add(itemWithIID(FE9Data.Item.THIEF_BAND.getIID()));
			break;
		default:
			break;
		}
		
		return equipment;
	}
	
	// These shouldn't be removed when possible.
	public List<FE9Item> getImportantEquipment() {
		return fe9ItemListFromSet(FE9Data.Item.allBands);
	}
	
	public List<FE9Item> possibleUpgradesToWeapon(FE9Item item, boolean isWielderPromoted) {
		if (item == null) { return null; }
		FE9Data.Item original = FE9Data.Item.withIID(iidOfItem(item));
		if (original == null || !original.isWeapon() || original.isStaff()) { return null; }
		Set<FE9Data.Item> upgrades = new HashSet<FE9Data.Item>();
		if (original.isERank()) { upgrades.addAll(FE9Data.Item.allDRankWeapons); }
		else if (original.isDRank()) { upgrades.addAll(FE9Data.Item.allCRankWeapons); }
		else if (original.isCRank()) { upgrades.addAll(FE9Data.Item.allCRankWeapons); upgrades.addAll(FE9Data.Item.allBRankWeapons); }
		else if (original.isBRank()) { // Unpromoted classes apparently can't go above B rank.
			upgrades.addAll(FE9Data.Item.allBRankWeapons); 
			if (isWielderPromoted) { 
				upgrades.addAll(FE9Data.Item.allARankWeapons); 
			}
		}
		else if (original.isARank()) { upgrades.addAll(FE9Data.Item.allSRankWeapons); }
		
		if (original.isSword()) { upgrades.retainAll(FE9Data.Item.allSwords); }
		else if (original.isLance()) { upgrades.retainAll(FE9Data.Item.allLances); }
		else if (original.isAxe()) { upgrades.retainAll(FE9Data.Item.allAxes); }
		else if (original.isBow()) { upgrades.retainAll(FE9Data.Item.allBows); }
		else if (original.isFireMagic()) { upgrades.retainAll(FE9Data.Item.allFireMagic); }
		else if (original.isThunderMagic()) { upgrades.retainAll(FE9Data.Item.allThunderMagic); }
		else if (original.isWindMagic()) { upgrades.retainAll(FE9Data.Item.allWindMagic); }
		else if (original.isLightMagic()) { upgrades.retainAll(FE9Data.Item.allLightMagic); }
		else { return null; }
		
		return upgrades.stream().sorted(new Comparator<FE9Data.Item>() {
			@Override
			public int compare(Item arg0, Item arg1) {
				return arg0.getIID().compareTo(arg1.getIID());
			}
		}).map(fe9DataItem -> {
			return itemWithIID(fe9DataItem.getIID());
		}).collect(Collectors.toList());
	}
	
	public List<FE9Item> getSimilarItemsTo(FE9Item originalItem) {
		if (originalItem == null) { return null; }
		Set<FE9Data.Item> fe9DataItems = new HashSet<FE9Data.Item>(); 
		if (isWeapon(originalItem)) {
			FE9Data.Item original = FE9Data.Item.withIID(iidOfItem(originalItem));
			if (original.isSword()) { fe9DataItems.addAll(FE9Data.Item.allSwords); }
			if (original.isLance()) { fe9DataItems.addAll(FE9Data.Item.allLances); }
			if (original.isAxe()) { fe9DataItems.addAll(FE9Data.Item.allAxes); }
			if (original.isBow()) { fe9DataItems.addAll(FE9Data.Item.allBows); }
			if (original.isMagic()) { 
				fe9DataItems.addAll(FE9Data.Item.allFireMagic);
				fe9DataItems.addAll(FE9Data.Item.allWindMagic);
				fe9DataItems.addAll(FE9Data.Item.allThunderMagic);
				fe9DataItems.addAll(FE9Data.Item.allLightMagic);
			}
			if (original.isStaff()) { fe9DataItems.addAll(FE9Data.Item.allStaves); }
			
			if (original.isERank()) { fe9DataItems.addAll(FE9Data.Item.allERankWeapons); }
			if (original.isDRank()) { fe9DataItems.addAll(FE9Data.Item.allDRankWeapons); }
			if (original.isCRank()) { fe9DataItems.addAll(FE9Data.Item.allCRankWeapons); }
			if (original.isBRank()) { fe9DataItems.addAll(FE9Data.Item.allBRankWeapons); }
			if (original.isARank()) { fe9DataItems.addAll(FE9Data.Item.allARankWeapons); }
			if (original.isSRank()) { fe9DataItems.addAll(FE9Data.Item.allSRankWeapons); }
		} else if (isConsumable(originalItem) || isPromotionItem(originalItem)) {
			fe9DataItems.addAll(FE9Data.Item.allConsumables);
			// Equally weight these.
			for (int i = 0; i < FE9Data.Item.allConsumables.size(); i += FE9Data.Item.allPromotionItems.size()) {
				fe9DataItems.addAll(FE9Data.Item.allPromotionItems);
			}
		} else if (isStatBooster(originalItem)) {
			fe9DataItems.addAll(FE9Data.Item.allStatBoosters);
		} else if (isTreasure(originalItem)) {
			fe9DataItems.addAll(FE9Data.Item.allGems);
		} else if (isSkillScroll(originalItem)) {
			fe9DataItems.addAll(FE9Data.Item.allSkillScrolls);
		}
		
		fe9DataItems.removeAll(FE9Data.Item.allRestrictedItems);
		fe9DataItems.removeAll(FE9Data.Item.blacklistedWeapons);
		
		return fe9ItemListFromSet(fe9DataItems);
	}
	
	public List<FE9Item> getPossibleRewards() {
		Set<FE9Data.Item> fe9DataItems = new HashSet<FE9Data.Item>();
		fe9DataItems.addAll(FE9Data.Item.allDroppableWeapons());
		fe9DataItems.addAll(FE9Data.Item.allGems);
		fe9DataItems.addAll(FE9Data.Item.allStatBoosters);
		fe9DataItems.addAll(FE9Data.Item.allSkillScrolls);
		fe9DataItems.addAll(FE9Data.Item.allConsumables);
		for (int i = 0; i < FE9Data.Item.allConsumables.size(); i += FE9Data.Item.allPromotionItems.size()) {
			fe9DataItems.addAll(FE9Data.Item.allPromotionItems);
		}
		
		return fe9ItemListFromSet(fe9DataItems);
	}
	
	private List<FE9Item> fe9ItemListFromSet(Set<FE9Data.Item> fe9dataItemSet) {
		if (fe9dataItemSet == null) { return new ArrayList<FE9Item>(); }
		return fe9dataItemSet.stream().sorted(new Comparator<FE9Data.Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				return o1.getIID().compareTo(o2.getIID());
			}
		}).map(fe9DataItem -> {
			return idLookup.get(fe9DataItem.getIID());
		}).collect(Collectors.toList());
	}
	
	public void compileDiffs(GCNISOHandler isoHandler) {
		for (FE9Item item : allItems) {
			item.commitChanges();
			if (item.hasCommittedChanges()) {
				fe8databin.writeDataToSection(itemDataSection, item.getAddressOffset(), item.getData());
			}
		}
	}
	
	public String getDisplayName(FE9Item item) {
		long pointer = item.getItemNamePointer();
		if (pointer == 0) { return "(null)"; }
		String identifier = fe8databin.stringForPointer(pointer);
		if (textLoader == null) { return identifier; }
		
		String resolvedValue = textLoader.textStringForIdentifier(identifier);
		if (resolvedValue != null) {
			return resolvedValue;
		} else {
			return identifier;
		}
	}
	
	private void debugPrintItem(FE9Item item, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "===== Printing Item =====");
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, 
				"IID: " + stringForPointer(item.getItemIDPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, 
				"MIID: " + stringForPointer(item.getItemNamePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, 
				"MH_I: " + stringForPointer(item.getItemDescriptionPointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Type: " + stringForPointer(item.getItemTypePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Subtype?: " + stringForPointer(item.getItemSubtypePointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Rank: " + stringForPointer(item.getItemRankPointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Restrictions: " + stringForPointer(item.getItemTrait1Pointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 1: " + stringForPointer(item.getItemTrait2Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 2: " + stringForPointer(item.getItemTrait3Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 3: " + stringForPointer(item.getItemTrait4Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 4: " + stringForPointer(item.getItemTrait5Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 5: " + stringForPointer(item.getItemTrait6Pointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Effectiveness: " + stringForPointer(item.getItemEffectiveness1Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 6: " + stringForPointer(item.getItemEffectiveness2Pointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Effect Animation: " + stringForPointer(item.getItemEffectAnimation1Pointer(), handler, commonTextLoader));
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Unknown 7: " + stringForPointer(item.getItemEffectAnimation2Pointer(), handler, commonTextLoader));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Cost per Use: " + item.getItemCost());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Durability: " + item.getItemDurability());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Might: " + item.getItemMight());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Accuracy: " + item.getItemAccuracy());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Weight: " + item.getItemWeight());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Critical: " + item.getItemCritical());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Range: " + item.getMinimumRange() + " ~ " + item.getMaximumRange());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Item Icon: " + item.getIconNumber());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "Weapon EXP: " + item.getWeaponExperience());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "HP Bonus: " + item.getHPBonus());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "STR Bonus: " + item.getSTRBonus());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "MAG Bonus: " + item.getMAGBonus());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "SKL Bonus: " + item.getSKLBonus());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "SPD Bonus: " + item.getSPDBonus());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "LCK Bonus: " + item.getLCKBonus());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "DEF Bonus: " + item.getDEFBonus());
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "RES Bonus: " + item.getRESBonus());
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER,
				"Remaining Bytes: " + WhyDoesJavaNotHaveThese.displayStringForBytes(item.getRemainingBytes()));
		
		DebugPrinter.log(DebugPrinter.Key.FE9_ITEM_LOADER, "===== End Printing Item =====");
	}

	private String stringForPointer(long pointer, GCNFileHandler handler, FE9CommonTextLoader commonTextLoader) {
		if (pointer == 0) { return "(null)"; }
		handler.setNextReadOffset(pointer);
		byte[] bytes = handler.continueReadingBytesUpToNextTerminator(pointer + 0xFF);
		String identifier = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(bytes);
		String resolvedValue = commonTextLoader.textStringForIdentifier(identifier);
		if (resolvedValue != null) {
			return identifier + " (" + resolvedValue + ")";
		} else {
			return identifier;
		}
	}
	
	public void recordOriginalItemData(ChangelogBuilder builder, ChangelogSection itemDataSection,
			FE9CommonTextLoader textData) {
		ChangelogTOC itemTOC = new ChangelogTOC("item-data");
		itemTOC.addClass("item-section-toc");
		itemDataSection.addElement(new ChangelogHeader(HeaderLevel.HEADING_2, "Item Data", "item-data-header"));
		itemDataSection.addElement(itemTOC);
		
		ChangelogSection itemContainer = new ChangelogSection("item-data-container");
		itemDataSection.addElement(itemContainer);
		
		for (FE9Item item : allItems) {
			createItemSection(item, textData, itemTOC, itemContainer, true);
		}
		
		setupRules(builder);
	}
	
	public void recordUpdatedItemData(ChangelogSection itemDataSection, FE9CommonTextLoader textData) {
		ChangelogTOC itemTOC = (ChangelogTOC)itemDataSection.getChildWithIdentifier("item-data");
		ChangelogSection itemContainer = (ChangelogSection)itemDataSection.getChildWithIdentifier("item-data-container");
		
		for (FE9Item item : allItems) {
			createItemSection(item, textData, itemTOC, itemContainer, false);
		}
	}
	
	private void createItemSection(FE9Item item, FE9CommonTextLoader textData, ChangelogTOC toc, ChangelogSection parentSection, boolean isOriginal) {
		String itemName = textData.textStringForIdentifier(getMIIDOfItem(item));
		String anchor = "item-data-" + iidOfItem(item);
		ChangelogTable itemDataTable;
		ChangelogSection section;
		if (isOriginal) {
			section = new ChangelogSection(anchor + "-section");
			section.addClass("item-data-section");
			toc.addAnchorWithTitle(anchor, itemName);
			
			ChangelogHeader titleHeader = new ChangelogHeader(HeaderLevel.HEADING_3, itemName, anchor);
			titleHeader.addClass("item-data-title");
			section.addElement(titleHeader);
			
			itemDataTable = new ChangelogTable(3, new String[] {"", "Old Value", "New Value"}, anchor + "-data-table");
			itemDataTable.addClass("item-data-table");
			itemDataTable.addRow(new String[] {"IID", iidOfItem(item), ""});
			itemDataTable.addRow(new String[] {"Name", itemName, ""});
			itemDataTable.addRow(new String[] {"Description", textData.textStringForIdentifier(fe8databin.stringForPointer(item.getItemDescriptionPointer())), ""});
			itemDataTable.addRow(new String[] {"Type", fe8databin.stringForPointer(item.getItemTypePointer()), ""});
			itemDataTable.addRow(new String[] {"Sub-type", fe8databin.stringForPointer(item.getItemSubtypePointer()), ""});
		} else {
			section = (ChangelogSection)parentSection.getChildWithIdentifier(anchor + "-section");
			itemDataTable = (ChangelogTable)section.getChildWithIdentifier(anchor + "-data-table");
			itemDataTable.setContents(0, 2, iidOfItem(item));
			itemDataTable.setContents(1, 2, itemName);
			itemDataTable.setContents(2, 2, textData.textStringForIdentifier(fe8databin.stringForPointer(item.getItemDescriptionPointer())));
			itemDataTable.setContents(3, 2, fe8databin.stringForPointer(item.getItemTypePointer()));
			itemDataTable.setContents(4, 2, fe8databin.stringForPointer(item.getItemSubtypePointer()));
		}
		
		if (isWeapon(item)) {
			if (isOriginal) {
				itemDataTable.addRow(new String[] {"Rank", weaponRankForItem(item).toString(), ""});
				itemDataTable.addRow(new String[] {"Trait 1", fe8databin.stringForPointer(item.getItemTrait1Pointer()), ""});
				itemDataTable.addRow(new String[] {"Trait 2", fe8databin.stringForPointer(item.getItemTrait2Pointer()), ""});
				itemDataTable.addRow(new String[] {"Trait 3", fe8databin.stringForPointer(item.getItemTrait3Pointer()), ""});
				itemDataTable.addRow(new String[] {"Trait 4", fe8databin.stringForPointer(item.getItemTrait4Pointer()), ""});
				itemDataTable.addRow(new String[] {"Trait 5", fe8databin.stringForPointer(item.getItemTrait5Pointer()), ""});
				itemDataTable.addRow(new String[] {"Trait 6", fe8databin.stringForPointer(item.getItemTrait6Pointer()), ""});
				itemDataTable.addRow(new String[] {"Effectiveness 1", fe8databin.stringForPointer(item.getItemEffectiveness1Pointer()), ""});
				itemDataTable.addRow(new String[] {"Effectiveness 2", fe8databin.stringForPointer(item.getItemEffectiveness2Pointer()), ""});
				itemDataTable.addRow(new String[] {"Durability", Integer.toString(item.getItemDurability()), ""});
				itemDataTable.addRow(new String[] {"Might", Integer.toString(item.getItemMight()), ""});
				itemDataTable.addRow(new String[] {"Accuracy", Integer.toString(item.getItemAccuracy()), ""});
				itemDataTable.addRow(new String[] {"Weight", Integer.toString(item.getItemWeight()), ""});
				itemDataTable.addRow(new String[] {"Critical", Integer.toString(item.getItemCritical()), ""});
				itemDataTable.addRow(new String[] {"Range", item.getMinimumRange() + " ~ " + item.getMaximumRange(), ""});
			} else {
				itemDataTable.setContents(5, 2, weaponRankForItem(item).toString());
				itemDataTable.setContents(6, 2, fe8databin.stringForPointer(item.getItemTrait1Pointer()));
				itemDataTable.setContents(7, 2, fe8databin.stringForPointer(item.getItemTrait2Pointer()));
				itemDataTable.setContents(8, 2, fe8databin.stringForPointer(item.getItemTrait3Pointer()));
				itemDataTable.setContents(9, 2, fe8databin.stringForPointer(item.getItemTrait4Pointer()));
				itemDataTable.setContents(10, 2, fe8databin.stringForPointer(item.getItemTrait5Pointer()));
				itemDataTable.setContents(11, 2, fe8databin.stringForPointer(item.getItemTrait6Pointer()));
				itemDataTable.setContents(12, 2, fe8databin.stringForPointer(item.getItemEffectiveness1Pointer()));
				itemDataTable.setContents(13, 2, fe8databin.stringForPointer(item.getItemEffectiveness2Pointer()));
				itemDataTable.setContents(14, 2, Integer.toString(item.getItemDurability()));
				itemDataTable.setContents(15, 2, Integer.toString(item.getItemMight()));
				itemDataTable.setContents(16, 2, Integer.toString(item.getItemAccuracy()));
				itemDataTable.setContents(17, 2, Integer.toString(item.getItemWeight()));
				itemDataTable.setContents(18, 2, Integer.toString(item.getItemCritical()));
				itemDataTable.setContents(19, 2, item.getMinimumRange() + " ~ " + item.getMaximumRange());
			}
		}
		
		if (isOriginal) {
			section.addElement(itemDataTable);
			parentSection.addElement(section);
		}
	}
	
	private void setupRules(ChangelogBuilder builder) {
		ChangelogStyleRule tocStyle = new ChangelogStyleRule();
		tocStyle.setElementClass("item-section-toc");
		tocStyle.addRule("display", "flex");
		tocStyle.addRule("flex-direction", "row");
		tocStyle.addRule("width", "75%");
		tocStyle.addRule("align-items", "center");
		tocStyle.addRule("justify-content", "center");
		tocStyle.addRule("flex-wrap", "wrap");
		tocStyle.addRule("margin-left", "auto");
		tocStyle.addRule("margin-right", "auto");
		builder.addStyle(tocStyle);
		
		ChangelogStyleRule tocItemAfter = new ChangelogStyleRule();
		tocItemAfter.setOverrideSelectorString(".item-section-toc div:not(:last-child)::after");
		tocItemAfter.addRule("content", "\"|\"");
		tocItemAfter.addRule("margin", "0px 5px");
		builder.addStyle(tocItemAfter);
		
		ChangelogStyleRule itemContainer = new ChangelogStyleRule();
		itemContainer.setElementIdentifier("item-data-container");
		itemContainer.addRule("display", "flex");
		itemContainer.addRule("flex-direction", "row");
		itemContainer.addRule("flex-wrap", "wrap");
		itemContainer.addRule("justify-content", "center");
		itemContainer.addRule("margin-left", "10px");
		itemContainer.addRule("margin-right", "10px");
		builder.addStyle(itemContainer);
		
		ChangelogStyleRule itemSection = new ChangelogStyleRule();
		itemSection.setElementClass("item-data-section");
		itemSection.addRule("margin", "20px");
		itemSection.addRule("flex", "0 0 400px");
		builder.addStyle(itemSection);
		
		ChangelogStyleRule tableStyle = new ChangelogStyleRule();
		tableStyle.setElementClass("item-data-table");
		tableStyle.addRule("width", "100%");
		tableStyle.addRule("border", "1px solid black");
		builder.addStyle(tableStyle);
		
		ChangelogStyleRule titleStyle = new ChangelogStyleRule();
		titleStyle.setElementClass("item-data-title");
		titleStyle.addRule("text-align", "center");
		builder.addStyle(titleStyle);
		
		ChangelogStyleRule columnStyle = new ChangelogStyleRule();
		columnStyle.setElementClass("item-data-table");
		columnStyle.setChildTags(new ArrayList<String>(Arrays.asList("td", "th")));
		columnStyle.addRule("border", "1px solid black");
		columnStyle.addRule("padding", "5px");
		builder.addStyle(columnStyle);
		
		ChangelogStyleRule firstColumnStyle = new ChangelogStyleRule();
		firstColumnStyle.setOverrideSelectorString(".item-data-table td:first-child");
		firstColumnStyle.addRule("width", "20%");
		firstColumnStyle.addRule("text-align", "right");
		builder.addStyle(firstColumnStyle);
		
		ChangelogStyleRule otherColumnStyle = new ChangelogStyleRule();
		otherColumnStyle.setOverrideSelectorString(".item-data-table th:not(:first-child)");
		otherColumnStyle.addRule("width", "40%");
		builder.addStyle(otherColumnStyle);
	}
}
