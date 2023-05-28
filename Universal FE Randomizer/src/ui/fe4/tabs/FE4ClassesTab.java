package ui.fe4.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.fe4.FE4ClassesView;
import ui.fe4.FE4PromotionView;
import ui.common.YuneTabItem;
import util.OptionRecorder;

public class FE4ClassesTab extends YuneTabItem {

    FE4ClassesView classes;
    FE4PromotionView promotions;

    public FE4ClassesTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        classes = addView(new FE4ClassesView(container, SWT.NONE), GuiUtil.defaultGridData(2, true));
        promotions = addView(new FE4PromotionView(container, SWT.NONE));
    }

    @Override
    protected String getTabName(){
        return "Classes";
    }

    @Override
    protected String getTabTooltip() {
        return "This Tab contains all Setting which are related to the classes that the playable and enemy characters have.";
    }

    @Override
    protected int numberColumns() {
        return 2;
    }

    @Override
    public void preloadOptions(OptionRecorder.FE4OptionBundle bundle) {
        classes.setClassOptions(bundle.classes);
        promotions.setPromotionOptions(bundle.promo);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.classes = classes.getClassOptions();
        bundle.promo = promotions.getPromotionOptions();
    }
}
