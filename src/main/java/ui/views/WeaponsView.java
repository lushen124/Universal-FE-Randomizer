package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
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
import ui.views.WeaponEffectSelectionView.WeaponEffectSelectionViewListener;
import ui.general.MinMaxControl;
import ui.model.MinMaxVarOption;
import ui.model.WeaponOptions;

public class WeaponsView extends YuneView<WeaponOptions> {

	private int numberColumns;

	private Button enableMightButton;
	private Spinner mightVarianceSpinner;
	private MinMaxControl mightRangeControl;
	
	private Button enableHitButton;
	private Spinner hitVarianceSpinner;
	private MinMaxControl hitRangeControl;
	
	private Button enableWeightButton;
	private Spinner weightVarianceSpinner;
	private MinMaxControl weightRangeControl;
	
	private Button enableDurabilityButton;
	private Spinner durabilityVarianceSpinner;
	private MinMaxControl durabilityRangeControl;
	
	private Button enableRandomEffectsButton;
	private Button noEffectsForIronButton;
	private Button noEffectsForSteelButton;
	private Button noEffectsForBasicThrownButton;
	private Button includeLaguzButton;
	private Label effectChanceLabel;
	private Spinner effectChanceSpinner;
	private WeaponEffectSelectionView effectsSelectionView;
	
	public WeaponsView(Composite parent, GameType type, int numberColumns) {
		super();
		createGroup(parent);
		this.type = type;
		this.numberColumns = numberColumns;
		compose();
	}

	@Override
	public String getGroupTitle() {
		return "Weapons";
	}

