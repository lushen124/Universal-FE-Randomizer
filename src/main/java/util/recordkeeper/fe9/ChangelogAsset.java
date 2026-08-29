package util.recordkeeper.fe9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangelogAsset implements ChangelogElement {
	
	private static Map<String, Base64Asset> nameToBase64 = new HashMap<String, Base64Asset>();
	
	private String identifier;
	private List<String> classes;
	
	private Base64Asset asset;
	
	public ChangelogAsset(String identifier, Base64Asset asset) {
		this.identifier = identifier;
		classes = new ArrayList<String>();
		
		if (!nameToBase64.containsKey(asset.name)) {
			nameToBase64.put(asset.name, asset);
		}
		
		this.asset = asset;
	}
	
	public static void registerAssets(ChangelogBuilder builder) {
		for (String name : nameToBase64.keySet()) {
			ChangelogStyleRule rule = new ChangelogStyleRule();
			Base64Asset asset = nameToBase64.get(name);
			rule.setElementClass("asset-" + asset.name);
			rule.addRule("background-image", "url('" + asset.base64String + "')");
			rule.addRule("background-repeat", "no-repeat");
			rule.addRule("background-size", "contain");
			rule.addRule("width", asset.width + "px");
			rule.addRule("height", asset.height + "px");
			builder.addStyle(rule);
		}
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public List<String> getClasses() {
		return classes;
	}

	@Override
	public void addClass(String elementClass) {
		classes.add(elementClass);
	}

	@Override
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("<a id=\"anchor-" + identifier + "\"></a>");
		sb.append("<div id=\"" + identifier + "\" class=\"asset-" + asset.name);
		for (String elementClass : classes) {
			sb.append(" " + elementClass);
		}
		sb.append("\"></div>");
		return sb.toString();
	}

}
