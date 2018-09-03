package util.recordkeeper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordKeeper {
	
	private class Entry {
		List<String> allKeys;
		Map<String, String> originalValues;
		Map<String, String> updatedValues; 
		
		private Entry() {
			allKeys = new ArrayList<String>();
			originalValues = new HashMap<String, String>();
			updatedValues = new HashMap<String, String>();
		}
	}
	
	private class EntryMap {
		List<String> keyList;
		Map<String, Entry> entriesByKey;
		
		private EntryMap() {
			keyList = new ArrayList<String>();
			entriesByKey = new HashMap<String, Entry>();
		}
	}
	
	private class Header {
		String title;
		
		List<String> keyList;
		Map<String, String> values;
		
		private Header() {
			keyList = new ArrayList<String>();
			values = new HashMap<String, String>();
		}
	}
	
	private Header header;
	private List<String> allCategories;
	private Map<String, EntryMap> entriesByCategory;
	
	public RecordKeeper(String title) {
		allCategories = new ArrayList<String>();
		entriesByCategory = new HashMap<String, EntryMap>();
		header = new Header();
		header.title = title;
		
		header.keyList = new ArrayList<String>();
		header.values = new HashMap<String, String>();
	}
	
	public void addHeaderItem(String title, String value) {
		if (header.keyList.contains(title)) { header.keyList.remove(title); }
		header.keyList.add(title);
		header.values.put(title, value);
	}

	public void recordOriginalEntry(String category, String entryKey, String key, String originalValue) {
		 EntryMap entryMap = entriesByCategory.get(category);
		 if (entryMap == null) {
			 entryMap = new EntryMap();
			 entriesByCategory.put(category, entryMap);
			 allCategories.add(category);
		 }
		 
		 Entry entry = entryMap.entriesByKey.get(entryKey);
		 if (entry == null) {
			 entry = new Entry();
			 entryMap.keyList.add(entryKey);
			 entryMap.entriesByKey.put(entryKey, entry);
		 }
		 
		 if (!entry.allKeys.contains(key)) { 
			 entry.allKeys.add(key);
		 }
		 entry.originalValues.put(key, originalValue);
	}
	
	public void recordUpdatedEntry(String category, String entryKey, String key, String updatedValue) {
		EntryMap entryMap = entriesByCategory.get(category);
		 if (entryMap == null) {
			 entryMap = new EntryMap();
			 entriesByCategory.put(category, entryMap);
			 allCategories.add(category);
		 }
		 
		 Entry entry = entryMap.entriesByKey.get(entryKey);
		 if (entry == null) {
			 entry = new Entry();
			 entryMap.keyList.add(entryKey);
			 entryMap.entriesByKey.put(entryKey, entry);
		 }
		 if (!entry.allKeys.contains(key)) { 
			 entry.allKeys.add(key);
		 }
		 entry.updatedValues.put(key, updatedValue);
	}
	
	public void sortKeysInCategory(String category) {
		EntryMap entryMap = entriesByCategory.get(category);
		if (entryMap != null) {
			Collections.sort(entryMap.keyList);
		}
	}
	
	public Boolean exportRecordsToHTML(String outputPath) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputPath), Charset.forName("UTF-8").newEncoder());
			writer.write("<html><meta http-equiv=\"Content-Type\" content = \"text/html; charset=utf-8\" /><head><style>\n");
			writer.write("table, th, td {\n\tborder: 1px solid black;\n}\n");
			writer.write("</style></head><body>\n");
			writer.write("<center><h1><p>Changelog for " + header.title + "</p></h1><br>\n");
			writer.write("<hr>\n");
			writer.write("<table>\n");
			for (String key : header.keyList) {
				String value = header.values.get(key);
				writer.write("<tr><td>" + key + "</td><td>" + value + "</td></tr>\n");
			}
			writer.write("</table>\n");
			writer.write("<br><hr><br>\n");
			
			for (String category : allCategories) {
				writer.write("<h2 id=\"" + keyFromString(category) + "\">" + category + "</h2>");
				int column = 0;
				writer.write("<table>\n");
				EntryMap entries = entriesByCategory.get(category);
				for (String entryKey : entries.keyList) {
					if (column == 0) { writer.write("<tr>\n"); }
					writer.write("<td><a href=\"#" + keyFromString(entryKey) + "\">" + entryKey + "</a></td>\n");
					column += 1;
					if (column == 4) {
						column = 0;
						writer.write("</tr>\n");
					}
				}
				writer.write("</table>\n");
				writer.write("<br><hr><br>\n");
			}
			
			for (String category : allCategories) {
				EntryMap entries = entriesByCategory.get(category);
				for (String entryKey : entries.keyList) {
					Entry entry = entries.entriesByKey.get(entryKey);
					writer.write("<h3 id=\"" + keyFromString(entryKey) + "\">" + entryKey + "</h3><br>\n");
					writer.write("<table>\n");
					for (String key : entry.allKeys) {
						String oldValue = entry.originalValues.get(key);
						String newValue = entry.updatedValues.get(key);
						writer.write("<tr><td>" + key + "</td><td>" + (oldValue != null ? oldValue : "(null)") + "</td><td>" + (newValue != null ? newValue : "(null)") + "</td></tr>\n");
					}
					writer.write("</table>\n");
					writer.write("<a href=\"#" + keyFromString(category) + "\">Back to " + category + "</a>\n");
				}
				writer.write("<br><hr><br>\n");
			}
			
			writer.write("</body></html>\n");
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private String keyFromString(String string) {
		return string.replace(' ', '_');
	}
}
