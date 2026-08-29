package fedata.gba.general;

import java.util.Map;
import java.util.Set;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFECharacterData.Affinity;

public interface GBAFECharacterProvider {
	
	public long characterDataTablePointer();
	public int numberOfCharacters();
	public int bytesPerCharacter();
	
	public GBAFECharacter[] allCharacters();
	public Map<Integer, GBAFECharacter> counters();
	
	public Set<GBAFECharacter> allPlayableCharacters();
	public Set<GBAFECharacter> extraCharacters();
	public Set<GBAFECharacter> allBossCharacters();
	public Set<GBAFECharacter> linkedCharacters(int characterID);
	
	public int appearanceChapter(int characterID);
	public int chapterCount();
	
	public Set<GBAFECharacter> charactersExcludedFromRandomRecruitment();
	
	public Set<Integer> linkedPortraitIDs(int characterID);
	
	public Set<GBAFECharacter> allFliers();
	public Set<GBAFECharacter> mustAttack();
	public Set<GBAFECharacter> femaleSet();
	public Set<GBAFECharacter> mustPromote();
	
	public GBAFECharacter characterWithID(int characterID);
	public boolean isValidCharacter(GBAFECharacter character);
	public GBAFECharacter nullCharacter();
	
	public boolean isEnemyAtAnyPoint(int characterID);
	
	public int[] affinityValues();
	public int affinityValueForAffinity(GBAFECharacterData.Affinity affinity);
	
	public int canonicalID(int characterID);
	
	// Some characters have levels in their character data that don't match with their chapter data.
	// Since our calculations rely on character data and not chapter data, some discrepancies might be significant.
	// This method will return the "correct" character level as they appear in gameplay if it doesn't match.
	// If the character data is consistent with the canonical level, this method should return null.
	public Integer canonicalLevelForCharacter(GBAFECharacter character);
	
	public GBAFECharacterData characterDataWithData(byte[] data, long offset, Boolean hasLimitedClasses);
}
