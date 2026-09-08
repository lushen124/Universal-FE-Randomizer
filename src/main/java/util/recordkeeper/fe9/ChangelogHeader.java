package util.recordkeeper.fe9;

import java.util.ArrayList;
import java.util.List;

public class ChangelogHeader implements ChangelogElement {
	
	public enum HeaderLevel {
		HEADING_1, HEADING_2, HEADING_3, HEADING_4, HEADING_5, HEADING_6;
		
		private int toInt() {
			switch (this) {
			case HEADING_1: return 1;
			case HEADING_2: return 2;
			case HEADING_3: return 3;
			case HEADING_4: return 4;
			case HEADING_5: return 5;
			case HEADING_6: return 6;
			default: return 7;
			}
		}
	}
	
	HeaderLevel level;
	String header;
	String identifier;
	
	private List<String> classes;
	
	public ChangelogHeader(HeaderLevel level, String header, String identifier) {
		this.level = level;
		this.header = header;
		this.identifier = identifier;
		
		classes = new ArrayList<String>();
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("<a id=\"anchor-" + identifier + "\"></a>");
		sb.append("<h" + level.toInt() + " id=\"" + identifier + "\"");
		if (!classes.isEmpty()) {
			sb.append(" class=\"");
			for (String elementClass : classes) {
				sb.append(elementClass + " ");
			}
			sb.append("\"");
		}
		sb.append(">" + header + "</h" + level.toInt() + ">");
		
		return sb.toString();
	}

	@Override
	public List<String> getClasses() {
		return classes;
	}

	@Override
	public void addClass(String elementClass) {
		classes.add(elementClass);
	}
}
