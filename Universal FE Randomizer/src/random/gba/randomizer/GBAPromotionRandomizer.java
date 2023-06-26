package random.gba.randomizer;

import java.util.*;
import java.util.stream.Collectors;

import fedata.gba.GBAFEClassData;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.fe8.FE8Data.CharacterClass;
import fedata.gba.fe8.PromotionBranch;
import fedata.gba.general.GBAFEClass;
import fedata.general.FEBase.GameType;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.PromotionDataLoader;
import random.general.PoolDistributor;
import ui.model.PromotionOptions;
import ui.model.PromotionOptions.Mode;

/**
 * Class responsible to perform the Promotion Randomization for GBAFE
 */
public class GBAPromotionRandomizer {
    static final int rngSalt = 879845164;

    private static Map<GBAFEClass, GBAFEClassData> classMap;

    public static void randomizePromotions(PromotionOptions options, PromotionDataLoader promotionData,
                                           ClassDataLoader classData, GameType type, Random rng) {
        classMap = getClassMapForGame(options, classData, type);

        switch (type) {
            case FE6:
            case FE7:
                if (options.promotionMode.equals(Mode.STRICT)) {
                    // Keep normal promotions, except letting Soldiers promote.
                    GBAFEClassData soldierClassData = classMap.get(
                            type.equals(GameType.FE6) ? FE6Data.CharacterClass.SOLDIER : FE7Data.CharacterClass.SOLDIER);
                    soldierClassData.setTargetPromotionID(type.equals(GameType.FE6) ? FE6Data.CharacterClass.GENERAL.ID
                            : FE7Data.CharacterClass.GENERAL.ID);
                    break;
                }
                randomizePromotions(options, classData, type, rng);
                break;
            case FE8:
                if (options.promotionMode.equals(Mode.STRICT)) {
                    Map<CharacterClass, PromotionBranch> promotionBranches = promotionData.getAllPromotionBranches();
                    PromotionBranch soldierPromotions = promotionBranches.get(FE8Data.CharacterClass.SOLDIER);
                    soldierPromotions.setFirstPromotion(FE8Data.CharacterClass.GENERAL.ID);
                    soldierPromotions.setSecondPromotion(FE8Data.CharacterClass.PALADIN.ID);
                    promotionBranches.put(FE8Data.CharacterClass.SOLDIER, soldierPromotions);
                    break;
                }
                randomizePromotionsForFE8(options, promotionData, classData, rng);
                break;
            default:
        }

    }

    /**
     * Due to branched Promotions FE8 needs to be handled much different from FE6
     * and FE7.
     */
    public static void randomizePromotionsForFE8(PromotionOptions options, PromotionDataLoader promotionData,
                                                 ClassDataLoader classData, Random rng) {
        // Get the promotion table
        Map<FE8Data.CharacterClass, PromotionBranch> promotionBranches = promotionData.getAllPromotionBranches();

        List<FE8Data.CharacterClass> classesNeedingPromotions = new ArrayList<>();
        classesNeedingPromotions.addAll(FE8Data.CharacterClass.allUnpromotedClasses);
        if (!options.allowMonsterClasses) {
           classesNeedingPromotions.removeAll(CharacterClass.allMonsterClasses);
        }

        // for each entry pick new promotions
        for (FE8Data.CharacterClass classToRandomize : classesNeedingPromotions) {
            // gather all the valid promotions based on the options
            List<GBAFEClass> promotions = getValidPromotionsForClass(options, classData, classToRandomize,
                    GameType.FE8);

            // If there aren't enough promotions to satisfy the branch, keep it vanilla
            if (promotions == null || promotions.isEmpty() || promotions.size() == 1) {
                continue;
            }
            PoolDistributor<GBAFEClass> promotionDistributor = new PoolDistributor<>();
            promotionDistributor.addAll(promotions);
            if (!classToRandomize.equals(FE8Data.CharacterClass.DANCER)) {
                PromotionBranch promotionBranch = promotionBranches.get(classToRandomize);
                GBAFEClass firstPromotion = promotionDistributor.getRandomItem(rng, true);
                GBAFEClass secondPromotion = promotionDistributor.getRandomItem(rng, true);

                // These classes could be equivalent (i.e. Swordmaster and Swordmaster_F), try
                // again.
                if (mightNeedReroll(firstPromotion, secondPromotion) && promotionDistributor.possibleResults().size() != 0) {
                    secondPromotion = promotionDistributor.getRandomItem(rng, true);
                }

                promotionBranch.setFirstPromotion(firstPromotion.getID());
                promotionBranch.setSecondPromotion(secondPromotion.getID());
                promotionBranches.put(classToRandomize, promotionBranch);
            } else {
                PromotionBranch promotionBranch = promotionBranches.get(classToRandomize);
                promotionBranch.setFirstPromotion(0);
                promotionBranch.setSecondPromotion(0);
                promotionBranches.put(classToRandomize, promotionBranch);
            }
        }
    }

