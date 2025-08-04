package ui.gba;

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
import fedata.gba.general.WeaponRank;
import fedata.gba.general.WeaponRanks;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.TextLoader;
import ui.component.LabelCheckboxView;
import ui.component.LabelFieldView;

public class GBAClassDataView extends Composite {
	
	private FileHandler fileHandler;
	private ClassDataLoader classData;
	private TextLoader textData;
	
	private List<GBAFEClassData> classList;

	private Combo classDropdown;
	
	private Group infoGroup;
	private LabelFieldView idField;
	private LabelFieldView nameIndexField;
	private LabelFieldView nameField;
	private LabelFieldView descriptionIndexField;
	private Label descriptionLabel;
	
	private Group growthsGroup;
	private LabelFieldView hpGrowthField;
	private LabelFieldView powGrowthField;
	private LabelFieldView sklGrowthField;
	private LabelFieldView spdGrowthField;
	private LabelFieldView defGrowthField;
	private LabelFieldView resGrowthField;
	private LabelFieldView lckGrowthField;
	
	private Group basesGroup;
	private LabelFieldView hpBaseField;
	private LabelFieldView powBaseField;
	private LabelFieldView sklBaseField;
	private LabelFieldView spdBaseField;
	private LabelFieldView defBaseField;
	private LabelFieldView resBaseField;
	private LabelFieldView conBaseField;
	
	private Group capsGroup;
	private LabelFieldView hpCapField;
	private LabelFieldView powCapField;
	private LabelFieldView sklCapField;
	private LabelFieldView spdCapField;
	private LabelFieldView defCapField;
	private LabelFieldView resCapField;
	private LabelFieldView lckCapField;
	
	private Group ranksGroup;
	private LabelFieldView swordRankField;
	private LabelFieldView lanceRankField;
	private LabelFieldView axeRankField;
	private LabelFieldView bowRankField;
	private LabelFieldView animaRankField;
	private LabelFieldView lightRankField;
	private LabelFieldView darkRankField;
	private LabelFieldView staffRankField;
	
	private Group promoGroup;
	private LabelFieldView hpBonusField;
	private LabelFieldView powBonusField;
	private LabelFieldView sklBonusField;
	private LabelFieldView spdBonusField;
	private LabelFieldView defBonusField;
	private LabelFieldView resBonusField;
	private LabelFieldView conBonusField;
	
	private Group miscGroup;
	private LabelFieldView movementRangeField;
	private LabelFieldView spriteIndexField;
	private LabelFieldView targetPromotionField;
	private LabelFieldView walkingSpeedField;
	private LabelFieldView battleAnimationPointerField;
	private LabelFieldView movementTypePointerField;
	private LabelFieldView rainMovementPointerField;
	private LabelFieldView snowMovementPointerField;
	private LabelFieldView terrainAvoidPointerField;
	private LabelFieldView terrainDefensePointerField;
	private LabelFieldView terrainResistancePointerField;
	private LabelFieldView terrainUnknownPointerField;
	
	private Group flagsGroup;
	private LabelCheckboxView ability1Flags;
	private LabelCheckboxView ability2Flags;
	private LabelCheckboxView ability3Flags;
	private LabelCheckboxView ability4Flags;
	
	private Composite topGroup;
	private Composite middleGroup;
	private Composite bottomGroup;
	
	private GameType gameType;
	
