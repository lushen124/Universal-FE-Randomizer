package ui;

import application.Main;
import fedata.general.FEBase.GameType;
import fedata.snes.fe4.FE4Data;
import io.FileHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import random.gba.randomizer.GBARandomizer;
import random.gcnwii.fe9.randomizer.FE9Randomizer;
import random.general.Randomizer;
import random.general.RandomizerListener;
import random.snes.fe4.randomizer.FE4Randomizer;
import ui.common.*;
import ui.general.FileFlowDelegate;
import ui.general.MessageModal;
import ui.general.ModalButtonListener;
import ui.general.ProgressModal;
import util.Bundle;
import util.DiffCompiler;
import util.OptionRecorder;
import util.OptionRecorder.FE4OptionBundle;
import util.OptionRecorder.FE9OptionBundle;
import util.OptionRecorder.GBAOptionBundle;
import util.SeedGenerator;
import util.recordkeeper.ChangelogBuilder;
import util.recordkeeper.RecordKeeper;

import java.io.IOException;
import java.util.*;

public class MainView implements FileFlowDelegate {

    public Shell mainShell;

    private ScrolledComposite scrollable;
    private Composite mainContainer;
    private ControlListener resizeListener;
    private int screenHeight;

    private ProgressModal progressBox;
    private boolean isShowingModalProgressDialog = false;
    private boolean patchingAvailable = false;


    private int currentLayout = 1;
    private GameType loadedGameType;

    // Widget Groups
    RomSelectionGroup romSelection;
    RomInfoGroup romInfo;
    SeedGroup seedGroup;
    YuneViewContainer viewContainer;

    Button randomizeButton;

    /**
     * Constructs the Main Window
     */
    public MainView(Display mainDisplay) {
        mainShell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX);
        mainShell.setText("Yune: GUI Rework");
        mainShell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
        mainShell.setLayout(new FillLayout());

        screenHeight = mainDisplay.getBounds().height;
        for (Monitor monitor : mainDisplay.getMonitors()) {
            screenHeight = Math.max(screenHeight, monitor.getClientArea().height);
        }

        screenHeight -= 20;

        scrollable = new ScrolledComposite(mainShell, SWT.V_SCROLL);
        mainContainer = new Composite(scrollable, SWT.NONE);
        scrollable.setContent(mainContainer);
        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.fill = true;
        rowLayout.justify = true;
        mainContainer.setLayout(rowLayout);


        romSelection = new RomSelectionGroup(mainContainer, this);

