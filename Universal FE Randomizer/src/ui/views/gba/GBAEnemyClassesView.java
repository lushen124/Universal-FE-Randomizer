package ui.views.gba;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import fedata.general.FEBase.GameType;
import ui.model.gba.EnemyClassOptions;
import ui.views.YuneView;

public class GBAEnemyClassesView extends YuneView<EnemyClassOptions> {
	private Button enableBossesButton;
	private Button enableMinionButton;
	private Label minionChanceLabel;
	private Spinner minionChanceSpinner;
	
	public GBAEnemyClassesView(Composite parent, GameType type) {
		super(parent, type);
	}

	@Override
	public String getGroupTitle() {
		return "Enemy Classes";
	}
	
	@Override
	public String getGroupTooltip() {
		return "Randomizes the classes of enemy characters.";
	}
	
	@Override
	public void initialize(EnemyClassOptions options) {
		if (options == null) {
			enableBossesButton.setSelection(false);
			enableMinionButton.setSelection(false);
			minionChanceLabel.setEnabled(false);
			minionChanceSpinner.setEnabled(false);
		} else {
			enableBossesButton.setSelection(options.randomizeBosses);
			enableMinionButton.setSelection(options.randomizeMinions);
			if (options.randomizeMinions) {
				minionChanceLabel.setEnabled(true);
				minionChanceSpinner.setEnabled(true);
				minionChanceSpinner.setSelection(options.minionChance);
			}
		}
	}

	@Override
	public EnemyClassOptions getOptions() {
		return new EnemyClassOptions(enableBossesButton.getSelection(), enableMinionButton.getSelection(), minionChanceSpinner.getSelection());
	}

	@Override
	protected void compose() {
		enableBossesButton = new Button(group, SWT.CHECK);
		enableBossesButton.setText("Randomize Bosses");
		enableBossesButton.setToolTipText("Randomize the class of all boss characters (enemy characters that have faces).");
		
		FormData buttonData = new FormData();
		buttonData.top = new FormAttachment(0, 0);
		buttonData.left = new FormAttachment(0, 0);
		enableBossesButton.setLayoutData(buttonData);
		
		enableMinionButton = new Button(group, SWT.CHECK);
		enableMinionButton.setText("Randomize Minions");
		enableMinionButton.setToolTipText("Randomize the classes of all minor enemies (enemy characters that don't have faces).");
		enableMinionButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				minionChanceLabel.setEnabled(enableMinionButton.getSelection());
				minionChanceSpinner.setEnabled(enableMinionButton.getSelection());
			}
		});
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(enableBossesButton, 10);
		buttonData.left = new FormAttachment(0, 0);
		enableMinionButton.setLayoutData(buttonData);
		
		minionChanceSpinner = new Spinner(group, SWT.NONE);
		minionChanceSpinner.setValues(50, 1, 100, 0, 5, 10);
		minionChanceSpinner.setToolTipText("The chance for a minion to change their class.");
		
		FormData spinnerData = new FormData();
		spinnerData.top = new FormAttachment(enableMinionButton, 5);
		spinnerData.right = new FormAttachment(100, 0);
		minionChanceSpinner.setLayoutData(spinnerData);
		
		minionChanceLabel = new Label(group, SWT.NONE);
		minionChanceLabel.setText("Chance: ");
		
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(minionChanceSpinner, 0, SWT.CENTER);
		labelData.right = new FormAttachment(minionChanceSpinner, -5);
		minionChanceLabel.setLayoutData(labelData);
	}
	
}
