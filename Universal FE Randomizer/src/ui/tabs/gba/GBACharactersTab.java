package ui.tabs.gba;

import fedata.general.FEBase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

import ui.common.YuneTabFormItem;
import ui.common.YuneTabGridItem;
import ui.views.CharacterShufflingView;
import ui.views.ClassesView;
import ui.views.RecruitmentView;
import ui.views.gba.GBAAdvancedClassesView;
import util.OptionRecorder.GBAOptionBundle;

/**
 * Tab for all Settings related to the character pool.
 *
 * This contains the views:
 * <ul>
 *     <li>Random Recruitment</li>
 *     <li>Class Randomization</li>
 *     <li>Character Shuffling</li>
 * </ul>
 *
 */
public class GBACharactersTab extends YuneTabFormItem {
    public GBACharactersTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    private RecruitmentView recruitment;
    private CharacterShufflingView shuffling;
    private GBAAdvancedClassesView classes;

    @Override
    protected void compose() {
    	FormData containerData = new FormData();
    	containerData.left = new FormAttachment(0, 10);
    	containerData.top = new FormAttachment(0, 10);
   
        classes = addView(new GBAAdvancedClassesView(container, type, SWT.HORIZONTAL), containerData);
        
        containerData = new FormData();
        containerData.top = new FormAttachment(0, 10);
        containerData.left = new FormAttachment(classes.group, 10);
        
        recruitment = addView(new RecruitmentView(container, type), containerData);
        
        containerData = new FormData();
        containerData.top = new FormAttachment(0, 10);
        containerData.left = new FormAttachment(recruitment.group, 10);
        
        shuffling = addView(new CharacterShufflingView(container, type), containerData);
    }

    @Override
    public String getTabName() {
        return "Characters";
    }

    @Override
    protected String getTabTooltip() {
        return "This tab contains all settings that are related to the character Slots. Such as shuffling in characters from configuration or randomizing the recruitment order.";
    }

    @Override
    public void preloadOptions(GBAOptionBundle bundle) {
        classes.initialize(bundle.classes);
        recruitment.initialize(bundle.recruitmentOptions);
        shuffling.initialize(bundle.characterShufflingOptions);
    }

    @Override
    public void updateOptionBundle(GBAOptionBundle bundle) {
        bundle.classes = classes.getOptions();
        bundle.recruitmentOptions = recruitment.getOptions();
        bundle.characterShufflingOptions = shuffling.getOptions();
    }

}
