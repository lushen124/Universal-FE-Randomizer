package ui.fe9;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

public class FE9ClassesView extends Composite {

	private Group container;
	
	private Button randomizePCs;
	private Button randomizeLords;
	private Button randomizeThieves;
	private Button randomizeSpecial;
	private Button allowCrossgender;
	private Button mixPCRaces;
	
	private Button randomizeBosses;
	private Button mixBossRaces;
	
	private Button randomizeMinions;
	private Button mixMinionRaces;
	private Label minionChanceLabel;
	private Spinner minionChanceSpinner;
	
	private Button forceDifferent;
	
	public FE9ClassesView(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new FillLayout());
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Classes");
		container.setToolTipText("Randomizes character classes.");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginRight = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		container.setLayout(mainLayout);
		
		randomizePCs = new Button(container, SWT.CHECK);
		randomizePCs.setText("Randomize Playable Characters");
		randomizePCs.setToolTipText("Randomizes all playable characters.");
		randomizePCs.setEnabled(true);
		randomizePCs.setSelection(false);
		randomizePCs.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				randomizeLords.setEnabled(randomizePCs.getSelection());
				randomizeThieves.setEnabled(randomizePCs.getSelection());
				randomizeSpecial.setEnabled(randomizePCs.getSelection());
				allowCrossgender.setEnabled(randomizePCs.getSelection());
				mixPCRaces.setEnabled(randomizePCs.getSelection());
				
