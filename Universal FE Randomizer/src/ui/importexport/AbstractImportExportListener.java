package ui.importexport;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import ui.MainView;
import ui.general.MessageModal;

/**
 * Abstract Base class for the import and export Listeners which contains the
 * common functionality between teh two, such as most validations, and the file selection flow
 */
public abstract class AbstractImportExportListener implements Listener {
    protected Shell mainShell;
    protected MainView mainView;
    protected FEBase.GameType type;
    protected String selectedFile;

    public AbstractImportExportListener(Shell mainShell, MainView mainView, FEBase.GameType type) {
        this.mainShell = mainShell;
        this.mainView = mainView;
        this.type = type;
    }

    public abstract void handleEvent(Event event);


    /**
     * Shows an error Modal to the user with the given Format String
     */
    protected void showError(String errorMessage, Object... args) {
        new MessageModal(mainShell, "Error", String.format("Import failed with error: %n" + errorMessage, args)).show();
    }

    /**
     * Opens the file dialog with the given style, and sets the {@link #selectedFile} with the result of the dialog selection.
     */
    protected void performFileSelection(int process) {
        FileDialog openDialog = new FileDialog(mainShell, process);
        openDialog.setFilterExtensions(new String[]{"*.json"});
        openDialog.setFilterNames(new String[]{"*.json"});
        selectedFile = openDialog.open();
    }

    /**
     * Defines what should happen if the selected file already exists
     */
    protected abstract boolean validateFileExistence();

    /**
     * Validates that the user actually selected a File
     */
    protected boolean isValidFileSelection() {
        if (selectedFile == null || selectedFile.length() == 0) {
            // nothing selected
            return false;
        }

        return validateFileExistence();
    }

    /**
     * Validates if the File the user had selected a valid Json file for the import / export.
     * For this Purpose that just means that the file name ends on .json
     * <p>
     * If it doesn't then show an error to the user, and they'll have to try again.
     */
    protected boolean isJsonFile() {
        if (!selectedFile.endsWith(".json")) {
            // Case that the user selected something that is not a json file.
            showError("The import/export only works with JSON files. %n Make sure that the file you want to import/export ends on .json");
            return false;
        }
        return true;
    }
}
