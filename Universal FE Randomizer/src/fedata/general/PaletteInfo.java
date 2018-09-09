package fedata.general;

public class PaletteInfo {
	
	int classID;
	int characterID;
	
	long paletteOffset;

	int[] hairColorOffsets; // Lightest to Darkest
	int[] primaryColorOffsets; // Lightest to Darkest
	int[] secondaryColorOffsets; // Lightest to Darkest
	int[] tertiaryColorOffsets; // Lightest to Darkest
	
	int paletteID;
	
	public PaletteInfo(PaletteInfo otherInfo) {
		classID = otherInfo.classID;
		characterID = otherInfo.characterID;
		
		paletteOffset = otherInfo.paletteOffset;
		
		if (otherInfo.hairColorOffsets != null) {
			hairColorOffsets = otherInfo.hairColorOffsets.clone();
		}
		if (otherInfo.primaryColorOffsets != null) {
			primaryColorOffsets = otherInfo.primaryColorOffsets.clone();
		}
		if (otherInfo.secondaryColorOffsets != null) {
			secondaryColorOffsets = otherInfo.secondaryColorOffsets.clone();
		}
		if (otherInfo.tertiaryColorOffsets != null) {
			tertiaryColorOffsets = otherInfo.tertiaryColorOffsets.clone();
		}
	}
	
	public PaletteInfo(int classID, int characterID, long offset, int hairStart, int hairCount, int primaryStart, int primaryCount) {
		this.paletteOffset = offset;
		
		this.classID = classID;
		this.characterID = characterID;
		
		hairColorOffsets = new int[hairCount];
		int hairPointer = hairStart;
		for (int i = 0; i < hairCount; i++) {
			hairColorOffsets[i] = hairPointer;
			hairPointer += 2;
		}
		
		primaryColorOffsets = new int[primaryCount];
		int primaryPointer = primaryStart;
		for (int i = 0; i < primaryCount; i++) {
			primaryColorOffsets[i] = primaryPointer;
			primaryPointer += 2;
		}
		
		secondaryColorOffsets = new int[] {};
		tertiaryColorOffsets = new int[] {};
	}
	
	public PaletteInfo(int classID, int characterID, long offset, int hairStart, int hairCount, int primaryStart, int primaryCount, int secondaryStart, int secondaryCount) {
		this.paletteOffset = offset;
		
		this.classID = classID;
		this.characterID = characterID;
		
		hairColorOffsets = new int[hairCount];
		int hairPointer = hairStart;
		for (int i = 0; i < hairCount; i++) {
			hairColorOffsets[i] = hairPointer;
			hairPointer += 2;
		}
		
		primaryColorOffsets = new int[primaryCount];
		int primaryPointer = primaryStart;
		for (int i = 0; i < primaryCount; i++) {
			primaryColorOffsets[i] = primaryPointer;
			primaryPointer += 2;
		}
		
		secondaryColorOffsets = new int[secondaryCount];
		int secondaryPointer = secondaryStart;
		for (int i = 0; i < secondaryCount; i++) {
			secondaryColorOffsets[i] = secondaryPointer;
			secondaryPointer += 2;
		}
		
		tertiaryColorOffsets = new int[] {};
	}
	
	public PaletteInfo(int classID, int characterID, long offset, int[] hairColors, int[] primaryColors, int[] secondaryColors) {
		this.paletteOffset = offset;
		
		this.classID = classID;
		this.characterID = characterID;
		
		this.hairColorOffsets = hairColors;
		this.primaryColorOffsets = primaryColors;
		this.secondaryColorOffsets = secondaryColors;
		this.tertiaryColorOffsets = new int[] {};
	}
	
	public PaletteInfo(int classID, int characterID, long offset, int[] hairColors, int[] primaryColors, int[] secondaryColors, int[] tertiaryColors) {
		this.paletteOffset = offset;
		
		this.classID = classID;
		this.characterID = characterID;
		
		this.hairColorOffsets = hairColors;
		this.primaryColorOffsets = primaryColors;
		this.secondaryColorOffsets = secondaryColors;
		this.tertiaryColorOffsets = tertiaryColors;
	}
	
	public void setPaletteID(int identifier) {
		paletteID = identifier;
	}
	
	public int getPaletteID() {
		return paletteID;
	}
	
	public int getClassID() {
		return classID;
	}
	
	public int getCharacterID() {
		return characterID;
	}
	
	public long getOffset() {
		return paletteOffset;
	}
}