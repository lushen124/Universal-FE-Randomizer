package ui;

import fedata.gba.general.TerrainTable;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import ui.general.MinMaxControl;
import ui.model.TerrainOptions;

import java.util.*;

import static fedata.gba.general.TerrainTable.TerrainTableType.*;

public class TerrainView extends Composite {

    private Group container;

    private Button enableButton;
    private Label effectChanceLabel;
    private Spinner effectChanceSpinner;
    private Button keepSafeTiles;

    private Map<TerrainTable.TerrainTableType, Button> enabledMap = new HashMap<>();
    private Map<TerrainTable.TerrainTableType, MinMaxControl> rangeMap = new HashMap<>();
    private Map<TerrainTable.TerrainTableType, Spinner> spinnerMap = new HashMap<>();
    private Map<TerrainTable.TerrainTableType, Label> spinnerLabelMap = new HashMap<>();


    public TerrainView(Composite parent, int style, GameType type) {
        super(parent, style);

        FillLayout layout = new FillLayout();
        setLayout(layout);

        container = new Group(this, SWT.NONE);
        container.setText("Terrain Bonuses");
        container.setToolTipText("Randomizes Terrain Bonuses.");

        FormLayout groupMargins = new FormLayout();
        groupMargins.marginLeft = 5;
        groupMargins.marginTop = 5;
        groupMargins.marginBottom = 5;
        groupMargins.marginRight = 5;
        container.setLayout(groupMargins);


        enableButton = new Button(container, SWT.CHECK);
        enableButton.setText("Enable Randomization");

        effectChanceSpinner = new Spinner(container, SWT.NONE);
        FormData formData = new FormData();
        formData.top = new FormAttachment(enableButton, 5);
        formData.right = new FormAttachment(100, -5);
        effectChanceSpinner.setLayoutData(formData);
        effectChanceSpinner.setEnabled(false);
        effectChanceSpinner.setMinimum(0);
        effectChanceSpinner.setMaximum(100);
        effectChanceSpinner.setSelection(0);
        effectChanceSpinner.setIncrement(1);

        effectChanceLabel = new Label(container, SWT.NONE);
        effectChanceLabel.setText("Effect chance: ");
        effectChanceLabel.setToolTipText("The chance that a tile will get any kind of effect.");
        formData = new FormData();
        formData.top = new FormAttachment(effectChanceSpinner, 0, SWT.TOP);
        formData.right = new FormAttachment(effectChanceSpinner, 0);
        effectChanceLabel.setLayoutData(formData);
        effectChanceLabel.setEnabled(false);

        keepSafeTiles = new Button(container, SWT.CHECK);
        keepSafeTiles.setText("Keep safe tiles");
        formData = new FormData();
        formData.left = new FormAttachment(enableButton, 10, SWT.LEFT);
        formData.top = new FormAttachment(effectChanceSpinner, 5);
        keepSafeTiles.setLayoutData(formData);
        keepSafeTiles.setEnabled(false);


        Control previousControl = keepSafeTiles;
        for (TerrainTable.TerrainTableType terrainType : Arrays.asList(AVOID, DEF, RES, HEALING, STATUS_RECOVERY)) {
            Composite group = createCompositeForTerrainType(container, terrainType);

            formData = new FormData();
            formData.left = new FormAttachment(container, 10);
            formData.top = new FormAttachment(previousControl, 5);
            formData.right = new FormAttachment(100, -5);
            group.setLayoutData(formData);
            previousControl = group;
        }

        enableButton.addListener(SWT.Selection, event -> {
            for (Control button : enabledMap.values()) {
                button.setEnabled(enableButton.getSelection());
            }
            for (Control button : spinnerMap.values()) {
                button.setEnabled(enableButton.getSelection());
            }
            for (Control button : spinnerLabelMap.values()) {
                button.setEnabled(enableButton.getSelection());
            }
            for (Control button : rangeMap.values()) {
                button.setEnabled(enableButton.getSelection());
            }
            keepSafeTiles.setEnabled(enableButton.getSelection());
            effectChanceLabel.setEnabled(enableButton.getSelection());
            effectChanceSpinner.setEnabled(enableButton.getSelection());
        });
    }

