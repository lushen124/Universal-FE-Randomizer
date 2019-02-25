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
import org.eclipse.swt.widgets.Listener;

import ui.model.RecruitmentOptions;
import ui.model.RecruitmentOptions.StatAdjustmentMode;

public class RecruitmentView extends Composite {
	
	private Group container;
	
	private Button enableButton;
	
	private Button autolevelButton;
	private Button absoluteButton;
	private Button relativeButton;
	
	public RecruitmentView(Composite parent, int style) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		container.setText("Recruitment");
		container.setToolTipText("Randomized character join order.");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		enableButton = new Button(container, SWT.CHECK);
		enableButton.setText("Randomize Recruitment");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				autolevelButton.setEnabled(true);
				absoluteButton.setEnabled(true);
				relativeButton.setEnabled(true);
			}
		});
		
		FormData enableData = new FormData();
		enableData.left = new FormAttachment(0, 0);
		enableData.top = new FormAttachment(0, 0);
		enableButton.setLayoutData(enableData);
		
		autolevelButton = new Button(container, SWT.RADIO);
		autolevelButton.setText("Autolevel Base Stats");
		autolevelButton.setToolTipText("Uses the character's growth rates to simulate leveling up or down from the character's original stats to their target level.");
		autolevelButton.setEnabled(false);
		autolevelButton.setSelection(true);
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(enableButton, 10, SWT.LEFT);
		optionData.top = new FormAttachment(enableButton, 10);
		autolevelButton.setLayoutData(optionData);
		
		absoluteButton = new Button(container, SWT.RADIO);
		absoluteButton.setText("Match Base Stats");
		absoluteButton.setToolTipText("Sets a character's base stats to match the character they replace.");
		absoluteButton.setEnabled(false);
		absoluteButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(autolevelButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(autolevelButton, 5);
		absoluteButton.setLayoutData(optionData);
		
		relativeButton = new Button(container, SWT.RADIO);
		relativeButton.setText("Relative Base Stats");
		relativeButton.setToolTipText("Pins the character's max stat to the max stat of the character they replace and retains the character's stat spread.");
		relativeButton.setEnabled(false);
		relativeButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(absoluteButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(absoluteButton, 5);
		relativeButton.setLayoutData(optionData);
	}
	
	public RecruitmentOptions getRecruitmentOptions() {
		boolean isEnabled = enableButton.getSelection();
		StatAdjustmentMode mode = null;
		if (autolevelButton.getSelection()) { mode = StatAdjustmentMode.AUTOLEVEL; }
		else if (absoluteButton.getSelection()) { mode = StatAdjustmentMode.MATCH_SLOT; }
		else if (relativeButton.getSelection()) { mode = StatAdjustmentMode.RELATIVE_TO_SLOT; }
		
		if (isEnabled && mode != null) {
			return new RecruitmentOptions(mode);
		} else {
			return null;
		}
	}
	
	public void setRecruitmentOptions(RecruitmentOptions options) {
		if (options == null) {
			enableButton.setSelection(false);
			autolevelButton.setEnabled(false);
			absoluteButton.setEnabled(false);
			relativeButton.setEnabled(false);
		} else {
			enableButton.setSelection(true);
			autolevelButton.setEnabled(true);
			absoluteButton.setEnabled(true);
			relativeButton.setEnabled(true);
			
			autolevelButton.setSelection(options.statMode == StatAdjustmentMode.AUTOLEVEL);
			absoluteButton.setSelection(options.statMode == StatAdjustmentMode.MATCH_SLOT);
			relativeButton.setSelection(options.statMode == StatAdjustmentMode.RELATIVE_TO_SLOT);
		}
	}
}
