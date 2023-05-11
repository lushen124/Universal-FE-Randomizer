package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;

public class SeedGroup extends YuneGroup {
    private Text seedField;
    private Button generateButton;

    private Button randomizeButton;

    public SeedGroup(Composite parent){
        super(parent);
    }

    @Override
    protected void compose() {
        Label seedLabel = new Label(group, SWT.NONE);
        seedLabel.setText("Randomizer Seed Phrase");
        seedField = new Text(group, SWT.BORDER);


        generateButton = new Button(group, SWT.PUSH);
        generateButton.setText("Generate");


        FormData seedFieldData = new FormData();
        seedFieldData.top = new FormAttachment(0, 0);
        seedFieldData.right = new FormAttachment(generateButton, -5);
        seedFieldData.left = new FormAttachment(seedLabel, 5);
        seedField.setLayoutData(seedFieldData);

        FormData seedLabelData = new FormData();
        seedLabelData.top = new FormAttachment(seedField, 0, SWT.CENTER);
        seedLabelData.left = new FormAttachment(0, 0);
        seedLabel.setLayoutData(seedLabelData);

        FormData generateData = new FormData();
        generateData.top = new FormAttachment(seedField, 0, SWT.CENTER);
        generateData.right = new FormAttachment(100, -5);
        generateData.width = 100;
        generateButton.setLayoutData(generateData);
    }

    public void setRandomizeButton(Button randomizeButton) {
        this.randomizeButton = randomizeButton;
        generateButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                randomizeButton.setEnabled(event.text.length() != 0);
            }
        });
    }
}
