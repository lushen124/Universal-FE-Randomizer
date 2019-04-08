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

import ui.fe4.FE4ClassOptions.BloodOptions;
import ui.fe4.FE4ClassOptions.ChildOptions;
import ui.fe4.FE4ClassOptions.ItemAssignmentOptions;
import ui.fe4.FE4ClassOptions.ShopOptions;

public class FE4ClassesView extends Composite {
	private Group container;
	
	private Button randomizePCs;
	private Button includeLords;
	private Button retainHealers;
	private Button retainHorses;
	private Button includeThieves;
	private Button includeDancers;
	private Button includeJulia;
	
	private Button adjustChildrenStrict;
	private Button adjustChildrenLoose;
	private Button randomizeChildren;
	
	private Button playerBloodNoChange;
	private Button playerBloodShuffle;
	private Button playerBloodRandomize;
	
	private Button retainShops;
	private Button adjustShops;
	private Button randomizeShops;
	private Button adjustConvoItems;
	
	private Button adjustSTRMAG;
	private Button strictSidgradeItems;
	private Button looseSidegradeItems;
	private Button randomItems;
	
	private Button randomizeMinions;
	
	private Button randomizeArenas;
	
	private Button randomizeBosses;
	
	private Button bossBloodNoChange;
	private Button bossBloodShuffle;
	private Button bossBloodRandomize;
	
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
		randomizePCs.setToolTipText("Randomizes all playable characters and enables other options requiring flexible classes.\n\nNotes:\n1. All holy weapons can be sold and re-bought freely.\n2. Sigurd's and Quan's holy weapons will NOT pass down to their children.");
		randomizePCs.setEnabled(true);
		randomizePCs.setSelection(false);
		randomizePCs.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				boolean enabled = randomizePCs.getSelection();
				includeLords.setEnabled(enabled);
				retainHealers.setEnabled(enabled);
				retainHorses.setEnabled(enabled);
				includeThieves.setEnabled(enabled);
				includeDancers.setEnabled(enabled);
				includeJulia.setEnabled(enabled);
				
				adjustChildrenStrict.setEnabled(enabled);
				adjustChildrenLoose.setEnabled(enabled);
				randomizeChildren.setEnabled(enabled);
				
				playerBloodNoChange.setEnabled(enabled);
				playerBloodShuffle.setEnabled(enabled && randomizeBosses.getSelection());
				playerBloodRandomize.setEnabled(enabled);
				
				retainShops.setEnabled(enabled);
				adjustShops.setEnabled(enabled);
				randomizeShops.setEnabled(enabled);
				
				adjustConvoItems.setEnabled(enabled);
				adjustSTRMAG.setEnabled(enabled);
				
				strictSidgradeItems.setEnabled(enabled);
				looseSidegradeItems.setEnabled(enabled);
				randomItems.setEnabled(enabled);
				
				if (!enabled) {
					if (playerBloodShuffle.getSelection() && bossBloodShuffle.getSelection()) {
						bossBloodShuffle.setSelection(false);
						playerBloodShuffle.setSelection(false);
						bossBloodNoChange.setSelection(true);
						playerBloodNoChange.setSelection(true);
					}
					bossBloodShuffle.setEnabled(false);
				} else {
					if (randomizeBosses.getSelection()) {
						bossBloodShuffle.setEnabled(true);
					}
				}
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
		includeDancers.setToolTipText("Includes Silvia, Lene, and Laylea for randomization and adds Dancer to the class pool (limit 1 per generation).\n\nNote: This will break Silvia's village event in Chapter 4 if enabled.");
		includeDancers.setEnabled(false);
		includeDancers.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(includeThieves, 0, SWT.LEFT);
		optionData.top = new FormAttachment(includeThieves, 5);
		includeDancers.setLayoutData(optionData);
		
