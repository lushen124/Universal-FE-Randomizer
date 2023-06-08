package ui.tabs.gba;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.views.BasesView;
import ui.views.EnemyBuffsView;
import ui.views.GrowthsView;
import ui.views.MOVCONAffinityView;
import util.OptionRecorder;

public class GBAStatsTab extends YuneTabItem {

    private GrowthsView growths;
    private BasesView bases;
    private MOVCONAffinityView movConAffinity;
    private EnemyBuffsView enemies;

    public GBAStatsTab(CTabFolder parent, GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        growths = addView(new GrowthsView(container, type.hasSTRMAGSplit()));
        enemies = addView(new EnemyBuffsView(container));
        setViewData(enemies, 1, 2);
        movConAffinity = addView(new MOVCONAffinityView(container));
        setViewData(movConAffinity, 1, 2);
        bases = addView(new BasesView(container, type));
    }

    @Override
    protected String getTabName(){
        return "Stats";
    }

    @Override
    protected String getTabTooltip() {
        return "This Tab contains all Setting which are related to the Stats of the Characters. Such as Bases, Growths, Mov, Con, Affinity, etc.";
    }

    @Override
    protected int numberColumns() {
        return 3;
    }

    @Override
    public void preloadOptions(OptionRecorder.GBAOptionBundle bundle) {
        growths.initialize(bundle.growths);
        bases.initialize(bundle.bases);
        movConAffinity.initialize(bundle.other);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.GBAOptionBundle bundle) {
        bundle.growths = growths.getOptions();
        bundle.bases = bases.getOptions();
        bundle.other = movConAffinity.getOptions();
    }
}
