package ui.views.fe4;

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

import ui.common.GuiUtil;
import ui.model.fe4.FE4EnemyBuffOptions;
import ui.model.fe4.FE4EnemyBuffOptions.EnemyScalingOptions;
import ui.views.YuneView;

public class FE4EnemyBuffView extends YuneView<FE4EnemyBuffOptions> {

	private Button enemyScalingButton;
	private Label scaleAmountLabel;
	private Spinner scaleAmountSpinner;
	private Button flatOptionButton;
	private Button proportionalOptionButton;
	
	private Button weaponImprovementButton;
	private Label improveChanceLabel;
	private Spinner improveChanceSpinner;
	
	private Button majorHolyBloodBossButton;
	
	public FE4EnemyBuffView(Composite parent) {
		super(parent);
	}

	@Override
	public String getGroupTitle() {
		return "Buff Enemies";
	}

	@Override
	public String getGroupTooltip() {
		return "Inflate enemy stats and equipment.";
	}
	
	@Override
	protected void compose(){
		enemyScalingButton = new Button(group, SWT.CHECK);
		enemyScalingButton.setText("Improve Enemy Stats");
		enemyScalingButton.setToolTipText("For Minions/Bosses: Increases the growth rate of classes.\nFor Holy Bosses: Increases bases of classes.");
		enemyScalingButton.setEnabled(true);
		enemyScalingButton.setSelection(false);
		enemyScalingButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				scaleAmountLabel.setEnabled(enemyScalingButton.getSelection());
				scaleAmountSpinner.setEnabled(enemyScalingButton.getSelection());
				flatOptionButton.setEnabled(enemyScalingButton.getSelection());
				proportionalOptionButton.setEnabled(enemyScalingButton.getSelection());
			}
		});
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		enemyScalingButton.setLayoutData(optionData);
		
		scaleAmountLabel = new Label(group, SWT.RIGHT);
		scaleAmountLabel.setText("Scaling Amount:");
		scaleAmountLabel.setEnabled(false);
		
		scaleAmountSpinner = new Spinner(group, SWT.NONE);
		scaleAmountSpinner.setValues(10, 5, 50, 0, 5, 10);
		scaleAmountSpinner.setEnabled(false);
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(enemyScalingButton, 10, SWT.LEFT);
		labelData.right = new FormAttachment(scaleAmountSpinner, -5);
		labelData.top = new FormAttachment(scaleAmountSpinner, 0, SWT.CENTER);
		scaleAmountLabel.setLayoutData(labelData);
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.top = new FormAttachment(enemyScalingButton, 5);
		scaleAmountSpinner.setLayoutData(spinnerData);
		
		flatOptionButton = new Button(group, SWT.RADIO);
		flatOptionButton.setText("Flat");
		flatOptionButton.setToolTipText("For Minions/Bosses: Adds the value to every class stat's growth rate.\nFor Holy Bosses: Adds 1 for every 10 to base stats (2 per 10 for HP).");
		flatOptionButton.setEnabled(false);
		flatOptionButton.setSelection(true);
		
		proportionalOptionButton = new Button(group, SWT.RADIO);
		proportionalOptionButton.setText("Proportional");
		proportionalOptionButton.setToolTipText("For Minions/Bosses: Multiplies every class's stat growth rates by (100 + value)%.\nFor Holy Bosses: Multiplies every personal base stat by (100 + value)%.");
		proportionalOptionButton.setEnabled(false);
		proportionalOptionButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(enemyScalingButton, 10, SWT.LEFT);
		optionData.top = new FormAttachment(scaleAmountSpinner, 5);
		optionData.right = new FormAttachment(proportionalOptionButton, -5);
		flatOptionButton.setLayoutData(optionData);
		
		optionData = new FormData();
		optionData.right = new FormAttachment(100, -5);
		optionData.top = new FormAttachment(flatOptionButton, 0, SWT.TOP);
		proportionalOptionButton.setLayoutData(optionData);
		
		///
		
		weaponImprovementButton = new Button(group, SWT.CHECK);
		weaponImprovementButton.setText("Improve Enemy Equipment");
		weaponImprovementButton.setToolTipText("For Minions/Bosses: Gives some enemies interesting or powerful weapons (excluding Prologue).\nFor Holy Bosses: No Effect.");
		weaponImprovementButton.setEnabled(true);
		weaponImprovementButton.setSelection(false);
		weaponImprovementButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				improveChanceLabel.setEnabled(weaponImprovementButton.getSelection());
				improveChanceSpinner.setEnabled(weaponImprovementButton.getSelection());
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(enemyScalingButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(flatOptionButton, 15);
		weaponImprovementButton.setLayoutData(optionData);
		
		improveChanceLabel = new Label(group, SWT.RIGHT);
		improveChanceLabel.setText("Improvement Chance:");
		improveChanceLabel.setEnabled(false);
		
		improveChanceSpinner = new Spinner(group, SWT.NONE);
		improveChanceSpinner.setValues(25, 5, 100, 0, 5, 10);
		improveChanceSpinner.setEnabled(false);
		
		labelData = new FormData();
		labelData.left = new FormAttachment(weaponImprovementButton, 10, SWT.LEFT);
		labelData.right = new FormAttachment(improveChanceSpinner, -5);
		labelData.top = new FormAttachment(improveChanceSpinner, 0, SWT.CENTER);
		improveChanceLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.top = new FormAttachment(weaponImprovementButton, 5);
		improveChanceSpinner.setLayoutData(spinnerData);
		
		///
		
		majorHolyBloodBossButton = new Button(group, SWT.CHECK);
		majorHolyBloodBossButton.setText("Force Major Blood and Holy Weapon");
		majorHolyBloodBossButton.setToolTipText("For Minions/Bosses: No Effect.\nFor Holy Bosses: Force bosses with holy blood to Major Blood and its respective holy weapon.\nNote: Julius and Danann are exempt from this.");
		majorHolyBloodBossButton.setEnabled(true);
		majorHolyBloodBossButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(weaponImprovementButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(improveChanceSpinner, 15);
		majorHolyBloodBossButton.setLayoutData(optionData);
	}

	@Override
	public FE4EnemyBuffOptions getOptions() {
		FE4EnemyBuffOptions.EnemyScalingOptions scalingOption = flatOptionButton.getSelection() ? FE4EnemyBuffOptions.EnemyScalingOptions.FLAT : FE4EnemyBuffOptions.EnemyScalingOptions.SCALING;
		return new FE4EnemyBuffOptions(enemyScalingButton.getSelection(), scalingOption, scaleAmountSpinner.getSelection(), weaponImprovementButton.getSelection(), improveChanceSpinner.getSelection(), majorHolyBloodBossButton.getSelection());
	}

	@Override
	public void initialize(FE4EnemyBuffOptions options) {
		if (options == null) {
			// Shouldn't happen
			return;
		}
		enemyScalingButton.setSelection(options.increaseEnemyScaling);
		scaleAmountLabel.setEnabled(options.increaseEnemyScaling);
		scaleAmountSpinner.setEnabled(options.increaseEnemyScaling);
		flatOptionButton.setEnabled(options.increaseEnemyScaling);
		proportionalOptionButton.setEnabled(options.increaseEnemyScaling);

		scaleAmountSpinner.setSelection(options.scalingAmount);

		flatOptionButton.setSelection(options.scalingOption == EnemyScalingOptions.FLAT);
		proportionalOptionButton.setSelection(options.scalingOption == EnemyScalingOptions.SCALING);

		weaponImprovementButton.setSelection(options.improveMinionWeapons);
		improveChanceLabel.setEnabled(options.improveMinionWeapons);
		improveChanceSpinner.setEnabled(options.improveMinionWeapons);

		improveChanceSpinner.setSelection(options.improvementChance);

		majorHolyBloodBossButton.setSelection(options.majorHolyBloodBosses);
	}
}
