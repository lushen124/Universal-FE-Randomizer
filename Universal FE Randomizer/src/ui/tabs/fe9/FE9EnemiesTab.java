package ui.tabs.fe9;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

import fedata.general.FEBase.GameType;
import ui.common.YuneTabFormItem;
import ui.views.fe9.FE9EnemyBuffView;
import ui.views.fe9.FE9EnemyClassesView;
import util.OptionRecorder.FE9OptionBundle;

public class FE9EnemiesTab extends YuneTabFormItem {
	
	private FE9EnemyClassesView classesView;
	private FE9EnemyBuffView buffView;
	
	public FE9EnemiesTab(CTabFolder parent) {
		super(parent, GameType.FE9);
	}

	@Override
	protected void compose() {
		classesView = new FE9EnemyClassesView(container);
		
		FormData classesData = new FormData();
		classesData.left = new FormAttachment(0, 10);
		classesData.top = new FormAttachment(0, 10);
		addView(classesView, classesData);
		
		buffView = new FE9EnemyBuffView(container, true);
		
		FormData buffData = new FormData();
		buffData.left = new FormAttachment(classesView.group, 5);
		buffData.top = new FormAttachment(0, 10);
		addView(buffView, buffData);
	}

	@Override
	public String getTabName() {
		return "Enemies";
	}

	@Override
	protected String getTabTooltip() {
		return "This Tab contains all Setting which are related to enemies.";
	}

    @Override
    public void preloadOptions(FE9OptionBundle bundle) {
    	classesView.initialize(bundle.enemyClasses);
        buffView.initialize(bundle.enemyBuff);
    }

    @Override
    public void updateOptionBundle(FE9OptionBundle bundle) {
    	bundle.enemyClasses = classesView.getOptions();
    	bundle.enemyBuff = buffView.getOptions();
    }
}
