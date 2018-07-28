package fedata.fe7;

import java.util.Set;

import fedata.FEChapter;
import fedata.FEChapterItem;
import fedata.FEChapterUnit;

public class FE7Chapter implements FEChapter {
	
	private long originalOffset;
	
	private Boolean hasChanges = false;
	
	private FEChapterUnit[] allUnits;
	
	public FE7Chapter(byte[] data, long originalOffset, int unitCount, Set<Integer> doNotTouchIndices) {
		super();
		this.originalOffset = originalOffset;
		
		allUnits = new FEChapterUnit[unitCount];
		
		long currentOffset = originalOffset;
		int currentBaseIndex = 0;
		
		int bytesPerUnit = FE7Data.BytesPerChapterUnit;
		for (int i = 0; i < unitCount; i++) {
			byte[] unitData = new byte[bytesPerUnit];
			System.arraycopy(data, currentBaseIndex, unitData, 0, bytesPerUnit);
			
			allUnits[i] = new FE7ChapterUnit(unitData, currentOffset, !doNotTouchIndices.contains(i));
			currentOffset += bytesPerUnit;
			currentBaseIndex += bytesPerUnit;
		}
		
		
	}

	@Override
	public FEChapterUnit[] allUnits() {
		return allUnits;
	}

	public void resetData() {
		for (int i = 0; i < allUnits.length; i++) {
			allUnits[i].resetData();
		}
	}
	
	public void commitChanges() {
		if (wasModified()) {
			hasChanges = true;
		}
		
		for (int i = 0; i < allUnits.length; i++) {
			allUnits[i].commitChanges();
		}
	}
	
	public Boolean hasCommittedChanges() {
		return hasChanges;
	}
	
	public byte[] getData() {
		int bytesPerUnit = FE7Data.BytesPerChapterUnit;
		byte[] currentData = new byte[bytesPerUnit * allUnits.length];
		
		int currentBaseIndex = 0;
		
		for (int i = 0; i < allUnits.length; i++) {
			System.arraycopy(allUnits[i].getData(), 0, currentData, currentBaseIndex, bytesPerUnit);
			currentBaseIndex += bytesPerUnit;
		}
		
		return currentData;
	}
	
	public Boolean wasModified() {
		for (int i = 0; i < allUnits.length; i++) {
			if (allUnits[i].wasModified()) {
				return true;
			}
		}
		
		return false;
	}
	
	public long getAddressOffset() {
		return originalOffset;
	}
	
}
