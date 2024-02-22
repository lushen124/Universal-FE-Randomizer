package ui.model;

import util.recordkeeper.RecordKeeper;

public class StatboosterOptions {
	public enum StatboosterRandomizationModes {
		SAME_STAT, SHUFFLE, MULTIPLE_STATS;
	}
	
	public final boolean enabled;
	public final StatboosterRandomizationModes mode;
	public final int boostStrengthMin;
	public final int boostStrengthMax;
	
	// IF MODE = MULTIPLE_STATS
	public final int multipleStatsMin;
	public final int multipleStatsMax;
	
	public final boolean includeMov;
	public final boolean includeCon;
	
	public final boolean applyHpModifier;
	public final int hpModifier;
	
	
	public StatboosterOptions(boolean enabled, StatboosterRandomizationModes mode,
			int boostStrengthMin, int boostStrengthMax, int multipleStatsMin, int multipleStatsMax, boolean includeMov,
			boolean includeCon, boolean applyHpModifier, int hpModifier) {
		this.enabled = enabled;
		this.mode = mode;
		this.boostStrengthMin = boostStrengthMin;
		this.boostStrengthMax = boostStrengthMax;
		this.multipleStatsMin = multipleStatsMin;
		this.multipleStatsMax = multipleStatsMax;
		this.includeMov = includeMov;
		this.includeCon = includeCon;
		this.applyHpModifier = applyHpModifier;
		this.hpModifier = hpModifier;
	}
	
	public void record(RecordKeeper rk) {
		if(!enabled) {
			rk.addHeaderItem("Statbooster Randomization", "NO");
			return;
		} 
		StringBuilder sb = new StringBuilder();
		sb.append("Randomization Mode: ");
		switch(mode) {
		case MULTIPLE_STATS:
			sb.append(String.format("Random Number of Stats, Min: %d, Max: %d", multipleStatsMin, multipleStatsMax));
			break;
		case SAME_STAT:
			sb.append("Same Stat");
			break;
		case SHUFFLE:
			sb.append("Shuffle");
		}
		sb.append("<br>");
		sb.append(String.format("Statboost bounds: Min %d, Max %d", boostStrengthMin, boostStrengthMax)).append("<br>");
		sb.append(includeMov ? "Boots included" : "Boots not included").append("<br>");
		sb.append(includeCon ? "Body Ring included" : "Body Ring not included").append("<br>");
		sb.append(applyHpModifier ? String.format("HP Modifier Applied, value: %d", hpModifier) : "No HP Modifier applied").append("<br>"); 
		rk.addHeaderItem("Statbooster Randomization", sb.toString());
	}
	
}
