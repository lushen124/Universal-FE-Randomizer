package fedata.general;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import util.WhyDoesJavaNotHaveThese;

public class PaletteColor {
	private double red;
	private double green;
	private double blue;
	
	private double hue;
	private double saturation;
	private double brightness;
	
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
	
	public int getRedValue() {
		return (int)(red * 255.0);
	}
	public int getGreenValue() {
		return (int)(green * 255.0);
	}
	public int getBlueValue() {
		return (int)(blue * 255.0);
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
}
