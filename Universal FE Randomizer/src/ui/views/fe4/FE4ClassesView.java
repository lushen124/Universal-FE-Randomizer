package ui.views.fe4;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import ui.common.GuiUtil;
import ui.common.YuneGroup;
import ui.model.fe4.FE4ClassOptions;
import ui.model.fe4.FE4ClassOptions.BloodOptions;
import ui.model.fe4.FE4ClassOptions.ChildOptions;
import ui.model.fe4.FE4ClassOptions.ItemAssignmentOptions;
import ui.model.fe4.FE4ClassOptions.ShopOptions;

public class FE4ClassesView extends Composite {
    protected Group container;

    protected EnemyOptionsGroup enemyGroup;
    protected ItemOptionsGroup itemGroup;
    protected PlayablesOptionsGroup playablesGroup;

    public FE4ClassesView(Composite parent) {
        super(parent, SWT.NONE);

        setLayout(new FillLayout());

        container = new Group(this, SWT.NONE);
        container.setText("Classes");
        container.setToolTipText("Randomize character classes and related options requiring flexible classes.");
        GridLayout gridLayout = GuiUtil.gridLayoutWithMargin();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        playablesGroup = new PlayablesOptionsGroup(this, container);
        GridData gridData = (GridData) GuiUtil.defaultGridData();
        gridData.verticalSpan = 2;
        playablesGroup.group.setLayoutData(gridData);
        itemGroup = new ItemOptionsGroup(this, container);
        itemGroup.group.setLayoutData(GuiUtil.defaultGridData());
        enemyGroup = new EnemyOptionsGroup(this, container);
        enemyGroup.group.setLayoutData(GuiUtil.defaultGridData());

    }

    public FE4ClassOptions getOptions() {
        ChildOptions childOptions = ChildOptions.MATCH_STRICT;
        if (playablesGroup.adjustChildrenLoose.getSelection()) {
            childOptions = ChildOptions.MATCH_LOOSE;
        } else if (playablesGroup.randomizeChildren.getSelection()) {
            childOptions = ChildOptions.RANDOM_CLASS;
        }

        ShopOptions shopOptions = ShopOptions.ADJUST_TO_MATCH;
        if (itemGroup.randomizeShops.getSelection()) {
            shopOptions = ShopOptions.RANDOMIZE;
        }

        ItemAssignmentOptions itemOptions = ItemAssignmentOptions.SIDEGRADE_STRICT;
        if (itemGroup.looseSidegradeItems.getSelection()) {
            itemOptions = ItemAssignmentOptions.SIDEGRADE_LOOSE;
        } else if (itemGroup.randomItems.getSelection()) {
            itemOptions = ItemAssignmentOptions.RANDOMIZE;
        }

        BloodOptions playerBloodOptions = BloodOptions.NO_CHANGE;
        BloodOptions bossBloodOptions = BloodOptions.NO_CHANGE;

        if (playablesGroup.playerBloodShuffle.getSelection()) {
            playerBloodOptions = BloodOptions.SHUFFLE;
        }
        if (playablesGroup.playerBloodRandomize.getSelection()) {
            playerBloodOptions = BloodOptions.RANDOMIZE;
        }

        if (enemyGroup.bossBloodShuffle.getSelection()) {
            bossBloodOptions = BloodOptions.SHUFFLE;
        }
        if (enemyGroup.bossBloodRandomize.getSelection()) {
            bossBloodOptions = BloodOptions.RANDOMIZE;
        }

        return new FE4ClassOptions(playablesGroup.randomizePCs.getSelection(), playablesGroup.includeLords.getSelection(), playablesGroup.retainHealers.getSelection(), playablesGroup.retainHorses.getSelection(), playablesGroup.includeThieves.getSelection(), playablesGroup.includeDancers.getSelection(), playablesGroup.includeJulia.getSelection(), playablesGroup.assignEvenly.getSelection(), childOptions, playerBloodOptions, shopOptions, itemGroup.adjustConvoItems.getSelection(), playablesGroup.adjustSTRMAG.getSelection(), itemOptions,
                enemyGroup.randomizeMinions.getSelection(), enemyGroup.randomizeArenas.getSelection(), enemyGroup.randomizeBosses.getSelection(), bossBloodOptions);
    }

