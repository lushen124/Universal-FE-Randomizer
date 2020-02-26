package util.recordkeeper;

import java.util.List;

public interface ChangelogElement {

	public String getIdentifier();
	
	public List<String> getClasses();
	public void addClass(String elementClass);
	
	public String build();
	
}
