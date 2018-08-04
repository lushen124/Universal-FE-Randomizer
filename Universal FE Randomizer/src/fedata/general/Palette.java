package fedata.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.FileHandler;

public class Palette {
	
	PaletteInfo info;
	private int paletteSize;
	
	private byte[] rawData;
	
	private List<PaletteColor> hair;
	private List<PaletteColor> primary;
	private List<PaletteColor> secondary;
	private List<PaletteColor> tertiary;
	
	public Palette(FileHandler handler, PaletteInfo info, int paletteSize) {
		this.info = info;
		this.paletteSize = paletteSize;
		
		rawData = handler.readBytesAtOffset(info.paletteOffset, paletteSize);
		
		hair = new ArrayList<PaletteColor>();
		for (int offset : info.hairColorOffsets) {
			hair.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 1)));
		}
		
		primary = new ArrayList<PaletteColor>();
		for (int offset : info.primaryColorOffsets) {
			primary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 1)));
		}
		
		secondary = new ArrayList<PaletteColor>();
		for (int offset : info.secondaryColorOffsets) {
			secondary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 1)));
		}
		
		tertiary = new ArrayList<PaletteColor>();
		for (int offset : info.tertiaryColorOffsets) {
			tertiary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 1)));
		}
	}
	
	public PaletteColor[] getHairColors() {
		return hair.toArray(new PaletteColor[hair.size()]);
	}
	
	public PaletteColor[] getPrimaryColors() {
		return primary.toArray(new PaletteColor[primary.size()]);
	}
	
	public PaletteColor[] getSecondaryColors() {
		return secondary.toArray(new PaletteColor[secondary.size()]);
	}
	
	public PaletteColor[] getTertiaryColors() {
		return tertiary.toArray(new PaletteColor[tertiary.size()]);
	}
	
	public void setHairColors(PaletteColor[] newHairColors) {
		setColorsToArea(newHairColors, hair);
	}
	
	public void setPrimaryColors(PaletteColor[] newPrimaryColors) {
		setColorsToArea(newPrimaryColors, primary);
	}
	
	public void setSecondaryColors(PaletteColor[] newSecondaryColors) {
		setColorsToArea(newSecondaryColors, secondary);
	}
	
	public void setTertiaryColors(PaletteColor[] newTertiaryColors) {
		setColorsToArea(newTertiaryColors, tertiary);
	}

	private void setColorsToArea(PaletteColor[] newColors, List<PaletteColor> targetArea) {
		if (targetArea.size() == 0) { return; }
		
		PaletteColor originalAverage = PaletteColor.averageColorFromColors(targetArea.toArray(new PaletteColor[targetArea.size()]));
		PaletteColor newAverage = PaletteColor.averageColorFromColors(newColors);
		
		List<PaletteColor> newList = new ArrayList<PaletteColor>();
		for (PaletteColor oldColor : targetArea) {
			double hueDelta = oldColor.getHue() - originalAverage.getHue();
			double saturationDelta = oldColor.getSaturation() - originalAverage.getSaturation();
			
			double newHue = hueDelta + newAverage.getHue();
			double newSaturation = saturationDelta + newAverage.getSaturation();
			
			newList.add(new PaletteColor(newHue, newSaturation, oldColor.getBrightness()));
		}
	}
}
