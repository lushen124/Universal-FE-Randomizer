package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.CharacterShufflingView;
import ui.RecruitmentView;

public class CharactersTab extends YuneTabItem {
    public CharactersTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        addView(new RecruitmentView(container, SWT.NONE, type));
        addView(new CharacterShufflingView(container, SWT.NONE, type));
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
        return 2;
    }
}
