package ui.views;

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

import fedata.general.FEBase.GameType;
import ui.common.GuiUtil;
import ui.model.ClassOptions;
import ui.model.ClassOptions.BaseTransferOption;
import ui.model.ClassOptions.GenderRestrictionOption;
import ui.model.ClassOptions.GrowthAdjustmentOption;

public class ClassesView extends YuneView<ClassOptions> {
	

	private Button randomizePCButton;
	private Button randomizePCLordsButton;
	private Button randomizePCThievesButton;
	private Button randomizePCSpecialButton;
	private Button evenClassesButton;

	private Button randomizeEnemiesButton;
	
	private Button randomizeBossesButton;
	
	private Button forceChangeButton;
	
	private Button strictGenderButton;
	private Button looseGenderButton;
	private Button noGenderButton;
	
	private Boolean hasMonsterOption;
	private Button mixMonsterClasses;
	
	private Group baseTransferGroup;
	private Button basesNoChangeButton;
	private Button basesAdjustMatchButton;
	private Button basesAdjustClassButton;
	
	private Group growthAdjustmentGroup;
	private Button growthNoAdjustmentButton;
	private Button personalGrowthButton;
	// Maybe enable this option in the future, but it does result in basically every character of the same class have the same exact growth spread,
	// which, on reflection, doesn't sound very interesting.
//	private Button classRelativeGrowthButton;

	public ClassesView(Composite parent, GameType type) {
		super(parent, type);
	}

	@Override
	public String getGroupTitle() {
		return "Classes";
	}

	@Override
	public String getGroupTooltip() {
		return "Randomize classes for all characters.";
	}

