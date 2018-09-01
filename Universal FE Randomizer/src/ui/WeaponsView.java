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

import fedata.FEBase.GameType;
import ui.WeaponEffectSelectionView.WeaponEffectSelectionViewListener;
import ui.model.MinMaxVarOption;
import ui.model.WeaponOptions;

public class WeaponsView extends Composite {
	private Group container;
	
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
	private Button noEffectsForIronButton;;
	private WeaponEffectSelectionView effectsSelectionView;
	
	public WeaponsView(Composite parent, int style, GameType type) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Weapons");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		///////////////////////////////////////////////////////
		
		enableMightButton = new Button(container, SWT.CHECK);
		enableMightButton.setText("Randomize Power (MT)");
		enableMightButton.setToolTipText("Applies a random delta +/- Variance to all weapons' MT stat. All weapons are then clamped to the min and max specified.");

		FormData mtData = new FormData();
		mtData.left = new FormAttachment(0, 5);
		mtData.top = new FormAttachment(0, 5);
		enableMightButton.setLayoutData(mtData);
		
		Composite mtParamContainer = new Composite(container, SWT.NONE);
		
		FormLayout mtParamLayout = new FormLayout();
		mtParamLayout.marginLeft = 5;
		mtParamLayout.marginTop = 5;
		mtParamLayout.marginBottom = 5;
		mtParamLayout.marginRight = 5;
		mtParamContainer.setLayout(mtParamLayout);
		
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
		
		enableHitButton = new Button(container, SWT.CHECK);
		enableHitButton.setText("Randomize Accuracy (Hit)");
		enableHitButton.setToolTipText("Applies a random delta +/- Variance to all weapons' accuracy. All weapons are then clamped to the min and max specified.");

		FormData hitData = new FormData();
		hitData.left = new FormAttachment(0, 5);
		hitData.top = new FormAttachment(mtParamContainer, 5);
		enableHitButton.setLayoutData(hitData);
		
		Composite hitParamContainer = new Composite(container, SWT.NONE);
		
		FormLayout hitParamLayout = new FormLayout();
		hitParamLayout.marginLeft = 5;
		hitParamLayout.marginTop = 5;
		hitParamLayout.marginBottom = 5;
		hitParamLayout.marginRight = 5;
		hitParamContainer.setLayout(hitParamLayout);
		
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
		
		enableWeightButton = new Button(container, SWT.CHECK);
		enableWeightButton.setText("Randomize Weights (WT)");
		enableWeightButton.setToolTipText("Applies a random delta +/- Variance to all weapons' weight. All weapons are then clamped to the min and max specified.");

		FormData wtData = new FormData();
		wtData.left = new FormAttachment(0, 5);
		wtData.top = new FormAttachment(hitParamContainer, 5);
		enableWeightButton.setLayoutData(wtData);
		
		Composite wtParamContainer = new Composite(container, SWT.NONE);
		
		FormLayout wtParamLayout = new FormLayout();
		wtParamLayout.marginLeft = 5;
		wtParamLayout.marginTop = 5;
		wtParamLayout.marginBottom = 5;
		wtParamLayout.marginRight = 5;
		wtParamContainer.setLayout(wtParamLayout);
		
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
		
		enableDurabilityButton = new Button(container, SWT.CHECK);
		enableDurabilityButton.setText("Randomize Durability");
		enableDurabilityButton.setToolTipText("Applies a random delta +/- Variance to all weapons' durability. All weapons are then clamped to the min and max specified. Siege tomes are limited to a 1-use minimum.");

		FormData durabilityData = new FormData();
		durabilityData.left = new FormAttachment(0, 5);
		durabilityData.top = new FormAttachment(wtParamContainer, 5);
		enableDurabilityButton.setLayoutData(durabilityData);
		
		Composite durabilityParamContainer = new Composite(container, SWT.NONE);
		
		FormLayout durabilityParamLayout = new FormLayout();
		durabilityParamLayout.marginLeft = 5;
		durabilityParamLayout.marginTop = 5;
		durabilityParamLayout.marginBottom = 5;
		durabilityParamLayout.marginRight = 5;
		durabilityParamContainer.setLayout(durabilityParamLayout);
		
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

		durabilityRangeControl.getMinSpinner().setValues(15, 1, 99, 0, 1, 5);
		durabilityRangeControl.getMaxSpinner().setValues(60, 1, 99, 0, 1, 5);
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
		
		enableRandomEffectsButton = new Button(container, SWT.CHECK);
		enableRandomEffectsButton.setText("Add Random Effects");
		enableRandomEffectsButton.setToolTipText("Adds a random effect to all weapons. Effects includes stat bonuses, effectiveness, weapon triangle reversal, brave, magic damge, etc. Weapons that already have effects get a second effect added on.");

		FormData effectsData = new FormData();
		effectsData.left = new FormAttachment(0, 5);
		effectsData.top = new FormAttachment(durabilityParamContainer, 5);
		enableRandomEffectsButton.setLayoutData(effectsData);
		
		noEffectsForIronButton = new Button(container, SWT.CHECK);
		noEffectsForIronButton.setText("Safe Basic Weapons");
		noEffectsForIronButton.setToolTipText("Iron Weapons (inc. Fire, Lightning, and Flux) remain unchanged. This establishes a safe-zone for weapons to not be broken.");
		noEffectsForIronButton.setEnabled(false);
		
		FormData ironData = new FormData();
		ironData.left = new FormAttachment(enableRandomEffectsButton, 10, SWT.LEFT);
		ironData.top = new FormAttachment(enableRandomEffectsButton, 5);
		noEffectsForIronButton.setLayoutData(ironData);
		
		updateWeaponEffectSelectionViewForGame(type);
	}
	
	public void updateWeaponEffectSelectionViewForGame(GameType type) {
		if (effectsSelectionView != null) { effectsSelectionView.dispose(); }
		
		effectsSelectionView = new WeaponEffectSelectionView(container, SWT.NONE, type);
		effectsSelectionView.setEnabled(false);
		
		FormData effectData = new FormData();
		effectData.left = new FormAttachment(noEffectsForIronButton, 10, SWT.LEFT);
		effectData.top = new FormAttachment(noEffectsForIronButton, 5);
		effectData.bottom = new FormAttachment(100, -5);
		effectData.width = 280;
		effectsSelectionView.setLayoutData(effectData);
		
		effectsSelectionView.setSelectionListener(new WeaponEffectSelectionViewListener() {
			@Override
			public void onSelectionChanged() {
				if (effectsSelectionView.isAllDisabled()) {
					enableRandomEffectsButton.setSelection(false);
					effectsSelectionView.setEnabled(false);
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
				if (enabled) {
					effectsSelectionView.selectAll();
					noEffectsForIronButton.setSelection(true);
				} else {
					effectsSelectionView.deselectAll();
					noEffectsForIronButton.setSelection(false);
				}
			}
		});
	}
	
	public WeaponOptions getWeaponOptions() {
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
		
		return new WeaponOptions(mightOptions, hitOptions, weightOptions, durabilityOptions, enableRandomEffectsButton.getSelection(), effectsSelectionView.getOptions(), noEffectsForIronButton.getSelection());
	}
}
