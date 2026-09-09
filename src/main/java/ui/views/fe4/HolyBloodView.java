package ui.views.fe4;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import ui.common.GuiUtil;
import ui.model.fe4.HolyBloodOptions;
import ui.model.fe4.HolyBloodOptions.STRMAGOptions;
import ui.views.YuneView;

public class HolyBloodView extends YuneView<HolyBloodOptions> {
	
	
	private Button randomizeGrowthBonusesButton;
	private Label growthTotalLabel;
	private Spinner growthBonusTotalSpinner;
	private Label chunkSizeLabel;
	private Spinner chunkSizeSpinner;
	private Label hpBaselineLabel;
	private Spinner hpBaselineSpinner;
	private Button uniqueBonusesButton;
	private Group strMagGroup;
	private Button noLimitButton;
	private Button adjustButton;
	private Button limitButton;
	
	private Button randomizeHolyWeaponBonusesButton;
	
	private Button giveHolyBlood;
	private Button matchClass;
	
	private Label majorBloodLabel;
	private Spinner majorBloodChance;
	private Label minorBloodLabel;
	private Spinner minorBloodChance;
	private Label noBloodLabel;
	private Spinner noBloodChance;
	
	public HolyBloodView(Composite parent) {
		super(parent);
	}

	@Override
	public String getGroupTitle() {
		return "Holy Blood";
	}

	@Override
	public String getGroupTooltip() {
		return "Randomizes the properties of Holy Blood.";
	}

