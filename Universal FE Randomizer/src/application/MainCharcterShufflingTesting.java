package application;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEStatDto;
import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import io.DiffApplicator;
import io.FileHandler;
import io.UPSPatcher;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.PortraitDataLoader;
import random.gba.loader.TextLoader;
import random.gba.randomizer.shuffling.CharacterShuffler;
import random.gba.randomizer.shuffling.GBACrossGameData;
import random.gba.randomizer.shuffling.data.GBAFEPortraitData;
import ui.model.CharacterShufflingOptions;
import ui.model.ItemAssignmentOptions;
import ui.model.ItemAssignmentOptions.ShopAdjustment;
import ui.model.ItemAssignmentOptions.WeaponReplacementPolicy;
import util.DebugListener;
import util.DebugPrinter;
import util.DiffCompiler;
import util.FreeSpaceManager;
import util.SeedGenerator;

public class MainCharcterShufflingTesting {

	public static void main(String... strings) throws IOException {
		DebugPrinter.registerListener(new DebugListener() {
			
			@Override
			public void logMessage(String category, String message) {
				System.out.printf("[%s] %s%n", category, message);
			}
		}, "sysout");
		
		mainFE6(false);
		// mainFE7();
		// mainFE8();
//		fe6JsonGenerator(strings);
//		fe7JsonGenerator(strings);
//		fe8JsonGenerator(strings);
	}

	public static void mainFE8() throws IOException {
		FileHandler sourceFile = new FileHandler("C:\\Users\\Marvin\\Desktop\\Yune\\FE8.gba");
		CharacterDataLoader cd = new CharacterDataLoader(FE8Data.characterProvider, sourceFile);
		TextLoader tl = new TextLoader(GameType.FE8, sourceFile);
		tl.allowTextChanges = true;
		PortraitDataLoader pd = new PortraitDataLoader(FE8Data.shufflingDataProvider, sourceFile);
		FreeSpaceManager freeSpace = new FreeSpaceManager(FEBase.GameType.FE8, FE8Data.InternalFreeRange, sourceFile);
		Random rng = new Random(SeedGenerator.generateSeedValue(SeedGenerator.generateRandomSeed(), 45648997));
		ChapterLoader chapterData = new ChapterLoader(GameType.FE8, sourceFile);
		CharacterShufflingOptions shufflingOptions = new CharacterShufflingOptions(
				CharacterShufflingOptions.ShuffleLevelingMode.AUTOLEVEL, true, 100,
				Arrays.asList("fe6chars.json", "fe7chars.json"));
		ClassDataLoader classData = new ClassDataLoader(FE8Data.classProvider, sourceFile);
		ItemDataLoader itemData = new ItemDataLoader(FE8Data.itemProvider, sourceFile, freeSpace);
		ItemAssignmentOptions inventoryOptions = new ItemAssignmentOptions(WeaponReplacementPolicy.ANY_USABLE,
				ShopAdjustment.ADJUST_TO_PARTY, false, false);

		CharacterShuffler.shuffleCharacters(GameType.FE8, cd, tl, rng, sourceFile, pd, freeSpace, chapterData,
				classData, shufflingOptions, inventoryOptions, itemData);
		DiffCompiler compiler = new DiffCompiler();
		pd.compileDiffs(compiler);
		tl.commitChanges(freeSpace, compiler);
		cd.compileDiffs(compiler);
		freeSpace.commitChanges(compiler);
		chapterData.compileDiffs(compiler);
		DiffApplicator.applyDiffs(compiler, sourceFile, "C:\\Users\\Marvin\\Desktop\\Yune\\test.gba");

		System.out.println("------------------------------------------------------");
		System.out.println("----------------------DONE----------------------------");
		System.out.println("------------------------------------------------------");
	}

