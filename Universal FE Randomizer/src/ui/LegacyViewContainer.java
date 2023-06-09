package ui;

import fedata.gcnwii.fe9.FE9Data;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import ui.views.*;
import ui.views.fe4.*;
import ui.views.fe9.CONAffinityView;
import ui.views.fe9.FE9ClassesView;
import ui.views.fe9.FE9EnemyBuffView;
import ui.views.fe9.FE9SkillView;
import util.OptionRecorder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * View Container that is the equivalent of the GUI from before the GUI Rework, everything is just in one large Form Layout.
 */
public class LegacyViewContainer extends YuneViewContainer {


    private GrowthsView growthView;
    private BasesView baseView;
    private ClassesView classView;
    private MOVCONAffinityView otherCharOptionView;
    private WeaponsView weaponView;
    private EnemyBuffsView enemyView;
    private GameMechanicsView miscView;
    private RewardRandomizationView rewardView;
    private RecruitmentView recruitView;
    private ItemAssignmentView itemAssignmentView;
    private CharacterShufflingView characterShufflingView;

    // FE4
    private FE4SkillsView skillsView;
    private HolyBloodView holyBloodView;
    private FE4ClassesView fe4ClassView;
    private FE4PromotionView fe4PromotionView;
    private FE4EnemyBuffView fe4EnemyBuffView;

    // FE9
    private FE9SkillView fe9SkillView;
    private CONAffinityView conAffinityView;
    private FE9EnemyBuffView fe9EnemyView;
    private FE9ClassesView fe9ClassesView;

    public LegacyViewContainer(Composite parent, GameType loadedType) {
        super(parent, loadedType);
    }

