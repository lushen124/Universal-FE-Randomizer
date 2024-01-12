package random.gba.randomizer;

import fedata.gba.AbstractGBAData;
import fedata.gba.general.TerrainTable;
import fedata.gba.general.TerrainTable.TerrainTableType;
import fedata.general.FEBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import random.gba.loader.TerrainDataLoader;
import ui.model.MinMaxOption;
import ui.model.TerrainOptions;

import java.util.*;

import static org.mockito.Mockito.mock;

public class TerrainRandomizerTest {

    TerrainDataLoader dataLoader;
    Random rng;

    private final byte B255 = (byte) (255 & 0xFF);
    private static final int[] RANDOMIZED_TILES = new int[] {1, 2, 4, 5};
    private static final int[] NON_SAFE_TILES = new int[] {1};
    private static final int[] SAFE_TILES = new int[] {4, 5};
    private boolean keepSafeTiles = false;

    @BeforeEach
    public void before() {
        Map<TerrainTableType, List<TerrainTable>> dataMap = new HashMap<>();
        List<TerrainTable> tables = new ArrayList<>();
        // Regular Movement for Infantry + Flier
        tables.add(new TerrainTable(1, new byte[]{B255, 1, 2, B255, 1, 2, B255}, TerrainTableType.MOVEMENT));
        tables.add(new TerrainTable(2, new byte[]{B255, 1, 1, B255, 1, 1, 1}, TerrainTableType.MOVEMENT));
        dataMap.put(TerrainTableType.MOVEMENT, tables);

        // Rain Movement for Infantry + Flier
        tables = new ArrayList<>();
        tables.add(new TerrainTable(3, new byte[]{B255, 1, 4, B255, 2, 4, B255}, TerrainTableType.MOVEMENT_RAIN));
        tables.add(new TerrainTable(4, new byte[]{B255, 1, 2, B255, 2, 2, 2}, TerrainTableType.MOVEMENT_RAIN));
        dataMap.put(TerrainTableType.MOVEMENT_RAIN, tables);

        // Snow Movement for Infantry + Flier
        tables = new ArrayList<>();
        tables.add(new TerrainTable(5, new byte[]{B255, 1, 4, B255, 2, 4, B255}, TerrainTableType.MOVEMENT_SNOW));
        tables.add(new TerrainTable(6, new byte[]{B255, 1, 2, B255, 2, 2, (byte) 2}, TerrainTableType.MOVEMENT_SNOW));
        dataMap.put(TerrainTableType.MOVEMENT_SNOW, tables);

        // Healing and Status recovery, these ar universal
        dataMap.put(TerrainTableType.HEALING, Arrays.asList(new TerrainTable(7, new byte[]{B255, 20, 0, 0, 0, 0, 0}, TerrainTableType.HEALING)));
        dataMap.put(TerrainTableType.STATUS_RECOVERY, Arrays.asList(new TerrainTable(8, new byte[]{B255, 1, 0, 0, 0, 0, 0}, TerrainTableType.HEALING)));

        // Defense for Infantry + Flier
        tables = new ArrayList<>();
        tables.add(new TerrainTable(9, new byte[]{B255, 2, 1, 0, 0, 0, 0}, TerrainTableType.DEF));
        tables.add(new TerrainTable(10, new byte[]{B255, 0, 0, 0, 0, 0, 0}, TerrainTableType.DEF));
        dataMap.put(TerrainTableType.DEF, tables);

        // Defense for Infantry + Flier
        tables = new ArrayList<>();
        tables.add(new TerrainTable(11, new byte[]{B255, 5, 0, 0, 0, 0, 0}, TerrainTableType.RES));
        tables.add(new TerrainTable(12, new byte[]{B255, 0, 0, 0, 0, 0, 0}, TerrainTableType.RES));
        dataMap.put(TerrainTableType.RES, tables);

        // Avoid for Infantry + Flier
        tables = new ArrayList<>();
        tables.add(new TerrainTable(13, new byte[]{B255, 50, 20, 0, 0, 0, 0}, TerrainTableType.AVOID));
        tables.add(new TerrainTable(14, new byte[]{B255, 0, 0, 0, 0, 0, 0}, TerrainTableType.AVOID));
        dataMap.put(TerrainTableType.AVOID, tables);

        Map<Long, Boolean> applicableToFlier = new HashMap<>();
        applicableToFlier.put(1L, false);
        applicableToFlier.put(2L, true);
        applicableToFlier.put(3L, false);
        applicableToFlier.put(4L, true);
        applicableToFlier.put(5L, false);
        applicableToFlier.put(6L, true);
        applicableToFlier.put(7L, true);
        applicableToFlier.put(8L, true);
        applicableToFlier.put(9L, false);
        applicableToFlier.put(10L, true);
        applicableToFlier.put(11L, false);
        applicableToFlier.put(12L, true);
        applicableToFlier.put(12L, false);
        applicableToFlier.put(12L, true);
        applicableToFlier.put(13L, false);
        applicableToFlier.put(14L, true);

        dataLoader = new TerrainDataLoader(FEBase.GameType.FE8, dataMap, applicableToFlier, 7);


        rng = new Random(123456789);
        burnRNs(4);
    }

