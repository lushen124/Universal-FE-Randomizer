package fedata.gba;

public interface GBAFEChapter {
	
	public int lordLeaderID();
	public int bossLeaderID();
	
	public GBAFEChapterUnit[] allUnits();
	public Boolean isClassSafe();
	
	public GBAFEChapterItem[] allRewards();
	public GBAFEChapterItem[] allTargetedRewards();
	
	public long[] getFightAddresses();
	public int fightCommandLength();
	public byte[] fightReplacementBytes();
	
	public String getFriendlyName();
	
	public Boolean shouldBeSimplified();
	
	public void applyNudges();
	
	public GBAFEChapterItem chapterItemGivenToCharacter(int characterID);
}
