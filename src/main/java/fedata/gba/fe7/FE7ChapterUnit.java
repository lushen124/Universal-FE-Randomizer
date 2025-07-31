package fedata.gba.fe7;

import fedata.gba.GBAFEChapterUnitData;

public class FE7ChapterUnit extends GBAFEChapterUnitData {

	public FE7ChapterUnit(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}

	@Override
	public void setAIToHeal() {
		data[12] = 0x0E;
		wasModified = true;
	}

	public void removeHealingAI() {
		data[12] = 0x00;
		wasModified = true;
	}

	@Override
	public void setUnitToDropLastItem(boolean drop) {
		if (drop) {
			data[15] |= 0x40;
		} else {
			data[15] &= ~0x40;
		}

		wasModified = true;
	}
	
	public boolean isAITargetingVillages() {
		return data[13] == 0x04 || data[13] == 0x05;
	}
}
