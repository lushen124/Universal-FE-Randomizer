package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.common.YuneTabItem;
import ui.legacy.ItemAssignmentView;
import ui.legacy.RewardRandomizationView;
import ui.legacy.WeaponsView;
import util.OptionRecorder;

public class GBAItemsTab extends YuneTabItem {

    private WeaponsView weapons;
    private ItemAssignmentView itemAssignment;
    private RewardRandomizationView rewards;

    public GBAItemsTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        weapons = addView(new WeaponsView(container, SWT.NONE, type), GuiUtil.defaultGridData(2));
        setViewData(weapons, 1, 3);
        itemAssignment = addView(new ItemAssignmentView(container, SWT.NONE, type));
        rewards = addView(new RewardRandomizationView(container, SWT.NONE, type));
    }

    @Override
    protected String getTabName() {
        return "Items";
    }

    @Override
    protected String getTabTooltip() {
        return "Contains all Setting related to Items. For Example Weapon Stats, and Weapon Assignment";
    }

    @Override
    protected int numberColumns() {
        return 2;
    }

    @Override
    public void preloadOptions(OptionRecorder.GBAOptionBundle bundle) {
        this.weapons.initialize(bundle.weapons);
        this.itemAssignment.initialize(bundle.itemAssignmentOptions);
        this.rewards.initialize(bundle.rewards);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.GBAOptionBundle bundle) {
        bundle.weapons = weapons.getOptions();
        bundle.itemAssignmentOptions = itemAssignment.getOptions();
        bundle.rewards = rewards.getOptions();
    }
}
