package util.recordkeeper;

import java.util.*;
import java.util.stream.Collectors;

public class RecordCategoryMap {
	private boolean sorted = false;
	private Map<String, RecordEntry> entries = new HashMap<>();

	public void addEntry(String key, RecordEntry entry) {
		if (!entries.containsKey(key))
			entries.put(key, entry);
	}

	public RecordEntry getEntry(String key) {
		return entries.get(key);
	}

	public Map<String, RecordEntry> getEntries() {
		return entries;
	}

	public List<String> getKeyList() {
		List<RecordEntry> entryList = new ArrayList<>(entries.values());
		if (sorted) {
			Collections.sort(entryList, entryList.get(0).getComparator());
		}
		return entryList.stream().map(e -> e.entryKey).collect(Collectors.toList());
	}

	public void setSorted() {
		this.sorted = true;
	}

}