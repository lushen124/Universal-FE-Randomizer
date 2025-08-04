package ui.tabs.fe9;

import org.eclipse.swt.custom.CTabFolder;

import fedata.general.FEBase.GameType;
import ui.common.YuneTabItem;
import ui.views.GameMechanicsView;
import util.OptionRecorder;

public class FE9MiscTab extends YuneTabItem {
	
	private GameMechanicsView mechanics;
	
	public FE9MiscTab(CTabFolder parent) {
        super(parent, GameType.FE9);
    }

    @Override
    protected void compose() {
        mechanics = addView(new GameMechanicsView(container, type));
    }

    @Override
    protected String getTabName() {
        return "Misc.";
    }

    @Override
    protected String getTabTooltip() {
        return "Tab for changes related to Game mechanics";
    }

    @Override
    protected int numberColumns() {
        return 1;
    }

    @Override
    public void preloadOptions(OptionRecorder.FE9OptionBundle bundle) {
        mechanics.initialize(bundle.mechanics);
    }


    @Override
    public void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        bundle.mechanics = mechanics.getOptions();
    }

}
