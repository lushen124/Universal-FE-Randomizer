package ui.tabs.gba;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.views.GameMechanicsView;
import util.OptionRecorder;

/**
 * Tab containing the GUI of the GBA (and FE4) Misc Tab
 */
public class GBAMechanicsTab extends YuneTabItem {

    private GameMechanicsView misc;

    public GBAMechanicsTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        misc = addView(new GameMechanicsView(container, type));
    }

    @Override
    protected String getTabName() {
        return "Misc.";
    }

    @Override
    protected String getTabTooltip() {
        return "Tab for changes related to Game mechanics, such as Fog of War, 1RN mode, Casual Mode etc.";
    }

    @Override
    protected int numberColumns() {
        return 1;
    }

    @Override
    public void preloadOptions(OptionRecorder.GBAOptionBundle bundle) {
        misc.initialize(bundle.otherOptions);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.GBAOptionBundle bundle) {
        bundle.otherOptions = misc.getOptions();
    }
}
