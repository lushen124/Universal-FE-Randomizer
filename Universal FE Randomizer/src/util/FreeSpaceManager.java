package util;

import java.util.HashMap;
import java.util.Map;

import fedata.FEBase;

public class FreeSpaceManager {
	
	private class AssignedSpace {
		long offset;
		byte[] value;
	}
	
	Map<String, AssignedSpace> changes;
	long freeAddress;
	
	public FreeSpaceManager(FEBase.GameType gameType) {
		switch (gameType) {
		case FE7:
			freeAddress = 0x1000000;
			break;
		default:
			freeAddress = -1;
			break;
		}
		changes = new HashMap<String, AssignedSpace>();
		
	}
	
	public long setValue(byte[] value, String key) {
		AssignedSpace assignment = changes.get(key);
		if (assignment != null) {
			changes.remove(key);
		}
		
		assignment = new AssignedSpace();
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
