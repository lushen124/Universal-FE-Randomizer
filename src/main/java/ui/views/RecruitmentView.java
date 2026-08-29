package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import fedata.general.FEBase.GameType;
import ui.common.GuiUtil;
import ui.model.RecruitmentOptions;
import ui.model.RecruitmentOptions.BaseStatAutolevelType;
import ui.model.RecruitmentOptions.ClassMode;
import ui.model.RecruitmentOptions.GrowthAdjustmentMode;
import ui.model.RecruitmentOptions.StatAdjustmentMode;

public class RecruitmentView extends YuneView<RecruitmentOptions> {
	

	private Button enableButton;
	
	private Group growthContainer;
	private Button fillGrowthButton;
	private Button slotGrowthButton;
	private Button slotRelativeGrowthButton;
	
	private Group basesContainer;
	private Button autolevelButton;
	private Composite autolevelTypeContainer;
	private Button autolevelOriginalButton;
	private Button autolevelNewButton;
	private Button absoluteButton;
	private Button relativeButton;
	
	private Group classContainer;
	private Button fillClassButton;
	private Button slotClassButton;
	
	private Button lordsButton;
	private Button thievesButton;
	private Button specialButton;
	
	private Button crossGenderButton;
	private Button includeExtras;
	
	public RecruitmentView(Composite parent, GameType type) {
		super(parent, type);
	}

	@Override
	public String getGroupTitle() {
		return "Recruitment";
	}

	@Override
	public String getGroupTooltip() {
		return "Randomized character join order.";
	}

