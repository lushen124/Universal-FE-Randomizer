package ui.gba;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import ui.component.LabelCheckboxView;
import ui.component.LabelFieldView;

public class GBAItemDataView extends Composite {
	
	private FileHandler fileHandler;
	
	private ClassDataLoader classData;
	private ItemDataLoader itemData;
	private TextLoader textData;
	
	private List<GBAFEItemData> itemList;
	
	private Combo itemDropdown;
	
	private Group infoGroup;
	private LabelFieldView idField;
	private LabelFieldView nameTextIDField;
	private LabelFieldView nameValueField;
	private LabelFieldView descriptionTextIDField;
	private Label descriptionLabel;
	
	private Group statsGroup;
	private LabelFieldView mightField;
	private LabelFieldView hitField;
	private LabelFieldView weightField;
	private LabelFieldView critField;
	private LabelFieldView durabilityField;
	private LabelFieldView rangeField;
	private LabelFieldView costPerUseField;
	
	private Group attributeGroup;
	private LabelFieldView rankField;
	private LabelFieldView effectivenessPointerField;
	private org.eclipse.swt.widgets.List effectivenessList;
	private LabelFieldView statBonusPointerField;
	private LabelFieldView statBonusField;
	
	private Group flagsGroup;
	private LabelCheckboxView ability1;
	private LabelCheckboxView ability2;
	private LabelCheckboxView ability3;
	private LabelCheckboxView weaponEffect;
	
	private Composite topComposite;
	private Composite bottomComposite;
	
