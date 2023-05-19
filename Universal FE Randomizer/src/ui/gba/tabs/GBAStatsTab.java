package ui.gba.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.legacy.BasesView;
import ui.legacy.EnemyBuffsView;
import ui.legacy.GrowthsView;
import ui.legacy.MOVCONAffinityView;
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
        growths = addView(new GrowthsView(container, SWT.NONE, type.hasSTRMAGSplit()));
        enemies = addView(new EnemyBuffsView(container, SWT.NONE));
        setViewData(enemies, 1, 2);
        movConAffinity = addView(new MOVCONAffinityView(container, SWT.NONE));
        setViewData(movConAffinity, 1, 2);
        bases = addView(new BasesView(container, SWT.NONE, type));
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
        growths.setGrowthOptions(bundle.growths);
        bases.setBasesOptions(bundle.bases);
        movConAffinity.setOtherCharacterOptions(bundle.other);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.GBAOptionBundle bundle) {
        bundle.growths = growths.getGrowthOptions();
        bundle.bases = bases.getBaseOptions();
        bundle.other = movConAffinity.getOtherCharacterOptions();
    }
}
