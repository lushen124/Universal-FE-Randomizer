package util;

import fedata.gba.general.PaletteColor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class PngUtil {

    /**
     * Returns true if the picture is a .png file and the colorType byte is set to 3
     */
    public static boolean isIndexBasedPng(InputStream stream, String picturePath) throws IOException {
        if (!picturePath.endsWith(".png")){
            return false;
        }

        // https://www.w3.org/TR/PNG-Chunks.html
        // 8 Bytes png indicator
        // 4 Bytes IHDR Chunk Length <-- Doesn't include itself, the chunk type, or CRC
        // 4 Bytes IHDR Chunk Type
        // IHDR Chunk Data:
        // Width:              4 bytes
        // Height:             4 bytes
        // Bit depth:          1 byte
        // Color type:         1 byte
        // Compression method: 1 byte
        // Filter method:      1 byte
        // Interlace method:   1 byte
        // 4 Bytes IHDR CRC
        byte[] metaData = new byte[33];

        stream.read(metaData, 0, 33);

        // Validate that is palette Based by checking this is 3 (palette used [1] + color used[2])
        return metaData[25] == 3;
    }

    /**
     * Parses the PLTE Chunk from the given input stream.
     * <p>
     * <a href="https://www.w3.org/TR/PNG-Chunks.html">Reference</a>
     */
    public static Set<PaletteColor> grabColorsFromPNGMetaData(InputStream stream) throws IOException {
        byte[] metaData;
        // Read 8 bytes and validate if the next chunk is the PLTE, if not skip a number of bytes
        // equal to the specified length of the next chunk+ 4 for the CRC until we find the PLTE chunk.
        metaData = new byte[8];
        int length;
        ByteBuffer buffer = ByteBuffer.wrap(metaData);

        // We Read 8 bytes each time (4 Bytes Length + 4 Bytes Chunk Type)
        // If the Chunk Bytes don't match the PLTE byte,
        // then we will skip ahead by a number of bytes equal to the value of the length + another 4 bytes for the current chunks CRC
        // That lands us at the start of the next chunk, so we can repeat the process until we find the PLTE Chunk
        while (!isPLTEChunk(metaData)) {
            stream.read(metaData, 0, 8);

            if (!isPLTEChunk(metaData)) {
                length = buffer.getInt();
                buffer.position(0);
                stream.skip(length + 4);
            }

        }

        // Discard the type
        Set<PaletteColor> colorSet = new HashSet<>(16);
        length = buffer.getInt();
        for (int i = 0; i < length / 3; i++) {
            // The Pallet in the PNG Specification only has 3 bytes for each entry R,G,B. For ease of reading it into an int, prepend an empty byte each time
            stream.read(metaData, 0, 3);
            if (metaData[0] == 0 && metaData[1] == 0 && metaData[2] == 0) {
                break;
            }
            colorSet.add(new PaletteColor(metaData[0] & 0xFF, metaData[1] & 0xFF, metaData[2] & 0xFF));
            buffer.position(0);
        }
        return colorSet;
    }


    private static boolean isPLTEChunk(byte[] metaData) {
        return metaData[4] == 80 || metaData[5] == 76 || metaData[6] == 84 || metaData[7] == 69;
    }
}
