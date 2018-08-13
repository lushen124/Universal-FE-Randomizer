package fedata;

public interface FEChapter {
	
	public int lordLeaderID();
	public int bossLeaderID();
	
	public FEChapterUnit[] allUnits();
	public Boolean isClassSafe();
	
	public FEChapterItem[] allRewards();
	
	public long[] getFightAddresses();
	public int fightCommandLength();
	public byte[] fightReplacementBytes();
	
	public String getFriendlyName();
	
	public Boolean shouldBeSimplified();
}
