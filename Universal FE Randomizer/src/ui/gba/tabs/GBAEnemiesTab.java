package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.legacy.EnemyBuffsView;
import util.OptionRecorder;

public class GBAEnemiesTab extends YuneTabItem {

    EnemyBuffsView enemies;

    public GBAEnemiesTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        enemies = addView(new EnemyBuffsView(container, SWT.NONE));
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

    @Override
    public void preloadOptions(OptionRecorder.GBAOptionBundle bundle) {
        enemies.setEnemyOptions(bundle.enemies);
    }
    @Override
    public void updateOptionBundle(OptionRecorder.GBAOptionBundle bundle) {
        bundle.enemies = enemies.getEnemyOptions();
    }
}
