package random.general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PoolDistributor<T> {

	List<T> itemList;
	Set<T> itemSet;

	public PoolDistributor() {
		itemList = new ArrayList<T>();
		itemSet = new HashSet<T>();
	}

	public void addAll(T... items) {
		for (T item : items) {
			addItem(item, 1);
		}
	}

	public void addAll(List<T> items) {
		for (T item : items) {
			addItem(item, 1);
		}
	}

	public void addItem(T item) {
		addItem(item, 1);
	}

	public void addItem(T item, int count) {
		if (item == null) {
			return;
		}
		itemList.add(item);
		itemSet.add(item);
	}

	public void removeItem(T itemToRemove, boolean allInstances) {
		if (itemToRemove == null) {
			return;
		}
		if (allInstances) {
			itemList.removeIf(item -> (item == itemToRemove));
			itemSet.remove(itemToRemove);
		} else {
			itemList.remove(itemToRemove);
			if (!itemList.contains(itemToRemove)) {
				itemSet.remove(itemToRemove);
			}
		}
	}

	public T getRandomItem(Random rng, boolean removeItem) {
		if (itemList.isEmpty()) {
			return null;
		}
		int randomIndex = rng.nextInt(itemList.size());
		T randomItem = itemList.get(randomIndex);
		if (removeItem) {
			itemList.remove(randomIndex);
			if (!itemList.contains(randomItem)) {
				itemSet.remove(randomItem);
			}
		}

		return randomItem;
	}

	public int itemCount(T item) {
		if (item == null) {
			return 0;
		}
		return (int) itemList.stream().filter(listItem -> (listItem == item)).count();
	}

	public Set<T> possibleResults() {
		return itemSet;
	}
}
