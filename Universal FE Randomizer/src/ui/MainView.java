package ui;

import application.Main;
import fedata.general.FEBase.GameType;
import io.FileHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import ui.common.*;
import ui.general.*;
import util.Bundle;
import util.OptionRecorder;
import util.SeedGenerator;

import java.io.IOException;
import java.util.*;

public class MainView implements FileFlowDelegate {

    public static final int CLASSIC = 1;
    public static final int TABBED = 2;
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
    // These are public so that the listeners can access them for information
    public RomSelectionGroup romSelection;
    public RomInfoGroup romInfo;
    public SeedGroup seedGroup;
    public YuneViewContainer viewContainer;

    private Button randomizeButton;

    private RandomizeButtonListener randomizeListener;

    /**
     * Constructs the Main Window
     */
    public MainView(Display mainDisplay) {
        mainShell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX);
        mainShell.setText(String.format("Yune: A Universal Fire Emblem Randomizer (%s)", Main.versionId));
        mainShell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
        mainShell.setLayout(new FillLayout());

        screenHeight = mainDisplay.getBounds().height;
        for (Monitor monitor : mainDisplay.getMonitors()) {
            screenHeight = Math.max(screenHeight, monitor.getClientArea().height);
        }

        screenHeight -= 20;

        // Wrap the whole dialog in a Vertical Scrollable just in case
        scrollable = new ScrolledComposite(mainShell, SWT.V_SCROLL);
        mainContainer = new Composite(scrollable, SWT.NONE);
        scrollable.setContent(mainContainer);

        // Create a Row Layout for the main container
        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.fill = true;
        rowLayout.marginRight = 20;
        mainContainer.setLayout(rowLayout);

        // Create the Rom Selection Group, so the user can select the initial rom
        romSelection = new RomSelectionGroup(mainContainer, this);
        initializeMenu();

        // Start display
        resize();
        mainShell.open();

