package fedata.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.FileHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.WhyDoesJavaNotHaveThese;

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
	
	private List<PaletteColor> supplementalHairColor = null;
	
	private Map<Integer, Integer> customMapping;
	
	private Boolean fullUpdate = false;
	
	private int indexOf(int index) {
		if (customMapping == null) {
			return index;
		}
		
		if (customMapping.containsKey(index)) {
			return customMapping.get(index);
		}
		
		return index;
	}
	
	public Palette(FileHandler handler, PaletteInfo info, int paletteSize, Map<Integer, Integer> customMap) {
		this.info = info;
		customMapping = customMap;
		
		rawData = handler.readBytesAtOffset(info.paletteOffset, paletteSize);
		
		hair = new ArrayList<PaletteColor>();
		for (int offset : info.hairColorOffsets) {
			int mappedOffset = indexOf(offset);
			hair.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		primary = new ArrayList<PaletteColor>();
		for (int offset : info.primaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			primary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		secondary = new ArrayList<PaletteColor>();
		for (int offset : info.secondaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			secondary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		tertiary = new ArrayList<PaletteColor>();
		for (int offset : info.tertiaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			tertiary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
	}
	
	public Palette(Palette template, Palette targetPalette, Palette[] allReferencePalettes, Map<Integer, Integer> customMap) {
		info = new PaletteInfo(template.info);
		info.paletteOffset = targetPalette.info.paletteOffset;
		customMapping = customMap;
		if (targetPalette.rawData.length > template.rawData.length) {
			rawData = targetPalette.rawData.clone();
			for (int i = 0; i < template.rawData.length; i++) {
				rawData[indexOf(i)] = template.rawData[i];
			}
		} else {
			rawData = template.rawData.clone();
		}
		fullUpdate = true;
		
		hair = new ArrayList<PaletteColor>();
		for (int offset : info.hairColorOffsets) {
			int mappedOffset = indexOf(offset);
			hair.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		primary = new ArrayList<PaletteColor>();
		for (int offset : info.primaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			primary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		secondary = new ArrayList<PaletteColor>();
		for (int offset : info.secondaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			secondary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		tertiary = new ArrayList<PaletteColor>();
		for (int offset : info.tertiaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			tertiary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		List<Palette> orderedPalettesBySaturation = new ArrayList<Palette>(Arrays.asList(allReferencePalettes));
		
		if (hair.size() > 0) {
			Boolean hairSet = false;
			Collections.sort(orderedPalettesBySaturation, new Comparator<Palette>() {
				@Override
				public int compare(Palette arg0, Palette arg1) {
					if (arg0.getHairColors().length > 0 && arg1.getHairColors().length > 0) {
						double arg0Saturation = arg0.getHairColors()[0].getSaturation();
						double arg1Saturation = arg1.getHairColors()[0].getSaturation();
						if (arg0Saturation > arg1Saturation) { return -1; }
						else if (arg0Saturation < arg1Saturation) { return 1; }
						else { return 0; }
					} else {
						return 0;
					}
				}
			});
			
			for (Palette otherPalette : orderedPalettesBySaturation) {
				if (otherPalette.getHairColors().length > 0) {
					setHairColors(PaletteColor.coerceColors(otherPalette.getHairColors(), hair.size()));
					hairSet = true;
					break;
				} else if (otherPalette.supplementalHairColor != null && !otherPalette.supplementalHairColor.isEmpty()) {
					List<PaletteColor> supplementalList = otherPalette.supplementalHairColor;
					setHairColors(PaletteColor.coerceColors(supplementalList.toArray(new PaletteColor[supplementalList.size()]), hair.size()));
					hairSet = true;
					break;
				}
			}
			
			if (!hairSet) {
				Collections.sort(orderedPalettesBySaturation, new Comparator<Palette>() {
					@Override
					public int compare(Palette arg0, Palette arg1) {
						if (arg0.getPrimaryColors().length > 0 && arg1.getPrimaryColors().length > 0) {
							double arg0Saturation = arg0.getPrimaryColors()[0].getSaturation();
							double arg1Saturation = arg1.getPrimaryColors()[0].getSaturation();
							if (arg0Saturation > arg1Saturation) { return -1; }
							else if (arg0Saturation < arg1Saturation) { return 1; }
							else { return 0; }
						} else {
							return 0;
						}
					}
				});
				for (Palette otherPalette : orderedPalettesBySaturation) {
					if (otherPalette.getPrimaryColors().length > 0) {
						setHairColors(PaletteColor.coerceColors(otherPalette.getPrimaryColors(), primary.size()));
						break;
					}
				}
			}
		}
		
		if (primary.size() > 0) {
			Collections.sort(orderedPalettesBySaturation, new Comparator<Palette>() {
				@Override
				public int compare(Palette arg0, Palette arg1) {
					if (arg0.getPrimaryColors().length > 0 && arg1.getPrimaryColors().length > 0) {
						double arg0Saturation = arg0.getPrimaryColors()[0].getSaturation();
						double arg1Saturation = arg1.getPrimaryColors()[0].getSaturation();
						if (arg0Saturation > arg1Saturation) { return -1; }
						else if (arg0Saturation < arg1Saturation) { return 1; }
						else { return 0; }
					} else {
						return 0;
					}
				}
			});
			for (Palette otherPalette : orderedPalettesBySaturation) {
				if (otherPalette.getPrimaryColors().length > 0) {
					setPrimaryColors(PaletteColor.coerceColors(otherPalette.getPrimaryColors(), primary.size()));
					break;
				}
			}
		}
		
		if (secondary.size() > 0) {
			Collections.sort(orderedPalettesBySaturation, new Comparator<Palette>() {
				@Override
				public int compare(Palette arg0, Palette arg1) {
					if (arg0.getSecondaryColors().length > 0 && arg1.getSecondaryColors().length > 0) {
						double arg0Saturation = arg0.getSecondaryColors()[0].getSaturation();
						double arg1Saturation = arg1.getSecondaryColors()[0].getSaturation();
						if (arg0Saturation > arg1Saturation) { return -1; }
						else if (arg0Saturation < arg1Saturation) { return 1; }
						else { return 0; }
					} else {
						return 0;
					}
				}
			});
			for (Palette otherPalette : orderedPalettesBySaturation) {
				if (otherPalette.getSecondaryColors().length > 0) {
					setSecondaryColors(PaletteColor.coerceColors(otherPalette.getSecondaryColors(), secondary.size()));
					break;
				}
			}
		}
		
		if (tertiary.size() > 0) {
			Collections.sort(orderedPalettesBySaturation, new Comparator<Palette>() {
				@Override
				public int compare(Palette arg0, Palette arg1) {
					if (arg0.getTertiaryColors().length > 0 && arg1.getTertiaryColors().length > 0) {
						double arg0Saturation = arg0.getTertiaryColors()[0].getSaturation();
						double arg1Saturation = arg1.getTertiaryColors()[0].getSaturation();
						if (arg0Saturation > arg1Saturation) { return -1; }
						else if (arg0Saturation < arg1Saturation) { return 1; }
						else { return 0; }
					} else {
						return 0;
					}
				}
			});
			for (Palette otherPalette : orderedPalettesBySaturation) {
				if (otherPalette.getTertiaryColors().length > 0) {
					setTertiaryColors(PaletteColor.coerceColors(otherPalette.getTertiaryColors(), tertiary.size()));
					break;
				}
			}
		}
	}
	
	public Palette(Palette template, Palette originalPalette, Palette alternatePalette, Map<Integer, Integer> customMap) {
		info = new PaletteInfo(template.info);
		info.paletteOffset = originalPalette.info.paletteOffset;
		customMapping = customMap;
		
		if (originalPalette.rawData.length > template.rawData.length) {
			rawData = originalPalette.rawData.clone();
			for (int i = 0; i < template.rawData.length; i++) {
				rawData[indexOf(i)] = template.rawData[i];
			}
		} else {
			rawData = template.rawData.clone();
		}
		fullUpdate = true;
		
		hair = new ArrayList<PaletteColor>();
		for (int offset : info.hairColorOffsets) {
			int mappedOffset = indexOf(offset);
			hair.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		primary = new ArrayList<PaletteColor>();
		for (int offset : info.primaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			primary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		secondary = new ArrayList<PaletteColor>();
		for (int offset : info.secondaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			secondary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		tertiary = new ArrayList<PaletteColor>();
		for (int offset : info.tertiaryColorOffsets) {
			int mappedOffset = indexOf(offset);
			tertiary.add(new PaletteColor(Arrays.copyOfRange(rawData, mappedOffset, mappedOffset + 2)));
		}
		
		PaletteColor[] originalHairColors = originalPalette.getHairColors();
		if (originalHairColors.length == 0) {
			if (alternatePalette != null) {
				originalHairColors = alternatePalette.getHairColors();
			}
			
			if (originalHairColors.length == 0 && originalPalette.supplementalHairColor != null) {
				originalHairColors = originalPalette.supplementalHairColor.toArray(new PaletteColor[originalPalette.supplementalHairColor.size()]);
			}
			
			if (originalHairColors.length == 0 && alternatePalette != null && alternatePalette.supplementalHairColor != null) {
				originalHairColors = alternatePalette.supplementalHairColor.toArray(new PaletteColor[alternatePalette.supplementalHairColor.size()]);
			}
			
			if (originalHairColors.length == 0) {
				// Use armor color for now if hair color isn't available.
				originalHairColors = originalPalette.getPrimaryColors();
			}
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
	
	public PaletteInfo getInfo() {
		return info;
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
	
	public void assignSupplementalHairColor(PaletteColor[] hairColors) {
		supplementalHairColor = new ArrayList<PaletteColor>();
		for (int i = 0; i < hairColors.length; i++) {
			supplementalHairColor.add(hairColors[i]);
		}
	}
	
	public void setHairColors(PaletteColor[] newHairColors) {
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Replacing old hair colors");
		for(int i = 0; i < hair.size(); i++) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + hair.get(i).toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + hair.get(i).toHSBString());
		}
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "With new colors");
		for (int i = 0; i < newHairColors.length; i++) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + newHairColors[i].toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + newHairColors[i].toHSBString());
		}
		Boolean success = setColorsToArea(newHairColors, hair);
		if (success) {
			hairModified = true;
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Successfully wrote hair colors");
			for(int i = 0; i < hair.size(); i++) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + hair.get(i).toRGBString());
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + hair.get(i).toHSBString());
			}
		} else {
			System.err.println("Failed to write hair colors.");
		}
	}
	
	public void setPrimaryColors(PaletteColor[] newPrimaryColors) {
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Replacing old primary colors");
		for(int i = 0; i < primary.size(); i++) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + primary.get(i).toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + primary.get(i).toHSBString());
		}
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "With new colors");
		for (int i = 0; i < newPrimaryColors.length; i++) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + newPrimaryColors[i].toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + newPrimaryColors[i].toHSBString());
		}
		Boolean success = setColorsToArea(newPrimaryColors, primary);
		if (success) {
			primaryModified = true;
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Successfully wrote primary colors");
			for(int i = 0; i < primary.size(); i++) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + primary.get(i).toRGBString());
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + primary.get(i).toHSBString());
			}
		} else {
			System.err.println("Failed to write primary colors.");
		}
	}
	
	public void setSecondaryColors(PaletteColor[] newSecondaryColors) {
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Replacing old secondary colors");
		for(int i = 0; i < secondary.size(); i++) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + secondary.get(i).toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + secondary.get(i).toHSBString());
		}
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "With new colors");
		for (int i = 0; i < newSecondaryColors.length; i++) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + newSecondaryColors[i].toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + newSecondaryColors[i].toHSBString());
		}
		
		Boolean success = setColorsToArea(newSecondaryColors, secondary);
		if (success) {
			secondaryModified = true;
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Successfully wrote secondary colors");
			for(int i = 0; i < secondary.size(); i++) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + secondary.get(i).toRGBString());
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + secondary.get(i).toHSBString());
			}
		} else {
			System.err.println("Failed to write secondary colors.");
		}
	}
	
	public void setTertiaryColors(PaletteColor[] newTertiaryColors) {
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Replacing old tertiary colors");
		for(int i = 0; i < tertiary.size(); i++) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + tertiary.get(i).toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + tertiary.get(i).toHSBString());
		}
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "With new colors");
		for (int i = 0; i < newTertiaryColors.length; i++) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + newTertiaryColors[i].toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + newTertiaryColors[i].toHSBString());
		}
		
		Boolean success = setColorsToArea(newTertiaryColors, tertiary);
		if (success) {
			tertiaryModified = true;
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Successfully wrote tertiary colors");
			for(int i = 0; i < tertiary.size(); i++) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t" + tertiary.get(i).toRGBString());
				DebugPrinter.log(DebugPrinter.Key.PALETTE, "\t\t" + tertiary.get(i).toHSBString());
			}
		} else {
			System.err.print("Failed to write tertiary colors.");
		}
	}
	
	public void commitPalette(DiffCompiler compiler) {
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "Comitting palette at offset 0x" + Long.toHexString(info.paletteOffset));
		if (fullUpdate) {
			byte[] dataToWrite = Arrays.copyOfRange(rawData, 0, rawData.length);
			for (int i = 0; i < hair.size(); i++) {
				int offset = info.hairColorOffsets[i];
				int mappedOffset = indexOf(offset);
				byte[] tuple = hair.get(i).toColorTuple();
				dataToWrite[mappedOffset] = tuple[0];
				dataToWrite[mappedOffset + 1] = tuple[1];
			}
			for (int i = 0; i < primary.size(); i++) {
				int offset = info.primaryColorOffsets[i];
				int mappedOffset = indexOf(offset);
				byte[] tuple = primary.get(i).toColorTuple();
				dataToWrite[mappedOffset] = tuple[0];
				dataToWrite[mappedOffset + 1] = tuple[1];
			}
			for (int i = 0; i < secondary.size(); i++) {
				int offset = info.secondaryColorOffsets[i];
				int mappedOffset = indexOf(offset);
				byte[] tuple = secondary.get(i).toColorTuple();
				dataToWrite[mappedOffset] = tuple[0];
				dataToWrite[mappedOffset + 1] = tuple[1];
			}
			for (int i = 0; i < tertiary.size(); i++) {
				int offset = info.tertiaryColorOffsets[i];
				int mappedOffset = indexOf(offset);
				byte[] tuple = tertiary.get(i).toColorTuple();
				dataToWrite[mappedOffset] = tuple[0];
				dataToWrite[mappedOffset + 1] = tuple[1];
			}
			
			compiler.addDiff(new Diff(info.paletteOffset, dataToWrite.length, dataToWrite, null));
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Data: " + WhyDoesJavaNotHaveThese.displayStringForBytes(dataToWrite));
		} else {
			if (hairModified) {
				for (int i = 0; i < hair.size(); i++) {
					int mappedOffset = indexOf(info.hairColorOffsets[i]);
					compiler.addDiff(new Diff(info.paletteOffset + mappedOffset, 2, hair.get(i).toColorTuple(), null));
				}
			}
			if (primaryModified) {
				for (int i = 0; i < primary.size(); i++) {
					int mappedOffset = indexOf(info.primaryColorOffsets[i]);
					compiler.addDiff(new Diff(info.paletteOffset + mappedOffset, 2, primary.get(i).toColorTuple(), null));
				}
			}
			if (secondaryModified) {
				for (int i = 0; i < secondary.size(); i++) {
					int mappedOffset = indexOf(info.secondaryColorOffsets[i]);
					compiler.addDiff(new Diff(info.paletteOffset + mappedOffset, 2, secondary.get(i).toColorTuple(), null));
				}
			}
			if (tertiaryModified) {
				for (int i = 0; i < tertiary.size(); i++) {
					int mappedOffset = indexOf(info.tertiaryColorOffsets[i]);
					compiler.addDiff(new Diff(info.paletteOffset + mappedOffset, 2, tertiary.get(i).toColorTuple(), null));
				}
			}
		}
	}

	private Boolean setColorsToArea(PaletteColor[] newColors, List<PaletteColor> targetArea) {
		if (targetArea.size() == 0) { return false; }
		
		if (targetArea.size() == newColors.length) {
			targetArea.clear();
			targetArea.addAll(Arrays.asList(newColors));
			return true;
		}
		
		if (targetArea.size() < newColors.length) {
			int targetSize = targetArea.size();
			targetArea.clear();
			for (int i = 0; i < targetSize; i++) {
				targetArea.add(newColors[i]);
			}
			return true;
		}
		
		if (newColors.length > 1 && targetArea.size() == newColors.length + 1) {
			// We only need one more color, just use the average color in the middle.
			int midIndex = (newColors.length / 2) - (newColors.length % 2 == 0 ? 1 : 0);
			PaletteColor midColor = PaletteColor.averageColorFromColors(new PaletteColor[] {newColors[midIndex], newColors[midIndex + 1]});
			
			targetArea.clear();
			targetArea.addAll(Arrays.asList(newColors));
			targetArea.add(midIndex + 1, midColor);
			return true;
		}
		
		if (newColors.length > 1 && targetArea.size() == newColors.length + 2) {
			// We need two more colors.
			if (newColors.length == 2) {
				PaletteColor midColor = PaletteColor.averageColorFromColors(new PaletteColor[] {newColors[0], newColors[1]});
				
				PaletteColor lowMid = PaletteColor.averageColorFromColors(new PaletteColor[] {newColors[0], midColor});
				PaletteColor highMid = PaletteColor.averageColorFromColors(new PaletteColor[] {midColor, newColors[1]});
				
				targetArea.clear();
				targetArea.addAll(Arrays.asList(newColors[0], lowMid, highMid, newColors[1]));
				return true;
			} else {
				int midIndex = (newColors.length / 2) - (newColors.length % 2 == 0 ? 1 : 0);
				
				int lowMidIndex = midIndex - 1;
				int highMidIndex = midIndex + 1;
				
				PaletteColor lowMid = PaletteColor.averageColorFromColors(new PaletteColor[] {newColors[lowMidIndex], newColors[midIndex]});
				PaletteColor highMid = PaletteColor.averageColorFromColors(new PaletteColor[] {newColors[midIndex], newColors[highMidIndex]});
				
				targetArea.clear();
				targetArea.addAll(Arrays.asList(newColors));
				targetArea.add(highMidIndex, highMid);
				targetArea.add(lowMidIndex, lowMid);
				return true;
			}
		}
		
		PaletteColor originalAverage = PaletteColor.averageColorFromColors(targetArea.toArray(new PaletteColor[targetArea.size()]));
		PaletteColor newAverage = PaletteColor.averageColorFromColors(newColors);
		
		List<PaletteColor> newList = new ArrayList<PaletteColor>();
		for (PaletteColor oldColor : targetArea) {
			double oldHue = oldColor.getHue();
			double averageHue = originalAverage.getHue();
			
			// We need to normalize the hues back to between 0 and 1.
			while (oldHue > 1) { oldHue = oldHue - 1; }
			while (averageHue > 1) { averageHue = averageHue - 1; }
			double hueDelta = oldHue - averageHue;
			
			if (Math.abs(hueDelta) > 0.5) { // We need to deal with the cyclic nature of hue. 0.1 and 0.9 are supposed to be 0.2 apart, not 0.8.
				if (Math.min(oldHue, averageHue) == oldHue) {
					hueDelta = (oldHue + 1) - averageHue;
				} else {
					hueDelta = oldHue - (averageHue + 1);
				}
			}
			double saturationDelta = oldColor.getSaturation() - originalAverage.getSaturation();
			
			double newHue = hueDelta + newAverage.getHue();
			double newSaturation = saturationDelta + newAverage.getSaturation();
			if (newSaturation < 0) { newSaturation = 0; }
			if (newSaturation > 1) { newSaturation = 1; }
			
			if (newHue < 0) { newHue = newHue + 1; }
			
			PaletteColor newColor = new PaletteColor(newHue, newSaturation > 0 ? newSaturation : 0, oldColor.getBrightness());
			newList.add(newColor);
			
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "Replacing Color " + oldColor.toRGBString() + " with new color " + newColor.toRGBString());
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "In HSB: Color " + oldColor.toHSBString() + " with new color " + newColor.toHSBString());
		}
		
		targetArea.clear();
		targetArea.addAll(newList);
		
		return true;
	}
}
