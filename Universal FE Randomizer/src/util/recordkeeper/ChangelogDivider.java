package util.recordkeeper;

public class ChangelogDivider implements ChangelogElement {

	@Override
	public String getIdentifier() {
		return null;
	}

	@Override
	public String build() {
		return "<hr>";
	}

}
