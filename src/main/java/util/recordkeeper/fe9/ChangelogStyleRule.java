package util.recordkeeper.fe9;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangelogStyleRule {

	private String elementTag;
	private String elementIdentifier;
	private String elementClass;
	
	private List<String> childTags;
	
	private String overrideSelectorString;
	
	private Map<String, String> rules;
	
	public ChangelogStyleRule() { rules = new HashMap<String, String>(); };
	
	public void setElementTag(String tag) {
		elementTag = tag;
	}
	
	public String getElementTag() { return elementTag; }
	
	public void setElementIdentifier(String id) {
		elementIdentifier = id;
	}
	
	public String getElementIdentifier() { return elementIdentifier; }
	
	public void setElementClass(String elClass) {
		elementClass = elClass;
	}
	
	public String getElementClass() { return elementClass; }
	
	public void setChildTags(List<String> childTags) {
		this.childTags = childTags;
	}
	
	public void addRule(String name, String value) {
		rules.put(name, value);
	}
	
	public void setOverrideSelectorString(String string) {
		overrideSelectorString = string;
	}
	
	public String build() {
		StringBuilder sb = new StringBuilder();
		if (overrideSelectorString != null) {
			sb.append(overrideSelectorString);
		} else {
			if (childTags != null && !childTags.isEmpty()) {
				for (String childTag : childTags) {
					if (elementTag != null) { sb.append(elementTag); }
					else if (elementIdentifier != null) { sb.append("#" + elementIdentifier); }
					else if (elementClass != null) { sb.append("." + elementClass); }
					
					sb.append(" " + childTag + ",");
				}
				sb.deleteCharAt(sb.length() - 1);
			} else {
				if (elementTag != null) { sb.append(elementTag); }
				else if (elementIdentifier != null) { sb.append("#" + elementIdentifier); }
				else if (elementClass != null) { sb.append("." + elementClass); }
			}
			
			if (sb.length() == 0) { return ""; }
		}
		sb.append(" {\n");
		for (String key : rules.keySet()) {
			sb.append("\t" + key + ": " + rules.get(key) + ";\n");
		}
		sb.append("}\n");
		return sb.toString();
	}
}
