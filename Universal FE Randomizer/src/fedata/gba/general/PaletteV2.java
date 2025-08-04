package fedata.gba.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.FileHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.LZ77;
import util.WhyDoesJavaNotHaveThese;

public class PaletteV2 {
	
	public enum PaletteType {
		PLAYER, ENEMY, NPC, OTHER, LINK
	}
	
	private static class ColorSet {
		private PaletteColor playerColor;
		private PaletteColor enemyColor;
		private PaletteColor npcColor;
		private PaletteColor otherColor;
		private PaletteColor linkColor; // Not always used.
		
		private ColorSet(byte[] decompressedData, int colorIndex) {
			assert decompressedData.length >= 0x80 : "Insufficient data for palette color set.";
			
			int byteIndex = colorIndex * 2;
			
			playerColor = new PaletteColor(new byte[] {decompressedData[byteIndex], decompressedData[byteIndex + 1]});
			byteIndex += 0x20;
			enemyColor = new PaletteColor(new byte[] {decompressedData[byteIndex], decompressedData[byteIndex + 1]});
			byteIndex += 0x20;
			npcColor = new PaletteColor(new byte[] {decompressedData[byteIndex], decompressedData[byteIndex + 1]});
			byteIndex += 0x20;
			otherColor = new PaletteColor(new byte[] {decompressedData[byteIndex], decompressedData[byteIndex + 1]});
			
			if (decompressedData.length == 0xA0) {
				// Link color is not available.
				byteIndex += 0x20;
				linkColor = new PaletteColor(new byte[] {decompressedData[byteIndex], decompressedData[byteIndex + 1]});
			}
		}
		
		private PaletteColor getColor(PaletteType type) {
			switch (type) {
			case PLAYER: return playerColor;
			case ENEMY: return enemyColor;
			case NPC: return npcColor;
			case OTHER: return otherColor;
			case LINK: return linkColor;
			}
			
			return null;
		}
		
		private void setColor(PaletteColor newColor, PaletteType type) {
			switch (type) {
			case PLAYER: playerColor = newColor; break;
			case ENEMY: enemyColor = newColor; break;
			case NPC: npcColor = newColor; break;
			case OTHER: otherColor = newColor; break;
			case LINK: if (linkColor != null) { linkColor = newColor; } break;
			}
		}
	}
	
	private byte[] decompressedData;
	
	private ColorSet[] colorArray;
	
	private PaletteInfo info;
	private long destinationOffset;
	private int originalCompressedLength;
	
	private int identifier;
	
	public PaletteV2(FileHandler handler, PaletteInfo info) {
		decompressedData = LZ77.decompress(handler, info.getOffset());
		colorArray = new ColorSet[16];
		for (int i = 0; i < 16; i++) {
			colorArray[i] = new ColorSet(decompressedData, i);
		}
		this.info = info;
		destinationOffset = info.getOffset();
		originalCompressedLength = LZ77.compressedLength(handler, info.getOffset());
		identifier = info.paletteID;
	}
	
	public PaletteV2(PaletteV2 other) {
		decompressedData = Arrays.copyOf(other.decompressedData, other.decompressedData.length);
		colorArray = new ColorSet[16];
		for (int i = 0; i < 16; i++) {
			colorArray[i] = new ColorSet(decompressedData, i);
		}
		this.info = other.info;
		destinationOffset = other.destinationOffset;
		originalCompressedLength = other.originalCompressedLength;
		identifier = other.identifier;
	}
	
	public int getNumColors() {
		return 16;
	}
	
	public PaletteColor colorAtIndex(int index, PaletteType type) {
		return colorArray[index].getColor(type);
	}
	
	public int getClassID() {
		return info.classID;
	}
	
	public void overrideOffset(long newOffset) {
		destinationOffset = newOffset;
	}
	
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	
	public byte[] getCompressedData() {
		return LZ77.compress(decompressedData);
	}
	
	public long getDestinationOffset() {
		return destinationOffset;
	}
	
	public int getOriginalCompressedLength() {
		return originalCompressedLength;
	}
	
	public boolean hasHair() { return info.hairColorOffsets.length > 0; }
	public boolean hasPrimary() { return info.primaryColorOffsets.length > 0; }
	public boolean hasSecondary() { return info.secondaryColorOffsets.length > 0; }
	public boolean hasTertiary() { return info.tertiaryColorOffsets.length > 0; }
	
	public boolean hasLinkColors() { return colorArray[0].linkColor != null; }
	
