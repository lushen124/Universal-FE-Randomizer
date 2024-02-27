package ui.model;

import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

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

    public void record(RecordKeeper rk, GameType type) {

        StringBuilder sb = new StringBuilder();
        if (GameType.FE4.equals(type)) {
            sb.append("Randomizer Rings: " + (randomizeRings ? "YES" : "NO"));
        } else {
            if (Boolean.TRUE.equals(randomizeRewards)) {
                sb.append("Randomization Mode: ");
                switch (rewardMode) {
                    case SIMILAR: sb.append("Similar rewards"); break;
                    case RANDOM: sb.append("Fully random"); break;
                }
                sb.append("<br>");
            }
            if (enemyDropChance != null) {
                sb.append("Adding random enemy drops with " + enemyDropChance + "% chance");
            }
        }
        rk.addHeaderItem("Reward Randomization", sb.toString());
    }
}
