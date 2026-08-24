package ui.views.gba;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import fedata.gba.general.GBAFECharacter;
import fedata.gba.general.GBAFECharacterProvider;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEClassProvider;
import fedata.general.FEBase.GameType;
import ui.model.gba.AdvancedPlayerClassOptions;
import ui.model.gba.AdvancedPlayerClassOptions.BaseTransferOption;
import ui.model.gba.AdvancedPlayerClassOptions.GenderRestrictionOption;
import ui.model.gba.AdvancedPlayerClassOptions.GrowthAdjustmentOption;
import ui.views.YuneView;
import ui.views.components.DualListSelectionView;
import ui.views.components.ListDisplayable;

public class GBAAdvancedClassesView extends YuneView<AdvancedPlayerClassOptions> {
	private GBAFECharacterProvider characterDataProvider;
	private GBAFEClassProvider classDataProvider;
	
	private int style;
	private GameType type;
	
	private Button enablePC;
	
	private Composite characterComposite;
	private Label charactersLabel;
	private DualListSelectionView<GBAFECharacter> characterSelection;
	
	private Composite unpromotedComposite;
	private Label unpromotedLabel;
	private DualListSelectionView<GBAFEClass> unpromotedSelection;
	
	private Composite promotedComposite;
	private Label promotedLabel;
	private DualListSelectionView<GBAFEClass> promotedSelection;
	
	private Composite otherOptionsComposite;
	private Button evenDistribution;
	private Button forceChange;
	private Button treatSimilarAsSame;
	
	private Composite growthOptions;
	private Button growthNoAdjustments;
	private Button growthTransferPersonalGrowths;
	
	private Composite basesOptions;
	private Button basesRetainPersonal;
	private Button basesRetainFinal;
	private Button basesAdjustToClass;
	
	private Composite genderOptions;
	private Button genderNoRestriction;
	private Button genderLoose;
	private Button genderStrict;
	
	// supports style: SWT.HORIZONTAL, SWT.VERTICAL
	public GBAAdvancedClassesView(Composite parent, GameType type, int style) {
		super(parent, type, true);
		this.type = type;
		characterDataProvider = type.charProvider();
		classDataProvider = type.classProvider();
		if (style == SWT.HORIZONTAL || style == SWT.VERTICAL) {
			this.style = style;
		} else {
			this.style = SWT.VERTICAL;
		}
		compose();
	}
	
	@Override
	public String getGroupTitle() {
		return "Player Classes";
	}
	
	@Override
	public String getGroupTooltip() {
		return "Randomizes the classes of playable characters.";
	}

