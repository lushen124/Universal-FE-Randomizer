package ui.tabs.gba;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabGridItem;
import ui.views.BasesView;
import ui.views.EnemyBuffsView;
import ui.views.GrowthsView;
import ui.views.MOVCONAffinityView;
import util.OptionRecorder;
import util.OptionRecorder.GBAOptionBundle;

/**
 * The Stats Tab for the GBAFE Games.
 *
 * This contains the views:
 * <ul>
 *     <li>Bases</li>
 *     <li>Growths</li>
 *     <li>Other Character Settings (Mov/Con/Affinity)</li>
 *     <li>Enemy Buffs</li>
 * </ul>
 */
public class GBAStatsTab extends YuneTabGridItem {

    private GrowthsView growths;
    private BasesView bases;
    private MOVCONAffinityView movConAffinity;

    public GBAStatsTab(CTabFolder parent, GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        growths = addView(new GrowthsView(container, type.hasSTRMAGSplit(), true));
        bases = addView(new BasesView(container, type));
        movConAffinity = addView(new MOVCONAffinityView(container));
        setViewData(movConAffinity, 1, 2);
    }

    @Override
    public String getTabName(){
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
    public void preloadOptions(GBAOptionBundle bundle) {
        growths.initialize(bundle.growths);
        bases.initialize(bundle.bases);
        movConAffinity.initialize(bundle.other);
    }

    @Override
    public void updateOptionBundle(GBAOptionBundle bundle) {
        bundle.growths = growths.getOptions();
        bundle.bases = bases.getOptions();
        bundle.other = movConAffinity.getOptions();
    }
}
