package random.general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WeightedDistributor<T> {
	
	List<T> itemList;
	Set<T> itemSet;
	
	public WeightedDistributor() {
		itemList = new ArrayList<T>();
		itemSet = new HashSet<T>();
	}
	
	public WeightedDistributor(WeightedDistributor<T> original) {
		itemList = new ArrayList<T>();
		itemList.addAll(original.itemList);
		
		itemSet = new HashSet<T>();
		itemSet.addAll(original.itemSet);
	}

	public void addItem(T item, int weight) {
		if (weight < 1 || item == null) { return; }
		for (int i = 0 ; i < weight; i++) {
			itemList.add(item);
		}
		itemSet.add(item);
	}
	
	public void addItems(List<T> items, int weight) {
		for (T item : items) {
			addItem(item, weight);
		}
	}
	
	public void removeItem(T itemToRemove) {
		if (itemToRemove == null) { return; }
		boolean didRemove = itemList.removeIf(item -> (item == itemToRemove));
		if (didRemove) {
			itemSet.remove(itemToRemove);
		}
	}
	
	public T getRandomItem(Random rng) {
		if (itemList.isEmpty()) { return null; }
		int randomIndex = rng.nextInt(itemList.size());
		return itemList.get(randomIndex);
	}
	
	public Set<T> possibleResults() {
		return itemSet;
	}
	
	public double chanceOfResult(T result) {
		double denominator = itemList.size();
		if (denominator == 0) { return 0; }
		double numerator = itemList.stream().filter(item -> (item == result)).count();
		return numerator / denominator;
	}
}
