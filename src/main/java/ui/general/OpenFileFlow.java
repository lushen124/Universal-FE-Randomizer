package ui.general;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class OpenFileFlow implements Listener {
	
	Shell parent;
	FileFlowDelegate delegate;
	
	String[] filterNames;
	String[] filterExtensions;

	public OpenFileFlow(Shell parent, FileFlowDelegate delegate) {
		super();
		this.parent = parent;
		this.delegate = delegate;
		
		filterExtensions = new String[] {"*.gba;*.smc;*.sfc;*.iso", "*"};
		filterNames = new String[] {"*.gba, *.smc, *.sfc, *.iso", "All Files (*.*)"};
	}
	
	public OpenFileFlow(Shell parent, FileFlowDelegate delegate, String[] extensions, String[] names) {
		super();
		this.parent = parent;
		this.delegate = delegate;
		
		filterNames = names;
		filterExtensions = extensions;
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		FileDialog openDialog = new FileDialog(parent, SWT.OPEN);
		openDialog.setFilterExtensions(filterExtensions);
		openDialog.setFilterNames(filterNames);
		delegate.onSelectedFile(openDialog.open());
	}

}
