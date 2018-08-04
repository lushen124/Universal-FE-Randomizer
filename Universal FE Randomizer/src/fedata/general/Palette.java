package fedata.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.FileHandler;
import util.Diff;
import util.DiffCompiler;

public class Palette {
	
	PaletteInfo info;
	
	private byte[] rawData;
	
	private List<PaletteColor> hair;
	private List<PaletteColor> primary;
	private List<PaletteColor> secondary;
	private List<PaletteColor> tertiary;
	
	private Boolean hairModified = false;
	private Boolean primaryModified = false;
	private Boolean secondaryModified = false;
	private Boolean tertiaryModified = false;
	
	private Boolean fullUpdate = false;
	
	public Palette(FileHandler handler, PaletteInfo info, int paletteSize) {
		this.info = info;
		
		rawData = handler.readBytesAtOffset(info.paletteOffset, paletteSize);
		
		hair = new ArrayList<PaletteColor>();
		for (int offset : info.hairColorOffsets) {
			hair.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 2)));
		}
		
		primary = new ArrayList<PaletteColor>();
		for (int offset : info.primaryColorOffsets) {
			primary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 2)));
		}
		
		secondary = new ArrayList<PaletteColor>();
		for (int offset : info.secondaryColorOffsets) {
			secondary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 2)));
		}
		
		tertiary = new ArrayList<PaletteColor>();
		for (int offset : info.tertiaryColorOffsets) {
			tertiary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 2)));
		}
	}
	
	public Palette(Palette template, Palette originalPalette) {
		info = template.info;
		info.paletteOffset = originalPalette.info.paletteOffset;
		rawData = template.rawData;
		fullUpdate = true;
		
		hair = new ArrayList<PaletteColor>();
		for (int offset : info.hairColorOffsets) {
			hair.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 2)));
		}
		
		primary = new ArrayList<PaletteColor>();
		for (int offset : info.primaryColorOffsets) {
			primary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 2)));
		}
		
		secondary = new ArrayList<PaletteColor>();
		for (int offset : info.secondaryColorOffsets) {
			secondary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 2)));
		}
		
		tertiary = new ArrayList<PaletteColor>();
		for (int offset : info.tertiaryColorOffsets) {
			tertiary.add(new PaletteColor(Arrays.copyOfRange(rawData, offset, offset + 2)));
		}
		
		PaletteColor[] originalHairColors = originalPalette.getHairColors();
		if (originalHairColors.length == 0) {
			// Use armor color for now if hair color isn't available.
			originalHairColors = originalPalette.getPrimaryColors();
		}
		
		setHairColors(originalHairColors);
		setPrimaryColors(originalPalette.getPrimaryColors());
		
		if (originalPalette.getSecondaryColors().length > 0) {
			setSecondaryColors(originalPalette.getSecondaryColors());
		}
		if (originalPalette.getTertiaryColors().length > 0) {
			setTertiaryColors(originalPalette.getTertiaryColors());
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
		Boolean success = setColorsToArea(newHairColors, hair);
		if (success) {
			hairModified = true;
		}
	}
	
	public void setPrimaryColors(PaletteColor[] newPrimaryColors) {
		Boolean success = setColorsToArea(newPrimaryColors, primary);
		if (success) {
			primaryModified = true;
		}
	}
	
	public void setSecondaryColors(PaletteColor[] newSecondaryColors) {
		Boolean success = setColorsToArea(newSecondaryColors, secondary);
		if (success) {
			secondaryModified = true;
		}
	}
	
	public void setTertiaryColors(PaletteColor[] newTertiaryColors) {
		Boolean success = setColorsToArea(newTertiaryColors, tertiary);
		if (success) {
			tertiaryModified = true;
		}
	}
	
	public void commitPalette(DiffCompiler compiler) {
		if (fullUpdate) {
			byte[] dataToWrite = Arrays.copyOfRange(rawData, 0, rawData.length);
			for (int i = 0; i < hair.size(); i++) {
				int offset = info.hairColorOffsets[i];
				byte[] tuple = hair.get(i).toColorTuple();
				dataToWrite[offset] = tuple[0];
				dataToWrite[offset + 1] = tuple[1];
			}
			for (int i = 0; i < primary.size(); i++) {
				int offset = info.primaryColorOffsets[i];
				byte[] tuple = primary.get(i).toColorTuple();
				dataToWrite[offset] = tuple[0];
				dataToWrite[offset + 1] = tuple[1];
			}
			for (int i = 0; i < secondary.size(); i++) {
				int offset = info.secondaryColorOffsets[i];
				byte[] tuple = secondary.get(i).toColorTuple();
				dataToWrite[offset] = tuple[0];
				dataToWrite[offset + 1] = tuple[1];
			}
			for (int i = 0; i < tertiary.size(); i++) {
				int offset = info.tertiaryColorOffsets[i];
				byte[] tuple = tertiary.get(i).toColorTuple();
				dataToWrite[offset] = tuple[0];
				dataToWrite[offset + 1] = tuple[1];
			}
			
			compiler.addDiff(new Diff(info.paletteOffset, dataToWrite.length, dataToWrite, null));
		} else {
			if (hairModified) {
				for (int i = 0; i < hair.size(); i++) {
					compiler.addDiff(new Diff(info.paletteOffset + info.hairColorOffsets[i], 2, hair.get(i).toColorTuple(), null));
				}
			}
			if (primaryModified) {
				for (int i = 0; i < primary.size(); i++) {
					compiler.addDiff(new Diff(info.paletteOffset + info.primaryColorOffsets[i], 2, primary.get(i).toColorTuple(), null));
				}
			}
			if (secondaryModified) {
				for (int i = 0; i < secondary.size(); i++) {
					compiler.addDiff(new Diff(info.paletteOffset + info.secondaryColorOffsets[i], 2, secondary.get(i).toColorTuple(), null));
				}
			}
			if (tertiaryModified) {
				for (int i = 0; i < tertiary.size(); i++) {
					compiler.addDiff(new Diff(info.paletteOffset + info.tertiaryColorOffsets[i], 2, tertiary.get(i).toColorTuple(), null));
				}
			}
		}
	}

	private Boolean setColorsToArea(PaletteColor[] newColors, List<PaletteColor> targetArea) {
		if (targetArea.size() == 0) { return false; }
		
		PaletteColor originalAverage = PaletteColor.averageColorFromColors(targetArea.toArray(new PaletteColor[targetArea.size()]));
		PaletteColor newAverage = PaletteColor.averageColorFromColors(newColors);
		
		List<PaletteColor> newList = new ArrayList<PaletteColor>();
		for (PaletteColor oldColor : targetArea) {
			double hueDelta = oldColor.getHue() - originalAverage.getHue();
			double saturationDelta = oldColor.getSaturation() - originalAverage.getSaturation();
			
			double newHue = hueDelta + newAverage.getHue();
			double newSaturation = saturationDelta + newAverage.getSaturation();
			if (newSaturation < 0) { newSaturation = 0; }
			if (newSaturation > 1) { newSaturation = 1; }
			
			newList.add(new PaletteColor(newHue, newSaturation > 0 ? newSaturation : 0, oldColor.getBrightness()));
		}
		
		targetArea.clear();
		targetArea.addAll(newList);
		
		return true;
	}
}
