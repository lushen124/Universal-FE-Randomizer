package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import fedata.general.FEBase.GameType;
import ui.general.MinMaxControl;
import ui.model.GrowthOptions;
import ui.model.MinMaxOption;
import ui.model.ShopOptions;

public class ShopView extends YuneView<ShopOptions> {
	private Boolean isEnabled = false;
	private GameType type;
	
	private Button enableButton;
	
	private MinMaxControl shopSizeControl;
	private Button progressionWeights;
	private Button armoryVendor;
	private Button secretVendor;
	private Button randomizeMapShops; // FE8 Only
	
	public ShopView(Composite parent, GameType type) {
		super();
		this.type = type;
		createGroup(parent);
		compose();
	}
	
	@Override
	public String getGroupTitle() {
		return "Shops";
	}

	@Override
	public String getGroupTooltip() {
		return "Randomizes the items sold in all shops.";
	}

	@Override
	protected void compose() {
		enableButton = new Button(group, SWT.CHECK);
		enableButton.setText("Enable Shop Randomization");
		enableButton.setToolTipText("Randomizes the items sold in every armory, vendor, and secret shop.");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setEnabled(enableButton.getSelection());
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.top = new FormAttachment(0, 5);
		buttonData.left = new FormAttachment(0, 5);
		enableButton.setLayoutData(buttonData);
		
		shopSizeControl = new MinMaxControl(group, SWT.NONE, "Min Items:", "Max Items:");
		shopSizeControl.getMinSpinner().setValues(8, 0, 20, 0, 1, 5);
		shopSizeControl.getMaxSpinner().setValues(12, 0, 24, 0, 1, 5);
		shopSizeControl.setEnabled(false);

		FormData rangeData = new FormData();
		rangeData.top = new FormAttachment(enableButton, 10);
		rangeData.left = new FormAttachment(0, 5);
		rangeData.right = new FormAttachment(100, -5);
		shopSizeControl.setLayoutData(rangeData);
		
		progressionWeights = new Button(group, SWT.CHECK);
		progressionWeights.setText("Use Progression Weights");
		progressionWeights.setToolTipText("Applies weights to the items sold so that shops later in the game are more likely to sell better or more powerful items than shops in the early game.");
		progressionWeights.setSelection(true);
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(shopSizeControl, 5);
		buttonData.left = new FormAttachment(enableButton, 5, SWT.LEFT);
		progressionWeights.setLayoutData(buttonData);
		
		armoryVendor = new Button(group, SWT.CHECK);
		armoryVendor.setText("Mix Armory and Vendor items");
		armoryVendor.setToolTipText("Allows physical weapons to be sold in vendors and magical weapons and items to be sold in armories.");
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(progressionWeights, 5);
		buttonData.left = new FormAttachment(progressionWeights, 0, SWT.LEFT);
		armoryVendor.setLayoutData(buttonData);
		
		secretVendor = new Button(group, SWT.CHECK);
		secretVendor.setText("Mix Secret and Vendor items");
		secretVendor.setToolTipText("Allows secret shop items (promotion items and stat boosters) to be sold in vendors.\n\nIf Armory and Vendor items are also mixed, allows these items to show up in Armories as well.");
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(armoryVendor, 5);
		buttonData.left = new FormAttachment(armoryVendor, 0, SWT.LEFT);
		secretVendor.setLayoutData(buttonData);
		
		if (type == GameType.FE8) {
			randomizeMapShops = new Button(group, SWT.CHECK);
			randomizeMapShops.setText("Randomize Map Shops");
			randomizeMapShops.setToolTipText("Randomizes the shops on the world map.");
			
			buttonData = new FormData();
			buttonData.top = new FormAttachment(secretVendor, 5);
			buttonData.left = new FormAttachment(secretVendor, 0, SWT.LEFT);
			randomizeMapShops.setLayoutData(buttonData);
		}
	}
	
	private void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		shopSizeControl.setEnabled(isEnabled);
		progressionWeights.setEnabled(isEnabled);
		armoryVendor.setEnabled(isEnabled);
		secretVendor.setEnabled(isEnabled);
		if (type == GameType.FE8) {
			randomizeMapShops.setEnabled(isEnabled);
		}
	}

	@Override
	public void initialize(ShopOptions options) {
		if (options != null) {
			enableButton.setSelection(true);
			shopSizeControl.setEnabled(true);
			shopSizeControl.setMin(options.shopSize.minValue);
			shopSizeControl.setMax(options.shopSize.maxValue);
			progressionWeights.setEnabled(true);
			armoryVendor.setEnabled(true);
			secretVendor.setEnabled(true);
			progressionWeights.setSelection(options.useProgressionWeights);
			armoryVendor.setSelection(options.mixArmoryVendor);
			secretVendor.setSelection(options.mixSecretVendor);
			if (type == GameType.FE8) {
				randomizeMapShops.setEnabled(true);
				randomizeMapShops.setSelection(options.randomizeMapShops);
			}
			isEnabled = true;
		} else {
			enableButton.setSelection(false);
			shopSizeControl.setEnabled(false);
			progressionWeights.setEnabled(false);
			armoryVendor.setEnabled(false);
			secretVendor.setEnabled(false);
			progressionWeights.setSelection(true);
			armoryVendor.setSelection(false);
			secretVendor.setSelection(false);
			if (type == GameType.FE8) {
				randomizeMapShops.setEnabled(false);
				randomizeMapShops.setSelection(false);
			}
			isEnabled = false;
		}
	}

	@Override
	public ShopOptions getOptions() {
		if (isEnabled) {
			boolean mapShopRandomization = false;
			if (type == GameType.FE8) {
				mapShopRandomization = randomizeMapShops.getSelection();
			}
			return new ShopOptions(shopSizeControl.getMinMaxOption(), progressionWeights.getSelection(), armoryVendor.getSelection(), secretVendor.getSelection(), mapShopRandomization);
		}
		
		return null;
	}
}