	public PaletteColor[] getHairColors(PaletteType paletteType) {
		if (!hasHair()) { return new PaletteColor[] {}; }
		if (paletteType == PaletteType.LINK && !hasLinkColors()) { return new PaletteColor[] {}; }
		
		PaletteColor[] result = new PaletteColor[info.hairColorOffsets.length];
		int resultIndex = 0;
		for (int colorIndex : info.hairColorOffsets) {
			result[resultIndex++] = colorArray[colorIndex].getColor(paletteType);
		}
		
		return result;
	}
	
	public PaletteColor[] getPrimaryColors(PaletteType paletteType) {
		if (!hasPrimary()) { return new PaletteColor[] {}; }
		if (paletteType == PaletteType.LINK && !hasLinkColors()) { return new PaletteColor[] {}; }
		
		PaletteColor[] result = new PaletteColor[info.primaryColorOffsets.length];
		int resultIndex = 0;
		for (int colorIndex : info.primaryColorOffsets) {
			result[resultIndex++] = colorArray[colorIndex].getColor(paletteType);
		}
		
		return result;
	}
	
	public PaletteColor[] getSecondaryColors(PaletteType paletteType) {
		if (!hasSecondary()) { return new PaletteColor[] {}; }
		if (paletteType == PaletteType.LINK && !hasLinkColors()) { return new PaletteColor[] {}; }
		
		PaletteColor[] result = new PaletteColor[info.secondaryColorOffsets.length];
		int resultIndex = 0;
		for (int colorIndex : info.secondaryColorOffsets) {
			result[resultIndex++] = colorArray[colorIndex].getColor(paletteType);
		}
		
		return result;
	}
	
	public PaletteColor[] getTertiaryColors(PaletteType paletteType) {
		if (!hasTertiary()) { return new PaletteColor[] {}; }
		if (paletteType == PaletteType.LINK && !hasLinkColors()) { return new PaletteColor[] {}; }
		
		PaletteColor[] result = new PaletteColor[info.tertiaryColorOffsets.length];
		int resultIndex = 0;
		for (int colorIndex : info.tertiaryColorOffsets) {
			result[resultIndex++] = colorArray[colorIndex].getColor(paletteType);
		}
		
		return result;
	}
	
	public void setPalette(List<PaletteColor> hair, List<PaletteColor> primary, List<PaletteColor> secondary, List<PaletteColor> tertiary, PaletteType paletteType) {
		setHair(hair, paletteType);
		setPrimary(primary, paletteType);
		setSecondary(secondary != null && !secondary.isEmpty() ? secondary : PaletteColor.adjustColors(primary, false, false), paletteType);
		setTertiary(tertiary != null && !tertiary.isEmpty() ? tertiary : PaletteColor.adjustColors(primary, false, false), paletteType);
	}
	
	public void adaptPalette(PaletteV2[] referencePalettes, PaletteType paletteType, PaletteColor[] supplementalHairColors) {
		List<PaletteColor> referenceHair = new ArrayList<PaletteColor>();
		List<PaletteColor> referencePrimary = new ArrayList<PaletteColor>();
		List<PaletteColor> referenceSecondary = new ArrayList<PaletteColor>();
		List<PaletteColor> referenceTertiary = new ArrayList<PaletteColor>();
		
		for (PaletteV2 palette : referencePalettes) {
			// Only pull colors if we don't already have any colors OR if the current palette has more colors to work with for a specific area.
			if (palette.hasHair() && (referenceHair.isEmpty() || palette.getHairColors(paletteType).length > referenceHair.size())) {
				referenceHair.clear();
				referenceHair.addAll(Arrays.asList(palette.getHairColors(paletteType)));
			}
			if (palette.hasPrimary() && (referencePrimary.isEmpty() || palette.getPrimaryColors(paletteType).length > referencePrimary.size())) {
				referencePrimary.clear();
				referencePrimary.addAll(Arrays.asList(palette.getPrimaryColors(paletteType)));
			}
			if (palette.hasSecondary() && (referenceSecondary.isEmpty() || palette.getSecondaryColors(paletteType).length > referenceSecondary.size())) {
				referenceSecondary.clear();
				referenceSecondary.addAll(Arrays.asList(palette.getSecondaryColors(paletteType)));
			}
			if (palette.hasTertiary() && (referenceTertiary.isEmpty() || palette.getTertiaryColors(paletteType).length > referenceTertiary.size())) {
				referenceTertiary.clear();
				referenceTertiary.addAll(Arrays.asList(palette.getTertiaryColors(paletteType)));
			}
		}
		
		if (supplementalHairColors != null && supplementalHairColors.length > 0) {
			referenceHair.clear();
			referenceHair.addAll(Arrays.asList(supplementalHairColors));
		}
		
		if (referenceHair.isEmpty() && hasHair()) {
			// If there's no hair info and we need hair info, reuse primary color.
			referenceHair.addAll(PaletteColor.adjustColors(referencePrimary, true, true));
		}
		
		// Primary usually exists.
		
		boolean filledSecondary = false;
		if (referenceSecondary.isEmpty() && hasSecondary()) {
			// Use primary color for secondary if we don't have secondary colors and need one.
			referenceSecondary.addAll(PaletteColor.adjustColors(referencePrimary, false, false));
			filledSecondary = true;
		}
		
		if (referenceTertiary.isEmpty() && hasTertiary()) {
			// Use primary again if tertiary is required.
			if (filledSecondary == true) {
			referenceTertiary.addAll(PaletteColor.adjustColors(referencePrimary, false, true));
			}
			else { referenceTertiary.addAll(PaletteColor.adjustColors(referenceSecondary, false, true)); }
		}
		
		setHair(referenceHair, paletteType);
		setPrimary(referencePrimary, paletteType);
		setSecondary(referenceSecondary, paletteType);
		setTertiary(referenceTertiary, paletteType);
	}
	
