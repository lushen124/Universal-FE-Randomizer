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
import io.gcn.GCNDataFileHandlerV2;
import io.gcn.GCNDataFileHandlerV2.GCNDataFileDataSection;
import util.DebugPrinter;
import util.Diff;
import util.WhyDoesJavaNotHaveThese;

public class FE9ChapterArmy {
	
	private GCNDataFileHandlerV2 disposHandler;
	public static class FE9ChapterArmySection {
		public final byte[] prefixBytes;
		private final GCNDataFileDataSection dataSection;
		
		private List<FE9ChapterUnit> units;
		
		private FE9ChapterArmySection(byte[] prefixBytes, GCNDataFileDataSection dataSection) {
			this.prefixBytes = prefixBytes;
			this.dataSection = dataSection;
			
			units = new ArrayList<FE9ChapterUnit>();
		}
		
		private void addUnit(FE9ChapterUnit unit) {
			units.add(unit);
		}
		
		public List<FE9ChapterUnit> allUnitsInSection() {
			return WhyDoesJavaNotHaveThese.createMutableCopy(units);
		}
		
		public String getName() {
			return dataSection.identifier;
		}
	}
	
	
	private List<FE9ChapterArmySection> allSections;
	private Map<String, FE9ChapterArmySection> armySectionsByName;
	private List<FE9ChapterUnit> allUnits;
	
	private Map<String, FE9ChapterUnit> unitsByUniqueID;
	private List<String> uniqueIDList;
	
	private Map<FE9ChapterUnit, String> armySectionNameByUnits;
	
	String chapterID;
	
	public FE9ChapterArmy(GCNDataFileHandlerV2 handler, FE9Data.Chapter chapter, String identifier) {
		disposHandler = handler;
		
		chapterID = identifier;
		
		allSections = new ArrayList<FE9ChapterArmySection>();
		allUnits = new ArrayList<FE9ChapterUnit>();
		unitsByUniqueID = new HashMap<String, FE9ChapterUnit>();
		uniqueIDList = new ArrayList<String>();
		
		armySectionsByName = new HashMap<String, FE9ChapterArmySection>();
		armySectionNameByUnits = new HashMap<FE9ChapterUnit, String>();
		
		int counter = 0;
		DebugPrinter.log(DebugPrinter.Key.FE9_ARMY_LOADER, "===Starting Army Data for " + chapterID + "===");
		for (GCNDataFileDataSection section : handler.getSections()) {
			if (section.identifier.contains("_date_")) { continue; }
			byte[] sectionHeader = section.getRawData(0, 4);
			int count = (int)sectionHeader[0];
			FE9ChapterArmySection armySection = new FE9ChapterArmySection(sectionHeader, section);
			allSections.add(armySection);
			armySectionsByName.put(section.identifier, armySection);
			int offset = 4;
			for (int i = 0; i < count; i++) {
				long dataOffset = offset + i * FE9Data.ChapterUnitEntrySize;
				FE9ChapterUnit unit = new FE9ChapterUnit(section.getRawData(dataOffset, FE9Data.ChapterUnitEntrySize), dataOffset);
				allUnits.add(unit);
				armySection.addUnit(unit);
				
				armySectionNameByUnits.put(unit, armySection.getName());
				
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
	
	public List<FE9ChapterArmySection> getArmySections() {
		return allSections;
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
		unit.setCharacterIDPointer(disposHandler.pointerForString(pid));
	}
	
	public String getJIDForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getClassIDPointer());
	}
	
	public void setJIDForUnit(FE9ChapterUnit unit, String jid) {
		if (unit == null || jid == null) { return; }
		disposHandler.addString(jid);
		unit.setClassIDPointer(disposHandler.pointerForString(jid));
	}
	
	public int getStartingLevelForUnit(FE9ChapterUnit unit) {
		return unit.getStartingLevel();
	}
	
	public void setStartingLevelForUnit(FE9ChapterUnit unit, int level) {
		unit.setStartingLevel(level);
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
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Weapon1Offset);
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
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Weapon2Offset);
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
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Weapon3Offset);
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
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Weapon4Offset);
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
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Item1Offset);
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
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Item2Offset);
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
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Item3Offset);
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
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(iid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Item4Offset);
		unit.setItem4Pointer(disposHandler.pointerForString(iid));
	}
	
	public String getSkill1ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getSkill1Pointer());
	}
	
	public void setSkill1ForUnit(FE9ChapterUnit unit, String sid) {
		if (unit == null || sid == null) { return; }
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(sid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Skill1Offset);
		unit.setSkill1Pointer(disposHandler.pointerForString(sid));
	}
	
	public String getSkill2ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getSkill2Pointer());
	}
	
	public void setSkill2ForUnit(FE9ChapterUnit unit, String sid) {
		if (unit == null || sid == null) { return; }
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(sid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Skill2Offset);
		unit.setSkill2Pointer(disposHandler.pointerForString(sid));
	}
	
	public String getSkill3ForUnit(FE9ChapterUnit unit) {
		return disposHandler.stringForPointer(unit.getSkill3Pointer());
	}
	
	public void setSkill3ForUnit(FE9ChapterUnit unit, String sid) {
		if (unit == null || sid == null) { return; }
		
		GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
		disposHandler.addString(sid);
		disposHandler.addPointerOffset(section, unit.getAddressOffset() + FE9ChapterUnit.Skill3Offset);
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
	
	public String getSEQ1ForUnit(FE9ChapterUnit unit) {
		if (unit == null) { return null; }
		return disposHandler.stringForPointer(unit.getSEQ1Pointer());
	}
	
	public String getSEQ2ForUnit(FE9ChapterUnit unit) {
		if (unit == null) { return null; }
		return disposHandler.stringForPointer(unit.getSEQ2Pointer());
	}
	
	public String getSEQ3ForUnit(FE9ChapterUnit unit) {
		if (unit == null) { return null; }
		return disposHandler.stringForPointer(unit.getSEQ3Pointer());
	}
	
	public String getMTYPEForUnit(FE9ChapterUnit unit) {
		if (unit == null) { return null; }
		return disposHandler.stringForPointer(unit.getMTYPEPointer());
	}
	
	public void commitChanges() {
		for (FE9ChapterUnit unit : allUnits) {
			unit.commitChanges();
			
			if (unit.hasCommittedChanges()) {
				GCNDataFileDataSection section = armySectionsByName.get(armySectionNameByUnits.get(unit)).dataSection;
				disposHandler.writeDataToSection(section, unit.getAddressOffset(), unit.getData());
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
	
	public boolean unitHasItem(FE9ChapterUnit unit, String iid) {
		String weapon1IID = getWeapon1ForUnit(unit);
		String weapon2IID = getWeapon2ForUnit(unit);
		String weapon3IID = getWeapon3ForUnit(unit);
		String weapon4IID = getWeapon4ForUnit(unit);
		
		String item1IID = getItem1ForUnit(unit);
		String item2IID = getItem2ForUnit(unit);
		String item3IID = getItem3ForUnit(unit);
		String item4IID = getItem4ForUnit(unit);
		
		return ((weapon1IID != null && weapon1IID.equals(iid)) ||
				(weapon2IID != null && weapon2IID.equals(iid)) ||
				(weapon3IID != null && weapon3IID.equals(iid)) ||
				(weapon4IID != null && weapon4IID.equals(iid)) ||
				(item1IID != null && item1IID.equals(iid)) ||
				(item2IID != null && item2IID.equals(iid)) ||
				(item3IID != null && item3IID.equals(iid)) ||
				(item4IID != null && item4IID.equals(iid)));
	}
}