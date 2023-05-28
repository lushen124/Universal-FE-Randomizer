package ui.fe4;

import fedata.snes.fe4.FE4Data;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import ui.fe4.SkillCountView.SkillCountListener;
import ui.fe9.SkillWeightOptions;
import ui.fe9.SkillWeightView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ui.fe4.SkillsOptions.Mode.RANDOMIZE;
import static ui.fe4.SkillsOptions.Mode.SHUFFLE;

public class SkillsView extends Composite {

    private boolean skillsEnabled;
    private SkillsOptions.Mode currentMode = SHUFFLE;

    private Group container;
    private Button enableButton;

    private Button retainSkillCountsButton;

    private Button shuffleButton;
    private Button separateByGeneration;

    private Button randomizeButton;
    private SkillCountView skillCountView;
    private SkillWeightView skillWeightView;
    private Spinner pursuitSpinner;

    public SkillsView(Composite parent, int style) {
        super(parent, style);

        setLayout(new FillLayout());

        container = new Group(this, SWT.NONE);

        container.setText("Skills");

        FormLayout mainLayout = new FormLayout();
        mainLayout.marginLeft = 5;
        mainLayout.marginRight = 5;
        mainLayout.marginTop = 5;
        mainLayout.marginBottom = 5;
        container.setLayout(mainLayout);

        enableButton = new Button(container, SWT.CHECK);
        enableButton.setText("Enable Skill Randomization");
        enableButton.setToolTipText("Randomizes the personal skills of playable characters. Child characters are not affected by any settings and continue to rely on inheritence from parents.");
        enableButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                skillsEnabled = enableButton.getSelection();

                retainSkillCountsButton.setEnabled(skillsEnabled);
                shuffleButton.setEnabled(skillsEnabled);
                randomizeButton.setEnabled(skillsEnabled);

                if (skillsEnabled) {
                    skillCountView.setEnabled(randomizeButton.getSelection() && !retainSkillCountsButton.getSelection());
                    skillWeightView.setEnabled(randomizeButton.getSelection());

                    separateByGeneration.setEnabled(shuffleButton.getSelection());
                } else {
                    skillCountView.setEnabled(false);
                    skillWeightView.setEnabled(false);

                    separateByGeneration.setEnabled(false);
                }
            }
        });

        retainSkillCountsButton = new Button(container, SWT.CHECK);
        retainSkillCountsButton.setText("Retain Number of Skills");
        retainSkillCountsButton.setToolTipText("Retains each character's normal number of skills.\n\ne.g. A character with 2 personal skills will continue to have 2 personal skills after shuffling or randomization.");
        retainSkillCountsButton.setEnabled(false);
        retainSkillCountsButton.setSelection(true);
        retainSkillCountsButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                skillCountView.setEnabled(!retainSkillCountsButton.getSelection() && randomizeButton.getSelection());
            }
        });

        FormData retainCountData = new FormData();
        retainCountData.left = new FormAttachment(enableButton, 10, SWT.LEFT);
        retainCountData.top = new FormAttachment(enableButton, 10);
        retainSkillCountsButton.setLayoutData(retainCountData);

        shuffleButton = new Button(container, SWT.RADIO);
        shuffleButton.setText("Shuffle");
        shuffleButton.setToolTipText("Shuffles and redistributes all playable characters' existing skills.");
        shuffleButton.setEnabled(false);
        shuffleButton.setSelection(true);
        shuffleButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                setMode(SHUFFLE);
            }
        });

        FormData optionData = new FormData();
        optionData.left = new FormAttachment(retainSkillCountsButton, 0, SWT.LEFT);
        optionData.top = new FormAttachment(retainSkillCountsButton, 10);
        shuffleButton.setLayoutData(optionData);

        separateByGeneration = new Button(container, SWT.CHECK);
        separateByGeneration.setText("Separate Pools by Generation");
        separateByGeneration.setToolTipText("Shuffles Generation 1 character skills separately from Generation 2 Common Characters and Substitutes.");
        separateByGeneration.setEnabled(false);
        separateByGeneration.setSelection(false);

        optionData = new FormData();
        optionData.left = new FormAttachment(shuffleButton, 10, SWT.LEFT);
        optionData.top = new FormAttachment(shuffleButton, 5);
        separateByGeneration.setLayoutData(optionData);

        randomizeButton = new Button(container, SWT.RADIO);
        randomizeButton.setText("Randomize");
        randomizeButton.setToolTipText("Randomizes all skills on all playable characters.");
        randomizeButton.setEnabled(false);
        randomizeButton.setSelection(false);
        randomizeButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                setMode(RANDOMIZE);
            }
        });

        optionData = new FormData();
        optionData.left = new FormAttachment(shuffleButton, 0, SWT.LEFT);
        optionData.top = new FormAttachment(separateByGeneration, 10);
        randomizeButton.setLayoutData(optionData);

        skillCountView = new SkillCountView(container, SWT.NONE);
        skillCountView.setEnabled(false);
        skillCountView.setListener(new SkillCountListener() {
            @Override
            public void onAllItemsDisabled() {
                retainSkillCountsButton.setSelection(true);
                skillCountView.setEnabled(false);
            }
        });

        FormData viewData = new FormData();
        viewData.left = new FormAttachment(randomizeButton, 0, SWT.LEFT);
        viewData.top = new FormAttachment(randomizeButton, 0);
        viewData.right = new FormAttachment(100, -5);
        skillCountView.setLayoutData(viewData);

        List<String> skillList = Arrays.stream(FE4Data.Skill.values()).map(FE4Data.Skill::capitalizedName).collect(Collectors.toList());

        skillWeightView = new SkillWeightView(container, SWT.NONE, skillList);
        skillWeightView.setEnabled(false);
        skillWeightView.setListener(new SkillWeightView.SkillWeightsListener() {
            @Override
            public void onEnableCountChanged(int enabledCount) {}

            @Override
            public void onAllItemsDisabled() {
                enableButton.setSelection(false);
                setEnabled(false);
            }
        });

        viewData = new FormData();
        viewData.left = new FormAttachment(skillCountView, 0, SWT.LEFT);
        viewData.top = new FormAttachment(skillCountView, 0);
        viewData.right = new FormAttachment(100, -5);
        skillWeightView.setLayoutData(viewData);


        Label pursuitLabel = new Label(container, SWT.NONE);
        pursuitLabel.setText("Pursuit Chance:");
        pursuitLabel.setEnabled(false);

        FormData labelData = new FormData();
        labelData.top = new FormAttachment(skillWeightView, 10);
        labelData.left = new FormAttachment(0, 0);
        pursuitLabel.setLayoutData(labelData);

        pursuitSpinner = new Spinner(container, SWT.NONE);
        pursuitSpinner.setValues(50, 0, 100, 0, 5, 10);
        pursuitSpinner.setEnabled(false);
        pursuitSpinner.setToolTipText("Due to Pursuit's outsized importance in FE4, pursuit's chance can be set independently of the skill pool specified above.\nWhether a unit has pursuit is rolled first before determining other skills.\nA unit randomized with 0 skills will not be rolled.");


        viewData = new FormData();
        viewData.left = new FormAttachment(pursuitLabel, 10);
        viewData.top = new FormAttachment(skillWeightView, 10);
        pursuitSpinner.setLayoutData(viewData);
    }

    private void setMode(SkillsOptions.Mode mode) {
        currentMode = mode;
        if (!skillsEnabled) {
            return;
        }

        separateByGeneration.setEnabled(SHUFFLE.equals(mode));
        pursuitSpinner.setEnabled(RANDOMIZE.equals(mode));
        skillCountView.setEnabled(RANDOMIZE.equals(mode) && !retainSkillCountsButton.getSelection());
        skillWeightView.setEnabled(RANDOMIZE.equals(mode));
    }

    public SkillsOptions getSkillOptions() {
        if (!skillsEnabled) {
            return null;
        }
        SkillWeightOptions skillWeights = skillWeightView.getSkillWeights();
        skillWeights.setPursuitChance(pursuitSpinner.getSelection());

        return new SkillsOptions(currentMode, retainSkillCountsButton.getSelection(), separateByGeneration.getSelection(), skillCountView.getSkillCountDistribution(), skillWeights);
    }

    public void setSkillOptions(SkillsOptions options) {
        skillsEnabled = options != null;
        enableButton.setSelection(skillsEnabled);
        retainSkillCountsButton.setEnabled(skillsEnabled);
        shuffleButton.setEnabled(skillsEnabled);
        randomizeButton.setEnabled(skillsEnabled);
        separateByGeneration.setEnabled(false);
        skillCountView.setEnabled(false);
        skillWeightView.setEnabled(false);

        if (options != null) {
            retainSkillCountsButton.setSelection(options.retainNumberOfSkills);
            pursuitSpinner.setEnabled(RANDOMIZE.equals(options.mode));

            setMode(options.mode);
            shuffleButton.setSelection(SHUFFLE.equals(options.mode));
            randomizeButton.setSelection(RANDOMIZE.equals(options.mode));
            separateByGeneration.setSelection(SHUFFLE.equals(options.mode) && options.separatePoolsByGeneration);

            if (RANDOMIZE.equals(options.mode)) {
                skillCountView.setSkillCountDistribution(options.skillCounts);
                skillWeightView.setSkillWeights(options.skillWeights);
            }
        }
    }
}
