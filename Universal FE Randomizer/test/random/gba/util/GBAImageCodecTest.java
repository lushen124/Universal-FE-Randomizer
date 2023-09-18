package random.gba.util;

import fedata.gba.general.PaletteColor;
import fedata.general.FEBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import random.gba.randomizer.shuffling.data.PortraitFormat;
import util.GBAImageCodec;

import java.io.IOException;
import java.util.*;

public class GBAImageCodecTest {

    /**
     * Simple test that when a PaletteColor is constructed, we get the correct 4 byte hex string from it afterwards
     */
    @Test
    public void testReconstructing4ByteHexString() {
        String expected = "344F";
        PaletteColor color = new PaletteColor(expected);
        Assertions.assertEquals(expected, color.to4ByteHexString());
    }
    
    @Test
    public void testAutoCalculatingPalette() {
        String eirikaPaletteString = "344FDF6F3F477D2ED3252C2587206377A26245416261FE335B23BA105373FE7F";
        List<PaletteColor> expectedArray = Arrays.asList(GBAImageCodec.getArrayFromPaletteString(eirikaPaletteString));
        List<PaletteColor> actualArray = Arrays.asList(Assertions.assertDoesNotThrow(() -> GBAImageCodec.collectPaletteForPicture("portraits/FE8/eirika.png")));
        String actualString = PaletteColor.arrayToString(actualArray.toArray(new PaletteColor[16]));

        assertPalettes(eirikaPaletteString, expectedArray, actualArray, actualString);
    }
    @Test
    public void testMainPortraitMissingColorsInPalette() throws IOException {
        String tethysPaletteString = "344FDF6F3F477D2ED3252C258720FD14790C9008FF52FF31FF27AC20734E3567";
        List<PaletteColor> expectedArray = Arrays.asList(GBAImageCodec.getArrayFromPaletteString(tethysPaletteString));
        List<PaletteColor> actualArray = Arrays.asList(Assertions.assertDoesNotThrow(() -> GBAImageCodec.collectPaletteForPicture("portraits/FE8/tethys.png")));
        String actualString = PaletteColor.arrayToString(actualArray.toArray(new PaletteColor[16]));

        assertPalettes(tethysPaletteString, expectedArray, actualArray, actualString);

        final HashSet<PaletteColor> unusedColors = new HashSet<>();

        for (PaletteColor color : expectedArray) {
            if (!actualArray.contains(color))
                unusedColors.add(color);
        }

        // Sort the unused colors to the back, and then sort by Red -> Green -> Blue
        // Unused must be sorted to the back as the image is compressed with index compared to pallet, so we don't want unused colors to interfere.
        Comparator<PaletteColor> paletteComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                PaletteColor p1 = (PaletteColor) o1;
                PaletteColor p2 = (PaletteColor) o2;

                boolean p1Unused =unusedColors.contains(p1);
                boolean p2Unused =unusedColors.contains(p2);



                if (p1Unused || p2Unused) {
                    return (p1Unused == p2Unused ? 0 : (p1Unused && !p2Unused ? 1 : -1));
                }

                int compare = Integer.compare(p1.getRedValue(), p2.getRedValue());

                if (compare != 0) {
                    return compare;
                }

                compare = Integer.compare(p1.getGreenValue(), p2.getGreenValue());
                if (compare != 0) {
                    return compare;
                }
                return Integer.compare(p1.getBlueValue(), p2.getBlueValue());
            }
        };



        expectedArray.sort(paletteComparator);
        actualArray.sort(paletteComparator);

        PortraitFormat format = PortraitFormat.getPortraitFormatForGame(FEBase.GameType.FE8);

        byte[] actualImage = GBAImageCodec.getGBAPortraitGraphicsDataForImage("portraits/FE8/tethys.png", actualArray.toArray(new PaletteColor[16]), format.getMainPortraitChunks(), format.getMainPortraitSize());
        byte[] expectedImage = GBAImageCodec.getGBAPortraitGraphicsDataForImage("portraits/FE8/tethys.png", expectedArray.toArray(new PaletteColor[16]), format.getMainPortraitChunks(), format.getMainPortraitSize());
        Assertions.assertArrayEquals(expectedImage, actualImage);
    }

    private static void assertPalettes(String tethysPaletteString, List<PaletteColor> expectedArray, List<PaletteColor> actualArray, String actualString) {
        // check that the PaletteColor objects that were calculated are all contained within the expected ones
        for(int i = 0; i< actualArray.size(); i++) {
            PaletteColor color = actualArray.get(i);
            if (color != null) {
                Assertions.assertTrue(expectedArray.contains(color), ""+i);
            }
        }

        // Get all the 4 Byte Components from the expected String so we can compare that the calculated string is correct too
        List<String> expectedColorsHex = new ArrayList(16);
        for (int i = 0; i < 64; i += 4) {
            expectedColorsHex.add(tethysPaletteString.substring(i, i+4));
        }

        // Check that assuming we didn't hit a palette that
        for (int i = 0; i < actualString.length(); i += 4) {
            String colorHex = actualString.substring(i, i + 4);
            if (!"0000".equals(colorHex))
                Assertions.assertTrue(expectedColorsHex.contains(colorHex));
        }
    }
}
