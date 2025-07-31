package random.gba.randomizer.shuffling;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import application.Main;
import util.DebugPrinter;


/**
 * Class responsible for reading the GBACrossGameData entries from the Json files for shuffling them cross games.
 */
public class CharacterImporter {

	// Seems to be necessary for Gson
	private static final class GBACrossGameDataType extends TypeToken<List<GBACrossGameData>> {}

	/**
	 * De-serializes the Data in the given file and returns all the characters that
	 * could be loaded from the files as a List. Note that this list can be empty.
	 */
	public static List<GBACrossGameData> importCharacterDataFromFiles(String... files) {
		List<GBACrossGameData> characters = new ArrayList<>();
		Gson gson = new Gson();
		for (String file : files) {
			try {
				InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(file);
				Reader r = null;
				if (inputStream != null) {
					r = new InputStreamReader(inputStream);
				} else {
					r = new FileReader(new File(file));
				}
				DebugPrinter.log(DebugPrinter.Key.GBA_CHARACTER_SHUFFLING, String.format("Trying to import characters from file %s", file));
				characters.addAll(gson.fromJson(r, new GBACrossGameDataType().getType()));
			} catch (Exception e) {
				System.out
						.println("Couldn't read the characters from file " + file + ", continuing with the next file.");
				e.printStackTrace();
			}
		}

		return characters;
	}

}