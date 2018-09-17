package fedata.fe8;

import java.util.HashMap;
import java.util.Map;

import fedata.FEModifiableObject;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;

public class FE8PromotionManager {
	
	private class PromotionBranch implements FEModifiableObject {
		byte[] originalData;
		byte[] data;
		
		long originalOffset;
		
		Boolean wasModified = false;
		Boolean hasChanges = false;
		
		private PromotionBranch(FileHandler handler, long offset) {
			originalData = handler.readBytesAtOffset(offset, FE8Data.BytesPerPromotionBranchEntry);
			data = originalData.clone();
			
			originalOffset = offset;
		}
		
		private int getFirstPromotion() {
			return data[0] & 0xFF;
		}
		
		private void setFirstPromotion(int classID) {
			data[0] = (byte)(classID & 0xFF);
			wasModified = true;
		}
		
		private int getSecondPromotion() {
			return data[1] & 0xFF;
		}
		
		private void setSecondPromotion(int classID) {
			data[1] = (byte)(classID & 0xFF);
			wasModified = true;
		}
		
		public void resetData() {
			data = originalData.clone();
			wasModified = false;
		}

		public void commitChanges() {
			if (wasModified) {
				originalData = data.clone();
				hasChanges = true;
			}
			
			wasModified = false;
		}

		public byte[] getData() {
			return data;
		}

		public Boolean hasCommittedChanges() {
			return hasChanges;
		}

		public Boolean wasModified() {
			return wasModified;
		}
		
		public long getAddressOffset() {
			return originalOffset;
		}
	}
	
	private Map<FE8Data.CharacterClass, PromotionBranch> promotionBranches;

	public FE8PromotionManager(FileHandler handler) {
		promotionBranches = new HashMap<FE8Data.CharacterClass, PromotionBranch>();
		long address = FileReadHelper.readAddress(handler, FE8Data.PromotionBranchTablePointer);
		for (FE8Data.CharacterClass currentClass : FE8Data.CharacterClass.values()) { // These are conveniently labeled in order of class ID.
			int index = currentClass.ID;
			promotionBranches.put(currentClass, new PromotionBranch(handler, address + (index * FE8Data.BytesPerPromotionBranchEntry)));
		}
		
		// Override Soldiers to promote.
		setFirstPromotionOptionForClass(FE8Data.CharacterClass.SOLDIER.ID, FE8Data.CharacterClass.GENERAL.ID);
		setSecondPromotionOptionForClass(FE8Data.CharacterClass.SOLDIER.ID, FE8Data.CharacterClass.PALADIN.ID);
	}
	
	public Boolean hasPromotions(int baseClassID) {
		return getFirstPromotionOptionClassID(baseClassID) != 0 || getSecondPromotionOptionClassID(baseClassID) != 0;
	}
	
	public int getFirstPromotionOptionClassID(int baseClassID) {
		FE8Data.CharacterClass baseClass = FE8Data.CharacterClass.valueOf(baseClassID);
		if (baseClass == null) { return 0; }
		PromotionBranch branch = promotionBranches.get(baseClass);
		if (branch == null) { return 0; }
		return branch.getFirstPromotion();
	}
	
	public int getSecondPromotionOptionClassID(int baseClassID) {
		FE8Data.CharacterClass baseClass = FE8Data.CharacterClass.valueOf(baseClassID);
		if (baseClass == null) { return 0; }
		PromotionBranch branch = promotionBranches.get(baseClass);
		if (branch == null) { return 0; }
		return branch.getSecondPromotion();
	}
	
	public void setFirstPromotionOptionForClass(int baseClassID, int firstPromotionClassID) {
		FE8Data.CharacterClass baseClass = FE8Data.CharacterClass.valueOf(baseClassID);
		if (baseClass == null) { return; }
		FE8Data.CharacterClass promotedClass = FE8Data.CharacterClass.valueOf(firstPromotionClassID);
		if (promotedClass == null) { return; }
		
		PromotionBranch branch = promotionBranches.get(baseClass);
		if (branch == null) { return; }
		branch.setFirstPromotion(promotedClass.ID);
	}
	
	public void setSecondPromotionOptionForClass(int baseClassID, int secondPromotionClassID) {
		FE8Data.CharacterClass baseClass = FE8Data.CharacterClass.valueOf(baseClassID);
		if (baseClass == null) { return; }
		FE8Data.CharacterClass promotedClass = FE8Data.CharacterClass.valueOf(secondPromotionClassID);
		if (promotedClass == null) { return; }
		
		PromotionBranch branch = promotionBranches.get(baseClass);
		if (branch == null) { return; }
		branch.setSecondPromotion(promotedClass.ID);
	}
	
	public void commit() {
		for (PromotionBranch branch : promotionBranches.values()) {
			branch.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (PromotionBranch branch : promotionBranches.values()) {
			branch.commitChanges();
			if (branch.hasCommittedChanges()) {
				Diff diff = new Diff(branch.getAddressOffset(), branch.getData().length, branch.getData(), null);
				compiler.addDiff(diff);
			}
		}
	}
}
