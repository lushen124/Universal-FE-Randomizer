package ui.tabs.fe4;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.views.fe4.FE4ClassesView;
import ui.views.fe4.FE4PromotionView;
import ui.common.YuneTabItem;
import util.OptionRecorder;

/**
 * The Classes Tab for FE4.
 *
 * This contains the views:
 * <ul>
 *     <li>Classes</li>
 *     <li>Promotions</li>
 * </ul>
 */
public class FE4ClassesTab extends YuneTabItem {

    FE4ClassesView classes;
    FE4PromotionView promotions;

    public FE4ClassesTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        // Account for internal margins within the width of this view
        classes = addView(new FE4ClassesView(container, 2), GuiUtil.defaultGridData(2, 4 * GuiUtil.DEFAULT_MARGIN_5));
        promotions = addView(new FE4PromotionView(container));
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
        classes.initialize(bundle.classes);
        promotions.initialize(bundle.promo);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.classes = classes.getOptions();
        bundle.promo = promotions.getOptions();
    }
}
