package fedata.gba.fe7;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fedata.gba.GBAFECharacterData;

public class FE7Character extends GBAFECharacterData {
	public FE7Character(byte[] data, long originalOffset, Boolean isClassRestricted) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
		this.isClassRestricted = isClassRestricted;
	}
	
	public GBAFECharacterData createCopy(boolean useOriginalData) {
		if (useOriginalData) {
			return new FE7Character(Arrays.copyOf(this.originalData, this.originalData.length), this.originalOffset, this.isClassRestricted);
		}
		return new FE7Character(Arrays.copyOf(this.data, this.data.length), this.originalOffset, this.isClassRestricted);
	}
	
	public int getOriginalID() {
		return originalData[4] & 0xFF;
	}
	
	@Override
	public void prepareForClassRandomization() {
		// null out custom battle animations.
		data[37] = 0;
		data[38] = 0;
	}
}
