package fedata;

public interface FEModifiableObject {
	public void resetData();
	public void commitChanges();
	public byte[] getData();
	public Boolean hasCommittedChanges();
	
	public Boolean wasModified();
	public long getAddressOffset();
	
}
