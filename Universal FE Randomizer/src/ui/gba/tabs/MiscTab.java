package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.MiscellaneousView;
import util.OptionRecorder;

public class MiscTab extends YuneTabItem {

    private MiscellaneousView misc;

    public MiscTab(CTabFolder parent, FEBase.GameType type) {
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
}
