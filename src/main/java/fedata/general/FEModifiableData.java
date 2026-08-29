package fedata.general;

public interface FEModifiableData {
	public void resetData();
	public void commitChanges();
	public byte[] getData();
	public Boolean hasCommittedChanges();
	
	public Boolean wasModified();
	public long getAddressOffset();
}
