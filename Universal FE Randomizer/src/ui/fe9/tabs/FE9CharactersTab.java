package ui.fe9.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.common.YuneTabItem;
import ui.fe9.FE9ClassesView;
import ui.fe9.FE9EnemyBuffView;
import ui.legacy.BasesView;
import ui.legacy.CONAffinityView;
import ui.legacy.GrowthsView;
import util.OptionRecorder;

public class FE9CharactersTab extends YuneTabItem {
    private GrowthsView growths;
    private BasesView bases;
    private CONAffinityView conAffinity;
    private FE9ClassesView classes;
    private FE9EnemyBuffView enemies;

    public FE9CharactersTab(CTabFolder parent) {
        super(parent, GameType.FE4);
    }

    @Override
    protected void compose() {
        growths = addView(new GrowthsView(container, SWT.NONE, type.hasSTRMAGSplit()));
        setViewData(growths, 1, 2);
        bases = addView(new BasesView(container, SWT.NONE, type));
        classes = addView(new FE9ClassesView(container, SWT.NONE));
        setViewData(classes, 1, 2);
        conAffinity = addView(new CONAffinityView(container, SWT.NONE));
        enemies = addView(new FE9EnemyBuffView(container, SWT.NONE), GuiUtil.defaultGridData(2));
        setViewData(enemies, 2, 1);
    }

    @Override
    protected String getTabName(){
        return "Characters";
    }

    @Override
    protected String getTabTooltip() {
        return "This Tab contains all Setting which are related to the characters stats and classes (including enemies).";
    }

    @Override
    protected int numberColumns() {
        return 3;
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
