package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Item;
import fedata.gcnwii.fe9.FE9Skill;
import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterArmy.FE9ChapterArmySection;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9CommonTextLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;
import ui.component.LabelFieldView;
import util.WhyDoesJavaNotHaveThese;

public class ArmyDataView extends Composite {
	
	private FE9CommonTextLoader textData;
	private FE9ClassDataLoader classData;
	private FE9CharacterDataLoader charData;
	private FE9ItemDataLoader itemData;
	private FE9ChapterDataLoader chapterData;
	private FE9SkillDataLoader skillData;
	
	private Combo chapterDropdown;
	
	private Combo armyDropdown;
	
	private Combo sectionDropdown;
	
	private Combo unitDropdown;
	
	private Group infoGroup;
	private LabelFieldView pid;
	private LabelFieldView jid;
	private LabelFieldView startingLevel;
	private LabelFieldView startingPosition;
	
	private Group inventoryGroup;
	private LabelFieldView weapon1;
	private LabelFieldView weapon2;
	private LabelFieldView weapon3;
	private LabelFieldView weapon4;
	private LabelFieldView item1;
	private LabelFieldView item2;
	private LabelFieldView item3;
	private LabelFieldView item4;
	private Button weapon1Drop;
	private Button weapon2Drop;
	private Button weapon3Drop;
	private Button weapon4Drop;
	private Button item1Drop;
	private Button item2Drop;
	private Button item3Drop;
	private Button item4Drop;
	
	private Group skillStatGroup;
	private LabelFieldView additionalSkill1;
	private LabelFieldView additionalSkill2;
	private LabelFieldView additionalSkill3;
	private LabelFieldView hpAdjust;
	private LabelFieldView strAdjust;
	private LabelFieldView magAdjust;
	private LabelFieldView sklAdjust;
	private LabelFieldView spdAdjust;
	private LabelFieldView lckAdjust;
	private LabelFieldView defAdjust;
	private LabelFieldView resAdjust;
	
	private Group otherGroup;
	private LabelFieldView seq1;
	private LabelFieldView seq2;
	private LabelFieldView seq3;
	private LabelFieldView mtype;
	private LabelFieldView postSkill;
	private LabelFieldView postAdjustment;
	private LabelFieldView preDrop;
	
	private FE9Data.Chapter currentChapter;
	private FE9ChapterArmy currentArmy;
	private FE9ChapterArmySection currentArmySection;
	private FE9ChapterUnit currentUnit;
	
	public ArmyDataView(Composite parent, int style, GCNISOHandler isoHandler) throws GCNISOException {
		super(parent, style);
		
		textData = new FE9CommonTextLoader(isoHandler);
		charData = new FE9CharacterDataLoader(isoHandler, textData);
		classData = new FE9ClassDataLoader(isoHandler, textData);
		itemData = new FE9ItemDataLoader(isoHandler, textData);
		chapterData = new FE9ChapterDataLoader(isoHandler, textData);
		skillData = new FE9SkillDataLoader(isoHandler, textData);
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		setLayout(mainLayout);
		
		chapterDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		chapterDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		chapterDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setChapter(FE9Data.Chapter.values()[chapterDropdown.getSelectionIndex()]);
			}
		});
		
		for (FE9Data.Chapter chapter : FE9Data.Chapter.values()) {
			if (chapter.getDisplayNameID() != null) {
				chapterDropdown.add(chapter.getDisplayString() + " (" + textData.textStringForIdentifier(chapter.getDisplayNameID()) + ")");
			} else {
				chapterDropdown.add(chapter.getDisplayString());
			}
		}
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		chapterDropdown.setLayoutData(formData);
		
		armyDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		armyDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		armyDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setArmy(chapterData.armiesForChapter(currentChapter).get(armyDropdown.getSelectionIndex()));
			}
		});
		armyDropdown.setEnabled(false);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.top = new FormAttachment(chapterDropdown, 5);
		formData.right = new FormAttachment(100, 0);
		armyDropdown.setLayoutData(formData);
		
		sectionDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		sectionDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		sectionDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setSection(currentArmy.getArmySections().get(sectionDropdown.getSelectionIndex()));
			}
		});
		sectionDropdown.setEnabled(false);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, 20);
		formData.top = new FormAttachment(armyDropdown, 5);
		formData.right = new FormAttachment(100, 0);
		sectionDropdown.setLayoutData(formData);
		
		unitDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		unitDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		unitDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setUnit(currentArmySection.allUnitsInSection().get(unitDropdown.getSelectionIndex()));
			}
		});
		unitDropdown.setEnabled(false);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, 30);
		formData.top = new FormAttachment(sectionDropdown, 5);
		formData.right = new FormAttachment(100, 0);
		unitDropdown.setLayoutData(formData);
		
		infoGroup = new Group(this, SWT.NONE);
		infoGroup.setText("Information");
		
		FormLayout infoLayout = new FormLayout();
		infoLayout.marginWidth = 5;
		infoLayout.marginHeight = 5;
		infoGroup.setLayout(infoLayout);
		
		FormData infoData = new FormData();
		infoData.left = new FormAttachment(0, 0);
		infoData.top = new FormAttachment(unitDropdown, 10);
