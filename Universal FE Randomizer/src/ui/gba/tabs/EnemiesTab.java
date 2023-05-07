package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.EnemyBuffsView;

public class EnemiesTab extends YuneTabItem {
    public EnemiesTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        addView(new EnemyBuffsView(container, SWT.NONE));
    }

    @Override
    protected String getTabName() {
        return "Enemies";
    }

    @Override
    protected String getTabTooltip() {
        return "All Options related to Enemy minions / bosses. Such as Buffing them.";
    }

    @Override
    protected int numberColumns() {
        return 2;
    }
}
