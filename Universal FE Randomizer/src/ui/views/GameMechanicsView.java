package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import fedata.general.FEBase.GameType;
import ui.common.GuiUtil;
import ui.general.MinMaxControl;
import ui.model.GameMechanicOptions;
import ui.model.GameMechanicOptions.ExperienceRate;

public class GameMechanicsView extends YuneView<GameMechanicOptions> {
	

	private Button applyEnglishPatch; // pre-FE6 only
	private Button tripleEffectiveness; // FE7 only
	

	private GameMechanicOptions.ExperienceRate experienceRate;
	

	public Button singleRNButton;
	

	private Button addFogOfWarButton;
	private Label fogOfWarChanceLabel;
	private Spinner fogOfWarChanceSpinner;
	private MinMaxControl fogOfWarVisionRangeControl;
	
	private Button casualModeButton;
	
	private Group experienceRateGroup;
	private Button normalExperienceButton;
	private Button paragonButton;
	private Button renegadeButton;
	
	// FE4 only.
	private Button followupRequirement;
	private Label withPursuitLabel;
	private Label withoutPursuitLabel;
	private Spinner withPursuitSpinner;
	private Spinner withoutPursuitSpinner;
	
	public GameMechanicsView(Composite parent, GameType gameType) {
		super(parent, gameType);
	}

	@Override
	public String getGroupTitle() {
		return "Game Mechanics";
	}

