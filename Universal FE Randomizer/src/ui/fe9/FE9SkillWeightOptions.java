package ui.fe9;

import java.util.Map;
import java.util.Set;

import ui.model.WeightedOptions;

public class FE9SkillWeightOptions {
	
	private Map<String, WeightedOptions> weightBySkillName;
	
	public FE9SkillWeightOptions(Map<String, WeightedOptions> weightsByName) {
		weightBySkillName = weightsByName;
	}
	
	public Set<String> getSkillNames() {
		return weightBySkillName.keySet();
	}
	
	public WeightedOptions getWeightedOptionsByName(String name) {
		return weightBySkillName.get(name);
	}
}
