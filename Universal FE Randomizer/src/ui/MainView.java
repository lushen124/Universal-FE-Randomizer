package ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import application.Main;
import fedata.FEBase.GameType;
import fedata.fe6.FE6Data;
import fedata.fe7.FE7Data;
import io.FileHandler;
import random.Randomizer;
import random.RandomizerListener;
import ui.general.MessageModal;
import ui.general.ProgressModal;
import util.DiffCompiler;
import util.SeedGenerator;

public class MainView implements FileFlowDelegate {
	
	public Shell mainShell;
	
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
	
	private Button randomizeButton;
	
	private Boolean isShowingModalProgressDialog = false;
	private ProgressModal progressBox;
	
	public MainView(Display mainDisplay) {
		super();
		
		Shell shell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.RESIZE & ~SWT.MAX); 
		 shell.setText("Yune: A Universal Fire Emblem Randomizer");
		 shell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
		 
		 mainShell = shell;
		 
		 setupMainShell();
		 
		 mainShell.layout();
		 final Point newSize = mainShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		 mainShell.setSize(newSize);
		 
		 /* Open shell window */
		  mainShell.open();
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
		  FormLayout mainLayout = new FormLayout();
		  mainLayout.marginWidth = 5;
		  mainLayout.marginHeight = 5;
		  mainShell.setLayout(mainLayout);
		  
		  /* Define widgets to add to the shell */
		  Label romFileLabel = new Label(mainShell, 0);
		  romFileLabel.setText("ROM File:");
		  
		  Text field = new Text(mainShell, SWT.BORDER);
		  field.setEditable(false);
		  filenameField = field;
		  
		  Button button = new Button(mainShell, SWT.PUSH);
		  button.setText("Browse...");
		  button.addListener(SWT.Selection, new OpenFileFlow(mainShell, this));
		  
		  FormData labelData = new FormData();
		  labelData.left = new FormAttachment(mainShell, 5);
		  labelData.top = new FormAttachment(field, 0, SWT.CENTER);
		  romFileLabel.setLayoutData(labelData);
		  
		  FormData fieldData = new FormData();
		  fieldData.left = new FormAttachment(romFileLabel, 5);
		  fieldData.top = new FormAttachment(0, 5);
		  fieldData.right = new FormAttachment(button, -5);
		  fieldData.width = 300;
		  field.setLayoutData(fieldData);
		  
