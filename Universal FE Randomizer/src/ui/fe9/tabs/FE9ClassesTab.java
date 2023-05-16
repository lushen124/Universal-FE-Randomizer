package ui.fe9.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.YuneTabItem;
import ui.fe9.FE9ClassesView;
import util.OptionRecorder;

public class FE9ClassesTab extends YuneTabItem {

    FE9ClassesView classes;

    public FE9ClassesTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        classes = addView(new FE9ClassesView(container, SWT.NONE));
    }

    @Override
    protected String getTabName(){
        return "Classes";
    }

    @Override
    protected String getTabTooltip() {
        return "This Tab contains all Setting which are related to the classes that the playable and enemy characters have.";
    }

    @Override
    protected int numberColumns() {
        return 2;
    }

    @Override
    public void preloadOptions(OptionRecorder.FE9OptionBundle bundle) {
        classes.setClassOptions(bundle.classes);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        bundle.classes = classes.getClassOptions();
    }
}
