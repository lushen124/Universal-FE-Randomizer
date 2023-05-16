package ui.fe4.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.fe4.FE4EnemyBuffView;
import util.OptionRecorder;

public class FE4EnemiesTab extends YuneTabItem {

    FE4EnemyBuffView enemies;
    public FE4EnemiesTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        enemies = addView(new FE4EnemyBuffView(container, SWT.NONE));
    }

    @Override
    protected String getTabName(){
        return "Enemies";
    }

    @Override
    protected String getTabTooltip() {
        return "This Tab contains all Setting which are related to enemies.";
    }

    @Override
    protected int numberColumns() {
        return 1;
    }

    @Override
    public void preloadOptions(OptionRecorder.FE4OptionBundle bundle) {
        enemies.setBuffOptions(bundle.enemyBuff);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.enemyBuff = enemies.getBuffOptions();
    }
}