    /**
     * Checks if the two given classes are either the exact same (e.g. SWORDMASTER
     * and SWORDMASTER) or Equivalent (SWORDMASTER and SWORDMASTER_F)
     */
    public static boolean mightNeedReroll(GBAFEClass firstPromotion, GBAFEClass secondPromotion) {
        String nameFirstPromo = firstPromotion.name().endsWith("_F")
                ? firstPromotion.name().substring(0, firstPromotion.name().length() - 2)
                : firstPromotion.name();

        String nameSecondPromo = firstPromotion.name().endsWith("_F")
                ? firstPromotion.name().substring(0, firstPromotion.name().length() - 2)
                : firstPromotion.name();

        return nameFirstPromo.equals(nameSecondPromo)
                && Arrays.asList(-1, 0, 1).contains(firstPromotion.getID() - secondPromotion.getID()); // The Female and Male classes are next to each other Id wise

    }

    /**
     * For FE6 & FE7
     * <p>
     * Figures out valid promotions for each class based on the given options and
     * sets their new promotions.
     */
    public static void randomizePromotions(PromotionOptions options, ClassDataLoader classData, GameType type,
                                           Random rng) {
        final String REGEX_SHOULD_NOT_PROMOTE = "^[DANCER|BARD]";
        Set<GBAFEClass> unpromotedClasses = new HashSet<>(
                type.equals(GameType.FE6) ? FE6Data.CharacterClass.allUnpromotedClasses
                        : FE7Data.CharacterClass.allUnpromotedClasses);

        // Get the classdata for all unpromoted classes that should promote (i.e. not
        // dancers)
        Map<GBAFEClass, GBAFEClassData> unpromotedClassDataMapping = classMap.entrySet()
                .stream()
                .filter(e -> unpromotedClasses.contains(e.getKey())
                        && !e.getKey().name().matches(REGEX_SHOULD_NOT_PROMOTE))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        for (GBAFEClass unpromotedClass : unpromotedClassDataMapping.keySet()) {
            PoolDistributor<GBAFEClass> promotionDistributor = new PoolDistributor<>();
            promotionDistributor.addAll(getValidPromotionsForClass(options, classData, unpromotedClass, type));
            GBAFEClassData classToEdit = unpromotedClassDataMapping.get(unpromotedClass);
            classToEdit.setTargetPromotionID(promotionDistributor.getRandomItem(rng, false).getID());
        }
    }

    /**
     * Given a character class returns all Classes that are valid promotions for the
     * given promotion requirements.
     */
    public static List<GBAFEClass> getValidPromotionsForClass(PromotionOptions options, ClassDataLoader classData,
                                                              GBAFEClass baseClass, GameType type) {
        List<GBAFEClass> validPromotions = classMap.keySet().stream().filter(candidate -> {
            // (A) Basic case, the class is on the wrong tier
            if (!isCorrectTier(baseClass, candidate, type)) {
                return false;
            }
            GBAFEClassData baseClassData = classMap.get(baseClass);
            GBAFEClassData candidateData = classMap.get(candidate);

            // (B) Check general damage type
            if (options.promotionMode.equals(Mode.RANDOM) && options.keepSameDamageType && !useSameDamageType(baseClassData, candidateData)) {
                return false;
            }

            // (C) Check specific weapon usage
            if (options.promotionMode.equals(Mode.RANDOM) && options.requireCommonWeapon && !hasAnyMatchingWeaponType(baseClassData, candidateData)) {
                return false;
            } else if (options.promotionMode.equals(Mode.LOOSE) && !canUseAllUnpromotedWeapons(baseClassData, candidateData)) {
                return false;
            }

            // (D) Check that the class has the correct mount if that is desired
            // If unit is a flier they should stay a flier.
            return !options.promotionMode.equals(Mode.LOOSE) || options.allowMountChanges || (classData.isFlying(baseClass.getID()) == classData.isFlying(candidate.getID()) &&
                    // If unit is a non-flying mounted unit keep them as such.
                    classData.isHorseUnit(baseClass.getID()) == classData.isHorseUnit(candidate.getID()));
        }).collect(Collectors.toList());

        return validPromotions;
    }


    /**
     * Checks that both classes have a magical rank, since in GBAFE there are no
     * mixed physical and special classes, we can just assume that both classes have
     * the same magic attributes
     */
    public static boolean useSameDamageType(GBAFEClassData baseClassData, GBAFEClassData candidateData) {
        boolean baseClassHasMagicRank = (baseClassData.getAnimaRank() + baseClassData.getDarkRank()
                + baseClassData.getLightRank() + baseClassData.getStaffRank()) > 0;

        boolean candidateClassHasMagicRank = (candidateData.getAnimaRank() + candidateData.getDarkRank()
                + candidateData.getLightRank() + candidateData.getStaffRank()) > 0;

        return baseClassHasMagicRank == candidateClassHasMagicRank;
    }

