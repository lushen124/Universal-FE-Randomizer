package ui.model;

public class MinMaxVarOption {
	
	public final int minValue;
	public final int maxValue;
	public final int variance;
	
	public MinMaxVarOption(int minValue, int maxValue, int variance) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.variance = variance;
	}
	
	public MinMaxVarOption(MinMaxOption minMax, int variance) {
		super();
		this.minValue = minMax.minValue;
		this.maxValue = minMax.maxValue;
		this.variance = variance;
	}
}
