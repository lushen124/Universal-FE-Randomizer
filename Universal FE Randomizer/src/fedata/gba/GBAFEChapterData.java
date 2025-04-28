package fedata.gba;

public interface GBAFEChapterData {
	
	public int lordLeaderID();
	public int bossLeaderID();
	
	public GBAFEChapterUnitData[] allUnits();
	public Boolean isClassSafe();
	
	public GBAFEChapterItemData[] allRewards();
	public GBAFEChapterItemData[] allTargetedRewards();
	
	public long[] getFightAddresses();
	public int fightCommandLength();
	public byte[] fightReplacementBytes();
	
	public String getFriendlyName();
	
	public Boolean shouldBeSimplified();
	public Boolean shouldCharacterBeUnarmed(int characterID);
	
	public void applyNudges();
	
	public GBAFEChapterItemData chapterItemGivenToCharacter(int characterID);
	
	public int getMaxEnemyClassLimit();
	
	public boolean chapterHasChests();
	public boolean chapterHasVillages();
}
