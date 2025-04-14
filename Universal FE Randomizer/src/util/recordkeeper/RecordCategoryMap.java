package util.recordkeeper;

import java.util.*;
import java.util.stream.Collectors;

public class RecordCategoryMap {
	private boolean sorted = false;
	private Map<String, RecordEntry> entries = new HashMap<>();
	private List<String> orderedKeys = new ArrayList<String>();

	public void addEntry(String key, RecordEntry entry) {
		if (!entries.containsKey(key))
			entries.put(key, entry);
		
		orderedKeys.add(key);
	}

	public RecordEntry getEntry(String key) {
		return entries.get(key);
	}

	public Map<String, RecordEntry> getEntries() {
		return entries;
	}

	public List<String> getKeyList() {
		if (sorted) {
			List<RecordEntry> entryList = new ArrayList<>(entries.values());
			Collections.sort(entryList, entryList.get(0).getComparator());
			return entryList.stream().map(e -> e.entryKey).collect(Collectors.toList());
		} else {
			return orderedKeys;
		}
	}

	public void setSorted() {
		this.sorted = true;
	}

}