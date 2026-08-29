package util.recordkeeper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordEntry {
	protected String entryKey;
	protected List<String> allInfoKeys = new ArrayList<>();
    protected Map<String, RecordInformation> recordInformation = new HashMap<>();

    public RecordEntry() {
    	
    }
    
    public RecordEntry(String entryKey) {
    	this.entryKey = entryKey;
    }
    
    public List<String> getInfoKeys(){
    	return this.allInfoKeys;
    }
    
    public void addInfo(String key, RecordInformation info) {
    	this.allInfoKeys.add(key);
        recordInformation.putIfAbsent(key, info);
    }

    public Map<String, RecordInformation> getInfos() {
        return this.recordInformation;
    }

    public RecordInformation getInfo(String key) {
        return this.recordInformation.get(key);
    }

    public void removeInfo(String key) {
    	this.allInfoKeys.remove(key);
    	this.recordInformation.remove(key);
    }
    
    public Comparator<RecordEntry> getComparator() {
    	return new Comparator<RecordEntry>() {

			@Override
			public int compare(RecordEntry o1, RecordEntry o2) {
				return o1.entryKey.compareTo(o2.entryKey);
			}
    		
		};
    }
}