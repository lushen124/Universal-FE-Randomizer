package ui.importexport;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import random.exc.UnsupportedGameException;
import ui.MainView;
import util.OptionRecorder;
import util.OptionRecorder.Bundle;
import util.OptionRecorder.FE4OptionBundle;
import util.OptionRecorder.FE9OptionBundle;
import util.OptionRecorder.GBAOptionBundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Button listener for Importing a Bundle Json file.
 */
public class ImportSettingsListener extends AbstractImportExportListener {

    public ImportSettingsListener(Shell mainShell, MainView mainView, GameType type) {
        super(mainShell, mainView, type);
    }

    public void handleEvent(Event event) {
        // Let the user select a Json file
        performFileSelection(SWT.OPEN);

        // Validate the selection
        if (!isValidFileSelection() || !isJsonFile()) {
            return;
        }

        try {
            // Start reading the file line by line, and try figuring
            // out what game the File is for, assuming it is a valid Export.
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
            String line;
            StringBuilder fileContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }

            // Parse the selected file into a Base Bundle, since that contains the GameType
            Gson gson = new Gson();
            Bundle baseBundle;
            try {
                if (fileContent.length() == 0) {
                   throw new JsonSyntaxException("Empty files get successfully parsed to a Bundle, don't allow that");
                }
                baseBundle = gson.fromJson(fileContent.toString(), Bundle.class);
            } catch (JsonSyntaxException e) {
                showError("The Selected Json file does not contain a valid Bundle.%n Please check the selected file.");
                return;
            }

            // Validate the GameType of the Bundle against the currently loaded one.
            if (!baseBundle.type.equals(type)) {
                showError("The Selected Settings are not valid for the currently loaded gameType. %n Expected %s was %s", type, baseBundle.type);
                return;
            }

            // Depending on the GameType of the Base Bundle, select the Final class to parse the bundle to.
            Class clazz;
            switch (baseBundle.type) {
                case FE6: case FE7: case FE8: clazz = GBAOptionBundle.class; break;
                case FE4: clazz = FE4OptionBundle.class; break;
                case FE9: clazz = FE9OptionBundle.class; break;
                default: throw new UnsupportedGameException();
            }

            // Parse the Bundle into the game of the correct GameType
            Object bundle = gson.fromJson(fileContent.toString(), clazz);

            // Update the Bundle in the OptionRecorder
            switch(type) {
                case FE4: OptionRecorder.options.fe4 = (FE4OptionBundle) bundle; break;
                case FE6: OptionRecorder.options.fe6 = (GBAOptionBundle) bundle; break;
                case FE7: OptionRecorder.options.fe7 = (GBAOptionBundle) bundle; break;
                case FE8: OptionRecorder.options.fe8 = (GBAOptionBundle) bundle; break;
                case FE9: OptionRecorder.options.fe9 = (FE9OptionBundle) bundle; break;
            }

            // Make the mainview reload the selected options
            mainView.preloadOptions(type);
        } catch (IOException | UnsupportedGameException e) {
            showError(e.getMessage());
        }
    }

    @Override
    protected boolean validateFileExistence() {
        if (!new File(selectedFile).exists()) {
            showError("File couldn't be found.");
            return false;
        }
        return true;
    }

}
