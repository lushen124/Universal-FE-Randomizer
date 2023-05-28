package ui.fe9.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.common.YuneTabItem;
import ui.legacy.GameMechanicsView;
import ui.legacy.WeaponsView;
import util.OptionRecorder;

public class FE9ItemsTab extends YuneTabItem {

    private WeaponsView weapons;
    private GameMechanicsView misc;

    public FE9ItemsTab(CTabFolder parent) {
        super(parent, GameType.FE9);
    }

    @Override
    protected void compose() {
        weapons = addView(new WeaponsView(container, SWT.NONE, type), GuiUtil.defaultGridData(2));
        misc = addView(new GameMechanicsView(container, SWT.NONE, type));
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
        misc.initialize(bundle.mechanics);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        bundle.weapons = weapons.getOptions();
        bundle.mechanics = misc.getOptions();
    }
}
