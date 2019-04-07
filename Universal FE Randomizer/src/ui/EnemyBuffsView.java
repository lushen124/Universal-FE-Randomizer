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
import ui.model.EnemyOptions.BossStatMode;
import ui.model.EnemyOptions.MinionGrowthMode;

public class EnemyBuffsView extends Composite {
	
	private Group container;
	
	private Button buffEnemyGrowthsButton;
	private Spinner buffSpinner;
	
	private Button flatBonusButton;
	private Button scalingBonusButton;
	
	private Button improveEnemyWeaponsButton;
	private Spinner weaponSpinner;
	
	private Button buffBossStatButton;
	private Label bossStatSpinnerLabel;
	private Spinner bossStatSpinner;
	
	private Button linearBossButton;
	private Button easeInOutBossButton;
	
	private Button improveBossWeaponButton;
	private Label bossWeaponSpinnerLabel;
	private Spinner bossWeaponSpinner;
	
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
		
		Group minionGroup = new Group(container, SWT.NONE);
		minionGroup.setText("Minions");
		
		FormLayout minionLayout = new FormLayout();
		minionLayout.marginLeft = 5;
		minionLayout.marginTop = 5;
		minionLayout.marginBottom = 5;
		minionLayout.marginRight = 5;
		minionGroup.setLayout(minionLayout);
		
		FormData minionData = new FormData();
		minionData.left = new FormAttachment(0, 0);
		minionData.right = new FormAttachment(100, 0);
		minionGroup.setLayoutData(minionData);
		
		buffEnemyGrowthsButton = new Button(minionGroup, SWT.CHECK);
		buffEnemyGrowthsButton.setText("Buff Enemy Growths");
		buffEnemyGrowthsButton.setToolTipText("Increases enemy growth rates.");
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
		
		Composite buffParamContainer = new Composite(minionGroup, SWT.NONE);
		
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
		flatBonusButton.setToolTipText("The buff amount is directly added to the enemy's growth rates for all stats.");
		flatBonusButton.setSelection(true);
		flatBonusButton.setEnabled(false);
		
		scalingBonusButton = new Button(buffModeContainer, SWT.RADIO);
		scalingBonusButton.setText("Scaling Buff");
		scalingBonusButton.setToolTipText("The buff amount is multiplied as a percentage to the enemy's growth rates for all stats.");
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
		
		improveEnemyWeaponsButton = new Button(minionGroup, SWT.CHECK);
		improveEnemyWeaponsButton.setText("Improve Enemy Weapons");
		improveEnemyWeaponsButton.setToolTipText("Adds a chance for enemies to spawn with a higher tier weapon than usual.");
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
		
		Label chanceLabel = new Label(minionGroup, SWT.RIGHT);
		chanceLabel.setText("Chance:");
		
		weaponSpinner = new Spinner(minionGroup, SWT.NONE);
		weaponSpinner.setValues(25, 0, 100, 0, 1, 5);
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
		
		Group bossGroup = new Group(container, SWT.NONE);
		bossGroup.setText("Bosses");
		
		FormLayout bossLayout = new FormLayout();
		bossLayout.marginLeft = 5;
		bossLayout.marginRight = 5;
		bossLayout.marginTop = 5;
		bossLayout.marginBottom = 5;
		bossGroup.setLayout(bossLayout);
		
		FormData bossData = new FormData();
		bossData.left = new FormAttachment(minionGroup, 0, SWT.LEFT);
		bossData.right = new FormAttachment(minionGroup, 0, SWT.RIGHT);
		bossData.top = new FormAttachment(minionGroup, 5);
		bossGroup.setLayoutData(bossData);
		
