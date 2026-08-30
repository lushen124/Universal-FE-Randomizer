package util.recordkeeper.fe9;

import java.util.List;

public interface ChangelogElement {

	public String getIdentifier();
	
	public List<String> getClasses();
	public void addClass(String elementClass);
	
	public String build();
	
}
