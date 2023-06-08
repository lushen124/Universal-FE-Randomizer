package ui.model;

import static ui.model.RewardOptions.RewardMode.SIMILAR;

public class RewardOptions {
    public enum RewardMode {
        SIMILAR, RANDOM
    }
    public final RewardMode rewardMode;

    public final Boolean randomizeRewards;
    public final Integer enemyDropChance;

    public final Boolean randomizeRings;

    /**
     * Constructor for FE4
     */
    public RewardOptions(Boolean randomizeRings) {
        this.randomizeRings = randomizeRings;
        this.rewardMode = SIMILAR;
        this.enemyDropChance = 0;
        this.randomizeRewards = false;
    }

    /**
     * Constructor for the GBAFE Games
     */
    public RewardOptions(Boolean randomizeRewards, Integer enemyDropChance) {
        this.rewardMode = SIMILAR;
        this.randomizeRewards = randomizeRewards;
        this.enemyDropChance = enemyDropChance;
        this.randomizeRings = false;
    }

    /**
     * Constructor for FE9
     */
    public RewardOptions(RewardMode rewardMode, Boolean randomizeRewards, Integer enemyDropChance) {
        this.rewardMode = rewardMode;
        this.randomizeRewards = randomizeRewards;
        this.enemyDropChance = enemyDropChance;
        this.randomizeRings = false;
    }
}
