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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import ui.general.MinMaxControl;
import ui.model.GrowthOptions;
import ui.model.MinMaxOption;
import ui.model.VarOption;

public class GrowthsView extends Composite {
	
	private Boolean isEnabled = false;
	private GrowthOptions.Mode currentMode = GrowthOptions.Mode.REDISTRIBUTE;
	
	private Group container;
	
	private Button enableButton;
	
	private Button redistributeOption;
	private Spinner varianceSpinner;
	
	private Button byDeltaOption;
	private Spinner deltaSpinner;
	
	private Button fullRandomOption;
	private MinMaxControl growthRangeControl;
	
	private Button adjustHPGrowths;
	private Button adjustSTRMAGSplit;

	public GrowthsView(Composite parent, int style, boolean hasSTRMAGSplit) {
		super(parent, style);

		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Growths");
		container.setToolTipText("Randomizes the growths of all playable characters.");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		enableButton = new Button(container, SWT.CHECK);
		enableButton.setText("Enable Growths Randomization");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setEnableGrowths(enableButton.getSelection());
			}
		});
		
		/////////////////////////////////////////////////////////////
		
		redistributeOption = new Button(container, SWT.RADIO);
		redistributeOption.setText("Redistribute");
		redistributeOption.setToolTipText("Randomly redistrubtes a character's total growths.");
		redistributeOption.setEnabled(false);
		redistributeOption.setSelection(true);
		redistributeOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(GrowthOptions.Mode.REDISTRIBUTE);				
			}
		});
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(enableButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(enableButton, 5);
		redistributeOption.setLayoutData(optionData);
		
		Composite redistParamContainer = new Composite(container, 0);
		
		FormLayout redistParamLayout = new FormLayout();
		redistParamLayout.marginLeft = 5;
		redistParamLayout.marginRight = 5;
		redistParamLayout.marginTop = 5;
		redistParamLayout.marginBottom = 5;
		redistParamContainer.setLayout(redistParamLayout);
		
		Label redistParamLabel = new Label(redistParamContainer, SWT.RIGHT);
		redistParamLabel.setText("Growth Variance:");
		
		varianceSpinner = new Spinner(redistParamContainer, SWT.NONE);
		varianceSpinner.setValues(30, 0, 255, 0, 1, 5);
		varianceSpinner.setEnabled(false);
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(varianceSpinner, -5);
		labelData.top = new FormAttachment(varianceSpinner, 0, SWT.CENTER);
		redistParamLabel.setLayoutData(labelData);
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		varianceSpinner.setLayoutData(spinnerData);
		
		FormData paramContainerData = new FormData();
		paramContainerData.top = new FormAttachment(redistributeOption, 0);
		paramContainerData.left = new FormAttachment(redistributeOption, 0, SWT.LEFT);
		paramContainerData.right = new FormAttachment(100, -5);
		redistParamContainer.setLayoutData(paramContainerData);
		
		/////////////////////////////////////////////////////////////
		
		byDeltaOption = new Button(container, SWT.RADIO);
		byDeltaOption.setText("Randomize Delta");
		byDeltaOption.setToolTipText("Applies a random delta between +X and -X to all growth areas.");
		byDeltaOption.setEnabled(false);
		byDeltaOption.setSelection(false);
		byDeltaOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(GrowthOptions.Mode.DELTA);				
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(redistParamContainer, 0, SWT.LEFT);
		optionData.top = new FormAttachment(redistParamContainer, 0);
		byDeltaOption.setLayoutData(optionData);
		
		Composite deltaParamContainer = new Composite(container, 0);
		
		FormLayout deltaParamLayout = new FormLayout();
		deltaParamLayout.marginLeft = 5;
		deltaParamLayout.marginRight = 5;
		deltaParamLayout.marginTop = 5;
		deltaParamLayout.marginBottom = 5;
		deltaParamContainer.setLayout(deltaParamLayout);
		
		Label deltaParamLabel = new Label(deltaParamContainer, SWT.RIGHT);
		deltaParamLabel.setText("Max Delta:");
		
		deltaSpinner = new Spinner(deltaParamContainer, SWT.NONE);
		deltaSpinner.setValues(20, 0, 255, 0, 1, 5);
		deltaSpinner.setEnabled(false);
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(deltaSpinner, -5);
		labelData.top = new FormAttachment(deltaSpinner, 0, SWT.CENTER);
		deltaParamLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		deltaSpinner.setLayoutData(spinnerData);
		
		paramContainerData = new FormData();
		paramContainerData.top = new FormAttachment(byDeltaOption, 0);
		paramContainerData.left = new FormAttachment(byDeltaOption, 0, SWT.LEFT);
		paramContainerData.right = new FormAttachment(100, -5);
		deltaParamContainer.setLayoutData(paramContainerData);
		
		/////////////////////////////////////////////////////////////
		
		fullRandomOption = new Button(container, SWT.RADIO);
		fullRandomOption.setText("Randomize Absolute");
		fullRandomOption.setToolTipText("Generates fully random growth rates between the specified minimum and maximum.");
		fullRandomOption.setEnabled(false);
		fullRandomOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(GrowthOptions.Mode.FULL);
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(deltaParamContainer, 0, SWT.LEFT);
		optionData.top = new FormAttachment(deltaParamContainer, 0);
		fullRandomOption.setLayoutData(optionData);
		
		growthRangeControl = new MinMaxControl(container, SWT.NONE, "Min Growth:", "Max Growth:");
		growthRangeControl.getMinSpinner().setValues(5, 0, 255, 0, 1, 5);
		growthRangeControl.getMaxSpinner().setValues(80, 0, 255, 0, 1, 5);
		growthRangeControl.setEnabled(false);
		
		paramContainerData = new FormData();
		paramContainerData.top = new FormAttachment(fullRandomOption, 0);
		paramContainerData.left = new FormAttachment(fullRandomOption, 0, SWT.LEFT);
		paramContainerData.right = new FormAttachment(100, -5);
		growthRangeControl.setLayoutData(paramContainerData);
		
		adjustHPGrowths = new Button(container, SWT.CHECK);
		adjustHPGrowths.setText("Adjust HP Growths");
		adjustHPGrowths.setToolTipText("Puts extra emphasis on HP growths relative to other stats.");
		adjustHPGrowths.setEnabled(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(fullRandomOption, 0, SWT.LEFT);
		optionData.top = new FormAttachment(growthRangeControl, 10);
		adjustHPGrowths.setLayoutData(optionData);
		
		if (hasSTRMAGSplit) {
			adjustSTRMAGSplit = new Button(container, SWT.CHECK);
			adjustSTRMAGSplit.setText("Adjust STR/MAG by Class");
			adjustSTRMAGSplit.setToolTipText("Ensures that characters that primarily use magic randomize a higher or equal magic growth than strength and that\ncharacters that primarily use physical attacks randomize a higher or equal strength growth than magic.\n\nCharacters that use both will not be weighted in either direction.");
			adjustSTRMAGSplit.setEnabled(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(fullRandomOption, 0, SWT.LEFT);
			optionData.top = new FormAttachment(adjustHPGrowths, 5);
			adjustSTRMAGSplit.setLayoutData(optionData);
		}
	}
	
	public void overrideMaxGrowthAllowed(int maxGrowth) {
		growthRangeControl.getMaxSpinner().setMaximum(maxGrowth);
	}
	
	private void setEnableGrowths(Boolean enabled) {
		redistributeOption.setEnabled(enabled);
		byDeltaOption.setEnabled(enabled);
		fullRandomOption.setEnabled(enabled);
		varianceSpinner.setEnabled(enabled && currentMode == GrowthOptions.Mode.REDISTRIBUTE);
		deltaSpinner.setEnabled(enabled && currentMode == GrowthOptions.Mode.DELTA);
		growthRangeControl.setEnabled(enabled && currentMode == GrowthOptions.Mode.FULL);
		adjustHPGrowths.setEnabled(enabled);
		if (adjustSTRMAGSplit != null) { adjustSTRMAGSplit.setEnabled(enabled && currentMode != GrowthOptions.Mode.DELTA); }
		
		isEnabled = enabled;
	}

	private void setMode(GrowthOptions.Mode newMode) {
		currentMode = newMode;
		if (isEnabled) {
			switch (newMode) {
			case REDISTRIBUTE:
				varianceSpinner.setEnabled(true);
				deltaSpinner.setEnabled(false);
				growthRangeControl.setEnabled(false);
				break;
			case DELTA: 
				varianceSpinner.setEnabled(false);
				deltaSpinner.setEnabled(true);
				growthRangeControl.setEnabled(false);
				break;
			case FULL:
				varianceSpinner.setEnabled(false);
				deltaSpinner.setEnabled(false);
				growthRangeControl.setEnabled(true);
				break;
			}
		}
	}
	
	public GrowthOptions getGrowthOptions() {
		if (!isEnabled) { return null; }
		
		VarOption redistributionOption = null;
		VarOption deltaOption = null;
		MinMaxOption fullOption = null;
		
		switch (currentMode) {
		case REDISTRIBUTE:
			redistributionOption = new VarOption(varianceSpinner.getSelection());
			break;
		case DELTA:
			deltaOption = new VarOption(deltaSpinner.getSelection());
			break;
		case FULL:
			fullOption = growthRangeControl.getMinMaxOption();
			break;
		}
		
		boolean adjustSTRMAG = adjustSTRMAGSplit != null ? adjustSTRMAGSplit.getSelection() : false;
		
		return new GrowthOptions(currentMode, redistributionOption, deltaOption, fullOption, adjustHPGrowths.getSelection(), adjustSTRMAG);
	}
	
	public void setGrowthOptions(GrowthOptions options) {
		if (options == null) {
			enableButton.setSelection(false);
			setEnableGrowths(false);
		} else {
			enableButton.setSelection(true);
			setEnableGrowths(true);
			setMode(options.mode);
			
			switch (options.mode) {
			case REDISTRIBUTE:	
				redistributeOption.setSelection(true);
				byDeltaOption.setSelection(false);
				fullRandomOption.setSelection(false);
				varianceSpinner.setSelection(options.redistributionOption.variance);
				break;
			case DELTA:
				redistributeOption.setSelection(false);
				byDeltaOption.setSelection(true);
				fullRandomOption.setSelection(false);
				deltaSpinner.setSelection(options.deltaOption.variance);
				break;
			case FULL:
				redistributeOption.setSelection(false);
				byDeltaOption.setSelection(false);
				fullRandomOption.setSelection(true);
				growthRangeControl.setMin(options.fullOption.minValue);
				growthRangeControl.setMax(options.fullOption.maxValue);
				break;
			}
			
			adjustHPGrowths.setSelection(options.adjustHP);
			if (adjustSTRMAGSplit != null) {
				adjustSTRMAGSplit.setSelection(options.adjustSTRMAGSplit);
			}
		}
	}
}
