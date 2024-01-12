package random.gba.randomizer;

import fedata.gba.general.TerrainTable;
import fedata.gba.general.TerrainTable.TerrainTableType;
import random.gba.loader.TerrainDataLoader;
import ui.model.MinMaxOption;
import ui.model.TerrainOptions;
import util.DebugPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static util.DebugPrinter.Key.GBA_TERRAIN_RANDOMIZER;

public class TerrainRandomizer {

    public static int rngSalt = 564894;

    private final Random rng;
    private final TerrainDataLoader terrainData;
    private final TerrainOptions options;
    private final List<Integer> untraversableTiles = new ArrayList<>();
    private final List<Integer> excludedTiles = new ArrayList<>();
    private final List<Integer> flierOnlyTiles = new ArrayList<>();
    private final List<Integer> safeTiles = new ArrayList<>();
    private final int numberTiles;

    public TerrainRandomizer(Random rng, TerrainDataLoader terrainData, TerrainOptions options) {
        this.rng = rng;
        this.terrainData = terrainData;
        this.options = options;

        // All tables in a game have the same number of entries, so this is fine
        numberTiles = terrainData.dataLengthBytes;

        determineUntraversableTiles();
        determineSafeTiles();
        determineFlierOnlyTiles();

        for (int i = 1; i < terrainData.dataLengthBytes; i++) {
            if (flierOnlyTiles.contains(i) || untraversableTiles.contains(i)) {
                excludedTiles.add(i);
                continue;
            }
            if (rng.nextInt(100) > options.effectChance) {
                excludedTiles.add(i);
            }
        }
    }

    public void randomize() {
        if (options.randomizeMovementCost) {
            handleTable(TerrainTableType.MOVEMENT, options.movementCostRange, true);
            handleTable(TerrainTableType.MOVEMENT_RAIN, options.movementCostRange, true);
            handleTable(TerrainTableType.MOVEMENT_SNOW, options.movementCostRange, true);
        }


        if (options.randomizeHealing) {
            handleTable(TerrainTableType.HEALING, options.healingRange, false, options.healingChance);
        }

        if (options.randomizeStatusRecovery) {
            handleTable(TerrainTableType.STATUS_RECOVERY, new MinMaxOption(0, 1), false, options.statusRestoreChance);
        }

        if (options.randomizeAvoid) {
            handleTable(TerrainTableType.AVOID, options.avoidRange, true, options.avoidChance);
        }

        if (options.randomizeDef) {
            handleTable(TerrainTableType.DEF, options.defRange, true, options.defChance);
        }

        if (options.randomizeRes) {
            handleTable(TerrainTableType.RES, options.resRange, true, options.resChance);
        }
    }

    /**
     * Determines all the tiles which are only usable by Fliers
     */
    private void determineFlierOnlyTiles() {
        List<TerrainTable> nonFlierMovementTables = terrainData.getTerrainTablesOfType(TerrainTableType.MOVEMENT).stream()
                .filter(table -> !terrainData.isUsedByFliers(table))
                .collect(Collectors.toList());

        for (int i = 1; i < numberTiles; i++) {
            boolean isFlierOnly = true;

            for (TerrainTable table : nonFlierMovementTables) {
                isFlierOnly &= table.tableType.isDisabled(table.dataAtIndex(i));
            }

            if (isFlierOnly && !untraversableTiles.contains(i)) {
                this.flierOnlyTiles.add(i);
            }
        }
    }

    /**
     * Determines the indicies of all Tiles that have no effect among: Avoid, Def, Res, Healing, or Status Recovery.
     */
    private void determineSafeTiles() {
        List<TerrainTable> tables = new ArrayList<>();
        tables.addAll(terrainData.getTerrainTablesOfType(TerrainTableType.AVOID));
        tables.addAll(terrainData.getTerrainTablesOfType(TerrainTableType.DEF));
        tables.addAll(terrainData.getTerrainTablesOfType(TerrainTableType.RES));
        tables.addAll(terrainData.getTerrainTablesOfType(TerrainTableType.HEALING));
        tables.addAll(terrainData.getTerrainTablesOfType(TerrainTableType.STATUS_RECOVERY));
        tables.removeIf(table -> terrainData.isUsedByFliers(table) && !table.tableType.isUniversal());

        for (int i = 1; i < numberTiles; i++) {
            boolean hasEffect = false;
            for (TerrainTable table : tables) {
                hasEffect |= !table.tableType.isDisabled(table.dataAtIndex(i));
            }
            if (!hasEffect) {
                this.safeTiles.add(i);
            }
        }

    }

    private void determineUntraversableTiles() {
        List<TerrainTable> moveTables = terrainData.getTerrainTablesOfType(TerrainTableType.MOVEMENT);
        for (int i = 1; i < numberTiles; i++) {
            boolean isUntraversable = true;

            for (TerrainTable table : moveTables) {
                isUntraversable &= table.tableType.isDisabled(table.dataAtIndex(i));
            }

            if (isUntraversable) {
                this.untraversableTiles.add(i);
            }
        }
    }

    public void handleTable(TerrainTableType tableType, MinMaxOption minMax, boolean excludeFliers) {
        handleTable(tableType, minMax, excludeFliers, -1);
    }

    public void handleTable(TerrainTableType tableType, MinMaxOption minMax, boolean excludeFliers, int chance) {
        List<TerrainTable> tablesOfType = terrainData.getTerrainTablesOfType(tableType);
        System.out.println("Found " + tablesOfType.size() + " tables of type "+tableType);
        for (TerrainTable table : tablesOfType) {
            // if the current Step is not applicable to fliers, then skip any table used by flier classes
            if (excludeFliers && terrainData.isUsedByFliers(table)) {
                continue;
            }
            for (int i = 1; i < table.getData().length; i++) {
                int oldValue = table.dataAtIndex(i);
                if (untraversableTiles.contains(i) // never randomize completely untraversable Tiles
                        || (options.keepSafeTiles && safeTiles.contains(i)) // If the user chose, keep things such as roads / plains safe
                        || excludedTiles.contains(i) // if this tile was randomized to not have a change, skip it
                        || tableType.mayNotChange(oldValue) // Exclude all Untraversable Tiles
                ) {
                    continue;
                }

                if (chance == -1 || rng.nextInt(100) <= chance) {
                    int newValue;
                    if (tableType.isToggle) {
                        newValue = tableType.max;
                    } else if (minMax.maxValue == minMax.minValue) {
                        newValue = minMax.maxValue;
                    } else {
                        newValue = rng.nextInt(minMax.maxValue - minMax.minValue) + minMax.minValue;
                    }
                    table.setAtIndex(i, newValue);
                    DebugPrinter.log(GBA_TERRAIN_RANDOMIZER,"Table "+table.getAddressOffset()+" index"+i+" oldValue "+oldValue+" newValue "+newValue);
                }
            }
            if (table.wasModified()) {
                DebugPrinter.log(GBA_TERRAIN_RANDOMIZER,"Modified table: " + table.getAddressOffset()+ ", for type " + tableType);
            }
        }
    }

}