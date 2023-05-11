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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import ui.common.RomInfoGroup;
import ui.common.RomSelectionGroup;
import ui.common.SeedGroup;
import ui.gba.tabs.*;
import ui.general.FileFlowDelegate;
import ui.general.MessageModal;
import ui.general.ModalButtonListener;
import ui.general.ProgressModal;
import util.OptionRecorder;
import util.OptionRecorder.GBAOptionBundle;

import java.io.IOException;
import java.util.*;

public class MainView implements FileFlowDelegate {

    public Shell mainShell;

    private Composite mainContainer;

    private GameType type = GameType.FE8;
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

    private void loadGameType(GameType type, String pathToFile, FileHandler handler) {
        loadedGameType = type;
        if (type == GameType.UNKNOWN) return;

        seedGroup = new SeedGroup(mainContainer);
        tabFolder = new CTabFolder(mainContainer, SWT.BORDER);

        randomizeButton = new Button(mainContainer, SWT.PUSH);
        randomizeButton.setText("Randomize");
        randomizeButton.setLayoutData(new RowData(SWT.DEFAULT, 50));

        // Clear the Previous tabs
        if (tabFolder.getTabList().length != 0) {
            for (CTabItem item : tabFolder.getItems()) {
                item.dispose();
            }
            availableTabs.clear();
        }

        if (type.isGBA()) {
            statsTab = addTab(new StatsTab(tabFolder, type));
            charactersTab = addTab(new CharactersTab(tabFolder, type));
            itemsTab = addTab(new ItemsTab(tabFolder, type));
            enemiesTab = addTab(new EnemiesTab(tabFolder, type));
            miscTab = addTab(new MiscTab(tabFolder, type));
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

    private void preloadOptions(GameType type) {
        if (OptionRecorder.options == null) {
            return;
        }

        if (type == GameType.FE4 && OptionRecorder.options.fe4 != null) {
            this.availableTabs.forEach(tab -> tab.preloadOptions(OptionRecorder.options.fe4));
        } else if (type.isGBA()) {
            final GBAOptionBundle bundle = OptionRecorder.getGBABundle(type);
            this.availableTabs.forEach(tab -> tab.preloadOptions(bundle));
        } else if (type == GameType.FE9 && OptionRecorder.options.fe9 != null) {
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
        romInfo = new RomInfoGroup(mainContainer);
        RomInfoDto romInfoDto = null;

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


        if (type != GameType.UNKNOWN) {
            loadGameType(type, pathToFile, handler);
            loadingModal.hide();
        } else {
            loadingModal.hide();
            MessageModal checksumFailure = buildChecksumFailureModal(pathToFile, handler);
            checksumFailure.show();
        }

        romInfo.setVisible(true);
        seedGroup.setVisible(true);
        tabFolder.setVisible(true);
        randomizeButton.setVisible(true);
        resize();

        if (loadedGameType == GameType.FE9 && System.getProperty("sun.arch.data.model").equals("32")) {
            MessageModal memoryWarning = new MessageModal(mainShell, "Warning", "You seem to be running a 32-bit Java VM.\nThere are known out of memory issues with\nrandomizing FE9 when using a 32-bit VM.\n\nThis may be addressed in a future release,\nbut please consider upgrading to a 64-bit JRE.");
            memoryWarning.show();
        }
    }


    private MessageModal buildChecksumFailureModal(String pathToFile, FileHandler handler){
        MessageModal checksumFailure = new MessageModal(mainShell, "Unrecognized Checksum", "Yune was unable to determine the game from the file selected.\n"
                + "If you know the game for the file, you may select it below.\n\nNote: Patching cannot be guaranteed, and is therefore, disabled.\n\n"
                + "Warning: Be aware that this file is likely untested and may cause errors.\n"
                + "There will be very limited support for issues from randomizing this file.");
        ModalButtonListener fe4Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadGameType(GameType.FE4, pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Genealogy of the Holy War");
            }
        };
        ModalButtonListener fe6Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadGameType(GameType.FE6, pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Binding Blade");
            }
        };
        ModalButtonListener fe7Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadGameType(GameType.FE7, pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Blazing Sword");
            }
        };
        ModalButtonListener fe8Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadGameType(GameType.FE8, pathToFile, handler);
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: The Sacred Stones");
            }
        };
        ModalButtonListener fe9Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadGameType(GameType.FE9, pathToFile, handler);
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
