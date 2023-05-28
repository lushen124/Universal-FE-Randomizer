package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.legacy.CharacterShufflingView;
import ui.legacy.ClassesView;
import ui.legacy.RecruitmentView;
import util.OptionRecorder.GBAOptionBundle;

public class GBACharactersTab extends YuneTabItem {
    public GBACharactersTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    private RecruitmentView recruitment;
    private CharacterShufflingView shuffling;
    private ClassesView classes;

    @Override
    protected void compose() {
        classes = addView(new ClassesView(container, SWT.NONE, type));
        recruitment = addView(new RecruitmentView(container, SWT.NONE, type));
        shuffling = addView(new CharacterShufflingView(container, SWT.NONE, type));
    }

    @Override
    protected String getTabName() {
        return "Characters";
    }

    @Override
    protected String getTabTooltip() {
        return "This tab contains all settings that are related to the character Slots. Such as shuffling in characters from configuration or randomizing the recruitment order.";
    }

    @Override
    protected int numberColumns() {
        return 3;
    }

    @Override
    public void preloadOptions(GBAOptionBundle bundle) {
        classes.initialize(bundle.classes);
        recruitment.setRecruitmentOptions(bundle.recruitmentOptions);
        shuffling.initialize(bundle.characterShufflingOptions, type);
    }

    @Override
    public void updateOptionBundle(GBAOptionBundle bundle) {
        bundle.classes = classes.getOptions();
        bundle.recruitmentOptions = recruitment.getRecruitmentOptions();
        bundle.characterShufflingOptions = shuffling.getOptions();
    }

}