    private Composite createCompositeForTerrainType(Composite container, TerrainTable.TerrainTableType terrainTableType) {
        Composite group = new Composite(container, SWT.NONE);
        group.setLayout(new FormLayout());

        Button typeEnabledButton = new Button(group, SWT.CHECK);
        typeEnabledButton.setEnabled(false);
        typeEnabledButton.setText("Randomize " + terrainTableType.displayString);
        enabledMap.put(terrainTableType, typeEnabledButton);

        Spinner chanceSpinner = new Spinner(group, SWT.NONE);
        chanceSpinner.setMinimum(0);
        chanceSpinner.setIncrement(5);
        chanceSpinner.setSelection(0);
        chanceSpinner.setMaximum(100);
        spinnerMap.put(terrainTableType, chanceSpinner);

        FormData formData = new FormData();
        formData.right = new FormAttachment(100, -5);
        formData.top = new FormAttachment(typeEnabledButton, 5);
        chanceSpinner.setLayoutData(formData);
        chanceSpinner.setEnabled(false);

        Label chanceLabel = new Label(group, SWT.NONE);
        chanceLabel.setText("Chance: ");
        chanceLabel.setEnabled(false);
        formData = new FormData();
        formData.right = new FormAttachment(chanceSpinner, 0, SWT.LEFT);
        formData.top = new FormAttachment(typeEnabledButton, 5);
        chanceLabel.setLayoutData(formData);
        spinnerLabelMap.put(terrainTableType, chanceLabel);

        if (!terrainTableType.isToggle) {
            MinMaxControl rangeControl = new MinMaxControl(group, SWT.NONE, "Min:", "Max:");
            rangeControl.getMinSpinner().setSelection(terrainTableType.min);
            rangeControl.getMinSpinner().setMinimum(terrainTableType.min);
            rangeControl.getMinSpinner().setMaximum(terrainTableType.max - 1);
            rangeControl.getMinSpinner().setIncrement(1);
            rangeControl.setEnabled(false);

            rangeControl.getMaxSpinner().setSelection(terrainTableType.min + 1);
            rangeControl.getMaxSpinner().setMinimum(terrainTableType.min + 1);
            rangeControl.getMaxSpinner().setMaximum(terrainTableType.max);
            rangeControl.getMaxSpinner().setIncrement(1);
            rangeControl.setEnabled(false);
            rangeMap.put(terrainTableType, rangeControl);
            formData = new FormData();
            formData.right = new FormAttachment(100, 0);
            formData.top = new FormAttachment(chanceSpinner, 5);
            rangeControl.setLayoutData(formData);
        }

        typeEnabledButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                for (Control child : group.getChildren()) {
                    if (child != typeEnabledButton) {
                        child.setEnabled(typeEnabledButton.getSelection());
                    }
                }
            }
        });

        return group;
    }

    public TerrainOptions getTerrainOptions() {
        TerrainOptions options = new TerrainOptions();
        if (!enableButton.getSelection()) {
            return options;
        }

        options.enabled = true;
        options.effectChance = effectChanceSpinner.getSelection();

        options.randomizeDef = enabledMap.get(DEF).getSelection();
        options.randomizeRes = enabledMap.get(RES).getSelection();
        options.randomizeAvoid = enabledMap.get(AVOID).getSelection();
        options.randomizeHealing = enabledMap.get(HEALING).getSelection();
        options.randomizeStatusRecovery = enabledMap.get(STATUS_RECOVERY).getSelection();

        options.defChance = spinnerMap.get(DEF).getSelection();
        options.resChance = spinnerMap.get(RES).getSelection();
        options.avoidChance = spinnerMap.get(AVOID).getSelection();
        options.healingChance = spinnerMap.get(HEALING).getSelection();
        options.statusRestoreChance = spinnerMap.get(STATUS_RECOVERY).getSelection();

        options.defRange = rangeMap.get(DEF).getMinMaxOption();
        options.resRange = rangeMap.get(RES).getMinMaxOption();
        options.avoidRange = rangeMap.get(AVOID).getMinMaxOption();
        options.healingRange = rangeMap.get(HEALING).getMinMaxOption();

        return options;
    }

    public void setTerrainOptions(TerrainOptions options) {
        if (options == null) {
            enableButton.setSelection(false);
        } else {
            enableButton.setSelection(true);
            keepSafeTiles.setEnabled(true);
            keepSafeTiles.setSelection(options.keepSafeTiles);

            effectChanceLabel.setEnabled(true);
            effectChanceSpinner.setEnabled(true);
            effectChanceSpinner.setSelection(options.effectChance);

            enabledMap.values().forEach(b -> b.setEnabled(true));
            enabledMap.get(DEF).setSelection(options.randomizeDef);
            enabledMap.get(RES).setSelection(options.randomizeRes);
            enabledMap.get(AVOID).setSelection(options.randomizeAvoid);
            enabledMap.get(HEALING).setSelection(options.randomizeHealing);
            enabledMap.get(STATUS_RECOVERY).setSelection(options.randomizeStatusRecovery);

            rangeMap.entrySet().forEach(e -> e.getValue().setEnabled(enabledMap.get(e.getKey()).getSelection()));
            if (options.defRange != null) {
                rangeMap.get(DEF).getMinSpinner().setSelection(options.defRange.minValue);
                rangeMap.get(DEF).getMaxSpinner().setSelection(options.defRange.maxValue);
            }
            if (options.resRange != null) {
                rangeMap.get(RES).getMinSpinner().setSelection(options.resRange.minValue);
                rangeMap.get(RES).getMaxSpinner().setSelection(options.resRange.maxValue);
            }
            if (options.avoidRange != null) {
                rangeMap.get(AVOID).getMinSpinner().setSelection(options.avoidRange.minValue);
                rangeMap.get(AVOID).getMaxSpinner().setSelection(options.avoidRange.maxValue);
            }
            if (options.healingRange != null) {
                rangeMap.get(HEALING).getMinSpinner().setSelection(options.healingRange.minValue);
                rangeMap.get(HEALING).getMaxSpinner().setSelection(options.healingRange.maxValue);
            }

            spinnerMap.entrySet().forEach(e -> e.getValue().setEnabled(enabledMap.get(e.getKey()).getSelection()));
            spinnerLabelMap.entrySet().forEach(e -> e.getValue().setEnabled(enabledMap.get(e.getKey()).getSelection()));
            spinnerMap.get(DEF).setSelection(options.defChance);
            spinnerMap.get(RES).setSelection(options.resChance);
            spinnerMap.get(AVOID).setSelection(options.avoidChance);
            spinnerMap.get(HEALING).setSelection(options.healingChance);
            spinnerMap.get(STATUS_RECOVERY).setSelection(options.statusRestoreChance);
        }
    }
}