	@Override
	protected void compose() {
		enableButton = new Button(group, SWT.CHECK);
		enableButton.setText("Randomize Recruitment");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				growthContainer.setEnabled(enableButton.getSelection());
				basesContainer.setEnabled(enableButton.getSelection());
				classContainer.setEnabled(enableButton.getSelection());
				
				fillGrowthButton.setEnabled(enableButton.getSelection());
				slotGrowthButton.setEnabled(enableButton.getSelection());
				slotRelativeGrowthButton.setEnabled(enableButton.getSelection());
				
				autolevelButton.setEnabled(enableButton.getSelection());
				absoluteButton.setEnabled(enableButton.getSelection());
				relativeButton.setEnabled(enableButton.getSelection());
				
				crossGenderButton.setEnabled(enableButton.getSelection());
				lordsButton.setEnabled(enableButton.getSelection());
				thievesButton.setEnabled(enableButton.getSelection());
				specialButton.setEnabled(enableButton.getSelection());
				
				autolevelTypeContainer.setEnabled(enableButton.getSelection() && autolevelButton.getSelection());
				autolevelOriginalButton.setEnabled(enableButton.getSelection() && autolevelButton.getSelection());
				autolevelNewButton.setEnabled(enableButton.getSelection() && autolevelButton.getSelection());
				
				fillClassButton.setEnabled(enableButton.getSelection());
				slotClassButton.setEnabled(enableButton.getSelection());
				
				if (includeExtras != null) {
					includeExtras.setEnabled(enableButton.getSelection());
				}
			}
		});
		
		FormData enableData = new FormData();
		enableData.left = new FormAttachment(0, 0);
		enableData.top = new FormAttachment(0, 0);
		enableButton.setLayoutData(enableData);
		
		///////////////////////////////////////////
		
		growthContainer = new Group(group, SWT.NONE);
		growthContainer.setText("Growths");
		growthContainer.setToolTipText("Determines how growths are assigned.");
		growthContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		FormData groupData = new FormData();
		groupData.left = new FormAttachment(enableButton, 10, SWT.LEFT);
		groupData.top = new FormAttachment(enableButton, 10);
		groupData.right = new FormAttachment(100, -5);
		growthContainer.setLayoutData(groupData);
		
		fillGrowthButton = new Button(growthContainer, SWT.RADIO);
		fillGrowthButton.setText("Use Fill Growths");
		fillGrowthButton.setToolTipText("Characters use their natural growth rates.");
		fillGrowthButton.setEnabled(false);
		fillGrowthButton.setSelection(true);
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		optionData.right = new FormAttachment(100, -5);
		fillGrowthButton.setLayoutData(optionData);
		
		slotGrowthButton = new Button(growthContainer, SWT.RADIO);
		slotGrowthButton.setText("Use Slot Growths");
		slotGrowthButton.setToolTipText("Characters use the growth rates of the character they replace.");
		slotGrowthButton.setEnabled(false);
		slotGrowthButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(fillGrowthButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(fillGrowthButton, 5);
		slotGrowthButton.setLayoutData(optionData);
		
		slotRelativeGrowthButton = new Button(growthContainer, SWT.RADIO);
		slotRelativeGrowthButton.setText("Slot Relative Growths");
		slotRelativeGrowthButton.setToolTipText("Characters use the growth values of the character they replace,\nbut retain their own growth strengths and weaknesses.");
		slotRelativeGrowthButton.setEnabled(false);
		slotRelativeGrowthButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(slotGrowthButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(slotGrowthButton, 5);
		slotRelativeGrowthButton.setLayoutData(optionData);
		
		/////////////////////////////////////////////////
		
		basesContainer = new Group(group, SWT.NONE);
		basesContainer.setText("Bases");
		basesContainer.setToolTipText("Determines how bases are transferred.");
		basesContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		groupData = new FormData();
		groupData.left = new FormAttachment(growthContainer, 0, SWT.LEFT);
		groupData.top = new FormAttachment(growthContainer, 10);
		groupData.right = new FormAttachment(100, -5);
		basesContainer.setLayoutData(groupData);
		
		autolevelButton = new Button(basesContainer, SWT.RADIO);
		autolevelButton.setText("Autolevel Base Stats");
		autolevelButton.setToolTipText("Uses the character's growth rates to simulate leveling up or down from the character's original stats to their target level.");
		autolevelButton.setEnabled(false);
		autolevelButton.setSelection(true);
		autolevelButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				autolevelTypeContainer.setEnabled(autolevelButton.getSelection() && enableButton.getSelection());
				autolevelOriginalButton.setEnabled(autolevelButton.getSelection() && enableButton.getSelection());
				autolevelNewButton.setEnabled(autolevelButton.getSelection() && enableButton.getSelection());
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		autolevelButton.setLayoutData(optionData);
		
		autolevelTypeContainer = new Composite(basesContainer, SWT.NONE);
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		fillLayout.spacing = 5;
		autolevelTypeContainer.setLayout(fillLayout);
		
		autolevelOriginalButton = new Button(autolevelTypeContainer, SWT.RADIO);
		autolevelOriginalButton.setText("Use Original Growths");
		autolevelOriginalButton.setToolTipText("Uses the character's natural growth rates to autolevel.");
		autolevelOriginalButton.setEnabled(false);
		autolevelOriginalButton.setSelection(true);
		
		autolevelNewButton = new Button(autolevelTypeContainer, SWT.RADIO);
		autolevelNewButton.setText("Use New Growths");
		autolevelNewButton.setToolTipText("Uses the character's newly assigned growth from above to autolevel.");
		autolevelNewButton.setEnabled(false);
		autolevelNewButton.setSelection(false);
		
		FormData autolevelContainerData = new FormData();
		autolevelContainerData.left = new FormAttachment(autolevelButton, 10, SWT.LEFT);
		autolevelContainerData.top = new FormAttachment(autolevelButton, 5);
		autolevelContainerData.right = new FormAttachment(100, -5);
		autolevelTypeContainer.setLayoutData(autolevelContainerData);
		
		absoluteButton = new Button(basesContainer, SWT.RADIO);
		absoluteButton.setText("Match Base Stats");
		absoluteButton.setToolTipText("Sets a character's base stats to match the character they replace.");
		absoluteButton.setEnabled(false);
		absoluteButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(autolevelButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(autolevelTypeContainer, 5);
		absoluteButton.setLayoutData(optionData);
		
		relativeButton = new Button(basesContainer, SWT.RADIO);
		relativeButton.setText("Relative Base Stats");
		relativeButton.setToolTipText("Pins the character's max stat to the max stat of the character they replace and retains the character's stat spread.");
		relativeButton.setEnabled(false);
		relativeButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(absoluteButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(absoluteButton, 5);
		relativeButton.setLayoutData(optionData);
		
		classContainer = new Group(group, SWT.NONE);
		classContainer.setText("Classes");
		classContainer.setToolTipText("Determines how classes are assigned.");
		classContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		groupData = new FormData();
		groupData.left = new FormAttachment(basesContainer, 0, SWT.LEFT);
		groupData.top = new FormAttachment(basesContainer, 10);
		groupData.right = new FormAttachment(100, -5);
		classContainer.setLayoutData(groupData);
		
		fillClassButton = new Button(classContainer, SWT.RADIO);
		fillClassButton.setText("Use Fill Class");
		switch (type) {
		case FE6:
			fillClassButton.setToolTipText("Characters retain their original class (after necessary promotion/demotion).\n\nFor example, Percival taking the place of Wolt will be a cavalier.");
			break;
		case FE7:
			fillClassButton.setToolTipText("Characters retain their original class (after necessary promotion/demotion).\n\nFor example, Louise taking the place of Serra will be an archer.");
			break;
		case FE8:
			fillClassButton.setToolTipText("Characters retain their original class (after necessary promotion/demotion).\n\nFor example, Duessel taking the place of Garcia will be either a Cavalier or an Armor Knight (due to branched promotion).");
			break;
		default:
			break;
		}
		fillClassButton.setEnabled(false);
		fillClassButton.setSelection(true);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		fillClassButton.setLayoutData(optionData);
		
		slotClassButton = new Button(classContainer, SWT.RADIO);
		slotClassButton.setText("Use Slot Class");
		switch (type) {
		case FE6:
			slotClassButton.setToolTipText("Characters take the class of the slot they fill.\n\nFor example, Percival taking the place of Wolt will be an archer.");
			break;
		case FE7:
			slotClassButton.setToolTipText("Characters take the class of the slot they fill.\n\nFor example, Louise taking the place of Serra will be a cleric.");
			break;
		case FE8:
			slotClassButton.setToolTipText("Characters take the class of the slot they fill.\n\nFor example, Duessel taking the place of Garcia will be a fighter.");
			break;
		default:
			break;
		}
		slotClassButton.setEnabled(false);
		slotClassButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(fillClassButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(fillClassButton, 5);
		slotClassButton.setLayoutData(optionData);
		
		lordsButton = new Button(group, SWT.CHECK);
		lordsButton.setText("Include Lords");
		lordsButton.setToolTipText("Allows Lord characters to randomize their recruitment time.");
		lordsButton.setEnabled(false);
		lordsButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(classContainer, 0, SWT.LEFT);
		optionData.top = new FormAttachment(classContainer, 10);
		lordsButton.setLayoutData(optionData);

		thievesButton = new Button(group, SWT.CHECK);
		thievesButton.setText("Include Thieves");
		thievesButton.setToolTipText("Allows Thief characters to randomize their recruitment time.");
		thievesButton.setEnabled(false);
		thievesButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(lordsButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(lordsButton, 10);
		thievesButton.setLayoutData(optionData);
		
		specialButton = new Button(group, SWT.CHECK);
		specialButton.setText("Include Special Characters");
		specialButton.setToolTipText("Allows Dancers, Bards, and Manaketes to randomize their recruitment time.");
		specialButton.setEnabled(false);
		specialButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(thievesButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(thievesButton, 5);
		specialButton.setLayoutData(optionData);
		
		crossGenderButton = new Button(group, SWT.CHECK);
		crossGenderButton.setText("Allow Cross-gender Assignments");
		crossGenderButton.setToolTipText("Allows males to be assigned to female slots and vice versa.");
		crossGenderButton.setEnabled(false);
		crossGenderButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(specialButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(specialButton, 5);
		crossGenderButton.setLayoutData(optionData);
		
		if (type == GameType.FE8) {
			// Option to include Creature Campaign
			includeExtras = new Button(group, SWT.CHECK);
			includeExtras.setText("Include Creature Campaign NPCs");
			includeExtras.setToolTipText("Includes NPCs from the creature campaign into the pool.\nSpecifically: Glen, Fado, Hayden, and Ismaire.");
			includeExtras.setEnabled(false);
			includeExtras.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(crossGenderButton, 0, SWT.LEFT);
			optionData.top = new FormAttachment(crossGenderButton, 5);
			includeExtras.setLayoutData(optionData);
		}
	}

	@Override
	public RecruitmentOptions getOptions() {
		boolean isEnabled = enableButton.getSelection();
		StatAdjustmentMode basesMode = null;
		BaseStatAutolevelType autolevel = null;
		if (autolevelButton.getSelection()) { 
			basesMode = StatAdjustmentMode.AUTOLEVEL;
			if (autolevelOriginalButton.getSelection()) { autolevel = BaseStatAutolevelType.USE_ORIGINAL; }
			else if (autolevelNewButton.getSelection()) { autolevel = BaseStatAutolevelType.USE_NEW; }
		}
		else if (absoluteButton.getSelection()) { basesMode = StatAdjustmentMode.MATCH_SLOT; }
		else if (relativeButton.getSelection()) { basesMode = StatAdjustmentMode.RELATIVE_TO_SLOT; }
		
		GrowthAdjustmentMode growthMode = null;
		if (fillGrowthButton.getSelection()) { growthMode = GrowthAdjustmentMode.USE_FILL; }
		else if (slotGrowthButton.getSelection()) { growthMode = GrowthAdjustmentMode.USE_SLOT; }
		else if (slotRelativeGrowthButton.getSelection()) { growthMode = GrowthAdjustmentMode.RELATIVE_TO_SLOT; }
		
		boolean extras = includeExtras != null ? includeExtras.getSelection() : false;
		
		ClassMode classMode = ClassMode.USE_FILL;
		if (slotClassButton.getSelection()) { classMode = ClassMode.USE_SLOT; }
		
		if (isEnabled && basesMode != null && growthMode != null) {
			return new RecruitmentOptions(growthMode, basesMode, autolevel, classMode, lordsButton.getSelection(), thievesButton.getSelection(), specialButton.getSelection(), crossGenderButton.getSelection(), extras);
		} else {
			return null;
		}
	}

	@Override
	public void initialize(RecruitmentOptions options) {
		boolean optionsAvailable = options != null;
		enableButton.setSelection(optionsAvailable);
		growthContainer.setEnabled(optionsAvailable);
		basesContainer.setEnabled(optionsAvailable);
		classContainer.setEnabled(optionsAvailable);

		fillGrowthButton.setEnabled(optionsAvailable);
		slotGrowthButton.setEnabled(optionsAvailable);
		slotRelativeGrowthButton.setEnabled(optionsAvailable);

		autolevelButton.setEnabled(optionsAvailable);
		autolevelTypeContainer.setEnabled(optionsAvailable);
		autolevelOriginalButton.setEnabled(optionsAvailable);
		autolevelNewButton.setEnabled(optionsAvailable);

		absoluteButton.setEnabled(optionsAvailable);
		relativeButton.setEnabled(optionsAvailable);

		fillClassButton.setEnabled(optionsAvailable);
		slotClassButton.setEnabled(optionsAvailable);

		lordsButton.setEnabled(optionsAvailable);
		thievesButton.setEnabled(optionsAvailable);
		specialButton.setEnabled(optionsAvailable);
		crossGenderButton.setEnabled(optionsAvailable);

		// This button might be null as FE7 for example doesn't have extras
		if (includeExtras != null) {
			includeExtras.setEnabled(optionsAvailable);
		}

		if (optionsAvailable) {
			fillGrowthButton.setSelection(options.growthMode == GrowthAdjustmentMode.USE_FILL || options.growthMode == null);
			slotGrowthButton.setSelection(options.growthMode == GrowthAdjustmentMode.USE_SLOT);
			slotRelativeGrowthButton.setSelection(options.growthMode == GrowthAdjustmentMode.RELATIVE_TO_SLOT);
			
			autolevelButton.setSelection(options.baseMode == StatAdjustmentMode.AUTOLEVEL || options.baseMode == null);
			absoluteButton.setSelection(options.baseMode == StatAdjustmentMode.MATCH_SLOT);
			relativeButton.setSelection(options.baseMode == StatAdjustmentMode.RELATIVE_TO_SLOT);
			
			autolevelOriginalButton.setSelection(options.autolevelMode == BaseStatAutolevelType.USE_ORIGINAL || options.autolevelMode == null);
			autolevelNewButton.setSelection(options.autolevelMode == BaseStatAutolevelType.USE_NEW);
			
			fillClassButton.setSelection(options.classMode == ClassMode.USE_FILL || options.classMode == null);
			slotClassButton.setSelection(options.classMode == ClassMode.USE_SLOT);
			
			autolevelOriginalButton.setEnabled(autolevelButton.getSelection());
			autolevelNewButton.setEnabled(autolevelButton.getSelection());
			
			lordsButton.setSelection(options.includeLords);
			thievesButton.setSelection(options.includeThieves);
			specialButton.setSelection(options.includeSpecial);
			crossGenderButton.setSelection(options.allowCrossGender);
			
			if (includeExtras != null) {
				includeExtras.setSelection(options.includeExtras);
			}
		}
	}
}
