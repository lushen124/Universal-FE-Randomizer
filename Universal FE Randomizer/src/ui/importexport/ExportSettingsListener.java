package ui.importexport;

import com.google.gson.Gson;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import ui.MainView;
import ui.general.MessageModal;
import util.DebugPrinter;
import util.OptionRecorder;

import java.io.*;

/**
 * Button listener for Importing a Bundle Json file.
 */
public class ExportSettingsListener extends AbstractImportExportListener {

    public ExportSettingsListener(Shell mainShell, MainView mainView, GameType type) {
        super(mainShell, mainView, type);
    }

    @Override
    public void handleEvent(Event event) {
        // Let user select where to save the file
        performFileSelection(SWT.SAVE);

        // Validate the selection
        if (!isValidFileSelection() || !isJsonFile()) {
            return;
        }

        // Make sure that the currently selected options are saved.
        mainView.triggerOptionSave(type);

        // Write the OptionBundle of the current game into a new File
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));) {
            OptionRecorder.Bundle bundle = OptionRecorder.getBundle(type);
            Gson gson = new Gson();
            String jsonString = gson.toJson(bundle);
            writer.write(jsonString);
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    @Override
    protected boolean validateFileExistence() {
        File file = new File(selectedFile);
        if (file.exists()) {
            MessageModal confirm = new MessageModal(mainShell, "Confirm", "The Selected file already exists, should it be overriden?");
            confirm.addButton("Yes", () -> confirm.hide()); // if yes do nothing
            confirm.addButton("No", () -> {
                selectedFile = null;
                confirm.hide();
            });
            confirm.show();
        }
        return selectedFile != null;
    }
}
