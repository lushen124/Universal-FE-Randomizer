package ui.views.fe9;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import ui.common.GuiUtil;
import ui.model.fe9.FE9OtherCharacterOptions;

public class CONAffinityView extends Composite {
	
	private Group container;
	
	private Button randomizeCONButton;
	private Label conVarianceLabel;
	private Spinner conVarianceSpinner;
	
	private Button randomizeAffinityButton;
	
	public CONAffinityView(Composite parent) {
		super(parent, SWT.NONE);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		container.setText("Other Character Settings");
		container.setLayout(GuiUtil.formLayoutWithMargin());
		
		randomizeCONButton = new Button(container, SWT.CHECK);
		randomizeCONButton.setText("Randomize Constitution");
		randomizeCONButton.setToolTipText("Randomizes Constitution, which affects weight, and therefore, the ability to\nto shove/rescue and to be shoved/rescued.");
		
		FormData conData = new FormData();
		conData.left = new FormAttachment(0, 0);
		conData.top = new FormAttachment(0, 0);
		randomizeCONButton.setLayoutData(conData);
		
		conVarianceSpinner = new Spinner(container, SWT.NONE);
		conVarianceSpinner.setValues(3, 1, 8, 0, 1, 1);
		conVarianceSpinner.setToolTipText("Determines how far in each direction Constitution is allowed to adjust.");
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -12);
		spinnerData.top = new FormAttachment(randomizeCONButton, 5);
		conVarianceSpinner.setLayoutData(spinnerData);
		
		conVarianceLabel = new Label(container, SWT.NONE);
		conVarianceLabel.setText("Variance:");
		
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(conVarianceSpinner, -5);
		labelData.top = new FormAttachment(conVarianceSpinner, 0, SWT.CENTER);
		conVarianceLabel.setLayoutData(labelData);
		
		conVarianceLabel.setEnabled(false);
		conVarianceSpinner.setEnabled(false);
		
		randomizeCONButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				conVarianceLabel.setEnabled(randomizeCONButton.getSelection());
				conVarianceSpinner.setEnabled(randomizeCONButton.getSelection());
			}
		});
		
		randomizeAffinityButton = new Button(container, SWT.CHECK);
		randomizeAffinityButton.setText("Randomize Affinity");
		randomizeAffinityButton.setToolTipText("Randomizes affinity, which affects support bonuses.");
		
		FormData affinityData = new FormData();
		affinityData.left = new FormAttachment(randomizeCONButton, 0, SWT.LEFT);
		affinityData.top = new FormAttachment(conVarianceSpinner, 5);
		randomizeAffinityButton.setLayoutData(affinityData);
	}

	public FE9OtherCharacterOptions getOptions() {
		return new FE9OtherCharacterOptions(randomizeCONButton.getSelection(), conVarianceSpinner.getSelection(), randomizeAffinityButton.getSelection());
	}

	public void initialize(FE9OtherCharacterOptions options) {
		if (options == null) {
			return;
		}

		randomizeCONButton.setSelection(options.randomizeCON);
		conVarianceLabel.setEnabled(options.randomizeCON);
		conVarianceSpinner.setEnabled(options.randomizeCON);
		conVarianceSpinner.setSelection(options.conVariance);
		randomizeAffinityButton.setSelection(options.randomizeAffinity);
	}
}
