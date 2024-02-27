package ui.tabs.fe9;

import fedata.gcnwii.fe9.FE9Data;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.common.YuneTabItem;
import ui.views.fe9.FE9SkillView;
import util.OptionRecorder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab for FE9 Skills.
 */
public class FE9SkillsTab extends YuneTabItem {

    private FE9SkillView skills;

    public FE9SkillsTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        List<String> skillList = FE9Data.Skill.allValidSkills.stream().map(FE9Data.Skill::getDisplayString).collect(Collectors.toList());
        skills = addView(new FE9SkillView(container, skillList, 3), GuiUtil.defaultGridData(3));
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
    public void preloadOptions(OptionRecorder.FE9OptionBundle bundle) {
        skills.initialize(bundle.skills);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        bundle.skills = skills.getOptions();
    }
}