	@Override
	protected void compose() {
		randomizePCButton = new Button(group, SWT.CHECK);
		randomizePCButton.setText("Randomize Playable Characters");
		randomizePCButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				randomizePCLordsButton.setEnabled(randomizePCButton.getSelection());
				randomizePCThievesButton.setEnabled(randomizePCButton.getSelection());
				randomizePCSpecialButton.setEnabled(randomizePCButton.getSelection());
				evenClassesButton.setEnabled(randomizePCButton.getSelection());
				
				baseTransferGroup.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				basesNoChangeButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				basesAdjustMatchButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				basesAdjustClassButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				
				growthAdjustmentGroup.setEnabled(randomizePCButton.getSelection());
				growthNoAdjustmentButton.setEnabled(randomizePCButton.getSelection());
				personalGrowthButton.setEnabled(randomizePCButton.getSelection());
//				classRelativeGrowthButton.setEnabled(randomizePCButton.getSelection());
				
				forceChangeButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection() || randomizeEnemiesButton.getSelection());
				strictGenderButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				looseGenderButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				noGenderButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				
				if (hasMonsterOption) {
					mixMonsterClasses.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection() || randomizeEnemiesButton.getSelection());
				}
			}
		});
		
		FormData pcFormData = new FormData();
		pcFormData.left = new FormAttachment(0, 5);
		pcFormData.top = new FormAttachment(0, 5);
		randomizePCButton.setLayoutData(pcFormData);
		
		randomizePCLordsButton = new Button(group, SWT.CHECK);
		randomizePCLordsButton.setText("Include Lords");
		randomizePCLordsButton.setToolTipText("If enabled, allows lords to be changed to random classes, as well as adds lords to the randomizable class pool.");
		randomizePCLordsButton.setEnabled(false);
		
		FormData pcLordsFormData = new FormData();
		pcLordsFormData.left = new FormAttachment(randomizePCButton, 10, SWT.LEFT);
		pcLordsFormData.top = new FormAttachment(randomizePCButton, 5);
		randomizePCLordsButton.setLayoutData(pcLordsFormData);
		
		randomizePCThievesButton = new Button(group, SWT.CHECK);
		randomizePCThievesButton.setText("Include Thieves");
		randomizePCThievesButton.setToolTipText("If enabled, allows thieves to be changed to random classes, as well as adds thieves to the randomizable class pool.");
		randomizePCThievesButton.setEnabled(false);
		
		FormData pcThievesFormData = new FormData();
		pcThievesFormData.left = new FormAttachment(randomizePCLordsButton, 0, SWT.LEFT);
		pcThievesFormData.top = new FormAttachment(randomizePCLordsButton, 5);
		randomizePCThievesButton.setLayoutData(pcThievesFormData);
		
		randomizePCSpecialButton = new Button(group, SWT.CHECK);
		randomizePCSpecialButton.setText("Include Special Classes");
		randomizePCSpecialButton.setToolTipText("If enabled, allows characters in special classes to be randomized, as well as adding those special classes to the class pool.");
		randomizePCSpecialButton.setEnabled(false);
		
		FormData pcSpecialData = new FormData();
		pcSpecialData.left = new FormAttachment(randomizePCThievesButton, 0, SWT.LEFT);
		pcSpecialData.top = new FormAttachment(randomizePCThievesButton, 5);
		randomizePCSpecialButton.setLayoutData(pcSpecialData);
		
		evenClassesButton = new Button(group, SWT.CHECK);
		evenClassesButton.setText("Assign Classes Evenly");
		evenClassesButton.setToolTipText("Attempts to assign classes so that the number of duplicates is minimized.");
		evenClassesButton.setEnabled(false);
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(randomizePCSpecialButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(randomizePCSpecialButton, 5);
		evenClassesButton.setLayoutData(optionData);
		
		growthAdjustmentGroup = new Group(group, SWT.NONE);
		growthAdjustmentGroup.setText("Growths");
		growthAdjustmentGroup.setLayout(GuiUtil.formLayoutWithMargin());
		
		FormData groupData = new FormData();
		groupData.left = new FormAttachment(0, 10);
		groupData.right = new FormAttachment(100, 0);
		groupData.top = new FormAttachment(evenClassesButton, 10);
		growthAdjustmentGroup.setLayoutData(groupData);
		
		growthNoAdjustmentButton = new Button(growthAdjustmentGroup, SWT.RADIO);
		growthNoAdjustmentButton.setText("No Adjustment");
		growthNoAdjustmentButton.setToolTipText("Do not adjust growth rates.");
		growthNoAdjustmentButton.setEnabled(false);
		growthNoAdjustmentButton.setSelection(true);
		
		FormData noAdjustData = new FormData();
		noAdjustData.left = new FormAttachment(0, 0);
		noAdjustData.top = new FormAttachment(0, 0);
		growthNoAdjustmentButton.setLayoutData(noAdjustData);
		
		personalGrowthButton = new Button(growthAdjustmentGroup, SWT.RADIO);
		personalGrowthButton.setText("Transfer Personal Growths");
		personalGrowthButton.setToolTipText("Apply personal growth offsets from the old class growths to the new class growths.\n\nFor example, if a character's old SPD growth was 10% higher than the old class's SPD growth,\ntheir new SPD growth would be 10% higher than the new class's SPD growth.");
		personalGrowthButton.setEnabled(false);
		personalGrowthButton.setSelection(false);
		
		FormData personalData = new FormData();
		personalData.left = new FormAttachment(0, 0);
		personalData.top = new FormAttachment(growthNoAdjustmentButton, 5);
		personalGrowthButton.setLayoutData(personalData);
		
//		classRelativeGrowthButton = new Button(growthAdjustmentGroup, SWT.RADIO);
//		classRelativeGrowthButton.setText("Class Relative Growths");
//		classRelativeGrowthButton.setToolTipText("Match the character's growth values to the class's growth spread.\n\nFor example, a character becoming a myrmidon will rearrange their growths such that Speed is their highest growth value.");
//		classRelativeGrowthButton.setEnabled(false);
//		classRelativeGrowthButton.setSelection(false);
//		
//		FormData classRelativeData = new FormData();
//		classRelativeData.left = new FormAttachment(0, 0);
//		classRelativeData.top = new FormAttachment(personalGrowthButton, 5);
//		classRelativeGrowthButton.setLayoutData(classRelativeData);
		
		//////////////////////////////////////////////////////////////////
		
		randomizeEnemiesButton = new Button(group, SWT.CHECK);
		randomizeEnemiesButton.setText("Randomize Regular Enemies");
		randomizeEnemiesButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				forceChangeButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection() || randomizeEnemiesButton.getSelection());
				if (hasMonsterOption) {
					mixMonsterClasses.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection() || randomizeEnemiesButton.getSelection());
				}
			}
		});
		
		FormData enemyFormData = new FormData();
		enemyFormData.left = new FormAttachment(randomizePCButton, 0, SWT.LEFT);
		enemyFormData.top = new FormAttachment(growthAdjustmentGroup, 10);
		randomizeEnemiesButton.setLayoutData(enemyFormData);
		
		//////////////////////////////////////////////////////////////////
		
		randomizeBossesButton = new Button(group, SWT.CHECK);
		randomizeBossesButton.setText("Randomize Bosses");
		randomizeBossesButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				baseTransferGroup.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				basesNoChangeButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				basesAdjustMatchButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				basesAdjustClassButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				
				forceChangeButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection() || randomizeEnemiesButton.getSelection());
				strictGenderButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				looseGenderButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				noGenderButton.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection());
				if (hasMonsterOption) {
					mixMonsterClasses.setEnabled(randomizePCButton.getSelection() || randomizeBossesButton.getSelection() || randomizeEnemiesButton.getSelection());
				}
			}
		});
		
		FormData bossFormData = new FormData();
		bossFormData.left = new FormAttachment(randomizeEnemiesButton, 0, SWT.LEFT);
		bossFormData.top = new FormAttachment(randomizeEnemiesButton, 10);
		randomizeBossesButton.setLayoutData(bossFormData);
		
		//////////////////////////////////////////////////////////////////
		
		baseTransferGroup = new Group(group, SWT.NONE);
		baseTransferGroup.setText("Bases");
		baseTransferGroup.setLayout(GuiUtil.formLayoutWithMargin());
		
		groupData = new FormData();
		groupData.left = new FormAttachment(randomizeBossesButton, 0, SWT.LEFT);
		groupData.top = new FormAttachment(randomizeBossesButton, 10);
		groupData.right = new FormAttachment(100, -5);
		baseTransferGroup.setLayoutData(groupData);
		
		basesNoChangeButton = new Button(baseTransferGroup, SWT.RADIO);
		basesNoChangeButton.setText("Retain Personal Bases");
		basesNoChangeButton.setToolTipText("Does not adjust personal base stats. Characters stats will be altered based on their target class.");
		basesNoChangeButton.setEnabled(false);
		basesNoChangeButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		basesNoChangeButton.setLayoutData(optionData);
		
		basesAdjustMatchButton = new Button(baseTransferGroup, SWT.RADIO);
		basesAdjustMatchButton.setText("Retain Final Bases");
		basesAdjustMatchButton.setToolTipText("Adjusts personal bases so that characters will have the same base stats as before the class change.");
		basesAdjustMatchButton.setEnabled(false);
		basesAdjustMatchButton.setSelection(true);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(basesNoChangeButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(basesNoChangeButton, 5);
		basesAdjustMatchButton.setLayoutData(optionData);
		
		basesAdjustClassButton = new Button(baseTransferGroup, SWT.RADIO);
		basesAdjustClassButton.setText("Adjust to Class");
		basesAdjustClassButton.setToolTipText("Adjusts personal bases so that characters will have their best base stats matching their class's best stats.");
		basesAdjustClassButton.setEnabled(false);
		basesAdjustClassButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(basesAdjustMatchButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(basesAdjustMatchButton, 5);
		basesAdjustClassButton.setLayoutData(optionData);
		
		Group genderGroup = new Group(group, SWT.NONE);
		genderGroup.setText("Gender Restriction");
		genderGroup.setLayout(GuiUtil.formLayoutWithMargin());
		
		noGenderButton = new Button(genderGroup, SWT.RADIO);
		noGenderButton.setText("No Restriction");
		noGenderButton.setToolTipText("No gender restrictions. Any character can become any class.");
		noGenderButton.setEnabled(false);
		noGenderButton.setSelection(true);
		
		FormData genderData = new FormData();
		genderData.top = new FormAttachment(0, 0);
		genderData.left = new FormAttachment(0, 0);
		noGenderButton.setLayoutData(genderData);
		
		looseGenderButton = new Button(genderGroup, SWT.RADIO);
		looseGenderButton.setText("Loose Restrictions");
		looseGenderButton.setToolTipText("No gender restriction, but will use the correct gender version of a selected class, if it exists.");
		looseGenderButton.setEnabled(false);
		looseGenderButton.setSelection(false);
		
		genderData = new FormData();
		genderData.top = new FormAttachment(noGenderButton, 5);
		genderData.left = new FormAttachment(0, 0);
		looseGenderButton.setLayoutData(genderData);
		
		strictGenderButton = new Button(genderGroup, SWT.RADIO);
		strictGenderButton.setText("Strict Restrictions");
		strictGenderButton.setToolTipText("Class options are restricted to those that match the character's gender.");
		strictGenderButton.setEnabled(false);
		strictGenderButton.setSelection(false);
		
		genderData = new FormData();
		genderData.top = new FormAttachment(looseGenderButton, 5);
		genderData.left = new FormAttachment(0, 0);
		strictGenderButton.setLayoutData(genderData);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(0, 5);
		optionData.right = new FormAttachment(100, -5);
		optionData.top = new FormAttachment(baseTransferGroup, 10);
		genderGroup.setLayoutData(optionData);
		
		forceChangeButton = new Button(group, SWT.CHECK);
		forceChangeButton.setText("Force Class Change");
		forceChangeButton.setToolTipText("Attempts to force every character to change to a different class.");
		forceChangeButton.setEnabled(false);
		forceChangeButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(genderGroup, 0, SWT.LEFT);
		optionData.top = new FormAttachment(genderGroup, 10);
		forceChangeButton.setLayoutData(optionData);
		
		if (type == GameType.FE8) {
			mixMonsterClasses = new Button(group, SWT.CHECK);
			mixMonsterClasses.setText("Mix Monster Classes");
			mixMonsterClasses.setToolTipText("If enabled, allows cross-assignment of classes between humans and monsters.\nIf disabled, ensures that units that were monsters remain monsters and units that were human remain humans when randomizing classes.\nHas no effect unless another class randomization option is enabled.");
			
			FormData monsterData = new FormData();
			monsterData.left = new FormAttachment(forceChangeButton, 0, SWT.LEFT);
			monsterData.top = new FormAttachment(forceChangeButton, 5);
			mixMonsterClasses.setLayoutData(monsterData);
			
			hasMonsterOption = true;
		} else {
			hasMonsterOption = false;
		}
	}

	@Override
	public ClassOptions getOptions() {
		Boolean pcsEnabled = randomizePCButton.getSelection();
		Boolean lordsEnabled = false;
		Boolean thievesEnabled = false;
		Boolean specialEnabled = false;
		if (pcsEnabled) {
			lordsEnabled = randomizePCLordsButton.getSelection();
			thievesEnabled = randomizePCThievesButton.getSelection();
			specialEnabled = randomizePCSpecialButton.getSelection();
		}

		BaseTransferOption baseOption = BaseTransferOption.ADJUST_TO_MATCH;
		if (basesNoChangeButton.getSelection()) { baseOption = BaseTransferOption.NO_CHANGE; }
		else if (basesAdjustClassButton.getSelection()) { baseOption = BaseTransferOption.ADJUST_TO_CLASS; }
		
		GenderRestrictionOption genderOption = GenderRestrictionOption.NONE;
		if (looseGenderButton.getSelection()) { genderOption = GenderRestrictionOption.LOOSE; }
		else if (strictGenderButton.getSelection()) { genderOption = GenderRestrictionOption.STRICT; }
		
		GrowthAdjustmentOption growthOption = GrowthAdjustmentOption.NO_CHANGE;
		if (personalGrowthButton.getSelection()) { growthOption = GrowthAdjustmentOption.TRANSFER_PERSONAL_GROWTHS; }
//		else if (classRelativeGrowthButton.getSelection()) { growthOption = GrowthAdjustmentOption.CLASS_RELATIVE_GROWTHS; }
		
		if (hasMonsterOption) {
			return new ClassOptions(pcsEnabled, lordsEnabled, thievesEnabled, specialEnabled, !mixMonsterClasses.getSelection(), forceChangeButton.getSelection(), genderOption, evenClassesButton.getSelection(), randomizeEnemiesButton.getSelection(), randomizeBossesButton.getSelection(), baseOption, growthOption);
		} else {
			return new ClassOptions(pcsEnabled, lordsEnabled, thievesEnabled, specialEnabled, forceChangeButton.getSelection(), genderOption, evenClassesButton.getSelection(), randomizeEnemiesButton.getSelection(), randomizeBossesButton.getSelection(), baseOption, growthOption);
		}
	}

	@Override
	public void initialize(ClassOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			if (options.randomizePCs) {
				randomizePCButton.setSelection(true);
				randomizePCLordsButton.setEnabled(true);
				randomizePCThievesButton.setEnabled(true);
				randomizePCSpecialButton.setEnabled(true);
				evenClassesButton.setEnabled(true);
				
				randomizePCLordsButton.setSelection(options.includeLords != null ? options.includeLords : false);
				randomizePCThievesButton.setSelection(options.includeThieves != null ? options.includeThieves : false);
				randomizePCSpecialButton.setSelection(options.includeSpecial != null ? options.includeSpecial : false);
				evenClassesButton.setSelection(options.assignEvenly);
				
				growthAdjustmentGroup.setEnabled(true);
				growthNoAdjustmentButton.setEnabled(true);
				personalGrowthButton.setEnabled(true);
//				classRelativeGrowthButton.setEnabled(true);
				
				growthNoAdjustmentButton.setSelection(options.growthOptions == null || options.growthOptions == GrowthAdjustmentOption.NO_CHANGE);
				personalGrowthButton.setSelection(options.growthOptions == GrowthAdjustmentOption.TRANSFER_PERSONAL_GROWTHS);
//				classRelativeGrowthButton.setSelection(options.growthOptions == GrowthAdjustmentOption.CLASS_RELATIVE_GROWTHS);
			}
			
			randomizeEnemiesButton.setSelection(options.randomizeEnemies);
			randomizeBossesButton.setSelection(options.randomizeBosses);
			
			if (options.randomizePCs || options.randomizeBosses) {
				baseTransferGroup.setEnabled(true);
				basesNoChangeButton.setEnabled(true);
				basesAdjustMatchButton.setEnabled(true);
				basesAdjustClassButton.setEnabled(true);
				
				noGenderButton.setEnabled(true);
				looseGenderButton.setEnabled(true);
				strictGenderButton.setEnabled(true);
				
				basesNoChangeButton.setSelection(options.basesTransfer == BaseTransferOption.NO_CHANGE);
				basesAdjustMatchButton.setSelection(options.basesTransfer == BaseTransferOption.ADJUST_TO_MATCH || options.basesTransfer == null);
				basesAdjustClassButton.setSelection(options.basesTransfer == BaseTransferOption.ADJUST_TO_CLASS);
				
				noGenderButton.setSelection(options.genderOption == GenderRestrictionOption.NONE);
				looseGenderButton.setSelection(options.genderOption == GenderRestrictionOption.LOOSE);
				strictGenderButton.setSelection(options.genderOption == GenderRestrictionOption.STRICT);
			} else {
				baseTransferGroup.setEnabled(false);
				basesNoChangeButton.setEnabled(false);
				basesAdjustMatchButton.setEnabled(false);
				basesAdjustClassButton.setEnabled(false);
				
				noGenderButton.setEnabled(false);
				looseGenderButton.setEnabled(false);
				strictGenderButton.setEnabled(false);
			}
			
			if (options.randomizePCs || options.randomizeEnemies || options.randomizeBosses) {
				forceChangeButton.setEnabled(true);
				forceChangeButton.setSelection(options.forceChange);
				
				if (hasMonsterOption) {
					mixMonsterClasses.setEnabled(true);
					mixMonsterClasses.setSelection(!options.separateMonsters);
				}
			} else {
				forceChangeButton.setEnabled(false);
				if (hasMonsterOption) {
					mixMonsterClasses.setEnabled(false);
				}
			}
		}
	}
}
