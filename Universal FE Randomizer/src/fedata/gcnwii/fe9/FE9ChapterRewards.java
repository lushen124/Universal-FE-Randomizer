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
	
	private Map<String, String> desertReplacements;
	
	public FE9ChapterRewards(GCNCMBFileHandler handler) {
		cmbHandler = handler;
		
		chestReplacements = new HashMap<String, String>();
		villageReplacements = new HashMap<String, String>();
		desertReplacements = new HashMap<String, String>();
		
		loadChests();
		loadVillages();
		loadDesert();
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
	
	public Set<String> getDesertContents() {
		Set<String> desert = new HashSet<String>();
		for (String key : desertReplacements.keySet()) {
			if (desertReplacements.get(key) != null) {
				desert.add(desertReplacements.get(key));
			} else {
				desert.add(key);
			}
		}
		
		return desert;
	}
	
	public Set<String> getOriginalChestContents() {
		return chestReplacements.keySet();
	}
	
	public Set<String> getOriginalVillageContents() {
		return villageReplacements.keySet();
	}
	
	public Set<String> getOriginalDesertContents() {
		return desertReplacements.keySet();
	}
	
	public void replaceChest(String originalIID, String replacementIID) {
		if (replacementIID == null) { return; }
		if (chestReplacements.containsKey(originalIID)) {
			chestReplacements.put(originalIID, replacementIID);
		}
	}
	
	public void replaceVillage(String originalIID, String replacementIID) {
		if (replacementIID == null) { return; }
		if (villageReplacements.containsKey(originalIID)) {
			villageReplacements.put(originalIID, replacementIID);
		}
	}
	
	public void replaceDesert(String originalIID, String replacementIID) {
		if (replacementIID == null) { return; }
		if (desertReplacements.containsKey(originalIID)) {
			desertReplacements.put(originalIID, replacementIID);
		}
	}
	
	public void commitChanges() {
		commitChests();
		commitVillages();
		commitDesert();
	}

	private void loadChests() {
		ByteArrayBuilder chestScript = new ByteArrayBuilder();
		byte[] tboxOpen = cmbHandler.referenceToString("TBoxOpen");
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
		byte[] visitOut = cmbHandler.referenceToString("VisitOut");
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
	
	private void loadDesert() {
		// Note, this prefix only works for Chapter 15 (but that's the only desert so, it's fine for now?)
		byte[] desertItemPrefix = new byte[] {
				0x38, 0x00, (byte)0xDE, 0x00, 0x21, 0x07, 0x01, 0x01, 0x00, 0x19, 0x10, 0x38, 0x00, (byte)0xE8, 0x02, 0x21, 0x07, 0x02, 0x19, 0x65, 0x38, 
				0x00, (byte)0xF0, 0x01, 0x21, 0x01, 0x00, 0x1D, 0x00, (byte)0xF7, 0x38, 0x01, 0x01, 0x02, 0x3C, 0x00, 0x0B, 0x01, 0x00, 0x1D, 0x01, 0x0E, 
				0x38, 0x01, 0x01, 0x02, 0x3D, 0x00, 0x08, 0x07, 0x01, 0x19, 0x64, 0x21, 0x20, 0x1D, 0x01, 0x1B, 0x01, 0x01, 0x01, 0x02, 0x01, 
				0x00, 0x38, 0x01, 0x37, 0x01, 0x01, 0x00, 0x38, 0x01, 0x40, 0x01, 0x41, 0x05, 0x01, 0x01, 0x01, 0x02, 0x34, 0x3D, 0x00, 0x12, 0x1D
		};
		List<Long> offsets = cmbHandler.offsetsForBytes(desertItemPrefix);
		for (Long offset : offsets) {
			byte[] potentialDesertEntry = cmbHandler.cmb_readBytesAtOffset(offset + desertItemPrefix.length, 2);
			String iid = cmbHandler.stringForOffset(potentialDesertEntry);
			if (iid != null && iid.startsWith("IID_")) {
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_LOADER, "Desert Item found in " + cmbHandler.getName() + ": " + iid);
				desertReplacements.put(iid, null);
			}
		}
	}
	
	private void commitChests() {
		if (chestReplacements.isEmpty()) { return; }
		
		ByteArrayBuilder chestScript = new ByteArrayBuilder();
		byte[] tboxOpen = cmbHandler.referenceToString("TBoxOpen");
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
					byte[] newIIDBytes = cmbHandler.referenceToString(replacementIID);
					cmbHandler.cmb_writeBytesToOffset(targetOffset, newIIDBytes);
				}
			}
		}
	}
	
	private void commitVillages() {
		if (villageReplacements.isEmpty()) { return; }
		
		ByteArrayBuilder villageScript = new ByteArrayBuilder();
		byte[] visitOut = cmbHandler.referenceToString("VisitOut");
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
				if (replacementIID != null && targetOffset != null) {
					cmbHandler.addString(replacementIID);
					byte[] newIIDBytes = cmbHandler.referenceToString(replacementIID);
					cmbHandler.cmb_writeBytesToOffset(targetOffset, newIIDBytes);
				}
			}
		}
	}
	
	private void commitDesert() {
		if (desertReplacements.isEmpty()) { return; }
		
		// Note, this prefix only works for Chapter 15 (but that's the only desert so, it's fine for now?)
		byte[] desertItemPrefix = new byte[] {
				0x38, 0x00, (byte)0xDE, 0x00, 0x21, 0x07, 0x01, 0x01, 0x00, 0x19, 0x10, 0x38, 0x00, (byte)0xE8, 0x02, 0x21, 0x07, 0x02, 0x19, 0x65, 0x38, 
				0x00, (byte)0xF0, 0x01, 0x21, 0x01, 0x00, 0x1D, 0x00, (byte)0xF7, 0x38, 0x01, 0x01, 0x02, 0x3C, 0x00, 0x0B, 0x01, 0x00, 0x1D, 0x01, 0x0E, 
				0x38, 0x01, 0x01, 0x02, 0x3D, 0x00, 0x08, 0x07, 0x01, 0x19, 0x64, 0x21, 0x20, 0x1D, 0x01, 0x1B, 0x01, 0x01, 0x01, 0x02, 0x01, 
				0x00, 0x38, 0x01, 0x37, 0x01, 0x01, 0x00, 0x38, 0x01, 0x40, 0x01, 0x41, 0x05, 0x01, 0x01, 0x01, 0x02, 0x34, 0x3D, 0x00, 0x12, 0x1D
		};
		List<Long> offsets = cmbHandler.offsetsForBytes(desertItemPrefix);
		for (Long offset : offsets) {
			Long targetOffset = offset + desertItemPrefix.length;
			byte[] potentialDesertEntry = cmbHandler.cmb_readBytesAtOffset(targetOffset, 2);
			String iid = cmbHandler.stringForOffset(potentialDesertEntry);
			if (iid != null && iid.startsWith("IID_")) {
				String replacementIID = desertReplacements.get(iid);
				if (replacementIID != null && targetOffset != null) {
					cmbHandler.addString(replacementIID);
					byte[] newIIDBytes = cmbHandler.referenceToString(replacementIID);
					cmbHandler.cmb_writeBytesToOffset(targetOffset, newIIDBytes);
				}
			}
		}
	}
}
