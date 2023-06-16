package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;
import ui.model.PrfOptions;

/**
 * View with options to make sure that the Lord will have a prf Weapon
 */
public class PrfView extends YuneView<PrfOptions> {
    Button createPrfsButton;
    Button unbreakablePrfsButton;

    public PrfView(Composite parent) {
        super(parent);
    }

    @Override
    public String getGroupTitle() {
        return "Prf Weapons";
    }

    @Override
    protected void compose() {
        createPrfsButton = new Button(group, SWT.CHECK);
        createPrfsButton.setText("Create Matching Prf Weapons");
        createPrfsButton.setToolTipText("If enabled, the randomizer will ensure that the Lord Character(s) have prf weapons which they can use.");

        FormData prfWeaponsData = new FormData();
        prfWeaponsData.left = new FormAttachment(0, 0);
        prfWeaponsData.top = new FormAttachment(0, 0);
        createPrfsButton.setLayoutData(prfWeaponsData);

        unbreakablePrfsButton = new Button(group, SWT.CHECK);
        unbreakablePrfsButton.setText("Make Prf Weapons Unbreakable");
        unbreakablePrfsButton.setToolTipText("If enabled, newly created Prf weapons will have infinite durability.");
        unbreakablePrfsButton.setEnabled(false);

        FormData unbreakablePrfData = new FormData();
        unbreakablePrfData.left = new FormAttachment(createPrfsButton, 10, SWT.LEFT);
        unbreakablePrfData.top = new FormAttachment(createPrfsButton, 5);
        unbreakablePrfsButton.setLayoutData(unbreakablePrfData);
        createPrfsButton.addListener(SWT.Selection, event -> unbreakablePrfsButton.setEnabled(createPrfsButton.getSelection()));
    }

    @Override
    public PrfOptions getOptions() {
        return new PrfOptions(createPrfsButton.getSelection(), unbreakablePrfsButton.getSelection());
    }

    @Override
    public void initialize(PrfOptions options) {
        if (options == null) return;
        this.createPrfsButton.setSelection(options.createPrfs);
        this.unbreakablePrfsButton.setSelection(options.createPrfs && options.unbreakablePrfs);
    }
}