		includeJulia = new Button(container, SWT.CHECK);
		includeJulia.setText("Include Julia");
		includeJulia.setToolTipText("Allows Julia to be randomized. Removes the guarantee of having Naga for endgame.");
		includeJulia.setEnabled(false);
		includeJulia.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(includeDancers, 0, SWT.LEFT);
		optionData.top = new FormAttachment(includeDancers, 5);
		includeJulia.setLayoutData(optionData);
		
		retainHealers = new Button(container, SWT.CHECK);
		retainHealers.setText("Retain Healers");
		retainHealers.setToolTipText("Ensures Edain, Claud, Lana, Muirne, Coirpre, and Charlot can all still use staves.");
		retainHealers.setEnabled(false);
		retainHealers.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(includeJulia, 0, SWT.LEFT);
		optionData.top = new FormAttachment(includeJulia, 5);
		retainHealers.setLayoutData(optionData);
		
		retainHorses = new Button(container, SWT.CHECK);
		retainHorses.setText("Retain Horseback Units");
		retainHorses.setToolTipText("Limits normally horseback units to other horseback classes.");
		retainHorses.setEnabled(false);
		retainHorses.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(retainHealers, 0, SWT.LEFT);
		optionData.top = new FormAttachment(retainHealers, 5);
		retainHorses.setLayoutData(optionData);
		
		Group childGroup = new Group(container, SWT.NONE);
		childGroup.setText("Children Options");
		
		FormLayout groupLayout = new FormLayout();
		groupLayout.marginLeft = 5;
		groupLayout.marginRight = 5;
		groupLayout.marginTop = 5;
		groupLayout.marginBottom = 5;
		childGroup.setLayout(groupLayout);
		
		FormData groupData = new FormData();
		groupData.left = new FormAttachment(retainHorses, 0, SWT.LEFT);
		groupData.top = new FormAttachment(retainHorses, 5);
		groupData.right = new FormAttachment(100, -5);
		childGroup.setLayoutData(groupData);
		
		{
			adjustChildrenStrict = new Button(childGroup, SWT.RADIO);
			adjustChildrenStrict.setText("Match Parents (Strict)");
			adjustChildrenStrict.setToolTipText("Sets the classes of the child characters to match parent equivalents. In cases where children for a mother are different classes, the analogue from generation 1 is used.\n\nSubstitute characters match exactly with their child counterparts.\n\nFor example, Edain's Children will be assigned classes matching Edain for Lana and matching Midir for Lester.");
			adjustChildrenStrict.setEnabled(false);
			adjustChildrenStrict.setSelection(true);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(0, 0);
			optionData.top = new FormAttachment(0, 0);
			adjustChildrenStrict.setLayoutData(optionData);
			
			adjustChildrenLoose = new Button(childGroup, SWT.RADIO);
			adjustChildrenLoose.setText("Match Parents (Loose)");
			adjustChildrenLoose.setToolTipText("Sets the classes of the child characters to classes that match at least one weapon.\n\nSubstitute characters will match the children in weapon usage.\n\nFor example, if Edain randomized as an Axe Knight and Midir randomized as a Myrmidon, Lana will randomize as somebody that can use an axe and Lester will randomize as somebody that can use a sword.");
			adjustChildrenLoose.setEnabled(false);
			adjustChildrenLoose.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(adjustChildrenStrict, 0, SWT.LEFT);
			optionData.top = new FormAttachment(adjustChildrenStrict, 5);
			adjustChildrenLoose.setLayoutData(optionData);
			
			randomizeChildren = new Button(childGroup, SWT.RADIO);
			randomizeChildren.setText("Randomize");
			randomizeChildren.setToolTipText("Randomly assigns the classes of child characters.\n\nSubstitute characters will match the children in weapon usage.");
			randomizeChildren.setEnabled(false);
			randomizeChildren.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(adjustChildrenLoose, 0, SWT.LEFT);
			optionData.top = new FormAttachment(adjustChildrenLoose, 5);
			randomizeChildren.setLayoutData(optionData);
		}
		
