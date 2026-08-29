package util.recordkeeper.fe9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangelogTOC implements ChangelogElement {
	
	private String identifier;
	private List<String> orderedAnchors;
	private Map<String, String> anchorsToDisplayString;
	private Map<String, ChangelogElement> anchorsToDisplayElement;
	
	private List<String> classes;
	
	public ChangelogTOC(String identifier) {
		this.identifier = identifier;
		
		orderedAnchors = new ArrayList<String>();
		anchorsToDisplayString = new HashMap<String, String>();
		anchorsToDisplayElement = new HashMap<String, ChangelogElement>();
		
		classes = new ArrayList<String>();
	}
	
	public void addAnchorWithTitle(String anchor, String displayString) {
		if (anchor == null || displayString == null) { return; }
		if (!anchor.startsWith("anchor-")) {
			anchor = "anchor-" + anchor;
		}
		orderedAnchors.add(anchor);
		anchorsToDisplayString.put(anchor, displayString);
	}
	
	public void addAnchorWithElement(String anchor, ChangelogElement displayElement) {
		if (anchor == null || displayElement == null) { return; }
		if (!anchor.startsWith("anchor-")) {
			anchor = "anchor-" + anchor;
		}
		orderedAnchors.add(anchor);
		anchorsToDisplayElement.put(anchor, displayElement);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String build() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<a id=\"anchor-" + identifier + "\"></a>");
		sb.append("<div class=\"toc-container");
		for (String elementClass : classes) {
			sb.append(" " + elementClass);
		}
		sb.append("\" id=\"" + identifier + "\">");
		for (String anchor : orderedAnchors) {
			sb.append("<div class=\"toc-item\" id=\"toc-item-" + anchor + "\">");
			sb.append("<a href=\"#" + anchor + "\">");
			if (anchorsToDisplayElement.get(anchor) != null) {
				sb.append(anchorsToDisplayElement.get(anchor).build());
			} else {
				sb.append(anchorsToDisplayString.get(anchor));
			}
			sb.append("</a></div>");
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
