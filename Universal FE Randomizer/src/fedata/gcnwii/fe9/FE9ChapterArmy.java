package fedata.gcnwii.fe9;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.FileWriter;
import io.gcn.GCNDataFileHandler;
import util.DebugPrinter;
import util.Diff;
import util.WhyDoesJavaNotHaveThese;

public class FE9ChapterArmy {
	
	private GCNDataFileHandler disposHandler;
	
	private List<FE9ChapterUnit> allUnits;
	
	private Map<String, FE9ChapterUnit> unitsByUniqueID;
	private List<String> uniqueIDList;
	
	String chapterID;
	
	public FE9ChapterArmy(GCNDataFileHandler handler, FE9Data.Chapter chapter, String identifier) {
		disposHandler = handler;
		
		chapterID = identifier;
		
		allUnits = new ArrayList<FE9ChapterUnit>();
		unitsByUniqueID = new HashMap<String, FE9ChapterUnit>();
		uniqueIDList = new ArrayList<String>();
		
		long offset = chapter.getStartingOffset();
		handler.setNextReadOffset(offset);
		long endingOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(handler.continueReadingBytes(4), false);
		DebugPrinter.log(DebugPrinter.Key.FE9_ARMY_LOADER, "===Starting Army Data for " + chapterID + "===");
		int counter = 0;
		while (handler.getNextReadOffset() < endingOffset) {
			byte[] sectionHeader = handler.continueReadingBytes(4);
			DebugPrinter.log(DebugPrinter.Key.FE9_ARMY_LOADER, "New Section: " + WhyDoesJavaNotHaveThese.displayStringForBytes(sectionHeader));
			int unitCount = sectionHeader[0]; // The first byte of this header determines the count. The other 3 somehow determine the type?
			for (int i = 0; i < unitCount; i++) {
				long originalOffset = handler.getNextReadOffset();
				FE9ChapterUnit unit = new FE9ChapterUnit(handler.continueReadingBytes(FE9Data.ChapterUnitEntrySize), originalOffset);
				allUnits.add(unit);
				
				// PID is not unique here. Many minions can share the same PID.
				// We need to generate a unique string to append to PIDs when storing them.
				String pid = disposHandler.stringForPointer(unit.getCharacterIDPointer());
				String uniqueID = " (" + Integer.toString((counter++)) + ")";
				unitsByUniqueID.put(pid + uniqueID, unit);
				uniqueIDList.add(pid + uniqueID);
				
				String jid = disposHandler.stringForPointer(unit.getClassIDPointer());
				
				DebugPrinter.log(DebugPrinter.Key.FE9_ARMY_LOADER, "Loaded " + pid + " (" + jid + ") in chapter " + chapterID);
			}
		}
		DebugPrinter.log(DebugPrinter.Key.FE9_ARMY_LOADER, "===End Army Data===");
	}
	
	public String getID() {
		return chapterID;
	}
	
	public List<String> getAllUnitIDs() {
		return uniqueIDList;
	}
	
	public FE9ChapterUnit getUnitForUnitID(String unitID) {
		return unitsByUniqueID.get(unitID);
	}
	
	public FE9ChapterUnit getUnitForPID(String pid) {
		Set<String> uniqueIDs = unitsByUniqueID.keySet();
		String matchingID = uniqueIDs.stream().filter(uniqueID -> {
			return uniqueID.startsWith(pid);
		}).findFirst().orElse(null);
		
		return unitsByUniqueID.get(matchingID);
	}
	
