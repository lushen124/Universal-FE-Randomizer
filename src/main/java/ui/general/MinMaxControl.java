package ui.general;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import ui.model.MinMaxOption;

public class MinMaxControl extends Composite {
	
	private Spinner minSpinner;
	private Spinner maxSpinner;
	
	private Label minLabel;
	private Label maxLabel;

	public MinMaxControl(Composite parent, int style, String minString, String maxString) {
		super(parent, style);
		setLayout(new FormLayout());
		
		minLabel = new Label(this, SWT.RIGHT);
		minLabel.setText(minString);
		
		maxLabel = new Label(this, SWT.RIGHT);
		maxLabel.setText(maxString);
		
		minSpinner = new Spinner(this, SWT.NONE);
		
		maxSpinner = new Spinner(this, SWT.NONE);
		
		minSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				int newMinValue = minSpinner.getSelection();
				maxSpinner.setMinimum(newMinValue);
				int currentMaxValue = maxSpinner.getSelection();
				if (currentMaxValue < newMinValue) {
					maxSpinner.setSelection(newMinValue);
				}
				
				layout(true);
			}
		});
		
		maxSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				int newMaxValue = maxSpinner.getSelection();
				minSpinner.setMaximum(newMaxValue);
				int currentMinValue = minSpinner.getSelection();
				if (currentMinValue > newMaxValue) {
					minSpinner.setSelection(newMaxValue);
				}
				
				layout(true);
			}
		});
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		maxSpinner.setLayoutData(spinnerData);
		
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(maxSpinner, -5);
		labelData.top = new FormAttachment(maxSpinner, 0, SWT.CENTER);
		maxLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(maxLabel, -5);
		minSpinner.setLayoutData(spinnerData);
		
		labelData = new FormData();
		labelData.right = new FormAttachment(minSpinner, -5);
		labelData.top = new FormAttachment(minSpinner, 0, SWT.CENTER);
		minLabel.setLayoutData(labelData);
	}
	
	public Spinner getMinSpinner() {
		return minSpinner;
	}
	
	public Spinner getMaxSpinner() {
		return maxSpinner;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		minLabel.setEnabled(enabled);
		maxLabel.setEnabled(enabled);
		minSpinner.setEnabled(enabled);
		maxSpinner.setEnabled(enabled);
	}
	
	public MinMaxOption getMinMaxOption() {
		return new MinMaxOption(minSpinner.getSelection(), maxSpinner.getSelection());
	}
	
	public void setMin(int min) {
		minSpinner.setSelection(min);
	}
	
	public void setMax(int max) {
		maxSpinner.setSelection(max);
	}

}
