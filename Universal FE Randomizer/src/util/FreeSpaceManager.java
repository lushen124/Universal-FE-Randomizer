package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fedata.general.FEBase;
import io.FileHandler;

public class FreeSpaceManager {
	
	private class AssignedSpace {
		long offset;
		byte[] value;
	}
	
	Map<String, AssignedSpace> changes;
	long freeAddress;
	
	List<AddressRange> internalRanges;
	List<Long> internalFreeAddress;
	
	public FreeSpaceManager(FEBase.GameType gameType, List<AddressRange> internalRanges, FileHandler handler) {
		switch (gameType) {
		case FE6:
			freeAddress = Math.max(0x82D4A0, handler.getFileLength());
			break;
		case FE7:
		case FE8:
			freeAddress = Math.max(0x1000000, handler.getFileLength());
			break;
		default:
			freeAddress = -1;
			break;
		}
		changes = new HashMap<String, AssignedSpace>();
		
		this.internalRanges = internalRanges;
		this.internalFreeAddress = internalRanges.stream().map(range -> (range.start)).collect(Collectors.toList());
	}
	
	public long reserveSpace(int length, String key, boolean byteAligned) {
		if (byteAligned) {
			while ((freeAddress & 0x3) != 0) { freeAddress++; }
		}
		long offset = freeAddress;
		freeAddress += length;
		DebugPrinter.log(DebugPrinter.Key.FREESPACE, "Reserving Space for " + key + " (" + Integer.toString(length) + " bytes) to offset 0x" + Long.toHexString(freeAddress));
		return offset;
	}
	
	public long reserveInternalSpace(int length, String key, boolean byteAligned) {
		for (int i = 0; i < internalRanges.size(); i++) {
			AddressRange range = internalRanges.get(i);
			long internalAddress = internalFreeAddress.get(i);
			if (byteAligned) {
				while ((internalAddress & 0x3) != 0) {
					internalAddress++;
				}
			}
			long bytesRemaining = range.end - internalAddress;
			if (bytesRemaining < length) {
				continue;
			}
			
			long offset = internalAddress;
			
			internalFreeAddress.remove(i);
			internalAddress += length;
			internalFreeAddress.add(i, internalAddress);
			
			return offset;
		}
		
		// Fall back to tacking it onto the end of the ROM.
		return reserveSpace(length, key, byteAligned);
	}
	
	// This is limited, so don't use this unless absolutely necessary.
	public long setValueToInternalSpace(byte[] value, String key, boolean byteAligned) {
		for (int i = 0; i < internalRanges.size(); i++) {
			AddressRange range = internalRanges.get(i);
			long internalAddress = internalFreeAddress.get(i);
			long bytesRemaining = range.end - internalAddress;
			if (byteAligned) {
				while ((internalAddress & 0x3) != 0) {
					internalAddress++;
				}
			}
			if (bytesRemaining < value.length) {
				continue;
			}
			AssignedSpace assignment = changes.get(key);
			if (assignment != null) {
				changes.remove(key);
			}
			
			assignment = new AssignedSpace();
			assignment.offset = internalAddress;
			assignment.value = value.clone();
			changes.put(key, assignment);
			
			DebugPrinter.log(DebugPrinter.Key.FREESPACE, "Assigning internal bytes with key " + key + " to offset 0x" + Long.toHexString(internalAddress));
			
			internalFreeAddress.remove(i);
			internalAddress += value.length;
			internalFreeAddress.add(i, internalAddress);
			
			return assignment.offset;
		}
		
		// Fall back to tacking it onto the end of the ROM.
		return setValue(value, key, byteAligned);
	}
	
	public long setValue(byte[] value, String key) {
		return setValue(value, key, false);
	}
	
	public long setValue(byte[] value, String key, boolean byteAligned) {
		AssignedSpace assignment = changes.get(key);
		if (assignment != null) {
			changes.remove(key);
		}
		
		assignment = new AssignedSpace();
		if (byteAligned) {
			while ((freeAddress & 0x3) != 0) {
				freeAddress++;
			}
		}
		assignment.offset = freeAddress;
		assignment.value = value.clone();
		changes.put(key, assignment);
		
		DebugPrinter.log(DebugPrinter.Key.FREESPACE, "Assigning bytes with key " + key + " to offset 0x" + Long.toHexString(freeAddress));
		
		freeAddress += value.length;
		
		return assignment.offset;
	}
	
	public Boolean hasOffsetForKey(String key) {
		return changes.containsKey(key);
	}
	
	public long getOffsetForKey(String key) {
		AssignedSpace assignment = changes.get(key);
		if (assignment != null) { return assignment.offset; }
		return -1;
	}
	
	public byte[] getByteValuesForKey(String key) {
		AssignedSpace assignment = changes.get(key);
		if (assignment != null) { return assignment.value; }
		return null;
	}

	public void commitChanges(DiffCompiler compiler) {
		for (String key : changes.keySet()) {
			AssignedSpace assignment = changes.get(key);
			DebugPrinter.log(DebugPrinter.Key.FREESPACE, "Commiting values " + WhyDoesJavaNotHaveThese.displayStringForBytes(assignment.value) + " to offset 0x" + Long.toHexString(assignment.offset) + " Key = " + key);
			compiler.addDiff(new Diff(assignment.offset, assignment.value.length, assignment.value, null));
		}
	}
}
