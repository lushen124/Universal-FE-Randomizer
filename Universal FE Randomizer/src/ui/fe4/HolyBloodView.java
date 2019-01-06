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
		holyWeaponBonusData.top = new FormAttachment(growthBonusParameterContainer, 0);
		randomizeHolyWeaponBonusesButton.setLayoutData(holyWeaponBonusData);
	}

	public HolyBloodOptions getHolyBloodOptions() {
		return new HolyBloodOptions(randomizeGrowthBonusesButton.getSelection(), growthBonusTotalSpinner.getSelection(), randomizeHolyWeaponBonusesButton.getSelection());
	}
}
