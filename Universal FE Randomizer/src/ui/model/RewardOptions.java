package ui.model;

public class RewardOptions {
    public enum RewardMode {
        SIMILAR, RANDOM
    }
    public final RewardMode rewardMode;

    public final Boolean randomizeRewards;
    public final Integer enemyDropChance;

    public RewardOptions(RewardMode rewardMode, Boolean randomizeRewards, Integer enemyDropChance) {
        this.rewardMode = rewardMode;
        this.randomizeRewards = randomizeRewards;
        this.enemyDropChance = enemyDropChance;
    }
}