	public GBAItemDataView(Composite parent, int style, GameType type, FileHandler handler) {
		super(parent, style);
		fileHandler = handler;		
		textData = new TextLoader(type, type.textProvider(), handler);
		itemData = ItemDataLoader.createReadDataLoader(type.itemProvider(), handler);
		classData = new ClassDataLoader(type.classProvider(), handler);
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		setLayout(mainLayout);
		
		itemList = itemData.getItemList();
		
		itemDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		itemDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		itemDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setItem(itemList.get(itemDropdown.getSelectionIndex()));
			}
		});
		
		FormData dropdownData = new FormData();
		dropdownData.left = new FormAttachment(0, 0);
		dropdownData.top = new FormAttachment(0, 0);
		dropdownData.right = new FormAttachment(100, 0);
		itemDropdown.setLayoutData(dropdownData);
		
		topComposite = new Composite(this, SWT.NONE);
		
		FormLayout topLayout = new FormLayout();
		topComposite.setLayout(topLayout);
		
		FormData topData = new FormData();
		topData.top = new FormAttachment(itemDropdown, 10);
		topData.left = new FormAttachment(0, 0);
		topData.right = new FormAttachment(100, 0);
		topComposite.setLayoutData(topData);
		
		bottomComposite = new Composite(this, SWT.NONE);
		
		FormLayout bottomLayout = new FormLayout();
		bottomComposite.setLayout(bottomLayout);
		
		FormData bottomData = new FormData();
		bottomData.top = new FormAttachment(topComposite, 10);
		bottomData.left = new FormAttachment(0, 0);
		bottomData.right = new FormAttachment(100, 0);
		bottomComposite.setLayoutData(bottomData);
		
		infoGroup = new Group(topComposite, SWT.NONE);
		infoGroup.setText("Information");
		
		FormLayout infoLayout = new FormLayout();
		infoLayout.marginWidth = 5;
		infoLayout.marginHeight = 5;
		infoGroup.setLayout(infoLayout);
		
		FormData infoData = new FormData();
		infoData.left = new FormAttachment(0, 0);
		infoData.top = new FormAttachment(0, 0);
		infoData.width = 280;
		infoGroup.setLayoutData(infoData);
		
		idField = new LabelFieldView(infoGroup, SWT.NONE);
		idField.setLabel("Item ID: ");
		idField.setField("(null)");
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		idField.setLayoutData(viewData);
		
		nameTextIDField = new LabelFieldView(infoGroup, SWT.NONE);
		nameTextIDField.setLabel("Name Text ID: ");
		nameTextIDField.setField("0x0000");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(idField, 10);
		viewData.right = new FormAttachment(100, 0);
		nameTextIDField.setLayoutData(viewData);
		
		nameValueField = new LabelFieldView(infoGroup, SWT.NONE);
		nameValueField.setLabel("Name: ");
		nameValueField.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(nameTextIDField, 10);
		viewData.right = new FormAttachment(100, 0);
		nameValueField.setLayoutData(viewData);
		
		descriptionTextIDField = new LabelFieldView(infoGroup, SWT.NONE);
		descriptionTextIDField.setLabel("Description Text ID: ");
		descriptionTextIDField.setField("0x0000");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(nameValueField, 10);
		viewData.right = new FormAttachment(100, 0);
		descriptionTextIDField.setLayoutData(viewData);
		
		descriptionLabel = new Label(infoGroup, SWT.WRAP);
		descriptionLabel.setText("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(descriptionTextIDField, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		descriptionLabel.setLayoutData(viewData);
		
		///////
		
		statsGroup = new Group(topComposite, SWT.NONE);
		statsGroup.setText("Stats");
		
		FormLayout statsLayout = new FormLayout();
		statsLayout.marginWidth = 5;
		statsLayout.marginHeight = 5;
		statsGroup.setLayout(statsLayout);
		
		FormData statsData = new FormData();
		statsData.left = new FormAttachment(infoGroup, 10);
		statsData.top = new FormAttachment(0, 0);
		statsData.width = 220;
		statsGroup.setLayoutData(statsData);
		
		mightField = new LabelFieldView(statsGroup, SWT.NONE);
		mightField.setLabel("Might: ");
		mightField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		mightField.setLayoutData(viewData);
		
		hitField = new LabelFieldView(statsGroup, SWT.NONE);
		hitField.setLabel("Hit: ");
		hitField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(mightField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		hitField.setLayoutData(viewData);
		
		weightField = new LabelFieldView(statsGroup, SWT.NONE);
		weightField.setLabel("Weight: ");
		weightField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(hitField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		weightField.setLayoutData(viewData);
		
		critField = new LabelFieldView(statsGroup, SWT.NONE);
		critField.setLabel("Critical: ");
		critField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(weightField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		critField.setLayoutData(viewData);
		
		durabilityField = new LabelFieldView(statsGroup, SWT.NONE);
		durabilityField.setLabel("Uses/Durability: ");
		durabilityField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(critField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		durabilityField.setLayoutData(viewData);
		
		rangeField = new LabelFieldView(statsGroup, SWT.NONE);
		rangeField.setLabel("Range: ");
		rangeField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(durabilityField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		rangeField.setLayoutData(viewData);
		
		costPerUseField = new LabelFieldView(statsGroup, SWT.NONE);
		costPerUseField.setLabel("Cost Per Use: ");
		costPerUseField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(rangeField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		costPerUseField.setLayoutData(viewData);
		
		///////
		
		attributeGroup = new Group(topComposite, SWT.NONE);
		attributeGroup.setText("Attributes");
		
		FormLayout attrLayout = new FormLayout();
		attrLayout.marginWidth = 5;
		attrLayout.marginHeight = 5;
		attributeGroup.setLayout(attrLayout);
		
		FormData attrData = new FormData();
		attrData.left = new FormAttachment(statsGroup, 10);
		attrData.top = new FormAttachment(0, 0);
		attrData.width = 320;
		attributeGroup.setLayoutData(attrData);
		
		rankField = new LabelFieldView(attributeGroup, SWT.NONE);
		rankField.setLabel("Rank: ");
		rankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		rankField.setLayoutData(viewData);
		
		effectivenessPointerField = new LabelFieldView(attributeGroup, SWT.NONE);
		effectivenessPointerField.setLabel("Effectiveness Pointer: ");
		effectivenessPointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(rankField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		effectivenessPointerField.setLayoutData(viewData);
		
		effectivenessList = new org.eclipse.swt.widgets.List(attributeGroup, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		effectivenessList.setBackground(new org.eclipse.swt.graphics.Color(getDisplay(), 255, 255, 255));
		
		viewData = new FormData();
		viewData.top = new FormAttachment(effectivenessPointerField, 10);
		viewData.left = new FormAttachment(10, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.height = 150;
		effectivenessList.setLayoutData(viewData);
		
		statBonusPointerField = new LabelFieldView(attributeGroup, SWT.NONE);
		statBonusPointerField.setLabel("Stat Bonuses Pointer: ");
		statBonusPointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(effectivenessList, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		statBonusPointerField.setLayoutData(viewData);
		
		statBonusField = new LabelFieldView(attributeGroup, SWT.NONE);
		statBonusField.setLabel("Stat Bonuses: ");
		statBonusField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(statBonusPointerField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		statBonusField.setLayoutData(viewData);
		
		///////
		
		flagsGroup = new Group(bottomComposite, SWT.NONE);
		flagsGroup.setText("Flags");
		
		FormLayout flagsLayout = new FormLayout();
		flagsLayout.marginWidth = 5;
		flagsLayout.marginHeight = 5;
		flagsGroup.setLayout(flagsLayout);
		
		FormData flagsData = new FormData();
		flagsData.left = new FormAttachment(0, 0);
		
		ability1 = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 1", itemData.ability1Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(0, 0);
		ability1.setLayoutData(viewData);
		
		ability2 = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 2", itemData.ability2Flags(), false);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(ability1, 10);
		viewData.top = new FormAttachment(0, 0);
		ability2.setLayoutData(viewData);
		
		boolean hasAbility3 = itemData.ability3Flags().isEmpty() == false;
		
		if (hasAbility3) {
			ability3 = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 3", itemData.ability3Flags(), false);
			
			viewData = new FormData();
			viewData.left = new FormAttachment(ability2, 10);
			viewData.top = new FormAttachment(0, 0);
			ability3.setLayoutData(viewData);
		}
		
		weaponEffect = new LabelCheckboxView(flagsGroup, SWT.NONE, "Weapon Effect", itemData.effectFlags(), true);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(hasAbility3 ? ability3 : ability2, 10);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		weaponEffect.setLayoutData(viewData);
		
		for (GBAFEItemData item : itemList) {
			String displayName = textData.getStringAtIndex(item.getNameIndex(), true);
			itemDropdown.add("[0x" + Integer.toHexString(item.getID()).toUpperCase() + "] " + (displayName != null ? displayName : "???"));
		}
	}

	private void setItem(GBAFEItemData item) {
		idField.setField("0x" + Integer.toHexString(item.getID()).toUpperCase());
		nameTextIDField.setField("0x" + Integer.toHexString(item.getNameIndex()).toUpperCase());
		nameValueField.setField(textData.getStringAtIndex(item.getNameIndex(), true));
		descriptionTextIDField.setField("0x" + Integer.toHexString(item.getDescriptionIndex()).toUpperCase());
		descriptionLabel.setText(textData.getStringAtIndex(item.getDescriptionIndex(), true));
		
		mightField.setField(Integer.toString(item.getMight()));
		hitField.setField(Integer.toString(item.getHit()));
		weightField.setField(Integer.toString(item.getWeight()));
		critField.setField(Integer.toString(item.getCritical()));
		durabilityField.setField(Integer.toString(item.getDurability()));
		rangeField.setField(Integer.toString(item.getMinRange()) + " ~ " + Integer.toString(item.getMaxRange()));
		costPerUseField.setField(Integer.toString(item.getCostPerUse()));
		
		rankField.setField(item.getWeaponRank().displayString());
		effectivenessPointerField.setField("0x" + Long.toHexString(item.getEffectivenessPointer()).toUpperCase());
		
		effectivenessList.removeAll();
		long targetPointer = item.getEffectivenessPointer();
		if (targetPointer != 0) {
			long address = targetPointer - 0x08000000;
			fileHandler.setNextReadOffset(address);
			byte[] classIDs = fileHandler.continueReadingBytesUpToNextTerminator(address + 255);
			for (int i = 0; i < classIDs.length; i++) {
				int classID = (int)(classIDs[i] & 0xFF);
				if (classID == 0) { continue; }
				GBAFEClassData charClass = classData.classForID(classID);
				if (charClass != null) {
					effectivenessList.add("[0x" + Integer.toHexString(classID) + "] " + textData.getStringAtIndex(charClass.getNameIndex(), true));
				} else {
					effectivenessList.add("[0x" + Integer.toHexString(classID) + "] ???");
				}
			}
		}
		
		statBonusPointerField.setField("0x" + Long.toHexString(item.getStatBonusPointer()).toUpperCase());
		targetPointer = item.getStatBonusPointer();
		if (targetPointer != 0) {
			long address = targetPointer - 0x08000000;
			byte[] boosts = fileHandler.readBytesAtOffset(address, 9);
			List<String> components = new ArrayList<String>();
			if (boosts[0] != 0) { components.add("+" + (int)boosts[0] + " HP"); }
			if (boosts[1] != 0) { components.add("+" + (int)boosts[1] + (itemData.isMagic(item) ? " MAG" : " STR")); }
			if (boosts[2] != 0) { components.add("+" + (int)boosts[2] + " SKL"); }
			if (boosts[3] != 0) { components.add("+" + (int)boosts[3] + " SPD"); }
			if (boosts[4] != 0) { components.add("+" + (int)boosts[4] + " DEF"); }
			if (boosts[5] != 0) { components.add("+" + (int)boosts[5] + " RES"); }
			if (boosts[6] != 0) { components.add("+" + (int)boosts[6] + " LCK"); }
			if (boosts[7] != 0) { components.add("+" + (int)boosts[7] + " MOV"); }
			if (boosts[8] != 0) { components.add("+" + (int)boosts[8] + " CON"); }
			statBonusField.setField(String.join(", ", components));
		} else {
			statBonusField.setField("");
		}
		
		ability1.setCheckboxes(itemData.ability1Flags().stream().filter(displayString -> itemData.itemHasFlagByDisplayString(displayString, item)).collect(Collectors.toList()));
		ability2.setCheckboxes(itemData.ability2Flags().stream().filter(displayString -> itemData.itemHasFlagByDisplayString(displayString, item)).collect(Collectors.toList()));
		if (ability3 != null) {
			ability3.setCheckboxes(itemData.ability3Flags().stream().filter(displayString -> itemData.itemHasFlagByDisplayString(displayString, item)).collect(Collectors.toList()));
		}
		weaponEffect.setCheckboxes(itemData.effectFlags().stream().filter(displayString -> itemData.itemHasFlagByDisplayString(displayString, item)).collect(Collectors.toList()));
		
		layout();
	}
}
