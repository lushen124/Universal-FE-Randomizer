package ui.general;

import application.Main;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
		imageLabel.setLayoutData(imageData);
		
		Composite bodyContainer = new Composite(dialogShell, SWT.NONE);
		bodyContainer.setLayout(new FormLayout());
		
		FormData compositeData = new FormData();
		compositeData.left = new FormAttachment(imageLabel, 10);
		compositeData.top = new FormAttachment(0, 10);
		compositeData.right = new FormAttachment(100, -10);
		compositeData.bottom = new FormAttachment(100, -10);
		bodyContainer.setLayoutData(compositeData);

		Link githubLink = new Link(bodyContainer, SWT.NONE);
		githubLink.setText("For suggestions, feature requests, bug reports or other things related to the randomizer <a href=\"https://github.com/lushen124/Universal-FE-Randomizer\">visit the github page</a>");
		// Event handling when users click on links.
		githubLink.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> Program.launch("https://github.com/lushen124/Universal-FE-Randomizer")));
		
		FormData linkData = new FormData();
		linkData.top = new FormAttachment(0, 0);
		linkData.left = new FormAttachment(0, 0);
		githubLink.setLayoutData(linkData);

		Composite credits = new Composite(bodyContainer, SWT.NONE);
		credits.setLayout(new FormLayout());
		
		FormData layoutData = new FormData();
		layoutData.top = new FormAttachment(githubLink, 30);
		layoutData.left = new FormAttachment(0, 0);
		layoutData.right = new FormAttachment(100, 0);
		credits.setLayoutData(layoutData);
		
		Composite contributors = new Composite(credits, SWT.NONE);
		contributors.setLayout(new FormLayout());
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 0);
		layoutData.left = new FormAttachment(0, 0);
		layoutData.right = new FormAttachment(25, -5);
		contributors.setLayoutData(layoutData);
		
		Label contributorsLabel = new Label(contributors, SWT.NONE);
		contributorsLabel.setText("Contributors:");
		
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(0, 0);
		labelData.left = new FormAttachment(0, 0);
		contributorsLabel.setLayoutData(labelData);

		Control previous = contributorsLabel;
		for (String contributor : Arrays.asList("lushen124", "Geeene", "Vennobennu", "sbeach", "libertyernie","muhmuhten")) {
			Link contributorLink = new Link(contributors, SWT.NONE);
			String link = "https://github.com/"+contributor;
			contributorLink.setText("<a href=\""+link+"\">"+contributor+"</a>");
			contributorLink.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> Program.launch(link)));
			
			labelData = new FormData();
			labelData.top = new FormAttachment(previous, (previous == contributorsLabel ? 20 : 5));
			labelData.left = new FormAttachment(0, 0);
			contributorLink.setLayoutData(labelData);
			previous = contributorLink;
		}
		
		Composite specialThanks = new Composite(credits, SWT.NONE);
		specialThanks.setLayout(new FormLayout());
		
		layoutData = new FormData();
		layoutData.top = new FormAttachment(contributors, 0, SWT.TOP);
		layoutData.left = new FormAttachment(25, 5);
		layoutData.right = new FormAttachment(100, -5);
		specialThanks.setLayoutData(layoutData);
		
		Label specialThanksLabel = new Label(specialThanks, SWT.NONE);
		specialThanksLabel.setText("Special Thanks:");
		
		labelData = new FormData();
		labelData.top = new FormAttachment(0, 0);
		labelData.left = new FormAttachment(0, 0);
		specialThanksLabel.setLayoutData(labelData);
		
		Point expectedContentWidth = githubLink.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int labelWidth = expectedContentWidth.x / 4 * 3;
		
		StyledText blazer = createSpecialThanks("Blazer", "For writing the most comprehensive GBAFE hacking guide I could have asked for when starting out in 2013.", specialThanks);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(specialThanksLabel, 20);
		labelData.left = new FormAttachment(0, 0);
		labelData.width = labelWidth;
		blazer.setLayoutData(labelData);
		
		StyledText lamia = createSpecialThanks("Lamia", "For compiling documentation on the inner workings of FE4 that I referenced extensively.", specialThanks);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(blazer, 5);
		labelData.left = new FormAttachment(0, 0);
		labelData.width = labelWidth;
		lamia.setLayoutData(labelData);
		
		StyledText vincent = createSpecialThanks("VincentASM", "For compiling documentation on hacking FE10 that I referenced extensively when working on FE9.", specialThanks);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(lamia, 5);
		labelData.left = new FormAttachment(0, 0);
		labelData.width = labelWidth;
		vincent.setLayoutData(labelData);
		
		StyledText hozu = createSpecialThanks("PokecheckHozu", "For helping with extensive testing on FE4 randomization and contributing fun ideas for how to randomize FE4.", specialThanks);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(vincent, 5);
		labelData.left = new FormAttachment(0, 0);
		labelData.width = labelWidth;
		hozu.setLayoutData(labelData);
		
		StyledText nmm = createSpecialThanks("Nightmare Module Authors", "If you've ever written a Nightmare module for a Fire Emblem game, I've almost certainly consulted your module when writing this randomizer, so this is for all of you.", specialThanks);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(hozu, 5);
		labelData.left = new FormAttachment(0, 0);
		labelData.width = labelWidth;
		nmm.setLayoutData(labelData);
		
		StyledText rfe = createSpecialThanks("/r/FireEmblem", "For being my home base of sorts when doing the bulk of the work for this randomizer.", specialThanks);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(nmm, 5);
		labelData.left = new FormAttachment(0, 0);
		labelData.width = labelWidth;
		rfe.setLayoutData(labelData);
		
		StyledText you = createSpecialThanks("You", "For giving this project some meaning for me by using it and having fun. :)", specialThanks);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(rfe, 5);
		labelData.left = new FormAttachment(0, 0);
		labelData.width = labelWidth;
		you.setLayoutData(labelData);

		Label currentVersionLabel = new Label(bodyContainer, SWT.NONE);
		currentVersionLabel.setText("Current version: " + Main.versionId);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(credits, 30);
		labelData.left = new FormAttachment(0, 0);
		currentVersionLabel.setLayoutData(labelData);

		ReleaseInformation releaseInformation = ReleaseInformation.get();
		if (releaseInformation != null) {
			Label newestVersion = new Label(bodyContainer, SWT.NONE);
			newestVersion.setText("Newest version: " + releaseInformation.versionId);
			
			labelData = new FormData();
			labelData.top = new FormAttachment(currentVersionLabel, 5);
			labelData.left = new FormAttachment(0, 0);
			newestVersion.setLayoutData(labelData);
			
			if (!Main.versionId.equals(releaseInformation.versionId)) {
				Link download = new Link(bodyContainer, SWT.NONE);
				download.setText("There exists a more recent version! <a href=\""+releaseInformation.releasePath+"\">Download it here</a>");
				
				linkData = new FormData();
				linkData.top = new FormAttachment(newestVersion, 5);
				linkData.left = new FormAttachment(0, 0);
				download.setLayoutData(linkData);
			}
		} else {
			Label newVersionCheckFail = new Label(bodyContainer,SWT.NONE);
			newVersionCheckFail.setText("Failed to request newest release information. Please check the github for a new release.");
			
			labelData = new FormData();
			labelData.top = new FormAttachment(currentVersionLabel, 5);
			labelData.left = new FormAttachment(0, 0);
			newVersionCheckFail.setLayoutData(labelData);
		}

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
	
	private StyledText createSpecialThanks(String name, String comment, Composite parent) {
		StyledText label = new StyledText(parent, SWT.READ_ONLY | SWT.WRAP);
		label.setBackground(parent.getBackground());
		label.setCaret(null);
		
		String fullText = name + " - " + comment;
		label.setText(fullText);
		
		StyleRange boldRange = new StyleRange();
		boldRange.start = 0;
		boldRange.length = name.length();
		boldRange.fontStyle = SWT.BOLD;
		
		label.setStyleRange(boldRange);
		
		return label;
	}
}
