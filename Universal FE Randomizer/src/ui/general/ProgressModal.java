package ui.general;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import application.Main;

public class ProgressModal {
	
	Display display;
	
	Shell dialogShell;
	
	Image yuneImage;
	Label imageLabel;
	
	public ProgressBar progressBar;
	public Label statusLabel;
	
	public ProgressModal(Shell parent, String title) {
		display = Display.getDefault();
		yuneImage = new Image(display, Main.class.getClassLoader().getResourceAsStream("YuneIcon_100x100.png"));
		
		dialogShell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM);
		dialogShell.setText(title);
		dialogShell.setImage(yuneImage);
		
		progressBar = new ProgressBar(dialogShell, SWT.SMOOTH);
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 5;
		mainLayout.marginHeight = 5;
		dialogShell.setLayout(mainLayout);
		
		imageLabel = new Label(dialogShell, SWT.NONE);
		imageLabel.setImage(yuneImage);
		
		FormData imageData = new FormData(100, 100);
		imageData.left = new FormAttachment(0, 10);
		imageData.top = new FormAttachment(0, 10);
		imageData.bottom = new FormAttachment(100, -10);
		imageLabel.setLayoutData(imageData);
		
		FormData progressData = new FormData(200, 20);
		progressData.left = new FormAttachment(imageLabel, 10);
		progressData.bottom = new FormAttachment(imageLabel, -50, SWT.CENTER);
		progressData.right = new FormAttachment(100, -10);
		progressBar.setLayoutData(progressData);
		
		statusLabel = new Label(dialogShell, SWT.NONE);
		
		FormData statusData = new FormData();
		statusData.left = new FormAttachment(progressBar, 0, SWT.LEFT);
		statusData.top = new FormAttachment(progressBar, 5);
		statusData.right = new FormAttachment(100, -10);
		statusLabel.setLayoutData(statusData);
		
		dialogShell.layout();
		final Point newSize = dialogShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		dialogShell.setSize(newSize);
	}
	
	public void show() {
		dialogShell.open();
		while (!dialogShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void hide() {
		dialogShell.close();
	}
}
