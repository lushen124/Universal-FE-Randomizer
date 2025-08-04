package fedata.gba.fe7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fedata.gba.GBAFEItemData;
import fedata.gba.GBAFESpellAnimationCollection;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data.Item.Ability1Mask;
import fedata.gba.fe7.FE7Data.Item.Ability2Mask;
import fedata.gba.fe7.FE7Data.Item.FE7WeaponRank;
import fedata.gba.fe7.FE7Data.Item.FE7WeaponType;
import fedata.gba.general.WeaponEffects;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponType;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import random.general.WeightedDistributor;
import random.gba.loader.ItemDataLoader.AdditionalData;
import ui.model.MinMaxOption;
import util.ByteArrayBuilder;
import util.DebugPrinter;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;

public class FE7Item implements GBAFEItemData {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	private String debugString = "Uninitialized";

	public FE7Item(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	public FE7Item() {
		super();
		this.originalData = new byte[FE7Data.BytesPerItem];
		this.data = new byte[FE7Data.BytesPerItem];
		this.originalOffset = -1;
	}
	
	public static int getIDFromItemData(byte[] itemData) {
		return itemData[6] & 0xFF;
	}
	
	public void initializeDisplayString(String debugString) {
		this.debugString = debugString;
	}
	
	public String displayString() {
		return debugString;
	}

	public int getNameIndex() {
		return (data[0] & 0xFF) | ((data[1] << 8) & 0xFF00);
	}

	public int getDescriptionIndex() {
		return (data[2] & 0xFF) | ((data[3] << 8) & 0xFF00);
	}

	public int getUseDescriptionIndex() {
		return (data[4] & 0xFF) | ((data[5] << 8) & 0xFF00);
	}

	public int getID() {
		return data[6] & 0xFF;
	}

	public WeaponType getType() {
		FE7WeaponType type = FE7WeaponType.valueOf(data[7] & 0xFF);
		return type.toGeneralType();
	}

	public boolean hasAbility1() {
		return true;
	}
	
	public int getAbility1() {
		return data[8] & 0xFF;
	}
	
	public String getAbility1Description(String delimiter) {
		return FE7Data.Item.Ability1Mask.stringOfActiveAbilities(getAbility1(), delimiter);
	}

	public boolean hasAbility2() {
		return true;
	}
	
	public int getAbility2() {
		return data[9] & 0xFF;
	}
	
	public String getAbility2Description(String delimiter) {
		return FE7Data.Item.Ability2Mask.stringOfActiveAbilities(getAbility2(), delimiter);
	}
	
	public void setAbility2(int ability) {
		data[9] = (byte)(ability & 0xFF);
		wasModified = true;
	}

	public boolean hasAbility3() {
		return true;
	}
	
	public int getAbility3() {
		return data[10] & 0xFF;
	}
	
	public void setAbility3(int ability) {
		data[10] = (byte)(ability & 0xFF);
		wasModified = true;
	}
	
	public String getAbility3Description(String delimiter) {
		return FE7Data.Item.Ability3Mask.stringOfActiveAbilities(getAbility3(), delimiter);
	}

	public boolean hasAbility4() {
		return false; // We have one, but we don't use it or modify it.
	}
	
	public int getAbility4() {
		return data[11] & 0xFF;
	}
	
	public String getAbility4Description(String delimiter) {
		return "[0x" + Integer.toHexString(getAbility4()).toUpperCase() + "]";
	}
	
	public boolean hasAbilityOrEffect(String abilityEffectString) {
		FE7Data.Item.Ability1Mask ability1 = FE7Data.Item.Ability1Mask.maskForDisplayString(abilityEffectString);
		if (ability1 != null) {
			return ((byte)getAbility1() & (byte)ability1.ID) != 0;
		}
		FE7Data.Item.Ability2Mask ability2 = FE7Data.Item.Ability2Mask.maskForDisplayString(abilityEffectString);
		if (ability2 != null) {
			return ((byte)getAbility2() & (byte)ability2.ID) != 0;
		}
		FE7Data.Item.Ability3Mask ability3 = FE7Data.Item.Ability3Mask.maskForDisplayString(abilityEffectString);
		if (ability3 != null) {
			return ((byte)getAbility3() & (byte)ability3.ID) != 0;
		}
		FE7Data.Item.WeaponEffect effect = FE7Data.Item.WeaponEffect.effectForDisplayString(abilityEffectString);
		if (effect != null) {
			return ((byte)getWeaponEffect() == (byte)effect.ID);
		}
		
		return false;
	}

	public long getStatBonusPointer() {
		return (data[12] & 0xFF) | ((data[13] << 8) & 0xFF00) | ((data[14] << 16) & 0xFF0000) | ((data[15] << 24) & 0xFF000000) ;
	}

	public long getEffectivenessPointer() {
		return (data[16] & 0xFF) | ((data[17] << 8) & 0xFF00) | ((data[18] << 16) & 0xFF0000) | ((data[19] << 24) & 0xFF000000) ;
	}
	
	public void setStatBonusPointer(long address) {
		if (address == 0) {
			data[12] = 0;
			data[13] = 0;
			data[14] = 0;
			data[15] = 0;
		} else {
			byte[] pointer = WhyDoesJavaNotHaveThese.bytesFromAddress(address);
			data[12] = pointer[0];
			data[13] = pointer[1];
			data[14] = pointer[2];
			data[15] = pointer[3];
		}
		wasModified = true;
	}
	
	public void setEffectivenessPointer(long address) {
		if (address == 0) {
			data[16] = 0;
			data[17] = 0;
			data[18] = 0;
			data[19] = 0;
		} else {
			byte[] pointer = WhyDoesJavaNotHaveThese.bytesFromAddress(address);
			data[16] = pointer[0];
			data[17] = pointer[1];
			data[18] = pointer[2];
			data[19] = pointer[3];
		}
		wasModified = true;
	}

	public int getDurability() {
		return data[20] & 0xFF;
	}

	public int getMight() {
		return data[21] & 0xFF;
	}

	public int getHit() {
		return data[22] & 0xFF;
	}

	public int getWeight() {
		return data[23] & 0xFF;
	}

	public int getCritical() {
		return data[24] & 0xFF;
	}

	public int getMinRange() {
		return (data[25] >> 4) & 0x0F;
	}

	public int getMaxRange() {
		return data[25] & 0x0F;
	}
	
	public int getCostPerUse() {
		return WhyDoesJavaNotHaveThese.intValueFromByteSubarray(data, 26, 2, true);
	}
	
	public void setCostPerUse(int costPerUse) {
		byte[] costData = WhyDoesJavaNotHaveThese.byteArrayFromLongValue(costPerUse, true, 2);
		data[26] = costData[0];
		data[27] = costData[1];
		wasModified = true;
 	}

	public WeaponRank getWeaponRank() {
		int rank = data[28] & 0xFF;
		FE7WeaponRank weaponRank = FE7Data.Item.FE7WeaponRank.valueOf(rank);
		if (weaponRank != null) {
			return weaponRank.toGeneralRank();
		} else {
			FE7Data.Item weapon = FE7Data.Item.valueOf(getID());
			if (weapon != null && FE7Data.Item.allPrfRank.contains(weapon)) {
				return WeaponRank.PRF;
			} else {
				return WeaponRank.NONE;
			}
		}
	}
	
	public void setWeaponRank(WeaponRank newRank) {
		int rankValue = FE7Data.Item.FE7WeaponRank.rankFromGeneralRank(newRank).value;
		data[28] = (byte)(rankValue & 0xFF);
		wasModified = true;
	}

	public boolean hasWeaponEffect() {
		return true;
	}
	
	public int getWeaponEffect() {
		return data[31];
	}
	
	public String getWeaponEffectDescription() {
		return FE7Data.Item.WeaponEffect.stringOfActiveEffect(getWeaponEffect());
	}
	
	public void setDurability(int durability) {
		durability = WhyDoesJavaNotHaveThese.clamp(durability, 0, 255);
		data[20] = (byte)(durability & 0xFF);
		wasModified = true;
	}
	
	public void setMight(int might) {
		might = WhyDoesJavaNotHaveThese.clamp(might, 0, 255);
		data[21] = (byte)(might & 0xFF);
		wasModified = true;
	}
	
	public void setHit(int hit) {
		hit = WhyDoesJavaNotHaveThese.clamp(hit, 0, 255);
		data[22] = (byte)(hit & 0xFF);
		wasModified = true;
	}
	
	public void setWeight(int weight) {
		weight = WhyDoesJavaNotHaveThese.clamp(weight, 0, 255);
		data[23] = (byte)(weight & 0xFF);
		wasModified = true;
	}
	private void setCritical(int critical) {
		critical = WhyDoesJavaNotHaveThese.clamp(critical, 0, 255);
		data[24] = (byte)(critical & 0xFF);
		wasModified = true;
	}
	
	private void setMinRange(int minRange) {
		int maxRange = getMaxRange();
		minRange = WhyDoesJavaNotHaveThese.clamp(minRange, 1, maxRange);
		
		data[25] = (byte)((byte)((minRange & 0x0F) << 4) | (byte)(maxRange & 0x0F));
		wasModified = true;
	}
	
	private void setMaxRange(int maxRange) {
		int minRange = getMinRange();
		maxRange = WhyDoesJavaNotHaveThese.clamp(maxRange, minRange, 3);
		
		data[25] = (byte)((byte)((minRange & 0x0F) << 4) | (byte)(maxRange & 0x0F));
		wasModified = true;
	}
	
	public void applyRandomEffect(WeightedDistributor<WeaponEffects> allowedEffects, ItemDataLoader itemData, TextLoader textData, GBAFESpellAnimationCollection spellAnimations, Random rng) {
		if (getType() == WeaponType.NOT_A_WEAPON) {
			return;
		}
		
		filterEffects(allowedEffects);
		
		if (allowedEffects.possibleResults().isEmpty()) {
			return;
		}

		WeaponEffects selectedEffect = allowedEffects.getRandomItem(rng);
		applyEffect(selectedEffect, itemData, spellAnimations, rng);
		String updatedDescription = ingameDescriptionString(itemData);
		if (updatedDescription != null) {
			textData.setStringAtIndex(getDescriptionIndex(), updatedDescription + "[X]");
			DebugPrinter.log(DebugPrinter.Key.WEAPONS, "Weapon " + textData.getStringAtIndex(getNameIndex(), true) + " is now " + updatedDescription);
		} else {
			DebugPrinter.log(DebugPrinter.Key.WEAPONS, "Weapon " + textData.getStringAtIndex(getNameIndex(), true) + " has no effect.");
		}
	}
	
	public int getWeaponExperience() {
		return data[32] & 0xFF;
	}
	
	public void resetData() {
		data = originalData;
		wasModified = false;
	}
	
	public void commitChanges() {
		if (wasModified) {
			hasChanges = true;
		}
		wasModified = false;
	}
	
	public Boolean hasCommittedChanges() {
		return hasChanges;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public Boolean wasModified() {
		return wasModified;
	}
	
	public long getAddressOffset() {
		return originalOffset;
	}
	
	private void filterEffects(WeightedDistributor<WeaponEffects> allowedEffects) {
		
		if (getStatBonusPointer() != 0) {
			allowedEffects.removeItem(WeaponEffects.STAT_BOOSTS);
		}
		if (getEffectivenessPointer() != 0) {
			allowedEffects.removeItem(WeaponEffects.EFFECTIVENESS);
		}
		if (getCritical() > 10) {
			allowedEffects.removeItem(WeaponEffects.HIGH_CRITICAL);
		}
		if (!(getMinRange() == 2 || getMaxRange() == 1) || getType() == WeaponType.AXE) { // Ranged axes are stupid to implement. They require modifying animation pointers for each class.
			allowedEffects.removeItem(WeaponEffects.EXTEND_RANGE);
		}

		if ((getAbility1() & FE7Data.Item.Ability1Mask.UNBREAKABLE.ID) != 0) {
			allowedEffects.removeItem(WeaponEffects.UNBREAKABLE);
		}
		if ((getAbility1() & FE7Data.Item.Ability1Mask.BRAVE.ID) != 0) {
			allowedEffects.removeItem(WeaponEffects.BRAVE);
		}
		if ((getAbility1() & FE7Data.Item.Ability1Mask.MAGIC_DAMAGE.ID) != 0 || (getAbility1() & FE7Data.Item.Ability1Mask.MAGIC.ID) != 0 || getType() == WeaponType.AXE) {
			allowedEffects.removeItem(WeaponEffects.MAGIC_DAMAGE);
		}
		
		if ((getAbility2() & FE7Data.Item.Ability2Mask.REVERSE_WEAPON_TRIANGLE.ID) != 0 || getType() == WeaponType.BOW) {
			allowedEffects.removeItem(WeaponEffects.REVERSE_TRIANGLE);
		}
		
		if (getWeaponEffect() != 0) {
			allowedEffects.removeItem(WeaponEffects.POISON);
			allowedEffects.removeItem(WeaponEffects.HALF_HP);
			allowedEffects.removeItem(WeaponEffects.DEVIL);
		}
	}
	
	private void applyEffect(WeaponEffects effect, ItemDataLoader itemData, GBAFESpellAnimationCollection spellAnimations, Random rng) {
		switch (effect) {
		case STAT_BOOSTS:
			long[] boosts = itemData.possibleStatBoostAddresses();
			int randomIndex = rng.nextInt(boosts.length);
			long selectedBoostAddress = boosts[randomIndex];
			setStatBonusPointer(selectedBoostAddress);
			break;
		case EFFECTIVENESS:
			long[] effects = itemData.possibleEffectivenessAddresses();
			randomIndex = rng.nextInt(effects.length);
			long selectedEffectivenessAddress = effects[randomIndex];
			setEffectivenessPointer(selectedEffectivenessAddress);
			break;
		case HIGH_CRITICAL:
			MinMaxOption range = (MinMaxOption)effect.additionalInfo.get(WeaponEffects.InfoKeys.CRITICAL_RANGE);
			int currentCritical = getCritical();
			int newCritical = currentCritical + 5 * ((range.minValue / 5) + rng.nextInt(((range.maxValue - range.minValue) / 5) + 1));
			setCritical(newCritical);
			break;
		case EXTEND_RANGE:
			int minRange = getMinRange();
			int maxRange = getMaxRange();
			if (minRange == 2) { // 2-range locked bows. 50/50 of being melee or longbow.
				int random = rng.nextInt(2);
				if (random == 0 || maxRange == 3) { minRange = 1; } // Longbows always gain melee range.
				else { maxRange = 3; }
			} else if (maxRange == 2) { // Hand Axes, Javelins, and Magic
				maxRange = 3;
			} else { // Melee weapons.
				maxRange = 2;
				if (getType() == WeaponType.LANCE) {
					spellAnimations.setAnimationValueForID(getID(), FE7SpellAnimationCollection.Animation.JAVELIN.value);
				} else if (getType() == WeaponType.AXE) {
					spellAnimations.setAnimationValueForID(getID(), FE7SpellAnimationCollection.Animation.THROWN_AXE.value);
				} else {
					spellAnimations.setAnimationValueForID(getID(), FE7SpellAnimationCollection.Animation.ARROW.value);
				}
			}
			setMinRange(minRange);
			setMaxRange(maxRange);
			break;
		case UNBREAKABLE:
			int ability1 = getAbility1();
			ability1 |= FE7Data.Item.Ability1Mask.UNBREAKABLE.ID;
			data[8] = (byte)(ability1 & 0xFF);
			int currentCostPerUse = getCostPerUse();
			setCostPerUse(currentCostPerUse * 64);
			wasModified = true;
			break;
		case BRAVE:
			ability1 = getAbility1();
			ability1 |= FE7Data.Item.Ability1Mask.BRAVE.ID;
			data[8] = (byte)(ability1 & 0xFF);
			wasModified = true;
			break;
		case MAGIC_DAMAGE:
			ability1 = getAbility1();
			ability1 |= FE7Data.Item.Ability1Mask.MAGIC_DAMAGE.ID;
			data[8] = (byte)(ability1 & 0xFF);
			
			if (getMaxRange() == 1) {
				setMaxRange(2);
			}
			wasModified = true;
			
			if (getType() == WeaponType.AXE) {
				// Unfortunately, ranged axes will soft lock the game if any other animation is used. 
				spellAnimations.setAnimationValueForID(getID(), FE7SpellAnimationCollection.Animation.THROWN_AXE.value);
			} else {
				// Everything else is fine though.
				spellAnimations.setAnimationValueForID(getID(), FE7SpellAnimationCollection.Animation.randomMagicAnimation(rng).value);
			}
			break;
		case REVERSE_TRIANGLE:
			int ability2 = getAbility2();
			ability2 |= FE7Data.Item.Ability2Mask.REVERSE_WEAPON_TRIANGLE.ID;
			data[9] = (byte)(ability2 & 0xFF);
			wasModified = true;
			break;
		case POISON:
			int effectValue = FE7Data.Item.WeaponEffect.POISON.ID;
			data[31] = (byte)(effectValue & 0xFF);
			wasModified = true;
			break;
		case HALF_HP:
			effectValue = FE7Data.Item.WeaponEffect.HALVES_HP.ID;
			data[31] = (byte)(effectValue & 0xFF);
			wasModified = true;
			break;
		case DEVIL:
			effectValue = FE7Data.Item.WeaponEffect.DEVIL.ID;
			data[31] = (byte)(effectValue & 0xFF);
			int currentMight = getMight();
			setMight(Math.max((int)(currentMight * 1.5), currentMight + 5));
			// Also boost weapon experience to 8.
			data[32] = 0x08;
			wasModified = true;
			break;
		default:
			// Do nothing.
		}
	}
	
	private String ingameDescriptionString(ItemDataLoader itemData) {
		List<String> traitStrings = new ArrayList<String>();
		Map<String, String> shortStrings = new HashMap<String, String>();
		
		Boolean isMagic = getType() == WeaponType.ANIMA || getType() == WeaponType.LIGHT || getType() == WeaponType.DARK;
		Boolean isNormallyMelee = getType() == WeaponType.SWORD || getType() == WeaponType.LANCE || getType() == WeaponType.AXE;
		Boolean isOnlyRanged = getType() == WeaponType.BOW;
		
		if (getStatBonusPointer() != 0) {
			String statBonus = itemData.descriptionStringForAddress(getStatBonusPointer() - 0x8000000, isMagic, false);
			if (statBonus != null) { traitStrings.add(statBonus); }
			String statBonusShort = itemData.descriptionStringForAddress(getStatBonusPointer() - 0x8000000, isMagic, true);
			if (statBonus != null) { shortStrings.put(statBonus, statBonusShort); }
		}
		
		if (getMaxRange() > 3) { traitStrings.add("Strikes from afar"); shortStrings.put("Strikes from afar", "Siege"); }
		else if (isNormallyMelee && getMaxRange() > 1) { traitStrings.add("Ranged"); }
		else if (getMaxRange() > 2) { traitStrings.add("Extended range"); shortStrings.put("Extended range", "Long range"); }
		else if (isOnlyRanged && getMinRange() < 2) { traitStrings.add("Usable at close range"); shortStrings.put("Usable at close range", "Melee"); }
		
		if ((getAbility1() & Ability1Mask.BRAVE.ID) != 0) { traitStrings.add("Strikes twice"); shortStrings.put("Strikes twice", "Brave"); }
		if ((getAbility1() & Ability1Mask.MAGIC_DAMAGE.ID) != 0) { traitStrings.add("Targets RES"); shortStrings.put("Targets RES", "Magic"); }
		
		if ((getAbility2() & Ability2Mask.REVERSE_WEAPON_TRIANGLE.ID) != 0) {
			if (getType() == WeaponType.SWORD) { traitStrings.add("Strong v. Lances"); shortStrings.put("Strong v. Lances", "Bests lances"); }
			else if (getType() == WeaponType.LANCE) { traitStrings.add("Strong v. Axes"); shortStrings.put("Strong v. Axes", "Bests axes"); }
			else if (getType() == WeaponType.AXE) { traitStrings.add("Strong v. Swords"); shortStrings.put("Strong v. Swords", "Bests Swords"); }
			else if (getType() == WeaponType.ANIMA) { traitStrings.add("Strong v. Dark"); shortStrings.put("Strong v. Dark", "Bests Dark"); }
			else if (getType() == WeaponType.LIGHT) { traitStrings.add("Strong v. Anima"); shortStrings.put("Strong v. Anima", "Bests Anima"); }
			else if (getType() == WeaponType.DARK) { traitStrings.add("Strong v. Light"); shortStrings.put("Strong v. Light", "Bests Light"); }
		}
		
		if (getWeaponEffect() == FE7Data.Item.WeaponEffect.POISON.ID) { traitStrings.add("Poisons on hit"); shortStrings.put("Poisons on hit", "Poison"); }
		else if (getWeaponEffect() == FE7Data.Item.WeaponEffect.HALVES_HP.ID) { traitStrings.add("Halves HP"); shortStrings.put("Halves HP", "Eclipse"); }
		else if (getWeaponEffect() == FE7Data.Item.WeaponEffect.DEVIL.ID) { traitStrings.add("May damage user"); shortStrings.put("May damage user", "Devil"); }
		
		if (getCritical() >= 20) { traitStrings.add("High Critical Rate"); shortStrings.put("High Critical Rate", "Critical"); }
		
		if (getEffectivenessPointer() != 0) {
			String effectiveness = itemData.descriptionStringForAddress(getEffectivenessPointer() - 0x8000000, isMagic, false);
			if (effectiveness != null) { traitStrings.add(effectiveness); }
			String effectivenessShort = itemData.descriptionStringForAddress(getEffectivenessPointer() - 0x8000000, isMagic, true);
			if (effectivenessShort != null) { shortStrings.put(effectiveness, effectivenessShort); }
		}
		
		if (traitStrings.isEmpty()) { return null; }
		
		int length = 0;
		for (String current : traitStrings) { length += current.length(); }
		List<String> shortenedStrings = new ArrayList<String>();
		while(length > 30 && traitStrings.size() > 0) {
			String lastString = traitStrings.get(traitStrings.size() - 1);
			traitStrings.remove(traitStrings.size() - 1);
			String shorterString = shortStrings.get(lastString);
			shortenedStrings.add(0, shorterString != null ? shorterString : lastString);
			
			length = 0;
			for (String current : traitStrings) { length += current.length(); }
			for (String current : shortenedStrings) { length += current.length(); }
		}
		
		traitStrings.addAll(shortenedStrings);
		
		StringBuilder sb = new StringBuilder();
		sb.append(traitStrings.get(0));
		traitStrings.remove(0);
		while (!traitStrings.isEmpty()) {
			sb.append(", " + traitStrings.get(0));
			traitStrings.remove(0);
		}
		
		sb.append(".");
		
		return sb.toString();
	}

	@Override
	public void turnIntoLordWeapon(int lordID, int nameIndex, int descriptionIndex, WeaponType weaponType,
			boolean isUnbreakable, int targetWeaponWeight, GBAFEItemData referenceItem, ItemDataLoader itemData,
			FreeSpaceManager freeSpace) {
		// We don't really use this for FE7, but if we need it, we can implement it.
		
	}

	@Override
	public GBAFEItemData createLordWeapon(int lordID, int newItemID, int nameIndex, int descriptionIndex, WeaponType weaponType,
			boolean isUnbreakable, int targetWeaponWeight, int iconIndex, ItemDataLoader itemData, FreeSpaceManager freeSpace) {
		
		FE7Item newItem = new FE7Item();
		
		// First two bytes are the name index.
		byte[] nameBytes = WhyDoesJavaNotHaveThese.byteArrayFromLongValue(nameIndex, true, 2);
		byte[] descriptionBytes = WhyDoesJavaNotHaveThese.byteArrayFromLongValue(descriptionIndex, true, 2);
		newItem.data[0] = nameBytes[0];
		newItem.data[1] = nameBytes[1];
		newItem.data[2] = descriptionBytes[0];
		newItem.data[3] = descriptionBytes[1];
		
		// Two bytes of use item description, which we won't use.
		newItem.data[4] = 0;
		newItem.data[5] = 0;
		
		// The Item ID is next.
		newItem.data[6] = (byte)newItemID;
		
		// Weapon type.
		switch (weaponType) {
		case SWORD: newItem.data[7] = 0; break;
		case LANCE: newItem.data[7] = 1; break;
		case AXE: newItem.data[7] = 2; break;
		case BOW: newItem.data[7] = 3; break;
		case ANIMA: newItem.data[7] = 5; break;
		case LIGHT: newItem.data[7] = 6; break;
		case DARK: newItem.data[7] = 7; break;
		default: assert false; return null;
		}
		
		int ability1 = FE7Data.Item.Ability1Mask.WEAPON.ID;
		if (weaponType == WeaponType.ANIMA || weaponType == WeaponType.LIGHT || weaponType == WeaponType.DARK) {
			ability1 |= FE7Data.Item.Ability1Mask.MAGIC.ID;
		}
		ability1 |= FE7Data.Item.Ability1Mask.UNSELLABLE.ID;
		if (isUnbreakable) {
			ability1 |= FE7Data.Item.Ability1Mask.UNBREAKABLE.ID;
		}
		newItem.data[8] = (byte)(ability1 & 0xFF);
		
		// Ability 2 has an unused weapon lock that we could use. We'll set it later if necessary.
		// Ability 3 is the locks, but we'll have it set later, because the locks needed are going to take some more logic.
		// Ability 4 is also not used.
		
		// No need for stat bonuses.
		newItem.setStatBonusPointer(0);
		// Effectiveness. It should be effective against Knights and Cavs. If it's a bow, it also needs fliers.
		long knightCavClassOffsets = itemData.offsetForAdditionalData(AdditionalData.KNIGHTCAV_EFFECT);
		if (weaponType == WeaponType.BOW) {
			String effectivenessKey = "Knights, Cavs, and Flier Effectiveness";
			long effectivenessPointer = freeSpace.getOffsetForKey(effectivenessKey);
			if (effectivenessPointer != -1) {
				newItem.setEffectivenessPointer(effectivenessPointer);
			} else {
				byte[] flierClassIDs = itemData.bytesForAdditionalData(AdditionalData.FLIERS_EFFECT);
				byte[] knightCavClassIDs = itemData.bytesForAdditionalData(AdditionalData.KNIGHTCAV_EFFECT);
				ByteArrayBuilder newClassIDs = new ByteArrayBuilder();
				newClassIDs.appendBytes(knightCavClassIDs);
				if (newClassIDs.getLastByteWritten() == 0) {
					newClassIDs.deleteLastByte();
				}
				newClassIDs.appendBytes(flierClassIDs);
				if (newClassIDs.getLastByteWritten() != 0) {
					newClassIDs.appendByte((byte)0);
				}
				newItem.setEffectivenessPointer(freeSpace.setValue(newClassIDs.toByteArray(), effectivenessKey));
			}
		} else {
			newItem.setEffectivenessPointer(knightCavClassOffsets);
		}
		
		// Weapon stats are copied from the base weapon (this).
		newItem.setDurability(getDurability());
		newItem.setMight(getMight());
		newItem.setHit(getHit());
		newItem.setWeight(targetWeaponWeight);
		newItem.setCritical(getCritical());
		int minRange = weaponType == WeaponType.BOW ? 2 : 1;
		int maxRange = weaponType == WeaponType.SWORD || weaponType == WeaponType.LANCE || weaponType == WeaponType.AXE ? 1 : 2; 
		newItem.data[25] = (byte)((minRange << 4) | (maxRange));
		newItem.data[26] = 0; 
		newItem.data[27] = 0; // Cost per use. Not useful since it's not sellable.
		newItem.data[28] = (byte)(FE7Data.Item.FE7WeaponRank.E.value & 0xFF); // Since we're re-using weapon locks, this needs to be set.
		newItem.data[29] = (byte)iconIndex;
		// Staff use effect should be 0. We don't deal with staves.
		newItem.data[30] = 0;
		newItem.data[31] = 0; // No other weird effects.
		// Copy everything else from this reference item.
		newItem.data[32] = data[32]; // Weapon XP
		newItem.data[33] = data[33];
		newItem.data[34] = data[34];
		newItem.data[35] = data[35];
		return newItem;
	}
}
