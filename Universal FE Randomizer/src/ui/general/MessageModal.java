package ui.general;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import application.Main;
import ui.common.GuiUtil;

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
	
	List selectionList;
	Map<String, ModalButtonListener> selectionChoices;
	Button listOKButton;
	ModalButtonListener selectionCancel;
	
	Boolean hasBeenDisplayed = false;
	
	public MessageModal(Shell parent, String title, String message) {
		display = Display.getDefault();
		yuneImage = new Image(display, Main.class.getClassLoader().getResourceAsStream("YuneIcon_100x100.png"));
		
		dialogShell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM);
		dialogShell.setText(title);
		dialogShell.setImage(yuneImage);
		
		dialogShell.addListener(SWT.CLOSE, new Listener() {

			@Override
			public void handleEvent(Event event) {
				event.doit = false;
			}
			
		});
		
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
		
		contentGroup = new Composite(dialogShell, SWT.NONE);
		contentGroup.setLayout(GuiUtil.formLayoutWithMargin());
		
		titleLabel = new Label(contentGroup, SWT.LEFT);
		titleLabel.setText(title);
		FontData normalFont = titleLabel.getFont().getFontData()[0];
		Font boldFont = new Font(display, normalFont.getName(), normalFont.getHeight(), SWT.BOLD);
		titleLabel.setFont(boldFont);
		
		FormData titleData = new FormData();
		titleData.top = new FormAttachment(0, 0);
		titleData.left = new FormAttachment(0, 0);
		titleData.right = new FormAttachment(100, 0);
		titleLabel.setLayoutData(titleData);
		
		descriptionLabel = new Label(contentGroup, SWT.LEFT | SWT.WRAP);
		descriptionLabel.setText(message);
		
		FormData descriptionData = new FormData();
		descriptionData.left = new FormAttachment(titleLabel, 0, SWT.LEFT);
		descriptionData.right = new FormAttachment(titleLabel, 0, SWT.RIGHT);
		descriptionData.top = new FormAttachment(titleLabel, 10);
		descriptionData.bottom = new FormAttachment(100, -5);
		Point expectedSize = descriptionLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		descriptionData.width = Math.max(200, expectedSize.x);
		descriptionData.height = Math.max(60, expectedSize.y);
		descriptionLabel.setLayoutData(descriptionData);
		
		FormData groupData = new FormData();
		groupData.left = new FormAttachment(imageLabel, 10);
		groupData.top = new FormAttachment(imageLabel, 0, SWT.TOP);
		groupData.right = new FormAttachment(100, -10);
		contentGroup.setLayoutData(groupData);
		
		layoutSize();
		Rectangle parentBounds = parent.getBounds();
		Rectangle dialogBounds = dialogShell.getBounds();
		
		dialogShell.setLocation(parentBounds.x + (parentBounds.width - dialogBounds.width) / 2, parentBounds.y + (parentBounds.height - dialogBounds.height) / 2);
	}
	
	public void addSelectionItems(Map<String, ModalButtonListener> items, java.util.List<String> orderedItems, ModalButtonListener onCancel) {
		if (hasBeenDisplayed) { return; }
		
		if (selectionList == null) {
			selectionList = new List(dialogShell, SWT.BORDER | SWT.SINGLE);
			FormData listData = new FormData();
			listData.top = new FormAttachment(contentGroup, 10);
			listData.left = new FormAttachment(contentGroup, 0, SWT.LEFT);
			listData.right = new FormAttachment(contentGroup, 0, SWT.RIGHT);
			selectionList.setLayoutData(listData);
		}
		selectionChoices = items;
		
		for (String itemKey : orderedItems) {
			selectionList.add(itemKey);
		}
		
		selectionCancel = onCancel;
	}
	
	public Button addButton(String title, ModalButtonListener listener) {
		if (hasBeenDisplayed) { return null; }
		
		if (buttonGroup == null) {
			buttonGroup = new Composite(dialogShell, SWT.NONE);
			FillLayout buttonGroupLayout = new FillLayout(SWT.HORIZONTAL);
			buttonGroupLayout.spacing = 10;
			buttonGroup.setLayout(buttonGroupLayout);
			
			if (selectionList != null) {
				FormData listData = (FormData)selectionList.getLayoutData();
				listData.bottom = new FormAttachment(buttonGroup, -10);
			} else {
				FormData contentData = (FormData)contentGroup.getLayoutData();
				contentData.bottom = new FormAttachment(buttonGroup, -10);
			}
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
		groupData.right = new FormAttachment(100, -10);
		groupData.bottom = new FormAttachment(100, -10);
		buttonGroup.setLayoutData(groupData);
		
		layoutSize();
		
		return button;
	}
	
	private void layoutSize() {
		dialogShell.layout();
		final Point newSize = dialogShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		dialogShell.setSize(newSize);
	}
	
	public void show() {
		if (numberOfButtons == 0) {
			if (selectionList != null) {
				addButton("Cancel", new ModalButtonListener() {
					@Override
					public void onSelected() {
						if (selectionCancel != null) {
							selectionCancel.onSelected();
						}
						hide();
					}
				});
				listOKButton = addButton("OK", new ModalButtonListener() {
					@Override
					public void onSelected() {
						int selectedIndex = selectionList.getSelectionIndices()[0];
						selectionChoices.get(selectionList.getItem(selectedIndex)).onSelected();
						hide();
					}
				});
				listOKButton.setEnabled(false);
				selectionList.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int[] selection = selectionList.getSelectionIndices();
						if (selection.length == 0) { listOKButton.setEnabled(false); }
						else { listOKButton.setEnabled(true); }
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						
					}
				});
			} else {
				addButton("OK", new ModalButtonListener() {
					@Override
					public void onSelected() {
						hide();
					}
				});
			}
		}
		dialogShell.open();
		hasBeenDisplayed = true;
		while (!dialogShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void showRaw() {
		dialogShell.open();
		hasBeenDisplayed = true;
	}
	
	public void hide() {
		dialogShell.close();
	}
}