    @Override
    protected void compose() {
        this.setLayout(new FormLayout());
        growthView = new GrowthsView(this, type.hasSTRMAGSplit());
        growthView.setSize(200, 200);

        FormData growthData = new FormData();
        growthData.top = new FormAttachment(0, 0);
        growthData.left = new FormAttachment(0, 0);
        growthView.setLayoutData(growthData);

        baseView = new BasesView(this, type);
        baseView.setSize(200, 200);

        FormData baseData = new FormData();
        baseData.top = new FormAttachment(growthView, 5);
        baseData.left = new FormAttachment(growthView, 0, SWT.LEFT);
        baseData.right = new FormAttachment(growthView, 0, SWT.RIGHT);
        baseView.setLayoutData(baseData);

        if (type == GameType.FE4) {
            // To prevent gen 2 overflow, the max growth allowed for any single stat is 85%.
            growthView.overrideMaxGrowthAllowed(85);

            holyBloodView = new HolyBloodView(this);
            holyBloodView.setSize(200, 200);

            FormData holyBloodData = new FormData();
            holyBloodData.top = new FormAttachment(baseView, 5);
            holyBloodData.left = new FormAttachment(baseView, 0, SWT.LEFT);
            holyBloodData.right = new FormAttachment(baseView, 0, SWT.RIGHT);
            holyBloodData.bottom = new FormAttachment(100, -10);
            holyBloodView.setLayoutData(holyBloodData);

            skillsView = new FE4SkillsView(this, 1);
            skillsView.setSize(200, 200);

            FormData skillsData = new FormData();
            skillsData.top = new FormAttachment(growthView, 0, SWT.TOP);
            skillsData.left = new FormAttachment(growthView, 5);
            skillsData.bottom = new FormAttachment(100, -10);
            skillsView.setLayoutData(skillsData);

            fe4ClassView = new FE4ClassesView(this);
            fe4ClassView.setSize(200, 200);

            FormData classData = new FormData();
            classData.top = new FormAttachment(skillsView, 0, SWT.TOP);
            classData.left = new FormAttachment(skillsView, 5);
            classData.bottom = new FormAttachment(100, -10);
            fe4ClassView.setLayoutData(classData);

            fe4PromotionView = new FE4PromotionView(this);
            fe4PromotionView.setSize(200, 200);

            FormData promoData = new FormData();
            promoData.top = new FormAttachment(fe4ClassView, 0, SWT.TOP);
            promoData.left = new FormAttachment(fe4ClassView, 5);
            promoData.right = new FormAttachment(100, -5);
            fe4PromotionView.setLayoutData(promoData);

            fe4EnemyBuffView = new FE4EnemyBuffView(this);
            fe4EnemyBuffView.setSize(200, 200);

            FormData buffData = new FormData();
            buffData.top = new FormAttachment(fe4PromotionView, 5);
            buffData.left = new FormAttachment(fe4PromotionView, 0, SWT.LEFT);
            buffData.right = new FormAttachment(fe4PromotionView, 0, SWT.RIGHT);
            fe4EnemyBuffView.setLayoutData(buffData);

            miscView = new GameMechanicsView(this, type);
            miscView.setSize(200, 200);

            FormData miscData = new FormData();
            miscData.top = new FormAttachment(fe4EnemyBuffView, 5);
            miscData.left = new FormAttachment(fe4EnemyBuffView, 0, SWT.LEFT);
            miscData.right = new FormAttachment(fe4EnemyBuffView, 0, SWT.RIGHT);
            //miscData.bottom = new FormAttachment(100, -10);
            miscView.setLayoutData(miscData);

            rewardView = new RewardRandomizationView(this, type);
            rewardView.setSize(200, 200);

            FormData rewardData = new FormData();
            rewardData.top = new FormAttachment(miscView, 5);
            rewardData.left = new FormAttachment(miscView, 0, SWT.LEFT);
            rewardData.right = new FormAttachment(miscView, 0, SWT.RIGHT);
            rewardView.setLayoutData(rewardData);

        } else if (type == GameType.FE9) {
            conAffinityView = new CONAffinityView(this);
            conAffinityView.setSize(200, 200);

            FormData conAffinityData = new FormData();
            conAffinityData.top = new FormAttachment(baseView, 5);
            conAffinityData.left = new FormAttachment(baseView, 0, SWT.LEFT);
            conAffinityData.right = new FormAttachment(baseView, 0, SWT.RIGHT);
            conAffinityView.setLayoutData(conAffinityData);

            miscView = new GameMechanicsView(this, type);
            miscView.setSize(200, 200);

            FormData miscData = new FormData();
            miscData.top = new FormAttachment(conAffinityView, 5);
            miscData.left = new FormAttachment(conAffinityView, 0, SWT.LEFT);
            miscData.right = new FormAttachment(conAffinityView, 0, SWT.RIGHT);
            //miscData.bottom = new FormAttachment(100, -10);
            miscView.setLayoutData(miscData);

            rewardView = new RewardRandomizationView(this, type);
            rewardView.setSize(200, 200);

            FormData rewardData = new FormData();
            rewardData.top = new FormAttachment(miscView, 5);
            rewardData.left = new FormAttachment(miscView, 0, SWT.LEFT);
            rewardData.right = new FormAttachment(miscView, 0, SWT.RIGHT);
            rewardView.setLayoutData(rewardData);

            List<String> skills = FE9Data.Skill.allValidSkills.stream().map(skill -> {
                return skill.getDisplayString();
            }).collect(Collectors.toList());
            fe9SkillView = new FE9SkillView(this, skills, 1);
            fe9SkillView.setSize(200, 200);

            FormData skillData = new FormData();
            skillData.top = new FormAttachment(growthView, 0, SWT.TOP);
            skillData.left = new FormAttachment(growthView, 5);
            skillData.bottom = new FormAttachment(100, -10);
            fe9SkillView.setLayoutData(skillData);

            weaponView = new WeaponsView(this, type, 1);
            weaponView.setSize(200, 200);

            FormData weaponData = new FormData();
            weaponData.top = new FormAttachment(growthView, 0, SWT.TOP);
            weaponData.left = new FormAttachment(fe9SkillView, 5);
            weaponView.setLayoutData(weaponData);

            fe9ClassesView = new FE9ClassesView(this);
            fe9ClassesView.setSize(200, 200);

            FormData classData = new FormData();
            classData.top = new FormAttachment(growthView, 0, SWT.TOP);
            classData.left = new FormAttachment(weaponView, 5);
            classData.right = new FormAttachment(100, -5);
            fe9ClassesView.setLayoutData(classData);

            fe9EnemyView = new FE9EnemyBuffView(this);
            fe9EnemyView.setSize(200, 200);

            FormData enemyData = new FormData();
            enemyData.top = new FormAttachment(fe9ClassesView, 5);
            enemyData.left = new FormAttachment(fe9ClassesView, 0, SWT.LEFT);
            enemyData.right = new FormAttachment(100, -5);
            fe9EnemyView.setLayoutData(enemyData);
        } else {
            otherCharOptionView = new MOVCONAffinityView(this);
            otherCharOptionView.setSize(200, 200);

            FormData otherData = new FormData();
            otherData.top = new FormAttachment(baseView, 5);
            otherData.left = new FormAttachment(baseView, 0, SWT.LEFT);
            otherData.right = new FormAttachment(baseView, 0, SWT.RIGHT);
            otherCharOptionView.setLayoutData(otherData);

            miscView = new GameMechanicsView(this, type);
            miscView.setSize(200, 200);

            FormData miscData = new FormData();
            miscData.top = new FormAttachment(otherCharOptionView, 5);
            miscData.left = new FormAttachment(otherCharOptionView, 0, SWT.LEFT);
            miscData.right = new FormAttachment(otherCharOptionView, 0, SWT.RIGHT);
            miscView.setLayoutData(miscData);

            rewardView = new RewardRandomizationView(this, type);
            rewardView.setSize(200, 200);

            FormData rewardData = new FormData();
            rewardData.top = new FormAttachment(miscView, 5);
            rewardData.left = new FormAttachment(miscView, 0, SWT.LEFT);
            rewardData.right = new FormAttachment(miscView, 0, SWT.RIGHT);
            rewardView.setLayoutData(rewardData);

            weaponView = new WeaponsView(this, type, 1);
            weaponView.setSize(200, 200);

            FormData weaponData = new FormData();
            weaponData.top = new FormAttachment(growthView, 0, SWT.TOP);
            weaponData.left = new FormAttachment(growthView, 5);
            weaponData.bottom = new FormAttachment(100, -10);
            weaponView.setLayoutData(weaponData);

            classView = new ClassesView(this, type);
            classView.setSize(200, 200);

            FormData classData = new FormData();
            classData.top = new FormAttachment(weaponView, 0, SWT.TOP);
            classData.left = new FormAttachment(weaponView, 5);
            classView.setLayoutData(classData);

            enemyView = new EnemyBuffsView(this);
            enemyView.setSize(200, 200);

            FormData enemyData = new FormData();
            enemyData.top = new FormAttachment(classView, 5);
            enemyData.left = new FormAttachment(classView, 0, SWT.LEFT);
            enemyData.right = new FormAttachment(classView, 0, SWT.RIGHT);
            enemyData.bottom = new FormAttachment(100, -10);
            enemyView.setLayoutData(enemyData);

            recruitView = new RecruitmentView(this, type);
            recruitView.setSize(200, 200);

            FormData recruitData = new FormData();
            recruitData.top = new FormAttachment(classView, 0, SWT.TOP);
            recruitData.left = new FormAttachment(classView, 5);
            recruitView.setLayoutData(recruitData);

            characterShufflingView = new CharacterShufflingView(this, type);
            characterShufflingView.setSize(200, 200);

            FormData characterShufflingData = new FormData();
            characterShufflingData.top = new FormAttachment(recruitView, 0, SWT.TOP);
            characterShufflingData.left = new FormAttachment(recruitView, 5);
            characterShufflingData.right = new FormAttachment(100, 0);
            characterShufflingView.setLayoutData(characterShufflingData);

            itemAssignmentView = new ItemAssignmentView(this, type);
            itemAssignmentView.setSize(200, 200);

            FormData itemAssignData = new FormData();
            itemAssignData.top = new FormAttachment(characterShufflingView, 5);
            itemAssignData.left = new FormAttachment(characterShufflingView, 0, SWT.LEFT);
            itemAssignData.right = new FormAttachment(characterShufflingView, 0, SWT.RIGHT);
            itemAssignmentView.setLayoutData(itemAssignData);

        }
    }

