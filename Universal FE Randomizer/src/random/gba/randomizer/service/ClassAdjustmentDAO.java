package random.gba.randomizer.service;

import java.util.ArrayList;
import java.util.List;

import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEStatDAO;

public class ClassAdjustmentDAO {
	public int levelAdjustment;
	public GBAFEClassData targetClass;
	public List<GBAFEStatDAO> promoBonuses = new ArrayList<>();
}
