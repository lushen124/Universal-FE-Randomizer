package ui.model;

import java.util.Map;
import java.util.Set;

public class SkillWeightOptions {
	
	private Map<String, WeightedOptions> weightBySkillName;

	// FE4 Only
	private int pursuitChance;

	public SkillWeightOptions(Map<String, WeightedOptions> weightsByName) {
		weightBySkillName = weightsByName;
	}
	public SkillWeightOptions(Map<String, WeightedOptions> weightsByName, int pursuitChance) {
		weightBySkillName = weightsByName;
		this.pursuitChance = pursuitChance;
	}

	public Set<String> getSkillNames() {
		return weightBySkillName.keySet();
	}
	public int getPursuitChance() { return this.pursuitChance; }
	public void setPursuitChance(int pursuitChance) { this.pursuitChance = pursuitChance; }
	public WeightedOptions getWeightedOptionsByName(String name) {
		return weightBySkillName.get(name);
	}
}