    @Override
    public void preloadOptions(OptionRecorder.GBAOptionBundle bundle) {
        growthView.initialize(bundle.growths);
        baseView.initialize(bundle.bases);
        classView.initialize(bundle.classes);
        weaponView.initialize(bundle.weapons);
        enemyView.initialize(bundle.enemies);
        miscView.initialize(bundle.otherOptions);
        rewardView.initialize(bundle.rewards);

        // GBA Specific
        otherCharOptionView.initialize(bundle.other);
        recruitView.initialize(bundle.recruitmentOptions);
        itemAssignmentView.initialize(bundle.itemAssignmentOptions);
        characterShufflingView.initialize(bundle.characterShufflingOptions, type);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.GBAOptionBundle bundle) {
        bundle.growths = growthView.getOptions();
        bundle.bases = baseView.getOptions();
        bundle.otherOptions = miscView.getOptions();
        bundle.rewards = rewardView.getOptions();
        bundle.classes = classView.getOptions();
        bundle.enemies = enemyView.getOptions();
        bundle.weapons = weaponView.getOptions();

        // GBA specific
        bundle.other = otherCharOptionView.getOptions();
        bundle.itemAssignmentOptions = itemAssignmentView.getOptions();
        bundle.recruitmentOptions = recruitView.getOptions();
        bundle.characterShufflingOptions = characterShufflingView.getOptions();
    }