	@Override
	public void initialize(AdvancedPlayerClassOptions options) {
		if (options == null) {
			updateEnabled(false);
			return;
		}
		
		enablePC.setSelection(options.randomizePlayableCharacters);
		evenDistribution.setSelection(options.assignEvenly);
		forceChange.setSelection(options.forceChange);
		treatSimilarAsSame.setSelection(options.treatSimilarAsSame);
		
		growthNoAdjustments.setSelection(options.growthAdjustments == GrowthAdjustmentOption.NO_CHANGE);
		growthTransferPersonalGrowths.setSelection(options.growthAdjustments == GrowthAdjustmentOption.TRANSFER_PERSONAL_GROWTHS);
		
		basesRetainPersonal.setSelection(options.transferBases == BaseTransferOption.NO_CHANGE);
		basesRetainFinal.setSelection(options.transferBases == BaseTransferOption.ADJUST_TO_MATCH);
		basesAdjustToClass.setSelection(options.transferBases == BaseTransferOption.ADJUST_TO_CLASS);
		
		genderNoRestriction.setSelection(options.restrictGender == GenderRestrictionOption.NONE);
		genderLoose.setSelection(options.restrictGender == GenderRestrictionOption.LOOSE);
		genderStrict.setSelection(options.restrictGender == GenderRestrictionOption.STRICT);
		
		updateEnabled(options.randomizePlayableCharacters);
		
		List<GBAFECharacter> ignoredCharacters = new ArrayList<GBAFECharacter>();
		List<GBAFECharacter> randomizedCharacters = new ArrayList<GBAFECharacter>();
		List<GBAFECharacter> fullCharacterList = characterDataProvider.allCanonicalPlayableCharacters().stream().sorted(ListDisplayable.displayableComparator).toList();
		fullCharacterList.forEach(character -> {
			if (options.randomizedCharacterIDs.contains(character.getID())) {
				randomizedCharacters.add(character);
			} else {
				ignoredCharacters.add(character);
			}
		});
		List<GBAFECharacter> extras = characterDataProvider.extraCharacters().stream().sorted(ListDisplayable.displayableComparator).toList();
		extras.forEach(character -> {
			if (options.randomizedCharacterIDs.contains(character.getID())) {
				randomizedCharacters.add(character);
			} else {
				ignoredCharacters.add(character);
			}
		});
		
		List<GBAFEClass> bannedUnpromoted = new ArrayList<GBAFEClass>();
		List<GBAFEClass> allowedUnpromoted = new ArrayList<GBAFEClass>();
		List<GBAFEClass> bannedPromoted = new ArrayList<GBAFEClass>();
		List<GBAFEClass> allowedPromoted = new ArrayList<GBAFEClass>();
		List<GBAFEClass> fullClassList = classDataProvider.allValidClasses().stream().sorted(ListDisplayable.displayableComparator).toList();
		fullClassList.forEach(charClass -> {
			boolean isPromoted = classDataProvider.isClassPromoted(charClass);
			boolean isAllowed = options.allowedClassIDs.contains(charClass.getID());
			
			if (isPromoted) {
				if (isAllowed) { allowedPromoted.add(charClass); }
				else { bannedPromoted.add(charClass); }
			} else {
				if (isAllowed) { allowedUnpromoted.add(charClass); }
				else { bannedUnpromoted.add(charClass); }
			}
		});
		
		characterSelection.updateItems(ignoredCharacters, randomizedCharacters);
		unpromotedSelection.updateItems(bannedUnpromoted, allowedUnpromoted);
		promotedSelection.updateItems(bannedPromoted, allowedPromoted);
	}
	
	private void updateEnabled(boolean isEnabled) {
		charactersLabel.setEnabled(isEnabled);
		characterSelection.setEnabled(isEnabled);
		unpromotedLabel.setEnabled(isEnabled);
		unpromotedSelection.setEnabled(isEnabled);
		promotedLabel.setEnabled(isEnabled);
		promotedSelection.setEnabled(isEnabled);
		otherOptionsComposite.setEnabled(isEnabled);
		evenDistribution.setEnabled(isEnabled);
		forceChange.setEnabled(isEnabled);
		treatSimilarAsSame.setEnabled(isEnabled && (evenDistribution.getSelection() || forceChange.getSelection()));
		
		growthOptions.setEnabled(isEnabled);
		growthNoAdjustments.setEnabled(isEnabled);
		growthTransferPersonalGrowths.setEnabled(isEnabled);
		
		basesOptions.setEnabled(isEnabled);
		basesRetainPersonal.setEnabled(isEnabled);
		basesRetainFinal.setEnabled(isEnabled);
		basesAdjustToClass.setEnabled(isEnabled);
		
		genderOptions.setEnabled(isEnabled);
		genderNoRestriction.setEnabled(isEnabled);
		genderLoose.setEnabled(isEnabled);
		genderStrict.setEnabled(isEnabled);
	}

