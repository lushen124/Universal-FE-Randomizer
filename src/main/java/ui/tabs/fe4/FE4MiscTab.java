package ui.tabs.fe4;

import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.views.GameMechanicsView;
import ui.views.RewardRandomizationView;
import util.OptionRecorder;

/**
 * Tab containing the GUI of the FE4 Misc Tab
 */
public class FE4MiscTab extends YuneTabItem {

    private GameMechanicsView mechanics;
    private RewardRandomizationView rewards;

    public FE4MiscTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        mechanics = addView(new GameMechanicsView(container, type));
        rewards = addView(new RewardRandomizationView(container, type));
    }

    @Override
    protected String getTabName() {
        return "Misc.";
    }

    @Override
    protected String getTabTooltip() {
        return "Tab for changes related to Game mechanics, such as Fog of War, 1RN mode, Casual Mode etc.";
    }

    @Override
    protected int numberColumns() {
        return 2;
    }

    @Override
    public void preloadOptions(OptionRecorder.FE4OptionBundle bundle) {
        mechanics.initialize(bundle.mechanics);
        rewards.initialize(bundle.rewards);
    }


    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.mechanics = mechanics.getOptions();
        bundle.rewards = rewards.getOptions();
    }
}
