package random.gba.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.general.GBAFECharacter;
import fedata.gba.general.GBAFECharacterProvider;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.recordkeeper.RecordKeeper;

public class CharacterDataLoader {
	private GBAFECharacterProvider provider;
	
	private Map<Integer, GBAFECharacterData> characterMap = new HashMap<Integer, GBAFECharacterData>();
	private Map<Integer, GBAFECharacterData> counterMap = new HashMap<Integer, GBAFECharacterData>();
	
	public static final String RecordKeeperCategoryKey = "Characters";
	
	public CharacterDataLoader(GBAFECharacterProvider provider, FileHandler handler) {
		super();
		this.provider = provider;
		long baseAddress = FileReadHelper.readAddress(handler, provider.characterDataTablePointer());
		for (GBAFECharacter character : provider.allCharacters()) {
			long offset = baseAddress + (provider.bytesPerCharacter() * character.getID());
			byte[] charData = handler.readBytesAtOffset(offset, provider.bytesPerCharacter());
			characterMap.put(character.getID(), provider.characterDataWithData(charData, offset, provider.characterWithID(character.getID()).isClassLimited()));
		}
		Map<Integer, GBAFECharacter> counters = provider.counters();
		for (int characterID : counters.keySet()) {
			counterMap.put(characterID, characterMap.get(counters.get(characterID).getID()));
		}
	}
	
	public String debugStringForCharacter(int characterID) {
		return provider.characterWithID(characterID).toString();
	}
	
	public GBAFECharacterData characterWithID(int characterID) {
		return characterMap.get(characterID);
	}
	
	public GBAFECharacterData[] playableCharacters() {
		return feCharactersFromSet(provider.allPlayableCharacters());
	}
	
	public List<GBAFECharacterData> canonicalPlayableCharacters() {
		List<GBAFECharacterData> charList = new ArrayList<GBAFECharacterData>(Arrays.asList(playableCharacters()));
		return charList.stream().filter(character -> {
			return provider.canonicalID(character.getID()) == character.getID();
		}).collect(Collectors.toList());
	}
	
	public List<GBAFECharacterData> charactersExcludedFromRandomRecruitment() {
		return provider.charactersExcludedFromRandomRecruitment().stream().sorted(GBAFECharacter.getIDComparator()).map(gbaFEChar -> {
			return characterWithID(gbaFEChar.getID());
		}).collect(Collectors.toList());
	}
	
	public GBAFECharacterData[] bossCharacters() {
		return feCharactersFromSet(provider.allBossCharacters());
	}
	
	public Boolean isPlayableCharacterID(int characterID) {
		return provider.characterWithID(characterID).isPlayable();
	}
	
	public Boolean isBossCharacterID(int characterID) {
		return provider.characterWithID(characterID).isBoss();
	}
	
	// Generally used for minions, whose character IDs we don't track, so nulls are probably pointing to minion characters.
		// Those are generally safe to change.
	public Boolean canChangeCharacterID(int characterID) {
		return provider.characterWithID(characterID).canChange();
	}
	
	public Boolean isLordCharacterID(int characterID) {
		return provider.characterWithID(characterID).isLord();
	}
	
	public Boolean isThiefCharacterID(int characterID) {
		return provider.characterWithID(characterID).isThief();
	}
	
	public Boolean canBuff(int characterID) {
		return provider.characterWithID(characterID).canBuff();
	}
	
	public int[] validAffinityValues() {
		return provider.affinityValues();
	}
	
	public Boolean characterIDRequiresRange(int characterID) {
		return provider.characterWithID(characterID).requiresRange();
	}
	
	public Boolean characterIDRequiresMelee(int characterID) {
		return provider.characterWithID(characterID).requiresMelee();
	}
	
	public Boolean mustAttack(int characterID) {
		GBAFECharacter character = provider.characterWithID(characterID);
		return provider.mustAttack().contains(character);
	}
	
	public Boolean isFemale(int characterID) {
		GBAFECharacter character = provider.characterWithID(characterID);
		return provider.femaleSet().contains(character);
	}
	
	public Boolean mustPromote(int characterID) {
		GBAFECharacter character = provider.characterWithID(characterID);
		return provider.mustPromote().contains(character);
	}
	
	public GBAFECharacterData characterRequiresCounterToCharacter(GBAFECharacterData character) {
		return counterMap.get(character.getID());
	}
	
	public Set<Integer> multiPortraitsForCharacter(int characterID) {
		return provider.linkedPortraitIDs(characterID);
	}
	
	public GBAFECharacterData[] linkedCharactersForCharacter(GBAFECharacterData character) {
		return feCharactersFromSet(provider.linkedCharacters(character.getID()));
	}
	
	public Boolean isFlyingCharacter(int characterID) {
		GBAFECharacter character = provider.characterWithID(characterID);
		return provider.allFliers().contains(character);
	}
	
	public int getCanonicalIDForCharacter(GBAFECharacterData character) {
		return provider.canonicalID(character.getID());
	}
	
