package fedata.gba.general;

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
        MOVEMENT(1, 255, 255, true, "Movement"),
        /**
         * Table containing the default Movement costs for the classes using this given table.
         * * Valid values are technically 1-254, 255 means it is untraversable.
         */
        MOVEMENT_RAIN(1, 255, 255, true, "Rain Movement"),
        /**
         * Table containing the default Movement costs for the classes using this given table.
         * Valid values are technically 1-254, 255 means it is untraversable.
         */
        MOVEMENT_SNOW(1, 255, 255, true, "Snow Movement"),

        /**
         * The number of additional avoid points the unit gets from standing on this tile
         */
        AVOID(0, 255, 0, false, "Avoid"),
        /**
         * The number of additional defense points the unit gets from standing on this tile
         */
        DEF(0, 255, 0, false, "Defense"),
        /**
         * The number of additional resistence points the unit gets from standing on this tile
         */
        RES(0, 255, 0, false, "Resistence"),
        /**
         * The number of hitpoints that are restored by standing on this tile in Percent
         */
        HEALING(0, 100, 0, true, "Healing"),
        /**
         * Decides if negative status effects on the unit are cleared when standing on this tile. 0 = No, 1 = Yes
         */
        STATUS_RECOVERY(0, 1, 0, true, "Status Recovery"),
        /**
         * INFO_DISPLAY decides if the attributes of a tile should be displayed in the GUI or not,
         * but apparently is also used for Berserk AI? Doesn't seem like something that makes sense to change.
         */
        /*INFO_DISPLAY*/;


        public int min;
        public int max;
        public int disabledValue;
        public boolean applicableToFliers;
        public boolean isToggle;
        public String displayString;

        TerrainTableType(int min, int max, int disabledValue, boolean applicableToFliers, String displayString) {
            this.min = min;
            this.max = max;
            this.disabledValue = disabledValue;
            this.applicableToFliers = applicableToFliers;
            this.isToggle = max - min == 1;
            this.displayString = displayString;
        }

        /** List containing all table types that are defined for each class */
        public static final List<TerrainTableType> CLASS_BOUND = Collections.unmodifiableList(Arrays.asList(MOVEMENT, MOVEMENT_RAIN, MOVEMENT_SNOW, AVOID, DEF, RES));

        /** List containing all table types that are not defined for each class */
        public static final List<TerrainTableType> UNIVERSAL = Collections.unmodifiableList(Arrays.asList(HEALING, STATUS_RECOVERY));

        /** List containing all movement tables */
        public static final List<TerrainTableType> MOVEMENT_TABLES = Collections.unmodifiableList(Arrays.asList(MOVEMENT, MOVEMENT_RAIN, MOVEMENT_SNOW));

        /**
         * returns true if the given value means it is disabled for the current table type. i.e. healing of 0 means there is no healing.
         */
        public boolean isDisabled(int value) {
            return this.disabledValue == value;
        }

        /**
         * Returns true if the given value is the minimum for this table type.
         * Currently the only difference to {@link #isDisabled(int)} is for movement tables, where the disabled value is 255.
         */
        public boolean isMin(int value) {
            return this.min == value;
        }

        /**
         * Returns true if the given value is a "Safe" value for this table type.
         *
         * This method is intended to identify thing such as plains.
         * A plain has no avoid / defense / default movement value etc.
         */
        public boolean isSafe(int value) {
            return this.isDisabled(value) && !isMovementTable() || (this.isMin(value) && isMovementTable());
        }

        /**
         * Returns true if the the given value shouldn't change for this TableType.
         * The only example currently are walls in Movement tables.
         */
        public boolean mayNotChange(int value) {
            return this.isMovementTable() && isDisabled(value);
        }

        /**
         * Returns true if this TerrainTableType is a movement table
         */
        public boolean isMovementTable() {
            return MOVEMENT_TABLES.contains(this);
        }

        /**
         * Returns true if this TerrainTableType is a universally defined table
         */
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
        wasModified = true;
    }
}
