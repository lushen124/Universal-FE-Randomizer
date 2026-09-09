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
import fedata.gba.GBAFECharacterData.Affinity;
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
	
	private GBAFECharacterData nullCharacterData = null;
	
	// FE7 (probably FE6 and FE8 in some cases as well) likes to use character data
	// for weapon ranks, instead of relying on class data. This means we have to adopt
	// a more consistent change in determining minion classes.
	private Map<Integer, GBAFECharacterData> minionData = new HashMap<Integer, GBAFECharacterData>();
	
	public static final String RecordKeeperCategoryKey = "Characters";
	public static final String RecordKeeperCategoryKeyBosses = "Characters - Bosses";
	
	public CharacterDataLoader(GBAFECharacterProvider provider, FileHandler handler) {
		super();
		this.provider = provider;
		long baseAddress = FileReadHelper.readAddress(handler, provider.characterDataTablePointer());
		for (int i = 0; i < provider.numberOfCharacters(); i++) {
			long offset = baseAddress + (provider.bytesPerCharacter() * i);
			GBAFECharacter character = provider.characterWithID(i);
			byte[] charData = handler.readBytesAtOffset(offset, provider.bytesPerCharacter());
			GBAFECharacterData characterData = provider.characterDataWithData(charData, offset, character.isClassLimited());
			if (provider.isValidCharacter(character)) {
				characterMap.put(character.getID(), characterData);
			} else {
				minionData.put(i, characterData);
			}
			
			if (character == provider.nullCharacter()) {
				nullCharacterData = characterData;
			}
		}
		Map<Integer, GBAFECharacter> counters = provider.counters();
		for (int characterID : counters.keySet()) {
			counterMap.put(characterID, characterMap.get(counters.get(characterID).getID()));
		}
	}
	
	public void applyLevelCorrectionsIfNecessary() {
		for (GBAFECharacter character : provider.allPlayableCharacters()) {
			Integer canonicalLevel = provider.canonicalLevelForCharacter(character);
			if (canonicalLevel != null) {
				GBAFECharacterData characterData = characterWithID(character.getID());
				characterData.setLevel(canonicalLevel);
				characterData.commitChanges();
			}
		}
	}
	
	public String debugStringForCharacter(int characterID) {
		return provider.characterWithID(characterID).toString();
	}
	
	public GBAFECharacterData characterWithID(int characterID) {
		GBAFECharacterData charData = characterMap.get(characterID);
		if (charData == null) {
			charData = minionData.get(characterID);
		}
		
		if (charData == null) {
			return nullCharacterData;
		}
		
		return charData;
	}
	
	public GBAFECharacterData[] playableCharacters() {
		return feCharactersFromSet(provider.allPlayableCharacters());
	}
	
	public List<GBAFECharacterData> canonicalPlayableCharacters(boolean includeExtras) {
		List<GBAFECharacterData> charList = new ArrayList<GBAFECharacterData>(Arrays.asList(playableCharacters()));
		if (includeExtras) {
			charList.addAll(Arrays.asList(feCharactersFromSet(provider.extraCharacters())));
		}
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
	
	public GBAFECharacterData minionCharacterWithID(int minionID) {
		return minionData.get(minionID);
	}
	
	public int appearanceChapter(GBAFECharacterData character) {
		return provider.appearanceChapter(character.getID());
	}
	
	public int chapterCount() {
		return provider.chapterCount();
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
	
	public Boolean isSpecialCharacterID(int characterID) {
		return provider.characterWithID(characterID).isSpecial();
	}
	
	public Boolean canBuff(int characterID) {
		return provider.characterWithID(characterID).canBuff();
	}
	
	public boolean isEnemyAtAnyPoint(int characterID) {
		return provider.isEnemyAtAnyPoint(characterID);
	}
	
	public int[] validAffinityValues() {
		return provider.affinityValues();
	}
	
	public int getAffinityValue(Affinity affinity) {
		return provider.affinityValueForAffinity(affinity);
	}
	
	public Boolean characterIDRequiresAttack(int characterID) {
		return provider.characterWithID(characterID).requiresAttack();
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
		for (GBAFECharacterData minion : minionData.values()) {
			minion.commitChanges();
			if (minion.hasCommittedChanges()) {
				Diff minionDiff = new Diff(minion.getAddressOffset(), minion.getData().length, minion.getData(), null);
				compiler.addDiff(minionDiff);
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

	public void recordCharacters(RecordKeeper rk, Boolean isInitial, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData) {
		for (GBAFECharacterData character : playableCharacters()) {
			recordCharacter(rk, character, isInitial, classData, itemData, textData, RecordKeeperCategoryKey);
		}
		rk.registerCategory(RecordKeeperCategoryKeyBosses);
		for (GBAFECharacterData boss : bossCharacters()) {
			recordCharacter(rk, boss, isInitial, classData, itemData, textData, RecordKeeperCategoryKeyBosses);
		}
		rk.sortKeysInCategory(RecordKeeperCategoryKeyBosses);
	}

	private void recordCharacter(RecordKeeper rk, GBAFECharacterData character, Boolean isInitial, ClassDataLoader classData, ItemDataLoader itemData, TextLoader textData, String category) {
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
			rk.recordOriginalEntry(category, internalName, "Name", name);
			rk.recordOriginalEntry(category, internalName, "Class", classValue);
			rk.recordOriginalEntry(category, internalName, "Level", Integer.toString(character.getLevel()));
			
			rk.recordOriginalEntry(category, internalName, "HP Growth", String.format("%d%%", character.getHPGrowth()));
			rk.recordOriginalEntry(category, internalName, "STR/MAG Growth", String.format("%d%%", character.getSTRGrowth()));
			rk.recordOriginalEntry(category, internalName, "SKL Growth", String.format("%d%%", character.getSKLGrowth()));
			rk.recordOriginalEntry(category, internalName, "SPD Growth", String.format("%d%%", character.getSPDGrowth()));
			rk.recordOriginalEntry(category, internalName, "LCK Growth", String.format("%d%%", character.getLCKGrowth()));
			rk.recordOriginalEntry(category, internalName, "DEF Growth", String.format("%d%%", character.getDEFGrowth()));
			rk.recordOriginalEntry(category, internalName, "RES Growth", String.format("%d%%", character.getRESGrowth()));
			
			rk.recordOriginalEntry(category, internalName, "Base HP", Integer.toString(character.getBaseHP() + charClass.getBaseHP()));
			rk.recordOriginalEntry(category, internalName, "Base STR/MAG", Integer.toString(character.getBaseSTR() + charClass.getBaseSTR()));
			rk.recordOriginalEntry(category, internalName, "Base SKL", Integer.toString(character.getBaseSKL() + charClass.getBaseSKL()));
			rk.recordOriginalEntry(category, internalName, "Base SPD", Integer.toString(character.getBaseSPD() + charClass.getBaseSPD()));
			rk.recordOriginalEntry(category, internalName, "Base LCK", Integer.toString(character.getBaseLCK() + charClass.getBaseLCK()));
			rk.recordOriginalEntry(category, internalName, "Base DEF", Integer.toString(character.getBaseDEF() + charClass.getBaseDEF()));
			rk.recordOriginalEntry(category, internalName, "Base RES", Integer.toString(character.getBaseRES() + charClass.getBaseRES()));
			
			rk.recordOriginalEntry(category, internalName, "Base CON", Integer.toString(character.getConstitution() + charClass.getCON()));
			
			rk.recordOriginalEntry(category, internalName, "Sword Rank", itemData.rankForValue(character.getSwordRank()).displayString());
			rk.recordOriginalEntry(category, internalName, "Lance Rank", itemData.rankForValue(character.getLanceRank()).displayString());
			rk.recordOriginalEntry(category, internalName, "Axe Rank", itemData.rankForValue(character.getAxeRank()).displayString());
			rk.recordOriginalEntry(category, internalName, "Bow Rank", itemData.rankForValue(character.getBowRank()).displayString());
			rk.recordOriginalEntry(category, internalName, "Anima Rank", itemData.rankForValue(character.getAnimaRank()).displayString());
			rk.recordOriginalEntry(category, internalName, "Light Rank", itemData.rankForValue(character.getLightRank()).displayString());
			rk.recordOriginalEntry(category, internalName, "Dark Rank", itemData.rankForValue(character.getDarkRank()).displayString());
			rk.recordOriginalEntry(category, internalName, "Staff Rank", itemData.rankForValue(character.getStaffRank()).displayString());
			
			rk.recordOriginalEntry(category, internalName, "Affinity", character.getAffinityName() + " (0x" + Integer.toHexString(character.getAffinityValue()).toUpperCase() + ")");
		} else {
			rk.recordUpdatedEntry(category, internalName, "Name", name);
			rk.recordUpdatedEntry(category, internalName, "Class", classValue);
			rk.recordUpdatedEntry(category, internalName, "Level", Integer.toString(character.getLevel()));
			
			rk.recordUpdatedEntry(category, internalName, "HP Growth", String.format("%d%%", character.getHPGrowth()));
			rk.recordUpdatedEntry(category, internalName, "STR/MAG Growth", String.format("%d%%", character.getSTRGrowth()));
			rk.recordUpdatedEntry(category, internalName, "SKL Growth", String.format("%d%%", character.getSKLGrowth()));
			rk.recordUpdatedEntry(category, internalName, "SPD Growth", String.format("%d%%", character.getSPDGrowth()));
			rk.recordUpdatedEntry(category, internalName, "LCK Growth", String.format("%d%%", character.getLCKGrowth()));
			rk.recordUpdatedEntry(category, internalName, "DEF Growth", String.format("%d%%", character.getDEFGrowth()));
			rk.recordUpdatedEntry(category, internalName, "RES Growth", String.format("%d%%", character.getRESGrowth()));
			
			rk.recordUpdatedEntry(category, internalName, "Base HP", Integer.toString(character.getBaseHP() + charClass.getBaseHP()));
			rk.recordUpdatedEntry(category, internalName, "Base STR/MAG", Integer.toString(character.getBaseSTR() + charClass.getBaseSTR()));
			rk.recordUpdatedEntry(category, internalName, "Base SKL", Integer.toString(character.getBaseSKL() + charClass.getBaseSKL()));
			rk.recordUpdatedEntry(category, internalName, "Base SPD", Integer.toString(character.getBaseSPD() + charClass.getBaseSPD()));
			rk.recordUpdatedEntry(category, internalName, "Base LCK", Integer.toString(character.getBaseLCK() + charClass.getBaseLCK()));
			rk.recordUpdatedEntry(category, internalName, "Base DEF", Integer.toString(character.getBaseDEF() + charClass.getBaseDEF()));
			rk.recordUpdatedEntry(category, internalName, "Base RES", Integer.toString(character.getBaseRES() + charClass.getBaseRES()));
			
			rk.recordUpdatedEntry(category, internalName, "Base CON", Integer.toString(character.getConstitution() + charClass.getCON()));
			
			rk.recordUpdatedEntry(category, internalName, "Sword Rank", itemData.rankForValue(character.getSwordRank()).displayString());
			rk.recordUpdatedEntry(category, internalName, "Lance Rank", itemData.rankForValue(character.getLanceRank()).displayString());
			rk.recordUpdatedEntry(category, internalName, "Axe Rank", itemData.rankForValue(character.getAxeRank()).displayString());
			rk.recordUpdatedEntry(category, internalName, "Bow Rank", itemData.rankForValue(character.getBowRank()).displayString());
			rk.recordUpdatedEntry(category, internalName, "Anima Rank", itemData.rankForValue(character.getAnimaRank()).displayString());
			rk.recordUpdatedEntry(category, internalName, "Light Rank", itemData.rankForValue(character.getLightRank()).displayString());
			rk.recordUpdatedEntry(category, internalName, "Dark Rank", itemData.rankForValue(character.getDarkRank()).displayString());
			rk.recordUpdatedEntry(category, internalName, "Staff Rank", itemData.rankForValue(character.getStaffRank()).displayString());
			
			rk.recordUpdatedEntry(category, internalName, "Affinity", character.getAffinityName() + " (0x" + Integer.toHexString(character.getAffinityValue()).toUpperCase() + ")");
		}
		
	}
}
