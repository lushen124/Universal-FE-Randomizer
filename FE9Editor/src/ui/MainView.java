package ui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import application.Main;
import io.FileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;

public class MainView {
	
	public Shell mainShell;
	
	private Label filePathLabel;
	private Text filePathField;
	private Button browseButton;
	
	private TabFolder folder;
	
	private TabItem characterTabItem;
	private TabItem classTabItem;
	private TabItem itemTabItem;
	
	private Composite characterComposite;
	private Composite classComposite;
	private Composite itemComposite;
	
	private Composite chapterComposite;
	
	private GCNISOHandler isoHandler;
	
	public MainView(Display mainDisplay) {
		super();
		
		Shell shell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX);
		shell.setText("Ashera");
		shell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("Ashera.png")));
		mainShell = shell;
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		shell.setLayout(mainLayout);
		
		filePathLabel = new Label(mainShell, SWT.NONE);
		filePathLabel.setText("Path:");
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.top = new FormAttachment(0, 0);
		filePathLabel.setLayoutData(labelData);
		
		filePathField = new Text(mainShell, SWT.BORDER);
		filePathField.setEditable(false);
		
		FormData filePathData = new FormData();
		filePathData.top = new FormAttachment(0, 0);
		filePathData.left = new FormAttachment(filePathLabel, 10);
		filePathData.width = 800;
		filePathField.setLayoutData(filePathData);
		
		browseButton = new Button(mainShell, SWT.NONE);
		browseButton.setText("Browse...");
		browseButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				FileDialog openDialog = new FileDialog(mainShell, SWT.OPEN);
				openDialog.setFilterExtensions(new String[] {"*.iso", "*"});
				openDialog.setFilterNames(new String[] {"*.iso", "All Files (*.*)"});
				onSelectedFile(openDialog.open());
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.top = new FormAttachment(0, 0);
		buttonData.left = new FormAttachment(filePathField, 10);
		buttonData.right = new FormAttachment(100, 0);
		buttonData.bottom = new FormAttachment(100, 0);
		browseButton.setLayoutData(buttonData);
		
		resize();
		
		mainShell.open();
	}
	
	private void resize() {
		mainShell.layout();
		//int titleBarHeight = mainShell.getBounds().height - mainShell.getClientArea().height;
		Point computedSize = mainShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		
		mainShell.setSize(computedSize);
	}
	
	private void addTabFolder() throws GCNISOException {
		FormData buttonData = new FormData();
		buttonData.top = new FormAttachment(0, 0);
		buttonData.left = new FormAttachment(filePathField, 10);
		buttonData.right = new FormAttachment(100, 0);
		browseButton.setLayoutData(buttonData);
		
		folder = new TabFolder(mainShell, SWT.BORDER);
		
		folder.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				resize();
			}
		});
		
		FormData folderData = new FormData();
		folderData.left = new FormAttachment(0, 0);
		folderData.right = new FormAttachment(100, 0);
		folderData.bottom = new FormAttachment(100, 0);
		folderData.top = new FormAttachment(browseButton, 10);
		folder.setLayoutData(folderData);
		
		characterTabItem = new TabItem(folder, SWT.NONE);
		characterTabItem.setText("Character Data");
		
		characterComposite = new CharacterDataView(folder, SWT.NONE, isoHandler);
		characterTabItem.setControl(characterComposite);
		
		classTabItem = new TabItem(folder, SWT.NONE);
		classTabItem.setText("Class Data");
		
		classComposite = new ClassDataView(folder, SWT.NONE, isoHandler);
		classTabItem.setControl(classComposite);
		
		itemTabItem = new TabItem(folder, SWT.NONE);
		itemTabItem.setText("Item Data");
		
		itemComposite = new ItemDataView(folder, SWT.NONE, isoHandler);
		itemTabItem.setControl(itemComposite);
	}
	
	private void onSelectedFile(String path) {
		filePathField.setText(path);
		
		try {
			FileHandler fileHandler = new FileHandler(path);
			isoHandler = new GCNISOHandler(fileHandler);
			addTabFolder();
		} catch (GCNISOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resize();
	}

}
