package ui.views.fe9;

import java.util.ArrayList;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Data.CharacterClass;
import fedata.general.FEBase.GameType;
import ui.model.fe9.FE9AdvancedClassOptions;
import ui.views.YuneView;

public class FE9AdvancedClassesView extends YuneView<FE9AdvancedClassOptions> {
	
	public enum LayoutStyle {
		TALL, WIDE
	}
	
	private LayoutStyle layoutStyle;
	
	private Button enablePC;
	private Label charactersLabel;
	private Label ignoreCharacterLabel;
	private Label randomizeCharacterLabel;
	private org.eclipse.swt.widgets.List pcIgnoreList;
	private org.eclipse.swt.widgets.List pcRandomizeList;
	private Button pcSelectAll;
	private Button pcSelectNone;
	private java.util.List<FE9Data.Character> selectedPCs;
	private java.util.List<FE9Data.Character> ignorePCs; 
	private java.util.List<FE9Data.Character> fullPCList; 
	
	private Label unpromotedClassesLabel;
	private Label promotedClassesLabel;
	private Label pcBannedUnpromotedClassListLabel;
	private Label pcAllowedUnpromotedClassListLabel;
	private org.eclipse.swt.widgets.List pcBannedUnpromotedClassList;
	private org.eclipse.swt.widgets.List pcAllowedUnpromotedClassList;
	private Label pcBannedPromotedClassListLabel;
	private Label pcAllowedPromotedClassListLabel;
	private org.eclipse.swt.widgets.List pcBannedPromotedClassList;
	private org.eclipse.swt.widgets.List pcAllowedPromotedClassList;
	private Label heronNote;
	private Button pcUnpromotedClassSelectAll;
	private Button pcUnpromotedClassSelectNone;
	private Button pcPromotedClassSelectAll;
	private Button pcPromotedClassSelectNone;
	private java.util.List<FE9Data.CharacterClass> selectedPCUnpromotedPool;
	private java.util.List<FE9Data.CharacterClass> bannedPCUnpromotedPool;
	private java.util.List<FE9Data.CharacterClass> bannedPCPromotedPool;
	private java.util.List<FE9Data.CharacterClass> selectedPCPromotedPool;
	private java.util.List<FE9Data.CharacterClass> fullUnpromotedList;
	private java.util.List<FE9Data.CharacterClass> fullPromotedList; 
	
	private Composite pcCharacterComposite;
	private Composite pcUnpromotedComposite;
	private Composite pcPromotedComposite;
	private Composite otherOptionsComposite;
	
	private Button treatSimilarAsSame;
	private Button distributeClassesEvenly;
	private Button allowCrossRace;
	private Button forceClassChange;
	
	public FE9AdvancedClassesView(Composite parent) {
		super(parent, GameType.FE9, true);
		layoutStyle = LayoutStyle.TALL;
		compose();
	}
	
	public FE9AdvancedClassesView(Composite parent, LayoutStyle layoutStyle) {
		super(parent, GameType.FE9, true);
		this.layoutStyle = layoutStyle;
		compose();
	}
	
	@Override
	public String getGroupTitle() {
		return "Player Classes";
	}

	@Override
	public String getGroupTooltip() {
		return "Randomizes classes for playable characters.";
	}

