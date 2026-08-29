package ui.model.fe9;

public class FE9OtherCharacterOptions {
	
	public final boolean randomizeCON;
	public final int conVariance;
	
	public final boolean randomizeAffinity;
	
	public FE9OtherCharacterOptions(boolean randomizeCON, int conVariance, boolean randomizeAffinity) {
		this.randomizeCON = randomizeCON;
		this.conVariance = conVariance;
		this.randomizeAffinity = randomizeAffinity;
	}

}
