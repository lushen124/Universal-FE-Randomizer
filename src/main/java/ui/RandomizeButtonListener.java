package ui;

import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import fedata.snes.fe4.FE4Data;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import random.gba.randomizer.GBARandomizer;
import random.gcnwii.fe9.randomizer.FE9Randomizer;
import random.general.Randomizer;
import random.general.RandomizerListener;
import random.snes.fe4.randomizer.FE4Randomizer;
import ui.general.MessageModal;
import ui.general.ModalButtonListener;
import util.Bundle;
import util.DiffCompiler;
import util.OptionRecorder;
import util.OptionRecorder.FE4OptionBundle;
import util.OptionRecorder.FE9OptionBundle;
import util.OptionRecorder.GBAOptionBundle;
import util.recordkeeper.fe9.*;
import util.recordkeeper.RecordKeeper;

import java.io.IOException;

/**
 * Listener for when the user clicks on the Randomize button.
 *
 * Prompts the user to select the target file location, and then starts the randomizer thread.
 */
public class RandomizeButtonListener implements Listener {

    private MainView mainView;
    private GameType type;

    /**
     * Default constructor that sets the back reference to the MainView and the GameType
     */
    public RandomizeButtonListener(MainView mainView, GameType type) {
        this.mainView = mainView;
        this.type = type;
    }

    /**
     * Setter for the current GameType.
     * Used so that the Listener doesn't always have to be recreated whenever the game changes.
     */
    public void updateGameType(GameType type) {
        this.type = type;
    }