    public void initialize(FE4ClassOptions options) {
        if (options == null) {
            // shouldn't happen.
        } else {
            if (options.randomizePlayableCharacters) {
                playablesGroup.randomizePCs.setSelection(true);
                playablesGroup.includeLords.setEnabled(true);
                playablesGroup.retainHealers.setEnabled(true);
                playablesGroup.retainHorses.setEnabled(true);
                playablesGroup.includeThieves.setEnabled(true);
                playablesGroup.includeDancers.setEnabled(true);
                playablesGroup.includeJulia.setEnabled(true);
                playablesGroup.assignEvenly.setEnabled(true);
                playablesGroup.adjustChildrenStrict.setEnabled(true);
                playablesGroup.adjustChildrenLoose.setEnabled(true);
                playablesGroup.randomizeChildren.setEnabled(true);
                playablesGroup.playerBloodNoChange.setEnabled(true);
                playablesGroup.playerBloodShuffle.setEnabled(options.randomizeBosses);
                playablesGroup.playerBloodRandomize.setEnabled(true);
                playablesGroup.adjustSTRMAG.setEnabled(true);

                itemGroup.retainShops.setEnabled(true);
                itemGroup.adjustShops.setEnabled(true);
                itemGroup.randomizeShops.setEnabled(true);
                itemGroup.adjustConvoItems.setEnabled(true);
                itemGroup.strictSidgradeItems.setEnabled(true);
                itemGroup.looseSidegradeItems.setEnabled(true);
                itemGroup.randomItems.setEnabled(true);

                playablesGroup.includeLords.setSelection(options.includeLords);
                playablesGroup.retainHealers.setSelection(options.retainHealers);
                playablesGroup.retainHorses.setSelection(options.retainHorses);
                playablesGroup.includeThieves.setSelection(options.includeThieves);
                playablesGroup.includeDancers.setSelection(options.includeDancers);
                playablesGroup.includeJulia.setSelection(options.includeJulia);
                playablesGroup.assignEvenly.setSelection(options.assignEvenly);

                playablesGroup.adjustChildrenStrict.setSelection(options.childOption == ChildOptions.MATCH_STRICT);
                playablesGroup.adjustChildrenLoose.setSelection(options.childOption == ChildOptions.MATCH_LOOSE);
                playablesGroup.randomizeChildren.setSelection(options.childOption == ChildOptions.RANDOM_CLASS);

                playablesGroup.playerBloodNoChange.setSelection(options.playerBloodOption == BloodOptions.NO_CHANGE || options.playerBloodOption == null);
                playablesGroup.playerBloodShuffle.setSelection(options.playerBloodOption == BloodOptions.SHUFFLE);
                playablesGroup.playerBloodRandomize.setSelection(options.playerBloodOption == BloodOptions.RANDOMIZE);

                playablesGroup.adjustSTRMAG.setSelection(options.adjustSTRMAG);
                itemGroup.retainShops.setSelection(options.shopOption == ShopOptions.DO_NOT_ADJUST);
                itemGroup.adjustShops.setSelection(options.shopOption == ShopOptions.ADJUST_TO_MATCH);
                itemGroup.randomizeShops.setSelection(options.shopOption == ShopOptions.RANDOMIZE);
                itemGroup.adjustConvoItems.setSelection(options.adjustConversationWeapons);
                itemGroup.strictSidgradeItems.setSelection(options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT);
                itemGroup.looseSidegradeItems.setSelection(options.itemOptions == ItemAssignmentOptions.SIDEGRADE_LOOSE);
                itemGroup.randomItems.setSelection(options.itemOptions == ItemAssignmentOptions.RANDOMIZE);
            }

            enemyGroup.randomizeMinions.setSelection(options.randomizeMinions);
            enemyGroup.randomizeArenas.setSelection(options.randomizeArena);

            if (options.randomizeBosses) {
                enemyGroup.randomizeBosses.setSelection(true);

                enemyGroup.bossBloodNoChange.setEnabled(true);
                enemyGroup.bossBloodShuffle.setEnabled(options.randomizePlayableCharacters);
                enemyGroup.bossBloodRandomize.setEnabled(true);

                enemyGroup.bossBloodNoChange.setSelection(options.bossBloodOption == BloodOptions.NO_CHANGE || options.bossBloodOption == null);
                enemyGroup.bossBloodShuffle.setSelection(options.bossBloodOption == BloodOptions.SHUFFLE);
                enemyGroup.bossBloodRandomize.setSelection(options.bossBloodOption == BloodOptions.RANDOMIZE);
            }
        }
    }
}