    @Override
    public void preloadOptions(OptionRecorder.FE4OptionBundle bundle) {
        growthView.initialize(bundle.growths);
        baseView.initialize(bundle.bases);
        miscView.initialize(bundle.mechanics);
        rewardView.initialize(bundle.rewards);

        // FE4 Specific
        skillsView.initialize(bundle.skills);
        fe4ClassView.initialize(bundle.classes);
        fe4EnemyBuffView.initialize(bundle.enemyBuff);
        fe4PromotionView.initialize(bundle.promo);
        holyBloodView.initialize(bundle.holyBlood);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        bundle.growths = growthView.getOptions();
        bundle.bases = baseView.getOptions();
        bundle.mechanics = miscView.getOptions();
        bundle.rewards = rewardView.getOptions();

        // FE4 specific
        bundle.skills = skillsView.getOptions();
        bundle.enemyBuff = fe4EnemyBuffView.getOptions();
        bundle.holyBlood = holyBloodView.getOptions();
        bundle.classes = fe4ClassView.getOptions();
        bundle.promo = fe4PromotionView.getOptions();
    }

    @Override
    public void preloadOptions(OptionRecorder.FE9OptionBundle bundle) {
        growthView.initialize(bundle.growths);
        baseView.initialize(bundle.bases);
        miscView.initialize(bundle.mechanics);
        rewardView.initialize(bundle.rewards);
        weaponView.initialize(bundle.weapons);

        // FE9 Specific
        conAffinityView.initialize(bundle.otherOptions);
        fe9SkillView.initialize(bundle.skills);
        fe9ClassesView.initialize(bundle.classes);
        fe9EnemyView.initialize(bundle.enemyBuff);
    }

    @Override
    public void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        bundle.growths = growthView.getOptions();
        bundle.bases = baseView.getOptions();
        bundle.weapons = weaponView.getOptions();
        bundle.mechanics = miscView.getOptions();
        bundle.rewards = rewardView.getOptions();

        // FE9 specific
        bundle.enemyBuff = fe9EnemyView.getOptions();
        bundle.otherOptions = conAffinityView.getOptions();
        bundle.skills = fe9SkillView.getOptions();
        bundle.classes = fe9ClassesView.getOptions();
    }
}
