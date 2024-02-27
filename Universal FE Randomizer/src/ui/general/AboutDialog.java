package ui.general;

import application.Main;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import util.ReleaseInformation;

import java.util.Arrays;

/**
 * Basic About dialog that is opened from the menu bar,
 */
public class AboutDialog {

	Display display;

	Shell dialogShell;

	Image yuneImage;
	Label imageLabel;


	public AboutDialog(Shell parent) {
		display = Display.getDefault();
		yuneImage = new Image(display, Main.class.getClassLoader().getResourceAsStream("YuneIcon_100x100.png"));
		
		dialogShell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM);
		dialogShell.setText("About Yune");
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
		imageData.bottom = new FormAttachment(100, -10);
		imageLabel.setLayoutData(imageData);

		Composite contentGroup = new Composite(dialogShell, SWT.NONE);
		contentGroup.setLayout(new RowLayout(SWT.VERTICAL));
		Link githubLink = new Link(contentGroup, SWT.NONE);
		githubLink.setText("For suggestions, feature requests, bug reports or other things related to the randomizer <a href=\"https://github.com/lushen124/Universal-FE-Randomizer\">visit the github page</a>");
		// Event handling when users click on links.
		githubLink.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> Program.launch("https://github.com/lushen124/Universal-FE-Randomizer")));

		new Label(contentGroup, SWT.NONE).setText("\n\n\n");
		new Label(contentGroup, SWT.NONE).setText("Contributors:\n");

		Control previous = githubLink;
		for (String contributor : Arrays.asList("lushen124", "Geeene", "Vennobennu", "sbeach", "libertyernie","muhmuhten")) {
			Link contributorLink = new Link(contentGroup, SWT.NONE);
			String link = "https://github.com/"+contributor;
			contributorLink.setText("<a href=\""+link+"\">"+contributor+"</a>");
			contributorLink.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> Program.launch(link)));
		}

		new Label(contentGroup, SWT.NONE).setText("\n\n\n");
		new Label(contentGroup, SWT.NONE).setText("Current version: " + Main.versionId);

		ReleaseInformation releaseInformation = ReleaseInformation.get();
		if (releaseInformation != null) {
			new Label(contentGroup,SWT.NONE).setText("Newest version: " + releaseInformation.versionId);
			if (!Main.versionId.equals(releaseInformation.versionId)) {
				new Link(contentGroup, SWT.NONE).setText("There exists a more recent version! <a href=\""+releaseInformation.releasePath+"\">Download it here</a>");
			}
		} else {
			new Label(contentGroup,SWT.NONE).setText("Failed to request newest release information. Please check the github for a new release.");
		}

		FormData groupData = new FormData();
		groupData.left = new FormAttachment(imageLabel, 10);
		groupData.top = new FormAttachment(imageLabel, 0, SWT.TOP);
		groupData.right = new FormAttachment(100, -10);
		contentGroup.setLayoutData(groupData);

		dialogShell.layout();
		final Point newSize = dialogShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		dialogShell.setSize(newSize);

		Rectangle parentBounds = parent.getBounds();
		Rectangle dialogBounds = dialogShell.getBounds();


		dialogShell.setLocation(parentBounds.x + (parentBounds.width - dialogBounds.width) / 2, parentBounds.y + (parentBounds.height - dialogBounds.height) / 2);
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
