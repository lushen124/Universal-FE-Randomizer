package random.gba.randomizer.shuffling;

import java.util.*;

import fedata.gba.GBAFECharacterData.Affinity;
import fedata.gba.GBAFEStatDto;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEClassProvider;
import fedata.general.FEBase.GameType;
import random.gba.loader.ClassDataLoader;
import util.DebugPrinter;

/**
 * Class that Represents the necessary data and offers useful functions for
 * importing a character into another GBA Game
 */
public class GBACrossGameData {
	public String name;
	public String portraitPath;
	public String paletteString;
	public String description1;
	public String description2;
	public GBACrossGameDataBattlePalette battlePalette;
	public String characterClass;
	public boolean promoted;
	public int level;
	public GBAFEStatDto bases;
	public GBAFEStatDto growths;
	public int[] weaponRanks;
	public int constitution;
	public String affinity;
	public String originGame;
	public int eyeX;
	public int eyeY;
	public int mouthX;
	public int mouthY;
	
	/**
	 * If this is a non-null value, it means that the user wants the character to be fixed in that slot.
	 * If multiple characters reference the same fixed slot, only the first one will actually happen.
	 */
	public Integer forcedSlot;
	
	/**
	 * Constructor used for generating the files initially. No need to maintain this.
	 */
	public GBACrossGameData(String name, String portraitPath, String description1, String description2, GBACrossGameDataBattlePalette battlePalette,
			String paletteString, GBAFEClass characterClass, boolean promoted, int level, GBAFEStatDto bases, GBAFEStatDto growths,
			int[] weaponRanks, int constitution, String affinity, byte[] facialFeatureCoordinates) {
		this.name = name;
		this.portraitPath = portraitPath;
		this.description1 = description1;
		this.description2 = description2;
		this.battlePalette = battlePalette;
		this.characterClass = characterClass.name();
		this.level = level;
		this.bases = bases;
		this.growths = growths;
		this.weaponRanks = weaponRanks;
		this.constitution = constitution;
		this.paletteString = paletteString;
		this.mouthX = facialFeatureCoordinates[0];
		this.mouthY = facialFeatureCoordinates[1];
		if (facialFeatureCoordinates.length == 4) {

			this.eyeX = facialFeatureCoordinates[2];
			this.eyeY = facialFeatureCoordinates[3];
		}
		this.affinity = affinity;
	}

