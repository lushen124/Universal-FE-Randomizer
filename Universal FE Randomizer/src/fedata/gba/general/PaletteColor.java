package fedata.gba.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fedata.gba.fe7.FE7Data;
import util.WhyDoesJavaNotHaveThese;

public class PaletteColor implements Comparable<PaletteColor> {
	private double red;
	private double green;
	private double blue;
	
	private double hue;
	private double saturation;
	private double brightness;
	private boolean backgroundColor;
	
	public static final Comparator<PaletteColor> lowToHighBrightnessComparator = new Comparator<PaletteColor>() {
		@Override
		public int compare(PaletteColor o1, PaletteColor o2) {
			return o1.brightness > o2.brightness ? WhyDoesJavaNotHaveThese.ComparatorResult.FIRST_GREATER.returnValue() : WhyDoesJavaNotHaveThese.ComparatorResult.SECOND_GREATER.returnValue();
		}
	};
	public static final Comparator<PaletteColor> backgroundColorComparator = new Comparator<PaletteColor>() {
		@Override
		public int compare(PaletteColor o1, PaletteColor o2) {
			return Boolean.compare(o1.isBackgroundColor(), o2.isBackgroundColor()) * -1;
		}
	};

	public PaletteColor(byte[] colorTuple) {
		int colorValue = ((colorTuple[1] << 8) & 0xFF00) | (colorTuple[0] & 0xFF);
		
		int redComponent = ((colorValue & 0x1F)) & 0xFF;
		int greenComponent = ((colorValue & 0x3E0) >> 5) & 0xFF;
		int blueComponent = ((colorValue & 0x7C00) >> 10) & 0xFF;
		
		int redValue = redComponent * 8;
		int greenValue = greenComponent * 8;
		int blueValue = blueComponent * 8;
		
		red = redValue / 255.0;
		green = greenValue / 255.0;
		blue = blueValue / 255.0;
		
		calculateValuesWithRGB();
	}
	
	public PaletteColor(int colorValue) {
		this((colorValue & 0xFF0000) >> 16, (colorValue & 0xFF00) >> 8, (colorValue & 0xFF));
	}
	public PaletteColor(int r, int g, int b) {
		red = (double)WhyDoesJavaNotHaveThese.clamp(r, 0, 255) / 255.0;
		green = (double)WhyDoesJavaNotHaveThese.clamp(g, 0, 255) / 255.0;
		blue = (double)WhyDoesJavaNotHaveThese.clamp(b, 0, 255) / 255.0;
		
		calculateValuesWithRGB();
	}
	
	public PaletteColor(double h, double s, double b) {
		while (h > 1) { h = h - 1; }
		hue = h;
		saturation = s;
		brightness = b;
		
		calculateValuesWithHSB();
	}
	
	/**
	 * 4 Hex Bytes per Color <br>
	 * 1st Byte = 0-F -> GG GR <br>
	 * 2nd Byte = 0-F -> RR RR <br>
	 * 3rd Byte = 0-7 -> []B BB <br>
	 * 4th Byte = 0-F -> B B GG <br>
	 * 
	 * Binary: (5 Bits each)<br>
	 * 
	 * G = 44|111 <br>
	 * R = 1 |2222 <br>
	 * B = 333 |44 <br>
	 */
	public PaletteColor(String colorString) {
		// We get 4 Hex Numbers, put each into it's own int
		int byte1 = Integer.parseInt(colorString.substring(0, 1), 16);
		int byte2 = Integer.parseInt(colorString.substring(1, 2), 16);
		int byte3 = Integer.parseInt(colorString.substring(2, 3), 16);
		int byte4 = Integer.parseInt(colorString.substring(3, 4), 16);

		// Get the values for each color based on the example in the method comment.
		// Bits that are on the right side of the Byte can be filtered out with bitwise
		// OR
		String byte2R = Integer.toBinaryString(byte2);
		String byte4G = Integer.toBinaryString(byte4 & 3);
		String byte3B = Integer.toBinaryString(byte3 & 7);
		// Bits that are on the right side of the Byte should be shifted left
		String byte1G = Integer.toBinaryString(byte1 >>> 1);
		String byte4B = Integer.toBinaryString(byte4 >>> 2);

		String rByte = Integer.toBinaryString(byte1 & 1) + ("0000".substring(byte2R.length()) + byte2R);
		String gByte = ("00".substring(byte4G.length()) + byte4G) + ("000".substring(byte1G.length()) + byte1G);
		String bByte = ("000".substring(byte3B.length()) + byte3B) + ("00".substring(byte4B.length()) + byte4B);

		red = (double) WhyDoesJavaNotHaveThese.clamp(8 * Integer.parseInt(rByte, 2), 0, 255) / 255.0;
		green = (double) WhyDoesJavaNotHaveThese.clamp(8 * Integer.parseInt(gByte, 2), 0, 255) / 255.0;
		blue = (double) WhyDoesJavaNotHaveThese.clamp(8 * Integer.parseInt(bByte, 2), 0, 255) / 255.0;
	}
	
