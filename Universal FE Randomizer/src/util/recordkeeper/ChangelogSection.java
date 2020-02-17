package util.recordkeeper;

import java.util.ArrayList;
import java.util.List;

public class ChangelogSection implements ChangelogElement {
	
	private String identifier;
	
	private List<ChangelogElement> childElements;
	
	public ChangelogSection(String identifier) {
		this.identifier = identifier;
		childElements = new ArrayList<ChangelogElement>();
	}
	
	public void addElement(ChangelogElement element) {
		childElements.add(element);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("<a name=\"" + identifier + "\"><div id=\"" + identifier + "\" class=\"changelog-section\"></a>");
		for (ChangelogElement element : childElements) {
			sb.append(element.build());
		}
		sb.append("</div>");
		return sb.toString();
	}
}