    @Test
    public void testKeepingSafeTiles() {
        MinMaxOption minMax = new MinMaxOption(1, 3);
        TerrainOptions terrainOptions = new TerrainOptions();
        terrainOptions.effectChance = 100;
        terrainOptions.randomizeDef = true;
        terrainOptions.defChance = 100;
        terrainOptions.defRange = minMax;
        terrainOptions.keepSafeTiles = true;
        this.keepSafeTiles = true;

        TerrainTable table = dataLoader.getTerrainTablesOfType(TerrainTableType.DEF).get(0);
        byte[] expected = table.copyOriginalData();
        prepareExpectedValues(expected, minMax);

        new TerrainRandomizer(rng, dataLoader, terrainOptions).randomize();
        validateStep(TerrainTableType.DEF, expected, minMax);
    }

    @Test
    public void testStatusRecovery() {
        TerrainOptions terrainOptions = new TerrainOptions();
        terrainOptions.effectChance = 100;
        terrainOptions.randomizeStatusRecovery = true;
        terrainOptions.statusRestoreChance = 100;

        new TerrainRandomizer(rng, dataLoader, terrainOptions).randomize();
        validateStep(TerrainTableType.STATUS_RECOVERY, new byte[]{B255, 1, 1, 0, 1, 1, 0}, null);
    }

    @Test
    public void testHealing() {
        MinMaxOption minMax = new MinMaxOption(10, 50);
        TerrainOptions terrainOptions = new TerrainOptions();
        terrainOptions.effectChance = 100;
        terrainOptions.randomizeHealing = true;
        terrainOptions.healingChance = 100;
        terrainOptions.healingRange = minMax;

        TerrainTable healing = dataLoader.getTerrainTablesOfType(TerrainTableType.HEALING).get(0);
        byte[] originalData = Arrays.copyOf(healing.getData(), healing.getData().length);

        byte[] expected = Arrays.copyOf(originalData, originalData.length);
        prepareExpectedValues(expected, minMax);


        new TerrainRandomizer(new Random(123456789), dataLoader, terrainOptions).randomize();

        validateStep(TerrainTableType.HEALING, expected, minMax);
    }
    @Test
    public void testAvoid() {
        MinMaxOption minMax = new MinMaxOption(10, 50);
        TerrainOptions terrainOptions = new TerrainOptions();
        terrainOptions.effectChance = 100;
        terrainOptions.randomizeAvoid = true;
        terrainOptions.avoidChance = 100;
        terrainOptions.avoidRange = minMax;

        TerrainTable healing = dataLoader.getTerrainTablesOfType(TerrainTableType.AVOID).get(0);

        byte[] expected = healing.copyOriginalData();
        prepareExpectedValues(expected, minMax);


        new TerrainRandomizer(new Random(123456789), dataLoader, terrainOptions).randomize();

        validateStep(TerrainTableType.AVOID, expected, minMax);
    }
    @Test
    public void testDefense() {
        MinMaxOption minMax = new MinMaxOption(1, 4);
        TerrainOptions terrainOptions = new TerrainOptions();
        terrainOptions.effectChance = 100;
        terrainOptions.randomizeDef = true;
        terrainOptions.defChance = 100;
        terrainOptions.defRange = minMax;

        TerrainTable healing = dataLoader.getTerrainTablesOfType(TerrainTableType.DEF).get(0);

        byte[] expected = healing.copyOriginalData();
        prepareExpectedValues(expected, minMax);


        new TerrainRandomizer(new Random(123456789), dataLoader, terrainOptions).randomize();

        validateStep(TerrainTableType.DEF, expected, minMax);
    }
    @Test
    public void testResistance() {
        MinMaxOption minMax = new MinMaxOption(3, 5);
        TerrainOptions terrainOptions = new TerrainOptions();
        terrainOptions.effectChance = 100;
        terrainOptions.randomizeRes = true;
        terrainOptions.resChance = 100;
        terrainOptions.resRange = minMax;

        TerrainTable healing = dataLoader.getTerrainTablesOfType(TerrainTableType.RES).get(0);

        byte[] expected = healing.copyOriginalData();
        prepareExpectedValues(expected, minMax);


        new TerrainRandomizer(new Random(123456789), dataLoader, terrainOptions).randomize();

        validateStep(TerrainTableType.RES, expected, minMax);
    }
    @Test
    public void testMinMaxEqual() {
        MinMaxOption minMax = new MinMaxOption(5, 5);
        TerrainOptions terrainOptions = new TerrainOptions();
        terrainOptions.effectChance = 100;
        terrainOptions.randomizeDef = true;
        terrainOptions.defChance = 100;
        terrainOptions.defRange = minMax;

        TerrainTable healing = dataLoader.getTerrainTablesOfType(TerrainTableType.DEF).get(0);

        byte[] expected = healing.copyOriginalData();
        prepareExpectedValues(expected, minMax);


        new TerrainRandomizer(new Random(123456789), dataLoader, terrainOptions).randomize();

        validateStep(TerrainTableType.DEF, expected, minMax);
    }

