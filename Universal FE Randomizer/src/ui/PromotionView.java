package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

import fedata.general.FEBase.GameType;
import ui.model.PromotionOptions;

public class PromotionView extends Composite {
    private Group container;

    private Button strictButton;

    private Button looseButton;
    private Button allowMountChangeButton;
    private Button allowEnemyClassButton;
    private Button allowMonsterClassButton;


    private Button randomButton;
    private Button commonWeaponButton;
    private Button keepDamageTypeButton;

    private PromotionOptions.Mode currentMode = PromotionOptions.Mode.STRICT;
    // FE6 Only
    private Button allowThiefPromotion;
    private Button keepThiefAbilities;
    private Button universally;

    public PromotionView(Composite parent, int style, GameType type) {
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
                setMode(PromotionOptions.Mode.STRICT, type);
            }
        });

        FormData optionData = new FormData();
        optionData.left = new FormAttachment(0, 0);
        optionData.top = new FormAttachment(0, 0);
        strictButton.setLayoutData(optionData);

        looseButton = new Button(container, SWT.RADIO);
        looseButton.setText("Similar Promotions");
        if (GameType.FE4.equals(type)) {
            looseButton
                    .setToolTipText("Sets promotions based on weapon ranks, holy blood, class skills, and base stats.");
        } else {
            looseButton.setToolTipText(
                    "Sets promotions primarily based on weapon ranks. \nnot recommended, heavily limited options especially for FE6.");
        }
        looseButton.setEnabled(true);
        looseButton.setSelection(false);
        looseButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                setMode(PromotionOptions.Mode.LOOSE, type);
            }
        });

        optionData = new FormData();
        optionData.left = new FormAttachment(strictButton, 0, SWT.LEFT);
        optionData.top = new FormAttachment(strictButton, 15);
        looseButton.setLayoutData(optionData);

        allowMountChangeButton = new Button(container, SWT.CHECK);
        allowMountChangeButton.setText("Allow Mount Change");
        allowMountChangeButton.setToolTipText(
                "Allows mounted units to change between mounts (e.g. flying to horseback, and vice versa).");
        allowMountChangeButton.setEnabled(false);
        allowMountChangeButton.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(looseButton, 10, SWT.LEFT);
        optionData.top = new FormAttachment(looseButton, 5);
        allowMountChangeButton.setLayoutData(optionData);

        allowEnemyClassButton = new Button(container, SWT.CHECK);
        allowEnemyClassButton.setText("Allow Enemy-only Promotions");
        if (GameType.FE8.equals(type)) {
            allowEnemyClassButton.setToolTipText(
                    "Allows units to promote into Special not usually usable classes (Necromancer, Fleet).");
        } else if (GameType.FE7.equals(type)) {
            allowEnemyClassButton.setToolTipText(
                    "Allows units to promote into Special not usually usable classes (Archsage, Dark Druid).");
        } else if (GameType.FE6.equals(type)) {
            allowEnemyClassButton.setToolTipText(
                    "Allows units to promote into Special not usually usable classes (King).");
        } else if (GameType.FE4.equals(type)) {
            allowEnemyClassButton
                    .setToolTipText("Allows units to promote into enemy-only classes like Baron, Queen, and Emperor.");
        }
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
                setMode(PromotionOptions.Mode.RANDOM, type);
            }
        });

        optionData = new FormData();
        optionData.left = new FormAttachment(looseButton, 0, SWT.LEFT);
        optionData.top = new FormAttachment(allowEnemyClassButton, 15);
        randomButton.setLayoutData(optionData);

        commonWeaponButton = new Button(container, SWT.CHECK);
        commonWeaponButton.setText("Requires Common Weapon");
        commonWeaponButton
                .setToolTipText("Requires the promoted class to share at least one weapon type with its predecessor.");
        commonWeaponButton.setEnabled(false);
        commonWeaponButton.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(randomButton, 10, SWT.LEFT);
        optionData.top = new FormAttachment(randomButton, 5);
        commonWeaponButton.setLayoutData(optionData);
        if (type.isGBA()) {
            keepDamageTypeButton = new Button(container, SWT.CHECK);
            keepDamageTypeButton.setText("Keep Same Damage Type");
            keepDamageTypeButton.setToolTipText(
                    "Magical classes will promote into random magical classes, while physical classes will stay in physical classes.");
            keepDamageTypeButton.setEnabled(false);
            keepDamageTypeButton.setSelection(false);

            optionData = new FormData();
            optionData.left = new FormAttachment(randomButton, 10, SWT.LEFT);
            optionData.top = new FormAttachment(commonWeaponButton, 5);
            keepDamageTypeButton.setLayoutData(optionData);

            if (type.equals(GameType.FE8)) {
                allowMonsterClassButton = new Button(container, SWT.CHECK);
                allowMonsterClassButton.setText("Allow Monster Promotions");
                allowMonsterClassButton.setToolTipText(
                        "Allows units to promote into Monster classes like Elder Bael, Wight, and Arch Mogall.");
                allowMonsterClassButton.setEnabled(false);
                allowMonsterClassButton.setSelection(false);

                optionData = new FormData();
                optionData.left = new FormAttachment(randomButton, 0, SWT.LEFT);
                optionData.top = new FormAttachment(keepDamageTypeButton, 10);
                allowMonsterClassButton.setLayoutData(optionData);
            } else if (GameType.FE6.equals(type)) {
                allowThiefPromotion = new Button(container, SWT.CHECK);
                allowThiefPromotion.setText("Allow Thief Promotion");
                allowThiefPromotion.setToolTipText("Allow Thieves to promote.");
                allowThiefPromotion.setEnabled(true);
                allowThiefPromotion.setSelection(false);

                optionData = new FormData();
                optionData.left = new FormAttachment(randomButton, 0, SWT.LEFT);
                optionData.top = new FormAttachment(keepDamageTypeButton, 10);
                allowThiefPromotion.setLayoutData(optionData);
                allowThiefPromotion.addListener(SWT.Selection, onSelection -> keepThiefAbilities.setEnabled(true));

                keepThiefAbilities = new Button(container, SWT.CHECK);
                keepThiefAbilities.setText("Keep thief abilities");
                keepThiefAbilities.setToolTipText("Allows Promoted thieves to keep their thief abilities.");
                keepThiefAbilities.setEnabled(false);
                keepThiefAbilities.setSelection(false);

                optionData = new FormData();
                optionData.left = new FormAttachment(allowThiefPromotion, 5, SWT.LEFT);
                optionData.top = new FormAttachment(allowThiefPromotion, 5);
                keepThiefAbilities.setLayoutData(optionData);

                keepThiefAbilities.addListener(SWT.Selection, onSelection -> universally.setEnabled(true));

                universally = new Button(container, SWT.CHECK);
                universally.setText("Universally");
                universally.setToolTipText("All units that are in the Promoted thief class can steal, no matter if they started as a thief or not.");
                universally.setEnabled(false);
                universally.setSelection(false);

                optionData = new FormData();
                optionData.left = new FormAttachment(keepThiefAbilities, 5, SWT.LEFT);
                optionData.top = new FormAttachment(keepThiefAbilities, 5);
                universally.setLayoutData(optionData);
            }
        }


    }

    private void setMode(PromotionOptions.Mode mode, GameType type) {
        currentMode = mode;

        allowMountChangeButton.setEnabled(currentMode == PromotionOptions.Mode.LOOSE);
        allowEnemyClassButton.setEnabled(currentMode == PromotionOptions.Mode.LOOSE);
        if (type.isGBA()) {
            keepDamageTypeButton.setEnabled(currentMode == PromotionOptions.Mode.RANDOM);
            if (GameType.FE8.equals(type)) {
                allowMonsterClassButton.setEnabled(true);
            }
        }
        commonWeaponButton.setEnabled(currentMode == PromotionOptions.Mode.RANDOM);
    }

    public PromotionOptions getPromotionOptions() {
        return new PromotionOptions(currentMode, allowMountChangeButton.getSelection(),
                allowEnemyClassButton == null ? null : allowEnemyClassButton.getSelection(),
                commonWeaponButton.getSelection(),
                allowMonsterClassButton == null ? null : allowMonsterClassButton.getSelection(),
                keepDamageTypeButton == null ? null : keepDamageTypeButton.getSelection(),
                allowThiefPromotion == null ? null : allowThiefPromotion.getSelection(),
                keepThiefAbilities == null ? null : keepThiefAbilities.getSelection(),
                universally == null ? null : universally.getSelection());
    }

    public void setPromotionOptions(PromotionOptions options, GameType type) {
        if (options == null) {
            // Shouldn't happen.
        } else {
            currentMode = options.promotionMode;

            if (currentMode == null) {
                currentMode = PromotionOptions.Mode.STRICT;
            }

            strictButton.setSelection(currentMode == PromotionOptions.Mode.STRICT);
            looseButton.setSelection(currentMode == PromotionOptions.Mode.LOOSE);
            allowMountChangeButton.setEnabled(currentMode == PromotionOptions.Mode.LOOSE);
            allowMountChangeButton.setSelection(options.allowMountChanges);
            if (type.equals(GameType.FE4)) {
                allowEnemyClassButton.setEnabled(currentMode == PromotionOptions.Mode.LOOSE);
                allowEnemyClassButton.setSelection(options.allowEnemyOnlyPromotedClasses);
            }

            if (type.isGBA()) {
                if (type.equals(GameType.FE8)) {
                    allowMonsterClassButton.setEnabled(true);
                    allowMonsterClassButton.setSelection(options.allowMonsterClasses);
                } else if (type.equals(GameType.FE6)) {
                    allowThiefPromotion.setSelection(options.allowThiefPromotion);

                    keepThiefAbilities.setEnabled(options.allowThiefPromotion);
                    keepThiefAbilities.setSelection(options.keepThiefAbilities);

                    universally.setEnabled(options.keepThiefAbilities);
                    universally.setSelection(options.universal);
                }

                keepDamageTypeButton.setEnabled(currentMode == PromotionOptions.Mode.RANDOM);
                keepDamageTypeButton.setSelection(options.keepSameDamageType);
            }

            randomButton.setSelection(currentMode == PromotionOptions.Mode.RANDOM);
            commonWeaponButton.setEnabled(currentMode == PromotionOptions.Mode.RANDOM);
            commonWeaponButton.setSelection(options.requireCommonWeapon);
        }
    }
}
