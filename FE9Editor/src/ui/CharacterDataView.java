package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
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

public class CharacterDataView extends Composite {
	
	private GCNISOHandler handler;
	
	private FE9CharacterDataLoader characterData;
	private FE9ClassDataLoader classData;
	private FE9SkillDataLoader skillData;
	private FE9CommonTextLoader textData;
	
	private Combo characterDropdown;
	
	private Group infoGroup;
	private LabelFieldView pid;
	private LabelFieldView mpid;
	private LabelFieldView fid;
	private LabelFieldView jid;
	private LabelFieldView unpromotedAid;
	private LabelFieldView promotedAid;
	private LabelFieldView laguzStartingGauge;
	
	private Group statsGroup;
	private LabelFieldView affinity;
	private LabelFieldView weaponLevels;
	private LabelFieldView sid1;
	private LabelFieldView sid2;
	private LabelFieldView sid3;
	private LabelFieldView level;
	private LabelFieldView build;
	private LabelFieldView weight;
	
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
	
	private Group unknownGroup;
	private LabelFieldView x30x33;
	private LabelFieldView x35;
	private LabelFieldView x49x53;

	public CharacterDataView(Composite parent, int style, GCNISOHandler isoHandler) throws GCNISOException {
		super(parent, style);
		handler = isoHandler;
		textData = new FE9CommonTextLoader(handler);
		characterData = new FE9CharacterDataLoader(isoHandler, textData);
		classData = new FE9ClassDataLoader(handler, textData);
		skillData = new FE9SkillDataLoader(handler, textData);
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		setLayout(mainLayout);
		
		characterDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		characterDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		characterDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setCharacter(characterData.allCharacters().get(characterDropdown.getSelectionIndex()));
			}
		});
		
		FormData dropdownData = new FormData();
		dropdownData.left = new FormAttachment(0, 0);
		dropdownData.top = new FormAttachment(0, 0);
		dropdownData.right = new FormAttachment(100, 0);
		characterDropdown.setLayoutData(dropdownData);
		
		infoGroup = new Group(this, SWT.NONE);
		infoGroup.setText("Information");
		
		FormLayout infoLayout = new FormLayout();
		infoLayout.marginWidth = 5;
		infoLayout.marginHeight = 5;
		infoGroup.setLayout(infoLayout);
		
		FormData infoData = new FormData();
		infoData.left = new FormAttachment(0, 0);
		infoData.top = new FormAttachment(characterDropdown, 10);
