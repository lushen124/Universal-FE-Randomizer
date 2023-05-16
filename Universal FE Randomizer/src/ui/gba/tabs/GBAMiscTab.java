package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.legacy.MiscellaneousView;
import util.OptionRecorder;

/**
 * Tab containing the GUI of the GBA (and FE4) Misc Tab
 */
public class GBAMiscTab extends YuneTabItem {

    private MiscellaneousView misc;

    public GBAMiscTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        misc = addView(new MiscellaneousView(container, SWT.NONE, type));
    }

    @Override
    protected String getTabName() {
        return "Misc.";
    }

    @Override
    protected String getTabTooltip() {
        return "Tab for Miscellaneous settings";
    }

    @Override
    protected int numberColumns() {
        return 1;
    }

    @Override
    public void preloadOptions(OptionRecorder.GBAOptionBundle bundle) {
        misc.setMiscellaneousOptions(bundle.otherOptions);
    }
    @Override
    public void preloadOptions(OptionRecorder.FE4OptionBundle bundle) {
        misc.setMiscellaneousOptions(bundle.misc);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.GBAOptionBundle bundle) {
        bundle.otherOptions = misc.getMiscellaneousOptions();
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.misc = misc.getMiscellaneousOptions();
    }
}
