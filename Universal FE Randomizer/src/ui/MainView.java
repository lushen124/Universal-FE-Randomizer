package ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import application.Main;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gcnwii.fe9.FE9Data;
import fedata.general.FEBase.GameType;
import fedata.snes.fe4.FE4Data;
import io.FileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gba.randomizer.AbstractGBARandomizer;
import random.gcnwii.fe9.randomizer.FE9Randomizer;
import random.general.Randomizer;
import random.general.RandomizerListener;
import random.snes.fe4.randomizer.FE4Randomizer;
import ui.fe4.FE4ClassesView;
import ui.fe4.FE4EnemyBuffView;
import ui.fe4.FE4PromotionView;
import ui.fe4.HolyBloodView;
import ui.fe4.SkillsView;
import ui.fe9.FE9ClassesView;
import ui.fe9.FE9EnemyBuffView;
import ui.fe9.FE9SkillView;
import ui.general.FileFlowDelegate;
import ui.general.MessageModal;
import ui.general.ModalButtonListener;
import ui.general.OpenFileFlow;
import ui.general.ProgressModal;
import util.DebugListener;
import util.DebugPrinter;
import util.DiffCompiler;
import util.OptionRecorder;
import util.OptionRecorder.FE4OptionBundle;
import util.OptionRecorder.FE9OptionBundle;
import util.OptionRecorder.GBAOptionBundle;
import util.SeedGenerator;
import util.recordkeeper.ChangelogBuilder;
import util.recordkeeper.RecordKeeper;

public class MainView implements FileFlowDelegate {
	
	public Shell mainShell;
	
	private Shell consoleShell;
	private Table consoleLog;
	private boolean consoleShellOpened;
	
	private ScrolledComposite scrollable;
	private Composite container;
	
	private ControlListener resizeListener;
	
	private int screenHeight;
	
	private Text filenameField;
	
	private Label seedLabel;
	private Text seedField;
	private Button generateButton;
	
	private GameType loadedGameType = GameType.UNKNOWN;
	private Boolean hasLoadedInfo = false;
	
	private boolean patchingAvailable = false;
	
	private Group romInfoGroup;
	private Label romName;
	private Label romCode;
	private Label friendlyName;
	
	private Label length;
	private Label checksum;
	
	private GrowthsView growthView;
	private BasesView baseView;
	private ClassesView classView;
	private MOVCONAffinityView otherCharOptionView;
	private WeaponsView weaponView;
	private EnemyBuffsView enemyView; 
	private MiscellaneousView miscView;
	private RecruitmentView recruitView;
	private ItemAssignmentView itemAssignmentView;
	private CharacterShufflingView characterShufflingView;
	
	// FE4
	private SkillsView skillsView;
	private HolyBloodView holyBloodView;
	private FE4ClassesView fe4ClassView;
	private FE4PromotionView fe4PromotionView;
	private FE4EnemyBuffView fe4EnemyBuffView;
	
	// FE9
	private FE9SkillView fe9SkillView;
	private CONAffinityView conAffinityView;
	private FE9EnemyBuffView fe9EnemyView;
	private FE9ClassesView fe9ClassesView;
	
	private Button randomizeButton;
	
	private Boolean isShowingModalProgressDialog = false;
	private ProgressModal progressBox;
	
	public MainView(Display mainDisplay) {
		super();
		
		Shell shell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX); 
		 shell.setText("Yune: A Universal Fire Emblem Randomizer (v0.9.3)");
		 shell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
		 
		 screenHeight = mainDisplay.getBounds().height;
		 for (Monitor monitor : mainDisplay.getMonitors()) {
			 screenHeight = Math.max(screenHeight, monitor.getClientArea().height);
		 }
		 
		 screenHeight -= 20;
		 
		 mainShell = shell;
		 
		 setupMainShell();
		 
		 resize();
		 
		 /* Open shell window */
		  mainShell.open();
		  