        resize();
        mainShell.open();
        mainDisplay.addFilter(SWT.KeyDown, new ConsoleListener());
        mainDisplay.addFilter(SWT.KeyDown, new LayoutSwapListener(this));
    }

    /**
     * Called by the {@link LayoutSwapListener} to re-render the current View container after the user pressed a shortcut to swap the layout:
     *
     * <ul>
     * <li>CTRL + 1 : Single view layout</li>
     * <li>CTRL + 2 : Tabbed layout </li>
     * <li>CTRL + 3 : Scroll layout </li>
     * </ul>
     */
    public void swapLayout(int newLayout) {
        // Do nothing if the user pressed the combination for the current layout again
        if (newLayout == currentLayout) {
            return;
        }

        // Create a new Bundle so we can transfer over the selections to the new Layout
        Bundle bundle = OptionRecorder.createBundle(loadedGameType);
        viewContainer.updateOptionBundle(bundle);

        // User selected a new layout, so dispose of the old one
        viewContainer.dispose();
        // Construct the new View Container depending on the selection
        switch(newLayout) {
            case 1:
                this.viewContainer = new LegacyViewContainer(mainContainer, loadedGameType);
                break;
            case 2:
                this.viewContainer = new TabbedViewContainer(mainContainer, loadedGameType);
                break;
            default:
                throw new UnsupportedOperationException("No layout exists for selection CTRL + " + newLayout);
        }

        // Load the options now that the new viewContainer has been created
        viewContainer.preloadOptions(bundle);
        // update the currentLayout so that if the user clicks the same thing multiple times, we don't swap layout each time
        currentLayout = newLayout;
        resize();
    }

    private void resize() {
        mainShell.layout();
        mainContainer.layout();
        int titleBarHeight = mainShell.getBounds().height - mainShell.getClientArea().height;
        Point containerSize = mainContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        // For some reason, in debug, everything works fine, but when exporting to JAR,
        // the right margin is off (maybe due to different JREs?) The +10 is to make sure the
        // JAR being run is shown correctly.
        Point actualSize = new Point(containerSize.x + 10, Math.min(containerSize.y + titleBarHeight, screenHeight));

        final Point contentSize = actualSize;

        if (actualSize.y - titleBarHeight < containerSize.y) {
            ScrollBar verticalScrollBar = scrollable.getVerticalBar();
            RowLayout containerLayout = (RowLayout)mainContainer.getLayout();
            containerLayout.marginRight = verticalScrollBar.getSize().x + 5;

            mainShell.layout();
            mainContainer.layout();
            containerSize = mainContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            actualSize = new Point(containerSize.x + 10, Math.min(containerSize.y + (mainShell.getBounds().height - mainShell.getClientArea().height), screenHeight));
        }

        // On Ubuntu, 44 is the size of the natural container size, so we'll give some additional margin too.
        if (containerSize.y < 50) {
            mainShell.setMinimumSize(containerSize.x + 10, 0);
        } else {
            mainShell.setMinimumSize(containerSize.x + 10, 300);
        }

        if (resizeListener != null) { mainShell.removeControlListener(resizeListener); }

        resizeListener = new ControlListener() {
            @Override
            public void controlMoved(ControlEvent e) {}
            @Override
            public void controlResized(ControlEvent e) {
                Point size = mainShell.getSize();
                if (contentSize.y < 50) { return; }
                if (size.y >= screenHeight) { return; } // This is to allow Full screen to work on Mac OS.
                if (size.y > contentSize.y || size.x > contentSize.x) {
                    mainShell.setSize(contentSize.x, contentSize.y);
                }
            }
        };

        mainShell.addControlListener(resizeListener);

        mainContainer.setSize(containerSize);
        mainShell.setSize(actualSize);

        FormData scrollableData = new FormData();
        scrollableData.top = new FormAttachment(0, 0);
        scrollableData.left = new FormAttachment(0, 0);
        scrollableData.right = new FormAttachment(100, 0);
        scrollableData.bottom = new FormAttachment(100, 0);
        scrollableData.width = actualSize.x;
        scrollableData.height = actualSize.y;
        scrollable.setLayoutData(scrollableData);
    }

    private void initializeViewContainer(String pathToFile, FileHandler handler) {
        if (loadedGameType == GameType.UNKNOWN) return;
        preloadOptions();
        seedGroup.setSeedFieldText(SeedGenerator.generateRandomSeed(loadedGameType));
        seedGroup.addGenerateButtonListener(randomizeButton, loadedGameType);

        // Remove Randomize Button Listeners
        for (Listener listener : randomizeButton.getListeners(SWT.Selection)) {
            randomizeButton.removeListener(SWT.Selection, listener);
        }

        randomizeButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                FileDialog openDialog = new FileDialog(mainShell, SWT.SAVE);
                openDialog.setFilterExtensions(loadedGameType.getFileExtensions());
                String writePath = openDialog.open();

                if (writePath == null || writePath.length() == 0) {
                    // No target path given (i.e. cancel), so do nothing
                    return;
                }

                // if the user selected the same rom name as the one loaded, we can't just overwrite it, so append a (Randomized) after the name of the file, but before the extension.
                if (writePath.equals(pathToFile)) {
                    String fileName = writePath.substring(0, writePath.length() - 4);
                    String extension = writePath.substring(writePath.length() - 4);
                    writePath = String.format("%s%s%s", fileName, " (Randomized)", extension);
                }
                DiffCompiler compiler = new DiffCompiler();

                if (loadedGameType == GameType.FE7) {
                    try {
                        compiler.addDiffsFromFile("tutorialSlayer");
                    } catch (IOException e) {
                        MessageBox tutorialSlayerFail = new MessageBox(mainShell, SWT.ICON_ERROR | SWT.OK | SWT.CANCEL);
                        tutorialSlayerFail.setText("Error");
                        tutorialSlayerFail.setMessage("Failed to patch the tutorial slayer.\n\nThe randomizer can continue, but it is recommended that Lyn Normal mode not be used.");
                        int selectedButton = tutorialSlayerFail.open();
                        if (selectedButton == SWT.CANCEL) {
                            return;
                        }
                    }
                }

                Randomizer randomizer = null;

                if (loadedGameType.isGBA()) {
                    // Start building the new Option Bundle
                    GBAOptionBundle bundle = new GBAOptionBundle();
                    bundle.seed = seedGroup.getSeed();
                    // Make the View Container update the settings they contain into the bundle
                    viewContainer.updateOptionBundle(bundle);
                    // Update the Bundle in the Option Recorder
                    OptionRecorder.recordGBAFEOptions(bundle, loadedGameType);
                    randomizer = new GBARandomizer(pathToFile, writePath, loadedGameType, compiler, bundle.growths, bundle.bases, bundle.classes, bundle.weapons, bundle.other, bundle.enemies, bundle.otherOptions, bundle.recruitmentOptions, bundle.itemAssignmentOptions, bundle.characterShufflingOptions, bundle.rewards, bundle.seed);
                } else if (loadedGameType.isSFC()) {
                    FE4OptionBundle bundle = new FE4OptionBundle();
                    bundle.seed = seedGroup.getSeed();
                    // Make the View Container update the settings they contain into the bundle
                    viewContainer.updateOptionBundle(bundle);
                    // Update the Bundle in the Option Recorder
                    OptionRecorder.recordFE4Options(bundle);
                    boolean headeredROM = handler.getCRC32() == FE4Data.CleanHeaderedCRC32;;
                    randomizer = new FE4Randomizer(pathToFile, headeredROM, writePath, compiler, bundle.growths, bundle.bases, bundle.holyBlood, bundle.skills, bundle.classes, bundle.promo, bundle.enemyBuff, bundle.mechanics, bundle.rewards, bundle.seed);
                } else if (loadedGameType.isGCN()) {
                    FE9OptionBundle bundle = new FE9OptionBundle();
                    bundle.seed = seedGroup.getSeed();
                    // Make the View Container update the settings they contain into the bundle
                    viewContainer.updateOptionBundle(bundle);
                    // Update the Bundle in the Option Recorder
                    OptionRecorder.recordFE9Options(bundle);
                    randomizer = new FE9Randomizer(pathToFile, writePath, bundle.growths, bundle.bases, bundle.skills, bundle.otherOptions, bundle.enemyBuff, bundle.classes, bundle.weapons, bundle.mechanics, bundle.rewards, bundle.seed);
                }

                final String romPath = writePath;
                randomizer.setListener(new RandomizerListener() {

                    @Override
                    public void onStatusUpdate(String status) {
                        progressBox.statusLabel.setText(status);
                    }

                    @Override
                    public void onComplete(RecordKeeper rk, ChangelogBuilder cb) {
                        hideModalProgressDialog();
                        MessageModal randomSuccess;
                        if (rk != null) {
                            randomSuccess = new MessageModal(mainShell, "Success", "Finished Randomizing!\n\nSave changelog?");
                            randomSuccess.addButton("Yes", new ModalButtonListener() {
                                @Override
                                public void onSelected() {
                                    randomSuccess.hide();
                                    FileDialog openDialog = new FileDialog(mainShell, SWT.SAVE);
                                    openDialog.setFilterExtensions(new String[]{"*.html"});
                                    String writePath = openDialog.open();
                                    if (writePath != null) {
                                        Boolean success = rk.exportRecordsToHTML(writePath);
                                        if (success) {
                                            MessageModal saveSuccess = new MessageModal(mainShell, "Success", "Changelog saved.");
                                            saveSuccess.show();
                                        } else {
                                            MessageModal saveFail = new MessageModal(mainShell, "Error", "Failed to write changelog.");
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
                            randomSuccess = new MessageModal(mainShell, "Success", "Finished Randomizing!\n\nSave changelog?");
                            randomSuccess.addButton("Yes", new ModalButtonListener() {
                                @Override
                                public void onSelected() {
                                    randomSuccess.hide();
                                    FileDialog openDialog = new FileDialog(mainShell, SWT.SAVE);
                                    openDialog.setFilterExtensions(new String[]{"*.html"});
                                    String changelogPath = openDialog.open();
                                    if (changelogPath != null) {
                                        int index = Math.max(romPath.lastIndexOf('/'), romPath.lastIndexOf('\\'));
                                        String title = romPath.substring(index + 1);
                                        cb.setDocumentTitle("Changelog for " + title);
                                        Boolean success = cb.writeToPath(changelogPath);
                                        if (success) {
                                            MessageModal saveSuccess = new MessageModal(mainShell, "Success", "Changelog saved.");
                                            saveSuccess.show();
                                        } else {
                                            MessageModal saveFail = new MessageModal(mainShell, "Error", "Failed to write changelog.");
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
                            randomSuccess = new MessageModal(mainShell, "Success", "Finished Randomizing!");
                        }

                        randomSuccess.show();
                    }

                    @Override
                    public void onError(String errorString) {
                        hideModalProgressDialog();
                        MessageModal randomFailure = new MessageModal(mainShell, "Error", "Randomization failed with error: " + errorString);
                        randomFailure.show();
                    }

                    @Override
                    public void onProgressUpdate(double progress) {
                        progressBox.progressBar.setSelection((int) (progress * 100));
                    }
                });

                randomizer.start();
                showModalProgressDialog();
            }
        });

    }

    private void createViewContainer() {
        if (viewContainer != null) {
            viewContainer.dispose();
        }
//        viewContainer = new TabbedViewContainer(mainContainer, loadedGameType);
        viewContainer = new LegacyViewContainer(mainContainer, loadedGameType);
    }


    public void showModalProgressDialog() {
        if (!isShowingModalProgressDialog) {
            isShowingModalProgressDialog = true;
            progressBox = new ProgressModal(mainShell, "Randomizing...");
            progressBox.progressBar.setMinimum(0);
            progressBox.progressBar.setMaximum(100);
            progressBox.show();
        }
    }

    public void hideModalProgressDialog() {
        if (isShowingModalProgressDialog) {
            isShowingModalProgressDialog = false;
            progressBox.hide();
            progressBox = null;
        }
    }

    private void disposeAll() {
        if (romSelection != null) romSelection.dispose();
        if (romInfo != null) romInfo.dispose();
        if (seedGroup != null) seedGroup.dispose();
    }

    /**
     * Read the saved
     */
    private void preloadOptions() {
        // If no Options exist at all (f.e. first time user), then don't preload
        if (OptionRecorder.options == null) {
            return;
        }

        // Try to find the bundle for the currently selected game
        final Bundle bundle = OptionRecorder.getBundle(loadedGameType);
        // if there are options saved for the game, then we can load them
        if (bundle != null) {
            viewContainer.preloadOptions(bundle);
        }
    }

    @Override
    public void onSelectedFile(String pathToFile) {
        if (pathToFile == null) {
            // No file selected, do nothing
            return;
        }

        // Set the path into the text field
        romSelection.setFilePath(pathToFile);


        MessageModal loadingModal = new MessageModal(mainShell, "Loading", "Verifying File...");
        loadingModal.showRaw();

        // Create the groups if they don't exist yet
        if (romInfo == null) {
            romInfo = new RomInfoGroup(mainContainer);
        }
        if (seedGroup == null) {
            seedGroup = new SeedGroup(mainContainer);
        }

        // Create the File Handler and try parsing the rom Info from it
        RomInfoDto romInfoDto;
        FileHandler handler;
        try {
            handler = new FileHandler(pathToFile);
            romInfoDto = RomInfoDto.forROM(handler);
        } catch (IOException e) {
            System.err.println("Failed to calculate checksum on input file.");
            e.printStackTrace();
            return;
        }

        loadedGameType = romInfoDto.getType();
        patchingAvailable = romInfoDto.isPatchingAvailable();
        romInfo.initialize(romInfoDto);

        if (randomizeButton == null) {
            randomizeButton = new Button(mainContainer, SWT.PUSH);
            randomizeButton.setText("Randomize");
            randomizeButton.setLayoutData(new RowData(SWT.DEFAULT, 50));
        }

        if (loadedGameType != GameType.UNKNOWN) {
            // successfully parsed the Game from the File, start initializing the view Container
            createViewContainer();

            initializeViewContainer(pathToFile, handler);
            loadingModal.hide();
        } else {
            // Parsing game from the file failed, let the user select what kind of rom this is
            loadingModal.hide();
            MessageModal checksumFailure = buildChecksumFailureModal(pathToFile, handler);
            checksumFailure.show();
        }

        //
        resize();

        if (loadedGameType == GameType.FE9 && System.getProperty("sun.arch.data.model").equals("32")) {
            MessageModal memoryWarning = new MessageModal(mainShell, "Warning", "You seem to be running a 32-bit Java VM.\nThere are known out of memory issues with\nrandomizing FE9 when using a 32-bit VM.\n\nThis may be addressed in a future release,\nbut please consider upgrading to a 64-bit JRE.");
            memoryWarning.show();
        }
    }


    private MessageModal buildChecksumFailureModal(String pathToFile, FileHandler handler) {
        MessageModal checksumFailure = new MessageModal(mainShell, "Unrecognized Checksum", "Yune was unable to determine the game from the file selected.\n" + "If you know the game for the file, you may select it below.\n\nNote: Patching cannot be guaranteed, and is therefore, disabled.\n\n" + "Warning: Be aware that this file is likely untested and may cause errors.\n" + "There will be very limited support for issues from randomizing this file.");
        ModalButtonListener fe4Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE4;
                initializeViewContainer(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Genealogy of the Holy War");
            }
        };
        ModalButtonListener fe6Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE6;
                initializeViewContainer(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Binding Blade");
            }
        };
        ModalButtonListener fe7Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE7;
                initializeViewContainer(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Blazing Sword");
            }
        };
        ModalButtonListener fe8Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE8;
                initializeViewContainer(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: The Sacred Stones");
            }
        };
        ModalButtonListener fe9Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE9;
                initializeViewContainer(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Path of Radiance");
            }
        };
        Map<String, ModalButtonListener> selectionMap = new HashMap<String, ModalButtonListener>();
        selectionMap.put("FE4 (Genealogy of the Holy War)", fe4Selection);
        selectionMap.put("FE6 (Binding Blade)", fe6Selection);
        selectionMap.put("FE7 (Blazing Sword)", fe7Selection);
        selectionMap.put("FE8 (The Sacred Stones)", fe8Selection);
        selectionMap.put("FE9 (Path of Radiance)", fe9Selection);
        checksumFailure.addSelectionItems(selectionMap, Arrays.asList("FE4 (Genealogy of the Holy War)", "FE6 (Binding Blade)", "FE7 (Blazing Sword)", "FE8 (The Sacred Stones)", "FE9 (Path of Radiance)"), new ModalButtonListener() {
            @Override
            public void onSelected() {
                // On cancel...
                disposeAll();
            }

        });

        return checksumFailure;
    }
}