	/**
	 * I don't want to add classes such as Ephraim Master Lord back to the older
	 * games, so find somewhat equivalent classes f.e. Paladin for Promoted Ephraim
	 * Lord
	 */
	public static GBAFEClass getEquivalentClass(GameType targetGame, GBACrossGameData targetData, ClassDataLoader classData) {
		if (classMap.isEmpty()) {
			buildClassMap();
		}

		Optional<GBAFEClass> classOpt;
		// Find the appropriate Provider
		GBAFEClassProvider sourceGameProvider = getProviderByGame(targetGame, targetData.originGame);
		GBAFEClassProvider targetGameProvider = getProviderByGame(targetGame, "");
		String classToSubstitute = targetData.characterClass;
		// If the targetGame is the same as the source game (shouldn't really happen?)
		// then just return the class
		if (targetData.originGame.toUpperCase().equals(targetGame.name())) {
			DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING,
					"The Charcter originates from the Target game, can just find the class by name.");
			classOpt = getClassFromProviderByName(targetGameProvider, classToSubstitute);
			if (classOpt.isPresent()) {
				DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, "The Charcters target class was found.");
				return classOpt.get();
			}
		}
		// Try to find the class in the targetGame by name
		classOpt = getClassFromProviderByName(targetGameProvider, classToSubstitute);
		if (classOpt.isPresent() && !isExceptionCase(targetGameProvider, classOpt) && classData.isValidClass(classOpt.get().getID())) {
			DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING,
					"Could find the class from a naive search of the name in the target game.");
			return classOpt.get();
		}

        // Find the class in the origin game
        classOpt = getClassFromProviderByName(sourceGameProvider, classToSubstitute);

        // try to find a fitting replacement if the origin class could be found
        if (classOpt.isPresent()) {
            GBAFEClass classToSub = classOpt.get();
            GBAFEClass substituteClass = classMap.get(classToSub).get(targetGame);
            if (substituteClass != null) {
                return substituteClass;
            } else {
                DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING,
                        String.format("Couldn't find an equivalent for class %s in game %s.", classToSub.name(), targetGame));
            }
        }

        return FE6Data.CharacterClass.NONE;
    }

    /**
     * Double check exceptional cases after Naive search.
     * <p>
     * F.e. FE8 has a bard class, but that one has no animations and as such isn't
     * usable (since it apparently might freeze), make sure not to give that one out
     * and instead give out Dancer in the fixed mapping.
     */
    protected static boolean isExceptionCase(GBAFEClassProvider provider, Optional<GBAFEClass> classOpt) {
        GBAFEClass chosenClass = classOpt.get();
        if (provider instanceof FE8Data) {
            return (Arrays.asList(FE8Data.CharacterClass.BARD, // Lack of Magic Animations?
                            // These following female classes are too much of a pain to make work.
                            // They have the same animation as the male one anyway, don't have Promo Bonuses
                            // either.
                            FE8Data.CharacterClass.WYVERN_RIDER_F, FE8Data.CharacterClass.WYVERN_LORD_F,
                            FE8Data.CharacterClass.HERO_F, FE8Data.CharacterClass.SHAMAN_F, FE8Data.CharacterClass.DRUID_F)
                    .contains(chosenClass));
        } else if (provider instanceof FE7Data) {
            return (Arrays.asList(FE7Data.CharacterClass.CAVALIER_F, // Not a useable class
                    FE7Data.CharacterClass.MERCENARY_F, // Not a useable class
                    // These following female classes are too much of a pain to make work.
                    // Also they have the same animation as the male one anyway, so it's not worth
                    // for just he map sprite, don't have Promo Bonuses either.
                    FE7Data.CharacterClass.SHAMAN_F, FE7Data.CharacterClass.MYRMIDON_F, FE7Data.CharacterClass.DRUID_F,
                    FE7Data.CharacterClass.HERO_F, FE7Data.CharacterClass.KNIGHT_F, FE7Data.CharacterClass.GENERAL_F,
                    FE7Data.CharacterClass.WYVERNKNIGHT_F, FE7Data.CharacterClass.NOMAD_F,
                    FE7Data.CharacterClass.NOMADTROOPER_F

            ).contains(chosenClass));

        } else if (provider instanceof FE6Data) {
            return (Arrays.asList(FE6Data.CharacterClass.PALADIN_F, // Not a useable class
                    FE6Data.CharacterClass.CAVALIER_F, // Not a useable class
                    FE6Data.CharacterClass.MERCENARY_F // Not a useable class
            ).contains(chosenClass));

        }

        return false;
    }

    /**
     * Stream through the names of all the classes returned by the provider, and see
     * if any matches the targetClass Name, if so then return an optional of that
     * class
     */
    protected static Optional<GBAFEClass> getClassFromProviderByName(GBAFEClassProvider provider, String className) {
        List<GBAFEClass> allClassesList = Arrays.asList(provider.allClasses());
        return allClassesList.stream().filter(c -> c.name().equals(className.toUpperCase())).findFirst();
    }

    /**
     * Tries to find the GBAFEClassProvider of the given originGame, if this isn't
     * one of FE6, FE7 or FE8, then instead returns the classProvider for the given
     * TargetGame as a default.
     */
    protected static GBAFEClassProvider getProviderByGame(GameType targetGame, String originGame) {
        if (GameType.FE6.name().equals(originGame.toUpperCase())) {
            return FE6Data.classProvider;
        } else if (GameType.FE7.name().equals(originGame.toUpperCase())) {
            return FE7Data.classProvider;
        } else if (GameType.FE8.name().equals(originGame.toUpperCase())) {
            return FE8Data.classProvider;
        }

        return getProviderByGame(null, targetGame.name());
    }

    /**
     * Map used to decide which FE6/7/8 class will be replaced by which one in the
     * other games
     */
    private static final Map<GBAFEClass, Map<GameType, GBAFEClass>> classMap = new HashMap<>();

    private static Map<GameType, List<GameType>> sourceGameMap =
            Collections.unmodifiableMap(new HashMap() {{
                put(GameType.FE6, Arrays.asList(GameType.FE7, GameType.FE8));
                put(GameType.FE7, Arrays.asList(GameType.FE6, GameType.FE8));
                put(GameType.FE8, Arrays.asList(GameType.FE6, GameType.FE7));
            }});


    /**
     * Builds the class Map, should only be done once when a replacement class is
     * requested for the first time.
     */

    private static void buildClassMap() {
        //@formatter:off
		// FE6 Classes -> FE7 / FE8
		addEntry(GameType.FE6, FE6Data.CharacterClass.LORD, FE7Data.CharacterClass.LORD_ELIWOOD, FE8Data.CharacterClass.MERCENARY);
		addEntry(GameType.FE6, FE6Data.CharacterClass.FIGHTER, FE7Data.CharacterClass.FIGHTER, FE8Data.CharacterClass.FIGHTER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.PEGASUS_KNIGHT, FE7Data.CharacterClass.PEGASUSKNIGHT, FE8Data.CharacterClass.PEGASUS_KNIGHT);
		addEntry(GameType.FE6, FE6Data.CharacterClass.FALCON_KNIGHT, FE7Data.CharacterClass.FALCONKNIGHT, FE8Data.CharacterClass.FALCON_KNIGHT);
		addEntry(GameType.FE6, FE6Data.CharacterClass.CAVALIER, FE7Data.CharacterClass.CAVALIER, FE8Data.CharacterClass.CAVALIER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.PALADIN, FE7Data.CharacterClass.PALADIN, FE8Data.CharacterClass.PALADIN);
		addEntry(GameType.FE6, FE6Data.CharacterClass.KNIGHT, FE7Data.CharacterClass.KNIGHT, FE8Data.CharacterClass.KNIGHT);
		addEntry(GameType.FE6, FE6Data.CharacterClass.MERCENARY, FE7Data.CharacterClass.MERCENARY, FE8Data.CharacterClass.MERCENARY);
		addEntry(GameType.FE6, FE6Data.CharacterClass.MYRMIDON, FE7Data.CharacterClass.MYRMIDON, FE8Data.CharacterClass.MYRMIDON);
		addEntry(GameType.FE6, FE6Data.CharacterClass.MYRMIDON_F, FE7Data.CharacterClass.MYRMIDON, FE8Data.CharacterClass.MYRMIDON_F);
		addEntry(GameType.FE6, FE6Data.CharacterClass.THIEF_F, FE7Data.CharacterClass.THIEF, FE8Data.CharacterClass.THIEF);
		addEntry(GameType.FE6, FE6Data.CharacterClass.PRIEST, FE7Data.CharacterClass.CLERIC, FE8Data.CharacterClass.PRIEST);
		addEntry(GameType.FE6, FE6Data.CharacterClass.TROUBADOUR, FE7Data.CharacterClass.TROUBADOUR, FE8Data.CharacterClass.TROUBADOUR);
		addEntry(GameType.FE6, FE6Data.CharacterClass.SAGE, FE7Data.CharacterClass.SAGE, FE8Data.CharacterClass.SAGE);
		addEntry(GameType.FE6, FE6Data.CharacterClass.SNIPER, FE7Data.CharacterClass.SNIPER, FE8Data.CharacterClass.SNIPER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.ARCHER_F, FE7Data.CharacterClass.ARCHER_F, FE8Data.CharacterClass.ARCHER_F);
		addEntry(GameType.FE6, FE6Data.CharacterClass.SHAMAN, FE7Data.CharacterClass.SHAMAN, FE8Data.CharacterClass.SHAMAN);
		addEntry(GameType.FE6, FE6Data.CharacterClass.SHAMAN_F, FE7Data.CharacterClass.SHAMAN, FE8Data.CharacterClass.SHAMAN);
		addEntry(GameType.FE6, FE6Data.CharacterClass.DRUID, FE7Data.CharacterClass.DRUID, FE8Data.CharacterClass.DRUID);
		addEntry(GameType.FE6, FE6Data.CharacterClass.DRUID_F, FE7Data.CharacterClass.DRUID, FE8Data.CharacterClass.DRUID);
		addEntry(GameType.FE6, FE6Data.CharacterClass.CLERIC, FE7Data.CharacterClass.CLERIC, FE8Data.CharacterClass.CLERIC);
		addEntry(GameType.FE6, FE6Data.CharacterClass.WYVERN_RIDER, FE7Data.CharacterClass.WYVERNKNIGHT, FE8Data.CharacterClass.WYVERN_RIDER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.WYVERN_RIDER_F, FE7Data.CharacterClass.WYVERNKNIGHT, FE8Data.CharacterClass.WYVERN_RIDER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.NOMAD, FE7Data.CharacterClass.NOMAD, FE8Data.CharacterClass.ARCHER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.NOMAD_F, FE7Data.CharacterClass.NOMAD, FE8Data.CharacterClass.ARCHER_F);
		addEntry(GameType.FE6, FE6Data.CharacterClass.NOMAD_TROOPER, FE7Data.CharacterClass.NOMADTROOPER, FE8Data.CharacterClass.RANGER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.NOMAD_TROOPER_F, FE7Data.CharacterClass.NOMADTROOPER_F, FE8Data.CharacterClass.RANGER_F);
		addEntry(GameType.FE6, FE6Data.CharacterClass.BISHOP, FE7Data.CharacterClass.BISHOP, FE8Data.CharacterClass.BISHOP);
		addEntry(GameType.FE6, FE6Data.CharacterClass.SWORDMASTER, FE7Data.CharacterClass.SWORDMASTER, FE8Data.CharacterClass.SWORDMASTER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.SWORDMASTER_F, FE7Data.CharacterClass.SWORDMASTER_F, FE8Data.CharacterClass.SWORDMASTER_F);
		addEntry(GameType.FE6, FE6Data.CharacterClass.GENERAL, FE7Data.CharacterClass.GENERAL, FE8Data.CharacterClass.GENERAL);
		addEntry(GameType.FE6, FE6Data.CharacterClass.BARD, FE7Data.CharacterClass.BARD, FE8Data.CharacterClass.DANCER);
		addEntry(GameType.FE6, FE6Data.CharacterClass.PIRATE, FE7Data.CharacterClass.PIRATE, FE8Data.CharacterClass.PIRATE);
		addEntry(GameType.FE6, FE6Data.CharacterClass.WARRIOR, FE7Data.CharacterClass.WARRIOR, FE8Data.CharacterClass.WARRIOR);
		addEntry(GameType.FE6, FE6Data.CharacterClass.HERO, FE7Data.CharacterClass.HERO, FE8Data.CharacterClass.HERO);
		addEntry(GameType.FE6, FE6Data.CharacterClass.HERO_F, FE7Data.CharacterClass.HERO, FE8Data.CharacterClass.HERO);
		addEntry(GameType.FE6, FE6Data.CharacterClass.KING, FE7Data.CharacterClass.GENERAL, FE8Data.CharacterClass.GENERAL);
		addEntry(GameType.FE6, FE6Data.CharacterClass.WYVERN_KNIGHT, FE7Data.CharacterClass.WYVERNLORD, FE8Data.CharacterClass.WYVERN_LORD);
		addEntry(GameType.FE6, FE6Data.CharacterClass.MANAKETE_F, FE7Data.CharacterClass.DANCER, FE8Data.CharacterClass.MANAKETE_F);
		addEntry(GameType.FE6, FE6Data.CharacterClass.KNIGHT_F, FE7Data.CharacterClass.KNIGHT, FE8Data.CharacterClass.KNIGHT_F);
		addEntry(GameType.FE6, FE6Data.CharacterClass.ARCHER, FE7Data.CharacterClass.ARCHER, FE8Data.CharacterClass.ARCHER);
		
		// FE7 Classes -> FE6 / FE8
		addEntry(GameType.FE7, FE7Data.CharacterClass.LORD_LYN, FE6Data.CharacterClass.MYRMIDON_F, FE8Data.CharacterClass.MYRMIDON);
		addEntry(GameType.FE7, FE7Data.CharacterClass.LORD_HECTOR, FE6Data.CharacterClass.KNIGHT, FE8Data.CharacterClass.KNIGHT);
		addEntry(GameType.FE7, FE7Data.CharacterClass.LORD_ELIWOOD, FE6Data.CharacterClass.MERCENARY, FE8Data.CharacterClass.MERCENARY);
		addEntry(GameType.FE7, FE7Data.CharacterClass.FIGHTER, FE6Data.CharacterClass.FIGHTER, FE8Data.CharacterClass.FIGHTER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.UBER_SAGE, FE6Data.CharacterClass.SAGE, FE8Data.CharacterClass.SAGE);
		addEntry(GameType.FE7, FE7Data.CharacterClass.PEGASUSKNIGHT, FE6Data.CharacterClass.PEGASUS_KNIGHT, FE8Data.CharacterClass.PEGASUS_KNIGHT);
		addEntry(GameType.FE7, FE7Data.CharacterClass.CAVALIER, FE6Data.CharacterClass.CAVALIER, FE8Data.CharacterClass.CAVALIER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.CAVALIER_F, FE6Data.CharacterClass.CAVALIER, FE8Data.CharacterClass.CAVALIER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.PALADIN, FE6Data.CharacterClass.PALADIN, FE8Data.CharacterClass.PALADIN);
		addEntry(GameType.FE7, FE7Data.CharacterClass.PALADIN_F, FE6Data.CharacterClass.PALADIN, FE8Data.CharacterClass.PALADIN_F);
		addEntry(GameType.FE7, FE7Data.CharacterClass.KNIGHT, FE6Data.CharacterClass.KNIGHT, FE8Data.CharacterClass.KNIGHT);
		addEntry(GameType.FE7, FE7Data.CharacterClass.MERCENARY, FE6Data.CharacterClass.MERCENARY, FE8Data.CharacterClass.MERCENARY);
		addEntry(GameType.FE7, FE7Data.CharacterClass.MYRMIDON, FE6Data.CharacterClass.MYRMIDON, FE8Data.CharacterClass.MYRMIDON);
		addEntry(GameType.FE7, FE7Data.CharacterClass.MYRMIDON_F, FE6Data.CharacterClass.MYRMIDON_F, FE8Data.CharacterClass.MYRMIDON_F);
		addEntry(GameType.FE7, FE7Data.CharacterClass.MAGE_F, FE6Data.CharacterClass.MAGE_F, FE8Data.CharacterClass.MAGE_F);
		addEntry(GameType.FE7, FE7Data.CharacterClass.MONK, FE6Data.CharacterClass.PRIEST, FE8Data.CharacterClass.MONK);
		addEntry(GameType.FE7, FE7Data.CharacterClass.TROUBADOUR, FE6Data.CharacterClass.TROUBADOUR, FE8Data.CharacterClass.TROUBADOUR);
		addEntry(GameType.FE7, FE7Data.CharacterClass.SAGE, FE6Data.CharacterClass.SAGE, FE8Data.CharacterClass.SAGE);
		addEntry(GameType.FE7, FE7Data.CharacterClass.SNIPER, FE6Data.CharacterClass.SNIPER, FE8Data.CharacterClass.SNIPER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.SNIPER_F, FE6Data.CharacterClass.SNIPER_F, FE8Data.CharacterClass.SNIPER_F);
		addEntry(GameType.FE7, FE7Data.CharacterClass.ARCHER, FE6Data.CharacterClass.ARCHER, FE8Data.CharacterClass.ARCHER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.ARCHER_F, FE6Data.CharacterClass.SNIPER_F, FE8Data.CharacterClass.SNIPER_F);
		addEntry(GameType.FE7, FE7Data.CharacterClass.SHAMAN, FE6Data.CharacterClass.SHAMAN, FE8Data.CharacterClass.SHAMAN);
		addEntry(GameType.FE7, FE7Data.CharacterClass.WYVERNKNIGHT, FE6Data.CharacterClass.WYVERN_RIDER, FE8Data.CharacterClass.WYVERN_KNIGHT);
		addEntry(GameType.FE7, FE7Data.CharacterClass.ASSASSIN, FE6Data.CharacterClass.SWORDMASTER, FE8Data.CharacterClass.ASSASSIN);
		addEntry(GameType.FE7, FE7Data.CharacterClass.NOMAD, FE6Data.CharacterClass.NOMAD, FE8Data.CharacterClass.ARCHER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.NOMADTROOPER, FE6Data.CharacterClass.NOMAD_TROOPER, FE8Data.CharacterClass.RANGER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.NOMADTROOPER_F, FE6Data.CharacterClass.NOMAD_TROOPER_F, FE8Data.CharacterClass.RANGER_F);
		addEntry(GameType.FE7, FE7Data.CharacterClass.BISHOP, FE6Data.CharacterClass.BISHOP, FE8Data.CharacterClass.BISHOP);
		addEntry(GameType.FE7, FE7Data.CharacterClass.SWORDMASTER, FE6Data.CharacterClass.SWORDMASTER, FE8Data.CharacterClass.SWORDMASTER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.SWORDMASTER_F, FE6Data.CharacterClass.SWORDMASTER_F, FE8Data.CharacterClass.SWORDMASTER_F);
		addEntry(GameType.FE7, FE7Data.CharacterClass.GENERAL, FE6Data.CharacterClass.GENERAL, FE8Data.CharacterClass.GENERAL);
		addEntry(GameType.FE7, FE7Data.CharacterClass.BARD, FE6Data.CharacterClass.BARD, FE8Data.CharacterClass.DANCER);
		addEntry(GameType.FE7, FE7Data.CharacterClass.PIRATE, FE6Data.CharacterClass.PIRATE, FE8Data.CharacterClass.PIRATE);
		addEntry(GameType.FE7, FE7Data.CharacterClass.WARRIOR, FE6Data.CharacterClass.WARRIOR, FE8Data.CharacterClass.WARRIOR);
		addEntry(GameType.FE7, FE7Data.CharacterClass.WYVERNLORD_F, FE6Data.CharacterClass.WYVERN_KNIGHT_F, FE8Data.CharacterClass.WYVERN_LORD);
		addEntry(GameType.FE7, FE7Data.CharacterClass.HERO, FE6Data.CharacterClass.HERO, FE8Data.CharacterClass.HERO);

		// FE8 Classes -> FE6 / FE7
		addEntry(GameType.FE8, FE8Data.CharacterClass.FIGHTER, FE6Data.CharacterClass.FIGHTER, FE7Data.CharacterClass.FIGHTER);
		addEntry(GameType.FE8, FE8Data.CharacterClass.TRAINEE, FE6Data.CharacterClass.FIGHTER, FE7Data.CharacterClass.FIGHTER);
		addEntry(GameType.FE8, FE8Data.CharacterClass.EPHRAIM_LORD, FE6Data.CharacterClass.SOLDIER, FE7Data.CharacterClass.SOLDIER);
		addEntry(GameType.FE8, FE8Data.CharacterClass.RECRUIT, FE6Data.CharacterClass.SOLDIER, FE7Data.CharacterClass.SOLDIER);
		addEntry(GameType.FE8, FE8Data.CharacterClass.EIRIKA_LORD, FE6Data.CharacterClass.MYRMIDON_F, FE7Data.CharacterClass.LORD_LYN);
		addEntry(GameType.FE8, FE8Data.CharacterClass.PEGASUS_KNIGHT, FE6Data.CharacterClass.PEGASUS_KNIGHT, FE7Data.CharacterClass.PEGASUSKNIGHT);
		addEntry(GameType.FE8, FE8Data.CharacterClass.MANAKETE_F, FE6Data.CharacterClass.MANAKETE_F, FE7Data.CharacterClass.DANCER);
		addEntry(GameType.FE8, FE8Data.CharacterClass.CAVALIER, FE6Data.CharacterClass.CAVALIER, FE7Data.CharacterClass.CAVALIER);
		addEntry(GameType.FE8, FE8Data.CharacterClass.CAVALIER_F, FE6Data.CharacterClass.CAVALIER, FE7Data.CharacterClass.CAVALIER);
		addEntry(GameType.FE8, FE8Data.CharacterClass.GREAT_KNIGHT, FE6Data.CharacterClass.GENERAL, FE7Data.CharacterClass.GENERAL);
		addEntry(GameType.FE8, FE8Data.CharacterClass.KNIGHT, FE6Data.CharacterClass.KNIGHT, FE7Data.CharacterClass.KNIGHT);
		addEntry(GameType.FE8, FE8Data.CharacterClass.MERCENARY, FE6Data.CharacterClass.MERCENARY, FE7Data.CharacterClass.MERCENARY);
		addEntry(GameType.FE8, FE8Data.CharacterClass.MERCENARY_F, FE6Data.CharacterClass.MERCENARY, FE7Data.CharacterClass.MERCENARY);
		addEntry(GameType.FE8, FE8Data.CharacterClass.MYRMIDON, FE6Data.CharacterClass.MYRMIDON, FE7Data.CharacterClass.MYRMIDON);
		addEntry(GameType.FE8, FE8Data.CharacterClass.MYRMIDON_F, FE6Data.CharacterClass.MYRMIDON_F, FE7Data.CharacterClass.MYRMIDON);
		addEntry(GameType.FE8, FE8Data.CharacterClass.PUPIL, FE6Data.CharacterClass.MAGE, FE7Data.CharacterClass.MAGE);
		addEntry(GameType.FE8, FE8Data.CharacterClass.MAGE, FE6Data.CharacterClass.MAGE, FE7Data.CharacterClass.MAGE);
		addEntry(GameType.FE8, FE8Data.CharacterClass.MAGE_F, FE6Data.CharacterClass.MAGE_F, FE7Data.CharacterClass.MAGE_F);
		addEntry(GameType.FE8, FE8Data.CharacterClass.PRIEST, FE6Data.CharacterClass.PRIEST, FE7Data.CharacterClass.CLERIC);
		addEntry(GameType.FE8, FE8Data.CharacterClass.MONK, FE6Data.CharacterClass.PRIEST, FE7Data.CharacterClass.MONK);
		addEntry(GameType.FE8, FE8Data.CharacterClass.TROUBADOUR, FE6Data.CharacterClass.TROUBADOUR, FE7Data.CharacterClass.TROUBADOUR);
		addEntry(GameType.FE8, FE8Data.CharacterClass.SAGE, FE6Data.CharacterClass.SAGE, FE7Data.CharacterClass.SAGE);
		addEntry(GameType.FE8, FE8Data.CharacterClass.SNIPER, FE6Data.CharacterClass.SNIPER, FE7Data.CharacterClass.SNIPER);
		addEntry(GameType.FE8, FE8Data.CharacterClass.ARCHER_F, FE6Data.CharacterClass.ARCHER_F, FE7Data.CharacterClass.ARCHER_F);
		addEntry(GameType.FE8, FE8Data.CharacterClass.SHAMAN, FE6Data.CharacterClass.SHAMAN, FE7Data.CharacterClass.SHAMAN);
		addEntry(GameType.FE8, FE8Data.CharacterClass.CLERIC, FE6Data.CharacterClass.CLERIC, FE7Data.CharacterClass.CLERIC);
		addEntry(GameType.FE8, FE8Data.CharacterClass.WYVERN_RIDER, FE6Data.CharacterClass.WYVERN_RIDER, FE7Data.CharacterClass.WYVERNKNIGHT);
		addEntry(GameType.FE8, FE8Data.CharacterClass.ROGUE, FE6Data.CharacterClass.SWORDMASTER, FE7Data.CharacterClass.ASSASSIN);
		addEntry(GameType.FE8, FE8Data.CharacterClass.FALCON_KNIGHT, FE6Data.CharacterClass.FALCON_KNIGHT, FE7Data.CharacterClass.FALCONKNIGHT);
	}

	//@formatter:on

	/**
	 * Add an entry to the class map. the parameters must ensure that the passed to
	 * parameters are in game order, as we will infer the target game based on the
	 * position (using the sourceGameMap).
	 * 
	 * @param sourceGame The game (of FE6,7,8) that the source class is from.
	 * @param source     the source class to put as a key in the map
	 * 
	 * @param to1        the replacement class in the first other game (f.e. if the
	 *                   source class is FE6, this MUST be the FE7 equivalent or if
	 *                   Source is FE7 then this is FE6)
	 * 
	 * @param to2        the replacement class in the second other game (f.e. if the
	 *                   source class is FE6, this MUST be the FE8 equivalent or if
	 *                   Source is FE8 then this is FE7)
	 */
	private static void addEntry(GameType sourceGame, GBAFEClass source, GBAFEClass to1, GBAFEClass to2) {
		List<GameType> sourceGames = sourceGameMap.get(sourceGame);
		Map<GameType, GBAFEClass> inner = new HashMap<>();
		inner.put(sourceGames.get(0), to1);
		inner.put(sourceGames.get(1), to2);
		classMap.put(source, Collections.unmodifiableMap(inner));
	}

}