	public void forceCommit(DiffCompiler compiler) {
		applyColorsToData();
		
		byte[] compressed = LZ77.compress(decompressedData);
		
		compiler.addDiff(new Diff(getDestinationOffset(), compressed.length, compressed, null));
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "[PaletteID: 0x" + Integer.toHexString(identifier) + "] Wrote " + Integer.toString(compressed.length) + " bytes to address 0x" + Long.toHexString(getDestinationOffset()));
	}
	
	public void commitPalette(DiffCompiler compiler) {
		if (identifier == 0) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE, "No identifier assigned to palette. Dropping palette...");
			return;
		}
		
		applyColorsToData();
		
		byte[] compressed = LZ77.compress(decompressedData);
		
		compiler.addDiff(new Diff(getDestinationOffset(), compressed.length, compressed, null));
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE, "[PaletteID: 0x" + Integer.toHexString(identifier) + "] Wrote " + Integer.toString(compressed.length) + " bytes to address 0x" + Long.toHexString(getDestinationOffset()));
	}
	
	private void setHair(List<PaletteColor> referenceHair, PaletteType paletteType) {
		if (referenceHair.size() == 0) { return; } // No hair colors to use.
		if (!hasHair()) { return; } // This palette has no space for hair.
		
		List<PaletteColor> sortedHair = referenceHair.stream().sorted(PaletteColor.lowToHighBrightnessComparator).collect(Collectors.toList());
		Collections.reverse(sortedHair);
		PaletteColor[] newHairColor = PaletteColor.coerceColors(sortedHair.toArray(new PaletteColor[sortedHair.size()]), info.hairColorOffsets.length);
		
		int lastIndex = newHairColor.length - 1;
		if (PaletteColor.getLuminosity(newHairColor[lastIndex]) > 0.7 && lastIndex > 0) {
			PaletteColor newColor = PaletteColor.darkerColor(newHairColor[lastIndex]);
			for (int i = 0; i < lastIndex; i++) {
				newHairColor[i] = newHairColor[i+1];
			}
			newHairColor[newHairColor.length-1] = newColor;
		}
    
		int newIndex = 0;
		for (int colorIndex : info.hairColorOffsets) {
			ColorSet set = colorArray[colorIndex];
			set.setColor(newHairColor[newIndex++], paletteType);
		}
		
		applyColorsToData();
	}
	
	private void setPrimary(List<PaletteColor> referencePrimary, PaletteType paletteType) {
		if (referencePrimary.size() == 0) { return; } // No primary colors to use.
		if (!hasPrimary()) { return; } // This palette has no space for primary. (shouldn't be happening...)
		
		List<PaletteColor> sortedPrimary = referencePrimary.stream().sorted(PaletteColor.lowToHighBrightnessComparator).collect(Collectors.toList());
		Collections.reverse(sortedPrimary);
		PaletteColor[] newPrimaryColor = PaletteColor.coerceColors(sortedPrimary.toArray(new PaletteColor[sortedPrimary.size()]), info.primaryColorOffsets.length);
		
		//Some primary sets end up too bright - let's darken them a tad.
		int lastIndex = newPrimaryColor.length - 1;
		double avgLum = 0;
		for (PaletteColor color : newPrimaryColor) {
			avgLum += PaletteColor.getLuminosity(color);
		}
		avgLum /= newPrimaryColor.length;
		if (avgLum > 0.51 && lastIndex > 1) {
			PaletteColor newColor = PaletteColor.darkerColor(newPrimaryColor[lastIndex]);
			for (int i = 0; i < lastIndex; i++) {
				newPrimaryColor[i] = newPrimaryColor[i+1];
			}
			newPrimaryColor[lastIndex] = newColor;
			if (PaletteColor.getLuminosity(newPrimaryColor[lastIndex-2]) - PaletteColor.getLuminosity(newPrimaryColor[lastIndex-1]) > 0.5) {
				newPrimaryColor[lastIndex-1] = PaletteColor.averageColorFromColors(new PaletteColor[] {newPrimaryColor[lastIndex-2], newColor});
			}
		}

		int newIndex = 0;
		for (int colorIndex : info.primaryColorOffsets) {
			ColorSet set = colorArray[colorIndex];
			set.setColor(newPrimaryColor[newIndex++], paletteType);
		}
		
		applyColorsToData();
	}
	
	private void setSecondary(List<PaletteColor> referenceSecondary, PaletteType paletteType) {
		if (referenceSecondary.size() == 0) { return; } // No secondary colors to use.
		if (!hasSecondary()) { return; } // This palette has no space for secondary.
		
		List<PaletteColor> sortedSecondary = referenceSecondary.stream().sorted(PaletteColor.lowToHighBrightnessComparator).collect(Collectors.toList());
		Collections.reverse(sortedSecondary);
		PaletteColor[] newSecondaryColor = PaletteColor.coerceColors(sortedSecondary.toArray(new PaletteColor[sortedSecondary.size()]), info.secondaryColorOffsets.length);
		
		int lastIndex = newSecondaryColor.length - 1;
		double avgLum = 0;
		for (PaletteColor color : newSecondaryColor) {
			avgLum += PaletteColor.getLuminosity(color);
		}
		avgLum /= newSecondaryColor.length;
		if (avgLum > 0.55) {
			PaletteColor newColor = PaletteColor.darkerColor(newSecondaryColor[lastIndex]);
			for (int i = 0; i < lastIndex; i++) {
				newSecondaryColor[i] = newSecondaryColor[i+1];
			}
			newSecondaryColor[newSecondaryColor.length-1] = newColor;
    }
		int newIndex = 0;
		for (int colorIndex : info.secondaryColorOffsets) {
			ColorSet set = colorArray[colorIndex];
			set.setColor(newSecondaryColor[newIndex++], paletteType);
		}
		
		applyColorsToData();
	}
	
	private void setTertiary(List<PaletteColor> referenceTertiary, PaletteType paletteType) {
		if (referenceTertiary.size() == 0) { return; } // No tertiary colors to use.
		if (!hasTertiary()) { return; } // This palette has no space for tertiary. (shouldn't be happening...)
		
		List<PaletteColor> sortedTertiary = referenceTertiary.stream().sorted(PaletteColor.lowToHighBrightnessComparator).collect(Collectors.toList());
		Collections.reverse(sortedTertiary);
		PaletteColor[] newTertiaryColor = PaletteColor.coerceColors(sortedTertiary.toArray(new PaletteColor[sortedTertiary.size()]), info.tertiaryColorOffsets.length, false);
		int newIndex = 0;
		for (int colorIndex : info.tertiaryColorOffsets) {
			ColorSet set = colorArray[colorIndex];
			set.setColor(newTertiaryColor[newIndex++], paletteType);
		}
		
		applyColorsToData();
	}
	
	private void applyColorsToData() {
		for (int i = 0; i < colorArray.length; i++) {
			ColorSet color = colorArray[i];
			int offset = 0;
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(color.getColor(PaletteType.PLAYER).toColorTuple(), decompressedData, offset + i * 2, 2);
			offset += 0x20;
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(color.getColor(PaletteType.ENEMY).toColorTuple(), decompressedData, offset + i * 2, 2);
			offset += 0x20;
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(color.getColor(PaletteType.NPC).toColorTuple(), decompressedData, offset + i * 2, 2);
			offset += 0x20;
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(color.getColor(PaletteType.OTHER).toColorTuple(), decompressedData, offset + i * 2, 2);
			if (color.getColor(PaletteType.LINK) != null && decompressedData.length > 0x80) {
				offset += 0x20;
				WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(color.getColor(PaletteType.LINK).toColorTuple(), decompressedData, offset + i * 2, 2);
			}
		}
	}
}