		buffBossStatButton = new Button(bossGroup, SWT.CHECK);
		buffBossStatButton.setText("Buff Boss Stats");
		buffBossStatButton.setToolTipText("Increases base stats of bosses.");
		buffBossStatButton.setEnabled(true);
		buffBossStatButton.setSelection(false);
		buffBossStatButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				boolean enabled = buffBossStatButton.getSelection();
				bossStatSpinnerLabel.setEnabled(enabled);
				bossStatSpinner.setEnabled(enabled);
				linearBossButton.setEnabled(enabled);
				easeInOutBossButton.setEnabled(enabled);
			}
		});
		
		linearBossButton = new Button(bossGroup, SWT.RADIO);
		linearBossButton.setText("Scale Linearly");
		linearBossButton.setToolTipText("Bosses gradually gain stats in a linear fashion up to the max gain.");
		linearBossButton.setSelection(true);
		linearBossButton.setEnabled(false);
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(buffBossStatButton, 5, SWT.LEFT);
		optionData.top = new FormAttachment(buffBossStatButton, 5);
		linearBossButton.setLayoutData(optionData);
		
		easeInOutBossButton = new Button(bossGroup, SWT.RADIO);
		easeInOutBossButton.setText("Ease In/Ease Out");
		easeInOutBossButton.setToolTipText("Ramps up more slowly and eases into the max gain.");
		easeInOutBossButton.setEnabled(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(linearBossButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(linearBossButton, 5);
		easeInOutBossButton.setLayoutData(optionData);
		
		bossStatSpinner = new Spinner(bossGroup, SWT.NONE);
		bossStatSpinner.setValues(5, 1, 20, 0, 1, 5);
		bossStatSpinner.setToolTipText("The maximum amount of stats a boss can gain in each area.\nThis value is the gain for the final regular boss of the game.");
		bossStatSpinner.setEnabled(false);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.top = new FormAttachment(easeInOutBossButton, 5);
		bossStatSpinner.setLayoutData(spinnerData);
		
		bossStatSpinnerLabel = new Label(bossGroup, SWT.NONE);
		bossStatSpinnerLabel.setText("Max Boost:");
		bossStatSpinnerLabel.setEnabled(false);
		
		labelData = new FormData();
		labelData.right = new FormAttachment(bossStatSpinner, -5);
		labelData.top = new FormAttachment(bossStatSpinner, 0, SWT.CENTER);
		bossStatSpinnerLabel.setLayoutData(labelData);
		
		improveBossWeaponButton = new Button(bossGroup, SWT.CHECK);
		improveBossWeaponButton.setText("Improve Boss Weapons");
		improveBossWeaponButton.setToolTipText("Adds a chance for bosses to spawn with a higher tier weapon than usual.");
		improveBossWeaponButton.setEnabled(true);
		improveBossWeaponButton.setSelection(false);
		improveBossWeaponButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				boolean enabled = improveBossWeaponButton.getSelection();
				bossWeaponSpinner.setEnabled(enabled);
				bossWeaponSpinnerLabel.setEnabled(enabled);
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(buffBossStatButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(bossStatSpinnerLabel, 10);
		improveBossWeaponButton.setLayoutData(optionData);
		
		bossWeaponSpinner = new Spinner(bossGroup, SWT.NONE);
		bossWeaponSpinner.setValues(25, 0, 100, 0, 1, 5);
		bossWeaponSpinner.setEnabled(false);
		
		spinnerData = new FormData();
		spinnerData.top = new FormAttachment(improveBossWeaponButton, 5);
		spinnerData.right = new FormAttachment(100, -5);
		bossWeaponSpinner.setLayoutData(spinnerData);
		
		bossWeaponSpinnerLabel = new Label(bossGroup, SWT.NONE);
		bossWeaponSpinnerLabel.setText("Chance:");
		bossWeaponSpinnerLabel.setEnabled(false);
		
		labelData = new FormData();
		labelData.right = new FormAttachment(bossWeaponSpinner, -5);
		labelData.top = new FormAttachment(bossWeaponSpinner, 0, SWT.CENTER);
		bossWeaponSpinnerLabel.setLayoutData(labelData);
	}
	
	public EnemyOptions getEnemyOptions() {

		boolean buffMinionWeapons = improveEnemyWeaponsButton.getSelection();
		int minionWeaponChance = weaponSpinner.getSelection();
		
		boolean buffBossWeapons = improveBossWeaponButton.getSelection();
		int bossWeaponChance = bossWeaponSpinner.getSelection();
		
		MinionGrowthMode minionMode = MinionGrowthMode.NONE;
		if (buffEnemyGrowthsButton.getSelection()) {
			if (flatBonusButton.getSelection()) { minionMode = MinionGrowthMode.FLAT; }
			else if (scalingBonusButton.getSelection()) { minionMode = MinionGrowthMode.SCALING; }
		}
		
		BossStatMode bossMode = BossStatMode.NONE;
		if (buffBossStatButton.getSelection()) {
			if (linearBossButton.getSelection()) { bossMode = BossStatMode.LINEAR; }
			else if (easeInOutBossButton.getSelection()) { bossMode = BossStatMode.EASE_IN_OUT; }
		}
		
		return new EnemyOptions(minionMode, buffSpinner.getSelection(), buffMinionWeapons, minionWeaponChance, bossMode, bossStatSpinner.getSelection(), buffBossWeapons, bossWeaponChance);
	}
	
	public void setEnemyOptions(EnemyOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			if (options.minionMode != null) {
				switch (options.minionMode) {
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
					buffSpinner.setSelection(options.minionBuff);
					break;
				case SCALING:
					buffEnemyGrowthsButton.setSelection(true);
					flatBonusButton.setEnabled(true);
					scalingBonusButton.setEnabled(true);
					flatBonusButton.setSelection(false);
					scalingBonusButton.setSelection(true);
					buffSpinner.setEnabled(true);
					buffSpinner.setSelection(options.minionBuff);
					break;
				}
			}
			if (options.improveMinionWeapons) {
				improveEnemyWeaponsButton.setSelection(true);
				weaponSpinner.setEnabled(true);
				weaponSpinner.setSelection(options.minionImprovementChance);
			}
			
			if (options.bossMode != null) {
				switch (options.bossMode) {
				case NONE:
					buffBossStatButton.setSelection(false);
					linearBossButton.setEnabled(false);
					easeInOutBossButton.setEnabled(false);
					bossStatSpinner.setEnabled(false);
					bossStatSpinnerLabel.setEnabled(false);
					break;
				case LINEAR:
					buffBossStatButton.setSelection(true);
					linearBossButton.setEnabled(true);
					easeInOutBossButton.setEnabled(true);
					bossStatSpinner.setEnabled(true);
					bossStatSpinnerLabel.setEnabled(true);
					
					linearBossButton.setSelection(true);
					easeInOutBossButton.setSelection(false);
					
					bossStatSpinner.setSelection(options.bossBuff);
					break;
				case EASE_IN_OUT:
					buffBossStatButton.setSelection(true);
					linearBossButton.setEnabled(true);
					easeInOutBossButton.setEnabled(true);
					bossStatSpinner.setEnabled(true);
					bossStatSpinnerLabel.setEnabled(true);
					
					linearBossButton.setSelection(false);
					easeInOutBossButton.setSelection(true);
					
					bossStatSpinner.setSelection(options.bossBuff);
					break;
				}
			}
			if (options.improveBossWeapons) {
				improveBossWeaponButton.setSelection(true);
				bossWeaponSpinner.setEnabled(true);
				bossStatSpinnerLabel.setEnabled(true);
				bossWeaponSpinner.setSelection(options.bossImprovementChance);
			}
		}
	}
}
