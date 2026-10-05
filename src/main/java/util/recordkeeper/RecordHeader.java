package util.recordkeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordHeader {
    String title;
    Map<String, String> randomizationOptions = new HashMap<>();
    protected boolean sorted;
    
    public List<String> getKeyList(){
    	ArrayList<String> keys = new ArrayList<>(randomizationOptions.keySet());
    	if(sorted) {
    		Collections.sort(keys);
    	}
    	return keys;
    }
}