package random.gba.randomizer.service;

import java.util.ArrayList;
import java.util.List;

import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEStatDto;

/**
 * Dto containing info for auto leveling
 */
public class ClassAdjustmentDto {
	public int levelAdjustment;
	public GBAFEClassData targetClass;
	public List<GBAFEStatDto> promoBonuses = new ArrayList<>();
}