	public void commit() {
		for (GBAFECharacterData character : characterMap.values()) {
			character.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (GBAFECharacterData character : characterMap.values()) {
			character.commitChanges();
			if (character.hasCommittedChanges()) {
				Diff charDiff = new Diff(character.getAddressOffset(), character.getData().length, character.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
	
	private GBAFECharacterData[] feCharactersFromSet(Set<GBAFECharacter> characterSet) {
		List<GBAFECharacter> orderedCharacters = new ArrayList<GBAFECharacter>(characterSet);
		Collections.sort(orderedCharacters, new Comparator<GBAFECharacter>() {
			public int compare(GBAFECharacter arg0, GBAFECharacter arg1) { return Integer.compare(arg0.getID(), arg1.getID()); }
		});
		
		GBAFECharacterData[] characterList = new GBAFECharacterData[orderedCharacters.size()];
		for (int i = 0; i < orderedCharacters.size(); i++) {
			characterList[i] = characterWithID(orderedCharacters.get(i).getID());
		}
		
		return characterList;
	}
	
	public void recordCharacters(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, TextLoader textData) {
		for (GBAFECharacterData character : playableCharacters()) {
			recordCharacter(rk, character, isInitial, classData, textData);
		}
		for (GBAFECharacterData boss : bossCharacters()) {
			recordCharacter(rk, boss, isInitial, classData, textData);
		}
	}
	
	private void recordCharacter(RecordKeeper rk, GBAFECharacterData character, Boolean isInitial, ClassDataLoader classData, TextLoader textData) {
		int nameIndex = character.getNameIndex();
		int classID = character.getClassID();
		GBAFEClassData charClass = classData.classForID(classID);
		int classNameIndex = charClass.getNameIndex();
		GBAFECharacter feChar = provider.characterWithID(character.getID());
		String internalName = feChar.toString();
		
		String name = textData.getStringAtIndex(nameIndex, true).trim();
		String className = textData.getStringAtIndex(classNameIndex, true).trim();
		
		String classValue = className + " (0x" + Integer.toHexString(classID).toUpperCase() + ")";
		if (isInitial) {
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Name", name);
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Class", classValue);
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "HP Growth", String.format("%d%%", character.getHPGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "STR/MAG Growth", String.format("%d%%", character.getSTRGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "SKL Growth", String.format("%d%%", character.getSKLGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "SPD Growth", String.format("%d%%", character.getSPDGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "LCK Growth", String.format("%d%%", character.getLCKGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "DEF Growth", String.format("%d%%", character.getDEFGrowth()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "RES Growth", String.format("%d%%", character.getRESGrowth()));
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Base HP", Integer.toString(character.getBaseHP() + charClass.getBaseHP()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Base STR/MAG", Integer.toString(character.getBaseSTR() + charClass.getBaseSTR()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Base SKL", Integer.toString(character.getBaseSKL() + charClass.getBaseSKL()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Base SPD", Integer.toString(character.getBaseSPD() + charClass.getBaseSPD()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Base LCK", Integer.toString(character.getBaseLCK() + charClass.getBaseLCK()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Base DEF", Integer.toString(character.getBaseDEF() + charClass.getBaseDEF()));
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Base RES", Integer.toString(character.getBaseRES() + charClass.getBaseRES()));
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Base CON", Integer.toString(character.getConstitution() + charClass.getCON()));
			
			rk.recordOriginalEntry(RecordKeeperCategoryKey, internalName, "Affinity", character.getAffinityName() + " (0x" + Integer.toHexString(character.getAffinityValue()).toUpperCase() + ")");
		} else {
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Name", name);
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Class", classValue);
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "HP Growth", String.format("%d%%", character.getHPGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "STR/MAG Growth", String.format("%d%%", character.getSTRGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "SKL Growth", String.format("%d%%", character.getSKLGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "SPD Growth", String.format("%d%%", character.getSPDGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "LCK Growth", String.format("%d%%", character.getLCKGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "DEF Growth", String.format("%d%%", character.getDEFGrowth()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "RES Growth", String.format("%d%%", character.getRESGrowth()));
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Base HP", Integer.toString(character.getBaseHP() + charClass.getBaseHP()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Base STR/MAG", Integer.toString(character.getBaseSTR() + charClass.getBaseSTR()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Base SKL", Integer.toString(character.getBaseSKL() + charClass.getBaseSKL()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Base SPD", Integer.toString(character.getBaseSPD() + charClass.getBaseSPD()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Base LCK", Integer.toString(character.getBaseLCK() + charClass.getBaseLCK()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Base DEF", Integer.toString(character.getBaseDEF() + charClass.getBaseDEF()));
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Base RES", Integer.toString(character.getBaseRES() + charClass.getBaseRES()));
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Base CON", Integer.toString(character.getConstitution() + charClass.getCON()));
			
			rk.recordUpdatedEntry(RecordKeeperCategoryKey, internalName, "Affinity", character.getAffinityName() + " (0x" + Integer.toHexString(character.getAffinityValue()).toUpperCase() + ")");
		}
	}
}
