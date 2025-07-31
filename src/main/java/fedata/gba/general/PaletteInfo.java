package fedata.gba.general;

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