				forceDifferent.setEnabled(randomizePCs.getSelection() || randomizeBosses.getSelection() || randomizeMinions.getSelection());
			}
		});
		
		FormData pcData = new FormData();
		pcData.left = new FormAttachment(0, 5);
		pcData.top = new FormAttachment(0, 5);
		randomizePCs.setLayoutData(pcData);
		
		randomizeLords = new Button(container, SWT.CHECK);
		randomizeLords.setText("Include Lords");
		randomizeLords.setToolTipText("Randomizes Ike into different classes and adds Ranger and Lord to the class pool. This may break some scripting specifically with Ike.");
		randomizeLords.setEnabled(false);
		randomizeLords.setSelection(false);
		
		FormData lordData = new FormData();
		lordData.left = new FormAttachment(randomizePCs, 5, SWT.LEFT);
		lordData.top = new FormAttachment(randomizePCs, 5);
		randomizeLords.setLayoutData(lordData);
		
		randomizeThieves = new Button(container, SWT.CHECK);
		randomizeThieves.setText("Include Thieves");
		randomizeThieves.setToolTipText("Randomizes thief characters and adds thieves to the class pool. Former theives will come with chest keys and door keys.");
		randomizeThieves.setEnabled(false);
		randomizeThieves.setSelection(false);
		
		FormData thiefData = new FormData();
		thiefData.left = new FormAttachment(randomizeLords, 0, SWT.LEFT);
		thiefData.top = new FormAttachment(randomizeLords, 5);
		randomizeThieves.setLayoutData(thiefData);
		
		randomizeSpecial = new Button(container, SWT.CHECK);
		randomizeSpecial.setText("Include Special Classes");
		randomizeSpecial.setToolTipText("Randomizes herons (i.e. Reyson). Adds herons to the randomization pool, but only one heron is possible.");
		randomizeSpecial.setEnabled(false);
		randomizeSpecial.setSelection(false);
		
		FormData specialData = new FormData();
		specialData.left = new FormAttachment(randomizeThieves, 0, SWT.LEFT);
		specialData.top = new FormAttachment(randomizeThieves, 5);
		randomizeSpecial.setLayoutData(specialData);
		
		allowCrossgender = new Button(container, SWT.CHECK);
		allowCrossgender.setText("Allow Crossgender Assignments");
		allowCrossgender.setToolTipText("Allows male characters to be assigned traditionally female classes and vice versa.");
		allowCrossgender.setEnabled(false);
		allowCrossgender.setSelection(false);
		
		FormData crossgenderData = new FormData();
		crossgenderData.left = new FormAttachment(randomizeSpecial, 0, SWT.LEFT);
		crossgenderData.top = new FormAttachment(randomizeSpecial, 5);
		allowCrossgender.setLayoutData(crossgenderData);
		
		mixPCRaces = new Button(container, SWT.CHECK);
		mixPCRaces.setText("Allow Cross-race Assignments");
		mixPCRaces.setToolTipText("Allows beorc characters to be assigned laguz classes and vice versa.");
		mixPCRaces.setEnabled(false);
		mixPCRaces.setSelection(false);
		
		FormData pcRaceData = new FormData();
		pcRaceData.left = new FormAttachment(allowCrossgender, 0, SWT.LEFT);
		pcRaceData.top = new FormAttachment(allowCrossgender, 5);
		mixPCRaces.setLayoutData(pcRaceData);
		
		randomizeBosses = new Button(container, SWT.CHECK);
		randomizeBosses.setText("Randomize Bosses");
		randomizeBosses.setToolTipText("Randomizes all boss characters. Playable characters that are also bosses are not included.");
		randomizeBosses.setEnabled(true);
		randomizeBosses.setSelection(false);
		randomizeBosses.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mixBossRaces.setEnabled(randomizeBosses.getSelection());
				forceDifferent.setEnabled(randomizePCs.getSelection() || randomizeBosses.getSelection() || randomizeMinions.getSelection());
			}
		});
		
		FormData bossData = new FormData();
		bossData.left = new FormAttachment(0, 5);
		bossData.top = new FormAttachment(mixPCRaces, 10);
		randomizeBosses.setLayoutData(bossData);
		
		mixBossRaces = new Button(container, SWT.CHECK);
		mixBossRaces.setText("Allow Cross-race Assignments");
		mixBossRaces.setToolTipText("Allows beorc bosses to be assigned laguz classes and vice versa.");
		mixBossRaces.setEnabled(false);
		mixBossRaces.setSelection(false);
		
		FormData bossRaceData = new FormData();
		bossRaceData.left = new FormAttachment(randomizeBosses, 5, SWT.LEFT);
		bossRaceData.top = new FormAttachment(randomizeBosses, 5);
		mixBossRaces.setLayoutData(bossRaceData);
		
		randomizeMinions = new Button(container, SWT.CHECK);
		randomizeMinions.setText("Randomize Minions");
		randomizeMinions.setToolTipText("Randomizes enemy minion characters.");
		randomizeMinions.setEnabled(true);
		randomizeMinions.setSelection(false);
		randomizeMinions.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				mixMinionRaces.setEnabled(randomizeMinions.getSelection());
				minionChanceLabel.setEnabled(randomizeMinions.getSelection());
				minionChanceSpinner.setEnabled(randomizeMinions.getSelection());
				forceDifferent.setEnabled(randomizePCs.getSelection() || randomizeBosses.getSelection() || randomizeMinions.getSelection());
			}
		});
		
		FormData minionData = new FormData();
		minionData.left = new FormAttachment(0, 5);
		minionData.top = new FormAttachment(mixBossRaces, 10);
		randomizeMinions.setLayoutData(minionData);
		
		mixMinionRaces = new Button(container, SWT.CHECK);
		mixMinionRaces.setText("Allow Cross-race Assignments");
		mixMinionRaces.setToolTipText("Allows beorc minions to be assigned laguz classes and vice versa.");
		mixMinionRaces.setEnabled(false);
		mixMinionRaces.setSelection(false);
		
		FormData minionRaceData = new FormData();
		minionRaceData.left = new FormAttachment(randomizeMinions, 5, SWT.LEFT);
		minionRaceData.top = new FormAttachment(randomizeMinions, 5);
		mixMinionRaces.setLayoutData(minionRaceData);
		
		minionChanceSpinner = new Spinner(container, SWT.NONE);
		minionChanceSpinner.setValues(50, 1, 100, 0, 1, 5);
		minionChanceSpinner.setEnabled(false);
		
		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		spinnerData.top = new FormAttachment(mixMinionRaces, 5);
		minionChanceSpinner.setLayoutData(spinnerData);
		
		minionChanceLabel = new Label(container, SWT.NONE);
		minionChanceLabel.setText("Chance:");
		minionChanceLabel.setEnabled(false);
		
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(minionChanceSpinner, -5);
		labelData.top = new FormAttachment(minionChanceSpinner, 0, SWT.CENTER);
		minionChanceLabel.setLayoutData(labelData);
		
		forceDifferent = new Button(container, SWT.CHECK);
		forceDifferent.setText("Force Class Change");
		forceDifferent.setToolTipText("Ensures that no character will remain the same class if randomized.");
		forceDifferent.setEnabled(false);
		forceDifferent.setSelection(false);
		
		FormData differentData = new FormData();
		differentData.left = new FormAttachment(0, 5);
		differentData.top = new FormAttachment(minionChanceLabel, 10);
		forceDifferent.setLayoutData(differentData);
	}
	
	public FE9ClassOptions getClassOptions() {
		return new FE9ClassOptions(randomizePCs.getSelection(), 
				randomizeLords.getSelection(), 
				randomizeThieves.getSelection(), 
				randomizeSpecial.getSelection(), 
				allowCrossgender.getSelection(), 
				mixPCRaces.getSelection(), 
				
				randomizeBosses.getSelection(), 
				mixBossRaces.getSelection(), 
				
				randomizeMinions.getSelection(), 
				mixMinionRaces.getSelection(), 
				minionChanceSpinner.getSelection(), 
				
				forceDifferent.getSelection());
	}
	
	public void setClassOptions(FE9ClassOptions options) {
		if (options == null) { return; }
		randomizePCs.setSelection(options.randomizePCs);
		randomizeLords.setEnabled(options.randomizePCs);
		randomizeThieves.setEnabled(options.randomizePCs);
		randomizeSpecial.setEnabled(options.randomizePCs);
		allowCrossgender.setEnabled(options.randomizePCs);
		mixPCRaces.setEnabled(options.randomizePCs);
		
		randomizeLords.setSelection(options.includeLords);
		randomizeThieves.setSelection(options.includeThieves);
		randomizeSpecial.setSelection(options.includeSpecial);
		allowCrossgender.setSelection(options.allowCrossgender);
		mixPCRaces.setSelection(options.mixPCRaces);
		
		randomizeBosses.setSelection(options.randomizeBosses);
		mixBossRaces.setEnabled(options.randomizeBosses);
		
		mixBossRaces.setSelection(options.mixBossRaces);
		
		randomizeMinions.setSelection(options.randomizeMinions);
		mixMinionRaces.setEnabled(options.randomizeMinions);
		minionChanceLabel.setEnabled(options.randomizeMinions);
		minionChanceSpinner.setEnabled(options.randomizeMinions);
		
		mixMinionRaces.setSelection(options.mixMinionRaces);
		minionChanceSpinner.setSelection(options.minionRandomChance);
		
		forceDifferent.setEnabled(options.randomizePCs || options.randomizeBosses || options.randomizeMinions);
		forceDifferent.setSelection(options.forceDifferent);
	}
}
