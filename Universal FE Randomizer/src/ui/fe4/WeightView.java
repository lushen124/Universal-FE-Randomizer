package ui.fe4;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ui.fe4.WeightedOptions.Weight;

public class WeightView extends Composite {

	private Button enableToggle;
	
	private Composite weightContainer;
	private Button veryLowWeight;
	private Button lowWeight;
	private Button normalWeight;
	private Button highWeight;
	private Button veryHighWeight;
	
	private WeightViewListener listener;
	private Boolean squelchCallbacks = false;
	
	private WeightedOptions.Weight currentWeight;
	
	public interface WeightViewListener {
		public void onWeightChanged(WeightedOptions.Weight oldWeight, WeightedOptions.Weight newWeight);
		public void onItemEnabled();
		public void onItemDisabled();
	}
	
	public void setListener(WeightViewListener listener) {
		this.listener = listener;
	}
	
	private void notifyItemEnabled() {
		if (listener != null && !squelchCallbacks) {
			listener.onItemEnabled();
		}
	}
	
	private void notifyItemDisabled() {
		if (listener != null && !squelchCallbacks) {
			listener.onItemDisabled();
		}
	}
	
	private void notifyWeightChanged(WeightedOptions.Weight oldWeight, WeightedOptions.Weight newWeight) {
		currentWeight = newWeight;
		if (listener != null && !squelchCallbacks) {
			listener.onWeightChanged(oldWeight, newWeight);
		}
	}
	
	public WeightView(String name, WeightedOptions.Weight defaultWeight, Composite parent, int style) {
		super(parent, style);
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		setLayout(mainLayout);
		
		enableToggle = new Button(this, SWT.CHECK);
		enableToggle.setText(name);
		enableToggle.setSelection(defaultWeight != WeightedOptions.Weight.NONE);
		enableToggle.setEnabled(true);
		enableToggle.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				veryLowWeight.setEnabled(enableToggle.getSelection());
				lowWeight.setEnabled(enableToggle.getSelection());
				normalWeight.setEnabled(enableToggle.getSelection());
				highWeight.setEnabled(enableToggle.getSelection());
				veryHighWeight.setEnabled(enableToggle.getSelection());
				
				if (enableToggle.getSelection()) {
					notifyItemEnabled();
				} else {
					notifyItemDisabled();
				}
			}
		});
		
		weightContainer = new Composite(this, SWT.NONE);
		
		weightContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		veryLowWeight = new Button(weightContainer, SWT.RADIO);
		veryLowWeight.setEnabled(enableToggle.getSelection());
		veryLowWeight.setSelection(defaultWeight == WeightedOptions.Weight.VERY_LOW);
		veryLowWeight.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifyWeightChanged(currentWeight, WeightedOptions.Weight.VERY_LOW);
			}
		});
		
		lowWeight = new Button(weightContainer, SWT.RADIO);
		lowWeight.setEnabled(enableToggle.getSelection());
		lowWeight.setSelection(defaultWeight == WeightedOptions.Weight.LOW);
		lowWeight.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifyWeightChanged(currentWeight, WeightedOptions.Weight.LOW);
			}
		});
		
		normalWeight = new Button(weightContainer, SWT.RADIO);
		normalWeight.setEnabled(enableToggle.getSelection());
		normalWeight.setSelection(defaultWeight == WeightedOptions.Weight.NORMAL);
		normalWeight.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifyWeightChanged(currentWeight, WeightedOptions.Weight.NORMAL);
			}
		});
		
		highWeight = new Button(weightContainer, SWT.RADIO);
		highWeight.setEnabled(enableToggle.getSelection());
		highWeight.setSelection(defaultWeight == WeightedOptions.Weight.HIGH);
		highWeight.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifyWeightChanged(currentWeight, WeightedOptions.Weight.HIGH);
			}
		});
		
		veryHighWeight = new Button(weightContainer, SWT.RADIO);
		veryHighWeight.setEnabled(enableToggle.getSelection());
		veryHighWeight.setSelection(defaultWeight == WeightedOptions.Weight.VERY_HIGH);
		veryHighWeight.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifyWeightChanged(currentWeight, WeightedOptions.Weight.VERY_HIGH);
			}
		});
		
		FormData toggleData = new FormData();
		toggleData.left = new FormAttachment(0, 0);
		toggleData.top = new FormAttachment(weightContainer, 0, SWT.CENTER);
		enableToggle.setLayoutData(toggleData);
		
		FormData weightContainerData = new FormData();
		weightContainerData.right = new FormAttachment(100, 0);
		weightContainerData.left = new FormAttachment(0, 100);
		weightContainer.setLayoutData(weightContainerData);
		
		currentWeight = defaultWeight;
	}
	
	public void setSelected(boolean selected) {
		if (selected && !enableToggle.getEnabled()) { return; }
		squelchCallbacks = true;
		enableToggle.setSelection(selected);
		veryLowWeight.setEnabled(selected);
		lowWeight.setEnabled(selected);
		normalWeight.setEnabled(selected);
		highWeight.setEnabled(selected);
		veryHighWeight.setEnabled(selected);
		squelchCallbacks = false;
	}
	
	public void setEnabled(boolean enabled) {
		enableToggle.setEnabled(enabled);
		
		if (enabled) {
			veryLowWeight.setEnabled(enableToggle.getSelection());
			lowWeight.setEnabled(enableToggle.getSelection());
			normalWeight.setEnabled(enableToggle.getSelection());
			highWeight.setEnabled(enableToggle.getSelection());
			veryHighWeight.setEnabled(enableToggle.getSelection());
		} else {
			veryLowWeight.setEnabled(false);
			lowWeight.setEnabled(false);
			normalWeight.setEnabled(false);
			highWeight.setEnabled(false);
			veryHighWeight.setEnabled(false);
		}
	}
	
	public boolean optionEnabled() {
		return enableToggle.getSelection();
	}
	
	public void setWeight(WeightedOptions.Weight newWeight) {
		setEnabled(newWeight != WeightedOptions.Weight.NONE);
		currentWeight = newWeight;
		
		squelchCallbacks = true;
		veryLowWeight.setSelection(newWeight == Weight.VERY_LOW);
		lowWeight.setSelection(newWeight == Weight.LOW);
		normalWeight.setSelection(newWeight == Weight.NORMAL);
		highWeight.setSelection(newWeight == Weight.HIGH);
		veryHighWeight.setSelection(newWeight == Weight.VERY_HIGH);
		squelchCallbacks = false;
	}
	
	public WeightedOptions getWeightedOptions() {
		if (enableToggle.getSelection()) {
			return new WeightedOptions(true, currentWeight);
		} else {
			return new WeightedOptions(false, WeightedOptions.Weight.NONE);
		}
	}
	
	public void setWeightedOptions(WeightedOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			setSelected(options.enabled);
			if (options.weight == WeightedOptions.Weight.NONE) {
				// Shouldn't happen.
			} else {
				setWeight(options.weight);
			}
		}
	}
}
