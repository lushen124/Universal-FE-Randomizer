package fedata.general;

public interface FEOverrideableAddress {

	public void setAddressOverride(long newAddress);
	public long getOverrideAddress();
	
	public long getOriginalAddress();
	
}
