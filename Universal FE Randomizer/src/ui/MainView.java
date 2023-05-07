package ui;

import application.Main;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import ui.common.RomInfoGroup;
import ui.common.RomSelectionGroup;
import ui.common.SeedGroup;
import ui.gba.tabs.*;
import ui.general.FileFlowDelegate;

public class MainView implements FileFlowDelegate {

    public Shell mainShell;

    private GameType type = GameType.FE8;

    private int windowHeight;

    public MainView(Display mainDisplay) {
        Shell shell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX);
        shell.setText("Yune: GUI Rework Test");
        shell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
        shell.setLayout(new FillLayout());

        Composite mainContainer = new Composite(shell, SWT.NONE);
        mainContainer.setLayout(new FormLayout());

        RomSelectionGroup romSelection = new RomSelectionGroup(mainContainer, this);
        FormData data = new FormData();
        data.left = new FormAttachment(0,0);
        data.top = new FormAttachment(0,0);
        data.right = new FormAttachment(100, 0);
        romSelection.group.setLayoutData(data);

        RomInfoGroup romInfo = new RomInfoGroup(mainContainer);
        romInfo.setRomName("ROM Name: FIREEMBLEM2E");
        romInfo.setRomCode("ROM Code: BE8E");
        romInfo.setFriendlyName("Display Name: Fire Emblem: The Sacred Stone");
        romInfo.setLength("File Length: 16777216");
        romInfo.setChecksum("CRC-32:A472546AE");

        data = new FormData();
        data.left = new FormAttachment(0,0);
        data.top = new FormAttachment(romSelection.group,5);
        data.right = new FormAttachment(100, 0);
        romInfo.group.setLayoutData(data);

        SeedGroup seed = new SeedGroup(mainContainer);
        data = new FormData();
        data.left = new FormAttachment(0,0);
        data.top = new FormAttachment(romInfo.group,1);
        data.right = new FormAttachment(100, 0);
        seed.group.setLayoutData(data);

//        romInfo.setVisible(false);
//        romInfo.setVisible(false);

        CTabFolder tabFolder = new CTabFolder(mainContainer, SWT.BORDER);
//        tabFolder.setSize(600, 800);
        if (type.isGBA()){
            StatsTab statsTab = new StatsTab(tabFolder, type);
            System.out.println(statsTab.getFont().getFontData()[0].getHeight());
            new CharactersTab(tabFolder, type);
            new ItemsTab(tabFolder, type);
            new EnemiesTab(tabFolder, type);
            new MiscTab(tabFolder, type);
        }

        tabFolder.setSelection(0);
        data = new FormData();
        data.left = new FormAttachment(0,0);
        data.top = new FormAttachment(seed.group,5);
        data.right = new FormAttachment(100, 0);
        tabFolder.setLayoutData(data);

        windowHeight = (int) Math.round(mainDisplay.getBounds().height * 0.7);
        for (Monitor monitor : mainDisplay.getMonitors()) {
            windowHeight = Math.max(windowHeight, (int) Math.round(monitor.getClientArea().height * 0.7));
        }
        mainShell = shell;
        mainContainer.layout();
        mainContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        mainShell.layout();
        mainShell.setSize(1000, windowHeight);
        mainShell.open();
    }

    @Override
    public void onSelectedFile(String pathToFile) {
    }
//    public MainView(Display mainDisplay) {
//        super();
//
//        FormData data = new FormData();
//
//        Shell shell = new Shell(mainDisplay, SWT.SHELL_TRIM & ~SWT.MAX);
//        shell.setText("Yune: GUI Rework Test");
//        shell.setImage(new Image(mainDisplay, Main.class.getClassLoader().getResourceAsStream("YuneIcon.png")));
//        shell.setLayout(new FormLayout());
//        Composite topContainer = new Composite(shell, SWT.NONE);
//        topContainer.setSize(400, 100);
//        data.top = new FormAttachment(0, 0);
//        data.left = new FormAttachment(0, 0);
//        data.right = new FormAttachment(100, 0);
//        topContainer.setLayoutData(data);
//
//        CTabFolder tabFolder = new CTabFolder(shell, SWT.BORDER);
//        tabFolder.setSize(400, 300);
//        FormData data2 = new FormData();
//        data2.top = new FormAttachment(topContainer, 10);
//        data2.left = new FormAttachment(topContainer, 0, SWT.LEFT);
//        data2.right = new FormAttachment(topContainer,0, SWT.RIGHT);
//        tabFolder.setLayoutData(data2);
//
//        CTabItem item1 = new CTabItem(tabFolder, SWT.BORDER);
//        item1.setText("Misc");
//        item1.setControl(new MiscellaneousView(tabFolder, SWT.NONE, GameType.FE8));
//
//        CTabItem item2 = new CTabItem(tabFolder, SWT.BORDER);
//        item2.setText("Growths");
//        item2.setControl(new GrowthsView(tabFolder, SWT.NONE, false));
//
//
//        screenHeight = (int) Math.round(mainDisplay.getBounds().height * 0.7);
//        for (Monitor monitor : mainDisplay.getMonitors()) {
//            screenHeight = Math.max(screenHeight, (int) Math.round(monitor.getClientArea().height *0.7));
//        }
//        mainShell = shell;
//        mainShell.layout();
//        mainShell.setSize(500, 500);
//        mainShell.open();
//    }
}
