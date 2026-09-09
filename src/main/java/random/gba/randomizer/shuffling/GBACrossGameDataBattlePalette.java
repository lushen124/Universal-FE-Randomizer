package random.gba.randomizer.shuffling;

import java.util.List;
import java.util.ArrayList;

import fedata.gba.general.PaletteColor;

public class GBACrossGameDataBattlePalette {
	public String[] hair;
	public String[] primary;
	public String[] secondary;
	public String[] tertiary;
	
	public GBACrossGameDataBattlePalette(String[] hair, String[] primary, String[] secondary, String[] tertiary) {
		this.hair = hair;
		this.primary = primary;
		this.secondary = secondary;
		this.tertiary = tertiary;
	}
	
	public List<PaletteColor> getHairColors() {
		return getColorsFromStrings(hair);
	}
	
	public List<PaletteColor> getPrimaryColors() {
		return getColorsFromStrings(primary);
	}
	
	public List<PaletteColor> getSecondaryColors() {
		return getColorsFromStrings(secondary);
	}
	
	public List<PaletteColor> getTertiaryColors() {
		return getColorsFromStrings(tertiary);
	}
	
	private List<PaletteColor> getColorsFromStrings(String[] hexStrings) {
		List<PaletteColor> colors = new ArrayList<PaletteColor>();
		if (hexStrings == null) { return colors; }
		for (int i = 0; i < hexStrings.length; i++) {
			colors.add(PaletteColor.colorFromHex(hexStrings[i]));
		}
		
		return colors;
	}
}