	public static void mainFE7() throws IOException {
		FileHandler sourceFile = new FileHandler("C:\\Users\\Marvin\\Desktop\\Yune\\FE7.gba");
		CharacterDataLoader cd = new CharacterDataLoader(FE7Data.characterProvider, sourceFile);
		TextLoader tl = new TextLoader(GameType.FE7, sourceFile);
		tl.allowTextChanges = true;
		PortraitDataLoader pd = new PortraitDataLoader(FE7Data.shufflingDataProvider, sourceFile);
		FreeSpaceManager freeSpace = new FreeSpaceManager(FEBase.GameType.FE7, FE7Data.InternalFreeRange, sourceFile);
		Random rng = new Random(SeedGenerator.generateSeedValue(SeedGenerator.generateRandomSeed(), 45648997));
		ChapterLoader chapterData = new ChapterLoader(GameType.FE7, sourceFile);
		CharacterShufflingOptions shufflingOptions = new CharacterShufflingOptions(
				CharacterShufflingOptions.ShuffleLevelingMode.AUTOLEVEL, true, 50,
				Arrays.asList("fe8chars.json", "fe6chars.json"));
		ClassDataLoader classData = new ClassDataLoader(FE7Data.classProvider, sourceFile);
		ItemDataLoader itemData = new ItemDataLoader(FE7Data.itemProvider, sourceFile, freeSpace);
		ItemAssignmentOptions inventoryOptions = new ItemAssignmentOptions(WeaponReplacementPolicy.ANY_USABLE,
				ShopAdjustment.ADJUST_TO_PARTY, false, false);

		CharacterShuffler.shuffleCharacters(GameType.FE7, cd, tl, rng, sourceFile, pd, freeSpace, chapterData,
				classData, shufflingOptions, inventoryOptions, itemData);
		DiffCompiler compiler = new DiffCompiler();
		pd.compileDiffs(compiler);
		tl.commitChanges(freeSpace, compiler);
		cd.compileDiffs(compiler);
		freeSpace.commitChanges(compiler);
		chapterData.compileDiffs(compiler);
		DiffApplicator.applyDiffs(compiler, sourceFile, "C:\\Users\\Marvin\\Desktop\\Yune\\test2.gba");

		System.out.println("------------------------------------------------------");
		System.out.println("----------------------DONE----------------------------");
		System.out.println("------------------------------------------------------");
	}

	public static void mainFE6(boolean translationPatch) throws IOException {
		FileHandler sourceFile;
		if (translationPatch) {
			UPSPatcher.applyUPSPatch("FE6Localization_v1.1.ups", "C:\\Users\\Marvin\\Desktop\\Yune\\JP.gba",
					"C:\\Users\\Marvin\\Desktop\\Yune\\Test3.gba.tmp", null);
			sourceFile = new FileHandler("C:\\Users\\Marvin\\Desktop\\Yune\\test3.gba.tmp");
		} else {
			sourceFile = new FileHandler("C:\\Users\\Marvin\\Desktop\\Yune\\JP.gba");
		}

		CharacterDataLoader cd = new CharacterDataLoader(FE6Data.characterProvider, sourceFile);
		TextLoader tl = new TextLoader(GameType.FE6, sourceFile);
		tl.allowTextChanges = true;
		PortraitDataLoader pd = new PortraitDataLoader(FE6Data.shufflingDataProvider, sourceFile);
		FreeSpaceManager freeSpace = new FreeSpaceManager(FEBase.GameType.FE6, FE6Data.InternalFreeRange, sourceFile);
		Random rng = new Random(SeedGenerator.generateSeedValue(SeedGenerator.generateRandomSeed(), 45648997));
		ChapterLoader chapterData = new ChapterLoader(GameType.FE6, sourceFile);
		CharacterShufflingOptions shufflingOptions = new CharacterShufflingOptions(
				CharacterShufflingOptions.ShuffleLevelingMode.AUTOLEVEL, true, 50,
				Arrays.asList("fe8chars.json", "fe7chars.json"));
		ClassDataLoader classData = new ClassDataLoader(FE6Data.classProvider, sourceFile);
		ItemDataLoader itemData = new ItemDataLoader(FE6Data.itemProvider, sourceFile, freeSpace);
		ItemAssignmentOptions inventoryOptions = new ItemAssignmentOptions(WeaponReplacementPolicy.EQUAL_RANK,
				ShopAdjustment.ADJUST_TO_PARTY, false, false);

		CharacterShuffler.shuffleCharacters(GameType.FE6, cd, tl, rng, sourceFile, pd, freeSpace, chapterData,
				classData, shufflingOptions, inventoryOptions, itemData);
		DiffCompiler compiler = new DiffCompiler();
		pd.compileDiffs(compiler);
		tl.commitChanges(freeSpace, compiler);
		cd.compileDiffs(compiler);
		freeSpace.commitChanges(compiler);
		chapterData.compileDiffs(compiler);
		DiffApplicator.applyDiffs(compiler, sourceFile, "C:\\Users\\Marvin\\Desktop\\Yune\\test3.gba");

		System.out.println("------------------------------------------------------");
		System.out.println("----------------------DONE----------------------------");
		System.out.println("------------------------------------------------------");
	}