		  mainDisplay.addFilter(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (((event.stateMask & SWT.CTRL) != 0) && ((event.stateMask & SWT.SHIFT) != 0) && (event.keyCode == 'c') && !consoleShellOpened) {
					openConsole();
				}
			}
		  });
	}
	
	private void resize() {
		mainShell.layout();
		container.layout();
		int titleBarHeight = mainShell.getBounds().height - mainShell.getClientArea().height;
		Point containerSize = container.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		// For some reason, in debug, everything works fine, but when exporting to JAR,
		// the right margin is off (maybe due to different JREs?) The +10 is to make sure the
		// JAR being run is shown correctly.
		Point actualSize = new Point(containerSize.x + 10, Math.min(containerSize.y + titleBarHeight, screenHeight));
		
		final Point contentSize = actualSize;
		
		if (actualSize.y - titleBarHeight < containerSize.y) {
			ScrollBar verticalScrollBar = scrollable.getVerticalBar();
			FormLayout containerLayout = (FormLayout)container.getLayout();
			containerLayout.marginRight = verticalScrollBar.getSize().x + 5;

			mainShell.layout();
			container.layout();
			containerSize = container.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			actualSize = new Point(containerSize.x + 10, Math.min(containerSize.y + (mainShell.getBounds().height - mainShell.getClientArea().height), screenHeight));
		}
		
		// On Ubuntu, 44 is the size of the natural container size, so we'll give some additional margin too.
		if (containerSize.y < 50) {
			mainShell.setMinimumSize(containerSize.x + 10, 0);
		} else {
			mainShell.setMinimumSize(containerSize.x + 10, 300);
		}
		
		if (resizeListener != null) { mainShell.removeControlListener(resizeListener); }
		
		resizeListener = new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {}
			@Override
			public void controlResized(ControlEvent e) {
				Point size = mainShell.getSize();
				if (contentSize.y < 50) { return; }
				if (size.y >= screenHeight) { return; } // This is to allow Full screen to work on Mac OS.
				if (size.y > contentSize.y || size.x > contentSize.x) {
					mainShell.setSize(contentSize.x, contentSize.y);
				}
			}
		};
		
		mainShell.addControlListener(resizeListener);
		
		container.setSize(containerSize);
		mainShell.setSize(actualSize);
		
		FormData scrollableData = new FormData();
		scrollableData.top = new FormAttachment(0, 0);
		scrollableData.left = new FormAttachment(0, 0);
		scrollableData.right = new FormAttachment(100, 0);
		scrollableData.bottom = new FormAttachment(100, 0);
		scrollableData.width = actualSize.x;
		scrollableData.height = actualSize.y;
		scrollable.setLayoutData(scrollableData);
	}
	
	public void showModalProgressDialog() {
		if (!isShowingModalProgressDialog) {
			isShowingModalProgressDialog = true;
			progressBox = new ProgressModal(mainShell, "Randomizing...");
			progressBox.progressBar.setMinimum(0);
			progressBox.progressBar.setMaximum(100);
			progressBox.show();
		}
	}
	
	public void hideModalProgressDialog() {
		if (isShowingModalProgressDialog) {
			isShowingModalProgressDialog = false;
			progressBox.hide();
			progressBox = null;
		}
	}

	public void setupMainShell() {		  
		mainShell.setLayout(new FillLayout());
		  
		scrollable = new ScrolledComposite(mainShell, SWT.V_SCROLL);

		container = new Composite(scrollable, SWT.NONE);

		scrollable.setContent(container);

		FormLayout containerLayout = new FormLayout();
		containerLayout.marginLeft = 5;
		containerLayout.marginRight = 5;
		containerLayout.marginHeight = 5;
		container.setLayout(containerLayout);

		/* Define widgets to add to the shell */
		Label romFileLabel = new Label(container, 0);
		romFileLabel.setText("ROM File:");

		Text field = new Text(container, SWT.BORDER);
		field.setEditable(false);
		filenameField = field;

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addListener(SWT.Selection, new OpenFileFlow(mainShell, this));

		FormData labelData = new FormData();
		labelData.left = new FormAttachment(container, 5);
		labelData.top = new FormAttachment(field, 0, SWT.CENTER);
		romFileLabel.setLayoutData(labelData);

		FormData fieldData = new FormData();
		fieldData.left = new FormAttachment(romFileLabel, 5);
		fieldData.top = new FormAttachment(0, 5);
		fieldData.right = new FormAttachment(button, -5);
		fieldData.width = 400;
		field.setLayoutData(fieldData);

		FormData buttonData = new FormData();
		buttonData.right = new FormAttachment(100, -5);
		buttonData.top = new FormAttachment(field, 0, SWT.CENTER);
		buttonData.width = 100;
		button.setLayoutData(buttonData);
	}
	
	private void openConsole() {
		Display mainDisplay = Display.getDefault();
		consoleShell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX);
		consoleShell.setText("Debug Console");
		consoleShell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
		setupConsoleShell();
		consoleShell.open();
		consoleShellOpened = true;
		
		consoleShell.addShellListener(new ShellListener() {
			
			@Override
			public void shellIconified(ShellEvent e) {
			}
			
			@Override
			public void shellDeiconified(ShellEvent e) {
			}
			
			@Override
			public void shellDeactivated(ShellEvent e) {
			}
			
			@Override
			public void shellClosed(ShellEvent e) {
				DebugPrinter.unregisterListener("consoleLog");
				consoleShellOpened = false;
			}
			
			@Override
			public void shellActivated(ShellEvent e) {
			}
		});
	}
	
	private void setupConsoleShell() {
		consoleShell.setLayout(new FillLayout());
		consoleShell.setSize(400, 400);
		
		consoleLog = new Table(consoleShell, SWT.BORDER | SWT.FULL_SELECTION);
		consoleLog.setHeaderVisible(true);
		consoleLog.setLinesVisible(true);
		consoleLog.setSize(400, 400);
		
		TableColumn categoryColumn = new TableColumn(consoleLog, SWT.NONE);
		categoryColumn.setText("Namespace");
		categoryColumn.pack();
		TableColumn messageColumn = new TableColumn(consoleLog, SWT.NONE);
		messageColumn.setText("Message");
		
		consoleShell.addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				consoleLog.setSize(consoleShell.getSize());
				messageColumn.setWidth(consoleLog.getSize().x - categoryColumn.getWidth());
			}
			@Override
			public void controlMoved(ControlEvent e) {	
			}
		});
		
		DebugPrinter.registerListener(new DebugListener() {
			@Override
			public void logMessage(String category, String message) {
				try {
					TableItem newItem = new TableItem(consoleLog, SWT.NONE);
					newItem.setText(0, category);
					newItem.setText(1, message);
				} catch (SWTException e) {
					e.printStackTrace();
				}
			}
		}, "consoleLog");
	}
	
	private void setupInfoLayout() {
		romInfoGroup = new Group(container, SWT.NONE);
		romInfoGroup.setText("ROM Info");
		romInfoGroup.setVisible(false);
		  
		FormData infoGroupData = new FormData();
		infoGroupData.left = new FormAttachment(0, 5);
		infoGroupData.right = new FormAttachment(100, -5);
		infoGroupData.top = new FormAttachment(filenameField, 5);
		romInfoGroup.setLayoutData(infoGroupData);
		  
		FillLayout infoLayout = new FillLayout();
		infoLayout.type = SWT.VERTICAL;
		romInfoGroup.setLayout(infoLayout);
		  
		Composite topInfo = new Composite(romInfoGroup, 0);
		topInfo.setLayout(new FillLayout());
		  
		Composite bottomInfo = new Composite(romInfoGroup, 0);
		bottomInfo.setLayout(new FillLayout());
		  
		romName = new Label(topInfo, SWT.LEFT);
		romCode = new Label(topInfo, SWT.LEFT);
		friendlyName = new Label(topInfo, SWT.LEFT);
		length = new Label(bottomInfo, SWT.LEFT);
		checksum = new Label(bottomInfo, SWT.LEFT);
		new Label(bottomInfo, SWT.LEFT); // Spacer
	}
	
	private void disposeRandomizationOptionsViews() {
		if (growthView != null) { growthView.dispose(); }
		if (baseView != null) { baseView.dispose(); }
		if (otherCharOptionView != null) { otherCharOptionView.dispose(); }
		if (weaponView != null) { weaponView.dispose(); }
		if (classView != null) { classView.dispose(); }
		if (enemyView != null) { enemyView.dispose(); }
		if (miscView != null) { miscView.dispose(); }
		if (recruitView != null) { recruitView.dispose(); }
		if (characterShufflingView != null) { characterShufflingView.dispose(); }
		if (itemAssignmentView != null) { itemAssignmentView.dispose(); }
		if (randomizeButton != null) { randomizeButton.dispose(); }
		
		if (seedField != null) { seedField.dispose(); }
		if (generateButton != null) { generateButton.dispose(); }
		if (seedLabel != null) { seedLabel.dispose(); }
		
		if (skillsView != null) { skillsView.dispose(); }
		if (holyBloodView != null) { holyBloodView.dispose(); }
		if (fe4ClassView != null) { fe4ClassView.dispose(); }
		if (fe4PromotionView != null) { fe4PromotionView.dispose(); }
		if (fe4EnemyBuffView != null) { fe4EnemyBuffView.dispose(); }
		
		if (fe9SkillView != null) { fe9SkillView.dispose(); }
		if (conAffinityView != null) { conAffinityView.dispose(); }
		if (fe9EnemyView != null) { fe9EnemyView.dispose(); }
		if (fe9ClassesView != null) { fe9ClassesView.dispose(); }
		
		resize();
	}
	
	private void preloadOptions(GameType type) {
		if (OptionRecorder.options == null) { return; }
		
		if (type == GameType.FE4 && OptionRecorder.options.fe4 != null) {
			FE4OptionBundle bundle = OptionRecorder.options.fe4;
			growthView.setGrowthOptions(bundle.growths);
			baseView.setBasesOptions(bundle.bases);
			holyBloodView.setHolyBloodOptions(bundle.holyBlood);
			miscView.setMiscellaneousOptions(bundle.misc);
			skillsView.setSkillOptions(bundle.skills);
			fe4ClassView.setClassOptions(bundle.classes);
			fe4PromotionView.setPromotionOptions(bundle.promo);
			fe4EnemyBuffView.setBuffOptions(bundle.enemyBuff);
		} else if (type.isGBA()) { 
			GBAOptionBundle bundle = null;
			if (type == GameType.FE6) { bundle = OptionRecorder.options.fe6; }
			else if (type == GameType.FE7) { bundle = OptionRecorder.options.fe7; }
			else if (type == GameType.FE8) { bundle = OptionRecorder.options.fe8; }
			if (bundle != null) {
				growthView.setGrowthOptions(bundle.growths);
				baseView.setBasesOptions(bundle.bases);
				otherCharOptionView.setOtherCharacterOptions(bundle.other);
				weaponView.setWeaponOptions(bundle.weapons);
				classView.setClassOptions(bundle.classes);
				enemyView.setEnemyOptions(bundle.enemies);
				miscView.setMiscellaneousOptions(bundle.otherOptions);		
				recruitView.setRecruitmentOptions(bundle.recruitmentOptions);
				characterShufflingView.setShufflingOptions(bundle.characterShufflingOptions, type);
				itemAssignmentView.setItemAssignmentOptions(bundle.itemAssignmentOptions);
			}
		} else if (type == GameType.FE9 && OptionRecorder.options.fe9 != null) {
			FE9OptionBundle bundle = OptionRecorder.options.fe9;
			growthView.setGrowthOptions(bundle.growths);
			baseView.setBasesOptions(bundle.bases);
			fe9SkillView.setSkillOptions(bundle.skills);
			conAffinityView.setOtherCharacterOptions(bundle.otherOptions);
			fe9EnemyView.setEnemyBuffOptions(bundle.enemyBuff);
			fe9ClassesView.setClassOptions(bundle.classes);
			weaponView.setWeaponOptions(bundle.weapons);
			miscView.setMiscellaneousOptions(bundle.misc);
		}
	}
	
	private void updateLayoutForGameType(GameType type) {
		
		disposeRandomizationOptionsViews();
		
		if (type == GameType.UNKNOWN) {
			return;
		}
		
		seedField = new Text(container, SWT.BORDER);
		seedField.addListener(SWT.CHANGED, new Listener() {
			@Override
			public void handleEvent(Event event) {
				randomizeButton.setEnabled(seedField.getText().length() > 0);
			}
		});
		Button button = new Button(container, SWT.PUSH);
		button.setText("Generate");
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				seedField.setText(SeedGenerator.generateRandomSeed());
				randomizeButton.setEnabled(seedField.getText().length() > 0);
			}
		});
		generateButton = button;
		  
		seedLabel = new Label(container, SWT.NONE);
		seedLabel.setText("Randomizer Seed Phrase: ");
		  
		seedField.setVisible(false);
		generateButton.setVisible(false);
		seedLabel.setVisible(false);
		  
		FormData seedFieldData = new FormData();
		seedFieldData.top = new FormAttachment(romInfoGroup, 10);
		seedFieldData.right = new FormAttachment(button, -5);
		seedFieldData.left = new FormAttachment(seedLabel, 5);
		seedField.setLayoutData(seedFieldData);
		  
		FormData seedLabelData = new FormData();
		seedLabelData.top = new FormAttachment(seedField, 0, SWT.CENTER);
		seedLabelData.left = new FormAttachment(romInfoGroup, 0, SWT.LEFT);
		seedLabel.setLayoutData(seedLabelData);
		  
		FormData generateData = new FormData();
		generateData.top = new FormAttachment(seedField, 0, SWT.CENTER);
		generateData.right = new FormAttachment(100, -5);
		generateData.width = 100;
		button.setLayoutData(generateData);
		
		growthView = new GrowthsView(container, SWT.NONE, type.hasSTRMAGSplit());
		growthView.setSize(200, 200);
		growthView.setVisible(false);
		  
		FormData growthData = new FormData();
		growthData.top = new FormAttachment(seedField, 10);
		growthData.left = new FormAttachment(romInfoGroup, 0, SWT.LEFT);
		growthView.setLayoutData(growthData);
		  
		baseView = new BasesView(container, SWT.NONE, type);
		baseView.setSize(200, 200);
		baseView.setVisible(false);
		  
		FormData baseData = new FormData();
		baseData.top = new FormAttachment(growthView, 5);
		baseData.left = new FormAttachment(growthView, 0, SWT.LEFT);
		baseData.right = new FormAttachment(growthView, 0, SWT.RIGHT);
		baseView.setLayoutData(baseData);
		
		randomizeButton = new Button(container, SWT.PUSH);
		randomizeButton.setText("Randomize!");
		randomizeButton.setVisible(false);
		  
		if (type == GameType.FE4) {
			// To prevent gen 2 overflow, the max growth allowed for any single stat is 85%.
			growthView.overrideMaxGrowthAllowed(85);
			
			holyBloodView = new HolyBloodView(container, SWT.NONE);
			holyBloodView.setSize(200, 200);
			holyBloodView.setVisible(false);
			  
			FormData holyBloodData = new FormData();
			holyBloodData.top = new FormAttachment(baseView, 5);
			holyBloodData.left = new FormAttachment(baseView, 0, SWT.LEFT);
			holyBloodData.right = new FormAttachment(baseView, 0, SWT.RIGHT);
			holyBloodData.bottom = new FormAttachment(100, -10);
			holyBloodView.setLayoutData(holyBloodData);
			
			skillsView = new SkillsView(container, SWT.NONE);
			skillsView.setSize(200, 200);
			skillsView.setVisible(false);
			
			FormData skillsData = new FormData();
			skillsData.top = new FormAttachment(growthView, 0, SWT.TOP);
			skillsData.left = new FormAttachment(growthView, 5);
			skillsData.bottom = new FormAttachment(100, -10);
			skillsView.setLayoutData(skillsData);
			
			fe4ClassView = new FE4ClassesView(container, SWT.NONE);
			fe4ClassView.setSize(200, 200);
			fe4ClassView.setVisible(false);
			  
			FormData classData = new FormData();
			classData.top = new FormAttachment(skillsView, 0, SWT.TOP);
			classData.left = new FormAttachment(skillsView, 5);
			classData.bottom = new FormAttachment(100, -10);
			fe4ClassView.setLayoutData(classData);
			
			fe4PromotionView = new FE4PromotionView(container, SWT.NONE);
			fe4PromotionView.setSize(200, 200);
			fe4PromotionView.setVisible(false);
			
			FormData promoData = new FormData();
			promoData.top = new FormAttachment(fe4ClassView, 0, SWT.TOP);
			promoData.left = new FormAttachment(fe4ClassView, 5);
			promoData.right = new FormAttachment(100, -5);
			fe4PromotionView.setLayoutData(promoData);
			
			fe4EnemyBuffView = new FE4EnemyBuffView(container, SWT.NONE);
			fe4EnemyBuffView.setSize(200, 200);
			fe4EnemyBuffView.setVisible(false);
			
			FormData buffData = new FormData();
			buffData.top = new FormAttachment(fe4PromotionView, 5);
			buffData.left = new FormAttachment(fe4PromotionView, 0, SWT.LEFT);
			buffData.right = new FormAttachment(fe4PromotionView, 0, SWT.RIGHT);
			fe4EnemyBuffView.setLayoutData(buffData);
			
			miscView = new MiscellaneousView(container, SWT.NONE, type);
			miscView.setSize(200, 200);
			miscView.setVisible(false);
			  
			FormData miscData = new FormData();
			miscData.top = new FormAttachment(fe4EnemyBuffView, 5);
			miscData.left = new FormAttachment(fe4EnemyBuffView, 0, SWT.LEFT);
			miscData.right = new FormAttachment(fe4EnemyBuffView, 0, SWT.RIGHT);
			//miscData.bottom = new FormAttachment(100, -10);
			miscView.setLayoutData(miscData);
			  
			FormData randomizeData = new FormData();
			randomizeData.top = new FormAttachment(miscView, 5);
			randomizeData.left = new FormAttachment(miscView, 0, SWT.LEFT);
			randomizeData.right = new FormAttachment(miscView, 0, SWT.RIGHT);
			randomizeData.bottom = new FormAttachment(100, -10);
			randomizeButton.setLayoutData(randomizeData);
			
		} else if (type == GameType.FE9) {
			conAffinityView = new CONAffinityView(container, SWT.NONE);
			conAffinityView.setSize(200, 200);
			conAffinityView.setVisible(false);
			
			FormData conAffinityData = new FormData();
			conAffinityData.top = new FormAttachment(baseView, 5);
			conAffinityData.left = new FormAttachment(baseView, 0, SWT.LEFT);
			conAffinityData.right = new FormAttachment(baseView, 0, SWT.RIGHT);
			conAffinityView.setLayoutData(conAffinityData);
			
			miscView = new MiscellaneousView(container, SWT.NONE, type);
			miscView.setSize(200, 200);
			miscView.setVisible(false);
			  
			FormData miscData = new FormData();
			miscData.top = new FormAttachment(conAffinityView, 5);
			miscData.left = new FormAttachment(conAffinityView, 0, SWT.LEFT);
			miscData.right = new FormAttachment(conAffinityView, 0, SWT.RIGHT);
			//miscData.bottom = new FormAttachment(100, -10);
			miscView.setLayoutData(miscData);
			
			List<String> skills = FE9Data.Skill.allValidSkills.stream().map( skill -> {
				return skill.getDisplayString();
			}).collect(Collectors.toList());
			fe9SkillView = new FE9SkillView(container, SWT.NONE, skills);
			fe9SkillView.setSize(200, 200);
			fe9SkillView.setVisible(false);
			
			FormData skillData = new FormData();
			skillData.top = new FormAttachment(growthView, 0, SWT.TOP);
			skillData.left = new FormAttachment(growthView, 5);
			skillData.bottom = new FormAttachment(100, -10);
			fe9SkillView.setLayoutData(skillData);
			
			weaponView = new WeaponsView(container, SWT.NONE, type);
			weaponView.setSize(200, 200);
			weaponView.setVisible(false);
		  
			FormData weaponData = new FormData();
			weaponData.top = new FormAttachment(growthView, 0, SWT.TOP);
			weaponData.left = new FormAttachment(fe9SkillView, 5);
			weaponView.setLayoutData(weaponData);
			
			fe9ClassesView = new FE9ClassesView(container, SWT.NONE);
			fe9ClassesView.setSize(200, 200);
			fe9ClassesView.setVisible(false);
			
			FormData classData = new FormData();
			classData.top = new FormAttachment(growthView, 0, SWT.TOP);
			classData.left = new FormAttachment(weaponView, 5);
			classData.right = new FormAttachment(100, -5);
			fe9ClassesView.setLayoutData(classData);
			
			fe9EnemyView = new FE9EnemyBuffView(container, SWT.NONE);
			fe9EnemyView.setSize(200, 200);
			fe9EnemyView.setVisible(false);
			
			FormData enemyData = new FormData();
			enemyData.top = new FormAttachment(fe9ClassesView, 5); 
			enemyData.left = new FormAttachment(fe9ClassesView, 0, SWT.LEFT);
			enemyData.right = new FormAttachment(100, -5);
			fe9EnemyView.setLayoutData(enemyData);
			
			FormData randomizeData = new FormData();
			randomizeData.top = new FormAttachment(fe9EnemyView, 5);
			randomizeData.left = new FormAttachment(fe9EnemyView, 0, SWT.LEFT);
			randomizeData.right = new FormAttachment(fe9EnemyView, 0, SWT.RIGHT);
			randomizeData.bottom = new FormAttachment(100, -10);
			randomizeButton.setLayoutData(randomizeData);
		} else {
			otherCharOptionView = new MOVCONAffinityView(container, SWT.NONE);
			otherCharOptionView.setSize(200, 200);
			otherCharOptionView.setVisible(false);
			  
			FormData otherData = new FormData();
			otherData.top = new FormAttachment(baseView, 5);
			otherData.left = new FormAttachment(baseView, 0, SWT.LEFT);
			otherData.right = new FormAttachment(baseView, 0, SWT.RIGHT);
			otherCharOptionView.setLayoutData(otherData);
			
			miscView = new MiscellaneousView(container, SWT.NONE, type);
			miscView.setSize(200, 200);
			miscView.setVisible(false);
			
			FormData miscData = new FormData();
			miscData.top = new FormAttachment(otherCharOptionView, 5);
			miscData.left = new FormAttachment(otherCharOptionView, 0, SWT.LEFT);
			miscData.right = new FormAttachment(otherCharOptionView, 0, SWT.RIGHT);
			miscData.bottom = new FormAttachment(100, -10);
			miscView.setLayoutData(miscData);
			
			weaponView = new WeaponsView(container, SWT.NONE, type);
			weaponView.setSize(200, 200);
			weaponView.setVisible(false);
		  
			FormData weaponData = new FormData();
			weaponData.top = new FormAttachment(growthView, 0, SWT.TOP);
			weaponData.left = new FormAttachment(growthView, 5);
			weaponData.bottom = new FormAttachment(100, -10);
			weaponView.setLayoutData(weaponData);
			
			classView = new ClassesView(container, SWT.NONE, type);
			classView.setSize(200, 200);
			classView.setVisible(false);
			  
			FormData classData = new FormData();
			classData.top = new FormAttachment(weaponView, 0, SWT.TOP);
			classData.left = new FormAttachment(weaponView, 5);
			classView.setLayoutData(classData);
			
			enemyView = new EnemyBuffsView(container, SWT.NONE);
			enemyView.setSize(200, 200);
			enemyView.setVisible(false);
			  
			FormData enemyData = new FormData();
			enemyData.top = new FormAttachment(classView, 5);
			enemyData.left = new FormAttachment(classView, 0, SWT.LEFT);
			enemyData.right = new FormAttachment(classView, 0, SWT.RIGHT);
			enemyData.bottom = new FormAttachment(100, -10);
			enemyView.setLayoutData(enemyData);
			
			recruitView = new RecruitmentView(container, SWT.NONE, type);
			recruitView.setSize(200, 200);
			recruitView.setVisible(false);
			
			FormData recruitData = new FormData();
			recruitData.top = new FormAttachment(classView, 0, SWT.TOP);
			recruitData.left = new FormAttachment(classView, 5);
			recruitView.setLayoutData(recruitData);
			
			characterShufflingView = new CharacterShufflingView(container, SWT.NONE, type);
			characterShufflingView.setSize(200, 200);
			characterShufflingView.setVisible(false);
			
			FormData characterShufflingData = new FormData();
			characterShufflingData.top = new FormAttachment(recruitView, 0, SWT.TOP);
			characterShufflingData.left = new FormAttachment(recruitView, 5);
			characterShufflingData.right = new FormAttachment(100, 0);
			characterShufflingView.setLayoutData(characterShufflingData);
			
			itemAssignmentView = new ItemAssignmentView(container, SWT.NONE, type);
			itemAssignmentView.setSize(200, 200);
			itemAssignmentView.setVisible(false);
			
			FormData itemAssignData = new FormData();
			itemAssignData.top = new FormAttachment(characterShufflingView, 5);
			itemAssignData.left = new FormAttachment(characterShufflingView, 0, SWT.LEFT);
			itemAssignData.right = new FormAttachment(characterShufflingView, 0, SWT.RIGHT);
			itemAssignmentView.setLayoutData(itemAssignData);
			  
			FormData randomizeData = new FormData();
			randomizeData.top = new FormAttachment(itemAssignmentView, 5);
			randomizeData.left = new FormAttachment(itemAssignmentView, 0, SWT.LEFT);
			randomizeData.right = new FormAttachment(itemAssignmentView, 0, SWT.RIGHT);
			randomizeData.bottom = new FormAttachment(100, -10);
			randomizeButton.setLayoutData(randomizeData);
		}
		
		resize();
	}

	@Override
	public void onSelectedFile(String pathToFile) {
		if (pathToFile != null) {
			filenameField.setText(pathToFile);
		} else {
			return;
		}
		
		if (!hasLoadedInfo) {
			setupInfoLayout();
			hasLoadedInfo = true;
		}
		
		MessageModal loadingModal = new MessageModal(mainShell, "Loading", "Verifying File...");
		loadingModal.showRaw();
		
		try {
			FileHandler handler = new FileHandler(pathToFile);
			byte [] result = handler.readBytesAtOffset(0xA0, 12);
			String gameTitle = new String(result, StandardCharsets.US_ASCII);
			romName.setText("ROM Name: " + gameTitle);
			
			result = handler.readBytesAtOffset(0xAC, 4);
			String gameCode = new String(result, StandardCharsets.US_ASCII);
			romCode.setText("ROM Code: " + gameCode);
			
			length.setText("File Length: " + handler.getFileLength());
			checksum.setText("CRC-32: " + Long.toHexString(handler.getCRC32()).toUpperCase());
			
			GameType type = loadedGameType;
			patchingAvailable = false;
			if (handler.getCRC32() == FE6Data.CleanCRC32) { 
				type = GameType.FE6;
				patchingAvailable = true;
				friendlyName.setText("Display Name: " + FE6Data.FriendlyName);
			}
			else if (handler.getCRC32() == FE7Data.CleanCRC32) { 
				type = GameType.FE7;
				friendlyName.setText("Display Name: " + FE7Data.FriendlyName);
			}
			else if (handler.getCRC32() == FE8Data.CleanCRC32) {
				type = GameType.FE8;
				friendlyName.setText("Display Name: " + FE8Data.FriendlyName);
			}
			else if (handler.getCRC32() == FE4Data.CleanHeaderedCRC32 || handler.getCRC32() == FE4Data.CleanUnheaderedCRC32) {
				type = GameType.FE4;
				patchingAvailable = true;
				friendlyName.setText("Display Name: " + FE4Data.FriendlyName);
				romName.setText("ROM Name: " + FE4Data.InternalName);
				romCode.setText("ROM Code: --");
			}
			else if (handler.getCRC32() == FE9Data.CleanCRC32) {
				type = GameType.FE9;
				friendlyName.setText("Display Name: " + FE9Data.FriendlyName);
				try {
					GCNISOHandler gcnHandler = new GCNISOHandler(handler);
					romName.setText("ROM Name: " + gcnHandler.getGameName());
					romCode.setText("ROM Code: " + gcnHandler.getGameCode());
				} catch (GCNISOException e) {
					DebugPrinter.log(DebugPrinter.Key.MAIN, e.getMessage());
					romName.setText("ROM Name: Read Failed");
					romCode.setText("ROM Code: Read Failed");
					type = GameType.UNKNOWN;
				}
			}
			else { 
				type = GameType.UNKNOWN;
				friendlyName.setText("Display Name: Unknown");
			}
			
			if (type != GameType.UNKNOWN) {
				loadGameType(type, pathToFile, handler);
				loadingModal.hide();
			} else {
				loadingModal.hide();
				
				MessageModal checksumFailure = new MessageModal(mainShell, "Unrecognized Checksum", "Yune was unable to determine the game from the file selected.\n"
						+ "If you know the game for the file, you may select it below.\n\nNote: Patching cannot be guaranteed, and is therefore, disabled.\n\n"
						+ "Warning: Be aware that this file is likely untested and may cause errors.\n" 
						+ "There will be very limited support for issues from randomizing this file.");
				ModalButtonListener fe4Selection = new ModalButtonListener() {
					@Override
					public void onSelected() {
						loadGameType(GameType.FE4, pathToFile, handler);
						friendlyName.setText("Display Name: (Unverified) Fire Emblem: Genealogy of the Holy War");
					}
				};
				ModalButtonListener fe6Selection = new ModalButtonListener() {
					@Override
					public void onSelected() {
						loadGameType(GameType.FE6, pathToFile, handler);
						friendlyName.setText("Display Name: (Unverified) Fire Emblem: Binding Blade");
					}
				};
				ModalButtonListener fe7Selection = new ModalButtonListener() {
					@Override
					public void onSelected() {
						loadGameType(GameType.FE7, pathToFile, handler);
						friendlyName.setText("Display Name: (Unverified) Fire Emblem: Blazing Sword");
					}
				};
				ModalButtonListener fe8Selection = new ModalButtonListener() {
					@Override
					public void onSelected() {
						loadGameType(GameType.FE8, pathToFile, handler);
						friendlyName.setText("Display Name: (Unverified) Fire Emblem: The Sacred Stones");
					}
				};
				ModalButtonListener fe9Selection = new ModalButtonListener() {
					@Override
					public void onSelected() {
						loadGameType(GameType.FE9, pathToFile, handler);
						friendlyName.setText("Display Name: (Unverified) Fire Emblem: Path of Radiance");
					}
				};
				Map<String, ModalButtonListener> selectionMap = new HashMap<String, ModalButtonListener>();
				selectionMap.put("FE4 (Genealogy of the Holy War)", fe4Selection);
				selectionMap.put("FE6 (Binding Blade)", fe6Selection);
				selectionMap.put("FE7 (Blazing Sword)", fe7Selection);
				selectionMap.put("FE8 (The Sacred Stones)", fe8Selection);
				selectionMap.put("FE9 (Path of Radiance)", fe9Selection);
				checksumFailure.addSelectionItems(selectionMap, Arrays.asList("FE4 (Genealogy of the Holy War)",
						"FE6 (Binding Blade)",
						"FE7 (Blazing Sword)",
						"FE8 (The Sacred Stones)",
						"FE9 (Path of Radiance)"), new ModalButtonListener() {
							@Override
							public void onSelected() {
								// On cancel...
								disposeRandomizationOptionsViews();
							}
					
				});
				checksumFailure.show();
			} 
			
			romInfoGroup.setVisible(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to load file for reading.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to calculate checksum on input file.");
			e.printStackTrace();
		}
		
		if (loadedGameType == GameType.FE9 && System.getProperty("sun.arch.data.model").equals("32")) {
			MessageModal memoryWarning = new MessageModal(mainShell, "Warning", "You seem to be running a 32-bit Java VM.\nThere are known out of memory issues with\nrandomizing FE9 when using a 32-bit VM.\n\nThis may be addressed in a future release,\nbut please consider upgrading to a 64-bit JRE.");
			memoryWarning.show();
		}
	}
	
	private void loadGameType(GameType type, String pathToFile, FileHandler handler) {
		loadedGameType = type;
		
		if (type == GameType.UNKNOWN) { return; }
		
		updateLayoutForGameType(type);
		
		// Preload options if there are any.
		preloadOptions(type);
		
		growthView.setVisible(true);
		baseView.setVisible(true);
		
		if (type == GameType.FE4) {
			fe4ClassView.setVisible(true);
			holyBloodView.setVisible(true);
			skillsView.setVisible(true);
			fe4PromotionView.setVisible(true);
			fe4EnemyBuffView.setVisible(true);
			
		} else if (type == GameType.FE9) {
			fe9SkillView.setVisible(true);
			conAffinityView.setVisible(true);
			fe9EnemyView.setVisible(true);
			fe9ClassesView.setVisible(true);
			weaponView.setVisible(true);
		} else {
			classView.setVisible(true);
			otherCharOptionView.setVisible(true);
			weaponView.setVisible(true);
			enemyView.setVisible(true);
			recruitView.setVisible(true);
			itemAssignmentView.setVisible(true);
			characterShufflingView.setVisible(true);
		}

		
		miscView.setVisible(true);
		miscView.setPatchingEnabled(patchingAvailable);
		
		randomizeButton.setVisible(true);
		
		seedField.setVisible(true);
		generateButton.setVisible(true);
		seedLabel.setVisible(true);
		
		seedField.setText(SeedGenerator.generateRandomSeed(type));
		for (Listener listener : generateButton.getListeners(SWT.Selection)) {
			generateButton.removeListener(SWT.Selection, listener);
		}
		generateButton.addListener(SWT.Selection, new Listener() {
			  @Override
				public void handleEvent(Event event) {
					seedField.setText(SeedGenerator.generateRandomSeed(type));
					randomizeButton.setEnabled(seedField.getText().length() > 0);
				}
		  });
		
		for (Listener listener : randomizeButton.getListeners(SWT.Selection)) {
			randomizeButton.removeListener(SWT.Selection, listener);
		}
		randomizeButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				FileDialog openDialog = new FileDialog(mainShell, SWT.SAVE);
				if (type.isGBA()) {
					openDialog.setFilterExtensions(new String[] {"*.gba"});
				} else if (type.isSFC()) {
					openDialog.setFilterExtensions(new String[] {"*.smc"});
				} else if (type.isGCN()) {
					openDialog.setFilterExtensions(new String[] {"*.iso"});
				}
				String writePath = openDialog.open();
				
				if (writePath != null && writePath.length() > 0) {
					if (writePath.equals(pathToFile)) {
						String extension = writePath.substring(writePath.length() - 4);
						StringBuilder sb = new StringBuilder(writePath);
						sb.delete(sb.length() - 4, sb.length());
						sb.append(" (Randomized)");
						sb.append(extension);
						writePath = sb.toString();
					}
					DiffCompiler compiler = new DiffCompiler();
					
					if (type == GameType.FE7) {
						try {
							compiler.addDiffsFromFile("tutorialSlayer");
						} catch (IOException e) {
							MessageBox tutorialSlayerFail = new MessageBox(mainShell, SWT.ICON_ERROR | SWT.OK | SWT.CANCEL);
							tutorialSlayerFail.setText("Error");
							tutorialSlayerFail.setMessage("Failed to patch the tutorial slayer.\n\nThe randomizer can continue, but it is recommended that Lyn Normal mode not be used.");
							int selectedButton = tutorialSlayerFail.open();
							if (selectedButton == SWT.CANCEL) {
								return;
							}
						}
					}
					
					Randomizer randomizer = null;
					
					if (type.isGBA()) {
						OptionRecorder.recordGBAFEOptions(type, 
								growthView.getGrowthOptions(),
								baseView.getBaseOptions(),
								classView.getClassOptions(),
								weaponView.getWeaponOptions(),
								otherCharOptionView.getOtherCharacterOptions(),
								enemyView.getEnemyOptions(),
								miscView.getMiscellaneousOptions(),
								recruitView.getRecruitmentOptions(),
								itemAssignmentView.getAssignmentOptions(),
								characterShufflingView.getShufflingOptions(),
								seedField.getText());
						randomizer = AbstractGBARandomizer.buildRandomizer(pathToFile, writePath, type, compiler, 
								OptionRecorder.getGBABundle(type),
								seedField.getText());
						
					} else if (type.isSFC()) {
						if (type == GameType.FE4) {
							boolean headeredROM = handler.getCRC32() == FE4Data.CleanHeaderedCRC32;;
							randomizer = new FE4Randomizer(pathToFile, headeredROM, writePath, compiler, 
									growthView.getGrowthOptions(),
									baseView.getBaseOptions(),
									holyBloodView.getHolyBloodOptions(),
									skillsView.getSkillOptions(),
									fe4ClassView.getClassOptions(),
									fe4PromotionView.getPromotionOptions(),
									fe4EnemyBuffView.getBuffOptions(),
									miscView.getMiscellaneousOptions(), 
									seedField.getText());
							
							OptionRecorder.recordFE4Options(growthView.getGrowthOptions(),
									baseView.getBaseOptions(),
									holyBloodView.getHolyBloodOptions(),
									skillsView.getSkillOptions(),
									fe4ClassView.getClassOptions(),
									fe4PromotionView.getPromotionOptions(),
									fe4EnemyBuffView.getBuffOptions(),
									miscView.getMiscellaneousOptions(), 
									seedField.getText());
						}
					} else if (type.isGCN()) {
						randomizer = new FE9Randomizer(pathToFile, writePath,
								growthView.getGrowthOptions(),
								baseView.getBaseOptions(),
								fe9SkillView.getSkillOptions(),
								conAffinityView.getOtherCharacterOptions(),
								fe9EnemyView.getEnemyBuffOptions(),
								fe9ClassesView.getClassOptions(),
								weaponView.getWeaponOptions(),
								miscView.getMiscellaneousOptions(),
								seedField.getText());
						
						OptionRecorder.recordFE9Options(growthView.getGrowthOptions(), 
								baseView.getBaseOptions(), 
								fe9SkillView.getSkillOptions(), 
								conAffinityView.getOtherCharacterOptions(), 
								fe9EnemyView.getEnemyBuffOptions(), 
								fe9ClassesView.getClassOptions(), 
								weaponView.getWeaponOptions(),
								miscView.getMiscellaneousOptions(),
								seedField.getText());
					}
					
					final String romPath = writePath;
					randomizer.setListener(new RandomizerListener() {

						@Override
						public void onStatusUpdate(String status) {
							progressBox.statusLabel.setText(status);
						}

						@Override
						public void onComplete(RecordKeeper rk, ChangelogBuilder cb) {
							hideModalProgressDialog();
							MessageModal randomSuccess;
							if (rk != null) {
								randomSuccess = new MessageModal(mainShell, "Success", "Finished Randomizing!\n\nSave changelog?");
								randomSuccess.addButton("Yes", new ModalButtonListener() {
									@Override
									public void onSelected() {
										randomSuccess.hide();
										FileDialog openDialog = new FileDialog(mainShell, SWT.SAVE);
										openDialog.setFilterExtensions(new String[] {"*.html"});
										String writePath = openDialog.open();
										if (writePath != null) {
											Boolean success = rk.exportRecordsToHTML(writePath);
											if (success) {
												MessageModal saveSuccess = new MessageModal(mainShell, "Success", "Changelog saved.");
												saveSuccess.show();
											} else {
												MessageModal saveFail = new MessageModal(mainShell, "Error", "Failed to write changelog.");
												saveFail.show();
											}
										}
									}
								});
								randomSuccess.addButton("No", new ModalButtonListener() {
									public void onSelected() {
										randomSuccess.hide();
									}
								});
							} else if (cb != null) {
								randomSuccess = new MessageModal(mainShell, "Success", "Finished Randomizing!\n\nSave changelog?");
								randomSuccess.addButton("Yes", new ModalButtonListener() {
									@Override
									public void onSelected() {
										randomSuccess.hide();
										FileDialog openDialog = new FileDialog(mainShell, SWT.SAVE);
										openDialog.setFilterExtensions(new String[] {"*.html"});
										String changelogPath = openDialog.open();
										if (changelogPath != null) {
											int index = Math.max(romPath.lastIndexOf('/'), romPath.lastIndexOf('\\'));
											String title =  romPath.substring(index + 1);
											cb.setDocumentTitle("Changelog for " + title);
											Boolean success = cb.writeToPath(changelogPath);
											if (success) {
												MessageModal saveSuccess = new MessageModal(mainShell, "Success", "Changelog saved.");
												saveSuccess.show();
											} else {
												MessageModal saveFail = new MessageModal(mainShell, "Error", "Failed to write changelog.");
												saveFail.show();
											}
										}
									}
								});
								randomSuccess.addButton("No", new ModalButtonListener() {
									public void onSelected() {
										randomSuccess.hide();
									}
								});
							} else {
								randomSuccess = new MessageModal(mainShell, "Success", "Finished Randomizing!");
							}
							
							randomSuccess.show();
						}

						@Override
						public void onError(String errorString) {
							hideModalProgressDialog();
							MessageModal randomFailure = new MessageModal(mainShell, "Error", "Randomization failed with error: " + errorString);
							randomFailure.show();
						}

						@Override
						public void onProgressUpdate(double progress) {
							progressBox.progressBar.setSelection((int)(progress * 100));
						}
					});
					
					randomizer.start();
					showModalProgressDialog();
				}
			}
		  });
	}
}
