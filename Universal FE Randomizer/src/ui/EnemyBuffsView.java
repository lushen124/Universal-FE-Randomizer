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

import ui.model.EnemyOptions;

public class EnemyBuffsView extends Composite {
	
	private Group container;
	
	private Button buffEnemyGrowthsButton;
	private Spinner buffSpinner;
	
	private Button flatBonusButton;
	private Button scalingBonusButton;
	
	private Button improveEnemyWeaponsButton;
	private Spinner weaponSpinner;
	
	public EnemyBuffsView(Composite parent, int style) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Buff Enemies");
		container.setToolTipText("Options to mix up the normal enemies, generally to make the game more challenging.");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		buffEnemyGrowthsButton = new Button(container, SWT.CHECK);
		buffEnemyGrowthsButton.setText("Buff Enemy Growths");
		buffEnemyGrowthsButton.setToolTipText("Enemy stats are calculated by a set of class growths. This option increases those growths by the amount specified. The higher the enemy's level, the more this scales up.");
		buffEnemyGrowthsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				buffSpinner.setEnabled(buffEnemyGrowthsButton.getSelection());
				
				flatBonusButton.setEnabled(buffEnemyGrowthsButton.getSelection());
				scalingBonusButton.setEnabled(buffEnemyGrowthsButton.getSelection());
			}
		});
		
		FormData buffData = new FormData();
		buffData.left = new FormAttachment(0, 5);
		buffData.top = new FormAttachment(0, 5);
		buffEnemyGrowthsButton.setLayoutData(buffData);
		
		Composite buffParamContainer = new Composite(container, SWT.NONE);
		
		FormLayout buffParamLayout = new FormLayout();
		buffParamLayout.marginLeft = 5;
		buffParamLayout.marginRight = 5;
		buffParamLayout.marginTop = 5;
		buffParamLayout.marginBottom = 5;
		buffParamContainer.setLayout(buffParamLayout);
		
		Label buffLabel = new Label(buffParamContainer, SWT.RIGHT);
		buffLabel.setText("Buff Amount:");
		
		buffSpinner = new Spinner(buffParamContainer, SWT.NONE);
		buffSpinner.setValues(10, 0, 100, 0, 1, 1);
		buffSpinner.setEnabled(false);
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(buffSpinner, -5);
		labelData.top = new FormAttachment(buffSpinner, 0, SWT.CENTER);
		buffLabel.setLayoutData(labelData);
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		buffSpinner.setLayoutData(spinnerData);
		
		Composite buffModeContainer = new Composite(buffParamContainer, SWT.NONE);
		
		FillLayout buffModeLayout = new FillLayout();
		buffModeContainer.setLayout(buffModeLayout);
		
		flatBonusButton = new Button(buffModeContainer, SWT.RADIO);
		flatBonusButton.setText("Flat Buff");
		flatBonusButton.setToolTipText("The buff amount above is directly added to the enemy's growth rates for all stats. This generally patches up class weaknesses. It's not recommended to go beyond 15 or 20 with this mode.");
		flatBonusButton.setSelection(true);
		flatBonusButton.setEnabled(false);
		
		scalingBonusButton = new Button(buffModeContainer, SWT.RADIO);
		scalingBonusButton.setText("Scaling Buff");
		scalingBonusButton.setToolTipText("The buff amount is multiplied as a percentage to the enemy's growth rates for all stats. This results in classes that are emphasized in their strong stats. A value of 100(%) is a 2x increase.");
		scalingBonusButton.setEnabled(false);
		
		FormData buffModeData = new FormData();
		buffModeData.top = new FormAttachment(buffSpinner, 5);
		buffModeData.left = new FormAttachment(0, 0);
		buffModeData.right = new FormAttachment(100, 0);
		buffModeContainer.setLayoutData(buffModeData);
		
		FormData paramContainerData = new FormData();
		paramContainerData.top = new FormAttachment(buffEnemyGrowthsButton, 0);
		paramContainerData.left = new FormAttachment(buffEnemyGrowthsButton, 0, SWT.LEFT);
		paramContainerData.right = new FormAttachment(100, -5);
		buffParamContainer.setLayoutData(paramContainerData);
		
		//////////////////////////////////////////////////////////////////
		
		improveEnemyWeaponsButton = new Button(container, SWT.CHECK);
		improveEnemyWeaponsButton.setText("Improve Enemy Weapons");
		improveEnemyWeaponsButton.setToolTipText("Adds a high chance of enemies spawning with a higher tier weapon than usual.");
		improveEnemyWeaponsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				weaponSpinner.setEnabled(improveEnemyWeaponsButton.getSelection());
			}
		});
		
		FormData improveWeaponsData = new FormData();
		improveWeaponsData.left = new FormAttachment(0, 5);
		improveWeaponsData.top = new FormAttachment(buffParamContainer, 5);
		improveEnemyWeaponsButton.setLayoutData(improveWeaponsData);
		
		Label chanceLabel = new Label(container, SWT.RIGHT);
		chanceLabel.setText("Chance:");
		
		weaponSpinner = new Spinner(container, SWT.NONE);
		weaponSpinner.setValues(25, 0, 100, 0, 1, 1);
		weaponSpinner.setEnabled(false);
		
		FormData chanceLabelData = new FormData();
		chanceLabelData.left = new FormAttachment(0, 5);
		chanceLabelData.right = new FormAttachment(weaponSpinner, -5);
		chanceLabelData.top = new FormAttachment(weaponSpinner, 0, SWT.CENTER);
		chanceLabel.setLayoutData(chanceLabelData);
		
		FormData chanceSpinnerData = new FormData();
		chanceSpinnerData.right = new FormAttachment(100, -10);
		chanceSpinnerData.top = new FormAttachment(improveEnemyWeaponsButton, 5);
		weaponSpinner.setLayoutData(chanceSpinnerData);
		
		//////////////////////////////////////////////////////////////////		
	}
	
	public EnemyOptions getEnemyOptions() {

		Boolean buffWeapons = improveEnemyWeaponsButton.getSelection();
		int buffChance = weaponSpinner.getSelection();
		
		if (buffEnemyGrowthsButton.getSelection() == false) {
			return new EnemyOptions(EnemyOptions.BuffMode.NONE, 0, buffWeapons, buffChance);
		} else {
			if (flatBonusButton.getSelection()) {
				return new EnemyOptions(EnemyOptions.BuffMode.FLAT, buffSpinner.getSelection(), buffWeapons, buffChance);
			} else {
				return new EnemyOptions(EnemyOptions.BuffMode.SCALING, buffSpinner.getSelection(), buffWeapons, buffChance);
			}
		}
	}
	
	public void setEnemyOptions(EnemyOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			switch (options.mode) {
			case NONE:
				buffEnemyGrowthsButton.setSelection(false);
				flatBonusButton.setEnabled(false);
				scalingBonusButton.setEnabled(false);
				buffSpinner.setEnabled(false);
				break;
			case FLAT:
				buffEnemyGrowthsButton.setSelection(true);
				flatBonusButton.setEnabled(true);
				scalingBonusButton.setEnabled(true);
				flatBonusButton.setSelection(true);
				scalingBonusButton.setSelection(false);
				buffSpinner.setEnabled(true);
				buffSpinner.setSelection(options.buffAmount);
				break;
			case SCALING:
				buffEnemyGrowthsButton.setSelection(true);
				flatBonusButton.setEnabled(true);
				scalingBonusButton.setEnabled(true);
				flatBonusButton.setSelection(false);
				scalingBonusButton.setSelection(true);
				buffSpinner.setEnabled(true);
				buffSpinner.setSelection(options.buffAmount);
				break;
			}
		}
	}
}
