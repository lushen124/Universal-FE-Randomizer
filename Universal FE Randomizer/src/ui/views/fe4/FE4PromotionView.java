package ui.views.fe4;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;
import ui.common.GuiUtil;
import ui.model.fe4.FE4PromotionOptions;
import ui.views.YuneView;

public class FE4PromotionView extends YuneView<FE4PromotionOptions> {

	private Button strictButton;
	
	private Button looseButton;
	private Button allowMountChangeButton;
	private Button allowEnemyClassButton;
	
	private Button randomButton;
	private Button commonWeaponButton;
	
	private FE4PromotionOptions.Mode currentMode;
	
	public FE4PromotionView(Composite parent) {
		super(parent);
	}

	@Override
	public String getGroupTitle() {
		return "Promotions";
	}

	@Override
	public String getGroupTooltip() {
		return "Controls class promotions for all playable characters.";
	}

	@Override
	protected void compose() {
		strictButton = new Button(group, SWT.RADIO);
		strictButton.setText("Default Promotions");
		strictButton.setToolTipText("Sets promotions based on normal class progression.");
		strictButton.setEnabled(true);
		strictButton.setSelection(true);
		strictButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(FE4PromotionOptions.Mode.STRICT);
			}
		});
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		strictButton.setLayoutData(optionData);
		
		looseButton = new Button(group, SWT.RADIO);
		looseButton.setText("Similar Promotions");
		looseButton.setToolTipText("Sets promotions based on weapon ranks, holy blood, class skills, and base stats.");
		looseButton.setEnabled(true);
		looseButton.setSelection(false);
		looseButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(FE4PromotionOptions.Mode.LOOSE);
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(strictButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(strictButton, 15);
		looseButton.setLayoutData(optionData);
		
		allowMountChangeButton = new Button(group, SWT.CHECK);
		allowMountChangeButton.setText("Allow Mount Change");
		allowMountChangeButton.setToolTipText("Allows mounted units to change between mounts (e.g. flying to horseback, and vice versa).");
		allowMountChangeButton.setEnabled(false);
		allowMountChangeButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(looseButton, 10, SWT.LEFT);
		optionData.top = new FormAttachment(looseButton, 5);
		allowMountChangeButton.setLayoutData(optionData);
		
		allowEnemyClassButton = new Button(group, SWT.CHECK);
		allowEnemyClassButton.setText("Allow Enemy-only Promotions");
		allowEnemyClassButton.setToolTipText("Allows units to promote into enemy-only classes like Baron, Queen, and Emperor.");
		allowEnemyClassButton.setEnabled(false);
		allowEnemyClassButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(allowMountChangeButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(allowMountChangeButton, 5);
		allowEnemyClassButton.setLayoutData(optionData);
		
		randomButton = new Button(group, SWT.RADIO);
		randomButton.setText("Random Promotions");
		randomButton.setToolTipText("Sets promotions enitrely randomly.");
		randomButton.setEnabled(true);
		randomButton.setSelection(false);
		randomButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setMode(FE4PromotionOptions.Mode.RANDOM);
			}
		});
		
		optionData = new FormData();
		optionData.left = new FormAttachment(looseButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(allowEnemyClassButton, 15);
		randomButton.setLayoutData(optionData);
		
		commonWeaponButton = new Button(group, SWT.CHECK);
		commonWeaponButton.setText("Requires Common Weapon");
		commonWeaponButton.setToolTipText("Requires the promoted class to share at least one weapon type with its predecessor.");
		commonWeaponButton.setEnabled(false);
		commonWeaponButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(randomButton, 10, SWT.LEFT);
		optionData.top = new FormAttachment(randomButton, 5);
		commonWeaponButton.setLayoutData(optionData);
	}


	private void setMode(FE4PromotionOptions.Mode mode) {
		currentMode = mode;
		
		allowMountChangeButton.setEnabled(currentMode == FE4PromotionOptions.Mode.LOOSE);
		allowEnemyClassButton.setEnabled(currentMode == FE4PromotionOptions.Mode.LOOSE);
		commonWeaponButton.setEnabled(currentMode == FE4PromotionOptions.Mode.RANDOM);	
	}

	@Override
	public FE4PromotionOptions getOptions() {
		return new FE4PromotionOptions(currentMode, allowMountChangeButton.getSelection(), allowEnemyClassButton.getSelection(), commonWeaponButton.getSelection());
	}

	@Override
	public void initialize(FE4PromotionOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			currentMode = options.promotionMode;
			
			if (currentMode == null) { currentMode = FE4PromotionOptions.Mode.STRICT; }
			
			strictButton.setSelection(currentMode == FE4PromotionOptions.Mode.STRICT);
			
			looseButton.setSelection(currentMode == FE4PromotionOptions.Mode.LOOSE);
			allowMountChangeButton.setEnabled(currentMode == FE4PromotionOptions.Mode.LOOSE);
			allowMountChangeButton.setSelection(options.allowMountChanges);
			allowEnemyClassButton.setEnabled(currentMode == FE4PromotionOptions.Mode.LOOSE);
			allowEnemyClassButton.setSelection(options.allowEnemyOnlyPromotedClasses);
			
			
			randomButton.setSelection(currentMode == FE4PromotionOptions.Mode.RANDOM);
			commonWeaponButton.setEnabled(currentMode == FE4PromotionOptions.Mode.RANDOM);
			commonWeaponButton.setSelection(options.requireCommonWeapon);
		}
	}
}
