package ui.fe4;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

public class FE4PromotionView extends Composite {
	private Group container;
	
	private Button strictButton;
	
	private Button looseButton;
	private Button allowMountChangeButton;
	private Button allowEnemyClassButton;
	
	private Button randomButton;
	private Button commonWeaponButton;
	
	private FE4PromotionOptions.Mode currentMode;
	
	public FE4PromotionView(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new FillLayout());
		
		container = new Group(this, SWT.NONE);
		container.setText("Promotions");
		container.setToolTipText("Controls class promotions for all playable characters.");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginRight = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		container.setLayout(mainLayout);
		
		strictButton = new Button(container, SWT.RADIO);
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
		
		looseButton = new Button(container, SWT.RADIO);
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
		
		allowMountChangeButton = new Button(container, SWT.CHECK);
		allowMountChangeButton.setText("Allow Mount Change");
		allowMountChangeButton.setToolTipText("Allows mounted units to change between mounts (e.g. flying to horseback, and vice versa).");
		allowMountChangeButton.setEnabled(false);
		allowMountChangeButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(looseButton, 10, SWT.LEFT);
		optionData.top = new FormAttachment(looseButton, 5);
		allowMountChangeButton.setLayoutData(optionData);
		
		allowEnemyClassButton = new Button(container, SWT.CHECK);
		allowEnemyClassButton.setText("Allow Enemy-only Promotions");
		allowEnemyClassButton.setToolTipText("Allows units to promote into enemy-only classes like Baron, Queen, and Emperor.");
		allowEnemyClassButton.setEnabled(false);
		allowEnemyClassButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(allowMountChangeButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(allowMountChangeButton, 5);
		allowEnemyClassButton.setLayoutData(optionData);
		
		randomButton = new Button(container, SWT.RADIO);
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
		
		commonWeaponButton = new Button(container, SWT.CHECK);
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
	
	public FE4PromotionOptions getPromotionOptions() {
		return new FE4PromotionOptions(currentMode, allowMountChangeButton.getSelection(), allowEnemyClassButton.getSelection(), commonWeaponButton.getSelection());
	}
	
	public void setPromotionOptions(FE4PromotionOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			currentMode = options.promotionMode;
			
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
