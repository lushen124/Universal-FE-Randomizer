package ui.tabs.gba;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

import fedata.general.FEBase.GameType;
import ui.common.YuneTabFormItem;
import ui.views.EnemyBuffsView;
import ui.views.gba.GBAEnemyClassesView;
import util.OptionRecorder.GBAOptionBundle;

public class GBAEnemiesTab extends YuneTabFormItem {

	private EnemyBuffsView buffView;
	private GBAEnemyClassesView enemyClassView;
	
	public GBAEnemiesTab(CTabFolder parent, GameType type) {
		super(parent, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void compose() {
		FormData buffData = new FormData();
		buffData.top = new FormAttachment(0, 10);
		buffData.left = new FormAttachment(0, 10);
		
		buffView = addView(new EnemyBuffsView(container), buffData);
		
		FormData enemyClassData = new FormData();
		enemyClassData.left = new FormAttachment(buffView.group, 5);
		enemyClassData.top = new FormAttachment(0, 10);
		
		enemyClassView = addView(new GBAEnemyClassesView(container, type), enemyClassData);
	}

	@Override
	public String getTabName() {
		return "Enemies";
	}

	@Override
	protected String getTabTooltip() {
		return "This tab includes all options regarding enemies (minions and bosses).";
	}

	@Override
	public void preloadOptions(GBAOptionBundle bundle) {
		buffView.initialize(bundle.enemies);
		enemyClassView.initialize(bundle.enemyClasses);
	}
	
	@Override
	public void updateOptionBundle(GBAOptionBundle bundle) {
		bundle.enemies = buffView.getOptions();
		bundle.enemyClasses = enemyClassView.getOptions();
	}
}
