package ui.tabs.fe9;

import fedata.general.FEBase.GameType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

import ui.common.YuneTabFormItem;
import ui.views.BasesView;
import ui.views.fe9.CONAffinityView;
import ui.views.fe9.FE9AdvancedClassesView;
import ui.views.GrowthsView;
import util.OptionRecorder.FE9OptionBundle;

/**
 * The Stats Tab for FE9 Games.
 *
 * This contains the views:
 * <ul>
 *     <li>Bases</li>
 *     <li>Growths</li>
 *     <li>Other Character Settings (Con/Affinity)</li>
 *     <li>Player Classes</li>
 * </ul>
 */
public class FE9CharactersTab extends YuneTabFormItem {
    private GrowthsView growths;
    private BasesView bases;
    private CONAffinityView conAffinity;
    private FE9AdvancedClassesView advancedClasses;

    public FE9CharactersTab(CTabFolder parent) {
        super(parent, GameType.FE9);
    }

    @Override
    protected void compose() {
    	growths = new GrowthsView(container, type.hasSTRMAGSplit(), false);
    	FormData growthsData = new FormData();
    	growthsData.top = new FormAttachment(0, 10);
    	growthsData.left = new FormAttachment(0, 10);
    	addView(growths, growthsData);
    	
    	bases = new BasesView(container, type);
    	FormData basesData = new FormData();
    	basesData.top = new FormAttachment(growths.group, 10);
    	basesData.left = new FormAttachment(0, 10);
    	basesData.right = new FormAttachment(growths.group, 0, SWT.RIGHT);
    	addView(bases, basesData);
    	
    	advancedClasses = new FE9AdvancedClassesView(container, FE9AdvancedClassesView.LayoutStyle.WIDE);
    	FormData classData = new FormData();
    	classData.top = new FormAttachment(0, 10);
    	classData.left = new FormAttachment(growths.group, 10);
    	addView(advancedClasses, classData);
    	
    	conAffinity = new CONAffinityView(container);
    	FormData conAffinityData = new FormData();
    	conAffinityData.top = new FormAttachment(bases.group, 10);
    	conAffinityData.left = new FormAttachment(0, 10);
    	conAffinityData.right = new FormAttachment(bases.group, 0, SWT.RIGHT);
    	addView(conAffinity, conAffinityData);
    }

    @Override
    public String getTabName(){
        return "Characters";
    }

    @Override
    protected String getTabTooltip() {
        return "This Tab contains all Setting which are related to player character stats and classes.";
    }

    @Override
    public void preloadOptions(FE9OptionBundle bundle) {
        growths.initialize(bundle.growths);
        bases.initialize(bundle.bases);
        conAffinity.initialize(bundle.otherOptions);
        advancedClasses.initialize(bundle.pcClasses);
    }

    @Override
    public void updateOptionBundle(FE9OptionBundle bundle) {
        bundle.otherOptions = conAffinity.getOptions();
        bundle.growths = growths.getOptions();
        bundle.bases = bases.getOptions();
        bundle.pcClasses = advancedClasses.getOptions();
    }
    
    @Override
    public boolean validate() {
    	return advancedClasses.validate();
    }
    
    @Override
    public String getValidationError() {
    	return advancedClasses.getValidationError();
    }
}
