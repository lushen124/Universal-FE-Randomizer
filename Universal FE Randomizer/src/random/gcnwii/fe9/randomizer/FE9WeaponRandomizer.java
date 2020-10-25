package random.gcnwii.fe9.randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fedata.gcnwii.fe9.FE9Item;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9CommonTextLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponEffect;
import ui.model.WeaponEffectOptions;
import util.WhyDoesJavaNotHaveThese;

public class FE9WeaponRandomizer {
	
	static final int rngSalt = 92209;
	
	public static void randomizeWeaponMight(int variance, int min, int max, FE9ItemDataLoader itemData, Random rng) {
		if (variance == 0) { return; }
		List<FE9Item> items = itemData.allItems();
		for (FE9Item item : items) {
			if (itemData.isBlacklisted(item)) { continue; }
			if (!itemData.isWeapon(item) || itemData.isStaff(item)) { continue; }
			int factor = rng.nextInt(2) == 0 ? 1 : -1;
			item.setItemMight(WhyDoesJavaNotHaveThese.clamp(item.getItemMight() + (rng.nextInt(variance + 1) * factor), min, max));
			item.commitChanges();
		}
	}
	
	public static void randomizeWeaponAccuracy(int variance, int min, int max, FE9ItemDataLoader itemData, Random rng) {
		if (variance == 0) { return; }
		List<FE9Item> items = itemData.allItems();
		for (FE9Item item : items) {
			if (itemData.isBlacklisted(item)) { continue; }
			if (!itemData.isWeapon(item) || itemData.isStaff(item)) { continue; }
			int factor = rng.nextInt(2) == 0 ? 1 : -1;
			item.setItemAccuracy(WhyDoesJavaNotHaveThese.clamp(item.getItemAccuracy() + (rng.nextInt(variance + 1) * factor), min, max));
			item.commitChanges();
		}
	}
	
	public static void randomizeWeaponWeight(int variance, int min, int max, FE9ItemDataLoader itemData, Random rng) {
		if (variance == 0) { return; }
		List<FE9Item> items = itemData.allItems();
		for (FE9Item item : items) {
			if (itemData.isBlacklisted(item)) { continue; }
			if (!itemData.isWeapon(item) || itemData.isStaff(item)) { continue; }
			int factor = rng.nextInt(2) == 0 ? 1 : -1;
			item.setItemWeight(WhyDoesJavaNotHaveThese.clamp(item.getItemWeight() + (rng.nextInt(variance + 1) * factor), min, max));
			item.commitChanges();
		}
	}

	public static void randomizeWeaponDurability(int variance, int min, int max, FE9ItemDataLoader itemData, Random rng) {
		if (variance == 0) { return; }
		List<FE9Item> items = itemData.allItems();
		for (FE9Item item : items) {
			if (itemData.isBlacklisted(item)) { continue; }
			if (!itemData.isWeapon(item) || itemData.isStaff(item)) { continue; }
			if (itemData.isSiegeTome(item)) { continue; }
			int factor = rng.nextInt(2) == 0 ? 1 : -1;
			item.setItemDurability(WhyDoesJavaNotHaveThese.clamp(item.getItemDurability() + (rng.nextInt(variance + 1) * factor), min, max));
			item.commitChanges();
		}
	}
	
	public static void addRandomEffects(int effectChance, boolean safeBasicWeapons, boolean includeLaguzWeapons, WeaponEffectOptions effectOptions, GCNISOHandler handler, FE9ItemDataLoader itemData, FE9CommonTextLoader textData, Random rng) {
		if (effectChance == 0) { return; }
		List<FE9Item> items = itemData.allItems();
		
		List<WeaponEffect> effects = new ArrayList<WeaponEffect>();
		if (effectOptions.statBoosts) { effects.add(WeaponEffect.STAT_BOOST); }
		if (effectOptions.effectiveness) { effects.add(WeaponEffect.EFFECTIVENESS); }
		if (effectOptions.unbreakable) { effects.add(WeaponEffect.UNBREAKABLE); }
		if (effectOptions.brave) { effects.add(WeaponEffect.BRAVE); }
		if (effectOptions.reverseTriangle) { effects.add(WeaponEffect.REVERSE_TRIANGLE); }
		if (effectOptions.extendedRange) { effects.add(WeaponEffect.EXTEND_RANGE); }
		if (effectOptions.highCritical) {
			WeaponEffect effect = WeaponEffect.CRITICAL;
			effect.additionalInfo.put(WeaponEffect.InfoKey.CRITICAL_RANGE, effectOptions.criticalRange);
			effects.add(effect);
		}
		if (effectOptions.magicDamage) { effects.add(WeaponEffect.MAGIC_DAMAGE); }
		if (effectOptions.poison) { effects.add(WeaponEffect.POISON); }
		if (effectOptions.stealHP) { effects.add(WeaponEffect.STEAL_HP); }
		if (effectOptions.critImmune) { effects.add(WeaponEffect.CRIT_IMMUNE); }
		if (effectOptions.noCrit) { effects.add(WeaponEffect.NO_CRIT); }
		
		if (effects.isEmpty()) { return ; }
		
		List<WeaponEffect> possibleEffects = new ArrayList<WeaponEffect>();
		
		for (FE9Item item : items) {
			if (itemData.isBlacklisted(item)) { continue; }
			if (!itemData.isWeapon(item)) { continue; }
			if (safeBasicWeapons && itemData.isBasicWeapon(item)) { continue; }
			if (!includeLaguzWeapons && itemData.isLaguzWeapon(item)) { continue; }
			if (rng.nextInt(100) >= effectChance) { continue; }
			
			boolean success = false;
			possibleEffects.clear();
			possibleEffects.addAll(effects);
			do {
				int selectedIndex = rng.nextInt(possibleEffects.size());
				success = itemData.applyEffectToWeapon(possibleEffects.get(selectedIndex), item, handler, textData, rng);
				possibleEffects.remove(selectedIndex);
			} while (!success && !possibleEffects.isEmpty());
			
			item.commitChanges();
		}
	}
}
