package ui.gba.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridData;
import ui.BasesView;
import ui.GrowthsView;
import ui.MOVCONAffinityView;
import util.OptionRecorder;

public class StatsTab extends YuneTabItem {

    private GrowthsView growths;
    private BasesView bases;
    private MOVCONAffinityView movConAffinity;

    public StatsTab(CTabFolder parent, GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        GridData growthsData = new GridData();
        growths = addView(new GrowthsView(container, SWT.NONE, type.hasSTRMAGSplit()), growthsData);
        bases = addView(new BasesView(container, SWT.NONE, type));
        movConAffinity = addView(new MOVCONAffinityView(container, SWT.NONE));
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
}
