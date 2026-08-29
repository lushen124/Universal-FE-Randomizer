package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ui.general.MinMaxControl;
import ui.model.GrowthOptions;
import ui.model.MinMaxOption;
import ui.model.ShopOptions;

public class ShopView extends YuneView<ShopOptions> {
	private Boolean isEnabled = false;
	
	private Button enableButton;
	
	private MinMaxControl shopSizeControl;
	
	public ShopView(Composite parent) {
		super();
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
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setEnabled(enableButton.getSelection());
			}
		});
		
		shopSizeControl = new MinMaxControl(group, SWT.NONE, "Min Items:", "Max Items:");
		shopSizeControl.getMinSpinner().setValues(4, 0, 20, 0, 1, 5);
		shopSizeControl.getMaxSpinner().setValues(12, 0, 24, 0, 1, 5);
		shopSizeControl.setEnabled(false);

		FormData rangeData = new FormData();
		rangeData.top = new FormAttachment(enableButton, 10);
		rangeData.left = new FormAttachment(0, 5);
		rangeData.right = new FormAttachment(100, -5);
		shopSizeControl.setLayoutData(rangeData);
	}
	
	private void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		shopSizeControl.setEnabled(isEnabled);
	}

	@Override
	public void initialize(ShopOptions options) {
		if (options != null) {
			enableButton.setSelection(true);
			shopSizeControl.setEnabled(true);
			shopSizeControl.setMin(options.shopSize.minValue);
			shopSizeControl.setMax(options.shopSize.maxValue);
			isEnabled = true;
		} else {
			enableButton.setSelection(false);
			shopSizeControl.setEnabled(false);
			isEnabled = false;
		}
	}

	@Override
	public ShopOptions getOptions() {
		if (isEnabled) {
			return new ShopOptions(shopSizeControl.getMinMaxOption());
		}
		
		return null;
	}
}
