package ui.views.fe9;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import ui.model.fe9.FE9EnemyClassOptions;
import ui.views.YuneView;

public class FE9EnemyClassesView extends YuneView<FE9EnemyClassOptions> {
	
	private Button randomizeBosses;
	private Button forceBossChange;
	private Button allowBossCrossRace;
	
	private Button randomizeMinions;
	private Label minionChanceLabel;
	private Spinner minionChanceSpinner;
	private Button allowMinionCrossRace;
	
	public FE9EnemyClassesView(Composite parent) {
		super(parent);
	}
	
	@Override
	public String getGroupTitle() {
		return "Enemy Classes";
	}

	@Override
	public String getGroupTooltip() {
		return "Randomizes classes for enemy characters.";
	}

	@Override
	public void initialize(FE9EnemyClassOptions options) {
		if (options == null) {
			randomizeBosses.setSelection(false);
			forceBossChange.setSelection(false);
			allowBossCrossRace.setSelection(false);
			forceBossChange.setEnabled(false);
			allowBossCrossRace.setEnabled(false);
			
			randomizeMinions.setSelection(false);
			allowMinionCrossRace.setSelection(false);
			minionChanceSpinner.setSelection(50);
			minionChanceSpinner.setEnabled(false);
			minionChanceLabel.setEnabled(false);
			allowMinionCrossRace.setEnabled(false);
			return;
		}
		
		randomizeBosses.setSelection(options.randomizeBosses);
		forceBossChange.setSelection(options.forceChange);
		allowBossCrossRace.setSelection(options.allowCrossRaceBosses);
		forceBossChange.setEnabled(options.randomizeBosses);
		allowBossCrossRace.setEnabled(options.randomizeBosses);
		
		randomizeMinions.setSelection(options.randomizeMinions);
		minionChanceSpinner.setSelection(options.minionRandomizeChance);
		allowMinionCrossRace.setEnabled(options.allowCrossRaceMinions);
		minionChanceSpinner.setEnabled(options.randomizeMinions);
		minionChanceLabel.setEnabled(options.randomizeMinions);
		allowMinionCrossRace.setEnabled(options.randomizeMinions);
	}

	@Override
	public FE9EnemyClassOptions getOptions() {
		return new FE9EnemyClassOptions(randomizeBosses.getSelection(), forceBossChange.getSelection(), allowBossCrossRace.getSelection(), 
				randomizeMinions.getSelection(), minionChanceSpinner.getSelection(), allowMinionCrossRace.getSelection());
	}

	@Override
	protected void compose() {
		randomizeBosses = new Button(group, SWT.CHECK);
		randomizeBosses.setText("Randomize Bosses");
		randomizeBosses.setToolTipText("Randomize classes of boss characters (i.e. those with faces) not including BK or Ashnard.");
		randomizeBosses.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				forceBossChange.setEnabled(randomizeBosses.getSelection());
				allowBossCrossRace.setEnabled(randomizeBosses.getSelection());
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(0, 5);
		randomizeBosses.setLayoutData(buttonData);
		
		forceBossChange = new Button(group, SWT.CHECK);
		forceBossChange.setText("Force Class Change");
		forceBossChange.setToolTipText("Ensures (where possible) the boss to be randomized into a class that is sufficiently different from their original class.");
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(randomizeBosses, 5, SWT.LEFT);
		buttonData.top = new FormAttachment(randomizeBosses, 5);
		forceBossChange.setLayoutData(buttonData);
		
		allowBossCrossRace = new Button(group, SWT.CHECK);
		allowBossCrossRace.setText("Allow Cross-race Assignments");
		allowBossCrossRace.setToolTipText("Allows Beorc bosses to randomize into Laguz classes (and vice-versa).");
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(randomizeBosses, 5, SWT.LEFT);
		buttonData.top = new FormAttachment(forceBossChange, 5);
		allowBossCrossRace.setLayoutData(buttonData);
		
		randomizeMinions = new Button(group, SWT.CHECK);
		randomizeMinions.setText("Randomize Minions");
		randomizeMinions.setToolTipText("Randomize classes of all minion characters (i.e. those without non-generic faces).");
		randomizeMinions.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				minionChanceLabel.setEnabled(randomizeMinions.getSelection());
				minionChanceSpinner.setEnabled(randomizeMinions.getSelection());
				allowMinionCrossRace.setEnabled(randomizeMinions.getSelection());
			}
		});
		
		buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 5);
		buttonData.top = new FormAttachment(allowBossCrossRace, 10);
		randomizeMinions.setLayoutData(buttonData);
		
		minionChanceSpinner = new Spinner(group, SWT.NONE);
		minionChanceSpinner.setValues(50, 1, 100, 0, 1, 5);
		minionChanceSpinner.setToolTipText("The chance for a minion to change class. A minion succeeding this check is guaranteed to change into a sufficiently different class.");
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, 0);
		spinnerData.top = new FormAttachment(randomizeMinions, 5);
		minionChanceSpinner.setLayoutData(spinnerData);
		
		minionChanceLabel = new Label(group, SWT.NONE);
		minionChanceLabel.setText("Chance: ");
		
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(minionChanceSpinner, 0, SWT.CENTER);
		labelData.right = new FormAttachment(minionChanceSpinner, -5);
		minionChanceLabel.setLayoutData(labelData);
		
		allowMinionCrossRace = new Button(group, SWT.CHECK);
		allowMinionCrossRace.setText("Allow Cross-race Assignments");
		allowMinionCrossRace.setToolTipText("Allows Beorc minions to randomize into Laguz classes (and vice-versa).");
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(minionChanceSpinner, 5);
		buttonData.left = new FormAttachment(randomizeMinions, 5, SWT.LEFT);
		allowMinionCrossRace.setLayoutData(buttonData);
	}
}
