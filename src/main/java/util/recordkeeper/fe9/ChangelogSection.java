package util.recordkeeper.fe9;

import java.util.ArrayList;
import java.util.List;

public class ChangelogSection implements ChangelogElement {
	
	private String identifier;
	
	private List<ChangelogElement> childElements;
	
	private List<String> classes;
	
	public ChangelogSection(String identifier) {
		this.identifier = identifier;
		childElements = new ArrayList<ChangelogElement>();
		
		classes = new ArrayList<String>();
	}
	
	public void addElement(ChangelogElement element) {
		childElements.add(element);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public ChangelogElement getChildWithIdentifier(String identifier) {
		for (ChangelogElement element : childElements) {
			if (element.getIdentifier().equals(identifier)) { return element; }
		}
		
		return null;
	}
	
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("<a id=\"anchor-" + identifier + "\"></a>");
		sb.append("<div id=\"" + identifier + "\" class=\"changelog-section");
		for (String elementClass : classes) {
			sb.append(" " + elementClass);
		}
		sb.append("\"></a>");
		for (ChangelogElement element : childElements) {
			sb.append(element.build() + System.lineSeparator());
		}
		sb.append("</div>");
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
