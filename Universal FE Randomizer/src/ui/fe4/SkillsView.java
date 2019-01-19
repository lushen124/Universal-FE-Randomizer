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
import org.eclipse.swt.widgets.Listener;

import ui.fe4.SkillCountView.SkillCountListener;
import ui.fe4.SkillWeightView.SkillWeightsListener;

public class SkillsView extends Composite {
	
	private boolean skillsEnabled;
	private SkillsOptions.Mode currentMode = SkillsOptions.Mode.SHUFFLE;
	
	private Group container;
	private Button enableButton;
	
	private Button retainSkillCountsButton;
	
	private Button shuffleButton;
	private Button separateByGeneration;
	
	private Button randomizeButton;
	private SkillCountView skillCountView;
	private SkillWeightView skillWeightView;
	
	public SkillsView(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new FillLayout());
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Skills");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginRight = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		container.setLayout(mainLayout);
		
		enableButton = new Button(container, SWT.CHECK);
		enableButton.setText("Enable Skill Randomization");
		enableButton.setToolTipText("Randomizes the personal skills of playable characters. Child characters are not affected by any settings and continue to rely on inheritence from parents.");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				skillsEnabled = enableButton.getSelection();
				
				retainSkillCountsButton.setEnabled(skillsEnabled);
				shuffleButton.setEnabled(skillsEnabled);
				randomizeButton.setEnabled(skillsEnabled);
				
				if (skillsEnabled) {
					skillCountView.setEnabled(randomizeButton.getSelection() && !retainSkillCountsButton.getSelection());
					skillWeightView.setEnabled(randomizeButton.getSelection());
					
					separateByGeneration.setEnabled(shuffleButton.getSelection());
				} else {
					skillCountView.setEnabled(false);
					skillWeightView.setEnabled(false);
					
					separateByGeneration.setEnabled(false);
				}
			}
		});
		
		retainSkillCountsButton = new Button(container, SWT.CHECK);
		retainSkillCountsButton.setText("Retain Number of Skills");
		retainSkillCountsButton.setToolTipText("Retains each character's normal number of skills.\n\ne.g. A character with 2 personal skills will continue to have 2 personal skills after shuffling or randomization.");
		retainSkillCountsButton.setEnabled(false);
		retainSkillCountsButton.setSelection(true);
		retainSkillCountsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				skillCountView.setEnabled(!retainSkillCountsButton.getSelection() && randomizeButton.getSelection());
			}
		});
		
		FormData retainCountData = new FormData();
		retainCountData.left = new FormAttachment(enableButton, 10, SWT.LEFT);
		retainCountData.top = new FormAttachment(enableButton, 10);
		retainSkillCountsButton.setLayoutData(retainCountData);
		
		shuffleButton = new Button(container, SWT.RADIO);
		shuffleButton.setText("Shuffle");
		shuffleButton.setToolTipText("Shuffles and redistributes all playable characters' existing skills.");
		shuffleButton.setEnabled(false);
		shuffleButton.setSelection(true);
		shuffleButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(SkillsOptions.Mode.SHUFFLE);
			}
		});
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(retainSkillCountsButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(retainSkillCountsButton, 10);
		shuffleButton.setLayoutData(optionData);
		
		separateByGeneration = new Button(container, SWT.CHECK);
		separateByGeneration.setText("Separate Pools by Generation");
		separateByGeneration.setToolTipText("Shuffles Generation 1 character skills separately from Generation 2 Common Characters and Substitutes.");
		separateByGeneration.setEnabled(false);
		separateByGeneration.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(shuffleButton, 10, SWT.LEFT);
		optionData.top = new FormAttachment(shuffleButton, 5);
		separateByGeneration.setLayoutData(optionData);
		
		randomizeButton = new Button(container, SWT.RADIO);
		randomizeButton.setText("Randomize");
		randomizeButton.setToolTipText("Randomizes all skills on all playable characters.");
		randomizeButton.setEnabled(false);
		randomizeButton.setSelection(false);
		randomizeButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(SkillsOptions.Mode.RANDOMIZE);
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(shuffleButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(separateByGeneration, 10);
		randomizeButton.setLayoutData(optionData);
		
		skillCountView = new SkillCountView(container, SWT.NONE);
		skillCountView.setEnabled(false);
		skillCountView.setListener(new SkillCountListener() {
			@Override
			public void onAllItemsDisabled() {
				retainSkillCountsButton.setSelection(true);
				skillCountView.setEnabled(false);
			}
		});
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(randomizeButton, 0, SWT.LEFT);
		viewData.top = new FormAttachment(randomizeButton, 0);
		viewData.right = new FormAttachment(100, -5);
		viewData.width = 280;
		skillCountView.setLayoutData(viewData);
		
		skillWeightView = new SkillWeightView(container, SWT.NONE);
		skillWeightView.setEnabled(false);
		skillWeightView.setListener(new SkillWeightsListener() {
			@Override
			public void onEnableCountChanged(int newCount) {
				skillCountView.setMaxSkillCount(newCount);
			}
			
			@Override
			public void onAllItemsDisabled() {
				randomizeButton.setSelection(false);
				shuffleButton.setSelection(true);
				setMode(SkillsOptions.Mode.SHUFFLE);
			}
		});
		
		viewData = new FormData();
		viewData.left = new FormAttachment(skillCountView, 0, SWT.LEFT);
		viewData.top = new FormAttachment(skillCountView, 0);
		viewData.right = new FormAttachment(100, -5);
		viewData.width = 280;
		skillWeightView.setLayoutData(viewData);
	}
	
	private void setMode(SkillsOptions.Mode mode) {
		currentMode = mode;
		if (skillsEnabled) {
			switch (mode) {
			case SHUFFLE:
				separateByGeneration.setEnabled(true);
				
				skillCountView.setEnabled(false);
				skillWeightView.setEnabled(false);
				break;
			case RANDOMIZE:
				separateByGeneration.setEnabled(false);
				
				skillCountView.setEnabled(!retainSkillCountsButton.getSelection());
				skillWeightView.setEnabled(true);
				break;
			}
		}
	}
	
	public SkillsOptions getSkillOptions() {
		if (!skillsEnabled) { return null; }
		return new SkillsOptions(currentMode, retainSkillCountsButton.getSelection(), separateByGeneration.getSelection(), skillCountView.getSkillCountDistribution(), skillWeightView.getSkillWeights());
	}
}
