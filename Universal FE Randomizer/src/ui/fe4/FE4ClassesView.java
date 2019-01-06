package ui.fe4;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import ui.fe4.FE4ClassOptions.ChildOptions;

public class FE4ClassesView extends Composite {
	private Group container;
	
	private Button randomizePCs;
	private Button includeLords;
	private Button includeThieves;
	private Button includeDancers;
	private Button adjustChildren;
	private Button randomizeChildren;
	private Button randomizeBlood;
	
	private Button randomizeMinions;
	
	private Button randomizeArenas;
	
	private Button randomizeBosses;
	private Button randomizeBossBlood;
	
	public FE4ClassesView(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new FillLayout());
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Classes");
		container.setToolTipText("Randomize character classes and related options requiring flexible classes.");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginRight = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		container.setLayout(mainLayout);
		
		randomizePCs = new Button(container, SWT.CHECK);
		randomizePCs.setText("Randomize Playable Characters");
		randomizePCs.setToolTipText("Randomizes all playable characters and enables other options requiring flexible classes.");
		randomizePCs.setEnabled(true);
		randomizePCs.setSelection(false);
		randomizePCs.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				boolean enabled = randomizePCs.getSelection();
				includeLords.setEnabled(enabled);
				includeThieves.setEnabled(enabled);
				includeDancers.setEnabled(enabled);
				
				adjustChildren.setEnabled(enabled);
				randomizeChildren.setEnabled(enabled);
				
