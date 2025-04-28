package ui.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class LabelFieldView extends Composite {
	
	private Label label;
	private Text field;

	public LabelFieldView(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new FormLayout());
		
		label = new Label(this, SWT.NONE);
		label.setText("Label");
		
		field = new Text(this, SWT.BORDER | SWT.RIGHT);
		field.setText("Value");
		field.setEditable(false);
		
		FormData fieldData = new FormData();
		fieldData.right = new FormAttachment(100, 0);
		fieldData.top = new FormAttachment(0, 0);
		fieldData.bottom = new FormAttachment(100, 0);
		field.setLayoutData(fieldData);
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.right = new FormAttachment(field, -5);
		labelData.top = new FormAttachment(field, 0, SWT.CENTER);
		label.setLayoutData(labelData);
		
		addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point computedLabelSize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				
				FormData fieldData = new FormData();
				fieldData.right = new FormAttachment(100, 0);
				fieldData.top = new FormAttachment(0, 0);
				fieldData.bottom = new FormAttachment(100, 0);
				fieldData.left = new FormAttachment(label, 5);
				field.setLayoutData(fieldData);
				
				FormData labelData = new FormData();
				labelData.left = new FormAttachment(0, 0);
				labelData.top = new FormAttachment(field, 0, SWT.CENTER);
				labelData.width = computedLabelSize.x;
				label.setLayoutData(labelData);	
				
				requestLayout();
			}
		});
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		label.setEnabled(enabled);
		field.setEnabled(enabled);
	}
	
	public void setLabel(String text) {
		label.setText(text);
		Point computedLabelSize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 0);
		labelData.top = new FormAttachment(field, 0, SWT.CENTER);
		labelData.width = computedLabelSize.x;
		label.setLayoutData(labelData);	
		requestLayout();
	}

	public void setField(String value) {
		if (value == null) { value = "(null)"; }
		field.setText(value);
		requestLayout();
	}
}
