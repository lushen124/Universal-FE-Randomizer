package ui.gba;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponRanks;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.TextLoader;
import ui.component.LabelCheckboxView;
import ui.component.LabelFieldView;

public class GBACharacterDataView extends Composite {

	private CharacterDataLoader charData;
	private ClassDataLoader classData;
	private TextLoader textData;
	
	private List<GBAFECharacterData> charList;
	
	private Combo characterDropdown;
	
	private Group infoGroup;
	private LabelFieldView idField;
	private LabelFieldView nameIndexField;
	private LabelFieldView nameField;
	private LabelFieldView descriptionIndexField;
	private Label descriptionLabel;
	private LabelFieldView classField;
	private LabelFieldView affinity;
	
	private Group statsGroup;
	private LabelFieldView hpField;
	private LabelFieldView powField;
	private LabelFieldView sklField;
	private LabelFieldView spdField;
	private LabelFieldView lckField;
	private LabelFieldView defField;
	private LabelFieldView resField;
	private LabelFieldView conField;
	
	private Group weaponsGroup;
	private LabelFieldView swordField;
	private LabelFieldView lanceField;
	private LabelFieldView axeField;
	private LabelFieldView bowField;
	private LabelFieldView animaField;
	private LabelFieldView lightField;
	private LabelFieldView darkField;
	private LabelFieldView staffField;
	
	private Group growthGroup;
	private LabelFieldView hpGrowthField;
	private LabelFieldView powGrowthField;
	private LabelFieldView sklGrowthField;
	private LabelFieldView spdGrowthField;
	private LabelFieldView lckGrowthField;
	private LabelFieldView defGrowthField;
	private LabelFieldView resGrowthField;
	
	private Group flagsGroup;
	private LabelCheckboxView ability1;
	private LabelCheckboxView ability2;
	private LabelCheckboxView ability3;
	private LabelCheckboxView ability4;
	
	private Group pointersGroup;
	private LabelFieldView supportsPointerField;
	private LabelFieldView unpromotedPaletteIDField;
	private LabelFieldView promotedPaletteIDField;
	private LabelFieldView unpromotedCustomSprite;
	private LabelFieldView promotedCustomSprite;
	
	private Composite topComposite;
	private Composite bottomComposite;
	
	private GameType type;
	
