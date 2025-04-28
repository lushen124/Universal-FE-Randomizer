package ui.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class LabelCheckboxView extends Composite {

	private Label label;
	private List<Button> checkboxes;
	
	public LabelCheckboxView(Composite parent, int style, String labelString, List<String> checkboxStrings, boolean useRadioButtons) {
		super(parent, style);
		
		setLayout(new FormLayout());
		
		label = new Label(this, SWT.NONE);
		label.setText(labelString);
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.top = new FormAttachment(0, 0);
		label.setLayoutData(labelData);
		
		checkboxes = new ArrayList<Button>();
		Button previousCheckbox = null;
		
		for (String string : checkboxStrings) {
			Button newCheckbox = new Button(this, useRadioButtons ? SWT.RADIO : SWT.CHECK);
			newCheckbox.setText(string);
			newCheckbox.setEnabled(false);
			
			checkboxes.add(newCheckbox);
			
			FormData viewData = new FormData();
			if (previousCheckbox != null) {
				viewData.top = new FormAttachment(previousCheckbox, 10);
				viewData.left = new FormAttachment(previousCheckbox, 0, SWT.LEFT);
				viewData.right = new FormAttachment(100, 0);
			} else {
				viewData.top = new FormAttachment(0, 0);
				viewData.left = new FormAttachment(label, 10);
				viewData.right = new FormAttachment(100, 0);
			}
			newCheckbox.setLayoutData(viewData);
			previousCheckbox = newCheckbox;
		}
		
		FormData lastData = (FormData)previousCheckbox.getLayoutData();
		lastData.bottom = new FormAttachment(100, 0);
		previousCheckbox.setLayoutData(lastData);
		
		addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point computedLabelSize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				
				FormData labelData = new FormData();
				labelData.left = new FormAttachment(0, 0);
				labelData.top = new FormAttachment(0, 0);
				labelData.width = computedLabelSize.x;
				label.setLayoutData(labelData);	
				
				layout();
			}
		});
	}
	
	public void clearAllCheckboxes() {
		for (Button checkbox : checkboxes) {
			checkbox.setSelection(false);
		}
	}
	
	public void setCheckboxes(List<String> checked) {
		for (Button checkbox : checkboxes) {
			checkbox.setSelection(checked.contains(checkbox.getText()));
		}
	}
}
