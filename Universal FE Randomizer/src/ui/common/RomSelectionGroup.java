package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;
import ui.general.FileFlowDelegate;
import ui.general.OpenFileFlow;

public class RomSelectionGroup extends YuneGroup {
    private Button openSelectorButton;
    private Label romFileLabel;
    private Text selectedPath;

    private FileFlowDelegate delegate;

    public RomSelectionGroup(Composite parent, FileFlowDelegate delegate){
        super(parent);
        this.delegate = delegate;
    }


    @Override
    protected void compose() {
        /* Define widgets to add to the shell */
        Label romFileLabel = new Label(group, 0);
        romFileLabel.setText("ROM File:");

        Text field = new Text(group, SWT.BORDER);
        field.setEditable(false);

        Button button = new Button(group, SWT.PUSH);
        button.setText("Browse...");
        button.addListener(SWT.Selection, new OpenFileFlow((Shell) group.getParent().getParent(), delegate));

        FormData labelData = new FormData();
        labelData.left = new FormAttachment(group, 5);
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
}
