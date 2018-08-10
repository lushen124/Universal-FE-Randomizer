package fedata;

public interface FEChapter {
	
	public FEChapterUnit[] allUnits();
	public Boolean isClassSafe();
	
	public FEChapterItem[] allRewards();
	
	public long[] getFightAddresses();
	public int fightCommandLength();
	
	public String getFriendlyName();
}