		Group bloodGroup = new Group(container, SWT.NONE);
		bloodGroup.setText("Holy Blood");
		
		groupLayout = new FormLayout();
		groupLayout.marginLeft = 5;
		groupLayout.marginRight = 5;
		groupLayout.marginTop = 5;
		groupLayout.marginBottom = 5;
		bloodGroup.setLayout(groupLayout);
		
		groupData = new FormData();
		groupData.left = new FormAttachment(childGroup, 0, SWT.LEFT);
		groupData.top = new FormAttachment(childGroup, 5);
		groupData.right = new FormAttachment(100, -5);
		bloodGroup.setLayoutData(groupData);
		
		{
			playerBloodNoChange = new Button(bloodGroup, SWT.RADIO);
			playerBloodNoChange.setText("No Change");
			playerBloodNoChange.setToolTipText("Playable Characters retain their original holy blood. Classes are limited to those that can support their original holy blood.");
			playerBloodNoChange.setEnabled(false);
			playerBloodNoChange.setSelection(true);
			playerBloodNoChange.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (playerBloodNoChange.getSelection()) {
						if (bossBloodShuffle.getSelection()) {
							bossBloodNoChange.setSelection(true);
							bossBloodShuffle.setSelection(false);
						}
					}
				}
			});
			
			optionData = new FormData();
			optionData.left = new FormAttachment(0, 0);
			optionData.top = new FormAttachment(0, 0);
			playerBloodNoChange.setLayoutData(optionData);
			
			playerBloodShuffle = new Button(bloodGroup, SWT.RADIO);
			playerBloodShuffle.setText("Shuffle");
			playerBloodShuffle.setToolTipText("Holy blood types are shuffled and assigned in a way such that relationships are preserved.\nUsing this option forces the corresponding setting on the boss blood.\nOnly available if bosses are allowed to be randomized.");
			playerBloodShuffle.setEnabled(false);
			playerBloodShuffle.setSelection(false);
			playerBloodShuffle.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (playerBloodShuffle.getSelection()) {
						bossBloodShuffle.setSelection(true);
						
						bossBloodNoChange.setSelection(false);
						bossBloodRandomize.setSelection(false);
					}
				}
			});
			
			optionData = new FormData();
			optionData.left = new FormAttachment(playerBloodNoChange, 0, SWT.LEFT);
			optionData.top = new FormAttachment(playerBloodNoChange, 5);
			playerBloodShuffle.setLayoutData(optionData);
			
			playerBloodRandomize = new Button(bloodGroup, SWT.RADIO);
			playerBloodRandomize.setText("Randomize");
			playerBloodRandomize.setToolTipText("Playable characters are allowed to randomize their blood. Children continue to inherit blood based on their parents.");
			playerBloodRandomize.setEnabled(false);
			playerBloodRandomize.setSelection(false);
			playerBloodRandomize.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (playerBloodRandomize.getSelection()) {
						if (bossBloodShuffle.getSelection()) {
							bossBloodRandomize.setSelection(true);
							bossBloodShuffle.setSelection(false);
						}
					}
				}
			});
			
			optionData = new FormData();
			optionData.left = new FormAttachment(playerBloodShuffle, 0, SWT.LEFT);
			optionData.top = new FormAttachment(playerBloodShuffle, 5);
			playerBloodRandomize.setLayoutData(optionData);
		}
		
		Group shopGroup = new Group(container, SWT.NONE);
		shopGroup.setText("Shop Options");
		
		FormLayout shopLayout = new FormLayout();
		shopLayout.marginLeft = 5;
		shopLayout.marginRight = 5;
		shopLayout.marginTop = 5;
		shopLayout.marginBottom = 5;
		shopGroup.setLayout(shopLayout);
		
		FormData shopData = new FormData();
		shopData.left = new FormAttachment(bloodGroup, 0, SWT.LEFT);
		shopData.top = new FormAttachment(bloodGroup, 5);
		shopData.right = new FormAttachment(100, -5);
		shopGroup.setLayoutData(shopData);
		
		{
			retainShops = new Button(shopGroup, SWT.RADIO);
			retainShops.setText("No Change");
			retainShops.setToolTipText("Retains shop items.");
			retainShops.setEnabled(false);
			retainShops.setSelection(true);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(0, 0);
			optionData.top = new FormAttachment(0, 0);
			retainShops.setLayoutData(optionData);
			
			adjustShops = new Button(shopGroup, SWT.RADIO);
			adjustShops.setText("Adjust to Party");
			adjustShops.setToolTipText("Changes shop items to reflect classes randomized for party.");
			adjustShops.setEnabled(false);
			adjustShops.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(retainShops, 0, SWT.LEFT);
			optionData.top = new FormAttachment(retainShops, 5);
			adjustShops.setLayoutData(optionData);
			
			randomizeShops = new Button(shopGroup, SWT.RADIO);
			randomizeShops.setText("Randomize");
			randomizeShops.setToolTipText("Randomize new shop items for every chapter.");
			randomizeShops.setEnabled(false);
			randomizeShops.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(adjustShops, 0, SWT.LEFT);
			optionData.top = new FormAttachment(adjustShops, 5);
			randomizeShops.setLayoutData(optionData);
		}
		
		adjustConvoItems = new Button(container, SWT.CHECK);
		adjustConvoItems.setText("Adjust Conversation Gifts");
		adjustConvoItems.setToolTipText("Updates the weapons received from conversations to weapons usable by the recipient.\n\nFor example, Lex/Chulainn normally give Ayra a Brave Sword. This option will change the Brave Sword to a weapon Ayra can use (assuming she can't use swords).");
		adjustConvoItems.setEnabled(false);
		adjustConvoItems.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(shopGroup, 0, SWT.LEFT);
		optionData.top = new FormAttachment(shopGroup, 5);
		adjustConvoItems.setLayoutData(optionData);
		
		adjustSTRMAG = new Button(container, SWT.CHECK);
		adjustSTRMAG.setText("Adjust STR/MAG Growths and Bases");
		adjustSTRMAG.setToolTipText("Swaps STR and MAG if a character randomizes to a class that uses the opposite attacking type.\n\nFor those that randomize from or into classes that use both, their growths and bases will not be altered.");
		adjustSTRMAG.setEnabled(false);
		adjustSTRMAG.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(adjustConvoItems, 0, SWT.LEFT);
		optionData.top = new FormAttachment(adjustConvoItems, 5);
		adjustSTRMAG.setLayoutData(optionData);
		
		Group itemAssignmentGroup = new Group(container, SWT.NONE);
		itemAssignmentGroup.setText("Weapon Assignment");
		
		FormLayout itemLayout = new FormLayout();
		itemLayout.marginLeft = 5;
		itemLayout.marginRight = 5;
		itemLayout.marginTop = 5;
		itemLayout.marginBottom = 5;
		itemAssignmentGroup.setLayout(itemLayout);
		
		FormData itemData = new FormData();
		itemData.left = new FormAttachment(adjustSTRMAG, 0, SWT.LEFT);
		itemData.top = new FormAttachment(adjustSTRMAG, 5);
		itemData.right = new FormAttachment(100, -5);
		itemAssignmentGroup.setLayoutData(itemData);
		
		{
			strictSidgradeItems = new Button(itemAssignmentGroup, SWT.RADIO);
			strictSidgradeItems.setText("Sidegrade (Strict)");
			strictSidgradeItems.setToolTipText("Assigns weapons using the direct analogue of the appropriate weapon type, where possible. Falls back to loose sidegrade if no matches are found.");
			strictSidgradeItems.setEnabled(false);
			strictSidgradeItems.setSelection(true);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(0, 0);
			optionData.top = new FormAttachment(0, 0);
			strictSidgradeItems.setLayoutData(optionData);
			
			looseSidegradeItems = new Button(itemAssignmentGroup, SWT.RADIO);
			looseSidegradeItems.setText("Sidegrade (Loose)");
			looseSidegradeItems.setToolTipText("Assigns weapons based on a more general normal weapon/special weapon split and have the same weapon rank.");
			looseSidegradeItems.setEnabled(false);
			looseSidegradeItems.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(strictSidgradeItems, 0, SWT.LEFT);
			optionData.top = new FormAttachment(strictSidgradeItems, 5);
			looseSidegradeItems.setLayoutData(optionData);
			
			randomItems = new Button(itemAssignmentGroup, SWT.RADIO);
			randomItems.setText("Randomize");
			randomItems.setToolTipText("Assigns weapons entirely randomly.");
			randomItems.setEnabled(false);
			randomItems.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(looseSidegradeItems, 0, SWT.LEFT);
			optionData.top = new FormAttachment(looseSidegradeItems, 5);
			randomItems.setLayoutData(optionData);
		}
		
		randomizeMinions = new Button(container, SWT.CHECK);
		randomizeMinions.setText("Randomize Regular Enemies");
		randomizeMinions.setToolTipText("Randomizes the classes for regular enemies. Due to how the game was coded and how many enemies are copy/pasted, randomizations are done in batches.");
		randomizeMinions.setEnabled(true);
		randomizeMinions.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(randomizePCs, 0, SWT.LEFT);
		optionData.top = new FormAttachment(itemAssignmentGroup, 10);
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
				bossBloodNoChange.setEnabled(randomizeBosses.getSelection());
				bossBloodShuffle.setEnabled(randomizeBosses.getSelection() && randomizePCs.getSelection());
				bossBloodRandomize.setEnabled(randomizeBosses.getSelection());
				
				if (!randomizeBosses.getSelection()) {
					if (bossBloodShuffle.getSelection() && playerBloodShuffle.getSelection()) {
						bossBloodShuffle.setSelection(false);
						playerBloodShuffle.setSelection(false);
						playerBloodNoChange.setSelection(true);
						bossBloodNoChange.setSelection(true);
					}
					playerBloodShuffle.setEnabled(false);
				} else {
					if (randomizePCs.getSelection()) {
						playerBloodShuffle.setEnabled(true);
					}
				}
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(randomizeArenas, 0, SWT.LEFT);
		optionData.top = new FormAttachment(randomizeArenas, 10);
		randomizeBosses.setLayoutData(optionData);
		
		Group bossBloodGroup = new Group(container, SWT.NONE);
		bossBloodGroup.setText("Boss Holy Blood");
		
		groupLayout = new FormLayout();
		groupLayout.marginLeft = 5;
		groupLayout.marginRight = 5;
		groupLayout.marginTop = 5;
		groupLayout.marginBottom = 5;
		bossBloodGroup.setLayout(groupLayout);
		
		groupData = new FormData();
		groupData.left = new FormAttachment(randomizeBosses, 0, SWT.LEFT);
		groupData.top = new FormAttachment(randomizeBosses, 5);
		groupData.right = new FormAttachment(100, -5);
		bossBloodGroup.setLayoutData(groupData);
		
		{
			bossBloodNoChange = new Button(bossBloodGroup, SWT.RADIO);
			bossBloodNoChange.setText("No Change");
			bossBloodNoChange.setToolTipText("Bosses retain the original holy blood. Class selection will be limited to those that support their original blood.");
			bossBloodNoChange.setEnabled(false);
			bossBloodNoChange.setSelection(true);
			bossBloodNoChange.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (bossBloodNoChange.getSelection()) {
						if (playerBloodShuffle.getSelection()) {
							playerBloodNoChange.setSelection(true);
							playerBloodShuffle.setSelection(false);
						}
					}
				}
			});
			
			optionData = new FormData();
			optionData.left = new FormAttachment(0, 0);
			optionData.top = new FormAttachment(0, 0);
			bossBloodNoChange.setLayoutData(optionData);
			
			bossBloodShuffle = new Button(bossBloodGroup, SWT.RADIO);
			bossBloodShuffle.setText("Shuffle");
			bossBloodShuffle.setToolTipText("Holy blood types are shuffled and assigned in a way such that relationships are preserved.\nUsing this option forces the corresponding setting on the player blood.\nOnly available if playable characters are allowed to be randomized.");
			bossBloodShuffle.setEnabled(false);
			bossBloodShuffle.setSelection(false);
			bossBloodShuffle.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (bossBloodShuffle.getSelection()) {
						playerBloodShuffle.setSelection(true);
						
						playerBloodNoChange.setSelection(false);
						playerBloodRandomize.setSelection(false);
					}
				}
			});
			
			optionData = new FormData();
			optionData.left = new FormAttachment(bossBloodNoChange, 0, SWT.LEFT);
			optionData.top = new FormAttachment(bossBloodNoChange, 5);
			bossBloodShuffle.setLayoutData(optionData);
			
			
			bossBloodRandomize = new Button(bossBloodGroup, SWT.RADIO);
			bossBloodRandomize.setText("Randomize");
			bossBloodRandomize.setToolTipText("Bosses with holy blood will randomize into a different holy blood, unlocking all classes for randomization.");
			bossBloodRandomize.setEnabled(false);
			bossBloodRandomize.setSelection(false);
			bossBloodRandomize.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (bossBloodRandomize.getSelection()) {
						if (playerBloodShuffle.getSelection()) {
							playerBloodRandomize.setSelection(true);
							playerBloodShuffle.setSelection(false);
						}
					}
				}
			});
			
			optionData = new FormData();
			optionData.left = new FormAttachment(bossBloodShuffle, 0, SWT.LEFT);
			optionData.top = new FormAttachment(bossBloodShuffle, 5);
			bossBloodRandomize.setLayoutData(optionData);
		}
	}
	
	public FE4ClassOptions getClassOptions() {
		ChildOptions childOptions = ChildOptions.MATCH_STRICT;
		if (adjustChildrenLoose.getSelection()) { childOptions = ChildOptions.MATCH_LOOSE; }
		else if (randomizeChildren.getSelection()) { childOptions = ChildOptions.RANDOM_CLASS; }
		
		ShopOptions shopOptions = ShopOptions.ADJUST_TO_MATCH;
		if (randomizeShops.getSelection()) { shopOptions = ShopOptions.RANDOMIZE; }
		
		ItemAssignmentOptions itemOptions = ItemAssignmentOptions.SIDEGRADE_STRICT;
		if (looseSidegradeItems.getSelection()) { itemOptions = ItemAssignmentOptions.SIDEGRADE_LOOSE; }
		else if (randomItems.getSelection()) { itemOptions = ItemAssignmentOptions.RANDOMIZE; }
		
		BloodOptions playerBloodOptions = BloodOptions.NO_CHANGE;
		BloodOptions bossBloodOptions = BloodOptions.NO_CHANGE;
		
		if (playerBloodShuffle.getSelection()) { playerBloodOptions = BloodOptions.SHUFFLE; }
		if (playerBloodRandomize.getSelection()) { playerBloodOptions = BloodOptions.RANDOMIZE; }
		
		if (bossBloodShuffle.getSelection()) { bossBloodOptions = BloodOptions.SHUFFLE; }
		if (bossBloodRandomize.getSelection()) { bossBloodOptions = BloodOptions.RANDOMIZE; }
		
		return new FE4ClassOptions(randomizePCs.getSelection(), includeLords.getSelection(), retainHealers.getSelection(), retainHorses.getSelection(), includeThieves.getSelection(), includeDancers.getSelection(), includeJulia.getSelection(), childOptions, playerBloodOptions, shopOptions, adjustConvoItems.getSelection(), adjustSTRMAG.getSelection(), itemOptions,
				randomizeMinions.getSelection(), randomizeArenas.getSelection(), randomizeBosses.getSelection(), bossBloodOptions);
	}
	
	public void setClassOptions(FE4ClassOptions options) {
		if (options == null) {
			// shouldn't happen.
		} else {
			if (options.randomizePlayableCharacters) {
				randomizePCs.setSelection(true);
				
				includeLords.setEnabled(true);
				retainHealers.setEnabled(true);
				retainHorses.setEnabled(true);
				includeThieves.setEnabled(true);
				includeDancers.setEnabled(true);
				includeJulia.setEnabled(true);
				
				adjustChildrenStrict.setEnabled(true);
				adjustChildrenLoose.setEnabled(true);
				randomizeChildren.setEnabled(true);
				
				playerBloodNoChange.setEnabled(true);
				playerBloodShuffle.setEnabled(options.randomizeBosses);
				playerBloodRandomize.setEnabled(true);
				
				retainShops.setEnabled(true);
				adjustShops.setEnabled(true);
				randomizeShops.setEnabled(true);
				
				adjustConvoItems.setEnabled(true);
				adjustSTRMAG.setEnabled(true);
				
				strictSidgradeItems.setEnabled(true);
				looseSidegradeItems.setEnabled(true);
				randomItems.setEnabled(true);
				
				includeLords.setSelection(options.includeLords);
				retainHealers.setSelection(options.retainHealers);
				retainHorses.setSelection(options.retainHorses);
				includeThieves.setSelection(options.includeThieves);
				includeDancers.setSelection(options.includeDancers);
				includeJulia.setSelection(options.includeJulia);
				
				adjustChildrenStrict.setSelection(options.childOption == ChildOptions.MATCH_STRICT);
				adjustChildrenLoose.setSelection(options.childOption == ChildOptions.MATCH_LOOSE);
				randomizeChildren.setSelection(options.childOption == ChildOptions.RANDOM_CLASS);
				
				playerBloodNoChange.setSelection(options.playerBloodOption == BloodOptions.NO_CHANGE || options.playerBloodOption == null);
				playerBloodShuffle.setSelection(options.playerBloodOption == BloodOptions.SHUFFLE);
				playerBloodRandomize.setSelection(options.playerBloodOption == BloodOptions.RANDOMIZE);
				
				retainShops.setSelection(options.shopOption == ShopOptions.DO_NOT_ADJUST);
				adjustShops.setSelection(options.shopOption == ShopOptions.ADJUST_TO_MATCH);
				randomizeShops.setSelection(options.shopOption == ShopOptions.RANDOMIZE);
				
				adjustConvoItems.setSelection(options.adjustConversationWeapons);
				adjustSTRMAG.setSelection(options.adjustSTRMAG);
				
				strictSidgradeItems.setSelection(options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT);
				looseSidegradeItems.setSelection(options.itemOptions == ItemAssignmentOptions.SIDEGRADE_LOOSE);
				randomItems.setSelection(options.itemOptions == ItemAssignmentOptions.RANDOMIZE);
			}
			
			randomizeMinions.setSelection(options.randomizeMinions);
			randomizeArenas.setSelection(options.randomizeArena);
			
			if (options.randomizeBosses) {
				randomizeBosses.setSelection(true);
				
				bossBloodNoChange.setEnabled(true);
				bossBloodShuffle.setEnabled(options.randomizePlayableCharacters);
				bossBloodRandomize.setEnabled(true);
				
				bossBloodNoChange.setSelection(options.bossBloodOption == BloodOptions.NO_CHANGE || options.bossBloodOption == null);
				bossBloodShuffle.setSelection(options.bossBloodOption == BloodOptions.SHUFFLE);
				bossBloodRandomize.setSelection(options.bossBloodOption == BloodOptions.RANDOMIZE);
			} else {
				
			}
		}
	}
}
