package util.recordkeeper.fe9;

import java.util.ArrayList;
import java.util.List;

public class ChangelogImage implements ChangelogElement {

	private String identifier;
	private String url;
	
	private int intrinsicWidth;
	private int intrinsicHeight;
	
	private List<String> classes;
	
	public ChangelogImage(String identifier, String url, int width, int height) {
		this.identifier = identifier;
		this.url = url;
		
		intrinsicWidth = width;
		intrinsicHeight = height;
		
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
		sb.append("<img ");
		
		sb.append("id=\"" + identifier + "\" ");
		if (!classes.isEmpty()) {
			sb.append("class=\"");
			for (String elementClass : classes) {
				sb.append(elementClass + " ");
			}
			sb.append("\" ");
		}
		sb.append("src=\"" + url + "\" ");
		
		if (intrinsicWidth > 0) { sb.append("width=\"" + intrinsicWidth + "\" "); }
		if (intrinsicHeight > 0) { sb.append("height=\"" + intrinsicHeight + "\" "); }
		
		sb.append(">");
		
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
