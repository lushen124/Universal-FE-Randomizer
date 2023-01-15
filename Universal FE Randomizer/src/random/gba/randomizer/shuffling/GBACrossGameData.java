package random.gba.randomizer.shuffling;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;

import fedata.gba.GBAFEStatDto;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.gba.general.GBAFEClass;
import fedata.gba.general.GBAFEClassProvider;
import fedata.general.FEBase.GameType;
import util.DebugPrinter;

/**
 * Class that Represents the necessary data and offers useful functions for importing a character into another
 * GBA Game
 */
public class GBACrossGameData {
	public String name;
	public String portraitPath;
	public String paletteString;
	public String description1;
	public String description2;
	public String characterClass;
	public int level;
	public GBAFEStatDto bases;
	public GBAFEStatDto growths;
	public int[] weaponRanks;
	public int constitution;
	public String originGame;
	public int eyeX;
	public int eyeY;
	public int mouthX;
	public int mouthY;

	public GBACrossGameData(String name, String portraitPath, String description1, String description2,
			String paletteString, GBAFEClass characterClass, int level, GBAFEStatDto bases, GBAFEStatDto growths,
			int[] weaponRanks, int constitution, byte[] facialFeatureCoordinates) {
		this.name = name;
		this.portraitPath = portraitPath;
		this.description1 = description1;
		this.description2 = description2;
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
	}

