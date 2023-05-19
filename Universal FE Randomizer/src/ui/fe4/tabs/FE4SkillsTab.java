package ui.fe4.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.fe4.SkillsView;
import util.OptionRecorder;

public class FE4SkillsTab extends YuneTabItem {

    private SkillsView skills;

    public FE4SkillsTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        skills = addView(new SkillsView(container, SWT.NONE));
    }

    @Override
    protected String getTabName(){
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
        skills.setSkillOptions(bundle.skills);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.skills = skills.getSkillOptions();
    }
}
