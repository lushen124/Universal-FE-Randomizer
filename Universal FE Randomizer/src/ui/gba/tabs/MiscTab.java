package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.MiscellaneousView;

public class MiscTab extends YuneTabItem {
    public MiscTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        addView(new MiscellaneousView(container, SWT.NONE, type));
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
}
