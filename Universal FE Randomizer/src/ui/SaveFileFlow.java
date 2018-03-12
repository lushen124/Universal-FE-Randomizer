package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class SaveFileFlow implements Listener {

	Shell parent;
	FileFlowDelegate delegate;

	public SaveFileFlow(Shell parent, FileFlowDelegate delegate) {
		super();
		this.parent = parent;
		this.delegate = delegate;
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		FileDialog openDialog = new FileDialog(parent, SWT.OPEN);
		delegate.onSelectedFile(openDialog.open());
	}
}