class EnemyOptionsGroup extends YuneGroup {

    private FE4ClassesView mainView;

    public Button randomizeMinions;

    public Button randomizeArenas;

    public Button randomizeBosses;

    public Button bossBloodNoChange;
    public Button bossBloodShuffle;
    public Button bossBloodRandomize;

    public EnemyOptionsGroup(FE4ClassesView parent, Composite container) {
        super(container);
        mainView = parent;
    }


    @Override
    protected void compose() {

        randomizeMinions = new Button(group, SWT.CHECK);
        randomizeMinions.setText("Randomize Regular Enemies");
        randomizeMinions.setToolTipText("Randomizes the classes for regular enemies. Due to how the game was coded and how many enemies are copy/pasted, randomizations are done in batches.");
        randomizeMinions.setEnabled(true);
        randomizeMinions.setSelection(false);

        FormData optionData = new FormData();
        optionData.left = new FormAttachment(0, 0);
        optionData.top = new FormAttachment(0, 0);
        optionData.right = new FormAttachment(100, -5);
        randomizeMinions.setLayoutData(optionData);

        randomizeArenas = new Button(group, SWT.CHECK);
        randomizeArenas.setText("Randomize Arena Enemies");
        randomizeArenas.setToolTipText("Randomizes the classes of enemies found in the arena.");
        randomizeArenas.setEnabled(true);
        randomizeArenas.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(randomizeMinions, 0, SWT.LEFT);
        optionData.top = new FormAttachment(randomizeMinions, 10);
        randomizeArenas.setLayoutData(optionData);

        randomizeBosses = new Button(group, SWT.CHECK);
        randomizeBosses.setText("Randomize Bosses");
        randomizeBosses.setToolTipText("Randomizes the classes of all bosses (all enemy characters with faces and names).");
        randomizeBosses.setEnabled(true);
        randomizeBosses.setSelection(false);
        randomizeBosses.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                bossBloodNoChange.setEnabled(randomizeBosses.getSelection());
                bossBloodShuffle.setEnabled(randomizeBosses.getSelection() && mainView.playablesGroup.randomizePCs.getSelection());
                bossBloodRandomize.setEnabled(randomizeBosses.getSelection());

                if (!randomizeBosses.getSelection()) {
                    if (bossBloodShuffle.getSelection() && mainView.playablesGroup.playerBloodShuffle.getSelection()) {
                        bossBloodShuffle.setSelection(false);
                        mainView.playablesGroup.playerBloodShuffle.setSelection(false);
                        mainView.playablesGroup.playerBloodNoChange.setSelection(true);
                        bossBloodNoChange.setSelection(true);
                    }
                    mainView.playablesGroup.playerBloodShuffle.setEnabled(false);
                } else {
                    if (mainView.playablesGroup.randomizePCs.getSelection()) {
                        mainView.playablesGroup.playerBloodShuffle.setEnabled(true);
                    }
                }
            }
        });

        optionData = new FormData();
        optionData.left = new FormAttachment(randomizeArenas, 0, SWT.LEFT);
        optionData.top = new FormAttachment(randomizeArenas, 10);
        randomizeBosses.setLayoutData(optionData);

        Group bossBloodGroup = new Group(group, SWT.NONE);
        bossBloodGroup.setText("Boss Holy Blood");
        bossBloodGroup.setLayout(GuiUtil.formLayoutWithMargin());

        FormData groupData = new FormData();
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
                        if (mainView.playablesGroup.playerBloodShuffle.getSelection()) {
                            mainView.playablesGroup.playerBloodNoChange.setSelection(true);
                            mainView.playablesGroup.playerBloodShuffle.setSelection(false);
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
                        mainView.playablesGroup.playerBloodShuffle.setSelection(true);
                        mainView.playablesGroup.playerBloodNoChange.setSelection(false);
                        mainView.playablesGroup.playerBloodRandomize.setSelection(false);
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
                        if (mainView.playablesGroup.playerBloodShuffle.getSelection()) {
                            mainView.playablesGroup.playerBloodRandomize.setSelection(true);
                            mainView.playablesGroup.playerBloodShuffle.setSelection(false);
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

    @Override
    protected String getGroupTitle() {
        return "Enemies";
    }
}

class ItemOptionsGroup extends YuneGroup {

    private FE4ClassesView mainView;

    public Button retainShops;
    public Button adjustShops;
    public Button randomizeShops;
    public Button adjustConvoItems;

    public Button strictSidgradeItems;
    public Button looseSidegradeItems;
    public Button randomItems;

    public ItemOptionsGroup(FE4ClassesView parent, Composite container) {
        super(container);
        mainView = parent;
    }

    @Override
    protected void compose() {
        Group shopGroup = new Group(group, SWT.NONE);
        shopGroup.setText("Shop Options");
        shopGroup.setLayout(GuiUtil.formLayoutWithMargin());

        FormData optionData = new FormData();
        optionData.left = new FormAttachment(0, 0);
        optionData.top = new FormAttachment(0, 0);
        optionData.right = new FormAttachment(100, -5);
        shopGroup.setLayoutData(optionData);

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

        adjustConvoItems = new Button(group, SWT.CHECK);
        adjustConvoItems.setText("Adjust Conversation Gifts");
        adjustConvoItems.setToolTipText("Updates the weapons received from conversations to weapons usable by the recipient.\n\nFor example, Lex/Chulainn normally give Ayra a Brave Sword. This option will change the Brave Sword to a weapon Ayra can use (assuming she can't use swords).");
        adjustConvoItems.setEnabled(false);
        adjustConvoItems.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(shopGroup, 0, SWT.LEFT);
        optionData.top = new FormAttachment(shopGroup, 5);
        adjustConvoItems.setLayoutData(optionData);

        Group itemAssignmentGroup = new Group(group, SWT.NONE);
        itemAssignmentGroup.setText("Weapon Assignment");
        itemAssignmentGroup.setLayout(GuiUtil.formLayoutWithMargin());

        FormData itemData = new FormData();
        itemData.left = new FormAttachment(adjustConvoItems, 0, SWT.LEFT);
        itemData.top = new FormAttachment(adjustConvoItems, 5);
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
    }

    @Override
    protected String getGroupTitle() {
        return "Items";
    }
}

class PlayablesOptionsGroup extends YuneGroup {

    private FE4ClassesView mainView;

    protected Button randomizePCs;
    protected Button includeLords;
    protected Button retainHealers;
    protected Button retainHorses;
    protected Button includeThieves;
    protected Button includeDancers;
    protected Button includeJulia;
    protected Button assignEvenly;

    protected Button adjustChildrenStrict;
    protected Button adjustChildrenLoose;
    protected Button randomizeChildren;

    protected Button playerBloodNoChange;
    protected Button playerBloodShuffle;
    protected Button playerBloodRandomize;
    protected Button adjustSTRMAG;

    public PlayablesOptionsGroup(FE4ClassesView parent, Composite container) {
        super(container);
        mainView = parent;
    }


    @Override
    protected void compose() {
        randomizePCs = new Button(group, SWT.CHECK);
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
                assignEvenly.setEnabled(enabled);

                adjustChildrenStrict.setEnabled(enabled);
                adjustChildrenLoose.setEnabled(enabled);
                randomizeChildren.setEnabled(enabled);

                playerBloodNoChange.setEnabled(enabled);
                playerBloodShuffle.setEnabled(enabled && mainView.enemyGroup.randomizeBosses.getSelection());
                playerBloodRandomize.setEnabled(enabled);
                adjustSTRMAG.setEnabled(enabled);

                mainView.itemGroup.retainShops.setEnabled(enabled);
                mainView.itemGroup.adjustShops.setEnabled(enabled);
                mainView.itemGroup.randomizeShops.setEnabled(enabled);
                mainView.itemGroup.adjustConvoItems.setEnabled(enabled);
                mainView.itemGroup.strictSidgradeItems.setEnabled(enabled);
                mainView.itemGroup.looseSidegradeItems.setEnabled(enabled);
                mainView.itemGroup.randomItems.setEnabled(enabled);

                if (!enabled) {
                    if (playerBloodShuffle.getSelection() && mainView.enemyGroup.bossBloodShuffle.getSelection()) {
                        mainView.enemyGroup.bossBloodShuffle.setSelection(false);
                        playerBloodShuffle.setSelection(false);
                        mainView.enemyGroup.bossBloodNoChange.setSelection(true);
                        playerBloodNoChange.setSelection(true);
                    }
                    mainView.enemyGroup.bossBloodShuffle.setEnabled(false);
                } else {
                    if (mainView.enemyGroup.randomizeBosses.getSelection()) {
                        mainView.enemyGroup.bossBloodShuffle.setEnabled(true);
                    }
                }
            }
        });

        FormData optionData = new FormData();
        optionData.left = new FormAttachment(0, 0);
        optionData.top = new FormAttachment(0, 0);
        randomizePCs.setLayoutData(optionData);

        includeLords = new Button(group, SWT.CHECK);
        includeLords.setText("Include Lords");
        includeLords.setToolTipText("Include Sigurd and Seliph for randomization and adds Junior Lord and Lord Knight to the class pool.");
        includeLords.setEnabled(false);
        includeLords.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(randomizePCs, 10, SWT.LEFT);
        optionData.top = new FormAttachment(randomizePCs, 5);
        includeLords.setLayoutData(optionData);

        includeThieves = new Button(group, SWT.CHECK);
        includeThieves.setText("Include Thieves");
        includeThieves.setToolTipText("Include Dew, Patty, and Daisy for randomization and adds Thief and Thief Fighter to the class pool.");
        includeThieves.setEnabled(false);
        includeThieves.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(includeLords, 0, SWT.LEFT);
        optionData.top = new FormAttachment(includeLords, 5);
        includeThieves.setLayoutData(optionData);

        includeDancers = new Button(group, SWT.CHECK);
        includeDancers.setText("Include Dancers");
        includeDancers.setToolTipText("Includes Silvia, Lene, and Laylea for randomization and adds Dancer to the class pool (limit 1 per generation).\n\nNote: This will break Silvia's village event in Chapter 4 if enabled.");
        includeDancers.setEnabled(false);
        includeDancers.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(includeThieves, 0, SWT.LEFT);
        optionData.top = new FormAttachment(includeThieves, 5);
        includeDancers.setLayoutData(optionData);

        includeJulia = new Button(group, SWT.CHECK);
        includeJulia.setText("Include Julia");
        includeJulia.setToolTipText("Allows Julia to be randomized. Removes the guarantee of having Naga for endgame.");
        includeJulia.setEnabled(false);
        includeJulia.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(includeDancers, 0, SWT.LEFT);
        optionData.top = new FormAttachment(includeDancers, 5);
        includeJulia.setLayoutData(optionData);

        retainHealers = new Button(group, SWT.CHECK);
        retainHealers.setText("Retain Healers");
        retainHealers.setToolTipText("Ensures Edain, Claud, Lana, Muirne, Coirpre, and Charlot can all still use staves.");
        retainHealers.setEnabled(false);
        retainHealers.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(includeJulia, 0, SWT.LEFT);
        optionData.top = new FormAttachment(includeJulia, 5);
        retainHealers.setLayoutData(optionData);

        retainHorses = new Button(group, SWT.CHECK);
        retainHorses.setText("Retain Horseback Units");
        retainHorses.setToolTipText("Limits normally horseback units to other horseback classes.");
        retainHorses.setEnabled(false);
        retainHorses.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(retainHealers, 0, SWT.LEFT);
        optionData.top = new FormAttachment(retainHealers, 5);
        retainHorses.setLayoutData(optionData);

        assignEvenly = new Button(group, SWT.CHECK);
        assignEvenly.setText("Assign Classes Evenly");
        assignEvenly.setToolTipText("Attempts to avoid duplicate class assignments where possible.\n\nEach generation will have its own class pool.");
        assignEvenly.setEnabled(false);
        assignEvenly.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(retainHorses, 0, SWT.LEFT);
        optionData.top = new FormAttachment(retainHorses, 5);
        assignEvenly.setLayoutData(optionData);

        Group childGroup = new Group(group, SWT.NONE);
        childGroup.setText("Children Options");
        childGroup.setLayout(GuiUtil.formLayoutWithMargin());

        FormData groupData = new FormData();
        groupData.left = new FormAttachment(assignEvenly, 0, SWT.LEFT);
        groupData.top = new FormAttachment(assignEvenly, 5);
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

        Group bloodGroup = new Group(group, SWT.NONE);
        bloodGroup.setText("Holy Blood");
        bloodGroup.setLayout(GuiUtil.formLayoutWithMargin());

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
                        if (mainView.enemyGroup.bossBloodShuffle.getSelection()) {
                            mainView.enemyGroup.bossBloodNoChange.setSelection(true);
                            mainView.enemyGroup.bossBloodShuffle.setSelection(false);
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
                        mainView.enemyGroup.bossBloodShuffle.setSelection(true);
                        mainView.enemyGroup.bossBloodNoChange.setSelection(false);
                        mainView.enemyGroup.bossBloodRandomize.setSelection(false);
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
                        if (mainView.enemyGroup.bossBloodShuffle.getSelection()) {
                            mainView.enemyGroup.bossBloodRandomize.setSelection(true);
                            mainView.enemyGroup.bossBloodShuffle.setSelection(false);
                        }
                    }
                }
            });

            optionData = new FormData();
            optionData.left = new FormAttachment(playerBloodShuffle, 0, SWT.LEFT);
            optionData.top = new FormAttachment(playerBloodShuffle, 5);
            playerBloodRandomize.setLayoutData(optionData);
        }

        adjustSTRMAG = new Button(group, SWT.CHECK);
        adjustSTRMAG.setText("Adjust STR/MAG Growths and Bases");
        adjustSTRMAG.setToolTipText("Swaps STR and MAG if a character randomizes to a class that uses the opposite attacking type.\n\nFor those that randomize from or into classes that use both, their growths and bases will not be altered.");
        adjustSTRMAG.setEnabled(false);
        adjustSTRMAG.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(bloodGroup, 0, SWT.LEFT);
        optionData.top = new FormAttachment(bloodGroup, 5);
        adjustSTRMAG.setLayoutData(optionData);
    }

}

