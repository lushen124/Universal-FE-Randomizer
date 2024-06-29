package ui.views;

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

import fedata.general.FEBase.GameType;
import ui.common.GuiUtil;
import ui.model.BaseOptions;
import ui.model.VarOption;

public class BasesView extends YuneView<BaseOptions> {
	
	private Boolean isEnabled = false;
	private BaseOptions.Mode currentMode = BaseOptions.Mode.REDISTRIBUTE;
	

	private Button enableButton;
	
	private Button redistributeOption;
	private Spinner varianceSpinner;
	
	private Button byDeltaOption;
	private Spinner deltaSpinner;
	
	private Button smartOption;
	
	private Button adjustSTRMAG;

	public BasesView(Composite parent, GameType type) {
		super(parent, type);
	}

	@Override
	public String getGroupTitle() {
		return "Bases";
	}

	@Override
	public String getGroupTooltip() {
		return "Randomizes the base stat offsets of all playable characters, relative to their class (excluding CON).";
	}

	@Override
	protected void compose() {
		enableButton = new Button(group, SWT.CHECK);
		enableButton.setText("Enable Bases Randomization");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setEnableBases(enableButton.getSelection());
			}
		});
		
		/////////////////////////////////////////////////////////////
		
		redistributeOption = new Button(group, SWT.RADIO);
		redistributeOption.setText("Redistribute");
		if (type == GameType.FE4) {
			redistributeOption.setToolTipText("Randomly redistrubtes the sum of the character's base stat offsets (excluding HP).");
		} else {
			redistributeOption.setToolTipText("Randomly redistrubtes the sum of the character's base stat offsets (excluding CON).");
		}
		redistributeOption.setEnabled(false);
		redistributeOption.setSelection(true);
		redistributeOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(BaseOptions.Mode.REDISTRIBUTE);				
			}
		});
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(enableButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(enableButton, 5);
		redistributeOption.setLayoutData(optionData);
		
		Composite redistParamContainer = new Composite(group, 0);
		redistParamContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		Label redistParamLabel = new Label(redistParamContainer, SWT.RIGHT);
		redistParamLabel.setText("Growth Variance:");
		
		varianceSpinner = new Spinner(redistParamContainer, SWT.NONE);
		varianceSpinner.setValues(5, 0, 10, 0, 1, 1);
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
		
		byDeltaOption = new Button(group, SWT.RADIO);
		byDeltaOption.setText("Randomize Delta");
		byDeltaOption.setToolTipText("Applies a random delta between +X and -X to all base stats (excluding CON).");
		byDeltaOption.setEnabled(false);
		byDeltaOption.setSelection(false);
		byDeltaOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(BaseOptions.Mode.DELTA);				
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(redistParamContainer, 0, SWT.LEFT);
		optionData.top = new FormAttachment(redistParamContainer, 0);
		byDeltaOption.setLayoutData(optionData);
		
		Composite deltaParamContainer = new Composite(group, 0);
		deltaParamContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		Label deltaParamLabel = new Label(deltaParamContainer, SWT.RIGHT);
		deltaParamLabel.setText("Max Delta:");
		
		deltaSpinner = new Spinner(deltaParamContainer, SWT.NONE);
		deltaSpinner.setValues(3, 1, 5, 0, 1, 1);
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
		
		if (type.hasSTRMAGSplit()) {
			adjustSTRMAG = new Button(group, SWT.CHECK);
			adjustSTRMAG.setText("Adjust STR/MAG by Class");
			adjustSTRMAG.setToolTipText("Ensures that characters that primarily use magic randomize a higher or equal magic base than strength and that\ncharacters that primarily use physical attacks randomize a higher or equal strength base than magic.\n\nCharacters that use both will not be weighted in either direction.");
			adjustSTRMAG.setEnabled(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(byDeltaOption, 0, SWT.LEFT);
			optionData.top = new FormAttachment(deltaParamContainer, 10);
			adjustSTRMAG.setLayoutData(optionData);
		}
		
		/////////////////////////////////////////////////////////////
		
		smartOption = new Button(group, SWT.RADIO);
		smartOption.setText("Smart Randomize");
		smartOption.setToolTipText("Attempts to set bases in a way that avoids extreme stats most of the time.");
		smartOption.setEnabled(false);
		smartOption.setSelection(false);
		smartOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				setMode(BaseOptions.Mode.SMART);
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(deltaParamContainer, 0, SWT.LEFT);
		optionData.top = new FormAttachment(deltaParamContainer, 0);
		smartOption.setLayoutData(optionData);
		
	}
	
	private void setEnableBases(Boolean enabled) {
		redistributeOption.setEnabled(enabled);
		byDeltaOption.setEnabled(enabled);
		smartOption.setEnabled(enabled);
		varianceSpinner.setEnabled(enabled && currentMode == BaseOptions.Mode.REDISTRIBUTE);
		deltaSpinner.setEnabled(enabled && currentMode == BaseOptions.Mode.DELTA);
		if (adjustSTRMAG != null) { adjustSTRMAG.setEnabled(enabled); }
		
		isEnabled = enabled;
	}

	private void setMode(BaseOptions.Mode newMode) {
		currentMode = newMode;
		if (isEnabled) {
			switch (newMode) {
			case REDISTRIBUTE:
				varianceSpinner.setEnabled(true);
				deltaSpinner.setEnabled(false);
				break;
			case DELTA: 
				varianceSpinner.setEnabled(false);
				deltaSpinner.setEnabled(true);
				break;
			case SMART:
				varianceSpinner.setEnabled(false);
				deltaSpinner.setEnabled(false);
				break;
			}
		}
	}

	@Override
	public BaseOptions getOptions() {
		if (!isEnabled) { return null; }
		
		VarOption redistributionOption = null;
		VarOption deltaOption = null;
		
		switch (currentMode) {
		case REDISTRIBUTE:
			redistributionOption = new VarOption(varianceSpinner.getSelection());
			break;
		case DELTA:
			deltaOption = new VarOption(deltaSpinner.getSelection());
			break;
		default:
			break;
		}
		
		boolean adjustSTRMAGBases = adjustSTRMAG != null ? adjustSTRMAG.getSelection() : false;
		
		return new BaseOptions(currentMode, redistributionOption, deltaOption, adjustSTRMAGBases);
	}

	@Override
	public void initialize(BaseOptions options) {
		if (options == null) {
			enableButton.setSelection(false);
			setEnableBases(false);
		} else {
			enableButton.setSelection(true);
			setEnableBases(true);
			setMode(options.mode);
			
			switch (options.mode) {
			case REDISTRIBUTE:
				redistributeOption.setSelection(true);
				byDeltaOption.setSelection(false);
				smartOption.setSelection(false);
				varianceSpinner.setSelection(options.redistributionOption.variance);
				break;
			case DELTA:
				redistributeOption.setSelection(false);
				byDeltaOption.setSelection(true);
				smartOption.setSelection(false);
				deltaSpinner.setSelection(options.deltaOption.variance);
				break;
			case SMART:
				redistributeOption.setSelection(false);
				byDeltaOption.setSelection(false);
				smartOption.setSelection(true);
				break;
			}
			
			if (adjustSTRMAG != null) {
				adjustSTRMAG.setSelection(options.adjustSTRMAGByClass);
			}
		}
	}

}