        // Add Listener for Shortcut CTRL + SHIFT + C to open the console
        mainDisplay.addFilter(SWT.KeyDown, new ConsoleListener());
        // Add listener for Shortcut CTRL + 1 or CTRL + 2 to swap between layouts
        mainDisplay.addFilter(SWT.KeyDown, new LayoutSwapListener(this));
    }

    /**
     * Called by the {@link LayoutSwapListener} to re-render the current View container after the user pressed a shortcut to swap the layout:
     *
     * <ul>
     * <li>CTRL + 1 : Single view layout</li>
     * <li>CTRL + 2 : Tabbed layout </li>
     * </ul>
     */
    public void swapLayout(int newLayout) {
        // If the user changed the layout before choosing a rom, just save the preference without changing the view container.
        if (loadedGameType == null) {
            currentLayout = newLayout;
            OptionRecorder.setLayoutPreference(newLayout);
            return;
        }

        // Do nothing if the user pressed the combination for the current layout again, or there wasn't a layout initialized yet
        if (newLayout == currentLayout || viewContainer == null) {
            return;
        }


        // Create a new Bundle so we can transfer over the selections to the new Layout
        Bundle bundle = OptionRecorder.createBundle(loadedGameType);
        viewContainer.updateOptionBundle(bundle);

        // User selected a new layout, so dispose of the old one
        viewContainer.dispose();

        createViewContainer(newLayout);

        // Load the options now that the new viewContainer has been created
        viewContainer.preloadOptions(bundle);
        OptionRecorder.setLayoutPreference(newLayout);
        resize();
    }


    private void resize() {
        mainShell.layout();
        mainContainer.layout();
        int titleBarHeight = mainShell.getBounds().height - mainShell.getClientArea().height;
        Point containerSize = mainContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        int menuBarHeight = mainShell.getMenuBar() == null ? 0 : 20;

        // For some reason, in debug, everything works fine, but when exporting to JAR,
        // the right margin is off (maybe due to different JREs?) The +10 is to make sure the
        // JAR being run is shown correctly.
        Point actualSize = new Point(containerSize.x + 10, Math.min(containerSize.y + titleBarHeight + menuBarHeight, screenHeight));

        final Point contentSize = actualSize;

        if (actualSize.y - titleBarHeight < containerSize.y) {
            ScrollBar verticalScrollBar = scrollable.getVerticalBar();
            RowLayout containerLayout = (RowLayout)mainContainer.getLayout();
            containerLayout.marginRight = verticalScrollBar.getSize().x + 10;

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

    private void onGameChosen() {
        // (1) Create the View Container for the users prefered layout
        createViewContainer(OptionRecorder.getLayoutPreference());
        // (2) preload the options from configuration if there are any.
        preloadOptions();
        // (3) Initialize the SeedField to a random quote from the current game
        seedGroup.setSeedFieldText(SeedGenerator.generateRandomSeed(loadedGameType));
        // (4) add Listener to the Seed Generation button
        seedGroup.addGenerateButtonListener(loadedGameType);

        // (5) Add or update the randomize button listener
        if (randomizeListener == null) {
            randomizeListener = new RandomizeButtonListener(this, loadedGameType);
            randomizeButton.addListener(SWT.Selection, randomizeListener);
        } else {
            randomizeListener.updateGameType(loadedGameType);
        }
    }

    private void initializeMenu() {
        Menu menuBar = mainShell.getDisplay().getMenuBar();
        if (menuBar == null) {
            menuBar = new Menu(mainShell, SWT.BAR);
            mainShell.setMenuBar(menuBar);
        }

        MenuItem file = new MenuItem(menuBar, SWT.CASCADE);
        file.setText("Menu");
        Menu dropdown = new Menu(menuBar);
        file.setMenu(dropdown);

        // Layout Selection
        MenuItem classic = new MenuItem(dropdown, SWT.RADIO);
        classic.setText("Classic Layout");
        classic.setSelection(OptionRecorder.getLayoutPreference() == CLASSIC);
        classic.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> swapLayout(CLASSIC)));
        MenuItem tabbed = new MenuItem(dropdown, SWT.RADIO);
        tabbed.setText("Tabbed Layout");
        tabbed.setSelection(OptionRecorder.getLayoutPreference() == TABBED);
        tabbed.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> swapLayout(TABBED)));

        // About
        MenuItem about = new MenuItem(dropdown, SWT.PUSH);
        about.setText("About...");
        about.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> new AboutDialog(mainShell).show()));

    }

    private void createViewContainer(int newLayout) {
        if (viewContainer != null) {
            viewContainer.dispose();
        }

        // Construct the new View Container depending on the selection
        switch(newLayout) {
            case CLASSIC:
                this.viewContainer = new LegacyViewContainer(mainContainer, loadedGameType);
                break;
            case TABBED:
                this.viewContainer = new TabbedViewContainer(mainContainer, loadedGameType);
                break;
            default:
                throw new UnsupportedOperationException("No layout exists for number: " + newLayout);
        }

        // update the currentLayout so that if the user clicks the same thing multiple times, we don't swap layout each time
        currentLayout = newLayout;
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

    public void setProgressDialogText(String status) {
        progressBox.statusLabel.setText(status);
    }

    public void setProgressDialogPercentage(Integer value) {
        progressBox.progressBar.setSelection(value);
    }

    private void disposeAll() {
        if (romSelection != null) romSelection.dispose();
        if (romInfo != null) romInfo.dispose();
        if (seedGroup != null) seedGroup.dispose();
        if (viewContainer != null) viewContainer.dispose();
        if (randomizeButton != null) randomizeButton.dispose();
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
            // successfully parsed the Game from the File, start initializing the Window
            onGameChosen();
            loadingModal.hide();
        } else {
            // Parsing game from the file failed, let the user select what kind of rom this is
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
        MessageModal checksumFailure = new MessageModal(mainShell, "Unrecognized Checksum", "Yune was unable to determine the game from the file selected.\n" + "If you know the game for the file, you may select it below.\n\nNote: Patching cannot be guaranteed, and is therefore, disabled.\n\n" + "Warning: Be aware that this file is likely untested and may cause errors.\n" + "There will be very limited support for issues from randomizing this file.");
        ModalButtonListener fe4Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE4;
                onGameChosen();
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Genealogy of the Holy War");
            }
        };
        ModalButtonListener fe6Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE6;
                onGameChosen();
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Binding Blade");
            }
        };
        ModalButtonListener fe7Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE7;
                onGameChosen();
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: Blazing Sword");
            }
        };
        ModalButtonListener fe8Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE8;
                onGameChosen();
                romInfo.setFriendlyName("Display Name: (Unverified) Fire Emblem: The Sacred Stones");
            }
        };
        ModalButtonListener fe9Selection = new ModalButtonListener() {
            @Override
            public void onSelected() {
                loadedGameType = GameType.FE9;
                onGameChosen();
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