	public boolean isSameAsColor(PaletteColor otherColor) {
		return toHexString().equals(otherColor.toHexString());
	}

	public boolean isNoColor() {
		return this.red == 0 && this.blue == 0 && this.green == 0 && this.brightness == 0 && this.hue == 0 && this.saturation == 0;
	}
	
	public String toHexString() {
		return String.format("#%s%s%s", 
				getRedValue() < 16 ? "0" + Integer.toHexString(getRedValue()) : Integer.toHexString(getRedValue()),
				getGreenValue() < 16 ? "0" + Integer.toHexString(getGreenValue()) : Integer.toHexString(getGreenValue()),
						getBlueValue() < 16 ? "0" + Integer.toHexString(getBlueValue()) : Integer.toHexString(getBlueValue()));
	}

	/**
	 * 4 Hex Bytes per Color <br>
	 * 1st Byte = 0-F -> GG GR <br>
	 * 2nd Byte = 0-F -> RR RR <br>
	 * 3rd Byte = 0-7 -> []B BB <br>
	 * 4th Byte = 0-F -> B B GG <br>
	 * <p>
	 * Binary: (5 Bits each)<br>
	 * <p>
	 * G = 44 |111 <br>
	 * R = 1 |2222 <br>
	 * B = 333|44 <br>
	 */
	public String to4ByteHexString() {
		// Divide by 8 as in the constructor we multiply by 8, but here we only have 5 bits of precision, so we want values 0-31
		int r = (getRedValue() / 8) & 0x1F;
		int g = (getGreenValue() / 8) & 0x1F;
		int b = (getBlueValue() / 8) & 0x1F;

		//
		int b1 = ((g & 0x7) << 1 | (r & 0x10) >> 4);
		int b2 = (r & 0xF);
		int b3 = ((b & 0x1C) >> 2);
		int b4 = ((b & 0x3) << 2 | (g & 0x18) >> 3);

		String b1Hex = Integer.toHexString(b1);
		String b2Hex = Integer.toHexString(b2);
		String b3Hex = Integer.toHexString(b3);
		String b4Hex = Integer.toHexString(b4);

		String hex =  String.format("%s%s%s%s", b1Hex, b2Hex, b3Hex, b4Hex).toUpperCase();

		return hex;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PaletteColor) || obj == null)
			return false;

		return toHexString().equals(((PaletteColor)obj).toHexString());
	}

	public static PaletteColor[] coerceColors(PaletteColor[] colors, int numberOfColors) {
		if (numberOfColors == 0) { return new PaletteColor[] {}; }
		
		// Remove dupes first, if any.
		List<PaletteColor> colorsArray = new ArrayList<PaletteColor>();
		Set<String> uniqueColors = new HashSet<String>();
		for (PaletteColor color : colors) {
			if (uniqueColors.contains(color.toHexString())) { continue; }
			uniqueColors.add(color.toHexString());
			colorsArray.add(color);
		}
					
		if (colorsArray.size() == numberOfColors) { return colorsArray.toArray(new PaletteColor[colorsArray.size()]); }
		else if (colorsArray.size() > numberOfColors) {
			PaletteColor[] uniqueColorArray = colorsArray.toArray(new PaletteColor[colorsArray.size()]);
			if (colorsArray.size() == numberOfColors) { return uniqueColorArray; }
			else if (colorsArray.size() > numberOfColors) { return reduceColors(uniqueColorArray, numberOfColors); }
			else { 
				if (colorsArray.isEmpty()) { return new PaletteColor[] {}; }
				else if (colorsArray.size() == 1) {
					if (colorsArray.get(0).brightness > 0.5) { return interpolateColors(new PaletteColor[] {colorsArray.get(0), darkerColor(colorsArray.get(0))}, numberOfColors); }
					else { return interpolateColors(new PaletteColor[] {lighterColor(colorsArray.get(0)), colorsArray.get(0)}, numberOfColors); }
				} else { return interpolateColors(uniqueColorArray, numberOfColors); }
			}
		}
		else { // (colors.length < numberOfColors) 
			if (colorsArray.size() == 1) { 
				if (colorsArray.get(0).brightness > 0.5) { return interpolateColors(new PaletteColor[] {colorsArray.get(0), darkerColor(colorsArray.get(0))}, numberOfColors); }
				else { return interpolateColors(new PaletteColor[] {lighterColor(colorsArray.get(0)), colorsArray.get(0)}, numberOfColors); }
			}
			else { return interpolateColors(colorsArray.toArray(new PaletteColor[colorsArray.size()]), numberOfColors); }
		}
	}
	
	// Will slightly shift all colors to yield different colors.
	public static List<PaletteColor> adjustColors(List<PaletteColor> colors, boolean favorBrightness, boolean favorSaturation) {
		if (colors.size() == 0) { return new ArrayList<PaletteColor>(); }
		List<PaletteColor> newColors = new ArrayList<PaletteColor>();
		double averageBrightness = 0;
		double averageSaturation = 0;
		for (int i = 0; i < colors.size(); i++) {
			averageBrightness += colors.get(i).brightness;
			averageSaturation += colors.get(i).saturation;
		}
		
		averageBrightness /= colors.size();
		averageSaturation /= colors.size();
		
		double brightnessAdjust = 0;
		double saturationAdjust = 0;
		
		if (averageBrightness <= 0.3) {
			brightnessAdjust += 0.05;
		} else if (averageBrightness >= 0.7) {
			brightnessAdjust -= 0.05;
		} else {
			if (favorBrightness) {
				brightnessAdjust += 0.02;
			} else {
				brightnessAdjust -= 0.02;
			}
		}
		
		if (averageSaturation <= 0.4) {
			saturationAdjust += 0.05;
		} else if (averageSaturation >= 0.6) {
			saturationAdjust -= 0.05;
		} else {
			if (favorSaturation) {
				saturationAdjust += 0.02;
			} else {
				saturationAdjust -= 0.02;
			}
		}
		
		for (int i = 0; i < colors.size(); i++) {
			PaletteColor oldColor = colors.get(i);
			PaletteColor adjustedColor = new PaletteColor(oldColor.hue, oldColor.saturation + saturationAdjust, oldColor.brightness + brightnessAdjust);
			newColors.add(adjustedColor);
		}
		
		return newColors;
	}
	
	private static PaletteColor[] reduceColors(PaletteColor[] colors, int numberOfColors) {
		int numberOfColorsToRemove = colors.length - numberOfColors;
		int indexDelta = colors.length / (numberOfColorsToRemove + 1);
		
		int currentIndex = colors.length - 1;
		List<PaletteColor> result = new ArrayList<PaletteColor>(Arrays.asList(colors));
		for (int i = 0; i < numberOfColorsToRemove; i++) {
			currentIndex -= indexDelta;
			result.remove(currentIndex);
		}
		
		return result.toArray(new PaletteColor[result.size()]);
	}
	
	private static PaletteColor darkerColor(PaletteColor referenceColor) {
		return new PaletteColor(referenceColor.hue, Math.max(referenceColor.saturation - 0.1, 0.0), Math.max(referenceColor.brightness * 0.8, 0.0));
	}
	
	private static PaletteColor lighterColor(PaletteColor referenceColor) {
		double distanceToMax = 1.0 - referenceColor.brightness;
		return new PaletteColor(referenceColor.hue, Math.max(referenceColor.saturation + 0.1, 0.0), Math.max(referenceColor.brightness + distanceToMax * 0.2, 0.0));
	}
	
	private static PaletteColor[] interpolateColors(PaletteColor[] colors, int numberOfColors) {
		if (colors.length < 2) { return null; }
		
		List<PaletteColor> result = new ArrayList<PaletteColor>(Arrays.asList(colors));
		
		while (result.size() < numberOfColors) {
			if (result.size() % 2 == 0) {
				// Even case: interpolate the middle two colors.
				int midIndex = result.size() / 2;
				PaletteColor firstColor = result.get(midIndex - 1);
				PaletteColor secondColor = result.get(midIndex);
				PaletteColor interpolated = averageColorFromColors(new PaletteColor[] {firstColor, secondColor});
				result.add(midIndex, interpolated);
			} else {
				// Odd case: interpolate the bottom half and top half
				int bottomHalfIndex = result.size() / 4;
				PaletteColor bottomFirst = result.get(bottomHalfIndex);
				PaletteColor bottomSecond = result.get(bottomHalfIndex + 1);
				PaletteColor bottomInterpolated = averageColorFromColors(new PaletteColor[] {bottomFirst, bottomSecond});
				result.add(bottomHalfIndex + 1, bottomInterpolated);
				if (result.size() == numberOfColors) {
					break;
				}
				int topHalfIndex = result.size() * 3 / 4;
				PaletteColor topFirst = result.get(topHalfIndex - 1);
				PaletteColor topSecond = result.get(topHalfIndex);
				PaletteColor topInterpolated = averageColorFromColors(new PaletteColor[] {topFirst, topSecond});
				result.add(topHalfIndex, topInterpolated);
			}
		}
		
		return result.toArray(new PaletteColor[result.size()]);
	}
	
	public static PaletteColor averageColorFromColors(PaletteColor[] otherColors) {
		double minHue = 1;
		double maxHue = 0;
		
		List<PaletteColor> list = new ArrayList<PaletteColor>();
		
		for (PaletteColor color : otherColors) {
			if (color.hue < minHue) {
				minHue = color.hue;
			}
			if (color.hue > maxHue) {
				maxHue = color.hue;
			}
			
			list.add(color);
		}
		
		list.sort(new Comparator<PaletteColor>() {
			public int compare(PaletteColor lhs, PaletteColor rhs) {
				return lhs.hue > rhs.hue ? 1 : (lhs.hue < rhs.hue ? -1 : 0);
			}
		});
		
		while (maxHue - minHue > 0.5) {
			PaletteColor lowestHue = list.remove(0);
			minHue = list.get(0).hue;
			lowestHue.setHue(lowestHue.hue + 1);
			list.add(lowestHue);
			maxHue = lowestHue.hue;
		}
		
		double hueSum = 0;
		double saturationSum = 0;
		double brightnessSum = 0;
		
		for (PaletteColor color : list) {
			hueSum = hueSum + color.hue;
			saturationSum = saturationSum + color.saturation;
			brightnessSum = brightnessSum + color.brightness;
		}
		
		PaletteColor averageColor = new PaletteColor(hueSum / list.size(), saturationSum / list.size(), brightnessSum / list.size());
		return averageColor;
	}
	
	public byte[] toColorTuple() {
		int redValue = (int)(red * 255.0);
		int greenValue = (int)(green * 255.0);
		int blueValue = (int)(blue * 255.0);
		
		int redComponent = (redValue / 8) & 0x1F;
		int greenComponent = (greenValue / 8) & 0x1F;
		int blueComponent = (blueValue / 8) & 0x1F;
		
		int colorValue = (blueComponent << 10) | (greenComponent << 5) | redComponent;
		return new byte[] { (byte)(colorValue & 0xFF), (byte)((colorValue & 0xFF00) >> 8) };
	}
	
	public String toRGBString() {
		return "R: " + getRedValue() + " G: " + getGreenValue() + " B: " + getBlueValue();
	}
	
	public String toHSBString() {
		return "H: " + (getHue() * 360) + " S: " + (getSaturation() * 100) + " B: " + (getBrightness() * 100);
	}
	
	// GBA only has color resolution up to 5 bits per component, so we should also note that when we return RGB values.
	public int getRedValue() {
		return (int)(red * 255.0) / 8 * 8;
	}
	public int getGreenValue() {
		return (int)(green * 255.0) / 8 * 8;
	}
	public int getBlueValue() {
		return (int)(blue * 255.0) / 8 * 8;
	}
	
	public double getHue() {
		return hue;
	}
	public double getSaturation() {
		return saturation;
	}
	public double getBrightness() {
		return brightness;
	}
	
	public void setRed(int newRedValue) {
		if (newRedValue >= 0 && newRedValue <= 255) {
			red = newRedValue / 255.0;
			calculateValuesWithRGB();
		} else {
			System.err.println("Invalid Red value detected.");
		}
	}
	public void setGreen(int newGreenValue) {
		if (newGreenValue >= 0 && newGreenValue <= 255) {
			green = newGreenValue / 255.0;
			calculateValuesWithRGB();
		} else {
			System.err.println("Invalid Green value detected.");
		}
	}
	public void setBlue(int newBlueValue) {
		if (newBlueValue >= 0 && newBlueValue <= 255) {
			blue = newBlueValue / 255.0;
			calculateValuesWithRGB();
		} else {
			System.err.println("Invalid Blue value detected.");
		}
	}
	
	public void setHue(double newHueValue) {
		if (newHueValue >= 0) {
			hue = newHueValue;
			calculateValuesWithHSB();
		} else {
			System.err.println("Invalid Hue value detected.");
		}
	}
	public void setSaturation(double newSaturationValue) {
		if (newSaturationValue >= 0 && newSaturationValue <= 1) {
			saturation = newSaturationValue;
			calculateValuesWithHSB();
		} else {
			System.err.println("Invalid Saturation value detected.");
		}
	}
	public void setBrightness(double newBrightnessValue) {
		if (newBrightnessValue >= 0 && newBrightnessValue <= 1) {
			brightness = newBrightnessValue;
			calculateValuesWithHSB();
		} else {
			System.err.println("Invalid Brightness value detected.");
		}
	}
	
	private void calculateValuesWithRGB() {
		if (red == green && green == blue) {
			hue = 0;
			saturation = 0;
			brightness = red;
		} else {
			double maxComponent = (red > green ? Math.max(red, blue) : Math.max(green, blue));
			double minComponent = (red > green ? Math.min(green, blue) : Math.min(red, blue));
			
			double midComponent = red + green + blue - (maxComponent + minComponent);
			double quotient = (midComponent - minComponent) / (maxComponent - minComponent) / 6;
			
			saturation = (maxComponent - minComponent) / maxComponent;
			brightness = maxComponent;
			
			if (red > blue && blue == green) { hue = 0; }
			else if (red > green && green > blue) { hue = quotient; }
			else if (red == green && green > blue) { hue = (double)1 / 6; }
			else if (green > red && red > blue) { hue = (double)2 / 6 - quotient; }
			else if (green > red && red == blue) { hue = (double)2 / 6; }
			else if (green > blue && blue > red) { hue = (double)2 / 6 + quotient; }
			else if (green == blue && blue > red) { hue = (double)3 / 6; }
			else if (blue > green && green > red) { hue = (double)4 / 6 - quotient; }
			else if (blue > green && green == red) { hue = (double)4 / 6; }
			else if (blue > red && red > green) { hue = (double)4 / 6 + quotient; }
			else if (blue == red && red > green) { hue = (double)5 / 6; }
			else if (red > blue && blue > green) { hue = 1 - quotient; }
		}
	}
	
	private void calculateValuesWithHSB() {
		if (saturation == 0) {
			red = brightness;
			green = brightness;
			blue = brightness;
		} else {
			double normalizedHue = hue;
			while (normalizedHue > 1) { normalizedHue = normalizedHue - 1; }
			
			double pro = brightness * saturation;
			double dim = brightness - pro;
			double mul = normalizedHue * 6;
			
			if (mul == 0) { 		red = brightness; 				green = dim; 					blue = dim; }
			else if (mul < 1) { 	red = brightness; 				green = dim + pro * (mul - 0); 	blue = dim; }
			else if (mul == 1) { 	red = brightness; 				green = brightness; 			blue = dim; }
			else if (mul < 2) { 	red = dim + pro * (2 - mul); 	green = brightness; 			blue = dim; }
			else if (mul == 2) {	red = dim;						green = brightness;				blue = dim; }
			else if (mul < 3) { 	red = dim;						green = brightness;				blue = dim + pro * (mul - 2); }
			else if (mul == 3) { 	red = dim;						green = brightness;				blue = brightness; }
			else if (mul < 4) {		red = dim;						green = dim + pro * (4 - mul);	blue = brightness; }
			else if (mul == 4) { 	red = dim;						green = dim;					blue = brightness; }
			else if (mul < 5) {		red = dim + pro * (mul - 4);	green = dim;					blue = brightness; }
			else if (mul == 5) {	red = brightness;				green = dim;					blue = brightness; }
			else if (mul < 6) { 	red = brightness;				green = dim;					blue = dim + pro * (6 - mul); }
		}
	}

	@Override
	public int compareTo(PaletteColor arg0) {
		// Return -1 if this is less than arg0, 1 if this is greater than arg0, and 0 if the two are the same.
		// We just need to compare brightness for our purposes, so that when ordered, our colors go from brightest to darkest.
		// So higher brightness values are "lower" for us.
		// If our brightness value is lower, we are a darker color and therefore come later in the list. This means we are "greater" than the other color.
		return brightness < arg0.brightness ? 1 : -1;
	}

	@Override
	public String toString() {
		return String.format("PaletteCollor red: %d, green %d, blue %d, brightness %f", getRedValue(), getGreenValue(), getBlueValue(), getBrightness());
	}

	@Override
	public int hashCode() {
		return toHexString().hashCode();
	}

	protected static String paletteStringForArray(PaletteColor[] palette) {
		return Stream.of(palette)
				.filter(o -> o != null)
				.map(PaletteColor::to4ByteHexString)// Convert every color to a 4 byte hex
				.collect(Collectors.joining()); // string them together
	}

	public static String arrayToString(PaletteColor[] palette) {
		return String.format("%-64s",PaletteColor.paletteStringForArray(palette)).replace(" ", "0");
	}

	public boolean isBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(boolean backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
