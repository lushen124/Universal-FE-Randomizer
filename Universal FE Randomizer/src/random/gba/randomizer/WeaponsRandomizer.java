package random.gba.randomizer;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import fedata.gba.GBAFEItemData;
import fedata.gba.general.WeaponEffects;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import ui.model.WeaponEffectOptions;
import util.WhyDoesJavaNotHaveThese;

public class WeaponsRandomizer {
	
	static final int rngSalt = 64;
	
	public static void randomizeMights(int minMT, int maxMT, int variance, ItemDataLoader itemsData, Random rng) {
		GBAFEItemData[] allWeapons = itemsData.getAllWeapons();
		
		for (GBAFEItemData weapon : allWeapons) {
			int originalMight = weapon.getMight();
			int newMight = originalMight;
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newMight += rng.nextInt(variance + 1);
			} else {
				newMight -= rng.nextInt(variance + 1);
			}
			
			weapon.setMight(WhyDoesJavaNotHaveThese.clamp(newMight, minMT, maxMT));
		}
		
		itemsData.commit();
	}
	
	public static void randomizeHit(int minHit, int maxHit, int variance, ItemDataLoader itemsData, Random rng) {
		GBAFEItemData[] allWeapons = itemsData.getAllWeapons();
		
		for (GBAFEItemData weapon : allWeapons) {
			int originalHit = weapon.getHit();
			int newHit = originalHit;
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newHit += rng.nextInt(variance + 1);
			} else {
				newHit -= rng.nextInt(variance + 1);
			}
			
			weapon.setHit(WhyDoesJavaNotHaveThese.clamp(newHit, minHit, maxHit));
		}
		
		itemsData.commit();
	}
	
	public static void randomizeDurability(int minDurability, int maxDurability, int variance, ItemDataLoader itemsData, Random rng) {
		GBAFEItemData[] allWeapons = itemsData.getAllWeapons();
		
		for (GBAFEItemData weapon : allWeapons) {
			int originalDurability = weapon.getDurability();
			int newDurability = originalDurability;
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newDurability += rng.nextInt(variance + 1);
			} else {
				newDurability -= rng.nextInt(variance + 1);
			}
			
			if (weapon.getMaxRange() == 10) {
				// Siege Tomes get a minimum of 1 since they're normally low use.
				weapon.setDurability(WhyDoesJavaNotHaveThese.clamp(newDurability, 1, maxDurability));
			} else {
				weapon.setDurability(WhyDoesJavaNotHaveThese.clamp(newDurability, minDurability, maxDurability));
			}
		}
		
		itemsData.commit();
	}
	
	public static void randomizeWeight(int minWT, int maxWT, int variance, ItemDataLoader itemsData, Random rng) {
		GBAFEItemData[] allWeapons = itemsData.getAllWeapons();
		
		for (GBAFEItemData weapon : allWeapons) {
			int originalWeight = weapon.getWeight();
			int newWeight = originalWeight;
			int randomNum = rng.nextInt(2);
			if (randomNum == 0) {
				newWeight += rng.nextInt(variance + 1);
			} else {
				newWeight -= rng.nextInt(variance + 1);
			}
			
			weapon.setWeight(WhyDoesJavaNotHaveThese.clamp(newWeight, minWT, maxWT));
		}
		
		itemsData.commit();
	}
	
	public static void randomizeEffects(WeaponEffectOptions effectOptions, ItemDataLoader itemsData, TextLoader textData, Boolean ignoreIronWeapons, Boolean ignoreSteelWeapons, Boolean ignoreThrownWeapons, int effectChance, Random rng) {
		GBAFEItemData[] allWeapons = itemsData.getAllWeapons();
		
		Set<WeaponEffects> enabledEffects = new HashSet<WeaponEffects>();
		
		if (effectOptions.statBoosts) { enabledEffects.add(WeaponEffects.STAT_BOOSTS); }
		if (effectOptions.effectiveness) { enabledEffects.add(WeaponEffects.EFFECTIVENESS); }
		if (effectOptions.unbreakable) { enabledEffects.add(WeaponEffects.UNBREAKABLE); }
		if (effectOptions.brave) { enabledEffects.add(WeaponEffects.BRAVE); }
		if (effectOptions.reverseTriangle) { enabledEffects.add(WeaponEffects.REVERSE_TRIANGLE); }
		if (effectOptions.extendedRange) { enabledEffects.add(WeaponEffects.EXTEND_RANGE); }
		if (effectOptions.highCritical) { enabledEffects.add(WeaponEffects.HIGH_CRITICAL); }
		if (effectOptions.magicDamage) { enabledEffects.add(WeaponEffects.MAGIC_DAMAGE); }
		if (effectOptions.poison) { enabledEffects.add(WeaponEffects.POISON); }
		if (effectOptions.eclipse) { enabledEffects.add(WeaponEffects.HALF_HP); }
		if (effectOptions.devil) { enabledEffects.add(WeaponEffects.DEVIL); }
		
		for (GBAFEItemData weapon : allWeapons) {
			if (ignoreIronWeapons && itemsData.isBasicWeapon(weapon.getID())) { continue; }
			if (ignoreSteelWeapons && itemsData.isSteelWeapon(weapon.getID())) { continue; }
			if (ignoreThrownWeapons && itemsData.isBasicThrowingWeapon(weapon.getID())) { continue; }
			
			if (rng.nextInt(100) < effectChance) {
				weapon.applyRandomEffect(enabledEffects, itemsData, textData, itemsData.spellAnimations, rng);
			}
		}
		
		itemsData.commit();
	}
}
