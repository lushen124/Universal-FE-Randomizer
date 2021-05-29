package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;

import fedata.general.FEBase.GameType;
import ui.model.MiscellaneousOptions;
import ui.model.MiscellaneousOptions.RewardMode;

public class MiscellaneousView extends Composite {
	
	private Group container;
	
	GameType type;
	
	private Button applyEnglishPatch; // pre-FE6 only
	private Button tripleEffectiveness; // FE7 only
	
	private Button randomizeChestVillageRewards;
	
	private MiscellaneousOptions.RewardMode rewardMode;
	
	private Composite rewardModeContainer;
	private Button similarRewardsButton;
	private Button randomRewardsButton;
	
	private Button enemyDropsButton;
	private Label enemyDropChanceLabel;
	private Spinner enemyDropChanceSpinner;
	
	// FE4 only.
	private Button followupRequirement;
	private Label withPursuitLabel;
	private Label withoutPursuitLabel;
	private Spinner withPursuitSpinner;
	private Spinner withoutPursuitSpinner;
	
	public MiscellaneousView(Composite parent, int style, GameType gameType) {
		super(parent, style);
		
		type = gameType;
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Miscellaneous");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		//////////////////////////////////////////////////////////////////
		
		Control lastControl = null;
		
		if (gameType.hasEnglishPatch()) {
			applyEnglishPatch = new Button(container, SWT.CHECK);
			applyEnglishPatch.setText("Apply English Patch");
			switch (gameType) {
			case FE4:
				applyEnglishPatch.setToolTipText("Applies the Project Naga localization patch.");
				break;
			case FE6:
				applyEnglishPatch.setToolTipText("Applies the FE6 Localization Patch v1.1.1.");
				break;
			default:
				break;
			}
			
			FormData patchData = new FormData();
			patchData.left = new FormAttachment(0, 5);
			patchData.top = new FormAttachment(0, 5);

			applyEnglishPatch.setLayoutData(patchData);
			
			lastControl = applyEnglishPatch;
		}
		
		if (gameType == GameType.FE7) {
			tripleEffectiveness = new Button(container, SWT.CHECK);
			tripleEffectiveness.setText("Set Effectiveness to 3x");
			tripleEffectiveness.setToolTipText("Reverts the weapon effectiveness to 3x like in the Japanese release, instead of 2x.");
			
			FormData effectivenessData = new FormData();
			effectivenessData.left = new FormAttachment(0, 5);
			if (lastControl == null) {
				effectivenessData.top = new FormAttachment(0, 5);
			} else {
				effectivenessData.top = new FormAttachment(lastControl, 10);
			}
			tripleEffectiveness.setLayoutData(effectivenessData);
			
			lastControl = tripleEffectiveness;
		}
		
		//////////////////////////////////////////////////////////////////
		
		
		randomizeChestVillageRewards = new Button(container, SWT.CHECK);
		if (gameType == GameType.FE4) {
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
		
		if (gameType == GameType.FE9) {
			rewardModeContainer = new Composite(container, SWT.NONE);
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
				public void handleEvent(Event event) { rewardMode = RewardMode.SIMILAR; }
			});
			
			randomRewardsButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) { rewardMode = RewardMode.RANDOM; }				
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
		if (gameType == GameType.FE9 || gameType == GameType.FE7 || gameType == GameType.FE8) {
			enemyDropsButton = new Button(container, SWT.CHECK);
			enemyDropsButton.setText("Add Random Enemy Drops");
			enemyDropsButton.setToolTipText("Gives a chance for random minions to drop weapons or a random item.");
			enemyDropsButton.setSelection(false);
			
			FormData dropData = new FormData();
			dropData.left = new FormAttachment(0, 5);
			dropData.top = new FormAttachment(previousControl, 10);
			enemyDropsButton.setLayoutData(dropData);
			
			enemyDropChanceSpinner = new Spinner(container, SWT.NONE);
			enemyDropChanceSpinner.setValues(10, 1, 100, 0, 1, 5);
			enemyDropChanceSpinner.setEnabled(false);
			
			FormData spinnerData = new FormData();
			spinnerData.right = new FormAttachment(100, -5);
			spinnerData.top = new FormAttachment(enemyDropsButton, 5);
			enemyDropChanceSpinner.setLayoutData(spinnerData);
			
			enemyDropChanceLabel = new Label(container, SWT.RIGHT);
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
			
			previousControl = enemyDropChanceSpinner;
		}
		
