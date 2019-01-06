package random.snes.fe4.loader;

import java.util.HashMap;
import java.util.Map;

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4EnemyCharacter;
import fedata.snes.fe4.FE4StaticCharacter;
import io.FileHandler;
import util.DebugPrinter;

public class CharacterDataLoader {
	
	private Map<FE4Data.Character, FE4StaticCharacter> staticPlayableCharacters;
	private Map<FE4Data.Character, FE4ChildCharacter> childCharacters;
	
	private Map<Integer, FE4EnemyCharacter> enemyCharacters;
	
	private Map<FE4Data.Character, FE4EnemyCharacter> bossCharacters;
	private Map<FE4Data.Character, FE4StaticCharacter> holyBloodBossCharacters;
	
	private boolean isHeadered;
	
	public static final String RecordKeeperCategoryKey = "Characters";
	
	public CharacterDataLoader(FileHandler handler, boolean headered) {
		super();
		
		this.isHeadered = headered;
		
		initializeStaticPlayableCharacters(handler);
		
		initializeChildCharacters(handler);
		
		initializeEnemyCharacters(handler);
		
		initializeBossCharacters(handler);
		
		initializeHolyBossCharacters(handler);
	}
	
	private void initializeStaticPlayableCharacters(FileHandler handler) {
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loading Static Playable Characters...");
		
		staticPlayableCharacters = new HashMap<FE4Data.Character, FE4StaticCharacter>();
		
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loading Gen1 Playable Characters...");
		
		// Load Gen 1 PCs.
		for (int i = 0; i < FE4Data.Gen1CharacterCount; i++) {
			long dataOffset = FE4Data.Gen1CharacterTableOffset + (i * FE4Data.StaticCharacterSize);
			if (!isHeadered) {
				dataOffset -= 0x200; 
			}
			byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.StaticCharacterSize);
			FE4StaticCharacter staticChar = new FE4StaticCharacter(charData, dataOffset);
			FE4Data.Character fe4Character = FE4Data.Character.valueOf(staticChar.getCharacterID());
			if (fe4Character != null) {
				staticPlayableCharacters.put(fe4Character, staticChar);
				DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded Character: " + fe4Character.toString());
			} else {
				System.err.println("Invalid character found in static playable characters. ID = 0x" + Integer.toHexString(staticChar.getCharacterID()));
			}
		}
		
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Finished Loading Gen1 Playable Characters!");
		
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loading Gen2 Playable Characters...");
		
		// Load Gen2 Statics.
		// Shanan, Dalvin, Asaello
		for (int i = 0; i < FE4Data.Gen2StaticCharacterTable1Count; i++) {
			long dataOffset = FE4Data.Gen2StaticCharacterTable1Offset + (i * FE4Data.StaticCharacterSize);
			if (!isHeadered) {
				dataOffset -= 0x200; 
			}
			byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.StaticCharacterSize);
			FE4StaticCharacter staticChar = new FE4StaticCharacter(charData, dataOffset);
			FE4Data.Character fe4Character = FE4Data.Character.valueOf(staticChar.getCharacterID());
			if (fe4Character != null) {
				staticPlayableCharacters.put(fe4Character, staticChar);
				DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded Character: " + fe4Character.toString());
			} else {
				System.err.println("Invalid character found in static playable characters. ID = 0x" + Integer.toHexString(staticChar.getCharacterID()));
			}
		}
		
		// Iuchar, Charlot, Hawk, Tristan, Finn, Deimne, Hannibal, Ares, Amid, Oifey, Daisy, Creidne, Muirne, Julia
		for (int i = 0; i < FE4Data.Gen2StaticCharacterTable2Count; i++) {
			long dataOffset = FE4Data.Gen2StaticCharacterTable2Offset + (i * FE4Data.StaticCharacterSize);
			if (!isHeadered) {
				dataOffset -= 0x200; 
			}
			byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.StaticCharacterSize);
			FE4StaticCharacter staticChar = new FE4StaticCharacter(charData, dataOffset);
			FE4Data.Character fe4Character = FE4Data.Character.valueOf(staticChar.getCharacterID());
			if (fe4Character != null) {
				staticPlayableCharacters.put(fe4Character, staticChar);
				DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded Character: " + fe4Character.toString());
			} else {
				System.err.println("Invalid character found in static playable characters. ID = 0x" + Integer.toHexString(staticChar.getCharacterID()));
			}
		}
		
		// Hermina, Linda, Laylea, Jeanne, Iucharba
		for (int i = 0; i < FE4Data.Gen2StaticCharacterTable3Count; i++) {
			long dataOffset = FE4Data.Gen2StaticCharacterTable3Offset + (i * FE4Data.StaticCharacterSize);
			if (!isHeadered) {
				dataOffset -= 0x200; 
			}
			byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.StaticCharacterSize);
			FE4StaticCharacter staticChar = new FE4StaticCharacter(charData, dataOffset);
			FE4Data.Character fe4Character = FE4Data.Character.valueOf(staticChar.getCharacterID());
			if (fe4Character != null) {
				staticPlayableCharacters.put(fe4Character, staticChar);
				DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded Character: " + fe4Character.toString());
			} else {
				System.err.println("Invalid character found in static playable characters. ID = 0x" + Integer.toHexString(staticChar.getCharacterID()));
			}
		}
		
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Finished Loading Gen2 Playable Characters!");
		
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Finished loading Static Playable Characters!");
	}
	
	private void initializeChildCharacters(FileHandler handler) {
		
	}
	
	private void initializeEnemyCharacters(FileHandler handler) {
		
	}
	
	private void initializeBossCharacters(FileHandler handler) {
		
	}
	
	private void initializeHolyBossCharacters(FileHandler handler) {
		
	}

}