//		infoData.bottom = new FormAttachment(100, 0);
		infoData.width = 250;
		infoGroup.setLayoutData(infoData);
		
		pid = new LabelFieldView(infoGroup, SWT.NONE);
		pid.setLabel("PID: ");
		pid.setField("(null)");
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		pid.setLayoutData(viewData);
		
		jid = new LabelFieldView(infoGroup, SWT.NONE);
		jid.setLabel("JID: ");
		jid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(pid, 10);
		viewData.right = new FormAttachment(100, 0);
		jid.setLayoutData(viewData);
		
		startingLevel = new LabelFieldView(infoGroup, SWT.NONE);
		startingLevel.setLabel("Level: ");
		startingLevel.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(jid, 10);
		viewData.right = new FormAttachment(100, 0);
		startingLevel.setLayoutData(viewData);
		
		startingPosition = new LabelFieldView(infoGroup, SWT.NONE);
		startingPosition.setLabel("Position: ");
		startingPosition.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(startingLevel, 10);
		viewData.right = new FormAttachment(100, 0);
		startingPosition.setLayoutData(viewData);
		
		inventoryGroup = new Group(this, SWT.NONE);
		inventoryGroup.setText("Inventory");
		
		FormLayout inventoryLayout = new FormLayout();
		inventoryLayout.marginWidth = 5;
		inventoryLayout.marginHeight = 5;
		inventoryGroup.setLayout(inventoryLayout);
		
		FormData inventoryData = new FormData();
		inventoryData.left = new FormAttachment(infoGroup, 10);
		inventoryData.top = new FormAttachment(unitDropdown, 10);