				randomizeBlood.setEnabled(enabled);
			}
		});
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		randomizePCs.setLayoutData(optionData);
		
		includeLords = new Button(container, SWT.CHECK);
		includeLords.setText("Include Lords");
		includeLords.setToolTipText("Include Sigurd and Seliph for randomization and adds Junior Lord and Lord Knight to the class pool.");
		includeLords.setEnabled(false);
		includeLords.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(randomizePCs, 10, SWT.LEFT);
		optionData.top = new FormAttachment(randomizePCs, 5);
		includeLords.setLayoutData(optionData);
		
		includeThieves = new Button(container, SWT.CHECK);
		includeThieves.setText("Include Thieves");
		includeThieves.setToolTipText("Include Dew, Patty, and Daisy for randomization and adds Thief and Thief Fighter to the class pool.");
		includeThieves.setEnabled(false);
		includeThieves.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(includeLords, 0, SWT.LEFT);
		optionData.top = new FormAttachment(includeLords, 5);
		includeThieves.setLayoutData(optionData);
		
		includeDancers = new Button(container, SWT.CHECK);
		includeDancers.setText("Include Dancers");
		includeDancers.setToolTipText("Includes Silvia, Lene, and Laylea for randomization and adds Dancer to the class pool (limit 1 per generation).");
		includeDancers.setEnabled(false);
		includeDancers.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(includeThieves, 0, SWT.LEFT);
		optionData.top = new FormAttachment(includeThieves, 5);
		includeDancers.setLayoutData(optionData);
		
		Group childGroup = new Group(container, SWT.NONE);
		childGroup.setText("Children Options");
		
		FormLayout groupLayout = new FormLayout();
		groupLayout.marginLeft = 5;
		groupLayout.marginRight = 5;
		groupLayout.marginTop = 5;
		groupLayout.marginBottom = 5;
		childGroup.setLayout(groupLayout);
		
		FormData groupData = new FormData();
		groupData.left = new FormAttachment(includeDancers, 0, SWT.LEFT);
		groupData.top = new FormAttachment(includeDancers, 5);
		groupData.right = new FormAttachment(100, -5);
		childGroup.setLayoutData(groupData);
		
		{
			adjustChildren = new Button(childGroup, SWT.RADIO);
			adjustChildren.setText("Match Parents");
			adjustChildren.setToolTipText("Sets the classes of the child characters to match parent equivalents. In cases where children for a mother are different classes, the analogue from generation 1 is used.\n\nSubstitute characters are not included, and will always be randomized independently.\n\nFor example, Edain's Children will be assigned classes matching Edain for Lana and matching Midir for Lester.");
			adjustChildren.setEnabled(false);
			adjustChildren.setSelection(true);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(0, 0);
			optionData.top = new FormAttachment(0, 0);
			adjustChildren.setLayoutData(optionData);
			
			randomizeChildren = new Button(childGroup, SWT.RADIO);
			randomizeChildren.setText("Randomize");
			randomizeChildren.setToolTipText("Randomly assigns the classes of child characters.");
			randomizeChildren.setEnabled(false);
			randomizeChildren.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(adjustChildren, 0, SWT.LEFT);
			optionData.top = new FormAttachment(adjustChildren, 5);
			randomizeChildren.setLayoutData(optionData);
		}
		
		randomizeBlood = new Button(container, SWT.CHECK);
		randomizeBlood.setText("Randomize Holy Blood");
		randomizeBlood.setToolTipText("Randomly assigns holy blood to characters. Note that if this option is not enabled, characters with Major Holy Blood will have a restricted class pool.");
		randomizeBlood.setEnabled(false);
		randomizeBlood.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(childGroup, 0, SWT.LEFT);
		optionData.top = new FormAttachment(childGroup, 5);
		randomizeBlood.setLayoutData(optionData);
		
		randomizeMinions = new Button(container, SWT.CHECK);
		randomizeMinions.setText("Randomize Regular Enemies");
		randomizeMinions.setToolTipText("Randomizes the classes for regular enemies. Due to how the game was coded and how many enemies are copy/pasted, randomizations are done in batches.");
		randomizeMinions.setEnabled(true);
		randomizeMinions.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(randomizePCs, 0, SWT.LEFT);
		optionData.top = new FormAttachment(randomizeBlood, 10);
		randomizeMinions.setLayoutData(optionData);
		
		randomizeArenas = new Button(container, SWT.CHECK);
		randomizeArenas.setText("Randomize Arena Enemies");
		randomizeArenas.setToolTipText("Randomizes the classes of enemies found in the arena.");
		randomizeArenas.setEnabled(true);
		randomizeArenas.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(randomizeMinions, 0, SWT.LEFT);
		optionData.top = new FormAttachment(randomizeMinions, 10);
		randomizeArenas.setLayoutData(optionData);
		
		randomizeBosses = new Button(container, SWT.CHECK);
		randomizeBosses.setText("Randomize Bosses");
		randomizeBosses.setToolTipText("Randomizes the classes of all bosses (all enemy characters with faces and names).");
		randomizeBosses.setEnabled(true);
		randomizeBosses.setSelection(false);
		randomizeBosses.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				randomizeBossBlood.setEnabled(randomizeBosses.getSelection());
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(randomizeArenas, 0, SWT.LEFT);
		optionData.top = new FormAttachment(randomizeArenas, 10);
		randomizeBosses.setLayoutData(optionData);
		
		randomizeBossBlood = new Button(container, SWT.CHECK);
		randomizeBossBlood.setText("Randomize Boss Holy Blood");
		randomizeBossBlood.setToolTipText("Only applies to bosses with holy blood.\n\nIf disabled, boss class pools are restricted by their holy blood weapons.\nIf enabled, allows bosses to change their holy blood, widening the class pool.");
		randomizeBossBlood.setEnabled(false);
		randomizeBossBlood.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(randomizeBosses, 10, SWT.LEFT);
		optionData.top = new FormAttachment(randomizeBosses, 5);
		randomizeBossBlood.setLayoutData(optionData);
	}
	
	public FE4ClassOptions getClassOptions() {
		ChildOptions childOptions = ChildOptions.ADJUST_TO_MATCH;
		if (randomizeChildren.getSelection()) { childOptions = ChildOptions.RANDOM_CLASS; }
		
		return new FE4ClassOptions(randomizePCs.getSelection(), includeLords.getSelection(), includeThieves.getSelection(), includeDancers.getSelection(), childOptions, randomizeBlood.getSelection(), 
				randomizeMinions.getSelection(), randomizeArenas.getSelection(), randomizeBosses.getSelection(), randomizeBossBlood.getSelection());
	}
}
