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
	protected final List<String> includedShuffles;
	protected final boolean changeDescription;
	
	public CharacterShufflingOptions(ShuffleLevelingMode mode, boolean shuffleEnabled, int chance, List<String> selectedFiles, boolean changeDescription) {
		this.levelingMode = mode;
		this.shuffleEnabled = shuffleEnabled;
		this.chance = chance;
		this.includedShuffles = selectedFiles;
		this.changeDescription = changeDescription;
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

	public boolean shouldChangeDescription() {
		return changeDescription;
	}
	
}
