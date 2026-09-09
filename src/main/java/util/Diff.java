package util;

public class Diff {
	public long address;
	public int length;
	public byte[] changes;
	public byte[] requiredOldValues;
	
	public Diff(long address, int length, byte[] changes, byte[] requiredOldValues) {
		super();
		this.address = address;
		this.length = length;
		this.changes = changes;
		this.requiredOldValues = requiredOldValues;
	}
}
