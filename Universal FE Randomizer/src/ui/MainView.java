package ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
import org.eclipse.swt.widgets.Text;

import application.Main;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.general.FEBase.GameType;
import fedata.snes.fe4.FE4Data;
import io.FileHandler;
import random.gba.randomizer.GBARandomizer;
import random.general.Randomizer;
import random.general.RandomizerListener;
import random.snes.fe4.randomizer.FE4Randomizer;
import ui.fe4.FE4ClassesView;
import ui.fe4.FE4EnemyBuffView;
import ui.fe4.FE4PromotionView;
import ui.fe4.HolyBloodView;
import ui.fe4.SkillsView;
import ui.general.FileFlowDelegate;
import ui.general.MessageModal;
import ui.general.ModalButtonListener;
import ui.general.OpenFileFlow;
import ui.general.ProgressModal;
import util.DiffCompiler;
import util.OptionRecorder;
import util.SeedGenerator;
import util.OptionRecorder.FE4OptionBundle;
import util.OptionRecorder.GBAOptionBundle;
import util.recordkeeper.RecordKeeper;

public class MainView implements FileFlowDelegate {
	
	public Shell mainShell;
	
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
	
	// FE4
	private SkillsView skillsView;
	private HolyBloodView holyBloodView;
	private FE4ClassesView fe4ClassView;
	private FE4PromotionView fe4PromotionView;
	private FE4EnemyBuffView fe4EnemyBuffView;
	
	private Button randomizeButton;
	
	private Boolean isShowingModalProgressDialog = false;
	private ProgressModal progressBox;
	
	public MainView(Display mainDisplay) {
		super();
		
		Shell shell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX); 
		 shell.setText("Yune: A Universal Fire Emblem Randomizer (v0.8.4)");
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
		
		resize();
	}
	
	private void preloadOptions(GameType type) {
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
				itemAssignmentView.setItemAssignmentOptions(bundle.itemAssignmentOptions);
			}
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
			
			randomizeButton = new Button(container, SWT.PUSH);
			randomizeButton.setText("Randomize!");
			randomizeButton.setVisible(false);
			  
			FormData randomizeData = new FormData();
			randomizeData.top = new FormAttachment(miscView, 5);
			randomizeData.left = new FormAttachment(miscView, 0, SWT.LEFT);
			randomizeData.right = new FormAttachment(miscView, 0, SWT.RIGHT);
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
			recruitData.right = new FormAttachment(100, -5);
			recruitView.setLayoutData(recruitData);
			
			itemAssignmentView = new ItemAssignmentView(container, SWT.NONE);
			itemAssignmentView.setSize(200, 200);
			itemAssignmentView.setVisible(false);
			
			FormData itemAssignData = new FormData();
			itemAssignData.top = new FormAttachment(recruitView, 5);
			itemAssignData.left = new FormAttachment(recruitView, 0, SWT.LEFT);
			itemAssignData.right = new FormAttachment(recruitView, 0, SWT.RIGHT);
			itemAssignmentView.setLayoutData(itemAssignData);
			
			randomizeButton = new Button(container, SWT.PUSH);
			randomizeButton.setText("Randomize!");
			randomizeButton.setVisible(false);
			  
			FormData randomizeData = new FormData();
			randomizeData.top = new FormAttachment(itemAssignmentView, 5);
			randomizeData.left = new FormAttachment(itemAssignmentView, 0, SWT.LEFT);
			randomizeData.right = new FormAttachment(recruitView, 0, SWT.RIGHT);
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
			if (handler.getCRC32() == FE6Data.CleanCRC32) { 
				type = GameType.FE6;
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
				friendlyName.setText("Display Name: " + FE4Data.FriendlyName);
				romName.setText("ROM Name: " + FE4Data.InternalName);
				romCode.setText("ROM Code: --");
			}
			else { 
				type = GameType.UNKNOWN;
				friendlyName.setText("Display Name: Unknown");
			}
			
			updateLayoutForGameType(type);
			
			loadedGameType = type;
			
			// Preload options if there are any.
			preloadOptions(type);
			
			final GameType gameType = type;
			
			if (type != GameType.UNKNOWN) {
				growthView.setVisible(true);
				baseView.setVisible(true);
				
				if (type == GameType.FE4) {
					fe4ClassView.setVisible(true);
					holyBloodView.setVisible(true);
					skillsView.setVisible(true);
					fe4PromotionView.setVisible(true);
					fe4EnemyBuffView.setVisible(true);
					
				} else {
					classView.setVisible(true);
					otherCharOptionView.setVisible(true);
					weaponView.setVisible(true);
					enemyView.setVisible(true);
					recruitView.setVisible(true);
					itemAssignmentView.setVisible(true);
				}
		
				miscView.setVisible(true);
				randomizeButton.setVisible(true);
				
				seedField.setVisible(true);
				generateButton.setVisible(true);
				seedLabel.setVisible(true);
				
				seedField.setText(SeedGenerator.generateRandomSeed(gameType));
				for (Listener listener : generateButton.getListeners(SWT.Selection)) {
					generateButton.removeListener(SWT.Selection, listener);
				}
				generateButton.addListener(SWT.Selection, new Listener() {
					  @Override
						public void handleEvent(Event event) {
							seedField.setText(SeedGenerator.generateRandomSeed(gameType));
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
						if (gameType.isGBA()) {
							openDialog.setFilterExtensions(new String[] {"*.gba"});
						} else if (gameType.isSFC()) {
							openDialog.setFilterExtensions(new String[] {".smc"});
						}
						String writePath = openDialog.open();
						
						if (writePath != null && writePath.length() > 0) {
							DiffCompiler compiler = new DiffCompiler();
							
							if (gameType == GameType.FE7) {
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
							
							if (gameType.isGBA()) {
								randomizer = new GBARandomizer(pathToFile, writePath, gameType, compiler, 
										growthView.getGrowthOptions(),
										baseView.getBaseOptions(),
										classView.getClassOptions(),
										weaponView.getWeaponOptions(),
										otherCharOptionView.getOtherCharacterOptions(),
										enemyView.getEnemyOptions(),
										miscView.getMiscellaneousOptions(),
										recruitView.getRecruitmentOptions(),
										itemAssignmentView.getAssignmentOptions(),
										seedField.getText());
								
								OptionRecorder.recordGBAFEOptions(gameType, 
										growthView.getGrowthOptions(),
										baseView.getBaseOptions(),
										classView.getClassOptions(),
										weaponView.getWeaponOptions(),
										otherCharOptionView.getOtherCharacterOptions(),
										enemyView.getEnemyOptions(),
										miscView.getMiscellaneousOptions(),
										recruitView.getRecruitmentOptions(),
										itemAssignmentView.getAssignmentOptions(),
										seedField.getText());
							} else if (gameType.isSFC()) {
								if (gameType == GameType.FE4) {
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
							}
							
							randomizer.setListener(new RandomizerListener() {

								@Override
								public void onStatusUpdate(String status) {
									progressBox.statusLabel.setText(status);
								}

								@Override
								public void onComplete(RecordKeeper rk) {
									hideModalProgressDialog();
									MessageModal randomSuccess = new MessageModal(mainShell, "Success", "Finished Randomizing!\n\nSave changelog?");
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
			} else {
				MessageBox checksumFail = new MessageBox(mainShell, SWT.ICON_ERROR | SWT.OK);
				checksumFail.setText("Failure");
				checksumFail.setMessage("Checksum failed.\n\nThis file may not be supported.");
				checksumFail.open();
				
				disposeRandomizationOptionsViews();
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
	}
}
