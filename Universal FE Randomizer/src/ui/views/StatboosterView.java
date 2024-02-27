package ui.views;

import org.eclipse.swt.SWT;
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

import ui.general.MinMaxControl;
import ui.model.MinMaxOption;
import ui.model.StatboosterOptions;
import ui.model.StatboosterOptions.StatboosterRandomizationModes;

public class StatboosterView extends YuneView<StatboosterOptions> {

	private Boolean isEnabled = false;
	private StatboosterRandomizationModes currentMode = StatboosterRandomizationModes.SAME_STAT;
	
	private Button enableButton;
	private MinMaxControl boostRangeControl;
	
	
	private Group modeContainer;
	private Button sameStatOption;
	private Button multipleBoostsOption;
	private MinMaxControl multipleBoostsRangeControl;
	private Button shuffleOption;
	
	
	private Button hpModifierButton;
	private Spinner hpModifierSpinner;

	private Button includeBoots;
	private Button includeBodyring;
	
	public StatboosterView(Composite parent) {
		super(parent);
	}
	
	@Override
	public String getGroupTitle() {
		return "Statboosters";
	}

	@Override
	public void initialize(StatboosterOptions options) {
		if (options == null) {
			enableButton.setSelection(false);
			enableChanged(false);
		} else {
			enableButton.setSelection(true);
			enableChanged(true);
			setMode(options.mode);
			
			boostRangeControl.setMin(options.boostStrengthMin);
			boostRangeControl.setMax(options.boostStrengthMax);
			
			sameStatOption.setSelection(StatboosterRandomizationModes.SAME_STAT.equals(options.mode));
			multipleBoostsOption.setSelection(StatboosterRandomizationModes.MULTIPLE_STATS.equals(options.mode));
			if (StatboosterRandomizationModes.MULTIPLE_STATS.equals(options.mode)) {
				multipleBoostsRangeControl.setEnabled(true);
				multipleBoostsRangeControl.setMin(options.multipleStatsMin);
				multipleBoostsRangeControl.setMax(options.multipleStatsMax);
			}
			shuffleOption.setSelection(StatboosterRandomizationModes.SHUFFLE.equals(options.mode));
			
			includeBodyring.setSelection(options.includeCon);
			includeBoots.setSelection(options.includeMov);
			
			hpModifierButton.setSelection(options.applyHpModifier);
			hpModifierSpinner.setSelection(options.hpModifier);
			hpModifierSpinner.setEnabled(hpModifierButton.getSelection());
		}
	}

	@Override
	public StatboosterOptions getOptions() {
if (!isEnabled) { return null; }
		
		MinMaxOption multipleStats = multipleBoostsRangeControl.getMinMaxOption();
		MinMaxOption boostRange = boostRangeControl.getMinMaxOption();
		
		
		return new StatboosterOptions(true, currentMode,boostRange.minValue, boostRange.maxValue, multipleStats.minValue, multipleStats.maxValue, includeBoots.getSelection(), includeBodyring.getSelection(), hpModifierButton.getSelection(), hpModifierSpinner.getSelection());
	}

