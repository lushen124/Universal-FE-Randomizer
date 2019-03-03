package fedata.gba.general;

import java.util.Map;
import java.util.Set;

import fedata.gba.GBAFECharacterData;

public interface GBAFECharacterProvider {
	
	public long characterDataTablePointer();
	public int numberOfCharacters();
	public int bytesPerCharacter();
	
	public GBAFECharacter[] allCharacters();
	public Map<Integer, GBAFECharacter> counters();
	
	public Set<GBAFECharacter> allPlayableCharacters();
	public Set<GBAFECharacter> allBossCharacters();
	public Set<GBAFECharacter> linkedCharacters(int characterID);
	
	public Set<GBAFECharacter> charactersExcludedFromRandomRecruitment();
	
	public Set<Integer> linkedPortraitIDs(int characterID);
	
	public Set<GBAFECharacter> allFliers();
	public Set<GBAFECharacter> mustAttack();
	public Set<GBAFECharacter> femaleSet();
	public Set<GBAFECharacter> mustPromote();
	
	public GBAFECharacter characterWithID(int characterID);
	
	public int[] affinityValues();
	
	public int canonicalID(int characterID);
	
	public GBAFECharacterData characterDataWithData(byte[] data, long offset, Boolean hasLimitedClasses);

}