	protected void compose() {
		enableMightButton = new Button(group, SWT.CHECK);
		enableMightButton.setText("Randomize Power (MT)");
		enableMightButton.setToolTipText("Applies a random delta +/- Variance to all weapons' MT stat. All weapons are then clamped to the min and max specified.");

		FormData mtData = new FormData();
		mtData.left = new FormAttachment(0, 5);
		mtData.top = new FormAttachment(0, 5);
		enableMightButton.setLayoutData(mtData);

		Composite mtParamContainer = new Composite(group, SWT.NONE);

		mtParamContainer.setLayout(GuiUtil.formLayoutWithMargin());

		Label mtVarianceLabel = new Label(mtParamContainer, SWT.RIGHT);
		mtVarianceLabel.setText("Variance:");

		mightVarianceSpinner = new Spinner(mtParamContainer, SWT.NONE);
		mightVarianceSpinner.setValues(3, 1, 31, 0, 1, 1);
		mightVarianceSpinner.setEnabled(false);

		FormData mtVarLabelData = new FormData();
		mtVarLabelData.left = new FormAttachment(0, 5);
		mtVarLabelData.right = new FormAttachment(50, -5);
		mtVarLabelData.top = new FormAttachment(mightVarianceSpinner, 0, SWT.CENTER);
		mtVarianceLabel.setLayoutData(mtVarLabelData);

		FormData mtVarSpinnerData = new FormData();
		mtVarSpinnerData.left = new FormAttachment(50, 0);
		mtVarSpinnerData.top = new FormAttachment(0, 5);
		mightVarianceSpinner.setLayoutData(mtVarSpinnerData);

		mightRangeControl = new MinMaxControl(mtParamContainer, SWT.NONE, "Min MT:", "Max MT:");

		mightRangeControl.getMinSpinner().setValues(0, 0, 31, 0, 1, 1);
		mightRangeControl.getMaxSpinner().setValues(23, 0, 31, 0, 1, 1);
		mightRangeControl.setEnabled(false);

		FormData mtRangeControlData = new FormData();
		mtRangeControlData.top = new FormAttachment(mightVarianceSpinner, 5);
		mtRangeControlData.left = new FormAttachment(0, 5);
		mtRangeControlData.right = new FormAttachment(100, -5);
		mightRangeControl.setLayoutData(mtRangeControlData);

		enableMightButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mightRangeControl.setEnabled(enableMightButton.getSelection());
				mightVarianceSpinner.setEnabled(enableMightButton.getSelection());
			}
		});

		FormData mtContainerData = new FormData();
		mtContainerData.left = new FormAttachment(enableMightButton, 0, SWT.LEFT);
		mtContainerData.top = new FormAttachment(enableMightButton, 0);
		mtParamContainer.setLayoutData(mtContainerData);

		///////////////////////////////////////////////////////

		enableHitButton = new Button(group, SWT.CHECK);
		enableHitButton.setText("Randomize Accuracy (Hit)");
		enableHitButton.setToolTipText("Applies a random delta +/- Variance to all weapons' accuracy. All weapons are then clamped to the min and max specified.");

		FormData hitData = new FormData();
		hitData.left = new FormAttachment(0, 5);
		hitData.top = new FormAttachment(mtParamContainer, 5);
		enableHitButton.setLayoutData(hitData);

		Composite hitParamContainer = new Composite(group, SWT.NONE);
		hitParamContainer.setLayout(GuiUtil.formLayoutWithMargin());

		Label hitVarianceLabel = new Label(hitParamContainer, SWT.RIGHT);
		hitVarianceLabel.setText("Variance:");

		hitVarianceSpinner = new Spinner(hitParamContainer, SWT.NONE);
		hitVarianceSpinner.setValues(20, 1, 255, 0, 1, 5);
		hitVarianceSpinner.setEnabled(false);

		FormData hitVarLabelData = new FormData();
		hitVarLabelData.left = new FormAttachment(0, 5);
		hitVarLabelData.right = new FormAttachment(50, -5);
		hitVarLabelData.top = new FormAttachment(hitVarianceSpinner, 0, SWT.CENTER);
		hitVarianceLabel.setLayoutData(hitVarLabelData);

		FormData hitVarSpinnerData = new FormData();
		hitVarSpinnerData.left = new FormAttachment(50, 0);
		hitVarSpinnerData.top = new FormAttachment(0, 5);
		hitVarianceSpinner.setLayoutData(hitVarSpinnerData);

		hitRangeControl = new MinMaxControl(hitParamContainer, SWT.NONE, "Min Hit:", "Max Hit:");

		hitRangeControl.getMinSpinner().setValues(55, 0, 255, 0, 1, 5);
		hitRangeControl.getMaxSpinner().setValues(100, 0, 255, 0, 1, 5);
		hitRangeControl.setEnabled(false);

		FormData hitRangeControlData = new FormData();
		hitRangeControlData.top = new FormAttachment(hitVarianceSpinner, 5);
		hitRangeControlData.left = new FormAttachment(0, 5);
		hitRangeControlData.right = new FormAttachment(100, -5);
		hitRangeControl.setLayoutData(hitRangeControlData);

		enableHitButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				hitRangeControl.setEnabled(enableHitButton.getSelection());
				hitVarianceSpinner.setEnabled(enableHitButton.getSelection());
			}
		});

		FormData hitContainerData = new FormData();
		hitContainerData.left = new FormAttachment(enableHitButton, 0, SWT.LEFT);
		hitContainerData.top = new FormAttachment(enableHitButton, 0);
		hitParamContainer.setLayoutData(hitContainerData);

		///////////////////////////////////////////////////////

		enableWeightButton = new Button(group, SWT.CHECK);
		enableWeightButton.setText("Randomize Weights (WT)");
		enableWeightButton.setToolTipText("Applies a random delta +/- Variance to all weapons' weight. All weapons are then clamped to the min and max specified.");

		FormData wtData = new FormData();
		wtData.left = new FormAttachment(0, 5);
		wtData.top = new FormAttachment(hitParamContainer, 5);
		enableWeightButton.setLayoutData(wtData);

		Composite wtParamContainer = new Composite(group, SWT.NONE);
		wtParamContainer.setLayout(GuiUtil.formLayoutWithMargin());

		Label wtVarianceLabel = new Label(wtParamContainer, SWT.RIGHT);
		wtVarianceLabel.setText("Variance:");

		weightVarianceSpinner = new Spinner(wtParamContainer, SWT.NONE);
		weightVarianceSpinner.setValues(5, 1, 255, 0, 1, 5);
		weightVarianceSpinner.setEnabled(false);

		FormData wtVarLabelData = new FormData();
		wtVarLabelData.left = new FormAttachment(0, 5);
		wtVarLabelData.right = new FormAttachment(50, -5);
		wtVarLabelData.top = new FormAttachment(weightVarianceSpinner, 0, SWT.CENTER);
		wtVarianceLabel.setLayoutData(wtVarLabelData);

		FormData wtVarSpinnerData = new FormData();
		wtVarSpinnerData.left = new FormAttachment(50, 0);
		wtVarSpinnerData.top = new FormAttachment(0, 5);
		weightVarianceSpinner.setLayoutData(wtVarSpinnerData);

		weightRangeControl = new MinMaxControl(wtParamContainer, SWT.NONE, "Min WT:", "Max WT:");

		weightRangeControl.getMinSpinner().setValues(2, 1, 30, 0, 1, 5);
		weightRangeControl.getMaxSpinner().setValues(20, 1, 30, 0, 1, 5);
		weightRangeControl.setEnabled(false);

		FormData wtRangeControlData = new FormData();
		wtRangeControlData.top = new FormAttachment(weightVarianceSpinner, 5);
		wtRangeControlData.left = new FormAttachment(0, 5);
		wtRangeControlData.right = new FormAttachment(100, -5);
		weightRangeControl.setLayoutData(wtRangeControlData);

		enableWeightButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				weightRangeControl.setEnabled(enableWeightButton.getSelection());
				weightVarianceSpinner.setEnabled(enableWeightButton.getSelection());
			}
		});

		FormData wtContainerData = new FormData();
		wtContainerData.left = new FormAttachment(enableWeightButton, 0, SWT.LEFT);
		wtContainerData.top = new FormAttachment(enableWeightButton, 0);
		wtParamContainer.setLayoutData(wtContainerData);

		///////////////////////////////////////////////////////

		enableDurabilityButton = new Button(group, SWT.CHECK);
		enableDurabilityButton.setText("Randomize Durability");
		enableDurabilityButton.setToolTipText("Applies a random delta +/- Variance to all weapons' durability. All weapons are then clamped to the min and max specified. Siege tomes are limited to a 1-use minimum.");

		FormData durabilityData = new FormData();
		durabilityData.left = new FormAttachment(0, 5);
		durabilityData.top = new FormAttachment(wtParamContainer, 5);
		enableDurabilityButton.setLayoutData(durabilityData);

		Composite durabilityParamContainer = new Composite(group, SWT.NONE);
		durabilityParamContainer.setLayout(GuiUtil.formLayoutWithMargin());

		Label durabilityVarianceLabel = new Label(durabilityParamContainer, SWT.RIGHT);
		durabilityVarianceLabel.setText("Variance:");

		durabilityVarianceSpinner = new Spinner(durabilityParamContainer, SWT.NONE);
		durabilityVarianceSpinner.setValues(20, 1, 255, 0, 1, 5);
		durabilityVarianceSpinner.setEnabled(false);

		FormData durabilityVarLabelData = new FormData();
		durabilityVarLabelData.left = new FormAttachment(0, 5);
		durabilityVarLabelData.right = new FormAttachment(50, -5);
		durabilityVarLabelData.top = new FormAttachment(durabilityVarianceSpinner, 0, SWT.CENTER);
		durabilityVarianceLabel.setLayoutData(durabilityVarLabelData);

		FormData durabilityVarSpinnerData = new FormData();
		durabilityVarSpinnerData.left = new FormAttachment(50, 0);
		durabilityVarSpinnerData.top = new FormAttachment(0, 5);
		durabilityVarianceSpinner.setLayoutData(durabilityVarSpinnerData);

		durabilityRangeControl = new MinMaxControl(durabilityParamContainer, SWT.NONE, "Min Uses:", "Max Uses:");

		durabilityRangeControl.getMinSpinner().setValues(15, 1, 63, 0, 1, 5);
		durabilityRangeControl.getMaxSpinner().setValues(60, 1, 63, 0, 1, 5);
		durabilityRangeControl.setEnabled(false);

		FormData durabilityRangeControlData = new FormData();
		durabilityRangeControlData.top = new FormAttachment(durabilityVarianceSpinner, 5);
		durabilityRangeControlData.left = new FormAttachment(0, 5);
		durabilityRangeControlData.right = new FormAttachment(100, -5);
		durabilityRangeControl.setLayoutData(durabilityRangeControlData);

		enableDurabilityButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				durabilityRangeControl.setEnabled(enableDurabilityButton.getSelection());
				durabilityVarianceSpinner.setEnabled(enableDurabilityButton.getSelection());
			}
		});

		FormData durabilityContainerData = new FormData();
		durabilityContainerData.left = new FormAttachment(enableDurabilityButton, 0, SWT.LEFT);
		durabilityContainerData.top = new FormAttachment(enableDurabilityButton, 0);
		durabilityParamContainer.setLayoutData(durabilityContainerData);

		///////////////////////////////////////////////////////

		enableRandomEffectsButton = new Button(group, SWT.CHECK);
		enableRandomEffectsButton.setText("Add Random Effects");
		enableRandomEffectsButton.setToolTipText("Adds a random effect to all weapons. Effects includes stat bonuses, effectiveness, weapon triangle reversal, brave, magic damge, etc. Weapons that already have effects get a second effect added on.");

		if (numberColumns == 1) {
			FormData effectsData = new FormData();
			effectsData.left = new FormAttachment(0, 5);
			effectsData.top = new FormAttachment(durabilityParamContainer, 5);
			enableRandomEffectsButton.setLayoutData(effectsData);
		} else if (numberColumns == 2) {
			FormData effectsData = new FormData();
			effectsData.left = new FormAttachment(mtParamContainer, 5, SWT.RIGHT);
			effectsData.top = new FormAttachment(0, 5);
			enableRandomEffectsButton.setLayoutData(effectsData);
		}


		noEffectsForIronButton = new Button(group, SWT.CHECK);
		noEffectsForIronButton.setText("Safe Basic Weapons");
		if (type == GameType.FE9) {
			noEffectsForIronButton.setToolTipText("Iron Weapons (inc. Knife, Fire, Wind, Thunder, and Light) remain unchanged. This establishes a safe-zone for weapons to not be broken.");
		} else {
			noEffectsForIronButton.setToolTipText("Iron Weapons (inc. Fire, Lightning, and Flux) remain unchanged. This establishes a safe-zone for weapons to not be broken.");
		}
		noEffectsForIronButton.setEnabled(false);

		FormData ironData = new FormData();
		ironData.left = new FormAttachment(enableRandomEffectsButton, 10, SWT.LEFT);
		ironData.top = new FormAttachment(enableRandomEffectsButton, 5);
		noEffectsForIronButton.setLayoutData(ironData);

		Control lastControl = noEffectsForIronButton;

		if (type.isGBA()) {
			noEffectsForSteelButton = new Button(group, SWT.CHECK);
			noEffectsForSteelButton.setText("Safe Steel Weapons");
			noEffectsForSteelButton.setToolTipText("Steel Weapons (and Thunder) remain unchanged.");
			noEffectsForSteelButton.setEnabled(false);

			FormData steelData = new FormData();
			steelData.left = new FormAttachment(noEffectsForIronButton, 10, SWT.LEFT);
			steelData.top = new FormAttachment(noEffectsForIronButton, 5);
			noEffectsForSteelButton.setLayoutData(steelData);

			noEffectsForBasicThrownButton = new Button(group, SWT.CHECK);
			noEffectsForBasicThrownButton.setText("Safe Basic Thrown Weapons");
			noEffectsForBasicThrownButton.setToolTipText("Thrown Weapons (Javelin, Hand Axe) remain unchanged.");
			noEffectsForBasicThrownButton.setEnabled(false);

			FormData thrownData = new FormData();
			thrownData.left = new FormAttachment(noEffectsForSteelButton, 0, SWT.LEFT);
			thrownData.top = new FormAttachment(noEffectsForSteelButton, 5);
			noEffectsForBasicThrownButton.setLayoutData(thrownData);

			noEffectsForIronButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					noEffectsForSteelButton.setEnabled(noEffectsForIronButton.getSelection());
					noEffectsForBasicThrownButton.setEnabled(noEffectsForIronButton.getSelection());
					noEffectsForSteelButton.setSelection(noEffectsForIronButton.getSelection());
					noEffectsForBasicThrownButton.setSelection(noEffectsForIronButton.getSelection());
				}
			});

			lastControl = noEffectsForBasicThrownButton;
		} else if (type == GameType.FE9) {
			includeLaguzButton = new Button(group, SWT.CHECK);
			includeLaguzButton.setText("Include Laguz Weapons");
			includeLaguzButton.setToolTipText("Adds a random effect to claws, fangs, beaks, and breaths. All laguz of the same type share the same weapon trait.\nSome effects (like extended range) are not eligible for laguz weapons.");
			includeLaguzButton.setEnabled(false);

			FormData laguzData = new FormData();
			laguzData.left = new FormAttachment(noEffectsForIronButton, 0, SWT.LEFT);
			laguzData.top = new FormAttachment(noEffectsForIronButton, 5);
			includeLaguzButton.setLayoutData(laguzData);

			lastControl = includeLaguzButton;
		}

		effectChanceSpinner = new Spinner(group, SWT.NONE);
		effectChanceSpinner.setToolTipText("Sets the chance of an effect being added to a weapon.");
		effectChanceSpinner.setEnabled(false);
		effectChanceSpinner.setValues(25, 1, 100, 0, 1, 5);
		effectChanceSpinner.setEnabled(false);

		effectChanceLabel = new Label(group, SWT.NONE);
		effectChanceLabel.setText("Effect Chance:");
		effectChanceLabel.setEnabled(false);

		FormData spinnerData = new FormData();
		spinnerData.left = new FormAttachment(effectChanceLabel, 10);
		spinnerData.top = new FormAttachment(lastControl, 5);
		effectChanceSpinner.setLayoutData(spinnerData);

		FormData labelData = new FormData();
		labelData.left = new FormAttachment(noEffectsForIronButton, 0, SWT.LEFT);
		labelData.top = new FormAttachment(effectChanceSpinner, 0, SWT.CENTER);
		effectChanceLabel.setLayoutData(labelData);

		updateWeaponEffectSelectionViewForGame(type);
	}
	
	public void updateWeaponEffectSelectionViewForGame(GameType type) {
		if (effectsSelectionView != null) { effectsSelectionView.dispose(); }
		
		effectsSelectionView = new WeaponEffectSelectionView(group, type);
		effectsSelectionView.setEnabled(false);
		
		FormData effectData = new FormData();
		effectData.left = new FormAttachment(noEffectsForIronButton, 10, SWT.LEFT);
		effectData.top = new FormAttachment(effectChanceSpinner, 5);
		effectData.bottom = new FormAttachment(100, -5);
		effectData.width = 280;
		effectsSelectionView.setLayoutData(effectData);
		
		effectsSelectionView.setSelectionListener(new WeaponEffectSelectionViewListener() {
			@Override
			public void onSelectionChanged() {
				if (effectsSelectionView.isAllDisabled()) {
					enableRandomEffectsButton.setSelection(false);
					noEffectsForIronButton.setEnabled(false);
					if (noEffectsForSteelButton != null) { noEffectsForSteelButton.setEnabled(false); }
					if (noEffectsForBasicThrownButton != null) { noEffectsForBasicThrownButton.setEnabled(false); }
					effectChanceLabel.setEnabled(false);
					effectChanceSpinner.setEnabled(false);
					effectsSelectionView.setEnabled(false);
					if (includeLaguzButton != null) { includeLaguzButton.setEnabled(false); }
				}
			}
		});
		
		for (Listener listener : enableRandomEffectsButton.getListeners(SWT.Selection)) {
			enableRandomEffectsButton.removeListener(SWT.Selection, listener);
		}
		
		enableRandomEffectsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Boolean enabled = enableRandomEffectsButton.getSelection();
				effectsSelectionView.setEnabled(enabled);
				noEffectsForIronButton.setEnabled(enabled);
				if (noEffectsForSteelButton != null) { noEffectsForSteelButton.setEnabled(enabled); }
				if (noEffectsForBasicThrownButton != null) { noEffectsForBasicThrownButton.setEnabled(enabled); }
				effectChanceLabel.setEnabled(enabled);
				effectChanceSpinner.setEnabled(enabled);
				if (includeLaguzButton != null) { includeLaguzButton.setEnabled(enabled); }
				if (enabled) {
					effectsSelectionView.selectAll();
					noEffectsForIronButton.setSelection(true);
					if (noEffectsForSteelButton != null) { noEffectsForSteelButton.setSelection(true); }
					if (noEffectsForBasicThrownButton != null) { noEffectsForBasicThrownButton.setSelection(true); }
				} else {
					effectsSelectionView.deselectAll();
					noEffectsForIronButton.setSelection(false);
					if (noEffectsForSteelButton != null) { noEffectsForSteelButton.setSelection(false); }
					if (noEffectsForBasicThrownButton != null) { noEffectsForBasicThrownButton.setSelection(false); }
				}
			}
		});
	}

	@Override
	public WeaponOptions getOptions() {
		MinMaxVarOption mightOptions = null;
		MinMaxVarOption hitOptions = null;
		MinMaxVarOption weightOptions = null;
		MinMaxVarOption durabilityOptions = null;
		
		if (enableMightButton.getSelection()) {
			mightOptions = new MinMaxVarOption(mightRangeControl.getMinMaxOption(), mightVarianceSpinner.getSelection());
		}
		if (enableHitButton.getSelection()) {
			hitOptions = new MinMaxVarOption(hitRangeControl.getMinMaxOption(), hitVarianceSpinner.getSelection());
		}
		if (enableWeightButton.getSelection()) {
			weightOptions = new MinMaxVarOption(weightRangeControl.getMinMaxOption(), weightVarianceSpinner.getSelection());
		}
		if (enableDurabilityButton.getSelection()) {
			durabilityOptions = new MinMaxVarOption(durabilityRangeControl.getMinMaxOption(), durabilityVarianceSpinner.getSelection());
		}
		
		return new WeaponOptions(mightOptions, hitOptions, weightOptions, durabilityOptions, enableRandomEffectsButton.getSelection(), effectChanceSpinner.getSelection(), effectsSelectionView.getOptions(), 
				noEffectsForIronButton.getSelection(),
				noEffectsForIronButton.getSelection() ? (noEffectsForSteelButton != null ? noEffectsForSteelButton.getSelection() : false) : false,
				noEffectsForIronButton.getSelection() ? (noEffectsForBasicThrownButton != null ? noEffectsForBasicThrownButton.getSelection() : false) : false, 
				includeLaguzButton != null ? includeLaguzButton.getSelection() : false);
	}

	@Override
	public void initialize(WeaponOptions options) {
		if (options == null) {
			// Shouldn't happen.
			return;
		}

		if (options.mightOptions != null) {
			enableMightButton.setSelection(true);
			mightRangeControl.setEnabled(true);
			mightRangeControl.setMin(options.mightOptions.minValue);
			mightRangeControl.setMax(options.mightOptions.maxValue);
			mightVarianceSpinner.setEnabled(true);
			mightVarianceSpinner.setSelection(options.mightOptions.variance);
		}

		if (options.hitOptions != null) {
			enableHitButton.setSelection(true);
			hitRangeControl.setEnabled(true);
			hitRangeControl.setMin(options.hitOptions.minValue);
			hitRangeControl.setMax(options.hitOptions.maxValue);
			hitVarianceSpinner.setEnabled(true);
			hitVarianceSpinner.setSelection(options.hitOptions.variance);
		}

		if (options.weightOptions != null) {
			enableWeightButton.setSelection(true);
			weightRangeControl.setEnabled(true);
			weightRangeControl.setMin(options.weightOptions.minValue);
			weightRangeControl.setMax(options.weightOptions.maxValue);
			weightVarianceSpinner.setEnabled(true);
			weightVarianceSpinner.setSelection(options.weightOptions.variance);
		}

		if (options.durabilityOptions != null) {
			enableDurabilityButton.setSelection(true);
			durabilityRangeControl.setEnabled(true);
			durabilityRangeControl.setMin(options.durabilityOptions.minValue);
			durabilityRangeControl.setMax(options.durabilityOptions.maxValue);
			durabilityVarianceSpinner.setEnabled(true);
			durabilityVarianceSpinner.setSelection(options.durabilityOptions.variance);
		}

		if (options.shouldAddEffects) {
			enableRandomEffectsButton.setSelection(true);
			effectsSelectionView.setEnabled(true);
			noEffectsForIronButton.setEnabled(true);
			effectChanceSpinner.setEnabled(true);
			effectChanceLabel.setEnabled(true);
			effectsSelectionView.initialize(options.effectsList);
			noEffectsForIronButton.setSelection(options.noEffectIronWeapons);
			if (noEffectsForSteelButton != null) {
				noEffectsForSteelButton.setEnabled(true);
				noEffectsForSteelButton.setSelection(options.noEffectSteelWeapons);
			}
			if (noEffectsForBasicThrownButton != null) {
				noEffectsForBasicThrownButton.setEnabled(true);
				noEffectsForBasicThrownButton.setSelection(options.noEffectThrownWeapons);
			}
			effectChanceSpinner.setSelection(options.effectChance);
			if (includeLaguzButton != null) {
				includeLaguzButton.setEnabled(true);
				includeLaguzButton.setSelection(options.includeLaguzWeapons);
			}
		}
	}
}
