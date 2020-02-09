package fedata.gcnwii.fe9;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.gcn.GCNCMBFileHandler;
import util.ByteArrayBuilder;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9ChapterRewards {
	
	private GCNCMBFileHandler cmbHandler;
	
	private Map<String, String> chestReplacements;
	private Map<String, String> villageReplacements;
	
	public FE9ChapterRewards(GCNCMBFileHandler handler) {
		cmbHandler = handler;
		
		chestReplacements = new HashMap<String, String>();
		villageReplacements = new HashMap<String, String>();
		
		loadChests();
		loadVillages();
	}
	
	public Set<String> getChestContents() {
		Set<String> chests = new HashSet<String>();
		for (String key : chestReplacements.keySet()) {
			if (chestReplacements.get(key) != null) {
				chests.add(chestReplacements.get(key));
			} else {
				chests.add(key);
			}
		}
		
		return chests;
	}
	
	public Set<String> getVillageContents() {
		Set<String> villages = new HashSet<String>();
		for (String key : villageReplacements.keySet()) {
			if (villageReplacements.get(key) != null) {
				villages.add(villageReplacements.get(key));
			} else {
				villages.add(key);
			}
		}
		
		return villages;
	}
	
	public Set<String> getOriginalChestContents() {
		return chestReplacements.keySet();
	}
	
	public Set<String> getOriginalVillageContents() {
		return villageReplacements.keySet();
	}
	
	public void replaceChest(String originalIID, String replacementIID) {
		if (chestReplacements.containsKey(originalIID)) {
			chestReplacements.put(originalIID, replacementIID);
		}
	}
	
	public void replaceVillage(String originalIID, String replacementIID) {
		if (villageReplacements.containsKey(originalIID)) {
			villageReplacements.put(originalIID, replacementIID);
		}
	}
	
	public void commitChanges() {
		commitChests();
		commitVillages();
	}

	private void loadChests() {
		ByteArrayBuilder chestScript = new ByteArrayBuilder();
		byte[] tboxOpen = cmbHandler.bytePrefixForString("TBoxOpen");
		if (tboxOpen == null) { DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "No Chests found in " + cmbHandler.getName()); return; }
		chestScript.appendBytes(tboxOpen);
		chestScript.appendBytes(new byte[] {0x02, 0x20});
		
		List<Long> offsets = cmbHandler.offsetsForBytes(chestScript.toByteArray());
		for (Long offset : offsets) {
			byte[] prefix = cmbHandler.cmb_readBytesAtOffset(offset, chestScript.getBytesWritten() + 1);
			byte[] potentialItemOffset = null;
			// There are two possible bytes following 0x02 and 0x20. 0x1C means the offset fits in 1 byte while 0x1D (this is the more common case) means the offset fits in 2 bytes.
			if (prefix[prefix.length - 1] == 0x1C) {
				// The offset is 1 byte.
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Next chest is limited to 1 byte offset.");
				potentialItemOffset = cmbHandler.cmb_readBytesAtOffset(offset + chestScript.getBytesWritten() + 1, 1);
			} else if (prefix[prefix.length - 1] == 0x1D) {
				// The offset is 2 bytes.
				potentialItemOffset = cmbHandler.cmb_readBytesAtOffset(offset + chestScript.getBytesWritten() + 1, 2);
			}
			
			String iid = cmbHandler.stringForOffset(potentialItemOffset);
			if (iid != null && iid.startsWith("IID_")) {
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Chest found in " + cmbHandler.getName() + " containing " + iid);
				chestReplacements.put(iid, null);
			}
		}
	}
	
	private void loadVillages() {
		ByteArrayBuilder villageScript = new ByteArrayBuilder();
		byte[] visitOut = cmbHandler.bytePrefixForString("VisitOut");
		if (visitOut == null) { DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "No Villages found in " + cmbHandler.getName()); return; }
		villageScript.appendBytes(visitOut);
		villageScript.appendBytes(new byte[] {0x03, 0x20});
		
		List<Long> offsets = cmbHandler.offsetsForBytes(villageScript.toByteArray());
		for (Long offset : offsets) {
			byte[] prefix = cmbHandler.cmb_readBytesAtOffset(offset, villageScript.getBytesWritten() + 1);
			// There are also another two possibilities. Chapter 11 uses 0x1D instead of the usual 0x38.
			// In the case of 0x1D, the item follows immediately afterwards.
			byte[] potentialItemOffset = null;
			if (prefix[prefix.length - 1] == 0x1D) {
				potentialItemOffset = cmbHandler.cmb_readBytesAtOffset(offset + villageScript.getBytesWritten() + 1, 2);
			} else if (prefix[prefix.length - 1] == 0x38) {
				byte[] potentialVillage = cmbHandler.cmb_readBytesAtOffset(offset, villageScript.getBytesWritten() + 25);
				potentialItemOffset = WhyDoesJavaNotHaveThese.subArray(potentialVillage, potentialVillage.length - 2, 2);
			}
			String iid = cmbHandler.stringForOffset(potentialItemOffset);
			if (iid != null && iid.startsWith("IID_")) {
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Village found in " + cmbHandler.getName() + " containing " + iid);
				villageReplacements.put(iid, null);
			}
		}
	}
	
	private void commitChests() {
		ByteArrayBuilder chestScript = new ByteArrayBuilder();
		byte[] tboxOpen = cmbHandler.bytePrefixForString("TBoxOpen");
		if (tboxOpen == null) { DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "No Chests found in " + cmbHandler.getName()); return; }
		chestScript.appendBytes(tboxOpen);
		chestScript.appendBytes(new byte[] {0x02, 0x20});
		
		List<Long> offsets = cmbHandler.offsetsForBytes(chestScript.toByteArray());
		for (Long offset : offsets) {
			byte[] prefix = cmbHandler.cmb_readBytesAtOffset(offset, chestScript.getBytesWritten() + 1);
			byte[] potentialItemOffset = null;
			Long targetOffset = null;
			// There are two possible bytes following 0x02 and 0x20. 0x1C means the offset fits in 1 byte while 0x1D (this is the more common case) means the offset fits in 2 bytes.
			if (prefix[prefix.length - 1] == 0x1C) {
				// We're not going to have enough space to do this one. :(
				potentialItemOffset = cmbHandler.cmb_readBytesAtOffset(offset + chestScript.getBytesWritten() + 1, 1);
			} else if (prefix[prefix.length - 1] == 0x1D) {
				// The offset is 2 bytes.
				potentialItemOffset = cmbHandler.cmb_readBytesAtOffset(offset + chestScript.getBytesWritten() + 1, 2);
				targetOffset = offset + chestScript.getBytesWritten() + 1;
			}
			
			String iid = cmbHandler.stringForOffset(potentialItemOffset);
			if (iid != null && chestReplacements.containsKey(iid)) {
				String replacementIID = chestReplacements.get(iid);
				if (replacementIID != null && targetOffset != null) {
					cmbHandler.addString(replacementIID);
					byte[] newIIDBytes = cmbHandler.bytePrefixForString(replacementIID);
					cmbHandler.cmb_writeBytesToOffset(targetOffset, newIIDBytes);
				}
			}
		}
	}
	
	private void commitVillages() {
		ByteArrayBuilder villageScript = new ByteArrayBuilder();
		byte[] visitOut = cmbHandler.bytePrefixForString("VisitOut");
		if (visitOut == null) { DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "No Villages found in " + cmbHandler.getName()); return; }
		villageScript.appendBytes(visitOut);
		villageScript.appendBytes(new byte[] {0x03, 0x20});
		
		List<Long> offsets = cmbHandler.offsetsForBytes(villageScript.toByteArray());
		for (Long offset : offsets) {
			byte[] prefix = cmbHandler.cmb_readBytesAtOffset(offset, villageScript.getBytesWritten() + 1);
			// There are also another two possibilities. Chapter 11 uses 0x1D instead of the usual 0x38.
			// In the case of 0x1D, the item follows immediately afterwards.
			byte[] potentialItemOffset = null;
			Long targetOffset = null;
			if (prefix[prefix.length - 1] == 0x1D) {
				potentialItemOffset = cmbHandler.cmb_readBytesAtOffset(offset + villageScript.getBytesWritten() + 1, 2);
				targetOffset = offset + villageScript.getBytesWritten() + 1;
			} else if (prefix[prefix.length - 1] == 0x38) {
				byte[] potentialVillage = cmbHandler.cmb_readBytesAtOffset(offset, villageScript.getBytesWritten() + 25);
				potentialItemOffset = WhyDoesJavaNotHaveThese.subArray(potentialVillage, potentialVillage.length - 2, 2);
				targetOffset = offset + villageScript.getBytesWritten() + 23;
			}
			String iid = cmbHandler.stringForOffset(potentialItemOffset);
			if (iid != null && villageReplacements.containsKey(iid)) {
				String replacementIID = villageReplacements.get(iid);
				if (replacementIID != null) {
					cmbHandler.addString(replacementIID);
					byte[] newIIDBytes = cmbHandler.bytePrefixForString(replacementIID);
					cmbHandler.cmb_writeBytesToOffset(targetOffset, newIIDBytes);
				}
			}
		}
	}
}
