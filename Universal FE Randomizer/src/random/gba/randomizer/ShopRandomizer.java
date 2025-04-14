package random.gba.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
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

public class ShopRandomizer {
	
	public static int rngSalt = 8480;
	
	private enum ItemQuality {
		EARLY, MID, LATE, SECRET, SUPER_SECRET;
	}

	public static void randomizeShops(ShopLoader shopData, ItemDataLoader itemData, boolean includePromoWeapons, boolean includePoisonWeapons, int minimumItemsPerShop, int maximumItemsPerShop, Random rng) {
		List<GBAFEShop> allShops = shopData.getAllShops();
		for (GBAFEShop shop : allShops) {
			if (shopData.shopWasUpdated(shop)) { continue; }
			
			List<GBAFEShop> linked = new ArrayList<GBAFEShop>(shop.groupedShops());
			Set<GBAFEItemData> sharedItemList = new HashSet<GBAFEItemData>();
			
			for (GBAFEShop currentShop : linked) {
				WeightedDistributor<ItemQuality> distributor = new WeightedDistributor<ItemQuality>();
				
				switch (currentShop.getGameStage()) {
				case EARLY:
					// 80% early, 15% mid, 4% late, 1% secret
					distributor.addItem(ItemQuality.EARLY, 80);
					distributor.addItem(ItemQuality.MID, 15);
					distributor.addItem(ItemQuality.LATE, 4);
					distributor.addItem(ItemQuality.SECRET, 1);
					break;
				case MID:
					// 15% early, 60% mid, 20% late, 4% secret, 1% rare secret
					distributor.addItem(ItemQuality.EARLY, 15);
					distributor.addItem(ItemQuality.MID, 60);
					distributor.addItem(ItemQuality.LATE, 20);
					distributor.addItem(ItemQuality.SECRET, 4);
					distributor.addItem(ItemQuality.SUPER_SECRET, 1);
					break;
				case LATE:
					// 10% early, 30% mid, 50% late, 8% secret, 2% rare secret
					distributor.addItem(ItemQuality.EARLY, 10);
					distributor.addItem(ItemQuality.MID, 30);
					distributor.addItem(ItemQuality.LATE, 50);
					distributor.addItem(ItemQuality.SECRET, 8);
					distributor.addItem(ItemQuality.SUPER_SECRET, 2);
					break;
				}
				
				PoolDistributor<GBAFEItemData> earlyPool = new PoolDistributor<GBAFEItemData>();
				PoolDistributor<GBAFEItemData> midPool = new PoolDistributor<GBAFEItemData>();
				PoolDistributor<GBAFEItemData> latePool = new PoolDistributor<GBAFEItemData>();
				PoolDistributor<GBAFEItemData> secretPool = new PoolDistributor<GBAFEItemData>();
				PoolDistributor<GBAFEItemData> superSecretPool = new PoolDistributor<GBAFEItemData>();
				
				if (shopData.isArmory(currentShop)) {
					earlyPool.addAll(itemData.earlyGameArmory());
					midPool.addAll(itemData.midGameArmory());
					latePool.addAll(itemData.lateGameArmory());
					List<GBAFEItemData> secretItems = itemData.secretItems();
					secretItems.removeIf(item -> item.getType() == WeaponType.NOT_A_WEAPON || item.getType() == WeaponType.STAFF || item.getType() == WeaponType.ANIMA || item.getType() == WeaponType.DARK || item.getType() == WeaponType.LIGHT);
					secretPool.addAll(secretItems);
					List<GBAFEItemData> superSecretItems = itemData.rareSecretItems();
					superSecretItems.removeIf(item -> item.getType() == WeaponType.NOT_A_WEAPON || item.getType() == WeaponType.STAFF || item.getType() == WeaponType.ANIMA || item.getType() == WeaponType.DARK || item.getType() == WeaponType.LIGHT);
					superSecretPool.addAll(superSecretItems);
				} else if (shopData.isVendor(currentShop)) {
					earlyPool.addAll(itemData.earlyGameVendor());
					midPool.addAll(itemData.midGameVendor());
					latePool.addAll(itemData.lateGameVendor());
					List<GBAFEItemData> secretItems = itemData.secretItems();
					secretItems.removeIf(item -> item.getType() == WeaponType.SWORD || item.getType() == WeaponType.LANCE || item.getType() == WeaponType.AXE || item.getType() == WeaponType.BOW);
					secretPool.addAll(secretItems);
					List<GBAFEItemData> superSecretItems = itemData.rareSecretItems();
					superSecretItems.removeIf(item -> item.getType() == WeaponType.SWORD || item.getType() == WeaponType.LANCE || item.getType() == WeaponType.AXE || item.getType() == WeaponType.BOW);
					superSecretPool.addAll(superSecretItems);
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
					
					List<GBAFEItemData> secretItems = itemData.secretItems();
					secretPool.addAll(secretItems);
					List<GBAFEItemData> superSecretItems = itemData.rareSecretItems();
					superSecretPool.addAll(superSecretItems);
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
				
				List<GBAFEItemData> newShopList = new ArrayList<GBAFEItemData>();
				int range = maximumItemsPerShop - minimumItemsPerShop;
				int numberOfItems = rng.nextInt(range) + minimumItemsPerShop;
				while (newShopList.size() < numberOfItems && distributor.possibleResults().isEmpty() == false) {
					ItemQuality quality = distributor.getRandomItem(rng);
					GBAFEItemData addedItem = null;
					switch (quality) {
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
				
				shopData.setItemsInShop(currentShop, newShopList);
			}
		}
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
