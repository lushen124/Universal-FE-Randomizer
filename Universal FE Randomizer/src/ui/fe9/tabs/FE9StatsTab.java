package ui.fe9.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.legacy.BasesView;
import ui.legacy.CONAffinityView;
import ui.legacy.GrowthsView;
import util.OptionRecorder;

public class FE9StatsTab extends YuneTabItem {

    private GrowthsView growths;
    private BasesView bases;
    private CONAffinityView conAffinity;
    public FE9StatsTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        growths = addView(new GrowthsView(container, SWT.NONE, type.hasSTRMAGSplit()));
        bases = addView(new BasesView(container, SWT.NONE, type));
        conAffinity = addView(new CONAffinityView(container, SWT.NONE));
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
    public void preloadOptions(OptionRecorder.FE9OptionBundle bundle) {
        growths.setGrowthOptions(bundle.growths);
        bases.setBasesOptions(bundle.bases);
        conAffinity.setOtherCharacterOptions(bundle.otherOptions);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        bundle.growths = growths.getGrowthOptions();
        bundle.bases = bases.getBaseOptions();
        bundle.otherOptions = conAffinity.getOtherCharacterOptions();
    }
}
