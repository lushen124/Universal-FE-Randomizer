package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

import random.gba.randomizer.shuffling.CharacterImporter;
import random.gba.randomizer.shuffling.GBACrossGameData;

public class CustomCastGenerator {

	public static void main(String[] args) throws IOException {

		// (1) Read User input where their file with the names is located.
		System.out.println("Please print the full path of the file where you added your characters.");
		Scanner scan = new Scanner(System.in);
		String enteredPath = scan.next();
		scan.close();

		// (2) Open and read the file into a list
		File selectedFile = new File(enteredPath);
		List<String> selectedChars = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
			String line = reader.readLine();
			while (line != null) {
				selectedChars.add(line);
				line = reader.readLine();
			}
		} catch (Exception e) {
			System.out.println("Something went wrong while reading the selected file.");
			e.printStackTrace();
		}

		// (3) Load the FE6/7/8 Configurations
		List<GBACrossGameData> availableCharacters = CharacterImporter.importCharacterDataFromFiles("fe6chars.json",
				"fe7chars.json", "fe8chars.json");
		List<GBACrossGameData> customCast = new ArrayList<>();

		// (4) For each char from the selection file try finding the correct character from the Configuration files
		for (String selectedChar : selectedChars) {
			GBACrossGameData addedChar = null;
			// Check for each character from the configuration if they have the same name
			inner : for (GBACrossGameData innerChar : availableCharacters) {
				// In some Cases (FE6/7 Marcus & Karel) The Name alone isn't unique enough, for
				// this allow adding the Game Number i.e. FE6 / FE7 at the start of the name)
				if (innerChar.name.equals(selectedChar)
						|| (innerChar.originGame.toUpperCase() + innerChar.name).equals(selectedChar)) {
					addedChar = innerChar;
					break inner;
				}
			}
			if (addedChar != null) {
				System.out.println(String.format("Adding %s from game %s", addedChar.name, addedChar.originGame));
				customCast.add(addedChar);
			}
		}

		// (5) Write the config file containing the selections
		Writer writer = Files.newBufferedWriter(Paths.get("customCast.json"));
		Gson gson = new Gson();
		gson.toJson(customCast, writer);
		writer.close();

		System.out.println("File generation completed successfully.");
	}

}
