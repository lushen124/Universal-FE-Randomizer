package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import fedata.general.FEBase.GameType;
import ui.model.MiscellaneousOptions;
import ui.model.MiscellaneousOptions.RewardMode;

public class MiscellaneousView extends Composite {
	
	private Group container;
	
	GameType type;
	
	private Button applyEnglishPatch; // pre-FE6 only
	
	private Button randomizeChestVillageRewards;
	
	private MiscellaneousOptions.RewardMode rewardMode;
	
	private Composite rewardModeContainer;
	private Button similarRewardsButton;
	private Button randomRewardsButton;
	
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
		
		if (gameType.hasEnglishPatch()) {
			applyEnglishPatch = new Button(container, SWT.CHECK);
			applyEnglishPatch.setText("Apply English Patch");
			applyEnglishPatch.setToolTipText("Given a raw Japanese version of the game, apply the localization patch from Serenes Forest on it. The result is an English version of the game.");
			
			FormData patchData = new FormData();
			patchData.left = new FormAttachment(0, 5);
			patchData.top = new FormAttachment(0, 5);
			applyEnglishPatch.setLayoutData(patchData);
		}
		
		//////////////////////////////////////////////////////////////////
		
		
		randomizeChestVillageRewards = new Button(container, SWT.CHECK);
		if (gameType == GameType.FE4) {
			randomizeChestVillageRewards.setText("Randomize Rings");
			randomizeChestVillageRewards.setToolTipText("Every instance of obtainable ring is randomized to a different kind of ring.");
		} else {
			randomizeChestVillageRewards.setText("Randomize Rewards");
			randomizeChestVillageRewards.setToolTipText("Rewards from chests, villages, and story events will now give out random rewards. Plot-important promotion items are excluded.");
		}
		
		FormData chestVillageData = new FormData();
		chestVillageData.left = new FormAttachment(0, 5);
		if (gameType.hasEnglishPatch()) {
			chestVillageData.top = new FormAttachment(applyEnglishPatch, 5);
		} else {
			chestVillageData.top = new FormAttachment(0, 5);
		}
		randomizeChestVillageRewards.setLayoutData(chestVillageData);
		
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
		}
	}

	public MiscellaneousOptions getMiscellaneousOptions() {
		if (type.isGBA()) {
			switch (type) {
			case FE6:
				return new MiscellaneousOptions(applyEnglishPatch.getSelection(), randomizeChestVillageRewards.getSelection());
			case FE7:
			default:
				return new MiscellaneousOptions(randomizeChestVillageRewards.getSelection());
			}
		} else if (type.isSFC()) {
			switch (type) {
			case FE4:
				return new MiscellaneousOptions(applyEnglishPatch.getSelection(), randomizeChestVillageRewards.getSelection());
			default:
				return new MiscellaneousOptions(false, false);
			}
		} else if (type.isGCN()) {
			return new MiscellaneousOptions(false, randomizeChestVillageRewards.getSelection(), rewardMode);
		}
		
		return new MiscellaneousOptions(false, false);
	}
	
	public void setMiscellaneousOptions(MiscellaneousOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			if (applyEnglishPatch != null) {
				applyEnglishPatch.setSelection(options.applyEnglishPatch);
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
		}
	}
}
