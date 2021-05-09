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
import org.eclipse.swt.widgets.Listener;

import fedata.general.FEBase.GameType;
import ui.model.ClassOptions;
import ui.model.ClassOptions.BaseTransferOption;
import ui.model.ClassOptions.GenderRestrictionOption;

public class ClassesView extends Composite {
	
	private Group container;
	
	private Button randomizePCButton;
	private Button randomizePCLordsButton;
	private Button createNewPrfWeaponsButton;
	private Button unbreakablePrfsButton;
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

	public ClassesView(Composite parent, int style, GameType type) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Classes");
		container.setToolTipText("Randomize classes for all characters.");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		randomizePCButton = new Button(container, SWT.CHECK);
		randomizePCButton.setText("Randomize Playable Characters");
		randomizePCButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				randomizePCLordsButton.setEnabled(randomizePCButton.getSelection());
				createNewPrfWeaponsButton.setEnabled(randomizePCButton.getSelection() && randomizePCLordsButton.getSelection());
				unbreakablePrfsButton.setEnabled(randomizePCButton.getSelection() && randomizePCLordsButton.getSelection() && createNewPrfWeaponsButton.getSelection());
				randomizePCThievesButton.setEnabled(randomizePCButton.getSelection());
				randomizePCSpecialButton.setEnabled(randomizePCButton.getSelection());
				evenClassesButton.setEnabled(randomizePCButton.getSelection());
				
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
		
		FormData pcFormData = new FormData();
		pcFormData.left = new FormAttachment(0, 5);
		pcFormData.top = new FormAttachment(0, 5);
		randomizePCButton.setLayoutData(pcFormData);
		
		randomizePCLordsButton = new Button(container, SWT.CHECK);
		randomizePCLordsButton.setText("Include Lords");
		randomizePCLordsButton.setToolTipText("If enabled, allows lords to be changed to random classes, as well as adds lords to the randomizable class pool.");
		randomizePCLordsButton.setEnabled(false);
		
		FormData pcLordsFormData = new FormData();
		pcLordsFormData.left = new FormAttachment(randomizePCButton, 10, SWT.LEFT);
		pcLordsFormData.top = new FormAttachment(randomizePCButton, 5);
		randomizePCLordsButton.setLayoutData(pcLordsFormData);
		
		createNewPrfWeaponsButton = new Button(container, SWT.CHECK);
		createNewPrfWeaponsButton.setText("Create Matching Prf Weapons");
		if (type == GameType.FE6) {
			createNewPrfWeaponsButton.setToolTipText("If enabled, new weapons matching Roy's new class are created and replaces the starting Rapier (with identical stats and traits).\nThis weapon will be locked to Roy only. This also updates the Binding Blade to be a weapon type Roy can use.\n\nNote: Due to a lack of weapon locks, Rapiers will no longer be in the weapon pool.");
		} else if (type == GameType.FE7) {
			createNewPrfWeaponsButton.setToolTipText("If enabled, new weapons matching Lyn/Eliwood/Hector's new classes are created and replace the starting Prf weapons (with identical stats and traits).\nNew weapons are locked to the characters specifically. This also updates Sol Katti/Durandal/Armads to be the correct weapon type for their respective lords.");
		} else if (type == GameType.FE8) {
			createNewPrfWeaponsButton.setToolTipText("If enabled, new weapons matching Eirika/Ephraim's new classes are created and replace the starting Prf weapons (with identical stats and traits).\nNew weapons are locked specifically to their respective characters. This also updates Sieglinde/Siegmund to be the correct weapon type for their respective lords.");
		}
		createNewPrfWeaponsButton.setEnabled(false);
		
		FormData prfWeaponsData = new FormData();
		prfWeaponsData.left = new FormAttachment(randomizePCLordsButton, 10, SWT.LEFT);
		prfWeaponsData.top = new FormAttachment(randomizePCLordsButton, 5);
		createNewPrfWeaponsButton.setLayoutData(prfWeaponsData);
		
