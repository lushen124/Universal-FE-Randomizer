package ui.tabs.fe4;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.views.fe4.FE4EnemyBuffView;
import ui.views.fe4.HolyBloodView;
import ui.views.BasesView;
import ui.views.GrowthsView;
import util.OptionRecorder;

/**
 * The Stats Tab for FE4.
 *
 * This contains the views:
 * <ul>
 *     <li>Bases</li>
 *     <li>Growths</li>
 *     <li>Holy Blood</li>
 *     <li>Enemy Buffs</li>
 * </ul>
 */
public class FE4StatsTab extends YuneTabItem {

    private GrowthsView growths;
    private BasesView bases;
    private HolyBloodView holyBlood;
    private FE4EnemyBuffView enemies;
    public FE4StatsTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        growths = addView(new GrowthsView(container, type.hasSTRMAGSplit(), false));
        holyBlood = addView(new HolyBloodView(container));
        setViewData(holyBlood, 1, 2);
        enemies = addView(new FE4EnemyBuffView(container));
        bases = addView(new BasesView(container, type));
    }

    @Override
    protected String getTabName(){
        return "Stats";
    }

    @Override
    protected String getTabTooltip() {
        return "This Tab contains all Setting which are related to the Stats of the Characters. Such as Bases, Growths, Holy Blood.";
    }

    @Override
    protected int numberColumns() {
        return 3;
    }

    @Override
    public void preloadOptions(OptionRecorder.FE4OptionBundle bundle) {
        growths.initialize(bundle.growths);
        bases.initialize(bundle.bases);
        holyBlood.initialize(bundle.holyBlood);
        enemies.initialize(bundle.enemyBuff);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.growths = growths.getOptions();
        bundle.bases = bases.getOptions();
        bundle.holyBlood = holyBlood.getOptions();
        bundle.enemyBuff = enemies.getOptions();
    }
}
