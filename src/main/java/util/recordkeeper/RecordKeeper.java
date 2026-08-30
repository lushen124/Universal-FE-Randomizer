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
import java.util.Set;

/**
 * Record Keeper for FE4 & GBAFE
 */
public class RecordKeeper {
	
	private RecordHeader header;
	private List<String> allCategories;
	private Map<String, RecordCategoryMap> entriesByCategory;
	
	private List<String> notes;
	
	public RecordKeeper(String title) {
		allCategories = new ArrayList<>();
		entriesByCategory = new HashMap<>();
		header = new RecordHeader();
		header.title = title;
		
		
		notes = new ArrayList<String>();
	}
	
	public void addHeaderItem(String title, String value) {
		header.randomizationOptions.put(title, value);
	}
	
	public void addNote(String note) {
		notes.add(note);
	}
	
	public void registerCategory(String category) {
		if (entriesByCategory.containsKey(category)) {
			return;
		}
		allCategories.add(category);
		entriesByCategory.put(category, new RecordCategoryMap());
	}
	
	public void setAdditionalInfo(String category, String entryKey, String key, String info) {
		RecordCategoryMap entryMap = entriesByCategory.get(category);
		if (entryMap == null) { return; }
		RecordEntry entry = entryMap.getEntry(key);
		if (entry == null) { return; }
		
		if(entry.getInfo(key) != null) {
			entry.getInfo(key).additionalInfo = info;
		}
	}
	
	public void clearAdditionalInfo(String category, String entryKey, String key) {
		RecordCategoryMap entryMap = entriesByCategory.get(category);
		if (entryMap == null) { return; }
		RecordEntry entry = entryMap.getEntry(key);
		if (entry == null) { return; }
		if(entry.getInfo(key) != null) {
			entry.getInfo(key).additionalInfo = null;
		}
	}

	public RecordEntry recordOriginalEntry(String category, String entryKey, String key, String originalValue) {
		RecordCategoryMap entryMap = entriesByCategory.get(category);
		 if (entryMap == null) {
			 entryMap = new RecordCategoryMap();
			 entriesByCategory.put(category, entryMap);
			 allCategories.add(category);
		 }
		 
		 RecordEntry entry = entryMap.getEntry(entryKey);
		 if (entry == null) {
			 entry = new RecordEntry(entryKey);
			 entryMap.addEntry(entryKey, entry);
		 }
		 
		 RecordInformation info = entry.getInfo(key);
		 if(info == null) {
			 // If the information is new, create a new object for it
			 entry.addInfo(key, new RecordInformation(originalValue));
			 return entry;
		 } 
		 
		 // if it already existed for some reason, just update
		 entry.getInfo(key).originalValue = originalValue;
		 return entry;
	}
	
	public RecordEntry recordUpdatedEntry(String category, String entryKey, String key, String updatedValue) {
		RecordCategoryMap entryMap = entriesByCategory.get(category);
		 if (entryMap == null) {
			 entryMap = new RecordCategoryMap();
			 entriesByCategory.put(category, entryMap);
			 allCategories.add(category);
		 }
		 
		 RecordEntry entry = entryMap.getEntry(entryKey);
		 if (entry == null) {
			 entry = new RecordEntry(entryKey);
			 entryMap.addEntry(entryKey, entry);
		 }
		 RecordInformation info = entry.getInfo(key);
		 if(info == null) {
			 // If the information is new, create a new object for it
			 entry.addInfo(key, new RecordInformation(null, updatedValue));
			 return entry;
		 } 
		 
		 // if it already existed for some reason, just update
		 entry.getInfo(key).updatedValue = updatedValue;
		 return entry;
	}
	
	public void sortKeysInCategory(String category) {
		RecordCategoryMap entryMap = entriesByCategory.get(category);
		entryMap.setSorted();
	}
	
	public void sortKeysInCategoryAndSubcategories(String category) {
		Set<String> keys = entriesByCategory.keySet();
		for (String key : keys) {
			if (key.startsWith(category)) {
				RecordCategoryMap entryMap = entriesByCategory.get(key);
				entryMap.setSorted();
			}
		}
	}
	
	public Boolean exportRecordsToHTML(String outputPath) {
		RecordBuilder builder = new RecordBuilder(outputPath);
		builder.buildHeader(header.title)
			.appendBasicTable(header.randomizationOptions.entrySet()).appendHorizontalSpacer()
			.appendSectionHeader("Notes", 2)
		    .appendLiteral("</center>\n")
			.appendLiteral("<div class=\"notes\">")
			.appendUnorderedList(notes)
			.appendLiteral("</div>\n")
			.appendLiteral("<center>\n").appendHorizontalSpacer();
		for (String category : allCategories) {
			builder.appendSectionHeader(category, 2)
				.appendTOC(entriesByCategory.get(category).getKeyList(), 4).appendHorizontalSpacer();
		}
		for (String category : allCategories) {
			RecordCategoryMap categoryMap = entriesByCategory.get(category);
			for (String entryKey : categoryMap.getKeyList()) {
				RecordEntry entry = categoryMap.getEntry(entryKey);
				builder.appendSectionHeader(entryKey, 3)
				.appendEntryComparison(entry)
				.appendLinkToTOC(category).appendHorizontalSpacer();
			}
		}
		builder.appendLiteral("</body></html>\n").write();
		
		return true;
	}
}