	@Override
	protected void compose() {
		enableButton = new Button(group, SWT.CHECK);
		enableButton.setText("Enable Statbooster Randomization");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				enableChanged(enableButton.getSelection());
			}
		});
		
		boostRangeControl = new MinMaxControl(group, SWT.NONE, "Min Boost:", "Max Boost:");
		boostRangeControl.getMinSpinner().setValues(1, 0, 20, 0, 1, 5);
		boostRangeControl.getMaxSpinner().setValues(3, 0, 20, 0, 1, 5);
		boostRangeControl.setEnabled(false);
		
		FormData boostRangeData = new FormData();
		boostRangeData.top = new FormAttachment(enableButton, 5);
		boostRangeData.left = new FormAttachment(0, 5);
		boostRangeData.right = new FormAttachment(100, -5);
		boostRangeControl.setLayoutData(boostRangeData);
		
		modeContainer = new Group(group, SWT.NONE);
		modeContainer.setText("Mode");
		
		FormLayout modeLayout = new FormLayout();
		modeLayout.marginTop = 5;
		modeLayout.marginLeft = 5;
		modeLayout.marginBottom = 5;
		modeLayout.marginRight = 5;
		modeContainer.setLayout(modeLayout);
		
		FormData modeData = new FormData();
		modeData.left = new FormAttachment(enableButton, 5, SWT.LEFT);
		modeData.top = new FormAttachment(boostRangeControl, 10);
		modeData.right = new FormAttachment(100, -5);
		modeContainer.setLayoutData(modeData);
		
		/////////////////////////////////////////////////////////////
		
		sameStatOption = new Button(modeContainer, SWT.RADIO);
		sameStatOption.setText("Same Stat");
		sameStatOption.setToolTipText("The Statboosters will still bost their normal stats, only the value is changed.");
		sameStatOption.setEnabled(false);
		sameStatOption.setSelection(true);
		sameStatOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(StatboosterRandomizationModes.SAME_STAT);				
			}
		});
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		sameStatOption.setLayoutData(optionData);
		
		/////////////////////////////////////////////////////////////
		
		multipleBoostsOption = new Button(modeContainer, SWT.RADIO);
		multipleBoostsOption.setText("Multiple Stats");
		multipleBoostsOption.setToolTipText("Each Statbooster can grant multiple random stats.");
		multipleBoostsOption.setEnabled(false);
		multipleBoostsOption.setSelection(false);
		multipleBoostsOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(StatboosterRandomizationModes.MULTIPLE_STATS);				
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(sameStatOption, 0, SWT.LEFT);
		optionData.top = new FormAttachment(sameStatOption, 0);
		multipleBoostsOption.setLayoutData(optionData);
		
		Composite multipleParamContainer = new Composite(modeContainer, 0);
		FormLayout multipleParamLayout = new FormLayout();
		multipleParamLayout.marginTop = 5;
		multipleParamLayout.marginLeft = 5;
		multipleParamLayout.marginBottom = 5;
		multipleParamLayout.marginRight = 5;
		multipleParamContainer.setLayout(multipleParamLayout);
		
		multipleBoostsRangeControl = new MinMaxControl(multipleParamContainer, SWT.NONE, "Min Boosts:", "Max Boosts:");
		multipleBoostsRangeControl.getMinSpinner().setValues(1, 0, 20, 0, 1, 5);
		multipleBoostsRangeControl.getMaxSpinner().setValues(3, 0, 20, 0, 1, 5);
		multipleBoostsRangeControl.setEnabled(false);
		
		optionData = new FormData();
		optionData.top = new FormAttachment(0, 0);
		optionData.left = new FormAttachment(0, 0);
		optionData.right = new FormAttachment(100, -5);
		multipleBoostsRangeControl.setLayoutData(optionData);
		
		optionData = new FormData();
		optionData.top = new FormAttachment(multipleBoostsOption, 5);
		optionData.left = new FormAttachment(multipleBoostsOption, 0, SWT.LEFT);
		multipleParamContainer.setLayoutData(optionData);
		
		/////////////////////////////////////////////////////////////
		
		shuffleOption = new Button(modeContainer, SWT.RADIO);
		shuffleOption.setText("Shuffle");
		shuffleOption.setToolTipText("The Stats that the boosters grant will be shuffled. There will always still be one booster for each Stat.");
		shuffleOption.setEnabled(false);
		shuffleOption.setSelection(false);
		shuffleOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(StatboosterRandomizationModes.SHUFFLE);				
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(multipleParamContainer, 0, SWT.LEFT);
		optionData.top = new FormAttachment(multipleParamContainer, 0);
		shuffleOption.setLayoutData(optionData);
		/////////////////////////////////////////////////////////////
		
		
		Group hpModifierContainer = new Group(group, 0);
		hpModifierContainer.setText("Misc");
		
		FormLayout hpModContainerLayout = new FormLayout();
		hpModContainerLayout.marginBottom = 5;
		hpModContainerLayout.marginTop = 5;
		hpModContainerLayout.marginLeft = 5;
		hpModContainerLayout.marginRight = 5;
		hpModifierContainer.setLayout(hpModContainerLayout);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(enableButton, 5, SWT.LEFT);
		optionData.top = new FormAttachment(modeContainer, 5);
		hpModifierContainer.setLayoutData(optionData);
		
		includeBoots = new Button(hpModifierContainer, SWT.CHECK);
		includeBoots.setText("Include Boots");
		includeBoots.setToolTipText("As you can't naturally level up move, randomizing the body ring is opt-in only.");
		includeBoots.setEnabled(false);
		
		optionData = new FormData();
		optionData.top = new FormAttachment(0, 0);
		optionData.left = new FormAttachment(0, 0);
		includeBoots.setLayoutData(optionData);
		
		includeBodyring = new Button(hpModifierContainer, SWT.CHECK);
		includeBodyring.setText("Include Body Ring");
		includeBodyring.setToolTipText("As you can't naturally level up con, randomizing the body ring is opt-in only.");
		includeBodyring.setEnabled(false);
		
		optionData = new FormData();
		optionData.top = new FormAttachment(includeBoots, 5);
		includeBodyring.setLayoutData(optionData);
		
		hpModifierButton = new Button(hpModifierContainer, SWT.CHECK);
		hpModifierButton.setText("Apply HP Modifier");
		hpModifierButton.setToolTipText("In Vanilla the HP Statbooster grants much more stats, with this option all HP statboosts will grant the selected flat modifier.");
		hpModifierButton.setEnabled(false);
		hpModifierButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				hpModifierSpinner.setEnabled(hpModifierButton.getEnabled());
			}
		});
		
		optionData = new FormData();
		optionData.top = new FormAttachment(includeBodyring, 5);
		hpModifierButton.setLayoutData(optionData);
		
		hpModifierSpinner = new Spinner(hpModifierContainer, SWT.NONE);
		hpModifierSpinner.setValues(5, 0, 10, 0, 1, 5);
		hpModifierSpinner.setEnabled(false);
		
		Label hpModifierLabel = new Label(hpModifierContainer, SWT.RIGHT);
		hpModifierLabel.setText("Additional HP:");
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(hpModifierSpinner, -5);
		labelData.top = new FormAttachment(hpModifierSpinner, 0, SWT.CENTER);
		hpModifierLabel.setLayoutData(labelData);
		
		FormData spinnerData = new FormData();
		spinnerData.top = new FormAttachment(hpModifierButton, 5);
		spinnerData.right = new FormAttachment(100, -5);
		hpModifierSpinner.setLayoutData(spinnerData);
	}

	private void setMode(StatboosterRandomizationModes newMode) {
		currentMode = newMode;
		if (isEnabled) {
			switch (newMode) {
			case SAME_STAT:
			case SHUFFLE:
				multipleBoostsRangeControl.setEnabled(false);
				boostRangeControl.setEnabled(true);
				break;
			case MULTIPLE_STATS: 
				multipleBoostsRangeControl.setEnabled(true);
				boostRangeControl.setEnabled(true);
				break;
			}
		}
	}
	
	private void enableChanged(Boolean enabled) {
		sameStatOption.setEnabled(enabled);
		multipleBoostsOption.setEnabled(enabled);
		shuffleOption.setEnabled(enabled);
		boostRangeControl.setEnabled(enabled);
		hpModifierButton.setEnabled(enabled);
		includeBodyring.setEnabled(enabled);
		includeBoots.setEnabled(enabled);
		isEnabled = enabled;
	}
}
