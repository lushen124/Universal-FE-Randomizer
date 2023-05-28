package ui.legacy;

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
import ui.model.BaseOptions;
import ui.model.VarOption;

public class BasesView extends Composite {
	
	private Boolean isEnabled = false;
	private BaseOptions.Mode currentMode = BaseOptions.Mode.REDISTRIBUTE;
	
	private Group container;
	
	private Button enableButton;
	
	private Button redistributeOption;
	private Spinner varianceSpinner;
	
	private Button byDeltaOption;
	private Spinner deltaSpinner;
	
	private Button adjustSTRMAG;

	public BasesView(Composite parent, int style, GameType type) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Bases");
		container.setToolTipText("Randomizes the base stat offsets of all playable characters, relative to their class (excluding CON).");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);

		enableButton = new Button(container, SWT.CHECK);
		enableButton.setText("Enable Bases Randomization");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setEnableBases(enableButton.getSelection());
			}
		});
		
		/////////////////////////////////////////////////////////////
		
		redistributeOption = new Button(container, SWT.RADIO);
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
		
		byDeltaOption = new Button(container, SWT.RADIO);
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
			adjustSTRMAG = new Button(container, SWT.CHECK);
			adjustSTRMAG.setText("Adjust STR/MAG by Class");
			adjustSTRMAG.setToolTipText("Ensures that characters that primarily use magic randomize a higher or equal magic base than strength and that\ncharacters that primarily use physical attacks randomize a higher or equal strength base than magic.\n\nCharacters that use both will not be weighted in either direction.");
			adjustSTRMAG.setEnabled(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(byDeltaOption, 0, SWT.LEFT);
			optionData.top = new FormAttachment(deltaParamContainer, 10);
			adjustSTRMAG.setLayoutData(optionData);
		}
	}
	
	private void setEnableBases(Boolean enabled) {
		redistributeOption.setEnabled(enabled);
		byDeltaOption.setEnabled(enabled);
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
			}
		}
	}
	
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
		}
		
		boolean adjustSTRMAGBases = adjustSTRMAG != null ? adjustSTRMAG.getSelection() : false;
		
		return new BaseOptions(currentMode, redistributionOption, deltaOption, adjustSTRMAGBases);
	}
	
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
				varianceSpinner.setSelection(options.redistributionOption.variance);
				break;
			case DELTA:
				redistributeOption.setSelection(false);
				byDeltaOption.setSelection(true);
				deltaSpinner.setSelection(options.deltaOption.variance);
				break;
			}
			
			if (adjustSTRMAG != null) {
				adjustSTRMAG.setSelection(options.adjustSTRMAGByClass);
			}
		}
	}

}