	/**
	 * returns the given String with the first letter uppercase
	 */
	public static String capital(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static void fe6JsonGenerator(String[] args) throws IOException {
		FileHandler sourceFile = new FileHandler("C:\\Users\\Marvin\\Desktop\\Yune\\JP.gba");
		CharacterDataLoader cd = new CharacterDataLoader(FE6Data.characterProvider, sourceFile);
		PortraitDataLoader pd = new PortraitDataLoader(FE6Data.shufflingDataProvider, sourceFile);

		List<GBACrossGameData> charas = new ArrayList<>();

		for (GBAFECharacterData c : cd.canonicalPlayableCharacters(true)) {
			GBACrossGameData chara = new GBACrossGameData(capital(c.displayString()),
					"portraits/FE6/" + c.displayString().toLowerCase(), "", "", "",
					FE6Data.CharacterClass.valueOf(c.getClassID()), c.getLevel(),
					new GBAFEStatDto(c.getBaseHP(), c.getBaseSTR(), c.getBaseSKL(), c.getBaseSPD(), c.getBaseDEF(),
							c.getBaseRES(), c.getBaseLCK()),
					new GBAFEStatDto(c.getHPGrowth(), c.getSTRGrowth(), c.getSKLGrowth(), c.getSPDGrowth(),
							c.getDEFGrowth(), c.getRESGrowth(), c.getLCKGrowth()),
					new int[] { c.getSwordRank(), c.getLanceRank(), c.getAxeRank(), c.getBowRank(), c.getStaffRank(),
							c.getAnimaRank(), c.getLightRank(), c.getDarkRank() },
					c.getConstitution(), pd.getPortraitDataByFaceId(c.getFaceID()).getFacialFeatureCoordinates());
			chara.originGame = "FE6";

			charas.add(chara);
		}

//		Writer writer = Files.newBufferedWriter(Paths.get("fe6Chars.json"));
//		Gson gson = new Gson();
//		gson.toJson(charas, writer);
//		writer.close();
	}

	public static void fe7JsonGenerator(String[] args) throws IOException {
		FileHandler sourceFile = new FileHandler("C:\\Users\\Marvin\\Desktop\\Yune\\fe7.gba");
		CharacterDataLoader cd = new CharacterDataLoader(FE7Data.characterProvider, sourceFile);
		PortraitDataLoader pd = new PortraitDataLoader(FE7Data.shufflingDataProvider, sourceFile);

		List<GBACrossGameData> charas = new ArrayList<>();

		for (GBAFECharacterData c : cd.canonicalPlayableCharacters(true)) {
			GBAFEPortraitData portraitData = pd.getPortraitDataByFaceId(c.getFaceID());
			StringBuilder sb = new StringBuilder(64);
			for (byte b : portraitData.getNewPalette()) {
				sb.append(Integer.toHexString(b >> 4 & 0xF));
				sb.append(Integer.toHexString(b & 0xF));
			}

			GBACrossGameData chara = new GBACrossGameData(capital(c.displayString()),
					"portraits/FE7/" + c.displayString().toLowerCase() + ".png", "", "", sb.toString().toUpperCase(),
					FE7Data.CharacterClass.valueOf(c.getClassID()), c.getLevel(),
					new GBAFEStatDto(c.getBaseHP(), c.getBaseSTR(), c.getBaseSKL(), c.getBaseSPD(), c.getBaseDEF(),
							c.getBaseRES(), c.getBaseLCK()),
					new GBAFEStatDto(c.getHPGrowth(), c.getSTRGrowth(), c.getSKLGrowth(), c.getSPDGrowth(),
							c.getDEFGrowth(), c.getRESGrowth(), c.getLCKGrowth()),
					new int[] { c.getSwordRank(), c.getLanceRank(), c.getAxeRank(), c.getBowRank(), c.getStaffRank(),
							c.getAnimaRank(), c.getLightRank(), c.getDarkRank() },
					c.getConstitution(), pd.getPortraitDataByFaceId(c.getFaceID()).getFacialFeatureCoordinates());
			chara.originGame = "FE7";

			charas.add(chara);
		}

		Writer writer = Files.newBufferedWriter(Paths.get("fe7Chars.json"));
		Gson gson = new Gson();
		gson.toJson(charas, writer);
		writer.close();
	}

	public static void fe8JsonGenerator(String[] args) throws IOException {
		FileHandler sourceFile = new FileHandler("C:\\Users\\Marvin\\Desktop\\Yune\\fe8.gba");
		CharacterDataLoader cd = new CharacterDataLoader(FE8Data.characterProvider, sourceFile);
		PortraitDataLoader pd = new PortraitDataLoader(FE8Data.shufflingDataProvider, sourceFile);

		List<GBACrossGameData> charas = new ArrayList<>();

		for (GBAFECharacterData c : cd.canonicalPlayableCharacters(true)) {
			GBAFEPortraitData portraitData = pd.getPortraitDataByFaceId(c.getFaceID());
			StringBuilder sb = new StringBuilder(64);
			for (byte b : portraitData.getNewPalette()) {
				sb.append(Integer.toHexString(b >> 4 & 0xF));
				sb.append(Integer.toHexString(b & 0xF));
			}

			GBACrossGameData chara = new GBACrossGameData(capital(c.displayString()),
					"portraits/FE8/" + c.displayString().toLowerCase() + ".png", "", "", sb.toString().toUpperCase(),
					FE8Data.CharacterClass.valueOf(c.getClassID()), c.getLevel(),
					new GBAFEStatDto(c.getBaseHP(), c.getBaseSTR(), c.getBaseSKL(), c.getBaseSPD(), c.getBaseDEF(),
							c.getBaseRES(), c.getBaseLCK()),
					new GBAFEStatDto(c.getHPGrowth(), c.getSTRGrowth(), c.getSKLGrowth(), c.getSPDGrowth(),
							c.getDEFGrowth(), c.getRESGrowth(), c.getLCKGrowth()),
					new int[] { c.getSwordRank(), c.getLanceRank(), c.getAxeRank(), c.getBowRank(), c.getStaffRank(),
							c.getAnimaRank(), c.getLightRank(), c.getDarkRank() },
					c.getConstitution(), pd.getPortraitDataByFaceId(c.getFaceID()).getFacialFeatureCoordinates());
			chara.originGame = "FE8";

			charas.add(chara);
		}

		Writer writer = Files.newBufferedWriter(Paths.get("fe8Chars.json"));
		Gson gson = new Gson();
		gson.toJson(charas, writer);
		writer.close();
	}
}
