package fedata.fe6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.FEItem;
import fedata.FESpellAnimationCollection;
import fedata.fe6.FE6Data.Item.Ability1Mask;
import fedata.fe6.FE6Data.Item.Ability2Mask;
import fedata.fe6.FE6Data.Item.FE6WeaponRank;
import fedata.fe6.FE6Data.Item.FE6WeaponType;
import fedata.general.WeaponEffects;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;
import random.ItemDataLoader;
import random.TextLoader;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE6Item implements FEItem {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;
	
	public FE6Item(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
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
		FE6WeaponType type = FE6WeaponType.valueOf(data[7] & 0xFF);
		return type.toGeneralType();
	}

	public int getAbility1() {
		return data[8] & 0xFF;
	}

	public int getAbility2() {
		return data[9] & 0xFF;
	}

	public int getAbility3() {
		return 0; // Unused
	}

	public int getAbility4() {
		return 0; // Unused
	}

	public long getStatBonusPointer() {
		return (data[12] & 0xFF) | ((data[13] << 8) & 0xFF00) | ((data[14] << 16) & 0xFF0000) | ((data[15] << 24) & 0xFF000000) ;
	}

	public long getEffectivenessPointer() {
		return (data[16] & 0xFF) | ((data[17] << 8) & 0xFF00) | ((data[18] << 16) & 0xFF0000) | ((data[19] << 24) & 0xFF000000) ;
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

	public WeaponRank getWeaponRank() {
		int rank = data[28] & 0xFF;
		FE6WeaponRank weaponRank = FE6Data.Item.FE6WeaponRank.valueOf(rank);
		if (weaponRank != null) {
			return weaponRank.toGeneralRank();
		} else {
			FE6Data.Item weapon = FE6Data.Item.valueOf(getID());
			if (weapon != null && FE6Data.Item.allPrfRank.contains(weapon)) {
				return WeaponRank.PRF;
			} else {
				return WeaponRank.NONE;
			}
		}
	}

	public int getWeaponEffect() {
		return data[31];
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
	
	public void applyRandomEffect(Set<WeaponEffects> allowedEffects, ItemDataLoader itemData, TextLoader textData, FESpellAnimationCollection spellAnimations, Random rng) {
		if (getType() == WeaponType.NOT_A_WEAPON) {
			return;
		}
		
		Set<WeaponEffects> effectPool = effectsAvailable();
		effectPool.retainAll(allowedEffects);
		
		if (effectPool.isEmpty()) {
			return;
		}
		
		int randomEffect = rng.nextInt(effectPool.size());
		WeaponEffects[] effectList = effectPool.toArray(new WeaponEffects[effectPool.size()]);
		WeaponEffects selectedEffect = effectList[randomEffect];
		applyEffect(selectedEffect, itemData, spellAnimations, rng);
		if (selectedEffect != WeaponEffects.NONE) {
			String updatedDescription = ingameDescriptionString(itemData);
			if (updatedDescription != null) {
				textData.setStringAtIndex(getDescriptionIndex(), updatedDescription);
				DebugPrinter.log(DebugPrinter.Key.WEAPONS, "Weapon " + textData.getStringAtIndex(getNameIndex()) + " is now " + updatedDescription);
			} else {
				DebugPrinter.log(DebugPrinter.Key.WEAPONS, "Weapon " + textData.getStringAtIndex(getNameIndex()) + " has no effect.");
			}
		}
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
	
	private Set<WeaponEffects> effectsAvailable() {
		
		Set<WeaponEffects> effects = new HashSet<WeaponEffects>();
		
		effects.add(WeaponEffects.NONE);
		
		if (getStatBonusPointer() == 0) {
			effects.add(WeaponEffects.STAT_BOOSTS);
		}
		if (getEffectivenessPointer() == 0) {
			effects.add(WeaponEffects.EFFECTIVENESS);
		}
		if (getCritical() < 10) {
			effects.add(WeaponEffects.HIGH_CRITICAL);
		}
		if ((getMinRange() == 2 || getMaxRange() == 1) && getType() != WeaponType.AXE) { // Ranged axes are stupid to implement. They require modifying animation pointers for each class.
			effects.add(WeaponEffects.EXTEND_RANGE);
		}

		if ((getAbility1() & FE6Data.Item.Ability1Mask.UNBREAKABLE.ID) == 0) {
			effects.add(WeaponEffects.UNBREAKABLE);
		}
		if ((getAbility1() & FE6Data.Item.Ability1Mask.BRAVE.ID) == 0) {
			effects.add(WeaponEffects.BRAVE);
		}
		if ((getAbility1() & FE6Data.Item.Ability1Mask.MAGICDAMAGE.ID) == 0 && (getAbility1() & FE6Data.Item.Ability1Mask.MAGIC.ID) == 0 && getType() != WeaponType.AXE) {
			effects.add(WeaponEffects.MAGIC_DAMAGE);
		}
		
		if ((getAbility2() & FE6Data.Item.Ability2Mask.REVERSEWEAPONTRIANGLE.ID) == 0 && getType() != WeaponType.BOW) {
			effects.add(WeaponEffects.REVERSE_TRIANGLE);
		}
		
		if (getWeaponEffect() == 0) {
			effects.add(WeaponEffects.POISON);
			effects.add(WeaponEffects.DEVIL);
		}
		
		return effects;
	}
	
	private void applyEffect(WeaponEffects effect, ItemDataLoader itemData, FESpellAnimationCollection spellAnimations, Random rng) {
		switch (effect) {
		case STAT_BOOSTS:
			long[] boosts = itemData.possibleStatBoostAddresses();
			int randomIndex = rng.nextInt(boosts.length);
			long selectedBoostAddress = boosts[randomIndex];
			byte[] pointer = WhyDoesJavaNotHaveThese.bytesFromAddress(selectedBoostAddress);
			data[12] = pointer[0];
			data[13] = pointer[1];
			data[14] = pointer[2];
			data[15] = pointer[3];
			wasModified = true;
			break;
		case EFFECTIVENESS:
			long[] effects = itemData.possibleEffectivenessAddresses();
			randomIndex = rng.nextInt(effects.length);
			long selectedEffectivenessAddress = effects[randomIndex];
			pointer = WhyDoesJavaNotHaveThese.bytesFromAddress(selectedEffectivenessAddress);
			data[16] = pointer[0];
			data[17] = pointer[1];
			data[18] = pointer[2];
			data[19] = pointer[3];
			wasModified = true;
			break;
		case HIGH_CRITICAL:
			int currentCritical = getCritical();
			int newCritical = currentCritical + 5 * (4 + rng.nextInt(7));
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
					spellAnimations.setAnimationValueForID(getID(), FE6SpellAnimationCollection.Animation.JAVELIN.value);
				} else if (getType() == WeaponType.AXE) {
					spellAnimations.setAnimationValueForID(getID(), FE6SpellAnimationCollection.Animation.THROWN_AXE.value);
				} else {
					spellAnimations.setAnimationValueForID(getID(), FE6SpellAnimationCollection.Animation.ARROW.value);
				}
			}
			setMinRange(minRange);
			setMaxRange(maxRange);
			break;
		case UNBREAKABLE:
			int ability1 = getAbility1();
			ability1 |= FE6Data.Item.Ability1Mask.UNBREAKABLE.ID;
			data[8] = (byte)(ability1 & 0xFF);
			wasModified = true;
			break;
		case BRAVE:
			ability1 = getAbility1();
			ability1 |= FE6Data.Item.Ability1Mask.BRAVE.ID;
			data[8] = (byte)(ability1 & 0xFF);
			wasModified = true;
			break;
		case MAGIC_DAMAGE:
			ability1 = getAbility1();
			ability1 |= FE6Data.Item.Ability1Mask.MAGICDAMAGE.ID;
			data[8] = (byte)(ability1 & 0xFF);
			
			if (getMaxRange() == 1) {
				setMaxRange(2);
			}
			wasModified = true;
			
			if (getType() == WeaponType.AXE) {
				// Unfortunately, ranged axes will soft lock the game if any other animation is used. 
				spellAnimations.setAnimationValueForID(getID(), FE6SpellAnimationCollection.Animation.THROWN_AXE.value);
			} else {
				// Everything else is fine though.
				spellAnimations.setAnimationValueForID(getID(), FE6SpellAnimationCollection.Animation.randomMagicAnimation(rng).value);
			}
			break;
		case REVERSE_TRIANGLE:
			int ability2 = getAbility2();
			ability2 |= FE6Data.Item.Ability2Mask.REVERSEWEAPONTRIANGLE.ID;
			data[9] = (byte)(ability2 & 0xFF);
			wasModified = true;
			break;
		case POISON:
			int effectValue = FE6Data.Item.WeaponEffect.POISON.ID;
			data[31] = (byte)(effectValue & 0xFF);
			wasModified = true;
			break;
		case DEVIL:
			effectValue = FE6Data.Item.WeaponEffect.DEVIL.ID;
			data[31] = (byte)(effectValue & 0xFF);
			int currentMight = getMight();
			setMight(Math.max((int)(currentMight * 1.5), currentMight + 5));
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
		if ((getAbility1() & Ability1Mask.MAGICDAMAGE.ID) != 0) { traitStrings.add("Fixed damage at range"); shortStrings.put("Fixed damage at range", "Magic"); }
		
		if ((getAbility2() & Ability2Mask.REVERSEWEAPONTRIANGLE.ID) != 0) {
			if (getType() == WeaponType.SWORD) { traitStrings.add("Strong vs. Lances"); shortStrings.put("Strong vs. Lances", "Bests lances"); }
			else if (getType() == WeaponType.LANCE) { traitStrings.add("Strong vs. Axes"); shortStrings.put("Strong vs. Axes", "Bests axes"); }
			else if (getType() == WeaponType.AXE) { traitStrings.add("Strong vs. Swords"); shortStrings.put("Strong vs. Swords", "Bests Swords"); }
			else if (getType() == WeaponType.ANIMA) { traitStrings.add("Strong vs. Dark Magic"); shortStrings.put("Strong vs. Dark Magic", "Bests Dark"); }
			else if (getType() == WeaponType.LIGHT) { traitStrings.add("Strong vs. Anima Magic"); shortStrings.put("Strong vs. Anima Magic", "Bests Anima"); }
			else if (getType() == WeaponType.DARK) { traitStrings.add("Strong vs. Light Magic"); shortStrings.put("Strong vs. Light Magic", "Bests Light"); }
		}
		
		if (getWeaponEffect() == FE6Data.Item.WeaponEffect.POISON.ID) { traitStrings.add("Poisons on hit"); shortStrings.put("Poisons on hit", "Poison"); }
		else if (getWeaponEffect() == FE6Data.Item.WeaponEffect.DEVIL.ID) { traitStrings.add("May damage user"); shortStrings.put("May damage user", "Devil"); }
		
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

}
