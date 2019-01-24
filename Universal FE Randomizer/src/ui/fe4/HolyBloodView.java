package ui.fe4;

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

public class HolyBloodView extends Composite {
	
	private Group container;
	
	private Button randomizeGrowthBonusesButton;
	private Spinner growthBonusTotalSpinner;
	
	private Button randomizeHolyWeaponBonusesButton;
	
	private Button giveHolyBlood;
	private Button matchClass;
	private Spinner majorBloodChance;
	
	public HolyBloodView(Composite parent, int style) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Holy Blood");
		container.setToolTipText("Randomizes the properties of Holy Blood.");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		randomizeGrowthBonusesButton = new Button(container, SWT.CHECK);
		randomizeGrowthBonusesButton.setText("Randomize Growth Bonuses");
		randomizeGrowthBonusesButton.setToolTipText("Randomly assigns the growth bonuses bestowed on those with Minor or Major Holy Blood.");
		randomizeGrowthBonusesButton.setEnabled(true);
		randomizeGrowthBonusesButton.setSelection(false);
		randomizeGrowthBonusesButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				growthBonusTotalSpinner.setEnabled(randomizeGrowthBonusesButton.getSelection());
			}
		});
		
		Composite growthBonusParameterContainer = new Composite(container, 0);
		
		FormLayout growthBonusParamContainerLayout = new FormLayout();
		growthBonusParamContainerLayout.marginLeft = 5;
		growthBonusParamContainerLayout.marginRight = 5;
		growthBonusParamContainerLayout.marginTop = 5;
		growthBonusParamContainerLayout.marginBottom = 5;
		growthBonusParameterContainer.setLayout(growthBonusParamContainerLayout);
		
		Label growthTotalLabel = new Label(growthBonusParameterContainer, SWT.RIGHT);
		growthTotalLabel.setText("Growth Bonus Total:");
		
		growthBonusTotalSpinner = new Spinner(growthBonusParameterContainer, SWT.NONE);
		growthBonusTotalSpinner.setValues(50, 0, 100, 0, 5, 10);
		growthBonusTotalSpinner.setEnabled(false);
		growthBonusTotalSpinner.setToolTipText("The total bonus for each holy blood granted to those with Minor Blood. The bonus is doubled for Major Blood.");
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(growthBonusTotalSpinner, -5);
		labelData.top = new FormAttachment(growthBonusTotalSpinner, 0, SWT.CENTER);
		growthTotalLabel.setLayoutData(labelData);
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		growthBonusTotalSpinner.setLayoutData(spinnerData);
		
		FormData containerData = new FormData();
		containerData.top = new FormAttachment(randomizeGrowthBonusesButton, 0);
		containerData.left = new FormAttachment(randomizeGrowthBonusesButton, 0, SWT.LEFT);
		containerData.right = new FormAttachment(100, -5);
		growthBonusParameterContainer.setLayoutData(containerData);
		
		/////////////////////////////////////////////////////////////
		
		randomizeHolyWeaponBonusesButton = new Button(container, SWT.CHECK);
		randomizeHolyWeaponBonusesButton.setText("Randomize Holy Weapon Bonuses");
		randomizeHolyWeaponBonusesButton.setToolTipText("Randomizes the bonuses granted when Holy Weapons are equipped. Does not affect Naga and Loptous.");
		randomizeHolyWeaponBonusesButton.setEnabled(true);
		randomizeHolyWeaponBonusesButton.setSelection(false);
		
		FormData holyWeaponBonusData = new FormData();
		holyWeaponBonusData.left = new FormAttachment(growthBonusParameterContainer, 0, SWT.LEFT);
		holyWeaponBonusData.top = new FormAttachment(growthBonusParameterContainer, 5);
		randomizeHolyWeaponBonusesButton.setLayoutData(holyWeaponBonusData);
		
		giveHolyBlood = new Button(container, SWT.CHECK);
		giveHolyBlood.setText("Assign Holy Blood to Playable Characters");
		giveHolyBlood.setToolTipText("Assigns either Major or Minor Holy Blood to all Playable Characters.\n\nThose that already have Major Holy Blood are unaffected.\nThose that have Minor Holy Blood may gain an additional Minor Blood or convert into Major Blood.\nThose with none have a chance of either Major or Minor Blood.\n\nApplies to all non-child characters.");
		giveHolyBlood.setEnabled(true);
		giveHolyBlood.setSelection(false);
		giveHolyBlood.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				matchClass.setEnabled(giveHolyBlood.getSelection());
				majorBloodChance.setEnabled(giveHolyBlood.getSelection());
			}
		});
		
		FormData giveBloodData = new FormData();
		giveBloodData.left = new FormAttachment(randomizeHolyWeaponBonusesButton, 0, SWT.LEFT);
		giveBloodData.top = new FormAttachment(randomizeHolyWeaponBonusesButton, 10);
		giveHolyBlood.setLayoutData(giveBloodData);
		
		matchClass = new Button(container, SWT.CHECK);
		matchClass.setText("Match Blood to Weapon Usage");
		matchClass.setToolTipText("When assigning a character with no holy blood, assigns a blood that matches their weapon type.\n\nFor characters with minor blood, a blood of a different weapon type will be used.");
		matchClass.setEnabled(false);
		matchClass.setSelection(false);
		
		FormData matchData = new FormData();
		matchData.left = new FormAttachment(giveHolyBlood, 10, SWT.LEFT);
		matchData.top = new FormAttachment(giveHolyBlood, 5);
		matchClass.setLayoutData(matchData);
		
		Composite giveBloodContainer = new Composite(container, 0);
		
		FormLayout giveBloodContainerLayout = new FormLayout();
		giveBloodContainerLayout.marginLeft = 5;
		giveBloodContainerLayout.marginRight = 5;
		giveBloodContainerLayout.marginTop = 5;
		giveBloodContainerLayout.marginBottom = 5;
		giveBloodContainer.setLayout(giveBloodContainerLayout);
		
		Label majorBloodLabel = new Label(giveBloodContainer, SWT.RIGHT);
		majorBloodLabel.setText("Major Blood Chance:");
		
		majorBloodChance = new Spinner(giveBloodContainer, SWT.NONE);
		majorBloodChance.setValues(25, 0, 100, 0, 5, 10);
		majorBloodChance.setEnabled(false);
		majorBloodChance.setToolTipText("The chance of a character obtaining Major Holy Blood. The remainder (out of 100) is allocated to Minor Holy Blood.");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(majorBloodChance, -5);
		labelData.top = new FormAttachment(majorBloodChance, 0, SWT.CENTER);
		majorBloodLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		majorBloodChance.setLayoutData(spinnerData);
		
		containerData = new FormData();
		containerData.top = new FormAttachment(matchClass, 0);
		containerData.left = new FormAttachment(matchClass, 0, SWT.LEFT);
		containerData.right = new FormAttachment(100, -5);
		giveBloodContainer.setLayoutData(containerData);
	}

	public HolyBloodOptions getHolyBloodOptions() {
		return new HolyBloodOptions(randomizeGrowthBonusesButton.getSelection(), growthBonusTotalSpinner.getSelection(), randomizeHolyWeaponBonusesButton.getSelection(), giveHolyBlood.getSelection(), matchClass.getSelection(), majorBloodChance.getSelection());
	}
	
	public void setHolyBloodOptions(HolyBloodOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			if (options.randomizeGrowthBonuses) {
				randomizeGrowthBonusesButton.setSelection(true);
				growthBonusTotalSpinner.setEnabled(true);
				growthBonusTotalSpinner.setSelection(options.growthTotal);
			}
			
			randomizeHolyWeaponBonusesButton.setSelection(options.randomizeWeaponBonuses);
			
			if (options.giveHolyBlood) {
				giveHolyBlood.setSelection(true);
				
				matchClass.setEnabled(true);
				majorBloodChance.setEnabled(true);
				
				matchClass.setSelection(options.matchClass);
				majorBloodChance.setSelection(options.majorBloodChance);
			}
		}
	}
}
