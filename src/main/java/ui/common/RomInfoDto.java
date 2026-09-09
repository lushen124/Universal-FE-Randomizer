package ui.common;

import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gcnwii.fe9.FE9Data;
import fedata.general.FEBase.GameType;
import fedata.snes.fe4.FE4Data;
import io.FileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Simple Dto that bundles info that is parsed from the File and is to be displayed in the GUI
 */
public class RomInfoDto {

    private boolean patchingAvailable = false;
    private GameType type = GameType.UNKNOWN;
    private String friendlyName = "Display Name: Unknown";
    private String romCode;
    private String romName;
    private long checksum;
    private long length;

    private RomInfoDto() {
    }

    public static RomInfoDto forROM(FileHandler handler) throws IOException {
        RomInfoDto dto = new RomInfoDto();
        long crc32 = handler.getCRC32();
        dto.romName = parseGameTitle(handler);
        dto.romCode = parseGameCode(handler);
        dto.length = handler.getFileLength();
        dto.checksum = crc32;
        fillByGame(dto, crc32, handler);

        return dto;
    }

    private static void fillByGame(RomInfoDto dto, long crc32, FileHandler handler) {
        // Note that only ONE of these cases will actually add the information to the DTO.
        caseFE4(dto, crc32);
        caseFE6(dto, crc32);
        caseFE7(dto, crc32);
        caseFE8(dto, crc32);
        caseFE9(dto, crc32, handler);
    }

    private static void caseFE4(RomInfoDto dto, long crc32) {
        if (crc32 != FE4Data.CleanHeaderedCRC32 && crc32 != FE4Data.CleanUnheaderedCRC32) return;
        dto.type = GameType.FE4;
        dto.patchingAvailable = true;
        dto.friendlyName = FE4Data.FriendlyName;
        dto.romName = FE4Data.InternalName;
        dto.romCode = "--";
    }
    private static void caseFE6(RomInfoDto dto, long crc32) {
        if (crc32 != FE6Data.CleanCRC32) return;
        dto.type = GameType.FE6;
        dto.patchingAvailable = true;
        dto.friendlyName = FE6Data.FriendlyName;
    }
    private static void caseFE7(RomInfoDto dto, long crc32) {
        if (crc32 != FE7Data.CleanCRC32) return;
        dto.type = GameType.FE7;
        dto.friendlyName = FE7Data.FriendlyName;
    }
    private static void caseFE8(RomInfoDto dto, long crc32) {
        if (crc32 != FE8Data.CleanCRC32) return;
        dto.type = GameType.FE8;
        dto.friendlyName = FE8Data.FriendlyName;
    }
    private static void caseFE9(RomInfoDto dto, long crc32, FileHandler handler) {
        if (crc32 != FE9Data.CleanCRC32) return;
        dto.friendlyName = FE9Data.FriendlyName;
        try {
            GCNISOHandler gcnHandler = new GCNISOHandler(handler);
            dto.type = GameType.FE9;
            dto.romName = gcnHandler.getGameName();
            dto.romCode = gcnHandler.getGameCode();
        } catch (GCNISOException e) {
            DebugPrinter.log(DebugPrinter.Key.MAIN, e.getMessage());
            dto.romName = "Read Failed";
            dto.romCode = "Read Failed";
        }
    }


    private static String parseGameTitle(FileHandler handler) {
        byte[] result = handler.readBytesAtOffset(0xA0, 12);
        return new String(result, StandardCharsets.US_ASCII);
    }

    private static String parseGameCode(FileHandler handler) {
        byte[] result = handler.readBytesAtOffset(0xAC, 4);
        return new String(result, StandardCharsets.US_ASCII);
    }


    public boolean isPatchingAvailable() {
        return patchingAvailable;
    }

    public GameType getType() {
        return type;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getRomCode() {
        return romCode;
    }

    public String getRomName() {
        return romName;
    }

    public long getChecksum() {
        return checksum;
    }

    public long getLength() {
        return length;
    }
}
