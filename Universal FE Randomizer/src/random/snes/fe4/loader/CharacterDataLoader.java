package random.snes.fe4.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4Data.EnemyTable;
import fedata.snes.fe4.FE4EnemyCharacter;
import fedata.snes.fe4.FE4StaticCharacter;
import io.FileHandler;
import util.DebugPrinter;
import util.recordkeeper.RecordKeeper;

public class CharacterDataLoader {
	
	private Map<FE4Data.Character, FE4StaticCharacter> staticPlayableCharacters;
	private Map<FE4Data.Character, FE4ChildCharacter> childCharacters;
	
	private Map<FE4Data.Character, FE4EnemyCharacter> enemyCharacters;
	private Map<FE4Data.Character, FE4EnemyCharacter> arenaCharacters;
	private Map<FE4Data.Character, FE4EnemyCharacter> bossCharacters;
	
	private Map<FE4Data.Character, FE4StaticCharacter> holyBloodBossCharacters;
	
	private boolean isHeadered;
	
	public static final String RecordKeeperCategoryKey = "Characters";
	
	public static final String RecordKeeperSubcategoryGen1 = "Gen 1";
	public static final String RecordKeeperSubcategoryGen2Static = "Gen 2 (Static)";
	public static final String RecordKeeperSubcategoryGen2Child = "Gen 2 (Children)";
	public static final String RecordKeeperSubcategoryGen2Subs = "Gen 2 (Substitutes)";
	
	public static final String RecordKeeperSubcategoryEnemy = "Minions";
	public static final String RecordKeeperSubcategoryArena = "Arena";
	public static final String RecordKeeperSubcategoryBoss1 = "Boss (Gen 1)";
	public static final String RecordKeeperSubcategoryBoss2 = "Boss (Gen 2)";
	
	public static final String RecordKeeperSubcategoryHolyBoss = "Bosses with Holy Blood or Skills";
	
	public CharacterDataLoader(FileHandler handler, boolean headered) {
		super();
		
		this.isHeadered = headered;
		
		initializeStaticPlayableCharacters(handler);
		
		initializeChildCharacters(handler);
		
		initializeEnemyAndArenaCharacters(handler);
		
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
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loading Child Playable Characters...");
		
		childCharacters = new HashMap<FE4Data.Character, FE4ChildCharacter>();
		
		// Seliph and Leif
		for (int i = 0; i < FE4Data.Gen2ChildrenCharacterTable1Count; i++) {
			long dataOffset = FE4Data.Gen2ChildrenCharacterTable1Offset + (i * FE4Data.Gen2ChildrenCharacterTable1ItemSize);
			if (!isHeadered) {
				dataOffset -= 0x200;
			}
			byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.Gen2ChildrenCharacterTable1ItemSize);
			FE4ChildCharacter child = new FE4ChildCharacter(charData, dataOffset);
			FE4Data.Character fe4Character = FE4Data.Character.valueOf(child.getCharacterID());
			if (fe4Character != null) {
				childCharacters.put(fe4Character, child);
				DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded Child: " + fe4Character.toString());
			} else {
				System.err.println("Invalid child found. ID = 0x" + Integer.toHexString(child.getCharacterID()));
			}
		}
		