	public String getPIDForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getCharacterIDPointer());
	}
	
	public void setPIDForUnit(FE9ChapterUnit unit, String pid) {
		if (unit == null || pid == null) { return; }
		disposHandler.addString(pid);
		disposHandler.commitAdditions();
		unit.setCharacterIDPointer(disposHandler.pointerForString(pid));
	}
	
	public String getJIDForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getClassIDPointer());
	}
	
	public void setJIDForUnit(FE9ChapterUnit unit, String jid) {
		if (unit == null || jid == null) { return; }
		disposHandler.addString(jid);
		disposHandler.commitAdditions();
		unit.setClassIDPointer(disposHandler.pointerForString(jid));
	}
	
	public String getWeapon1ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getWeapon1Pointer());
	}
	
	public void setWeapon1ForUnit(FE9ChapterUnit unit, String iid) {
		if (unit == null) { return; }
		if (iid == null) {
			unit.setWeapon1Pointer(0);
			return;
		}
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Weapon1Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setWeapon1Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getWeapon2ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getWeapon2Pointer());
	}
	
	public void setWeapon2ForUnit(FE9ChapterUnit unit, String iid) {
		if (unit == null) { return; }
		if (iid == null) {
			unit.setWeapon2Pointer(0);
			return;
		}
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Weapon2Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setWeapon2Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getWeapon3ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getWeapon3Pointer());
	}
	
	public void setWeapon3ForUnit(FE9ChapterUnit unit, String iid) {
		if (unit == null) { return; }
		if (iid == null) {
			unit.setWeapon3Pointer(0);
			return;
		}
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Weapon3Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setWeapon3Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getWeapon4ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getWeapon4Pointer());
	}
	
	public void setWeapon4ForUnit(FE9ChapterUnit unit, String iid) {
		if (unit == null) { return; }
		if (iid == null) {
			unit.setWeapon4Pointer(0);
			return;
		}
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Weapon4Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setWeapon4Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getItem1ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getItem1Pointer());
	}
	
	public void setItem1ForUnit(FE9ChapterUnit unit, String iid) {
		if (unit == null) { return; }
		if (iid == null) {
			unit.setItem1Pointer(0);
			return;
		}
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Item1Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setItem1Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getItem2ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getItem2Pointer());
	}
	
	public void setItem2ForUnit(FE9ChapterUnit unit, String iid) {
		if (unit == null) { return; }
		if (iid == null) {
			unit.setItem2Pointer(0);
			return;
		}
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Item2Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setItem2Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getItem3ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getItem3Pointer());
	}
	
	public void setItem3ForUnit(FE9ChapterUnit unit, String iid) {
		if (unit == null) { return; }
		if (iid == null) {
			unit.setItem3Pointer(0);
			return;
		}
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Item3Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setItem3Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getItem4ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getItem4Pointer());
	}
	
	public void setItem4ForUnit(FE9ChapterUnit unit, String iid) {
		if (unit == null) { return; }
		if (iid == null) {
			unit.setItem4Pointer(0);
			return;
		}
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Item4Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setItem4Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getSkill1ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getSkill1Pointer());
	}
	
	public void setSkill1ForUnit(FE9ChapterUnit unit, String sid) {
		if (unit == null || sid == null) { return; }
		disposHandler.addString(sid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Skill1Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setSkill1Pointer(disposHandler.pointerForString(sid));
	}
	
	public String getSkill2ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getSkill2Pointer());
	}
	
	public void setSkill2ForUnit(FE9ChapterUnit unit, String sid) {
		if (unit == null || sid == null) { return; }
		disposHandler.addString(sid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Skill2Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setSkill2Pointer(disposHandler.pointerForString(sid));
	}
	
	public String getSkill3ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getSkill3Pointer());
	}
	
	public void setSkill3ForUnit(FE9ChapterUnit unit, String sid) {
		if (unit == null || sid == null) { return; }
		disposHandler.addString(sid);
		disposHandler.addPointerOffset(unit.getAddressOffset() + FE9ChapterUnit.Skill3Offset - 0x20);
		disposHandler.commitAdditions();
		unit.setSkill3Pointer(disposHandler.pointerForString(sid));
	}
	
	public int getStartingXForUnit(FE9ChapterUnit unit) {
		return unit.getStartingX();
	}
	
	public int getStartingYForUnit(FE9ChapterUnit unit) {
		return unit.getStartingY();
	}
	
	public int getEndingXForUnit(FE9ChapterUnit unit) {
		return unit.getEndingX();
	}
	
	public int getEndingYForUnit(FE9ChapterUnit unit) {
		return unit.getEndingY();
	}
	
	public void commitChanges() {
		for (FE9ChapterUnit unit : allUnits) {
			unit.commitChanges();
			
			if (unit.hasCommittedChanges()) {
				byte[] unitData = unit.getData();
				disposHandler.addChange(new Diff(unit.getAddressOffset(), unitData.length, unitData, null));
			}
		}
	}
	
	public void debugWriteDisposHandler(String path) {
		try {
			FileWriter.writeBinaryDataToFile(disposHandler.getRawData(), path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}