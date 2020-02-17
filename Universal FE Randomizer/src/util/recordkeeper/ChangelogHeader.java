package util.recordkeeper;

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
	
	public ChangelogHeader(HeaderLevel level, String header, String identifier) {
		this.level = level;
		this.header = header;
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public String build() {
		return "<h" + level.toInt() + " id=\"" + identifier + "\">" + header + "</h" + level.toInt() + ">";
	}
}