	@Override
	public AdvancedPlayerClassOptions getOptions() {
		List<GBAFECharacter> selectedCharacters = characterSelection.getAllRightItems();
		List<GBAFEClass> selectedClasses = new ArrayList<GBAFEClass>();
		
		selectedClasses.addAll(unpromotedSelection.getAllRightItems());
		selectedClasses.addAll(promotedSelection.getAllRightItems());
		
		GrowthAdjustmentOption growthOption = GrowthAdjustmentOption.NO_CHANGE;
		if (growthTransferPersonalGrowths.getSelection()) {
			growthOption = GrowthAdjustmentOption.TRANSFER_PERSONAL_GROWTHS;
		}
		
		BaseTransferOption basesOption = BaseTransferOption.ADJUST_TO_MATCH;
		if (basesRetainPersonal.getSelection()) {
			basesOption = BaseTransferOption.NO_CHANGE;
		} else if (basesAdjustToClass.getSelection()) {
			basesOption = BaseTransferOption.ADJUST_TO_CLASS;
		}
		
		GenderRestrictionOption genderOption = GenderRestrictionOption.NONE;
		if (genderLoose.getSelection()) {
			genderOption = GenderRestrictionOption.LOOSE;
		} else if (genderStrict.getSelection()) {
			genderOption = GenderRestrictionOption.STRICT;
		}
		
		return new AdvancedPlayerClassOptions(enablePC.getSelection(),
				selectedCharacters.stream().map(character -> character.getID()).toList(), 
				selectedClasses.stream().map(charClass -> charClass.getID()).toList(), 
				evenDistribution.getSelection(),
				forceChange.getSelection(), 
				treatSimilarAsSame.getSelection(), 
				basesOption, 
				genderOption, 
				growthOption);
	}
	
	private boolean isVerticalLayout() {
		return (style & SWT.HORIZONTAL) == 0;
	}

	@Override
	protected void compose() {
		enablePC = new Button(group, SWT.CHECK);
		enablePC.setText("Randomize Playable Characters");
		enablePC.setToolTipText("Randomize classes for playable characters.");
		enablePC.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateEnabled(enablePC.getSelection());
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.top = new FormAttachment(0, 0);
		buttonData.left = new FormAttachment(0, 0);
		enablePC.setLayoutData(buttonData);
		
		buildCharacterComposite();
		buildUnpromotedComposite();
		buildPromotedComposite();
		buildOtherOptions(isVerticalLayout() == false);
		
		if (isVerticalLayout()) {
			FormData compositeData = new FormData();
			compositeData.top = new FormAttachment(enablePC, 10);
			compositeData.left = new FormAttachment(0, 5);
			compositeData.right = new FormAttachment(100, -5);
			characterComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(characterComposite, 10);
			compositeData.left = new FormAttachment(0, 5);
			compositeData.right = new FormAttachment(100, -5);
			unpromotedComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(unpromotedComposite, 10);
			compositeData.left = new FormAttachment(0, 5);
			compositeData.right = new FormAttachment(100, -5);
			promotedComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(promotedComposite, 10);
			compositeData.left = new FormAttachment(0, 5);
			otherOptionsComposite.setLayoutData(compositeData);
		} else {
			FormData compositeData = new FormData();
			compositeData.top = new FormAttachment(enablePC, 10);
			compositeData.left = new FormAttachment(0, 5);
			compositeData.right = new FormAttachment(50, -5);
			characterComposite.setLayoutData(compositeData);
			characterSelection.setListHeight(250);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(characterComposite, 10);
			compositeData.left = new FormAttachment(0, 5);
			unpromotedComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(unpromotedComposite, 0, SWT.TOP);
			compositeData.left = new FormAttachment(unpromotedComposite, 10);
			compositeData.right = new FormAttachment(100, -5);
			promotedComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.bottom = new FormAttachment(characterComposite, 0, SWT.BOTTOM);
			compositeData.left = new FormAttachment(characterComposite, 5);
			compositeData.right = new FormAttachment(100, -5);
			otherOptionsComposite.setLayoutData(compositeData);
		}
	}
	
