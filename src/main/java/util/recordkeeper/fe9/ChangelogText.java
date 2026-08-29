package util.recordkeeper.fe9;

import java.util.ArrayList;
import java.util.List;

public class ChangelogText implements ChangelogElement {
	
	public enum Style {
		NONE, BOLD, ITALICS
	}
	
	private String identifier;
	private String content;
	
	private Style textStyle;
	
	private List<String> classes;
	
	public ChangelogText(String identifier, Style textStyle, String content) {
		this.identifier = identifier;
		this.content = content;
		
		this.textStyle = textStyle;
		
		classes = new ArrayList<String>();
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("<a id=\"anchor-" + identifier + "\"></a>");
		sb.append("<p id=\"" + identifier + "\" ");
		if (!classes.isEmpty()) {
			sb.append("class=\"");
			for (String elementClass : classes) {
				sb.append(elementClass + " ");
			}
			sb.append("\"");
		}
		switch (textStyle) {
		case NONE: break;
		case BOLD: sb.append(" style=\"font-weight:bold;\""); break;
		case ITALICS: sb.append(" style=\"font-style:italic;\""); break;
		}
		sb.append(">" + content + "</p>" + System.lineSeparator());
		
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
