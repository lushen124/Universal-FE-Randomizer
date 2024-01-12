package fedata.gba.fe7;

import fedata.gba.GBAFEChapterUnitData;

public class FE7ChapterUnit extends GBAFEChapterUnitData {

	public FE7ChapterUnit(byte[] data, long originalOffset) {
		super(data, originalOffset);
	}

	@Override
	public void setAIToHeal(Boolean allowAttack) {
		data[12] = (byte) (allowAttack ? 0x0F : 0x0E);
		wasModified = true;
	}

	@Override
	public void setAIToOnlyAttack(Boolean allowMove) {
		data[12] = (byte) (allowMove ? 0x00 : 0x03);
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
}
