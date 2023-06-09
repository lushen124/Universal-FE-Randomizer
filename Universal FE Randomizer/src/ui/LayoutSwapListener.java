package ui;

import application.Main;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import util.DebugListener;
import util.DebugPrinter;

/**
 * Listens for keyboard inputs that are mapped to cause the currently used {@link YuneViewContainer} to be swapped.
 */
public class LayoutSwapListener implements Listener {

    private MainView mainView;

    public LayoutSwapListener(MainView mainView){
        this.mainView = mainView;
    }

    @Override
    public void handleEvent(Event event) {
        if (((event.stateMask & SWT.CTRL) != 0) && (event.keyCode == '1')) {
            mainView.swapLayout(1);
        }
        if (((event.stateMask & SWT.CTRL) != 0) && (event.keyCode == '2')) {
            mainView.swapLayout(2);
        }
    }

}