	@Override
	protected void compose() {
		Control previousControl = null;
		
		if (type.hasEnglishPatch()) {
			applyEnglishPatch = new Button(group, SWT.CHECK);
			applyEnglishPatch.setText("Apply English Patch");
			switch (type) {
			case FE4:
				applyEnglishPatch.setToolTipText("Applies the Project Naga localization patch.");
				break;
			case FE6:
				applyEnglishPatch.setToolTipText("Applies the FE6 Localization Patch v1.1.1.");
				break;
			default:
				break;
			}
			
			FormData patchData = new FormData();
			patchData.left = new FormAttachment(0, 5);
			patchData.top = new FormAttachment(0, 5);

			applyEnglishPatch.setLayoutData(patchData);

			previousControl = applyEnglishPatch;
		}
		
		if (type == GameType.FE7) {
			tripleEffectiveness = new Button(group, SWT.CHECK);
			tripleEffectiveness.setText("Set Effectiveness to 3x");
			tripleEffectiveness.setToolTipText("Reverts the weapon effectiveness to 3x like in the Japanese release, instead of 2x.");
			
			FormData effectivenessData = new FormData();
			effectivenessData.left = new FormAttachment(0, 5);
			if (previousControl == null) {
				effectivenessData.top = new FormAttachment(0, 5);
			} else {
				effectivenessData.top = new FormAttachment(previousControl, 10);
			}
			tripleEffectiveness.setLayoutData(effectivenessData);
			previousControl = tripleEffectiveness;
		}

		//////////////////////////////////////////////////////////////////

		if (type.isGBA()) {
			singleRNButton = new Button(group, SWT.CHECK);
			singleRNButton.setText("Enable Single RN for Hit");
			singleRNButton.setToolTipText("Makes accuracy rolls based on a single random number instead of the average of two random numbers.\n\nGood for those that don't like being lied to about hit rates.");
			singleRNButton.setSelection(false);
			
			FormData rnData = new FormData();
			rnData.left = new FormAttachment(0, 5);
			rnData.top = new FormAttachment(previousControl, 10);
			singleRNButton.setLayoutData(rnData);
			
			previousControl = singleRNButton;
		}

		// Fog of War
		if (type.isGBA()) {
			addFogOfWarButton = new Button(group, SWT.CHECK);
			addFogOfWarButton.setText("Add Fog of War (Beta)");
			if (type == GameType.FE7) {
				addFogOfWarButton.setToolTipText("Adds a chance for maps to feature fog of war.\n\nMaps featuring Kishuna are exempt.");
			} else {
				addFogOfWarButton.setToolTipText("Adds a chance for maps to have fog of war.");
			}
			addFogOfWarButton.setSelection(false);
			addFogOfWarButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					// TODO Auto-generated method stub
					fogOfWarChanceSpinner.setEnabled(addFogOfWarButton.getSelection());
					fogOfWarChanceLabel.setEnabled(addFogOfWarButton.getSelection());
					fogOfWarVisionRangeControl.setEnabled(addFogOfWarButton.getSelection());
				}
			});
			
			FormData formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(previousControl, 10);
			addFogOfWarButton.setLayoutData(formData);
			
			fogOfWarChanceSpinner = new Spinner(group, SWT.CHECK);
			fogOfWarChanceSpinner.setValues(10, 1, 100, 0, 1, 1);
			fogOfWarChanceSpinner.setEnabled(false);
			
			formData = new FormData();
			formData.right = new FormAttachment(100, -5);
			formData.top = new FormAttachment(addFogOfWarButton, 5);
			fogOfWarChanceSpinner.setLayoutData(formData);
			
			fogOfWarChanceLabel = new Label(group, SWT.NONE);
			fogOfWarChanceLabel.setText("Fog of War Chance:");
			fogOfWarChanceLabel.setEnabled(false);
			
			formData = new FormData();
			formData.right = new FormAttachment(fogOfWarChanceSpinner, -5);
			formData.top = new FormAttachment(fogOfWarChanceSpinner, 0, SWT.CENTER);
			fogOfWarChanceLabel.setLayoutData(formData);
			
			fogOfWarVisionRangeControl = new MinMaxControl(group, SWT.NONE, "Vision Range", "~");
			fogOfWarVisionRangeControl.setMin(3);
			fogOfWarVisionRangeControl.getMinSpinner().setValues(3, 1, 6, 0, 1, 1);
			fogOfWarVisionRangeControl.setMax(6);
			fogOfWarVisionRangeControl.getMaxSpinner().setValues(6, 3, 15, 0, 1, 1);
			fogOfWarVisionRangeControl.setEnabled(false);
			
			formData = new FormData();
			formData.right = new FormAttachment(100, 0);
			formData.top = new FormAttachment(fogOfWarChanceSpinner, 5);
			formData.left = new FormAttachment(addFogOfWarButton, 5, SWT.LEFT);
			fogOfWarVisionRangeControl.setLayoutData(formData);
			
			previousControl = fogOfWarVisionRangeControl;
		}
		
		if (type.isGBA()) {
			casualModeButton = new Button(group, SWT.CHECK);
			casualModeButton.setText("Enable Casual Mode");
			casualModeButton.setToolTipText("Disables permadeath. Defeated playable characters are available again in the next chapter.\n\nThe normal Game Over triggers are still active (i.e. Lord defeated).");
			casualModeButton.setSelection(false);
			
			FormData formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(previousControl, 10);
			casualModeButton.setLayoutData(formData);
			
			experienceRateGroup = new Group(group, SWT.NONE);
			experienceRateGroup.setText("Experience Rate");
			experienceRateGroup.setLayout(GuiUtil.formLayoutWithMargin());
			
			formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.right = new FormAttachment(100, -5);
			formData.top = new FormAttachment(casualModeButton, 10);
			experienceRateGroup.setLayoutData(formData);
			
			normalExperienceButton = new Button(experienceRateGroup, SWT.RADIO);
			normalExperienceButton.setText("Normal");
			normalExperienceButton.setToolTipText("Normal Experience Gain.");
			normalExperienceButton.setSelection(true);
			experienceRate = ExperienceRate.NORMAL;
			
			normalExperienceButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					experienceRate = ExperienceRate.NORMAL;
				}
			});
			
			formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(0, 5);
			normalExperienceButton.setLayoutData(formData);
			
			paragonButton = new Button(experienceRateGroup, SWT.RADIO);
			paragonButton.setText("Paragon Mode");
			paragonButton.setToolTipText("Doubles Experience Gain.");
			paragonButton.setSelection(false);
			paragonButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					experienceRate = ExperienceRate.PARAGON;
				}
			});
			
			formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(normalExperienceButton, 5);
			paragonButton.setLayoutData(formData);
			
			renegadeButton = new Button(experienceRateGroup, SWT.RADIO);
			renegadeButton.setText("Renegade Mode");
			renegadeButton.setToolTipText("Halves Experience Gain.");
			renegadeButton.setSelection(false);
			renegadeButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					experienceRate = ExperienceRate.RENEGADE;
				}
			});
			
			formData = new FormData();
			formData.left = new FormAttachment(0, 5);
			formData.top = new FormAttachment(paragonButton, 5);
			renegadeButton.setLayoutData(formData);
			
			previousControl = experienceRateGroup;
		}
		
		if (type == GameType.FE4) {
			followupRequirement = new Button(group, SWT.CHECK);
			followupRequirement.setText("Remove Pursuit Follow-up Requirement");
			followupRequirement.setToolTipText("Modifies the battle system so that the Pursuit skill is not needed to make follow-up attacks.");
			followupRequirement.setSelection(false);
			
			FormData followupData = new FormData();
			followupData.left = new FormAttachment(0, 5);
			followupData.top = new FormAttachment(previousControl, 10);
			followupRequirement.setLayoutData(followupData);
			
			withoutPursuitSpinner = new Spinner(group, SWT.NONE);
			withoutPursuitSpinner.setValues(6, 1, 10, 0, 1, 1);
			withoutPursuitSpinner.setEnabled(false);
			withoutPursuitSpinner.setToolTipText("Sets the minimum Attack Speed advantage needed to perform follow-up attacks without the Pursuit skill.");
			
			FormData spinnerData = new FormData();
			spinnerData.right = new FormAttachment(100, -5);
			spinnerData.top = new FormAttachment(followupRequirement, 5);
			withoutPursuitSpinner.setLayoutData(spinnerData);
			
			withoutPursuitLabel = new Label(group, SWT.NONE);
			withoutPursuitLabel.setText("AS Threshold w/o Pursuit:");
			withoutPursuitLabel.setEnabled(false);
			
			FormData labelData = new FormData();
			labelData.right = new FormAttachment(withoutPursuitSpinner, -5);
			labelData.top = new FormAttachment(withoutPursuitSpinner, 0, SWT.CENTER);
			withoutPursuitLabel.setLayoutData(labelData);
			
			withPursuitSpinner = new Spinner(group, SWT.NONE);
			withPursuitSpinner.setValues(3, 1, 10, 0, 1, 1);
			withPursuitSpinner.setEnabled(false);
			withPursuitSpinner.setToolTipText("Sets the minimum Attack Speed advantage needed to perform follow-up attacks with the Pursuit skill.");
			
			spinnerData = new FormData();
			spinnerData.right = new FormAttachment(100, -5);
			spinnerData.top = new FormAttachment(withoutPursuitSpinner, 5);
			withPursuitSpinner.setLayoutData(spinnerData);
			
			withPursuitLabel = new Label(group, SWT.NONE);
			withPursuitLabel.setText("AS Threshold w/ Pursuit:");
			withPursuitLabel.setEnabled(false);
			
			labelData = new FormData();
			labelData.right = new FormAttachment(withPursuitSpinner, -5);
			labelData.top = new FormAttachment(withPursuitSpinner, 0, SWT.CENTER);
			withPursuitLabel.setLayoutData(labelData);
			
			followupRequirement.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					withoutPursuitLabel.setEnabled(followupRequirement.getSelection());
					withoutPursuitSpinner.setEnabled(followupRequirement.getSelection());
					withPursuitLabel.setEnabled(followupRequirement.getSelection());
					withPursuitSpinner.setEnabled(followupRequirement.getSelection());
				}
			});
		}
	}

	@Override
	public GameMechanicOptions getOptions() {
		if (type.isGBA()) {
			switch (type) {
			case FE6:
				return new GameMechanicOptions(applyEnglishPatch.getSelection(), false, singleRNButton.getSelection(), addFogOfWarButton.getSelection(), fogOfWarChanceSpinner.getSelection(), fogOfWarVisionRangeControl.getMinMaxOption(), casualModeButton.getSelection(), experienceRate);
			case FE7:
			default:
				return new GameMechanicOptions(tripleEffectiveness != null ? tripleEffectiveness.getSelection() : false, singleRNButton.getSelection(), addFogOfWarButton.getSelection(), fogOfWarChanceSpinner.getSelection(), fogOfWarVisionRangeControl.getMinMaxOption(), casualModeButton.getSelection(), experienceRate);
			}
		} else if (type.isSFC()) {
			switch (type) {
			case FE4:
				return new GameMechanicOptions(applyEnglishPatch.getSelection(), new GameMechanicOptions.FollowupRequirement(!followupRequirement.getSelection(), withPursuitSpinner.getSelection(), withoutPursuitSpinner.getSelection()));
			default:
				return new GameMechanicOptions(false, false, false, false, 0, null, false, ExperienceRate.NORMAL);
			}
		}
		return new GameMechanicOptions(false, false, false, false, 0, null, false, ExperienceRate.NORMAL);
	}

	@Override
	public void initialize(GameMechanicOptions options) {
		if (options == null) {
			// Shouldn't happen.
			return;
		}

		if (applyEnglishPatch != null) {
			applyEnglishPatch.setSelection(options.applyEnglishPatch);
		}
		if (tripleEffectiveness != null) {
			tripleEffectiveness.setSelection(options.tripleEffectiveness);
		}

		if (singleRNButton != null) {
			singleRNButton.setSelection(options.singleRNMode);
		}

		if (addFogOfWarButton != null) {
			addFogOfWarButton.setSelection(options.randomizeFogOfWar);

			fogOfWarChanceSpinner.setEnabled(true);
			fogOfWarChanceLabel.setEnabled(true);
			fogOfWarVisionRangeControl.setEnabled(true);

			fogOfWarChanceSpinner.setSelection(options.fogOfWarChance);
			fogOfWarVisionRangeControl.setMin(options.fogOfWarVisionRange.minValue);
			fogOfWarVisionRangeControl.setMax(options.fogOfWarVisionRange.maxValue);
		}

		if (casualModeButton != null) {
			casualModeButton.setSelection(options.casualMode);
		}

		if (experienceRateGroup != null) {
			normalExperienceButton.setSelection(ExperienceRate.NORMAL.equals(options.experienceRate));
			paragonButton.setSelection(ExperienceRate.PARAGON.equals(options.experienceRate));
			renegadeButton.setSelection(ExperienceRate.RENEGADE.equals(options.experienceRate));
		}

		experienceRate = options.experienceRate;
	}
}
