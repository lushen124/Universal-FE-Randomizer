package util;

public class AddressRange {
	public final long start;
	public final long end;
	
	public AddressRange(long startAddress, long endAddress) {
		start = startAddress;
		end = endAddress;
	}
	
	public Boolean contains(long address) {
		return (address >= start && address < end);
	}
}
