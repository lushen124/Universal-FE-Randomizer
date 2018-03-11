package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class MinMaxControl extends Composite {
	
	private Spinner minSpinner;
	private Spinner maxSpinner;

	public MinMaxControl(Composite parent, int style, String minString, String maxString) {
		super(parent, style);
		
		FormLayout layout = new FormLayout();
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 5;
		layout.marginBottom = 5;
		setLayout(layout);
		
		Label minLabel = new Label(this, SWT.RIGHT);
		minLabel.setText(minString);
		
		Label maxLabel = new Label(this, SWT.RIGHT);
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
			}
		});
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(minSpinner, -5);
		labelData.top = new FormAttachment(minSpinner, 0, SWT.CENTER);
		minLabel.setLayoutData(labelData);
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(50, -5);
		minSpinner.setLayoutData(spinnerData);
		
		labelData = new FormData();
		labelData.left = new FormAttachment(50, 5);
		labelData.right = new FormAttachment(maxSpinner, -5);
		labelData.top = new FormAttachment(maxSpinner, 0, SWT.CENTER);
		maxLabel.setLayoutData(labelData);
		
		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		maxSpinner.setLayoutData(spinnerData);
	}
	
	public Spinner getMinSpinner() {
		return minSpinner;
	}
	
	public Spinner getMaxSpinner() {
		return maxSpinner;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		minSpinner.setEnabled(enabled);
		maxSpinner.setEnabled(enabled);
	}
	
	

}
