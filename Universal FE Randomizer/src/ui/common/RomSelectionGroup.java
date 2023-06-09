package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;
import ui.general.FileFlowDelegate;
import ui.general.OpenFileFlow;

public class RomSelectionGroup extends YuneGroup {
    private Button openSelectorButton;
    private Text selectedPath;

    private FileFlowDelegate delegate;

    public RomSelectionGroup(Composite parent, FileFlowDelegate delegate){
        createGroup(parent);
        // Delegate has to be applied before the Compose method is called because it is needed for adding the button listener
        this.delegate = delegate;
        compose();
    }


    @Override
    protected void compose() {
        /* Define widgets to add to the shell */
        Label romFileLabel = new Label(group, 0);
        romFileLabel.setText("ROM File:");

        selectedPath = new Text(group, SWT.BORDER);
        selectedPath.setEditable(false);

        openSelectorButton = new Button(group, SWT.PUSH);
        openSelectorButton.setText("Browse...");
        openSelectorButton.addListener(SWT.Selection, new OpenFileFlow((Shell) group.getParent().getParent().getParent(), delegate));

        FormData labelData = new FormData();
        labelData.left = new FormAttachment(group, 5);
        labelData.top = new FormAttachment(selectedPath, 0, SWT.CENTER);
        romFileLabel.setLayoutData(labelData);

        FormData fieldData = new FormData();
        fieldData.left = new FormAttachment(romFileLabel, 5);
        fieldData.top = new FormAttachment(0, 5);
        fieldData.right = new FormAttachment(openSelectorButton, -5);
        fieldData.width = 400;
        selectedPath.setLayoutData(fieldData);

        FormData buttonData = new FormData();
        buttonData.right = new FormAttachment(100, -5);
        buttonData.top = new FormAttachment(selectedPath, 0, SWT.CENTER);
        buttonData.width = 100;
        openSelectorButton.setLayoutData(buttonData);
    }

    public void setFilePath(String path) {
        this.selectedPath.setText(path);
    }

    public String getFilePath() { return this.selectedPath.getText(); }
}
