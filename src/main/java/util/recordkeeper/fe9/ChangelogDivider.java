package util.recordkeeper.fe9;

import java.util.List;

public class ChangelogDivider implements ChangelogElement {

	@Override
	public String getIdentifier() {
		return null;
	}

	@Override
	public String build() {
		return "<hr>";
	}

	@Override
	public List<String> getClasses() {
		return null;
	}

	@Override
	public void addClass(String elementClass) {
		
	}

}
