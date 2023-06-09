package ui.tabs.fe4;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.common.YuneTabItem;
import ui.views.fe4.FE4SkillsView;
import util.OptionRecorder;

/**
 * The Stats Tab for FE4.
 */
public class FE4SkillsTab extends YuneTabItem {

    private FE4SkillsView skills;

    public FE4SkillsTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        skills = addView(new FE4SkillsView(container, 3), GuiUtil.defaultGridData(3));
    }

    @Override
    protected String getTabName() {
        return "Skills";
    }

    @Override
    protected String getTabTooltip() {
        return "This Tab contains all Setting related to the skill distribution for the Player Characters.";
    }

    @Override
    protected int numberColumns() {
        return 1;
    }

    @Override
    public void preloadOptions(OptionRecorder.FE4OptionBundle bundle) {
        skills.initialize(bundle.skills);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.skills = skills.getOptions();
    }
}
