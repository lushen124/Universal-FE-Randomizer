package ui.tabs.fe9;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.common.YuneTabItem;
import ui.views.RewardRandomizationView;
import ui.views.WeaponsView;
import util.OptionRecorder;

/**
 * The Stats Tab for FE9.
 *
 * This contains the views:
 * <ul>
 *     <li>Weapon</li>
 *     <li>Rewards</li>
 * </ul>
 */
public class FE9ItemsTab extends YuneTabItem {

    private WeaponsView weapons;
    private RewardRandomizationView rewards;

    public FE9ItemsTab(CTabFolder parent) {
        super(parent, GameType.FE9);
    }

    @Override
    protected void compose() {
        weapons = addView(new WeaponsView(container, type, 2), GuiUtil.defaultGridData(2));
        rewards = addView(new RewardRandomizationView(container, type));
    }

    @Override
    protected String getTabName() {
        return "Items";
    }

    @Override
    protected String getTabTooltip() {
        return "Contains all Setting related to Items.";
    }

    @Override
    protected int numberColumns() {
        return 2;
    }

    @Override
    public void preloadOptions(OptionRecorder.FE9OptionBundle bundle) {
        weapons.initialize(bundle.weapons);
        rewards.initialize(bundle.rewards);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        bundle.weapons = weapons.getOptions();
        bundle.rewards = rewards.getOptions();
    }
}