	@Override
	public void initialize(FE9AdvancedClassOptions options) {
		selectedPCs = new ArrayList<FE9Data.Character>();
		ignorePCs = new ArrayList<FE9Data.Character>();
		selectedPCUnpromotedPool = new ArrayList<FE9Data.CharacterClass>();
		selectedPCPromotedPool = new ArrayList<FE9Data.CharacterClass>();
		
		fullPCList = FE9Data.Character.allPlayableCharacters.stream().sorted(characterDisplayNameComparator).filter(character -> character.isModifiable()).toList();
		
		for (FE9Data.Character playableCharacter : fullPCList) {
			pcRandomizeList.add(playableCharacter.getDisplayName());
		}
		
		selectedPCs.addAll(fullPCList);
		
		java.util.List<FE9Data.CharacterClass> classes = FE9Data.CharacterClass.playerEligibleClasses.stream().sorted(classDisplayNameComparator).toList();
		fullUnpromotedList = new ArrayList<FE9Data.CharacterClass>();
		fullPromotedList = new ArrayList<FE9Data.CharacterClass>();
		
		for (FE9Data.CharacterClass charClass : classes) {
			if (charClass.isPromotedClass()) {
				fullPromotedList.add(charClass);
				pcAllowedPromotedClassList.add(charClass.getDisplayName());
			} else {
				fullUnpromotedList.add(charClass);
				pcAllowedUnpromotedClassList.add(charClass.getDisplayName());
			}
		}
		
		selectedPCUnpromotedPool.addAll(fullUnpromotedList);
		selectedPCPromotedPool.addAll(fullPromotedList);
		bannedPCUnpromotedPool = new ArrayList<FE9Data.CharacterClass>();
		bannedPCPromotedPool = new ArrayList<FE9Data.CharacterClass>();
		
		if (options == null) {
			updateEnabled(false);
			enablePC.setSelection(false);
			return;
		}
		
		enablePC.setSelection(options.randomizePlayerCharacters);
		updateEnabled(options.randomizePlayerCharacters);
		treatSimilarAsSame.setSelection(options.treatSimilarAsSame);
		distributeClassesEvenly.setSelection(options.evenDistribution);
		allowCrossRace.setSelection(options.allowCrossRace);
		forceClassChange.setSelection(options.forceChange);
		
		treatSimilarAsSame.setEnabled(options.evenDistribution || options.forceChange);
		
		ignorePCs.clear();
		selectedPCs.clear();
		bannedPCPromotedPool.clear();
		selectedPCUnpromotedPool.clear();
		bannedPCUnpromotedPool.clear();
		selectedPCPromotedPool.clear();
		
		for (FE9Data.Character character : fullPCList) {
			if (options.playerCharactersToRandomize.contains(character)) {
				selectedPCs.add(character);
			} else {
				ignorePCs.add(character);
			}
		}
		
		for (FE9Data.CharacterClass charClass : fullUnpromotedList) {
			if (options.playerTargetClasses.contains(charClass)) {
				selectedPCUnpromotedPool.add(charClass);
			} else {
				bannedPCUnpromotedPool.add(charClass);
			}
		}
		
		for (FE9Data.CharacterClass charClass : fullPromotedList) {
			if (options.playerTargetClasses.contains(charClass)) {
				selectedPCPromotedPool.add(charClass);
			} else {
				bannedPCPromotedPool.add(charClass);
			}
		}
		
		ignorePCs.sort(characterDisplayNameComparator);
		selectedPCs.sort(characterDisplayNameComparator);
		bannedPCPromotedPool.sort(classDisplayNameComparator);
		selectedPCPromotedPool.sort(classDisplayNameComparator);
		bannedPCUnpromotedPool.sort(classDisplayNameComparator);
		selectedPCUnpromotedPool.sort(classDisplayNameComparator);
		
		repopulateRandomizeList();
		repopulateIgnoreList();
		repopulateBannedUnpromotedList();
		repopulateAllowedUnpromotedList();
		repopulateBannedPromotedList();
		repopulateAllowedPromotedList();
	}

	@Override
	public FE9AdvancedClassOptions getOptions() {
		java.util.List<FE9Data.CharacterClass> fullClassList = new ArrayList<FE9Data.CharacterClass>();
		fullClassList.addAll(selectedPCUnpromotedPool);
		fullClassList.addAll(selectedPCPromotedPool);
		return new FE9AdvancedClassOptions(enablePC.getSelection(), selectedPCs, fullClassList,
				treatSimilarAsSame.getSelection(),
				distributeClassesEvenly.getSelection(), 
				forceClassChange.getSelection(), 
				allowCrossRace.getSelection());
	}

