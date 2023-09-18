package util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import application.Main;
import fedata.gba.general.PaletteColor;
import random.gba.randomizer.shuffling.data.PortraitChunkInfo;

public class GBAImageCodec {

    public static final PaletteColor[] gbaWeaponColorPalette = new PaletteColor[]{new PaletteColor(192, 248, 200),
            new PaletteColor(248, 248, 248), new PaletteColor(200, 192, 184), new PaletteColor(144, 144, 128),
            new PaletteColor(40, 56, 32), new PaletteColor(216, 208, 32), new PaletteColor(160, 8, 8),
            new PaletteColor(56, 80, 240),

            new PaletteColor(112, 120, 144), new PaletteColor(176, 176, 208), new PaletteColor(40, 128, 96),
            new PaletteColor(104, 200, 184), new PaletteColor(112, 80, 48), new PaletteColor(152, 128, 112),
            new PaletteColor(80, 56, 64), new PaletteColor(192, 96, 0)};

    public static byte[] getGBAGraphicsDataForImage(String name, PaletteColor[] palette) {
        if (palette == null || name == null) {
            return null;
        }

        InputStream stream = Main.class.getClassLoader().getResourceAsStream(name);
        try {
            BufferedImage image = ImageIO.read(stream);
            int width = image.getWidth();
            int height = image.getHeight();

            if (width % 8 != 0 || height % 8 != 0) {
                return null;
            }

            // GBA stores graphics in 8x8 chunks. A graphic that is larger than 8x8 is
            // stored left to right, top to bottom.
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
                            // Every pair of pixels is stored backwards. That is to say, if the pair of
                            // colors are [5, 10],
                            // the byte that encodes both pixels has the red index in the least significant
                            // bits
                            // and the blue index in the most significant bits, or 0xA5.
                            int firstColorIndex = indexOfColorInPalette(new PaletteColor(image.getRGB(trueX, trueY)),
                                    palette);
                            int secondColorIndex = indexOfColorInPalette(
                                    new PaletteColor(image.getRGB(trueX + 1, trueY)), palette);
                            if (firstColorIndex == -1 || secondColorIndex == -1) {
                                return null;
                            }
                            result[resultIndex++] = (byte) ((secondColorIndex << 4) | (firstColorIndex));
                        }
                    }
                }
            }
            return result;
        } catch (IOException e) {
            System.out.println("GBAImageCodec.getGBAGraphicsDataForImage: IOException ");
            return null;
        }
    }

    /*
     * For the Portrait Section see the following links for reference:
     * https://www.dropbox.com/sh/3m004vettv9g3og/AADFmL4DZVbE-nEHLS68rvF1a/
     * Nintenlord/Hacking/PortraitInserter/Portraits?dl=0&preview=PortraitFormat.cs&
     * subfolder_nav_tracking=1
     * https://www.dropbox.com/sh/3m004vettv9g3og/AAAVAAJR2eg_2RffuiuQDI24a/
     * Nintenlord/Hacking/GBA/GBA/Graphics?dl=0&preview=GBAGraphics.cs&
     * subfolder_nav_tracking=1
     * https://github.com/FEBuilderGBA/FEBuilderGBA/blob/dc89b6e2fa777f224de4739bb61bed38b70495fa/FEBuilderGBA/ImageUtil.cs#L1648
     */

    /**
     * Debug Statics
     */
    private static int DEBUG_COUNTER = 0;
    private static final boolean DEBUG = false;

    /**
     * Convenience overload of
     * {@link #getGBAPortraitGraphicsDataForImage(String, PaletteColor[], List, Size, Optional)}
     * that passes an empty optional, indicating that there is no prefix
     */
    public static byte[] getGBAPortraitGraphicsDataForImage(String name, PaletteColor[] palette,
                                                            List<PortraitChunkInfo> chunks, Size size) throws IOException {
        return getGBAPortraitGraphicsDataForImage(name, palette, chunks, size, Optional.empty());
    }

    /**
     * Returns the given image converted to a Byte array that can be inserted into
     * the rom.
     *
     * @param name    relative path to the portrait that should be converted
     * @param palette array containing the palette which should be used to convert
     *                the image
     * @param chunks  the chunks of the bigger image that should be considered (Main
     *                portrait, Mini Portrait, or Mouth Frames)
     * @param size    the size of the subimage that the abvove chunks build
     * @param prefix  An optional containing the prefix that should be added before
     *                the output, if any
     */
    public static byte[] getGBAPortraitGraphicsDataForImage(String name, PaletteColor[] palette,
                                                            List<PortraitChunkInfo> chunks, Size size, Optional<byte[]> prefix) throws IOException {
        if (palette == null || name == null || chunks == null || chunks.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("One of the arguments is invalid: palette %s, name %s, chunks %s, add %s, size %s",
                            palette, name, chunks, prefix, size));
        }

        InputStream stream = getImageAsStream(name);
        if (stream == null) return null;

        BufferedImage image = ImageIO.read(stream);
        int width = image.getWidth();
        int height = image.getHeight();

        // Verify that the image is the correct size
        if (width % 8 != 0 || height % 8 != 0)
            return null;

        /*
         * Create a new Buffered image with the size of the given size. And create the
         * Graphics2D object so we can write into the BufferedImage
         */
        BufferedImage destImage = new BufferedImage(size.width * 8, size.height * 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = destImage.createGraphics();

        /*
         * For all the chunks that should be part of this image, write them into the
         * newly created subimage
         */
        for (PortraitChunkInfo chunk : chunks) {
            Rectangle scaledRect = chunk.getRect();
            scaledRect.x *= 8;
            scaledRect.y *= 8;
            scaledRect.width *= 8;
            scaledRect.height *= 8;

            Point scaledPoint = chunk.getPoint();
            scaledPoint.x *= 8;
            scaledPoint.y *= 8;

            BufferedImage subImage = image.getSubimage(scaledRect.x, scaledRect.y, scaledRect.width, scaledRect.height);

            graphics.drawImage(subImage, null, scaledPoint.x, scaledPoint.y);
        }
        if (DEBUG)
            ImageIO.write(destImage, "png", new File("new-test" + (DEBUG_COUNTER++) + ".png"));
        return convertImageImpl(destImage, palette, prefix);
    }

    private static InputStream getImageAsStream(String name) {
        InputStream stream = Main.class.getClassLoader().getResourceAsStream(name);

        if (stream == null) {
            DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, "Couldn't find the Image %s in the resources, trying to find it in the same folder next");
            try {
                stream = new FileInputStream(new File(name));
            } catch (Exception e) {
                DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, "Couldn't find the Image %s in the same folder. Skipping the character.");
                return null;
            }
        }
        return stream;
    }

    /**
     * See:
     * https://github.com/FEBuilderGBA/FEBuilderGBA/blob/5eddcc577f01da3a3be5f5ba23d41d59270146ef/FEBuilderGBA/ImageUtil.cs#L1648
     */
    public static byte[] convertImageImpl(BufferedImage image, PaletteColor[] palette, Optional<byte[]> prefix) {
        int prefixLength = 0;
        if (prefix.isPresent()) {
            prefixLength = prefix.get().length;
        }

        byte[] result = new byte[image.getWidth() / 2 * image.getHeight() + prefixLength];

        // FE8 Main portraits seem to always start with these 3 bytes, so if the
        // argument
        // addPrefix is passed, add these and start later into the array
        if (prefix.isPresent()) {
            byte[] prefixBytes = prefix.get();
            for (int i = 0; i < prefixLength; i++) {
                result[i] = prefixBytes[i];
            }
        }

        final int chunkSize = 8;
        int width = image.getWidth();
        int height = image.getHeight();

        if (width % chunkSize != 0 || height % chunkSize != 0) {
            throw new IllegalArgumentException(
                    String.format("Height %d or %d Width isn't divisible by 8", height, width));
        }

        // GBA stores graphics in 8x8 chunks. A graphic that is larger than 8x8 is
        // stored left to right, top to bottom.
        int horizontalChunks = width / chunkSize;
        int verticalChunks = height / chunkSize;

        int nn = 0 + prefixLength;
        for (int yChunk = 0; yChunk < verticalChunks; yChunk++) {
            for (int xChunk = 0; xChunk < horizontalChunks; xChunk++) {
                for (int y = 0; y < chunkSize; y++) {
                    for (int x = 0; x < chunkSize; x += 2) {
                        int trueX = xChunk * chunkSize + x;
                        int trueY = yChunk * chunkSize + y;

                        // Each pixel is 4 bits for a total of 16 possible colors in the palette.
                        // Every pair of pixels is stored backwards. That is to say, if the pair of
                        // colors are [5, 10],
                        // the byte that encodes both pixels has the red index in the least significant
                        // bits
                        // and the blue index in the most significant bits, or 0xA5.
                        PaletteColor pixel1 = new PaletteColor(image.getRGB(trueX, trueY));
                        PaletteColor pixel2 = new PaletteColor(image.getRGB(trueX + 1, trueY));

                        byte a = (byte) indexOfColorInPalette(pixel1, palette);
                        byte b = (byte) indexOfColorInPalette(pixel2, palette);
                        if (a == -1 || b == -1) {
                            return (pixel1.isNoColor() && pixel2.isNoColor()) ? shrinkArray(result, nn) : null;
                        }

                        result[nn] = (byte) ((a & 0xF) + ((b & 0xF) << 4));
                        nn++;
                    }
                }
            }
        }

        return result;
    }

    public static PaletteColor[] collectPaletteForPicture(String picturePath) throws IOException {
        // load the image
        InputStream stream = getImageAsStream(picturePath);
        if (stream == null) return null;
        // load the image
        BufferedImage read = ImageIO.read(stream);
        PaletteColor bgColor;
        List<PaletteColor> colors;
        if (!PngUtil.isIndexBasedPng(stream, picturePath) || true) {
            colors = new ArrayList<>(grabColorsFromPicture(read));
        } else {
            // Grab only the color of the first pixel, which is presumably the background color,
            // so we can move the BackgroundColor to the front.
            bgColor = new PaletteColor(read.getRGB(0,0));

            // This is an index based PNG Image, so grab the colors from the metadata
            colors = new ArrayList<>(PngUtil.grabColorsFromPNGMetaData(stream));
            colors.get(colors.indexOf(bgColor)).setBackgroundColor(true);
        }

        // Sort the backgroundColor to the front, as GBAFE expects it at the front
        colors.sort(PaletteColor.backgroundColorComparator);

        // GBA FE can only handle 16 colors, so if there are more throw an exception
        if (colors.size() > 16) {
            throw new IllegalArgumentException(String.format("Palette for image %s couldn't be generated. The Picture contains %d colors rather than 16.", picturePath, colors.size()));
        }

        // Copy over the colors to an array, while making sure there are no null elements in here to prevent NPEs
        return colors.toArray(new PaletteColor[colors.size()]);
    }

    /**
     * Given an image it will grab upto 16 unique colors from that image and return them as a PaletteColor array,
     * to be used for importing images.
     * <br>
     * <br>
     * Note that while this method throws an exception if there are more than 16 colors,
     * it will not throw an exception if there are less than 16. For Example, Tethys has only 14 colors used,
     * so unless we parse the pallet from the image metadata somehow,
     * it is preferable to have any users of his method ensure it can handle pallets < 16.
     */
    public static Set<PaletteColor>  grabColorsFromPicture(BufferedImage image) throws IOException {
        // go through the image and collect all the unique colors
        Set<PaletteColor> colorSet = new HashSet<>(16);
        boolean isBg = true;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                PaletteColor color = new PaletteColor(image.getRGB(x, y));
                if (isBg) {
                    color.setBackgroundColor(true);
                    isBg = false;
                }
                colorSet.add(color);
            }
        }
        return colorSet;
    }

    /**
     * Only relevant for FE6.
     * Here the portrait format has a lot of empty space at the end, since it's not a perfect rectangle,
     * so make sure that we don't include all that empty space at the end.
     */
    public static byte[] shrinkArray(byte[] original, int newSize) {
        byte[] newArray = new byte[newSize];
        WhyDoesJavaNotHaveThese.copyBytesFromByteArray(original, newArray, 0, newSize);
        return newArray;
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
    public static PaletteColor[] getArrayFromPaletteString(String paletteString) {
        if (paletteString == null || paletteString.length() != 64) {
            throw new IllegalArgumentException("The given arguments aren't applicable for a 16 color palette.");
        }
        // Each palette has 16 Colors
        PaletteColor[] palette = new PaletteColor[16];
        for (int i = 0; i < 16; i += 1) {
            palette[i] = new PaletteColor(paletteString.substring(i * 4, i * 4 + 4));
        }

        return palette;
    }

    private static int indexOfColorInPalette(PaletteColor color, PaletteColor[] palette) {
        for (int i = 0; i < palette.length; i++) {
            if (color.isSameAsColor(palette[i])) {
                return i;
            }
        }

        return -1;
    }

}
