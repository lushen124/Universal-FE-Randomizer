package random.gba.loader;

import fedata.gba.GBAFEClassData;
import fedata.gba.general.TerrainTable;
import fedata.gba.general.TerrainTable.TerrainTableType;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;

import java.util.*;
import java.util.stream.Collectors;

import static util.DebugPrinter.Key.GBA_TERRAIN_RANDOMIZER;

/**
 *
 */
public class TerrainDataLoader {

    GameType gameType;

    private Map<TerrainTableType, List<TerrainTable>> terrainDataMap = new HashMap<>();
    private Map<Long, Boolean> pointerUsedByFliers = new HashMap<>();
    public int dataLengthBytes;

    /**
     * Construtor for unit tests
     */
    public TerrainDataLoader(GameType gameType, Map<TerrainTableType, List<TerrainTable>> dataMap, Map<Long, Boolean> pointerUsedByFliers, int dataLengthBytes) {
        this.gameType = gameType;
        this.terrainDataMap = dataMap;
        this.pointerUsedByFliers = pointerUsedByFliers;
        this.dataLengthBytes = dataLengthBytes;
    }

    public TerrainDataLoader(GameType type, ClassDataLoader classData, FileHandler handler) {
        this.gameType = type;
        Collection<GBAFEClassData> allClasses = classData.getClassMap().values();
        dataLengthBytes = terrainDataLengthInBytes();
        for (TerrainTableType tableType : TerrainTableType.CLASS_BOUND) {
            List<TerrainTable> terrainDataOfType = allClasses.stream()
                    .map(e -> e.getTerrainPointerByType(tableType)) // Map each class to it's pointer for the current type
                    .distinct()// Filter out duplicates
                    .filter(pointer -> pointer > 0 && pointer < 0x9000000)// Filter out non-valid pointers
                    .peek(pointer -> pointerUsedByFliers.put(pointer, isPointerUsedByFliers(classData, allClasses, tableType, pointer)))
                    .filter(pointer -> { // Filter out fliers if the Table Type isn't applicable to them (DEF, RES, AVOID)
                        // If it's applicable to fliers, we can keep all entries regardless
                        if (tableType.applicableToFliers) {
                            return true;
                        }
                        // If it's not applicable to fliers, check if the current pointer is used by any flier, if so then remove them
                        return Boolean.FALSE.equals(pointerUsedByFliers.get(pointer));
                    })
                    .map(offset ->
                        new TerrainTable(offset, handler.readBytesAtOffset(offset, dataLengthBytes), tableType)
                    )// Create a TerrainData object for the current Pointer
                    .collect(Collectors.toList()); // Collect them to a list
            terrainDataMap.put(tableType, terrainDataOfType);
        }
        for (TerrainTableType tableType : TerrainTableType.UNIVERSAL) {
            long offset = staticPointerByTypeAndGame(tableType);
            TerrainTable terrainDataOfType = new TerrainTable(offset, handler.readBytesAtOffset(offset, dataLengthBytes), tableType);
            terrainDataMap.put(tableType, Arrays.asList(terrainDataOfType));
        }
    }

    public void compileDiffs(DiffCompiler diffCompiler) {
        terrainDataMap.entrySet().stream().map(e -> e.getValue()).flatMap(e -> e.stream()).forEach(table -> {
            if (!table.wasModified()) {
                return;
            }
            DebugPrinter.log(GBA_TERRAIN_RANDOMIZER,"Adding Terrain Diff at: " + table.getAddressOffset());
            diffCompiler.addDiff(new Diff(table.getAddressOffset(), table.getData().length, table.getData(), null));
        });
    }

    /**
     * Returns true if any of the classes is a flying class and matches the given pointer
     */
    private static boolean isPointerUsedByFliers(ClassDataLoader classData, Collection<GBAFEClassData> allClasses, TerrainTableType tableType, Long pointer) {
        return allClasses.stream()
                .filter(c -> classData.isFlying(c.getID()))
                .anyMatch(c -> c.getTerrainPointerByType(tableType) == pointer);
    }

    /**
     * Returns all the terrain tables for the given TerrainTableType.
     * For example: Returns all the Movement Tables (Foot, Horse, Knight etc.) if the given TerrainTableType is MOVEMENT
     */
    public List<TerrainTable> getTerrainTablesOfType(TerrainTableType type) {
        return terrainDataMap.get(type);
    }

    /**
     * Returns true if during the generation of the TerrainTables we recognized that the given pointer is used by fliers.
     * This can be used to filter out tables that apply to fliers from randomization.
     * An example use case is that in vanilla DEF / RES / Avoid don't apply to fliers, but they still have tables for them.
     */
    public boolean isUsedByFliers(TerrainTable table) {
        if (TerrainTableType.UNIVERSAL.contains(table.tableType)) {
            return true;
        }

        if (!pointerUsedByFliers.containsKey(table.getAddressOffset())) {
            throw new IllegalArgumentException(String.format("All the tables must have been mapped to if they are being used by fliers or not! But Pointer %d wasn't", table.getAddressOffset()));
        }
        return pointerUsedByFliers.get(table.getAddressOffset());
    }

    public List<TerrainTable> getMovementCostsForNonFliers() {
        return terrainDataMap.get(TerrainTableType.MOVEMENT).stream().filter(data -> !pointerUsedByFliers.get(data.getAddressOffset())).collect(Collectors.toList());
    }

    private int terrainDataLengthInBytes() {
        if (GameType.FE8.equals(gameType) || GameType.FE7.equals(gameType)) {
            return 0x41;
        } else {
            // FE6
            return 0x33;
        }
    }

    private long staticPointerByTypeAndGame(TerrainTableType tableType) {
        if (GameType.FE8.equals(gameType)) {
            return TerrainTableType.HEALING.equals(tableType) ? 0x80C744 : 0x80C785;
        } else if (GameType.FE7.equals(gameType)) {
            return TerrainTableType.HEALING.equals(tableType) ? 0xBE47C4 : 0xBE4805;
        } else {
            // FE6
            return TerrainTableType.HEALING.equals(tableType) ? 0x60CBA9 : 0x60CBDC;
        }
    }
}
