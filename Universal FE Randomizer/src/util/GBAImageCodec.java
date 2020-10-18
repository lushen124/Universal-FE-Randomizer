package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import application.Main;
import fedata.gba.general.PaletteColor;

public class GBAImageCodec {
	
	public static final PaletteColor[] gbaWeaponColorPalette = new PaletteColor[] {
			new PaletteColor(192, 248, 200),
			new PaletteColor(248, 248, 248),
			new PaletteColor(200, 192, 184),
			new PaletteColor(144, 144, 128),
			new PaletteColor(40, 56, 32),
			new PaletteColor(216, 208, 32),
			new PaletteColor(160, 8, 8),
			new PaletteColor(56, 80, 240),
			
			new PaletteColor(112, 120, 144),
			new PaletteColor(176, 176, 208),
			new PaletteColor(40, 128, 96),
			new PaletteColor(104, 200, 184),
			new PaletteColor(112, 80, 48),
			new PaletteColor(152, 128, 112),
			new PaletteColor(80, 56, 64),
			new PaletteColor(192, 96, 0)
	};
	
	public static byte[] getGBAGraphicsDataForImage(String name, PaletteColor[] palette) {
		if (palette == null || name == null) { return null; }
		
		InputStream stream = Main.class.getClassLoader().getResourceAsStream(name);
		try {
			BufferedImage image = ImageIO.read(stream);
			int width = image.getWidth();
			int height = image.getHeight();
			
			if (width % 8 != 0 || height % 8 != 0) { return null; }
			
			// GBA stores graphics in 8x8 chunks. A graphic that is larger than 8x8 is stored left to right, top to bottom.
			int horizontalChunks = width / 8;
			int verticalChunks = height / 8;
			
			// Each chunk is 0x20 bytes long.
			byte[] result = new byte[0x20 * horizontalChunks * verticalChunks];
			int resultIndex = 0;
			
			for (int yChunk = 0; yChunk < verticalChunks; yChunk++) {
				for (int xChunk = 0; xChunk < horizontalChunks; xChunk++) {
					for (int y = 0; y < 8; y++) {
						for (int x = 0; x < 8; x += 2) {
							int trueX = xChunk * 8 + x;
							int trueY = yChunk * 8 + y;
							
							// Each pixel is 4 bits for a total of 16 possible colors in the palette.
							// Every pair of pixels is stored backwards. That is to say, if the pair of colors are [5, 10], 
							// the byte that encodes both pixels has the red index in the least significant bits
							// and the blue index in the most significant bits, or 0xA5.
							int firstColorIndex = indexOfColorInPalette(colorForInteger(image.getRGB(trueX, trueY)), palette);
							int secondColorIndex = indexOfColorInPalette(colorForInteger(image.getRGB(trueX + 1, trueY)), palette);
							if (firstColorIndex == -1 || secondColorIndex == -1) { return null; }
							result[resultIndex++] = (byte)((secondColorIndex << 4) | (firstColorIndex));
						}
					}
				}
			}
			return result;
		} catch (IOException e) {
			return null;
		}
	}
	
	private static int indexOfColorInPalette(PaletteColor color, PaletteColor[] palette) {
		for (int i = 0; i < palette.length; i++) {
			if (color.isSameAsColor(palette[i])) {
				return i;
			}
		}
		
		return -1;
	}
	
	private static PaletteColor colorForInteger(int colorValue) {
		int red = (colorValue & 0xFF0000) >> 16;
		int green = (colorValue & 0xFF00) >> 8;
		int blue = (colorValue & 0xFF);
		return new PaletteColor(red, green, blue);
	}

}