		// Altena
		for (int i = 0; i < FE4Data.Gen2ChildrenCharacterTable2Count; i++) {
			long dataOffset = FE4Data.Gen2ChildrenCharacterTable2Offset + (i * FE4Data.Gen2ChildrenCharacterSize);
			if (!isHeadered) {
				dataOffset -= 0x200;
			}
			byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.Gen2ChildrenCharacterSize);
			FE4ChildCharacter child = new FE4ChildCharacter(charData, dataOffset);
			FE4Data.Character fe4Character = FE4Data.Character.valueOf(child.getCharacterID());
			if (fe4Character != null) {
				childCharacters.put(fe4Character, child);
				DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded Child: " + fe4Character.toString());
			} else {
				System.err.println("Invalid child found. ID = 0x" + Integer.toHexString(child.getCharacterID()));
			}
		}
		
		// Every other child.
		for (int i = 0; i < FE4Data.Gen2ChildrenCharacterTable3Count; i++) {
			long dataOffset = FE4Data.Gen2ChildrenCharacterTable3Offset + (i * FE4Data.Gen2ChildrenCharacterSize);
			if (!isHeadered) {
				dataOffset -= 0x200;
			}
			byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.Gen2ChildrenCharacterSize);
			FE4ChildCharacter child = new FE4ChildCharacter(charData, dataOffset);
			FE4Data.Character fe4Character = FE4Data.Character.valueOf(child.getCharacterID());
			if (fe4Character != null) {
				childCharacters.put(fe4Character, child);
				DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded Child: " + fe4Character.toString());
			} else {
				System.err.println("Invalid child found. ID = 0x" + Integer.toHexString(child.getCharacterID()));
			}
		}
		
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Finished loading Child Playable Characters!");
	}
	
	private void initializeEnemyAndArenaCharacters(FileHandler handler) {
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loading Minions and Arena Characters...");
		
		enemyCharacters = new HashMap<FE4Data.Character, FE4EnemyCharacter>();
		arenaCharacters = new HashMap<FE4Data.Character, FE4EnemyCharacter>();
		bossCharacters = new HashMap<FE4Data.Character, FE4EnemyCharacter>();
		
		for (EnemyTable table : EnemyTable.values()) {
			DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loading from " + table.toString());
			long baseOffset = table.offset;
			int count = table.count;
			for (int i = 0; i < count; i++) {
				long dataOffset = baseOffset + (i * FE4Data.EnemyDataSize);
				if (!isHeadered) {
					dataOffset -= 0x200;
				}
				byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.EnemyDataSize);
				FE4EnemyCharacter enemy = new FE4EnemyCharacter(charData, dataOffset);
				FE4Data.Character fe4Character = FE4Data.Character.valueOf(enemy.getCharacterID());
				if (fe4Character != null) {
					if (fe4Character.isMinion()) {
						enemyCharacters.put(fe4Character, enemy);
						DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded " + fe4Character.toString() + " as minion.");
					} else if (fe4Character.isArena()) {
						arenaCharacters.put(fe4Character, enemy);
						DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded " + fe4Character.toString() + " as arena.");
					} else {
						bossCharacters.put(fe4Character, enemy);
						DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded " + fe4Character.toString() + " as boss.");
					}
				} else {
					System.err.println("Invalid enemy found. Dropping... (Enemy ID: 0x" + Integer.toHexString(enemy.getCharacterID()) + ")");
				}
			}
			DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Finished loading from " + table.toString());
		}
		
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Finished loading Minions and Arena Characters!");
	}
	
	private void initializeHolyBossCharacters(FileHandler handler) {
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loading Bosses with Holy Blood...");
		
		holyBloodBossCharacters = new HashMap<FE4Data.Character, FE4StaticCharacter>();
		
		for (FE4Data.HolyEnemyTable table : FE4Data.HolyEnemyTable.values()) {
			DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loading from " + table.toString());
			long baseOffset = table.offset;
			int count = table.count;
			for (int i = 0; i < count; i++) {
				long dataOffset = baseOffset + (i * FE4Data.StaticCharacterSize);
				if (!isHeadered) {
					dataOffset -= 0x200;
				}
				byte[] charData = handler.readBytesAtOffset(dataOffset, FE4Data.StaticCharacterSize);
				FE4StaticCharacter holyChar = new FE4StaticCharacter(charData, dataOffset);
				FE4Data.Character fe4Character = FE4Data.Character.valueOf(holyChar.getCharacterID());
				if (fe4Character != null) {
					holyBloodBossCharacters.put(fe4Character, holyChar);
					DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Loaded " + fe4Character.toString() + " as a holy character.");
				} else {
					System.err.println("Invalid holy character found. ID = 0x" + Integer.toHexString(holyChar.getCharacterID()));
				}
			}
			DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Finished loading from " + table.toString());
		}
		
		DebugPrinter.log(DebugPrinter.Key.FE4_CHARACTER_LOADER, "Finished loading bosses with holy blood!");
	}

	public void recordCharacters(RecordKeeper rk, Boolean isInitial, ItemMapper itemMap) {
		
		rk.registerCategory(RecordKeeperCategoryKey + " - " + RecordKeeperSubcategoryGen1);
		rk.registerCategory(RecordKeeperCategoryKey + " - " + RecordKeeperSubcategoryGen2Static);
		rk.registerCategory(RecordKeeperCategoryKey + " - " + RecordKeeperSubcategoryGen2Subs);
		rk.registerCategory(RecordKeeperCategoryKey + " - " + RecordKeeperSubcategoryGen2Child);
		rk.registerCategory(RecordKeeperCategoryKey + " - " + RecordKeeperSubcategoryBoss1);
		rk.registerCategory(RecordKeeperCategoryKey + " - " + RecordKeeperSubcategoryBoss2);
		rk.registerCategory(RecordKeeperCategoryKey + " - " + RecordKeeperSubcategoryHolyBoss);
		
		for (FE4Data.Character fe4Char : staticPlayableCharacters.keySet()) {
			if (fe4Char.isGen1() && fe4Char.isPlayable()) {
				recordStaticCharacter(rk, isInitial, fe4Char, staticPlayableCharacters.get(fe4Char), RecordKeeperSubcategoryGen1, itemMap);
			} else if (fe4Char.isGen2() && fe4Char.isStatic() && !fe4Char.isSubstitute()) {
				recordStaticCharacter(rk, isInitial, fe4Char, staticPlayableCharacters.get(fe4Char), RecordKeeperSubcategoryGen2Static, itemMap);
			} else if (fe4Char.isGen2() && fe4Char.isSubstitute()) {
				recordStaticCharacter(rk, isInitial, fe4Char, staticPlayableCharacters.get(fe4Char), RecordKeeperSubcategoryGen2Subs, itemMap);
			}
		}
		
		for (FE4Data.Character fe4Char : childCharacters.keySet()) {
			recordChildCharacter(rk, isInitial, fe4Char, childCharacters.get(fe4Char), RecordKeeperSubcategoryGen2Child, itemMap);
		}
		
		for (FE4Data.Character fe4Char : bossCharacters.keySet()) {
			if (fe4Char.isGen1() && fe4Char.isBoss()) {
				recordEnemy(rk, isInitial, fe4Char, bossCharacters.get(fe4Char), RecordKeeperSubcategoryBoss1, itemMap);
			} else if (fe4Char.isGen2() && fe4Char.isBoss()) {
				recordEnemy(rk, isInitial, fe4Char, bossCharacters.get(fe4Char), RecordKeeperSubcategoryBoss2, itemMap);
			}
		}
		
		for (FE4Data.Character fe4Char : holyBloodBossCharacters.keySet()) {
			// Bosses don't have an item map because none of their items are tracked.
			recordStaticCharacter(rk, isInitial, fe4Char, holyBloodBossCharacters.get(fe4Char), RecordKeeperSubcategoryHolyBoss, null);
		}
	}
	
	private void recordData(RecordKeeper rk, boolean isInitial, String category, String entryKey, String key, String value) {
		if (isInitial) {
			rk.recordOriginalEntry(category, entryKey, key, value);
		} else {
			rk.recordUpdatedEntry(category, entryKey, key, value);
		}
	}
	
	private void recordEnemy(RecordKeeper rk, boolean isInitial, FE4Data.Character character, FE4EnemyCharacter enemyChar, String subcategory, ItemMapper itemMap) {
		String name = character.toString();
		FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(enemyChar.getClassID());
		String className = charClass != null ? charClass.toString() : "Unknown [0x" + Integer.toHexString(enemyChar.getClassID()).toUpperCase() + "]";
		
		String category = RecordKeeperCategoryKey + " - " + subcategory;
		
		recordData(rk, isInitial, category, name, "Class", className);
		
		recordData(rk, isInitial, category, name, "Level", Integer.toString(enemyChar.getLevel()));
		recordData(rk, isInitial, category, name, "Leadership", Integer.toString(enemyChar.getLeadership()));
		
		int equipment1 = enemyChar.getEquipment1();
		FE4Data.Item item1 = FE4Data.Item.valueOf(equipment1);
		if (item1 == null) {
			recordData(rk, isInitial, category, name, "Equipment 1", "None");
		} else {
			recordData(rk, isInitial, category, name, "Equipment 1", item1.toString());
		}
		
		int equipment2 = enemyChar.getEquipment2();
		FE4Data.Item item2 = FE4Data.Item.valueOf(equipment2);
		if (item2 == null) {
			recordData(rk, isInitial, category, name, "Equipment 2", "None");
		} else {
			recordData(rk, isInitial, category, name, "Equipment 2", item2.toString());
		}
		
		int drop = enemyChar.getDropableEquipment();
		FE4Data.Item dropItem = itemMap.getItemAtIndex(drop);
		if (dropItem == null) {
			recordData(rk, isInitial, category, name, "Drop", "None");
		} else {
			recordData(rk, isInitial, category, name, "Drop", "[0x" + Integer.toHexString(drop).toUpperCase() + "] " + dropItem.toString());
		}
	}
	
	private void recordChildCharacter(RecordKeeper rk, boolean isInitial, FE4Data.Character character, FE4ChildCharacter childChar, String subcategory, ItemMapper itemMap) {
		String name = character.toString();
		FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(childChar.getClassID());
		String className = charClass != null ? charClass.toString() : "Unknown [0x" + Integer.toHexString(childChar.getClassID()).toUpperCase() + "]";
		
		String category = RecordKeeperCategoryKey + " - " + subcategory;
		
		recordData(rk, isInitial, category, name, "Class", className);
		
		int equipment1 = childChar.getEquipment1();
		FE4Data.Item item1 = itemMap.getItemAtIndex(equipment1);
		if (item1 == null) {
			recordData(rk, isInitial, category, name, "Equipment 1", "None");
		} else {
			recordData(rk, isInitial, category, name, "Equipment 1", "[0x" + Integer.toHexString(equipment1).toUpperCase() + "] " + item1.toString());
		}
		
		int equipment2 = childChar.getEquipment2();
		FE4Data.Item item2 = itemMap.getItemAtIndex(equipment2);
		if (item2 == null) {
			recordData(rk, isInitial, category, name, "Equipment 2", "None");
		} else {
			recordData(rk, isInitial, category, name, "Equipment 2", "[0x" + Integer.toHexString(equipment2).toUpperCase() + "] " + item2.toString());
		}
		
		recordData(rk, isInitial, category, name, "Primary Influence", childChar.getMajorInfluence().toString());
	}
	
	private void recordStaticCharacter(RecordKeeper rk, boolean isInitial, FE4Data.Character character, FE4StaticCharacter staticChar, String subcategory, ItemMapper itemMap) {
		String name = character.toString();
		FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
		String className = charClass != null ? charClass.toString() : "Unknown [0x" + Integer.toHexString(staticChar.getClassID()).toUpperCase() + "]";
		
		String category = RecordKeeperCategoryKey + " - " + subcategory;
		
		recordData(rk, isInitial, category, name, "Class", className);
			
		recordData(rk, isInitial, category, name, "Personal HP Growth", String.format("%d%%", staticChar.getHPGrowth()));
		recordData(rk, isInitial, category, name, "Personal STR Growth", String.format("%d%%", staticChar.getSTRGrowth()));
		recordData(rk, isInitial, category, name, "Personal MAG Growth", String.format("%d%%", staticChar.getMAGGrowth()));
		recordData(rk, isInitial, category, name, "Personal SKL Growth", String.format("%d%%", staticChar.getSKLGrowth()));
		recordData(rk, isInitial, category, name, "Personal SPD Growth", String.format("%d%%", staticChar.getSPDGrowth()));
		recordData(rk, isInitial, category, name, "Personal DEF Growth", String.format("%d%%", staticChar.getDEFGrowth()));
		recordData(rk, isInitial, category, name, "Personal RES Growth", String.format("%d%%", staticChar.getRESGrowth()));
		recordData(rk, isInitial, category, name, "Personal LCK Growth", String.format("%d%%", staticChar.getLCKGrowth()));
			
		recordData(rk, isInitial, category, name, "Base HP", Integer.toString(staticChar.getBaseHP()));
		recordData(rk, isInitial, category, name, "Base STR", Integer.toString(staticChar.getBaseSTR()));
		recordData(rk, isInitial, category, name, "Base MAG", Integer.toString(staticChar.getBaseMAG()));
		recordData(rk, isInitial, category, name, "Base SKL", Integer.toString(staticChar.getBaseSKL()));
		recordData(rk, isInitial, category, name, "Base SPD", Integer.toString(staticChar.getBaseSPD()));
		recordData(rk, isInitial, category, name, "Base DEF", Integer.toString(staticChar.getBaseDEF()));
		recordData(rk, isInitial, category, name, "Base RES", Integer.toString(staticChar.getBaseRES()));
		recordData(rk, isInitial, category, name, "Base LCK", Integer.toString(staticChar.getBaseLCK()));
			
		List<FE4Data.SkillSlot1> skillSlot1 = FE4Data.SkillSlot1.slot1Skills(staticChar.getSkillSlot1Value());
		if (skillSlot1.isEmpty()) {
			recordData(rk, isInitial, category, name, "Personal Skill Slot 1", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Data.SkillSlot1 skill : skillSlot1) {
				sb.append(skill.toString() + "<br>");
			}
			recordData(rk, isInitial, category, name, "Personal Skill Slot 1", sb.toString());
		}
		
		List<FE4Data.SkillSlot2> skillSlot2 = FE4Data.SkillSlot2.slot2Skills(staticChar.getSkillSlot2Value());
		if (skillSlot2.isEmpty()) {
			recordData(rk, isInitial, category, name, "Personal Skill Slot 2", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Data.SkillSlot2 skill : skillSlot2) {
				sb.append(skill.toString() + "<br>");
			}
			recordData(rk, isInitial, category, name, "Personal Skill Slot 2", sb.toString());
		}
		
		List<FE4Data.SkillSlot3> skillSlot3 = FE4Data.SkillSlot3.slot3Skills(staticChar.getSkillSlot3Value());
		if (skillSlot3.isEmpty()) {
			recordData(rk, isInitial, category, name, "Personal Skill Slot 3", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Data.SkillSlot3 skill : skillSlot3) {
				sb.append(skill.toString() + "<br>");
			}
			recordData(rk, isInitial, category, name, "Personal Skill Slot 3", sb.toString());
		}
		
		List<FE4Data.HolyBloodSlot1> holyBloodSlot1 = FE4Data.HolyBloodSlot1.slot1HolyBlood(staticChar.getHolyBlood1Value());
		if (holyBloodSlot1.isEmpty()) {
			recordData(rk, isInitial, category, name, "Holy Blood Slot 1", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Data.HolyBloodSlot1 holyBlood : holyBloodSlot1) {
				sb.append(holyBlood.toString() + "<br>");
			}
			recordData(rk, isInitial, category, name, "Holy Blood Slot 1", sb.toString());
		}
		
		List<FE4Data.HolyBloodSlot2> holyBloodSlot2 = FE4Data.HolyBloodSlot2.slot2HolyBlood(staticChar.getHolyBlood2Value());
		if (holyBloodSlot2.isEmpty()) {
			recordData(rk, isInitial, category, name, "Holy Blood Slot 2", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Data.HolyBloodSlot2 holyBlood : holyBloodSlot2) {
				sb.append(holyBlood.toString() + "<br>");
			}
			recordData(rk, isInitial, category, name, "Holy Blood Slot 2", sb.toString());
		}
		
		List<FE4Data.HolyBloodSlot3> holyBloodSlot3 = FE4Data.HolyBloodSlot3.slot3HolyBlood(staticChar.getHolyBlood3Value());
		if (holyBloodSlot3.isEmpty()) {
			recordData(rk, isInitial, category, name, "Holy Blood Slot 3", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Data.HolyBloodSlot3 holyBlood : holyBloodSlot3) {
				sb.append(holyBlood.toString() + "<br>");
			}
			recordData(rk, isInitial, category, name, "Holy Blood Slot 3", sb.toString());
		}
		
		List<FE4Data.HolyBloodSlot4> holyBloodSlot4 = FE4Data.HolyBloodSlot4.slot4HolyBlood(staticChar.getHolyBlood4Value());
		if (holyBloodSlot4.isEmpty()) {
			recordData(rk, isInitial, category, name, "Holy Blood Slot 4", "None");
		} else {
			StringBuilder sb = new StringBuilder();
			for (FE4Data.HolyBloodSlot4 holyBlood : holyBloodSlot4) {
				sb.append(holyBlood.toString() + "<br>");
			}
			recordData(rk, isInitial, category, name, "Holy Blood Slot 4", sb.toString());
		}
		
		int equipment1 = staticChar.getEquipment1();
		FE4Data.Item item1 = itemMap != null ? itemMap.getItemAtIndex(equipment1) : FE4Data.Item.valueOf(equipment1);
		if (item1 == null) {
			recordData(rk, isInitial, category, name, "Equipment 1", "None");
		} else {
			if (itemMap != null) {
				recordData(rk, isInitial, category, name, "Equipment 1", "[0x" + Integer.toHexString(equipment1).toUpperCase() + "] " + item1.toString());
			} else {
				recordData(rk, isInitial, category, name, "Equipment 1", item1.toString());
			}
		}
		
		int equipment2 = staticChar.getEquipment2();
		FE4Data.Item item2 = itemMap != null ? itemMap.getItemAtIndex(equipment2) : FE4Data.Item.valueOf(equipment2);
		if (item2 == null) {
			recordData(rk, isInitial, category, name, "Equipment 2", "None");
		} else {
			if (itemMap != null) {
				recordData(rk, isInitial, category, name, "Equipment 2", "[0x" + Integer.toHexString(equipment2).toUpperCase() + "] " + item2.toString());
			}  else {
				recordData(rk, isInitial, category, name, "Equipment 2", item2.toString());
			}
		}
		
		int equipment3 = staticChar.getEquipment3();
		FE4Data.Item item3 = itemMap != null ? itemMap.getItemAtIndex(equipment3) : FE4Data.Item.valueOf(equipment3);
		if (item3 == null) {
			recordData(rk, isInitial, category, name, "Equipment 3", "None");
		} else {
			if (itemMap != null) {
				recordData(rk, isInitial, category, name, "Equipment 3", "[0x" + Integer.toHexString(equipment3).toUpperCase() + "] " + item3.toString());
			}  else {
				recordData(rk, isInitial, category, name, "Equipment 3", item3.toString());
			}
		}
	}
}