    @Override
    public void handleEvent(Event event) {
        FileDialog openDialog = new FileDialog(mainView.mainShell, SWT.SAVE);
        openDialog.setFilterExtensions(type.getFileExtensions());
        String writePath = openDialog.open();
        String sourceFile = mainView.romSelection.getFilePath();

        if (writePath == null || writePath.length() == 0) {
            // No target path given (i.e. cancel), so do nothing
            return;
        }

        // if the user selected the same rom name as the one loaded, we can't just overwrite it, so append a (Randomized) after the name of the file, but before the extension.
        if (writePath.equals(sourceFile)) {
            String fileName = writePath.substring(0, writePath.length() - 4);
            String extension = writePath.substring(writePath.length() - 4);
            writePath = String.format("%s%s%s", fileName, " (Randomized)", extension);
        }
        DiffCompiler compiler = new DiffCompiler();

        if (type == GameType.FE7) {
            try {
                compiler.addDiffsFromFile("tutorialSlayer");
            } catch (IOException e) {
                MessageBox tutorialSlayerFail = new MessageBox(mainView.mainShell, SWT.ICON_ERROR | SWT.OK | SWT.CANCEL);
                tutorialSlayerFail.setText("Error");
                tutorialSlayerFail.setMessage("Failed to patch the tutorial slayer.\n\nThe randomizer can continue, but it is recommended that Lyn Normal mode not be used.");
                int selectedButton = tutorialSlayerFail.open();
                if (selectedButton == SWT.CANCEL) {
                    return;
                }
            }
        }

        Randomizer randomizer = null;

        // Start building the new Option Bundle
        Bundle baseBundle = OptionRecorder.createBundle(type);
        baseBundle.seed = mainView.seedGroup.getSeed();
        // Make the View Container update the settings they contain into the bundle
        mainView.viewContainer.updateOptionBundle(baseBundle);
        if (type.isGBA()) {
            GBAOptionBundle bundle = (GBAOptionBundle) baseBundle;
            // Update the Bundle in the Option Recorder
            OptionRecorder.recordGBAFEOptions(bundle, type);
            randomizer = new GBARandomizer(sourceFile, writePath, type, compiler, bundle.growths, bundle.bases, bundle.classes, bundle.weapons, bundle.other, bundle.enemies, bundle.otherOptions, bundle.recruitmentOptions, bundle.itemAssignmentOptions, bundle.characterShufflingOptions, bundle.statboosterOptions, bundle.rewards, bundle.prfs, bundle.shopOptions, bundle.seed);
        } else if (type.isSFC()) {
            // Update the Bundle in the Option Recorder
            FE4OptionBundle bundle = (FE4OptionBundle) baseBundle;
            OptionRecorder.recordFE4Options(bundle);
            boolean headeredROM = mainView.romInfo.getCrc32() == FE4Data.CleanHeaderedCRC32;
            randomizer = new FE4Randomizer(sourceFile, headeredROM, writePath, compiler, bundle.growths, bundle.bases, bundle.holyBlood, bundle.skills, bundle.classes, bundle.promo, bundle.enemyBuff, bundle.mechanics, bundle.rewards, bundle.seed);
        } else if (type.isGCN()) {
            // Update the Bundle in the Option Recorder
            FE9OptionBundle bundle = (FE9OptionBundle) baseBundle;
            OptionRecorder.recordFE9Options(bundle);
            randomizer = new FE9Randomizer(sourceFile, writePath, bundle.growths, bundle.bases, bundle.skills, bundle.otherOptions, bundle.enemyBuff, bundle.classes, bundle.weapons, bundle.mechanics, bundle.rewards, bundle.seed);
        }

        final String romPath = writePath;
        randomizer.setListener(new RandomizerListener() {

            @Override
            public void onStatusUpdate(String status) {
                mainView.setProgressDialogText(status);
            }

            @Override
            public void onComplete(RecordKeeper rk, ChangelogBuilder cb) {
                mainView.hideModalProgressDialog();
                MessageModal randomSuccess;
                if (rk != null) {
                    randomSuccess = new MessageModal(mainView.mainShell, "Success", "Finished Randomizing!\n\nSave changelog?");
                    randomSuccess.addButton("Yes", new ModalButtonListener() {
                        @Override
                        public void onSelected() {
                            randomSuccess.hide();
                            FileDialog openDialog = new FileDialog(mainView.mainShell, SWT.SAVE);
                            openDialog.setFilterExtensions(new String[]{"*.html"});
                            String writePath = openDialog.open();
                            if (writePath != null) {
                                Boolean success = rk.exportRecordsToHTML(writePath);
                                if (success) {
                                    MessageModal saveSuccess = new MessageModal(mainView.mainShell, "Success", "Changelog saved.");
                                    saveSuccess.show();
                                } else {
                                    MessageModal saveFail = new MessageModal(mainView.mainShell, "Error", "Failed to write changelog.");
                                    saveFail.show();
                                }
                            }
                        }
                    });
                    randomSuccess.addButton("No", new ModalButtonListener() {
                        public void onSelected() {
                            randomSuccess.hide();
                        }
                    });
                } else if (cb != null) {
                    randomSuccess = new MessageModal(mainView.mainShell, "Success", "Finished Randomizing!\n\nSave changelog?");
                    randomSuccess.addButton("Yes", new ModalButtonListener() {
                        @Override
                        public void onSelected() {
                            randomSuccess.hide();
                            FileDialog openDialog = new FileDialog(mainView.mainShell, SWT.SAVE);
                            openDialog.setFilterExtensions(new String[]{"*.html"});
                            String changelogPath = openDialog.open();
                            if (changelogPath != null) {
                                int index = Math.max(romPath.lastIndexOf('/'), romPath.lastIndexOf('\\'));
                                String title = romPath.substring(index + 1);
                                cb.setDocumentTitle("Changelog for " + title);
                                Boolean success = cb.writeToPath(changelogPath);
                                if (success) {
                                    MessageModal saveSuccess = new MessageModal(mainView.mainShell, "Success", "Changelog saved.");
                                    saveSuccess.show();
                                } else {
                                    MessageModal saveFail = new MessageModal(mainView.mainShell, "Error", "Failed to write changelog.");
                                    saveFail.show();
                                }
                            }
                        }
                    });
                    randomSuccess.addButton("No", new ModalButtonListener() {
                        public void onSelected() {
                            randomSuccess.hide();
                        }
                    });
                } else {
                    randomSuccess = new MessageModal(mainView.mainShell, "Success", "Finished Randomizing!");
                }

                randomSuccess.show();
            }

            @Override
            public void onError(String errorString) {
                mainView.hideModalProgressDialog();
                MessageModal randomFailure = new MessageModal(mainView.mainShell, "Error", "Randomization failed with error: " + errorString);
                randomFailure.show();
            }

            @Override
            public void onProgressUpdate(double progress) {
                mainView.setProgressDialogPercentage((int) (progress * 100));
            }
        });

        randomizer.start();
        mainView.showModalProgressDialog();
    }
}

