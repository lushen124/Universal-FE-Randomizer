package ui.tabs.fe9;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.views.fe9.FE9EnemyBuffView;
import util.OptionRecorder;

import static fedata.general.FEBase.GameType.FE9;

public class FE9EnemiesTab extends YuneTabItem {

    FE9EnemyBuffView enemies;

    public FE9EnemiesTab(CTabFolder parent) {
        super(parent, FE9);
    }

    @Override
    protected void compose() {
        enemies = addView(new FE9EnemyBuffView(container));
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
    public void preloadOptions(OptionRecorder.FE9OptionBundle bundle) {
        enemies.initialize(bundle.enemyBuff);
    }
    @Override
    public void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        bundle.enemyBuff = enemies.getOptions();
    }

}
