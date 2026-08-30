package random.gba.randomizer.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fedata.gba.GBAFECharacterData;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.TextLoader;

/**
 * This class is used for Random Recruitment and Character Shuffling to go
 * through the text and replace the names of the slot characters with their new
 * names.
 * 
 * 
 * For Random Recruitment additionally the Portraits are changed to reflect the
 * new unit in that slot. This isn't necessary for Character Shuffling, as the
 * old portrait will just be repointed to the new one in the portrait data.
 */
public class GBATextReplacementService {

	private static Map<String, String> textReplacements = new HashMap<>();

	/**
	 * Enqueue the change of the given Slot Character Portrait and Name in the text
	 * data to the given Fill Character. Assuming that text changes are allowed.
	 * 
	 * This API is currently only use by Random Recruitment
	 */
	public static void enqueueUpdate(TextLoader textData, CharacterDataLoader characterData, GBAFECharacterData slot,
			GBAFECharacterData fill) {
		enqueuePortraitsUpdate(textData, characterData, slot, fill);
		enqueueNameUpdate(textData, slot, fill);
	}

	/**
	 * Prepares to replace the given Slot Characters Portrait references with the
	 * given Fill Character in the text data.
	 */
	public static void enqueuePortraitsUpdate(TextLoader textData, CharacterDataLoader characterData,
			GBAFECharacterData slot, GBAFECharacterData fill) {
		Set<Integer> portraits = characterData.multiPortraitsForCharacter(slot.getID());
		// For character with multiple faces, the current one is already in the Set,
		// but it's a Set, so this will not be added if it's a duplicate.
		portraits.add(slot.getFaceID());

		// Add all the portraits to be replaced
		for (int faceID : portraits) {
			textReplacements.put(String.format("[LoadFace][0x%s]", Integer.toHexString(faceID)),
					String.format("[LoadFace][0x%s]", Integer.toHexString(fill.getFaceID())));
		}
	}

	/**
	 * Prepares to replace the given Slot Characters Name references with the given
	 * Fill Character in the text data.
	 * 
	 * Convenience overload of
	 * {@link #enqueueNameUpdate(TextLoader, String, String)}
	 */
	public static void enqueueNameUpdate(TextLoader textData, GBAFECharacterData slot, GBAFECharacterData fill) {
		String oldName = textData.getStringAtIndex(slot.getNameIndex(), true).trim();
		String newName = textData.getStringAtIndex(fill.getNameIndex(), true).trim();
		enqueueNameUpdate(textData, oldName, newName);
	}

	/**
	 * Prepares to replace the given oldName with the given newName
	 */
	public static void enqueueNameUpdate(TextLoader textData, String oldName, String newName) {
		// TODO: pronouns?
		textReplacements.put(oldName, newName);
		textReplacements.put(oldName.toUpperCase(), newName.toUpperCase());
	}

	/**
	 * Run through the game text and apply the text changes.
	 */
	public static void applyChanges(TextLoader textData) {
		if (!textData.allowTextChanges) {
			// If we try to apply the changes but aren't allowed to there is probably no
			// reason to keep the map
			textReplacements.clear();
			return;
		}

		// Build tokens for pattern
		String patternString = "(" + patternStringFromReplacements(textReplacements) + ")";
		Pattern pattern = Pattern.compile(patternString);

		for (int i = 0; i < textData.getStringCount(); i++) {
			if (textData.isExcludedNameIndex(i)) {
				// don't replace things such as Iron Lance
				continue;
			}
			String originalStringWithCodes = textData.getStringAtIndex(i, false);

			String workingString = new String(originalStringWithCodes);
			Matcher matcher = pattern.matcher(workingString);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String capture = matcher.group(1);
				String replacementKey = textReplacements.get(capture);
				if (replacementKey == null) {
					// Strip out any stuttering.
					String truncated = capture.substring(capture.lastIndexOf('-') + 1);
					replacementKey = textReplacements.get(truncated);
				}
				matcher.appendReplacement(sb, replacementKey);
			}

			matcher.appendTail(sb);

			textData.setStringAtIndex(i, sb.toString());
		}

		// All text replacements are done, clear the map so we can do more later if
		// needed.
		textReplacements.clear();
	}

	private static String patternStringFromReplacements(Map<String, String> replacements) {
		StringBuilder sb = new StringBuilder();
		for (String stringToReplace : replacements.keySet()) {
			boolean isControlCode = stringToReplace.charAt(0) == '[';

			if (!isControlCode) {
				sb.append("\\b[" + stringToReplace.charAt(0) + "-]*");
			} // Removes any stuttering (ala "E-E-Eliwood!")
			sb.append(Pattern.compile(stringToReplace.replace("[", "\\[").replace("]", "\\]"), Pattern.LITERAL));
			if (!isControlCode) {
				sb.append("\\b");
			}
			sb.append('|');
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
