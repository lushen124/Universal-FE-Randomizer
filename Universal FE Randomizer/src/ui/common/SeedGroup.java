package ui.common;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;
import util.SeedGenerator;

public class SeedGroup extends YuneGroup {
    private Text seedField;
    private Button generateButton;

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

    public void addGenerateButtonListener(GameType type) {
        // First remove old listeners (previous game)
        for (Listener listener : generateButton.getListeners(SWT.Selection)) {
            generateButton.removeListener(SWT.Selection, listener);
        }

        // Now add a new listener with the current game
        generateButton.addListener(SWT.Selection, selectionEvent -> {
            seedField.setText(SeedGenerator.generateRandomSeed(type));
        });
    }

    public String getSeed() {
        return this.seedField.getText();
    }

    public void setSeedFieldText(String seed) {
        this.seedField.setText(seed);
    }
}
