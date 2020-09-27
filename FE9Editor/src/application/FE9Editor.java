package application;

import org.eclipse.swt.widgets.Display;

import ui.MainView;

public class FE9Editor {
	
	static Display mainDisplay;
	static MainView mainView;
	
	public static void main(String[] args) {
		mainDisplay = new Display();
		
		mainView = new MainView(mainDisplay);
		
		while (!mainView.mainShell.isDisposed()) {
			if (!mainDisplay.readAndDispatch()) {
				mainDisplay.sleep();
			}
		}
		
		mainDisplay.dispose();
	}

}
