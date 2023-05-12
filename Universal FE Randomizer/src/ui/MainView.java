package ui;

import application.Main;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import random.gba.randomizer.GBARandomizer;
import random.general.Randomizer;
import random.general.RandomizerListener;
import ui.common.RomInfoGroup;
import ui.common.RomSelectionGroup;
import ui.common.SeedGroup;
import ui.gba.tabs.*;
import ui.general.FileFlowDelegate;
import ui.general.MessageModal;
import ui.general.ModalButtonListener;
import ui.general.ProgressModal;
import util.DiffCompiler;
import util.OptionRecorder;
import util.OptionRecorder.FE4OptionBundle;
import util.OptionRecorder.FE9OptionBundle;
import util.OptionRecorder.GBAOptionBundle;
import util.SeedGenerator;
import util.recordkeeper.ChangelogBuilder;
import util.recordkeeper.RecordKeeper;

import java.io.IOException;
import java.util.List;
import java.util.*;

public class MainView implements FileFlowDelegate {

    public Shell mainShell;

    private Composite mainContainer;

    private ProgressModal progressBox;
    private boolean isShowingModalProgressDialog = false;
    private boolean patchingAvailable = false;

    public GameType loadedGameType;

    // Widget Groups
    RomSelectionGroup romSelection;
    RomInfoGroup romInfo;
    SeedGroup seedGroup;
    CTabFolder tabFolder;

    Button randomizeButton;

    // Tabs
    List<YuneTabItem> availableTabs = new ArrayList<>();

    // GBA Tabs
    CharactersTab charactersTab;
    EnemiesTab enemiesTab;
    ItemsTab itemsTab;
    MiscTab miscTab;
    StatsTab statsTab;


    public MainView(Display mainDisplay) {
        mainShell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX);
        mainShell.setText("Yune: GUI Rework Test");
        mainShell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
        mainShell.setLayout(new FillLayout());

        mainContainer = new Composite(mainShell, SWT.NONE);
        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.fill = true;
        rowLayout.justify = true;
        mainContainer.setLayout(rowLayout);


        romSelection = new RomSelectionGroup(mainContainer, this);