	private void buildCharacterComposite() {
		characterComposite = new Composite(group, SWT.NONE);
		characterComposite.setLayout(new FormLayout());
		
		charactersLabel = new Label(characterComposite, SWT.NONE);
		charactersLabel.setText("Characters");
		
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(0, 0);
		labelData.left = new FormAttachment(0, 0);
		charactersLabel.setLayoutData(labelData);
		
		List<GBAFECharacter> characterList = new ArrayList<GBAFECharacter>(characterDataProvider.allCanonicalPlayableCharacters().stream().toList());
		characterList.addAll(characterDataProvider.extraCharacters());
		characterList.sort(ListDisplayable.displayableComparator);
		
		characterSelection = new DualListSelectionView<GBAFECharacter>(characterComposite, "Ignore", "Randomize", "Select None", "Select All", 
				characterList, characterDataProvider.extraCharacters().stream().sorted(ListDisplayable.displayableComparator).toList(), 
				characterDataProvider.allCanonicalPlayableCharacters().stream().sorted(ListDisplayable.displayableComparator).toList());
		
		FormData compositeData = new FormData();
		compositeData.top = new FormAttachment(charactersLabel, 5);
		compositeData.left = new FormAttachment(charactersLabel, 5, SWT.LEFT);
		compositeData.right = new FormAttachment(100, 0);
		compositeData.bottom = new FormAttachment(100, 0);
		characterSelection.setLayoutData(compositeData);
	}
	
	private void buildUnpromotedComposite() {
		unpromotedComposite = new Composite(group, SWT.NONE);
		unpromotedComposite.setLayout(new FormLayout());
		
		unpromotedLabel = new Label(unpromotedComposite, SWT.NONE);
		unpromotedLabel.setText("Target Classes (Unpromoted)");
		
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(0, 0);
		labelData.left = new FormAttachment(0, 0);
		unpromotedLabel.setLayoutData(labelData);
		
		List<GBAFEClass> allClasses = classDataProvider.allValidClasses().stream()
				.filter(charClass -> classDataProvider.isClassPromoted(charClass) == false)
				.sorted(ListDisplayable.displayableComparator)
				.toList();
		List<GBAFEClass> disabledByDefault = new ArrayList<GBAFEClass>();
		List<GBAFEClass> includedByDefault = new ArrayList<GBAFEClass>();
		allClasses.forEach(charClass -> {
			if (classDataProvider.disabledByDefaultClasses().contains(charClass)) {
				disabledByDefault.add(charClass);
			} else {
				includedByDefault.add(charClass);
			}
		});
		unpromotedSelection = new DualListSelectionView<GBAFEClass>(unpromotedComposite, "Banned", "Allowed", "Select None", "Select All",
				allClasses, disabledByDefault, includedByDefault);
		
		FormData compositeData = new FormData();
		compositeData.top = new FormAttachment(unpromotedLabel, 5);
		compositeData.left = new FormAttachment(unpromotedLabel, 5, SWT.LEFT);
		compositeData.right = new FormAttachment(100, 0);
		compositeData.bottom = new FormAttachment(100, 0);
		unpromotedSelection.setLayoutData(compositeData);
	}
	
	private void buildPromotedComposite() {
		promotedComposite = new Composite(group, SWT.NONE);
		promotedComposite.setLayout(new FormLayout());
		
		promotedLabel = new Label(promotedComposite, SWT.NONE);
		promotedLabel.setText("Target Classes (Promoted)");
		
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(0, 0);
		labelData.left = new FormAttachment(0, 0);
		promotedLabel.setLayoutData(labelData);
		
		List<GBAFEClass> allClasses = classDataProvider.allValidClasses().stream()
				.filter(charClass -> classDataProvider.isClassPromoted(charClass))
				.sorted(ListDisplayable.displayableComparator)
				.toList();
		List<GBAFEClass> disabledByDefault = new ArrayList<GBAFEClass>();
		List<GBAFEClass> includedByDefault = new ArrayList<GBAFEClass>();
		allClasses.forEach(charClass -> {
			if (classDataProvider.disabledByDefaultClasses().contains(charClass)) {
				disabledByDefault.add(charClass);
			} else {
				includedByDefault.add(charClass);
			}
		});
		promotedSelection = new DualListSelectionView<GBAFEClass>(promotedComposite, "Banned", "Allowed", "Select None", "Select All",
				allClasses, disabledByDefault, includedByDefault);
		
		FormData compositeData = new FormData();
		compositeData.top = new FormAttachment(promotedLabel, 5);
		compositeData.left = new FormAttachment(promotedLabel, 5, SWT.LEFT);
		compositeData.right = new FormAttachment(100, 0);
		compositeData.bottom = new FormAttachment(100, 0);
		promotedSelection.setLayoutData(compositeData);
	}

