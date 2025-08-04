package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import ui.common.GuiUtil;
import ui.general.MinMaxControl;
import ui.model.GrowthOptions;
import ui.model.MinMaxOption;
import ui.model.MinMaxVarOption;


public class GrowthsView extends YuneView<GrowthOptions> {

	private boolean hasSTRMAGSplit;
	private boolean supportsSmartRandomization;

	private Boolean isEnabled = false;
	private GrowthOptions.Mode currentMode = GrowthOptions.Mode.REDISTRIBUTE;


	private Button enableButton;

	private Group modeContainer;

	private Button redistributeOption;
	private Spinner varianceSpinner;

	private Button byDeltaOption;
	private Spinner deltaSpinner;

	private Button fullRandomOption;
	private MinMaxControl growthRangeControl;
	
	private Button smartOption;

	private Button adjustHPGrowths;
	private Button adjustSTRMAGSplit;

	public GrowthsView(Composite parent, boolean hasSTRMAGSplit, boolean supportsSmartRandom) {
		super();
		createGroup(parent);
		this.hasSTRMAGSplit = hasSTRMAGSplit;
		supportsSmartRandomization = supportsSmartRandom;
		compose();
	}


	@Override
	public String getGroupTitle() {
		return "Growths";
	}

	@Override
	public String getGroupTooltip() {
		return "Randomizes the growths of all playable characters.";
	}

