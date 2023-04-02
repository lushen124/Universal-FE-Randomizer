package fedata.gba.fe8;

import java.util.Arrays;

import fedata.gba.GBAFECharacterData;

public class FE8Character extends GBAFECharacterData {
	public FE8Character(byte[] data, long originalOffset, Boolean isClassRestricted) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
		this.isClassRestricted = isClassRestricted;
	}

	public GBAFECharacterData createCopy(boolean useOriginalData) {
		if (useOriginalData) {
			return new FE8Character(Arrays.copyOf(this.originalData, this.originalData.length), this.originalOffset,
					this.isClassRestricted);
		}

		return new FE8Character(Arrays.copyOf(this.data, this.data.length), this.originalOffset,
				this.isClassRestricted);
	}

	public int getOriginalDescriptionIndex() {
		return (originalData[2] & 0xFF) | ((originalData[3] & 0xFF) << 8);
	}

	public int getOriginalID() {
		return originalData[4] & 0xFF;
	}

	// FE8 uses a separate table away from the character data.
	@Override
	public int getUnpromotedPaletteIndex() {
		return 0;
	}

	@Override
	public void setUnpromotedPaletteIndex(int newIndex) {
	}

	@Override
	public int getPromotedPaletteIndex() {
		return 0;
	}

	@Override
	public void setPromotedPaletteIndex(int newIndex) {
	}

}