	@Override
	protected void compose() {
		randomizeGrowthBonusesButton = new Button(group, SWT.CHECK);
		randomizeGrowthBonusesButton.setText("Randomize Growth Bonuses");
		randomizeGrowthBonusesButton.setToolTipText("Randomly assigns the growth bonuses bestowed on those with Minor or Major Holy Blood.");
		randomizeGrowthBonusesButton.setEnabled(true);
		randomizeGrowthBonusesButton.setSelection(false);
		randomizeGrowthBonusesButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				boolean enabled = randomizeGrowthBonusesButton.getSelection();
				
				growthTotalLabel.setEnabled(enabled);
				growthBonusTotalSpinner.setEnabled(enabled);
				
				chunkSizeLabel.setEnabled(enabled);
				chunkSizeSpinner.setEnabled(enabled);
				
				hpBaselineLabel.setEnabled(enabled);
				hpBaselineSpinner.setEnabled(enabled);
				
				uniqueBonusesButton.setEnabled(enabled);
				
				strMagGroup.setEnabled(enabled);
				noLimitButton.setEnabled(enabled);
				adjustButton.setEnabled(enabled);
				limitButton.setEnabled(enabled);
			}
		});
		
		Composite growthBonusParameterContainer = new Composite(group, 0);
		growthBonusParameterContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		growthTotalLabel = new Label(growthBonusParameterContainer, SWT.RIGHT);
		growthTotalLabel.setText("Growth Bonus Total:");
		growthTotalLabel.setEnabled(false);
		
		growthBonusTotalSpinner = new Spinner(growthBonusParameterContainer, SWT.READ_ONLY);
		growthBonusTotalSpinner.setValues(50, 0, 100, 0, 5, 10);
		growthBonusTotalSpinner.setEnabled(false);
		growthBonusTotalSpinner.setToolTipText("The total bonus for each holy blood granted to those with Minor Blood. The bonus is doubled for Major Blood.");
		growthBonusTotalSpinner.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int newTotal = growthBonusTotalSpinner.getSelection();
				int chunkSize = chunkSizeSpinner.getSelection();
				if (newTotal < chunkSize) {
					chunkSize = newTotal;
				}
				chunkSizeSpinner.setValues(chunkSize, 5, newTotal, 0, 5, 10);
				
				int hpBaseline = hpBaselineSpinner.getSelection();
				if (newTotal < hpBaseline) {
					hpBaseline = newTotal;
				}
				hpBaselineSpinner.setValues(hpBaseline, 0, newTotal, 0, 5, 10);
			}
		});
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.right = new FormAttachment(growthBonusTotalSpinner, -5);
		labelData.top = new FormAttachment(growthBonusTotalSpinner, 0, SWT.CENTER);
		growthTotalLabel.setLayoutData(labelData);
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		growthBonusTotalSpinner.setLayoutData(spinnerData);
		
		chunkSizeLabel = new Label(growthBonusParameterContainer, SWT.RIGHT);
		chunkSizeLabel.setText("Chunk Size:");
		chunkSizeLabel.setEnabled(false);
		
		chunkSizeSpinner = new Spinner(growthBonusParameterContainer, SWT.READ_ONLY);
		chunkSizeSpinner.setValues(5, 5, 50, 0, 5, 10);
		chunkSizeSpinner.setEnabled(false);
		chunkSizeSpinner.setToolTipText("Determines how much growth is distributed in each pass. Each growth area will be a multiple of this value except when distributing the remainder.");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.right = new FormAttachment(chunkSizeSpinner, -5);
		labelData.top = new FormAttachment(chunkSizeSpinner, 0, SWT.CENTER);
		chunkSizeLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.top = new FormAttachment(growthBonusTotalSpinner, 5);
		chunkSizeSpinner.setLayoutData(spinnerData);
		
		hpBaselineLabel = new Label(growthBonusParameterContainer, SWT.RIGHT);
		hpBaselineLabel.setText("HP Baseline:");
		hpBaselineLabel.setEnabled(false);
		
		hpBaselineSpinner = new Spinner(growthBonusParameterContainer, SWT.READ_ONLY);
		hpBaselineSpinner.setValues(0, 0, 50, 0, 5, 10);
		hpBaselineSpinner.setEnabled(false);
		hpBaselineSpinner.setToolTipText("Determines what the HP bonus for all bloods start as.\nIf 0, HP is treated like a normal stat area for bonuses.\nIf not 0, HP has reduced weight when assigning bonuses.");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.right = new FormAttachment(hpBaselineSpinner, -5);
		labelData.top = new FormAttachment(hpBaselineSpinner, 0, SWT.CENTER);
		hpBaselineLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.top = new FormAttachment(chunkSizeSpinner, 5);
		hpBaselineSpinner.setLayoutData(spinnerData);
		
		uniqueBonusesButton = new Button(growthBonusParameterContainer, SWT.CHECK);
		uniqueBonusesButton.setText("Generate Unique Bonuses");
		uniqueBonusesButton.setToolTipText("Attempts to make sure no two bloods grant similar bonuses.");
		uniqueBonusesButton.setSelection(false);
		uniqueBonusesButton.setEnabled(false);
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(hpBaselineSpinner, 5);
		uniqueBonusesButton.setLayoutData(optionData);
		
		strMagGroup = new Group(growthBonusParameterContainer, SWT.NONE);
		strMagGroup.setText("STR/MAG");
		strMagGroup.setLayout(GuiUtil.formLayoutWithMargin());
		
		noLimitButton = new Button(strMagGroup, SWT.RADIO);
		noLimitButton.setText("No Limitations");
		noLimitButton.setToolTipText("Freely assigns STR/MAG, regardless of whether the blood is physical or magical.");
		noLimitButton.setEnabled(false);
		noLimitButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		noLimitButton.setLayoutData(optionData);
		
		adjustButton = new Button(strMagGroup, SWT.RADIO);
		adjustButton.setText("Adjust to Blood");
		adjustButton.setToolTipText("Freely assigns STR/MAG, but ensures STR is the higher stat for physical bloods and MAG is the higher stat for magical bloods.");
		adjustButton.setEnabled(false);
		adjustButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(noLimitButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(noLimitButton, 5);
		adjustButton.setLayoutData(optionData);
		
		limitButton = new Button(strMagGroup, SWT.RADIO);
		limitButton.setText("Limit to Blood");
		limitButton.setToolTipText("Do not allow STR to be assigned to magical bloods or MAG to be assigned to physical bloods.");
		limitButton.setEnabled(false);
		limitButton.setSelection(true);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(adjustButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(adjustButton, 5);
		limitButton.setLayoutData(optionData);
		
		FormData containerData = new FormData();
		containerData.top = new FormAttachment(uniqueBonusesButton, 5);
		containerData.left = new FormAttachment(uniqueBonusesButton, 0, SWT.LEFT);
		containerData.right = new FormAttachment(100, -5);
		strMagGroup.setLayoutData(containerData);
		
		containerData = new FormData();
		containerData.top = new FormAttachment(randomizeGrowthBonusesButton, 0);
		containerData.left = new FormAttachment(randomizeGrowthBonusesButton, 10, SWT.LEFT);
		containerData.right = new FormAttachment(100, -5);
		growthBonusParameterContainer.setLayoutData(containerData);
		
		/////////////////////////////////////////////////////////////
		
		randomizeHolyWeaponBonusesButton = new Button(group, SWT.CHECK);
		randomizeHolyWeaponBonusesButton.setText("Randomize Holy Weapon Bonuses");
		randomizeHolyWeaponBonusesButton.setToolTipText("Randomizes the bonuses granted when Holy Weapons are equipped. Does not affect Naga and Loptous.");
		randomizeHolyWeaponBonusesButton.setEnabled(true);
		randomizeHolyWeaponBonusesButton.setSelection(false);
		
		FormData holyWeaponBonusData = new FormData();
		holyWeaponBonusData.left = new FormAttachment(growthBonusParameterContainer, 0, SWT.LEFT);
		holyWeaponBonusData.top = new FormAttachment(growthBonusParameterContainer, 5);
		randomizeHolyWeaponBonusesButton.setLayoutData(holyWeaponBonusData);
		
		giveHolyBlood = new Button(group, SWT.CHECK);
		giveHolyBlood.setText("Assign Holy Blood to Playable Characters");
		giveHolyBlood.setToolTipText("Assigns either Major or Minor Holy Blood to all Playable Characters.\n\nThose that already have Major Holy Blood are unaffected.\nThose that have Minor Holy Blood may gain an additional Minor Blood or convert into Major Blood.\nThose with none have a chance of either Major or Minor Blood.\n\nApplies to all non-child characters.");
		giveHolyBlood.setEnabled(true);
		giveHolyBlood.setSelection(false);
		giveHolyBlood.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				matchClass.setEnabled(giveHolyBlood.getSelection());
				majorBloodLabel.setEnabled(giveHolyBlood.getSelection());
				majorBloodChance.setEnabled(giveHolyBlood.getSelection());
				minorBloodLabel.setEnabled(giveHolyBlood.getSelection());
				minorBloodChance.setEnabled(giveHolyBlood.getSelection());
				noBloodLabel.setEnabled(giveHolyBlood.getSelection());
			}
		});
		
		FormData giveBloodData = new FormData();
		giveBloodData.left = new FormAttachment(randomizeHolyWeaponBonusesButton, 0, SWT.LEFT);
		giveBloodData.top = new FormAttachment(randomizeHolyWeaponBonusesButton, 10);
		giveHolyBlood.setLayoutData(giveBloodData);
		
		matchClass = new Button(group, SWT.CHECK);
		matchClass.setText("Match Blood to Weapon Usage");
		matchClass.setToolTipText("When assigning a character with no holy blood, assigns a blood that matches their weapon type.\n\nFor characters with minor blood, a blood of a different weapon type will be used.");
		matchClass.setEnabled(false);
		matchClass.setSelection(false);
		
		FormData matchData = new FormData();
		matchData.left = new FormAttachment(giveHolyBlood, 10, SWT.LEFT);
		matchData.top = new FormAttachment(giveHolyBlood, 5);
		matchClass.setLayoutData(matchData);
		
		Composite giveBloodContainer = new Composite(group, 0);
		giveBloodContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		majorBloodLabel = new Label(giveBloodContainer, SWT.RIGHT);
		majorBloodLabel.setText("Major Blood Chance:");
		majorBloodLabel.setEnabled(false);
		
		majorBloodChance = new Spinner(giveBloodContainer, SWT.NONE);
		majorBloodChance.setValues(50, 0, 100, 0, 1, 1);
		majorBloodChance.setEnabled(false);
		majorBloodChance.setToolTipText("The chance of a character obtaining Major Holy Blood.");
		majorBloodChance.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (majorBloodChance.getSelection() + minorBloodChance.getSelection() < 100) {
					noBloodChance.setValues(100 - majorBloodChance.getSelection() - minorBloodChance.getSelection(), 0, 100, 0, 1, 1);
				} else {
					noBloodChance.setValues(0, 0, 100, 0, 1, 1);
					minorBloodChance.setValues(100 - majorBloodChance.getSelection(), 0, 100, 0, 1, 1);
				}
			}
		});
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(majorBloodChance, -5);
		labelData.top = new FormAttachment(majorBloodChance, 0, SWT.CENTER);
		majorBloodLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		majorBloodChance.setLayoutData(spinnerData);
		
		minorBloodLabel = new Label(giveBloodContainer, SWT.RIGHT);
		minorBloodLabel.setText("Minor Blood Chance:");
		minorBloodLabel.setEnabled(false);
		
		minorBloodChance = new Spinner(giveBloodContainer, SWT.NONE);
		minorBloodChance.setValues(50, 0, 100, 0, 1, 1);
		minorBloodChance.setEnabled(false);
		minorBloodChance.setToolTipText("The chance of a character obtaining Minor Holy Blood.");
		minorBloodChance.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (minorBloodChance.getSelection() + majorBloodChance.getSelection() < 100) {
					noBloodChance.setValues(100 - majorBloodChance.getSelection() - minorBloodChance.getSelection(), 0, 100, 0, 1, 1);
				} else {
					noBloodChance.setValues(0, 0, 100, 0, 1, 1);
					majorBloodChance.setValues(100 - minorBloodChance.getSelection(), 0, 100, 0, 1, 1);
				}
			}
		});
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(minorBloodChance, -5);
		labelData.top = new FormAttachment(minorBloodChance, 0, SWT.CENTER);
		minorBloodLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.left = new FormAttachment(majorBloodChance, 0, SWT.LEFT);
		spinnerData.top = new FormAttachment(majorBloodChance, 5);
		minorBloodChance.setLayoutData(spinnerData);
		
		noBloodLabel = new Label(giveBloodContainer, SWT.RIGHT);
		noBloodLabel.setText("[Calculated] No Blood Chance:");
		noBloodLabel.setEnabled(false);
		
		noBloodChance = new Spinner(giveBloodContainer, SWT.NONE);
		noBloodChance.setValues(0, 0, 100, 0, 1, 1);
		noBloodChance.setEnabled(false);
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(noBloodChance, -5);
		labelData.top = new FormAttachment(noBloodChance, 0, SWT.CENTER);
		noBloodLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.left = new FormAttachment(minorBloodChance, 0, SWT.LEFT);
		spinnerData.top = new FormAttachment(minorBloodChance, 5);
		noBloodChance.setLayoutData(spinnerData);
		
		containerData = new FormData();
		containerData.top = new FormAttachment(matchClass, 0);
		containerData.left = new FormAttachment(matchClass, 0, SWT.LEFT);
		containerData.right = new FormAttachment(100, -5);
		giveBloodContainer.setLayoutData(containerData);
	}

	@Override
	public HolyBloodOptions getOptions() {
		HolyBloodOptions.STRMAGOptions strMag = STRMAGOptions.NO_LIMIT;
		if (adjustButton.getSelection()) { strMag = STRMAGOptions.ADJUST_STR_MAG; }
		else if (limitButton.getSelection()) { strMag = STRMAGOptions.LIMIT_STR_MAG; }
		
		return new HolyBloodOptions(randomizeGrowthBonusesButton.getSelection(), growthBonusTotalSpinner.getSelection(), chunkSizeSpinner.getSelection(), hpBaselineSpinner.getSelection(), strMag, uniqueBonusesButton.getSelection(), 
				randomizeHolyWeaponBonusesButton.getSelection(), 
				giveHolyBlood.getSelection(), matchClass.getSelection(), majorBloodChance.getSelection(), minorBloodChance.getSelection());
	}

	@Override
	public void initialize(HolyBloodOptions options) {
		if (options == null) {
			// Shouldn't happen.
			return;
		}

		boolean growthBonusesEnabled = options.randomizeGrowthBonuses;
		randomizeGrowthBonusesButton.setSelection(growthBonusesEnabled);
		growthTotalLabel.setEnabled(growthBonusesEnabled);
		growthBonusTotalSpinner.setEnabled(growthBonusesEnabled);
		growthBonusTotalSpinner.setSelection(options.growthTotal);
		chunkSizeLabel.setEnabled(growthBonusesEnabled);
		chunkSizeSpinner.setEnabled(growthBonusesEnabled);
		chunkSizeSpinner.setSelection(Math.max(5, options.chunkSize));
		hpBaselineLabel.setEnabled(growthBonusesEnabled);
		hpBaselineSpinner.setEnabled(growthBonusesEnabled);
		hpBaselineSpinner.setSelection(options.hpBaseline);
		uniqueBonusesButton.setEnabled(growthBonusesEnabled);
		uniqueBonusesButton.setSelection(options.generateUniqueBonuses);

		strMagGroup.setEnabled(growthBonusesEnabled);
		noLimitButton.setEnabled(growthBonusesEnabled);
		adjustButton.setEnabled(growthBonusesEnabled);
		limitButton.setEnabled(growthBonusesEnabled);

		noLimitButton.setSelection(options.strMagOptions == STRMAGOptions.NO_LIMIT);
		adjustButton.setSelection(options.strMagOptions == STRMAGOptions.ADJUST_STR_MAG);
		limitButton.setSelection(options.strMagOptions == null || options.strMagOptions == STRMAGOptions.LIMIT_STR_MAG);

		randomizeHolyWeaponBonusesButton.setSelection(options.randomizeWeaponBonuses);

		giveHolyBlood.setSelection(options.giveHolyBlood);

		matchClass.setEnabled(options.giveHolyBlood);
		majorBloodChance.setEnabled(options.giveHolyBlood);
		minorBloodChance.setEnabled(options.giveHolyBlood);

		majorBloodLabel.setEnabled(options.giveHolyBlood);
		minorBloodLabel.setEnabled(options.giveHolyBlood);
		noBloodLabel.setEnabled(options.giveHolyBlood);

		matchClass.setSelection(options.matchClass);

		majorBloodChance.setSelection(options.majorBloodChance);
		minorBloodChance.setSelection(options.minorBloodChance);
		noBloodChance.setSelection(100 - options.majorBloodChance - options.minorBloodChance);
	}
}
