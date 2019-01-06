package ui.fe4;

public class WeightedOptions {
	
	public enum Weight {
		NONE, VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH
	}
	
	public final boolean enabled;
	public final Weight weight;

	public WeightedOptions(boolean enabled, Weight weight) {
		super();
		this.enabled = enabled;
		this.weight = weight;
	}
}
