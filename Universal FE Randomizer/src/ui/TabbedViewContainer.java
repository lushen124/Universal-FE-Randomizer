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
import ui.tabs.fe4.FE4MiscTab;
import ui.tabs.fe4.FE4SkillsTab;
import ui.tabs.fe4.FE4StatsTab;
import ui.tabs.fe9.FE9CharactersTab;
import ui.tabs.fe9.FE9ItemsTab;
import ui.tabs.fe9.FE9MiscTab;
import ui.tabs.fe9.FE9SkillsTab;
import ui.tabs.gba.GBACharactersTab;
import ui.tabs.gba.GBAItemsTab;
import ui.tabs.gba.GBAMechanicsTab;
import ui.tabs.gba.GBAStatsTab;
import util.Bundle;
import util.OptionRecorder;

import java.util.ArrayList;
import java.util.List;

/**
 * View container which presents the Options in a tab based layout using {@link CTabFolder} and {@link CTabItem}.
 *
 * See also {@link YuneTabItem}.
 */
public class TabbedViewContainer extends YuneViewContainer {

    CTabFolder tabFolder;

    // Tabs
    List<YuneTabItem> availableTabs;

    public TabbedViewContainer(Composite parent, GameType loadedType) {
        super(parent, loadedType);
    }

    @Override
    protected void compose() {
        this.setLayout(new FillLayout());
        tabFolder = new CTabFolder(this, SWT.NONE);
        availableTabs = new ArrayList<>();

        // Initialize the Tab Folder with the tabs based on the game type
        if (type.isGBA()) {
            addTab(new GBAStatsTab(tabFolder, type));
            addTab(new GBACharactersTab(tabFolder, type));
            addTab(new GBAItemsTab(tabFolder, type));
            addTab(new GBAMechanicsTab(tabFolder, type));
        } else if (type.isSFC()) {
            addTab(new FE4StatsTab(tabFolder));
            addTab(new FE4ClassesTab(tabFolder));
            addTab(new FE4SkillsTab(tabFolder));
            addTab(new FE4MiscTab(tabFolder));
        } else if (type.isGCN()) {
            addTab(new FE9CharactersTab(tabFolder));
            addTab(new FE9ItemsTab(tabFolder));
            addTab(new FE9SkillsTab(tabFolder));
            addTab(new FE9MiscTab(tabFolder));
        }

        tabFolder.setSelection(0);
    }

    /**
     * Adds the given {@link YuneTabItem} to the list of available Tabs.
     * Usually should be called directly with the output of a YuneTabItem constructor.
     *
     * @return Returns the parameter tabItem, so that you can assign it to a variable in addition to adding it to the list (if needed).
     */
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
