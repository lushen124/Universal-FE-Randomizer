package fedata.gba.fe6;

import fedata.gba.GBAFEChapterUnitData;

public class FE6ChapterUnit extends GBAFEChapterUnitData {
	public FE6ChapterUnit(byte[] data, long originalOffset) {
		super(data, originalOffset);
	}

	@Override
	public void setAIToHeal(Boolean allowAttack) {
		if (allowAttack) { // Based off of late-game sages
			data[12] = 0x0E;
			data[13] = 0x03;
			data[14] = 0x29;
			data[15] = 0x00;
		} else { // Based off of early-game priests
			data[12] = 0x0E;
			data[13] = 0x03;
			data[14] = 0x10;
			data[15] = 0x00;
		}
		wasModified = true;
	}

	@Override
	public void setAIToOnlyAttack(Boolean allowMove) {
		if (allowMove) { // Based off of chapter 1 AI (aggressive minions)
			data[12] = 0x00;
			data[13] = 0x00;
			data[14] = 0x09;
			data[15] = 0x00;
		} else { // Based off of chapter 1 AI (waiting minions)
			data[12] = 0x00;
			data[13] = 0x03;
			data[14] = 0x09; // Some archers use 0x29 here instead. This value changes throughout the
								// chapters, so no idea how it'll perform beyond chapter 1.
			data[15] = 0x00;
		}
		wasModified = true;
	}

	@Override
	public void setUnitToDropLastItem(boolean drop) {
		// We don't support this on FE6 (at least not yet).
	}
}
