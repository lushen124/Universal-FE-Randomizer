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

import fedata.general.FEBase.GameType;
import ui.general.MinMaxControl;
import ui.model.MiscellaneousOptions;
import ui.model.MiscellaneousOptions.ExperienceRate;
import ui.model.MiscellaneousOptions.RewardMode;

public class MiscellaneousView extends Composite {
	
	private Group container;
	
	GameType type;
	
	private Button applyEnglishPatch; // pre-FE6 only
	private Button tripleEffectiveness; // FE7 only
	
	private Button randomizeChestVillageRewards;
	
	private MiscellaneousOptions.RewardMode rewardMode;
	private MiscellaneousOptions.ExperienceRate experienceRate;
	
	private Composite rewardModeContainer;
	private Button similarRewardsButton;
	private Button randomRewardsButton;
	
	public Button singleRNButton;
	
	private Button enemyDropsButton;
	private Label enemyDropChanceLabel;
	private Spinner enemyDropChanceSpinner;
	
	private Button addFogOfWarButton;
	private Label fogOfWarChanceLabel;
	private Spinner fogOfWarChanceSpinner;
	private MinMaxControl fogOfWarVisionRangeControl;
	
	private Button casualModeButton;
	
	private Group experienceRateGroup;
	private Button normalExperienceButton;
	private Button paragonButton;
	private Button renegadeButton;
	
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
		