	public GBACharacterDataView(Composite parent, int style, GameType type, FileHandler handler) {
		super(parent, style);
		
		this.type = type;
		charData = CharacterDataLoader.createReadOnlyLoader(type.charProvider(), handler);
		classData = ClassDataLoader.createReadOnlyClassDataLoader(type.classProvider(), handler);
		textData = new TextLoader(type, type.textProvider(), handler);
		
		charList = charData.characterList();
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		setLayout(mainLayout);
		
		characterDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		characterDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		characterDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setCharacter(charList.get(characterDropdown.getSelectionIndex()));
			}
		});
		
		FormData dropdownData = new FormData();
		dropdownData.left = new FormAttachment(0, 0);
		dropdownData.top = new FormAttachment(0, 0);
		dropdownData.right = new FormAttachment(100, 0);
		characterDropdown.setLayoutData(dropdownData);
		
		topComposite = new Composite(this, SWT.NONE);
		FormLayout topLayout = new FormLayout();
		topComposite.setLayout(topLayout);
		
		FormData topData = new FormData();
		topData.left = new FormAttachment(0, 0);
		topData.top = new FormAttachment(characterDropdown, 10);
		topData.right = new FormAttachment(100, 0);
		topComposite.setLayoutData(topData);
		
		bottomComposite = new Composite(this, SWT.NONE);
		FormLayout bottomLayout = new FormLayout();
		bottomComposite.setLayout(bottomLayout);
		
		FormData bottomData = new FormData();
		bottomData.left = new FormAttachment(0, 0);
		bottomData.top = new FormAttachment(topComposite, 10);
		bottomData.right = new FormAttachment(100, 0);
		bottomComposite.setLayoutData(bottomData);
		
		////////////
		
		infoGroup = new Group(topComposite, SWT.NONE);
		infoGroup.setText("Information");
		
		FormLayout infoLayout = new FormLayout();
		infoLayout.marginWidth = 5;
		infoLayout.marginHeight = 5;
		infoGroup.setLayout(infoLayout);
		
		FormData infoData = new FormData();
		infoData.top = new FormAttachment(0, 0);
		infoData.left = new FormAttachment(0, 0);
		infoData.width = 250;
		infoGroup.setLayoutData(infoData);
		
		idField = new LabelFieldView(infoGroup, SWT.NONE);
		idField.setLabel("Character ID: ");
		idField.setField("");
		
		FormData viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		idField.setLayoutData(viewData);
		
		nameIndexField = new LabelFieldView(infoGroup, SWT.NONE);
		nameIndexField.setLabel("Name Index: ");
		nameIndexField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(idField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		nameIndexField.setLayoutData(viewData);
		
		nameField = new LabelFieldView(infoGroup, SWT.NONE);
		nameField.setLabel("Name: ");
		nameField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(nameIndexField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		nameField.setLayoutData(viewData);
		
		descriptionIndexField = new LabelFieldView(infoGroup, SWT.NONE);
		descriptionIndexField.setLabel("Description Index: ");
		descriptionIndexField.setField("");
		attachFullWidthCompositeBelowSibling(descriptionIndexField, nameField, false);
		
		descriptionLabel = new Label(infoGroup, SWT.NONE);
		attachFullWidthCompositeBelowSibling(descriptionLabel, descriptionIndexField, false);
		
		classField = new LabelFieldView(infoGroup, SWT.NONE);
		classField.setLabel("Class: ");
		classField.setField("");
		attachFullWidthCompositeBelowSibling(classField, descriptionLabel, false);
		
		affinity = new LabelFieldView(infoGroup, SWT.NONE);
		affinity.setLabel("Affinity: ");
		affinity.setField("");
		attachFullWidthCompositeBelowSibling(affinity, classField, true);
		
		////////////
		
		statsGroup = new Group(topComposite, SWT.NONE);
		statsGroup.setText("Base Stats");
		
		FormLayout statLayout = new FormLayout();
		statLayout.marginWidth = 5;
		statLayout.marginHeight = 5;
		statsGroup.setLayout(statLayout);
		
		FormData statsData = new FormData();
		statsData.top = new FormAttachment(0, 0);
		statsData.left = new FormAttachment(infoGroup, 10);
		statsData.width = 200;
		statsGroup.setLayoutData(statsData);
		
		hpField = createLabelFieldView(statsGroup, "Base HP: ", null, false);
		powField = createLabelFieldView(statsGroup, "Base STR: ", hpField, false);
		sklField = createLabelFieldView(statsGroup, "Base SKL: ", powField, false);
		spdField = createLabelFieldView(statsGroup, "Base SPD: ", sklField, false);
		lckField = createLabelFieldView(statsGroup, "Base LCK: ", spdField, false);
		defField = createLabelFieldView(statsGroup, "Base DEF: ", lckField, false);
		resField = createLabelFieldView(statsGroup, "Base RES: ", defField, false);
		conField = createLabelFieldView(statsGroup, "Base CON: ", resField, true);
		
		////////////
		
		growthGroup = new Group(topComposite, SWT.NONE);
		growthGroup.setText("Growth Rates");
		
		FormLayout growthLayout = new FormLayout();
		growthLayout.marginWidth = 5;
		growthLayout.marginHeight = 5;
		growthGroup.setLayout(growthLayout);
		
		FormData growthData = new FormData();
		growthData.top = new FormAttachment(0, 0);
		growthData.left = new FormAttachment(statsGroup, 10);
		growthData.width = 200;
		growthGroup.setLayoutData(growthData);
		
		hpGrowthField = createLabelFieldView(growthGroup, "HP Growth: ", null, false);
		powGrowthField = createLabelFieldView(growthGroup, "STR Growth: ", hpGrowthField, false);
		sklGrowthField = createLabelFieldView(growthGroup, "SKL Growth: ", powGrowthField, false);
		spdGrowthField = createLabelFieldView(growthGroup, "SPD Growth: ", sklGrowthField, false);
		lckGrowthField = createLabelFieldView(growthGroup, "LCK Growth: ", spdGrowthField, false);
		defGrowthField = createLabelFieldView(growthGroup, "DEF Growth: ", lckGrowthField, false);
		resGrowthField = createLabelFieldView(growthGroup, "RES Growth: ", defGrowthField, true);
		
		////////////
		
		weaponsGroup = createGroup(topComposite, "Weapon Ranks", 200, growthGroup, false);
		swordField = createLabelFieldView(weaponsGroup, "Sword: ", null, false);
		lanceField = createLabelFieldView(weaponsGroup, "Lance: ", swordField, false);
		axeField = createLabelFieldView(weaponsGroup, "Axe: ", lanceField, false);
		bowField = createLabelFieldView(weaponsGroup, "Bow: ", axeField, false);
		animaField = createLabelFieldView(weaponsGroup, "Anima: ", bowField, false);
		lightField = createLabelFieldView(weaponsGroup, "Light: ", animaField, false);
		darkField = createLabelFieldView(weaponsGroup, "Dark: ", lightField, false);
		staffField = createLabelFieldView(weaponsGroup, "Staff: ", darkField, true);
		
		////////////
		
		pointersGroup = createGroup(topComposite, "Pointers and Misc.", 250, weaponsGroup, true);
		supportsPointerField = createLabelFieldView(pointersGroup, "Supports Data: ", null, false);
		unpromotedPaletteIDField = createLabelFieldView(pointersGroup, "Unpromoted Palette ID: ", supportsPointerField, false);
		promotedPaletteIDField = createLabelFieldView(pointersGroup, "Promoted Palette ID: ", unpromotedPaletteIDField, type != GameType.FE7);
		
		if (type == GameType.FE7) {
			unpromotedCustomSprite = createLabelFieldView(pointersGroup, "Unpromoted Custom Sprite: ", promotedPaletteIDField, false);
			promotedCustomSprite = createLabelFieldView(pointersGroup, "Promoted Custom Sprite: ", unpromotedCustomSprite, true);
		}
		
		////////////
		
		flagsGroup = new Group(bottomComposite, SWT.NONE);
		flagsGroup.setText("Flags");
		
		FormLayout flagsLayout = new FormLayout();
		flagsLayout.marginWidth = 5;
		flagsLayout.marginHeight = 5;
		flagsGroup.setLayout(flagsLayout);
		
		FormData flagsData = new FormData();
		flagsData.top = new FormAttachment(0, 0);
		flagsData.left = new FormAttachment(0, 0);
		flagsData.right = new FormAttachment(100, 0);
		flagsData.bottom = new FormAttachment(100, 0);
		flagsGroup.setLayoutData(flagsData);
		
		ability1 = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 1", classData.ability1Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(0, 0);
		ability1.setLayoutData(viewData);
		
		ability2 = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 2", classData.ability2Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(ability1, 10);
		ability2.setLayoutData(viewData);
		
		ability3 = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 3", classData.ability3Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(ability2, 10);
		ability3.setLayoutData(viewData);
		
		ability4 = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 4", classData.ability4Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(ability3, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		ability4.setLayoutData(viewData);
		
		for (GBAFECharacterData character : charList) {
			String characterName = textData.getStringAtIndex(character.getNameIndex(), true);
			String displayString = "[0x" + Integer.toHexString(character.getID()).toUpperCase() + "] " + (characterName != null ? characterName : "???");
			characterDropdown.add(displayString);
		}
	}
	
	private void setCharacter(GBAFECharacterData character) {
		idField.setField("0x" + Integer.toHexString(character.getID()).toUpperCase());
		nameIndexField.setField("0x" + Integer.toHexString(character.getNameIndex()).toUpperCase());
		nameField.setField(textData.getStringAtIndex(character.getNameIndex(), true));
		descriptionIndexField.setField("0x" + Integer.toHexString(character.getDescriptionIndex()).toUpperCase());
		descriptionLabel.setText(textData.getStringAtIndex(character.getDescriptionIndex(), true));
		GBAFEClassData charClass = classData.classForID(character.getClassID());
		if (charClass != null) {
			String className = textData.getStringAtIndex(charClass.getNameIndex(), true);
			classField.setField("[0x" + Integer.toHexString(character.getClassID()).toUpperCase() + "] " + (className != null ? className : "???"));
		} else {
			classField.setField("0x" + Integer.toHexString(character.getClassID()).toUpperCase());
		}
		affinity.setField(character.getAffinityName());
		
		WeaponRanks charRanks = character.getWeaponRanks(type);
		boolean isPhysical = charRanks.swordRank != WeaponRank.NONE || charRanks.lanceRank != WeaponRank.NONE || charRanks.axeRank != WeaponRank.NONE || charRanks.bowRank != WeaponRank.NONE;
		
		hpField.setField(Integer.toString(character.getBaseHP()) + " (+" + (charClass != null ? Integer.toString(charClass.getBaseHP()) : "?") + ")");
		powField.setLabel(isPhysical ? "Base STR: " : "Base MAG: ");
		powField.setField(Integer.toString(character.getBaseSTR()) + " (+" + (charClass != null ? Integer.toString(charClass.getBaseSTR()) : "?") + ")");
		sklField.setField(Integer.toString(character.getBaseSKL()) + " (+" + (charClass != null ? Integer.toString(charClass.getBaseSKL()) : "?") + ")");
		spdField.setField(Integer.toString(character.getBaseSPD()) + " (+" + (charClass != null ? Integer.toString(charClass.getBaseSPD()) : "?") + ")");
		lckField.setField(Integer.toString(character.getBaseLCK()) + " (+" + (charClass != null ? Integer.toString(charClass.getBaseLCK()) : "?") + ")");
		defField.setField(Integer.toString(character.getBaseDEF()) + " (+" + (charClass != null ? Integer.toString(charClass.getBaseDEF()) : "?") + ")");
		resField.setField(Integer.toString(character.getBaseRES()) + " (+" + (charClass != null ? Integer.toString(charClass.getBaseRES()) : "?") + ")");
		conField.setField(Integer.toString(character.getConstitution()) + " (+" + (charClass != null ? Integer.toString(charClass.getCON()) : "?") + ")");
		
		hpGrowthField.setField(Integer.toString(character.getHPGrowth()) + "%");
		powGrowthField.setLabel(isPhysical ? "STR Growth: " : "MAG Growth: ");
		powGrowthField.setField(Integer.toString(character.getSTRGrowth()) + "%");
		sklGrowthField.setField(Integer.toString(character.getSKLGrowth()) + "%");
		spdGrowthField.setField(Integer.toString(character.getSPDGrowth()) + "%");
		lckGrowthField.setField(Integer.toString(character.getLCKGrowth()) + "%");
		defGrowthField.setField(Integer.toString(character.getDEFGrowth()) + "%");
		resGrowthField.setField(Integer.toString(character.getRESGrowth()) + "%");
		
		WeaponRanks classRanks = null;
		if (charClass != null) {
			classRanks = charClass.getWeaponRanks(true, type);	
		}
		
		populateRankField(swordField, charRanks.swordRank, classRanks != null ? classRanks.swordRank : WeaponRank.NONE, character.getSwordRank());
		populateRankField(lanceField, charRanks.lanceRank, classRanks != null ? classRanks.lanceRank : WeaponRank.NONE, character.getLanceRank());
		populateRankField(axeField, charRanks.axeRank, classRanks != null ? classRanks.axeRank : WeaponRank.NONE, character.getAxeRank());
		populateRankField(bowField, charRanks.bowRank, classRanks != null ? classRanks.bowRank : WeaponRank.NONE, character.getBowRank());
		populateRankField(animaField, charRanks.animaRank, classRanks != null ? classRanks.animaRank : WeaponRank.NONE, character.getAnimaRank());
		populateRankField(lightField, charRanks.lightRank, classRanks != null ? classRanks.lightRank : WeaponRank.NONE, character.getLightRank());
		populateRankField(darkField, charRanks.darkRank, classRanks != null ? classRanks.darkRank : WeaponRank.NONE, character.getDarkRank());
		populateRankField(staffField, charRanks.staffRank, classRanks != null ? classRanks.staffRank : WeaponRank.NONE, character.getStaffRank());
		
		supportsPointerField.setField("0x" + Long.toHexString(character.getSupportsDataPointer()).toUpperCase());
		unpromotedPaletteIDField.setField("0x" + Integer.toHexString(character.getUnpromotedPaletteIndex()).toUpperCase());
		promotedPaletteIDField.setField("0x" + Integer.toHexString(character.getPromotedPaletteIndex()).toUpperCase());
		
		if (type == GameType.FE7) {
			unpromotedCustomSprite.setField("0x" + Integer.toHexString(character.getCustomUnpromotedBattleSprite()).toUpperCase());
			promotedCustomSprite.setField("0x" + Integer.toHexString(character.getCustomPromotedBattleSprite()).toUpperCase());
		}
		
		ability1.setCheckboxes(classData.ability1Flags().stream().filter(displayString -> charData.characterHasFlagsByDisplayString(displayString, character)).collect(Collectors.toList()));
		ability2.setCheckboxes(classData.ability2Flags().stream().filter(displayString -> charData.characterHasFlagsByDisplayString(displayString, character)).collect(Collectors.toList()));
		ability3.setCheckboxes(classData.ability3Flags().stream().filter(displayString -> charData.characterHasFlagsByDisplayString(displayString, character)).collect(Collectors.toList()));
		ability4.setCheckboxes(classData.ability4Flags().stream().filter(displayString -> charData.characterHasFlagsByDisplayString(displayString, character)).collect(Collectors.toList()));
	}
	
	private void populateRankField(LabelFieldView view, WeaponRank charRank, WeaponRank classRank, int rankValue) {
		int rankValueOffset = WeaponRank.offsetFromRoundedRank(rankValue, type);
		if (charRank != WeaponRank.NONE) {
			if (rankValueOffset > 0) {
				view.setField(charRank.displayString() + " (+" + rankValueOffset + ")");	
			} else {
				view.setField(charRank.displayString());
			}
		} else if (classRank != WeaponRank.NONE) {
			view.setField("[" + classRank.displayString() + "]");
		} else {
			if (rankValueOffset > 0) {
				WeaponRank roundedRankValue = WeaponRank.roundToFullRank(rankValue, type);
				view.setField("<" + roundedRankValue.displayString() + "> (" + rankValue + ")");
			} else {
				view.setField("");
			}
		}
	}
	
	private Group createGroup(Composite parent, String title, int width, Control previousSibling, boolean isLast) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(title);
		
		FormLayout layout = new FormLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		group.setLayout(layout);
		
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		if (previousSibling != null) {
			data.left = new FormAttachment(previousSibling, 10);
		} else {
			data.left = new FormAttachment(0, 0);
		}
		
		if (isLast) {
			data.right = new FormAttachment(100, 0);
		}
		data.width = width;
		group.setLayoutData(data);
		
		return group;
	}
	
	private LabelFieldView createLabelFieldView(Composite parent, String label, Control previousSibling, boolean isLast) {
		LabelFieldView view = new LabelFieldView(parent, SWT.NONE);
		view.setLabel(label);
		view.setField("");
		
		attachFullWidthCompositeBelowSibling(view, previousSibling, isLast);
		return view;
	}
	
	private void attachFullWidthCompositeBelowSibling(Control composite, Control sibling, boolean isLast) {
		FormData data = new FormData();
		if (sibling == null) {
			data.top = new FormAttachment(0, 0);
		} else {
			data.top = new FormAttachment(sibling, 10);
		}
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		if (isLast) {
			data.bottom = new FormAttachment(100, 0);
		}
		composite.setLayoutData(data);
	}
}