	/**
	 * I don't want to add classes such as Ephraim Master Lord back to the older
	 * games, so find somewhat equivalent classes f.e. Paladin for Promoted Ephraim
	 * Lord
	 */
	public static GBAFEClass getEquivalentClass(GameType targetGame, GBACrossGameData targetData) {
		Optional<GBAFEClass> classOpt;
		// Find the appropriate Provider
		GBAFEClassProvider sourceGameProvider = getProviderByGame(targetGame, targetData.originGame);
		GBAFEClassProvider targetGameProvider = getProviderByGame(targetGame, "");
		String classToSubstitute = targetData.characterClass;
		// If the targetGame is the same as the source game (shouldn't really happen?)
		// then just return the class
		if (targetData.originGame.toUpperCase().equals(targetGame.name())) {
			DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, "The Charcter originates from the Target game, can just find the class by name.");
			classOpt = getClassFromProviderByName(targetGameProvider, classToSubstitute);
			if (classOpt.isPresent()) {
				DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, "The Charcters target class was found.");
				return classOpt.get();
			}
		}
		// Try to find the class in the targetGame by name
		classOpt = getClassFromProviderByName(targetGameProvider, classToSubstitute);
		if (classOpt.isPresent() && !isExceptionCase(targetGameProvider, classOpt)) {
			DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, "Could find the class from a naive search of the name in the target game.");
			return classOpt.get();
		}

		// Find the class in the origin game
		classOpt = getClassFromProviderByName(sourceGameProvider, classToSubstitute);

		// try to find a fitting replacement if the origin class could be found
		if (classOpt.isPresent()) {
			GBAFEClass classToSub = classOpt.get();
			if (classToSub instanceof FE8Data.CharacterClass) {
				return getReplacementForFe8Class(classToSub, targetGame);
			} else if (classToSub instanceof FE7Data.CharacterClass) {
				return getReplacementForFe7Class(classToSub, targetGame);
			} else if (classToSub instanceof FE6Data.CharacterClass) {
				return getReplacementForFe6Class(classToSub, targetGame);
			}
		}

		return FE6Data.CharacterClass.NONE;
	}

	/**
	 * Double check exceptional cases after Naive search.
	 * 
	 * F.e. FE8 has a bard class, but that one has no animations and as such isn't
	 * usable (since it apparently might freeze), make sure not to give that one out
	 * and instead give out Dancer in the fixed mapping.
	 */
	protected static boolean isExceptionCase(GBAFEClassProvider provider, Optional<GBAFEClass> classOpt) {
		GBAFEClass chosenClass = classOpt.get();
		if (provider instanceof FE8Data) {
			return (Arrays.asList(
					FE8Data.CharacterClass.BARD, // Lack of Magic Animations?
					// These following female classes are too much of a pain to make work. 
					// They have the same animation as the male one anyway, don't have Promo Bonuses either.
					FE8Data.CharacterClass.WYVERN_RIDER_F, 
					FE8Data.CharacterClass.WYVERN_LORD_F, 
					FE8Data.CharacterClass.HERO_F, 
					FE8Data.CharacterClass.SHAMAN_F, 
					FE8Data.CharacterClass.DRUID_F 
					).contains(chosenClass));
		} else if(provider instanceof FE7Data) {
			return (Arrays.asList(
					FE7Data.CharacterClass.CAVALIER_F, // Not a useable class
					FE7Data.CharacterClass.MERCENARY_F, // Not a useable class
					// These following female classes are too much of a pain to make work. 
					// Also they have the same animation as the male one anyway, so it's not worth for just he map sprite, don't have Promo Bonuses either. 
					FE7Data.CharacterClass.SHAMAN_F, 
					FE7Data.CharacterClass.MYRMIDON_F, 
					FE7Data.CharacterClass.DRUID_F, 
					FE7Data.CharacterClass.HERO_F, 
					FE7Data.CharacterClass.KNIGHT_F, 
					FE7Data.CharacterClass.GENERAL_F,
					FE7Data.CharacterClass.WYVERNKNIGHT_F,
					FE7Data.CharacterClass.NOMAD_F,
					FE7Data.CharacterClass.NOMADTROOPER_F
					
					).contains(chosenClass));
			
		} else if(provider instanceof FE6Data) {
			return (Arrays.asList(
					FE6Data.CharacterClass.PALADIN_F, // Not a useable class
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
	 * Returns the Closest Replacement Class for each of the classes used by an FE8
	 * Character as a FE6 / 7 Class
	 */
	protected static GBAFEClass getReplacementForFe8Class(GBAFEClass classToSubstitute, GameType targetGame) {
		FE8Data.CharacterClass castedClass = (FE8Data.CharacterClass) classToSubstitute;
		boolean isFE6 = targetGame.equals(GameType.FE6);
		if (asList(FE8Data.CharacterClass.FIGHTER, FE8Data.CharacterClass.TRAINEE).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.FIGHTER : FE7Data.CharacterClass.FIGHTER;
		} else if (asList(FE8Data.CharacterClass.EPHRAIM_LORD, FE8Data.CharacterClass.RECRUIT).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SOLDIER : FE7Data.CharacterClass.SOLDIER;
		} else if (asList(FE8Data.CharacterClass.EIRIKA_LORD).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MYRMIDON_F : FE7Data.CharacterClass.MYRMIDON_F;
		} else if (asList(FE8Data.CharacterClass.PEGASUS_KNIGHT).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.PEGASUS_KNIGHT : FE7Data.CharacterClass.PEGASUSKNIGHT;
		} else if (asList(FE8Data.CharacterClass.MANAKETE_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MANAKETE_F : FE7Data.CharacterClass.DANCER;
		} else if (asList(FE8Data.CharacterClass.CAVALIER).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.CAVALIER : FE7Data.CharacterClass.CAVALIER;
		} else if (asList(FE8Data.CharacterClass.CAVALIER_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.CAVALIER : FE7Data.CharacterClass.CAVALIER_F;
		} else if (asList(FE8Data.CharacterClass.GREAT_KNIGHT).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.GENERAL : FE7Data.CharacterClass.GENERAL;
		} else if (asList(FE8Data.CharacterClass.KNIGHT).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.KNIGHT : FE7Data.CharacterClass.KNIGHT;
		} else if (asList(FE8Data.CharacterClass.MERCENARY).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MERCENARY : FE7Data.CharacterClass.MERCENARY;
		} else if (asList(FE8Data.CharacterClass.MERCENARY_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MERCENARY : FE7Data.CharacterClass.MERCENARY_F;
		} else if (asList(FE8Data.CharacterClass.MYRMIDON).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MYRMIDON : FE7Data.CharacterClass.MYRMIDON;
		} else if (asList(FE8Data.CharacterClass.MYRMIDON_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MYRMIDON_F : FE7Data.CharacterClass.MYRMIDON_F;
		} else if (asList(FE8Data.CharacterClass.PUPIL, FE8Data.CharacterClass.MAGE).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MAGE : FE7Data.CharacterClass.MAGE;
		} else if (asList(FE8Data.CharacterClass.MAGE_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MAGE_F : FE7Data.CharacterClass.MAGE_F;
		} else if (asList(FE8Data.CharacterClass.PRIEST, FE8Data.CharacterClass.MONK).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.PRIEST : FE7Data.CharacterClass.MONK;
		} else if (asList(FE8Data.CharacterClass.TROUBADOUR).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.TROUBADOUR : FE7Data.CharacterClass.TROUBADOUR;
		} else if (asList(FE8Data.CharacterClass.SAGE).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SAGE : FE7Data.CharacterClass.SAGE;
		} else if (asList(FE8Data.CharacterClass.SNIPER).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SNIPER : FE7Data.CharacterClass.SNIPER;
		} else if (asList(FE8Data.CharacterClass.ARCHER_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.ARCHER_F : FE7Data.CharacterClass.ARCHER_F;
		} else if (asList(FE8Data.CharacterClass.SHAMAN).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SHAMAN : FE7Data.CharacterClass.SHAMAN;
		} else if (asList(FE8Data.CharacterClass.CLERIC).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.CLERIC : FE7Data.CharacterClass.CLERIC;
		} else if (asList(FE8Data.CharacterClass.WYVERN_RIDER).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.WYVERN_RIDER : FE7Data.CharacterClass.WYVERNKNIGHT;
		} else if (asList(FE8Data.CharacterClass.ROGUE).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.NONE : FE7Data.CharacterClass.ASSASSIN;
		}
		return FE6Data.CharacterClass.NONE;
	}

	/**
	 * Returns the Closest Replacement Class for each of the classes used by an FE7
	 * Character as a FE6 / 8 Class
	 */
	protected static GBAFEClass getReplacementForFe7Class(GBAFEClass classToSubstitute, GameType targetGame) {
		FE7Data.CharacterClass castedClass = (FE7Data.CharacterClass) classToSubstitute;
		boolean isFE6 = targetGame.equals(GameType.FE6);
		if (asList(FE7Data.CharacterClass.LORD_LYN).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MYRMIDON_F : FE8Data.CharacterClass.MYRMIDON_F;
		} else if (asList(FE7Data.CharacterClass.LORD_HECTOR).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.KNIGHT : FE8Data.CharacterClass.KNIGHT;
		} else if (asList(FE7Data.CharacterClass.LORD_ELIWOOD).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MERCENARY : FE8Data.CharacterClass.MERCENARY;
		} else if (asList(FE7Data.CharacterClass.FIGHTER).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.FIGHTER : FE8Data.CharacterClass.FIGHTER;
		} else if (asList(FE7Data.CharacterClass.UBER_SAGE).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SAGE : FE8Data.CharacterClass.SAGE;
		} else if (asList(FE7Data.CharacterClass.PEGASUSKNIGHT).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.PEGASUS_KNIGHT : FE8Data.CharacterClass.PEGASUS_KNIGHT;
		} else if (asList(FE7Data.CharacterClass.CAVALIER).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.CAVALIER : FE8Data.CharacterClass.CAVALIER;
		} else if (asList(FE7Data.CharacterClass.CAVALIER_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.CAVALIER : FE8Data.CharacterClass.CAVALIER;
		} else if (asList(FE7Data.CharacterClass.PALADIN).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.PALADIN : FE8Data.CharacterClass.PALADIN;
		} else if (asList(FE7Data.CharacterClass.PALADIN_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.PALADIN : FE8Data.CharacterClass.PALADIN;
		} else if (asList(FE7Data.CharacterClass.KNIGHT).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.KNIGHT : FE8Data.CharacterClass.KNIGHT;
		} else if (asList(FE7Data.CharacterClass.MERCENARY).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MERCENARY : FE8Data.CharacterClass.MERCENARY;
		} else if (asList(FE7Data.CharacterClass.MERCENARY_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MERCENARY : FE8Data.CharacterClass.MERCENARY;
		} else if (asList(FE7Data.CharacterClass.MYRMIDON).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MYRMIDON : FE8Data.CharacterClass.MYRMIDON;
		} else if (asList(FE7Data.CharacterClass.MYRMIDON_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MYRMIDON_F : FE8Data.CharacterClass.MYRMIDON_F;
		} else if (asList(FE7Data.CharacterClass.MAGE_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.MAGE_F : FE8Data.CharacterClass.MAGE_F;
		} else if (asList(FE7Data.CharacterClass.MONK).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.PRIEST : FE8Data.CharacterClass.MONK;
		} else if (asList(FE7Data.CharacterClass.TROUBADOUR).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.TROUBADOUR : FE8Data.CharacterClass.TROUBADOUR;
		} else if (asList(FE7Data.CharacterClass.SAGE).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SAGE : FE8Data.CharacterClass.SAGE;
		} else if (asList(FE7Data.CharacterClass.SNIPER).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SNIPER : FE8Data.CharacterClass.SNIPER;
		} else if (asList(FE7Data.CharacterClass.ARCHER_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.ARCHER_F : FE8Data.CharacterClass.ARCHER_F;
		} else if (asList(FE7Data.CharacterClass.SHAMAN).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SHAMAN : FE8Data.CharacterClass.SHAMAN;
		} else if (asList(FE7Data.CharacterClass.CLERIC).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.CLERIC : FE8Data.CharacterClass.CLERIC;
		} else if (asList(FE7Data.CharacterClass.WYVERNKNIGHT).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.WYVERN_RIDER : FE8Data.CharacterClass.WYVERN_KNIGHT;
		} else if (asList(FE7Data.CharacterClass.ASSASSIN).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.NONE : FE8Data.CharacterClass.ASSASSIN;
		} else if (asList(FE7Data.CharacterClass.NOMAD).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.NOMAD : FE8Data.CharacterClass.ARCHER;
		} else if (asList(FE7Data.CharacterClass.NOMADTROOPER).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.NOMAD_TROOPER : FE8Data.CharacterClass.RANGER;
		} else if (asList(FE7Data.CharacterClass.NOMADTROOPER_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.NOMAD_TROOPER_F : FE8Data.CharacterClass.RANGER_F;
		} else if (asList(FE7Data.CharacterClass.BISHOP).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.BISHOP : FE8Data.CharacterClass.BISHOP;
		} else if (asList(FE7Data.CharacterClass.SWORDMASTER_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SWORDMASTER_F : FE8Data.CharacterClass.SWORDMASTER_F;
		} else if (asList(FE7Data.CharacterClass.SWORDMASTER).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SWORDMASTER : FE8Data.CharacterClass.SWORDMASTER;
		} else if (asList(FE7Data.CharacterClass.GENERAL).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.GENERAL : FE8Data.CharacterClass.GENERAL;
		} else if (asList(FE7Data.CharacterClass.BARD).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.BARD : FE8Data.CharacterClass.DANCER;
		} else if (asList(FE7Data.CharacterClass.ASSASSIN).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.SWORDMASTER : FE8Data.CharacterClass.ASSASSIN;
		} else if (asList(FE7Data.CharacterClass.PIRATE).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.PIRATE : FE8Data.CharacterClass.PIRATE;
		} else if (asList(FE7Data.CharacterClass.WARRIOR).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.WARRIOR : FE8Data.CharacterClass.WARRIOR;
		} else if (asList(FE7Data.CharacterClass.WYVERNLORD_F).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.WYVERN_KNIGHT_F : FE8Data.CharacterClass.WYVERN_LORD;
		} else if (asList(FE7Data.CharacterClass.HERO).contains(castedClass)) {
			return isFE6 ? FE6Data.CharacterClass.HERO : FE8Data.CharacterClass.HERO;
		}
		return FE6Data.CharacterClass.NONE;
	}

	/**
	 * Returns the Closest Replacement Class for each of the classes used by an FE7
	 * Character as a FE6 / 8 Class
	 */
	protected static GBAFEClass getReplacementForFe6Class(GBAFEClass classToSubstitute, GameType targetGame) {
		FE6Data.CharacterClass castedClass = (FE6Data.CharacterClass) classToSubstitute;
		boolean isFE7 = targetGame.equals(GameType.FE7);
		if (asList(FE6Data.CharacterClass.LORD).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.LORD_ELIWOOD : FE8Data.CharacterClass.MERCENARY;
		} else if (asList(FE6Data.CharacterClass.FIGHTER).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.FIGHTER : FE8Data.CharacterClass.FIGHTER;
		} else if (asList(FE6Data.CharacterClass.PEGASUS_KNIGHT).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.PEGASUSKNIGHT : FE8Data.CharacterClass.PEGASUS_KNIGHT;
		} else if (asList(FE6Data.CharacterClass.FALCON_KNIGHT).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.FALCONKNIGHT : FE8Data.CharacterClass.FALCON_KNIGHT;
		} else if (asList(FE6Data.CharacterClass.CAVALIER).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.CAVALIER : FE8Data.CharacterClass.CAVALIER;
		} else if (asList(FE6Data.CharacterClass.PALADIN).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.PALADIN : FE8Data.CharacterClass.PALADIN;
		} else if (asList(FE6Data.CharacterClass.KNIGHT).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.KNIGHT : FE8Data.CharacterClass.KNIGHT;
		} else if (asList(FE6Data.CharacterClass.MERCENARY).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.MERCENARY : FE8Data.CharacterClass.MERCENARY;
		} else if (asList(FE6Data.CharacterClass.MYRMIDON).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.MYRMIDON : FE8Data.CharacterClass.MYRMIDON;
		} else if (asList(FE6Data.CharacterClass.MYRMIDON_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.MYRMIDON_F : FE8Data.CharacterClass.MYRMIDON_F;
		} else if (asList(FE6Data.CharacterClass.THIEF_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.THIEF : FE8Data.CharacterClass.THIEF;
		} else if (asList(FE6Data.CharacterClass.MAGE_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.MAGE_F : FE8Data.CharacterClass.MAGE_F;
		} else if (asList(FE6Data.CharacterClass.PRIEST).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.MONK : FE8Data.CharacterClass.PRIEST;
		} else if (asList(FE6Data.CharacterClass.TROUBADOUR).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.TROUBADOUR : FE8Data.CharacterClass.TROUBADOUR;
		} else if (asList(FE6Data.CharacterClass.SAGE).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.SAGE : FE8Data.CharacterClass.SAGE;
		} else if (asList(FE6Data.CharacterClass.SNIPER).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.SNIPER : FE8Data.CharacterClass.SNIPER;
		} else if (asList(FE6Data.CharacterClass.ARCHER_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.ARCHER_F : FE8Data.CharacterClass.ARCHER_F;
		} else if (asList(FE6Data.CharacterClass.SHAMAN, FE6Data.CharacterClass.SHAMAN_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.SHAMAN : FE8Data.CharacterClass.SHAMAN;
		} else if (asList(FE6Data.CharacterClass.CLERIC).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.CLERIC : FE8Data.CharacterClass.CLERIC;
		} else if (asList(FE6Data.CharacterClass.WYVERN_RIDER).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.WYVERNKNIGHT : FE8Data.CharacterClass.WYVERN_RIDER;
		} else if (asList(FE6Data.CharacterClass.WYVERN_RIDER_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.WYVERNKNIGHT : FE8Data.CharacterClass.WYVERN_RIDER;
		} else if (asList(FE6Data.CharacterClass.NOMAD).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.NOMAD : FE8Data.CharacterClass.ARCHER;
		} else if (asList(FE6Data.CharacterClass.NOMAD_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.NOMAD : FE8Data.CharacterClass.ARCHER_F;
		} else if (asList(FE6Data.CharacterClass.NOMAD_TROOPER).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.NOMADTROOPER : FE8Data.CharacterClass.RANGER;
		} else if (asList(FE6Data.CharacterClass.NOMAD_TROOPER_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.NOMADTROOPER : FE8Data.CharacterClass.RANGER_F;
		} else if (asList(FE6Data.CharacterClass.BISHOP).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.BISHOP : FE8Data.CharacterClass.BISHOP;
		} else if (asList(FE6Data.CharacterClass.SWORDMASTER_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.SWORDMASTER_F : FE8Data.CharacterClass.SWORDMASTER_F;
		} else if (asList(FE6Data.CharacterClass.SWORDMASTER).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.SWORDMASTER : FE8Data.CharacterClass.SWORDMASTER;
		} else if (asList(FE6Data.CharacterClass.GENERAL).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.GENERAL : FE8Data.CharacterClass.GENERAL;
		} else if (asList(FE6Data.CharacterClass.BARD).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.BARD : FE8Data.CharacterClass.DANCER;
		} else if (asList(FE6Data.CharacterClass.SWORDMASTER_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.SWORDMASTER_F : FE8Data.CharacterClass.SWORDMASTER_F;
		} else if (asList(FE6Data.CharacterClass.PIRATE).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.PIRATE : FE8Data.CharacterClass.PIRATE;
		} else if (asList(FE6Data.CharacterClass.WARRIOR).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.WARRIOR : FE8Data.CharacterClass.WARRIOR;
		} else if (asList(FE6Data.CharacterClass.HERO).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.HERO : FE8Data.CharacterClass.HERO;
		} else if (asList(FE6Data.CharacterClass.HERO_F).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.HERO : FE8Data.CharacterClass.HERO;
		} else if (asList(FE6Data.CharacterClass.KING).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.GENERAL : FE8Data.CharacterClass.GENERAL;
		} else if (asList(FE6Data.CharacterClass.WYVERN_KNIGHT).contains(castedClass)) {
			return isFE7 ? FE7Data.CharacterClass.WYVERNLORD : FE8Data.CharacterClass.WYVERN_LORD;
		}
		return FE6Data.CharacterClass.NONE;
	}

}