        resize();
        mainShell.open();
    }

    private void resize() {
        mainContainer.layout();
        mainShell.layout();
        Point containerSize = mainShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        mainShell.setSize(containerSize);
    }

    private void loadGameType(String pathToFile, FileHandler handler) {
        if (loadedGameType == GameType.UNKNOWN) return;
        updateLayoutForGameType();
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
                if (loadedGameType.isGBA()) {
                    openDialog.setFilterExtensions(new String[]{"*.gba"});
                } else if (loadedGameType.isSFC()) {
                    openDialog.setFilterExtensions(new String[]{"*.smc"});
                } else if (loadedGameType.isGCN()) {
                    openDialog.setFilterExtensions(new String[]{"*.iso"});
                }
                String writePath = openDialog.open();

                if (writePath != null && writePath.length() > 0) {
                    if (writePath.equals(pathToFile)) {
                        String extension = writePath.substring(writePath.length() - 4);
                        StringBuilder sb = new StringBuilder(writePath);
                        sb.delete(sb.length() - 4, sb.length());
                        sb.append(" (Randomized)");
                        sb.append(extension);
                        writePath = sb.toString();
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
                        GBAOptionBundle updatedOptions = new GBAOptionBundle();
                        updatedOptions.seed = seedGroup.getSeed();
                        // Make the Tabs update the settings they contain into the bundle
                        availableTabs.stream().forEach(tab -> tab.updateOptionBundle(updatedOptions));
                        // Update the Bundle in the Option Recorder
                        OptionRecorder.recordGBAFEOptions(updatedOptions, loadedGameType);
                    } else if (loadedGameType.isSFC()) {
                        FE4OptionBundle updatedOptions = new FE4OptionBundle();
                        updatedOptions.seed = seedGroup.getSeed();
                        // Make the Tabs update the settings they contain into the bundle
                        availableTabs.stream().forEach(tab -> tab.updateOptionBundle(updatedOptions));
                        // Update the Bundle in the Option Recorder
                        OptionRecorder.recordFE4Options(updatedOptions);
                    } else if (loadedGameType.isGCN()) {
                        FE9OptionBundle updatedOptions = new FE9OptionBundle();
                        updatedOptions.seed = seedGroup.getSeed();
                        // Make the Tabs update the settings they contain into the bundle
                        availableTabs.stream().forEach(tab -> tab.updateOptionBundle(updatedOptions));
                        // Update the Bundle in the Option Recorder
                        OptionRecorder.recordFE9Options(updatedOptions);
                    }
                    GBAOptionBundle gbaBundle = OptionRecorder.getGBABundle(loadedGameType);
                    randomizer = new GBARandomizer(pathToFile, writePath, loadedGameType, compiler,
                            gbaBundle.growths, gbaBundle.bases, gbaBundle.classes, gbaBundle.weapons,
                            gbaBundle.other, gbaBundle.enemies, gbaBundle.otherOptions, gbaBundle.recruitmentOptions,
                            gbaBundle.itemAssignmentOptions, gbaBundle.characterShufflingOptions, gbaBundle.seed);
//
//                        OptionRecorder.recordGBAFEOptions(type,
//                                growthView.getGrowthOptions(),
//                                baseView.getBaseOptions(),
//                                classView.getClassOptions(),
//                                weaponView.getWeaponOptions(),
//                                otherCharOptionView.getOtherCharacterOptions(),
//                                enemyView.getEnemyOptions(),
//                                miscView.getMiscellaneousOptions(),
//                                recruitView.getRecruitmentOptions(),
//                                itemAssignmentView.getAssignmentOptions(),
//                                characterShufflingView.getShufflingOptions(),
//                                seedField.getText());
//                    } else if (type.isSFC()) {
//                        if (type == GameType.FE4) {
//                            boolean headeredROM = handler.getCRC32() == FE4Data.CleanHeaderedCRC32;;
//                            randomizer = new FE4Randomizer(pathToFile, headeredROM, writePath, compiler,
//                                    growthView.getGrowthOptions(),
//                                    baseView.getBaseOptions(),
//                                    holyBloodView.getHolyBloodOptions(),
//                                    skillsView.getSkillOptions(),
//                                    fe4ClassView.getClassOptions(),
//                                    fe4PromotionView.getPromotionOptions(),
//                                    fe4EnemyBuffView.getBuffOptions(),
//                                    miscView.getMiscellaneousOptions(),
//                                    seedField.getText());
//
//                            OptionRecorder.recordFE4Options(growthView.getGrowthOptions(),
//                                    baseView.getBaseOptions(),
//                                    holyBloodView.getHolyBloodOptions(),
//                                    skillsView.getSkillOptions(),
//                                    fe4ClassView.getClassOptions(),
//                                    fe4PromotionView.getPromotionOptions(),
//                                    fe4EnemyBuffView.getBuffOptions(),
//                                    miscView.getMiscellaneousOptions(),
//                                    seedField.getText());
//                        }
//                    } else if (type.isGCN()) {
//                        randomizer = new FE9Randomizer(pathToFile, writePath,
//                                growthView.getGrowthOptions(),
//                                baseView.getBaseOptions(),
//                                fe9SkillView.getSkillOptions(),
//                                conAffinityView.getOtherCharacterOptions(),
//                                fe9EnemyView.getEnemyBuffOptions(),
//                                fe9ClassesView.getClassOptions(),
//                                weaponView.getWeaponOptions(),
//                                miscView.getMiscellaneousOptions(),
//                                seedField.getText());
//
//                        OptionRecorder.recordFE9Options(growthView.getGrowthOptions(),
//                                baseView.getBaseOptions(),
//                                fe9SkillView.getSkillOptions(),
//                                conAffinityView.getOtherCharacterOptions(),
//                                fe9EnemyView.getEnemyBuffOptions(),
//                                fe9ClassesView.getClassOptions(),
//                                weaponView.getWeaponOptions(),
//                                miscView.getMiscellaneousOptions(),
//                                seedField.getText());
//                    }

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
            }
        });

    }

    private void updateLayoutForGameType() {
        // Clear the Previous tabs
        if (tabFolder.getTabList().length != 0) {
            for (CTabItem item : tabFolder.getItems()) {
                item.dispose();
            }
            availableTabs.clear();
        }

        if (loadedGameType.isGBA()) {
            statsTab = addTab(new StatsTab(tabFolder, loadedGameType));
            charactersTab = addTab(new CharactersTab(tabFolder, loadedGameType));
            itemsTab = addTab(new ItemsTab(tabFolder, loadedGameType));
            enemiesTab = addTab(new EnemiesTab(tabFolder, loadedGameType));
            miscTab = addTab(new MiscTab(tabFolder, loadedGameType));
        }

        tabFolder.setSelection(0);
    }


    public <T extends YuneTabItem> T addTab(T tabItem) {
        this.availableTabs.add(tabItem);
        return tabItem;
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
        // Tab Folder disposal should also dispose all children automatically
        if (tabFolder != null) tabFolder.dispose();
    }

    private void preloadOptions() {
        if (OptionRecorder.options == null) {
            return;
        }

        if (loadedGameType == GameType.FE4 && OptionRecorder.options.fe4 != null) {
            this.availableTabs.forEach(tab -> tab.preloadOptions(OptionRecorder.options.fe4));
        } else if (loadedGameType.isGBA()) {
            final GBAOptionBundle bundle = OptionRecorder.getGBABundle(loadedGameType);
            if (bundle != null) {
                this.availableTabs.forEach(tab -> tab.preloadOptions(bundle));
            }
        } else if (loadedGameType == GameType.FE9 && OptionRecorder.options.fe9 != null) {
            this.availableTabs.forEach(tab -> tab.preloadOptions(OptionRecorder.options.fe9));
        }

    }

    @Override
    public void onSelectedFile(String pathToFile) {
        if (pathToFile != null) {
            romSelection.setFilePath(pathToFile);
        } else {
            return;
        }

        MessageModal loadingModal = new MessageModal(mainShell, "Loading", "Verifying File...");
        loadingModal.showRaw();

        if (romInfo == null) {
            romInfo = new RomInfoGroup(mainContainer);
        }
        if (seedGroup == null) {
            seedGroup = new SeedGroup(mainContainer);
        }
        if (tabFolder == null) {
            tabFolder = new CTabFolder(mainContainer, SWT.BORDER);
        }
        if (randomizeButton == null) {
            randomizeButton = new Button(mainContainer, SWT.PUSH);
            randomizeButton.setText("Randomize");
            randomizeButton.setLayoutData(new RowData(SWT.DEFAULT, 50));
        }

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


        if (loadedGameType != GameType.UNKNOWN) {
            loadGameType(pathToFile, handler);
            loadingModal.hide();
        } else {
            loadingModal.hide();
            MessageModal checksumFailure = buildChecksumFailureModal(pathToFile, handler);
            checksumFailure.show();
        }

        resize();

        if (loadedGameType == GameType.FE9 && System.getProperty("sun.arch.data.model").equals("32")) {
            MessageModal memoryWarning = new MessageModal(mainShell, "Warning", "You seem to be running a 32-bit Java VM.\nThere are known out of memory issues with\nrandomizing FE9 when using a 32-bit VM.\n\nThis may be addressed in a future release,\nbut please consider upgrading to a 64-bit JRE.");
            memoryWarning.show();
        }
    }


    private MessageModal buildChecksumFailureModal(String pathToFile, FileHandler handler) {
        MessageModal checksumFailure = new MessageModal(mainShell, "Unrecognized Checksum", "Yune was unable to determine the game from the file selected.\n"
                + "If you know the game for the file, you may select it below.\n\nNote: Patching cannot be guaranteed, and is therefore, disabled.\n\n"
                + "Warning: Be aware that this file is likely untested and may cause errors.\n"
                + "There will be very limited support for issues from randomizing this file.");
        ModalButtonListener fe4Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE4;
                loadGameType(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Genealogy of the Holy War");
            }
        };
        ModalButtonListener fe6Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE6;
                loadGameType(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Binding Blade");
            }
        };
        ModalButtonListener fe7Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE7;
                loadGameType(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Blazing Sword");
            }
        };
        ModalButtonListener fe8Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE8;
                loadGameType(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: The Sacred Stones");
            }
        };
        ModalButtonListener fe9Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE9;
                loadGameType(pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Path of Radiance");
            }
        };
        Map<String, ModalButtonListener> selectionMap = new HashMap<String, ModalButtonListener>();
        selectionMap.put("FE4 (Genealogy of the Holy War)", fe4Selection);
        selectionMap.put("FE6 (Binding Blade)", fe6Selection);
        selectionMap.put("FE7 (Blazing Sword)", fe7Selection);
        selectionMap.put("FE8 (The Sacred Stones)", fe8Selection);
        selectionMap.put("FE9 (Path of Radiance)", fe9Selection);
        checksumFailure.addSelectionItems(selectionMap, Arrays.asList("FE4 (Genealogy of the Holy War)",
                "FE6 (Binding Blade)",
                "FE7 (Blazing Sword)",
                "FE8 (The Sacred Stones)",
                "FE9 (Path of Radiance)"), new ModalButtonListener() {
            @Override
            public void onSelected() {
                // On cancel...
                disposeAll();
            }

        });

        return checksumFailure;
    }
}