		unbreakablePrfsButton = new Button(container, SWT.CHECK);
		unbreakablePrfsButton.setText("Make Prf Weapons Unbreakable");
		unbreakablePrfsButton.setToolTipText("If enabled, newly created Prf weapons will have infinite durability.");
		unbreakablePrfsButton.setEnabled(false);
		
		FormData unbreakablePrfData = new FormData();
		unbreakablePrfData.left = new FormAttachment(createNewPrfWeaponsButton, 10, SWT.LEFT);
		unbreakablePrfData.top = new FormAttachment(createNewPrfWeaponsButton, 5);
		unbreakablePrfsButton.setLayoutData(unbreakablePrfData);
		
		randomizePCLordsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				createNewPrfWeaponsButton.setEnabled(randomizePCLordsButton.getSelection() && randomizePCButton.getSelection());
				unbreakablePrfsButton.setEnabled(randomizePCLordsButton.getSelection() && createNewPrfWeaponsButton.getSelection() && randomizePCButton.getSelection());
			}
		});
		
		createNewPrfWeaponsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				unbreakablePrfsButton.setEnabled(randomizePCLordsButton.getSelection() && createNewPrfWeaponsButton.getSelection() && randomizePCButton.getSelection());
			}
		});
		
		randomizePCThievesButton = new Button(container, SWT.CHECK);
		randomizePCThievesButton.setText("Include Thieves");
		randomizePCThievesButton.setToolTipText("If enabled, allows thieves to be changed to random classes, as well as adds thieves to the randomizable class pool.");
		randomizePCThievesButton.setEnabled(false);
		
		FormData pcThievesFormData = new FormData();
		pcThievesFormData.left = new FormAttachment(randomizePCLordsButton, 0, SWT.LEFT);
		pcThievesFormData.top = new FormAttachment(unbreakablePrfsButton, 5);
		randomizePCThievesButton.setLayoutData(pcThievesFormData);
		
		randomizePCSpecialButton = new Button(container, SWT.CHECK);
		randomizePCSpecialButton.setText("Include Special Classes");
		randomizePCSpecialButton.setToolTipText("If enabled, allows characters in special classes to be randomized, as well as adding those special classes to the class pool.");
		randomizePCSpecialButton.setEnabled(false);
		
		FormData pcSpecialData = new FormData();
		pcSpecialData.left = new FormAttachment(randomizePCThievesButton, 0, SWT.LEFT);
		pcSpecialData.top = new FormAttachment(randomizePCThievesButton, 5);
		randomizePCSpecialButton.setLayoutData(pcSpecialData);
		
		evenClassesButton = new Button(container, SWT.CHECK);
		evenClassesButton.setText("Assign Classes Evenly");
		evenClassesButton.setToolTipText("Attempts to assign classes so that the number of duplicates is minimized.");
		evenClassesButton.setEnabled(false);
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(randomizePCSpecialButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(randomizePCSpecialButton, 5);
		evenClassesButton.setLayoutData(optionData);
		
		//////////////////////////////////////////////////////////////////
		
		randomizeEnemiesButton = new Button(container, SWT.CHECK);
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
		enemyFormData.top = new FormAttachment(evenClassesButton, 10);
		randomizeEnemiesButton.setLayoutData(enemyFormData);
		
		//////////////////////////////////////////////////////////////////
		
		randomizeBossesButton = new Button(container, SWT.CHECK);
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
		
		baseTransferGroup = new Group(container, SWT.NONE);
		baseTransferGroup.setText("Bases");
		
		FormLayout groupLayout = new FormLayout();
		groupLayout.marginLeft = 5;
		groupLayout.marginRight = 5;
		groupLayout.marginTop = 5;
		groupLayout.marginBottom = 5;
		baseTransferGroup.setLayout(groupLayout);
		
		FormData groupData = new FormData();
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
		
		Group genderGroup = new Group(container, SWT.NONE);
		genderGroup.setText("Gender Restriction");
		FormLayout genderLayout = new FormLayout();
		genderLayout.marginLeft = 5;
		genderLayout.marginTop = 5;
		genderLayout.marginRight = 5;
		genderLayout.marginBottom = 5;
		genderGroup.setLayout(genderLayout);
		
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
		
		forceChangeButton = new Button(container, SWT.CHECK);
		forceChangeButton.setText("Force Class Change");
		forceChangeButton.setToolTipText("Attempts to force every character to change to a different class.");
		forceChangeButton.setEnabled(false);
		forceChangeButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(genderGroup, 0, SWT.LEFT);
		optionData.top = new FormAttachment(genderGroup, 10);
		forceChangeButton.setLayoutData(optionData);
		
		if (type == GameType.FE8) {
			mixMonsterClasses = new Button(container, SWT.CHECK);
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
	
	public ClassOptions getClassOptions() {
		Boolean pcsEnabled = randomizePCButton.getSelection();
		Boolean lordsEnabled = false;
		Boolean thievesEnabled = false;
		Boolean specialEnabled = false;
		if (pcsEnabled) {
			lordsEnabled = randomizePCLordsButton.getSelection();
			thievesEnabled = randomizePCThievesButton.getSelection();
			specialEnabled = randomizePCSpecialButton.getSelection();
		}
		
		Boolean newPrfs = false;
		Boolean unbreakablePrfs = false;
		if (lordsEnabled) {
			newPrfs = createNewPrfWeaponsButton.getSelection();
			if (newPrfs) {
				unbreakablePrfs = unbreakablePrfsButton.getSelection();
			}
		}
		
		BaseTransferOption baseOption = BaseTransferOption.ADJUST_TO_MATCH;
		if (basesNoChangeButton.getSelection()) { baseOption = BaseTransferOption.NO_CHANGE; }
		else if (basesAdjustClassButton.getSelection()) { baseOption = BaseTransferOption.ADJUST_TO_CLASS; }
		
		GenderRestrictionOption genderOption = GenderRestrictionOption.NONE;
		if (looseGenderButton.getSelection()) { genderOption = GenderRestrictionOption.LOOSE; }
		else if (strictGenderButton.getSelection()) { genderOption = GenderRestrictionOption.STRICT; }
		
		if (hasMonsterOption) {
			return new ClassOptions(pcsEnabled, lordsEnabled, newPrfs, unbreakablePrfs, thievesEnabled, specialEnabled, !mixMonsterClasses.getSelection(), forceChangeButton.getSelection(), genderOption, evenClassesButton.getSelection(), randomizeEnemiesButton.getSelection(), randomizeBossesButton.getSelection(), baseOption);
		} else {
			return new ClassOptions(pcsEnabled, lordsEnabled, newPrfs, unbreakablePrfs, thievesEnabled, specialEnabled, forceChangeButton.getSelection(), genderOption, evenClassesButton.getSelection(), randomizeEnemiesButton.getSelection(), randomizeBossesButton.getSelection(), baseOption);
		}
	}
	
	public void setClassOptions(ClassOptions options) {
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
				if (options.includeLords) {
					createNewPrfWeaponsButton.setEnabled(true);
					createNewPrfWeaponsButton.setSelection(options.createPrfs != null ? options.createPrfs : false);
					if (options.createPrfs) {
						unbreakablePrfsButton.setEnabled(true);
						unbreakablePrfsButton.setSelection(options.unbreakablePrfs != null ? options.unbreakablePrfs : false);
					}
				}
				
				randomizePCThievesButton.setSelection(options.includeThieves != null ? options.includeThieves : false);
				randomizePCSpecialButton.setSelection(options.includeSpecial != null ? options.includeSpecial : false);
				evenClassesButton.setSelection(options.assignEvenly);
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