	private void buildOtherOptions(boolean useGroupContainer) {
		if (useGroupContainer) {
			Group optionsGroup = new Group(group, SWT.SHADOW_ETCHED_IN);
			optionsGroup.setText("Other Options");
			otherOptionsComposite = optionsGroup;
		} else {
			otherOptionsComposite = new Composite(group, SWT.NONE);
		}
		otherOptionsComposite.setLayout(new FormLayout());
		
		evenDistribution = new Button(otherOptionsComposite, SWT.CHECK);
		evenDistribution.setText("Distribute Classes Evenly");
		evenDistribution.setToolTipText("Attempts to avoid assigning classes that have already been assigned, where possible.");
		evenDistribution.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				treatSimilarAsSame.setEnabled((evenDistribution.getSelection() || forceChange.getSelection()) && enablePC.getSelection());
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(0, 5);
		evenDistribution.setLayoutData(buttonData);
		
		forceChange = new Button(otherOptionsComposite, SWT.CHECK);
		forceChange.setText("Force Class Change");
		forceChange.setToolTipText("Ensures (where possible) characters are assigned classes that are different from their original class.");
		forceChange.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				treatSimilarAsSame.setEnabled((evenDistribution.getSelection() || forceChange.getSelection()) && enablePC.getSelection());
			}
		});
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(evenDistribution, 10);
		forceChange.setLayoutData(buttonData);
		
		treatSimilarAsSame = new Button(otherOptionsComposite, SWT.CHECK);
		treatSimilarAsSame.setText("Treat Similar Classes as Same Class");
		treatSimilarAsSame.setToolTipText("Classes of different gender (e.g. Male vs. Female Archers) are treated as the same class.\nThis only has an effect when forcing class changes or distributing classes evenly.");
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(forceChange, 10);
		treatSimilarAsSame.setLayoutData(buttonData);
		
		Group growthOptionsGroup = new Group(otherOptionsComposite, SWT.SHADOW_ETCHED_IN);
		growthOptionsGroup.setText("Growth Adjustment");
		growthOptions = growthOptionsGroup;
		growthOptions.setLayout(new FormLayout());
		
		FormData compositeData = new FormData();
		compositeData.top = new FormAttachment(treatSimilarAsSame, 10);
		compositeData.left = new FormAttachment(0, 5);
		compositeData.right = new FormAttachment(100, -5);
		growthOptions.setLayoutData(compositeData);
		
		growthNoAdjustments = new Button(growthOptions, SWT.RADIO);
		growthNoAdjustments.setText("No Adjustment");
		growthNoAdjustments.setToolTipText("Do not modify growth rates.");
		growthNoAdjustments.setSelection(true);
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(0, 5);
		buttonData.left = new FormAttachment(0, 5);
		growthNoAdjustments.setLayoutData(buttonData);
		
		growthTransferPersonalGrowths = new Button(growthOptions, SWT.RADIO);
		growthTransferPersonalGrowths.setText("Transfer Personal Growths");
		growthTransferPersonalGrowths.setToolTipText("Derives personal growths using the difference between the character's growths and their old class growths and applies that to the new class's growths.");
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(growthNoAdjustments, 5);
		buttonData.left = new FormAttachment(0, 5);
		buttonData.bottom = new FormAttachment(100, -5);
		growthTransferPersonalGrowths.setLayoutData(buttonData);
		
		Group basesOptionsGroup = new Group(otherOptionsComposite, SWT.SHADOW_ETCHED_IN);
		basesOptionsGroup.setText("Base Stat Adjustment");
		basesOptions = basesOptionsGroup;
		basesOptions.setLayout(new FormLayout());
		
		compositeData = new FormData();
		compositeData.top = new FormAttachment(growthOptions, 10);
		compositeData.left = new FormAttachment(0, 5);
		compositeData.right = new FormAttachment(100, -5);
		basesOptions.setLayoutData(compositeData);
		
		basesRetainPersonal = new Button(basesOptions, SWT.RADIO);
		basesRetainPersonal.setText("Retain Personal Bases");
		basesRetainPersonal.setToolTipText("Do not modify personal bases. Character's base stats will change depending on the difference between old and new class's base stats.");
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(0, 5);
		buttonData.left = new FormAttachment(0, 5);
		basesRetainPersonal.setLayoutData(buttonData);
		
		basesRetainFinal = new Button(basesOptions, SWT.RADIO);
		basesRetainFinal.setText("Retain Final Bases");
		basesRetainFinal.setToolTipText("Adjusts personal bases so that the character effectively has the same bases as they did in their old class.");
		basesRetainFinal.setSelection(true);
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(basesRetainPersonal, 5);
		buttonData.left = new FormAttachment(0, 5);
		basesRetainFinal.setLayoutData(buttonData);
		
		basesAdjustToClass = new Button(basesOptions, SWT.RADIO);
		basesAdjustToClass.setText("Adjust to Class");
		basesAdjustToClass.setToolTipText("Reallocate the character's effective bases to match the stat spread of their new class.");
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(basesRetainFinal, 5);
		buttonData.left = new FormAttachment(0, 5);
		buttonData.bottom = new FormAttachment(100, -5);
		basesAdjustToClass.setLayoutData(buttonData);
		
		Group genderGroup = new Group(otherOptionsComposite, SWT.SHADOW_ETCHED_IN);
		genderGroup.setText("Gender Options");
		genderOptions = genderGroup;
		genderOptions.setLayout(new FormLayout());
		
		compositeData = new FormData();
		compositeData.top = new FormAttachment(basesOptions, 10);
		compositeData.left = new FormAttachment(0, 5);
		compositeData.right = new FormAttachment(100, -5);
		compositeData.bottom = new FormAttachment(100, -5);
		genderOptions.setLayoutData(compositeData);
		
		genderNoRestriction = new Button(genderOptions, SWT.RADIO);
		genderNoRestriction.setText("No Restriction");
		genderNoRestriction.setToolTipText("No gender restrictions. All characters can be assigned any of the selected classes.");
		genderNoRestriction.setSelection(true);
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(0, 5);
		buttonData.left = new FormAttachment(0, 5);
		genderNoRestriction.setLayoutData(buttonData);
		
		genderLoose = new Button(genderOptions, SWT.RADIO);
		genderLoose.setText("Loose Restrictions");
		genderLoose.setToolTipText("All characters can be assigned any of the selected classes. If the selected class has a version that matches the character's gender, that version will be used.");
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(genderNoRestriction, 5);
		buttonData.left = new FormAttachment(0, 5);
		genderLoose.setLayoutData(buttonData);
		
		genderStrict = new Button(genderOptions, SWT.RADIO);
		genderStrict.setText("Strict Restrictions");
		genderStrict.setToolTipText("All characters are limited to classes that match their gender (when possible).");
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(genderLoose, 5);
		buttonData.left = new FormAttachment(0, 5);
		buttonData.bottom = new FormAttachment(100, -5);
		genderStrict.setLayoutData(buttonData);
	}
}