		if (gameType.isGBA()) {
			singleRNButton = new Button(container, SWT.CHECK);
			singleRNButton.setText("Enable Single RN for Hit");
			singleRNButton.setToolTipText("Makes accuracy rolls based on a single random number instead of the average of two random numbers.\n\nGood for those that don't like being lied to about hit rates.");
			singleRNButton.setSelection(false);
			
			FormData rnData = new FormData();
			rnData.left = new FormAttachment(0, 5);
			rnData.top = new FormAttachment(previousControl, 10);
			singleRNButton.setLayoutData(rnData);
			
			previousControl = singleRNButton;
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
		
		// Fog of War
		if (gameType.isGBA()) {
			addFogOfWarButton = new Button(container, SWT.CHECK);
			addFogOfWarButton.setText("Add Fog of War (Beta)");
			if (gameType == GameType.FE7) {
				addFogOfWarButton.setToolTipText("Adds a chance for maps to feature fog of war.\n\nMaps featuring Kishuna are exempt.");
			} else {
				addFogOfWarButton.setToolTipText("Adds a chance for maps to have fog of war.");
			}
			addFogOfWarButton.setSelection(false);
			addFogOfWarButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					fogOfWarChanceSpinner.setEnabled(addFogOfWarButton.getSelection());
					fogOfWarChanceLabel.setEnabled(addFogOfWarButton.getSelection());
					fogOfWarVisionRangeControl.setEnabled(addFogOfWarButton.getSelection());
				}	
			});
			
			FormData formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(previousControl, 10);
			addFogOfWarButton.setLayoutData(formData);
			
			fogOfWarChanceSpinner = new Spinner(container, SWT.CHECK);
			fogOfWarChanceSpinner.setValues(10, 1, 100, 0, 1, 1);
			fogOfWarChanceSpinner.setEnabled(false);
			
			formData = new FormData();
			formData.right = new FormAttachment(100, -5);
			formData.top = new FormAttachment(addFogOfWarButton, 5);
			fogOfWarChanceSpinner.setLayoutData(formData);
			
			fogOfWarChanceLabel = new Label(container, SWT.NONE);
			fogOfWarChanceLabel.setText("Fog of War Chance:");
			fogOfWarChanceLabel.setEnabled(false);
			
			formData = new FormData();
			formData.right = new FormAttachment(fogOfWarChanceSpinner, -5);
			formData.top = new FormAttachment(fogOfWarChanceSpinner, 0, SWT.CENTER);
			fogOfWarChanceLabel.setLayoutData(formData);
			
			fogOfWarVisionRangeControl = new MinMaxControl(container, SWT.NONE, "Vision Range", "~");
			fogOfWarVisionRangeControl.setMin(3);
			fogOfWarVisionRangeControl.getMinSpinner().setValues(3, 1, 6, 0, 1, 1);
			fogOfWarVisionRangeControl.setMax(6);
			fogOfWarVisionRangeControl.getMaxSpinner().setValues(6, 3, 15, 0, 1, 1);
			fogOfWarVisionRangeControl.setEnabled(false);
			
			formData = new FormData();
			formData.right = new FormAttachment(100, 0);
			formData.top = new FormAttachment(fogOfWarChanceSpinner, 5);
			formData.left = new FormAttachment(addFogOfWarButton, 5, SWT.LEFT);
			fogOfWarVisionRangeControl.setLayoutData(formData);
			
			previousControl = fogOfWarVisionRangeControl;
		}
		
		if (gameType.isGBA()) {
			casualModeButton = new Button(container, SWT.CHECK);
			casualModeButton.setText("Enable Casual Mode");
			casualModeButton.setToolTipText("Disables permadeath. Defeated playable characters are available again in the next chapter.\n\nThe normal Game Over triggers are still active (i.e. Lord defeated).");
			casualModeButton.setSelection(false);
			
			FormData formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(previousControl, 10);
			casualModeButton.setLayoutData(formData);
			
			experienceRateGroup = new Group(container, SWT.NONE);
			experienceRateGroup.setText("Experience Rate");
			
			FormLayout experinceLayout = new FormLayout();
			experinceLayout.marginLeft = 5;
			experinceLayout.marginTop = 5;
			experinceLayout.marginBottom = 5;
			experinceLayout.marginRight = 5;
			experienceRateGroup.setLayout(experinceLayout);
			
			formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.right = new FormAttachment(100, 5);
			formData.top = new FormAttachment(casualModeButton, 10);
			experienceRateGroup.setLayoutData(formData);
			
			normalExperienceButton = new Button(experienceRateGroup, SWT.RADIO);
			normalExperienceButton.setText("Normal");
			normalExperienceButton.setToolTipText("Normal Experience Gain.");
			normalExperienceButton.setSelection(true);
			experienceRate = ExperienceRate.NORMAL;
			
			normalExperienceButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					experienceRate = ExperienceRate.NORMAL;
				}
			});
			
			formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(0, 5);
			normalExperienceButton.setLayoutData(formData);
			
			paragonButton = new Button(experienceRateGroup, SWT.RADIO);
			paragonButton.setText("Paragon Mode");
			paragonButton.setToolTipText("Doubles Experience Gain.");
			paragonButton.setSelection(false);
			paragonButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					experienceRate = ExperienceRate.PARAGON;
				}
			});
			
			formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(normalExperienceButton, 5);
			paragonButton.setLayoutData(formData);
			
			renegadeButton = new Button(experienceRateGroup, SWT.RADIO);
			renegadeButton.setText("Renegade Mode");
			renegadeButton.setToolTipText("Halves Experience Gain.");
			renegadeButton.setSelection(false);
			renegadeButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					experienceRate = ExperienceRate.RENEGADE;
				}
			});
			
			formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(paragonButton, 5);
			renegadeButton.setLayoutData(formData);
			
			previousControl = experienceRateGroup;
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
				return new MiscellaneousOptions(applyEnglishPatch.getSelection(), randomizeChestVillageRewards.getSelection(), false, singleRNButton.getSelection(), addFogOfWarButton.getSelection(), fogOfWarChanceSpinner.getSelection(), fogOfWarVisionRangeControl.getMinMaxOption(), casualModeButton.getSelection(), experienceRate);
			case FE7:
			default:
				return new MiscellaneousOptions(randomizeChestVillageRewards.getSelection(), enemyDropsButton.getSelection() ? enemyDropChanceSpinner.getSelection() : 0, tripleEffectiveness != null ? tripleEffectiveness.getSelection() : false, singleRNButton.getSelection(), addFogOfWarButton.getSelection(), fogOfWarChanceSpinner.getSelection(), fogOfWarVisionRangeControl.getMinMaxOption(), casualModeButton.getSelection(), experienceRate);
			}
		} else if (type.isSFC()) {
			switch (type) {
			case FE4:
				return new MiscellaneousOptions(applyEnglishPatch.getSelection(), randomizeChestVillageRewards.getSelection(), new MiscellaneousOptions.FollowupRequirement(!followupRequirement.getSelection(), withPursuitSpinner.getSelection(), withoutPursuitSpinner.getSelection()));
			default:
				return new MiscellaneousOptions(false, 0, false, false, false, 0, null, false, ExperienceRate.NORMAL);
			}
		} else if (type.isGCN()) {
			return new MiscellaneousOptions(false, randomizeChestVillageRewards.getSelection(), rewardMode, enemyDropsButton.getSelection() ? enemyDropChanceSpinner.getSelection() : 0);
		}
		return new MiscellaneousOptions(false, 0, false, false, false, 0, null, false, ExperienceRate.NORMAL);
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
			
			if (singleRNButton != null) {
				singleRNButton.setSelection(options.singleRNMode);
			}
			
			if (enemyDropsButton != null && enemyDropChanceSpinner != null) {
				enemyDropsButton.setSelection(options.enemyDropChance > 0);
				enemyDropChanceSpinner.setEnabled(options.enemyDropChance > 0);
				enemyDropChanceLabel.setEnabled(options.enemyDropChance > 0);
				if (options.enemyDropChance > 0) {
					enemyDropChanceSpinner.setSelection(options.enemyDropChance);
				}
			}
			
			if (addFogOfWarButton != null) {
				addFogOfWarButton.setSelection(options.randomizeFogOfWar);
				
				fogOfWarChanceSpinner.setEnabled(true);
				fogOfWarChanceLabel.setEnabled(true);
				fogOfWarVisionRangeControl.setEnabled(true);
				
				fogOfWarChanceSpinner.setSelection(options.fogOfWarChance);
				fogOfWarVisionRangeControl.setMin(options.fogOfWarVisionRange.minValue);
				fogOfWarVisionRangeControl.setMax(options.fogOfWarVisionRange.maxValue);
			}
			
			if (casualModeButton != null) {
				casualModeButton.setSelection(options.casualMode);
			}
			
			if (experienceRateGroup != null) {
				normalExperienceButton.setSelection(false);
				paragonButton.setSelection(false);
				renegadeButton.setSelection(false);
				
				switch (options.experienceRate) {
				case NORMAL:
					normalExperienceButton.setSelection(true);
					break;
				case PARAGON:
					paragonButton.setSelection(true);
					break;
				case RENEGADE:
					renegadeButton.setSelection(true);
					break;
				}
			}
			
			experienceRate = options.experienceRate;
			rewardMode = options.rewardMode;
		}
	}
}
