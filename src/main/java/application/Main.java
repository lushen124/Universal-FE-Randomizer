package application;

import org.eclipse.swt.widgets.*;
import ui.MainView;

public class Main {
    //for the patches look at how FE6Localization_v1.1br1.ups is run!

    public static final String versionId = "0.9.21";

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
