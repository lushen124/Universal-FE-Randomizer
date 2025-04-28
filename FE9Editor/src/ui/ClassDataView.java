package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Skill;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9CommonTextLoader;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;
import ui.component.LabelFieldView;
import util.WhyDoesJavaNotHaveThese;

public class ClassDataView extends Composite {

	private GCNISOHandler handler;
	private FE9ClassDataLoader classData;
	private FE9SkillDataLoader skillData;
	private FE9CommonTextLoader textData;
	
	private Combo classDropdown;
	
	private Group infoGroup;
	private LabelFieldView jid;
	private LabelFieldView mjid;
	private LabelFieldView mhj;
	private Label descriptionLabel;
	private LabelFieldView promotedJid;
	private LabelFieldView defaultIid;
	
	private Group statsGroup;
	private LabelFieldView weaponLevels;
	private LabelFieldView sid1;
	private LabelFieldView sid2;
	private LabelFieldView sid3;
	private LabelFieldView race;
	private LabelFieldView trait;
	private LabelFieldView build;
	private LabelFieldView weight;
	private LabelFieldView movementRange;
	// Unknown byte here...
	private LabelFieldView unknownByte;
	private LabelFieldView skillCapacity;
	
	private Group growthGroup;
	private LabelFieldView hpGrowth;
	private LabelFieldView strGrowth;
	private LabelFieldView magGrowth;
	private LabelFieldView sklGrowth;
	private LabelFieldView spdGrowth;
	private LabelFieldView lckGrowth;
	private LabelFieldView defGrowth;
	private LabelFieldView resGrowth;
	
	private Group basesGroup;
	private LabelFieldView hpBase;
	private LabelFieldView strBase;
	private LabelFieldView magBase;
	private LabelFieldView sklBase;
	private LabelFieldView spdBase;
	private LabelFieldView lckBase;
	private LabelFieldView defBase;
	private LabelFieldView resBase;
	
	private Group maxGroup;
	private LabelFieldView hpMax;
	private LabelFieldView strMax;
	private LabelFieldView magMax;
	private LabelFieldView sklMax;
	private LabelFieldView spdMax;
	private LabelFieldView lckMax;
	private LabelFieldView defMax;
	private LabelFieldView resMax;
	
	private Group unknownGroup;
	private LabelFieldView x41x43;
	private LabelFieldView x5Cx63;
	
	public ClassDataView(Composite parent, int style, GCNISOHandler isoHandler) throws GCNISOException {
		super(parent, style);
		handler = isoHandler;
		textData = new FE9CommonTextLoader(handler);
		classData = new FE9ClassDataLoader(handler, textData);
		skillData = new FE9SkillDataLoader(handler, textData);
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		setLayout(mainLayout);
		
		classDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		classDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		classDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setClass(classData.allClasses().get(classDropdown.getSelectionIndex()));
			}
		});
		
		FormData dropdownData = new FormData();
		dropdownData.left = new FormAttachment(0, 0);
		dropdownData.top = new FormAttachment(0, 0);
		dropdownData.right = new FormAttachment(100, 0);
		classDropdown.setLayoutData(dropdownData);
		
		infoGroup = new Group(this, SWT.NONE);
		infoGroup.setText("Information");
		
		FormLayout infoLayout = new FormLayout();
		infoLayout.marginWidth = 5;
		infoLayout.marginHeight = 5;
		infoGroup.setLayout(infoLayout);
		
		FormData infoData = new FormData();
		infoData.left = new FormAttachment(0, 0);
		infoData.top = new FormAttachment(classDropdown, 10);
