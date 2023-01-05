package random.gba.randomizer.shuffling;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


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
		String folder = System.getProperty("user.dir");
		for (String file : files) {
			try {
				System.out.println(folder+"\\"+file);
				characters.addAll(gson.fromJson(new FileReader(folder+"\\"+file), new GBACrossGameDataType().getType()));
			} catch (Exception e) {
				System.out
						.println("Couldn't read the characters from file " + file + ", continuing with the next file.");
			}
		}

		return characters;
	}

}