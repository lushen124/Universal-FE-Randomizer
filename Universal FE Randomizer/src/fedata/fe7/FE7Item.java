package fedata.fe7;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import fedata.FEItem;
import fedata.FESpellAnimationCollection;
import fedata.fe7.FE7Data.Item.FE7WeaponRank;
import fedata.fe7.FE7Data.Item.FE7WeaponType;
import fedata.fe7.FE7Data.Item.WeaponEffect;
import fedata.fe7.FE7SpellAnimationCollection.Animation;
import fedata.general.WeaponEffects;
import fedata.general.WeaponRank;
import fedata.general.WeaponType;
import util.WhyDoesJavaNotHaveThese;

public class FE7Item implements FEItem {
	
	private byte[] originalData;
	private byte[] data;
	
	private long originalOffset;
	
	private enum StatBoost {
		STRMAG(0x1000000), SKL(0x1000008), SPD(0x1000010), DEF(0x1000018), RES(0x1000020), LCK(0x1000028);
		
		public long address;
		
		private StatBoost(final long address) { this.address = address; }
		
		public byte[] toPointer() {
			long referenceAddress = 0x08000000 + address;
			return new byte[] { (byte)(referenceAddress & 0xFF), (byte)((referenceAddress >> 8) & 0xFF), (byte)((referenceAddress >> 16) & 0xFF), (byte)((referenceAddress >> 24) & 0xFF) };
		}
	}
	
	private enum Effectiveness {
		KNIGHTCAV(0xC97E9C), KNIGHT(0xC97E96), DRAGON(0xC97EC5), CAVALRY(0xC97EB7), MYRMIDON(0xC97EAD), FLIERS(0xC97ED2), DRAGON2(0xC97EC5);
		
		public long address;
		
		private Effectiveness(final long address) { this.address = address; }
		
		public byte[] toPointer() {
			long referenceAddress = 0x08000000 + address;
			return new byte[] { (byte)(referenceAddress & 0xFF), (byte)((referenceAddress >> 8) & 0xFF), (byte)((referenceAddress >> 16) & 0xFF), (byte)((referenceAddress >> 24) & 0xFF) };
		}
	}
	
	private Boolean wasModified = false;
	private Boolean hasChanges = false;

	public FE7Item(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}

