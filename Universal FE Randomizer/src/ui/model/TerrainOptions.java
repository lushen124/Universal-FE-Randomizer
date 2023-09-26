package ui.model;

public class TerrainOptions {

    /*
     * Healing Related Settings
     */
    public final boolean randomizeHealing;
    public final int healingChance;
    public final MinMaxOption healingRange;

    /*
     * Status recovery Related Settings
     */
    public final boolean randomizeStatusRecovery;
    public final int statusRestoreChance;

    /*
     * avoid Related Settings
     */
    public final boolean randomizeAvoid;
    public final MinMaxOption avoidRange;
    public final int avoidChance;

    /*
     * Defense Related Settings
     */
    public final boolean randomizeDef;
    public final MinMaxOption defRange;
    public final int defChance;
    /*
     * Resistence Related Settings
     */
    public final boolean randomizeRes;
    public final MinMaxOption resRange;
    public final int resChance;
    /*
     * Movement Related Settings
     */
    public final boolean randomizeMovementCost;
    public final MinMaxOption movementCostRange;

    /*
     * Flag to keep tiles that didn't use to have effects safe
     */
    public final boolean keepSafeTiles;

    /*
     * Chance for a tile to get some kind of effect, otherwise it stays as it is in vanilla
     */
    public final int effectChance;

    public TerrainOptions(boolean randomizeHealing, int healingChance, MinMaxOption healingRange,
                          boolean randomizeStatusRecovery, int statusRestoreChance,
                          boolean randomizeAvoid, MinMaxOption avoidRange, int avoidChance,
                          boolean randomizeDef, MinMaxOption defRange, int defChance,
                          boolean randomizeRes, MinMaxOption resRange, int resChance,
                          boolean randomizeMovementCost, MinMaxOption movementCostRange,
                          boolean keepSafeTiles, int effectChance) {
        this.randomizeHealing = randomizeHealing;
        this.healingChance = healingChance;
        this.healingRange = healingRange;
        this.randomizeStatusRecovery = randomizeStatusRecovery;
        this.statusRestoreChance = statusRestoreChance;
        this.randomizeAvoid = randomizeAvoid;
        this.avoidRange = avoidRange;
        this.avoidChance = avoidChance;
        this.randomizeDef = randomizeDef;
        this.defRange = defRange;
        this.defChance = defChance;
        this.randomizeRes = randomizeRes;
        this.resRange = resRange;
        this.resChance = resChance;
        this.randomizeMovementCost = randomizeMovementCost;
        this.movementCostRange = movementCostRange;
        this.keepSafeTiles = keepSafeTiles;
        this.effectChance = effectChance;
    }
}