//		infoData.bottom = new FormAttachment(100, 0);
		infoData.width = 320;
		infoGroup.setLayoutData(infoData);
		
		jid = new LabelFieldView(infoGroup, SWT.NONE);
		jid.setLabel("JID: ");
		jid.setField("(null)");
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		jid.setLayoutData(viewData);
		
		mjid = new LabelFieldView(infoGroup, SWT.NONE);
		mjid.setLabel("MJID: ");
		mjid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(jid, 10);
		viewData.right = new FormAttachment(100, 0);
		mjid.setLayoutData(viewData);
		
		mhj = new LabelFieldView(infoGroup, SWT.NONE);
		mhj.setLabel("MH_J: ");
		mhj.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(mjid, 10);
		viewData.right = new FormAttachment(100, 0);
		mhj.setLayoutData(viewData);
		
		descriptionLabel = new Label(infoGroup, SWT.WRAP);
		descriptionLabel.setText("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(mhj, 10);
		viewData.right = new FormAttachment(100, 0);
		descriptionLabel.setLayoutData(viewData);
		
		promotedJid = new LabelFieldView(infoGroup, SWT.NONE);
		promotedJid.setLabel("Promoted JID: ");
		promotedJid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(descriptionLabel, 10);
		viewData.right = new FormAttachment(100, 0);
		promotedJid.setLayoutData(viewData);
		
		defaultIid = new LabelFieldView(infoGroup, SWT.NONE);
		defaultIid.setLabel("Default IID: ");
		defaultIid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(promotedJid, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, -5);
		defaultIid.setLayoutData(viewData);
		
		statsGroup = new Group(this, SWT.NONE);
		statsGroup.setText("Statistics");
		
		FormLayout statsLayout = new FormLayout();
		statsLayout.marginWidth = 5;
		statsLayout.marginHeight = 5;
		statsGroup.setLayout(statsLayout);
		
		FormData statsData = new FormData();
		statsData.left = new FormAttachment(infoGroup, 10);
		statsData.top = new FormAttachment(classDropdown, 10);
		statsData.bottom = new FormAttachment(100, 0);
		statsData.width = 280;
		statsGroup.setLayoutData(statsData);
		
		weaponLevels = new LabelFieldView(statsGroup, SWT.NONE);
		weaponLevels.setLabel("Weapon Levels: ");
		weaponLevels.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		weaponLevels.setLayoutData(viewData);
		
		sid1 = new LabelFieldView(statsGroup, SWT.NONE);
		sid1.setLabel("Skill 1: ");
		sid1.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(weaponLevels, 10);
		viewData.right = new FormAttachment(100, 0);
		sid1.setLayoutData(viewData);
		
		sid2 = new LabelFieldView(statsGroup, SWT.NONE);
		sid2.setLabel("Skill 2: ");
		sid2.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sid1, 10);
		viewData.right = new FormAttachment(100, 0);
		sid2.setLayoutData(viewData);
		
		sid3 = new LabelFieldView(statsGroup, SWT.NONE);
		sid3.setLabel("Skill 3: ");
		sid3.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sid2, 10);
		viewData.right = new FormAttachment(100, 0);
		sid3.setLayoutData(viewData);
		
		race = new LabelFieldView(statsGroup, SWT.NONE);
		race.setLabel("Race: ");
		race.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sid3, 10);
		viewData.right = new FormAttachment(100, 0);
		race.setLayoutData(viewData);
		
		trait = new LabelFieldView(statsGroup, SWT.NONE);
		trait.setLabel("Trait: ");
		trait.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(race, 10);
		viewData.right = new FormAttachment(100, 0);
		trait.setLayoutData(viewData);
		
		build = new LabelFieldView(statsGroup, SWT.NONE);
		build.setLabel("Build: ");
		build.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(trait, 10);
		viewData.right = new FormAttachment(100, 0);
		build.setLayoutData(viewData);
		
		weight = new LabelFieldView(statsGroup, SWT.NONE);
		weight.setLabel("Weight: ");
		weight.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(build, 10);
		viewData.right = new FormAttachment(100, 0);
		weight.setLayoutData(viewData);
		
		movementRange = new LabelFieldView(statsGroup, SWT.NONE);
		movementRange.setLabel("Movement Range: ");
		movementRange.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(weight, 10);
		viewData.right = new FormAttachment(100, 0);
		movementRange.setLayoutData(viewData);
		
		unknownByte = new LabelFieldView(statsGroup, SWT.NONE);
		unknownByte.setLabel("Unknown: ");
		unknownByte.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(movementRange, 10);
		viewData.right = new FormAttachment(100, 0);
		unknownByte.setLayoutData(viewData);
		
		skillCapacity = new LabelFieldView(statsGroup, SWT.NONE);
		skillCapacity.setLabel("Skill Capacity: ");
		skillCapacity.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(unknownByte, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		skillCapacity.setLayoutData(viewData);
		
		growthGroup = new Group(this, SWT.NONE);
		growthGroup.setText("Growths");
		
		FormLayout growthsLayout = new FormLayout();
		growthsLayout.marginWidth = 5;
		growthsLayout.marginHeight = 5;
		growthGroup.setLayout(growthsLayout);
		
		FormData growthsData = new FormData();
		growthsData.left = new FormAttachment(statsGroup, 10);
		growthsData.top = new FormAttachment(classDropdown, 10);
		growthsData.width = 150;
		growthGroup.setLayoutData(growthsData);
		
		hpGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		hpGrowth.setLabel("HP %: ");
		hpGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		hpGrowth.setLayoutData(viewData);
		
		strGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		strGrowth.setLabel("STR %: ");
		strGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(hpGrowth, 10);
		viewData.right = new FormAttachment(100, 0);
		strGrowth.setLayoutData(viewData);
		
		magGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		magGrowth.setLabel("MAG %: ");
		magGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(strGrowth, 10);
		viewData.right = new FormAttachment(100, 0);
		magGrowth.setLayoutData(viewData);
		
		sklGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		sklGrowth.setLabel("SKL %: ");
		sklGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(magGrowth, 10);
		viewData.right = new FormAttachment(100, 0);
		sklGrowth.setLayoutData(viewData);
		
		spdGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		spdGrowth.setLabel("SPD %: ");
		spdGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sklGrowth, 10);
		viewData.right = new FormAttachment(100, 0);
		spdGrowth.setLayoutData(viewData);
		
		lckGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		lckGrowth.setLabel("LCK %: ");
		lckGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(spdGrowth, 10);
		viewData.right = new FormAttachment(100, 0);
		lckGrowth.setLayoutData(viewData);
		
		defGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		defGrowth.setLabel("DEF %: ");
		defGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(lckGrowth, 10);
		viewData.right = new FormAttachment(100, 0);
		defGrowth.setLayoutData(viewData);
		
		resGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		resGrowth.setLabel("RES %: ");
		resGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(defGrowth, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		resGrowth.setLayoutData(viewData);
		
		basesGroup = new Group(this, SWT.NONE);
		basesGroup.setText("Bases");
		
		FormLayout basesLayout = new FormLayout();
		basesLayout.marginWidth = 5;
		basesLayout.marginHeight = 5;
		basesGroup.setLayout(basesLayout);
		
		FormData basesData = new FormData();
		basesData.left = new FormAttachment(growthGroup, 10);
		basesData.top = new FormAttachment(classDropdown, 10);
		basesData.width = 150;
		basesGroup.setLayoutData(basesData);
		
		hpBase = new LabelFieldView(basesGroup, SWT.NONE);
		hpBase.setLabel("HP: ");
		hpBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		hpBase.setLayoutData(viewData);
		
		strBase = new LabelFieldView(basesGroup, SWT.NONE);
		strBase.setLabel("STR: ");
		strBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(hpBase, 10);
		viewData.right = new FormAttachment(100, 0);
		strBase.setLayoutData(viewData);
		
		magBase = new LabelFieldView(basesGroup, SWT.NONE);
		magBase.setLabel("MAG: ");
		magBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(strBase, 10);
		viewData.right = new FormAttachment(100, 0);
		magBase.setLayoutData(viewData);
		
		sklBase = new LabelFieldView(basesGroup, SWT.NONE);
		sklBase.setLabel("SKL: ");
		sklBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(magBase, 10);
		viewData.right = new FormAttachment(100, 0);
		sklBase.setLayoutData(viewData);
		
		spdBase = new LabelFieldView(basesGroup, SWT.NONE);
		spdBase.setLabel("SPD: ");
		spdBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sklBase, 10);
		viewData.right = new FormAttachment(100, 0);
		spdBase.setLayoutData(viewData);
		
		lckBase = new LabelFieldView(basesGroup, SWT.NONE);
		lckBase.setLabel("LCK: ");
		lckBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(spdBase, 10);
		viewData.right = new FormAttachment(100, 0);
		lckBase.setLayoutData(viewData);
		
		defBase = new LabelFieldView(basesGroup, SWT.NONE);
		defBase.setLabel("DEF: ");
		defBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(lckBase, 10);
		viewData.right = new FormAttachment(100, 0);
		defBase.setLayoutData(viewData);
		
		resBase = new LabelFieldView(basesGroup, SWT.NONE);
		resBase.setLabel("RES: ");
		resBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(defBase, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		resBase.setLayoutData(viewData);
		
		maxGroup = new Group(this, SWT.NONE);
		maxGroup.setText("Caps");
		
		FormLayout maxLayout = new FormLayout();
		maxLayout.marginWidth = 5;
		maxLayout.marginHeight = 5;
		maxGroup.setLayout(maxLayout);
		
		FormData maxData = new FormData();
		maxData.left = new FormAttachment(basesGroup, 10);
		maxData.top = new FormAttachment(classDropdown, 10);
		maxData.width = 150;
		maxGroup.setLayoutData(maxData);
		
		hpMax = new LabelFieldView(maxGroup, SWT.NONE);
		hpMax.setLabel("HP: ");
		hpMax.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		hpMax.setLayoutData(viewData);
		
		strMax = new LabelFieldView(maxGroup, SWT.NONE);
		strMax.setLabel("STR: ");
		strMax.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(hpMax, 10);
		viewData.right = new FormAttachment(100, 0);
		strMax.setLayoutData(viewData);
		
		magMax = new LabelFieldView(maxGroup, SWT.NONE);
		magMax.setLabel("MAG: ");
		magMax.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(strMax, 10);
		viewData.right = new FormAttachment(100, 0);
		magMax.setLayoutData(viewData);
		
		sklMax = new LabelFieldView(maxGroup, SWT.NONE);
		sklMax.setLabel("SKL: ");
		sklMax.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(magMax, 10);
		viewData.right = new FormAttachment(100, 0);
		sklMax.setLayoutData(viewData);
		
		spdMax = new LabelFieldView(maxGroup, SWT.NONE);
		spdMax.setLabel("SPD: ");
		spdMax.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sklMax, 10);
		viewData.right = new FormAttachment(100, 0);
		spdMax.setLayoutData(viewData);
		
		lckMax = new LabelFieldView(maxGroup, SWT.NONE);
		lckMax.setLabel("LCK: ");
		lckMax.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(spdMax, 10);
		viewData.right = new FormAttachment(100, 0);
		lckMax.setLayoutData(viewData);
		
		defMax = new LabelFieldView(maxGroup, SWT.NONE);
		defMax.setLabel("DEF: ");
		defMax.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(lckMax, 10);
		viewData.right = new FormAttachment(100, 0);
		defMax.setLayoutData(viewData);
		
		resMax = new LabelFieldView(maxGroup, SWT.NONE);
		resMax.setLabel("RES: ");
		resMax.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(defMax, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		resMax.setLayoutData(viewData);
		
		unknownGroup = new Group(this, SWT.NONE);
		unknownGroup.setText("Unknown");
		
		FormLayout unknownLayout = new FormLayout();
		unknownLayout.marginWidth = 5;
		unknownLayout.marginHeight = 5;
		unknownGroup.setLayout(unknownLayout);
		
		FormData unknownData = new FormData();
		unknownData.left = new FormAttachment(maxGroup, 10);
		unknownData.top = new FormAttachment(classDropdown, 10);
		unknownData.width = 230;
		unknownGroup.setLayoutData(unknownData);
		
		x41x43 = new LabelFieldView(unknownGroup, SWT.NONE);
		x41x43.setLabel("0x41 ~ 0x43: ");
		x41x43.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		x41x43.setLayoutData(viewData);
		
		x5Cx63 = new LabelFieldView(unknownGroup, SWT.NONE);
		x5Cx63.setLabel("0x5C ~ 0x63: ");
		x5Cx63.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(x41x43, 10);
		viewData.right = new FormAttachment(100, 0);
		x5Cx63.setLayoutData(viewData);
		
		for (FE9Class charClass : classData.allClasses()) {
			classDropdown.add(classData.getJIDForClass(charClass) + " (" + classData.getDisplayName(charClass) + ")");
		}
	}
	
	private void setClass(FE9Class charClass) {
		jid.setField(classData.getJIDForClass(charClass));
		mjid.setField(classData.getMJIDForClass(charClass) + " (" + textData.textStringForIdentifier(classData.getMJIDForClass(charClass)) + ")");
		mhj.setField(classData.getMHJForClass(charClass));
		String descriptionString = textData.textStringForIdentifier(classData.getMHJForClass(charClass));
		descriptionLabel.setText(descriptionString != null ? descriptionString : "No description.");
		promotedJid.setField(classData.getJIDForClass(classData.getPromotedClass(charClass)));
		defaultIid.setField(classData.getDefaultIIDForClass(charClass));
		
		weaponLevels.setField(classData.getWeaponLevelsForClass(charClass));
		FE9Skill skill1 = skillData.getSkillWithSID(classData.getSID1ForClass(charClass));
		FE9Skill skill2 = skillData.getSkillWithSID(classData.getSID2ForClass(charClass));
		FE9Skill skill3 = skillData.getSkillWithSID(classData.getSID3ForClass(charClass));
		sid1.setField(skill1 != null ? skillData.getSID(skill1) + " (" + skillData.displayNameForSkill(skill1) + ")" : "(null)");
		sid2.setField(skill2 != null ? skillData.getSID(skill2) + " (" + skillData.displayNameForSkill(skill2) + ")" : "(null)");
		sid3.setField(skill3 != null ? skillData.getSID(skill3) + " (" + skillData.displayNameForSkill(skill3) + ")" : "(null)");
		race.setField(classData.getRaceForClass(charClass));
		trait.setField(classData.getTraitForClass(charClass));
		build.setField(Integer.toString(charClass.getBaseCON()));
		weight.setField(Integer.toString(charClass.getBaseWeight()));
		movementRange.setField(Integer.toString(charClass.getMovementRange()));
		unknownByte.setField("0x" + Integer.toHexString(charClass.getUnknownByte()));
		skillCapacity.setField(Integer.toString(charClass.getSkillCapacity()));
		
		hpGrowth.setField(charClass.getHPGrowth() + "%");
		strGrowth.setField(charClass.getSTRGrowth() + "%");
		magGrowth.setField(charClass.getMAGGrowth() + "%");
		sklGrowth.setField(charClass.getSKLGrowth() + "%");
		spdGrowth.setField(charClass.getSPDGrowth() + "%");
		lckGrowth.setField(charClass.getLCKGrowth() + "%");
		defGrowth.setField(charClass.getDEFGrowth() + "%");
		resGrowth.setField(charClass.getRESGrowth() + "%");
		
		hpBase.setField(Integer.toString(charClass.getBaseHP()));
		strBase.setField(Integer.toString(charClass.getBaseSTR()));
		magBase.setField(Integer.toString(charClass.getBaseMAG()));
		sklBase.setField(Integer.toString(charClass.getBaseSKL()));
		spdBase.setField(Integer.toString(charClass.getBaseSPD()));
		lckBase.setField(Integer.toString(charClass.getBaseLCK()));
		defBase.setField(Integer.toString(charClass.getBaseDEF()));
		resBase.setField(Integer.toString(charClass.getBaseRES()));
		
		hpMax.setField(Integer.toString(charClass.getMaxHP()));
		strMax.setField(Integer.toString(charClass.getMaxSTR()));
		magMax.setField(Integer.toString(charClass.getMaxMAG()));
		sklMax.setField(Integer.toString(charClass.getMaxSKL()));
		spdMax.setField(Integer.toString(charClass.getMaxSPD()));
		lckMax.setField(Integer.toString(charClass.getMaxLCK()));
		defMax.setField(Integer.toString(charClass.getMaxDEF()));
		resMax.setField(Integer.toString(charClass.getMaxRES()));
		
		x41x43.setField(WhyDoesJavaNotHaveThese.displayStringForBytes(charClass.getUnknown3Bytes()));
		x5Cx63.setField(WhyDoesJavaNotHaveThese.displayStringForBytes(charClass.getLaguzData()));
		
		layout();
	}
}
