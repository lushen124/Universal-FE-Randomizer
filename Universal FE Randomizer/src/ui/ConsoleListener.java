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

public class ConsoleListener implements Listener {
    private boolean consoleShellOpened = false;
    private Shell consoleShell;
    private Table consoleLog;

    @Override
    public void handleEvent(Event event) {
        if (((event.stateMask & SWT.CTRL) != 0) && ((event.stateMask & SWT.SHIFT) != 0) && (event.keyCode == 'c') && !consoleShellOpened) {
            openConsole();
        }
    }

    private void openConsole() {
        Display mainDisplay = Display.getDefault();
        consoleShell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX);
        consoleShell.setText("Debug Console");
        consoleShell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
        setupConsoleShell();
        consoleShell.open();
        consoleShellOpened = true;

        consoleShell.addShellListener(new ShellListener() {

            @Override
            public void shellIconified(ShellEvent e) {
            }

            @Override
            public void shellDeiconified(ShellEvent e) {
            }

            @Override
            public void shellDeactivated(ShellEvent e) {
            }

            @Override
            public void shellClosed(ShellEvent e) {
                DebugPrinter.unregisterListener("consoleLog");
                consoleShellOpened = false;
            }

            @Override
            public void shellActivated(ShellEvent e) {
            }
        });
    }

    private void setupConsoleShell() {
        consoleShell.setLayout(new FillLayout());
        consoleShell.setSize(400, 400);

        consoleLog = new Table(consoleShell, SWT.BORDER | SWT.FULL_SELECTION);
        consoleLog.setHeaderVisible(true);
        consoleLog.setLinesVisible(true);
        consoleLog.setSize(400, 400);

        TableColumn categoryColumn = new TableColumn(consoleLog, SWT.NONE);
        categoryColumn.setText("Namespace");
        categoryColumn.pack();
        TableColumn messageColumn = new TableColumn(consoleLog, SWT.NONE);
        messageColumn.setText("Message");

        consoleShell.addControlListener(new ControlListener() {
            @Override
            public void controlResized(ControlEvent e) {
                consoleLog.setSize(consoleShell.getSize());
                messageColumn.setWidth(consoleLog.getSize().x - categoryColumn.getWidth());
            }
            @Override
            public void controlMoved(ControlEvent e) {
            }
        });

        DebugPrinter.registerListener(new DebugListener() {
            @Override
            public void logMessage(String category, String message) {
                try {
                    TableItem newItem = new TableItem(consoleLog, SWT.NONE);
                    newItem.setText(0, category);
                    newItem.setText(1, message);
                } catch (SWTException e) {
                    e.printStackTrace();
                }
            }
        }, "consoleLog");
    }
}
