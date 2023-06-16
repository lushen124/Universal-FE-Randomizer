package ui.views;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import ui.common.GuiUtil;
import ui.model.CharacterShufflingOptions;
import ui.model.CharacterShufflingOptions.ShuffleLevelingMode;

import java.util.ArrayList;
import java.util.List;

public class CharacterShufflingView extends YuneView<CharacterShufflingOptions> {
	
	private Button enableButton;
	private Label shuffleChanceLabel;
	private Spinner shuffleChanceSpinner;
	private Button changeDescriptionButton;
	
	private Group modeContainer;
	private Button unchangedButton;
	private Button autoLevelingButton;
	

	private Group includedFilesContainer;
	private Button includeFE6Button;
	private Button includeFE7Button;
	private Button includeFE8Button;
	private Label includedFilesLabel;
	private Button selectFilesButton;
	private FileDialog fileDialog;
	private List<String> includedShuffles = new ArrayList<>();
	

	
	
	public CharacterShufflingView(Composite parent, GameType type) {
		super(parent, type);
	}

	@Override
	public String getGroupTitle() {
		return "Character Shuffling";
	}

	@Override
	public String getGroupTooltip() {
		return "Shuffle in characters from other games.";
	}

	@Override
	protected void compose() {
		enableButton = new Button(group, SWT.CHECK);
		enableButton.setText("Character Shuffling");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				boolean newState = enableButton.getSelection();
				autoLevelingButton.setEnabled(newState);
				
				modeContainer.setEnabled(newState);
				unchangedButton.setEnabled(newState);
				shuffleChanceSpinner.setEnabled(newState);
				
				
				includedFilesContainer.setEnabled(newState);
				includeFE6Button.setEnabled(newState && !type.equals(GameType.FE6));
				includeFE7Button.setEnabled(newState && !type.equals(GameType.FE7));
				includeFE8Button.setEnabled(newState && !type.equals(GameType.FE8));
				selectFilesButton.setEnabled(newState);
			}
		});
		
		FormData enableData = new FormData();
		enableData.left = new FormAttachment(0, 0);
		enableData.top = new FormAttachment(0, 0);
		enableButton.setLayoutData(enableData);
		
		///////////////////////////////////////////
		
		shuffleChanceLabel = new Label(group, SWT.NONE);
		shuffleChanceLabel.setText("Chance:");
		shuffleChanceLabel.setToolTipText("The chance for each player character to get replaced by one of the shuffled ones.:");
		
		shuffleChanceSpinner = new Spinner(group, SWT.NONE);
		shuffleChanceSpinner.setToolTipText("The chance for each player character to get replaced by one of the shuffled ones.");
		shuffleChanceSpinner.setEnabled(false);
		shuffleChanceSpinner.setValues(25, 1, 100, 0, 1, 5);

		
		FormData shuffleChanceLabelData = new FormData();
		shuffleChanceLabelData.right = new FormAttachment(shuffleChanceSpinner, -5);
		shuffleChanceLabelData.top = new FormAttachment(shuffleChanceSpinner, 0, SWT.CENTER);
		shuffleChanceLabel.setLayoutData(shuffleChanceLabelData);
		
		FormData shuffleChanceSpinnerData = new FormData();
		shuffleChanceSpinnerData.top = new FormAttachment(enableButton, 5);
		shuffleChanceSpinnerData.right = new FormAttachment(100, -10);
		shuffleChanceSpinner.setLayoutData(shuffleChanceSpinnerData);
		
		changeDescriptionButton = new Button(group, SWT.CHECK);
		changeDescriptionButton.setText("Change Descriptions?");
		changeDescriptionButton.setToolTipText("If the Description of the replaced Character should be overwritten. Leaving this off might reduce confusion of which character is which.");
		FormData changeDescriptionButtonData = new FormData();
		changeDescriptionButtonData.left = new FormAttachment(enableButton, 5, SWT.LEFT);
		changeDescriptionButtonData.top = new FormAttachment(shuffleChanceSpinner, 0);
		changeDescriptionButton.setLayoutData(changeDescriptionButtonData);
		
		modeContainer = new Group(group, SWT.NONE);
		modeContainer.setText("Leveling Mode");
		modeContainer.setToolTipText("Determines how the Level of the shuffled in character will be changed.");
		modeContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		FormData groupData = new FormData();
		groupData.left = new FormAttachment(changeDescriptionButton, 0, SWT.LEFT);
		groupData.top = new FormAttachment(changeDescriptionButton, 5);
		groupData.right = new FormAttachment(100, -5);
		modeContainer.setLayoutData(groupData);
		
		
		autoLevelingButton = new Button(modeContainer, SWT.RADIO);
		autoLevelingButton.setText("Autolevel");
		autoLevelingButton.setToolTipText("Autolevel the character to the level of the character they replace.");
		autoLevelingButton.setEnabled(false);
		autoLevelingButton.setSelection(true);
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		optionData.right = new FormAttachment(100, -5);
		autoLevelingButton.setLayoutData(optionData);

		
		unchangedButton = new Button(modeContainer, SWT.RADIO);
		unchangedButton.setText("Keep unchanged");
		unchangedButton.setToolTipText("Character will be shuffled in as is.");
		unchangedButton.setEnabled(false);
		unchangedButton.setSelection(false);
		optionData = new FormData();
		optionData.left = new FormAttachment(autoLevelingButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(autoLevelingButton, 5);
		unchangedButton.setLayoutData(optionData);
		
		
		includedFilesContainer = new Group(group, SWT.NONE);
		includedFilesContainer.setText("Included Shuffles");
		includedFilesContainer.setToolTipText("Determines which configured Character swill be included for the shuffling.");
		includedFilesContainer.setLayout(GuiUtil.formLayoutWithMargin());
		
		FormData includedFilesContainerData = new FormData();
		includedFilesContainerData.left = new FormAttachment(modeContainer, 0, SWT.LEFT);
		includedFilesContainerData.top = new FormAttachment(modeContainer, 0);
		includedFilesContainerData.right = new FormAttachment(100, -5);
		includedFilesContainer.setLayoutData(includedFilesContainerData);
		
		includeFE6Button = new Button(includedFilesContainer, SWT.CHECK);
		includeFE6Button.setText("Include FE6");
		includeFE6Button.setEnabled(false);
		includeFE6Button.setSelection(false);
		optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		optionData.right = new FormAttachment(100, -5);
		includeFE6Button.setLayoutData(optionData);
		
		
		includeFE7Button = new Button(includedFilesContainer, SWT.CHECK);
		includeFE7Button.setText("Include FE7");
		includeFE7Button.setEnabled(false);
		includeFE7Button.setSelection(false);
		optionData = new FormData();
		optionData.left = new FormAttachment(includeFE6Button, 0, SWT.LEFT);
		optionData.top = new FormAttachment(includeFE6Button, 5);
		includeFE7Button.setLayoutData(optionData);
		
		includeFE8Button = new Button(includedFilesContainer, SWT.CHECK);
		includeFE8Button.setText("Include FE8");
		includeFE8Button.setEnabled(false);
		includeFE8Button.setSelection(false);
		optionData = new FormData();
		optionData.left = new FormAttachment(includeFE7Button, 0, SWT.LEFT);
		optionData.top = new FormAttachment(includeFE7Button, 5);
		includeFE8Button.setLayoutData(optionData);
		
		Composite shell = group;
		while(!(shell instanceof Shell)) {
			shell = shell.getParent();
		}

		fileDialog = new FileDialog((Shell) shell, SWT.MULTI);
		
		
		selectFilesButton = new Button(includedFilesContainer, SWT.PUSH);
		selectFilesButton.setText("Select configuration files to include");
		selectFilesButton.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				fileDialog.setFilterExtensions(new String[]{"*.json"});
				fileDialog.setFilterNames(new String[] {"JSON Files"});
				// Reset the already inclauded shuffles whenever we enter the dialog again, 
				// this limits us to only selecting files from one folder, but atleast we don't have to add a proper way to remove ones from here...
				includedShuffles = new ArrayList<>();
				// Open file dialog
				String selectedFiles = fileDialog.open();
				
				// If user selected something
				if(selectedFiles != null) {
					String[] chosenFiles = fileDialog.getFileNames();
					String currentFolder = fileDialog.getFilterPath();
					StringBuilder sb = new StringBuilder();
					for (String file : chosenFiles) {
						includedShuffles.add(currentFolder + "/"+ file);
						if (!sb.isEmpty()){
							sb.append(",");
						}
						sb.append(file);
					}
					includedFilesLabel.setText(sb.toString());
					includedFilesLabel.getParent().layout();
				}
			}
		});
		optionData = new FormData();
		optionData.left = new FormAttachment(includeFE8Button, 0, SWT.LEFT);
		optionData.top = new FormAttachment(includeFE8Button, 5);
		selectFilesButton.setLayoutData(optionData);
		
		
		includedFilesLabel = new Label(includedFilesContainer, SWT.NONE);
		includedFilesLabel.setToolTipText("The Names of all the custom files that are included");
		includedFilesLabel.setText("");
		optionData = new FormData();
		optionData.left = new FormAttachment(selectFilesButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(selectFilesButton, 5);
		includedFilesLabel.setLayoutData(optionData);

		
		
	}

	@Override
	public CharacterShufflingOptions getOptions() {
		boolean isEnabled = enableButton.getSelection();
		ShuffleLevelingMode levelingMode = autoLevelingButton.getSelection() ? ShuffleLevelingMode.AUTOLEVEL : ShuffleLevelingMode.UNCHANGED;
		int chance = shuffleChanceSpinner.getSelection();
		List<String> shuffles = new ArrayList<>(includedShuffles);
		if (includeFE6Button.getSelection()) {
			shuffles.add("fe6chars.json");
		}
		if (includeFE7Button.getSelection()) {
			shuffles.add("fe7chars.json");
		}
		if (includeFE8Button.getSelection()) {
			shuffles.add("fe8chars.json");
		}
		
		
		return new CharacterShufflingOptions(levelingMode, isEnabled, chance, shuffles, isEnabled);
	}

	@Override
	public void initialize(CharacterShufflingOptions options) {
		if (options == null) {
			enableButton.setSelection(false);
			modeContainer.setEnabled(false);
			autoLevelingButton.setEnabled(false);
			unchangedButton.setEnabled(false);
			shuffleChanceSpinner.setEnabled(false);
			includedFilesContainer.setEnabled(false);
			includeFE6Button.setEnabled(false);
			includeFE7Button.setEnabled(false);
			includeFE8Button.setEnabled(false);
			selectFilesButton.setEnabled(false);
		} else {
			enableButton.setSelection(true);
			
			modeContainer.setEnabled(true);
			
			autoLevelingButton.setEnabled(true);
			autoLevelingButton.setSelection(ShuffleLevelingMode.AUTOLEVEL.equals(options.getLevelingMode()));
			
			unchangedButton.setEnabled(true);
			unchangedButton.setSelection(ShuffleLevelingMode.UNCHANGED.equals(options.getLevelingMode()));

			shuffleChanceSpinner.setEnabled(true);
			shuffleChanceSpinner.setSelection(options.getChance());
			
			includeFE6Button.setEnabled(!GameType.FE6.equals(type));
			includeFE6Button.setSelection(!GameType.FE6.equals(type) && options.getIncludedShuffles().contains("fe6chars.json"));
			
			includeFE7Button.setEnabled(!GameType.FE7.equals(type));
			includeFE7Button.setSelection(!GameType.FE7.equals(type) && options.getIncludedShuffles().contains("fe7chars.json"));
			
			includeFE8Button.setEnabled(!GameType.FE8.equals(type));
			includeFE8Button.setSelection(!GameType.FE8.equals(type) && options.getIncludedShuffles().contains("fe8chars.json"));
		}
	}
}
