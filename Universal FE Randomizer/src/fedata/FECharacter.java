package fedata;

public interface FECharacter {
	public int getNameIndex();
	public int getDescriptionIndex();
	
	public int getHPGrowth();
	public void setHPGrowth(int hpGrowth);
	
	public int getSTRGrowth();
	public void setSTRGrowth(int strGrowth);
	
	public int getSKLGrowth();
	
	public void setSKLGrowth(int sklGrowth);
	public int getSPDGrowth();
	
	public void setSPDGrowth(int spdGrowth);
	public int getDEFGrowth();
	
	public void setDEFGrowth(int defGrowth);
	
	public int getRESGrowth();
	public void setRESGrowth(int resGrowth);
	
	public int getLCKGrowth();
	public void setLCKGrowth(int lckGrowth);
	
	public void resetData();
	public byte[] getCharacterData();
	
	public Boolean wasModified();
	public long getAddressOffset();
}
