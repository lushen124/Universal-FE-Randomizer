package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fedata.gba.GBAFEItemData;
import fedata.gba.general.GBAFEShop;
import fedata.gba.general.WeaponType;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.ShopLoader;
import random.general.PoolDistributor;
import random.general.WeightedDistributor;
import ui.model.ShopOptions;

public class ShopRandomizer {
	
	public static int rngSalt = 8480;
	
	private enum ItemQuality {
		ANY, EARLY, MID, LATE, SECRET, SUPER_SECRET;
	}

	public static void randomizeShops(ShopLoader shopData, ItemDataLoader itemData, boolean includePromoWeapons, boolean includePoisonWeapons, ShopOptions options, Random rng) {
		List<GBAFEShop> allShops = shopData.getAllShops();
		for (GBAFEShop shop : allShops) {
			if (shopData.shopWasUpdated(shop)) { continue; }
			if (shopData.isMapShop(shop) && options.randomizeMapShops == false) { continue; }
			
			List<GBAFEShop> linked = new ArrayList<GBAFEShop>(shop.groupedShops());
			linked.sort(new Comparator<GBAFEShop>() {
				@Override
				public int compare(GBAFEShop o1, GBAFEShop o2) {
					return Integer.compare(allShops.indexOf(o1), allShops.indexOf(o2));
				}
			});
			Set<GBAFEItemData> sharedItemList = new HashSet<GBAFEItemData>();
			
			for (GBAFEShop currentShop : linked) {
				if (shopData.isMapShop(currentShop) && options.randomizeMapShops == false) { continue; }
				WeightedDistributor<ItemQuality> distributor = new WeightedDistributor<ItemQuality>();
				
				if (options.useProgressionWeights) {
					switch (currentShop.getGameStage()) {
					case EARLY:
						// 70% early, 20% mid, 9% late, 1% secret
						distributor.addItem(ItemQuality.EARLY, 70);
						distributor.addItem(ItemQuality.MID, 20);
						distributor.addItem(ItemQuality.LATE, 9);
						distributor.addItem(ItemQuality.SECRET, 1);
						break;
					case MID:
						// 25% early, 50% mid, 15% late, 8% secret, 2% rare secret
						distributor.addItem(ItemQuality.EARLY, 25);
						distributor.addItem(ItemQuality.MID, 50);
						distributor.addItem(ItemQuality.LATE, 15);
						distributor.addItem(ItemQuality.SECRET, 8);
						distributor.addItem(ItemQuality.SUPER_SECRET, 2);
						break;
					case LATE:
						// 10% early, 25% mid, 45% late, 16% secret, 4% rare secret
						distributor.addItem(ItemQuality.EARLY, 10);
						distributor.addItem(ItemQuality.MID, 25);
						distributor.addItem(ItemQuality.LATE, 45);
						distributor.addItem(ItemQuality.SECRET, 16);
						distributor.addItem(ItemQuality.SUPER_SECRET, 4);
						break;
					}
				} else {
					distributor.addItem(ItemQuality.ANY, 1);
				}
				
				PoolDistributor<GBAFEItemData> earlyPool = new PoolDistributor<GBAFEItemData>(false);
				PoolDistributor<GBAFEItemData> midPool = new PoolDistributor<GBAFEItemData>(false);
				PoolDistributor<GBAFEItemData> latePool = new PoolDistributor<GBAFEItemData>(false);
				PoolDistributor<GBAFEItemData> secretPool = new PoolDistributor<GBAFEItemData>(false);
				PoolDistributor<GBAFEItemData> superSecretPool = new PoolDistributor<GBAFEItemData>(false);
				
				if (shopData.isArmory(currentShop)) {
					addArmoryToPools(itemData, earlyPool, midPool, latePool, secretPool, superSecretPool);
					if (options.mixArmoryVendor) {
						addVendorToPools(itemData, earlyPool, midPool, latePool, secretPool, superSecretPool);
						if (options.mixSecretVendor) {
							addSecretToPools(itemData, secretPool, superSecretPool);
						}
					}
				} else if (shopData.isVendor(currentShop)) {
					addVendorToPools(itemData, earlyPool, midPool, latePool, secretPool, superSecretPool);
					if (options.mixSecretVendor) {
						addSecretToPools(itemData, secretPool, superSecretPool);
					}
				} else { // Secret shops.
					distributor = new WeightedDistributor<ItemQuality>();
					switch (currentShop.getGameStage()) {
					case EARLY:
						// 90% secret, 10% rare secret
						distributor.addItem(ItemQuality.SECRET, 90);
						distributor.addItem(ItemQuality.SUPER_SECRET, 10);
						break;
					case MID:
						// 75% secret, 25% rare secret
						distributor.addItem(ItemQuality.SECRET, 75);
						distributor.addItem(ItemQuality.SUPER_SECRET, 25);
						break;
					case LATE:
						// 60% secret, 40% rare secret
						distributor.addItem(ItemQuality.SECRET, 60);
						distributor.addItem(ItemQuality.SUPER_SECRET, 40);
						break;
					}
					
					addSecretToPools(itemData, secretPool, superSecretPool);
				}
				
				if (includePoisonWeapons == false) {
					for (GBAFEItemData item : itemData.getAllWeapons()) {
						if (itemData.isPoisonWeapon(item.getID())) {
							earlyPool.removeItem(item, true);
							midPool.removeItem(item, true);
							latePool.removeItem(item, true);
							secretPool.removeItem(item, true);
							superSecretPool.removeItem(item, true);
						}
					}
				}
				
				if (includePromoWeapons == false) {
					for (GBAFEItemData item : itemData.getAllWeapons()) {
						if (itemData.isPromoWeapon(item.getID())) {
							earlyPool.removeItem(item, true);
							midPool.removeItem(item, true);
							latePool.removeItem(item, true);
							secretPool.removeItem(item, true);
							superSecretPool.removeItem(item, true);
						}
					}
				}
				
				PoolDistributor<GBAFEItemData> anyPool = new PoolDistributor<GBAFEItemData>();
				anyPool.addAll(earlyPool.possibleResults());
				anyPool.addAll(midPool.possibleResults());
				anyPool.addAll(latePool.possibleResults());
				anyPool.addAll(secretPool.possibleResults());
				anyPool.addAll(superSecretPool.possibleResults());
				
				List<GBAFEItemData> newShopList = new ArrayList<GBAFEItemData>();
				int range = options.shopSize.maxValue - options.shopSize.minValue;
				int numberOfItems = rng.nextInt(range + 1) + options.shopSize.minValue;
				if (shopData.isMapShop(currentShop)) {
					numberOfItems = Math.max(options.shopSize.maxValue, shopData.getItemsInShop(currentShop).size());
				}
				while (newShopList.size() < numberOfItems) {
					if (distributor.possibleResults().isEmpty()) {
						distributor.addItem(ItemQuality.ANY, 1);
					}
					ItemQuality quality = distributor.getRandomItem(rng);
					GBAFEItemData addedItem = null;
					switch (quality) {
					case ANY:
						addedItem = addRandomElement(newShopList, sharedItemList, anyPool, rng, false);
						if (addedItem == null) {
							assert sharedItemList.size() >= anyPool.possibleResults().size(): "No items remaining for this group of shops, but we haven't exhausted all items yet somehow.";
							sharedItemList.clear();
							continue;
						}
						break;
					case EARLY:
						addedItem = addRandomElement(newShopList, sharedItemList, earlyPool, rng, false);
						if (addedItem == null) {
							distributor.removeItem(ItemQuality.EARLY);
							continue;
						}
						break;
					case MID:
						addedItem = addRandomElement(newShopList, sharedItemList, midPool, rng, false);
						if (addedItem == null) {
							distributor.removeItem(ItemQuality.MID);
							continue;
						}
						break;
					case LATE:
						addedItem = addRandomElement(newShopList, sharedItemList, latePool, rng, false); 
						if (addedItem == null) {
							distributor.removeItem(ItemQuality.LATE);
							continue;
						}
						break;
					case SECRET:
						addedItem = addRandomElement(newShopList, sharedItemList, secretPool, rng, true); 
						if (addedItem == null) {
							distributor.removeItem(ItemQuality.SECRET);
							continue;
						}
						break;
					case SUPER_SECRET:
						addedItem = addRandomElement(newShopList, sharedItemList, superSecretPool, rng, true); 
						if (addedItem == null) {
							distributor.removeItem(ItemQuality.SUPER_SECRET);
							continue;
						}
						break;
					}
					
					if (addedItem != null) {
						sharedItemList.add(addedItem);
					}
				}
				
				newShopList.sort(GBAFEItemData.idComparator);
				shopData.setItemsInShop(currentShop, newShopList);
			}
		}
	}
	