//		infoData.bottom = new FormAttachment(100, 0);
		inventoryData.width = 350;
		inventoryGroup.setLayoutData(inventoryData);
		
		weapon1 = new LabelFieldView(inventoryGroup, SWT.NONE);
		weapon1.setLabel("Weapon 1: ");
		weapon1.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.width = 280;
		weapon1.setLayoutData(viewData);
		
		weapon1Drop = new Button(inventoryGroup, SWT.CHECK);
		weapon1Drop.setText("Drop");
		weapon1Drop.setEnabled(false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(weapon1, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(weapon1, 0, SWT.CENTER);
		weapon1Drop.setLayoutData(viewData);
		
		weapon2 = new LabelFieldView(inventoryGroup, SWT.NONE);
		weapon2.setLabel("Weapon 2: ");
		weapon2.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(weapon1, 10);
		viewData.width = 280;
		weapon2.setLayoutData(viewData);
		
		weapon2Drop = new Button(inventoryGroup, SWT.CHECK);
		weapon2Drop.setText("Drop");
		weapon2Drop.setEnabled(false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(weapon2, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(weapon2, 0, SWT.CENTER);
		weapon2Drop.setLayoutData(viewData);
		
		weapon3 = new LabelFieldView(inventoryGroup, SWT.NONE);
		weapon3.setLabel("Weapon 3: ");
		weapon3.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(weapon2, 10);
		viewData.width = 280;
		weapon3.setLayoutData(viewData);
		
		weapon3Drop = new Button(inventoryGroup, SWT.CHECK);
		weapon3Drop.setText("Drop");
		weapon3Drop.setEnabled(false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(weapon3, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(weapon3, 0, SWT.CENTER);
		weapon3Drop.setLayoutData(viewData);
		
		weapon4 = new LabelFieldView(inventoryGroup, SWT.NONE);
		weapon4.setLabel("Weapon 4: ");
		weapon4.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(weapon3, 10);
		viewData.width = 280;
		weapon4.setLayoutData(viewData);
		
		weapon4Drop = new Button(inventoryGroup, SWT.CHECK);
		weapon4Drop.setText("Drop");
		weapon4Drop.setEnabled(false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(weapon4, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(weapon4, 0, SWT.CENTER);
		weapon4Drop.setLayoutData(viewData);
		
		item1 = new LabelFieldView(inventoryGroup, SWT.NONE);
		item1.setLabel("Item 1: ");
		item1.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(weapon4, 10);
		viewData.width = 280;
		item1.setLayoutData(viewData);
		
		item1Drop = new Button(inventoryGroup, SWT.CHECK);
		item1Drop.setText("Drop");
		item1Drop.setEnabled(false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(item1, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(item1, 0, SWT.CENTER);
		item1Drop.setLayoutData(viewData);
		
		item2 = new LabelFieldView(inventoryGroup, SWT.NONE);
		item2.setLabel("Item 2: ");
		item2.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(item1, 10);
		viewData.width = 280;
		item2.setLayoutData(viewData);
		
		item2Drop = new Button(inventoryGroup, SWT.CHECK);
		item2Drop.setText("Drop");
		item2Drop.setEnabled(false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(item2, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(item2, 0, SWT.CENTER);
		item2Drop.setLayoutData(viewData);
		
		item3 = new LabelFieldView(inventoryGroup, SWT.NONE);
		item3.setLabel("Item 3: ");
		item3.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(item2, 10);
		viewData.width = 280;
		item3.setLayoutData(viewData);
		
		item3Drop = new Button(inventoryGroup, SWT.CHECK);
		item3Drop.setText("Drop");
		item3Drop.setEnabled(false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(item3, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(item3, 0, SWT.CENTER);
		item3Drop.setLayoutData(viewData);
		
		item4 = new LabelFieldView(inventoryGroup, SWT.NONE);
		item4.setLabel("Item 4: ");
		item4.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(item3, 10);
		viewData.width = 280;
		item4.setLayoutData(viewData);
		
		item4Drop = new Button(inventoryGroup, SWT.CHECK);
		item4Drop.setText("Drop");
		item4Drop.setEnabled(false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(item4, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(item4, 0, SWT.CENTER);
		item4Drop.setLayoutData(viewData);
		
		skillStatGroup = new Group(this, SWT.NONE);
		skillStatGroup.setText("Skills and Stat Adjustments");
		
		FormLayout skillStatLayout = new FormLayout();
		skillStatLayout.marginWidth = 5;
		skillStatLayout.marginHeight = 5;
		skillStatGroup.setLayout(skillStatLayout);
		
		FormData skillStatData = new FormData();
		skillStatData.left = new FormAttachment(inventoryGroup, 10);
		skillStatData.top = new FormAttachment(unitDropdown, 10);
		//skillStatData.bottom = new FormAttachment(100, 0);
		skillStatData.width = 250;
		skillStatGroup.setLayoutData(skillStatData);
		
		additionalSkill1 = new LabelFieldView(skillStatGroup, SWT.NONE);
		additionalSkill1.setLabel("Skill 1: ");
		additionalSkill1.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.width = 280;
		additionalSkill1.setLayoutData(viewData);
		
		additionalSkill2 = new LabelFieldView(skillStatGroup, SWT.NONE);
		additionalSkill2.setLabel("Skill 2: ");
		additionalSkill2.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(additionalSkill1, 10);
		viewData.right = new FormAttachment(100, 0);
		additionalSkill2.setLayoutData(viewData);
		
		additionalSkill3 = new LabelFieldView(skillStatGroup, SWT.NONE);
		additionalSkill3.setLabel("Skill 3: ");
		additionalSkill3.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(additionalSkill2, 10);
		viewData.right = new FormAttachment(100, 0);
		additionalSkill3.setLayoutData(viewData);
		
		hpAdjust = new LabelFieldView(skillStatGroup, SWT.NONE);
		hpAdjust.setLabel("HP Adjustment: ");
		hpAdjust.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(additionalSkill3, 10);
		viewData.right = new FormAttachment(100, 0);
		hpAdjust.setLayoutData(viewData);
		
		strAdjust = new LabelFieldView(skillStatGroup, SWT.NONE);
		strAdjust.setLabel("STR Adjustment: ");
		strAdjust.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(hpAdjust, 10);
		viewData.right = new FormAttachment(100, 0);
		strAdjust.setLayoutData(viewData);
		
		magAdjust = new LabelFieldView(skillStatGroup, SWT.NONE);
		magAdjust.setLabel("MAG Adjustment: ");
		magAdjust.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(strAdjust, 10);
		viewData.right = new FormAttachment(100, 0);
		magAdjust.setLayoutData(viewData);
		
		sklAdjust = new LabelFieldView(skillStatGroup, SWT.NONE);
		sklAdjust.setLabel("SKL Adjustment: ");
		sklAdjust.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(magAdjust, 10);
		viewData.right = new FormAttachment(100, 0);
		sklAdjust.setLayoutData(viewData);
		
		spdAdjust = new LabelFieldView(skillStatGroup, SWT.NONE);
		spdAdjust.setLabel("SPD Adjustment: ");
		spdAdjust.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sklAdjust, 10);
		viewData.right = new FormAttachment(100, 0);
		spdAdjust.setLayoutData(viewData);
		
		lckAdjust = new LabelFieldView(skillStatGroup, SWT.NONE);
		lckAdjust.setLabel("LCK Adjustment: ");
		lckAdjust.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(spdAdjust, 10);
		viewData.right = new FormAttachment(100, 0);
		lckAdjust.setLayoutData(viewData);
		
		defAdjust = new LabelFieldView(skillStatGroup, SWT.NONE);
		defAdjust.setLabel("DEF Adjustment: ");
		defAdjust.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(lckAdjust, 10);
		viewData.right = new FormAttachment(100, 0);
		defAdjust.setLayoutData(viewData);
		
		resAdjust = new LabelFieldView(skillStatGroup, SWT.NONE);
		resAdjust.setLabel("RES Adjustment: ");
		resAdjust.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(defAdjust, 10);
		viewData.right = new FormAttachment(100, 0);
		resAdjust.setLayoutData(viewData);
		
		otherGroup = new Group(this, SWT.NONE);
		otherGroup.setText("Other Data");
		
		FormLayout otherLayout = new FormLayout();
		otherLayout.marginWidth = 5;
		otherLayout.marginHeight = 5;
		otherGroup.setLayout(otherLayout);
		
		FormData otherData = new FormData();
		otherData.left = new FormAttachment(skillStatGroup, 10);
		otherData.top = new FormAttachment(unitDropdown, 10);
		//skillStatData.bottom = new FormAttachment(100, 0);
		otherData.width = 250;
		otherGroup.setLayoutData(otherData);
		
		seq1 = new LabelFieldView(otherGroup, SWT.NONE);
		seq1.setLabel("SEQ 1: ");
		seq1.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.width = 280;
		seq1.setLayoutData(viewData);
		
		seq2 = new LabelFieldView(otherGroup, SWT.NONE);
		seq2.setLabel("SEQ 2: ");
		seq2.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(seq1, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.width = 280;
		seq2.setLayoutData(viewData);
		
		seq3 = new LabelFieldView(otherGroup, SWT.NONE);
		seq3.setLabel("SEQ 3: ");
		seq3.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(seq2, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.width = 280;
		seq3.setLayoutData(viewData);
		
		mtype = new LabelFieldView(otherGroup, SWT.NONE);
		mtype.setLabel("MTYPE: ");
		mtype.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(seq3, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.width = 280;
		mtype.setLayoutData(viewData);
	}
	
	public void setChapter(FE9Data.Chapter chapter) {
		if (currentChapter == null && chapter != null) { armyDropdown.setEnabled(true); }
		if (chapter == currentChapter) { return; }
		currentChapter = chapter;
		
		armyDropdown.removeAll();
		currentArmy = null;
		sectionDropdown.removeAll();
		sectionDropdown.setEnabled(false);
		currentArmySection = null;
		unitDropdown.removeAll();
		unitDropdown.setEnabled(false);
		setUnit(null);
		for (FE9ChapterArmy army : chapterData.armiesForChapter(currentChapter)) {
			armyDropdown.add(army.getID());
		}
	}
	
	public void setArmy(FE9ChapterArmy army) {
		if (currentArmy == null && army != null) { sectionDropdown.setEnabled(true); }
		if (army == currentArmy) { return; }
		currentArmy = army;
		
		sectionDropdown.removeAll();
		currentArmySection = null;
		unitDropdown.removeAll();
		unitDropdown.setEnabled(false);
		setUnit(null);
		for (FE9ChapterArmySection section : currentArmy.getArmySections()) {
			sectionDropdown.add("Section " + currentArmy.getArmySections().indexOf(section) + " [" + section.getName() + "]");
		}
	}

	public void setSection(FE9ChapterArmySection armySection) {
		if (currentArmySection == null && armySection != null) { unitDropdown.setEnabled(true); }
		if (armySection == currentArmySection) { return; }
		currentArmySection = armySection;
		
		unitDropdown.removeAll();
		setUnit(null);
		for (FE9ChapterUnit unit : currentArmySection.allUnitsInSection()) {
			unitDropdown.add("Unit " + currentArmySection.allUnitsInSection().indexOf(unit) + " (" + currentArmy.getPIDForUnit(unit) + ")");
		}
	}
	
	public void setUnit(FE9ChapterUnit unit) {
		if (unit == currentUnit) { return; }
		currentUnit = unit;
		
		FE9Character character = unit != null ? charData.characterWithID(currentArmy.getPIDForUnit(unit)) : null;
		FE9Class charClass = unit != null ? classData.classWithID(currentArmy.getJIDForUnit(unit)) : null;
		
		pid.setField(unit != null ? currentArmy.getPIDForUnit(unit) + " (" + charData.getDisplayName(character) + ")" : "(null)");
		jid.setField(unit != null ? currentArmy.getJIDForUnit(unit) + " (" + classData.getDisplayName(charClass) + ")": "(null)");
		startingLevel.setField(unit != null ? Integer.toString(currentArmy.getStartingLevelForUnit(unit)) : "(null)");
		startingPosition.setField(unit != null ? String.format("%d, %d", currentArmy.getStartingXForUnit(unit), currentArmy.getStartingYForUnit(unit)) : "(null)");
		
		FE9Item weapon1Item = unit != null ? itemData.itemWithIID(currentArmy.getWeapon1ForUnit(unit)) : null;
		FE9Item weapon2Item = unit != null ? itemData.itemWithIID(currentArmy.getWeapon2ForUnit(unit)) : null;
		FE9Item weapon3Item = unit != null ? itemData.itemWithIID(currentArmy.getWeapon3ForUnit(unit)) : null;
		FE9Item weapon4Item = unit != null ? itemData.itemWithIID(currentArmy.getWeapon4ForUnit(unit)) : null;
		weapon1.setField((unit != null && weapon1Item != null) ? currentArmy.getWeapon1ForUnit(unit) + " (" + itemData.getDisplayName(weapon1Item) + ")" : "(null)");
		weapon2.setField((unit != null && weapon2Item != null) ? currentArmy.getWeapon2ForUnit(unit) + " (" + itemData.getDisplayName(weapon2Item) + ")" : "(null)");
		weapon3.setField((unit != null && weapon3Item != null) ? currentArmy.getWeapon3ForUnit(unit) + " (" + itemData.getDisplayName(weapon3Item) + ")" : "(null)");
		weapon4.setField((unit != null && weapon4Item != null) ? currentArmy.getWeapon4ForUnit(unit) + " (" + itemData.getDisplayName(weapon4Item) + ")" : "(null)");
		
		weapon1Drop.setSelection(unit != null ? unit.willDropWeapon1() : false);
		weapon2Drop.setSelection(unit != null ? unit.willDropWeapon2() : false);
		weapon3Drop.setSelection(unit != null ? unit.willDropWeapon3() : false);
		weapon4Drop.setSelection(unit != null ? unit.willDropWeapon4() : false);
		
		FE9Item item1Item = unit != null ? itemData.itemWithIID(currentArmy.getItem1ForUnit(unit)) : null;
		FE9Item item2Item = unit != null ? itemData.itemWithIID(currentArmy.getItem2ForUnit(unit)) : null;
		FE9Item item3Item = unit != null ? itemData.itemWithIID(currentArmy.getItem3ForUnit(unit)) : null;
		FE9Item item4Item = unit != null ? itemData.itemWithIID(currentArmy.getItem4ForUnit(unit)) : null;
		item1.setField((unit != null && item1Item != null) ? currentArmy.getItem1ForUnit(unit) + " (" + itemData.getDisplayName(item1Item) + ")" : "(null)");
		item2.setField((unit != null && item2Item != null) ? currentArmy.getItem2ForUnit(unit) + " (" + itemData.getDisplayName(item2Item) + ")" : "(null)");
		item3.setField((unit != null && item3Item != null) ? currentArmy.getItem3ForUnit(unit) + " (" + itemData.getDisplayName(item3Item) + ")" : "(null)");
		item4.setField((unit != null && item4Item != null) ? currentArmy.getItem4ForUnit(unit) + " (" + itemData.getDisplayName(item4Item) + ")" : "(null)");
		
		item1Drop.setSelection(unit != null ? unit.willDropItem1() : false);
		item2Drop.setSelection(unit != null ? unit.willDropItem2() : false);
		item3Drop.setSelection(unit != null ? unit.willDropItem3() : false);
		item4Drop.setSelection(unit != null ? unit.willDropItem4() : false);
		
		FE9Skill skill1 = unit != null ? skillData.getSkillWithSID(currentArmy.getSkill1ForUnit(unit)) : null;
		FE9Skill skill2 = unit != null ? skillData.getSkillWithSID(currentArmy.getSkill2ForUnit(unit)) : null;
		FE9Skill skill3 = unit != null ? skillData.getSkillWithSID(currentArmy.getSkill3ForUnit(unit)) : null;
		additionalSkill1.setField((unit != null && skill1 != null) ? currentArmy.getSkill1ForUnit(unit) + "(" + skillData.displayNameForSkill(skill1) + ")" : "(null)");
		additionalSkill2.setField((unit != null && skill2 != null) ? currentArmy.getSkill2ForUnit(unit) + "(" + skillData.displayNameForSkill(skill2) + ")" : "(null)");
		additionalSkill3.setField((unit != null && skill3 != null) ? currentArmy.getSkill3ForUnit(unit) + "(" + skillData.displayNameForSkill(skill3) + ")" : "(null)");
		
		hpAdjust.setField(unit != null ? Integer.toString(unit.getHPAdjustment()) : "(null)");
		strAdjust.setField(unit != null ? Integer.toString(unit.getSTRAdjustment()) : "(null)");
		magAdjust.setField(unit != null ? Integer.toString(unit.getMAGAdjustment()) : "(null)");
		sklAdjust.setField(unit != null ? Integer.toString(unit.getSKLAdjustment()) : "(null)");
		spdAdjust.setField(unit != null ? Integer.toString(unit.getSPDAdjustment()) : "(null)");
		lckAdjust.setField(unit != null ? Integer.toString(unit.getLCKAdjustment()) : "(null)");
		defAdjust.setField(unit != null ? Integer.toString(unit.getDEFAdjustment()) : "(null)");
		resAdjust.setField(unit != null ? Integer.toString(unit.getRESAdjustment()) : "(null)");
		
		seq1.setField(unit != null ? currentArmy.getSEQ1ForUnit(unit) : "(null)");
		seq2.setField(unit != null ? currentArmy.getSEQ2ForUnit(unit) : "(null)");
		seq3.setField(unit != null ? currentArmy.getSEQ3ForUnit(unit) : "(null)");
		mtype.setField(unit != null ? currentArmy.getMTYPEForUnit(unit) : "(null)");
	}
}