//		infoData.bottom = new FormAttachment(100, 0);
		infoData.width = 300;
		infoGroup.setLayoutData(infoData);
		
		pid = new LabelFieldView(infoGroup, SWT.NONE);
		pid.setLabel("PID: ");
		pid.setField("(null)");
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		pid.setLayoutData(viewData);
		
		mpid = new LabelFieldView(infoGroup, SWT.NONE);
		mpid.setLabel("MPID: ");
		mpid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(pid, 10);
		viewData.right = new FormAttachment(100, 0);
		mpid.setLayoutData(viewData);
		
		fid = new LabelFieldView(infoGroup, SWT.NONE);
		fid.setLabel("FID: ");
		fid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(mpid, 10);
		viewData.right = new FormAttachment(100, 0);
		fid.setLayoutData(viewData);
		
		jid = new LabelFieldView(infoGroup, SWT.NONE);
		jid.setLabel("JID: ");
		jid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(fid, 10);
		viewData.right = new FormAttachment(100, 0);
		jid.setLayoutData(viewData);
		
		unpromotedAid = new LabelFieldView(infoGroup, SWT.NONE);
		unpromotedAid.setLabel("AID (Base): ");
		unpromotedAid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(jid, 10);
		viewData.right = new FormAttachment(100, 0);
		unpromotedAid.setLayoutData(viewData);
		
		promotedAid = new LabelFieldView(infoGroup, SWT.NONE);
		promotedAid.setLabel("AID (Promoted): ");
		promotedAid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(unpromotedAid, 10);
		viewData.right = new FormAttachment(100, 0);
		promotedAid.setLayoutData(viewData);
		
		laguzStartingGauge = new LabelFieldView(infoGroup, SWT.NONE);
		laguzStartingGauge.setLabel("Starting gauge (Laguz): ");
		laguzStartingGauge.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(promotedAid, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		laguzStartingGauge.setLayoutData(viewData);
		
		statsGroup = new Group(this, SWT.NONE);
		statsGroup.setText("Statistics");
		
		FormLayout statLayout = new FormLayout();
		statLayout.marginWidth = 5;
		statLayout.marginHeight = 5;
		statsGroup.setLayout(statLayout);
		
		FormData statData = new FormData();
		statData.left = new FormAttachment(infoGroup, 10);
		statData.top = new FormAttachment(characterDropdown, 10);
		//statData.bottom = new FormAttachment(100, 0);
		statData.width = 250;
		statsGroup.setLayoutData(statData);
		
		affinity = new LabelFieldView(statsGroup, SWT.NONE);
		affinity.setLabel("Affinity: ");
		affinity.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		affinity.setLayoutData(viewData);
		
		weaponLevels = new LabelFieldView(statsGroup, SWT.NONE);
		weaponLevels.setLabel("Weapon Levels: ");
		weaponLevels.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(affinity, 10);
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
		
		level = new LabelFieldView(statsGroup, SWT.NONE);
		level.setLabel("Level: ");
		level.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sid3, 10);
		viewData.right = new FormAttachment(100, 0);
		level.setLayoutData(viewData);
		
		build = new LabelFieldView(statsGroup, SWT.NONE);
		build.setLabel("Build (offset): ");
		build.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(level, 10);
		viewData.right = new FormAttachment(100, 0);
		build.setLayoutData(viewData);
		
		weight = new LabelFieldView(statsGroup, SWT.NONE);
		weight.setLabel("Weight (offset): ");
		weight.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(build, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		weight.setLayoutData(viewData);
		
		growthGroup = new Group(this, SWT.NONE);
		growthGroup.setText("Growths");
		
		FormLayout growthLayout = new FormLayout();
		growthLayout.marginWidth = 5;
		growthLayout.marginHeight = 5;
		growthGroup.setLayout(growthLayout);
		
		FormData growthData = new FormData();
		growthData.left = new FormAttachment(statsGroup, 10);
		growthData.top = new FormAttachment(characterDropdown, 10);
		//growthData.bottom = new FormAttachment(100, 0);
		growthData.width = 150;
		growthGroup.setLayoutData(growthData);
		
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
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(hpGrowth, 10);
		strGrowth.setLayoutData(viewData);
		
		magGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		magGrowth.setLabel("MAG %: ");
		magGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(strGrowth, 10);
		magGrowth.setLayoutData(viewData);
		
		sklGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		sklGrowth.setLabel("SKL %: ");
		sklGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(magGrowth, 10);
		sklGrowth.setLayoutData(viewData);
		
		spdGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		spdGrowth.setLabel("SPD %: ");
		spdGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(sklGrowth, 10);
		spdGrowth.setLayoutData(viewData);
		
		lckGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		lckGrowth.setLabel("LCK %: ");
		lckGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(spdGrowth, 10);
		lckGrowth.setLayoutData(viewData);
		
		defGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		defGrowth.setLabel("DEF %: ");
		defGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(lckGrowth, 10);
		defGrowth.setLayoutData(viewData);
		
		resGrowth = new LabelFieldView(growthGroup, SWT.NONE);
		resGrowth.setLabel("RES %: ");
		resGrowth.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(defGrowth, 10);
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
		basesData.top = new FormAttachment(characterDropdown, 10);
		//basesData.bottom = new FormAttachment(100, 0);
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
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(hpBase, 10);
		strBase.setLayoutData(viewData);
		
		magBase = new LabelFieldView(basesGroup, SWT.NONE);
		magBase.setLabel("MAG: ");
		magBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(strBase, 10);
		magBase.setLayoutData(viewData);
		
		sklBase = new LabelFieldView(basesGroup, SWT.NONE);
		sklBase.setLabel("SKL: ");
		sklBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(magBase, 10);
		sklBase.setLayoutData(viewData);
		
		spdBase = new LabelFieldView(basesGroup, SWT.NONE);
		spdBase.setLabel("SPD: ");
		spdBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(sklBase, 10);
		spdBase.setLayoutData(viewData);
		
		lckBase = new LabelFieldView(basesGroup, SWT.NONE);
		lckBase.setLabel("LCK: ");
		lckBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(spdBase, 10);
		lckBase.setLayoutData(viewData);
		
		defBase = new LabelFieldView(basesGroup, SWT.NONE);
		defBase.setLabel("DEF: ");
		defBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(lckBase, 10);
		defBase.setLayoutData(viewData);
		
		resBase = new LabelFieldView(basesGroup, SWT.NONE);
		resBase.setLabel("RES: ");
		resBase.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(defBase, 10);
		viewData.bottom = new FormAttachment(100, 0);
		resBase.setLayoutData(viewData);
		
		unknownGroup = new Group(this, SWT.NONE);
		unknownGroup.setText("Unknown");
		
		FormLayout unknownLayout = new FormLayout();
		unknownLayout.marginWidth = 5;
		unknownLayout.marginHeight = 5;
		unknownGroup.setLayout(unknownLayout);
		
		FormData unknownData = new FormData();
		unknownData.left = new FormAttachment(basesGroup, 10);
		unknownData.top = new FormAttachment(characterDropdown, 10);
		unknownData.width = 260;
		unknownData.right = new FormAttachment(100, 0);
		unknownGroup.setLayoutData(unknownData);
		
		x30x33 = new LabelFieldView(unknownGroup, SWT.NONE);
		x30x33.setLabel("0x33 ~ 0x33");
		x30x33.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(0, 0);
		x30x33.setLayoutData(viewData);
		
		x35 = new LabelFieldView(unknownGroup, SWT.NONE);
		x35.setLabel("0x35");
		x35.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(x30x33, 10);
		x35.setLayoutData(viewData);
		
		x49x53 = new LabelFieldView(unknownGroup, SWT.NONE);
		x49x53.setLabel("0x49 ~ 0x53");
		x49x53.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.top = new FormAttachment(x35, 10);
		viewData.bottom = new FormAttachment(100, 0);
		x49x53.setLayoutData(viewData);
		
		for (FE9Character character : characterData.allCharacters()) {
			characterDropdown.add(characterData.getPIDForCharacter(character) + " (" + characterData.getDisplayName(character) + ")");
		}
	}
	
	private void setCharacter(FE9Character character) {
		FE9Class characterClass = classData.classWithID(characterData.getJIDForCharacter(character));
		pid.setField(characterData.getPIDForCharacter(character));
		mpid.setField(characterData.getMPIDForCharacter(character) + " (" + textData.textStringForIdentifier(characterData.getMPIDForCharacter(character)) + ")");
		fid.setField(characterData.getFIDForCharacter(character));
		jid.setField(characterData.getJIDForCharacter(character) + " (" + textData.textStringForIdentifier(classData.getMJIDForClass(characterClass)) + ")");
		unpromotedAid.setField(characterData.getUnpromotedAIDForCharacter(character));
		promotedAid.setField(characterData.getPromotedAIDForCharacter(character));
		laguzStartingGauge.setField(Integer.toString(characterData.getLaguzStartingGaugeForCharacter(character)));
		
		FE9Data.Affinity affinityValue = characterData.getAffinityForCharacter(character);
		affinity.setField(affinityValue != null ? affinityValue.getInternalID() : "(null)");
		weaponLevels.setField(characterData.getWeaponLevelStringForCharacter(character));
		FE9Skill skill1 = skillData.getSkillWithSID(characterData.getSID1ForCharacter(character));
		FE9Skill skill2 = skillData.getSkillWithSID(characterData.getSID2ForCharacter(character));
		FE9Skill skill3 = skillData.getSkillWithSID(characterData.getSID3ForCharacter(character));
		sid1.setField(skill1 != null ? skillData.getSID(skill1) + " (" + skillData.displayNameForSkill(skill1) + ")" : "(null)");
		sid2.setField(skill2 != null ? skillData.getSID(skill2) + " (" + skillData.displayNameForSkill(skill2) + ")" : "(null)");
		sid3.setField(skill3 != null ? skillData.getSID(skill3) + " (" + skillData.displayNameForSkill(skill3) + ")" : "(null)");
		level.setField(Integer.toString(characterData.getLevelForCharacter(character)));
		build.setField(Integer.toString(characterData.getBuildForCharacter(character)) + " (+" + characterClass.getBaseCON() + ")");
		weight.setField(Integer.toString(characterData.getWeightForCharacter(character)) + " (+" + characterClass.getBaseWeight() + ")");
		
		hpGrowth.setField(character.getHPGrowth() + "%");
		strGrowth.setField(character.getSTRGrowth() + "%");
		magGrowth.setField(character.getMAGGrowth() + "%");
		sklGrowth.setField(character.getSKLGrowth() + "%");
		spdGrowth.setField(character.getSPDGrowth() + "%");
		lckGrowth.setField(character.getLCKGrowth() + "%");
		defGrowth.setField(character.getDEFGrowth() + "%");
		resGrowth.setField(character.getRESGrowth() + "%");
		
		hpBase.setField(Integer.toString(character.getBaseHP()) + " (+" + characterClass.getBaseHP() + ")");
		strBase.setField(Integer.toString(character.getBaseSTR()) + " (+" + characterClass.getBaseSTR() + ")");
		magBase.setField(Integer.toString(character.getBaseMAG()) + " (+" + characterClass.getBaseMAG() + ")");
		sklBase.setField(Integer.toString(character.getBaseSKL()) + " (+" + characterClass.getBaseSKL() + ")");
		spdBase.setField(Integer.toString(character.getBaseSPD()) + " (+" + characterClass.getBaseSPD() + ")");
		lckBase.setField(Integer.toString(character.getBaseLCK()) + " (+" + characterClass.getBaseLCK() + ")");
		defBase.setField(Integer.toString(character.getBaseDEF()) + " (+" + characterClass.getBaseDEF() + ")");
		resBase.setField(Integer.toString(character.getBaseRES()) + " (+" + characterClass.getBaseRES() + ")");
		
		x30x33.setField(WhyDoesJavaNotHaveThese.displayStringForBytes(WhyDoesJavaNotHaveThese.subArray(character.getUnknown6Bytes(), 0, 4)));
		x35.setField(WhyDoesJavaNotHaveThese.displayStringForBytes(WhyDoesJavaNotHaveThese.subArray(character.getUnknown6Bytes(), 5, 1)));
		x49x53.setField(WhyDoesJavaNotHaveThese.displayStringForBytes(character.getUnknown13Bytes()));
		
	}
}