	@Override
	protected void compose() {
		enableButton = new Button(group, SWT.CHECK);
		enableButton.setText("Enable Growths Randomization");
		enableButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setEnableGrowths(enableButton.getSelection());
			}
		});

		growthRangeControl = new MinMaxControl(group, SWT.NONE, "Min Growth:", "Max Growth:");
		growthRangeControl.getMinSpinner().setValues(5, 0, 255, 0, 1, 5);
		growthRangeControl.getMaxSpinner().setValues(80, 0, 255, 0, 1, 5);
		growthRangeControl.setEnabled(false);

		FormData rangeData = new FormData();
		rangeData.top = new FormAttachment(enableButton, 5);
		rangeData.left = new FormAttachment(0, 5);
		rangeData.right = new FormAttachment(100, -5);
		growthRangeControl.setLayoutData(rangeData);

		modeContainer = new Group(group, SWT.NONE);
		modeContainer.setText("Mode");
		modeContainer.setLayout(GuiUtil.formLayoutWithMargin());

		FormData modeData = new FormData();
		modeData.left = new FormAttachment(enableButton, 5, SWT.LEFT);
		modeData.top = new FormAttachment(growthRangeControl, 10);
		modeData.right = new FormAttachment(100, -5);
		modeContainer.setLayoutData(modeData);

		/////////////////////////////////////////////////////////////

		redistributeOption = new Button(modeContainer, SWT.RADIO);
		redistributeOption.setText("Redistribute");
		redistributeOption.setToolTipText("Randomly redistrubtes a character's total growths.");
		redistributeOption.setEnabled(false);
		redistributeOption.setSelection(true);
		redistributeOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(GrowthOptions.Mode.REDISTRIBUTE);
			}
		});

		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		redistributeOption.setLayoutData(optionData);

		Composite redistParamContainer = new Composite(modeContainer, 0);
		redistParamContainer.setLayout(GuiUtil.formLayoutWithMargin());

		Label redistParamLabel = new Label(redistParamContainer, SWT.RIGHT);
		redistParamLabel.setText("Growth Variance:");

		varianceSpinner = new Spinner(redistParamContainer, SWT.NONE);
		varianceSpinner.setValues(30, 0, 255, 0, 1, 5);
		varianceSpinner.setEnabled(false);

		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(varianceSpinner, -5);
		labelData.top = new FormAttachment(varianceSpinner, 0, SWT.CENTER);
		redistParamLabel.setLayoutData(labelData);

		FormData spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		varianceSpinner.setLayoutData(spinnerData);

		FormData paramContainerData = new FormData();
		paramContainerData.top = new FormAttachment(redistributeOption, 0);
		paramContainerData.left = new FormAttachment(redistributeOption, 0, SWT.LEFT);
		paramContainerData.right = new FormAttachment(100, -5);
		redistParamContainer.setLayoutData(paramContainerData);

		/////////////////////////////////////////////////////////////

		byDeltaOption = new Button(modeContainer, SWT.RADIO);
		byDeltaOption.setText("Randomize Delta");
		byDeltaOption.setToolTipText("Applies a random delta between +X and -X to all growth areas.");
		byDeltaOption.setEnabled(false);
		byDeltaOption.setSelection(false);
		byDeltaOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(GrowthOptions.Mode.DELTA);
			}
		});

		optionData = new FormData();
		optionData.left = new FormAttachment(redistParamContainer, 0, SWT.LEFT);
		optionData.top = new FormAttachment(redistParamContainer, 0);
		byDeltaOption.setLayoutData(optionData);

		Composite deltaParamContainer = new Composite(modeContainer, 0);
		deltaParamContainer.setLayout(GuiUtil.formLayoutWithMargin());

		Label deltaParamLabel = new Label(deltaParamContainer, SWT.RIGHT);
		deltaParamLabel.setText("Max Delta:");

		deltaSpinner = new Spinner(deltaParamContainer, SWT.NONE);
		deltaSpinner.setValues(20, 0, 255, 0, 1, 5);
		deltaSpinner.setEnabled(false);

		labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(deltaSpinner, -5);
		labelData.top = new FormAttachment(deltaSpinner, 0, SWT.CENTER);
		deltaParamLabel.setLayoutData(labelData);

		spinnerData = new FormData();
		spinnerData.right = new FormAttachment(100, -5);
		deltaSpinner.setLayoutData(spinnerData);

		paramContainerData = new FormData();
		paramContainerData.top = new FormAttachment(byDeltaOption, 0);
		paramContainerData.left = new FormAttachment(byDeltaOption, 0, SWT.LEFT);
		paramContainerData.right = new FormAttachment(100, -5);
		deltaParamContainer.setLayoutData(paramContainerData);

		/////////////////////////////////////////////////////////////

		fullRandomOption = new Button(modeContainer, SWT.RADIO);
		fullRandomOption.setText("Randomize Absolute");
		fullRandomOption.setToolTipText("Generates fully random growth rates between the specified minimum and maximum.");
		fullRandomOption.setEnabled(false);
		fullRandomOption.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(GrowthOptions.Mode.FULL);
			}
		});

		optionData = new FormData();
		optionData.left = new FormAttachment(deltaParamContainer, 0, SWT.LEFT);
		optionData.top = new FormAttachment(deltaParamContainer, 0);
		fullRandomOption.setLayoutData(optionData);
		
		if (supportsSmartRandomization) {
			smartOption = new Button(modeContainer, SWT.RADIO);
			smartOption.setText("Smart Randomize");
			smartOption.setToolTipText("Attempts to generate growth rates that are randomized, but relatively normal looking.");
			smartOption.setEnabled(false);
			smartOption.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					setMode(GrowthOptions.Mode.SMART);
				}
			});
			
			optionData = new FormData();
			optionData.left = new FormAttachment(fullRandomOption, 0, SWT.LEFT);
			optionData.top = new FormAttachment(fullRandomOption, 10);
			smartOption.setLayoutData(optionData);
		}

		adjustHPGrowths = new Button(group, SWT.CHECK);
		adjustHPGrowths.setText("Adjust HP Growths");
		adjustHPGrowths.setToolTipText("Puts extra emphasis on HP growths relative to other stats.");
		adjustHPGrowths.setEnabled(false);

		optionData = new FormData();
		optionData.left = new FormAttachment(enableButton, 10, SWT.LEFT);
		optionData.top = new FormAttachment(modeContainer, 10);
		adjustHPGrowths.setLayoutData(optionData);

		if (hasSTRMAGSplit) {
			adjustSTRMAGSplit = new Button(group, SWT.CHECK);
			adjustSTRMAGSplit.setText("Adjust STR/MAG by Class");
			adjustSTRMAGSplit.setToolTipText("Ensures that characters that primarily use magic randomize a higher or equal magic growth than strength and that\ncharacters that primarily use physical attacks randomize a higher or equal strength growth than magic.\n\nCharacters that use both will not be weighted in either direction.");
			adjustSTRMAGSplit.setEnabled(false);

			optionData = new FormData();
			optionData.left = new FormAttachment(adjustHPGrowths, 0, SWT.LEFT);
			optionData.top = new FormAttachment(adjustHPGrowths, 5);
			adjustSTRMAGSplit.setLayoutData(optionData);
		}
	}

	public void overrideMaxGrowthAllowed(int maxGrowth) {
		growthRangeControl.getMaxSpinner().setMaximum(maxGrowth);
	}

	private void setMode(GrowthOptions.Mode newMode) {
		currentMode = newMode;
		if (isEnabled) {
			switch (newMode) {
				case REDISTRIBUTE:
					varianceSpinner.setEnabled(true);
					deltaSpinner.setEnabled(false);
					growthRangeControl.setEnabled(true);
					break;
				case DELTA:
					varianceSpinner.setEnabled(false);
					deltaSpinner.setEnabled(true);
					growthRangeControl.setEnabled(true);
					break;
				case FULL:
					varianceSpinner.setEnabled(false);
					deltaSpinner.setEnabled(false);
					growthRangeControl.setEnabled(true);
					break;
				case SMART:
					varianceSpinner.setEnabled(false);
					deltaSpinner.setEnabled(false);
					growthRangeControl.setEnabled(false);
					break;
			}
		}
	}

	@Override
	public GrowthOptions getOptions() {
		if (!isEnabled) { return null; }

		MinMaxVarOption redistributionOption = null;
		MinMaxVarOption deltaOption = null;
		MinMaxOption fullOption = null;

		switch (currentMode) {
			case REDISTRIBUTE:
				redistributionOption = new MinMaxVarOption(growthRangeControl.getMinMaxOption(), varianceSpinner.getSelection());
				break;
			case DELTA:
				deltaOption = new MinMaxVarOption(growthRangeControl.getMinMaxOption(), deltaSpinner.getSelection());
				break;
			case FULL:
				fullOption = growthRangeControl.getMinMaxOption();
				break;
			case SMART:
				break;
		}

		boolean adjustSTRMAG = adjustSTRMAGSplit != null ? adjustSTRMAGSplit.getSelection() : false;

		return new GrowthOptions(currentMode, redistributionOption, deltaOption, fullOption, adjustHPGrowths.getSelection(), adjustSTRMAG);
	}

	private void setEnableGrowths(Boolean enabled) {
		redistributeOption.setEnabled(enabled);
		byDeltaOption.setEnabled(enabled);
		fullRandomOption.setEnabled(enabled);
		if (supportsSmartRandomization) {
			smartOption.setEnabled(enabled);
		}
		varianceSpinner.setEnabled(enabled && currentMode == GrowthOptions.Mode.REDISTRIBUTE);
		deltaSpinner.setEnabled(enabled && currentMode == GrowthOptions.Mode.DELTA);
		growthRangeControl.setEnabled(enabled);
		adjustHPGrowths.setEnabled(enabled);
		if (adjustSTRMAGSplit != null) { adjustSTRMAGSplit.setEnabled(enabled && currentMode != GrowthOptions.Mode.DELTA); }

		isEnabled = enabled;
	}

	@Override
	public void initialize(GrowthOptions options) {
		if (options == null) {
			enableButton.setSelection(false);
			setEnableGrowths(false);
			return;
		}

		enableButton.setSelection(true);
		setEnableGrowths(true);
		setMode(options.mode);

		switch (options.mode) {
			case REDISTRIBUTE:
				redistributeOption.setSelection(true);
				byDeltaOption.setSelection(false);
				fullRandomOption.setSelection(false);
				if (supportsSmartRandomization) {
					smartOption.setSelection(false);
				}
				varianceSpinner.setSelection(options.redistributionOption.variance);
				if (options.redistributionOption.minValue < growthRangeControl.getMinSpinner().getMaximum()) {
					growthRangeControl.setMin(options.redistributionOption.minValue);
					growthRangeControl.setMax(options.redistributionOption.maxValue);
				} else {
					growthRangeControl.setMax(options.redistributionOption.maxValue);
					growthRangeControl.setMin(options.redistributionOption.minValue);
				}
				break;
			case DELTA:
				redistributeOption.setSelection(false);
				byDeltaOption.setSelection(true);
				fullRandomOption.setSelection(false);
				if (supportsSmartRandomization) {
					smartOption.setSelection(false);
				}
				deltaSpinner.setSelection(options.deltaOption.variance);
				if (options.deltaOption.minValue < growthRangeControl.getMinSpinner().getMaximum()) {
					growthRangeControl.setMin(options.deltaOption.minValue);
					growthRangeControl.setMax(options.deltaOption.maxValue);
				} else {
					growthRangeControl.setMax(options.deltaOption.maxValue);
					growthRangeControl.setMin(options.deltaOption.minValue);
				}
				break;
			case FULL:
				redistributeOption.setSelection(false);
				byDeltaOption.setSelection(false);
				fullRandomOption.setSelection(true);
				if (supportsSmartRandomization) {
					smartOption.setSelection(false);
				}
				if (options.fullOption.minValue < growthRangeControl.getMinSpinner().getMaximum()) {
					growthRangeControl.setMin(options.fullOption.minValue);
					growthRangeControl.setMax(options.fullOption.maxValue);
				} else {
					growthRangeControl.setMax(options.fullOption.maxValue);
					growthRangeControl.setMin(options.fullOption.minValue);
				}
				break;
			case SMART:
				redistributeOption.setSelection(false);
				byDeltaOption.setSelection(false);
				fullRandomOption.setSelection(false);
				if (supportsSmartRandomization) {
					smartOption.setSelection(true);
				}
				break;
		}

		adjustHPGrowths.setSelection(options.adjustHP);
		if (adjustSTRMAGSplit != null) {
			adjustSTRMAGSplit.setSelection(options.adjustSTRMAGSplit);
		}
	}
}
