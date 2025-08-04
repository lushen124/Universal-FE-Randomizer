package ui;

import fedata.gcnwii.fe9.FE9Data;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import ui.common.GuiUtil;
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
    private PrfView prfView;
    private StatboosterView statboosterView;
    private ShopView shopView;

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
        growthView = new GrowthsView(this, type.hasSTRMAGSplit(), type.isGBA());
        growthView.group.setSize(200, 200);

        FormData growthData = new FormData();
        growthData.top = new FormAttachment(0, 0);
        growthData.left = new FormAttachment(0, 0);
        growthView.group.setLayoutData(growthData);

        baseView = new BasesView(this, type);
        baseView.group.setSize(200, 200);

        FormData baseData = new FormData();
        baseData.top = new FormAttachment(growthView.group, 5);
        baseData.left = new FormAttachment(growthView.group, 0, SWT.LEFT);
        baseData.right = new FormAttachment(growthView.group, 0, SWT.RIGHT);
        baseView.group.setLayoutData(baseData);

        if (type == GameType.FE4) {
            composeFE4();
        } else if (type == GameType.FE9) {
            composeFE9();
        } else {
            composeGBA();
        }
    }

    private void composeFE9(){
        conAffinityView = new CONAffinityView(this);
        conAffinityView.group.setSize(200, 200);

        FormData conAffinityData = new FormData();
        conAffinityData.top = new FormAttachment(baseView.group, 5);
        conAffinityData.left = new FormAttachment(baseView.group, 0, SWT.LEFT);
        conAffinityData.right = new FormAttachment(baseView.group, 0, SWT.RIGHT);
        conAffinityView.group.setLayoutData(conAffinityData);

        rewardView = new RewardRandomizationView(this, type);
        rewardView.group.setSize(200, 200);

        FormData rewardData = new FormData();
        rewardData.top = new FormAttachment(conAffinityView.group, 5);
        rewardData.left = new FormAttachment(conAffinityView.group, 0, SWT.LEFT);
        rewardData.right = new FormAttachment(conAffinityView.group, 0, SWT.RIGHT);
        rewardView.group.setLayoutData(rewardData);
        
        miscView = new GameMechanicsView(this, type);
        miscView.group.setSize(200, 200);
        
        FormData miscData = new FormData();
        miscData.top = new FormAttachment(rewardView.group, 5);
        miscData.left = new FormAttachment(rewardView.group, 0, SWT.LEFT);
        miscData.right = new FormAttachment(rewardView.group, 0, SWT.RIGHT);
        miscView.group.setLayoutData(miscData);

        List<String> skills = FE9Data.Skill.allValidSkills.stream().map(FE9Data.Skill::getDisplayString).collect(Collectors.toList());
        fe9SkillView = new FE9SkillView(this, skills, 1);
        fe9SkillView.group.setSize(200, 200);

        FormData skillData = new FormData();
        skillData.top = new FormAttachment(growthView.group, 0, SWT.TOP);
        skillData.left = new FormAttachment(growthView.group, 5);
        skillData.bottom = new FormAttachment(100, -10);
        fe9SkillView.group.setLayoutData(skillData);

        weaponView = new WeaponsView(this, type, 1);
        weaponView.group.setSize(200, 200);

        FormData weaponData = new FormData();
        weaponData.top = new FormAttachment(growthView.group, 0, SWT.TOP);
        weaponData.left = new FormAttachment(fe9SkillView.group, 5);
        weaponView.group.setLayoutData(weaponData);

        fe9ClassesView = new FE9ClassesView(this);
        fe9ClassesView.group.setSize(200, 200);

        FormData classData = new FormData();
        classData.top = new FormAttachment(growthView.group, 0, SWT.TOP);
        classData.left = new FormAttachment(weaponView.group, 5);
        classData.right = new FormAttachment(100, -5);
        fe9ClassesView.group.setLayoutData(classData);

        fe9EnemyView = new FE9EnemyBuffView(this, false);
        fe9EnemyView.group.setSize(200, 200);

        FormData enemyData = new FormData();
        enemyData.top = new FormAttachment(fe9ClassesView.group, 5);
        enemyData.left = new FormAttachment(fe9ClassesView.group, 0, SWT.LEFT);
        enemyData.right = new FormAttachment(100, -5);
        fe9EnemyView.group.setLayoutData(enemyData);
    }

    private void composeGBA() {
        otherCharOptionView = new MOVCONAffinityView(this);
        otherCharOptionView.group.setSize(200, 200);

        FormData otherData = new FormData();
        otherData.top = new FormAttachment(baseView.group, 5);
        otherData.left = new FormAttachment(baseView.group, 0, SWT.LEFT);
        otherData.right = new FormAttachment(baseView.group, 0, SWT.RIGHT);
        otherCharOptionView.group.setLayoutData(otherData);

        miscView = new GameMechanicsView(this, type);
        miscView.group.setSize(200, 200);

        FormData miscData = new FormData();
        miscData.top = new FormAttachment(otherCharOptionView.group, 5);
        miscData.left = new FormAttachment(otherCharOptionView.group, 0, SWT.LEFT);
        miscData.right = new FormAttachment(otherCharOptionView.group, 0, SWT.RIGHT);
        miscView.group.setLayoutData(miscData);

        rewardView = new RewardRandomizationView(this, type);
        rewardView.group.setSize(200, 200);

        FormData rewardData = new FormData();
        rewardData.top = new FormAttachment(miscView.group, 5);
        rewardData.left = new FormAttachment(miscView.group, 0, SWT.LEFT);
        rewardData.right = new FormAttachment(miscView.group, 0, SWT.RIGHT);
        rewardView.group.setLayoutData(rewardData);

        weaponView = new WeaponsView(this, type, 1);
        weaponView.group.setSize(200, 200);

        FormData weaponData = new FormData();
        weaponData.top = new FormAttachment(growthView.group, 0, SWT.TOP);
        weaponData.left = new FormAttachment(growthView.group, 5);
        weaponData.bottom = new FormAttachment(100, -10);
        weaponView.group.setLayoutData(weaponData);

        classView = new ClassesView(this, type);
        classView.group.setSize(200, 200);

        FormData classData = new FormData();
        classData.top = new FormAttachment(weaponView.group, 0, SWT.TOP);
        classData.left = new FormAttachment(weaponView.group, 5);
        classData.width = GuiUtil.DEFAULT_ITEM_WIDTH_300;
        classView.group.setLayoutData(classData);

        enemyView = new EnemyBuffsView(this);
        enemyView.group.setSize(200, 200);

        FormData enemyData = new FormData();
        enemyData.top = new FormAttachment(classView.group, 5);
        enemyData.left = new FormAttachment(classView.group, 0, SWT.LEFT);
        enemyData.right = new FormAttachment(classView.group, 0, SWT.RIGHT);
        enemyData.bottom = new FormAttachment(100, -10);
        enemyView.group.setLayoutData(enemyData);

        recruitView = new RecruitmentView(this, type);
        recruitView.group.setSize(200, 200);

        FormData recruitData = new FormData();
        recruitData.top = new FormAttachment(classView.group, 0, SWT.TOP);
        recruitData.left = new FormAttachment(classView.group, 5);
        recruitView.group.setLayoutData(recruitData);
        
        shopView = new ShopView(this);
        shopView.group.setSize(200, 200);
        
        FormData shopData = new FormData();
        shopData.top = new FormAttachment(recruitView.group, 5);
        shopData.left = new FormAttachment(recruitView.group, 0, SWT.LEFT);
        shopData.right = new FormAttachment(recruitView.group, 0, SWT.RIGHT);
        shopView.group.setLayoutData(shopData);

        characterShufflingView = new CharacterShufflingView(this, type);
        characterShufflingView.group.setSize(200, 200);

        FormData characterShufflingData = new FormData();
        characterShufflingData.top = new FormAttachment(recruitView.group, 0, SWT.TOP);
        characterShufflingData.left = new FormAttachment(recruitView.group, 5);
        characterShufflingData.right = new FormAttachment(100, 0);
        characterShufflingView.group.setLayoutData(characterShufflingData);

        itemAssignmentView = new ItemAssignmentView(this, type);
        itemAssignmentView.group.setSize(200, 200);

        FormData itemAssignData = new FormData();
        itemAssignData.top = new FormAttachment(characterShufflingView.group, 5);
        itemAssignData.left = new FormAttachment(characterShufflingView.group, 0, SWT.LEFT);
        itemAssignData.right = new FormAttachment(characterShufflingView.group, 0, SWT.RIGHT);
        itemAssignmentView.group.setLayoutData(itemAssignData);

        prfView = new PrfView(this);
        prfView.group.setSize(200, 200);

        FormData prfViewData = new FormData();
        prfViewData.top = new FormAttachment(itemAssignmentView.group, 5);
        prfViewData.left = new FormAttachment(itemAssignmentView.group, 0, SWT.LEFT);
        prfViewData.right = new FormAttachment(itemAssignmentView.group, 0, SWT.RIGHT);
        prfView.group.setLayoutData(prfViewData);
        
        statboosterView = new StatboosterView(this, SWT.NONE);
        statboosterView.setSize(200, 200);
        
        FormData statboosterData = new FormData();
        statboosterData.top = new FormAttachment(prfView.group, 5);
        statboosterData.left = new FormAttachment(prfView.group, 0, SWT.LEFT);
        statboosterData.right = new FormAttachment(prfView.group, 0, SWT.RIGHT);
        statboosterView.setLayoutData(statboosterData);
    }

    private void composeFE4() {
        // To prevent gen 2 overflow, the max growth allowed for any single stat is 85%.
        growthView.overrideMaxGrowthAllowed(85);

        holyBloodView = new HolyBloodView(this);
        holyBloodView.group.setSize(200, 200);

        FormData holyBloodData = new FormData();
        holyBloodData.top = new FormAttachment(baseView.group, 5);
        holyBloodData.left = new FormAttachment(baseView.group, 0, SWT.LEFT);
        holyBloodData.right = new FormAttachment(baseView.group, 0, SWT.RIGHT);
        holyBloodData.bottom = new FormAttachment(100, -10);
        holyBloodView.group.setLayoutData(holyBloodData);

        skillsView = new FE4SkillsView(this, 1);
        skillsView.group.setSize(200, 200);

        FormData skillsData = new FormData();
        skillsData.top = new FormAttachment(growthView.group, 0, SWT.TOP);
        skillsData.left = new FormAttachment(growthView.group, 5);
        skillsData.bottom = new FormAttachment(100, -10);
        skillsView.group.setLayoutData(skillsData);

        fe4ClassView = new FE4ClassesView(this, 1);
        fe4ClassView.group.setSize(200, 200);

        FormData classData = new FormData();
        classData.top = new FormAttachment(skillsView.group, 0, SWT.TOP);
        classData.left = new FormAttachment(skillsView.group, 5);
        classData.bottom = new FormAttachment(100, -10);
        fe4ClassView.group.setLayoutData(classData);

        fe4PromotionView = new FE4PromotionView(this);
        fe4PromotionView.group.setSize(200, 200);

        FormData promoData = new FormData();
        promoData.top = new FormAttachment(fe4ClassView.group, 0, SWT.TOP);
        promoData.left = new FormAttachment(fe4ClassView.group, 5);
        promoData.right = new FormAttachment(100, -5);
        fe4PromotionView.group.setLayoutData(promoData);

        fe4EnemyBuffView = new FE4EnemyBuffView(this);
        fe4EnemyBuffView.group.setSize(200, 200);

        FormData buffData = new FormData();
        buffData.top = new FormAttachment(fe4PromotionView.group, 5);
        buffData.left = new FormAttachment(fe4PromotionView.group, 0, SWT.LEFT);
        buffData.right = new FormAttachment(fe4PromotionView.group, 0, SWT.RIGHT);
        fe4EnemyBuffView.group.setLayoutData(buffData);

        miscView = new GameMechanicsView(this, type);
        miscView.group.setSize(200, 200);

        FormData miscData = new FormData();
        miscData.top = new FormAttachment(fe4EnemyBuffView.group, 5);
        miscData.left = new FormAttachment(fe4EnemyBuffView.group, 0, SWT.LEFT);
        miscData.right = new FormAttachment(fe4EnemyBuffView.group, 0, SWT.RIGHT);
        //miscData.bottom = new FormAttachment(100, -10);
        miscView.group.setLayoutData(miscData);

        rewardView = new RewardRandomizationView(this, type);
        rewardView.group.setSize(200, 200);

        FormData rewardData = new FormData();
        rewardData.top = new FormAttachment(miscView.group, 5);
        rewardData.left = new FormAttachment(miscView.group, 0, SWT.LEFT);
        rewardData.right = new FormAttachment(miscView.group, 0, SWT.RIGHT);
        rewardView.group.setLayoutData(rewardData);
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
        shopView.initialize(bundle.shopOptions);

        // GBA Specific
        prfView.initialize(bundle.prfs);
        otherCharOptionView.initialize(bundle.other);
        recruitView.initialize(bundle.recruitmentOptions);
        itemAssignmentView.initialize(bundle.itemAssignmentOptions);
        characterShufflingView.initialize(bundle.characterShufflingOptions);
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
        bundle.shopOptions = shopView.getOptions();

        // GBA specific
        bundle.prfs = prfView.getOptions();
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
        rewardView.initialize(bundle.rewards);
        weaponView.initialize(bundle.weapons);
        miscView.initialize(bundle.mechanics);

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
        bundle.rewards = rewardView.getOptions();
        bundle.mechanics = miscView.getOptions();

        // FE9 specific
        bundle.enemyBuff = fe9EnemyView.getOptions();
        bundle.otherOptions = conAffinityView.getOptions();
        bundle.skills = fe9SkillView.getOptions();
        bundle.classes = fe9ClassesView.getOptions();
    }
}