	private static void addArmoryToPools(ItemDataLoader itemData,
			PoolDistributor<GBAFEItemData> early, 
			PoolDistributor<GBAFEItemData> mid, 
			PoolDistributor<GBAFEItemData> late, 
			PoolDistributor<GBAFEItemData> secret, 
			PoolDistributor<GBAFEItemData> superSecret) {
		early.addAll(itemData.earlyGameArmory());
		mid.addAll(itemData.midGameArmory());
		late.addAll(itemData.lateGameArmory());
		List<GBAFEItemData> secretItems = itemData.secretItems();
		secretItems.removeIf(item -> item.getType() == WeaponType.NOT_A_WEAPON || item.getType() == WeaponType.STAFF || item.getType() == WeaponType.ANIMA || item.getType() == WeaponType.DARK || item.getType() == WeaponType.LIGHT);
		secret.addAll(secretItems);
		List<GBAFEItemData> superSecretItems = itemData.rareSecretItems();
		superSecretItems.removeIf(item -> item.getType() == WeaponType.NOT_A_WEAPON || item.getType() == WeaponType.STAFF || item.getType() == WeaponType.ANIMA || item.getType() == WeaponType.DARK || item.getType() == WeaponType.LIGHT);
		superSecret.addAll(superSecretItems);
	}
	
