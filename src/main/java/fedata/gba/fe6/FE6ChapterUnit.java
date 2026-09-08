package fedata.gba.fe6;

import fedata.gba.GBAFEChapterUnitData;

public class FE6ChapterUnit extends GBAFEChapterUnitData {
	public FE6ChapterUnit(byte[] data, long originalOffset) {
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
		// We don't support this on FE6 (at least not yet).
	}
	
	public boolean isAITargetingVillages() {
		return data[13] == 0x04 || data[13] == 0x05;
	}
}