    /**
     * Returns true if the given classes have atleast one matching weapon type.
     */
    public static boolean hasAnyMatchingWeaponType(GBAFEClassData baseClass, GBAFEClassData candidate) {
        boolean ret = false;
        ret |= baseClass.getSwordRank() <= candidate.getSwordRank();
        ret |= baseClass.getLanceRank() <= candidate.getLanceRank();
        ret |= baseClass.getAxeRank() <= candidate.getAxeRank();
        ret |= baseClass.getBowRank() <= candidate.getBowRank();
        ret |= baseClass.getDarkRank() <= candidate.getDarkRank();
        ret |= baseClass.getLightRank() <= candidate.getLightRank();
        ret |= baseClass.getAnimaRank() <= candidate.getAnimaRank();
        ret |= baseClass.getStaffRank() <= candidate.getStaffRank();
        return ret;
    }

    /**
     * Checks that a given promotion candidate class can use all the weapons of the
     * unpromoted class (and more)
     * <p>
     * e.g. Mercenary -> Paladin = True
     * Cavalier -> Paladin = True
     * Cavalier -> Swordmaster = false
     */
    public static boolean canUseAllUnpromotedWeapons(GBAFEClassData baseClass, GBAFEClassData candidate) {
        boolean ret = true;
        ret &= baseClass.getSwordRank() <= candidate.getSwordRank();
        ret &= baseClass.getLanceRank() <= candidate.getLanceRank();
        ret &= baseClass.getAxeRank() <= candidate.getAxeRank();
        ret &= baseClass.getBowRank() <= candidate.getBowRank();
        ret &= baseClass.getDarkRank() <= candidate.getDarkRank();
        ret &= baseClass.getLightRank() <= candidate.getLightRank();
        ret &= baseClass.getAnimaRank() <= candidate.getAnimaRank();
        ret &= baseClass.getStaffRank() <= candidate.getStaffRank();

        return ret;
    }

    /**
     * Returns true if both of the classes are of the same tier (i.e. unpromoted, promoted or in FE8 trainee)
     */
    public static boolean isCorrectTier(GBAFEClass baseClass, GBAFEClass candidate, GameType type) {
        if (GameType.FE8.equals(type)) {
            FE8Data.CharacterClass castedBaseClass = (FE8Data.CharacterClass) baseClass;
            // In the case of FE8 we could have a trainee class, which will promote to an
            // unpromoted class.
            return (castedBaseClass.isTrainee() && !candidate.isPromoted())
                    || (!castedBaseClass.isPromoted() && !castedBaseClass.isTrainee() && candidate.isPromoted());
        }
        return !baseClass.isPromoted() && candidate.isPromoted();
    }

    /**
     * Builds a map between the FE6/7/8Data.CharacterClass Enum and the actual game
     * data, to figure out what are valid promotions for the Data.CharacterClass
     */
    public static Map<GBAFEClass, GBAFEClassData> getClassMapForGame(PromotionOptions options, ClassDataLoader classData, GameType type) {
        Map<Integer, GBAFEClassData> classes = classData.getClassMap();
        Map<GBAFEClass, GBAFEClassData> ret = new HashMap<>();
        switch (type) {
            case FE6:
                FE6Data.CharacterClass.allValidClasses.stream().forEach(c -> ret.put(c, classes.get(c.ID)));
                if (PromotionOptions.Mode.RANDOM.equals(options.promotionMode) || options.allowEnemyOnlyPromotedClasses) {
                    ret.put(FE6Data.CharacterClass.KING, classes.get(FE6Data.CharacterClass.KING));
                }
                break;
            case FE7:
                FE7Data.CharacterClass.allValidClasses.stream().forEach(c -> ret.put(c, classes.get(c.ID)));
                if (PromotionOptions.Mode.RANDOM.equals(options.promotionMode) || options.allowEnemyOnlyPromotedClasses) {
                    FE7Data.CharacterClass.allSpecialEnemyClasses.stream().forEach(c -> ret.put(c, classes.get(c.ID)));
                }
                break;
            case FE8:
                FE8Data.CharacterClass.allValidClasses.stream()
                        // Filter out monster classes if user doesn't want them
                        .filter(c -> options.allowMonsterClasses || !CharacterClass.allMonsterClasses.contains(c))
                        .forEach(c -> ret.put(c, classes.get(c.ID)));
                if (PromotionOptions.Mode.RANDOM.equals(options.promotionMode) || options.allowEnemyOnlyPromotedClasses) {
                    FE8Data.CharacterClass.allSpecialEnemyClasses.stream().forEach(c -> ret.put(c, classes.get(c.ID)));
                }
                break;
            default:
                // Do Nothing.
        }
        return ret;
    }
}