    public void validateStep(TerrainTableType tableType, byte[] expected, MinMaxOption minMax) {
        validateStep(tableType, expected, minMax, 0);
    }

    public void validateStep(TerrainTableType tableType, byte[] expected, MinMaxOption minMax, int tableNumber) {
        TerrainTable table = dataLoader.getTerrainTablesOfType(tableType).get(tableNumber);
//        Assertions.assertNotEquals(healing.getData(), healing.getOriginalDataCopy());
        Assertions.assertArrayEquals(expected, table.getData());

        int[] tiles = this.keepSafeTiles ? NON_SAFE_TILES : RANDOMIZED_TILES;

        if (minMax != null) {
            for (int tile : tiles) {
                int data = table.dataAtIndex(tile);
                Assertions.assertTrue(minMax.maxValue >= data && minMax.minValue <= data, "Value outside of range");
            }
        }

    }

    public void burnRNs(int number) {
        for (int i = 0; i < number; i++) {
            rng.nextInt();
        }
    }

    public void prepareExpectedValues(byte[] targetArray, MinMaxOption minMax) {
        // Discard one RN and then save the next one 4 times each.
        // The first RN is the check if the tile gets the healing effect.
        // The Second RN is the healing value it gains
        int[] tiles = this.keepSafeTiles ? NON_SAFE_TILES : RANDOMIZED_TILES;
        for (int i : tiles) {
            int chanceNumber = rng.nextInt(); // Discard one value used for the chance
            int variance = minMax.maxValue - minMax.minValue;
            int newValue = (variance != 0 ? rng.nextInt(minMax.maxValue - minMax.minValue) : 0) + minMax.minValue;
            targetArray[i] = AbstractGBAData.asByte(newValue);
        }
    }


    TerrainOptions getFullOptions() {
        TerrainOptions terrainOptions = new TerrainOptions();
        terrainOptions.randomizeDef = true;
        terrainOptions.defChance = 20;
        terrainOptions.defRange = new MinMaxOption(1, 3);
        terrainOptions.randomizeRes = true;
        terrainOptions.resChance = 10;
        terrainOptions.resRange = new MinMaxOption(3, 5);
        terrainOptions.randomizeAvoid = true;
        terrainOptions.avoidRange = new MinMaxOption(10, 50);
        terrainOptions.healingChance = 10;
        terrainOptions.healingRange = new MinMaxOption(10, 30);
        terrainOptions.randomizeHealing = true;
        terrainOptions.randomizeStatusRecovery = true;
        terrainOptions.statusRestoreChance = 10;
        terrainOptions.effectChance = 100;

        return terrainOptions;
    }


}
