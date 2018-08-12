package ui.general;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import application.Main;

public class MessageModal {
	
	static final int ButtonWidth = 80;
	static final int ButtonHeight = 24;

	Display display;
	
	Shell dialogShell;
	
	Image yuneImage;
	Label imageLabel;
	
	Composite contentGroup;
	Label titleLabel;
	Label descriptionLabel;
	
	Composite buttonGroup;
	int numberOfButtons = 0;
	
	Boolean hasBeenDisplayed = false;
	
	public MessageModal(Shell parent, String title, String message) {
		display = Display.getDefault();
		yuneImage = new Image(display, Main.class.getClassLoader().getResourceAsStream("YuneIcon_100x100.png"));
		
		dialogShell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM);
		dialogShell.setText(title);
		dialogShell.setImage(yuneImage);
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 5;
		mainLayout.marginHeight = 5;
		dialogShell.setLayout(mainLayout);
		
		imageLabel = new Label(dialogShell, SWT.NONE);
		imageLabel.setImage(yuneImage);
		
		FormData imageData = new FormData(100, 100);
		imageData.left = new FormAttachment(0, 10);
		imageData.top = new FormAttachment(0, 10);
		imageLabel.setLayoutData(imageData);
		
		Composite contentGroup = new Composite(dialogShell, SWT.NONE);
		FormLayout contentLayout = new FormLayout();
		contentGroup.setLayout(contentLayout);
		
		titleLabel = new Label(contentGroup, SWT.LEFT);
		titleLabel.setText(title);
		FontData normalFont = titleLabel.getFont().getFontData()[0];
		Font boldFont = new Font(display, normalFont.getName(), normalFont.getHeight(), SWT.BOLD);
		titleLabel.setFont(boldFont);
		
		FormData titleData = new FormData();
		titleData.width = 200;
		titleLabel.setLayoutData(titleData);
		
		descriptionLabel = new Label(contentGroup, SWT.LEFT);
		descriptionLabel.setText(message);
		
		FormData descriptionData = new FormData();
		descriptionData.left = new FormAttachment(titleLabel, 0, SWT.LEFT);
		descriptionData.right = new FormAttachment(titleLabel, 0, SWT.RIGHT);
		descriptionData.top = new FormAttachment(titleLabel, 10);
		descriptionLabel.setLayoutData(descriptionData);
		
		FormData groupData = new FormData();
		groupData.left = new FormAttachment(imageLabel, 10);
		groupData.top = new FormAttachment(imageLabel, 0, SWT.CENTER);
		groupData.right = new FormAttachment(100, -10);
		contentGroup.setLayoutData(groupData);
		
		layoutSize();
	}
	
	public void addButton(String title, ModalButtonListener listener) {
		if (hasBeenDisplayed) { return; }
		
		if (buttonGroup == null) {
			buttonGroup = new Composite(dialogShell, SWT.NONE);
			FillLayout buttonGroupLayout = new FillLayout(SWT.HORIZONTAL);
			buttonGroup.setLayout(buttonGroupLayout);
		}
		
		numberOfButtons++;
		
		Button button = new Button(buttonGroup, SWT.PUSH);
		button.setText(title);
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				listener.onSelected();
			}
		});
		
		FormData groupData = new FormData();
		groupData.width = ButtonWidth * numberOfButtons;
		groupData.height = ButtonHeight;
		groupData.top = new FormAttachment(imageLabel, 10);
		groupData.right = new FormAttachment(100, -10);
		groupData.bottom = new FormAttachment(100, -10);
		buttonGroup.setLayoutData(groupData);
		
		layoutSize();
	}
	
	private void layoutSize() {
		dialogShell.layout();
		final Point newSize = dialogShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		dialogShell.setSize(newSize);
	}
	
	public void show() {
		if (numberOfButtons == 0) {
			addButton("OK", new ModalButtonListener() {
				@Override
				public void onSelected() {
					hide();
				}
			});
		}
		dialogShell.open();
		hasBeenDisplayed = true;
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
