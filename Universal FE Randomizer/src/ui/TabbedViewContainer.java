package ui;

import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import ui.common.Preloadable;
import ui.common.YuneTabItem;
import ui.tabs.fe4.FE4ClassesTab;
import ui.tabs.fe4.FE4SkillsTab;
import ui.tabs.fe4.FE4StatsTab;
import ui.tabs.fe9.FE9CharactersTab;
import ui.tabs.fe9.FE9ItemsTab;
import ui.tabs.fe9.FE9SkillsTab;
import ui.tabs.gba.GBACharactersTab;
import ui.tabs.gba.GBAItemsTab;
import ui.tabs.gba.GBAMechanicsTab;
import ui.tabs.gba.GBAStatsTab;
import util.Bundle;
import util.OptionRecorder;

import java.util.ArrayList;
import java.util.List;

public class TabbedViewContainer extends YuneViewContainer {

    CTabFolder tabFolder;

    // Tabs
    List<YuneTabItem> availableTabs = new ArrayList<>();

    // GBA Tabs
    YuneTabItem charactersTab;
    YuneTabItem enemiesTab;
    YuneTabItem itemsTab;
    YuneTabItem miscTab;
    YuneTabItem statsTab;
    YuneTabItem classesTab;
    YuneTabItem skillsTab;



    public TabbedViewContainer(Composite parent, GameType loadedType) {
        super(parent, loadedType);
        this.setLayout(new FillLayout());
        tabFolder = new CTabFolder(this, SWT.NONE);
        // Clear the Tabs from potentially loaded previous games
        if (tabFolder.getTabList().length != 0) {
            for (CTabItem item : tabFolder.getItems()) {
                item.dispose();
            }
            availableTabs.clear();
        }

        // Initialize the Tab Folder with the tabs based on the game type
        if (type.isGBA()) {
            statsTab = addTab(new GBAStatsTab(tabFolder, type));
            charactersTab = addTab(new GBACharactersTab(tabFolder, type));
            itemsTab = addTab(new GBAItemsTab(tabFolder, type));
            miscTab = addTab(new GBAMechanicsTab(tabFolder, type));
        } else if (type.isSFC()) {
            statsTab = addTab(new FE4StatsTab(tabFolder));
            classesTab = addTab(new FE4ClassesTab(tabFolder));
            skillsTab = addTab(new FE4SkillsTab(tabFolder));
            miscTab = addTab(new GBAMechanicsTab(tabFolder, type));
        } else if (type.isGCN()) {
            charactersTab = addTab(new FE9CharactersTab(tabFolder));
            itemsTab = addTab(new FE9ItemsTab(tabFolder));
            skillsTab = addTab(new FE9SkillsTab(tabFolder));
        }

        tabFolder.setSelection(0);
    }

    public <T extends YuneTabItem> T addTab(T tabItem) {
        this.availableTabs.add(tabItem);
        return tabItem;
    }

    @Override
    public void preloadOptions(Bundle bundle) {
        this.availableTabs.forEach(tab -> tab.preloadOptions(bundle));
    }

    @Override
    public void updateOptionBundle(Bundle bundle) {
        this.availableTabs.forEach(tab -> tab.updateOptionBundle(bundle));
    }
}
