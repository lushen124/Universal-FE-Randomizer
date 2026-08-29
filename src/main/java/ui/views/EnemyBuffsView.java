package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import ui.common.GuiUtil;
import ui.model.EnemyOptions;
import ui.model.EnemyOptions.BossStatMode;
import ui.model.EnemyOptions.MinionGrowthMode;

public class EnemyBuffsView extends YuneView<EnemyOptions> {
	

	private Button buffEnemyGrowthsButton;
	private Label minionSpinnerLabel;
	private Spinner buffSpinner;
	
	private Button flatBonusButton;
	private Button scalingBonusButton;
	
	private Button improveEnemyWeaponsButton;
	private Label minionWeaponSpinnerLabel;
	private Spinner weaponSpinner;
	
	private Button minionHP;
	private Button minionSTR;
	private Button minionSKL;
	private Button minionSPD;
	private Button minionLCK;
	private Button minionDEF;
	private Button minionRES;
	
	private Button buffBossStatButton;
	private Label bossStatSpinnerLabel;
	private Spinner bossStatSpinner;
	
	private Button linearBossButton;
	private Button easeInOutBossButton;
	
	private Button improveBossWeaponButton;
	private Label bossWeaponSpinnerLabel;
	private Spinner bossWeaponSpinner;
	
	private Button bossHP;
	private Button bossSTR;
	private Button bossSKL;
	private Button bossSPD;
	private Button bossLCK;
	private Button bossDEF;
	private Button bossRES;
	
	public EnemyBuffsView(Composite parent) {
		super(parent);
	}
	@Override
	public String getGroupTitle() {
		return "Buff Enemies";
	}

