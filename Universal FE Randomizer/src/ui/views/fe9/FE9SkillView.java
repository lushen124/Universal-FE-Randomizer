package ui.views.fe9;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;
import ui.common.GuiUtil;
import ui.model.fe9.FE9SkillsOptions;
import ui.model.fe9.FE9SkillsOptions.Mode;
import ui.views.YuneView;
import ui.views.fe9.SkillWeightView.SkillWeightsListener;

import java.util.List;

public class FE9SkillView extends YuneView<FE9SkillsOptions> {

	private int numberColumns;
	private List<String> skillList;

	private boolean skillsEnabled;
	
	private FE9SkillsOptions.Mode currentMode = Mode.RANDOMIZE_EXISTING;
	
	private Button enableButton;
	
	private Button randomizeExistingModeButton;
	private Button randomizeFullModeButton;
	
	private Label countLabel;
	private Spinner skillCountSpinner;
	
	private SkillWeightView weightsView;
	
	public FE9SkillView(Composite parent, List<String> skillList, int skillColumns) {
		super();
		createGroup(parent);
		this.skillList = skillList;
		this.numberColumns = skillColumns;
		compose();
	}

	@Override
	public String getGroupTitle() {
		return "Skills";
	}

	@Override
	protected void compose(){
		enableButton = new Button(group, SWT.CHECK);
		enableButton.setText("Enable Skill Randomization");
		enableButton.setToolTipText("Randomizes skils for playable characters.");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setEnabled(enableButton.getSelection());
			}
		});
		
		randomizeExistingModeButton = new Button(group, SWT.RADIO);
		randomizeExistingModeButton.setText("Randomize Existing Skills");
		randomizeExistingModeButton.setToolTipText("Only randomizes skills that already exist.\nA character with no skills will still have no skills.\nA character with a skill may have a different skill.");
		randomizeExistingModeButton.setSelection(true);
		
		randomizeFullModeButton = new Button(group, SWT.RADIO);
		randomizeFullModeButton.setText("Fully Randomize Skills");
		randomizeFullModeButton.setToolTipText("Randomizes skills for all characters.\nA character with no skills may gain a skill.\nA character with an existing skill may have a different skill or lose it.");
		
		FormData modeData = new FormData();
		modeData.left = new FormAttachment(enableButton, 10, SWT.LEFT);
		modeData.top = new FormAttachment(enableButton, 10);
		randomizeExistingModeButton.setLayoutData(modeData);
		
		modeData = new FormData();
		modeData.left = new FormAttachment(randomizeExistingModeButton, 0, SWT.LEFT);
		modeData.top = new FormAttachment(randomizeExistingModeButton, 10);
		randomizeFullModeButton.setLayoutData(modeData);
		
		skillCountSpinner = new Spinner(group, SWT.NONE);
		skillCountSpinner.setValues(80, 0, 100, 0, 1, 5);
		skillCountSpinner.setToolTipText("Sets the chance a character has to have a skill.");
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.top = new FormAttachment(randomizeFullModeButton, 10);
		skillCountSpinner.setLayoutData(spinnerData);
		
		countLabel = new Label(group, SWT.NONE);
		countLabel.setText("Chance for Skill: ");
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(randomizeFullModeButton, 5, SWT.LEFT);
		labelData.top = new FormAttachment(skillCountSpinner, 0, SWT.CENTER);
		labelData.right = new FormAttachment(skillCountSpinner, -5);
		countLabel.setLayoutData(labelData);
		
		randomizeExistingModeButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(FE9SkillsOptions.Mode.RANDOMIZE_EXISTING);
			}
		});
		
		randomizeFullModeButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(FE9SkillsOptions.Mode.FULL_RANDOM);
			}
		});
		
		weightsView = new SkillWeightView(group, SWT.NONE, skillList, numberColumns);
		weightsView.setListener(new SkillWeightsListener() {
			@Override
			public void onEnableCountChanged(int enabledCount) {}
			
			@Override
			public void onAllItemsDisabled() {
				enableButton.setSelection(false);
				setEnabled(false);
			}
		});
		
		FormData weightData = new FormData();
		weightData.left = new FormAttachment(enableButton, 0, SWT.LEFT);
		weightData.top = new FormAttachment(skillCountSpinner, 5);
		weightData.right = new FormAttachment(100, -5);
		weightsView.setLayoutData(weightData);
		
		setEnabled(false);
	}
	
	public void setMode(FE9SkillsOptions.Mode mode) {
		currentMode = mode;
		switch (mode) {
		case RANDOMIZE_EXISTING:
			countLabel.setEnabled(false);
			skillCountSpinner.setEnabled(false);
			randomizeExistingModeButton.setSelection(true);
			randomizeFullModeButton.setSelection(false);
			break;
		case FULL_RANDOM:
			countLabel.setEnabled(skillsEnabled);
			skillCountSpinner.setEnabled(skillsEnabled);
			randomizeFullModeButton.setSelection(true);
			randomizeExistingModeButton.setSelection(false);
			break;
		}
	}
	
	public void setEnabled(boolean enabled) {
		skillsEnabled = enabled;
		enableButton.setSelection(skillsEnabled);
		
		randomizeExistingModeButton.setEnabled(skillsEnabled);
		randomizeFullModeButton.setEnabled(skillsEnabled);
		
		countLabel.setEnabled(skillsEnabled && randomizeFullModeButton.getSelection());
		skillCountSpinner.setEnabled(skillsEnabled && randomizeFullModeButton.getSelection());
		
		weightsView.setEnabled(skillsEnabled);
	}

	@Override
	public FE9SkillsOptions getOptions() {
		if (!skillsEnabled) { return null; }
		return new FE9SkillsOptions(currentMode, skillCountSpinner.getSelection(), weightsView.getSkillWeights());
	}

	@Override
	public void initialize(FE9SkillsOptions options) {
		if (options == null) {
			setEnabled(false);
			setMode(FE9SkillsOptions.Mode.RANDOMIZE_EXISTING);
		} else {
			setEnabled(true);
			setMode(options.mode);
			skillCountSpinner.setSelection(options.skillChance);
			weightsView.setSkillWeights(options.skillWeights);
		}
	}
}
