package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import fedata.general.FEBase.GameType;
import ui.model.MiscellaneousOptions;

public class MiscellaneousView extends Composite {
	
	private Group container;
	
	GameType type;
	
	private Button applyEnglishPatch; // pre-FE6 only
	
	private Button randomizeChestVillageRewards;
	private Button randomizeRecruitmentOrder;
	
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

		//////////////////////////////////////////////////////////////////
	
		if (gameType != GameType.FE4) {
			randomizeRecruitmentOrder = new Button(container, SWT.CHECK);
			randomizeRecruitmentOrder.setText("Randomize Recruitment Order");
			randomizeRecruitmentOrder.setToolTipText("Mixes up the order in which characters join the party.");
			randomizeRecruitmentOrder.setEnabled(true);
			
			FormData randomRecruitData = new FormData();
			randomRecruitData.left = new FormAttachment(0, 5);
			randomRecruitData.top = new FormAttachment(randomizeChestVillageRewards, 5);
			randomizeRecruitmentOrder.setLayoutData(randomRecruitData);
		}
	}

	public MiscellaneousOptions getMiscellaneousOptions() {
		if (type.isGBA()) {
			switch (type) {
			case FE6:
				return new MiscellaneousOptions(applyEnglishPatch.getSelection(), randomizeChestVillageRewards.getSelection(), randomizeRecruitmentOrder.getSelection());
			case FE7:
			default:
				return new MiscellaneousOptions(randomizeChestVillageRewards.getSelection(), randomizeRecruitmentOrder.getSelection());
			}
		} else if (type.isSFC()) {
			switch (type) {
			case FE4:
				return new MiscellaneousOptions(applyEnglishPatch.getSelection(), randomizeChestVillageRewards.getSelection(), false);
			default:
				return new MiscellaneousOptions(false, false, false);
			}
		}
		
		return new MiscellaneousOptions(false, false, false);
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
			if (randomizeRecruitmentOrder != null) {
				randomizeRecruitmentOrder.setSelection(options.randomizeRecruitment);
			}
		}
	}
}
