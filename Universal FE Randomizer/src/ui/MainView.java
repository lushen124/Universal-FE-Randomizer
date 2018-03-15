package ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import application.Main;
import fedata.FE7Data;
import fedata.FEBase;
import io.DiffApplicator;
import io.FileHandler;
import random.CharacterDataLoader;
import random.GrowthsRandomizer;
import util.DiffCompiler;

public class MainView implements FileFlowDelegate {
	
	public Shell mainShell;
	
	private Text filenameField;
	
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
	
	public MainView(Display mainDisplay) {
		super();
		
		Shell shell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.RESIZE & ~SWT.MAX); 
		 shell.setText("Universal FE Randomizer");
		 shell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("icon.png")));
		 shell.setSize(1024, 768);
		 
		 mainShell = shell;
		 
		 setupMainShell();
		 int requiredX = classView.getBounds().x + classView.getBounds().width + 10;
		 int requiredY = Math.max(otherCharOptionView.getBounds().y + otherCharOptionView.getBounds().height, weaponView.getBounds().y + weaponView.getBounds().height) + 40;
		 shell.setSize(requiredX, requiredY);
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
		  field.setLayoutData(fieldData);
		  
		  FormData buttonData = new FormData();
		  buttonData.right = new FormAttachment(100, -5);
		  buttonData.top = new FormAttachment(field, 0, SWT.CENTER);
		  buttonData.width = 100;
		  button.setLayoutData(buttonData);
		  
		  romInfoGroup = new Group(mainShell, SWT.NONE);
		  romInfoGroup.setText("ROM Info");
		  romInfoGroup.setVisible(false);
		  
		  FormData infoGroupData = new FormData();
		  infoGroupData.left = new FormAttachment(0, 5);
		  infoGroupData.right = new FormAttachment(100, -5);
		  infoGroupData.top = new FormAttachment(field, 5);
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
		  new Label(bottomInfo, SWT.LEFT);
		  
		  growthView = new GrowthsView(mainShell, SWT.NONE);
		  growthView.setSize(200, 200);
		  growthView.setVisible(false);
		  
		  FormData growthData = new FormData();
		  growthData.top = new FormAttachment(romInfoGroup, 5);
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
		  
		  weaponView = new WeaponsView(mainShell, SWT.NONE);
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
		  classView.setLayoutData(classData);
		  
		  enemyView = new EnemyBuffsView(mainShell, SWT.NONE);
		  enemyView.setSize(200, 200);
		  enemyView.setVisible(false);
		  
		  FormData enemyData = new FormData();
		  enemyData.top = new FormAttachment(classView, 5);
		  enemyData.left = new FormAttachment(classView, 0, SWT.LEFT);
		  enemyData.right = new FormAttachment(classView, 0, SWT.RIGHT);
		  enemyView.setLayoutData(enemyData);
		  
		  miscView = new MiscellaneousView(mainShell, SWT.NONE);
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

		  /* Open shell window */
		  mainShell.open();
	}

	@Override
	public void onSelectedFile(String pathToFile) {
		if (filenameField != null) {
			filenameField.setText(pathToFile);
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
			} else {
				friendlyName.setText("Display Name: Unknown");
			}
			
			length.setText("File Length: " + handler.getFileLength());
			checksum.setText("CRC-32: " + Long.toHexString(handler.getCRC32()).toUpperCase());
			
			if (handler.getCRC32() != FE7Data.CleanCRC32) {
				MessageBox checksumFail = new MessageBox(mainShell, SWT.ICON_ERROR | SWT.OK);
				checksumFail.setText("Failure");
				checksumFail.setMessage("Checksum failed.\n\nExpected Checksum: " + Long.toHexString(FE7Data.CleanCRC32).toUpperCase() + "\n\nActual Checksum: " + Long.toHexString(handler.getCRC32()).toUpperCase());
				checksumFail.open();
				
				growthView.setVisible(false);
				baseView.setVisible(false);
				classView.setVisible(false);
				otherCharOptionView.setVisible(false);
				weaponView.setVisible(false);
				enemyView.setVisible(false);
				miscView.setVisible(false);
				randomizeButton.setVisible(false);
			} else {
				growthView.setVisible(true);
				baseView.setVisible(true);
				classView.setVisible(true);
				otherCharOptionView.setVisible(true);
				weaponView.setVisible(true);
				enemyView.setVisible(true);
				miscView.setVisible(true);
				randomizeButton.setVisible(true);
				
				//TextHelper textHelper = new TextHelper(FEBase.GameType.FE7, handler);
				
				CharacterDataLoader characterData = new CharacterDataLoader(FEBase.GameType.FE7, handler);
				GrowthsRandomizer.randomizeGrowthsByRedistribution(0, characterData);
				DiffCompiler compiler = new DiffCompiler();
				compiler.addDiffsFromFile("tutorialSlayer");
				characterData.compileDiffs(compiler);
				
				FileDialog openDialog = new FileDialog(mainShell, SWT.SAVE);
				String writePath = openDialog.open();
				if (writePath != null) {
					DiffApplicator.applyDiffs(compiler, handler, writePath);
				}

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
