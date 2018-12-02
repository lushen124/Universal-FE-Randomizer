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
import ui.model.MinMaxOption;
import ui.model.MinVarOption;
import ui.model.OtherCharacterOptions;

public class MOVCONAffinityView extends Composite {
	
	private Group container;
	
	private Button randomizeMOVButton;
	private MinMaxControl movLimitControl;
	
	private Button randomizeCONButton;
	private Spinner varianceSpinner;
	private Spinner minCONSpinner;
	
	private Button randomizeAffinityButton;

	public MOVCONAffinityView(Composite parent, int style) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Other Character Settings");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		///////////////////////////////////////////////////////
		
		randomizeMOVButton = new Button(container, SWT.CHECK);
		randomizeMOVButton.setText("Randomize MOV");
		randomizeMOVButton.setToolTipText("Assigns each class a random MOV range between the minimum and maximum specified. Male and Female versions are considered different classes.");
		
		FormData movButtonData = new FormData();
		movButtonData.left = new FormAttachment(0, 5);
		movButtonData.top = new FormAttachment(0, 5);
		randomizeMOVButton.setLayoutData(movButtonData);
		
		movLimitControl = new MinMaxControl(container, SWT.NONE, "Min MOV:", "Max MOV:");
		
		movLimitControl.getMinSpinner().setValues(4, 1, 8, 0, 1, 1);
		movLimitControl.getMaxSpinner().setValues(8, 4, 9, 0, 1, 1);
		movLimitControl.setEnabled(false);
		
		FormData paramContainerData = new FormData();
		paramContainerData.top = new FormAttachment(randomizeMOVButton, 0);
		paramContainerData.left = new FormAttachment(randomizeMOVButton, 0, SWT.LEFT);
		paramContainerData.right = new FormAttachment(100, -5);
		movLimitControl.setLayoutData(paramContainerData);
		
		randomizeMOVButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				movLimitControl.setEnabled(randomizeMOVButton.getSelection());
			}
		});
		
		///////////////////////////////////////////////////////
		
		randomizeCONButton = new Button(container, SWT.CHECK);
		randomizeCONButton.setText("Randomize CON");
		randomizeCONButton.setToolTipText("Randomly adjusts each character's CON offset from their class base, up to a maximum variance.");
		
		FormData conButtonData = new FormData();
		conButtonData.left = new FormAttachment(randomizeMOVButton, 0, SWT.LEFT);
		conButtonData.top = new FormAttachment(movLimitControl, 10);
		randomizeCONButton.setLayoutData(conButtonData);
		
		Composite conParamContainer = new Composite(container, SWT.NONE);
		
		FormLayout conParamLayout = new FormLayout();
		conParamLayout.marginLeft = 5;
		conParamLayout.marginTop = 5;
		conParamLayout.marginBottom = 5;
		conParamLayout.marginRight = 5;
		conParamContainer.setLayout(conParamLayout);
		
		Label varianceLabel = new Label(conParamContainer, SWT.RIGHT);
		varianceLabel.setText("Variance:");
		
		Label minLabel = new Label(conParamContainer, SWT.RIGHT);
		minLabel.setText("Min CON:");
		
		varianceSpinner = new Spinner(conParamContainer, SWT.NONE);
		varianceSpinner.setValues(3, 1, 10, 0, 1, 1);
		varianceSpinner.setEnabled(false);
		
		minCONSpinner = new Spinner(conParamContainer, SWT.NONE);
		minCONSpinner.setValues(3, 0, 15, 0, 1, 1);
		minCONSpinner.setEnabled(false);
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(varianceSpinner, -5);
		labelData.top = new FormAttachment(varianceSpinner, 0, SWT.CENTER);
		varianceLabel.setLayoutData(labelData);
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(50, -5);
		varianceSpinner.setLayoutData(spinnerData);
		
		labelData = new FormData();
		labelData.left = new FormAttachment(50, 5);
		labelData.right = new FormAttachment(minCONSpinner, -5);
		labelData.top = new FormAttachment(minCONSpinner, 0, SWT.CENTER);
		minLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		minCONSpinner.setLayoutData(spinnerData);
		
		randomizeCONButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				varianceSpinner.setEnabled(randomizeCONButton.getSelection());
				minCONSpinner.setEnabled(randomizeCONButton.getSelection());
			}
		});
		
		FormData conContainerData = new FormData();
		conContainerData.top = new FormAttachment(randomizeCONButton, 0);
		conContainerData.left = new FormAttachment(randomizeCONButton, 0, SWT.LEFT);
		conContainerData.right = new FormAttachment(100, -5);
		conParamContainer.setLayoutData(conContainerData);
		
		///////////////////////////////////////////////////////
		
		randomizeAffinityButton = new Button(container, SWT.CHECK);
		randomizeAffinityButton.setText("Randomize Affinity");
		randomizeAffinityButton.setToolTipText("Assigns random support affinities to all characters.");
		
		FormData affinityData = new FormData();
		affinityData.left = new FormAttachment(randomizeCONButton, 0, SWT.LEFT);
		affinityData.top = new FormAttachment(conParamContainer, 10);
		randomizeAffinityButton.setLayoutData(affinityData);
	}
	
	public OtherCharacterOptions getOtherCharacterOptions() {
		MinMaxOption movementOptions = null;
		MinVarOption constitutionOptions = null;
		
		if (randomizeMOVButton.getSelection()) {
			movementOptions = movLimitControl.getMinMaxOption();
		}
		if (randomizeCONButton.getSelection()) {
			constitutionOptions = new MinVarOption(minCONSpinner.getSelection(), varianceSpinner.getSelection());
		}
		
		return new OtherCharacterOptions(movementOptions, constitutionOptions, randomizeAffinityButton.getSelection());
	}
}
