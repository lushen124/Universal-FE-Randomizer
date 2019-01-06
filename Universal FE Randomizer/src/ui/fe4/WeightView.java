package ui.fe4;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

public class WeightView extends Composite {

	private Button enableToggle;
	
	private Composite weightContainer;
	private Button veryLowWeight;
	private Button lowWeight;
	private Button normalWeight;
	private Button highWeight;
	private Button veryHighWeight;
	
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
			}
		});
		
		weightContainer = new Composite(this, SWT.NONE);
		
		weightContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		veryLowWeight = new Button(weightContainer, SWT.RADIO);
		veryLowWeight.setEnabled(enableToggle.getSelection());
		veryLowWeight.setSelection(defaultWeight == WeightedOptions.Weight.VERY_LOW);
		
		lowWeight = new Button(weightContainer, SWT.RADIO);
		lowWeight.setEnabled(enableToggle.getSelection());
		lowWeight.setSelection(defaultWeight == WeightedOptions.Weight.LOW);
		
		normalWeight = new Button(weightContainer, SWT.RADIO);
		normalWeight.setEnabled(enableToggle.getSelection());
		normalWeight.setSelection(defaultWeight == WeightedOptions.Weight.NORMAL);
		
		highWeight = new Button(weightContainer, SWT.RADIO);
		highWeight.setEnabled(enableToggle.getSelection());
		highWeight.setSelection(defaultWeight == WeightedOptions.Weight.HIGH);
		
		veryHighWeight = new Button(weightContainer, SWT.RADIO);
		veryHighWeight.setEnabled(enableToggle.getSelection());
		veryHighWeight.setSelection(defaultWeight == WeightedOptions.Weight.VERY_HIGH);
		
		FormData toggleData = new FormData();
		toggleData.left = new FormAttachment(0, 0);
		toggleData.top = new FormAttachment(weightContainer, 0, SWT.CENTER);
		enableToggle.setLayoutData(toggleData);
		
		FormData weightContainerData = new FormData();
		weightContainerData.right = new FormAttachment(100, 0);
		weightContainerData.left = new FormAttachment(0, 100);
		weightContainer.setLayoutData(weightContainerData);
	}
	
	public void setSelected(boolean selected) {
		if (selected && !enableToggle.getEnabled()) { return; }
		enableToggle.setSelection(selected);
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
	
	public WeightedOptions getWeightedOptions() {
		if (enableToggle.getSelection()) {
			WeightedOptions.Weight weight = WeightedOptions.Weight.VERY_LOW;
			if (lowWeight.getSelection()) { weight = WeightedOptions.Weight.LOW; }
			else if (normalWeight.getSelection()) { weight = WeightedOptions.Weight.NORMAL; }
			else if (highWeight.getSelection()) { weight = WeightedOptions.Weight.HIGH; }
			else if (veryHighWeight.getSelection()) { weight = WeightedOptions.Weight.VERY_HIGH; }
			
			return new WeightedOptions(true, weight);
		} else {
			return new WeightedOptions(false, WeightedOptions.Weight.NONE);
		}
	}
}