	public int getNameIndex() {
		return (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
	}

	public int getDescriptionIndex() {
		return (data[2] & 0xFF) | ((data[3] & 0xFF) << 8);
	}

	public int getUseDescriptionIndex() {
		return (data[4] & 0xFF) | ((data[5] & 0xFF) << 8);
	}

	public int getID() {
		return data[6] & 0xFF;
	}

	public WeaponType getType() {
		FE7WeaponType type = FE7WeaponType.valueOf(data[7] & 0xFF);
		return type.toGeneralType();
	}

	public int getAbility1() {
		return data[8] & 0xFF;
	}

	public int getAbility2() {
		return data[9] & 0xFF;
	}

	public int getAbility3() {
		return data[10] & 0xFF;
	}

	public int getAbility4() {
		return data[11] & 0xFF;
	}

	public long getStatBonusPointer() {
		return (data[12] & 0xFF) | ((data[13] & 0xFF) << 8) | ((data[14] & 0xFF) << 16) | ((data[15] & 0xFF) << 24) ;
	}

	public long getEffectivenessPointer() {
		return (data[16] & 0xFF) | ((data[17] & 0xFF) << 8) | ((data[18] & 0xFF) << 16) | ((data[19] & 0xFF) << 24) ;
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
	
	public void applyRandomEffect(Set<WeaponEffects> allowedEffects, FESpellAnimationCollection spellAnimations) {
		if (getType() == WeaponType.NOT_A_WEAPON) {
			System.out.println("Item " + getID() + " is not a weapon.");
			return;
		}
		
		Set<WeaponEffects> effectPool = effectsAvailable();
		effectPool.retainAll(allowedEffects);
		
		if (effectPool.isEmpty()) {
			System.out.println("No allowed effects for " + getID() + "!");
			return;
		}
		
		int randomEffect = ThreadLocalRandom.current().nextInt(effectPool.size());
		WeaponEffects[] effectList = effectPool.toArray(new WeaponEffects[effectPool.size()]);
		applyEffect(effectList[randomEffect], spellAnimations);
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

		if ((getAbility1() & FE7Data.Item.Ability1Mask.UNBREAKABLE.ID) == 0) {
			effects.add(WeaponEffects.UNBREAKABLE);
		}
		if ((getAbility1() & FE7Data.Item.Ability1Mask.BRAVE.ID) == 0) {
			effects.add(WeaponEffects.BRAVE);
		}
		if ((getAbility1() & FE7Data.Item.Ability1Mask.MAGICDAMAGE.ID) == 0 && (getAbility1() & FE7Data.Item.Ability1Mask.MAGIC.ID) == 0 && getType() != WeaponType.AXE) {
			effects.add(WeaponEffects.MAGIC_DAMAGE);
		}
		
		if ((getAbility2() & FE7Data.Item.Ability2Mask.REVERSEWEAPONTRIANGLE.ID) == 0) {
			effects.add(WeaponEffects.REVERSE_TRIANGLE);
		}
		
		if (getWeaponEffect() == FE7Data.Item.WeaponEffect.POISON.ID) {
			effects.add(WeaponEffects.POISON);
		}
		if (getWeaponEffect() == FE7Data.Item.WeaponEffect.HALFHP.ID) {
			effects.add(WeaponEffects.HALF_HP);
		}
		if (getWeaponEffect() == FE7Data.Item.WeaponEffect.DEVIL.ID) {
			effects.add(WeaponEffects.DEVIL);
		}
		
		return effects;
	}
	
	private void applyEffect(WeaponEffects effect, FESpellAnimationCollection spellAnimations) {
		switch (effect) {
		case STAT_BOOSTS:
			StatBoost[] boosts = StatBoost.values();
			int randomIndex = ThreadLocalRandom.current().nextInt(boosts.length);
			StatBoost selectedBoost = boosts[randomIndex];
			byte[] pointer = selectedBoost.toPointer();
			data[12] = pointer[0];
			data[13] = pointer[1];
			data[14] = pointer[2];
			data[15] = pointer[3];
			wasModified = true;
			break;
		case EFFECTIVENESS:
			Effectiveness[] effects = Effectiveness.values();
			randomIndex = ThreadLocalRandom.current().nextInt(effects.length);
			Effectiveness selectedEffect = effects[randomIndex];
			pointer = selectedEffect.toPointer();
			data[16] = pointer[0];
			data[17] = pointer[1];
			data[18] = pointer[2];
			data[19] = pointer[3];
			wasModified = true;
			break;
		case HIGH_CRITICAL:
			int currentCritical = getCritical();
			int newCritical = currentCritical + 5 * (4 + ThreadLocalRandom.current().nextInt(7));
			setCritical(newCritical);
			break;
		case EXTEND_RANGE:
			int minRange = getMinRange();
			int maxRange = getMaxRange();
			if (minRange == 2) { // 2-range locked bows. 50/50 of being melee or longbow.
				int random = ThreadLocalRandom.current().nextInt(2);
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
			ability1 |= FE7Data.Item.Ability1Mask.MAGICDAMAGE.ID;
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
				spellAnimations.setAnimationValueForID(getID(), FE7SpellAnimationCollection.Animation.randomMagicAnimation().value);
			}
			break;
		case REVERSE_TRIANGLE:
			int ability2 = getAbility2();
			ability2 |= FE7Data.Item.Ability2Mask.REVERSEWEAPONTRIANGLE.ID;
			data[9] = (byte)(ability2 & 0xFF);
			wasModified = true;
			break;
		case POISON:
			int effectValue = FE7Data.Item.WeaponEffect.POISON.ID;
			data[31] = (byte)(effectValue & 0xFF);
			wasModified = true;
			break;
		case HALF_HP:
			effectValue = FE7Data.Item.WeaponEffect.HALFHP.ID;
			data[31] = (byte)(effectValue & 0xFF);
			wasModified = true;
			break;
		case DEVIL:
			effectValue = FE7Data.Item.WeaponEffect.DEVIL.ID;
			wasModified = true;
			break;
		default:
			// Do nothing.
		}
	}
}
