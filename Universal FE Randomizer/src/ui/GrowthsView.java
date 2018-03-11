package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

public class GrowthsView extends Composite {
	
	public enum Mode {
		REDISTRIBUTE, DELTA, FULL
	}
	
	private Boolean isEnabled = false;
	private Mode currentMode = Mode.REDISTRIBUTE;
	
	private Group container;
	
	private Button enableButton;
	
	private Button redistributeOption;
	private Spinner varianceSpinner;
	
	private Button byDeltaOption;
	private Spinner deltaSpinner;
	
	private Button fullRandomOption;
	private MinMaxControl growthRangeControl;

	public GrowthsView(Composite parent, int style) {
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
				setMode(Mode.REDISTRIBUTE);				
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
				setMode(Mode.DELTA);				
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
		deltaSpinner.setValues(50, 0, 255, 0, 1, 5);
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
				setMode(Mode.FULL);
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
	}
	
	private void setEnableGrowths(Boolean enabled) {
		redistributeOption.setEnabled(enabled);
		byDeltaOption.setEnabled(enabled);
		fullRandomOption.setEnabled(enabled);
		varianceSpinner.setEnabled(enabled && currentMode == Mode.REDISTRIBUTE);
		deltaSpinner.setEnabled(enabled && currentMode == Mode.DELTA);
		growthRangeControl.setEnabled(enabled && currentMode == Mode.FULL);
		
		isEnabled = enabled;
	}

	private void setMode(Mode newMode) {
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
	
	public Boolean isGrowthEnabled() {
		return isEnabled;
	}
	
	public Mode randomizationType() {
		return currentMode;
	}
	
	public int getDeltaParameter() {
		return deltaSpinner.getSelection();
	}
	
	public int getAbsoluteMinimum() {
		return growthRangeControl.getMinSpinner().getSelection();
	}
	
	public int getAbsoluteMaximum() {
		return growthRangeControl.getMaxSpinner().getSelection();
	}
	
	public int getRedistributionVariance() {
		return varianceSpinner.getSelection();
	}
}
