package ui.model;

import java.util.List;

/**
 * Model containing the different Options 
 */
public class CharacterShufflingOptions {
	public enum ShuffleLevelingMode {
		UNCHANGED, AUTOLEVEL;
	}
	
	protected final ShuffleLevelingMode levelingMode;
	protected final boolean shuffleEnabled;
	protected final int chance;
	private final List<String> includedShuffles;
	
	public CharacterShufflingOptions(ShuffleLevelingMode mode, boolean shuffleEnabled, int chance, List<String> selectedFiles) {
		this.levelingMode = mode;
		this.shuffleEnabled = shuffleEnabled;
		this.chance = chance;
		this.includedShuffles = selectedFiles;
	}
	
	public ShuffleLevelingMode getLevelingMode() {
		return levelingMode;
	}

	public boolean isShuffleEnabled() {
		return shuffleEnabled;
	}
	
	public int getChance() {
		return chance;
	}

	public List<String> getIncludedShuffles() {
		return includedShuffles;
	}
	
}
