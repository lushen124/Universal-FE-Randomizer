package ui.views;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import ui.common.GuiUtil;
import ui.model.RewardOptions;
import ui.model.RewardOptions.RewardMode;

public class RewardRandomizationView extends YuneView<RewardOptions> {

    private Button randomizeChestVillageRewards;

    private RewardMode rewardMode;

    private Composite rewardModeContainer;
    private Button similarRewardsButton;
    private Button randomRewardsButton;

    private Button enemyDropsButton;
    private Label enemyDropChanceLabel;
    private Spinner enemyDropChanceSpinner;

    public RewardRandomizationView(Composite parent, GameType gameType) {
        super(parent, gameType);
    }

    @Override
    public String getGroupTitle() {
        return "Rewards";
    }

    @Override
    public void compose() {
        Control lastControl = null;

        randomizeChestVillageRewards = new Button(group, SWT.CHECK);
        if (type == GameType.FE4) {
            randomizeChestVillageRewards.setText("Randomize Rings");
            randomizeChestVillageRewards.setToolTipText("Every instance of an obtainable ring is randomized to a different kind of ring.");
        } else {
            randomizeChestVillageRewards.setText("Randomize Rewards");
            randomizeChestVillageRewards.setToolTipText("Rewards from chests, villages, and story events will now give out random rewards. Plot-important promotion items are excluded.");
        }

        FormData chestVillageData = new FormData();
        chestVillageData.left = new FormAttachment(0, 5);
        if (lastControl != null) {
            chestVillageData.top = new FormAttachment(lastControl, 10);
        } else {
            chestVillageData.top = new FormAttachment(0, 5);
        }
        randomizeChestVillageRewards.setLayoutData(chestVillageData);

        Control previousControl = randomizeChestVillageRewards;

        if (type == GameType.FE9) {
            rewardModeContainer = new Composite(group, SWT.NONE);
            rewardModeContainer.setLayout(new FormLayout());

            FormData containerData = new FormData();
            containerData.left = new FormAttachment(randomizeChestVillageRewards, 5, SWT.LEFT);
            containerData.top = new FormAttachment(randomizeChestVillageRewards, 5);
            rewardModeContainer.setLayoutData(containerData);

            similarRewardsButton = new Button(rewardModeContainer, SWT.RADIO);
            similarRewardsButton.setText("Similar Replacements");
            similarRewardsButton.setToolTipText("Replaces rewards with those of a similar type.\ne.g. Weapons are replaced with weapons, stat boosters are replaced with other stat boosters, etc.");
            similarRewardsButton.setSelection(true);
            rewardMode = RewardMode.SIMILAR;

            FormData buttonData = new FormData();
            buttonData.left = new FormAttachment(0, 0);
            buttonData.top = new FormAttachment(0, 0);
            similarRewardsButton.setLayoutData(buttonData);

            randomRewardsButton = new Button(rewardModeContainer, SWT.RADIO);
            randomRewardsButton.setText("Random Replacements");
            randomRewardsButton.setToolTipText("Replaces rewards with anything.");

            buttonData = new FormData();
            buttonData.left = new FormAttachment(0, 0);
            buttonData.top = new FormAttachment(similarRewardsButton, 5);
            randomRewardsButton.setLayoutData(buttonData);

            similarRewardsButton.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    rewardMode = RewardMode.SIMILAR;
                }
            });

            randomRewardsButton.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    rewardMode = RewardMode.RANDOM;
                }
            });

            similarRewardsButton.setEnabled(false);
            randomRewardsButton.setEnabled(false);

            randomizeChestVillageRewards.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    similarRewardsButton.setEnabled(randomizeChestVillageRewards.getSelection());
                    randomRewardsButton.setEnabled(randomizeChestVillageRewards.getSelection());
                }
            });

            previousControl = rewardModeContainer;
        }

        // Random enemy drops
        if (type == GameType.FE9 || type == GameType.FE7 || type == GameType.FE8) {
            enemyDropsButton = new Button(group, SWT.CHECK);
            enemyDropsButton.setText("Add Random Enemy Drops");
            enemyDropsButton.setToolTipText("Gives a chance for random minions to drop weapons or a random item.");
            enemyDropsButton.setSelection(false);

            FormData dropData = new FormData();
            dropData.left = new FormAttachment(0, 5);
            dropData.top = new FormAttachment(previousControl, 10);
            enemyDropsButton.setLayoutData(dropData);

            enemyDropChanceSpinner = new Spinner(group, SWT.NONE);
            enemyDropChanceSpinner.setValues(10, 1, 100, 0, 1, 5);
            enemyDropChanceSpinner.setEnabled(false);

            FormData spinnerData = new FormData();
            spinnerData.right = new FormAttachment(100, -5);
            spinnerData.top = new FormAttachment(enemyDropsButton, 5);
            enemyDropChanceSpinner.setLayoutData(spinnerData);

            enemyDropChanceLabel = new Label(group, SWT.RIGHT);
            enemyDropChanceLabel.setText("Chance: ");
            enemyDropChanceLabel.setEnabled(false);

            FormData labelData = new FormData();
            labelData.right = new FormAttachment(enemyDropChanceSpinner, -5);
            labelData.top = new FormAttachment(enemyDropChanceSpinner, 0, SWT.CENTER);
            enemyDropChanceLabel.setLayoutData(labelData);

            enemyDropsButton.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    enemyDropChanceSpinner.setEnabled(enemyDropsButton.getSelection());
                    enemyDropChanceLabel.setEnabled(enemyDropsButton.getSelection());
                }
            });
        }
    }


    @Override
    public void initialize(RewardOptions options) {
        if (options == null) {
            return;
        }

        if (randomizeChestVillageRewards != null) {
            randomizeChestVillageRewards.setSelection(options.randomizeRewards);
        }

        if (similarRewardsButton != null) {
            similarRewardsButton.setSelection(options.rewardMode == RewardMode.SIMILAR);
            similarRewardsButton.setEnabled(options.randomizeRewards);
        }
        if (randomRewardsButton != null) {
            randomRewardsButton.setSelection(options.rewardMode == RewardMode.RANDOM);
            randomRewardsButton.setEnabled(options.randomizeRewards);
        }

        if (enemyDropsButton != null && enemyDropChanceSpinner != null) {
            enemyDropsButton.setSelection(options.enemyDropChance > 0);
            enemyDropChanceSpinner.setEnabled(options.enemyDropChance > 0);
            enemyDropChanceLabel.setEnabled(options.enemyDropChance > 0);
            if (options.enemyDropChance > 0) {
                enemyDropChanceSpinner.setSelection(options.enemyDropChance);
            }
        }

        rewardMode = options.rewardMode;
    }

    @Override
    public RewardOptions getOptions() {
        if (GameType.FE4.equals(type)) {
            return new RewardOptions(randomizeChestVillageRewards.getSelection());
        } else if (GameType.FE9.equals(type)) {
            return new RewardOptions(rewardMode, randomRewardsButton.getSelection(), enemyDropsButton.getSelection() == true ? enemyDropChanceSpinner.getSelection() : 0);
        }

        return new RewardOptions(randomizeChestVillageRewards.getSelection(), !GameType.FE6.equals(type) && enemyDropsButton.getSelection() == true ? enemyDropChanceSpinner.getSelection() : 0);
    }

}