	@Override
	protected void compose() {
		enablePC = new Button(group, SWT.CHECK);
		enablePC.setText("Randomize Playable Character Classes");
		enablePC.setToolTipText("Randomizes the classes of all playable characters.");
		enablePC.setEnabled(true);
		enablePC.setSelection(false);
		enablePC.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				updateEnabled(enablePC.getSelection());
				treatSimilarAsSame.setEnabled(enablePC.getSelection() && (distributeClassesEvenly.getSelection() || forceClassChange.getSelection()));
			}
		});
		
		FormData pcData = new FormData();
		pcData.left = new FormAttachment(0, 5);
		pcData.top = new FormAttachment(0, 5);
		enablePC.setLayoutData(pcData);
		
		////////
		
		pcCharacterComposite = new Composite(group, SWT.NONE);
		pcCharacterComposite.setLayout(new FormLayout());
		pcUnpromotedComposite = new Composite(group, SWT.NONE);
		pcUnpromotedComposite.setLayout(new FormLayout());
		pcPromotedComposite = new Composite(group, SWT.NONE);
		pcPromotedComposite.setLayout(new FormLayout());
		
		if (layoutStyle == LayoutStyle.TALL) {
			otherOptionsComposite = new Composite(group, SWT.NONE);
			otherOptionsComposite.setLayout(new FormLayout());
			
			FormData compositeData = new FormData();
			compositeData.left = new FormAttachment(0, 0);
			compositeData.top = new FormAttachment(enablePC, 10);
			compositeData.right = new FormAttachment(100, 0);
			pcCharacterComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(pcCharacterComposite, 10);
			compositeData.left = new FormAttachment(0, 0);
			compositeData.right = new FormAttachment(100, 0);
			pcUnpromotedComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(pcUnpromotedComposite, 10);
			compositeData.left = new FormAttachment(0, 0);
			compositeData.right = new FormAttachment(100, 0);
			pcPromotedComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(pcPromotedComposite, 10);
			compositeData.left = new FormAttachment(0, 0);
			compositeData.right = new FormAttachment(100, 0);
			otherOptionsComposite.setLayoutData(compositeData);
		} else {
			Group otherOptions = new Group(group, SWT.SHADOW_ETCHED_IN);
			otherOptions.setLayout(new FormLayout());
			otherOptions.setText("Options");
			otherOptionsComposite = otherOptions;
			
			FormData compositeData = new FormData();
			compositeData.left = new FormAttachment(0, 0);
			compositeData.top = new FormAttachment(enablePC, 10);
			compositeData.right = new FormAttachment(50, 0);
			pcCharacterComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.left = new FormAttachment(50, 0);
			compositeData.bottom = new FormAttachment(pcCharacterComposite, 0, SWT.BOTTOM);
			compositeData.right = new FormAttachment(100, 0);
			otherOptionsComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(pcCharacterComposite, 10);
			compositeData.left = new FormAttachment(0, 0);
			compositeData.right = new FormAttachment(50, 0);
			pcUnpromotedComposite.setLayoutData(compositeData);
			
			compositeData = new FormData();
			compositeData.top = new FormAttachment(pcCharacterComposite, 10);
			compositeData.left = new FormAttachment(50, 0);
			compositeData.right = new FormAttachment(100, 0);
			pcPromotedComposite.setLayoutData(compositeData);
		}
		
		////////
		
		composePCCharactersView();
		
		////////
		
		composePCUnpromotedClassesView();
		
		////////
		
		composePCPromotedClassesView();
		
		////////
		
		distributeClassesEvenly = new Button(otherOptionsComposite, SWT.CHECK);
		distributeClassesEvenly.setText("Distribute Classes Evenly");
		distributeClassesEvenly.setToolTipText("Attempts to avoid assigning already assigned classes when possible.");
		distributeClassesEvenly.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				treatSimilarAsSame.setEnabled((distributeClassesEvenly.getSelection() || forceClassChange.getSelection()) && enablePC.getSelection());
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 0);
		buttonData.top = new FormAttachment(0, 0);
		distributeClassesEvenly.setLayoutData(buttonData);
		
		allowCrossRace = new Button(otherOptionsComposite, SWT.CHECK);
		allowCrossRace.setText("Allow Cross-race Assignments");
		allowCrossRace.setToolTipText("Allows Beorc units to be assigned Laguz classes (and vice-versa).");
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 0);
		buttonData.top = new FormAttachment(distributeClassesEvenly, 10);
		allowCrossRace.setLayoutData(buttonData);
		
		forceClassChange = new Button(otherOptionsComposite, SWT.CHECK);
		forceClassChange.setText("Force Class Change");
		forceClassChange.setToolTipText("Ensures (where possible) characters are assigned classes that are different from their original classes.");
		forceClassChange.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				treatSimilarAsSame.setEnabled((distributeClassesEvenly.getSelection() || forceClassChange.getSelection()) && enablePC.getSelection());
			}
		});
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 0);
		buttonData.top = new FormAttachment(allowCrossRace, 10);
		forceClassChange.setLayoutData(buttonData);
		
		treatSimilarAsSame = new Button(otherOptionsComposite, SWT.CHECK);
		treatSimilarAsSame.setText("Treat Similar Classes as Same Class");
		treatSimilarAsSame.setToolTipText("Classes with different flavors (i.e. Cavaliers, Paladins, and Sages) or gender (e.g. Male vs.\nFemale Myrmidons) are treated as the same class.\n\nThis only has an effect when distributing classes evenly or forcing class change.");
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 0);
		buttonData.top = new FormAttachment(forceClassChange, 10);
		buttonData.bottom = new FormAttachment(100, 0);
		treatSimilarAsSame.setLayoutData(buttonData);
	}
	
	private void composePCCharactersView() {
		charactersLabel = new Label(pcCharacterComposite, SWT.NONE);
		charactersLabel.setText("Characters");
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.top = new FormAttachment(0, 0);
		charactersLabel.setLayoutData(labelData);
		
		ignoreCharacterLabel = new Label(pcCharacterComposite, SWT.NONE);
		ignoreCharacterLabel.setText("Ignore");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.top = new FormAttachment(charactersLabel, 5);
		labelData.right = new FormAttachment(50, -5);
		ignoreCharacterLabel.setLayoutData(labelData);
		
		randomizeCharacterLabel = new Label(pcCharacterComposite, SWT.NONE);
		randomizeCharacterLabel.setText("Randomize");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(ignoreCharacterLabel, 5);
		labelData.top = new FormAttachment(ignoreCharacterLabel, 0, SWT.TOP);
		labelData.right = new FormAttachment(100, -5);
		randomizeCharacterLabel.setLayoutData(labelData);
		
		pcIgnoreList = new org.eclipse.swt.widgets.List(pcCharacterComposite, SWT.SINGLE);
		pcIgnoreList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				randomizeIgnoreCharacter(pcIgnoreList.getSelectionIndex());
			}
			
		});
		
		
		FormData listData = new FormData();
		listData.left = new FormAttachment(0, 5);
		listData.top = new FormAttachment(ignoreCharacterLabel, 5);
		listData.right = new FormAttachment(50, -5);
		listData.height = 150;
		pcIgnoreList.setLayoutData(listData);
		
		pcRandomizeList = new org.eclipse.swt.widgets.List(pcCharacterComposite, SWT.SINGLE);
		pcRandomizeList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ignoreRandomizeCharacter(pcRandomizeList.getSelectionIndex());
			}
			
		});
		
		listData = new FormData();
		listData.left = new FormAttachment(pcIgnoreList, 5);
		listData.top = new FormAttachment(randomizeCharacterLabel, 5);
		listData.right = new FormAttachment(100, -5);
		listData.height = 150;
		pcRandomizeList.setLayoutData(listData);
		
		pcSelectAll = new Button(pcCharacterComposite, SWT.PUSH);
		pcSelectAll.setText("Select All");
		pcSelectAll.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				ignorePCs.clear();
				selectedPCs.clear();
				selectedPCs.addAll(fullPCList);
				repopulateIgnoreList();
				repopulateRandomizeList();
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(pcRandomizeList, 5);
		buttonData.right = new FormAttachment(100, -5);
		pcSelectAll.setLayoutData(buttonData);
		
		pcSelectNone = new Button(pcCharacterComposite, SWT.PUSH);
		pcSelectNone.setText("Select None");
		pcSelectNone.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				ignorePCs.clear();
				selectedPCs.clear();
				ignorePCs.addAll(fullPCList);
				repopulateIgnoreList();
				repopulateRandomizeList();
			}
		});
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(pcSelectAll, 5);
		buttonData.right = new FormAttachment(100, -5);
		buttonData.bottom = new FormAttachment(100, 0);
		pcSelectNone.setLayoutData(buttonData);
	}
	
	private void composePCUnpromotedClassesView() {
		unpromotedClassesLabel = new Label(pcUnpromotedComposite, SWT.NONE);
		unpromotedClassesLabel.setText("Target Classes (Unpromoted)");
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.top = new FormAttachment(0, 0);
		unpromotedClassesLabel.setLayoutData(labelData);
		
		pcBannedUnpromotedClassListLabel = new Label(pcUnpromotedComposite, SWT.NONE);
		pcBannedUnpromotedClassListLabel.setText("Banned");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.top = new FormAttachment(unpromotedClassesLabel, 5);
		labelData.right = new FormAttachment(50, -5);
		pcBannedUnpromotedClassListLabel.setLayoutData(labelData);
		
		pcAllowedUnpromotedClassListLabel = new Label(pcUnpromotedComposite, SWT.NONE);
		pcAllowedUnpromotedClassListLabel.setText("Allowed");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(pcBannedUnpromotedClassListLabel, 5);
		labelData.top = new FormAttachment(pcBannedUnpromotedClassListLabel, 0, SWT.TOP);
		labelData.right = new FormAttachment(100, -5);
		pcAllowedUnpromotedClassListLabel.setLayoutData(labelData);
		
		pcBannedUnpromotedClassList = new org.eclipse.swt.widgets.List(pcUnpromotedComposite, SWT.SINGLE);
		pcBannedUnpromotedClassList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				allowUnpromotedClass(pcBannedUnpromotedClassList.getSelectionIndex());
			}
		});
		
		FormData listData = new FormData();
		listData.left = new FormAttachment(pcBannedUnpromotedClassListLabel, 0, SWT.LEFT);
		listData.top = new FormAttachment(pcBannedUnpromotedClassListLabel, 5);
		listData.right = new FormAttachment(50, -5);
		listData.height = 150;
		pcBannedUnpromotedClassList.setLayoutData(listData);
		
		pcAllowedUnpromotedClassList = new org.eclipse.swt.widgets.List(pcUnpromotedComposite, SWT.SINGLE);
		pcAllowedUnpromotedClassList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				banUnpromotedClass(pcAllowedUnpromotedClassList.getSelectionIndex());
			}
		});
		
		listData = new FormData();
		listData.left = new FormAttachment(pcBannedUnpromotedClassList, 5);
		listData.top = new FormAttachment(pcAllowedUnpromotedClassListLabel, 5);
		listData.right = new FormAttachment(100, -5);
		listData.height = 150;
		pcAllowedUnpromotedClassList.setLayoutData(listData);
		
		pcUnpromotedClassSelectAll = new Button(pcUnpromotedComposite, SWT.PUSH);
		pcUnpromotedClassSelectAll.setText("Select All");
		pcUnpromotedClassSelectAll.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				bannedPCUnpromotedPool.clear();
				selectedPCUnpromotedPool.clear();
				selectedPCUnpromotedPool.addAll(fullUnpromotedList);
				repopulateBannedUnpromotedList();
				repopulateAllowedUnpromotedList();
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(pcBannedUnpromotedClassList, 5);
		buttonData.right = new FormAttachment(100, -5);
		pcUnpromotedClassSelectAll.setLayoutData(buttonData);
		
		pcUnpromotedClassSelectNone = new Button(pcUnpromotedComposite, SWT.PUSH);
		pcUnpromotedClassSelectNone.setText("Select None");
		pcUnpromotedClassSelectNone.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				bannedPCUnpromotedPool.clear();
				selectedPCUnpromotedPool.clear();
				bannedPCUnpromotedPool.addAll(fullUnpromotedList);
				repopulateBannedUnpromotedList();
				repopulateAllowedUnpromotedList();
			}
		});
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(pcUnpromotedClassSelectAll, 5);
		buttonData.right = new FormAttachment(100, -5);
		pcUnpromotedClassSelectNone.setLayoutData(buttonData);
	}
	
	private void composePCPromotedClassesView() {
		promotedClassesLabel = new Label(pcPromotedComposite, SWT.NONE);
		promotedClassesLabel.setText("Target Classes (Promoted + Laguz)");
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.top = new FormAttachment(0, 0);
		promotedClassesLabel.setLayoutData(labelData);
		
		pcBannedPromotedClassListLabel = new Label(pcPromotedComposite, SWT.NONE);
		pcBannedPromotedClassListLabel.setText("Banned");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.top = new FormAttachment(promotedClassesLabel, 5);
		labelData.right = new FormAttachment(50, -5);
		pcBannedPromotedClassListLabel.setLayoutData(labelData);
		
		pcAllowedPromotedClassListLabel = new Label(pcPromotedComposite, SWT.NONE);
		pcAllowedPromotedClassListLabel.setText("Allowed");
		
		labelData = new FormData();
		labelData.left = new FormAttachment(pcBannedPromotedClassListLabel, 5);
		labelData.top = new FormAttachment(pcBannedPromotedClassListLabel, 0, SWT.TOP);
		labelData.right = new FormAttachment(100, -5);
		pcAllowedPromotedClassListLabel.setLayoutData(labelData);
		
		pcBannedPromotedClassList = new org.eclipse.swt.widgets.List(pcPromotedComposite, SWT.SINGLE);
		pcBannedPromotedClassList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				allowPromotedClass(pcBannedPromotedClassList.getSelectionIndex());
			}
		});
		
		FormData listData = new FormData();
		listData.left = new FormAttachment(pcBannedPromotedClassListLabel, 0, SWT.LEFT);
		listData.top = new FormAttachment(pcBannedPromotedClassListLabel, 5);
		listData.right = new FormAttachment(50, -5);
		listData.height = 150;
		pcBannedPromotedClassList.setLayoutData(listData);
		
		pcAllowedPromotedClassList = new org.eclipse.swt.widgets.List(pcPromotedComposite, SWT.SINGLE);
		pcAllowedPromotedClassList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				banPromotedClass(pcAllowedPromotedClassList.getSelectionIndex());
			}
		});
		
		listData = new FormData();
		listData.left = new FormAttachment(pcBannedPromotedClassList, 5);
		listData.top = new FormAttachment(pcAllowedPromotedClassListLabel, 5);
		listData.right = new FormAttachment(100, -5);
		listData.height = 150;
		pcAllowedPromotedClassList.setLayoutData(listData);
		
		pcPromotedClassSelectAll = new Button(pcPromotedComposite, SWT.PUSH);
		pcPromotedClassSelectAll.setText("Select All");
		pcPromotedClassSelectAll.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				bannedPCPromotedPool.clear();
				selectedPCPromotedPool.clear();
				selectedPCPromotedPool.addAll(fullPromotedList);
				repopulateBannedPromotedList();
				repopulateAllowedPromotedList();
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(pcBannedPromotedClassList, 5);
		buttonData.right = new FormAttachment(100, -5);
		pcPromotedClassSelectAll.setLayoutData(buttonData);
		
		pcPromotedClassSelectNone = new Button(pcPromotedComposite, SWT.PUSH);
		pcPromotedClassSelectNone.setText("Select None");
		pcPromotedClassSelectNone.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				bannedPCPromotedPool.clear();
				selectedPCPromotedPool.clear();
				bannedPCPromotedPool.addAll(fullPromotedList);
				repopulateBannedPromotedList();
				repopulateAllowedPromotedList();
			}
		});
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(pcPromotedClassSelectAll, 5);
		buttonData.right = new FormAttachment(100, -5);
		pcPromotedClassSelectNone.setLayoutData(buttonData);
		
		heronNote = new Label(pcPromotedComposite, SWT.CENTER);
		heronNote.setText("Note: If Heron is selected, a maximum of\none Heron will be assigned.");
		FontData normalFont = heronNote.getFont().getFontData()[0];
		heronNote.setFont(new Font(heronNote.getDisplay(), new FontData(normalFont.getName(), normalFont.getHeight(), SWT.ITALIC)));
		
		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.top = new FormAttachment(pcPromotedClassSelectNone, 5);
		labelData.right = new FormAttachment(100, -5);
		heronNote.setLayoutData(labelData);
	}
	
	private void repopulateIgnoreList() {
		pcIgnoreList.removeAll();
		for (FE9Data.Character playableCharacter : ignorePCs) {
			pcIgnoreList.add(playableCharacter.getDisplayName());
		}
	}
	
	private void repopulateRandomizeList() {
		pcRandomizeList.removeAll();
		for (FE9Data.Character playableCharacter : selectedPCs) {
			pcRandomizeList.add(playableCharacter.getDisplayName());
		}
	}
	
	private void repopulateBannedUnpromotedList() {
		pcBannedUnpromotedClassList.removeAll();
		for (FE9Data.CharacterClass charClass : bannedPCUnpromotedPool) {
			pcBannedUnpromotedClassList.add(charClass.getDisplayName());
		}
	}
	
	private void repopulateAllowedUnpromotedList() {
		pcAllowedUnpromotedClassList.removeAll();
		for (FE9Data.CharacterClass charClass: selectedPCUnpromotedPool) {
			pcAllowedUnpromotedClassList.add(charClass.getDisplayName());
		}
	}
	
	private void repopulateBannedPromotedList() {
		pcBannedPromotedClassList.removeAll();
		for (FE9Data.CharacterClass charClass : bannedPCPromotedPool) {
			pcBannedPromotedClassList.add(charClass.getDisplayName());
		}
	}
	
	private void repopulateAllowedPromotedList() {
		pcAllowedPromotedClassList.removeAll();
		for (FE9Data.CharacterClass charClass: selectedPCPromotedPool) {
			pcAllowedPromotedClassList.add(charClass.getDisplayName());
		}
	}
	
	private void randomizeIgnoreCharacter(int index) {
		FE9Data.Character character = ignorePCs.get(index);
		ignorePCs.remove(index);
		pcIgnoreList.remove(index);
		selectedPCs.add(character);
		selectedPCs.sort(characterDisplayNameComparator);
		int targetIndex = selectedPCs.indexOf(character);
		pcRandomizeList.add(character.getDisplayName(), targetIndex);
	}
	
	private void ignoreRandomizeCharacter(int index) {
		FE9Data.Character character = selectedPCs.get(index);
		selectedPCs.remove(index);
		pcRandomizeList.remove(index);
		ignorePCs.add(character);
		ignorePCs.sort(characterDisplayNameComparator);
		int targetIndex = ignorePCs.indexOf(character);
		pcIgnoreList.add(character.getDisplayName(), targetIndex);
	}
	
	private void allowUnpromotedClass(int index) {
		FE9Data.CharacterClass charClass = bannedPCUnpromotedPool.get(index);
		bannedPCUnpromotedPool.remove(index);
		pcBannedUnpromotedClassList.remove(index);
		selectedPCUnpromotedPool.add(charClass);
		selectedPCUnpromotedPool.sort(classDisplayNameComparator);
		int targetIndex = selectedPCUnpromotedPool.indexOf(charClass);
		pcAllowedUnpromotedClassList.add(charClass.getDisplayName(), targetIndex);
	}
	
	private void banUnpromotedClass(int index) {
		FE9Data.CharacterClass charClass = selectedPCUnpromotedPool.get(index);
		selectedPCUnpromotedPool.remove(index);
		pcAllowedUnpromotedClassList.remove(index);
		bannedPCUnpromotedPool.add(charClass);
		bannedPCUnpromotedPool.sort(classDisplayNameComparator);
		int targetIndex = bannedPCUnpromotedPool.indexOf(charClass);
		pcBannedUnpromotedClassList.add(charClass.getDisplayName(), targetIndex);
	}
	
	private void allowPromotedClass(int index) {
		FE9Data.CharacterClass charClass = bannedPCPromotedPool.get(index);
		bannedPCPromotedPool.remove(index);
		pcBannedPromotedClassList.remove(index);
		selectedPCPromotedPool.add(charClass);
		selectedPCPromotedPool.sort(classDisplayNameComparator);
		int targetIndex = selectedPCPromotedPool.indexOf(charClass);
		pcAllowedPromotedClassList.add(charClass.getDisplayName(), targetIndex);
	}
	
	private void banPromotedClass(int index) {
		FE9Data.CharacterClass charClass = selectedPCPromotedPool.get(index);
		selectedPCPromotedPool.remove(index);
		pcAllowedPromotedClassList.remove(index);
		bannedPCPromotedPool.add(charClass);
		bannedPCPromotedPool.sort(classDisplayNameComparator);
		int targetIndex = bannedPCPromotedPool.indexOf(charClass);
		pcBannedPromotedClassList.add(charClass.getDisplayName(), targetIndex);
	}
	
	@Override
	public boolean validate() {
		if (enablePC.getSelection() == false) { return true; }
		
		if (selectedPCUnpromotedPool.isEmpty() || selectedPCPromotedPool.isEmpty()) {
			return false;
		}
		if (allowCrossRace.getSelection() == false) {
			if (selectedPCPromotedPool.stream().anyMatch(charClass -> charClass.isLaguz()) == false || 
					selectedPCPromotedPool.stream().anyMatch(charClass -> charClass.isBeorc()) == false) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String getValidationError() {
		java.util.List<String> errors = new ArrayList<String>();
		
		if (selectedPCUnpromotedPool.isEmpty()) {
			errors.add("* Randomizing Player Character classes requires at least one unpromoted class selected.");
		}
		if (selectedPCPromotedPool.isEmpty()) {
			errors.add("* Randomizing Player Character classes requires at least one promoted class selected.");
		} else if (allowCrossRace.getSelection() == false) {
			if (selectedPCPromotedPool.stream().anyMatch(charClass -> charClass.isLaguz()) == false) {
				errors.add("* Randomizing Player Character classes without cross-race assignments requires at least one Laguz class selected.");
			}
			if (selectedPCPromotedPool.stream().anyMatch(charClass -> charClass.isBeorc()) == false) {
				errors.add("* Randomizing Player Character classes without cross-race assignments requires at least one promoted Beorc class selected.");
			}
		}
		
		return String.join(System.lineSeparator(), errors);
	}
	
	private void updateEnabled(boolean isEnabled) {
		charactersLabel.setEnabled(isEnabled);
		ignoreCharacterLabel.setEnabled(isEnabled);
		randomizeCharacterLabel.setEnabled(isEnabled);
		pcIgnoreList.setEnabled(isEnabled);
		pcRandomizeList.setEnabled(isEnabled);
		pcSelectAll.setEnabled(isEnabled);
		pcSelectNone.setEnabled(isEnabled);
		
		pcBannedUnpromotedClassListLabel.setEnabled(isEnabled);
		pcBannedUnpromotedClassList.setEnabled(isEnabled);
		pcBannedPromotedClassListLabel.setEnabled(isEnabled);
		pcBannedPromotedClassList.setEnabled(isEnabled);
		pcAllowedUnpromotedClassListLabel.setEnabled(isEnabled);
		pcAllowedUnpromotedClassList.setEnabled(isEnabled);
		pcAllowedPromotedClassListLabel.setEnabled(isEnabled);
		pcAllowedPromotedClassList.setEnabled(isEnabled);
		pcUnpromotedClassSelectAll.setEnabled(isEnabled);
		pcUnpromotedClassSelectNone.setEnabled(isEnabled);
		pcPromotedClassSelectAll.setEnabled(isEnabled);
		pcPromotedClassSelectNone.setEnabled(isEnabled);
		
		unpromotedClassesLabel.setEnabled(isEnabled);
		promotedClassesLabel.setEnabled(isEnabled);
		
		distributeClassesEvenly.setEnabled(isEnabled);
		allowCrossRace.setEnabled(isEnabled);
		forceClassChange.setEnabled(isEnabled);
		treatSimilarAsSame.setEnabled(isEnabled);
	}
	
	private static final Comparator<FE9Data.Character> characterDisplayNameComparator = new Comparator<FE9Data.Character>() {
		@Override
		public int compare(FE9Data.Character arg0, FE9Data.Character arg1) {
			return arg0.getDisplayName().compareTo(arg1.getDisplayName());
		}
	};
	
	private static final Comparator<FE9Data.CharacterClass> classDisplayNameComparator = new Comparator<FE9Data.CharacterClass>() {
		@Override
		public int compare(CharacterClass o1, CharacterClass o2) {
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
	};
}
