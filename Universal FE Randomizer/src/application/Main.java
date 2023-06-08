package application;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import ui.MainView;

public class Main {

    static Display mainDisplay;
    static MainView mainView;

    public static void main(String[] args) {

        /* Instantiate Display object, it represents SWT session */
        mainDisplay = new Display();

        mainView = new MainView(mainDisplay);


        while (!mainView.mainShell.isDisposed()) {
            if (!mainDisplay.readAndDispatch())
                mainDisplay.sleep();
        }

        /* Dispose the display */
        mainDisplay.dispose();
    }

}