	private static void addVendorToPools(ItemDataLoader itemData,
			PoolDistributor<GBAFEItemData> early, 
			PoolDistributor<GBAFEItemData> mid, 
			PoolDistributor<GBAFEItemData> late, 
			PoolDistributor<GBAFEItemData> secret, 
			PoolDistributor<GBAFEItemData> superSecret) {
		early.addAll(itemData.earlyGameVendor());
		mid.addAll(itemData.midGameVendor());
		late.addAll(itemData.lateGameVendor());
		List<GBAFEItemData> secretItems = itemData.secretItems();
		secretItems.removeIf(item -> item.getType() == WeaponType.SWORD || item.getType() == WeaponType.LANCE || item.getType() == WeaponType.AXE || item.getType() == WeaponType.BOW);
		secret.addAll(secretItems);
		List<GBAFEItemData> superSecretItems = itemData.rareSecretItems();
		superSecretItems.removeIf(item -> item.getType() == WeaponType.SWORD || item.getType() == WeaponType.LANCE || item.getType() == WeaponType.AXE || item.getType() == WeaponType.BOW);
		superSecret.addAll(superSecretItems);
	}
	
	private static void addSecretToPools(ItemDataLoader itemData,
			PoolDistributor<GBAFEItemData> secret, 
			PoolDistributor<GBAFEItemData> superSecret) {
		List<GBAFEItemData> secretItems = itemData.secretItems();
		secret.addAll(secretItems);
		List<GBAFEItemData> superSecretItems = itemData.rareSecretItems();
		superSecret.addAll(superSecretItems);
	}
	
	private static GBAFEItemData addRandomElement(List<GBAFEItemData> destination, Set<GBAFEItemData> excludeSet, PoolDistributor<GBAFEItemData> pool, Random rng, boolean deprioritizeWeapons) {
		if (destination.containsAll(pool.possibleResults())) {
			return null;
		}
		if (excludeSet.containsAll(pool.possibleResults())) {
			return null;
		}
		
		GBAFEItemData item = pool.getRandomItem(rng, false);
		boolean rerolledWeapon = false;
		while (destination.contains(item) || excludeSet.contains(item)) {
			item = pool.getRandomItem(rng, false);
			if (item.getType() != WeaponType.NOT_A_WEAPON && deprioritizeWeapons && !rerolledWeapon) {
				rerolledWeapon = true;
				item = pool.getRandomItem(rng, false);
			}
		}
		destination.add(item);
		return item;
	}
}
