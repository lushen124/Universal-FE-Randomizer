package random.gba.randomizer;

import fedata.TerrainTable;
import fedata.TerrainTable.TerrainTableType;
import fedata.general.FEBase;
import org.junit.jupiter.api.BeforeEach;
import random.gba.loader.TerrainDataLoader;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class TerrainRandomizerTest {

    TerrainDataLoader dataLoader;

    @BeforeEach
    public void before() {
        Map<TerrainTableType, List<TerrainTable>> map = new HashMap<>();
        List<TerrainTable> movementTables = new ArrayList<>();
        // Hypothetical Infantry table
        movementTables.add(new TerrainTable(1, new byte[]{1, 2, (byte) 255, 1, (byte) 255}, TerrainTableType.MOVEMENT));
        // Hypothehical Flier table
        movementTables.add(new TerrainTable(2, new byte[]{1, 1, (byte) 255, 1, 1}, TerrainTableType.MOVEMENT));
        map.put(TerrainTableType.MOVEMENT, movementTables);
        movementTables = new ArrayList<>();
        // Hypothetical Infantry table
        movementTables.add(new TerrainTable(3, new byte[]{1, 4, (byte) 255, 2, 4, (byte) 255}, TerrainTableType.MOVEMENT_RAIN));
        // Hypothehical Flier table
        movementTables.add(new TerrainTable(4, new byte[]{1, 2, (byte) 255, 2, 2, (byte) 2}, TerrainTableType.MOVEMENT_RAIN));

        movementTables = new ArrayList<>();
        // Hypothetical Infantry table
        movementTables.add(new TerrainTable(5, new byte[]{1, 4, (byte) 255, 2, 4, (byte) 255}, TerrainTableType.MOVEMENT_RAIN));
        // Hypothehical Flier table
        movementTables.add(new TerrainTable(6, new byte[]{1, 2, (byte) 255, 2, 2, (byte) 2}, TerrainTableType.MOVEMENT_RAIN));

        dataLoader = new TerrainDataLoader(FEBase.GameType.FE8, null, null, 5);
    }


}