	@Override
	public String getGroupTooltip() {
		return "Options to mix up the normal enemies, generally to make the game more challenging.";
	}
	@Override
	protected void compose() {
		Group minionGroup = new Group(group, SWT.NONE);
		minionGroup.setText("Minions");
		minionGroup.setLayout(GuiUtil.formLayoutWithMargin());
		
		FormData minionData = new FormData();
		minionData.top = new FormAttachment(0, 0);
		minionData.left = new FormAttachment(0, 0);
		minionData.right = new FormAttachment(100, 0);
		minionGroup.setLayoutData(minionData);
		
		buffEnemyGrowthsButton = new Button(minionGroup, SWT.CHECK);
		buffEnemyGrowthsButton.setText("Buff Enemy Growths");
		buffEnemyGrowthsButton.setToolTipText("Increases enemy growth rates.\n\nNote: Regardless of the method and amount used, the cap for\nenemy growths is 127% in any single area.");
		buffEnemyGrowthsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				buffSpinner.setEnabled(buffEnemyGrowthsButton.getSelection());
				minionSpinnerLabel.setEnabled(buffEnemyGrowthsButton.getSelection());
				
				flatBonusButton.setEnabled(buffEnemyGrowthsButton.getSelection());
				scalingBonusButton.setEnabled(buffEnemyGrowthsButton.getSelection());
				
				minionHP.setEnabled(buffEnemyGrowthsButton.getSelection());
				minionSTR.setEnabled(buffEnemyGrowthsButton.getSelection());
				minionSKL.setEnabled(buffEnemyGrowthsButton.getSelection());
				minionSPD.setEnabled(buffEnemyGrowthsButton.getSelection());
				minionLCK.setEnabled(buffEnemyGrowthsButton.getSelection());
				minionDEF.setEnabled(buffEnemyGrowthsButton.getSelection());
				minionRES.setEnabled(buffEnemyGrowthsButton.getSelection());
			}
		});
		
		FormData buffData = new FormData();
		buffData.left = new FormAttachment(0, 5);
		buffData.top = new FormAttachment(0, 5);
		buffEnemyGrowthsButton.setLayoutData(buffData);
		
		Composite buffParamContainer = new Composite(minionGroup, SWT.NONE);
		buffParamContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		minionSpinnerLabel = new Label(buffParamContainer, SWT.RIGHT);
		minionSpinnerLabel.setText("Buff Amount:");
		minionSpinnerLabel.setEnabled(false);
		
		buffSpinner = new Spinner(buffParamContainer, SWT.NONE);
		buffSpinner.setValues(10, 0, 100, 0, 1, 1);
		buffSpinner.setEnabled(false);
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(buffSpinner, -5);
		labelData.top = new FormAttachment(buffSpinner, 0, SWT.CENTER);
		minionSpinnerLabel.setLayoutData(labelData);
		
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
		
		Composite minionStatGroup = new Composite(minionGroup, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		minionStatGroup.setLayout(rowLayout);
		
		minionHP = new Button(minionStatGroup, SWT.CHECK);
		minionHP.setText("HP");
		minionHP.setToolTipText("Apply buff to minion health.");
		minionHP.setEnabled(false);
		minionHP.setSelection(true);
		
		minionSTR = new Button(minionStatGroup, SWT.CHECK);
		minionSTR.setText("STR/MAG");
		minionSTR.setToolTipText("Apply buff to minion attack power.");
		minionSTR.setEnabled(false);
		minionSTR.setSelection(true);
		
		minionSKL = new Button(minionStatGroup, SWT.CHECK);
		minionSKL.setText("SKL");
		minionSKL.setToolTipText("Apply buff to minion accuracy and critical chance.");
		minionSKL.setEnabled(false);
		minionSKL.setSelection(true);
		
		minionSPD = new Button(minionStatGroup, SWT.CHECK);
		minionSPD.setText("SPD");
		minionSPD.setToolTipText("Apply buff to minion speed and evasion.");
		minionSPD.setEnabled(false);
		minionSPD.setSelection(true);
		
		minionLCK = new Button(minionStatGroup, SWT.CHECK);
		minionLCK.setText("LCK");
		minionLCK.setToolTipText("Apply buff to minion accuracy and critical evasion.");
		minionLCK.setEnabled(false);
		minionLCK.setSelection(true);
		
		minionDEF = new Button(minionStatGroup, SWT.CHECK);
		minionDEF.setText("DEF");
		minionDEF.setToolTipText("Apply buff to minion physical defense.");
		minionDEF.setEnabled(false);
		minionDEF.setSelection(true);
		
		minionRES = new Button(minionStatGroup, SWT.CHECK);
		minionRES.setText("RES");
		minionRES.setToolTipText("Apply buff to minion magical defense.");
		minionRES.setEnabled(false);
		minionRES.setSelection(true);
		
		FormData minionGroupData = new FormData();
		minionGroupData.left = new FormAttachment(0, 10);
		minionGroupData.right = new FormAttachment(100, -10);
		minionGroupData.top = new FormAttachment(buffParamContainer, 0);
		minionGroupData.width = 200;
		minionStatGroup.setLayoutData(minionGroupData);
		
		//////////////////////////////////////////////////////////////////
		
		improveEnemyWeaponsButton = new Button(minionGroup, SWT.CHECK);
		improveEnemyWeaponsButton.setText("Improve Enemy Weapons");
		improveEnemyWeaponsButton.setToolTipText("Adds a chance for enemies to spawn with a higher tier weapon than usual.");
		improveEnemyWeaponsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				weaponSpinner.setEnabled(improveEnemyWeaponsButton.getSelection());
				minionWeaponSpinnerLabel.setEnabled(improveEnemyWeaponsButton.getSelection());
			}
		});
		
		FormData improveWeaponsData = new FormData();
		improveWeaponsData.left = new FormAttachment(0, 5);
		improveWeaponsData.top = new FormAttachment(minionStatGroup, 10);
		improveEnemyWeaponsButton.setLayoutData(improveWeaponsData);
		
		minionWeaponSpinnerLabel = new Label(minionGroup, SWT.RIGHT);
		minionWeaponSpinnerLabel.setText("Chance:");
		minionWeaponSpinnerLabel.setEnabled(false);
		
		weaponSpinner = new Spinner(minionGroup, SWT.NONE);
		weaponSpinner.setValues(25, 0, 100, 0, 1, 5);
		weaponSpinner.setEnabled(false);
		
		FormData chanceLabelData = new FormData();
		chanceLabelData.left = new FormAttachment(0, 5);
		chanceLabelData.right = new FormAttachment(weaponSpinner, -5);
		chanceLabelData.top = new FormAttachment(weaponSpinner, 0, SWT.CENTER);
		minionWeaponSpinnerLabel.setLayoutData(chanceLabelData);
		
		FormData chanceSpinnerData = new FormData();
		chanceSpinnerData.right = new FormAttachment(100, -10);
		chanceSpinnerData.top = new FormAttachment(improveEnemyWeaponsButton, 5);
		chanceSpinnerData.bottom = new FormAttachment(100, -5);
		weaponSpinner.setLayoutData(chanceSpinnerData);
		
		//////////////////////////////////////////////////////////////////	
		
		Group bossGroup = new Group(group, SWT.NONE);
		bossGroup.setText("Bosses");
		bossGroup.setLayout(GuiUtil.formLayoutWithMargin());
		
		FormData bossData = new FormData();
		bossData.left = new FormAttachment(0, 0);
		bossData.right = new FormAttachment(100, 0);
		bossData.top = new FormAttachment(minionGroup, 5);
		bossData.bottom = new FormAttachment(100, 0);
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
				
				bossHP.setEnabled(enabled);
				bossSTR.setEnabled(enabled);
				bossSKL.setEnabled(enabled);
				bossSPD.setEnabled(enabled);
				bossLCK.setEnabled(enabled);
				bossDEF.setEnabled(enabled);
				bossRES.setEnabled(enabled);
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
		
		Composite bossStatGroup = new Composite(bossGroup, SWT.NONE);
		rowLayout = new RowLayout();
		bossStatGroup.setLayout(rowLayout);
		
		bossHP = new Button(bossStatGroup, SWT.CHECK);
		bossHP.setText("HP");
		bossHP.setToolTipText("Apply buff to boss health.");
		bossHP.setEnabled(false);
		bossHP.setSelection(true);
		
		bossSTR = new Button(bossStatGroup, SWT.CHECK);
		bossSTR.setText("STR/MAG");
		bossSTR.setToolTipText("Apply buff to boss attack power.");
		bossSTR.setEnabled(false);
		bossSTR.setSelection(true);
		
		bossSKL = new Button(bossStatGroup, SWT.CHECK);
		bossSKL.setText("SKL");
		bossSKL.setToolTipText("Apply buff to boss accuracy and critical chance.");
		bossSKL.setEnabled(false);
		bossSKL.setSelection(true);
		
		bossSPD = new Button(bossStatGroup, SWT.CHECK);
		bossSPD.setText("SPD");
		bossSPD.setToolTipText("Apply buff to boss speed and evasion.");
		bossSPD.setEnabled(false);
		bossSPD.setSelection(true);
		
		bossLCK = new Button(bossStatGroup, SWT.CHECK);
		bossLCK.setText("LCK");
		bossLCK.setToolTipText("Apply buff to boss accuracy and critical evasion.");
		bossLCK.setEnabled(false);
		bossLCK.setSelection(true);
		
		bossDEF = new Button(bossStatGroup, SWT.CHECK);
		bossDEF.setText("DEF");
		bossDEF.setToolTipText("Apply buff to boss physical defense.");
		bossDEF.setEnabled(false);
		bossDEF.setSelection(true);
		
		bossRES = new Button(bossStatGroup, SWT.CHECK);
		bossRES.setText("RES");
		bossRES.setToolTipText("Apply buff to boss magical defense.");
		bossRES.setEnabled(false);
		bossRES.setSelection(true);
		
		FormData bossGroupData = new FormData();
		bossGroupData.left = new FormAttachment(0, 10);
		bossGroupData.right = new FormAttachment(100, -10);
		bossGroupData.top = new FormAttachment(bossStatSpinner, 5);
		bossGroupData.width = 200;
		bossStatGroup.setLayoutData(bossGroupData);
		
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
		optionData.top = new FormAttachment(bossStatGroup, 10);
		improveBossWeaponButton.setLayoutData(optionData);
		
		bossWeaponSpinner = new Spinner(bossGroup, SWT.NONE);
		bossWeaponSpinner.setValues(25, 0, 100, 0, 1, 5);
		bossWeaponSpinner.setEnabled(false);
		
		bossWeaponSpinnerLabel = new Label(bossGroup, SWT.NONE);
		bossWeaponSpinnerLabel.setText("Chance:");
		bossWeaponSpinnerLabel.setEnabled(false);
		
		labelData = new FormData();
		labelData.right = new FormAttachment(bossWeaponSpinner, -5);
		labelData.top = new FormAttachment(bossWeaponSpinner, 0, SWT.CENTER);
		bossWeaponSpinnerLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.top = new FormAttachment(improveBossWeaponButton, 5);
		spinnerData.right = new FormAttachment(100, -10);
		spinnerData.bottom = new FormAttachment(100, -5);
		bossWeaponSpinner.setLayoutData(spinnerData);
	}

	@Override
	public EnemyOptions getOptions() {

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
		
		return new EnemyOptions(minionMode, buffSpinner.getSelection(), buffMinionWeapons, minionWeaponChance, 
				new EnemyOptions.BuffStats(minionHP.getSelection(), minionSTR.getSelection(), minionSKL.getSelection(), minionSPD.getSelection(), minionLCK.getSelection(), minionDEF.getSelection(), minionRES.getSelection()), 
				bossMode, bossStatSpinner.getSelection(), buffBossWeapons, bossWeaponChance,
				new EnemyOptions.BuffStats(bossHP.getSelection(), bossSTR.getSelection(), bossSKL.getSelection(), bossSPD.getSelection(), bossLCK.getSelection(), bossDEF.getSelection(), bossRES.getSelection()));
	}

	@Override
	public void initialize(EnemyOptions options) {
		if (options == null) {
			// Shouldn't happen.
			return;
		}
		if (options.minionMode != null) {
			switch (options.minionMode) {
			case NONE:
				buffEnemyGrowthsButton.setSelection(false);
				flatBonusButton.setEnabled(false);
				scalingBonusButton.setEnabled(false);
				buffSpinner.setEnabled(false);
				minionSpinnerLabel.setEnabled(false);
				break;
			case FLAT:
				buffEnemyGrowthsButton.setSelection(true);
				flatBonusButton.setEnabled(true);
				scalingBonusButton.setEnabled(true);
				flatBonusButton.setSelection(true);
				scalingBonusButton.setSelection(false);
				buffSpinner.setEnabled(true);
				minionSpinnerLabel.setEnabled(true);
				buffSpinner.setSelection(options.minionBuff);
				break;
			case SCALING:
				buffEnemyGrowthsButton.setSelection(true);
				flatBonusButton.setEnabled(true);
				scalingBonusButton.setEnabled(true);
				flatBonusButton.setSelection(false);
				scalingBonusButton.setSelection(true);
				buffSpinner.setEnabled(true);
				minionSpinnerLabel.setEnabled(true);
				buffSpinner.setSelection(options.minionBuff);
				break;
			}

			if (options.minionMode != EnemyOptions.MinionGrowthMode.NONE) {
				minionHP.setEnabled(true);
				minionSTR.setEnabled(true);
				minionSKL.setEnabled(true);
				minionSPD.setEnabled(true);
				minionDEF.setEnabled(true);
				minionRES.setEnabled(true);
				minionLCK.setEnabled(true);

				if (options.minionBuffStats != null) {
					minionHP.setSelection(options.minionBuffStats.hp);
					minionSTR.setSelection(options.minionBuffStats.str);
					minionSKL.setSelection(options.minionBuffStats.skl);
					minionSPD.setSelection(options.minionBuffStats.spd);
					minionDEF.setSelection(options.minionBuffStats.def);
					minionRES.setSelection(options.minionBuffStats.res);
					minionLCK.setSelection(options.minionBuffStats.lck);
				}
			}
		}
		if (options.improveMinionWeapons) {
			improveEnemyWeaponsButton.setSelection(true);
			weaponSpinner.setEnabled(true);
			weaponSpinner.setSelection(options.minionImprovementChance);
			minionWeaponSpinnerLabel.setEnabled(true);
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

			if (options.bossMode != EnemyOptions.BossStatMode.NONE) {
				bossHP.setEnabled(true);
				bossSTR.setEnabled(true);
				bossSKL.setEnabled(true);
				bossSPD.setEnabled(true);
				bossDEF.setEnabled(true);
				bossRES.setEnabled(true);
				bossLCK.setEnabled(true);

				if (options.bossBuffStats != null) {
					bossHP.setSelection(options.bossBuffStats.hp);
					bossSTR.setSelection(options.bossBuffStats.str);
					bossSKL.setSelection(options.bossBuffStats.skl);
					bossSPD.setSelection(options.bossBuffStats.spd);
					bossDEF.setSelection(options.bossBuffStats.def);
					bossRES.setSelection(options.bossBuffStats.res);
					bossLCK.setSelection(options.bossBuffStats.lck);
				}
			}
		}
		if (options.improveBossWeapons) {
			improveBossWeaponButton.setSelection(true);
			bossWeaponSpinnerLabel.setEnabled(true);
			bossWeaponSpinner.setEnabled(true);
			bossStatSpinnerLabel.setEnabled(true);
			bossWeaponSpinner.setSelection(options.bossImprovementChance);
		}
	}
}
