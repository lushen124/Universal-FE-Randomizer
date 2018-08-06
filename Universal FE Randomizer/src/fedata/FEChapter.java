package fedata;

public interface FEChapter {
	
	public FEChapterUnit[] allUnits();
	public Boolean isClassSafe();
	
	public FEChapterItem[] allRewards();
	
	public String getFriendlyName();
}
