package ui.tabs.fe9;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.custom.CTabFolder;
import ui.common.GuiUtil;
import ui.common.YuneTabItem;
import ui.views.fe9.FE9ClassesView;
import ui.views.fe9.FE9EnemyBuffView;
import ui.views.BasesView;
import ui.views.fe9.CONAffinityView;
import ui.views.GrowthsView;
import util.OptionRecorder;
import util.OptionRecorder.FE9OptionBundle;

/**
 * The Stats Tab for FE9 Games.
 *
 * This contains the views:
 * <ul>
 *     <li>Bases</li>
 *     <li>Growths</li>
 *     <li>Other Character Settings (Con/Affinity)</li>
 *     <li>Enemy Buffs</li>
 *     <li>Classes</li>
 * </ul>
 */
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
        growths = addView(new GrowthsView(container, type.hasSTRMAGSplit(), false));
        setViewData(growths, 1, 2);
        bases = addView(new BasesView(container, type));
        classes = addView(new FE9ClassesView(container));
        setViewData(classes, 1, 2);
        conAffinity = addView(new CONAffinityView(container));
        enemies = addView(new FE9EnemyBuffView(container, true), GuiUtil.defaultGridData(2));
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
    public void preloadOptions(FE9OptionBundle bundle) {
        growths.initialize(bundle.growths);
        bases.initialize(bundle.bases);
        conAffinity.initialize(bundle.otherOptions);
        classes.initialize(bundle.classes);
        enemies.initialize(bundle.enemyBuff);
    }

    @Override
    public void updateOptionBundle(FE9OptionBundle bundle) {
        bundle.classes = classes.getOptions();
        bundle.otherOptions = conAffinity.getOptions();
        bundle.growths = growths.getOptions();
        bundle.bases = bases.getOptions();
        bundle.enemyBuff = enemies.getOptions();
    }
}
