package ui.fe4;

public class WeightedOptions {
	
	public enum Weight {
		NONE, VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH;
		
		// return = slope * x + offset
		public int integerWeightUsingLinearWeight(int slope, int offset) {
			switch (this) {
			case NONE: return 0;
			case VERY_LOW: return slope + offset;
			case LOW: return 2 * slope + offset;
			case NORMAL: return 3 * slope + offset;
			case HIGH: return 4 * slope + offset;
			case VERY_HIGH: return 5 * slope + offset;
			}
			
			return 0;
		}
		
		// return = multiplier * x^(exponent) + offset
		public int integerWeightsUsingQuadraticWeight(int exponent, double multiplier, int offset) {
			switch (this) {
			case NONE: return 0;
			case VERY_LOW: return (int) Math.round(Math.pow(1, exponent) * multiplier) + offset;
			case LOW: return (int) Math.round(Math.pow(2, exponent) * multiplier) + offset;
			case NORMAL: return (int) Math.round(Math.pow(3, exponent) * multiplier) + offset;
			case HIGH: return (int) Math.round(Math.pow(4, exponent) * multiplier) + offset;
			case VERY_HIGH: return (int) Math.round(Math.pow(5, exponent) * multiplier) + offset;
			}
			
			return 0;
		}
		
		// return multiplier * base^x + offset
		public int integerWeightsUsingExpontialWeight(int base, int offset, double multiplier) {
			switch (this) {
			case NONE: return 0;
			case VERY_LOW: return (int) Math.round(Math.pow(base, 1) * multiplier) + offset;
			case LOW: return (int) Math.round(Math.pow(base, 2) * multiplier) + offset;
			case NORMAL: return (int) Math.round(Math.pow(base, 3) * multiplier) + offset;
			case HIGH: return (int) Math.round(Math.pow(base, 4) * multiplier) + offset;
			case VERY_HIGH: return (int) Math.round(Math.pow(base, 5) * multiplier) + offset;
			}
			
			return 0;
		}
	}
	
	public final boolean enabled;
	public final Weight weight;

	public WeightedOptions(boolean enabled, Weight weight) {
		super();
		this.enabled = enabled;
		this.weight = weight;
	}
}