	public GBAClassDataView(Composite parent, int style, GameType type, FileHandler handler) {
		super(parent, style);
		fileHandler = handler;		
		textData = new TextLoader(type, type.textProvider(), handler);
		classData = ClassDataLoader.createReadOnlyClassDataLoader(type.classProvider(), handler);
		
		gameType = type;
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		setLayout(mainLayout);
		
		classList = classData.getParsedClassList();
		
		classDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		classDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		classDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setClass(classList.get(classDropdown.getSelectionIndex()));
			}
		});
		
		FormData dropdownData = new FormData();
		dropdownData.left = new FormAttachment(0, 0);
		dropdownData.top = new FormAttachment(0, 0);
		dropdownData.right = new FormAttachment(100, 0);
		classDropdown.setLayoutData(dropdownData);
		
		topGroup = new Composite(this, SWT.NONE);
		FormLayout topLayout = new FormLayout();
		topGroup.setLayout(topLayout);
		
		FormData topData = new FormData();
		topData.left = new FormAttachment(0, 0);
		topData.top = new FormAttachment(classDropdown, 10);
		topData.right = new FormAttachment(100, 0);
		topGroup.setLayoutData(topData);
		
		middleGroup = new Composite(this, SWT.NONE);
		FormLayout middleLayout = new FormLayout();
		middleGroup.setLayout(middleLayout);
		
		FormData middleData = new FormData();
		middleData.left = new FormAttachment(0, 0);
		middleData.right = new FormAttachment(100, 0);
		middleData.top = new FormAttachment(topGroup, 10);
		middleGroup.setLayoutData(middleData);
		
		bottomGroup = new Composite(this, SWT.NONE);
		FormLayout bottomLayout = new FormLayout();
		bottomGroup.setLayout(bottomLayout);
		
		FormData bottomData = new FormData();
		bottomData.left = new FormAttachment(0, 0);
		bottomData.right = new FormAttachment(100, 0);
		bottomData.top = new FormAttachment(middleGroup, 10);
		bottomGroup.setLayoutData(bottomData);
		
		/////////
		
		infoGroup = new Group(topGroup, SWT.NONE);
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
		idField.setLabel("Class ID: ");
		idField.setField("(null)");
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		idField.setLayoutData(viewData);
		
		nameIndexField = new LabelFieldView(infoGroup, SWT.NONE);
		nameIndexField.setLabel("Name Text ID: ");
		nameIndexField.setField("0x0000");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(idField, 10);
		viewData.right = new FormAttachment(100, 0);
		nameIndexField.setLayoutData(viewData);
		
		nameField = new LabelFieldView(infoGroup, SWT.NONE);
		nameField.setLabel("Name: ");
		nameField.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(nameIndexField, 10);
		viewData.right = new FormAttachment(100, 0);
		nameField.setLayoutData(viewData);
		
		descriptionIndexField = new LabelFieldView(infoGroup, SWT.NONE);
		descriptionIndexField.setLabel("Description Text ID: ");
		descriptionIndexField.setField("0x0000");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(nameField, 10);
		viewData.right = new FormAttachment(100, 0);
		descriptionIndexField.setLayoutData(viewData);
		
		descriptionLabel = new Label(infoGroup, SWT.WRAP);
		descriptionLabel.setText("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(descriptionIndexField, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		descriptionLabel.setLayoutData(viewData);
		
		//////////
		
		ranksGroup = new Group(topGroup, SWT.NONE);
		ranksGroup.setText("Weapon Ranks");
		
		FormLayout rankLayout = new FormLayout();
		rankLayout.marginWidth = 5;
		rankLayout.marginHeight = 5;
		ranksGroup.setLayout(rankLayout);
		
		FormData rankData = new FormData();
		rankData.left = new FormAttachment(infoGroup, 10);
		rankData.top = new FormAttachment(0, 0);
		rankData.width = 180;
		ranksGroup.setLayoutData(rankData);
		
		swordRankField = new LabelFieldView(ranksGroup, SWT.NONE);
		swordRankField.setLabel("Sword Rank: ");
		swordRankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		swordRankField.setLayoutData(viewData);
		
		lanceRankField = new LabelFieldView(ranksGroup, SWT.NONE);
		lanceRankField.setLabel("Lance Rank: ");
		lanceRankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(swordRankField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		lanceRankField.setLayoutData(viewData);
		
		axeRankField = new LabelFieldView(ranksGroup, SWT.NONE);
		axeRankField.setLabel("Axe Rank: ");
		axeRankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(lanceRankField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		axeRankField.setLayoutData(viewData);
		
		bowRankField = new LabelFieldView(ranksGroup, SWT.NONE);
		bowRankField.setLabel("Bow Rank: ");
		bowRankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(axeRankField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		bowRankField.setLayoutData(viewData);
		
		animaRankField = new LabelFieldView(ranksGroup, SWT.NONE);
		animaRankField.setLabel("Anima Rank: ");
		animaRankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(bowRankField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		animaRankField.setLayoutData(viewData);
		
		lightRankField = new LabelFieldView(ranksGroup, SWT.NONE);
		lightRankField.setLabel("Light Rank: ");
		lightRankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(animaRankField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		lightRankField.setLayoutData(viewData);
		
		darkRankField = new LabelFieldView(ranksGroup, SWT.NONE);
		darkRankField.setLabel("Dark Rank: ");
		darkRankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(lightRankField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		darkRankField.setLayoutData(viewData);
		
		staffRankField = new LabelFieldView(ranksGroup, SWT.NONE);
		staffRankField.setLabel("Staff Rank: ");
		staffRankField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(darkRankField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		staffRankField.setLayoutData(viewData);
		
		//////////
		
		miscGroup = new Group(topGroup, SWT.NONE);
		miscGroup.setText("Pointers and Other Data");
		
		FormLayout miscLayout = new FormLayout();
		miscLayout.marginWidth = 5;
		miscLayout.marginHeight = 5;
		miscGroup.setLayout(miscLayout);
		
		FormData miscData = new FormData();
		miscData.left = new FormAttachment(ranksGroup, 10);
		miscData.top = new FormAttachment(0, 0);
		miscData.right = new FormAttachment(100, 0);
		miscGroup.setLayoutData(miscData);
		
		movementRangeField = new LabelFieldView(miscGroup, SWT.NONE);
		movementRangeField.setLabel("Movement Range: ");
		movementRangeField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		movementRangeField.setLayoutData(viewData);
		
		spriteIndexField = new LabelFieldView(miscGroup, SWT.NONE);
		spriteIndexField.setLabel("Sprite Index: ");
		spriteIndexField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(movementRangeField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		spriteIndexField.setLayoutData(viewData);
		
		targetPromotionField = new LabelFieldView(miscGroup, SWT.NONE);
		targetPromotionField.setLabel("Promotion Class: ");
		targetPromotionField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(spriteIndexField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		targetPromotionField.setLayoutData(viewData);
		
		walkingSpeedField = new LabelFieldView(miscGroup, SWT.NONE);
		walkingSpeedField.setLabel("Walking Speed: ");
		walkingSpeedField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(targetPromotionField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		walkingSpeedField.setLayoutData(viewData);
		
		battleAnimationPointerField = new LabelFieldView(miscGroup, SWT.NONE);
		battleAnimationPointerField.setLabel("Battle Animation Pointer: ");
		battleAnimationPointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(walkingSpeedField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		battleAnimationPointerField.setLayoutData(viewData);
		
		movementTypePointerField = new LabelFieldView(miscGroup, SWT.NONE);
		movementTypePointerField.setLabel("Movement Type Pointer: ");
		movementTypePointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(battleAnimationPointerField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		movementTypePointerField.setLayoutData(viewData);
		
		rainMovementPointerField = new LabelFieldView(miscGroup, SWT.NONE);
		rainMovementPointerField.setLabel("Rain Movement Pointer: ");
		rainMovementPointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(movementTypePointerField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		rainMovementPointerField.setLayoutData(viewData);
		
		snowMovementPointerField = new LabelFieldView(miscGroup, SWT.NONE);
		snowMovementPointerField.setLabel("Snow Movement Pointer: ");
		snowMovementPointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(rainMovementPointerField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		snowMovementPointerField.setLayoutData(viewData);
		
		terrainAvoidPointerField = new LabelFieldView(miscGroup, SWT.NONE);
		terrainAvoidPointerField.setLabel("Terrain Avoid Bonus Pointer: ");
		terrainAvoidPointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(snowMovementPointerField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		terrainAvoidPointerField.setLayoutData(viewData);
		
		terrainDefensePointerField = new LabelFieldView(miscGroup, SWT.NONE);
		terrainDefensePointerField.setLabel("Terrain Defense Bonus Pointer: ");
		terrainDefensePointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(terrainAvoidPointerField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		terrainDefensePointerField.setLayoutData(viewData);
		
		terrainResistancePointerField = new LabelFieldView(miscGroup, SWT.NONE);
		terrainResistancePointerField.setLabel("Terrain Resistance Bonus Pointer: ");
		terrainResistancePointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(terrainDefensePointerField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		terrainResistancePointerField.setLayoutData(viewData);
		
		terrainUnknownPointerField = new LabelFieldView(miscGroup, SWT.NONE);
		terrainUnknownPointerField.setLabel("Unknown Pointer: ");
		terrainUnknownPointerField.setField("");
		
		viewData = new FormData();
		viewData.top = new FormAttachment(terrainResistancePointerField, 10);
		viewData.left = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		terrainUnknownPointerField.setLayoutData(viewData);
		
		//////////
		
		growthsGroup = new Group(middleGroup, SWT.NONE);
		growthsGroup.setText("Growths");
		
		FormLayout growthLayout = new FormLayout();
		growthLayout.marginWidth = 5;
		growthLayout.marginHeight = 5;
		growthsGroup.setLayout(growthLayout);
		
		FormData growthData = new FormData();
		growthData.left = new FormAttachment(0, 0);
		growthData.top = new FormAttachment(0, 0);
		growthData.width = 200;
		growthsGroup.setLayoutData(growthData);
		
		hpGrowthField = new LabelFieldView(growthsGroup, SWT.NONE);
		hpGrowthField.setLabel("HP Growth: ");
		hpGrowthField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		hpGrowthField.setLayoutData(viewData);
		
		powGrowthField = new LabelFieldView(growthsGroup, SWT.NONE);
		powGrowthField.setLabel("STR Growth: ");
		powGrowthField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(hpGrowthField, 10);
		viewData.right = new FormAttachment(100, 0);
		powGrowthField.setLayoutData(viewData);
		
		sklGrowthField = new LabelFieldView(growthsGroup, SWT.NONE);
		sklGrowthField.setLabel("SKL Growth: ");
		sklGrowthField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(powGrowthField, 10);
		viewData.right = new FormAttachment(100, 0);
		sklGrowthField.setLayoutData(viewData);
		
		spdGrowthField = new LabelFieldView(growthsGroup, SWT.NONE);
		spdGrowthField.setLabel("SPD Growth: ");
		spdGrowthField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sklGrowthField, 10);
		viewData.right = new FormAttachment(100, 0);
		spdGrowthField.setLayoutData(viewData);
		
		defGrowthField = new LabelFieldView(growthsGroup, SWT.NONE);
		defGrowthField.setLabel("DEF Growth: ");
		defGrowthField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(spdGrowthField, 10);
		viewData.right = new FormAttachment(100, 0);
		defGrowthField.setLayoutData(viewData);
		
		resGrowthField = new LabelFieldView(growthsGroup, SWT.NONE);
		resGrowthField.setLabel("RES Growth: ");
		resGrowthField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(defGrowthField, 10);
		viewData.right = new FormAttachment(100, 0);
		resGrowthField.setLayoutData(viewData);
		
		lckGrowthField = new LabelFieldView(growthsGroup, SWT.NONE);
		lckGrowthField.setLabel("LCK Growth: ");
		lckGrowthField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(resGrowthField, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		lckGrowthField.setLayoutData(viewData);
		
		//////////
		
		basesGroup = new Group(middleGroup, SWT.NONE);
		basesGroup.setText("Bases");
		
		FormLayout basesLayout = new FormLayout();
		basesLayout.marginWidth = 5;
		basesLayout.marginHeight = 5;
		basesGroup.setLayout(basesLayout);
		
		FormData basesData = new FormData();
		basesData.left = new FormAttachment(growthsGroup, 10);
		basesData.top = new FormAttachment(0, 0);
		basesData.width = 200;
		basesGroup.setLayoutData(basesData);
		
		hpBaseField = new LabelFieldView(basesGroup, SWT.NONE);
		hpBaseField.setLabel("Base HP: ");
		hpBaseField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		hpBaseField.setLayoutData(viewData);
		
		powBaseField = new LabelFieldView(basesGroup, SWT.NONE);
		powBaseField.setLabel("Base STR: ");
		powBaseField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(hpBaseField, 10);
		viewData.right = new FormAttachment(100, 0);
		powBaseField.setLayoutData(viewData);
		
		sklBaseField = new LabelFieldView(basesGroup, SWT.NONE);
		sklBaseField.setLabel("Base SKL: ");
		sklBaseField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(powBaseField, 10);
		viewData.right = new FormAttachment(100, 0);
		sklBaseField.setLayoutData(viewData);
		
		spdBaseField = new LabelFieldView(basesGroup, SWT.NONE);
		spdBaseField.setLabel("Base SPD: ");
		spdBaseField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sklBaseField, 10);
		viewData.right = new FormAttachment(100, 0);
		spdBaseField.setLayoutData(viewData);
		
		defBaseField = new LabelFieldView(basesGroup, SWT.NONE);
		defBaseField.setLabel("Base DEF: ");
		defBaseField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(spdBaseField, 10);
		viewData.right = new FormAttachment(100, 0);
		defBaseField.setLayoutData(viewData);
		
		resBaseField = new LabelFieldView(basesGroup, SWT.NONE);
		resBaseField.setLabel("Base RES: ");
		resBaseField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(defBaseField, 10);
		viewData.right = new FormAttachment(100, 0);
		resBaseField.setLayoutData(viewData);
		
		conBaseField = new LabelFieldView(basesGroup, SWT.NONE);
		conBaseField.setLabel("Base CON: ");
		conBaseField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(resBaseField, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		conBaseField.setLayoutData(viewData);
		
		//////////
		
		capsGroup = new Group(middleGroup, SWT.NONE);
		capsGroup.setText("Caps");
		
		FormLayout capsLayout = new FormLayout();
		capsLayout.marginWidth = 5;
		capsLayout.marginHeight = 5;
		capsGroup.setLayout(capsLayout);
		
		FormData capsData = new FormData();
		capsData.left = new FormAttachment(basesGroup, 10);
		capsData.top = new FormAttachment(0, 0);
		capsData.width = 200;
		capsGroup.setLayoutData(capsData);
		
		hpCapField = new LabelFieldView(capsGroup, SWT.NONE);
		hpCapField.setLabel("Max HP: ");
		hpCapField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		hpCapField.setLayoutData(viewData);
		
		powCapField = new LabelFieldView(capsGroup, SWT.NONE);
		powCapField.setLabel("Max STR: ");
		powCapField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(hpCapField, 10);
		viewData.right = new FormAttachment(100, 0);
		powCapField.setLayoutData(viewData);
		
		sklCapField = new LabelFieldView(capsGroup, SWT.NONE);
		sklCapField.setLabel("Max SKL: ");
		sklCapField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(powCapField, 10);
		viewData.right = new FormAttachment(100, 0);
		sklCapField.setLayoutData(viewData);
		
		spdCapField = new LabelFieldView(capsGroup, SWT.NONE);
		spdCapField.setLabel("Max SPD: ");
		spdCapField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sklCapField, 10);
		viewData.right = new FormAttachment(100, 0);
		spdCapField.setLayoutData(viewData);
		
		defCapField = new LabelFieldView(capsGroup, SWT.NONE);
		defCapField.setLabel("Max DEF: ");
		defCapField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(spdCapField, 10);
		viewData.right = new FormAttachment(100, 0);
		defCapField.setLayoutData(viewData);
		
		resCapField = new LabelFieldView(capsGroup, SWT.NONE);
		resCapField.setLabel("Max RES: ");
		resCapField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(defCapField, 10);
		viewData.right = new FormAttachment(100, 0);
		resCapField.setLayoutData(viewData);
		
		lckCapField = new LabelFieldView(capsGroup, SWT.NONE);
		lckCapField.setLabel("Max LCK: ");
		lckCapField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(resCapField, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		lckCapField.setLayoutData(viewData);
		
		//////////
		
		promoGroup = new Group(middleGroup, SWT.NONE);
		promoGroup.setText("Promotion Bonuses");
		
		FormLayout promoLayout = new FormLayout();
		promoLayout.marginWidth = 5;
		promoLayout.marginHeight = 5;
		promoGroup.setLayout(promoLayout);
		
		FormData promoData = new FormData();
		promoData.left = new FormAttachment(capsGroup, 10);
		promoData.top = new FormAttachment(0, 0);
		promoData.width = 200;
		promoGroup.setLayoutData(promoData);
		
		hpBonusField = new LabelFieldView(promoGroup, SWT.NONE);
		hpBonusField.setLabel("HP Bonus: ");
		hpBonusField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		hpBonusField.setLayoutData(viewData);
		
		powBonusField = new LabelFieldView(promoGroup, SWT.NONE);
		powBonusField.setLabel("STR Bonus: ");
		powBonusField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(hpBonusField, 10);
		viewData.right = new FormAttachment(100, 0);
		powBonusField.setLayoutData(viewData);
		
		sklBonusField = new LabelFieldView(promoGroup, SWT.NONE);
		sklBonusField.setLabel("SKL Bonus: ");
		sklBonusField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(powBonusField, 10);
		viewData.right = new FormAttachment(100, 0);
		sklBonusField.setLayoutData(viewData);
		
		spdBonusField = new LabelFieldView(promoGroup, SWT.NONE);
		spdBonusField.setLabel("SPD Bonus: ");
		spdBonusField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(sklBonusField, 10);
		viewData.right = new FormAttachment(100, 0);
		spdBonusField.setLayoutData(viewData);
		
		defBonusField = new LabelFieldView(promoGroup, SWT.NONE);
		defBonusField.setLabel("DEF Bonus: ");
		defBonusField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(spdBonusField, 10);
		viewData.right = new FormAttachment(100, 0);
		defBonusField.setLayoutData(viewData);
		
		resBonusField = new LabelFieldView(promoGroup, SWT.NONE);
		resBonusField.setLabel("RES Bonus: ");
		resBonusField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(defBonusField, 10);
		viewData.right = new FormAttachment(100, 0);
		resBonusField.setLayoutData(viewData);
		
		conBonusField = new LabelFieldView(promoGroup, SWT.NONE);
		conBonusField.setLabel("CON Bonus: ");
		conBonusField.setField("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(resBonusField, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		conBonusField.setLayoutData(viewData);
		
		//////////
		
		flagsGroup = new Group(bottomGroup, SWT.NONE);
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
		
		ability1Flags = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 1", classData.ability1Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(0, 0);
		ability1Flags.setLayoutData(viewData);
		
		ability2Flags = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 2", classData.ability2Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(ability1Flags, 10);
		ability2Flags.setLayoutData(viewData);
		
		ability3Flags = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 3", classData.ability3Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(ability2Flags, 10);
		ability3Flags.setLayoutData(viewData);
		
		ability4Flags = new LabelCheckboxView(flagsGroup, SWT.NONE, "Ability 4", classData.ability4Flags(), false);
		
		viewData = new FormData();
		viewData.top = new FormAttachment(0, 0);
		viewData.left = new FormAttachment(ability3Flags, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		ability4Flags.setLayoutData(viewData);
		
		
		for (GBAFEClassData charClass : classList) {
			String displayName = textData.getStringAtIndex(charClass.getNameIndex(), true);
			classDropdown.add("[0x" + Integer.toHexString(charClass.getID()).toUpperCase() + "] " + (displayName != null ? displayName : "???"));
		}
	}
	
	private void setClass(GBAFEClassData charClass) {
		idField.setField("0x" + Integer.toHexString(charClass.getID()).toUpperCase());
		nameIndexField.setField("0x" + Integer.toHexString(charClass.getNameIndex()).toUpperCase());
		nameField.setField(textData.getStringAtIndex(charClass.getNameIndex(), true));
		descriptionIndexField.setField("0x" + Integer.toHexString(charClass.getDescriptionIndex()).toUpperCase());
		String description = textData.getStringAtIndex(charClass.getDescriptionIndex(), true);
		descriptionLabel.setText(description != null ? description : "(null)");
		
		WeaponRanks unroundedRanks = charClass.getWeaponRanks(false, gameType);
		WeaponRanks roundedRanks = charClass.getWeaponRanks(true, gameType);
		swordRankField.setField(rankString(charClass.getSwordRank(), unroundedRanks.swordRank, roundedRanks.swordRank));
		lanceRankField.setField(rankString(charClass.getLanceRank(), unroundedRanks.lanceRank, roundedRanks.lanceRank));
		axeRankField.setField(rankString(charClass.getAxeRank(), unroundedRanks.axeRank, roundedRanks.axeRank));
		bowRankField.setField(rankString(charClass.getBowRank(), unroundedRanks.bowRank, roundedRanks.bowRank));
		animaRankField.setField(rankString(charClass.getAnimaRank(), unroundedRanks.animaRank, roundedRanks.animaRank));
		lightRankField.setField(rankString(charClass.getLightRank(), unroundedRanks.lightRank, roundedRanks.lightRank));
		darkRankField.setField(rankString(charClass.getDarkRank(), unroundedRanks.darkRank, roundedRanks.darkRank));
		staffRankField.setField(rankString(charClass.getStaffRank(), unroundedRanks.staffRank, roundedRanks.staffRank));
		
		boolean isPhysical = roundedRanks.swordRank != WeaponRank.NONE || roundedRanks.lanceRank != WeaponRank.NONE || roundedRanks.axeRank != WeaponRank.NONE || roundedRanks.bowRank != WeaponRank.NONE;
		if (isPhysical) {
			powGrowthField.setLabel("STR Growth: ");
			powBaseField.setLabel("Base STR: ");
			powCapField.setLabel("Max STR: ");
			powBonusField.setLabel("STR Bonus: ");
		} else {
			powGrowthField.setLabel("MAG Growth: ");
			powBaseField.setLabel("Base MAG: ");
			powCapField.setLabel("Max MAG: ");
			powBonusField.setLabel("MAG Bonus: ");
		}
		powGrowthField.requestLayout();
		powBaseField.requestLayout();
		powCapField.requestLayout();
		powBonusField.requestLayout();
		
		hpGrowthField.setField(Integer.toString(charClass.getHPGrowth()) + "%");
		powGrowthField.setField(Integer.toString(charClass.getSTRGrowth()) + "%");
		sklGrowthField.setField(Integer.toString(charClass.getSKLGrowth()) + "%");
		spdGrowthField.setField(Integer.toString(charClass.getSPDGrowth()) + "%");
		defGrowthField.setField(Integer.toString(charClass.getDEFGrowth()) + "%");
		resGrowthField.setField(Integer.toString(charClass.getRESGrowth()) + "%");
		lckGrowthField.setField(Integer.toString(charClass.getLCKGrowth()) + "%");
		
		hpBaseField.setField(Integer.toString(charClass.getBaseHP()));
		powBaseField.setField(Integer.toString(charClass.getBaseSTR()));
		sklBaseField.setField(Integer.toString(charClass.getBaseSKL()));
		spdBaseField.setField(Integer.toString(charClass.getBaseSPD()));
		defBaseField.setField(Integer.toString(charClass.getBaseDEF()));
		resBaseField.setField(Integer.toString(charClass.getBaseRES()));
		conBaseField.setField(Integer.toString(charClass.getCON()));
		
		hpCapField.setField(Integer.toString(charClass.getMaxHP()));
		powCapField.setField(Integer.toString(charClass.getMaxSTR()));
		sklCapField.setField(Integer.toString(charClass.getMaxSKL()));
		spdCapField.setField(Integer.toString(charClass.getMaxSPD()));
		defCapField.setField(Integer.toString(charClass.getMaxDEF()));
		resCapField.setField(Integer.toString(charClass.getMaxRES()));
		lckCapField.setField(Integer.toString(charClass.getMaxLCK()));
		
		if (charClass.isPromotedClass()) {
			hpBonusField.setField(Integer.toString(charClass.getPromoHP()));
			powBonusField.setField(Integer.toString(charClass.getPromoSTR()));
			sklBonusField.setField(Integer.toString(charClass.getPromoSKL()));
			spdBonusField.setField(Integer.toString(charClass.getPromoSPD()));
			defBonusField.setField(Integer.toString(charClass.getPromoDEF()));
			resBonusField.setField(Integer.toString(charClass.getPromoRES()));
			conBonusField.setField(Integer.toString(charClass.getPromoCON()));
		} else {
			hpBonusField.setField("--");
			powBonusField.setField("--");
			sklBonusField.setField("--");
			spdBonusField.setField("--");
			defBonusField.setField("--");
			resBonusField.setField("--");
			conBonusField.setField("--");
		}
		
		movementRangeField.setField(Integer.toString(charClass.getMOV()));
		spriteIndexField.setField("0x" + Integer.toHexString(charClass.getSpriteIndex()).toUpperCase());
		GBAFEClassData promotionClass = classData.classForID(charClass.getTargetPromotionID());
		String promoDisplayString = "(null)";
		if (promotionClass != null) {
			String promotionClassName = textData.getStringAtIndex(promotionClass.getNameIndex(), true);
			if (promotionClassName != null) {
				promoDisplayString = "[0x" + Integer.toHexString(promotionClass.getID()).toUpperCase() + "] " + promotionClassName;
			} else {
				promoDisplayString = "[0x" + Integer.toHexString(charClass.getTargetPromotionID()).toUpperCase() + "] ???";
			}
		}
		targetPromotionField.setField(promoDisplayString);
		walkingSpeedField.setField("0x" + Integer.toHexString(charClass.getWalkingSpeed()).toUpperCase());
		
		battleAnimationPointerField.setField("0x" + Long.toHexString(charClass.getBattleAnimationPointer()).toUpperCase());
		movementTypePointerField.setField("0x" + Long.toHexString(charClass.getMovementTypePointer()).toUpperCase());
		Long rainPointer = charClass.getRainMovementPointer();
		if (rainPointer == null) {
			rainMovementPointerField.setEnabled(false);
			rainMovementPointerField.setField("--");
		} else {
			rainMovementPointerField.setEnabled(true);
			rainMovementPointerField.setField("0x" + Long.toHexString(rainPointer).toUpperCase());
		}
		
		Long snowPointer = charClass.getSnowMovementPointer();
		if (snowPointer == null) {
			snowMovementPointerField.setEnabled(false);
			snowMovementPointerField.setField("--");
		} else {
			snowMovementPointerField.setEnabled(true);
			snowMovementPointerField.setField("0x" + Long.toHexString(snowPointer).toUpperCase());
		}
		terrainAvoidPointerField.setField("0x" + Long.toHexString(charClass.getTerrainAvoidBonusPointer()).toUpperCase());
		terrainDefensePointerField.setField("0x" + Long.toHexString(charClass.getTerrainDefenseBonusPointer()).toUpperCase());
		terrainResistancePointerField.setField("0x" + Long.toHexString(charClass.getTerrainResistanceBonusPointer()).toUpperCase());
		terrainUnknownPointerField.setField("0x" + Long.toHexString(charClass.getTerrainUnknownBonusPointer()).toUpperCase());
		
		ability1Flags.setCheckboxes(classData.ability1Flags().stream().filter(displayString -> classData.classHasFlagByDisplayString(displayString, charClass)).collect(Collectors.toList()));
		ability2Flags.setCheckboxes(classData.ability2Flags().stream().filter(displayString -> classData.classHasFlagByDisplayString(displayString, charClass)).collect(Collectors.toList()));
		ability3Flags.setCheckboxes(classData.ability3Flags().stream().filter(displayString -> classData.classHasFlagByDisplayString(displayString, charClass)).collect(Collectors.toList()));
		ability4Flags.setCheckboxes(classData.ability4Flags().stream().filter(displayString -> classData.classHasFlagByDisplayString(displayString, charClass)).collect(Collectors.toList()));
		
		requestLayout();
	}
	
	private String rankString(int rankValue, WeaponRank unrounded, WeaponRank rounded) {
		if (unrounded == rounded) {
			if (unrounded != WeaponRank.NONE) {
				return unrounded.displayString();
			} else {
				return "";
			}
		}
		
		return rounded.displayString() + " (0x" + Integer.toHexString(rankValue) + ")";
	}
}
