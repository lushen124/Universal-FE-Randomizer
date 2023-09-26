package fedata;

import fedata.gba.AbstractGBAData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Represents a Terrain Table, each Terrain table applies to a subset of classes in the game (or all),
 * and describes the properties of it relating to all tiles.
 * <p>
 * Each Terrain Table relates to a TerrainTableType and there can be multiple tables for one TerrainTableType.
 */
public class TerrainTable extends AbstractGBAData {
    public enum TerrainTableType {

        /**
         * Table containing the default Movement costs for the classes using this given table.
         * Valid values are technically 1-254, 255 means it is untraversable.
         */
        MOVEMENT(1, 255, 255, true),
        /**
         * Table containing the default Movement costs for the classes using this given table.
         *          * Valid values are technically 1-254, 255 means it is untraversable.
         */
        MOVEMENT_RAIN(1, 255, 255, true),
        /**
         * Table containing the default Movement costs for the classes using this given table.
         * Valid values are technically 1-254, 255 means it is untraversable.
         */
        MOVEMENT_SNOW(1, 255, 255, true),

        /**
         * The number of additional defense points the unit gets from standing on this tile
         */
        AVOID(0, 255, 0, false),
        /**
         * The number of additional defense points the unit gets from standing on this tile
         */
        DEF(0, 255, 0, false),
        /**
         * The number of additional resistence points the unit gets from standing on this tile
         */
        RES(0, 255, 0, false),
        /**
         * The number of hitpoints that are restored by standing on this tile in Percent
         */
        HEALING(0, 100, 0, true),
        /**
         * Decides if negative status effects on the unit are cleared when standing on this tile. 0 = No, 1 = Yes
         */
        STATUS_RECOVERY(0, 1, 0, true),
        /**
         * INFO_DISPLAY decides if the attributes of a tile should be displayed in the GUI or not,
         * but apparently is also used for Berserk AI? Doesn't seem like something that makes sense to.
         */
        /*INFO_DISPLAY*/;


        public int min;
        public int max;
        public int disabledValue;
        public boolean applicableToFliers;
        public boolean isToggle;

        TerrainTableType(int min, int max, int disabledValue, boolean applicableToFliers) {
            this.min = min;
            this.max = max;
            this.disabledValue = disabledValue;
            this.applicableToFliers = applicableToFliers;
            this.isToggle = max - min == 1;
        }

        public static final List<TerrainTableType> CLASS_BOUND = Collections.unmodifiableList(Arrays.asList(MOVEMENT, MOVEMENT_RAIN, MOVEMENT_SNOW, AVOID, DEF, RES));
        public static final List<TerrainTableType> UNIVERSAL = Collections.unmodifiableList(Arrays.asList(HEALING, STATUS_RECOVERY));
        public static final List<TerrainTableType> MOVEMENT_TABLES = Collections.unmodifiableList(Arrays.asList(MOVEMENT, MOVEMENT_RAIN, MOVEMENT_SNOW));

        public boolean isDisabled(int value) {
            return this.disabledValue == value;
        }
        public boolean isMin(int value) {
            return this.min == value;
        }

        public boolean isSafe(int value) {
            return this.isDisabled(value) && !isMovementTable() || (this.isMin(value) && isMovementTable());
        }

        public boolean mayNotChange(int value) {
            return this.isMovementTable() && isDisabled(value);
        }

        public boolean isMovementTable() {
            switch (this) {
                case MOVEMENT:
                case MOVEMENT_RAIN:
                case MOVEMENT_SNOW:
                    return true;
                default:
                    return false;

            }
        }

        public boolean isUniversal() {
            return UNIVERSAL.contains(this);
        }
    }

    public final TerrainTableType tableType;

    public TerrainTable(long offset, byte[] data, TerrainTableType type) {
        super(data, offset);
        this.tableType = type;
    }

    public void setAtIndex(int index, int newValue) {
        if (index == 0) {
            // The first value in all terrain tables may not be changed
            return;
        }
        data[index] = asByte(newValue);
    }
}