		  FormData buttonData = new FormData();
		  buttonData.right = new FormAttachment(100, -5);
		  buttonData.top = new FormAttachment(field, 0, SWT.CENTER);
		  buttonData.width = 100;
		  button.setLayoutData(buttonData);
	}
	
	private void setupInfoLayout() {
		romInfoGroup = new Group(mainShell, SWT.NONE);
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
	
	private void updateLayoutForGameType(GameType type) {
		if (growthView != null) { growthView.dispose(); }
		if (baseView != null) { baseView.dispose(); }
		if (otherCharOptionView != null) { otherCharOptionView.dispose(); }
		if (weaponView != null) { weaponView.dispose(); }
		if (classView != null) { classView.dispose(); }
		if (enemyView != null) { enemyView.dispose(); }
		if (miscView != null) { miscView.dispose(); }
		if (randomizeButton != null) { randomizeButton.dispose(); }
		
		if (type == GameType.UNKNOWN) {
			mainShell.layout();
			final Point newSize = mainShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			mainShell.setSize(newSize);
		}
		
		if (seedField != null) { seedField.dispose(); }
		if (generateButton != null) { generateButton.dispose(); }
		if (seedLabel != null) { seedLabel.dispose(); }
		
		seedField = new Text(mainShell, SWT.BORDER);
		seedField.addListener(SWT.CHANGED, new Listener() {
			@Override
			public void handleEvent(Event event) {
				randomizeButton.setEnabled(seedField.getText().length() > 0);
			}
		});
		Button button = new Button(mainShell, SWT.PUSH);
		button.setText("Generate");
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				seedField.setText(SeedGenerator.generateRandomSeed());
				randomizeButton.setEnabled(seedField.getText().length() > 0);
			}
		});
		generateButton = button;
		  
		seedLabel = new Label(mainShell, SWT.NONE);
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
		
		growthView = new GrowthsView(mainShell, SWT.NONE);
		growthView.setSize(200, 200);
		growthView.setVisible(false);
		  
		FormData growthData = new FormData();
		growthData.top = new FormAttachment(seedField, 10);
		growthData.left = new FormAttachment(romInfoGroup, 0, SWT.LEFT);
		growthView.setLayoutData(growthData);
		  
		baseView = new BasesView(mainShell, SWT.NONE);
		baseView.setSize(200, 200);
		baseView.setVisible(false);
		  
		FormData baseData = new FormData();
		baseData.top = new FormAttachment(growthView, 5);
		baseData.left = new FormAttachment(growthView, 0, SWT.LEFT);
		baseData.right = new FormAttachment(growthView, 0, SWT.RIGHT);
		baseView.setLayoutData(baseData);
		  
		otherCharOptionView = new MOVCONAffinityView(mainShell, SWT.NONE);
		otherCharOptionView.setSize(200, 200);
		otherCharOptionView.setVisible(false);
		  
		FormData otherData = new FormData();
		otherData.top = new FormAttachment(baseView, 5);
		otherData.left = new FormAttachment(baseView, 0, SWT.LEFT);
		otherData.right = new FormAttachment(baseView, 0, SWT.RIGHT);
		otherData.bottom = new FormAttachment(100, -10);
		otherCharOptionView.setLayoutData(otherData);
		  
		weaponView = new WeaponsView(mainShell, SWT.NONE, type);
		weaponView.setSize(200, 200);
		weaponView.setVisible(false);
		  
		FormData weaponData = new FormData();
		weaponData.top = new FormAttachment(growthView, 0, SWT.TOP);
		weaponData.left = new FormAttachment(growthView, 5);
		weaponData.bottom = new FormAttachment(100, -10);
		weaponView.setLayoutData(weaponData);
		  
		classView = new ClassesView(mainShell, SWT.NONE);
		classView.setSize(200, 200);
		classView.setVisible(false);
		  
		FormData classData = new FormData();
		classData.top = new FormAttachment(weaponView, 0, SWT.TOP);
		classData.left = new FormAttachment(weaponView, 5);
		classData.right = new FormAttachment(100, -5);
		classView.setLayoutData(classData);
		  
		enemyView = new EnemyBuffsView(mainShell, SWT.NONE);
		enemyView.setSize(200, 200);
		enemyView.setVisible(false);
		  
		FormData enemyData = new FormData();
		enemyData.top = new FormAttachment(classView, 5);
		enemyData.left = new FormAttachment(classView, 0, SWT.LEFT);
		enemyData.right = new FormAttachment(classView, 0, SWT.RIGHT);
		enemyView.setLayoutData(enemyData);
		  
		miscView = new MiscellaneousView(mainShell, SWT.NONE, type);
		miscView.setSize(200, 200);
		miscView.setVisible(false);
		  
		FormData miscData = new FormData();
		miscData.top = new FormAttachment(enemyView, 5);
		miscData.left = new FormAttachment(enemyView, 0, SWT.LEFT);
		miscData.right = new FormAttachment(enemyView, 0, SWT.RIGHT);
		miscView.setLayoutData(miscData);
		  
		randomizeButton = new Button(mainShell, SWT.PUSH);
		randomizeButton.setText("Randomize!");
		randomizeButton.setVisible(false);
		  
		FormData randomizeData = new FormData();
		randomizeData.top = new FormAttachment(miscView, 5);
		randomizeData.left = new FormAttachment(miscView, 0, SWT.LEFT);
		randomizeData.right = new FormAttachment(miscView, 0, SWT.RIGHT);
		randomizeData.bottom = new FormAttachment(100, -10);
		randomizeButton.setLayoutData(randomizeData);
		
		mainShell.layout();
		final Point newSize = mainShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		 mainShell.setSize(newSize);
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
		}
		
		try {
			FileHandler handler = new FileHandler(pathToFile);
			byte [] result = handler.readBytesAtOffset(0xA0, 12);
			String gameTitle = new String(result, StandardCharsets.US_ASCII);
			romName.setText("ROM Name: " + gameTitle);
			
			result = handler.readBytesAtOffset(0xAC, 4);
			String gameCode = new String(result, StandardCharsets.US_ASCII);
			romCode.setText("ROM Code: " + gameCode);
			
			if (gameCode.equals(FE7Data.GameCode)) {
				friendlyName.setText("Display Name: " + FE7Data.FriendlyName);
			} else if (gameCode.equals(FE6Data.GameCode)) {
				friendlyName.setText("Display Name: " + FE6Data.FriendlyName);
			} else {
				friendlyName.setText("Display Name: Unknown");
			}
			
			length.setText("File Length: " + handler.getFileLength());
			checksum.setText("CRC-32: " + Long.toHexString(handler.getCRC32()).toUpperCase());
			
			GameType type = loadedGameType;
			if (handler.getCRC32() == FE6Data.CleanCRC32) { type = GameType.FE6; }
			else if (handler.getCRC32() == FE7Data.CleanCRC32) { type = GameType.FE7; }
			
			updateLayoutForGameType(type);
			
			loadedGameType = type;
			
			final GameType gameType = type;
			
			if (type != GameType.UNKNOWN) {
				growthView.setVisible(true);
				baseView.setVisible(true);
				classView.setVisible(true);
				otherCharOptionView.setVisible(true);
				weaponView.setVisible(true);
				enemyView.setVisible(true);
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
						//TextHelper textHelper = new TextHelper(FEBase.GameType.FE7, handler);
						
						FileDialog openDialog = new FileDialog(mainShell, SWT.SAVE);
						openDialog.setFilterExtensions(new String[] {"*.gba"});
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
							
							Randomizer randomizer = new Randomizer(pathToFile, writePath, gameType, compiler, 
									growthView.getGrowthOptions(),
									baseView.getBaseOptions(),
									classView.getClassOptions(),
									weaponView.getWeaponOptions(),
									otherCharOptionView.getOtherCharacterOptions(),
									enemyView.getEnemyOptions(),
									miscView.getMiscellaneousOptions(),
									seedField.getText());
							
							randomizer.setListener(new RandomizerListener() {

								@Override
								public void onStatusUpdate(String status) {
									progressBox.statusLabel.setText(status);
								}

								@Override
								public void onComplete() {
									hideModalProgressDialog();
									MessageModal randomSuccess = new MessageModal(mainShell, "Success", "Finished Randomizing!");
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