		if (gameType == GameType.FE4) {
			followupRequirement = new Button(container, SWT.CHECK);
			followupRequirement.setText("Remove Pursuit Follow-up Requirement");
			followupRequirement.setToolTipText("Modifies the battle system so that the Pursuit skill is not needed to make follow-up attacks.");
			followupRequirement.setSelection(false);
			
			FormData followupData = new FormData();
			followupData.left = new FormAttachment(0, 5);
			followupData.top = new FormAttachment(previousControl, 10);
			followupRequirement.setLayoutData(followupData);
			
			withoutPursuitSpinner = new Spinner(container, SWT.NONE);
			withoutPursuitSpinner.setValues(6, 1, 10, 0, 1, 1);
			withoutPursuitSpinner.setEnabled(false);
			withoutPursuitSpinner.setToolTipText("Sets the minimum Attack Speed advantage needed to perform follow-up attacks without the Pursuit skill.");
			
			FormData spinnerData = new FormData();
			spinnerData.right = new FormAttachment(100, -5);
			spinnerData.top = new FormAttachment(followupRequirement, 5);
			withoutPursuitSpinner.setLayoutData(spinnerData);
			
			withoutPursuitLabel = new Label(container, SWT.NONE);
			withoutPursuitLabel.setText("AS Threshold w/o Pursuit:");
			withoutPursuitLabel.setEnabled(false);
			
			FormData labelData = new FormData();
			labelData.right = new FormAttachment(withoutPursuitSpinner, -5);
			labelData.top = new FormAttachment(withoutPursuitSpinner, 0, SWT.CENTER);
			withoutPursuitLabel.setLayoutData(labelData);
			
			withPursuitSpinner = new Spinner(container, SWT.NONE);
			withPursuitSpinner.setValues(3, 1, 10, 0, 1, 1);
			withPursuitSpinner.setEnabled(false);
			withPursuitSpinner.setToolTipText("Sets the minimum Attack Speed advantage needed to perform follow-up attacks with the Pursuit skill.");
			
			spinnerData = new FormData();
			spinnerData.right = new FormAttachment(100, -5);
			spinnerData.top = new FormAttachment(withoutPursuitSpinner, 5);
			withPursuitSpinner.setLayoutData(spinnerData);
			
			withPursuitLabel = new Label(container, SWT.NONE);
			withPursuitLabel.setText("AS Threshold w/ Pursuit:");
			withPursuitLabel.setEnabled(false);
			
			labelData = new FormData();
			labelData.right = new FormAttachment(withPursuitSpinner, -5);
			labelData.top = new FormAttachment(withPursuitSpinner, 0, SWT.CENTER);
			withPursuitLabel.setLayoutData(labelData);
			
			followupRequirement.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					withoutPursuitLabel.setEnabled(followupRequirement.getSelection());
					withoutPursuitSpinner.setEnabled(followupRequirement.getSelection());
					withPursuitLabel.setEnabled(followupRequirement.getSelection());
					withPursuitSpinner.setEnabled(followupRequirement.getSelection());
				}
			});
			
			previousControl = withPursuitSpinner;
		}
	}
	
	public void setPatchingEnabled(boolean patchingEnabled) {
		if (applyEnglishPatch != null) {
			if (patchingEnabled) {
				applyEnglishPatch.setEnabled(true);
			} else {
				applyEnglishPatch.setEnabled(false);
				applyEnglishPatch.setSelection(false);
			}
		}
	}

	public MiscellaneousOptions getMiscellaneousOptions() {
		if (type.isGBA()) {
			switch (type) {
			case FE6:
				return new MiscellaneousOptions(applyEnglishPatch.getSelection(), randomizeChestVillageRewards.getSelection(), false);
			case FE7:
			default:
				return new MiscellaneousOptions(randomizeChestVillageRewards.getSelection(), enemyDropsButton.getSelection() ? enemyDropChanceSpinner.getSelection() : 0, tripleEffectiveness != null ? tripleEffectiveness.getSelection() : false);
			}
		} else if (type.isSFC()) {
			switch (type) {
			case FE4:
				return new MiscellaneousOptions(applyEnglishPatch.getSelection(), randomizeChestVillageRewards.getSelection(), new MiscellaneousOptions.FollowupRequirement(!followupRequirement.getSelection(), withPursuitSpinner.getSelection(), withoutPursuitSpinner.getSelection()));
			default:
				return new MiscellaneousOptions(false, 0, false);
			}
		} else if (type.isGCN()) {
			return new MiscellaneousOptions(false, randomizeChestVillageRewards.getSelection(), rewardMode, enemyDropsButton.getSelection() ? enemyDropChanceSpinner.getSelection() : 0);
		}
		
		return new MiscellaneousOptions(false, 0, false);
	}
	
	public void setMiscellaneousOptions(MiscellaneousOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			if (applyEnglishPatch != null) {
				applyEnglishPatch.setSelection(options.applyEnglishPatch);
			}
			if (tripleEffectiveness != null) {
				tripleEffectiveness.setSelection(options.tripleEffectiveness);
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
		}
	}
}
